/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import WorkloadByStepCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/workload-by-step-card/WorkloadByStepCard.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const mockProps = {
	page: 1,
	pageSize: 10,
	processId: '12345',
	sort: 'overdueTaskCount:desc',
};

describe('The WorkloadByStepCard component should', () => {
	let container;

	afterAll(cleanup);

	const data = {
		items: [
			{
				instanceCount: 1,
				node: {
					label: 'Node Name',
				},
				onTimeInstanceCount: 1,
				overdueInstanceCount: 0,
			},
		],
		totalCount: 1,
	};

	beforeAll(async () => {
		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve(data),
			ok: true,
		});

		const renderResult = render(
			<MockRouter withoutRouterProps>
				<WorkloadByStepCard {...mockProps} routeParams={mockProps} />
			</MockRouter>
		);

		container = renderResult.container;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Load table component with request data and navigation links', () => {
		const workloadByStepTable = container.querySelector('.table');
		const tableItems = workloadByStepTable.children[1].children[0].children;

		expect(tableItems[0]).toHaveTextContent('Node Name');
		expect(tableItems[1]).toHaveTextContent(0);
		expect(tableItems[2]).toHaveTextContent(1);
		expect(tableItems[3]).toHaveTextContent(1);

		expect(tableItems[1].innerHTML).toContain(
			'/instance/12345/20/1/dateOverdue:asc?backPath=%2F1%2F20%2Ftitle%253Aasc%3FbackPath%3D%252F&amp;filters.statuses%5B0%5D=Pending&amp;filters.slaStatuses%5B0%5D=Overdue'
		);
		expect(tableItems[2].innerHTML).toContain(
			'/instance/12345/20/1/dateOverdue:asc?backPath=%2F1%2F20%2Ftitle%253Aasc%3FbackPath%3D%252F&amp;filters.statuses%5B0%5D=Pending&amp;filters.slaStatuses%5B0%5D=OnTime'
		);
		expect(tableItems[3].innerHTML).toContain(
			'/instance/12345/20/1/dateOverdue:asc?backPath=%2F1%2F20%2Ftitle%253Aasc%3FbackPath%3D%252F&amp;filters.statuses%5B0%5D=Pending'
		);
	});
});
