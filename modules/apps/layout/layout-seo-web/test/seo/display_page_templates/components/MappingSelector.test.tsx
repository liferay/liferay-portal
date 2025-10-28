/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

// @ts-ignore

import MappingSelector from '../../../../src/main/resources/META-INF/resources/js/seo/display_page_templates/components/MappingSelector';

const baseProps = {
	fieldTypes: ['image'],
	fields: [
		{key: 'field-1', label: 'Field 1', type: 'text'},
		{key: 'field-2', label: 'Field 2', type: 'text'},
	],
	helpMessage: 'Map a image field, it will be used as Image.',
	label: 'Label test mapping field',
	name: 'testMappingSelector',
	selectedSource: {
		classTypeLabel: 'Label source type',
	},
};

const renderComponent = (selectedFieldKey: string = 'field-2') => {
	const props = {...baseProps, selectedFieldKey};

	return render(<MappingSelector {...props} />);
};

describe('MappingSelector', () => {
	it('renders the MappingSelector with initial value', () => {
		renderComponent();

		// Check the readonly input with the selected field name

		const input: HTMLInputElement = screen.getByDisplayValue(
			'Label source type: Field 2'
		);

		expect(input.readOnly).toBeTruthy();

		// Check the hidden input with the selected field key

		const hiddenInput: HTMLInputElement =
			screen.getByDisplayValue('field-2');

		expect(hiddenInput.type).toBe('hidden');
		expect(hiddenInput.name).toBe('testMappingSelector');

		// Check the help message

		expect(
			screen.getByText('Map a image field, it will be used as Image.')
		).toBeInTheDocument();
	});

	it('renders the MappingSelector without initial value', () => {
		renderComponent('');

		// Check the readonly input with the selected field name

		const input: HTMLInputElement =
			screen.getByDisplayValue('-- unmapped --');

		expect(input).toBeInTheDocument();

		// Check the hidden input with the selected field key

		const hiddenInput: HTMLInputElement =
			screen.getByDisplayValue('unmapped');

		expect(hiddenInput).toBeInTheDocument();
	});

	it('opens the mapping panel when the mapping button is clicked', async () => {
		renderComponent();

		expect(
			document.querySelector('.dpt-mapping-panel')
		).not.toBeInTheDocument();

		const mappingButton = screen.getByTitle('map');

		await userEvent.click(mappingButton);

		expect(
			document.querySelector('.dpt-mapping-panel')
		).toBeInTheDocument();

		// Check the selected field

		const select = screen.getByLabelText('field') as HTMLSelectElement;

		expect(select).toHaveValue('field-2');

		// Close the mapping panel

		await userEvent.click(mappingButton);

		expect(
			document.querySelector('.dpt-mapping-panel')
		).not.toBeInTheDocument();
	});

	it('selects another field', async () => {
		renderComponent();

		const hiddenInput: HTMLInputElement =
			screen.getByDisplayValue('field-2');
		const input: HTMLInputElement = screen.getByDisplayValue(
			'Label source type: Field 2'
		);

		await userEvent.click(screen.getByTitle('map'));

		await userEvent.selectOptions(
			screen.getByLabelText('field'),
			'field-1'
		);

		await userEvent.click(screen.getByText('map-content'));

		expect(hiddenInput).toHaveValue('field-1');
		expect(input).toHaveValue('Label source type: Field 1');
	});

	it('unmaps the field', async () => {
		renderComponent();

		const hiddenInput: HTMLInputElement =
			screen.getByDisplayValue('field-2');
		const input: HTMLInputElement = screen.getByDisplayValue(
			'Label source type: Field 2'
		);

		await userEvent.click(screen.getByTitle('map'));

		await userEvent.selectOptions(
			screen.getByLabelText('field'),
			'unmapped'
		);

		await userEvent.click(screen.getByText('map-content'));

		expect(hiddenInput).toHaveValue('unmapped');
		expect(input).toHaveValue('-- unmapped --');
	});

	it('closes the panel clicking outside', async () => {
		renderComponent();

		await userEvent.click(screen.getByTitle('map'));

		expect(
			document.querySelector('.dpt-mapping-panel')
		).toBeInTheDocument();

		await userEvent.click(
			screen.getByDisplayValue('Label source type: Field 2')
		);

		expect(
			document.querySelector('.dpt-mapping-panel')
		).not.toBeInTheDocument();
	});
});
