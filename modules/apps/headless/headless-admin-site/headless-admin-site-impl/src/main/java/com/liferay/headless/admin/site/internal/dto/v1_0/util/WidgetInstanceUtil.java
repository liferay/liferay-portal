/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.site.internal.dto.v1_0.util;

import com.liferay.headless.admin.site.dto.v1_0.WidgetInstance;
import com.liferay.headless.admin.site.dto.v1_0.WidgetPermission;
import com.liferay.layout.exporter.PortletPermissionsExporter;
import com.liferay.layout.exporter.PortletPreferencesPortletConfigurationExporter;
import com.liferay.osgi.util.ServiceTrackerFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.portlet.PortletIdCodec;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Map;

import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Mikel Lorza
 */
public class WidgetInstanceUtil {

	public static WidgetInstance getWidgetInstance(
		String instanceId, long plid, String portletId) {

		return new WidgetInstance() {
			{
				setWidgetConfig(() -> _getWidgetConfig(plid, portletId));
				setWidgetInstanceId(() -> instanceId);
				setWidgetName(
					() -> PortletIdCodec.decodePortletName(portletId));
				setWidgetPermissions(
					() -> _getWidgetPermissions(plid, portletId));
			}
		};
	}

	private static Map<String, Object> _getWidgetConfig(
		long plid, String portletId) {

		PortletPreferencesPortletConfigurationExporter
			portletPreferencesPortletConfigurationExporter =
				_portletPreferencesPortletConfigurationExporterServiceTracker.
					getService();

		if (portletPreferencesPortletConfigurationExporter == null) {
			return null;
		}

		return portletPreferencesPortletConfigurationExporter.
			getPortletConfiguration(plid, portletId);
	}

	private static WidgetPermission[] _getWidgetPermissions(
		long plid, String portletId) {

		PortletPermissionsExporter portletPermissionsExporter =
			_portletPermissionsExporterServiceTracker.getService();

		if (portletPermissionsExporter == null) {
			return null;
		}

		Map<String, String[]> portletPermissions =
			portletPermissionsExporter.getPortletPermissions(plid, portletId);

		if (MapUtil.isEmpty(portletPermissions)) {
			return new WidgetPermission[0];
		}

		return TransformUtil.transformToArray(
			portletPermissions.entrySet(),
			entry -> {
				if (ArrayUtil.isEmpty(entry.getValue())) {
					return null;
				}

				return new WidgetPermission() {
					{
						setActionIds(entry::getValue);
						setRoleName(entry::getKey);
					}
				};
			},
			WidgetPermission.class);
	}

	private static final ServiceTracker
		<PortletPermissionsExporter, PortletPermissionsExporter>
			_portletPermissionsExporterServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(WidgetInstanceUtil.class),
					PortletPermissionsExporter.class);
	private static final ServiceTracker
		<PortletPreferencesPortletConfigurationExporter,
		 PortletPreferencesPortletConfigurationExporter>
			_portletPreferencesPortletConfigurationExporterServiceTracker =
				ServiceTrackerFactory.open(
					FrameworkUtil.getBundle(WidgetInstanceUtil.class),
					PortletPreferencesPortletConfigurationExporter.class);

}