/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import previousPage from '../../../../../src/main/resources/META-INF/resources/js/custom/form/thunks/previousPage.es';
import {evaluate} from '../../../../../src/main/resources/META-INF/resources/js/utils/evaluation.es';

const PAGES = [
	{enabled: true, rows: [], title: ''},
	{enabled: true, rows: [], title: ''},
	{enabled: true, rows: [], title: ''},
];

describe('previousPage', () => {
	const originalThemeDisplay = themeDisplay;

	beforeEach(() => {
		themeDisplay = {
			...originalThemeDisplay,
			getSiteGroupId: () => '20126',
		};
	});

	afterEach(() => {
		jest.useRealTimers();

		themeDisplay = originalThemeDisplay;
	});

	it('cancels the pending evaluation of the same form', () => {
		jest.useFakeTimers();

		fetch.mockResponseOnce(JSON.stringify(PAGES));

		const evaluatorContext = {
			activePage: 1,
			containerId: '_portletNamespace_dataEngineLayoutRenderer1',
			defaultLanguageId: 'en_US',
			editingLanguageId: 'en_US',
			pages: PAGES,
			portletNamespace: '_portletNamespace_',
		};

		evaluate('text', evaluatorContext);

		previousPage(evaluatorContext)(jest.fn());

		jest.runAllTimers();

		expect(fetch).toHaveBeenCalledTimes(1);
	});
});
