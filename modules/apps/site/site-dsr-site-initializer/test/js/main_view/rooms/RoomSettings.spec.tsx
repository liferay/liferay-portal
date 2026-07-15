/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';
import {cleanup, render, screen, waitFor} from '@testing-library/react';
import React from 'react';

import RoomService from '../../../../src/main/resources/META-INF/resources/js/common/services/RoomService';
import RoomSettings from '../../../../src/main/resources/META-INF/resources/js/main_view/rooms/RoomSettings';

jest.mock('@liferay/site-cms-site-initializer', () => ({
	Toolbar: ({title}: {title: string}) => <h1>{title}</h1>,
	VerticalNavLayout: ({
		items,
	}: {
		items: Array<{id: string; label: string}>;
	}) => (
		<ul>
			{items.map((item) => (
				<li key={item.id}>{item.label}</li>
			))}
		</ul>
	),
}));

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/main_view/rooms/RoomGeneralSettings',
	() => ({__esModule: true, default: () => null})
);

jest.mock(
	'../../../../src/main/resources/META-INF/resources/js/common/services/RoomService',
	() => ({
		__esModule: true,
		default: {getRoom: jest.fn()},
	})
);

const room = {
	externalReferenceCode: 'ERC-1',
	friendlyURL: '/room-1',
	id: 1,
	name: 'Room 1',
	siteId: 40526,
};

describe('RoomSettings', () => {
	afterEach(() => {
		cleanup();

		jest.clearAllMocks();
	});

	it('loads the room and renders the settings navigation', async () => {
		(RoomService.getRoom as jest.Mock).mockResolvedValue(room);

		render(<RoomSettings backURL="/back" roomId={1} />);

		await waitFor(() => {
			expect(RoomService.getRoom).toHaveBeenCalledWith(1);
		});

		expect(screen.getByRole('heading')).toBeInTheDocument();

		expect(screen.getByText('general')).toBeInTheDocument();
	});
});
