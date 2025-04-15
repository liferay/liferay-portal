/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.remote.json.web.service.web.internal;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.ServiceContextFactory;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.transaction.Isolation;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.remote.json.web.service.JSONWebServiceAction;
import com.liferay.portal.remote.json.web.service.exception.NoSuchJSONWebServiceException;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import java.util.Collections;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Igor Spasic
 */
public class JSONWebServiceTest extends BaseJSONWebServiceTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() throws Exception {
		final Method getDefaultPlidMethod = LayoutLocalService.class.getMethod(
			"getDefaultPlid", long.class, boolean.class);

		ReflectionTestUtil.setFieldValue(
			LayoutLocalServiceUtil.class, "_service",
			ProxyUtil.newProxyInstance(
				LayoutLocalService.class.getClassLoader(),
				new Class<?>[] {LayoutLocalService.class},
				new InvocationHandler() {

					@Override
					public Object invoke(
						Object proxy, Method method, Object[] args) {

						if (getDefaultPlidMethod.equals(method)) {
							return 0L;
						}

						throw new UnsupportedOperationException();
					}

				}));

		PropsTestUtil.setProps(Collections.emptyMap());

		initPortalServices();

		registerActionClass(CamelFooService.class);
		registerActionClass(FooService.class);
		registerActionClass(FooService.class, "test-context");
	}

	@Before
	public void setUp() throws Exception {
		Mockito.when(
			ServiceContextFactory.getInstance(
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			new ServiceContext()
		);
	}

	@After
	public void tearDown() {
		_serviceContextFactoryMockedStatic.close();
	}

	@Test
	public void testArgumentsMatching() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/hello-world");

		try {
			lookupJSONWebServiceAction(mockHttpServletRequest);

			Assert.fail();
		}
		catch (NoSuchJSONWebServiceException noSuchJSONWebServiceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchJSONWebServiceException);
			}
		}

		mockHttpServletRequest = createHttpRequest(
			"/foo/hello-world/user-id/173/world-name/Forbidden Planet");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"Welcome 173 to Forbidden Planet", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest("/foo/hello-world");

		mockHttpServletRequest.setParameter("userId", "371");
		mockHttpServletRequest.setParameter("worldName", "Impossible Planet");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"Welcome 371 to Impossible Planet", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/hello-world/user-id/173");

		mockHttpServletRequest.setParameter("worldName", "Impossible Planet");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"Welcome 173 to Impossible Planet", jsonWebServiceAction.invoke());
	}

	@Test
	public void testCamelCaseNormalizedParameters() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/camel/good-name/goodboy/bad-name/badboy");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		String result = (String)jsonWebServiceAction.invoke();

		Assert.assertEquals("goodboy*badboy", result);

		mockHttpServletRequest = createHttpRequest("/foo/camel");

		mockHttpServletRequest.setParameter("goodName", "goodboy");
		mockHttpServletRequest.setParameter("badNAME", "badboy");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		result = (String)jsonWebServiceAction.invoke();

		Assert.assertEquals("goodboy*badboy", result);

		mockHttpServletRequest.removeAllParameters();
		mockHttpServletRequest.setParameter("goodName", "goodboy");
		mockHttpServletRequest.setParameter("badName", "badboy");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		result = (String)jsonWebServiceAction.invoke();

		Assert.assertEquals("goodboy*badboy", result);
	}

	@Test
	public void testCreateArgumentInstances() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/use1/+foo-data");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"using #1: h=177/id=-1/n=John Doe/v=foo!",
			jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest("/foo/use2/+foo-data");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		try {
			jsonWebServiceAction.invoke();

			Assert.fail();
		}
		catch (Exception exception) {
		}

		mockHttpServletRequest = createHttpRequest(
			"/foo/use2/+foo-data:" + FooDataImpl.class.getName());

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"using #2: h=177/id=-1/n=John Doe/v=foo!",
			jsonWebServiceAction.invoke());
	}

	@Test
	public void testCreateArgumentInstancesWithJSONData() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/use1");

		mockHttpServletRequest.setParameter(
			"fooData",
			"{\"height\": 121, \"name\":\"Felix\", \"value\":\"!!!\"}");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"using #1: h=121/id=-1/n=Felix/v=!!!",
			jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest("/foo/use2");

		mockHttpServletRequest.setParameter(
			"fooData", "{height: 121, name:'Felix', value:'!!!'}");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		try {
			jsonWebServiceAction.invoke();

			Assert.fail();
		}
		catch (Exception exception) {
		}

		mockHttpServletRequest = createHttpRequest("/foo/use2");

		mockHttpServletRequest.setParameter(
			"fooData:" + FooDataImpl.class.getName(),
			"{\"height\": 121, \"name\":\"Felix\", \"value\":\"!!!\"}");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"using #2: h=121/id=-1/n=Felix/v=!!!",
			jsonWebServiceAction.invoke());
	}

	@Test
	public void testDefaultServiceContext() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/srvcctx");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			ServiceContext.class.getName(), jsonWebServiceAction.invoke());
	}

	@Test
	public void testEnumParameter() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/camelfoo/add-isolation/isolation/DEFAULT");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		jsonWebServiceAction.invoke();
	}

	@Test
	public void testEnumReturnValue() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/camelfoo/get-isolation");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(Isolation.DEFAULT, jsonWebServiceAction.invoke());
	}

	@Test
	public void testInnerParameters() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/use1/+foo-data/foo-data.value/bar!");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"using #1: h=177/id=-1/n=John Doe/v=bar!",
			jsonWebServiceAction.invoke());
	}

	@Test
	public void testMatchingOverload() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123");

		try {
			lookupJSONWebServiceAction(mockHttpServletRequest);

			Assert.fail();
		}
		catch (Exception exception) {
		}

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123/name/Name");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-1", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123/name-id/321");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-2", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one.3/id/123/name-id/321");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-3", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123/name/Name/name-id/321");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-1", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest("/foo/method-one.2/id/123");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-1", jsonWebServiceAction.invoke());
	}

	@Test
	public void testMatchingOverloadInContext() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123");

		setServletContext(mockHttpServletRequest, "test-context");

		try {
			lookupJSONWebServiceAction(mockHttpServletRequest);

			Assert.fail();
		}
		catch (Exception exception) {
		}

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123/name/Name");

		setServletContext(mockHttpServletRequest, "test-context");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-1", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123/name-id/321");

		setServletContext(mockHttpServletRequest, "test-context");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-2", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one.3/id/123/name-id/321");

		setServletContext(mockHttpServletRequest, "test-context");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-3", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/method-one/id/123/name/Name/name-id/321");

		setServletContext(mockHttpServletRequest, "test-context");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-1", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest("/foo/method-one.2/id/123");

		setServletContext(mockHttpServletRequest, "test-context");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("m-1", jsonWebServiceAction.invoke());
	}

	@Test
	public void testModifyServiceContext() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/srvcctx2");

		mockHttpServletRequest.setParameter(
			"serviceContext", "{\"failOnPortalException\" : false}");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		ServiceContext serviceContext =
			(ServiceContext)jsonWebServiceAction.invoke();

		Assert.assertFalse(serviceContext.isFailOnPortalException());
	}

	@Test
	public void testNaming() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/not-found");

		try {
			lookupJSONWebServiceAction(mockHttpServletRequest);

			Assert.fail();
		}
		catch (NoSuchJSONWebServiceException noSuchJSONWebServiceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchJSONWebServiceException);
			}
		}

		mockHttpServletRequest = createHttpRequest("/foo/hello");

		Assert.assertNotNull(
			lookupJSONWebServiceAction(mockHttpServletRequest));

		mockHttpServletRequest = createHttpRequest("/camelfoo/hello");

		Assert.assertNotNull(
			lookupJSONWebServiceAction(mockHttpServletRequest));

		mockHttpServletRequest = createHttpRequest("/camelfoo/hello-world");

		Assert.assertNotNull(
			lookupJSONWebServiceAction(mockHttpServletRequest));

		mockHttpServletRequest = createHttpRequest("/camelfoo/brave-new-world");

		try {
			lookupJSONWebServiceAction(mockHttpServletRequest);

			Assert.fail();
		}
		catch (NoSuchJSONWebServiceException noSuchJSONWebServiceException) {
			if (_log.isDebugEnabled()) {
				_log.debug(noSuchJSONWebServiceException);
			}
		}

		mockHttpServletRequest = createHttpRequest("/camelfoo/cool-new-world");

		Assert.assertNotNull(
			lookupJSONWebServiceAction(mockHttpServletRequest));
	}

	@Test
	public void testNullValues() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/null-lover");

		mockHttpServletRequest.setParameter("-name", "");
		mockHttpServletRequest.setParameter("number", "173");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("null!", jsonWebServiceAction.invoke());

		mockHttpServletRequest.setParameter("name", "liferay");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("[liferay|173]", jsonWebServiceAction.invoke());

		mockHttpServletRequest = createHttpRequest(
			"/foo/null-lover/-name/number/173");

		jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals("null!", jsonWebServiceAction.invoke());
	}

	@Test
	public void testSimpleMethod() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/hello");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertNotNull(jsonWebServiceAction);

		Assert.assertEquals("world", jsonWebServiceAction.invoke());
	}

	@Test
	public void testTypeConversion1() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/hey");

		mockHttpServletRequest.setParameter("calendar", "1330419334285");
		mockHttpServletRequest.setParameter("userIds", "1,2,3");
		mockHttpServletRequest.setParameter("locales", "\"en\",\"fr\"");
		mockHttpServletRequest.setParameter("ids", "173,-7,007");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"2012, 1/3, en/2, 173/3", jsonWebServiceAction.invoke());
	}

	@Test
	public void testTypeConversion2() throws Exception {
		MockHttpServletRequest mockHttpServletRequest = createHttpRequest(
			"/foo/hey");

		mockHttpServletRequest.setParameter("calendar", "1330419334285");
		mockHttpServletRequest.setParameter("userIds", "[1,2,3]");
		mockHttpServletRequest.setParameter("locales", "[\"en\",\"fr\"]");
		mockHttpServletRequest.setParameter("ids", "[173,-7,007]");

		JSONWebServiceAction jsonWebServiceAction = lookupJSONWebServiceAction(
			mockHttpServletRequest);

		Assert.assertEquals(
			"2012, 1/3, en/2, 173/3", jsonWebServiceAction.invoke());
	}

	@Test
	public void testTypeConversionDate() throws Exception {
		MockHttpServletRequest mockHttpServletRequest1 = createHttpRequest(
			"/foo/date");

		mockHttpServletRequest1.setParameter("date", "2021-05-09");

		JSONWebServiceAction jsonWebServiceAction1 = lookupJSONWebServiceAction(
			mockHttpServletRequest1);

		Assert.assertEquals(
			"Sun May 09 00:00:00 GMT 2021", jsonWebServiceAction1.invoke());

		MockHttpServletRequest mockHttpServletRequest2 = createHttpRequest(
			"/foo/date");

		mockHttpServletRequest2.setParameter("date", "2021-5-9");

		JSONWebServiceAction jsonWebServiceAction2 = lookupJSONWebServiceAction(
			mockHttpServletRequest2);

		Assert.assertEquals(
			"Sun May 09 00:00:00 GMT 2021", jsonWebServiceAction2.invoke());
	}

	private static final Log _log = LogFactoryUtil.getLog(
		JSONWebServiceTest.class);

	private final MockedStatic<ServiceContextFactory>
		_serviceContextFactoryMockedStatic = Mockito.mockStatic(
			ServiceContextFactory.class);

}