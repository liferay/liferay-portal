/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import WorkloadByAssigneePage from '../../../src/main/resources/META-INF/resources/js/components/workload-by-assignee-page/WorkloadByAssigneePage.es';
import {MockRouter} from '../../mock/MockRouter.es';

describe('The workload by assignee page table should', () => {
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
			assignee: {id: 2, image: 'path/to/image.jpg', name: 'User 2'},
			onTimeTaskCount: 3,
			overdueTaskCount: 7,
			taskCount: 10,
		},
	];

	let rows;

	afterEach(cleanup);

	test('Be rendered with "User 1" and "User 2" names', () => {
		const {getAllByRole} = render(
			<WorkloadByAssigneePage.Body.Table items={items} />,
			{wrapper: MockRouter}
		);

		rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('User 1');
		expect(rows[2]).toHaveTextContent('User 2');
	});

	test('Be rendered with "10" and "3" values as "On Time" column', () => {
		const {getAllByRole} = render(
			<WorkloadByAssigneePage.Body.Table items={items} />,
			{wrapper: MockRouter}
		);

		rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('10');
		expect(rows[2]).toHaveTextContent('3');
	});

	test('Be rendered with "5" and "7" values as "Overdue" column', () => {
		const {getAllByRole} = render(
			<WorkloadByAssigneePage.Body.Table items={items} />,
			{wrapper: MockRouter}
		);

		rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('5');
		expect(rows[2]).toHaveTextContent('7');
	});

	test('Be rendered with "15" and "10" values as "Total Pending" column', () => {
		const {getAllByRole} = render(
			<WorkloadByAssigneePage.Body.Table items={items} />,
			{wrapper: MockRouter}
		);

		rows = getAllByRole('row');

		expect(rows[1]).toHaveTextContent('15');
		expect(rows[2]).toHaveTextContent('10');
	});
});
