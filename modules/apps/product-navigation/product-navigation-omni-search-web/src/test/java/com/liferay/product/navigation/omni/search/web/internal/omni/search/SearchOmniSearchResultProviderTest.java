/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.product.navigation.omni.search.web.internal.omni.search;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.language.Language;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.security.permission.PermissionChecker;
import com.liferay.portal.kernel.security.permission.ResourceActions;
import com.liferay.portal.kernel.security.permission.ResourceActionsUtil;
import com.liferay.portal.kernel.service.LayoutLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.Portal;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.product.navigation.omni.search.OmniSearchResult;

import jakarta.portlet.PortletURL;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.MockedStatic;
import org.mockito.Mockito;

/**
 * @author Thiago Buarque
 */
public class SearchOmniSearchResultProviderTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() {
		LanguageUtil languageUtil = new LanguageUtil();

		Language language = Mockito.mock(Language.class);

		Mockito.when(
			language.get(Mockito.any(Locale.class), Mockito.anyString())
		).thenAnswer(
			invocationOnMock -> invocationOnMock.getArgument(1)
		);

		languageUtil.setLanguage(language);

		ResourceActionsUtil resourceActionsUtil = new ResourceActionsUtil();

		Mockito.when(
			_resourceActions.getModelResource(
				Mockito.any(Locale.class), Mockito.anyString())
		).thenReturn(
			_MODEL_RESOURCE
		);

		resourceActionsUtil.setResourceActions(_resourceActions);

		ReflectionTestUtil.setFieldValue(
			_searchOmniSearchResultProvider, "_layoutLocalService",
			_layoutLocalService);
		ReflectionTestUtil.setFieldValue(
			_searchOmniSearchResultProvider, "_portal", _portal);
		ReflectionTestUtil.setFieldValue(
			_searchOmniSearchResultProvider, "_searcher", _searcher);
		ReflectionTestUtil.setFieldValue(
			_searchOmniSearchResultProvider, "_searchRequestBuilderFactory",
			_searchRequestBuilderFactory);

		SearchRequestBuilder searchRequestBuilder = Mockito.mock(
			SearchRequestBuilder.class, Mockito.RETURNS_SELF);

		SearchRequest searchRequest = Mockito.mock(SearchRequest.class);

		Mockito.when(
			searchRequestBuilder.build()
		).thenReturn(
			searchRequest
		);

		Mockito.when(
			_searchRequestBuilderFactory.builder()
		).thenReturn(
			searchRequestBuilder
		);

		Mockito.when(
			_searcher.search(searchRequest)
		).thenReturn(
			_searchResponse
		);

		Mockito.when(
			_searchResponse.getSearchHits()
		).thenReturn(
			_searchHits
		);

		Mockito.when(
			_searchResponse.getTotalHits()
		).thenReturn(
			_TOTAL_HITS
		);

		Mockito.when(
			_portal.getHttpServletRequest(_liferayPortletRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_portal.getOriginalServletRequest(_httpServletRequest)
		).thenReturn(
			_httpServletRequest
		);

		Mockito.when(
			_themeDisplay.getLocale()
		).thenReturn(
			LocaleUtil.US
		);

		Mockito.when(
			_themeDisplay.getPermissionChecker()
		).thenReturn(
			_permissionChecker
		);

		Mockito.when(
			_themeDisplay.getURLHome()
		).thenReturn(
			_REDIRECT
		);
	}

	@After
	public void tearDown() {
		_assetRendererFactoryRegistryUtilMockedStatic.close();
	}

	@Test
	public void testGetOmniSearchResults() throws Exception {
		AssetRenderer<Object> assetRenderer = _setUpAssetRenderer(
			"Welcome Article", true);

		PortletURL editPortletURL = Mockito.mock(PortletURL.class);

		Mockito.when(
			assetRenderer.getURLEdit(
				_liferayPortletRequest, _liferayPortletResponse,
				LiferayWindowState.NORMAL, _REDIRECT)
		).thenReturn(
			editPortletURL
		);

		_setUpAssetRendererFactory(
			assetRenderer, _CLASS_NAME, "icon-web-content", "Web Content");

		_setUpSearchHits(_setUpDocument(_CLASS_NAME, _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		Assert.assertEquals(
			omniSearchResults.toString(), 1, omniSearchResults.size());

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		Assert.assertEquals("search", sectionOmniSearchResult.getIcon());
		Assert.assertEquals(
			"results (" + _TOTAL_HITS + ")",
			sectionOmniSearchResult.getTitle());
		Assert.assertEquals(
			OmniSearchResult.Type.SECTION, sectionOmniSearchResult.getType());

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		Assert.assertEquals(
			entryOmniSearchResults.toString(), 1,
			entryOmniSearchResults.size());

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(
			"Web Content", entryOmniSearchResult.getDescription());
		Assert.assertEquals(
			"icon-web-content", entryOmniSearchResult.getIcon());
		Assert.assertEquals(
			"Welcome Article", entryOmniSearchResult.getTitle());
		Assert.assertEquals(
			OmniSearchResult.Type.ENTRY, entryOmniSearchResult.getType());
		Assert.assertEquals(
			String.valueOf(editPortletURL), entryOmniSearchResult.getURL());
	}

	@Test
	public void testGetOmniSearchResultsIsEmptyWithoutAnAssetRendererFactory()
		throws Exception {

		_setUpSearchHits(_setUpDocument(_CLASS_NAME, _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsResolvesLayouts() throws Exception {
		Layout layout = Mockito.mock(Layout.class);

		Mockito.when(
			layout.getName(LocaleUtil.US)
		).thenReturn(
			"Home"
		);

		Group group = Mockito.mock(Group.class);

		Mockito.when(
			group.getDescriptiveName(LocaleUtil.US)
		).thenReturn(
			"Liferay DXP"
		);

		Mockito.when(
			layout.getGroup()
		).thenReturn(
			group
		);

		Mockito.when(
			_layoutLocalService.fetchLayout(_CLASS_PK)
		).thenReturn(
			layout
		);

		String layoutFriendlyURL = RandomTestUtil.randomString();

		Mockito.when(
			_portal.getLayoutFriendlyURL(layout, _themeDisplay)
		).thenReturn(
			layoutFriendlyURL
		);

		_setUpSearchHits(_setUpDocument(Layout.class.getName(), _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(
			_MODEL_RESOURCE + " - Liferay DXP",
			entryOmniSearchResult.getDescription());
		Assert.assertEquals("page", entryOmniSearchResult.getIcon());
		Assert.assertEquals("Home", entryOmniSearchResult.getTitle());
		Assert.assertEquals(layoutFriendlyURL, entryOmniSearchResult.getURL());
	}

	@Test
	public void testGetOmniSearchResultsSkipsHitsWithABlankAssetURL()
		throws Exception {

		AssetRenderer<Object> assetRenderer = _setUpAssetRenderer(
			"Welcome Article", true);

		Mockito.when(
			assetRenderer.getURLViewInContext(
				_liferayPortletRequest, _liferayPortletResponse, _REDIRECT)
		).thenReturn(
			StringPool.BLANK
		);

		_setUpAssetRendererFactory(
			assetRenderer, _CLASS_NAME, "icon-web-content", "Web Content");

		_setUpSearchHits(_setUpDocument(_CLASS_NAME, _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsSkipsHitsWithoutAnAssetURL()
		throws Exception {

		_setUpAssetRendererFactory(
			_setUpAssetRenderer("Welcome Article", true), _CLASS_NAME,
			"icon-web-content", "Web Content");

		_setUpSearchHits(_setUpDocument(_CLASS_NAME, _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsSkipsHitsWithoutViewPermission()
		throws Exception {

		_setUpAssetRendererFactory(
			_setUpAssetRenderer("Welcome Article", false), _CLASS_NAME,
			"icon-web-content", "Web Content");

		_setUpSearchHits(_setUpDocument(_CLASS_NAME, _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		Assert.assertTrue(
			omniSearchResults.toString(), omniSearchResults.isEmpty());
	}

	@Test
	public void testGetOmniSearchResultsUsesTheURLViewInContext()
		throws Exception {

		AssetRenderer<Object> assetRenderer = _setUpAssetRenderer(
			"Welcome Article", true);

		String viewInContextURL = RandomTestUtil.randomString();

		Mockito.when(
			assetRenderer.getURLViewInContext(
				_liferayPortletRequest, _liferayPortletResponse, _REDIRECT)
		).thenReturn(
			viewInContextURL
		);

		_setUpAssetRendererFactory(
			assetRenderer, _CLASS_NAME, "icon-web-content", "Web Content");

		_setUpSearchHits(_setUpDocument(_CLASS_NAME, _CLASS_PK));

		List<OmniSearchResult> omniSearchResults = _getOmniSearchResults();

		OmniSearchResult sectionOmniSearchResult = omniSearchResults.get(0);

		List<OmniSearchResult> entryOmniSearchResults =
			sectionOmniSearchResult.getOmniSearchResults();

		OmniSearchResult entryOmniSearchResult = entryOmniSearchResults.get(0);

		Assert.assertEquals(viewInContextURL, entryOmniSearchResult.getURL());
	}

	private List<OmniSearchResult> _getOmniSearchResults() throws Exception {
		return _searchOmniSearchResultProvider.getOmniSearchResults(
			RandomTestUtil.randomString(), _liferayPortletRequest,
			_liferayPortletResponse, _themeDisplay);
	}

	private AssetRenderer<Object> _setUpAssetRenderer(
			String title, boolean viewPermission)
		throws Exception {

		AssetRenderer<Object> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.when(
			assetRenderer.getTitle(LocaleUtil.US)
		).thenReturn(
			title
		);

		Mockito.when(
			assetRenderer.hasEditPermission(_permissionChecker)
		).thenReturn(
			true
		);

		Mockito.when(
			assetRenderer.hasViewPermission(_permissionChecker)
		).thenReturn(
			viewPermission
		);

		return assetRenderer;
	}

	private void _setUpAssetRendererFactory(
			AssetRenderer<Object> assetRenderer, String className,
			String iconCssClass, String typeName)
		throws Exception {

		AssetRendererFactory<Object> assetRendererFactory = Mockito.mock(
			AssetRendererFactory.class);

		Mockito.when(
			assetRendererFactory.getAssetRenderer(_CLASS_PK)
		).thenReturn(
			assetRenderer
		);

		Mockito.when(
			assetRendererFactory.getIconCssClass()
		).thenReturn(
			iconCssClass
		);

		Mockito.when(
			assetRendererFactory.getTypeName(LocaleUtil.US)
		).thenReturn(
			typeName
		);

		_assetRendererFactoryRegistryUtilMockedStatic.when(
			() ->
				AssetRendererFactoryRegistryUtil.
					getAssetRendererFactoryByClassName(className)
		).thenReturn(
			assetRendererFactory
		);
	}

	private Document _setUpDocument(String className, long classPK) {
		Document document = Mockito.mock(Document.class);

		Mockito.when(
			document.getString(Field.ENTRY_CLASS_NAME)
		).thenReturn(
			className
		);

		Mockito.when(
			document.getString(Field.ENTRY_CLASS_PK)
		).thenReturn(
			String.valueOf(classPK)
		);

		return document;
	}

	private void _setUpSearchHits(Document... documents) {
		SearchHit[] searchHits = new SearchHit[documents.length];

		for (int i = 0; i < documents.length; i++) {
			searchHits[i] = Mockito.mock(SearchHit.class);

			Mockito.when(
				searchHits[i].getDocument()
			).thenReturn(
				documents[i]
			);
		}

		Mockito.when(
			_searchHits.getSearchHits()
		).thenReturn(
			Arrays.asList(searchHits)
		);
	}

	private static final String _CLASS_NAME = RandomTestUtil.randomString();

	private static final long _CLASS_PK = RandomTestUtil.randomLong();

	private static final String _MODEL_RESOURCE = RandomTestUtil.randomString();

	private static final String _REDIRECT = RandomTestUtil.randomString();

	private static final int _TOTAL_HITS = RandomTestUtil.randomInt(1, 100);

	private final MockedStatic<AssetRendererFactoryRegistryUtil>
		_assetRendererFactoryRegistryUtilMockedStatic = Mockito.mockStatic(
			AssetRendererFactoryRegistryUtil.class);
	private final HttpServletRequest _httpServletRequest = Mockito.mock(
		HttpServletRequest.class);
	private final LayoutLocalService _layoutLocalService = Mockito.mock(
		LayoutLocalService.class);
	private final LiferayPortletRequest _liferayPortletRequest = Mockito.mock(
		LiferayPortletRequest.class);
	private final LiferayPortletResponse _liferayPortletResponse = Mockito.mock(
		LiferayPortletResponse.class);
	private final PermissionChecker _permissionChecker = Mockito.mock(
		PermissionChecker.class);
	private final Portal _portal = Mockito.mock(Portal.class);
	private final ResourceActions _resourceActions = Mockito.mock(
		ResourceActions.class);
	private final Searcher _searcher = Mockito.mock(Searcher.class);
	private final SearchHits _searchHits = Mockito.mock(SearchHits.class);
	private final SearchOmniSearchResultProvider
		_searchOmniSearchResultProvider = new SearchOmniSearchResultProvider();
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory =
		Mockito.mock(SearchRequestBuilderFactory.class);
	private final SearchResponse _searchResponse = Mockito.mock(
		SearchResponse.class);
	private final ThemeDisplay _themeDisplay = Mockito.mock(ThemeDisplay.class);

}