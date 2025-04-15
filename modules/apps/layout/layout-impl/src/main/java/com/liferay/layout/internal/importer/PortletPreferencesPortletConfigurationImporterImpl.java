/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer;

import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.portlet.PortletPreferencesFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PortletKeys;

import jakarta.portlet.PortletPreferences;

import java.util.Collection;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jürgen Kappler
 */
@Component(service = PortletPreferencesPortletConfigurationImporter.class)
public class PortletPreferencesPortletConfigurationImporterImpl
	implements PortletPreferencesPortletConfigurationImporter {

	@Override
	public void importPortletConfiguration(
			long plid, String portletId,
			Map<String, Object> portletConfiguration)
		throws Exception {

		if (portletConfiguration == null) {
			return;
		}

		Layout layout = _layoutLocalService.fetchLayout(plid);

		if (layout == null) {
			return;
		}

		String portletName = PortletIdCodec.decodePortletName(portletId);

		Portlet portlet = _portletLocalService.getPortletById(portletName);

		if (portlet == null) {
			return;
		}

		PortletPreferences portletPreferences =
			PortletPreferencesFactoryUtil.fromXML(
				layout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId, portlet.getDefaultPreferences());

		for (Map.Entry<String, Object> entrySet :
				portletConfiguration.entrySet()) {

			Object value = entrySet.getValue();

			if (value instanceof Collection) {
				portletPreferences.setValues(
					entrySet.getKey(),
					ArrayUtil.toStringArray((Collection<String>)value));
			}
			else {
				portletPreferences.setValue(entrySet.getKey(), (String)value);
			}
		}

		String portletPreferencesXML = PortletPreferencesFactoryUtil.toXML(
			portletPreferences);

		com.liferay.portal.kernel.model.PortletPreferences
			persistedPortletPreferences =
				_portletPreferencesLocalService.fetchPortletPreferences(
					PortletKeys.PREFS_OWNER_ID_DEFAULT,
					PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
					portletId);

		if (persistedPortletPreferences == null) {
			_portletPreferencesLocalService.addPortletPreferences(
				layout.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId, null, portletPreferencesXML);
		}
		else {
			_portletPreferencesLocalService.updatePreferences(
				PortletKeys.PREFS_OWNER_ID_DEFAULT,
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				portletId, portletPreferencesXML);
		}
	}

	@Reference
	private LayoutLocalService _layoutLocalService;

	@Reference
	private PortletLocalService _portletLocalService;

	@Reference
	private PortletPreferencesLocalService _portletPreferencesLocalService;

}