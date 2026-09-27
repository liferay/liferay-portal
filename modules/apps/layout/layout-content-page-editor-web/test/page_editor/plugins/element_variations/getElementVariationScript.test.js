/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getElementVariationScript from '../../../../src/main/resources/META-INF/resources/page_editor/plugins/element_variations/getElementVariationScript';

describe('getElementVariationScript', () => {
	it('runs the custom javascript against the target element', () => {
		const script = getElementVariationScript({
			js: 'element.classList.add("featured");',
			targetElement: '#banner',
		});

		expect(script).toContain(
			'const element = document.querySelector("#banner");'
		);
		expect(script).toContain('element.classList.add("featured");');
	});
});
