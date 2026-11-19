/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import React from 'react';

export default function ProductSticker({
	className = 'c-mr-2',
	imageURL,
	size,
	symbol = 'product',
}: {
	className?: string;
	imageURL?: string | null;
	size?: React.ComponentProps<typeof ClaySticker>['size'];
	symbol?: string;
}) {
	return (
		<ClaySticker
			className={`${className} flex-shrink-0 inline-item inline-item-before ${imageURL ? '' : 'content-icon-custom-structure'}`}
			size={size}
		>
			{imageURL ? (
				<ClaySticker.Image alt="" src={imageURL} />
			) : (
				<ClayIcon symbol={symbol} />
			)}
		</ClaySticker>
	);
}
