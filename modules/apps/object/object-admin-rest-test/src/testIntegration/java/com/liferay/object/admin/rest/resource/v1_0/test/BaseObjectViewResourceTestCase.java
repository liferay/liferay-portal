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
import com.liferay.object.admin.rest.client.dto.v1_0.ObjectView;
import com.liferay.object.admin.rest.client.http.HttpInvoker;
import com.liferay.object.admin.rest.client.pagination.Page;
import com.liferay.object.admin.rest.client.pagination.Pagination;
import com.liferay.object.admin.rest.client.resource.v1_0.ObjectViewResource;
import com.liferay.object.admin.rest.client.serdes.v1_0.ObjectViewSerDes;
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
public abstract class BaseObjectViewResourceTestCase {

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

		_objectViewResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		objectViewResource = ObjectViewResource.builder(
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

		ObjectView objectView1 = randomObjectView();

		String json = objectMapper.writeValueAsString(objectView1);

		ObjectView objectView2 = ObjectViewSerDes.toDTO(json);

		Assert.assertTrue(equals(objectView1, objectView2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ObjectView objectView = randomObjectView();

		String json1 = objectMapper.writeValueAsString(objectView);
		String json2 = ObjectViewSerDes.toJSON(objectView);

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

		ObjectView objectView = randomObjectView();

		objectView.setObjectDefinitionExternalReferenceCode(regex);

		String json = ObjectViewSerDes.toJSON(objectView);

		Assert.assertFalse(json.contains(regex));

		objectView = ObjectViewSerDes.toDTO(json);

		Assert.assertEquals(
			regex, objectView.getObjectDefinitionExternalReferenceCode());
	}

	@Test
	public void testDeleteObjectView() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ObjectView objectView = testDeleteObjectView_addObjectView();

		assertHttpResponseStatusCode(
			204,
			objectViewResource.deleteObjectViewHttpResponse(
				objectView.getId()));

		assertHttpResponseStatusCode(
			404,
			objectViewResource.getObjectViewHttpResponse(objectView.getId()));
		assertHttpResponseStatusCode(
			404, objectViewResource.getObjectViewHttpResponse(0L));
	}

	protected ObjectView testDeleteObjectView_addObjectView() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteObjectView() throws Exception {

		// No namespace

		ObjectView objectView1 = testGraphQLDeleteObjectView_addObjectView();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteObjectView",
						new HashMap<String, Object>() {
							{
								put("objectViewId", objectView1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteObjectView"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectView",
					new HashMap<String, Object>() {
						{
							put("objectViewId", objectView1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace objectAdmin_v1_0

		ObjectView objectView2 = testGraphQLDeleteObjectView_addObjectView();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"objectAdmin_v1_0",
						new GraphQLField(
							"deleteObjectView",
							new HashMap<String, Object>() {
								{
									put("objectViewId", objectView2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"Object/deleteObjectView"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"objectAdmin_v1_0",
					new GraphQLField(
						"objectView",
						new HashMap<String, Object>() {
							{
								put("objectViewId", objectView2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ObjectView testGraphQLDeleteObjectView_addObjectView()
		throws Exception {

		return testGraphQLObjectView_addObjectView();
	}

	@Test
	public void testDeleteObjectViewBatch() throws Exception {
		ObjectView objectView1 = testDeleteObjectViewBatch_addObjectView();

		testDeleteObjectViewBatch_deleteObjectView(
			202, null, objectView1.getId());

		assertHttpResponseStatusCode(
			404,
			objectViewResource.getObjectViewHttpResponse(objectView1.getId()));
	}

	protected ObjectView testDeleteObjectViewBatch_addObjectView()
		throws Exception {

		return testDeleteObjectView_addObjectView();
	}

	protected void testDeleteObjectViewBatch_deleteObjectView(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			objectViewResource.deleteObjectViewBatchHttpResponse(
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
	public void testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getIrrelevantExternalReferenceCode();

		Page<ObjectView> page =
			objectViewResource.
				getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ObjectView irrelevantObjectView =
				testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
					irrelevantExternalReferenceCode,
					randomIrrelevantObjectView());

			page =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						irrelevantExternalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectView, (List<ObjectView>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ObjectView objectView1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, randomObjectView());

		ObjectView objectView2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, randomObjectView());

		page =
			objectViewResource.
				getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
					externalReferenceCode, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectView1, (List<ObjectView>)page.getItems());
		assertContains(objectView2, (List<ObjectView>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExpectedActions(
				externalReferenceCode));

		objectViewResource.deleteObjectView(objectView1.getId());

		objectViewResource.deleteObjectView(objectView2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExternalReferenceCode();

		Page<ObjectView> objectViewsPage =
			objectViewResource.
				getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
					externalReferenceCode, null, null, null);

		int totalCount = GetterUtil.getInteger(objectViewsPage.getTotalCount());

		ObjectView objectView1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, randomObjectView());

		ObjectView objectView2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, randomObjectView());

		ObjectView objectView3 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, randomObjectView());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectView> page1 =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectView1, (List<ObjectView>)page1.getItems());

			Page<ObjectView> page2 =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectView2, (List<ObjectView>)page2.getItems());

			Page<ObjectView> page3 =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(objectView3, (List<ObjectView>)page3.getItems());
		}
		else {
			Page<ObjectView> page1 =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(1, totalCount + 2), null);

			List<ObjectView> objectViews1 = (List<ObjectView>)page1.getItems();

			Assert.assertEquals(
				objectViews1.toString(), totalCount + 2, objectViews1.size());

			Page<ObjectView> page2 =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectView> objectViews2 = (List<ObjectView>)page2.getItems();

			Assert.assertEquals(
				objectViews2.toString(), 1, objectViews2.size());

			Page<ObjectView> page3 =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectView1, (List<ObjectView>)page3.getItems());
			assertContains(objectView2, (List<ObjectView>)page3.getItems());
			assertContains(objectView3, (List<ObjectView>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectView1, objectView2) -> {
				BeanTestUtil.setProperty(
					objectView1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectView1, objectView2) -> {
				BeanTestUtil.setProperty(
					objectView1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectView2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectView1, objectView2) -> {
				BeanTestUtil.setProperty(objectView1, entityField.getName(), 0);
				BeanTestUtil.setProperty(objectView2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectView1, objectView2) -> {
				Class<?> clazz = objectView1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectView1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectView2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectView1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectView2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectView1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectView2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ObjectView, ObjectView, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExternalReferenceCode();

		ObjectView objectView1 = randomObjectView();
		ObjectView objectView2 = randomObjectView();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectView1, objectView2);
		}

		objectView1 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, objectView1);

		objectView2 =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				externalReferenceCode, objectView2);

		Page<ObjectView> page =
			objectViewResource.
				getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
					externalReferenceCode, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectView> ascPage =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(objectView1, (List<ObjectView>)ascPage.getItems());
			assertContains(objectView2, (List<ObjectView>)ascPage.getItems());

			Page<ObjectView> descPage =
				objectViewResource.
					getObjectDefinitionByExternalReferenceCodeObjectViewsPage(
						externalReferenceCode, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(objectView2, (List<ObjectView>)descPage.getItems());
			assertContains(objectView1, (List<ObjectView>)descPage.getItems());
		}
	}

	protected ObjectView
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_addObjectView(
				String externalReferenceCode, ObjectView objectView)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectViewsPage()
		throws Exception {

		String externalReferenceCode =
			testGetObjectDefinitionByExternalReferenceCodeObjectViewsPage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionByExternalReferenceCodeObjectViews",
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
			objectDefinitionByExternalReferenceCodeObjectViewsJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/objectDefinitionByExternalReferenceCodeObjectViews");

		long totalCount =
			objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
				getLong("totalCount");

		ObjectView objectView1 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectViewsPageObjectDefinitionObjectView_addObjectView(
				externalReferenceCode, randomObjectView());

		ObjectView objectView2 =
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectViewsPageObjectDefinitionObjectView_addObjectView(
				externalReferenceCode, randomObjectView());

		objectDefinitionByExternalReferenceCodeObjectViewsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectViews");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectView1,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
						getString("items"))));
		assertContains(
			objectView2,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
						getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionByExternalReferenceCodeObjectViewsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("objectAdmin_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/objectAdmin_v1_0",
				"JSONObject/objectDefinitionByExternalReferenceCodeObjectViews");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
				getLong("totalCount"));

		assertContains(
			objectView1,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
						getString("items"))));
		assertContains(
			objectView2,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionByExternalReferenceCodeObjectViewsJSONObject.
						getString("items"))));
	}

	protected ObjectView
			testGraphQLGetObjectDefinitionByExternalReferenceCodeObjectViewsPageObjectDefinitionObjectView_addObjectView(
				String externalReferenceCode, ObjectView objectView)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetObjectDefinitionObjectViewsPage() throws Exception {
		Long objectDefinitionId =
			testGetObjectDefinitionObjectViewsPage_getObjectDefinitionId();
		Long irrelevantObjectDefinitionId =
			testGetObjectDefinitionObjectViewsPage_getIrrelevantObjectDefinitionId();

		Page<ObjectView> page =
			objectViewResource.getObjectDefinitionObjectViewsPage(
				objectDefinitionId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantObjectDefinitionId != null) {
			ObjectView irrelevantObjectView =
				testGetObjectDefinitionObjectViewsPage_addObjectView(
					irrelevantObjectDefinitionId, randomIrrelevantObjectView());

			page = objectViewResource.getObjectDefinitionObjectViewsPage(
				irrelevantObjectDefinitionId, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantObjectView, (List<ObjectView>)page.getItems());
			assertValid(
				page,
				testGetObjectDefinitionObjectViewsPage_getExpectedActions(
					irrelevantObjectDefinitionId));
		}

		ObjectView objectView1 =
			testGetObjectDefinitionObjectViewsPage_addObjectView(
				objectDefinitionId, randomObjectView());

		ObjectView objectView2 =
			testGetObjectDefinitionObjectViewsPage_addObjectView(
				objectDefinitionId, randomObjectView());

		page = objectViewResource.getObjectDefinitionObjectViewsPage(
			objectDefinitionId, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(objectView1, (List<ObjectView>)page.getItems());
		assertContains(objectView2, (List<ObjectView>)page.getItems());
		assertValid(
			page,
			testGetObjectDefinitionObjectViewsPage_getExpectedActions(
				objectDefinitionId));

		objectViewResource.deleteObjectView(objectView1.getId());

		objectViewResource.deleteObjectView(objectView2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetObjectDefinitionObjectViewsPage_getExpectedActions(
				Long objectDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/object-admin/v1.0/object-definitions/{objectDefinitionId}/object-views/batch".
				replace(
					"{objectDefinitionId}",
					String.valueOf(objectDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetObjectDefinitionObjectViewsPageWithPagination()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectViewsPage_getObjectDefinitionId();

		Page<ObjectView> objectViewsPage =
			objectViewResource.getObjectDefinitionObjectViewsPage(
				objectDefinitionId, null, null, null);

		int totalCount = GetterUtil.getInteger(objectViewsPage.getTotalCount());

		ObjectView objectView1 =
			testGetObjectDefinitionObjectViewsPage_addObjectView(
				objectDefinitionId, randomObjectView());

		ObjectView objectView2 =
			testGetObjectDefinitionObjectViewsPage_addObjectView(
				objectDefinitionId, randomObjectView());

		ObjectView objectView3 =
			testGetObjectDefinitionObjectViewsPage_addObjectView(
				objectDefinitionId, randomObjectView());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ObjectView> page1 =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(objectView1, (List<ObjectView>)page1.getItems());

			Page<ObjectView> page2 =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectView2, (List<ObjectView>)page2.getItems());

			Page<ObjectView> page3 =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(objectView3, (List<ObjectView>)page3.getItems());
		}
		else {
			Page<ObjectView> page1 =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null, Pagination.of(1, totalCount + 2),
					null);

			List<ObjectView> objectViews1 = (List<ObjectView>)page1.getItems();

			Assert.assertEquals(
				objectViews1.toString(), totalCount + 2, objectViews1.size());

			Page<ObjectView> page2 =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ObjectView> objectViews2 = (List<ObjectView>)page2.getItems();

			Assert.assertEquals(
				objectViews2.toString(), 1, objectViews2.size());

			Page<ObjectView> page3 =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(objectView1, (List<ObjectView>)page3.getItems());
			assertContains(objectView2, (List<ObjectView>)page3.getItems());
			assertContains(objectView3, (List<ObjectView>)page3.getItems());
		}
	}

	@Test
	public void testGetObjectDefinitionObjectViewsPageWithSortDateTime()
		throws Exception {

		testGetObjectDefinitionObjectViewsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, objectView1, objectView2) -> {
				BeanTestUtil.setProperty(
					objectView1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetObjectDefinitionObjectViewsPageWithSortDouble()
		throws Exception {

		testGetObjectDefinitionObjectViewsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, objectView1, objectView2) -> {
				BeanTestUtil.setProperty(
					objectView1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					objectView2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectViewsPageWithSortInteger()
		throws Exception {

		testGetObjectDefinitionObjectViewsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, objectView1, objectView2) -> {
				BeanTestUtil.setProperty(objectView1, entityField.getName(), 0);
				BeanTestUtil.setProperty(objectView2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetObjectDefinitionObjectViewsPageWithSortString()
		throws Exception {

		testGetObjectDefinitionObjectViewsPageWithSort(
			EntityField.Type.STRING,
			(entityField, objectView1, objectView2) -> {
				Class<?> clazz = objectView1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						objectView1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						objectView2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						objectView1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						objectView2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						objectView1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						objectView2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetObjectDefinitionObjectViewsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, ObjectView, ObjectView, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long objectDefinitionId =
			testGetObjectDefinitionObjectViewsPage_getObjectDefinitionId();

		ObjectView objectView1 = randomObjectView();
		ObjectView objectView2 = randomObjectView();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, objectView1, objectView2);
		}

		objectView1 = testGetObjectDefinitionObjectViewsPage_addObjectView(
			objectDefinitionId, objectView1);

		objectView2 = testGetObjectDefinitionObjectViewsPage_addObjectView(
			objectDefinitionId, objectView2);

		Page<ObjectView> page =
			objectViewResource.getObjectDefinitionObjectViewsPage(
				objectDefinitionId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ObjectView> ascPage =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(objectView1, (List<ObjectView>)ascPage.getItems());
			assertContains(objectView2, (List<ObjectView>)ascPage.getItems());

			Page<ObjectView> descPage =
				objectViewResource.getObjectDefinitionObjectViewsPage(
					objectDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(objectView2, (List<ObjectView>)descPage.getItems());
			assertContains(objectView1, (List<ObjectView>)descPage.getItems());
		}
	}

	protected ObjectView testGetObjectDefinitionObjectViewsPage_addObjectView(
			Long objectDefinitionId, ObjectView objectView)
		throws Exception {

		return objectViewResource.postObjectDefinitionObjectView(
			objectDefinitionId, objectView);
	}

	protected Long
			testGetObjectDefinitionObjectViewsPage_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetObjectDefinitionObjectViewsPage_getIrrelevantObjectDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetObjectDefinitionObjectViewsPage()
		throws Exception {

		Long objectDefinitionId =
			testGetObjectDefinitionObjectViewsPage_getObjectDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"objectDefinitionObjectViews",
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

		JSONObject objectDefinitionObjectViewsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/objectDefinitionObjectViews");

		long totalCount = objectDefinitionObjectViewsJSONObject.getLong(
			"totalCount");

		ObjectView objectView1 =
			testGraphQLObjectDefinitionObjectView_addObjectView(
				objectDefinitionId, randomObjectView());

		ObjectView objectView2 =
			testGraphQLObjectDefinitionObjectView_addObjectView(
				objectDefinitionId, randomObjectView());

		objectDefinitionObjectViewsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/objectDefinitionObjectViews");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectViewsJSONObject.getLong("totalCount"));

		assertContains(
			objectView1,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionObjectViewsJSONObject.getString("items"))));
		assertContains(
			objectView2,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionObjectViewsJSONObject.getString("items"))));

		// Using the namespace objectAdmin_v1_0

		objectDefinitionObjectViewsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("objectAdmin_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/objectAdmin_v1_0",
			"JSONObject/objectDefinitionObjectViews");

		Assert.assertEquals(
			totalCount + 2,
			objectDefinitionObjectViewsJSONObject.getLong("totalCount"));

		assertContains(
			objectView1,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionObjectViewsJSONObject.getString("items"))));
		assertContains(
			objectView2,
			Arrays.asList(
				ObjectViewSerDes.toDTOs(
					objectDefinitionObjectViewsJSONObject.getString("items"))));
	}

	@Test
	public void testGetObjectView() throws Exception {
		ObjectView postObjectView = testGetObjectView_addObjectView();

		ObjectView getObjectView = objectViewResource.getObjectView(
			postObjectView.getId());

		assertEquals(postObjectView, getObjectView);
		assertValid(getObjectView);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ObjectView postObjectView = testGetObjectView_addObjectView();

		ObjectView getObjectView = objectViewResource.getObjectView(
			postObjectView.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.object.admin.rest.dto.v1_0.ObjectView"
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

		Object item = vulcanCRUDItemDelegate.getItem(postObjectView.getId());

		assertEquals(getObjectView, ObjectViewSerDes.toDTO(item.toString()));
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

	protected ObjectView testGetObjectView_addObjectView() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetObjectView() throws Exception {
		ObjectView objectView = testGraphQLGetObjectView_addObjectView();

		// No namespace

		Assert.assertTrue(
			equals(
				objectView,
				ObjectViewSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectView",
								new HashMap<String, Object>() {
									{
										put("objectViewId", objectView.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/objectView"))));

		// Using the namespace objectAdmin_v1_0

		Assert.assertTrue(
			equals(
				objectView,
				ObjectViewSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"objectAdmin_v1_0",
								new GraphQLField(
									"objectView",
									new HashMap<String, Object>() {
										{
											put(
												"objectViewId",
												objectView.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/objectAdmin_v1_0",
						"Object/objectView"))));
	}

	@Test
	public void testGraphQLGetObjectViewNotFound() throws Exception {
		Long irrelevantObjectViewId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"objectView",
						new HashMap<String, Object>() {
							{
								put("objectViewId", irrelevantObjectViewId);
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
							"objectView",
							new HashMap<String, Object>() {
								{
									put("objectViewId", irrelevantObjectViewId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ObjectView testGraphQLGetObjectView_addObjectView()
		throws Exception {

		return testGraphQLObjectView_addObjectView();
	}

	@Test
	public void testPostObjectDefinitionByExternalReferenceCodeObjectView()
		throws Exception {

		ObjectView randomObjectView = randomObjectView();

		ObjectView postObjectView =
			testPostObjectDefinitionByExternalReferenceCodeObjectView_addObjectView(
				randomObjectView);

		assertEquals(randomObjectView, postObjectView);
		assertValid(postObjectView);
	}

	protected ObjectView
			testPostObjectDefinitionByExternalReferenceCodeObjectView_addObjectView(
				ObjectView objectView)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectView()
		throws Exception {

		ObjectView randomObjectView = randomObjectView();

		ObjectView objectView =
			testGraphQLObjectDefinitionObjectView_addObjectView(
				testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectView_getObjectDefinitionId(
					randomObjectView),
				randomObjectView);

		Assert.assertTrue(equals(randomObjectView, objectView));
	}

	protected Long
			testGraphQLPostObjectDefinitionByExternalReferenceCodeObjectView_getObjectDefinitionId(
				ObjectView objectView)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectDefinitionObjectView() throws Exception {
		ObjectView randomObjectView = randomObjectView();

		ObjectView postObjectView =
			testPostObjectDefinitionObjectView_addObjectView(randomObjectView);

		assertEquals(randomObjectView, postObjectView);
		assertValid(postObjectView);
	}

	protected ObjectView testPostObjectDefinitionObjectView_addObjectView(
			ObjectView objectView)
		throws Exception {

		return objectViewResource.postObjectDefinitionObjectView(
			testGetObjectDefinitionObjectViewsPage_getObjectDefinitionId(),
			objectView);
	}

	@Test
	public void testGraphQLPostObjectDefinitionObjectView() throws Exception {
		ObjectView randomObjectView = randomObjectView();

		ObjectView objectView =
			testGraphQLObjectDefinitionObjectView_addObjectView(
				testGraphQLPostObjectDefinitionObjectView_getObjectDefinitionId(
					randomObjectView),
				randomObjectView);

		Assert.assertTrue(equals(randomObjectView, objectView));
	}

	protected Long
			testGraphQLPostObjectDefinitionObjectView_getObjectDefinitionId(
				ObjectView objectView)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostObjectViewCopy() throws Exception {
		ObjectView randomObjectView = randomObjectView();

		ObjectView postObjectView = testPostObjectViewCopy_addObjectView(
			randomObjectView);

		assertEquals(randomObjectView, postObjectView);
		assertValid(postObjectView);
	}

	protected ObjectView testPostObjectViewCopy_addObjectView(
			ObjectView objectView)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutObjectView() throws Exception {
		ObjectView postObjectView = testPutObjectView_addObjectView();

		ObjectView randomObjectView = randomObjectView();

		ObjectView putObjectView = objectViewResource.putObjectView(
			postObjectView.getId(), randomObjectView);

		assertEquals(randomObjectView, putObjectView);
		assertValid(putObjectView);

		ObjectView getObjectView = objectViewResource.getObjectView(
			putObjectView.getId());

		assertEquals(randomObjectView, getObjectView);
		assertValid(getObjectView);
	}

	protected ObjectView testPutObjectView_addObjectView() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ObjectView objectView1 =
			testBatchEngineDeleteImportTask_addObjectView();

		testBatchEngineDeleteImportTask_deleteObjectView(
			200, null, objectView1.getId());

		assertHttpResponseStatusCode(
			404,
			objectViewResource.getObjectViewHttpResponse(objectView1.getId()));
	}

	protected ObjectView testBatchEngineDeleteImportTask_addObjectView()
		throws Exception {

		return testDeleteObjectView_addObjectView();
	}

	protected void testBatchEngineDeleteImportTask_deleteObjectView(
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
				"com.liferay.object.admin.rest.dto.v1_0.ObjectView", null, null,
				null, null,
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

	protected ObjectView testGraphQLObjectView_addObjectView()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectView testGraphQLObjectDefinitionObjectView_addObjectView()
		throws Exception {

		return testGraphQLObjectDefinitionObjectView_addObjectView(
			testGraphQLObjectDefinitionObjectView_getObjectDefinitionId(),
			randomObjectView());
	}

	protected Long testGraphQLObjectDefinitionObjectView_getObjectDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected ObjectView testGraphQLObjectDefinitionObjectView_addObjectView(
			Long objectDefinitionId, ObjectView objectView)
		throws Exception {

		JSONDeserializer<ObjectView> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(ObjectView.class)) {

			if (getGraphQLValue(field.get(objectView)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(objectView)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createObjectDefinitionObjectView",
						new HashMap<String, Object>() {
							{
								put("objectDefinitionId", objectDefinitionId);
								put("objectView", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createObjectDefinitionObjectView"),
			ObjectView.class);
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
		ObjectView objectView, List<ObjectView> objectViews) {

		boolean contains = false;

		for (ObjectView item : objectViews) {
			if (equals(objectView, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			objectViews + " does not contain " + objectView, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ObjectView objectView1, ObjectView objectView2) {

		Assert.assertTrue(
			objectView1 + " does not equal " + objectView2,
			equals(objectView1, objectView2));
	}

	protected void assertEquals(
		List<ObjectView> objectViews1, List<ObjectView> objectViews2) {

		Assert.assertEquals(objectViews1.size(), objectViews2.size());

		for (int i = 0; i < objectViews1.size(); i++) {
			ObjectView objectView1 = objectViews1.get(i);
			ObjectView objectView2 = objectViews2.get(i);

			assertEquals(objectView1, objectView2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ObjectView> objectViews1, List<ObjectView> objectViews2) {

		Assert.assertEquals(objectViews1.size(), objectViews2.size());

		for (ObjectView objectView1 : objectViews1) {
			boolean contains = false;

			for (ObjectView objectView2 : objectViews2) {
				if (equals(objectView1, objectView2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				objectViews2 + " does not contain " + objectView1, contains);
		}
	}

	protected void assertValid(ObjectView objectView) throws Exception {
		boolean valid = true;

		if (objectView.getDateCreated() == null) {
			valid = false;
		}

		if (objectView.getDateModified() == null) {
			valid = false;
		}

		if (objectView.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (objectView.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultObjectView", additionalAssertFieldName)) {

				if (objectView.getDefaultObjectView() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (objectView.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (objectView.getObjectDefinitionExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (objectView.getObjectDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectViewColumns", additionalAssertFieldName)) {

				if (objectView.getObjectViewColumns() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectViewFilterColumns", additionalAssertFieldName)) {

				if (objectView.getObjectViewFilterColumns() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"objectViewSortColumns", additionalAssertFieldName)) {

				if (objectView.getObjectViewSortColumns() == null) {
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

	protected void assertValid(Page<ObjectView> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ObjectView> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ObjectView> objectViews = page.getItems();

		int size = objectViews.size();

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
					com.liferay.object.admin.rest.dto.v1_0.ObjectView.class)) {

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

	protected boolean equals(ObjectView objectView1, ObjectView objectView2) {
		if (objectView1 == objectView2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectView1.getActions(),
						(Map)objectView2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectView1.getDateCreated(),
						objectView2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectView1.getDateModified(),
						objectView2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultObjectView", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectView1.getDefaultObjectView(),
						objectView2.getDefaultObjectView())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						objectView1.getId(), objectView2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)objectView1.getName(),
						(Map)objectView2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectView1.getObjectDefinitionExternalReferenceCode(),
						objectView2.
							getObjectDefinitionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectDefinitionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectView1.getObjectDefinitionId(),
						objectView2.getObjectDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectViewColumns", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectView1.getObjectViewColumns(),
						objectView2.getObjectViewColumns())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectViewFilterColumns", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectView1.getObjectViewFilterColumns(),
						objectView2.getObjectViewFilterColumns())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"objectViewSortColumns", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						objectView1.getObjectViewSortColumns(),
						objectView2.getObjectViewSortColumns())) {

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

		if (!(_objectViewResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_objectViewResource;

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
		EntityField entityField, String operator, ObjectView objectView) {

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
				Date date = objectView.getDateCreated();

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

				sb.append(_format.format(objectView.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = objectView.getDateModified();

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

				sb.append(_format.format(objectView.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("defaultObjectView")) {
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
				objectView.getObjectDefinitionExternalReferenceCode();

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

		if (entityFieldName.equals("objectViewColumns")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectViewFilterColumns")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("objectViewSortColumns")) {
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

	protected ObjectView randomObjectView() throws Exception {
		return new ObjectView() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultObjectView = RandomTestUtil.randomBoolean();
				id = RandomTestUtil.randomLong();
				objectDefinitionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				objectDefinitionId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ObjectView randomIrrelevantObjectView() throws Exception {
		ObjectView randomIrrelevantObjectView = randomObjectView();

		return randomIrrelevantObjectView;
	}

	protected ObjectView randomPatchObjectView() throws Exception {
		return randomObjectView();
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

	protected ObjectViewResource objectViewResource;
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
		LogFactoryUtil.getLog(BaseObjectViewResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.object.admin.rest.resource.v1_0.ObjectViewResource
		_objectViewResource;

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