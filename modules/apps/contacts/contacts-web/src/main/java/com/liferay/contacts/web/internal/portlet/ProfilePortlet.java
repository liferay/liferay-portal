/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.portlet;

import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"com.liferay.portlet.css-class-wrapper=contacts-portlet",
		"com.liferay.portlet.display-category=category.social",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.icon=/icons/contacts_center.png",
		"com.liferay.portlet.instanceable=true",
		"jakarta.portlet.display-name=Profile",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=Profile",
		"jakarta.portlet.info.short-title=Profile",
		"jakarta.portlet.info.title=Profile",
		"jakarta.portlet.init-param.config-template=/configuration.jsp",
		"jakarta.portlet.init-param.view-template=/profile/view.jsp",
		"jakarta.portlet.name=" + ContactsPortletKeys.PROFILE,
		"jakarta.portlet.portlet-mode=text/html;config",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user"
	},
	service = Portlet.class
)
@Deprecated
public class ProfilePortlet extends ContactsCenterPortlet {
}