/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayButton from '@clayui/button';
import DropDown from '@clayui/drop-down';
import ClayIcon from '@clayui/icon';
import {openToast} from 'frontend-js-components-web';
import React, {useCallback, useEffect, useState} from 'react';

import '../../../../css/components/RoomTrend.scss';
import RoomService from '../../../common/services/RoomService';
import {
	ROOM_TREND_OPTIONS,
	TTrendOptions,
	getImage,
	getRoomTrendOption,
} from '../../../common/utils/roomTrend';
import {IRoomObjectEntry} from '../../../common/utils/types';
import {
	AnalyticsFilters,
	TAnalyticsFilter,
	TRoomAnalyticsFilterValue,
} from '../types';
import AnalyticsFrame from './AnalyticsFrame';

function getDegrees(percentage: number) {
	const clampedPercentage = Math.max(0, Math.min(100, percentage));

	return Math.round(-90 + (clampedPercentage / 100) * 180);
}

const RoomTrend = () => {
	const [room, setRoom] = useState<IRoomObjectEntry | null>(null);
	const [trendStatus, setTrendStatus] = useState<TTrendOptions>(
		ROOM_TREND_OPTIONS[0]
	);

	const {color, icon, label, percentage, useSpritemap} = trendStatus;

	const updateRoomTrend = useCallback(
		(trend: number) => {
			if (!room) {
				return;
			}

			return RoomService.updateRoom(room.id, {
				trend,
			})
				.then((roomObjectEntry: IRoomObjectEntry) => {
					setTrendStatus(getRoomTrendOption(trend));

					Liferay.fire('dsr-room-updated', {room: roomObjectEntry});
				})
				.catch((error: any) => {
					openToast({
						message:
							error.message ||
							Liferay.Language.get('unexpected-error'),
						type: 'danger',
					});
				});
		},
		[room]
	);

	useEffect(() => {
		if (room) {
			setTrendStatus(getRoomTrendOption(room.trend));
		}

		return () => {};
	}, [room]);

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
				<AnalyticsFrame className="mt-4 room-trend-frame-height">
					<div className="align-items-center d-flex justify-content-between p-3 room-trend">
						<div>
							<div className="align-items-center d-flex mb-1">
								<p className="font-weight-semi-bold inline-item inline-item-before m-0">
									{Liferay.Language.get('room-trend')}
								</p>
							</div>

							<DropDown
								closeOnClick
								trigger={
									<ClayButton
										className="align-items-center d-flex font-weight-normal justify-content-between px-2 room-trend-button"
										displayType="secondary"
									>
										<span className="align-items-center d-flex flex-grow-1 overflow-hidden">
											<ClayIcon
												className="flex-shrink-0"
												color={color}
												fontSize={16}
												spritemap={
													useSpritemap
														? getImage(
																'room_trend_icons.svg'
															)
														: undefined
												}
												symbol={icon}
											/>

											<span className="pl-2 room-trend-button-text text-left text-truncate">
												{label}
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
									{ROOM_TREND_OPTIONS.map((option, index) => (
										<DropDown.Item
											active={
												room.trend === option.status
											}
											key={index}
											onClick={() =>
												updateRoomTrend(option.status)
											}
										>
											<span className="mr-4">
												<ClayIcon
													color={option.color}
													fontSize={16}
													spritemap={
														option.useSpritemap
															? getImage(
																	'room_trend_icons.svg'
																)
															: undefined
													}
													symbol={option.icon}
												/>
											</span>

											{option.label}
										</DropDown.Item>
									))}
								</DropDown.ItemList>
							</DropDown>
						</div>

						<div className="gauge-container inline-item-after ml-4">
							<img
								alt={Liferay.Language.get(
									'room-trend-semicircle'
								)}
								className="room-trend-semicircle"
								src={getImage('room_trend_semicircle.svg')}
							/>

							<img
								alt={Liferay.Language.get('room-trend-pointer')}
								className="room-trend-pointer"
								src={getImage('room_trend_pointer.svg')}
								style={{
									transform: `rotate(${getDegrees(percentage)}deg) translateZ(0)`,
								}}
							/>
						</div>
					</div>
				</AnalyticsFrame>
			) : null}
		</>
	);
};

export default RoomTrend;
