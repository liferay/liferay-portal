/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen} from '@testing-library/react';
import React from 'react';

import ActivityLog from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/ActivityLog';

const mockLiferayLanguageGet = jest.fn((key: string) => {
	if (key === 'commented-on') {
		return 'Commented on';
	}
	if (key === 'uploaded-a-x') {
		return 'Uploaded a {0}';
	}
	if (key === 'viewed-a-x') {
		return 'Viewed a {0}';
	}

	return key;
});

let originalLiferay: any;

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	sub: (str: string, arg: string) => str.replace('{0}', arg),
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
		const {activityLogFixture} = require('../fixtures/ActivityLogFixture');

		return {
			__esModule: true,
			default: jest.fn(() => ({
				isLoading: false,
				response: activityLogFixture,
				sendRequest: jest.fn(),
			})),
		};
	}
);

describe('ActivityLog Component', () => {
	beforeAll(() => {
		originalLiferay = window.Liferay;

		window.Liferay = {
			...originalLiferay,
			Language: {
				...originalLiferay?.Language,
				get: mockLiferayLanguageGet,
			},
			ThemeDisplay: {
				...originalLiferay?.ThemeDisplay,
				getBCP47LanguageId: () => 'en-US',
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

	afterAll(() => {
		window.Liferay = originalLiferay;

		cleanup();

		jest.resetAllMocks();
	});

	afterEach(() => {
		cleanup();
	});

	it('renders a date header per distinct event day', () => {
		render(<ActivityLog isAnalyticsEnabled={true} />);

		expect(screen.getByText('2026-03-06')).toBeInTheDocument();
		expect(screen.getByText('2026-03-07')).toBeInTheDocument();
	});

	it('groups consecutive logs for the same user', () => {
		render(<ActivityLog isAnalyticsEnabled={true} />);

		expect(screen.getAllByText('John Doe').length).toBe(1);
		expect(screen.getAllByText('Paul Gerome').length).toBe(1);
	});

	it('falls back to the anonymous label when a user session has no user name', () => {
		render(<ActivityLog isAnalyticsEnabled={true} />);

		expect(screen.getByText('anonymous')).toBeInTheDocument();
	});

	it('renders an asset title per event', () => {
		render(<ActivityLog isAnalyticsEnabled={true} />);

		expect(screen.getByText('document_a')).toBeInTheDocument();
		expect(screen.getByText('document_b')).toBeInTheDocument();
		expect(screen.getByText('document_c')).toBeInTheDocument();
	});

	it('renders the not-configured message when analytics cloud is not configured', () => {
		render(<ActivityLog isAnalyticsEnabled={false} />);

		expect(
			screen.getByText('analytics-cloud-is-not-configured')
		).toBeInTheDocument();
	});
});
