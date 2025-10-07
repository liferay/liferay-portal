/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';

import {
	ISearchAssetObjectEntry,
	ISearchAssetTypeInformation,
} from '../../../common/types/AssetType';

export interface IAssetTypeInfoPanelContext
	extends ISearchAssetTypeInformation {
	assetLibrary?: {
		externalReferenceCode: string;
		groupId: number;
		name: string;
	};
	cmsGroupId?: string | null;
	commentsProps?: any;
	objectEntries?: ISearchAssetObjectEntry[];
}

const BASE_CONTEXT: IAssetTypeInfoPanelContext = {
	cmsGroupId: null,
	externalReferenceCode: null,
	icon: null,
	id: null,
	objectEntries: [],
	title: null,
	title_i18n: {},
	type: null,
};

export const AssetTypeInfoPanelContext = React.createContext(BASE_CONTEXT);

AssetTypeInfoPanelContext.displayName = 'AssetTypeInfoPanelContext';
