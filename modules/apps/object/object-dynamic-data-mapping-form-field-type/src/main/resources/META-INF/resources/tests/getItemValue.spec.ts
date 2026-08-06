/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getItemValue} from '../js/ObjectRelationship/getItemValue';

describe('getItemValue', () => {
	it('falls back to the id when the value key is absent', () => {
		expect(getItemValue({id: 42}, 'value')).toBe(42);
	});

	it('keeps a zero value rather than falling back to the id', () => {
		expect(getItemValue({id: 42, value: 0}, 'value')).toBe(0);
	});

	it('returns the value key when both it and the id are present', () => {
		expect(getItemValue({id: 42, value: 7}, 'value')).toBe(7);
	});

	it('returns undefined when neither the value key nor the id resolves', () => {
		expect(getItemValue({title: 'Product'}, 'value')).toBeUndefined();
	});

	it('returns undefined when the item is undefined', () => {
		expect(getItemValue(undefined, 'value')).toBeUndefined();
	});

	it('returns undefined when the resolved value is not a number or a string', () => {
		expect(getItemValue({value: {nested: true}}, 'value')).toBeUndefined();
	});
});
