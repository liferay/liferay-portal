/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getOperatorLabel,
	getOperators,
} from '../../../src/main/resources/META-INF/resources/js/constants/operators';

describe('getOperators', () => {
	it('returns equality operators for a select criteria', () => {
		expect(getOperators('select', 'string')).toEqual(['eq', 'not_eq']);
	});

	it('returns set operators for a select criteria of type set', () => {
		expect(getOperators('select', 'set')).toEqual([
			'includes',
			'not_includes',
		]);
	});

	it('returns set operators for a text criteria of type set', () => {
		expect(getOperators('text', 'set')).toEqual([
			'includes',
			'not_includes',
		]);
	});
});

describe('getOperatorLabel', () => {
	it('labels the set operators as contains and does not contain', () => {
		expect(getOperatorLabel('includes', 'select')).toBe('contains');
		expect(getOperatorLabel('not_includes', 'select')).toBe(
			'does-not-contain'
		);
	});
});
