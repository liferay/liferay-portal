/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.background.task.web.internal.portlet;

import com.liferay.portal.background.task.constants.BackgroundTaskPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Dante Wang
 */
@Component(
	property = {
		"com.liferay.portlet.add-default-resource=true",
		"com.liferay.portlet.display-category=category.hidden",
		"jakarta.portlet.display-name=Background Task",
		"jakarta.portlet.init-param.view-template=/view.jsp",
		"jakarta.portlet.name=" + BackgroundTaskPortletKeys.BACKGROUND_TASK,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator,power-user,user",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class BackgroundTaskPortlet extends MVCPortlet {
}