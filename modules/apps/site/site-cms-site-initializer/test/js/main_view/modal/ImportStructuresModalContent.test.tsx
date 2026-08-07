/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import {openToast} from 'frontend-js-components-web';
import React from 'react';

import ApiHelper from '../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper';
import {openCMSModal} from '../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal';
import ImportStructuresModalContent from '../../../../src/main/resources/META-INF/resources/js/main_view/modal/ImportStructuresModalContent';

jest.mock('frontend-js-components-web', () => ({
	openToast: jest.fn(),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/ApiHelper'
);
jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/utils/openCMSModal'
);

const mockGet = ApiHelper.get as jest.MockedFunction<typeof ApiHelper.get>;
const mockOpenCMSModal = openCMSModal as jest.MockedFunction<
	typeof openCMSModal
>;
const mockOpenToast = openToast as jest.MockedFunction<typeof openToast>;
const mockPostFormData = ApiHelper.postFormData as jest.MockedFunction<
	typeof ApiHelper.postFormData
>;

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();

const DEFAULT_PROPS = {
	closeModal: mockCloseModal,
	importURL: '/o/site-cms-site-initializer/import',
	loadData: mockLoadData,
};

const STRUCTURE_API_URL =
	'/o/object-admin/v1.0/object-definitions/by-external-reference-code/';

const renderComponent = (props = DEFAULT_PROPS) =>
	render(<ImportStructuresModalContent {...props} />);

const createJSONFile = (content: string, name = 'structure.json') =>
	new File([content], name, {type: 'application/json'});

const boundObjectDefinitionsFile = createJSONFile(
	JSON.stringify([
		{
			externalReferenceCode: 'STRUCTURE1',
			objectFolderExternalReferenceCode: 'L_CMS_CONTENT_STRUCTURES',
		},
		{
			externalReferenceCode: 'REPEATABLEGROUP1',
			objectFolderExternalReferenceCode:
				'L_CMS_STRUCTURE_REPEATABLE_GROUPS',
		},
	])
);

const selectFile = async (container: HTMLElement, file: File) => {
	const input =
		container.querySelector<HTMLInputElement>('input[type="file"]')!;

	fireEvent.change(input, {target: {files: [file]}});

	await screen.findByText(file.name);
};

describe('ImportStructuresModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockGet.mockResolvedValue({
			data: null,
			error: 'Not found',
			status: null,
		});
		mockPostFormData.mockResolvedValue({
			data: {},
			error: null,
			status: null,
		});
	});

	it('renders the header, description, file field, and enabled buttons', () => {
		renderComponent();

		expect(
			screen.getByText('import-content-structures')
		).toBeInTheDocument();
		expect(
			screen.getByText(
				'select-a-json-file-to-import-the-content-structures'
			)
		).toBeInTheDocument();
		expect(screen.getByText('select-file')).toBeInTheDocument();
		expect(screen.getByText('cancel')).toBeInTheDocument();
		expect(screen.getByText('import')).toBeEnabled();
	});

	it('shows a required error and does not import when no file is selected', async () => {
		renderComponent();

		fireEvent.click(screen.getByText('import'));

		expect(
			await screen.findByText('the-x-field-is-required')
		).toBeInTheDocument();
		expect(mockPostFormData).not.toHaveBeenCalled();
	});

	it('shows an error and does not import when the file is not valid JSON', async () => {
		const {container} = renderComponent();

		await selectFile(container, createJSONFile('not json', 'bad.json'));

		fireEvent.click(screen.getByText('import'));

		expect(
			await screen.findByText('you-have-entered-invalid-json')
		).toBeInTheDocument();
		expect(mockPostFormData).not.toHaveBeenCalled();
	});

	it('imports directly when no structure already exists', async () => {
		const {container} = renderComponent();

		await selectFile(container, boundObjectDefinitionsFile);

		fireEvent.click(screen.getByText('import'));

		await waitFor(() => {
			expect(mockPostFormData).toHaveBeenCalledTimes(1);
		});

		expect(mockGet).toHaveBeenCalledTimes(1);
		expect(mockGet).toHaveBeenCalledWith(`${STRUCTURE_API_URL}STRUCTURE1`);
		expect(mockCloseModal).toHaveBeenCalledTimes(1);
		expect(mockLoadData).toHaveBeenCalledTimes(1);
	});

	it('posts the bound definitions as an array', async () => {
		const {container} = renderComponent();

		await selectFile(container, boundObjectDefinitionsFile);

		fireEvent.click(screen.getByText('import'));

		await waitFor(() => {
			expect(mockPostFormData).toHaveBeenCalledTimes(1);
		});

		const formData = mockPostFormData.mock.calls[0][0];

		const objectDefinitions = JSON.parse(
			formData.get('objectDefinitions') as string
		);

		expect(objectDefinitions).toHaveLength(2);
		expect(formData.get('active')).toBe('true');
	});

	it('opens the override warning modal when a structure already exists', async () => {
		mockGet.mockResolvedValue({
			data: {name: 'My Existing Structure'} as any,
			error: null,
			status: null,
		});

		const {container} = renderComponent();

		await selectFile(container, boundObjectDefinitionsFile);

		fireEvent.click(screen.getByText('import'));

		await waitFor(() => {
			expect(mockOpenCMSModal).toHaveBeenCalledTimes(1);
		});

		expect(mockOpenCMSModal.mock.calls[0][0].status).toBe('warning');
		expect(mockCloseModal).toHaveBeenCalledTimes(1);
		expect(mockPostFormData).not.toHaveBeenCalled();
	});

	it('lists the existing structures and imports from the warning modal', async () => {
		mockGet.mockResolvedValue({
			data: {name: 'My Existing Structure'} as any,
			error: null,
			status: null,
		});

		const {container} = renderComponent();

		await selectFile(container, boundObjectDefinitionsFile);

		fireEvent.click(screen.getByText('import'));

		await waitFor(() => {
			expect(mockOpenCMSModal).toHaveBeenCalledTimes(1);
		});

		const WarningModalContent =
			mockOpenCMSModal.mock.calls[0][0].contentComponent;

		cleanup();

		render(<WarningModalContent closeModal={jest.fn()} />);

		expect(screen.getByText('My Existing Structure')).toBeInTheDocument();

		fireEvent.click(screen.getByText('import'));

		await waitFor(() => {
			expect(mockPostFormData).toHaveBeenCalledTimes(1);
		});
	});

	it('shows a danger toast and closes the modal when the import fails', async () => {
		mockPostFormData.mockResolvedValue({
			data: null,
			error: 'Error importing structure',
			status: 'ERROR',
		});

		const {container} = renderComponent();

		await selectFile(container, boundObjectDefinitionsFile);

		fireEvent.click(screen.getByText('import'));

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			);
		});

		expect(mockCloseModal).toHaveBeenCalled();
	});
});
