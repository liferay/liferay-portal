/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {ObjectField} from '../types/ObjectDefinition';

export default function isCustomObjectField(objectField: ObjectField) {
	if (objectField.businessType === 'Relationship') {
		return false;
	}

	if (objectField.system && !['title', 'file'].includes(objectField.name)) {
		return false;
	}

	return true;
}
