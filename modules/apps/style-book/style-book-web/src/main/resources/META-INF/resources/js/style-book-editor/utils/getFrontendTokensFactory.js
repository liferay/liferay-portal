/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokens} from './getFrontendTokens';

export function getFrontendTokensFactory({
	frontendTokenDefinitions,
	themeFrontendTokenDefinitionId,
}) {
	const frontendTokens = getFrontendTokens(
		frontendTokenDefinitions,
		themeFrontendTokenDefinitionId
	);

	return (customFrontendTokenDefinition) => ({
		...frontendTokens,
		...getFrontendTokens([customFrontendTokenDefinition]),
	});
}
