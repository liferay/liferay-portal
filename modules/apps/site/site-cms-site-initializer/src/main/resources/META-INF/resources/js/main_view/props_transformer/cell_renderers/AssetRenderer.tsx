/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import {findAction, replaceTokens} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import {dateUtils, sub} from 'frontend-js-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {getFileMimeTypeObjectDefinitionStickerValue} from '../utils/transformViewsItemProps';

interface ActionItem {
	data: {id: string};
	href?: string;
}

const formatDate = (date: string) => {
	return dateUtils.format(new Date(date), 'P p');
};

export default function AssetRenderer({
	actions,
	additionalProps,
	itemData,
	onViewClick,
	options,
	renderSubtitle,
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
	onViewClick?: (itemData: any) => void;
	options: {actionId: string};
	renderSubtitle?: (itemData: ISearchAssetObjectEntry) => React.ReactNode;
	value: string;
}) {
	const {actionId} = options;
	const title =
		value && value !== '' ? value : Liferay.Language.get('untitled-asset');

	const hasUpdatePermission = Boolean(itemData?.actions?.update);

	let formattedHref = null;
	let shouldOpenModal = false;

	if (actions.length && actionId) {
		if (hasUpdatePermission) {
			const selectedAction = findAction(actions, actionId);

			if (selectedAction?.href) {
				formattedHref = replaceTokens(selectedAction.href, itemData);
			}
		}
		else if (onViewClick) {
			shouldOpenModal = true;
		}
	}

	if (!formattedHref && !shouldOpenModal) {
		return <>{title}</>;
	}

	return (
		<div className="d-flex">
			<ClaySticker
				className={classNames(
					'c-mr-2',
					'flex-shrink-0',
					'inline-item',
					'inline-item-before',
					getFileMimeTypeObjectDefinitionStickerValue(
						additionalProps.fileMimeTypeCssClasses,
						additionalProps.objectDefinitionCssClasses,
						itemData
					)
				)}
			>
				<ClayIcon
					symbol={getFileMimeTypeObjectDefinitionStickerValue(
						additionalProps.fileMimeTypeIcons,
						additionalProps.objectDefinitionIcons,
						itemData
					)}
				/>
			</ClaySticker>

			<div>
				<div className="table-list-title">
					<ClayLink
						aria-label={title}
						className="text-decoration-underline"
						data-senna-off
						href={formattedHref || '#'}
						onClick={(event: React.MouseEvent) => {
							if (shouldOpenModal && onViewClick) {
								event.preventDefault();
								onViewClick(itemData);
							}
						}}
					>
						{title}
					</ClayLink>
				</div>

				<div className="text-3 text-secondary">
					{renderSubtitle
						? renderSubtitle(itemData)
						: sub(
								Liferay.Language.get('modified-at-x-by-x'),
								formatDate(itemData.dateModified),
								itemData.embedded?.modifiedBy?.name ??
									itemData.embedded?.creator?.name
							)}
				</div>
			</div>
		</div>
	);
}
