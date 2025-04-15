/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.search.experiences.rest.internal.resource.v1_0;

import com.liferay.portal.json.JSONFactoryImpl;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.util.HashMapBuilder;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.MapUtil;
import com.liferay.portal.kernel.util.TreeMapBuilder;
import com.liferay.portal.search.document.Document;
import com.liferay.portal.search.document.Field;
import com.liferay.portal.search.hits.SearchHit;
import com.liferay.portal.search.searcher.SearchRequest;
import com.liferay.portal.search.searcher.SearchRequestBuilder;
import com.liferay.portal.search.searcher.SearchRequestBuilderFactory;
import com.liferay.portal.search.searcher.SearchResponseBuilder;
import com.liferay.portal.search.searcher.Searcher;
import com.liferay.portal.search.test.util.AssertUtils;
import com.liferay.portal.test.rule.LiferayUnitTestRule;
import com.liferay.portal.vulcan.pagination.Pagination;
import com.liferay.search.experiences.blueprint.exception.InvalidElementInstanceException;
import com.liferay.search.experiences.blueprint.exception.InvalidParameterException;
import com.liferay.search.experiences.blueprint.exception.InvalidQueryEntryException;
import com.liferay.search.experiences.blueprint.exception.InvalidWebCacheItemException;
import com.liferay.search.experiences.blueprint.exception.PrivateIPAddressException;
import com.liferay.search.experiences.blueprint.exception.UnresolvedTemplateVariableException;
import com.liferay.search.experiences.blueprint.search.request.enhancer.SXPBlueprintSearchRequestEnhancer;
import com.liferay.search.experiences.rest.dto.v1_0.Hit;
import com.liferay.search.experiences.rest.dto.v1_0.SXPBlueprint;
import com.liferay.search.experiences.rest.dto.v1_0.SearchHits;
import com.liferay.search.experiences.rest.dto.v1_0.SearchResponse;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.mockito.Mockito;

/**
 * @author André de Oliveira
 * @author Wade Cao
 */
public class SearchResponseResourceImplTest {

	@ClassRule
	@Rule
	public static final LiferayUnitTestRule liferayUnitTestRule =
		LiferayUnitTestRule.INSTANCE;

	@Before
	public void setUp() throws Exception {
		_searchResponseResourceImpl = new SearchResponseResourceImpl();

		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "contextCompany",
			Mockito.mock(Company.class));
		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "contextHttpServletRequest",
			Mockito.mock(HttpServletRequest.class));
		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "contextUser",
			Mockito.mock(User.class));
		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "_jsonFactory", new JSONFactoryImpl());
		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "_searcher", _searcher);
		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "_searchRequestBuilderFactory",
			_searchRequestBuilderFactory);
		ReflectionTestUtil.setFieldValue(
			_searchResponseResourceImpl, "_sxpBlueprintSearchRequestEnhancer",
			_sxpBlueprintSearchRequestEnhancer);

		SearchRequestBuilder searchRequestBuilder = Mockito.mock(
			SearchRequestBuilder.class);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			_searchRequestBuilderFactory
		).builder();

		Mockito.doReturn(
			Mockito.mock(
				com.liferay.portal.search.searcher.SearchResponse.class)
		).when(
			_searcher
		).search(
			Mockito.any()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).companyId(
			Mockito.anyLong()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).from(
			Mockito.anyInt()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).queryString(
			Mockito.nullable(String.class)
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).size(
			Mockito.anyInt()
		);

		Mockito.doReturn(
			searchRequestBuilder
		).when(
			searchRequestBuilder
		).withSearchContext(
			Mockito.any()
		);

		SearchResponseBuilder searchResponseBuilder = Mockito.mock(
			SearchResponseBuilder.class);

		Mockito.doReturn(
			searchResponseBuilder
		).when(
			searchRequestBuilder
		).withSearchContextGet(
			Mockito.any()
		);
	}

	@Test
	public void testSearchErrors() throws Exception {
		RuntimeException runtimeException = new RuntimeException();

		runtimeException.addSuppressed(InvalidQueryEntryException.at(0));
		runtimeException.addSuppressed(InvalidQueryEntryException.at(2));

		Mockito.doThrow(
			runtimeException
		).when(
			_sxpBlueprintSearchRequestEnhancer
		).enhance(
			Mockito.any(), Mockito.nullable(String.class)
		);

		SearchResponse searchResponse = _searchResponseResourceImpl.search(
			Mockito.mock(Pagination.class), null, new SXPBlueprint());

		Map[] errorMaps = searchResponse.getErrors();

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", InvalidQueryEntryException.class.getName()
			).put(
				"localizedMessage", "Error"
			).put(
				"msg", "Invalid query entry at: 0"
			).put(
				"severity", "ERROR"
			).build(),
			errorMaps[0]);
		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", InvalidQueryEntryException.class.getName()
			).put(
				"localizedMessage", "Error"
			).put(
				"msg", "Invalid query entry at: 2"
			).put(
				"severity", "ERROR"
			).build(),
			errorMaps[1]);
	}

	@Test
	public void testSearchWarnings() throws Exception {
		RuntimeException runtimeException = new RuntimeException();

		Throwable throwable1 = InvalidElementInstanceException.at(1);

		Throwable throwable2 = InvalidQueryEntryException.at(2);

		throwable2.addSuppressed(
			UnresolvedTemplateVariableException.with(
				RandomTestUtil.randomString()));

		throwable1.addSuppressed(throwable2);

		runtimeException.addSuppressed(throwable1);

		Throwable throwable3 = InvalidElementInstanceException.at(3);

		throwable3.addSuppressed(
			InvalidParameterException.with(RandomTestUtil.randomString()));

		runtimeException.addSuppressed(throwable3);

		runtimeException.addSuppressed(new InvalidWebCacheItemException());
		runtimeException.addSuppressed(new PrivateIPAddressException());

		Mockito.doThrow(
			runtimeException
		).when(
			_sxpBlueprintSearchRequestEnhancer
		).enhance(
			Mockito.any(), Mockito.nullable(String.class)
		);

		SearchResponse searchResponse = _searchResponseResourceImpl.search(
			Mockito.mock(Pagination.class), null, new SXPBlueprint());

		Map[] errorMaps = searchResponse.getErrors();

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass",
				InvalidElementInstanceException.class.getName()
			).put(
				"localizedMessage", "Element skipped"
			).put(
				"msg", "Invalid element instance at: 1"
			).put(
				"severity", "WARN"
			).put(
				"sxpElementId", "querySXPElement-1"
			).build(),
			errorMaps[0]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", InvalidQueryEntryException.class.getName()
			).put(
				"localizedMessage", "Error"
			).put(
				"msg", "Invalid query entry at: 2"
			).put(
				"severity", "ERROR"
			).put(
				"sxpElementId", "querySXPElement-1"
			).build(),
			errorMaps[1]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass",
				UnresolvedTemplateVariableException.class.getName()
			).put(
				"localizedMessage", "Error"
			).put(
				"severity", "ERROR"
			).put(
				"sxpElementId", "querySXPElement-1"
			).build(),
			errorMaps[2]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass",
				InvalidElementInstanceException.class.getName()
			).put(
				"localizedMessage", "Element skipped"
			).put(
				"msg", "Invalid element instance at: 3"
			).put(
				"severity", "WARN"
			).put(
				"sxpElementId", "querySXPElement-3"
			).build(),
			errorMaps[3]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", InvalidParameterException.class.getName()
			).put(
				"localizedMessage", "Error"
			).put(
				"severity", "ERROR"
			).put(
				"sxpElementId", "querySXPElement-3"
			).build(),
			errorMaps[4]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", InvalidWebCacheItemException.class.getName()
			).put(
				"severity", "WARN"
			).build(),
			errorMaps[5]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", PrivateIPAddressException.class.getName()
			).put(
				"severity", "WARN"
			).build(),
			errorMaps[6]);
	}

	@Test
	public void testToSearchResponse() throws Exception {
		com.liferay.portal.search.searcher.SearchResponse portalSearchResponse =
			_createSearchResponse(
				"{}", "{}",
				_createSearchHits(
					(float)RandomTestUtil.randomDouble(),
					RandomTestUtil.randomLong(),
					new SearchHit[] {
						_createSearchHit(
							RandomTestUtil.randomString(),
							new HashMap<String, Field>(),
							RandomTestUtil.randomString(),
							(float)RandomTestUtil.randomDouble(),
							RandomTestUtil.randomLong())
					}));

		SearchResponse searchResponse =
			_searchResponseResourceImpl.toSearchResponse(portalSearchResponse);

		SearchHits searchHits = searchResponse.getSearchHits();

		Assert.assertArrayEquals(new Hit[0], searchHits.getHits());

		String requestString = JSONUtil.put(
			"query", JSONUtil.put("term", JSONUtil.put("title", "Liferay"))
		).put(
			"took", 5
		).toString();

		String responseString = JSONUtil.put(
			"hits", JSONUtil.put("max_score", RandomTestUtil.randomDouble())
		).put(
			"took", 5
		).toString();

		portalSearchResponse = _createSearchResponse(
			requestString, responseString,
			_createSearchHits(
				(float)RandomTestUtil.randomDouble(),
				RandomTestUtil.randomLong(),
				new SearchHit[] {
					_createSearchHit(
						RandomTestUtil.randomString(),
						TreeMapBuilder.put(
							"field1st", _getField("field1st", "Lone Value")
						).put(
							"field2nd",
							_getField("field2nd", 4, 8, 15, 16, 23, 42)
						).build(),
						RandomTestUtil.randomString(),
						(float)RandomTestUtil.randomDouble(),
						RandomTestUtil.randomLong())
				}));

		searchResponse = _searchResponseResourceImpl.toSearchResponse(
			portalSearchResponse);

		com.liferay.portal.search.hits.SearchHits portalSearchHits =
			portalSearchResponse.getSearchHits();

		searchHits = searchResponse.getSearchHits();

		Assert.assertEquals(
			Float.valueOf(portalSearchHits.getMaxScore()),
			searchHits.getMaxScore());
		Assert.assertEquals(
			Long.valueOf(portalSearchHits.getTotalHits()),
			searchHits.getTotalHits());

		List<SearchHit> list = portalSearchHits.getSearchHits();

		SearchHit portalSearchHit = list.get(0);

		Hit hit = searchHits.getHits()[0];

		Assert.assertEquals(
			"{field1st={\"values\": [\"Lone Value\"]}, field2nd={\"values\": " +
				"[\"4\", \"8\", \"15\", \"16\", \"23\", \"42\"]}}",
			String.valueOf(hit.getDocumentFields()));
		Assert.assertEquals(
			portalSearchHit.getExplanation(), hit.getExplanation());
		Assert.assertEquals(portalSearchHit.getId(), hit.getId());
		Assert.assertEquals(
			Float.valueOf(portalSearchHit.getScore()), hit.getScore());
		Assert.assertEquals(
			Long.valueOf(portalSearchHit.getVersion()), hit.getVersion());

		Assert.assertTrue(searchResponse.getRequest() instanceof Map);
		Assert.assertEquals(requestString, searchResponse.getRequestString());
		Assert.assertTrue(searchResponse.getResponse() instanceof Map);
		Assert.assertEquals(responseString, searchResponse.getResponseString());
	}

	@Test
	public void testWarningsAndErrors() throws Exception {
		InvalidElementInstanceException invalidElementInstanceException =
			InvalidElementInstanceException.at(1);

		Mockito.doThrow(
			invalidElementInstanceException
		).when(
			_sxpBlueprintSearchRequestEnhancer
		).enhance(
			Mockito.any(), Mockito.nullable(String.class)
		);

		NumberFormatException numberFormatException =
			new NumberFormatException();

		Mockito.doThrow(
			numberFormatException
		).when(
			_searcher
		).search(
			Mockito.any()
		);

		SearchResponse searchResponse = _searchResponseResourceImpl.search(
			Mockito.mock(Pagination.class), null, new SXPBlueprint());

		Map[] errorMaps = searchResponse.getErrors();

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass",
				InvalidElementInstanceException.class.getName()
			).put(
				"localizedMessage", "Element skipped"
			).put(
				"msg", "Invalid element instance at: 1"
			).put(
				"severity", "WARN"
			).put(
				"sxpElementId", "querySXPElement-1"
			).build(),
			errorMaps[0]);

		_assertEquals(
			HashMapBuilder.put(
				"exceptionClass", NumberFormatException.class.getName()
			).put(
				"localizedMessage", "Error"
			).put(
				"severity", "ERROR"
			).build(),
			errorMaps[1]);
	}

	private void _assertEquals(
		Map<String, String> expectedMap, Map<String, String> actualMap) {

		Map<String, String> map = new HashMap<>();

		for (Map.Entry<String, String> entry : actualMap.entrySet()) {
			if (expectedMap.containsKey(entry.getKey())) {
				map.put(entry.getKey(), entry.getValue());
			}
		}

		AssertUtils.assertEquals(
			() -> String.valueOf(actualMap), expectedMap, map);
	}

	private SearchHit _createSearchHit(
		String explanation, Map<String, Field> fields, String id, float score,
		long version) {

		if (MapUtil.isEmpty(fields)) {
			return null;
		}

		Document document = Mockito.mock(Document.class);

		Mockito.doReturn(
			fields
		).when(
			document
		).getFields();

		SearchHit searchHit = Mockito.mock(SearchHit.class);

		Mockito.doReturn(
			document
		).when(
			searchHit
		).getDocument();

		Mockito.doReturn(
			explanation
		).when(
			searchHit
		).getExplanation();

		Mockito.doReturn(
			id
		).when(
			searchHit
		).getId();

		Mockito.doReturn(
			score
		).when(
			searchHit
		).getScore();

		Mockito.doReturn(
			version
		).when(
			searchHit
		).getVersion();

		return searchHit;
	}

	private com.liferay.portal.search.hits.SearchHits _createSearchHits(
		float maxScore, long totalHits, SearchHit[] searchHitArray) {

		com.liferay.portal.search.hits.SearchHits searchHits = Mockito.mock(
			com.liferay.portal.search.hits.SearchHits.class);

		if (searchHitArray[0] == null) {
			return searchHits;
		}

		Mockito.doReturn(
			maxScore
		).when(
			searchHits
		).getMaxScore();

		Mockito.doReturn(
			Arrays.asList(searchHitArray)
		).when(
			searchHits
		).getSearchHits();

		Mockito.doReturn(
			totalHits
		).when(
			searchHits
		).getTotalHits();

		return searchHits;
	}

	private com.liferay.portal.search.searcher.SearchResponse
		_createSearchResponse(
			String requestString, String reponseString,
			com.liferay.portal.search.hits.SearchHits searchHits) {

		com.liferay.portal.search.searcher.SearchResponse searchResponse =
			Mockito.mock(
				com.liferay.portal.search.searcher.SearchResponse.class);

		Mockito.doReturn(
			Mockito.mock(SearchRequest.class)
		).when(
			searchResponse
		).getRequest();

		Mockito.doReturn(
			requestString
		).when(
			searchResponse
		).getRequestString();

		Mockito.doReturn(
			reponseString
		).when(
			searchResponse
		).getResponseString();

		Mockito.doReturn(
			searchHits
		).when(
			searchResponse
		).getSearchHits();

		Mockito.doReturn(
			LocaleUtil.US
		).when(
			searchResponse
		).withSearchContextGet(
			Mockito.any()
		);

		return searchResponse;
	}

	private Field _getField(String name, Object... object) {
		return new Field() {

			public String getName() {
				return name;
			}

			public Object getValue() {
				return object;
			}

			public List<Object> getValues() {
				return Arrays.asList(object);
			}

		};
	}

	private final Searcher _searcher = Mockito.mock(Searcher.class);
	private final SearchRequestBuilderFactory _searchRequestBuilderFactory =
		Mockito.mock(SearchRequestBuilderFactory.class);
	private SearchResponseResourceImpl _searchResponseResourceImpl;
	private final SXPBlueprintSearchRequestEnhancer
		_sxpBlueprintSearchRequestEnhancer = Mockito.mock(
			SXPBlueprintSearchRequestEnhancer.class);

}