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
public class VimeoDLVideoExternalShortcutProvider
	implements DLVideoExternalShortcutProvider {

	@Override
	public DLVideoExternalShortcut getDLVideoExternalShortcut(String url) {
		String vimeoVideoId = _getVimeoVideoId(url);

		if (Validator.isNull(vimeoVideoId)) {
			return null;
		}

		final JSONObject jsonObject = _getEmbedJSONObject(url);

		return new DLVideoExternalShortcut() {

			@Override
			public String getDescription() {
				return jsonObject.getString("description");
			}

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
				return StringBundler.concat(
					"<iframe allowfullscreen frameborder=\"0\" height=\"315\" ",
					"mozallowfullscreen src=\"https://player.vimeo.com/video/",
					vimeoVideoId, "\" webkitallowfullscreen ",
					"width=\"560\"></iframe>");
			}

		};
	}

	private JSONObject _getEmbedJSONObject(String url) {
		try {
			Http.Options options = new Http.Options();

			options.addHeader("Content-Type", ContentTypes.APPLICATION_JSON);
			options.setLocation("https://vimeo.com/api/oembed.json?url=" + url);

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

			return null;
		}
	}

	private String _getVimeoVideoId(String url) {
		for (Pattern urlPattern : _urlPatterns) {
			Matcher matcher = urlPattern.matcher(url);

			if (matcher.matches()) {
				return matcher.group(1);
			}
		}

		return null;
	}

	private static final Log _log = LogFactoryUtil.getLog(
		VimeoDLVideoExternalShortcutProvider.class);

	private static final List<Pattern> _urlPatterns = Arrays.asList(
		Pattern.compile(
			"https?:\\/\\/(?:www\\.)?vimeo\\.com\\/album\\/.*\\/video" +
				"\\/(\\S*)"),
		Pattern.compile(
			"https?:\\/\\/(?:www\\.)?vimeo\\.com\\/showcase\\/.*\\/video" +
				"\\/(\\S*)"),
		Pattern.compile(
			"https?:\\/\\/(?:www\\.)?vimeo\\.com\\/channels\\/.*\\/(\\S*)"),
		Pattern.compile(
			"https?:\\/\\/(?:www\\.)?vimeo\\.com\\/groups\\/.*\\/videos" +
				"\\/(\\S*)"),
		Pattern.compile("https?:\\/\\/(?:www\\.)?vimeo\\.com\\/(\\S*)$"),
		Pattern.compile("https?:\\/\\/player\\.vimeo\\.com\\/video\\/(\\S*)$"));

	@Reference
	private Http _http;

	@Reference
	private JSONFactory _jsonFactory;

}