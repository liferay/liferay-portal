/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import RoomStatistics from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/RoomStatistics';
import {roomStatisticsFixture} from '../fixtures/RoomStatisticsFixture';

let originalLiferay: any;

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === '1-day') {
		return '1 day';
	}

	if (key === '1-hour') {
		return '1 hour';
	}

	if (key === '1-minute') {
		return '1 minute';
	}

	if (key === 'x-days') {
		return 'x days';
	}

	if (key === 'x-hours') {
		return 'x hours';
	}

	if (key === 'x-minutes') {
		return 'x minutes';
	}

	return key;
});

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	sub: (str: string, ...args: any[]) => {
		args.forEach((arg) => {
			str = str.replace('x', String(arg));
		});

		return str;
	},
}));

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useIsInViewport',
	() => ({
		__esModule: true,
		default: jest.fn(() => true),
	})
);

let mockAnalyticsResponse: any;

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery',
	() => ({
		__esModule: true,
		default: jest.fn(() => ({
			isLoading: false,
			response: mockAnalyticsResponse,
			sendRequest: jest.fn(),
		})),
	})
);

const withTotalSessionDurationInMinutes = (minutes: number) => ({
	...roomStatisticsFixture,
	siteVisitorBehavior: {
		...roomStatisticsFixture.siteVisitorBehavior,
		totalSessionDuration: minutes * 60000,
	},
});

describe('RoomStatistics', () => {
	beforeAll(() => {
		originalLiferay = window.Liferay;

		window.Liferay = {
			...originalLiferay,
			Language: {
				...originalLiferay?.Language,
				get: mockLiferayLanguageGet,
			},

			detach: (name, fn): void => {
				window.removeEventListener(name as string, fn as EventListener);
			},
			fire: (name, payload) => {
				const event = document.createEvent('CustomEvent');

				event.initCustomEvent(name);

				if (payload) {
					Object.keys(payload).forEach((key: string) => {
						(event as any)[key] = payload[key];
					});
				}

				window.dispatchEvent(event);
			},
			on: (name, fn) => {
				if (fn) {
					window.addEventListener(
						name as string,
						fn as EventListener
					);
				}

				return {
					detach: () => {
						if (fn) {
							window.removeEventListener(
								name as string,
								fn as EventListener
							);
						}

						return 0;
					},
				};
			},
		};
	});

	beforeEach(() => {
		mockAnalyticsResponse = roomStatisticsFixture;
	});

	afterAll(() => {
		window.Liferay = originalLiferay;

		cleanup();

		jest.resetAllMocks();
	});

	it('matches snapshot', () => {
		const {container} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(container).toMatchSnapshot();
	});

	it('renders with provided data', () => {
		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('45 minutes')).toBeInTheDocument();
		expect(getByText('100')).toBeInTheDocument();
		expect(getByText('20')).toBeInTheDocument();
		expect(getByText('10')).toBeInTheDocument();
		expect(getByText('5')).toBeInTheDocument();
	});

	it('renders 1 minute when totalSessionDuration is 86321 milliseconds', () => {
		mockAnalyticsResponse = {
			...roomStatisticsFixture,
			siteVisitorBehavior: {
				...roomStatisticsFixture.siteVisitorBehavior,
				totalSessionDuration: 86321,
			},
		};

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('1 minute')).toBeInTheDocument();
	});

	it('renders 2 hours 30 minutes when totalSessionDuration is 2h30', () => {
		mockAnalyticsResponse = withTotalSessionDurationInMinutes(2 * 60 + 30);

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('2 hours 30 minutes')).toBeInTheDocument();
	});

	it('renders 1 hour when totalSessionDuration is 1h', () => {
		mockAnalyticsResponse = withTotalSessionDurationInMinutes(60);

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('1 hour')).toBeInTheDocument();
	});

	it('renders 1 day when totalSessionDuration is 24h', () => {
		mockAnalyticsResponse = withTotalSessionDurationInMinutes(24 * 60);

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('1 day')).toBeInTheDocument();
	});

	it('renders 1 day 1 hour when totalSessionDuration is 25h30', () => {
		mockAnalyticsResponse = withTotalSessionDurationInMinutes(25 * 60 + 30);

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('1 day 1 hour')).toBeInTheDocument();
	});

	it('renders 2 days when totalSessionDuration is 48h', () => {
		mockAnalyticsResponse = withTotalSessionDurationInMinutes(48 * 60);

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('2 days')).toBeInTheDocument();
	});

	it('renders 2 days 2 hours when totalSessionDuration is 50h15', () => {
		mockAnalyticsResponse = withTotalSessionDurationInMinutes(50 * 60 + 15);

		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={true} />
		);

		expect(getByText('2 days 2 hours')).toBeInTheDocument();
	});

	it('renders the not-configured message when analytics cloud is not configured', () => {
		const {getByText} = render(
			<RoomStatistics isAnalyticsEnabled={false} />
		);

		expect(
			getByText('analytics-cloud-is-not-configured')
		).toBeInTheDocument();
	});
});
