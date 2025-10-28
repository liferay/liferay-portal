/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import WorkloadByAssigneePage from '../../../src/main/resources/META-INF/resources/js/components/workload-by-assignee-page/WorkloadByAssigneePage.es';
import {MockRouter} from '../../mock/MockRouter.es';

const items = [
	{
		assignee: {id: 1, name: 'User 1'},
		onTimeTaskCount: 10,
		overdueTaskCount: 5,
		taskCount: 15,
	},
	{
		assignee: {id: 2, image: 'path/to/image.jpg', name: 'User 2'},
		onTimeTaskCount: 3,
		overdueTaskCount: 7,
		taskCount: 10,
	},
];

describe('The workload by assignee page body should', () => {
	let getAllByRole;

	afterEach(cleanup);

	beforeEach(async () => {
		const routeParams = {
			page: '1',
			pageSize: '5',
			processId: '12345',
			sort: 'overdueTaskCount:desc',
		};

		fetch.mockResolvedValue({
			json: () => Promise.resolve({items, totalCount: 2}),
			ok: true,
			text: () => Promise.resolve(),
		});

		const renderResult = render(
			<MockRouter>
				<WorkloadByAssigneePage routeParams={routeParams} />
			</MockRouter>
		);

		getAllByRole = renderResult.getAllByRole;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with "User 1" and "User 2" names', async () => {
		const rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('User 1');
		expect(rows[2]).toHaveTextContent('User 2');
	});
});
