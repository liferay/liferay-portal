/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.tuning.rankings.web.internal.util;

import com.liferay.asset.kernel.model.AssetEntry;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.DocumentBuilder;
import com.liferay.portal.search.tuning.rankings.web.internal.BaseRankingsWebTestCase;
import com.liferay.portal.search.web.interpreter.SearchResultInterpreter;
import com.liferay.portal.test.rule.LiferayUnitTestRule;

import jakarta.portlet.PortletRequest;
import jakarta.portlet.PortletURL;
import jakarta.portlet.ResourceRequest;
import jakarta.portlet.ResourceResponse;

import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author Wade Cao
 */
public class RankingResultUtilTest extends BaseRankingsWebTestCase {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Test
	public void testGetAssetRenderer() {
		_setUpDocumentBuilderFactory();

		SearchResultInterpreter searchResultInterpreter = Mockito.mock(
			SearchResultInterpreter.class);

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			assetRenderer
		).when(
			searchResultInterpreter
		).getAssetRenderer(
			Mockito.any()
		);

		Mockito.doReturn(
			searchResultInterpreter
		).when(
			searchResultInterpreterProvider
		).getSearchResultInterpreter(
			Mockito.anyString()
		);

		Assert.assertEquals(
			assetRenderer,
			RankingResultUtil.getAssetRenderer(
				"entryClassName", Long.valueOf(1111)));
	}

	@Test
	public void testGetRankingResultViewURL() throws Exception {
		Document document = _setUpDocument();
		PortletURL portletURL = _setUpPortletURL();
		ResourceRequest resourceRequest = _setUpResourceRequest("223");
		ResourceResponse resourceResponse = _setUpResourceResponse(portletURL);

		SearchResultInterpreter searchResultInterpreter =
			_setUpGetRankingResultViewURLMocks();

		Mockito.doReturn(
			"444"
		).when(
			searchResultInterpreter
		).getAssetURLViewInContext(
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString()
		);

		Assert.assertEquals(
			"444?inheritRedirect=true&redirect=myurl",
			RankingResultUtil.getRankingResultViewURL(
				document, resourceRequest, resourceResponse, true));
	}

	@Test
	public void testGetRankingResultViewURLException() throws Exception {
		Document document = _setUpDocument();
		PortletURL portletURL = _setUpPortletURL();
		ResourceRequest resourceRequest = _setUpResourceRequest("223");
		ResourceResponse resourceResponse = _setUpResourceResponse(portletURL);

		SearchResultInterpreter searchResultInterpreter =
			_setUpGetRankingResultViewURLMocks();

		Mockito.doThrow(
			Exception.class
		).when(
			searchResultInterpreter
		).getAssetURLViewInContext(
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString()
		);

		Assert.assertEquals(
			StringPool.BLANK,
			RankingResultUtil.getRankingResultViewURL(
				document, resourceRequest, resourceResponse, true));
	}

	private AssetEntry _setUpAssetEntry(AssetEntry assetEntry) {
		if (assetEntry == null) {
			return assetEntry;
		}

		Mockito.doReturn(
			111L
		).when(
			assetEntry
		).getEntryId();

		Mockito.doReturn(
			"222"
		).when(
			assetEntry
		).getLayoutUuid();

		return assetEntry;
	}

	private Document _setUpDocument() throws Exception {
		Document document = Mockito.mock(Document.class);

		Mockito.doReturn(
			"1111"
		).when(
			document
		).getString(
			Mockito.anyString()
		);

		return document;
	}

	private void _setUpDocumentBuilderFactory() {
		DocumentBuilder documentBuilder = Mockito.mock(DocumentBuilder.class);

		Mockito.doReturn(
			documentBuilder
		).when(
			documentBuilder
		).setString(
			Mockito.any(), Mockito.any()
		);

		Mockito.doReturn(
			documentBuilder
		).when(
			documentBuilder
		).setLong(
			Mockito.any(), Mockito.any()
		);

		Mockito.doReturn(
			Mockito.mock(Document.class)
		).when(
			documentBuilder
		).build();

		Mockito.doReturn(
			documentBuilder
		).when(
			documentBuilderFactory
		).builder();
	}

	private SearchResultInterpreter _setUpGetRankingResultViewURLMocks()
		throws Exception {

		_setUpPortalGetCurrentURL("myurl");
		_setUpPortalStripURLAnchor();

		setUpPortalUtil();

		AssetEntry assetEntry = _setUpAssetEntry(
			Mockito.mock(AssetEntry.class));
		SearchResultInterpreter searchResultInterpreter = Mockito.mock(
			SearchResultInterpreter.class);

		Mockito.doReturn(
			assetEntry
		).when(
			searchResultInterpreter
		).getAssetEntry(
			Mockito.any()
		);

		Mockito.doThrow(
			Exception.class
		).when(
			searchResultInterpreter
		).getAssetURLViewInContext(
			Mockito.any(), Mockito.any(), Mockito.any(), Mockito.anyString()
		);

		Mockito.doReturn(
			searchResultInterpreter
		).when(
			searchResultInterpreterProvider
		).getSearchResultInterpreter(
			Mockito.anyString()
		);

		return searchResultInterpreter;
	}

	private void _setUpPortalGetCurrentURL(String currentURL) {
		Mockito.doReturn(
			currentURL
		).when(
			portal
		).getCurrentURL(
			Mockito.any(PortletRequest.class)
		);
	}

	private void _setUpPortalStripURLAnchor() {
		Mockito.doAnswer(
			invocation -> new String[] {
				invocation.getArgument(0, String.class), StringPool.BLANK
			}
		).when(
			portal
		).stripURLAnchor(
			Mockito.anyString(), Mockito.anyString()
		);
	}

	@SuppressWarnings("deprecation")
	private PortletURL _setUpPortletURL() throws Exception {
		PortletURL portletURL = Mockito.mock(PortletURL.class);

		Mockito.doNothing(
		).when(
			portletURL
		).setParameter(
			Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doNothing(
		).when(
			portletURL
		).setPortletMode(
			Mockito.any()
		);

		Mockito.doNothing(
		).when(
			portletURL
		).setWindowState(
			Mockito.any()
		);

		return portletURL;
	}

	private ResourceRequest _setUpResourceRequest(String uuid) {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.doReturn(
			uuid
		).when(
			layout
		).getUuid();

		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			layout
		).when(
			themeDisplay
		).getLayout();

		ResourceRequest resourceRequest = Mockito.mock(ResourceRequest.class);

		Mockito.doReturn(
			themeDisplay
		).when(
			resourceRequest
		).getAttribute(
			Mockito.anyString()
		);

		return resourceRequest;
	}

	private ResourceResponse _setUpResourceResponse(PortletURL portletURL) {
		ResourceResponse resourceResponse = Mockito.mock(
			ResourceResponse.class);

		Mockito.doReturn(
			portletURL
		).when(
			resourceResponse
		).createRenderURL();

		return resourceResponse;
	}

}