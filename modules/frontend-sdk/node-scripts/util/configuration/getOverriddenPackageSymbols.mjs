/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {GLOBAL_NODE_SCRIPTS_CONFIG_FILE} from '../locations.mjs';
import projectScopeRequire from '../projectScopeRequire.mjs';

/**
 * @returns
 * Something like:
 *
 * {
 *   '@clayui/charts': ['bb', 'default']
 * }
 */
export default async function getOverriddenPackageSymbols() {
	const {symbols} = projectScopeRequire(GLOBAL_NODE_SCRIPTS_CONFIG_FILE);

	return symbols || {};
}
