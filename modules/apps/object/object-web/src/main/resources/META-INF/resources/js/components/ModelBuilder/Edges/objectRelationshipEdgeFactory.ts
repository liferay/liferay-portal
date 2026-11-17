/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {stringUtils} from '@liferay/object-js-components-web';

import {ObjectRelationshipEdgeData} from '../types';
import {manyMarkerId} from './ManyMarker';
import {ObjectRelationshipMap} from './ObjectRelationshipMap';
import {oneMarkerId} from './OneMarker';

interface GetObjectRelationships {
	objectDefinitionExternalReferenceCode1: string;
	objectDefinitionExternalReferenceCode2: string;
	objectRelationshipMap: ObjectRelationshipMap;
}

function getObjectRelationships({
	objectDefinitionExternalReferenceCode1,
	objectDefinitionExternalReferenceCode2,
	objectRelationshipMap,
}: GetObjectRelationships) {
	const objectRelationships =
		objectRelationshipMap.getValueByExternalReferenceCodes(
			objectDefinitionExternalReferenceCode1,
			objectDefinitionExternalReferenceCode2
		);

	if (!objectRelationships) {
		return [];
	}

	objectRelationshipMap.deleteByExternalReferenceCodes(
		objectDefinitionExternalReferenceCode1,
		objectDefinitionExternalReferenceCode2
	);

	return objectRelationships;
}

interface ObjectRelationshipEdgeFactory {
	objectDefinition: ObjectDefinitionNodeData;
	objectRelationship: ObjectRelationship;
	objectRelationshipMap: ObjectRelationshipMap;
	selectedObjectRelationshipId: number | undefined;
}

export function objectRelationshipEdgeFactory({
	objectDefinition,
	objectRelationship,
	objectRelationshipMap,
	selectedObjectRelationshipId,
}: ObjectRelationshipEdgeFactory) {
	const objectRelationships: ObjectRelationship[] = [];

	const isSelfObjectRelationship =
		objectRelationship.objectDefinitionExternalReferenceCode1 ===
		objectRelationship.objectDefinitionExternalReferenceCode2;

	const isTreeStructureRelationship = objectRelationship.edge === true;

	objectRelationships.push(
		...getObjectRelationships({
			objectDefinitionExternalReferenceCode1:
				objectRelationship.objectDefinitionExternalReferenceCode1,
			objectDefinitionExternalReferenceCode2:
				objectRelationship.objectDefinitionExternalReferenceCode2,
			objectRelationshipMap,
		})
	);

	objectRelationships.push(
		...getObjectRelationships({
			objectDefinitionExternalReferenceCode1:
				objectRelationship.objectDefinitionExternalReferenceCode2,
			objectDefinitionExternalReferenceCode2:
				objectRelationship.objectDefinitionExternalReferenceCode1,
			objectRelationshipMap,
		})
	);

	if (objectRelationships.length !== 0) {
		return {
			data: objectRelationships.map((objectRelationship) => {
				return {
					defaultLanguageId: objectDefinition.defaultLanguageId,
					edge: objectRelationship.edge,
					id: objectRelationship.id,
					label: stringUtils.getLocalizableLabel({
						fallbackLabel: objectRelationship.name,
						fallbackLanguageId: objectDefinition.defaultLanguageId,
						labels: objectRelationship.label,
					}),
					markerEndId:
						objectRelationship.type === 'oneToOne'
							? `${oneMarkerId}#${objectRelationship.id}`
							: `${manyMarkerId}#${objectRelationship.id}`,
					markerStartId:
						objectRelationship.type === 'manyToMany'
							? `${manyMarkerId}#${objectRelationship.id}`
							: `${oneMarkerId}#${objectRelationship.id}`,
					name: objectRelationship.name,
					selected:
						selectedObjectRelationshipId === objectRelationship.id,
					type: objectRelationship.type,
				};
			}) as ObjectRelationshipEdgeData[],
			id: `reactflow__edge-object-relationship-parent-${objectRelationship.objectDefinitionId1}-child-${objectRelationship.objectDefinitionId2}`,
			source: `${objectDefinition.id}`,
			sourceHandle: isSelfObjectRelationship ? 'fixedLeftHandle' : null,
			target: `${objectRelationship.objectDefinitionId2}`,
			targetHandle: isSelfObjectRelationship ? 'fixedRightHandle' : null,
			type: isSelfObjectRelationship
				? 'selfObjectRelationshipEdge'
				: isTreeStructureRelationship
					? 'treeStructureObjectRelationshipEdge'
					: 'defaultObjectRelationshipEdge',
		};
	}
}
