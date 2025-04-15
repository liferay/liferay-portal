/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test.util;

import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.io.WriterOutputStream;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portal.kernel.util.ProgressTracker;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portal.upload.LiferayServletRequest;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockMultipartHttpServletRequest;

/**
 * @author Manuel de la Peña
 */
public class PortletContainerTestUtil {

	public static Map<String, FileItem[]> getFileParameters(
			int size, byte[] bytes)
		throws Exception {

		return getFileParameters(size, null, bytes);
	}

	public static Map<String, FileItem[]> getFileParameters(
			int size, String namespace, byte[] bytes)
		throws Exception {

		Map<String, FileItem[]> fileParameters = new HashMap<>();

		for (int i = 0; i < size; i++) {
			String fileParameter = "fileParameter" + i;

			if (namespace != null) {
				fileParameter = namespace.concat(fileParameter);
			}

			FileItem[] fileItems = new FileItem[2];

			for (int j = 0; j < fileItems.length; j++) {
				fileItems[j] = _createFileItem(bytes);
			}

			fileParameters.put(fileParameter, fileItems);
		}

		return fileParameters;
	}

	public static HttpServletRequest getHttpServletRequest(
			Group group, Layout layout)
		throws PortalException {

		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		httpServletRequest.setAttribute(WebKeys.LAYOUT, layout);

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setCompany(
			CompanyLocalServiceUtil.getCompany(layout.getCompanyId()));
		themeDisplay.setLayout(layout);
		themeDisplay.setLayoutSet(layout.getLayoutSet());
		themeDisplay.setPlid(layout.getPlid());
		themeDisplay.setPortalURL(TestPropsValues.PORTAL_URL);
		themeDisplay.setRequest(httpServletRequest);
		themeDisplay.setScopeGroupId(group.getGroupId());
		themeDisplay.setSiteGroupId(group.getGroupId());
		themeDisplay.setUser(TestPropsValues.getUser());

		httpServletRequest.setAttribute(WebKeys.THEME_DISPLAY, themeDisplay);

		return httpServletRequest;
	}

	public static LiferayServletRequest getMultipartRequest(
		String fileNameParameter, byte[] bytes) {

		MockMultipartHttpServletRequest mockMultipartHttpServletRequest =
			new MockMultipartHttpServletRequest();

		mockMultipartHttpServletRequest.addFile(
			new MockMultipartFile(fileNameParameter, bytes));
		mockMultipartHttpServletRequest.setContent(bytes);
		mockMultipartHttpServletRequest.setContentType(
			"multipart/form-data;boundary=" + System.currentTimeMillis());
		mockMultipartHttpServletRequest.setCharacterEncoding("UTF-8");

		MockHttpSession mockHttpSession = new MockHttpSession();

		mockHttpSession.setAttribute(ProgressTracker.PERCENT, new Object());

		mockMultipartHttpServletRequest.setSession(mockHttpSession);

		return new LiferayServletRequest(mockMultipartHttpServletRequest);
	}

	public static Response getPortalAuthentication(
			HttpServletRequest httpServletRequest, Layout layout,
			String portletId)
		throws Exception {

		// Get the portal authentication token by making a resource request

		PortletURL portletURL = PortletURLFactoryUtil.create(
			httpServletRequest, portletId, layout.getPlid(),
			PortletRequest.RESOURCE_PHASE);

		return request(portletURL.toString());
	}

	public static Map<String, List<String>> getRegularParameters(int size) {
		Map<String, List<String>> regularParameters = new HashMap<>();

		for (int i = 0; i < size; i++) {
			List<String> items = new ArrayList<>();

			for (int j = 0; j < 10; j++) {
				items.add(RandomTestUtil.randomString());
			}

			regularParameters.put("regularParameter" + i, items);
		}

		return regularParameters;
	}

	public static Response postMultipart(
			String url,
			MockMultipartHttpServletRequest mockMultipartHttpServletRequest,
			String fileNameParameter)
		throws IOException {

		if (mockMultipartHttpServletRequest.getInputStream() == null) {
			throw new IllegalStateException("Input stream is null");
		}

		String[] cookies = mockMultipartHttpServletRequest.getParameterValues(
			"Cookie");

		if (ArrayUtil.isEmpty(cookies)) {
			throw new IllegalStateException("Cookie is null");
		}

		CloseableHttpResponse closeableHttpResponse = null;

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		try (CloseableHttpClient closeableHttpClient =
				httpClientBuilder.build();
			StringWriter stringWriter = new StringWriter();
			WriterOutputStream writerOutputStream = new WriterOutputStream(
				stringWriter)) {

			RequestBuilder requestBuilder = RequestBuilder.post(url);

			for (String cookie : cookies) {
				requestBuilder.addHeader(new BasicHeader("Cookie", cookie));
			}

			byte[] bytes = FileUtil.getBytes(
				mockMultipartHttpServletRequest.getInputStream());

			MultipartEntityBuilder multipartEntityBuilder =
				MultipartEntityBuilder.create();

			ByteArrayBody byteArrayBody = new ByteArrayBody(
				bytes, ContentType.DEFAULT_BINARY, fileNameParameter);

			multipartEntityBuilder.addPart(fileNameParameter, byteArrayBody);

			requestBuilder.setEntity(multipartEntityBuilder.build());

			URI uri = requestBuilder.getUri();

			closeableHttpResponse = closeableHttpClient.execute(
				new HttpHost(uri.getHost(), uri.getPort()),
				requestBuilder.build());

			StatusLine statusLine = closeableHttpResponse.getStatusLine();

			HttpEntity httpEntity = closeableHttpResponse.getEntity();

			httpEntity.writeTo(writerOutputStream);

			writerOutputStream.flush();

			return new Response(
				statusLine.getStatusCode(), stringWriter.toString(), null);
		}
		finally {
			try {
				if (closeableHttpResponse != null) {
					closeableHttpResponse.close();
				}
			}
			catch (IOException ioException) {
				if (_log.isWarnEnabled()) {
					_log.warn(ioException);
				}
			}
		}
	}

	public static Response request(String url) throws IOException {
		return request(url, Collections.<String, List<String>>emptyMap());
	}

	public static Response request(
			String url, Map<String, List<String>> headers)
		throws IOException {

		URL urlObject = new URL(url);

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)urlObject.openConnection();

		httpURLConnection.setConnectTimeout(1500 * 1000);
		httpURLConnection.setInstanceFollowRedirects(true);
		httpURLConnection.setReadTimeout(1500 * 1000);

		for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
			String key = entry.getKey();

			for (String value : entry.getValue()) {
				if (key.equals("Cookie")) {
					httpURLConnection.addRequestProperty(
						key, value.split(";", 2)[0]);
				}
				else {
					httpURLConnection.setRequestProperty(key, value);
				}
			}
		}

		try (InputStream inputStream = httpURLConnection.getInputStream()) {
			Map<String, List<String>> headerFields =
				httpURLConnection.getHeaderFields();

			return new Response(
				httpURLConnection.getResponseCode(),
				StringUtil.read(inputStream), headerFields.get("Set-Cookie"));
		}
		catch (IOException ioException) {
			try (InputStream inputStream = httpURLConnection.getErrorStream()) {
				if (inputStream != null) {
					while (inputStream.read() != -1);
				}
			}

			return new Response(
				httpURLConnection.getResponseCode(), null, null);
		}
		finally {
			httpURLConnection.disconnect();
		}
	}

	public static class Response {

		public String getBody() {
			return _body;
		}

		public int getCode() {
			return _code;
		}

		public List<String> getCookies() {
			return _cookies;
		}

		private Response(int code, String body, List<String> cookies) {
			_code = code;
			_body = body;
			_cookies = cookies;
		}

		private final String _body;
		private final int _code;
		private final List<String> _cookies;

	}

	private static FileItem _createFileItem(byte[] bytes) throws Exception {
		Path tempFilePath = Files.createTempFile(null, null);

		Files.write(tempFilePath, bytes);

		File tempFile = tempFilePath.toFile();

		FinalizeManager.register(
			tempFile, new DeleteFileFinalizeAction(tempFile.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);

		String contentType = RandomTestUtil.randomString();

		return ProxyUtil.newDelegateProxyInstance(
			FileItem.class.getClassLoader(), FileItem.class,
			new Object() {

				public void delete() {
					tempFile.delete();
				}

				public String getContentType() {
					return contentType;
				}

				public String getFileName() {
					return tempFile.getName();
				}

				public String getFullFileName() {
					return tempFile.getName();
				}

				public InputStream getInputStream() throws IOException {
					return new FileInputStream(tempFile);
				}

				public long getSize() {
					return bytes.length;
				}

				public int getSizeThreshold() {
					return 1024;
				}

				public File getStoreLocation() {
					return tempFile;
				}

				public boolean isFormField() {
					return true;
				}

				public boolean isInMemory() {
					return false;
				}

			},
			null);
	}

	private static final Log _log = LogFactoryUtil.getLog(
		PortletContainerTestUtil.class);

}