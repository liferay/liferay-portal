/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.web.internal.application.list;

import com.liferay.application.list.BasePanelApp;
import com.liferay.application.list.PanelApp;
import com.liferay.application.list.PanelAppNavigationItem;
import com.liferay.application.list.constants.PanelCategoryKeys;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.saml.constants.SamlPortletKeys;
import com.liferay.saml.runtime.configuration.SamlProviderConfigurationHelper;

import jakarta.servlet.http.HttpServletRequest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Carlos Sierra Andrés
 */
@Component(
	property = {
		"panel.app.order:Integer=600",
		"panel.category.key=" + PanelCategoryKeys.CONTROL_PANEL_SECURITY
	},
	service = PanelApp.class
)
public class SamlAdminPanelApp extends BasePanelApp {

	@Override
	public String getIcon() {
		return "lock";
	}

	@Override
	public List<PanelAppNavigationItem> getPanelAppNavigationItems(
			HttpServletRequest httpServletRequest)
		throws PortalException {

		List<PanelAppNavigationItem> panelAppNavigationItems =
			new ArrayList<>();

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		panelAppNavigationItems.add(
			_getPanelAppNavigationItem(
				httpServletRequest, "general", themeDisplay));

		if (_samlProviderConfigurationHelper.isRoleIb()) {
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "identity-provider", themeDisplay));
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "service-provider-connections",
					themeDisplay));
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "service-provider", themeDisplay));
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "identity-provider-connections",
					themeDisplay));
		}
		else if (_samlProviderConfigurationHelper.isRoleIdp()) {
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "identity-provider", themeDisplay));
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "service-provider-connections",
					themeDisplay));
		}
		else if (_samlProviderConfigurationHelper.isRoleSp()) {
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "service-provider", themeDisplay));
			panelAppNavigationItems.add(
				_getPanelAppNavigationItem(
					httpServletRequest, "identity-provider-connections",
					themeDisplay));
		}

		if (panelAppNavigationItems.size() < 2) {
			return Collections.emptyList();
		}

		return panelAppNavigationItems;
	}

	@Override
	public Portlet getPortlet() {
		return _portlet;
	}

	@Override
	public String getPortletId() {
		return SamlPortletKeys.SAML_ADMIN;
	}

	private PanelAppNavigationItem _getPanelAppNavigationItem(
			HttpServletRequest httpServletRequest, String tabs1,
			ThemeDisplay themeDisplay)
		throws PortalException {

		return new PanelAppNavigationItem(
			_language.get(LocaleUtil.ENGLISH, tabs1),
			PortletURLBuilder.create(
				getPortletURL(httpServletRequest)
			).setTabs1(
				tabs1
			).buildString(),
			_language.get(themeDisplay.getLocale(), tabs1));
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(jakarta.portlet.name=" + SamlPortletKeys.SAML_ADMIN + ")"
	)
	private Portlet _portlet;

	@Reference
	private SamlProviderConfigurationHelper _samlProviderConfigurationHelper;

}