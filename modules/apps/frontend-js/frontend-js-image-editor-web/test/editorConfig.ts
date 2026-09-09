/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	RATIO_PRESETS,
	resolveConfig,
} from '../src/main/resources/META-INF/resources/js/editorConfig';

describe('resolveConfig', () => {
	it('exposes everything by default', () => {
		expect(resolveConfig().crop).toEqual({
			enabled: true,
			ratios: RATIO_PRESETS,
			rotate: true,
			straighten: true,
		});
	});

	it('switches the crop tools off with false', () => {
		expect(resolveConfig({crop: false}).crop).toEqual({
			enabled: false,
			ratios: [],
			rotate: false,
			straighten: false,
		});
	});

	it('narrows the ratios to a subset in preset order', () => {
		expect(
			resolveConfig({crop: {ratios: ['16:9', 'custom']}}).crop.ratios
		).toEqual(['custom', '16:9']);
	});

	it('ignores names that do not exist', () => {
		expect(
			resolveConfig({crop: {ratios: ['1:1', 'nope' as never]}}).crop
				.ratios
		).toEqual(['1:1']);
	});

	it('turns crop features off individually', () => {
		expect(resolveConfig({crop: {rotate: false}}).crop).toMatchObject({
			enabled: true,
			rotate: false,
			straighten: true,
		});
	});
});
