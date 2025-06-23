/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {VIEWPORT_SIZES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {getResetLabelByViewport} from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getResetLabelByViewport';

jest.mock(
	'../../../../src/main/resources/META-INF/resources/page_editor/app/config/index',
	() => ({
		config: {
			availableViewportSizes: {
				desktop: {label: 'desktop'},
				landscapeMobile: {label: 'landscapeMobile'},
				portraitMobile: {label: 'portraitMobile'},
				tablet: {label: 'tablet'},
			},
		},
	})
);

const getLabel = (viewport) => `reset-to-${viewport}-value`;

describe('getResetLabelByViewport', () => {
	test.each([
		[VIEWPORT_SIZES.desktop, getLabel('initial')],
		[VIEWPORT_SIZES.tablet, getLabel(VIEWPORT_SIZES.desktop)],
		[VIEWPORT_SIZES.landscapeMobile, getLabel(VIEWPORT_SIZES.tablet)],
		[
			VIEWPORT_SIZES.portraitMobile,
			getLabel(VIEWPORT_SIZES.landscapeMobile),
		],
	])('when the viewport is %p returns %p', (viewport, title) => {
		expect(getResetLabelByViewport(viewport)).toBe(title);
	});
});
