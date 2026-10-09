/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.resource.bundle;

import com.liferay.petra.concurrent.ConcurrentReferenceKeyHashMap;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Carlos Sierra Andrés
 */
public class CacheResourceBundleLoader implements ResourceBundleLoader {

	public static void clearCache() {
		for (CacheResourceBundleLoader cacheResourceBundleLoader :
				_cacheResourceBundleLoaders) {

			if (cacheResourceBundleLoader == null) {
				continue;
			}

			Map<Locale, ResourceBundle> resourceBundles =
				cacheResourceBundleLoader._resourceBundles;

			resourceBundles.clear();
		}
	}

	public CacheResourceBundleLoader(
		ResourceBundleLoader resourceBundleLoader) {

		_resourceBundleLoader = resourceBundleLoader;

		_cacheResourceBundleLoaders.add(this);
	}

	@Override
	public ResourceBundle loadResourceBundle(Locale locale) {
		ResourceBundle resourceBundle = _resourceBundles.get(locale);

		if (resourceBundle == _nullResourceBundle) {
			return null;
		}

		if (resourceBundle == null) {
			try {
				resourceBundle = _resourceBundleLoader.loadResourceBundle(
					locale);
			}
			catch (Exception exception) {
				if (_log.isDebugEnabled()) {
					_log.debug(exception);
				}
			}

			if (resourceBundle == null) {
				_resourceBundles.put(locale, _nullResourceBundle);
			}
			else {
				_resourceBundles.put(locale, resourceBundle);
			}
		}

		return resourceBundle;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CacheResourceBundleLoader.class);

	private static final Set<CacheResourceBundleLoader>
		_cacheResourceBundleLoaders = Collections.newSetFromMap(
			new ConcurrentReferenceKeyHashMap<>(
				FinalizeManager.WEAK_REFERENCE_FACTORY));

	private static final ResourceBundle _nullResourceBundle =
		new ResourceBundle() {

			@Override
			public Enumeration<String> getKeys() {
				throw new UnsupportedOperationException();
			}

			@Override
			protected Object handleGetObject(String key) {
				throw new UnsupportedOperationException();
			}

		};

	private final ResourceBundleLoader _resourceBundleLoader;
	private final Map<Locale, ResourceBundle> _resourceBundles =
		new ConcurrentHashMap<>();

}