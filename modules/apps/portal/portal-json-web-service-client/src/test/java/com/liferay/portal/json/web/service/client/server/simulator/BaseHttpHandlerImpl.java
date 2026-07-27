/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.json.web.service.client.server.simulator;

import com.liferay.portal.json.web.service.client.server.simulator.constants.SimulatorConstants;
import com.liferay.portal.kernel.util.GetterUtil;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * @author Igor Beslic
 */
public class BaseHttpHandlerImpl implements HttpHandler {

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		List<NameValuePair> parameters = getParameters(httpExchange);

		int responseHTTPStatus = 400;

		if (_containsParameter(
				SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS,
				parameters)) {

			responseHTTPStatus = GetterUtil.getInteger(
				_getFirstValue(
					SimulatorConstants.HTTP_PARAMETER_RESPOND_WITH_STATUS,
					parameters));

			if (responseHTTPStatus == 400) {
				httpExchange.sendResponseHeaders(responseHTTPStatus, -1);

				return;
			}
		}

		boolean returnParamsInJSON = false;

		if (_containsParameter(
				SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON,
				parameters)) {

			returnParamsInJSON = Boolean.parseBoolean(
				_getFirstValue(
					SimulatorConstants.HTTP_PARAMETER_RETURN_PARMS_IN_JSON,
					parameters));
		}

		if ((responseHTTPStatus == 204) || (responseHTTPStatus == 401) ||
			(responseHTTPStatus == 405)) {

			String requestMethod = httpExchange.getRequestMethod();

			if (returnParamsInJSON && requestMethod.equals("GET")) {
				httpExchange.sendResponseHeaders(400, -1);
			}
			else {
				httpExchange.sendResponseHeaders(responseHTTPStatus, -1);
			}
		}

		String responseBody = getResponseBody(httpExchange);

		if ((responseHTTPStatus != 202) && returnParamsInJSON) {
			responseBody = getResponseBody(parameters, httpExchange);
		}

		Headers responseHeaders = httpExchange.getResponseHeaders();

		responseHeaders.add("Content-Type", getAccept(httpExchange));

		httpExchange.sendResponseHeaders(responseHTTPStatus, 0);

		OutputStream outputStream = httpExchange.getResponseBody();

		outputStream.write(responseBody.getBytes());

		outputStream.flush();

		outputStream.close();
	}

	protected String getAccept(HttpExchange httpExchange) {
		Headers requestHeaders = httpExchange.getRequestHeaders();

		for (Map.Entry<String, List<String>> headerEntry :
				requestHeaders.entrySet()) {

			String headerKey = headerEntry.getKey();

			if (!headerKey.equals("Accept")) {
				continue;
			}

			List<String> headerValues = headerEntry.getValue();

			if (headerValues.size() != 1) {
				throw new UnsupportedOperationException(
					"Only one Content-Type header value is allowed");
			}

			return headerValues.get(0);
		}

		return "plain/text";
	}

	protected String getAsJSON(List<NameValuePair> parameters) {
		StringBuffer sb = new StringBuffer();

		sb.append("{");

		for (NameValuePair nameValuePair : parameters) {
			sb.append("\"");
			sb.append(nameValuePair.getName());
			sb.append("\":\"");
			sb.append(nameValuePair.getValue());
			sb.append("\",");
		}

		sb.setLength(sb.length() - 1);

		sb.append("}");

		return sb.toString();
	}

	protected String getAsPlaintext(List<NameValuePair> parameters) {
		StringBuffer sb = new StringBuffer();

		for (NameValuePair nameValuePair : parameters) {
			sb.append(nameValuePair.getName());
			sb.append("=");
			sb.append(nameValuePair.getValue());
			sb.append("\n");
		}

		sb.setLength(sb.length() - 1);

		return sb.toString();
	}

	protected String getBody(HttpExchange httpExchange) throws IOException {
		StringBuffer sb = new StringBuffer();

		BufferedReader bufferedReader = new BufferedReader(
			new InputStreamReader(httpExchange.getRequestBody()));

		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}

	protected List<NameValuePair> getParameters(HttpExchange httpExchange)
		throws IOException {

		List<NameValuePair> parameters = new ArrayList<>();

		parameters.addAll(_parseParameters(getBody(httpExchange)));
		parameters.addAll(_parseParameters(getQuery(httpExchange)));

		return parameters;
	}

	protected String getQuery(HttpExchange httpExchange) throws IOException {
		String requestURIString = String.valueOf(httpExchange.getRequestURI());

		if (!requestURIString.contains("?")) {
			return null;
		}

		return requestURIString.substring(requestURIString.indexOf("?") + 1);
	}

	protected String getResponseBody(HttpExchange httpExchange) {
		String contentType = getAccept(httpExchange);

		if (Objects.equals(contentType, "application/json")) {
			return SimulatorConstants.RESPONSE_SUCCESS_IN_JSON;
		}

		return SimulatorConstants.RESPONSE_SUCCESS_IN_PLAIN_TXT;
	}

	protected String getResponseBody(
		List<NameValuePair> parameters, HttpExchange httpExchange) {

		String acceptContentType = getAccept(httpExchange);

		if (Objects.equals(acceptContentType, "application/json")) {
			return getAsJSON(parameters);
		}

		return getAsPlaintext(parameters);
	}

	protected boolean hasHeaderValue(
		String header, String value, HttpExchange httpExchange) {

		Headers requestHeaders = httpExchange.getRequestHeaders();

		for (Map.Entry<String, List<String>> headerEntry :
				requestHeaders.entrySet()) {

			String headerKey = headerEntry.getKey();

			if (!headerKey.equals(header)) {
				continue;
			}

			for (String headerValue : headerEntry.getValue()) {
				if (headerValue.equals(value)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean _containsParameter(
		String name, int expected, List<NameValuePair> parameters) {

		int actual = 0;

		for (NameValuePair nameValuePair : parameters) {
			if (name.equals(nameValuePair.getName())) {
				actual++;
			}
		}

		if (expected == actual) {
			return true;
		}

		return false;
	}

	private boolean _containsParameter(
		String name, List<NameValuePair> parameters) {

		return _containsParameter(name, 1, parameters);
	}

	private String _getFirstValue(String name, List<NameValuePair> parameters) {
		for (NameValuePair nameValuePair : parameters) {
			if (name.equals(nameValuePair.getName())) {
				return nameValuePair.getValue();
			}
		}

		return null;
	}

	private List<NameValuePair> _parseParameters(
		String parameterEntriesString) {

		if (parameterEntriesString == null) {
			return Collections.emptyList();
		}

		parameterEntriesString = parameterEntriesString.trim();

		if (parameterEntriesString.length() == 0) {
			return Collections.emptyList();
		}

		List<NameValuePair> parameters = new ArrayList<>();

		String[] parameterEntries = parameterEntriesString.split("&");

		for (String parameterEntry : parameterEntries) {
			String[] parameterKeyValues = parameterEntry.split("=");

			parameters.add(
				new BasicNameValuePair(
					parameterKeyValues[0], parameterKeyValues[1]));
		}

		return parameters;
	}

}