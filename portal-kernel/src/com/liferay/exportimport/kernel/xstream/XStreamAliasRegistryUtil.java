/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.exportimport.kernel.xstream;

import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * @author Máté Thurzó
 */
public class XStreamAliasRegistryUtil {

	public static Set<Class<?>> getAliases() {
		return new HashSet<>(_xstreamAliases.keySet());
	}

	private XStreamAliasRegistryUtil() {
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTracker<XStreamAlias, XStreamAlias>
		_serviceTracker;
	private static final Map<Class<?>, String> _xstreamAliases =
		new ConcurrentHashMap<>();

	private static class XStreamAliasServiceTrackerCustomizer
		implements ServiceTrackerCustomizer<XStreamAlias, XStreamAlias> {

		@Override
		public XStreamAlias addingService(
			ServiceReference<XStreamAlias> serviceReference) {

			XStreamAlias xStreamAlias = _bundleContext.getService(
				serviceReference);

			_xstreamAliases.put(
				xStreamAlias.getClazz(), xStreamAlias.getName());

			return xStreamAlias;
		}

		@Override
		public void modifiedService(
			ServiceReference<XStreamAlias> serviceReference,
			XStreamAlias xStreamAlias) {
		}

		@Override
		public void removedService(
			ServiceReference<XStreamAlias> serviceReference,
			XStreamAlias xStreamAlias) {

			_bundleContext.ungetService(serviceReference);

			_xstreamAliases.remove(xStreamAlias.getClazz());
		}

	}

	static {
		_serviceTracker = new ServiceTracker<>(
			_bundleContext, XStreamAlias.class,
			new XStreamAliasServiceTrackerCustomizer());

		_serviceTracker.open();
	}

}