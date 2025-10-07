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

import com.liferay.headless.admin.user.client.dto.v1_0.AccountGroup;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountGroupResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.AccountGroupSerDes;
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
public abstract class BaseAccountGroupResourceTestCase {

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

		_accountGroupResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountGroupResource = AccountGroupResource.builder(
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

		AccountGroup accountGroup1 = randomAccountGroup();

		String json = objectMapper.writeValueAsString(accountGroup1);

		AccountGroup accountGroup2 = AccountGroupSerDes.toDTO(json);

		Assert.assertTrue(equals(accountGroup1, accountGroup2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountGroup accountGroup = randomAccountGroup();

		String json1 = objectMapper.writeValueAsString(accountGroup);
		String json2 = AccountGroupSerDes.toJSON(accountGroup);

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

		AccountGroup accountGroup = randomAccountGroup();

		accountGroup.setDescription(regex);
		accountGroup.setExternalReferenceCode(regex);
		accountGroup.setName(regex);

		String json = AccountGroupSerDes.toJSON(accountGroup);

		Assert.assertFalse(json.contains(regex));

		accountGroup = AccountGroupSerDes.toDTO(json);

		Assert.assertEquals(regex, accountGroup.getDescription());
		Assert.assertEquals(regex, accountGroup.getExternalReferenceCode());
		Assert.assertEquals(regex, accountGroup.getName());
	}

	@Test
	public void testDeleteAccountGroup() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup accountGroup = testDeleteAccountGroup_addAccountGroup();

		assertHttpResponseStatusCode(
			204,
			accountGroupResource.deleteAccountGroupHttpResponse(
				accountGroup.getId()));

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup.getId()));
		assertHttpResponseStatusCode(
			404, accountGroupResource.getAccountGroupHttpResponse(0L));
	}

	protected AccountGroup testDeleteAccountGroup_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountGroup() throws Exception {

		// No namespace

		AccountGroup accountGroup1 =
			testGraphQLDeleteAccountGroup_addAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountGroup",
						new HashMap<String, Object>() {
							{
								put("accountGroupId", accountGroup1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteAccountGroup"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountGroup",
					new HashMap<String, Object>() {
						{
							put("accountGroupId", accountGroup1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		AccountGroup accountGroup2 =
			testGraphQLDeleteAccountGroup_addAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountGroup",
							new HashMap<String, Object>() {
								{
									put(
										"accountGroupId",
										accountGroup2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountGroup"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"accountGroup",
						new HashMap<String, Object>() {
							{
								put("accountGroupId", accountGroup2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected AccountGroup testGraphQLDeleteAccountGroup_addAccountGroup()
		throws Exception {

		return testGraphQLAccountGroup_addAccountGroup();
	}

	@Test
	public void testDeleteAccountGroupBatch() throws Exception {
		AccountGroup accountGroup1 =
			testDeleteAccountGroupBatch_addAccountGroup();

		testDeleteAccountGroupBatch_deleteAccountGroup(
			202, accountGroup1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup1.getId()));

		accountGroup1 = testDeleteAccountGroupBatch_addAccountGroup();

		testDeleteAccountGroupBatch_deleteAccountGroup(
			202, null, accountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup1.getId()));

		accountGroup1 = testDeleteAccountGroupBatch_addAccountGroup();
		AccountGroup accountGroup2 =
			testDeleteAccountGroupBatch_addAccountGroup();

		testDeleteAccountGroupBatch_deleteAccountGroup(
			202, accountGroup2.getExternalReferenceCode(),
			accountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup1.getId()));
		assertHttpResponseStatusCode(
			200,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup2.getId()));

		testDeleteAccountGroupBatch_deleteAccountGroup(
			202, accountGroup2.getExternalReferenceCode(),
			accountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup2.getId()));
	}

	protected AccountGroup testDeleteAccountGroupBatch_addAccountGroup()
		throws Exception {

		return testDeleteAccountGroup_addAccountGroup();
	}

	protected void testDeleteAccountGroupBatch_deleteAccountGroup(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			accountGroupResource.deleteAccountGroupBatchHttpResponse(
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
	public void testDeleteAccountGroupByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup accountGroup =
			testDeleteAccountGroupByExternalReferenceCode_addAccountGroup();

		assertHttpResponseStatusCode(
			204,
			accountGroupResource.
				deleteAccountGroupByExternalReferenceCodeHttpResponse(
					accountGroup.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.
				getAccountGroupByExternalReferenceCodeHttpResponse(
					accountGroup.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			accountGroupResource.
				getAccountGroupByExternalReferenceCodeHttpResponse("-"));
	}

	protected AccountGroup
			testDeleteAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountGroupByExternalReferenceCode()
		throws Exception {

		// No namespace

		AccountGroup accountGroup1 =
			testGraphQLDeleteAccountGroupByExternalReferenceCode_addAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										accountGroup1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountGroupByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountGroupByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									accountGroup1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		AccountGroup accountGroup2 =
			testGraphQLDeleteAccountGroupByExternalReferenceCode_addAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountGroupByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											accountGroup2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountGroupByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"accountGroupByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										accountGroup2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected AccountGroup
			testGraphQLDeleteAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return testGraphQLAccountGroup_addAccountGroup();
	}

	@Test
	public void testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup accountGroup =
			testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup();

		assertHttpResponseStatusCode(
			204,
			accountGroupResource.
				deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					accountGroup.getExternalReferenceCode()));
	}

	protected AccountGroup
			testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		// No namespace

		AccountGroup accountGroup1 =
			testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"accountExternalReferenceCode",
									"\"" +
										testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										accountGroup1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode"));

		// Using the namespace headlessAdminUser_v1_0

		AccountGroup accountGroup2 =
			testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"accountExternalReferenceCode",
										"\"" +
											testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											accountGroup2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode"));
	}

	protected String
			testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountGroup
			testGraphQLDeleteAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return testGraphQLAccountGroup_addAccountGroup();
	}

	@Test
	public void testGetAccountAccountGroupsPage() throws Exception {
		Long accountId = testGetAccountAccountGroupsPage_getAccountId();
		Long irrelevantAccountId =
			testGetAccountAccountGroupsPage_getIrrelevantAccountId();

		Page<AccountGroup> page =
			accountGroupResource.getAccountAccountGroupsPage(
				accountId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAccountId != null) {
			AccountGroup irrelevantAccountGroup =
				testGetAccountAccountGroupsPage_addAccountGroup(
					irrelevantAccountId, randomIrrelevantAccountGroup());

			page = accountGroupResource.getAccountAccountGroupsPage(
				irrelevantAccountId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountGroup, (List<AccountGroup>)page.getItems());
			assertValid(
				page,
				testGetAccountAccountGroupsPage_getExpectedActions(
					irrelevantAccountId));
		}

		AccountGroup accountGroup1 =
			testGetAccountAccountGroupsPage_addAccountGroup(
				accountId, randomAccountGroup());

		AccountGroup accountGroup2 =
			testGetAccountAccountGroupsPage_addAccountGroup(
				accountId, randomAccountGroup());

		page = accountGroupResource.getAccountAccountGroupsPage(
			accountId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountGroup1, (List<AccountGroup>)page.getItems());
		assertContains(accountGroup2, (List<AccountGroup>)page.getItems());
		assertValid(
			page,
			testGetAccountAccountGroupsPage_getExpectedActions(accountId));

		accountGroupResource.deleteAccountGroup(accountGroup1.getId());

		accountGroupResource.deleteAccountGroup(accountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountAccountGroupsPage_getExpectedActions(Long accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountAccountGroupsPageWithPagination()
		throws Exception {

		Long accountId = testGetAccountAccountGroupsPage_getAccountId();

		Page<AccountGroup> accountGroupsPage =
			accountGroupResource.getAccountAccountGroupsPage(accountId, null);

		int totalCount = GetterUtil.getInteger(
			accountGroupsPage.getTotalCount());

		AccountGroup accountGroup1 =
			testGetAccountAccountGroupsPage_addAccountGroup(
				accountId, randomAccountGroup());

		AccountGroup accountGroup2 =
			testGetAccountAccountGroupsPage_addAccountGroup(
				accountId, randomAccountGroup());

		AccountGroup accountGroup3 =
			testGetAccountAccountGroupsPage_addAccountGroup(
				accountId, randomAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountGroup> page1 =
				accountGroupResource.getAccountAccountGroupsPage(
					accountId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(accountGroup1, (List<AccountGroup>)page1.getItems());

			Page<AccountGroup> page2 =
				accountGroupResource.getAccountAccountGroupsPage(
					accountId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(accountGroup2, (List<AccountGroup>)page2.getItems());

			Page<AccountGroup> page3 =
				accountGroupResource.getAccountAccountGroupsPage(
					accountId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(accountGroup3, (List<AccountGroup>)page3.getItems());
		}
		else {
			Page<AccountGroup> page1 =
				accountGroupResource.getAccountAccountGroupsPage(
					accountId, Pagination.of(1, totalCount + 2));

			List<AccountGroup> accountGroups1 =
				(List<AccountGroup>)page1.getItems();

			Assert.assertEquals(
				accountGroups1.toString(), totalCount + 2,
				accountGroups1.size());

			Page<AccountGroup> page2 =
				accountGroupResource.getAccountAccountGroupsPage(
					accountId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountGroup> accountGroups2 =
				(List<AccountGroup>)page2.getItems();

			Assert.assertEquals(
				accountGroups2.toString(), 1, accountGroups2.size());

			Page<AccountGroup> page3 =
				accountGroupResource.getAccountAccountGroupsPage(
					accountId, Pagination.of(1, (int)totalCount + 3));

			assertContains(accountGroup1, (List<AccountGroup>)page3.getItems());
			assertContains(accountGroup2, (List<AccountGroup>)page3.getItems());
			assertContains(accountGroup3, (List<AccountGroup>)page3.getItems());
		}
	}

	protected AccountGroup testGetAccountAccountGroupsPage_addAccountGroup(
			Long accountId, AccountGroup accountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountAccountGroupsPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountAccountGroupsPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage()
		throws Exception {

		String accountExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getAccountExternalReferenceCode();
		String irrelevantAccountExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getIrrelevantAccountExternalReferenceCode();

		Page<AccountGroup> page =
			accountGroupResource.
				getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
					accountExternalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantAccountExternalReferenceCode != null) {
			AccountGroup irrelevantAccountGroup =
				testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
					irrelevantAccountExternalReferenceCode,
					randomIrrelevantAccountGroup());

			page =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						irrelevantAccountExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountGroup, (List<AccountGroup>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getExpectedActions(
					irrelevantAccountExternalReferenceCode));
		}

		AccountGroup accountGroup1 =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				accountExternalReferenceCode, randomAccountGroup());

		AccountGroup accountGroup2 =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				accountExternalReferenceCode, randomAccountGroup());

		page =
			accountGroupResource.
				getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
					accountExternalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountGroup1, (List<AccountGroup>)page.getItems());
		assertContains(accountGroup2, (List<AccountGroup>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getExpectedActions(
				accountExternalReferenceCode));

		accountGroupResource.deleteAccountGroup(accountGroup1.getId());

		accountGroupResource.deleteAccountGroup(accountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getExpectedActions(
				String accountExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPageWithPagination()
		throws Exception {

		String accountExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getAccountExternalReferenceCode();

		Page<AccountGroup> accountGroupsPage =
			accountGroupResource.
				getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
					accountExternalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			accountGroupsPage.getTotalCount());

		AccountGroup accountGroup1 =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				accountExternalReferenceCode, randomAccountGroup());

		AccountGroup accountGroup2 =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				accountExternalReferenceCode, randomAccountGroup());

		AccountGroup accountGroup3 =
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				accountExternalReferenceCode, randomAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountGroup> page1 =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(accountGroup1, (List<AccountGroup>)page1.getItems());

			Page<AccountGroup> page2 =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(accountGroup2, (List<AccountGroup>)page2.getItems());

			Page<AccountGroup> page3 =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(accountGroup3, (List<AccountGroup>)page3.getItems());
		}
		else {
			Page<AccountGroup> page1 =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<AccountGroup> accountGroups1 =
				(List<AccountGroup>)page1.getItems();

			Assert.assertEquals(
				accountGroups1.toString(), totalCount + 2,
				accountGroups1.size());

			Page<AccountGroup> page2 =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountGroup> accountGroups2 =
				(List<AccountGroup>)page2.getItems();

			Assert.assertEquals(
				accountGroups2.toString(), 1, accountGroups2.size());

			Page<AccountGroup> page3 =
				accountGroupResource.
					getAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage(
						accountExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(accountGroup1, (List<AccountGroup>)page3.getItems());
			assertContains(accountGroup2, (List<AccountGroup>)page3.getItems());
			assertContains(accountGroup3, (List<AccountGroup>)page3.getItems());
		}
	}

	protected AccountGroup
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_addAccountGroup(
				String accountExternalReferenceCode, AccountGroup accountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountExternalReferenceCodeAccountGroupsPage_getIrrelevantAccountExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountGroup() throws Exception {
		AccountGroup postAccountGroup = testGetAccountGroup_addAccountGroup();

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			postAccountGroup.getId());

		assertEquals(postAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		AccountGroup postAccountGroup = testGetAccountGroup_addAccountGroup();

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			postAccountGroup.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.user.dto.v1_0.AccountGroup"
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

		Object item = vulcanCRUDItemDelegate.getItem(postAccountGroup.getId());

		assertEquals(
			getAccountGroup, AccountGroupSerDes.toDTO(item.toString()));
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

	protected AccountGroup testGetAccountGroup_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountGroup() throws Exception {
		AccountGroup accountGroup =
			testGraphQLGetAccountGroup_addAccountGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				accountGroup,
				AccountGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountGroup",
								new HashMap<String, Object>() {
									{
										put(
											"accountGroupId",
											accountGroup.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/accountGroup"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				accountGroup,
				AccountGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"accountGroup",
									new HashMap<String, Object>() {
										{
											put(
												"accountGroupId",
												accountGroup.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/accountGroup"))));
	}

	@Test
	public void testGraphQLGetAccountGroupNotFound() throws Exception {
		Long irrelevantAccountGroupId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountGroup",
						new HashMap<String, Object>() {
							{
								put("accountGroupId", irrelevantAccountGroupId);
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
							"accountGroup",
							new HashMap<String, Object>() {
								{
									put(
										"accountGroupId",
										irrelevantAccountGroupId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected AccountGroup testGraphQLGetAccountGroup_addAccountGroup()
		throws Exception {

		return testGraphQLAccountGroup_addAccountGroup();
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCode() throws Exception {
		AccountGroup postAccountGroup =
			testGetAccountGroupByExternalReferenceCode_addAccountGroup();

		AccountGroup getAccountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				postAccountGroup.getExternalReferenceCode());

		assertEquals(postAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	protected AccountGroup
			testGetAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountGroupByExternalReferenceCode()
		throws Exception {

		AccountGroup accountGroup =
			testGraphQLGetAccountGroupByExternalReferenceCode_addAccountGroup();

		// No namespace

		Assert.assertTrue(
			equals(
				accountGroup,
				AccountGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountGroupByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												accountGroup.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountGroupByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				accountGroup,
				AccountGroupSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"accountGroupByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													accountGroup.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/accountGroupByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetAccountGroupByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountGroupByExternalReferenceCode",
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
							"accountGroupByExternalReferenceCode",
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

	protected AccountGroup
			testGraphQLGetAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		return testGraphQLAccountGroup_addAccountGroup();
	}

	@Test
	public void testGetAccountGroupsPage() throws Exception {
		Page<AccountGroup> page = accountGroupResource.getAccountGroupsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		AccountGroup accountGroup1 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		AccountGroup accountGroup2 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		page = accountGroupResource.getAccountGroupsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountGroup1, (List<AccountGroup>)page.getItems());
		assertContains(accountGroup2, (List<AccountGroup>)page.getItems());
		assertValid(page, testGetAccountGroupsPage_getExpectedActions());

		accountGroupResource.deleteAccountGroup(accountGroup1.getId());

		accountGroupResource.deleteAccountGroup(accountGroup2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountGroupsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountGroupsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		AccountGroup accountGroup1 = randomAccountGroup();

		accountGroup1 = testGetAccountGroupsPage_addAccountGroup(accountGroup1);

		for (EntityField entityField : entityFields) {
			Page<AccountGroup> page = accountGroupResource.getAccountGroupsPage(
				null, getFilterString(entityField, "between", accountGroup1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountGroup1),
				(List<AccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetAccountGroupsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountGroupsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountGroupsPageWithFilterStringContains()
		throws Exception {

		testGetAccountGroupsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountGroupsPageWithFilterStringEquals()
		throws Exception {

		testGetAccountGroupsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountGroupsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountGroupsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAccountGroupsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AccountGroup accountGroup1 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup accountGroup2 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		for (EntityField entityField : entityFields) {
			Page<AccountGroup> page = accountGroupResource.getAccountGroupsPage(
				null, getFilterString(entityField, operator, accountGroup1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountGroup1),
				(List<AccountGroup>)page.getItems());
		}
	}

	@Test
	public void testGetAccountGroupsPageWithPagination() throws Exception {
		Page<AccountGroup> accountGroupsPage =
			accountGroupResource.getAccountGroupsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			accountGroupsPage.getTotalCount());

		AccountGroup accountGroup1 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		AccountGroup accountGroup2 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		AccountGroup accountGroup3 = testGetAccountGroupsPage_addAccountGroup(
			randomAccountGroup());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountGroup> page1 =
				accountGroupResource.getAccountGroupsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(accountGroup1, (List<AccountGroup>)page1.getItems());

			Page<AccountGroup> page2 =
				accountGroupResource.getAccountGroupsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(accountGroup2, (List<AccountGroup>)page2.getItems());

			Page<AccountGroup> page3 =
				accountGroupResource.getAccountGroupsPage(
					null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(accountGroup3, (List<AccountGroup>)page3.getItems());
		}
		else {
			Page<AccountGroup> page1 =
				accountGroupResource.getAccountGroupsPage(
					null, null, Pagination.of(1, totalCount + 2), null);

			List<AccountGroup> accountGroups1 =
				(List<AccountGroup>)page1.getItems();

			Assert.assertEquals(
				accountGroups1.toString(), totalCount + 2,
				accountGroups1.size());

			Page<AccountGroup> page2 =
				accountGroupResource.getAccountGroupsPage(
					null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountGroup> accountGroups2 =
				(List<AccountGroup>)page2.getItems();

			Assert.assertEquals(
				accountGroups2.toString(), 1, accountGroups2.size());

			Page<AccountGroup> page3 =
				accountGroupResource.getAccountGroupsPage(
					null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(accountGroup1, (List<AccountGroup>)page3.getItems());
			assertContains(accountGroup2, (List<AccountGroup>)page3.getItems());
			assertContains(accountGroup3, (List<AccountGroup>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountGroupsPageWithSortDateTime() throws Exception {
		testGetAccountGroupsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, accountGroup1, accountGroup2) -> {
				BeanTestUtil.setProperty(
					accountGroup1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountGroupsPageWithSortDouble() throws Exception {
		testGetAccountGroupsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, accountGroup1, accountGroup2) -> {
				BeanTestUtil.setProperty(
					accountGroup1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					accountGroup2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountGroupsPageWithSortInteger() throws Exception {
		testGetAccountGroupsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, accountGroup1, accountGroup2) -> {
				BeanTestUtil.setProperty(
					accountGroup1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					accountGroup2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountGroupsPageWithSortString() throws Exception {
		testGetAccountGroupsPageWithSort(
			EntityField.Type.STRING,
			(entityField, accountGroup1, accountGroup2) -> {
				Class<?> clazz = accountGroup1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						accountGroup1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						accountGroup2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						accountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						accountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						accountGroup1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						accountGroup2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAccountGroupsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, AccountGroup, AccountGroup, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		AccountGroup accountGroup1 = randomAccountGroup();
		AccountGroup accountGroup2 = randomAccountGroup();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, accountGroup1, accountGroup2);
		}

		accountGroup1 = testGetAccountGroupsPage_addAccountGroup(accountGroup1);

		accountGroup2 = testGetAccountGroupsPage_addAccountGroup(accountGroup2);

		Page<AccountGroup> page = accountGroupResource.getAccountGroupsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AccountGroup> ascPage =
				accountGroupResource.getAccountGroupsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				accountGroup1, (List<AccountGroup>)ascPage.getItems());
			assertContains(
				accountGroup2, (List<AccountGroup>)ascPage.getItems());

			Page<AccountGroup> descPage =
				accountGroupResource.getAccountGroupsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				accountGroup2, (List<AccountGroup>)descPage.getItems());
			assertContains(
				accountGroup1, (List<AccountGroup>)descPage.getItems());
		}
	}

	protected AccountGroup testGetAccountGroupsPage_addAccountGroup(
			AccountGroup accountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountGroupsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"accountGroups",
			new HashMap<String, Object>() {
				{
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject accountGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accountGroups");

		long totalCount = accountGroupsJSONObject.getLong("totalCount");

		AccountGroup accountGroup1 = testGraphQLAccountGroup_addAccountGroup(
			randomAccountGroup());

		AccountGroup accountGroup2 = testGraphQLAccountGroup_addAccountGroup(
			randomAccountGroup());

		accountGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accountGroups");

		Assert.assertEquals(
			totalCount + 2, accountGroupsJSONObject.getLong("totalCount"));

		assertContains(
			accountGroup1,
			Arrays.asList(
				AccountGroupSerDes.toDTOs(
					accountGroupsJSONObject.getString("items"))));
		assertContains(
			accountGroup2,
			Arrays.asList(
				AccountGroupSerDes.toDTOs(
					accountGroupsJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		accountGroupsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/accountGroups");

		Assert.assertEquals(
			totalCount + 2, accountGroupsJSONObject.getLong("totalCount"));

		assertContains(
			accountGroup1,
			Arrays.asList(
				AccountGroupSerDes.toDTOs(
					accountGroupsJSONObject.getString("items"))));
		assertContains(
			accountGroup2,
			Arrays.asList(
				AccountGroupSerDes.toDTOs(
					accountGroupsJSONObject.getString("items"))));
	}

	@Test
	public void testPatchAccountGroup() throws Exception {
		AccountGroup postAccountGroup = testPatchAccountGroup_addAccountGroup();

		AccountGroup randomPatchAccountGroup = randomPatchAccountGroup();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup patchAccountGroup = accountGroupResource.patchAccountGroup(
			postAccountGroup.getId(), randomPatchAccountGroup);

		AccountGroup expectedPatchAccountGroup = postAccountGroup.clone();

		BeanTestUtil.copyProperties(
			randomPatchAccountGroup, expectedPatchAccountGroup);

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			patchAccountGroup.getId());

		assertEquals(expectedPatchAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	protected AccountGroup testPatchAccountGroup_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAccountGroupByExternalReferenceCode()
		throws Exception {

		AccountGroup postAccountGroup =
			testPatchAccountGroupByExternalReferenceCode_addAccountGroup();

		AccountGroup randomPatchAccountGroup = randomPatchAccountGroup();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup patchAccountGroup =
			accountGroupResource.patchAccountGroupByExternalReferenceCode(
				postAccountGroup.getExternalReferenceCode(),
				randomPatchAccountGroup);

		AccountGroup expectedPatchAccountGroup = postAccountGroup.clone();

		BeanTestUtil.copyProperties(
			randomPatchAccountGroup, expectedPatchAccountGroup);

		AccountGroup getAccountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				patchAccountGroup.getExternalReferenceCode());

		assertEquals(expectedPatchAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	protected AccountGroup
			testPatchAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountGroup() throws Exception {
		AccountGroup randomAccountGroup = randomAccountGroup();

		AccountGroup postAccountGroup = testPostAccountGroup_addAccountGroup(
			randomAccountGroup);

		assertEquals(randomAccountGroup, postAccountGroup);
		assertValid(postAccountGroup);
	}

	protected AccountGroup testPostAccountGroup_addAccountGroup(
			AccountGroup accountGroup)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostAccountGroup() throws Exception {
		AccountGroup randomAccountGroup = randomAccountGroup();

		AccountGroup accountGroup = testGraphQLAccountGroup_addAccountGroup(
			randomAccountGroup);

		Assert.assertTrue(equals(randomAccountGroup, accountGroup));
	}

	@Test
	public void testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountGroup accountGroup =
			testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup();

		assertHttpResponseStatusCode(
			204,
			accountGroupResource.
				postAccountGroupByExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					accountGroup.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.
				postAccountGroupByExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					"-"));
	}

	protected String
			testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountGroup
			testPostAccountGroupByExternalReferenceCodeAccountByExternalReferenceCode_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAccountGroup() throws Exception {
		AccountGroup postAccountGroup = testPutAccountGroup_addAccountGroup();

		AccountGroup randomAccountGroup = randomAccountGroup();

		AccountGroup putAccountGroup = accountGroupResource.putAccountGroup(
			postAccountGroup.getId(), randomAccountGroup);

		assertEquals(randomAccountGroup, putAccountGroup);
		assertValid(putAccountGroup);

		AccountGroup getAccountGroup = accountGroupResource.getAccountGroup(
			putAccountGroup.getId());

		assertEquals(randomAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);
	}

	protected AccountGroup testPutAccountGroup_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAccountGroupByExternalReferenceCode() throws Exception {
		AccountGroup postAccountGroup =
			testPutAccountGroupByExternalReferenceCode_addAccountGroup();

		AccountGroup randomAccountGroup = randomAccountGroup();

		AccountGroup putAccountGroup =
			accountGroupResource.putAccountGroupByExternalReferenceCode(
				postAccountGroup.getExternalReferenceCode(),
				randomAccountGroup);

		assertEquals(randomAccountGroup, putAccountGroup);
		assertValid(putAccountGroup);

		AccountGroup getAccountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				putAccountGroup.getExternalReferenceCode());

		assertEquals(randomAccountGroup, getAccountGroup);
		assertValid(getAccountGroup);

		AccountGroup newAccountGroup =
			testPutAccountGroupByExternalReferenceCode_createAccountGroup();

		putAccountGroup =
			accountGroupResource.putAccountGroupByExternalReferenceCode(
				newAccountGroup.getExternalReferenceCode(), newAccountGroup);

		assertEquals(newAccountGroup, putAccountGroup);
		assertValid(putAccountGroup);

		getAccountGroup =
			accountGroupResource.getAccountGroupByExternalReferenceCode(
				putAccountGroup.getExternalReferenceCode());

		assertEquals(newAccountGroup, getAccountGroup);

		Assert.assertEquals(
			newAccountGroup.getExternalReferenceCode(),
			putAccountGroup.getExternalReferenceCode());
	}

	protected AccountGroup
			testPutAccountGroupByExternalReferenceCode_addAccountGroup()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountGroup
			testPutAccountGroupByExternalReferenceCode_createAccountGroup()
		throws Exception {

		return randomAccountGroup();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		AccountGroup accountGroup1 =
			testBatchEngineDeleteImportTask_addAccountGroup();

		testBatchEngineDeleteImportTask_deleteAccountGroup(
			200, accountGroup1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup1.getId()));

		accountGroup1 = testBatchEngineDeleteImportTask_addAccountGroup();

		testBatchEngineDeleteImportTask_deleteAccountGroup(
			200, null, accountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup1.getId()));

		accountGroup1 = testBatchEngineDeleteImportTask_addAccountGroup();
		AccountGroup accountGroup2 =
			testBatchEngineDeleteImportTask_addAccountGroup();

		testBatchEngineDeleteImportTask_deleteAccountGroup(
			200, accountGroup2.getExternalReferenceCode(),
			accountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup1.getId()));
		assertHttpResponseStatusCode(
			200,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup2.getId()));

		testBatchEngineDeleteImportTask_deleteAccountGroup(
			200, accountGroup2.getExternalReferenceCode(),
			accountGroup1.getId());

		assertHttpResponseStatusCode(
			404,
			accountGroupResource.getAccountGroupHttpResponse(
				accountGroup2.getId()));
	}

	protected AccountGroup testBatchEngineDeleteImportTask_addAccountGroup()
		throws Exception {

		return testDeleteAccountGroup_addAccountGroup();
	}

	protected void testBatchEngineDeleteImportTask_deleteAccountGroup(
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
				"com.liferay.headless.admin.user.dto.v1_0.AccountGroup", null,
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

	protected AccountGroup testGraphQLAccountGroup_addAccountGroup()
		throws Exception {

		return testGraphQLAccountGroup_addAccountGroup(randomAccountGroup());
	}

	protected AccountGroup testGraphQLAccountGroup_addAccountGroup(
			AccountGroup accountGroup)
		throws Exception {

		JSONDeserializer<AccountGroup> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(AccountGroup.class)) {

			if (getGraphQLValue(field.get(accountGroup)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(accountGroup)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAccountGroup",
						new HashMap<String, Object>() {
							{
								put("accountGroup", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createAccountGroup"),
			AccountGroup.class);
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
		AccountGroup accountGroup, List<AccountGroup> accountGroups) {

		boolean contains = false;

		for (AccountGroup item : accountGroups) {
			if (equals(accountGroup, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountGroups + " does not contain " + accountGroup, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountGroup accountGroup1, AccountGroup accountGroup2) {

		Assert.assertTrue(
			accountGroup1 + " does not equal " + accountGroup2,
			equals(accountGroup1, accountGroup2));
	}

	protected void assertEquals(
		List<AccountGroup> accountGroups1, List<AccountGroup> accountGroups2) {

		Assert.assertEquals(accountGroups1.size(), accountGroups2.size());

		for (int i = 0; i < accountGroups1.size(); i++) {
			AccountGroup accountGroup1 = accountGroups1.get(i);
			AccountGroup accountGroup2 = accountGroups2.get(i);

			assertEquals(accountGroup1, accountGroup2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountGroup> accountGroups1, List<AccountGroup> accountGroups2) {

		Assert.assertEquals(accountGroups1.size(), accountGroups2.size());

		for (AccountGroup accountGroup1 : accountGroups1) {
			boolean contains = false;

			for (AccountGroup accountGroup2 : accountGroups2) {
				if (equals(accountGroup1, accountGroup2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountGroups2 + " does not contain " + accountGroup1,
				contains);
		}
	}

	protected void assertValid(AccountGroup accountGroup) throws Exception {
		boolean valid = true;

		if (accountGroup.getDateCreated() == null) {
			valid = false;
		}

		if (accountGroup.getDateModified() == null) {
			valid = false;
		}

		if (accountGroup.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountBriefs", additionalAssertFieldName)) {
				if (accountGroup.getAccountBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (accountGroup.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (accountGroup.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (accountGroup.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (accountGroup.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (accountGroup.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountGroup.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (accountGroup.getPermissions() == null) {
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

	protected void assertValid(Page<AccountGroup> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountGroup> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountGroup> accountGroups = page.getItems();

		int size = accountGroups.size();

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
					com.liferay.headless.admin.user.dto.v1_0.AccountGroup.
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
		AccountGroup accountGroup1, AccountGroup accountGroup2) {

		if (accountGroup1 == accountGroup2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getAccountBriefs(),
						accountGroup2.getAccountBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)accountGroup1.getActions(),
						(Map)accountGroup2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getCreator(),
						accountGroup2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getCustomFields(),
						accountGroup2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getDateCreated(),
						accountGroup2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getDateModified(),
						accountGroup2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getDescription(),
						accountGroup2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountGroup1.getExternalReferenceCode(),
						accountGroup2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getId(), accountGroup2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getName(), accountGroup2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountGroup1.getPermissions(),
						accountGroup2.getPermissions())) {

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

		if (!(_accountGroupResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountGroupResource;

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
		EntityField entityField, String operator, AccountGroup accountGroup) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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
				Date date = accountGroup.getDateCreated();

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

				sb.append(_format.format(accountGroup.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = accountGroup.getDateModified();

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

				sb.append(_format.format(accountGroup.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("description")) {
			Object object = accountGroup.getDescription();

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
			Object object = accountGroup.getExternalReferenceCode();

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
			Object object = accountGroup.getName();

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

		if (entityFieldName.equals("permissions")) {
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

	protected AccountGroup randomAccountGroup() throws Exception {
		return new AccountGroup() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected AccountGroup randomIrrelevantAccountGroup() throws Exception {
		AccountGroup randomIrrelevantAccountGroup = randomAccountGroup();

		return randomIrrelevantAccountGroup;
	}

	protected AccountGroup randomPatchAccountGroup() throws Exception {
		return randomAccountGroup();
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

	protected AccountGroupResource accountGroupResource;
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
		LogFactoryUtil.getLog(BaseAccountGroupResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.AccountGroupResource
		_accountGroupResource;

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