/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom/extend-expect';

// eslint-disable-next-line @liferay/portal/no-cross-module-deep-import, @liferay/no-extraneous-dependencies
import {checkAccessibility} from '@liferay/layout-js-components-web/test/__lib__/index';
import {
	render,
	screen,
	waitForElementToBeRemoved,
} from '@testing-library/react';
import React from 'react';

import ApiHelper from '../../../js/apis/ApiHelper';
import {TrafficChannels} from '../../../js/components/cms/TrafficChannels';

const mockData = {
	data: {
		items: [
			{
				count: 10,
				name: 'direct',
				percentage: 0.833,
			},
			{
				count: 20,
				name: 'social',
				percentage: 0.1667,
			},
			{
				count: 15,
				name: 'referrals',
				percentage: 0.125,
			},
			{
				count: 10,
				name: 'paid-search',
				percentage: 0.833,
			},
			{
				count: 30,
				name: 'email',
				percentage: 0.25,
			},
			{
				count: 35,
				name: 'others',
				percentage: 0.2917,
			},
		],
		totalCount: 120,
	},
	error: null,
};

describe('TrafficChannels', () => {
	it('renders', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue(mockData);

		render(<TrafficChannels />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText(
				'this-metric-calculates-the-top-five-traffic-channels-that-generated-the-highest-number-of-views-for-the-asset'
			)
		).toBeTruthy();
	});

	it('checks the accessibility of the share content', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue(mockData);

		const {container} = render(<TrafficChannels />);

		await checkAccessibility({bestPractices: true, context: container});
	});

	it('renders 5 traffic channels and others', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue(mockData);

		render(<TrafficChannels />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		const trafficChannelItem = screen.queryAllByRole('row');

		expect(trafficChannelItem).toHaveLength(7);
	});

	it('renders an empty state', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [],
				totalCount: 0,
			},
			error: null,
		});

		render(<TrafficChannels />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		expect(
			screen.getByText('views-by-traffic-channels')
		).toBeInTheDocument();

		expect(
			screen.getByText(
				'this-metric-calculates-the-top-five-traffic-channels-that-generated-the-highest-number-of-views-for-the-asset'
			)
		).toBeInTheDocument();

		expect(screen.getByText('no-data-available-yet')).toBeInTheDocument();

		expect(
			screen.getByText(
				'there-is-no-data-available-for-the-applied-filters-or-from-the-data-source'
			)
		).toBeInTheDocument();

		expect(
			screen.getByText('learn-more-about-asset-performance')
		).toBeInTheDocument();
	});

	it('calculates the percentage correctly', async () => {
		jest.spyOn(ApiHelper, 'get').mockResolvedValue({
			data: {
				items: [
					{
						count: 10,
						name: 'direct',
						percentage: 0.1667,
					},
					{
						count: 10,
						name: 'social',
						percentage: 0.1667,
					},
					{
						count: 10,
						name: 'referrals',
						percentage: 0.1667,
					},
					{
						count: 10,
						name: 'paid-search',
						percentage: 0.1667,
					},
					{
						count: 10,
						name: 'email',
						percentage: 0.1667,
					},
					{
						count: 10,
						name: 'others',
						percentage: 0.1667,
					},
				],
				totalCount: 60,
			},
			error: null,
		});

		const expectedPercentage = ((10 / 60) * 100).toFixed(2);

		render(<TrafficChannels />);

		await waitForElementToBeRemoved(() => screen.getByTestId('loading'));

		const trafficChannelItem =
			screen.getByText(/direct/).parentElement?.parentElement;

		expect(trafficChannelItem?.textContent).toContain(expectedPercentage);
	});
});
