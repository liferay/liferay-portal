/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.portlet.action;

import com.liferay.configuration.admin.constants.ConfigurationAdminPortletKeys;
import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;
import com.liferay.configuration.admin.web.internal.display.ConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.ConfigurationScreenConfigurationEntry;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContext;
import com.liferay.configuration.admin.web.internal.display.context.ConfigurationScopeDisplayContextFactory;
import com.liferay.configuration.admin.web.internal.util.ConfigurationEntryRetriever;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCRenderCommand;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ParamUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Jorge Ferrer
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.INSTANCE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SITE_SETTINGS,
		"jakarta.portlet.name=" + ConfigurationAdminPortletKeys.SYSTEM_SETTINGS,
		"mvc.command.name=/configuration_admin/view_configuration_screen",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1000)
	},
	service = MVCRenderCommand.class
)
public class ViewConfigurationScreenMVCRenderCommand
	implements MVCRenderCommand {

	@Override
	public String render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws PortletException {

		String configurationScreenKey = ParamUtil.getString(
			renderRequest, "configurationScreenKey");

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		ConfigurationScreen configurationScreen =
			_configurationEntryRetriever.getConfigurationScreen(
				configurationScreenKey);

		if (!configurationScreen.isVisible()) {
			throw new PortletException(
				StringBundler.concat(
					"The ", configurationScreen.getScope(), " configuration \"",
					configurationScreen.getName(themeDisplay.getLocale()),
					"\" is not accessible"));
		}

		ConfigurationScopeDisplayContext configurationScopeDisplayContext =
			ConfigurationScopeDisplayContextFactory.create(renderRequest);

		renderRequest.setAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_CATEGORY_MENU_DISPLAY,
			_configurationEntryRetriever.getConfigurationCategoryMenuDisplay(
				configurationScreen.getCategoryKey(),
				themeDisplay.getLanguageId(),
				configurationScopeDisplayContext.getScope(),
				configurationScopeDisplayContext.getScopePK()));

		renderRequest.setAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_SCREEN,
			configurationScreen);

		ConfigurationEntry configurationEntry =
			new ConfigurationScreenConfigurationEntry(
				configurationScreen, _portal.getLocale(renderRequest));

		renderRequest.setAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_ENTRY, configurationEntry);

		return "/view_configuration_screen.jsp";
	}

	@Reference
	private ConfigurationEntryRetriever _configurationEntryRetriever;

	@Reference
	private Portal _portal;

}