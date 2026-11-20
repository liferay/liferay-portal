/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.configuration;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.configuration.metatype.bnd.util.ConfigurableUtil;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.CompanyConstants;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.ObjectValuePair;
import com.liferay.portal.security.ldap.constants.LDAPConstants;

import java.io.IOException;

import java.util.Collections;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Michael C. Han
 */
public class CompanyScopedConfigurationProvider
	<T extends CompanyScopedConfiguration>
		extends BaseConfigurationProvider<T>
		implements ConfigurationProvider<T> {

	public CompanyScopedConfigurationProvider(
		ConfigurationAdmin configurationAdmin, Class<T> metatype) {

		super(configurationAdmin);

		_metatype = metatype;

		_defaultConfiguration = ConfigurableUtil.createConfigurable(
			metatype, Collections.emptyMap());
	}

	@Override
	public boolean delete(long companyId) {
		ObjectValuePair<Configuration, T> objectValuePair =
			_configurations.remove(companyId);

		if (objectValuePair == null) {
			return false;
		}

		Configuration configuration = objectValuePair.getKey();

		_pidCompanyConfigurations.remove(configuration.getPid());

		try {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			configuration.delete();

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Deleted configuration ", getMetatypeId(),
						" for company ", companyId, " with properties: ",
						properties));
			}
		}
		catch (IOException ioException) {
			throw new SystemException(ioException);
		}

		return true;
	}

	@Override
	public boolean delete(long companyId, long index) {
		return delete(companyId);
	}

	@Override
	public T getConfiguration(long companyId) {
		ObjectValuePair<Configuration, T> objectValuePair = _configurations.get(
			companyId);

		if (objectValuePair == null) {
			objectValuePair = _configurations.get(CompanyConstants.SYSTEM);
		}

		if (objectValuePair == null) {
			return _defaultConfiguration;
		}

		return objectValuePair.getValue();
	}

	@Override
	public T getConfiguration(long companyId, long index) {
		return getConfiguration(companyId);
	}

	@Override
	public Dictionary<String, Object> getConfigurationProperties(
		long companyId) {

		ObjectValuePair<Configuration, T> objectValuePair = _configurations.get(
			companyId);

		if (objectValuePair == null) {
			objectValuePair = _configurations.get(CompanyConstants.SYSTEM);
		}

		if (objectValuePair == null) {
			return new HashMapDictionary<>();
		}

		Configuration configuration = objectValuePair.getKey();

		return configuration.getProperties();
	}

	@Override
	public Dictionary<String, Object> getConfigurationProperties(
		long companyId, long index) {

		return getConfigurationProperties(companyId);
	}

	@Override
	public List<T> getConfigurations(long companyId) {
		return getConfigurations(companyId, true);
	}

	@Override
	public List<T> getConfigurations(long companyId, boolean useDefault) {
		ObjectValuePair<Configuration, T> objectValuePair = _configurations.get(
			companyId);

		if ((objectValuePair == null) && useDefault) {
			objectValuePair = _configurations.get(CompanyConstants.SYSTEM);
		}

		if ((objectValuePair == null) && useDefault) {
			return Collections.singletonList(_defaultConfiguration);
		}
		else if (objectValuePair != null) {
			return Collections.singletonList(objectValuePair.getValue());
		}

		return Collections.emptyList();
	}

	@Override
	public List<Dictionary<String, Object>> getConfigurationsProperties(
		long companyId) {

		return getConfigurationsProperties(companyId, true);
	}

	@Override
	public List<Dictionary<String, Object>> getConfigurationsProperties(
		long companyId, boolean useDefault) {

		ObjectValuePair<Configuration, T> objectValuePair = _configurations.get(
			companyId);

		if ((objectValuePair == null) && useDefault) {
			objectValuePair = _configurations.get(CompanyConstants.SYSTEM);
		}

		if ((objectValuePair == null) && useDefault) {
			return Collections.<Dictionary<String, Object>>singletonList(
				new HashMapDictionary<String, Object>());
		}
		else if (objectValuePair != null) {
			Configuration configuration = objectValuePair.getKey();

			return Collections.singletonList(configuration.getProperties());
		}

		return Collections.emptyList();
	}

	@Override
	public Class<T> getMetatype() {
		return _metatype;
	}

	@Override
	public synchronized void registerConfiguration(
		Configuration configuration) {

		Dictionary<String, Object> properties = configuration.getProperties();

		if (properties == null) {
			properties = new HashMapDictionary<>();
		}

		T configurable = ConfigurableUtil.createConfigurable(
			getMetatype(), properties);

		_configurations.put(
			configurable.companyId(),
			new ObjectValuePair<>(configuration, configurable));
		_pidCompanyConfigurations.put(
			configuration.getPid(), configurable.companyId());
	}

	@Override
	public synchronized void unregisterConfiguration(String pid) {
		Long companyId = _pidCompanyConfigurations.remove(pid);

		if (companyId != null) {
			_configurations.remove(companyId);
		}
	}

	@Override
	public void updateProperties(
		long companyId, Dictionary<String, Object> properties) {

		if (properties == null) {
			properties = new HashMapDictionary<>();
		}

		properties.put(LDAPConstants.COMPANY_ID, companyId);

		ObjectValuePair<Configuration, T> objectValuePair = _configurations.get(
			companyId);

		try {
			Configuration configuration = null;

			if (objectValuePair == null) {
				configuration = configurationAdmin.createFactoryConfiguration(
					getMetatypeId(), StringPool.QUESTION);
			}
			else {
				configuration = objectValuePair.getKey();
			}

			configuration.update(properties);

			if (_log.isDebugEnabled()) {
				_log.debug(
					StringBundler.concat(
						"Updated configuration ", getMetatypeId(),
						" for company ", companyId, " with properties: ",
						properties));
			}
		}
		catch (IOException ioException) {
			throw new SystemException(
				"Unable to update configuration", ioException);
		}
	}

	@Override
	public void updateProperties(
		long companyId, long index, Dictionary<String, Object> properties) {

		updateProperties(companyId, properties);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CompanyScopedConfigurationProvider.class);

	private final Map<Long, ObjectValuePair<Configuration, T>> _configurations =
		new HashMap<>();
	private final T _defaultConfiguration;
	private final Class<T> _metatype;
	private final Map<String, Long> _pidCompanyConfigurations = new HashMap<>();

}