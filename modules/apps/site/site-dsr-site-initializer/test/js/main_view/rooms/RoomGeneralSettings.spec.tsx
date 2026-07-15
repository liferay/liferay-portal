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

import RoomService from '../../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import RoomGeneralSettings from '../../../../src/main/resources/META-INF/resources/js/main_view/rooms/RoomGeneralSettings';

const mockNavigate = jest.fn();
const mockOpenToast = jest.fn();

jest.mock('@liferay/site-cms-site-initializer', () => ({
	FieldText: ({
		label,
		name,
		value,
	}: {
		label: string;
		name: string;
		value: string;
	}) => (
		<div>
			<label htmlFor={name}>{label}</label>

			<input id={name} name={name} readOnly value={value} />
		</div>
	),
	FieldWrapper: ({
		children,
		label,
	}: {
		children: React.ReactNode;
		label: string;
	}) => (
		<div>
			<span>{label}</span>

			{children}
		</div>
	),
	required: (value: string) => (value ? undefined : 'Required'),
	validate: () => ({}),
}));

jest.mock('frontend-js-components-web', () => ({
	openToast: (...args: unknown[]) => mockOpenToast(...args),
	useId: () => 'id-',
}));

jest.mock('frontend-js-web', () => ({
	...(jest.requireActual('frontend-js-web') as any),
	navigate: (...args: unknown[]) => mockNavigate(...args),
	sessionStorage: {
		TYPES: {NECESSARY: 'NECESSARY'},
		setItem: jest.fn(),
	},
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {updateRoomSettings: jest.fn()},
	})
);

const room = {
	externalReferenceCode: 'ERC-1',
	friendlyURL: '/room-1',
	id: 1,
	name: 'Room 1',
	siteId: 40526,
} as any;

describe('RoomGeneralSettings', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('renders the room fields with their values', () => {
		render(<RoomGeneralSettings backURL="/back" room={room} />);

		expect(screen.getByLabelText('name')).toHaveValue('Room 1');

		expect(screen.getByLabelText('external-reference-code')).toHaveValue(
			'ERC-1'
		);

		expect(screen.getByLabelText('site-id')).toHaveValue('40526');
	});

	it('saves the settings through the service', async () => {
		(RoomService.updateRoomSettings as jest.Mock).mockResolvedValue(room);

		render(<RoomGeneralSettings backURL="/back" room={room} />);

		fireEvent.click(screen.getByRole('button', {name: 'save'}));

		await waitFor(() => {
			expect(RoomService.updateRoomSettings).toHaveBeenCalledWith(1, {
				externalReferenceCode: 'ERC-1',
				friendlyURL: '/room-1',
				name: 'Room 1',
			});
		});

		expect(mockNavigate).toHaveBeenCalledWith('/back');
	});
});
