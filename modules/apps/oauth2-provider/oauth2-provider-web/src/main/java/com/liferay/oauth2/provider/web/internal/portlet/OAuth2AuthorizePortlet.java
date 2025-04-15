/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth2.provider.web.internal.portlet;

import com.liferay.oauth2.provider.web.internal.constants.OAuth2ProviderPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Tomas Polesovsky
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.application-type=full-page-application",
		"com.liferay.portlet.css-class-wrapper=portlet-oauth2-provider-authorize",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.preferences-unique-per-layout=false",
		"jakarta.portlet.display-name=OAuth2 Authorize Portlet",
		"jakarta.portlet.init-param.portlet-title-based-navigation=true",
		"jakarta.portlet.init-param.template-path=/authorize/",
		"jakarta.portlet.init-param.view-template=/authorize/authorize.jsp",
		"jakarta.portlet.name=" + OAuth2ProviderPortletKeys.OAUTH2_AUTHORIZE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0",
		"portlet.add.default.resource.check.whitelist=" + OAuth2ProviderPortletKeys.OAUTH2_AUTHORIZE
	},
	service = Portlet.class
)
public class OAuth2AuthorizePortlet extends MVCPortlet {
}