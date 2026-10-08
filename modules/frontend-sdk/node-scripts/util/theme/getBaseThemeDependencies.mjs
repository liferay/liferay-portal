/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import project from './project.mjs';
import {getBaseThemeGlob} from './themeInspector.mjs';
import {resolveDependency} from './themeUtil.mjs';

/**
 * Returns the base theme dependencies as a list of `{cwd, glob}` objects,
 * ordered so that later ones override earlier ones.
 */
export default function getBaseThemeDependencies() {
	const {themeConfig} = project;
	const {baseTheme} = themeConfig;

	const glob = getBaseThemeGlob(themeConfig);

	const dependencies = [
		{
			cwd: resolveDependency('liferay-frontend-theme-unstyled'),
			glob,
		},
	];

	if (baseTheme === 'styled') {
		dependencies.push({
			cwd: resolveDependency('liferay-frontend-theme-styled'),
			glob,
		});
	}
	else if (baseTheme !== 'unstyled') {
		throw new Error(
			`Unsupported base theme ${JSON.stringify(baseTheme)}: only ` +
				`'styled' and 'unstyled' are supported`
		);
	}

	return dependencies;
}
