/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../services/ApiHelper';
import {State} from '../contexts/StateContext';
import {ObjectDefinition} from '../types/ObjectDefinition';
import {ReferencedStructure, Structures} from '../types/Structure';
import buildObjectDefinition from '../utils/buildObjectDefinition';
import buildStructure from '../utils/buildStructure';
import {Field} from '../utils/field';
import getRandomId from '../utils/getRandomId';

async function createStructure({
	erc = getRandomId(),
	fields,
	label,
	name,
	spaces,
}: {
	erc?: State['erc'];
	fields: (Field | ReferencedStructure)[];
	label: State['label'];
	name: State['name'];
	spaces: State['spaces'];
}) {
	const objectDefinition = buildObjectDefinition({
		erc,
		fields,
		label,
		name,
		spaces,
	});

	return await ApiHelper.post<{id: number}>(
		'/o/object-admin/v1.0/object-definitions',
		objectDefinition
	);
}

async function getStructures(): Promise<Structures> {
	const filter =
		"(objectFolderExternalReferenceCode eq 'L_CMS_CONTENT_STRUCTURES') or (objectFolderExternalReferenceCode eq 'L_CMS_FILE_TYPES')";

	const {data, error} = await ApiHelper.get<{items: ObjectDefinition[]}>(
		`/o/object-admin/v1.0/object-definitions?filter=${filter}`
	);

	if (data) {
		const structures: Structures = new Map();

		for (const objectDefinition of data.items) {
			const structure = buildStructure(objectDefinition);

			if (!structure || structure.status === 'draft') {
				continue;
			}

			structures.set(structure.erc, structure);
		}

		return structures;
	}

	throw new Error(error);
}

async function publishStructure({id}: {id: State['id']}) {
	if (!id) {
		return;
	}

	return await ApiHelper.post(
		`/o/object-admin/v1.0/object-definitions/${id}/publish`
	);
}

async function updateStructure({
	erc,
	fields,
	id,
	label,
	name,
	spaces,
}: {
	erc: State['erc'];
	fields: (Field | ReferencedStructure)[];
	id: State['id'];
	label: State['label'];
	name: State['name'];
	spaces: State['spaces'];
}) {
	const objectDefinition = buildObjectDefinition({
		erc,
		fields,
		id,
		label,
		name,
		spaces,
	});

	return await ApiHelper.put(
		`/o/object-admin/v1.0/object-definitions/${id}`,
		objectDefinition
	);
}

export default {
	createStructure,
	getStructures,
	publishStructure,
	updateStructure,
};
