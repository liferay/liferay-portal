/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import React from 'react';

export default function MessageHeader({message}: {message: string}) {
	return (
		<div className="d-flex flex-row font-weight-semi-bold">
			<div className="align-items-start d-inline-block ml-2 mt-2 text-2 text-primary">
				<ClayIcon spritemap={Liferay.Icons.spritemap} symbol="stars" />
			</div>

			<div className="m-2">{message}</div>
		</div>
	);
}
