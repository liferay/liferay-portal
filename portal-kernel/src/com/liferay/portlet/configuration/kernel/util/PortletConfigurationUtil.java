/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.configuration.kernel.util;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.portlet.PortletSetupUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Brian Wing Shun Chan
 */
public class PortletConfigurationUtil {

	public static String getPortletCustomCSSClassName(
			PortletPreferences portletPreferences)
		throws Exception {

		String customCSSClassName = StringPool.BLANK;

		String css = portletPreferences.getValue(
			"portletSetupCss", StringPool.BLANK);

		if (Validator.isNotNull(css)) {
			JSONObject cssJSONObject = PortletSetupUtil.cssToJSONObject(
				portletPreferences, css);

			JSONObject advancedDataJSONObject = cssJSONObject.getJSONObject(
				"advancedData");

			if (advancedDataJSONObject != null) {
				customCSSClassName = advancedDataJSONObject.getString(
					"customCSSClassName");
			}
		}

		return customCSSClassName;
	}

	public static String getPortletTitle(
		String portletId, PortletPreferences portletPreferences,
		String languageId) {

		if (!isUseCustomTitle(portletPreferences)) {
			return null;
		}

		return portletPreferences.getValue(
			"portletSetupTitle_" + languageId,
			PortalUtil.getPortletTitle(portletId, languageId));
	}

	public static boolean isUseCustomTitle(
		PortletPreferences portletPreferences) {

		return GetterUtil.getBoolean(
			portletPreferences.getValue("portletSetupUseCustomTitle", null));
	}

}