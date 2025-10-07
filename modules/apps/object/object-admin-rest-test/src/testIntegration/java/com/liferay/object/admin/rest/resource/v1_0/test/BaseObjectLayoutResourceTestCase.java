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
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectLayout;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectLayoutResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectLayoutSerDes;
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
public abstract class BaseObjectLayoutResourceTestCase {

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

		_objectLayoutResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectLayoutResource = ObjectLayoutResource.builder(
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

		ObjectLayout objectLayout1 = randomObjectLayout();

		String json = objectMapper.writeValueAsString(objectLayout1);

		ObjectLayout objectLayout2 = ObjectLayoutSerDes.toDTO(json);

		Assert.assertTrue(equals(objectLayout1, objectLayout2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectLayout objectLayout = randomObjectLayout();

		String json1 = objectMapper.writeValueAsString(objectLayout);
		String json2 = ObjectLayoutSerDes.toJSON(objectLayout);

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

		ObjectLayout objectLayout = randomObjectLayout();

		objectLayout.setObjectDefinitionExternalReferenceCode(regex);

		String json = ObjectLayoutSerDes.toJSON(objectLayout);

		Assert.assertFalse(json.contains(regex));

		objectLayout = ObjectLayoutSerDes.toDTO(json);

		Assert.assertEquals(
			regex, objectLayout.getObjectDefinitionExternalReferenceCode());
	}

	@Test
	public void testDeleteObjectLayout() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectLayout objectLayout = testDeleteObjectLayout_addObjectLayout();

		assertHttpResponseStatusCode(
			204,
			objectLayoutResource.deleteObjectLayoutHttpResponse(
				objectLayout.getId()));

		assertHttpResponseStatusCode(
			404,
			objectLayoutResource.getObjectLayoutHttpResponse(
				objectLayout.getId()));
		assertHttpResponseStatusCode(
			404, objectLayoutResource.getObjectLayoutHttpResponse(0L));
	}

	protected ObjectLayout testDeleteObjectLayout_addObjectLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectLayout() throws Exception {

		// No namespace

		ObjectLayout objectLayout1 =
			testGraphQLDeleteObjectLayout_addObjectLayout();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectLayout",
						new HashMap<String, Object>() {
							{
								put("objectLayoutId", objectLayout1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectLayout"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectLayout",
					new HashMap<String, Object>() {
						{
							put("objectLayoutId", objectLayout1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectLayout objectLayout2 =
			testGraphQLDeleteObjectLayout_addObjectLayout();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectLayout",
							new HashMap<String, Object>() {
								{
									put(
										"objectLayoutId",
										objectLayout2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectLayout"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectLayout",
						new HashMap<String, Object>() {
							{
								put("objectLayoutId", objectLayout2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectLayout testGraphQLDeleteObjectLayout_addObjectLayout()
		throws Exception {

		return testGraphQLObjectLayout_addObjectLayout();
	}

	@Test
	public void testDeleteObjectLayoutBatch() throws Exception {
		ObjectLayout objectLayout1 =
			testDeleteObjectLayoutBatch_addObjectLayout();

		testDeleteObjectLayoutBatch_deleteObjectLayout(
			202, null, objectLayout1.getId());

		assertHttpResponseStatusCode(
			404,
			objectLayoutResource.getObjectLayoutHttpResponse(
				objectLayout1.getId()));
	}

	protected ObjectLayout testDeleteObjectLayoutBatch_addObjectLayout()
		throws Exception {

		return testDeleteObjectLayout_addObjectLayout();
	}

	protected void testDeleteObjectLayoutBatch_deleteObjectLayout(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectLayoutResource.deleteObjectLayoutBatchHttpResponse(
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
	public void testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getIrrelevantExternalReferenceCode();

		Page<ObjectLayout> page =
			objectLayoutResource.
				getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ObjectLayout irrelevantObjectLayout =
				testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
					irrelevantExternalReferenceCode,
					randomIrrelevantObjectLayout());

			page =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectLayout, (List<ObjectLayout>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ObjectLayout objectLayout1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		ObjectLayout objectLayout2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		page =
			objectLayoutResource.
				getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectLayout1, (List<ObjectLayout>)page.getItems());
		assertContains(objectLayout2, (List<ObjectLayout>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExpectedActions(
				externalReferenceCode));

		objectLayoutResource.deleteObjectLayout(objectLayout1.getId());

		objectLayoutResource.deleteObjectLayout(objectLayout2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExternalReferenceCode();

		Page<ObjectLayout> objectLayoutsPage =
			objectLayoutResource.
				getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectLayoutsPage.getTotalCount());

		ObjectLayout objectLayout1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		ObjectLayout objectLayout2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		ObjectLayout objectLayout3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectLayout> page1 =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectLayout1, (List<ObjectLayout>)page1.getItems());

			Page<ObjectLayout> page2 =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectLayout2, (List<ObjectLayout>)page2.getItems());

			Page<ObjectLayout> page3 =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectLayout3, (List<ObjectLayout>)page3.getItems());
		}
		else {
			Page<ObjectLayout> page1 =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectLayout> objectLayouts1 =
				(List<ObjectLayout>)page1.getItems();

			Assert.assertEquals(
				objectLayouts1.toString(), totalCount + 2,
				objectLayouts1.size());

			Page<ObjectLayout> page2 =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectLayout> objectLayouts2 =
				(List<ObjectLayout>)page2.getItems();

			Assert.assertEquals(
				objectLayouts2.toString(), 1, objectLayouts2.size());

			Page<ObjectLayout> page3 =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectLayout1, (List<ObjectLayout>)page3.getItems());
			assertContains(objectLayout2, (List<ObjectLayout>)page3.getItems());
			assertContains(objectLayout3, (List<ObjectLayout>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectLayout1, objectLayout2) -> {
				BeanTestUtil.setProperty(
					objectLayout1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectLayout1, objectLayout2) -> {
				BeanTestUtil.setProperty(
					objectLayout1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectLayout2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectLayout1, objectLayout2) -> {
				BeanTestUtil.setProperty(
					objectLayout1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectLayout2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectLayout1, objectLayout2) -> {
				Class<?> clazz = objectLayout1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectLayout1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectLayout2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectLayout1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectLayout2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectLayout1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectLayout2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ObjectLayout, ObjectLayout, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExternalReferenceCode();

		ObjectLayout objectLayout1 = randomObjectLayout();
		ObjectLayout objectLayout2 = randomObjectLayout();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectLayout1, objectLayout2);
		}

		objectLayout1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, objectLayout1);

		objectLayout2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				externalReferenceCode, objectLayout2);

		Page<ObjectLayout> page =
			objectLayoutResource.
				getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
					externalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectLayout> ascPage =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				objectLayout1, (List<ObjectLayout>)ascPage.getItems());
			assertContains(
				objectLayout2, (List<ObjectLayout>)ascPage.getItems());

			Page<ObjectLayout> descPage =
				objectLayoutResource.
					getObjectDefinitionByExternalReferenceCodeObjectLayoutsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				objectLayout2, (List<ObjectLayout>)descPage.getItems());
			assertContains(
				objectLayout1, (List<ObjectLayout>)descPage.getItems());
		}
	}

	protected ObjectLayout
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_addObjectLayout(
				String externalReferenceCode, ObjectLayout objectLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionByExternalReferenceCodeObjectLayouts",
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
			objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/objectDefinitionByExternalReferenceCodeObjectLayouts");

		long totalCount =
			objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
				getLong("totalCount");

		ObjectLayout objectLayout1 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageObjectDefinitionObjectLayout_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		ObjectLayout objectLayout2 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageObjectDefinitionObjectLayout_addObjectLayout(
				externalReferenceCode, randomObjectLayout());

		objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectLayouts");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectLayout1,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
						getString("items"))));
		assertContains(
			objectLayout2,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
						getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectLayouts");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectLayout1,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
						getString("items"))));
		assertContains(
			objectLayout2,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectLayoutsJSONObject.
						getString("items"))));
	}

	protected ObjectLayout
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectLayoutsPageObjectDefinitionObjectLayout_addObjectLayout(
				String externalReferenceCode, ObjectLayout objectLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionObjectLayoutsPage() throws Exception {
		Long objectDefinitionId =
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectLayoutsPage_getIrrelevantObjectDefinitionId();

		Page<ObjectLayout> page =
			objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
				objectDefinitionId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectLayout irrelevantObjectLayout =
				testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
					irrelevantObjectDefinitionId,
					randomIrrelevantObjectLayout());

			page = objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
				irrelevantObjectDefinitionId, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectLayout, (List<ObjectLayout>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionObjectLayoutsPage_getExpectedActions(
					irrelevantObjectDefinitionId));
		}

		ObjectLayout objectLayout1 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		ObjectLayout objectLayout2 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		page = objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
			objectDefinitionId, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectLayout1, (List<ObjectLayout>)page.getItems());
		assertContains(objectLayout2, (List<ObjectLayout>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionObjectLayoutsPage_getExpectedActions(
				objectDefinitionId));

		objectLayoutResource.deleteObjectLayout(objectLayout1.getId());

		objectLayoutResource.deleteObjectLayout(objectLayout2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionObjectLayoutsPage_getExpectedActions(
				Long objectDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-layouts/batch".
				replace(
					"{objectDefinitionId}",
					String.valueOf(objectDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionObjectLayoutsPageWithPagination()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId();

		Page<ObjectLayout> objectLayoutsPage =
			objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
				objectDefinitionId, null, null, null);

		int totalCount = GetterUtil.getInteger(
			objectLayoutsPage.getTotalCount());

		ObjectLayout objectLayout1 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		ObjectLayout objectLayout2 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		ObjectLayout objectLayout3 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectLayout> page1 =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectLayout1, (List<ObjectLayout>)page1.getItems());

			Page<ObjectLayout> page2 =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectLayout2, (List<ObjectLayout>)page2.getItems());

			Page<ObjectLayout> page3 =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectLayout3, (List<ObjectLayout>)page3.getItems());
		}
		else {
			Page<ObjectLayout> page1 =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null, Pagination.of(1, totalCount + 2),
					null);

			List<ObjectLayout> objectLayouts1 =
				(List<ObjectLayout>)page1.getItems();

			Assert.assertEquals(
				objectLayouts1.toString(), totalCount + 2,
				objectLayouts1.size());

			Page<ObjectLayout> page2 =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectLayout> objectLayouts2 =
				(List<ObjectLayout>)page2.getItems();

			Assert.assertEquals(
				objectLayouts2.toString(), 1, objectLayouts2.size());

			Page<ObjectLayout> page3 =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectLayout1, (List<ObjectLayout>)page3.getItems());
			assertContains(objectLayout2, (List<ObjectLayout>)page3.getItems());
			assertContains(objectLayout3, (List<ObjectLayout>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectLayoutsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionObjectLayoutsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectLayout1, objectLayout2) -> {
				BeanTestUtil.setProperty(
					objectLayout1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionObjectLayoutsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionObjectLayoutsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectLayout1, objectLayout2) -> {
				BeanTestUtil.setProperty(
					objectLayout1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectLayout2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectLayoutsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionObjectLayoutsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectLayout1, objectLayout2) -> {
				BeanTestUtil.setProperty(
					objectLayout1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					objectLayout2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectLayoutsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionObjectLayoutsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectLayout1, objectLayout2) -> {
				Class<?> clazz = objectLayout1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectLayout1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectLayout2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectLayout1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectLayout2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectLayout1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectLayout2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionObjectLayoutsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, ObjectLayout, ObjectLayout, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId();

		ObjectLayout objectLayout1 = randomObjectLayout();
		ObjectLayout objectLayout2 = randomObjectLayout();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectLayout1, objectLayout2);
		}

		objectLayout1 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, objectLayout1);

		objectLayout2 =
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				objectDefinitionId, objectLayout2);

		Page<ObjectLayout> page =
			objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
				objectDefinitionId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectLayout> ascPage =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				objectLayout1, (List<ObjectLayout>)ascPage.getItems());
			assertContains(
				objectLayout2, (List<ObjectLayout>)ascPage.getItems());

			Page<ObjectLayout> descPage =
				objectLayoutResource.getObjectDefinitionObjectLayoutsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				objectLayout2, (List<ObjectLayout>)descPage.getItems());
			assertContains(
				objectLayout1, (List<ObjectLayout>)descPage.getItems());
		}
	}

	protected ObjectLayout
			testGetObjectDefinitionObjectLayoutsPage_addObjectLayout(
				Long objectDefinitionId, ObjectLayout objectLayout)
		throws Exception {

		return objectLayoutResource.postObjectDefinitionObjectLayout(
			objectDefinitionId, objectLayout);
	}

	protected Long
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectDefinitionObjectLayoutsPage_getIrrelevantObjectDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionObjectLayoutsPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionObjectLayouts",
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

		JSONObject objectDefinitionObjectLayoutsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectLayouts");

		long totalCount = objectDefinitionObjectLayoutsJSONObject.getLong(
			"totalCount");

		ObjectLayout objectLayout1 =
			testGraphQLObjectDefinitionObjectLayout_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		ObjectLayout objectLayout2 =
			testGraphQLObjectDefinitionObjectLayout_addObjectLayout(
				objectDefinitionId, randomObjectLayout());

		objectDefinitionObjectLayoutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitionObjectLayouts");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectLayoutsJSONObject.getLong("totalCount"));

		assertContains(
			objectLayout1,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionObjectLayoutsJSONObject.getString(
						"items"))));
		assertContains(
			objectLayout2,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionObjectLayoutsJSONObject.getString(
						"items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionObjectLayoutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("objectAdmin_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/objectAdmin_v1_0",
			"JSONObject/objectDefinitionObjectLayouts");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectLayoutsJSONObject.getLong("totalCount"));

		assertContains(
			objectLayout1,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionObjectLayoutsJSONObject.getString(
						"items"))));
		assertContains(
			objectLayout2,
			Arrays.asList(
				ObjectLayoutSerDes.toDTOs(
					objectDefinitionObjectLayoutsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetObjectLayout() throws Exception {
		ObjectLayout postObjectLayout = testGetObjectLayout_addObjectLayout();

		ObjectLayout getObjectLayout = objectLayoutResource.getObjectLayout(
			postObjectLayout.getId());

		assertEquals(postObjectLayout, getObjectLayout);
		assertValid(getObjectLayout);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectLayout postObjectLayout = testGetObjectLayout_addObjectLayout();

		ObjectLayout getObjectLayout = objectLayoutResource.getObjectLayout(
			postObjectLayout.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.object.admin.rest.dto.v1_0.ObjectLayout"
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

		Object item = vulcanCRUDItemDelegate.getItem(postObjectLayout.getId());

		assertEquals(
			getObjectLayout, ObjectLayoutSerDes.toDTO(item.toString()));
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

	protected ObjectLayout testGetObjectLayout_addObjectLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectLayout() throws Exception {
		ObjectLayout objectLayout =
			testGraphQLGetObjectLayout_addObjectLayout();

		// No namespace

		Assert.assertTrue(
			equals(
				objectLayout,
				ObjectLayoutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectLayout",
								new HashMap<String, Object>() {
									{
										put(
											"objectLayoutId",
											objectLayout.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectLayout"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectLayout,
				ObjectLayoutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectLayout",
									new HashMap<String, Object>() {
										{
											put(
												"objectLayoutId",
												objectLayout.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectLayout"))));
	}

	@Test
	public void testGraphQLGetObjectLayoutNotFound() throws Exception {
		Long irrelevantObjectLayoutId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectLayout",
						new HashMap<String, Object>() {
							{
								put("objectLayoutId", irrelevantObjectLayoutId);
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
							"objectLayout",
							new HashMap<String, Object>() {
								{
									put(
										"objectLayoutId",
										irrelevantObjectLayoutId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectLayout testGraphQLGetObjectLayout_addObjectLayout()
		throws Exception {

		return testGraphQLObjectLayout_addObjectLayout();
	}

	@Test
	public void testPostObjectDefinitionByExternalReferenceCodeObjectLayout()
		throws Exception {

		ObjectLayout randomObjectLayout = randomObjectLayout();

		ObjectLayout postObjectLayout =
			testPostObjectDefinitionByExternalReferenceCodeObjectLayout_addObjectLayout(
				randomObjectLayout);

		assertEquals(randomObjectLayout, postObjectLayout);
		assertValid(postObjectLayout);
	}

	protected ObjectLayout
			testPostObjectDefinitionByExternalReferenceCodeObjectLayout_addObjectLayout(
				ObjectLayout objectLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectLayout()
		throws Exception {

		ObjectLayout randomObjectLayout = randomObjectLayout();

		ObjectLayout objectLayout =
			testGraphQLObjectDefinitionObjectLayout_addObjectLayout(
				testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectLayout_getObjectDefinitionId(
					randomObjectLayout),
				randomObjectLayout);

		Assert.assertTrue(equals(randomObjectLayout, objectLayout));
	}

	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectLayout_getObjectDefinitionId(
				ObjectLayout objectLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionObjectLayout() throws Exception {
		ObjectLayout randomObjectLayout = randomObjectLayout();

		ObjectLayout postObjectLayout =
			testPostObjectDefinitionObjectLayout_addObjectLayout(
				randomObjectLayout);

		assertEquals(randomObjectLayout, postObjectLayout);
		assertValid(postObjectLayout);
	}

	protected ObjectLayout testPostObjectDefinitionObjectLayout_addObjectLayout(
			ObjectLayout objectLayout)
		throws Exception {

		return objectLayoutResource.postObjectDefinitionObjectLayout(
			testGetObjectDefinitionObjectLayoutsPage_getObjectDefinitionId(),
			objectLayout);
	}

	@Test
	public void testGraphQLPostObjectDefinitionObjectLayout() throws Exception {
		ObjectLayout randomObjectLayout = randomObjectLayout();

		ObjectLayout objectLayout =
			testGraphQLObjectDefinitionObjectLayout_addObjectLayout(
				testGraphQLPostObjectDefinitionObjectLayout_getObjectDefinitionId(
					randomObjectLayout),
				randomObjectLayout);

		Assert.assertTrue(equals(randomObjectLayout, objectLayout));
	}

	protected Long
			testGraphQLPostObjectDefinitionObjectLayout_getObjectDefinitionId(
				ObjectLayout objectLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectLayout() throws Exception {
		ObjectLayout postObjectLayout = testPutObjectLayout_addObjectLayout();

		ObjectLayout randomObjectLayout = randomObjectLayout();

		ObjectLayout putObjectLayout = objectLayoutResource.putObjectLayout(
			postObjectLayout.getId(), randomObjectLayout);

		assertEquals(randomObjectLayout, putObjectLayout);
		assertValid(putObjectLayout);

		ObjectLayout getObjectLayout = objectLayoutResource.getObjectLayout(
			putObjectLayout.getId());

		assertEquals(randomObjectLayout, getObjectLayout);
		assertValid(getObjectLayout);
	}

	protected ObjectLayout testPutObjectLayout_addObjectLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectLayout objectLayout1 =
			testBatchEngineDeleteImportTask_addObjectLayout();

		testBatchEngineDeleteImportTask_deleteObjectLayout(
			200, null, objectLayout1.getId());

		assertHttpResponseStatusCode(
			404,
			objectLayoutResource.getObjectLayoutHttpResponse(
				objectLayout1.getId()));
	}

	protected ObjectLayout testBatchEngineDeleteImportTask_addObjectLayout()
		throws Exception {

		return testDeleteObjectLayout_addObjectLayout();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectLayout(
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
				"com.liferay.object.admin.rest.dto.v1_0.ObjectLayout", null,
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

	protected ObjectLayout testGraphQLObjectLayout_addObjectLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectLayout
			testGraphQLObjectDefinitionObjectLayout_addObjectLayout()
		throws Exception {

		return testGraphQLObjectDefinitionObjectLayout_addObjectLayout(
			testGraphQLObjectDefinitionObjectLayout_getObjectDefinitionId(),
			randomObjectLayout());
	}

	protected Long
			testGraphQLObjectDefinitionObjectLayout_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectLayout
			testGraphQLObjectDefinitionObjectLayout_addObjectLayout(
				Long objectDefinitionId, ObjectLayout objectLayout)
		throws Exception {

		JSONDeserializer<ObjectLayout> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectLayout.class)) {

			if (getGraphQLValue(field.get(objectLayout)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectLayout)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectDefinitionObjectLayout",
						new HashMap<String, Object>() {
							{
								put("objectDefinitionId", objectDefinitionId);
								put("objectLayout", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createObjectDefinitionObjectLayout"),
			ObjectLayout.class);
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
		ObjectLayout objectLayout, List<ObjectLayout> objectLayouts) {

		boolean contains = false;

		for (ObjectLayout item : objectLayouts) {
			if (equals(objectLayout, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectLayouts + " does not contain " + objectLayout, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectLayout objectLayout1, ObjectLayout objectLayout2) {

		Assert.assertTrue(
			objectLayout1 + " does not equal " + objectLayout2,
			equals(objectLayout1, objectLayout2));
	}

	protected void assertEquals(
		List<ObjectLayout> objectLayouts1, List<ObjectLayout> objectLayouts2) {

		Assert.assertEquals(objectLayouts1.size(), objectLayouts2.size());

		for (int i = 0; i < objectLayouts1.size(); i++) {
			ObjectLayout objectLayout1 = objectLayouts1.get(i);
			ObjectLayout objectLayout2 = objectLayouts2.get(i);

			assertEquals(objectLayout1, objectLayout2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectLayout> objectLayouts1, List<ObjectLayout> objectLayouts2) {

		Assert.assertEquals(objectLayouts1.size(), objectLayouts2.size());

		for (ObjectLayout objectLayout1 : objectLayouts1) {
			boolean contains = false;

			for (ObjectLayout objectLayout2 : objectLayouts2) {
				if (equals(objectLayout1, objectLayout2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectLayouts2 + " does not contain " + objectLayout1,
				contains);
		}
	}

	protected void assertValid(ObjectLayout objectLayout) throws Exception {
		boolean valid = true;

		if (objectLayout.getDateCreated() == null) {
			valid = false;
		}

		if (objectLayout.getDateModified() == null) {
			valid = false;
		}

		if (objectLayout.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectLayout.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultObjectLayout", additionalAssertFieldName)) {

				if (objectLayout.getDefaultObjectLayout() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectLayout.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectLayout.getObjectDefinitionExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (objectLayout.getObjectDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("objectLayoutTabs", additionalAssertFieldName)) {
				if (objectLayout.getObjectLayoutTabs() == null) {
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

	protected void assertValid(Page<ObjectLayout> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectLayout> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectLayout> objectLayouts = page.getItems();

		int size = objectLayouts.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.object.admin.rest.dto.v1_0.ObjectLayout.
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
		ObjectLayout objectLayout1, ObjectLayout objectLayout2) {

		if (objectLayout1 == objectLayout2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectLayout1.getActions(),
						(Map)objectLayout2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectLayout1.getDateCreated(),
						objectLayout2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectLayout1.getDateModified(),
						objectLayout2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultObjectLayout", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectLayout1.getDefaultObjectLayout(),
						objectLayout2.getDefaultObjectLayout())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectLayout1.getId(), objectLayout2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectLayout1.getName(),
						(Map)objectLayout2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectLayout1.
							getObjectDefinitionExternalReferenceCode(),
						objectLayout2.
							getObjectDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectLayout1.getObjectDefinitionId(),
						objectLayout2.getObjectDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("objectLayoutTabs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectLayout1.getObjectLayoutTabs(),
						objectLayout2.getObjectLayoutTabs())) {

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

		if (!(_objectLayoutResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectLayoutResource;

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
		EntityField entityField, String operator, ObjectLayout objectLayout) {

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
				Date date = objectLayout.getDateCreated();

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

				sb.append(_format.format(objectLayout.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectLayout.getDateModified();

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

				sb.append(_format.format(objectLayout.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("defaultObjectLayout")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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
				objectLayout.getObjectDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("objectLayoutTabs")) {
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

	protected ObjectLayout randomObjectLayout() throws Exception {
		return new ObjectLayout() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultObjectLayout = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				objectDefinitionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ObjectLayout randomIrrelevantObjectLayout() throws Exception {
		ObjectLayout randomIrrelevantObjectLayout = randomObjectLayout();

		return randomIrrelevantObjectLayout;
	}

	protected ObjectLayout randomPatchObjectLayout() throws Exception {
		return randomObjectLayout();
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

	protected ObjectLayoutResource objectLayoutResource;
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
		LogFactoryUtil.getLog(BaseObjectLayoutResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.object.admin.rest.resource.v1_0.ObjectLayoutResource
		_objectLayoutResource;

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