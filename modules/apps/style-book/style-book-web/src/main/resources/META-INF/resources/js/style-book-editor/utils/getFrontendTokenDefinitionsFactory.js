/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {addFrontendTokenDefinitionId} from './addFrontendTokenDefinitionId';
import {isFrontendTokenDefinitionEmpty} from './isFrontendTokenDefinitionEmpty';
import {mergeCustomFrontendTokenDefinition} from './mergeCustomFrontendTokenDefinition';

export function getFrontendTokenDefinitionsFactory({
	frontendTokenDefinitions,
	themeFrontendTokenDefinitionId,
}) {
	const taggedFrontendTokenDefinitions = frontendTokenDefinitions.map(
		addFrontendTokenDefinitionId
	);

	return (customFrontendTokenDefinition) => {
		return taggedFrontendTokenDefinitions
			.map((frontendTokenDefinition) => {
				if (
					frontendTokenDefinition.id !==
					themeFrontendTokenDefinitionId
				) {
					return frontendTokenDefinition;
				}

				return mergeCustomFrontendTokenDefinition({
					customFrontendTokenDefinition: addFrontendTokenDefinitionId(
						customFrontendTokenDefinition
					),
					frontendTokenDefinition,
				});
			})
			.filter(
				(frontendTokenDefinition) =>
					!isFrontendTokenDefinitionEmpty(frontendTokenDefinition)
			);
	};
}
