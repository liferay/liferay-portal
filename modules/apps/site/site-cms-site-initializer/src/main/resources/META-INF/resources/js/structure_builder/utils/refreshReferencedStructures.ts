/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectDefinition, ObjectDefinitions} from '../types/ObjectDefinition';
import {
	ReferencedStructure,
	RepeatableGroup,
	Structure,
} from '../types/Structure';
import {
	buildField,
	buildReferencedStructure,
	getSpaces,
} from './buildStructure';
import isCustomObjectField from './isCustomObjectField';

export default function refreshReferencedStructures({
	ancestors = [],
	objectDefinition,
	objectDefinitions,
	root,
}: {
	ancestors?: Array<ObjectDefinition['externalReferenceCode']>;
	objectDefinition?: ObjectDefinition;
	objectDefinitions: ObjectDefinitions;
	root: ReferencedStructure | RepeatableGroup | Structure;
}) {
	const children = new Map();

	// Iterate over children

	for (const child of root.children.values()) {

		// It's referenced structure

		if (child.type === 'referenced-structure') {

			// Ignore it if it's not in the new objectDefinition

			const objectRelationship =
				objectDefinition?.objectRelationships?.find(
					({name}) => name === child.name
				);

			if (objectDefinition && !objectRelationship) {
				continue;
			}

			// Insert it with updated data and refresh its children

			const relatedObjectDefinition = objectDefinitions[child.erc]!;

			const referencedStructure: ReferencedStructure = {
				...child,
				children: refreshReferencedStructures({
					ancestors: [...ancestors, root.erc],
					objectDefinition: relatedObjectDefinition,
					objectDefinitions,
					root: child,
				}),
				label: relatedObjectDefinition.label,
				spaces: getSpaces(relatedObjectDefinition),
			};

			children.set(referencedStructure.uuid, referencedStructure);
		}

		// It's repeatable group

		else if (child.type === 'repeatable-group') {

			// Ignore it if it's not in the new objectDefinition

			const objectRelationship =
				objectDefinition?.objectRelationships?.find(
					({name}) => name === child.name
				);

			if (objectDefinition && !objectRelationship) {
				continue;
			}

			// Insert it with updated data and refresh its children

			const relatedObjectDefinition = objectDefinitions[child.erc]!;

			const repeatableGroup: RepeatableGroup = {
				...child,
				children: refreshReferencedStructures({
					ancestors: [...ancestors, root.erc],
					objectDefinition: relatedObjectDefinition,
					objectDefinitions,
					root: child,
				}),
				label: relatedObjectDefinition.label,
			};

			children.set(repeatableGroup.uuid, repeatableGroup);
		}

		// It's a field

		else {

			// Ignore if it's not in the new objectDefinition

			const objectField = objectDefinition?.objectFields?.find(
				(objectField) => objectField.name === child.name
			);

			if (objectDefinition && !objectField) {
				continue;
			}

			// Insert it with updated data

			const field = {
				...child,
				label: objectField?.label || child.label,
			};

			children.set(field.uuid, field);
		}
	}

	// If we are inside referenced structure or repeatable group, insert new elements

	if (objectDefinition) {
		const childrenNames = Array.from(root.children.values()).map(
			(child) => child.name
		);

		// Insert new fields

		const newObjectFields = Array.from(
			objectDefinition.objectFields || []
		).filter(
			(objectField) =>
				!childrenNames.includes(objectField.name) &&
				isCustomObjectField(objectField)
		);

		for (const objectField of newObjectFields) {
			const field = buildField({objectField, parent: root.uuid});

			children.set(field.uuid, field);
		}

		// Insert new referenced structures

		const newObjectRelationships = Array.from(
			objectDefinition.objectRelationships || []
		).filter(
			(objectRelationship) =>
				!childrenNames.includes(objectRelationship.name)
		);

		for (const objectRelationship of newObjectRelationships) {
			const referencedStructure = buildReferencedStructure({
				ancestors,
				erc: objectRelationship.objectDefinitionExternalReferenceCode2,
				objectDefinitions,
				parent: root.uuid,
				relationshipName: objectRelationship.name,
			});

			children.set(referencedStructure.uuid, referencedStructure);
		}
	}

	return children;
}
