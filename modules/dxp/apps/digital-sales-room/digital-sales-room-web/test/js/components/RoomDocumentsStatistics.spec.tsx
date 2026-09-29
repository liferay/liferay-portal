/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import DocumentsStatistics from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/RoomDocumentsStatistics';

let originalLiferay: any;

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === '1-hour') {
		return '1 hour';
	}

	if (key === '1-minute') {
		return '1 minute';
	}

	if (key === 'x-hours') {
		return 'x hours';
	}

	if (key === 'x-minutes') {
		return 'x minutes';
	}

	if (key === 'x-users') {
		return 'x users';
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

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useAnalyticsQuery',
	() => {
		const {
			roomDocumentsStatisticsFixture,
		} = require('../fixtures/RoomDocumentsStatisticsFixture');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: roomDocumentsStatisticsFixture,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('RoomDocumentsStatistics', () => {
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
	});

	it('renders the component with provided data', () => {
		const {baseElement} = render(
			<DocumentsStatistics
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(baseElement).toMatchSnapshot();

		expect(screen.getByText('pdf_test')).toBeInTheDocument();
		expect(screen.getByText('89')).toBeInTheDocument();
		expect(screen.getAllByText('324')[0]).toBeInTheDocument();
	});

	it('renders the user involved count from userInvolvedMetric', () => {
		render(
			<DocumentsStatistics
				isAnalyticsEnabled={true}
				namespace="test-namespace"
			/>
		);

		expect(screen.getAllByText('4').length).toBe(3);
		expect(screen.getAllByText('3').length).toBe(2);
		expect(screen.getByText('2')).toBeInTheDocument();
	});

	it('renders the not-configured message when analytics cloud is not configured', () => {
		render(
			<DocumentsStatistics
				isAnalyticsEnabled={false}
				namespace="test-namespace"
			/>
		);

		expect(
			screen.getByText('analytics-cloud-is-not-configured')
		).toBeInTheDocument();
	});
});
