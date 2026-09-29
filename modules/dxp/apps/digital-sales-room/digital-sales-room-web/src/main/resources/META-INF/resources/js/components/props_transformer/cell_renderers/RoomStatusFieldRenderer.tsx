/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import Label from '@clayui/label';
import React from 'react';

import {ROOM_STATUS} from '../../../common/utils/roomStatus';

const RoomStatusFieldRenderer = ({value}: {value?: number}) => {
	if (value === ROOM_STATUS.INACTIVE) {
		return (
			<Label displayType="warning" inverse>
				{Liferay.Language.get('archived')}
			</Label>
		);
	}

	return (
		<Label displayType="success" inverse>
			{Liferay.Language.get('active')}
		</Label>
	);
};

export default RoomStatusFieldRenderer;
