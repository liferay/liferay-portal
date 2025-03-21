/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template;

import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.PortletConstants;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portlet.PortletPreferencesImpl;
import com.liferay.portlet.PreferencesValueUtil;

import jakarta.portlet.ReadOnlyException;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author Brian Wing Shun Chan
 * @author László Csontos
 */
public class TemplatePortletPreferences {

	public String getPreferences(Map<String, Object> preferences)
		throws ReadOnlyException {

		StringBundler sb = new StringBundler();

		sb.append("<portlet-preferences>");

		for (Map.Entry<String, Object> entry : preferences.entrySet()) {
			sb.append("<preference><name>");
			sb.append(entry.getKey());
			sb.append("</name>");

			Object valueObject = entry.getValue();

			if (valueObject instanceof Collection) {
				for (Object value : (Collection)valueObject) {
					if (value instanceof String) {
						sb.append("<value>");
						sb.append(
							PreferencesValueUtil.toCompactSafe((String)value));
						sb.append("</value>");
					}
				}
			}
			else if (valueObject instanceof String) {
				sb.append("<value>");
				sb.append(
					PreferencesValueUtil.toCompactSafe((String)valueObject));
				sb.append("</value>");
			}
			else if (valueObject instanceof String[]) {
				for (String value : (String[])valueObject) {
					sb.append("<value>");
					sb.append(PreferencesValueUtil.toCompactSafe(value));
					sb.append("</value>");
				}
			}
			else {
				sb.setIndex(sb.index() - 3);

				continue;
			}

			sb.append("</preference>");
		}

		sb.append("</portlet-preferences>");

		return sb.toString();
	}

	public String getPreferences(String key, String value)
		throws ReadOnlyException {

		return getPreferences(Collections.singletonMap(key, value));
	}

	@Override
	public String toString() {
		PortletPreferencesImpl portletPreferencesImpl =
			_portletPreferencesImplThreadLocal.get();

		try {
			return PortletPreferencesFactoryUtil.toXML(portletPreferencesImpl);
		}
		catch (Exception exception) {
			_log.error(exception);

			return PortletConstants.DEFAULT_PREFERENCES;
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		TemplatePortletPreferences.class);

	private final ThreadLocal<PortletPreferencesImpl>
		_portletPreferencesImplThreadLocal = new CentralizedThreadLocal<>(
			TemplatePortletPreferences.class.getName(),
			PortletPreferencesImpl::new);

}