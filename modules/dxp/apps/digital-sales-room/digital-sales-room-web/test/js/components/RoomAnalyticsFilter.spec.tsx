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

import RoomService from '../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import RoomAnalyticsFilter from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/components/filters/RoomAnalyticsFilter';
import {
	AnalyticsFilters,
	IAnalyticsRoomFilter,
} from '../../../src/main/resources/META-INF/resources/js/main_view/analytics/types';

let originalLiferay: any;

jest.mock(
	'../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {
			getRooms: jest.fn(),
		},
	})
);

const mockRoom = {id: 54321, name: 'Acme Room', siteId: 11111} as any;

const filter: IAnalyticsRoomFilter = {
	active: true,
	component: RoomAnalyticsFilter,
	value: {room: null},
};

describe('RoomAnalyticsFilter', () => {
	beforeAll(() => {
		originalLiferay = (global as any).Liferay;

		(global as any).Liferay = {
			...originalLiferay,
			detach: jest.fn(),
			on: jest.fn(() => ({detach: jest.fn()})),
		};
	});

	afterAll(() => {
		(global as any).Liferay = originalLiferay;
	});

	beforeEach(() => {
		(RoomService.getRooms as jest.Mock).mockResolvedValue({
			items: [mockRoom],
		});
	});

	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('defaults the trigger to All Rooms', () => {
		render(
			<RoomAnalyticsFilter
				filter={filter}
				groupIds={['11111']}
				setValue={jest.fn()}
			/>
		);

		expect(screen.getByRole('button')).toHaveTextContent('all-rooms');
	});

	it('lists All Rooms plus existing rooms in the dropdown', async () => {
		render(
			<RoomAnalyticsFilter
				filter={filter}
				groupIds={['11111']}
				setValue={jest.fn()}
			/>
		);

		fireEvent.click(screen.getByRole('button'));

		expect(
			await screen.findByRole('menuitem', {name: /all-rooms/i})
		).toBeInTheDocument();

		expect(
			await screen.findByRole('menuitem', {name: /Acme Room/})
		).toBeInTheDocument();
	});

	it('omits rooms whose site is not visible', async () => {
		(RoomService.getRooms as jest.Mock).mockResolvedValue({
			items: [mockRoom, {id: 98765, name: 'Hidden Room', siteId: 22222}],
		});

		render(
			<RoomAnalyticsFilter
				filter={filter}
				groupIds={['11111']}
				setValue={jest.fn()}
			/>
		);

		fireEvent.click(screen.getByRole('button'));

		expect(
			await screen.findByRole('menuitem', {name: /Acme Room/})
		).toBeInTheDocument();

		expect(
			screen.queryByRole('menuitem', {name: /Hidden Room/})
		).not.toBeInTheDocument();
	});

	it('reflects the selection in the trigger and notifies the parent', async () => {
		const setValue = jest.fn();

		render(
			<RoomAnalyticsFilter
				filter={filter}
				groupIds={['11111']}
				setValue={setValue}
			/>
		);

		fireEvent.click(screen.getByRole('button'));

		fireEvent.click(
			await screen.findByRole('menuitem', {name: /Acme Room/})
		);

		await waitFor(() =>
			expect(screen.getByRole('button')).toHaveTextContent('Acme Room')
		);

		expect(setValue).toHaveBeenCalledWith({
			[AnalyticsFilters.ROOM]: expect.objectContaining({
				value: {room: mockRoom},
			}),
		});

		fireEvent.click(screen.getByRole('button'));

		fireEvent.click(
			await screen.findByRole('menuitem', {name: /all-rooms/i})
		);

		await waitFor(() =>
			expect(screen.getByRole('button')).toHaveTextContent('all-rooms')
		);

		expect(setValue).toHaveBeenCalledWith({
			[AnalyticsFilters.ROOM]: expect.objectContaining({
				value: {room: null},
			}),
		});
	});
});
