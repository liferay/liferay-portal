/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	EnabledGroupSelection,
	getEnabledGroupSelection,
} from './getEnabledGroupSelection';

const SUBTITLES: Record<
	'export' | 'import',
	Record<EnabledGroupSelection, string>
> = {
	export: {
		sites: Liferay.Language.get(
			'export-complete-sites,-including-all-their-pages,-content,-and-settings'
		),
		sitesAndSpaces: Liferay.Language.get(
			'export-complete-sites-and-spaces,-including-all-their-pages,-content,-and-settings'
		),
		spaces: Liferay.Language.get(
			'export-complete-spaces,-including-all-their-content'
		),
	},
	import: {
		sites: Liferay.Language.get(
			'import-complete-sites-from-your-file,-including-all-their-pages,-content,-and-settings'
		),
		sitesAndSpaces: Liferay.Language.get(
			'import-complete-sites-and-spaces-from-your-file,-including-all-their-pages,-content,-and-settings'
		),
		spaces: Liferay.Language.get(
			'import-complete-spaces-from-your-file,-including-all-their-content'
		),
	},
};

export function getScopeSubtitle(
	process: 'export' | 'import',
	options: {siteSelectionEnabled?: boolean; spaceSelectionEnabled?: boolean}
) {
	return SUBTITLES[process][getEnabledGroupSelection(options) ?? 'sites'];
}
