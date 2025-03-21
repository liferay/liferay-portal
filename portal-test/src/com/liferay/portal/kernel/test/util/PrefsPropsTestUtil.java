/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.test.util;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.service.PortalPreferencesLocalServiceUtil;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.kernel.util.PropsUtil;

import jakarta.portlet.PortletPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Stian Sigvartsen
 */
public class PrefsPropsTestUtil {

	public static SafeCloseable swapWithSafeCloseable(
			long companyId, String firstKey, Object firstValue,
			Object... keysAndValues)
		throws Exception {

		PortletPreferences portletPreferences1 =
			PortalPreferencesLocalServiceUtil.getPreferences(
				companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

		Map<String, String> oldValues = new HashMap<>();

		_setTemporaryValue(
			oldValues, portletPreferences1, firstKey,
			String.valueOf(firstValue));

		for (int i = 0; i < keysAndValues.length; i += 2) {
			String key = String.valueOf(keysAndValues[i]);
			String value = String.valueOf(keysAndValues[i + 1]);

			_setTemporaryValue(oldValues, portletPreferences1, key, value);
		}

		portletPreferences1.store();

		return () -> {
			try {
				PortletPreferences portletPreferences2 =
					PortalPreferencesLocalServiceUtil.getPreferences(
						companyId, PortletKeys.PREFS_OWNER_TYPE_COMPANY);

				for (Map.Entry<String, String> entry : oldValues.entrySet()) {
					portletPreferences2.setValue(
						entry.getKey(), entry.getValue());
				}

				portletPreferences2.store();
			}
			catch (Exception exception) {
				throw new RuntimeException(exception);
			}
		};
	}

	private static void _setTemporaryValue(
			Map<String, String> oldValues,
			PortletPreferences portletPreferences, String key, String value)
		throws Exception {

		oldValues.put(
			key, portletPreferences.getValue(key, PropsUtil.get(key)));

		portletPreferences.setValue(key, value);
	}

}