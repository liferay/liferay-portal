/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {toCamelCase} from '../../utils/string';

describe('toCamelCase(str, removeSpecialCharacters, keepFirstLetterCase)', () => {
	it('returns first letter in uppercase', () => {
		expect(toCamelCase('FirstLetterUpperCase', false, true)).toBe(
			'FirstLetterUpperCase'
		);
	});

	it('returns first letter in lowercase', () => {
		expect(toCamelCase('firstLetterLowerCase', false, true)).toBe(
			'firstLetterLowerCase'
		);
	});

	it('removes special characters', () => {
		expect(toCamelCase('specialCharacter$', true, false)).toBe(
			'specialCharacter'
		);
	});

	it('makes lower case string camelCase', () => {
		expect(toCamelCase('lower case', false, false)).toBe('lowerCase');
	});
});
