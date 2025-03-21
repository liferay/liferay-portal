/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.osgi.web.portlet.container.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.test.util.LayoutFriendlyURLRandomizerBumper;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.application.type.ApplicationType;
import com.liferay.portal.kernel.model.LayoutConstants;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.portlet.PortletQName;
import com.liferay.portal.kernel.portlet.PortletURLFactoryUtil;
import com.liferay.portal.kernel.portlet.url.builder.PortletURLBuilder;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.PortletLocalServiceUtil;
import com.liferay.portal.kernel.test.randomizerbumpers.NumericStringRandomizerBumper;
import com.liferay.portal.kernel.test.randomizerbumpers.UniqueStringRandomizerBumper;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.util.FriendlyURLNormalizerUtil;
import com.liferay.portal.kernel.util.HashMapDictionaryBuilder;
import com.liferay.portal.kernel.util.UnicodeProperties;
import com.liferay.portal.osgi.web.portlet.container.test.util.PortletContainerTestUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Raymond Augé
 */
@RunWith(Arquillian.class)
public class PublicRenderParameterTest extends BasePortletContainerTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new LiferayIntegrationTestRule();

	@Test
	public void testWithModuleLayoutTypeController() throws Exception {
		final String prpName = "categoryId";
		final String prpValue = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE,
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);
		final AtomicBoolean success = new AtomicBoolean(false);

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException {

				PrintWriter printWriter = renderResponse.getWriter();

				String value = renderRequest.getParameter(prpName);

				if (prpValue.equals(value)) {
					success.set(true);
				}

				printWriter.write(value);
			}

		};

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"com.liferay.portlet.application-type",
				new String[] {
					ApplicationType.FULL_PAGE_APPLICATION.toString(),
					ApplicationType.WIDGET.toString()
				}
			).put(
				"jakarta.portlet.supported-public-render-parameter", prpName
			).build(),
			TEST_PORTLET_ID, false);

		Portlet portlet = PortletLocalServiceUtil.getPortletById(
			TestPropsValues.getCompanyId(), TEST_PORTLET_ID);

		Assert.assertFalse(portlet.isUndeployedPortlet());

		String name = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE,
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);

		layout = LayoutLocalServiceUtil.addLayout(
			null, TestPropsValues.getUserId(), group.getGroupId(), false,
			LayoutConstants.DEFAULT_PARENT_LAYOUT_ID, name, null, null,
			"full_page_application", false,
			StringPool.SLASH + FriendlyURLNormalizerUtil.normalize(name),
			ServiceContextTestUtil.getServiceContext());

		UnicodeProperties typeSettingsUnicodeProperties =
			layout.getTypeSettingsProperties();

		typeSettingsUnicodeProperties.setProperty(
			"fullPageApplicationPortlet", TEST_PORTLET_ID);

		LayoutLocalServiceUtil.updateLayout(layout);

		String portletURLString = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				PortletContainerTestUtil.getHttpServletRequest(group, layout),
				TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE)
		).setParameter(
			prpName, prpValue
		).buildString();

		Assert.assertTrue(
			portletURLString,
			portletURLString.contains(
				PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE));

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURLString);

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(success.get());
	}

	@Test
	public void testWithPortalLayoutTypeController() throws Exception {
		final String prpName = "categoryId";
		final String prpValue = RandomTestUtil.randomString(
			LayoutFriendlyURLRandomizerBumper.INSTANCE,
			NumericStringRandomizerBumper.INSTANCE,
			UniqueStringRandomizerBumper.INSTANCE);
		final AtomicBoolean success = new AtomicBoolean(false);

		testPortlet = new TestPortlet() {

			@Override
			public void render(
					RenderRequest renderRequest, RenderResponse renderResponse)
				throws IOException {

				PrintWriter printWriter = renderResponse.getWriter();

				String value = renderRequest.getParameter(prpName);

				if (prpValue.equals(value)) {
					success.set(true);
				}

				printWriter.write(value);
			}

		};

		setUpPortlet(
			testPortlet,
			HashMapDictionaryBuilder.<String, Object>put(
				"jakarta.portlet.supported-public-render-parameter", prpName
			).build(),
			TEST_PORTLET_ID);

		String portletURLString = PortletURLBuilder.create(
			PortletURLFactoryUtil.create(
				PortletContainerTestUtil.getHttpServletRequest(group, layout),
				TEST_PORTLET_ID, layout.getPlid(), PortletRequest.RENDER_PHASE)
		).setParameter(
			prpName, prpValue
		).buildString();

		Assert.assertTrue(
			portletURLString,
			portletURLString.contains(
				PortletQName.PUBLIC_RENDER_PARAMETER_NAMESPACE));

		PortletContainerTestUtil.Response response =
			PortletContainerTestUtil.request(portletURLString);

		Assert.assertEquals(200, response.getCode());

		Assert.assertTrue(success.get());
	}

}