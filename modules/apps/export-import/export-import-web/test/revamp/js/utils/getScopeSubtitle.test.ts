/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getScopeSubtitle} from '../../../../src/main/resources/META-INF/resources/revamp/js/utils/getScopeSubtitle';

describe('getScopeSubtitle', () => {
	it('describes the sites of an export', () => {
		expect(getScopeSubtitle('export', true, false)).toBe(
			'export-complete-sites,-including-all-their-pages,-content,-and-settings'
		);
	});

	it('describes the spaces of an export', () => {
		expect(getScopeSubtitle('export', false, true)).toBe(
			'export-complete-spaces,-including-all-their-content'
		);
	});

	it('describes the sites and spaces of an export', () => {
		expect(getScopeSubtitle('export', true, true)).toBe(
			'export-complete-sites-and-spaces,-including-all-their-pages,-content,-and-settings'
		);
	});

	it('describes the sites of an import', () => {
		expect(getScopeSubtitle('import', true, false)).toBe(
			'import-complete-sites-from-your-file,-including-all-their-pages,-content,-and-settings'
		);
	});

	it('describes the spaces of an import', () => {
		expect(getScopeSubtitle('import', false, true)).toBe(
			'import-complete-spaces-from-your-file,-including-all-their-content'
		);
	});

	it('describes the sites and spaces of an import', () => {
		expect(getScopeSubtitle('import', true, true)).toBe(
			'import-complete-sites-and-spaces-from-your-file,-including-all-their-pages,-content,-and-settings'
		);
	});
});
