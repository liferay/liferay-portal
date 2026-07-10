/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import React from 'react';

export default function ProductNameRenderer({
	actions,
	itemData,
	value,
}: {
	actions: any[];
	itemData: any;
	value: string;
}) {
	const editAction = findAction(actions, 'edit');

	const href =
		editAction?.href && itemData?.embedded?.actions?.update
			? replaceTokens(editAction.href, itemData)
			: null;

	return (
		<span className="align-items-center d-flex table-list-title">
			<ClaySticker className="c-mr-2 content-icon-custom-structure flex-shrink-0 inline-item inline-item-before">
				<ClayIcon symbol="web-content" />
			</ClaySticker>

			{href ? (
				<ClayLink href={href}>{value}</ClayLink>
			) : (
				<span>{value}</span>
			)}
		</span>
	);
}
