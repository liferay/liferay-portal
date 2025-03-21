/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.HttpComponentsUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.URLCodec;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portlet.SecurityPortletContainerWrapper;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;
import jakarta.portlet.WindowState;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Collections;
import java.util.Dictionary;
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
public class RenderRequestPortletContainerTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testInvalidPortletId() throws Exception {
		String url = StringBundler.concat(
			layout.getRegularURL(
				PortletContainerTestUtil.getHttpServletRequest(group, layout)),
			"?p_p_id=", URLCodec.encodeURL("'\"><script>alert(1)</script>"),
			"&p_p_lifecycle=0&p_p_state=exclusive");

		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SecurityPortletContainerWrapper.class.getName(),
				LoggerTestUtil.WARN)) {

			PortletContainerTestUtil.Response response =
				PortletContainerTestUtil.request(url);

			List<LogEntry> logEntries = logCapture.getLogEntries();

			int totalExpectedEvents = 2;

			Assert.assertEquals(
				logEntries.toString(), totalExpectedEvents, logEntries.size());

			for (int i = 0; i < totalExpectedEvents; i++) {
				LogEntry logEntry = logEntries.get(i);

				Assert.assertEquals(
					"Invalid portlet ID '\"><script>alert(1)</script>",
					logEntry.getMessage());
			}

			Assert.assertEquals(200, response.getCode());
		}
	}

	@Test
	public void testIsAccessGrantedByPortletAuthenticationToken()
		throws Exception {

		TestPortlet testTargetPortlet = new TestPortlet();

		Dictionary<String, Object> properties =
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.add-default-resource", Boolean.TRUE
			).put(
				"com.liferay.portlet.system", Boolean.TRUE
			).build();

		final String testTargetPortletId = "testTargetPortletId";

		setUpPortlet(testTargetPortlet, properties, testTargetPortletId, false);

		testPortlet = new TestPortlet() {

			@Override
			public void serveResource(
					ResourceRequest resourceRequest,
					ResourceResponse resourceResponse)
				throws IOException {

				PrintWriter printWriter = resourceResponse.getWriter();

				PortletURL portletURL = PortletURLFactoryUtil.create(
					resourceRequest, testTargetPortletId, layout.getPlid(),
					PortletRequest.RENDER_PHASE);

				printWriter.write(
					MapUtil.getString(
						HttpComponentsUtil.getParameterMap(
							HttpComponentsUtil.getQueryString(
								portletURL.toString())),
						"p_p_auth"));
			}

		};

		properties = new HashMapDictionary<>();

		setUpPortlet(testPortlet, properties, TEST_PORTLET_ID);

		// Get the portlet authentication token by making a resource request

		HttpServletRequest httpServletRequest =
			PortletContainerTestUtil.getHttpServletRequest(group, layout);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			httpServletRequest, TEST_PORTLET_ID, layout.getPlid(),
			PortletRequest.RESOURCE_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		testTargetPortlet.reset();

		// Make a render request to the target portlet using the portlet
		// authentication token

		String url = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				httpServletRequest, testTargetPortletId, layout.getPlid(),
				PortletRequest.RENDER_PHASE)
		).setWindowState(
			WindowState.MAXIMIZED
		).buildString();

		url = HttpComponentsUtil.setParameter(
			url, "p_p_auth", response.getBody());

		response = PortletContainerTestUtil.request(
			url, Collections.singletonMap("Cookie", response.getCookies()));

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testTargetPortlet.isCalledRender());
	}

	@Test
	public void testIsAccessGrantedByPortletOnPage() throws Exception {
		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
	}

	@Test
	public void testIsAccessGrantedByRuntimePortlet() throws Exception {
		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				PortletContext portletContext = getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher("/runtime_portlet.jsp");

				portletRequestDispatcher.include(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		TestPortlet testRuntimePortlet = new TestPortlet();
		String testRuntimePortletId = "testRuntimePortletId";

		setUpPortlet(
			testRuntimePortlet, new HashMapDictionary<String, Object>(),
			testRuntimePortletId, false);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					PortletURLFactoryUtil.create(
						PortletContainerTestUtil.getHttpServletRequest(
							group, layout),
						TEST_PORTLET_ID, layout.getPlid(),
						PortletRequest.RENDER_PHASE)
				).setParameter(
					"testRuntimePortletId", testRuntimePortletId
				).buildString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testRuntimePortlet.isCalledRender());
	}

}