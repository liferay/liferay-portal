/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

'use strict';

const _ = require('lodash');
const path = require('path');

const project = require('../../lib/project');
const util = require('../../lib/util');
const {getBaseThemeGlob, getLiferayThemeJSON} = require('./theme_inspector');

function getBaseThemeDependencies(
	baseThemePath = project.dir,
	dependencies = []
) {
	const {baseTheme} = getLiferayThemeJSON(baseThemePath);
	const baseThemeGlob = getBaseThemeGlob(baseThemePath);

	dependencies = _.uniq(
		dependencies.concat([
			path.join(
				util.resolveDependency('liferay-frontend-theme-unstyled'),
				baseThemeGlob
			),
		])
	);

	if (_.isObject(baseTheme)) {
		baseThemePath = path.join(
			baseThemePath,
			'node_modules',
			baseTheme.name
		);

		dependencies = getBaseThemeDependencies(baseThemePath, dependencies);

		dependencies.push(path.resolve(baseThemePath, 'src', '**', '*'));

		return dependencies;
	}
	else if (baseTheme === 'styled' || baseTheme === 'admin') {
		dependencies.splice(
			1,
			0,
			path.join(
				util.resolveDependency('liferay-frontend-theme-styled'),
				baseThemeGlob
			)
		);

		if (baseTheme === 'admin') {
			dependencies.splice(
				2,
				0,
				path.join(
					util.resolveDependency('liferay-frontend-theme-admin-web'),
					baseThemeGlob
				)
			);
		}

		return dependencies;
	}

	return dependencies;
}

module.exports = getBaseThemeDependencies;
