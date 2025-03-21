/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.client.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.lang.reflect.Field;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import java.util.Base64;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;

import jakarta.annotation.Generated;

/**
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public class HttpInvoker {

	public static HttpInvoker newHttpInvoker() {
		return new HttpInvoker();
	}

	public HttpInvoker body(String body, String contentType) {
		_body = body;
		_contentType = contentType;

		return this;
	}

	public HttpInvoker header(String name, String value) {
		_headers.put(name, value);

		return this;
	}

	public HttpInvoker httpMethod(HttpMethod httpMethod) {
		_httpMethod = httpMethod;

		return this;
	}

	public HttpResponse invoke() throws IOException {
		HttpResponse httpResponse = new HttpResponse();

		HttpURLConnection httpURLConnection = _openHttpURLConnection();

		byte[] binaryContent = _readResponse(httpURLConnection);

		httpResponse.setBinaryContent(binaryContent);
		httpResponse.setContent(new String(binaryContent));

		httpResponse.setContentType(
			httpURLConnection.getHeaderField("Content-Type"));
		httpResponse.setMessage(httpURLConnection.getResponseMessage());
		httpResponse.setStatusCode(httpURLConnection.getResponseCode());

		httpURLConnection.disconnect();

		return httpResponse;
	}

	public HttpInvoker multipart() {
		_contentType =
			"multipart/form-data; charset=utf-8; boundary=__MULTIPART_BOUNDARY__";
		_multipartBoundary = "__MULTIPART_BOUNDARY__";

		return this;
	}

	public HttpInvoker parameter(String name, String value) {
		return parameter(name, new String[] {value});
	}

	public HttpInvoker parameter(String name, String[] values) {
		String[] oldValues = _parameters.get(name);

		if (oldValues != null) {
			String[] newValues = new String[oldValues.length + values.length];

			System.arraycopy(oldValues, 0, newValues, 0, oldValues.length);
			System.arraycopy(
				values, 0, newValues, oldValues.length, values.length);

			_parameters.put(name, newValues);
		}
		else {
			_parameters.put(name, values);
		}

		return this;
	}

	public HttpInvoker part(String name, File file) {
		_files.put(name, file);

		return this;
	}

	public HttpInvoker part(String name, String value) {
		_parts.put(name, value);

		return this;
	}

	public HttpInvoker path(String path) {
		_path = path;

		return this;
	}

	public HttpInvoker path(String name, Object value) {
		_path = _path.replaceFirst(
			"\\{" + name + "\\}",
			Matcher.quoteReplacement(String.valueOf(value)));

		return this;
	}

	public HttpInvoker userNameAndPassword(String userNameAndPassword)
		throws IOException {

		Base64.Encoder encoder = Base64.getEncoder();

		_encodedUserNameAndPassword = new String(
			encoder.encode(userNameAndPassword.getBytes("UTF-8")), "UTF-8");

		return this;
	}

	public enum HttpMethod {

		DELETE, GET, PATCH, POST, PUT

	}

	public class HttpResponse {

		public byte[] getBinaryContent() {
			return _binaryContent;
		}

		public String getContent() {
			return _content;
		}

		public String getContentType() {
			return _contentType;
		}

		public String getMessage() {
			return _message;
		}

		public int getStatusCode() {
			return _statusCode;
		}

		public void setBinaryContent(byte[] binaryContent) {
			_binaryContent = binaryContent;
		}

		public void setContent(String content) {
			_content = content;
		}

		public void setContentType(String contentType) {
			_contentType = contentType;
		}

		public void setMessage(String message) {
			_message = message;
		}

		public void setStatusCode(int statusCode) {
			_statusCode = statusCode;
		}

		private byte[] _binaryContent;
		private String _content;
		private String _contentType;
		private String _message;
		private int _statusCode;

	}

	private HttpInvoker() {
	}

	private void _appendPart(
			OutputStream outputStream, PrintWriter printWriter, String key,
			Object value)
		throws IOException {

		printWriter.append("\r\n--");
		printWriter.append(_multipartBoundary);
		printWriter.append("\r\nContent-Disposition: form-data; name=\"");
		printWriter.append(key);
		printWriter.append("\";");

		if (value instanceof File) {
			File file = (File)value;

			printWriter.append(" filename=\"");
			printWriter.append(_filter(file.getName()));
			printWriter.append("\"\r\nContent-Type: ");
			printWriter.append(
				URLConnection.guessContentTypeFromName(file.getName()));
			printWriter.append("\r\n\r\n");

			printWriter.flush();

			byte[] buffer = new byte[4096];
			FileInputStream fileInputStream = new FileInputStream(file);
			int read = -1;

			while ((read = fileInputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, read);
			}

			outputStream.flush();

			fileInputStream.close();
		}
		else {
			printWriter.append("\r\n\r\n");
			printWriter.append(value.toString());
		}

		printWriter.append("\r\n");
	}

	private String _filter(String fileName) {
		fileName = fileName.replaceAll("\"", "");
		fileName = fileName.replaceAll("\n", "");
		fileName = fileName.replaceAll("\r", "");

		return fileName;
	}

	private HttpURLConnection _getHttpURLConnection(
			HttpMethod httpMethod, String urlString)
		throws IOException {

		URL url = new URL(urlString);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		try {
			HttpURLConnection methodHttpURLConnection = httpURLConnection;

			if (Objects.equals(url.getProtocol(), "https")) {
				Class<?> clazz = httpURLConnection.getClass();

				Field field = clazz.getDeclaredField("delegate");

				field.setAccessible(true);

				methodHttpURLConnection = (HttpURLConnection)field.get(
					httpURLConnection);
			}

			_methodField.set(methodHttpURLConnection, httpMethod.name());
		}
		catch (ReflectiveOperationException reflectiveOperationException) {
			throw new IOException(reflectiveOperationException);
		}

		return httpURLConnection;
	}

	private String _getQueryString() throws IOException {
		StringBuilder sb = new StringBuilder();

		Set<Map.Entry<String, String[]>> set = _parameters.entrySet();

		Iterator<Map.Entry<String, String[]>> iterator = set.iterator();

		while (iterator.hasNext()) {
			Map.Entry<String, String[]> entry = iterator.next();

			String[] values = entry.getValue();

			for (int i = 0; i < values.length; i++) {
				String name = URLEncoder.encode(entry.getKey(), "UTF-8");

				sb.append(name);

				sb.append("=");

				String value = URLEncoder.encode(values[i], "UTF-8");

				sb.append(value);

				if ((i + 1) < values.length) {
					sb.append("&");
				}
			}

			if (iterator.hasNext()) {
				sb.append("&");
			}
		}

		return sb.toString();
	}

	private HttpURLConnection _openHttpURLConnection() throws IOException {
		String urlString = _path;

		String queryString = _getQueryString();

		if (queryString.length() > 0) {
			if (!urlString.contains("?")) {
				urlString += "?";
			}

			urlString += queryString;
		}

		HttpURLConnection httpURLConnection = _getHttpURLConnection(
			_httpMethod, urlString);

		if (_encodedUserNameAndPassword != null) {
			httpURLConnection.setRequestProperty(
				"Authorization", "Basic " + _encodedUserNameAndPassword);
		}

		if (_contentType != null) {
			httpURLConnection.setRequestProperty("Content-Type", _contentType);
		}

		for (Map.Entry<String, String> header : _headers.entrySet()) {
			httpURLConnection.setRequestProperty(
				header.getKey(), header.getValue());
		}

		_writeBody(httpURLConnection);

		return httpURLConnection;
	}

	private byte[] _readResponse(HttpURLConnection httpURLConnection)
		throws IOException {

		ByteArrayOutputStream byteArrayOutputStream =
			new ByteArrayOutputStream();

		InputStream inputStream = null;

		int responseCode = httpURLConnection.getResponseCode();

		if (responseCode > 299) {
			inputStream = httpURLConnection.getErrorStream();
		}
		else {
			inputStream = httpURLConnection.getInputStream();
		}

		if (inputStream != null) {
			byte[] bytes = new byte[8192];

			while (true) {
				int read = inputStream.read(bytes, 0, bytes.length);

				if (read == -1) {
					break;
				}

				byteArrayOutputStream.write(bytes, 0, read);
			}
		}

		byteArrayOutputStream.flush();

		return byteArrayOutputStream.toByteArray();
	}

	private void _writeBody(HttpURLConnection httpURLConnection)
		throws IOException {

		if ((_body == null) && _files.isEmpty() && _parts.isEmpty()) {
			return;
		}

		httpURLConnection.setDoOutput(true);

		OutputStream outputStream = httpURLConnection.getOutputStream();

		try (PrintWriter printWriter = new PrintWriter(
				new OutputStreamWriter(outputStream, "UTF-8"), true)) {

			if (_contentType.startsWith("multipart/form-data")) {
				for (Map.Entry<String, String> entry : _parts.entrySet()) {
					_appendPart(
						outputStream, printWriter, entry.getKey(),
						entry.getValue());
				}

				for (Map.Entry<String, File> entry : _files.entrySet()) {
					_appendPart(
						outputStream, printWriter, entry.getKey(),
						entry.getValue());
				}

				printWriter.append("--" + _multipartBoundary + "--");

				printWriter.flush();

				outputStream.flush();
			}
			else {
				printWriter.append(_body);

				printWriter.flush();
			}
		}
	}

	private static final Field _methodField;

	static {
		try {
			_methodField = HttpURLConnection.class.getDeclaredField("method");

			_methodField.setAccessible(true);
		}
		catch (Exception exception) {
			throw new ExceptionInInitializerError(exception);
		}
	}

	private String _body;
	private String _contentType;
	private String _encodedUserNameAndPassword;
	private final Map<String, File> _files = new LinkedHashMap<>();
	private final Map<String, String> _headers = new LinkedHashMap<>();
	private HttpMethod _httpMethod = HttpMethod.GET;
	private String _multipartBoundary;
	private final Map<String, String[]> _parameters = new LinkedHashMap<>();
	private final Map<String, String> _parts = new LinkedHashMap<>();
	private String _path;

}