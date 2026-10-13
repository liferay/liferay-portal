/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getFrontendTokenValue({
	frontendTokens = {},
	frontendTokensValues,
	name,
}) {
	const visitedNames = new Set([name]);

	let frontendTokenValue = frontendTokensValues[name];

	while (
		frontendTokenValue?.name &&
		!visitedNames.has(frontendTokenValue.name)
	) {
		const linkedName = frontendTokenValue.name;

		visitedNames.add(linkedName);

		if (!frontendTokensValues[linkedName]) {
			return (
				frontendTokens[linkedName]?.defaultValue ||
				frontendTokenValue.value
			);
		}

		frontendTokenValue = frontendTokensValues[linkedName];
	}

	return frontendTokenValue?.value;
}
