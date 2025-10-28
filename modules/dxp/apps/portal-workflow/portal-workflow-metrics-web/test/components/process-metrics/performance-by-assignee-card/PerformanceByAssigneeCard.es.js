/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import PerformanceByAssigneeCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/performance-by-assignee-card/PerformanceByAssigneeCard.es';
import {stringify} from '../../../../src/main/resources/META-INF/resources/js/shared/components/router/queryString.es';
import {jsonSessionStorage} from '../../../../src/main/resources/META-INF/resources/js/shared/util/storage.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const {filters, processId} = {
	filters: {
		assigneeDateEnd: '2019-12-09T00:00:00Z',
		assigneeDateStart: '2019-12-03T00:00:00Z',
		assigneeTaskNames: ['update'],
		assigneeTimeRange: ['7'],
	},
	processId: 12345,
};
const items = [
	{
		assignee: {
			image: 'path/to/image',
			name: 'User Test First',
		},
		durationTaskAvg: 10800000,
		taskCount: 10,
	},
	{
		assignee: {
			image: 'path/to/image',
			name: 'User Test Second',
		},
		durationTaskAvg: 475200000,
		taskCount: 31,
	},
	{
		assignee: {
			name: 'User Test Third',
		},
		durationTaskAvg: 0,
		taskCount: 1,
	},
];
const processStepsData = {
	items: [
		{
			label: 'Review',
			name: 'review',
		},
		{
			label: 'Update',
			name: 'update',
		},
	],
	totalCount: 2,
};
const query = stringify({filters});
const timeRangeData = {
	items: [
		{
			dateEnd: '2019-12-09T00:00:00Z',
			dateStart: '2019-12-03T00:00:00Z',
			defaultTimeRange: false,
			id: 7,
			name: 'Last 7 Days',
		},
		{
			dateEnd: '2019-12-09T00:00:00Z',
			dateStart: '2019-11-10T00:00:00Z',
			defaultTimeRange: true,
			id: 30,
			name: 'Last 30 Days',
		},
	],
	totalCount: 2,
};

describe('The performance by assignee card component should', () => {
	let getByText;

	beforeAll(() => {
		jsonSessionStorage.set('timeRanges', timeRangeData);
	});

	describe('Be rendered with results', () => {
		afterEach(cleanup);

		beforeEach(async () => {
			fetch
				.mockResolvedValueOnce({
					json: () =>
						Promise.resolve({items, totalCount: items.length}),
					ok: true,
				})
				.mockResolvedValueOnce({
					json: () => Promise.resolve(processStepsData),
					ok: true,
				});

			const wrapper = ({children}) => (
				<MockRouter query={query}>{children}</MockRouter>
			);

			const renderResult = render(
				<PerformanceByAssigneeCard routeParams={{processId}} />,
				{wrapper}
			);

			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Be rendered with "View All Assignees" button and total "(3)"', () => {
			const viewAllAssignees = getByText('view-all-assignees (3)');

			expect(viewAllAssignees).toBeTruthy();
			expect(viewAllAssignees.parentNode.getAttribute('href')).toContain(
				'filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7&filters.taskNames%5B0%5D=update'
			);
		});

		it('Be rendered with process step filter', async () => {
			const processStepFilter = getByText('all-steps');
			const activeItem = document.querySelectorAll('.active')[0];

			expect(processStepFilter).not.toBeNull();
			expect(activeItem).toHaveTextContent('Update');
		});

		it('Be rendered with time range filter', async () => {
			const timeRangeFilter = getByText('Last 30 Days');
			const activeItem = document.querySelectorAll('.active')[1];

			expect(timeRangeFilter).not.toBeNull();
			expect(activeItem).toHaveTextContent('Last 7 Days');
		});
	});

	describe('Be rendered without results', () => {
		beforeAll(async () => {
			fetch
				.mockResolvedValueOnce({
					json: () => Promise.resolve({items: [], totalCount: 0}),
					ok: true,
				})
				.mockResolvedValueOnce({
					json: () => Promise.resolve(processStepsData),
					ok: true,
				});

			const wrapper = ({children}) => (
				<MockRouter query={query}>{children}</MockRouter>
			);

			render(<PerformanceByAssigneeCard routeParams={{processId}} />, {
				wrapper,
			});

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Be rendered with empty state view', () => {
			const emptyStateMessage = getByText('no-results-were-found');

			expect(emptyStateMessage).toBeTruthy();
		});
	});
});
