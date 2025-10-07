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

import com.liferay.headless.admin.user.client.dto.v1_0.Phone;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.resource.v1_0.PhoneResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.PhoneSerDes;
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
public abstract class BasePhoneResourceTestCase {

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

		_phoneResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		phoneResource = PhoneResource.builder(
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

		Phone phone1 = randomPhone();

		String json = objectMapper.writeValueAsString(phone1);

		Phone phone2 = PhoneSerDes.toDTO(json);

		Assert.assertTrue(equals(phone1, phone2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Phone phone = randomPhone();

		String json1 = objectMapper.writeValueAsString(phone);
		String json2 = PhoneSerDes.toJSON(phone);

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

		Phone phone = randomPhone();

		phone.setExtension(regex);
		phone.setExternalReferenceCode(regex);
		phone.setPhoneNumber(regex);
		phone.setPhoneType(regex);

		String json = PhoneSerDes.toJSON(phone);

		Assert.assertFalse(json.contains(regex));

		phone = PhoneSerDes.toDTO(json);

		Assert.assertEquals(regex, phone.getExtension());
		Assert.assertEquals(regex, phone.getExternalReferenceCode());
		Assert.assertEquals(regex, phone.getPhoneNumber());
		Assert.assertEquals(regex, phone.getPhoneType());
	}

	@Test
	public void testDeletePhone() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Phone phone = testDeletePhone_addPhone();

		assertHttpResponseStatusCode(
			204, phoneResource.deletePhoneHttpResponse(phone.getId()));

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone.getId()));
		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(0L));
	}

	protected Phone testDeletePhone_addPhone() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePhone() throws Exception {

		// No namespace

		Phone phone1 = testGraphQLDeletePhone_addPhone();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePhone",
						new HashMap<String, Object>() {
							{
								put("phoneId", phone1.getId());
							}
						})),
				"JSONObject/data", "Object/deletePhone"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"phone",
					new HashMap<String, Object>() {
						{
							put("phoneId", phone1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		Phone phone2 = testGraphQLDeletePhone_addPhone();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deletePhone",
							new HashMap<String, Object>() {
								{
									put("phoneId", phone2.getId());
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deletePhone"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"phone",
						new HashMap<String, Object>() {
							{
								put("phoneId", phone2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Phone testGraphQLDeletePhone_addPhone() throws Exception {
		return testGraphQLPhone_addPhone();
	}

	@Test
	public void testDeletePhoneBatch() throws Exception {
		Phone phone1 = testDeletePhoneBatch_addPhone();

		testDeletePhoneBatch_deletePhone(
			202, phone1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone1.getId()));

		phone1 = testDeletePhoneBatch_addPhone();

		testDeletePhoneBatch_deletePhone(202, null, phone1.getId());

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone1.getId()));

		phone1 = testDeletePhoneBatch_addPhone();
		Phone phone2 = testDeletePhoneBatch_addPhone();

		testDeletePhoneBatch_deletePhone(
			202, phone2.getExternalReferenceCode(), phone1.getId());

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone1.getId()));
		assertHttpResponseStatusCode(
			200, phoneResource.getPhoneHttpResponse(phone2.getId()));

		testDeletePhoneBatch_deletePhone(
			202, phone2.getExternalReferenceCode(), phone1.getId());

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone2.getId()));
	}

	protected Phone testDeletePhoneBatch_addPhone() throws Exception {
		return testDeletePhone_addPhone();
	}

	protected void testDeletePhoneBatch_deletePhone(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			phoneResource.deletePhoneBatchHttpResponse(
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
	public void testDeletePhoneByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Phone phone = testDeletePhoneByExternalReferenceCode_addPhone();

		assertHttpResponseStatusCode(
			204,
			phoneResource.deletePhoneByExternalReferenceCodeHttpResponse(
				phone.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			phoneResource.getPhoneByExternalReferenceCodeHttpResponse(
				phone.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			phoneResource.getPhoneByExternalReferenceCodeHttpResponse("-"));
	}

	protected Phone testDeletePhoneByExternalReferenceCode_addPhone()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePhoneByExternalReferenceCode()
		throws Exception {

		// No namespace

		Phone phone1 = testGraphQLDeletePhoneByExternalReferenceCode_addPhone();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePhoneByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + phone1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data",
				"Object/deletePhoneByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"phoneByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + phone1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessAdminUser_v1_0

		Phone phone2 = testGraphQLDeletePhoneByExternalReferenceCode_addPhone();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessAdminUser_v1_0",
						new GraphQLField(
							"deletePhoneByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											phone2.getExternalReferenceCode() +
												"\"");
								}
							}))),
				"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
				"Object/deletePhoneByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessAdminUser_v1_0",
					new GraphQLField(
						"phoneByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + phone2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Phone testGraphQLDeletePhoneByExternalReferenceCode_addPhone()
		throws Exception {

		return testGraphQLPhone_addPhone();
	}

	@Test
	public void testGetAccountByExternalReferenceCodePhonesPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodePhonesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodePhonesPage_getIrrelevantExternalReferenceCode();

		Page<Phone> page =
			phoneResource.getAccountByExternalReferenceCodePhonesPage(
				externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Phone irrelevantPhone =
				testGetAccountByExternalReferenceCodePhonesPage_addPhone(
					irrelevantExternalReferenceCode, randomIrrelevantPhone());

			page = phoneResource.getAccountByExternalReferenceCodePhonesPage(
				irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPhone, (List<Phone>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodePhonesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Phone phone1 = testGetAccountByExternalReferenceCodePhonesPage_addPhone(
			externalReferenceCode, randomPhone());

		Phone phone2 = testGetAccountByExternalReferenceCodePhonesPage_addPhone(
			externalReferenceCode, randomPhone());

		page = phoneResource.getAccountByExternalReferenceCodePhonesPage(
			externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(phone1, (List<Phone>)page.getItems());
		assertContains(phone2, (List<Phone>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodePhonesPage_getExpectedActions(
				externalReferenceCode));

		phoneResource.deletePhone(phone1.getId());

		phoneResource.deletePhone(phone2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodePhonesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Phone testGetAccountByExternalReferenceCodePhonesPage_addPhone(
			String externalReferenceCode, Phone phone)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodePhonesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodePhonesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountPhonesPage() throws Exception {
		Long accountId = testGetAccountPhonesPage_getAccountId();
		Long irrelevantAccountId =
			testGetAccountPhonesPage_getIrrelevantAccountId();

		Page<Phone> page = phoneResource.getAccountPhonesPage(accountId);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountId != null) {
			Phone irrelevantPhone = testGetAccountPhonesPage_addPhone(
				irrelevantAccountId, randomIrrelevantPhone());

			page = phoneResource.getAccountPhonesPage(irrelevantAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPhone, (List<Phone>)page.getItems());
			assertValid(
				page,
				testGetAccountPhonesPage_getExpectedActions(
					irrelevantAccountId));
		}

		Phone phone1 = testGetAccountPhonesPage_addPhone(
			accountId, randomPhone());

		Phone phone2 = testGetAccountPhonesPage_addPhone(
			accountId, randomPhone());

		page = phoneResource.getAccountPhonesPage(accountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(phone1, (List<Phone>)page.getItems());
		assertContains(phone2, (List<Phone>)page.getItems());
		assertValid(
			page, testGetAccountPhonesPage_getExpectedActions(accountId));

		phoneResource.deletePhone(phone1.getId());

		phoneResource.deletePhone(phone2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetAccountPhonesPage_getExpectedActions(Long accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Phone testGetAccountPhonesPage_addPhone(
			Long accountId, Phone phone)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountPhonesPage_getAccountId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountPhonesPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationByExternalReferenceCodePhonesPage()
		throws Exception {

		String externalReferenceCode =
			testGetOrganizationByExternalReferenceCodePhonesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetOrganizationByExternalReferenceCodePhonesPage_getIrrelevantExternalReferenceCode();

		Page<Phone> page =
			phoneResource.getOrganizationByExternalReferenceCodePhonesPage(
				externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Phone irrelevantPhone =
				testGetOrganizationByExternalReferenceCodePhonesPage_addPhone(
					irrelevantExternalReferenceCode, randomIrrelevantPhone());

			page =
				phoneResource.getOrganizationByExternalReferenceCodePhonesPage(
					irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPhone, (List<Phone>)page.getItems());
			assertValid(
				page,
				testGetOrganizationByExternalReferenceCodePhonesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Phone phone1 =
			testGetOrganizationByExternalReferenceCodePhonesPage_addPhone(
				externalReferenceCode, randomPhone());

		Phone phone2 =
			testGetOrganizationByExternalReferenceCodePhonesPage_addPhone(
				externalReferenceCode, randomPhone());

		page = phoneResource.getOrganizationByExternalReferenceCodePhonesPage(
			externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(phone1, (List<Phone>)page.getItems());
		assertContains(phone2, (List<Phone>)page.getItems());
		assertValid(
			page,
			testGetOrganizationByExternalReferenceCodePhonesPage_getExpectedActions(
				externalReferenceCode));

		phoneResource.deletePhone(phone1.getId());

		phoneResource.deletePhone(phone2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationByExternalReferenceCodePhonesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Phone
			testGetOrganizationByExternalReferenceCodePhonesPage_addPhone(
				String externalReferenceCode, Phone phone)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodePhonesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetOrganizationByExternalReferenceCodePhonesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetOrganizationPhonesPage() throws Exception {
		String organizationId =
			testGetOrganizationPhonesPage_getOrganizationId();
		String irrelevantOrganizationId =
			testGetOrganizationPhonesPage_getIrrelevantOrganizationId();

		Page<Phone> page = phoneResource.getOrganizationPhonesPage(
			organizationId);

		long totalCount = page.getTotalCount();

		if (irrelevantOrganizationId != null) {
			Phone irrelevantPhone = testGetOrganizationPhonesPage_addPhone(
				irrelevantOrganizationId, randomIrrelevantPhone());

			page = phoneResource.getOrganizationPhonesPage(
				irrelevantOrganizationId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPhone, (List<Phone>)page.getItems());
			assertValid(
				page,
				testGetOrganizationPhonesPage_getExpectedActions(
					irrelevantOrganizationId));
		}

		Phone phone1 = testGetOrganizationPhonesPage_addPhone(
			organizationId, randomPhone());

		Phone phone2 = testGetOrganizationPhonesPage_addPhone(
			organizationId, randomPhone());

		page = phoneResource.getOrganizationPhonesPage(organizationId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(phone1, (List<Phone>)page.getItems());
		assertContains(phone2, (List<Phone>)page.getItems());
		assertValid(
			page,
			testGetOrganizationPhonesPage_getExpectedActions(organizationId));

		phoneResource.deletePhone(phone1.getId());

		phoneResource.deletePhone(phone2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetOrganizationPhonesPage_getExpectedActions(
				String organizationId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Phone testGetOrganizationPhonesPage_addPhone(
			String organizationId, Phone phone)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetOrganizationPhonesPage_getOrganizationId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String testGetOrganizationPhonesPage_getIrrelevantOrganizationId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPhone() throws Exception {
		Phone postPhone = testGetPhone_addPhone();

		Phone getPhone = phoneResource.getPhone(postPhone.getId());

		assertEquals(postPhone, getPhone);
		assertValid(getPhone);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Phone postPhone = testGetPhone_addPhone();

		Phone getPhone = phoneResource.getPhone(postPhone.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany, "com.liferay.headless.admin.user.dto.v1_0.Phone"
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

		Object item = vulcanCRUDItemDelegate.getItem(postPhone.getId());

		assertEquals(getPhone, PhoneSerDes.toDTO(item.toString()));
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

	protected Phone testGetPhone_addPhone() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPhone() throws Exception {
		Phone phone = testGraphQLGetPhone_addPhone();

		// No namespace

		Assert.assertTrue(
			equals(
				phone,
				PhoneSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"phone",
								new HashMap<String, Object>() {
									{
										put("phoneId", phone.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/phone"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				phone,
				PhoneSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"phone",
									new HashMap<String, Object>() {
										{
											put("phoneId", phone.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/phone"))));
	}

	@Test
	public void testGraphQLGetPhoneNotFound() throws Exception {
		Long irrelevantPhoneId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"phone",
						new HashMap<String, Object>() {
							{
								put("phoneId", irrelevantPhoneId);
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
							"phone",
							new HashMap<String, Object>() {
								{
									put("phoneId", irrelevantPhoneId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Phone testGraphQLGetPhone_addPhone() throws Exception {
		return testGraphQLPhone_addPhone();
	}

	@Test
	public void testGetPhoneByExternalReferenceCode() throws Exception {
		Phone postPhone = testGetPhoneByExternalReferenceCode_addPhone();

		Phone getPhone = phoneResource.getPhoneByExternalReferenceCode(
			postPhone.getExternalReferenceCode());

		assertEquals(postPhone, getPhone);
		assertValid(getPhone);
	}

	protected Phone testGetPhoneByExternalReferenceCode_addPhone()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetPhoneByExternalReferenceCode() throws Exception {
		Phone phone = testGraphQLGetPhoneByExternalReferenceCode_addPhone();

		// No namespace

		Assert.assertTrue(
			equals(
				phone,
				PhoneSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"phoneByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												phone.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/phoneByExternalReferenceCode"))));

		// Using the namespace headlessAdminUser_v1_0

		Assert.assertTrue(
			equals(
				phone,
				PhoneSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessAdminUser_v1_0",
								new GraphQLField(
									"phoneByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													phone.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data", "JSONObject/headlessAdminUser_v1_0",
						"Object/phoneByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetPhoneByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"phoneByExternalReferenceCode",
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
							"phoneByExternalReferenceCode",
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

	protected Phone testGraphQLGetPhoneByExternalReferenceCode_addPhone()
		throws Exception {

		return testGraphQLPhone_addPhone();
	}

	@Test
	public void testGetUserAccountByExternalReferenceCodePhonesPage()
		throws Exception {

		String externalReferenceCode =
			testGetUserAccountByExternalReferenceCodePhonesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetUserAccountByExternalReferenceCodePhonesPage_getIrrelevantExternalReferenceCode();

		Page<Phone> page =
			phoneResource.getUserAccountByExternalReferenceCodePhonesPage(
				externalReferenceCode);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Phone irrelevantPhone =
				testGetUserAccountByExternalReferenceCodePhonesPage_addPhone(
					irrelevantExternalReferenceCode, randomIrrelevantPhone());

			page =
				phoneResource.getUserAccountByExternalReferenceCodePhonesPage(
					irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPhone, (List<Phone>)page.getItems());
			assertValid(
				page,
				testGetUserAccountByExternalReferenceCodePhonesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Phone phone1 =
			testGetUserAccountByExternalReferenceCodePhonesPage_addPhone(
				externalReferenceCode, randomPhone());

		Phone phone2 =
			testGetUserAccountByExternalReferenceCodePhonesPage_addPhone(
				externalReferenceCode, randomPhone());

		page = phoneResource.getUserAccountByExternalReferenceCodePhonesPage(
			externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(phone1, (List<Phone>)page.getItems());
		assertContains(phone2, (List<Phone>)page.getItems());
		assertValid(
			page,
			testGetUserAccountByExternalReferenceCodePhonesPage_getExpectedActions(
				externalReferenceCode));

		phoneResource.deletePhone(phone1.getId());

		phoneResource.deletePhone(phone2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountByExternalReferenceCodePhonesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Phone
			testGetUserAccountByExternalReferenceCodePhonesPage_addPhone(
				String externalReferenceCode, Phone phone)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserAccountByExternalReferenceCodePhonesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetUserAccountByExternalReferenceCodePhonesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetUserAccountPhonesPage() throws Exception {
		Long userAccountId = testGetUserAccountPhonesPage_getUserAccountId();
		Long irrelevantUserAccountId =
			testGetUserAccountPhonesPage_getIrrelevantUserAccountId();

		Page<Phone> page = phoneResource.getUserAccountPhonesPage(
			userAccountId);

		long totalCount = page.getTotalCount();

		if (irrelevantUserAccountId != null) {
			Phone irrelevantPhone = testGetUserAccountPhonesPage_addPhone(
				irrelevantUserAccountId, randomIrrelevantPhone());

			page = phoneResource.getUserAccountPhonesPage(
				irrelevantUserAccountId);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantPhone, (List<Phone>)page.getItems());
			assertValid(
				page,
				testGetUserAccountPhonesPage_getExpectedActions(
					irrelevantUserAccountId));
		}

		Phone phone1 = testGetUserAccountPhonesPage_addPhone(
			userAccountId, randomPhone());

		Phone phone2 = testGetUserAccountPhonesPage_addPhone(
			userAccountId, randomPhone());

		page = phoneResource.getUserAccountPhonesPage(userAccountId);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(phone1, (List<Phone>)page.getItems());
		assertContains(phone2, (List<Phone>)page.getItems());
		assertValid(
			page,
			testGetUserAccountPhonesPage_getExpectedActions(userAccountId));

		phoneResource.deletePhone(phone1.getId());

		phoneResource.deletePhone(phone2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUserAccountPhonesPage_getExpectedActions(Long userAccountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected Phone testGetUserAccountPhonesPage_addPhone(
			Long userAccountId, Phone phone)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserAccountPhonesPage_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetUserAccountPhonesPage_getIrrelevantUserAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchPhone() throws Exception {
		Phone postPhone = testPatchPhone_addPhone();

		Phone randomPatchPhone = randomPatchPhone();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Phone patchPhone = phoneResource.patchPhone(
			postPhone.getId(), randomPatchPhone);

		Phone expectedPatchPhone = postPhone.clone();

		BeanTestUtil.copyProperties(randomPatchPhone, expectedPatchPhone);

		Phone getPhone = phoneResource.getPhone(patchPhone.getId());

		assertEquals(expectedPatchPhone, getPhone);
		assertValid(getPhone);
	}

	protected Phone testPatchPhone_addPhone() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchPhoneByExternalReferenceCode() throws Exception {
		Phone postPhone = testPatchPhoneByExternalReferenceCode_addPhone();

		Phone randomPatchPhone = randomPatchPhone();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Phone patchPhone = phoneResource.patchPhoneByExternalReferenceCode(
			postPhone.getExternalReferenceCode(), randomPatchPhone);

		Phone expectedPatchPhone = postPhone.clone();

		BeanTestUtil.copyProperties(randomPatchPhone, expectedPatchPhone);

		Phone getPhone = phoneResource.getPhoneByExternalReferenceCode(
			patchPhone.getExternalReferenceCode());

		assertEquals(expectedPatchPhone, getPhone);
		assertValid(getPhone);
	}

	protected Phone testPatchPhoneByExternalReferenceCode_addPhone()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Phone phone1 = testBatchEngineDeleteImportTask_addPhone();

		testBatchEngineDeleteImportTask_deletePhone(
			200, phone1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone1.getId()));

		phone1 = testBatchEngineDeleteImportTask_addPhone();

		testBatchEngineDeleteImportTask_deletePhone(200, null, phone1.getId());

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone1.getId()));

		phone1 = testBatchEngineDeleteImportTask_addPhone();
		Phone phone2 = testBatchEngineDeleteImportTask_addPhone();

		testBatchEngineDeleteImportTask_deletePhone(
			200, phone2.getExternalReferenceCode(), phone1.getId());

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone1.getId()));
		assertHttpResponseStatusCode(
			200, phoneResource.getPhoneHttpResponse(phone2.getId()));

		testBatchEngineDeleteImportTask_deletePhone(
			200, phone2.getExternalReferenceCode(), phone1.getId());

		assertHttpResponseStatusCode(
			404, phoneResource.getPhoneHttpResponse(phone2.getId()));
	}

	protected Phone testBatchEngineDeleteImportTask_addPhone()
		throws Exception {

		return testDeletePhone_addPhone();
	}

	protected void testBatchEngineDeleteImportTask_deletePhone(
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
				"com.liferay.headless.admin.user.dto.v1_0.Phone", null, null,
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

	protected Phone testGraphQLPhone_addPhone() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(Phone phone, List<Phone> phones) {
		boolean contains = false;

		for (Phone item : phones) {
			if (equals(phone, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(phones + " does not contain " + phone, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Phone phone1, Phone phone2) {
		Assert.assertTrue(
			phone1 + " does not equal " + phone2, equals(phone1, phone2));
	}

	protected void assertEquals(List<Phone> phones1, List<Phone> phones2) {
		Assert.assertEquals(phones1.size(), phones2.size());

		for (int i = 0; i < phones1.size(); i++) {
			Phone phone1 = phones1.get(i);
			Phone phone2 = phones2.get(i);

			assertEquals(phone1, phone2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<Phone> phones1, List<Phone> phones2) {

		Assert.assertEquals(phones1.size(), phones2.size());

		for (Phone phone1 : phones1) {
			boolean contains = false;

			for (Phone phone2 : phones2) {
				if (equals(phone1, phone2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				phones2 + " does not contain " + phone1, contains);
		}
	}

	protected void assertValid(Phone phone) throws Exception {
		boolean valid = true;

		if (phone.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("extension", additionalAssertFieldName)) {
				if (phone.getExtension() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (phone.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (phone.getPhoneNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("phoneType", additionalAssertFieldName)) {
				if (phone.getPhoneType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (phone.getPrimary() == null) {
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

	protected void assertValid(Page<Phone> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Phone> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Phone> phones = page.getItems();

		int size = phones.size();

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
					com.liferay.headless.admin.user.dto.v1_0.Phone.class)) {

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

	protected boolean equals(Phone phone1, Phone phone2) {
		if (phone1 == phone2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("extension", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						phone1.getExtension(), phone2.getExtension())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						phone1.getExternalReferenceCode(),
						phone2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(phone1.getId(), phone2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("phoneNumber", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						phone1.getPhoneNumber(), phone2.getPhoneNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("phoneType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						phone1.getPhoneType(), phone2.getPhoneType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("primary", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						phone1.getPrimary(), phone2.getPrimary())) {

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

		if (!(_phoneResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_phoneResource;

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
		EntityField entityField, String operator, Phone phone) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("extension")) {
			Object object = phone.getExtension();

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
			Object object = phone.getExternalReferenceCode();

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

		if (entityFieldName.equals("phoneNumber")) {
			Object object = phone.getPhoneNumber();

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

		if (entityFieldName.equals("phoneType")) {
			Object object = phone.getPhoneType();

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

		if (entityFieldName.equals("primary")) {
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

	protected Phone randomPhone() throws Exception {
		return new Phone() {
			{
				extension = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				phoneNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				phoneType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				primary = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected Phone randomIrrelevantPhone() throws Exception {
		Phone randomIrrelevantPhone = randomPhone();

		return randomIrrelevantPhone;
	}

	protected Phone randomPatchPhone() throws Exception {
		return randomPhone();
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

	protected PhoneResource phoneResource;
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
		LogFactoryUtil.getLog(BasePhoneResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.PhoneResource
		_phoneResource;

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