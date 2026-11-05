/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getScopeSubtitle} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/getScopeSubtitle';

describe('getScopeSubtitle', () => {
	it.each([
		[
			'export',
			true,
			false,
			'export-complete-sites,-including-all-their-pages,-content,-and-settings',
		],
		[
			'export',
			false,
			true,
			'export-complete-spaces,-including-all-their-content',
		],
		[
			'export',
			true,
			true,
			'export-complete-sites-and-spaces,-including-all-their-pages,-content,-and-settings',
		],
		[
			'import',
			true,
			false,
			'import-complete-sites-from-your-file,-including-all-their-pages,-content,-and-settings',
		],
		[
			'import',
			false,
			true,
			'import-complete-spaces-from-your-file,-including-all-their-content',
		],
		[
			'import',
			true,
			true,
			'import-complete-sites-and-spaces-from-your-file,-including-all-their-pages,-content,-and-settings',
		],
	] as const)(
		'describes an %s with sites %s and spaces %s',
		(process, siteSelectionEnabled, spaceSelectionEnabled, expected) => {
			expect(
				getScopeSubtitle(process, {
					siteSelectionEnabled,
					spaceSelectionEnabled,
				})
			).toBe(expected);
		}
	);
});
