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

import com.liferay.data.engine.rest.client.dto.v2_0.DataRecordCollection;
import com.liferay.data.engine.rest.client.http.HttpInvoker;
import com.liferay.data.engine.rest.client.pagination.Page;
import com.liferay.data.engine.rest.client.pagination.Pagination;
import com.liferay.data.engine.rest.client.permission.Permission;
import com.liferay.data.engine.rest.client.resource.v2_0.DataRecordCollectionResource;
import com.liferay.data.engine.rest.client.serdes.v2_0.DataRecordCollectionSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
import com.liferay.portal.kernel.json.JSONDeserializer;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.model.RoleConstants;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ResourceActionLocalService;
import com.liferay.portal.kernel.service.ResourcePermissionLocalService;
import com.liferay.portal.kernel.service.RoleLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.RoleTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.DateUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
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
public abstract class BaseDataRecordCollectionResourceTestCase {

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

		_dataRecordCollectionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		dataRecordCollectionResource = DataRecordCollectionResource.builder(
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

		DataRecordCollection dataRecordCollection1 =
			randomDataRecordCollection();

		String json = objectMapper.writeValueAsString(dataRecordCollection1);

		DataRecordCollection dataRecordCollection2 =
			DataRecordCollectionSerDes.toDTO(json);

		Assert.assertTrue(equals(dataRecordCollection1, dataRecordCollection2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DataRecordCollection dataRecordCollection =
			randomDataRecordCollection();

		String json1 = objectMapper.writeValueAsString(dataRecordCollection);
		String json2 = DataRecordCollectionSerDes.toJSON(dataRecordCollection);

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

		DataRecordCollection dataRecordCollection =
			randomDataRecordCollection();

		dataRecordCollection.setDataRecordCollectionKey(regex);

		String json = DataRecordCollectionSerDes.toJSON(dataRecordCollection);

		Assert.assertFalse(json.contains(regex));

		dataRecordCollection = DataRecordCollectionSerDes.toDTO(json);

		Assert.assertEquals(
			regex, dataRecordCollection.getDataRecordCollectionKey());
	}

	@Test
	public void testDeleteDataRecordCollection() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataRecordCollection dataRecordCollection =
			testDeleteDataRecordCollection_addDataRecordCollection();

		assertHttpResponseStatusCode(
			204,
			dataRecordCollectionResource.deleteDataRecordCollectionHttpResponse(
				dataRecordCollection.getId()));

		assertHttpResponseStatusCode(
			404,
			dataRecordCollectionResource.getDataRecordCollectionHttpResponse(
				dataRecordCollection.getId()));
		assertHttpResponseStatusCode(
			404,
			dataRecordCollectionResource.getDataRecordCollectionHttpResponse(
				0L));
	}

	protected DataRecordCollection
			testDeleteDataRecordCollection_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteDataRecordCollection() throws Exception {

		// No namespace

		DataRecordCollection dataRecordCollection1 =
			testGraphQLDeleteDataRecordCollection_addDataRecordCollection();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDataRecordCollection",
						new HashMap<String, Object>() {
							{
								put(
									"dataRecordCollectionId",
									dataRecordCollection1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDataRecordCollection"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataRecordCollection",
					new HashMap<String, Object>() {
						{
							put(
								"dataRecordCollectionId",
								dataRecordCollection1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace dataEngine_v2_0

		DataRecordCollection dataRecordCollection2 =
			testGraphQLDeleteDataRecordCollection_addDataRecordCollection();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"dataEngine_v2_0",
						new GraphQLField(
							"deleteDataRecordCollection",
							new HashMap<String, Object>() {
								{
									put(
										"dataRecordCollectionId",
										dataRecordCollection2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"Object/deleteDataRecordCollection"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"dataEngine_v2_0",
					new GraphQLField(
						"dataRecordCollection",
						new HashMap<String, Object>() {
							{
								put(
									"dataRecordCollectionId",
									dataRecordCollection2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DataRecordCollection
			testGraphQLDeleteDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return testGraphQLDataRecordCollection_addDataRecordCollection();
	}

	@Test
	public void testDeleteDataRecordCollectionBatch() throws Exception {
		DataRecordCollection dataRecordCollection1 =
			testDeleteDataRecordCollectionBatch_addDataRecordCollection();

		testDeleteDataRecordCollectionBatch_deleteDataRecordCollection(
			202, null, dataRecordCollection1.getId());

		assertHttpResponseStatusCode(
			404,
			dataRecordCollectionResource.getDataRecordCollectionHttpResponse(
				dataRecordCollection1.getId()));
	}

	protected DataRecordCollection
			testDeleteDataRecordCollectionBatch_addDataRecordCollection()
		throws Exception {

		return testDeleteDataRecordCollection_addDataRecordCollection();
	}

	protected void
			testDeleteDataRecordCollectionBatch_deleteDataRecordCollection(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			dataRecordCollectionResource.
				deleteDataRecordCollectionBatchHttpResponse(
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
	public void testGetDataDefinitionDataRecordCollection() throws Exception {
		DataRecordCollection postDataRecordCollection =
			testGetDataDefinitionDataRecordCollection_addDataRecordCollection();

		DataRecordCollection getDataRecordCollection =
			dataRecordCollectionResource.getDataDefinitionDataRecordCollection(
				testGetDataDefinitionDataRecordCollection_getDataDefinitionId(
					postDataRecordCollection));

		assertEquals(postDataRecordCollection, getDataRecordCollection);
		assertValid(getDataRecordCollection);
	}

	protected DataRecordCollection
			testGetDataDefinitionDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return testPostDataDefinitionDataRecordCollection_addDataRecordCollection(
			randomDataRecordCollection());
	}

	protected Long
			testGetDataDefinitionDataRecordCollection_getDataDefinitionId(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDataDefinitionDataRecordCollection()
		throws Exception {

		DataRecordCollection dataRecordCollection =
			testGraphQLGetDataDefinitionDataRecordCollection_addDataRecordCollection();

		// No namespace

		Assert.assertTrue(
			equals(
				dataRecordCollection,
				DataRecordCollectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataDefinitionDataRecordCollection",
								new HashMap<String, Object>() {
									{
										put(
											"dataDefinitionId",
											testGraphQLGetDataDefinitionDataRecordCollection_getDataDefinitionId(
												dataRecordCollection));
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/dataDefinitionDataRecordCollection"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataRecordCollection,
				DataRecordCollectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataDefinitionDataRecordCollection",
									new HashMap<String, Object>() {
										{
											put(
												"dataDefinitionId",
												testGraphQLGetDataDefinitionDataRecordCollection_getDataDefinitionId(
													dataRecordCollection));
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataDefinitionDataRecordCollection"))));
	}

	protected Long
			testGraphQLGetDataDefinitionDataRecordCollection_getDataDefinitionId(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDataDefinitionDataRecordCollectionNotFound()
		throws Exception {

		Long irrelevantDataDefinitionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataDefinitionDataRecordCollection",
						new HashMap<String, Object>() {
							{
								put(
									"dataDefinitionId",
									irrelevantDataDefinitionId);
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
							"dataDefinitionDataRecordCollection",
							new HashMap<String, Object>() {
								{
									put(
										"dataDefinitionId",
										irrelevantDataDefinitionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataRecordCollection
			testGraphQLGetDataDefinitionDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection();
	}

	@Test
	public void testGetDataDefinitionDataRecordCollectionsPage()
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId();
		Long irrelevantDataDefinitionId =
			testGetDataDefinitionDataRecordCollectionsPage_getIrrelevantDataDefinitionId();

		Page<DataRecordCollection> page =
			dataRecordCollectionResource.
				getDataDefinitionDataRecordCollectionsPage(
					dataDefinitionId, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantDataDefinitionId != null) {
			DataRecordCollection irrelevantDataRecordCollection =
				testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
					irrelevantDataDefinitionId,
					randomIrrelevantDataRecordCollection());

			page =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						irrelevantDataDefinitionId, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDataRecordCollection,
				(List<DataRecordCollection>)page.getItems());
			assertValid(
				page,
				testGetDataDefinitionDataRecordCollectionsPage_getExpectedActions(
					irrelevantDataDefinitionId));
		}

		DataRecordCollection dataRecordCollection1 =
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		DataRecordCollection dataRecordCollection2 =
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		page =
			dataRecordCollectionResource.
				getDataDefinitionDataRecordCollectionsPage(
					dataDefinitionId, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			dataRecordCollection1, (List<DataRecordCollection>)page.getItems());
		assertContains(
			dataRecordCollection2, (List<DataRecordCollection>)page.getItems());
		assertValid(
			page,
			testGetDataDefinitionDataRecordCollectionsPage_getExpectedActions(
				dataDefinitionId));

		dataRecordCollectionResource.deleteDataRecordCollection(
			dataRecordCollection1.getId());

		dataRecordCollectionResource.deleteDataRecordCollection(
			dataRecordCollection2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDataDefinitionDataRecordCollectionsPage_getExpectedActions(
				Long dataDefinitionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/data-engine/v2.0/data-definitions/{dataDefinitionId}/data-record-collections/batch".
				replace(
					"{dataDefinitionId}", String.valueOf(dataDefinitionId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetDataDefinitionDataRecordCollectionsPageWithPagination()
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId();

		Page<DataRecordCollection> dataRecordCollectionsPage =
			dataRecordCollectionResource.
				getDataDefinitionDataRecordCollectionsPage(
					dataDefinitionId, null, null);

		int totalCount = GetterUtil.getInteger(
			dataRecordCollectionsPage.getTotalCount());

		DataRecordCollection dataRecordCollection1 =
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		DataRecordCollection dataRecordCollection2 =
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		DataRecordCollection dataRecordCollection3 =
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DataRecordCollection> page1 =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						dataDefinitionId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				dataRecordCollection1,
				(List<DataRecordCollection>)page1.getItems());

			Page<DataRecordCollection> page2 =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						dataDefinitionId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				dataRecordCollection2,
				(List<DataRecordCollection>)page2.getItems());

			Page<DataRecordCollection> page3 =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						dataDefinitionId, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				dataRecordCollection3,
				(List<DataRecordCollection>)page3.getItems());
		}
		else {
			Page<DataRecordCollection> page1 =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						dataDefinitionId, null,
						Pagination.of(1, totalCount + 2));

			List<DataRecordCollection> dataRecordCollections1 =
				(List<DataRecordCollection>)page1.getItems();

			Assert.assertEquals(
				dataRecordCollections1.toString(), totalCount + 2,
				dataRecordCollections1.size());

			Page<DataRecordCollection> page2 =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						dataDefinitionId, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DataRecordCollection> dataRecordCollections2 =
				(List<DataRecordCollection>)page2.getItems();

			Assert.assertEquals(
				dataRecordCollections2.toString(), 1,
				dataRecordCollections2.size());

			Page<DataRecordCollection> page3 =
				dataRecordCollectionResource.
					getDataDefinitionDataRecordCollectionsPage(
						dataDefinitionId, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				dataRecordCollection1,
				(List<DataRecordCollection>)page3.getItems());
			assertContains(
				dataRecordCollection2,
				(List<DataRecordCollection>)page3.getItems());
			assertContains(
				dataRecordCollection3,
				(List<DataRecordCollection>)page3.getItems());
		}
	}

	protected DataRecordCollection
			testGetDataDefinitionDataRecordCollectionsPage_addDataRecordCollection(
				Long dataDefinitionId,
				DataRecordCollection dataRecordCollection)
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				dataDefinitionId, dataRecordCollection);
	}

	protected Long
			testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDataDefinitionDataRecordCollectionsPage_getIrrelevantDataDefinitionId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDataDefinitionDataRecordCollectionsPage()
		throws Exception {

		Long dataDefinitionId =
			testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId();

		GraphQLField graphQLField = new GraphQLField(
			"dataDefinitionDataRecordCollections",
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

		JSONObject dataDefinitionDataRecordCollectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/dataDefinitionDataRecordCollections");

		long totalCount = dataDefinitionDataRecordCollectionsJSONObject.getLong(
			"totalCount");

		DataRecordCollection dataRecordCollection1 =
			testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		DataRecordCollection dataRecordCollection2 =
			testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection(
				dataDefinitionId, randomDataRecordCollection());

		dataDefinitionDataRecordCollectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/dataDefinitionDataRecordCollections");

		Assert.assertEquals(
			totalCount + 2,
			dataDefinitionDataRecordCollectionsJSONObject.getLong(
				"totalCount"));

		assertContains(
			dataRecordCollection1,
			Arrays.asList(
				DataRecordCollectionSerDes.toDTOs(
					dataDefinitionDataRecordCollectionsJSONObject.getString(
						"items"))));
		assertContains(
			dataRecordCollection2,
			Arrays.asList(
				DataRecordCollectionSerDes.toDTOs(
					dataDefinitionDataRecordCollectionsJSONObject.getString(
						"items"))));

		// Using the namespace dataEngine_v2_0

		dataDefinitionDataRecordCollectionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("dataEngine_v2_0", graphQLField)),
				"JSONObject/data", "JSONObject/dataEngine_v2_0",
				"JSONObject/dataDefinitionDataRecordCollections");

		Assert.assertEquals(
			totalCount + 2,
			dataDefinitionDataRecordCollectionsJSONObject.getLong(
				"totalCount"));

		assertContains(
			dataRecordCollection1,
			Arrays.asList(
				DataRecordCollectionSerDes.toDTOs(
					dataDefinitionDataRecordCollectionsJSONObject.getString(
						"items"))));
		assertContains(
			dataRecordCollection2,
			Arrays.asList(
				DataRecordCollectionSerDes.toDTOs(
					dataDefinitionDataRecordCollectionsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetDataRecordCollection() throws Exception {
		DataRecordCollection postDataRecordCollection =
			testGetDataRecordCollection_addDataRecordCollection();

		DataRecordCollection getDataRecordCollection =
			dataRecordCollectionResource.getDataRecordCollection(
				postDataRecordCollection.getId());

		assertEquals(postDataRecordCollection, getDataRecordCollection);
		assertValid(getDataRecordCollection);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DataRecordCollection postDataRecordCollection =
			testGetDataRecordCollection_addDataRecordCollection();

		DataRecordCollection getDataRecordCollection =
			dataRecordCollectionResource.getDataRecordCollection(
				postDataRecordCollection.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.data.engine.rest.dto.v2_0.DataRecordCollection"
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
			postDataRecordCollection.getId());

		assertEquals(
			getDataRecordCollection,
			DataRecordCollectionSerDes.toDTO(item.toString()));
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

	protected DataRecordCollection
			testGetDataRecordCollection_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetDataRecordCollection() throws Exception {
		DataRecordCollection dataRecordCollection =
			testGraphQLGetDataRecordCollection_addDataRecordCollection();

		// No namespace

		Assert.assertTrue(
			equals(
				dataRecordCollection,
				DataRecordCollectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataRecordCollection",
								new HashMap<String, Object>() {
									{
										put(
											"dataRecordCollectionId",
											dataRecordCollection.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/dataRecordCollection"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataRecordCollection,
				DataRecordCollectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataRecordCollection",
									new HashMap<String, Object>() {
										{
											put(
												"dataRecordCollectionId",
												dataRecordCollection.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataRecordCollection"))));
	}

	@Test
	public void testGraphQLGetDataRecordCollectionNotFound() throws Exception {
		Long irrelevantDataRecordCollectionId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataRecordCollection",
						new HashMap<String, Object>() {
							{
								put(
									"dataRecordCollectionId",
									irrelevantDataRecordCollectionId);
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
							"dataRecordCollection",
							new HashMap<String, Object>() {
								{
									put(
										"dataRecordCollectionId",
										irrelevantDataRecordCollectionId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataRecordCollection
			testGraphQLGetDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return testGraphQLDataRecordCollection_addDataRecordCollection();
	}

	@Test
	public void testGetDataRecordCollectionPermissionByCurrentUser()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetDataRecordCollectionPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataRecordCollection postDataRecordCollection =
			testGetDataRecordCollectionPermissionsPage_addDataRecordCollection();

		Page<Permission> page =
			dataRecordCollectionResource.getDataRecordCollectionPermissionsPage(
				postDataRecordCollection.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected DataRecordCollection
			testGetDataRecordCollectionPermissionsPage_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteDataRecordCollectionByDataRecordCollectionKey()
		throws Exception {

		DataRecordCollection postDataRecordCollection =
			testGetSiteDataRecordCollectionByDataRecordCollectionKey_addDataRecordCollection();

		DataRecordCollection getDataRecordCollection =
			dataRecordCollectionResource.
				getSiteDataRecordCollectionByDataRecordCollectionKey(
					postDataRecordCollection.getSiteId(),
					postDataRecordCollection.getDataRecordCollectionKey());

		assertEquals(postDataRecordCollection, getDataRecordCollection);
		assertValid(getDataRecordCollection);
	}

	protected DataRecordCollection
			testGetSiteDataRecordCollectionByDataRecordCollectionKey_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteDataRecordCollectionByDataRecordCollectionKey()
		throws Exception {

		DataRecordCollection dataRecordCollection =
			testGraphQLGetSiteDataRecordCollectionByDataRecordCollectionKey_addDataRecordCollection();

		// No namespace

		Assert.assertTrue(
			equals(
				dataRecordCollection,
				DataRecordCollectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataRecordCollectionByDataRecordCollectionKey",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												dataRecordCollection.
													getSiteId() + "\"");
										put(
											"dataRecordCollectionKey",
											"\"" +
												dataRecordCollection.
													getDataRecordCollectionKey() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/dataRecordCollectionByDataRecordCollectionKey"))));

		// Using the namespace dataEngine_v2_0

		Assert.assertTrue(
			equals(
				dataRecordCollection,
				DataRecordCollectionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"dataEngine_v2_0",
								new GraphQLField(
									"dataRecordCollectionByDataRecordCollectionKey",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													dataRecordCollection.
														getSiteId() + "\"");
											put(
												"dataRecordCollectionKey",
												"\"" +
													dataRecordCollection.
														getDataRecordCollectionKey() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/dataEngine_v2_0",
						"Object/dataRecordCollectionByDataRecordCollectionKey"))));
	}

	@Test
	public void testGraphQLGetSiteDataRecordCollectionByDataRecordCollectionKeyNotFound()
		throws Exception {

		String irrelevantDataRecordCollectionKey =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"dataRecordCollectionByDataRecordCollectionKey",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"dataRecordCollectionKey",
									irrelevantDataRecordCollectionKey);
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
							"dataRecordCollectionByDataRecordCollectionKey",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"dataRecordCollectionKey",
										irrelevantDataRecordCollectionKey);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DataRecordCollection
			testGraphQLGetSiteDataRecordCollectionByDataRecordCollectionKey_addDataRecordCollection()
		throws Exception {

		return testGraphQLSiteDataRecordCollection_addDataRecordCollection();
	}

	@Test
	public void testPostDataDefinitionDataRecordCollection() throws Exception {
		DataRecordCollection randomDataRecordCollection =
			randomDataRecordCollection();

		DataRecordCollection postDataRecordCollection =
			testPostDataDefinitionDataRecordCollection_addDataRecordCollection(
				randomDataRecordCollection);

		assertEquals(randomDataRecordCollection, postDataRecordCollection);
		assertValid(postDataRecordCollection);
	}

	protected DataRecordCollection
			testPostDataDefinitionDataRecordCollection_addDataRecordCollection(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		return dataRecordCollectionResource.
			postDataDefinitionDataRecordCollection(
				testGetDataDefinitionDataRecordCollectionsPage_getDataDefinitionId(),
				dataRecordCollection);
	}

	@Test
	public void testGraphQLPostDataDefinitionDataRecordCollection()
		throws Exception {

		DataRecordCollection randomDataRecordCollection =
			randomDataRecordCollection();

		DataRecordCollection dataRecordCollection =
			testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection(
				testGraphQLPostDataDefinitionDataRecordCollection_getDataDefinitionId(
					randomDataRecordCollection),
				randomDataRecordCollection);

		Assert.assertTrue(
			equals(randomDataRecordCollection, dataRecordCollection));
	}

	protected Long
			testGraphQLPostDataDefinitionDataRecordCollection_getDataDefinitionId(
				DataRecordCollection dataRecordCollection)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutDataRecordCollection() throws Exception {
		DataRecordCollection postDataRecordCollection =
			testPutDataRecordCollection_addDataRecordCollection();

		DataRecordCollection randomDataRecordCollection =
			randomDataRecordCollection();

		DataRecordCollection putDataRecordCollection =
			dataRecordCollectionResource.putDataRecordCollection(
				postDataRecordCollection.getId(), randomDataRecordCollection);

		assertEquals(randomDataRecordCollection, putDataRecordCollection);
		assertValid(putDataRecordCollection);

		DataRecordCollection getDataRecordCollection =
			dataRecordCollectionResource.getDataRecordCollection(
				putDataRecordCollection.getId());

		assertEquals(randomDataRecordCollection, getDataRecordCollection);
		assertValid(getDataRecordCollection);
	}

	protected DataRecordCollection
			testPutDataRecordCollection_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutDataRecordCollectionPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DataRecordCollection dataRecordCollection =
			testPutDataRecordCollectionPermissionsPage_addDataRecordCollection();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			dataRecordCollectionResource.
				putDataRecordCollectionPermissionsPageHttpResponse(
					dataRecordCollection.getId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"VIEW"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			dataRecordCollectionResource.
				putDataRecordCollectionPermissionsPageHttpResponse(
					0L,
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected DataRecordCollection
			testPutDataRecordCollectionPermissionsPage_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DataRecordCollection dataRecordCollection1 =
			testBatchEngineDeleteImportTask_addDataRecordCollection();

		testBatchEngineDeleteImportTask_deleteDataRecordCollection(
			200, null, dataRecordCollection1.getId());

		assertHttpResponseStatusCode(
			404,
			dataRecordCollectionResource.getDataRecordCollectionHttpResponse(
				dataRecordCollection1.getId()));
	}

	protected DataRecordCollection
			testBatchEngineDeleteImportTask_addDataRecordCollection()
		throws Exception {

		return testDeleteDataRecordCollection_addDataRecordCollection();
	}

	protected void testBatchEngineDeleteImportTask_deleteDataRecordCollection(
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
				"com.liferay.data.engine.rest.dto.v2_0.DataRecordCollection",
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

	protected DataRecordCollection
			testGraphQLDataRecordCollection_addDataRecordCollection()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataRecordCollection
			testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection()
		throws Exception {

		return testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection(
			testGraphQLDataDefinitionDataRecordCollection_getDataDefinitionId(),
			randomDataRecordCollection());
	}

	protected Long
			testGraphQLDataDefinitionDataRecordCollection_getDataDefinitionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected DataRecordCollection
			testGraphQLDataDefinitionDataRecordCollection_addDataRecordCollection(
				Long dataDefinitionId,
				DataRecordCollection dataRecordCollection)
		throws Exception {

		JSONDeserializer<DataRecordCollection> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DataRecordCollection.class)) {

			if (getGraphQLValue(field.get(dataRecordCollection)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(dataRecordCollection)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createDataDefinitionDataRecordCollection",
						new HashMap<String, Object>() {
							{
								put("dataDefinitionId", dataDefinitionId);
								put("dataRecordCollection", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createDataDefinitionDataRecordCollection"),
			DataRecordCollection.class);
	}

	protected DataRecordCollection
			testGraphQLSiteDataRecordCollection_addDataRecordCollection()
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
		DataRecordCollection dataRecordCollection,
		List<DataRecordCollection> dataRecordCollections) {

		boolean contains = false;

		for (DataRecordCollection item : dataRecordCollections) {
			if (equals(dataRecordCollection, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			dataRecordCollections + " does not contain " + dataRecordCollection,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DataRecordCollection dataRecordCollection1,
		DataRecordCollection dataRecordCollection2) {

		Assert.assertTrue(
			dataRecordCollection1 + " does not equal " + dataRecordCollection2,
			equals(dataRecordCollection1, dataRecordCollection2));
	}

	protected void assertEquals(
		List<DataRecordCollection> dataRecordCollections1,
		List<DataRecordCollection> dataRecordCollections2) {

		Assert.assertEquals(
			dataRecordCollections1.size(), dataRecordCollections2.size());

		for (int i = 0; i < dataRecordCollections1.size(); i++) {
			DataRecordCollection dataRecordCollection1 =
				dataRecordCollections1.get(i);
			DataRecordCollection dataRecordCollection2 =
				dataRecordCollections2.get(i);

			assertEquals(dataRecordCollection1, dataRecordCollection2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DataRecordCollection> dataRecordCollections1,
		List<DataRecordCollection> dataRecordCollections2) {

		Assert.assertEquals(
			dataRecordCollections1.size(), dataRecordCollections2.size());

		for (DataRecordCollection dataRecordCollection1 :
				dataRecordCollections1) {

			boolean contains = false;

			for (DataRecordCollection dataRecordCollection2 :
					dataRecordCollections2) {

				if (equals(dataRecordCollection1, dataRecordCollection2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				dataRecordCollections2 + " does not contain " +
					dataRecordCollection1,
				contains);
		}
	}

	protected void assertValid(DataRecordCollection dataRecordCollection)
		throws Exception {

		boolean valid = true;

		if (dataRecordCollection.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				dataRecordCollection.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dataDefinitionId", additionalAssertFieldName)) {
				if (dataRecordCollection.getDataDefinitionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dataRecordCollectionKey", additionalAssertFieldName)) {

				if (dataRecordCollection.getDataRecordCollectionKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (dataRecordCollection.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (dataRecordCollection.getName() == null) {
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

	protected void assertValid(Page<DataRecordCollection> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DataRecordCollection> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DataRecordCollection> dataRecordCollections =
			page.getItems();

		int size = dataRecordCollections.size();

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
					com.liferay.data.engine.rest.dto.v2_0.DataRecordCollection.
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
		DataRecordCollection dataRecordCollection1,
		DataRecordCollection dataRecordCollection2) {

		if (dataRecordCollection1 == dataRecordCollection2) {
			return true;
		}

		if (!Objects.equals(
				dataRecordCollection1.getSiteId(),
				dataRecordCollection2.getSiteId())) {

			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("dataDefinitionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataRecordCollection1.getDataDefinitionId(),
						dataRecordCollection2.getDataDefinitionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dataRecordCollectionKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						dataRecordCollection1.getDataRecordCollectionKey(),
						dataRecordCollection2.getDataRecordCollectionKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataRecordCollection1.getDescription(),
						(Map)dataRecordCollection2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						dataRecordCollection1.getId(),
						dataRecordCollection2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!equals(
						(Map)dataRecordCollection1.getName(),
						(Map)dataRecordCollection2.getName())) {

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

		if (!(_dataRecordCollectionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_dataRecordCollectionResource;

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
		DataRecordCollection dataRecordCollection) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("dataDefinitionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataRecordCollectionKey")) {
			Object object = dataRecordCollection.getDataRecordCollectionKey();

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

		if (entityFieldName.equals("siteId")) {
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

	protected DataRecordCollection randomDataRecordCollection()
		throws Exception {

		return new DataRecordCollection() {
			{
				dataDefinitionId = RandomTestUtil.randomLong();
				dataRecordCollectionKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected DataRecordCollection randomIrrelevantDataRecordCollection()
		throws Exception {

		DataRecordCollection randomIrrelevantDataRecordCollection =
			randomDataRecordCollection();

		randomIrrelevantDataRecordCollection.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantDataRecordCollection;
	}

	protected DataRecordCollection randomPatchDataRecordCollection()
		throws Exception {

		return randomDataRecordCollection();
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

	protected DataRecordCollectionResource dataRecordCollectionResource;
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
		LogFactoryUtil.getLog(BaseDataRecordCollectionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.data.engine.rest.resource.v2_0.DataRecordCollectionResource
			_dataRecordCollectionResource;

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