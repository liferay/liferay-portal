/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.kernel.util;

import jakarta.servlet.http.Cookie;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.annotation.versioning.ProviderType;

/**
 * @author Brian Wing Shun Chan
 * @author Hugo Huijser
 */
@ProviderType
public interface Http {

	public static final String HTTP = "http";

	public static final int HTTP_PORT = 80;

	public static final String HTTP_WITH_SLASH = "http://";

	public static final String HTTPS = "https";

	public static final int HTTPS_PORT = 443;

	public static final String HTTPS_WITH_SLASH = "https://";

	public static final String PROTOCOL_DELIMITER = "://";

	public static final int URL_MAXIMUM_LENGTH = 2083;

	public Cookie[] getCookies();

	public boolean hasProxyConfig();

	public boolean isNonProxyHost(String host);

	public boolean isProxyHost(String host);

	public byte[] URLtoByteArray(Http.Options options) throws IOException;

	public byte[] URLtoByteArray(String location) throws IOException;

	public byte[] URLtoByteArray(String location, boolean post)
		throws IOException;

	public InputStream URLtoInputStream(Http.Options options)
		throws IOException;

	public InputStream URLtoInputStream(String location) throws IOException;

	public InputStream URLtoInputStream(String location, boolean post)
		throws IOException;

	public String URLtoString(Http.Options options) throws IOException;

	public String URLtoString(String location) throws IOException;

	public String URLtoString(String location, boolean post) throws IOException;

	/**
	 * This method only uses the default Commons HttpClient implementation when
	 * the URL object represents a HTTP resource. The URL object could also
	 * represent a file or some JNDI resource. In that case, the default Java
	 * implementation is used.
	 *
	 * @param  url the URL
	 * @return A string representation of the resource referenced by the URL
	 *         object
	 * @throws IOException if an IO exception occurred
	 */
	public String URLtoString(URL url) throws IOException;

	public class Auth {

		public Auth(
			String host, int port, String realm, String username,
			String password) {

			_host = host;
			_port = port;
			_realm = realm;
			_username = username;
			_password = password;
		}

		public String getHost() {
			return _host;
		}

		public String getPassword() {
			return _password;
		}

		public int getPort() {
			return _port;
		}

		public String getRealm() {
			return _realm;
		}

		public String getUsername() {
			return _username;
		}

		private final String _host;
		private final String _password;
		private final int _port;
		private final String _realm;
		private final String _username;

	}

	public class Body {

		public Body(String content, String contentType, String charset) {
			_content = content;
			_contentType = contentType;
			_charset = charset;
		}

		public String getCharset() {
			return _charset;
		}

		public String getContent() {
			return _content;
		}

		public String getContentType() {
			return _contentType;
		}

		private final String _charset;
		private final String _content;
		private String _contentType;

	}

	public enum CookieSpec {

		DEFAULT, IGNORE_COOKIES, NETSCAPE, STANDARD, STANDARD_STRICT

	}

	public class FilePart {

		public FilePart(
			String name, String fileName, byte[] value, String contentType,
			String charSet) {

			_name = name;
			_fileName = fileName;
			_value = value;
			_contentType = contentType;
			_charSet = charSet;
		}

		public String getCharSet() {
			return _charSet;
		}

		public String getContentType() {
			return _contentType;
		}

		public String getFileName() {
			return _fileName;
		}

		public String getName() {
			return _name;
		}

		public byte[] getValue() {
			return _value;
		}

		private final String _charSet;
		private String _contentType;
		private final String _fileName;
		private final String _name;
		private final byte[] _value;

	}

	public class InputStreamPart {

		public InputStreamPart(
			String name, String inputStreamName, InputStream inputStream,
			String contentType) {

			_name = name;
			_inputStreamName = inputStreamName;
			_inputStream = inputStream;
			_contentType = contentType;
		}

		public String getContentType() {
			return _contentType;
		}

		public InputStream getInputStream() {
			return _inputStream;
		}

		public String getInputStreamName() {
			return _inputStreamName;
		}

		public String getName() {
			return _name;
		}

		private final String _contentType;
		private final InputStream _inputStream;
		private final String _inputStreamName;
		private final String _name;

	}

	public enum Method {

		DELETE, GET, HEAD, PATCH, POST, PUT

	}

	public class Options {

		public void addFilePart(
			String name, String fileName, byte[] value, String contentType,
			String charSet) {

			if (_body != null) {
				throw new IllegalArgumentException(
					"File part cannot be added because a body has already " +
						"been set");
			}

			if (_fileParts == null) {
				_fileParts = new ArrayList<>();
			}

			FilePart filePart = new FilePart(
				name, fileName, value, contentType, charSet);

			_fileParts.add(filePart);
		}

		public void addHeader(String name, String value) {
			if (_headers == null) {
				_headers = new HashMap<>();
			}

			_headers.put(name, value);
		}

		public void addInputStreamPart(
			String name, String inputStreamName, InputStream inputStream,
			String contentType) {

			if (_body != null) {
				throw new IllegalArgumentException(
					"Input stream part cannot be added because a body has " +
						"already been set");
			}

			if (_inputStreamParts == null) {
				_inputStreamParts = new ArrayList<>();
			}

			InputStreamPart inputStreamPart = new InputStreamPart(
				name, inputStreamName, inputStream, contentType);

			_inputStreamParts.add(inputStreamPart);
		}

		public void addPart(String name, String value) {
			if (_body != null) {
				throw new IllegalArgumentException(
					"Part cannot be added because a body has already been set");
			}

			if (_parts == null) {
				_parts = new HashMap<>();
			}

			_parts.put(name, value);
		}

		public Auth getAuth() {
			return _auth;
		}

		public Body getBody() {
			return _body;
		}

		public Cookie[] getCookies() {
			return _cookies;
		}

		public CookieSpec getCookieSpec() {
			return _cookieSpec;
		}

		public List<FilePart> getFileParts() {
			return _fileParts;
		}

		public String getHeader(String name) {
			if (_headers == null) {
				return null;
			}

			return _headers.get(name);
		}

		public Map<String, String> getHeaders() {
			return _headers;
		}

		public List<InputStreamPart> getInputStreamParts() {
			return _inputStreamParts;
		}

		public String getLocation() {
			return _location;
		}

		public Method getMethod() {
			return _method;
		}

		public Map<String, String> getParts() {
			return _parts;
		}

		public Response getResponse() {
			return _response;
		}

		public int getTimeout() {
			return _timeout;
		}

		public boolean isDelete() {
			if (_method == Method.DELETE) {
				return true;
			}

			return false;
		}

		public boolean isFollowRedirects() {
			return _followRedirects;
		}

		public boolean isGet() {
			if (_method == Method.GET) {
				return true;
			}

			return false;
		}

		public boolean isHead() {
			if (_method == Method.HEAD) {
				return true;
			}

			return false;
		}

		public boolean isNormalizeURI() {
			return _normalizeURI;
		}

		public boolean isPatch() {
			if (_method == Method.PATCH) {
				return true;
			}

			return false;
		}

		public boolean isPost() {
			if (_method == Method.POST) {
				return true;
			}

			return false;
		}

		public boolean isPut() {
			if (_method == Method.PUT) {
				return true;
			}

			return false;
		}

		public void setAuth(Http.Auth auth) {
			setAuth(
				auth.getHost(), auth.getPort(), auth.getRealm(),
				auth.getUsername(), auth.getPassword());
		}

		public void setAuth(
			String host, int port, String realm, String username,
			String password) {

			_auth = new Auth(host, port, realm, username, password);
		}

		public void setBody(Http.Body body) {
			setBody(
				body.getContent(), body.getContentType(), body.getCharset());
		}

		public void setBody(
			String content, String contentType, String charset) {

			if (_parts != null) {
				throw new IllegalArgumentException(
					"Body cannot be set because a part has already been added");
			}

			_body = new Body(content, contentType, charset);
		}

		public void setCookies(Cookie[] cookies) {
			_cookies = cookies;
		}

		public void setCookieSpec(Http.CookieSpec cookieSpec) {
			if (cookieSpec != null) {
				_cookieSpec = cookieSpec;
			}
		}

		public void setDelete(boolean delete) {
			if (delete) {
				_method = Method.DELETE;
			}
			else {
				_method = Method.GET;
			}
		}

		public void setFileParts(List<FilePart> fileParts) {
			_fileParts = fileParts;
		}

		public void setFollowRedirects(boolean followRedirects) {
			_followRedirects = followRedirects;
		}

		public void setHead(boolean head) {
			if (head) {
				_method = Method.HEAD;
			}
			else {
				_method = Method.GET;
			}
		}

		public void setHeaders(Map<String, String> headers) {
			_headers = headers;
		}

		public void setInputStreamParts(
			List<InputStreamPart> inputStreamParts) {

			_inputStreamParts = inputStreamParts;
		}

		public void setLocation(String location) {
			_location = location;
		}

		public void setMethod(Method method) {
			if (method != null) {
				_method = method;
			}
		}

		public void setNormalizeURI(boolean normalizeURI) {
			_normalizeURI = normalizeURI;
		}

		public void setParts(Map<String, String> parts) {
			_parts = parts;
		}

		public void setPatch(boolean patch) {
			if (patch) {
				_method = Method.PATCH;
			}
			else {
				_method = Method.GET;
			}
		}

		public void setPost(boolean post) {
			if (post) {
				_method = Method.POST;
			}
			else {
				_method = Method.GET;
			}
		}

		public void setPut(boolean put) {
			if (put) {
				_method = Method.PUT;
			}
			else {
				_method = Method.GET;
			}
		}

		public void setResponse(Response response) {
			_response = response;
		}

		public void setTimeout(int timeout) {
			_timeout = timeout;
		}

		private Auth _auth;
		private Body _body;
		private Cookie[] _cookies;
		private CookieSpec _cookieSpec;
		private List<FilePart> _fileParts;
		private boolean _followRedirects = true;
		private Map<String, String> _headers;
		private List<InputStreamPart> _inputStreamParts;
		private String _location;
		private Method _method = Method.GET;
		private boolean _normalizeURI = true;
		private Map<String, String> _parts;
		private Response _response = new Response();
		private int _timeout;

	}

	public class Response {

		public void addHeader(String name, String value) {
			if (_headers == null) {
				_headers = new HashMap<>();
			}

			_headers.put(StringUtil.toLowerCase(name), value);
		}

		public int getContentLength() {
			return _contentLength;
		}

		public long getContentLengthLong() {
			return _contentLengthLong;
		}

		public String getContentType() {
			return _contentType;
		}

		public String getHeader(String name) {
			if (_headers == null) {
				return null;
			}

			return _headers.get(StringUtil.toLowerCase(name));
		}

		public Map<String, String> getHeaders() {
			return _headers;
		}

		public String getRedirect() {
			return _redirect;
		}

		public int getResponseCode() {
			return _responseCode;
		}

		public void setContentLength(int contentLength) {
			_contentLength = contentLength;
		}

		public void setContentLengthLong(long contentLengthLong) {
			_contentLengthLong = contentLengthLong;
		}

		public void setContentType(String contentType) {
			_contentType = contentType;
		}

		public void setHeaders(Map<String, String> headers) {
			_headers = headers;
		}

		public void setRedirect(String redirect) {
			_redirect = redirect;
		}

		public void setResponseCode(int responseCode) {
			_responseCode = responseCode;
		}

		private int _contentLength = -1;
		private long _contentLengthLong = -1;
		private String _contentType;
		private Map<String, String> _headers;
		private String _redirect;
		private int _responseCode = -1;

	}

}