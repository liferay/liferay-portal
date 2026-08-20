/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isValidEmailAddress} from '../../../js/EmailAddress/util/emailAddressUtil';

describe('isValidEmailAddress', () => {
	it.each([
		'a.b@x.co',
		"o'brien@example.com",
		'Test@Example.COM',
		'test@example.com',
		'user+tag@sub.example.com',
	])('accepts %s', (value) => {
		expect(isValidEmailAddress(value)).toBe(true);
	});

	it.each([
		'a@b@c.com',
		'test @example.com',
		'test@',
		'test@example.c',
		'user()@example.com',
		'user,name@example.com',
		'user;@example.com',
		'user<>@example.com',
		'user@-bad.com',
		'user@exa_mple.com',
	])('rejects %s', (value) => {
		expect(isValidEmailAddress(value)).toBe(false);
	});
});
