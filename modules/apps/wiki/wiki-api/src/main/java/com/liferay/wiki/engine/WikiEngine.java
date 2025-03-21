/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.wiki.engine;

import com.liferay.wiki.exception.PageContentException;
import com.liferay.wiki.model.WikiNode;
import com.liferay.wiki.model.WikiPage;

import jakarta.portlet.PortletURL;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import java.io.IOException;

import java.util.Locale;
import java.util.Map;

/**
 * @author Jorge Ferrer
 */
public interface WikiEngine {

	/**
	 * Returns the content of the given page converted to HTML using the view
	 * and edit URLs to build links.
	 *
	 * @param  page the wiki page
	 * @param  viewPageURL the URL to view the page
	 * @param  editPageURL the URL to edit the page
	 * @param  attachmentURLPrefix the URL prefix to use for attachments to the
	 *         page
	 * @return the content of the given page converted to HTML
	 */
	public String convert(
			WikiPage page, PortletURL viewPageURL, PortletURL editPageURL,
			String attachmentURLPrefix)
		throws PageContentException;

	public String getFormat();

	public String getFormatLabel(Locale locale);

	/**
	 * Returns a map of the links included in the given page. The key of each
	 * map entry is the title of the linked page. The value is a Boolean object
	 * that indicates if the linked page exists or not.
	 *
	 * @param  page the page
	 * @return a map of links included in the given page
	 */
	public Map<String, Boolean> getOutgoingLinks(WikiPage page)
		throws PageContentException;

	public String getToolbarSet();

	public void renderEditPage(
			ServletRequest servletRequest, ServletResponse servletResponse,
			WikiNode node, WikiPage page)
		throws IOException, ServletException;

	/**
	 * Returns <code>true</code> if the content of a wiki page for this engine
	 * is valid.
	 *
	 * @param  nodeId the ID of the wiki page node
	 * @param  content the page content
	 * @return <code>true</code> if the content of a wiki page for this engine
	 *         is valid; <code>false</code> otherwise
	 */
	public boolean validate(long nodeId, String content);

}