/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.frontend.js.web.internal.resource;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONException;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.URLUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.Locale;

/**
 * @author Iván Zaera Avellón
 */
public class LanguageFrontendResource implements FrontendResource {

	public LanguageFrontendResource(
		JSONFactory jsonFactory, Language language, String languageId,
		long maxAge, boolean sendNoCache, URL url) {

		_jsonFactory = jsonFactory;
		_language = language;
		_languageId = languageId;
		_maxAge = maxAge;
		_sendNoCache = sendNoCache;
		_url = url;
	}

	@Override
	public String getCacheKey() {
		return null;
	}

	@Override
	public String getContentType() {
		return ContentTypes.APPLICATION_JAVASCRIPT;
	}

	@Override
	public String getETag() {
		return null;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		StringBuilder sb = new StringBuilder();

		Locale locale = LocaleUtil.fromLanguageId(_languageId);

		JSONArray languageKeysJSONArray = _getLanguageKeysJSONArray();

		for (int i = 0; i < languageKeysJSONArray.length(); i++) {
			String key = languageKeysJSONArray.getString(i);

			String label = _language.get(locale, key);

			sb.append(StringPool.APOSTROPHE);
			sb.append(key.replaceAll("'", "\\\\'"));
			sb.append("':'");
			sb.append(label.replaceAll("'", "\\\\'"));
			sb.append("',\n");
		}

		String content = StringUtil.replace(
			_TPL_JAVA_SCRIPT, new String[] {"[$LABELS$]"},
			new String[] {sb.toString()});

		return new ByteArrayInputStream(
			content.getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public long getMaxAge() {
		return _maxAge;
	}

	@Override
	public boolean isImmutable() {
		return false;
	}

	@Override
	public boolean isPrivate() {
		return false;
	}

	@Override
	public boolean isSendNoCache() {
		return _sendNoCache;
	}

	private static String _loadTemplate(String name) {
		try (InputStream inputStream =
				LanguageFrontendResource.class.getResourceAsStream(
					"dependencies/" + name)) {

			return StringUtil.read(inputStream);
		}
		catch (Exception exception) {
			_log.error("Unable to read template " + name, exception);
		}

		return StringPool.BLANK;
	}

	private JSONArray _getLanguageKeysJSONArray() throws IOException {
		try {
			JSONObject jsonObject = _jsonFactory.createJSONObject(
				URLUtil.toString(_url));

			return jsonObject.getJSONArray("keys");
		}
		catch (JSONException jsonException) {
			throw new IOException(
				"Invalid language JSON file " + _url, jsonException);
		}
	}

	private static final String _TPL_JAVA_SCRIPT;

	private static final Log _log = LogFactoryUtil.getLog(
		LanguageFrontendResource.class);

	static {
		_TPL_JAVA_SCRIPT = _loadTemplate("all.js.tpl");
	}

	private final JSONFactory _jsonFactory;
	private final Language _language;
	private final String _languageId;
	private final long _maxAge;
	private final boolean _sendNoCache;
	private final URL _url;

}