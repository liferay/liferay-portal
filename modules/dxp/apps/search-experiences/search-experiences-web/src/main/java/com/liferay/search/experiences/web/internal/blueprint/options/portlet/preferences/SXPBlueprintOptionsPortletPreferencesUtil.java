/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.options.portlet.preferences;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.portlet.PortletPreferences;

/**
 * @author Kevin Tan
 */
public class SXPBlueprintOptionsPortletPreferencesUtil {

	public static String getValue(
		PortletPreferences portletPreferences, String key) {

		if (portletPreferences == null) {
			return StringPool.BLANK;
		}

		String value = portletPreferences.getValue(key, StringPool.BLANK);

		value = StringUtil.trim(value);

		if (Validator.isBlank(value)) {
			return StringPool.BLANK;
		}

		return value;
	}

}