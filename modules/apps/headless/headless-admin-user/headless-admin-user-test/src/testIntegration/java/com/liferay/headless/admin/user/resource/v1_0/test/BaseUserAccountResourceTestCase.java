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

import com.liferay.headless.admin.user.client.dto.v1_0.UserAccount;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.UserAccountSerDes;
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
public abstract class BaseUserAccountResourceTestCase {

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

		_userAccountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		userAccountResource = UserAccountResource.builder(
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

		UserAccount userAccount1 = randomUserAccount();

		String json = objectMapper.writeValueAsString(userAccount1);

		UserAccount userAccount2 = UserAccountSerDes.toDTO(json);

		Assert.assertTrue(equals(userAccount1, userAccount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		UserAccount userAccount = randomUserAccount();

		String json1 = objectMapper.writeValueAsString(userAccount);
		String json2 = UserAccountSerDes.toJSON(userAccount);

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

		UserAccount userAccount = randomUserAccount();

		userAccount.setAdditionalName(regex);
		userAccount.setAlternateName(regex);
		userAccount.setCurrentPassword(regex);
		userAccount.setDashboardURL(regex);
		userAccount.setEmailAddress(regex);
		userAccount.setExternalReferenceCode(regex);
		userAccount.setFamilyName(regex);
		userAccount.setGivenName(regex);
		userAccount.setHonorificPrefix(regex);
		userAccount.setHonorificSuffix(regex);
		userAccount.setImage(regex);
		userAccount.setImageExternalReferenceCode(regex);
		userAccount.setJobTitle(regex);
		userAccount.setLanguageDisplayName(regex);
		userAccount.setLanguageId(regex);
		userAccount.setName(regex);
		userAccount.setPassword(regex);
		userAccount.setProfileURL(regex);

		String json = UserAccountSerDes.toJSON(userAccount);

		Assert.assertFalse(json.contains(regex));

		userAccount = UserAccountSerDes.toDTO(json);

		Assert.assertEquals(regex, userAccount.getAdditionalName());
		Assert.assertEquals(regex, userAccount.getAlternateName());
		Assert.assertEquals(regex, userAccount.getCurrentPassword());
		Assert.assertEquals(regex, userAccount.getDashboardURL());
		Assert.assertEquals(regex, userAccount.getEmailAddress());
		Assert.assertEquals(regex, userAccount.getExternalReferenceCode());
		Assert.assertEquals(regex, userAccount.getFamilyName());
		Assert.assertEquals(regex, userAccount.getGivenName());
		Assert.assertEquals(regex, userAccount.getHonorificPrefix());
		Assert.assertEquals(regex, userAccount.getHonorificSuffix());
		Assert.assertEquals(regex, userAccount.getImage());
		Assert.assertEquals(regex, userAccount.getImageExternalReferenceCode());
		Assert.assertEquals(regex, userAccount.getJobTitle());
		Assert.assertEquals(regex, userAccount.getLanguageDisplayName());
		Assert.assertEquals(regex, userAccount.getLanguageId());
		Assert.assertEquals(regex, userAccount.getName());
		Assert.assertEquals(regex, userAccount.getPassword());
		Assert.assertEquals(regex, userAccount.getProfileURL());
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					"-"));
	}

	protected UserAccount
			testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"accountExternalReferenceCode",
									"\"" +
										testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										userAccount1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountByExternalReferenceCodeUserAccountByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"accountExternalReferenceCode",
								"\"" +
									testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
										"\"");
							put(
								"externalReferenceCode",
								"\"" + userAccount1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"accountExternalReferenceCode",
										"\"" +
											testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
												"\"");
									put(
										"externalReferenceCode",
										"\"" +
											userAccount2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"accountByExternalReferenceCodeUserAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"accountExternalReferenceCode",
									"\"" +
										testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
											"\"");
								put(
									"externalReferenceCode",
									"\"" +
										userAccount2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected String
			testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testGraphQLDeleteAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return testGraphQLAccountUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteAccountUserAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount = testDeleteAccountUserAccount_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.deleteAccountUserAccountHttpResponse(
				testDeleteAccountUserAccount_getAccountId(),
				userAccount.getId()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getAccountUserAccountHttpResponse(
				testDeleteAccountUserAccount_getAccountId(),
				userAccount.getId()));
		assertHttpResponseStatusCode(
			404,
			userAccountResource.getAccountUserAccountHttpResponse(
				testDeleteAccountUserAccount_getAccountId(), 0L));
	}

	protected UserAccount testDeleteAccountUserAccount_addUserAccount()
		throws Exception {

		return testPostAccountUserAccount_addUserAccount(randomUserAccount());
	}

	protected Long testDeleteAccountUserAccount_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountUserAccount() throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteAccountUserAccount_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountUserAccount",
						new HashMap<String, Object>() {
							{
								put(
									"accountId",
									testGraphQLDeleteAccountUserAccount_getAccountId());
								put("userAccountId", userAccount1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteAccountUserAccount"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountUserAccount",
					new HashMap<String, Object>() {
						{
							put(
								"accountId",
								testGraphQLDeleteAccountUserAccount_getAccountId());
							put("userAccountId", userAccount1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteAccountUserAccount_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountUserAccount",
							new HashMap<String, Object>() {
								{
									put(
										"accountId",
										testGraphQLDeleteAccountUserAccount_getAccountId());
									put("userAccountId", userAccount2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountUserAccount"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"accountUserAccount",
						new HashMap<String, Object>() {
							{
								put(
									"accountId",
									testGraphQLDeleteAccountUserAccount_getAccountId());
								put("userAccountId", userAccount2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Long testGraphQLDeleteAccountUserAccount_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount testGraphQLDeleteAccountUserAccount_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteAccountUserAccountByEmailAddress() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAccountUserAccountByEmailAddress_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteAccountUserAccountByEmailAddressHttpResponse(
					testDeleteAccountUserAccountByEmailAddress_getAccountId(),
					userAccount.getEmailAddress()));
	}

	protected UserAccount
			testDeleteAccountUserAccountByEmailAddress_addUserAccount()
		throws Exception {

		return testPostAccountUserAccount_addUserAccount(randomUserAccount());
	}

	protected Long testDeleteAccountUserAccountByEmailAddress_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountUserAccountByEmailAddress()
		throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteAccountUserAccountByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountUserAccountByEmailAddress",
						new HashMap<String, Object>() {
							{
								put(
									"accountId",
									testGraphQLDeleteAccountUserAccountByEmailAddress_getAccountId());
								put(
									"emailAddress",
									"\"" + userAccount1.getEmailAddress() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountUserAccountByEmailAddress"));

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteAccountUserAccountByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountUserAccountByEmailAddress",
							new HashMap<String, Object>() {
								{
									put(
										"accountId",
										testGraphQLDeleteAccountUserAccountByEmailAddress_getAccountId());
									put(
										"emailAddress",
										"\"" + userAccount2.getEmailAddress() +
											"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountUserAccountByEmailAddress"));
	}

	protected Long
			testGraphQLDeleteAccountUserAccountByEmailAddress_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testGraphQLDeleteAccountUserAccountByEmailAddress_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteAccountUserAccountByExternalReferenceCodeByEmailAddressHttpResponse(
					testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
						userAccount),
					userAccount.getEmailAddress()));
	}

	protected UserAccount
			testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountUserAccountByExternalReferenceCodeByEmailAddress",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
											userAccount1) + "\"");
								put(
									"emailAddress",
									"\"" + userAccount1.getEmailAddress() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountUserAccountByExternalReferenceCodeByEmailAddress"));

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountUserAccountByExternalReferenceCodeByEmailAddress",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
												userAccount2) + "\"");
									put(
										"emailAddress",
										"\"" + userAccount2.getEmailAddress() +
											"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountUserAccountByExternalReferenceCodeByEmailAddress"));
	}

	protected String
			testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testGraphQLDeleteAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteAccountUserAccountsByEmailAddress() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAccountUserAccountsByEmailAddress_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteAccountUserAccountsByEmailAddressHttpResponse(
					testDeleteAccountUserAccountsByEmailAddress_getAccountId(),
					null));
	}

	protected UserAccount
			testDeleteAccountUserAccountsByEmailAddress_addUserAccount()
		throws Exception {

		return testPostAccountUserAccount_addUserAccount(randomUserAccount());
	}

	protected Long testDeleteAccountUserAccountsByEmailAddress_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountUserAccountsByEmailAddress()
		throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteAccountUserAccountsByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountUserAccountsByEmailAddress",
						new HashMap<String, Object>() {
							{
								put(
									"accountId",
									testGraphQLDeleteAccountUserAccountsByEmailAddress_getAccountId());
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountUserAccountsByEmailAddress"));

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteAccountUserAccountsByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountUserAccountsByEmailAddress",
							new HashMap<String, Object>() {
								{
									put(
										"accountId",
										testGraphQLDeleteAccountUserAccountsByEmailAddress_getAccountId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountUserAccountsByEmailAddress"));
	}

	protected Long
			testGraphQLDeleteAccountUserAccountsByEmailAddress_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testGraphQLDeleteAccountUserAccountsByEmailAddress_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteAccountUserAccountsByExternalReferenceCodeByEmailAddressHttpResponse(
					testDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
						userAccount),
					null));
	}

	protected UserAccount
			testDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress()
		throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
											userAccount1) + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress"));

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
												userAccount2) + "\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteAccountUserAccountsByExternalReferenceCodeByEmailAddress"));
	}

	protected String
			testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_getExternalReferenceCode(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testGraphQLDeleteAccountUserAccountsByExternalReferenceCodeByEmailAddress_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteUserAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount = testDeleteUserAccount_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.deleteUserAccountHttpResponse(
				userAccount.getId()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount.getId()));
		assertHttpResponseStatusCode(
			404, userAccountResource.getUserAccountHttpResponse(0L));
	}

	protected UserAccount testDeleteUserAccount_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteUserAccount() throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteUserAccount_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteUserAccount",
						new HashMap<String, Object>() {
							{
								put("userAccountId", userAccount1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteUserAccount"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"userAccount",
					new HashMap<String, Object>() {
						{
							put("userAccountId", userAccount1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteUserAccount_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteUserAccount",
							new HashMap<String, Object>() {
								{
									put("userAccountId", userAccount2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteUserAccount"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"userAccount",
						new HashMap<String, Object>() {
							{
								put("userAccountId", userAccount2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected UserAccount testGraphQLDeleteUserAccount_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testDeleteUserAccountBatch() throws Exception {
		UserAccount userAccount1 = testDeleteUserAccountBatch_addUserAccount();

		testDeleteUserAccountBatch_deleteUserAccount(
			202, userAccount1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount1.getId()));

		userAccount1 = testDeleteUserAccountBatch_addUserAccount();

		testDeleteUserAccountBatch_deleteUserAccount(
			202, null, userAccount1.getId());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount1.getId()));

		userAccount1 = testDeleteUserAccountBatch_addUserAccount();
		UserAccount userAccount2 = testDeleteUserAccountBatch_addUserAccount();

		testDeleteUserAccountBatch_deleteUserAccount(
			202, userAccount2.getExternalReferenceCode(), userAccount1.getId());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount1.getId()));
		assertHttpResponseStatusCode(
			200,
			userAccountResource.getUserAccountHttpResponse(
				userAccount2.getId()));

		testDeleteUserAccountBatch_deleteUserAccount(
			202, userAccount2.getExternalReferenceCode(), userAccount1.getId());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount2.getId()));
	}

	protected UserAccount testDeleteUserAccountBatch_addUserAccount()
		throws Exception {

		return testDeleteUserAccount_addUserAccount();
	}

	protected void testDeleteUserAccountBatch_deleteUserAccount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			userAccountResource.deleteUserAccountBatchHttpResponse(
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
	public void testDeleteUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteUserAccountByExternalReferenceCode_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteUserAccountByExternalReferenceCodeHttpResponse(
					userAccount.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getUserAccountByExternalReferenceCodeHttpResponse(
					userAccount.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getUserAccountByExternalReferenceCodeHttpResponse("-"));
	}

	protected UserAccount
			testDeleteUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteUserAccountByExternalReferenceCode()
		throws Exception {

		// No namespace

		UserAccount userAccount1 =
			testGraphQLDeleteUserAccountByExternalReferenceCode_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteUserAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										userAccount1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteUserAccountByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"userAccountByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + userAccount1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		UserAccount userAccount2 =
			testGraphQLDeleteUserAccountByExternalReferenceCode_addUserAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteUserAccountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											userAccount2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteUserAccountByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"userAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										userAccount2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected UserAccount
			testGraphQLDeleteUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		UserAccount postUserAccount =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount();

		UserAccount getUserAccount =
			userAccountResource.
				getAccountByExternalReferenceCodeUserAccountByExternalReferenceCode(
					testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					postUserAccount.getExternalReferenceCode());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		UserAccount userAccount =
			testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountByExternalReferenceCodeUserAccountByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"accountExternalReferenceCode",
											"\"" +
												testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
													"\"");
										put(
											"externalReferenceCode",
											"\"" +
												userAccount.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountByExternalReferenceCodeUserAccountByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"accountByExternalReferenceCodeUserAccountByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"accountExternalReferenceCode",
												"\"" +
													testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode() +
														"\"");
											put(
												"externalReferenceCode",
												"\"" +
													userAccount.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/accountByExternalReferenceCodeUserAccountByExternalReferenceCode"))));
	}

	protected String
			testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantAccountExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountByExternalReferenceCodeUserAccountByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"accountExternalReferenceCode",
									irrelevantAccountExternalReferenceCode);
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
							"accountByExternalReferenceCodeUserAccountByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"accountExternalReferenceCode",
										irrelevantAccountExternalReferenceCode);
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserAccount
			testGraphQLGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return testGraphQLAccountUserAccount_addUserAccount();
	}

	@Test
	public void testGetAccountUserAccount() throws Exception {
		UserAccount postUserAccount =
			testGetAccountUserAccount_addUserAccount();

		UserAccount getUserAccount = userAccountResource.getAccountUserAccount(
			testGetAccountUserAccount_getAccountId(), postUserAccount.getId());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testGetAccountUserAccount_addUserAccount()
		throws Exception {

		return testPostAccountUserAccount_addUserAccount(randomUserAccount());
	}

	protected Long testGetAccountUserAccount_getAccountId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountUserAccount() throws Exception {
		UserAccount userAccount =
			testGraphQLGetAccountUserAccount_addUserAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountUserAccount",
								new HashMap<String, Object>() {
									{
										put(
											"accountId",
											testGraphQLGetAccountUserAccount_getAccountId());
										put(
											"userAccountId",
											userAccount.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/accountUserAccount"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"accountUserAccount",
									new HashMap<String, Object>() {
										{
											put(
												"accountId",
												testGraphQLGetAccountUserAccount_getAccountId());
											put(
												"userAccountId",
												userAccount.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/accountUserAccount"))));
	}

	protected Long testGraphQLGetAccountUserAccount_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountUserAccountNotFound() throws Exception {
		Long irrelevantAccountId = RandomTestUtil.randomLong();
		Long irrelevantUserAccountId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountUserAccount",
						new HashMap<String, Object>() {
							{
								put("accountId", irrelevantAccountId);
								put("userAccountId", irrelevantUserAccountId);
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
							"accountUserAccount",
							new HashMap<String, Object>() {
								{
									put("accountId", irrelevantAccountId);
									put(
										"userAccountId",
										irrelevantUserAccountId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserAccount testGraphQLGetAccountUserAccount_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getIrrelevantExternalReferenceCode();

		Page<UserAccount> page =
			userAccountResource.
				getAccountUserAccountsByExternalReferenceCodePage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			UserAccount irrelevantUserAccount =
				testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantUserAccount());

			page =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetAccountUserAccountsByExternalReferenceCodePage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		UserAccount userAccount1 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		page =
			userAccountResource.
				getAccountUserAccountsByExternalReferenceCodePage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetAccountUserAccountsByExternalReferenceCodePage_getExpectedActions(
				externalReferenceCode));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountUserAccountsByExternalReferenceCodePage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", userAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithFilterStringContains()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithFilterStringEquals()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetAccountUserAccountsByExternalReferenceCodePageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode();

		UserAccount userAccount1 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, userAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode();

		Page<UserAccount> userAccountsPage =
			userAccountResource.
				getAccountUserAccountsByExternalReferenceCodePage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount3 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithSortDateTime()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithSortDouble()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithSortInteger()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountUserAccountsByExternalReferenceCodePageWithSortString()
		throws Exception {

		testGetAccountUserAccountsByExternalReferenceCodePageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetAccountUserAccountsByExternalReferenceCodePageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, UserAccount, UserAccount, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, userAccount1);

		userAccount2 =
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				externalReferenceCode, userAccount2);

		Page<UserAccount> page =
			userAccountResource.
				getAccountUserAccountsByExternalReferenceCodePage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.
					getAccountUserAccountsByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount
			testGetAccountUserAccountsByExternalReferenceCodePage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountUserAccountsByExternalReferenceCodePage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetAccountUserAccountsByExternalReferenceCodePage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountUserAccountsByExternalReferenceCodePage_getExternalReferenceCode();

		GraphQLField graphQLField = new GraphQLField(
			"accountUserAccountsByExternalReferenceCode",
			new HashMap<String, Object>() {
				{
					put(
						"externalReferenceCode",
						"\"" + externalReferenceCode + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject accountUserAccountsByExternalReferenceCodeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/accountUserAccountsByExternalReferenceCode");

		long totalCount =
			accountUserAccountsByExternalReferenceCodeJSONObject.getLong(
				"totalCount");

		UserAccount userAccount1 =
			testGraphQLGetAccountUserAccountsByExternalReferenceCodePageAccountUserAccount_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGraphQLGetAccountUserAccountsByExternalReferenceCodePageAccountUserAccount_addUserAccount(
				externalReferenceCode, randomUserAccount());

		accountUserAccountsByExternalReferenceCodeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/accountUserAccountsByExternalReferenceCode");

		Assert.assertEquals(
			totalCount + 2,
			accountUserAccountsByExternalReferenceCodeJSONObject.getLong(
				"totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		accountUserAccountsByExternalReferenceCodeJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(
					new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"JSONObject/accountUserAccountsByExternalReferenceCode");

		Assert.assertEquals(
			totalCount + 2,
			accountUserAccountsByExternalReferenceCodeJSONObject.getLong(
				"totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsByExternalReferenceCodeJSONObject.
						getString("items"))));
	}

	protected UserAccount
			testGraphQLGetAccountUserAccountsByExternalReferenceCodePageAccountUserAccount_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAccountUserAccountsPage() throws Exception {
		Long accountId = testGetAccountUserAccountsPage_getAccountId();
		Long irrelevantAccountId =
			testGetAccountUserAccountsPage_getIrrelevantAccountId();

		Page<UserAccount> page = userAccountResource.getAccountUserAccountsPage(
			accountId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountId != null) {
			UserAccount irrelevantUserAccount =
				testGetAccountUserAccountsPage_addUserAccount(
					irrelevantAccountId, randomIrrelevantUserAccount());

			page = userAccountResource.getAccountUserAccountsPage(
				irrelevantAccountId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetAccountUserAccountsPage_getExpectedActions(
					irrelevantAccountId));
		}

		UserAccount userAccount1 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		UserAccount userAccount2 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		page = userAccountResource.getAccountUserAccountsPage(
			accountId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page, testGetAccountUserAccountsPage_getExpectedActions(accountId));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountUserAccountsPage_getExpectedActions(Long accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/user-accounts/batch".
				replace("{accountId}", String.valueOf(accountId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAccountUserAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountUserAccountsPage_getAccountId();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 = testGetAccountUserAccountsPage_addUserAccount(
			accountId, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null,
					getFilterString(entityField, "between", userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetAccountUserAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountUserAccountsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountUserAccountsPageWithFilterStringContains()
		throws Exception {

		testGetAccountUserAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountUserAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetAccountUserAccountsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountUserAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountUserAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAccountUserAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountUserAccountsPage_getAccountId();

		UserAccount userAccount1 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null,
					getFilterString(entityField, operator, userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetAccountUserAccountsPageWithPagination()
		throws Exception {

		Long accountId = testGetAccountUserAccountsPage_getAccountId();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getAccountUserAccountsPage(
				accountId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		UserAccount userAccount2 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		UserAccount userAccount3 =
			testGetAccountUserAccountsPage_addUserAccount(
				accountId, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountUserAccountsPageWithSortDateTime()
		throws Exception {

		testGetAccountUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountUserAccountsPageWithSortDouble()
		throws Exception {

		testGetAccountUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountUserAccountsPageWithSortInteger()
		throws Exception {

		testGetAccountUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountUserAccountsPageWithSortString()
		throws Exception {

		testGetAccountUserAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAccountUserAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountUserAccountsPage_getAccountId();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetAccountUserAccountsPage_addUserAccount(
			accountId, userAccount1);

		userAccount2 = testGetAccountUserAccountsPage_addUserAccount(
			accountId, userAccount2);

		Page<UserAccount> page = userAccountResource.getAccountUserAccountsPage(
			accountId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getAccountUserAccountsPage(
					accountId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetAccountUserAccountsPage_addUserAccount(
			Long accountId, UserAccount userAccount)
		throws Exception {

		return userAccountResource.postAccountUserAccount(
			accountId, userAccount);
	}

	protected Long testGetAccountUserAccountsPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountUserAccountsPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetAccountUserAccountsPage() throws Exception {
		Long accountId = testGetAccountUserAccountsPage_getAccountId();

		GraphQLField graphQLField = new GraphQLField(
			"accountUserAccounts",
			new HashMap<String, Object>() {
				{
					put("accountId", accountId);
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject accountUserAccountsJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/accountUserAccounts");

		long totalCount = accountUserAccountsJSONObject.getLong("totalCount");

		UserAccount userAccount1 = testGraphQLAccountUserAccount_addUserAccount(
			accountId, randomUserAccount());

		UserAccount userAccount2 = testGraphQLAccountUserAccount_addUserAccount(
			accountId, randomUserAccount());

		accountUserAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/accountUserAccounts");

		Assert.assertEquals(
			totalCount + 2,
			accountUserAccountsJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		accountUserAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/accountUserAccounts");

		Assert.assertEquals(
			totalCount + 2,
			accountUserAccountsJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					accountUserAccountsJSONObject.getString("items"))));
	}

	@Test
	public void testGetMyUserAccount() throws Exception {
		UserAccount postUserAccount = testGetMyUserAccount_addUserAccount();

		UserAccount getUserAccount = userAccountResource.getMyUserAccount();

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testGetMyUserAccount_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetMyUserAccount() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGraphQLGetMyUserAccountNotFound() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getIrrelevantExternalReferenceCode();

		Page<UserAccount> page =
			userAccountResource.
				getOrganizationByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			UserAccount irrelevantUserAccount =
				testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantUserAccount());

			page =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		UserAccount userAccount1 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		page =
			userAccountResource.
				getOrganizationByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExpectedActions(
				externalReferenceCode));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", userAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilterStringContains()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetOrganizationByExternalReferenceCodeUserAccountsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();

		UserAccount userAccount1 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, userAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();

		Page<UserAccount> userAccountsPage =
			userAccountResource.
				getOrganizationByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount3 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSortDateTime()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSortDouble()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSortInteger()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSortString()
		throws Exception {

		testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetOrganizationByExternalReferenceCodeUserAccountsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, UserAccount, UserAccount, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, userAccount1);

		userAccount2 =
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, userAccount2);

		Page<UserAccount> page =
			userAccountResource.
				getOrganizationByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.
					getOrganizationByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeUserAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationUserAccountsPage() throws Exception {
		String organizationId =
			testGetOrganizationUserAccountsPage_getOrganizationId();
		String irrelevantOrganizationId =
			testGetOrganizationUserAccountsPage_getIrrelevantOrganizationId();

		Page<UserAccount> page =
			userAccountResource.getOrganizationUserAccountsPage(
				organizationId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantOrganizationId != null) {
			UserAccount irrelevantUserAccount =
				testGetOrganizationUserAccountsPage_addUserAccount(
					irrelevantOrganizationId, randomIrrelevantUserAccount());

			page = userAccountResource.getOrganizationUserAccountsPage(
				irrelevantOrganizationId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetOrganizationUserAccountsPage_getExpectedActions(
					irrelevantOrganizationId));
		}

		UserAccount userAccount1 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		UserAccount userAccount2 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		page = userAccountResource.getOrganizationUserAccountsPage(
			organizationId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetOrganizationUserAccountsPage_getExpectedActions(
				organizationId));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationUserAccountsPage_getExpectedActions(
				String organizationId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationId =
			testGetOrganizationUserAccountsPage_getOrganizationId();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 = testGetOrganizationUserAccountsPage_addUserAccount(
			organizationId, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null,
					getFilterString(entityField, "between", userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetOrganizationUserAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithFilterStringContains()
		throws Exception {

		testGetOrganizationUserAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetOrganizationUserAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetOrganizationUserAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetOrganizationUserAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationId =
			testGetOrganizationUserAccountsPage_getOrganizationId();

		UserAccount userAccount1 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null,
					getFilterString(entityField, operator, userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithPagination()
		throws Exception {

		String organizationId =
			testGetOrganizationUserAccountsPage_getOrganizationId();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getOrganizationUserAccountsPage(
				organizationId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		UserAccount userAccount2 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		UserAccount userAccount3 =
			testGetOrganizationUserAccountsPage_addUserAccount(
				organizationId, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithSortDateTime()
		throws Exception {

		testGetOrganizationUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithSortDouble()
		throws Exception {

		testGetOrganizationUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithSortInteger()
		throws Exception {

		testGetOrganizationUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetOrganizationUserAccountsPageWithSortString()
		throws Exception {

		testGetOrganizationUserAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetOrganizationUserAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String organizationId =
			testGetOrganizationUserAccountsPage_getOrganizationId();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetOrganizationUserAccountsPage_addUserAccount(
			organizationId, userAccount1);

		userAccount2 = testGetOrganizationUserAccountsPage_addUserAccount(
			organizationId, userAccount2);

		Page<UserAccount> page =
			userAccountResource.getOrganizationUserAccountsPage(
				organizationId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getOrganizationUserAccountsPage(
					organizationId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetOrganizationUserAccountsPage_addUserAccount(
			String organizationId, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetOrganizationUserAccountsPage_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationUserAccountsPage_getIrrelevantOrganizationId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetSiteAccountUserAccountSelected() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGetSiteUserAccountsPage() throws Exception {
		Long siteId = testGetSiteUserAccountsPage_getSiteId();
		Long irrelevantSiteId =
			testGetSiteUserAccountsPage_getIrrelevantSiteId();

		Page<UserAccount> page = userAccountResource.getSiteUserAccountsPage(
			siteId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantSiteId != null) {
			UserAccount irrelevantUserAccount =
				testGetSiteUserAccountsPage_addUserAccount(
					irrelevantSiteId, randomIrrelevantUserAccount());

			page = userAccountResource.getSiteUserAccountsPage(
				irrelevantSiteId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetSiteUserAccountsPage_getExpectedActions(
					irrelevantSiteId));
		}

		UserAccount userAccount1 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		UserAccount userAccount2 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		page = userAccountResource.getSiteUserAccountsPage(
			siteId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page, testGetSiteUserAccountsPage_getExpectedActions(siteId));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSiteUserAccountsPage_getExpectedActions(Long siteId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSiteUserAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteUserAccountsPage_getSiteId();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null,
					getFilterString(entityField, "between", userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetSiteUserAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetSiteUserAccountsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSiteUserAccountsPageWithFilterStringContains()
		throws Exception {

		testGetSiteUserAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteUserAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetSiteUserAccountsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSiteUserAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetSiteUserAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetSiteUserAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteUserAccountsPage_getSiteId();

		UserAccount userAccount1 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null,
					getFilterString(entityField, operator, userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetSiteUserAccountsPageWithPagination() throws Exception {
		Long siteId = testGetSiteUserAccountsPage_getSiteId();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getSiteUserAccountsPage(
				siteId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		UserAccount userAccount2 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		UserAccount userAccount3 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null, Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetSiteUserAccountsPageWithSortDateTime() throws Exception {
		testGetSiteUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSiteUserAccountsPageWithSortDouble() throws Exception {
		testGetSiteUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSiteUserAccountsPageWithSortInteger() throws Exception {
		testGetSiteUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSiteUserAccountsPageWithSortString() throws Exception {
		testGetSiteUserAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSiteUserAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long siteId = testGetSiteUserAccountsPage_getSiteId();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, userAccount1);

		userAccount2 = testGetSiteUserAccountsPage_addUserAccount(
			siteId, userAccount2);

		Page<UserAccount> page = userAccountResource.getSiteUserAccountsPage(
			siteId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getSiteUserAccountsPage(
					siteId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetSiteUserAccountsPage_addUserAccount(
			Long siteId, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetSiteUserAccountsPage_getSiteId() throws Exception {
		return testGroup.getGroupId();
	}

	protected Long testGetSiteUserAccountsPage_getIrrelevantSiteId()
		throws Exception {

		return irrelevantGroup.getGroupId();
	}

	@Test
	public void testGetUserAccount() throws Exception {
		UserAccount postUserAccount = testGetUserAccount_addUserAccount();

		UserAccount getUserAccount = userAccountResource.getUserAccount(
			postUserAccount.getId());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		UserAccount postUserAccount = testGetUserAccount_addUserAccount();

		UserAccount getUserAccount = userAccountResource.getUserAccount(
			postUserAccount.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.user.dto.v1_0.UserAccount"
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

		Object item = vulcanCRUDItemDelegate.getItem(postUserAccount.getId());

		assertEquals(getUserAccount, UserAccountSerDes.toDTO(item.toString()));
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

	protected UserAccount testGetUserAccount_addUserAccount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserAccount() throws Exception {
		UserAccount userAccount = testGraphQLGetUserAccount_addUserAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"userAccount",
								new HashMap<String, Object>() {
									{
										put(
											"userAccountId",
											userAccount.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/userAccount"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"userAccount",
									new HashMap<String, Object>() {
										{
											put(
												"userAccountId",
												userAccount.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/userAccount"))));
	}

	@Test
	public void testGraphQLGetUserAccountNotFound() throws Exception {
		Long irrelevantUserAccountId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"userAccount",
						new HashMap<String, Object>() {
							{
								put("userAccountId", irrelevantUserAccountId);
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
							"userAccount",
							new HashMap<String, Object>() {
								{
									put(
										"userAccountId",
										irrelevantUserAccountId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserAccount testGraphQLGetUserAccount_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testGetUserAccountByEmailAddress() throws Exception {
		UserAccount postUserAccount =
			testGetUserAccountByEmailAddress_addUserAccount();

		UserAccount getUserAccount =
			userAccountResource.getUserAccountByEmailAddress(
				postUserAccount.getEmailAddress());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testGetUserAccountByEmailAddress_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserAccountByEmailAddress() throws Exception {
		UserAccount userAccount =
			testGraphQLGetUserAccountByEmailAddress_addUserAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"userAccountByEmailAddress",
								new HashMap<String, Object>() {
									{
										put(
											"emailAddress",
											"\"" +
												userAccount.getEmailAddress() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/userAccountByEmailAddress"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"userAccountByEmailAddress",
									new HashMap<String, Object>() {
										{
											put(
												"emailAddress",
												"\"" +
													userAccount.
														getEmailAddress() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/userAccountByEmailAddress"))));
	}

	@Test
	public void testGraphQLGetUserAccountByEmailAddressNotFound()
		throws Exception {

		String irrelevantEmailAddress =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"userAccountByEmailAddress",
						new HashMap<String, Object>() {
							{
								put("emailAddress", irrelevantEmailAddress);
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
							"userAccountByEmailAddress",
							new HashMap<String, Object>() {
								{
									put("emailAddress", irrelevantEmailAddress);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected UserAccount
			testGraphQLGetUserAccountByEmailAddress_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testGetUserAccountByExternalReferenceCode() throws Exception {
		UserAccount postUserAccount =
			testGetUserAccountByExternalReferenceCode_addUserAccount();

		UserAccount getUserAccount =
			userAccountResource.getUserAccountByExternalReferenceCode(
				postUserAccount.getExternalReferenceCode());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount
			testGetUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserAccountByExternalReferenceCode()
		throws Exception {

		UserAccount userAccount =
			testGraphQLGetUserAccountByExternalReferenceCode_addUserAccount();

		// No namespace

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"userAccountByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												userAccount.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/userAccountByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				userAccount,
				UserAccountSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"userAccountByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													userAccount.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/userAccountByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetUserAccountByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"userAccountByExternalReferenceCode",
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
							"userAccountByExternalReferenceCode",
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

	protected UserAccount
			testGraphQLGetUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount();
	}

	@Test
	public void testGetUserAccountsByStatusPage() throws Exception {
		String status = testGetUserAccountsByStatusPage_getStatus();
		String irrelevantStatus =
			testGetUserAccountsByStatusPage_getIrrelevantStatus();

		Page<UserAccount> page =
			userAccountResource.getUserAccountsByStatusPage(
				status, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantStatus != null) {
			UserAccount irrelevantUserAccount =
				testGetUserAccountsByStatusPage_addUserAccount(
					irrelevantStatus, randomIrrelevantUserAccount());

			page = userAccountResource.getUserAccountsByStatusPage(
				irrelevantStatus, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetUserAccountsByStatusPage_getExpectedActions(
					irrelevantStatus));
		}

		UserAccount userAccount1 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		UserAccount userAccount2 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		page = userAccountResource.getUserAccountsByStatusPage(
			status, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page, testGetUserAccountsByStatusPage_getExpectedActions(status));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountsByStatusPage_getExpectedActions(String status)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUserAccountsByStatusPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String status = testGetUserAccountsByStatusPage_getStatus();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 = testGetUserAccountsByStatusPage_addUserAccount(
			status, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getUserAccountsByStatusPage(
					status, null,
					getFilterString(entityField, "between", userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserAccountsByStatusPageWithFilterDoubleEquals()
		throws Exception {

		testGetUserAccountsByStatusPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUserAccountsByStatusPageWithFilterStringContains()
		throws Exception {

		testGetUserAccountsByStatusPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserAccountsByStatusPageWithFilterStringEquals()
		throws Exception {

		testGetUserAccountsByStatusPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserAccountsByStatusPageWithFilterStringStartsWith()
		throws Exception {

		testGetUserAccountsByStatusPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetUserAccountsByStatusPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String status = testGetUserAccountsByStatusPage_getStatus();

		UserAccount userAccount1 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.getUserAccountsByStatusPage(
					status, null,
					getFilterString(entityField, operator, userAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserAccountsByStatusPageWithPagination()
		throws Exception {

		String status = testGetUserAccountsByStatusPage_getStatus();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getUserAccountsByStatusPage(
				status, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		UserAccount userAccount2 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		UserAccount userAccount3 =
			testGetUserAccountsByStatusPage_addUserAccount(
				status, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null, Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetUserAccountsByStatusPageWithSortDateTime()
		throws Exception {

		testGetUserAccountsByStatusPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUserAccountsByStatusPageWithSortDouble()
		throws Exception {

		testGetUserAccountsByStatusPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUserAccountsByStatusPageWithSortInteger()
		throws Exception {

		testGetUserAccountsByStatusPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUserAccountsByStatusPageWithSortString()
		throws Exception {

		testGetUserAccountsByStatusPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUserAccountsByStatusPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String status = testGetUserAccountsByStatusPage_getStatus();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetUserAccountsByStatusPage_addUserAccount(
			status, userAccount1);

		userAccount2 = testGetUserAccountsByStatusPage_addUserAccount(
			status, userAccount2);

		Page<UserAccount> page =
			userAccountResource.getUserAccountsByStatusPage(
				status, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getUserAccountsByStatusPage(
					status, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetUserAccountsByStatusPage_addUserAccount(
			String status, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetUserAccountsByStatusPage_getStatus()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetUserAccountsByStatusPage_getIrrelevantStatus()
		throws Exception {

		return null;
	}

	@Test
	public void testGraphQLGetUserAccountsByStatusPage() throws Exception {
		String status = testGetUserAccountsByStatusPage_getStatus();

		GraphQLField graphQLField = new GraphQLField(
			"userAccountsByStatus",
			new HashMap<String, Object>() {
				{
					put("status", "\"" + status + "\"");
					put("search", null);
					put("page", 1);
					put("pageSize", 10);
				}
			},
			new GraphQLField("items", getGraphQLFields()),
			new GraphQLField("page"), new GraphQLField("totalCount"));

		// No namespace

		JSONObject userAccountsByStatusJSONObject =
			JSONUtil.getValueAsJSONObject(
				invokeGraphQLQuery(graphQLField), "JSONObject/data",
				"JSONObject/userAccountsByStatus");

		long totalCount = userAccountsByStatusJSONObject.getLong("totalCount");

		UserAccount userAccount1 =
			testGraphQLGetUserAccountsByStatusPageUserAccount_addUserAccount(
				status, randomUserAccount());

		UserAccount userAccount2 =
			testGraphQLGetUserAccountsByStatusPageUserAccount_addUserAccount(
				status, randomUserAccount());

		userAccountsByStatusJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userAccountsByStatus");

		Assert.assertEquals(
			totalCount + 2,
			userAccountsByStatusJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsByStatusJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsByStatusJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		userAccountsByStatusJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/userAccountsByStatus");

		Assert.assertEquals(
			totalCount + 2,
			userAccountsByStatusJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsByStatusJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsByStatusJSONObject.getString("items"))));
	}

	protected UserAccount
			testGraphQLGetUserAccountsByStatusPageUserAccount_addUserAccount(
				String status, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetUserAccountsPage() throws Exception {
		Page<UserAccount> page = userAccountResource.getUserAccountsPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		UserAccount userAccount1 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		UserAccount userAccount2 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		page = userAccountResource.getUserAccountsPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(page, testGetUserAccountsPage_getExpectedActions());

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountsPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUserAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 = testGetUserAccountsPage_addUserAccount(userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page = userAccountResource.getUserAccountsPage(
				null, getFilterString(entityField, "between", userAccount1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetUserAccountsPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUserAccountsPageWithFilterStringContains()
		throws Exception {

		testGetUserAccountsPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetUserAccountsPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetUserAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetUserAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		UserAccount userAccount1 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page = userAccountResource.getUserAccountsPage(
				null, getFilterString(entityField, operator, userAccount1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserAccountsPageWithPagination() throws Exception {
		Page<UserAccount> userAccountsPage =
			userAccountResource.getUserAccountsPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		UserAccount userAccount2 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		UserAccount userAccount3 = testGetUserAccountsPage_addUserAccount(
			randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 = userAccountResource.getUserAccountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 = userAccountResource.getUserAccountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 = userAccountResource.getUserAccountsPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 = userAccountResource.getUserAccountsPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 = userAccountResource.getUserAccountsPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 = userAccountResource.getUserAccountsPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetUserAccountsPageWithSortDateTime() throws Exception {
		testGetUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUserAccountsPageWithSortDouble() throws Exception {
		testGetUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUserAccountsPageWithSortInteger() throws Exception {
		testGetUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUserAccountsPageWithSortString() throws Exception {
		testGetUserAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUserAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetUserAccountsPage_addUserAccount(userAccount1);

		userAccount2 = testGetUserAccountsPage_addUserAccount(userAccount2);

		Page<UserAccount> page = userAccountResource.getUserAccountsPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage = userAccountResource.getUserAccountsPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getUserAccountsPage(
					null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetUserAccountsPage_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetUserAccountsPage() throws Exception {
		GraphQLField graphQLField = new GraphQLField(
			"userAccounts",
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

		JSONObject userAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userAccounts");

		long totalCount = userAccountsJSONObject.getLong("totalCount");

		UserAccount userAccount1 = testGraphQLUserAccount_addUserAccount(
			randomUserAccount());

		UserAccount userAccount2 = testGraphQLUserAccount_addUserAccount(
			randomUserAccount());

		userAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(graphQLField), "JSONObject/data",
			"JSONObject/userAccounts");

		Assert.assertEquals(
			totalCount + 2, userAccountsJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));

		// Using the namespace headlessAdminUser_v1_0

		userAccountsJSONObject = JSONUtil.getValueAsJSONObject(
			invokeGraphQLQuery(
				new GraphQLField("headlessAdminUser_v1_0", graphQLField)),
			"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
			"JSONObject/userAccounts");

		Assert.assertEquals(
			totalCount + 2, userAccountsJSONObject.getLong("totalCount"));

		assertContains(
			userAccount1,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));
		assertContains(
			userAccount2,
			Arrays.asList(
				UserAccountSerDes.toDTOs(
					userAccountsJSONObject.getString("items"))));
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPage()
		throws Exception {

		String externalReferenceCode =
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetUserGroupByExternalReferenceCodeUsersPage_getIrrelevantExternalReferenceCode();

		Page<UserAccount> page =
			userAccountResource.getUserGroupByExternalReferenceCodeUsersPage(
				externalReferenceCode, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			UserAccount irrelevantUserAccount =
				testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantUserAccount());

			page =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetUserGroupByExternalReferenceCodeUsersPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		UserAccount userAccount1 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		page = userAccountResource.getUserGroupByExternalReferenceCodeUsersPage(
			externalReferenceCode, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetUserGroupByExternalReferenceCodeUsersPage_getExpectedActions(
				externalReferenceCode));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserGroupByExternalReferenceCodeUsersPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", userAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithFilterDoubleEquals()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithFilterStringContains()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithFilterStringEquals()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithFilterStringStartsWith()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetUserGroupByExternalReferenceCodeUsersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode();

		UserAccount userAccount1 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, userAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getUserGroupByExternalReferenceCodeUsersPage(
				externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount3 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithSortDateTime()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithSortDouble()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithSortInteger()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUserGroupByExternalReferenceCodeUsersPageWithSortString()
		throws Exception {

		testGetUserGroupByExternalReferenceCodeUsersPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUserGroupByExternalReferenceCodeUsersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, userAccount1);

		userAccount2 =
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				externalReferenceCode, userAccount2);

		Page<UserAccount> page =
			userAccountResource.getUserGroupByExternalReferenceCodeUsersPage(
				externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.
					getUserGroupByExternalReferenceCodeUsersPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount
			testGetUserGroupByExternalReferenceCodeUsersPage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserGroupByExternalReferenceCodeUsersPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserGroupByExternalReferenceCodeUsersPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserGroupUsersPage() throws Exception {
		Long userGroupId = testGetUserGroupUsersPage_getUserGroupId();
		Long irrelevantUserGroupId =
			testGetUserGroupUsersPage_getIrrelevantUserGroupId();

		Page<UserAccount> page = userAccountResource.getUserGroupUsersPage(
			userGroupId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantUserGroupId != null) {
			UserAccount irrelevantUserAccount =
				testGetUserGroupUsersPage_addUserAccount(
					irrelevantUserGroupId, randomIrrelevantUserAccount());

			page = userAccountResource.getUserGroupUsersPage(
				irrelevantUserGroupId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetUserGroupUsersPage_getExpectedActions(
					irrelevantUserGroupId));
		}

		UserAccount userAccount1 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		UserAccount userAccount2 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		page = userAccountResource.getUserGroupUsersPage(
			userGroupId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page, testGetUserGroupUsersPage_getExpectedActions(userGroupId));

		userAccountResource.deleteUserAccount(userAccount1.getId());

		userAccountResource.deleteUserAccount(userAccount2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserGroupUsersPage_getExpectedActions(Long userGroupId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUserGroupUsersPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long userGroupId = testGetUserGroupUsersPage_getUserGroupId();

		UserAccount userAccount1 = randomUserAccount();

		userAccount1 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, userAccount1);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page = userAccountResource.getUserGroupUsersPage(
				userGroupId, null,
				getFilterString(entityField, "between", userAccount1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserGroupUsersPageWithFilterDoubleEquals()
		throws Exception {

		testGetUserGroupUsersPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUserGroupUsersPageWithFilterStringContains()
		throws Exception {

		testGetUserGroupUsersPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserGroupUsersPageWithFilterStringEquals()
		throws Exception {

		testGetUserGroupUsersPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUserGroupUsersPageWithFilterStringStartsWith()
		throws Exception {

		testGetUserGroupUsersPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetUserGroupUsersPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long userGroupId = testGetUserGroupUsersPage_getUserGroupId();

		UserAccount userAccount1 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount2 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		for (EntityField entityField : entityFields) {
			Page<UserAccount> page = userAccountResource.getUserGroupUsersPage(
				userGroupId, null,
				getFilterString(entityField, operator, userAccount1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(userAccount1),
				(List<UserAccount>)page.getItems());
		}
	}

	@Test
	public void testGetUserGroupUsersPageWithPagination() throws Exception {
		Long userGroupId = testGetUserGroupUsersPage_getUserGroupId();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		UserAccount userAccount2 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		UserAccount userAccount3 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 = userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 = userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 = userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 = userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null, Pagination.of(1, totalCount + 2),
				null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 = userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null, Pagination.of(2, totalCount + 2),
				null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 = userAccountResource.getUserGroupUsersPage(
				userGroupId, null, null, Pagination.of(1, (int)totalCount + 3),
				null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetUserGroupUsersPageWithSortDateTime() throws Exception {
		testGetUserGroupUsersPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUserGroupUsersPageWithSortDouble() throws Exception {
		testGetUserGroupUsersPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUserGroupUsersPageWithSortInteger() throws Exception {
		testGetUserGroupUsersPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUserGroupUsersPageWithSortString() throws Exception {
		testGetUserGroupUsersPageWithSort(
			EntityField.Type.STRING,
			(entityField, userAccount1, userAccount2) -> {
				Class<?> clazz = userAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						userAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						userAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUserGroupUsersPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long userGroupId = testGetUserGroupUsersPage_getUserGroupId();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, userAccount1);

		userAccount2 = testGetUserGroupUsersPage_addUserAccount(
			userGroupId, userAccount2);

		Page<UserAccount> page = userAccountResource.getUserGroupUsersPage(
			userGroupId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.getUserGroupUsersPage(
					userGroupId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getUserGroupUsersPage(
					userGroupId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetUserGroupUsersPage_addUserAccount(
			Long userGroupId, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserGroupUsersPage_getUserGroupId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserGroupUsersPage_getIrrelevantUserGroupId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchSiteAccountUserAccountSelected() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testPatchSiteAccountUserAccountSelected_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.patchSiteAccountUserAccountSelectedHttpResponse(
				testPatchSiteAccountUserAccountSelected_getSiteId(),
				testPatchSiteAccountUserAccountSelected_getAccountId(),
				userAccount.getId()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.patchSiteAccountUserAccountSelectedHttpResponse(
				testPatchSiteAccountUserAccountSelected_getSiteId(),
				testPatchSiteAccountUserAccountSelected_getAccountId(), 0L));
	}

	protected Long testPatchSiteAccountUserAccountSelected_getSiteId()
		throws Exception {

		return testGroup.getGroupId();
	}

	protected Long testPatchSiteAccountUserAccountSelected_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testPatchSiteAccountUserAccountSelected_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelectedHttpResponse(
					testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_getFriendlyUrlPath(),
					testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_getAccountExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				patchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelectedHttpResponse(
					testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_getFriendlyUrlPath(),
					testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_getAccountExternalReferenceCode(),
					"-"));
	}

	protected String
			testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_getFriendlyUrlPath()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testPatchSiteByFriendlyUrlPathAccountByExternalReferenceCodeAccountExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeSelected_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchUserAccount() throws Exception {
		UserAccount postUserAccount = testPatchUserAccount_addUserAccount();

		UserAccount randomPatchUserAccount = randomPatchUserAccount();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount patchUserAccount = userAccountResource.patchUserAccount(
			postUserAccount.getId(), randomPatchUserAccount);

		UserAccount expectedPatchUserAccount = postUserAccount.clone();

		BeanTestUtil.copyProperties(
			randomPatchUserAccount, expectedPatchUserAccount);

		UserAccount getUserAccount = userAccountResource.getUserAccount(
			patchUserAccount.getId());

		assertEquals(expectedPatchUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testPatchUserAccount_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchUserAccountByExternalReferenceCode() throws Exception {
		UserAccount postUserAccount =
			testPatchUserAccountByExternalReferenceCode_addUserAccount();

		UserAccount randomPatchUserAccount = randomPatchUserAccount();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount patchUserAccount =
			userAccountResource.patchUserAccountByExternalReferenceCode(
				postUserAccount.getExternalReferenceCode(),
				randomPatchUserAccount);

		UserAccount expectedPatchUserAccount = postUserAccount.clone();

		BeanTestUtil.copyProperties(
			randomPatchUserAccount, expectedPatchUserAccount);

		UserAccount getUserAccount =
			userAccountResource.getUserAccountByExternalReferenceCode(
				patchUserAccount.getExternalReferenceCode());

		assertEquals(expectedPatchUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount
			testPatchUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				postAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				postAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					"-"));
	}

	protected String
			testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testPostAccountByExternalReferenceCodeUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountUserAccount() throws Exception {
		UserAccount randomUserAccount = randomUserAccount();

		UserAccount postUserAccount = testPostAccountUserAccount_addUserAccount(
			randomUserAccount);

		assertEquals(randomUserAccount, postUserAccount);
		assertValid(postUserAccount);
	}

	protected UserAccount testPostAccountUserAccount_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		return userAccountResource.postAccountUserAccount(
			testGetAccountUserAccountsPage_getAccountId(), userAccount);
	}

	@Test
	public void testGraphQLPostAccountUserAccount() throws Exception {
		UserAccount randomUserAccount = randomUserAccount();

		UserAccount userAccount = testGraphQLAccountUserAccount_addUserAccount(
			testGraphQLPostAccountUserAccount_getAccountId(),
			randomUserAccount);

		Assert.assertTrue(equals(randomUserAccount, userAccount));
	}

	protected Long testGraphQLPostAccountUserAccount_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountUserAccountByEmailAddress() throws Exception {
		UserAccount randomUserAccount = randomUserAccount();

		UserAccount postUserAccount =
			testPostAccountUserAccountByEmailAddress_addUserAccount(
				randomUserAccount);

		assertEquals(randomUserAccount, postUserAccount);
		assertValid(postUserAccount);
	}

	protected UserAccount
			testPostAccountUserAccountByEmailAddress_addUserAccount(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostAccountUserAccountByEmailAddress()
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount userAccount = testGraphQLUserAccount_addUserAccount(
			randomUserAccount);

		Assert.assertTrue(equals(randomUserAccount, userAccount));
	}

	@Test
	public void testPostAccountUserAccountByExternalReferenceCode()
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount postUserAccount =
			testPostAccountUserAccountByExternalReferenceCode_addUserAccount(
				randomUserAccount);

		assertEquals(randomUserAccount, postUserAccount);
		assertValid(postUserAccount);
	}

	protected UserAccount
			testPostAccountUserAccountByExternalReferenceCode_addUserAccount(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostAccountUserAccountByExternalReferenceCode()
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount userAccount = testGraphQLAccountUserAccount_addUserAccount(
			testGraphQLPostAccountUserAccountByExternalReferenceCode_getAccountId(),
			randomUserAccount);

		Assert.assertTrue(equals(randomUserAccount, userAccount));
	}

	protected Long
			testGraphQLPostAccountUserAccountByExternalReferenceCode_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount postUserAccount =
			testPostAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount(
				randomUserAccount);

		assertEquals(randomUserAccount, postUserAccount);
		assertValid(postUserAccount);
	}

	protected UserAccount
			testPostAccountUserAccountByExternalReferenceCodeByEmailAddress_addUserAccount(
				UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostAccountUserAccountByExternalReferenceCodeByEmailAddress()
		throws Exception {

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount userAccount = testGraphQLUserAccount_addUserAccount(
			randomUserAccount);

		Assert.assertTrue(equals(randomUserAccount, userAccount));
	}

	@Test
	public void testPostAccountUserAccountsByEmailAddress() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostAccountUserAccountsByExternalReferenceCodeByEmailAddress()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostUserAccount() throws Exception {
		UserAccount randomUserAccount = randomUserAccount();

		UserAccount postUserAccount = testPostUserAccount_addUserAccount(
			randomUserAccount);

		assertEquals(randomUserAccount, postUserAccount);
		assertValid(postUserAccount);
	}

	protected UserAccount testPostUserAccount_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLPostUserAccount() throws Exception {
		UserAccount randomUserAccount = randomUserAccount();

		UserAccount userAccount = testGraphQLUserAccount_addUserAccount(
			randomUserAccount);

		Assert.assertTrue(equals(randomUserAccount, userAccount));
	}

	@Test
	public void testPostUserAccountImage() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPutUserAccount() throws Exception {
		UserAccount postUserAccount = testPutUserAccount_addUserAccount();

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount putUserAccount = userAccountResource.putUserAccount(
			postUserAccount.getId(), randomUserAccount);

		assertEquals(randomUserAccount, putUserAccount);
		assertValid(putUserAccount);

		UserAccount getUserAccount = userAccountResource.getUserAccount(
			putUserAccount.getId());

		assertEquals(randomUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testPutUserAccount_addUserAccount() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutUserAccountByExternalReferenceCode() throws Exception {
		UserAccount postUserAccount =
			testPutUserAccountByExternalReferenceCode_addUserAccount();

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount putUserAccount =
			userAccountResource.putUserAccountByExternalReferenceCode(
				postUserAccount.getExternalReferenceCode(), randomUserAccount);

		assertEquals(randomUserAccount, putUserAccount);
		assertValid(putUserAccount);

		UserAccount getUserAccount =
			userAccountResource.getUserAccountByExternalReferenceCode(
				putUserAccount.getExternalReferenceCode());

		assertEquals(randomUserAccount, getUserAccount);
		assertValid(getUserAccount);

		UserAccount newUserAccount =
			testPutUserAccountByExternalReferenceCode_createUserAccount();

		putUserAccount =
			userAccountResource.putUserAccountByExternalReferenceCode(
				newUserAccount.getExternalReferenceCode(), newUserAccount);

		assertEquals(newUserAccount, putUserAccount);
		assertValid(putUserAccount);

		getUserAccount =
			userAccountResource.getUserAccountByExternalReferenceCode(
				putUserAccount.getExternalReferenceCode());

		assertEquals(newUserAccount, getUserAccount);

		Assert.assertEquals(
			newUserAccount.getExternalReferenceCode(),
			putUserAccount.getExternalReferenceCode());
	}

	protected UserAccount
			testPutUserAccountByExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount
			testPutUserAccountByExternalReferenceCode_createUserAccount()
		throws Exception {

		return randomUserAccount();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		UserAccount userAccount1 =
			testBatchEngineDeleteImportTask_addUserAccount();

		testBatchEngineDeleteImportTask_deleteUserAccount(
			200, userAccount1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount1.getId()));

		userAccount1 = testBatchEngineDeleteImportTask_addUserAccount();

		testBatchEngineDeleteImportTask_deleteUserAccount(
			200, null, userAccount1.getId());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount1.getId()));

		userAccount1 = testBatchEngineDeleteImportTask_addUserAccount();
		UserAccount userAccount2 =
			testBatchEngineDeleteImportTask_addUserAccount();

		testBatchEngineDeleteImportTask_deleteUserAccount(
			200, userAccount2.getExternalReferenceCode(), userAccount1.getId());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount1.getId()));
		assertHttpResponseStatusCode(
			200,
			userAccountResource.getUserAccountHttpResponse(
				userAccount2.getId()));

		testBatchEngineDeleteImportTask_deleteUserAccount(
			200, userAccount2.getExternalReferenceCode(), userAccount1.getId());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getUserAccountHttpResponse(
				userAccount2.getId()));
	}

	protected UserAccount testBatchEngineDeleteImportTask_addUserAccount()
		throws Exception {

		return testDeleteUserAccount_addUserAccount();
	}

	protected void testBatchEngineDeleteImportTask_deleteUserAccount(
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
				"com.liferay.headless.admin.user.dto.v1_0.UserAccount", null,
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

	protected UserAccount testGraphQLAccountUserAccount_addUserAccount()
		throws Exception {

		return testGraphQLAccountUserAccount_addUserAccount(
			testGraphQLAccountUserAccount_getAccountId(), randomUserAccount());
	}

	protected Long testGraphQLAccountUserAccount_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected UserAccount testGraphQLAccountUserAccount_addUserAccount(
			Long accountId, UserAccount userAccount)
		throws Exception {

		JSONDeserializer<UserAccount> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(UserAccount.class)) {

			if (getGraphQLValue(field.get(userAccount)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(userAccount)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createAccountUserAccount",
						new HashMap<String, Object>() {
							{
								put("accountId", accountId);
								put("userAccount", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createAccountUserAccount"),
			UserAccount.class);
	}

	protected UserAccount testGraphQLUserAccount_addUserAccount()
		throws Exception {

		return testGraphQLUserAccount_addUserAccount(randomUserAccount());
	}

	protected UserAccount testGraphQLUserAccount_addUserAccount(
			UserAccount userAccount)
		throws Exception {

		JSONDeserializer<UserAccount> jsonDeserializer =
			JSONFactoryUtil.createJSONDeserializer();

		StringBuilder sb = new StringBuilder("{");

		for (java.lang.reflect.Field field :
				getDeclaredFields(UserAccount.class)) {

			if (getGraphQLValue(field.get(userAccount)) != null) {
				if (sb.length() > 1) {
					sb.append(", ");
				}

				sb.append(field.getName());
				sb.append(": ");
				sb.append(getGraphQLValue(field.get(userAccount)));
			}
		}

		sb.append("}");

		List<GraphQLField> graphQLFields = getGraphQLFields();

		return jsonDeserializer.deserialize(
			JSONUtil.getValueAsString(
				invokeGraphQLMutation(
					new GraphQLField(
						"createUserAccount",
						new HashMap<String, Object>() {
							{
								put("userAccount", sb.toString());
							}
						},
						graphQLFields)),
				"JSONObject/data", "JSONObject/createUserAccount"),
			UserAccount.class);
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
		UserAccount userAccount, List<UserAccount> userAccounts) {

		boolean contains = false;

		for (UserAccount item : userAccounts) {
			if (equals(userAccount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			userAccounts + " does not contain " + userAccount, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		UserAccount userAccount1, UserAccount userAccount2) {

		Assert.assertTrue(
			userAccount1 + " does not equal " + userAccount2,
			equals(userAccount1, userAccount2));
	}

	protected void assertEquals(
		List<UserAccount> userAccounts1, List<UserAccount> userAccounts2) {

		Assert.assertEquals(userAccounts1.size(), userAccounts2.size());

		for (int i = 0; i < userAccounts1.size(); i++) {
			UserAccount userAccount1 = userAccounts1.get(i);
			UserAccount userAccount2 = userAccounts2.get(i);

			assertEquals(userAccount1, userAccount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<UserAccount> userAccounts1, List<UserAccount> userAccounts2) {

		Assert.assertEquals(userAccounts1.size(), userAccounts2.size());

		for (UserAccount userAccount1 : userAccounts1) {
			boolean contains = false;

			for (UserAccount userAccount2 : userAccounts2) {
				if (equals(userAccount1, userAccount2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				userAccounts2 + " does not contain " + userAccount1, contains);
		}
	}

	protected void assertValid(UserAccount userAccount) throws Exception {
		boolean valid = true;

		if (userAccount.getDateCreated() == null) {
			valid = false;
		}

		if (userAccount.getDateModified() == null) {
			valid = false;
		}

		if (userAccount.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountBriefs", additionalAssertFieldName)) {
				if (userAccount.getAccountBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (userAccount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("additionalName", additionalAssertFieldName)) {
				if (userAccount.getAdditionalName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("alternateName", additionalAssertFieldName)) {
				if (userAccount.getAlternateName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"assetLibraryBriefs", additionalAssertFieldName)) {

				if (userAccount.getAssetLibraryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("birthDate", additionalAssertFieldName)) {
				if (userAccount.getBirthDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (userAccount.getCreator() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("currentPassword", additionalAssertFieldName)) {
				if (userAccount.getCurrentPassword() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (userAccount.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("dashboardURL", additionalAssertFieldName)) {
				if (userAccount.getDashboardURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("emailAddress", additionalAssertFieldName)) {
				if (userAccount.getEmailAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (userAccount.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("familyName", additionalAssertFieldName)) {
				if (userAccount.getFamilyName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("gender", additionalAssertFieldName)) {
				if (userAccount.getGender() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("givenName", additionalAssertFieldName)) {
				if (userAccount.getGivenName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("hasLoginDate", additionalAssertFieldName)) {
				if (userAccount.getHasLoginDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("honorificPrefix", additionalAssertFieldName)) {
				if (userAccount.getHonorificPrefix() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("honorificSuffix", additionalAssertFieldName)) {
				if (userAccount.getHonorificSuffix() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("image", additionalAssertFieldName)) {
				if (userAccount.getImage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"imageExternalReferenceCode", additionalAssertFieldName)) {

				if (userAccount.getImageExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("imageId", additionalAssertFieldName)) {
				if (userAccount.getImageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("jobTitle", additionalAssertFieldName)) {
				if (userAccount.getJobTitle() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (userAccount.getKeywords() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"languageDisplayName", additionalAssertFieldName)) {

				if (userAccount.getLanguageDisplayName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("languageId", additionalAssertFieldName)) {
				if (userAccount.getLanguageId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("lastLoginDate", additionalAssertFieldName)) {
				if (userAccount.getLastLoginDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (userAccount.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"organizationBriefs", additionalAssertFieldName)) {

				if (userAccount.getOrganizationBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("password", additionalAssertFieldName)) {
				if (userAccount.getPassword() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (userAccount.getPermissions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("profileURL", additionalAssertFieldName)) {
				if (userAccount.getProfileURL() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roleBriefs", additionalAssertFieldName)) {
				if (userAccount.getRoleBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("siteBriefs", additionalAssertFieldName)) {
				if (userAccount.getSiteBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (userAccount.getStatus() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (userAccount.getTaxonomyCategoryBriefs() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"userAccountContactInformation",
					additionalAssertFieldName)) {

				if (userAccount.getUserAccountContactInformation() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userGroupBriefs", additionalAssertFieldName)) {
				if (userAccount.getUserGroupBriefs() == null) {
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

	protected void assertValid(Page<UserAccount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<UserAccount> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<UserAccount> userAccounts = page.getItems();

		int size = userAccounts.size();

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
					com.liferay.headless.admin.user.dto.v1_0.UserAccount.
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
		UserAccount userAccount1, UserAccount userAccount2) {

		if (userAccount1 == userAccount2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getAccountBriefs(),
						userAccount2.getAccountBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)userAccount1.getActions(),
						(Map)userAccount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("additionalName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getAdditionalName(),
						userAccount2.getAdditionalName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("alternateName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getAlternateName(),
						userAccount2.getAlternateName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"assetLibraryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getAssetLibraryBriefs(),
						userAccount2.getAssetLibraryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("birthDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getBirthDate(),
						userAccount2.getBirthDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("creator", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getCreator(), userAccount2.getCreator())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("currentPassword", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getCurrentPassword(),
						userAccount2.getCurrentPassword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getCustomFields(),
						userAccount2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dashboardURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getDashboardURL(),
						userAccount2.getDashboardURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateCreated", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getDateCreated(),
						userAccount2.getDateCreated())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("dateModified", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getDateModified(),
						userAccount2.getDateModified())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("emailAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getEmailAddress(),
						userAccount2.getEmailAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getExternalReferenceCode(),
						userAccount2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("familyName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getFamilyName(),
						userAccount2.getFamilyName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("gender", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getGender(), userAccount2.getGender())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("givenName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getGivenName(),
						userAccount2.getGivenName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("hasLoginDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getHasLoginDate(),
						userAccount2.getHasLoginDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("honorificPrefix", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getHonorificPrefix(),
						userAccount2.getHonorificPrefix())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("honorificSuffix", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getHonorificSuffix(),
						userAccount2.getHonorificSuffix())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getId(), userAccount2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("image", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getImage(), userAccount2.getImage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"imageExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getImageExternalReferenceCode(),
						userAccount2.getImageExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("imageId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getImageId(), userAccount2.getImageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("jobTitle", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getJobTitle(),
						userAccount2.getJobTitle())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("keywords", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getKeywords(),
						userAccount2.getKeywords())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"languageDisplayName", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getLanguageDisplayName(),
						userAccount2.getLanguageDisplayName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("languageId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getLanguageId(),
						userAccount2.getLanguageId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("lastLoginDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getLastLoginDate(),
						userAccount2.getLastLoginDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getName(), userAccount2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"organizationBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getOrganizationBriefs(),
						userAccount2.getOrganizationBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("password", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getPassword(),
						userAccount2.getPassword())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("permissions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getPermissions(),
						userAccount2.getPermissions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("profileURL", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getProfileURL(),
						userAccount2.getProfileURL())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roleBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getRoleBriefs(),
						userAccount2.getRoleBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("siteBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getSiteBriefs(),
						userAccount2.getSiteBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("status", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getStatus(), userAccount2.getStatus())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"taxonomyCategoryBriefs", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getTaxonomyCategoryBriefs(),
						userAccount2.getTaxonomyCategoryBriefs())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"userAccountContactInformation",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getUserAccountContactInformation(),
						userAccount2.getUserAccountContactInformation())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userGroupBriefs", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getUserGroupBriefs(),
						userAccount2.getUserGroupBriefs())) {

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

		if (!(_userAccountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_userAccountResource;

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
		EntityField entityField, String operator, UserAccount userAccount) {

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

		if (entityFieldName.equals("additionalName")) {
			Object object = userAccount.getAdditionalName();

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

		if (entityFieldName.equals("alternateName")) {
			Object object = userAccount.getAlternateName();

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

		if (entityFieldName.equals("assetLibraryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("birthDate")) {
			if (operator.equals("between")) {
				Date date = userAccount.getBirthDate();

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

				sb.append(_format.format(userAccount.getBirthDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("creator")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("currentPassword")) {
			Object object = userAccount.getCurrentPassword();

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

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("dashboardURL")) {
			Object object = userAccount.getDashboardURL();

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
				Date date = userAccount.getDateCreated();

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

				sb.append(_format.format(userAccount.getDateCreated()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("dateModified")) {
			if (operator.equals("between")) {
				Date date = userAccount.getDateModified();

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

				sb.append(_format.format(userAccount.getDateModified()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("emailAddress")) {
			Object object = userAccount.getEmailAddress();

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
			Object object = userAccount.getExternalReferenceCode();

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

		if (entityFieldName.equals("familyName")) {
			Object object = userAccount.getFamilyName();

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

		if (entityFieldName.equals("gender")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("givenName")) {
			Object object = userAccount.getGivenName();

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

		if (entityFieldName.equals("hasLoginDate")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("honorificPrefix")) {
			Object object = userAccount.getHonorificPrefix();

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

		if (entityFieldName.equals("honorificSuffix")) {
			Object object = userAccount.getHonorificSuffix();

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

		if (entityFieldName.equals("image")) {
			Object object = userAccount.getImage();

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

		if (entityFieldName.equals("imageExternalReferenceCode")) {
			Object object = userAccount.getImageExternalReferenceCode();

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

		if (entityFieldName.equals("imageId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("jobTitle")) {
			Object object = userAccount.getJobTitle();

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

		if (entityFieldName.equals("keywords")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("languageDisplayName")) {
			Object object = userAccount.getLanguageDisplayName();

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

		if (entityFieldName.equals("languageId")) {
			Object object = userAccount.getLanguageId();

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

		if (entityFieldName.equals("lastLoginDate")) {
			if (operator.equals("between")) {
				Date date = userAccount.getLastLoginDate();

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

				sb.append(_format.format(userAccount.getLastLoginDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = userAccount.getName();

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

		if (entityFieldName.equals("organizationBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("password")) {
			Object object = userAccount.getPassword();

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

		if (entityFieldName.equals("profileURL")) {
			Object object = userAccount.getProfileURL();

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

		if (entityFieldName.equals("roleBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("siteBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("status")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("taxonomyCategoryBriefs")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userAccountContactInformation")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("userGroupBriefs")) {
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

	protected UserAccount randomUserAccount() throws Exception {
		return new UserAccount() {
			{
				additionalName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				alternateName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				birthDate = RandomTestUtil.nextDate();
				currentPassword = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dashboardURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				dateCreated = RandomTestUtil.nextDate();
				dateModified = RandomTestUtil.nextDate();
				emailAddress =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				familyName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				givenName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hasLoginDate = RandomTestUtil.randomBoolean();
				honorificPrefix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				honorificSuffix = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				image = StringUtil.toLowerCase(RandomTestUtil.randomString());
				imageExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				imageId = RandomTestUtil.randomLong();
				jobTitle = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				languageDisplayName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				languageId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				lastLoginDate = RandomTestUtil.nextDate();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				password = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				profileURL = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected UserAccount randomIrrelevantUserAccount() throws Exception {
		UserAccount randomIrrelevantUserAccount = randomUserAccount();

		return randomIrrelevantUserAccount;
	}

	protected UserAccount randomPatchUserAccount() throws Exception {
		return randomUserAccount();
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

	protected UserAccountResource userAccountResource;
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
		LogFactoryUtil.getLog(BaseUserAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.UserAccountResource
		_userAccountResource;

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