/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

import {openModal} from 'frontend-js-components-web';

export default function openSigningModal({title, url}) {
	const currentURL = new URL(window.location.href);

	currentURL.searchParams.delete('dsRequestId');

	window.history.replaceState(null, '', currentURL.toString());

	// XSS: title is escaped by React, which renders it as text in Modal

	openModal({
		size: 'full-screen',
		title,
		url,
	});
}
