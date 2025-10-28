/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {render} from '@testing-library/react';
import React from 'react';

import Navigation from '../../../src/main/resources/META-INF/resources/js/components/Navigation';
import {ChartStateContextProvider} from '../../../src/main/resources/META-INF/resources/js/context/ChartStateContext';
import ConnectionContext from '../../../src/main/resources/META-INF/resources/js/context/ConnectionContext';
import {StoreContextProvider} from '../../../src/main/resources/META-INF/resources/js/context/StoreContext';

import '@testing-library/jest-dom';

const mockEndpoints = {
	analyticsReportsHistoricalReadsURL: '/o/analyticsReportsHistoricalReadsURL',
	analyticsReportsHistoricalViewsURL: '/o/analyticsReportsHistoricalViewsURL',
	analyticsReportsTotalReadsURL: '/o/analyticsReportsTotalReadsURL',
	analyticsReportsTotalViewsURL: '/o/analyticsReportsTotalViewsURL',
	analyticsReportsTrafficSourcesURL: '/o/analyticsReportsTrafficSourcesURL',
};

const mockLanguageTag = 'en-US';

const mockNamespace = 'namespace';

const mockPage = {plid: 20};

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

const mockViewURLs = [
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
		viewURL: 'http://localhost:8080/es/web/guest/-/contenido-web-basico',
	},
];

const noop = () => {};

describe('Navigation', () => {
	beforeEach(() => {
		fetch.mockResponse(Promise.resolve(JSON.stringify({})));
	});

	afterEach(() => {
		jest.clearAllMocks();
	});

	it('displays an alert error message if there is no valid connection', () => {
		const testProps = {
			author: {
				authorId: '',
				name: 'John Tester',
				url: '',
			},
			canonicalURL:
				'http://localhost:8080/en/web/guest/-/basic-web-content',
			pagePublishDate: 'Thu Aug 10 08:17:57 GMT 2020',
			pageTitle: 'A testing page',
			timeRange: {endDate: '2020-01-27', startDate: '2020-02-02'},
			timeSpanKey: 'last-7-days',
		};

		const {getByText} = render(
			<ConnectionContext.Provider
				value={{
					validAnalyticsConnection: false,
				}}
			>
				<StoreContextProvider
					value={{
						endpoints: mockEndpoints,
						languageTag: mockLanguageTag,
						namespace: mockNamespace,
						page: mockPage,
					}}
				>
					<ChartStateContextProvider
						publishDate={testProps.pagePublishDate}
						timeRange={testProps.timeRange}
						timeSpanKey={testProps.timeSpanKey}
					>
						<Navigation
							author={testProps.author}
							canonicalURL={testProps.canonicalURL}
							endpoints={mockEndpoints}
							onSelectedLanguageClick={noop}
							page={testProps.page}
							pagePublishDate={testProps.pagePublishDate}
							pageTitle={testProps.pageTitle}
							timeSpanOptions={mockTimeSpanOptions}
							viewURLs={mockViewURLs}
						/>
					</ChartStateContextProvider>
				</StoreContextProvider>
			</ConnectionContext.Provider>
		);

		expect(getByText('an-unexpected-error-occurred')).toBeInTheDocument();
	});

	it('displays an alert warning message if some data is temporarily unavailable', () => {
		const testProps = {
			author: {
				authorId: '',
				name: 'John Tester',
				url: '',
			},
			canonicalURL:
				'http://localhost:8080/en/web/guest/-/basic-web-content',
			pagePublishDate: 'Thu Aug 10 08:17:57 GMT 2020',
			pageTitle: 'A testing page',
			timeRange: {endDate: '2020-01-27', startDate: '2020-02-02'},
			timeSpanKey: 'last-7-days',
		};

		const {getByText} = render(
			<StoreContextProvider
				value={{
					endpoints: mockEndpoints,
					languageTag: mockLanguageTag,
					namespace: mockNamespace,
					page: mockPage,
					warning: true,
				}}
			>
				<ChartStateContextProvider
					publishDate={testProps.publishDate}
					timeRange={testProps.timeRange}
					timeSpanKey={testProps.timeSpanKey}
				>
					<Navigation
						author={testProps.author}
						canonicalURL={testProps.canonicalURL}
						endpoints={mockEndpoints}
						onSelectedLanguageClick={noop}
						page={testProps.page}
						pagePublishDate={testProps.pagePublishDate}
						pageTitle={testProps.pageTitle}
						timeSpanOptions={mockTimeSpanOptions}
						viewURLs={mockViewURLs}
					/>
				</ChartStateContextProvider>
			</StoreContextProvider>
		);

		expect(
			getByText('some-data-is-temporarily-unavailable')
		).toBeInTheDocument();
	});

	it('displays an alert info message if the content was published today', () => {
		const testProps = {
			author: {
				authorId: '',
				name: 'John Tester',
				url: '',
			},
			canonicalURL:
				'http://localhost:8080/en/web/guest/-/basic-web-content',
			pagePublishDate: 'Thu Feb 02 08:17:57 GMT 2020',
			pageTitle: 'A testing page',
			timeRange: {endDate: '2020-01-27', startDate: '2020-02-02'},
			timeSpanKey: 'last-7-days',
		};

		const {getByText} = render(
			<StoreContextProvider
				value={{
					endpoints: mockEndpoints,
					languageTag: mockLanguageTag,
					namespace: mockNamespace,
					page: mockPage,
					publishedToday: true,
				}}
			>
				<ChartStateContextProvider
					publishDate={testProps.pagePublishDate}
					timeRange={testProps.timeRange}
					timeSpanKey={testProps.timeSpanKey}
				>
					<Navigation
						author={testProps.author}
						canonicalURL={testProps.canonicalURL}
						endpoints={mockEndpoints}
						onSelectedLanguageClick={noop}
						page={testProps.page}
						pagePublishDate={testProps.pagePublishDate}
						pageTitle={testProps.pageTitle}
						timeSpanOptions={mockTimeSpanOptions}
						viewURLs={mockViewURLs}
					/>
				</ChartStateContextProvider>
			</StoreContextProvider>
		);

		expect(getByText('no-data-is-available-yet')).toBeInTheDocument();
		expect(
			getByText('content-has-just-been-published')
		).toBeInTheDocument();
	});
});
