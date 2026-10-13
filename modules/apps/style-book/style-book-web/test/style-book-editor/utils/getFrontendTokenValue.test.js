/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getFrontendTokenValue} from '../../../src/main/resources/META-INF/resources/js/style-book-editor/utils/getFrontendTokenValue';

describe('getFrontendTokenValue', () => {
	it('returns the value of a token that is not linked', () => {
		expect(
			getFrontendTokenValue({
				frontendTokensValues: {'theme:a': {value: '#111'}},
				name: 'theme:a',
			})
		).toBe('#111');
	});

	it('returns the value of the last token in a chain of linked tokens', () => {
		const frontendTokensValues = {
			'theme:a': {name: 'theme:b', value: '#111'},
			'theme:b': {name: 'theme:c', value: 'var(--c)'},
			'theme:c': {value: '#333'},
		};

		expect(
			getFrontendTokenValue({frontendTokensValues, name: 'theme:a'})
		).toBe('#333');
		expect(
			getFrontendTokenValue({frontendTokensValues, name: 'theme:b'})
		).toBe('#333');
	});

	it('returns its own value when the linked token has no value', () => {
		expect(
			getFrontendTokenValue({
				frontendTokensValues: {
					'theme:a': {name: 'theme:b', value: 'var(--b)'},
				},
				name: 'theme:a',
			})
		).toBe('var(--b)');
	});

	it('returns the default value of the linked token when it has no value', () => {
		expect(
			getFrontendTokenValue({
				frontendTokens: {'theme:b': {defaultValue: '#222'}},
				frontendTokensValues: {
					'theme:a': {name: 'theme:b', value: 'var(--b)'},
				},
				name: 'theme:a',
			})
		).toBe('#222');
	});

	it('stops following links when the tokens reference each other', () => {
		const frontendTokensValues = {
			'theme:a': {name: 'theme:b', value: '#111'},
			'theme:b': {name: 'theme:a', value: '#222'},
		};

		expect(
			getFrontendTokenValue({frontendTokensValues, name: 'theme:a'})
		).toBe('#222');
		expect(
			getFrontendTokenValue({frontendTokensValues, name: 'theme:b'})
		).toBe('#111');
	});
});
