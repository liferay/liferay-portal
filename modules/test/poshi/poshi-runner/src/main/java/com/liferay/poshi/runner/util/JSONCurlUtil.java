/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.poshi.runner.util;

import com.liferay.poshi.core.util.OSDetector;
import com.liferay.poshi.core.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Michael Hashimoto
 */
public class JSONCurlUtil {

	public static String delete(String requestString)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "DELETE");

		return request.send();
	}

	public static String delete(String requestString, String jsonPath)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "DELETE");

		return _getParsedResponse(request, jsonPath);
	}

	public static String get(String requestString)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "GET");

		return request.send();
	}

	public static String get(String requestString, String jsonPath)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "GET");

		return _getParsedResponse(request, jsonPath);
	}

	public static String head(String requestString)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "HEAD");

		return request.send();
	}

	public static String head(String requestString, String jsonPath)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "HEAD");

		return _getParsedResponse(request, jsonPath);
	}

	public static String patch(String requestString)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "PATCH");

		return request.send();
	}

	public static String patch(String requestString, String jsonPath)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "PATCH");

		return _getParsedResponse(request, jsonPath);
	}

	public static String post(String requestString)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "POST");

		return request.send();
	}

	public static String post(String requestString, String jsonPath)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "POST");

		return _getParsedResponse(request, jsonPath);
	}

	public static String put(String requestString)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "PUT");

		return request.send();
	}

	public static String put(String requestString, String jsonPath)
		throws IOException, TimeoutException {

		Request request = new Request(requestString, "PUT");

		return _getParsedResponse(request, jsonPath);
	}

	public static synchronized String synchronizedPost(String requestString)
		throws IOException, TimeoutException {

		return post(requestString);
	}

	public static synchronized String synchronizedPost(
			String requestString, String jsonPath)
		throws IOException, TimeoutException {

		return post(requestString, jsonPath);
	}

	private static String _getParsedResponse(Request request, String jsonPath)
		throws IOException, TimeoutException {

		String response = request.send();

		return JSONUtil.getWithJSONPath(response, jsonPath);
	}

	private static class Request {

		public Request(String requestString, String requestMethod) {
			_requestMethod = requestMethod;

			requestString = requestString.replaceAll("\\s+\\\\?\\s*\\n", " ");

			requestString = _encodeCurlData(requestString);

			List<String> tokens = _tokenize(requestString.trim());

			_requestURL = _getRequestURL(tokens);

			tokens.remove(_requestURL);

			_setRequestOptions(tokens);
		}

		public String send() throws IOException, TimeoutException {
			StringBuilder sb = new StringBuilder();

			sb.append("curl -X ");
			sb.append(_requestMethod);
			sb.append(" ");
			sb.append(_getRequestOptionsString());
			sb.append(" ");
			sb.append(_requestURL);

			Process process = ExecUtil.executeCommands(
				true, new File("."), 1000 * 60 * 15, sb.toString());

			InputStream inputStream = process.getInputStream();

			String response = ExecUtil.readInputStream(inputStream, true);

			_log(response);

			inputStream = process.getErrorStream();

			String errorString = ExecUtil.readInputStream(inputStream, true);

			_log(errorString);

			if (process.exitValue() != 0) {
				throw new RuntimeException(
					"Command finished with exit value: " + process.exitValue());
			}

			return response.replaceAll("\\\\r\\\\n|\\\\n|\\\\r", "%0A");
		}

		private String _encodeCurlData(String requestString) {
			Matcher matcher = _escapePattern.matcher(requestString);

			String encodedRequestString = requestString;

			while (matcher.find()) {
				String key = "$CURLDATA:" + StringUtil.randomString("10");

				_curlDataMap.put(key, matcher.group(1));

				encodedRequestString = StringUtil.replace(
					encodedRequestString, matcher.group(0), key);
			}

			return encodedRequestString;
		}

		private String _getRequestOptionsString() {
			return StringUtils.join(_requestOptions, " ");
		}

		private String _getRequestURL(List<String> tokens) {
			String token = tokens.get(0);

			if (token.startsWith("file") || token.startsWith("http")) {
				return token;
			}

			StringBuilder sb = new StringBuilder();

			sb.append("The curl URL must be the first argument of the ");
			sb.append("request string.\n'");
			sb.append(token);
			sb.append("' is an invalid URL.");

			throw new IllegalArgumentException(sb.toString());
		}

		private void _log(String message) {
			if (message == null) {
				message = "";
			}

			if (message.length() > _maxPrintLineLength) {
				System.out.println(
					message.substring(0, _maxPrintLineLength) + "...");

				return;
			}

			System.out.println(message);
		}

		private void _setRequestOptions(List<String> tokens) {
			for (int i = 0; i < tokens.size(); i++) {
				String optionType = tokens.get(i);

				Matcher optionTypeMatcher = _requestPattern.matcher(optionType);

				if (optionTypeMatcher.matches()) {
					String optionValue = "";

					if (i < (tokens.size() - 1)) {
						String nextToken = tokens.get(i + 1);

						optionTypeMatcher = _requestPattern.matcher(nextToken);

						if (!optionTypeMatcher.matches()) {
							if (nextToken.matches("\\$CURLDATA:\\w{10}")) {
								nextToken = _curlDataMap.get(nextToken);
							}

							optionValue = nextToken;

							i++;
						}
					}

					_requestOptions.add(
						new RequestOption(optionType, optionValue));
				}
			}
		}

		private List<String> _tokenize(String requestString) {
			List<String> tokens = new ArrayList<>();

			Matcher matcher = _requestPattern.matcher(requestString);

			int end = -1;
			int start = -1;

			while (matcher.find()) {
				if (!tokens.isEmpty()) {
					end = matcher.start();

					if ((end - start) > 1) {
						String miscellaneousToken = requestString.substring(
							start, end);

						tokens.add(miscellaneousToken.trim());
					}
				}

				String token = matcher.group(1);

				tokens.add(token.trim());

				start = matcher.end();
			}

			if (start != requestString.length()) {
				tokens.add(requestString.substring(start));
			}

			return tokens;
		}

		private static Pattern _escapePattern = Pattern.compile(
			"<CURL_DATA\\[([\\s\\S]*?)\\]CURL_DATA>");
		private static Pattern _requestPattern = Pattern.compile(
			"(-[\\w#:\\.]|--[\\w#:\\.-]{2,}|(?:[\\s]|^)(?:file|https?)" +
				":[^\\s]+)(\\s+|\\Z)");

		private Map<String, String> _curlDataMap = new HashMap<>();
		private final int _maxPrintLineLength = 2500;
		private final String _requestMethod;
		private List<RequestOption> _requestOptions = new ArrayList<>();
		private final String _requestURL;

	}

	private static class RequestOption {

		public RequestOption(String optionType, String optionValue) {
			_optionType = optionType;

			_optionValue = _formatOptionValue(optionType, optionValue);
		}

		public String getRequestOptionType() {
			return _customOptionsMap.getOrDefault(_optionType, _optionType);
		}

		public String toString() {
			StringBuilder sb = new StringBuilder();

			sb.append(getRequestOptionType());

			if (!_optionValue.isEmpty()) {
				sb.append(" ");
				sb.append(_escapeOptionValue(_optionValue));
			}

			return sb.toString();
		}

		private String _escapeOptionValue(String optionValue) {
			StringBuilder sb = new StringBuilder();

			if (OSDetector.isWindows()) {
				if (_isValidJSON(optionValue)) {
					optionValue = String.valueOf(new JSONObject(optionValue));
				}

				sb.append("\"");

				optionValue = optionValue.replaceAll(
					"\\\\\"", "\\\\\\\\\\\\\\\"");

				optionValue = optionValue.replaceAll("(?<!\\\\)\"", "\\\\\\\"");

				optionValue = StringUtil.replace(optionValue, "&", "^&");

				sb.append(optionValue);

				sb.append("\"");
			}
			else {
				sb.append("'");
				sb.append(optionValue);
				sb.append("'");
			}

			return sb.toString();
		}

		private String _formatOptionValue(
			String optionType, String optionValue) {

			optionValue = optionValue.trim();

			if ((optionValue.startsWith("'") && optionValue.endsWith("'")) ||
				(optionValue.startsWith("\"") && optionValue.endsWith("\""))) {

				optionValue = optionValue.substring(
					1, optionValue.length() - 1);
			}

			if (optionType.equals("--json-data")) {
				try {
					optionValue = String.valueOf(new JSONObject(optionValue));
				}
				catch (JSONException jsonException) {
					throw new RuntimeException(
						"Invalid JSON: '" + optionValue + "'");
				}
			}

			return optionValue;
		}

		private boolean _isValidJSON(String jsonTestString) {
			try {
				new JSONObject(jsonTestString);

				return true;
			}
			catch (JSONException jsonException) {
				return false;
			}
		}

		private static Map<String, String> _customOptionsMap =
			new HashMap<String, String>() {
				{
					put("--json-data", "--data");
				}
			};

		private final String _optionType;
		private final String _optionValue;

	}

}