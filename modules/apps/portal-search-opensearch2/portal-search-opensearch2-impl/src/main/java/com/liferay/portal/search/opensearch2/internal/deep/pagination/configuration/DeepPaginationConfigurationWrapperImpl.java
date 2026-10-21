/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.opensearch2.internal.deep.pagination.configuration;

import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.search.opensearch2.configuration.DeepPaginationConfiguration;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Gustavo Lima
 */
@Component(
	configurationPid = "com.liferay.portal.search.opensearch2.configuration.DeepPaginationConfiguration",
	service = DeepPaginationConfigurationWrapper.class
)
public class DeepPaginationConfigurationWrapperImpl
	implements DeepPaginationConfigurationWrapper {

	@Override
	public DeepPaginationConfiguration getDeepPaginationConfiguration(
		long companyId) {

		try {
			DeepPaginationConfiguration deepPaginationConfiguration =
				_configurationProvider.getSystemConfiguration(
					DeepPaginationConfiguration.class);

			if (!deepPaginationConfiguration.enableDeepPagination()) {
				return _configurationProvider.getCompanyConfiguration(
					DeepPaginationConfiguration.class, companyId);
			}

			return deepPaginationConfiguration;
		}
		catch (ConfigurationException configurationException) {
			return ReflectionUtil.throwException(configurationException);
		}
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

}