/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {replaceTokens, useFDSRecordVisit} from '@liferay/frontend-data-set-web';
import React, {useMemo} from 'react';

import SharedIcon from '../../../common/components/SharedIcon';
import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {openSharedItemViewModal} from '../utils/openSharedItemViewModal';

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
	const recordVisit = useFDSRecordVisit();

	const {assetType, fileTypeIcon, fileTypeIconColor, siteName} = itemData;
	const title =
		value && value !== '' && value !== 'null'
			? value
			: Liferay.Language.get('untitled-asset');

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

	const isFolder = itemData?.className === OBJECT_ENTRY_FOLDER_CLASS_NAME;
	const visible = itemData?.visible;

	const shouldOpenModal =
		!itemData?.actionIds?.includes('UPDATE') && !isFolder && visible;

	const linkHref = useMemo(() => {
		const {actionId} = options;

		if (shouldOpenModal || !actions.length || !actionId || !visible) {
			return null;
		}

		const resolvedActionId = isFolder
			? `${actionId}Folder`
			: `${actionId}Edit`;

		const selectedAction = actions.find(
			({data}) => data?.id === resolvedActionId
		);

		if (!selectedAction?.href) {
			return null;
		}

		return replaceTokens(selectedAction.href, itemData);
	}, [actions, isFolder, itemData, options, shouldOpenModal, visible]);

	return (
		<span className="align-items-center c-gap-2 d-flex table-list-title">
			<ClaySticker className={`flex-shrink-0 ${iconColor}`}>
				<ClayIcon aria-hidden="true" symbol={icon} />
			</ClaySticker>

			{shouldOpenModal ? (
				<ClayLink
					aria-label={title}
					data-senna-off
					href="#"
					onClick={(event: React.MouseEvent) => {
						event.preventDefault();

						openSharedItemViewModal(itemData);
					}}
				>
					{title}
				</ClayLink>
			) : linkHref ? (
				<ClayLink
					aria-label={title}
					data-senna-off
					href={linkHref}
					onClick={() =>
						recordVisit(itemData, {href: linkHref, label: title})
					}
				>
					{title}
				</ClayLink>
			) : (
				<span>{title}</span>
			)}

			{siteName && <SharedIcon spaceName={siteName} />}
		</span>
	);
}
