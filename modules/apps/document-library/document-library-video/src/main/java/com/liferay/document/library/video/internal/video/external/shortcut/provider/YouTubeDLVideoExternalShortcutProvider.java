/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.video.internal.video.external.shortcut.provider;

import com.liferay.document.library.video.external.shortcut.DLVideoExternalShortcut;
import com.liferay.document.library.video.external.shortcut.provider.DLVideoExternalShortcutProvider;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.net.HttpURLConnection;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

/**
 * @author Alejandro Tardín
 */
@Component(service = DLVideoExternalShortcutProvider.class)
public class YouTubeDLVideoExternalShortcutProvider
	implements DLVideoExternalShortcutProvider {

	@Override
	public DLVideoExternalShortcut getDLVideoExternalShortcut(String url) {
		String youTubeVideoId = _getYouTubeVideoId(url);

		if (Validator.isNull(youTubeVideoId)) {
			return null;
		}

		JSONObject jsonObject = _getEmbedJSONObject(url);

		return new DLVideoExternalShortcut() {

			@Override
			public String getThumbnailURL() {
				return jsonObject.getString("thumbnail_url");
			}

			@Override
			public String getTitle() {
				return jsonObject.getString("title");
			}

			@Override
			public String getURL() {
				return url;
			}

			@Override
			public String renderHTML(HttpServletRequest httpServletRequest) {
				String iframeSrc =
					"https://www.youtube.com/embed/" + youTubeVideoId +
						"?rel=0";
				String start = HttpComponentsUtil.getParameter(url, "t", false);

				if (Validator.isNotNull(start)) {
					iframeSrc = HttpComponentsUtil.addParameter(
						iframeSrc, "start", start);
				}

				return StringBundler.concat(
					"<iframe allow=\"autoplay; encrypted-media\" ",
					"allowfullscreen height=\"315\" frameborder=\"0\" ",
					"src=\"", iframeSrc, "\" width=\"560\"></iframe>");
			}

		};
	}

	private JSONObject _getEmbedJSONObject(String url) {
		try {
			Http.Options options = new Http.Options();

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setLocation(
				"https://www.youtube.com/oembed?format=json&url=" + url);

			String responseJSON = _http.URLtoString(options);

			Http.Response response = options.getResponse();

			JSONObject jsonObject;

			if (response.getResponseCode() != HttpURLConnection.HTTP_OK) {
				jsonObject = _jsonFactory.createJSONObject();
			}
			else {
				jsonObject = _jsonFactory.createJSONObject(responseJSON);
			}

			return jsonObject;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug(exception);
			}

			return _jsonFactory.createJSONObject();
		}
	}

	private String _getYouTubeVideoId(String url) {
		for (Pattern urlPattern : _urlPatterns) {
			Matcher matcher = urlPattern.matcher(url);

			if (matcher.matches()) {
				return matcher.group(1);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		YouTubeDLVideoExternalShortcutProvider.class);

	private static final List<Pattern> _urlPatterns = Arrays.asList(
		Pattern.compile(
			"https?:\\/\\/(?:www\\.)?youtube\\.com\\/watch\\S*v=([^?&]*)\\S*$"),
		Pattern.compile(
			"https?:\\/\\/(?:www\\.)?youtube\\.com\\/\\S*\\/([^?&]*)\\S*$"),
		Pattern.compile("https?:\\/\\/(?:www\\.)?youtu\\.be\\/(\\S*)$"));

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}