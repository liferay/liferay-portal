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

import com.liferay.headless.admin.user.client.dto.v1_0.Account;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.AccountSerDes;
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
public abstract class BaseAccountResourceTestCase {

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

		_accountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountResource = AccountResource.builder(
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

		Account account1 = randomAccount();

		String json = objectMapper.writeValueAsString(account1);

		Account account2 = AccountSerDes.toDTO(json);

		Assert.assertTrue(equals(account1, account2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Account account = randomAccount();

		String json1 = objectMapper.writeValueAsString(account);
		String json2 = AccountSerDes.toJSON(account);

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

		Account account = randomAccount();

		account.setDefaultBillingAddressExternalReferenceCode(regex);
		account.setDefaultShippingAddressExternalReferenceCode(regex);
		account.setDescription(regex);
		account.setExternalReferenceCode(regex);
		account.setLogoBase64(regex);
		account.setLogoExternalReferenceCode(regex);
		account.setLogoURL(regex);
		account.setName(regex);
		account.setParentAccountExternalReferenceCode(regex);
		account.setTaxId(regex);

		String json = AccountSerDes.toJSON(account);

		Assert.assertFalse(json.contains(regex));

		account = AccountSerDes.toDTO(json);

		Assert.assertEquals(
			regex, account.getDefaultBillingAddressExternalReferenceCode());
		Assert.assertEquals(
			regex, account.getDefaultShippingAddressExternalReferenceCode());
		Assert.assertEquals(regex, account.getDescription());
		Assert.assertEquals(regex, account.getExternalReferenceCode());
		Assert.assertEquals(regex, account.getLogoBase64());
		Assert.assertEquals(regex, account.getLogoExternalReferenceCode());
		Assert.assertEquals(regex, account.getLogoURL());
		Assert.assertEquals(regex, account.getName());
		Assert.assertEquals(
			regex, account.getParentAccountExternalReferenceCode());
		Assert.assertEquals(regex, account.getTaxId());
	}

	@Test
	public void testDeleteAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account = testDeleteAccount_addAccount();

		assertHttpResponseStatusCode(
			204, accountResource.deleteAccountHttpResponse(account.getId()));

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account.getId()));
		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(0L));
	}

	protected Account testDeleteAccount_addAccount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccount() throws Exception {

		// No namespace

		Account account1 = testGraphQLDeleteAccount_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccount",
						new HashMap<String, Object>() {
							{
								put("accountId", account1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteAccount"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"account",
					new HashMap<String, Object>() {
						{
							put("accountId", account1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		Account account2 = testGraphQLDeleteAccount_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccount",
							new HashMap<String, Object>() {
								{
									put("accountId", account2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccount"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"account",
						new HashMap<String, Object>() {
							{
								put("accountId", account2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Account testGraphQLDeleteAccount_addAccount() throws Exception {
		return testGraphQLAccount_addAccount();
	}

	@Test
	public void testDeleteAccountBatch() throws Exception {
		Account account1 = testDeleteAccountBatch_addAccount();

		testDeleteAccountBatch_deleteAccount(
			202, account1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account1.getId()));

		account1 = testDeleteAccountBatch_addAccount();

		testDeleteAccountBatch_deleteAccount(202, null, account1.getId());

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account1.getId()));

		account1 = testDeleteAccountBatch_addAccount();
		Account account2 = testDeleteAccountBatch_addAccount();

		testDeleteAccountBatch_deleteAccount(
			202, account2.getExternalReferenceCode(), account1.getId());

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account1.getId()));
		assertHttpResponseStatusCode(
			200, accountResource.getAccountHttpResponse(account2.getId()));

		testDeleteAccountBatch_deleteAccount(
			202, account2.getExternalReferenceCode(), account1.getId());

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account2.getId()));
	}

	protected Account testDeleteAccountBatch_addAccount() throws Exception {
		return testDeleteAccount_addAccount();
	}

	protected void testDeleteAccountBatch_deleteAccount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			accountResource.deleteAccountBatchHttpResponse(
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
	public void testDeleteAccountByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account = testDeleteAccountByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.deleteAccountByExternalReferenceCodeHttpResponse(
				account.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountResource.getAccountByExternalReferenceCodeHttpResponse(
				account.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			accountResource.getAccountByExternalReferenceCodeHttpResponse("-"));
	}

	protected Account testDeleteAccountByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountByExternalReferenceCode()
		throws Exception {

		// No namespace

		Account account1 =
			testGraphQLDeleteAccountByExternalReferenceCode_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + account1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + account1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		Account account2 =
			testGraphQLDeleteAccountByExternalReferenceCode_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											account2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"accountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + account2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Account
			testGraphQLDeleteAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return testGraphQLAccount_addAccount();
	}

	@Test
	public void testDeleteOrganizationAccounts() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account = testDeleteOrganizationAccounts_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.deleteOrganizationAccountsHttpResponse(
				testDeleteOrganizationAccounts_getOrganizationId(), null));
	}

	protected Account testDeleteOrganizationAccounts_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteOrganizationAccounts_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrganizationAccounts() throws Exception {

		// No namespace

		Account account1 = testGraphQLDeleteOrganizationAccounts_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrganizationAccounts",
						new HashMap<String, Object>() {
							{
								put(
									"organizationId",
									testGraphQLDeleteOrganizationAccounts_getOrganizationId());
							}
						})),
				"JSONObject/data", "Object/deleteOrganizationAccounts"));

		// Using the namespace headlessAdminUser_v1_0

		Account account2 = testGraphQLDeleteOrganizationAccounts_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteOrganizationAccounts",
							new HashMap<String, Object>() {
								{
									put(
										"organizationId",
										testGraphQLDeleteOrganizationAccounts_getOrganizationId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteOrganizationAccounts"));
	}

	protected Long testGraphQLDeleteOrganizationAccounts_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account testGraphQLDeleteOrganizationAccounts_addAccount()
		throws Exception {

		return testGraphQLOrganizationAccount_addAccount();
	}

	@Test
	public void testDeleteOrganizationAccountsByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testDeleteOrganizationAccountsByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				deleteOrganizationAccountsByExternalReferenceCodeHttpResponse(
					testDeleteOrganizationAccountsByExternalReferenceCode_getOrganizationId(),
					null));
	}

	protected Account
			testDeleteOrganizationAccountsByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteOrganizationAccountsByExternalReferenceCode_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrganizationAccountsByExternalReferenceCode()
		throws Exception {

		// No namespace

		Account account1 =
			testGraphQLDeleteOrganizationAccountsByExternalReferenceCode_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrganizationAccountsByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"organizationId",
									testGraphQLDeleteOrganizationAccountsByExternalReferenceCode_getOrganizationId());
							}
						})),
				"JSONObject/data",
				"Object/deleteOrganizationAccountsByExternalReferenceCode"));

		// Using the namespace headlessAdminUser_v1_0

		Account account2 =
			testGraphQLDeleteOrganizationAccountsByExternalReferenceCode_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteOrganizationAccountsByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"organizationId",
										testGraphQLDeleteOrganizationAccountsByExternalReferenceCode_getOrganizationId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteOrganizationAccountsByExternalReferenceCode"));
	}

	protected Long
			testGraphQLDeleteOrganizationAccountsByExternalReferenceCode_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testGraphQLDeleteOrganizationAccountsByExternalReferenceCode_addAccount()
		throws Exception {

		return testGraphQLAccount_addAccount();
	}

	@Test
	public void testDeleteOrganizationByExternalReferenceCodeAccounts()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testDeleteOrganizationByExternalReferenceCodeAccounts_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				deleteOrganizationByExternalReferenceCodeAccountsHttpResponse(
					testDeleteOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
						account),
					null));
	}

	protected Account
			testDeleteOrganizationByExternalReferenceCodeAccounts_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
				Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts()
		throws Exception {

		// No namespace

		Account account1 =
			testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrganizationByExternalReferenceCodeAccounts",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
											account1) + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrganizationByExternalReferenceCodeAccounts"));

		// Using the namespace headlessAdminUser_v1_0

		Account account2 =
			testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteOrganizationByExternalReferenceCodeAccounts",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
												account2) + "\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteOrganizationByExternalReferenceCodeAccounts"));
	}

	protected String
			testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
				Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testGraphQLDeleteOrganizationByExternalReferenceCodeAccounts_addAccount()
		throws Exception {

		return testGraphQLOrganizationAccount_addAccount();
	}

	@Test
	public void testDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					testDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode(),
					null));
	}

	protected Account
			testDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		// No namespace

		Account account1 =
			testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"organizationExternalReferenceCode",
									"\"" +
										testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode"));

		// Using the namespace headlessAdminUser_v1_0

		Account account2 =
			testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"organizationExternalReferenceCode",
										"\"" +
											testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode"));
	}

	protected String
			testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testGraphQLDeleteOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return testGraphQLAccount_addAccount();
	}

	@Test
	public void testGetAccount() throws Exception {
		Account postAccount = testGetAccount_addAccount();

		Account getAccount = accountResource.getAccount(postAccount.getId());

		assertEquals(postAccount, getAccount);
		assertValid(getAccount);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Account postAccount = testGetAccount_addAccount();

		Account getAccount = accountResource.getAccount(postAccount.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.admin.user.dto.v1_0.Account"
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

		Object item = vulcanCRUDItemDelegate.getItem(postAccount.getId());

		assertEquals(getAccount, AccountSerDes.toDTO(item.toString()));
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

	protected Account testGetAccount_addAccount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccount() throws Exception {
		Account account = testGraphQLGetAccount_addAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				account,
				AccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"account",
								new HashMap<String, Object>() {
									{
										put("accountId", account.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/account"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				account,
				AccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"account",
									new HashMap<String, Object>() {
										{
											put("accountId", account.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/account"))));
	}

	@Test
	public void testGraphQLGetAccountNotFound() throws Exception {
		Long irrelevantAccountId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"account",
						new HashMap<String, Object>() {
							{
								put("accountId", irrelevantAccountId);
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
							"account",
							new HashMap<String, Object>() {
								{
									put("accountId", irrelevantAccountId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Account testGraphQLGetAccount_addAccount() throws Exception {
		return testGraphQLAccount_addAccount();
	}

	@Test
	public void testGetAccountByExternalReferenceCode() throws Exception {
		Account postAccount =
			testGetAccountByExternalReferenceCode_addAccount();

		Account getAccount = accountResource.getAccountByExternalReferenceCode(
			postAccount.getExternalReferenceCode());

		assertEquals(postAccount, getAccount);
		assertValid(getAccount);
	}

	protected Account testGetAccountByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCode()
		throws Exception {

		Account account =
			testGraphQLGetAccountByExternalReferenceCode_addAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				account,
				AccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												account.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				account,
				AccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"accountByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													account.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/accountByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountByExternalReferenceCode",
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
							"accountByExternalReferenceCode",
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

	protected Account testGraphQLGetAccountByExternalReferenceCode_addAccount()
		throws Exception {

		return testGraphQLAccount_addAccount();
	}

	@Test
	public void testGetAccountGroupAccountsPage() throws Exception {
		Long accountGroupId =
			testGetAccountGroupAccountsPage_getAccountGroupId();
		Long irrelevantAccountGroupId =
			testGetAccountGroupAccountsPage_getIrrelevantAccountGroupId();

		Page<Account> page = accountResource.getAccountGroupAccountsPage(
			accountGroupId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountGroupId != null) {
			Account irrelevantAccount =
				testGetAccountGroupAccountsPage_addAccount(
					irrelevantAccountGroupId, randomIrrelevantAccount());

			page = accountResource.getAccountGroupAccountsPage(
				irrelevantAccountGroupId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantAccount, (List<Account>)page.getItems());
			assertValid(
				page,
				testGetAccountGroupAccountsPage_getExpectedActions(
					irrelevantAccountGroupId));
		}

		Account account1 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		Account account2 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		page = accountResource.getAccountGroupAccountsPage(
			accountGroupId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(account1, (List<Account>)page.getItems());
		assertContains(account2, (List<Account>)page.getItems());
		assertValid(
			page,
			testGetAccountGroupAccountsPage_getExpectedActions(accountGroupId));

		accountResource.deleteAccount(account1.getId());

		accountResource.deleteAccount(account2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountGroupAccountsPage_getExpectedActions(
				Long accountGroupId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountGroupAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountGroupId =
			testGetAccountGroupAccountsPage_getAccountGroupId();

		Account account1 = randomAccount();

		account1 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, account1);

		for (EntityField entityField : entityFields) {
			Page<Account> page = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null,
				getFilterString(entityField, "between", account1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetAccountGroupAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountGroupAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountGroupAccountsPageWithFilterStringContains()
		throws Exception {

		testGetAccountGroupAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountGroupAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetAccountGroupAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountGroupAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountGroupAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAccountGroupAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountGroupId =
			testGetAccountGroupAccountsPage_getAccountGroupId();

		Account account1 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account2 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		for (EntityField entityField : entityFields) {
			Page<Account> page = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null,
				getFilterString(entityField, operator, account1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetAccountGroupAccountsPageWithPagination()
		throws Exception {

		Long accountGroupId =
			testGetAccountGroupAccountsPage_getAccountGroupId();

		Page<Account> accountsPage =
			accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(accountsPage.getTotalCount());

		Account account1 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		Account account2 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		Account account3 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, randomAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Account> page1 = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(account1, (List<Account>)page1.getItems());

			Page<Account> page2 = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(account2, (List<Account>)page2.getItems());

			Page<Account> page3 = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(account3, (List<Account>)page3.getItems());
		}
		else {
			Page<Account> page1 = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<Account> accounts1 = (List<Account>)page1.getItems();

			Assert.assertEquals(
				accounts1.toString(), totalCount + 2, accounts1.size());

			Page<Account> page2 = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Account> accounts2 = (List<Account>)page2.getItems();

			Assert.assertEquals(accounts2.toString(), 1, accounts2.size());

			Page<Account> page3 = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(account1, (List<Account>)page3.getItems());
			assertContains(account2, (List<Account>)page3.getItems());
			assertContains(account3, (List<Account>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountGroupAccountsPageWithSortDateTime()
		throws Exception {

		testGetAccountGroupAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(
					account1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountGroupAccountsPageWithSortDouble()
		throws Exception {

		testGetAccountGroupAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(account2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountGroupAccountsPageWithSortInteger()
		throws Exception {

		testGetAccountGroupAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0);
				BeanTestUtil.setProperty(account2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountGroupAccountsPageWithSortString()
		throws Exception {

		testGetAccountGroupAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, account1, account2) -> {
				Class<?> clazz = account1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAccountGroupAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Account, Account, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountGroupId =
			testGetAccountGroupAccountsPage_getAccountGroupId();

		Account account1 = randomAccount();
		Account account2 = randomAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, account1, account2);
		}

		account1 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, account1);

		account2 = testGetAccountGroupAccountsPage_addAccount(
			accountGroupId, account2);

		Page<Account> page = accountResource.getAccountGroupAccountsPage(
			accountGroupId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Account> ascPage = accountResource.getAccountGroupAccountsPage(
				accountGroupId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(account1, (List<Account>)ascPage.getItems());
			assertContains(account2, (List<Account>)ascPage.getItems());

			Page<Account> descPage =
				accountResource.getAccountGroupAccountsPage(
					accountGroupId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(account2, (List<Account>)descPage.getItems());
			assertContains(account1, (List<Account>)descPage.getItems());
		}
	}

	protected Account testGetAccountGroupAccountsPage_addAccount(
			Long accountGroupId, Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountGroupAccountsPage_getAccountGroupId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountGroupAccountsPage_getIrrelevantAccountGroupId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPage()
		throws Exception {

		String accountGroupExternalReferenceCode =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode();
		String irrelevantAccountGroupExternalReferenceCode =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getIrrelevantAccountGroupExternalReferenceCode();

		Page<Account> page =
			accountResource.getAccountGroupByExternalReferenceCodeAccountsPage(
				accountGroupExternalReferenceCode, null, null,
				Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountGroupExternalReferenceCode != null) {
			Account irrelevantAccount =
				testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
					irrelevantAccountGroupExternalReferenceCode,
					randomIrrelevantAccount());

			page =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						irrelevantAccountGroupExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantAccount, (List<Account>)page.getItems());
			assertValid(
				page,
				testGetAccountGroupByExternalReferenceCodeAccountsPage_getExpectedActions(
					irrelevantAccountGroupExternalReferenceCode));
		}

		Account account1 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		Account account2 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		page =
			accountResource.getAccountGroupByExternalReferenceCodeAccountsPage(
				accountGroupExternalReferenceCode, null, null,
				Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(account1, (List<Account>)page.getItems());
		assertContains(account2, (List<Account>)page.getItems());
		assertValid(
			page,
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getExpectedActions(
				accountGroupExternalReferenceCode));

		accountResource.deleteAccount(account1.getId());

		accountResource.deleteAccount(account2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getExpectedActions(
				String accountGroupExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountGroupExternalReferenceCode =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode();

		Account account1 = randomAccount();

		account1 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, account1);

		for (EntityField entityField : entityFields) {
			Page<Account> page =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null,
						getFilterString(entityField, "between", account1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilterStringContains()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetAccountGroupByExternalReferenceCodeAccountsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountGroupExternalReferenceCode =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode();

		Account account1 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account2 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		for (EntityField entityField : entityFields) {
			Page<Account> page =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null,
						getFilterString(entityField, operator, account1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithPagination()
		throws Exception {

		String accountGroupExternalReferenceCode =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode();

		Page<Account> accountsPage =
			accountResource.getAccountGroupByExternalReferenceCodeAccountsPage(
				accountGroupExternalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(accountsPage.getTotalCount());

		Account account1 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		Account account2 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		Account account3 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, randomAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Account> page1 =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(account1, (List<Account>)page1.getItems());

			Page<Account> page2 =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(account2, (List<Account>)page2.getItems());

			Page<Account> page3 =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(account3, (List<Account>)page3.getItems());
		}
		else {
			Page<Account> page1 =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<Account> accounts1 = (List<Account>)page1.getItems();

			Assert.assertEquals(
				accounts1.toString(), totalCount + 2, accounts1.size());

			Page<Account> page2 =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Account> accounts2 = (List<Account>)page2.getItems();

			Assert.assertEquals(accounts2.toString(), 1, accounts2.size());

			Page<Account> page3 =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(account1, (List<Account>)page3.getItems());
			assertContains(account2, (List<Account>)page3.getItems());
			assertContains(account3, (List<Account>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithSortDateTime()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(
					account1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithSortDouble()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(account2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithSortInteger()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0);
				BeanTestUtil.setProperty(account2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountGroupByExternalReferenceCodeAccountsPageWithSortString()
		throws Exception {

		testGetAccountGroupByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, account1, account2) -> {
				Class<?> clazz = account1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetAccountGroupByExternalReferenceCodeAccountsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer<EntityField, Account, Account, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String accountGroupExternalReferenceCode =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode();

		Account account1 = randomAccount();
		Account account2 = randomAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, account1, account2);
		}

		account1 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, account1);

		account2 =
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				accountGroupExternalReferenceCode, account2);

		Page<Account> page =
			accountResource.getAccountGroupByExternalReferenceCodeAccountsPage(
				accountGroupExternalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Account> ascPage =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(account1, (List<Account>)ascPage.getItems());
			assertContains(account2, (List<Account>)ascPage.getItems());

			Page<Account> descPage =
				accountResource.
					getAccountGroupByExternalReferenceCodeAccountsPage(
						accountGroupExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(account2, (List<Account>)descPage.getItems());
			assertContains(account1, (List<Account>)descPage.getItems());
		}
	}

	protected Account
			testGetAccountGroupByExternalReferenceCodeAccountsPage_addAccount(
				String accountGroupExternalReferenceCode, Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getAccountGroupExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountGroupByExternalReferenceCodeAccountsPage_getIrrelevantAccountGroupExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountsPage() throws Exception {
		Page<Account> page = accountResource.getAccountsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Account account1 = testGetAccountsPage_addAccount(randomAccount());

		Account account2 = testGetAccountsPage_addAccount(randomAccount());

		page = accountResource.getAccountsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(account1, (List<Account>)page.getItems());
		assertContains(account2, (List<Account>)page.getItems());
		assertValid(page, testGetAccountsPage_getExpectedActions());

		accountResource.deleteAccount(account1.getId());

		accountResource.deleteAccount(account2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountsPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Account account1 = randomAccount();

		account1 = testGetAccountsPage_addAccount(account1);

		for (EntityField entityField : entityFields) {
			Page<Account> page = accountResource.getAccountsPage(
				null, getFilterString(entityField, "between", account1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetAccountsPageWithFilterDoubleEquals() throws Exception {
		testGetAccountsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountsPageWithFilterStringContains() throws Exception {
		testGetAccountsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountsPageWithFilterStringEquals() throws Exception {
		testGetAccountsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountsPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Account account1 = testGetAccountsPage_addAccount(randomAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account2 = testGetAccountsPage_addAccount(randomAccount());

		for (EntityField entityField : entityFields) {
			Page<Account> page = accountResource.getAccountsPage(
				null, getFilterString(entityField, operator, account1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetAccountsPageWithPagination() throws Exception {
		Page<Account> accountsPage = accountResource.getAccountsPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(accountsPage.getTotalCount());

		Account account1 = testGetAccountsPage_addAccount(randomAccount());

		Account account2 = testGetAccountsPage_addAccount(randomAccount());

		Account account3 = testGetAccountsPage_addAccount(randomAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Account> page1 = accountResource.getAccountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(account1, (List<Account>)page1.getItems());

			Page<Account> page2 = accountResource.getAccountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(account2, (List<Account>)page2.getItems());

			Page<Account> page3 = accountResource.getAccountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(account3, (List<Account>)page3.getItems());
		}
		else {
			Page<Account> page1 = accountResource.getAccountsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Account> accounts1 = (List<Account>)page1.getItems();

			Assert.assertEquals(
				accounts1.toString(), totalCount + 2, accounts1.size());

			Page<Account> page2 = accountResource.getAccountsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Account> accounts2 = (List<Account>)page2.getItems();

			Assert.assertEquals(accounts2.toString(), 1, accounts2.size());

			Page<Account> page3 = accountResource.getAccountsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(account1, (List<Account>)page3.getItems());
			assertContains(account2, (List<Account>)page3.getItems());
			assertContains(account3, (List<Account>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountsPageWithSortDateTime() throws Exception {
		testGetAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(
					account1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountsPageWithSortDouble() throws Exception {
		testGetAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(account2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountsPageWithSortInteger() throws Exception {
		testGetAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0);
				BeanTestUtil.setProperty(account2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountsPageWithSortString() throws Exception {
		testGetAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, account1, account2) -> {
				Class<?> clazz = account1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Account, Account, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Account account1 = randomAccount();
		Account account2 = randomAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, account1, account2);
		}

		account1 = testGetAccountsPage_addAccount(account1);

		account2 = testGetAccountsPage_addAccount(account2);

		Page<Account> page = accountResource.getAccountsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Account> ascPage = accountResource.getAccountsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(account1, (List<Account>)ascPage.getItems());
			assertContains(account2, (List<Account>)ascPage.getItems());

			Page<Account> descPage = accountResource.getAccountsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(account2, (List<Account>)descPage.getItems());
			assertContains(account1, (List<Account>)descPage.getItems());
		}
	}

	protected Account testGetAccountsPage_addAccount(Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"accounts",
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

		JSONObject accountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accounts");

		long totalCount = accountsJSONObject.getLong("totalCount");

		Account account1 = testGraphQLAccount_addAccount(randomAccount());

		Account account2 = testGraphQLAccount_addAccount(randomAccount());

		accountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accounts");

		Assert.assertEquals(
			totalCount + 2, accountsJSONObject.getLong("totalCount"));

		assertContains(
			account1,
			Arrays.asList(
				AccountSerDes.toDTOs(accountsJSONObject.getString("items"))));
		assertContains(
			account2,
			Arrays.asList(
				AccountSerDes.toDTOs(accountsJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		accountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/accounts");

		Assert.assertEquals(
			totalCount + 2, accountsJSONObject.getLong("totalCount"));

		assertContains(
			account1,
			Arrays.asList(
				AccountSerDes.toDTOs(accountsJSONObject.getString("items"))));
		assertContains(
			account2,
			Arrays.asList(
				AccountSerDes.toDTOs(accountsJSONObject.getString("items"))));
	}

	@Test
	public void testGetOrganizationAccountsPage() throws Exception {
		String organizationId =
			testGetOrganizationAccountsPage_getOrganizationId();
		String irrelevantOrganizationId =
			testGetOrganizationAccountsPage_getIrrelevantOrganizationId();

		Page<Account> page = accountResource.getOrganizationAccountsPage(
			organizationId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantOrganizationId != null) {
			Account irrelevantAccount =
				testGetOrganizationAccountsPage_addAccount(
					irrelevantOrganizationId, randomIrrelevantAccount());

			page = accountResource.getOrganizationAccountsPage(
				irrelevantOrganizationId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantAccount, (List<Account>)page.getItems());
			assertValid(
				page,
				testGetOrganizationAccountsPage_getExpectedActions(
					irrelevantOrganizationId));
		}

		Account account1 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		Account account2 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		page = accountResource.getOrganizationAccountsPage(
			organizationId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(account1, (List<Account>)page.getItems());
		assertContains(account2, (List<Account>)page.getItems());
		assertValid(
			page,
			testGetOrganizationAccountsPage_getExpectedActions(organizationId));

		accountResource.deleteAccount(account1.getId());

		accountResource.deleteAccount(account2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationAccountsPage_getExpectedActions(
				String organizationId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrganizationAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationId =
			testGetOrganizationAccountsPage_getOrganizationId();

		Account account1 = randomAccount();

		account1 = testGetOrganizationAccountsPage_addAccount(
			organizationId, account1);

		for (EntityField entityField : entityFields) {
			Page<Account> page = accountResource.getOrganizationAccountsPage(
				organizationId, null,
				getFilterString(entityField, "between", account1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrganizationAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrganizationAccountsPageWithFilterStringContains()
		throws Exception {

		testGetOrganizationAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetOrganizationAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrganizationAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrganizationAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationId =
			testGetOrganizationAccountsPage_getOrganizationId();

		Account account1 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account2 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		for (EntityField entityField : entityFields) {
			Page<Account> page = accountResource.getOrganizationAccountsPage(
				organizationId, null,
				getFilterString(entityField, operator, account1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationAccountsPageWithPagination()
		throws Exception {

		String organizationId =
			testGetOrganizationAccountsPage_getOrganizationId();

		Page<Account> accountsPage =
			accountResource.getOrganizationAccountsPage(
				organizationId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(accountsPage.getTotalCount());

		Account account1 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		Account account2 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		Account account3 = testGetOrganizationAccountsPage_addAccount(
			organizationId, randomAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Account> page1 = accountResource.getOrganizationAccountsPage(
				organizationId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(account1, (List<Account>)page1.getItems());

			Page<Account> page2 = accountResource.getOrganizationAccountsPage(
				organizationId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(account2, (List<Account>)page2.getItems());

			Page<Account> page3 = accountResource.getOrganizationAccountsPage(
				organizationId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(account3, (List<Account>)page3.getItems());
		}
		else {
			Page<Account> page1 = accountResource.getOrganizationAccountsPage(
				organizationId, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<Account> accounts1 = (List<Account>)page1.getItems();

			Assert.assertEquals(
				accounts1.toString(), totalCount + 2, accounts1.size());

			Page<Account> page2 = accountResource.getOrganizationAccountsPage(
				organizationId, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Account> accounts2 = (List<Account>)page2.getItems();

			Assert.assertEquals(accounts2.toString(), 1, accounts2.size());

			Page<Account> page3 = accountResource.getOrganizationAccountsPage(
				organizationId, null, null,
				Pagination.of(1, (int)totalCount + 3), null);

			assertContains(account1, (List<Account>)page3.getItems());
			assertContains(account2, (List<Account>)page3.getItems());
			assertContains(account3, (List<Account>)page3.getItems());
		}
	}

	@Test
	public void testGetOrganizationAccountsPageWithSortDateTime()
		throws Exception {

		testGetOrganizationAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(
					account1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrganizationAccountsPageWithSortDouble()
		throws Exception {

		testGetOrganizationAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(account2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrganizationAccountsPageWithSortInteger()
		throws Exception {

		testGetOrganizationAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0);
				BeanTestUtil.setProperty(account2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrganizationAccountsPageWithSortString()
		throws Exception {

		testGetOrganizationAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, account1, account2) -> {
				Class<?> clazz = account1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrganizationAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Account, Account, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationId =
			testGetOrganizationAccountsPage_getOrganizationId();

		Account account1 = randomAccount();
		Account account2 = randomAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, account1, account2);
		}

		account1 = testGetOrganizationAccountsPage_addAccount(
			organizationId, account1);

		account2 = testGetOrganizationAccountsPage_addAccount(
			organizationId, account2);

		Page<Account> page = accountResource.getOrganizationAccountsPage(
			organizationId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Account> ascPage = accountResource.getOrganizationAccountsPage(
				organizationId, null, null,
				Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(account1, (List<Account>)ascPage.getItems());
			assertContains(account2, (List<Account>)ascPage.getItems());

			Page<Account> descPage =
				accountResource.getOrganizationAccountsPage(
					organizationId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(account2, (List<Account>)descPage.getItems());
			assertContains(account1, (List<Account>)descPage.getItems());
		}
	}

	protected Account testGetOrganizationAccountsPage_addAccount(
			String organizationId, Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetOrganizationAccountsPage_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationAccountsPage_getIrrelevantOrganizationId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeAccountsPage_getIrrelevantExternalReferenceCode();

		Page<Account> page =
			accountResource.getOrganizationByExternalReferenceCodeAccountsPage(
				externalReferenceCode, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Account irrelevantAccount =
				testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
					irrelevantExternalReferenceCode, randomIrrelevantAccount());

			page =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantAccount, (List<Account>)page.getItems());
			assertValid(
				page,
				testGetOrganizationByExternalReferenceCodeAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Account account1 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		Account account2 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		page =
			accountResource.getOrganizationByExternalReferenceCodeAccountsPage(
				externalReferenceCode, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(account1, (List<Account>)page.getItems());
		assertContains(account2, (List<Account>)page.getItems());
		assertValid(
			page,
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExpectedActions(
				externalReferenceCode));

		accountResource.deleteAccount(account1.getId());

		accountResource.deleteAccount(account2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode();

		Account account1 = randomAccount();

		account1 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, account1);

		for (EntityField entityField : entityFields) {
			Page<Account> page =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", account1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithFilterStringContains()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetOrganizationByExternalReferenceCodeAccountsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode();

		Account account1 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account2 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		for (EntityField entityField : entityFields) {
			Page<Account> page =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, account1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode();

		Page<Account> accountsPage =
			accountResource.getOrganizationByExternalReferenceCodeAccountsPage(
				externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(accountsPage.getTotalCount());

		Account account1 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		Account account2 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		Account account3 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, randomAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Account> page1 =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(account1, (List<Account>)page1.getItems());

			Page<Account> page2 =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(account2, (List<Account>)page2.getItems());

			Page<Account> page3 =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(account3, (List<Account>)page3.getItems());
		}
		else {
			Page<Account> page1 =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<Account> accounts1 = (List<Account>)page1.getItems();

			Assert.assertEquals(
				accounts1.toString(), totalCount + 2, accounts1.size());

			Page<Account> page2 =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Account> accounts2 = (List<Account>)page2.getItems();

			Assert.assertEquals(accounts2.toString(), 1, accounts2.size());

			Page<Account> page3 =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(account1, (List<Account>)page3.getItems());
			assertContains(account2, (List<Account>)page3.getItems());
			assertContains(account3, (List<Account>)page3.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithSortDateTime()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(
					account1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithSortDouble()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(account2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithSortInteger()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0);
				BeanTestUtil.setProperty(account2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeAccountsPageWithSortString()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, account1, account2) -> {
				Class<?> clazz = account1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetOrganizationByExternalReferenceCodeAccountsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer<EntityField, Account, Account, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode();

		Account account1 = randomAccount();
		Account account2 = randomAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, account1, account2);
		}

		account1 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, account1);

		account2 =
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				externalReferenceCode, account2);

		Page<Account> page =
			accountResource.getOrganizationByExternalReferenceCodeAccountsPage(
				externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Account> ascPage =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(account1, (List<Account>)ascPage.getItems());
			assertContains(account2, (List<Account>)ascPage.getItems());

			Page<Account> descPage =
				accountResource.
					getOrganizationByExternalReferenceCodeAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(account2, (List<Account>)descPage.getItems());
			assertContains(account1, (List<Account>)descPage.getItems());
		}
	}

	protected Account
			testGetOrganizationByExternalReferenceCodeAccountsPage_addAccount(
				String externalReferenceCode, Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage()
		throws Exception {

		String organizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode();
		String irrelevantOrganizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getIrrelevantOrganizationExternalReferenceCode();

		Page<Account> page =
			accountResource.
				getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
					organizationExternalReferenceCode, null, null,
					Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantOrganizationExternalReferenceCode != null) {
			Account irrelevantAccount =
				testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
					irrelevantOrganizationExternalReferenceCode,
					randomIrrelevantAccount());

			page =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						irrelevantOrganizationExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantAccount, (List<Account>)page.getItems());
			assertValid(
				page,
				testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getExpectedActions(
					irrelevantOrganizationExternalReferenceCode));
		}

		Account account1 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		Account account2 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		page =
			accountResource.
				getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
					organizationExternalReferenceCode, null, null,
					Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(account1, (List<Account>)page.getItems());
		assertContains(account2, (List<Account>)page.getItems());
		assertValid(
			page,
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getExpectedActions(
				organizationExternalReferenceCode));

		accountResource.deleteAccount(account1.getId());

		accountResource.deleteAccount(account2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getExpectedActions(
				String organizationExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode();

		Account account1 = randomAccount();

		account1 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, account1);

		for (EntityField entityField : entityFields) {
			Page<Account> page =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null,
						getFilterString(entityField, "between", account1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilterDoubleEquals()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilterStringContains()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilterStringEquals()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilterStringStartsWith()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode();

		Account account1 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account2 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		for (EntityField entityField : entityFields) {
			Page<Account> page =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null,
						getFilterString(entityField, operator, account1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(account1),
				(List<Account>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithPagination()
		throws Exception {

		String organizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode();

		Page<Account> accountsPage =
			accountResource.
				getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
					organizationExternalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(accountsPage.getTotalCount());

		Account account1 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		Account account2 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		Account account3 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, randomAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Account> page1 =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(account1, (List<Account>)page1.getItems());

			Page<Account> page2 =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(account2, (List<Account>)page2.getItems());

			Page<Account> page3 =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(account3, (List<Account>)page3.getItems());
		}
		else {
			Page<Account> page1 =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<Account> accounts1 = (List<Account>)page1.getItems();

			Assert.assertEquals(
				accounts1.toString(), totalCount + 2, accounts1.size());

			Page<Account> page2 =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Account> accounts2 = (List<Account>)page2.getItems();

			Assert.assertEquals(accounts2.toString(), 1, accounts2.size());

			Page<Account> page3 =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(account1, (List<Account>)page3.getItems());
			assertContains(account2, (List<Account>)page3.getItems());
			assertContains(account3, (List<Account>)page3.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSortDateTime()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(
					account1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSortDouble()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(account2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSortInteger()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, account1, account2) -> {
				BeanTestUtil.setProperty(account1, entityField.getName(), 0);
				BeanTestUtil.setProperty(account2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSortString()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.STRING,
			(entityField, account1, account2) -> {
				Class<?> clazz = account1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						account1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						account2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer<EntityField, Account, Account, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode();

		Account account1 = randomAccount();
		Account account2 = randomAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, account1, account2);
		}

		account1 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, account1);

		account2 =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				organizationExternalReferenceCode, account2);

		Page<Account> page =
			accountResource.
				getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
					organizationExternalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Account> ascPage =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(account1, (List<Account>)ascPage.getItems());
			assertContains(account2, (List<Account>)ascPage.getItems());

			Page<Account> descPage =
				accountResource.
					getOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage(
						organizationExternalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(account2, (List<Account>)descPage.getItems());
			assertContains(account1, (List<Account>)descPage.getItems());
		}
	}

	protected Account
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_addAccount(
				String organizationExternalReferenceCode, Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getIrrelevantOrganizationExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage()
		throws Exception {

		String organizationExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePage_getOrganizationExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCode",
			new HashMap<String, Object>() {
				{
					put(
						"organizationExternalReferenceCode",
						"\"" + organizationExternalReferenceCode + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject
			organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject =
				JSONUtil.getValueAsJSONObject(
					invokeGraphQLQuery(graphQLField), "JSONObject/data",
					"JSONObject/organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCode");

		long totalCount =
			organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
				getLong("totalCount");

		Account account1 =
			testGraphQLGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageAccount_addAccount(
				organizationExternalReferenceCode, randomAccount());

		Account account2 =
			testGraphQLGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageAccount_addAccount(
				organizationExternalReferenceCode, randomAccount());

		organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCode");

		Assert.assertEquals(
			totalCount + 2,
			organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
				getLong("totalCount"));

		assertContains(
			account1,
			Arrays.asList(
				AccountSerDes.toDTOs(
					organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));
		assertContains(
			account2,
			Arrays.asList(
				AccountSerDes.toDTOs(
					organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"JSONObject/organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCode");

		Assert.assertEquals(
			totalCount + 2,
			organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
				getLong("totalCount"));

		assertContains(
			account1,
			Arrays.asList(
				AccountSerDes.toDTOs(
					organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));
		assertContains(
			account2,
			Arrays.asList(
				AccountSerDes.toDTOs(
					organizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));
	}

	protected Account
			testGraphQLGetOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountsByExternalReferenceCodePageAccount_addAccount(
				String organizationExternalReferenceCode, Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAccount() throws Exception {
		Account postAccount = testPatchAccount_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account patchAccount = accountResource.patchAccount(
			postAccount.getId(), randomPatchAccount);

		Account expectedPatchAccount = postAccount.clone();

		BeanTestUtil.copyProperties(randomPatchAccount, expectedPatchAccount);

		Account getAccount = accountResource.getAccount(patchAccount.getId());

		assertEquals(expectedPatchAccount, getAccount);
		assertValid(getAccount);
	}

	protected Account testPatchAccount_addAccount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAccountByExternalReferenceCode() throws Exception {
		Account postAccount =
			testPatchAccountByExternalReferenceCode_addAccount();

		Account randomPatchAccount = randomPatchAccount();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account patchAccount =
			accountResource.patchAccountByExternalReferenceCode(
				postAccount.getExternalReferenceCode(), randomPatchAccount);

		Account expectedPatchAccount = postAccount.clone();

		BeanTestUtil.copyProperties(randomPatchAccount, expectedPatchAccount);

		Account getAccount = accountResource.getAccountByExternalReferenceCode(
			patchAccount.getExternalReferenceCode());

		assertEquals(expectedPatchAccount, getAccount);
		assertValid(getAccount);
	}

	protected Account testPatchAccountByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrganizationMoveAccounts() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account = testPatchOrganizationMoveAccounts_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.patchOrganizationMoveAccountsHttpResponse(
				testPatchOrganizationMoveAccounts_getSourceOrganizationId(),
				testPatchOrganizationMoveAccounts_getTargetOrganizationId(),
				null));

		assertHttpResponseStatusCode(
			404,
			accountResource.patchOrganizationMoveAccountsHttpResponse(
				testPatchOrganizationMoveAccounts_getSourceOrganizationId(),
				testPatchOrganizationMoveAccounts_getTargetOrganizationId(),
				null));
	}

	protected Long testPatchOrganizationMoveAccounts_getSourceOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPatchOrganizationMoveAccounts_getTargetOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account testPatchOrganizationMoveAccounts_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchOrganizationMoveAccountsByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testPatchOrganizationMoveAccountsByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				patchOrganizationMoveAccountsByExternalReferenceCodeHttpResponse(
					testPatchOrganizationMoveAccountsByExternalReferenceCode_getSourceOrganizationId(),
					testPatchOrganizationMoveAccountsByExternalReferenceCode_getTargetOrganizationId(),
					null));

		assertHttpResponseStatusCode(
			404,
			accountResource.
				patchOrganizationMoveAccountsByExternalReferenceCodeHttpResponse(
					testPatchOrganizationMoveAccountsByExternalReferenceCode_getSourceOrganizationId(),
					testPatchOrganizationMoveAccountsByExternalReferenceCode_getTargetOrganizationId(),
					null));
	}

	protected Long
			testPatchOrganizationMoveAccountsByExternalReferenceCode_getSourceOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPatchOrganizationMoveAccountsByExternalReferenceCode_getTargetOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testPatchOrganizationMoveAccountsByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccount() throws Exception {
		Account randomAccount = randomAccount();

		Account postAccount = testPostAccount_addAccount(randomAccount);

		assertEquals(randomAccount, postAccount);
		assertValid(postAccount);
	}

	protected Account testPostAccount_addAccount(Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostAccount() throws Exception {
		Account randomAccount = randomAccount();

		Account account = testGraphQLAccount_addAccount(randomAccount);

		Assert.assertTrue(equals(randomAccount, account));
	}

	@Test
	public void testPostOrganizationAccounts() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account = testPostOrganizationAccounts_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.postOrganizationAccountsHttpResponse(
				testPostOrganizationAccounts_getOrganizationId(), null));

		assertHttpResponseStatusCode(
			404,
			accountResource.postOrganizationAccountsHttpResponse(
				testPostOrganizationAccounts_getOrganizationId(), null));
	}

	protected Long testPostOrganizationAccounts_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account testPostOrganizationAccounts_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrganizationAccountsByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testPostOrganizationAccountsByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				postOrganizationAccountsByExternalReferenceCodeHttpResponse(
					testPostOrganizationAccountsByExternalReferenceCode_getOrganizationId(),
					null));

		assertHttpResponseStatusCode(
			404,
			accountResource.
				postOrganizationAccountsByExternalReferenceCodeHttpResponse(
					testPostOrganizationAccountsByExternalReferenceCode_getOrganizationId(),
					null));
	}

	protected Long
			testPostOrganizationAccountsByExternalReferenceCode_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testPostOrganizationAccountsByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrganizationByExternalReferenceCodeAccounts()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testPostOrganizationByExternalReferenceCodeAccounts_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				postOrganizationByExternalReferenceCodeAccountsHttpResponse(
					testPostOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
						account),
					null));

		assertHttpResponseStatusCode(
			404,
			accountResource.
				postOrganizationByExternalReferenceCodeAccountsHttpResponse(
					testPostOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
						account),
					null));
	}

	protected String
			testPostOrganizationByExternalReferenceCodeAccounts_getExternalReferenceCode(
				Account account)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testPostOrganizationByExternalReferenceCodeAccounts_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Account account =
			testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount();

		assertHttpResponseStatusCode(
			204,
			accountResource.
				postOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode(),
					null));

		assertHttpResponseStatusCode(
			404,
			accountResource.
				postOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCodeHttpResponse(
					testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode(),
					null));
	}

	protected String
			testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_getOrganizationExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account
			testPostOrganizationByExternalReferenceCodeOrganizationExternalReferenceCodeAccountByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAccount() throws Exception {
		Account postAccount = testPutAccount_addAccount();

		Account randomAccount = randomAccount();

		Account putAccount = accountResource.putAccount(
			postAccount.getId(), randomAccount);

		assertEquals(randomAccount, putAccount);
		assertValid(putAccount);

		Account getAccount = accountResource.getAccount(putAccount.getId());

		assertEquals(randomAccount, getAccount);
		assertValid(getAccount);
	}

	protected Account testPutAccount_addAccount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAccountByExternalReferenceCode() throws Exception {
		Account postAccount =
			testPutAccountByExternalReferenceCode_addAccount();

		Account randomAccount = randomAccount();

		Account putAccount = accountResource.putAccountByExternalReferenceCode(
			postAccount.getExternalReferenceCode(), randomAccount);

		assertEquals(randomAccount, putAccount);
		assertValid(putAccount);

		Account getAccount = accountResource.getAccountByExternalReferenceCode(
			putAccount.getExternalReferenceCode());

		assertEquals(randomAccount, getAccount);
		assertValid(getAccount);

		Account newAccount =
			testPutAccountByExternalReferenceCode_createAccount();

		putAccount = accountResource.putAccountByExternalReferenceCode(
			newAccount.getExternalReferenceCode(), newAccount);

		assertEquals(newAccount, putAccount);
		assertValid(putAccount);

		getAccount = accountResource.getAccountByExternalReferenceCode(
			putAccount.getExternalReferenceCode());

		assertEquals(newAccount, getAccount);

		Assert.assertEquals(
			newAccount.getExternalReferenceCode(),
			putAccount.getExternalReferenceCode());
	}

	protected Account testPutAccountByExternalReferenceCode_addAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Account testPutAccountByExternalReferenceCode_createAccount()
		throws Exception {

		return randomAccount();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Account account1 = testBatchEngineDeleteImportTask_addAccount();

		testBatchEngineDeleteImportTask_deleteAccount(
			200, account1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account1.getId()));

		account1 = testBatchEngineDeleteImportTask_addAccount();

		testBatchEngineDeleteImportTask_deleteAccount(
			200, null, account1.getId());

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account1.getId()));

		account1 = testBatchEngineDeleteImportTask_addAccount();
		Account account2 = testBatchEngineDeleteImportTask_addAccount();

		testBatchEngineDeleteImportTask_deleteAccount(
			200, account2.getExternalReferenceCode(), account1.getId());

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account1.getId()));
		assertHttpResponseStatusCode(
			200, accountResource.getAccountHttpResponse(account2.getId()));

		testBatchEngineDeleteImportTask_deleteAccount(
			200, account2.getExternalReferenceCode(), account1.getId());

		assertHttpResponseStatusCode(
			404, accountResource.getAccountHttpResponse(account2.getId()));
	}

	protected Account testBatchEngineDeleteImportTask_addAccount()
		throws Exception {

		return testDeleteAccount_addAccount();
	}

	protected void testBatchEngineDeleteImportTask_deleteAccount(
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
				"com.liferay.headless.admin.user.dto.v1_0.Account", null, null,
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

	protected Account testGraphQLAccount_addAccount() throws Exception {
		return testGraphQLAccount_addAccount(randomAccount());
	}

	protected Account testGraphQLAccount_addAccount(Account account)
		throws Exception {

		JSONDeserializer<Account> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field : getDeclaredFields(Account.class)) {
			if (getGraphQLValue(field.get(account)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(account)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAccount",
						new HashMap<String, Object>() {
							{
								put("account", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createAccount"),
			Account.class);
	}

	protected Account testGraphQLOrganizationAccount_addAccount()
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

	protected void assertContains(Account account, List<Account> accounts) {
		boolean contains = false;

		for (Account item : accounts) {
			if (equals(account, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(accounts + " does not contain " + account, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Account account1, Account account2) {
		Assert.assertTrue(
			account1 + " does not equal " + account2,
			equals(account1, account2));
	}

	protected void assertEquals(
		List<Account> accounts1, List<Account> accounts2) {

		Assert.assertEquals(accounts1.size(), accounts2.size());

		for (int i = 0; i < accounts1.size(); i++) {
			Account account1 = accounts1.get(i);
			Account account2 = accounts2.get(i);

			assertEquals(account1, account2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Account> accounts1, List<Account> accounts2) {

		Assert.assertEquals(accounts1.size(), accounts2.size());

		for (Account account1 : accounts1) {
			boolean contains = false;

			for (Account account2 : accounts2) {
				if (equals(account1, account2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accounts2 + " does not contain " + account1, contains);
		}
	}

	protected void assertValid(Account account) throws Exception {
		boolean valid = true;

		if (account.getDateCreated() == null) {
			valid = false;
		}

		if (account.getDateModified() == null) {
			valid = false;
		}

		if (account.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountContactInformation", additionalAssertFieldName)) {

				if (account.getAccountContactInformation() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupBriefs", additionalAssertFieldName)) {

				if (account.getAccountGroupBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountRoles", additionalAssertFieldName)) {
				if (account.getAccountRoles() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountUserAccounts", additionalAssertFieldName)) {

				if (account.getAccountUserAccounts() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (account.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (account.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (account.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultBillingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (account.getDefaultBillingAddressExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultBillingAddressId", additionalAssertFieldName)) {

				if (account.getDefaultBillingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultShippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (account.getDefaultShippingAddressExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultShippingAddressId", additionalAssertFieldName)) {

				if (account.getDefaultShippingAddressId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (account.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("domains", additionalAssertFieldName)) {
				if (account.getDomains() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (account.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (account.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("logoBase64", additionalAssertFieldName)) {
				if (account.getLogoBase64() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"logoExternalReferenceCode", additionalAssertFieldName)) {

				if (account.getLogoExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("logoId", additionalAssertFieldName)) {
				if (account.getLogoId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("logoURL", additionalAssertFieldName)) {
				if (account.getLogoURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (account.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("numberOfUsers", additionalAssertFieldName)) {
				if (account.getNumberOfUsers() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"organizationExternalReferenceCodes",
					additionalAssertFieldName)) {

				if (account.getOrganizationExternalReferenceCodes() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("organizationIds", additionalAssertFieldName)) {
				if (account.getOrganizationIds() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"parentAccountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (account.getParentAccountExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("parentAccountId", additionalAssertFieldName)) {
				if (account.getParentAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (account.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("postalAddresses", additionalAssertFieldName)) {
				if (account.getPostalAddresses() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (account.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("taxId", additionalAssertFieldName)) {
				if (account.getTaxId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (account.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (account.getType() == null) {
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

	protected void assertValid(Page<Account> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Account> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Account> accounts = page.getItems();

		int size = accounts.size();

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
					com.liferay.headless.admin.user.dto.v1_0.Account.class)) {

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

	protected boolean equals(Account account1, Account account2) {
		if (account1 == account2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountContactInformation", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getAccountContactInformation(),
						account2.getAccountContactInformation())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountGroupBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getAccountGroupBriefs(),
						account2.getAccountGroupBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountRoles", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getAccountRoles(),
						account2.getAccountRoles())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountUserAccounts", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getAccountUserAccounts(),
						account2.getAccountUserAccounts())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)account1.getActions(),
						(Map)account2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getCreator(), account2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getCustomFields(),
						account2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getDateCreated(), account2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getDateModified(),
						account2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultBillingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.
							getDefaultBillingAddressExternalReferenceCode(),
						account2.
							getDefaultBillingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultBillingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getDefaultBillingAddressId(),
						account2.getDefaultBillingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultShippingAddressExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.
							getDefaultShippingAddressExternalReferenceCode(),
						account2.
							getDefaultShippingAddressExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"defaultShippingAddressId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getDefaultShippingAddressId(),
						account2.getDefaultShippingAddressId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getDescription(), account2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("domains", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getDomains(), account2.getDomains())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getExternalReferenceCode(),
						account2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(account1.getId(), account2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getKeywords(), account2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("logoBase64", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getLogoBase64(), account2.getLogoBase64())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"logoExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getLogoExternalReferenceCode(),
						account2.getLogoExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("logoId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getLogoId(), account2.getLogoId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("logoURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getLogoURL(), account2.getLogoURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getName(), account2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("numberOfUsers", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getNumberOfUsers(),
						account2.getNumberOfUsers())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"organizationExternalReferenceCodes",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getOrganizationExternalReferenceCodes(),
						account2.getOrganizationExternalReferenceCodes())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("organizationIds", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getOrganizationIds(),
						account2.getOrganizationIds())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"parentAccountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getParentAccountExternalReferenceCode(),
						account2.getParentAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("parentAccountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getParentAccountId(),
						account2.getParentAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getPermissions(), account2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("postalAddresses", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getPostalAddresses(),
						account2.getPostalAddresses())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getStatus(), account2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("taxId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getTaxId(), account2.getTaxId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						account1.getTaxonomyCategoryBriefs(),
						account2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						account1.getType(), account2.getType())) {

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

		if (!(_accountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountResource;

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
		EntityField entityField, String operator, Account account) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountContactInformation")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountGroupBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountRoles")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountUserAccounts")) {
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
				Date date = account.getDateCreated();

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

				sb.append(_format.format(account.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = account.getDateModified();

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

				sb.append(_format.format(account.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals(
				"defaultBillingAddressExternalReferenceCode")) {

			Object object =
				account.getDefaultBillingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("defaultBillingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"defaultShippingAddressExternalReferenceCode")) {

			Object object =
				account.getDefaultShippingAddressExternalReferenceCode();

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

		if (entityFieldName.equals("defaultShippingAddressId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = account.getDescription();

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

		if (entityFieldName.equals("domains")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = account.getExternalReferenceCode();

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

		if (entityFieldName.equals("logoBase64")) {
			Object object = account.getLogoBase64();

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

		if (entityFieldName.equals("logoExternalReferenceCode")) {
			Object object = account.getLogoExternalReferenceCode();

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

		if (entityFieldName.equals("logoId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("logoURL")) {
			Object object = account.getLogoURL();

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

		if (entityFieldName.equals("name")) {
			Object object = account.getName();

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

		if (entityFieldName.equals("numberOfUsers")) {
			sb.append(String.valueOf(account.getNumberOfUsers()));

			return sb.toString();
		}

		if (entityFieldName.equals("organizationExternalReferenceCodes")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("organizationIds")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("parentAccountExternalReferenceCode")) {
			Object object = account.getParentAccountExternalReferenceCode();

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

		if (entityFieldName.equals("parentAccountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("permissions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("postalAddresses")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			sb.append(String.valueOf(account.getStatus()));

			return sb.toString();
		}

		if (entityFieldName.equals("taxId")) {
			Object object = account.getTaxId();

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

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("type")) {
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

	protected Account randomAccount() throws Exception {
		return new Account() {
			{
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				defaultBillingAddressExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				defaultBillingAddressId = RandomTestUtil.randomLong();
				defaultShippingAddressExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				defaultShippingAddressId = RandomTestUtil.randomLong();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				logoBase64 = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				logoExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				logoId = RandomTestUtil.randomLong();
				logoURL = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				numberOfUsers = RandomTestUtil.randomInt();
				parentAccountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				parentAccountId = RandomTestUtil.randomLong();
				status = RandomTestUtil.randomInt();
				taxId = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected Account randomIrrelevantAccount() throws Exception {
		Account randomIrrelevantAccount = randomAccount();

		return randomIrrelevantAccount;
	}

	protected Account randomPatchAccount() throws Exception {
		return randomAccount();
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

	protected AccountResource accountResource;
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
		LogFactoryUtil.getLog(BaseAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.AccountResource
		_accountResource;

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