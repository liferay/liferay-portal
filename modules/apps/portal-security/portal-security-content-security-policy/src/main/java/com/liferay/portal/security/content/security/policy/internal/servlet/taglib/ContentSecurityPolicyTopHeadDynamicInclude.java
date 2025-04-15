/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.content.security.policy.internal.servlet.taglib;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProvider;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfiguration;
import com.liferay.portal.security.content.security.policy.internal.configuration.ContentSecurityPolicyConfigurationUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(service = DynamicInclude.class)
public class ContentSecurityPolicyTopHeadDynamicInclude
	implements DynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print("<script data-senna-track=\"permanent\"");

		String nonce = _contentSecurityPolicyNonceProvider.getNonce(
			httpServletRequest);

		if (Validator.isNotNull(nonce)) {
			printWriter.print(" nonce=\"");
			printWriter.print(nonce);
			printWriter.print(StringPool.QUOTE);
		}

		printWriter.print(" type=\"text/javascript\">window.Liferay.CSP = ");
		printWriter.print("{nonce: '");

		ContentSecurityPolicyConfiguration contentSecurityPolicyConfiguration =
			ContentSecurityPolicyConfigurationUtil.
				getContentSecurityPolicyConfiguration(httpServletRequest);

		if ((contentSecurityPolicyConfiguration != null) &&
			contentSecurityPolicyConfiguration.enabled()) {

			String policy = contentSecurityPolicyConfiguration.policy();

			if (!policy.contains("'strict-dynamic'")) {
				printWriter.print(nonce);
			}
		}

		printWriter.println("'};</script>");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

	@Reference
	private ContentSecurityPolicyNonceProvider
		_contentSecurityPolicyNonceProvider;

}