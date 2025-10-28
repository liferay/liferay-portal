/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import {AppContext} from '../../../../src/main/resources/META-INF/resources/js/components/AppContext.es';
import PerformanceByAssigneeCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/performance-by-assignee-card/PerformanceByAssigneeCard.es';
import {MockRouter} from '../../../mock/MockRouter.es';

const wrapper = ({children}) => (
	<AppContext.Provider value={{defaultDelta: 20}}>
		<MockRouter>{children}</MockRouter>
	</AppContext.Provider>
);

describe('The performance by assignee body component with data should', () => {
	let container;

	const items = [
		{
			assignee: {
				image: 'path/to/image',
				name: 'User Test First',
			},
			durationTaskAvg: 10800000,
			taskCount: 10,
		},
		{
			assignee: {
				image: 'path/to/image',
				name: 'User Test Second',
			},
			durationTaskAvg: 475200000,
			taskCount: 31,
		},
		{
			assignee: {
				name: 'User Test Third',
			},
			durationTaskAvg: 0,
			taskCount: 1,
		},
	];
	const data = {items, totalCount: items.length};

	afterEach(cleanup);

	beforeEach(async () => {
		const renderResult = render(
			<PerformanceByAssigneeCard.Body {...data} />,
			{wrapper}
		);

		container = renderResult.container;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Be rendered with user avatar or lexicon user icon', () => {
		const assigneeProfileInfo =
			container.querySelectorAll('.assignee-name');

		expect(assigneeProfileInfo[0].children[0].innerHTML).toContain(
			'path/to/image'
		);
		expect(assigneeProfileInfo[1].children[0].innerHTML).toContain(
			'path/to/image'
		);
		expect(assigneeProfileInfo[2].children[0].innerHTML).toContain(
			'lexicon-icon-user'
		);
	});

	it('Be rendered with assignee name', () => {
		const assigneeName = container.querySelectorAll('.assignee-name');

		expect(assigneeName[0]).toHaveTextContent('User Test First');
		expect(assigneeName[1]).toHaveTextContent('User Test Second');
		expect(assigneeName[2]).toHaveTextContent('User Test Third');
	});

	it('Be rendered with average completion time', () => {
		const durations = container.querySelectorAll('.task-count-value');

		expect(durations[1].innerHTML).toEqual('3h');
		expect(durations[3].innerHTML).toEqual('5d 12h');
		expect(durations[5].innerHTML).toEqual('0min');
	});
});

describe('The performance by assignee body component without data should', () => {
	let getByText;

	const items = [];

	const data = {items, totalCount: 0};

	afterEach(cleanup);

	beforeEach(async () => {
		const renderResult = render(
			<PerformanceByAssigneeCard.Body {...data} />,
			{wrapper}
		);

		getByText = renderResult.getByText;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	it('Render empty state', () => {
		const emptyStateMessage = getByText('there-is-no-data-at-the-moment');

		expect(emptyStateMessage).toBeTruthy();
	});
});
