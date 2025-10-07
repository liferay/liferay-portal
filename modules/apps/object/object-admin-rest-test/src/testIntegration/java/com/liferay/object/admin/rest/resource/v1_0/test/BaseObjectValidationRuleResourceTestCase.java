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
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectValidationRule;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectValidationRuleResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectValidationRuleSerDes;
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
public abstract class BaseObjectValidationRuleResourceTestCase {

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

		_objectValidationRuleResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectValidationRuleResource = ObjectValidationRuleResource.builder(
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

		ObjectValidationRule objectValidationRule1 =
			randomObjectValidationRule();

		String json = objectMapper.writeValueAsString(objectValidationRule1);

		ObjectValidationRule objectValidationRule2 =
			ObjectValidationRuleSerDes.toDTO(json);

		Assert.assertTrue(equals(objectValidationRule1, objectValidationRule2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectValidationRule objectValidationRule =
			randomObjectValidationRule();

		String json1 = objectMapper.writeValueAsString(objectValidationRule);
		String json2 = ObjectValidationRuleSerDes.toJSON(objectValidationRule);

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

		ObjectValidationRule objectValidationRule =
			randomObjectValidationRule();

		objectValidationRule.setEngine(regex);
		objectValidationRule.setEngineLabel(regex);
		objectValidationRule.setExternalReferenceCode(regex);
		objectValidationRule.setObjectDefinitionExternalReferenceCode(regex);
		objectValidationRule.setScript(regex);

		String json = ObjectValidationRuleSerDes.toJSON(objectValidationRule);

		Assert.assertFalse(json.contains(regex));

		objectValidationRule = ObjectValidationRuleSerDes.toDTO(json);

		Assert.assertEquals(regex, objectValidationRule.getEngine());
		Assert.assertEquals(regex, objectValidationRule.getEngineLabel());
		Assert.assertEquals(
			regex, objectValidationRule.getExternalReferenceCode());
		Assert.assertEquals(
			regex,
			objectValidationRule.getObjectDefinitionExternalReferenceCode());
		Assert.assertEquals(regex, objectValidationRule.getScript());
	}

	@Test
	public void testDeleteObjectValidationRule() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectValidationRule objectValidationRule =
			testDeleteObjectValidationRule_addObjectValidationRule();

		assertHttpResponseStatusCode(
			204,
			objectValidationRuleResource.deleteObjectValidationRuleHttpResponse(
				objectValidationRule.getId()));

		assertHttpResponseStatusCode(
			404,
			objectValidationRuleResource.getObjectValidationRuleHttpResponse(
				objectValidationRule.getId()));
		assertHttpResponseStatusCode(
			404,
			objectValidationRuleResource.getObjectValidationRuleHttpResponse(
				0L));
	}

	protected ObjectValidationRule
			testDeleteObjectValidationRule_addObjectValidationRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectValidationRule() throws Exception {

		// No namespace

		ObjectValidationRule objectValidationRule1 =
			testGraphQLDeleteObjectValidationRule_addObjectValidationRule();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectValidationRule",
						new HashMap<String, Object>() {
							{
								put(
									"objectValidationRuleId",
									objectValidationRule1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectValidationRule"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectValidationRule",
					new HashMap<String, Object>() {
						{
							put(
								"objectValidationRuleId",
								objectValidationRule1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectValidationRule objectValidationRule2 =
			testGraphQLDeleteObjectValidationRule_addObjectValidationRule();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectValidationRule",
							new HashMap<String, Object>() {
								{
									put(
										"objectValidationRuleId",
										objectValidationRule2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectValidationRule"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectValidationRule",
						new HashMap<String, Object>() {
							{
								put(
									"objectValidationRuleId",
									objectValidationRule2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectValidationRule
			testGraphQLDeleteObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return testGraphQLObjectValidationRule_addObjectValidationRule();
	}

	@Test
	public void testDeleteObjectValidationRuleBatch() throws Exception {
		ObjectValidationRule objectValidationRule1 =
			testDeleteObjectValidationRuleBatch_addObjectValidationRule();

		testDeleteObjectValidationRuleBatch_deleteObjectValidationRule(
			202, null, objectValidationRule1.getId());

		assertHttpResponseStatusCode(
			404,
			objectValidationRuleResource.getObjectValidationRuleHttpResponse(
				objectValidationRule1.getId()));
	}

	protected ObjectValidationRule
			testDeleteObjectValidationRuleBatch_addObjectValidationRule()
		throws Exception {

		return testDeleteObjectValidationRule_addObjectValidationRule();
	}

	protected void
			testDeleteObjectValidationRuleBatch_deleteObjectValidationRule(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectValidationRuleResource.
				deleteObjectValidationRuleBatchHttpResponse(
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
	public void testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getIrrelevantExternalReferenceCode();

		Page<ObjectValidationRule> page =
			objectValidationRuleResource.
				getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ObjectValidationRule irrelevantObjectValidationRule =
				testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
					irrelevantExternalReferenceCode,
					randomIrrelevantObjectValidationRule());

			page =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectValidationRule,
				(List<ObjectValidationRule>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ObjectValidationRule objectValidationRule1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		page =
			objectValidationRuleResource.
				getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			objectValidationRule1, (List<ObjectValidationRule>)page.getItems());
		assertContains(
			objectValidationRule2, (List<ObjectValidationRule>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExpectedActions(
				externalReferenceCode));

		objectValidationRuleResource.deleteObjectValidationRule(
			objectValidationRule1.getId());

		objectValidationRuleResource.deleteObjectValidationRule(
			objectValidationRule2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExternalReferenceCode();

		Page<ObjectValidationRule> objectValidationRulesPage =
			objectValidationRuleResource.
				getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectValidationRulesPage.getTotalCount());

		ObjectValidationRule objectValidationRule1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectValidationRule> page1 =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)page1.getItems());

			Page<ObjectValidationRule> page2 =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)page2.getItems());

			Page<ObjectValidationRule> page3 =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectValidationRule3,
				(List<ObjectValidationRule>)page3.getItems());
		}
		else {
			Page<ObjectValidationRule> page1 =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectValidationRule> objectValidationRules1 =
				(List<ObjectValidationRule>)page1.getItems();

			Assert.assertEquals(
				objectValidationRules1.toString(), totalCount + 2,
				objectValidationRules1.size());

			Page<ObjectValidationRule> page2 =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectValidationRule> objectValidationRules2 =
				(List<ObjectValidationRule>)page2.getItems();

			Assert.assertEquals(
				objectValidationRules2.toString(), 1,
				objectValidationRules2.size());

			Page<ObjectValidationRule> page3 =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)page3.getItems());
			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)page3.getItems());
			assertContains(
				objectValidationRule3,
				(List<ObjectValidationRule>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				BeanTestUtil.setProperty(
					objectValidationRule1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				BeanTestUtil.setProperty(
					objectValidationRule1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectValidationRule2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				BeanTestUtil.setProperty(
					objectValidationRule1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectValidationRule2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSortString()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				Class<?> clazz = objectValidationRule1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectValidationRule1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectValidationRule2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectValidationRule1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectValidationRule2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectValidationRule1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectValidationRule2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ObjectValidationRule, ObjectValidationRule,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExternalReferenceCode();

		ObjectValidationRule objectValidationRule1 =
			randomObjectValidationRule();
		ObjectValidationRule objectValidationRule2 =
			randomObjectValidationRule();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, objectValidationRule1, objectValidationRule2);
		}

		objectValidationRule1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, objectValidationRule1);

		objectValidationRule2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				externalReferenceCode, objectValidationRule2);

		Page<ObjectValidationRule> page =
			objectValidationRuleResource.
				getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
					externalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectValidationRule> ascPage =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)ascPage.getItems());
			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)ascPage.getItems());

			Page<ObjectValidationRule> descPage =
				objectValidationRuleResource.
					getObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)descPage.getItems());
			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)descPage.getItems());
		}
	}

	protected ObjectValidationRule
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_addObjectValidationRule(
				String externalReferenceCode,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionByExternalReferenceCodeObjectValidationRules",
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
			objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/objectDefinitionByExternalReferenceCodeObjectValidationRules");

		long totalCount =
			objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
				getLong("totalCount");

		ObjectValidationRule objectValidationRule1 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageObjectDefinitionObjectValidationRule_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule2 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageObjectDefinitionObjectValidationRule_addObjectValidationRule(
				externalReferenceCode, randomObjectValidationRule());

		objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectValidationRules");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
				getLong("totalCount"));

		assertContains(
			objectValidationRule1,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
						getString("items"))));
		assertContains(
			objectValidationRule2,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
						getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectValidationRules");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
				getLong("totalCount"));

		assertContains(
			objectValidationRule1,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
						getString("items"))));
		assertContains(
			objectValidationRule2,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectValidationRulesJSONObject.
						getString("items"))));
	}

	protected ObjectValidationRule
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectValidationRulesPageObjectDefinitionObjectValidationRule_addObjectValidationRule(
				String externalReferenceCode,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionObjectValidationRulesPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectValidationRulesPage_getIrrelevantObjectDefinitionId();

		Page<ObjectValidationRule> page =
			objectValidationRuleResource.
				getObjectDefinitionObjectValidationRulesPage(
					objectDefinitionId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectValidationRule irrelevantObjectValidationRule =
				testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
					irrelevantObjectDefinitionId,
					randomIrrelevantObjectValidationRule());

			page =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						irrelevantObjectDefinitionId, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectValidationRule,
				(List<ObjectValidationRule>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionObjectValidationRulesPage_getExpectedActions(
					irrelevantObjectDefinitionId));
		}

		ObjectValidationRule objectValidationRule1 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule2 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		page =
			objectValidationRuleResource.
				getObjectDefinitionObjectValidationRulesPage(
					objectDefinitionId, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			objectValidationRule1, (List<ObjectValidationRule>)page.getItems());
		assertContains(
			objectValidationRule2, (List<ObjectValidationRule>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionObjectValidationRulesPage_getExpectedActions(
				objectDefinitionId));

		objectValidationRuleResource.deleteObjectValidationRule(
			objectValidationRule1.getId());

		objectValidationRuleResource.deleteObjectValidationRule(
			objectValidationRule2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionObjectValidationRulesPage_getExpectedActions(
				Long objectDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-validation-rules/batch".
				replace(
					"{objectDefinitionId}",
					String.valueOf(objectDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionObjectValidationRulesPageWithPagination()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId();

		Page<ObjectValidationRule> objectValidationRulesPage =
			objectValidationRuleResource.
				getObjectDefinitionObjectValidationRulesPage(
					objectDefinitionId, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectValidationRulesPage.getTotalCount());

		ObjectValidationRule objectValidationRule1 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule2 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule3 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectValidationRule> page1 =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)page1.getItems());

			Page<ObjectValidationRule> page2 =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)page2.getItems());

			Page<ObjectValidationRule> page3 =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectValidationRule3,
				(List<ObjectValidationRule>)page3.getItems());
		}
		else {
			Page<ObjectValidationRule> page1 =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectValidationRule> objectValidationRules1 =
				(List<ObjectValidationRule>)page1.getItems();

			Assert.assertEquals(
				objectValidationRules1.toString(), totalCount + 2,
				objectValidationRules1.size());

			Page<ObjectValidationRule> page2 =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectValidationRule> objectValidationRules2 =
				(List<ObjectValidationRule>)page2.getItems();

			Assert.assertEquals(
				objectValidationRules2.toString(), 1,
				objectValidationRules2.size());

			Page<ObjectValidationRule> page3 =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)page3.getItems());
			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)page3.getItems());
			assertContains(
				objectValidationRule3,
				(List<ObjectValidationRule>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectValidationRulesPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionObjectValidationRulesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				BeanTestUtil.setProperty(
					objectValidationRule1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionObjectValidationRulesPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionObjectValidationRulesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				BeanTestUtil.setProperty(
					objectValidationRule1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectValidationRule2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectValidationRulesPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionObjectValidationRulesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				BeanTestUtil.setProperty(
					objectValidationRule1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectValidationRule2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectValidationRulesPageWithSortString()
		throws Exception {

		testGetObjectDefinitionObjectValidationRulesPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectValidationRule1, objectValidationRule2) -> {
				Class<?> clazz = objectValidationRule1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectValidationRule1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectValidationRule2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectValidationRule1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectValidationRule2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectValidationRule1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectValidationRule2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionObjectValidationRulesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ObjectValidationRule, ObjectValidationRule,
				 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId();

		ObjectValidationRule objectValidationRule1 =
			randomObjectValidationRule();
		ObjectValidationRule objectValidationRule2 =
			randomObjectValidationRule();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, objectValidationRule1, objectValidationRule2);
		}

		objectValidationRule1 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, objectValidationRule1);

		objectValidationRule2 =
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				objectDefinitionId, objectValidationRule2);

		Page<ObjectValidationRule> page =
			objectValidationRuleResource.
				getObjectDefinitionObjectValidationRulesPage(
					objectDefinitionId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectValidationRule> ascPage =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)ascPage.getItems());
			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)ascPage.getItems());

			Page<ObjectValidationRule> descPage =
				objectValidationRuleResource.
					getObjectDefinitionObjectValidationRulesPage(
						objectDefinitionId, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectValidationRule2,
				(List<ObjectValidationRule>)descPage.getItems());
			assertContains(
				objectValidationRule1,
				(List<ObjectValidationRule>)descPage.getItems());
		}
	}

	protected ObjectValidationRule
			testGetObjectDefinitionObjectValidationRulesPage_addObjectValidationRule(
				Long objectDefinitionId,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				objectDefinitionId, objectValidationRule);
	}

	protected Long
			testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectDefinitionObjectValidationRulesPage_getIrrelevantObjectDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionObjectValidationRulesPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionObjectValidationRules",
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

		JSONObject objectDefinitionObjectValidationRulesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectValidationRules");

		long totalCount =
			objectDefinitionObjectValidationRulesJSONObject.getLong(
				"totalCount");

		ObjectValidationRule objectValidationRule1 =
			testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		ObjectValidationRule objectValidationRule2 =
			testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule(
				objectDefinitionId, randomObjectValidationRule());

		objectDefinitionObjectValidationRulesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectValidationRules");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectValidationRulesJSONObject.getLong(
				"totalCount"));

		assertContains(
			objectValidationRule1,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionObjectValidationRulesJSONObject.getString(
						"items"))));
		assertContains(
			objectValidationRule2,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionObjectValidationRulesJSONObject.getString(
						"items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionObjectValidationRulesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionObjectValidationRules");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectValidationRulesJSONObject.getLong(
				"totalCount"));

		assertContains(
			objectValidationRule1,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionObjectValidationRulesJSONObject.getString(
						"items"))));
		assertContains(
			objectValidationRule2,
			Arrays.asList(
				ObjectValidationRuleSerDes.toDTOs(
					objectDefinitionObjectValidationRulesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetObjectValidationRule() throws Exception {
		ObjectValidationRule postObjectValidationRule =
			testGetObjectValidationRule_addObjectValidationRule();

		ObjectValidationRule getObjectValidationRule =
			objectValidationRuleResource.getObjectValidationRule(
				postObjectValidationRule.getId());

		assertEquals(postObjectValidationRule, getObjectValidationRule);
		assertValid(getObjectValidationRule);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectValidationRule postObjectValidationRule =
			testGetObjectValidationRule_addObjectValidationRule();

		ObjectValidationRule getObjectValidationRule =
			objectValidationRuleResource.getObjectValidationRule(
				postObjectValidationRule.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postObjectValidationRule.getId());

		assertEquals(
			getObjectValidationRule,
			ObjectValidationRuleSerDes.toDTO(item.toString()));
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

	protected ObjectValidationRule
			testGetObjectValidationRule_addObjectValidationRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectValidationRule() throws Exception {
		ObjectValidationRule objectValidationRule =
			testGraphQLGetObjectValidationRule_addObjectValidationRule();

		// No namespace

		Assert.assertTrue(
			equals(
				objectValidationRule,
				ObjectValidationRuleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectValidationRule",
								new HashMap<String, Object>() {
									{
										put(
											"objectValidationRuleId",
											objectValidationRule.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectValidationRule"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectValidationRule,
				ObjectValidationRuleSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectValidationRule",
									new HashMap<String, Object>() {
										{
											put(
												"objectValidationRuleId",
												objectValidationRule.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectValidationRule"))));
	}

	@Test
	public void testGraphQLGetObjectValidationRuleNotFound() throws Exception {
		Long irrelevantObjectValidationRuleId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectValidationRule",
						new HashMap<String, Object>() {
							{
								put(
									"objectValidationRuleId",
									irrelevantObjectValidationRuleId);
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
							"objectValidationRule",
							new HashMap<String, Object>() {
								{
									put(
										"objectValidationRuleId",
										irrelevantObjectValidationRuleId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectValidationRule
			testGraphQLGetObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return testGraphQLObjectValidationRule_addObjectValidationRule();
	}

	@Test
	public void testPatchObjectValidationRule() throws Exception {
		ObjectValidationRule postObjectValidationRule =
			testPatchObjectValidationRule_addObjectValidationRule();

		ObjectValidationRule randomPatchObjectValidationRule =
			randomPatchObjectValidationRule();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectValidationRule patchObjectValidationRule =
			objectValidationRuleResource.patchObjectValidationRule(
				postObjectValidationRule.getId(),
				randomPatchObjectValidationRule);

		ObjectValidationRule expectedPatchObjectValidationRule =
			postObjectValidationRule.clone();

		BeanTestUtil.copyProperties(
			randomPatchObjectValidationRule, expectedPatchObjectValidationRule);

		ObjectValidationRule getObjectValidationRule =
			objectValidationRuleResource.getObjectValidationRule(
				patchObjectValidationRule.getId());

		assertEquals(
			expectedPatchObjectValidationRule, getObjectValidationRule);
		assertValid(getObjectValidationRule);
	}

	protected ObjectValidationRule
			testPatchObjectValidationRule_addObjectValidationRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionByExternalReferenceCodeObjectValidationRule()
		throws Exception {

		ObjectValidationRule randomObjectValidationRule =
			randomObjectValidationRule();

		ObjectValidationRule postObjectValidationRule =
			testPostObjectDefinitionByExternalReferenceCodeObjectValidationRule_addObjectValidationRule(
				randomObjectValidationRule);

		assertEquals(randomObjectValidationRule, postObjectValidationRule);
		assertValid(postObjectValidationRule);
	}

	protected ObjectValidationRule
			testPostObjectDefinitionByExternalReferenceCodeObjectValidationRule_addObjectValidationRule(
				ObjectValidationRule objectValidationRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectValidationRule()
		throws Exception {

		ObjectValidationRule randomObjectValidationRule =
			randomObjectValidationRule();

		ObjectValidationRule objectValidationRule =
			testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule(
				testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectValidationRule_getObjectDefinitionId(
					randomObjectValidationRule),
				randomObjectValidationRule);

		Assert.assertTrue(
			equals(randomObjectValidationRule, objectValidationRule));
	}

	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectValidationRule_getObjectDefinitionId(
				ObjectValidationRule objectValidationRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionObjectValidationRule()
		throws Exception {

		ObjectValidationRule randomObjectValidationRule =
			randomObjectValidationRule();

		ObjectValidationRule postObjectValidationRule =
			testPostObjectDefinitionObjectValidationRule_addObjectValidationRule(
				randomObjectValidationRule);

		assertEquals(randomObjectValidationRule, postObjectValidationRule);
		assertValid(postObjectValidationRule);
	}

	protected ObjectValidationRule
			testPostObjectDefinitionObjectValidationRule_addObjectValidationRule(
				ObjectValidationRule objectValidationRule)
		throws Exception {

		return objectValidationRuleResource.
			postObjectDefinitionObjectValidationRule(
				testGetObjectDefinitionObjectValidationRulesPage_getObjectDefinitionId(),
				objectValidationRule);
	}

	@Test
	public void testGraphQLPostObjectDefinitionObjectValidationRule()
		throws Exception {

		ObjectValidationRule randomObjectValidationRule =
			randomObjectValidationRule();

		ObjectValidationRule objectValidationRule =
			testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule(
				testGraphQLPostObjectDefinitionObjectValidationRule_getObjectDefinitionId(
					randomObjectValidationRule),
				randomObjectValidationRule);

		Assert.assertTrue(
			equals(randomObjectValidationRule, objectValidationRule));
	}

	protected Long
			testGraphQLPostObjectDefinitionObjectValidationRule_getObjectDefinitionId(
				ObjectValidationRule objectValidationRule)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectValidationRule() throws Exception {
		ObjectValidationRule postObjectValidationRule =
			testPutObjectValidationRule_addObjectValidationRule();

		ObjectValidationRule randomObjectValidationRule =
			randomObjectValidationRule();

		ObjectValidationRule putObjectValidationRule =
			objectValidationRuleResource.putObjectValidationRule(
				postObjectValidationRule.getId(), randomObjectValidationRule);

		assertEquals(randomObjectValidationRule, putObjectValidationRule);
		assertValid(putObjectValidationRule);

		ObjectValidationRule getObjectValidationRule =
			objectValidationRuleResource.getObjectValidationRule(
				putObjectValidationRule.getId());

		assertEquals(randomObjectValidationRule, getObjectValidationRule);
		assertValid(getObjectValidationRule);
	}

	protected ObjectValidationRule
			testPutObjectValidationRule_addObjectValidationRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectValidationRule objectValidationRule1 =
			testBatchEngineDeleteImportTask_addObjectValidationRule();

		testBatchEngineDeleteImportTask_deleteObjectValidationRule(
			200, null, objectValidationRule1.getId());

		assertHttpResponseStatusCode(
			404,
			objectValidationRuleResource.getObjectValidationRuleHttpResponse(
				objectValidationRule1.getId()));
	}

	protected ObjectValidationRule
			testBatchEngineDeleteImportTask_addObjectValidationRule()
		throws Exception {

		return testDeleteObjectValidationRule_addObjectValidationRule();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectValidationRule(
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
				"com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule",
				null, null, null, null,
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

	protected ObjectValidationRule
			testGraphQLObjectValidationRule_addObjectValidationRule()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectValidationRule
			testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule()
		throws Exception {

		return testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule(
			testGraphQLObjectDefinitionObjectValidationRule_getObjectDefinitionId(),
			randomObjectValidationRule());
	}

	protected Long
			testGraphQLObjectDefinitionObjectValidationRule_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectValidationRule
			testGraphQLObjectDefinitionObjectValidationRule_addObjectValidationRule(
				Long objectDefinitionId,
				ObjectValidationRule objectValidationRule)
		throws Exception {

		JSONDeserializer<ObjectValidationRule> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectValidationRule.class)) {

			if (getGraphQLValue(field.get(objectValidationRule)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectValidationRule)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectDefinitionObjectValidationRule",
						new HashMap<String, Object>() {
							{
								put("objectDefinitionId", objectDefinitionId);
								put("objectValidationRule", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createObjectDefinitionObjectValidationRule"),
			ObjectValidationRule.class);
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
		ObjectValidationRule objectValidationRule,
		List<ObjectValidationRule> objectValidationRules) {

		boolean contains = false;

		for (ObjectValidationRule item : objectValidationRules) {
			if (equals(objectValidationRule, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectValidationRules + " does not contain " + objectValidationRule,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectValidationRule objectValidationRule1,
		ObjectValidationRule objectValidationRule2) {

		Assert.assertTrue(
			objectValidationRule1 + " does not equal " + objectValidationRule2,
			equals(objectValidationRule1, objectValidationRule2));
	}

	protected void assertEquals(
		List<ObjectValidationRule> objectValidationRules1,
		List<ObjectValidationRule> objectValidationRules2) {

		Assert.assertEquals(
			objectValidationRules1.size(), objectValidationRules2.size());

		for (int i = 0; i < objectValidationRules1.size(); i++) {
			ObjectValidationRule objectValidationRule1 =
				objectValidationRules1.get(i);
			ObjectValidationRule objectValidationRule2 =
				objectValidationRules2.get(i);

			assertEquals(objectValidationRule1, objectValidationRule2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectValidationRule> objectValidationRules1,
		List<ObjectValidationRule> objectValidationRules2) {

		Assert.assertEquals(
			objectValidationRules1.size(), objectValidationRules2.size());

		for (ObjectValidationRule objectValidationRule1 :
				objectValidationRules1) {

			boolean contains = false;

			for (ObjectValidationRule objectValidationRule2 :
					objectValidationRules2) {

				if (equals(objectValidationRule1, objectValidationRule2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectValidationRules2 + " does not contain " +
					objectValidationRule1,
				contains);
		}
	}

	protected void assertValid(ObjectValidationRule objectValidationRule)
		throws Exception {

		boolean valid = true;

		if (objectValidationRule.getDateCreated() == null) {
			valid = false;
		}

		if (objectValidationRule.getDateModified() == null) {
			valid = false;
		}

		if (objectValidationRule.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectValidationRule.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (objectValidationRule.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("engine", additionalAssertFieldName)) {
				if (objectValidationRule.getEngine() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("engineLabel", additionalAssertFieldName)) {
				if (objectValidationRule.getEngineLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("errorLabel", additionalAssertFieldName)) {
				if (objectValidationRule.getErrorLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectValidationRule.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectValidationRule.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectValidationRule.
						getObjectDefinitionExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (objectValidationRule.getObjectDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectValidationRuleSettings",
					additionalAssertFieldName)) {

				if (objectValidationRule.getObjectValidationRuleSettings() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("outputType", additionalAssertFieldName)) {
				if (objectValidationRule.getOutputType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("script", additionalAssertFieldName)) {
				if (objectValidationRule.getScript() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (objectValidationRule.getSystem() == null) {
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

	protected void assertValid(Page<ObjectValidationRule> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectValidationRule> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectValidationRule> objectValidationRules =
			page.getItems();

		int size = objectValidationRules.size();

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
					com.liferay.object.admin.rest.dto.v1_0.ObjectValidationRule.
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
		ObjectValidationRule objectValidationRule1,
		ObjectValidationRule objectValidationRule2) {

		if (objectValidationRule1 == objectValidationRule2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectValidationRule1.getActions(),
						(Map)objectValidationRule2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getActive(),
						objectValidationRule2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getDateCreated(),
						objectValidationRule2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getDateModified(),
						objectValidationRule2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("engine", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getEngine(),
						objectValidationRule2.getEngine())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("engineLabel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getEngineLabel(),
						objectValidationRule2.getEngineLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("errorLabel", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectValidationRule1.getErrorLabel(),
						(Map)objectValidationRule2.getErrorLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectValidationRule1.getExternalReferenceCode(),
						objectValidationRule2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getId(),
						objectValidationRule2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectValidationRule1.getName(),
						(Map)objectValidationRule2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectValidationRule1.
							getObjectDefinitionExternalReferenceCode(),
						objectValidationRule2.
							getObjectDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectValidationRule1.getObjectDefinitionId(),
						objectValidationRule2.getObjectDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectValidationRuleSettings",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectValidationRule1.getObjectValidationRuleSettings(),
						objectValidationRule2.
							getObjectValidationRuleSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("outputType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getOutputType(),
						objectValidationRule2.getOutputType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("script", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getScript(),
						objectValidationRule2.getScript())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectValidationRule1.getSystem(),
						objectValidationRule2.getSystem())) {

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

		if (!(_objectValidationRuleResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectValidationRuleResource;

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
		ObjectValidationRule objectValidationRule) {

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

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = objectValidationRule.getDateCreated();

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

				sb.append(
					_format.format(objectValidationRule.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectValidationRule.getDateModified();

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

				sb.append(
					_format.format(objectValidationRule.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("engine")) {
			Object object = objectValidationRule.getEngine();

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

		if (entityFieldName.equals("engineLabel")) {
			Object object = objectValidationRule.getEngineLabel();

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

		if (entityFieldName.equals("errorLabel")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = objectValidationRule.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionExternalReferenceCode")) {
			Object object =
				objectValidationRule.getObjectDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("objectDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectValidationRuleSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("outputType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("script")) {
			Object object = objectValidationRule.getScript();

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

	protected ObjectValidationRule randomObjectValidationRule()
		throws Exception {

		return new ObjectValidationRule() {
			{
				active = RandomTestUtil.randomBoolean();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				engine = StringUtil.toLowerCase(RandomTestUtil.randomString());
				engineLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				objectDefinitionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionId = RandomTestUtil.randomLong();
				script = StringUtil.toLowerCase(RandomTestUtil.randomString());
				system = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ObjectValidationRule randomIrrelevantObjectValidationRule()
		throws Exception {

		ObjectValidationRule randomIrrelevantObjectValidationRule =
			randomObjectValidationRule();

		return randomIrrelevantObjectValidationRule;
	}

	protected ObjectValidationRule randomPatchObjectValidationRule()
		throws Exception {

		return randomObjectValidationRule();
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

	protected ObjectValidationRuleResource objectValidationRuleResource;
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
		LogFactoryUtil.getLog(BaseObjectValidationRuleResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.object.admin.rest.resource.v1_0.ObjectValidationRuleResource
			_objectValidationRuleResource;

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