/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.jenkins.results.parser.aws;

import com.amazonaws.DefaultRequest;
import com.amazonaws.auth.AWS4Signer;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.http.HttpMethodName;

import com.liferay.jenkins.results.parser.JenkinsResultsParserUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;

import java.nio.charset.StandardCharsets;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author Brittney Nguyen
 */
public class AmazonPrometheusUtil {

	public static JSONArray query(String query, String workspaceURL)
		throws IOException {

		Map<String, String> parameters = new LinkedHashMap<>();

		parameters.put("query", query);

		return _getResultJSONArray("/query", parameters, workspaceURL);
	}

	public static JSONArray queryRange(
			long endTime, String query, long startTime, long step,
			String workspaceURL)
		throws IOException {

		Map<String, String> parameters = new LinkedHashMap<>();

		parameters.put("end", String.valueOf(endTime));
		parameters.put("query", query);
		parameters.put("start", String.valueOf(startTime));
		parameters.put("step", String.valueOf(step));

		return _getResultJSONArray("/query_range", parameters, workspaceURL);
	}

	private static String _getFormContent(Map<String, String> parameters)
		throws IOException {

		StringBuilder sb = new StringBuilder();

		for (Map.Entry<String, String> entry : parameters.entrySet()) {
			if (sb.length() > 0) {
				sb.append("&");
			}

			sb.append(entry.getKey());
			sb.append("=");
			sb.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
		}

		return sb.toString();
	}

	private static String _getResponseContent(
			String apiPath, Map<String, String> parameters, String workspaceURL)
		throws IOException {

		URL url = new URL(workspaceURL);

		String host = url.getHost();

		String[] hostParts = host.split("\\.");

		if (hostParts.length < 2) {
			throw new IOException(
				"Unable to get the region from the workspace URL " +
					workspaceURL);
		}

		String formContent = _getFormContent(parameters);

		byte[] formContentBytes = formContent.getBytes(StandardCharsets.UTF_8);

		DefaultRequest<Void> defaultRequest = new DefaultRequest<>(
			_SERVICE_NAME);

		defaultRequest.addHeader("Accept-Encoding", "identity");
		defaultRequest.addHeader(
			"Content-Type", "application/x-www-form-urlencoded");
		defaultRequest.setContent(new ByteArrayInputStream(formContentBytes));
		defaultRequest.setEndpoint(URI.create("https://" + host));
		defaultRequest.setHttpMethod(HttpMethodName.POST);
		defaultRequest.setResourcePath(
			JenkinsResultsParserUtil.combine(
				url.getPath(), "/api/v1", apiPath));

		AWS4Signer aws4Signer = new AWS4Signer();

		aws4Signer.setRegionName(hostParts[1]);
		aws4Signer.setServiceName(_SERVICE_NAME);

		AWSCredentialsProvider awsCredentialsProvider =
			DefaultAWSCredentialsProviderChain.getInstance();

		aws4Signer.sign(
			defaultRequest, awsCredentialsProvider.getCredentials());

		URL requestURL = new URL(
			JenkinsResultsParserUtil.combine(
				"https://", host, defaultRequest.getResourcePath()));

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)requestURL.openConnection();

		try {
			httpURLConnection.setConnectTimeout(_TIMEOUT);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setReadTimeout(_TIMEOUT);
			httpURLConnection.setRequestMethod("POST");

			Map<String, String> headers = defaultRequest.getHeaders();

			for (Map.Entry<String, String> entry : headers.entrySet()) {
				String name = entry.getKey();

				if (name.equalsIgnoreCase("Host")) {
					continue;
				}

				httpURLConnection.setRequestProperty(name, entry.getValue());
			}

			try (OutputStream outputStream =
					httpURLConnection.getOutputStream()) {

				outputStream.write(formContentBytes);
			}

			InputStream inputStream = null;
			int responseCode = httpURLConnection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				inputStream = httpURLConnection.getInputStream();
			}
			else {
				inputStream = httpURLConnection.getErrorStream();
			}

			String responseContent = "";

			if (inputStream != null) {
				try {
					responseContent = IOUtils.toString(
						inputStream, StandardCharsets.UTF_8);
				}
				finally {
					inputStream.close();
				}
			}

			if (responseCode != HttpURLConnection.HTTP_OK) {
				throw new IOException(
					JenkinsResultsParserUtil.combine(
						"Unable to query ", requestURL.toString(), ": HTTP ",
						String.valueOf(responseCode), " ", responseContent));
			}

			return responseContent;
		}
		finally {
			httpURLConnection.disconnect();
		}
	}

	private static JSONArray _getResultJSONArray(
			String apiPath, Map<String, String> parameters, String workspaceURL)
		throws IOException {

		IOException ioException1 = null;

		for (int i = 0; i < 3; i++) {
			if (i > 0) {
				JenkinsResultsParserUtil.sleep(10 * 1000);
			}

			try {
				JSONObject responseJSONObject = new JSONObject(
					_getResponseContent(apiPath, parameters, workspaceURL));

				if (!Objects.equals(
						responseJSONObject.optString("status"), "success")) {

					throw new IOException(
						JenkinsResultsParserUtil.combine(
							"Unable to query ", parameters.get("query"), ": ",
							responseJSONObject.toString()));
				}

				JSONObject dataJSONObject = responseJSONObject.getJSONObject(
					"data");

				return dataJSONObject.getJSONArray("result");
			}
			catch (IOException ioException2) {
				ioException1 = ioException2;

				System.out.println(ioException2.getMessage());
			}
		}

		throw ioException1;
	}

	private static final String _SERVICE_NAME = "aps";

	private static final int _TIMEOUT = 60 * 1000;

}