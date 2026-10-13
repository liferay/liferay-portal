/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.osgi.framework.BundleContext;

/**
 * @author Eduardo García
 * @author Raymond Augé
 */
public class FriendlyURLResolverRegistryUtil {

	public static FriendlyURLResolver getFriendlyURLResolver(
		long companyId, String urlSeparator) {

		for (FriendlyURLResolver friendlyURLResolver : _serviceTrackerList) {
			if (((friendlyURLResolver.getCompanyId() == 0) ||
				 (friendlyURLResolver.getCompanyId() == companyId)) &&
				Objects.equals(
					friendlyURLResolver.getURLSeparator(), urlSeparator)) {

				return friendlyURLResolver;
			}
		}

		return null;
	}

	public static FriendlyURLResolver
		getFriendlyURLResolverByDefaultURLSeparator(
			String defaultURLSeparator) {

		for (FriendlyURLResolver friendlyURLResolver : _serviceTrackerList) {
			if (Objects.equals(
					friendlyURLResolver.getDefaultURLSeparator(),
					defaultURLSeparator)) {

				return friendlyURLResolver;
			}
		}

		return null;
	}

	public static Collection<FriendlyURLResolver>
		getFriendlyURLResolversAsCollection(long companyId) {

		Collection<FriendlyURLResolver> friendlyURLResolvers =
			new ArrayList<>();

		for (FriendlyURLResolver friendlyURLResolver : _serviceTrackerList) {
			if ((friendlyURLResolver.getCompanyId() != 0) &&
				(friendlyURLResolver.getCompanyId() != companyId)) {

				continue;
			}

			friendlyURLResolvers.add(friendlyURLResolver);
		}

		return friendlyURLResolvers;
	}

	public static String[] getURLSeparators(long companyId) {
		Map<Long, String[]> urlSeparatorsMap = _urlSeparatorsMap.get();

		String[] urlSeparators = urlSeparatorsMap.get(companyId);

		if (urlSeparators != null) {
			return urlSeparators;
		}

		urlSeparators = TransformUtil.transformToArray(
			getFriendlyURLResolversAsCollection(companyId),
			FriendlyURLResolver::getURLSeparator, String.class);

		urlSeparatorsMap.put(companyId, urlSeparators);

		return urlSeparators;
	}

	public static void removeURLSeparators() {
		_urlSeparatorsMap.remove();
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTrackerList<FriendlyURLResolver>
		_serviceTrackerList = ServiceTrackerListFactory.open(
			_bundleContext, FriendlyURLResolver.class);
	private static final ThreadLocal<Map<Long, String[]>> _urlSeparatorsMap =
		new CentralizedThreadLocal<>(
			FriendlyURLResolverRegistryUtil.class.getName() +
				"._urlSeparatorsMap",
			HashMap::new);

}