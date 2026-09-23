/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isFrontendTokenDefinitionEmpty} from './isFrontendTokenDefinitionEmpty';

export function mergeCustomFrontendTokenDefinition({
	customFrontendTokenDefinition,
	frontendTokenDefinition,
}) {
	if (isFrontendTokenDefinitionEmpty(customFrontendTokenDefinition)) {
		return frontendTokenDefinition;
	}

	return _merge(frontendTokenDefinition, customFrontendTokenDefinition, [
		'frontendTokenCategories',
		'frontendTokenSets',
		'frontendTokens',
	]);
}

function _merge(item, customItem, [key, ...remainingKeys]) {
	if (!key) {
		return customItem;
	}

	const children = [...(item[key] || [])];

	for (const customChild of customItem[key] || []) {
		const index = children.findIndex(
			(child) => child.name === customChild.name
		);

		if (index === -1) {
			children.push(customChild);
		}
		else {
			children[index] = _merge(
				children[index],
				customChild,
				remainingKeys
			);
		}
	}

	return {...item, [key]: children};
}
