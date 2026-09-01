/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.oauth.client.constants.OAuthClientAdminPortletKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Arthur Chan
 */
@Component(
	property = {
		"panel.app.order:Integer=300",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_SECURITY
	},
	service = PanelApp.class
)
public class OAuthClientAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "shield-check";
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
				_language.get(LocaleUtil.ENGLISH, "oauth-clients"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/oauth_client_admin/view_oauth_client_entries"
				).setNavigation(
					"oauth-clients"
				).buildString(),
				_language.get(themeDisplay.getLocale(), "oauth-clients")),
			new PanelAppNavigationItem(
				_language.get(
					LocaleUtil.ENGLISH, "oauth-client-as-local-metadata"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/oauth_client_admin/view_oauth_client_as_local_metadata"
				).setNavigation(
					"oauth-client-as-local-metadata"
				).buildString(),
				_language.get(
					themeDisplay.getLocale(),
					"oauth-client-as-local-metadata")),
			new PanelAppNavigationItem(
				_language.get(
					LocaleUtil.ENGLISH, "oauth-client-pr-local-metadata"),
				PortletURLBuilder.create(
					getPortletURL(httpServletRequest)
				).setMVCRenderCommandName(
					"/oauth_client_admin/view_oauth_client_pr_local_metadata"
				).setNavigation(
					"oauth-client-pr-local-metadata"
				).buildString(),
				_language.get(
					themeDisplay.getLocale(),
					"oauth-client-pr-local-metadata")));
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN;
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN + ")"
	)
	private Portlet _portlet;

}