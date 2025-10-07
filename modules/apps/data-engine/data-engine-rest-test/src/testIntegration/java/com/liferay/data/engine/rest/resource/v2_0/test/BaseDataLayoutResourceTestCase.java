/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.data.engine.rest.resource.v2_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.data.engine.rest.client.dto.v2_0.DataLayout;
import com.liferay.data.engine.rest.client.http.HttpInvoker;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.resource.v2_0.DataLayoutResource;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataLayoutSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
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
 * @author Jeyvison Nascimento
 * @generated
 */
@Generated("")
public abstract class BaseDataLayoutResourceTestCase {

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

		_dataLayoutResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		dataLayoutResource = DataLayoutResource.builder(
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

		DataLayout dataLayout1 = randomDataLayout();

		String json = objectMapper.writeValueAsString(dataLayout1);

		DataLayout dataLayout2 = DataLayoutSerDes.toDTO(json);

		Assert.assertTrue(equals(dataLayout1, dataLayout2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DataLayout dataLayout = randomDataLayout();

		String json1 = objectMapper.writeValueAsString(dataLayout);
		String json2 = DataLayoutSerDes.toJSON(dataLayout);

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

		DataLayout dataLayout = randomDataLayout();

		dataLayout.setContentType(regex);
		dataLayout.setDataLayoutKey(regex);
		dataLayout.setPaginationMode(regex);

		String json = DataLayoutSerDes.toJSON(dataLayout);

		Assert.assertFalse(json.contains(regex));

		dataLayout = DataLayoutSerDes.toDTO(json);

		Assert.assertEquals(regex, dataLayout.getContentType());
		Assert.assertEquals(regex, dataLayout.getDataLayoutKey());
		Assert.assertEquals(regex, dataLayout.getPaginationMode());
	}

	@Test
	public void testDeleteDataDefinitionDataLayout() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataLayout dataLayout =
			testDeleteDataDefinitionDataLayout_addDataLayout();

		assertHttpResponseStatusCode(
			204,
			dataLayoutResource.deleteDataDefinitionDataLayoutHttpResponse(
				testDeleteDataDefinitionDataLayout_getDataDefinitionId(
					dataLayout)));
	}

	protected DataLayout testDeleteDataDefinitionDataLayout_addDataLayout()
		throws Exception {

		return testPostDataDefinitionDataLayout_addDataLayout(
			randomDataLayout());
	}

	protected Long testDeleteDataDefinitionDataLayout_getDataDefinitionId(
			DataLayout dataLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDataDefinitionDataLayout() throws Exception {

		// No namespace

		DataLayout dataLayout1 =
			testGraphQLDeleteDataDefinitionDataLayout_addDataLayout();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDataDefinitionDataLayout",
						new HashMap<String, Object>() {
							{
								put(
									"dataDefinitionId",
									testGraphQLDeleteDataDefinitionDataLayout_getDataDefinitionId(
										dataLayout1));
							}
						})),
				"JSONObject/data", "Object/deleteDataDefinitionDataLayout"));

		// Using the namespace dataEngine_v2_0

		DataLayout dataLayout2 =
			testGraphQLDeleteDataDefinitionDataLayout_addDataLayout();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"deleteDataDefinitionDataLayout",
							new HashMap<String, Object>() {
								{
									put(
										"dataDefinitionId",
										testGraphQLDeleteDataDefinitionDataLayout_getDataDefinitionId(
											dataLayout2));
								}
							}))),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"Object/deleteDataDefinitionDataLayout"));
	}

	protected Long
			testGraphQLDeleteDataDefinitionDataLayout_getDataDefinitionId(
				DataLayout dataLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataLayout
			testGraphQLDeleteDataDefinitionDataLayout_addDataLayout()
		throws Exception {

		return testGraphQLDataDefinitionDataLayout_addDataLayout();
	}

	@Test
	public void testDeleteDataLayout() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataLayout dataLayout = testDeleteDataLayout_addDataLayout();

		assertHttpResponseStatusCode(
			204,
			dataLayoutResource.deleteDataLayoutHttpResponse(
				dataLayout.getId()));

		assertHttpResponseStatusCode(
			404,
			dataLayoutResource.getDataLayoutHttpResponse(dataLayout.getId()));
		assertHttpResponseStatusCode(
			404, dataLayoutResource.getDataLayoutHttpResponse(0L));
	}

	protected DataLayout testDeleteDataLayout_addDataLayout() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDataLayout() throws Exception {

		// No namespace

		DataLayout dataLayout1 = testGraphQLDeleteDataLayout_addDataLayout();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDataLayout",
						new HashMap<String, Object>() {
							{
								put("dataLayoutId", dataLayout1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDataLayout"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataLayout",
					new HashMap<String, Object>() {
						{
							put("dataLayoutId", dataLayout1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace dataEngine_v2_0

		DataLayout dataLayout2 = testGraphQLDeleteDataLayout_addDataLayout();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"deleteDataLayout",
							new HashMap<String, Object>() {
								{
									put("dataLayoutId", dataLayout2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"Object/deleteDataLayout"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataEngine_v2_0",
					new GraphQLField(
						"dataLayout",
						new HashMap<String, Object>() {
							{
								put("dataLayoutId", dataLayout2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DataLayout testGraphQLDeleteDataLayout_addDataLayout()
		throws Exception {

		return testGraphQLDataLayout_addDataLayout();
	}

	@Test
	public void testDeleteDataLayoutBatch() throws Exception {
		DataLayout dataLayout1 = testDeleteDataLayoutBatch_addDataLayout();

		testDeleteDataLayoutBatch_deleteDataLayout(
			202, null, dataLayout1.getId());

		assertHttpResponseStatusCode(
			404,
			dataLayoutResource.getDataLayoutHttpResponse(dataLayout1.getId()));
	}

	protected DataLayout testDeleteDataLayoutBatch_addDataLayout()
		throws Exception {

		return testDeleteDataLayout_addDataLayout();
	}

	protected void testDeleteDataLayoutBatch_deleteDataLayout(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			dataLayoutResource.deleteDataLayoutBatchHttpResponse(
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
	public void testGetDataDefinitionDataLayoutsPage() throws Exception {
		Long dataDefinitionId =
			testGetDataDefinitionDataLayoutsPage_getDataDefinitionId();
		Long irrelevantDataDefinitionId =
			testGetDataDefinitionDataLayoutsPage_getIrrelevantDataDefinitionId();

		Page<DataLayout> page =
			dataLayoutResource.getDataDefinitionDataLayoutsPage(
				dataDefinitionId, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantDataDefinitionId != null) {
			DataLayout irrelevantDataLayout =
				testGetDataDefinitionDataLayoutsPage_addDataLayout(
					irrelevantDataDefinitionId, randomIrrelevantDataLayout());

			page = dataLayoutResource.getDataDefinitionDataLayoutsPage(
				irrelevantDataDefinitionId, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDataLayout, (List<DataLayout>)page.getItems());
			assertValid(
				page,
				testGetDataDefinitionDataLayoutsPage_getExpectedActions(
					irrelevantDataDefinitionId));
		}

		DataLayout dataLayout1 =
			testGetDataDefinitionDataLayoutsPage_addDataLayout(
				dataDefinitionId, randomDataLayout());

		DataLayout dataLayout2 =
			testGetDataDefinitionDataLayoutsPage_addDataLayout(
				dataDefinitionId, randomDataLayout());

		page = dataLayoutResource.getDataDefinitionDataLayoutsPage(
			dataDefinitionId, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(dataLayout1, (List<DataLayout>)page.getItems());
		assertContains(dataLayout2, (List<DataLayout>)page.getItems());
		assertValid(
			page,
			testGetDataDefinitionDataLayoutsPage_getExpectedActions(
				dataDefinitionId));

		dataLayoutResource.deleteDataLayout(dataLayout1.getId());

		dataLayoutResource.deleteDataLayout(dataLayout2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataLayoutsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/data-engine/v2.0/data-definitions/{dataDefinitionId}/data-layouts/batch".
				replace(
					"{dataDefinitionId}", String.valueOf(dataDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetDataDefinitionDataLayoutsPageWithPagination()
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataLayoutsPage_getDataDefinitionId();

		Page<DataLayout> dataLayoutsPage =
			dataLayoutResource.getDataDefinitionDataLayoutsPage(
				dataDefinitionId, null, null, null);

		int totalCount = GetterUtil.getInteger(dataLayoutsPage.getTotalCount());

		DataLayout dataLayout1 =
			testGetDataDefinitionDataLayoutsPage_addDataLayout(
				dataDefinitionId, randomDataLayout());

		DataLayout dataLayout2 =
			testGetDataDefinitionDataLayoutsPage_addDataLayout(
				dataDefinitionId, randomDataLayout());

		DataLayout dataLayout3 =
			testGetDataDefinitionDataLayoutsPage_addDataLayout(
				dataDefinitionId, randomDataLayout());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DataLayout> page1 =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(dataLayout1, (List<DataLayout>)page1.getItems());

			Page<DataLayout> page2 =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(dataLayout2, (List<DataLayout>)page2.getItems());

			Page<DataLayout> page3 =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(dataLayout3, (List<DataLayout>)page3.getItems());
		}
		else {
			Page<DataLayout> page1 =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null, Pagination.of(1, totalCount + 2),
					null);

			List<DataLayout> dataLayouts1 = (List<DataLayout>)page1.getItems();

			Assert.assertEquals(
				dataLayouts1.toString(), totalCount + 2, dataLayouts1.size());

			Page<DataLayout> page2 =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DataLayout> dataLayouts2 = (List<DataLayout>)page2.getItems();

			Assert.assertEquals(
				dataLayouts2.toString(), 1, dataLayouts2.size());

			Page<DataLayout> page3 =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(dataLayout1, (List<DataLayout>)page3.getItems());
			assertContains(dataLayout2, (List<DataLayout>)page3.getItems());
			assertContains(dataLayout3, (List<DataLayout>)page3.getItems());
		}
	}

	@Test
	public void testGetDataDefinitionDataLayoutsPageWithSortDateTime()
		throws Exception {

		testGetDataDefinitionDataLayoutsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, dataLayout1, dataLayout2) -> {
				BeanTestUtil.setProperty(
					dataLayout1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDataDefinitionDataLayoutsPageWithSortDouble()
		throws Exception {

		testGetDataDefinitionDataLayoutsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, dataLayout1, dataLayout2) -> {
				BeanTestUtil.setProperty(
					dataLayout1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					dataLayout2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDataDefinitionDataLayoutsPageWithSortInteger()
		throws Exception {

		testGetDataDefinitionDataLayoutsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, dataLayout1, dataLayout2) -> {
				BeanTestUtil.setProperty(dataLayout1, entityField.getName(), 0);
				BeanTestUtil.setProperty(dataLayout2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDataDefinitionDataLayoutsPageWithSortString()
		throws Exception {

		testGetDataDefinitionDataLayoutsPageWithSort(
			EntityField.Type.STRING,
			(entityField, dataLayout1, dataLayout2) -> {
				Class<?> clazz = dataLayout1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						dataLayout1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						dataLayout2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						dataLayout1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						dataLayout2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						dataLayout1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						dataLayout2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDataDefinitionDataLayoutsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, DataLayout, DataLayout, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long dataDefinitionId =
			testGetDataDefinitionDataLayoutsPage_getDataDefinitionId();

		DataLayout dataLayout1 = randomDataLayout();
		DataLayout dataLayout2 = randomDataLayout();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, dataLayout1, dataLayout2);
		}

		dataLayout1 = testGetDataDefinitionDataLayoutsPage_addDataLayout(
			dataDefinitionId, dataLayout1);

		dataLayout2 = testGetDataDefinitionDataLayoutsPage_addDataLayout(
			dataDefinitionId, dataLayout2);

		Page<DataLayout> page =
			dataLayoutResource.getDataDefinitionDataLayoutsPage(
				dataDefinitionId, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DataLayout> ascPage =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(dataLayout1, (List<DataLayout>)ascPage.getItems());
			assertContains(dataLayout2, (List<DataLayout>)ascPage.getItems());

			Page<DataLayout> descPage =
				dataLayoutResource.getDataDefinitionDataLayoutsPage(
					dataDefinitionId, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(dataLayout2, (List<DataLayout>)descPage.getItems());
			assertContains(dataLayout1, (List<DataLayout>)descPage.getItems());
		}
	}

	protected DataLayout testGetDataDefinitionDataLayoutsPage_addDataLayout(
			Long dataDefinitionId, DataLayout dataLayout)
		throws Exception {

		return dataLayoutResource.postDataDefinitionDataLayout(
			dataDefinitionId, dataLayout);
	}

	protected Long testGetDataDefinitionDataLayoutsPage_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDataDefinitionDataLayoutsPage_getIrrelevantDataDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDataDefinitionDataLayoutsPage() throws Exception {
		Long dataDefinitionId =
			testGetDataDefinitionDataLayoutsPage_getDataDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"dataDefinitionDataLayouts",
			new HashMap<String, Object>() {
				{
					put("dataDefinitionId", dataDefinitionId);
					put("keywords", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject dataDefinitionDataLayoutsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/dataDefinitionDataLayouts");

		long totalCount = dataDefinitionDataLayoutsJSONObject.getLong(
			"totalCount");

		DataLayout dataLayout1 =
			testGraphQLDataDefinitionDataLayout_addDataLayout(
				dataDefinitionId, randomDataLayout());

		DataLayout dataLayout2 =
			testGraphQLDataDefinitionDataLayout_addDataLayout(
				dataDefinitionId, randomDataLayout());

		dataDefinitionDataLayoutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/dataDefinitionDataLayouts");

		Assert.assertEquals(
			totalCount + 2,
			dataDefinitionDataLayoutsJSONObject.getLong("totalCount"));

		assertContains(
			dataLayout1,
			Arrays.asList(
				DataLayoutSerDes.toDTOs(
					dataDefinitionDataLayoutsJSONObject.getString("items"))));
		assertContains(
			dataLayout2,
			Arrays.asList(
				DataLayoutSerDes.toDTOs(
					dataDefinitionDataLayoutsJSONObject.getString("items"))));

		// Using the namespace dataEngine_v2_0

		dataDefinitionDataLayoutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("dataEngine_v2_0", graphQLField)),
			"JSONObject/data", "JSONObject/dataEngine_v2_0",
			"JSONObject/dataDefinitionDataLayouts");

		Assert.assertEquals(
			totalCount + 2,
			dataDefinitionDataLayoutsJSONObject.getLong("totalCount"));

		assertContains(
			dataLayout1,
			Arrays.asList(
				DataLayoutSerDes.toDTOs(
					dataDefinitionDataLayoutsJSONObject.getString("items"))));
		assertContains(
			dataLayout2,
			Arrays.asList(
				DataLayoutSerDes.toDTOs(
					dataDefinitionDataLayoutsJSONObject.getString("items"))));
	}

	@Test
	public void testGetDataLayout() throws Exception {
		DataLayout postDataLayout = testGetDataLayout_addDataLayout();

		DataLayout getDataLayout = dataLayoutResource.getDataLayout(
			postDataLayout.getId());

		assertEquals(postDataLayout, getDataLayout);
		assertValid(getDataLayout);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DataLayout postDataLayout = testGetDataLayout_addDataLayout();

		DataLayout getDataLayout = dataLayoutResource.getDataLayout(
			postDataLayout.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.data.engine.rest.dto.v2_0.DataLayout"
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

		Object item = vulcanCRUDItemDelegate.getItem(postDataLayout.getId());

		assertEquals(getDataLayout, DataLayoutSerDes.toDTO(item.toString()));
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

	protected DataLayout testGetDataLayout_addDataLayout() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDataLayout() throws Exception {
		DataLayout dataLayout = testGraphQLGetDataLayout_addDataLayout();

		// No namespace

		Assert.assertTrue(
			equals(
				dataLayout,
				DataLayoutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataLayout",
								new HashMap<String, Object>() {
									{
										put("dataLayoutId", dataLayout.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/dataLayout"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataLayout,
				DataLayoutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataLayout",
									new HashMap<String, Object>() {
										{
											put(
												"dataLayoutId",
												dataLayout.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataLayout"))));
	}

	@Test
	public void testGraphQLGetDataLayoutNotFound() throws Exception {
		Long irrelevantDataLayoutId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataLayout",
						new HashMap<String, Object>() {
							{
								put("dataLayoutId", irrelevantDataLayoutId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace dataEngine_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"dataLayout",
							new HashMap<String, Object>() {
								{
									put("dataLayoutId", irrelevantDataLayoutId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataLayout testGraphQLGetDataLayout_addDataLayout()
		throws Exception {

		return testGraphQLDataLayout_addDataLayout();
	}

	@Test
	public void testGetSiteDataLayoutByContentTypeByDataLayoutKey()
		throws Exception {

		DataLayout postDataLayout =
			testGetSiteDataLayoutByContentTypeByDataLayoutKey_addDataLayout();

		DataLayout getDataLayout =
			dataLayoutResource.getSiteDataLayoutByContentTypeByDataLayoutKey(
				postDataLayout.getSiteId(), postDataLayout.getContentType(),
				postDataLayout.getDataLayoutKey());

		assertEquals(postDataLayout, getDataLayout);
		assertValid(getDataLayout);
	}

	protected DataLayout
			testGetSiteDataLayoutByContentTypeByDataLayoutKey_addDataLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteDataLayoutByContentTypeByDataLayoutKey()
		throws Exception {

		DataLayout dataLayout =
			testGraphQLGetSiteDataLayoutByContentTypeByDataLayoutKey_addDataLayout();

		// No namespace

		Assert.assertTrue(
			equals(
				dataLayout,
				DataLayoutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataLayoutByContentTypeByDataLayoutKey",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + dataLayout.getSiteId() +
												"\"");
										put(
											"contentType",
											"\"" + dataLayout.getContentType() +
												"\"");
										put(
											"dataLayoutKey",
											"\"" +
												dataLayout.getDataLayoutKey() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/dataLayoutByContentTypeByDataLayoutKey"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataLayout,
				DataLayoutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataLayoutByContentTypeByDataLayoutKey",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" + dataLayout.getSiteId() +
													"\"");
											put(
												"contentType",
												"\"" +
													dataLayout.
														getContentType() +
															"\"");
											put(
												"dataLayoutKey",
												"\"" +
													dataLayout.
														getDataLayoutKey() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataLayoutByContentTypeByDataLayoutKey"))));
	}

	@Test
	public void testGraphQLGetSiteDataLayoutByContentTypeByDataLayoutKeyNotFound()
		throws Exception {

		String irrelevantContentType =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantDataLayoutKey =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataLayoutByContentTypeByDataLayoutKey",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put("contentType", irrelevantContentType);
								put("dataLayoutKey", irrelevantDataLayoutKey);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace dataEngine_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"dataLayoutByContentTypeByDataLayoutKey",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put("contentType", irrelevantContentType);
									put(
										"dataLayoutKey",
										irrelevantDataLayoutKey);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataLayout
			testGraphQLGetSiteDataLayoutByContentTypeByDataLayoutKey_addDataLayout()
		throws Exception {

		return testGraphQLSiteDataLayout_addDataLayout();
	}

	@Test
	public void testPostDataDefinitionDataLayout() throws Exception {
		DataLayout randomDataLayout = randomDataLayout();

		DataLayout postDataLayout =
			testPostDataDefinitionDataLayout_addDataLayout(randomDataLayout);

		assertEquals(randomDataLayout, postDataLayout);
		assertValid(postDataLayout);
	}

	protected DataLayout testPostDataDefinitionDataLayout_addDataLayout(
			DataLayout dataLayout)
		throws Exception {

		return dataLayoutResource.postDataDefinitionDataLayout(
			testGetDataDefinitionDataLayoutsPage_getDataDefinitionId(),
			dataLayout);
	}

	@Test
	public void testGraphQLPostDataDefinitionDataLayout() throws Exception {
		DataLayout randomDataLayout = randomDataLayout();

		DataLayout dataLayout =
			testGraphQLDataDefinitionDataLayout_addDataLayout(
				testGraphQLPostDataDefinitionDataLayout_getDataDefinitionId(
					randomDataLayout),
				randomDataLayout);

		Assert.assertTrue(equals(randomDataLayout, dataLayout));
	}

	protected Long testGraphQLPostDataDefinitionDataLayout_getDataDefinitionId(
			DataLayout dataLayout)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDataLayoutContext() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPutDataLayout() throws Exception {
		DataLayout postDataLayout = testPutDataLayout_addDataLayout();

		DataLayout randomDataLayout = randomDataLayout();

		DataLayout putDataLayout = dataLayoutResource.putDataLayout(
			postDataLayout.getId(), randomDataLayout);

		assertEquals(randomDataLayout, putDataLayout);
		assertValid(putDataLayout);

		DataLayout getDataLayout = dataLayoutResource.getDataLayout(
			putDataLayout.getId());

		assertEquals(randomDataLayout, getDataLayout);
		assertValid(getDataLayout);
	}

	protected DataLayout testPutDataLayout_addDataLayout() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DataLayout dataLayout1 =
			testBatchEngineDeleteImportTask_addDataLayout();

		testBatchEngineDeleteImportTask_deleteDataLayout(
			200, null, dataLayout1.getId());

		assertHttpResponseStatusCode(
			404,
			dataLayoutResource.getDataLayoutHttpResponse(dataLayout1.getId()));
	}

	protected DataLayout testBatchEngineDeleteImportTask_addDataLayout()
		throws Exception {

		return testDeleteDataLayout_addDataLayout();
	}

	protected void testBatchEngineDeleteImportTask_deleteDataLayout(
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
				"com.liferay.data.engine.rest.dto.v2_0.DataLayout", null, null,
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

	protected DataLayout testGraphQLDataDefinitionDataLayout_addDataLayout()
		throws Exception {

		return testGraphQLDataDefinitionDataLayout_addDataLayout(
			testGraphQLDataDefinitionDataLayout_getDataDefinitionId(),
			randomDataLayout());
	}

	protected Long testGraphQLDataDefinitionDataLayout_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataLayout testGraphQLDataDefinitionDataLayout_addDataLayout(
			Long dataDefinitionId, DataLayout dataLayout)
		throws Exception {

		JSONDeserializer<DataLayout> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DataLayout.class)) {

			if (getGraphQLValue(field.get(dataLayout)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(dataLayout)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDataDefinitionDataLayout",
						new HashMap<String, Object>() {
							{
								put("dataDefinitionId", dataDefinitionId);
								put("dataLayout", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createDataDefinitionDataLayout"),
			DataLayout.class);
	}

	protected DataLayout testGraphQLDataLayout_addDataLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataLayout testGraphQLSiteDataLayout_addDataLayout()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
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
		DataLayout dataLayout, List<DataLayout> dataLayouts) {

		boolean contains = false;

		for (DataLayout item : dataLayouts) {
			if (equals(dataLayout, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			dataLayouts + " does not contain " + dataLayout, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DataLayout dataLayout1, DataLayout dataLayout2) {

		Assert.assertTrue(
			dataLayout1 + " does not equal " + dataLayout2,
			equals(dataLayout1, dataLayout2));
	}

	protected void assertEquals(
		List<DataLayout> dataLayouts1, List<DataLayout> dataLayouts2) {

		Assert.assertEquals(dataLayouts1.size(), dataLayouts2.size());

		for (int i = 0; i < dataLayouts1.size(); i++) {
			DataLayout dataLayout1 = dataLayouts1.get(i);
			DataLayout dataLayout2 = dataLayouts2.get(i);

			assertEquals(dataLayout1, dataLayout2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DataLayout> dataLayouts1, List<DataLayout> dataLayouts2) {

		Assert.assertEquals(dataLayouts1.size(), dataLayouts2.size());

		for (DataLayout dataLayout1 : dataLayouts1) {
			boolean contains = false;

			for (DataLayout dataLayout2 : dataLayouts2) {
				if (equals(dataLayout1, dataLayout2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				dataLayouts2 + " does not contain " + dataLayout1, contains);
		}
	}

	protected void assertValid(DataLayout dataLayout) throws Exception {
		boolean valid = true;

		if (dataLayout.getDateCreated() == null) {
			valid = false;
		}

		if (dataLayout.getDateModified() == null) {
			valid = false;
		}

		if (dataLayout.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(dataLayout.getSiteId(), testGroup.getGroupId())) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (dataLayout.getContentType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataDefinitionId", additionalAssertFieldName)) {
				if (dataLayout.getDataDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataLayoutFields", additionalAssertFieldName)) {
				if (dataLayout.getDataLayoutFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataLayoutKey", additionalAssertFieldName)) {
				if (dataLayout.getDataLayoutKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataLayoutPages", additionalAssertFieldName)) {
				if (dataLayout.getDataLayoutPages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataRules", additionalAssertFieldName)) {
				if (dataLayout.getDataRules() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (dataLayout.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (dataLayout.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("paginationMode", additionalAssertFieldName)) {
				if (dataLayout.getPaginationMode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (dataLayout.getUserId() == null) {
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

	protected void assertValid(Page<DataLayout> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DataLayout> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DataLayout> dataLayouts = page.getItems();

		int size = dataLayouts.size();

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

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.data.engine.rest.dto.v2_0.DataLayout.class)) {

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

	protected boolean equals(DataLayout dataLayout1, DataLayout dataLayout2) {
		if (dataLayout1 == dataLayout2) {
			return true;
		}

		if (!Objects.equals(dataLayout1.getSiteId(), dataLayout2.getSiteId())) {
			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("contentType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getContentType(),
						dataLayout2.getContentType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataDefinitionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getDataDefinitionId(),
						dataLayout2.getDataDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataLayoutFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataLayout1.getDataLayoutFields(),
						(Map)dataLayout2.getDataLayoutFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataLayoutKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getDataLayoutKey(),
						dataLayout2.getDataLayoutKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataLayoutPages", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getDataLayoutPages(),
						dataLayout2.getDataLayoutPages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataRules", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getDataRules(),
						dataLayout2.getDataRules())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getDateCreated(),
						dataLayout2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getDateModified(),
						dataLayout2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataLayout1.getDescription(),
						(Map)dataLayout2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getId(), dataLayout2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataLayout1.getName(),
						(Map)dataLayout2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("paginationMode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getPaginationMode(),
						dataLayout2.getPaginationMode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataLayout1.getUserId(), dataLayout2.getUserId())) {

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

		if (!(_dataLayoutResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_dataLayoutResource;

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
		EntityField entityField, String operator, DataLayout dataLayout) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("contentType")) {
			Object object = dataLayout.getContentType();

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

		if (entityFieldName.equals("dataDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataLayoutFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataLayoutKey")) {
			Object object = dataLayout.getDataLayoutKey();

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

		if (entityFieldName.equals("dataLayoutPages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataRules")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = dataLayout.getDateCreated();

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

				sb.append(_format.format(dataLayout.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = dataLayout.getDateModified();

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

				sb.append(_format.format(dataLayout.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
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

		if (entityFieldName.equals("paginationMode")) {
			Object object = dataLayout.getPaginationMode();

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

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userId")) {
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

	protected DataLayout randomDataLayout() throws Exception {
		return new DataLayout() {
			{
				contentType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dataDefinitionId = RandomTestUtil.randomLong();
				dataLayoutKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				paginationMode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
				userId = RandomTestUtil.randomLong();
			}
		};
	}

	protected DataLayout randomIrrelevantDataLayout() throws Exception {
		DataLayout randomIrrelevantDataLayout = randomDataLayout();

		randomIrrelevantDataLayout.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantDataLayout;
	}

	protected DataLayout randomPatchDataLayout() throws Exception {
		return randomDataLayout();
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

	protected DataLayoutResource dataLayoutResource;
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
		LogFactoryUtil.getLog(BaseDataLayoutResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.data.engine.rest.resource.v2_0.DataLayoutResource
		_dataLayoutResource;

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