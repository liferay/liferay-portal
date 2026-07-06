/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const getIcon = ({symbol}: {symbol: string}): string => {
	const spritemapUrl = new URL(Liferay.Icons.spritemap, window.location.href);

	spritemapUrl.host = window.location.host;
	spritemapUrl.protocol = window.location.protocol;

	return `<svg xmlns="http://www.w3.org/2000/svg">
		<use href="${spritemapUrl.toString()}#${symbol}" />
	</svg>`;
};

export default getIcon;
