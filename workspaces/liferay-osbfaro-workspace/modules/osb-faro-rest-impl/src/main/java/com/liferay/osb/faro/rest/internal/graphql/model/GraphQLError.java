/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.faro.rest.internal.graphql.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import com.liferay.petra.string.StringBundler;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Leslie Wong
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraphQLError {

	public String getClassification() {
		if (_extensions == null) {
			return null;
		}

		return Objects.toString(_extensions.get("classification"), null);
	}

	public Map<String, Object> getExtensions() {
		return _extensions;
	}

	public Integer getHTTPStatus() {
		if (_extensions == null) {
			return null;
		}

		Object httpStatus = _extensions.get("httpStatus");

		if (!(httpStatus instanceof Number)) {
			return null;
		}

		Number number = (Number)httpStatus;

		return number.intValue();
	}

	public String getMessage() {
		return _message;
	}

	public String getMessageKey() {
		if (_messageKey != null) {
			return _messageKey;
		}

		if (_extensions == null) {
			return null;
		}

		return Objects.toString(_extensions.get("messageKey"), null);
	}

	public List<Object> getPath() {
		return _path;
	}

	public void setExtensions(Map<String, Object> extensions) {
		_extensions = extensions;
	}

	public void setMessage(String message) {
		_message = message;
	}

	public void setMessageKey(String messageKey) {
		_messageKey = messageKey;
	}

	public void setPath(List<Object> path) {
		_path = path;
	}

	@Override
	public String toString() {
		StringBundler sb = new StringBundler(7);

		sb.append("{message=");
		sb.append(_message);

		String messageKey = getMessageKey();

		if (messageKey != null) {
			sb.append(", messageKey=");
			sb.append(messageKey);
		}

		if (_path != null) {
			sb.append(", path=");
			sb.append(_path);
		}

		sb.append("}");

		return sb.toString();
	}

	private Map<String, Object> _extensions;
	private String _message;
	private String _messageKey;
	private List<Object> _path;

}