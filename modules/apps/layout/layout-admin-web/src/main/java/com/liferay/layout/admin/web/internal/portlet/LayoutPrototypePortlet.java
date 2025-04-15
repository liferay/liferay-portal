/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.admin.web.internal.portlet;

import com.liferay.layout.prototype.constants.LayoutPrototypePortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Pavel Savinov
 */
@Component(
	property = {
		"com.liferay.portlet.display-category=category.hidden",
		"jakarta.portlet.name=" + LayoutPrototypePortletKeys.LAYOUT_PROTOTYPE,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class LayoutPrototypePortlet extends MVCPortlet {
}