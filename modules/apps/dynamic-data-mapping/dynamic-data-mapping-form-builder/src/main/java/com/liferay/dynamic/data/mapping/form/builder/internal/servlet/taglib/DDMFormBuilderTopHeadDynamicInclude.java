/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.form.builder.internal.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Rafael Praxedes
 */
@Component(service = DynamicInclude.class)
public class DDMFormBuilderTopHeadDynamicInclude extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		PrintWriter printWriter = httpServletResponse.getWriter();

		printWriter.print("<link href=\"");
		printWriter.print(
			_portal.getStaticResourceURL(
				httpServletRequest,
				StringBundler.concat(
					themeDisplay.getCDNBaseURL(), _postfix, "/css/main.css")));
		printWriter.print(StringPool.QUOTE);
		printWriter.print(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		printWriter.println(" rel=\"stylesheet\" type = \"text/css\" />");
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"com.liferay.data.engine.taglib#/data_layout_builder/start.jsp" +
				"#pre");
		dynamicIncludeRegistry.register(
			"com.liferay.dynamic.data.mapping.form.web#" +
				"EditElementSetMVCRenderCommand#render");
		dynamicIncludeRegistry.register(
			"com.liferay.dynamic.data.mapping.form.web#" +
				"EditFormInstanceMVCRenderCommand#render");
	}

	@Activate
	protected void activate() {
		_postfix = _portal.getPathProxy();

		if (_postfix.isEmpty()) {
			_postfix = _servletContext.getContextPath();
		}
		else {
			_postfix = _postfix.concat(_servletContext.getContextPath());
		}
	}

	@Reference
	private Portal _portal;

	private String _postfix;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.dynamic.data.mapping.form.builder)"
	)
	private ServletContext _servletContext;

}