/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectAction,
	ObjectActionAPI,
} from '@liferay/object-admin-rest-client-js/v1_0';

import {getRandomInt} from '../utils/getRandomInt';
import {ApiHelpers} from './ApiHelpers';

export class ObjectActionApiHelper {
	readonly apiHelpers: ApiHelpers;

	constructor(apiHelpers: ApiHelpers) {
		this.apiHelpers = apiHelpers;
	}

	async postRandomAction(
		objectDefinitionExternalReferenceCode: string,
		objectAction: Partial<ObjectAction>
	): Promise<ObjectAction> {
		const objectActionAPIClient =
			await this.apiHelpers.buildRestClient(ObjectActionAPI);

		return (
			await objectActionAPIClient.postObjectDefinitionByExternalReferenceCodeObjectAction(
				objectDefinitionExternalReferenceCode,
				{
					active: true,
					label: {en_US: 'Custom Action'},
					name: `customAction${getRandomInt()}`,
					...objectAction,
				}
			)
		).body;
	}
}
