/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClaySticker from '@clayui/sticker';
import React from 'react';

const ItemWithStickerRenderer = ({
	label,
	stickerContent,
	suffix,
}: {
	label: string;
	stickerContent: React.ReactNode;
	suffix?: React.ReactNode;
}) => {
	return (
		<span className="align-items-center d-flex">
			<ClaySticker
				className="c-mr-2"
				displayType="secondary"
				shape="circle"
				size="lg"
			>
				{stickerContent}
			</ClaySticker>

			{label}

			{suffix}
		</span>
	);
};

export default ItemWithStickerRenderer;
