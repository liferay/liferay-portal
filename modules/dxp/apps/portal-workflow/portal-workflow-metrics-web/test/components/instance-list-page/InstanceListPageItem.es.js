/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import {InstanceListContext} from '../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
import {Table} from '../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageTable.es';
import {ModalContext} from '../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import {MockRouter} from '../../mock/MockRouter.es';

const instance = {
	assetTitle: 'New Post',
	assetType: 'Blog',
	assignees: [{id: 20124, name: 'Test Test'}],
	creator: {
		name: 'User 1',
	},
	dateCreated: new Date('2019-01-01'),
	id: 1,
	taskNames: ['Review', 'Update'],
};

const setInstanceId = jest.fn();
const openModal = jest.fn();

const ContainerMock = ({children}) => {
	const [, setSelectAll] = useState(false);
	const [selectedItem, setSelectedItem] = useState({
		assetTitle: 'Blog1',
		assetType: 'Blogs Entry',
		assignees: [{id: 2, name: 'Test Test'}],
		id: 1,
		status: 'In Progress',
		taskNames: ['Review', 'Update'],
	});
	const [selectedItems, setSelectedItems] = useState([
		{
			assetTitle: 'Blog1',
			assetType: 'Blogs Entry',
			assignees: [{id: 2, name: 'Test Test'}],
			id: 1,
			status: 'In Progress',
			taskNames: ['Review'],
		},
	]);
	const [singleTransition, setSingleTransition] = useState({});

	return (
		<MockRouter>
			<InstanceListContext.Provider
				value={{
					selectedItem,
					selectedItems,
					setInstanceId,
					setSelectAll,
					setSelectedItem,
					setSelectedItems,
				}}
			>
				<ModalContext.Provider
					value={{
						openModal,
						setSingleTransition,
						singleTransition,
						visibleModal: '',
					}}
				>
					{children}
				</ModalContext.Provider>
			</InstanceListContext.Provider>
		</MockRouter>
	);
};

describe('The instance list item should', () => {
	afterEach(cleanup);

	it('Be rendered with "User 1", "Jan 01, 2019, 12:00 AM", and "Review, Update" columns', () => {
		const {queryByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const creatorCell = queryByText('User 1');
		const dateCreatedCell = queryByText('Jan 01, 2019, 12:00 AM');
		const taskNamesCell = queryByText('Review, Update');

		expect(creatorCell).toBeTruthy();
		expect(dateCreatedCell).toBeTruthy();
		expect(taskNamesCell).toBeTruthy();
	});

	it('Be rendered with check icon when the slaStatus is "OnTime"', () => {
		const {container} = render(
			<table>
				<tbody>
					<Table.Item {...instance} slaStatus="OnTime" />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const instanceStatusIcon = container.querySelector(
			'.lexicon-icon-check-circle'
		);

		expect(instanceStatusIcon).toBeTruthy();
	});

	it('Be rendered with exclamation icon when the slaStatus is "Overdue"', () => {
		const {container} = render(
			<table>
				<tbody>
					<Table.Item {...instance} slaStatus="Overdue" />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const instanceStatusIcon = container.querySelector(
			'.lexicon-icon-exclamation-circle'
		);

		expect(instanceStatusIcon).toBeTruthy();
	});

	it('Be rendered with hr icon and due date when the slaStatus is "Untracked"', () => {
		const {container} = render(
			<table>
				<tbody>
					<Table.Item {...instance} slaStatus="Untracked" />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const instanceStatusIcon = container.querySelector('.lexicon-icon-hr');

		expect(instanceStatusIcon).toBeTruthy();

		const instanceStatus = container.querySelector('.due-date.text-info');

		expect(instanceStatus).toBeTruthy();
		expect(instanceStatus.innerHTML).toEqual('-');
	});

	it('Be rendered with due date success when the slaStatus is "OnTime" and slaResult status is "RUNNING"', () => {
		const slaResult = {
			dateOverdue: '2021-04-16T12:44:25Z',
			name: 'SLA Test',
			onTime: true,
			remainingTime: 99999000,
			status: 'RUNNING',
		};

		const {baseElement, container, queryByText} = render(
			<table>
				<tbody>
					<Table.Item
						{...instance}
						slaResults={[slaResult]}
						slaStatus="OnTime"
					/>
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		act(() => {
			jest.advanceTimersByTime(100);
		});

		const dueDateCol = container.querySelector('.due-date.text-success');

		expect(dueDateCol).toBeTruthy();

		const dueDateBadge = dueDateCol.querySelector('.due-date-badge');

		expect(dueDateBadge).toBeTruthy();

		const dateText = queryByText('Apr 16, 2021');

		expect(dateText).toBeTruthy();

		fireEvent.mouseOver(dateText);

		act(() => {
			jest.advanceTimersByTime(1001);
		});

		let popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeTruthy();

		fireEvent.mouseEnter(popoverElement);

		const slaNamePopoverText = queryByText('SLA Test:');

		expect(slaNamePopoverText).toBeTruthy();

		const slaDateTimeRemainingTime = queryByText(
			'Apr 16, 2021, 12:44 PM (1d 3h 46min left)'
		);

		expect(slaDateTimeRemainingTime).toBeTruthy();

		fireEvent.mouseLeave(popoverElement);
		fireEvent.mouseOut(dateText);

		popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeNull();
	});

	it('Be rendered with due date danger when the slaStatus is "Overdue" and slaResult status is "RUNNING"', () => {
		const slaResult = {
			dateOverdue: '2021-04-16T12:44:25Z',
			name: 'SLA Test',
			onTime: false,
			remainingTime: -99999000,
			status: 'RUNNING',
		};

		const {baseElement, container, queryByText} = render(
			<table>
				<tbody>
					<Table.Item
						{...instance}
						slaResults={[slaResult]}
						slaStatus="Overdue"
					/>
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const dueDateCol = container.querySelector('.due-date.text-danger');

		expect(dueDateCol).toBeTruthy();

		const dueDateBadge = dueDateCol.querySelector('.due-date-badge');

		expect(dueDateBadge).toBeTruthy();

		const dateText = queryByText('Apr 16, 2021');

		expect(dateText).toBeTruthy();

		fireEvent.mouseOver(dateText);

		act(() => {
			jest.advanceTimersByTime(1001);
		});

		let popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeTruthy();

		const slaNamePopoverText = queryByText('SLA Test:');

		expect(slaNamePopoverText).toBeTruthy();

		const slaDateTimeRemainingTime = queryByText(
			'Apr 16, 2021, 12:44 PM (1d 3h 46min overdue)'
		);

		expect(slaDateTimeRemainingTime).toBeTruthy();

		fireEvent.mouseOut(dateText);

		popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeNull();
	});

	it('Be rendered with due date when the year is not the current year', () => {
		const slaResult = {
			dateOverdue: '2020-04-16T12:44:25Z',
			status: 'RUNNING',
		};

		const {queryByText} = render(
			<table>
				<tbody>
					<Table.Item
						{...instance}
						slaResults={[slaResult]}
						slaStatus="Overdue"
					/>
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const dateText = queryByText('Apr 16, 2020');

		expect(dateText).toBeTruthy();
	});

	it('Be rendered with remaining time when the SLA is less than a minute.', async () => {
		const slaResult = {
			dateOverdue: '2021-04-16T12:44:25Z',
			name: 'SLA Test',
			onTime: true,
			remainingTime: 10000,
			status: 'RUNNING',
		};

		const {baseElement, queryByText} = render(
			<table>
				<tbody>
					<Table.Item
						{...instance}
						slaResults={[slaResult]}
						slaStatus="OnTime"
					/>
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const dateText = queryByText('Apr 16, 2021');

		expect(dateText).toBeTruthy();

		fireEvent.mouseOver(dateText);

		act(() => {
			jest.advanceTimersByTime(1001);
		});

		const popoverElement = baseElement.querySelector('.popover');

		expect(popoverElement).toBeTruthy();

		const slaDateTimeRemainingTime = queryByText(
			'Apr 16, 2021, 12:44 PM (10sec left)'
		);

		expect(slaDateTimeRemainingTime).toBeTruthy();
	});

	it('Be rendered with due date when the slaResults is empty', () => {
		const {container} = render(
			<table>
				<tbody>
					<Table.Item
						{...instance}
						slaResults={[]}
						slaStatus="OnTime"
					/>
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const dueDateCol = container.querySelector('.due-date.text-info');

		expect(dueDateCol).toBeTruthy();
		expect(dueDateCol.innerHTML).toEqual('-');
	});

	it('Call setInstanceId with "1" as instance id param', () => {
		instance.status = 'Completed';

		const {container} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const instanceIdLink = container.querySelector('.link-text');

		fireEvent.click(instanceIdLink);

		expect(setInstanceId).toBeCalledWith(1);
	});

	it('set BulkReassign modal visualization by clicking the reassign task button', () => {
		const {getByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const reassignTaskButton = getByText('reassign-task');

		fireEvent.click(reassignTaskButton);

		expect(openModal).toHaveBeenCalled();
	});

	it('set BulkTransition modal visualization by clicking the reassign task button', () => {
		const {getByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const reassignTaskButton = getByText('Transition');

		fireEvent.click(reassignTaskButton);

		expect(openModal).toHaveBeenCalled();
	});
});

describe('The InstanceListPageItem quick action menu should', () => {
	afterEach(cleanup);

	const instance = {
		assetTitle: 'New Post',
		assetType: 'Blog',
		dateCreated: new Date('2019-01-01'),
		id: 1,
		taskNames: ['Review'],
		transitions: [
			{
				label: 'Approve',
				name: 'approve',
			},
			{
				label: 'Reject',
				name: 'reject',
			},
		],
	};

	it('set SingleReassign modal visualization by clicking the reassign task button', () => {
		const {getByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const reassignTaskButton = getByText('reassign-task');

		fireEvent.click(reassignTaskButton);

		expect(openModal).toHaveBeenCalled();
	});

	it('set SingleUpdateDueDate modal visualization by clicking the reassign task button', () => {
		const {getByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const reassignTaskButton = getByText('update-due-date');

		fireEvent.click(reassignTaskButton);

		expect(openModal).toHaveBeenCalled();
	});

	it('set SingleUpdateDueDate modal visualization by clicking the reassign task button', () => {
		const {getByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const reassignTaskButton = getByText('update-due-date');

		fireEvent.click(reassignTaskButton);

		expect(openModal).toHaveBeenCalled();
	});

	it('set SingleUpdateDueDate modal visualization by clicking the reassign task button', () => {
		const {getByText} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const reassignTaskButton = getByText('Approve');

		fireEvent.click(reassignTaskButton);

		expect(openModal).toHaveBeenCalled();
	});
});

describe('The InstanceListPageItem instance checkbox component should', () => {
	afterEach(cleanup);

	const instance = {
		assetTitle: 'New Post',
		assetType: 'Blog',
		dateCreated: new Date('2019-01-01'),
		id: 1,
		taskNames: ['Review'],
	};

	it('Set checkbox value by clicking it', () => {
		const {container} = render(
			<table>
				<tbody>
					<Table.Item {...instance} />
				</tbody>
			</table>,
			{
				wrapper: ContainerMock,
			}
		);

		const instanceCheckbox = container.querySelector(
			'input.custom-control-input'
		);

		expect(instanceCheckbox.checked).toEqual(true);

		fireEvent.click(instanceCheckbox);
		expect(instanceCheckbox.checked).toEqual(false);

		fireEvent.click(instanceCheckbox);
		expect(instanceCheckbox.checked).toEqual(true);
	});
});
