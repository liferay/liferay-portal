/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import VisualizationModes from '../../../src/main/resources/META-INF/resources/js/data_set/visualization_modes/VisualizationModes';
import {OBJECT_RELATIONSHIP} from '../../../src/main/resources/META-INF/resources/js/utils/constants';
import {IDataSet} from '../../../src/main/resources/META-INF/resources/js/utils/types';

const DATA_SET = {
	externalReferenceCode: 'test-data-set-erc',
	id: '38718',
	label: 'Test Data Set',
	restApplication: '/data-set-admin/data-sets',
	restEndpoint: '/',
	restSchema: 'DataSet',
} as IDataSet;

const DEFAULT_PROPS = {
	backURL: '/back',
	cellClientExtensionRenderers: [],
	dataSet: DATA_SET,
	fieldTreeItems: [],
	filterClientExtensionRenderers: [],
	filterableFieldTreeItems: [],
	manageUserViewsURL: '/manage-user-views',
	namespace: 'testNamespace_',
	onActiveSectionChange: jest.fn(),
	onDataSetUpdate: jest.fn(),
	resolvedRESTSchemas: [],
	restApplications: [DATA_SET.restApplication],
	saveDataSetSortURL: '/save-data-set-sort',
	saveDataSetTableSectionsURL: '/save-data-set-table-sections',
	spritemap: '/spritemap.svg',
	visualizationModeClientExtensions: [
		{
			externalReferenceCode: 'LXC:timeline',
			icon: 'date',
			name: 'Timeline',
		},
	],
};

// Each row's drag handle is named after its visualization mode

const getRowNames = () =>
	screen
		.getAllByRole('button', {name: /^drag-/})
		.map((button) => button.getAttribute('aria-label')!.slice(5));

const getRow = (name: string) =>
	screen.getAllByRole('row').find((row) => row.textContent?.includes(name))!;

const renderVisualizationModes = (
	props: Partial<typeof DEFAULT_PROPS> & {dataSet?: IDataSet} = {}
) => render(<VisualizationModes {...DEFAULT_PROPS} {...props} />);

describe('VisualizationModes', () => {
	let patchRequestBodies: Array<{visualizationModes: string}>;

	beforeEach(() => {
		patchRequestBodies = [];

		fetch.mockResponse(async (request) => {
			if (request.method === 'PATCH') {
				const body = await request.json();

				patchRequestBodies.push(body);

				return JSON.stringify({...DATA_SET, ...body});
			}

			if (request.url.includes(OBJECT_RELATIONSHIP.DATA_SET_SORTS)) {
				return JSON.stringify({items: []});
			}

			return JSON.stringify({
				...DATA_SET,
				[OBJECT_RELATIONSHIP.DATA_SET_TABLE_SECTIONS]: [
					{fieldName: 'label'},
				],
			});
		});
	});

	it('lists the visualization modes in the order the data set persists, followed by the missing ones', async () => {
		renderVisualizationModes({
			dataSet: {
				...DATA_SET,
				visualizationModes: JSON.stringify([
					{enabled: true, name: 'table'},
					{enabled: true, name: 'LXC:timeline'},
					{enabled: false, name: 'cards'},
				]),
			},
		});

		expect(getRowNames()).toEqual([
			'table',
			'Timeline',
			'cards',
			'list[noun]',
		]);

		expect(within(getRow('cards')).getByRole('switch')).not.toBeChecked();
		expect(within(getRow('list[noun]')).getByRole('switch')).toBeChecked();
	});

	it('persists the order after moving a visualization mode', async () => {
		renderVisualizationModes();

		screen.getByRole('button', {name: 'drag-cards'}).focus();

		await userEvent.keyboard('{Enter}{ArrowDown}{Enter}');

		expect(getRowNames()).toEqual([
			'list[noun]',
			'cards',
			'table',
			'Timeline',
		]);

		expect(
			JSON.parse(patchRequestBodies[0].visualizationModes).map(
				({name}: {name: string}) => name
			)
		).toEqual(['list', 'cards', 'table', 'LXC:timeline']);
	});

	it('disables client extensions until they are enabled', async () => {
		renderVisualizationModes();

		const toggle = within(getRow('Timeline')).getByRole('switch');

		expect(toggle).not.toBeChecked();

		await userEvent.click(toggle);

		expect(toggle).toBeChecked();

		expect(
			JSON.parse(patchRequestBodies[0].visualizationModes)
		).toContainEqual({enabled: true, name: 'LXC:timeline'});
	});

	it('keeps the configuration and the visualization modes of client extensions that are not deployed', async () => {
		const configuration = {columns: 3};

		renderVisualizationModes({
			dataSet: {
				...DATA_SET,
				visualizationModes: JSON.stringify([
					{configuration, enabled: true, name: 'LXC:timeline'},
					{configuration, enabled: true, name: 'LXC:undeployed'},
				]),
			},
		});

		await userEvent.click(within(getRow('cards')).getByRole('switch'));

		const visualizationModes = JSON.parse(
			patchRequestBodies[0].visualizationModes
		);

		expect(visualizationModes).toContainEqual({
			configuration,
			enabled: true,
			name: 'LXC:timeline',
		});
		expect(visualizationModes).toContainEqual({
			configuration,
			enabled: true,
			name: 'LXC:undeployed',
		});
	});

	it('keeps at least one visualization mode enabled', async () => {
		renderVisualizationModes({
			dataSet: {
				...DATA_SET,
				visualizationModes: JSON.stringify([
					{enabled: true, name: 'table'},
					{enabled: false, name: 'cards'},
					{enabled: false, name: 'list'},
				]),
			},
		});

		expect(within(getRow('table')).getByRole('switch')).toBeDisabled();
		expect(within(getRow('cards')).getByRole('switch')).toBeEnabled();
	});

	it('warns about enabled visualization modes without fields', async () => {
		renderVisualizationModes();

		expect(
			await within(getRow('cards')).findByText('no-fields-assigned')
		).toBeVisible();

		expect(
			within(getRow('table')).queryByText('no-fields-assigned')
		).not.toBeInTheDocument();
		expect(
			within(getRow('Timeline')).queryByText('no-fields-assigned')
		).not.toBeInTheDocument();
	});

	it('opens a visualization mode from its edit button and goes back from the breadcrumb', async () => {
		renderVisualizationModes();

		await userEvent.click(
			within(getRow('cards')).getByRole('button', {name: /edit/})
		);

		expect(
			screen.getByRole('heading', {name: 'cards'})
		).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'visualization-modes'})
		);

		expect(
			screen.getByRole('heading', {name: 'visualization-modes'})
		).toBeInTheDocument();
	});

	it('opens a visualization mode when clicking its row', async () => {
		renderVisualizationModes();

		await userEvent.click(screen.getByText('Timeline'));

		expect(
			screen.getByRole('heading', {name: 'Timeline'})
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'this-visualization-mode-is-provided-by-a-client-extension-and-receives-all-the-fields-of-each-item'
			)
		).toBeVisible();
	});
});
