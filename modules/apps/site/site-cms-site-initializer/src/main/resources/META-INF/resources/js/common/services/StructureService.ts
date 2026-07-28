/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {State} from '../../structure_builder/contexts/StateContext';
import {Structure} from '../../structure_builder/types/Structure';
import buildGroupObjectDefinitions from '../../structure_builder/utils/buildGroupObjectDefinitions';
import buildObjectDefinition from '../../structure_builder/utils/buildObjectDefinition';
import buildObjectRelationships from '../../structure_builder/utils/buildObjectRelationships';
import getRandomId from '../../structure_builder/utils/getRandomId';
import {ObjectDefinition} from '../types/ObjectDefinition';
import ApiHelper from './ApiHelper';

export type StructureServiceError = 'slug-in-use' | 'in-use' | 'unexpected';

function classifyError(type?: string | null): StructureServiceError {
	if (!type) {
		return 'unexpected';
	}

	if (type.startsWith('ObjectDefinitionFriendlyURLSeparatorException')) {
		return 'slug-in-use';
	}

	if (type.startsWith('ObjectDefinitionNameException')) {
		return 'in-use';
	}

	return 'unexpected';
}

async function createStructure({
	children,
	erc = getRandomId(),
	label,
	name,
	publishedChildren,
	settings,
	slug,
	spaces,
	status,
	workflows,
}: {
	children: Structure['children'];
	erc?: Structure['erc'];
	label: Structure['label'];
	name: Structure['name'];
	publishedChildren: State['publishedChildren'];
	settings: Structure['settings'];
	slug: Structure['slug'];
	spaces: Structure['spaces'];
	status: Structure['status'];
	workflows: Structure['workflows'];
}) {

	// Publish object definitions for repeatable groups

	const objectDefinitions = buildGroupObjectDefinitions({
		children,
		publishedChildren,
	});

	for (const objectDefinition of objectDefinitions) {
		const {error, type} = await ApiHelper.put(
			`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinition.externalReferenceCode}`,
			objectDefinition
		);

		if (error) {
			return {data: null, error: classifyError(type)};
		}
	}

	// Publish the main object definition

	const mainObjectDefinition = buildObjectDefinition({
		children,
		erc,
		label,
		name,
		settings,
		slug,
		spaces,
		status,
		workflows,
	});

	const {data, error, type} = await ApiHelper.post<{id: number}>(
		'/o/object-admin/v1.0/object-definitions',
		mainObjectDefinition
	);

	if (error) {
		return {data: null, error: classifyError(type)};
	}

	const objectRelationships = buildObjectRelationships({
		children,
		structureERC: erc,
	});

	for (const objectRelationship of objectRelationships) {
		const {error, type} = await ApiHelper.post(
			`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectRelationship.objectDefinitionExternalReferenceCode1}/object-relationships`,
			objectRelationship
		);

		if (error) {
			return {data: null, error: classifyError(type)};
		}
	}

	return {
		data,
		error: null,
	};
}

async function updateStructure({
	children,
	erc,
	history,
	id,
	label,
	name,
	publishedChildren,
	settings,
	slug,
	spaces,
	status,
	workflows,
}: {
	children: Structure['children'];
	erc: Structure['erc'];
	history: State['history'];
	id: Structure['id'];
	label: Structure['label'];
	name: Structure['name'];
	publishedChildren: State['publishedChildren'];
	settings: Structure['settings'];
	slug: Structure['slug'];
	spaces: Structure['spaces'];
	status: Structure['status'];
	workflows: Structure['workflows'];
}) {
	const groupObjectDefinitions = buildGroupObjectDefinitions({
		children,
		publishedChildren,
	});

	const mainObjectDefinition = buildObjectDefinition({
		children,
		erc,
		id,
		label,
		name,
		settings,
		slug,
		spaces,
		status,
		workflows,
	});

	const objectRelationships = buildObjectRelationships({
		children,
		structureERC: erc,
	});

	const pathMain = Liferay.ThemeDisplay.getPathMain();

	const formData = new FormData();

	formData.append(
		'deletedObjectRelationships',
		JSON.stringify(
			history.deletedRelationships.map((relationship) => ({
				objectDefinitionERC: relationship.structureERC,
				objectRelationshipERC: relationship.relationshipERC,
			}))
		)
	);

	formData.append(
		'deletedRepeatableGroupsERCs',
		history.deletedGroupERCs.join(',')
	);

	formData.append('objectDefinition', JSON.stringify(mainObjectDefinition));

	formData.append('objectRelationships', JSON.stringify(objectRelationships));

	formData.append(
		'repeatableGroupObjectDefinitions',
		JSON.stringify(groupObjectDefinitions)
	);

	const response = await ApiHelper.postFormData(
		formData,
		`${pathMain}/cms/update-structure`
	);

	if (response.error !== null) {
		return {error: classifyError(response.type)};
	}

	return response;
}

async function deleteStructure({id}: {id: Structure['id']}) {
	const pathMain = Liferay.ThemeDisplay.getPathMain();

	const formData = new FormData();

	formData.append('objectDefinitionId', String(id));

	const response = await ApiHelper.postFormData(
		formData,
		`${pathMain}/cms/delete-structure`
	);

	if (response?.error) {
		return {error: response.error};
	}
}

async function updateStructureWorkflow({
	structureIds,
	workflow,
}: {
	structureIds: string[];
	workflow: string;
}) {
	return {
		error: false,
		structureIds,
		workflow,
	};
}

async function getStructure(externalReferenceCode: string) {
	return ApiHelper.get<ObjectDefinition>(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${externalReferenceCode}`
	);
}

export default {
	createStructure,
	deleteStructure,
	getStructure,
	updateStructure,
	updateStructureWorkflow,
};
