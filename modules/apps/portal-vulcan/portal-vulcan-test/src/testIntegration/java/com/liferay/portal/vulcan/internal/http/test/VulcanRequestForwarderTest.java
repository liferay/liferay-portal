/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.vulcan.internal.http.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.function.UnsafeSupplier;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.util.HTTPTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.Http;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.vulcan.http.VulcanRequestForwarder;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * @author Alejandro Tardín
 */
@RunWith(Arquillian.class)
public class VulcanRequestForwarderTest {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@Before
	public void setUp() {
		Bundle bundle = FrameworkUtil.getBundle(
			VulcanRequestForwarderTest.class);

		BundleContext bundleContext = bundle.getBundleContext();

		_serviceRegistration = bundleContext.registerService(
			Application.class, new TestApplication(),
			HashMapDictionaryBuilder.<String, Object>put(
				"liferay.auth.verifier", true
			).put(
				"liferay.jackson", false
			).put(
				"liferay.oauth2", false
			).put(
				"osgi.jaxrs.application.base", "/test"
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
	public void testForward() throws Exception {
		_testForward(
			200,
			new VulcanRequestForwarder.Request() {

				@Override
				public String getMethod() {
					return "DELETE";
				}

				@Override
				public String getPath() {
					return "/test/mirror";
				}

				@Override
				public User getUser() {
					return _getUser(TestPropsValues::getUser);
				}

			});
		_testForward(
			200,
			new VulcanRequestForwarder.Request() {

				@Override
				public String getMethod() {
					return "GET";
				}

				@Override
				public String getPath() {
					return "/test/mirror?parameter=value";
				}

				@Override
				public User getUser() {
					return _getUser(UserTestUtil::addUser);
				}

			});
		_testForward(
			200,
			new VulcanRequestForwarder.Request() {

				@Override
				public String getMethod() {
					return "GET";
				}

				@Override
				public String getPath() {
					return "/test/mirror";
				}

				@Override
				public User getUser() {
					return _getUser(
						() -> _userLocalService.getGuestUser(
							TestPropsValues.getCompanyId()));
				}

			});
		_testForward(
			404,
			new VulcanRequestForwarder.Request() {

				@Override
				public String getMethod() {
					return "GET";
				}

				@Override
				public String getPath() {
					return "/test/mirror-not-found";
				}

				@Override
				public User getUser() {
					return _getUser(TestPropsValues::getUser);
				}

			});
		_testForward(
			200,
			new VulcanRequestForwarder.Request() {

				@Override
				public byte[] getBody() {
					return "body".getBytes();
				}

				@Override
				public String getContentType() {
					return MediaType.TEXT_PLAIN;
				}

				@Override
				public Map<String, String> getHeaders() {
					return HashMapBuilder.put(
						"X-Test-Header", "value"
					).build();
				}

				@Override
				public String getMethod() {
					return "POST";
				}

				@Override
				public String getPath() {
					return "/test/mirror";
				}

				@Override
				public User getUser() {
					return _getUser(TestPropsValues::getUser);
				}

			});
		_testForward(
			200,
			new VulcanRequestForwarder.Request() {

				@Override
				public byte[] getBody() {
					return "body".getBytes();
				}

				@Override
				public String getContentType() {
					return MediaType.TEXT_PLAIN;
				}

				@Override
				public String getMethod() {
					return "PUT";
				}

				@Override
				public String getPath() {
					return "/test/mirror";
				}

				@Override
				public User getUser() {
					return _getUser(TestPropsValues::getUser);
				}

			});
	}

	public class TestApplication extends Application {

		@DELETE
		@Path("/mirror")
		@Produces(MediaType.APPLICATION_JSON)
		public String deleteMirror(
			@Context HttpServletRequest httpServletRequest) {

			return _mirror(httpServletRequest, null);
		}

		@Consumes(MediaType.APPLICATION_JSON)
		@Path("/forward")
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		public String forward(
				String json, @Context HttpServletRequest httpServletRequest)
			throws Exception {

			JSONObject jsonObject = _jsonFactory.createJSONObject(json);

			VulcanRequestForwarder.Response response =
				_vulcanRequestForwarder.forward(
					httpServletRequest,
					new VulcanRequestForwarder.Request() {

						@Override
						public byte[] getBody() {
							String body = jsonObject.getString("body", null);

							if (body == null) {
								return null;
							}

							return body.getBytes();
						}

						@Override
						public String getContentType() {
							return jsonObject.getString("contentType", null);
						}

						@Override
						public Map<String, String> getHeaders() {
							if (!jsonObject.has("headers")) {
								return null;
							}

							Map<String, String> headers = new HashMap<>();

							JSONObject headersJSONObject =
								jsonObject.getJSONObject("headers");

							for (String key : headersJSONObject.keySet()) {
								headers.put(
									key, headersJSONObject.getString(key));
							}

							return headers;
						}

						@Override
						public String getMethod() {
							return jsonObject.getString("method");
						}

						@Override
						public String getPath() {
							return jsonObject.getString("path");
						}

						@Override
						public User getUser() {
							return _getUser(
								() -> _userLocalService.getUser(
									jsonObject.getLong("userId")));
						}

					});

			return JSONUtil.put(
				"callerUserId",
				() -> {
					PermissionChecker permissionChecker =
						PermissionThreadLocal.getPermissionChecker();

					return permissionChecker.getUserId();
				}
			).put(
				"content", _jsonFactory.createJSONObject(response.getContent())
			).put(
				"contentType", response.getContentType()
			).put(
				"statusCode", response.getStatusCode()
			).toString();
		}

		@GET
		@Path("/mirror")
		@Produces(MediaType.APPLICATION_JSON)
		public String getMirror(
			@Context HttpServletRequest httpServletRequest) {

			return _mirror(httpServletRequest, null);
		}

		@GET
		@Path("/mirror-not-found")
		@Produces(MediaType.APPLICATION_JSON)
		public Response getMirrorNotFound(
			@Context HttpServletRequest httpServletRequest) {

			return Response.status(
				Response.Status.NOT_FOUND
			).entity(
				_mirror(httpServletRequest, null)
			).build();
		}

		@Override
		public Set<Object> getSingletons() {
			return Collections.singleton(this);
		}

		@Consumes(MediaType.TEXT_PLAIN)
		@Path("/mirror")
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		public String postMirror(
			String body, @Context HttpServletRequest httpServletRequest) {

			return _mirror(httpServletRequest, body);
		}

		@Consumes(MediaType.TEXT_PLAIN)
		@Path("/mirror")
		@Produces(MediaType.APPLICATION_JSON)
		@PUT
		public String putMirror(
			String body, @Context HttpServletRequest httpServletRequest) {

			return _mirror(httpServletRequest, body);
		}

		private String _mirror(
			HttpServletRequest httpServletRequest, String body) {

			return JSONUtil.put(
				"body", body
			).put(
				"contentType", httpServletRequest.getContentType()
			).put(
				"headers",
				() -> {
					JSONObject headersJSONObject =
						_jsonFactory.createJSONObject();

					Enumeration<String> enumeration =
						httpServletRequest.getHeaderNames();

					while (enumeration.hasMoreElements()) {
						String name = enumeration.nextElement();

						headersJSONObject.put(
							name, httpServletRequest.getHeader(name));
					}

					return headersJSONObject;
				}
			).put(
				"method", httpServletRequest.getMethod()
			).put(
				"path", httpServletRequest.getPathInfo()
			).put(
				"userId", () -> PortalUtil.getUserId(httpServletRequest)
			).toString();
		}

	}

	private User _getUser(UnsafeSupplier<User, Exception> userUnsafeSupplier) {
		try {
			return userUnsafeSupplier.get();
		}
		catch (Exception exception) {
			return ReflectionUtil.throwException(exception);
		}
	}

	private void _testForward(
			int expectedStatusCode, VulcanRequestForwarder.Request request)
		throws Exception {

		JSONObject jsonObject = JSONUtil.put(
			"body",
			() -> {
				byte[] body = request.getBody();

				if (body == null) {
					return null;
				}

				return new String(body);
			}
		).put(
			"contentType", request.getContentType()
		).put(
			"headers", request.getHeaders()
		).put(
			"method", request.getMethod()
		).put(
			"path", request.getPath()
		).put(
			"userId",
			() -> {
				User user = request.getUser();

				return user.getUserId();
			}
		);

		JSONAssert.assertEquals(
			JSONUtil.put(
				"callerUserId", TestPropsValues.getUserId()
			).put(
				"content",
				JSONUtil.merge(
					jsonObject,
					JSONUtil.put(
						"headers",
						JSONUtil.merge(
							JSONUtil.put(
								"ACCEPT", "application/json"
							).put(
								"Accept-Language", "en-US"
							).put(
								"Content-Length",
								() -> {
									byte[] body = request.getBody();

									if (body == null) {
										return null;
									}

									return String.valueOf(body.length);
								}
							).put(
								"Content-Type", request::getContentType
							),
							_jsonFactory.createJSONObject(
								request.getHeaders()))))
			).put(
				"contentType", "application/json"
			).put(
				"statusCode", expectedStatusCode
			).toString(),
			HTTPTestUtil.invokeToJSONObject(
				jsonObject.toString(), "test/forward",
				HashMapBuilder.put(
					"X-Original-Header", RandomTestUtil.randomString()
				).build(),
				Http.Method.POST
			).toString(),
			JSONCompareMode.STRICT);
	}

	@Inject
	private JSONFactory _jsonFactory;

	private ServiceRegistration<Application> _serviceRegistration;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanRequestForwarder _vulcanRequestForwarder;

}