/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {VIEWPORT_SIZES} from '../../../../src/main/resources/META-INF/resources/page_editor/app/config/constants/viewportSizes';
import {getResponsiveConfig} from '../../../../src/main/resources/META-INF/resources/page_editor/app/utils/getResponsiveConfig';

// The viewport keys are flattened into the result, so the merged shape is only
// known at runtime.

const SAMPLE_RESPONSIVE_CONFIG: Record<string, unknown> = {
	nonViewport: 'yep',

	[VIEWPORT_SIZES.portraitMobile]: {
		deep: {
			common: 'commonMobile',
			mobile: 'deepMobile',
		},
		sample: 'mobileValue',
	},

	[VIEWPORT_SIZES.desktop]: {
		deep: {
			common: 'commonDesktop',
			desktop: 'deepDesktop',
		},
		sample: 'desktopValue',
	},
};

describe('getResponsiveConfig', () => {
	it('keeps non-viewport related configuration', () => {
		expect(
			getResponsiveConfig(
				SAMPLE_RESPONSIVE_CONFIG,
				VIEWPORT_SIZES.portraitMobile
			).nonViewport
		).toBe('yep');
	});

	it('merges deep objects', () => {
		expect(
			getResponsiveConfig(
				SAMPLE_RESPONSIVE_CONFIG,
				VIEWPORT_SIZES.portraitMobile
			).deep
		).toEqual({
			common: 'commonMobile',
			desktop: 'deepDesktop',
			mobile: 'deepMobile',
		});
	});

	it('always contains styles', () => {
		expect(
			getResponsiveConfig(
				SAMPLE_RESPONSIVE_CONFIG,
				VIEWPORT_SIZES.desktop
			).styles
		).toEqual({});
	});

	it('merges configuration in order', () => {
		expect(
			getResponsiveConfig(
				SAMPLE_RESPONSIVE_CONFIG,
				VIEWPORT_SIZES.portraitMobile
			).sample
		).toBe('mobileValue');
	});

	it('ignores smaller viewport sizes', () => {
		expect(
			getResponsiveConfig(
				SAMPLE_RESPONSIVE_CONFIG,
				VIEWPORT_SIZES.desktop
			).sample
		).toBe('desktopValue');
	});
});
