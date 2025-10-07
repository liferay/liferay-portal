/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import ProcessListPage from '../../../src/main/resources/META-INF/resources/js/components/process-list-page/ProcessListPage.es';
import {MockRouter} from '../../mock/MockRouter.es';

const items = [
	{
		instancesCount: 0,
		process: {
			title: 'Single Approver 1',
		},
	},
	{
		instancesCount: 0,
		process: {
			title: 'Single Approver 2',
		},
	},
	{
		instancesCount: 0,
		process: {
			title: 'Single Approver 3',
		},
	},
];

describe('The performance by assignee page body should', () => {
	let container;

	afterEach(cleanup);

	beforeEach(() => {
		const renderResult = render(
			<MockRouter>
				<ProcessListPage.Body
					items={items}
					page="1"
					pageSize="5"
					totalCount={items.length}
				/>
			</MockRouter>
		);

		container = renderResult.container;
	});

	test('Be rendered with process names', () => {
		const processName = container.querySelectorAll('.table-title');

		expect(processName[0]).toHaveTextContent('Single Approver 1');
		expect(processName[1]).toHaveTextContent('Single Approver 2');
		expect(processName[2]).toHaveTextContent('Single Approver 3');
	});
});
