/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {
	cleanup,
	fireEvent,
	render,
	screen,
	waitFor,
} from '@testing-library/react';
import React from 'react';

import RoomTrend from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/RoomTrend';

let originalLiferay: any;

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {
			updateRoom: jest.fn((_roomId, {trend}) => Promise.resolve({trend})),
		},
	})
);

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/hooks/useIsInViewport',
	() => ({
		__esModule: true,
		default: jest.fn(() => true),
	})
);

const mockRoom = {
	id: 12345,
	trend: 0,
};

describe('RoomTrend', () => {
	beforeAll(() => {
		originalLiferay = window.Liferay;

		window.Liferay = {
			...originalLiferay,
			Language: {
				...originalLiferay?.Language,
				get: (key: string) => key,
			},
			ThemeDisplay: {
				...originalLiferay?.ThemeDisplay,
				getPathContext: () => '',
				getPortalURL: () => 'http://localhost',
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

	afterEach(cleanup);

	it('renders with the default trend status', async () => {
		const {container} = render(<RoomTrend />);

		Liferay.fire('dsr-filters-updated', {
			filters: {
				room: {
					value: {
						room: mockRoom,
					},
				},
			},
		});

		expect(await screen.findByText('room-trend')).toBeInTheDocument();

		const buttonText = container.querySelector('.room-trend-button-text');

		expect(buttonText?.textContent).toBe('cold');
	});

	it('changes the trend status when an option is selected', async () => {
		const {container} = render(<RoomTrend />);

		Liferay.fire('dsr-filters-updated', {
			filters: {
				room: {
					value: {
						room: mockRoom,
					},
				},
			},
		});

		const dropdownButton = await screen.findByRole('button', {
			name: /cold/i,
		});

		await waitFor(() => fireEvent.click(dropdownButton));

		const hotOptions = screen.getAllByText('hot');

		await waitFor(() => fireEvent.click(hotOptions[0]));

		await waitFor(() => {
			expect(
				container.querySelector('.room-trend-button-text')?.textContent
			).toBe('hot');
		});
	});

	it('updates the pointer rotation when the trend status changes', async () => {
		render(<RoomTrend />);

		Liferay.fire('dsr-filters-updated', {
			filters: {
				room: {
					value: {
						room: mockRoom,
					},
				},
			},
		});

		await waitFor(() => {
			const pointer = document.querySelector(
				'.room-trend-pointer'
			) as HTMLElement;

			expect(pointer).not.toBeNull();
			expect(pointer.style.transform).toBe(
				'rotate(-67deg) translateZ(0)'
			);
		});

		const dropdownButton = await screen.findByRole('button', {
			name: /cold/i,
		});

		fireEvent.click(dropdownButton);

		const hotOption = await screen.findByRole('menuitem', {name: /hot/i});

		fireEvent.click(hotOption);

		await waitFor(() => {
			const pointer = document.querySelector(
				'.room-trend-pointer'
			) as HTMLElement;

			expect(pointer.style.transform).toBe('rotate(45deg) translateZ(0)');
		});
	});
});
