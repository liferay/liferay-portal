/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.url.builder.internal.servlet.taglib;

import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.WebContextScriptAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = {
		"dynamic.include.key=/html/common/themes/top_js.jspf#resources",
		"service.ranking:Integer=" + (Integer.MAX_VALUE - 1)
	},
	service = DynamicInclude.class
)
public class PortalURLBuilderImplDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print("<script");
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));

		try {
			if (Validator.isNotNull(
					PortalUtil.getCDNHost(httpServletRequest))) {

				printWriter.print(" crossorigin=\"\"");
			}
		}
		catch (Exception exception) {
			_log.error(exception);
		}

		printWriter.print(" data-senna-track=\"permanent\" src=\"");

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		WebContextScriptAbsolutePortalURLBuilder
			webContextScriptAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forWebContextScript(
					"portal-url-builder-impl", "/Liferay.js");

		printWriter.print(webContextScriptAbsolutePortalURLBuilder.build());

		printWriter.print("\" type=\"text/javascript\"></script>");
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortalURLBuilderImplDynamicInclude.class);

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

}