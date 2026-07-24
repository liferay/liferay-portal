/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';
import React from 'react';

import ItemWithStickerRenderer from './ItemWithStickerRenderer';

const SiteRenderer = ({
	itemData,
	value,
}: {
	itemData: {logo?: string};
	value: string;
}) => {
	return (
		<ItemWithStickerRenderer
			label={value}
			stickerContent={
				<ClaySticker.Image alt={value} src={itemData.logo} />
			}
		/>
	);
};

export default SiteRenderer;
