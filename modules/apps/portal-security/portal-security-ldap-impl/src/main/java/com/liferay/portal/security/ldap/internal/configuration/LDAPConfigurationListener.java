/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.internal.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.ldap.authenticator.configuration.LDAPAuthConfiguration;
import com.liferay.portal.security.ldap.configuration.CompanyScopedConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.ConfigurationProvider;
import com.liferay.portal.security.ldap.configuration.SystemLDAPConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPExportConfiguration;
import com.liferay.portal.security.ldap.exportimport.configuration.LDAPImportConfiguration;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationEvent;
import org.osgi.service.cm.ConfigurationListener;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Michael C. Han
 */
@Component(service = {})
public class LDAPConfigurationListener implements ConfigurationListener {

	@Override
	public void configurationEvent(ConfigurationEvent configurationEvent) {
		String factoryPid = configurationEvent.getFactoryPid();

		if (Validator.isNull(factoryPid)) {
			factoryPid = configurationEvent.getPid();
		}

		if (factoryPid.endsWith(".scoped")) {
			factoryPid = StringUtil.replaceLast(
				factoryPid, ".scoped", StringPool.BLANK);
		}

		ConfigurationProvider<?> configurationProvider =
			_configurationProviders.get(factoryPid);

		if (configurationProvider == null) {
			return;
		}

		try {
			if (configurationEvent.getType() == ConfigurationEvent.CM_DELETED) {
				configurationProvider.unregisterConfiguration(
					configurationEvent.getPid());
			}
			else {
				configurationProvider.registerConfiguration(
					_configurationAdmin.getConfiguration(
						configurationEvent.getPid(), StringPool.QUESTION));
			}
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to load configuration " + configurationEvent.getPid(),
				ioException);
		}
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistrations.add(
			bundleContext.registerService(
				ConfigurationListener.class, this, null));

		_register(
			bundleContext,
			new CompanyScopedConfigurationProvider<>(
				_configurationAdmin, LDAPAuthConfiguration.class));
		_register(
			bundleContext,
			new CompanyScopedConfigurationProvider<>(
				_configurationAdmin, LDAPExportConfiguration.class));
		_register(
			bundleContext,
			new CompanyScopedConfigurationProvider<>(
				_configurationAdmin, LDAPImportConfiguration.class));
		_register(
			bundleContext,
			new CompanyScopedConfigurationProvider<>(
				_configurationAdmin, SystemLDAPConfiguration.class));
		_register(
			bundleContext,
			new LDAPServerConfigurationProviderImpl(_configurationAdmin));
	}

	@Deactivate
	protected void deactivate() {
		for (ServiceRegistration<?> serviceRegistration :
				_serviceRegistrations) {

			serviceRegistration.unregister();
		}
	}

	private void _register(
		BundleContext bundleContext,
		ConfigurationProvider<?> configurationProvider) {

		Class<?> metatype = configurationProvider.getMetatype();

		String factoryPid = metatype.getName();

		_configurationProviders.put(factoryPid, configurationProvider);

		_serviceRegistrations.add(
			bundleContext.registerService(
				ConfigurationProvider.class.getName(), configurationProvider,
				MapUtil.singletonDictionary("factoryPid", factoryPid)));
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private final Map<String, ConfigurationProvider<?>>
		_configurationProviders = new HashMap<>();
	private final List<ServiceRegistration<?>> _serviceRegistrations =
		new ArrayList<>();

}