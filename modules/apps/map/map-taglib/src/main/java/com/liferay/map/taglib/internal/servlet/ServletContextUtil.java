/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.map.taglib.internal.servlet;

import com.liferay.map.MapProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapperFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.module.service.Snapshot;
import com.liferay.portal.kernel.service.GroupLocalService;

import jakarta.servlet.ServletContext;

import java.util.Collection;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Jürgen Kappler
 */
public class ServletContextUtil {

	public static GroupLocalService getGroupLocalService() {
		return _groupLocalServiceSnapshot.get();
	}

	public static MapProvider getMapProvider(String mapProviderKey) {
		return _mapProviders.getService(mapProviderKey);
	}

	public static Collection<MapProvider> getMapProviders() {
		return _mapProviders.values();
	}

	public static ServletContext getServletContext() {
		return _servletContextSnapshot.get();
	}

	private static final Snapshot<GroupLocalService>
		_groupLocalServiceSnapshot = new Snapshot<>(
			ServletContextUtil.class, GroupLocalService.class);
	private static final ServiceTrackerMap<String, MapProvider> _mapProviders;
	private static final Snapshot<ServletContext> _servletContextSnapshot =
		new Snapshot<>(
			ServletContextUtil.class, ServletContext.class,
			"(osgi.web.symbolicname=com.liferay.map.taglib)");

	static {
		Bundle bundle = FrameworkUtil.getBundle(ServletContextUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_mapProviders = ServiceTrackerMapFactory.openSingleValueMap(
			bundleContext, MapProvider.class, null,
			ServiceReferenceMapperFactory.createFromFunction(
				bundleContext, MapProvider::getKey));
	}

}