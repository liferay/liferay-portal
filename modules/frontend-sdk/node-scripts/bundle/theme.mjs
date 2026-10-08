/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import path from 'path';

import {MODULES_DIR} from '../util/locations.mjs';
import build from '../util/theme/tasks/build.mjs';

/**
 * Builds a theme in the liferay-portal repo.
 */
export default async function run() {
	await build({
		cssCommonPath: path.normalize('build_gradle/frontend-css-common'),
		sassIncludePaths: path.normalize(`${MODULES_DIR}/node_modules`),
		styledPath: path.normalize(
			`${MODULES_DIR}/apps/frontend-theme/frontend-theme-styled/src/main/resources/META-INF/resources/_styled`
		),
		unstyledPath: path.normalize(
			`${MODULES_DIR}/apps/frontend-theme/frontend-theme-unstyled/src/main/resources/META-INF/resources/_unstyled`
		),
	});
}
