/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';

import '@testing-library/jest-dom';

import {
	FILTER_PRESETS,
	FilterDefs,
	isIdentityFilter,
} from '../../src/main/resources/META-INF/resources/js/imaging/FilterDefs';
import {
	Adjustments,
	DEFAULT_ADJUSTMENTS,
	FilterPreset,
} from '../../src/main/resources/META-INF/resources/js/state/types';

function markup(
	adjustments: Partial<Adjustments>,
	filter: FilterPreset = 'none'
): string {
	return renderToStaticMarkup(
		<svg>
			<defs>
				<FilterDefs
					adjustments={{...DEFAULT_ADJUSTMENTS, ...adjustments}}
					filter={filter}
					id="test-filter"
				/>
			</defs>
		</svg>
	);
}

describe('FilterDefs', () => {
	it('detects the identity pipeline', () => {
		expect(isIdentityFilter({...DEFAULT_ADJUSTMENTS}, 'none')).toBe(true);
		expect(
			isIdentityFilter({...DEFAULT_ADJUSTMENTS, brightness: 5}, 'none')
		).toBe(false);
		expect(isIdentityFilter({...DEFAULT_ADJUSTMENTS}, 'sepia')).toBe(false);
	});

	it('maps brightness to a linear transfer slope', () => {
		expect(markup({brightness: 20})).toContain('slope="1.2"');
	});

	it('keeps contrast centered on the midtones', () => {
		expect(markup({contrast: 50})).toContain('intercept="-0.25"');
	});

	it('maps saturation to a saturate matrix', () => {
		expect(markup({saturation: -100})).toContain('values="0"');
	});

	it('renders a tone curve table only when shadows or highlights are set', () => {
		expect(markup({})).not.toContain('tableValues');
		expect(markup({shadows: 50})).toContain('tableValues');
		expect(markup({highlights: 50})).toContain('tableValues');
	});

	it('lifts the blacks with positive shadows and clamps at zero', () => {
		expect(markup({shadows: 100})).toContain('tableValues="0.3500');
		expect(markup({shadows: -100})).toContain('tableValues="0.0000');
	});

	it('renders every preset without throwing', () => {
		expect(FILTER_PRESETS).toHaveLength(20);

		for (const preset of FILTER_PRESETS) {
			expect(markup({}, preset)).toContain('filter');
		}
	});

	it('emits a shared tone curve for a faded look', () => {
		expect(markup({}, 'fade')).toContain('tableValues');
	});

	it('emits per-channel curves for cross processing', () => {
		const output = markup({}, 'crossprocess');

		const tables = output.match(/tableValues="[^"]+"/g) ?? [];

		expect(tables).toHaveLength(3);
		expect(new Set(tables).size).toBe(3);
	});

	it('quantises the channels for the posterize preset', () => {
		expect(markup({}, 'posterize')).toContain('type="discrete"');
	});

	it('drops saturation to zero for the grayscale preset', () => {
		expect(markup({saturation: 40}, 'grayscale')).toContain('values="0"');
	});
});
