/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../common/services/ApiHelper';
import {Structure} from '../types/Structure';
import buildGroupObjectDefinitions from '../utils/buildGroupObjectDefinitions';
import buildObjectDefinition from '../utils/buildObjectDefinition';
import getRandomId from '../utils/getRandomId';

async function createStructure({
	children,
	erc = getRandomId(),
	label,
	name,
	spaces,
	status,
	workflows,
}: {
	children: Structure['children'];
	erc?: Structure['erc'];
	label: Structure['label'];
	name: Structure['name'];
	spaces: Structure['spaces'];
	status: Structure['status'];
	workflows: Structure['workflows'];
}) {

	// Publish object definitions for repeatable groups

	const objectDefinitions = buildGroupObjectDefinitions({children});

	for (const objectDefinition of objectDefinitions) {
		const {error} = await ApiHelper.put(
			`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinition.externalReferenceCode}`,
			objectDefinition
		);

		if (error) {
			return {
				error: Liferay.Language.get(
					'an-unexpected-error-occurred-while-saving-or-publishing-the-content-structure'
				),
			};
		}
	}

	// Publish the main object definition

	const mainObjectDefinition = buildObjectDefinition({
		children,
		erc,
		label,
		name,
		spaces,
		status,
		workflows,
	});

	return await ApiHelper.post<{id: number}>(
		'/o/object-admin/v1.0/object-definitions',
		mainObjectDefinition
	);
}

async function updateStructure({
	children,
	erc,
	label,
	name,
	spaces,
	status,
	workflows,
}: {
	children: Structure['children'];
	erc: Structure['erc'];
	label: Structure['label'];
	name: Structure['name'];
	spaces: Structure['spaces'];
	status: Structure['status'];
	workflows: Structure['workflows'];
}) {

	// Publish object definitions for repeatable groups

	const objectDefinitions = buildGroupObjectDefinitions({children});

	for (const objectDefinition of objectDefinitions) {
		const {error} = await ApiHelper.put(
			`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${objectDefinition.externalReferenceCode}`,
			objectDefinition
		);

		if (error) {
			return {
				error: Liferay.Language.get(
					'an-unexpected-error-occurred-while-saving-or-publishing-the-content-structure'
				),
			};
		}
	}

	// Publish the main object definition

	const mainObjectDefinition = buildObjectDefinition({
		children,
		erc,
		label,
		name,
		spaces,
		status,
		workflows,
	});

	return await ApiHelper.put(
		`/o/object-admin/v1.0/object-definitions/by-external-reference-code/${erc}`,
		mainObjectDefinition
	);
}

export default {
	createStructure,
	updateStructure,
};
