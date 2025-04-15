/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.util;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import java.util.Set;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Eduardo García
 */
public class DDMDisplayRegistryUtil {

	public static DDMDisplay getDDMDisplay(String portletId) {
		return _serviceTrackerMap.getService(portletId);
	}

	public static String[] getPortletIds() {
		Set<String> portletIds = _serviceTrackerMap.keySet();

		return portletIds.toArray(new String[0]);
	}

	private static final ServiceTrackerMap<String, DDMDisplay>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(DDMDisplayRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, DDMDisplay.class, "jakarta.portlet.name");
	}

}