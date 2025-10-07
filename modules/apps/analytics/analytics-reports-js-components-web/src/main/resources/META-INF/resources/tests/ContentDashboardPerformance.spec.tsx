/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';
import {
	fireEvent,
	render,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import {fetch} from 'frontend-js-web';
import React from 'react';

import ContentDashboardPerformance from '../js/ContentDashboardPerformance';
import {RangeSelectors} from '../js/components/RangeSelectorsDropdown';
import {AssetTypes, Individuals} from '../js/types/global';
import {TrendClassification, assetMetrics} from '../js/utils/metrics';

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as object),
	fetch: jest.fn(() => Promise.resolve({})),
}));

const mockedFetch = fetch as unknown as jest.MockedFunction<
	(url: string, options: {body: FormData}) => Promise<{}>
>;

const MOCKED_CONNECTED_DATA = {
	assetId: '123',
	assetType: AssetTypes.Blog,
	connectedToAnalyticsCloud: true,
	groupId: '456',
	isAdmin: true,
	siteSyncedToAnalyticsCloud: true,
};

const getMockedData = (assetType: AssetTypes) => ({
	assetId: '123',
	assetType,
	defaultMetric: {
		metricType: assetMetrics[assetType][0],
		trend: {
			percentage: 50,
			trendClassification: TrendClassification.Positive,
		},
		value: 1000,
	},
	selectedMetrics: assetMetrics[assetType].map((metricName) => ({
		metricType: metricName,
		trend: {
			percentage: 50,
			trendClassification: TrendClassification.Positive,
		},
		value: 1000,
	})),
});

describe.skip('ContentDashboardPerformance Overview Metrics', () => {
	afterEach(() => {
		mockedFetch.mockReset();
	});

	it('renders', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByText} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		expect(mockedFetch).toHaveBeenCalledTimes(5);

		expect(getByText('overview')).toBeTruthy();
	});

	it('is able to see 2 metrics on Overview Metric component: Views and Comments', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		mockedFetch.mockReturnValueOnce(
			Promise.resolve(
				new Response(JSON.stringify(getMockedData(AssetTypes.Blog)))
			)
		);

		mockedFetch.mockReturnValueOnce(
			Promise.resolve(
				new Response(JSON.stringify(getMockedData(AssetTypes.Blog)))
			)
		);

		const {container, getByText} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		expect(mockedFetch).toHaveBeenCalledTimes(6);

		expect(getByText('comments')).toBeInTheDocument();
		expect(getByText('views')).toBeInTheDocument();
	});

	it('is able to see 3 metrics on Overview Metric component: Downloads, Impressions and Comments', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		mockedFetch.mockReturnValueOnce(
			Promise.resolve(
				new Response(JSON.stringify(getMockedData(AssetTypes.Document)))
			)
		);

		mockedFetch.mockReturnValueOnce(
			Promise.resolve(
				new Response(JSON.stringify(getMockedData(AssetTypes.Document)))
			)
		);

		const {container, getByText} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		expect(mockedFetch).toHaveBeenCalledTimes(6);

		expect(getByText('downloads')).toBeInTheDocument();
		expect(getByText('impressions')).toBeInTheDocument();
		expect(getByText('comments')).toBeInTheDocument();
	});

	it('is able to see 1 metric on Overview Metric component: Views', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		mockedFetch.mockReturnValueOnce(
			Promise.resolve(
				new Response(
					JSON.stringify(getMockedData(AssetTypes.WebContent))
				)
			)
		);

		mockedFetch.mockReturnValueOnce(
			Promise.resolve(
				new Response(
					JSON.stringify(getMockedData(AssetTypes.WebContent))
				)
			)
		);

		const {container, getByText} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		expect(mockedFetch).toHaveBeenCalledTimes(6);

		expect(getByText('views')).toBeInTheDocument();
	});
});

describe('ContentDashboardPerformance Filter by Individuals', () => {
	it('is able to filter Overview Metric by all individuals (default)', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const individualsFilter = getByTestId('individuals');

		fireEvent.click(individualsFilter);

		fireEvent.click(
			getByTestId(`filter-item-${Individuals.AllIndividuals}`)
		);

		expect(individualsFilter.textContent).toEqual('all-individuals');
	});

	it('is able to filter Overview Metric by known Iindividuals', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const individualsFilter = getByTestId('individuals');

		fireEvent.click(individualsFilter);

		fireEvent.click(
			getByTestId(`filter-item-${Individuals.KnownIndividuals}`)
		);

		expect(individualsFilter.textContent).toEqual('known-individuals');
	});

	it('is able to filter Overview Metric by anonymous individuals', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const individualsFilter = getByTestId('individuals');

		fireEvent.click(individualsFilter);

		fireEvent.click(
			getByTestId(`filter-item-${Individuals.AnonymousIndividuals}`)
		);

		expect(individualsFilter.textContent).toEqual('anonymous-individuals');
	});
});

describe('ContentDashboardPerformance Filter by RangeSelectors', () => {
	it('is able to filter Overview Metric by last 7 days', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const rangeSelectorsFilter = getByTestId('rangeSelectors');

		fireEvent.click(rangeSelectorsFilter);

		fireEvent.click(getByTestId(`filter-item-${RangeSelectors.Last7Days}`));

		expect(rangeSelectorsFilter.textContent).toEqual('last-7-days');
	});

	it('is able to filter Overview Metric by last 28 days', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const rangeSelectorsFilter = getByTestId('rangeSelectors');

		fireEvent.click(rangeSelectorsFilter);

		fireEvent.click(
			getByTestId(`filter-item-${RangeSelectors.Last28Days}`)
		);

		expect(rangeSelectorsFilter.textContent).toEqual('last-28-days');
	});

	it('is able to filter Overview Metric by last 30 days', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const rangeSelectorsFilter = getByTestId('rangeSelectors');

		fireEvent.click(rangeSelectorsFilter);

		fireEvent.click(
			getByTestId(`filter-item-${RangeSelectors.Last30Days}`)
		);

		expect(rangeSelectorsFilter.textContent).toEqual('last-30-days');
	});

	it('is able to filter Overview Metric by last 90 days', async () => {
		mockedFetch.mockReturnValueOnce(
			Promise.resolve(new Response(JSON.stringify(MOCKED_CONNECTED_DATA)))
		);

		const {container, getByTestId} = render(
			<ContentDashboardPerformance
				contentPerformanceDataFetchURL="/o/api/fake-url"
				getItemVersionsURL=""
			/>
		);

		await waitForElementToBeRemoved(
			container.querySelector('.loading-animation')
		);

		const rangeSelectorsFilter = getByTestId('rangeSelectors');

		fireEvent.click(rangeSelectorsFilter);

		fireEvent.click(
			getByTestId(`filter-item-${RangeSelectors.Last90Days}`)
		);

		expect(rangeSelectorsFilter.textContent).toEqual('last-90-days');
	});
});
