/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.web.internal.search.results.portlet;

import com.liferay.asset.kernel.service.AssetEntryLocalService;
import com.liferay.asset.util.AssetRendererFactoryLookup;
import com.liferay.portal.configuration.module.configuration.ConfigurationProviderUtil;
import com.liferay.portal.kernel.dao.search.SearchContainer;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.portlet.LiferayPortletConfig;
import com.liferay.portal.kernel.search.Document;
import com.liferay.portal.kernel.search.DocumentImpl;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.Indexer;
import com.liferay.portal.kernel.search.IndexerRegistry;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.PropsTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.PortletDisplay;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.summary.Summary;
import com.liferay.portal.search.summary.SummaryBuilder;
import com.liferay.portal.search.summary.SummaryBuilderFactory;
import com.liferay.portal.search.web.internal.display.context.PortletURLFactory;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchRequest;
import com.liferay.portal.search.web.portlet.shared.search.PortletSharedSearchResponse;
import com.liferay.portal.search.web.search.request.SearchSettings;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.util.PortalImpl;

import jakarta.portlet.PortletException;
import jakarta.portlet.PortletURL;
import jakarta.portlet.RenderRequest;
import jakarta.portlet.RenderResponse;
import jakarta.portlet.RenderURL;

import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

import java.util.Arrays;
import java.util.Collections;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.ArgumentCaptor;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author André de Oliveira
 */
public class SearchResultsPortletTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@BeforeClass
	public static void setUpClass() {
		_configurationProviderUtilMockedStatic = Mockito.mockStatic(
			ConfigurationProviderUtil.class);
	}

	@AfterClass
	public static void tearDownClass() {
		_configurationProviderUtilMockedStatic.close();
	}

	@Before
	public void setUp() throws Exception {
		_setUpPortalUtil();
		_setUpPortletSharedSearchResponse();
		_setUpPropsUtil();
		_setUpSearchSettings();
		_setUpUserLocalService();

		_portletURLFactory = _createPortletURLFactory();
		_renderRequest = _createRenderRequest();
		_renderResponse = _createRenderResponse();

		_searchResultsPortlet = _createSearchResultsPortlet();

		ReflectionTestUtil.setFieldValue(
			_searchResultsPortlet, "_portal", PortalUtil.getPortal());
	}

	@Test
	public void testDocumentWithoutSummaryIsRemoved() throws Exception {
		Document document = _createDocumentWithSummary();

		_setUpSearchResponseDocuments(document, _createDocument());

		render();

		_assertDisplayContextDocuments(document);
	}

	@Test
	public void testGetIteratorURL() throws Exception {
		Mockito.doReturn(
			"/search?delta=10&start=2"
		).when(
			_renderRequest
		).getAttribute(
			WebKeys.CURRENT_URL
		);

		render();

		SearchResultsPortletDisplayContext searchResultsPortletDisplayContext =
			_getDisplayContext();

		SearchContainer<Document> searchContainer =
			searchResultsPortletDisplayContext.getSearchContainer();

		Assert.assertEquals(
			"/search?delta=10",
			String.valueOf(searchContainer.getIteratorURL()));
	}

	protected void render() throws IOException, PortletException {
		_searchResultsPortlet.render(_renderRequest, _renderResponse);
	}

	private void _assertDisplayContextDocuments(Document... expectedDocuments) {
		SearchResultsPortletDisplayContext searchResultsPortletDisplayContext =
			_getDisplayContext();

		Assert.assertEquals(
			Arrays.asList(expectedDocuments),
			searchResultsPortletDisplayContext.getDocuments());
	}

	private Document _createDocument() {
		Document document = new DocumentImpl();

		String className = RandomTestUtil.randomString();

		document.addKeyword(Field.ENTRY_CLASS_NAME, className);

		return document;
	}

	private Document _createDocumentWithSummary() throws Exception {
		Document document = new DocumentImpl();

		String className = RandomTestUtil.randomString();

		document.addKeyword(Field.ENTRY_CLASS_NAME, className);

		Mockito.doReturn(
			_createIndexerWithSummary()
		).when(
			_indexerRegistry
		).getIndexer(
			className
		);

		return document;
	}

	private Indexer<?> _createIndexerWithSummary() throws Exception {
		Indexer<?> indexer = Mockito.mock(Indexer.class);

		Mockito.doReturn(
			new com.liferay.portal.kernel.search.Summary(null, null, null)
		).when(
			indexer
		).getSummary(
			Mockito.any(), Mockito.anyString(), Mockito.any(), Mockito.any()
		);

		return indexer;
	}

	private PortletSharedSearchRequest _createPortletSharedSearchRequest() {
		PortletSharedSearchRequest portletSharedSearchRequest = Mockito.mock(
			PortletSharedSearchRequest.class);

		Mockito.doReturn(
			_portletSharedSearchResponse
		).when(
			portletSharedSearchRequest
		).search(
			Mockito.any()
		);

		return portletSharedSearchRequest;
	}

	private PortletURLFactory _createPortletURLFactory() throws Exception {
		PortletURLFactory portletURLFactory = Mockito.mock(
			PortletURLFactory.class);

		Mockito.doReturn(
			Mockito.mock(PortletURL.class)
		).when(
			portletURLFactory
		).getPortletURL();

		return portletURLFactory;
	}

	private RenderRequest _createRenderRequest() {
		RenderRequest renderRequest = Mockito.mock(RenderRequest.class);

		Mockito.doReturn(
			RandomTestUtil.randomString()
		).when(
			renderRequest
		).getParameter(
			"mvcPath"
		);

		Mockito.doReturn(
			RenderRequest.RENDER_MARKUP
		).when(
			renderRequest
		).getAttribute(
			RenderRequest.RENDER_PART
		);

		return renderRequest;
	}

	private RenderResponse _createRenderResponse() {
		RenderResponse renderResponse = Mockito.mock(RenderResponse.class);

		Mockito.doReturn(
			Mockito.mock(RenderURL.class)
		).when(
			renderResponse
		).createRenderURL();

		return renderResponse;
	}

	private SearchResultsPortlet _createSearchResultsPortlet()
		throws Exception {

		SearchResultsPortlet searchResultsPortlet = new SearchResultsPortlet() {
			{
				assetEntryLocalService = Mockito.mock(
					AssetEntryLocalService.class);
				assetRendererFactoryLookup = Mockito.mock(
					AssetRendererFactoryLookup.class);
				indexerRegistry = _indexerRegistry;
				portletSharedSearchRequest =
					_createPortletSharedSearchRequest();
				resourceActions = Mockito.mock(ResourceActions.class);
				summaryBuilderFactory = _createSummaryBuilderFactory();
				userLocalService = _userLocalService;
			}

			@Override
			public void init() {
			}

			@Override
			protected void doDispatch(
				RenderRequest renderRequest, RenderResponse renderResponse) {
			}

			@Override
			protected String getCurrentURL(RenderRequest renderRequest) {
				return RandomTestUtil.randomString();
			}

			@Override
			protected HttpServletRequest getHttpServletRequest(
				RenderRequest renderRequest) {

				ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

				Mockito.when(
					themeDisplay.getPortletDisplay()
				).thenReturn(
					Mockito.mock(PortletDisplay.class)
				);

				HttpServletRequest httpServletRequest = Mockito.mock(
					HttpServletRequest.class);

				Mockito.when(
					(ThemeDisplay)httpServletRequest.getAttribute(
						WebKeys.THEME_DISPLAY)
				).thenReturn(
					themeDisplay
				);

				return httpServletRequest;
			}

			@Override
			protected PortletURLFactory getPortletURLFactory(
				RenderRequest renderRequest, RenderResponse renderResponse) {

				return _portletURLFactory;
			}

		};

		searchResultsPortlet.init(Mockito.mock(LiferayPortletConfig.class));

		return searchResultsPortlet;
	}

	private SummaryBuilderFactory _createSummaryBuilderFactory() {
		SummaryBuilder summaryBuilder = Mockito.mock(SummaryBuilder.class);

		Mockito.doReturn(
			Mockito.mock(Summary.class)
		).when(
			summaryBuilder
		).build();

		SummaryBuilderFactory summaryBuilderFactory = Mockito.mock(
			SummaryBuilderFactory.class);

		Mockito.doReturn(
			summaryBuilder
		).when(
			summaryBuilderFactory
		).newInstance();

		return summaryBuilderFactory;
	}

	private SearchResultsPortletDisplayContext _getDisplayContext() {
		ArgumentCaptor<SearchResultsPortletDisplayContext> argumentCaptor =
			ArgumentCaptor.forClass(SearchResultsPortletDisplayContext.class);

		Mockito.verify(
			_renderRequest
		).setAttribute(
			Mockito.eq(WebKeys.PORTLET_DISPLAY_CONTEXT),
			argumentCaptor.capture()
		);

		return argumentCaptor.getValue();
	}

	private void _setUpPortalUtil() {
		ReflectionTestUtil.setFieldValue(
			PortalUtil.class, "_portal", new PortalImpl());
	}

	private void _setUpPortletSharedSearchResponse() {
		Mockito.doReturn(
			null
		).when(
			_portletSharedSearchResponse
		).getKeywords();

		Mockito.doReturn(
			null
		).when(
			_portletSharedSearchResponse
		).getPortletPreferences(
			Mockito.any()
		);

		Mockito.doReturn(
			_searchResponse
		).when(
			_portletSharedSearchResponse
		).getFederatedSearchResponse(
			Mockito.any()
		);

		Mockito.doReturn(
			_searchRequest
		).when(
			_searchResponse
		).getRequest();

		Mockito.doReturn(
			_searchSettings
		).when(
			_portletSharedSearchResponse
		).getSearchSettings();

		Mockito.doReturn(
			new ThemeDisplay()
		).when(
			_portletSharedSearchResponse
		).getThemeDisplay(
			Mockito.any()
		);
	}

	private void _setUpPropsUtil() {
		PropsTestUtil.setProps(Collections.emptyMap());
	}

	private void _setUpSearchResponseDocuments(Document... documents) {
		Mockito.doReturn(
			Arrays.asList(documents)
		).when(
			_searchResponse
		).getDocuments71();
	}

	private void _setUpSearchSettings() {
		Mockito.when(
			_searchSettings.getSearchContext()
		).thenReturn(
			_searchContext
		);
	}

	private void _setUpUserLocalService() {
		Mockito.doReturn(
			Mockito.mock(User.class)
		).when(
			_userLocalService
		).fetchUser(
			Mockito.anyLong()
		);
	}

	private static MockedStatic<ConfigurationProviderUtil>
		_configurationProviderUtilMockedStatic;

	private final IndexerRegistry _indexerRegistry = Mockito.mock(
		IndexerRegistry.class);
	private final PortletSharedSearchResponse _portletSharedSearchResponse =
		Mockito.mock(PortletSharedSearchResponse.class);
	private PortletURLFactory _portletURLFactory;
	private RenderRequest _renderRequest;
	private RenderResponse _renderResponse = Mockito.mock(RenderResponse.class);
	private final SearchContext _searchContext = Mockito.mock(
		SearchContext.class);
	private final SearchRequest _searchRequest = Mockito.mock(
		SearchRequest.class);
	private final SearchResponse _searchResponse = Mockito.mock(
		SearchResponse.class);
	private SearchResultsPortlet _searchResultsPortlet;
	private final SearchSettings _searchSettings = Mockito.mock(
		SearchSettings.class);
	private final UserLocalService _userLocalService = Mockito.mock(
		UserLocalService.class);

}