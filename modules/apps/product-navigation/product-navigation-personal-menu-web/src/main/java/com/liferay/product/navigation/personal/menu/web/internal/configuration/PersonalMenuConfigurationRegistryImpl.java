/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.personal.menu.web.internal.configuration;

import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration;
import com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfigurationRegistry;

import java.util.Dictionary;
import java.util.Map;
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
 * @author Samuel Trong Tran
 */
@Component(
	configurationPid = "com.liferay.product.navigation.personal.menu.configuration.PersonalMenuConfiguration",
	service = PersonalMenuConfigurationRegistry.class
)
public class PersonalMenuConfigurationRegistryImpl
	implements PersonalMenuConfigurationRegistry {

	@Override
	public PersonalMenuConfiguration getCompanyPersonalMenuConfiguration(
		long companyId) {

		return _companyPersonalMenuConfigurations.getOrDefault(
			companyId, _systemPersonalMenuConfiguration);
	}

	@Activate
	protected void activate(
		BundleContext bundleContext, Map<String, Object> properties) {

		modified(properties);

		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new PersonalMenuConfigurationRegistryManagedServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				"com.liferay.product.navigation.personal.menu.configuration." +
					"PersonalMenuConfiguration.scoped"
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	@Modified
	protected void modified(Map<String, Object> properties) {
		_systemPersonalMenuConfiguration = ConfigurableUtil.createConfigurable(
			PersonalMenuConfiguration.class, properties);
	}

	private void _unmapPid(String pid) {
		Long companyId = _companyIds.remove(pid);

		if (companyId != null) {
			_companyPersonalMenuConfigurations.remove(companyId);
		}
	}

	private final Map<String, Long> _companyIds = new ConcurrentHashMap<>();
	private final Map<Long, PersonalMenuConfiguration>
		_companyPersonalMenuConfigurations = new ConcurrentHashMap<>();
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;
	private volatile PersonalMenuConfiguration _systemPersonalMenuConfiguration;

	private class PersonalMenuConfigurationRegistryManagedServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			_unmapPid(pid);
		}

		@Override
		public String getName() {
			return "com.liferay.product.navigation.personal.menu." +
				"configuration.PersonalMenuConfiguration.scoped";
		}

		@Override
		public void updated(String pid, Dictionary dictionary)
			throws ConfigurationException {

			_unmapPid(pid);

			long companyId = GetterUtil.getLong(
				dictionary.get("companyId"), CompanyConstants.SYSTEM);

			if (companyId != CompanyConstants.SYSTEM) {
				_companyIds.put(pid, companyId);
				_companyPersonalMenuConfigurations.put(
					companyId,
					ConfigurableUtil.createConfigurable(
						PersonalMenuConfiguration.class, dictionary));
			}
		}

	}

}