/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

import {getImage, getRoomTrendOption} from '../../../common/utils/roomTrend';

const RoomTrendRenderer = ({value}: {value?: number | null}) => {
	if (value === undefined || value === null) {
		return <>--</>;
	}

	const trendOption = getRoomTrendOption(value);

	return (
		<span className="align-items-center d-inline-flex text-truncate">
			<ClayIcon
				className="flex-shrink-0"
				color={trendOption.color}
				fontSize={16}
				spritemap={
					trendOption.useSpritemap
						? getImage('room_trend_icons.svg')
						: undefined
				}
				symbol={trendOption.icon}
			/>

			<span className="pl-2 text-truncate">{trendOption.label}</span>
		</span>
	);
};

export default RoomTrendRenderer;
