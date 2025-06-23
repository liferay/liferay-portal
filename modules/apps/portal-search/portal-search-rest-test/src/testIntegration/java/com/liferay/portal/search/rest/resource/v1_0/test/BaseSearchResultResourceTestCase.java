/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.search.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.rest.client.dto.v1_0.SearchResult;
import com.liferay.portal.search.rest.client.http.HttpInvoker;
import com.liferay.portal.search.rest.client.pagination.Page;
import com.liferay.portal.search.rest.client.pagination.Pagination;
import com.liferay.portal.search.rest.client.resource.v1_0.SearchResultResource;
import com.liferay.portal.search.rest.client.serdes.v1_0.SearchResultSerDes;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Petteri Karttunen
 * @generated
 */
@Generated("")
public abstract class BaseSearchResultResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_searchResultResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		searchResultResource = SearchResultResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();
	}

	@After
	public void tearDown() throws Exception {
		GroupTestUtil.deleteGroup(irrelevantGroup);
		GroupTestUtil.deleteGroup(testGroup);
	}

	@Test
	public void testClientSerDesToDTO() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SearchResult searchResult1 = randomSearchResult();

		String json = objectMapper.writeValueAsString(searchResult1);

		SearchResult searchResult2 = SearchResultSerDes.toDTO(json);

		Assert.assertTrue(equals(searchResult1, searchResult2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		SearchResult searchResult = randomSearchResult();

		String json1 = objectMapper.writeValueAsString(searchResult);
		String json2 = SearchResultSerDes.toJSON(searchResult);

		Assert.assertEquals(
			objectMapper.readTree(json1), objectMapper.readTree(json2));
	}

	protected ObjectMapper getClientSerDesObjectMapper() {
		return new ObjectMapper() {
			{
				configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
				configure(
					SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
				enable(SerializationFeature.INDENT_OUTPUT);
				setDateFormat(new ISO8601DateFormat());
				setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
				setSerializationInclusion(JsonInclude.Include.NON_NULL);
				setVisibility(
					PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
				setVisibility(
					PropertyAccessor.GETTER, JsonAutoDetect.Visibility.NONE);
			}
		};
	}

	@Test
	public void testEscapeRegexInStringFields() throws Exception {
		String regex = "^[0-9]+(\\.[0-9]{1,2})\"?";

		SearchResult searchResult = randomSearchResult();

		searchResult.setDescription(regex);
		searchResult.setEntryClassName(regex);
		searchResult.setItemURL(regex);
		searchResult.setTitle(regex);

		String json = SearchResultSerDes.toJSON(searchResult);

		Assert.assertFalse(json.contains(regex));

		searchResult = SearchResultSerDes.toDTO(json);

		Assert.assertEquals(regex, searchResult.getDescription());
		Assert.assertEquals(regex, searchResult.getEntryClassName());
		Assert.assertEquals(regex, searchResult.getItemURL());
		Assert.assertEquals(regex, searchResult.getTitle());
	}

	@Test
	public void testGetSearchPage() throws Exception {
		Page<SearchResult> page = searchResultResource.getSearchPage(
			RandomTestUtil.randomString(), null, RandomTestUtil.randomString(),
			RandomTestUtil.randomString(), null, null, Pagination.of(1, 10),
			null);

		long totalCount = page.getTotalCount();

		SearchResult searchResult1 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		SearchResult searchResult2 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		page = searchResultResource.getSearchPage(
			null, null, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(searchResult1, (List<SearchResult>)page.getItems());
		assertContains(searchResult2, (List<SearchResult>)page.getItems());
		assertValid(page, testGetSearchPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetSearchPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSearchPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		SearchResult searchResult1 = randomSearchResult();

		searchResult1 = testGetSearchPage_addSearchResult(searchResult1);

		for (EntityField entityField : entityFields) {
			Page<SearchResult> page = searchResultResource.getSearchPage(
				null, null, null, null, null,
				getFilterString(entityField, "between", searchResult1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(searchResult1),
				(List<SearchResult>)page.getItems());
		}
	}

	@Test
	public void testGetSearchPageWithFilterDoubleEquals() throws Exception {
		testGetSearchPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSearchPageWithFilterStringContains() throws Exception {
		testGetSearchPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSearchPageWithFilterStringEquals() throws Exception {
		testGetSearchPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSearchPageWithFilterStringStartsWith() throws Exception {
		testGetSearchPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetSearchPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SearchResult searchResult1 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		SearchResult searchResult2 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		for (EntityField entityField : entityFields) {
			Page<SearchResult> page = searchResultResource.getSearchPage(
				null, null, null, null, null,
				getFilterString(entityField, operator, searchResult1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(searchResult1),
				(List<SearchResult>)page.getItems());
		}
	}

	@Test
	public void testGetSearchPageWithPagination() throws Exception {
		Page<SearchResult> searchResultsPage =
			searchResultResource.getSearchPage(
				null, null, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			searchResultsPage.getTotalCount());

		SearchResult searchResult1 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		SearchResult searchResult2 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		SearchResult searchResult3 = testGetSearchPage_addSearchResult(
			randomSearchResult());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<SearchResult> page1 = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(searchResult1, (List<SearchResult>)page1.getItems());

			Page<SearchResult> page2 = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(searchResult2, (List<SearchResult>)page2.getItems());

			Page<SearchResult> page3 = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(searchResult3, (List<SearchResult>)page3.getItems());
		}
		else {
			Page<SearchResult> page1 = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<SearchResult> searchResults1 =
				(List<SearchResult>)page1.getItems();

			Assert.assertEquals(
				searchResults1.toString(), totalCount + 2,
				searchResults1.size());

			Page<SearchResult> page2 = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<SearchResult> searchResults2 =
				(List<SearchResult>)page2.getItems();

			Assert.assertEquals(
				searchResults2.toString(), 1, searchResults2.size());

			Page<SearchResult> page3 = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(searchResult1, (List<SearchResult>)page3.getItems());
			assertContains(searchResult2, (List<SearchResult>)page3.getItems());
			assertContains(searchResult3, (List<SearchResult>)page3.getItems());
		}
	}

	@Test
	public void testGetSearchPageWithSortDateTime() throws Exception {
		testGetSearchPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, searchResult1, searchResult2) -> {
				BeanTestUtil.setProperty(
					searchResult1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSearchPageWithSortDouble() throws Exception {
		testGetSearchPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, searchResult1, searchResult2) -> {
				BeanTestUtil.setProperty(
					searchResult1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					searchResult2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSearchPageWithSortInteger() throws Exception {
		testGetSearchPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, searchResult1, searchResult2) -> {
				BeanTestUtil.setProperty(
					searchResult1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					searchResult2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSearchPageWithSortString() throws Exception {
		testGetSearchPageWithSort(
			EntityField.Type.STRING,
			(entityField, searchResult1, searchResult2) -> {
				Class<?> clazz = searchResult1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						searchResult1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						searchResult2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						searchResult1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						searchResult2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						searchResult1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						searchResult2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSearchPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, SearchResult, SearchResult, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		SearchResult searchResult1 = randomSearchResult();
		SearchResult searchResult2 = randomSearchResult();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, searchResult1, searchResult2);
		}

		searchResult1 = testGetSearchPage_addSearchResult(searchResult1);

		searchResult2 = testGetSearchPage_addSearchResult(searchResult2);

		Page<SearchResult> page = searchResultResource.getSearchPage(
			null, null, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<SearchResult> ascPage = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(
				searchResult1, (List<SearchResult>)ascPage.getItems());
			assertContains(
				searchResult2, (List<SearchResult>)ascPage.getItems());

			Page<SearchResult> descPage = searchResultResource.getSearchPage(
				null, null, null, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(
				searchResult2, (List<SearchResult>)descPage.getItems());
			assertContains(
				searchResult1, (List<SearchResult>)descPage.getItems());
		}
	}

	protected SearchResult testGetSearchPage_addSearchResult(
			SearchResult searchResult)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSearchPage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		SearchResult searchResult, List<SearchResult> searchResults) {

		boolean contains = false;

		for (SearchResult item : searchResults) {
			if (equals(searchResult, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			searchResults + " does not contain " + searchResult, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		SearchResult searchResult1, SearchResult searchResult2) {

		Assert.assertTrue(
			searchResult1 + " does not equal " + searchResult2,
			equals(searchResult1, searchResult2));
	}

	protected void assertEquals(
		List<SearchResult> searchResults1, List<SearchResult> searchResults2) {

		Assert.assertEquals(searchResults1.size(), searchResults2.size());

		for (int i = 0; i < searchResults1.size(); i++) {
			SearchResult searchResult1 = searchResults1.get(i);
			SearchResult searchResult2 = searchResults2.get(i);

			assertEquals(searchResult1, searchResult2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<SearchResult> searchResults1, List<SearchResult> searchResults2) {

		Assert.assertEquals(searchResults1.size(), searchResults2.size());

		for (SearchResult searchResult1 : searchResults1) {
			boolean contains = false;

			for (SearchResult searchResult2 : searchResults2) {
				if (equals(searchResult1, searchResult2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				searchResults2 + " does not contain " + searchResult1,
				contains);
		}
	}

	protected void assertValid(SearchResult searchResult) throws Exception {
		boolean valid = true;

		if (searchResult.getDateCreated() == null) {
			valid = false;
		}

		if (searchResult.getDateModified() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (searchResult.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (searchResult.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("embedded", additionalAssertFieldName)) {
				if (searchResult.getEmbedded() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("entryClassName", additionalAssertFieldName)) {
				if (searchResult.getEntryClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("itemURL", additionalAssertFieldName)) {
				if (searchResult.getItemURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("score", additionalAssertFieldName)) {
				if (searchResult.getScore() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (searchResult.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		Assert.assertTrue(valid);
	}

	protected void assertValid(Page<SearchResult> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<SearchResult> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<SearchResult> searchResults = page.getItems();

		int size = searchResults.size();

		if ((page.getLastPage() > 0) && (page.getPage() > 0) &&
			(page.getPageSize() > 0) && (page.getTotalCount() > 0) &&
			(size > 0)) {

			valid = true;
		}

		Assert.assertTrue(valid);

		assertValid(page.getActions(), expectedActions);
	}

	protected void assertValid(
		Map<String, Map<String, String>> actions1,
		Map<String, Map<String, String>> actions2) {

		for (String key : actions2.keySet()) {
			Map action = actions1.get(key);

			Assert.assertNotNull(key + " does not contain an action", action);

			Map<String, String> expectedAction = actions2.get(key);

			Assert.assertEquals(
				expectedAction.get("method"), action.get("method"));
			Assert.assertEquals(expectedAction.get("href"), action.get("href"));
		}
	}

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.portal.search.rest.dto.v1_0.SearchResult.
						class)) {

			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			graphQLFields.addAll(getGraphQLFields(field));
		}

		return graphQLFields;
	}

	protected List<GraphQLField> getGraphQLFields(
			java.lang.reflect.Field... fields)
		throws Exception {

		List<GraphQLField> graphQLFields = new ArrayList<>();

		for (java.lang.reflect.Field field : fields) {
			com.liferay.portal.vulcan.graphql.annotation.GraphQLField
				vulcanGraphQLField = field.getAnnotation(
					com.liferay.portal.vulcan.graphql.annotation.GraphQLField.
						class);

			if (vulcanGraphQLField != null) {
				Class<?> clazz = field.getType();

				if (clazz.isArray()) {
					clazz = clazz.getComponentType();
				}

				List<GraphQLField> childrenGraphQLFields = getGraphQLFields(
					getDeclaredFields(clazz));

				graphQLFields.add(
					new GraphQLField(field.getName(), childrenGraphQLFields));
			}
		}

		return graphQLFields;
	}

	protected String[] getIgnoredEntityFieldNames() {
		return new String[0];
	}

	protected boolean equals(
		SearchResult searchResult1, SearchResult searchResult2) {

		if (searchResult1 == searchResult2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)searchResult1.getActions(),
						(Map)searchResult2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getDateCreated(),
						searchResult2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getDateModified(),
						searchResult2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getDescription(),
						searchResult2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("embedded", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getEmbedded(),
						searchResult2.getEmbedded())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("entryClassName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getEntryClassName(),
						searchResult2.getEntryClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("itemURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getItemURL(),
						searchResult2.getItemURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("score", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getScore(), searchResult2.getScore())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						searchResult1.getTitle(), searchResult2.getTitle())) {

					return false;
				}

				continue;
			}

			throw new IllegalArgumentException(
				"Invalid additional assert field name " +
					additionalAssertFieldName);
		}

		return true;
	}

	protected boolean equals(
		Map<String, Object> map1, Map<String, Object> map2) {

		if (Objects.equals(map1.keySet(), map2.keySet())) {
			for (Map.Entry<String, Object> entry : map1.entrySet()) {
				if (entry.getValue() instanceof Map) {
					if (!equals(
							(Map)entry.getValue(),
							(Map)map2.get(entry.getKey()))) {

						return false;
					}
				}
				else if (!Objects.deepEquals(
							entry.getValue(), map2.get(entry.getKey()))) {

					return false;
				}
			}

			return true;
		}

		return false;
	}

	protected java.lang.reflect.Field[] getDeclaredFields(Class clazz)
		throws Exception {

		if (clazz.getClassLoader() == null) {
			return new java.lang.reflect.Field[0];
		}

		return TransformUtil.transform(
			ReflectionUtil.getDeclaredFields(clazz),
			field -> {
				if (field.isSynthetic()) {
					return null;
				}

				return field;
			},
			java.lang.reflect.Field.class);
	}

	protected java.util.Collection<EntityField> getEntityFields()
		throws Exception {

		if (!(_searchResultResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_searchResultResource;

		EntityModel entityModel = entityModelResource.getEntityModel(
			new MultivaluedHashMap());

		if (entityModel == null) {
			return Collections.emptyList();
		}

		Map<String, EntityField> entityFieldsMap =
			entityModel.getEntityFieldsMap();

		return entityFieldsMap.values();
	}

	protected List<EntityField> getEntityFields(EntityField.Type type)
		throws Exception {

		return TransformUtil.transform(
			getEntityFields(),
			entityField -> {
				if (!Objects.equals(entityField.getType(), type) ||
					ArrayUtil.contains(
						getIgnoredEntityFieldNames(), entityField.getName())) {

					return null;
				}

				return entityField;
			});
	}

	protected String getFilterString(
		EntityField entityField, String operator, SearchResult searchResult) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = searchResult.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(searchResult.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = searchResult.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(searchResult.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = searchResult.getDescription();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("embedded")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("entryClassName")) {
			Object object = searchResult.getEntryClassName();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("itemURL")) {
			Object object = searchResult.getItemURL();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		if (entityFieldName.equals("score")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = searchResult.getTitle();

			String value = String.valueOf(object);

			if (operator.equals("contains")) {
				sb = new StringBundler();

				sb.append("contains(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 2)) {
					sb.append(value.substring(1, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else if (operator.equals("startswith")) {
				sb = new StringBundler();

				sb.append("startswith(");
				sb.append(entityFieldName);
				sb.append(",'");

				if ((object != null) && (value.length() > 1)) {
					sb.append(value.substring(0, value.length() - 1));
				}
				else {
					sb.append(value);
				}

				sb.append("')");
			}
			else {
				sb.append("'");
				sb.append(value);
				sb.append("'");
			}

			return sb.toString();
		}

		throw new IllegalArgumentException(
			"Invalid entity field " + entityFieldName);
	}

	protected String invoke(String query) throws Exception {
		HttpInvoker httpInvoker = HttpInvoker.newHttpInvoker();

		httpInvoker.body(
			JSONUtil.put(
				"query", query
			).toString(),
			"application/json");
		httpInvoker.httpMethod(HttpInvoker.HttpMethod.POST);
		httpInvoker.path("http://localhost:8080/o/graphql");
		httpInvoker.userNameAndPassword(
			"test@liferay.com:" + PropsValues.DEFAULT_ADMIN_PASSWORD);

		HttpInvoker.HttpResponse httpResponse = httpInvoker.invoke();

		return httpResponse.getContent();
	}

	protected JSONObject invokeGraphQLMutation(GraphQLField graphQLField)
		throws Exception {

		GraphQLField mutationGraphQLField = new GraphQLField(
			"mutation", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(mutationGraphQLField.toString()));
	}

	protected JSONObject invokeGraphQLQuery(GraphQLField graphQLField)
		throws Exception {

		GraphQLField queryGraphQLField = new GraphQLField(
			"query", graphQLField);

		return JSONFactoryUtil.createJSONObject(
			invoke(queryGraphQLField.toString()));
	}

	protected SearchResult randomSearchResult() throws Exception {
		return new SearchResult() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				entryClassName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				itemURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected SearchResult randomIrrelevantSearchResult() throws Exception {
		SearchResult randomIrrelevantSearchResult = randomSearchResult();

		return randomIrrelevantSearchResult;
	}

	protected SearchResult randomPatchSearchResult() throws Exception {
		return randomSearchResult();
	}

	protected SearchResultResource searchResultResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected com.liferay.portal.kernel.model.Group testGroup;

	protected static class BeanTestUtil {

		public static void copyProperties(Object source, Object target)
			throws Exception {

			Class<?> sourceClass = source.getClass();

			Class<?> targetClass = target.getClass();

			for (java.lang.reflect.Field field :
					_getAllDeclaredFields(sourceClass)) {

				if (field.isSynthetic()) {
					continue;
				}

				Method getMethod = _getMethod(
					sourceClass, field.getName(), "get");

				try {
					Method setMethod = _getMethod(
						targetClass, field.getName(), "set",
						getMethod.getReturnType());

					setMethod.invoke(target, getMethod.invoke(source));
				}
				catch (Exception e) {
					continue;
				}
			}
		}

		public static boolean hasProperty(Object bean, String name) {
			Method setMethod = _getMethod(
				bean.getClass(), "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod != null) {
				return true;
			}

			return false;
		}

		public static void setProperty(Object bean, String name, Object value)
			throws Exception {

			Class<?> clazz = bean.getClass();

			Method setMethod = _getMethod(
				clazz, "set" + StringUtil.upperCaseFirstLetter(name));

			if (setMethod == null) {
				throw new NoSuchMethodException();
			}

			Class<?>[] parameterTypes = setMethod.getParameterTypes();

			setMethod.invoke(bean, _translateValue(parameterTypes[0], value));
		}

		private static List<java.lang.reflect.Field> _getAllDeclaredFields(
			Class<?> clazz) {

			List<java.lang.reflect.Field> fields = new ArrayList<>();

			while ((clazz != null) && (clazz != Object.class)) {
				for (java.lang.reflect.Field field :
						clazz.getDeclaredFields()) {

					fields.add(field);
				}

				clazz = clazz.getSuperclass();
			}

			return fields;
		}

		private static Method _getMethod(Class<?> clazz, String name) {
			for (Method method : clazz.getMethods()) {
				if (name.equals(method.getName()) &&
					(method.getParameterCount() == 1) &&
					_parameterTypes.contains(method.getParameterTypes()[0])) {

					return method;
				}
			}

			return null;
		}

		private static Method _getMethod(
				Class<?> clazz, String fieldName, String prefix,
				Class<?>... parameterTypes)
			throws Exception {

			return clazz.getMethod(
				prefix + StringUtil.upperCaseFirstLetter(fieldName),
				parameterTypes);
		}

		private static Object _translateValue(
			Class<?> parameterType, Object value) {

			if ((value instanceof Integer) &&
				parameterType.equals(Long.class)) {

				Integer intValue = (Integer)value;

				return intValue.longValue();
			}

			return value;
		}

		private static final Set<Class<?>> _parameterTypes = new HashSet<>(
			Arrays.asList(
				Boolean.class, Date.class, Double.class, Integer.class,
				Long.class, Map.class, String.class));

	}

	protected class GraphQLField {

		public GraphQLField(String key, GraphQLField... graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(String key, List<GraphQLField> graphQLFields) {
			this(key, new HashMap<>(), graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			GraphQLField... graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = Arrays.asList(graphQLFields);
		}

		public GraphQLField(
			String key, Map<String, Object> parameterMap,
			List<GraphQLField> graphQLFields) {

			_key = key;
			_parameterMap = parameterMap;
			_graphQLFields = graphQLFields;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder(_key);

			if (!_parameterMap.isEmpty()) {
				sb.append("(");

				for (Map.Entry<String, Object> entry :
						_parameterMap.entrySet()) {

					sb.append(entry.getKey());
					sb.append(": ");
					sb.append(entry.getValue());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append(")");
			}

			if (!_graphQLFields.isEmpty()) {
				sb.append("{");

				for (GraphQLField graphQLField : _graphQLFields) {
					sb.append(graphQLField.toString());
					sb.append(", ");
				}

				sb.setLength(sb.length() - 2);

				sb.append("}");
			}

			return sb.toString();
		}

		private final List<GraphQLField> _graphQLFields;
		private final String _key;
		private final Map<String, Object> _parameterMap;

	}

	private static final com.liferay.portal.kernel.log.Log _log =
		LogFactoryUtil.getLog(BaseSearchResultResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.portal.search.rest.resource.v1_0.SearchResultResource
		_searchResultResource;

}