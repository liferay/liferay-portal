/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {hasLegacySeparator} from '../../components/ObjectDetails/SeparatorContainer';

describe('hasLegacySeparator(value)', () => {
	it('returns true if the value is exactly the string character l', () => {
		expect(hasLegacySeparator('l')).toBe(true);
	});

	it('returns false if the value is different from the string character l', () => {
		expect(hasLegacySeparator('account')).toBe(false);
		expect(hasLegacySeparator('c_custom')).toBe(false);
		expect(hasLegacySeparator('lll')).toBe(false);
		expect(hasLegacySeparator('')).toBe(false);
		expect(hasLegacySeparator(undefined)).toBe(false);
	});
});
