/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.petra.io.StreamUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.util.Validator;

import jakarta.servlet.http.HttpServletRequest;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Igor Spasic
 */
public class JSONRPCRequest {

	public static JSONRPCRequest detectJSONRPCRequest(
		HttpServletRequest httpServletRequest) {

		try {
			String requestBody = StreamUtil.toString(
				httpServletRequest.getInputStream());

			if (Validator.isNull(requestBody) ||
				!requestBody.startsWith(StringPool.OPEN_CURLY_BRACE) ||
				!requestBody.endsWith(StringPool.CLOSE_CURLY_BRACE)) {

				return null;
			}

			JSONDeserializer<HashMap<Object, Object>> jsonDeserializer =
				JSONFactoryUtil.createJSONDeserializer();

			jsonDeserializer.use(null, HashMap.class);
			jsonDeserializer.use("parameters", HashMap.class);

			HashMap<Object, Object> requestBodyMap =
				jsonDeserializer.deserialize(requestBody);

			JSONRPCRequest jsonrpcRequest = new JSONRPCRequest();

			Number id = (Number)requestBodyMap.get("id");

			if (id != null) {
				jsonrpcRequest._id = Integer.valueOf(id.intValue());
			}

			jsonrpcRequest._jsonrpc = (String)requestBodyMap.get("jsonrpc");
			jsonrpcRequest._method = (String)requestBodyMap.get("method");
			jsonrpcRequest._parameters = (Map<String, ?>)requestBodyMap.get(
				"params");

			if (Validator.isNull(jsonrpcRequest._method)) {
				return null;
			}

			return jsonrpcRequest;
		}
		catch (Exception exception) {
			if (_log.isDebugEnabled()) {
				_log.debug("Unable to parse JSON RPC request", exception);
			}

			return null;
		}
	}

	public Integer getId() {
		return _id;
	}

	public String getJsonrpc() {
		return _jsonrpc;
	}

	public String getMethod() {
		return _method;
	}

	public String getParameter(String name) {
		Object value = _parameters.get(name);

		if (value != null) {
			return String.valueOf(value);
		}

		return null;
	}

	public Set<String> getParameterNames() {
		return _parameters.keySet();
	}

	public void setId(Integer id) {
		_id = id;
	}

	public void setJsonrpc(String jsonrpc) {
		_jsonrpc = jsonrpc;
	}

	public void setMethod(String method) {
		_method = method;
	}

	public void setParameters(Map<String, ?> parameters) {
		_parameters = parameters;
	}

	private static final Log _log = LogFactoryUtil.getLog(JSONRPCRequest.class);

	private Integer _id;
	private String _jsonrpc;
	private String _method;
	private Map<String, ?> _parameters;

}