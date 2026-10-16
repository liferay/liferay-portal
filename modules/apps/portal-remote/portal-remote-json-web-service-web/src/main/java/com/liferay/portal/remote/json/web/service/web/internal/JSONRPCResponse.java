/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializable;
import com.liferay.portal.kernel.json.JSONSerializer;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsValues;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Igor Spasic
 */
public class JSONRPCResponse implements JSONSerializable {

	public JSONRPCResponse(
		JSONRPCRequest jsonRPCRequest, Object result, Exception exception) {

		_jsonrpc = GetterUtil.getString(jsonRPCRequest.getJsonrpc());

		Error error = null;

		if (!_jsonrpc.equals("2.0")) {
			error = new Error(-32700, "Invalid JSON RPC version " + _jsonrpc);
			result = null;
		}
		else if (exception != null) {
			int code = -32603;

			String message = "Internal error";

			if (exception instanceof InvocationTargetException) {
				code = -32602;

				message = "Invalid params";

				if (PropsValues.JSON_SERVICE_SERIALIZE_THROWABLE) {
					message = String.valueOf(exception.getCause());
				}
			}
			else if (PropsValues.JSON_SERVICE_SERIALIZE_THROWABLE) {
				message = exception.toString();
			}

			error = new Error(code, message);
			result = null;
		}

		_result = result;

		_error = error;
		_id = jsonRPCRequest.getId();
	}

	@Override
	public String toJSONString() {
		Map<String, Object> response = new HashMap<>();

		if (_error != null) {
			response.put("error", _error);
		}

		if (_id != null) {
			response.put("id", _id);
		}

		if (_jsonrpc != null) {
			response.put("jsonrpc", "2.0");
		}

		if (_result != null) {
			response.put("result", _result);
		}

		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();

		jsonSerializer.exclude("*.class");
		jsonSerializer.include("error", "result");

		return jsonSerializer.serialize(response);
	}

	public class Error {

		public Error(int code, String message) {
			_code = code;
			_message = message;
		}

		public int getCode() {
			return _code;
		}

		public String getMessage() {
			return _message;
		}

		private final int _code;
		private final String _message;

	}

	private final Error _error;
	private final Integer _id;
	private final String _jsonrpc;
	private final Object _result;

}