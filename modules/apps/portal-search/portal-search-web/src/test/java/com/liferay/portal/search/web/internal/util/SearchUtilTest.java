/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.util;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.asset.kernel.service.AssetEntryLocalServiceUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.test.log.LogCapture;
import com.liferay.portal.test.log.LogEntry;
import com.liferay.portal.test.log.LoggerTestUtil;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Selena Aungst
 */
public class SearchUtilTest {

	@ClassRule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_assetEntryLocalServiceUtilMockedStatic.reset();
		_assetRendererFactoryRegistryUtilMockedStatic.reset();

		_setUpAssetRendererFactoryRegistryUtil();
		_setUpPortalUtil();
		_setUpRenderResponse();
	}

	@Test
	public void testGetSearchResultViewURL() {
		_setUpAssetEntryLocalServiceUtil(true);

		Assert.assertEquals(
			_URL_VIEW_CONTENT,
			SearchUtil.getSearchResultViewURL(
				_renderRequest, _renderResponse, _CLASS_NAME, _CLASS_PK,
				false));
	}

	@Test
	public void testGetSearchResultViewURLWithInvisibleAssetEntry() {
		_setUpAssetEntryLocalServiceUtil(false);

		Assert.assertEquals(
			_URL_VIEW_IN_CONTEXT,
			SearchUtil.getSearchResultViewURL(
				_renderRequest, _renderResponse, _CLASS_NAME, _CLASS_PK,
				false));
	}

	@Test
	public void testGetSearchResultViewURLWithoutAssetEntry() {
		try (LogCapture logCapture = LoggerTestUtil.configureLog4JLogger(
				SearchUtil.class.getName(), LoggerTestUtil.ERROR)) {

			Assert.assertEquals(
				StringPool.BLANK,
				SearchUtil.getSearchResultViewURL(
					_renderRequest, _renderResponse, _CLASS_NAME, _CLASS_PK,
					false));

			List<LogEntry> logEntries = logCapture.getLogEntries();

			Assert.assertTrue(logEntries.isEmpty());
		}
	}

	private void _setUpAssetEntryLocalServiceUtil(boolean visible) {
		AssetEntry assetEntry = Mockito.mock(AssetEntry.class);

		Mockito.doReturn(
			visible
		).when(
			assetEntry
		).isVisible();

		_assetEntryLocalServiceUtilMockedStatic.when(
			() -> AssetEntryLocalServiceUtil.fetchEntry(_CLASS_NAME, _CLASS_PK)
		).thenReturn(
			assetEntry
		);
	}

	private void _setUpAssetRendererFactoryRegistryUtil() throws Exception {
		AssetRendererFactory<?> assetRendererFactory = Mockito.mock(
			AssetRendererFactory.class);

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			_URL_VIEW_IN_CONTEXT
		).when(
			assetRenderer
		).getURLViewInContext(
			_liferayPortletRequest, _liferayPortletResponse, _URL_VIEW_CONTENT
		);

		Mockito.doReturn(
			assetRenderer
		).when(
			assetRendererFactory
		).getAssetRenderer(
			_CLASS_PK
		);

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() ->
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(_CLASS_NAME)
		).thenReturn(
			assetRendererFactory
		);
	}

	private void _setUpPortalUtil() {
		PortalUtil portalUtil = new PortalUtil();

		Portal portal = Mockito.mock(Portal.class);

		Mockito.doReturn(
			_liferayPortletRequest
		).when(
			portal
		).getLiferayPortletRequest(
			_renderRequest
		);

		Mockito.doReturn(
			_liferayPortletResponse
		).when(
			portal
		).getLiferayPortletResponse(
			_renderResponse
		);

		portalUtil.setPortal(portal);
	}

	private void _setUpRenderResponse() {
		RenderURL renderURL = Mockito.mock(RenderURL.class);

		Mockito.doReturn(
			_URL_VIEW_CONTENT
		).when(
			renderURL
		).toString();

		Mockito.doReturn(
			renderURL
		).when(
			_renderResponse
		).createRenderURL();
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _CLASS_PK = RandomTestUtil.randomLong();

	private static final String _URL_VIEW_CONTENT =
		RandomTestUtil.randomString();

	private static final String _URL_VIEW_IN_CONTEXT =
		RandomTestUtil.randomString();

	private static final MockedStatic<AssetEntryLocalServiceUtil>
		_assetEntryLocalServiceUtilMockedStatic = Mockito.mockStatic(
			AssetEntryLocalServiceUtil.class);
	private static final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);

	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final RenderRequest _renderRequest = Mockito.mock(
		RenderRequest.class);
	private final RenderResponse _renderResponse = Mockito.mock(
		RenderResponse.class);

}