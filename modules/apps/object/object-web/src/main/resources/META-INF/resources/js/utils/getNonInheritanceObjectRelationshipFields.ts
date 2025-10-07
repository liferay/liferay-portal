/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {API} from '@liferay/object-js-components-web';

const getInheritanceObjectRelationshipExternalReferenceCodes = async (
	objectRelationshipExternalReferenceCodes: string[],
	parentObjectDefinitionExternalReferenceCode: string
): Promise<string[]> => {
	if (objectRelationshipExternalReferenceCodes.length === 1) {
		return objectRelationshipExternalReferenceCodes;
	}

	try {
		const objectRelationships =
			await API.getObjectDefinitionByExternalReferenceCodeObjectRelationships(
				parentObjectDefinitionExternalReferenceCode,
				{
					filter: 'edge eq true',
				}
			);

		return objectRelationships.map(
			({externalReferenceCode}) => externalReferenceCode
		);
	}
	catch (error) {
		console.error(
			`Failed to fetch inheritance relationships for parent ${parentObjectDefinitionExternalReferenceCode}:`,
			error
		);

		return [];
	}
};

const groupObjectRelationshipsByParent = (
	objectRelationshipFields: ObjectField[]
): Map<string, string[]> => {
	const objectRelationshipsByParent = new Map<string, string[]>();

	for (const {
		objectDefinitionExternalReferenceCode1,
		objectRelationshipExternalReferenceCode,
	} of objectRelationshipFields) {
		if (
			objectDefinitionExternalReferenceCode1 &&
			objectRelationshipExternalReferenceCode
		) {
			const existing =
				objectRelationshipsByParent.get(
					objectDefinitionExternalReferenceCode1
				) ?? [];

			objectRelationshipsByParent.set(
				objectDefinitionExternalReferenceCode1,
				[...existing, objectRelationshipExternalReferenceCode]
			);
		}
	}

	return objectRelationshipsByParent;
};

export async function getNonInheritanceObjectRelationshipFields(
	objectDefinition: ObjectDefinition
): Promise<ObjectField[]> {
	const {objectDefinitionSettings, objectFields} = objectDefinition;

	if (!objectDefinitionSettings) {
		return objectFields;
	}

	const rootObjectDefinitionExternalReferenceCodes =
		objectDefinitionSettings.find(
			({name}) => name === 'rootObjectDefinitionExternalReferenceCodes'
		);

	if (!rootObjectDefinitionExternalReferenceCodes) {
		return objectFields;
	}

	const objectRelationshipFields = objectFields.filter(
		(field) => field.businessType === 'Relationship'
	);

	const objectRelationshipsByParent = groupObjectRelationshipsByParent(
		objectRelationshipFields
	);

	const inheritanceObjectRelationshipPromises: Promise<string[]>[] = [];

	for (const [
		parentObjectDefinitionExternalReferenceCode,
		objectRelationshipExternalReferenceCodes,
	] of objectRelationshipsByParent.entries()) {
		if (
			rootObjectDefinitionExternalReferenceCodes.value.includes(
				parentObjectDefinitionExternalReferenceCode
			)
		) {
			const promise =
				getInheritanceObjectRelationshipExternalReferenceCodes(
					objectRelationshipExternalReferenceCodes,
					parentObjectDefinitionExternalReferenceCode
				);

			inheritanceObjectRelationshipPromises.push(promise);
		}
	}

	const inheritanceObjectRelationshipPromisesResolve = await Promise.all(
		inheritanceObjectRelationshipPromises
	);
	const inheritanceObjectRelationship = new Set(
		inheritanceObjectRelationshipPromisesResolve.flat()
	);

	return objectFields.filter((objectField) => {
		if (objectField.businessType === 'Relationship') {
			return !inheritanceObjectRelationship.has(
				objectField.objectRelationshipExternalReferenceCode!
			);
		}

		return true;
	});
}
