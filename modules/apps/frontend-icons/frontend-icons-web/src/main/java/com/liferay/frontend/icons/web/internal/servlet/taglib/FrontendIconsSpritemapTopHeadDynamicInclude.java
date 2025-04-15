/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.icons.web.internal.servlet.taglib;

import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.content.security.policy.ContentSecurityPolicyNonceProviderUtil;
import com.liferay.portal.kernel.servlet.taglib.BaseDynamicInclude;
import com.liferay.portal.kernel.servlet.taglib.DynamicInclude;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

import org.osgi.service.component.annotations.Component;

/**
 * @author Bryce Osterhaus
 */
@Component(service = DynamicInclude.class)
public class FrontendIconsSpritemapTopHeadDynamicInclude
	extends BaseDynamicInclude {

	@Override
	public void include(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String key)
		throws IOException {

		PrintWriter printWriter = httpServletResponse.getWriter();

		StringBundler sb = new StringBundler(8);

		sb.append("<script");
		sb.append(
			ContentSecurityPolicyNonceProviderUtil.getNonceAttribute(
				httpServletRequest));
		sb.append(" data-senna-track=\"temporary\">Liferay.Icons = Liferay.");
		sb.append("Icons || {};Liferay.Icons.controlPanelSpritemap = '");

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		sb.append(themeDisplay.getPathControlPanelSpritemap());

		sb.append("'; Liferay.Icons.spritemap = '");
		sb.append(themeDisplay.getPathThemeSpritemap());
		sb.append("';</script>\n");

		printWriter.write(sb.toString());
	}

	@Override
	public void register(DynamicIncludeRegistry dynamicIncludeRegistry) {
		dynamicIncludeRegistry.register(
			"/html/common/themes/top_js.jspf#resources");
	}

}