/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import required from '../../src/main/resources/META-INF/resources/js/utils/required';

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
