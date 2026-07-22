/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {ISearchAssetObjectEntry} from '../../common/types/AssetType';
import {AssetTypeInfoPanelContext, IAssetTypeInfoPanelContext} from './context';

import '../../../css/components/AssetTypeInfoPanel.scss';

import {SidePanel} from '@clayui/core';
import ClayIcon from '@clayui/icon';
import ClaySticker from '@clayui/sticker';
import classNames from 'classnames';
import {sub} from 'frontend-js-web';

import {getFileMimeTypeObjectDefinitionStickerValue} from '../props_transformer/utils/transformViewsItemProps';
import AssetTypeInfoPanelEmptyState from './AssetTypeInfoPanelEmptyState';
import AssetTypeInfoPanelFilesView from './AssetTypeInfoPanelFilesView';
import AssetTypeInfoPanelFolderView from './AssetTypeInfoPanelFolderView';
import {getAssetType} from './util';
import {ASSET_TYPE} from './util/constants';

const AssetTypeInfoPanelContent = ({
	additionalProps: {
		breadcrumbProps,
		candidateAssetLibraries,
		cmsGroupId,
		commentsProps,
		...otherProps
	},
	dataSetId,
	items: selectedAssets = [],
}: {
	additionalProps: any;
	dataSetId?: string;
	items: ISearchAssetObjectEntry[];
}) => {
	if (selectedAssets?.length !== 1) {
		return (
			<>
				<SidePanel.Header>
					<SidePanel.Title>
						<span className="inline-flex text-nowrap">
							{!selectedAssets.length
								? Liferay.Language.get('no-assets-selected')
								: sub(
										Liferay.Language.get(
											'x-assets-selected'
										),
										selectedAssets.length
									)}
						</span>
					</SidePanel.Title>
				</SidePanel.Header>

				<SidePanel.Body>
					<AssetTypeInfoPanelEmptyState
						selected={selectedAssets.length}
					/>
				</SidePanel.Body>
			</>
		);
	}

	const asset = selectedAssets[0].embedded;
	const stickerClassName = classNames(
		getFileMimeTypeObjectDefinitionStickerValue(
			otherProps.fileMimeTypeCssClasses,
			otherProps.objectDefinitionCssClasses,
			selectedAssets[0]
		)
	);
	const stickerSymbol = getFileMimeTypeObjectDefinitionStickerValue(
		otherProps.fileMimeTypeIcons,
		otherProps.objectDefinitionIcons,
		selectedAssets[0]
	);

	const type = getAssetType(asset);

	return (
		<AssetTypeInfoPanelContext.Provider
			value={
				{
					actions: selectedAssets[0].actions,
					asset,
					assetLibrary: candidateAssetLibraries.find(
						({groupId}: {groupId: number}) =>
							Number(groupId) === Number(asset.scopeId)
					),
					breadcrumbProps,
					cmpProjectAssetRelationshipObjectDefinitionId:
						otherProps.cmpProjectAssetRelationshipObjectDefinitionId,
					cmpProjectObjectDefinitionId:
						otherProps.cmpProjectObjectDefinitionId,
					cmpProjectViewURL: otherProps.cmpProjectViewURL,
					cmpTaskObjectDefinitionId:
						otherProps.cmpTaskObjectDefinitionId,
					cmpTaskViewURL: otherProps.cmpTaskViewURL,
					cmsGroupId,
					commentsProps,
					dataSetId,
					entryClassName: selectedAssets[0].entryClassName,
					selectedAssets,
					type,
				} as IAssetTypeInfoPanelContext
			}
		>
			<SidePanel.Header>
				<SidePanel.Title>
					<span className="inline-flex text-nowrap">
						<ClaySticker className={stickerClassName}>
							<ClayIcon symbol={stickerSymbol} />
						</ClaySticker>

						<span className="inline-item text-truncate-inline">
							<h3 className="inline-item-after mb-0 text-truncate">
								{asset?.title_i18n?.[
									Liferay.ThemeDisplay.getLanguageId()
								] ||
									asset?.title ||
									Liferay.Language.get('untitled-asset')}
							</h3>
						</span>
					</span>
				</SidePanel.Title>
			</SidePanel.Header>

			{type === ASSET_TYPE.FOLDER ? (
				<AssetTypeInfoPanelFolderView />
			) : (
				<AssetTypeInfoPanelFilesView />
			)}
		</AssetTypeInfoPanelContext.Provider>
	);
};

export default AssetTypeInfoPanelContent;
