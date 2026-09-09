/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import {sessionKeyOf} from '../src/main/resources/META-INF/resources/js/ImageEditor';
import {LoadedImage} from '../src/main/resources/META-INF/resources/js/imaging/loadImage';

const image = (previewUrl: string): LoadedImage => ({
	blob: new Blob(),
	fileName: 'a.jpg',
	height: 800,
	previewUrl,
	type: 'image/jpeg',
	width: 1200,
});

describe('the editing session is keyed to the image (R2-002)', () => {
	it('derives a different session key for a different image', () => {
		expect(sessionKeyOf(image('blob:a'))).not.toBe(
			sessionKeyOf(image('blob:b'))
		);
		expect(sessionKeyOf(image('blob:a'))).toBe(
			sessionKeyOf(image('blob:a'))
		);
	});
});
