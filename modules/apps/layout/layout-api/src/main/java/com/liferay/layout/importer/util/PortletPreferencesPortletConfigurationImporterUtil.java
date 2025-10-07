/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.importer.util;

import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.Map;

/**
 * @author Lourdes Fernández Besada
 */
public class PortletPreferencesPortletConfigurationImporterUtil {

	public static void importPortletConfiguration(
			long plid, String portletId,
			Map<String, Object> portletConfiguration)
		throws Exception {

		PortletPreferencesPortletConfigurationImporter
			portletPreferencesPortletConfigurationImporter =
				_portletPreferencesPortletConfigurationImporterSnapshot.get();

		portletPreferencesPortletConfigurationImporter.
			importPortletConfiguration(plid, portletId, portletConfiguration);
	}

	private static final Snapshot
		<PortletPreferencesPortletConfigurationImporter>
			_portletPreferencesPortletConfigurationImporterSnapshot =
				new Snapshot<>(
					PortletPreferencesPortletConfigurationImporterUtil.class,
					PortletPreferencesPortletConfigurationImporter.class);

}