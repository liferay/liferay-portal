/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import PerformanceByStepCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/performance-by-step-card/PerformanceByStepCard.es';
import {stringify} from '../../../../src/main/resources/META-INF/resources/js/shared/components/router/queryString.es';
import {jsonSessionStorage} from '../../../../src/main/resources/META-INF/resources/js/shared/util/storage.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const {filters, processId} = {
	filters: {
		processVersion: '1.0',
		stepDateEnd: '2019-12-09T00:00:00Z',
		stepDateStart: '2019-12-03T00:00:00Z',
		stepTimeRange: ['7'],
	},
	processId: 12345,
};
const items = [
	{
		breachedInstanceCount: 3,
		breachedInstancePercentage: 30,
		durationAvg: 10800000,
		node: {
			name: 'Review',
		},
	},
	{
		breachedInstanceCount: 7,
		breachedInstancePercentage: 22.5806,
		durationAvg: 475200000,
		node: {
			name: 'Update',
		},
	},
	{
		breachedInstanceCount: 0,
		breachedInstancePercentage: 0,
		durationAvg: 0,
		node: {
			name: 'Translate',
		},
	},
];
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
const processVersions = {items: [{name: '1.0'}]};

describe('The performance by step card component should', () => {
	let getAllByText;
	let getByText;

	beforeAll(() => {
		jsonSessionStorage.set('timeRanges', timeRangeData);
	});

	describe('Be rendered with results', () => {
		beforeAll(async () => {
			fetch
				.mockResolvedValueOnce({
					json: () => Promise.resolve(processVersions),
					ok: true,
				})
				.mockResolvedValue({
					json: () =>
						Promise.resolve({items, totalCount: items.length}),
					ok: true,
				});

			const wrapper = ({children}) => (
				<MockRouter query={query}>{children}</MockRouter>
			);

			const renderResult = render(
				<PerformanceByStepCard routeParams={{processId}} />,
				{wrapper}
			);

			getAllByText = renderResult.getAllByText;
			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Be rendered with time range filter', async () => {
			const activeItems = document.querySelectorAll('.active');

			expect(getAllByText('Last 7 Days').length).toEqual(2);
			expect(activeItems[1]).toHaveTextContent('Last 7 Days');
		});

		it('Be rendered with "View All Steps" button and total "(3)"', () => {
			const viewAllSteps = getByText('view-all-steps (3)');

			expect(viewAllSteps).toBeTruthy();
			expect(viewAllSteps.parentNode.getAttribute('href')).toContain(
				'filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7'
			);
		});
	});

	describe('Be rendered without results', () => {
		afterEach(cleanup);

		beforeEach(async () => {
			fetch
				.mockResolvedValueOnce({
					json: () => Promise.resolve(processVersions),
					ok: true,
				})
				.mockResolvedValue({
					json: () => Promise.resolve({items: [], totalCount: 0}),
					ok: true,
				});

			const wrapper = ({children}) => (
				<MockRouter query={query}>{children}</MockRouter>
			);

			const renderResult = render(
				<PerformanceByStepCard routeParams={{processId}} />,
				{wrapper}
			);

			getByText = renderResult.getByText;

			await act(async () => {
				jest.runAllTimers();
			});
		});

		it('Be rendered with empty state view', () => {
			const emptyStateMessage = getByText(
				'there-is-no-data-at-the-moment'
			);

			expect(emptyStateMessage).toBeTruthy();
		});
	});
});
