/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import BulkEditWorkflowAssigneeModalContent from '../../js/components/modal/BulkEditWorkflowAssigneeModalContent';

const mockOpenToast = jest.fn();

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args: any[]) => mockOpenToast(...args),
}));

const mockBulkAssignWorkflowTasks = jest.fn();
const mockGetAssignableUsersForWorkflowTasks = jest.fn();

jest.mock('../../js/utils/api', () => ({
	bulkAssignWorkflowTasks: (...args: any[]) =>
		mockBulkAssignWorkflowTasks(...args),
	getAssignableUsersForWorkflowTasks: (...args: any[]) =>
		mockGetAssignableUsersForWorkflowTasks(...args),
}));

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();
const mockSelectedData = {
	items: [{embedded: {id: 1}}, {embedded: {id: 2}}],
};

const mockAssignableUsersResponse = {
	data: {
		workflowTaskAssignableUsers: [
			{
				assignableUsers: [
					{id: 10, name: 'User A'},
					{id: 20, name: 'User B'},
				],
				workflowTaskId: 1,
			},
			{
				assignableUsers: [{id: 10, name: 'User A'}],
				workflowTaskId: 2,
			},
		],
	},
	error: null,
};

describe('BulkEditWorkflowAssigneeModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
	});

	it('calls close modal on cancel click', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce(
			mockAssignableUsersResponse
		);

		const {getByText} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		fireEvent.click(getByText('cancel'));

		expect(mockCloseModal).toHaveBeenCalled();
	});

	it('intersects assignable users across all selected tasks', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce(
			mockAssignableUsersResponse
		);

		const {queryByText} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => {
			expect(queryByText('User B')).not.toBeInTheDocument();
		});
	});

	it('patches assign-to-user endpoint and closes modal on success', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce(
			mockAssignableUsersResponse
		);
		mockBulkAssignWorkflowTasks.mockResolvedValueOnce({error: null});

		const {getByText} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => getByText('User A'));

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() => {
			expect(mockBulkAssignWorkflowTasks).toHaveBeenCalledWith([
				{assigneeId: 10, workflowTaskId: 1},
				{assigneeId: 10, workflowTaskId: 2},
			]);
			expect(mockCloseModal).toHaveBeenCalled();
			expect(mockLoadData).toHaveBeenCalled();
		});
	});

	it('renders disabled combobox when no assignable users are found', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce({
			data: {workflowTaskAssignableUsers: []},
			error: null,
		});

		const {getByRole} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => {
			expect(getByRole('combobox')).toBeDisabled();
		});
	});

	it('renders the modal and fetches assignable users', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce(
			mockAssignableUsersResponse
		);

		const {getByText} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => {
			expect(getByText('User A')).toBeInTheDocument();
		});
	});

	it('shows a success toast on success', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce(
			mockAssignableUsersResponse
		);
		mockBulkAssignWorkflowTasks.mockResolvedValueOnce({error: null});

		const {getByText} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => getByText('User A'));

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() =>
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'success'})
			)
		);
	});

	it('shows error toast and re-enables save when assign patch fails', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce(
			mockAssignableUsersResponse
		);
		mockBulkAssignWorkflowTasks.mockResolvedValueOnce({
			error: new Error('patch failed'),
		});

		const {getByText} = render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => getByText('User A'));

		fireEvent.submit(getByText('save').closest('form')!);

		await waitFor(() =>
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			)
		);

		expect(mockCloseModal).not.toHaveBeenCalled();
		expect(getByText('save')).not.toBeDisabled();
	});

	it('shows error toast when fetching assignable users fails', async () => {
		mockGetAssignableUsersForWorkflowTasks.mockResolvedValueOnce({
			data: null,
			error: new Error('network error'),
		});

		render(
			<BulkEditWorkflowAssigneeModalContent
				closeModal={mockCloseModal}
				loadData={mockLoadData}
				selectedData={mockSelectedData as any}
			/>
		);

		await waitFor(() => {
			expect(mockOpenToast).toHaveBeenCalledWith(
				expect.objectContaining({type: 'danger'})
			);
		});
	});
});
