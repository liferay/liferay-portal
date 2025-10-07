/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IClayStickerProps} from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

import SpaceSticker from '../../../common/components/SpaceSticker';

const SpaceRenderer = ({
	href,
	itemData,
	size,
	value,
}: {
	href?: string;
	itemData: {
		settings?: {logoColor: IClayStickerProps['displayType']};
	};
	size?: IClayStickerProps['size'];
	value: string;
}) => {
	return (
		<span
			className={classNames(
				'align-items-center',
				'd-flex',
				'space-renderer-sticker',
				{'list-group-title': !!href}
			)}
		>
			<SpaceSticker
				displayType={itemData.settings?.logoColor}
				href={href}
				name={value}
				size={size || 'xs'}
			/>
		</span>
	);
};

export default SpaceRenderer;
