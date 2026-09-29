/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import useAnalyticsQuery from '../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery';
import MostActiveVisitors from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/MostActiveVisitors';
import {mostActiveVisitorsFixture} from '../fixtures/MostActiveVisitorsFixture';

let originalLiferay: any;

const mockLiferayLanguageGet = jest.fn((key: string) => {
	return key;
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
			mostActiveVisitorsFixture,
		} = require('../fixtures/MostActiveVisitorsFixture');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: mostActiveVisitorsFixture,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('MostActiveVisitors', () => {
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
			response: mostActiveVisitorsFixture,
			sendRequest: jest.fn(),
		}));
	});

	it('renders the component with provided data', () => {
		const {baseElement} = render(
			<MostActiveVisitors
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(baseElement).toMatchSnapshot();

		expect(screen.getByText('John')).toBeInTheDocument();
		expect(screen.getByText('Doe')).toBeInTheDocument();
		expect(screen.getByText('150')).toBeInTheDocument();
		expect(screen.getByText('actions')).toBeInTheDocument();
		expect(screen.getByText('john.doe@liferay.com')).toBeInTheDocument();
	});

	it('renders "anonymous" when visitor fields are missing', () => {
		const anonymousResponse = {
			mostActiveVisitors: {
				mostActiveVisitors: [
					{
						activitiesCount: 12,
						emailAddress: undefined,
						firstName: undefined,
						id: '2',
						lastName: undefined,
					},
				],
				total: 1,
			},
		};

		(useAnalyticsQuery as jest.Mock).mockImplementation(() => ({
			isLoading: false,
			response: anonymousResponse,
			sendRequest: jest.fn(),
		}));

		render(
			<MostActiveVisitors
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(screen.getByText('anonymous')).toBeInTheDocument();
	});

	it('renders the not-configured message when analytics cloud is not configured', () => {
		render(
			<MostActiveVisitors
				isAnalyticsEnabled={false}
				namespace="test-namespace"
			/>
		);

		expect(
			screen.getByText('analytics-cloud-is-not-configured')
		).toBeInTheDocument();
	});
});
