/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import BulkEditWorkflowDueDateModalContent from '../../js/components/modal/BulkEditWorkflowDueDateModalContent';

jest.mock('../../js/components/DateField', () => ({
	__esModule: true,
	dateConfig: {momentFormat: 'MM/DD/YYYY'},
	default: ({
		id,
		onChange,
	}: {
		id: string;
		onChange: (value: string) => Promise<void> | void;
	}) => (
		<input
			data-testid="mock-date-field"
			id={id}
			onChange={(event) => onChange(event.target.value)}
			type="text"
		/>
	),
}));

const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

const mockBulkUpdateWorkflowTaskDueDate = jest.fn();

jest.mock('../../js/utils/api', () => ({
	bulkUpdateWorkflowTaskDueDate: (...args: any[]) =>
		mockBulkUpdateWorkflowTaskDueDate(...args),
}));

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();
const mockSelectedData = {
	items: [{embedded: {id: 1}}, {embedded: {id: 2}}],
};

describe('BulkEditWorkflowDueDateModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('calls close modal on cancel click', () => {
		const {getByText} = render(
			<BulkEditWorkflowDueDateModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		fireEvent.click(getByText('cancel'));

		expect(mockCloseModal).toHaveBeenCalled();
	});

	it('does not submit when no date is selected', async () => {
		const {getByText} = render(
			<BulkEditWorkflowDueDateModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() => {
			expect(mockBulkUpdateWorkflowTaskDueDate).not.toHaveBeenCalled();
		});
	});

	it('keeps modal open and re-enables save button when patch fails', async () => {
		mockBulkUpdateWorkflowTaskDueDate.mockResolvedValueOnce({
			error: new Error('Failed'),
		});

		const {getByTestId, getByText} = render(
			<BulkEditWorkflowDueDateModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '06/05/2026'},
		});

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() => {
			expect(mockCloseModal).not.toHaveBeenCalled();
			expect(getByText('save')).not.toBeDisabled();
		});
	});

	it('patches update-due-date endpoint and closes modal on success', async () => {
		mockBulkUpdateWorkflowTaskDueDate.mockResolvedValueOnce({error: null});

		const {getByTestId, getByText} = render(
			<BulkEditWorkflowDueDateModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '06/05/2026'},
		});

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() => {
			expect(mockBulkUpdateWorkflowTaskDueDate).toHaveBeenCalled();
			expect(mockCloseModal).toHaveBeenCalled();
			expect(mockLoadData).toHaveBeenCalled();
		});
	});

	it('renders the modal with save button disabled when no date is selected', () => {
		const {getByText} = render(
			<BulkEditWorkflowDueDateModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		expect(getByText('save')).toBeDisabled();
	});

	it('shows a success toast on success', async () => {
		mockBulkUpdateWorkflowTaskDueDate.mockResolvedValueOnce({error: null});

		const {getByTestId, getByText} = render(
			<BulkEditWorkflowDueDateModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		fireEvent.change(getByTestId('mock-date-field'), {
			target: {value: '06/05/2026'},
		});

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() =>
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'success'})
			)
		);
	});
});
