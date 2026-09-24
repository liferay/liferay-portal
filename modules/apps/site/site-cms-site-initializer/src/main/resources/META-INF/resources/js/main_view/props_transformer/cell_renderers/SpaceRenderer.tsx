/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IClayStickerProps} from '@clayui/sticker';
import {useFDSRecordVisit} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import React from 'react';

import SpaceSticker from '../../../common/components/SpaceSticker';
import {LogoColor} from '../../../common/types/Space';

const SpaceRenderer = ({
	href,
	itemData,
	logoColor,
	size = 'xs',
	value,
}: {
	href?: string;
	itemData?: any;
	logoColor?: LogoColor;
	size?: IClayStickerProps['size'];
	value: string;
}) => {
	const recordVisit = useFDSRecordVisit();

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
				displayType={logoColor}
				href={href}
				name={value}
				onLinkClick={() => recordVisit(itemData, {href, label: value})}
				size={size}
			/>
		</span>
	);
};

export default SpaceRenderer;
