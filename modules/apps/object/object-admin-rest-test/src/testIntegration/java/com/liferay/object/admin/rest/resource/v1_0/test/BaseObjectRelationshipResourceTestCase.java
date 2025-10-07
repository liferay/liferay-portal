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
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectRelationship;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectRelationshipResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectRelationshipSerDes;
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
public abstract class BaseObjectRelationshipResourceTestCase {

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

		_objectRelationshipResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectRelationshipResource = ObjectRelationshipResource.builder(
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

		ObjectRelationship objectRelationship1 = randomObjectRelationship();

		String json = objectMapper.writeValueAsString(objectRelationship1);

		ObjectRelationship objectRelationship2 = ObjectRelationshipSerDes.toDTO(
			json);

		Assert.assertTrue(equals(objectRelationship1, objectRelationship2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectRelationship objectRelationship = randomObjectRelationship();

		String json1 = objectMapper.writeValueAsString(objectRelationship);
		String json2 = ObjectRelationshipSerDes.toJSON(objectRelationship);

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

		ObjectRelationship objectRelationship = randomObjectRelationship();

		objectRelationship.setExternalReferenceCode(regex);
		objectRelationship.setName(regex);
		objectRelationship.setObjectDefinitionExternalReferenceCode1(regex);
		objectRelationship.setObjectDefinitionExternalReferenceCode2(regex);
		objectRelationship.setObjectDefinitionName2(regex);
		objectRelationship.setObjectDefinitionScope2(regex);
		objectRelationship.setParameterObjectFieldName(regex);

		String json = ObjectRelationshipSerDes.toJSON(objectRelationship);

		Assert.assertFalse(json.contains(regex));

		objectRelationship = ObjectRelationshipSerDes.toDTO(json);

		Assert.assertEquals(
			regex, objectRelationship.getExternalReferenceCode());
		Assert.assertEquals(regex, objectRelationship.getName());
		Assert.assertEquals(
			regex,
			objectRelationship.getObjectDefinitionExternalReferenceCode1());
		Assert.assertEquals(
			regex,
			objectRelationship.getObjectDefinitionExternalReferenceCode2());
		Assert.assertEquals(
			regex, objectRelationship.getObjectDefinitionName2());
		Assert.assertEquals(
			regex, objectRelationship.getObjectDefinitionScope2());
		Assert.assertEquals(
			regex, objectRelationship.getParameterObjectFieldName());
	}

	@Test
	public void testDeleteObjectRelationship() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectRelationship objectRelationship =
			testDeleteObjectRelationship_addObjectRelationship();

		assertHttpResponseStatusCode(
			204,
			objectRelationshipResource.deleteObjectRelationshipHttpResponse(
				objectRelationship.getId()));

		assertHttpResponseStatusCode(
			404,
			objectRelationshipResource.getObjectRelationshipHttpResponse(
				objectRelationship.getId()));
		assertHttpResponseStatusCode(
			404,
			objectRelationshipResource.getObjectRelationshipHttpResponse(0L));
	}

	protected ObjectRelationship
			testDeleteObjectRelationship_addObjectRelationship()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectRelationship() throws Exception {

		// No namespace

		ObjectRelationship objectRelationship1 =
			testGraphQLDeleteObjectRelationship_addObjectRelationship();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectRelationship",
						new HashMap<String, Object>() {
							{
								put(
									"objectRelationshipId",
									objectRelationship1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectRelationship"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectRelationship",
					new HashMap<String, Object>() {
						{
							put(
								"objectRelationshipId",
								objectRelationship1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectRelationship objectRelationship2 =
			testGraphQLDeleteObjectRelationship_addObjectRelationship();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectRelationship",
							new HashMap<String, Object>() {
								{
									put(
										"objectRelationshipId",
										objectRelationship2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectRelationship"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectRelationship",
						new HashMap<String, Object>() {
							{
								put(
									"objectRelationshipId",
									objectRelationship2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectRelationship
			testGraphQLDeleteObjectRelationship_addObjectRelationship()
		throws Exception {

		return testGraphQLObjectRelationship_addObjectRelationship();
	}

	@Test
	public void testDeleteObjectRelationshipBatch() throws Exception {
		ObjectRelationship objectRelationship1 =
			testDeleteObjectRelationshipBatch_addObjectRelationship();

		testDeleteObjectRelationshipBatch_deleteObjectRelationship(
			202, null, objectRelationship1.getId());

		assertHttpResponseStatusCode(
			404,
			objectRelationshipResource.getObjectRelationshipHttpResponse(
				objectRelationship1.getId()));
	}

	protected ObjectRelationship
			testDeleteObjectRelationshipBatch_addObjectRelationship()
		throws Exception {

		return testDeleteObjectRelationship_addObjectRelationship();
	}

	protected void testDeleteObjectRelationshipBatch_deleteObjectRelationship(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectRelationshipResource.
				deleteObjectRelationshipBatchHttpResponse(
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
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getIrrelevantExternalReferenceCode();

		Page<ObjectRelationship> page =
			objectRelationshipResource.
				getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ObjectRelationship irrelevantObjectRelationship =
				testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
					irrelevantExternalReferenceCode,
					randomIrrelevantObjectRelationship());

			page =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectRelationship,
				(List<ObjectRelationship>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ObjectRelationship objectRelationship1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		ObjectRelationship objectRelationship2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		page =
			objectRelationshipResource.
				getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			objectRelationship1, (List<ObjectRelationship>)page.getItems());
		assertContains(
			objectRelationship2, (List<ObjectRelationship>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExpectedActions(
				externalReferenceCode));

		objectRelationshipResource.deleteObjectRelationship(
			objectRelationship1.getId());

		objectRelationshipResource.deleteObjectRelationship(
			objectRelationship2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode();

		ObjectRelationship objectRelationship1 = randomObjectRelationship();

		objectRelationship1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, objectRelationship1);

		for (EntityField entityField : entityFields) {
			Page<ObjectRelationship> page =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, "between", objectRelationship1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectRelationship1),
				(List<ObjectRelationship>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilterDoubleEquals()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilterStringContains()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilterStringEquals()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilterStringStartsWith()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode();

		ObjectRelationship objectRelationship1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectRelationship objectRelationship2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		for (EntityField entityField : entityFields) {
			Page<ObjectRelationship> page =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null,
						getFilterString(
							entityField, operator, objectRelationship1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectRelationship1),
				(List<ObjectRelationship>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode();

		Page<ObjectRelationship> objectRelationshipsPage =
			objectRelationshipResource.
				getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectRelationshipsPage.getTotalCount());

		ObjectRelationship objectRelationship1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		ObjectRelationship objectRelationship2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		ObjectRelationship objectRelationship3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectRelationship> page1 =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)page1.getItems());

			Page<ObjectRelationship> page2 =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)page2.getItems());

			Page<ObjectRelationship> page3 =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectRelationship3,
				(List<ObjectRelationship>)page3.getItems());
		}
		else {
			Page<ObjectRelationship> page1 =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectRelationship> objectRelationships1 =
				(List<ObjectRelationship>)page1.getItems();

			Assert.assertEquals(
				objectRelationships1.toString(), totalCount + 2,
				objectRelationships1.size());

			Page<ObjectRelationship> page2 =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectRelationship> objectRelationships2 =
				(List<ObjectRelationship>)page2.getItems();

			Assert.assertEquals(
				objectRelationships2.toString(), 1,
				objectRelationships2.size());

			Page<ObjectRelationship> page3 =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)page3.getItems());
			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)page3.getItems());
			assertContains(
				objectRelationship3,
				(List<ObjectRelationship>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectRelationship1, objectRelationship2) -> {
				BeanTestUtil.setProperty(
					objectRelationship1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectRelationship1, objectRelationship2) -> {
				BeanTestUtil.setProperty(
					objectRelationship1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectRelationship2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectRelationship1, objectRelationship2) -> {
				BeanTestUtil.setProperty(
					objectRelationship1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectRelationship2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectRelationship1, objectRelationship2) -> {
				Class<?> clazz = objectRelationship1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectRelationship1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectRelationship2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectRelationship1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectRelationship2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectRelationship1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectRelationship2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ObjectRelationship, ObjectRelationship,
					 Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode();

		ObjectRelationship objectRelationship1 = randomObjectRelationship();
		ObjectRelationship objectRelationship2 = randomObjectRelationship();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, objectRelationship1, objectRelationship2);
		}

		objectRelationship1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, objectRelationship1);

		objectRelationship2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				externalReferenceCode, objectRelationship2);

		Page<ObjectRelationship> page =
			objectRelationshipResource.
				getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectRelationship> ascPage =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)ascPage.getItems());
			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)ascPage.getItems());

			Page<ObjectRelationship> descPage =
				objectRelationshipResource.
					getObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)descPage.getItems());
			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)descPage.getItems());
		}
	}

	protected ObjectRelationship
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_addObjectRelationship(
				String externalReferenceCode,
				ObjectRelationship objectRelationship)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionByExternalReferenceCodeObjectRelationships",
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
			objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/objectDefinitionByExternalReferenceCodeObjectRelationships");

		long totalCount =
			objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
				getLong("totalCount");

		ObjectRelationship objectRelationship1 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageObjectDefinitionObjectRelationship_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		ObjectRelationship objectRelationship2 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageObjectDefinitionObjectRelationship_addObjectRelationship(
				externalReferenceCode, randomObjectRelationship());

		objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectRelationships");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectRelationship1,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
						getString("items"))));
		assertContains(
			objectRelationship2,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
						getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectRelationships");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectRelationship1,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
						getString("items"))));
		assertContains(
			objectRelationship2,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectRelationshipsJSONObject.
						getString("items"))));
	}

	protected ObjectRelationship
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectRelationshipsPageObjectDefinitionObjectRelationship_addObjectRelationship(
				String externalReferenceCode,
				ObjectRelationship objectRelationship)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getIrrelevantObjectDefinitionId();

		Page<ObjectRelationship> page =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinitionId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectRelationship irrelevantObjectRelationship =
				testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
					irrelevantObjectDefinitionId,
					randomIrrelevantObjectRelationship());

			page =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						irrelevantObjectDefinitionId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectRelationship,
				(List<ObjectRelationship>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionObjectRelationshipsPage_getExpectedActions(
					irrelevantObjectDefinitionId));
		}

		ObjectRelationship objectRelationship1 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		ObjectRelationship objectRelationship2 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		page =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinitionId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			objectRelationship1, (List<ObjectRelationship>)page.getItems());
		assertContains(
			objectRelationship2, (List<ObjectRelationship>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionObjectRelationshipsPage_getExpectedActions(
				objectDefinitionId));

		objectRelationshipResource.deleteObjectRelationship(
			objectRelationship1.getId());

		objectRelationshipResource.deleteObjectRelationship(
			objectRelationship2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionObjectRelationshipsPage_getExpectedActions(
				Long objectDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-relationships/batch".
				replace(
					"{objectDefinitionId}",
					String.valueOf(objectDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId();

		ObjectRelationship objectRelationship1 = randomObjectRelationship();

		objectRelationship1 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, objectRelationship1);

		for (EntityField entityField : entityFields) {
			Page<ObjectRelationship> page =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null,
						getFilterString(
							entityField, "between", objectRelationship1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectRelationship1),
				(List<ObjectRelationship>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithFilterDoubleEquals()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithFilterStringContains()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithFilterStringEquals()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithFilterStringStartsWith()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetObjectDefinitionObjectRelationshipsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId();

		ObjectRelationship objectRelationship1 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectRelationship objectRelationship2 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		for (EntityField entityField : entityFields) {
			Page<ObjectRelationship> page =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null,
						getFilterString(
							entityField, operator, objectRelationship1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(objectRelationship1),
				(List<ObjectRelationship>)page.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithPagination()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId();

		Page<ObjectRelationship> objectRelationshipsPage =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinitionId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectRelationshipsPage.getTotalCount());

		ObjectRelationship objectRelationship1 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		ObjectRelationship objectRelationship2 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		ObjectRelationship objectRelationship3 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectRelationship> page1 =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)page1.getItems());

			Page<ObjectRelationship> page2 =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)page2.getItems());

			Page<ObjectRelationship> page3 =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				objectRelationship3,
				(List<ObjectRelationship>)page3.getItems());
		}
		else {
			Page<ObjectRelationship> page1 =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectRelationship> objectRelationships1 =
				(List<ObjectRelationship>)page1.getItems();

			Assert.assertEquals(
				objectRelationships1.toString(), totalCount + 2,
				objectRelationships1.size());

			Page<ObjectRelationship> page2 =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectRelationship> objectRelationships2 =
				(List<ObjectRelationship>)page2.getItems();

			Assert.assertEquals(
				objectRelationships2.toString(), 1,
				objectRelationships2.size());

			Page<ObjectRelationship> page3 =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)page3.getItems());
			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)page3.getItems());
			assertContains(
				objectRelationship3,
				(List<ObjectRelationship>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectRelationship1, objectRelationship2) -> {
				BeanTestUtil.setProperty(
					objectRelationship1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectRelationship1, objectRelationship2) -> {
				BeanTestUtil.setProperty(
					objectRelationship1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectRelationship2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectRelationship1, objectRelationship2) -> {
				BeanTestUtil.setProperty(
					objectRelationship1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectRelationship2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectRelationshipsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionObjectRelationshipsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectRelationship1, objectRelationship2) -> {
				Class<?> clazz = objectRelationship1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectRelationship1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectRelationship2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectRelationship1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectRelationship2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectRelationship1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectRelationship2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionObjectRelationshipsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ObjectRelationship, ObjectRelationship, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId();

		ObjectRelationship objectRelationship1 = randomObjectRelationship();
		ObjectRelationship objectRelationship2 = randomObjectRelationship();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, objectRelationship1, objectRelationship2);
		}

		objectRelationship1 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, objectRelationship1);

		objectRelationship2 =
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				objectDefinitionId, objectRelationship2);

		Page<ObjectRelationship> page =
			objectRelationshipResource.
				getObjectDefinitionObjectRelationshipsPage(
					objectDefinitionId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectRelationship> ascPage =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)ascPage.getItems());
			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)ascPage.getItems());

			Page<ObjectRelationship> descPage =
				objectRelationshipResource.
					getObjectDefinitionObjectRelationshipsPage(
						objectDefinitionId, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectRelationship2,
				(List<ObjectRelationship>)descPage.getItems());
			assertContains(
				objectRelationship1,
				(List<ObjectRelationship>)descPage.getItems());
		}
	}

	protected ObjectRelationship
			testGetObjectDefinitionObjectRelationshipsPage_addObjectRelationship(
				Long objectDefinitionId, ObjectRelationship objectRelationship)
		throws Exception {

		return objectRelationshipResource.
			postObjectDefinitionObjectRelationship(
				objectDefinitionId, objectRelationship);
	}

	protected Long
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectDefinitionObjectRelationshipsPage_getIrrelevantObjectDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionObjectRelationshipsPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionObjectRelationships",
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

		JSONObject objectDefinitionObjectRelationshipsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectRelationships");

		long totalCount = objectDefinitionObjectRelationshipsJSONObject.getLong(
			"totalCount");

		ObjectRelationship objectRelationship1 =
			testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		ObjectRelationship objectRelationship2 =
			testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship(
				objectDefinitionId, randomObjectRelationship());

		objectDefinitionObjectRelationshipsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectRelationships");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectRelationshipsJSONObject.getLong(
				"totalCount"));

		assertContains(
			objectRelationship1,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionObjectRelationshipsJSONObject.getString(
						"items"))));
		assertContains(
			objectRelationship2,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionObjectRelationshipsJSONObject.getString(
						"items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionObjectRelationshipsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionObjectRelationships");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectRelationshipsJSONObject.getLong(
				"totalCount"));

		assertContains(
			objectRelationship1,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionObjectRelationshipsJSONObject.getString(
						"items"))));
		assertContains(
			objectRelationship2,
			Arrays.asList(
				ObjectRelationshipSerDes.toDTOs(
					objectDefinitionObjectRelationshipsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetObjectRelationship() throws Exception {
		ObjectRelationship postObjectRelationship =
			testGetObjectRelationship_addObjectRelationship();

		ObjectRelationship getObjectRelationship =
			objectRelationshipResource.getObjectRelationship(
				postObjectRelationship.getId());

		assertEquals(postObjectRelationship, getObjectRelationship);
		assertValid(getObjectRelationship);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectRelationship postObjectRelationship =
			testGetObjectRelationship_addObjectRelationship();

		ObjectRelationship getObjectRelationship =
			objectRelationshipResource.getObjectRelationship(
				postObjectRelationship.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship"
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
			postObjectRelationship.getId());

		assertEquals(
			getObjectRelationship,
			ObjectRelationshipSerDes.toDTO(item.toString()));
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

	protected ObjectRelationship
			testGetObjectRelationship_addObjectRelationship()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectRelationship() throws Exception {
		ObjectRelationship objectRelationship =
			testGraphQLGetObjectRelationship_addObjectRelationship();

		// No namespace

		Assert.assertTrue(
			equals(
				objectRelationship,
				ObjectRelationshipSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectRelationship",
								new HashMap<String, Object>() {
									{
										put(
											"objectRelationshipId",
											objectRelationship.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectRelationship"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectRelationship,
				ObjectRelationshipSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectRelationship",
									new HashMap<String, Object>() {
										{
											put(
												"objectRelationshipId",
												objectRelationship.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectRelationship"))));
	}

	@Test
	public void testGraphQLGetObjectRelationshipNotFound() throws Exception {
		Long irrelevantObjectRelationshipId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectRelationship",
						new HashMap<String, Object>() {
							{
								put(
									"objectRelationshipId",
									irrelevantObjectRelationshipId);
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
							"objectRelationship",
							new HashMap<String, Object>() {
								{
									put(
										"objectRelationshipId",
										irrelevantObjectRelationshipId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectRelationship
			testGraphQLGetObjectRelationship_addObjectRelationship()
		throws Exception {

		return testGraphQLObjectRelationship_addObjectRelationship();
	}

	@Test
	public void testPostObjectDefinitionByExternalReferenceCodeObjectRelationship()
		throws Exception {

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship postObjectRelationship =
			testPostObjectDefinitionByExternalReferenceCodeObjectRelationship_addObjectRelationship(
				randomObjectRelationship);

		assertEquals(randomObjectRelationship, postObjectRelationship);
		assertValid(postObjectRelationship);
	}

	protected ObjectRelationship
			testPostObjectDefinitionByExternalReferenceCodeObjectRelationship_addObjectRelationship(
				ObjectRelationship objectRelationship)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectRelationship()
		throws Exception {

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship objectRelationship =
			testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship(
				testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectRelationship_getObjectDefinitionId(),
				randomObjectRelationship);

		Assert.assertTrue(equals(randomObjectRelationship, objectRelationship));
	}

	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectRelationship_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionObjectRelationship() throws Exception {
		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship postObjectRelationship =
			testPostObjectDefinitionObjectRelationship_addObjectRelationship(
				randomObjectRelationship);

		assertEquals(randomObjectRelationship, postObjectRelationship);
		assertValid(postObjectRelationship);
	}

	protected ObjectRelationship
			testPostObjectDefinitionObjectRelationship_addObjectRelationship(
				ObjectRelationship objectRelationship)
		throws Exception {

		return objectRelationshipResource.
			postObjectDefinitionObjectRelationship(
				testGetObjectDefinitionObjectRelationshipsPage_getObjectDefinitionId(),
				objectRelationship);
	}

	@Test
	public void testGraphQLPostObjectDefinitionObjectRelationship()
		throws Exception {

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship objectRelationship =
			testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship(
				testGraphQLPostObjectDefinitionObjectRelationship_getObjectDefinitionId(),
				randomObjectRelationship);

		Assert.assertTrue(equals(randomObjectRelationship, objectRelationship));
	}

	protected Long
			testGraphQLPostObjectDefinitionObjectRelationship_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectRelationship() throws Exception {
		ObjectRelationship postObjectRelationship =
			testPutObjectRelationship_addObjectRelationship();

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship putObjectRelationship =
			objectRelationshipResource.putObjectRelationship(
				postObjectRelationship.getId(), randomObjectRelationship);

		assertEquals(randomObjectRelationship, putObjectRelationship);
		assertValid(putObjectRelationship);

		ObjectRelationship getObjectRelationship =
			objectRelationshipResource.getObjectRelationship(
				putObjectRelationship.getId());

		assertEquals(randomObjectRelationship, getObjectRelationship);
		assertValid(getObjectRelationship);
	}

	protected ObjectRelationship
			testPutObjectRelationship_addObjectRelationship()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectRelationshipByExternalReferenceCode()
		throws Exception {

		ObjectRelationship postObjectRelationship =
			testPutObjectRelationshipByExternalReferenceCode_addObjectRelationship();

		ObjectRelationship randomObjectRelationship =
			randomObjectRelationship();

		ObjectRelationship putObjectRelationship =
			objectRelationshipResource.
				putObjectRelationshipByExternalReferenceCode(
					postObjectRelationship.getExternalReferenceCode(),
					randomObjectRelationship);

		assertEquals(randomObjectRelationship, putObjectRelationship);
		assertValid(putObjectRelationship);

		ObjectRelationship getObjectRelationship =
			testPutObjectRelationshipByExternalReferenceCode_getObjectRelationship(
				putObjectRelationship.getExternalReferenceCode());

		assertEquals(randomObjectRelationship, getObjectRelationship);
		assertValid(getObjectRelationship);

		ObjectRelationship newObjectRelationship =
			testPutObjectRelationshipByExternalReferenceCode_createObjectRelationship();

		putObjectRelationship =
			objectRelationshipResource.
				putObjectRelationshipByExternalReferenceCode(
					newObjectRelationship.getExternalReferenceCode(),
					newObjectRelationship);

		assertEquals(newObjectRelationship, putObjectRelationship);
		assertValid(putObjectRelationship);

		getObjectRelationship =
			testPutObjectRelationshipByExternalReferenceCode_getObjectRelationship(
				putObjectRelationship.getExternalReferenceCode());

		assertEquals(newObjectRelationship, getObjectRelationship);

		Assert.assertEquals(
			newObjectRelationship.getExternalReferenceCode(),
			putObjectRelationship.getExternalReferenceCode());
	}

	protected ObjectRelationship
		testPutObjectRelationshipByExternalReferenceCode_getObjectRelationship(
			String externalReferenceCode) {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectRelationship
			testPutObjectRelationshipByExternalReferenceCode_addObjectRelationship()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectRelationship
			testPutObjectRelationshipByExternalReferenceCode_createObjectRelationship()
		throws Exception {

		return randomObjectRelationship();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectRelationship objectRelationship1 =
			testBatchEngineDeleteImportTask_addObjectRelationship();

		testBatchEngineDeleteImportTask_deleteObjectRelationship(
			200, null, objectRelationship1.getId());

		assertHttpResponseStatusCode(
			404,
			objectRelationshipResource.getObjectRelationshipHttpResponse(
				objectRelationship1.getId()));
	}

	protected ObjectRelationship
			testBatchEngineDeleteImportTask_addObjectRelationship()
		throws Exception {

		return testDeleteObjectRelationship_addObjectRelationship();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectRelationship(
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
				"com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship",
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected ObjectRelationship
			testGraphQLObjectRelationship_addObjectRelationship()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectRelationship
			testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship()
		throws Exception {

		return testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship(
			testGraphQLObjectDefinitionObjectRelationship_getObjectDefinitionId(),
			randomObjectRelationship());
	}

	protected Long
			testGraphQLObjectDefinitionObjectRelationship_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectRelationship
			testGraphQLObjectDefinitionObjectRelationship_addObjectRelationship(
				Long objectDefinitionId, ObjectRelationship objectRelationship)
		throws Exception {

		JSONDeserializer<ObjectRelationship> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectRelationship.class)) {

			if (getGraphQLValue(field.get(objectRelationship)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectRelationship)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectDefinitionObjectRelationship",
						new HashMap<String, Object>() {
							{
								put("objectDefinitionId", objectDefinitionId);
								put("objectRelationship", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createObjectDefinitionObjectRelationship"),
			ObjectRelationship.class);
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
		ObjectRelationship objectRelationship,
		List<ObjectRelationship> objectRelationships) {

		boolean contains = false;

		for (ObjectRelationship item : objectRelationships) {
			if (equals(objectRelationship, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectRelationships + " does not contain " + objectRelationship,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectRelationship objectRelationship1,
		ObjectRelationship objectRelationship2) {

		Assert.assertTrue(
			objectRelationship1 + " does not equal " + objectRelationship2,
			equals(objectRelationship1, objectRelationship2));
	}

	protected void assertEquals(
		List<ObjectRelationship> objectRelationships1,
		List<ObjectRelationship> objectRelationships2) {

		Assert.assertEquals(
			objectRelationships1.size(), objectRelationships2.size());

		for (int i = 0; i < objectRelationships1.size(); i++) {
			ObjectRelationship objectRelationship1 = objectRelationships1.get(
				i);
			ObjectRelationship objectRelationship2 = objectRelationships2.get(
				i);

			assertEquals(objectRelationship1, objectRelationship2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectRelationship> objectRelationships1,
		List<ObjectRelationship> objectRelationships2) {

		Assert.assertEquals(
			objectRelationships1.size(), objectRelationships2.size());

		for (ObjectRelationship objectRelationship1 : objectRelationships1) {
			boolean contains = false;

			for (ObjectRelationship objectRelationship2 :
					objectRelationships2) {

				if (equals(objectRelationship1, objectRelationship2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectRelationships2 + " does not contain " +
					objectRelationship1,
				contains);
		}
	}

	protected void assertValid(ObjectRelationship objectRelationship)
		throws Exception {

		boolean valid = true;

		if (objectRelationship.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectRelationship.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("deletionType", additionalAssertFieldName)) {
				if (objectRelationship.getDeletionType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("edge", additionalAssertFieldName)) {
				if (objectRelationship.getEdge() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (objectRelationship.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (objectRelationship.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectRelationship.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode1",
					additionalAssertFieldName)) {

				if (objectRelationship.
						getObjectDefinitionExternalReferenceCode1() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode2",
					additionalAssertFieldName)) {

				if (objectRelationship.
						getObjectDefinitionExternalReferenceCode2() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId1", additionalAssertFieldName)) {

				if (objectRelationship.getObjectDefinitionId1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId2", additionalAssertFieldName)) {

				if (objectRelationship.getObjectDefinitionId2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionModifiable2", additionalAssertFieldName)) {

				if (objectRelationship.getObjectDefinitionModifiable2() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionName2", additionalAssertFieldName)) {

				if (objectRelationship.getObjectDefinitionName2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionScope2", additionalAssertFieldName)) {

				if (objectRelationship.getObjectDefinitionScope2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionSystem2", additionalAssertFieldName)) {

				if (objectRelationship.getObjectDefinitionSystem2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectField", additionalAssertFieldName)) {
				if (objectRelationship.getObjectField() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parameterObjectFieldId", additionalAssertFieldName)) {

				if (objectRelationship.getParameterObjectFieldId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parameterObjectFieldName", additionalAssertFieldName)) {

				if (objectRelationship.getParameterObjectFieldName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("reverse", additionalAssertFieldName)) {
				if (objectRelationship.getReverse() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (objectRelationship.getSystem() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (objectRelationship.getType() == null) {
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

	protected void assertValid(Page<ObjectRelationship> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectRelationship> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectRelationship> objectRelationships =
			page.getItems();

		int size = objectRelationships.size();

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
					com.liferay.object.admin.rest.dto.v1_0.ObjectRelationship.
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
		ObjectRelationship objectRelationship1,
		ObjectRelationship objectRelationship2) {

		if (objectRelationship1 == objectRelationship2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectRelationship1.getActions(),
						(Map)objectRelationship2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("deletionType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getDeletionType(),
						objectRelationship2.getDeletionType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("edge", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getEdge(),
						objectRelationship2.getEdge())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getExternalReferenceCode(),
						objectRelationship2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getId(),
						objectRelationship2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectRelationship1.getLabel(),
						(Map)objectRelationship2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getName(),
						objectRelationship2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode1",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.
							getObjectDefinitionExternalReferenceCode1(),
						objectRelationship2.
							getObjectDefinitionExternalReferenceCode1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode2",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.
							getObjectDefinitionExternalReferenceCode2(),
						objectRelationship2.
							getObjectDefinitionExternalReferenceCode2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId1", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getObjectDefinitionId1(),
						objectRelationship2.getObjectDefinitionId1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId2", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getObjectDefinitionId2(),
						objectRelationship2.getObjectDefinitionId2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionModifiable2", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getObjectDefinitionModifiable2(),
						objectRelationship2.getObjectDefinitionModifiable2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionName2", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getObjectDefinitionName2(),
						objectRelationship2.getObjectDefinitionName2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionScope2", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getObjectDefinitionScope2(),
						objectRelationship2.getObjectDefinitionScope2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionSystem2", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getObjectDefinitionSystem2(),
						objectRelationship2.getObjectDefinitionSystem2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectField", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getObjectField(),
						objectRelationship2.getObjectField())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parameterObjectFieldId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getParameterObjectFieldId(),
						objectRelationship2.getParameterObjectFieldId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parameterObjectFieldName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectRelationship1.getParameterObjectFieldName(),
						objectRelationship2.getParameterObjectFieldName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("reverse", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getReverse(),
						objectRelationship2.getReverse())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("system", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getSystem(),
						objectRelationship2.getSystem())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectRelationship1.getType(),
						objectRelationship2.getType())) {

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

		if (!(_objectRelationshipResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectRelationshipResource;

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
		ObjectRelationship objectRelationship) {

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

		if (entityFieldName.equals("deletionType")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("edge")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = objectRelationship.getExternalReferenceCode();

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

		if (entityFieldName.equals("name")) {
			Object object = objectRelationship.getName();

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
				objectRelationship.getObjectDefinitionExternalReferenceCode1();

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

		if (entityFieldName.equals("objectDefinitionExternalReferenceCode2")) {
			Object object =
				objectRelationship.getObjectDefinitionExternalReferenceCode2();

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

		if (entityFieldName.equals("objectDefinitionId1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionId2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionModifiable2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectDefinitionName2")) {
			Object object = objectRelationship.getObjectDefinitionName2();

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

		if (entityFieldName.equals("objectDefinitionScope2")) {
			Object object = objectRelationship.getObjectDefinitionScope2();

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

		if (entityFieldName.equals("objectDefinitionSystem2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectField")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parameterObjectFieldId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parameterObjectFieldName")) {
			Object object = objectRelationship.getParameterObjectFieldName();

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

		if (entityFieldName.equals("reverse")) {
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

	protected ObjectRelationship randomObjectRelationship() throws Exception {
		return new ObjectRelationship() {
			{
				edge = RandomTestUtil.randomBoolean();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				objectDefinitionExternalReferenceCode1 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionExternalReferenceCode2 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionId1 = RandomTestUtil.randomLong();
				objectDefinitionId2 = RandomTestUtil.randomLong();
				objectDefinitionModifiable2 = RandomTestUtil.randomBoolean();
				objectDefinitionName2 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionScope2 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionSystem2 = RandomTestUtil.randomBoolean();
				parameterObjectFieldId = RandomTestUtil.randomLong();
				parameterObjectFieldName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				reverse = RandomTestUtil.randomBoolean();
				system = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ObjectRelationship randomIrrelevantObjectRelationship()
		throws Exception {

		ObjectRelationship randomIrrelevantObjectRelationship =
			randomObjectRelationship();

		return randomIrrelevantObjectRelationship;
	}

	protected ObjectRelationship randomPatchObjectRelationship()
		throws Exception {

		return randomObjectRelationship();
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

	protected ObjectRelationshipResource objectRelationshipResource;
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
		LogFactoryUtil.getLog(BaseObjectRelationshipResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.object.admin.rest.resource.v1_0.ObjectRelationshipResource
			_objectRelationshipResource;

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