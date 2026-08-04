/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import {
	getPercentage,
	getStatsColor,
	getStatsIcon,
} from '@liferay/analytics-reports-js-components-web';
import {
	FrontendDataSet,
	IInfoPanelComponent,
	IItemsActions,
} from '@liferay/frontend-data-set-web';
import classNames from 'classnames';
import React, {useContext, useRef} from 'react';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../common/utils/constants';
import {openCMSModal} from '../../../../common/utils/openCMSModal';
import AssetTypeInfoPanel from '../../../info_panel/AssetTypeInfoPanelContent';
import AssetNavigationModalContent from '../../../modal/asset_navigation_view/AssetNavigationModalContent';
import shareAction from '../../../props_transformer/actions/shareAction';
import {getFileMimeTypeObjectDefinitionStickerValue} from '../../../props_transformer/utils/transformViewsItemProps';
import {BaseCard} from '../../common/BaseCard';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {DownloadButton} from './DownloadButton';

const TITLE_RENDERER = 'TopAssetsTitleRenderer';
const TREND_RENDERER = 'TopAssetsTrendRenderer';

const API_URL = '/o/analytics-cms-rest/v1.0/performance-top-asset';

const NESTED_FIELDS =
	'nestedFields=embedded,file.metadata,file.previewURL,file.thumbnailURL,' +
	'systemProperties.objectDefinitionBrief';

function TitleCell({itemData, value}: {itemData: any; value: string}) {
	const {additionalProps} = useContext(PerformanceContext);

	return (
		<div className="align-items-center d-flex">
			{additionalProps && itemData.embedded ? (
				<ClaySticker
					className={classNames(
						'flex-shrink-0',
						'mr-2',
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
			) : (
				<ClaySticker className="flex-shrink-0 mr-2 text-secondary">
					<ClayIcon symbol="document-text" />
				</ClaySticker>
			)}

			<Text size={3} weight="semi-bold">
				{value}
			</Text>
		</div>
	);
}

const customRenderers = {
	tableCell: [
		{
			component: TitleCell,
			name: TITLE_RENDERER,
			type: 'internal' as const,
		},
		{
			component: ({itemData}: {itemData: any}) => {
				const {trend} = itemData;

				if (!trend) {
					return null;
				}

				const icon = getStatsIcon(trend.percentage);

				const percentage = `${getPercentage(trend.percentage)}%`;

				return (
					<Text color={getStatsColor(trend.classification)} size={3}>
						{icon && (
							<ClayIcon
								aria-label={icon}
								className="mr-1"
								symbol={icon}
							/>
						)}

						{percentage}
					</Text>
				);
			},
			name: TREND_RENDERER,
			type: 'internal' as const,
		},
	],
};

const sorts = [
	{
		direction: 'asc' as const,
		key: 'assetTitle',
		label: Liferay.Language.get('title'),
	},
	{
		active: true,
		direction: 'desc' as const,
		key: 'viewsMetric',
		label: Liferay.Language.get('views'),
	},
];

const itemsActions: IItemsActions[] = [
	{
		data: {id: 'view'},
		icon: 'view',
		label: Liferay.Language.get('view'),
	},
	{
		data: {id: 'show-details'},
		icon: 'info-circle-open',
		label: Liferay.Language.get('show-details'),
		target: 'infoPanel',
	},
	{
		data: {id: 'share'},
		icon: 'share',
		label: Liferay.Language.get('share'),
	},
];

const views = [
	{
		contentRenderer: 'table',
		default: true,
		label: Liferay.Language.get('table'),
		name: 'table',
		schema: {
			fields: [
				{
					contentRenderer: TITLE_RENDERER,
					fieldName: 'title',
					label: Liferay.Language.get('title'),
				},
				{
					contentRenderer: TREND_RENDERER,
					fieldName: 'trend',
					label: Liferay.Language.get('trend'),
				},
				{
					fieldName: 'engagement',
					label: Liferay.Language.get('engagement-rate'),
				},
				{
					fieldName: 'impressions',
					label: Liferay.Language.get('impressions'),
				},
				{
					fieldName: 'views',
					label: Liferay.Language.get('views'),
				},
				{
					fieldName: 'downloads',
					label: Liferay.Language.get('downloads'),
				},
			],
		},
		thumbnail: 'table',
	},
];

export function TopAssets() {
	const {additionalProps, range, space} = useContext(PerformanceContext);

	const depotEntryIds = space.value === 'all' ? undefined : [space.value];

	const additionalAPIURLParameters = [
		NESTED_FIELDS,
		`rangeKey=${range.rangeKey}`,
		...(depotEntryIds ?? []).map((id) => `depotEntryIds=${id}`),
	].join('&');

	const infoPanelContainerRef = useRef<HTMLElement | null>(
		document.querySelector<HTMLElement>('.cms-section')
	);

	function onActionDropdownItemClick({
		action,
		event,
		itemData,
		items,
	}: {
		action: any;
		event: Event;
		itemData: any;
		items: any;
	}) {
		if (!additionalProps) {
			return;
		}

		if (action?.data?.id === 'share') {
			const {autocompleteURL, collaboratorURLs} = additionalProps;

			shareAction({
				autocompleteURL,
				collaboratorURL: collaboratorURLs[itemData.className],
				creator: itemData.embedded.creator,
				entryClassName: itemData.className,
				itemId: itemData.embedded.id,
				title: itemData.embedded?.title,
			});
		}
		else if (action?.data?.id === 'view') {
			event?.preventDefault();

			const filteredItems = items.filter(
				(item: any) =>
					item?.className !== OBJECT_ENTRY_FOLDER_CLASS_NAME
			);

			const currentItemPos = filteredItems.findIndex(
				(item: any) => item.embedded.id === itemData.embedded.id
			);

			openCMSModal({
				contentComponent: () =>
					AssetNavigationModalContent({
						additionalProps,
						contentViewURL: additionalProps.contentViewURL,
						currentIndex: currentItemPos,
						items: filteredItems,
					}),
				size: 'full-screen',
			});
		}
	}

	return (
		<BaseCard
			Preferences={
				<DownloadButton
					href={PerformanceService.getTopAssetsExportURL({
						depotEntryIds,
						rangeKey: range.rangeKey,
					})}
				/>
			}
			description={Liferay.Language.get(
				'list-of-assets-with-the-most-visitors-interactions'
			)}
			title={Liferay.Language.get('top-assets')}
			uppercaseTitle={false}
		>
			<FrontendDataSet
				additionalAPIURLParameters={additionalAPIURLParameters}
				apiURL={API_URL}
				customRenderers={customRenderers}
				emptyState={{
					description: Liferay.Language.get(
						'there-are-no-assets-created-in-the-space'
					),
					image: '/states/cms_empty_state.svg',
					title: Liferay.Language.get('no-assets-yet'),
				}}
				hideManagementBarInEmptyState={true}
				id="cmsPerformanceTopAssets"
				infoPanelComponent={TopAssetsInfoPanel}
				infoPanelContainerRef={infoPanelContainerRef}
				infoPanelPosition="fixed"
				itemsActions={itemsActions}
				key={additionalAPIURLParameters}
				onActionDropdownItemClick={onActionDropdownItemClick}
				pagination={{initialDelta: 20}}
				selectedItemsKey="embedded.id"
				selectionType="multiple"
				showManagementBar={true}
				showPagination={true}
				showSearch={true}
				sorts={sorts}
				style="fluid"
				views={views}
			/>
		</BaseCard>
	);
}

function TopAssetsInfoPanel({items = []}: IInfoPanelComponent) {
	const {additionalProps} = useContext(PerformanceContext);

	if (!additionalProps) {
		return null;
	}

	return (
		<AssetTypeInfoPanel
			additionalProps={additionalProps}
			dataSetId="cmsPerformanceTopAssets"
			items={items}
		/>
	);
}
