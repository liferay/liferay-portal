/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

export default function AuthorCellRenderer({value}: {value: string}) {
	return (
		<span className="seo-studio-affected-pages-author">
			<ClaySticker
				className="mr-2"
				displayType="outline-9"
				shape="circle"
				size="sm"
			>
				<ClayIcon symbol="user" />
			</ClaySticker>

			{value}
		</span>
	);
}
