/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.memory.DeleteFileFinalizeAction;
import com.liferay.petra.memory.FinalizeManager;
import com.liferay.portal.kernel.exception.NoSuchModelException;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.security.auth.PrincipalException;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.test.FinalizeManagerUtil;
import com.liferay.portal.kernel.test.GCUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsValuesTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.upload.FileItem;
import com.liferay.portal.kernel.upload.UploadServletRequest;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LinkedHashMapBuilder;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Path;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.After;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Igor Spasic
 * @author Raymond Augé
 */
public class JSONWebServiceServiceActionTest
	extends BaseJSONWebServiceTestCase {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		initPortalServices();

		ReflectionTestUtil.setFieldValue(
			jsonWebServiceActionsManager, "_portal",
			Mockito.mock(Portal.class));

		_jsonWebServiceServiceAction = new JSONWebServiceServiceAction(
			jsonWebServiceActionsManager);
	}

	@After
	public void tearDown() throws InterruptedException {
		GCUtil.gc(true);

		FinalizeManagerUtil.drainPendingFinalizeActions();
	}

	@Test
	public void testInvokerNullCall() throws Exception {
		registerActionClass(FooService.class);

		String json = toJSON(
			LinkedHashMapBuilder.<String, Object>put(
				"/foo/null-return", new LinkedHashMap<>()
			).build());

		MockHttpServletRequest mockHttpServletRequest =
			createInvokerHttpServletRequest(json);

		Assert.assertEquals(
			"{}",
			_jsonWebServiceServiceAction.getJSON(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	@Test
	public void testInvokerSimpleCall() throws Exception {
		registerActionClass(FooService.class);

		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			"/foo/hello-world", params
		).build();

		params.put("userId", 173);
		params.put("worldName", "Jupiter");

		String json = toJSON(map);

		MockHttpServletRequest mockHttpServletRequest =
			createInvokerHttpServletRequest(json);

		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"",
			_jsonWebServiceServiceAction.getJSON(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	@Test
	public void testMultipartRequest() throws Exception {
		registerActionClass(FooService.class);

		HttpServletRequest httpServletRequest = _createUploadServletRequest(
			createHttpRequest("/foo/add-file"),
			HashMapBuilder.<String, FileItem[]>put(
				"fileName", new FileItem[] {_createFileItem("aaa")}
			).build());

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			httpServletRequest);

		Assert.assertNotNull(jsonWebServiceAction);
	}

	@Test
	public void testMultipartRequestFilesUpload() throws Exception {
		registerActionClass(FooService.class);

		HttpServletRequest httpServletRequest = _createUploadServletRequest(
			createHttpRequest("/foo/upload-files"),
			HashMapBuilder.<String, FileItem[]>put(
				"firstFile", new FileItem[] {_createFileItem("aaa")}
			).put(
				"otherFiles",
				new FileItem[] {_createFileItem("bbb"), _createFileItem("ccc")}
			).build());

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			httpServletRequest);

		Assert.assertNotNull(jsonWebServiceAction);

		Object result = jsonWebServiceAction.invoke();

		Assert.assertNotNull(result);

		Assert.assertEquals("aaabbbccc", result.toString());
	}

	@Test
	public void testServletContextInvoker1() throws Exception {
		testServletContextInvoker("somectx", true, "/foo/hello-world");
	}

	@Test
	public void testServletContextInvoker2() throws Exception {
		testServletContextInvoker("somectx", false, "/somectx.foo/hello-world");
	}

	@Test
	public void testServletContextRequestParams1() throws Exception {
		testServletContextRequestParams("somectx", true, "/foo/hello-world");
	}

	@Test
	public void testServletContextRequestParams2() throws Exception {
		testServletContextRequestParams(
			"somectx", false, "/somectx.foo/hello-world");
	}

	@Test
	public void testServletContextURL1() throws Exception {
		testServletContextURL(
			"somectx", true, "/foo/hello-world/user-id/173/world-name/Jupiter");
	}

	@Test
	public void testServletContextURL2() throws Exception {
		testServletContextURL(
			"somectx", false,
			"/somectx.foo/hello-world/user-id/173/world-name/Jupiter");
	}

	@Test
	public void testStatusNotFound() throws Exception {
		_testStatusNotFound("/foo/no-such-model-exception");
		_testStatusNotFound("/foo/principal-exception");
		_testStatusNotFound("/foo/security-exception");

		_testJSONRPCError(
			"Internal error", ClassCastException.class, "hello",
			LinkedHashMapBuilder.<String, Object>put(
				"i1", RandomTestUtil.randomString()
			).build());
		_testJSONRPCError(
			"Invalid params", NoSuchModelException.class,
			"no-such-model-exception", new LinkedHashMap<>());
		_testJSONRPCError(
			"Invalid params", PrincipalException.class, "principal-exception",
			new LinkedHashMap<>());
		_testJSONRPCError(
			"Invalid params", SecurityException.class, "security-exception",
			new LinkedHashMap<>());
	}

	protected MockHttpServletRequest createInvokerHttpServletRequest(
		String content) {

		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/invoke");

		mockHttpServletRequest.setContent(content.getBytes());
		mockHttpServletRequest.setMethod(HttpMethods.POST);
		mockHttpServletRequest.setRemoteUser("root");

		return mockHttpServletRequest;
	}

	protected void testServletContextInvoker(
			String contextName, boolean setContextPath, String query)
		throws Exception {

		registerActionClass(FooService.class, contextName);

		Map<String, Object> params = new LinkedHashMap<>();

		Map<String, Object> map = LinkedHashMapBuilder.<String, Object>put(
			query, params
		).build();

		params.put("userId", 173);
		params.put("worldName", "Jupiter");

		String json = toJSON(map);

		MockHttpServletRequest mockHttpServletRequest =
			createInvokerHttpServletRequest(json);

		if (setContextPath) {
			setServletContext(mockHttpServletRequest, contextName);
		}

		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"",
			_jsonWebServiceServiceAction.getJSON(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	protected void testServletContextRequestParams(
			String contextName, boolean setContextPath, String request)
		throws Exception {

		registerActionClass(FooService.class, contextName);

		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			request);

		mockHttpServletRequest.setParameter("userId", "173");
		mockHttpServletRequest.setParameter("worldName", "Jupiter");

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		if (setContextPath) {
			setServletContext(mockHttpServletRequest, contextName);
		}

		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"",
			_jsonWebServiceServiceAction.getJSON(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	protected void testServletContextURL(
			String contextName, boolean setContextPath, String request)
		throws Exception {

		registerActionClass(FooService.class, contextName);

		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			request);

		mockHttpServletRequest.setMethod(HttpMethods.GET);

		if (setContextPath) {
			setServletContext(mockHttpServletRequest, contextName);
		}

		Assert.assertEquals(
			"\"Welcome 173 to Jupiter\"",
			_jsonWebServiceServiceAction.getJSON(
				mockHttpServletRequest, new MockHttpServletResponse()));
	}

	private FileItem _createFileItem(String content) throws Exception {
		Path tempFilePath = Files.createTempFile(null, null);

		Files.write(tempFilePath, content.getBytes());

		File tempFile = tempFilePath.toFile();

		FinalizeManager.register(
			tempFile, new DeleteFileFinalizeAction(tempFile.getAbsolutePath()),
			FinalizeManager.PHANTOM_REFERENCE_FACTORY);

		return ProxyUtil.newDelegateProxyInstance(
			FileItem.class.getClassLoader(), FileItem.class,
			new Object() {

				public File getStoreLocation() {
					return tempFile;
				}

				public boolean isInMemory() {
					return false;
				}

			},
			null);
	}

	private UploadServletRequest _createUploadServletRequest(
		HttpServletRequest httpServletRequest,
		Map<String, FileItem[]> multipartParameterMap) {

		return (UploadServletRequest)ProxyUtil.newProxyInstance(
			JSONWebServiceServiceActionTest.class.getClassLoader(),
			new Class<?>[] {UploadServletRequest.class},
			(proxy, method, args) -> {
				if (Objects.equals(
						method.getName(), "getMultipartParameterMap")) {

					return multipartParameterMap;
				}

				return method.invoke(httpServletRequest, args);
			});
	}

	private String _getJSONRPCErrorMessage(
			String method, Map<String, Object> parameters)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo", HttpMethods.POST);

		mockHttpServletRequest.setContent(
			toJSON(
				LinkedHashMapBuilder.<String, Object>put(
					"id", RandomTestUtil.randomInt()
				).put(
					"jsonrpc", "2.0"
				).put(
					"method", method
				).put(
					"params", parameters
				).build()
			).getBytes());

		Map<String, Object> map = toMap(
			_jsonWebServiceServiceAction.getJSON(
				mockHttpServletRequest, new MockHttpServletResponse()));

		Map<String, Object> error = (Map<String, Object>)map.get("error");

		return (String)error.get("message");
	}

	private void _testJSONRPCError(
			String expectedErrorMessage,
			Class<? extends Exception> expectedExceptionClass, String method,
			Map<String, Object> parameters)
		throws Exception {

		registerActionClass(FooService.class);

		Assert.assertEquals(
			expectedErrorMessage, _getJSONRPCErrorMessage(method, parameters));

		try (SafeCloseable safeCloseable =
				PropsValuesTestUtil.swapWithSafeCloseable(
					"JSON_SERVICE_SERIALIZE_THROWABLE", true)) {

			String message = _getJSONRPCErrorMessage(method, parameters);

			Assert.assertTrue(
				message.contains(expectedExceptionClass.getName()));
		}
	}

	private void _testStatusNotFound(String path) throws Exception {
		registerActionClass(FooService.class);

		MockHttpServletRequest mockHttpServletRequest =
			createInvokerHttpServletRequest(
				toJSON(
					LinkedHashMapBuilder.<String, Object>put(
						path, new LinkedHashMap<>()
					).build()));

		MockHttpServletResponse mockHttpServletResponse =
			new MockHttpServletResponse();

		String json = _jsonWebServiceServiceAction.getJSON(
			mockHttpServletRequest, mockHttpServletResponse);

		Assert.assertEquals(
			HttpServletResponse.SC_NOT_FOUND,
			mockHttpServletResponse.getStatus());

		Assert.assertEquals(JSONFactoryUtil.getNullJSON(), json);
	}

	private static JSONWebServiceServiceAction _jsonWebServiceServiceAction;

}