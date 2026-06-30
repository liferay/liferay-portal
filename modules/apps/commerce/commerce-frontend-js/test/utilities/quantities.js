/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getMinQuantity} from '../../src/main/resources/META-INF/resources/utilities/quantities';

describe('getMinQuantity', () => {
	it('returns the minimum quantity when it is already a multiple of the multiple quantity', () => {
		expect(getMinQuantity(10, 5)).toBe('10');
	});

	it('returns the multiple quantity as the first selectable quantity when the minimum order quantity is lower than the multiple order quantity', () => {
		expect(getMinQuantity(4, 5)).toBe('5');
	});

	it('rounds the minimum up to the next multiple when the minimum order quantity is higher than the multiple order quantity', () => {
		expect(getMinQuantity(6, 5)).toBe('10');
	});
});
