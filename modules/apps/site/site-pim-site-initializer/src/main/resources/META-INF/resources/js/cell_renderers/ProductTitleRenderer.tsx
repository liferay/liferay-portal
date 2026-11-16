/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayLink from '@clayui/link';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '@liferay/site-cms-site-initializer';
import React from 'react';

import ProductSticker from './ProductSticker';
import getImageURL from './getImageURL';

export default function ProductTitleRenderer({
	actions,
	itemData,
	value,
}: {
	actions: any[];
	itemData: any;
	value: string;
}) {
	const folder = itemData?.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	const action = findAction(actions, folder ? 'actionLinkFolder' : 'edit');

	const href =
		action?.href && (folder || itemData?.embedded?.actions?.update)
			? replaceTokens(action.href, itemData)
			: null;

	return (
		<span className="align-items-center d-flex table-list-title">
			<ProductSticker
				imageURL={getImageURL(itemData?.embedded?.images)}
				symbol={folder ? 'folder' : 'product'}
			/>

			{href ? (
				<ClayLink href={href}>{value}</ClayLink>
			) : (
				<span>{value}</span>
			)}
		</span>
	);
}
