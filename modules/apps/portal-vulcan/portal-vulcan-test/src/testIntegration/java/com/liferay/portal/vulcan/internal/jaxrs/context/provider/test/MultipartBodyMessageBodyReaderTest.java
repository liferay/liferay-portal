/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.jaxrs.context.provider.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.io.StreamUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.vulcan.multipart.BinaryFile;
import com.liferay.portal.vulcan.multipart.MultipartBody;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.StandardCharsets;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class MultipartBodyMessageBodyReaderTest {

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			MultipartBodyMessageBodyReaderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", false
			).put(
				"liferay.jackson", false
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test-vulcan"
			).put(
				"osgi.jaxrs.extension.select",
				"(osgi.jaxrs.name=Liferay.Vulcan)"
			).build());
	}

	@After
	public void tearDown() {
		_serviceRegistration.unregister();
	}

	@Test
	public void testPostMultipart() throws Exception {
		URL url = new URL("http://localhost:8080/o/test-vulcan/test-class");

		HttpURLConnection httpURLConnection =
			(HttpURLConnection)url.openConnection();

		httpURLConnection.setChunkedStreamingMode(50);
		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod(Http.Method.POST.name());

		String boundary = "__MULTIPART_BOUNDARY__";

		httpURLConnection.setRequestProperty(
			"Content-Type", "multipart/form-data; boundary=" + boundary);

		try (OutputStream outputStream = httpURLConnection.getOutputStream();
			PrintWriter printWriter = new PrintWriter(
				new OutputStreamWriter(outputStream, StandardCharsets.UTF_8),
				true)) {

			printWriter.append("--" + boundary + "\r\n");
			printWriter.append(
				"Content-Disposition: form-data; name=\"param\"\r\n");
			printWriter.append(
				"Content-Type: text/plain; charset=UTF-8\r\n\r\n");
			printWriter.append("value\r\n");
			printWriter.append("--" + boundary + "\r\n");
			printWriter.append("Content-Disposition: form-data;");
			printWriter.append(" name=\"file\"; filename=\"filename.txt\"\r\n");
			printWriter.append(
				"Content-Type: text/plain; charset=UTF-8\r\n\r\n");
			printWriter.append("file content\r\n");
			printWriter.append("--" + boundary + "--\r\n");
			printWriter.flush();
		}

		Assert.assertEquals(200, httpURLConnection.getResponseCode());
	}

	public static class TestApplication extends Application {

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@Consumes("multipart/form-data")
		@Path("/test-class")
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		public Response testClass(MultipartBody multipartBody)
			throws Exception {

			BinaryFile binaryFile = multipartBody.getBinaryFile("file");
			Map<String, String> values = multipartBody.getValues();

			if ((binaryFile == null) ||
				!StringUtil.equals("text/plain", binaryFile.getContentType()) ||
				!StringUtil.equals("filename.txt", binaryFile.getFileName()) ||
				!StringUtil.equals(
					"file content",
					StreamUtil.toString(binaryFile.getInputStream())) ||
				(values == null) ||
				!StringUtil.equals(values.get("param"), "value")) {

				return Response.serverError(
				).build();
			}

			return Response.ok(
			).build();
		}

	}

	private ServiceRegistration<Application> _serviceRegistration;

}