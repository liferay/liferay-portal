/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, waitFor} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import EditPIMConnectorFieldMappings from '../../src/main/resources/META-INF/resources/js/EditPIMConnectorFieldMappings';

const mockFetch = jest.fn();
const mockNavigate = jest.fn();

jest.mock('frontend-js-web', () => ({
	fetch: (...args: unknown[]) => mockFetch(...args),
	navigate: (...args: unknown[]) => mockNavigate(...args),
	sub: (template: string) => template,
}));

jest.mock('@liferay/site-cms-site-initializer', () => {
	const Toolbar = ({children, title}: any) => (
		<div>
			<h1>{title}</h1>

			{children}
		</div>
	);

	Toolbar.Item = ({children}: any) => <div>{children}</div>;

	return {
		RequiredMark: () => <span>required</span>,
		Toolbar,
	};
});

const BASE_SKU_CLASS_NAME = 'com.liferay.object.model.ObjectDefinition#P4R4';

const PROPS = {
	apiURL: '/o/pim/connector-field-mappings',
	backURL: '/web/cms/field-mappings?objectEntryId=42',
	channelField: 'skus[].sku',
	channelFieldLabel: 'SKU',
	fieldMappings: [],
	objectDefinitions: [
		{
			className: BASE_SKU_CLASS_NAME,
			label: 'PIM Base SKU',
			objectFields: [{label: 'Code', name: 'code'}],
		},
		{
			className: 'com.liferay.object.model.ObjectDefinition#S1Z3',
			label: 'PIM Shirt',
			objectFields: [{label: 'Name', name: 'name'}],
		},
	],
	objectEntryId: 42,
	objectRelationshipObjectFieldName:
		'r_pimConnectorToPIMConnectorFieldMappings_l_pimConnectorId',
	spritemap: '/icons.svg',
	title: 'Edit SKU',
};

function getRequestBody(index: number) {
	return JSON.parse(mockFetch.mock.calls[index][1].body);
}

describe('EditPIMConnectorFieldMappings', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockFetch.mockResolvedValue({ok: true});
	});

	it('creates a field mapping carrying the connector relationship', async () => {
		render(<EditPIMConnectorFieldMappings {...PROPS} />);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'code'
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		expect(mockFetch.mock.calls[0][0]).toBe(
			'/o/pim/connector-field-mappings'
		);
		expect(mockFetch.mock.calls[0][1].method).toBe('POST');
		expect(getRequestBody(0)).toEqual({
			channelFieldName: 'skus[].sku',
			priority: 0,
			r_pimConnectorToPIMConnectorFieldMappings_l_pimConnectorId: 42,
			sourceClassName: '',
			sourceFieldName: 'code',
			type: 'dynamicValue',
			value: '',
		});

		expect(mockNavigate).toHaveBeenCalledWith(PROPS.backURL);
	});

	it('patches a field mapping it was given', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				fieldMappings={[
					{
						id: 7,
						sourceClassName: '',
						sourceFieldName: 'code',
						type: 'dynamicValue',
						value: '',
					},
				]}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'name'
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		expect(mockFetch.mock.calls[0][0]).toBe(
			'/o/pim/connector-field-mappings/7'
		);
		expect(mockFetch.mock.calls[0][1].method).toBe('PATCH');
		expect(getRequestBody(0)).not.toHaveProperty(
			'r_pimConnectorToPIMConnectorFieldMappings_l_pimConnectorId'
		);
		expect(getRequestBody(0).sourceFieldName).toBe('name');
	});

	it('numbers the rows it saves by their order', async () => {
		render(<EditPIMConnectorFieldMappings {...PROPS} />);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'code'
		);

		await userEvent.click(screen.getByLabelText('add-row'));

		await userEvent.selectOptions(
			screen.getAllByLabelText('source-attribute')[1],
			'name'
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(getRequestBody(0).priority).toBe(0);
		expect(getRequestBody(0).sourceFieldName).toBe('code');
		expect(getRequestBody(1).priority).toBe(1);
		expect(getRequestBody(1).sourceFieldName).toBe('name');
	});

	it('deletes a field mapping the integrator unmapped', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				fieldMappings={[
					{
						id: 7,
						sourceClassName: '',
						sourceFieldName: 'code',
						type: 'dynamicValue',
						value: '',
					},
				]}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			''
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		expect(mockFetch.mock.calls[0][0]).toBe(
			'/o/pim/connector-field-mappings/7'
		);
		expect(mockFetch.mock.calls[0][1].method).toBe('DELETE');
	});

	it('saves a fixed value without a source', async () => {
		render(<EditPIMConnectorFieldMappings {...PROPS} />);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'fixedValue'
		);

		await userEvent.type(screen.getByLabelText('value'), 'ABC-1');

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		expect(getRequestBody(0).type).toBe('fixedValue');
		expect(getRequestBody(0).value).toBe('ABC-1');
		expect(getRequestBody(0).sourceFieldName).toBe('');
	});

	it('stays on the page when a request fails', async () => {
		mockFetch.mockResolvedValue({ok: false});

		render(<EditPIMConnectorFieldMappings {...PROPS} />);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'code'
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		expect(mockNavigate).not.toHaveBeenCalled();
	});

	it('drops a field mapping whose source attribute no longer exists', () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				fieldMappings={[
					{
						id: 7,
						sourceClassName: '',
						sourceFieldName: 'discontinued',
						type: 'dynamicValue',
						value: '',
					},
				]}
			/>
		);

		expect(screen.getByLabelText('source-attribute')).toHaveValue('');
	});
});
