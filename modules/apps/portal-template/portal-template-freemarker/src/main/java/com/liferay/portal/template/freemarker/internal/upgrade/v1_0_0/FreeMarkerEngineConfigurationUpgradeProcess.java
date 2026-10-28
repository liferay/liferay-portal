/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.template.freemarker.internal.upgrade.v1_0_0;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.template.freemarker.configuration.FreeMarkerEngineConfiguration;

import java.io.IOException;

import java.util.Dictionary;

import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Tina Tian
 */
public class FreeMarkerEngineConfigurationUpgradeProcess
	extends UpgradeProcess {

	public FreeMarkerEngineConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin) {

		_configurationAdmin = configurationAdmin;
	}

	@Override
	protected void doUpgrade() throws InvalidSyntaxException, IOException {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			StringBundler.concat(
				"(", Constants.SERVICE_PID, "=",
				FreeMarkerEngineConfiguration.class.getName(), ")"));

		if (configurations == null) {
			return;
		}

		Configuration configuration = configurations[0];

		Dictionary<String, Object> dictionary = configuration.getProperties();

		if ((dictionary == null) ||
			(dictionary.get("allowedInstantiableClasses") != null)) {

			return;
		}

		Object allowedClasses = dictionary.get("allowedClasses");

		if (allowedClasses == null) {
			return;
		}

		dictionary.put("allowedInstantiableClasses", allowedClasses);

		configuration.updateIfDifferent(dictionary);
	}

	private final ConfigurationAdmin _configurationAdmin;

}