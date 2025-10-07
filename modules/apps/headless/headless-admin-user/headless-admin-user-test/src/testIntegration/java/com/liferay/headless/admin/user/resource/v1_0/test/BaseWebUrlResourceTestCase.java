/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.admin.user.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.admin.user.client.dto.v1_0.WebUrl;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.resource.v1_0.WebUrlResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.WebUrlSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONArray;
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
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
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
public abstract class BaseWebUrlResourceTestCase {

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

		_webUrlResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		webUrlResource = WebUrlResource.builder(
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

		WebUrl webUrl1 = randomWebUrl();

		String json = objectMapper.writeValueAsString(webUrl1);

		WebUrl webUrl2 = WebUrlSerDes.toDTO(json);

		Assert.assertTrue(equals(webUrl1, webUrl2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WebUrl webUrl = randomWebUrl();

		String json1 = objectMapper.writeValueAsString(webUrl);
		String json2 = WebUrlSerDes.toJSON(webUrl);

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

		WebUrl webUrl = randomWebUrl();

		webUrl.setExternalReferenceCode(regex);
		webUrl.setUrl(regex);
		webUrl.setUrlType(regex);

		String json = WebUrlSerDes.toJSON(webUrl);

		Assert.assertFalse(json.contains(regex));

		webUrl = WebUrlSerDes.toDTO(json);

		Assert.assertEquals(regex, webUrl.getExternalReferenceCode());
		Assert.assertEquals(regex, webUrl.getUrl());
		Assert.assertEquals(regex, webUrl.getUrlType());
	}

	@Test
	public void testDeleteWebUrl() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WebUrl webUrl = testDeleteWebUrl_addWebUrl();

		assertHttpResponseStatusCode(
			204, webUrlResource.deleteWebUrlHttpResponse(webUrl.getId()));

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl.getId()));
		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(0L));
	}

	protected WebUrl testDeleteWebUrl_addWebUrl() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWebUrl() throws Exception {

		// No namespace

		WebUrl webUrl1 = testGraphQLDeleteWebUrl_addWebUrl();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWebUrl",
						new HashMap<String, Object>() {
							{
								put("webUrlId", webUrl1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteWebUrl"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"webUrl",
					new HashMap<String, Object>() {
						{
							put("webUrlId", webUrl1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		WebUrl webUrl2 = testGraphQLDeleteWebUrl_addWebUrl();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteWebUrl",
							new HashMap<String, Object>() {
								{
									put("webUrlId", webUrl2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteWebUrl"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"webUrl",
						new HashMap<String, Object>() {
							{
								put("webUrlId", webUrl2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WebUrl testGraphQLDeleteWebUrl_addWebUrl() throws Exception {
		return testGraphQLWebUrl_addWebUrl();
	}

	@Test
	public void testDeleteWebUrlBatch() throws Exception {
		WebUrl webUrl1 = testDeleteWebUrlBatch_addWebUrl();

		testDeleteWebUrlBatch_deleteWebUrl(
			202, webUrl1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl1.getId()));

		webUrl1 = testDeleteWebUrlBatch_addWebUrl();

		testDeleteWebUrlBatch_deleteWebUrl(202, null, webUrl1.getId());

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl1.getId()));

		webUrl1 = testDeleteWebUrlBatch_addWebUrl();
		WebUrl webUrl2 = testDeleteWebUrlBatch_addWebUrl();

		testDeleteWebUrlBatch_deleteWebUrl(
			202, webUrl2.getExternalReferenceCode(), webUrl1.getId());

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl1.getId()));
		assertHttpResponseStatusCode(
			200, webUrlResource.getWebUrlHttpResponse(webUrl2.getId()));

		testDeleteWebUrlBatch_deleteWebUrl(
			202, webUrl2.getExternalReferenceCode(), webUrl1.getId());

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl2.getId()));
	}

	protected WebUrl testDeleteWebUrlBatch_addWebUrl() throws Exception {
		return testDeleteWebUrl_addWebUrl();
	}

	protected void testDeleteWebUrlBatch_deleteWebUrl(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			webUrlResource.deleteWebUrlBatchHttpResponse(
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
	public void testDeleteWebUrlByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WebUrl webUrl = testDeleteWebUrlByExternalReferenceCode_addWebUrl();

		assertHttpResponseStatusCode(
			204,
			webUrlResource.deleteWebUrlByExternalReferenceCodeHttpResponse(
				webUrl.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			webUrlResource.getWebUrlByExternalReferenceCodeHttpResponse(
				webUrl.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			webUrlResource.getWebUrlByExternalReferenceCodeHttpResponse("-"));
	}

	protected WebUrl testDeleteWebUrlByExternalReferenceCode_addWebUrl()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWebUrlByExternalReferenceCode()
		throws Exception {

		// No namespace

		WebUrl webUrl1 =
			testGraphQLDeleteWebUrlByExternalReferenceCode_addWebUrl();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWebUrlByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + webUrl1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteWebUrlByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"webUrlByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + webUrl1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		WebUrl webUrl2 =
			testGraphQLDeleteWebUrlByExternalReferenceCode_addWebUrl();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteWebUrlByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											webUrl2.getExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteWebUrlByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"webUrlByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + webUrl2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected WebUrl testGraphQLDeleteWebUrlByExternalReferenceCode_addWebUrl()
		throws Exception {

		return testGraphQLWebUrl_addWebUrl();
	}

	@Test
	public void testGetAccountByExternalReferenceCodeWebUrlsPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeWebUrlsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeWebUrlsPage_getIrrelevantExternalReferenceCode();

		Page<WebUrl> page =
			webUrlResource.getAccountByExternalReferenceCodeWebUrlsPage(
				externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WebUrl irrelevantWebUrl =
				testGetAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
					irrelevantExternalReferenceCode, randomIrrelevantWebUrl());

			page = webUrlResource.getAccountByExternalReferenceCodeWebUrlsPage(
				irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWebUrl, (List<WebUrl>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeWebUrlsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WebUrl webUrl1 =
			testGetAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
				externalReferenceCode, randomWebUrl());

		WebUrl webUrl2 =
			testGetAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
				externalReferenceCode, randomWebUrl());

		page = webUrlResource.getAccountByExternalReferenceCodeWebUrlsPage(
			externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(webUrl1, (List<WebUrl>)page.getItems());
		assertContains(webUrl2, (List<WebUrl>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeWebUrlsPage_getExpectedActions(
				externalReferenceCode));

		webUrlResource.deleteWebUrl(webUrl1.getId());

		webUrlResource.deleteWebUrl(webUrl2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeWebUrlsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WebUrl testGetAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
			String externalReferenceCode, WebUrl webUrl)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeWebUrlsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeWebUrlsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountWebUrlsPage() throws Exception {
		Long accountId = testGetAccountWebUrlsPage_getAccountId();
		Long irrelevantAccountId =
			testGetAccountWebUrlsPage_getIrrelevantAccountId();

		Page<WebUrl> page = webUrlResource.getAccountWebUrlsPage(accountId);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountId != null) {
			WebUrl irrelevantWebUrl = testGetAccountWebUrlsPage_addWebUrl(
				irrelevantAccountId, randomIrrelevantWebUrl());

			page = webUrlResource.getAccountWebUrlsPage(irrelevantAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWebUrl, (List<WebUrl>)page.getItems());
			assertValid(
				page,
				testGetAccountWebUrlsPage_getExpectedActions(
					irrelevantAccountId));
		}

		WebUrl webUrl1 = testGetAccountWebUrlsPage_addWebUrl(
			accountId, randomWebUrl());

		WebUrl webUrl2 = testGetAccountWebUrlsPage_addWebUrl(
			accountId, randomWebUrl());

		page = webUrlResource.getAccountWebUrlsPage(accountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(webUrl1, (List<WebUrl>)page.getItems());
		assertContains(webUrl2, (List<WebUrl>)page.getItems());
		assertValid(
			page, testGetAccountWebUrlsPage_getExpectedActions(accountId));

		webUrlResource.deleteWebUrl(webUrl1.getId());

		webUrlResource.deleteWebUrl(webUrl2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountWebUrlsPage_getExpectedActions(Long accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WebUrl testGetAccountWebUrlsPage_addWebUrl(
			Long accountId, WebUrl webUrl)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountWebUrlsPage_getAccountId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountWebUrlsPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeWebUrlsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_getIrrelevantExternalReferenceCode();

		Page<WebUrl> page =
			webUrlResource.getOrganizationByExternalReferenceCodeWebUrlsPage(
				externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WebUrl irrelevantWebUrl =
				testGetOrganizationByExternalReferenceCodeWebUrlsPage_addWebUrl(
					irrelevantExternalReferenceCode, randomIrrelevantWebUrl());

			page =
				webUrlResource.
					getOrganizationByExternalReferenceCodeWebUrlsPage(
						irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWebUrl, (List<WebUrl>)page.getItems());
			assertValid(
				page,
				testGetOrganizationByExternalReferenceCodeWebUrlsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WebUrl webUrl1 =
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_addWebUrl(
				externalReferenceCode, randomWebUrl());

		WebUrl webUrl2 =
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_addWebUrl(
				externalReferenceCode, randomWebUrl());

		page = webUrlResource.getOrganizationByExternalReferenceCodeWebUrlsPage(
			externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(webUrl1, (List<WebUrl>)page.getItems());
		assertContains(webUrl2, (List<WebUrl>)page.getItems());
		assertValid(
			page,
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_getExpectedActions(
				externalReferenceCode));

		webUrlResource.deleteWebUrl(webUrl1.getId());

		webUrlResource.deleteWebUrl(webUrl2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WebUrl
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_addWebUrl(
				String externalReferenceCode, WebUrl webUrl)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeWebUrlsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationWebUrlsPage() throws Exception {
		String organizationId =
			testGetOrganizationWebUrlsPage_getOrganizationId();
		String irrelevantOrganizationId =
			testGetOrganizationWebUrlsPage_getIrrelevantOrganizationId();

		Page<WebUrl> page = webUrlResource.getOrganizationWebUrlsPage(
			organizationId);

		long totalCount = page.getTotalCount();

		if (irrelevantOrganizationId != null) {
			WebUrl irrelevantWebUrl = testGetOrganizationWebUrlsPage_addWebUrl(
				irrelevantOrganizationId, randomIrrelevantWebUrl());

			page = webUrlResource.getOrganizationWebUrlsPage(
				irrelevantOrganizationId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWebUrl, (List<WebUrl>)page.getItems());
			assertValid(
				page,
				testGetOrganizationWebUrlsPage_getExpectedActions(
					irrelevantOrganizationId));
		}

		WebUrl webUrl1 = testGetOrganizationWebUrlsPage_addWebUrl(
			organizationId, randomWebUrl());

		WebUrl webUrl2 = testGetOrganizationWebUrlsPage_addWebUrl(
			organizationId, randomWebUrl());

		page = webUrlResource.getOrganizationWebUrlsPage(organizationId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(webUrl1, (List<WebUrl>)page.getItems());
		assertContains(webUrl2, (List<WebUrl>)page.getItems());
		assertValid(
			page,
			testGetOrganizationWebUrlsPage_getExpectedActions(organizationId));

		webUrlResource.deleteWebUrl(webUrl1.getId());

		webUrlResource.deleteWebUrl(webUrl2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationWebUrlsPage_getExpectedActions(
				String organizationId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WebUrl testGetOrganizationWebUrlsPage_addWebUrl(
			String organizationId, WebUrl webUrl)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetOrganizationWebUrlsPage_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationWebUrlsPage_getIrrelevantOrganizationId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserAccountByExternalReferenceCodeWebUrlsPage()
		throws Exception {

		String externalReferenceCode =
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_getIrrelevantExternalReferenceCode();

		Page<WebUrl> page =
			webUrlResource.getUserAccountByExternalReferenceCodeWebUrlsPage(
				externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WebUrl irrelevantWebUrl =
				testGetUserAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
					irrelevantExternalReferenceCode, randomIrrelevantWebUrl());

			page =
				webUrlResource.getUserAccountByExternalReferenceCodeWebUrlsPage(
					irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWebUrl, (List<WebUrl>)page.getItems());
			assertValid(
				page,
				testGetUserAccountByExternalReferenceCodeWebUrlsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WebUrl webUrl1 =
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
				externalReferenceCode, randomWebUrl());

		WebUrl webUrl2 =
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
				externalReferenceCode, randomWebUrl());

		page = webUrlResource.getUserAccountByExternalReferenceCodeWebUrlsPage(
			externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(webUrl1, (List<WebUrl>)page.getItems());
		assertContains(webUrl2, (List<WebUrl>)page.getItems());
		assertValid(
			page,
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_getExpectedActions(
				externalReferenceCode));

		webUrlResource.deleteWebUrl(webUrl1.getId());

		webUrlResource.deleteWebUrl(webUrl2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WebUrl
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_addWebUrl(
				String externalReferenceCode, WebUrl webUrl)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserAccountByExternalReferenceCodeWebUrlsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserAccountWebUrlsPage() throws Exception {
		Long userAccountId = testGetUserAccountWebUrlsPage_getUserAccountId();
		Long irrelevantUserAccountId =
			testGetUserAccountWebUrlsPage_getIrrelevantUserAccountId();

		Page<WebUrl> page = webUrlResource.getUserAccountWebUrlsPage(
			userAccountId);

		long totalCount = page.getTotalCount();

		if (irrelevantUserAccountId != null) {
			WebUrl irrelevantWebUrl = testGetUserAccountWebUrlsPage_addWebUrl(
				irrelevantUserAccountId, randomIrrelevantWebUrl());

			page = webUrlResource.getUserAccountWebUrlsPage(
				irrelevantUserAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantWebUrl, (List<WebUrl>)page.getItems());
			assertValid(
				page,
				testGetUserAccountWebUrlsPage_getExpectedActions(
					irrelevantUserAccountId));
		}

		WebUrl webUrl1 = testGetUserAccountWebUrlsPage_addWebUrl(
			userAccountId, randomWebUrl());

		WebUrl webUrl2 = testGetUserAccountWebUrlsPage_addWebUrl(
			userAccountId, randomWebUrl());

		page = webUrlResource.getUserAccountWebUrlsPage(userAccountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(webUrl1, (List<WebUrl>)page.getItems());
		assertContains(webUrl2, (List<WebUrl>)page.getItems());
		assertValid(
			page,
			testGetUserAccountWebUrlsPage_getExpectedActions(userAccountId));

		webUrlResource.deleteWebUrl(webUrl1.getId());

		webUrlResource.deleteWebUrl(webUrl2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountWebUrlsPage_getExpectedActions(Long userAccountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected WebUrl testGetUserAccountWebUrlsPage_addWebUrl(
			Long userAccountId, WebUrl webUrl)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserAccountWebUrlsPage_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserAccountWebUrlsPage_getIrrelevantUserAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWebUrl() throws Exception {
		WebUrl postWebUrl = testGetWebUrl_addWebUrl();

		WebUrl getWebUrl = webUrlResource.getWebUrl(postWebUrl.getId());

		assertEquals(postWebUrl, getWebUrl);
		assertValid(getWebUrl);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		WebUrl postWebUrl = testGetWebUrl_addWebUrl();

		WebUrl getWebUrl = webUrlResource.getWebUrl(postWebUrl.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.admin.user.dto.v1_0.WebUrl"
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

		Object item = vulcanCRUDItemDelegate.getItem(postWebUrl.getId());

		assertEquals(getWebUrl, WebUrlSerDes.toDTO(item.toString()));
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

	protected WebUrl testGetWebUrl_addWebUrl() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWebUrl() throws Exception {
		WebUrl webUrl = testGraphQLGetWebUrl_addWebUrl();

		// No namespace

		Assert.assertTrue(
			equals(
				webUrl,
				WebUrlSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"webUrl",
								new HashMap<String, Object>() {
									{
										put("webUrlId", webUrl.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/webUrl"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				webUrl,
				WebUrlSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"webUrl",
									new HashMap<String, Object>() {
										{
											put("webUrlId", webUrl.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/webUrl"))));
	}

	@Test
	public void testGraphQLGetWebUrlNotFound() throws Exception {
		Long irrelevantWebUrlId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"webUrl",
						new HashMap<String, Object>() {
							{
								put("webUrlId", irrelevantWebUrlId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"webUrl",
							new HashMap<String, Object>() {
								{
									put("webUrlId", irrelevantWebUrlId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WebUrl testGraphQLGetWebUrl_addWebUrl() throws Exception {
		return testGraphQLWebUrl_addWebUrl();
	}

	@Test
	public void testGetWebUrlByExternalReferenceCode() throws Exception {
		WebUrl postWebUrl = testGetWebUrlByExternalReferenceCode_addWebUrl();

		WebUrl getWebUrl = webUrlResource.getWebUrlByExternalReferenceCode(
			postWebUrl.getExternalReferenceCode());

		assertEquals(postWebUrl, getWebUrl);
		assertValid(getWebUrl);
	}

	protected WebUrl testGetWebUrlByExternalReferenceCode_addWebUrl()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetWebUrlByExternalReferenceCode() throws Exception {
		WebUrl webUrl = testGraphQLGetWebUrlByExternalReferenceCode_addWebUrl();

		// No namespace

		Assert.assertTrue(
			equals(
				webUrl,
				WebUrlSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"webUrlByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												webUrl.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/webUrlByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				webUrl,
				WebUrlSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"webUrlByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													webUrl.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/webUrlByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetWebUrlByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"webUrlByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"webUrlByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected WebUrl testGraphQLGetWebUrlByExternalReferenceCode_addWebUrl()
		throws Exception {

		return testGraphQLWebUrl_addWebUrl();
	}

	@Test
	public void testPatchWebUrl() throws Exception {
		WebUrl postWebUrl = testPatchWebUrl_addWebUrl();

		WebUrl randomPatchWebUrl = randomPatchWebUrl();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WebUrl patchWebUrl = webUrlResource.patchWebUrl(
			postWebUrl.getId(), randomPatchWebUrl);

		WebUrl expectedPatchWebUrl = postWebUrl.clone();

		BeanTestUtil.copyProperties(randomPatchWebUrl, expectedPatchWebUrl);

		WebUrl getWebUrl = webUrlResource.getWebUrl(patchWebUrl.getId());

		assertEquals(expectedPatchWebUrl, getWebUrl);
		assertValid(getWebUrl);
	}

	protected WebUrl testPatchWebUrl_addWebUrl() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchWebUrlByExternalReferenceCode() throws Exception {
		WebUrl postWebUrl = testPatchWebUrlByExternalReferenceCode_addWebUrl();

		WebUrl randomPatchWebUrl = randomPatchWebUrl();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WebUrl patchWebUrl = webUrlResource.patchWebUrlByExternalReferenceCode(
			postWebUrl.getExternalReferenceCode(), randomPatchWebUrl);

		WebUrl expectedPatchWebUrl = postWebUrl.clone();

		BeanTestUtil.copyProperties(randomPatchWebUrl, expectedPatchWebUrl);

		WebUrl getWebUrl = webUrlResource.getWebUrlByExternalReferenceCode(
			patchWebUrl.getExternalReferenceCode());

		assertEquals(expectedPatchWebUrl, getWebUrl);
		assertValid(getWebUrl);
	}

	protected WebUrl testPatchWebUrlByExternalReferenceCode_addWebUrl()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WebUrl webUrl1 = testBatchEngineDeleteImportTask_addWebUrl();

		testBatchEngineDeleteImportTask_deleteWebUrl(
			200, webUrl1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl1.getId()));

		webUrl1 = testBatchEngineDeleteImportTask_addWebUrl();

		testBatchEngineDeleteImportTask_deleteWebUrl(
			200, null, webUrl1.getId());

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl1.getId()));

		webUrl1 = testBatchEngineDeleteImportTask_addWebUrl();
		WebUrl webUrl2 = testBatchEngineDeleteImportTask_addWebUrl();

		testBatchEngineDeleteImportTask_deleteWebUrl(
			200, webUrl2.getExternalReferenceCode(), webUrl1.getId());

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl1.getId()));
		assertHttpResponseStatusCode(
			200, webUrlResource.getWebUrlHttpResponse(webUrl2.getId()));

		testBatchEngineDeleteImportTask_deleteWebUrl(
			200, webUrl2.getExternalReferenceCode(), webUrl1.getId());

		assertHttpResponseStatusCode(
			404, webUrlResource.getWebUrlHttpResponse(webUrl2.getId()));
	}

	protected WebUrl testBatchEngineDeleteImportTask_addWebUrl()
		throws Exception {

		return testDeleteWebUrl_addWebUrl();
	}

	protected void testBatchEngineDeleteImportTask_deleteWebUrl(
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
				"com.liferay.headless.admin.user.dto.v1_0.WebUrl", null, null,
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

	protected WebUrl testGraphQLWebUrl_addWebUrl() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(WebUrl webUrl, List<WebUrl> webUrls) {
		boolean contains = false;

		for (WebUrl item : webUrls) {
			if (equals(webUrl, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(webUrls + " does not contain " + webUrl, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(WebUrl webUrl1, WebUrl webUrl2) {
		Assert.assertTrue(
			webUrl1 + " does not equal " + webUrl2, equals(webUrl1, webUrl2));
	}

	protected void assertEquals(List<WebUrl> webUrls1, List<WebUrl> webUrls2) {
		Assert.assertEquals(webUrls1.size(), webUrls2.size());

		for (int i = 0; i < webUrls1.size(); i++) {
			WebUrl webUrl1 = webUrls1.get(i);
			WebUrl webUrl2 = webUrls2.get(i);

			assertEquals(webUrl1, webUrl2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WebUrl> webUrls1, List<WebUrl> webUrls2) {

		Assert.assertEquals(webUrls1.size(), webUrls2.size());

		for (WebUrl webUrl1 : webUrls1) {
			boolean contains = false;

			for (WebUrl webUrl2 : webUrls2) {
				if (equals(webUrl1, webUrl2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				webUrls2 + " does not contain " + webUrl1, contains);
		}
	}

	protected void assertValid(WebUrl webUrl) throws Exception {
		boolean valid = true;

		if (webUrl.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (webUrl.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (webUrl.getPrimary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (webUrl.getUrl() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("urlType", additionalAssertFieldName)) {
				if (webUrl.getUrlType() == null) {
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

	protected void assertValid(Page<WebUrl> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WebUrl> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WebUrl> webUrls = page.getItems();

		int size = webUrls.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.admin.user.dto.v1_0.WebUrl.class)) {

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

	protected boolean equals(WebUrl webUrl1, WebUrl webUrl2) {
		if (webUrl1 == webUrl2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						webUrl1.getExternalReferenceCode(),
						webUrl2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(webUrl1.getId(), webUrl2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						webUrl1.getPrimary(), webUrl2.getPrimary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("url", additionalAssertFieldName)) {
				if (!Objects.deepEquals(webUrl1.getUrl(), webUrl2.getUrl())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("urlType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						webUrl1.getUrlType(), webUrl2.getUrlType())) {

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

		if (!(_webUrlResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_webUrlResource;

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
		EntityField entityField, String operator, WebUrl webUrl) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = webUrl.getExternalReferenceCode();

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

		if (entityFieldName.equals("primary")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("url")) {
			Object object = webUrl.getUrl();

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

		if (entityFieldName.equals("urlType")) {
			Object object = webUrl.getUrlType();

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

	protected WebUrl randomWebUrl() throws Exception {
		return new WebUrl() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				primary = RandomTestUtil.randomBoolean();
				url = StringUtil.toLowerCase(RandomTestUtil.randomString());
				urlType = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected WebUrl randomIrrelevantWebUrl() throws Exception {
		WebUrl randomIrrelevantWebUrl = randomWebUrl();

		return randomIrrelevantWebUrl;
	}

	protected WebUrl randomPatchWebUrl() throws Exception {
		return randomWebUrl();
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

	protected WebUrlResource webUrlResource;
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
		LogFactoryUtil.getLog(BaseWebUrlResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.WebUrlResource
		_webUrlResource;

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