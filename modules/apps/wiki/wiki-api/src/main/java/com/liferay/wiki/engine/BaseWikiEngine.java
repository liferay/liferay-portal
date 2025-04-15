/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine;

import com.liferay.petra.io.unsync.UnsyncStringWriter;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.resource.bundle.ResourceBundleLoader;
import com.liferay.portal.kernel.servlet.PipingServletResponse;
import com.liferay.portal.kernel.util.ResourceBundleUtil;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.language.LanguageResources;
import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

import jakarta.portlet.PortletURL;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.jsp.PageContext;

import java.io.IOException;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Iván Zaera
 */
public abstract class BaseWikiEngine implements WikiEngine {

	public static BaseWikiEngine getBaseWikiEngine(
		ServletRequest servletRequest) {

		return (BaseWikiEngine)servletRequest.getAttribute(_BASE_WIKI_ENGINE);
	}

	public static WikiNode getWikiNode(ServletRequest servletRequest) {
		return (WikiNode)servletRequest.getAttribute(_WIKI_NODE);
	}

	public static WikiPage getWikiPage(ServletRequest servletRequest) {
		return (WikiPage)servletRequest.getAttribute(_WIKI_PAGE);
	}

	@Override
	public String convert(
		WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
		String attachmentURLPrefix) {

		return page.getContent();
	}

	public abstract String getEditorName();

	@Override
	public String getFormatLabel(Locale locale) {
		ResourceBundleLoader resourceBundleLoader = getResourceBundleLoader();

		ResourceBundle resourceBundle = resourceBundleLoader.loadResourceBundle(
			locale);

		String format = getFormat();

		String formatLabel = ResourceBundleUtil.getString(
			resourceBundle, format);

		if (formatLabel != null) {
			return formatLabel;
		}

		return format;
	}

	public String getHelpPageHTML(PageContext pageContext)
		throws IOException, ServletException {

		if (!isHelpPageDefined()) {
			return StringPool.BLANK;
		}

		HttpServletResponse httpServletResponse =
			(HttpServletResponse)pageContext.getResponse();

		UnsyncStringWriter unsyncStringWriter = new UnsyncStringWriter();

		PipingServletResponse pipingServletResponse = new PipingServletResponse(
			httpServletResponse, unsyncStringWriter);

		ServletContext servletContext = getHelpPageServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getHelpPageJSP());

		requestDispatcher.include(
			pageContext.getRequest(), pipingServletResponse);

		StringBundler sb = unsyncStringWriter.getStringBundler();

		return sb.toString();
	}

	public String getHelpPageTitle(HttpServletRequest httpServletRequest) {
		return LanguageUtil.format(
			httpServletRequest, "x-syntax-help",
			getFormatLabel(httpServletRequest.getLocale()), false);
	}

	public abstract String getHelpURL();

	@Override
	public Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException {

		return Collections.emptyMap();
	}

	@Override
	public String getToolbarSet() {
		return "creole";
	}

	public boolean isHelpPageDefined() {
		if ((getHelpPageServletContext() == null) ||
			Validator.isNull(getHelpPageJSP())) {

			return false;
		}

		return true;
	}

	@Override
	public void renderEditPage(
			ServletRequest servletRequest, ServletResponse servletResponse,
			WikiNode node, WikiPage page)
		throws IOException, ServletException {

		ServletContext servletContext = getEditPageServletContext();

		RequestDispatcher requestDispatcher =
			servletContext.getRequestDispatcher(getEditPageJSP());

		servletRequest.setAttribute(_BASE_WIKI_ENGINE, this);
		servletRequest.setAttribute(_WIKI_NODE, node);
		servletRequest.setAttribute(_WIKI_PAGE, page);

		requestDispatcher.include(servletRequest, servletResponse);
	}

	@Override
	public boolean validate(long nodeId, String newContent) {
		return true;
	}

	protected String getEditPageJSP() {
		return "/edit_page.jsp";
	}

	protected abstract ServletContext getEditPageServletContext();

	protected String getHelpPageJSP() {
		return "/help_page.jsp";
	}

	protected abstract ServletContext getHelpPageServletContext();

	protected ResourceBundleLoader getResourceBundleLoader() {
		return LanguageResources.PORTAL_RESOURCE_BUNDLE_LOADER;
	}

	private static final String _BASE_WIKI_ENGINE =
		BaseWikiEngine.class.getName() + "#BASE_WIKI_ENGINE";

	private static final String _WIKI_NODE =
		BaseWikiEngine.class.getName() + "#WIKI_NODE";

	private static final String _WIKI_PAGE =
		BaseWikiEngine.class.getName() + "#WIKI_PAGE";

}