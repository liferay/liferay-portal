/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import PerformanceByStepPage from '../../../src/main/resources/META-INF/resources/js/components/performance-by-step-page/PerformanceByStepPage.es';
import PromisesResolver from '../../../src/main/resources/META-INF/resources/js/shared/components/promises-resolver/PromisesResolver.es';
import {MockRouter} from '../../mock/MockRouter.es';

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

const wrapper = ({children}) => (
	<MockRouter>
		<PromisesResolver promises={[Promise.resolve()]}>
			{children}
		</PromisesResolver>
	</MockRouter>
);

describe('The performance by step page body should', () => {
	let getAllByRole;

	afterEach(cleanup);

	beforeEach(async () => {
		const renderResult = render(
			<PerformanceByStepPage.Body
				filtered={false}
				items={items}
				totalCount={items.length}
			/>,
			{wrapper}
		);

		getAllByRole = renderResult.getAllByRole;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with step names', () => {
		const rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('Review');
		expect(rows[2]).toHaveTextContent('Update');
	});
});

describe('The subcomponents from workload by assignee page body should', () => {
	afterEach(cleanup);

	it('Be rendered with empty view and no content message', async () => {
		const {getByText} = render(
			<PerformanceByStepPage.Body items={[]} totalCount={0} />
		);

		const emptyStateMessage = getByText('there-is-no-data-at-the-moment');

		expect(emptyStateMessage).toBeTruthy();
	});

	it('Be rendered with empty view and no results message', async () => {
		const {getByText} = render(
			<PerformanceByStepPage.Body filtered items={[]} totalCount={0} />
		);

		const emptyStateMessage = getByText('no-results-were-found');

		expect(emptyStateMessage).toBeTruthy();
	});
});
