/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import ClipboardJS from 'clipboard';
import {openToast} from 'frontend-js-components-web';

export default function CopySitemapURL({namespace}) {
	const button = document.getElementById(`${namespace}copySitemapURLButton`);

	if (!button) {
		return;
	}

	const clipboard = new ClipboardJS(button);

	clipboard.on('success', () =>
		openToast({
			message: Liferay.Language.get('copied-link-to-the-clipboard'),
		})
	);

	return {
		dispose() {
			clipboard.destroy();
		},
	};
}
