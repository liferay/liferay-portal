/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IInternalRenderer, IItemsActions} from '@liferay/frontend-data-set-web';
import {openToast} from 'frontend-js-components-web';
import {escapeHTML} from 'frontend-js-web';
import React from 'react';

import ApiHelper from '../../../common/services/ApiHelper';
import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import {openCMSModal} from '../../../common/utils/openCMSModal';
import AssetNavigationModalContent from '../../modal/asset_navigation_view/AssetNavigationModalContent';
import SimpleActionLinkRenderer, {
	ActionItem,
} from '../cell_renderers/SimpleActionLinkRenderer';
import {getFileMimeTypeObjectDefinitionStickerValue} from './transformViewsItemProps';

export interface AdditionalProps {
	additionalAPIURLParameters?: string;
	contentViewURL?: string;
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
	searchAsYouType?: boolean;
	searchSuggestionsEnabled?: boolean;
}

export interface IAssetListItem {
	id?: number;
	title?: string;
}

interface AssetListFDSConfig<T> {
	renderSubtitle: (itemData: T) => React.ReactNode;
	titleRendererName: string;
}

const NESTED_FIELDS = 'embedded,systemProperties.objectDefinitionBrief';

const SEARCH_URL = '/o/search/v1.0/search';

function getAssetsByIdsURL(ids: number[]) {
	const searchParams = new URLSearchParams({
		emptySearch: 'true',
		filter: ids.map((id) => `entryClassPK eq ${id}`).join(' or '),
		nestedFields: NESTED_FIELDS,
		pageSize: String(ids.length),
	});

	return `${SEARCH_URL}?${searchParams}`;
}

export async function openAssetViewModal(
	additionalProps: AdditionalProps,
	itemData: IAssetListItem,
	items: IAssetListItem[]
) {
	const {data, error} = await ApiHelper.get<{
		items: ISearchAssetObjectEntry[];
	}>(getAssetsByIdsURL(items.flatMap(({id}) => id ?? [])));

	const searchAssetObjectEntries = items.flatMap(
		({id}) => data?.items.find(({embedded}) => embedded?.id === id) ?? []
	);

	if (!searchAssetObjectEntries.length) {
		openToast({
			message: error
				? escapeHTML(error)
				: Liferay.Language.get('an-unexpected-error-occurred'),
			type: 'danger',
		});

		return;
	}

	openCMSModal({
		contentComponent: () =>
			AssetNavigationModalContent({
				additionalProps,
				contentViewURL: additionalProps.contentViewURL ?? '',
				currentIndex: Math.max(
					searchAssetObjectEntries.findIndex(
						({embedded}) => embedded?.id === itemData.id
					),
					0
				),
				items: searchAssetObjectEntries,
			}),
		size: 'full-screen',
	});
}

function getViewItemAction(additionalProps: AdditionalProps): IItemsActions {
	return {
		data: {id: 'view'},
		icon: 'view',
		label: Liferay.Language.get('view'),
		onClick: ({
			itemData,
			items,
		}: {
			itemData: IAssetListItem;
			items: IAssetListItem[];
		}) => openAssetViewModal(additionalProps, itemData, items),
	};
}

export function createAssetListFDSPropsBuilder<T extends IAssetListItem>({
	renderSubtitle,
	titleRendererName,
}: AssetListFDSConfig<T>) {
	function TitleRenderer({
		actions,
		itemData,
		value,
	}: {
		actions: ActionItem[];
		itemData: T;
		value?: string;
	}) {
		return (
			<div className="d-flex flex-column">
				<SimpleActionLinkRenderer
					actions={actions}
					itemData={itemData}
					options={{actionId: 'edit'}}
					value={value ?? ''}
				/>

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
			itemsActions: [
				...itemsActions,
				getViewItemAction(remainingAdditionalProps),
			],
			searchAsYouType: otherProps.searchAsYouType ?? true,
			searchSuggestionsEnabled:
				otherProps.searchSuggestionsEnabled ?? true,
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
						item: T;
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

export function createTitleSortedFDSPropsTransformer<T extends object>(
	getAssetListFDSProps: (props: AssetListFDSProps) => T
) {
	return function TitleSortedFDSPropsTransformer(props: AssetListFDSProps) {
		return {
			...getAssetListFDSProps(props),
			sorts: [
				{
					active: true,
					direction: 'asc',
					key: 'title',
					label: Liferay.Language.get('title'),
				},
			],
		};
	};
}
