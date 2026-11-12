/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.internal.configuration.manager;

import com.liferay.configuration.admin.util.ConfigurationFilterStringUtil;
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

import java.util.Dictionary;

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
		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				role.getCompanyId(), null,
				MenuAccessConfiguration.class.getName(), null));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			String[] accessToControlMenuRoleExternalReferenceCodes =
				(String[])properties.get(
					"accessToControlMenuRoleExternalReferenceCodes");

			if (!ArrayUtil.contains(
					accessToControlMenuRoleExternalReferenceCodes,
					role.getExternalReferenceCode())) {

				continue;
			}

			properties.put(
				"accessToControlMenuRoleExternalReferenceCodes",
				ArrayUtil.remove(
					accessToControlMenuRoleExternalReferenceCodes,
					role.getExternalReferenceCode()));

			configuration.update(properties);
		}
	}

	@Override
	public String[] getAccessToControlMenuRoleExternalReferenceCodes(
			long groupId)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		MenuAccessConfiguration menuAccessConfiguration =
			_configurationProvider.getGroupConfiguration(
				MenuAccessConfiguration.class, group.getCompanyId(), groupId);

		return menuAccessConfiguration.
			accessToControlMenuRoleExternalReferenceCodes();
	}

	@Override
	public boolean isShowControlMenuByRole(long groupId) throws Exception {
		Group group = _groupLocalService.getGroup(groupId);

		MenuAccessConfiguration menuAccessConfiguration =
			_configurationProvider.getGroupConfiguration(
				MenuAccessConfiguration.class, group.getCompanyId(), groupId);

		return menuAccessConfiguration.showControlMenuByRole();
	}

	@Override
	public void updateMenuAccessConfiguration(
			long groupId,
			String[] accessToControlMenuRoleExternalReferenceCodes,
			boolean showControlMenuByRole)
		throws Exception {

		Group group = _groupLocalService.getGroup(groupId);

		if (accessToControlMenuRoleExternalReferenceCodes == null) {
			Role administratorRole = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.ADMINISTRATOR);
			Role siteAdministratorRole = _roleLocalService.getRole(
				group.getCompanyId(), RoleConstants.SITE_ADMINISTRATOR);

			accessToControlMenuRoleExternalReferenceCodes = new String[] {
				administratorRole.getExternalReferenceCode(),
				siteAdministratorRole.getExternalReferenceCode()
			};
		}

		_configurationProvider.saveGroupConfiguration(
			MenuAccessConfiguration.class, group.getCompanyId(), groupId,
			HashMapDictionaryBuilder.<String, Object>put(
				"accessToControlMenuRoleExternalReferenceCodes",
				accessToControlMenuRoleExternalReferenceCodes
			).put(
				"showControlMenuByRole", showControlMenuByRole
			).build());
	}

	@Override
	public void updateRoleAccessToControlMenu(Role originalRole, Role role)
		throws Exception {

		Configuration[] configurations = _configurationAdmin.listConfigurations(
			ConfigurationFilterStringUtil.getGroupScopedFilterString(
				role.getCompanyId(), null,
				MenuAccessConfiguration.class.getName(), null));

		if (configurations == null) {
			return;
		}

		for (Configuration configuration : configurations) {
			Dictionary<String, Object> properties =
				configuration.getProperties();

			String[] accessToControlMenuRoleExternalReferenceCodes =
				(String[])properties.get(
					"accessToControlMenuRoleExternalReferenceCodes");

			if (!ArrayUtil.contains(
					accessToControlMenuRoleExternalReferenceCodes,
					originalRole.getExternalReferenceCode())) {

				continue;
			}

			ArrayUtil.replace(
				accessToControlMenuRoleExternalReferenceCodes,
				originalRole.getExternalReferenceCode(),
				role.getExternalReferenceCode());

			properties.put(
				"accessToControlMenuRoleExternalReferenceCodes",
				accessToControlMenuRoleExternalReferenceCodes);

			configuration.update(properties);
		}
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