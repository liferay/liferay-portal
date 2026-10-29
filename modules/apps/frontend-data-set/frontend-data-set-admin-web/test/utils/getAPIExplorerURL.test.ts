/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getAPIExplorerURL from '../../src/main/resources/META-INF/resources/js/utils/getAPIExplorerURL';

const getPathContext = Liferay.ThemeDisplay
	.getPathContext as unknown as jest.Mock;

describe('getAPIExplorerURL', () => {
	afterEach(() => {
		getPathContext.mockReturnValue('/');
	});

	it('addresses the API Explorer and the document it opens', () => {
		getPathContext.mockReturnValue('');

		expect(getAPIExplorerURL('/headless-delivery/v1.0')).toBe(
			'http://localhost:8080/o/api?endpoint=http://localhost:8080/o/headless-delivery/v1.0/openapi.json'
		);
	});

	it('keeps the portal path context in both addresses', () => {
		getPathContext.mockReturnValue('/portal');

		expect(getAPIExplorerURL('/headless-delivery/v1.0')).toBe(
			'http://localhost:8080/portal/o/api?endpoint=http://localhost:8080/portal/o/headless-delivery/v1.0/openapi.json'
		);
	});
});
