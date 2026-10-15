/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLabel from '@clayui/label';
import ClaySticker from '@clayui/sticker';
import React from 'react';

export default function ProductRelationshipSelectorTitleRenderer({
	current = false,
	value,
}: {
	current?: boolean;
	value: string;
}) {
	return (
		<span className="align-items-center d-flex table-list-title">
			<ClaySticker className="c-mr-2 content-icon-custom-structure flex-shrink-0 inline-item inline-item-before">
				<ClayIcon symbol="product-configuration" />
			</ClaySticker>

			<span>{value}</span>

			{current && (
				<ClayLabel className="c-ml-auto" displayType="info" inverse>
					{Liferay.Language.get('viewing')}
				</ClayLabel>
			)}
		</span>
	);
}
