/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.servlet.taglib;

import com.liferay.osgi.service.tracker.collections.map.ServiceReferenceMapper;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.util.ListUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.List;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

/**
 * @author Carlos Sierra Andrés
 * @author Raymond Augé
 */
public class DynamicIncludeUtil {

	public static List<DynamicInclude> getDynamicIncludes(String key) {
		return _dynamicIncludes.getService(key);
	}

	public static boolean hasDynamicInclude(String key) {
		return ListUtil.isNotEmpty(getDynamicIncludes(key));
	}

	public static void include(
		HttpServletRequest httpServletRequest,
		HttpServletResponse httpServletResponse, String key,
		boolean ascendingPriority) {

		List<DynamicInclude> dynamicIncludes = getDynamicIncludes(key);

		if (ListUtil.isEmpty(dynamicIncludes)) {
			return;
		}

		Iterator<DynamicInclude> iterator = null;

		if (ascendingPriority) {
			iterator = dynamicIncludes.iterator();
		}
		else {
			iterator = ListUtil.reverseIterator(dynamicIncludes);
		}

		while (iterator.hasNext()) {
			DynamicInclude dynamicInclude = iterator.next();

			try {
				dynamicInclude.include(
					httpServletRequest, httpServletResponse, key);
			}
			catch (Exception exception) {
				_log.error(exception);
			}
		}
	}

	private DynamicIncludeUtil() {
	}

	private static final Log _log = LogFactoryUtil.getLog(
		DynamicIncludeUtil.class);

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();

	private static final ServiceTrackerMap<String, List<DynamicInclude>>
		_dynamicIncludes = ServiceTrackerMapFactory.openMultiValueMap(
			_bundleContext, DynamicInclude.class, null,
			new ServiceReferenceMapper<String, DynamicInclude>() {

				@Override
				public void map(
					ServiceReference<DynamicInclude> serviceReference,
					final Emitter<String> emitter) {

					DynamicInclude dynamicInclude = _bundleContext.getService(
						serviceReference);

					dynamicInclude.register(
						new DynamicInclude.DynamicIncludeRegistry() {

							@Override
							public void register(String key) {
								emitter.emit(key);
							}

						});

					_bundleContext.ungetService(serviceReference);
				}

			});

}