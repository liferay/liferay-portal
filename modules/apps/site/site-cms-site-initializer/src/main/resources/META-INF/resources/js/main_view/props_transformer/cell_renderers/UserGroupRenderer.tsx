/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

const UserGroupRenderer = ({
	itemData,
	value,
}: {
	itemData: any;
	value: string;
}) => {
	const groupCount = itemData.numberOfUserAccounts || 0;

	return (
		<span className="align-items-center d-flex">
			<ClaySticker
				className="c-mr-2"
				displayType="secondary"
				shape="circle"
				size="lg"
			>
				<ClayIcon symbol="users" />
			</ClaySticker>

			{value}

			<span className="ml-1">
				(
				{Liferay.Util.sub(
					Liferay.Language.get('x-members'),
					groupCount
				)}
				)
			</span>
		</span>
	);
};

export default UserGroupRenderer;
