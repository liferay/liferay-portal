/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.cms.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.cms.client.dto.v1_0.AssetUsage;
import com.liferay.headless.cms.client.http.HttpInvoker;
import com.liferay.headless.cms.client.pagination.Page;
import com.liferay.headless.cms.client.pagination.Pagination;
import com.liferay.headless.cms.client.resource.v1_0.AssetUsageResource;
import com.liferay.headless.cms.client.serdes.v1_0.AssetUsageSerDes;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
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
 * @author Crescenzo Rega
 * @generated
 */
@Generated("")
public abstract class BaseAssetUsageResourceTestCase {

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

		_assetUsageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		assetUsageResource = AssetUsageResource.builder(
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

		AssetUsage assetUsage1 = randomAssetUsage();

		String json = objectMapper.writeValueAsString(assetUsage1);

		AssetUsage assetUsage2 = AssetUsageSerDes.toDTO(json);

		Assert.assertTrue(equals(assetUsage1, assetUsage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AssetUsage assetUsage = randomAssetUsage();

		String json1 = objectMapper.writeValueAsString(assetUsage);
		String json2 = AssetUsageSerDes.toJSON(assetUsage);

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

		AssetUsage assetUsage = randomAssetUsage();

		assetUsage.setName(regex);
		assetUsage.setType(regex);
		assetUsage.setUrl(regex);

		String json = AssetUsageSerDes.toJSON(assetUsage);

		Assert.assertFalse(json.contains(regex));

		assetUsage = AssetUsageSerDes.toDTO(json);

		Assert.assertEquals(regex, assetUsage.getName());
		Assert.assertEquals(regex, assetUsage.getType());
		Assert.assertEquals(regex, assetUsage.getUrl());
	}

	@Test
	public void testGetAssetUsagesAssetPage() throws Exception {
		Long assetId = testGetAssetUsagesAssetPage_getAssetId();
		Long irrelevantAssetId =
			testGetAssetUsagesAssetPage_getIrrelevantAssetId();

		Page<AssetUsage> page = assetUsageResource.getAssetUsagesAssetPage(
			assetId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetId != null) {
			AssetUsage irrelevantAssetUsage =
				testGetAssetUsagesAssetPage_addAssetUsage(
					irrelevantAssetId, randomIrrelevantAssetUsage());

			page = assetUsageResource.getAssetUsagesAssetPage(
				irrelevantAssetId, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAssetUsage, (List<AssetUsage>)page.getItems());
			assertValid(
				page,
				testGetAssetUsagesAssetPage_getExpectedActions(
					irrelevantAssetId));
		}

		AssetUsage assetUsage1 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, randomAssetUsage());

		AssetUsage assetUsage2 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, randomAssetUsage());

		page = assetUsageResource.getAssetUsagesAssetPage(
			assetId, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(assetUsage1, (List<AssetUsage>)page.getItems());
		assertContains(assetUsage2, (List<AssetUsage>)page.getItems());
		assertValid(
			page, testGetAssetUsagesAssetPage_getExpectedActions(assetId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetUsagesAssetPage_getExpectedActions(Long assetId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetUsagesAssetPageWithPagination() throws Exception {
		Long assetId = testGetAssetUsagesAssetPage_getAssetId();

		Page<AssetUsage> assetUsagesPage =
			assetUsageResource.getAssetUsagesAssetPage(
				assetId, null, null, null);

		int totalCount = GetterUtil.getInteger(assetUsagesPage.getTotalCount());

		AssetUsage assetUsage1 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, randomAssetUsage());

		AssetUsage assetUsage2 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, randomAssetUsage());

		AssetUsage assetUsage3 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, randomAssetUsage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AssetUsage> page1 = assetUsageResource.getAssetUsagesAssetPage(
				assetId, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(assetUsage1, (List<AssetUsage>)page1.getItems());

			Page<AssetUsage> page2 = assetUsageResource.getAssetUsagesAssetPage(
				assetId, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(assetUsage2, (List<AssetUsage>)page2.getItems());

			Page<AssetUsage> page3 = assetUsageResource.getAssetUsagesAssetPage(
				assetId, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(assetUsage3, (List<AssetUsage>)page3.getItems());
		}
		else {
			Page<AssetUsage> page1 = assetUsageResource.getAssetUsagesAssetPage(
				assetId, null, Pagination.of(1, totalCount + 2), null);

			List<AssetUsage> assetUsages1 = (List<AssetUsage>)page1.getItems();

			Assert.assertEquals(
				assetUsages1.toString(), totalCount + 2, assetUsages1.size());

			Page<AssetUsage> page2 = assetUsageResource.getAssetUsagesAssetPage(
				assetId, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AssetUsage> assetUsages2 = (List<AssetUsage>)page2.getItems();

			Assert.assertEquals(
				assetUsages2.toString(), 1, assetUsages2.size());

			Page<AssetUsage> page3 = assetUsageResource.getAssetUsagesAssetPage(
				assetId, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(assetUsage1, (List<AssetUsage>)page3.getItems());
			assertContains(assetUsage2, (List<AssetUsage>)page3.getItems());
			assertContains(assetUsage3, (List<AssetUsage>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetUsagesAssetPageWithSortDateTime() throws Exception {
		testGetAssetUsagesAssetPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, assetUsage1, assetUsage2) -> {
				BeanTestUtil.setProperty(
					assetUsage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetUsagesAssetPageWithSortDouble() throws Exception {
		testGetAssetUsagesAssetPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, assetUsage1, assetUsage2) -> {
				BeanTestUtil.setProperty(
					assetUsage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					assetUsage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetUsagesAssetPageWithSortInteger() throws Exception {
		testGetAssetUsagesAssetPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, assetUsage1, assetUsage2) -> {
				BeanTestUtil.setProperty(assetUsage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(assetUsage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetUsagesAssetPageWithSortString() throws Exception {
		testGetAssetUsagesAssetPageWithSort(
			EntityField.Type.STRING,
			(entityField, assetUsage1, assetUsage2) -> {
				Class<?> clazz = assetUsage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						assetUsage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						assetUsage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						assetUsage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						assetUsage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						assetUsage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						assetUsage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetUsagesAssetPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, AssetUsage, AssetUsage, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetId = testGetAssetUsagesAssetPage_getAssetId();

		AssetUsage assetUsage1 = randomAssetUsage();
		AssetUsage assetUsage2 = randomAssetUsage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, assetUsage1, assetUsage2);
		}

		assetUsage1 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, assetUsage1);

		assetUsage2 = testGetAssetUsagesAssetPage_addAssetUsage(
			assetId, assetUsage2);

		Page<AssetUsage> page = assetUsageResource.getAssetUsagesAssetPage(
			assetId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AssetUsage> ascPage =
				assetUsageResource.getAssetUsagesAssetPage(
					assetId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(assetUsage1, (List<AssetUsage>)ascPage.getItems());
			assertContains(assetUsage2, (List<AssetUsage>)ascPage.getItems());

			Page<AssetUsage> descPage =
				assetUsageResource.getAssetUsagesAssetPage(
					assetId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(assetUsage2, (List<AssetUsage>)descPage.getItems());
			assertContains(assetUsage1, (List<AssetUsage>)descPage.getItems());
		}
	}

	protected AssetUsage testGetAssetUsagesAssetPage_addAssetUsage(
			Long assetId, AssetUsage assetUsage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetUsagesAssetPage_getAssetId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetUsagesAssetPage_getIrrelevantAssetId()
		throws Exception {

		return null;
	}

	protected void assertContains(
		AssetUsage assetUsage, List<AssetUsage> assetUsages) {

		boolean contains = false;

		for (AssetUsage item : assetUsages) {
			if (equals(assetUsage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			assetUsages + " does not contain " + assetUsage, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AssetUsage assetUsage1, AssetUsage assetUsage2) {

		Assert.assertTrue(
			assetUsage1 + " does not equal " + assetUsage2,
			equals(assetUsage1, assetUsage2));
	}

	protected void assertEquals(
		List<AssetUsage> assetUsages1, List<AssetUsage> assetUsages2) {

		Assert.assertEquals(assetUsages1.size(), assetUsages2.size());

		for (int i = 0; i < assetUsages1.size(); i++) {
			AssetUsage assetUsage1 = assetUsages1.get(i);
			AssetUsage assetUsage2 = assetUsages2.get(i);

			assertEquals(assetUsage1, assetUsage2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AssetUsage> assetUsages1, List<AssetUsage> assetUsages2) {

		Assert.assertEquals(assetUsages1.size(), assetUsages2.size());

		for (AssetUsage assetUsage1 : assetUsages1) {
			boolean contains = false;

			for (AssetUsage assetUsage2 : assetUsages2) {
				if (equals(assetUsage1, assetUsage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				assetUsages2 + " does not contain " + assetUsage1, contains);
		}
	}

	protected void assertValid(AssetUsage assetUsage) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (assetUsage.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (assetUsage.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (assetUsage.getUrl() == null) {
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

	protected void assertValid(Page<AssetUsage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AssetUsage> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AssetUsage> assetUsages = page.getItems();

		int size = assetUsages.size();

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
					com.liferay.headless.cms.dto.v1_0.AssetUsage.class)) {

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

	protected boolean equals(AssetUsage assetUsage1, AssetUsage assetUsage2) {
		if (assetUsage1 == assetUsage2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetUsage1.getName(), assetUsage2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetUsage1.getType(), assetUsage2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						assetUsage1.getUrl(), assetUsage2.getUrl())) {

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

		if (!(_assetUsageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_assetUsageResource;

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
		EntityField entityField, String operator, AssetUsage assetUsage) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("name")) {
			Object object = assetUsage.getName();

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

		if (entityFieldName.equals("type")) {
			Object object = assetUsage.getType();

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

		if (entityFieldName.equals("url")) {
			Object object = assetUsage.getUrl();

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

	protected AssetUsage randomAssetUsage() throws Exception {
		return new AssetUsage() {
			{
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
				url = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected AssetUsage randomIrrelevantAssetUsage() throws Exception {
		AssetUsage randomIrrelevantAssetUsage = randomAssetUsage();

		return randomIrrelevantAssetUsage;
	}

	protected AssetUsage randomPatchAssetUsage() throws Exception {
		return randomAssetUsage();
	}

	protected AssetUsageResource assetUsageResource;
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
		LogFactoryUtil.getLog(BaseAssetUsageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.cms.resource.v1_0.AssetUsageResource
		_assetUsageResource;

}