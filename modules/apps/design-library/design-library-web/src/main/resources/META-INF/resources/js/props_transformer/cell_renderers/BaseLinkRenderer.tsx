/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {IItemsActions, formatActionURL} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import React from 'react';

import {DesignLibrary} from '../../types';

export interface BaseLinkRendererProps {
	action?: IItemsActions;
	itemData: DesignLibrary;
	value: string;
}

export function BaseLinkRenderer({
	action,
	itemData,
	stickerClassName,
	stickerStyle,
	symbol,
	value,
}: BaseLinkRendererProps & {
	stickerClassName: string;
	stickerStyle?: React.CSSProperties;
	symbol: string;
}) {
	if (!action || !action?.href) {
		return <>{value}</>;
	}

	const formattedHref = formatActionURL(action.href, itemData, action.target);

	return (
		<div className="align-items-center d-flex table-list-title">
			<ClaySticker
				className={classNames(
					'c-mr-2',
					'flex-shrink-0',
					'inline-item',
					'inline-item-before',
					stickerClassName
				)}
				style={stickerStyle}
			>
				<ClayIcon symbol={symbol} />
			</ClaySticker>

			<ClayLink aria-label={value} data-senna-off href={formattedHref}>
				{value}
			</ClayLink>
		</div>
	);
}
