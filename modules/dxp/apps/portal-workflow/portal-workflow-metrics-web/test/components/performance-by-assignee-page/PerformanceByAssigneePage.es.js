/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, render} from '@testing-library/react';
import React from 'react';

import PerformanceByAssigneePage from '../../../src/main/resources/META-INF/resources/js/components/performance-by-assignee-page/PerformanceByAssigneePage.es';
import {stringify} from '../../../src/main/resources/META-INF/resources/js/shared/components/router/queryString.es';
import {jsonSessionStorage} from '../../../src/main/resources/META-INF/resources/js/shared/util/storage.es';
import {MockRouter} from '../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const {filters, processId} = {
	filters: {
		stepDateEnd: '2019-12-09T00:00:00Z',
		stepDateStart: '2019-12-03T00:00:00Z',
		stepTimeRange: ['7'],
	},
	processId: 12345,
};

const items = [
	{
		assignee: {image: 'path/to/image', name: 'User Test First'},
		durationTaskAvg: 10800000,
		taskCount: 10,
	},
	{
		assignee: {image: 'path/to/image', name: 'User Test Second'},
		durationTaskAvg: 475200000,
		taskCount: 31,
	},
	{
		assignee: {name: 'User Test Third'},
		durationTaskAvg: 0,
		taskCount: 1,
	},
];

const data = {items, totalCount: items.length};
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

describe('The PerformanceByAssigneePage component having data should', () => {
	let getAllByRole;
	let rows;

	beforeAll(async () => {
		jsonSessionStorage.set('timeRanges', timeRangeData);

		const wrapper = ({children}) => (
			<MockRouter query={query}>{children}</MockRouter>
		);

		fetch.mockResolvedValue({
			json: () => Promise.resolve(data),
			ok: true,
			text: () => Promise.resolve(),
		});

		const renderResult = render(
			<PerformanceByAssigneePage routeParams={{processId}} />,
			{wrapper}
		);

		getAllByRole = renderResult.getAllByRole;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with user avatar or lexicon user icon', async () => {
		rows = getAllByRole('row');

		const assigneeProfileInfo1 = rows[1].children[0].children[0];
		const assigneeProfileInfo2 = rows[1].children[0].children[0];
		const assigneeProfileInfo3 = rows[3].children[0].children[0];

		expect(assigneeProfileInfo1.children[0].innerHTML).toContain(
			'path/to/image'
		);
		expect(assigneeProfileInfo2.children[0].innerHTML).toContain(
			'path/to/image'
		);
		expect(assigneeProfileInfo3.children[0].innerHTML).toContain(
			'lexicon-icon-user'
		);
	});

	it('Be rendered with assignee names', async () => {
		expect(rows[1]).toHaveTextContent('User Test First');
		expect(rows[2]).toHaveTextContent('User Test Second');
		expect(rows[3]).toHaveTextContent('User Test Third');
	});

	it('Be rendered with average completion time', () => {
		expect(rows[1]).toHaveTextContent('3h');
		expect(rows[2]).toHaveTextContent('5d 12h');
		expect(rows[3]).toHaveTextContent('0min');
	});
});
