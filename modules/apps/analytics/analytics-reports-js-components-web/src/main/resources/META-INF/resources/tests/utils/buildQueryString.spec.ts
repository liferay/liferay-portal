/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {buildQueryString} from '../../js/utils/buildQueryString';

describe('buildQueryString', () => {
	it('returns a query string with a single parameter', () => {
		const result = buildQueryString({foo: 'bar'});

		expect(result).toBe('?foo=bar');
	});

	it('correctly encodes special characters in the value', () => {
		const result = buildQueryString({name: 'John & Jane'});

		expect(result).toBe('?name=John%20%26%20Jane');
	});

	it('not encodes an array of values', () => {
		const result = buildQueryString({
			foo: ['bar', 'baz'],
			name: 'John & Jane',
		});

		expect(result).toBe('?foo=bar,baz&name=John%20%26%20Jane');
	});

	it('ignores parameters with an empty string as value', () => {
		const result = buildQueryString({bar: 'baz', foo: ''});

		expect(result).toBe('?bar=baz');
	});

	it('ignores parameters based on shouldIgnoreParam function', () => {
		const shouldIgnoreParam = (value: string | string[]) =>
			value === 'ignore-this';

		const result = buildQueryString(
			{bar: 'valid', foo: 'ignore-this'},
			{shouldIgnoreParam}
		);

		expect(result).toBe('?bar=valid');
	});

	it('returns parameters sorted alphabetically by key', () => {
		const result = buildQueryString({a: '1', b: '2', c: '3'});

		expect(result).toBe('?a=1&b=2&c=3');
	});
});
