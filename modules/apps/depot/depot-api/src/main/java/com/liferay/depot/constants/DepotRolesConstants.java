/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.constants;

/**
 * @author Cristina González
 */
public class DepotRolesConstants {

	public static final String ASSET_LIBRARY_ADMINISTRATOR =
		"Asset Library Administrator";

	public static final String ASSET_LIBRARY_CONNECTED_SITE_MEMBER =
		"Asset Library Connected Site Member";

	public static final String ASSET_LIBRARY_CONTENT_REVIEWER =
		"Asset Library Content Reviewer";

	public static final String ASSET_LIBRARY_MEMBER = "Asset Library Member";

	public static final String ASSET_LIBRARY_OWNER = "Asset Library Owner";

	public static final String[] DEPOT_ROLE_NAMES = {
		ASSET_LIBRARY_ADMINISTRATOR, ASSET_LIBRARY_CONNECTED_SITE_MEMBER,
		ASSET_LIBRARY_CONTENT_REVIEWER, ASSET_LIBRARY_MEMBER,
		ASSET_LIBRARY_OWNER
	};

	public static final String SUBTYPE_CATALOG = "catalog";

	public static final String SUBTYPE_PROJECT = "project";

	public static final String SUBTYPE_SPACE = "space";

	public static String getSubtype(int depotType) {
		if (depotType == DepotConstants.TYPE_CATALOG) {
			return SUBTYPE_CATALOG;
		}

		if (depotType == DepotConstants.TYPE_PROJECT) {
			return SUBTYPE_PROJECT;
		}

		if (depotType == DepotConstants.TYPE_SPACE) {
			return SUBTYPE_SPACE;
		}

		return null;
	}

}