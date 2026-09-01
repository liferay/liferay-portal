/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.roles.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.roles.admin.constants.RolesAdminPortletKeys;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributor;
import com.liferay.roles.admin.role.type.contributor.RoleTypeContributorShowFilterRegistryUtil;
import com.liferay.roles.admin.role.type.contributor.provider.RoleTypeContributorProvider;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"panel.app.order:Integer=500",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_USERS
	},
	service = PanelApp.class
)
public class RolesPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "organizations";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		List<RoleTypeContributor> roleTypeContributors = ListUtil.filter(
			_roleTypeContributorProvider.getRoleTypeContributors(),
			roleTypeContributor -> {
				if (roleTypeContributor == null) {
					return false;
				}

				return RoleTypeContributorShowFilterRegistryUtil.isShow(
					themeDisplay.getPermissionChecker(), roleTypeContributor);
			});

		return TransformUtil.unsafeTransform(
			roleTypeContributors,
			roleTypeContributor -> new PanelAppNavigationItem(
				_language.get(
					LocaleUtil.ENGLISH,
					roleTypeContributor.getTabTitle(LocaleUtil.ENGLISH)),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setParameter(
					"roleType", roleTypeContributor.getType()
				).buildString(),
				_language.get(
					themeDisplay.getLocale(),
					roleTypeContributor.getTabTitle(
						themeDisplay.getLocale()))));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return RolesAdminPortletKeys.ROLES_ADMIN;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + RolesAdminPortletKeys.ROLES_ADMIN + ")"
	)
	private Portlet _portlet;

	@Reference
	private RoleTypeContributorProvider _roleTypeContributorProvider;

}