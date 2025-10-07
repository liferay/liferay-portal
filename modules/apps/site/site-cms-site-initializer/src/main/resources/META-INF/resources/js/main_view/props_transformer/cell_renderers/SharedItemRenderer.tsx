/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {ClayTooltipProvider} from '@clayui/tooltip';
import {sub} from 'frontend-js-web';
import React, {useMemo} from 'react';

import formatActionURL from '../../../common/utils/formatActionURL';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

interface ActionItem {
	data: {id: string};
	href?: string;
}

export default function SharedItemRenderer({
	actions,
	itemData,
	options,
	value,
}: {
	actions: ActionItem[];
	itemData: any;
	options: {actionId: string};
	value: string;
}) {
	const {assetType, fileTypeIcon, fileTypeIconColor, siteName} = itemData;

	let icon;
	let iconColor;

	if (fileTypeIcon && fileTypeIconColor) {
		icon = fileTypeIcon;
		iconColor = fileTypeIconColor;
	}
	else if (assetType?.includes('Web Content')) {
		icon = 'forms';
		iconColor = 'content-icon-basic-content';
	}
	else if (assetType?.includes('Blog')) {
		icon = 'blogs';
		iconColor = 'content-icon-blog';
	}
	else {
		icon = 'web-content';
		iconColor = 'content-icon-web-content';
	}

	const linkHref = useMemo(() => {
		const {actionId} = options;

		if (!actions.length || !actionId) {
			return null;
		}

		const isFolder = itemData?.className === OBJECT_ENTRY_FOLDER_CLASS_NAME;
		const isUpdate = itemData?.actionIds?.includes('UPDATE');

		const resolvedActionId = isFolder
			? `${actionId}Folder`
			: isUpdate
				? `${actionId}Edit`
				: actionId;

		const selectedAction = actions.find(
			({data}) => data?.id === resolvedActionId
		);

		if (!selectedAction?.href) {
			return null;
		}

		return formatActionURL(itemData, selectedAction.href);
	}, [actions, itemData, options]);

	return (
		<span className="align-items-center c-gap-2 d-flex table-list-title">
			<ClaySticker className={`flex-shrink-0 ${iconColor}`}>
				<ClayIcon aria-hidden="true" symbol={icon} />
			</ClaySticker>

			{linkHref ? (
				<ClayLink aria-label={value} data-senna-off href={linkHref}>
					{value}
				</ClayLink>
			) : (
				<span>{value}</span>
			)}

			{siteName && (
				<ClayTooltipProvider>
					<ClaySticker
						className="flex-shrink-0"
						data-tooltip-align="top"
						displayType="unstyled"
						title={sub(
							Liferay.Language.get('shared-from-x'),
							`"${siteName}"`
						)}
					>
						<ClayIcon className="text-secondary" symbol="users" />
					</ClaySticker>
				</ClayTooltipProvider>
			)}
		</span>
	);
}
