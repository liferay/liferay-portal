/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '../../common/types/ObjectDefinition';

const CMS_SYSTEM_OBJECT_FIELD_NAMES: Record<string, string[]> = {
	L_CMS_BASIC_DOCUMENT: ['file', 'title'],
	L_CMS_BASIC_WEB_CONTENT: ['content', 'title'],
	L_CMS_BLOG: ['content', 'coverImage', 'subtitle', 'title'],
	L_CMS_EXTERNAL_VIDEO: ['title', 'videoURL'],
};

const CUSTOM_OBJECT_SYSTEM_FIELD_NAMES = ['file', 'title'];

let CONTRIBUTED_SYSTEM_OBJECT_FIELD_NAMES: Record<string, string[]> = {};

export function setSystemObjectFieldNames(
	systemObjectFieldNames: Record<string, string[]>
) {
	CONTRIBUTED_SYSTEM_OBJECT_FIELD_NAMES = systemObjectFieldNames ?? {};
}

export default function isCustomObjectField(
	objectField: ObjectField,
	objectDefinitionERC: string
) {
	if (objectField.businessType === 'Relationship') {
		return false;
	}

	if (objectField.system) {
		const allowedSystemFields =
			CMS_SYSTEM_OBJECT_FIELD_NAMES[objectDefinitionERC] ??
			CONTRIBUTED_SYSTEM_OBJECT_FIELD_NAMES[objectDefinitionERC] ??
			CUSTOM_OBJECT_SYSTEM_FIELD_NAMES;

		if (!allowedSystemFields.includes(objectField.name)) {
			return false;
		}
	}

	return true;
}
