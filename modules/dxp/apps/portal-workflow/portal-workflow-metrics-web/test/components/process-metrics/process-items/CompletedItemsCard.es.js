/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

// import {act, cleanup, render} from '@testing-library/react';
// import React from 'react';

// import CompletedItemsCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/process-items/CompletedItemsCard.es';
// import {stringify} from '../../../../src/main/resources/META-INF/resources/js/shared/components/router/queryString.es';
// import {jsonSessionStorage} from '../../../../src/main/resources/META-INF/resources/js/shared/util/storage.es';
// import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

// const {filters, processId} = {
// 	filters: {
// 		completedDateEnd: '2019-12-09T00:00:00Z',
// 		completedDateStart: '2019-12-03T00:00:00Z',
// 		completedTimeRange: ['7'],
// 	},
// 	processId: 12345,
// };
// const data = {
// 	instanceCount: 6,
// 	onTimeInstanceCount: 2,
// 	overdueInstanceCount: 1,
// 	process: {
// 		id: 38803,
// 		title: 'Single Approver',
// 	},
// 	untrackedInstanceCount: 3,
// };
// const query = stringify({filters});
// const timeRangeData = {
// 	items: [
// 		{
// 			dateEnd: '2019-12-09T00:00:00Z',
// 			dateStart: '2019-12-03T00:00:00Z',
// 			defaultTimeRange: false,
// 			id: 7,
// 			name: 'Last 7 Days',
// 		},
// 		{
// 			dateEnd: '2019-12-09T00:00:00Z',
// 			dateStart: '2019-11-10T00:00:00Z',
// 			defaultTimeRange: true,
// 			id: 30,
// 			name: 'Last 30 Days',
// 		},
// 	],
// 	totalCount: 2,
// };

describe('The completed items card component should', () => {
	let container;
	let getAllByText;

	// afterEach(cleanup);

	// beforeAll(() => {
	// 	jsonSessionStorage.set('timeRanges', timeRangeData);
	// });

	// beforeEach(async () => {
	// 	const clientMock = {
	// 		get: jest.fn().mockResolvedValue({data}),
	// 	};

	// 	const wrapper = ({children}) => (
	// 		<MockRouter client={clientMock} query={query}>
	// 			{children}
	// 		</MockRouter>
	// 	);

	// 	const renderResult = render(
	// 		<CompletedItemsCard routeParams={{processId}} />,
	// 		{wrapper}
	// 	);

	// 	container = renderResult.container;
	// 	getAllByText = renderResult.getAllByText;

	// 	await act(async () => {
	// 		jest.runAllTimers();
	// 	});
	// });

	xit('Be rendered with time range filter', async () => {
		const activeItem = document.querySelector('.active');

		expect(getAllByText('Last 7 Days').length).toEqual(2);
		expect(activeItem).toHaveTextContent('Last 7 Days');
	});

	xit('Be rendered with overdue count "1"', () => {
		const panelBody = container.querySelector('.panel-body');

		const overdueLink = panelBody.children[0].children[0];
		const overdueHeader = overdueLink.children[0].children[0];
		const overdueBody = overdueLink.children[0].children[1];
		const overdueFooter = overdueLink.children[0].children[2].children[0];

		expect(overdueHeader).toHaveTextContent('overdue');
		expect(overdueBody).toHaveTextContent('1');
		expect(overdueFooter).toHaveTextContent('16.67%');
		expect(overdueLink.getAttribute('href')).toContain(
			'filters.statuses%5B0%5D=Completed&filters.slaStatuses%5B0%5D=Overdue&filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7'
		);
	});

	xit('Be rendered with onTime count "2"', () => {
		const panelBody = container.querySelector('.panel-body');

		const onTimeLink = panelBody.children[0].children[1];
		const onTimeHeader = onTimeLink.children[0].children[0];
		const onTimeBody = onTimeLink.children[0].children[1];
		const onTimeFooter = onTimeLink.children[0].children[2].children[0];

		expect(onTimeHeader).toHaveTextContent('on-time');
		expect(onTimeBody).toHaveTextContent('2');
		expect(onTimeFooter).toHaveTextContent('33.33%');
		expect(onTimeLink.getAttribute('href')).toContain(
			'filters.statuses%5B0%5D=Completed&filters.slaStatuses%5B0%5D=OnTime&filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7'
		);
	});

	xit('Be rendered with untracked count "3"', () => {
		const panelBody = container.querySelector('.panel-body');

		const untrackedLink = panelBody.children[0].children[2];
		const untrackedHeader = untrackedLink.children[0].children[0];
		const untrackedBody = untrackedLink.children[0].children[1];
		const untrackedFooter =
			untrackedLink.children[0].children[2].children[0];

		expect(untrackedHeader).toHaveTextContent('untracked');
		expect(untrackedBody).toHaveTextContent('3');
		expect(untrackedFooter).toHaveTextContent('50%');
		expect(untrackedLink.getAttribute('href')).toContain(
			'filters.statuses%5B0%5D=Completed&filters.slaStatuses%5B0%5D=Untracked&filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7'
		);
	});

	xit('Be rendered with total completed count "6"', () => {
		const panelBody = container.querySelector('.panel-body');

		const totalLink = panelBody.children[0].children[3];
		const totalHeader = totalLink.children[0].children[0];
		const totalBody = totalLink.children[0].children[1];

		expect(totalHeader).toHaveTextContent('total-completed');
		expect(totalBody).toHaveTextContent('6');
		expect(totalLink.getAttribute('href')).toContain(
			'filters.statuses%5B0%5D=Completed&filters.dateEnd=2019-12-09T00%3A00%3A00Z&filters.dateStart=2019-12-03T00%3A00%3A00Z&filters.timeRange%5B0%5D=7'
		);
	});
});
