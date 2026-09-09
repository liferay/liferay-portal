/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import '@testing-library/jest-dom';

import * as surface from '../src/main/resources/META-INF/resources/js/index';

describe('the module surface', () => {
	it('exports the core contracts', () => {
		expect(Object.keys(surface).sort()).toEqual([
			'ImageEditor',
			'ImageEditorLoadError',
			'disposeLoadedImage',
			'liferayMessages',
			'loadImage',
			'setMessages',
		]);
	});
});
