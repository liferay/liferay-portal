/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.utility.page.login.internal;

import com.liferay.layout.utility.page.kernel.LayoutUtilityPageEntryViewRenderer;
import com.liferay.layout.utility.page.kernel.constants.LayoutUtilityPageEntryConstants;
import com.liferay.portal.kernel.feature.flag.FeatureFlagManagerUtil;
import com.liferay.portal.kernel.language.Language;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Olivér Kecskeméty
 */
@Component(
	property = "utility.page.type=" + LayoutUtilityPageEntryConstants.TYPE_LOGIN,
	service = LayoutUtilityPageEntryViewRenderer.class
)
public class LoginLayoutUtilityPageEntryViewRenderer
	implements LayoutUtilityPageEntryViewRenderer {

	@Override
	public String getLabel(Locale locale) {
		return _language.get(locale, "sign-in");
	}

	@Override
	public String getType() {
		return LayoutUtilityPageEntryConstants.TYPE_LOGIN;
	}

	@Override
	public boolean isEnabled() {
		return FeatureFlagManagerUtil.isEnabled("LPD-6378");
	}

	@Override
	public void renderHTML(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/login.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.layout.utility.page.login)"
	)
	private ServletContext _servletContext;

}