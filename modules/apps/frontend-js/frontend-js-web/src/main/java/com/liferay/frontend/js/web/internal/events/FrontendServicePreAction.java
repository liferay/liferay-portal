/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.events;

import com.liferay.portal.kernel.events.Action;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.frontend.hashed.files.HashedFilesRegistry;
import com.liferay.portal.kernel.model.Theme;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.AbsolutePortalURLBuilderFactory;
import com.liferay.portal.url.builder.WebContextScriptAbsolutePortalURLBuilder;
import com.liferay.portal.url.builder.WebContextStylesheetAbsolutePortalURLBuilder;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Iván Zaera Avellón
 */
@Component(
	property = "key=servlet.service.events.pre", service = LifecycleAction.class
)
public class FrontendServicePreAction extends Action {

	@Override
	public void run(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws ActionException {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (themeDisplay == null) {
			return;
		}

		AbsolutePortalURLBuilder absolutePortalURLBuilder =
			_absolutePortalURLBuilderFactory.getAbsolutePortalURLBuilder(
				httpServletRequest);

		Theme theme = themeDisplay.getTheme();
		boolean rtl = _portal.isRightToLeft(httpServletRequest);

		WebContextStylesheetAbsolutePortalURLBuilder
			webContextStylesheetAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forWebContextStylesheet(
					theme.getServletContextName(),
					rtl ? "/css/clay_rtl.css" : "/css/clay.css");

		themeDisplay.setDefaultClayCSSURL(
			webContextStylesheetAbsolutePortalURLBuilder.build());

		webContextStylesheetAbsolutePortalURLBuilder =
			absolutePortalURLBuilder.forWebContextStylesheet(
				theme.getServletContextName(),
				rtl ? "/css/main_rtl.css" : "/css/main.css");

		themeDisplay.setDefaultMainCSSURL(
			webContextStylesheetAbsolutePortalURLBuilder.build());

		WebContextScriptAbsolutePortalURLBuilder
			webContextScriptAbsolutePortalURLBuilder =
				absolutePortalURLBuilder.forWebContextScript(
					theme.getServletContextName(), "/js/main.js");

		themeDisplay.setDefaultMainJSURL(
			webContextScriptAbsolutePortalURLBuilder.build());
	}

	@Reference
	private AbsolutePortalURLBuilderFactory _absolutePortalURLBuilderFactory;

	@Reference
	private HashedFilesRegistry _hashedFilesRegistry;

	@Reference
	private Portal _portal;

}