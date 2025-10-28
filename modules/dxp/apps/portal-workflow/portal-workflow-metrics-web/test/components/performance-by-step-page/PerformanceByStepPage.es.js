/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render} from '@testing-library/react';
import React from 'react';

import PerformanceByStepPage from '../../../src/main/resources/META-INF/resources/js/components/performance-by-step-page/PerformanceByStepPage.es';
import {jsonSessionStorage} from '../../../src/main/resources/META-INF/resources/js/shared/util/storage.es';
import {MockRouter} from '../../mock/MockRouter.es';

import '@testing-library/jest-dom';

describe('The PerformanceByStepPage component having data should', () => {
	let getAllByRole;
	let rows;

	const items = [
		{
			breachedInstanceCount: 4,
			durationAvg: 10800000,
			instanceCount: 4,
			node: {key: 'review', label: 'Review', name: 'Review'},
			onTimeInstanceCount: 4,
			overdueInstanceCount: 4,
		},
		{
			breachedInstanceCount: 2,
			durationAvg: 475200000,
			instanceCount: 2,
			node: {key: 'update', label: 'Update', name: 'Update'},
			onTimeInstanceCount: 2,
			overdueInstanceCount: 2,
		},
	];

	const data = {items, totalCount: items.length};

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

	const wrapper = ({children}) => <MockRouter>{children}</MockRouter>;

	beforeAll(async () => {
		jsonSessionStorage.set('timeRanges', timeRangeData);

		fetch.mockResolvedValue({
			json: () => Promise.resolve(data),
			ok: true,
			text: () => Promise.resolve(),
		});

		const renderResult = render(
			<PerformanceByStepPage routeParams={{processId: '1234'}} />,
			{wrapper}
		);

		getAllByRole = renderResult.getAllByRole;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with step names', async () => {
		rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('Review');
		expect(rows[2]).toHaveTextContent('Update');
	});

	it('Be rendered with SLA Breached (%)', () => {
		expect(rows[1]).toHaveTextContent('4 (0%)');
		expect(rows[2]).toHaveTextContent('2 (0%)');
	});

	it('Be rendered with average completion time', () => {
		expect(rows[1]).toHaveTextContent('3h');
		expect(rows[2]).toHaveTextContent('5d 12h');
	});
});
