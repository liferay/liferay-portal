/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.ldap.configuration;

import aQute.bnd.annotation.metatype.Meta;

import com.liferay.petra.concurrent.DCLSingleton;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Michael C. Han
 */
public abstract class BaseConfigurationProvider<T>
	implements ConfigurationProvider<T>,
			   ServiceFactory<ConfigurationProvider<T>> {

	public BaseConfigurationProvider(ConfigurationAdmin configurationAdmin) {
		this.configurationAdmin = configurationAdmin;
	}

	@Override
	public ConfigurationProvider<T> getService(
		Bundle bundle,
		ServiceRegistration<ConfigurationProvider<T>> serviceRegistration) {

		return _configurationProviderDCLSingleton.getSingleton(
			this::_registerConfigurations);
	}

	@Override
	public void ungetService(
		Bundle bundle,
		ServiceRegistration<ConfigurationProvider<T>> serviceRegistration,
		ConfigurationProvider<T> configurationProvider) {
	}

	protected String getMetatypeId() {
		if (_factoryPid != null) {
			return _factoryPid;
		}

		Class<T> metatype = getMetatype();

		Meta.OCD metaOCD = metatype.getAnnotation(Meta.OCD.class);

		if (metaOCD == null) {
			return null;
		}

		_factoryPid = metaOCD.id();

		if (_factoryPid == null) {
			_factoryPid = metatype.getName();
		}

		return _factoryPid;
	}

	protected final ConfigurationAdmin configurationAdmin;

	private ConfigurationProvider<T> _registerConfigurations() {
		try {
			Configuration[] configurations =
				configurationAdmin.listConfigurations(
					"(service.factoryPid=" + getMetatypeId() + "*)");

			if (configurations != null) {
				for (Configuration configuration : configurations) {
					registerConfiguration(configuration);
				}
			}
		}
		catch (Exception exception) {
			if (_log.isWarnEnabled()) {
				_log.warn("Unable to register configurations", exception);
			}
		}

		return this;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		BaseConfigurationProvider.class);

	private final DCLSingleton<ConfigurationProvider<T>>
		_configurationProviderDCLSingleton = new DCLSingleton<>();
	private String _factoryPid;

}