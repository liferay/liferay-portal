/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.translation.web.internal.translator;

import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.translation.translator.TranslatorPacket;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Adolfo Pérez
 */
public class JSONTranslatorPacket implements TranslatorPacket {

	/**
	 * @deprecated As of Cavanaugh (7.4.x), replaced by {@link
	 *             #JSONTranslatorPacket(long, JSONObject)}
	 */
	@Deprecated
	public JSONTranslatorPacket(JSONObject jsonObject) {
		this(CompanyThreadLocal.getCompanyId(), jsonObject);
	}

	public JSONTranslatorPacket(long companyId, JSONObject jsonObject) {
		_companyId = companyId;

		_sourceLanguageId = jsonObject.getString("sourceLanguageId");
		_targetLanguageId = jsonObject.getString("targetLanguageId");

		JSONObject fieldsJSONObject = jsonObject.getJSONObject("fields");
		JSONObject htmlJSONObject = jsonObject.getJSONObject("html");

		for (String key : fieldsJSONObject.keySet()) {
			_fieldsMap.put(key, fieldsJSONObject.getString(key));
			_htmlMap.put(key, _getHtml(key, htmlJSONObject));
		}
	}

	@Override
	public long getCompanyId() {
		return _companyId;
	}

	@Override
	public Map<String, String> getFieldsMap() {
		return _fieldsMap;
	}

	@Override
	public Map<String, Boolean> getHTMLMap() {
		return _htmlMap;
	}

	@Override
	public String getSourceLanguageId() {
		return _sourceLanguageId;
	}

	@Override
	public String getTargetLanguageId() {
		return _targetLanguageId;
	}

	private Boolean _getHtml(String key, JSONObject htmlJSONObject) {
		if (htmlJSONObject == null) {
			return null;
		}

		return htmlJSONObject.getBoolean(key);
	}

	private final long _companyId;
	private final Map<String, String> _fieldsMap = new LinkedHashMap<>();
	private final Map<String, Boolean> _htmlMap = new LinkedHashMap<>();
	private final String _sourceLanguageId;
	private final String _targetLanguageId;

}