/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';

import '@testing-library/jest-dom';

import {
	FilterDefs,
	isIdentityFilter,
} from '../../src/main/resources/META-INF/resources/js/imaging/FilterDefs';
import {
	Adjustments,
	DEFAULT_ADJUSTMENTS,
} from '../../src/main/resources/META-INF/resources/js/state/types';

function markup(adjustments: Partial<Adjustments>): string {
	return renderToStaticMarkup(
		<svg>
			<defs>
				<FilterDefs
					adjustments={{...DEFAULT_ADJUSTMENTS, ...adjustments}}
					id="test-filter"
				/>
			</defs>
		</svg>
	);
}

describe('FilterDefs', () => {
	it('detects the identity pipeline', () => {
		expect(isIdentityFilter({...DEFAULT_ADJUSTMENTS})).toBe(true);
		expect(isIdentityFilter({...DEFAULT_ADJUSTMENTS, brightness: 5})).toBe(
			false
		);
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
});
