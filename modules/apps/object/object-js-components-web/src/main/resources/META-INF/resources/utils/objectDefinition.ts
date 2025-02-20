/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {firstLetterUppercase, removeAllSpecialCharacters} from './string';

export function normalizeName(str: string) {
	const split = str.split(' ');

	const capitalizeFirstLetters = split.map((str: string) =>
		firstLetterUppercase(str)
	);
	const join = capitalizeFirstLetters.join('');

	return removeAllSpecialCharacters(join);
}

export function findObjectDefinitionById(objectDefinitions: ObjectDefinition[], id: number) {
	return 	objectDefinitions.find(objectDefinition => objectDefinition.id === id);
}

export function hasLocalizedField({objectFields}: ObjectDefinition) {
	return objectFields.some(field => field.localized);
}