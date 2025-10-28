/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import {InstanceListContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
import {ModalContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import SingleReassignModal from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/reassign/single/SingleReassignModal.es';
import {MockRouter} from '../../../../../mock/MockRouter.es';

const ContainerMock = ({children}) => {
	const [selectedItem, setSelectedItem] = useState({
		assetTitle: 'Blog1',
		assetType: 'Blogs Entry',
		assignees: [{id: 2, name: 'Test Test'}],
		id: 1,
		status: 'In Progress',
		taskNames: ['Review'],
	});
	const [selectedItems, setSelectedItems] = useState([]);

	return (
		<MockRouter>
			<InstanceListContext.Provider
				value={{
					selectedItem,
					selectedItems,
					setSelectedItem,
					setSelectedItems,
				}}
			>
				<ModalContext.Provider>{children}</ModalContext.Provider>
			</InstanceListContext.Provider>
		</MockRouter>
	);
};

const setAssigneeId = jest.fn();

describe('The SingleReassignModalTable component should', () => {
	afterEach(cleanup);

	const data = {
		items: [
			{
				assigneePerson: {
					id: 20124,
					name: 'Test Test',
				},
				completed: true,
				id: 40336,
				instanceId: 40330,
				name: 'review',
			},
		],
	};

	const mockItem = {
		assetTitle: 'Blog2',
		assetType: 'Blogs Entry',
		assignees: [{id: 20124, name: 'Test Test'}],
		creator: {id: 20124, name: 'Test Test'},
		dateCreated: '2019-12-10T17:44:44Z',
		id: 40330,
		slaStatus: 'Overdue',
		status: 'Completed',
		taskNames: ['Update'],
	};

	it('Render with statuses Completed and Overdue', () => {
		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items: [{id: 1, name: 'Test'}]}),
			ok: true,
		});

		const {container} = render(
			<SingleReassignModal.Table
				data={data}
				setAssigneeId={setAssigneeId}
				{...mockItem}
			/>,
			{
				wrapper: ContainerMock,
			}
		);

		const singleReassignModalTable = container.querySelector('.table');

		expect(singleReassignModalTable.innerHTML).not.toBeUndefined();
		expect(singleReassignModalTable.innerHTML).not.toBeNull();
	});

	it('Render with no taskName', () => {
		const {container} = render(
			<SingleReassignModal.Table
				data={data}
				setAssigneeId={setAssigneeId}
				{...mockItem}
			/>,
			{
				wrapper: ContainerMock,
			}
		);

		const singleReassignModalTable = container.querySelector('.table');

		expect(singleReassignModalTable.innerHTML).not.toBeUndefined();
		expect(singleReassignModalTable.innerHTML).not.toBeNull();
	});

	it('Render with no data', () => {
		const {container} = render(
			<SingleReassignModal.Table
				data={{}}
				setAssigneeId={setAssigneeId}
				{...mockItem}
			/>,
			{
				wrapper: ContainerMock,
			}
		);

		const singleReassignModalTable = container.querySelector('.table');

		expect(singleReassignModalTable.innerHTML).not.toBeUndefined();
		expect(singleReassignModalTable.innerHTML).not.toBeNull();
	});

	it('Render with taskNames', () => {
		const data = {
			items: [
				{
					assigneeRoles: [],
					completed: true,
					dateCompletion: '2019-12-10T17:45:38Z',
					dateCreated: '2019-12-10T17:44:45Z',
					definitionId: 38902,
					definitionName: 'Single Approver',
					definitionVersion: '',
					description: '',
					instanceId: 40330,
					name: 'review',
					objectReviewed: {
						id: 40324,
						resourceType: 'BlogPosting',
					},
				},
			],
		};

		const {container} = render(
			<SingleReassignModal.Table
				data={data}
				setAssigneeId={setAssigneeId}
				{...mockItem}
			/>,
			{
				wrapper: ContainerMock,
			}
		);

		const singleReassignModalTable = container.querySelector('.table');

		expect(singleReassignModalTable.innerHTML).not.toBeUndefined();
		expect(singleReassignModalTable.innerHTML).not.toBeNull();
	});
});

describe('The AssigneeInput component should', () => {
	const setReassignMock = jest.fn();

	it('Render change assignee input text to Test', async () => {
		cleanup();

		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items: [{id: 1, name: 'Test'}]}),
			ok: true,
		});

		render(
			<MockRouter>
				<SingleReassignModal.Table.AssigneeInput
					reassignedTasks={{
						tasks: [{assigneeId: 20124, id: 39347}],
					}}
					setAssigneeId={setAssigneeId}
					setReassignedTasks={setReassignMock}
					taskId={39347}
				/>
			</MockRouter>
		);

		const autocompleteInput = document.querySelector('input.form-control');

		fireEvent.change(autocompleteInput, {target: {value: 'Test'}});

		await act(async () => {
			jest.advanceTimersByTime(100);
		});

		expect(autocompleteInput.value).toBe('Test');
	});

	it('Change its text to "Test"', async () => {
		cleanup();

		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items: [{id: 1, name: 'Test'}]}),
			ok: true,
		});

		render(
			<MockRouter>
				<SingleReassignModal.Table.AssigneeInput
					reassignedTasks={{
						tasks: [{assigneeId: 20124, id: 39347}],
					}}
					setReassignedTasks={setReassignMock}
					taskId={39347}
				/>
			</MockRouter>
		);

		await act(async () => {
			jest.advanceTimersByTime(100);
		});

		expect(fetch).toHaveBeenCalled();
	});

	it('Select a new assignee', async () => {
		cleanup();

		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items: [{id: 1, name: 'Test'}]}),
			ok: true,
		});

		render(
			<MockRouter>
				<SingleReassignModal.Table.AssigneeInput
					reassignedTasks={{tasks: []}}
					setAssigneeId={setAssigneeId}
					setReassignedTasks={setReassignMock}
					taskId={39347}
				/>
			</MockRouter>
		);

		const autocompleteInput = document.querySelector('input.form-control');

		fireEvent.change(autocompleteInput, {target: {value: 'Test'}});

		await act(async () => {
			jest.advanceTimersByTime(100);
		});

		fireEvent.blur(autocompleteInput);

		const dropDownListItem = document.querySelector('.dropdown-item');

		fireEvent.click(dropDownListItem);
	});

	it('Select a new assignee with input already filled', async () => {
		cleanup();

		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve({items: [{id: 1, name: 'Test'}]}),
			ok: true,
		});

		render(
			<MockRouter>
				<SingleReassignModal.Table.AssigneeInput
					reassignedTasks={{tasks: [{assigneeId: 20124, id: 39347}]}}
					setAssigneeId={setAssigneeId}
					setReassignedTasks={setReassignMock}
					taskId={39347}
				/>
			</MockRouter>
		);

		const autocompleteInput = document.querySelector('input.form-control');

		fireEvent.change(autocompleteInput, {target: {value: 'Test'}});

		await act(async () => {
			jest.advanceTimersByTime(100);
		});

		fireEvent.blur(autocompleteInput);

		const dropDownListItem = document.querySelector('.dropdown-item');

		fireEvent.click(dropDownListItem);
	});
});
