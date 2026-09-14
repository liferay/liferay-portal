/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	ADJUSTMENT_KEYS,
	FILTER_PRESETS,
	RATIO_PRESETS,
	resolveConfig,
} from '../src/main/resources/META-INF/resources/js/editorConfig';

describe('resolveConfig', () => {
	it('exposes every adjustment slider by default', () => {
		expect(resolveConfig().adjustments).toEqual(ADJUSTMENT_KEYS);
	});

	it('switches the adjustment sliders off with false', () => {
		expect(resolveConfig({adjustments: false}).adjustments).toEqual([]);
	});

	it('narrows the adjustment sliders to a subset in slider order', () => {
		expect(
			resolveConfig({adjustments: {sliders: ['shadows', 'contrast']}})
				.adjustments
		).toEqual(['contrast', 'shadows']);
	});

	it('exposes every filter preset by default', () => {
		expect(resolveConfig().filters).toEqual(FILTER_PRESETS);
	});

	it('switches the filter gallery off with false', () => {
		expect(resolveConfig({filters: false}).filters).toEqual([]);
	});

	it('narrows the filter presets to a subset in gallery order', () => {
		expect(
			resolveConfig({filters: {presets: ['sepia', 'none']}}).filters
		).toEqual(['none', 'sepia']);
	});

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
