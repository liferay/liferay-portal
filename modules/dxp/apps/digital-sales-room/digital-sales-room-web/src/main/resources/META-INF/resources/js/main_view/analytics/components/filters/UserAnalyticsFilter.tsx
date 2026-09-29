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
import {IRoomObjectEntry, IUserAccount} from '../../../../common/utils/types';
import {
	AnalyticsFilters,
	IAnalyticsUserFilter,
	TAnalyticsFilter,
	TRoomAnalyticsFilterValue,
} from '../../types';

interface IProps {
	filter: IAnalyticsUserFilter;
	setValue: any;
}

export default function UserAnalyticsFilter({filter, setValue}: IProps) {
	const [room, setRoom] = useState<IRoomObjectEntry | null>(null);
	const [user, setUser] = useState<IUserAccount | null>(null);
	const [users, setUsers] = useState<IUserAccount[]>([]);

	const doSetRoom = useCallback(
		({filters}: {filters: TAnalyticsFilter}) => {
			const {room: roomFilterValue} = filters[AnalyticsFilters.ROOM]
				?.value as TRoomAnalyticsFilterValue;

			if (JSON.stringify(room) !== JSON.stringify(roomFilterValue)) {
				setRoom(roomFilterValue);
			}
		},
		[room]
	);

	const getUsers = useCallback(async () => {
		if (!room) {
			setValue({
				[AnalyticsFilters.USER]: {
					...filter,
					value: [],
				},
			});

			setUser(null);
			setUsers([]);

			return;
		}

		try {
			const users = await RoomService.getRoomUserAccounts(room.id);

			if (!users.some((item) => item.id === user?.id)) {
				setUser(null);
			}

			setUsers(users);
		}
		catch (error: any) {
			openToast({
				message:
					error.message || Liferay.Language.get('unexpected-error'),
				type: 'danger',
			});
		}
	}, [filter, room, setUsers, setValue, user]);

	useEffect(() => {
		setValue({
			[AnalyticsFilters.USER]: {
				...filter,
				value: user ? [user.emailAddress] : [],
			},
		});
	}, [filter, setValue, user]);

	useEffect(() => {
		Liferay.on('dsr-filters-updated', doSetRoom);

		return () => {
			Liferay.detach('dsr-filters-updated', doSetRoom);
		};
	}, [doSetRoom]);

	useEffect(() => {
		getUsers();
	}, [getUsers]);

	return (
		<DropDown
			className="ml-3 user-filter-dropdown"
			closeOnClick
			trigger={
				<ClayButton
					className="align-items-center d-flex font-weight-normal justify-content-between px-2 user-filter-button w-100"
					displayType="secondary"
				>
					<span className="align-items-center d-flex flex-grow-1 overflow-hidden">
						<AccountSticker
							logoURL={user?.image}
							name={
								user
									? user.name
									: Liferay.Language.get('all-users')
							}
							shape="circle"
							size="sm"
						/>

						<span className="pl-2 text-truncate">
							{user
								? user.name
								: Liferay.Language.get('all-users')}
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
					active={!user}
					key="all-users"
					onClick={() => setUser(null)}
				>
					<span>
						<AccountSticker
							name={Liferay.Language.get('all-users')}
							shape="circle"
							size="sm"
						/>
					</span>

					<span className="pl-2">
						{Liferay.Language.get('all-users')}
					</span>
				</DropDown.Item>

				{users.map((userAccount: IUserAccount) => (
					<DropDown.Item
						active={user?.id === userAccount.id}
						key={userAccount.id}
						onClick={() => setUser(userAccount)}
					>
						<span>
							<AccountSticker
								logoURL={userAccount.image}
								name={userAccount.name}
								shape="circle"
								size="sm"
							/>
						</span>

						<span className="pl-2">{userAccount.name}</span>
					</DropDown.Item>
				))}
			</DropDown.ItemList>
		</DropDown>
	);
}
