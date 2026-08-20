/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectField,
	ObjectRelationshipAPI,
} from '@liferay/object-admin-rest-client-js';

import {DataApiHelpers} from '../../../helpers/ApiHelpers';
import {generateObjectFields} from './generateObjectFields';
import {getFreshObjectRelationshipName} from './getFreshObjectRelationshipName';

/**
 * Posts the pair of object definitions an aggregation field needs. The source
 * definition holds the field whose values are aggregated, the aggregation
 * definition is the one to add the aggregation field to, and a one-to-many
 * relationship points from the aggregation definition to the source one.
 *
 * The returned relationshipObjectFieldName is the field a source object entry
 * sets to attach itself to an aggregation object entry.
 */
export async function postAggregationObjectDefinitions({
	apiHelpers,
	objectFieldBusinessType,
}: {
	apiHelpers: DataApiHelpers;
	objectFieldBusinessType: ObjectField['businessType'];
}) {
	const [aggregatedObjectField] = generateObjectFields({
		objectFieldBusinessTypes: [objectFieldBusinessType],
	});

	const sourceObjectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			objectFields: [aggregatedObjectField],
			status: {code: 0},
		});

	const aggregationObjectDefinition =
		await apiHelpers.objectAdmin.postRandomObjectDefinition({
			status: {code: 0},
		});

	for (const objectDefinition of [
		sourceObjectDefinition,
		aggregationObjectDefinition,
	]) {
		apiHelpers.data.push({
			id: objectDefinition.id,
			type: 'objectDefinition',
		});
	}

	const objectRelationshipAPIClient = await apiHelpers.buildRestClient(
		ObjectRelationshipAPI
	);

	const objectRelationshipName = await getFreshObjectRelationshipName(
		apiHelpers,
		[
			aggregationObjectDefinition.externalReferenceCode!,
			sourceObjectDefinition.externalReferenceCode!,
		]
	);

	const {body: objectRelationship} =
		await objectRelationshipAPIClient.postObjectDefinitionByExternalReferenceCodeObjectRelationship(
			aggregationObjectDefinition.externalReferenceCode!,
			{
				deletionType: 'cascade',
				label: {en_US: objectRelationshipName},
				name: objectRelationshipName,
				objectDefinitionExternalReferenceCode2:
					sourceObjectDefinition.externalReferenceCode,
				objectDefinitionId2: sourceObjectDefinition.id,
				objectDefinitionName2: sourceObjectDefinition.name,
				type: 'oneToMany',
			}
		);

	const aggregationObjectDefinitionName = aggregationObjectDefinition.name!;

	const uncapitalizedAggregationObjectDefinitionName =
		aggregationObjectDefinitionName[0].toLowerCase() +
		aggregationObjectDefinitionName.substring(1);

	return {
		aggregatedObjectField,
		aggregationObjectDefinition,
		objectRelationship,
		relationshipObjectFieldName: `r_${objectRelationship.name}_c_${uncapitalizedAggregationObjectDefinitionName}Id`,
		sourceObjectDefinition,
	};
}
