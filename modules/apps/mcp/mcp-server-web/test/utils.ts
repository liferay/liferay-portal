/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	isSystemMask,
	required,
	toODataStringLiteral,
} from '../src/main/resources/META-INF/resources/js/utils';

import type {DataMask} from '../src/main/resources/META-INF/resources/js/types';

describe('required', () => {
	it('returns an error message for an empty value', () => {
		expect(required('')).toBe('this-field-is-required');
	});

	it('returns an error message for a whitespace-only value', () => {
		expect(required('   ')).toBe('this-field-is-required');
	});

	it('returns undefined for a non-empty value', () => {
		expect(required('summarize-page')).toBeUndefined();
	});
});

describe('toODataStringLiteral', () => {
	it('wraps the value in single quotes', () => {
		expect(toODataStringLiteral('custom')).toBe("'custom'");
	});

	it('escapes embedded single quotes by doubling them', () => {
		expect(toODataStringLiteral("O'Brien's")).toBe("'O''Brien''s'");
	});
});

describe('isSystemMask', () => {
	const dataMask = (key: string): DataMask => ({
		detectionRegex: '\\d+',
		maskType: {key: key as DataMask['maskType']['key'], name: key},
		name: 'mask',
		replacementValue: '[X]',
	});

	it('returns true for a system mask', () => {
		expect(isSystemMask(dataMask('system'))).toBe(true);
	});

	it('returns false for a custom mask', () => {
		expect(isSystemMask(dataMask('custom'))).toBe(false);
	});

	it('returns false when there is no mask', () => {
		expect(isSystemMask(null)).toBe(false);
	});
});
