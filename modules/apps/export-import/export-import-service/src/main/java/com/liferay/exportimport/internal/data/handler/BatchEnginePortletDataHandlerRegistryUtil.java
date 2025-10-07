/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.internal.data.handler;

import com.liferay.exportimport.kernel.lar.PortletDataHandler;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Vendel Toreki
 */
public class BatchEnginePortletDataHandlerRegistryUtil {

	public static BatchEnginePortletDataHandler
		getBatchEnginePortletDataHandler(String className) {

		PortletDataHandler portletDataHandler = _serviceTrackerMap.getService(
			className);

		if (portletDataHandler instanceof
				BatchEnginePortletDataHandler batchEnginePortletDataHandler) {

			return batchEnginePortletDataHandler;
		}

		return null;
	}

	public static boolean hasBatchEnginePortletDataHandler(String className) {
		return _serviceTrackerMap.containsKey(className);
	}

	private static final ServiceTrackerMap<String, PortletDataHandler>
		_serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			BatchEnginePortletDataHandlerRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceTrackerMap = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, PortletDataHandler.class,
			"batch.engine.task.item.delegate.item.class.name");
	}

}