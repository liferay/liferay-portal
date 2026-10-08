/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const TEMPLATE_LANG_FTL = 'ftl';
const TEMPLATE_LANG_VM = 'vm';

export function getBaseThemeGlob(themeConfig) {
	const {templateLanguage} = themeConfig;
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
