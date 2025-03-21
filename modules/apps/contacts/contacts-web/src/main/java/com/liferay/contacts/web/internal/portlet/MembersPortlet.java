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
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.css-class-wrapper=contacts-portlet",
		"com.liferay.portlet.display-category=category.social",
		"com.liferay.portlet.friendly-url-mapping=contacts",
		"com.liferay.portlet.header-portlet-css=/css/main.css",
		"com.liferay.portlet.header-portlet-javascript=/js/legacy/main.js",
		"com.liferay.portlet.icon=/icons/contacts_center.png",
		"jakarta.portlet.display-name=Members",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=Members",
		"jakarta.portlet.info.short-title=Members",
		"jakarta.portlet.info.title=Members",
		"jakarta.portlet.init-param.config-template=/configuration.jsp",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + ContactsPortletKeys.MEMBERS,
		"jakarta.portlet.portlet-mode=text/html;config",
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user"
	},
	service = Portlet.class
)
@Deprecated
public class MembersPortlet extends ContactsCenterPortlet {
}