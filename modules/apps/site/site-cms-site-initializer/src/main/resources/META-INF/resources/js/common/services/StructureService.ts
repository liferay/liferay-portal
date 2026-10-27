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
import {ServerError} from '../../structure_builder/utils/validation';
import {ObjectDefinition} from '../types/ObjectDefinition';
import ApiHelper from './ApiHelper';

function classifyError(type?: string | null): ServerError {
	if (!type) {
		return 'unexpected';
	}

	if (type === 'DuplicateObjectDefinitionExternalReferenceCodeException') {
		return 'erc-in-use';
	}

	if (type.startsWith('ObjectDefinitionFriendlyURLSeparatorException')) {
		return 'slug-in-use';
	}

	if (type === 'ObjectDefinitionNameException.MustNotBeDuplicate') {
		return 'name-in-use';
	}

	if (type.startsWith('PrincipalException')) {
		return 'permission';
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
	titleFieldName,
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
	titleFieldName: Structure['titleFieldName'];
	workflows: Structure['workflows'];
}) {
	return saveStructure({
		children,
		deletedGroupERCs: [],
		deletedRelationships: [],
		erc,
		label,
		name,
		publishedChildren,
		settings,
		slug,
		spaces,
		status,
		titleFieldName,
		workflows,
	});
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
	titleFieldName,
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
	titleFieldName: Structure['titleFieldName'];
	workflows: Structure['workflows'];
}) {
	return saveStructure({
		children,
		deletedGroupERCs: history.deletedGroupERCs,
		deletedRelationships: history.deletedRelationships,
		erc,
		id,
		label,
		name,
		publishedChildren,
		settings,
		slug,
		spaces,
		status,
		titleFieldName,
		workflows,
	});
}

async function saveStructure({
	children,
	deletedGroupERCs,
	deletedRelationships,
	erc,
	id,
	label,
	name,
	publishedChildren,
	settings,
	slug,
	spaces,
	status,
	titleFieldName,
	workflows,
}: {
	children: Structure['children'];
	deletedGroupERCs: State['history']['deletedGroupERCs'];
	deletedRelationships: State['history']['deletedRelationships'];
	erc: Structure['erc'];
	id?: Structure['id'];
	label: Structure['label'];
	name: Structure['name'];
	publishedChildren: State['publishedChildren'];
	settings: Structure['settings'];
	slug: Structure['slug'];
	spaces: Structure['spaces'];
	status: Structure['status'];
	titleFieldName: Structure['titleFieldName'];
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
		titleFieldName,
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
			deletedRelationships.map((relationship) => ({
				objectDefinitionERC: relationship.structureERC,
				objectRelationshipERC: relationship.relationshipERC,
			}))
		)
	);

	formData.append('deletedGroupERCs', deletedGroupERCs.join(','));

	formData.append('objectDefinition', JSON.stringify(mainObjectDefinition));

	formData.append('objectRelationships', JSON.stringify(objectRelationships));

	formData.append(
		'repeatableGroupObjectDefinitions',
		JSON.stringify(groupObjectDefinitions)
	);

	const response = await ApiHelper.postFormData<{id: string}>(
		formData,
		`${pathMain}/cms/update-structure`
	);

	if (response.error !== null) {
		return {data: null, error: classifyError(response.type)};
	}

	return {data: {id: Number(response.data.id)}, error: null};
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
