/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import React, {useId} from 'react';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../common/utils/constants';
import {getFileMimeTypeObjectDefinitionStickerValue} from '../utils/transformViewsItemProps';

export interface ActionItem {
	data: {id: string};
	href?: string;
}

export default function SimpleActionLinkRenderer({
	actions,
	additionalProps,
	itemData,
	onViewClick,
	options,
	systemIconLabel,
	trailingIcon,
	value,
}: {
	actions: ActionItem[];
	additionalProps?: {
		fileMimeTypeCssClasses: Record<string, string>;
		fileMimeTypeIcons: Record<string, string>;
		objectDefinitionCssClasses: Record<string, string>;
		objectDefinitionIcons: Record<string, string>;
	};
	itemData: any;
	onViewClick?: (itemData: any) => void;
	options: {actionId: string};
	systemIconLabel?: string;
	trailingIcon?: React.ReactNode;
	value: string;
}) {
	const {actionId} = options;
	const systemIconId = useId();
	const title =
		value && value !== '' ? value : Liferay.Language.get('untitled-asset');

	const isFolder =
		itemData?.entryClassName === OBJECT_ENTRY_FOLDER_CLASS_NAME;

	const hasUpdatePermission = Boolean(itemData?.actions?.update);

	let formattedHref = null;
	let shouldOpenModal = false;

	if (actions.length && actionId) {
		if (!isFolder && hasUpdatePermission) {
			const selectedAction = findAction(actions, actionId);

			if (selectedAction?.href) {
				formattedHref = replaceTokens(selectedAction.href, itemData);
			}
		}
		else if (isFolder) {
			const selectedAction = findAction(actions, `${actionId}Folder`);

			if (selectedAction?.href) {
				formattedHref = replaceTokens(selectedAction.href, itemData);
			}
		}
		else if (onViewClick) {
			shouldOpenModal = true;
		}
	}

	const stickerElement = additionalProps && (
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
	);

	const showSystemIcon = Boolean(itemData.system && systemIconLabel);

	const systemIcon = itemData.system && systemIconLabel && (
		<span
			className="c-ml-2 lfr-portal-tooltip text-secondary"
			data-title={systemIconLabel}
		>
			<ClayIcon symbol="lock" />

			<span className="sr-only" id={systemIconId}>
				{systemIconLabel}
			</span>
		</span>
	);

	if (shouldOpenModal) {
		return (
			<div className="align-items-center d-flex table-list-title">
				{stickerElement}

				<ClayLink
					aria-describedby={showSystemIcon ? systemIconId : undefined}
					aria-label={title}
					data-senna-off
					href="#"
					onClick={(event: React.MouseEvent) => {
						event.preventDefault();

						onViewClick!(itemData);
					}}
				>
					{title}

					{systemIcon}
				</ClayLink>

				{trailingIcon}
			</div>
		);
	}

	if (!formattedHref) {
		if (systemIcon || trailingIcon) {
			return (
				<div className="align-items-center d-flex table-list-title">
					<span>{title}</span>

					{systemIcon}

					{trailingIcon}
				</div>
			);
		}

		return <>{title}</>;
	}

	return (
		<div className="align-items-center d-flex table-list-title">
			{stickerElement}

			<ClayLink
				aria-describedby={showSystemIcon ? systemIconId : undefined}
				aria-label={title}
				data-senna-off
				href={formattedHref}
			>
				{title}

				{systemIcon}
			</ClayLink>

			{trailingIcon}
		</div>
	);
}
