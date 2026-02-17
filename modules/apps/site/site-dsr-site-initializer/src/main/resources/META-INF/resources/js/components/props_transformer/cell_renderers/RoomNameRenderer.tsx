/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {IRoomObjectEntry} from '../../../common/utils/types';

const RoomNameRenderer = ({data}: {data: IRoomObjectEntry}) => {
	const {name, r_accountToDSRRooms_accountEntry: account} = data;

	return (
		<div>
			<div className="text-weight-semi-bold">{name}</div>

			<div className="text-2 text-truncate">{account.name}</div>
		</div>
	);
};

export default RoomNameRenderer;
