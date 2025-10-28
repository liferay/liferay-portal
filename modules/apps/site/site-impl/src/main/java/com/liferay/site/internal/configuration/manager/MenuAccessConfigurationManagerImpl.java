/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.configuration.manager;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.role.RoleConstants;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.site.configuration.MenuAccessConfiguration;
import com.liferay.site.configuration.manager.MenuAccessConfigurationManager;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Mikel Lorza
 */
@Component(service = MenuAccessConfigurationManager.class)
public class MenuAccessConfigurationManagerImpl
	implements MenuAccessConfigurationManager {

	@Override
	public void deleteRoleAccessToControlMenu(Role role) throws Exception {
		String filterString = StringBundler.concat(
			"(service.factoryPid=", MenuAccessConfiguration.class.getName(),
			".scoped)");

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			filterString);

		if (configurations == null) {
			return;
		}

		String roleId = String.valueOf(role.getRoleId());

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			String[] accessToControlMenuRoleIds = (String[])properties.get(
				"accessToControlMenuRoleIds");

			if (ArrayUtil.contains(accessToControlMenuRoleIds, roleId)) {
				accessToControlMenuRoleIds = ArrayUtil.remove(
					accessToControlMenuRoleIds, roleId);

				properties.put(
					"accessToControlMenuRoleIds", accessToControlMenuRoleIds);

				configuration.update(properties);
			}
		}
	}

	@Override
	public String[] getAccessToControlMenuRoleIds(long groupId)
		throws Exception {

		MenuAccessConfiguration menuAccessConfiguration =
			_configurationProvider.getGroupConfiguration(
				MenuAccessConfiguration.class, groupId);

		return menuAccessConfiguration.accessToControlMenuRoleIds();
	}

	@Override
	public boolean isShowControlMenuByRole(long groupId) throws Exception {
		MenuAccessConfiguration menuAccessConfiguration =
			_configurationProvider.getGroupConfiguration(
				MenuAccessConfiguration.class, groupId);

		return menuAccessConfiguration.showControlMenuByRole();
	}

	@Override
	public void updateMenuAccessConfiguration(
			long groupId, String[] accessToControlMenuRoleIds,
			boolean showControlMenuByRole)
		throws Exception {

		if (accessToControlMenuRoleIds == null) {
			List<String> accessToControlMenuRoleIdsList = new ArrayList<>();

			Group group = _groupLocalService.getGroup(groupId);

			Role administratorRole = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.ADMINISTRATOR);

			accessToControlMenuRoleIdsList.add(
				String.valueOf(administratorRole.getRoleId()));

			Role siteAdministratorRole = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

			accessToControlMenuRoleIdsList.add(
				String.valueOf(siteAdministratorRole.getRoleId()));

			accessToControlMenuRoleIds = ArrayUtil.toStringArray(
				accessToControlMenuRoleIdsList);
		}

		_configurationProvider.saveGroupConfiguration(
			MenuAccessConfiguration.class, groupId,
			HashMapDictionaryBuilder.<String, Object>put(
				"accessToControlMenuRoleIds", accessToControlMenuRoleIds
			).put(
				"showControlMenuByRole", showControlMenuByRole
			).build());
	}

	@Reference
	private ConfigurationAdmin _configurationAdmin;

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private RoleLocalService _roleLocalService;

}