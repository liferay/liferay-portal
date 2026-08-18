/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

export default function ProductRelationshipSelectorNameRenderer({
	value,
}: {
	value: string;
}) {
	return (
		<span className="align-items-center d-flex table-list-title">
			<ClaySticker className="c-mr-2 content-icon-custom-structure flex-shrink-0 inline-item inline-item-before">
				<ClayIcon symbol="web-content" />
			</ClaySticker>

			<span>{value}</span>
		</span>
	);
}
