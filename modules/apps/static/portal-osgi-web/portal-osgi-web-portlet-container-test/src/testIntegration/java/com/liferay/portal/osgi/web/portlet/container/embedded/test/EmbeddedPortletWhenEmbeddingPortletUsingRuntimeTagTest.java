/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.portlet.PortletURLFactory;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.PortletPreferencesLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.PortletKeys;
import com.liferay.portal.osgi.web.portlet.container.test.BasePortletContainerTestCase;
import com.liferay.portal.osgi.web.portlet.container.test.TestPortlet;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;

import jakarta.portlet.PortletContext;
import jakarta.portlet.PortletException;
import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletRequestDispatcher;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import java.io.IOException;

import java.util.Dictionary;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manuel de la Peña
 */
@RunWith(Arquillian.class)
public class EmbeddedPortletWhenEmbeddingPortletUsingRuntimeTagTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_layoutTypePortlet = (LayoutTypePortlet)layout.getLayoutType();

		_layoutStaticPortletsAll = PropsValues.LAYOUT_STATIC_PORTLETS_ALL;
	}

	@Test
	public void testShouldNotCreatePortletPreferencesEmbeddedAndRuntimePortlets()
		throws Exception {

		TestPortlet testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				super.render(renderRequest, renderResponse);

				PortletContext portletContext = getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher("/runtime_portlet.jsp");

				portletRequestDispatcher.include(renderRequest, renderResponse);
			}

		};

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		setUpPortlet(testPortlet, properties, TEST_PORTLET_ID);

		TestRuntimePortlet testRuntimePortlet = new TestRuntimePortlet();
		String testRuntimePortletId = "testRuntimePortletId";

		setUpPortlet(
			testRuntimePortlet, properties, testRuntimePortletId, false);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					_portletURLFactory.create(
						PortletContainerTestUtil.getHttpServletRequest(
							group, layout),
						TEST_PORTLET_ID, layout.getPlid(),
						PortletRequest.RENDER_PHASE)
				).setParameters(
					HashMapBuilder.put(
						"persistSettings",
						new String[] {Boolean.FALSE.toString()}
					).put(
						"testRuntimePortletId",
						new String[] {testRuntimePortletId}
					).build()
				).buildString());

		Assert.assertEquals(
			"portletPreferences count should be 0", 0,
			_portletPreferencesLocalService.getPortletPreferencesCount(
				PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
				testRuntimePortletId));

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledRender());
		Assert.assertTrue(testRuntimePortlet.isCalledRender());
	}

	@Test
	public void testShouldRenderEmbeddedAndRuntimePortlets() throws Exception {
		TestPortlet testPortlet = new TestPortlet() {

			@Override
			public void serveResource(
					ResourceRequest resourceRequest,
					ResourceResponse resourceResponse)
				throws IOException, PortletException {

				super.serveResource(resourceRequest, resourceResponse);

				PortletContext portletContext = getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher("/runtime_portlet.jsp");

				portletRequestDispatcher.include(
					resourceRequest, resourceResponse);
			}

		};

		Dictionary<String, Object> properties = new HashMapDictionary<>();

		setUpPortlet(testPortlet, properties, TEST_PORTLET_ID, false);

		_portletPreferencesLocalService.addPortletPreferences(
			TestPropsValues.getCompanyId(), PortletKeys.PREFS_OWNER_ID_DEFAULT,
			PortletKeys.PREFS_OWNER_TYPE_LAYOUT, layout.getPlid(),
			TEST_PORTLET_ID, null, null);

		TestRuntimePortlet testRuntimePortlet = new TestRuntimePortlet();
		String testRuntimePortletId = "testRuntimePortletId";

		setUpPortlet(
			testRuntimePortlet, properties, testRuntimePortletId, false);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				PortletURLBuilder.create(
					_portletURLFactory.create(
						PortletContainerTestUtil.getHttpServletRequest(
							group, layout),
						TEST_PORTLET_ID, layout.getPlid(),
						PortletRequest.RESOURCE_PHASE)
				).setParameter(
					"testRuntimePortletId", testRuntimePortletId
				).buildString());

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(testPortlet.isCalledServeResource());
		Assert.assertTrue(testRuntimePortlet.isCalledRuntime());
	}

	@Test
	public void testShouldRenderPortletResourcesEmbeddedAndRuntimePortlets()
		throws Exception {

		TestPortlet testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				super.render(renderRequest, renderResponse);

				PortletContext portletContext = getPortletContext();

				PortletRequestDispatcher portletRequestDispatcher =
					portletContext.getRequestDispatcher(
						"/runtime_custom_portlet.jsp");

				portletRequestDispatcher.include(renderRequest, renderResponse);
			}

		};

		setUpPortlet(
			testPortlet, new HashMapDictionary<>(), TEST_PORTLET_ID + "1");
		setUpPortlet(
			testPortlet, new HashMapDictionary<>(), TEST_PORTLET_ID + "2");

		TestRuntimePortlet testRuntimePortlet = new TestRuntimePortlet();

		String[] paths = {
			RandomTestUtil.randomString(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), RandomTestUtil.randomString()
		};

		setUpPortlet(
			testRuntimePortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.footer-portlet-css", "/" + paths[0]
			).put(
				"com.liferay.portlet.footer-portlet-javascript", "/" + paths[1]
			).put(
				"com.liferay.portlet.header-portlet-css", "/" + paths[2]
			).put(
				"com.liferay.portlet.header-portlet-javascript", "/" + paths[3]
			).build(),
			"testRuntimePortletId", false);

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(
				layout.getRegularURL(
					PortletContainerTestUtil.getHttpServletRequest(
						group, layout)));

		Assert.assertEquals(200, response.getCode());

		String body = response.getBody();

		for (String path : paths) {
			Assert.assertTrue(body.contains(path));
			Assert.assertTrue(body.indexOf(path) == body.lastIndexOf(path));
		}
	}

	private static String[] _layoutStaticPortletsAll;
	private static LayoutTypePortlet _layoutTypePortlet;

	@Inject
	private Language _language;

	@Inject
	private PortletPreferencesLocalService _portletPreferencesLocalService;

	@Inject
	private PortletURLFactory _portletURLFactory;

	@Inject
	private UserLocalService _userLocalService;

}