/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.language.override.internal;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.language.LanguageResources;
import com.liferay.portal.language.override.internal.provider.PLOOriginalTranslationThreadLocal;
import com.liferay.portal.language.override.model.PLOEntry;
import com.liferay.portal.language.override.service.PLOEntryLocalService;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Regisson Aguiar
 */
@Component(service = PLOOverrideResourceBundleManager.class)
public class PLOOverrideResourceBundleManager {

	public ResourceBundle getOverrideResourceBundle(Locale locale) {
		long companyId = CompanyThreadLocal.getCompanyId();

		DCLSingleton<Map<String, OverrideResourceBundle>>
			overrideResourceBundleDCLSingleton =
				_overrideResourceBundleMaps.get(companyId);

		if (overrideResourceBundleDCLSingleton == null) {
			overrideResourceBundleDCLSingleton = new DCLSingleton<>();

			DCLSingleton<Map<String, OverrideResourceBundle>>
				previousOverrideResourceBundleDCLSingleton =
					_overrideResourceBundleMaps.putIfAbsent(
						companyId, overrideResourceBundleDCLSingleton);

			if (previousOverrideResourceBundleDCLSingleton != null) {
				overrideResourceBundleDCLSingleton =
					previousOverrideResourceBundleDCLSingleton;
			}
		}

		Map<String, OverrideResourceBundle> companyOverrideResourceBundles =
			overrideResourceBundleDCLSingleton.getSingleton(
				() -> _createOverrideResourceBundles(companyId));

		if (companyOverrideResourceBundles.isEmpty() ||
			PLOOriginalTranslationThreadLocal.isUseOriginalTranslation()) {

			return null;
		}

		return companyOverrideResourceBundles.get(
			_language.getLanguageId(locale));
	}

	protected static void clearCache() {
		_overrideResourceBundleMaps.clear();

		LanguageResources.clearResourceBundles();
	}

	private Map<String, OverrideResourceBundle> _createOverrideResourceBundles(
		long companyId) {

		Map<String, OverrideResourceBundle> companyOverrideResourceBundles =
			new HashMap<>();

		for (PLOEntry ploEntry :
				_ploEntryLocalService.getPLOEntries(companyId)) {

			companyOverrideResourceBundles.compute(
				ploEntry.getLanguageId(),
				(key, value) -> {
					if (value == null) {
						value = new OverrideResourceBundle(new HashMap<>());
					}

					value.put(ploEntry.getKey(), ploEntry.getValue());

					return value;
				});
		}

		return companyOverrideResourceBundles;
	}

	private static final Map
		<Long, DCLSingleton<Map<String, OverrideResourceBundle>>>
			_overrideResourceBundleMaps = new ConcurrentHashMap<>();

	@Reference
	private Language _language;

	@Reference
	private PLOEntryLocalService _ploEntryLocalService;

	private static class OverrideResourceBundle extends ResourceBundle {

		@Override
		public Enumeration<String> getKeys() {
			return Collections.enumeration(_overrideMap.keySet());
		}

		public boolean isEmpty() {
			return _overrideMap.isEmpty();
		}

		public void put(String key, String value) {
			_overrideMap.put(key, value);
		}

		public void remove(String key) {
			_overrideMap.remove(key);
		}

		@Override
		protected Object handleGetObject(String key) {
			return _overrideMap.get(key);
		}

		@Override
		protected Set<String> handleKeySet() {
			return _overrideMap.keySet();
		}

		private OverrideResourceBundle(Map<String, String> overrideMap) {
			_overrideMap = overrideMap;
		}

		private final Map<String, String> _overrideMap;

	}

}