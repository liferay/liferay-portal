/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getTimeSeparator} from '../../utils/datetime';

describe('getTimeSeparator', () => {
	it('returns "." for locale formats that use period as the time separator', () => {
		expect(getTimeSeparator('DD.MM.YYYY HH.mm')).toBe('.');
	});

	it('returns ":" for locale formats that use colon as the time separator', () => {
		expect(getTimeSeparator('MM/DD/YYYY HH:mm')).toBe(':');
	});

	it('returns ":" for 12-hour formats', () => {
		expect(getTimeSeparator('MM/DD/YYYY h:mm A')).toBe(':');
	});

	it('returns ":" when the format has no time portion', () => {
		expect(getTimeSeparator('MM/DD/YYYY')).toBe(':');
	});

	it('returns ":" for an empty format', () => {
		expect(getTimeSeparator('')).toBe(':');
	});
});
