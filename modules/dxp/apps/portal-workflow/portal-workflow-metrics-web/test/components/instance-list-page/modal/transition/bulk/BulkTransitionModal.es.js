/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, fireEvent, render} from '@testing-library/react';
import React, {useState} from 'react';

import {InstanceListContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/InstanceListPageProvider.es';
import {ModalContext} from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/ModalProvider.es';
import BulkTransitionModal from '../../../../../../src/main/resources/META-INF/resources/js/components/instance-list-page/modal/transition/bulk/BulkTransitionModal.es';
import ToasterProvider from '../../../../../../src/main/resources/META-INF/resources/js/shared/components/toaster/ToasterProvider.es';
import {MockRouter} from '../../../../../mock/MockRouter.es';
import FetchMock, {fetchMockResponse} from '../../../../../mock/fetch.es';

import '@testing-library/jest-dom';

const tasks = (range) =>
	new Array(range).fill({}).map((_, id) => ({
		assetTitle: `title${id + 1}`,
		assetType: 'Blogs Entry',
		assignee: {
			additionalName: '',
			contentType: 'UserAccount',
			familyName: 'Test',
			givenName: 'Test',
			id: 20127,
			name: 'Test Test',
			profileURL: '/web/test',
		},
		assigneeRoles: [],
		classPK: id + 1,
		completed: false,
		dateCreated: '2020-03-03T12:04:46Z',
		description: '',
		id: id + 1,
		instanceId: id + 1,
		label: 'Review',
		name: 'review',
		processId: id + 1,
		processVersion: '1.0',
	}));

const updateTaskItem = {...tasks(1)[0], label: 'Update', name: 'update'};

const {data, items, processSteps} = {
	data: {
		workflowTaskTransitions: [
			{
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
				workflowDefinitionVersion: '1',
				workflowTaskLabel: 'Review',
				workflowTaskName: 'review',
			},
			{
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
				workflowDefinitionVersion: '1',
				workflowTaskLabel: 'Update',
				workflowTaskName: 'update',
			},
		],
	},
	items: [...tasks(13), updateTaskItem],
	lastPage: 9,
	page: 1,
	pageSize: 5,
	processSteps: [
		{key: 'review', name: 'Review'},
		{key: 'update', name: 'Update'},
	],
	totalCount: 45,
};

const mockTasks = {items, totalCount: items.length + 1};

const fetchMock = new FetchMock({
	GET: {
		default: fetchMockResponse({items: processSteps}),
	},
	PATCH: {
		default: [fetchMockResponse({}, false), fetchMockResponse()],
	},
	POST: {
		default: [
			fetchMockResponse(new Error('Request failed 1'), false),
			fetchMockResponse(new Error('Request failed 1'), false),
			fetchMockResponse(new Error('Request failed 1'), false),
			fetchMockResponse(mockTasks),
			fetchMockResponse(mockTasks),
			fetchMockResponse(new Error('Request failed 2'), false),
			fetchMockResponse(new Error('Request failed 2'), false),
			fetchMockResponse(mockTasks),
			fetchMockResponse(data),
		],
	},
});

const ContainerMock = ({children}) => {
	const processId = '12345';

	// eslint-disable-next-line react-hooks/rules-of-hooks
	const [bulkTransition, setBulkTransition] = useState({
		transition: {errors: {}, onGoing: false},
		transitionTasks: [],
	});

	// eslint-disable-next-line react-hooks/rules-of-hooks
	const [selectedItems, setSelectedItems] = useState([
		{
			assetTitle: 'Blog1',
			assetType: 'Blogs Entry',
			assignees: [{id: 2, name: 'Test Test'}],
			id: 1,
			status: 'In Progress',
			taskNames: ['Review'],
		},
		{
			assetTitle: 'Blog2',
			assetType: 'Blogs Entry',
			assignees: [{id: 2, name: 'Test Test'}],
			id: 2,
			status: 'In Progress',
			taskNames: ['Review'],
		},
	]);

	// eslint-disable-next-line react-hooks/rules-of-hooks
	const [selectTasks, setSelectTasks] = useState({
		selectAll: false,
		tasks: [],
	});

	return (
		<MockRouter>
			<InstanceListContext.Provider
				value={{
					selectedItems,
					setSelectedItems,
				}}
			>
				<ModalContext.Provider
					value={{
						bulkTransition,
						processId,
						selectTasks,
						setBulkTransition,
						setSelectTasks,
						visibleModal: 'bulkTransition',
					}}
				>
					<ToasterProvider>{children}</ToasterProvider>
				</ModalContext.Provider>
			</InstanceListContext.Provider>
		</MockRouter>
	);
};

describe('The BulkTransitionModal component should', () => {
	let getAllByText;
	let getByText;

	beforeAll(async () => {
		const component = render(<BulkTransitionModal />, {
			wrapper: ContainerMock,
		});

		getAllByText = component.getAllByText;
		getByText = component.getByText;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	beforeEach(() => {
		fetchMock.mock();
	});

	afterEach(() => {
		fetchMock.reset();
	});

	xit('Render "Select tasks" step with fetch error and retrying', async () => {
		const alertError = getByText('your-request-has-failed');
		const emptyStateMessage = getByText('unable-to-retrieve-data');
		const retryButton = getByText('retry');

		expect(alertError).toBeTruthy();
		expect(emptyStateMessage).toBeTruthy();

		fireEvent.click(retryButton);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render "Select tasks" step with items', async () => {
		const cancelBtn = getByText('cancel');
		const checkAllButton = document.querySelectorAll(
			'.custom-control-input'
		)[0];
		const modal = document.querySelector('.modal');
		const nextBtn = getByText('next');
		const checkboxes = document.querySelectorAll(
			'input.custom-control-input'
		);
		const stepBar = document.querySelector('.step-of-bar');
		const table = document.querySelector('.table');

		const content = modal.children[0].children[0];
		const checkbox1 = checkboxes[1];
		const checkbox2 = checkboxes[2];

		const header = content.children[0];

		expect(header).toHaveTextContent('select-steps-to-transition');
		expect(stepBar.children[0]).toHaveTextContent('select-steps');
		expect(stepBar.children[1]).toHaveTextContent('step-x-of-x');
		expect(getAllByText('process-step').length).toBe(2);
		expect(cancelBtn).toHaveTextContent('cancel');
		expect(nextBtn).toHaveAttribute('disabled');

		const items = table.children[1].children;

		expect(checkbox1.checked).toBe(false);
		expect(items[0].children[1]).toHaveTextContent('1');
		expect(items[0].children[2]).toHaveTextContent('Blogs Entry: title1');
		expect(items[0].children[3]).toHaveTextContent('Review');
		expect(items[0].children[4]).toHaveTextContent('Test Test');

		expect(checkbox2.checked).toBe(false);
		expect(items[1].children[1]).toHaveTextContent('2');
		expect(items[1].children[2]).toHaveTextContent('Blogs Entry: title2');
		expect(items[1].children[3]).toHaveTextContent('Review');
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

		await act(async () => {
			jest.runAllTimers();
		});

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

		await act(async () => {
			jest.runAllTimers();
		});

		expect(checkbox1.checked).toBe(true);
		expect(checkbox2.checked).toBe(true);
		expect(checkAllButton.checked).toBe(true);
		expect(label).toHaveTextContent('x-of-x-selected');
		expect(nextBtn).not.toBeDisabled();

		const selectAllButton = getByText('select-all');

		fireEvent.click(selectAllButton);

		label = getByText('all-selected');

		expect(label).toBeTruthy();
		expect(nextBtn).not.toBeDisabled();

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Render "Select transitions" step failing the fetch and retry then', async () => {
		const alertError = getByText('your-request-has-failed');
		const nextButton = getByText('next');

		expect(alertError).toBeTruthy();

		fireEvent.click(nextButton);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('Load the second step and all transitions successfully', async () => {
		const modal = document.querySelector('.modal');
		const stepBar = document.querySelector('.step-of-bar');

		const content = modal.children[0].children[0];

		const header = content.children[0];

		expect(header).toHaveTextContent('choose-transition-per-step');
		expect(stepBar.children[0]).toHaveTextContent('choose-transition');
		expect(stepBar.children[1]).toHaveTextContent('step-x-of-x');
	});

	xit('Show alert message when attempt to transition without selecting any transition go to previous step and forward', async () => {
		const nextBtn = getByText('done');

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});

		const alertError = getByText(
			'all-steps-require-a-transition-to-be-selected-to-complete-this-action'
		);

		expect(alertError).toBeTruthy();

		const modal = document.querySelector('.modal');
		const content = modal.children[0].children[0];

		const header = content.children[0];
		const previousButton = getByText('previous');

		fireEvent.click(previousButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(header).toHaveTextContent('select-steps-to-transition');

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});
	});

	xit('render a panel for each task names', () => {
		const taskNamesPanel = document.querySelectorAll('.panel-header > h4');

		expect(taskNamesPanel[0]).toHaveTextContent('Review');
		expect(taskNamesPanel[1]).toHaveTextContent('Update');
	});

	xit('Select a transition to "approve" and fail the patch request and retry then', async () => {
		const nextBtn = getByText('done');
		const reviewTransitionSelect = document.querySelector(
			'#transitionSelect0_0'
		);
		const updateTransitionSelect = document.querySelector(
			'#transitionSelect0_1'
		);

		fireEvent.change(reviewTransitionSelect, {target: {value: 'approve'}});
		fireEvent.change(updateTransitionSelect, {target: {value: 'approve'}});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(reviewTransitionSelect.value).toEqual('approve');

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});

		const alertError = getByText(
			'your-request-has-failed select-done-to-retry'
		);

		expect(alertError).toBeTruthy();
	});

	xit('Click "Show All" button, add a comment and retry patch request successfully', async () => {
		const addCommentButton = getAllByText('add-comment')[0];
		const nextBtn = getByText('done');
		const showAllButton = getByText('show-all');

		fireEvent.click(showAllButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(showAllButton).toHaveTextContent('show-less');

		fireEvent.click(addCommentButton);

		await act(async () => {
			jest.runAllTimers();
		});

		expect(document.querySelector('.panel-footer')).toHaveTextContent(
			'comment (optional)'
		);

		const commentField = document.querySelector('#commentTextArea');

		fireEvent.change(commentField, {
			target: {value: 'Transition comment text'},
		});

		await act(async () => {
			jest.runAllTimers();
		});

		expect(commentField.value).toEqual('Transition comment text');

		fireEvent.click(nextBtn);

		await act(async () => {
			jest.runAllTimers();
		});

		const alertToast = document.querySelectorAll('.alert-dismissible');

		expect(alertToast[0]).toHaveTextContent(
			'success:the-selected-steps-have-transitioned-successfully'
		);
	});
});
