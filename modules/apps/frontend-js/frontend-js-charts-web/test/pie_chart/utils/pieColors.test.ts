/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {getPieSliceColors} from '../../../src/main/resources/META-INF/resources/js/pie_chart/utils/pieColors';

describe('getPieSliceColors', () => {
	it('returns the per-datum color when one is set', () => {
		const colors = getPieSliceColors([
			{color: '#123456', label: 'A', value: 1},
		]);

		expect(colors[0]).toBe('#123456');
	});

	it('falls back to a shared accessible color when unset', () => {
		const colors = getPieSliceColors([{label: 'A', value: 1}]);

		expect(colors[0]).toMatch(/^var\(--/);
	});

	it('falls back to a shared accessible color when the override is empty', () => {
		const colors = getPieSliceColors([{color: '', label: 'A', value: 1}]);

		expect(colors[0]).toMatch(/^var\(--/);
	});

	it('yields one color per datum beyond the base families', () => {
		const data = Array.from({length: 15}, (_, index) => ({
			label: `slice-${index}`,
			value: index + 1,
		}));

		const colors = getPieSliceColors(data);

		expect(colors).toHaveLength(15);
		colors.forEach((color) => expect(color).toMatch(/^var\(--/));
	});
});
