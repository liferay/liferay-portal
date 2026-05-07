/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {openToast} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useState} from 'react';

import AccountSticker from '../../../../common/components/AccountSticker';
import RoomService from '../../../../common/services/RoomService';
import {IRoomObjectEntry} from '../../../../common/utils/types';
import {AnalyticsFilters, IAnalyticsRoomFilter} from '../../types';

interface IProps {
	filter: IAnalyticsRoomFilter;
	setValue: any;
}

export default function RoomAnalyticsFilter({filter, setValue}: IProps) {
	const [room, setRoom] = useState<IRoomObjectEntry | null>(null);
	const [rooms, setRooms] = useState<IRoomObjectEntry[]>([]);

	const getRooms = useCallback(async () => {
		try {
			const {items: rooms} = await RoomService.getRooms();

			setRooms(rooms);
		}
		catch (error: any) {
			openToast({
				message:
					error.message || Liferay.Language.get('unexpected-error'),
				type: 'danger',
			});
		}
	}, [setRooms]);

	useEffect(() => {
		if (room) {
			setValue({
				[AnalyticsFilters.ROOM]: {
					...filter,
					value: {
						room,
					},
				},
			});
		}
		else {
			setValue({
				[AnalyticsFilters.ROOM]: {
					...filter,
					value: {
						room: null,
					},
				},
			});
		}
	}, [filter, room, setValue]);

	useEffect(() => {
		Liferay.on('dsr-room-updated', getRooms);

		return () => {
			Liferay.detach('dsr-room-updated', getRooms);
		};
	}, [getRooms]);

	useEffect(() => {
		getRooms();
	}, [getRooms]);

	return (
		<DropDown
			className="room-filter-dropdown w-100"
			closeOnClick
			trigger={
				<ClayButton
					className="align-items-center d-flex font-weight-normal justify-content-between px-2 room-filter-button w-100"
					displayType="secondary"
				>
					<span className="align-items-center d-flex flex-grow-1 overflow-hidden">
						<AccountSticker
							logoURL={
								room?.r_accountToDSRRooms_accountEntry?.logoURL
							}
							name={
								room
									? room.name
									: Liferay.Language.get('all-rooms')
							}
							shape="user-icon"
							size="sm"
						/>

						<span className="pl-2 text-truncate">
							{room
								? room.name
								: Liferay.Language.get('all-rooms')}
						</span>
					</span>

					<ClayIcon
						className="flex-shrink-0 ml-2 mt-0"
						symbol="caret-double"
					/>
				</ClayButton>
			}
		>
			<DropDown.ItemList>
				<DropDown.Item
					active={!room}
					key="all-rooms"
					onClick={() => setRoom(null)}
				>
					<span>
						<AccountSticker
							name={Liferay.Language.get('all-rooms')}
							shape="user-icon"
							size="sm"
						/>
					</span>

					<span className="pl-2">
						{Liferay.Language.get('all-rooms')}
					</span>
				</DropDown.Item>

				{rooms.map((roomObjectEntry: IRoomObjectEntry) => (
					<DropDown.Item
						active={room?.id === roomObjectEntry.id}
						key={roomObjectEntry.id}
						onClick={() => setRoom(roomObjectEntry)}
					>
						<span>
							<AccountSticker
								logoURL={
									roomObjectEntry
										.r_accountToDSRRooms_accountEntry
										?.logoURL
								}
								name={roomObjectEntry.name}
								shape="user-icon"
								size="sm"
							/>
						</span>

						<span className="pl-2">{roomObjectEntry.name}</span>
					</DropDown.Item>
				))}
			</DropDown.ItemList>
		</DropDown>
	);
}
