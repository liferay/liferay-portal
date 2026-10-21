/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import toODataStringLiteral from '../../src/main/resources/META-INF/resources/js/utils/toODataStringLiteral';

describe('toODataStringLiteral', () => {
	it('wraps the value in single quotes', () => {
		expect(toODataStringLiteral('custom')).toBe("'custom'");
	});

	it('escapes embedded single quotes by doubling them', () => {
		expect(toODataStringLiteral("O'Brien's")).toBe("'O''Brien''s'");
	});
});
