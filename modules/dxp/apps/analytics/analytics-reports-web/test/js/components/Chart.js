/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {render, waitFor} from '@testing-library/react';
import React from 'react';

import Chart from '../../../src/main/resources/META-INF/resources/js/components/Chart';
import {ChartStateContextProvider} from '../../../src/main/resources/META-INF/resources/js/context/ChartStateContext';
import {StoreContextProvider} from '../../../src/main/resources/META-INF/resources/js/context/StoreContext';

const mockReadsDataProvider = jest.fn(() =>
	Promise.resolve({
		analyticsReportsHistoricalReads: {
			histogram: [
				{
					key: '2020-01-27T00:00',
					previousKey: '2020-01-20T00:00',
					previousValue: 22.0,
					value: 33.0,
				},
				{
					key: '2020-01-28T00:00',
					previousKey: '2020-01-21T00:00',
					previousValue: 23.0,
					value: 33.0,
				},
				{
					key: '2020-01-29T00:00',
					previousKey: '2020-01-22T00:00',
					previousValue: 24.0,
					value: 34.0,
				},
				{
					key: '2020-01-30T00:00',
					previousKey: '2020-01-23T00:00',
					previousValue: 25.0,
					value: 33.0,
				},
				{
					key: '2020-01-31T00:00',
					previousKey: '2020-01-24T00:00',
					previousValue: 26.0,
					value: 32.0,
				},
				{
					key: '2020-02-01T00:00',
					previousKey: '2020-01-25T00:00',
					previousValue: 27.0,
					value: 31.0,
				},
				{
					key: '2020-02-02T00:00',
					previousKey: '2020-01-26T00:00',
					previousValue: 28.0,
					value: 30.0,
				},
			],
			previousValue: 175.0,
			value: 226.0,
		},
	})
);

const mockViewsDataProvider = jest.fn(() =>
	Promise.resolve({
		analyticsReportsHistoricalViews: {
			histogram: [
				{
					key: '2020-01-27T00:00',
					previousKey: '2020-01-20T00:00',
					previousValue: 22.0,
					value: 32.0,
				},
				{
					key: '2020-01-28T00:00',
					previousKey: '2020-01-21T00:00',
					previousValue: 23.0,
					value: 33.0,
				},
				{
					key: '2020-01-29T00:00',
					previousKey: '2020-01-22T00:00',
					previousValue: 24.0,
					value: 34.0,
				},
				{
					key: '2020-01-30T00:00',
					previousKey: '2020-01-23T00:00',
					previousValue: 25.0,
					value: 33.0,
				},
				{
					key: '2020-01-31T00:00',
					previousKey: '2020-01-24T00:00',
					previousValue: 26.0,
					value: 32.0,
				},
				{
					key: '2020-02-01T00:00',
					previousKey: '2020-01-25T00:00',
					previousValue: 27.0,
					value: 31.0,
				},
				{
					key: '2020-02-02T00:00',
					previousKey: '2020-01-26T00:00',
					previousValue: 28.0,
					value: 30.0,
				},
			],
			previousValue: 175.0,
			value: 225.0,
		},
	})
);

const mockLanguageTag = 'en-US';

const mockPublishDate = 'Thu Aug 10 08:17:57 GMT 2020';

const mockTimeSpanOptions = [
	{
		key: 'last-30-days',
		label: 'Last 30 Days',
	},
	{
		key: 'last-7-days',
		label: 'Last 7 Days',
	},
	{
		key: 'last-24-hours',
		label: 'Last 24 Hours',
	},
];

describe('Chart', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('displays total views and date range title for default time span', async () => {
		const testProps = {
			pagePublishDate: 'Thu Aug 10 08:17:57 GMT 2019',
			timeRange: {endDate: '2020-02-02', startDate: '2020-01-27'},
			timeSpanKey: 'last-7-days',
		};

		const {getByText, queryByText} = render(
			<StoreContextProvider value={{languageTag: mockLanguageTag}}>
				<ChartStateContextProvider
					publishDate={testProps.pagePublishDate}
					timeRange={testProps.timeRange}
					timeSpanKey={testProps.timeSpanKey}
				>
					<Chart
						dataProviders={[mockViewsDataProvider]}
						publishDate={mockPublishDate}
						timeSpanOptions={mockTimeSpanOptions}
					/>
				</ChartStateContextProvider>
			</StoreContextProvider>
		);

		await waitFor(() =>
			expect(mockViewsDataProvider).toHaveBeenCalledTimes(1)
		);

		expect(getByText('225')).toBeInTheDocument();

		// TimeSpan selector is now in Main component, not in Chart

		expect(queryByText('Jan 27 - Feb 2, 2020')).not.toBeInTheDocument();
	});

	it('displays total views and reads and date range title for default time span', async () => {
		const testProps = {
			pagePublishDate: 'Thu Aug 10 08:17:57 GMT 2019',
			timeRange: {endDate: '2020-02-02', startDate: '2020-01-27'},
			timeSpanKey: 'last-7-days',
		};

		const {getByText, queryByText} = render(
			<StoreContextProvider value={{languageTag: mockLanguageTag}}>
				<ChartStateContextProvider
					publishDate={testProps.pagePublishDate}
					timeRange={testProps.timeRange}
					timeSpanKey={testProps.timeSpanKey}
				>
					<Chart
						dataProviders={[
							mockViewsDataProvider,
							mockReadsDataProvider,
						]}
						publishDate={mockPublishDate}
						timeSpanOptions={mockTimeSpanOptions}
					/>
				</ChartStateContextProvider>
			</StoreContextProvider>
		);

		await waitFor(() => {
			expect(mockViewsDataProvider).toHaveBeenCalledTimes(1);
			expect(mockReadsDataProvider).toHaveBeenCalledTimes(1);
		});

		expect(getByText('225')).toBeInTheDocument();
		expect(getByText('226')).toBeInTheDocument();

		// TimeSpan selector is now in Main component, not in Chart

		expect(queryByText('Jan 27 - Feb 2, 2020')).not.toBeInTheDocument();
	});
});
