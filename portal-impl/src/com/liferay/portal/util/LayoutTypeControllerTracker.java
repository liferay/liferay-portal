/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutTypeController;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.model.impl.LayoutTypeControllerImpl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Raymond Augé
 */
public class LayoutTypeControllerTracker {

	public static LayoutTypeController getLayoutTypeController(Layout layout) {
		return getLayoutTypeController(layout.getType());
	}

	public static LayoutTypeController getLayoutTypeController(String type) {
		LayoutTypeController layoutTypeController =
			_serviceTrackerMap.getService(type);

		if (layoutTypeController != null) {
			return layoutTypeController;
		}

		layoutTypeController = _serviceTrackerMap.getService(
			LayoutConstants.TYPE_PORTLET);

		if (layoutTypeController != null) {
			return layoutTypeController;
		}

		return _defaultLayoutTypeController;
	}

	public static Map<String, LayoutTypeController> getLayoutTypeControllers() {
		HashMap<String, LayoutTypeController> layoutTypeControllers =
			new HashMap<>();

		for (String type : _serviceTrackerMap.keySet()) {
			layoutTypeControllers.put(
				type, _serviceTrackerMap.getService(type));
		}

		return layoutTypeControllers;
	}

	public static String[] getTypes() {
		Set<String> types = _serviceTrackerMap.keySet();

		return types.toArray(new String[0]);
	}

	private static final LayoutTypeController _defaultLayoutTypeController =
		new LayoutTypeControllerImpl(LayoutConstants.TYPE_PORTLET);
	private static final ServiceTrackerMap<String, LayoutTypeController>
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			SystemBundleUtil.getBundleContext(), LayoutTypeController.class,
			"layout.type");

}