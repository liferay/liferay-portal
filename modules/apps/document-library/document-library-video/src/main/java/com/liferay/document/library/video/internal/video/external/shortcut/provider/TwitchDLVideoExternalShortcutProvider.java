/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.video.external.shortcut.provider;

import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.provider.DLVideoExternalShortcutProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLVideoExternalShortcutProvider.class)
public class TwitchDLVideoExternalShortcutProvider
	implements DLVideoExternalShortcutProvider {

	@Override
	public DLVideoExternalShortcut getDLVideoExternalShortcut(String url) {
		String videoQueryParam = _getVideoQueryParam(url);

		if (videoQueryParam == null) {
			return null;
		}

		return new DLVideoExternalShortcut() {

			@Override
			public String getURL() {
				return url;
			}

			@Override
			public String renderHTML(HttpServletRequest httpServletRequest) {
				return StringBundler.concat(
					"<iframe allowfullscreen=\"true\" frameborder=\"0\" ",
					"height=\"315\" ",
					"src=\"https://player.twitch.tv/?autoplay=false&",
					videoQueryParam, "&parent=",
					_portal.getHost(httpServletRequest),
					"\" scrolling=\"no\" width=\"560\" ></iframe>");
			}

		};
	}

	private String _getTwitchVideoId(Pattern pattern, String url) {
		Matcher matcher = pattern.matcher(url);

		if (matcher.matches()) {
			return matcher.group(1);
		}

		return null;
	}

	private String _getVideoQueryParam(String url) {
		String twitchVideoId = _getTwitchVideoId(_videoURLPattern, url);

		if (Validator.isNull(twitchVideoId)) {
			twitchVideoId = _getTwitchVideoId(_channelURLPattern, url);

			if (Validator.isNull(twitchVideoId)) {
				return null;
			}

			return "channel=" + twitchVideoId;
		}

		return "video=" + twitchVideoId;
	}

	private static final Pattern _channelURLPattern = Pattern.compile(
		"https?:\\/\\/(?:www\\.)?twitch\\.tv\\/(\\S*)$");
	private static final Pattern _videoURLPattern = Pattern.compile(
		"https?:\\/\\/(?:www\\.)?twitch\\.tv\\/videos\\/(\\S*)$");

	@Reference
	private Portal _portal;

}