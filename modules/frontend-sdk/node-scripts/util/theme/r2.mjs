/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import rtlcss from 'rtlcss';

/**
 * Flips the given CSS for right-to-left languages.
 *
 * @param {string} css
 * @return {string}
 */
export default function r2(css) {
	return rtlcss.process(css.replaceAll('/* @noflip */', '/*rtl:ignore*/'));
}
