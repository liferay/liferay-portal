/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.webdav.test;

import com.liferay.petra.string.CharPool;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.GroupConstants;
import com.liferay.portal.kernel.servlet.HttpHeaders;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.ContentTypes;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Tuple;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.kernel.webdav.WebDAVStorage;
import com.liferay.portal.kernel.webdav.WebDAVUtil;
import com.liferay.portal.kernel.webdav.methods.Method;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.webdav.WebDAVServlet;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Alexander Chow
 */
public class BaseWebDAVTestCase {

	public static void assertCode(int statusCode, Tuple tuple) {
		int returnedStatusCode = -1;

		if (tuple != null) {
			returnedStatusCode = getStatusCode(tuple);
		}

		Assert.assertEquals(statusCode, returnedStatusCode);
	}

	public static int getStatusCode(Tuple tuple) {
		return (Integer)tuple.getObject(0);
	}

	public Tuple service(
		String method, String path, Map<String, String> headers, byte[] data) {

		String requestURI = StringBundler.concat(
			_CONTEXT_PATH, _SERVLET_PATH, _PATH_INFO_PREFACE, path);

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest(method, requestURI);

		mockHttpServletRequest.setContextPath(_CONTEXT_PATH);
		mockHttpServletRequest.setServletPath(_SERVLET_PATH);
		mockHttpServletRequest.setPathInfo(_PATH_INFO_PREFACE + path);

		try {
			mockHttpServletRequest.setRemoteUser(
				String.valueOf(TestPropsValues.getUserId()));
		}
		catch (Exception exception) {
			Assert.fail("User ID cannot be initialized");
		}

		if (headers == null) {
			headers = new HashMap<>();
		}

		headers.put(HttpHeaders.USER_AGENT, getUserAgent());

		try {
			throw new Exception();
		}
		catch (Exception exception) {
			StackTraceElement[] stackTraceElements = exception.getStackTrace();

			for (StackTraceElement stackTraceElement : stackTraceElements) {
				String methodName = stackTraceElement.getMethodName();

				if (methodName.equals("setUp") ||
					methodName.equals("tearDown") ||
					methodName.startsWith("test")) {

					String testName = StringUtil.extractLast(
						stackTraceElement.getClassName(), CharPool.PERIOD);

					testName = StringUtil.removeSubstrings(
						testName, "WebDAV", "Test");

					headers.put(
						"X-Litmus",
						StringBundler.concat(
							testName, ": (", stackTraceElement.getMethodName(),
							":", stackTraceElement.getLineNumber(), ")"));

					break;
				}
			}
		}

		if (data != null) {
			mockHttpServletRequest.setContent(data);

			String contentType = headers.remove(HttpHeaders.CONTENT_TYPE);

			if (contentType != null) {
				mockHttpServletRequest.setContentType(contentType);
			}
			else {
				mockHttpServletRequest.setContentType(ContentTypes.TEXT_PLAIN);
			}
		}

		for (Map.Entry<String, String> entry : headers.entrySet()) {
			mockHttpServletRequest.addHeader(entry.getKey(), entry.getValue());
		}

		try {
			WebDAVServlet webDAVServlet = new WebDAVServlet();

			MockHttpServletResponse mockHttpServletResponse =
				new MockHttpServletResponse();

			webDAVServlet.service(
				mockHttpServletRequest, mockHttpServletResponse);

			int statusCode = mockHttpServletResponse.getStatus();
			byte[] responseBody =
				mockHttpServletResponse.getContentAsByteArray();

			Map<String, String> responseHeaders = new HashMap<>();

			for (String name : mockHttpServletResponse.getHeaderNames()) {
				responseHeaders.put(
					name, mockHttpServletResponse.getHeader(name));
			}

			return new Tuple(statusCode, responseBody, responseHeaders);
		}
		catch (Exception exception) {
			exception.printStackTrace();
		}

		return null;
	}

	public Tuple serviceCopyOrMove(
		String method, String path, Map<String, String> headers,
		String destination, int depth, boolean overwrite) {

		if (headers == null) {
			headers = new HashMap<>();
		}

		headers.put("Depth", getDepth(depth));
		headers.put("Destination", _PATH_INFO_PREFACE + destination);
		headers.put("Overwrite", getOverwrite(overwrite));

		return service(method, path, headers, null);
	}

	public Tuple serviceCopyOrMove(
		String method, String path, String destination) {

		return serviceCopyOrMove(method, path, destination, false);
	}

	public Tuple serviceCopyOrMove(
		String method, String path, String destination, boolean overwrite) {

		return serviceCopyOrMove(method, path, null, destination, 0, overwrite);
	}

	public Tuple serviceCopyOrMove(
		String method, String path, String destination, String lock) {

		Map<String, String> headers = null;

		if (Validator.isNotNull(lock)) {
			headers = HashMapBuilder.put(
				"If", "<opaquelocktoken:" + lock + ">"
			).build();
		}

		return serviceCopyOrMove(method, path, headers, destination, 0, false);
	}

	public Tuple serviceDelete(String name) {
		return service(Method.DELETE, name, null, null);
	}

	public Tuple serviceGet(String name) {
		return service(Method.GET, name, null, null);
	}

	public Tuple serviceLock(
		String path, Map<String, String> headers, int depth) {

		if (headers == null) {
			headers = new HashMap<>();
		}

		headers.put("Depth", getDepth(depth));
		headers.put("Timeout", "Second-" + 3600);

		return service(Method.LOCK, path, headers, _LOCK_XML.getBytes());
	}

	public Tuple servicePropFind(String name) {
		return service(Method.PROPFIND, name, null, _PROPFIND_XML.getBytes());
	}

	public Tuple servicePut(String name, byte[] data) {
		return servicePut(name, data, null);
	}

	public Tuple servicePut(String name, byte[] data, String lock) {
		Map<String, String> headers = null;

		if (Validator.isNotNull(lock)) {
			headers = HashMapBuilder.put(
				"If", "<opaquelocktoken:" + lock + ">"
			).build();
		}

		return service(Method.PUT, name, headers, data);
	}

	public Tuple serviceUnlock(String path, String lock) {
		Map<String, String> headers = null;

		if (Validator.isNotNull(lock)) {
			headers = HashMapBuilder.put(
				"Lock-Token", "<opaquelocktoken:" + lock + ">"
			).build();
		}

		return service(Method.UNLOCK, path, headers, null);
	}

	protected static String getDepth(int depth) {
		String depthString = "infinity";

		if (depth == 0) {
			depthString = "0";
		}

		return depthString;
	}

	protected static Map<String, String> getHeaders(Tuple tuple) {
		return (Map<String, String>)tuple.getObject(2);
	}

	protected static String getLock(Tuple tuple) {
		String token = "";

		Map<String, String> headers = getHeaders(tuple);

		String value = GetterUtil.getString(headers.get("Lock-Token"));

		int beg = value.indexOf(WebDAVUtil.TOKEN_PREFIX);

		if (beg >= 0) {
			beg += WebDAVUtil.TOKEN_PREFIX.length();

			if (beg < value.length()) {
				int end = value.indexOf(">", beg);

				token = GetterUtil.getString(value.substring(beg, end));
			}
		}

		return token;
	}

	protected static String getOverwrite(boolean overwrite) {
		String overwriteString = "F";

		if (overwrite) {
			overwriteString = "T";
		}

		return overwriteString;
	}

	protected static byte[] getResponseBody(Tuple tuple) {
		return (byte[])tuple.getObject(1);
	}

	protected static String getResponseBodyString(Tuple tuple) {
		byte[] data = getResponseBody(tuple);

		return new String(data);
	}

	protected String getFolderName() {
		return _FOLDER_NAME;
	}

	protected String getUserAgent() {
		return _DEFAULT_USER_AGENT;
	}

	private static final String _CONTEXT_PATH = "/webdav";

	private static final String _DEFAULT_USER_AGENT = "Liferay-litmus";

	private static final String _FOLDER_NAME = "WebDAVTest";

	private static final String _LOCK_XML;

	private static final String _PATH_INFO_PREFACE = StringBundler.concat(
		GroupConstants.GUEST_FRIENDLY_URL, "/document_library/", _FOLDER_NAME,
		"/");

	private static final String _PROPFIND_XML;

	private static final String _SERVLET_PATH = "";

	@Inject
	private static WebDAVStorage _webDAVStorage;

	static {
		_LOCK_XML = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n",
			"<D:lockinfo xmlns:D='DAV:'>\n",
			"<D:lockscope><D:exclusive/></D:lockscope>\n",
			"<D:locktype><D:write/></D:locktype>\n", "<D:owner>\n",
			"<D:href>http://www.liferay.com</D:href>\n", "</D:owner>\n",
			"</D:lockinfo>\n");

		_PROPFIND_XML = StringBundler.concat(
			"<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n",
			"<D:propfind xmlns:D=\"DAV:\">\n", "<D:allprop/>\n",
			"</D:propfind>");
	}

}