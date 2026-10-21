/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import toIdentifier from '../../src/main/resources/META-INF/resources/js/utils/toIdentifier';

describe('toIdentifier', () => {
	it('lowercases the name', () => {
		expect(toIdentifier('Summarize')).toBe('summarize');
	});

	it('replaces spaces and symbols with single hyphens', () => {
		expect(toIdentifier('Summarize Page & Comments')).toBe(
			'summarize-page-comments'
		);
	});

	it('collapses consecutive separators', () => {
		expect(toIdentifier('summarize -- page')).toBe('summarize-page');
	});

	it('trims leading and trailing separators', () => {
		expect(toIdentifier('  Summarize Page!  ')).toBe('summarize-page');
	});

	it('keeps digits', () => {
		expect(toIdentifier('Top 10 Results')).toBe('top-10-results');
	});

	it('returns an empty string when nothing remains', () => {
		expect(toIdentifier('!!!')).toBe('');
	});
});
