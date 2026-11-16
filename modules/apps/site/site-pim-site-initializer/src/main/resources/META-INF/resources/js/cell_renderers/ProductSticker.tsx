/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

export default function ProductSticker({
	imageURL,
	symbol = 'product',
}: {
	imageURL?: string | null;
	symbol?: string;
}) {
	return (
		<ClaySticker
			className={`c-mr-2 flex-shrink-0 inline-item inline-item-before ${imageURL ? '' : 'content-icon-custom-structure'}`}
		>
			{imageURL ? (
				<ClaySticker.Image alt="" src={imageURL} />
			) : (
				<ClayIcon symbol={symbol} />
			)}
		</ClaySticker>
	);
}
