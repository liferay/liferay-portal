/**
 * SPDX-FileCopyrightText: © 2017 Liferay, Inc. <https://liferay.com>
 * SPDX-License-Identifier: MIT
 */

const path = require('path');

const TEMPLATE_LANG_FTL = 'ftl';
const TEMPLATE_LANG_VM = 'vm';

function getBaseThemeGlob(themePath) {
	const templateLanguage = getLiferayThemeJSON(themePath).templateLanguage;
	const glob = '**/!(package.json';

	switch (templateLanguage) {
		case TEMPLATE_LANG_FTL:
			return glob + '|*.' + TEMPLATE_LANG_VM + ')';

		case TEMPLATE_LANG_VM:
			return glob + '|*.' + TEMPLATE_LANG_FTL + ')';

		default:
			return glob + ')';
	}
}

function getLiferayThemeJSON(themePath) {
	// eslint-disable-next-line @liferay/no-dynamic-require
	return require(path.join(themePath, 'package.json')).liferayTheme;
}

module.exports = {getBaseThemeGlob, getLiferayThemeJSON};
