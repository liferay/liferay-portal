/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	FrontendDataSetContext,
	filterItemActions,
	findAction,
} from '@liferay/frontend-data-set-web';
import React, {useContext} from 'react';

import {ActionItem} from '../../types';
import {BaseLinkRenderer, BaseLinkRendererProps} from './BaseLinkRenderer';

export default function LinkRenderer({
	actions,
	itemData,
	options: {actionId},
	stickerClassName,
	stickerStyle,
	symbol,
	...props
}: BaseLinkRendererProps & {
	actions: ActionItem[];
	options: {actionId: string};
	stickerClassName: string;
	stickerStyle?: React.CSSProperties;
	symbol: string;
}) {
	const {infoPanelOpen, selectable, selectedItemsKey, selectedItemsValue} =
		useContext(FrontendDataSetContext);

	const filteredActions = filterItemActions({
		actions,
		infoPanelOpen,
		itemData,
		selectable,
		selectedItemsKey,
		selectedItemsValue,
	});

	const action = findAction(filteredActions, actionId);

	return (
		<BaseLinkRenderer
			{...props}
			action={action}
			itemData={itemData}
			stickerClassName={stickerClassName}
			stickerStyle={stickerStyle}
			symbol={symbol}
		/>
	);
}
