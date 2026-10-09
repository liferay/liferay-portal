/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.iframe.sanitizer.internal.configuration.helper;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.SetUtil;
import com.liferay.portal.security.iframe.sanitizer.configuration.IFrameConfiguration;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Modified;

/**
 * @author Alicia García
 */
@Component(
	configurationPid = "com.liferay.portal.security.iframe.sanitizer.configuration.IFrameConfiguration",
	service = IFrameConfigurationHelper.class
)
public class IFrameConfigurationHelper {

	public Set<String> getCompanyBlacklist(long companyId) {
		return _companyBlacklist.getOrDefault(companyId, _defaultBlacklist);
	}

	public IFrameConfiguration getCompanyIFrameConfiguration(long companyId) {
		return _companyConfigurationBeans.getOrDefault(
			companyId, _defaultIFrameConfiguration);
	}

	public Set<String> getCompanyWhitelist(long companyId) {
		return _companyWhitelist.getOrDefault(companyId, _defaultWhitelist);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new IFrameConfigurationManagedServiceFactory(),
			MapUtil.singletonDictionary(
				Constants.SERVICE_PID,
				"com.liferay.portal.security.iframe.sanitizer.configuration." +
					"IFrameConfiguration.scoped"));
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_defaultIFrameConfiguration = ConfigurableUtil.createConfigurable(
			IFrameConfiguration.class, properties);

		_defaultBlacklist = SetUtil.fromArray(
			_defaultIFrameConfiguration.blacklist());
		_defaultWhitelist = SetUtil.fromArray(
			_defaultIFrameConfiguration.whitelist());
	}

	private Set<String> _getBlacklist(long companyId) {
		IFrameConfiguration iFrameConfiguration = getCompanyIFrameConfiguration(
			companyId);

		if (iFrameConfiguration == null) {
			return Collections.emptySet();
		}

		return _getClassNames(
			SetUtil.fromArray(iFrameConfiguration.blacklist()));
	}

	private Set<String> _getClassNames(Set<String> classNames) {
		if (SetUtil.isEmpty(classNames)) {
			return Collections.emptySet();
		}

		for (String className : classNames) {
			className = className.trim();

			if (className.isEmpty()) {
				continue;
			}

			className = _stripTrailingStar(className);

			classNames.add(className);
		}

		return classNames;
	}

	private Set<String> _getWhitelist(long companyId) {
		IFrameConfiguration iFrameConfiguration = getCompanyIFrameConfiguration(
			companyId);

		if (iFrameConfiguration == null) {
			return Collections.emptySet();
		}

		return _getClassNames(
			SetUtil.fromArray(iFrameConfiguration.whitelist()));
	}

	private String _stripTrailingStar(String item) {
		if (item.equals(StringPool.STAR)) {
			return item;
		}

		char c = item.charAt(item.length() - 1);

		if (c == CharPool.STAR) {
			return item.substring(0, item.length() - 1);
		}

		return item;
	}

	private final Map<Long, Set<String>> _companyBlacklist =
		new ConcurrentHashMap<>();
	private final Map<Long, IFrameConfiguration> _companyConfigurationBeans =
		new ConcurrentHashMap<>();
	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final Map<Long, Set<String>> _companyWhitelist =
		new ConcurrentHashMap<>();
	private volatile Set<String> _defaultBlacklist = new HashSet<>();
	private volatile IFrameConfiguration _defaultIFrameConfiguration;
	private volatile Set<String> _defaultWhitelist = new HashSet<>();
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;

	private class IFrameConfigurationManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_unmapPid(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.portal.security.iframe.sanitizer." +
				"configuration.IFrameConfiguration.scoped";
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
						IFrameConfiguration.class, dictionary));
				_companyIds.put(pid, companyId);
				_companyBlacklist.put(companyId, _getBlacklist(companyId));
				_companyWhitelist.put(companyId, _getWhitelist(companyId));
			}
		}

		private void _unmapPid(String pid) {
			Long companyId = _companyIds.remove(pid);

			if (companyId != null) {
				_companyConfigurationBeans.remove(companyId);
				_companyBlacklist.remove(companyId);
				_companyWhitelist.remove(companyId);
			}
		}

	}

}