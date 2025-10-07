/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper, {RequestResult} from '../../../common/services/ApiHelper';
import {IAssetObjectEntry} from '../../../common/types/AssetType';

export type EntryCategorizationDTO = {
	keywords?: IAssetObjectEntry['keywords'];
	lastAddedBrief?: any;
	taxonomyCategoryBriefs?: IAssetObjectEntry['taxonomyCategoryBriefs'];
	taxonomyCategoryIds?: IAssetObjectEntry['taxonomyCategoryIds'];
};

async function getObjectEntry(
	url: string
): Promise<RequestResult<IAssetObjectEntry>> {
	return await ApiHelper.get(`${url}?nestedFields=embeddedTaxonomyCategory`);
}

async function patchObjectEntry(
	data: EntryCategorizationDTO,
	url: string
): Promise<RequestResult<IAssetObjectEntry>> {
	return await ApiHelper.patch(
		data,
		`${url}?nestedFields=embeddedTaxonomyCategory`
	);
}

export default {
	getObjectEntry,
	patchObjectEntry,
};
