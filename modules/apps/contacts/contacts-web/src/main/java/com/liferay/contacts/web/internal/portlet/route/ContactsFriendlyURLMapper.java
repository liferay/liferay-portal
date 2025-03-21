/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.contacts.web.internal.portlet.route;

import com.liferay.contacts.web.internal.constants.ContactsPortletKeys;
import com.liferay.portal.kernel.portlet.DefaultFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.FriendlyURLMapper;

import org.osgi.service.component.annotations.Component;

/**
 * @author Drew Brokke
 * @deprecated As of Cavanaugh (7.4.x)
 */
@Component(
	property = {
		"com.liferay.portlet.friendly-url-routes=META-INF/friendly-url-routes/routes.xml",
		"jakarta.portlet.name=" + ContactsPortletKeys.CONTACTS_CENTER,
		"jakarta.portlet.name=" + ContactsPortletKeys.MEMBERS
	},
	service = FriendlyURLMapper.class
)
@Deprecated
public class ContactsFriendlyURLMapper extends DefaultFriendlyURLMapper {

	@Override
	public String getMapping() {
		return _MAPPING;
	}

	@Override
	public String getPortletId() {
		return ContactsPortletKeys.MEMBERS;
	}

	private static final String _MAPPING = "contacts";

}