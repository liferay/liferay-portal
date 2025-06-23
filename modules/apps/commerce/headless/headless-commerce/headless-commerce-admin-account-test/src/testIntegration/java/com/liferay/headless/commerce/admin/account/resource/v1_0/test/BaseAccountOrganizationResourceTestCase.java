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

import com.liferay.headless.commerce.admin.account.client.dto.v1_0.AccountOrganization;
import com.liferay.headless.commerce.admin.account.client.dto.v1_0.User;
import com.liferay.headless.commerce.admin.account.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.account.client.pagination.Page;
import com.liferay.headless.commerce.admin.account.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.account.client.resource.v1_0.AccountOrganizationResource;
import com.liferay.headless.commerce.admin.account.client.serdes.v1_0.AccountOrganizationSerDes;
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
public abstract class BaseAccountOrganizationResourceTestCase {

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

		_accountOrganizationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountOrganizationResource = AccountOrganizationResource.builder(
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

		AccountOrganization accountOrganization1 = randomAccountOrganization();

		String json = objectMapper.writeValueAsString(accountOrganization1);

		AccountOrganization accountOrganization2 =
			AccountOrganizationSerDes.toDTO(json);

		Assert.assertTrue(equals(accountOrganization1, accountOrganization2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountOrganization accountOrganization = randomAccountOrganization();

		String json1 = objectMapper.writeValueAsString(accountOrganization);
		String json2 = AccountOrganizationSerDes.toJSON(accountOrganization);

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

		AccountOrganization accountOrganization = randomAccountOrganization();

		accountOrganization.setName(regex);
		accountOrganization.setOrganizationExternalReferenceCode(regex);
		accountOrganization.setTreePath(regex);

		String json = AccountOrganizationSerDes.toJSON(accountOrganization);

		Assert.assertFalse(json.contains(regex));

		accountOrganization = AccountOrganizationSerDes.toDTO(json);

		Assert.assertEquals(regex, accountOrganization.getName());
		Assert.assertEquals(
			regex, accountOrganization.getOrganizationExternalReferenceCode());
		Assert.assertEquals(regex, accountOrganization.getTreePath());
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountOrganization()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testDeleteAccountIdAccountOrganization() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountOrganization()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeAccountOrganization()
		throws Exception {

		Assert.assertTrue(true);
	}

	@Test
	public void testGraphQLGetAccountByExternalReferenceCodeAccountOrganizationNotFound()
		throws Exception {

		Assert.assertTrue(true);
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountOrganizationsPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getIrrelevantExternalReferenceCode();

		Page<AccountOrganization> page =
			accountOrganizationResource.
				getAccountByExternalReferenceCodeAccountOrganizationsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			AccountOrganization irrelevantAccountOrganization =
				testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountOrganization());

			page =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountOrganization,
				(List<AccountOrganization>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		AccountOrganization accountOrganization1 =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
				externalReferenceCode, randomAccountOrganization());

		AccountOrganization accountOrganization2 =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
				externalReferenceCode, randomAccountOrganization());

		page =
			accountOrganizationResource.
				getAccountByExternalReferenceCodeAccountOrganizationsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountOrganization1, (List<AccountOrganization>)page.getItems());
		assertContains(
			accountOrganization2, (List<AccountOrganization>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeAccountOrganizationsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getExternalReferenceCode();

		Page<AccountOrganization> accountOrganizationsPage =
			accountOrganizationResource.
				getAccountByExternalReferenceCodeAccountOrganizationsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			accountOrganizationsPage.getTotalCount());

		AccountOrganization accountOrganization1 =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
				externalReferenceCode, randomAccountOrganization());

		AccountOrganization accountOrganization2 =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
				externalReferenceCode, randomAccountOrganization());

		AccountOrganization accountOrganization3 =
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
				externalReferenceCode, randomAccountOrganization());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountOrganization> page1 =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountOrganization1,
				(List<AccountOrganization>)page1.getItems());

			Page<AccountOrganization> page2 =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountOrganization2,
				(List<AccountOrganization>)page2.getItems());

			Page<AccountOrganization> page3 =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountOrganization3,
				(List<AccountOrganization>)page3.getItems());
		}
		else {
			Page<AccountOrganization> page1 =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<AccountOrganization> accountOrganizations1 =
				(List<AccountOrganization>)page1.getItems();

			Assert.assertEquals(
				accountOrganizations1.toString(), totalCount + 2,
				accountOrganizations1.size());

			Page<AccountOrganization> page2 =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountOrganization> accountOrganizations2 =
				(List<AccountOrganization>)page2.getItems();

			Assert.assertEquals(
				accountOrganizations2.toString(), 1,
				accountOrganizations2.size());

			Page<AccountOrganization> page3 =
				accountOrganizationResource.
					getAccountByExternalReferenceCodeAccountOrganizationsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountOrganization1,
				(List<AccountOrganization>)page3.getItems());
			assertContains(
				accountOrganization2,
				(List<AccountOrganization>)page3.getItems());
			assertContains(
				accountOrganization3,
				(List<AccountOrganization>)page3.getItems());
		}
	}

	protected AccountOrganization
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_addAccountOrganization(
				String externalReferenceCode,
				AccountOrganization accountOrganization)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeAccountOrganizationsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountIdAccountOrganization() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testGraphQLGetAccountIdAccountOrganization() throws Exception {
		Assert.assertTrue(true);
	}

	@Test
	public void testGraphQLGetAccountIdAccountOrganizationNotFound()
		throws Exception {

		Assert.assertTrue(true);
	}

	@Test
	public void testGetAccountIdAccountOrganizationsPage() throws Exception {
		Long id = testGetAccountIdAccountOrganizationsPage_getId();
		Long irrelevantId =
			testGetAccountIdAccountOrganizationsPage_getIrrelevantId();

		Page<AccountOrganization> page =
			accountOrganizationResource.getAccountIdAccountOrganizationsPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			AccountOrganization irrelevantAccountOrganization =
				testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
					irrelevantId, randomIrrelevantAccountOrganization());

			page =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountOrganization,
				(List<AccountOrganization>)page.getItems());
			assertValid(
				page,
				testGetAccountIdAccountOrganizationsPage_getExpectedActions(
					irrelevantId));
		}

		AccountOrganization accountOrganization1 =
			testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
				id, randomAccountOrganization());

		AccountOrganization accountOrganization2 =
			testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
				id, randomAccountOrganization());

		page = accountOrganizationResource.getAccountIdAccountOrganizationsPage(
			id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			accountOrganization1, (List<AccountOrganization>)page.getItems());
		assertContains(
			accountOrganization2, (List<AccountOrganization>)page.getItems());
		assertValid(
			page,
			testGetAccountIdAccountOrganizationsPage_getExpectedActions(id));
	}

	protected Map<String, Map<String, String>>
			testGetAccountIdAccountOrganizationsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountIdAccountOrganizationsPageWithPagination()
		throws Exception {

		Long id = testGetAccountIdAccountOrganizationsPage_getId();

		Page<AccountOrganization> accountOrganizationsPage =
			accountOrganizationResource.getAccountIdAccountOrganizationsPage(
				id, null);

		int totalCount = GetterUtil.getInteger(
			accountOrganizationsPage.getTotalCount());

		AccountOrganization accountOrganization1 =
			testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
				id, randomAccountOrganization());

		AccountOrganization accountOrganization2 =
			testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
				id, randomAccountOrganization());

		AccountOrganization accountOrganization3 =
			testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
				id, randomAccountOrganization());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountOrganization> page1 =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				accountOrganization1,
				(List<AccountOrganization>)page1.getItems());

			Page<AccountOrganization> page2 =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountOrganization2,
				(List<AccountOrganization>)page2.getItems());

			Page<AccountOrganization> page3 =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				accountOrganization3,
				(List<AccountOrganization>)page3.getItems());
		}
		else {
			Page<AccountOrganization> page1 =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id, Pagination.of(1, totalCount + 2));

			List<AccountOrganization> accountOrganizations1 =
				(List<AccountOrganization>)page1.getItems();

			Assert.assertEquals(
				accountOrganizations1.toString(), totalCount + 2,
				accountOrganizations1.size());

			Page<AccountOrganization> page2 =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountOrganization> accountOrganizations2 =
				(List<AccountOrganization>)page2.getItems();

			Assert.assertEquals(
				accountOrganizations2.toString(), 1,
				accountOrganizations2.size());

			Page<AccountOrganization> page3 =
				accountOrganizationResource.
					getAccountIdAccountOrganizationsPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				accountOrganization1,
				(List<AccountOrganization>)page3.getItems());
			assertContains(
				accountOrganization2,
				(List<AccountOrganization>)page3.getItems());
			assertContains(
				accountOrganization3,
				(List<AccountOrganization>)page3.getItems());
		}
	}

	protected AccountOrganization
			testGetAccountIdAccountOrganizationsPage_addAccountOrganization(
				Long id, AccountOrganization accountOrganization)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountOrganizationsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountIdAccountOrganizationsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountOrganization()
		throws Exception {

		AccountOrganization randomAccountOrganization =
			randomAccountOrganization();

		AccountOrganization postAccountOrganization =
			testPostAccountByExternalReferenceCodeAccountOrganization_addAccountOrganization(
				randomAccountOrganization);

		assertEquals(randomAccountOrganization, postAccountOrganization);
		assertValid(postAccountOrganization);
	}

	protected AccountOrganization
			testPostAccountByExternalReferenceCodeAccountOrganization_addAccountOrganization(
				AccountOrganization accountOrganization)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountIdAccountOrganization() throws Exception {
		AccountOrganization randomAccountOrganization =
			randomAccountOrganization();

		AccountOrganization postAccountOrganization =
			testPostAccountIdAccountOrganization_addAccountOrganization(
				randomAccountOrganization);

		assertEquals(randomAccountOrganization, postAccountOrganization);
		assertValid(postAccountOrganization);
	}

	protected AccountOrganization
			testPostAccountIdAccountOrganization_addAccountOrganization(
				AccountOrganization accountOrganization)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		AccountOrganization accountOrganization,
		List<AccountOrganization> accountOrganizations) {

		boolean contains = false;

		for (AccountOrganization item : accountOrganizations) {
			if (equals(accountOrganization, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountOrganizations + " does not contain " + accountOrganization,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountOrganization accountOrganization1,
		AccountOrganization accountOrganization2) {

		Assert.assertTrue(
			accountOrganization1 + " does not equal " + accountOrganization2,
			equals(accountOrganization1, accountOrganization2));
	}

	protected void assertEquals(
		List<AccountOrganization> accountOrganizations1,
		List<AccountOrganization> accountOrganizations2) {

		Assert.assertEquals(
			accountOrganizations1.size(), accountOrganizations2.size());

		for (int i = 0; i < accountOrganizations1.size(); i++) {
			AccountOrganization accountOrganization1 =
				accountOrganizations1.get(i);
			AccountOrganization accountOrganization2 =
				accountOrganizations2.get(i);

			assertEquals(accountOrganization1, accountOrganization2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountOrganization> accountOrganizations1,
		List<AccountOrganization> accountOrganizations2) {

		Assert.assertEquals(
			accountOrganizations1.size(), accountOrganizations2.size());

		for (AccountOrganization accountOrganization1 : accountOrganizations1) {
			boolean contains = false;

			for (AccountOrganization accountOrganization2 :
					accountOrganizations2) {

				if (equals(accountOrganization1, accountOrganization2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountOrganizations2 + " does not contain " +
					accountOrganization1,
				contains);
		}
	}

	protected void assertValid(AccountOrganization accountOrganization)
		throws Exception {

		boolean valid = true;

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (accountOrganization.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountOrganization.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"organizationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (accountOrganization.
						getOrganizationExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("organizationId", additionalAssertFieldName)) {
				if (accountOrganization.getOrganizationId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("treePath", additionalAssertFieldName)) {
				if (accountOrganization.getTreePath() == null) {
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

	protected void assertValid(Page<AccountOrganization> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountOrganization> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountOrganization> accountOrganizations =
			page.getItems();

		int size = accountOrganizations.size();

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
						AccountOrganization.class)) {

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
		AccountOrganization accountOrganization1,
		AccountOrganization accountOrganization2) {

		if (accountOrganization1 == accountOrganization2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountOrganization1.getAccountId(),
						accountOrganization2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountOrganization1.getName(),
						accountOrganization2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"organizationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountOrganization1.
							getOrganizationExternalReferenceCode(),
						accountOrganization2.
							getOrganizationExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("organizationId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountOrganization1.getOrganizationId(),
						accountOrganization2.getOrganizationId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("treePath", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountOrganization1.getTreePath(),
						accountOrganization2.getTreePath())) {

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

		if (!(_accountOrganizationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountOrganizationResource;

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
		AccountOrganization accountOrganization) {

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

		if (entityFieldName.equals("name")) {
			Object object = accountOrganization.getName();

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

		if (entityFieldName.equals("organizationExternalReferenceCode")) {
			Object object =
				accountOrganization.getOrganizationExternalReferenceCode();

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

		if (entityFieldName.equals("organizationId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("treePath")) {
			Object object = accountOrganization.getTreePath();

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

	protected AccountOrganization randomAccountOrganization() throws Exception {
		return new AccountOrganization() {
			{
				accountId = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				organizationExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				organizationId = RandomTestUtil.randomLong();
				treePath = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected AccountOrganization randomIrrelevantAccountOrganization()
		throws Exception {

		AccountOrganization randomIrrelevantAccountOrganization =
			randomAccountOrganization();

		return randomIrrelevantAccountOrganization;
	}

	protected AccountOrganization randomPatchAccountOrganization()
		throws Exception {

		return randomAccountOrganization();
	}

	protected AccountOrganizationResource accountOrganizationResource;
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
		LogFactoryUtil.getLog(BaseAccountOrganizationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.account.resource.v1_0.
		AccountOrganizationResource _accountOrganizationResource;

}