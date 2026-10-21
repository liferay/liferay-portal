/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal.osgi.commands;

import com.liferay.osgi.util.osgi.commands.OSGiCommands;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge García Jiménez
 */
@Component(
	property = {
		"osgi.command.function=resetCompanyConfiguration",
		"osgi.command.function=resetGroupConfiguration",
		"osgi.command.function=resetSystemConfiguration",
		"osgi.command.scope=csp"
	},
	service = OSGiCommands.class
)
public class CSPOSGiCommands implements OSGiCommands {

	public void resetCompanyConfiguration(long companyId)
		throws ConfigurationException {

		_configurationProvider.deleteCompanyConfiguration(
			ContentSecurityPolicyConfiguration.class, companyId);
	}

	public void resetGroupConfiguration(long groupId)
		throws ConfigurationException {

		Group group = _groupLocalService.fetchGroup(groupId);

		_configurationProvider.deleteGroupConfiguration(
			ContentSecurityPolicyConfiguration.class, group.getCompanyId(),
			groupId);
	}

	public void resetSystemConfiguration() throws ConfigurationException {
		_configurationProvider.deleteSystemConfiguration(
			ContentSecurityPolicyConfiguration.class);
	}

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

}