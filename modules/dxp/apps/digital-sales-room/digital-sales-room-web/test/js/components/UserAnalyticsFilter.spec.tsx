/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, fireEvent, render, screen} from '@testing-library/react';
import React from 'react';

import RoomService from '../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import UserAnalyticsFilter from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/filters/UserAnalyticsFilter';
import {IAnalyticsUserFilter} from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/types';

let originalLiferay: any;

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {
			getRoomUserAccounts: jest.fn(),
		},
	})
);

const mockRoom = {id: 12345, name: 'Acme Room'} as any;

const mockUser = {id: 67890, name: 'John Doe'};

const filter: IAnalyticsUserFilter = {
	active: true,
	component: UserAnalyticsFilter,
	value: [],
};

function fireRoomSelected(room: any) {
	Liferay.fire('dsr-filters-updated', {
		filters: {
			room: {
				value: {
					room,
				},
			},
		},
	});
}

describe('UserAnalyticsFilter', () => {
	beforeAll(() => {
		originalLiferay = window.Liferay;

		window.Liferay = {
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
		window.Liferay = originalLiferay;
	});

	beforeEach(() => {
		(RoomService.getRoomUserAccounts as jest.Mock).mockResolvedValue([
			mockUser,
		]);
	});

	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('lists only All Users when no room is selected', () => {
		render(<UserAnalyticsFilter filter={filter} setValue={jest.fn()} />);

		expect(screen.getByRole('button')).toHaveTextContent('all-users');

		fireEvent.click(screen.getByRole('button'));

		expect(
			screen.getByRole('menuitem', {name: /all-users/i})
		).toBeInTheDocument();
		expect(
			screen.queryByRole('menuitem', {name: /John Doe/})
		).not.toBeInTheDocument();
	});

	it('lists the users of the selected room', async () => {
		render(<UserAnalyticsFilter filter={filter} setValue={jest.fn()} />);

		fireRoomSelected(mockRoom);

		fireEvent.click(screen.getByRole('button'));

		expect(
			await screen.findByRole('menuitem', {name: /John Doe/})
		).toBeInTheDocument();
	});
});
