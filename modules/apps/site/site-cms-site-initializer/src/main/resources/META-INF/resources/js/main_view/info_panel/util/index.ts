/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ISearchAssetObjectEntry,
	ISearchAssetTypeInformation,
} from '../../../common/types/AssetType';
import {ASSET_TYPE} from './constants';

export function getBaseAssetInformation({
	actions: {
		get: {href},
	},
	embedded: {externalReferenceCode, id, title, title_i18n},
}: ISearchAssetObjectEntry): ISearchAssetTypeInformation {
	const baseAssetInfo: ISearchAssetTypeInformation = {
		externalReferenceCode,
		id,
		title,
		title_i18n,
	};

	if (href.includes('object-entry-folders')) {
		baseAssetInfo.icon = 'folder';
		baseAssetInfo.type = ASSET_TYPE.FOLDER;
	}
	else if (
		href.includes('basic-documents') ||
		href.includes('external-videos')
	) {
		baseAssetInfo.icon = 'document-image';
		baseAssetInfo.type = ASSET_TYPE.FILES;
	}
	else if (href.includes('basic-web-contents') || href.includes('blogs')) {
		baseAssetInfo.icon = 'forms';
		baseAssetInfo.type = ASSET_TYPE.CONTENTS;
	}

	return baseAssetInfo;
}
