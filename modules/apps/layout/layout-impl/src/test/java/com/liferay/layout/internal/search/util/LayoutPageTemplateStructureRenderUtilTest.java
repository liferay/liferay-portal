/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.internal.search.util;

import com.liferay.fragment.model.FragmentEntryLink;
import com.liferay.fragment.renderer.FragmentRendererContext;
import com.liferay.fragment.renderer.FragmentRendererController;
import com.liferay.fragment.service.FragmentEntryLinkLocalServiceUtil;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.util.constants.LayoutDataItemTypeConstants;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONFactory;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.test.TestInfo;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.ServerDetector;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Javier Moral
 */
public class LayoutPageTemplateStructureRenderUtilTest {

	@ClassRule
	public static LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_jsonFactory = JSONFactoryUtil.getJSONFactory();
		_portal = PortalUtil.getPortal();

		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(new JSONFactoryImpl());

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(Mockito.mock(Portal.class));
	}

	@AfterClass
	public static void tearDownClass() {
		JSONFactoryUtil jsonFactoryUtil = new JSONFactoryUtil();

		jsonFactoryUtil.setJSONFactory(_jsonFactory);

		PortalUtil portalUtil = new PortalUtil();

		portalUtil.setPortal(_portal);
	}

	@Test
	@TestInfo("LPD-109403")
	public void testRenderLayoutContent() {
		_testRenderLayoutContentWithJBoss();
		_testRenderLayoutContentWithTomcat();
		_testRenderLayoutContentWithWildfly();
	}

	private FragmentRendererContext _getFragmentRendererContext() {
		FragmentRendererController fragmentRendererController = Mockito.mock(
			FragmentRendererController.class);

		long fragmentEntryLinkId = RandomTestUtil.randomLong();
		LayoutPageTemplateStructure layoutPageTemplateStructure = Mockito.mock(
			LayoutPageTemplateStructure.class);

		Mockito.when(
			layoutPageTemplateStructure.getData(0)
		).thenReturn(
			JSONUtil.put(
				"items",
				JSONUtil.put(
					"item1",
					JSONUtil.put(
						"config",
						JSONUtil.put(
							"fragmentEntryLinkId",
							String.valueOf(fragmentEntryLinkId))
					).put(
						"itemId", "item1"
					).put(
						"parentId", StringPool.BLANK
					).put(
						"type", LayoutDataItemTypeConstants.TYPE_FRAGMENT
					))
			).put(
				"rootItems", JSONUtil.put("main", "item1")
			).toString()
		);

		try (MockedStatic<FragmentEntryLinkLocalServiceUtil>
				fragmentEntryLinkLocalServiceUtilMockedStatic =
					Mockito.mockStatic(
						FragmentEntryLinkLocalServiceUtil.class)) {

			FragmentEntryLink fragmentEntryLink = Mockito.mock(
				FragmentEntryLink.class);

			fragmentEntryLinkLocalServiceUtilMockedStatic.when(
				() -> FragmentEntryLinkLocalServiceUtil.fetchFragmentEntryLink(
					fragmentEntryLinkId)
			).thenReturn(
				fragmentEntryLink
			);

			LayoutPageTemplateStructureRenderUtil.renderLayoutContent(
				fragmentRendererController,
				Mockito.mock(HttpServletRequest.class),
				Mockito.mock(HttpServletResponse.class),
				layoutPageTemplateStructure, LocaleUtil.US, 0);
		}

		ArgumentCaptor<FragmentRendererContext> argumentCaptor =
			ArgumentCaptor.forClass(FragmentRendererContext.class);

		Mockito.verify(
			fragmentRendererController
		).render(
			argumentCaptor.capture(), Mockito.any(), Mockito.any()
		);

		return argumentCaptor.getValue();
	}

	private void _testRenderLayoutContentWithJBoss() {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isJBoss
			).thenReturn(
				true
			);

			FragmentRendererContext fragmentRendererContext =
				_getFragmentRendererContext();

			Assert.assertTrue(fragmentRendererContext.isDisablePortletRender());
		}
	}

	private void _testRenderLayoutContentWithTomcat() {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isTomcat
			).thenReturn(
				true
			);

			FragmentRendererContext fragmentRendererContext =
				_getFragmentRendererContext();

			Assert.assertFalse(
				fragmentRendererContext.isDisablePortletRender());
		}
	}

	private void _testRenderLayoutContentWithWildfly() {
		try (MockedStatic<ServerDetector> serverDetectorMockedStatic =
				Mockito.mockStatic(ServerDetector.class)) {

			serverDetectorMockedStatic.when(
				ServerDetector::isWildfly
			).thenReturn(
				true
			);

			FragmentRendererContext fragmentRendererContext =
				_getFragmentRendererContext();

			Assert.assertTrue(fragmentRendererContext.isDisablePortletRender());
		}
	}

	private static JSONFactory _jsonFactory;
	private static Portal _portal;

}