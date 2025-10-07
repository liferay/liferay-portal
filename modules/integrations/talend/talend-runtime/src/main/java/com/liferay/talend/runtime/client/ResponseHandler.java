/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.talend.runtime.client;

import com.liferay.talend.runtime.client.exception.ResponseContentClientException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import javax.ws.rs.core.Response;

/**
 * @author Igor Beslic
 */
public class ResponseHandler {

	public JsonObject asJsonObject(Response response) {
		JsonReader jsonReader = Json.createReader(
			(InputStream)response.getEntity());

		return jsonReader.readObject();
	}

	public String asText(Response response) {
		InputStream inputStream = (InputStream)response.getEntity();

		InputStreamReader inputStreamReader = new InputStreamReader(
			inputStream);

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		StringBuffer sb = new StringBuffer();

		try {
			while (bufferedReader.ready()) {
				sb.append(bufferedReader.readLine());

				if (bufferedReader.ready()) {
					sb.append(" ");
				}
			}
		}
		catch (IOException ioException) {
			throw new ResponseContentClientException(
				"Unable to read response content", response.getStatus(),
				ioException);
		}

		return sb.toString();
	}

	public List<String> getContentType(Response response) {
		String contentTypeHeader = response.getHeaderString("Content-Type");

		if ((contentTypeHeader == null) || contentTypeHeader.isEmpty()) {
			return Collections.emptyList();
		}

		String[] headers = contentTypeHeader.split(",");

		List<String> headerValues = new ArrayList<>();

		for (String header : headers) {
			headerValues.add(header);
		}

		return headerValues;
	}

	public Response.Status.Family getResponseStatusFamily(Response response) {
		Response.StatusType statusType = response.getStatusInfo();

		return statusType.getFamily();
	}

	public boolean isApplicationJsonContentType(Response response) {
		List<String> strings = getContentType(response);

		return strings.contains("application/json");
	}

	public boolean isRedirect(Response response) {
		if (getResponseStatusFamily(response) ==
				Response.Status.Family.REDIRECTION) {

			return true;
		}

		return false;
	}

	public boolean isSuccess(Response response) {
		if (getResponseStatusFamily(response) ==
				Response.Status.Family.SUCCESSFUL) {

			return true;
		}

		return false;
	}

}