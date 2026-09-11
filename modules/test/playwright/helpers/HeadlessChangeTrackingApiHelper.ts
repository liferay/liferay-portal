/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {CTCollectionAPI} from '@liferay/change-tracking-rest-client-js';

import getRandomString from '../utils/getRandomString';
import {ApiHelpers, DataApiHelpers} from './ApiHelpers';

export class HeadlessChangeTrackingApiHelper {
	readonly apiHelpers: ApiHelpers;
	readonly basePath: string;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
		this.basePath = 'change-tracking-rest/v1.0';
	}

	async checkoutCTCollection(ctCollectionId: number) {
		const ctCollectionAPIClient =
			await this.apiHelpers.buildRestClient(CTCollectionAPI);

		return await ctCollectionAPIClient.postCTCollectionCheckout(
			ctCollectionId
		);
	}

	async createCTCollection(name: string) {
		const requestBody = {
			description: '',
			externalReferenceCode: getRandomString(),
			name,
			status: {code: 0, label: 'string', label_i18n: 'string'},
		};

		const ctCollectionAPIClient =
			await this.apiHelpers.buildRestClient(CTCollectionAPI);

		const ctCollection =
			await ctCollectionAPIClient.postCTCollection(requestBody);

		if (
			this.apiHelpers instanceof DataApiHelpers &&
			ctCollection?.body?.id
		) {
			this.apiHelpers.data.push({
				id: ctCollection.body.id,
				type: 'ctCollection',
			});
		}

		return ctCollection;
	}

	async deleteCTCollection(ctCollectionId: number) {
		const ctCollectionAPIClient =
			await this.apiHelpers.buildRestClient(CTCollectionAPI);

		return await ctCollectionAPIClient.deleteCTCollection(ctCollectionId);
	}

	async publishCTCollection(ctCollectionId: number) {
		const ctCollectionAPIClient =
			await this.apiHelpers.buildRestClient(CTCollectionAPI);

		return await ctCollectionAPIClient.postCTCollectionPublish(
			ctCollectionId
		);
	}

	async scheduleCTCollection(ctCollectionId: number) {
		const today = new Date();

		const scheduledDate = new Date(
			today.getFullYear() + 1,
			today.getMonth(),
			today.getDate()
		);

		const ctCollectionAPIClient =
			await this.apiHelpers.buildRestClient(CTCollectionAPI);

		return await ctCollectionAPIClient.postCTCollectionSchedulePublish(
			ctCollectionId,
			scheduledDate
		);
	}
}
