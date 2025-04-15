/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.social.bookmark.twitter.internal;

import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.social.bookmarks.SocialBookmark;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.util.Locale;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(
	property = {
		"social.bookmarks.priority:Integer=3", "social.bookmarks.type=twitter"
	},
	service = SocialBookmark.class
)
public class TwitterSocialBookmark implements SocialBookmark {

	@Override
	public String getName(Locale locale) {
		return _language.get(locale, "twitter");
	}

	@Override
	public String getPostURL(String title, String url) {
		return String.format(
			"https://twitter.com/intent/tweet?text=%s&tw_p=tweetbutton&url=%s",
			URLCodec.encodeURL(title), url);
	}

	@Override
	public void render(
			String target, String title, String url,
			HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse)
		throws IOException, ServletException {

		RequestDispatcher requestDispatcher =
			_servletContext.getRequestDispatcher("/page.jsp");

		requestDispatcher.include(httpServletRequest, httpServletResponse);
	}

	@Reference
	private Language _language;

	@Reference(
		target = "(osgi.web.symbolicname=com.liferay.social.bookmark.twitter)"
	)
	private ServletContext _servletContext;

}