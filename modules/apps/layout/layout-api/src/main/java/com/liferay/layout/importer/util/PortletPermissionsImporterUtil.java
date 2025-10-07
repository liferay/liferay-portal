/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.importer.util;

import com.liferay.layout.importer.PortletPermissionsImporter;
import com.liferay.portal.kernel.module.service.Snapshot;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Lourdes Fernández Besada
 */
public class PortletPermissionsImporterUtil {

	public static void importPortletPermissions(
			long plid, String portletId, Set<String> warningMessages,
			List<Map<String, Object>> widgetPermissionsMaps)
		throws Exception {

		PortletPermissionsImporter portletPermissionsImporter =
			_portletPermissionsImporterSnapshot.get();

		portletPermissionsImporter.importPortletPermissions(
			plid, portletId, warningMessages, widgetPermissionsMaps);
	}

	private static final Snapshot<PortletPermissionsImporter>
		_portletPermissionsImporterSnapshot = new Snapshot<>(
			PortletPermissionsImporterUtil.class,
			PortletPermissionsImporter.class);

}