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

import com.liferay.headless.admin.user.client.dto.v1_0.EmailAddress;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.resource.v1_0.EmailAddressResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.EmailAddressSerDes;
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
public abstract class BaseEmailAddressResourceTestCase {

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

		_emailAddressResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		emailAddressResource = EmailAddressResource.builder(
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

		EmailAddress emailAddress1 = randomEmailAddress();

		String json = objectMapper.writeValueAsString(emailAddress1);

		EmailAddress emailAddress2 = EmailAddressSerDes.toDTO(json);

		Assert.assertTrue(equals(emailAddress1, emailAddress2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		EmailAddress emailAddress = randomEmailAddress();

		String json1 = objectMapper.writeValueAsString(emailAddress);
		String json2 = EmailAddressSerDes.toJSON(emailAddress);

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

		EmailAddress emailAddress = randomEmailAddress();

		emailAddress.setEmailAddress(regex);
		emailAddress.setExternalReferenceCode(regex);
		emailAddress.setType(regex);

		String json = EmailAddressSerDes.toJSON(emailAddress);

		Assert.assertFalse(json.contains(regex));

		emailAddress = EmailAddressSerDes.toDTO(json);

		Assert.assertEquals(regex, emailAddress.getEmailAddress());
		Assert.assertEquals(regex, emailAddress.getExternalReferenceCode());
		Assert.assertEquals(regex, emailAddress.getType());
	}

	@Test
	public void testDeleteEmailAddress() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		EmailAddress emailAddress = testDeleteEmailAddress_addEmailAddress();

		assertHttpResponseStatusCode(
			204,
			emailAddressResource.deleteEmailAddressHttpResponse(
				emailAddress.getId()));

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress.getId()));
		assertHttpResponseStatusCode(
			404, emailAddressResource.getEmailAddressHttpResponse(0L));
	}

	protected EmailAddress testDeleteEmailAddress_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteEmailAddress() throws Exception {

		// No namespace

		EmailAddress emailAddress1 =
			testGraphQLDeleteEmailAddress_addEmailAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteEmailAddress",
						new HashMap<String, Object>() {
							{
								put("emailAddressId", emailAddress1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteEmailAddress"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"emailAddress",
					new HashMap<String, Object>() {
						{
							put("emailAddressId", emailAddress1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		EmailAddress emailAddress2 =
			testGraphQLDeleteEmailAddress_addEmailAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteEmailAddress",
							new HashMap<String, Object>() {
								{
									put(
										"emailAddressId",
										emailAddress2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteEmailAddress"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"emailAddress",
						new HashMap<String, Object>() {
							{
								put("emailAddressId", emailAddress2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected EmailAddress testGraphQLDeleteEmailAddress_addEmailAddress()
		throws Exception {

		return testGraphQLEmailAddress_addEmailAddress();
	}

	@Test
	public void testDeleteEmailAddressBatch() throws Exception {
		EmailAddress emailAddress1 =
			testDeleteEmailAddressBatch_addEmailAddress();

		testDeleteEmailAddressBatch_deleteEmailAddress(
			202, emailAddress1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress1.getId()));

		emailAddress1 = testDeleteEmailAddressBatch_addEmailAddress();

		testDeleteEmailAddressBatch_deleteEmailAddress(
			202, null, emailAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress1.getId()));

		emailAddress1 = testDeleteEmailAddressBatch_addEmailAddress();
		EmailAddress emailAddress2 =
			testDeleteEmailAddressBatch_addEmailAddress();

		testDeleteEmailAddressBatch_deleteEmailAddress(
			202, emailAddress2.getExternalReferenceCode(),
			emailAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress1.getId()));
		assertHttpResponseStatusCode(
			200,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress2.getId()));

		testDeleteEmailAddressBatch_deleteEmailAddress(
			202, emailAddress2.getExternalReferenceCode(),
			emailAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress2.getId()));
	}

	protected EmailAddress testDeleteEmailAddressBatch_addEmailAddress()
		throws Exception {

		return testDeleteEmailAddress_addEmailAddress();
	}

	protected void testDeleteEmailAddressBatch_deleteEmailAddress(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			emailAddressResource.deleteEmailAddressBatchHttpResponse(
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
	public void testDeleteEmailAddressByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		EmailAddress emailAddress =
			testDeleteEmailAddressByExternalReferenceCode_addEmailAddress();

		assertHttpResponseStatusCode(
			204,
			emailAddressResource.
				deleteEmailAddressByExternalReferenceCodeHttpResponse(
					emailAddress.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.
				getEmailAddressByExternalReferenceCodeHttpResponse(
					emailAddress.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			emailAddressResource.
				getEmailAddressByExternalReferenceCodeHttpResponse("-"));
	}

	protected EmailAddress
			testDeleteEmailAddressByExternalReferenceCode_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteEmailAddressByExternalReferenceCode()
		throws Exception {

		// No namespace

		EmailAddress emailAddress1 =
			testGraphQLDeleteEmailAddressByExternalReferenceCode_addEmailAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteEmailAddressByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										emailAddress1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteEmailAddressByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"emailAddressByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									emailAddress1.getExternalReferenceCode() +
										"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		EmailAddress emailAddress2 =
			testGraphQLDeleteEmailAddressByExternalReferenceCode_addEmailAddress();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deleteEmailAddressByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											emailAddress2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deleteEmailAddressByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"emailAddressByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										emailAddress2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected EmailAddress
			testGraphQLDeleteEmailAddressByExternalReferenceCode_addEmailAddress()
		throws Exception {

		return testGraphQLEmailAddress_addEmailAddress();
	}

	@Test
	public void testGetAccountByExternalReferenceCodeEmailAddressesPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeEmailAddressesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeEmailAddressesPage_getIrrelevantExternalReferenceCode();

		Page<EmailAddress> page =
			emailAddressResource.
				getAccountByExternalReferenceCodeEmailAddressesPage(
					externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			EmailAddress irrelevantEmailAddress =
				testGetAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
					irrelevantExternalReferenceCode,
					randomIrrelevantEmailAddress());

			page =
				emailAddressResource.
					getAccountByExternalReferenceCodeEmailAddressesPage(
						irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEmailAddress, (List<EmailAddress>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		EmailAddress emailAddress1 =
			testGetAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				externalReferenceCode, randomEmailAddress());

		EmailAddress emailAddress2 =
			testGetAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				externalReferenceCode, randomEmailAddress());

		page =
			emailAddressResource.
				getAccountByExternalReferenceCodeEmailAddressesPage(
					externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(emailAddress1, (List<EmailAddress>)page.getItems());
		assertContains(emailAddress2, (List<EmailAddress>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
				externalReferenceCode));

		emailAddressResource.deleteEmailAddress(emailAddress1.getId());

		emailAddressResource.deleteEmailAddress(emailAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected EmailAddress
			testGetAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				String externalReferenceCode, EmailAddress emailAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeEmailAddressesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeEmailAddressesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountEmailAddressesPage() throws Exception {
		Long accountId = testGetAccountEmailAddressesPage_getAccountId();
		Long irrelevantAccountId =
			testGetAccountEmailAddressesPage_getIrrelevantAccountId();

		Page<EmailAddress> page =
			emailAddressResource.getAccountEmailAddressesPage(accountId);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountId != null) {
			EmailAddress irrelevantEmailAddress =
				testGetAccountEmailAddressesPage_addEmailAddress(
					irrelevantAccountId, randomIrrelevantEmailAddress());

			page = emailAddressResource.getAccountEmailAddressesPage(
				irrelevantAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEmailAddress, (List<EmailAddress>)page.getItems());
			assertValid(
				page,
				testGetAccountEmailAddressesPage_getExpectedActions(
					irrelevantAccountId));
		}

		EmailAddress emailAddress1 =
			testGetAccountEmailAddressesPage_addEmailAddress(
				accountId, randomEmailAddress());

		EmailAddress emailAddress2 =
			testGetAccountEmailAddressesPage_addEmailAddress(
				accountId, randomEmailAddress());

		page = emailAddressResource.getAccountEmailAddressesPage(accountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(emailAddress1, (List<EmailAddress>)page.getItems());
		assertContains(emailAddress2, (List<EmailAddress>)page.getItems());
		assertValid(
			page,
			testGetAccountEmailAddressesPage_getExpectedActions(accountId));

		emailAddressResource.deleteEmailAddress(emailAddress1.getId());

		emailAddressResource.deleteEmailAddress(emailAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountEmailAddressesPage_getExpectedActions(Long accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected EmailAddress testGetAccountEmailAddressesPage_addEmailAddress(
			Long accountId, EmailAddress emailAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountEmailAddressesPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountEmailAddressesPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetEmailAddress() throws Exception {
		EmailAddress postEmailAddress = testGetEmailAddress_addEmailAddress();

		EmailAddress getEmailAddress = emailAddressResource.getEmailAddress(
			postEmailAddress.getId());

		assertEquals(postEmailAddress, getEmailAddress);
		assertValid(getEmailAddress);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		EmailAddress postEmailAddress = testGetEmailAddress_addEmailAddress();

		EmailAddress getEmailAddress = emailAddressResource.getEmailAddress(
			postEmailAddress.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.admin.user.dto.v1_0.EmailAddress"
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

		Object item = vulcanCRUDItemDelegate.getItem(postEmailAddress.getId());

		assertEquals(
			getEmailAddress, EmailAddressSerDes.toDTO(item.toString()));
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

	protected EmailAddress testGetEmailAddress_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetEmailAddress() throws Exception {
		EmailAddress emailAddress =
			testGraphQLGetEmailAddress_addEmailAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				emailAddress,
				EmailAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"emailAddress",
								new HashMap<String, Object>() {
									{
										put(
											"emailAddressId",
											emailAddress.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/emailAddress"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				emailAddress,
				EmailAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"emailAddress",
									new HashMap<String, Object>() {
										{
											put(
												"emailAddressId",
												emailAddress.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/emailAddress"))));
	}

	@Test
	public void testGraphQLGetEmailAddressNotFound() throws Exception {
		Long irrelevantEmailAddressId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"emailAddress",
						new HashMap<String, Object>() {
							{
								put("emailAddressId", irrelevantEmailAddressId);
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
							"emailAddress",
							new HashMap<String, Object>() {
								{
									put(
										"emailAddressId",
										irrelevantEmailAddressId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected EmailAddress testGraphQLGetEmailAddress_addEmailAddress()
		throws Exception {

		return testGraphQLEmailAddress_addEmailAddress();
	}

	@Test
	public void testGetEmailAddressByExternalReferenceCode() throws Exception {
		EmailAddress postEmailAddress =
			testGetEmailAddressByExternalReferenceCode_addEmailAddress();

		EmailAddress getEmailAddress =
			emailAddressResource.getEmailAddressByExternalReferenceCode(
				postEmailAddress.getExternalReferenceCode());

		assertEquals(postEmailAddress, getEmailAddress);
		assertValid(getEmailAddress);
	}

	protected EmailAddress
			testGetEmailAddressByExternalReferenceCode_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetEmailAddressByExternalReferenceCode()
		throws Exception {

		EmailAddress emailAddress =
			testGraphQLGetEmailAddressByExternalReferenceCode_addEmailAddress();

		// No namespace

		Assert.assertTrue(
			equals(
				emailAddress,
				EmailAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"emailAddressByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												emailAddress.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/emailAddressByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				emailAddress,
				EmailAddressSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"emailAddressByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													emailAddress.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/emailAddressByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetEmailAddressByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"emailAddressByExternalReferenceCode",
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
							"emailAddressByExternalReferenceCode",
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

	protected EmailAddress
			testGraphQLGetEmailAddressByExternalReferenceCode_addEmailAddress()
		throws Exception {

		return testGraphQLEmailAddress_addEmailAddress();
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodeEmailAddressesPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getIrrelevantExternalReferenceCode();

		Page<EmailAddress> page =
			emailAddressResource.
				getOrganizationByExternalReferenceCodeEmailAddressesPage(
					externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			EmailAddress irrelevantEmailAddress =
				testGetOrganizationByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
					irrelevantExternalReferenceCode,
					randomIrrelevantEmailAddress());

			page =
				emailAddressResource.
					getOrganizationByExternalReferenceCodeEmailAddressesPage(
						irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEmailAddress, (List<EmailAddress>)page.getItems());
			assertValid(
				page,
				testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		EmailAddress emailAddress1 =
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				externalReferenceCode, randomEmailAddress());

		EmailAddress emailAddress2 =
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				externalReferenceCode, randomEmailAddress());

		page =
			emailAddressResource.
				getOrganizationByExternalReferenceCodeEmailAddressesPage(
					externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(emailAddress1, (List<EmailAddress>)page.getItems());
		assertContains(emailAddress2, (List<EmailAddress>)page.getItems());
		assertValid(
			page,
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
				externalReferenceCode));

		emailAddressResource.deleteEmailAddress(emailAddress1.getId());

		emailAddressResource.deleteEmailAddress(emailAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected EmailAddress
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				String externalReferenceCode, EmailAddress emailAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodeEmailAddressesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationEmailAddressesPage() throws Exception {
		String organizationId =
			testGetOrganizationEmailAddressesPage_getOrganizationId();
		String irrelevantOrganizationId =
			testGetOrganizationEmailAddressesPage_getIrrelevantOrganizationId();

		Page<EmailAddress> page =
			emailAddressResource.getOrganizationEmailAddressesPage(
				organizationId);

		long totalCount = page.getTotalCount();

		if (irrelevantOrganizationId != null) {
			EmailAddress irrelevantEmailAddress =
				testGetOrganizationEmailAddressesPage_addEmailAddress(
					irrelevantOrganizationId, randomIrrelevantEmailAddress());

			page = emailAddressResource.getOrganizationEmailAddressesPage(
				irrelevantOrganizationId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEmailAddress, (List<EmailAddress>)page.getItems());
			assertValid(
				page,
				testGetOrganizationEmailAddressesPage_getExpectedActions(
					irrelevantOrganizationId));
		}

		EmailAddress emailAddress1 =
			testGetOrganizationEmailAddressesPage_addEmailAddress(
				organizationId, randomEmailAddress());

		EmailAddress emailAddress2 =
			testGetOrganizationEmailAddressesPage_addEmailAddress(
				organizationId, randomEmailAddress());

		page = emailAddressResource.getOrganizationEmailAddressesPage(
			organizationId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(emailAddress1, (List<EmailAddress>)page.getItems());
		assertContains(emailAddress2, (List<EmailAddress>)page.getItems());
		assertValid(
			page,
			testGetOrganizationEmailAddressesPage_getExpectedActions(
				organizationId));

		emailAddressResource.deleteEmailAddress(emailAddress1.getId());

		emailAddressResource.deleteEmailAddress(emailAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationEmailAddressesPage_getExpectedActions(
				String organizationId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected EmailAddress
			testGetOrganizationEmailAddressesPage_addEmailAddress(
				String organizationId, EmailAddress emailAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetOrganizationEmailAddressesPage_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationEmailAddressesPage_getIrrelevantOrganizationId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserAccountByExternalReferenceCodeEmailAddressesPage()
		throws Exception {

		String externalReferenceCode =
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getIrrelevantExternalReferenceCode();

		Page<EmailAddress> page =
			emailAddressResource.
				getUserAccountByExternalReferenceCodeEmailAddressesPage(
					externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			EmailAddress irrelevantEmailAddress =
				testGetUserAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
					irrelevantExternalReferenceCode,
					randomIrrelevantEmailAddress());

			page =
				emailAddressResource.
					getUserAccountByExternalReferenceCodeEmailAddressesPage(
						irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEmailAddress, (List<EmailAddress>)page.getItems());
			assertValid(
				page,
				testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		EmailAddress emailAddress1 =
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				externalReferenceCode, randomEmailAddress());

		EmailAddress emailAddress2 =
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				externalReferenceCode, randomEmailAddress());

		page =
			emailAddressResource.
				getUserAccountByExternalReferenceCodeEmailAddressesPage(
					externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(emailAddress1, (List<EmailAddress>)page.getItems());
		assertContains(emailAddress2, (List<EmailAddress>)page.getItems());
		assertValid(
			page,
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
				externalReferenceCode));

		emailAddressResource.deleteEmailAddress(emailAddress1.getId());

		emailAddressResource.deleteEmailAddress(emailAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected EmailAddress
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_addEmailAddress(
				String externalReferenceCode, EmailAddress emailAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserAccountByExternalReferenceCodeEmailAddressesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserAccountEmailAddressesPage() throws Exception {
		Long userAccountId =
			testGetUserAccountEmailAddressesPage_getUserAccountId();
		Long irrelevantUserAccountId =
			testGetUserAccountEmailAddressesPage_getIrrelevantUserAccountId();

		Page<EmailAddress> page =
			emailAddressResource.getUserAccountEmailAddressesPage(
				userAccountId);

		long totalCount = page.getTotalCount();

		if (irrelevantUserAccountId != null) {
			EmailAddress irrelevantEmailAddress =
				testGetUserAccountEmailAddressesPage_addEmailAddress(
					irrelevantUserAccountId, randomIrrelevantEmailAddress());

			page = emailAddressResource.getUserAccountEmailAddressesPage(
				irrelevantUserAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantEmailAddress, (List<EmailAddress>)page.getItems());
			assertValid(
				page,
				testGetUserAccountEmailAddressesPage_getExpectedActions(
					irrelevantUserAccountId));
		}

		EmailAddress emailAddress1 =
			testGetUserAccountEmailAddressesPage_addEmailAddress(
				userAccountId, randomEmailAddress());

		EmailAddress emailAddress2 =
			testGetUserAccountEmailAddressesPage_addEmailAddress(
				userAccountId, randomEmailAddress());

		page = emailAddressResource.getUserAccountEmailAddressesPage(
			userAccountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(emailAddress1, (List<EmailAddress>)page.getItems());
		assertContains(emailAddress2, (List<EmailAddress>)page.getItems());
		assertValid(
			page,
			testGetUserAccountEmailAddressesPage_getExpectedActions(
				userAccountId));

		emailAddressResource.deleteEmailAddress(emailAddress1.getId());

		emailAddressResource.deleteEmailAddress(emailAddress2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountEmailAddressesPage_getExpectedActions(
				Long userAccountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected EmailAddress testGetUserAccountEmailAddressesPage_addEmailAddress(
			Long userAccountId, EmailAddress emailAddress)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserAccountEmailAddressesPage_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetUserAccountEmailAddressesPage_getIrrelevantUserAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchEmailAddress() throws Exception {
		EmailAddress postEmailAddress = testPatchEmailAddress_addEmailAddress();

		EmailAddress randomPatchEmailAddress = randomPatchEmailAddress();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		EmailAddress patchEmailAddress = emailAddressResource.patchEmailAddress(
			postEmailAddress.getId(), randomPatchEmailAddress);

		EmailAddress expectedPatchEmailAddress = postEmailAddress.clone();

		BeanTestUtil.copyProperties(
			randomPatchEmailAddress, expectedPatchEmailAddress);

		EmailAddress getEmailAddress = emailAddressResource.getEmailAddress(
			patchEmailAddress.getId());

		assertEquals(expectedPatchEmailAddress, getEmailAddress);
		assertValid(getEmailAddress);
	}

	protected EmailAddress testPatchEmailAddress_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchEmailAddressByExternalReferenceCode()
		throws Exception {

		EmailAddress postEmailAddress =
			testPatchEmailAddressByExternalReferenceCode_addEmailAddress();

		EmailAddress randomPatchEmailAddress = randomPatchEmailAddress();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		EmailAddress patchEmailAddress =
			emailAddressResource.patchEmailAddressByExternalReferenceCode(
				postEmailAddress.getExternalReferenceCode(),
				randomPatchEmailAddress);

		EmailAddress expectedPatchEmailAddress = postEmailAddress.clone();

		BeanTestUtil.copyProperties(
			randomPatchEmailAddress, expectedPatchEmailAddress);

		EmailAddress getEmailAddress =
			emailAddressResource.getEmailAddressByExternalReferenceCode(
				patchEmailAddress.getExternalReferenceCode());

		assertEquals(expectedPatchEmailAddress, getEmailAddress);
		assertValid(getEmailAddress);
	}

	protected EmailAddress
			testPatchEmailAddressByExternalReferenceCode_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		EmailAddress emailAddress1 =
			testBatchEngineDeleteImportTask_addEmailAddress();

		testBatchEngineDeleteImportTask_deleteEmailAddress(
			200, emailAddress1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress1.getId()));

		emailAddress1 = testBatchEngineDeleteImportTask_addEmailAddress();

		testBatchEngineDeleteImportTask_deleteEmailAddress(
			200, null, emailAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress1.getId()));

		emailAddress1 = testBatchEngineDeleteImportTask_addEmailAddress();
		EmailAddress emailAddress2 =
			testBatchEngineDeleteImportTask_addEmailAddress();

		testBatchEngineDeleteImportTask_deleteEmailAddress(
			200, emailAddress2.getExternalReferenceCode(),
			emailAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress1.getId()));
		assertHttpResponseStatusCode(
			200,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress2.getId()));

		testBatchEngineDeleteImportTask_deleteEmailAddress(
			200, emailAddress2.getExternalReferenceCode(),
			emailAddress1.getId());

		assertHttpResponseStatusCode(
			404,
			emailAddressResource.getEmailAddressHttpResponse(
				emailAddress2.getId()));
	}

	protected EmailAddress testBatchEngineDeleteImportTask_addEmailAddress()
		throws Exception {

		return testDeleteEmailAddress_addEmailAddress();
	}

	protected void testBatchEngineDeleteImportTask_deleteEmailAddress(
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
				"com.liferay.headless.admin.user.dto.v1_0.EmailAddress", null,
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

	protected EmailAddress testGraphQLEmailAddress_addEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		EmailAddress emailAddress, List<EmailAddress> emailAddresses) {

		boolean contains = false;

		for (EmailAddress item : emailAddresses) {
			if (equals(emailAddress, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			emailAddresses + " does not contain " + emailAddress, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		EmailAddress emailAddress1, EmailAddress emailAddress2) {

		Assert.assertTrue(
			emailAddress1 + " does not equal " + emailAddress2,
			equals(emailAddress1, emailAddress2));
	}

	protected void assertEquals(
		List<EmailAddress> emailAddresses1,
		List<EmailAddress> emailAddresses2) {

		Assert.assertEquals(emailAddresses1.size(), emailAddresses2.size());

		for (int i = 0; i < emailAddresses1.size(); i++) {
			EmailAddress emailAddress1 = emailAddresses1.get(i);
			EmailAddress emailAddress2 = emailAddresses2.get(i);

			assertEquals(emailAddress1, emailAddress2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<EmailAddress> emailAddresses1,
		List<EmailAddress> emailAddresses2) {

		Assert.assertEquals(emailAddresses1.size(), emailAddresses2.size());

		for (EmailAddress emailAddress1 : emailAddresses1) {
			boolean contains = false;

			for (EmailAddress emailAddress2 : emailAddresses2) {
				if (equals(emailAddress1, emailAddress2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				emailAddresses2 + " does not contain " + emailAddress1,
				contains);
		}
	}

	protected void assertValid(EmailAddress emailAddress) throws Exception {
		boolean valid = true;

		if (emailAddress.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("emailAddress", additionalAssertFieldName)) {
				if (emailAddress.getEmailAddress() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (emailAddress.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (emailAddress.getPrimary() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (emailAddress.getType() == null) {
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

	protected void assertValid(Page<EmailAddress> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<EmailAddress> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<EmailAddress> emailAddresses = page.getItems();

		int size = emailAddresses.size();

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
					com.liferay.headless.admin.user.dto.v1_0.EmailAddress.
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
		EmailAddress emailAddress1, EmailAddress emailAddress2) {

		if (emailAddress1 == emailAddress2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("emailAddress", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						emailAddress1.getEmailAddress(),
						emailAddress2.getEmailAddress())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						emailAddress1.getExternalReferenceCode(),
						emailAddress2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						emailAddress1.getId(), emailAddress2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						emailAddress1.getPrimary(),
						emailAddress2.getPrimary())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("type", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						emailAddress1.getType(), emailAddress2.getType())) {

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

		if (!(_emailAddressResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_emailAddressResource;

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
		EntityField entityField, String operator, EmailAddress emailAddress) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("emailAddress")) {
			Object object = emailAddress.getEmailAddress();

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
			Object object = emailAddress.getExternalReferenceCode();

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

		if (entityFieldName.equals("type")) {
			Object object = emailAddress.getType();

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

	protected EmailAddress randomEmailAddress() throws Exception {
		return new EmailAddress() {
			{
				emailAddress =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				primary = RandomTestUtil.randomBoolean();
				type = StringUtil.toLowerCase(RandomTestUtil.randomString());
			}
		};
	}

	protected EmailAddress randomIrrelevantEmailAddress() throws Exception {
		EmailAddress randomIrrelevantEmailAddress = randomEmailAddress();

		return randomIrrelevantEmailAddress;
	}

	protected EmailAddress randomPatchEmailAddress() throws Exception {
		return randomEmailAddress();
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

	protected EmailAddressResource emailAddressResource;
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
		LogFactoryUtil.getLog(BaseEmailAddressResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.EmailAddressResource
		_emailAddressResource;

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