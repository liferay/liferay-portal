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

import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.headless.delivery.client.dto.v1_0.Field;
import com.liferay.headless.delivery.client.dto.v1_0.WikiPage;
import com.liferay.headless.delivery.client.http.HttpInvoker;
import com.liferay.headless.delivery.client.pagination.Page;
import com.liferay.headless.delivery.client.pagination.Pagination;
import com.liferay.headless.delivery.client.permission.Permission;
import com.liferay.headless.delivery.client.resource.v1_0.WikiPageResource;
import com.liferay.headless.delivery.client.serdes.v1_0.WikiPageSerDes;
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
public abstract class BaseWikiPageResourceTestCase {

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

		_wikiPageResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		wikiPageResource = WikiPageResource.builder(
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

		WikiPage wikiPage1 = randomWikiPage();

		String json = objectMapper.writeValueAsString(wikiPage1);

		WikiPage wikiPage2 = WikiPageSerDes.toDTO(json);

		Assert.assertTrue(equals(wikiPage1, wikiPage2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WikiPage wikiPage = randomWikiPage();

		String json1 = objectMapper.writeValueAsString(wikiPage);
		String json2 = WikiPageSerDes.toJSON(wikiPage);

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

		WikiPage wikiPage = randomWikiPage();

		wikiPage.setContent(regex);
		wikiPage.setDescription(regex);
		wikiPage.setEncodingFormat(regex);
		wikiPage.setExternalReferenceCode(regex);
		wikiPage.setHeadline(regex);

		String json = WikiPageSerDes.toJSON(wikiPage);

		Assert.assertFalse(json.contains(regex));

		wikiPage = WikiPageSerDes.toDTO(json);

		Assert.assertEquals(regex, wikiPage.getContent());
		Assert.assertEquals(regex, wikiPage.getDescription());
		Assert.assertEquals(regex, wikiPage.getEncodingFormat());
		Assert.assertEquals(regex, wikiPage.getExternalReferenceCode());
		Assert.assertEquals(regex, wikiPage.getHeadline());
	}

	@Test
	public void testDeleteSiteWikiPageByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage wikiPage =
			testDeleteSiteWikiPageByExternalReferenceCode_addWikiPage();

		assertHttpResponseStatusCode(
			204,
			wikiPageResource.
				deleteSiteWikiPageByExternalReferenceCodeHttpResponse(
					wikiPage.getSiteId(), wikiPage.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			wikiPageResource.getSiteWikiPageByExternalReferenceCodeHttpResponse(
				wikiPage.getSiteId(), wikiPage.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			wikiPageResource.getSiteWikiPageByExternalReferenceCodeHttpResponse(
				wikiPage.getSiteId(), "-"));
	}

	protected WikiPage
			testDeleteSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSiteWikiPageByExternalReferenceCode()
		throws Exception {

		// No namespace

		WikiPage wikiPage1 =
			testGraphQLDeleteSiteWikiPageByExternalReferenceCode_addWikiPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSiteWikiPageByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + wikiPage1.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										wikiPage1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteSiteWikiPageByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wikiPageByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put("siteKey", "\"" + wikiPage1.getSiteId() + "\"");
							put(
								"externalReferenceCode",
								"\"" + wikiPage1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		WikiPage wikiPage2 =
			testGraphQLDeleteSiteWikiPageByExternalReferenceCode_addWikiPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteSiteWikiPageByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"siteKey",
										"\"" + wikiPage2.getSiteId() + "\"");
									put(
										"externalReferenceCode",
										"\"" +
											wikiPage2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteSiteWikiPageByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"wikiPageByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"siteKey",
									"\"" + wikiPage2.getSiteId() + "\"");
								put(
									"externalReferenceCode",
									"\"" +
										wikiPage2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WikiPage
			testGraphQLDeleteSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		return testGraphQLSiteWikiPage_addWikiPage();
	}

	@Test
	public void testDeleteWikiPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage wikiPage = testDeleteWikiPage_addWikiPage();

		assertHttpResponseStatusCode(
			204, wikiPageResource.deleteWikiPageHttpResponse(wikiPage.getId()));

		assertHttpResponseStatusCode(
			404, wikiPageResource.getWikiPageHttpResponse(wikiPage.getId()));
		assertHttpResponseStatusCode(
			404, wikiPageResource.getWikiPageHttpResponse(0L));
	}

	protected WikiPage testDeleteWikiPage_addWikiPage() throws Exception {
		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testGraphQLDeleteWikiPage() throws Exception {

		// No namespace

		WikiPage wikiPage1 = testGraphQLDeleteWikiPage_addWikiPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWikiPage",
						new HashMap<String, Object>() {
							{
								put("wikiPageId", wikiPage1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWikiPage"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"wikiPage",
					new HashMap<String, Object>() {
						{
							put("wikiPageId", wikiPage1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessDelivery_v1_0

		WikiPage wikiPage2 = testGraphQLDeleteWikiPage_addWikiPage();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessDelivery_v1_0",
						new GraphQLField(
							"deleteWikiPage",
							new HashMap<String, Object>() {
								{
									put("wikiPageId", wikiPage2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
				"Object/deleteWikiPage"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessDelivery_v1_0",
					new GraphQLField(
						"wikiPage",
						new HashMap<String, Object>() {
							{
								put("wikiPageId", wikiPage2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WikiPage testGraphQLDeleteWikiPage_addWikiPage()
		throws Exception {

		return testGraphQLWikiPage_addWikiPage();
	}

	@Test
	public void testDeleteWikiPageBatch() throws Exception {
		WikiPage wikiPage1 = testDeleteWikiPageBatch_addWikiPage();

		testDeleteWikiPageBatch_deleteWikiPage(202, null, wikiPage1.getId());

		assertHttpResponseStatusCode(
			404, wikiPageResource.getWikiPageHttpResponse(wikiPage1.getId()));
	}

	protected WikiPage testDeleteWikiPageBatch_addWikiPage() throws Exception {
		return testDeleteWikiPage_addWikiPage();
	}

	protected void testDeleteWikiPageBatch_deleteWikiPage(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			wikiPageResource.deleteWikiPageBatchHttpResponse(
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
	public void testGetSiteWikiPageByExternalReferenceCode() throws Exception {
		WikiPage postWikiPage =
			testGetSiteWikiPageByExternalReferenceCode_addWikiPage();

		WikiPage getWikiPage =
			wikiPageResource.getSiteWikiPageByExternalReferenceCode(
				postWikiPage.getSiteId(),
				postWikiPage.getExternalReferenceCode());

		assertEquals(postWikiPage, getWikiPage);
		assertValid(getWikiPage);
	}

	protected WikiPage testGetSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSiteWikiPageByExternalReferenceCode()
		throws Exception {

		WikiPage wikiPage =
			testGraphQLGetSiteWikiPageByExternalReferenceCode_addWikiPage();

		// No namespace

		Assert.assertTrue(
			equals(
				wikiPage,
				WikiPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wikiPageByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"siteKey",
											"\"" + wikiPage.getSiteId() + "\"");
										put(
											"externalReferenceCode",
											"\"" +
												wikiPage.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/wikiPageByExternalReferenceCode"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				wikiPage,
				WikiPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"wikiPageByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"siteKey",
												"\"" + wikiPage.getSiteId() +
													"\"");
											put(
												"externalReferenceCode",
												"\"" +
													wikiPage.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/wikiPageByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSiteWikiPageByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wikiPageByExternalReferenceCode",
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
							"wikiPageByExternalReferenceCode",
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

	protected WikiPage
			testGraphQLGetSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		return testGraphQLSiteWikiPage_addWikiPage();
	}

	@Test
	public void testGetWikiNodeWikiPagesPage() throws Exception {
		Long wikiNodeId = testGetWikiNodeWikiPagesPage_getWikiNodeId();
		Long irrelevantWikiNodeId =
			testGetWikiNodeWikiPagesPage_getIrrelevantWikiNodeId();

		Page<WikiPage> page = wikiPageResource.getWikiNodeWikiPagesPage(
			wikiNodeId, null, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantWikiNodeId != null) {
			WikiPage irrelevantWikiPage =
				testGetWikiNodeWikiPagesPage_addWikiPage(
					irrelevantWikiNodeId, randomIrrelevantWikiPage());

			page = wikiPageResource.getWikiNodeWikiPagesPage(
				irrelevantWikiNodeId, null, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWikiPage, (List<WikiPage>)page.getItems());
			assertValid(
				page,
				testGetWikiNodeWikiPagesPage_getExpectedActions(
					irrelevantWikiNodeId));
		}

		WikiPage wikiPage1 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		WikiPage wikiPage2 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		page = wikiPageResource.getWikiNodeWikiPagesPage(
			wikiNodeId, null, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(wikiPage1, (List<WikiPage>)page.getItems());
		assertContains(wikiPage2, (List<WikiPage>)page.getItems());
		assertValid(
			page, testGetWikiNodeWikiPagesPage_getExpectedActions(wikiNodeId));

		wikiPageResource.deleteWikiPage(wikiPage1.getId());

		wikiPageResource.deleteWikiPage(wikiPage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWikiNodeWikiPagesPage_getExpectedActions(Long wikiNodeId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-delivery/v1.0/wiki-nodes/{wikiNodeId}/wiki-pages/batch".
				replace("{wikiNodeId}", String.valueOf(wikiNodeId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long wikiNodeId = testGetWikiNodeWikiPagesPage_getWikiNodeId();

		WikiPage wikiPage1 = randomWikiPage();

		wikiPage1 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, wikiPage1);

		for (EntityField entityField : entityFields) {
			Page<WikiPage> page = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null,
				getFilterString(entityField, "between", wikiPage1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(wikiPage1),
				(List<WikiPage>)page.getItems());
		}
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithFilterDoubleEquals()
		throws Exception {

		testGetWikiNodeWikiPagesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithFilterStringContains()
		throws Exception {

		testGetWikiNodeWikiPagesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithFilterStringEquals()
		throws Exception {

		testGetWikiNodeWikiPagesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithFilterStringStartsWith()
		throws Exception {

		testGetWikiNodeWikiPagesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetWikiNodeWikiPagesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long wikiNodeId = testGetWikiNodeWikiPagesPage_getWikiNodeId();

		WikiPage wikiPage1 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage wikiPage2 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		for (EntityField entityField : entityFields) {
			Page<WikiPage> page = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null,
				getFilterString(entityField, operator, wikiPage1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(wikiPage1),
				(List<WikiPage>)page.getItems());
		}
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithPagination() throws Exception {
		Long wikiNodeId = testGetWikiNodeWikiPagesPage_getWikiNodeId();

		Page<WikiPage> wikiPagesPage =
			wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null, null, null);

		int totalCount = GetterUtil.getInteger(wikiPagesPage.getTotalCount());

		WikiPage wikiPage1 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		WikiPage wikiPage2 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		WikiPage wikiPage3 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WikiPage> page1 = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(wikiPage1, (List<WikiPage>)page1.getItems());

			Page<WikiPage> page2 = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(wikiPage2, (List<WikiPage>)page2.getItems());

			Page<WikiPage> page3 = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(wikiPage3, (List<WikiPage>)page3.getItems());
		}
		else {
			Page<WikiPage> page1 = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<WikiPage> wikiPages1 = (List<WikiPage>)page1.getItems();

			Assert.assertEquals(
				wikiPages1.toString(), totalCount + 2, wikiPages1.size());

			Page<WikiPage> page2 = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WikiPage> wikiPages2 = (List<WikiPage>)page2.getItems();

			Assert.assertEquals(wikiPages2.toString(), 1, wikiPages2.size());

			Page<WikiPage> page3 = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(wikiPage1, (List<WikiPage>)page3.getItems());
			assertContains(wikiPage2, (List<WikiPage>)page3.getItems());
			assertContains(wikiPage3, (List<WikiPage>)page3.getItems());
		}
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithSortDateTime()
		throws Exception {

		testGetWikiNodeWikiPagesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, wikiPage1, wikiPage2) -> {
				BeanTestUtil.setProperty(
					wikiPage1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithSortDouble() throws Exception {
		testGetWikiNodeWikiPagesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, wikiPage1, wikiPage2) -> {
				BeanTestUtil.setProperty(wikiPage1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(wikiPage2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithSortInteger() throws Exception {
		testGetWikiNodeWikiPagesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, wikiPage1, wikiPage2) -> {
				BeanTestUtil.setProperty(wikiPage1, entityField.getName(), 0);
				BeanTestUtil.setProperty(wikiPage2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWikiNodeWikiPagesPageWithSortString() throws Exception {
		testGetWikiNodeWikiPagesPageWithSort(
			EntityField.Type.STRING,
			(entityField, wikiPage1, wikiPage2) -> {
				Class<?> clazz = wikiPage1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						wikiPage1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						wikiPage2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						wikiPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						wikiPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						wikiPage1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						wikiPage2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWikiNodeWikiPagesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, WikiPage, WikiPage, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long wikiNodeId = testGetWikiNodeWikiPagesPage_getWikiNodeId();

		WikiPage wikiPage1 = randomWikiPage();
		WikiPage wikiPage2 = randomWikiPage();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, wikiPage1, wikiPage2);
		}

		wikiPage1 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, wikiPage1);

		wikiPage2 = testGetWikiNodeWikiPagesPage_addWikiPage(
			wikiNodeId, wikiPage2);

		Page<WikiPage> page = wikiPageResource.getWikiNodeWikiPagesPage(
			wikiNodeId, null, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<WikiPage> ascPage = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(wikiPage1, (List<WikiPage>)ascPage.getItems());
			assertContains(wikiPage2, (List<WikiPage>)ascPage.getItems());

			Page<WikiPage> descPage = wikiPageResource.getWikiNodeWikiPagesPage(
				wikiNodeId, null, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(wikiPage2, (List<WikiPage>)descPage.getItems());
			assertContains(wikiPage1, (List<WikiPage>)descPage.getItems());
		}
	}

	protected WikiPage testGetWikiNodeWikiPagesPage_addWikiPage(
			Long wikiNodeId, WikiPage wikiPage)
		throws Exception {

		return wikiPageResource.postWikiNodeWikiPage(wikiNodeId, wikiPage);
	}

	protected Long testGetWikiNodeWikiPagesPage_getWikiNodeId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWikiNodeWikiPagesPage_getIrrelevantWikiNodeId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetWikiNodeWikiPagesPage() throws Exception {
		Long wikiNodeId = testGetWikiNodeWikiPagesPage_getWikiNodeId();

		GraphQLField graphQLField = new GraphQLField(
			"wikiNodeWikiPages",
			new HashMap<String, Object>() {
				{
					put("wikiNodeId", wikiNodeId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject wikiNodeWikiPagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/wikiNodeWikiPages");

		long totalCount = wikiNodeWikiPagesJSONObject.getLong("totalCount");

		WikiPage wikiPage1 = testGraphQLWikiNodeWikiPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		WikiPage wikiPage2 = testGraphQLWikiNodeWikiPage_addWikiPage(
			wikiNodeId, randomWikiPage());

		wikiNodeWikiPagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/wikiNodeWikiPages");

		Assert.assertEquals(
			totalCount + 2, wikiNodeWikiPagesJSONObject.getLong("totalCount"));

		assertContains(
			wikiPage1,
			Arrays.asList(
				WikiPageSerDes.toDTOs(
					wikiNodeWikiPagesJSONObject.getString("items"))));
		assertContains(
			wikiPage2,
			Arrays.asList(
				WikiPageSerDes.toDTOs(
					wikiNodeWikiPagesJSONObject.getString("items"))));

		// Using the namespace headlessDelivery_v1_0

		wikiNodeWikiPagesJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessDelivery_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
			"JSONObject/wikiNodeWikiPages");

		Assert.assertEquals(
			totalCount + 2, wikiNodeWikiPagesJSONObject.getLong("totalCount"));

		assertContains(
			wikiPage1,
			Arrays.asList(
				WikiPageSerDes.toDTOs(
					wikiNodeWikiPagesJSONObject.getString("items"))));
		assertContains(
			wikiPage2,
			Arrays.asList(
				WikiPageSerDes.toDTOs(
					wikiNodeWikiPagesJSONObject.getString("items"))));
	}

	@Test
	public void testGetWikiPage() throws Exception {
		WikiPage postWikiPage = testGetWikiPage_addWikiPage();

		WikiPage getWikiPage = wikiPageResource.getWikiPage(
			postWikiPage.getId());

		assertEquals(postWikiPage, getWikiPage);
		assertValid(getWikiPage);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WikiPage postWikiPage = testGetWikiPage_addWikiPage();

		WikiPage getWikiPage = wikiPageResource.getWikiPage(
			postWikiPage.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.delivery.dto.v1_0.WikiPage"
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

		Object item = vulcanCRUDItemDelegate.getItem(postWikiPage.getId());

		assertEquals(getWikiPage, WikiPageSerDes.toDTO(item.toString()));
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

	protected WikiPage testGetWikiPage_addWikiPage() throws Exception {
		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testGraphQLGetWikiPage() throws Exception {
		WikiPage wikiPage = testGraphQLGetWikiPage_addWikiPage();

		// No namespace

		Assert.assertTrue(
			equals(
				wikiPage,
				WikiPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"wikiPage",
								new HashMap<String, Object>() {
									{
										put("wikiPageId", wikiPage.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/wikiPage"))));

		// Using the namespace headlessDelivery_v1_0

		Assert.assertTrue(
			equals(
				wikiPage,
				WikiPageSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessDelivery_v1_0",
								new GraphQLField(
									"wikiPage",
									new HashMap<String, Object>() {
										{
											put("wikiPageId", wikiPage.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessDelivery_v1_0",
						"Object/wikiPage"))));
	}

	@Test
	public void testGraphQLGetWikiPageNotFound() throws Exception {
		Long irrelevantWikiPageId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"wikiPage",
						new HashMap<String, Object>() {
							{
								put("wikiPageId", irrelevantWikiPageId);
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
							"wikiPage",
							new HashMap<String, Object>() {
								{
									put("wikiPageId", irrelevantWikiPageId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WikiPage testGraphQLGetWikiPage_addWikiPage() throws Exception {
		return testGraphQLWikiPage_addWikiPage();
	}

	@Test
	public void testGetWikiPagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage postWikiPage = testGetWikiPagePermissionsPage_addWikiPage();

		Page<Permission> page = wikiPageResource.getWikiPagePermissionsPage(
			postWikiPage.getId(), RoleConstants.GUEST);

		Assert.assertNotNull(page);
	}

	protected WikiPage testGetWikiPagePermissionsPage_addWikiPage()
		throws Exception {

		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testGetWikiPageWikiPagesPage() throws Exception {
		Long parentWikiPageId =
			testGetWikiPageWikiPagesPage_getParentWikiPageId();
		Long irrelevantParentWikiPageId =
			testGetWikiPageWikiPagesPage_getIrrelevantParentWikiPageId();

		Page<WikiPage> page = wikiPageResource.getWikiPageWikiPagesPage(
			parentWikiPageId);

		long totalCount = page.getTotalCount();

		if (irrelevantParentWikiPageId != null) {
			WikiPage irrelevantWikiPage =
				testGetWikiPageWikiPagesPage_addWikiPage(
					irrelevantParentWikiPageId, randomIrrelevantWikiPage());

			page = wikiPageResource.getWikiPageWikiPagesPage(
				irrelevantParentWikiPageId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWikiPage, (List<WikiPage>)page.getItems());
			assertValid(
				page,
				testGetWikiPageWikiPagesPage_getExpectedActions(
					irrelevantParentWikiPageId));
		}

		WikiPage wikiPage1 = testGetWikiPageWikiPagesPage_addWikiPage(
			parentWikiPageId, randomWikiPage());

		WikiPage wikiPage2 = testGetWikiPageWikiPagesPage_addWikiPage(
			parentWikiPageId, randomWikiPage());

		page = wikiPageResource.getWikiPageWikiPagesPage(parentWikiPageId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(wikiPage1, (List<WikiPage>)page.getItems());
		assertContains(wikiPage2, (List<WikiPage>)page.getItems());
		assertValid(
			page,
			testGetWikiPageWikiPagesPage_getExpectedActions(parentWikiPageId));

		wikiPageResource.deleteWikiPage(wikiPage1.getId());

		wikiPageResource.deleteWikiPage(wikiPage2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetWikiPageWikiPagesPage_getExpectedActions(
				Long parentWikiPageId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WikiPage testGetWikiPageWikiPagesPage_addWikiPage(
			Long parentWikiPageId, WikiPage wikiPage)
		throws Exception {

		return wikiPageResource.postWikiPageWikiPage(
			parentWikiPageId, wikiPage);
	}

	protected Long testGetWikiPageWikiPagesPage_getParentWikiPageId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWikiPageWikiPagesPage_getIrrelevantParentWikiPageId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostWikiNodeWikiPage() throws Exception {
		WikiPage randomWikiPage = randomWikiPage();

		WikiPage postWikiPage = testPostWikiNodeWikiPage_addWikiPage(
			randomWikiPage);

		assertEquals(randomWikiPage, postWikiPage);
		assertValid(postWikiPage);
	}

	protected WikiPage testPostWikiNodeWikiPage_addWikiPage(WikiPage wikiPage)
		throws Exception {

		return wikiPageResource.postWikiNodeWikiPage(
			testGetWikiNodeWikiPagesPage_getWikiNodeId(), wikiPage);
	}

	@Test
	public void testGraphQLPostWikiNodeWikiPage() throws Exception {
		WikiPage randomWikiPage = randomWikiPage();

		WikiPage wikiPage = testGraphQLWikiNodeWikiPage_addWikiPage(
			testGraphQLPostWikiNodeWikiPage_getWikiNodeId(randomWikiPage),
			randomWikiPage);

		Assert.assertTrue(equals(randomWikiPage, wikiPage));
	}

	protected Long testGraphQLPostWikiNodeWikiPage_getWikiNodeId(
			WikiPage wikiPage)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWikiPageWikiPage() throws Exception {
		WikiPage randomWikiPage = randomWikiPage();

		WikiPage postWikiPage = testPostWikiPageWikiPage_addWikiPage(
			randomWikiPage);

		assertEquals(randomWikiPage, postWikiPage);
		assertValid(postWikiPage);
	}

	protected WikiPage testPostWikiPageWikiPage_addWikiPage(WikiPage wikiPage)
		throws Exception {

		return wikiPageResource.postWikiPageWikiPage(
			testGetWikiPageWikiPagesPage_getParentWikiPageId(), wikiPage);
	}

	@Test
	public void testPutSiteWikiPageByExternalReferenceCode() throws Exception {
		WikiPage postWikiPage =
			testPutSiteWikiPageByExternalReferenceCode_addWikiPage();

		WikiPage randomWikiPage = randomWikiPage();

		WikiPage putWikiPage =
			wikiPageResource.putSiteWikiPageByExternalReferenceCode(
				postWikiPage.getSiteId(),
				postWikiPage.getExternalReferenceCode(), randomWikiPage);

		assertEquals(randomWikiPage, putWikiPage);
		assertValid(putWikiPage);

		WikiPage getWikiPage =
			wikiPageResource.getSiteWikiPageByExternalReferenceCode(
				putWikiPage.getSiteId(),
				putWikiPage.getExternalReferenceCode());

		assertEquals(randomWikiPage, getWikiPage);
		assertValid(getWikiPage);

		WikiPage newWikiPage =
			testPutSiteWikiPageByExternalReferenceCode_createWikiPage();

		putWikiPage = wikiPageResource.putSiteWikiPageByExternalReferenceCode(
			newWikiPage.getSiteId(), newWikiPage.getExternalReferenceCode(),
			newWikiPage);

		assertEquals(newWikiPage, putWikiPage);
		assertValid(putWikiPage);

		getWikiPage = wikiPageResource.getSiteWikiPageByExternalReferenceCode(
			putWikiPage.getSiteId(), putWikiPage.getExternalReferenceCode());

		assertEquals(newWikiPage, getWikiPage);

		Assert.assertEquals(
			newWikiPage.getExternalReferenceCode(),
			putWikiPage.getExternalReferenceCode());
	}

	protected WikiPage testPutSiteWikiPageByExternalReferenceCode_addWikiPage()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPage
			testPutSiteWikiPageByExternalReferenceCode_createWikiPage()
		throws Exception {

		return randomWikiPage();
	}

	@Test
	public void testPutWikiPage() throws Exception {
		WikiPage postWikiPage = testPutWikiPage_addWikiPage();

		WikiPage randomWikiPage = randomWikiPage();

		WikiPage putWikiPage = wikiPageResource.putWikiPage(
			postWikiPage.getId(), randomWikiPage);

		assertEquals(randomWikiPage, putWikiPage);
		assertValid(putWikiPage);

		WikiPage getWikiPage = wikiPageResource.getWikiPage(
			putWikiPage.getId());

		assertEquals(randomWikiPage, getWikiPage);
		assertValid(getWikiPage);
	}

	protected WikiPage testPutWikiPage_addWikiPage() throws Exception {
		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testPutWikiPagePermissionsPage() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage wikiPage = testPutWikiPagePermissionsPage_addWikiPage();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		com.liferay.portal.kernel.model.Role role = RoleTestUtil.addRole(
			RoleConstants.TYPE_REGULAR);

		assertHttpResponseStatusCode(
			200,
			wikiPageResource.putWikiPagePermissionsPageHttpResponse(
				wikiPage.getId(),
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
			wikiPageResource.putWikiPagePermissionsPageHttpResponse(
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

	protected WikiPage testPutWikiPagePermissionsPage_addWikiPage()
		throws Exception {

		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testPutWikiPageSubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage wikiPage = testPutWikiPageSubscribe_addWikiPage();

		assertHttpResponseStatusCode(
			204,
			wikiPageResource.putWikiPageSubscribeHttpResponse(
				wikiPage.getId()));

		assertHttpResponseStatusCode(
			404, wikiPageResource.putWikiPageSubscribeHttpResponse(0L));
	}

	protected WikiPage testPutWikiPageSubscribe_addWikiPage() throws Exception {
		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testPutWikiPageUnsubscribe() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WikiPage wikiPage = testPutWikiPageUnsubscribe_addWikiPage();

		assertHttpResponseStatusCode(
			204,
			wikiPageResource.putWikiPageUnsubscribeHttpResponse(
				wikiPage.getId()));

		assertHttpResponseStatusCode(
			404, wikiPageResource.putWikiPageUnsubscribeHttpResponse(0L));
	}

	protected WikiPage testPutWikiPageUnsubscribe_addWikiPage()
		throws Exception {

		return testPostWikiPageWikiPage_addWikiPage(randomWikiPage());
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WikiPage wikiPage1 = testBatchEngineDeleteImportTask_addWikiPage();

		testBatchEngineDeleteImportTask_deleteWikiPage(
			200, null, wikiPage1.getId());

		assertHttpResponseStatusCode(
			404, wikiPageResource.getWikiPageHttpResponse(wikiPage1.getId()));
	}

	protected WikiPage testBatchEngineDeleteImportTask_addWikiPage()
		throws Exception {

		return testDeleteWikiPage_addWikiPage();
	}

	protected void testBatchEngineDeleteImportTask_deleteWikiPage(
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
				"com.liferay.headless.delivery.dto.v1_0.WikiPage", null, null,
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected WikiPage testGraphQLSiteWikiPage_addWikiPage() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPage testGraphQLWikiPage_addWikiPage() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPage testGraphQLWikiNodeWikiPage_addWikiPage()
		throws Exception {

		return testGraphQLWikiNodeWikiPage_addWikiPage(
			testGraphQLWikiNodeWikiPage_getWikiNodeId(), randomWikiPage());
	}

	protected Long testGraphQLWikiNodeWikiPage_getWikiNodeId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected WikiPage testGraphQLWikiNodeWikiPage_addWikiPage(
			Long wikiNodeId, WikiPage wikiPage)
		throws Exception {

		JSONDeserializer<WikiPage> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(WikiPage.class)) {

			if (getGraphQLValue(field.get(wikiPage)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(wikiPage)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createWikiNodeWikiPage",
						new HashMap<String, Object>() {
							{
								put("wikiNodeId", wikiNodeId);
								put("wikiPage", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createWikiNodeWikiPage"),
			WikiPage.class);
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

	protected void assertContains(WikiPage wikiPage, List<WikiPage> wikiPages) {
		boolean contains = false;

		for (WikiPage item : wikiPages) {
			if (equals(wikiPage, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			wikiPages + " does not contain " + wikiPage, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(WikiPage wikiPage1, WikiPage wikiPage2) {
		Assert.assertTrue(
			wikiPage1 + " does not equal " + wikiPage2,
			equals(wikiPage1, wikiPage2));
	}

	protected void assertEquals(
		List<WikiPage> wikiPages1, List<WikiPage> wikiPages2) {

		Assert.assertEquals(wikiPages1.size(), wikiPages2.size());

		for (int i = 0; i < wikiPages1.size(); i++) {
			WikiPage wikiPage1 = wikiPages1.get(i);
			WikiPage wikiPage2 = wikiPages2.get(i);

			assertEquals(wikiPage1, wikiPage2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WikiPage> wikiPages1, List<WikiPage> wikiPages2) {

		Assert.assertEquals(wikiPages1.size(), wikiPages2.size());

		for (WikiPage wikiPage1 : wikiPages1) {
			boolean contains = false;

			for (WikiPage wikiPage2 : wikiPages2) {
				if (equals(wikiPage1, wikiPage2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				wikiPages2 + " does not contain " + wikiPage1, contains);
		}
	}

	protected void assertValid(WikiPage wikiPage) throws Exception {
		boolean valid = true;

		if (wikiPage.getDateCreated() == null) {
			valid = false;
		}

		if (wikiPage.getDateModified() == null) {
			valid = false;
		}

		if (wikiPage.getId() == null) {
			valid = false;
		}

		if (!Objects.equals(wikiPage.getSiteId(), testGroup.getGroupId())) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (wikiPage.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (wikiPage.getAggregateRating() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (wikiPage.getContent() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (wikiPage.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (wikiPage.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (wikiPage.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (wikiPage.getEncodingFormat() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (wikiPage.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (wikiPage.getHeadline() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (wikiPage.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfAttachments", additionalAssertFieldName)) {

				if (wikiPage.getNumberOfAttachments() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfWikiPages", additionalAssertFieldName)) {

				if (wikiPage.getNumberOfWikiPages() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("parentWikiPageId", additionalAssertFieldName)) {
				if (wikiPage.getParentWikiPageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (wikiPage.getRelatedContents() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (wikiPage.getSubscribed() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (wikiPage.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (wikiPage.getTaxonomyCategoryIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (wikiPage.getViewableBy() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("wikiNodeId", additionalAssertFieldName)) {
				if (wikiPage.getWikiNodeId() == null) {
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

	protected void assertValid(Page<WikiPage> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WikiPage> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WikiPage> wikiPages = page.getItems();

		int size = wikiPages.size();

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
					com.liferay.headless.delivery.dto.v1_0.WikiPage.class)) {

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

	protected boolean equals(WikiPage wikiPage1, WikiPage wikiPage2) {
		if (wikiPage1 == wikiPage2) {
			return true;
		}

		if (!Objects.equals(wikiPage1.getSiteId(), wikiPage2.getSiteId())) {
			return false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)wikiPage1.getActions(),
						(Map)wikiPage2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("aggregateRating", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getAggregateRating(),
						wikiPage2.getAggregateRating())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("content", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getContent(), wikiPage2.getContent())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getCreator(), wikiPage2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getCustomFields(),
						wikiPage2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getDateCreated(),
						wikiPage2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getDateModified(),
						wikiPage2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getDescription(),
						wikiPage2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("encodingFormat", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getEncodingFormat(),
						wikiPage2.getEncodingFormat())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiPage1.getExternalReferenceCode(),
						wikiPage2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("headline", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getHeadline(), wikiPage2.getHeadline())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(wikiPage1.getId(), wikiPage2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getKeywords(), wikiPage2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfAttachments", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiPage1.getNumberOfAttachments(),
						wikiPage2.getNumberOfAttachments())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"numberOfWikiPages", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiPage1.getNumberOfWikiPages(),
						wikiPage2.getNumberOfWikiPages())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("parentWikiPageId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getParentWikiPageId(),
						wikiPage2.getParentWikiPageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("relatedContents", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getRelatedContents(),
						wikiPage2.getRelatedContents())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("subscribed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getSubscribed(), wikiPage2.getSubscribed())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiPage1.getTaxonomyCategoryBriefs(),
						wikiPage2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryIds", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						wikiPage1.getTaxonomyCategoryIds(),
						wikiPage2.getTaxonomyCategoryIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("viewableBy", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getViewableBy(), wikiPage2.getViewableBy())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("wikiNodeId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						wikiPage1.getWikiNodeId(), wikiPage2.getWikiNodeId())) {

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

		if (!(_wikiPageResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_wikiPageResource;

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
		EntityField entityField, String operator, WikiPage wikiPage) {

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

		if (entityFieldName.equals("aggregateRating")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("content")) {
			Object object = wikiPage.getContent();

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
				Date date = wikiPage.getDateCreated();

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

				sb.append(_format.format(wikiPage.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = wikiPage.getDateModified();

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

				sb.append(_format.format(wikiPage.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = wikiPage.getDescription();

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

		if (entityFieldName.equals("encodingFormat")) {
			Object object = wikiPage.getEncodingFormat();

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
			Object object = wikiPage.getExternalReferenceCode();

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

		if (entityFieldName.equals("headline")) {
			Object object = wikiPage.getHeadline();

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

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("numberOfAttachments")) {
			sb.append(String.valueOf(wikiPage.getNumberOfAttachments()));

			return sb.toString();
		}

		if (entityFieldName.equals("numberOfWikiPages")) {
			sb.append(String.valueOf(wikiPage.getNumberOfWikiPages()));

			return sb.toString();
		}

		if (entityFieldName.equals("parentWikiPageId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("relatedContents")) {
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

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("viewableBy")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("wikiNodeId")) {
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

	protected WikiPage randomWikiPage() throws Exception {
		return new WikiPage() {
			{
				content = StringUtil.toLowerCase(RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				encodingFormat = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				headline = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				numberOfAttachments = RandomTestUtil.randomInt();
				numberOfWikiPages = RandomTestUtil.randomInt();
				parentWikiPageId = RandomTestUtil.randomLong();
				siteId = testGroup.getGroupId();
				subscribed = RandomTestUtil.randomBoolean();
				wikiNodeId = RandomTestUtil.randomLong();
			}
		};
	}

	protected WikiPage randomIrrelevantWikiPage() throws Exception {
		WikiPage randomIrrelevantWikiPage = randomWikiPage();

		randomIrrelevantWikiPage.setSiteId(irrelevantGroup.getGroupId());

		return randomIrrelevantWikiPage;
	}

	protected WikiPage randomPatchWikiPage() throws Exception {
		return randomWikiPage();
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

	protected WikiPageResource wikiPageResource;
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
		LogFactoryUtil.getLog(BaseWikiPageResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.delivery.resource.v1_0.WikiPageResource
		_wikiPageResource;

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