/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.admin.rest.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectField;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectFieldResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectFieldSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.PathSegment;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.lang.reflect.Method;

import java.net.URI;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseObjectFieldResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

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

		_objectFieldResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectFieldResource = ObjectFieldResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).locale(
			LocaleUtil.getDefault()
		).build();

		importTaskResource = ImportTaskResource.builder(
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

		ObjectField objectField1 = randomObjectField();

		String json = objectMapper.writeValueAsString(objectField1);

		ObjectField objectField2 = ObjectFieldSerDes.toDTO(json);

		Assert.assertTrue(equals(objectField1, objectField2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectField objectField = randomObjectField();

		String json1 = objectMapper.writeValueAsString(objectField);
		String json2 = ObjectFieldSerDes.toJSON(objectField);

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

		ObjectField objectField = randomObjectField();

		objectField.setDefaultValue(regex);
		objectField.setExternalReferenceCode(regex);
		objectField.setIndexedLanguageId(regex);
		objectField.setListTypeDefinitionExternalReferenceCode(regex);
		objectField.setName(regex);
		objectField.setObjectDefinitionExternalReferenceCode1(regex);
		objectField.setObjectRelationshipExternalReferenceCode(regex);
		objectField.setReadOnlyConditionExpression(regex);

		String json = ObjectFieldSerDes.toJSON(objectField);

		Assert.assertFalse(json.contains(regex));

		objectField = ObjectFieldSerDes.toDTO(json);

		Assert.assertEquals(regex, objectField.getDefaultValue());
		Assert.assertEquals(regex, objectField.getExternalReferenceCode());
		Assert.assertEquals(regex, objectField.getIndexedLanguageId());
		Assert.assertEquals(
			regex, objectField.getListTypeDefinitionExternalReferenceCode());
		Assert.assertEquals(regex, objectField.getName());
		Assert.assertEquals(
			regex, objectField.getObjectDefinitionExternalReferenceCode1());
		Assert.assertEquals(
			regex, objectField.getObjectRelationshipExternalReferenceCode());
		Assert.assertEquals(
			regex, objectField.getReadOnlyConditionExpression());
	}

	@Test
	public void testDeleteObjectField() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectField objectField = testDeleteObjectField_addObjectField();

		assertHttpResponseStatusCode(
			204,
			objectFieldResource.deleteObjectFieldHttpResponse(
				objectField.getId()));

		assertHttpResponseStatusCode(
			404,
			objectFieldResource.getObjectFieldHttpResponse(
				objectField.getId()));
		assertHttpResponseStatusCode(
			404, objectFieldResource.getObjectFieldHttpResponse(0L));
	}

	protected ObjectField testDeleteObjectField_addObjectField()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectField() throws Exception {

		// No namespace

		ObjectField objectField1 =
			testGraphQLDeleteObjectField_addObjectField();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectField",
						new HashMap<String, Object>() {
							{
								put("objectFieldId", objectField1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectField"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectField",
					new HashMap<String, Object>() {
						{
							put("objectFieldId", objectField1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectField objectField2 =
			testGraphQLDeleteObjectField_addObjectField();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectField",
							new HashMap<String, Object>() {
								{
									put("objectFieldId", objectField2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectField"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectField",
						new HashMap<String, Object>() {
							{
								put("objectFieldId", objectField2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectField testGraphQLDeleteObjectField_addObjectField()
		throws Exception {

		return testGraphQLObjectField_addObjectField();
	}

	@Test
	public void testDeleteObjectFieldBatch() throws Exception {
		ObjectField objectField1 = testDeleteObjectFieldBatch_addObjectField();

		testDeleteObjectFieldBatch_deleteObjectField(
			202, null, objectField1.getId());

		assertHttpResponseStatusCode(
			404,
			objectFieldResource.getObjectFieldHttpResponse(
				objectField1.getId()));
	}

	protected ObjectField testDeleteObjectFieldBatch_addObjectField()
		throws Exception {

		return testDeleteObjectField_addObjectField();
	}

	protected void testDeleteObjectFieldBatch_deleteObjectField(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectFieldResource.deleteObjectFieldBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getIrrelevantExternalReferenceCode();

		Page<ObjectField> page =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ObjectField irrelevantObjectField =
				testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
					irrelevantExternalReferenceCode,
					randomIrrelevantObjectField());

			page =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectField, (List<ObjectField>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ObjectField objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		page =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectField1, (List<ObjectField>)page.getItems());
		assertContains(objectField2, (List<ObjectField>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExpectedActions(
				externalReferenceCode));

		objectFieldResource.deleteObjectField(objectField1.getId());

		objectFieldResource.deleteObjectField(objectField2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();

		ObjectField objectField1 = randomObjectField();

		objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, objectField1);

		for (EntityField entityField : entityFields) {
			Page<ObjectField> page =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", objectField1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectField1),
				(List<ObjectField>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilterDoubleEquals()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilterStringContains()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilterStringEquals()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilterStringStartsWith()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();

		ObjectField objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectField objectField2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		for (EntityField entityField : entityFields) {
			Page<ObjectField> page =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, objectField1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectField1),
				(List<ObjectField>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();

		Page<ObjectField> objectFieldsPage =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectFieldsPage.getTotalCount());

		ObjectField objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		ObjectField objectField3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, randomObjectField());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectField> page1 =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectField1, (List<ObjectField>)page1.getItems());

			Page<ObjectField> page2 =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectField2, (List<ObjectField>)page2.getItems());

			Page<ObjectField> page3 =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectField3, (List<ObjectField>)page3.getItems());
		}
		else {
			Page<ObjectField> page1 =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectField> objectFields1 =
				(List<ObjectField>)page1.getItems();

			Assert.assertEquals(
				objectFields1.toString(), totalCount + 2, objectFields1.size());

			Page<ObjectField> page2 =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectField> objectFields2 =
				(List<ObjectField>)page2.getItems();

			Assert.assertEquals(
				objectFields2.toString(), 1, objectFields2.size());

			Page<ObjectField> page3 =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectField1, (List<ObjectField>)page3.getItems());
			assertContains(objectField2, (List<ObjectField>)page3.getItems());
			assertContains(objectField3, (List<ObjectField>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectField1, objectField2) -> {
				BeanTestUtil.setProperty(
					objectField1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectField1, objectField2) -> {
				BeanTestUtil.setProperty(
					objectField1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectField2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectField1, objectField2) -> {
				BeanTestUtil.setProperty(
					objectField1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectField2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectField1, objectField2) -> {
				Class<?> clazz = objectField1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectField1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectField2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectField1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectField2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectField1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectField2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ObjectField, ObjectField, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();

		ObjectField objectField1 = randomObjectField();
		ObjectField objectField2 = randomObjectField();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectField1, objectField2);
		}

		objectField1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, objectField1);

		objectField2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				externalReferenceCode, objectField2);

		Page<ObjectField> page =
			objectFieldResource.
				getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectField> ascPage =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(objectField1, (List<ObjectField>)ascPage.getItems());
			assertContains(objectField2, (List<ObjectField>)ascPage.getItems());

			Page<ObjectField> descPage =
				objectFieldResource.
					getObjectDefinitionByExternalReferenceCodeObjectFieldsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectField2, (List<ObjectField>)descPage.getItems());
			assertContains(
				objectField1, (List<ObjectField>)descPage.getItems());
		}
	}

	protected ObjectField
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_addObjectField(
				String externalReferenceCode, ObjectField objectField)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectFieldsPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionByExternalReferenceCodeObjectFields",
			new HashMap<String, Object>() {
				{
					put(
						"externalReferenceCode",
						"\"" + externalReferenceCode + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject
			objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/objectDefinitionByExternalReferenceCodeObjectFields");

		long totalCount =
			objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
				getLong("totalCount");

		ObjectField objectField1 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageObjectDefinitionObjectField_addObjectField(
				externalReferenceCode, randomObjectField());

		ObjectField objectField2 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageObjectDefinitionObjectField_addObjectField(
				externalReferenceCode, randomObjectField());

		objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectFields");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectField1,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
						getString("items"))));
		assertContains(
			objectField2,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
						getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectFields");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectField1,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
						getString("items"))));
		assertContains(
			objectField2,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectFieldsJSONObject.
						getString("items"))));
	}

	protected ObjectField
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectFieldsPageObjectDefinitionObjectField_addObjectField(
				String externalReferenceCode, ObjectField objectField)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPage() throws Exception {
		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getIrrelevantObjectDefinitionId();

		Page<ObjectField> page =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectField irrelevantObjectField =
				testGetObjectDefinitionObjectFieldsPage_addObjectField(
					irrelevantObjectDefinitionId,
					randomIrrelevantObjectField());

			page = objectFieldResource.getObjectDefinitionObjectFieldsPage(
				irrelevantObjectDefinitionId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectField, (List<ObjectField>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionObjectFieldsPage_getExpectedActions(
					irrelevantObjectDefinitionId));
		}

		ObjectField objectField1 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		page = objectFieldResource.getObjectDefinitionObjectFieldsPage(
			objectDefinitionId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectField1, (List<ObjectField>)page.getItems());
		assertContains(objectField2, (List<ObjectField>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionObjectFieldsPage_getExpectedActions(
				objectDefinitionId));

		objectFieldResource.deleteObjectField(objectField1.getId());

		objectFieldResource.deleteObjectField(objectField2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionObjectFieldsPage_getExpectedActions(
				Long objectDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-fields/batch".
				replace(
					"{objectDefinitionId}",
					String.valueOf(objectDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();

		ObjectField objectField1 = randomObjectField();

		objectField1 = testGetObjectDefinitionObjectFieldsPage_addObjectField(
			objectDefinitionId, objectField1);

		for (EntityField entityField : entityFields) {
			Page<ObjectField> page =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null,
					getFilterString(entityField, "between", objectField1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectField1),
				(List<ObjectField>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithFilterDoubleEquals()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithFilterStringContains()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithFilterStringEquals()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithFilterStringStartsWith()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetObjectDefinitionObjectFieldsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();

		ObjectField objectField1 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectField objectField2 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		for (EntityField entityField : entityFields) {
			Page<ObjectField> page =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null,
					getFilterString(entityField, operator, objectField1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectField1),
				(List<ObjectField>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithPagination()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();

		Page<ObjectField> objectFieldsPage =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectFieldsPage.getTotalCount());

		ObjectField objectField1 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField2 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField3 =
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				objectDefinitionId, randomObjectField());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectField> page1 =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectField1, (List<ObjectField>)page1.getItems());

			Page<ObjectField> page2 =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectField2, (List<ObjectField>)page2.getItems());

			Page<ObjectField> page3 =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectField3, (List<ObjectField>)page3.getItems());
		}
		else {
			Page<ObjectField> page1 =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<ObjectField> objectFields1 =
				(List<ObjectField>)page1.getItems();

			Assert.assertEquals(
				objectFields1.toString(), totalCount + 2, objectFields1.size());

			Page<ObjectField> page2 =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectField> objectFields2 =
				(List<ObjectField>)page2.getItems();

			Assert.assertEquals(
				objectFields2.toString(), 1, objectFields2.size());

			Page<ObjectField> page3 =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectField1, (List<ObjectField>)page3.getItems());
			assertContains(objectField2, (List<ObjectField>)page3.getItems());
			assertContains(objectField3, (List<ObjectField>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectField1, objectField2) -> {
				BeanTestUtil.setProperty(
					objectField1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectField1, objectField2) -> {
				BeanTestUtil.setProperty(
					objectField1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectField2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectField1, objectField2) -> {
				BeanTestUtil.setProperty(
					objectField1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectField2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectFieldsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionObjectFieldsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectField1, objectField2) -> {
				Class<?> clazz = objectField1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectField1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectField2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectField1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectField2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectField1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectField2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionObjectFieldsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, ObjectField, ObjectField, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();

		ObjectField objectField1 = randomObjectField();
		ObjectField objectField2 = randomObjectField();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectField1, objectField2);
		}

		objectField1 = testGetObjectDefinitionObjectFieldsPage_addObjectField(
			objectDefinitionId, objectField1);

		objectField2 = testGetObjectDefinitionObjectFieldsPage_addObjectField(
			objectDefinitionId, objectField2);

		Page<ObjectField> page =
			objectFieldResource.getObjectDefinitionObjectFieldsPage(
				objectDefinitionId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectField> ascPage =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(objectField1, (List<ObjectField>)ascPage.getItems());
			assertContains(objectField2, (List<ObjectField>)ascPage.getItems());

			Page<ObjectField> descPage =
				objectFieldResource.getObjectDefinitionObjectFieldsPage(
					objectDefinitionId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				objectField2, (List<ObjectField>)descPage.getItems());
			assertContains(
				objectField1, (List<ObjectField>)descPage.getItems());
		}
	}

	protected ObjectField
			testGetObjectDefinitionObjectFieldsPage_addObjectField(
				Long objectDefinitionId, ObjectField objectField)
		throws Exception {

		return objectFieldResource.postObjectDefinitionObjectField(
			objectDefinitionId, objectField);
	}

	protected Long
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectDefinitionObjectFieldsPage_getIrrelevantObjectDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionObjectFieldsPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionObjectFields",
			new HashMap<String, Object>() {
				{
					put("objectDefinitionId", objectDefinitionId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject objectDefinitionObjectFieldsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectFields");

		long totalCount = objectDefinitionObjectFieldsJSONObject.getLong(
			"totalCount");

		ObjectField objectField1 =
			testGraphQLObjectDefinitionObjectField_addObjectField(
				objectDefinitionId, randomObjectField());

		ObjectField objectField2 =
			testGraphQLObjectDefinitionObjectField_addObjectField(
				objectDefinitionId, randomObjectField());

		objectDefinitionObjectFieldsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitionObjectFields");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectFieldsJSONObject.getLong("totalCount"));

		assertContains(
			objectField1,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionObjectFieldsJSONObject.getString(
						"items"))));
		assertContains(
			objectField2,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionObjectFieldsJSONObject.getString(
						"items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionObjectFieldsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("objectAdmin_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/objectAdmin_v1_0",
			"JSONObject/objectDefinitionObjectFields");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectFieldsJSONObject.getLong("totalCount"));

		assertContains(
			objectField1,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionObjectFieldsJSONObject.getString(
						"items"))));
		assertContains(
			objectField2,
			Arrays.asList(
				ObjectFieldSerDes.toDTOs(
					objectDefinitionObjectFieldsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetObjectField() throws Exception {
		ObjectField postObjectField = testGetObjectField_addObjectField();

		ObjectField getObjectField = objectFieldResource.getObjectField(
			postObjectField.getId());

		assertEquals(postObjectField, getObjectField);
		assertValid(getObjectField);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectField postObjectField = testGetObjectField_addObjectField();

		ObjectField getObjectField = objectFieldResource.getObjectField(
			postObjectField.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.object.admin.rest.dto.v1_0.ObjectField"
			).acceptLanguage(
				new AcceptLanguage() {

					@Override
					public List<Locale> getLocales() {
						return Arrays.asList(LocaleUtil.getDefault());
					}

					@Override
					public String getPreferredLanguageId() {
						return LocaleUtil.toLanguageId(LocaleUtil.getDefault());
					}

					@Override
					public Locale getPreferredLocale() {
						return LocaleUtil.getDefault();
					}

				}
			).groupLocalService(
				_groupLocalService
			).httpServletRequest(
				testVulcanCRUDItemDelegate_getHttpServletRequest()
			).httpServletResponse(
				new MockHttpServletResponse()
			).resourceActionLocalService(
				_resourceActionLocalService
			).resourcePermissionLocalService(
				_resourcePermissionLocalService
			).roleLocalService(
				_roleLocalService
			).scopeChecker(
				_scopeChecker
			).uriInfo(
				testVulcanCRUDItemDelegate_getUriInfo()
			).user(
				testVulcanCRUDItemDelegate_getUser()
			).build();

		Object item = vulcanCRUDItemDelegate.getItem(postObjectField.getId());

		assertEquals(getObjectField, ObjectFieldSerDes.toDTO(item.toString()));
	}

	protected HttpServletRequest
		testVulcanCRUDItemDelegate_getHttpServletRequest() {

		return new MockHttpServletRequest() {

			@Override
			public StringBuffer getRequestURL() {
				return new StringBuffer(
					StringBundler.concat(
						"http://localhost:8080/o/v1.0/",
						RandomTestUtil.randomString(), "/",
						RandomTestUtil.randomString()));
			}

		};
	}

	protected UriInfo testVulcanCRUDItemDelegate_getUriInfo() {
		String applicationPath = RandomTestUtil.randomString() + "/";
		String resourcePath = RandomTestUtil.randomString();

		return new UriInfo() {

			@Override
			public String getPath() {
				return resourcePath;
			}

			@Override
			public String getPath(boolean decode) {
				return getPath();
			}

			@Override
			public List<PathSegment> getPathSegments() {
				return Collections.emptyList();
			}

			@Override
			public List<PathSegment> getPathSegments(boolean decode) {
				return getPathSegments();
			}

			@Override
			public URI getRequestUri() {
				return URI.create(
					"http://localhost:8080/o/" + applicationPath +
						resourcePath);
			}

			@Override
			public UriBuilder getRequestUriBuilder() {
				return UriBuilder.fromUri(getRequestUri());
			}

			@Override
			public URI getAbsolutePath() {
				return getRequestUri();
			}

			@Override
			public UriBuilder getAbsolutePathBuilder() {
				return getRequestUriBuilder();
			}

			@Override
			public URI getBaseUri() {
				return URI.create("http://localhost:8080/o/" + applicationPath);
			}

			@Override
			public UriBuilder getBaseUriBuilder() {
				return UriBuilder.fromUri(getBaseUri());
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getPathParameters(
				boolean decode) {

				return getPathParameters();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters() {
				return new MultivaluedHashMap<>();
			}

			@Override
			public MultivaluedMap<String, String> getQueryParameters(
				boolean decode) {

				return getQueryParameters();
			}

			@Override
			public List<String> getMatchedURIs() {
				return Collections.emptyList();
			}

			@Override
			public List<String> getMatchedURIs(boolean decode) {
				return getMatchedURIs();
			}

			@Override
			public List<Object> getMatchedResources() {
				return Collections.emptyList();
			}

			@Override
			public URI resolve(URI requestUri) {
				return getBaseUri().resolve(requestUri);
			}

			@Override
			public URI relativize(URI uri) {
				return getBaseUri().relativize(uri);
			}

		};
	}

	protected com.liferay.portal.kernel.model.User
		testVulcanCRUDItemDelegate_getUser() {

		return _testCompanyAdminUser;
	}

	protected ObjectField testGetObjectField_addObjectField() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectField() throws Exception {
		ObjectField objectField = testGraphQLGetObjectField_addObjectField();

		// No namespace

		Assert.assertTrue(
			equals(
				objectField,
				ObjectFieldSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectField",
								new HashMap<String, Object>() {
									{
										put(
											"objectFieldId",
											objectField.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectField"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectField,
				ObjectFieldSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectField",
									new HashMap<String, Object>() {
										{
											put(
												"objectFieldId",
												objectField.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectField"))));
	}

	@Test
	public void testGraphQLGetObjectFieldNotFound() throws Exception {
		Long irrelevantObjectFieldId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectField",
						new HashMap<String, Object>() {
							{
								put("objectFieldId", irrelevantObjectFieldId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace objectAdmin_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"objectField",
							new HashMap<String, Object>() {
								{
									put(
										"objectFieldId",
										irrelevantObjectFieldId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectField testGraphQLGetObjectField_addObjectField()
		throws Exception {

		return testGraphQLObjectField_addObjectField();
	}

	@Test
	public void testPatchObjectField() throws Exception {
		ObjectField postObjectField = testPatchObjectField_addObjectField();

		ObjectField randomPatchObjectField = randomPatchObjectField();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectField patchObjectField = objectFieldResource.patchObjectField(
			postObjectField.getId(), randomPatchObjectField);

		ObjectField expectedPatchObjectField = postObjectField.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectField, expectedPatchObjectField);

		ObjectField getObjectField = objectFieldResource.getObjectField(
			patchObjectField.getId());

		assertEquals(expectedPatchObjectField, getObjectField);
		assertValid(getObjectField);
	}

	protected ObjectField testPatchObjectField_addObjectField()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionByExternalReferenceCodeObjectField()
		throws Exception {

		ObjectField randomObjectField = randomObjectField();

		ObjectField postObjectField =
			testPostObjectDefinitionByExternalReferenceCodeObjectField_addObjectField(
				randomObjectField);

		assertEquals(randomObjectField, postObjectField);
		assertValid(postObjectField);
	}

	protected ObjectField
			testPostObjectDefinitionByExternalReferenceCodeObjectField_addObjectField(
				ObjectField objectField)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectField()
		throws Exception {

		ObjectField randomObjectField = randomObjectField();

		ObjectField objectField =
			testGraphQLObjectDefinitionObjectField_addObjectField(
				testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectField_getObjectDefinitionId(),
				randomObjectField);

		Assert.assertTrue(equals(randomObjectField, objectField));
	}

	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectField_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionObjectField() throws Exception {
		ObjectField randomObjectField = randomObjectField();

		ObjectField postObjectField =
			testPostObjectDefinitionObjectField_addObjectField(
				randomObjectField);

		assertEquals(randomObjectField, postObjectField);
		assertValid(postObjectField);
	}

	protected ObjectField testPostObjectDefinitionObjectField_addObjectField(
			ObjectField objectField)
		throws Exception {

		return objectFieldResource.postObjectDefinitionObjectField(
			testGetObjectDefinitionObjectFieldsPage_getObjectDefinitionId(),
			objectField);
	}

	@Test
	public void testGraphQLPostObjectDefinitionObjectField() throws Exception {
		ObjectField randomObjectField = randomObjectField();

		ObjectField objectField =
			testGraphQLObjectDefinitionObjectField_addObjectField(
				testGraphQLPostObjectDefinitionObjectField_getObjectDefinitionId(),
				randomObjectField);

		Assert.assertTrue(equals(randomObjectField, objectField));
	}

	protected Long
			testGraphQLPostObjectDefinitionObjectField_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectField() throws Exception {
		ObjectField postObjectField = testPutObjectField_addObjectField();

		ObjectField randomObjectField = randomObjectField();

		ObjectField putObjectField = objectFieldResource.putObjectField(
			postObjectField.getId(), randomObjectField);

		assertEquals(randomObjectField, putObjectField);
		assertValid(putObjectField);

		ObjectField getObjectField = objectFieldResource.getObjectField(
			putObjectField.getId());

		assertEquals(randomObjectField, getObjectField);
		assertValid(getObjectField);
	}

	protected ObjectField testPutObjectField_addObjectField() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectField objectField1 =
			testBatchEngineDeleteImportTask_addObjectField();

		testBatchEngineDeleteImportTask_deleteObjectField(
			200, null, objectField1.getId());

		assertHttpResponseStatusCode(
			404,
			objectFieldResource.getObjectFieldHttpResponse(
				objectField1.getId()));
	}

	protected ObjectField testBatchEngineDeleteImportTask_addObjectField()
		throws Exception {

		return testDeleteObjectField_addObjectField();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectField(
			int expectedStatusCode, String externalReferenceCode, Long id,
			String... parameters)
		throws Exception {

		ImportTaskResource importTaskResource = ImportTaskResource.builder(
		).authentication(
			_testCompanyAdminUser.getEmailAddress(),
			PropsValues.DEFAULT_ADMIN_PASSWORD
		).endpoint(
			testCompany.getVirtualHostname(), 8080, "http"
		).parameters(
			parameters
		).build();

		HttpResponse httpResponse =
			importTaskResource.deleteImportTaskHttpResponse(
				"com.liferay.object.admin.rest.dto.v1_0.ObjectField", null,
				null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ObjectField testGraphQLObjectField_addObjectField()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectField
			testGraphQLObjectDefinitionObjectField_addObjectField()
		throws Exception {

		return testGraphQLObjectDefinitionObjectField_addObjectField(
			testGraphQLObjectDefinitionObjectField_getObjectDefinitionId(),
			randomObjectField());
	}

	protected Long
			testGraphQLObjectDefinitionObjectField_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectField testGraphQLObjectDefinitionObjectField_addObjectField(
			Long objectDefinitionId, ObjectField objectField)
		throws Exception {

		JSONDeserializer<ObjectField> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectField.class)) {

			if (getGraphQLValue(field.get(objectField)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectField)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectDefinitionObjectField",
						new HashMap<String, Object>() {
							{
								put("objectDefinitionId", objectDefinitionId);
								put("objectField", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createObjectDefinitionObjectField"),
			ObjectField.class);
	}

	protected String getGraphQLValue(Object value) throws Exception {
		if (value == null) {
			return null;
		}
		else if (value instanceof Boolean || value instanceof Number) {
			return value.toString();
		}
		else if (value instanceof Date date) {
			return "\"" +
				DateUtil.getDate(
					date, "yyyy-MM-dd'T'HH:mm:ss'Z'", LocaleUtil.getDefault(),
					TimeZone.getTimeZone("UTC")) + "\"";
		}
		else if (value instanceof Enum<?> enm) {
			return enm.name();
		}
		else if (value instanceof Map<?, ?> map) {
			List<String> entries = new ArrayList<>();

			for (Map.Entry<?, ?> entry : map.entrySet()) {
				String graphQLValue = getGraphQLValue(entry.getValue());

				if (graphQLValue != null) {
					entries.add(entry.getKey() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
		else if (value instanceof Object[] array) {
			List<String> entries = new ArrayList<>();

			for (Object entry : array) {
				String graphQLValue = getGraphQLValue(entry);

				if (graphQLValue != null) {
					entries.add(graphQLValue);
				}
			}

			return "[" + String.join(", ", entries) + "]";
		}
		else if (value instanceof String) {
			return "\"" + value + "\"";
		}
		else {
			List<String> entries = new ArrayList<>();

			Class<?> clazz = value.getClass();
			java.lang.reflect.Field[] declaredFields = getDeclaredFields(clazz);

			if (declaredFields.length == 0) {
				declaredFields = getDeclaredFields(clazz.getSuperclass());
			}

			for (java.lang.reflect.Field field : declaredFields) {
				String graphQLValue = getGraphQLValue(field.get(value));

				if (graphQLValue != null) {
					entries.add(field.getName() + ": " + graphQLValue);
				}
			}

			return "{" + String.join(", ", entries) + "}";
		}
	}

	protected void assertContains(
		ObjectField objectField, List<ObjectField> objectFields) {

		boolean contains = false;

		for (ObjectField item : objectFields) {
			if (equals(objectField, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectFields + " does not contain " + objectField, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectField objectField1, ObjectField objectField2) {

		Assert.assertTrue(
			objectField1 + " does not equal " + objectField2,
			equals(objectField1, objectField2));
	}

	protected void assertEquals(
		List<ObjectField> objectFields1, List<ObjectField> objectFields2) {

		Assert.assertEquals(objectFields1.size(), objectFields2.size());

		for (int i = 0; i < objectFields1.size(); i++) {
			ObjectField objectField1 = objectFields1.get(i);
			ObjectField objectField2 = objectFields2.get(i);

			assertEquals(objectField1, objectField2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectField> objectFields1, List<ObjectField> objectFields2) {

		Assert.assertEquals(objectFields1.size(), objectFields2.size());

		for (ObjectField objectField1 : objectFields1) {
			boolean contains = false;

			for (ObjectField objectField2 : objectFields2) {
				if (equals(objectField1, objectField2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectFields2 + " does not contain " + objectField1, contains);
		}
	}

	protected void assertValid(ObjectField objectField) throws Exception {
		boolean valid = true;

		if (objectField.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("DBType", additionalAssertFieldName)) {
				if (objectField.getDBType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectField.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("businessType", additionalAssertFieldName)) {
				if (objectField.getBusinessType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("defaultValue", additionalAssertFieldName)) {
				if (objectField.getDefaultValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectField.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("indexed", additionalAssertFieldName)) {
				if (objectField.getIndexed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("indexedAsKeyword", additionalAssertFieldName)) {
				if (objectField.getIndexedAsKeyword() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"indexedLanguageId", additionalAssertFieldName)) {

				if (objectField.getIndexedLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (objectField.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectField.getListTypeDefinitionExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionId", additionalAssertFieldName)) {

				if (objectField.getListTypeDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("localized", additionalAssertFieldName)) {
				if (objectField.getLocalized() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectField.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode1",
					additionalAssertFieldName)) {

				if (objectField.getObjectDefinitionExternalReferenceCode1() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectFieldSettings", additionalAssertFieldName)) {

				if (objectField.getObjectFieldSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectRelationshipExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectField.getObjectRelationshipExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("readOnly", additionalAssertFieldName)) {
				if (objectField.getReadOnly() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"readOnlyConditionExpression", additionalAssertFieldName)) {

				if (objectField.getReadOnlyConditionExpression() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relationshipType", additionalAssertFieldName)) {
				if (objectField.getRelationshipType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (objectField.getRequired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("state", additionalAssertFieldName)) {
				if (objectField.getState() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (objectField.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (objectField.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unique", additionalAssertFieldName)) {
				if (objectField.getUnique() == null) {
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

	protected void assertValid(Page<ObjectField> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectField> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectField> objectFields = page.getItems();

		int size = objectFields.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.object.admin.rest.dto.v1_0.ObjectField.class)) {

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
		ObjectField objectField1, ObjectField objectField2) {

		if (objectField1 == objectField2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("DBType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getDBType(), objectField2.getDBType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectField1.getActions(),
						(Map)objectField2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("businessType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getBusinessType(),
						objectField2.getBusinessType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("defaultValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getDefaultValue(),
						objectField2.getDefaultValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.getExternalReferenceCode(),
						objectField2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getId(), objectField2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("indexed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getIndexed(), objectField2.getIndexed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("indexedAsKeyword", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getIndexedAsKeyword(),
						objectField2.getIndexedAsKeyword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"indexedLanguageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.getIndexedLanguageId(),
						objectField2.getIndexedLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectField1.getLabel(),
						(Map)objectField2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.
							getListTypeDefinitionExternalReferenceCode(),
						objectField2.
							getListTypeDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"listTypeDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.getListTypeDefinitionId(),
						objectField2.getListTypeDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("localized", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getLocalized(),
						objectField2.getLocalized())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getName(), objectField2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode1",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.
							getObjectDefinitionExternalReferenceCode1(),
						objectField2.
							getObjectDefinitionExternalReferenceCode1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectFieldSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.getObjectFieldSettings(),
						objectField2.getObjectFieldSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectRelationshipExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.
							getObjectRelationshipExternalReferenceCode(),
						objectField2.
							getObjectRelationshipExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("readOnly", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getReadOnly(),
						objectField2.getReadOnly())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"readOnlyConditionExpression", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectField1.getReadOnlyConditionExpression(),
						objectField2.getReadOnlyConditionExpression())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relationshipType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getRelationshipType(),
						objectField2.getRelationshipType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getRequired(),
						objectField2.getRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("state", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getState(), objectField2.getState())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getSystem(), objectField2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getType(), objectField2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unique", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectField1.getUnique(), objectField2.getUnique())) {

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

		if (!(_objectFieldResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectFieldResource;

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
		EntityField entityField, String operator, ObjectField objectField) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("DBType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("businessType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultValue")) {
			Object object = objectField.getDefaultValue();

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

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = objectField.getExternalReferenceCode();

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

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("indexed")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("indexedAsKeyword")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("indexedLanguageId")) {
			Object object = objectField.getIndexedLanguageId();

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

		if (entityFieldName.equals("label")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("listTypeDefinitionExternalReferenceCode")) {
			Object object =
				objectField.getListTypeDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("listTypeDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("localized")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = objectField.getName();

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

		if (entityFieldName.equals("objectDefinitionExternalReferenceCode1")) {
			Object object =
				objectField.getObjectDefinitionExternalReferenceCode1();

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

		if (entityFieldName.equals("objectFieldSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectRelationshipExternalReferenceCode")) {
			Object object =
				objectField.getObjectRelationshipExternalReferenceCode();

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

		if (entityFieldName.equals("readOnly")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("readOnlyConditionExpression")) {
			Object object = objectField.getReadOnlyConditionExpression();

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

		if (entityFieldName.equals("relationshipType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("required")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("state")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unique")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

	protected ObjectField randomObjectField() throws Exception {
		return new ObjectField() {
			{
				defaultValue = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				indexed = RandomTestUtil.randomBoolean();
				indexedAsKeyword = RandomTestUtil.randomBoolean();
				indexedLanguageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				listTypeDefinitionExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				listTypeDefinitionId = RandomTestUtil.randomLong();
				localized = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				objectDefinitionExternalReferenceCode1 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectRelationshipExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				readOnlyConditionExpression = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				required = RandomTestUtil.randomBoolean();
				state = RandomTestUtil.randomBoolean();
				system = RandomTestUtil.randomBoolean();
				unique = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ObjectField randomIrrelevantObjectField() throws Exception {
		ObjectField randomIrrelevantObjectField = randomObjectField();

		return randomIrrelevantObjectField;
	}

	protected ObjectField randomPatchObjectField() throws Exception {
		return randomObjectField();
	}

	protected final JSONObject waitForFinish(
			String expectedExecuteStatus, JSONObject jsonObject)
		throws Exception {

		while (true) {
			ImportTask importTask = importTaskResource.getImportTask(
				jsonObject.getLong("id"));

			ImportTask.ExecuteStatus executeStatus =
				importTask.getExecuteStatus();

			if (StringUtil.equals(executeStatus.getValue(), "COMPLETED") ||
				StringUtil.equals(executeStatus.getValue(), "FAILED")) {

				Assert.assertEquals(
					expectedExecuteStatus, executeStatus.getValue());

				return jsonObject;
			}
		}
	}

	protected ObjectFieldResource objectFieldResource;
	protected ImportTaskResource importTaskResource;
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
		LogFactoryUtil.getLog(BaseObjectFieldResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.object.admin.rest.resource.v1_0.ObjectFieldResource
		_objectFieldResource;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private ResourceActionLocalService _resourceActionLocalService;

	@Inject
	private ResourcePermissionLocalService _resourcePermissionLocalService;

	@Inject
	private RoleLocalService _roleLocalService;

	@Inject
	private ScopeChecker _scopeChecker;

	@Inject
	private UserLocalService _userLocalService;

	@Inject
	private VulcanCRUDItemDelegateBuilderRegistry
		_vulcanCRUDItemDelegateBuilderRegistry;

}