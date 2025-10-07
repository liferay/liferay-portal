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
import com.liferay.headless.delivery.client.dto.v1_0.DocumentShortcut;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.resource.v1_0.DocumentShortcutResource;
import com.liferay.headless.delivery.client.serdes.v1_0.DocumentShortcutSerDes;
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
public abstract class BaseDocumentShortcutResourceTestCase {

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

		_documentShortcutResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		documentShortcutResource = DocumentShortcutResource.builder(
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

		DocumentShortcut documentShortcut1 = randomDocumentShortcut();

		String json = objectMapper.writeValueAsString(documentShortcut1);

		DocumentShortcut documentShortcut2 = DocumentShortcutSerDes.toDTO(json);

		Assert.assertTrue(equals(documentShortcut1, documentShortcut2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		DocumentShortcut documentShortcut = randomDocumentShortcut();

		String json1 = objectMapper.writeValueAsString(documentShortcut);
		String json2 = DocumentShortcutSerDes.toJSON(documentShortcut);

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

		DocumentShortcut documentShortcut = randomDocumentShortcut();

		documentShortcut.setAssetLibraryKey(regex);
		documentShortcut.setExternalReferenceCode(regex);
		documentShortcut.setTitle(regex);

		String json = DocumentShortcutSerDes.toJSON(documentShortcut);

		Assert.assertFalse(json.contains(regex));

		documentShortcut = DocumentShortcutSerDes.toDTO(json);

		Assert.assertEquals(regex, documentShortcut.getAssetLibraryKey());
		Assert.assertEquals(regex, documentShortcut.getExternalReferenceCode());
		Assert.assertEquals(regex, documentShortcut.getTitle());
	}

	@Test
	public void testDeleteDocumentShortcut() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentShortcut documentShortcut =
			testDeleteDocumentShortcut_addDocumentShortcut();

		assertHttpResponseStatusCode(
			204,
			documentShortcutResource.deleteDocumentShortcutHttpResponse(
				documentShortcut.getId()));

		assertHttpResponseStatusCode(
			404,
			documentShortcutResource.getDocumentShortcutHttpResponse(
				documentShortcut.getId()));
		assertHttpResponseStatusCode(
			404, documentShortcutResource.getDocumentShortcutHttpResponse(0L));
	}

	protected DocumentShortcut testDeleteDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	@Test
	public void testGraphQLDeleteDocumentShortcut() throws Exception {

		// No namespace

		DocumentShortcut documentShortcut1 =
			testGraphQLDeleteDocumentShortcut_addDocumentShortcut();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteDocumentShortcut",
						new HashMap<String, Object>() {
							{
								put(
									"documentShortcutId",
									documentShortcut1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteDocumentShortcut"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentShortcut",
					new HashMap<String, Object>() {
						{
							put(
								"documentShortcutId",
								documentShortcut1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentShortcut documentShortcut2 =
			testGraphQLDeleteDocumentShortcut_addDocumentShortcut();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteDocumentShortcut",
							new HashMap<String, Object>() {
								{
									put(
										"documentShortcutId",
										documentShortcut2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteDocumentShortcut"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentShortcut",
						new HashMap<String, Object>() {
							{
								put(
									"documentShortcutId",
									documentShortcut2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentShortcut
			testGraphQLDeleteDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return testGraphQLDocumentShortcut_addDocumentShortcut();
	}

	@Test
	public void testDeleteDocumentShortcutBatch() throws Exception {
		DocumentShortcut documentShortcut1 =
			testDeleteDocumentShortcutBatch_addDocumentShortcut();

		testDeleteDocumentShortcutBatch_deleteDocumentShortcut(
			202, null, documentShortcut1.getId());

		assertHttpResponseStatusCode(
			404,
			documentShortcutResource.getDocumentShortcutHttpResponse(
				documentShortcut1.getId()));
	}

	protected DocumentShortcut
			testDeleteDocumentShortcutBatch_addDocumentShortcut()
		throws Exception {

		return testDeleteDocumentShortcut_addDocumentShortcut();
	}

	protected void testDeleteDocumentShortcutBatch_deleteDocumentShortcut(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			documentShortcutResource.deleteDocumentShortcutBatchHttpResponse(
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
	public void testDeleteSiteDocumentShortcutByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentShortcut documentShortcut =
			testDeleteSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut();

		assertHttpResponseStatusCode(
			204,
			documentShortcutResource.
				deleteSiteDocumentShortcutByExternalReferenceCodeHttpResponse(
					documentShortcut.getSiteId(),
					documentShortcut.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			documentShortcutResource.
				getSiteDocumentShortcutByExternalReferenceCodeHttpResponse(
					documentShortcut.getSiteId(),
					documentShortcut.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			documentShortcutResource.
				getSiteDocumentShortcutByExternalReferenceCodeHttpResponse(
					documentShortcut.getSiteId(), "-"));
	}

	protected DocumentShortcut
			testDeleteSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	@Test
	public void testGraphQLDeleteSiteDocumentShortcutByExternalReferenceCode()
		throws Exception {

		// No namespace

		DocumentShortcut documentShortcut1 =
			testGraphQLDeleteSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteDocumentShortcutByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + documentShortcut1.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										documentShortcut1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteDocumentShortcutByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"documentShortcutByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"siteKey",
								"\"" + documentShortcut1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" +
									documentShortcut1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		DocumentShortcut documentShortcut2 =
			testGraphQLDeleteSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteDocumentShortcutByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + documentShortcut2.getSiteId() +
											"\"");
									put(
										"externalReferenceCode",
										"\"" +
											documentShortcut2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteDocumentShortcutByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"documentShortcutByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + documentShortcut2.getSiteId() +
										"\"");
								put(
									"externalReferenceCode",
									"\"" +
										documentShortcut2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected DocumentShortcut
			testGraphQLDeleteSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut()
		throws Exception {

		return testGraphQLSiteDocumentShortcut_addDocumentShortcut();
	}

	@Test
	public void testGetAssetLibraryDocumentShortcutsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryDocumentShortcutsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryDocumentShortcutsPage_getIrrelevantAssetLibraryId();

		Page<DocumentShortcut> page =
			documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
				assetLibraryId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			DocumentShortcut irrelevantDocumentShortcut =
				testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
					irrelevantAssetLibraryId,
					randomIrrelevantDocumentShortcut());

			page =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					irrelevantAssetLibraryId,
					Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentShortcut,
				(List<DocumentShortcut>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryDocumentShortcutsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		DocumentShortcut documentShortcut1 =
			testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		DocumentShortcut documentShortcut2 =
			testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		page = documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
			assetLibraryId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			documentShortcut1, (List<DocumentShortcut>)page.getItems());
		assertContains(
			documentShortcut2, (List<DocumentShortcut>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryDocumentShortcutsPage_getExpectedActions(
				assetLibraryId));

		documentShortcutResource.deleteDocumentShortcut(
			documentShortcut1.getId());

		documentShortcutResource.deleteDocumentShortcut(
			documentShortcut2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryDocumentShortcutsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/asset-libraries/{assetLibraryId}/document-shortcuts/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryDocumentShortcutsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentShortcutsPage_getAssetLibraryId();

		Page<DocumentShortcut> documentShortcutsPage =
			documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
				assetLibraryId, null);

		int totalCount = GetterUtil.getInteger(
			documentShortcutsPage.getTotalCount());

		DocumentShortcut documentShortcut1 =
			testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		DocumentShortcut documentShortcut2 =
			testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		DocumentShortcut documentShortcut3 =
			testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentShortcut> page1 =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					assetLibraryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentShortcut1, (List<DocumentShortcut>)page1.getItems());

			Page<DocumentShortcut> page2 =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					assetLibraryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentShortcut2, (List<DocumentShortcut>)page2.getItems());

			Page<DocumentShortcut> page3 =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					assetLibraryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentShortcut3, (List<DocumentShortcut>)page3.getItems());
		}
		else {
			Page<DocumentShortcut> page1 =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					assetLibraryId, Pagination.of(1, totalCount + 2));

			List<DocumentShortcut> documentShortcuts1 =
				(List<DocumentShortcut>)page1.getItems();

			Assert.assertEquals(
				documentShortcuts1.toString(), totalCount + 2,
				documentShortcuts1.size());

			Page<DocumentShortcut> page2 =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					assetLibraryId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentShortcut> documentShortcuts2 =
				(List<DocumentShortcut>)page2.getItems();

			Assert.assertEquals(
				documentShortcuts2.toString(), 1, documentShortcuts2.size());

			Page<DocumentShortcut> page3 =
				documentShortcutResource.getAssetLibraryDocumentShortcutsPage(
					assetLibraryId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				documentShortcut1, (List<DocumentShortcut>)page3.getItems());
			assertContains(
				documentShortcut2, (List<DocumentShortcut>)page3.getItems());
			assertContains(
				documentShortcut3, (List<DocumentShortcut>)page3.getItems());
		}
	}

	protected DocumentShortcut
			testGetAssetLibraryDocumentShortcutsPage_addDocumentShortcut(
				Long assetLibraryId, DocumentShortcut documentShortcut)
		throws Exception {

		return documentShortcutResource.postAssetLibraryDocumentShortcut(
			assetLibraryId, documentShortcut);
	}

	protected Long testGetAssetLibraryDocumentShortcutsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryDocumentShortcutsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGraphQLGetAssetLibraryDocumentShortcutsPage()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryDocumentShortcutsPage_getAssetLibraryId();

		GraphQLField graphQLField = new GraphQLField(
			"assetLibraryDocumentShortcuts",
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

		JSONObject assetLibraryDocumentShortcutsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/assetLibraryDocumentShortcuts");

		long totalCount = assetLibraryDocumentShortcutsJSONObject.getLong(
			"totalCount");

		DocumentShortcut documentShortcut1 =
			testGraphQLAssetLibraryDocumentShortcut_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		DocumentShortcut documentShortcut2 =
			testGraphQLAssetLibraryDocumentShortcut_addDocumentShortcut(
				assetLibraryId, randomDocumentShortcut());

		assetLibraryDocumentShortcutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/assetLibraryDocumentShortcuts");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentShortcutsJSONObject.getLong("totalCount"));

		assertContains(
			documentShortcut1,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					assetLibraryDocumentShortcutsJSONObject.getString(
						"items"))));
		assertContains(
			documentShortcut2,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					assetLibraryDocumentShortcutsJSONObject.getString(
						"items"))));

		// Using the namespace headlessDelivery_v1_0

		assetLibraryDocumentShortcutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/assetLibraryDocumentShortcuts");

		Assert.assertEquals(
			totalCount + 2,
			assetLibraryDocumentShortcutsJSONObject.getLong("totalCount"));

		assertContains(
			documentShortcut1,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					assetLibraryDocumentShortcutsJSONObject.getString(
						"items"))));
		assertContains(
			documentShortcut2,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					assetLibraryDocumentShortcutsJSONObject.getString(
						"items"))));
	}

	@Test
	public void testGetDocumentShortcut() throws Exception {
		DocumentShortcut postDocumentShortcut =
			testGetDocumentShortcut_addDocumentShortcut();

		DocumentShortcut getDocumentShortcut =
			documentShortcutResource.getDocumentShortcut(
				postDocumentShortcut.getId());

		assertEquals(postDocumentShortcut, getDocumentShortcut);
		assertValid(getDocumentShortcut);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		DocumentShortcut postDocumentShortcut =
			testGetDocumentShortcut_addDocumentShortcut();

		DocumentShortcut getDocumentShortcut =
			documentShortcutResource.getDocumentShortcut(
				postDocumentShortcut.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.delivery.dto.v1_0.DocumentShortcut"
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
			postDocumentShortcut.getId());

		assertEquals(
			getDocumentShortcut, DocumentShortcutSerDes.toDTO(item.toString()));
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

	protected DocumentShortcut testGetDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	@Test
	public void testGraphQLGetDocumentShortcut() throws Exception {
		DocumentShortcut documentShortcut =
			testGraphQLGetDocumentShortcut_addDocumentShortcut();

		// No namespace

		Assert.assertTrue(
			equals(
				documentShortcut,
				DocumentShortcutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentShortcut",
								new HashMap<String, Object>() {
									{
										put(
											"documentShortcutId",
											documentShortcut.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/documentShortcut"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentShortcut,
				DocumentShortcutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentShortcut",
									new HashMap<String, Object>() {
										{
											put(
												"documentShortcutId",
												documentShortcut.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentShortcut"))));
	}

	@Test
	public void testGraphQLGetDocumentShortcutNotFound() throws Exception {
		Long irrelevantDocumentShortcutId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentShortcut",
						new HashMap<String, Object>() {
							{
								put(
									"documentShortcutId",
									irrelevantDocumentShortcutId);
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
							"documentShortcut",
							new HashMap<String, Object>() {
								{
									put(
										"documentShortcutId",
										irrelevantDocumentShortcutId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected DocumentShortcut
			testGraphQLGetDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return testGraphQLDocumentShortcut_addDocumentShortcut();
	}

	@Test
	public void testGetSiteDocumentShortcutByExternalReferenceCode()
		throws Exception {

		DocumentShortcut postDocumentShortcut =
			testGetSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut();

		DocumentShortcut getDocumentShortcut =
			documentShortcutResource.
				getSiteDocumentShortcutByExternalReferenceCode(
					postDocumentShortcut.getSiteId(),
					postDocumentShortcut.getExternalReferenceCode());

		assertEquals(postDocumentShortcut, getDocumentShortcut);
		assertValid(getDocumentShortcut);
	}

	protected DocumentShortcut
			testGetSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	@Test
	public void testGraphQLGetSiteDocumentShortcutByExternalReferenceCode()
		throws Exception {

		DocumentShortcut documentShortcut =
			testGraphQLGetSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut();

		// No namespace

		Assert.assertTrue(
			equals(
				documentShortcut,
				DocumentShortcutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"documentShortcutByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												documentShortcut.getSiteId() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												documentShortcut.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/documentShortcutByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				documentShortcut,
				DocumentShortcutSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"documentShortcutByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													documentShortcut.
														getSiteId() + "\"");
											put(
												"externalReferenceCode",
												"\"" +
													documentShortcut.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/documentShortcutByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteDocumentShortcutByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"documentShortcutByExternalReferenceCode",
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
							"documentShortcutByExternalReferenceCode",
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

	protected DocumentShortcut
			testGraphQLGetSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut()
		throws Exception {

		return testGraphQLSiteDocumentShortcut_addDocumentShortcut();
	}

	@Test
	public void testGetSiteDocumentShortcutsPage() throws Exception {
		Long siteId = testGetSiteDocumentShortcutsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteDocumentShortcutsPage_getIrrelevantSiteId();

		Page<DocumentShortcut> page =
			documentShortcutResource.getSiteDocumentShortcutsPage(
				siteId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			DocumentShortcut irrelevantDocumentShortcut =
				testGetSiteDocumentShortcutsPage_addDocumentShortcut(
					irrelevantSiteId, randomIrrelevantDocumentShortcut());

			page = documentShortcutResource.getSiteDocumentShortcutsPage(
				irrelevantSiteId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantDocumentShortcut,
				(List<DocumentShortcut>)page.getItems());
			assertValid(
				page,
				testGetSiteDocumentShortcutsPage_getExpectedActions(
					irrelevantSiteId));
		}

		DocumentShortcut documentShortcut1 =
			testGetSiteDocumentShortcutsPage_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		DocumentShortcut documentShortcut2 =
			testGetSiteDocumentShortcutsPage_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		page = documentShortcutResource.getSiteDocumentShortcutsPage(
			siteId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			documentShortcut1, (List<DocumentShortcut>)page.getItems());
		assertContains(
			documentShortcut2, (List<DocumentShortcut>)page.getItems());
		assertValid(
			page, testGetSiteDocumentShortcutsPage_getExpectedActions(siteId));

		documentShortcutResource.deleteDocumentShortcut(
			documentShortcut1.getId());

		documentShortcutResource.deleteDocumentShortcut(
			documentShortcut2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteDocumentShortcutsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/sites/{siteId}/document-shortcuts/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteDocumentShortcutsPageWithPagination()
		throws Exception {

		Long siteId = testGetSiteDocumentShortcutsPage_getSiteId();

		Page<DocumentShortcut> documentShortcutsPage =
			documentShortcutResource.getSiteDocumentShortcutsPage(siteId, null);

		int totalCount = GetterUtil.getInteger(
			documentShortcutsPage.getTotalCount());

		DocumentShortcut documentShortcut1 =
			testGetSiteDocumentShortcutsPage_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		DocumentShortcut documentShortcut2 =
			testGetSiteDocumentShortcutsPage_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		DocumentShortcut documentShortcut3 =
			testGetSiteDocumentShortcutsPage_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<DocumentShortcut> page1 =
				documentShortcutResource.getSiteDocumentShortcutsPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				documentShortcut1, (List<DocumentShortcut>)page1.getItems());

			Page<DocumentShortcut> page2 =
				documentShortcutResource.getSiteDocumentShortcutsPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentShortcut2, (List<DocumentShortcut>)page2.getItems());

			Page<DocumentShortcut> page3 =
				documentShortcutResource.getSiteDocumentShortcutsPage(
					siteId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				documentShortcut3, (List<DocumentShortcut>)page3.getItems());
		}
		else {
			Page<DocumentShortcut> page1 =
				documentShortcutResource.getSiteDocumentShortcutsPage(
					siteId, Pagination.of(1, totalCount + 2));

			List<DocumentShortcut> documentShortcuts1 =
				(List<DocumentShortcut>)page1.getItems();

			Assert.assertEquals(
				documentShortcuts1.toString(), totalCount + 2,
				documentShortcuts1.size());

			Page<DocumentShortcut> page2 =
				documentShortcutResource.getSiteDocumentShortcutsPage(
					siteId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<DocumentShortcut> documentShortcuts2 =
				(List<DocumentShortcut>)page2.getItems();

			Assert.assertEquals(
				documentShortcuts2.toString(), 1, documentShortcuts2.size());

			Page<DocumentShortcut> page3 =
				documentShortcutResource.getSiteDocumentShortcutsPage(
					siteId, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				documentShortcut1, (List<DocumentShortcut>)page3.getItems());
			assertContains(
				documentShortcut2, (List<DocumentShortcut>)page3.getItems());
			assertContains(
				documentShortcut3, (List<DocumentShortcut>)page3.getItems());
		}
	}

	protected DocumentShortcut
			testGetSiteDocumentShortcutsPage_addDocumentShortcut(
				Long siteId, DocumentShortcut documentShortcut)
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			siteId, documentShortcut);
	}

	protected Long testGetSiteDocumentShortcutsPage_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testGetSiteDocumentShortcutsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGraphQLGetSiteDocumentShortcutsPage() throws Exception {
		Long siteId = testGetSiteDocumentShortcutsPage_getSiteId();

		GraphQLField graphQLField = new GraphQLField(
			"documentShortcuts",
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

		JSONObject documentShortcutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentShortcuts");

		long totalCount = documentShortcutsJSONObject.getLong("totalCount");

		DocumentShortcut documentShortcut1 =
			testGraphQLSiteDocumentShortcut_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		DocumentShortcut documentShortcut2 =
			testGraphQLSiteDocumentShortcut_addDocumentShortcut(
				siteId, randomDocumentShortcut());

		documentShortcutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/documentShortcuts");

		Assert.assertEquals(
			totalCount + 2, documentShortcutsJSONObject.getLong("totalCount"));

		assertContains(
			documentShortcut1,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					documentShortcutsJSONObject.getString("items"))));
		assertContains(
			documentShortcut2,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					documentShortcutsJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		documentShortcutsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/documentShortcuts");

		Assert.assertEquals(
			totalCount + 2, documentShortcutsJSONObject.getLong("totalCount"));

		assertContains(
			documentShortcut1,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					documentShortcutsJSONObject.getString("items"))));
		assertContains(
			documentShortcut2,
			Arrays.asList(
				DocumentShortcutSerDes.toDTOs(
					documentShortcutsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchDocumentShortcut() throws Exception {
		DocumentShortcut postDocumentShortcut =
			testPatchDocumentShortcut_addDocumentShortcut();

		DocumentShortcut randomPatchDocumentShortcut =
			randomPatchDocumentShortcut();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		DocumentShortcut patchDocumentShortcut =
			documentShortcutResource.patchDocumentShortcut(
				postDocumentShortcut.getId(), randomPatchDocumentShortcut);

		DocumentShortcut expectedPatchDocumentShortcut =
			postDocumentShortcut.clone();

		BeanTestUtil.copyProperties(
			randomPatchDocumentShortcut, expectedPatchDocumentShortcut);

		DocumentShortcut getDocumentShortcut =
			documentShortcutResource.getDocumentShortcut(
				patchDocumentShortcut.getId());

		assertEquals(expectedPatchDocumentShortcut, getDocumentShortcut);
		assertValid(getDocumentShortcut);
	}

	protected DocumentShortcut testPatchDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	@Test
	public void testPostAssetLibraryDocumentShortcut() throws Exception {
		DocumentShortcut randomDocumentShortcut = randomDocumentShortcut();

		DocumentShortcut postDocumentShortcut =
			testPostAssetLibraryDocumentShortcut_addDocumentShortcut(
				randomDocumentShortcut);

		assertEquals(randomDocumentShortcut, postDocumentShortcut);
		assertValid(postDocumentShortcut);
	}

	protected DocumentShortcut
			testPostAssetLibraryDocumentShortcut_addDocumentShortcut(
				DocumentShortcut documentShortcut)
		throws Exception {

		return documentShortcutResource.postAssetLibraryDocumentShortcut(
			testGetAssetLibraryDocumentShortcutsPage_getAssetLibraryId(),
			documentShortcut);
	}

	@Test
	public void testGraphQLPostAssetLibraryDocumentShortcut() throws Exception {
		DocumentShortcut randomDocumentShortcut = randomDocumentShortcut();

		DocumentShortcut documentShortcut =
			testGraphQLAssetLibraryDocumentShortcut_addDocumentShortcut(
				testDepotEntry.getDepotEntryId(), randomDocumentShortcut);

		Assert.assertTrue(equals(randomDocumentShortcut, documentShortcut));
	}

	@Test
	public void testPostSiteDocumentShortcut() throws Exception {
		DocumentShortcut randomDocumentShortcut = randomDocumentShortcut();

		DocumentShortcut postDocumentShortcut =
			testPostSiteDocumentShortcut_addDocumentShortcut(
				randomDocumentShortcut);

		assertEquals(randomDocumentShortcut, postDocumentShortcut);
		assertValid(postDocumentShortcut);
	}

	protected DocumentShortcut testPostSiteDocumentShortcut_addDocumentShortcut(
			DocumentShortcut documentShortcut)
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGetSiteDocumentShortcutsPage_getSiteId(), documentShortcut);
	}

	@Test
	public void testGraphQLPostSiteDocumentShortcut() throws Exception {
		DocumentShortcut randomDocumentShortcut = randomDocumentShortcut();

		DocumentShortcut documentShortcut =
			testGraphQLSiteDocumentShortcut_addDocumentShortcut(
				testGroup.getGroupId(), randomDocumentShortcut);

		Assert.assertTrue(equals(randomDocumentShortcut, documentShortcut));
	}

	@Test
	public void testPutDocumentShortcut() throws Exception {
		DocumentShortcut postDocumentShortcut =
			testPutDocumentShortcut_addDocumentShortcut();

		DocumentShortcut randomDocumentShortcut = randomDocumentShortcut();

		DocumentShortcut putDocumentShortcut =
			documentShortcutResource.putDocumentShortcut(
				postDocumentShortcut.getId(), randomDocumentShortcut);

		assertEquals(randomDocumentShortcut, putDocumentShortcut);
		assertValid(putDocumentShortcut);

		DocumentShortcut getDocumentShortcut =
			documentShortcutResource.getDocumentShortcut(
				putDocumentShortcut.getId());

		assertEquals(randomDocumentShortcut, getDocumentShortcut);
		assertValid(getDocumentShortcut);
	}

	protected DocumentShortcut testPutDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	@Test
	public void testPutSiteDocumentShortcutByExternalReferenceCode()
		throws Exception {

		DocumentShortcut postDocumentShortcut =
			testPutSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut();

		DocumentShortcut randomDocumentShortcut = randomDocumentShortcut();

		DocumentShortcut putDocumentShortcut =
			documentShortcutResource.
				putSiteDocumentShortcutByExternalReferenceCode(
					postDocumentShortcut.getSiteId(),
					postDocumentShortcut.getExternalReferenceCode(),
					randomDocumentShortcut);

		assertEquals(randomDocumentShortcut, putDocumentShortcut);
		assertValid(putDocumentShortcut);

		DocumentShortcut getDocumentShortcut =
			documentShortcutResource.
				getSiteDocumentShortcutByExternalReferenceCode(
					putDocumentShortcut.getSiteId(),
					putDocumentShortcut.getExternalReferenceCode());

		assertEquals(randomDocumentShortcut, getDocumentShortcut);
		assertValid(getDocumentShortcut);

		DocumentShortcut newDocumentShortcut =
			testPutSiteDocumentShortcutByExternalReferenceCode_createDocumentShortcut();

		putDocumentShortcut =
			documentShortcutResource.
				putSiteDocumentShortcutByExternalReferenceCode(
					newDocumentShortcut.getSiteId(),
					newDocumentShortcut.getExternalReferenceCode(),
					newDocumentShortcut);

		assertEquals(newDocumentShortcut, putDocumentShortcut);
		assertValid(putDocumentShortcut);

		getDocumentShortcut =
			documentShortcutResource.
				getSiteDocumentShortcutByExternalReferenceCode(
					putDocumentShortcut.getSiteId(),
					putDocumentShortcut.getExternalReferenceCode());

		assertEquals(newDocumentShortcut, getDocumentShortcut);

		Assert.assertEquals(
			newDocumentShortcut.getExternalReferenceCode(),
			putDocumentShortcut.getExternalReferenceCode());
	}

	protected DocumentShortcut
			testPutSiteDocumentShortcutByExternalReferenceCode_addDocumentShortcut()
		throws Exception {

		return documentShortcutResource.postSiteDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	protected DocumentShortcut
			testPutSiteDocumentShortcutByExternalReferenceCode_createDocumentShortcut()
		throws Exception {

		return randomDocumentShortcut();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		DocumentShortcut documentShortcut1 =
			testBatchEngineDeleteImportTask_addDocumentShortcut();

		testBatchEngineDeleteImportTask_deleteDocumentShortcut(
			200, null, documentShortcut1.getId());

		assertHttpResponseStatusCode(
			404,
			documentShortcutResource.getDocumentShortcutHttpResponse(
				documentShortcut1.getId()));
	}

	protected DocumentShortcut
			testBatchEngineDeleteImportTask_addDocumentShortcut()
		throws Exception {

		return testDeleteDocumentShortcut_addDocumentShortcut();
	}

	protected void testBatchEngineDeleteImportTask_deleteDocumentShortcut(
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
				"com.liferay.headless.delivery.dto.v1_0.DocumentShortcut", null,
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

	protected DocumentShortcut testGraphQLDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return testGraphQLDocumentShortcut_addDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	protected DocumentShortcut testGraphQLDocumentShortcut_addDocumentShortcut(
			Long siteId, DocumentShortcut documentShortcut)
		throws Exception {

		JSONDeserializer<DocumentShortcut> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentShortcut.class)) {

			if (getGraphQLValue(field.get(documentShortcut)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(documentShortcut)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentShortcut",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("documentShortcut", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocumentShortcut"),
			DocumentShortcut.class);
	}

	protected DocumentShortcut
			testGraphQLSiteDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return testGraphQLSiteDocumentShortcut_addDocumentShortcut(
			testGroup.getGroupId(), randomDocumentShortcut());
	}

	protected DocumentShortcut
			testGraphQLSiteDocumentShortcut_addDocumentShortcut(
				Long siteId, DocumentShortcut documentShortcut)
		throws Exception {

		JSONDeserializer<DocumentShortcut> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentShortcut.class)) {

			if (getGraphQLValue(field.get(documentShortcut)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(documentShortcut)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteDocumentShortcut",
						new HashMap<String, Object>() {
							{
								put("siteKey", "\"" + siteId + "\"");
								put("documentShortcut", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteDocumentShortcut"),
			DocumentShortcut.class);
	}

	protected DocumentShortcut
			testGraphQLAssetLibraryDocumentShortcut_addDocumentShortcut()
		throws Exception {

		return testGraphQLAssetLibraryDocumentShortcut_addDocumentShortcut(
			testDepotEntry.getDepotEntryId(), randomDocumentShortcut());
	}

	protected DocumentShortcut
			testGraphQLAssetLibraryDocumentShortcut_addDocumentShortcut(
				Long assetLibraryId, DocumentShortcut documentShortcut)
		throws Exception {

		JSONDeserializer<DocumentShortcut> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(DocumentShortcut.class)) {

			if (getGraphQLValue(field.get(documentShortcut)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(documentShortcut)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAssetLibraryDocumentShortcut",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" + assetLibraryId + "\"");
								put("documentShortcut", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data",
				"JSONObject/createAssetLibraryDocumentShortcut"),
			DocumentShortcut.class);
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
		DocumentShortcut documentShortcut,
		List<DocumentShortcut> documentShortcuts) {

		boolean contains = false;

		for (DocumentShortcut item : documentShortcuts) {
			if (equals(documentShortcut, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			documentShortcuts + " does not contain " + documentShortcut,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		DocumentShortcut documentShortcut1,
		DocumentShortcut documentShortcut2) {

		Assert.assertTrue(
			documentShortcut1 + " does not equal " + documentShortcut2,
			equals(documentShortcut1, documentShortcut2));
	}

	protected void assertEquals(
		List<DocumentShortcut> documentShortcuts1,
		List<DocumentShortcut> documentShortcuts2) {

		Assert.assertEquals(
			documentShortcuts1.size(), documentShortcuts2.size());

		for (int i = 0; i < documentShortcuts1.size(); i++) {
			DocumentShortcut documentShortcut1 = documentShortcuts1.get(i);
			DocumentShortcut documentShortcut2 = documentShortcuts2.get(i);

			assertEquals(documentShortcut1, documentShortcut2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<DocumentShortcut> documentShortcuts1,
		List<DocumentShortcut> documentShortcuts2) {

		Assert.assertEquals(
			documentShortcuts1.size(), documentShortcuts2.size());

		for (DocumentShortcut documentShortcut1 : documentShortcuts1) {
			boolean contains = false;

			for (DocumentShortcut documentShortcut2 : documentShortcuts2) {
				if (equals(documentShortcut1, documentShortcut2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				documentShortcuts2 + " does not contain " + documentShortcut1,
				contains);
		}
	}

	protected void assertValid(DocumentShortcut documentShortcut)
		throws Exception {

		boolean valid = true;

		if (documentShortcut.getDateCreated() == null) {
			valid = false;
		}

		if (documentShortcut.getDateModified() == null) {
			valid = false;
		}

		if (documentShortcut.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(
				documentShortcut.getAssetLibraryKey(),
				testDepotEntryGroup.getGroupKey()) &&
			!Objects.equals(
				documentShortcut.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (documentShortcut.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (documentShortcut.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (documentShortcut.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("folderId", additionalAssertFieldName)) {
				if (documentShortcut.getFolderId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("targetDocumentId", additionalAssertFieldName)) {
				if (documentShortcut.getTargetDocumentId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (documentShortcut.getTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (documentShortcut.getViewableBy() == null) {
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

	protected void assertValid(Page<DocumentShortcut> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<DocumentShortcut> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<DocumentShortcut> documentShortcuts =
			page.getItems();

		int size = documentShortcuts.size();

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
					com.liferay.headless.delivery.dto.v1_0.DocumentShortcut.
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
		DocumentShortcut documentShortcut1,
		DocumentShortcut documentShortcut2) {

		if (documentShortcut1 == documentShortcut2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)documentShortcut1.getActions(),
						(Map)documentShortcut2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getDateCreated(),
						documentShortcut2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getDateModified(),
						documentShortcut2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						documentShortcut1.getExternalReferenceCode(),
						documentShortcut2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("folderId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getFolderId(),
						documentShortcut2.getFolderId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getId(), documentShortcut2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("targetDocumentId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getTargetDocumentId(),
						documentShortcut2.getTargetDocumentId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("title", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getTitle(),
						documentShortcut2.getTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						documentShortcut1.getViewableBy(),
						documentShortcut2.getViewableBy())) {

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

		if (!(_documentShortcutResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_documentShortcutResource;

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
		DocumentShortcut documentShortcut) {

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
			Object object = documentShortcut.getAssetLibraryKey();

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
				Date date = documentShortcut.getDateCreated();

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

				sb.append(_format.format(documentShortcut.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = documentShortcut.getDateModified();

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

				sb.append(_format.format(documentShortcut.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = documentShortcut.getExternalReferenceCode();

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

		if (entityFieldName.equals("folderId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("targetDocumentId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("title")) {
			Object object = documentShortcut.getTitle();

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

	protected DocumentShortcut randomDocumentShortcut() throws Exception {
		return new DocumentShortcut() {
			{
				assetLibraryKey = String.valueOf(
					testDepotEntry.getDepotEntryId());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				folderId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
				targetDocumentId = RandomTestUtil.randomLong();
				title = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected DocumentShortcut randomIrrelevantDocumentShortcut()
		throws Exception {

		DocumentShortcut randomIrrelevantDocumentShortcut =
			randomDocumentShortcut();

		randomIrrelevantDocumentShortcut.setAssetLibraryKey(
			String.valueOf(irrelevantDepotEntry.getDepotEntryId()));

		randomIrrelevantDocumentShortcut.setSiteId(
			irrelevantGroup.getGroupId());

		return randomIrrelevantDocumentShortcut;
	}

	protected DocumentShortcut randomPatchDocumentShortcut() throws Exception {
		return randomDocumentShortcut();
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

	protected DocumentShortcutResource documentShortcutResource;
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
		LogFactoryUtil.getLog(BaseDocumentShortcutResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.DocumentShortcutResource
		_documentShortcutResource;

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