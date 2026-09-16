/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {
	mirrorOverlay,
	overlayBounds,
	overlayHitBox,
	overlayLabel,
	overlayTransform,
} from '../../src/main/resources/META-INF/resources/js/imaging/overlayShapes';
import {TextOverlay} from '../../src/main/resources/META-INF/resources/js/state/types';

const CAPTION: TextOverlay = {
	color: '#ffffff',
	fontFamily: 'sans-serif',
	fontSize: 50,
	id: 'text-1',
	kind: 'text',
	text: 'Hello',
	x: 400,
	y: 500,
};

describe('a text annotation', () => {
	it('sits on its baseline, so its box rises above the anchor', () => {
		expect(overlayBounds(CAPTION)).toEqual({
			height: 60,
			width: 150,
			x: 400,
			y: 450,
		});
	});

	it('is named by what it says', () => {
		expect(overlayLabel(CAPTION)).toBe('text-x');
	});

	it('turns about its own center', () => {
		expect(overlayTransform(CAPTION)).toBeUndefined();

		expect(overlayTransform({...CAPTION, rotation: 45})).toBe(
			'rotate(45 475 480)'
		);
	});

	it('keeps its place when the photograph mirrors', () => {
		expect(mirrorOverlay(CAPTION, 1000)).toMatchObject({x: 450});

		expect(mirrorOverlay({...CAPTION, rotation: 30}, 1000)).toMatchObject({
			rotation: -30,
		});
	});

	it('keeps a full size target when the caption is tiny', () => {
		const footnote = {...CAPTION, fontSize: 10, text: 'a'};

		expect(overlayHitBox(footnote, 24)).toEqual({
			height: 24,
			width: 24,
			x: 393,
			y: 484,
		});
	});
});
