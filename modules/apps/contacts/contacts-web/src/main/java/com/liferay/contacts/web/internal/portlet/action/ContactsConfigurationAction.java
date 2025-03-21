/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.portlet.action;

import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.portal.kernel.portlet.ConfigurationAction;
import com.liferay.portal.kernel.portlet.DefaultConfigurationAction;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER,
		"jakarta.portlet.name=" + ContactsPortletKeys.MEMBERS,
		"jakarta.portlet.name=" + ContactsPortletKeys.PROFILE
	},
	service = ConfigurationAction.class
)
@Deprecated
public class ContactsConfigurationAction extends DefaultConfigurationAction {
}