/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.captcha.taglib.internal.struts;

import com.liferay.captcha.util.CaptchaUtil;
import com.liferay.portal.kernel.struts.StrutsAction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = "path=/portal/captcha/get_image", service = StrutsAction.class
)
public class GetCaptchaImageStrutsAction implements StrutsAction {

	@Override
	public String execute(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		CaptchaUtil.serveImage(httpServletRequest, httpServletResponse);

		return null;
	}

}