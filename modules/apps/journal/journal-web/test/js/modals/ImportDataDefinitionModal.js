/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, fireEvent, render} from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import Modal from '../../../src/main/resources/META-INF/resources/js/modals/ImportDataDefinitionModal';

const ImportDataDefinitionModal = () => <Modal portletNamespace="test" />;

describe('Import Structure Modal', () => {
	let file;
	let openModal;

	beforeEach(() => {
		Liferay.component = jest.fn((_component, {open}) => {
			openModal = () => act(() => open());
		});
		Liferay.destroyComponent = jest.fn();
		file = new File(['testing file upload'], 'Testing.lar', {
			type: '.lar',
		});
	});

	it('renders the title modal', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const title = await findByText('import-structure');

		expect(title).toBeInTheDocument();
	});

	it('renders the info alert', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const infoAlert = await findByText(
			'the-import-process-will-run-in-the-background-and-may-take-a-few-minutes'
		);

		expect(infoAlert).toBeInTheDocument();
	});

	it('renders name input', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const inputName = await findByText('name');

		expect(inputName).toBeInTheDocument();
	});

	it('input name has correct name prop', async () => {
		const {findByLabelText} = render(<ImportDataDefinitionModal />);

		openModal();

		const inputName = await findByLabelText('name');

		expect(inputName).toHaveAttribute('name', 'testname');
	});

	it('renders json input', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const jsonInput = await findByText('json-file');

		expect(jsonInput).toBeInTheDocument();
	});

	it('renders the attribute name as the portlet name appended by `jsonFile`', async () => {
		const {findAllByRole} = render(<ImportDataDefinitionModal />);

		openModal();

		await findAllByRole('textbox');

		const inputFile = document.querySelector('input[type="file"]');

		expect(inputFile).toHaveAttribute('name', 'testjsonFile');
	});

	it('renders cancel button in footer', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const buttonCancel = await findByText('cancel');

		expect(buttonCancel).toBeInTheDocument();
	});

	it('the cancel button closes modal', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const buttonCancel = await findByText('cancel');

		userEvent.click(buttonCancel);

		const body = document.getElementsByTagName('body').item(0);

		expect(body).not.toHaveClass();
	});

	it('renders import button in footer', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const buttonImport = await findByText('import');

		expect(buttonImport).toBeInTheDocument();
	});

	it('when exists a file selected, the file name appear in disabled input file', async () => {
		const {findAllByRole} = render(<ImportDataDefinitionModal />);

		openModal();

		await findAllByRole('textbox');

		const inputFile = document.querySelector('input[type="file"]');
		const inputFileName = document.getElementById('testjsonFile');

		fireEvent.change(inputFile, {
			target: {files: [file]},
			value: 'C://downloads/Testing.lar',
		});

		expect(inputFileName).toHaveValue('Testing.lar');
		expect(inputFileName).toBeDisabled();
	});

	it('the clear button dont appear, when not exists value in input file name', async () => {
		const {findByText, queryByText} = render(<ImportDataDefinitionModal />);

		openModal();

		await findByText('import-structure');
		const clearButton = queryByText('clear');

		expect(clearButton).not.toBeInTheDocument();
	});

	it('the clear button appear when exists value in input file name', async () => {
		const {findAllByRole, queryByText} = render(
			<ImportDataDefinitionModal />
		);

		openModal();

		await findAllByRole('textbox');

		const inputFile = document.querySelector('input[type="file"]');
		const inputFileName = document.getElementById('testjsonFile');

		fireEvent.change(inputFile, {target: {files: [file]}});

		const clearButton = queryByText('clear');

		expect(inputFileName).toHaveValue('Testing.lar');
		expect(clearButton).toBeInTheDocument();
	});

	it('the click in clear button erases the file', async () => {
		const {findAllByRole, getByText} = render(
			<ImportDataDefinitionModal />
		);

		openModal();

		await findAllByRole('textbox');

		const inputFile = document.querySelector('input[type="file"]');
		const inputFileName = document.getElementById('testjsonFile');

		fireEvent.change(inputFile, {target: {files: [file]}});

		userEvent.click(getByText('clear'));

		expect(inputFileName).toHaveValue('');
	});

	it('the import button is disabled when the input arent filled', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const buttonImport = await findByText('import');

		expect(buttonImport).toBeDisabled();
	});

	it('the import button isnt disabled when the inputs are filled', async () => {
		const {findByLabelText, getByLabelText, getByText} = render(
			<ImportDataDefinitionModal />
		);

		openModal();

		const inputName = await findByLabelText('name');
		const inputFile = document.getElementsByName('testjsonFile')[0];
		const inputFileName = getByLabelText('json-file');
		const buttonImport = getByText('import');

		fireEvent.change(inputFile, {target: {files: [file]}});
		userEvent.type(inputName, 'structure test');

		expect(inputFileName).toHaveValue('Testing.lar');
		expect(inputName).toHaveValue('structure test');
		expect(buttonImport).toBeEnabled();
	});

	it('the button import has submit property', async () => {
		const {findByText} = render(<ImportDataDefinitionModal />);

		openModal();

		const buttonImport = await findByText('import');

		expect(buttonImport).toHaveAttribute('type', 'submit');
	});
});
