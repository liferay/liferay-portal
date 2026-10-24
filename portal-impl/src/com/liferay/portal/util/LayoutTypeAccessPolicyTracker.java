/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutTypeAccessPolicy;
import com.liferay.portal.kernel.model.impl.DefaultLayoutTypeAccessPolicyImpl;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

/**
 * @author Adolfo Pérez
 */
public class LayoutTypeAccessPolicyTracker {

	public static LayoutTypeAccessPolicy getLayoutTypeAccessPolicy(
		Layout layout) {

		return getLayoutTypeAccessPolicy(layout.getType());
	}

	public static LayoutTypeAccessPolicy getLayoutTypeAccessPolicy(
		String type) {

		LayoutTypeAccessPolicy layoutTypeAccessPolicy =
			_serviceTrackerMap.getService(type);

		if (layoutTypeAccessPolicy == null) {
			return DefaultLayoutTypeAccessPolicyImpl.create();
		}

		return layoutTypeAccessPolicy;
	}

	private static final ServiceTrackerMap<String, LayoutTypeAccessPolicy>
		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			SystemBundleUtil.getBundleContext(), LayoutTypeAccessPolicy.class,
			"layout.type");

}