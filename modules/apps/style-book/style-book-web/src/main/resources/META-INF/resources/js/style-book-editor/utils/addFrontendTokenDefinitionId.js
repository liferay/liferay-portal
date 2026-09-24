/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function addFrontendTokenDefinitionId(frontendTokenDefinition) {
	return _add(frontendTokenDefinition, frontendTokenDefinition.id, [
		'frontendTokenCategories',
		'frontendTokenSets',
		'frontendTokens',
	]);
}

function _add(item, tokenDefinitionId, [key, ...remainingKeys]) {
	if (!key) {
		return {...item, tokenDefinitionId};
	}

	if (!item[key]) {
		return item;
	}

	return {
		...item,
		[key]: item[key].map((child) =>
			_add(child, tokenDefinitionId, remainingKeys)
		),
	};
}
