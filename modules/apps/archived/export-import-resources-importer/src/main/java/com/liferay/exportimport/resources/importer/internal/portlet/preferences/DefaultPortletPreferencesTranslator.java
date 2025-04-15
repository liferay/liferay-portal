/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.resources.importer.internal.portlet.preferences;

import com.liferay.exportimport.resources.importer.internal.constants.ResourcesImporterConstants;
import com.liferay.exportimport.resources.importer.portlet.preferences.PortletPreferencesTranslator;
import com.liferay.portal.kernel.json.JSONObject;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletPreferences;

import org.osgi.service.component.annotations.Component;

/**
 * @author Brian Greenwald
 */
@Component(
	property = "portlet.preferences.translator.portlet.id=" + ResourcesImporterConstants.PORTLET_ID_DEFAULT,
	service = PortletPreferencesTranslator.class
)
public class DefaultPortletPreferencesTranslator
	implements PortletPreferencesTranslator {

	@Override
	public void translate(
			JSONObject portletPreferencesJSONObject, String key,
			PortletPreferences portletPreferences)
		throws PortletException {

		String value = portletPreferencesJSONObject.getString(key);

		portletPreferences.setValue(key, value);
	}

}