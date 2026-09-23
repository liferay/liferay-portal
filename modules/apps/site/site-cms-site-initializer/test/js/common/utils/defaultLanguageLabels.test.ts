/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getDefaultLanguageLabel} from '../../../../src/main/resources/META-INF/resources/js/common/utils/defaultLanguageLabels';

describe('getDefaultLanguageLabel', () => {
	it('Returns the default language label when the key is present', () => {
		const defaultLanguageLabels = {
			labels: {
				'option': 'Option',
				'select-related-content': 'Select related content',
			},
			locale: 'en_US',
		};

		expect(
			getDefaultLanguageLabel({defaultLanguageLabels, key: 'option'})
		).toBe('Option');

		expect(
			getDefaultLanguageLabel({
				defaultLanguageLabels,
				key: 'select-related-content',
			})
		).toBe('Select related content');
	});

	it('Falls back to Liferay.Language.get when the key is missing', () => {
		expect(
			getDefaultLanguageLabel({
				defaultLanguageLabels: {labels: {}, locale: 'en_US'},
				key: 'untracked-key',
			})
		).toBe('untracked-key');
	});
});
