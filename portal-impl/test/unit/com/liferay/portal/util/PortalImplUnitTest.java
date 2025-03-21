/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.util;

import com.liferay.petra.string.StringBundler;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.module.util.SystemBundleUtil;
import com.liferay.portal.kernel.portlet.InvokerPortlet;
import com.liferay.portal.kernel.security.auth.AlwaysAllowDoAsUser;
import com.liferay.portal.kernel.service.GroupLocalServiceUtil;
import com.liferay.portal.kernel.servlet.DummyHttpServletResponse;
import com.liferay.portal.kernel.servlet.DynamicServletRequest;
import com.liferay.portal.kernel.servlet.PersistentHttpServletRequestWrapper;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.upgrade.MockPortletPreferences;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.LayoutTypePortletFactoryUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ProxyFactory;
import com.liferay.portal.kernel.util.ProxyUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.impl.LayoutImpl;
import com.liferay.portal.model.impl.LayoutSetImpl;
import com.liferay.portal.model.impl.PortletAppImpl;
import com.liferay.portal.model.impl.PortletImpl;
import com.liferay.portal.model.impl.UserImpl;
import com.liferay.portal.spring.context.PortalContextLoaderListener;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.theme.ThemeDisplayFactory;
import com.liferay.portlet.ActionRequestFactory;
import com.liferay.portlet.ActionResponseFactory;
import com.liferay.portlet.internal.MutableRenderParametersImpl;
import com.liferay.portlet.test.MockLiferayPortletContext;

import jakarta.portlet.ActionRequest;
import jakarta.portlet.ActionResponse;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletMode;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.logging.Level;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import org.springframework.mock.web.MockHttpServletRequest;

/**
 * @author Miguel Pastor
 */
public class PortalImplUnitTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_groupLocalServiceUtilMockedStatic.reset();
		_portalContextLoaderListenerMockedStatic.reset();
	}

	@After
	public void tearDown() {
		_groupLocalServiceUtilMockedStatic.close();
		_portalContextLoaderListenerMockedStatic.close();
	}

	@Test
	public void testCopyRequestParameters() throws PortletException {

		// Without password

		Map<String, String[]> params = HashMapBuilder.put(
			"p_u_i_d",
			new String[] {String.valueOf(RandomTestUtil.randomLong())}
		).put(
			"passwordReset",
			new String[] {String.valueOf(RandomTestUtil.randomBoolean())}
		).put(
			"redirect", new String[] {RandomTestUtil.randomString()}
		).build();

		Enumeration<String> enumeration = Collections.enumeration(
			Arrays.asList(
				"p_u_i_d", "password1", "password2", "passwordReset",
				"redirect"));

		MockedStatic<PortalUtil> portalUtilMockedStatic = Mockito.mockStatic(
			PortalUtil.class);

		ActionResponse actionResponse = _createActionResponse(
			portalUtilMockedStatic);

		_portalImpl.copyRequestParameters(
			_createActionRequest(params, enumeration), actionResponse);

		_assertActionResponse(actionResponse, params);

		// With password

		params.put("password1", new String[] {RandomTestUtil.randomString()});
		params.put("password2", new String[] {RandomTestUtil.randomString()});

		_portalImpl.copyRequestParameters(
			_createActionRequest(params, enumeration), actionResponse);

		portalUtilMockedStatic.close();

		_assertActionResponse(actionResponse, params);
	}

	@Test
	public void testGetForwardedHost() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setServerName("serverName");

		Assert.assertEquals(
			"serverName", _portalImpl.getForwardedHost(mockHttpServletRequest));
	}

	@Test
	public void testGetForwardedHostWithCustomXForwardedHostEnabledAndNotValidHost()
		throws Exception {

		try {
			_storeAndResetPropsValuesValue("X-Forwarded-Custom-Host", null);

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequestWithHeader(
					"X-Forwarded-Custom-Host");

			_portalImpl.getForwardedHost(mockHttpServletRequest);
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof NullPointerException);
		}
		finally {
			_restorePropsValuesValue();
		}
	}

	@Test
	public void testGetForwardedHostWithCustomXForwardedHostEnabledAndValidHost()
		throws Exception {

		try {
			_storeAndResetPropsValuesValue(
				"X-Forwarded-Custom-Host", "forwardedServer");

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequestWithHeader(
					"X-Forwarded-Custom-Host");

			Assert.assertEquals(
				"forwardedServer",
				_portalImpl.getForwardedHost(mockHttpServletRequest));
		}
		finally {
			_restorePropsValuesValue();
		}
	}

	@Test
	public void testGetForwardedHostWithXForwardedHostDisabled()
		throws Exception {

		boolean webServerForwardedHostEnabled =
			PropsValues.WEB_SERVER_FORWARDED_HOST_ENABLED;

		try {
			setPropsValuesValue("WEB_SERVER_FORWARDED_HOST_ENABLED", false);

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequestWithHeader("X-Forwarded-Host");

			Assert.assertEquals(
				"serverName",
				_portalImpl.getForwardedHost(mockHttpServletRequest));
		}
		finally {
			setPropsValuesValue(
				"WEB_SERVER_FORWARDED_HOST_ENABLED",
				webServerForwardedHostEnabled);
		}
	}

	@Test
	public void testGetForwardedHostWithXForwardedHostEnabledAndNotValidHost()
		throws Exception {

		try {
			_storeAndResetPropsValuesValue("X-Forwarded-Host", null);

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequestWithHeader("X-Forwarded-Host");

			_portalImpl.getForwardedHost(mockHttpServletRequest);
		}
		catch (Exception exception) {
			Assert.assertTrue(exception instanceof NullPointerException);
		}
		finally {
			_restorePropsValuesValue();
		}
	}

	@Test
	public void testGetForwardedHostWithXForwardedHostEnabledAndValidHost()
		throws Exception {

		try {
			_storeAndResetPropsValuesValue(
				"X-Forwarded-Host", "forwardedServer");

			MockHttpServletRequest mockHttpServletRequest =
				_createMockHttpServletRequestWithHeader("X-Forwarded-Host");

			Assert.assertEquals(
				"forwardedServer",
				_portalImpl.getForwardedHost(mockHttpServletRequest));
		}
		finally {
			_restorePropsValuesValue();
		}
	}

	@Test
	public void testGetForwardedPort() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setServerPort(8080);

		Assert.assertEquals(
			8080, _portalImpl.getForwardedPort(mockHttpServletRequest));
	}

	@Test
	public void testGetForwardedPortWithCustomXForwardedPort()
		throws Exception {

		boolean webServerForwardedPortEnabled =
			PropsValues.WEB_SERVER_FORWARDED_PORT_ENABLED;
		String webServerForwardedPortHeader =
			PropsValues.WEB_SERVER_FORWARDED_PORT_HEADER;

		try {
			setPropsValuesValue("WEB_SERVER_FORWARDED_PORT_ENABLED", false);
			setPropsValuesValue(
				"WEB_SERVER_FORWARDED_PORT_HEADER", "X-Forwarded-Custom-Port");

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.addHeader("X-Forwarded-Custom-Port", 8081);
			mockHttpServletRequest.setServerPort(8080);

			Assert.assertEquals(
				8080, _portalImpl.getForwardedPort(mockHttpServletRequest));
		}
		finally {
			setPropsValuesValue(
				"WEB_SERVER_FORWARDED_PORT_ENABLED",
				webServerForwardedPortEnabled);
			setPropsValuesValue(
				"WEB_SERVER_FORWARDED_PORT_HEADER",
				webServerForwardedPortHeader);
		}
	}

	@Test
	public void testGetForwardedPortWithXForwardedPortDisabled()
		throws Exception {

		boolean webServerForwardedHostEnabled =
			PropsValues.WEB_SERVER_FORWARDED_PORT_ENABLED;

		try {
			setPropsValuesValue("WEB_SERVER_FORWARDED_PORT_ENABLED", false);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.addHeader("X-Forwarded-Port", 8081);
			mockHttpServletRequest.setServerPort(8080);

			Assert.assertEquals(
				8080, _portalImpl.getForwardedPort(mockHttpServletRequest));
		}
		finally {
			setPropsValuesValue(
				"WEB_SERVER_FORWARDED_PORT_ENABLED",
				webServerForwardedHostEnabled);
		}
	}

	@Test
	public void testGetForwardedPortWithXForwardedPortEnabled()
		throws Exception {

		boolean webServerForwardedPortEnabled =
			PropsValues.WEB_SERVER_FORWARDED_PORT_ENABLED;

		try {
			setPropsValuesValue("WEB_SERVER_FORWARDED_PORT_ENABLED", true);

			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.addHeader("X-Forwarded-Port", "8081");
			mockHttpServletRequest.setServerPort(8080);

			Assert.assertEquals(
				8081, _portalImpl.getForwardedPort(mockHttpServletRequest));
		}
		finally {
			setPropsValuesValue(
				"WEB_SERVER_FORWARDED_PORT_ENABLED",
				webServerForwardedPortEnabled);
		}
	}

	@Test
	public void testGetHost() {
		_assertGetHost("123.1.1.1", "123.1.1.1");
		_assertGetHost("123.1.1.1:80", "123.1.1.1");
		_assertGetHost("[0:0:0:0:0:0:0:1]", "0:0:0:0:0:0:0:1");
		_assertGetHost("[0:0:0:0:0:0:0:1]:80", "0:0:0:0:0:0:0:1");
		_assertGetHost("[::1]", "::1");
		_assertGetHost("[::1]:80", "::1");
		_assertGetHost("abc.com", "abc.com");
		_assertGetHost("abc.com:80", "abc.com");
	}

	@Test
	public void testGetLayoutSetFriendlyURLLayoutSetWithoutVirtualHost()
		throws Exception {

		_setUpPortalImpl(StringPool.BLANK);

		_assertGetLayoutSetFriendlyURL(
			"/web/test-group", "http://liferay.com:8080", false,
			new TreeMap<>());
	}

	@Test
	public void testGetLayoutSetFriendlyURLPrivateLayoutSetWithoutVirtualHost()
		throws Exception {

		_setUpPortalImpl(StringPool.BLANK);

		_assertGetLayoutSetFriendlyURL(
			"/group/test-group", "http://liferay.com:8080", true,
			new TreeMap<>());
	}

	@Test
	public void testGetLayoutSetFriendlyURLUserGroupLayoutSetWithoutVirtualHost()
		throws Exception {

		_setUpPortalImpl(StringPool.BLANK, true);

		_assertGetLayoutSetFriendlyURL(
			"/user/test-group", "http://liferay.com:8080", true,
			new TreeMap<>());
	}

	@Test
	public void testGetLayoutSetFriendlyURLWhenLayoutSetMatchesWithDifferentVirtualHost()
		throws Exception {

		_setUpPortalImpl(StringPool.BLANK);

		_assertGetLayoutSetFriendlyURL(
			"/web/test-group", "http://liferay.com:8080", false,
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());
	}

	@Test
	public void testGetLayoutSetFriendlyURLWhenLayoutSetMatchesWithSameVirtualHost()
		throws Exception {

		_setUpPortalImpl(StringPool.BLANK);

		_assertGetLayoutSetFriendlyURL(
			"http://test.com:8080", "http://test.com:8080", false,
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());
	}

	@Test
	public void testGetLayoutSetFriendlyURLWithContextPathWhenLayoutSetMatchesWithDifferentVirtualHost()
		throws Exception {

		_setUpPortalImpl("context-path");

		_assertGetLayoutSetFriendlyURL(
			"/context-path/web/test-group",
			"http://liferay.com:8080/context-path", false,
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());
	}

	@Test
	public void testGetLayoutSetFriendlyURLWithContextPathWhenLayoutSetMatchesWithSameVirtualHost()
		throws Exception {

		_setUpPortalImpl("context-path");

		_assertGetLayoutSetFriendlyURL(
			"http://test.com:8080/context-path",
			"http://test.com:8080/context-path", false,
			TreeMapBuilder.put(
				"test.com", StringPool.BLANK
			).build());
	}

	@Test
	public void testGetOriginalServletRequest() {
		HttpServletRequest httpServletRequest = new MockHttpServletRequest();

		Assert.assertSame(
			httpServletRequest,
			_portalImpl.getOriginalServletRequest(httpServletRequest));

		HttpServletRequestWrapper requestWrapper1 =
			new HttpServletRequestWrapper(httpServletRequest);

		Assert.assertSame(
			httpServletRequest,
			_portalImpl.getOriginalServletRequest(requestWrapper1));

		HttpServletRequestWrapper requestWrapper2 =
			new HttpServletRequestWrapper(requestWrapper1);

		Assert.assertSame(
			httpServletRequest,
			_portalImpl.getOriginalServletRequest(requestWrapper2));

		HttpServletRequestWrapper requestWrapper3 =
			new PersistentHttpServletRequestWrapper1(requestWrapper2);

		HttpServletRequest originalHttpServletRequest =
			_portalImpl.getOriginalServletRequest(requestWrapper3);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper1.class,
			originalHttpServletRequest.getClass());
		Assert.assertNotSame(requestWrapper3, originalHttpServletRequest);
		Assert.assertSame(
			httpServletRequest, getWrappedRequest(originalHttpServletRequest));

		HttpServletRequestWrapper requestWrapper4 =
			new PersistentHttpServletRequestWrapper2(requestWrapper3);

		originalHttpServletRequest = _portalImpl.getOriginalServletRequest(
			requestWrapper4);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper2.class,
			originalHttpServletRequest.getClass());
		Assert.assertNotSame(requestWrapper4, originalHttpServletRequest);

		originalHttpServletRequest = getWrappedRequest(
			originalHttpServletRequest);

		Assert.assertSame(
			PersistentHttpServletRequestWrapper1.class,
			originalHttpServletRequest.getClass());
		Assert.assertNotSame(requestWrapper3, originalHttpServletRequest);
		Assert.assertSame(
			httpServletRequest, getWrappedRequest(originalHttpServletRequest));
	}

	@Test
	public void testGetUserId() {
		BundleContext bundleContext = SystemBundleUtil.getBundleContext();

		boolean[] calledAlwaysAllowDoAsUser = {false};

		ServiceRegistration<AlwaysAllowDoAsUser> serviceRegistration =
			bundleContext.registerService(
				AlwaysAllowDoAsUser.class,
				(AlwaysAllowDoAsUser)ProxyUtil.newProxyInstance(
					AlwaysAllowDoAsUser.class.getClassLoader(),
					new Class<?>[] {AlwaysAllowDoAsUser.class},
					(proxy, method, args) -> {
						calledAlwaysAllowDoAsUser[0] = true;

						if (Objects.equals(method.getName(), "equals")) {
							return true;
						}

						if (Objects.equals(method.getName(), "hashcode")) {
							return 0;
						}

						return Collections.emptyList();
					}),
				null);

		try {
			MockHttpServletRequest mockHttpServletRequest =
				new MockHttpServletRequest();

			mockHttpServletRequest.setParameter("doAsUserId", "1");

			_portalImpl.getUserId(mockHttpServletRequest);

			Assert.assertTrue(
				"AlwaysAllowDoAsUser not called", calledAlwaysAllowDoAsUser[0]);

			calledAlwaysAllowDoAsUser[0] = false;

			_portalImpl.getUserId(new MockHttpServletRequest());

			Assert.assertFalse(
				"AlwaysAllowDoAsUser should not be called",
				calledAlwaysAllowDoAsUser[0]);
		}
		finally {
			serviceRegistration.unregister();
		}
	}

	@Test
	public void testIsSecureWithSecureRequest() {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.setSecure(true);

		Assert.assertTrue(_portalImpl.isSecure(mockHttpServletRequest));
	}

	@Test
	public void testIsValidResourceId() {
		Assert.assertTrue(_portalImpl.isValidResourceId("/view.jsp"));
		Assert.assertTrue(_portalImpl.isValidResourceId("%2fview.jsp"));
		Assert.assertTrue(_portalImpl.isValidResourceId("%252fview.jsp"));

		Assert.assertFalse(
			_portalImpl.isValidResourceId("/META-INF/MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%2fMETA-INF%2fMANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%252fMETA-INF%252fMANIFEST.MF"));

		Assert.assertFalse(
			_portalImpl.isValidResourceId("/META-INF\\MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%2fMETA-INF%5cMANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%252fMETA-INF%255cMANIFEST.MF"));

		Assert.assertFalse(
			_portalImpl.isValidResourceId("\\META-INF/MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%5cMETA-INF%2fMANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%255cMETA-INF%252fMANIFEST.MF"));

		Assert.assertFalse(
			_portalImpl.isValidResourceId("\\META-INF\\MANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%5cMETA-INF%5cMANIFEST.MF"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%255cMETA-INF%255cMANIFEST.MF"));

		Assert.assertFalse(_portalImpl.isValidResourceId("/WEB-INF/web.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%2fWEB-INF%2fweb.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%252fWEB-INF%252fweb.xml"));

		Assert.assertFalse(_portalImpl.isValidResourceId("/WEB-INF\\web.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%2fWEB-INF%5cweb.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%252fWEB-INF%255cweb.xml"));

		Assert.assertFalse(_portalImpl.isValidResourceId("\\WEB-INF/web.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%5cWEB-INF%2fweb.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%255cWEB-INF%252fweb.xml"));

		Assert.assertFalse(_portalImpl.isValidResourceId("\\WEB-INF\\web.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%5cWEB-INF%5cweb.xml"));
		Assert.assertFalse(
			_portalImpl.isValidResourceId("%255cWEB-INF%255cweb.xml"));

		Assert.assertTrue(_portalImpl.isValidResourceId("%25252525252525252f"));

		StringBundler sb = new StringBundler();

		sb.append("%");

		for (int i = 0; i < 100000; i++) {
			sb.append("25");
		}

		sb.append("2f");

		Assert.assertFalse(_portalImpl.isValidResourceId(sb.toString()));

		try (LogCapture logCapture = LoggerTestUtil.configureJDKLogger(
				HttpComponentsUtil.class.getName(), Level.OFF)) {

			Assert.assertFalse(_portalImpl.isValidResourceId("%view.jsp"));
		}
	}

	@Test
	public void testUpdateRedirectRemoveLayoutURL() {
		Assert.assertEquals(
			"/web/group",
			_portalImpl.updateRedirect(
				"/web/group/layout", "/group/layout", "/group"));
	}

	protected HttpServletRequest getWrappedRequest(
		HttpServletRequest httpServletRequest) {

		HttpServletRequestWrapper requestWrapper =
			(HttpServletRequestWrapper)httpServletRequest;

		return (HttpServletRequest)requestWrapper.getRequest();
	}

	protected void setPropsValuesValue(String fieldName, Object value) {
		ReflectionTestUtil.setFieldValue(PropsValues.class, fieldName, value);
	}

	private void _assertActionResponse(
		ActionResponse actionResponse, Map<String, String[]> params) {

		MutableRenderParametersImpl mutableRenderParametersImpl =
			(MutableRenderParametersImpl)actionResponse.getRenderParameters();

		Assert.assertEquals(
			mutableRenderParametersImpl.getValues("redirect")[0],
			params.get("redirect")[0]);
		Assert.assertEquals(
			mutableRenderParametersImpl.getValues("p_u_i_d")[0],
			params.get("p_u_i_d")[0]);
		Assert.assertEquals(
			mutableRenderParametersImpl.getValues("passwordReset")[0],
			params.get("passwordReset")[0]);
		Assert.assertNull(mutableRenderParametersImpl.getValues("password1"));
		Assert.assertNull(mutableRenderParametersImpl.getValues("password2"));
	}

	private void _assertGetHost(String httpHostHeader, String host) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader("Host", httpHostHeader);

		Assert.assertEquals(host, _portalImpl.getHost(mockHttpServletRequest));
	}

	private void _assertGetLayoutSetFriendlyURL(
			String expectedFriendlyURL, String portalURL, boolean privateLayout,
			TreeMap<String, String> virtualHostnames)
		throws Exception {

		LayoutSet layoutSet = new LayoutSetImpl();

		layoutSet.setLayoutSetId(11L);
		layoutSet.setGroupId(2000L);
		layoutSet.setPrivateLayout(privateLayout);
		layoutSet.setVirtualHostnames(virtualHostnames);

		ThemeDisplay themeDisplay = ThemeDisplayFactory.create();

		themeDisplay.setDoAsGroupId(0);
		themeDisplay.setI18nLanguageId(null);

		Layout layout = new LayoutImpl();

		layout.setType(LayoutConstants.TYPE_CONTENT);
		layout.setLayoutSet(layoutSet);

		themeDisplay.setLayout(layout);

		themeDisplay.setRefererGroupId(0);
		themeDisplay.setRefererPlid(0);
		themeDisplay.setSecure(false);
		themeDisplay.setServerPort(8080);
		themeDisplay.setURLPortal(portalURL);

		Assert.assertEquals(
			expectedFriendlyURL,
			_portalImpl.getLayoutSetFriendlyURL(layoutSet, themeDisplay));
	}

	private ActionRequest _createActionRequest(
		Map<String, String[]> params, Enumeration<String> enumeration) {

		ActionRequest actionRequestMock = Mockito.mock(ActionRequest.class);

		Mockito.when(
			actionRequestMock.getParameterNames()
		).thenReturn(
			enumeration
		);

		Mockito.when(
			actionRequestMock.getParameterValues("redirect")
		).thenReturn(
			params.get("redirect")
		);

		Mockito.when(
			actionRequestMock.getParameterValues("p_u_i_d")
		).thenReturn(
			params.get("p_u_i_d")
		);

		Mockito.when(
			actionRequestMock.getParameterValues("passwordReset")
		).thenReturn(
			params.get("passwordReset")
		);

		Mockito.when(
			actionRequestMock.getParameterValues("password1")
		).thenReturn(
			params.get("password1")
		);

		Mockito.when(
			actionRequestMock.getParameterValues("password2")
		).thenReturn(
			params.get("password2")
		);

		return actionRequestMock;
	}

	private ActionRequest _createActionRequest(PortletMode portletMode) {
		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		HttpServletRequest httpServletRequest = new DynamicServletRequest(
			mockHttpServletRequest, new HashMap<>());

		httpServletRequest.setAttribute(
			WebKeys.THEME_DISPLAY, ThemeDisplayFactory.create());

		Portlet portlet = new PortletImpl(
			RandomTestUtil.randomLong(), RandomTestUtil.randomString());

		portlet.setPortletApp(
			new PortletAppImpl(RandomTestUtil.randomString()));

		return ActionRequestFactory.create(
			httpServletRequest, portlet,
			ProxyFactory.newDummyInstance(InvokerPortlet.class),
			new MockLiferayPortletContext(RandomTestUtil.randomString()),
			WindowState.NORMAL, portletMode, new MockPortletPreferences(),
			4000L);
	}

	private ActionResponse _createActionResponse(
			MockedStatic<PortalUtil> portalUtilMockedStatic)
		throws PortletException {

		LayoutTypePortletFactoryUtil layoutTypePortletFactoryUtil =
			new LayoutTypePortletFactoryUtil();

		layoutTypePortletFactoryUtil.setLayoutTypePortletFactory(
			new LayoutTypePortletFactoryImpl());

		portalUtilMockedStatic.when(
			() -> PortalUtil.updateWindowState(
				Mockito.anyString(), Mockito.any(UserImpl.class),
				Mockito.any(LayoutImpl.class), Mockito.any(WindowState.class),
				Mockito.any(HttpServletRequest.class))
		).thenReturn(
			WindowState.NORMAL
		);

		PortletMode portletMode = Mockito.mock(PortletMode.class);

		Mockito.doReturn(
			null
		).when(
			portletMode
		).toString();

		return ActionResponseFactory.create(
			_createActionRequest(portletMode), new DummyHttpServletResponse(),
			new UserImpl(), new LayoutImpl());
	}

	private MockHttpServletRequest _createMockHttpServletRequestWithHeader(
		String header) {

		MockHttpServletRequest mockHttpServletRequest =
			new MockHttpServletRequest();

		mockHttpServletRequest.addHeader(header, "forwardedServer");
		mockHttpServletRequest.setServerName("serverName");

		return mockHttpServletRequest;
	}

	private void _restorePropsValuesValue() {
		setPropsValuesValue(
			"VIRTUAL_HOSTS_VALID_HOSTS", _virtualHostsValidHosts);
		setPropsValuesValue(
			"WEB_SERVER_FORWARDED_HOST_ENABLED",
			_webServerForwardedHostEnabled);
		setPropsValuesValue(
			"WEB_SERVER_FORWARDED_HOST_HEADER", _webServerForwardedHostHeader);
	}

	private void _setUpGroupLocalServiceUtil(boolean userGroup)
		throws Exception {

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getGroupId()
		).thenReturn(
			2000L
		);

		Mockito.when(
			group.isUser()
		).thenReturn(
			userGroup
		);

		Mockito.when(
			group.getFriendlyURL()
		).thenReturn(
			"/test-group"
		);

		Mockito.when(
			GroupLocalServiceUtil.getGroup(Mockito.anyLong())
		).thenReturn(
			group
		);
	}

	private void _setUpPortalContextLoaderListener(String contextPath) {
		Mockito.when(
			PortalContextLoaderListener.getPortalServletContextPath()
		).thenReturn(
			contextPath
		);
	}

	private void _setUpPortalImpl(String contextPath) throws Exception {
		_setUpPortalImpl(contextPath, false);
	}

	private void _setUpPortalImpl(String contextPath, boolean userGroup)
		throws Exception {

		_setUpGroupLocalServiceUtil(userGroup);
		_setUpPortalContextLoaderListener(contextPath);

		_portalImpl = new PortalImpl();
	}

	private void _storeAndResetPropsValuesValue(
		String forwaredHostHeader, String forwaredServer) {

		_webServerForwardedHostEnabled =
			PropsValues.WEB_SERVER_FORWARDED_HOST_ENABLED;

		_webServerForwardedHostHeader =
			PropsValues.WEB_SERVER_FORWARDED_HOST_HEADER;

		_virtualHostsValidHosts = PropsValues.VIRTUAL_HOSTS_VALID_HOSTS;

		if (forwaredHostHeader != null) {
			setPropsValuesValue(
				"VIRTUAL_HOSTS_VALID_HOSTS", new String[] {forwaredServer});
		}

		setPropsValuesValue("WEB_SERVER_FORWARDED_HOST_ENABLED", true);
		setPropsValuesValue(
			"WEB_SERVER_FORWARDED_HOST_HEADER", forwaredHostHeader);
	}

	private final MockedStatic<GroupLocalServiceUtil>
		_groupLocalServiceUtilMockedStatic = Mockito.mockStatic(
			GroupLocalServiceUtil.class);
	private final MockedStatic<PortalContextLoaderListener>
		_portalContextLoaderListenerMockedStatic = Mockito.mockStatic(
			PortalContextLoaderListener.class);
	private PortalImpl _portalImpl = new PortalImpl();
	private String[] _virtualHostsValidHosts;
	private boolean _webServerForwardedHostEnabled;
	private String _webServerForwardedHostHeader;

	private static class PersistentHttpServletRequestWrapper1
		extends PersistentHttpServletRequestWrapper {

		private PersistentHttpServletRequestWrapper1(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);
		}

	}

	private static class PersistentHttpServletRequestWrapper2
		extends PersistentHttpServletRequestWrapper {

		private PersistentHttpServletRequestWrapper2(
			HttpServletRequest httpServletRequest) {

			super(httpServletRequest);
		}

	}

}