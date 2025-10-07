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
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountChannelShippingOption;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.account.client.pagination.Page;
import com.liferay.headless.commerce.admin.account.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.AccountChannelShippingOptionResource;
import com.liferay.headless.commerce.admin.account.client.serdes.v1_0.AccountChannelShippingOptionSerDes;
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
public abstract class BaseAccountChannelShippingOptionResourceTestCase {

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

		_accountChannelShippingOptionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountChannelShippingOptionResource =
			AccountChannelShippingOptionResource.builder(
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

		AccountChannelShippingOption accountChannelShippingOption1 =
			randomAccountChannelShippingOption();

		String json = objectMapper.writeValueAsString(
			accountChannelShippingOption1);

		AccountChannelShippingOption accountChannelShippingOption2 =
			AccountChannelShippingOptionSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				accountChannelShippingOption1, accountChannelShippingOption2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountChannelShippingOption accountChannelShippingOption =
			randomAccountChannelShippingOption();

		String json1 = objectMapper.writeValueAsString(
			accountChannelShippingOption);
		String json2 = AccountChannelShippingOptionSerDes.toJSON(
			accountChannelShippingOption);

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

		AccountChannelShippingOption accountChannelShippingOption =
			randomAccountChannelShippingOption();

		accountChannelShippingOption.setAccountExternalReferenceCode(regex);
		accountChannelShippingOption.setChannelExternalReferenceCode(regex);
		accountChannelShippingOption.setShippingMethodKey(regex);
		accountChannelShippingOption.setShippingOptionKey(regex);

		String json = AccountChannelShippingOptionSerDes.toJSON(
			accountChannelShippingOption);

		Assert.assertFalse(json.contains(regex));

		accountChannelShippingOption = AccountChannelShippingOptionSerDes.toDTO(
			json);

		Assert.assertEquals(
			regex,
			accountChannelShippingOption.getAccountExternalReferenceCode());
		Assert.assertEquals(
			regex,
			accountChannelShippingOption.getChannelExternalReferenceCode());
		Assert.assertEquals(
			regex, accountChannelShippingOption.getShippingMethodKey());
		Assert.assertEquals(
			regex, accountChannelShippingOption.getShippingOptionKey());
	}

	@Test
	public void testDeleteAccountChannelShippingOption() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountChannelShippingOption accountChannelShippingOption =
			testDeleteAccountChannelShippingOption_addAccountChannelShippingOption();

		assertHttpResponseStatusCode(
			204,
			accountChannelShippingOptionResource.
				deleteAccountChannelShippingOptionHttpResponse(
					accountChannelShippingOption.getId()));

		assertHttpResponseStatusCode(
			404,
			accountChannelShippingOptionResource.
				getAccountChannelShippingOptionHttpResponse(
					accountChannelShippingOption.getId()));
		assertHttpResponseStatusCode(
			404,
			accountChannelShippingOptionResource.
				getAccountChannelShippingOptionHttpResponse(0L));
	}

	protected AccountChannelShippingOption
			testDeleteAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteAccountChannelShippingOption()
		throws Exception {

		// No namespace

		AccountChannelShippingOption accountChannelShippingOption1 =
			testGraphQLDeleteAccountChannelShippingOption_addAccountChannelShippingOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteAccountChannelShippingOption",
						new HashMap<String, Object>() {
							{
								put(
									"id",
									accountChannelShippingOption1.getId());
							}
						})),
				"JSONObject/data",
				"Object/deleteAccountChannelShippingOption"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"accountChannelShippingOption",
					new HashMap<String, Object>() {
						{
							put("id", accountChannelShippingOption1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminAccount_v1_0

		AccountChannelShippingOption accountChannelShippingOption2 =
			testGraphQLDeleteAccountChannelShippingOption_addAccountChannelShippingOption();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminAccount_v1_0",
						new GraphQLField(
							"deleteAccountChannelShippingOption",
							new HashMap<String, Object>() {
								{
									put(
										"id",
										accountChannelShippingOption2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminAccount_v1_0",
				"Object/deleteAccountChannelShippingOption"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminAccount_v1_0",
					new GraphQLField(
						"accountChannelShippingOption",
						new HashMap<String, Object>() {
							{
								put(
									"id",
									accountChannelShippingOption2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected AccountChannelShippingOption
			testGraphQLDeleteAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		return testGraphQLAccountChannelShippingOption_addAccountChannelShippingOption();
	}

	@Test
	public void testDeleteAccountChannelShippingOptionBatch() throws Exception {
		AccountChannelShippingOption accountChannelShippingOption1 =
			testDeleteAccountChannelShippingOptionBatch_addAccountChannelShippingOption();

		testDeleteAccountChannelShippingOptionBatch_deleteAccountChannelShippingOption(
			202, null, accountChannelShippingOption1.getId());

		assertHttpResponseStatusCode(
			404,
			accountChannelShippingOptionResource.
				getAccountChannelShippingOptionHttpResponse(
					accountChannelShippingOption1.getId()));
	}

	protected AccountChannelShippingOption
			testDeleteAccountChannelShippingOptionBatch_addAccountChannelShippingOption()
		throws Exception {

		return testDeleteAccountChannelShippingOption_addAccountChannelShippingOption();
	}

	protected void
			testDeleteAccountChannelShippingOptionBatch_deleteAccountChannelShippingOption(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			accountChannelShippingOptionResource.
				deleteAccountChannelShippingOptionBatchHttpResponse(
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
	public void testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getIrrelevantExternalReferenceCode();

		Page<AccountChannelShippingOption> page =
			accountChannelShippingOptionResource.
				getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			AccountChannelShippingOption
				irrelevantAccountChannelShippingOption =
					testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
						irrelevantExternalReferenceCode,
						randomIrrelevantAccountChannelShippingOption());

			page =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountChannelShippingOption,
				(List<AccountChannelShippingOption>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		AccountChannelShippingOption accountChannelShippingOption1 =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				externalReferenceCode, randomAccountChannelShippingOption());

		AccountChannelShippingOption accountChannelShippingOption2 =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				externalReferenceCode, randomAccountChannelShippingOption());

		page =
			accountChannelShippingOptionResource.
				getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountChannelShippingOption1,
			(List<AccountChannelShippingOption>)page.getItems());
		assertContains(
			accountChannelShippingOption2,
			(List<AccountChannelShippingOption>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExpectedActions(
				externalReferenceCode));

		accountChannelShippingOptionResource.deleteAccountChannelShippingOption(
			accountChannelShippingOption1.getId());

		accountChannelShippingOptionResource.deleteAccountChannelShippingOption(
			accountChannelShippingOption2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExternalReferenceCode();

		Page<AccountChannelShippingOption> accountChannelShippingOptionsPage =
			accountChannelShippingOptionResource.
				getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			accountChannelShippingOptionsPage.getTotalCount());

		AccountChannelShippingOption accountChannelShippingOption1 =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				externalReferenceCode, randomAccountChannelShippingOption());

		AccountChannelShippingOption accountChannelShippingOption2 =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				externalReferenceCode, randomAccountChannelShippingOption());

		AccountChannelShippingOption accountChannelShippingOption3 =
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				externalReferenceCode, randomAccountChannelShippingOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountChannelShippingOption> page1 =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountChannelShippingOption1,
				(List<AccountChannelShippingOption>)page1.getItems());

			Page<AccountChannelShippingOption> page2 =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountChannelShippingOption2,
				(List<AccountChannelShippingOption>)page2.getItems());

			Page<AccountChannelShippingOption> page3 =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountChannelShippingOption3,
				(List<AccountChannelShippingOption>)page3.getItems());
		}
		else {
			Page<AccountChannelShippingOption> page1 =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<AccountChannelShippingOption> accountChannelShippingOptions1 =
				(List<AccountChannelShippingOption>)page1.getItems();

			Assert.assertEquals(
				accountChannelShippingOptions1.toString(), totalCount + 2,
				accountChannelShippingOptions1.size());

			Page<AccountChannelShippingOption> page2 =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountChannelShippingOption> accountChannelShippingOptions2 =
				(List<AccountChannelShippingOption>)page2.getItems();

			Assert.assertEquals(
				accountChannelShippingOptions2.toString(), 1,
				accountChannelShippingOptions2.size());

			Page<AccountChannelShippingOption> page3 =
				accountChannelShippingOptionResource.
					getAccountByExternalReferenceCodeAccountChannelShippingOptionPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountChannelShippingOption1,
				(List<AccountChannelShippingOption>)page3.getItems());
			assertContains(
				accountChannelShippingOption2,
				(List<AccountChannelShippingOption>)page3.getItems());
			assertContains(
				accountChannelShippingOption3,
				(List<AccountChannelShippingOption>)page3.getItems());
		}
	}

	protected AccountChannelShippingOption
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				String externalReferenceCode,
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountChannelShippingOptionPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountChannelShippingOption() throws Exception {
		AccountChannelShippingOption postAccountChannelShippingOption =
			testGetAccountChannelShippingOption_addAccountChannelShippingOption();

		AccountChannelShippingOption getAccountChannelShippingOption =
			accountChannelShippingOptionResource.
				getAccountChannelShippingOption(
					postAccountChannelShippingOption.getId());

		assertEquals(
			postAccountChannelShippingOption, getAccountChannelShippingOption);
		assertValid(getAccountChannelShippingOption);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		AccountChannelShippingOption postAccountChannelShippingOption =
			testGetAccountChannelShippingOption_addAccountChannelShippingOption();

		AccountChannelShippingOption getAccountChannelShippingOption =
			accountChannelShippingOptionResource.
				getAccountChannelShippingOption(
					postAccountChannelShippingOption.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.account.dto.v1_0.AccountChannelShippingOption"
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
			postAccountChannelShippingOption.getId());

		assertEquals(
			getAccountChannelShippingOption,
			AccountChannelShippingOptionSerDes.toDTO(item.toString()));
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

	protected AccountChannelShippingOption
			testGetAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountChannelShippingOption() throws Exception {
		AccountChannelShippingOption accountChannelShippingOption =
			testGraphQLGetAccountChannelShippingOption_addAccountChannelShippingOption();

		// No namespace

		Assert.assertTrue(
			equals(
				accountChannelShippingOption,
				AccountChannelShippingOptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountChannelShippingOption",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											accountChannelShippingOption.
												getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountChannelShippingOption"))));

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertTrue(
			equals(
				accountChannelShippingOption,
				AccountChannelShippingOptionSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminAccount_v1_0",
								new GraphQLField(
									"accountChannelShippingOption",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												accountChannelShippingOption.
													getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminAccount_v1_0",
						"Object/accountChannelShippingOption"))));
	}

	@Test
	public void testGraphQLGetAccountChannelShippingOptionNotFound()
		throws Exception {

		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountChannelShippingOption",
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
							"accountChannelShippingOption",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected AccountChannelShippingOption
			testGraphQLGetAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		return testGraphQLAccountChannelShippingOption_addAccountChannelShippingOption();
	}

	@Test
	public void testGetAccountIdAccountChannelShippingOptionPage()
		throws Exception {

		Long id = testGetAccountIdAccountChannelShippingOptionPage_getId();
		Long irrelevantId =
			testGetAccountIdAccountChannelShippingOptionPage_getIrrelevantId();

		Page<AccountChannelShippingOption> page =
			accountChannelShippingOptionResource.
				getAccountIdAccountChannelShippingOptionPage(
					id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			AccountChannelShippingOption
				irrelevantAccountChannelShippingOption =
					testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
						irrelevantId,
						randomIrrelevantAccountChannelShippingOption());

			page =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountChannelShippingOption,
				(List<AccountChannelShippingOption>)page.getItems());
			assertValid(
				page,
				testGetAccountIdAccountChannelShippingOptionPage_getExpectedActions(
					irrelevantId));
		}

		AccountChannelShippingOption accountChannelShippingOption1 =
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				id, randomAccountChannelShippingOption());

		AccountChannelShippingOption accountChannelShippingOption2 =
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				id, randomAccountChannelShippingOption());

		page =
			accountChannelShippingOptionResource.
				getAccountIdAccountChannelShippingOptionPage(
					id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountChannelShippingOption1,
			(List<AccountChannelShippingOption>)page.getItems());
		assertContains(
			accountChannelShippingOption2,
			(List<AccountChannelShippingOption>)page.getItems());
		assertValid(
			page,
			testGetAccountIdAccountChannelShippingOptionPage_getExpectedActions(
				id));

		accountChannelShippingOptionResource.deleteAccountChannelShippingOption(
			accountChannelShippingOption1.getId());

		accountChannelShippingOptionResource.deleteAccountChannelShippingOption(
			accountChannelShippingOption2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountIdAccountChannelShippingOptionPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountIdAccountChannelShippingOptionPageWithPagination()
		throws Exception {

		Long id = testGetAccountIdAccountChannelShippingOptionPage_getId();

		Page<AccountChannelShippingOption> accountChannelShippingOptionsPage =
			accountChannelShippingOptionResource.
				getAccountIdAccountChannelShippingOptionPage(id, null);

		int totalCount = GetterUtil.getInteger(
			accountChannelShippingOptionsPage.getTotalCount());

		AccountChannelShippingOption accountChannelShippingOption1 =
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				id, randomAccountChannelShippingOption());

		AccountChannelShippingOption accountChannelShippingOption2 =
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				id, randomAccountChannelShippingOption());

		AccountChannelShippingOption accountChannelShippingOption3 =
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				id, randomAccountChannelShippingOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountChannelShippingOption> page1 =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountChannelShippingOption1,
				(List<AccountChannelShippingOption>)page1.getItems());

			Page<AccountChannelShippingOption> page2 =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountChannelShippingOption2,
				(List<AccountChannelShippingOption>)page2.getItems());

			Page<AccountChannelShippingOption> page3 =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountChannelShippingOption3,
				(List<AccountChannelShippingOption>)page3.getItems());
		}
		else {
			Page<AccountChannelShippingOption> page1 =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						id, Pagination.of(1, totalCount + 2));

			List<AccountChannelShippingOption> accountChannelShippingOptions1 =
				(List<AccountChannelShippingOption>)page1.getItems();

			Assert.assertEquals(
				accountChannelShippingOptions1.toString(), totalCount + 2,
				accountChannelShippingOptions1.size());

			Page<AccountChannelShippingOption> page2 =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountChannelShippingOption> accountChannelShippingOptions2 =
				(List<AccountChannelShippingOption>)page2.getItems();

			Assert.assertEquals(
				accountChannelShippingOptions2.toString(), 1,
				accountChannelShippingOptions2.size());

			Page<AccountChannelShippingOption> page3 =
				accountChannelShippingOptionResource.
					getAccountIdAccountChannelShippingOptionPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountChannelShippingOption1,
				(List<AccountChannelShippingOption>)page3.getItems());
			assertContains(
				accountChannelShippingOption2,
				(List<AccountChannelShippingOption>)page3.getItems());
			assertContains(
				accountChannelShippingOption3,
				(List<AccountChannelShippingOption>)page3.getItems());
		}
	}

	protected AccountChannelShippingOption
			testGetAccountIdAccountChannelShippingOptionPage_addAccountChannelShippingOption(
				Long id,
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountChannelShippingOptionPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetAccountIdAccountChannelShippingOptionPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchAccountChannelShippingOption() throws Exception {
		AccountChannelShippingOption postAccountChannelShippingOption =
			testPatchAccountChannelShippingOption_addAccountChannelShippingOption();

		AccountChannelShippingOption randomPatchAccountChannelShippingOption =
			randomPatchAccountChannelShippingOption();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountChannelShippingOption patchAccountChannelShippingOption =
			accountChannelShippingOptionResource.
				patchAccountChannelShippingOption(
					postAccountChannelShippingOption.getId(),
					randomPatchAccountChannelShippingOption);

		AccountChannelShippingOption expectedPatchAccountChannelShippingOption =
			postAccountChannelShippingOption.clone();

		BeanTestUtil.copyProperties(
			randomPatchAccountChannelShippingOption,
			expectedPatchAccountChannelShippingOption);

		AccountChannelShippingOption getAccountChannelShippingOption =
			accountChannelShippingOptionResource.
				getAccountChannelShippingOption(
					patchAccountChannelShippingOption.getId());

		assertEquals(
			expectedPatchAccountChannelShippingOption,
			getAccountChannelShippingOption);
		assertValid(getAccountChannelShippingOption);
	}

	protected AccountChannelShippingOption
			testPatchAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountChannelShippingOption()
		throws Exception {

		AccountChannelShippingOption randomAccountChannelShippingOption =
			randomAccountChannelShippingOption();

		AccountChannelShippingOption postAccountChannelShippingOption =
			testPostAccountByExternalReferenceCodeAccountChannelShippingOption_addAccountChannelShippingOption(
				randomAccountChannelShippingOption);

		assertEquals(
			randomAccountChannelShippingOption,
			postAccountChannelShippingOption);
		assertValid(postAccountChannelShippingOption);
	}

	protected AccountChannelShippingOption
			testPostAccountByExternalReferenceCodeAccountChannelShippingOption_addAccountChannelShippingOption(
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountIdAccountChannelShippingOption()
		throws Exception {

		AccountChannelShippingOption randomAccountChannelShippingOption =
			randomAccountChannelShippingOption();

		AccountChannelShippingOption postAccountChannelShippingOption =
			testPostAccountIdAccountChannelShippingOption_addAccountChannelShippingOption(
				randomAccountChannelShippingOption);

		assertEquals(
			randomAccountChannelShippingOption,
			postAccountChannelShippingOption);
		assertValid(postAccountChannelShippingOption);
	}

	protected AccountChannelShippingOption
			testPostAccountIdAccountChannelShippingOption_addAccountChannelShippingOption(
				AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		AccountChannelShippingOption accountChannelShippingOption1 =
			testBatchEngineDeleteImportTask_addAccountChannelShippingOption();

		testBatchEngineDeleteImportTask_deleteAccountChannelShippingOption(
			200, null, accountChannelShippingOption1.getId());

		assertHttpResponseStatusCode(
			404,
			accountChannelShippingOptionResource.
				getAccountChannelShippingOptionHttpResponse(
					accountChannelShippingOption1.getId()));
	}

	protected AccountChannelShippingOption
			testBatchEngineDeleteImportTask_addAccountChannelShippingOption()
		throws Exception {

		return testDeleteAccountChannelShippingOption_addAccountChannelShippingOption();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteAccountChannelShippingOption(
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
				"com.liferay.headless.commerce.admin.account.dto.v1_0.AccountChannelShippingOption",
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

	protected AccountChannelShippingOption
			testGraphQLAccountChannelShippingOption_addAccountChannelShippingOption()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		AccountChannelShippingOption accountChannelShippingOption,
		List<AccountChannelShippingOption> accountChannelShippingOptions) {

		boolean contains = false;

		for (AccountChannelShippingOption item :
				accountChannelShippingOptions) {

			if (equals(accountChannelShippingOption, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountChannelShippingOptions + " does not contain " +
				accountChannelShippingOption,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountChannelShippingOption accountChannelShippingOption1,
		AccountChannelShippingOption accountChannelShippingOption2) {

		Assert.assertTrue(
			accountChannelShippingOption1 + " does not equal " +
				accountChannelShippingOption2,
			equals(
				accountChannelShippingOption1, accountChannelShippingOption2));
	}

	protected void assertEquals(
		List<AccountChannelShippingOption> accountChannelShippingOptions1,
		List<AccountChannelShippingOption> accountChannelShippingOptions2) {

		Assert.assertEquals(
			accountChannelShippingOptions1.size(),
			accountChannelShippingOptions2.size());

		for (int i = 0; i < accountChannelShippingOptions1.size(); i++) {
			AccountChannelShippingOption accountChannelShippingOption1 =
				accountChannelShippingOptions1.get(i);
			AccountChannelShippingOption accountChannelShippingOption2 =
				accountChannelShippingOptions2.get(i);

			assertEquals(
				accountChannelShippingOption1, accountChannelShippingOption2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountChannelShippingOption> accountChannelShippingOptions1,
		List<AccountChannelShippingOption> accountChannelShippingOptions2) {

		Assert.assertEquals(
			accountChannelShippingOptions1.size(),
			accountChannelShippingOptions2.size());

		for (AccountChannelShippingOption accountChannelShippingOption1 :
				accountChannelShippingOptions1) {

			boolean contains = false;

			for (AccountChannelShippingOption accountChannelShippingOption2 :
					accountChannelShippingOptions2) {

				if (equals(
						accountChannelShippingOption1,
						accountChannelShippingOption2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountChannelShippingOptions2 + " does not contain " +
					accountChannelShippingOption1,
				contains);
		}
	}

	protected void assertValid(
			AccountChannelShippingOption accountChannelShippingOption)
		throws Exception {

		boolean valid = true;

		if (accountChannelShippingOption.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (accountChannelShippingOption.
						getAccountExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (accountChannelShippingOption.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (accountChannelShippingOption.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (accountChannelShippingOption.
						getChannelExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (accountChannelShippingOption.getChannelId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingMethodId", additionalAssertFieldName)) {
				if (accountChannelShippingOption.getShippingMethodId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingMethodKey", additionalAssertFieldName)) {

				if (accountChannelShippingOption.getShippingMethodKey() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("shippingOptionId", additionalAssertFieldName)) {
				if (accountChannelShippingOption.getShippingOptionId() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingOptionKey", additionalAssertFieldName)) {

				if (accountChannelShippingOption.getShippingOptionKey() ==
						null) {

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

	protected void assertValid(Page<AccountChannelShippingOption> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountChannelShippingOption> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountChannelShippingOption>
			accountChannelShippingOptions = page.getItems();

		int size = accountChannelShippingOptions.size();

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

		graphQLFields.add(new GraphQLField("id"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.account.dto.v1_0.
						AccountChannelShippingOption.class)) {

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
		AccountChannelShippingOption accountChannelShippingOption1,
		AccountChannelShippingOption accountChannelShippingOption2) {

		if (accountChannelShippingOption1 == accountChannelShippingOption2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountChannelShippingOption1.
							getAccountExternalReferenceCode(),
						accountChannelShippingOption2.
							getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountChannelShippingOption1.getAccountId(),
						accountChannelShippingOption2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)accountChannelShippingOption1.getActions(),
						(Map)accountChannelShippingOption2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"channelExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountChannelShippingOption1.
							getChannelExternalReferenceCode(),
						accountChannelShippingOption2.
							getChannelExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("channelId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountChannelShippingOption1.getChannelId(),
						accountChannelShippingOption2.getChannelId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountChannelShippingOption1.getId(),
						accountChannelShippingOption2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingMethodId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountChannelShippingOption1.getShippingMethodId(),
						accountChannelShippingOption2.getShippingMethodId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingMethodKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountChannelShippingOption1.getShippingMethodKey(),
						accountChannelShippingOption2.getShippingMethodKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("shippingOptionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountChannelShippingOption1.getShippingOptionId(),
						accountChannelShippingOption2.getShippingOptionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"shippingOptionKey", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountChannelShippingOption1.getShippingOptionKey(),
						accountChannelShippingOption2.getShippingOptionKey())) {

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

		if (!(_accountChannelShippingOptionResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountChannelShippingOptionResource;

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
		AccountChannelShippingOption accountChannelShippingOption) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountExternalReferenceCode")) {
			Object object =
				accountChannelShippingOption.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("channelExternalReferenceCode")) {
			Object object =
				accountChannelShippingOption.getChannelExternalReferenceCode();

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

		if (entityFieldName.equals("channelId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingMethodId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingMethodKey")) {
			Object object = accountChannelShippingOption.getShippingMethodKey();

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

		if (entityFieldName.equals("shippingOptionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("shippingOptionKey")) {
			Object object = accountChannelShippingOption.getShippingOptionKey();

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

	protected AccountChannelShippingOption randomAccountChannelShippingOption()
		throws Exception {

		return new AccountChannelShippingOption() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				channelExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				channelId = RandomTestUtil.randomLong();
				id = RandomTestUtil.randomLong();
				shippingMethodId = RandomTestUtil.randomLong();
				shippingMethodKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				shippingOptionId = RandomTestUtil.randomLong();
				shippingOptionKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected AccountChannelShippingOption
			randomIrrelevantAccountChannelShippingOption()
		throws Exception {

		AccountChannelShippingOption
			randomIrrelevantAccountChannelShippingOption =
				randomAccountChannelShippingOption();

		return randomIrrelevantAccountChannelShippingOption;
	}

	protected AccountChannelShippingOption
			randomPatchAccountChannelShippingOption()
		throws Exception {

		return randomAccountChannelShippingOption();
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

	protected AccountChannelShippingOptionResource
		accountChannelShippingOptionResource;
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
		LogFactoryUtil.getLog(
			BaseAccountChannelShippingOptionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.account.resource.v1_0.
		AccountChannelShippingOptionResource
			_accountChannelShippingOptionResource;

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