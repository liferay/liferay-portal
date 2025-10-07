/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	ISearchAssetObjectEntry,
	ISearchAssetTypeInformation,
} from '../../common/types/AssetType';
import AssetTypeInfoPanelBody from './AssetTypeInfoPanelBody';
import AssetTypeInfoPanelHeader from './AssetTypeInfoPanelHeader';
import {AssetTypeInfoPanelContext, IAssetTypeInfoPanelContext} from './context';

import '../../../css/components/AssetTypeInfoPanel.scss';
import {getBaseAssetInformation} from './util';

const AssetTypeInfoPanelContent = ({
	additionalProps: {assetLibraries, cmsGroupId, commentsProps},
	items: objectEntries,
}: {
	additionalProps: any;
	items: ISearchAssetObjectEntry[];
}) => {
	const assetInfo: ISearchAssetTypeInformation =
		objectEntries?.length === 1
			? getBaseAssetInformation(objectEntries[0])
			: {};

	const assetLibrary = assetLibraries.find(
		({groupId}: {groupId: number}) =>
			Number(groupId) === Number(objectEntries?.[0]?.embedded?.scopeId)
	);

	return (
		<>
			<AssetTypeInfoPanelContext.Provider
				value={
					{
						assetLibrary,
						cmsGroupId,
						commentsProps,
						objectEntries,
						...assetInfo,
					} as IAssetTypeInfoPanelContext
				}
			>
				<AssetTypeInfoPanelHeader />

				<AssetTypeInfoPanelBody />
			</AssetTypeInfoPanelContext.Provider>
		</>
	);
};

export default AssetTypeInfoPanelContent;
