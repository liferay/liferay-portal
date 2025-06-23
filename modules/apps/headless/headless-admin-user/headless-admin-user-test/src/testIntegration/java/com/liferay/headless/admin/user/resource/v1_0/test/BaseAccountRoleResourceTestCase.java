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

import com.liferay.headless.admin.user.client.dto.v1_0.AccountRole;
import com.liferay.headless.admin.user.client.http.HttpInvoker;
import com.liferay.headless.admin.user.client.pagination.Page;
import com.liferay.headless.admin.user.client.pagination.Pagination;
import com.liferay.headless.admin.user.client.resource.v1_0.AccountRoleResource;
import com.liferay.headless.admin.user.client.serdes.v1_0.AccountRoleSerDes;
import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Javier Gamarra
 * @generated
 */
@Generated("")
public abstract class BaseAccountRoleResourceTestCase {

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

		_accountRoleResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		accountRoleResource = AccountRoleResource.builder(
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

		AccountRole accountRole1 = randomAccountRole();

		String json = objectMapper.writeValueAsString(accountRole1);

		AccountRole accountRole2 = AccountRoleSerDes.toDTO(json);

		Assert.assertTrue(equals(accountRole1, accountRole2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		AccountRole accountRole = randomAccountRole();

		String json1 = objectMapper.writeValueAsString(accountRole);
		String json2 = AccountRoleSerDes.toJSON(accountRole);

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

		AccountRole accountRole = randomAccountRole();

		accountRole.setDescription(regex);
		accountRole.setDisplayName(regex);
		accountRole.setExternalReferenceCode(regex);
		accountRole.setName(regex);

		String json = AccountRoleSerDes.toJSON(accountRole);

		Assert.assertFalse(json.contains(regex));

		accountRole = AccountRoleSerDes.toDTO(json);

		Assert.assertEquals(regex, accountRole.getDescription());
		Assert.assertEquals(regex, accountRole.getDisplayName());
		Assert.assertEquals(regex, accountRole.getExternalReferenceCode());
		Assert.assertEquals(regex, accountRole.getName());
	}

	@Test
	public void testDeleteAccountAccountRoleUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testDeleteAccountAccountRoleUserAccountAssociation_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountAccountRoleUserAccountAssociationHttpResponse(
					testDeleteAccountAccountRoleUserAccountAssociation_getAccountId(
						accountRole),
					accountRole.getId(),
					testDeleteAccountAccountRoleUserAccountAssociation_getUserAccountId()));
	}

	protected AccountRole
			testDeleteAccountAccountRoleUserAccountAssociation_addAccountRole()
		throws Exception {

		return testPostAccountAccountRole_addAccountRole(randomAccountRole());
	}

	protected Long
			testDeleteAccountAccountRoleUserAccountAssociation_getAccountId(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testDeleteAccountAccountRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddressHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					accountRole.getExternalReferenceCode(),
					testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()));
	}

	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					accountRole.getExternalReferenceCode(),
					accountRole.getExternalReferenceCode()));
	}

	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					accountRole.getId(),
					testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getEmailAddress()));
	}

	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getExternalReferenceCode(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				deleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
					testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					accountRole.getId(),
					accountRole.getExternalReferenceCode()));
	}

	protected AccountRole
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getIrrelevantExternalReferenceCode();

		Page<AccountRole> page =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			AccountRole irrelevantAccountRole =
				testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountRole());

			page =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountRole, (List<AccountRole>)page.getItems());
			assertValid(
				page,
				testGetAccountAccountRolesByExternalReferenceCodePage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		AccountRole accountRole1 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		page =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountRole1, (List<AccountRole>)page.getItems());
		assertContains(accountRole2, (List<AccountRole>)page.getItems());
		assertValid(
			page,
			testGetAccountAccountRolesByExternalReferenceCodePage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAccountAccountRolesByExternalReferenceCodePage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		AccountRole accountRole1 = randomAccountRole();

		accountRole1 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, accountRole1);

		for (EntityField entityField : entityFields) {
			Page<AccountRole> page =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null,
						getFilterString(entityField, "between", accountRole1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountRole1),
				(List<AccountRole>)page.getItems());
		}
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithFilterStringContains()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithFilterStringEquals()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetAccountAccountRolesByExternalReferenceCodePageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		AccountRole accountRole1 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole2 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		for (EntityField entityField : entityFields) {
			Page<AccountRole> page =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null,
						getFilterString(entityField, operator, accountRole1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountRole1),
				(List<AccountRole>)page.getItems());
		}
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		Page<AccountRole> accountRolesPage =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			accountRolesPage.getTotalCount());

		AccountRole accountRole1 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		AccountRole accountRole3 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, randomAccountRole());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountRole> page1 =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(accountRole1, (List<AccountRole>)page1.getItems());

			Page<AccountRole> page2 =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(accountRole2, (List<AccountRole>)page2.getItems());

			Page<AccountRole> page3 =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(accountRole3, (List<AccountRole>)page3.getItems());
		}
		else {
			Page<AccountRole> page1 =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<AccountRole> accountRoles1 =
				(List<AccountRole>)page1.getItems();

			Assert.assertEquals(
				accountRoles1.toString(), totalCount + 2, accountRoles1.size());

			Page<AccountRole> page2 =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountRole> accountRoles2 =
				(List<AccountRole>)page2.getItems();

			Assert.assertEquals(
				accountRoles2.toString(), 1, accountRoles2.size());

			Page<AccountRole> page3 =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(accountRole1, (List<AccountRole>)page3.getItems());
			assertContains(accountRole2, (List<AccountRole>)page3.getItems());
			assertContains(accountRole3, (List<AccountRole>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithSortDateTime()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, accountRole1, accountRole2) -> {
				BeanTestUtil.setProperty(
					accountRole1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithSortDouble()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, accountRole1, accountRole2) -> {
				BeanTestUtil.setProperty(
					accountRole1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					accountRole2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithSortInteger()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithSort(
			EntityField.Type.INTEGER,
			(entityField, accountRole1, accountRole2) -> {
				BeanTestUtil.setProperty(
					accountRole1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					accountRole2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountAccountRolesByExternalReferenceCodePageWithSortString()
		throws Exception {

		testGetAccountAccountRolesByExternalReferenceCodePageWithSort(
			EntityField.Type.STRING,
			(entityField, accountRole1, accountRole2) -> {
				Class<?> clazz = accountRole1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						accountRole1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						accountRole2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						accountRole1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						accountRole2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						accountRole1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						accountRole2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetAccountAccountRolesByExternalReferenceCodePageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, AccountRole, AccountRole, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		String externalReferenceCode =
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode();

		AccountRole accountRole1 = randomAccountRole();
		AccountRole accountRole2 = randomAccountRole();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, accountRole1, accountRole2);
		}

		accountRole1 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, accountRole1);

		accountRole2 =
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				externalReferenceCode, accountRole2);

		Page<AccountRole> page =
			accountRoleResource.
				getAccountAccountRolesByExternalReferenceCodePage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AccountRole> ascPage =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(accountRole1, (List<AccountRole>)ascPage.getItems());
			assertContains(accountRole2, (List<AccountRole>)ascPage.getItems());

			Page<AccountRole> descPage =
				accountRoleResource.
					getAccountAccountRolesByExternalReferenceCodePage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				accountRole2, (List<AccountRole>)descPage.getItems());
			assertContains(
				accountRole1, (List<AccountRole>)descPage.getItems());
		}
	}

	protected AccountRole
			testGetAccountAccountRolesByExternalReferenceCodePage_addAccountRole(
				String externalReferenceCode, AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountAccountRolesByExternalReferenceCodePage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountAccountRolesByExternalReferenceCodePage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountAccountRolesPage() throws Exception {
		Long accountId = testGetAccountAccountRolesPage_getAccountId();
		Long irrelevantAccountId =
			testGetAccountAccountRolesPage_getIrrelevantAccountId();

		Page<AccountRole> page = accountRoleResource.getAccountAccountRolesPage(
			accountId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAccountId != null) {
			AccountRole irrelevantAccountRole =
				testGetAccountAccountRolesPage_addAccountRole(
					irrelevantAccountId, randomIrrelevantAccountRole());

			page = accountRoleResource.getAccountAccountRolesPage(
				irrelevantAccountId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountRole, (List<AccountRole>)page.getItems());
			assertValid(
				page,
				testGetAccountAccountRolesPage_getExpectedActions(
					irrelevantAccountId));
		}

		AccountRole accountRole1 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		page = accountRoleResource.getAccountAccountRolesPage(
			accountId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountRole1, (List<AccountRole>)page.getItems());
		assertContains(accountRole2, (List<AccountRole>)page.getItems());
		assertValid(
			page, testGetAccountAccountRolesPage_getExpectedActions(accountId));
	}

	protected Map<String, Map<String, String>>
			testGetAccountAccountRolesPage_getExpectedActions(Long accountId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-admin-user/v1.0/accounts/{accountId}/account-roles/batch".
				replace("{accountId}", String.valueOf(accountId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetAccountAccountRolesPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		AccountRole accountRole1 = randomAccountRole();

		accountRole1 = testGetAccountAccountRolesPage_addAccountRole(
			accountId, accountRole1);

		for (EntityField entityField : entityFields) {
			Page<AccountRole> page =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null,
					getFilterString(entityField, "between", accountRole1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountRole1),
				(List<AccountRole>)page.getItems());
		}
	}

	@Test
	public void testGetAccountAccountRolesPageWithFilterDoubleEquals()
		throws Exception {

		testGetAccountAccountRolesPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetAccountAccountRolesPageWithFilterStringContains()
		throws Exception {

		testGetAccountAccountRolesPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountAccountRolesPageWithFilterStringEquals()
		throws Exception {

		testGetAccountAccountRolesPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetAccountAccountRolesPageWithFilterStringStartsWith()
		throws Exception {

		testGetAccountAccountRolesPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetAccountAccountRolesPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		AccountRole accountRole1 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole2 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		for (EntityField entityField : entityFields) {
			Page<AccountRole> page =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null,
					getFilterString(entityField, operator, accountRole1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(accountRole1),
				(List<AccountRole>)page.getItems());
		}
	}

	@Test
	public void testGetAccountAccountRolesPageWithPagination()
		throws Exception {

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		Page<AccountRole> accountRolesPage =
			accountRoleResource.getAccountAccountRolesPage(
				accountId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			accountRolesPage.getTotalCount());

		AccountRole accountRole1 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		AccountRole accountRole3 =
			testGetAccountAccountRolesPage_addAccountRole(
				accountId, randomAccountRole());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<AccountRole> page1 =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(accountRole1, (List<AccountRole>)page1.getItems());

			Page<AccountRole> page2 =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(accountRole2, (List<AccountRole>)page2.getItems());

			Page<AccountRole> page3 =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(accountRole3, (List<AccountRole>)page3.getItems());
		}
		else {
			Page<AccountRole> page1 =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null, Pagination.of(1, totalCount + 2),
					null);

			List<AccountRole> accountRoles1 =
				(List<AccountRole>)page1.getItems();

			Assert.assertEquals(
				accountRoles1.toString(), totalCount + 2, accountRoles1.size());

			Page<AccountRole> page2 =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null, Pagination.of(2, totalCount + 2),
					null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<AccountRole> accountRoles2 =
				(List<AccountRole>)page2.getItems();

			Assert.assertEquals(
				accountRoles2.toString(), 1, accountRoles2.size());

			Page<AccountRole> page3 =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(accountRole1, (List<AccountRole>)page3.getItems());
			assertContains(accountRole2, (List<AccountRole>)page3.getItems());
			assertContains(accountRole3, (List<AccountRole>)page3.getItems());
		}
	}

	@Test
	public void testGetAccountAccountRolesPageWithSortDateTime()
		throws Exception {

		testGetAccountAccountRolesPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, accountRole1, accountRole2) -> {
				BeanTestUtil.setProperty(
					accountRole1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAccountAccountRolesPageWithSortDouble()
		throws Exception {

		testGetAccountAccountRolesPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, accountRole1, accountRole2) -> {
				BeanTestUtil.setProperty(
					accountRole1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					accountRole2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAccountAccountRolesPageWithSortInteger()
		throws Exception {

		testGetAccountAccountRolesPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, accountRole1, accountRole2) -> {
				BeanTestUtil.setProperty(
					accountRole1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					accountRole2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAccountAccountRolesPageWithSortString()
		throws Exception {

		testGetAccountAccountRolesPageWithSort(
			EntityField.Type.STRING,
			(entityField, accountRole1, accountRole2) -> {
				Class<?> clazz = accountRole1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						accountRole1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						accountRole2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						accountRole1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						accountRole2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						accountRole1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						accountRole2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetAccountAccountRolesPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, AccountRole, AccountRole, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long accountId = testGetAccountAccountRolesPage_getAccountId();

		AccountRole accountRole1 = randomAccountRole();
		AccountRole accountRole2 = randomAccountRole();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, accountRole1, accountRole2);
		}

		accountRole1 = testGetAccountAccountRolesPage_addAccountRole(
			accountId, accountRole1);

		accountRole2 = testGetAccountAccountRolesPage_addAccountRole(
			accountId, accountRole2);

		Page<AccountRole> page = accountRoleResource.getAccountAccountRolesPage(
			accountId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<AccountRole> ascPage =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(accountRole1, (List<AccountRole>)ascPage.getItems());
			assertContains(accountRole2, (List<AccountRole>)ascPage.getItems());

			Page<AccountRole> descPage =
				accountRoleResource.getAccountAccountRolesPage(
					accountId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				accountRole2, (List<AccountRole>)descPage.getItems());
			assertContains(
				accountRole1, (List<AccountRole>)descPage.getItems());
		}
	}

	protected AccountRole testGetAccountAccountRolesPage_addAccountRole(
			Long accountId, AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountAccountRole(
			accountId, accountRole);
	}

	protected Long testGetAccountAccountRolesPage_getAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAccountAccountRolesPage_getIrrelevantAccountId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage()
		throws Exception {

		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getIrrelevantExternalReferenceCode();
		String emailAddress =
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getEmailAddress();
		String irrelevantEmailAddress =
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getIrrelevantEmailAddress();

		Page<AccountRole> page =
			accountRoleResource.
				getAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage(
					externalReferenceCode, emailAddress);

		long totalCount = page.getTotalCount();

		if ((irrelevantExternalReferenceCode != null) &&
			(irrelevantEmailAddress != null)) {

			AccountRole irrelevantAccountRole =
				testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_addAccountRole(
					irrelevantExternalReferenceCode, irrelevantEmailAddress,
					randomIrrelevantAccountRole());

			page =
				accountRoleResource.
					getAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage(
						irrelevantExternalReferenceCode,
						irrelevantEmailAddress);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountRole, (List<AccountRole>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getExpectedActions(
					irrelevantExternalReferenceCode, irrelevantEmailAddress));
		}

		AccountRole accountRole1 =
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_addAccountRole(
				externalReferenceCode, emailAddress, randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_addAccountRole(
				externalReferenceCode, emailAddress, randomAccountRole());

		page =
			accountRoleResource.
				getAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage(
					externalReferenceCode, emailAddress);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountRole1, (List<AccountRole>)page.getItems());
		assertContains(accountRole2, (List<AccountRole>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getExpectedActions(
				externalReferenceCode, emailAddress));
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getExpectedActions(
				String externalReferenceCode, String emailAddress)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected AccountRole
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_addAccountRole(
				String externalReferenceCode, String emailAddress,
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByEmailAddressAccountRolesPage_getIrrelevantEmailAddress()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage()
		throws Exception {

		String accountExternalReferenceCode =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getAccountExternalReferenceCode();
		String irrelevantAccountExternalReferenceCode =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getIrrelevantAccountExternalReferenceCode();
		String externalReferenceCode =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getIrrelevantExternalReferenceCode();

		Page<AccountRole> page =
			accountRoleResource.
				getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage(
					accountExternalReferenceCode, externalReferenceCode);

		long totalCount = page.getTotalCount();

		if ((irrelevantAccountExternalReferenceCode != null) &&
			(irrelevantExternalReferenceCode != null)) {

			AccountRole irrelevantAccountRole =
				testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_addAccountRole(
					irrelevantAccountExternalReferenceCode,
					irrelevantExternalReferenceCode,
					randomIrrelevantAccountRole());

			page =
				accountRoleResource.
					getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage(
						irrelevantAccountExternalReferenceCode,
						irrelevantExternalReferenceCode);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantAccountRole, (List<AccountRole>)page.getItems());
			assertValid(
				page,
				testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getExpectedActions(
					irrelevantAccountExternalReferenceCode,
					irrelevantExternalReferenceCode));
		}

		AccountRole accountRole1 =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_addAccountRole(
				accountExternalReferenceCode, externalReferenceCode,
				randomAccountRole());

		AccountRole accountRole2 =
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_addAccountRole(
				accountExternalReferenceCode, externalReferenceCode,
				randomAccountRole());

		page =
			accountRoleResource.
				getAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage(
					accountExternalReferenceCode, externalReferenceCode);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(accountRole1, (List<AccountRole>)page.getItems());
		assertContains(accountRole2, (List<AccountRole>)page.getItems());
		assertValid(
			page,
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getExpectedActions(
				accountExternalReferenceCode, externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getExpectedActions(
				String accountExternalReferenceCode,
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	protected AccountRole
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_addAccountRole(
				String accountExternalReferenceCode,
				String externalReferenceCode, AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getIrrelevantAccountExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAccountByExternalReferenceCodeUserAccountByExternalReferenceCodeAccountRolesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testPostAccountAccountRole() throws Exception {
		AccountRole randomAccountRole = randomAccountRole();

		AccountRole postAccountRole = testPostAccountAccountRole_addAccountRole(
			randomAccountRole);

		assertEquals(randomAccountRole, postAccountRole);
		assertValid(postAccountRole);
	}

	protected AccountRole testPostAccountAccountRole_addAccountRole(
			AccountRole accountRole)
		throws Exception {

		return accountRoleResource.postAccountAccountRole(
			testGetAccountAccountRolesPage_getAccountId(), accountRole);
	}

	@Test
	public void testPostAccountAccountRoleByExternalReferenceCode()
		throws Exception {

		AccountRole randomAccountRole = randomAccountRole();

		AccountRole postAccountRole =
			testPostAccountAccountRoleByExternalReferenceCode_addAccountRole(
				randomAccountRole);

		assertEquals(randomAccountRole, postAccountRole);
		assertValid(postAccountRole);
	}

	protected AccountRole
			testPostAccountAccountRoleByExternalReferenceCode_addAccountRole(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountAccountRoleUserAccountAssociation()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testPostAccountAccountRoleUserAccountAssociation_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountAccountRoleUserAccountAssociationHttpResponse(
					testPostAccountAccountRoleUserAccountAssociation_getAccountId(
						accountRole),
					accountRole.getId(),
					testPostAccountAccountRoleUserAccountAssociation_getUserAccountId()));

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountAccountRoleUserAccountAssociationHttpResponse(
					testPostAccountAccountRoleUserAccountAssociation_getAccountId(
						accountRole),
					0L,
					testPostAccountAccountRoleUserAccountAssociation_getUserAccountId()));
	}

	protected Long
			testPostAccountAccountRoleUserAccountAssociation_getAccountId(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testPostAccountAccountRoleUserAccountAssociation_getUserAccountId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountRole
			testPostAccountAccountRoleUserAccountAssociation_addAccountRole()
		throws Exception {

		return testPostAccountAccountRole_addAccountRole(randomAccountRole());
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddressHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					accountRole.getExternalReferenceCode(),
					testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()));

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddressHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					"-",
					testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()));
	}

	protected String
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getExternalReferenceCode(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountRole
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					accountRole.getExternalReferenceCode(),
					accountRole.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCodeHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					"-", "-"));
	}

	protected String
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountRole
			testPostAccountByExternalReferenceCodeAccountRoleByExternalReferenceCodeUserAccountByExternalReferenceCode_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					accountRole.getId(),
					testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getEmailAddress()));

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddressHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getExternalReferenceCode(
						accountRole),
					0L,
					testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getEmailAddress()));
	}

	protected String
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getExternalReferenceCode(
				AccountRole accountRole)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_getEmailAddress()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountRole
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByEmailAddress_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		AccountRole accountRole =
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_addAccountRole();

		assertHttpResponseStatusCode(
			204,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					accountRole.getId(),
					accountRole.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			accountRoleResource.
				postAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCodeHttpResponse(
					testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_getAccountExternalReferenceCode(),
					0L, "-"));
	}

	protected String
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_getAccountExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected AccountRole
			testPostAccountByExternalReferenceCodeAccountRoleUserAccountByExternalReferenceCode_addAccountRole()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected void assertContains(
		AccountRole accountRole, List<AccountRole> accountRoles) {

		boolean contains = false;

		for (AccountRole item : accountRoles) {
			if (equals(accountRole, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			accountRoles + " does not contain " + accountRole, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		AccountRole accountRole1, AccountRole accountRole2) {

		Assert.assertTrue(
			accountRole1 + " does not equal " + accountRole2,
			equals(accountRole1, accountRole2));
	}

	protected void assertEquals(
		List<AccountRole> accountRoles1, List<AccountRole> accountRoles2) {

		Assert.assertEquals(accountRoles1.size(), accountRoles2.size());

		for (int i = 0; i < accountRoles1.size(); i++) {
			AccountRole accountRole1 = accountRoles1.get(i);
			AccountRole accountRole2 = accountRoles2.get(i);

			assertEquals(accountRole1, accountRole2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<AccountRole> accountRoles1, List<AccountRole> accountRoles2) {

		Assert.assertEquals(accountRoles1.size(), accountRoles2.size());

		for (AccountRole accountRole1 : accountRoles1) {
			boolean contains = false;

			for (AccountRole accountRole2 : accountRoles2) {
				if (equals(accountRole1, accountRole2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				accountRoles2 + " does not contain " + accountRole1, contains);
		}
	}

	protected void assertValid(AccountRole accountRole) throws Exception {
		boolean valid = true;

		if (accountRole.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (accountRole.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (accountRole.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayName", additionalAssertFieldName)) {
				if (accountRole.getDisplayName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (accountRole.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (accountRole.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roleId", additionalAssertFieldName)) {
				if (accountRole.getRoleId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roleType", additionalAssertFieldName)) {
				if (accountRole.getRoleType() == null) {
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

	protected void assertValid(Page<AccountRole> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<AccountRole> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<AccountRole> accountRoles = page.getItems();

		int size = accountRoles.size();

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
					com.liferay.headless.admin.user.dto.v1_0.AccountRole.
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
		AccountRole accountRole1, AccountRole accountRole2) {

		if (accountRole1 == accountRole2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getAccountId(),
						accountRole2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getDescription(),
						accountRole2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayName", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getDisplayName(),
						accountRole2.getDisplayName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						accountRole1.getExternalReferenceCode(),
						accountRole2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getId(), accountRole2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getName(), accountRole2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roleId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getRoleId(), accountRole2.getRoleId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roleType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						accountRole1.getRoleType(),
						accountRole2.getRoleType())) {

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

		if (!(_accountRoleResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_accountRoleResource;

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
		EntityField entityField, String operator, AccountRole accountRole) {

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

		if (entityFieldName.equals("description")) {
			Object object = accountRole.getDescription();

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

		if (entityFieldName.equals("displayName")) {
			Object object = accountRole.getDisplayName();

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
			Object object = accountRole.getExternalReferenceCode();

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
			Object object = accountRole.getName();

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

		if (entityFieldName.equals("roleId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("roleType")) {
			sb.append(String.valueOf(accountRole.getRoleType()));

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

	protected AccountRole randomAccountRole() throws Exception {
		return new AccountRole() {
			{
				accountId = RandomTestUtil.randomLong();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				displayName = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				roleId = RandomTestUtil.randomLong();
				roleType = RandomTestUtil.randomInt();
			}
		};
	}

	protected AccountRole randomIrrelevantAccountRole() throws Exception {
		AccountRole randomIrrelevantAccountRole = randomAccountRole();

		return randomIrrelevantAccountRole;
	}

	protected AccountRole randomPatchAccountRole() throws Exception {
		return randomAccountRole();
	}

	protected AccountRoleResource accountRoleResource;
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
		LogFactoryUtil.getLog(BaseAccountRoleResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.admin.user.resource.v1_0.AccountRoleResource
		_accountRoleResource;

}