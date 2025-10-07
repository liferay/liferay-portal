/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {render, screen, waitFor} from '@testing-library/react';

// @ts-ignore

import fetchMock from 'fetch-mock';
import React from 'react';

import {RightSidebarObjectDefinitionDetails} from '../../../components/ModelBuilder/RightSidebar/RightSidebarObjectDefinitionDetails';
import {objectDefinition} from './__mock__/objectDefinition';

const OBJECT_DEFINITION_URL_REGEX =
	/\/o\/object-admin\/v1\.0\/object-definitions\/by-external-reference-code\/.+/;

const renderComponent = async (customProps = {}) => {
	await fetchMock.get(OBJECT_DEFINITION_URL_REGEX, {
		body: {...objectDefinition, ...customProps},
	});

	render(<RightSidebarObjectDefinitionDetails companies={[]} sites={[]} />);
};

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	sub: jest.fn((langKey, arg) => langKey.replace('x', arg)),
}));

jest.mock(
	'../../../components/ModelBuilder/ModelBuilderContext/objectFolderContext',
	() => ({
		useObjectFolderContext: () => {
			return [
				{
					selectedObjectDefinitionNode: {
						data: {externalReferenceCode: '1'},
					},
				},
				jest.fn(),
			];
		},
	})
);

jest.mock('react-flow-renderer', () => {
	return {
		useStore() {
			return {
				edges: [],
				nodes: [],
			};
		},
	};
});

afterAll(() => {
	jest.restoreAllMocks();
});

afterEach(() => {
	fetchMock.restore();
});

describe('inheritance alert', () => {
	it('is hidden when the object definition is the root', async () => {
		const label = Math.random();

		await renderComponent({
			externalReferenceCode: '1',
			label: {en_US: label},
			rootObjectDefinitionExternalReferenceCode: '1',
		});

		await waitFor(async () => {
			return expect(
				await screen.findByText(`${label}-details`)
			).toBeVisible();
		});

		const inheritanceObjectDefinitionAlert = screen.queryByText(
			/inheritance-is-enabled-for-at-least-one-relationship/i
		);

		expect(inheritanceObjectDefinitionAlert).toBeNull();
	});

	it('is hidden when the object definition does not have a root object definition', async () => {
		const label = Math.random();

		await renderComponent({
			externalReferenceCode: '1',
			label: {en_US: label},
			rootObjectDefinitionExternalReferenceCode: '',
		});

		await waitFor(async () => {
			return expect(
				await screen.findByText(`${label}-details`)
			).toBeVisible();
		});

		const inheritanceObjectDefinitionAlert = screen.queryByText(
			/inheritance-is-enabled-for-at-least-one-relationship/i
		);

		expect(inheritanceObjectDefinitionAlert).toBeNull();
	});

	it('is visible when the object definition is a descendant of another object definition', async () => {
		await renderComponent({
			externalReferenceCode: '1',
			rootObjectDefinitionExternalReferenceCode: '2',
		});

		const inheritanceObjectDefinitionAlert = await screen.findByText(
			/inheritance-is-enabled-for-at-least-one-relationship/i
		);

		expect(inheritanceObjectDefinitionAlert).toBeVisible();
	});
});

describe('object definition configuration', () => {
	it('enable account restriction is enabled by default', async () => {
		await renderComponent({storageType: 'salesforce'});

		const accountRestrictionToggle = await screen.findByRole('switch', {
			name: 'enable-account-restriction',
		});

		expect(accountRestrictionToggle).toBeEnabled();
	});

	it('panel link is enabled by default', async () => {
		await renderComponent();

		const panelLink = await screen.findByRole('combobox', {
			name: 'panel-link',
		});

		expect(panelLink).toBeEnabled();
	});

	it('scope is enabled by default', async () => {
		await renderComponent();

		const scope = await screen.findByRole('combobox', {
			name: 'scope',
		});

		expect(scope).toBeEnabled();
	});

	it('show widget in page builder is enabled by default', async () => {
		await renderComponent();

		const showWidgetToggle = await screen.findByRole('switch', {
			name: 'show-widget-in-page-builder',
		});

		expect(showWidgetToggle).toBeEnabled();
	});
});

describe('when the object definition is approved', () => {
	it('enable account restriction is disabled', async () => {
		await renderComponent({
			accountEntryRestricted: true,
			status: {label: 'approved'},
		});

		const accountRestrictionToggle = await screen.findByRole('switch', {
			name: 'enable-account-restriction',
		});

		expect(accountRestrictionToggle).toBeDisabled();
	});

	it('panel link is enabled', async () => {
		await renderComponent({status: {label: 'approved'}});

		const panelLink = await screen.findByRole('combobox', {
			name: 'panel-link',
		});

		expect(panelLink).toBeEnabled();
	});

	it('scope is disabled', async () => {
		await renderComponent({status: {label: 'approved'}});

		const scope = await screen.findByRole('combobox', {
			name: 'scope',
		});

		expect(scope).toBeDisabled();
	});

	it('show widget in page builder is enabled', async () => {
		await renderComponent({status: {label: 'approved'}});

		const showWidgetToggle = await screen.findByRole('switch', {
			name: 'show-widget-in-page-builder',
		});

		expect(showWidgetToggle).toBeEnabled();
	});
});

describe('when the object definition is a system object', () => {
	it('enable account restriction is enabled', async () => {
		await renderComponent({system: true});

		const accountRestrictionToggle = await screen.findByRole('switch', {
			name: 'enable-account-restriction',
		});

		expect(accountRestrictionToggle).toBeDisabled();
	});

	it('panel link is disabled', async () => {
		await renderComponent({modifiable: false, system: true});

		const panelLink = await screen.findByRole('combobox', {
			name: 'panel-link',
		});

		expect(panelLink).toBeDisabled();
	});

	it('scope is enabled', async () => {
		await renderComponent({system: true});

		const scope = await screen.findByRole('combobox', {
			name: 'scope',
		});

		expect(scope).toBeEnabled();
	});

	it('show widget in page builder is disabled', async () => {
		await renderComponent({modifiable: false, system: true});

		const showWidgetToggle = await screen.findByRole('switch', {
			name: 'show-widget-in-page-builder',
		});

		expect(showWidgetToggle).toBeDisabled();
	});
});

describe('when the object definition has a root object definition', () => {
	it('configurations are enabled', async () => {
		await renderComponent({
			objectDefinitionSettings: [
				{
					name: 'rootObjectDefinitionExternalReferenceCodes',
					value: '1',
				},
			],
			storageType: 'sugarcrm',
		});

		const accountRestriction = await screen.findByRole('switch', {
			name: 'enable-account-restriction',
		});

		const panelLink = await screen.findByRole('combobox', {
			name: 'panel-link',
		});

		const scope = await screen.findByRole('combobox', {name: 'scope'});

		const showWidget = await screen.findByRole('switch', {
			name: 'show-widget-in-page-builder',
		});

		expect(accountRestriction).toBeEnabled();
		expect(panelLink).toBeEnabled();
		expect(scope).toBeEnabled();
		expect(showWidget).toBeEnabled();
	});
});

describe('when the object definition has storageType salesforce', () => {
	it('enable account restriction is enabled', async () => {
		await renderComponent({storageType: 'salesforce'});

		const accountRestrictionToggle = await screen.findByRole('switch', {
			name: 'enable-account-restriction',
		});

		expect(accountRestrictionToggle).toBeEnabled();
	});

	it('panel link is enabled', async () => {
		await renderComponent({storageType: 'salesforce'});

		const panelLink = await screen.findByRole('combobox', {
			name: 'panel-link',
		});

		expect(panelLink).toBeEnabled();
	});

	it('scope is disabled', async () => {
		await renderComponent({storageType: 'salesforce'});

		const scope = await screen.findByRole('combobox', {
			name: 'scope',
		});

		expect(scope).toBeDisabled();
	});

	it('show widget in page builder is enabled', async () => {
		await renderComponent({storageType: 'salesforce'});

		const showWidgetToggle = await screen.findByRole('switch', {
			name: 'show-widget-in-page-builder',
		});

		expect(showWidgetToggle).toBeEnabled();
	});
});
