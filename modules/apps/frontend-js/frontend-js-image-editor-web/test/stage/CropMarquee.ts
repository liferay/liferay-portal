/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {applyResizeModifiers} from '../../src/main/resources/META-INF/resources/js/stage/CropMarquee';

const ORIGIN = {height: 400, width: 800, x: 100, y: 100};

describe('applyResizeModifiers', () => {
	it('passes the base rectangle through without modifiers', () => {
		const base = {height: 400, width: 900, x: 100, y: 100};

		expect(
			applyResizeModifiers(
				base,
				ORIGIN,
				{right: true},
				{
					center: false,
					proportional: false,
				}
			)
		).toEqual(base);
	});

	it('keeps the origin proportions on a shift edge drag', () => {
		const base = {height: 400, width: 1000, x: 100, y: 100};

		const result = applyResizeModifiers(
			base,
			ORIGIN,
			{right: true},
			{
				center: false,
				proportional: true,
			}
		);

		expect(result).toEqual({height: 500, width: 1000, x: 100, y: 50});
	});

	it('anchors the opposite corner on a shift corner drag', () => {
		const base = {height: 450, width: 1000, x: 100, y: 100};

		const result = applyResizeModifiers(
			base,
			ORIGIN,
			{bottom: true, right: true},
			{center: false, proportional: true}
		);

		expect(result).toEqual({height: 500, width: 1000, x: 100, y: 100});
	});

	it('resizes around the center with alt', () => {
		const base = {height: 400, width: 850, x: 100, y: 100};

		const result = applyResizeModifiers(
			base,
			ORIGIN,
			{right: true},
			{
				center: true,
				proportional: false,
			}
		);

		expect(result).toEqual({height: 400, width: 900, x: 50, y: 100});
	});

	it('combines alt and shift into a centered proportional resize', () => {
		const base = {height: 400, width: 900, x: 100, y: 100};

		const result = applyResizeModifiers(
			base,
			ORIGIN,
			{right: true},
			{
				center: true,
				proportional: true,
			}
		);

		expect(result).toEqual({height: 500, width: 1000, x: 0, y: 50});
	});

	it('ignores modifiers for a plain move gesture', () => {
		const base = {height: 400, width: 800, x: 300, y: 200};

		expect(
			applyResizeModifiers(
				base,
				ORIGIN,
				{},
				{
					center: true,
					proportional: true,
				}
			)
		).toEqual(base);
	});
});
