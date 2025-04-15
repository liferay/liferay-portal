/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.admin.web.internal.portlet;

import com.liferay.oauth.client.admin.web.internal.constants.OAuthClientAdminPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Arthur Chan
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-oauth-client-admin",
		"com.liferay.portlet.display-category=category.hidden",
		"com.liferay.portlet.header-portlet-css=/admin/css/main.css",
		"com.liferay.portlet.preferences-company-wide=true",
		"jakarta.portlet.display-name=OAuth Client Administration",
		"jakarta.portlet.init-param.portlet-title-based-navigation=true",
		"jakarta.portlet.init-param.template-path=/admin/",
		"jakarta.portlet.init-param.view-template=/admin/view.jsp",
		"jakarta.portlet.name=" + OAuthClientAdminPortletKeys.OAUTH_CLIENT_ADMIN,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class OAuthClientAdminPortlet extends MVCPortlet {
}