/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import AssetTypeInfoPanelDefaultView from './AssetTypeInfoPanelDefaultView';
import AssetTypeInfoPanelFilesView from './AssetTypeInfoPanelFilesView';
import AssetTypeInfoPanelFolderView from './AssetTypeInfoPanelFolderView';
import {AssetTypeInfoPanelContext, IAssetTypeInfoPanelContext} from './context';
import {ASSET_TYPE} from './util/constants';

const AssetTypeInfoPanelBody = () => {
	const {objectEntries = [], type}: IAssetTypeInfoPanelContext = useContext(
		AssetTypeInfoPanelContext
	);

	return (
		<>
			{objectEntries.length > 1 || !objectEntries.length ? (
				<AssetTypeInfoPanelDefaultView />
			) : type === ASSET_TYPE.FOLDER ? (
				<AssetTypeInfoPanelFolderView />
			) : (
				<AssetTypeInfoPanelFilesView />
			)}
		</>
	);
};

export default AssetTypeInfoPanelBody;
