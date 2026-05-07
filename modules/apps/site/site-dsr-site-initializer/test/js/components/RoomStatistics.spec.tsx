/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render} from '@testing-library/react';
import React from 'react';

import RoomStatistics from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/RoomStatistics';

const {Liferay: originalLiferay} = global.window;

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

(global as any).Liferay = {
	Language: {
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
			roomStatisticsDevEnvData,
		} = require('../fixtures/analyticsDevEnvData');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: roomStatisticsDevEnvData,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('RoomStatistics', () => {
	beforeAll(() => {
		window['Liferay'] = {
			...originalLiferay,

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

	afterAll(() => {
		cleanup();

		window.Liferay = originalLiferay;

		jest.resetAllMocks();
	});

	it('matches snapshot', () => {
		const {container} = render(<RoomStatistics />);

		expect(container).toMatchSnapshot();
	});

	it('renders with provided data', () => {
		const {getByText} = render(<RoomStatistics />);

		expect(getByText('0-hours 45-minutes')).toBeInTheDocument();
		expect(getByText('100')).toBeInTheDocument();
		expect(getByText('20')).toBeInTheDocument();
		expect(getByText('10')).toBeInTheDocument();
		expect(getByText('5')).toBeInTheDocument();
	});
});
