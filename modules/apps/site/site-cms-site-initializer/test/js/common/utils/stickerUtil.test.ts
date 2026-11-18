/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {
	getInitials,
	getStickerColor,
} from '../../../../src/main/resources/META-INF/resources/js/common/utils/stickerUtil';

describe('getInitials', () => {
	it.each([
		['the first letter of the first two words', 'Andrés Molina', 'AM'],
		['only the first two words', 'Marketing Team Spain', 'MT'],
		['the first letter of a single word', 'marketing', 'M'],
		['ignoring extra spaces', '  ana   torres ', 'AT'],
		['an empty string for an empty name', '', ''],
	])('returns %s', (_description, name, expectedInitials) => {
		expect(getInitials(name)).toBe(expectedInitials);
	});
});

describe('getStickerColor', () => {
	it('returns the same outline color for the same key', () => {
		expect(getStickerColor('ERC-1')).toBe(getStickerColor('ERC-1'));
	});

	it('returns one of the ten outline colors', () => {
		for (const key of [
			'',
			'a',
			'ERC-1',
			'2c3f9a7e-0b1d-4c6e-9f2a-1d8e7b6c5a4f',
		]) {
			expect(getStickerColor(key)).toMatch(/^outline-\d$/);
		}
	});
});
