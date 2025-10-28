/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

// @ts-ignore

import MappingInput from '../../../../src/main/resources/META-INF/resources/js/seo/display_page_templates/components/MappingInput';

const baseProps = {
	fieldTypes: ['text'],
	fields: [
		{key: 'field-1', label: 'Test Field 1', type: 'text'},
		{
			key: 'field-2',
			label: 'Field 2: with }right curly brackets}}}}, line breaks by \r\nwin\r\n\r\n\r\n, \nlinux\n and \rold mac\r',
			type: 'image',
		},
	],
	helpMessage: 'Map a text field, it will be used as Title.',
	label: 'Label test mapping field',
	name: 'testMappingInput',
};

const renderComponent = (value: string = '') => {
	const props = {...baseProps, value};

	return render(<MappingInput {...props} />);
};

describe('MappingInput', () => {
	it('renders the mapping input', () => {
		renderComponent();

		const input: HTMLInputElement = screen.getByLabelText(
			'Label test mapping field'
		);

		expect(input).toBeInTheDocument();
		expect(input.name).toBe('testMappingInput');
		expect(input.type).toBe('text');
		expect(input).toHaveValue('');
		expect(screen.getByTitle('map')).toBeInTheDocument();
		expect(
			screen.getByText('Map a text field, it will be used as Title.')
		).toBeInTheDocument();
	});

	it('renders the mapping input with the initial value', () => {
		renderComponent('custom value');

		const input: HTMLInputElement = screen.getByLabelText(
			'Label test mapping field'
		);

		expect(input).toHaveValue('custom value');
	});

	it('opens the mapping panel when the mapping button is clicked', async () => {
		renderComponent();

		expect(
			document.querySelector('.dpt-mapping-panel')
		).not.toBeInTheDocument();

		await userEvent.click(screen.getByTitle('map'));

		expect(
			document.querySelector('.dpt-mapping-panel')
		).toBeInTheDocument();
	});

	it('selects a field, adding a new field ${key:label} to the input', async () => {
		renderComponent();

		await userEvent.click(screen.getByTitle('map'));

		await userEvent.selectOptions(
			screen.getByLabelText('field'),
			'field-1'
		);

		await userEvent.click(screen.getByText('add-field'));

		const input: HTMLInputElement = screen.getByLabelText(
			'Label test mapping field'
		);

		expect(input).toHaveValue(`$\{field-1:Test Field 1}`);
	});

	it('selects a field, adding a new field ${key:label} sanitized to the input', async () => {
		renderComponent();

		await userEvent.click(screen.getByTitle('map'));

		await userEvent.selectOptions(
			screen.getByLabelText('field'),
			'field-2'
		);

		await userEvent.click(screen.getByText('add-field'));

		const sanitizedLabel =
			'Field 2: with right curly brackets, line breaks by win, linux and old mac';

		const input: HTMLInputElement = screen.getByLabelText(
			'Label test mapping field'
		);

		expect(input).toHaveValue(`$\{field-2:${sanitizedLabel}}`);
	});
});
