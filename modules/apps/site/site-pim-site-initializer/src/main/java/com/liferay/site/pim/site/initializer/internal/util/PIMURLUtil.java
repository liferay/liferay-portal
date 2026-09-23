/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.pim.site.initializer.internal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Andrea Sbarra
 * @author Stefano Motta
 */
public class PIMURLUtil {

	public static String getConnectorsURL(ThemeDisplay themeDisplay) {
		return _getSiteURL("/connectors", themeDisplay);
	}

	public static String getEditConnectorURL(
		String objectEntryId, ThemeDisplay themeDisplay) {

		return _getURL("/edit-connector", objectEntryId, themeDisplay);
	}

	public static String getEditFieldMappingURL(
		String objectEntryId, ThemeDisplay themeDisplay) {

		return StringBundler.concat(
			_getSiteURL("/edit-field-mapping", themeDisplay), "?objectEntryId=",
			objectEntryId);
	}

	public static String getExportToLiferayCommerceURL() {
		return "/o/pim/export-to-liferay-commerce";
	}

	public static String getFieldMappingsURL(
		String objectEntryId, ThemeDisplay themeDisplay) {

		return _getURL("/field-mappings", objectEntryId, themeDisplay);
	}

	private static String _getSiteURL(
		String friendlyURL, ThemeDisplay themeDisplay) {

		Group group = themeDisplay.getScopeGroup();

		return StringBundler.concat(
			themeDisplay.getPathFriendlyURLPublic(), group.getFriendlyURL(),
			friendlyURL);
	}

	private static String _getURL(
		String friendlyURL, String objectEntryId, ThemeDisplay themeDisplay) {

		String url = StringBundler.concat(
			_getSiteURL(friendlyURL, themeDisplay), "?backURL=",
			URLCodec.encodeURL(themeDisplay.getURLCurrent()));

		if (Validator.isNull(objectEntryId)) {
			return url;
		}

		return StringBundler.concat(url, "&objectEntryId=", objectEntryId);
	}

}