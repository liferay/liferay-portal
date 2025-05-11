/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.taxonomy.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.admin.taxonomy.client.dto.v1_0.Keyword;
import com.liferay.headless.admin.taxonomy.client.http.HttpInvoker;
import com.liferay.headless.admin.taxonomy.client.pagination.Page;
import com.liferay.headless.admin.taxonomy.client.pagination.Pagination;
import com.liferay.headless.admin.taxonomy.client.permission.Permission;
import com.liferay.headless.admin.taxonomy.client.resource.v1_0.KeywordResource;
import com.liferay.headless.admin.taxonomy.client.serdes.v1_0.KeywordSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.accept.language.AcceptLanguage;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegate;
import com.liferay.portal.vulcan.crud.VulcanCRUDItemDelegateBuilderRegistry;
import com.liferay.portal.vulcan.resource.EntityModelResource;

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

import javax.annotation.Generated;

import javax.servlet.http.HttpServletRequest;

import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

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
public abstract class BaseKeywordResourceTestCase {

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

		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null,
			new ServiceContext() {
				{
					setCompanyId(testGroup.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});

		_keywordResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		keywordResource = KeywordResource.builder(
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

		Keyword keyword1 = randomKeyword();

		String json = objectMapper.writeValueAsString(keyword1);

		Keyword keyword2 = KeywordSerDes.toDTO(json);

		Assert.assertTrue(equals(keyword1, keyword2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Keyword keyword = randomKeyword();

		String json1 = objectMapper.writeValueAsString(keyword);
		String json2 = KeywordSerDes.toJSON(keyword);

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

		Keyword keyword = randomKeyword();

		keyword.setAssetLibraryKey(regex);
		keyword.setExternalReferenceCode(regex);
		keyword.setName(regex);
		keyword.setSiteExternalReferenceCode(regex);

		String json = KeywordSerDes.toJSON(keyword);

		Assert.assertFalse(json.contains(regex));

		keyword = KeywordSerDes.toDTO(json);

		Assert.assertEquals(regex, keyword.getAssetLibraryKey());
		Assert.assertEquals(regex, keyword.getExternalReferenceCode());
		Assert.assertEquals(regex, keyword.getName());
		Assert.assertEquals(regex, keyword.getSiteExternalReferenceCode());
	}

	@Test
	public void testDeleteAssetLibraryKeywordByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword =
			testDeleteAssetLibraryKeywordByExternalReferenceCode_addKeyword();

		assertHttpResponseStatusCode(
			204,
			keywordResource.
				deleteAssetLibraryKeywordByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
					keyword.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			keywordResource.
				getAssetLibraryKeywordByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
					keyword.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			keywordResource.
				getAssetLibraryKeywordByExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
					"-"));
	}

	protected Long
			testDeleteAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Keyword
			testDeleteAssetLibraryKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return keywordResource.postAssetLibraryKeyword(
			testDepotEntry.getDepotEntryId(), randomKeyword());
	}

	@Test
	public void testDeleteKeyword() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword = testDeleteKeyword_addKeyword();

		assertHttpResponseStatusCode(
			204, keywordResource.deleteKeywordHttpResponse(keyword.getId()));

		assertHttpResponseStatusCode(
			404, keywordResource.getKeywordHttpResponse(keyword.getId()));
		assertHttpResponseStatusCode(
			404, keywordResource.getKeywordHttpResponse(0L));
	}

	protected Keyword testDeleteKeyword_addKeyword() throws Exception {
		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testGraphQLDeleteKeyword() throws Exception {

		// No namespace

		Keyword keyword1 = testGraphQLDeleteKeyword_addKeyword();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteKeyword",
						new HashMap<String, Object>() {
							{
								put("keywordId", keyword1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteKeyword"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"keyword",
					new HashMap<String, Object>() {
						{
							put("keywordId", keyword1.getId());
						}
					},
					new GraphQLField("id"))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminTaxonomy_v1_0

		Keyword keyword2 = testGraphQLDeleteKeyword_addKeyword();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"deleteKeyword",
							new HashMap<String, Object>() {
								{
									put("keywordId", keyword2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
				"Object/deleteKeyword"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminTaxonomy_v1_0",
					new GraphQLField(
						"keyword",
						new HashMap<String, Object>() {
							{
								put("keywordId", keyword2.getId());
							}
						},
						new GraphQLField("id")))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Keyword testGraphQLDeleteKeyword_addKeyword() throws Exception {
		return testGraphQLKeyword_addKeyword();
	}

	@Test
	public void testDeleteKeywordBatch() throws Exception {
		Keyword keyword1 = testDeleteKeywordBatch_addKeyword();

		testDeleteKeywordBatch_deleteKeyword(
			"COMPLETED", null, keyword1.getId());

		assertHttpResponseStatusCode(
			404, keywordResource.getKeywordHttpResponse(keyword1.getId()));
	}

	protected Keyword testDeleteKeywordBatch_addKeyword() throws Exception {
		return testDeleteKeyword_addKeyword();
	}

	protected void testDeleteKeywordBatch_deleteKeyword(
			String expectedExecuteStatus, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			keywordResource.deleteKeywordBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"id", () -> id
					)));

		Assert.assertEquals(202, httpResponse.getStatusCode());

		waitForFinish(
			expectedExecuteStatus,
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testDeleteSiteKeywordByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword =
			testDeleteSiteKeywordByExternalReferenceCode_addKeyword();

		assertHttpResponseStatusCode(
			204,
			keywordResource.
				deleteSiteKeywordByExternalReferenceCodeHttpResponse(
					testDeleteSiteKeywordByExternalReferenceCode_getSiteId(
						keyword),
					keyword.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			keywordResource.getSiteKeywordByExternalReferenceCodeHttpResponse(
				testDeleteSiteKeywordByExternalReferenceCode_getSiteId(keyword),
				keyword.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			keywordResource.getSiteKeywordByExternalReferenceCodeHttpResponse(
				testDeleteSiteKeywordByExternalReferenceCode_getSiteId(keyword),
				"-"));
	}

	protected Long testDeleteSiteKeywordByExternalReferenceCode_getSiteId(
			Keyword keyword)
		throws Exception {

		return keyword.getSiteId();
	}

	protected Keyword testDeleteSiteKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testGetAssetLibraryKeywordByExternalReferenceCode()
		throws Exception {

		Keyword postKeyword =
			testGetAssetLibraryKeywordByExternalReferenceCode_addKeyword();

		Keyword getKeyword =
			keywordResource.getAssetLibraryKeywordByExternalReferenceCode(
				testGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
				postKeyword.getExternalReferenceCode());

		assertEquals(postKeyword, getKeyword);
		assertValid(getKeyword);
	}

	protected Long
			testGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Keyword
			testGetAssetLibraryKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return keywordResource.postAssetLibraryKeyword(
			testDepotEntry.getDepotEntryId(), randomKeyword());
	}

	@Test
	public void testGraphQLGetAssetLibraryKeywordByExternalReferenceCode()
		throws Exception {

		Keyword keyword =
			testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_addKeyword();

		// No namespace

		Assert.assertTrue(
			equals(
				keyword,
				KeywordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"assetLibraryKeywordByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"assetLibraryId",
											"\"" +
												testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId() +
													"\"");

										put(
											"externalReferenceCode",
											"\"" +
												keyword.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/assetLibraryKeywordByExternalReferenceCode"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				keyword,
				KeywordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"assetLibraryKeywordByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"assetLibraryId",
												"\"" +
													testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId() +
														"\"");

											put(
												"externalReferenceCode",
												"\"" +
													keyword.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/assetLibraryKeywordByExternalReferenceCode"))));
	}

	protected Long
			testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAssetLibraryKeywordByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"assetLibraryKeywordByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"assetLibraryId",
									"\"" +
										testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId() +
											"\"");
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"assetLibraryKeywordByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"assetLibraryId",
										"\"" +
											testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId() +
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

	protected Keyword
			testGraphQLGetAssetLibraryKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return testGraphQLKeyword_addKeyword();
	}

	@Test
	public void testGetAssetLibraryKeywordPermissionsPage() throws Exception {
		Page<Permission> page =
			keywordResource.getAssetLibraryKeywordPermissionsPage(
				testDepotEntry.getDepotEntryId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected Keyword testGetAssetLibraryKeywordPermissionsPage_addKeyword()
		throws Exception {

		return testPostAssetLibraryKeyword_addKeyword(randomKeyword());
	}

	@Test
	public void testGetAssetLibraryKeywordsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryKeywordsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryKeywordsPage_getIrrelevantAssetLibraryId();

		Page<Keyword> page = keywordResource.getAssetLibraryKeywordsPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			Keyword irrelevantKeyword =
				testGetAssetLibraryKeywordsPage_addKeyword(
					irrelevantAssetLibraryId, randomIrrelevantKeyword());

			page = keywordResource.getAssetLibraryKeywordsPage(
				irrelevantAssetLibraryId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantKeyword, (List<Keyword>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryKeywordsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		Keyword keyword1 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		Keyword keyword2 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		page = keywordResource.getAssetLibraryKeywordsPage(
			assetLibraryId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(keyword1, (List<Keyword>)page.getItems());
		assertContains(keyword2, (List<Keyword>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryKeywordsPage_getExpectedActions(assetLibraryId));

		keywordResource.deleteKeyword(keyword1.getId());

		keywordResource.deleteKeyword(keyword2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryKeywordsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-taxonomy/v1.0/asset-libraries/{assetLibraryId}/keywords/batch".
				replace("{assetLibraryId}", String.valueOf(assetLibraryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryKeywordsPage_getAssetLibraryId();

		Keyword keyword1 = randomKeyword();

		keyword1 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, keyword1);

		for (EntityField entityField : entityFields) {
			Page<Keyword> page = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null,
				getFilterString(entityField, "between", keyword1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(keyword1),
				(List<Keyword>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithFilterStringContains()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithFilterStringEquals()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAssetLibraryKeywordsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryKeywordsPage_getAssetLibraryId();

		Keyword keyword1 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword2 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		for (EntityField entityField : entityFields) {
			Page<Keyword> page = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null,
				getFilterString(entityField, operator, keyword1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(keyword1),
				(List<Keyword>)page.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryKeywordsPage_getAssetLibraryId();

		Page<Keyword> keywordsPage =
			keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(keywordsPage.getTotalCount());

		Keyword keyword1 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		Keyword keyword2 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		Keyword keyword3 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, randomKeyword());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Keyword> page1 = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(keyword1, (List<Keyword>)page1.getItems());

			Page<Keyword> page2 = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(keyword2, (List<Keyword>)page2.getItems());

			Page<Keyword> page3 = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
		else {
			Page<Keyword> page1 = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, totalCount + 2), null);

			List<Keyword> keywords1 = (List<Keyword>)page1.getItems();

			Assert.assertEquals(
				keywords1.toString(), totalCount + 2, keywords1.size());

			Page<Keyword> page2 = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Keyword> keywords2 = (List<Keyword>)page2.getItems();

			Assert.assertEquals(keywords2.toString(), 1, keywords2.size());

			Page<Keyword> page3 = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(keyword1, (List<Keyword>)page3.getItems());
			assertContains(keyword2, (List<Keyword>)page3.getItems());
			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(
					keyword1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(keyword1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(keyword2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(keyword1, entityField.getName(), 0);
				BeanTestUtil.setProperty(keyword2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryKeywordsPageWithSortString()
		throws Exception {

		testGetAssetLibraryKeywordsPageWithSort(
			EntityField.Type.STRING,
			(entityField, keyword1, keyword2) -> {
				Class<?> clazz = keyword1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAssetLibraryKeywordsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Keyword, Keyword, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryKeywordsPage_getAssetLibraryId();

		Keyword keyword1 = randomKeyword();
		Keyword keyword2 = randomKeyword();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, keyword1, keyword2);
		}

		keyword1 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, keyword1);

		keyword2 = testGetAssetLibraryKeywordsPage_addKeyword(
			assetLibraryId, keyword2);

		Page<Keyword> page = keywordResource.getAssetLibraryKeywordsPage(
			assetLibraryId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Keyword> ascPage = keywordResource.getAssetLibraryKeywordsPage(
				assetLibraryId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(keyword1, (List<Keyword>)ascPage.getItems());
			assertContains(keyword2, (List<Keyword>)ascPage.getItems());

			Page<Keyword> descPage =
				keywordResource.getAssetLibraryKeywordsPage(
					assetLibraryId, null, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(keyword2, (List<Keyword>)descPage.getItems());
			assertContains(keyword1, (List<Keyword>)descPage.getItems());
		}
	}

	protected Keyword testGetAssetLibraryKeywordsPage_addKeyword(
			Long assetLibraryId, Keyword keyword)
		throws Exception {

		return keywordResource.postAssetLibraryKeyword(assetLibraryId, keyword);
	}

	protected Long testGetAssetLibraryKeywordsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long testGetAssetLibraryKeywordsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetKeyword() throws Exception {
		Keyword postKeyword = testGetKeyword_addKeyword();

		Keyword getKeyword = keywordResource.getKeyword(postKeyword.getId());

		assertEquals(postKeyword, getKeyword);
		assertValid(getKeyword);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Keyword postKeyword = testGetKeyword_addKeyword();

		Keyword getKeyword = keywordResource.getKeyword(postKeyword.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword"
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

		Object item = vulcanCRUDItemDelegate.getItem(postKeyword.getId());

		assertEquals(getKeyword, KeywordSerDes.toDTO(item.toString()));
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

	protected Keyword testGetKeyword_addKeyword() throws Exception {
		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testGraphQLGetKeyword() throws Exception {
		Keyword keyword = testGraphQLGetKeyword_addKeyword();

		// No namespace

		Assert.assertTrue(
			equals(
				keyword,
				KeywordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"keyword",
								new HashMap<String, Object>() {
									{
										put("keywordId", keyword.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/keyword"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				keyword,
				KeywordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"keyword",
									new HashMap<String, Object>() {
										{
											put("keywordId", keyword.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/keyword"))));
	}

	@Test
	public void testGraphQLGetKeywordNotFound() throws Exception {
		Long irrelevantKeywordId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"keyword",
						new HashMap<String, Object>() {
							{
								put("keywordId", irrelevantKeywordId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"keyword",
							new HashMap<String, Object>() {
								{
									put("keywordId", irrelevantKeywordId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Keyword testGraphQLGetKeyword_addKeyword() throws Exception {
		return testGraphQLKeyword_addKeyword();
	}

	@Test
	public void testGetKeywordsPage() throws Exception {
		Page<Keyword> page = keywordResource.getKeywordsPage(
			null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Keyword keyword1 = testGetKeywordsPage_addKeyword(randomKeyword());

		Keyword keyword2 = testGetKeywordsPage_addKeyword(randomKeyword());

		page = keywordResource.getKeywordsPage(
			null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(keyword1, (List<Keyword>)page.getItems());
		assertContains(keyword2, (List<Keyword>)page.getItems());
		assertValid(page, testGetKeywordsPage_getExpectedActions());

		keywordResource.deleteKeyword(keyword1.getId());

		keywordResource.deleteKeyword(keyword2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetKeywordsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetKeywordsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Keyword keyword1 = randomKeyword();

		keyword1 = testGetKeywordsPage_addKeyword(keyword1);

		for (EntityField entityField : entityFields) {
			Page<Keyword> page = keywordResource.getKeywordsPage(
				null, null, getFilterString(entityField, "between", keyword1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(keyword1),
				(List<Keyword>)page.getItems());
		}
	}

	@Test
	public void testGetKeywordsPageWithFilterDoubleEquals() throws Exception {
		testGetKeywordsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetKeywordsPageWithFilterStringContains() throws Exception {
		testGetKeywordsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetKeywordsPageWithFilterStringEquals() throws Exception {
		testGetKeywordsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetKeywordsPageWithFilterStringStartsWith()
		throws Exception {

		testGetKeywordsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetKeywordsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Keyword keyword1 = testGetKeywordsPage_addKeyword(randomKeyword());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword2 = testGetKeywordsPage_addKeyword(randomKeyword());

		for (EntityField entityField : entityFields) {
			Page<Keyword> page = keywordResource.getKeywordsPage(
				null, null, getFilterString(entityField, operator, keyword1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(keyword1),
				(List<Keyword>)page.getItems());
		}
	}

	@Test
	public void testGetKeywordsPageWithPagination() throws Exception {
		Page<Keyword> keywordsPage = keywordResource.getKeywordsPage(
			null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(keywordsPage.getTotalCount());

		Keyword keyword1 = testGetKeywordsPage_addKeyword(randomKeyword());

		Keyword keyword2 = testGetKeywordsPage_addKeyword(randomKeyword());

		Keyword keyword3 = testGetKeywordsPage_addKeyword(randomKeyword());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Keyword> page1 = keywordResource.getKeywordsPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(keyword1, (List<Keyword>)page1.getItems());

			Page<Keyword> page2 = keywordResource.getKeywordsPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(keyword2, (List<Keyword>)page2.getItems());

			Page<Keyword> page3 = keywordResource.getKeywordsPage(
				null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
		else {
			Page<Keyword> page1 = keywordResource.getKeywordsPage(
				null, null, null, Pagination.of(1, totalCount + 2), null);

			List<Keyword> keywords1 = (List<Keyword>)page1.getItems();

			Assert.assertEquals(
				keywords1.toString(), totalCount + 2, keywords1.size());

			Page<Keyword> page2 = keywordResource.getKeywordsPage(
				null, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Keyword> keywords2 = (List<Keyword>)page2.getItems();

			Assert.assertEquals(keywords2.toString(), 1, keywords2.size());

			Page<Keyword> page3 = keywordResource.getKeywordsPage(
				null, null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(keyword1, (List<Keyword>)page3.getItems());
			assertContains(keyword2, (List<Keyword>)page3.getItems());
			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
	}

	@Test
	public void testGetKeywordsPageWithSortDateTime() throws Exception {
		testGetKeywordsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(
					keyword1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetKeywordsPageWithSortDouble() throws Exception {
		testGetKeywordsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(keyword1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(keyword2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetKeywordsPageWithSortInteger() throws Exception {
		testGetKeywordsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(keyword1, entityField.getName(), 0);
				BeanTestUtil.setProperty(keyword2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetKeywordsPageWithSortString() throws Exception {
		testGetKeywordsPageWithSort(
			EntityField.Type.STRING,
			(entityField, keyword1, keyword2) -> {
				Class<?> clazz = keyword1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetKeywordsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Keyword, Keyword, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Keyword keyword1 = randomKeyword();
		Keyword keyword2 = randomKeyword();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, keyword1, keyword2);
		}

		keyword1 = testGetKeywordsPage_addKeyword(keyword1);

		keyword2 = testGetKeywordsPage_addKeyword(keyword2);

		Page<Keyword> page = keywordResource.getKeywordsPage(
			null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Keyword> ascPage = keywordResource.getKeywordsPage(
				null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(keyword1, (List<Keyword>)ascPage.getItems());
			assertContains(keyword2, (List<Keyword>)ascPage.getItems());

			Page<Keyword> descPage = keywordResource.getKeywordsPage(
				null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(keyword2, (List<Keyword>)descPage.getItems());
			assertContains(keyword1, (List<Keyword>)descPage.getItems());
		}
	}

	protected Keyword testGetKeywordsPage_addKeyword(Keyword keyword)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetKeywordsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"keywords",
			new HashMap<String, Object>() {
				{
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject keywordsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/keywords");

		long totalCount = keywordsJSONObject.getLong("totalCount");

		Keyword keyword1 = testGraphQLGetKeywordsPage_addKeyword();
		Keyword keyword2 = testGraphQLGetKeywordsPage_addKeyword();

		keywordsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/keywords");

		Assert.assertEquals(
			totalCount + 2, keywordsJSONObject.getLong("totalCount"));

		assertContains(
			keyword1,
			Arrays.asList(
				KeywordSerDes.toDTOs(keywordsJSONObject.getString("items"))));
		assertContains(
			keyword2,
			Arrays.asList(
				KeywordSerDes.toDTOs(keywordsJSONObject.getString("items"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		keywordsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminTaxonomy_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminTaxonomy_v1_0",
			"JSONObject/keywords");

		Assert.assertEquals(
			totalCount + 2, keywordsJSONObject.getLong("totalCount"));

		assertContains(
			keyword1,
			Arrays.asList(
				KeywordSerDes.toDTOs(keywordsJSONObject.getString("items"))));
		assertContains(
			keyword2,
			Arrays.asList(
				KeywordSerDes.toDTOs(keywordsJSONObject.getString("items"))));
	}

	protected Keyword testGraphQLGetKeywordsPage_addKeyword() throws Exception {
		return testGraphQLKeyword_addKeyword();
	}

	@Test
	public void testGetKeywordsRankedPage() throws Exception {
		Page<Keyword> page = keywordResource.getKeywordsRankedPage(
			null, null, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		Keyword keyword1 = testGetKeywordsRankedPage_addKeyword(
			randomKeyword());

		Keyword keyword2 = testGetKeywordsRankedPage_addKeyword(
			randomKeyword());

		page = keywordResource.getKeywordsRankedPage(
			null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(keyword1, (List<Keyword>)page.getItems());
		assertContains(keyword2, (List<Keyword>)page.getItems());
		assertValid(page, testGetKeywordsRankedPage_getExpectedActions());

		keywordResource.deleteKeyword(keyword1.getId());

		keywordResource.deleteKeyword(keyword2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetKeywordsRankedPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetKeywordsRankedPageWithPagination() throws Exception {
		Page<Keyword> keywordsPage = keywordResource.getKeywordsRankedPage(
			null, null, null);

		int totalCount = GetterUtil.getInteger(keywordsPage.getTotalCount());

		Keyword keyword1 = testGetKeywordsRankedPage_addKeyword(
			randomKeyword());

		Keyword keyword2 = testGetKeywordsRankedPage_addKeyword(
			randomKeyword());

		Keyword keyword3 = testGetKeywordsRankedPage_addKeyword(
			randomKeyword());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Keyword> page1 = keywordResource.getKeywordsRankedPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(keyword1, (List<Keyword>)page1.getItems());

			Page<Keyword> page2 = keywordResource.getKeywordsRankedPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(keyword2, (List<Keyword>)page2.getItems());

			Page<Keyword> page3 = keywordResource.getKeywordsRankedPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
		else {
			Page<Keyword> page1 = keywordResource.getKeywordsRankedPage(
				null, null, Pagination.of(1, totalCount + 2));

			List<Keyword> keywords1 = (List<Keyword>)page1.getItems();

			Assert.assertEquals(
				keywords1.toString(), totalCount + 2, keywords1.size());

			Page<Keyword> page2 = keywordResource.getKeywordsRankedPage(
				null, null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Keyword> keywords2 = (List<Keyword>)page2.getItems();

			Assert.assertEquals(keywords2.toString(), 1, keywords2.size());

			Page<Keyword> page3 = keywordResource.getKeywordsRankedPage(
				null, null, Pagination.of(1, (int)totalCount + 3));

			assertContains(keyword1, (List<Keyword>)page3.getItems());
			assertContains(keyword2, (List<Keyword>)page3.getItems());
			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
	}

	protected Keyword testGetKeywordsRankedPage_addKeyword(Keyword keyword)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetSiteKeywordByExternalReferenceCode() throws Exception {
		Keyword postKeyword =
			testGetSiteKeywordByExternalReferenceCode_addKeyword();

		Keyword getKeyword =
			keywordResource.getSiteKeywordByExternalReferenceCode(
				testGetSiteKeywordByExternalReferenceCode_getSiteId(
					postKeyword),
				postKeyword.getExternalReferenceCode());

		assertEquals(postKeyword, getKeyword);
		assertValid(getKeyword);
	}

	protected Long testGetSiteKeywordByExternalReferenceCode_getSiteId(
			Keyword keyword)
		throws Exception {

		return keyword.getSiteId();
	}

	protected Keyword testGetSiteKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testGraphQLGetSiteKeywordByExternalReferenceCode()
		throws Exception {

		Keyword keyword =
			testGraphQLGetSiteKeywordByExternalReferenceCode_addKeyword();

		// No namespace

		Assert.assertTrue(
			equals(
				keyword,
				KeywordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"keywordByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" +
												testGraphQLGetSiteKeywordByExternalReferenceCode_getSiteId(
													keyword) + "\"");

										put(
											"externalReferenceCode",
											"\"" +
												keyword.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/keywordByExternalReferenceCode"))));

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertTrue(
			equals(
				keyword,
				KeywordSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminTaxonomy_v1_0",
								new GraphQLField(
									"keywordByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" +
													testGraphQLGetSiteKeywordByExternalReferenceCode_getSiteId(
														keyword) + "\"");

											put(
												"externalReferenceCode",
												"\"" +
													keyword.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessAdminTaxonomy_v1_0",
						"Object/keywordByExternalReferenceCode"))));
	}

	protected Long testGraphQLGetSiteKeywordByExternalReferenceCode_getSiteId(
			Keyword keyword)
		throws Exception {

		return keyword.getSiteId();
	}

	@Test
	public void testGraphQLGetSiteKeywordByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"keywordByExternalReferenceCode",
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

		// Using the namespace headlessAdminTaxonomy_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminTaxonomy_v1_0",
						new GraphQLField(
							"keywordByExternalReferenceCode",
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

	protected Keyword
			testGraphQLGetSiteKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return testGraphQLKeyword_addKeyword();
	}

	@Test
	public void testGetSiteKeywordPermissionsPage() throws Exception {
		Page<Permission> page = keywordResource.getSiteKeywordPermissionsPage(
			testGroup.getGroupId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected Keyword testGetSiteKeywordPermissionsPage_addKeyword()
		throws Exception {

		return testPostSiteKeyword_addKeyword(randomKeyword());
	}

	@Test
	public void testGetSiteKeywordsPage() throws Exception {
		Long siteId = testGetSiteKeywordsPage_getSiteId();
		Long irrelevantSiteId = testGetSiteKeywordsPage_getIrrelevantSiteId();

		Page<Keyword> page = keywordResource.getSiteKeywordsPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			Keyword irrelevantKeyword = testGetSiteKeywordsPage_addKeyword(
				irrelevantSiteId, randomIrrelevantKeyword());

			page = keywordResource.getSiteKeywordsPage(
				irrelevantSiteId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantKeyword, (List<Keyword>)page.getItems());
			assertValid(
				page,
				testGetSiteKeywordsPage_getExpectedActions(irrelevantSiteId));
		}

		Keyword keyword1 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		Keyword keyword2 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		page = keywordResource.getSiteKeywordsPage(
			siteId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(keyword1, (List<Keyword>)page.getItems());
		assertContains(keyword2, (List<Keyword>)page.getItems());
		assertValid(page, testGetSiteKeywordsPage_getExpectedActions(siteId));

		keywordResource.deleteKeyword(keyword1.getId());

		keywordResource.deleteKeyword(keyword2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteKeywordsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-taxonomy/v1.0/sites/{siteId}/keywords/batch".
				replace("{siteId}", String.valueOf(siteId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetSiteKeywordsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteKeywordsPage_getSiteId();

		Keyword keyword1 = randomKeyword();

		keyword1 = testGetSiteKeywordsPage_addKeyword(siteId, keyword1);

		for (EntityField entityField : entityFields) {
			Page<Keyword> page = keywordResource.getSiteKeywordsPage(
				siteId, null, null,
				getFilterString(entityField, "between", keyword1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(keyword1),
				(List<Keyword>)page.getItems());
		}
	}

	@Test
	public void testGetSiteKeywordsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteKeywordsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteKeywordsPageWithFilterStringContains()
		throws Exception {

		testGetSiteKeywordsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteKeywordsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteKeywordsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteKeywordsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteKeywordsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteKeywordsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteKeywordsPage_getSiteId();

		Keyword keyword1 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword2 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		for (EntityField entityField : entityFields) {
			Page<Keyword> page = keywordResource.getSiteKeywordsPage(
				siteId, null, null,
				getFilterString(entityField, operator, keyword1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(keyword1),
				(List<Keyword>)page.getItems());
		}
	}

	@Test
	public void testGetSiteKeywordsPageWithPagination() throws Exception {
		Long siteId = testGetSiteKeywordsPage_getSiteId();

		Page<Keyword> keywordsPage = keywordResource.getSiteKeywordsPage(
			siteId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(keywordsPage.getTotalCount());

		Keyword keyword1 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		Keyword keyword2 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		Keyword keyword3 = testGetSiteKeywordsPage_addKeyword(
			siteId, randomKeyword());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Keyword> page1 = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(keyword1, (List<Keyword>)page1.getItems());

			Page<Keyword> page2 = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(keyword2, (List<Keyword>)page2.getItems());

			Page<Keyword> page3 = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
		else {
			Page<Keyword> page1 = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<Keyword> keywords1 = (List<Keyword>)page1.getItems();

			Assert.assertEquals(
				keywords1.toString(), totalCount + 2, keywords1.size());

			Page<Keyword> page2 = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Keyword> keywords2 = (List<Keyword>)page2.getItems();

			Assert.assertEquals(keywords2.toString(), 1, keywords2.size());

			Page<Keyword> page3 = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null, Pagination.of(1, (int)totalCount + 3),
				null);

			assertContains(keyword1, (List<Keyword>)page3.getItems());
			assertContains(keyword2, (List<Keyword>)page3.getItems());
			assertContains(keyword3, (List<Keyword>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteKeywordsPageWithSortDateTime() throws Exception {
		testGetSiteKeywordsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(
					keyword1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteKeywordsPageWithSortDouble() throws Exception {
		testGetSiteKeywordsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(keyword1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(keyword2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteKeywordsPageWithSortInteger() throws Exception {
		testGetSiteKeywordsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, keyword1, keyword2) -> {
				BeanTestUtil.setProperty(keyword1, entityField.getName(), 0);
				BeanTestUtil.setProperty(keyword2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteKeywordsPageWithSortString() throws Exception {
		testGetSiteKeywordsPageWithSort(
			EntityField.Type.STRING,
			(entityField, keyword1, keyword2) -> {
				Class<?> clazz = keyword1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						keyword1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						keyword2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteKeywordsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Keyword, Keyword, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteKeywordsPage_getSiteId();

		Keyword keyword1 = randomKeyword();
		Keyword keyword2 = randomKeyword();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, keyword1, keyword2);
		}

		keyword1 = testGetSiteKeywordsPage_addKeyword(siteId, keyword1);

		keyword2 = testGetSiteKeywordsPage_addKeyword(siteId, keyword2);

		Page<Keyword> page = keywordResource.getSiteKeywordsPage(
			siteId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Keyword> ascPage = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(keyword1, (List<Keyword>)ascPage.getItems());
			assertContains(keyword2, (List<Keyword>)ascPage.getItems());

			Page<Keyword> descPage = keywordResource.getSiteKeywordsPage(
				siteId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(keyword2, (List<Keyword>)descPage.getItems());
			assertContains(keyword1, (List<Keyword>)descPage.getItems());
		}
	}

	protected Keyword testGetSiteKeywordsPage_addKeyword(
			Long siteId, Keyword keyword)
		throws Exception {

		return keywordResource.postSiteKeyword(siteId, keyword);
	}

	protected Long testGetSiteKeywordsPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteKeywordsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testPostAssetLibraryKeyword() throws Exception {
		Keyword randomKeyword = randomKeyword();

		Keyword postKeyword = testPostAssetLibraryKeyword_addKeyword(
			randomKeyword);

		assertEquals(randomKeyword, postKeyword);
		assertValid(postKeyword);
	}

	protected Keyword testPostAssetLibraryKeyword_addKeyword(Keyword keyword)
		throws Exception {

		return keywordResource.postAssetLibraryKeyword(
			testGetAssetLibraryKeywordsPage_getAssetLibraryId(), keyword);
	}

	@Test
	public void testPostKeyword() throws Exception {
		Keyword randomKeyword = randomKeyword();

		Keyword postKeyword = testPostKeyword_addKeyword(randomKeyword);

		assertEquals(randomKeyword, postKeyword);
		assertValid(postKeyword);
	}

	protected Keyword testPostKeyword_addKeyword(Keyword keyword)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostSiteKeyword() throws Exception {
		Keyword randomKeyword = randomKeyword();

		Keyword postKeyword = testPostSiteKeyword_addKeyword(randomKeyword);

		assertEquals(randomKeyword, postKeyword);
		assertValid(postKeyword);
	}

	protected Keyword testPostSiteKeyword_addKeyword(Keyword keyword)
		throws Exception {

		return keywordResource.postSiteKeyword(
			testGetSiteKeywordsPage_getSiteId(), keyword);
	}

	@Test
	public void testGraphQLPostSiteKeyword() throws Exception {
		Keyword randomKeyword = randomKeyword();

		Keyword keyword = testGraphQLKeyword_addKeyword(randomKeyword);

		Assert.assertTrue(equals(randomKeyword, keyword));
	}

	@Test
	public void testPutAssetLibraryKeywordByExternalReferenceCode()
		throws Exception {

		Keyword postKeyword =
			testPutAssetLibraryKeywordByExternalReferenceCode_addKeyword();

		Keyword randomKeyword = randomKeyword();

		Keyword putKeyword =
			keywordResource.putAssetLibraryKeywordByExternalReferenceCode(
				testPutAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
				postKeyword.getExternalReferenceCode(), randomKeyword);

		assertEquals(randomKeyword, putKeyword);
		assertValid(putKeyword);

		Keyword getKeyword =
			keywordResource.getAssetLibraryKeywordByExternalReferenceCode(
				testPutAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
				putKeyword.getExternalReferenceCode());

		assertEquals(randomKeyword, getKeyword);
		assertValid(getKeyword);

		Keyword newKeyword =
			testPutAssetLibraryKeywordByExternalReferenceCode_createKeyword();

		putKeyword =
			keywordResource.putAssetLibraryKeywordByExternalReferenceCode(
				testPutAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
				newKeyword.getExternalReferenceCode(), newKeyword);

		assertEquals(newKeyword, putKeyword);
		assertValid(putKeyword);

		getKeyword =
			keywordResource.getAssetLibraryKeywordByExternalReferenceCode(
				testPutAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId(),
				putKeyword.getExternalReferenceCode());

		assertEquals(newKeyword, getKeyword);

		Assert.assertEquals(
			newKeyword.getExternalReferenceCode(),
			putKeyword.getExternalReferenceCode());
	}

	protected Long
			testPutAssetLibraryKeywordByExternalReferenceCode_getAssetLibraryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Keyword
			testPutAssetLibraryKeywordByExternalReferenceCode_createKeyword()
		throws Exception {

		return randomKeyword();
	}

	protected Keyword
			testPutAssetLibraryKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return keywordResource.postAssetLibraryKeyword(
			testDepotEntry.getDepotEntryId(), randomKeyword());
	}

	@Test
	public void testPutAssetLibraryKeywordPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword =
			testPutAssetLibraryKeywordPermissionsPage_addKeyword();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			keywordResource.putAssetLibraryKeywordPermissionsPageHttpResponse(
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
			keywordResource.putAssetLibraryKeywordPermissionsPageHttpResponse(
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

	protected Keyword testPutAssetLibraryKeywordPermissionsPage_addKeyword()
		throws Exception {

		return keywordResource.postAssetLibraryKeyword(
			testDepotEntry.getDepotEntryId(), randomKeyword());
	}

	@Test
	public void testPutKeyword() throws Exception {
		Keyword postKeyword = testPutKeyword_addKeyword();

		Keyword randomKeyword = randomKeyword();

		Keyword putKeyword = keywordResource.putKeyword(
			postKeyword.getId(), randomKeyword);

		assertEquals(randomKeyword, putKeyword);
		assertValid(putKeyword);

		Keyword getKeyword = keywordResource.getKeyword(putKeyword.getId());

		assertEquals(randomKeyword, getKeyword);
		assertValid(getKeyword);
	}

	protected Keyword testPutKeyword_addKeyword() throws Exception {
		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testPutKeywordMerge() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword = testPutKeywordMerge_addKeyword();

		assertHttpResponseStatusCode(
			204, keywordResource.putKeywordMergeHttpResponse(null, null));

		assertHttpResponseStatusCode(
			404, keywordResource.putKeywordMergeHttpResponse(null, null));
	}

	protected Keyword testPutKeywordMerge_addKeyword() throws Exception {
		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testPutKeywordSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword = testPutKeywordSubscribe_addKeyword();

		assertHttpResponseStatusCode(
			204,
			keywordResource.putKeywordSubscribeHttpResponse(keyword.getId()));

		assertHttpResponseStatusCode(
			404, keywordResource.putKeywordSubscribeHttpResponse(0L));
	}

	protected Keyword testPutKeywordSubscribe_addKeyword() throws Exception {
		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testPutKeywordUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword = testPutKeywordUnsubscribe_addKeyword();

		assertHttpResponseStatusCode(
			204,
			keywordResource.putKeywordUnsubscribeHttpResponse(keyword.getId()));

		assertHttpResponseStatusCode(
			404, keywordResource.putKeywordUnsubscribeHttpResponse(0L));
	}

	protected Keyword testPutKeywordUnsubscribe_addKeyword() throws Exception {
		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testPutSiteKeywordByExternalReferenceCode() throws Exception {
		Keyword postKeyword =
			testPutSiteKeywordByExternalReferenceCode_addKeyword();

		Keyword randomKeyword = randomKeyword();

		Keyword putKeyword =
			keywordResource.putSiteKeywordByExternalReferenceCode(
				testPutSiteKeywordByExternalReferenceCode_getSiteId(
					postKeyword),
				postKeyword.getExternalReferenceCode(), randomKeyword);

		assertEquals(randomKeyword, putKeyword);
		assertValid(putKeyword);

		Keyword getKeyword =
			keywordResource.getSiteKeywordByExternalReferenceCode(
				testPutSiteKeywordByExternalReferenceCode_getSiteId(putKeyword),
				putKeyword.getExternalReferenceCode());

		assertEquals(randomKeyword, getKeyword);
		assertValid(getKeyword);

		Keyword newKeyword =
			testPutSiteKeywordByExternalReferenceCode_createKeyword();

		putKeyword = keywordResource.putSiteKeywordByExternalReferenceCode(
			testPutSiteKeywordByExternalReferenceCode_getSiteId(newKeyword),
			newKeyword.getExternalReferenceCode(), newKeyword);

		assertEquals(newKeyword, putKeyword);
		assertValid(putKeyword);

		getKeyword = keywordResource.getSiteKeywordByExternalReferenceCode(
			testPutSiteKeywordByExternalReferenceCode_getSiteId(putKeyword),
			putKeyword.getExternalReferenceCode());

		assertEquals(newKeyword, getKeyword);

		Assert.assertEquals(
			newKeyword.getExternalReferenceCode(),
			putKeyword.getExternalReferenceCode());
	}

	protected Long testPutSiteKeywordByExternalReferenceCode_getSiteId(
			Keyword keyword)
		throws Exception {

		return keyword.getSiteId();
	}

	protected Keyword testPutSiteKeywordByExternalReferenceCode_createKeyword()
		throws Exception {

		return randomKeyword();
	}

	protected Keyword testPutSiteKeywordByExternalReferenceCode_addKeyword()
		throws Exception {

		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Test
	public void testPutSiteKeywordPermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Keyword keyword = testPutSiteKeywordPermissionsPage_addKeyword();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			keywordResource.putSiteKeywordPermissionsPageHttpResponse(
				keyword.getSiteId(),
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
			keywordResource.putSiteKeywordPermissionsPageHttpResponse(
				keyword.getSiteId(),
				new Permission[] {
					new Permission() {
						{
							setActionIds(new String[] {"-"});
							setRoleName("-");
						}
					}
				}));
	}

	protected Keyword testPutSiteKeywordPermissionsPage_addKeyword()
		throws Exception {

		return keywordResource.postSiteKeyword(
			testGroup.getGroupId(), randomKeyword());
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void appendGraphQLFieldValue(StringBuilder sb, Object value)
		throws Exception {

		if (value instanceof Object[]) {
			StringBuilder arraySB = new StringBuilder("[");

			for (Object object : (Object[])value) {
				if (arraySB.length() > 1) {
					arraySB.append(", ");
				}

				arraySB.append("{");

				Class<?> clazz = object.getClass();

				for (java.lang.reflect.Field field :
						getDeclaredFields(clazz.getSuperclass())) {

					arraySB.append(field.getName());
					arraySB.append(": ");

					appendGraphQLFieldValue(arraySB, field.get(object));

					arraySB.append(", ");
				}

				arraySB.setLength(arraySB.length() - 2);

				arraySB.append("}");
			}

			arraySB.append("]");

			sb.append(arraySB.toString());
		}
		else if (value instanceof String) {
			sb.append("\"");
			sb.append(value);
			sb.append("\"");
		}
		else {
			sb.append(value);
		}
	}

	protected Keyword testGraphQLKeyword_addKeyword() throws Exception {
		return testGraphQLKeyword_addKeyword(randomKeyword());
	}

	protected Keyword testGraphQLKeyword_addKeyword(Keyword keyword)
		throws Exception {

		JSONDeserializer<Keyword> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Keyword.class)) {
			if (!ArrayUtil.contains(
					getAdditionalAssertFieldNames(), field.getName())) {

				continue;
			}

			if (sb.length() > 1) {
				sb.append(", ");
			}

			sb.append(field.getName());
			sb.append(": ");

			appendGraphQLFieldValue(sb, field.get(keyword));
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		graphQLFields.add(new GraphQLField("externalReferenceCode"));

		graphQLFields.add(new GraphQLField("id"));

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createSiteKeyword",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + testGroup.getGroupId() + "\"");
								put("keyword", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createSiteKeyword"),
			Keyword.class);
	}

	protected void assertContains(Keyword keyword, List<Keyword> keywords) {
		boolean contains = false;

		for (Keyword item : keywords) {
			if (equals(keyword, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(keywords + " does not contain " + keyword, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Keyword keyword1, Keyword keyword2) {
		Assert.assertTrue(
			keyword1 + " does not equal " + keyword2,
			equals(keyword1, keyword2));
	}

	protected void assertEquals(
		List<Keyword> keywords1, List<Keyword> keywords2) {

		Assert.assertEquals(keywords1.size(), keywords2.size());

		for (int i = 0; i < keywords1.size(); i++) {
			Keyword keyword1 = keywords1.get(i);
			Keyword keyword2 = keywords2.get(i);

			assertEquals(keyword1, keyword2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Keyword> keywords1, List<Keyword> keywords2) {

		Assert.assertEquals(keywords1.size(), keywords2.size());

		for (Keyword keyword1 : keywords1) {
			boolean contains = false;

			for (Keyword keyword2 : keywords2) {
				if (equals(keyword1, keyword2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				keywords2 + " does not contain " + keyword1, contains);
		}
	}

	protected void assertValid(Keyword keyword) throws Exception {
		boolean valid = true;

		if (keyword.getDateCreated() == null) {
			valid = false;
		}

		if (keyword.getDateModified() == null) {
			valid = false;
		}

		if (keyword.getId() == null) {
			valid = false;
		}

		com.liferay.portal.kernel.model.Group group = testDepotEntry.getGroup();

		if (!Objects.equals(
				keyword.getAssetLibraryKey(), group.getGroupKey()) &&
			!Objects.equals(keyword.getSiteId(), testGroup.getGroupId())) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (keyword.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraries", additionalAssertFieldName)) {
				if (keyword.getAssetLibraries() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("assetLibraryKey", additionalAssertFieldName)) {
				if (keyword.getAssetLibraryKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (keyword.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (keyword.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"keywordUsageCount", additionalAssertFieldName)) {

				if (keyword.getKeywordUsageCount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (keyword.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (keyword.getSiteExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (keyword.getSubscribed() == null) {
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

	protected void assertValid(Page<Keyword> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Keyword> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Keyword> keywords = page.getItems();

		int size = keywords.size();

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

		graphQLFields.add(new GraphQLField("siteId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.taxonomy.dto.v1_0.Keyword.
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

	protected boolean equals(Keyword keyword1, Keyword keyword2) {
		if (keyword1 == keyword2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)keyword1.getActions(),
						(Map)keyword2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("assetLibraries", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						keyword1.getAssetLibraries(),
						keyword2.getAssetLibraries())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						keyword1.getCreator(), keyword2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						keyword1.getDateCreated(), keyword2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						keyword1.getDateModified(),
						keyword2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						keyword1.getExternalReferenceCode(),
						keyword2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(keyword1.getId(), keyword2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals(
					"keywordUsageCount", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						keyword1.getKeywordUsageCount(),
						keyword2.getKeywordUsageCount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						keyword1.getName(), keyword2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"siteExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						keyword1.getSiteExternalReferenceCode(),
						keyword2.getSiteExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						keyword1.getSubscribed(), keyword2.getSubscribed())) {

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

		if (!(_keywordResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_keywordResource;

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
		EntityField entityField, String operator, Keyword keyword) {

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

		if (entityFieldName.equals("assetLibraries")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("assetLibraryKey")) {
			Object object = keyword.getAssetLibraryKey();

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

		if (entityFieldName.equals("dateCreated")) {
			if (operator.equals("between")) {
				Date date = keyword.getDateCreated();

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

				sb.append(_format.format(keyword.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = keyword.getDateModified();

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

				sb.append(_format.format(keyword.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = keyword.getExternalReferenceCode();

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

		if (entityFieldName.equals("keywordUsageCount")) {
			sb.append(String.valueOf(keyword.getKeywordUsageCount()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = keyword.getName();

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

		if (entityFieldName.equals("siteExternalReferenceCode")) {
			Object object = keyword.getSiteExternalReferenceCode();

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

		if (entityFieldName.equals("subscribed")) {
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

	protected Keyword randomKeyword() throws Exception {
		return new Keyword() {
			{
				assetLibraryKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				keywordUsageCount = RandomTestUtil.randomInt();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				siteExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Keyword randomIrrelevantKeyword() throws Exception {
		Keyword randomIrrelevantKeyword = randomKeyword();

		randomIrrelevantKeyword.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantKeyword;
	}

	protected Keyword randomPatchKeyword() throws Exception {
		return randomKeyword();
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

	protected KeywordResource keywordResource;
	protected ImportTaskResource importTaskResource;
	protected com.liferay.portal.kernel.model.Group irrelevantGroup;
	protected com.liferay.portal.kernel.model.Company testCompany;
	protected DepotEntry testDepotEntry;
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
		LogFactoryUtil.getLog(BaseKeywordResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.taxonomy.resource.v1_0.KeywordResource
		_keywordResource;

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