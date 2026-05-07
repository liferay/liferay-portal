/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import MostActiveVisitors from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/MostActiveVisitors';

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
			mostActiveVisitorsDevEnvData,
		} = require('../fixtures/analyticsDevEnvData');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: mostActiveVisitorsDevEnvData,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('MostActiveVisitors', () => {
	beforeEach(() => {
		jest.fn();
	});

	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('renders the component with provided data', () => {
		const {baseElement} = render(
			<MostActiveVisitors namespace="test-namespace" />
		);

		expect(baseElement).toMatchSnapshot();

		expect(screen.getByText('John')).toBeInTheDocument();
		expect(screen.getByText('Doe')).toBeInTheDocument();
		expect(screen.getByText('150')).toBeInTheDocument();
		expect(screen.getByText('actions')).toBeInTheDocument();
		expect(screen.getByText('john.doe@liferay.com')).toBeInTheDocument();
	});
});
