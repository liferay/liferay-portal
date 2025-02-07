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

import com.liferay.object.admin.rest.client.dto.v1_0.ObjectDefinition;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectDefinitionResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectDefinitionSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import java.lang.reflect.Method;

import java.text.DateFormat;

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

import javax.annotation.Generated;

import javax.ws.rs.core.MultivaluedHashMap;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseObjectDefinitionResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

	@BeforeClass
	public static void setUpClass() throws Exception {
		_dateFormat = DateFormatFactoryUtil.getSimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");
	}

	@Before
	public void setUp() throws Exception {
		irrelevantGroup = GroupTestUtil.addGroup();
		testGroup = GroupTestUtil.addGroup();

		testCompany = CompanyLocalServiceUtil.getCompany(
			testGroup.getCompanyId());

		_objectDefinitionResource.setContextCompany(testCompany);

		com.liferay.portal.kernel.model.User testCompanyAdminUser =
			UserTestUtil.getAdminUser(testCompany.getCompanyId());

		objectDefinitionResource = ObjectDefinitionResource.builder(
		).authentication(
			testCompanyAdminUser.getEmailAddress(),
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

		ObjectDefinition objectDefinition1 = randomObjectDefinition();

		String json = objectMapper.writeValueAsString(objectDefinition1);

		ObjectDefinition objectDefinition2 = ObjectDefinitionSerDes.toDTO(json);

		Assert.assertTrue(equals(objectDefinition1, objectDefinition2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectDefinition objectDefinition = randomObjectDefinition();

		String json1 = objectMapper.writeValueAsString(objectDefinition);
		String json2 = ObjectDefinitionSerDes.toJSON(objectDefinition);

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

		ObjectDefinition objectDefinition = randomObjectDefinition();

		objectDefinition.setAccountEntryRestrictedObjectFieldName(regex);
		objectDefinition.setClassName(regex);
		objectDefinition.setDefaultLanguageId(regex);
		objectDefinition.setExternalReferenceCode(regex);
		objectDefinition.setName(regex);
		objectDefinition.setObjectFolderExternalReferenceCode(regex);
		objectDefinition.setPanelAppOrder(regex);
		objectDefinition.setPanelCategoryKey(regex);
		objectDefinition.setRestContextPath(regex);
		objectDefinition.setRootObjectDefinitionExternalReferenceCode(regex);
		objectDefinition.setScope(regex);
		objectDefinition.setStorageType(regex);
		objectDefinition.setTitleObjectFieldName(regex);

		String json = ObjectDefinitionSerDes.toJSON(objectDefinition);

		Assert.assertFalse(json.contains(regex));

		objectDefinition = ObjectDefinitionSerDes.toDTO(json);

		Assert.assertEquals(
			regex, objectDefinition.getAccountEntryRestrictedObjectFieldName());
		Assert.assertEquals(regex, objectDefinition.getClassName());
		Assert.assertEquals(regex, objectDefinition.getDefaultLanguageId());
		Assert.assertEquals(regex, objectDefinition.getExternalReferenceCode());
		Assert.assertEquals(regex, objectDefinition.getName());
		Assert.assertEquals(
			regex, objectDefinition.getObjectFolderExternalReferenceCode());
		Assert.assertEquals(regex, objectDefinition.getPanelAppOrder());
		Assert.assertEquals(regex, objectDefinition.getPanelCategoryKey());
		Assert.assertEquals(regex, objectDefinition.getRestContextPath());
		Assert.assertEquals(
			regex,
			objectDefinition.getRootObjectDefinitionExternalReferenceCode());
		Assert.assertEquals(regex, objectDefinition.getScope());
		Assert.assertEquals(regex, objectDefinition.getStorageType());
		Assert.assertEquals(regex, objectDefinition.getTitleObjectFieldName());
	}

	@Test
	public void testGetObjectDefinitionsPage() throws Exception {
		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		ObjectDefinition objectDefinition1 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		ObjectDefinition objectDefinition2 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		page = objectDefinitionResource.getObjectDefinitionsPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			objectDefinition1, (List<ObjectDefinition>)page.getItems());
		assertContains(
			objectDefinition2, (List<ObjectDefinition>)page.getItems());
		assertValid(page, testGetObjectDefinitionsPage_getExpectedActions());

		objectDefinitionResource.deleteObjectDefinition(
			objectDefinition1.getId());

		objectDefinitionResource.deleteObjectDefinition(
			objectDefinition2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		ObjectDefinition objectDefinition1 = randomObjectDefinition();

		objectDefinition1 = testGetObjectDefinitionsPage_addObjectDefinition(
			objectDefinition1);

		for (EntityField entityField : entityFields) {
			Page<ObjectDefinition> page =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null,
					getFilterString(entityField, "between", objectDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectDefinition1),
				(List<ObjectDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionsPageWithFilterDoubleEquals()
		throws Exception {

		testGetObjectDefinitionsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetObjectDefinitionsPageWithFilterStringContains()
		throws Exception {

		testGetObjectDefinitionsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionsPageWithFilterStringEquals()
		throws Exception {

		testGetObjectDefinitionsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionsPageWithFilterStringStartsWith()
		throws Exception {

		testGetObjectDefinitionsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetObjectDefinitionsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ObjectDefinition objectDefinition1 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectDefinition objectDefinition2 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		for (EntityField entityField : entityFields) {
			Page<ObjectDefinition> page =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null,
					getFilterString(entityField, operator, objectDefinition1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectDefinition1),
				(List<ObjectDefinition>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionsPageWithPagination() throws Exception {
		Page<ObjectDefinition> objectDefinitionPage =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectDefinitionPage.getTotalCount());

		ObjectDefinition objectDefinition1 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		ObjectDefinition objectDefinition2 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		ObjectDefinition objectDefinition3 =
			testGetObjectDefinitionsPage_addObjectDefinition(
				randomObjectDefinition());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectDefinition> page1 =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				objectDefinition1, (List<ObjectDefinition>)page1.getItems());

			Page<ObjectDefinition> page2 =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				objectDefinition2, (List<ObjectDefinition>)page2.getItems());

			Page<ObjectDefinition> page3 =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				objectDefinition3, (List<ObjectDefinition>)page3.getItems());
		}
		else {
			Page<ObjectDefinition> page1 =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null, Pagination.of(1, totalCount + 2), null);

			List<ObjectDefinition> objectDefinitions1 =
				(List<ObjectDefinition>)page1.getItems();

			Assert.assertEquals(
				objectDefinitions1.toString(), totalCount + 2,
				objectDefinitions1.size());

			Page<ObjectDefinition> page2 =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectDefinition> objectDefinitions2 =
				(List<ObjectDefinition>)page2.getItems();

			Assert.assertEquals(
				objectDefinitions2.toString(), 1, objectDefinitions2.size());

			Page<ObjectDefinition> page3 =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				objectDefinition1, (List<ObjectDefinition>)page3.getItems());
			assertContains(
				objectDefinition2, (List<ObjectDefinition>)page3.getItems());
			assertContains(
				objectDefinition3, (List<ObjectDefinition>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectDefinition1, objectDefinition2) -> {
				BeanTestUtil.setProperty(
					objectDefinition1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionsPageWithSortDouble() throws Exception {
		testGetObjectDefinitionsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectDefinition1, objectDefinition2) -> {
				BeanTestUtil.setProperty(
					objectDefinition1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectDefinition2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionsPageWithSortInteger() throws Exception {
		testGetObjectDefinitionsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectDefinition1, objectDefinition2) -> {
				BeanTestUtil.setProperty(
					objectDefinition1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectDefinition2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionsPageWithSortString() throws Exception {
		testGetObjectDefinitionsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectDefinition1, objectDefinition2) -> {
				Class<?> clazz = objectDefinition1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectDefinition1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectDefinition2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectDefinition1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectDefinition2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ObjectDefinition, ObjectDefinition, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		ObjectDefinition objectDefinition1 = randomObjectDefinition();
		ObjectDefinition objectDefinition2 = randomObjectDefinition();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, objectDefinition1, objectDefinition2);
		}

		objectDefinition1 = testGetObjectDefinitionsPage_addObjectDefinition(
			objectDefinition1);

		objectDefinition2 = testGetObjectDefinitionsPage_addObjectDefinition(
			objectDefinition2);

		Page<ObjectDefinition> page =
			objectDefinitionResource.getObjectDefinitionsPage(
				null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectDefinition> ascPage =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				objectDefinition1, (List<ObjectDefinition>)ascPage.getItems());
			assertContains(
				objectDefinition2, (List<ObjectDefinition>)ascPage.getItems());

			Page<ObjectDefinition> descPage =
				objectDefinitionResource.getObjectDefinitionsPage(
					null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				objectDefinition2, (List<ObjectDefinition>)descPage.getItems());
			assertContains(
				objectDefinition1, (List<ObjectDefinition>)descPage.getItems());
		}
	}

	protected ObjectDefinition testGetObjectDefinitionsPage_addObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectDefinitionsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitions",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject objectDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitions");

		long totalCount = objectDefinitionsJSONObject.getLong("totalCount");

		ObjectDefinition objectDefinition1 =
			testGraphQLGetObjectDefinitionsPage_addObjectDefinition();
		ObjectDefinition objectDefinition2 =
			testGraphQLGetObjectDefinitionsPage_addObjectDefinition();

		objectDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitions");

		Assert.assertEquals(
			totalCount + 2, objectDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			objectDefinition1,
			Arrays.asList(
				ObjectDefinitionSerDes.toDTOs(
					objectDefinitionsJSONObject.getString("items"))));
		assertContains(
			objectDefinition2,
			Arrays.asList(
				ObjectDefinitionSerDes.toDTOs(
					objectDefinitionsJSONObject.getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("objectAdmin_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/objectAdmin_v1_0",
			"JSONObject/objectDefinitions");

		Assert.assertEquals(
			totalCount + 2, objectDefinitionsJSONObject.getLong("totalCount"));

		assertContains(
			objectDefinition1,
			Arrays.asList(
				ObjectDefinitionSerDes.toDTOs(
					objectDefinitionsJSONObject.getString("items"))));
		assertContains(
			objectDefinition2,
			Arrays.asList(
				ObjectDefinitionSerDes.toDTOs(
					objectDefinitionsJSONObject.getString("items"))));
	}

	protected ObjectDefinition
			testGraphQLGetObjectDefinitionsPage_addObjectDefinition()
		throws Exception {

		return testGraphQLObjectDefinition_addObjectDefinition();
	}

	@Test
	public void testPostObjectDefinition() throws Exception {
		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		ObjectDefinition postObjectDefinition =
			testPostObjectDefinition_addObjectDefinition(
				randomObjectDefinition);

		assertEquals(randomObjectDefinition, postObjectDefinition);
		assertValid(postObjectDefinition);
	}

	protected ObjectDefinition testPostObjectDefinition_addObjectDefinition(
			ObjectDefinition objectDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCode()
		throws Exception {

		ObjectDefinition postObjectDefinition =
			testGetObjectDefinitionByExternalReferenceCode_addObjectDefinition();

		ObjectDefinition getObjectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				postObjectDefinition.getExternalReferenceCode());

		assertEquals(postObjectDefinition, getObjectDefinition);
		assertValid(getObjectDefinition);
	}

	protected ObjectDefinition
			testGetObjectDefinitionByExternalReferenceCode_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCode()
		throws Exception {

		ObjectDefinition objectDefinition =
			testGraphQLGetObjectDefinitionByExternalReferenceCode_addObjectDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				objectDefinition,
				ObjectDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectDefinitionByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												objectDefinition.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/objectDefinitionByExternalReferenceCode"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectDefinition,
				ObjectDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectDefinitionByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													objectDefinition.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectDefinitionByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectDefinitionByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
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
							"objectDefinitionByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectDefinition
			testGraphQLGetObjectDefinitionByExternalReferenceCode_addObjectDefinition()
		throws Exception {

		return testGraphQLObjectDefinition_addObjectDefinition();
	}

	@Test
	public void testPutObjectDefinitionByExternalReferenceCode()
		throws Exception {

		ObjectDefinition postObjectDefinition =
			testPutObjectDefinitionByExternalReferenceCode_addObjectDefinition();

		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		ObjectDefinition putObjectDefinition =
			objectDefinitionResource.putObjectDefinitionByExternalReferenceCode(
				postObjectDefinition.getExternalReferenceCode(),
				randomObjectDefinition);

		assertEquals(randomObjectDefinition, putObjectDefinition);
		assertValid(putObjectDefinition);

		ObjectDefinition getObjectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				putObjectDefinition.getExternalReferenceCode());

		assertEquals(randomObjectDefinition, getObjectDefinition);
		assertValid(getObjectDefinition);

		ObjectDefinition newObjectDefinition =
			testPutObjectDefinitionByExternalReferenceCode_createObjectDefinition();

		putObjectDefinition =
			objectDefinitionResource.putObjectDefinitionByExternalReferenceCode(
				newObjectDefinition.getExternalReferenceCode(),
				newObjectDefinition);

		assertEquals(newObjectDefinition, putObjectDefinition);
		assertValid(putObjectDefinition);

		getObjectDefinition =
			objectDefinitionResource.getObjectDefinitionByExternalReferenceCode(
				putObjectDefinition.getExternalReferenceCode());

		assertEquals(newObjectDefinition, getObjectDefinition);

		Assert.assertEquals(
			newObjectDefinition.getExternalReferenceCode(),
			putObjectDefinition.getExternalReferenceCode());
	}

	protected ObjectDefinition
			testPutObjectDefinitionByExternalReferenceCode_createObjectDefinition()
		throws Exception {

		return randomObjectDefinition();
	}

	protected ObjectDefinition
			testPutObjectDefinitionByExternalReferenceCode_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteObjectDefinition() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectDefinition objectDefinition =
			testDeleteObjectDefinition_addObjectDefinition();

		assertHttpResponseStatusCode(
			204,
			objectDefinitionResource.deleteObjectDefinitionHttpResponse(
				objectDefinition.getId()));

		assertHttpResponseStatusCode(
			404,
			objectDefinitionResource.getObjectDefinitionHttpResponse(
				objectDefinition.getId()));

		assertHttpResponseStatusCode(
			404, objectDefinitionResource.getObjectDefinitionHttpResponse(0L));
	}

	protected ObjectDefinition testDeleteObjectDefinition_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectDefinition() throws Exception {

		// No namespace

		ObjectDefinition objectDefinition1 =
			testGraphQLDeleteObjectDefinition_addObjectDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"objectDefinitionId",
									objectDefinition1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectDefinition"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectDefinition",
					new HashMap<String, Object>() {
						{
							put(
								"objectDefinitionId",
								objectDefinition1.getId());
						}
					},
					new GraphQLField("id"))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectDefinition objectDefinition2 =
			testGraphQLDeleteObjectDefinition_addObjectDefinition();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"objectDefinitionId",
										objectDefinition2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectDefinition"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"objectDefinitionId",
									objectDefinition2.getId());
							}
						},
						new GraphQLField("id")))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectDefinition
			testGraphQLDeleteObjectDefinition_addObjectDefinition()
		throws Exception {

		return testGraphQLObjectDefinition_addObjectDefinition();
	}

	@Test
	public void testGetObjectDefinition() throws Exception {
		ObjectDefinition postObjectDefinition =
			testGetObjectDefinition_addObjectDefinition();

		ObjectDefinition getObjectDefinition =
			objectDefinitionResource.getObjectDefinition(
				postObjectDefinition.getId());

		assertEquals(postObjectDefinition, getObjectDefinition);
		assertValid(getObjectDefinition);
	}

	protected ObjectDefinition testGetObjectDefinition_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectDefinition() throws Exception {
		ObjectDefinition objectDefinition =
			testGraphQLGetObjectDefinition_addObjectDefinition();

		// No namespace

		Assert.assertTrue(
			equals(
				objectDefinition,
				ObjectDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectDefinition",
								new HashMap<String, Object>() {
									{
										put(
											"objectDefinitionId",
											objectDefinition.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectDefinition"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectDefinition,
				ObjectDefinitionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectDefinition",
									new HashMap<String, Object>() {
										{
											put(
												"objectDefinitionId",
												objectDefinition.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectDefinition"))));
	}

	@Test
	public void testGraphQLGetObjectDefinitionNotFound() throws Exception {
		Long irrelevantObjectDefinitionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectDefinition",
						new HashMap<String, Object>() {
							{
								put(
									"objectDefinitionId",
									irrelevantObjectDefinitionId);
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
							"objectDefinition",
							new HashMap<String, Object>() {
								{
									put(
										"objectDefinitionId",
										irrelevantObjectDefinitionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectDefinition
			testGraphQLGetObjectDefinition_addObjectDefinition()
		throws Exception {

		return testGraphQLObjectDefinition_addObjectDefinition();
	}

	@Test
	public void testPatchObjectDefinition() throws Exception {
		ObjectDefinition postObjectDefinition =
			testPatchObjectDefinition_addObjectDefinition();

		ObjectDefinition randomPatchObjectDefinition =
			randomPatchObjectDefinition();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectDefinition patchObjectDefinition =
			objectDefinitionResource.patchObjectDefinition(
				postObjectDefinition.getId(), randomPatchObjectDefinition);

		ObjectDefinition expectedPatchObjectDefinition =
			postObjectDefinition.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectDefinition, expectedPatchObjectDefinition);

		ObjectDefinition getObjectDefinition =
			objectDefinitionResource.getObjectDefinition(
				patchObjectDefinition.getId());

		assertEquals(expectedPatchObjectDefinition, getObjectDefinition);
		assertValid(getObjectDefinition);
	}

	protected ObjectDefinition testPatchObjectDefinition_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectDefinition() throws Exception {
		ObjectDefinition postObjectDefinition =
			testPutObjectDefinition_addObjectDefinition();

		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		ObjectDefinition putObjectDefinition =
			objectDefinitionResource.putObjectDefinition(
				postObjectDefinition.getId(), randomObjectDefinition);

		assertEquals(randomObjectDefinition, putObjectDefinition);
		assertValid(putObjectDefinition);

		ObjectDefinition getObjectDefinition =
			objectDefinitionResource.getObjectDefinition(
				putObjectDefinition.getId());

		assertEquals(randomObjectDefinition, getObjectDefinition);
		assertValid(getObjectDefinition);
	}

	protected ObjectDefinition testPutObjectDefinition_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionPublish() throws Exception {
		ObjectDefinition randomObjectDefinition = randomObjectDefinition();

		ObjectDefinition postObjectDefinition =
			testPostObjectDefinitionPublish_addObjectDefinition(
				randomObjectDefinition);

		assertEquals(randomObjectDefinition, postObjectDefinition);
		assertValid(postObjectDefinition);
	}

	protected ObjectDefinition
			testPostObjectDefinitionPublish_addObjectDefinition(
				ObjectDefinition objectDefinition)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ObjectDefinition testGraphQLObjectDefinition_addObjectDefinition()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ObjectDefinition objectDefinition,
		List<ObjectDefinition> objectDefinitions) {

		boolean contains = false;

		for (ObjectDefinition item : objectDefinitions) {
			if (equals(objectDefinition, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectDefinitions + " does not contain " + objectDefinition,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectDefinition objectDefinition1,
		ObjectDefinition objectDefinition2) {

		Assert.assertTrue(
			objectDefinition1 + " does not equal " + objectDefinition2,
			equals(objectDefinition1, objectDefinition2));
	}

	protected void assertEquals(
		List<ObjectDefinition> objectDefinitions1,
		List<ObjectDefinition> objectDefinitions2) {

		Assert.assertEquals(
			objectDefinitions1.size(), objectDefinitions2.size());

		for (int i = 0; i < objectDefinitions1.size(); i++) {
			ObjectDefinition objectDefinition1 = objectDefinitions1.get(i);
			ObjectDefinition objectDefinition2 = objectDefinitions2.get(i);

			assertEquals(objectDefinition1, objectDefinition2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectDefinition> objectDefinitions1,
		List<ObjectDefinition> objectDefinitions2) {

		Assert.assertEquals(
			objectDefinitions1.size(), objectDefinitions2.size());

		for (ObjectDefinition objectDefinition1 : objectDefinitions1) {
			boolean contains = false;

			for (ObjectDefinition objectDefinition2 : objectDefinitions2) {
				if (equals(objectDefinition1, objectDefinition2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectDefinitions2 + " does not contain " + objectDefinition1,
				contains);
		}
	}

	protected void assertValid(ObjectDefinition objectDefinition)
		throws Exception {

		boolean valid = true;

		if (objectDefinition.getDateCreated() == null) {
			valid = false;
		}

		if (objectDefinition.getDateModified() == null) {
			valid = false;
		}

		if (objectDefinition.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountEntryRestricted", additionalAssertFieldName)) {

				if (objectDefinition.getAccountEntryRestricted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountEntryRestrictedObjectFieldName",
					additionalAssertFieldName)) {

				if (objectDefinition.
						getAccountEntryRestrictedObjectFieldName() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectDefinition.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (objectDefinition.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (objectDefinition.getClassName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (objectDefinition.getDefaultLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"enableCategorization", additionalAssertFieldName)) {

				if (objectDefinition.getEnableCategorization() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("enableComments", additionalAssertFieldName)) {
				if (objectDefinition.getEnableComments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"enableFriendlyURLCustomization",
					additionalAssertFieldName)) {

				if (objectDefinition.getEnableFriendlyURLCustomization() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"enableIndexSearch", additionalAssertFieldName)) {

				if (objectDefinition.getEnableIndexSearch() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"enableLocalization", additionalAssertFieldName)) {

				if (objectDefinition.getEnableLocalization() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"enableObjectEntryDraft", additionalAssertFieldName)) {

				if (objectDefinition.getEnableObjectEntryDraft() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"enableObjectEntryHistory", additionalAssertFieldName)) {

				if (objectDefinition.getEnableObjectEntryHistory() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectDefinition.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (objectDefinition.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("modifiable", additionalAssertFieldName)) {
				if (objectDefinition.getModifiable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectDefinition.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectActions", additionalAssertFieldName)) {
				if (objectDefinition.getObjectActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionSettings", additionalAssertFieldName)) {

				if (objectDefinition.getObjectDefinitionSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectFields", additionalAssertFieldName)) {
				if (objectDefinition.getObjectFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectDefinition.getObjectFolderExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectLayouts", additionalAssertFieldName)) {
				if (objectDefinition.getObjectLayouts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectRelationships", additionalAssertFieldName)) {

				if (objectDefinition.getObjectRelationships() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectValidationRules", additionalAssertFieldName)) {

				if (objectDefinition.getObjectValidationRules() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectViews", additionalAssertFieldName)) {
				if (objectDefinition.getObjectViews() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("panelAppOrder", additionalAssertFieldName)) {
				if (objectDefinition.getPanelAppOrder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("panelCategoryKey", additionalAssertFieldName)) {
				if (objectDefinition.getPanelCategoryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parameterRequired", additionalAssertFieldName)) {

				if (objectDefinition.getParameterRequired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("pluralLabel", additionalAssertFieldName)) {
				if (objectDefinition.getPluralLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("portlet", additionalAssertFieldName)) {
				if (objectDefinition.getPortlet() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("restContextPath", additionalAssertFieldName)) {
				if (objectDefinition.getRestContextPath() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"rootObjectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectDefinition.
						getRootObjectDefinitionExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("scope", additionalAssertFieldName)) {
				if (objectDefinition.getScope() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (objectDefinition.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("storageType", additionalAssertFieldName)) {
				if (objectDefinition.getStorageType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (objectDefinition.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"titleObjectFieldName", additionalAssertFieldName)) {

				if (objectDefinition.getTitleObjectFieldName() == null) {
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

	protected void assertValid(Page<ObjectDefinition> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectDefinition> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectDefinition> objectDefinitions =
			page.getItems();

		int size = objectDefinitions.size();

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
					com.liferay.object.admin.rest.dto.v1_0.ObjectDefinition.
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
		ObjectDefinition objectDefinition1,
		ObjectDefinition objectDefinition2) {

		if (objectDefinition1 == objectDefinition2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountEntryRestricted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getAccountEntryRestricted(),
						objectDefinition2.getAccountEntryRestricted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountEntryRestrictedObjectFieldName",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.
							getAccountEntryRestrictedObjectFieldName(),
						objectDefinition2.
							getAccountEntryRestrictedObjectFieldName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectDefinition1.getActions(),
						(Map)objectDefinition2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getActive(),
						objectDefinition2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("className", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getClassName(),
						objectDefinition2.getClassName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getDateCreated(),
						objectDefinition2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getDateModified(),
						objectDefinition2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultLanguageId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getDefaultLanguageId(),
						objectDefinition2.getDefaultLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"enableCategorization", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getEnableCategorization(),
						objectDefinition2.getEnableCategorization())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("enableComments", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getEnableComments(),
						objectDefinition2.getEnableComments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"enableFriendlyURLCustomization",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getEnableFriendlyURLCustomization(),
						objectDefinition2.
							getEnableFriendlyURLCustomization())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"enableIndexSearch", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getEnableIndexSearch(),
						objectDefinition2.getEnableIndexSearch())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"enableLocalization", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getEnableLocalization(),
						objectDefinition2.getEnableLocalization())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"enableObjectEntryDraft", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getEnableObjectEntryDraft(),
						objectDefinition2.getEnableObjectEntryDraft())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"enableObjectEntryHistory", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getEnableObjectEntryHistory(),
						objectDefinition2.getEnableObjectEntryHistory())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getExternalReferenceCode(),
						objectDefinition2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getId(), objectDefinition2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectDefinition1.getLabel(),
						(Map)objectDefinition2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("modifiable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getModifiable(),
						objectDefinition2.getModifiable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getName(),
						objectDefinition2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectActions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getObjectActions(),
						objectDefinition2.getObjectActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getObjectDefinitionSettings(),
						objectDefinition2.getObjectDefinitionSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getObjectFields(),
						objectDefinition2.getObjectFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectFolderExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.
							getObjectFolderExternalReferenceCode(),
						objectDefinition2.
							getObjectFolderExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectLayouts", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getObjectLayouts(),
						objectDefinition2.getObjectLayouts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectRelationships", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getObjectRelationships(),
						objectDefinition2.getObjectRelationships())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectValidationRules", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getObjectValidationRules(),
						objectDefinition2.getObjectValidationRules())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectViews", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getObjectViews(),
						objectDefinition2.getObjectViews())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("panelAppOrder", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getPanelAppOrder(),
						objectDefinition2.getPanelAppOrder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("panelCategoryKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getPanelCategoryKey(),
						objectDefinition2.getPanelCategoryKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parameterRequired", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getParameterRequired(),
						objectDefinition2.getParameterRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("pluralLabel", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectDefinition1.getPluralLabel(),
						(Map)objectDefinition2.getPluralLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("portlet", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getPortlet(),
						objectDefinition2.getPortlet())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("restContextPath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getRestContextPath(),
						objectDefinition2.getRestContextPath())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"rootObjectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.
							getRootObjectDefinitionExternalReferenceCode(),
						objectDefinition2.
							getRootObjectDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("scope", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getScope(),
						objectDefinition2.getScope())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getStatus(),
						objectDefinition2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("storageType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getStorageType(),
						objectDefinition2.getStorageType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectDefinition1.getSystem(),
						objectDefinition2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"titleObjectFieldName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectDefinition1.getTitleObjectFieldName(),
						objectDefinition2.getTitleObjectFieldName())) {

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

		if (!(_objectDefinitionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectDefinitionResource;

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
		ObjectDefinition objectDefinition) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountEntryRestricted")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountEntryRestrictedObjectFieldName")) {
			Object object =
				objectDefinition.getAccountEntryRestrictedObjectFieldName();

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

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("className")) {
			Object object = objectDefinition.getClassName();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = objectDefinition.getDateCreated();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(objectDefinition.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectDefinition.getDateModified();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(
					_dateFormat.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(
					_dateFormat.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(
					_dateFormat.format(objectDefinition.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("defaultLanguageId")) {
			Object object = objectDefinition.getDefaultLanguageId();

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

		if (entityFieldName.equals("enableCategorization")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enableComments")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enableFriendlyURLCustomization")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enableIndexSearch")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enableLocalization")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enableObjectEntryDraft")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("enableObjectEntryHistory")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = objectDefinition.getExternalReferenceCode();

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

		if (entityFieldName.equals("label")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("modifiable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("name")) {
			Object object = objectDefinition.getName();

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

		if (entityFieldName.equals("objectActions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectFolderExternalReferenceCode")) {
			Object object =
				objectDefinition.getObjectFolderExternalReferenceCode();

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

		if (entityFieldName.equals("objectLayouts")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectRelationships")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectValidationRules")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectViews")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("panelAppOrder")) {
			Object object = objectDefinition.getPanelAppOrder();

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

		if (entityFieldName.equals("panelCategoryKey")) {
			Object object = objectDefinition.getPanelCategoryKey();

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

		if (entityFieldName.equals("parameterRequired")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("pluralLabel")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("portlet")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("restContextPath")) {
			Object object = objectDefinition.getRestContextPath();

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

		if (entityFieldName.equals(
				"rootObjectDefinitionExternalReferenceCode")) {

			Object object =
				objectDefinition.getRootObjectDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("scope")) {
			Object object = objectDefinition.getScope();

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

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("storageType")) {
			Object object = objectDefinition.getStorageType();

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

		if (entityFieldName.equals("system")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("titleObjectFieldName")) {
			Object object = objectDefinition.getTitleObjectFieldName();

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

	protected ObjectDefinition randomObjectDefinition() throws Exception {
		return new ObjectDefinition() {
			{
				accountEntryRestricted = RandomTestUtil.randomBoolean();
				accountEntryRestrictedObjectFieldName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				active = RandomTestUtil.randomBoolean();
				className = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultLanguageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				enableCategorization = RandomTestUtil.randomBoolean();
				enableComments = RandomTestUtil.randomBoolean();
				enableFriendlyURLCustomization = RandomTestUtil.randomBoolean();
				enableIndexSearch = RandomTestUtil.randomBoolean();
				enableLocalization = RandomTestUtil.randomBoolean();
				enableObjectEntryDraft = RandomTestUtil.randomBoolean();
				enableObjectEntryHistory = RandomTestUtil.randomBoolean();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				modifiable = RandomTestUtil.randomBoolean();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				objectFolderExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				panelAppOrder = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				panelCategoryKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				parameterRequired = RandomTestUtil.randomBoolean();
				portlet = RandomTestUtil.randomBoolean();
				restContextPath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				rootObjectDefinitionExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				scope = StringUtil.toLowerCase(RandomTestUtil.randomString());
				storageType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				system = RandomTestUtil.randomBoolean();
				titleObjectFieldName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected ObjectDefinition randomIrrelevantObjectDefinition()
		throws Exception {

		ObjectDefinition randomIrrelevantObjectDefinition =
			randomObjectDefinition();

		return randomIrrelevantObjectDefinition;
	}

	protected ObjectDefinition randomPatchObjectDefinition() throws Exception {
		return randomObjectDefinition();
	}

	protected ObjectDefinitionResource objectDefinitionResource;
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
		LogFactoryUtil.getLog(BaseObjectDefinitionResourceTestCase.class);

	private static DateFormat _dateFormat;

	@Inject
	private com.liferay.object.admin.rest.resource.v1_0.ObjectDefinitionResource
		_objectDefinitionResource;

}