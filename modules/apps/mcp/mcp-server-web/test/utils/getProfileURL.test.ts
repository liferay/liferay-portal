/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getProfileURL from '../../src/main/resources/META-INF/resources/js/utils/getProfileURL';

const getPathContext = Liferay.ThemeDisplay
	.getPathContext as unknown as jest.Mock;

describe('getProfileURL', () => {
	afterEach(() => {
		getPathContext.mockReturnValue('/');
	});

	beforeEach(() => {
		getPathContext.mockReturnValue('');
	});

	it('appends the profile path to the base URL', () => {
		expect(getProfileURL('tool-search')).toBe(
			'http://localhost:8080/o/mcp/tool-search'
		);
	});

	it('includes the portal context path', () => {
		getPathContext.mockReturnValue('/liferay');

		expect(getProfileURL('tool-search')).toBe(
			'http://localhost:8080/liferay/o/mcp/tool-search'
		);
	});

	it('returns the base URL without a profile path', () => {
		expect(getProfileURL('')).toBe('http://localhost:8080/o/mcp');
	});
});
