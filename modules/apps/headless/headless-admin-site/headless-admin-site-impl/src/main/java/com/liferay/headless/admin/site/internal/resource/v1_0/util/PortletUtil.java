/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.resource.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.WidgetPermission;
import com.liferay.headless.admin.site.internal.resource.v1_0.layout.structure.item.importer.WidgetInstanceLayoutStructureItemImporter;
import com.liferay.layout.importer.PortletPermissionsImporter;
import com.liferay.layout.importer.PortletPreferencesPortletConfigurationImporter;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.ResourceConstants;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.permission.PortletPermissionUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Mikel Lorza
 */
public class PortletUtil {

	public static void importPortletPermissions(
			Layout layout, String portletId, String portletName,
			WidgetPermission[] widgetPermissions)
		throws Exception {

		PortletPermissionsImporter portletPermissionsImporter =
			_portletPermissionsImporterServiceTracker.getService();

		if (portletPermissionsImporter == null) {
			return;
		}

		if ((widgetPermissions != null) && (widgetPermissions.length == 0)) {
			ResourcePermissionLocalServiceUtil.deleteResourcePermissions(
				layout.getCompanyId(), portletName,
				ResourceConstants.SCOPE_INDIVIDUAL,
				PortletPermissionUtil.getPrimaryKey(
					layout.getPlid(), portletId));

			return;
		}

		portletPermissionsImporter.importPortletPermissions(
			layout.getPlid(), portletId, new HashSet<>(),
			_getWidgetPermissionsMaps(widgetPermissions));
	}

	public static void importPortletPreferences(
			Layout layout, String portletId, Map<String, Object> widgetConfig)
		throws Exception {

		PortletPreferencesPortletConfigurationImporter
			portletPreferencesPortletConfigurationImporter =
				_portletPreferencesPortletConfigurationImporterServiceTracker.
					getService();

		if (portletPreferencesPortletConfigurationImporter == null) {
			return;
		}

		portletPreferencesPortletConfigurationImporter.
			importPortletConfiguration(
				layout.getPlid(), portletId, widgetConfig);
	}

	private static List<Map<String, Object>> _getWidgetPermissionsMaps(
		WidgetPermission[] widgetPermissions) {

		if (ArrayUtil.isEmpty(widgetPermissions)) {
			return new ArrayList<>();
		}

		List<Map<String, Object>> widgetPermissionsMaps = new ArrayList<>();

		for (WidgetPermission widgetPermission : widgetPermissions) {
			widgetPermissionsMaps.add(
				HashMapBuilder.<String, Object>put(
					"actionKeys", Arrays.asList(widgetPermission.getActionIds())
				).put(
					"roleKey", widgetPermission.getRoleName()
				).build());
		}

		return widgetPermissionsMaps;
	}

	private static final ServiceTracker
		<PortletPermissionsImporter, PortletPermissionsImporter>
			_portletPermissionsImporterServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(
						WidgetInstanceLayoutStructureItemImporter.class),
					PortletPermissionsImporter.class);
	private static final ServiceTracker
		<PortletPreferencesPortletConfigurationImporter,
		 PortletPreferencesPortletConfigurationImporter>
			_portletPreferencesPortletConfigurationImporterServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(
						WidgetInstanceLayoutStructureItemImporter.class),
					PortletPreferencesPortletConfigurationImporter.class);

}