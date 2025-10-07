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

import com.liferay.data.engine.rest.client.dto.v2_0.DataRecord;
import com.liferay.data.engine.rest.client.http.HttpInvoker;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.resource.v2_0.DataRecordResource;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataRecordSerDes;
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
public abstract class BaseDataRecordResourceTestCase {

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

		_dataRecordResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		dataRecordResource = DataRecordResource.builder(
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

		DataRecord dataRecord1 = randomDataRecord();

		String json = objectMapper.writeValueAsString(dataRecord1);

		DataRecord dataRecord2 = DataRecordSerDes.toDTO(json);

		Assert.assertTrue(equals(dataRecord1, dataRecord2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DataRecord dataRecord = randomDataRecord();

		String json1 = objectMapper.writeValueAsString(dataRecord);
		String json2 = DataRecordSerDes.toJSON(dataRecord);

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

		DataRecord dataRecord = randomDataRecord();

		String json = DataRecordSerDes.toJSON(dataRecord);

		Assert.assertFalse(json.contains(regex));

		dataRecord = DataRecordSerDes.toDTO(json);
	}

	@Test
	public void testDeleteDataRecord() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataRecord dataRecord = testDeleteDataRecord_addDataRecord();

		assertHttpResponseStatusCode(
			204,
			dataRecordResource.deleteDataRecordHttpResponse(
				dataRecord.getId()));

		assertHttpResponseStatusCode(
			404,
			dataRecordResource.getDataRecordHttpResponse(dataRecord.getId()));
		assertHttpResponseStatusCode(
			404, dataRecordResource.getDataRecordHttpResponse(0L));
	}

	protected DataRecord testDeleteDataRecord_addDataRecord() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDataRecord() throws Exception {

		// No namespace

		DataRecord dataRecord1 = testGraphQLDeleteDataRecord_addDataRecord();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDataRecord",
						new HashMap<String, Object>() {
							{
								put("dataRecordId", dataRecord1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDataRecord"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataRecord",
					new HashMap<String, Object>() {
						{
							put("dataRecordId", dataRecord1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace dataEngine_v2_0

		DataRecord dataRecord2 = testGraphQLDeleteDataRecord_addDataRecord();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"deleteDataRecord",
							new HashMap<String, Object>() {
								{
									put("dataRecordId", dataRecord2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"Object/deleteDataRecord"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataEngine_v2_0",
					new GraphQLField(
						"dataRecord",
						new HashMap<String, Object>() {
							{
								put("dataRecordId", dataRecord2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DataRecord testGraphQLDeleteDataRecord_addDataRecord()
		throws Exception {

		return testGraphQLDataRecord_addDataRecord();
	}

	@Test
	public void testDeleteDataRecordBatch() throws Exception {
		DataRecord dataRecord1 = testDeleteDataRecordBatch_addDataRecord();

		testDeleteDataRecordBatch_deleteDataRecord(
			202, null, dataRecord1.getId());

		assertHttpResponseStatusCode(
			404,
			dataRecordResource.getDataRecordHttpResponse(dataRecord1.getId()));
	}

	protected DataRecord testDeleteDataRecordBatch_addDataRecord()
		throws Exception {

		return testDeleteDataRecord_addDataRecord();
	}

	protected void testDeleteDataRecordBatch_deleteDataRecord(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			dataRecordResource.deleteDataRecordBatchHttpResponse(
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
	public void testGetDataDefinitionDataRecordsPage() throws Exception {
		Long dataDefinitionId =
			testGetDataDefinitionDataRecordsPage_getDataDefinitionId();
		Long irrelevantDataDefinitionId =
			testGetDataDefinitionDataRecordsPage_getIrrelevantDataDefinitionId();

		Page<DataRecord> page =
			dataRecordResource.getDataDefinitionDataRecordsPage(
				dataDefinitionId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantDataDefinitionId != null) {
			DataRecord irrelevantDataRecord =
				testGetDataDefinitionDataRecordsPage_addDataRecord(
					irrelevantDataDefinitionId, randomIrrelevantDataRecord());

			page = dataRecordResource.getDataDefinitionDataRecordsPage(
				irrelevantDataDefinitionId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDataRecord, (List<DataRecord>)page.getItems());
			assertValid(
				page,
				testGetDataDefinitionDataRecordsPage_getExpectedActions(
					irrelevantDataDefinitionId));
		}

		DataRecord dataRecord1 =
			testGetDataDefinitionDataRecordsPage_addDataRecord(
				dataDefinitionId, randomDataRecord());

		DataRecord dataRecord2 =
			testGetDataDefinitionDataRecordsPage_addDataRecord(
				dataDefinitionId, randomDataRecord());

		page = dataRecordResource.getDataDefinitionDataRecordsPage(
			dataDefinitionId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(dataRecord1, (List<DataRecord>)page.getItems());
		assertContains(dataRecord2, (List<DataRecord>)page.getItems());
		assertValid(
			page,
			testGetDataDefinitionDataRecordsPage_getExpectedActions(
				dataDefinitionId));

		dataRecordResource.deleteDataRecord(dataRecord1.getId());

		dataRecordResource.deleteDataRecord(dataRecord2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataRecordsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/data-engine/v2.0/data-definitions/{dataDefinitionId}/data-records/batch".
				replace(
					"{dataDefinitionId}", String.valueOf(dataDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetDataDefinitionDataRecordsPageWithPagination()
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataRecordsPage_getDataDefinitionId();

		Page<DataRecord> dataRecordsPage =
			dataRecordResource.getDataDefinitionDataRecordsPage(
				dataDefinitionId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(dataRecordsPage.getTotalCount());

		DataRecord dataRecord1 =
			testGetDataDefinitionDataRecordsPage_addDataRecord(
				dataDefinitionId, randomDataRecord());

		DataRecord dataRecord2 =
			testGetDataDefinitionDataRecordsPage_addDataRecord(
				dataDefinitionId, randomDataRecord());

		DataRecord dataRecord3 =
			testGetDataDefinitionDataRecordsPage_addDataRecord(
				dataDefinitionId, randomDataRecord());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DataRecord> page1 =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(dataRecord1, (List<DataRecord>)page1.getItems());

			Page<DataRecord> page2 =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(dataRecord2, (List<DataRecord>)page2.getItems());

			Page<DataRecord> page3 =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(dataRecord3, (List<DataRecord>)page3.getItems());
		}
		else {
			Page<DataRecord> page1 =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<DataRecord> dataRecords1 = (List<DataRecord>)page1.getItems();

			Assert.assertEquals(
				dataRecords1.toString(), totalCount + 2, dataRecords1.size());

			Page<DataRecord> page2 =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DataRecord> dataRecords2 = (List<DataRecord>)page2.getItems();

			Assert.assertEquals(
				dataRecords2.toString(), 1, dataRecords2.size());

			Page<DataRecord> page3 =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(dataRecord1, (List<DataRecord>)page3.getItems());
			assertContains(dataRecord2, (List<DataRecord>)page3.getItems());
			assertContains(dataRecord3, (List<DataRecord>)page3.getItems());
		}
	}

	@Test
	public void testGetDataDefinitionDataRecordsPageWithSortDateTime()
		throws Exception {

		testGetDataDefinitionDataRecordsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, dataRecord1, dataRecord2) -> {
				BeanTestUtil.setProperty(
					dataRecord1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDataDefinitionDataRecordsPageWithSortDouble()
		throws Exception {

		testGetDataDefinitionDataRecordsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, dataRecord1, dataRecord2) -> {
				BeanTestUtil.setProperty(
					dataRecord1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					dataRecord2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDataDefinitionDataRecordsPageWithSortInteger()
		throws Exception {

		testGetDataDefinitionDataRecordsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, dataRecord1, dataRecord2) -> {
				BeanTestUtil.setProperty(dataRecord1, entityField.getName(), 0);
				BeanTestUtil.setProperty(dataRecord2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDataDefinitionDataRecordsPageWithSortString()
		throws Exception {

		testGetDataDefinitionDataRecordsPageWithSort(
			EntityField.Type.STRING,
			(entityField, dataRecord1, dataRecord2) -> {
				Class<?> clazz = dataRecord1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						dataRecord1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						dataRecord2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						dataRecord1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						dataRecord2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						dataRecord1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						dataRecord2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDataDefinitionDataRecordsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, DataRecord, DataRecord, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long dataDefinitionId =
			testGetDataDefinitionDataRecordsPage_getDataDefinitionId();

		DataRecord dataRecord1 = randomDataRecord();
		DataRecord dataRecord2 = randomDataRecord();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, dataRecord1, dataRecord2);
		}

		dataRecord1 = testGetDataDefinitionDataRecordsPage_addDataRecord(
			dataDefinitionId, dataRecord1);

		dataRecord2 = testGetDataDefinitionDataRecordsPage_addDataRecord(
			dataDefinitionId, dataRecord2);

		Page<DataRecord> page =
			dataRecordResource.getDataDefinitionDataRecordsPage(
				dataDefinitionId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DataRecord> ascPage =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(dataRecord1, (List<DataRecord>)ascPage.getItems());
			assertContains(dataRecord2, (List<DataRecord>)ascPage.getItems());

			Page<DataRecord> descPage =
				dataRecordResource.getDataDefinitionDataRecordsPage(
					dataDefinitionId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(dataRecord2, (List<DataRecord>)descPage.getItems());
			assertContains(dataRecord1, (List<DataRecord>)descPage.getItems());
		}
	}

	protected DataRecord testGetDataDefinitionDataRecordsPage_addDataRecord(
			Long dataDefinitionId, DataRecord dataRecord)
		throws Exception {

		return dataRecordResource.postDataDefinitionDataRecord(
			dataDefinitionId, dataRecord);
	}

	protected Long testGetDataDefinitionDataRecordsPage_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDataDefinitionDataRecordsPage_getIrrelevantDataDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDataDefinitionDataRecordsPage() throws Exception {
		Long dataDefinitionId =
			testGetDataDefinitionDataRecordsPage_getDataDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"dataDefinitionDataRecords",
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

		JSONObject dataDefinitionDataRecordsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/dataDefinitionDataRecords");

		long totalCount = dataDefinitionDataRecordsJSONObject.getLong(
			"totalCount");

		DataRecord dataRecord1 =
			testGraphQLDataDefinitionDataRecord_addDataRecord(
				dataDefinitionId, randomDataRecord());

		DataRecord dataRecord2 =
			testGraphQLDataDefinitionDataRecord_addDataRecord(
				dataDefinitionId, randomDataRecord());

		dataDefinitionDataRecordsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/dataDefinitionDataRecords");

		Assert.assertEquals(
			totalCount + 2,
			dataDefinitionDataRecordsJSONObject.getLong("totalCount"));

		assertContains(
			dataRecord1,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataDefinitionDataRecordsJSONObject.getString("items"))));
		assertContains(
			dataRecord2,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataDefinitionDataRecordsJSONObject.getString("items"))));

		// Using the namespace dataEngine_v2_0

		dataDefinitionDataRecordsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("dataEngine_v2_0", graphQLField)),
			"JSONObject/data", "JSONObject/dataEngine_v2_0",
			"JSONObject/dataDefinitionDataRecords");

		Assert.assertEquals(
			totalCount + 2,
			dataDefinitionDataRecordsJSONObject.getLong("totalCount"));

		assertContains(
			dataRecord1,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataDefinitionDataRecordsJSONObject.getString("items"))));
		assertContains(
			dataRecord2,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataDefinitionDataRecordsJSONObject.getString("items"))));
	}

	@Test
	public void testGetDataRecord() throws Exception {
		DataRecord postDataRecord = testGetDataRecord_addDataRecord();

		DataRecord getDataRecord = dataRecordResource.getDataRecord(
			postDataRecord.getId());

		assertEquals(postDataRecord, getDataRecord);
		assertValid(getDataRecord);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DataRecord postDataRecord = testGetDataRecord_addDataRecord();

		DataRecord getDataRecord = dataRecordResource.getDataRecord(
			postDataRecord.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.data.engine.rest.dto.v2_0.DataRecord"
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

		Object item = vulcanCRUDItemDelegate.getItem(postDataRecord.getId());

		assertEquals(getDataRecord, DataRecordSerDes.toDTO(item.toString()));
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

	protected DataRecord testGetDataRecord_addDataRecord() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDataRecord() throws Exception {
		DataRecord dataRecord = testGraphQLGetDataRecord_addDataRecord();

		// No namespace

		Assert.assertTrue(
			equals(
				dataRecord,
				DataRecordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataRecord",
								new HashMap<String, Object>() {
									{
										put("dataRecordId", dataRecord.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/dataRecord"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataRecord,
				DataRecordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataRecord",
									new HashMap<String, Object>() {
										{
											put(
												"dataRecordId",
												dataRecord.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataRecord"))));
	}

	@Test
	public void testGraphQLGetDataRecordNotFound() throws Exception {
		Long irrelevantDataRecordId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataRecord",
						new HashMap<String, Object>() {
							{
								put("dataRecordId", irrelevantDataRecordId);
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
							"dataRecord",
							new HashMap<String, Object>() {
								{
									put("dataRecordId", irrelevantDataRecordId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataRecord testGraphQLGetDataRecord_addDataRecord()
		throws Exception {

		return testGraphQLDataRecord_addDataRecord();
	}

	@Test
	public void testGetDataRecordCollectionDataRecordExport() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetDataRecordCollectionDataRecordsPage() throws Exception {
		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();
		Long irrelevantDataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getIrrelevantDataRecordCollectionId();

		Page<DataRecord> page =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantDataRecordCollectionId != null) {
			DataRecord irrelevantDataRecord =
				testGetDataRecordCollectionDataRecordsPage_addDataRecord(
					irrelevantDataRecordCollectionId,
					randomIrrelevantDataRecord());

			page = dataRecordResource.getDataRecordCollectionDataRecordsPage(
				irrelevantDataRecordCollectionId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDataRecord, (List<DataRecord>)page.getItems());
			assertValid(
				page,
				testGetDataRecordCollectionDataRecordsPage_getExpectedActions(
					irrelevantDataRecordCollectionId));
		}

		DataRecord dataRecord1 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		DataRecord dataRecord2 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		page = dataRecordResource.getDataRecordCollectionDataRecordsPage(
			dataRecordCollectionId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(dataRecord1, (List<DataRecord>)page.getItems());
		assertContains(dataRecord2, (List<DataRecord>)page.getItems());
		assertValid(
			page,
			testGetDataRecordCollectionDataRecordsPage_getExpectedActions(
				dataRecordCollectionId));

		dataRecordResource.deleteDataRecord(dataRecord1.getId());

		dataRecordResource.deleteDataRecord(dataRecord2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDataRecordCollectionDataRecordsPage_getExpectedActions(
				Long dataRecordCollectionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/data-engine/v2.0/data-record-collections/{dataRecordCollectionId}/data-records/batch".
				replace(
					"{dataRecordCollectionId}",
					String.valueOf(dataRecordCollectionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetDataRecordCollectionDataRecordsPageWithPagination()
		throws Exception {

		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();

		Page<DataRecord> dataRecordsPage =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(dataRecordsPage.getTotalCount());

		DataRecord dataRecord1 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		DataRecord dataRecord2 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		DataRecord dataRecord3 =
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DataRecord> page1 =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(dataRecord1, (List<DataRecord>)page1.getItems());

			Page<DataRecord> page2 =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(dataRecord2, (List<DataRecord>)page2.getItems());

			Page<DataRecord> page3 =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(dataRecord3, (List<DataRecord>)page3.getItems());
		}
		else {
			Page<DataRecord> page1 =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<DataRecord> dataRecords1 = (List<DataRecord>)page1.getItems();

			Assert.assertEquals(
				dataRecords1.toString(), totalCount + 2, dataRecords1.size());

			Page<DataRecord> page2 =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DataRecord> dataRecords2 = (List<DataRecord>)page2.getItems();

			Assert.assertEquals(
				dataRecords2.toString(), 1, dataRecords2.size());

			Page<DataRecord> page3 =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(dataRecord1, (List<DataRecord>)page3.getItems());
			assertContains(dataRecord2, (List<DataRecord>)page3.getItems());
			assertContains(dataRecord3, (List<DataRecord>)page3.getItems());
		}
	}

	@Test
	public void testGetDataRecordCollectionDataRecordsPageWithSortDateTime()
		throws Exception {

		testGetDataRecordCollectionDataRecordsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, dataRecord1, dataRecord2) -> {
				BeanTestUtil.setProperty(
					dataRecord1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDataRecordCollectionDataRecordsPageWithSortDouble()
		throws Exception {

		testGetDataRecordCollectionDataRecordsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, dataRecord1, dataRecord2) -> {
				BeanTestUtil.setProperty(
					dataRecord1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					dataRecord2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDataRecordCollectionDataRecordsPageWithSortInteger()
		throws Exception {

		testGetDataRecordCollectionDataRecordsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, dataRecord1, dataRecord2) -> {
				BeanTestUtil.setProperty(dataRecord1, entityField.getName(), 0);
				BeanTestUtil.setProperty(dataRecord2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDataRecordCollectionDataRecordsPageWithSortString()
		throws Exception {

		testGetDataRecordCollectionDataRecordsPageWithSort(
			EntityField.Type.STRING,
			(entityField, dataRecord1, dataRecord2) -> {
				Class<?> clazz = dataRecord1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						dataRecord1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						dataRecord2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						dataRecord1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						dataRecord2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						dataRecord1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						dataRecord2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDataRecordCollectionDataRecordsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, DataRecord, DataRecord, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();

		DataRecord dataRecord1 = randomDataRecord();
		DataRecord dataRecord2 = randomDataRecord();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, dataRecord1, dataRecord2);
		}

		dataRecord1 = testGetDataRecordCollectionDataRecordsPage_addDataRecord(
			dataRecordCollectionId, dataRecord1);

		dataRecord2 = testGetDataRecordCollectionDataRecordsPage_addDataRecord(
			dataRecordCollectionId, dataRecord2);

		Page<DataRecord> page =
			dataRecordResource.getDataRecordCollectionDataRecordsPage(
				dataRecordCollectionId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DataRecord> ascPage =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(dataRecord1, (List<DataRecord>)ascPage.getItems());
			assertContains(dataRecord2, (List<DataRecord>)ascPage.getItems());

			Page<DataRecord> descPage =
				dataRecordResource.getDataRecordCollectionDataRecordsPage(
					dataRecordCollectionId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(dataRecord2, (List<DataRecord>)descPage.getItems());
			assertContains(dataRecord1, (List<DataRecord>)descPage.getItems());
		}
	}

	protected DataRecord
			testGetDataRecordCollectionDataRecordsPage_addDataRecord(
				Long dataRecordCollectionId, DataRecord dataRecord)
		throws Exception {

		return dataRecordResource.postDataRecordCollectionDataRecord(
			dataRecordCollectionId, dataRecord);
	}

	protected Long
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDataRecordCollectionDataRecordsPage_getIrrelevantDataRecordCollectionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDataRecordCollectionDataRecordsPage()
		throws Exception {

		Long dataRecordCollectionId =
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId();

		GraphQLField graphQLField = new GraphQLField(
			"dataRecordCollectionDataRecords",
			new HashMap<String, Object>() {
				{
					put("dataRecordCollectionId", dataRecordCollectionId);
					put("keywords", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject dataRecordCollectionDataRecordsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/dataRecordCollectionDataRecords");

		long totalCount = dataRecordCollectionDataRecordsJSONObject.getLong(
			"totalCount");

		DataRecord dataRecord1 =
			testGraphQLDataRecordCollectionDataRecord_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		DataRecord dataRecord2 =
			testGraphQLDataRecordCollectionDataRecord_addDataRecord(
				dataRecordCollectionId, randomDataRecord());

		dataRecordCollectionDataRecordsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/dataRecordCollectionDataRecords");

		Assert.assertEquals(
			totalCount + 2,
			dataRecordCollectionDataRecordsJSONObject.getLong("totalCount"));

		assertContains(
			dataRecord1,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataRecordCollectionDataRecordsJSONObject.getString(
						"items"))));
		assertContains(
			dataRecord2,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataRecordCollectionDataRecordsJSONObject.getString(
						"items"))));

		// Using the namespace dataEngine_v2_0

		dataRecordCollectionDataRecordsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("dataEngine_v2_0", graphQLField)),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"JSONObject/dataRecordCollectionDataRecords");

		Assert.assertEquals(
			totalCount + 2,
			dataRecordCollectionDataRecordsJSONObject.getLong("totalCount"));

		assertContains(
			dataRecord1,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataRecordCollectionDataRecordsJSONObject.getString(
						"items"))));
		assertContains(
			dataRecord2,
			Arrays.asList(
				DataRecordSerDes.toDTOs(
					dataRecordCollectionDataRecordsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testPatchDataRecord() throws Exception {
		DataRecord postDataRecord = testPatchDataRecord_addDataRecord();

		DataRecord randomPatchDataRecord = randomPatchDataRecord();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataRecord patchDataRecord = dataRecordResource.patchDataRecord(
			postDataRecord.getId(), randomPatchDataRecord);

		DataRecord expectedPatchDataRecord = postDataRecord.clone();

		BeanTestUtil.copyProperties(
			randomPatchDataRecord, expectedPatchDataRecord);

		DataRecord getDataRecord = dataRecordResource.getDataRecord(
			patchDataRecord.getId());

		assertEquals(expectedPatchDataRecord, getDataRecord);
		assertValid(getDataRecord);
	}

	protected DataRecord testPatchDataRecord_addDataRecord() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDataDefinitionDataRecord() throws Exception {
		DataRecord randomDataRecord = randomDataRecord();

		DataRecord postDataRecord =
			testPostDataDefinitionDataRecord_addDataRecord(randomDataRecord);

		assertEquals(randomDataRecord, postDataRecord);
		assertValid(postDataRecord);
	}

	protected DataRecord testPostDataDefinitionDataRecord_addDataRecord(
			DataRecord dataRecord)
		throws Exception {

		return dataRecordResource.postDataDefinitionDataRecord(
			testGetDataDefinitionDataRecordsPage_getDataDefinitionId(),
			dataRecord);
	}

	@Test
	public void testGraphQLPostDataDefinitionDataRecord() throws Exception {
		DataRecord randomDataRecord = randomDataRecord();

		DataRecord dataRecord =
			testGraphQLDataDefinitionDataRecord_addDataRecord(
				testGraphQLPostDataDefinitionDataRecord_getDataDefinitionId(),
				randomDataRecord);

		Assert.assertTrue(equals(randomDataRecord, dataRecord));
	}

	protected Long testGraphQLPostDataDefinitionDataRecord_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostDataRecordCollectionDataRecord() throws Exception {
		DataRecord randomDataRecord = randomDataRecord();

		DataRecord postDataRecord =
			testPostDataRecordCollectionDataRecord_addDataRecord(
				randomDataRecord);

		assertEquals(randomDataRecord, postDataRecord);
		assertValid(postDataRecord);
	}

	protected DataRecord testPostDataRecordCollectionDataRecord_addDataRecord(
			DataRecord dataRecord)
		throws Exception {

		return dataRecordResource.postDataRecordCollectionDataRecord(
			testGetDataRecordCollectionDataRecordsPage_getDataRecordCollectionId(),
			dataRecord);
	}

	@Test
	public void testGraphQLPostDataRecordCollectionDataRecord()
		throws Exception {

		DataRecord randomDataRecord = randomDataRecord();

		DataRecord dataRecord =
			testGraphQLDataRecordCollectionDataRecord_addDataRecord(
				testGraphQLPostDataRecordCollectionDataRecord_getDataRecordCollectionId(
					randomDataRecord),
				randomDataRecord);

		Assert.assertTrue(equals(randomDataRecord, dataRecord));
	}

	protected Long
			testGraphQLPostDataRecordCollectionDataRecord_getDataRecordCollectionId(
				DataRecord dataRecord)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutDataRecord() throws Exception {
		DataRecord postDataRecord = testPutDataRecord_addDataRecord();

		DataRecord randomDataRecord = randomDataRecord();

		DataRecord putDataRecord = dataRecordResource.putDataRecord(
			postDataRecord.getId(), randomDataRecord);

		assertEquals(randomDataRecord, putDataRecord);
		assertValid(putDataRecord);

		DataRecord getDataRecord = dataRecordResource.getDataRecord(
			putDataRecord.getId());

		assertEquals(randomDataRecord, getDataRecord);
		assertValid(getDataRecord);
	}

	protected DataRecord testPutDataRecord_addDataRecord() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DataRecord dataRecord1 =
			testBatchEngineDeleteImportTask_addDataRecord();

		testBatchEngineDeleteImportTask_deleteDataRecord(
			200, null, dataRecord1.getId());

		assertHttpResponseStatusCode(
			404,
			dataRecordResource.getDataRecordHttpResponse(dataRecord1.getId()));
	}

	protected DataRecord testBatchEngineDeleteImportTask_addDataRecord()
		throws Exception {

		return testDeleteDataRecord_addDataRecord();
	}

	protected void testBatchEngineDeleteImportTask_deleteDataRecord(
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
				"com.liferay.data.engine.rest.dto.v2_0.DataRecord", null, null,
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

	protected DataRecord testGraphQLDataRecord_addDataRecord()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataRecord testGraphQLDataDefinitionDataRecord_addDataRecord()
		throws Exception {

		return testGraphQLDataDefinitionDataRecord_addDataRecord(
			testGraphQLDataDefinitionDataRecord_getDataDefinitionId(),
			randomDataRecord());
	}

	protected Long testGraphQLDataDefinitionDataRecord_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataRecord testGraphQLDataDefinitionDataRecord_addDataRecord(
			Long dataDefinitionId, DataRecord dataRecord)
		throws Exception {

		JSONDeserializer<DataRecord> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DataRecord.class)) {

			if (getGraphQLValue(field.get(dataRecord)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(dataRecord)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDataDefinitionDataRecord",
						new HashMap<String, Object>() {
							{
								put("dataDefinitionId", dataDefinitionId);
								put("dataRecord", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createDataDefinitionDataRecord"),
			DataRecord.class);
	}

	protected DataRecord
			testGraphQLDataRecordCollectionDataRecord_addDataRecord()
		throws Exception {

		return testGraphQLDataRecordCollectionDataRecord_addDataRecord(
			testGraphQLDataRecordCollectionDataRecord_getDataRecordCollectionId(),
			randomDataRecord());
	}

	protected Long
			testGraphQLDataRecordCollectionDataRecord_getDataRecordCollectionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataRecord
			testGraphQLDataRecordCollectionDataRecord_addDataRecord(
				Long dataRecordCollectionId, DataRecord dataRecord)
		throws Exception {

		JSONDeserializer<DataRecord> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DataRecord.class)) {

			if (getGraphQLValue(field.get(dataRecord)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(dataRecord)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDataRecordCollectionDataRecord",
						new HashMap<String, Object>() {
							{
								put(
									"dataRecordCollectionId",
									dataRecordCollectionId);
								put("dataRecord", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createDataRecordCollectionDataRecord"),
			DataRecord.class);
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
		DataRecord dataRecord, List<DataRecord> dataRecords) {

		boolean contains = false;

		for (DataRecord item : dataRecords) {
			if (equals(dataRecord, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			dataRecords + " does not contain " + dataRecord, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DataRecord dataRecord1, DataRecord dataRecord2) {

		Assert.assertTrue(
			dataRecord1 + " does not equal " + dataRecord2,
			equals(dataRecord1, dataRecord2));
	}

	protected void assertEquals(
		List<DataRecord> dataRecords1, List<DataRecord> dataRecords2) {

		Assert.assertEquals(dataRecords1.size(), dataRecords2.size());

		for (int i = 0; i < dataRecords1.size(); i++) {
			DataRecord dataRecord1 = dataRecords1.get(i);
			DataRecord dataRecord2 = dataRecords2.get(i);

			assertEquals(dataRecord1, dataRecord2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DataRecord> dataRecords1, List<DataRecord> dataRecords2) {

		Assert.assertEquals(dataRecords1.size(), dataRecords2.size());

		for (DataRecord dataRecord1 : dataRecords1) {
			boolean contains = false;

			for (DataRecord dataRecord2 : dataRecords2) {
				if (equals(dataRecord1, dataRecord2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				dataRecords2 + " does not contain " + dataRecord1, contains);
		}
	}

	protected void assertValid(DataRecord dataRecord) throws Exception {
		boolean valid = true;

		if (dataRecord.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"dataRecordCollectionId", additionalAssertFieldName)) {

				if (dataRecord.getDataRecordCollectionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataRecordValues", additionalAssertFieldName)) {
				if (dataRecord.getDataRecordValues() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (dataRecord.getStatus() == null) {
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

	protected void assertValid(Page<DataRecord> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DataRecord> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DataRecord> dataRecords = page.getItems();

		int size = dataRecords.size();

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
					com.liferay.data.engine.rest.dto.v2_0.DataRecord.class)) {

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

	protected boolean equals(DataRecord dataRecord1, DataRecord dataRecord2) {
		if (dataRecord1 == dataRecord2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"dataRecordCollectionId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataRecord1.getDataRecordCollectionId(),
						dataRecord2.getDataRecordCollectionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataRecordValues", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataRecord1.getDataRecordValues(),
						(Map)dataRecord2.getDataRecordValues())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataRecord1.getId(), dataRecord2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataRecord1.getStatus(), dataRecord2.getStatus())) {

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

		if (!(_dataRecordResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_dataRecordResource;

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
		EntityField entityField, String operator, DataRecord dataRecord) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dataRecordCollectionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataRecordValues")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			sb.append(String.valueOf(dataRecord.getStatus()));

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

	protected DataRecord randomDataRecord() throws Exception {
		return new DataRecord() {
			{
				dataRecordCollectionId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				status = RandomTestUtil.randomInt();
			}
		};
	}

	protected DataRecord randomIrrelevantDataRecord() throws Exception {
		DataRecord randomIrrelevantDataRecord = randomDataRecord();

		return randomIrrelevantDataRecord;
	}

	protected DataRecord randomPatchDataRecord() throws Exception {
		return randomDataRecord();
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

	protected DataRecordResource dataRecordResource;
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
		LogFactoryUtil.getLog(BaseDataRecordResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.data.engine.rest.resource.v2_0.DataRecordResource
		_dataRecordResource;

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