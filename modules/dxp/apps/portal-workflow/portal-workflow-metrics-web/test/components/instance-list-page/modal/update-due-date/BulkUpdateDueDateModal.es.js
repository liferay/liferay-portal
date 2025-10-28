/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent} from '@testing-library/react';

// import React, {useState} from 'react';

// import {InstanceListContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
// import {ModalContext} from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
// import BulkUpdateDueDateModal from '../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/update-due-date/BulkUpdateDueDateModal.es';
// import ToasterProvider from '../../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
// import {MockRouter} from '../../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

// const {assignees, items, processSteps} = {
// 	assignees: [{id: 1, name: 'Test Test'}],
// 	items: [
// 		{
// 			assetTitle: 'Blog 1',
// 			assetType: 'Blog',
// 			assignee: {
// 				id: 1,
// 				name: 'Test Test',
// 			},
// 			id: 1,
// 			instanceId: 1,
// 			label: 'Review',
// 		},
// 		{
// 			assetTitle: 'Blog 2',
// 			assetType: 'Blog',
// 			assignee: {
// 				id: 1,
// 				name: 'Test Test',
// 			},
// 			id: 2,
// 			instanceId: 2,
// 			label: 'Update',
// 		},
// 	],
// 	processSteps: [
// 		{key: 'review', name: 'Review'},
// 		{key: 'update', name: 'Update'},
// 	],
// 	selectedItems: [{id: 1}, {id: 2}],
// };

// const clientMock = {
// 	patch: jest
// 		.fn()
// 		.mockRejectedValueOnce(new Error('request-failure'))
// 		.mockResolvedValueOnce({data: {}}),
// 	post: jest
// 		.fn()
// 		.mockRejectedValueOnce(new Error('request-failure'))
// 		.mockResolvedValueOnce({data: {items, totalCount: items.length + 1}})
// 		.mockRejectedValueOnce(new Error('request-failure'))
// 		.mockResolvedValue({data: {items: [items[0]], totalCount: 1}}),
// 	request: jest
// 		.fn()
// 		.mockResolvedValueOnce({data: {items: processSteps}})
// 		.mockResolvedValueOnce({data: {items: assignees}})
// 		.mockResolvedValueOnce({data: {items: processSteps}})
// 		.mockResolvedValueOnce({data: {items: assignees}})
// 		.mockResolvedValueOnce({data: {items: processSteps}})
// 		.mockResolvedValueOnce({data: {items: assignees}}),
// };

// const ContainerMock = ({children}) => {
// 	const [updateDueDate, setUpdateDueDate] = useState({});
// 	const processId = '12345';
// 	const [selectAll, setSelectAll] = useState(false);
// 	const [selectTasks, setSelectTasks] = useState({
// 		selectAll: false,
// 		tasks: [],
// 	});

// 	const [selectedItems, setSelectedItems] = useState([]);

// 	return (
// 		<MockRouter client={clientMock}>
// 			<InstanceListContext.Provider
// 				value={{
// 					selectAll,
// 					selectedItems,
// 					setSelectAll,
// 					setSelectedItems,
// 				}}
// 			>
// 				<ModalContext.Provider
// 					value={{
// 						processId,
// 						selectTasks,
// 						setSelectTasks,
// 						setUpdateDueDate,
// 						updateDueDate,
// 						visibleModal: 'bulkUpdateDueDate',
// 					}}
// 				>
// 					<ToasterProvider>{children}</ToasterProvider>
// 				</ModalContext.Provider>
// 			</InstanceListContext.Provider>
// 		</MockRouter>
// 	);
// };

describe('The BulkReassignModal component should', () => {
	let getAllByRole;
	let getAllByText;
	let getByText;

	// beforeAll(async () => {
	// 	renderResult = render(<BulkUpdateDueDateModal />, {
	// 		wrapper: ContainerMock,
	// 	});

	// 	getAllByRole = renderResult.getAllByRole;
	// 	getAllByText = renderResult.getAllByText;
	// 	getByText = renderResult.getByText;

	// 	await act(async () => {
	// 		jest.runAllTimers();
	// 	});
	// });

	xit('Render "Select tasks" step with fetch error and retrying', async () => {
		const alertError = getByText('your-request-has-failed');
		const emptyStateMessage = getByText('unable-to-retrieve-data');
		const retryBtn = getByText('retry');

		expect(alertError).toBeTruthy();
		expect(emptyStateMessage).toBeTruthy();

		fireEvent.click(retryBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render "Select tasks" step with items', async () => {
		const assigneeFilter = getByText('assignee');
		const cancelBtn = getByText('cancel');
		const checkAllButton = document.querySelectorAll(
			'input.custom-control-input'
		)[0];
		const modal = document.querySelector('.modal');
		const nextBtn = getByText('next');
		const rows = getAllByRole('row');
		const stepBar = document.querySelector('.step-of-bar');
		const table = document.querySelector('.table');

		const content = modal.children[0].children[0];
		const checkbox1 = rows[1].querySelector('input.custom-control-input');
		const checkbox2 = rows[2].querySelector('input.custom-control-input');

		const header = content.children[0];

		expect(header).toHaveTextContent('select-tasks-to-update');
		expect(stepBar.children[0]).toHaveTextContent('select-tasks');
		expect(stepBar.children[1]).toHaveTextContent('step-x-of-x');
		expect(getAllByText('process-step').length).toBe(2);
		expect(assigneeFilter).not.toBeUndefined();
		expect(cancelBtn).toBeTruthy();
		expect(nextBtn).toHaveAttribute('disabled');

		const items = table.children[1].children;

		expect(checkbox1.checked).toBe(false);
		expect(items[0].children[1]).toHaveTextContent('1');
		expect(items[0].children[2]).toHaveTextContent('Blog: Blog 1');
		expect(items[0].children[3]).toHaveTextContent('Review');
		expect(items[0].children[4]).toHaveTextContent('Test Test');

		expect(checkbox2.checked).toBe(false);
		expect(items[1].children[1]).toHaveTextContent('2');
		expect(items[1].children[2]).toHaveTextContent('Blog: Blog 2');
		expect(items[1].children[3]).toHaveTextContent('Update');
		expect(items[1].children[4]).toHaveTextContent('Test Test');

		expect(checkAllButton.checked).toBe(false);

		fireEvent.click(checkAllButton);

		await act(async () => {
			jest.runAllTimers();
		});

		let label = getByText('x-of-x-selected');

		expect(checkbox1.checked).toBe(true);
		expect(checkbox2.checked).toBe(true);
		expect(checkAllButton.checked).toBe(true);
		expect(label).toBeTruthy();

		const clearButton = getByText('clear');

		fireEvent.click(clearButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(checkbox1.checked).toBe(false);
		expect(checkbox2.checked).toBe(false);
		expect(checkAllButton.checked).toBe(false);

		fireEvent.click(checkbox1);

		label = getByText('x-of-x-selected');

		expect(checkbox1.checked).toBe(true);
		expect(checkbox2.checked).toBe(false);
		expect(checkAllButton.checked).toBe(false);
		expect(label).toBeTruthy();

		fireEvent.click(checkbox1);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(checkbox1.checked).toBe(false);
		expect(checkbox2.checked).toBe(false);
		expect(checkAllButton.checked).toBe(false);

		fireEvent.click(checkAllButton);

		label = getByText('x-of-x-selected');

		expect(checkbox1.checked).toBe(true);
		expect(checkbox2.checked).toBe(true);
		expect(checkAllButton.checked).toBe(true);
		expect(label).toBeTruthy();
		expect(nextBtn).not.toBeDisabled();

		const selectAllButton = getByText('select-all');

		await fireEvent.click(selectAllButton);

		label = getByText('all-selected');

		expect(label).toBeTruthy();
		expect(nextBtn).not.toBeDisabled();

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render "Select tasks" step with next error and retrying', async () => {
		const alertError = getByText('your-request-has-failed');
		const nextBtn = getByText('next');

		expect(alertError).toBeTruthy();
		expect(nextBtn).not.toBeDisabled();

		fireEvent.click(nextBtn);

		expect(nextBtn).toBeDisabled();

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render "Update tasks due dates" step with items and back to previous step', async () => {
		const modal = document.querySelector('.modal');
		const nextBtn = getByText('done');
		const previousBtn = getByText('previous');

		const content = modal.children[0].children[0];

		const header = content.children[0];

		expect(header).toHaveTextContent('update-tasks-due-dates');

		fireEvent.click(previousBtn);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(header).toHaveTextContent('select-tasks-to-update');

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(header).toHaveTextContent('update-tasks-due-dates');

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render "Update tasks due dates" step with update fetch error and retrying', async () => {
		const alertError = getByText(
			'your-request-has-failed select-done-to-retry'
		);
		const nextBtn = getByText('done');

		expect(alertError).toBeTruthy();

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});

		const alertToast = await document.querySelector('.alert-dismissible');

		const alertClose = alertToast.children[1];

		expect(alertToast).toHaveTextContent(
			'the-due-date-for-this-task-has-been-updated'
		);

		fireEvent.click(alertClose);

		const alertContainer = document.querySelector('.alert-container');

		expect(alertContainer.children[0].children.length).toBe(0);
	});
});
