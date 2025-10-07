/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.delivery.client.dto.v1_0.DocumentFolder;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.Rating;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentFolderResource;
import com.liferay.headless.delivery.client.serdes.v1_0.DocumentFolderSerDes;
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
import com.liferay.portal.kernel.model.RoleConstants;
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
import com.liferay.portal.kernel.test.util.RoleTestUtil;
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
public abstract class BaseDocumentFolderResourceTestCase {

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

		_documentFolderResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		documentFolderResource = DocumentFolderResource.builder(
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

		DocumentFolder documentFolder1 = randomDocumentFolder();

		String json = objectMapper.writeValueAsString(documentFolder1);

		DocumentFolder documentFolder2 = DocumentFolderSerDes.toDTO(json);

		Assert.assertTrue(equals(documentFolder1, documentFolder2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DocumentFolder documentFolder = randomDocumentFolder();

		String json1 = objectMapper.writeValueAsString(documentFolder);
		String json2 = DocumentFolderSerDes.toJSON(documentFolder);

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

		DocumentFolder documentFolder = randomDocumentFolder();

		documentFolder.setAssetLibraryKey(regex);
		documentFolder.setDescription(regex);
		documentFolder.setExternalReferenceCode(regex);
		documentFolder.setName(regex);

		String json = DocumentFolderSerDes.toJSON(documentFolder);

		Assert.assertFalse(json.contains(regex));

		documentFolder = DocumentFolderSerDes.toDTO(json);

		Assert.assertEquals(regex, documentFolder.getAssetLibraryKey());
		Assert.assertEquals(regex, documentFolder.getDescription());
		Assert.assertEquals(regex, documentFolder.getExternalReferenceCode());
		Assert.assertEquals(regex, documentFolder.getName());
	}

	@Test
	public void testDeleteDocumentFolder() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testDeleteDocumentFolder_addDocumentFolder();

		assertHttpResponseStatusCode(
			204,
			documentFolderResource.deleteDocumentFolderHttpResponse(
				documentFolder.getId()));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.getDocumentFolderHttpResponse(
				documentFolder.getId()));
		assertHttpResponseStatusCode(
			404, documentFolderResource.getDocumentFolderHttpResponse(0L));
	}

	protected DocumentFolder testDeleteDocumentFolder_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLDeleteDocumentFolder() throws Exception {

		// No namespace

		DocumentFolder documentFolder1 =
			testGraphQLDeleteDocumentFolder_addDocumentFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocumentFolder",
						new HashMap<String, Object>() {
							{
								put(
									"documentFolderId",
									documentFolder1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocumentFolder"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentFolder",
					new HashMap<String, Object>() {
						{
							put("documentFolderId", documentFolder1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentFolder documentFolder2 =
			testGraphQLDeleteDocumentFolder_addDocumentFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocumentFolder",
							new HashMap<String, Object>() {
								{
									put(
										"documentFolderId",
										documentFolder2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocumentFolder"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentFolder",
						new HashMap<String, Object>() {
							{
								put(
									"documentFolderId",
									documentFolder2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentFolder testGraphQLDeleteDocumentFolder_addDocumentFolder()
		throws Exception {

		return testGraphQLDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testDeleteDocumentFolderBatch() throws Exception {
		DocumentFolder documentFolder1 =
			testDeleteDocumentFolderBatch_addDocumentFolder();

		testDeleteDocumentFolderBatch_deleteDocumentFolder(
			202, null, documentFolder1.getId());

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.getDocumentFolderHttpResponse(
				documentFolder1.getId()));
	}

	protected DocumentFolder testDeleteDocumentFolderBatch_addDocumentFolder()
		throws Exception {

		return testDeleteDocumentFolder_addDocumentFolder();
	}

	protected void testDeleteDocumentFolderBatch_deleteDocumentFolder(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			documentFolderResource.deleteDocumentFolderBatchHttpResponse(
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
	public void testDeleteDocumentFolderMyRating() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testDeleteDocumentFolderMyRating_addDocumentFolder();

		assertHttpResponseStatusCode(
			204,
			documentFolderResource.deleteDocumentFolderMyRatingHttpResponse(
				documentFolder.getId()));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.getDocumentFolderMyRatingHttpResponse(
				documentFolder.getId()));
		assertHttpResponseStatusCode(
			404,
			documentFolderResource.getDocumentFolderMyRatingHttpResponse(0L));
	}

	protected DocumentFolder
			testDeleteDocumentFolderMyRating_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLDeleteDocumentFolderMyRating() throws Exception {

		// No namespace

		DocumentFolder documentFolder1 =
			testGraphQLDeleteDocumentFolderMyRating_addDocumentFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocumentFolderMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"documentFolderId",
									documentFolder1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocumentFolderMyRating"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentFolderMyRating",
					new HashMap<String, Object>() {
						{
							put("documentFolderId", documentFolder1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentFolder documentFolder2 =
			testGraphQLDeleteDocumentFolderMyRating_addDocumentFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocumentFolderMyRating",
							new HashMap<String, Object>() {
								{
									put(
										"documentFolderId",
										documentFolder2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocumentFolderMyRating"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentFolderMyRating",
						new HashMap<String, Object>() {
							{
								put(
									"documentFolderId",
									documentFolder2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentFolder
			testGraphQLDeleteDocumentFolderMyRating_addDocumentFolder()
		throws Exception {

		return testGraphQLDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testDeleteSiteDocumentsFolderByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testDeleteSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder();

		assertHttpResponseStatusCode(
			204,
			documentFolderResource.
				deleteSiteDocumentsFolderByExternalReferenceCodeHttpResponse(
					documentFolder.getSiteId(),
					documentFolder.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.
				getSiteDocumentsFolderByExternalReferenceCodeHttpResponse(
					documentFolder.getSiteId(),
					documentFolder.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			documentFolderResource.
				getSiteDocumentsFolderByExternalReferenceCodeHttpResponse(
					documentFolder.getSiteId(), "-"));
	}

	protected DocumentFolder
			testDeleteSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLDeleteSiteDocumentsFolderByExternalReferenceCode()
		throws Exception {

		// No namespace

		DocumentFolder documentFolder1 =
			testGraphQLDeleteSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteDocumentsFolderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + documentFolder1.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										documentFolder1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteDocumentsFolderByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentsFolderByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + documentFolder1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									documentFolder1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentFolder documentFolder2 =
			testGraphQLDeleteSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteDocumentsFolderByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + documentFolder2.getSiteId() +
											"\"");
									put(
										"externalReferenceCode",
										"\"" +
											documentFolder2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteDocumentsFolderByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentsFolderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + documentFolder2.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										documentFolder2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentFolder
			testGraphQLDeleteSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder()
		throws Exception {

		return testGraphQLSiteDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testGetAssetLibraryDocumentFolderPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder postDocumentFolder =
			testGetAssetLibraryDocumentFolderPermissionsPage_addDocumentFolder();

		Page<Permission> page =
			documentFolderResource.getAssetLibraryDocumentFolderPermissionsPage(
				testDepotEntry.getDepotEntryId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected DocumentFolder
			testGetAssetLibraryDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postAssetLibraryDocumentFolder(
			testDepotEntry.getDepotEntryId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentFolderPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder postDocumentFolder =
			testGraphQLGetAssetLibraryDocumentFolderPermissionsPage_addDocumentFolder();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentFolderPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"assetLibraryId",
						"\"" +
							testGraphQLGetAssetLibraryDocumentFolderPermissionsPage_getAssetLibraryId() +
								"\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject assetLibraryDocumentFolderPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentFolderPermissions");

		Assert.assertNotNull(assetLibraryDocumentFolderPermissionsJSONObject);
	}

	protected Long
			testGraphQLGetAssetLibraryDocumentFolderPermissionsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected DocumentFolder
			testGraphQLGetAssetLibraryDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return testGraphQLAssetLibraryDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getIrrelevantAssetLibraryId();

		Page<DocumentFolder> page =
			documentFolderResource.getAssetLibraryDocumentFoldersPage(
				assetLibraryId, null, null, null, null, Pagination.of(1, 10),
				null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			DocumentFolder irrelevantDocumentFolder =
				testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
					irrelevantAssetLibraryId, randomIrrelevantDocumentFolder());

			page = documentFolderResource.getAssetLibraryDocumentFoldersPage(
				irrelevantAssetLibraryId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentFolder,
				(List<DocumentFolder>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentFoldersPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		DocumentFolder documentFolder1 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		page = documentFolderResource.getAssetLibraryDocumentFoldersPage(
			assetLibraryId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(documentFolder1, (List<DocumentFolder>)page.getItems());
		assertContains(documentFolder2, (List<DocumentFolder>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentFoldersPage_getExpectedActions(
				assetLibraryId));

		documentFolderResource.deleteDocumentFolder(documentFolder1.getId());

		documentFolderResource.deleteDocumentFolder(documentFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentFoldersPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/asset-libraries/{assetLibraryId}/document-folders/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId();

		DocumentFolder documentFolder1 = randomDocumentFolder();

		documentFolder1 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, documentFolder1);

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> page =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null,
					getFilterString(entityField, "between", documentFolder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentFolder1),
				(List<DocumentFolder>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryDocumentFoldersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId();

		DocumentFolder documentFolder1 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder2 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> page =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null,
					getFilterString(entityField, operator, documentFolder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentFolder1),
				(List<DocumentFolder>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId();

		Page<DocumentFolder> documentFoldersPage =
			documentFolderResource.getAssetLibraryDocumentFoldersPage(
				assetLibraryId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			documentFoldersPage.getTotalCount());

		DocumentFolder documentFolder1 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder3 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentFolder> page1 =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentFolder1, (List<DocumentFolder>)page1.getItems());

			Page<DocumentFolder> page2 =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				documentFolder2, (List<DocumentFolder>)page2.getItems());

			Page<DocumentFolder> page3 =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
		else {
			Page<DocumentFolder> page1 =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<DocumentFolder> documentFolders1 =
				(List<DocumentFolder>)page1.getItems();

			Assert.assertEquals(
				documentFolders1.toString(), totalCount + 2,
				documentFolders1.size());

			Page<DocumentFolder> page2 =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentFolder> documentFolders2 =
				(List<DocumentFolder>)page2.getItems();

			Assert.assertEquals(
				documentFolders2.toString(), 1, documentFolders2.size());

			Page<DocumentFolder> page3 =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				documentFolder1, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					documentFolder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					documentFolder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersPageWithSortString()
		throws Exception {

		testGetAssetLibraryDocumentFoldersPageWithSort(
			EntityField.Type.STRING,
			(entityField, documentFolder1, documentFolder2) -> {
				Class<?> clazz = documentFolder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryDocumentFoldersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DocumentFolder, DocumentFolder, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId();

		DocumentFolder documentFolder1 = randomDocumentFolder();
		DocumentFolder documentFolder2 = randomDocumentFolder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, documentFolder1, documentFolder2);
		}

		documentFolder1 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, documentFolder1);

		documentFolder2 =
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				assetLibraryId, documentFolder2);

		Page<DocumentFolder> page =
			documentFolderResource.getAssetLibraryDocumentFoldersPage(
				assetLibraryId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> ascPage =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				documentFolder1, (List<DocumentFolder>)ascPage.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)ascPage.getItems());

			Page<DocumentFolder> descPage =
				documentFolderResource.getAssetLibraryDocumentFoldersPage(
					assetLibraryId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				documentFolder2, (List<DocumentFolder>)descPage.getItems());
			assertContains(
				documentFolder1, (List<DocumentFolder>)descPage.getItems());
		}
	}

	protected DocumentFolder
			testGetAssetLibraryDocumentFoldersPage_addDocumentFolder(
				Long assetLibraryId, DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postAssetLibraryDocumentFolder(
			assetLibraryId, documentFolder);
	}

	protected Long testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentFoldersPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentFoldersPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentFolders",
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

		JSONObject assetLibraryDocumentFoldersJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentFolders");

		long totalCount = assetLibraryDocumentFoldersJSONObject.getLong(
			"totalCount");

		DocumentFolder documentFolder1 =
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		assetLibraryDocumentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/assetLibraryDocumentFolders");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentFoldersJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersJSONObject.getString("items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryDocumentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/assetLibraryDocumentFolders");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentFoldersJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersJSONObject.getString("items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersJSONObject.getString("items"))));
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersRatedByMePage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersRatedByMePage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentFoldersRatedByMePage_getIrrelevantAssetLibraryId();

		Page<DocumentFolder> page =
			documentFolderResource.getAssetLibraryDocumentFoldersRatedByMePage(
				assetLibraryId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			DocumentFolder irrelevantDocumentFolder =
				testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
					irrelevantAssetLibraryId, randomIrrelevantDocumentFolder());

			page =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						irrelevantAssetLibraryId,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentFolder,
				(List<DocumentFolder>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentFoldersRatedByMePage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		DocumentFolder documentFolder1 =
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		page =
			documentFolderResource.getAssetLibraryDocumentFoldersRatedByMePage(
				assetLibraryId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(documentFolder1, (List<DocumentFolder>)page.getItems());
		assertContains(documentFolder2, (List<DocumentFolder>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentFoldersRatedByMePage_getExpectedActions(
				assetLibraryId));

		documentFolderResource.deleteDocumentFolder(documentFolder1.getId());

		documentFolderResource.deleteDocumentFolder(documentFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentFoldersRatedByMePage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentFoldersRatedByMePageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersRatedByMePage_getAssetLibraryId();

		Page<DocumentFolder> documentFoldersPage =
			documentFolderResource.getAssetLibraryDocumentFoldersRatedByMePage(
				assetLibraryId, null);

		int totalCount = GetterUtil.getInteger(
			documentFoldersPage.getTotalCount());

		DocumentFolder documentFolder1 =
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder3 =
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentFolder> page1 =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						assetLibraryId,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentFolder1, (List<DocumentFolder>)page1.getItems());

			Page<DocumentFolder> page2 =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						assetLibraryId,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				documentFolder2, (List<DocumentFolder>)page2.getItems());

			Page<DocumentFolder> page3 =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						assetLibraryId,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
		else {
			Page<DocumentFolder> page1 =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						assetLibraryId, Pagination.of(1, totalCount + 2));

			List<DocumentFolder> documentFolders1 =
				(List<DocumentFolder>)page1.getItems();

			Assert.assertEquals(
				documentFolders1.toString(), totalCount + 2,
				documentFolders1.size());

			Page<DocumentFolder> page2 =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						assetLibraryId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentFolder> documentFolders2 =
				(List<DocumentFolder>)page2.getItems();

			Assert.assertEquals(
				documentFolders2.toString(), 1, documentFolders2.size());

			Page<DocumentFolder> page3 =
				documentFolderResource.
					getAssetLibraryDocumentFoldersRatedByMePage(
						assetLibraryId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				documentFolder1, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
	}

	protected DocumentFolder
			testGetAssetLibraryDocumentFoldersRatedByMePage_addDocumentFolder(
				Long assetLibraryId, DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postAssetLibraryDocumentFolder(
			assetLibraryId, documentFolder);
	}

	protected Long
			testGetAssetLibraryDocumentFoldersRatedByMePage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentFoldersRatedByMePage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentFoldersRatedByMePage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentFoldersRatedByMePage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentFoldersRatedByMe",
			new HashMap<String, Object>() {
				{
					put("assetLibraryId", "\"" + assetLibraryId + "\"");
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject assetLibraryDocumentFoldersRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentFoldersRatedByMe");

		long totalCount =
			assetLibraryDocumentFoldersRatedByMeJSONObject.getLong(
				"totalCount");

		DocumentFolder documentFolder1 =
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				assetLibraryId, randomDocumentFolder());

		assetLibraryDocumentFoldersRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentFoldersRatedByMe");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentFoldersRatedByMeJSONObject.getLong(
				"totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersRatedByMeJSONObject.getString(
						"items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersRatedByMeJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryDocumentFoldersRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessDelivery_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"JSONObject/assetLibraryDocumentFoldersRatedByMe");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentFoldersRatedByMeJSONObject.getLong(
				"totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersRatedByMeJSONObject.getString(
						"items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					assetLibraryDocumentFoldersRatedByMeJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetDocumentFolder() throws Exception {
		DocumentFolder postDocumentFolder =
			testGetDocumentFolder_addDocumentFolder();

		DocumentFolder getDocumentFolder =
			documentFolderResource.getDocumentFolder(
				postDocumentFolder.getId());

		assertEquals(postDocumentFolder, getDocumentFolder);
		assertValid(getDocumentFolder);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DocumentFolder postDocumentFolder =
			testGetDocumentFolder_addDocumentFolder();

		DocumentFolder getDocumentFolder =
			documentFolderResource.getDocumentFolder(
				postDocumentFolder.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.DocumentFolder"
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
			postDocumentFolder.getId());

		assertEquals(
			getDocumentFolder, DocumentFolderSerDes.toDTO(item.toString()));
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

	protected DocumentFolder testGetDocumentFolder_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLGetDocumentFolder() throws Exception {
		DocumentFolder documentFolder =
			testGraphQLGetDocumentFolder_addDocumentFolder();

		// No namespace

		Assert.assertTrue(
			equals(
				documentFolder,
				DocumentFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentFolder",
								new HashMap<String, Object>() {
									{
										put(
											"documentFolderId",
											documentFolder.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/documentFolder"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentFolder,
				DocumentFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentFolder",
									new HashMap<String, Object>() {
										{
											put(
												"documentFolderId",
												documentFolder.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentFolder"))));
	}

	@Test
	public void testGraphQLGetDocumentFolderNotFound() throws Exception {
		Long irrelevantDocumentFolderId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentFolder",
						new HashMap<String, Object>() {
							{
								put(
									"documentFolderId",
									irrelevantDocumentFolderId);
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
							"documentFolder",
							new HashMap<String, Object>() {
								{
									put(
										"documentFolderId",
										irrelevantDocumentFolderId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentFolder testGraphQLGetDocumentFolder_addDocumentFolder()
		throws Exception {

		return testGraphQLDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPage() throws Exception {
		Long parentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId();
		Long irrelevantParentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getIrrelevantParentDocumentFolderId();

		Page<DocumentFolder> page =
			documentFolderResource.getDocumentFolderDocumentFoldersPage(
				parentDocumentFolderId, null, null, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantParentDocumentFolderId != null) {
			DocumentFolder irrelevantDocumentFolder =
				testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
					irrelevantParentDocumentFolderId,
					randomIrrelevantDocumentFolder());

			page = documentFolderResource.getDocumentFolderDocumentFoldersPage(
				irrelevantParentDocumentFolderId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentFolder,
				(List<DocumentFolder>)page.getItems());
			assertValid(
				page,
				testGetDocumentFolderDocumentFoldersPage_getExpectedActions(
					irrelevantParentDocumentFolderId));
		}

		DocumentFolder documentFolder1 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		page = documentFolderResource.getDocumentFolderDocumentFoldersPage(
			parentDocumentFolderId, null, null, null, null,
			Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(documentFolder1, (List<DocumentFolder>)page.getItems());
		assertContains(documentFolder2, (List<DocumentFolder>)page.getItems());
		assertValid(
			page,
			testGetDocumentFolderDocumentFoldersPage_getExpectedActions(
				parentDocumentFolderId));

		documentFolderResource.deleteDocumentFolder(documentFolder1.getId());

		documentFolderResource.deleteDocumentFolder(documentFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetDocumentFolderDocumentFoldersPage_getExpectedActions(
				Long parentDocumentFolderId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId();

		DocumentFolder documentFolder1 = randomDocumentFolder();

		documentFolder1 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, documentFolder1);

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> page =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null,
					getFilterString(entityField, "between", documentFolder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentFolder1),
				(List<DocumentFolder>)page.getItems());
		}
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithFilterDoubleEquals()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithFilterStringContains()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithFilterStringEquals()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithFilterStringStartsWith()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetDocumentFolderDocumentFoldersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId();

		DocumentFolder documentFolder1 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder2 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> page =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null,
					getFilterString(entityField, operator, documentFolder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentFolder1),
				(List<DocumentFolder>)page.getItems());
		}
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithPagination()
		throws Exception {

		Long parentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId();

		Page<DocumentFolder> documentFoldersPage =
			documentFolderResource.getDocumentFolderDocumentFoldersPage(
				parentDocumentFolderId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			documentFoldersPage.getTotalCount());

		DocumentFolder documentFolder1 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		DocumentFolder documentFolder3 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentFolder> page1 =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentFolder1, (List<DocumentFolder>)page1.getItems());

			Page<DocumentFolder> page2 =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				documentFolder2, (List<DocumentFolder>)page2.getItems());

			Page<DocumentFolder> page3 =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
		else {
			Page<DocumentFolder> page1 =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<DocumentFolder> documentFolders1 =
				(List<DocumentFolder>)page1.getItems();

			Assert.assertEquals(
				documentFolders1.toString(), totalCount + 2,
				documentFolders1.size());

			Page<DocumentFolder> page2 =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentFolder> documentFolders2 =
				(List<DocumentFolder>)page2.getItems();

			Assert.assertEquals(
				documentFolders2.toString(), 1, documentFolders2.size());

			Page<DocumentFolder> page3 =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				documentFolder1, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithSortDateTime()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithSortDouble()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					documentFolder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithSortInteger()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					documentFolder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetDocumentFolderDocumentFoldersPageWithSortString()
		throws Exception {

		testGetDocumentFolderDocumentFoldersPageWithSort(
			EntityField.Type.STRING,
			(entityField, documentFolder1, documentFolder2) -> {
				Class<?> clazz = documentFolder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetDocumentFolderDocumentFoldersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DocumentFolder, DocumentFolder, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long parentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId();

		DocumentFolder documentFolder1 = randomDocumentFolder();
		DocumentFolder documentFolder2 = randomDocumentFolder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, documentFolder1, documentFolder2);
		}

		documentFolder1 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, documentFolder1);

		documentFolder2 =
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				parentDocumentFolderId, documentFolder2);

		Page<DocumentFolder> page =
			documentFolderResource.getDocumentFolderDocumentFoldersPage(
				parentDocumentFolderId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> ascPage =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				documentFolder1, (List<DocumentFolder>)ascPage.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)ascPage.getItems());

			Page<DocumentFolder> descPage =
				documentFolderResource.getDocumentFolderDocumentFoldersPage(
					parentDocumentFolderId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				documentFolder2, (List<DocumentFolder>)descPage.getItems());
			assertContains(
				documentFolder1, (List<DocumentFolder>)descPage.getItems());
		}
	}

	protected DocumentFolder
			testGetDocumentFolderDocumentFoldersPage_addDocumentFolder(
				Long parentDocumentFolderId, DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postDocumentFolderDocumentFolder(
			parentDocumentFolderId, documentFolder);
	}

	protected Long
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetDocumentFolderDocumentFoldersPage_getIrrelevantParentDocumentFolderId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetDocumentFolderDocumentFoldersPage()
		throws Exception {

		Long parentDocumentFolderId =
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId();

		GraphQLField graphQLField = new GraphQLField(
			"documentFolderDocumentFolders",
			new HashMap<String, Object>() {
				{
					put("parentDocumentFolderId", parentDocumentFolderId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentFolderDocumentFoldersJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentFolderDocumentFolders");

		long totalCount = documentFolderDocumentFoldersJSONObject.getLong(
			"totalCount");

		DocumentFolder documentFolder1 =
			testGraphQLGetDocumentFolderDocumentFoldersPageDocumentFolder_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGraphQLGetDocumentFolderDocumentFoldersPageDocumentFolder_addDocumentFolder(
				parentDocumentFolderId, randomDocumentFolder());

		documentFolderDocumentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentFolderDocumentFolders");

		Assert.assertEquals(
			totalCount + 2,
			documentFolderDocumentFoldersJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFolderDocumentFoldersJSONObject.getString(
						"items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFolderDocumentFoldersJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		documentFolderDocumentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentFolderDocumentFolders");

		Assert.assertEquals(
			totalCount + 2,
			documentFolderDocumentFoldersJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFolderDocumentFoldersJSONObject.getString(
						"items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFolderDocumentFoldersJSONObject.getString(
						"items"))));
	}

	protected DocumentFolder
			testGraphQLGetDocumentFolderDocumentFoldersPageDocumentFolder_addDocumentFolder(
				Long parentDocumentFolderId, DocumentFolder documentFolder)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetDocumentFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder postDocumentFolder =
			testGetDocumentFolderPermissionsPage_addDocumentFolder();

		Page<Permission> page =
			documentFolderResource.getDocumentFolderPermissionsPage(
				postDocumentFolder.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected DocumentFolder
			testGetDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLGetDocumentFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder postDocumentFolder =
			testGraphQLGetDocumentFolderPermissionsPage_addDocumentFolder();

		GraphQLField graphQLField = new GraphQLField(
			"documentFolderPermissions",
			new HashMap<String, Object>() {
				{
					put("documentFolderId", postDocumentFolder.getId());
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject documentFolderPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentFolderPermissions");

		Assert.assertNotNull(documentFolderPermissionsJSONObject);
	}

	protected DocumentFolder
			testGraphQLGetDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return testGraphQLDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testGetSiteDocumentFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder postDocumentFolder =
			testGetSiteDocumentFolderPermissionsPage_addDocumentFolder();

		Page<Permission> page =
			documentFolderResource.getSiteDocumentFolderPermissionsPage(
				testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected DocumentFolder
			testGetSiteDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLGetSiteDocumentFolderPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder postDocumentFolder =
			testGraphQLGetSiteDocumentFolderPermissionsPage_addDocumentFolder();

		GraphQLField graphQLField = new GraphQLField(
			"siteDocumentFolderPermissions",
			new HashMap<String, Object>() {
				{
					put(
						"siteKey",
						"\"" + postDocumentFolder.getSiteId() + "\"");
				}
			},
			new GraphQLField("page"), new GraphQLField("totalCount"));

		JSONObject siteDocumentFolderPermissionsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/siteDocumentFolderPermissions");

		Assert.assertNotNull(siteDocumentFolderPermissionsJSONObject);
	}

	protected DocumentFolder
			testGraphQLGetSiteDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return testGraphQLSiteDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testGetSiteDocumentFoldersPage() throws Exception {
		Long siteId = testGetSiteDocumentFoldersPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDocumentFoldersPage_getIrrelevantSiteId();

		Page<DocumentFolder> page =
			documentFolderResource.getSiteDocumentFoldersPage(
				siteId, null, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			DocumentFolder irrelevantDocumentFolder =
				testGetSiteDocumentFoldersPage_addDocumentFolder(
					irrelevantSiteId, randomIrrelevantDocumentFolder());

			page = documentFolderResource.getSiteDocumentFoldersPage(
				irrelevantSiteId, null, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentFolder,
				(List<DocumentFolder>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentFoldersPage_getExpectedActions(
					irrelevantSiteId));
		}

		DocumentFolder documentFolder1 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		page = documentFolderResource.getSiteDocumentFoldersPage(
			siteId, null, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(documentFolder1, (List<DocumentFolder>)page.getItems());
		assertContains(documentFolder2, (List<DocumentFolder>)page.getItems());
		assertValid(
			page, testGetSiteDocumentFoldersPage_getExpectedActions(siteId));

		documentFolderResource.deleteDocumentFolder(documentFolder1.getId());

		documentFolderResource.deleteDocumentFolder(documentFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentFoldersPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/document-folders/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentFoldersPage_getSiteId();

		DocumentFolder documentFolder1 = randomDocumentFolder();

		documentFolder1 = testGetSiteDocumentFoldersPage_addDocumentFolder(
			siteId, documentFolder1);

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> page =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null,
					getFilterString(entityField, "between", documentFolder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentFolder1),
				(List<DocumentFolder>)page.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteDocumentFoldersPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithFilterStringContains()
		throws Exception {

		testGetSiteDocumentFoldersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithFilterStringEquals()
		throws Exception {

		testGetSiteDocumentFoldersPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteDocumentFoldersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteDocumentFoldersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentFoldersPage_getSiteId();

		DocumentFolder documentFolder1 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder2 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> page =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null,
					getFilterString(entityField, operator, documentFolder1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(documentFolder1),
				(List<DocumentFolder>)page.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteDocumentFoldersPage_getSiteId();

		Page<DocumentFolder> documentFoldersPage =
			documentFolderResource.getSiteDocumentFoldersPage(
				siteId, null, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			documentFoldersPage.getTotalCount());

		DocumentFolder documentFolder1 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder3 =
			testGetSiteDocumentFoldersPage_addDocumentFolder(
				siteId, randomDocumentFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentFolder> page1 =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentFolder1, (List<DocumentFolder>)page1.getItems());

			Page<DocumentFolder> page2 =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				documentFolder2, (List<DocumentFolder>)page2.getItems());

			Page<DocumentFolder> page3 =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
		else {
			Page<DocumentFolder> page1 =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<DocumentFolder> documentFolders1 =
				(List<DocumentFolder>)page1.getItems();

			Assert.assertEquals(
				documentFolders1.toString(), totalCount + 2,
				documentFolders1.size());

			Page<DocumentFolder> page2 =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentFolder> documentFolders2 =
				(List<DocumentFolder>)page2.getItems();

			Assert.assertEquals(
				documentFolders2.toString(), 1, documentFolders2.size());

			Page<DocumentFolder> page3 =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(
				documentFolder1, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithSortDateTime()
		throws Exception {

		testGetSiteDocumentFoldersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithSortDouble()
		throws Exception {

		testGetSiteDocumentFoldersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					documentFolder2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithSortInteger()
		throws Exception {

		testGetSiteDocumentFoldersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, documentFolder1, documentFolder2) -> {
				BeanTestUtil.setProperty(
					documentFolder1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					documentFolder2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteDocumentFoldersPageWithSortString()
		throws Exception {

		testGetSiteDocumentFoldersPageWithSort(
			EntityField.Type.STRING,
			(entityField, documentFolder1, documentFolder2) -> {
				Class<?> clazz = documentFolder1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						documentFolder1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						documentFolder2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteDocumentFoldersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, DocumentFolder, DocumentFolder, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteDocumentFoldersPage_getSiteId();

		DocumentFolder documentFolder1 = randomDocumentFolder();
		DocumentFolder documentFolder2 = randomDocumentFolder();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, documentFolder1, documentFolder2);
		}

		documentFolder1 = testGetSiteDocumentFoldersPage_addDocumentFolder(
			siteId, documentFolder1);

		documentFolder2 = testGetSiteDocumentFoldersPage_addDocumentFolder(
			siteId, documentFolder2);

		Page<DocumentFolder> page =
			documentFolderResource.getSiteDocumentFoldersPage(
				siteId, null, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<DocumentFolder> ascPage =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				documentFolder1, (List<DocumentFolder>)ascPage.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)ascPage.getItems());

			Page<DocumentFolder> descPage =
				documentFolderResource.getSiteDocumentFoldersPage(
					siteId, null, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				documentFolder2, (List<DocumentFolder>)descPage.getItems());
			assertContains(
				documentFolder1, (List<DocumentFolder>)descPage.getItems());
		}
	}

	protected DocumentFolder testGetSiteDocumentFoldersPage_addDocumentFolder(
			Long siteId, DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			siteId, documentFolder);
	}

	protected Long testGetSiteDocumentFoldersPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteDocumentFoldersPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentFoldersPage() throws Exception {
		Long siteId = testGetSiteDocumentFoldersPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documentFolders",
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

		JSONObject documentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentFolders");

		long totalCount = documentFoldersJSONObject.getLong("totalCount");

		DocumentFolder documentFolder1 =
			testGraphQLSiteDocumentFolder_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGraphQLSiteDocumentFolder_addDocumentFolder(
				siteId, randomDocumentFolder());

		documentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentFolders");

		Assert.assertEquals(
			totalCount + 2, documentFoldersJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersJSONObject.getString("items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentFoldersJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentFolders");

		Assert.assertEquals(
			totalCount + 2, documentFoldersJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersJSONObject.getString("items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteDocumentFoldersRatedByMePage() throws Exception {
		Long siteId = testGetSiteDocumentFoldersRatedByMePage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDocumentFoldersRatedByMePage_getIrrelevantSiteId();

		Page<DocumentFolder> page =
			documentFolderResource.getSiteDocumentFoldersRatedByMePage(
				siteId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			DocumentFolder irrelevantDocumentFolder =
				testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
					irrelevantSiteId, randomIrrelevantDocumentFolder());

			page = documentFolderResource.getSiteDocumentFoldersRatedByMePage(
				irrelevantSiteId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentFolder,
				(List<DocumentFolder>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentFoldersRatedByMePage_getExpectedActions(
					irrelevantSiteId));
		}

		DocumentFolder documentFolder1 =
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, randomDocumentFolder());

		page = documentFolderResource.getSiteDocumentFoldersRatedByMePage(
			siteId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(documentFolder1, (List<DocumentFolder>)page.getItems());
		assertContains(documentFolder2, (List<DocumentFolder>)page.getItems());
		assertValid(
			page,
			testGetSiteDocumentFoldersRatedByMePage_getExpectedActions(siteId));

		documentFolderResource.deleteDocumentFolder(documentFolder1.getId());

		documentFolderResource.deleteDocumentFolder(documentFolder2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentFoldersRatedByMePage_getExpectedActions(
				Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentFoldersRatedByMePageWithPagination()
		throws Exception {

		Long siteId = testGetSiteDocumentFoldersRatedByMePage_getSiteId();

		Page<DocumentFolder> documentFoldersPage =
			documentFolderResource.getSiteDocumentFoldersRatedByMePage(
				siteId, null);

		int totalCount = GetterUtil.getInteger(
			documentFoldersPage.getTotalCount());

		DocumentFolder documentFolder1 =
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder3 =
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				siteId, randomDocumentFolder());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentFolder> page1 =
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentFolder1, (List<DocumentFolder>)page1.getItems());

			Page<DocumentFolder> page2 =
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentFolder2, (List<DocumentFolder>)page2.getItems());

			Page<DocumentFolder> page3 =
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
		else {
			Page<DocumentFolder> page1 =
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					siteId, Pagination.of(1, totalCount + 2));

			List<DocumentFolder> documentFolders1 =
				(List<DocumentFolder>)page1.getItems();

			Assert.assertEquals(
				documentFolders1.toString(), totalCount + 2,
				documentFolders1.size());

			Page<DocumentFolder> page2 =
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					siteId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentFolder> documentFolders2 =
				(List<DocumentFolder>)page2.getItems();

			Assert.assertEquals(
				documentFolders2.toString(), 1, documentFolders2.size());

			Page<DocumentFolder> page3 =
				documentFolderResource.getSiteDocumentFoldersRatedByMePage(
					siteId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				documentFolder1, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder2, (List<DocumentFolder>)page3.getItems());
			assertContains(
				documentFolder3, (List<DocumentFolder>)page3.getItems());
		}
	}

	protected DocumentFolder
			testGetSiteDocumentFoldersRatedByMePage_addDocumentFolder(
				Long siteId, DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			siteId, documentFolder);
	}

	protected Long testGetSiteDocumentFoldersRatedByMePage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteDocumentFoldersRatedByMePage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentFoldersRatedByMePage()
		throws Exception {

		Long siteId = testGetSiteDocumentFoldersRatedByMePage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documentFoldersRatedByMe",
			new HashMap<String, Object>() {
				{
					put("siteKey", "\"" + siteId + "\"");
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject documentFoldersRatedByMeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/documentFoldersRatedByMe");

		long totalCount = documentFoldersRatedByMeJSONObject.getLong(
			"totalCount");

		DocumentFolder documentFolder1 =
			testGraphQLSiteDocumentFolder_addDocumentFolder(
				siteId, randomDocumentFolder());

		DocumentFolder documentFolder2 =
			testGraphQLSiteDocumentFolder_addDocumentFolder(
				siteId, randomDocumentFolder());

		documentFoldersRatedByMeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentFoldersRatedByMe");

		Assert.assertEquals(
			totalCount + 2,
			documentFoldersRatedByMeJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersRatedByMeJSONObject.getString("items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersRatedByMeJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentFoldersRatedByMeJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentFoldersRatedByMe");

		Assert.assertEquals(
			totalCount + 2,
			documentFoldersRatedByMeJSONObject.getLong("totalCount"));

		assertContains(
			documentFolder1,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersRatedByMeJSONObject.getString("items"))));
		assertContains(
			documentFolder2,
			Arrays.asList(
				DocumentFolderSerDes.toDTOs(
					documentFoldersRatedByMeJSONObject.getString("items"))));
	}

	@Test
	public void testGetSiteDocumentsFolderByExternalReferenceCode()
		throws Exception {

		DocumentFolder postDocumentFolder =
			testGetSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder();

		DocumentFolder getDocumentFolder =
			documentFolderResource.
				getSiteDocumentsFolderByExternalReferenceCode(
					postDocumentFolder.getSiteId(),
					postDocumentFolder.getExternalReferenceCode());

		assertEquals(postDocumentFolder, getDocumentFolder);
		assertValid(getDocumentFolder);
	}

	protected DocumentFolder
			testGetSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testGraphQLGetSiteDocumentsFolderByExternalReferenceCode()
		throws Exception {

		DocumentFolder documentFolder =
			testGraphQLGetSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder();

		// No namespace

		Assert.assertTrue(
			equals(
				documentFolder,
				DocumentFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentsFolderByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + documentFolder.getSiteId() +
												"\"");
										put(
											"externalReferenceCode",
											"\"" +
												documentFolder.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/documentsFolderByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentFolder,
				DocumentFolderSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentsFolderByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													documentFolder.getSiteId() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													documentFolder.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentsFolderByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteDocumentsFolderByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentsFolderByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + irrelevantGroup.getGroupId() + "\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
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
							"documentsFolderByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + irrelevantGroup.getGroupId() +
											"\"");
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentFolder
			testGraphQLGetSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder()
		throws Exception {

		return testGraphQLSiteDocumentFolder_addDocumentFolder();
	}

	@Test
	public void testPatchDocumentFolder() throws Exception {
		DocumentFolder postDocumentFolder =
			testPatchDocumentFolder_addDocumentFolder();

		DocumentFolder randomPatchDocumentFolder = randomPatchDocumentFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder patchDocumentFolder =
			documentFolderResource.patchDocumentFolder(
				postDocumentFolder.getId(), randomPatchDocumentFolder);

		DocumentFolder expectedPatchDocumentFolder = postDocumentFolder.clone();

		BeanTestUtil.copyProperties(
			randomPatchDocumentFolder, expectedPatchDocumentFolder);

		DocumentFolder getDocumentFolder =
			documentFolderResource.getDocumentFolder(
				patchDocumentFolder.getId());

		assertEquals(expectedPatchDocumentFolder, getDocumentFolder);
		assertValid(getDocumentFolder);
	}

	protected DocumentFolder testPatchDocumentFolder_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testPostAssetLibraryDocumentFolder() throws Exception {
		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder postDocumentFolder =
			testPostAssetLibraryDocumentFolder_addDocumentFolder(
				randomDocumentFolder);

		assertEquals(randomDocumentFolder, postDocumentFolder);
		assertValid(postDocumentFolder);
	}

	protected DocumentFolder
			testPostAssetLibraryDocumentFolder_addDocumentFolder(
				DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postAssetLibraryDocumentFolder(
			testGetAssetLibraryDocumentFoldersPage_getAssetLibraryId(),
			documentFolder);
	}

	@Test
	public void testGraphQLPostAssetLibraryDocumentFolder() throws Exception {
		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder documentFolder =
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				testDepotEntry.getDepotEntryId(), randomDocumentFolder);

		Assert.assertTrue(equals(randomDocumentFolder, documentFolder));
	}

	@Test
	public void testPostDocumentFolderDocumentFolder() throws Exception {
		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder postDocumentFolder =
			testPostDocumentFolderDocumentFolder_addDocumentFolder(
				randomDocumentFolder);

		assertEquals(randomDocumentFolder, postDocumentFolder);
		assertValid(postDocumentFolder);
	}

	protected DocumentFolder
			testPostDocumentFolderDocumentFolder_addDocumentFolder(
				DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postDocumentFolderDocumentFolder(
			testGetDocumentFolderDocumentFoldersPage_getParentDocumentFolderId(),
			documentFolder);
	}

	@Test
	public void testGraphQLPostDocumentFolderDocumentFolder() throws Exception {
		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder documentFolder =
			testGraphQLDocumentFolder_addDocumentFolder(
				testGroup.getGroupId(), randomDocumentFolder);

		Assert.assertTrue(equals(randomDocumentFolder, documentFolder));
	}

	@Test
	public void testPostSiteDocumentFolder() throws Exception {
		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder postDocumentFolder =
			testPostSiteDocumentFolder_addDocumentFolder(randomDocumentFolder);

		assertEquals(randomDocumentFolder, postDocumentFolder);
		assertValid(postDocumentFolder);
	}

	protected DocumentFolder testPostSiteDocumentFolder_addDocumentFolder(
			DocumentFolder documentFolder)
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGetSiteDocumentFoldersPage_getSiteId(), documentFolder);
	}

	@Test
	public void testGraphQLPostSiteDocumentFolder() throws Exception {
		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder documentFolder =
			testGraphQLSiteDocumentFolder_addDocumentFolder(
				testGroup.getGroupId(), randomDocumentFolder);

		Assert.assertTrue(equals(randomDocumentFolder, documentFolder));
	}

	@Test
	public void testPutAssetLibraryDocumentFolderPermissionsPage()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testPutAssetLibraryDocumentFolderPermissionsPage_addDocumentFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			documentFolderResource.
				putAssetLibraryDocumentFolderPermissionsPageHttpResponse(
					testDepotEntry.getDepotEntryId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"PERMISSIONS"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.
				putAssetLibraryDocumentFolderPermissionsPageHttpResponse(
					testDepotEntry.getDepotEntryId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected DocumentFolder
			testPutAssetLibraryDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postAssetLibraryDocumentFolder(
			testDepotEntry.getDepotEntryId(), randomDocumentFolder());
	}

	@Test
	public void testPutDocumentFolder() throws Exception {
		DocumentFolder postDocumentFolder =
			testPutDocumentFolder_addDocumentFolder();

		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder putDocumentFolder =
			documentFolderResource.putDocumentFolder(
				postDocumentFolder.getId(), randomDocumentFolder);

		assertEquals(randomDocumentFolder, putDocumentFolder);
		assertValid(putDocumentFolder);

		DocumentFolder getDocumentFolder =
			documentFolderResource.getDocumentFolder(putDocumentFolder.getId());

		assertEquals(randomDocumentFolder, getDocumentFolder);
		assertValid(getDocumentFolder);
	}

	protected DocumentFolder testPutDocumentFolder_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testPutDocumentFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testPutDocumentFolderPermissionsPage_addDocumentFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			documentFolderResource.putDocumentFolderPermissionsPageHttpResponse(
				documentFolder.getId(),
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
			documentFolderResource.putDocumentFolderPermissionsPageHttpResponse(
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

	protected DocumentFolder
			testPutDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testPutDocumentFolderSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testPutDocumentFolderSubscribe_addDocumentFolder();

		assertHttpResponseStatusCode(
			204,
			documentFolderResource.putDocumentFolderSubscribeHttpResponse(
				documentFolder.getId()));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.putDocumentFolderSubscribeHttpResponse(0L));
	}

	protected DocumentFolder testPutDocumentFolderSubscribe_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testPutDocumentFolderUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testPutDocumentFolderUnsubscribe_addDocumentFolder();

		assertHttpResponseStatusCode(
			204,
			documentFolderResource.putDocumentFolderUnsubscribeHttpResponse(
				documentFolder.getId()));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.putDocumentFolderUnsubscribeHttpResponse(
				0L));
	}

	protected DocumentFolder
			testPutDocumentFolderUnsubscribe_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testPutSiteDocumentFolderPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentFolder documentFolder =
			testPutSiteDocumentFolderPermissionsPage_addDocumentFolder();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			documentFolderResource.
				putSiteDocumentFolderPermissionsPageHttpResponse(
					testGroup.getGroupId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"PERMISSIONS"});
								setRoleName(role.getName());
							}
						}
					}));

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.
				putSiteDocumentFolderPermissionsPageHttpResponse(
					testGroup.getGroupId(),
					new Permission[] {
						new Permission() {
							{
								setActionIds(new String[] {"-"});
								setRoleName("-");
							}
						}
					}));
	}

	protected DocumentFolder
			testPutSiteDocumentFolderPermissionsPage_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	@Test
	public void testPutSiteDocumentsFolderByExternalReferenceCode()
		throws Exception {

		DocumentFolder postDocumentFolder =
			testPutSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder();

		DocumentFolder randomDocumentFolder = randomDocumentFolder();

		DocumentFolder putDocumentFolder =
			documentFolderResource.
				putSiteDocumentsFolderByExternalReferenceCode(
					postDocumentFolder.getSiteId(),
					postDocumentFolder.getExternalReferenceCode(),
					randomDocumentFolder);

		assertEquals(randomDocumentFolder, putDocumentFolder);
		assertValid(putDocumentFolder);

		DocumentFolder getDocumentFolder =
			documentFolderResource.
				getSiteDocumentsFolderByExternalReferenceCode(
					putDocumentFolder.getSiteId(),
					putDocumentFolder.getExternalReferenceCode());

		assertEquals(randomDocumentFolder, getDocumentFolder);
		assertValid(getDocumentFolder);

		DocumentFolder newDocumentFolder =
			testPutSiteDocumentsFolderByExternalReferenceCode_createDocumentFolder();

		putDocumentFolder =
			documentFolderResource.
				putSiteDocumentsFolderByExternalReferenceCode(
					newDocumentFolder.getSiteId(),
					newDocumentFolder.getExternalReferenceCode(),
					newDocumentFolder);

		assertEquals(newDocumentFolder, putDocumentFolder);
		assertValid(putDocumentFolder);

		getDocumentFolder =
			documentFolderResource.
				getSiteDocumentsFolderByExternalReferenceCode(
					putDocumentFolder.getSiteId(),
					putDocumentFolder.getExternalReferenceCode());

		assertEquals(newDocumentFolder, getDocumentFolder);

		Assert.assertEquals(
			newDocumentFolder.getExternalReferenceCode(),
			putDocumentFolder.getExternalReferenceCode());
	}

	protected DocumentFolder
			testPutSiteDocumentsFolderByExternalReferenceCode_addDocumentFolder()
		throws Exception {

		return documentFolderResource.postSiteDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	protected DocumentFolder
			testPutSiteDocumentsFolderByExternalReferenceCode_createDocumentFolder()
		throws Exception {

		return randomDocumentFolder();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DocumentFolder documentFolder1 =
			testBatchEngineDeleteImportTask_addDocumentFolder();

		testBatchEngineDeleteImportTask_deleteDocumentFolder(
			200, null, documentFolder1.getId());

		assertHttpResponseStatusCode(
			404,
			documentFolderResource.getDocumentFolderHttpResponse(
				documentFolder1.getId()));
	}

	protected DocumentFolder testBatchEngineDeleteImportTask_addDocumentFolder()
		throws Exception {

		return testDeleteDocumentFolder_addDocumentFolder();
	}

	protected void testBatchEngineDeleteImportTask_deleteDocumentFolder(
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
				"com.liferay.headless.delivery.dto.v1_0.DocumentFolder", null,
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

	@Test
	public void testGetDocumentFolderMyRating() throws Exception {
		DocumentFolder postDocumentFolder =
			testGetDocumentFolder_addDocumentFolder();

		Rating postRating = testGetDocumentFolderMyRating_addRating(
			postDocumentFolder.getId(), randomRating());

		Rating getRating = documentFolderResource.getDocumentFolderMyRating(
			postDocumentFolder.getId());

		assertEquals(postRating, getRating);
		assertValid(getRating);
	}

	protected Rating testGetDocumentFolderMyRating_addRating(
			long documentFolderId, Rating rating)
		throws Exception {

		return documentFolderResource.postDocumentFolderMyRating(
			documentFolderId, rating);
	}

	@Test
	public void testPostDocumentFolderMyRating() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testPutDocumentFolderMyRating() throws Exception {
		DocumentFolder postDocumentFolder =
			testPutDocumentFolder_addDocumentFolder();

		testPutDocumentFolderMyRating_addRating(
			postDocumentFolder.getId(), randomRating());

		Rating randomRating = randomRating();

		Rating putRating = documentFolderResource.putDocumentFolderMyRating(
			postDocumentFolder.getId(), randomRating);

		assertEquals(randomRating, putRating);
		assertValid(putRating);
	}

	protected Rating testPutDocumentFolderMyRating_addRating(
			long documentFolderId, Rating rating)
		throws Exception {

		return documentFolderResource.postDocumentFolderMyRating(
			documentFolderId, rating);
	}

	protected DocumentFolder testGraphQLDocumentFolder_addDocumentFolder()
		throws Exception {

		return testGraphQLDocumentFolder_addDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	protected DocumentFolder testGraphQLDocumentFolder_addDocumentFolder(
			Long siteId, DocumentFolder documentFolder)
		throws Exception {

		JSONDeserializer<DocumentFolder> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentFolder.class)) {

			if (getGraphQLValue(field.get(documentFolder)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(documentFolder)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentFolder",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("documentFolder", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocumentFolder"),
			DocumentFolder.class);
	}

	protected DocumentFolder testGraphQLSiteDocumentFolder_addDocumentFolder()
		throws Exception {

		return testGraphQLSiteDocumentFolder_addDocumentFolder(
			testGroup.getGroupId(), randomDocumentFolder());
	}

	protected DocumentFolder testGraphQLSiteDocumentFolder_addDocumentFolder(
			Long siteId, DocumentFolder documentFolder)
		throws Exception {

		JSONDeserializer<DocumentFolder> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentFolder.class)) {

			if (getGraphQLValue(field.get(documentFolder)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(documentFolder)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentFolder",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("documentFolder", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocumentFolder"),
			DocumentFolder.class);
	}

	protected DocumentFolder
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder()
		throws Exception {

		return testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
			testDepotEntry.getDepotEntryId(), randomDocumentFolder());
	}

	protected DocumentFolder
			testGraphQLAssetLibraryDocumentFolder_addDocumentFolder(
				Long assetLibraryId, DocumentFolder documentFolder)
		throws Exception {

		JSONDeserializer<DocumentFolder> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentFolder.class)) {

			if (getGraphQLValue(field.get(documentFolder)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(documentFolder)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryDocumentFolder",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put("documentFolder", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryDocumentFolder"),
			DocumentFolder.class);
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
		DocumentFolder documentFolder, List<DocumentFolder> documentFolders) {

		boolean contains = false;

		for (DocumentFolder item : documentFolders) {
			if (equals(documentFolder, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			documentFolders + " does not contain " + documentFolder, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DocumentFolder documentFolder1, DocumentFolder documentFolder2) {

		Assert.assertTrue(
			documentFolder1 + " does not equal " + documentFolder2,
			equals(documentFolder1, documentFolder2));
	}

	protected void assertEquals(
		List<DocumentFolder> documentFolders1,
		List<DocumentFolder> documentFolders2) {

		Assert.assertEquals(documentFolders1.size(), documentFolders2.size());

		for (int i = 0; i < documentFolders1.size(); i++) {
			DocumentFolder documentFolder1 = documentFolders1.get(i);
			DocumentFolder documentFolder2 = documentFolders2.get(i);

			assertEquals(documentFolder1, documentFolder2);
		}
	}

	protected void assertEquals(Rating rating1, Rating rating2) {
		Assert.assertTrue(
			rating1 + " does not equal " + rating2, equals(rating1, rating2));
	}

	protected void assertEqualsIgnoringOrder(
		List<DocumentFolder> documentFolders1,
		List<DocumentFolder> documentFolders2) {

		Assert.assertEquals(documentFolders1.size(), documentFolders2.size());

		for (DocumentFolder documentFolder1 : documentFolders1) {
			boolean contains = false;

			for (DocumentFolder documentFolder2 : documentFolders2) {
				if (equals(documentFolder1, documentFolder2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				documentFolders2 + " does not contain " + documentFolder1,
				contains);
		}
	}

	protected void assertValid(DocumentFolder documentFolder) throws Exception {
		boolean valid = true;

		if (documentFolder.getDateCreated() == null) {
			valid = false;
		}

		if (documentFolder.getDateModified() == null) {
			valid = false;
		}

		if (documentFolder.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				documentFolder.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				documentFolder.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (documentFolder.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (documentFolder.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (documentFolder.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (documentFolder.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (documentFolder.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (documentFolder.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (documentFolder.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfDocumentFolders", additionalAssertFieldName)) {

				if (documentFolder.getNumberOfDocumentFolders() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfDocuments", additionalAssertFieldName)) {

				if (documentFolder.getNumberOfDocuments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentDocumentFolderId", additionalAssertFieldName)) {

				if (documentFolder.getParentDocumentFolderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (documentFolder.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (documentFolder.getViewableBy() == null) {
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

	protected void assertValid(Page<DocumentFolder> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DocumentFolder> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DocumentFolder> documentFolders = page.getItems();

		int size = documentFolders.size();

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

	protected void assertValid(Rating rating) {
		boolean valid = true;

		if (rating.getDateCreated() == null) {
			valid = false;
		}

		if (rating.getDateModified() == null) {
			valid = false;
		}

		if (rating.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (rating.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (rating.getBestRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (rating.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (rating.getRatingValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (rating.getWorstRating() == null) {
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

	protected String[] getAdditionalAssertFieldNames() {
		return new String[0];
	}

	protected String[] getAdditionalRatingAssertFieldNames() {
		return new String[0];
	}

	protected List<GraphQLField> getGraphQLFields() throws Exception {
		List<GraphQLField> graphQLFields = new ArrayList<>();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.delivery.dto.v1_0.DocumentFolder.
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
		DocumentFolder documentFolder1, DocumentFolder documentFolder2) {

		if (documentFolder1 == documentFolder2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentFolder1.getActions(),
						(Map)documentFolder2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getCreator(),
						documentFolder2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getCustomFields(),
						documentFolder2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getDateCreated(),
						documentFolder2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getDateModified(),
						documentFolder2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getDescription(),
						documentFolder2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentFolder1.getExternalReferenceCode(),
						documentFolder2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getId(), documentFolder2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getName(), documentFolder2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfDocumentFolders", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentFolder1.getNumberOfDocumentFolders(),
						documentFolder2.getNumberOfDocumentFolders())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfDocuments", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentFolder1.getNumberOfDocuments(),
						documentFolder2.getNumberOfDocuments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentDocumentFolderId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentFolder1.getParentDocumentFolderId(),
						documentFolder2.getParentDocumentFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getSubscribed(),
						documentFolder2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentFolder1.getViewableBy(),
						documentFolder2.getViewableBy())) {

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

	protected boolean equals(Rating rating1, Rating rating2) {
		if (rating1 == rating2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalRatingAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getActions(), rating2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("bestRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getBestRating(), rating2.getBestRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getCreator(), rating2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateCreated(), rating2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getDateModified(), rating2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(rating1.getId(), rating2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("ratingValue", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getRatingValue(), rating2.getRatingValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("worstRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						rating1.getWorstRating(), rating2.getWorstRating())) {

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

		if (!(_documentFolderResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_documentFolderResource;

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
		DocumentFolder documentFolder) {

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
			Object object = documentFolder.getAssetLibraryKey();

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

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = documentFolder.getDateCreated();

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

				sb.append(_format.format(documentFolder.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = documentFolder.getDateModified();

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

				sb.append(_format.format(documentFolder.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = documentFolder.getDescription();

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
			Object object = documentFolder.getExternalReferenceCode();

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
			Object object = documentFolder.getName();

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

		if (entityFieldName.equals("numberOfDocumentFolders")) {
			sb.append(
				String.valueOf(documentFolder.getNumberOfDocumentFolders()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfDocuments")) {
			sb.append(String.valueOf(documentFolder.getNumberOfDocuments()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentDocumentFolderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("subscribed")) {
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

	protected DocumentFolder randomDocumentFolder() throws Exception {
		return new DocumentFolder() {
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
				numberOfDocumentFolders = RandomTestUtil.randomInt();
				numberOfDocuments = RandomTestUtil.randomInt();
				parentDocumentFolderId = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected DocumentFolder randomIrrelevantDocumentFolder() throws Exception {
		DocumentFolder randomIrrelevantDocumentFolder = randomDocumentFolder();

		randomIrrelevantDocumentFolder.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantDocumentFolder.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantDocumentFolder;
	}

	protected DocumentFolder randomPatchDocumentFolder() throws Exception {
		return randomDocumentFolder();
	}

	protected Rating randomRating() throws Exception {
		return new Rating() {
			{
				bestRating = RandomTestUtil.randomDouble();
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				id = RandomTestUtil.randomLong();
				ratingValue = RandomTestUtil.randomDouble();
				worstRating = RandomTestUtil.randomDouble();
			}
		};
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

	protected DocumentFolderResource documentFolderResource;
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
		LogFactoryUtil.getLog(BaseDocumentFolderResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.DocumentFolderResource
		_documentFolderResource;

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