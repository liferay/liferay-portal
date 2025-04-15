/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.flags.web.internal.portlet.action;

import com.liferay.flags.web.internal.constants.FlagsPortletKeys;
import com.liferay.portal.kernel.portlet.bridges.mvc.BaseMVCResourceCommand;
import com.liferay.portal.kernel.portlet.bridges.mvc.MVCResourceCommand;

import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"jakarta.portlet.name=" + FlagsPortletKeys.FLAGS,
		"mvc.command.name=/flags/get_captcha"
	},
	service = MVCResourceCommand.class
)
public class GetCaptchaMVCResourceCommand extends BaseMVCResourceCommand {

	@Override
	protected void doServeResource(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws Exception {

		include(resourceRequest, resourceResponse, "/get_captcha.jsp");
	}

}