/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.video.external.shortcut.provider;

import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.provider.DLVideoExternalShortcutProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

import java.net.URLEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLVideoExternalShortcutProvider.class)
public class FacebookDLVideoExternalShortcutProvider
	implements DLVideoExternalShortcutProvider {

	@Override
	public DLVideoExternalShortcut getDLVideoExternalShortcut(String url) {
		if (!_matches(url)) {
			return null;
		}

		return new DLVideoExternalShortcut() {

			@Override
			public String getURL() {
				return url;
			}

			@Override
			public String renderHTML(HttpServletRequest httpServletRequest) {
				try {
					return StringBundler.concat(
						"<iframe allowFullScreen=\"true\" allowTransparency=",
						"\"true\" frameborder=\"0\" height=\"315\" ",
						"src=\"https://www.facebook.com/plugins/video.php?",
						"height=315&href=",
						URLEncoder.encode(url, StringPool.UTF8),
						"&show_text=0&width=560\" scrolling=\"no\" ",
						"style=\"border: none; overflow: hidden;\" ",
						"width=\"560\"></iframe>");
				}
				catch (UnsupportedEncodingException
							unsupportedEncodingException) {

					if (_log.isDebugEnabled()) {
						_log.debug(unsupportedEncodingException);
					}

					return null;
				}
			}

		};
	}

	private boolean _matches(String url) {
		for (Pattern urlPattern : _urlPatterns) {
			Matcher matcher = urlPattern.matcher(url);

			if (matcher.matches()) {
				return true;
			}
		}

		return false;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		FacebookDLVideoExternalShortcutProvider.class);

	private static final List<Pattern> _urlPatterns = Arrays.asList(
		Pattern.compile(
			"(https?:\\/\\/(?:(www|m)\\.)?facebook\\.com\\/watch\\/?\\?" +
				"v=\\S*)"),
		Pattern.compile(
			"(https?:\\/\\/(?:www\\.)?facebook\\.com\\/\\S*\\/videos\\/\\S*)"),
		Pattern.compile("(https?:\\/\\/fb\\.watch\\/\\S*)"));

}