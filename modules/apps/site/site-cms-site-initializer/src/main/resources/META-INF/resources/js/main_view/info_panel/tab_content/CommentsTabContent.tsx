/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React, {useContext} from 'react';

import {ISearchAssetObjectEntry} from '../../../common/types/AssetType';
import getAssetCommentsURLs from '../../../common/utils/getAssetCommentsURLs';
import CommentsPanel from '../../../content_editor/components/panels/CommentsPanel';
import {
	AssetTypeInfoPanelContext,
	IAssetTypeInfoPanelContext,
} from '../context';

const CommentsTabContent = () => {
	const {
		asset: {id},
		commentsProps,
		selectedAssets = [],
	}: IAssetTypeInfoPanelContext = useContext(AssetTypeInfoPanelContext);

	const [{entryClassName}]: ISearchAssetObjectEntry[] = selectedAssets;

	return (
		<CommentsPanel
			{...commentsProps}
			{...getAssetCommentsURLs(commentsProps, entryClassName, id)}
		/>
	);
};

export default CommentsTabContent;
