/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.admin.service;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.Constants;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		Constants.SERVICE_PID + "=com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration",
		Constants.SERVICE_PID + "=com.liferay.portal.vulcan.internal.configuration.HeadlessAPICacheCompanyConfiguration.scoped"
	},
	service = ManagedServiceFactory.class
)
public class HeadlessAPICacheManagedServiceFactory
	implements ManagedServiceFactory {

	@Override
	public void deleted(String pid) {
		Dictionary<String, ?> dictionary = _dictionaries.remove(pid);

		if (dictionary != null) {
			_updateCacheableEndpoints(
				GetterUtil.getLong(dictionary.get("companyId")));
		}
	}

	public String getCacheControl(long companyId, String path) {
		List<CacheableEndpoint> cacheableEndpoints = _cacheableEndpoints.get(
			companyId);

		if (ListUtil.isEmpty(cacheableEndpoints)) {
			return null;
		}

		String[] pathParts = StringUtil.split(path, CharPool.SLASH);

		for (CacheableEndpoint cacheableEndpoint : cacheableEndpoints) {
			if (cacheableEndpoint.matches(pathParts)) {
				return cacheableEndpoint.getCacheControl();
			}
		}

		return null;
	}

	@Override
	public String getName() {
		return _FACTORY_PID;
	}

	@Override
	public void updated(String pid, Dictionary<String, ?> dictionary) {
		long companyId = GetterUtil.getLong(
			dictionary.get("companyId"), CompanyConstants.SYSTEM);

		if (companyId == CompanyConstants.SYSTEM) {
			deleted(pid);

			return;
		}

		Dictionary<String, ?> oldDictionary = _dictionaries.put(
			pid, dictionary);

		if (oldDictionary != null) {
			long oldCompanyId = GetterUtil.getLong(
				oldDictionary.get("companyId"));

			if (oldCompanyId != companyId) {
				_updateCacheableEndpoints(oldCompanyId);
			}
		}

		_updateCacheableEndpoints(companyId);
	}

	private void _updateCacheableEndpoints(long companyId) {
		List<CacheableEndpoint> cacheableEndpoints = new ArrayList<>();

		for (Dictionary<String, ?> dictionary : _dictionaries.values()) {
			if (companyId != GetterUtil.getLong(dictionary.get("companyId"))) {
				continue;
			}

			cacheableEndpoints.add(
				new CacheableEndpoint(
					ConfigurableUtil.createConfigurable(
						HeadlessAPICacheCompanyConfiguration.class,
						dictionary)));
		}

		if (cacheableEndpoints.isEmpty()) {
			_cacheableEndpoints.remove(companyId);

			return;
		}

		Collections.sort(cacheableEndpoints);

		_cacheableEndpoints.put(companyId, cacheableEndpoints);
	}

	private static final String _FACTORY_PID =
		"com.liferay.portal.vulcan.internal.configuration." +
			"HeadlessAPICacheCompanyConfiguration";

	private final Map<Long, List<CacheableEndpoint>> _cacheableEndpoints =
		new ConcurrentHashMap<>();
	private final Map<String, Dictionary<String, ?>> _dictionaries =
		new ConcurrentHashMap<>();

	private static class CacheableEndpoint
		implements Comparable<CacheableEndpoint> {

		public CacheableEndpoint(
			HeadlessAPICacheCompanyConfiguration
				headlessAPICacheCompanyConfiguration) {

			_patternParts = StringUtil.split(
				headlessAPICacheCompanyConfiguration.path(), CharPool.SLASH);

			if (headlessAPICacheCompanyConfiguration.maxAge() <= 0) {
				_cacheControl =
					headlessAPICacheCompanyConfiguration.cacheControl();
			}
			else {
				_cacheControl = StringBundler.concat(
					headlessAPICacheCompanyConfiguration.cacheControl(),
					", max-age=",
					headlessAPICacheCompanyConfiguration.maxAge());
			}

			int wildcardCount = 0;

			for (String patternPart : _patternParts) {
				if (Objects.equals(patternPart, _WILDCARD)) {
					wildcardCount++;
				}
			}

			_wildcardCount = wildcardCount;
		}

		@Override
		public int compareTo(CacheableEndpoint cacheableEndpoint) {
			if (_wildcardCount != cacheableEndpoint._wildcardCount) {
				return Integer.compare(
					_wildcardCount, cacheableEndpoint._wildcardCount);
			}

			return Integer.compare(
				cacheableEndpoint._patternParts.length, _patternParts.length);
		}

		public String getCacheControl() {
			return _cacheControl;
		}

		public boolean matches(String[] pathParts) {
			if (pathParts.length != _patternParts.length) {
				return false;
			}

			for (int i = 0; i < pathParts.length; i++) {
				if (!Objects.equals(_patternParts[i], _WILDCARD) &&
					!Objects.equals(pathParts[i], _patternParts[i])) {

					return false;
				}
			}

			return true;
		}

		private static final String _WILDCARD = "*";

		private final String _cacheControl;
		private final String[] _patternParts;
		private final int _wildcardCount;

	}

}