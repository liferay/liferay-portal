/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokenValue} from './getFrontendTokenValue';

export function getResolvedFrontendTokenValues(frontendTokensValues) {
	return Object.fromEntries(
		Object.entries(frontendTokensValues).map(
			([name, frontendTokenValue]) => [
				name,
				{
					...frontendTokenValue,
					value: getFrontendTokenValue({frontendTokensValues, name}),
				},
			]
		)
	);
}
