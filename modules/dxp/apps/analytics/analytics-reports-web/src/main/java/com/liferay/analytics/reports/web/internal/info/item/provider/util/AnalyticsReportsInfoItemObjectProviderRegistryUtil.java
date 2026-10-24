/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.reports.web.internal.info.item.provider.util;

import com.liferay.analytics.reports.info.item.provider.AnalyticsReportsInfoItemObjectProvider;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Cristina González
 */
public class AnalyticsReportsInfoItemObjectProviderRegistryUtil {

	public static AnalyticsReportsInfoItemObjectProvider<?>
		getAnalyticsReportsInfoItemObjectProvider(String className) {

		return _analyticsReportsInfoItemObjectProviderServiceTrackerMap.
			getService(className);
	}

	private static final ServiceTrackerMap
		<String, AnalyticsReportsInfoItemObjectProvider<?>>
			_analyticsReportsInfoItemObjectProviderServiceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(
			AnalyticsReportsInfoItemObjectProviderRegistryUtil.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_analyticsReportsInfoItemObjectProviderServiceTrackerMap =
			ServiceTrackerMapFactory.openSingleValueMap(
				bundleContext,
				(Class<AnalyticsReportsInfoItemObjectProvider<?>>)
					(Class<?>)AnalyticsReportsInfoItemObjectProvider.class,
				null,
				(serviceReference, emitter) -> {
					AnalyticsReportsInfoItemObjectProvider<?>
						analyticsReportsInfoItemObjectProvider =
							bundleContext.getService(serviceReference);

					try {
						emitter.emit(
							analyticsReportsInfoItemObjectProvider.
								getClassName());
					}
					finally {
						bundleContext.ungetService(serviceReference);
					}
				});
	}

}