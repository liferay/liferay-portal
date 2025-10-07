/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.internal.configuration;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.saml.constants.SamlProviderConfigurationKeys;
import com.liferay.saml.runtime.configuration.SamlProviderConfiguration;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedServiceFactory;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mika Koivisto
 */
@Component(service = SamlProviderConfigurationHelper.class)
public class SamlProviderConfigurationHelperImpl
	implements SamlProviderConfigurationHelper {

	@Override
	public SamlProviderConfiguration getSamlProviderConfiguration() {
		ConfigurationHolder configurationHolder =
			_configurationHolderByCompanyId.get(
				CompanyThreadLocal.getCompanyId());

		if (configurationHolder == null) {
			configurationHolder = _configurationHolderByCompanyId.get(
				CompanyConstants.SYSTEM);
		}

		if (configurationHolder != null) {
			return configurationHolder.getSamlProviderConfiguration();
		}

		return _defaultSamlProviderConfiguration;
	}

	@Override
	public boolean isEnabled() {
		SamlProviderConfiguration samlProviderConfiguration =
			getSamlProviderConfiguration();

		return samlProviderConfiguration.enabled();
	}

	@Override
	public boolean isLDAPImportEnabled() {
		SamlProviderConfiguration samlProviderConfiguration =
			getSamlProviderConfiguration();

		return samlProviderConfiguration.ldapImportEnabled();
	}

	@Override
	public boolean isRoleIb() {
		SamlProviderConfiguration samlProviderConfiguration =
			getSamlProviderConfiguration();

		return Objects.equals(
			samlProviderConfiguration.role(),
			SamlProviderConfigurationKeys.SAML_ROLE_IB);
	}

	@Override
	public boolean isRoleIdp() {
		SamlProviderConfiguration samlProviderConfiguration =
			getSamlProviderConfiguration();

		return Objects.equals(
			samlProviderConfiguration.role(),
			SamlProviderConfigurationKeys.SAML_ROLE_IDP);
	}

	@Override
	public boolean isRoleSp() {
		SamlProviderConfiguration samlProviderConfiguration =
			getSamlProviderConfiguration();

		return Objects.equals(
			samlProviderConfiguration.role(),
			SamlProviderConfigurationKeys.SAML_ROLE_SP);
	}

	@Override
	public void updateProperties(UnicodeProperties unicodeProperties)
		throws Exception {

		long companyId = CompanyThreadLocal.getCompanyId();

		ConfigurationHolder configurationHolder =
			_configurationHolderByCompanyId.get(companyId);

		Configuration configuration = null;
		Dictionary<String, Object> configurationProperties = null;

		if (configurationHolder == null) {
			configuration = _configurationAdmin.createFactoryConfiguration(
				FACTORY_PID, StringPool.QUESTION);

			configurationProperties = new HashMapDictionary<>();

			Dictionary<String, ?> systemProperties = _getSystemProperties();

			if (systemProperties != null) {
				Enumeration<String> enumeration = systemProperties.keys();

				while (enumeration.hasMoreElements()) {
					String key = enumeration.nextElement();

					configurationProperties.put(key, systemProperties.get(key));
				}
			}
		}
		else {
			configuration = _configurationAdmin.getConfiguration(
				configurationHolder.getPid(), StringPool.QUESTION);

			configurationProperties = configuration.getProperties();
		}

		for (Map.Entry<String, String> mapEntry :
				unicodeProperties.entrySet()) {

			configurationProperties.put(mapEntry.getKey(), mapEntry.getValue());
		}

		configurationProperties.put("companyId", companyId);

		configuration.update(configurationProperties);

		_updateConfiguration(
			configuration.getPid(), configuration.getProperties());
	}

	@Activate
	protected void activate(BundleContext bundleContext) {
		_serviceRegistration = bundleContext.registerService(
			ManagedServiceFactory.class,
			new SamlProviderConfigurationServiceFactory(),
			HashMapDictionaryBuilder.put(
				Constants.SERVICE_PID,
				SamlProviderConfigurationHelperImpl.FACTORY_PID
			).build());
	}

	@Deactivate
	protected void deactivate() {
		_serviceRegistration.unregister();
	}

	protected static final String FACTORY_PID =
		"com.liferay.saml.runtime.configuration.SamlProviderConfiguration";

	private Dictionary<String, ?> _getSystemProperties() throws Exception {
		ConfigurationHolder configurationHolder =
			_configurationHolderByCompanyId.get(CompanyConstants.SYSTEM);

		if (configurationHolder == null) {
			return null;
		}

		Configuration configuration = _configurationAdmin.getConfiguration(
			configurationHolder.getPid(), StringPool.QUESTION);

		return configuration.getProperties();
	}

	private void _updateConfiguration(
			String pid, Dictionary<String, ?> properties)
		throws ConfigurationException {

		Long companyId = GetterUtil.getLong(properties.get("companyId"));

		SamlProviderConfiguration samlProviderConfiguration =
			ConfigurableUtil.createConfigurable(
				SamlProviderConfiguration.class, properties);

		ConfigurationHolder configurationHolder = new ConfigurationHolder(
			samlProviderConfiguration, pid);

		_configurationHolderByCompanyId.put(companyId, configurationHolder);

		ConfigurationHolder oldConfigurationHolder =
			_configurationHolderByPid.put(pid, configurationHolder);

		if (oldConfigurationHolder != null) {
			SamlProviderConfiguration oldSamlProviderConfiguration =
				oldConfigurationHolder.getSamlProviderConfiguration();

			if (oldSamlProviderConfiguration.companyId() != companyId) {
				_configurationHolderByCompanyId.remove(
					oldSamlProviderConfiguration.companyId());
			}
		}
	}

	private static final Log _log = LogFactoryUtil.getLog(
		SamlProviderConfigurationHelperImpl.class);

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	private final Map<Long, ConfigurationHolder>
		_configurationHolderByCompanyId = new ConcurrentHashMap<>();
	private final Map<String, ConfigurationHolder> _configurationHolderByPid =
		new ConcurrentHashMap<>();
	private final SamlProviderConfiguration _defaultSamlProviderConfiguration =
		ConfigurableUtil.createConfigurable(
			SamlProviderConfiguration.class, Collections.emptyMap());
	private ServiceRegistration<ManagedServiceFactory> _serviceRegistration;

	private class ConfigurationHolder {

		public ConfigurationHolder(
			SamlProviderConfiguration samlProviderConfiguration, String pid) {

			_samlProviderConfiguration = samlProviderConfiguration;
			_pid = pid;
		}

		public String getPid() {
			return _pid;
		}

		public SamlProviderConfiguration getSamlProviderConfiguration() {
			return _samlProviderConfiguration;
		}

		private final String _pid;
		private final SamlProviderConfiguration _samlProviderConfiguration;

	}

	private class SamlProviderConfigurationServiceFactory
		implements ManagedServiceFactory {

		@Override
		public void deleted(String pid) {
			ConfigurationHolder configurationHolder =
				_configurationHolderByPid.remove(pid);

			if (configurationHolder == null) {
				if (_log.isWarnEnabled()) {
					_log.warn("Unable to delete missing configuration " + pid);
				}

				return;
			}

			SamlProviderConfiguration samlProviderConfiguration =
				configurationHolder.getSamlProviderConfiguration();

			_configurationHolderByCompanyId.remove(
				samlProviderConfiguration.companyId());
		}

		@Override
		public String getName() {
			return "SAML Provider Configuration Factory";
		}

		@Override
		public void updated(String pid, Dictionary<String, ?> properties)
			throws ConfigurationException {

			_updateConfiguration(pid, properties);
		}

	}

}