/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.portal.kernel.cache.PortalCache;
import com.liferay.portal.kernel.cache.PortalCacheHelperUtil;
import com.liferay.portal.kernel.cache.PortalCacheManagerNames;
import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eduardo Lundgren
 */
public class JavaScriptBundleUtil {

	public static void clearCache() {
		_portalCache.removeAll();
	}

	public static String[] getFileNames(String bundleId) {
		String[] fileNames = _portalCache.get(bundleId);

		if (fileNames == null) {
			List<String> fileNamesList = new ArrayList<>();

			Set<String> dependencies = _getDependencies(
				bundleId, new LinkedHashSet<String>());

			for (String dependency : dependencies) {
				String[] dependencyFileNames = PropsUtil.getArray(dependency);

				for (String dependencyFileName : dependencyFileNames) {
					fileNamesList.add(dependencyFileName);
				}
			}

			fileNames = fileNamesList.toArray(new String[0]);

			_portalCache.put(bundleId, fileNames);
		}

		return fileNames;
	}

	private static Set<String> _getDependencies(
		String bundleId, Set<String> dependencies) {

		if (!ArrayUtil.contains(PropsValues.JAVASCRIPT_BUNDLE_IDS, bundleId)) {
			return dependencies;
		}

		String[] bundleDependencies = PropsUtil.getArray(
			PropsKeys.JAVASCRIPT_BUNDLE_DEPENDENCIES, new Filter(bundleId));

		for (String bundleDependency : bundleDependencies) {
			String[] bundleDependencyDependencies = PropsUtil.getArray(
				PropsKeys.JAVASCRIPT_BUNDLE_DEPENDENCIES,
				new Filter(bundleDependency));

			if (!ArrayUtil.contains(bundleDependencyDependencies, bundleId)) {
				_getDependencies(bundleDependency, dependencies);
			}

			dependencies.add(bundleDependency);
		}

		dependencies.add(bundleId);

		return dependencies;
	}

	private static final String _CACHE_NAME =
		JavaScriptBundleUtil.class.getName();

	private static final PortalCache<String, String[]> _portalCache =
		PortalCacheHelperUtil.getPortalCache(
			PortalCacheManagerNames.SINGLE_VM, _CACHE_NAME);

}