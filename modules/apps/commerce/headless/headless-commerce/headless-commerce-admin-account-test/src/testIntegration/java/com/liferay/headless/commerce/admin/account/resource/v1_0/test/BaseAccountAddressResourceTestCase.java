/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.account.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountAddress;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.account.client.pagination.Page;
import com.liferay.headless.commerce.admin.account.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.AccountAddressResource;
import com.liferay.headless.commerce.admin.account.client.serdes.v1_0.AccountAddressSerDes;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseAccountAddressResourceTestCase {

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

		_accountAddressResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountAddressResource = AccountAddressResource.builder(
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

		AccountAddress accountAddress1 = randomAccountAddress();

		String json = objectMapper.writeValueAsString(accountAddress1);

		AccountAddress accountAddress2 = AccountAddressSerDes.toDTO(json);

		Assert.assertTrue(equals(accountAddress1, accountAddress2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountAddress accountAddress = randomAccountAddress();

		String json1 = objectMapper.writeValueAsString(accountAddress);
		String json2 = AccountAddressSerDes.toJSON(accountAddress);

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

		AccountAddress accountAddress = randomAccountAddress();

		accountAddress.setCity(regex);
		accountAddress.setCountryISOCode(regex);
		accountAddress.setDescription(regex);
		accountAddress.setExternalReferenceCode(regex);
		accountAddress.setName(regex);
		accountAddress.setPhoneNumber(regex);
		accountAddress.setRegionISOCode(regex);
		accountAddress.setStreet1(regex);
		accountAddress.setStreet2(regex);
		accountAddress.setStreet3(regex);
		accountAddress.setZip(regex);

		String json = AccountAddressSerDes.toJSON(accountAddress);

		Assert.assertFalse(json.contains(regex));

		accountAddress = AccountAddressSerDes.toDTO(json);

		Assert.assertEquals(regex, accountAddress.getCity());
		Assert.assertEquals(regex, accountAddress.getCountryISOCode());
		Assert.assertEquals(regex, accountAddress.getDescription());
		Assert.assertEquals(regex, accountAddress.getExternalReferenceCode());
		Assert.assertEquals(regex, accountAddress.getName());
		Assert.assertEquals(regex, accountAddress.getPhoneNumber());
		Assert.assertEquals(regex, accountAddress.getRegionISOCode());
		Assert.assertEquals(regex, accountAddress.getStreet1());
		Assert.assertEquals(regex, accountAddress.getStreet2());
		Assert.assertEquals(regex, accountAddress.getStreet3());
		Assert.assertEquals(regex, accountAddress.getZip());
	}

	@Test
	public void testDeleteAccountAddress() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountAddress accountAddress =
			testDeleteAccountAddress_addAccountAddress();

		assertHttpResponseStatusCode(
			204,
			accountAddressResource.deleteAccountAddressHttpResponse(
				accountAddress.getId()));

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress.getId()));
		assertHttpResponseStatusCode(
			404, accountAddressResource.getAccountAddressHttpResponse(0L));
	}

	protected AccountAddress testDeleteAccountAddress_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountAddress() throws Exception {

		// No namespace

		AccountAddress accountAddress1 =
			testGraphQLDeleteAccountAddress_addAccountAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountAddress",
						new HashMap<String, Object>() {
							{
								put("id", accountAddress1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteAccountAddress"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountAddress",
					new HashMap<String, Object>() {
						{
							put("id", accountAddress1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminAccount_v1_0

		AccountAddress accountAddress2 =
			testGraphQLDeleteAccountAddress_addAccountAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminAccount_v1_0",
						new GraphQLField(
							"deleteAccountAddress",
							new HashMap<String, Object>() {
								{
									put("id", accountAddress2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminAccount_v1_0",
				"Object/deleteAccountAddress"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminAccount_v1_0",
					new GraphQLField(
						"accountAddress",
						new HashMap<String, Object>() {
							{
								put("id", accountAddress2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected AccountAddress testGraphQLDeleteAccountAddress_addAccountAddress()
		throws Exception {

		return testGraphQLAccountAddress_addAccountAddress();
	}

	@Test
	public void testDeleteAccountAddressBatch() throws Exception {
		AccountAddress accountAddress1 =
			testDeleteAccountAddressBatch_addAccountAddress();

		testDeleteAccountAddressBatch_deleteAccountAddress(
			202, accountAddress1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress1.getId()));

		accountAddress1 = testDeleteAccountAddressBatch_addAccountAddress();

		testDeleteAccountAddressBatch_deleteAccountAddress(
			202, null, accountAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress1.getId()));

		accountAddress1 = testDeleteAccountAddressBatch_addAccountAddress();
		AccountAddress accountAddress2 =
			testDeleteAccountAddressBatch_addAccountAddress();

		testDeleteAccountAddressBatch_deleteAccountAddress(
			202, accountAddress2.getExternalReferenceCode(),
			accountAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress1.getId()));
		assertHttpResponseStatusCode(
			200,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress2.getId()));

		testDeleteAccountAddressBatch_deleteAccountAddress(
			202, accountAddress2.getExternalReferenceCode(),
			accountAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress2.getId()));
	}

	protected AccountAddress testDeleteAccountAddressBatch_addAccountAddress()
		throws Exception {

		return testDeleteAccountAddress_addAccountAddress();
	}

	protected void testDeleteAccountAddressBatch_deleteAccountAddress(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			accountAddressResource.deleteAccountAddressBatchHttpResponse(
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
	public void testDeleteAccountAddressByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountAddress accountAddress =
			testDeleteAccountAddressByExternalReferenceCode_addAccountAddress();

		assertHttpResponseStatusCode(
			204,
			accountAddressResource.
				deleteAccountAddressByExternalReferenceCodeHttpResponse(
					accountAddress.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.
				getAccountAddressByExternalReferenceCodeHttpResponse(
					accountAddress.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			accountAddressResource.
				getAccountAddressByExternalReferenceCodeHttpResponse("-"));
	}

	protected AccountAddress
			testDeleteAccountAddressByExternalReferenceCode_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountAddressByExternalReferenceCode()
		throws Exception {

		// No namespace

		AccountAddress accountAddress1 =
			testGraphQLDeleteAccountAddressByExternalReferenceCode_addAccountAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountAddressByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										accountAddress1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountAddressByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountAddressByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									accountAddress1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminAccount_v1_0

		AccountAddress accountAddress2 =
			testGraphQLDeleteAccountAddressByExternalReferenceCode_addAccountAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminAccount_v1_0",
						new GraphQLField(
							"deleteAccountAddressByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											accountAddress2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminAccount_v1_0",
				"Object/deleteAccountAddressByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminAccount_v1_0",
					new GraphQLField(
						"accountAddressByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										accountAddress2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected AccountAddress
			testGraphQLDeleteAccountAddressByExternalReferenceCode_addAccountAddress()
		throws Exception {

		return testGraphQLAccountAddress_addAccountAddress();
	}

	@Test
	public void testGetAccountAddress() throws Exception {
		AccountAddress postAccountAddress =
			testGetAccountAddress_addAccountAddress();

		AccountAddress getAccountAddress =
			accountAddressResource.getAccountAddress(
				postAccountAddress.getId());

		assertEquals(postAccountAddress, getAccountAddress);
		assertValid(getAccountAddress);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		AccountAddress postAccountAddress =
			testGetAccountAddress_addAccountAddress();

		AccountAddress getAccountAddress =
			accountAddressResource.getAccountAddress(
				postAccountAddress.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.account.dto.v1_0.AccountAddress"
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
			postAccountAddress.getId());

		assertEquals(
			getAccountAddress, AccountAddressSerDes.toDTO(item.toString()));
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

	protected AccountAddress testGetAccountAddress_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountAddress() throws Exception {
		AccountAddress accountAddress =
			testGraphQLGetAccountAddress_addAccountAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				accountAddress,
				AccountAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountAddress",
								new HashMap<String, Object>() {
									{
										put("id", accountAddress.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/accountAddress"))));

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertTrue(
			equals(
				accountAddress,
				AccountAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminAccount_v1_0",
								new GraphQLField(
									"accountAddress",
									new HashMap<String, Object>() {
										{
											put("id", accountAddress.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminAccount_v1_0",
						"Object/accountAddress"))));
	}

	@Test
	public void testGraphQLGetAccountAddressNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountAddress",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminAccount_v1_0",
						new GraphQLField(
							"accountAddress",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected AccountAddress testGraphQLGetAccountAddress_addAccountAddress()
		throws Exception {

		return testGraphQLAccountAddress_addAccountAddress();
	}

	@Test
	public void testGetAccountAddressByExternalReferenceCode()
		throws Exception {

		AccountAddress postAccountAddress =
			testGetAccountAddressByExternalReferenceCode_addAccountAddress();

		AccountAddress getAccountAddress =
			accountAddressResource.getAccountAddressByExternalReferenceCode(
				postAccountAddress.getExternalReferenceCode());

		assertEquals(postAccountAddress, getAccountAddress);
		assertValid(getAccountAddress);
	}

	protected AccountAddress
			testGetAccountAddressByExternalReferenceCode_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountAddressByExternalReferenceCode()
		throws Exception {

		AccountAddress accountAddress =
			testGraphQLGetAccountAddressByExternalReferenceCode_addAccountAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				accountAddress,
				AccountAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountAddressByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												accountAddress.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountAddressByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertTrue(
			equals(
				accountAddress,
				AccountAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminAccount_v1_0",
								new GraphQLField(
									"accountAddressByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													accountAddress.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminAccount_v1_0",
						"Object/accountAddressByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetAccountAddressByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountAddressByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminAccount_v1_0",
						new GraphQLField(
							"accountAddressByExternalReferenceCode",
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

	protected AccountAddress
			testGraphQLGetAccountAddressByExternalReferenceCode_addAccountAddress()
		throws Exception {

		return testGraphQLAccountAddress_addAccountAddress();
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountAddressesPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getIrrelevantExternalReferenceCode();

		Page<AccountAddress> page =
			accountAddressResource.
				getAccountByExternalReferenceCodeAccountAddressesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			AccountAddress irrelevantAccountAddress =
				testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountAddress());

			page =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountAddress,
				(List<AccountAddress>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeAccountAddressesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		AccountAddress accountAddress1 =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
				externalReferenceCode, randomAccountAddress());

		AccountAddress accountAddress2 =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
				externalReferenceCode, randomAccountAddress());

		page =
			accountAddressResource.
				getAccountByExternalReferenceCodeAccountAddressesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountAddress1, (List<AccountAddress>)page.getItems());
		assertContains(accountAddress2, (List<AccountAddress>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getExpectedActions(
				externalReferenceCode));

		accountAddressResource.deleteAccountAddress(accountAddress1.getId());

		accountAddressResource.deleteAccountAddress(accountAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountAddressesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getExternalReferenceCode();

		Page<AccountAddress> accountAddressesPage =
			accountAddressResource.
				getAccountByExternalReferenceCodeAccountAddressesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			accountAddressesPage.getTotalCount());

		AccountAddress accountAddress1 =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
				externalReferenceCode, randomAccountAddress());

		AccountAddress accountAddress2 =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
				externalReferenceCode, randomAccountAddress());

		AccountAddress accountAddress3 =
			testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
				externalReferenceCode, randomAccountAddress());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountAddress> page1 =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountAddress1, (List<AccountAddress>)page1.getItems());

			Page<AccountAddress> page2 =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountAddress2, (List<AccountAddress>)page2.getItems());

			Page<AccountAddress> page3 =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountAddress3, (List<AccountAddress>)page3.getItems());
		}
		else {
			Page<AccountAddress> page1 =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<AccountAddress> accountAddresses1 =
				(List<AccountAddress>)page1.getItems();

			Assert.assertEquals(
				accountAddresses1.toString(), totalCount + 2,
				accountAddresses1.size());

			Page<AccountAddress> page2 =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountAddress> accountAddresses2 =
				(List<AccountAddress>)page2.getItems();

			Assert.assertEquals(
				accountAddresses2.toString(), 1, accountAddresses2.size());

			Page<AccountAddress> page3 =
				accountAddressResource.
					getAccountByExternalReferenceCodeAccountAddressesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountAddress1, (List<AccountAddress>)page3.getItems());
			assertContains(
				accountAddress2, (List<AccountAddress>)page3.getItems());
			assertContains(
				accountAddress3, (List<AccountAddress>)page3.getItems());
		}
	}

	protected AccountAddress
			testGetAccountByExternalReferenceCodeAccountAddressesPage_addAccountAddress(
				String externalReferenceCode, AccountAddress accountAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountAddressesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountIdAccountAddressesPage() throws Exception {
		Long id = testGetAccountIdAccountAddressesPage_getId();
		Long irrelevantId =
			testGetAccountIdAccountAddressesPage_getIrrelevantId();

		Page<AccountAddress> page =
			accountAddressResource.getAccountIdAccountAddressesPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			AccountAddress irrelevantAccountAddress =
				testGetAccountIdAccountAddressesPage_addAccountAddress(
					irrelevantId, randomIrrelevantAccountAddress());

			page = accountAddressResource.getAccountIdAccountAddressesPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountAddress,
				(List<AccountAddress>)page.getItems());
			assertValid(
				page,
				testGetAccountIdAccountAddressesPage_getExpectedActions(
					irrelevantId));
		}

		AccountAddress accountAddress1 =
			testGetAccountIdAccountAddressesPage_addAccountAddress(
				id, randomAccountAddress());

		AccountAddress accountAddress2 =
			testGetAccountIdAccountAddressesPage_addAccountAddress(
				id, randomAccountAddress());

		page = accountAddressResource.getAccountIdAccountAddressesPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountAddress1, (List<AccountAddress>)page.getItems());
		assertContains(accountAddress2, (List<AccountAddress>)page.getItems());
		assertValid(
			page, testGetAccountIdAccountAddressesPage_getExpectedActions(id));

		accountAddressResource.deleteAccountAddress(accountAddress1.getId());

		accountAddressResource.deleteAccountAddress(accountAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountIdAccountAddressesPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountIdAccountAddressesPageWithPagination()
		throws Exception {

		Long id = testGetAccountIdAccountAddressesPage_getId();

		Page<AccountAddress> accountAddressesPage =
			accountAddressResource.getAccountIdAccountAddressesPage(id, null);

		int totalCount = GetterUtil.getInteger(
			accountAddressesPage.getTotalCount());

		AccountAddress accountAddress1 =
			testGetAccountIdAccountAddressesPage_addAccountAddress(
				id, randomAccountAddress());

		AccountAddress accountAddress2 =
			testGetAccountIdAccountAddressesPage_addAccountAddress(
				id, randomAccountAddress());

		AccountAddress accountAddress3 =
			testGetAccountIdAccountAddressesPage_addAccountAddress(
				id, randomAccountAddress());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountAddress> page1 =
				accountAddressResource.getAccountIdAccountAddressesPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountAddress1, (List<AccountAddress>)page1.getItems());

			Page<AccountAddress> page2 =
				accountAddressResource.getAccountIdAccountAddressesPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				accountAddress2, (List<AccountAddress>)page2.getItems());

			Page<AccountAddress> page3 =
				accountAddressResource.getAccountIdAccountAddressesPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				accountAddress3, (List<AccountAddress>)page3.getItems());
		}
		else {
			Page<AccountAddress> page1 =
				accountAddressResource.getAccountIdAccountAddressesPage(
					id, Pagination.of(1, totalCount + 2));

			List<AccountAddress> accountAddresses1 =
				(List<AccountAddress>)page1.getItems();

			Assert.assertEquals(
				accountAddresses1.toString(), totalCount + 2,
				accountAddresses1.size());

			Page<AccountAddress> page2 =
				accountAddressResource.getAccountIdAccountAddressesPage(
					id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountAddress> accountAddresses2 =
				(List<AccountAddress>)page2.getItems();

			Assert.assertEquals(
				accountAddresses2.toString(), 1, accountAddresses2.size());

			Page<AccountAddress> page3 =
				accountAddressResource.getAccountIdAccountAddressesPage(
					id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountAddress1, (List<AccountAddress>)page3.getItems());
			assertContains(
				accountAddress2, (List<AccountAddress>)page3.getItems());
			assertContains(
				accountAddress3, (List<AccountAddress>)page3.getItems());
		}
	}

	protected AccountAddress
			testGetAccountIdAccountAddressesPage_addAccountAddress(
				Long id, AccountAddress accountAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountAddressesPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountAddressesPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchAccountAddress() throws Exception {
		AccountAddress postAccountAddress =
			testPatchAccountAddress_addAccountAddress();

		AccountAddress randomPatchAccountAddress = randomPatchAccountAddress();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountAddress patchAccountAddress =
			accountAddressResource.patchAccountAddress(
				postAccountAddress.getId(), randomPatchAccountAddress);

		AccountAddress expectedPatchAccountAddress = postAccountAddress.clone();

		BeanTestUtil.copyProperties(
			randomPatchAccountAddress, expectedPatchAccountAddress);

		AccountAddress getAccountAddress =
			accountAddressResource.getAccountAddress(
				patchAccountAddress.getId());

		assertEquals(expectedPatchAccountAddress, getAccountAddress);
		assertValid(getAccountAddress);
	}

	protected AccountAddress testPatchAccountAddress_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchAccountAddressByExternalReferenceCode()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountAddress()
		throws Exception {

		AccountAddress randomAccountAddress = randomAccountAddress();

		AccountAddress postAccountAddress =
			testPostAccountByExternalReferenceCodeAccountAddress_addAccountAddress(
				randomAccountAddress);

		assertEquals(randomAccountAddress, postAccountAddress);
		assertValid(postAccountAddress);
	}

	protected AccountAddress
			testPostAccountByExternalReferenceCodeAccountAddress_addAccountAddress(
				AccountAddress accountAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountIdAccountAddress() throws Exception {
		AccountAddress randomAccountAddress = randomAccountAddress();

		AccountAddress postAccountAddress =
			testPostAccountIdAccountAddress_addAccountAddress(
				randomAccountAddress);

		assertEquals(randomAccountAddress, postAccountAddress);
		assertValid(postAccountAddress);
	}

	protected AccountAddress testPostAccountIdAccountAddress_addAccountAddress(
			AccountAddress accountAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutAccountAddress() throws Exception {
		AccountAddress postAccountAddress =
			testPutAccountAddress_addAccountAddress();

		AccountAddress randomAccountAddress = randomAccountAddress();

		AccountAddress putAccountAddress =
			accountAddressResource.putAccountAddress(
				postAccountAddress.getId(), randomAccountAddress);

		assertEquals(randomAccountAddress, putAccountAddress);
		assertValid(putAccountAddress);

		AccountAddress getAccountAddress =
			accountAddressResource.getAccountAddress(putAccountAddress.getId());

		assertEquals(randomAccountAddress, getAccountAddress);
		assertValid(getAccountAddress);
	}

	protected AccountAddress testPutAccountAddress_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		AccountAddress accountAddress1 =
			testBatchEngineDeleteImportTask_addAccountAddress();

		testBatchEngineDeleteImportTask_deleteAccountAddress(
			200, accountAddress1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress1.getId()));

		accountAddress1 = testBatchEngineDeleteImportTask_addAccountAddress();

		testBatchEngineDeleteImportTask_deleteAccountAddress(
			200, null, accountAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress1.getId()));

		accountAddress1 = testBatchEngineDeleteImportTask_addAccountAddress();
		AccountAddress accountAddress2 =
			testBatchEngineDeleteImportTask_addAccountAddress();

		testBatchEngineDeleteImportTask_deleteAccountAddress(
			200, accountAddress2.getExternalReferenceCode(),
			accountAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress1.getId()));
		assertHttpResponseStatusCode(
			200,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress2.getId()));

		testBatchEngineDeleteImportTask_deleteAccountAddress(
			200, accountAddress2.getExternalReferenceCode(),
			accountAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			accountAddressResource.getAccountAddressHttpResponse(
				accountAddress2.getId()));
	}

	protected AccountAddress testBatchEngineDeleteImportTask_addAccountAddress()
		throws Exception {

		return testDeleteAccountAddress_addAccountAddress();
	}

	protected void testBatchEngineDeleteImportTask_deleteAccountAddress(
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
				"com.liferay.headless.commerce.admin.account.dto.v1_0.AccountAddress",
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

	protected AccountAddress testGraphQLAccountAddress_addAccountAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		AccountAddress accountAddress, List<AccountAddress> accountAddresses) {

		boolean contains = false;

		for (AccountAddress item : accountAddresses) {
			if (equals(accountAddress, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountAddresses + " does not contain " + accountAddress, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountAddress accountAddress1, AccountAddress accountAddress2) {

		Assert.assertTrue(
			accountAddress1 + " does not equal " + accountAddress2,
			equals(accountAddress1, accountAddress2));
	}

	protected void assertEquals(
		List<AccountAddress> accountAddresses1,
		List<AccountAddress> accountAddresses2) {

		Assert.assertEquals(accountAddresses1.size(), accountAddresses2.size());

		for (int i = 0; i < accountAddresses1.size(); i++) {
			AccountAddress accountAddress1 = accountAddresses1.get(i);
			AccountAddress accountAddress2 = accountAddresses2.get(i);

			assertEquals(accountAddress1, accountAddress2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountAddress> accountAddresses1,
		List<AccountAddress> accountAddresses2) {

		Assert.assertEquals(accountAddresses1.size(), accountAddresses2.size());

		for (AccountAddress accountAddress1 : accountAddresses1) {
			boolean contains = false;

			for (AccountAddress accountAddress2 : accountAddresses2) {
				if (equals(accountAddress1, accountAddress2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountAddresses2 + " does not contain " + accountAddress1,
				contains);
		}
	}

	protected void assertValid(AccountAddress accountAddress) throws Exception {
		boolean valid = true;

		if (accountAddress.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (accountAddress.getCity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (accountAddress.getCountryISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("defaultBilling", additionalAssertFieldName)) {
				if (accountAddress.getDefaultBilling() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("defaultShipping", additionalAssertFieldName)) {
				if (accountAddress.getDefaultShipping() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (accountAddress.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (accountAddress.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (accountAddress.getLatitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (accountAddress.getLongitude() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountAddress.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (accountAddress.getPhoneNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (accountAddress.getRegionISOCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (accountAddress.getStreet1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (accountAddress.getStreet2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (accountAddress.getStreet3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (accountAddress.getType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (accountAddress.getZip() == null) {
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

	protected void assertValid(Page<AccountAddress> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountAddress> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountAddress> accountAddresses = page.getItems();

		int size = accountAddresses.size();

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
					com.liferay.headless.commerce.admin.account.dto.v1_0.
						AccountAddress.class)) {

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
		AccountAddress accountAddress1, AccountAddress accountAddress2) {

		if (accountAddress1 == accountAddress2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("city", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getCity(), accountAddress2.getCity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("countryISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getCountryISOCode(),
						accountAddress2.getCountryISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("defaultBilling", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getDefaultBilling(),
						accountAddress2.getDefaultBilling())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("defaultShipping", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getDefaultShipping(),
						accountAddress2.getDefaultShipping())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getDescription(),
						accountAddress2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountAddress1.getExternalReferenceCode(),
						accountAddress2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getId(), accountAddress2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("latitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getLatitude(),
						accountAddress2.getLatitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("longitude", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getLongitude(),
						accountAddress2.getLongitude())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getName(), accountAddress2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getPhoneNumber(),
						accountAddress2.getPhoneNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("regionISOCode", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getRegionISOCode(),
						accountAddress2.getRegionISOCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getStreet1(),
						accountAddress2.getStreet1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getStreet2(),
						accountAddress2.getStreet2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("street3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getStreet3(),
						accountAddress2.getStreet3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getType(), accountAddress2.getType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("zip", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountAddress1.getZip(), accountAddress2.getZip())) {

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

		if (!(_accountAddressResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountAddressResource;

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
		AccountAddress accountAddress) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("city")) {
			Object object = accountAddress.getCity();

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

		if (entityFieldName.equals("countryISOCode")) {
			Object object = accountAddress.getCountryISOCode();

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

		if (entityFieldName.equals("defaultBilling")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("defaultShipping")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = accountAddress.getDescription();

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
			Object object = accountAddress.getExternalReferenceCode();

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

		if (entityFieldName.equals("latitude")) {
			sb.append(String.valueOf(accountAddress.getLatitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("longitude")) {
			sb.append(String.valueOf(accountAddress.getLongitude()));

			return sb.toString();
		}

		if (entityFieldName.equals("name")) {
			Object object = accountAddress.getName();

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

		if (entityFieldName.equals("phoneNumber")) {
			Object object = accountAddress.getPhoneNumber();

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

		if (entityFieldName.equals("regionISOCode")) {
			Object object = accountAddress.getRegionISOCode();

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

		if (entityFieldName.equals("street1")) {
			Object object = accountAddress.getStreet1();

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

		if (entityFieldName.equals("street2")) {
			Object object = accountAddress.getStreet2();

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

		if (entityFieldName.equals("street3")) {
			Object object = accountAddress.getStreet3();

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

		if (entityFieldName.equals("type")) {
			sb.append(String.valueOf(accountAddress.getType()));

			return sb.toString();
		}

		if (entityFieldName.equals("zip")) {
			Object object = accountAddress.getZip();

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

	protected AccountAddress randomAccountAddress() throws Exception {
		return new AccountAddress() {
			{
				city = StringUtil.toLowerCase(RandomTestUtil.randomString());
				countryISOCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				defaultBilling = RandomTestUtil.randomBoolean();
				defaultShipping = RandomTestUtil.randomBoolean();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				latitude = RandomTestUtil.randomDouble();
				longitude = RandomTestUtil.randomDouble();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				phoneNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				regionISOCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				street1 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street2 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				street3 = StringUtil.toLowerCase(RandomTestUtil.randomString());
				type = RandomTestUtil.randomInt();
				zip = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected AccountAddress randomIrrelevantAccountAddress() throws Exception {
		AccountAddress randomIrrelevantAccountAddress = randomAccountAddress();

		return randomIrrelevantAccountAddress;
	}

	protected AccountAddress randomPatchAccountAddress() throws Exception {
		return randomAccountAddress();
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

	protected AccountAddressResource accountAddressResource;
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
		LogFactoryUtil.getLog(BaseAccountAddressResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.account.resource.v1_0.
		AccountAddressResource _accountAddressResource;

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