/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClayIcon from '@clayui/icon';
import ClayLink from '@clayui/link';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {dateUtils, sub} from 'frontend-js-web';
import React from 'react';

import formatActionURL from '../../../common/utils/formatActionURL';
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

	const selectedAction = actions.find(({data}) => data?.id === actionId);

	if (!selectedAction?.href) {
		return <>{title}</>;
	}

	const formattedHref = formatActionURL(itemData, selectedAction.href);

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
						href={formattedHref}
					>
						<div>{title}</div>
					</ClayLink>
				</div>

				<div className="text-2 text-muted">
					{sub(
						Liferay.Language.get('modified-at-x-by-x'),
						formatDate(itemData.dateModified),
						itemData.embedded.creator.name
					)}
				</div>
			</div>
		</div>
	);
}
