/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.auto.tagger.internal.util;

import com.liferay.asset.auto.tagger.AssetAutoTagProvider;
import com.liferay.asset.auto.tagger.configuration.AssetAutoTaggerConfiguration;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMapFactory;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.Validator;

import java.util.ArrayList;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;

/**
 * @author Alejandro Tardín
 */
public class AssetAutoTaggerUtil {

	public static List<AssetAutoTagProvider<?>> getAssetAutoTagProviders(
		String className) {

		List<AssetAutoTagProvider<?>> assetAutoTagProviders = new ArrayList<>();

		List<AssetAutoTagProvider<?>> generalAssetAutoTagProviders =
			_serviceTrackerMap.getService("*");

		if (ListUtil.isNotEmpty(generalAssetAutoTagProviders)) {
			assetAutoTagProviders.addAll(generalAssetAutoTagProviders);
		}

		if (Validator.isNotNull(className)) {
			List<AssetAutoTagProvider<?>> classNameAssetAutoTagProviders =
				_serviceTrackerMap.getService(className);

			if (ListUtil.isNotEmpty(classNameAssetAutoTagProviders)) {
				assetAutoTagProviders.addAll(classNameAssetAutoTagProviders);
			}
		}

		return assetAutoTagProviders;
	}

	public static List<AssetAutoTagProvider<?>>
		getAssetEntryAssetAutoTagProviders() {

		return _serviceTrackerMap.getService(AssetEntry.class.getName());
	}

	public static boolean isAutoTaggable(
		AssetAutoTaggerConfiguration assetAutoTaggerConfiguration,
		AssetEntry assetEntry) {

		if (assetAutoTaggerConfiguration.isEnabled() &&
			assetEntry.isVisible() &&
			(ListUtil.isNotEmpty(
				getAssetAutoTagProviders(assetEntry.getClassName())) ||
			 ListUtil.isNotEmpty(getAssetEntryAssetAutoTagProviders()))) {

			return true;
		}

		return false;
	}

	private static final ServiceTrackerMap
		<String, List<AssetAutoTagProvider<?>>> _serviceTrackerMap;

	static {
		Bundle bundle = FrameworkUtil.getBundle(AssetAutoTaggerUtil.class);

		_serviceTrackerMap =
			(ServiceTrackerMap<String, List<AssetAutoTagProvider<?>>>)
				(ServiceTrackerMap)ServiceTrackerMapFactory.openMultiValueMap(
					bundle.getBundleContext(), AssetAutoTagProvider.class,
					"model.class.name");
	}

}