/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {Locator} from '@playwright/test';

/**
 * Resolves whether the image the locator points to has actually rendered its
 * bitmap. Checking `naturalWidth` right after locating the element is racy: the
 * `<img>` can be in the DOM while its file is still downloading, so
 * `naturalWidth` reads `0` and the caller fails spuriously. This waits for the
 * `load` event when the image is not yet `complete`.
 */
export async function isImageLoaded(image: Locator): Promise<boolean> {
	return image.evaluate((element: HTMLImageElement) => {
		if (element.complete) {
			return element.naturalWidth > 0;
		}

		return new Promise<boolean>((resolve) => {
			element.addEventListener('load', () =>
				resolve(element.naturalWidth > 0)
			);
			element.addEventListener('error', () => resolve(false));
		});
	});
}
