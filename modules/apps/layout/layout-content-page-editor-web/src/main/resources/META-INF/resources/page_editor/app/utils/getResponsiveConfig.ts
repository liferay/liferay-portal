/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {isNullOrUndefined} from '@liferay/layout-js-components-web';

import {CommonStyles} from '../../types/layout_data/BaseLayoutDataItem';
import {VIEWPORT_SIZES, ViewportSize} from '../config/constants/viewportSizes';

const ORDERED_VIEWPORT_SIZES: ViewportSize[] = [
	VIEWPORT_SIZES.desktop,
	VIEWPORT_SIZES.tablet,
	VIEWPORT_SIZES.landscapeMobile,
	VIEWPORT_SIZES.portraitMobile,
];

export function getResponsiveConfig<T extends object>(
	config: T,
	viewportSize: ViewportSize
): T & {styles: NonNullable<CommonStyles['styles']>} {
	const viewportSizeIndex = ORDERED_VIEWPORT_SIZES.indexOf(viewportSize);

	const source = config as Record<string, unknown>;

	let responsiveConfig: Record<string, unknown> = {
		styles: {},
	};

	Object.keys(source)
		.filter((key) => !ORDERED_VIEWPORT_SIZES.some((size) => size === key))
		.forEach((key) => {
			responsiveConfig[key] = source[key];
		});

	for (let i = 0; i <= viewportSizeIndex; i++) {
		responsiveConfig = mergeDeep(
			responsiveConfig,
			(source[ORDERED_VIEWPORT_SIZES[i]] as Record<string, unknown>) || {}
		);
	}

	return responsiveConfig as T & {
		styles: NonNullable<CommonStyles['styles']>;
	};
}

function mergeDeep(
	...objects: Record<string, unknown>[]
): Record<string, unknown> {
	const target: Record<string, unknown> = {};

	objects.forEach((object) => {
		Object.keys(object).forEach((key) => {
			if (
				typeof object[key] === 'object' &&
				object[key] !== null &&
				typeof target[key] === 'object' &&
				target[key] !== null
			) {
				target[key] = mergeDeep(
					target[key] as Record<string, unknown>,
					object[key] as Record<string, unknown>
				);
			}
			else if (!isNullOrUndefined(object[key]) && object[key] !== '') {
				target[key] = object[key];
			}
		});
	});

	return target;
}
