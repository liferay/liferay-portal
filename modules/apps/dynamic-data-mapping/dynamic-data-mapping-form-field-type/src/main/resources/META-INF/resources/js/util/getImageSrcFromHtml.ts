/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export function getImagesSrcFromHtml(html: string) {
	const document = new DOMParser().parseFromString(html, 'text/html');

	const images = document.querySelectorAll('img');

	if (!images.length) {
		return [];
	}

	return Array.from(images).map((image) => image.getAttribute('src'));
}
