/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.configuration.persistence.listener;

import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListener;
import com.liferay.portal.configuration.persistence.listener.ConfigurationModelListenerException;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleThreadLocal;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.vulcan.internal.configuration.VulcanCompanyConfiguration;
import com.liferay.portal.vulcan.internal.configuration.VulcanConfiguration;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Carlos Correa
 */
public class BaseConfigurationModelListener
	implements ConfigurationModelListener {

	@Override
	public void onBeforeSave(String pid, Dictionary<String, Object> dictionary)
		throws ConfigurationModelListenerException {

		try {
			String path = (String)dictionary.get("path");

			_validatePathExists(path);

			_validateUniqueConfiguration(path, dictionary);
		}
		catch (Exception exception) {
			throw new ConfigurationModelListenerException(
				exception.getMessage(), BaseConfigurationModelListener.class,
				getClass(), dictionary);
		}
	}

	protected void init(ConfigurationAdmin configurationAdmin) {
		_configurationAdmin = configurationAdmin;
	}

	private void _validatePathExists(String path) throws Exception {
		if (Validator.isNotNull(path)) {
			return;
		}

		throw new Exception(
			ResourceBundleUtil.getString(
				ResourceBundleUtil.getBundle(
					"content.Language",
					LocaleThreadLocal.getThemeDisplayLocale(), getClass()),
				"path-cannot-be-empty"));
	}

	private void _validateUniqueConfiguration(
			Dictionary<String, Object> dictionary, String filterString)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			filterString);

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			if (configuration == null) {
				continue;
			}

			Dictionary<String, Object> properties =
				configuration.getProperties();

			Object servicePid = properties.get("service.pid");

			if (servicePid.equals(dictionary.get("service.pid"))) {
				continue;
			}

			configuration.delete();
		}
	}

	private void _validateUniqueConfiguration(
			String path, Dictionary<String, Object> dictionary)
		throws Exception {

		String filterString = String.format(
			"(&(path=%s)(service.factoryPid=%s))", path,
			VulcanConfiguration.class.getName());

		_validateUniqueConfiguration(dictionary, filterString);

		if (dictionary.get("companyId") == null) {
			return;
		}

		filterString = String.format(
			"(&(path=%s)(service.factoryPid=%s)(%s=%d))", path,
			VulcanCompanyConfiguration.class.getName(),
			ExtendedObjectClassDefinition.Scope.COMPANY.getPropertyKey(),
			GetterUtil.getLong(dictionary.get("companyId")));

		_validateUniqueConfiguration(dictionary, filterString);
	}

	private ConfigurationAdmin _configurationAdmin;

}