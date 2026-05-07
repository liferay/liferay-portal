/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import LatestActivity from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/LatestActivity';

const mockLiferayLanguageGet = jest.fn((key: string) => {
	return key;
});

(global as any).Liferay = {
	...(global as any).Liferay,
	Language: {
		...(global as any).Liferay.Language,
		get: mockLiferayLanguageGet,
	},
};

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
			latestActivityDevEnvData,
		} = require('../fixtures/analyticsDevEnvData');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: latestActivityDevEnvData,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('LatestActivity', () => {
	beforeEach(() => {
		jest.fn();
	});

	afterEach(() => {
		cleanup();
		jest.clearAllMocks();
	});

	it('renders the component with provided data', () => {
		const {baseElement} = render(
			<LatestActivity namespace="test-namespace" />
		);

		expect(baseElement).toMatchSnapshot();

		expect(screen.getByText('John Doe')).toBeInTheDocument();
		expect(screen.getByText('Created a new document')).toBeInTheDocument();
	});

	it('renders the correct timestamp representation from moment', () => {
		render(<LatestActivity namespace="test-namespace" />);

		expect(screen.getByText('2 hours ago')).toBeInTheDocument();
	});
});
