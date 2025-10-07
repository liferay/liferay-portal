/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

export default function itemSelectorValueToSiteNavigationMenuItem(
	siteNavigationMenuItem
) {
	const {
		contextualMenu,
		parentSiteNavigationMenuItemId,
		privateLayout,
		returnType,
		siteNavigationMenuExternalReferenceCode,
		siteNavigationMenuId,
		title,
	} = siteNavigationMenuItem;

	return {
		contextualMenu,
		parentSiteNavigationMenuItemId,
		privateLayout,
		siteNavigationMenuExternalReferenceCode,
		siteNavigationMenuId,
		title,
		type: returnType,
	};
}
