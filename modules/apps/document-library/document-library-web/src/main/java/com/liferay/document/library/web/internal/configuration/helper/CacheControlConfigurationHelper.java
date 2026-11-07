/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.configuration.helper;

import com.liferay.document.library.web.internal.configuration.CacheControlConfiguration;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;

import java.util.Dictionary;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;

/**
 * @author Adolfo Pérez
 */
public class CacheControlConfigurationHelper {

	public CacheControlConfigurationHelper(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new CacheControlConfigurationManagedServiceFactory(),
			MapUtil.singletonDictionary(
				Constants.SERVICE_PID,
				"com.liferay.document.library.web.internal.configuration." +
					"CacheControlConfiguration.scoped"));
	}

	public void destroy() {
		_serviceRegistration.unregister();
	}

	public CacheControlConfiguration getCompanyCacheControlConfiguration(
		long companyId) {

		return _companyConfigurationBeans.getOrDefault(
			companyId, _systemCacheControlConfiguration);
	}

	public void modified(Map<String, Object> properties) {
		_systemCacheControlConfiguration = ConfigurableUtil.createConfigurable(
			CacheControlConfiguration.class, properties);
	}

	private final Map<Long, CacheControlConfiguration>
		_companyConfigurationBeans = new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final ServiceRegistration<ManagedServiceFactory>
		_serviceRegistration;
	private volatile CacheControlConfiguration _systemCacheControlConfiguration;

	private class CacheControlConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_unmapPid(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.document.library.web.internal.configuration." +
				"CacheControlConfiguration.scoped";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> dictionary)
			throws ConfigurationException {

			_unmapPid(pid);

			long companyId = GetterUtil.getLong(
				dictionary.get("companyId"), CompanyConstants.SYSTEM);

			if (companyId != CompanyConstants.SYSTEM) {
				_companyConfigurationBeans.put(
					companyId,
					ConfigurableUtil.createConfigurable(
						CacheControlConfiguration.class, dictionary));
				_companyIds.put(pid, companyId);
			}
		}

		private void _unmapPid(String pid) {
			Long companyId = _companyIds.remove(pid);

			if (companyId != null) {
				_companyConfigurationBeans.remove(companyId);
			}
		}

	}

}