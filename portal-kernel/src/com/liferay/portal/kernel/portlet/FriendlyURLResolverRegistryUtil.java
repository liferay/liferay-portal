/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.portlet;

import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerList;
import com.liferay.osgi.service.tracker.collections.list.ServiceTrackerListFactory;
import com.liferay.petra.lang.CentralizedThreadLocal;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.osgi.framework.BundleContext;

/**
 * @author Eduardo García
 * @author Raymond Augé
 */
public class FriendlyURLResolverRegistryUtil {

	public static FriendlyURLResolver getFriendlyURLResolver(
		String urlSeparator) {

		for (FriendlyURLResolver friendlyURLResolver : _serviceTrackerList) {
			if (Objects.equals(
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
		getFriendlyURLResolversAsCollection() {

		return _serviceTrackerList.toList();
	}

	public static String[] getURLSeparators() {
		String[] urlSeparators = _urlSeparators.get();

		if (urlSeparators != null) {
			return urlSeparators;
		}

		List<String> urlSeparatorsList = new ArrayList<>();

		for (FriendlyURLResolver friendlyURLResolver : _serviceTrackerList) {
			if (friendlyURLResolver != null) {
				urlSeparatorsList.add(friendlyURLResolver.getURLSeparator());
			}
		}

		urlSeparators = urlSeparatorsList.toArray(new String[0]);

		_urlSeparators.set(urlSeparators);

		return urlSeparators;
	}

	private static final BundleContext _bundleContext =
		SystemBundleUtil.getBundleContext();
	private static final ServiceTrackerList<FriendlyURLResolver>
		_serviceTrackerList = ServiceTrackerListFactory.open(
			_bundleContext, FriendlyURLResolver.class);
	private static final ThreadLocal<String[]> _urlSeparators =
		new CentralizedThreadLocal<>(
			FriendlyURLResolverRegistryUtil.class.getName() +
				"._urlSeparators");

}