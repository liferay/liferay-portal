/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.web.internal.power.tools.portlet;

import com.liferay.portal.kernel.portlet.bridges.mvc.MVCPortlet;
import com.liferay.search.experiences.constants.SXPPortletKeys;

import jakarta.portlet.Portlet;

import org.osgi.service.component.annotations.Component;

/**
 * @author Petteri Karttunen
 * @author André de Oliveira
 * @author Brian Wing Shun Chan
 */
@Component(
	enabled = false,
	property = {
		"com.liferay.portlet.css-class-wrapper=portlet-sxp-power-toys",
		"com.liferay.portlet.display-category=category.hidden",
		"jakarta.portlet.init-param.template-path=/META-INF/resources/",
		"jakarta.portlet.init-param.view-template=/sxp_blueprint_admin/view.jsp",
		"jakarta.portlet.name=" + SXPPortletKeys.SXP_POWER_TOOLS,
		"jakarta.portlet.resource-bundle=content.Language",
		"jakarta.portlet.security-role-ref=administrator",
		"jakarta.portlet.version=4.0"
	},
	service = Portlet.class
)
public class SXPPowerToolsPortlet extends MVCPortlet {

	// TODO Add feature flag

}