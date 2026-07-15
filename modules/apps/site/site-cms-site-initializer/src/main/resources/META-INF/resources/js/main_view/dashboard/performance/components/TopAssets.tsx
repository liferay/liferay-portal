/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Text} from '@clayui/core';
import ClayIcon from '@clayui/icon';
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
import React, {useContext, useRef} from 'react';

import {OBJECT_ENTRY_FOLDER_CLASS_NAME} from '../../../../common/utils/constants';
import {openCMSModal} from '../../../../common/utils/openCMSModal';
import AssetTypeInfoPanel from '../../../info_panel/AssetTypeInfoPanelContent';
import AssetNavigationModalContent from '../../../modal/asset_navigation_view/AssetNavigationModalContent';
import shareAction from '../../../props_transformer/actions/shareAction';
import {BaseCard} from '../../common/BaseCard';
import {PerformanceContext} from '../PerformanceContext';
import PerformanceService from '../PerformanceService';
import {DownloadButton} from './DownloadButton';

const TITLE_RENDERER = 'TopAssetsTitleRenderer';
const TREND_RENDERER = 'TopAssetsTrendRenderer';

const API_URL = '/o/search/v1.0/search';

const ADDITIONAL_API_URL_PARAMETERS =
	"emptySearch=true&filter=cmsSection eq 'contents'&nestedFields=embedded," +
	'systemProperties.objectDefinitionBrief';

function getAssetIcon(mimeType?: string) {
	if (!mimeType) {
		return 'document-text';
	}

	if (mimeType.startsWith('image/')) {
		return 'document-image';
	}

	if (mimeType.startsWith('video/')) {
		return 'document-video';
	}

	if (mimeType.includes('spreadsheet') || mimeType.includes('excel')) {
		return 'document-table';
	}

	return 'document-text';
}

const customRenderers = {
	tableCell: [
		{
			component: ({itemData, value}: {itemData: any; value: string}) => (
				<div className="align-items-center d-flex">
					<ClayIcon
						className="mr-2 text-secondary"
						symbol={getAssetIcon(itemData.mimeType)}
					/>

					<Text size={3} weight="semi-bold">
						{value}
					</Text>
				</div>
			),
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

const filters = [
	{
		apiURL: "/o/headless-asset-library/v1.0/asset-libraries?filter=type eq 'Space'",
		entityFieldType: 'string',
		id: 'groupIds',
		itemKey: 'siteId',
		itemLabel: 'name',
		label: Liferay.Language.get('space'),
		multiple: true,
		type: 'selection',
	},
];

const sorts = [
	{
		direction: 'asc' as const,
		key: 'title',
		label: Liferay.Language.get('title'),
	},
	{
		active: true,
		direction: 'desc' as const,
		key: 'dateModified',
		label: Liferay.Language.get('modified'),
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
					fieldName: 'embedded.title',
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
				collaboratorURL: collaboratorURLs[itemData.entryClassName],
				creator: itemData.embedded.creator,
				entryClassName: itemData.entryClassName,
				itemId: itemData.embedded.id,
				title: itemData.embedded?.title,
			});
		}
		else if (action?.data?.id === 'view') {
			event?.preventDefault();

			const filteredItems = items.filter(
				(item: any) =>
					item?.entryClassName !== OBJECT_ENTRY_FOLDER_CLASS_NAME
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
				additionalAPIURLParameters={ADDITIONAL_API_URL_PARAMETERS}
				apiURL={API_URL}
				customRenderers={customRenderers}
				emptyState={{
					description: Liferay.Language.get(
						'there-are-no-assets-created-in-the-space'
					),
					image: '/states/cms_empty_state.svg',
					title: Liferay.Language.get('no-assets-yet'),
				}}
				filters={filters}
				hideManagementBarInEmptyState={true}
				id="cmsPerformanceTopAssets"
				infoPanelComponent={TopAssetsInfoPanel}
				infoPanelContainerRef={infoPanelContainerRef}
				infoPanelPosition="fixed"
				itemsActions={itemsActions}
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
