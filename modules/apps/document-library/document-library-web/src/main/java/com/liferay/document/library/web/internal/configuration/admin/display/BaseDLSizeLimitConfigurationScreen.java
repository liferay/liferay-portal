/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.document.library.configuration.DLSizeLimitConfigurationProvider;
import com.liferay.document.library.web.internal.display.context.DLSizeLimitConfigurationDisplayContext;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Objects;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Adolfo Pérez
 */
public abstract class BaseDLSizeLimitConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "documents-and-media";
	}

	@Override
	public String getKey() {
		return "dl-size-limit-configuration-" + getScope();
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, "dl-size-limit-configuration-name");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			httpServletRequest.setAttribute(
				DLSizeLimitConfigurationDisplayContext.class.getName(),
				new DLSizeLimitConfigurationDisplayContext(
					dlSizeLimitConfigurationProvider, httpServletRequest,
					PortalUtil.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_RESPONSE)),
					getScope(), _getScopePK(httpServletRequest)));

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(
					"/document_library_settings/size_limit.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException("Unable to render size_limit.jsp", exception);
		}
	}

	@Reference
	protected DLSizeLimitConfigurationProvider dlSizeLimitConfigurationProvider;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.web)"
	)
	protected ServletContext servletContext;

	private long _getScopePK(HttpServletRequest httpServletRequest) {
		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (Objects.equals(
				getScope(),
				ExtendedObjectClassDefinition.Scope.COMPANY.getValue())) {

			return themeDisplay.getCompanyId();
		}
		else if (Objects.equals(
					getScope(),
					ExtendedObjectClassDefinition.Scope.GROUP.getValue())) {

			return themeDisplay.getScopeGroupId();
		}
		else if (Objects.equals(
					getScope(),
					ExtendedObjectClassDefinition.Scope.SYSTEM.getValue())) {

			return 0L;
		}

		throw new IllegalArgumentException("Unsupported scope: " + getScope());
	}

}