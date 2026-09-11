/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {editedImageMarkup} from '../../src/main/resources/META-INF/resources/js/imaging/exportImage';
import {initialEditState} from '../../src/main/resources/META-INF/resources/js/state/editorReducer';
import {Adjustments} from '../../src/main/resources/META-INF/resources/js/state/types';

const DATA_URL = 'data:image/jpeg;base64,AAAA';

function markup(adjustments: Partial<Adjustments> = {}) {
	const state = initialEditState(1600, 1000);

	return editedImageMarkup(
		{...state, adjustments: {...state.adjustments, ...adjustments}},
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
});
