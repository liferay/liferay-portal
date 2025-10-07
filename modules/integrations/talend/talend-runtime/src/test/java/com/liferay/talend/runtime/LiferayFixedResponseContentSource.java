/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime;

import com.liferay.talend.BaseTestCase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.json.JsonObject;

/**
 * @author Igor Beslic
 */
public class LiferayFixedResponseContentSource extends LiferaySource {

	public LiferayFixedResponseContentSource(JsonObject jsonObject) {
		_jsonObject = jsonObject;
	}

	@Override
	public JsonObject doGetRequest(String resourceURL) {
		if (_jsonObject != null) {
			return _jsonObject;
		}

		return _baseTestCase.readObject(
			_getResponseContentFileName(resourceURL));
	}

	public void setBaseTestCase(BaseTestCase baseTestCase) {
		_baseTestCase = baseTestCase;
	}

	private String _getResponseContentFileName(String resourceURL) {
		Matcher matcher = _resourceURLPattern.matcher(resourceURL);

		if (!matcher.matches()) {
			throw new UnsupportedOperationException(
				"Unable to extract file name from given resource URL " +
					resourceURL);
		}

		String fileName = matcher.group(2);
		String fileNumber = matcher.group(3);

		StringBuilder sb = new StringBuilder();

		sb.append(fileName.replace('-', '_'));
		sb.append("_");
		sb.append(fileNumber.substring(fileNumber.indexOf("=") + 1));
		sb.append(".json");

		return sb.toString();
	}

	private static final Pattern _resourceURLPattern = Pattern.compile(
		"/o/.+/v\\d+(.\\d+)*/([^/\\s]+)/.+\\?.*(page=\\d+).*");

	private BaseTestCase _baseTestCase;
	private final JsonObject _jsonObject;

}