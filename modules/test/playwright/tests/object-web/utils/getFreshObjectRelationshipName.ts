/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectRelationshipAPI} from '@liferay/object-admin-rest-client-js';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../utils/getRandomInt';

export async function getFreshObjectRelationshipName(
	apiHelpers: DataApiHelpers,
	objectDefinitionExternalReferenceCodes: string[],
	prefix: string = 'objectRelName'
): Promise<string> {
	if (prefix.length > 21) {
		throw new Error(
			`Prefix "${prefix}" is too long for a relationship name: the ` +
				`ten digit draw needs the name to stay within thirty one ` +
				`characters, so the prefix can hold at most twenty one`
		);
	}

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	for (;;) {
		const objectRelationshipName = prefix + getRandomInt();

		let taken = false;

		for (const externalReferenceCode of objectDefinitionExternalReferenceCodes) {
			const {body} =
				await objectRelationshipAPIClient.getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
					externalReferenceCode,
					undefined,
					undefined,
					undefined,
					objectRelationshipName
				);

			if (
				body.items?.some((item) => item.name === objectRelationshipName)
			) {
				taken = true;

				break;
			}
		}

		if (!taken) {
			return objectRelationshipName;
		}
	}
}
