/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.blueprint.admin.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.engine.SearchEngineInformation;
import com.liferay.portal.search.web.application.list.constants.SearchPanelCategoryKeys;
import com.liferay.search.experiences.constants.SXPPortletKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Objects;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Petteri Karttunen
 */
@Component(
	enabled = false,
	property = {
		"panel.app.order:Integer=100",
		"panel.category.key=" + SearchPanelCategoryKeys.CONTROL_PANEL_SEARCH
	},
	service = PanelApp.class
)
public class SXPBlueprintAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "blue-print";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		return List.of(
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "blueprints"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/sxp_blueprint_admin/view_sxp_blueprints"
				).setTabs1(
					"sxpBlueprints"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "blueprints")),
			new PanelAppNavigationItem(
				_language.get(LocaleUtil.ENGLISH, "elements"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/sxp_blueprint_admin/view_sxp_elements"
				).setTabs1(
					"sxpElements"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "elements")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SXPPortletKeys.SXP_BLUEPRINT_ADMIN;
	}

	@Override
	public boolean isShow(PermissionChecker permissionChecker, Group group)
		throws PortalException {

		if (Objects.equals(searchEngineInformation.getVendorString(), "Solr")) {
			return false;
		}

		return super.isShow(permissionChecker, group);
	}

	@Reference
	protected SearchEngineInformation searchEngineInformation;

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + SXPPortletKeys.SXP_BLUEPRINT_ADMIN + ")"
	)
	private Portlet _portlet;

}