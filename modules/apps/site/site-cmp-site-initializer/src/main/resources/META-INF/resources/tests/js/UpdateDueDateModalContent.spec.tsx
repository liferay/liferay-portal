/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import UpdateDueDateModalContent from '../../js/components/modal/UpdateDueDateModalContent';

jest.mock('../../js/components/DateField', () => ({
	...(jest.requireActual('../../js/components/DateField') as object),
	__esModule: true,
	default: ({
		errorMessage,
		id,
		initialValue,
		onChange,
	}: {
		errorMessage?: string;
		id: string;
		initialValue?: string;
		onChange: (value: string) => Promise<void> | void;
		required?: boolean;
	}) => (
		<>
			<input
				data-testid="mock-date-field"
				defaultValue={initialValue}
				id={id}
				onChange={(event) => onChange(event.target.value)}
				type="text"
			/>

			{errorMessage ? <span>{errorMessage}</span> : null}
		</>
	),
}));

const mockPatchTaskById = jest.fn();

jest.mock('../../js/utils/api', () => ({
	patchTaskById: (...args: any[]) => mockPatchTaskById(...args),
}));

const mockDisplayDueDateSuccessToast = jest.fn();

jest.mock('../../js/utils/toastUtil', () => ({
	displayDueDateSuccessToast: (...args: any[]) =>
		mockDisplayDueDateSuccessToast(...args),
}));

const mockDisplayErrorToast = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	displayErrorToast: (...args: any[]) => mockDisplayErrorToast(...args),
}));

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();

const renderModal = (
	props?: Partial<React.ComponentProps<typeof UpdateDueDateModalContent>>
) =>
	render(
		<UpdateDueDateModalContent
			closeModal={mockCloseModal}
			cmpTaskObjectEntryId="123"
			cmpTaskObjectEntryTitle="Task Title"
			dueDate="2026-07-15T00:00:00Z"
			loadData={mockLoadData}
			{...props}
		/>
	);

describe('UpdateDueDateModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('clears the due date when submitted empty', async () => {
		mockPatchTaskById.mockResolvedValue({error: null});

		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: ''},
		});

		fireEvent.submit(getByText('update').closest('form')!);

		await waitFor(() => {
			expect(mockPatchTaskById).toHaveBeenCalledWith({
				body: {dueDate: ''},
				taskId: '123',
			});
		});

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockLoadData).toHaveBeenCalled();
	});

	it('clears the due date when submitted with only whitespace', async () => {
		mockPatchTaskById.mockResolvedValue({error: null});

		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '   '},
		});

		fireEvent.submit(getByText('update').closest('form')!);

		await waitFor(() => {
			expect(mockPatchTaskById).toHaveBeenCalledWith({
				body: {dueDate: ''},
				taskId: '123',
			});
		});

		expect(mockCloseModal).toHaveBeenCalled();
	});

	it('hands the updated task to onTaskUpdated instead of reloading', async () => {
		const updatedTask = {dueDate: '2026-08-20', id: 123};

		mockPatchTaskById.mockResolvedValue({data: updatedTask, error: null});

		const onTaskUpdated = jest.fn();

		const {getByTestId, getByText} = renderModal({onTaskUpdated});

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '08/20/2026'},
		});

		fireEvent.submit(getByText('update').closest('form')!);

		await waitFor(() => {
			expect(onTaskUpdated).toHaveBeenCalledWith(updatedTask);
		});

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockLoadData).not.toHaveBeenCalled();
	});

	it('pre-fills the picker with the task due date', () => {
		const {getByTestId} = renderModal();

		expect(getByTestId('mock-date-field')).toHaveValue('07/15/2026');
	});

	it('shows an error toast and stays open when the patch fails', async () => {
		mockPatchTaskById.mockResolvedValue({
			error: 'You do not have permission',
		});

		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '08/20/2026'},
		});

		fireEvent.submit(getByText('update').closest('form')!);

		await waitFor(() => {
			expect(mockDisplayErrorToast).toHaveBeenCalledWith(
				'You do not have permission'
			);
		});

		expect(mockCloseModal).not.toHaveBeenCalled();
	});

	it('updates the due date on submit', async () => {
		mockPatchTaskById.mockResolvedValue({error: null});

		const {getByTestId, getByText} = renderModal();

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '08/20/2026'},
		});

		fireEvent.submit(getByText('update').closest('form')!);

		await waitFor(() => {
			expect(mockPatchTaskById).toHaveBeenCalledWith({
				body: {dueDate: '2026-08-20'},
				taskId: '123',
			});
		});

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockLoadData).toHaveBeenCalled();
		expect(mockDisplayDueDateSuccessToast).toHaveBeenCalledWith(
			'Task Title'
		);
	});
});
