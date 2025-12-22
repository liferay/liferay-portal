/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	IAssetObjectEntry,
	ISearchAssetObjectEntry,
} from '../../../common/types/AssetType';

export interface IAssetTypeInfoPanelContext {
	actions: ISearchAssetObjectEntry['actions'];
	asset: IAssetObjectEntry;
	assetLibrary?: {
		externalReferenceCode: string;
		groupId: number;
		name: string;
	};
	cmsGroupId: number | string;
	commentsProps: any;
	selectedAssets: ISearchAssetObjectEntry[];
	type: string;
}

export const AssetTypeInfoPanelContext = React.createContext(
	{} as IAssetTypeInfoPanelContext
);

AssetTypeInfoPanelContext.displayName = 'AssetTypeInfoPanelContext';
