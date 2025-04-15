/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.events.ActionException;
import com.liferay.portal.kernel.events.LifecycleAction;
import com.liferay.portal.kernel.events.LifecycleEvent;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletSession;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class SharedSessionPortletContainerTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testPrivateFalsePortalToPortlet() throws Exception {
		final String attributeKey = "TEST_ATTRIBUTE";
		final String attributeValue = "TEST_VALUE";
		final AtomicReference<Object> sessionValue = new AtomicReference<>();

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				PortletSession portletSession =
					renderRequest.getPortletSession();

				Object value = portletSession.getAttribute(
					attributeKey, PortletSession.APPLICATION_SCOPE);

				sessionValue.set(value);

				super.render(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.private-session-attributes", Boolean.FALSE
			).build(),
			TEST_PORTLET_ID);

		LifecycleAction lifecycleAction = new LifecycleAction() {

			@Override
			public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
				throws ActionException {

				HttpServletRequest httpServletRequest =
					lifecycleEvent.getRequest();

				HttpSession httpSession = httpServletRequest.getSession(true);

				httpSession.setAttribute(attributeKey, attributeValue);
			}

		};

		registerService(
			LifecycleAction.class, lifecycleAction,
			HashMapDictionaryBuilder.<String, Object>put(
				"key", "servlet.service.events.pre"
			).build());

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
		Assert.assertEquals(attributeValue, sessionValue.get());
	}

	@Test
	public void testPrivateFalsePortletToPortal() throws Exception {
		final String attributeKey = "TEST_ATTRIBUTE";
		final String attributeValue = "TEST_VALUE";
		final AtomicReference<Object> sessionValue = new AtomicReference<>();

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				PortletSession portletSession =
					renderRequest.getPortletSession();

				portletSession.setAttribute(
					attributeKey, attributeValue,
					PortletSession.APPLICATION_SCOPE);

				super.render(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.private-session-attributes", Boolean.FALSE
			).build(),
			TEST_PORTLET_ID);

		LifecycleAction lifecycleAction = new LifecycleAction() {

			@Override
			public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
				throws ActionException {

				HttpServletRequest httpServletRequest =
					lifecycleEvent.getRequest();

				HttpSession httpSession = httpServletRequest.getSession(true);

				Object value = httpSession.getAttribute(attributeKey);

				sessionValue.set(value);
			}

		};

		registerService(
			LifecycleAction.class, lifecycleAction,
			HashMapDictionaryBuilder.<String, Object>put(
				"key", "servlet.service.events.post"
			).build());

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
		Assert.assertEquals(attributeValue, sessionValue.get());
	}

	@Test
	public void testPrivateTruePortalToPortlet() throws Exception {
		final String attributeKey = "TEST_ATTRIBUTE";
		final String attributeValue = "TEST_VALUE";
		final AtomicReference<Object> sessionValue = new AtomicReference<>();

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				PortletSession portletSession =
					renderRequest.getPortletSession();

				Object value = portletSession.getAttribute(
					attributeKey, PortletSession.APPLICATION_SCOPE);

				sessionValue.set(value);

				super.render(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		LifecycleAction lifecycleAction = new LifecycleAction() {

			@Override
			public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
				throws ActionException {

				HttpServletRequest httpServletRequest =
					lifecycleEvent.getRequest();

				HttpSession httpSession = httpServletRequest.getSession(true);

				httpSession.setAttribute(attributeKey, attributeValue);
			}

		};

		registerService(
			LifecycleAction.class, lifecycleAction,
			HashMapDictionaryBuilder.<String, Object>put(
				"key", "servlet.service.events.pre"
			).build());

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
		Assert.assertNotEquals(attributeValue, sessionValue.get());
	}

	@Test
	public void testPrivateTruePortalToPortletSharedAttribute()
		throws Exception {

		final String attributeKey = "LIFERAY_SHARED_TEST_ATTRIBUTE";
		final String attributeValue = "TEST_VALUE";
		final AtomicReference<Object> sessionValue = new AtomicReference<>();

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				PortletSession portletSession =
					renderRequest.getPortletSession();

				Object value = portletSession.getAttribute(
					attributeKey, PortletSession.APPLICATION_SCOPE);

				sessionValue.set(value);

				super.render(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		LifecycleAction lifecycleAction = new LifecycleAction() {

			@Override
			public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
				throws ActionException {

				HttpServletRequest httpServletRequest =
					lifecycleEvent.getRequest();

				HttpSession httpSession = httpServletRequest.getSession(true);

				httpSession.setAttribute(attributeKey, attributeValue);
			}

		};

		registerService(
			LifecycleAction.class, lifecycleAction,
			HashMapDictionaryBuilder.<String, Object>put(
				"key", "servlet.service.events.pre"
			).build());

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
		Assert.assertEquals(attributeValue, sessionValue.get());
	}

	@Test
	public void testPrivateTruePortletToPortal() throws Exception {
		final String attributeKey = "TEST_ATTRIBUTE";
		final String attributeValue = "TEST_VALUE";
		final AtomicReference<Object> sessionValue = new AtomicReference<>();

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				PortletSession portletSession =
					renderRequest.getPortletSession();

				portletSession.setAttribute(
					attributeKey, attributeValue,
					PortletSession.APPLICATION_SCOPE);

				super.render(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet, new HashMapDictionary<String, Object>(),
			TEST_PORTLET_ID);

		LifecycleAction lifecycleAction = new LifecycleAction() {

			@Override
			public void processLifecycleEvent(LifecycleEvent lifecycleEvent)
				throws ActionException {

				HttpServletRequest httpServletRequest =
					lifecycleEvent.getRequest();

				HttpSession httpSession = httpServletRequest.getSession(true);

				Object value = httpSession.getAttribute(attributeKey);

				sessionValue.set(value);
			}

		};

		registerService(
			LifecycleAction.class, lifecycleAction,
			HashMapDictionaryBuilder.<String, Object>put(
				"key", "servlet.service.events.post"
			).build());

		PortletURL portletURL = PortletURLFactoryUtil.create(
			PortletContainerTestUtil.getHttpServletRequest(group, layout),
			TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURL.toString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
		Assert.assertNotEquals(attributeValue, sessionValue.get());
	}

}