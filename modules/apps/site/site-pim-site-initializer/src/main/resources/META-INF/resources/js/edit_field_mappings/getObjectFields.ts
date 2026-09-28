/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {IObjectDefinition, IObjectField} from './types';

export default function getObjectFields(
	objectDefinitions: IObjectDefinition[],
	sourceClassName: string
): IObjectField[] {
	if (sourceClassName) {
		const objectDefinition = objectDefinitions.find(
			({className}) => className === sourceClassName
		);

		return objectDefinition ? objectDefinition.objectFields : [];
	}

	const objectFields = new Map<string, IObjectField>();

	for (const objectDefinition of objectDefinitions) {
		for (const objectField of objectDefinition.objectFields) {
			if (!objectFields.has(objectField.name)) {
				objectFields.set(objectField.name, objectField);
			}
		}
	}

	return [...objectFields.values()].sort((a, b) =>
		a.label.localeCompare(b.label)
	);
}
