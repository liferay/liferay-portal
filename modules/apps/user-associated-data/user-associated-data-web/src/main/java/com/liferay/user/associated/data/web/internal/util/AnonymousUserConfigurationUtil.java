/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.user.associated.data.web.internal.util;

import com.liferay.user.associated.data.web.internal.configuration.AnonymousUserConfiguration;

import java.io.IOException;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Drew Brokke
 */
public class AnonymousUserConfigurationUtil {

	public static Configuration get(
			long companyId, ConfigurationAdmin configurationAdmin)
		throws InvalidSyntaxException, IOException {

		String filterString = String.format(
			"(&(service.factoryPid=%s)(companyId=%s))", _getFactoryPid(),
			companyId);

		return _get(configurationAdmin, filterString);
	}

	public static Configuration get(
			long companyId, ConfigurationAdmin configurationAdmin, long userId)
		throws InvalidSyntaxException, IOException {

		String filterString = String.format(
			"(&(service.factoryPid=%s)(companyId=%s)(userId=%s))",
			_getFactoryPid(), String.valueOf(companyId),
			String.valueOf(userId));

		return _get(configurationAdmin, filterString);
	}

	private static Configuration _get(
			ConfigurationAdmin configurationAdmin, String filterString)
		throws InvalidSyntaxException, IOException {

		Configuration[] configurations = configurationAdmin.listConfigurations(
			filterString);

		if (configurations == null) {
			return null;
		}

		return configurations[0];
	}

	private static String _getFactoryPid() {
		return AnonymousUserConfiguration.class.getName() + ".scoped";
	}

}