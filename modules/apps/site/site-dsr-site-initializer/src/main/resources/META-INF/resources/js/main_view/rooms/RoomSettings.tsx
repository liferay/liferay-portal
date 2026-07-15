/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Toolbar, VerticalNavLayout} from '@liferay/site-cms-site-initializer';
import {sub} from 'frontend-js-web';
import React, {useEffect, useState} from 'react';

import RoomService from '../../common/services/RoomService';
import {IRoomObjectEntry} from '../../common/utils/types';
import RoomGeneralSettings from './RoomGeneralSettings';

export default function RoomSettings({
	backURL,
	roomId,
}: {
	backURL: string;
	roomId: number;
}) {
	const [room, setRoom] = useState<IRoomObjectEntry | null>(null);

	useEffect(() => {
		RoomService.getRoom(roomId).then((room) => {
			setRoom(room);
		});
	}, [roomId]);

	if (!room) {
		return null;
	}

	const verticalNavItems = [
		{
			component: <RoomGeneralSettings backURL={backURL} room={room} />,
			id: 'general',
			label: Liferay.Language.get('general'),
		},
	];

	return (
		<>
			<Toolbar
				backURL={backURL}
				title={sub(Liferay.Language.get('x-settings'), room.name)}
			/>

			<VerticalNavLayout items={verticalNavItems} />
		</>
	);
}
