/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import {AppContext} from '../../../../src/main/resources/META-INF/resources/js/components/AppContext.es';
import WorkloadByAssigneeCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/workload-by-assignee-card/WorkloadByAssigneeCard.es';
import {MockRouter} from '../../../mock/MockRouter.es';

const items = [
	{
		assignee: {
			id: 1,
			name: 'User 1',
		},
		onTimeTaskCount: 10,
		overdueTaskCount: 5,
		taskCount: 15,
	},
	{
		assignee: {
			id: 2,
			name: 'User 2',
		},
		onTimeTaskCount: 3,
		overdueTaskCount: 7,
		taskCount: 10,
	},
];
const data = {items, totalCount: 2};

const wrapper = ({children}) => (
	<AppContext.Provider value={{defaultDelta: 20}}>
		<MockRouter>{children}</MockRouter>
	</AppContext.Provider>
);

describe('The workload by assignee body should', () => {
	let getByText;

	afterEach(cleanup);

	describe('be rendered with overdue tab active', () => {
		beforeEach(() => {
			const renderResult = render(
				<WorkloadByAssigneeCard.Body
					currentTab="onTime"
					{...data}
					processId={12345}
				/>,
				{wrapper}
			);

			getByText = renderResult.getByText;
		});

		test('Be rendered with "User 1" and "User 2" items', () => {
			const assigneeName1 = getByText('User 1');
			const assigneeName2 = getByText('User 2');

			expect(assigneeName1).toBeTruthy();
			expect(assigneeName2).toBeTruthy();

			expect(assigneeName1.parentNode.getAttribute('href')).toContain(
				'&filters.assigneeIds%5B0%5D=1&filters.statuses%5B0%5D=Pending&filters.slaStatuses%5B0%5D=OnTime'
			);
			expect(assigneeName2.parentNode.getAttribute('href')).toContain(
				'&filters.assigneeIds%5B0%5D=2&filters.statuses%5B0%5D=Pending&filters.slaStatuses%5B0%5D=OnTime'
			);
		});

		test('Be rendered with "View All Steps" button and total "(2)"', () => {
			const viewAllAssignees = getByText('view-all-assignees (2)');

			expect(viewAllAssignees).toBeTruthy();
			expect(
				viewAllAssignees.parentNode.getAttribute('href')
			).not.toContain('filters.taskNames%5B0%5D=allSteps');
		});
	});

	describe('be rendered with total tab active', () => {
		beforeEach(() => {
			const renderResult = render(
				<WorkloadByAssigneeCard.Body
					currentTab="total"
					items={[items[0]]}
					processId={12345}
					processStepKey="review"
					totalCount={1}
				/>,
				{wrapper}
			);

			getByText = renderResult.getByText;
		});

		test('and with "User 1" item', () => {
			const assigneeName = getByText('User 1');

			expect(assigneeName).toBeTruthy();
			expect(assigneeName.parentNode.getAttribute('href')).toContain(
				'&filters.assigneeIds%5B0%5D=1&filters.statuses%5B0%5D=Pending&filters.taskNames%5B0%5D=review'
			);
		});

		test('and with "View All Steps" button and total "(1)"', () => {
			const viewAllAssignees = getByText('view-all-assignees (1)');

			expect(viewAllAssignees).toBeTruthy();
			expect(viewAllAssignees.parentNode.getAttribute('href')).toContain(
				'filters.taskNames%5B0%5D=review'
			);
		});
	});

	describe('be rendered with onTime tab active', () => {
		beforeEach(() => {
			const renderResult = render(
				<WorkloadByAssigneeCard.Body
					currentTab="onTime"
					items={[items[1]]}
					processId={12345}
					processStepKey="update"
					totalCount={1}
				/>,
				{wrapper}
			);

			getByText = renderResult.getByText;
		});

		test('and with "User 2" item', () => {
			const assigneeName = getByText('User 2');

			expect(assigneeName).toBeTruthy();
			expect(assigneeName.parentNode.getAttribute('href')).toContain(
				'&filters.assigneeIds%5B0%5D=2&filters.statuses%5B0%5D=Pending&filters.taskNames%5B0%5D=update&filters.slaStatuses%5B0%5D=OnTime'
			);
		});

		test('and with "View All Steps" button and total "(1)"', () => {
			const viewAllAssignees = getByText('view-all-assignees (1)');

			expect(viewAllAssignees).toBeTruthy();
			expect(viewAllAssignees.parentNode.getAttribute('href')).toContain(
				'filters.taskNames%5B0%5D=update'
			);
		});
	});

	describe('be rendered with overdue tab active and empty state', () => {
		afterAll(cleanup);

		beforeAll(() => {
			const renderResult = render(
				<WorkloadByAssigneeCard.Body
					currentTab="overdue"
					items={[]}
					processId={12345}
					totalCount={0}
				/>,
				{wrapper}
			);

			getByText = renderResult.getByText;
		});

		test('Be rendered with a empty state', () => {
			expect(
				getByText('there-are-no-assigned-items-overdue-at-the-moment')
			).toBeTruthy();
		});
	});

	describe('be rendered with total tab active and empty state', () => {
		afterAll(cleanup);

		beforeAll(() => {
			const renderResult = render(
				<WorkloadByAssigneeCard.Body
					items={[]}
					processId={12345}
					totalCount={0}
				/>,
				{wrapper}
			);

			getByText = renderResult.getByText;
		});

		test('Be rendered with a empty state', () => {
			expect(
				getByText('there-are-no-items-assigned-to-users-at-the-moment')
			).toBeTruthy();
		});
	});

	describe('be rendered with onTime tab active and empty state', () => {
		afterAll(cleanup);

		beforeAll(() => {
			const renderResult = render(
				<WorkloadByAssigneeCard.Body
					currentTab="onTime"
					items={[]}
					processId={12345}
					totalCount={0}
				/>,
				{wrapper}
			);

			getByText = renderResult.getByText;
		});

		test('Be rendered with a empty state', () => {
			expect(
				getByText('there-are-no-assigned-items-on-time-at-the-moment')
			).toBeTruthy();
		});
	});
});
