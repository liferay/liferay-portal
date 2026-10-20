/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import fetch from 'jest-fetch-mock';
import React from 'react';

import '@testing-library/jest-dom';

import {NewStaticSiteExport} from '../../../../../src/main/resources/META-INF/resources/revamp/js/pages/export/NewStaticSiteExport';
import {mockPageTreeItems} from '../../mocks/mockPageTreeItems';

jest.mock('staging-taglib', () => ({
	PagesTree: require('../../mocks/MockPagesTree').MockPagesTree,
}));

const DEFAULT_PROPS = {
	availableLanguages: [
		{label: 'English (United States)', value: 'en_US'},
		{label: 'Spanish (Spain)', value: 'es_ES'},
	],
	backURL: '/some/back/url',
	defaultLanguageId: 'en_US',
	exportProcessAPIURL: '/o/export-static-site',
	pageTreeModalConfiguration: {
		groupId: 20121,
		pageSize: 20,
		privateLayoutsAvailable: false,
		siteExternalReferenceCode: 'site-erc',
	},
	portletNamespace: '_portletNamespace_',
};

const getExportFormData = () =>
	fetch.mock.calls.find(
		([url, init]) =>
			String(url).includes('export-static-site') &&
			init?.method === 'POST'
	)?.[1]?.body as FormData;

const renderComponent = () =>
	render(<NewStaticSiteExport {...DEFAULT_PROPS} />);

const submitExport = async () => {
	const exportButton = screen.getByRole('button', {name: /^export$/i});

	await waitFor(() => expect(exportButton).toBeEnabled());

	await userEvent.click(exportButton);
};

const typeFileName = () =>
	userEvent.type(screen.getByRole('textbox', {name: /^name/i}), 'test-file');

describe('NewStaticSiteExport', () => {
	const {ResizeObserver: ResizeObserverOriginal} = window;

	beforeAll(() => {
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));
	});

	afterAll(() => {
		window.ResizeObserver = ResizeObserverOriginal;
	});

	beforeEach(() => {
		jest.clearAllMocks();

		fetch.resetMocks();
		fetch.mockResponse(async (request) => {
			if (request.url.includes('get_layouts_tree')) {
				return JSON.stringify({
					hasMoreElements: false,
					items: mockPageTreeItems,
				});
			}

			if (request.url.includes('session_tree_js_click')) {
				return (await request.text()).includes('cmd=layoutCheck')
					? JSON.stringify([1, 2])
					: '[]';
			}

			return JSON.stringify({id: 1});
		});
	});

	it('selects every page when export all pages is unchecked', async () => {
		renderComponent();

		await userEvent.click(
			screen.getByRole('checkbox', {name: 'export-all-pages'})
		);

		expect(screen.getByText('all-pages')).toBeInTheDocument();

		await userEvent.click(
			screen.getByRole('button', {name: 'select-layouts'})
		);

		expect(await screen.findByLabelText('page-1')).toBeChecked();
		expect(screen.getByLabelText('page-2')).toBeChecked();
	});

	it('exports the languages selected', async () => {
		renderComponent();

		await typeFileName();

		await userEvent.type(
			screen.getByRole('combobox', {name: 'languages'}),
			'Spanish'
		);

		await userEvent.click(
			await screen.findByRole('option', {name: 'Spanish (Spain)'})
		);

		await submitExport();

		await waitFor(() =>
			expect(
				getExportFormData().getAll('_portletNamespace_languageIds')
			).toEqual(['en_US', 'es_ES'])
		);
	});

	it('exports the pages selected through the modal', async () => {
		renderComponent();

		await typeFileName();

		await userEvent.click(
			screen.getByRole('checkbox', {name: 'export-all-pages'})
		);
		await userEvent.click(
			screen.getByRole('button', {name: 'select-layouts'})
		);

		await userEvent.click(await screen.findByLabelText('page-2'));
		await userEvent.click(screen.getByRole('button', {name: 'select'}));

		await submitExport();

		await waitFor(() =>
			expect(
				getExportFormData().getAll('_portletNamespace_layoutIds')
			).toEqual(['1'])
		);

		expect(
			getExportFormData().getAll('_portletNamespace_languageIds')
		).toEqual(['en_US']);
		expect(getExportFormData().get('_portletNamespace_name')).toBe(
			'test-file'
		);
	});
});
