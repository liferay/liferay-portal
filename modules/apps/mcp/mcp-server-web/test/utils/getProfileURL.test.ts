/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getProfileURL from '../../src/main/resources/META-INF/resources/js/utils/getProfileURL';

describe('getProfileURL', () => {
	it('appends the profile path to the base URL', () => {
		expect(getProfileURL('tool-search')).toBe(
			`${window.location.origin}/o/mcp/tool-search`
		);
	});

	it('returns the base URL without a profile path', () => {
		expect(getProfileURL('')).toBe(`${window.location.origin}/o/mcp`);
	});
});
