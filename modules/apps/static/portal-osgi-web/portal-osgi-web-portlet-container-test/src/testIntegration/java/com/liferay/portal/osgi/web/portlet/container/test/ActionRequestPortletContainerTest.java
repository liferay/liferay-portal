/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.security.auth.AuthToken;
import com.liferay.portal.kernel.security.auth.AuthTokenWhitelist;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.PropsKeys;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.security.auth.AuthTokenWhitelistImpl;
import com.liferay.portal.security.auth.SessionAuthToken;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.SecurityPortletContainerWrapper;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class ActionRequestPortletContainerTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testAuthTokenCheckEnabled() throws Exception {
		registerService(
			AuthToken.class, new DisabledSessionAuthToken(),
			HashMapDictionaryBuilder.<String, Object>put(
				"service.ranking", Integer.MAX_VALUE
			).build());

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.ACTION_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testAuthTokenIgnoreOrigins() throws Exception {
		registerService(
			Object.class, new Object(),
			HashMapDictionaryBuilder.<String, Object>put(
				PropsKeys.AUTH_TOKEN_IGNORE_ORIGINS,
				SecurityPortletContainerWrapper.class.getName()
			).build());

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.ACTION_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testAuthTokenIgnorePortlets() throws Exception {
		registerService(
			Object.class, new Object(),
			HashMapDictionaryBuilder.<String, Object>put(
				PropsKeys.AUTH_TOKEN_IGNORE_PORTLETS, TEST_PORTLET_ID
			).build());

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.ACTION_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testInitParam() throws Exception {
		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.init-param.check-auth-token",
				Boolean.FALSE.toString()
			).build(),
			TEST_PORTLET_ID);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.ACTION_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testNoPortalAuthenticationTokens() throws Exception {
		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		String url = String.valueOf(
			PortletURLFactoryUtil.create(
				PortletContainerTestUtil.getHttpServletRequest(group, layout),
				TEST_PORTLET_ID, layout.getPlid(),
				PortletRequest.ACTION_PHASE));

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SecurityPortletContainerWrapper.class.getName(),
				LoggerTestUtil.DEBUG)) {

			PortletContainerTestUtil.Response response =
				PortletContainerTestUtil.request(url);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertEquals(logEntries.toString(), 1, logEntries.size());

			LogEntry logEntry = logEntries.get(0);

			Throwable throwable = logEntry.getThrowable();

			Assert.assertEquals(
				"User 0 session does not have a CSRF token for " +
					"com.liferay.portlet.SecurityPortletContainerWrapper",
				throwable.getMessage());

			Assert.assertEquals(403, response.getCode());
			Assert.assertFalse(testPortlet.isCalledAction());
		}
	}

	@Test
	public void testPortalAuthenticationToken() throws Exception {
		testPortlet = new ActionRequestTestPortlet();

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		HttpServletRequest httpServletRequest =
			PortletContainerTestUtil.getHttpServletRequest(group, layout);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.getPortalAuthentication(
				httpServletRequest, layout, TEST_PORTLET_ID);

		testPortlet.reset();

		// Make an action request using the portal authentication token

		String url = String.valueOf(
			PortletURLFactoryUtil.create(
				httpServletRequest, TEST_PORTLET_ID, layout.getPlid(),
				PortletRequest.ACTION_PHASE));

		url = HttpComponentsUtil.setParameter(
			url, "p_auth", response.getBody());

		response = PortletContainerTestUtil.request(
			url, Collections.singletonMap("Cookie", response.getCookies()));

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testPortalAuthenticationTokenSecret() throws Exception {
		registerService(
			AuthTokenWhitelist.class, new TestSharedSecretTokenWhitelist(),
			HashMapDictionaryBuilder.<String, Object>put(
				"service.ranking", Integer.MAX_VALUE
			).build());

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						PortletContainerTestUtil.getHttpServletRequest(
							group, layout),
						TEST_PORTLET_ID, layout.getPlid(),
						PortletRequest.ACTION_PHASE)
				).setParameter(
					"p_auth_secret", _SHARED_SECRET
				).buildString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testStrutsAction() throws Exception {
		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				PropsKeys.AUTH_TOKEN_IGNORE_ACTIONS, "/test/portlet/1"
			).put(
				"com.liferay.portlet.struts-path", "test/portlet"
			).build(),
			TEST_PORTLET_ID);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						PortletContainerTestUtil.getHttpServletRequest(
							group, layout),
						TEST_PORTLET_ID, layout.getPlid(),
						PortletRequest.ACTION_PHASE)
				).setParameter(
					"struts_action", "/test/portlet/1"
				).buildString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	@Test
	public void testXCSRFToken() throws Exception {
		testPortlet = new ActionRequestTestPortlet();

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		HttpServletRequest httpServletRequest =
			PortletContainerTestUtil.getHttpServletRequest(group, layout);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.getPortalAuthentication(
				httpServletRequest, layout, TEST_PORTLET_ID);

		testPortlet.reset();

		// Make an action request using the portal authentication token

		String url = String.valueOf(
			PortletURLFactoryUtil.create(
				httpServletRequest, TEST_PORTLET_ID, layout.getPlid(),
				PortletRequest.ACTION_PHASE));

		url = HttpComponentsUtil.removeParameter(url, "p_auth");

		response = PortletContainerTestUtil.request(
			url,
			HashMapBuilder.<String, List<String>>put(
				"Cookie", response.getCookies()
			).put(
				"X-CSRF-Token", Collections.singletonList(response.getBody())
			).build());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledAction());
	}

	private static final String _SHARED_SECRET = "test";

	private static class ActionRequestTestPortlet extends TestPortlet {

		@Override
		public void serveResource(
				ResourceRequest resourceRequest,
				ResourceResponse resourceResponse)
			throws IOException {

			PrintWriter printWriter = resourceResponse.getWriter();

			PortletURL portletURL = resourceResponse.createActionURL();

			String portalAuthenticationToken = MapUtil.getString(
				HttpComponentsUtil.getParameterMap(
					HttpComponentsUtil.getQueryString(portletURL.toString())),
				"p_auth");

			printWriter.write(portalAuthenticationToken);
		}

	}

	private static class DisabledSessionAuthToken extends SessionAuthToken {

		@Override
		public void addCSRFToken(
			HttpServletRequest httpServletRequest,
			LiferayPortletURL liferayPortletURL) {
		}

		@Override
		public void checkCSRFToken(
			HttpServletRequest httpServletRequest, String origin) {
		}

	}

	private static class TestSharedSecretTokenWhitelist
		extends AuthTokenWhitelistImpl {

		@Override
		public boolean isValidSharedSecret(String sharedSecret) {
			return _SHARED_SECRET.equals(sharedSecret);
		}

	}

}