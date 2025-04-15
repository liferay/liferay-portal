/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.knowledge.base.web.internal.configuration.admin.display;

import com.liferay.configuration.admin.display.ConfigurationScreen;
import com.liferay.knowledge.base.configuration.KBServiceConfigurationProvider;
import com.liferay.knowledge.base.web.internal.display.context.KBArticleCompanyConfigurationDisplayContext;
import com.liferay.portal.configuration.metatype.annotations.ExtendedObjectClassDefinition;
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

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alicia García
 */
@Component(service = ConfigurationScreen.class)
public class KBConfigurationScreen implements ConfigurationScreen {

	@Override
	public String getCategoryKey() {
		return "knowledge-base";
	}

	@Override
	public String getKey() {
		return "knowledge-base-service";
	}

	@Override
	public String getName(Locale locale) {
		return _language.get(
			locale, "knowledge-base-service-configuration-name");
	}

	@Override
	public String getScope() {
		return ExtendedObjectClassDefinition.Scope.SYSTEM.getValue();
	}

	@Override
	public void render(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException {

		try {
			httpServletRequest.setAttribute(
				KBArticleCompanyConfigurationDisplayContext.class.getName(),
				new KBArticleCompanyConfigurationDisplayContext(
					httpServletRequest, _kbServiceConfigurationProvider,
					_portal.getLiferayPortletResponse(
						(PortletResponse)httpServletRequest.getAttribute(
							JavaConstants.JAVAX_PORTLET_RESPONSE))));

			RequestDispatcher requestDispatcher =
				_servletContext.getRequestDispatcher(
					"/admin/knowledge_base_settings" +
						"/kb_article_expiration_date_configuration.jsp");

			requestDispatcher.include(httpServletRequest, httpServletResponse);
		}
		catch (Exception exception) {
			throw new IOException(
				"Unable to render kb_article_expiration_date_configuration.jsp",
				exception);
		}
	}

	@Reference
	private KBServiceConfigurationProvider _kbServiceConfigurationProvider;

	@Reference
	private Language _language;

	@Reference
	private Portal _portal;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.knowledge.base.web)"
	)
	private ServletContext _servletContext;

}