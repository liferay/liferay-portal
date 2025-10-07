/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import SearchBuilder from '../../core/SearchBuilder';
import {axios} from '../../utils/axios';
import fetcher from '../fetcher';

export default class HeadlessPublisherAsset {
	static async createPublisherAsset(body: unknown) {
		const response = await axios.post('o/c/publisherassetses', body);

		return response.data;
	}

	static async deletePublisherAsset(id: number | string) {
		return fetcher.delete(`o/c/publisherassetses/${id}`);
	}

	static getProductPublisherAssetsByProductId(productId: number | string) {
		const searchParams = new URLSearchParams({
			filter: SearchBuilder.eq(
				'r_productEntryToPublisherAssets_CProductId',
				productId
			),
		});

		return fetcher<APIResponse>(
			`o/c/publisherassetses?${searchParams.toString()}`
		);
	}
}
