/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	language,
	signed_in,
} from '../src/main/resources/META-INF/resources/custom-attributes/index';

describe('custom-attributes', () => {
	describe('custom attribute language', () => {
		it('works and returns the Liferay language id', async () => {
			const value = language();

			expect(typeof value).toBe('string');
			expect(value).toBe('en_US');
		});
	});

	describe('custom attribute signed_in', () => {
		it('works and returns a boolean', async () => {
			const value = signed_in();

			expect(typeof value).toBe('boolean');
		});
	});
});
