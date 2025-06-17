/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.fragment.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.fragment.web.internal.configuration.helper.FragmentServiceConfigurationHelper;
import com.liferay.fragment.web.internal.display.context.FragmentServiceConfigurationDisplayContext;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.Portal;

import jakarta.portlet.PortletResponse;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Reference;

/**
 * @author Eudaldo Alonso
 */
public abstract class BaseFragmentServiceConfigurationScreen
	implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "page-fragments";
	}

	@Override
	public String getKey() {
		return "fragments-service-" + getScope();
	}

	@Override
	public String getName(Locale locale) {
		return language.get(locale, "fragment-configuration-name");
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			httpServletRequest.setAttribute(
				FragmentServiceConfigurationDisplayContext.class.getName(),
				new FragmentServiceConfigurationDisplayContext(
					httpServletRequest,
					portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAKARTA_PORTLET_RESPONSE)),
					fragmentServiceConfigurationHelper, getScope()));

			RequestDispatcher requestDispatcher =
				servletContext.getRequestDispatcher(
					"/fragment_service_configuration.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render fragment_service_configuration.jsp",
				exception);
		}
	}

	@Reference
	protected FragmentServiceConfigurationHelper
		fragmentServiceConfigurationHelper;

	@Reference
	protected Language language;

	@Reference
	protected Portal portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.fragment.web)",
		unbind = "-"
	)
	protected ServletContext servletContext;

}