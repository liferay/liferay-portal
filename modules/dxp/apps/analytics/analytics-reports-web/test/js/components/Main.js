/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {fireEvent, render, waitFor} from '@testing-library/react';
import React from 'react';

import Main from '../../../src/main/resources/META-INF/resources/js/components/Main';
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

const mockTrafficSourcesDataProvider = jest.fn(() =>
	Promise.resolve([
		{
			countryKeywords: [
				{
					countryCode: 'us',
					countryName: 'United States',
					keywords: [],
				},
				{
					countryCode: 'es',
					countryName: 'Spain',
					keywords: [],
				},
			],
			helpMessage: 'Testing Help Message',
			name: 'testing',
			share: 30.0,
			title: 'Testing',
			value: 32178,
		},
		{
			countryKeywords: [
				{
					countryCode: 'us',
					countryName: 'United States',
					keywords: [],
				},
				{
					countryCode: 'es',
					countryName: 'Spain',
					keywords: [],
				},
			],
			helpMessage: 'Second Testing Help Message',
			name: 'second-testing',
			share: 70.0,
			title: 'Second Testing',
			value: 278256,
		},
	])
);

const mockTotalViewsDataProvider = jest.fn(() => {
	return Promise.resolve(9999);
});

const mockReadsViewsDataProvider = jest.fn(() => {
	return Promise.resolve(9999);
});

const mockLanguageTag = 'en-US';

const mockPublishDate = 'Thu Aug 10 08:17:57 GMT 2019';

const mockTimeSpanOptions = [
	{
		key: 'last-30-days',
		label: 'Last 30 Days',
	},
	{
		key: 'last-7-days',
		label: 'Last 7 Days',
	},
];

const mockedContextProps = {
	publishDate: mockPublishDate,
	timeRange: {endDate: '2020-02-02', startDate: '2020-01-27'},
	timeSpanKey: 'last-7-days',
};

const mockedProps = {
	author: {
		authorId: '',
		name: 'John Tester',
		url: '',
	},
	canonicalURL: 'http://localhost:8080/en/web/guest/-/basic-web-content',
	chartDataProviders: [mockViewsDataProvider, mockReadsDataProvider],
	onSelectedLanguageClick: () => {},
	onTrafficSourceClick: () => {},
	pagePublishDate: mockPublishDate,
	pageTitle: 'A testing page',
	timeSpanOptions: mockTimeSpanOptions,
	totalReadsDataProvider: mockReadsViewsDataProvider,
	totalViewsDataProvider: mockTotalViewsDataProvider,
	trafficSourcesDataProvider: mockTrafficSourcesDataProvider,
	viewURLs: [
		{
			default: true,
			languageId: 'en-US',
			languageLabel: 'English (United States)',
			selected: true,
			viewURL: 'http://localhost:8080/en/web/guest/-/basic-web-content',
		},
		{
			default: false,
			languageId: 'es-ES',
			languageLabel: 'Spanish (Spain)',
			selected: false,
			viewURL:
				'http://localhost:8080/es/web/guest/-/contenido-web-basico',
		},
	],
};

describe('Main', () => {
	afterEach(() => {
		jest.clearAllMocks();
	});

	it('displays date range selector and charts for default time span', async () => {
		const {getByText} = render(
			<StoreContextProvider value={{languageTag: mockLanguageTag}}>
				<ChartStateContextProvider {...mockedContextProps}>
					<Main {...mockedProps} />
				</ChartStateContextProvider>
			</StoreContextProvider>
		);

		await waitFor(() => {
			expect(mockTotalViewsDataProvider).toHaveBeenCalledTimes(1);
			expect(mockTrafficSourcesDataProvider).toHaveBeenCalledTimes(1);
		});

		expect(getByText('Last 7 Days')).toBeInTheDocument();
		expect(getByText('Jan 27 - Feb 2, 2020')).toBeInTheDocument();
	});

	it('displays information for time span selected when navigating time spans horizontally', async () => {
		const {container, getByText} = render(
			<StoreContextProvider value={{languageTag: mockLanguageTag}}>
				<ChartStateContextProvider {...mockedContextProps}>
					<Main {...mockedProps} />
				</ChartStateContextProvider>
			</StoreContextProvider>
		);

		fireEvent.click(
			container.querySelector('[aria-label="previous-period"]')
		);

		expect(getByText('20 - Jan 26, 2020')).toBeInTheDocument();
		await waitFor(() => {
			expect(mockTrafficSourcesDataProvider).toHaveBeenCalledTimes(2);
		});

		fireEvent.click(container.querySelector('[aria-label="next-period"]'));

		expect(getByText('Jan 27 - Feb 2, 2020')).toBeInTheDocument();
		await waitFor(() => {
			expect(mockTrafficSourcesDataProvider).toHaveBeenCalledTimes(3);
		});
	});
});
