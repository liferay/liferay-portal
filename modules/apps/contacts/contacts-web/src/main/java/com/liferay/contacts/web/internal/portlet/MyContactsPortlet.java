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
		"jakarta.portlet.display-name=My Contacts",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.info.keywords=My Contacts",
		"jakarta.portlet.info.short-title=My Contacts",
		"jakarta.portlet.info.title=My Contacts",
		"jakarta.portlet.init-param.view-template=/my_contacts/view.jsp",
		"jakarta.portlet.name=" + ContactsPortletKeys.MY_CONTACTS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,guest,power-user,user"
	},
	service = Portlet.class
)
@Deprecated
public class MyContactsPortlet extends ContactsCenterPortlet {
}