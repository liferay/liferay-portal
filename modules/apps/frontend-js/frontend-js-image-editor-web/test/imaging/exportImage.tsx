/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {editedImageMarkup} from '../../src/main/resources/META-INF/resources/js/imaging/exportImage';
import {initialEditState} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {
	Adjustments,
	Frame,
} from '../../src/main/resources/META-INF/resources/js/state/types';

const DATA_URL = 'data:image/jpeg;base64,AAAA';

function markup(
	adjustments: Partial<Adjustments> = {},
	frame: Partial<Frame> = {}
) {
	const state = initialEditState(1600, 1000);

	return editedImageMarkup(
		{
			...state,
			adjustments: {...state.adjustments, ...adjustments},
			frame: {...state.frame, ...frame},
		},
		DATA_URL
	);
}

describe('editedImageMarkup', () => {
	it('renders the image at the crop size', () => {
		expect(markup()).toContain('viewBox="0 0 1600 1000"');
	});

	it('leaves the image unfiltered when nothing is adjusted', () => {
		expect(markup()).not.toContain('filter="url(#export-filter)"');
	});

	it('applies the color pipeline to the exported image', () => {
		const output = markup({brightness: 40});

		expect(output).toContain('filter="url(#export-filter)"');
		expect(output).toContain('id="export-filter"');
		expect(output).toContain('slope="1.4"');
	});

	it('draws the frame over the exported picture', () => {
		expect(markup()).not.toContain('editor-frame');

		const output = markup({}, {kind: 'mat'});

		expect(output).toContain('class="editor-frame"');
		expect(output).toContain('stroke-width="40"');
	});
});

describe('the frame and the annotations', () => {
	const caption = {
		color: '#ffffff',
		fontFamily: 'sans-serif',
		fontSize: 48,
		id: 'text-1',
		kind: 'text' as const,
		text: 'Hello',
		x: 100,
		y: 900,
	};

	const order = (overAnnotations: boolean) => {
		const state = initialEditState(1600, 1000);

		const output = editedImageMarkup(
			{
				...state,
				frame: {...state.frame, kind: 'mat', overAnnotations},
				overlays: [caption],
			},
			DATA_URL
		);

		return [output.indexOf('editor-frame'), output.indexOf('<text')];
	};

	it('draws the annotations, then the frame over them by default', () => {
		const [frame, text] = order(true);

		expect(text).toBeGreaterThan(-1);
		expect(frame).toBeGreaterThan(text);
	});

	it('draws the frame first when it goes under the annotations', () => {
		const [frame, text] = order(false);

		expect(frame).toBeGreaterThan(-1);
		expect(text).toBeGreaterThan(frame);
	});
});
