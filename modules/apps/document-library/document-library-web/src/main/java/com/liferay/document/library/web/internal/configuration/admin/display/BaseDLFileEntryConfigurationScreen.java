/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.document.library.configuration.DLFileEntryConfigurationProvider;
import com.liferay.document.library.web.internal.display.context.DLFileEntryConfigurationDisplayContext;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Marco Galluzzi
 */
public abstract class BaseDLFileEntryConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "documents-and-media";
	}

	@Override
	public String getKey() {
		return "dl-file-entry-configuration-" + getScope();
	}

	@Override
	public String getName(Locale locale) {
		return language.get(locale, "dl-file-entry-configuration-name");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			ExtendedObjectClassDefinition.Scope scope =
				ExtendedObjectClassDefinition.Scope.getScope(getScope());

			httpServletRequest.setAttribute(
				DLFileEntryConfigurationDisplayContext.class.getName(),
				new DLFileEntryConfigurationDisplayContext(
					dlFileEntryConfigurationProvider, httpServletRequest,
					portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_RESPONSE)),
					scope, _getScopePK(httpServletRequest, scope)));

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(
					"/document_library_settings" +
						"/dl_file_entry_configuration.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render dl_file_entry_configuration.jsp", exception);
		}
	}

	@Reference
	protected DLFileEntryConfigurationProvider dlFileEntryConfigurationProvider;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.document.library.web)"
	)
	protected ServletContext servletContext;

	private long _getScopePK(
		HttpServletRequest httpServletRequest,
		ExtendedObjectClassDefinition.Scope scope) {

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		if (scope == ExtendedObjectClassDefinition.Scope.COMPANY) {
			return themeDisplay.getCompanyId();
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.GROUP) {
			return themeDisplay.getScopeGroupId();
		}
		else if (scope == ExtendedObjectClassDefinition.Scope.SYSTEM) {
			return 0L;
		}

		throw new IllegalArgumentException("Unsupported scope: " + scope);
	}

}