/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';
import moment from 'moment';
import React, {useEffect, useState} from 'react';

import '../../../../css/components/RoomGeneral.scss';
import AccountSticker from '../../../common/components/AccountSticker';
import {IRoomObjectEntry} from '../../../common/utils/types';
import {
	AnalyticsFilters,
	TAnalyticsFilter,
	TRoomAnalyticsFilterValue,
} from '../types';
import AnalyticsFrame from './AnalyticsFrame';

const RoomGeneral = () => {
	const [room, setRoom] = useState<IRoomObjectEntry | null>(null);

	useEffect(() => {
		const doSetRoom = ({filters}: {filters: TAnalyticsFilter}) => {
			const {room} = filters[AnalyticsFilters.ROOM]
				?.value as TRoomAnalyticsFilterValue;

			setRoom(room);
		};

		Liferay.on('dsr-filters-updated', doSetRoom);

		return () => {
			Liferay.detach('dsr-filters-updated', doSetRoom);
		};
	}, []);

	return (
		<>
			{room ? (
				<AnalyticsFrame className="mt-4 p-3 room-general-frame-height">
					<div className="align-items-center d-flex justify-content-between">
						<div className="align-items-center d-flex">
							<AccountSticker
								logoURL={
									room.r_accountToDSRRooms_accountEntry
										?.logoURL
								}
								name={room.name}
								shape="user-icon"
								size="xxl"
							/>

							<h2 className="mb-0 pl-3 py-4">{room.name}</h2>
						</div>

						<div className="align-items-center d-flex justify-content-between">
							<span
								className="px-3"
								dangerouslySetInnerHTML={{
									__html: sub(
										Liferay.Language.get('active-days-x'),
										`<strong>${moment()
											.startOf('day')
											.diff(
												moment(
													room.dateCreated
												).startOf('day'),
												'days'
											)}</strong>`
									),
								}}
							/>

							<span
								className="px-3"
								dangerouslySetInnerHTML={{
									__html: sub(
										Liferay.Language.get('first-visit-x'),
										`<strong>${moment(
											room.dateCreated
										).format('MM.DD.YYYY')}</strong>`
									),
								}}
							/>
						</div>
					</div>
				</AnalyticsFrame>
			) : null}
		</>
	);
};

export default RoomGeneral;
