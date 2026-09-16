/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import React from 'react';
import {renderToStaticMarkup} from 'react-dom/server';

import '@testing-library/jest-dom';

import {FrameShape} from '../../src/main/resources/META-INF/resources/js/imaging/frameShapes';
import {
	CropRect,
	Frame,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const FRAME: Frame = {
	color: '#ffffff',
	kind: 'mat',
	offset: 0,
	overAnnotations: true,
	size: 4,
};

const FULL: CropRect = {height: 1000, width: 2000, x: 0, y: 0};

function attributes(crop: CropRect, frame: Frame) {
	const markup = renderToStaticMarkup(
		<svg>
			<FrameShape crop={crop} frame={frame} />
		</svg>
	);

	return Object.fromEntries(
		[...markup.matchAll(/([a-z-]+)="([^"]+)"/g)].map(([, key, value]) => [
			key,
			value,
		])
	);
}

describe('a frame', () => {
	it('draws nothing at all when there is none', () => {
		expect(
			renderToStaticMarkup(
				<FrameShape crop={FULL} frame={{...FRAME, kind: 'none'}} />
			)
		).toBe('');
	});

	it('measures itself against the crop, not the image', () => {
		const full = attributes(FULL, FRAME);

		expect(full['stroke-width']).toBe('40');
		expect(full.x).toBe('20');
		expect(full.width).toBe('1960');
	});

	it('reframes itself when the crop moves and shrinks', () => {
		const cropped = attributes(
			{height: 500, width: 500, x: 300, y: 200},
			FRAME
		);

		expect(cropped['stroke-width']).toBe('20');
		expect(cropped.x).toBe('310');
		expect(cropped.y).toBe('210');
		expect(cropped.width).toBe('480');
	});

	it('offsets the frame from the edge when asked', () => {
		const offset = attributes(FULL, {...FRAME, offset: 5});

		expect(offset.x).toBe('70');
	});

	it('takes its colour from the frame', () => {
		expect(attributes(FULL, {...FRAME, color: '#ff0000'}).stroke).toBe(
			'#ff0000'
		);
	});
});
