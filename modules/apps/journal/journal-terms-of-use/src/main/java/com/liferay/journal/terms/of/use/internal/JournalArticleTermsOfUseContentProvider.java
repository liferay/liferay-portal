/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.terms.of.use.internal;

import com.liferay.journal.configuration.JournalServiceConfiguration;
import com.liferay.journal.terms.of.use.internal.constants.JournalArticleTermsOfUseWebConstants;
import com.liferay.journal.terms.of.use.internal.display.context.JournalArticleTermsOfUseDisplayContext;
import com.liferay.portal.configuration.module.configuration.ConfigurationProvider;
import com.liferay.portal.kernel.terms.of.use.TermsOfUseContentProvider;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.WebKeys;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Eduardo García
 */
@Component(
	configurationPid = "com.liferay.journal.configuration.JournalServiceConfiguration",
	service = TermsOfUseContentProvider.class
)
public class JournalArticleTermsOfUseContentProvider
	implements TermsOfUseContentProvider {

	@Override
	public void includeConfig(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_includeJSPPath(
			httpServletRequest, httpServletResponse, _JSP_PATH_CONFIGURATION);
	}

	@Override
	public void includeView(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws Exception {

		_includeJSPPath(
			httpServletRequest, httpServletResponse, _JSP_PATH_VIEW);
	}

	private void _includeJSPPath(
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, String jspPath)
		throws Exception {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher(jspPath);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)httpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		JournalServiceConfiguration journalServiceConfiguration =
			_configurationProvider.getCompanyConfiguration(
				JournalServiceConfiguration.class, themeDisplay.getCompanyId());

		httpServletRequest.setAttribute(
			JournalArticleTermsOfUseWebConstants.
				JOURNAL_ARTICLE_TERMS_OF_USE_DISPLAY_CONTEXT,
			new JournalArticleTermsOfUseDisplayContext(
				journalServiceConfiguration, themeDisplay));

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	private static final String _JSP_PATH_CONFIGURATION = "/configuration.jsp";

	private static final String _JSP_PATH_VIEW = "/view.jsp";

	@Reference
	private ConfigurationProvider _configurationProvider;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.journal.terms.of.use)"
	)
	private ServletContext _servletContext;

}