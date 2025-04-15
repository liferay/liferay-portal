/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.avalara.connector.web.internal.portlet;

import com.liferay.commerce.avalara.connector.configuration.CommerceAvalaraConnectorConfiguration;
import com.liferay.commerce.avalara.connector.web.internal.constants.CommerceAvalaraPortletKeys;
import com.liferay.commerce.avalara.connector.web.internal.display.context.CommerceAvalaraDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.module.configuration.ConfigurationException;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.portal.kernel.settings.CompanyServiceSettingsLocator;
import com.liferay.portal.kernel.settings.ParameterMapSettingsLocator;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.Portlet;
import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Katie Nesterovich
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.layout-cacheable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"com.liferay.portlet.private-request-attributes=false",
		"com.liferay.portlet.private-session-attributes=false",
		"com.liferay.portlet.render-weight=50",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Avalara",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + CommerceAvalaraPortletKeys.COMMERCE_AVALARA,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class CommerceAvalaraPortlet extends MVCPortlet {

	@Override
	public void render(
			RenderRequest renderRequest, RenderResponse renderResponse)
		throws IOException, PortletException {

		CommerceAvalaraDisplayContext commerceAvalaraDisplayContext =
			new CommerceAvalaraDisplayContext(
				_language, renderRequest, renderResponse);

		renderRequest.setAttribute(
			WebKeys.PORTLET_DISPLAY_CONTEXT, commerceAvalaraDisplayContext);

		ThemeDisplay themeDisplay = (ThemeDisplay)renderRequest.getAttribute(
			WebKeys.THEME_DISPLAY);

		try {
			CommerceAvalaraConnectorConfiguration
				commerceAvalaraConnectorConfiguration =
					_configurationProvider.getConfiguration(
						CommerceAvalaraConnectorConfiguration.class,
						new ParameterMapSettingsLocator(
							renderRequest.getParameterMap(),
							new CompanyServiceSettingsLocator(
								themeDisplay.getCompanyId(),
								CommerceAvalaraConnectorConfiguration.class.
									getName())));

			renderRequest.setAttribute(
				CommerceAvalaraConnectorConfiguration.class.getName(),
				commerceAvalaraConnectorConfiguration);
		}
		catch (ConfigurationException configurationException) {
			_log.error(
				"Unable to get Avalara configuration", configurationException);
		}

		super.render(renderRequest, renderResponse);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		CommerceAvalaraPortlet.class);

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference
	private Language _language;

}