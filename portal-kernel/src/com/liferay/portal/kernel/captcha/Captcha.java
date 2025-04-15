/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.captcha;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 */
@ProviderType
public interface Captcha {

	public void check(HttpServletRequest httpServletRequest)
		throws CaptchaException;

	public void check(PortletRequest portletRequest) throws CaptchaException;

	public void enforceCaptcha(HttpServletRequest httpServletRequest);

	public void enforceCaptcha(PortletRequest portletRequest);

	public String getName();

	public String getTaglibPath();

	public boolean isEnabled(HttpServletRequest httpServletRequest);

	public boolean isEnabled(PortletRequest portletRequest);

	public void serveImage(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException;

	public String serveImage(OutputStream outputStream) throws IOException;

	public void serveImage(
			ResourceRequest resourceRequest, ResourceResponse resourceResponse)
		throws IOException;

}