/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.upgrade.v3_0_0;

import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.upgrade.UpgradeProcess;
import com.liferay.portal.kernel.util.GetterUtil;

import java.util.Dictionary;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

/**
 * @author Thiago Buarque
 */
public class MenuAccessConfigurationUpgradeProcess extends UpgradeProcess {

	public MenuAccessConfigurationUpgradeProcess(
		ConfigurationAdmin configurationAdmin,
		RoleLocalService roleLocalService) {

		_configurationAdmin = configurationAdmin;
		_roleLocalService = roleLocalService;
	}

	@Override
	protected void doUpgrade() throws Exception {
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			"(service.factoryPid=com.liferay.site.configuration." +
				"MenuAccessConfiguration.scoped)");

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			String[] accessToControlMenuRoleIds = (String[])properties.remove(
				"accessToControlMenuRoleIds");

			if (accessToControlMenuRoleIds == null) {
				continue;
			}

			properties.put(
				"accessToControlMenuRoleExternalReferenceCodes",
				TransformUtil.transform(
					accessToControlMenuRoleIds,
					accessToControlMenuRoleId -> {
						Role role = _roleLocalService.fetchRole(
							GetterUtil.getLong(accessToControlMenuRoleId));

						if (role == null) {
							return null;
						}

						return role.getExternalReferenceCode();
					},
					String.class));

			configuration.update(properties);
		}
	}

	private final ConfigurationAdmin _configurationAdmin;
	private final RoleLocalService _roleLocalService;

}