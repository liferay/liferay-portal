/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import useAnalyticsQuery from '../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery';
import LatestActivity from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/LatestActivity';
import {latestActivityFixture} from '../fixtures/LatestActivityFixture';

let originalLiferay: any;

const mockLiferayLanguageGet = jest.fn((key: string) => {
	return key;
});

jest.mock('moment', () => {
	const mockMomentInstance = {
		fromNow: jest.fn().mockReturnValue('2 hours ago'),
		startOf: jest.fn().mockReturnThis(),
		subtract: jest.fn().mockReturnThis(),
		toString: jest.fn().mockReturnValue('1772757506000'),
		valueOf: jest.fn().mockReturnValue(1772757506000),
	};

	const momentMock = jest.fn(() => mockMomentInstance);

	const nowMock = jest.fn(() => ({
		toString: () => '1772757506000',
		valueOf: () => 1772757506000,
	}));

	return {
		__esModule: true,
		default: momentMock,
		now: nowMock,
	};
});

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useIsInViewport',
	() => ({
		__esModule: true,
		default: jest.fn(() => true),
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery',
	() => {
		const {
			latestActivityFixture,
		} = require('../fixtures/LatestActivityFixture');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: latestActivityFixture,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('LatestActivity', () => {
	beforeAll(() => {
		originalLiferay = (global as any).Liferay;

		(global as any).Liferay = {
			...originalLiferay,
			Language: {
				...originalLiferay?.Language,
				get: mockLiferayLanguageGet,
			},
		};
	});

	afterAll(() => {
		(global as any).Liferay = originalLiferay;
	});

	beforeEach(() => {
		jest.fn();
	});

	afterEach(() => {
		cleanup();
		jest.clearAllMocks();

		(useAnalyticsQuery as jest.Mock).mockImplementation(() => ({
			isLoading: false,
			response: latestActivityFixture,
			sendRequest: jest.fn(),
		}));
	});

	it('renders the component with provided data', () => {
		const {baseElement} = render(
			<LatestActivity
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(baseElement).toMatchSnapshot();

		expect(screen.getByText('John Doe')).toBeInTheDocument();
		expect(screen.getByText('Created a new document')).toBeInTheDocument();
	});

	it('renders the correct timestamp representation from moment', () => {
		render(
			<LatestActivity
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(screen.getByText('2 hours ago')).toBeInTheDocument();
	});

	it('renders "anonymous" when the event has no individualName', () => {
		const anonymousResponse = {
			events: {
				eventEntries: [
					{
						createDate: '2026-03-26T14:30:00Z',
						individualName: undefined,
						name: 'pageViewed',
					},
				],
			},
		};

		(useAnalyticsQuery as jest.Mock).mockImplementation(() => ({
			isLoading: false,
			response: anonymousResponse,
			sendRequest: jest.fn(),
		}));

		render(
			<LatestActivity
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(screen.getByText('anonymous')).toBeInTheDocument();
	});

	it('renders the not-configured message when analytics cloud is not configured', () => {
		render(
			<LatestActivity
				isAnalyticsEnabled={false}
				namespace="test-namespace"
			/>
		);

		expect(
			screen.getByText('analytics-cloud-is-not-configured')
		).toBeInTheDocument();
	});
});
