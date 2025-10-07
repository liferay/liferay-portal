/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.delivery.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.delivery.client.dto.v1_0.DocumentDataDefinitionType;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentDataDefinitionTypeResource;
import com.liferay.headless.delivery.client.serdes.v1_0.DocumentDataDefinitionTypeSerDes;
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
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
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
public abstract class BaseDocumentDataDefinitionTypeResourceTestCase {

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

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

		_documentDataDefinitionTypeResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		documentDataDefinitionTypeResource =
			DocumentDataDefinitionTypeResource.builder(
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

		DocumentDataDefinitionType documentDataDefinitionType1 =
			randomDocumentDataDefinitionType();

		String json = objectMapper.writeValueAsString(
			documentDataDefinitionType1);

		DocumentDataDefinitionType documentDataDefinitionType2 =
			DocumentDataDefinitionTypeSerDes.toDTO(json);

		Assert.assertTrue(
			equals(documentDataDefinitionType1, documentDataDefinitionType2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DocumentDataDefinitionType documentDataDefinitionType =
			randomDocumentDataDefinitionType();

		String json1 = objectMapper.writeValueAsString(
			documentDataDefinitionType);
		String json2 = DocumentDataDefinitionTypeSerDes.toJSON(
			documentDataDefinitionType);

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

		DocumentDataDefinitionType documentDataDefinitionType =
			randomDocumentDataDefinitionType();

		documentDataDefinitionType.setAssetLibraryKey(regex);
		documentDataDefinitionType.setDescription(regex);
		documentDataDefinitionType.setExternalReferenceCode(regex);
		documentDataDefinitionType.setName(regex);

		String json = DocumentDataDefinitionTypeSerDes.toJSON(
			documentDataDefinitionType);

		Assert.assertFalse(json.contains(regex));

		documentDataDefinitionType = DocumentDataDefinitionTypeSerDes.toDTO(
			json);

		Assert.assertEquals(
			regex, documentDataDefinitionType.getAssetLibraryKey());
		Assert.assertEquals(regex, documentDataDefinitionType.getDescription());
		Assert.assertEquals(
			regex, documentDataDefinitionType.getExternalReferenceCode());
		Assert.assertEquals(regex, documentDataDefinitionType.getName());
	}

	@Test
	public void testDeleteDocumentDataDefinitionType() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentDataDefinitionType documentDataDefinitionType =
			testDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType();

		assertHttpResponseStatusCode(
			204,
			documentDataDefinitionTypeResource.
				deleteDocumentDataDefinitionTypeHttpResponse(
					documentDataDefinitionType.getId()));

		assertHttpResponseStatusCode(
			404,
			documentDataDefinitionTypeResource.
				getDocumentDataDefinitionTypeHttpResponse(
					documentDataDefinitionType.getId()));
		assertHttpResponseStatusCode(
			404,
			documentDataDefinitionTypeResource.
				getDocumentDataDefinitionTypeHttpResponse(0L));
	}

	protected DocumentDataDefinitionType
			testDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return documentDataDefinitionTypeResource.
			postSiteDocumentDataDefinitionType(
				testGroup.getGroupId(), randomDocumentDataDefinitionType());
	}

	@Test
	public void testGraphQLDeleteDocumentDataDefinitionType() throws Exception {

		// No namespace

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGraphQLDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocumentDataDefinitionType",
						new HashMap<String, Object>() {
							{
								put(
									"documentDataDefinitionTypeId",
									documentDataDefinitionType1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocumentDataDefinitionType"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentDataDefinitionType",
					new HashMap<String, Object>() {
						{
							put(
								"documentDataDefinitionTypeId",
								documentDataDefinitionType1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGraphQLDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocumentDataDefinitionType",
							new HashMap<String, Object>() {
								{
									put(
										"documentDataDefinitionTypeId",
										documentDataDefinitionType2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocumentDataDefinitionType"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentDataDefinitionType",
						new HashMap<String, Object>() {
							{
								put(
									"documentDataDefinitionTypeId",
									documentDataDefinitionType2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentDataDefinitionType
			testGraphQLDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return testGraphQLDocumentDataDefinitionType_addDocumentDataDefinitionType();
	}

	@Test
	public void testDeleteDocumentDataDefinitionTypeBatch() throws Exception {
		DocumentDataDefinitionType documentDataDefinitionType1 =
			testDeleteDocumentDataDefinitionTypeBatch_addDocumentDataDefinitionType();

		testDeleteDocumentDataDefinitionTypeBatch_deleteDocumentDataDefinitionType(
			202, null, documentDataDefinitionType1.getId());

		assertHttpResponseStatusCode(
			404,
			documentDataDefinitionTypeResource.
				getDocumentDataDefinitionTypeHttpResponse(
					documentDataDefinitionType1.getId()));
	}

	protected DocumentDataDefinitionType
			testDeleteDocumentDataDefinitionTypeBatch_addDocumentDataDefinitionType()
		throws Exception {

		return testDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType();
	}

	protected void
			testDeleteDocumentDataDefinitionTypeBatch_deleteDocumentDataDefinitionType(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			documentDataDefinitionTypeResource.
				deleteDocumentDataDefinitionTypeBatchHttpResponse(
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
	public void testGetAssetLibraryDocumentDataDefinitionTypesPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getIrrelevantAssetLibraryId();

		Page<DocumentDataDefinitionType> page =
			documentDataDefinitionTypeResource.
				getAssetLibraryDocumentDataDefinitionTypesPage(
					assetLibraryId, null, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			DocumentDataDefinitionType irrelevantDocumentDataDefinitionType =
				testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
					irrelevantAssetLibraryId,
					randomIrrelevantDocumentDataDefinitionType());

			page =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						irrelevantAssetLibraryId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentDataDefinitionType,
				(List<DocumentDataDefinitionType>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentDataDefinitionTypesPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		page =
			documentDataDefinitionTypeResource.
				getAssetLibraryDocumentDataDefinitionTypesPage(
					assetLibraryId, null, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			documentDataDefinitionType1,
			(List<DocumentDataDefinitionType>)page.getItems());
		assertContains(
			documentDataDefinitionType2,
			(List<DocumentDataDefinitionType>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getExpectedActions(
				assetLibraryId));

		documentDataDefinitionTypeResource.deleteDocumentDataDefinitionType(
			documentDataDefinitionType1.getId());

		documentDataDefinitionTypeResource.deleteDocumentDataDefinitionType(
			documentDataDefinitionType2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/asset-libraries/{assetLibraryId}/document-data-definition-types/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId();

		DocumentDataDefinitionType documentDataDefinitionType1 =
			randomDocumentDataDefinitionType();

		documentDataDefinitionType1 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, documentDataDefinitionType1);

		for (EntityField entityField : entityFields) {
			Page<DocumentDataDefinitionType> page =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null,
						getFilterString(
							entityField, "between",
							documentDataDefinitionType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentDataDefinitionType1),
				(List<DocumentDataDefinitionType>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryDocumentDataDefinitionTypesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId();

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		for (EntityField entityField : entityFields) {
			Page<DocumentDataDefinitionType> page =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null,
						getFilterString(
							entityField, operator, documentDataDefinitionType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentDataDefinitionType1),
				(List<DocumentDataDefinitionType>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId();

		Page<DocumentDataDefinitionType> documentDataDefinitionTypesPage =
			documentDataDefinitionTypeResource.
				getAssetLibraryDocumentDataDefinitionTypesPage(
					assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			documentDataDefinitionTypesPage.getTotalCount());

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType3 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentDataDefinitionType> page1 =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)page1.getItems());

			Page<DocumentDataDefinitionType> page2 =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)page2.getItems());

			Page<DocumentDataDefinitionType> page3 =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				documentDataDefinitionType3,
				(List<DocumentDataDefinitionType>)page3.getItems());
		}
		else {
			Page<DocumentDataDefinitionType> page1 =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<DocumentDataDefinitionType> documentDataDefinitionTypes1 =
				(List<DocumentDataDefinitionType>)page1.getItems();

			Assert.assertEquals(
				documentDataDefinitionTypes1.toString(), totalCount + 2,
				documentDataDefinitionTypes1.size());

			Page<DocumentDataDefinitionType> page2 =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentDataDefinitionType> documentDataDefinitionTypes2 =
				(List<DocumentDataDefinitionType>)page2.getItems();

			Assert.assertEquals(
				documentDataDefinitionTypes2.toString(), 1,
				documentDataDefinitionTypes2.size());

			Page<DocumentDataDefinitionType> page3 =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)page3.getItems());
			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)page3.getItems());
			assertContains(
				documentDataDefinitionType3,
				(List<DocumentDataDefinitionType>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				BeanTestUtil.setProperty(
					documentDataDefinitionType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				BeanTestUtil.setProperty(
					documentDataDefinitionType1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					documentDataDefinitionType2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				BeanTestUtil.setProperty(
					documentDataDefinitionType1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					documentDataDefinitionType2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryDocumentDataDefinitionTypesPageWithSortString()
		throws Exception {

		testGetAssetLibraryDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				Class<?> clazz = documentDataDefinitionType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						documentDataDefinitionType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						documentDataDefinitionType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						documentDataDefinitionType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						documentDataDefinitionType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						documentDataDefinitionType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						documentDataDefinitionType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DocumentDataDefinitionType,
				 DocumentDataDefinitionType, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId();

		DocumentDataDefinitionType documentDataDefinitionType1 =
			randomDocumentDataDefinitionType();
		DocumentDataDefinitionType documentDataDefinitionType2 =
			randomDocumentDataDefinitionType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, documentDataDefinitionType1,
				documentDataDefinitionType2);
		}

		documentDataDefinitionType1 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, documentDataDefinitionType1);

		documentDataDefinitionType2 =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				assetLibraryId, documentDataDefinitionType2);

		Page<DocumentDataDefinitionType> page =
			documentDataDefinitionTypeResource.
				getAssetLibraryDocumentDataDefinitionTypesPage(
					assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DocumentDataDefinitionType> ascPage =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)ascPage.getItems());
			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)ascPage.getItems());

			Page<DocumentDataDefinitionType> descPage =
				documentDataDefinitionTypeResource.
					getAssetLibraryDocumentDataDefinitionTypesPage(
						assetLibraryId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)descPage.getItems());
			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)descPage.getItems());
		}
	}

	protected DocumentDataDefinitionType
			testGetAssetLibraryDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				Long assetLibraryId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return documentDataDefinitionTypeResource.
			postAssetLibraryDocumentDataDefinitionType(
				assetLibraryId, documentDataDefinitionType);
	}

	protected Long
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentDataDefinitionTypesPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentDataDefinitionTypes",
			new HashMap<String, Object>() {
				{
					put("assetLibraryId", "\"" + assetLibraryId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject assetLibraryDocumentDataDefinitionTypesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentDataDefinitionTypes");

		long totalCount =
			assetLibraryDocumentDataDefinitionTypesJSONObject.getLong(
				"totalCount");

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGraphQLAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGraphQLAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
				assetLibraryId, randomDocumentDataDefinitionType());

		assetLibraryDocumentDataDefinitionTypesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentDataDefinitionTypes");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentDataDefinitionTypesJSONObject.getLong(
				"totalCount"));

		assertContains(
			documentDataDefinitionType1,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					assetLibraryDocumentDataDefinitionTypesJSONObject.getString(
						"items"))));
		assertContains(
			documentDataDefinitionType2,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					assetLibraryDocumentDataDefinitionTypesJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryDocumentDataDefinitionTypesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/assetLibraryDocumentDataDefinitionTypes");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentDataDefinitionTypesJSONObject.getLong(
				"totalCount"));

		assertContains(
			documentDataDefinitionType1,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					assetLibraryDocumentDataDefinitionTypesJSONObject.getString(
						"items"))));
		assertContains(
			documentDataDefinitionType2,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					assetLibraryDocumentDataDefinitionTypesJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetDocumentDataDefinitionType() throws Exception {
		DocumentDataDefinitionType postDocumentDataDefinitionType =
			testGetDocumentDataDefinitionType_addDocumentDataDefinitionType();

		DocumentDataDefinitionType getDocumentDataDefinitionType =
			documentDataDefinitionTypeResource.getDocumentDataDefinitionType(
				postDocumentDataDefinitionType.getId());

		assertEquals(
			postDocumentDataDefinitionType, getDocumentDataDefinitionType);
		assertValid(getDocumentDataDefinitionType);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DocumentDataDefinitionType postDocumentDataDefinitionType =
			testGetDocumentDataDefinitionType_addDocumentDataDefinitionType();

		DocumentDataDefinitionType getDocumentDataDefinitionType =
			documentDataDefinitionTypeResource.getDocumentDataDefinitionType(
				postDocumentDataDefinitionType.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.DocumentDataDefinitionType"
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
			postDocumentDataDefinitionType.getId());

		assertEquals(
			getDocumentDataDefinitionType,
			DocumentDataDefinitionTypeSerDes.toDTO(item.toString()));
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

	protected DocumentDataDefinitionType
			testGetDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return documentDataDefinitionTypeResource.
			postSiteDocumentDataDefinitionType(
				testGroup.getGroupId(), randomDocumentDataDefinitionType());
	}

	@Test
	public void testGraphQLGetDocumentDataDefinitionType() throws Exception {
		DocumentDataDefinitionType documentDataDefinitionType =
			testGraphQLGetDocumentDataDefinitionType_addDocumentDataDefinitionType();

		// No namespace

		Assert.assertTrue(
			equals(
				documentDataDefinitionType,
				DocumentDataDefinitionTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentDataDefinitionType",
								new HashMap<String, Object>() {
									{
										put(
											"documentDataDefinitionTypeId",
											documentDataDefinitionType.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/documentDataDefinitionType"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentDataDefinitionType,
				DocumentDataDefinitionTypeSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentDataDefinitionType",
									new HashMap<String, Object>() {
										{
											put(
												"documentDataDefinitionTypeId",
												documentDataDefinitionType.
													getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentDataDefinitionType"))));
	}

	@Test
	public void testGraphQLGetDocumentDataDefinitionTypeNotFound()
		throws Exception {

		Long irrelevantDocumentDataDefinitionTypeId =
			RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentDataDefinitionType",
						new HashMap<String, Object>() {
							{
								put(
									"documentDataDefinitionTypeId",
									irrelevantDocumentDataDefinitionTypeId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"documentDataDefinitionType",
							new HashMap<String, Object>() {
								{
									put(
										"documentDataDefinitionTypeId",
										irrelevantDocumentDataDefinitionTypeId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentDataDefinitionType
			testGraphQLGetDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return testGraphQLDocumentDataDefinitionType_addDocumentDataDefinitionType();
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPage() throws Exception {
		Long siteId = testGetSiteDocumentDataDefinitionTypesPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDocumentDataDefinitionTypesPage_getIrrelevantSiteId();

		Page<DocumentDataDefinitionType> page =
			documentDataDefinitionTypeResource.
				getSiteDocumentDataDefinitionTypesPage(
					siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			DocumentDataDefinitionType irrelevantDocumentDataDefinitionType =
				testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
					irrelevantSiteId,
					randomIrrelevantDocumentDataDefinitionType());

			page =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						irrelevantSiteId, null, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentDataDefinitionType,
				(List<DocumentDataDefinitionType>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentDataDefinitionTypesPage_getExpectedActions(
					irrelevantSiteId));
		}

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		page =
			documentDataDefinitionTypeResource.
				getSiteDocumentDataDefinitionTypesPage(
					siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			documentDataDefinitionType1,
			(List<DocumentDataDefinitionType>)page.getItems());
		assertContains(
			documentDataDefinitionType2,
			(List<DocumentDataDefinitionType>)page.getItems());
		assertValid(
			page,
			testGetSiteDocumentDataDefinitionTypesPage_getExpectedActions(
				siteId));

		documentDataDefinitionTypeResource.deleteDocumentDataDefinitionType(
			documentDataDefinitionType1.getId());

		documentDataDefinitionTypeResource.deleteDocumentDataDefinitionType(
			documentDataDefinitionType2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentDataDefinitionTypesPage_getExpectedActions(
				Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/document-data-definition-types/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentDataDefinitionTypesPage_getSiteId();

		DocumentDataDefinitionType documentDataDefinitionType1 =
			randomDocumentDataDefinitionType();

		documentDataDefinitionType1 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, documentDataDefinitionType1);

		for (EntityField entityField : entityFields) {
			Page<DocumentDataDefinitionType> page =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null,
						getFilterString(
							entityField, "between",
							documentDataDefinitionType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentDataDefinitionType1),
				(List<DocumentDataDefinitionType>)page.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithFilterStringContains()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithFilterStringEquals()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteDocumentDataDefinitionTypesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentDataDefinitionTypesPage_getSiteId();

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		for (EntityField entityField : entityFields) {
			Page<DocumentDataDefinitionType> page =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null,
						getFilterString(
							entityField, operator, documentDataDefinitionType1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentDataDefinitionType1),
				(List<DocumentDataDefinitionType>)page.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteDocumentDataDefinitionTypesPage_getSiteId();

		Page<DocumentDataDefinitionType> documentDataDefinitionTypesPage =
			documentDataDefinitionTypeResource.
				getSiteDocumentDataDefinitionTypesPage(
					siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			documentDataDefinitionTypesPage.getTotalCount());

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType3 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentDataDefinitionType> page1 =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)page1.getItems());

			Page<DocumentDataDefinitionType> page2 =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)page2.getItems());

			Page<DocumentDataDefinitionType> page3 =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				documentDataDefinitionType3,
				(List<DocumentDataDefinitionType>)page3.getItems());
		}
		else {
			Page<DocumentDataDefinitionType> page1 =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<DocumentDataDefinitionType> documentDataDefinitionTypes1 =
				(List<DocumentDataDefinitionType>)page1.getItems();

			Assert.assertEquals(
				documentDataDefinitionTypes1.toString(), totalCount + 2,
				documentDataDefinitionTypes1.size());

			Page<DocumentDataDefinitionType> page2 =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentDataDefinitionType> documentDataDefinitionTypes2 =
				(List<DocumentDataDefinitionType>)page2.getItems();

			Assert.assertEquals(
				documentDataDefinitionTypes2.toString(), 1,
				documentDataDefinitionTypes2.size());

			Page<DocumentDataDefinitionType> page3 =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)page3.getItems());
			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)page3.getItems());
			assertContains(
				documentDataDefinitionType3,
				(List<DocumentDataDefinitionType>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithSortDateTime()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				BeanTestUtil.setProperty(
					documentDataDefinitionType1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithSortDouble()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				BeanTestUtil.setProperty(
					documentDataDefinitionType1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					documentDataDefinitionType2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithSortInteger()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				BeanTestUtil.setProperty(
					documentDataDefinitionType1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					documentDataDefinitionType2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteDocumentDataDefinitionTypesPageWithSortString()
		throws Exception {

		testGetSiteDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type.STRING,
			(entityField, documentDataDefinitionType1,
			 documentDataDefinitionType2) -> {

				Class<?> clazz = documentDataDefinitionType1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						documentDataDefinitionType1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						documentDataDefinitionType2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						documentDataDefinitionType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						documentDataDefinitionType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						documentDataDefinitionType1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						documentDataDefinitionType2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteDocumentDataDefinitionTypesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DocumentDataDefinitionType,
				 DocumentDataDefinitionType, Exception> unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentDataDefinitionTypesPage_getSiteId();

		DocumentDataDefinitionType documentDataDefinitionType1 =
			randomDocumentDataDefinitionType();
		DocumentDataDefinitionType documentDataDefinitionType2 =
			randomDocumentDataDefinitionType();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, documentDataDefinitionType1,
				documentDataDefinitionType2);
		}

		documentDataDefinitionType1 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, documentDataDefinitionType1);

		documentDataDefinitionType2 =
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				siteId, documentDataDefinitionType2);

		Page<DocumentDataDefinitionType> page =
			documentDataDefinitionTypeResource.
				getSiteDocumentDataDefinitionTypesPage(
					siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DocumentDataDefinitionType> ascPage =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)ascPage.getItems());
			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)ascPage.getItems());

			Page<DocumentDataDefinitionType> descPage =
				documentDataDefinitionTypeResource.
					getSiteDocumentDataDefinitionTypesPage(
						siteId, null, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				documentDataDefinitionType2,
				(List<DocumentDataDefinitionType>)descPage.getItems());
			assertContains(
				documentDataDefinitionType1,
				(List<DocumentDataDefinitionType>)descPage.getItems());
		}
	}

	protected DocumentDataDefinitionType
			testGetSiteDocumentDataDefinitionTypesPage_addDocumentDataDefinitionType(
				Long siteId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return documentDataDefinitionTypeResource.
			postSiteDocumentDataDefinitionType(
				siteId, documentDataDefinitionType);
	}

	protected Long testGetSiteDocumentDataDefinitionTypesPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long
			testGetSiteDocumentDataDefinitionTypesPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentDataDefinitionTypesPage()
		throws Exception {

		Long siteId = testGetSiteDocumentDataDefinitionTypesPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documentDataDefinitionTypes",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentDataDefinitionTypesJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentDataDefinitionTypes");

		long totalCount = documentDataDefinitionTypesJSONObject.getLong(
			"totalCount");

		DocumentDataDefinitionType documentDataDefinitionType1 =
			testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		DocumentDataDefinitionType documentDataDefinitionType2 =
			testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				siteId, randomDocumentDataDefinitionType());

		documentDataDefinitionTypesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentDataDefinitionTypes");

		Assert.assertEquals(
			totalCount + 2,
			documentDataDefinitionTypesJSONObject.getLong("totalCount"));

		assertContains(
			documentDataDefinitionType1,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					documentDataDefinitionTypesJSONObject.getString("items"))));
		assertContains(
			documentDataDefinitionType2,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					documentDataDefinitionTypesJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentDataDefinitionTypesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentDataDefinitionTypes");

		Assert.assertEquals(
			totalCount + 2,
			documentDataDefinitionTypesJSONObject.getLong("totalCount"));

		assertContains(
			documentDataDefinitionType1,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					documentDataDefinitionTypesJSONObject.getString("items"))));
		assertContains(
			documentDataDefinitionType2,
			Arrays.asList(
				DocumentDataDefinitionTypeSerDes.toDTOs(
					documentDataDefinitionTypesJSONObject.getString("items"))));
	}

	@Test
	public void testPostAssetLibraryDocumentDataDefinitionType()
		throws Exception {

		DocumentDataDefinitionType randomDocumentDataDefinitionType =
			randomDocumentDataDefinitionType();

		DocumentDataDefinitionType postDocumentDataDefinitionType =
			testPostAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
				randomDocumentDataDefinitionType);

		assertEquals(
			randomDocumentDataDefinitionType, postDocumentDataDefinitionType);
		assertValid(postDocumentDataDefinitionType);
	}

	protected DocumentDataDefinitionType
			testPostAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return documentDataDefinitionTypeResource.
			postAssetLibraryDocumentDataDefinitionType(
				testGetAssetLibraryDocumentDataDefinitionTypesPage_getAssetLibraryId(),
				documentDataDefinitionType);
	}

	@Test
	public void testGraphQLPostAssetLibraryDocumentDataDefinitionType()
		throws Exception {

		DocumentDataDefinitionType randomDocumentDataDefinitionType =
			randomDocumentDataDefinitionType();

		DocumentDataDefinitionType documentDataDefinitionType =
			testGraphQLAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
				testDepotEntry.getDepotEntryId(),
				randomDocumentDataDefinitionType);

		Assert.assertTrue(
			equals(
				randomDocumentDataDefinitionType, documentDataDefinitionType));
	}

	@Test
	public void testPostSiteDocumentDataDefinitionType() throws Exception {
		DocumentDataDefinitionType randomDocumentDataDefinitionType =
			randomDocumentDataDefinitionType();

		DocumentDataDefinitionType postDocumentDataDefinitionType =
			testPostSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				randomDocumentDataDefinitionType);

		assertEquals(
			randomDocumentDataDefinitionType, postDocumentDataDefinitionType);
		assertValid(postDocumentDataDefinitionType);
	}

	protected DocumentDataDefinitionType
			testPostSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		return documentDataDefinitionTypeResource.
			postSiteDocumentDataDefinitionType(
				testGetSiteDocumentDataDefinitionTypesPage_getSiteId(),
				documentDataDefinitionType);
	}

	@Test
	public void testGraphQLPostSiteDocumentDataDefinitionType()
		throws Exception {

		DocumentDataDefinitionType randomDocumentDataDefinitionType =
			randomDocumentDataDefinitionType();

		DocumentDataDefinitionType documentDataDefinitionType =
			testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				testGroup.getGroupId(), randomDocumentDataDefinitionType);

		Assert.assertTrue(
			equals(
				randomDocumentDataDefinitionType, documentDataDefinitionType));
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DocumentDataDefinitionType documentDataDefinitionType1 =
			testBatchEngineDeleteImportTask_addDocumentDataDefinitionType();

		testBatchEngineDeleteImportTask_deleteDocumentDataDefinitionType(
			200, null, documentDataDefinitionType1.getId());

		assertHttpResponseStatusCode(
			404,
			documentDataDefinitionTypeResource.
				getDocumentDataDefinitionTypeHttpResponse(
					documentDataDefinitionType1.getId()));
	}

	protected DocumentDataDefinitionType
			testBatchEngineDeleteImportTask_addDocumentDataDefinitionType()
		throws Exception {

		return testDeleteDocumentDataDefinitionType_addDocumentDataDefinitionType();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteDocumentDataDefinitionType(
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
				"com.liferay.headless.delivery.dto.v1_0.DocumentDataDefinitionType",
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

	protected DocumentDataDefinitionType
			testGraphQLDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return testGraphQLDocumentDataDefinitionType_addDocumentDataDefinitionType(
			testGroup.getGroupId(), randomDocumentDataDefinitionType());
	}

	protected DocumentDataDefinitionType
			testGraphQLDocumentDataDefinitionType_addDocumentDataDefinitionType(
				Long siteId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		JSONDeserializer<DocumentDataDefinitionType> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentDataDefinitionType.class)) {

			if (getGraphQLValue(field.get(documentDataDefinitionType)) !=
					null) {

				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(
					getGraphQLValue(field.get(documentDataDefinitionType)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentDataDefinitionType",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put(
									"documentDataDefinitionType",
									sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createSiteDocumentDataDefinitionType"),
			DocumentDataDefinitionType.class);
	}

	protected DocumentDataDefinitionType
			testGraphQLAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return testGraphQLAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
			testDepotEntry.getDepotEntryId(),
			randomDocumentDataDefinitionType());
	}

	protected DocumentDataDefinitionType
			testGraphQLAssetLibraryDocumentDataDefinitionType_addDocumentDataDefinitionType(
				Long assetLibraryId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		JSONDeserializer<DocumentDataDefinitionType> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentDataDefinitionType.class)) {

			if (getGraphQLValue(field.get(documentDataDefinitionType)) !=
					null) {

				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(
					getGraphQLValue(field.get(documentDataDefinitionType)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryDocumentDataDefinitionType",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put(
									"documentDataDefinitionType",
									sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryDocumentDataDefinitionType"),
			DocumentDataDefinitionType.class);
	}

	protected DocumentDataDefinitionType
			testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType()
		throws Exception {

		return testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
			testGroup.getGroupId(), randomDocumentDataDefinitionType());
	}

	protected DocumentDataDefinitionType
			testGraphQLSiteDocumentDataDefinitionType_addDocumentDataDefinitionType(
				Long siteId,
				DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		JSONDeserializer<DocumentDataDefinitionType> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentDataDefinitionType.class)) {

			if (getGraphQLValue(field.get(documentDataDefinitionType)) !=
					null) {

				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(
					getGraphQLValue(field.get(documentDataDefinitionType)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentDataDefinitionType",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put(
									"documentDataDefinitionType",
									sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createSiteDocumentDataDefinitionType"),
			DocumentDataDefinitionType.class);
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
		DocumentDataDefinitionType documentDataDefinitionType,
		List<DocumentDataDefinitionType> documentDataDefinitionTypes) {

		boolean contains = false;

		for (DocumentDataDefinitionType item : documentDataDefinitionTypes) {
			if (equals(documentDataDefinitionType, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			documentDataDefinitionTypes + " does not contain " +
				documentDataDefinitionType,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DocumentDataDefinitionType documentDataDefinitionType1,
		DocumentDataDefinitionType documentDataDefinitionType2) {

		Assert.assertTrue(
			documentDataDefinitionType1 + " does not equal " +
				documentDataDefinitionType2,
			equals(documentDataDefinitionType1, documentDataDefinitionType2));
	}

	protected void assertEquals(
		List<DocumentDataDefinitionType> documentDataDefinitionTypes1,
		List<DocumentDataDefinitionType> documentDataDefinitionTypes2) {

		Assert.assertEquals(
			documentDataDefinitionTypes1.size(),
			documentDataDefinitionTypes2.size());

		for (int i = 0; i < documentDataDefinitionTypes1.size(); i++) {
			DocumentDataDefinitionType documentDataDefinitionType1 =
				documentDataDefinitionTypes1.get(i);
			DocumentDataDefinitionType documentDataDefinitionType2 =
				documentDataDefinitionTypes2.get(i);

			assertEquals(
				documentDataDefinitionType1, documentDataDefinitionType2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DocumentDataDefinitionType> documentDataDefinitionTypes1,
		List<DocumentDataDefinitionType> documentDataDefinitionTypes2) {

		Assert.assertEquals(
			documentDataDefinitionTypes1.size(),
			documentDataDefinitionTypes2.size());

		for (DocumentDataDefinitionType documentDataDefinitionType1 :
				documentDataDefinitionTypes1) {

			boolean contains = false;

			for (DocumentDataDefinitionType documentDataDefinitionType2 :
					documentDataDefinitionTypes2) {

				if (equals(
						documentDataDefinitionType1,
						documentDataDefinitionType2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				documentDataDefinitionTypes2 + " does not contain " +
					documentDataDefinitionType1,
				contains);
		}
	}

	protected void assertValid(
			DocumentDataDefinitionType documentDataDefinitionType)
		throws Exception {

		boolean valid = true;

		if (documentDataDefinitionType.getDateCreated() == null) {
			valid = false;
		}

		if (documentDataDefinitionType.getDateModified() == null) {
			valid = false;
		}

		if (documentDataDefinitionType.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				documentDataDefinitionType.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				documentDataDefinitionType.getSiteId(),
				testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (documentDataDefinitionType.getAvailableLanguages() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionFields", additionalAssertFieldName)) {

				if (documentDataDefinitionType.getDataDefinitionFields() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("dataLayout", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getDataLayout() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getDescription_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"documentMetadataSetIds", additionalAssertFieldName)) {

				if (documentDataDefinitionType.getDocumentMetadataSetIds() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (documentDataDefinitionType.getExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getName_i18n() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (documentDataDefinitionType.getViewableBy() == null) {
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

	protected void assertValid(Page<DocumentDataDefinitionType> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DocumentDataDefinitionType> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DocumentDataDefinitionType>
			documentDataDefinitionTypes = page.getItems();

		int size = documentDataDefinitionTypes.size();

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

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.
						DocumentDataDefinitionType.class)) {

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
		DocumentDataDefinitionType documentDataDefinitionType1,
		DocumentDataDefinitionType documentDataDefinitionType2) {

		if (documentDataDefinitionType1 == documentDataDefinitionType2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentDataDefinitionType1.getActions(),
						(Map)documentDataDefinitionType2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"availableLanguages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentDataDefinitionType1.getAvailableLanguages(),
						documentDataDefinitionType2.getAvailableLanguages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getCreator(),
						documentDataDefinitionType2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"dataDefinitionFields", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentDataDefinitionType1.getDataDefinitionFields(),
						documentDataDefinitionType2.
							getDataDefinitionFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dataLayout", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getDataLayout(),
						documentDataDefinitionType2.getDataLayout())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getDateCreated(),
						documentDataDefinitionType2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getDateModified(),
						documentDataDefinitionType2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getDescription(),
						documentDataDefinitionType2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentDataDefinitionType1.getDescription_i18n(),
						(Map)
							documentDataDefinitionType2.
								getDescription_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"documentMetadataSetIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentDataDefinitionType1.getDocumentMetadataSetIds(),
						documentDataDefinitionType2.
							getDocumentMetadataSetIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentDataDefinitionType1.getExternalReferenceCode(),
						documentDataDefinitionType2.
							getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getId(),
						documentDataDefinitionType2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getName(),
						documentDataDefinitionType2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name_i18n", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentDataDefinitionType1.getName_i18n(),
						(Map)documentDataDefinitionType2.getName_i18n())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentDataDefinitionType1.getViewableBy(),
						documentDataDefinitionType2.getViewableBy())) {

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

		if (!(_documentDataDefinitionTypeResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_documentDataDefinitionTypeResource;

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
		DocumentDataDefinitionType documentDataDefinitionType) {

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

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = documentDataDefinitionType.getAssetLibraryKey();

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

		if (entityFieldName.equals("availableLanguages")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataDefinitionFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dataLayout")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = documentDataDefinitionType.getDateCreated();

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
					_format.format(
						documentDataDefinitionType.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = documentDataDefinitionType.getDateModified();

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
					_format.format(
						documentDataDefinitionType.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = documentDataDefinitionType.getDescription();

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

		if (entityFieldName.equals("description_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("documentMetadataSetIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object =
				documentDataDefinitionType.getExternalReferenceCode();

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
			Object object = documentDataDefinitionType.getName();

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

		if (entityFieldName.equals("name_i18n")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("viewableBy")) {
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

	protected DocumentDataDefinitionType randomDocumentDataDefinitionType()
		throws Exception {

		return new DocumentDataDefinitionType() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
			}
		};
	}

	protected DocumentDataDefinitionType
			randomIrrelevantDocumentDataDefinitionType()
		throws Exception {

		DocumentDataDefinitionType randomIrrelevantDocumentDataDefinitionType =
			randomDocumentDataDefinitionType();

		randomIrrelevantDocumentDataDefinitionType.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantDocumentDataDefinitionType.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantDocumentDataDefinitionType;
	}

	protected DocumentDataDefinitionType randomPatchDocumentDataDefinitionType()
		throws Exception {

		return randomDocumentDataDefinitionType();
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

	protected DocumentDataDefinitionTypeResource
		documentDataDefinitionTypeResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
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
		LogFactoryUtil.getLog(
			BaseDocumentDataDefinitionTypeResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.
		DocumentDataDefinitionTypeResource _documentDataDefinitionTypeResource;

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