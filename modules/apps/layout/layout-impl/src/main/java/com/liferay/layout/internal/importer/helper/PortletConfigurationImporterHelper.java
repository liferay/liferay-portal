/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.importer.helper;

import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.PortletLocalService;

import java.util.Map;

/**
 * @author Jürgen Kappler
 */
public class PortletConfigurationImporterHelper {

	public PortletConfigurationImporterHelper(
		LayoutLocalService layoutLocalService,
		PortletLocalService portletLocalService,
		PortletPreferencesPortletConfigurationImporter
			portletPreferencesPortletConfigurationImporter) {

		_layoutLocalService = layoutLocalService;
		_portletLocalService = portletLocalService;
		_portletPreferencesPortletConfigurationImporter =
			portletPreferencesPortletConfigurationImporter;
	}

	public void importPortletConfiguration(
			long plid, String portletId, Map<String, Object> widgetConfig)
		throws Exception {

		if (widgetConfig == null) {
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

		_portletPreferencesPortletConfigurationImporter.
			importPortletConfiguration(
				layout.getPlid(), portletId, widgetConfig);
	}

	private final LayoutLocalService _layoutLocalService;
	private final PortletLocalService _portletLocalService;
	private final PortletPreferencesPortletConfigurationImporter
		_portletPreferencesPortletConfigurationImporter;

}