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
		VerticalNavLayout: ({items}: any) => (
			<div>
				{items.map((item: any) => (
					<section aria-label={item.label} key={item.id}>
						{item.component}
					</section>
				))}
			</div>
		),
	};
});

const MAPPING_RULE = {
	id: 0,
	stripHTMLTags: false,
	textCase: '',
	trimWhitespace: false,
	truncateLength: 0,
	urlEncode: false,
	useFirstNonemptySource: false,
};

const PIM_BASIC_PRODUCT_CLASS_NAME =
	'com.liferay.object.model.ObjectDefinition#P4R4';

const PROPS = {
	apiURL: '/o/pim/connector-field-mappings',
	backURL: '/web/cms/field-mappings?objectEntryId=42',
	channelField: 'skus[].sku',
	channelFieldLabel: 'SKU',
	channelFieldMultiple: false,
	fieldMappings: [],
	mappingRule: null,
	mappingRuleAPIURL: '/o/pim/connector-field-mapping-rules',
	mappingRuleObjectRelationshipObjectFieldName:
		'r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId',
	objectDefinitions: [
		{
			className: PIM_BASIC_PRODUCT_CLASS_NAME,
			label: 'PIM Basic Product',
			objectFields: [{label: 'SKU', name: 'sku'}],
		},
		{
			className: 'com.liferay.object.model.ObjectDefinition#S1Z3',
			label: 'PIM Shirt',
			objectFields: [{label: 'Title', name: 'title'}],
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
			'sku'
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
			sourceFieldName: 'sku',
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
						sourceFieldName: 'sku',
						type: 'dynamicValue',
						value: '',
					},
				]}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'title'
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
		expect(getRequestBody(0).sourceFieldName).toBe('title');
	});

	it('numbers the rows it saves by their order', async () => {
		render(<EditPIMConnectorFieldMappings {...PROPS} />);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'sku'
		);

		await userEvent.click(screen.getByLabelText('add-row'));

		await userEvent.selectOptions(
			screen.getAllByLabelText('source-attribute')[1],
			'title'
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(getRequestBody(0).priority).toBe(0);
		expect(getRequestBody(0).sourceFieldName).toBe('sku');
		expect(getRequestBody(1).priority).toBe(1);
		expect(getRequestBody(1).sourceFieldName).toBe('title');
	});

	it('deletes a field mapping the integrator unmapped', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				fieldMappings={[
					{
						id: 7,
						sourceClassName: '',
						sourceFieldName: 'sku',
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
			'sku'
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

	it('disables the mapping rules of a channel field that is not text', () => {
		render(<EditPIMConnectorFieldMappings {...PROPS} />);

		expect(screen.getByLabelText('strip-html-tags')).toBeDisabled();
	});

	it('creates a mapping rule carrying the connector relationship', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				mappingRule={MAPPING_RULE}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'sku'
		);

		await userEvent.click(screen.getByLabelText('strip-html-tags'));

		await userEvent.selectOptions(screen.getByLabelText('truncate'), '80');

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(mockFetch.mock.calls[1][0]).toBe(
			'/o/pim/connector-field-mapping-rules'
		);
		expect(mockFetch.mock.calls[1][1].method).toBe('POST');
		expect(getRequestBody(1)).toEqual({
			channelFieldName: 'skus[].sku',
			r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId: 42,
			stripHTMLTags: true,
			textCase: '',
			trimWhitespace: false,
			truncateLength: 80,
			urlEncode: false,
			useFirstNonemptySource: false,
		});

		expect(mockNavigate).toHaveBeenCalledWith(PROPS.backURL);
	});

	it('patches a mapping rule it was given', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				mappingRule={{...MAPPING_RULE, id: 9, stripHTMLTags: true}}
			/>
		);

		await userEvent.selectOptions(
			screen.getByLabelText('source-attribute'),
			'sku'
		);

		await userEvent.selectOptions(screen.getByLabelText('case'), 'upper');

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(2));

		expect(mockFetch.mock.calls[1][0]).toBe(
			'/o/pim/connector-field-mapping-rules/9'
		);
		expect(mockFetch.mock.calls[1][1].method).toBe('PATCH');
		expect(getRequestBody(1)).not.toHaveProperty(
			'r_pimConnectorToPIMFieldMappingRules_l_pimConnectorId'
		);
		expect(getRequestBody(1).textCase).toBe('upper');
	});

	it('deletes a mapping rule whose rules are all turned off', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				mappingRule={{...MAPPING_RULE, id: 9, stripHTMLTags: true}}
			/>
		);

		await userEvent.click(screen.getByLabelText('strip-html-tags'));

		await userEvent.click(screen.getByText('save'));

		await waitFor(() => expect(mockFetch).toHaveBeenCalledTimes(1));

		expect(mockFetch.mock.calls[0][0]).toBe(
			'/o/pim/connector-field-mapping-rules/9'
		);
		expect(mockFetch.mock.calls[0][1].method).toBe('DELETE');
	});

	it('creates no mapping rule when every rule is off', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				mappingRule={MAPPING_RULE}
			/>
		);

		await userEvent.click(screen.getByText('save'));

		await waitFor(() =>
			expect(mockNavigate).toHaveBeenCalledWith(PROPS.backURL)
		);

		expect(mockFetch).not.toHaveBeenCalled();
	});

	it('refuses to save mapping rules without a source', async () => {
		render(
			<EditPIMConnectorFieldMappings
				{...PROPS}
				mappingRule={MAPPING_RULE}
			/>
		);

		await userEvent.click(screen.getByLabelText('strip-html-tags'));

		await userEvent.click(screen.getByText('save'));

		expect(Liferay.Util.openToast).toHaveBeenCalledWith({
			message:
				'add-at-least-one-source-attribute-to-save-the-mapping-rules-or-turn-the-mapping-rules-off',
			type: 'danger',
		});
		expect(mockFetch).not.toHaveBeenCalled();
		expect(mockNavigate).not.toHaveBeenCalled();
	});
});
