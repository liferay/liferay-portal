/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import domAlign from 'dom-align';

type AlignBase = {
	offset?: readonly [number, number];
	overflow?: {adjustX: boolean; adjustY: boolean; alwaysByViewport?: boolean};
	points?: readonly [string, string];
	targetOffset?: readonly [string, string];
};

type AlignProps<T, K> = AlignBase & {
	sourceElement: K;
	targetElement: T;
};

function isRtl<T extends HTMLElement>(element: T) {
	return window.getComputedStyle(element).direction === 'rtl';
}

export function doAlign<T extends HTMLElement, K extends HTMLElement>({
	sourceElement,
	targetElement,
	...config
}: AlignProps<T, K>): Required<AlignBase> {
	const rtl = isRtl(sourceElement);

	// `dom-align` clears the stale side itself, but only after measuring the
	// source, so clear it first. Leave the side it writes: measured at its
	// static position, an auto width source has a width it will not keep.

	sourceElement.style.bottom = '';
	sourceElement.style[rtl ? 'left' : 'right'] = '';

	return domAlign(sourceElement, targetElement, {
		...config,
		useCssRight: rtl,
	});
}
