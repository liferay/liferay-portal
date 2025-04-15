/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.configuration.admin.web.internal.util;

import com.liferay.configuration.admin.display.ConfigurationFormRenderer;
import com.liferay.configuration.admin.web.internal.constants.ConfigurationAdminWebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Map;

/**
 * @author Jorge Ferrer
 */
public class DefaultConfigurationFormRenderer
	implements ConfigurationFormRenderer {

	@Override
	public String getPid() {
		return "DEFAULT";
	}

	@Override
	public Map<String, Object> getRequestParameters(
		HttpServletRequest httpServletRequest) {

		return null;
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		String html = (String)httpServletRequest.getAttribute(
			ConfigurationAdminWebKeys.CONFIGURATION_MODEL_FORM_HTML);

		printWriter.print(html);
	}

}