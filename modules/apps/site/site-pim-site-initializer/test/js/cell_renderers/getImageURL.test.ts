/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import getImageURL from '../../../src/main/resources/META-INF/resources/js/cell_renderers/getImageURL';

describe('getImageURL', () => {
	it('returns the URL of the first image with a file', () => {
		expect(
			getImageURL([
				{image: null},
				{image: {link: {href: '/first.png'}}},
				{image: {link: {href: '/second.png'}}},
			])
		).toBe('/first.png');
	});

	it('returns undefined when there are no images', () => {
		expect(getImageURL([])).toBeUndefined();
		expect(getImageURL(undefined)).toBeUndefined();
	});
});
