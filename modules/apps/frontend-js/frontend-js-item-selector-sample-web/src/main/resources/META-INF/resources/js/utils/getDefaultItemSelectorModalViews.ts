/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IView} from '@liferay/frontend-data-set-web';

import {assetLibraryViews, documentViews, userViews} from './defaultViews';

export enum EItemSelectorModalViewsConfig {
	ASSET_LIBRARIES = 'assetLibraries',
	DOCUMENTS = 'documents',
	USER_ACCOUNTS = 'userAccounts',
}

export const getDefaultItemSelectorModalViews = function ({
	viewsConfig,
}: {
	viewsConfig: `${EItemSelectorModalViewsConfig}` | IView[];
}): any {
	if (typeof viewsConfig === 'object') {
		return viewsConfig;
	}
	else if (viewsConfig === EItemSelectorModalViewsConfig.ASSET_LIBRARIES) {
		return assetLibraryViews;
	}
	else if (viewsConfig === EItemSelectorModalViewsConfig.DOCUMENTS) {
		return documentViews;
	}
	else if (viewsConfig === EItemSelectorModalViewsConfig.USER_ACCOUNTS) {
		return userViews;
	}
};
