/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.test.util.suggestions;

import com.liferay.asset.kernel.AssetRendererFactoryRegistryUtil;
import com.liferay.asset.kernel.model.AssetRenderer;
import com.liferay.asset.kernel.model.AssetRendererFactory;
import com.liferay.osgi.service.tracker.collections.map.ServiceTrackerMap;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.ClassName;
import com.liferay.portal.kernel.portlet.LiferayPortletRequest;
import com.liferay.portal.kernel.portlet.LiferayPortletResponse;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.search.Field;
import com.liferay.portal.kernel.search.SearchContext;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.theme.ThemeDisplay;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.ListUtil;
import com.liferay.portal.search.asset.AssetURLViewProvider;
import com.liferay.portal.search.constants.SearchContextAttributes;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.hits.SearchHits;
import com.liferay.portal.search.internal.suggestions.SuggestionBuilderFactoryImpl;
import com.liferay.portal.search.internal.suggestions.SuggestionsContributorResultsBuilderFactoryImpl;
import com.liferay.portal.search.rest.dto.v1_0.SuggestionsContributorConfiguration;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponse;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.spi.suggestions.SuggestionsContributor;
import com.liferay.portal.search.suggestions.SuggestionsContributorResults;

import jakarta.portlet.MutableRenderParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

/**
 * @author Bryan Engler
 */
public abstract class BaseSuggestionsContributorTestCase {

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		_setUpAssetURLViewProvider();
		_setUpLiferayPortletRequest();
		_setUpLiferayPortletResponse();
		_setUpSearchContext();
		_setUpSearchRequestBuilderFactory();
		_setUpSuggestionsContributor();
		_setUpSuggestionsContributorConfiguration();
	}

	@Test
	public void testSearchHitsWithZeroTotalHits() throws Exception {
		setUpSearcher(0);

		Assert.assertNull(getSuggestionsContributorResults());

		Mockito.verify(
			_liferayPortletRequest, Mockito.never()
		).getAttribute(
			Mockito.anyString()
		);
	}

	@Test
	public void testSearchTuningRankingsIsContributed() throws Exception {
		setUpSearcher(0);

		getSuggestionsContributorResults();

		ArgumentCaptor<Consumer<SearchContext>> argumentCaptor =
			ArgumentCaptor.forClass(
				(Class<Consumer<SearchContext>>)(Class)Consumer.class);

		Mockito.verify(
			_searchRequestBuilder
		).withSearchContext(
			argumentCaptor.capture()
		);

		Consumer<SearchContext> argumentCaptorValue = argumentCaptor.getValue();

		SearchContext searchContext = Mockito.mock(SearchContext.class);

		argumentCaptorValue.accept(searchContext);

		Mockito.verify(
			searchContext
		).setAttribute(
			SearchContextAttributes.ATTRIBUTE_KEY_CONTRIBUTE_TUNING_RANKINGS,
			Boolean.TRUE
		);
	}

	@Rule
	public TestName testName = new TestName();

	protected abstract String getKeywords();

	protected abstract SuggestionsContributor getSuggestionsContributor();

	protected SuggestionsContributorResults getSuggestionsContributorResults() {
		return _suggestionsContributor.getSuggestionsContributorResults(
			_liferayPortletRequest, _liferayPortletResponse, _searchContext,
			_suggestionsContributorConfiguration);
	}

	protected void setUpAssetRendererFactoryRegistryUtil(
			boolean assetRendererFactoryNull, String title, String summary)
		throws Exception {

		setUpAssetRendererFactoryRegistryUtil(
			assetRendererFactoryNull, null, title, summary);
	}

	protected void setUpAssetRendererFactoryRegistryUtil(
			boolean assetRendererFactoryNull, String className, String title,
			String summary)
		throws Exception {

		ReflectionTestUtil.setFieldValue(
			AssetRendererFactoryRegistryUtil.class,
			"_classNameAssetRenderFactoriesServiceTrackerMap",
			_serviceTrackerMap);

		if (assetRendererFactoryNull) {
			Mockito.doReturn(
				null
			).when(
				_serviceTrackerMap
			).getService(
				Mockito.anyString()
			);

			return;
		}

		AssetRenderer<?> assetRenderer = Mockito.mock(AssetRenderer.class);

		Mockito.doReturn(
			summary
		).when(
			assetRenderer
		).getSummary(
			_liferayPortletRequest, _liferayPortletResponse
		);

		Mockito.doReturn(
			title
		).when(
			assetRenderer
		).getTitle(
			Mockito.any()
		);

		Mockito.doReturn(
			assetRenderer
		).when(
			assetRendererFactory
		).getAssetRenderer(
			Mockito.anyLong()
		);

		Mockito.when(
			_serviceTrackerMap.getService(Mockito.anyString())
		).thenAnswer(
			invocation -> {
				if (Objects.nonNull(className) &&
					Objects.equals(invocation.getArgument(0), className)) {

					return null;
				}

				return assetRendererFactory;
			}
		);
	}

	protected void setUpAssetRendererFactoryRegistryUtil(
			String title, String summary)
		throws Exception {

		setUpAssetRendererFactoryRegistryUtil(false, null, title, summary);
	}

	protected void setUpSearcher(long totalHits) throws Exception {
		SearchResponse searchResponse = Mockito.mock(SearchResponse.class);

		SearchHits searchHits = Mockito.mock(SearchHits.class);

		List<SearchHit> searchHitsList = new ArrayList<>();

		for (int i = 1; i <= totalHits; i++) {
			ClassName className = Mockito.mock(ClassName.class);

			Mockito.doReturn(
				"Class Name " + i
			).when(
				className
			).getClassName();

			Mockito.doReturn(
				className
			).when(
				_classNameLocalService
			).getClassName(
				Mockito.eq(Long.valueOf(i))
			);

			SearchHit searchHit = Mockito.mock(SearchHit.class);

			Document document = Mockito.mock(Document.class);

			Mockito.doReturn(
				Long.valueOf(1)
			).when(
				document
			).getLong(
				Mockito.eq(Field.ENTRY_CLASS_PK)
			);

			Mockito.doReturn(
				"Class Name 1"
			).when(
				document
			).getString(
				Mockito.eq(Field.ENTRY_CLASS_NAME)
			);

			Mockito.doReturn(
				"Document Title " + i
			).when(
				document
			).getString(
				Mockito.startsWith("title")
			);

			Mockito.doReturn(
				ListUtil.fromArray("Document Text " + i)
			).when(
				document
			).getStrings(
				Mockito.anyString()
			);

			Mockito.doReturn(
				Long.valueOf(i)
			).when(
				document
			).getValue(
				Mockito.eq(Field.CLASS_NAME_ID)
			);

			Mockito.doReturn(
				Long.valueOf(i)
			).when(
				document
			).getValue(
				Mockito.eq(Field.CLASS_PK)
			);

			Mockito.doReturn(
				document
			).when(
				searchHit
			).getDocument();

			Mockito.doReturn(
				Float.valueOf(i)
			).when(
				searchHit
			).getScore();

			searchHitsList.add(searchHit);
		}

		Mockito.doReturn(
			searchHitsList
		).when(
			searchHits
		).getSearchHits();

		Mockito.doReturn(
			Long.valueOf(totalHits)
		).when(
			searchHits
		).getTotalHits();

		Mockito.doReturn(
			searchHits
		).when(
			searchResponse
		).getSearchHits();

		Mockito.doReturn(
			searchResponse
		).when(
			searcher
		).search(
			Mockito.any()
		);
	}

	protected void setUpSuggestionsContributorConfiguration(String textField) {
		_suggestionsContributorConfiguration.setAttributes(
			HashMapBuilder.<String, Object>put(
				"fields", ListUtil.fromArray("field")
			).put(
				"sxpBlueprintId", RandomTestUtil.randomLong()
			).put(
				"textField", textField
			).build());
		_suggestionsContributorConfiguration.setSize(
			RandomTestUtil.randomInt());
	}

	@Mock
	protected AssetRendererFactory<?> assetRendererFactory;

	@Mock
	protected Searcher searcher;

	private void _setUpAssetURLViewProvider() {
		Mockito.when(
			_assetURLViewProvider.getAssetURLView(
				Mockito.any(), Mockito.any(), Mockito.anyString(),
				Mockito.anyLong(), Mockito.any(), Mockito.any())
		).thenAnswer(
			invocation ->
				invocation.getArgument(2, String.class) + StringPool.UNDERLINE +
					invocation.getArgument(3)
		);
	}

	private void _setUpLiferayPortletRequest() {
		ThemeDisplay themeDisplay = Mockito.mock(ThemeDisplay.class);

		Mockito.doReturn(
			RandomTestUtil.randomLong()
		).when(
			themeDisplay
		).getScopeGroupId();

		Mockito.doReturn(
			themeDisplay
		).when(
			_liferayPortletRequest
		).getAttribute(
			Mockito.anyString()
		);
	}

	private void _setUpLiferayPortletResponse() {
		LiferayPortletURL liferayPortletURL = Mockito.mock(
			LiferayPortletURL.class);

		Mockito.doReturn(
			liferayPortletURL
		).when(
			_liferayPortletResponse
		).createLiferayPortletURL(
			Mockito.anyLong(), Mockito.anyString(), Mockito.anyString()
		);

		Mockito.doReturn(
			Mockito.mock(MutableRenderParameters.class)
		).when(
			liferayPortletURL
		).getRenderParameters();
	}

	private void _setUpSearchContext() {
		Mockito.doReturn(
			getKeywords()
		).when(
			_searchContext
		).getKeywords();

		Mockito.doReturn(
			"test"
		).when(
			_searchContext
		).getAttribute(
			Mockito.anyString()
		);
	}

	private void _setUpSearchRequestBuilderFactory() {
		Mockito.doReturn(
			Mockito.mock(SearchRequest.class)
		).when(
			_searchRequestBuilder
		).build();

		Mockito.doReturn(
			_searchRequestBuilder
		).when(
			_searchRequestBuilder
		).from(
			Mockito.anyInt()
		);

		Mockito.doReturn(
			_searchRequestBuilder
		).when(
			_searchRequestBuilder
		).queryString(
			Mockito.anyString()
		);

		Mockito.doReturn(
			_searchRequestBuilder
		).when(
			_searchRequestBuilder
		).size(
			Mockito.anyInt()
		);

		Mockito.doReturn(
			_searchRequestBuilder
		).when(
			_searchRequestBuilder
		).withSearchContext(
			Mockito.any(Consumer.class)
		);

		Mockito.doReturn(
			_searchRequestBuilder
		).when(
			_searchRequestBuilderFactory
		).builder();
	}

	private void _setUpSuggestionsContributor() {
		_suggestionsContributor = getSuggestionsContributor();

		ReflectionTestUtil.setFieldValue(
			_suggestionsContributor, "_assetURLViewProvider",
			_assetURLViewProvider);
		ReflectionTestUtil.setFieldValue(
			_suggestionsContributor, "_classNameLocalService",
			_classNameLocalService);
		ReflectionTestUtil.setFieldValue(
			_suggestionsContributor, "_searcher", searcher);
		ReflectionTestUtil.setFieldValue(
			_suggestionsContributor, "_searchRequestBuilderFactory",
			_searchRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_suggestionsContributor,
			"_suggestionsContributorResultsBuilderFactory",
			new SuggestionsContributorResultsBuilderFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_suggestionsContributor, "_suggestionBuilderFactory",
			new SuggestionBuilderFactoryImpl());
	}

	private void _setUpSuggestionsContributorConfiguration() {
		_suggestionsContributorConfiguration =
			new SuggestionsContributorConfiguration();

		_suggestionsContributorConfiguration.setDisplayGroupName(
			testName.getMethodName());
	}

	@Mock
	private AssetURLViewProvider _assetURLViewProvider;

	@Mock
	private ClassNameLocalService _classNameLocalService;

	@Mock
	private LiferayPortletRequest _liferayPortletRequest;

	@Mock
	private LiferayPortletResponse _liferayPortletResponse;

	@Mock
	private SearchContext _searchContext;

	private final SearchRequestBuilder _searchRequestBuilder = Mockito.mock(
		SearchRequestBuilder.class);

	@Mock
	private SearchRequestBuilderFactory _searchRequestBuilderFactory;

	@Mock
	private ServiceTrackerMap<String, AssetRendererFactory<?>>
		_serviceTrackerMap;

	private SuggestionsContributor _suggestionsContributor;
	private SuggestionsContributorConfiguration
		_suggestionsContributorConfiguration;

}