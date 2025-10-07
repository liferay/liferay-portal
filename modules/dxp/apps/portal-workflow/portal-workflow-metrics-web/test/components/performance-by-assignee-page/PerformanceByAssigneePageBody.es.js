/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import PerformanceByAssigneePage from '../../../src/main/resources/META-INF/resources/js/components/performance-by-assignee-page/PerformanceByAssigneePage.es';
import PromisesResolver from '../../../src/main/resources/META-INF/resources/js/shared/components/promises-resolver/PromisesResolver.es';
import {MockRouter} from '../../mock/MockRouter.es';

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

const wrapper = ({children}) => (
	<MockRouter>
		<PromisesResolver promises={[Promise.resolve()]}>
			{children}
		</PromisesResolver>
	</MockRouter>
);

describe('The performance by assignee page body should', () => {
	let getAllByRole;

	afterEach(cleanup);

	beforeEach(async () => {
		const renderResult = render(
			<PerformanceByAssigneePage.Body
				items={items}
				page="1"
				pageSize="5"
				totalCount={items.length}
			/>,
			{wrapper}
		);

		getAllByRole = renderResult.getAllByRole;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with assignees names', () => {
		const rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('User Test First');
		expect(rows[2]).toHaveTextContent('User Test Second');
		expect(rows[3]).toHaveTextContent('User Test Third');
	});
});

describe('The subcomponents from workload by assignee page body should', () => {
	afterEach(cleanup);

	test('Be rendered with empty view and no content message', async () => {
		const {getByText} = render(
			<PerformanceByAssigneePage.Body items={[]} totalCount={0} />
		);

		const emptyStateMessage = getByText('there-is-no-data-at-the-moment');

		expect(emptyStateMessage).toBeTruthy();
	});

	test('Be rendered with empty view and no results message', async () => {
		const {getByText} = render(
			<PerformanceByAssigneePage.Body
				filtered={true}
				items={[]}
				totalCount={0}
			/>
		);

		const emptyStateMessage = getByText('no-results-were-found');

		expect(emptyStateMessage).toBeTruthy();
	});
});
