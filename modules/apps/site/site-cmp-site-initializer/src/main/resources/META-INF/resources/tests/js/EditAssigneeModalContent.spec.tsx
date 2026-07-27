/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import EditAssigneeModalContent from '../../js/components/modal/EditAssigneeModalContent';
import createMockFetchAssigneeContext from '../js/__mocks__/createMockFetchAssigneeContext';

const mockCloseModal = jest.fn();
const mockLoadData = jest.fn();
const mockPatchTaskById = jest.fn();

jest.mock('../../js/utils/api', () => ({
	patchTaskById: (...args: any[]) => mockPatchTaskById(...args),
}));

describe('EditAssigneeModalContent', () => {
	beforeEach(() => {
		jest.clearAllMocks();
		global.fetch = createMockFetchAssigneeContext();
	});

	it('calls patchTaskById when submitting the form', async () => {
		mockPatchTaskById.mockResolvedValue({ok: true});

		const {getByText} = render(
			<EditAssigneeModalContent
				closeModal={mockCloseModal}
				cmpTaskObjectEntryId="123"
				cmpTaskObjectEntryTitle="Task Title"
				loadData={mockLoadData}
				value={{name: 'New Assignee'}}
			/>
		);

		const saveButton = getByText('save');

		fireEvent.click(saveButton);

		await waitFor(() => {
			expect(mockPatchTaskById).toHaveBeenCalledWith({
				body: {assignTo: {name: 'New Assignee'}},
				taskId: '123',
			});
		});

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockLoadData).toHaveBeenCalled();
	});

	it('hands the updated task to onTaskUpdated instead of reloading', async () => {
		const updatedTask = {assignTo: {name: 'New Assignee'}, id: 123};

		mockPatchTaskById.mockResolvedValue({data: updatedTask, error: null});

		const onTaskUpdated = jest.fn();

		const {getByText} = render(
			<EditAssigneeModalContent
				closeModal={mockCloseModal}
				cmpTaskObjectEntryId="123"
				cmpTaskObjectEntryTitle="Task Title"
				loadData={mockLoadData}
				onTaskUpdated={onTaskUpdated}
				value={{name: 'New Assignee'}}
			/>
		);

		fireEvent.click(getByText('save'));

		await waitFor(() => {
			expect(onTaskUpdated).toHaveBeenCalledWith(updatedTask);
		});

		expect(mockCloseModal).toHaveBeenCalled();
		expect(mockLoadData).not.toHaveBeenCalled();
	});
});
