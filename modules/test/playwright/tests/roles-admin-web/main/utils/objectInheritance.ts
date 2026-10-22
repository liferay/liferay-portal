/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitionAPI,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js/v1_0';

import {getRandomInt} from '../../../../utils/getRandomInt';

export async function createInheritanceRelationship(
	objectRelationshipAPIClient: ObjectRelationshipAPI,
	parent: ObjectDefinition,
	child: ObjectDefinition,
	namePrefix: string
) {
	return objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
		parent.externalReferenceCode as string,
		{
			edge: true,
			label: {en_US: namePrefix + getRandomInt()},
			name: namePrefix + getRandomInt(),
			objectDefinitionExternalReferenceCode1:
				parent.externalReferenceCode,
			objectDefinitionExternalReferenceCode2: child.externalReferenceCode,
			objectDefinitionId1: parent.id,
			objectDefinitionId2: child.id,
			objectDefinitionName2: child.name,
			type: 'oneToMany',
		}
	);
}

export async function setAllowStandaloneObjectEntry(
	objectDefinitionAPIClient: ObjectDefinitionAPI,
	child: ObjectDefinition,
	value: 'true' | 'false'
) {
	return objectDefinitionAPIClient.patchObjectDefinition(child.id as number, {
		objectDefinitionSettings: [
			{
				name: 'allowStandaloneObjectEntry',
				value: value as unknown as object,
			},
		],
	});
}
