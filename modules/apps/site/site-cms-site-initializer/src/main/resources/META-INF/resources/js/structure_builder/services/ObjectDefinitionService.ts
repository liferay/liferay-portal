/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ApiHelper from '../../common/services/ApiHelper';
import {ObjectDefinition, ObjectDefinitions} from '../types/ObjectDefinition';

async function getObjectDefinitions(): Promise<ObjectDefinitions> {
	const filter =
		'(status/any(x:(x eq 0))) and (' +
		"(objectFolderExternalReferenceCode eq 'L_CMS_CONTENT_STRUCTURES') or " +
		"(objectFolderExternalReferenceCode eq 'L_CMS_FILE_TYPES') or " +
		"(objectFolderExternalReferenceCode eq 'L_CMS_STRUCTURE_REPEATABLE_GROUPS'))";

	const items = await ApiHelper.getAll<ObjectDefinition>({
		filter,
		url: '/o/object-admin/v1.0/object-definitions',
	});

	const objectDefinitions: ObjectDefinitions = {};

	for (const objectDefinition of items) {
		objectDefinitions[objectDefinition.externalReferenceCode] =
			objectDefinition;
	}

	return objectDefinitions;
}

export default {
	getObjectDefinitions,
};
