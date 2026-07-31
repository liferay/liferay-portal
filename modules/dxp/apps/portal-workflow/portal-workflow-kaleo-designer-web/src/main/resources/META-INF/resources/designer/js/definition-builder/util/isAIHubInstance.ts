/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

function isAIHubInstance(): boolean {
	return Boolean(
		Liferay.PropsValues &&
			Liferay.PropsValues.ENTERPRISE_PRODUCT_AI_HUB_ENABLED
	);
}

export {isAIHubInstance};
