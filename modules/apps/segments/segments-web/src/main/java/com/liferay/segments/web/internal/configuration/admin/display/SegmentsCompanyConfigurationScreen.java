/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.segments.configuration.provider.SegmentsConfigurationProvider;
import com.liferay.segments.web.internal.display.context.SegmentsCompanyConfigurationDisplayContext;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Cristina González
 * @author Stefan Tanasie
 */
@Component(service = ConfigurationScreen.class)
public class SegmentsCompanyConfigurationScreen implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "segments";
	}

	@Override
	public String getKey() {
		return "segments-service";
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(locale, "segments-service-configuration-name");
	}

	@Override
	public String getScope() {
		return "company";
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/segments_configuration.jsp");

			httpServletRequest.setAttribute(
				SegmentsCompanyConfigurationDisplayContext.class.getName(),
				new SegmentsCompanyConfigurationDisplayContext(
					httpServletRequest, _portal,
					_segmentsConfigurationProvider));

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render /segments_configuration.jsp", exception);
		}
	}

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference
	private SegmentsConfigurationProvider _segmentsConfigurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.segments.web)",
		unbind = "-"
	)
	private ServletContext _servletContext;

}