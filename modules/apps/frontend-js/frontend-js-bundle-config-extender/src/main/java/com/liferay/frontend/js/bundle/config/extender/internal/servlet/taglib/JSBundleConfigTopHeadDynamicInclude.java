/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.bundle.config.extender.internal.servlet.taglib;

import com.liferay.frontend.js.bundle.config.extender.internal.JSBundleConfigRegistryUtil;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Chema Balsas
 */
@Component(
	property = {
		"dynamic.include.key=/html/common/themes/top_js.jspf#resources",
		"service.ranking:Integer=" + Integer.MIN_VALUE
	},
	service = DynamicInclude.class
)
public class JSBundleConfigTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.write("<script");
		printWriter.write(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.write(" data-senna-track=\"temporary\" src=\"");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		printWriter.write(themeDisplay.getCDNDynamicResourcesHost());

		printWriter.write(_portal.getPathModule());
		printWriter.write("/js_bundle_config?t=");
		printWriter.write(
			String.valueOf(JSBundleConfigRegistryUtil.getLastModified()));
		printWriter.write("\" type=\"");
		printWriter.write(ContentTypes.TEXT_JAVASCRIPT);
		printWriter.write("\"></script>\n");
	}

	@Reference
	private Portal _portal;

}