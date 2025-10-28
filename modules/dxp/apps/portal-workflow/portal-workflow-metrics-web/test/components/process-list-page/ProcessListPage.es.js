/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import ProcessListPage from '../../../src/main/resources/META-INF/resources/js/components/process-list-page/ProcessListPage.es';
import {MockRouter} from '../../mock/MockRouter.es';

describe('The process list page component having data should', () => {
	let container;

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
	const data = {items, totalCount: items.length};

	fetch.mockResolvedValueOnce({
		json: () => Promise.resolve(data),
		ok: true,
	});

	const routeParams = {
		page: 1,
		pageSize: 20,
		query: '',
		sort: 'overdueInstanceCount%3Adesc',
	};

	afterEach(cleanup);

	const wrapper = ({children}) => <MockRouter>{children}</MockRouter>;

	beforeEach(async () => {
		const renderResult = render(
			<ProcessListPage routeParams={routeParams} />,
			{wrapper}
		);

		container = renderResult.container;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with process names', () => {
		const processName = container.querySelectorAll('.table-title');

		expect(processName[0]).toHaveTextContent('Single Approver 1');
		expect(processName[1]).toHaveTextContent('Single Approver 2');
		expect(processName[2]).toHaveTextContent('Single Approver 3');
	});
});
