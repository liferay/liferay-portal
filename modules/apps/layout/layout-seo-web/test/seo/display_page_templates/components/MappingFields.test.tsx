/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, screen, within} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

// @ts-ignore

import MappingFields from '../../../../src/main/resources/META-INF/resources/js/seo/display_page_templates/components/MappingFields';

const baseProps = {
	fields: [
		{key: 'field-text-1', label: 'Field Text 1', type: 'text'},
		{key: 'field-text-2', label: 'Field Text 2', type: 'text'},
		{key: 'field-image-1', label: 'Field Image 1', type: 'image'},
		{key: 'field-image-2', label: 'Field Image 2', type: 'image'},
	],
	inputs: [
		{
			fieldTypes: ['text'],
			label: 'Text with default value',
			name: 'inputText',
			selectedFieldKey: 'field-text-1',
		},
		{
			fieldTypes: ['image'],
			label: 'Image for sharing',
			name: 'inputImage',
			selectedFieldKey: 'field-image-1',
		},
	],
	selectedSource: {
		classTypeLabel: 'Label source type',
	},
};

const renderComponent = () => render(<MappingFields {...baseProps} />);

describe('MappingFields', () => {
	it('renders mapping input or mapping select depending on the fieldTypes', () => {
		renderComponent();

		const textInput: HTMLInputElement = screen.getByLabelText(
			'Text with default value'
		);

		expect(textInput).toBeInTheDocument();
		expect(textInput.type).toBe('text');
		expect(textInput).toHaveValue('');

		const imageInput: HTMLInputElement =
			screen.getByLabelText('Image for sharing');

		expect(imageInput).toBeInTheDocument();
		expect(imageInput.readOnly).toBeTruthy();
		expect(imageInput).toHaveValue('Label source type: Field Image 1');
	});

	it('renders only fields with type "text" when the input fieldType is "text"', async () => {
		renderComponent();

		const inputField: HTMLElement | null = screen.getByText(
			'Text with default value'
		).parentElement;

		await userEvent.click(within(inputField!).getByTitle('map'));

		expect(screen.getAllByRole('option')).toHaveLength(2);
	});

	it('renders only fields with type "image" when the input fieldType is "image"', async () => {
		renderComponent();

		const inputField: HTMLElement | null =
			screen.getByText('Image for sharing').parentElement;

		await userEvent.click(within(inputField!).getByTitle('map'));

		expect(screen.getAllByRole('option')).toHaveLength(3);
	});
});
