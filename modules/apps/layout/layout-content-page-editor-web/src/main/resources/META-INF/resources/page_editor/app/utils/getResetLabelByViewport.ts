/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {sub} from 'frontend-js-web';

import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';
import {config} from '../config/index';

const ORDERED_VIEWPORT_SIZES = [
	VIEWPORT_SIZES.desktop,
	VIEWPORT_SIZES.tablet,
	VIEWPORT_SIZES.landscapeMobile,
	VIEWPORT_SIZES.portraitMobile,
];

export function getResetLabelByViewport(selectedViewportSize: ViewportSize) {
	let previousViewport = Liferay.Language.get('initial');

	if (selectedViewportSize !== VIEWPORT_SIZES.desktop) {
		previousViewport =
			config.availableViewportSizes[
				ORDERED_VIEWPORT_SIZES[
					ORDERED_VIEWPORT_SIZES.indexOf(selectedViewportSize) - 1
				]
			].label;
	}

	return sub(Liferay.Language.get('reset-to-x-value'), previousViewport);
}
