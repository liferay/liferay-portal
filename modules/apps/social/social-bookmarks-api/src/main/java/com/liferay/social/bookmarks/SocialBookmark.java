/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.bookmarks;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * Provides a specialized interface to define a social bookmark.
 *
 * <p>
 * Every OSGi service registered with this interface is available in the social
 * bookmarks configuration menu. When registering an implementation, the
 * property {@code social.bookmarks.type} must be set to a unique key
 * identifying the sharing service (e.g., {@code facebook}). If two services
 * share the same value for this property, the one with the highest service
 * ranking is used.
 * </p>
 *
 * @author Alejandro Tardín
 */
public interface SocialBookmark {

	/**
	 * Returns the social bookmark's name. This name is displayed in settings,
	 * tooltips, etc.
	 *
	 * @param  locale the requested locale of the message
	 * @return the social bookmark's name
	 */
	public String getName(Locale locale);

	/**
	 * Returns the URL that users are redirected to when clicking the social
	 * bookmark.
	 *
	 * @param  title the title of the content being shared
	 * @param  url the URL of the content being shared (e.g., the current page)
	 * @return the URL that users are redirected to when clicking the social
	 *         bookmark
	 */
	public String getPostURL(String title, String url);

	/**
	 * Renders the social bookmark's content. This method is called when using
	 * the {@code inline} display style.
	 *
	 * <p>
	 * This typically renders a link to the sharing URL with a custom icon or
	 * image. However, if the sharing platform provides code to display the
	 * bookmark, it can also be rendered from this method.
	 * </p>
	 *
	 * @param  target the desired target for the link (e.g., {@code _blank})
	 * @param  title the title of the content being shared
	 * @param  url the URL of the content being shared (e.g., the current page)
	 * @param  httpServletRequest the servlet request
	 * @param  httpServletResponse the servlet response
	 * @throws IOException if an IO exception occurred
	 * @throws ServletException if a servlet exception occurred
	 */
	public void render(
			String target, String title, String url,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException;

}