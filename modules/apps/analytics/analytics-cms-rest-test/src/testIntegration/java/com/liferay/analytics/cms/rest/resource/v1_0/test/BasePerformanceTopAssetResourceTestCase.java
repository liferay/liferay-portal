/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.analytics.cms.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.analytics.cms.rest.client.dto.v1_0.PerformanceTopAsset;
import com.liferay.analytics.cms.rest.client.http.HttpInvoker;
import com.liferay.analytics.cms.rest.client.pagination.Page;
import com.liferay.analytics.cms.rest.client.pagination.Pagination;
import com.liferay.analytics.cms.rest.client.resource.v1_0.PerformanceTopAssetResource;
import com.liferay.analytics.cms.rest.client.serdes.v1_0.PerformanceTopAssetSerDes;
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
import com.liferay.portal.kernel.test.util.JAXRSWhiteboardTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PortalUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
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
 * @author Rachael Koestartyo
 * @generated
 */
@Generated("")
public abstract class BasePerformanceTopAssetResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_format = FastDateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

		JAXRSWhiteboardTestUtil.ensureReady();
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_performanceTopAssetResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		performanceTopAssetResource = PerformanceTopAssetResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(),
			PortalUtil.getPortalServerPort(false), "http"
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

		PerformanceTopAsset performanceTopAsset1 = randomPerformanceTopAsset();

		String json = objectMapper.writeValueAsString(performanceTopAsset1);

		PerformanceTopAsset performanceTopAsset2 =
			PerformanceTopAssetSerDes.toDTO(json);

		Assert.assertTrue(equals(performanceTopAsset1, performanceTopAsset2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PerformanceTopAsset performanceTopAsset = randomPerformanceTopAsset();

		String json1 = objectMapper.writeValueAsString(performanceTopAsset);
		String json2 = PerformanceTopAssetSerDes.toJSON(performanceTopAsset);

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

		PerformanceTopAsset performanceTopAsset = randomPerformanceTopAsset();

		performanceTopAsset.setClassName(regex);
		performanceTopAsset.setExternalReferenceCode(regex);
		performanceTopAsset.setTitle(regex);
		performanceTopAsset.setType(regex);

		String json = PerformanceTopAssetSerDes.toJSON(performanceTopAsset);

		Assert.assertFalse(json.contains(regex));

		performanceTopAsset = PerformanceTopAssetSerDes.toDTO(json);

		Assert.assertEquals(regex, performanceTopAsset.getClassName());
		Assert.assertEquals(
			regex, performanceTopAsset.getExternalReferenceCode());
		Assert.assertEquals(regex, performanceTopAsset.getTitle());
		Assert.assertEquals(regex, performanceTopAsset.getType());
	}

	@Test
	public void testGetPerformanceTopAssetExport() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetPerformanceTopAssetPage() throws Exception {
		Page<PerformanceTopAsset> page =
			performanceTopAssetResource.getPerformanceTopAssetPage(
				null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		PerformanceTopAsset performanceTopAsset1 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		PerformanceTopAsset performanceTopAsset2 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		page = performanceTopAssetResource.getPerformanceTopAssetPage(
			null, null, null, null, Pagination.of(1, (int)totalCount + 2),
			null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			performanceTopAsset1, (List<PerformanceTopAsset>)page.getItems());
		assertContains(
			performanceTopAsset2, (List<PerformanceTopAsset>)page.getItems());
		assertValid(page, testGetPerformanceTopAssetPage_getExpectedActions());
	}

	protected Map<String, Map<String, String>>
			testGetPerformanceTopAssetPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPerformanceTopAssetPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		PerformanceTopAsset performanceTopAsset1 = randomPerformanceTopAsset();

		performanceTopAsset1 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				performanceTopAsset1);

		for (EntityField entityField : entityFields) {
			Page<PerformanceTopAsset> page =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null,
					getFilterString(
						entityField, "between", performanceTopAsset1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(performanceTopAsset1),
				(List<PerformanceTopAsset>)page.getItems());
		}
	}

	@Test
	public void testGetPerformanceTopAssetPageWithFilterDoubleEquals()
		throws Exception {

		testGetPerformanceTopAssetPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPerformanceTopAssetPageWithFilterStringContains()
		throws Exception {

		testGetPerformanceTopAssetPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPerformanceTopAssetPageWithFilterStringEquals()
		throws Exception {

		testGetPerformanceTopAssetPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPerformanceTopAssetPageWithFilterStringStartsWith()
		throws Exception {

		testGetPerformanceTopAssetPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPerformanceTopAssetPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		PerformanceTopAsset performanceTopAsset1 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PerformanceTopAsset performanceTopAsset2 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		for (EntityField entityField : entityFields) {
			Page<PerformanceTopAsset> page =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null,
					getFilterString(
						entityField, operator, performanceTopAsset1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(performanceTopAsset1),
				(List<PerformanceTopAsset>)page.getItems());
		}
	}

	@Test
	public void testGetPerformanceTopAssetPageWithPagination()
		throws Exception {

		Page<PerformanceTopAsset> performanceTopAssetsPage =
			performanceTopAssetResource.getPerformanceTopAssetPage(
				null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			performanceTopAssetsPage.getTotalCount());

		PerformanceTopAsset performanceTopAsset1 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		PerformanceTopAsset performanceTopAsset2 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		PerformanceTopAsset performanceTopAsset3 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				randomPerformanceTopAsset());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PerformanceTopAsset> page1 =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				performanceTopAsset1,
				(List<PerformanceTopAsset>)page1.getItems());

			Page<PerformanceTopAsset> page2 =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				performanceTopAsset2,
				(List<PerformanceTopAsset>)page2.getItems());

			Page<PerformanceTopAsset> page3 =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				performanceTopAsset3,
				(List<PerformanceTopAsset>)page3.getItems());
		}
		else {
			Page<PerformanceTopAsset> page1 =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<PerformanceTopAsset> performanceTopAssets1 =
				(List<PerformanceTopAsset>)page1.getItems();

			Assert.assertEquals(
				performanceTopAssets1.toString(), totalCount + 2,
				performanceTopAssets1.size());

			Page<PerformanceTopAsset> page2 =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PerformanceTopAsset> performanceTopAssets2 =
				(List<PerformanceTopAsset>)page2.getItems();

			Assert.assertEquals(
				performanceTopAssets2.toString(), 1,
				performanceTopAssets2.size());

			Page<PerformanceTopAsset> page3 =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				performanceTopAsset1,
				(List<PerformanceTopAsset>)page3.getItems());
			assertContains(
				performanceTopAsset2,
				(List<PerformanceTopAsset>)page3.getItems());
			assertContains(
				performanceTopAsset3,
				(List<PerformanceTopAsset>)page3.getItems());
		}
	}

	@Test
	public void testGetPerformanceTopAssetPageWithSortDateTime()
		throws Exception {

		testGetPerformanceTopAssetPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, performanceTopAsset1, performanceTopAsset2) -> {
				BeanTestUtil.setProperty(
					performanceTopAsset1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPerformanceTopAssetPageWithSortDouble()
		throws Exception {

		testGetPerformanceTopAssetPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, performanceTopAsset1, performanceTopAsset2) -> {
				BeanTestUtil.setProperty(
					performanceTopAsset1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					performanceTopAsset2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPerformanceTopAssetPageWithSortInteger()
		throws Exception {

		testGetPerformanceTopAssetPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, performanceTopAsset1, performanceTopAsset2) -> {
				BeanTestUtil.setProperty(
					performanceTopAsset1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					performanceTopAsset2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPerformanceTopAssetPageWithSortString()
		throws Exception {

		testGetPerformanceTopAssetPageWithSort(
			EntityField.Type.STRING,
			(entityField, performanceTopAsset1, performanceTopAsset2) -> {
				Class<?> clazz = performanceTopAsset1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						performanceTopAsset1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						performanceTopAsset2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						performanceTopAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						performanceTopAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						performanceTopAsset1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						performanceTopAsset2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPerformanceTopAssetPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PerformanceTopAsset, PerformanceTopAsset,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		PerformanceTopAsset performanceTopAsset1 = randomPerformanceTopAsset();
		PerformanceTopAsset performanceTopAsset2 = randomPerformanceTopAsset();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, performanceTopAsset1, performanceTopAsset2);
		}

		performanceTopAsset1 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				performanceTopAsset1);

		performanceTopAsset2 =
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				performanceTopAsset2);

		Page<PerformanceTopAsset> page =
			performanceTopAssetResource.getPerformanceTopAssetPage(
				null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PerformanceTopAsset> ascPage =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				performanceTopAsset1,
				(List<PerformanceTopAsset>)ascPage.getItems());
			assertContains(
				performanceTopAsset2,
				(List<PerformanceTopAsset>)ascPage.getItems());

			Page<PerformanceTopAsset> descPage =
				performanceTopAssetResource.getPerformanceTopAssetPage(
					null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				performanceTopAsset2,
				(List<PerformanceTopAsset>)descPage.getItems());
			assertContains(
				performanceTopAsset1,
				(List<PerformanceTopAsset>)descPage.getItems());
		}
	}

	protected PerformanceTopAsset
			testGetPerformanceTopAssetPage_addPerformanceTopAsset(
				PerformanceTopAsset performanceTopAsset)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		PerformanceTopAsset performanceTopAsset,
		List<PerformanceTopAsset> performanceTopAssets) {

		boolean contains = false;

		for (PerformanceTopAsset item : performanceTopAssets) {
			if (equals(performanceTopAsset, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			performanceTopAssets + " does not contain " + performanceTopAsset,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PerformanceTopAsset performanceTopAsset1,
		PerformanceTopAsset performanceTopAsset2) {

		Assert.assertTrue(
			performanceTopAsset1 + " does not equal " + performanceTopAsset2,
			equals(performanceTopAsset1, performanceTopAsset2));
	}

	protected void assertEquals(
		List<PerformanceTopAsset> performanceTopAssets1,
		List<PerformanceTopAsset> performanceTopAssets2) {

		Assert.assertEquals(
			performanceTopAssets1.size(), performanceTopAssets2.size());

		for (int i = 0; i < performanceTopAssets1.size(); i++) {
			PerformanceTopAsset performanceTopAsset1 =
				performanceTopAssets1.get(i);
			PerformanceTopAsset performanceTopAsset2 =
				performanceTopAssets2.get(i);

			assertEquals(performanceTopAsset1, performanceTopAsset2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PerformanceTopAsset> performanceTopAssets1,
		List<PerformanceTopAsset> performanceTopAssets2) {

		Assert.assertEquals(
			performanceTopAssets1.size(), performanceTopAssets2.size());

		for (PerformanceTopAsset performanceTopAsset1 : performanceTopAssets1) {
			boolean contains = false;

			for (PerformanceTopAsset performanceTopAsset2 :
					performanceTopAssets2) {

				if (equals(performanceTopAsset1, performanceTopAsset2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				performanceTopAssets2 + " does not contain " +
					performanceTopAsset1,
				contains);
		}
	}

	protected void assertValid(PerformanceTopAsset performanceTopAsset)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (performanceTopAsset.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("downloads", additionalAssertFieldName)) {
				if (performanceTopAsset.getDownloads() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("embedded", additionalAssertFieldName)) {
				if (performanceTopAsset.getEmbedded() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("engagement", additionalAssertFieldName)) {
				if (performanceTopAsset.getEngagement() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (performanceTopAsset.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("impressions", additionalAssertFieldName)) {
				if (performanceTopAsset.getImpressions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (performanceTopAsset.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("trend", additionalAssertFieldName)) {
				if (performanceTopAsset.getTrend() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (performanceTopAsset.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("views", additionalAssertFieldName)) {
				if (performanceTopAsset.getViews() == null) {
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

	protected void assertValid(Page<PerformanceTopAsset> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PerformanceTopAsset> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PerformanceTopAsset> performanceTopAssets =
			page.getItems();

		int size = performanceTopAssets.size();

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

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.analytics.cms.rest.dto.v1_0.PerformanceTopAsset.
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
		PerformanceTopAsset performanceTopAsset1,
		PerformanceTopAsset performanceTopAsset2) {

		if (performanceTopAsset1 == performanceTopAsset2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getClassName(),
						performanceTopAsset2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("downloads", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getDownloads(),
						performanceTopAsset2.getDownloads())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("embedded", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getEmbedded(),
						performanceTopAsset2.getEmbedded())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("engagement", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getEngagement(),
						performanceTopAsset2.getEngagement())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						performanceTopAsset1.getExternalReferenceCode(),
						performanceTopAsset2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("impressions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getImpressions(),
						performanceTopAsset2.getImpressions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getTitle(),
						performanceTopAsset2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("trend", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getTrend(),
						performanceTopAsset2.getTrend())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getType(),
						performanceTopAsset2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("views", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						performanceTopAsset1.getViews(),
						performanceTopAsset2.getViews())) {

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

		if (!(_performanceTopAssetResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_performanceTopAssetResource;

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
		EntityField entityField, String operator,
		PerformanceTopAsset performanceTopAsset) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("className")) {
			Object object = performanceTopAsset.getClassName();

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

		if (entityFieldName.equals("downloads")) {
			sb.append(String.valueOf(performanceTopAsset.getDownloads()));

			return sb.toString();
		}

		if (entityFieldName.equals("embedded")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("engagement")) {
			sb.append(String.valueOf(performanceTopAsset.getEngagement()));

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = performanceTopAsset.getExternalReferenceCode();

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

		if (entityFieldName.equals("impressions")) {
			sb.append(String.valueOf(performanceTopAsset.getImpressions()));

			return sb.toString();
		}

		if (entityFieldName.equals("title")) {
			Object object = performanceTopAsset.getTitle();

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

		if (entityFieldName.equals("trend")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			Object object = performanceTopAsset.getType();

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

		if (entityFieldName.equals("views")) {
			sb.append(String.valueOf(performanceTopAsset.getViews()));

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
		httpInvoker.path(
			"http://localhost:" + PortalUtil.getPortalServerPort(false) +
				"/o/graphql");
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

	protected PerformanceTopAsset randomPerformanceTopAsset() throws Exception {
		return new PerformanceTopAsset() {
			{
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				downloads = RandomTestUtil.randomDouble();
				engagement = RandomTestUtil.randomDouble();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				impressions = RandomTestUtil.randomDouble();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				views = RandomTestUtil.randomDouble();
			}
		};
	}

	protected PerformanceTopAsset randomIrrelevantPerformanceTopAsset()
		throws Exception {

		PerformanceTopAsset randomIrrelevantPerformanceTopAsset =
			randomPerformanceTopAsset();

		return randomIrrelevantPerformanceTopAsset;
	}

	protected PerformanceTopAsset randomPatchPerformanceTopAsset()
		throws Exception {

		return randomPerformanceTopAsset();
	}

	protected PerformanceTopAssetResource performanceTopAssetResource;
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
		LogFactoryUtil.getLog(BasePerformanceTopAssetResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.analytics.cms.rest.resource.v1_0.PerformanceTopAssetResource
			_performanceTopAssetResource;

}
// LIFERAY-REST-BUILDER-HASH:1085740111