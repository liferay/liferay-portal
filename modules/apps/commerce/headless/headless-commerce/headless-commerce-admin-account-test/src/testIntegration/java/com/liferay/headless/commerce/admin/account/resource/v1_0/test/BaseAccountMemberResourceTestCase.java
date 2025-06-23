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

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountMember;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.account.client.pagination.Page;
import com.liferay.headless.commerce.admin.account.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.AccountMemberResource;
import com.liferay.headless.commerce.admin.account.client.serdes.v1_0.AccountMemberSerDes;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.util.PropsValues;
import com.liferay.portal.vulcan.resource.EntityModelResource;

import jakarta.annotation.Generated;

import jakarta.ws.rs.core.MultivaluedHashMap;

import java.lang.reflect.Method;

import java.text.Format;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

/**
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseAccountMemberResourceTestCase {

	@ClassRule
	@Rule
	public static final LiferayIntegrationTestRule liferayIntegrationTestRule =
		new LiferayIntegrationTestRule();

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

		_accountMemberResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountMemberResource = AccountMemberResource.builder(
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

		AccountMember accountMember1 = randomAccountMember();

		String json = objectMapper.writeValueAsString(accountMember1);

		AccountMember accountMember2 = AccountMemberSerDes.toDTO(json);

		Assert.assertTrue(equals(accountMember1, accountMember2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountMember accountMember = randomAccountMember();

		String json1 = objectMapper.writeValueAsString(accountMember);
		String json2 = AccountMemberSerDes.toJSON(accountMember);

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

		AccountMember accountMember = randomAccountMember();

		accountMember.setEmail(regex);
		accountMember.setExternalReferenceCode(regex);
		accountMember.setName(regex);
		accountMember.setUserExternalReferenceCode(regex);

		String json = AccountMemberSerDes.toJSON(accountMember);

		Assert.assertFalse(json.contains(regex));

		accountMember = AccountMemberSerDes.toDTO(json);

		Assert.assertEquals(regex, accountMember.getEmail());
		Assert.assertEquals(regex, accountMember.getExternalReferenceCode());
		Assert.assertEquals(regex, accountMember.getName());
		Assert.assertEquals(
			regex, accountMember.getUserExternalReferenceCode());
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountMember accountMember =
			testDeleteAccountByExternalReferenceCodeAccountMember_addAccountMember();

		assertHttpResponseStatusCode(
			204,
			accountMemberResource.
				deleteAccountByExternalReferenceCodeAccountMemberHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
						accountMember),
					accountMember.getUserId()));

		assertHttpResponseStatusCode(
			404,
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMemberHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
						accountMember),
					accountMember.getUserId()));
		assertHttpResponseStatusCode(
			404,
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMemberHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
						accountMember),
					accountMember.getUserId()));
	}

	protected AccountMember
			testDeleteAccountByExternalReferenceCodeAccountMember_addAccountMember()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
				AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAccountIdAccountMember() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountMember accountMember =
			testDeleteAccountIdAccountMember_addAccountMember();

		assertHttpResponseStatusCode(
			204,
			accountMemberResource.deleteAccountIdAccountMemberHttpResponse(
				testDeleteAccountIdAccountMember_getId(),
				accountMember.getUserId()));

		assertHttpResponseStatusCode(
			404,
			accountMemberResource.getAccountIdAccountMemberHttpResponse(
				testDeleteAccountIdAccountMember_getId(),
				accountMember.getUserId()));
		assertHttpResponseStatusCode(
			404,
			accountMemberResource.getAccountIdAccountMemberHttpResponse(
				testDeleteAccountIdAccountMember_getId(),
				accountMember.getUserId()));
	}

	protected AccountMember testDeleteAccountIdAccountMember_addAccountMember()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteAccountIdAccountMember_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		AccountMember postAccountMember =
			testGetAccountByExternalReferenceCodeAccountMember_addAccountMember();

		AccountMember getAccountMember =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMember(
					testGetAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
						postAccountMember),
					postAccountMember.getUserId());

		assertEquals(postAccountMember, getAccountMember);
		assertValid(getAccountMember);
	}

	protected AccountMember
			testGetAccountByExternalReferenceCodeAccountMember_addAccountMember()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
				AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		AccountMember accountMember =
			testGraphQLGetAccountByExternalReferenceCodeAccountMember_addAccountMember();

		// No namespace

		Assert.assertTrue(
			equals(
				accountMember,
				AccountMemberSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountByExternalReferenceCodeAccountMember",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												testGraphQLGetAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
													accountMember) + "\"");
										put(
											"userId",
											accountMember.getUserId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/accountByExternalReferenceCodeAccountMember"))));

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertTrue(
			equals(
				accountMember,
				AccountMemberSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminAccount_v1_0",
								new GraphQLField(
									"accountByExternalReferenceCodeAccountMember",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													testGraphQLGetAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
														accountMember) + "\"");
											put(
												"userId",
												accountMember.getUserId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminAccount_v1_0",
						"Object/accountByExternalReferenceCodeAccountMember"))));
	}

	protected String
			testGraphQLGetAccountByExternalReferenceCodeAccountMember_getExternalReferenceCode(
				AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeAccountMemberNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		Long irrelevantUserId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountByExternalReferenceCodeAccountMember",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									irrelevantExternalReferenceCode);
								put("userId", irrelevantUserId);
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
							"accountByExternalReferenceCodeAccountMember",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										irrelevantExternalReferenceCode);
									put("userId", irrelevantUserId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected AccountMember
			testGraphQLGetAccountByExternalReferenceCodeAccountMember_addAccountMember()
		throws Exception {

		return testGraphQLAccountMember_addAccountMember();
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountMembersPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountMembersPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountMembersPage_getIrrelevantExternalReferenceCode();

		Page<AccountMember> page =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMembersPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			AccountMember irrelevantAccountMember =
				testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountMember());

			page =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountMember, (List<AccountMember>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeAccountMembersPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		AccountMember accountMember1 =
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				externalReferenceCode, randomAccountMember());

		AccountMember accountMember2 =
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				externalReferenceCode, randomAccountMember());

		page =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMembersPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountMember1, (List<AccountMember>)page.getItems());
		assertContains(accountMember2, (List<AccountMember>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeAccountMembersPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeAccountMembersPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountMembersPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountMembersPage_getExternalReferenceCode();

		Page<AccountMember> accountMembersPage =
			accountMemberResource.
				getAccountByExternalReferenceCodeAccountMembersPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			accountMembersPage.getTotalCount());

		AccountMember accountMember1 =
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				externalReferenceCode, randomAccountMember());

		AccountMember accountMember2 =
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				externalReferenceCode, randomAccountMember());

		AccountMember accountMember3 =
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				externalReferenceCode, randomAccountMember());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountMember> page1 =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountMember1, (List<AccountMember>)page1.getItems());

			Page<AccountMember> page2 =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountMember2, (List<AccountMember>)page2.getItems());

			Page<AccountMember> page3 =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountMember3, (List<AccountMember>)page3.getItems());
		}
		else {
			Page<AccountMember> page1 =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<AccountMember> accountMembers1 =
				(List<AccountMember>)page1.getItems();

			Assert.assertEquals(
				accountMembers1.toString(), totalCount + 2,
				accountMembers1.size());

			Page<AccountMember> page2 =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountMember> accountMembers2 =
				(List<AccountMember>)page2.getItems();

			Assert.assertEquals(
				accountMembers2.toString(), 1, accountMembers2.size());

			Page<AccountMember> page3 =
				accountMemberResource.
					getAccountByExternalReferenceCodeAccountMembersPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountMember1, (List<AccountMember>)page3.getItems());
			assertContains(
				accountMember2, (List<AccountMember>)page3.getItems());
			assertContains(
				accountMember3, (List<AccountMember>)page3.getItems());
		}
	}

	protected AccountMember
			testGetAccountByExternalReferenceCodeAccountMembersPage_addAccountMember(
				String externalReferenceCode, AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountMembersPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountMembersPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountIdAccountMember() throws Exception {
		AccountMember postAccountMember =
			testGetAccountIdAccountMember_addAccountMember();

		AccountMember getAccountMember =
			accountMemberResource.getAccountIdAccountMember(
				testGetAccountIdAccountMember_getId(),
				postAccountMember.getUserId());

		assertEquals(postAccountMember, getAccountMember);
		assertValid(getAccountMember);
	}

	protected AccountMember testGetAccountIdAccountMember_addAccountMember()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountMember_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountIdAccountMember() throws Exception {
		AccountMember accountMember =
			testGraphQLGetAccountIdAccountMember_addAccountMember();

		// No namespace

		Assert.assertTrue(
			equals(
				accountMember,
				AccountMemberSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"accountIdAccountMember",
								new HashMap<String, Object>() {
									{
										put(
											"id",
											testGraphQLGetAccountIdAccountMember_getId());
										put(
											"userId",
											accountMember.getUserId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/accountIdAccountMember"))));

		// Using the namespace headlessCommerceAdminAccount_v1_0

		Assert.assertTrue(
			equals(
				accountMember,
				AccountMemberSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminAccount_v1_0",
								new GraphQLField(
									"accountIdAccountMember",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												testGraphQLGetAccountIdAccountMember_getId());
											put(
												"userId",
												accountMember.getUserId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminAccount_v1_0",
						"Object/accountIdAccountMember"))));
	}

	protected Long testGraphQLGetAccountIdAccountMember_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetAccountIdAccountMemberNotFound()
		throws Exception {

		Long irrelevantId = RandomTestUtil.randomLong();
		Long irrelevantUserId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"accountIdAccountMember",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
								put("userId", irrelevantUserId);
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
							"accountIdAccountMember",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
									put("userId", irrelevantUserId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected AccountMember
			testGraphQLGetAccountIdAccountMember_addAccountMember()
		throws Exception {

		return testGraphQLAccountMember_addAccountMember();
	}

	@Test
	public void testGetAccountIdAccountMembersPage() throws Exception {
		Long id = testGetAccountIdAccountMembersPage_getId();
		Long irrelevantId =
			testGetAccountIdAccountMembersPage_getIrrelevantId();

		Page<AccountMember> page =
			accountMemberResource.getAccountIdAccountMembersPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			AccountMember irrelevantAccountMember =
				testGetAccountIdAccountMembersPage_addAccountMember(
					irrelevantId, randomIrrelevantAccountMember());

			page = accountMemberResource.getAccountIdAccountMembersPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountMember, (List<AccountMember>)page.getItems());
			assertValid(
				page,
				testGetAccountIdAccountMembersPage_getExpectedActions(
					irrelevantId));
		}

		AccountMember accountMember1 =
			testGetAccountIdAccountMembersPage_addAccountMember(
				id, randomAccountMember());

		AccountMember accountMember2 =
			testGetAccountIdAccountMembersPage_addAccountMember(
				id, randomAccountMember());

		page = accountMemberResource.getAccountIdAccountMembersPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountMember1, (List<AccountMember>)page.getItems());
		assertContains(accountMember2, (List<AccountMember>)page.getItems());
		assertValid(
			page, testGetAccountIdAccountMembersPage_getExpectedActions(id));
	}

	protected Map<String, Map<String, String>>
			testGetAccountIdAccountMembersPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountIdAccountMembersPageWithPagination()
		throws Exception {

		Long id = testGetAccountIdAccountMembersPage_getId();

		Page<AccountMember> accountMembersPage =
			accountMemberResource.getAccountIdAccountMembersPage(id, null);

		int totalCount = GetterUtil.getInteger(
			accountMembersPage.getTotalCount());

		AccountMember accountMember1 =
			testGetAccountIdAccountMembersPage_addAccountMember(
				id, randomAccountMember());

		AccountMember accountMember2 =
			testGetAccountIdAccountMembersPage_addAccountMember(
				id, randomAccountMember());

		AccountMember accountMember3 =
			testGetAccountIdAccountMembersPage_addAccountMember(
				id, randomAccountMember());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountMember> page1 =
				accountMemberResource.getAccountIdAccountMembersPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountMember1, (List<AccountMember>)page1.getItems());

			Page<AccountMember> page2 =
				accountMemberResource.getAccountIdAccountMembersPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				accountMember2, (List<AccountMember>)page2.getItems());

			Page<AccountMember> page3 =
				accountMemberResource.getAccountIdAccountMembersPage(
					id,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				accountMember3, (List<AccountMember>)page3.getItems());
		}
		else {
			Page<AccountMember> page1 =
				accountMemberResource.getAccountIdAccountMembersPage(
					id, Pagination.of(1, totalCount + 2));

			List<AccountMember> accountMembers1 =
				(List<AccountMember>)page1.getItems();

			Assert.assertEquals(
				accountMembers1.toString(), totalCount + 2,
				accountMembers1.size());

			Page<AccountMember> page2 =
				accountMemberResource.getAccountIdAccountMembersPage(
					id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountMember> accountMembers2 =
				(List<AccountMember>)page2.getItems();

			Assert.assertEquals(
				accountMembers2.toString(), 1, accountMembers2.size());

			Page<AccountMember> page3 =
				accountMemberResource.getAccountIdAccountMembersPage(
					id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountMember1, (List<AccountMember>)page3.getItems());
			assertContains(
				accountMember2, (List<AccountMember>)page3.getItems());
			assertContains(
				accountMember3, (List<AccountMember>)page3.getItems());
		}
	}

	protected AccountMember testGetAccountIdAccountMembersPage_addAccountMember(
			Long id, AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountMembersPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountMembersPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPatchAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPatchAccountIdAccountMember() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountMember()
		throws Exception {

		AccountMember randomAccountMember = randomAccountMember();

		AccountMember postAccountMember =
			testPostAccountByExternalReferenceCodeAccountMember_addAccountMember(
				randomAccountMember);

		assertEquals(randomAccountMember, postAccountMember);
		assertValid(postAccountMember);
	}

	protected AccountMember
			testPostAccountByExternalReferenceCodeAccountMember_addAccountMember(
				AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountIdAccountMember() throws Exception {
		AccountMember randomAccountMember = randomAccountMember();

		AccountMember postAccountMember =
			testPostAccountIdAccountMember_addAccountMember(
				randomAccountMember);

		assertEquals(randomAccountMember, postAccountMember);
		assertValid(postAccountMember);
	}

	protected AccountMember testPostAccountIdAccountMember_addAccountMember(
			AccountMember accountMember)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected AccountMember testGraphQLAccountMember_addAccountMember()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		AccountMember accountMember, List<AccountMember> accountMembers) {

		boolean contains = false;

		for (AccountMember item : accountMembers) {
			if (equals(accountMember, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountMembers + " does not contain " + accountMember, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountMember accountMember1, AccountMember accountMember2) {

		Assert.assertTrue(
			accountMember1 + " does not equal " + accountMember2,
			equals(accountMember1, accountMember2));
	}

	protected void assertEquals(
		List<AccountMember> accountMembers1,
		List<AccountMember> accountMembers2) {

		Assert.assertEquals(accountMembers1.size(), accountMembers2.size());

		for (int i = 0; i < accountMembers1.size(); i++) {
			AccountMember accountMember1 = accountMembers1.get(i);
			AccountMember accountMember2 = accountMembers2.get(i);

			assertEquals(accountMember1, accountMember2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountMember> accountMembers1,
		List<AccountMember> accountMembers2) {

		Assert.assertEquals(accountMembers1.size(), accountMembers2.size());

		for (AccountMember accountMember1 : accountMembers1) {
			boolean contains = false;

			for (AccountMember accountMember2 : accountMembers2) {
				if (equals(accountMember1, accountMember2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountMembers2 + " does not contain " + accountMember1,
				contains);
		}
	}

	protected void assertValid(AccountMember accountMember) throws Exception {
		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (accountMember.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountRoles", additionalAssertFieldName)) {
				if (accountMember.getAccountRoles() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("email", additionalAssertFieldName)) {
				if (accountMember.getEmail() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (accountMember.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountMember.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"userExternalReferenceCode", additionalAssertFieldName)) {

				if (accountMember.getUserExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (accountMember.getUserId() == null) {
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

	protected void assertValid(Page<AccountMember> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountMember> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountMember> accountMembers = page.getItems();

		int size = accountMembers.size();

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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.account.dto.v1_0.
						AccountMember.class)) {

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
		AccountMember accountMember1, AccountMember accountMember2) {

		if (accountMember1 == accountMember2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountMember1.getAccountId(),
						accountMember2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountRoles", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountMember1.getAccountRoles(),
						accountMember2.getAccountRoles())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("email", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountMember1.getEmail(), accountMember2.getEmail())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountMember1.getExternalReferenceCode(),
						accountMember2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountMember1.getName(), accountMember2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"userExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountMember1.getUserExternalReferenceCode(),
						accountMember2.getUserExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("userId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountMember1.getUserId(),
						accountMember2.getUserId())) {

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

		if (!(_accountMemberResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountMemberResource;

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
		EntityField entityField, String operator, AccountMember accountMember) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("accountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountRoles")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("email")) {
			Object object = accountMember.getEmail();

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
			Object object = accountMember.getExternalReferenceCode();

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
			Object object = accountMember.getName();

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

		if (entityFieldName.equals("userExternalReferenceCode")) {
			Object object = accountMember.getUserExternalReferenceCode();

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

		if (entityFieldName.equals("userId")) {
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

	protected AccountMember randomAccountMember() throws Exception {
		return new AccountMember() {
			{
				accountId = RandomTestUtil.randomLong();
				email =
					StringUtil.toLowerCase(RandomTestUtil.randomString()) +
						"@liferay.com";
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				userExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				userId = RandomTestUtil.randomLong();
			}
		};
	}

	protected AccountMember randomIrrelevantAccountMember() throws Exception {
		AccountMember randomIrrelevantAccountMember = randomAccountMember();

		return randomIrrelevantAccountMember;
	}

	protected AccountMember randomPatchAccountMember() throws Exception {
		return randomAccountMember();
	}

	protected AccountMemberResource accountMemberResource;
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
		LogFactoryUtil.getLog(BaseAccountMemberResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.account.resource.v1_0.
		AccountMemberResource _accountMemberResource;

}