/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

const getIcon = ({symbol}: {symbol: string}): string => {
	return `<svg xmlns="http://www.w3.org/2000/svg">
		<use href="${Liferay.Icons.spritemap}#${symbol}" />
	</svg>`;
};

export default getIcon;
