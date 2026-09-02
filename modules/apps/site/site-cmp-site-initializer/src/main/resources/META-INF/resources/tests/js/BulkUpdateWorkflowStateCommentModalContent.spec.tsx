/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import BulkUpdateWorkflowStateCommentModalContent from '../../js/components/modal/BulkUpdateWorkflowStateCommentModalContent';

const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

const mockBulkChangeWorkflowTaskTransitions = jest.fn();

jest.mock('../../js/utils/api', () => ({
	bulkChangeWorkflowTaskTransitions: (...args: any[]) =>
		mockBulkChangeWorkflowTaskTransitions(...args),
}));

const changeTransitions = [
	{transitionName: 'approve', workflowTaskId: 1},
	{transitionName: 'publish', workflowTaskId: 2},
];

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();

function renderModal() {
	return render(
		<BulkUpdateWorkflowStateCommentModalContent
			changeTransitions={changeTransitions}
			closeModal={mockCloseModal}
			loadData={mockLoadData}
		/>
	);
}

function save() {
	fireEvent.submit(
		screen.getByText('save').closest('form') as HTMLFormElement
	);
}

describe('BulkUpdateWorkflowStateCommentModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();

		mockBulkChangeWorkflowTaskTransitions.mockResolvedValue({});
	});

	it('closes and reloads the data once the transitions are applied', async () => {
		renderModal();

		save();

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'success'})
			);
		});

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockLoadData).toHaveBeenCalled();
	});

	it('discards everything when the modal is cancelled', () => {
		renderModal();

		fireEvent.click(screen.getByText('cancel'));

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockBulkChangeWorkflowTaskTransitions).not.toHaveBeenCalled();
	});

	it('refreshes the list when applying the transitions fails', async () => {
		mockBulkChangeWorkflowTaskTransitions.mockResolvedValue({
			error: 'Task 2 could not be transitioned',
		});

		renderModal();

		save();

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({
					message: 'Task 2 could not be transitioned',
					type: 'danger',
				})
			);
		});

		expect(mockLoadData).toHaveBeenCalled();
		expect(mockCloseModal).toHaveBeenCalled();
	});

	it('sends the comment with every transition', async () => {
		renderModal();

		fireEvent.change(screen.getByLabelText('comment'), {
			target: {value: 'Please review'},
		});

		save();

		await waitFor(() => {
			expect(mockBulkChangeWorkflowTaskTransitions).toHaveBeenCalledWith([
				{
					comment: 'Please review',
					transitionName: 'approve',
					workflowTaskId: 1,
				},
				{
					comment: 'Please review',
					transitionName: 'publish',
					workflowTaskId: 2,
				},
			]);
		});
	});
});
