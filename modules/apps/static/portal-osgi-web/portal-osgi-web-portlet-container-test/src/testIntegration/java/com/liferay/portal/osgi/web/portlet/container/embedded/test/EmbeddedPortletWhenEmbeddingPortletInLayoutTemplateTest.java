/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.embedded.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.model.LayoutTemplateConstants;
import com.liferay.portal.kernel.model.LayoutTypePortlet;
import com.liferay.portal.kernel.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.kernel.servlet.HttpMethods;
import com.liferay.portal.kernel.template.TemplateConstants;
import com.liferay.portal.kernel.test.portlet.MockLiferayPortletRenderResponse;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapDictionary;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.JavaConstants;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.layoutconfiguration.util.RuntimePageUtil;
import com.liferay.portal.osgi.web.portlet.container.test.BasePortletContainerTestCase;
import com.liferay.portal.osgi.web.portlet.container.test.TestPortlet;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletException;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Jorge Díaz
 */
@RunWith(Arquillian.class)
public class EmbeddedPortletWhenEmbeddingPortletInLayoutTemplateTest
	extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testRenderEmbeddedPortlet() throws Exception {
		setUpPortlet(
			testPortlet, new HashMapDictionary<>(), TEST_PORTLET_ID, false);

		_processLayoutTemplate(
			"${processor.processPortlet(\"" + TEST_PORTLET_ID + "\")}");

		Assert.assertTrue(testPortlet.isCalledRender());
	}

	@Test
	public void testRenderEmbeddedPortletFollowsRenderWeights()
		throws Exception {

		List<String> renderedPortlets = new ArrayList<>();

		_setUpPortletWithRenderWeight(
			renderedPortlets, "TEST_EMBEDDED_PORTLET_1", 1, false);
		_setUpPortletWithRenderWeight(
			renderedPortlets, "TEST_EMBEDDED_PORTLET_2", 2, false);
		_setUpPortletWithRenderWeight(
			renderedPortlets, "TEST_EMBEDDED_PORTLET_3", 3, false);
		_setUpPortletWithRenderWeight(
			renderedPortlets, "TEST_COLUMN-1_PORTLET_1", 4, true);
		_setUpPortletWithRenderWeight(
			renderedPortlets, "TEST_COLUMN-1_PORTLET_2", 5, true);

		_processLayoutTemplate(
			StringBundler.concat(
				"${processor.processPortlet(\"TEST_EMBEDDED_PORTLET_3\")}",
				"${processor.processPortlet(\"TEST_EMBEDDED_PORTLET_1\")}",
				"${processor.processPortlet(\"TEST_EMBEDDED_PORTLET_2\")}",
				"${processor.processColumn(\"column-1\"",
				"\"portlet-column-content portlet-column-content-first\")}"));

		Assert.assertEquals(
			Arrays.asList(
				"TEST_COLUMN-1_PORTLET_2", "TEST_COLUMN-1_PORTLET_1",
				"TEST_EMBEDDED_PORTLET_3", "TEST_EMBEDDED_PORTLET_2",
				"TEST_EMBEDDED_PORTLET_1"),
			renderedPortlets);
	}

	private void _processLayoutTemplate(String templateContent)
		throws Exception {

		MockHttpServletRequest mockHttpServletRequest =
			(MockHttpServletRequest)
				PortletContainerTestUtil.getHttpServletRequest(group, layout);

		mockHttpServletRequest.setAttribute(
			JavaConstants.JAKARTA_PORTLET_RESPONSE,
			new MockLiferayPortletRenderResponse());
		mockHttpServletRequest.setAttribute(
			WebKeys.CTX, mockHttpServletRequest.getServletContext());
		mockHttpServletRequest.setAttribute(
			WebKeys.CURRENT_URL, RandomTestUtil.randomString());
		mockHttpServletRequest.setMethod(HttpMethods.GET);

		ThemeDisplay themeDisplay =
			(ThemeDisplay)mockHttpServletRequest.getAttribute(
				WebKeys.THEME_DISPLAY);

		themeDisplay.setLayoutTypePortlet(
			(LayoutTypePortlet)layout.getLayoutType());
		themeDisplay.setLocale(LocaleUtil.getDefault());

		LayoutSet layoutSet = group.getPublicLayoutSet();

		themeDisplay.setLookAndFeel(
			layoutSet.getTheme(), layoutSet.getColorScheme());

		themeDisplay.setPermissionChecker(
			PermissionCheckerFactoryUtil.create(TestPropsValues.getUser()));
		themeDisplay.setRealUser(TestPropsValues.getUser());

		RuntimePageUtil.processTemplate(
			mockHttpServletRequest, new MockHttpServletResponse(), null,
			_TEMPLATE_ID, templateContent, TemplateConstants.LANG_TYPE_FTL);
	}

	private void _setUpPortletWithRenderWeight(
			List<String> renderedPortlets, String portletName, int renderWeight,
			boolean addToLayout)
		throws Exception {

		TestPortlet testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException, PortletException {

				super.render(renderRequest, renderResponse);

				renderedPortlets.add(portletName);
			}

		};

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.render-weight", renderWeight
			).build(),
			portletName, addToLayout);
	}

	private static final String _TEMPLATE_ID =
		"themeId" + LayoutTemplateConstants.CUSTOM_SEPARATOR + "testId";

}