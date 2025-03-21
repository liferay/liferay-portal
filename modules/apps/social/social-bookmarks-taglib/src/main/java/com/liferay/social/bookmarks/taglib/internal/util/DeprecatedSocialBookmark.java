/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.bookmarks.taglib.internal.util;

import com.liferay.portal.kernel.configuration.Filter;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.social.bookmarks.SocialBookmark;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

/**
 * @author Alejandro Tardín
 */
public class DeprecatedSocialBookmark implements SocialBookmark {

	public DeprecatedSocialBookmark(String type) {
		_type = type;
	}

	@Override
	public String getName(Locale locale) {
		return LanguageUtil.get(locale, _type);
	}

	@Override
	public String getPostURL(String title, String url) {
		return StringUtil.replace(
			PropsUtil.get(_SOCIAL_BOOKMARK_POST_URL, new Filter(_type)),
			new String[] {
				"${liferay:social-bookmark:title}",
				"${liferay:social-bookmark:url}"
			},
			new String[] {URLCodec.encodeURL(title), url});
	}

	@Override
	public void render(
			String target, String title, String url,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			httpServletRequest.getRequestDispatcher(
				"/bookmark/deprecated_bookmark.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	private static final String _SOCIAL_BOOKMARK_POST_URL =
		"social.bookmark.post.url";

	private final String _type;

}