/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.seo;

import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.search.web.internal.portlet.shared.task.helper.PortletSharedRequestHelper;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletPreferences;
import jakarta.portlet.RenderRequest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Amos Fong
 */
public class BasePortletLayoutSEOMetaRobotsProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		PropsUtil.set("feature.flag.LPD-71164", "true");

		_basePortletLayoutSEOMetaRobotsProvider = Mockito.mock(
			BasePortletLayoutSEOMetaRobotsProvider.class,
			Mockito.CALLS_REAL_METHODS);

		_portal = Mockito.mock(Portal.class);

		ReflectionTestUtil.setFieldValue(
			_basePortletLayoutSEOMetaRobotsProvider, "portal", _portal);

		_portletSharedRequestHelper = Mockito.mock(
			PortletSharedRequestHelper.class);

		ReflectionTestUtil.setFieldValue(
			_basePortletLayoutSEOMetaRobotsProvider,
			"portletSharedRequestHelper", _portletSharedRequestHelper);
	}

	@After
	public void tearDown() {
		PropsUtil.set("feature.flag.LPD-71164", "false");
	}

	@Test
	public void testGetContentIndexingDisabledWithSelection() {
		_mockSEOPortletPreferences("categoryId", false);

		Mockito.when(
			_portletSharedRequestHelper.getParameter(
				Mockito.eq("categoryId"), Mockito.any(RenderRequest.class))
		).thenReturn(
			"100"
		);

		Assert.assertEquals(
			"noindex, nofollow",
			_basePortletLayoutSEOMetaRobotsProvider.getContent(
				_createRenderRequest()));
	}

	@Test
	public void testGetContentIndexingDisabledWithoutSelection() {
		_mockSEOPortletPreferences("categoryId", false);

		Mockito.when(
			_portletSharedRequestHelper.getParameter(
				Mockito.eq("categoryId"), Mockito.any(RenderRequest.class))
		).thenReturn(
			null
		);

		Assert.assertEquals(
			StringPool.BLANK,
			_basePortletLayoutSEOMetaRobotsProvider.getContent(
				_createRenderRequest()));
	}

	@Test
	public void testGetContentIndexingEnabled() {
		_mockSEOPortletPreferences("categoryId", true);

		Mockito.when(
			_portletSharedRequestHelper.getParameter(
				Mockito.eq("categoryId"), Mockito.any(RenderRequest.class))
		).thenReturn(
			"100"
		);

		Assert.assertEquals(
			StringPool.BLANK,
			_basePortletLayoutSEOMetaRobotsProvider.getContent(
				_createRenderRequest()));
	}

	private RenderRequest _createRenderRequest() {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.when(
			_portal.getCompanyId(renderRequest)
		).thenReturn(
			_COMPANY_ID
		);

		Mockito.when(
			renderRequest.getPreferences()
		).thenReturn(
			Mockito.mock(PortletPreferences.class)
		);

		return renderRequest;
	}

	private void _mockSEOPortletPreferences(
		String parameterName, boolean webCrawlerIndexingEnabled) {

		SEOPortletPreferences seoPortletPreferences = Mockito.mock(
			SEOPortletPreferences.class);

		Mockito.doReturn(
			seoPortletPreferences
		).when(
			_basePortletLayoutSEOMetaRobotsProvider
		).getSEOPortletPreferences(
			Mockito.any()
		);

		Mockito.when(
			seoPortletPreferences.getSEOParameterName()
		).thenReturn(
			parameterName
		);

		Mockito.when(
			seoPortletPreferences.isWebCrawlerIndexingEnabled()
		).thenReturn(
			webCrawlerIndexingEnabled
		);
	}

	private static final long _COMPANY_ID = RandomTestUtil.randomLong();

	private BasePortletLayoutSEOMetaRobotsProvider
		_basePortletLayoutSEOMetaRobotsProvider;
	private Portal _portal;
	private PortletSharedRequestHelper _portletSharedRequestHelper;

}