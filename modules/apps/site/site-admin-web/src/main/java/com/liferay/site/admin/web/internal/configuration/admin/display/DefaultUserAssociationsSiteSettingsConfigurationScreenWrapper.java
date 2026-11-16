/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.admin.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.display.ConfigurationScreenWrapper;
import com.liferay.item.selector.ItemSelector;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Role;
import com.liferay.portal.kernel.model.Team;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.TeamLocalService;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.site.admin.web.internal.display.context.DefaultUserAssociationsDisplayContext;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenContributor;
import com.liferay.site.settings.configuration.admin.display.SiteSettingsConfigurationScreenFactory;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.Serializable;

import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(service = ConfigurationScreen.class)
public class DefaultUserAssociationsSiteSettingsConfigurationScreenWrapper
	extends ConfigurationScreenWrapper {

	@Override
	protected ConfigurationScreen getConfigurationScreen() {
		return _siteSettingsConfigurationScreenFactory.create(
			new DefaultUserAssociationsSiteSettingsConfigurationScreenContributor());
	}

	@Reference
	private GroupLocalService _groupLocalService;

	@Reference
	private ItemSelector _itemSelector;

	@Reference
	private Language _language;

	@Reference
	private RoleLocalService _roleLocalService;

	@Reference(target = "(osgi.web.symbolicname=com.liferay.site.admin.web)")
	private ServletContext _servletContext;

	@Reference
	private SiteSettingsConfigurationScreenFactory
		_siteSettingsConfigurationScreenFactory;

	@Reference
	private TeamLocalService _teamLocalService;

	private class
		DefaultUserAssociationsSiteSettingsConfigurationScreenContributor
			implements SiteSettingsConfigurationScreenContributor {

		@Override
		public Dictionary<String, Object> exportProperties(
			Serializable scopePK) {

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			return HashMapDictionaryBuilder.<String, Object>put(
				"defaultSiteRoleExternalReferenceCodes",
				() -> {
					List<String> defaultSiteRoleExternalReferenceCodes =
						TransformUtil.transformToList(
							StringUtil.split(
								group.getTypeSettingsProperty(
									"defaultSiteRoleIds"),
								0L),
							roleId -> {
								Role role = _roleLocalService.fetchRole(roleId);

								if (role == null) {
									return null;
								}

								return role.getExternalReferenceCode();
							});

					if (defaultSiteRoleExternalReferenceCodes.isEmpty()) {
						return null;
					}

					return defaultSiteRoleExternalReferenceCodes;
				}
			).put(
				"defaultTeamNames",
				() -> {
					List<String> defaultTeamNames =
						TransformUtil.transformToList(
							StringUtil.split(
								group.getTypeSettingsProperty("defaultTeamIds"),
								0L),
							teamId -> {
								Team team = _teamLocalService.fetchTeam(teamId);

								if (team == null) {
									return null;
								}

								return team.getName();
							});

					if (defaultTeamNames.isEmpty()) {
						return null;
					}

					return defaultTeamNames;
				}
			).build();
		}

		@Override
		public String getCategoryKey() {
			return "users";
		}

		@Override
		public String getJspPath() {
			return "/site_settings/default_user_associations.jsp";
		}

		@Override
		public String getKey() {
			return "site-configuration-default-user-associations";
		}

		@Override
		public String getName(Locale locale) {
			return _language.get(locale, "default-user-associations");
		}

		@Override
		public String getSaveMVCActionCommandName() {
			return "/site_admin/edit_default_user_associations";
		}

		@Override
		public ServletContext getServletContext() {
			return _servletContext;
		}

		@Override
		public void importProperties(
				Dictionary<String, Object> properties, Serializable scopePK)
			throws Exception {

			List<String> defaultSiteRoleExternalReferenceCodes =
				(List<String>)properties.get(
					"defaultSiteRoleExternalReferenceCodes");
			List<String> defaultTeamNames = (List<String>)properties.get(
				"defaultTeamNames");

			if ((defaultSiteRoleExternalReferenceCodes == null) &&
				(defaultTeamNames == null)) {

				return;
			}

			Group group = _groupLocalService.fetchGroup((long)scopePK);

			UnicodeProperties typeSettingsUnicodeProperties =
				group.getTypeSettingsProperties();

			if (defaultSiteRoleExternalReferenceCodes != null) {
				typeSettingsUnicodeProperties.put(
					"defaultSiteRoleIds",
					StringUtil.merge(
						TransformUtil.unsafeTransformToLongArray(
							defaultSiteRoleExternalReferenceCodes,
							defaultSiteRoleExternalReferenceCode -> {
								Role role =
									_roleLocalService.
										getRoleByExternalReferenceCode(
											defaultSiteRoleExternalReferenceCode,
											group.getCompanyId());

								return role.getRoleId();
							})));
			}

			if (defaultTeamNames != null) {
				typeSettingsUnicodeProperties.put(
					"defaultTeamIds",
					StringUtil.merge(
						TransformUtil.unsafeTransformToLongArray(
							defaultTeamNames,
							defaultTeamName -> {
								Team team = _teamLocalService.getTeam(
									group.getGroupId(), defaultTeamName);

								return team.getTeamId();
							})));
			}

			_groupLocalService.updateGroup(
				group.getGroupId(), typeSettingsUnicodeProperties.toString());
		}

		@Override
		public boolean isVisible(Group group) {
			return !group.isCompany();
		}

		@Override
		public void setAttributes(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

			SiteSettingsConfigurationScreenContributor.super.setAttributes(
				httpServletRequest, httpServletResponse);

			httpServletRequest.setAttribute(
				DefaultUserAssociationsDisplayContext.class.getName(),
				new DefaultUserAssociationsDisplayContext(
					httpServletRequest, _itemSelector));
		}

	}

}