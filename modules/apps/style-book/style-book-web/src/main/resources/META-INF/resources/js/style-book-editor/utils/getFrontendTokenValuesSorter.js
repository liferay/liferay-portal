/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getFrontendTokenValuesSorter({
	customTokenDefinitionId,
	customTokenDefinitionPriority,
	defaultPriority,
	frontendTokenDefinitions,
}) {
	const tokenDefinitionPriorities = Object.fromEntries([
		...frontendTokenDefinitions.map(({id, priority}) => [id, priority]),
		...(customTokenDefinitionId === undefined
			? []
			: [[customTokenDefinitionId, customTokenDefinitionPriority]]),
	]);

	return (frontendTokensValues) =>
		Object.values(frontendTokensValues).sort(
			(frontendTokenValue1, frontendTokenValue2) => {
				const frontendTokenValue1Priority =
					tokenDefinitionPriorities[
						frontendTokenValue1.tokenDefinitionId
					];

				const frontendTokenValue2Priority =
					tokenDefinitionPriorities[
						frontendTokenValue2.tokenDefinitionId
					];

				return (
					(frontendTokenValue1Priority ?? defaultPriority) -
					(frontendTokenValue2Priority ?? defaultPriority)
				);
			}
		);
}
