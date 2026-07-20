/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import ReviewDateRenderer from './cell_renderers/ReviewDateRenderer';
import {getFileMimeTypeObjectDefinitionStickerValue} from './utils/transformViewsItemProps';

interface AdditionalProps {
	additionalAPIURLParameters?: string;
	fileMimeTypeCssClasses?: Record<string, string>;
	fileMimeTypeIcons?: Record<string, string>;
	objectDefinitionCssClasses?: Record<string, string>;
	objectDefinitionIcons?: Record<string, string>;
}

function OverdueReviewTitleRenderer({
	itemData,
	value,
}: {
	itemData: {dateReview?: string};
	value?: string;
}) {
	return (
		<div className="d-flex flex-column">
			<span>{value}</span>

			<span className="font-weight-normal text-3">
				<ReviewDateRenderer itemData={itemData} />
			</span>
		</div>
	);
}

export default function OverdueReviewsFDSPropsTransformer({
	additionalProps,
	itemsActions = [],
	...otherProps
}: {
	additionalProps: AdditionalProps;
	apiURL?: string;
	itemsActions?: IItemsActions[];
}) {
	const {additionalAPIURLParameters, ...remainingAdditionalProps} =
		additionalProps || {};

	return {
		...otherProps,
		additionalAPIURLParameters,
		additionalProps: remainingAdditionalProps,
		customRenderers: {
			listSection: [
				{
					component: OverdueReviewTitleRenderer,
					name: 'overdueReviewTitle',
					type: 'internal',
				} as IInternalRenderer,
			],
		},
		hideManagementBarInEmptyState: true,
		itemsActions,
		sorts: [
			{
				active: true,
				direction: 'asc',
				key: 'dateReview',
				label: Liferay.Language.get('review-date'),
			},
			{
				direction: 'asc',
				key: 'title',
				label: Liferay.Language.get('title'),
			},
		],
		views: [
			{
				contentRenderer: 'list',
				default: true,
				label: Liferay.Language.get('list'),
				name: 'list',
				schema: {
					description: '',
					sticker: 'sticker',
					symbol: 'symbol',
					title: 'title',
					titleRendererName: 'overdueReviewTitle',
				},
				setItemComponentProps: ({
					item,
					props,
				}: {
					item: ISearchAssetObjectEntry;
					props: Record<string, unknown>;
				}) => ({
					...props,
					item: {
						...item,
						sticker: {
							className:
								getFileMimeTypeObjectDefinitionStickerValue(
									remainingAdditionalProps.fileMimeTypeCssClasses,
									remainingAdditionalProps.objectDefinitionCssClasses ??
										{},
									item
								),
						},
						symbol: getFileMimeTypeObjectDefinitionStickerValue(
							remainingAdditionalProps.fileMimeTypeIcons,
							remainingAdditionalProps.objectDefinitionIcons ??
								{},
							item
						),
					},
				}),
				thumbnail: 'list',
			},
		],
	};
}
