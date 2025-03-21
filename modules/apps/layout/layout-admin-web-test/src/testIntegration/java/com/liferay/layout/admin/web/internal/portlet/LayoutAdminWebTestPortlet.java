/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet;

import com.liferay.layout.admin.web.internal.portlet.constants.LayoutAdminWebPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Eudaldo Alonso
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.instanceable=true",
		"com.liferay.portlet.preferences-owned-by-group=true",
		"com.liferay.portlet.scopeable=true",
		"jakarta.portlet.display-name=Test",
		"jakarta.portlet.expiration-cache=0",
		"jakarta.portlet.name=" + LayoutAdminWebPortletKeys.LAYOUT_ADMIN_WEB_TEST_PORTLET,
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class LayoutAdminWebTestPortlet extends MVCPortlet {
}