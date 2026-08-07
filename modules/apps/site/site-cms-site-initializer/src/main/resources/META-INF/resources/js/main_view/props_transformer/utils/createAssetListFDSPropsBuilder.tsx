/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import React from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {getFileMimeTypeObjectDefinitionStickerValue} from './transformViewsItemProps';

export interface AdditionalProps {
	additionalAPIURLParameters?: string;
	fileMimeTypeCssClasses?: Record<string, string>;
	fileMimeTypeIcons?: Record<string, string>;
	objectDefinitionCssClasses?: Record<string, string>;
	objectDefinitionIcons?: Record<string, string>;
}

export interface AssetListFDSProps {
	additionalProps: AdditionalProps;
	apiURL?: string;
	id?: string;
	itemsActions?: IItemsActions[];
}

interface AssetListFDSConfig {
	renderSubtitle: (itemData: ISearchAssetObjectEntry) => React.ReactNode;
	titleRendererName: string;
}

export function createAssetListFDSPropsBuilder({
	renderSubtitle,
	titleRendererName,
}: AssetListFDSConfig) {
	function TitleRenderer({
		itemData,
		value,
	}: {
		itemData: ISearchAssetObjectEntry;
		value?: string;
	}) {
		return (
			<div className="d-flex flex-column">
				<span>{value}</span>

				<span className="font-weight-normal text-3">
					{renderSubtitle(itemData)}
				</span>
			</div>
		);
	}

	return function getAssetListFDSProps({
		additionalProps,
		itemsActions = [],
		...otherProps
	}: AssetListFDSProps) {
		const {additionalAPIURLParameters, ...remainingAdditionalProps} =
			additionalProps || {};

		return {
			...otherProps,
			additionalAPIURLParameters,
			additionalProps: remainingAdditionalProps,
			customRenderers: {
				listSection: [
					{
						component: TitleRenderer,
						name: titleRendererName,
						type: 'internal',
					} as IInternalRenderer,
				],
			},
			hideManagementBarInEmptyState: true,
			itemsActions,
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
						titleRendererName,
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
	};
}
