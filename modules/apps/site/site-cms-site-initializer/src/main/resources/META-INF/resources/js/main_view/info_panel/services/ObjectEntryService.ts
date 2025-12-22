/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from '../../../common/services/ApiHelper';
import {
	IAssetObjectEntry,
	ITaxonomyCategoryBrief,
} from '../../../common/types/AssetType';

export interface EntryCategorizationDTO extends IAssetObjectEntry {
	keywordsToAdd?: string[];
	keywordsToRemove?: string[];
	lastAddedBrief?: ITaxonomyCategoryBrief;
	lastRemovedBrief?: ITaxonomyCategoryBrief;
	taxonomyCategoryIdsToAdd?: number[];
	taxonomyCategoryIdsToRemove?: number[];
}

async function getObjectEntry(
	url: string,
	nestedFields = 'embeddedTaxonomyCategory,systemProperties.objectDefinitionBrief'
): Promise<RequestResult<IAssetObjectEntry>> {
	const getURL: URL = new URL(url, window.location.origin);

	if (nestedFields) {
		getURL.searchParams.append('nestedFields', nestedFields);
	}

	return await ApiHelper.get(getURL.toString());
}

async function patchObjectEntry(
	data: IAssetObjectEntry,
	url: string,
	nestedFields = 'embeddedTaxonomyCategory,systemProperties.objectDefinitionBrief'
): Promise<RequestResult<IAssetObjectEntry>> {
	const patchURL: URL = new URL(url, window.location.origin);

	if (nestedFields) {
		patchURL.searchParams.append('nestedFields', nestedFields);
	}

	return await ApiHelper.patch(data, patchURL.toString());
}

export default {
	getObjectEntry,
	patchObjectEntry,
};
