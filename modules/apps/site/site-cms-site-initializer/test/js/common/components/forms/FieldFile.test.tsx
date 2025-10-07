/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import FieldFile, {
	TFieldFile,
} from '../../../../../src/main/resources/META-INF/resources/js/common/components/forms/FieldFile';

const DEFAULT_PROPS: TFieldFile = {
	fieldId: 'file-input',
	label: 'File',
	onFileChange: jest.fn(),
	validExtensions: '.json',
};

const renderComponent = (props: TFieldFile = DEFAULT_PROPS) => {
	return render(<FieldFile {...props} />);
};

describe('FieldFile', () => {
	it('renders', () => {
		renderComponent();

		expect(screen.getByLabelText('File')).toBeInTheDocument();
		expect(screen.getByRole('textbox')).toHaveValue('');
		expect(screen.getByTitle('add')).toBeInTheDocument();
	});

	it('selects a file and show its name', () => {
		const {container} = renderComponent();

		const file = new File(['{"key": "value"}'], 'test.json', {
			type: 'application/json',
		});

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		fireEvent.change(input, {
			target: {files: [file]},
		});

		expect(screen.getByRole('textbox')).toHaveValue('test.json');
	});

	it('removes the file when click on "Remove"', () => {
		const {container} = renderComponent();

		const file = new File(['{"key": "value"}'], 'test.json', {
			type: 'application/json',
		});

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		fireEvent.change(input, {
			target: {files: [file]},
		});

		fireEvent.click(screen.getByTitle('remove-x'));

		expect(screen.getByRole('textbox')).toHaveValue('');
		expect(screen.getByTitle('add')).toBeInTheDocument();
	});

	it('allows to change the file', () => {
		const {container} = renderComponent();

		const file1 = new File(['{"key": "value"}'], 'test1.json', {
			type: 'application/json',
		});
		const file2 = new File(['{"key": "value"}'], 'test2.json', {
			type: 'application/json',
		});

		const input =
			container.querySelector<HTMLInputElement>('input[type="file"]')!;

		fireEvent.change(input, {
			target: {files: [file1]},
		});

		fireEvent.click(screen.getByTitle('change-x'));

		fireEvent.change(input, {
			target: {files: [file2]},
		});

		expect(screen.getByRole('textbox')).toHaveValue('test2.json');
	});

	it('shows the error message', () => {
		renderComponent({...DEFAULT_PROPS, errorMessage: 'File error'});

		expect(screen.getByText('File error')).toBeInTheDocument();
	});
});
