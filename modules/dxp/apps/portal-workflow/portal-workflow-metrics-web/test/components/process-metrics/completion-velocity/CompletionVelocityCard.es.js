/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {act, cleanup, render} from '@testing-library/react';
import React from 'react';

import CompletionVelocityCard from '../../../../src/main/resources/META-INF/resources/js/components/process-metrics/completion-velocity/CompletionVelocityCard.es';
import {stringify} from '../../../../src/main/resources/META-INF/resources/js/shared/components/router/queryString.es';
import {jsonSessionStorage} from '../../../../src/main/resources/META-INF/resources/js/shared/util/storage.es';
import {MockRouter} from '../../../mock/MockRouter.es';

import '@testing-library/jest-dom';

const {filters, processId} = {
	filters: {
		completionDateEnd: '2019-12-09T00:00:00Z',
		completionDateStart: '2019-12-03T00:00:00Z',
		completionTimeRange: ['7'],
		completionVelocityUnit: ['Days'],
	},
	processId: 12345,
};
const data = {
	histograms: [
		{
			key: '2019-12-03T00:00',
			value: 0.0,
		},
		{
			key: '2019-12-04T00:00',
			value: 0.0,
		},
		{
			key: '2019-12-05T00:00',
			value: 0.0,
		},
		{
			key: '2019-12-06T00:00',
			value: 0.0,
		},
		{
			key: '2019-12-07T00:00',
			value: 0.0,
		},
		{
			key: '2019-12-08T00:00',
			value: 0.8,
		},
		{
			key: '2019-12-09T00:00',
			value: 0.0,
		},
	],
	value: 0.36,
};
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

describe('The completion velocity card component should', () => {
	let getAllByText;
	let getByText;
	const {ResizeObserver} = window;

	beforeAll(async () => {
		delete window.ResizeObserver;
		window.ResizeObserver = jest.fn().mockImplementation(() => ({
			disconnect: jest.fn(),
			observe: jest.fn(),
			unobserve: jest.fn(),
		}));

		jsonSessionStorage.set('timeRanges', timeRangeData);

		fetch.mockResolvedValueOnce({
			json: () => Promise.resolve(data),
			ok: true,
		});

		const renderResult = render(
			<MockRouter query={query}>
				<CompletionVelocityCard routeParams={{processId}} />
			</MockRouter>
		);

		getAllByText = renderResult.getAllByText;
		getByText = renderResult.getByText;

		await act(async () => {
			jest.runAllTimers();
		});
	});

	afterAll(() => {
		cleanup();
		window.ResizeObserver = ResizeObserver;
		jest.restoreAllMocks();
	});

	it('Be rendered with time range filter', () => {
		const timeRangeFilter = getByText('Last 30 Days');
		const activeItem = document.querySelector('.active');

		expect(timeRangeFilter).not.toBeNull();
		expect(activeItem).toHaveTextContent('Last 7 Days');
	});

	it('Be rendered with velocity unit filter', () => {
		const velocityUnitFilter = getAllByText('inst-day')[0];

		const activeItem = document.querySelectorAll('.active')[1];

		expect(velocityUnitFilter).not.toBeNull();
		expect(activeItem).toHaveTextContent('inst-day');
	});
});
