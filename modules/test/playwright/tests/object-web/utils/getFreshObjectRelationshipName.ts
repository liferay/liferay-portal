/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectRelationshipAPI} from '@liferay/object-admin-rest-client-js';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {getRandomInt} from '../../../utils/getRandomInt';

/**
 * Returns a relationship name that is free on both definitions the
 * relationship will connect. Relationship names are unique per definition, on
 * both sides, because the reverse relationship carries the same name, and on
 * a shared system definition the namespace also holds every other test's
 * relationships and every leftover a crashed run left behind. The name is
 * drawn from a ten digit space and each definition is asked whether it is
 * taken, drawing again until it is not, so a collision with anything already
 * persisted is impossible. Two callers drawing in the same moment can still
 * race each other, and for that window the create's own duplicate refusal
 * remains the arbiter, being the only atomic claim.
 *
 * Use this for every relationship name a test creates.
 */
export async function getFreshObjectRelationshipName(
	apiHelpers: DataApiHelpers,
	objectDefinitionExternalReferenceCodes: string[],
	prefix: string = 'objectRelName'
): Promise<string> {

	// The name becomes part of a database column, so the validator holds it
	// to a hard budget that the ten digits must fit inside as well.

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
