/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import React from 'react';

import formatActionURL from '../../../common/utils/formatActionURL';
import {getFileMimeTypeObjectDefinitionStickerValue} from '../utils/transformViewsItemProps';

const OBJECT_ENTRY_FOLDER_CLASS_NAME =
	'com.liferay.object.model.ObjectEntryFolder';

interface ActionItem {
	data: {id: string};
	href?: string;
}

export default function SimpleActionLinkRenderer({
	actions,
	additionalProps,
	itemData,
	options,
	value,
}: {
	actions: ActionItem[];
	additionalProps: {
		fileMimeTypeCssClasses: Record<string, string>;
		fileMimeTypeIcons: Record<string, string>;
		objectDefinitionCssClasses: Record<string, string>;
		objectDefinitionIcons: Record<string, string>;
	};
	itemData: any;
	options: {actionId: string};
	value: string;
}) {
	const {actionId} = options;
	const title =
		value && value !== '' ? value : Liferay.Language.get('untitled-asset');

	if (!actions.length || !actionId) {
		return <>{title}</>;
	}

	const isFolder =
		itemData?.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	const resolvedActionId = isFolder ? `${actionId}Folder` : actionId;

	const selectedAction = actions.find(
		({data}) => data?.id === resolvedActionId
	);

	if (!selectedAction?.href) {
		return <>{title}</>;
	}

	const formattedHref = formatActionURL(itemData, selectedAction.href);

	return (
		<div className="align-items-center d-flex table-list-title">
			<ClaySticker
				className={classNames(
					'c-mr-2',
					'flex-shrink-0',
					'inline-item',
					'inline-item-before',
					isFolder
						? 'file-icon-color-0'
						: getFileMimeTypeObjectDefinitionStickerValue(
								additionalProps.fileMimeTypeCssClasses,
								additionalProps.objectDefinitionCssClasses,
								itemData
							)
				)}
			>
				<ClayIcon
					symbol={
						isFolder
							? 'folder'
							: getFileMimeTypeObjectDefinitionStickerValue(
									additionalProps.fileMimeTypeIcons,
									additionalProps.objectDefinitionIcons,
									itemData
								)
					}
				/>
			</ClaySticker>

			<ClayLink aria-label={title} data-senna-off href={formattedHref}>
				{title}
			</ClayLink>
		</div>
	);
}
