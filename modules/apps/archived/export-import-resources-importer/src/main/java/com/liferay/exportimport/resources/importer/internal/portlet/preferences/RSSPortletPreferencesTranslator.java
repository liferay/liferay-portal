/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.portlet.preferences;

import com.liferay.exportimport.resources.importer.portlet.preferences.PortletPreferencesTranslator;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.service.component.annotations.Component;

/**
 * @author Michael C. Han
 */
@Component(
	property = "portlet.preferences.translator.portlet.id=com_liferay_rss_web_portlet_RSSPortlet",
	service = PortletPreferencesTranslator.class
)
public class RSSPortletPreferencesTranslator
	implements PortletPreferencesTranslator {

	@Override
	public void translate(
			JSONObject portletPreferencesJSONObject, String key,
			PortletPreferences portletPreferences)
		throws PortletException {

		if (!key.equals("titles") && !key.equals("urls")) {
			String value = portletPreferencesJSONObject.getString(key);

			portletPreferences.setValue(key, value);

			return;
		}

		List<String> values = new ArrayList<>();

		JSONObject jsonObject = portletPreferencesJSONObject.getJSONObject(key);

		Iterator<String> iterator = jsonObject.keys();

		while (iterator.hasNext()) {
			String jsonObjectKey = iterator.next();

			values.add(jsonObject.getString(jsonObjectKey));
		}

		portletPreferences.setValues(key, values.toArray(new String[0]));
	}

}