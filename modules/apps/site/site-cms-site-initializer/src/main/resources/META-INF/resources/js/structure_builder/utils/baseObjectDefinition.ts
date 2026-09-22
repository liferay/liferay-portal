/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	ObjectDefinition,
	ObjectDefinitions,
} from '../../common/types/ObjectDefinition';

let baseObjectDefinition: ObjectDefinition | null = null;

let baseObjectDefinitions: ObjectDefinitions = {};

export function getBaseObjectDefinition(): ObjectDefinition | null {
	return baseObjectDefinition;
}

export function getBaseObjectDefinitions(): ObjectDefinitions {
	return baseObjectDefinitions;
}

export function setBaseObjectDefinition(
	objectDefinition: ObjectDefinition | null,
	objectDefinitions: ObjectDefinitions
): void {
	baseObjectDefinition = objectDefinition;
	baseObjectDefinitions = objectDefinitions;
}
