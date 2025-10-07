/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.asset.library.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.depot.constants.DepotConstants;
import com.liferay.depot.model.DepotEntry;
import com.liferay.depot.service.DepotEntryLocalServiceUtil;
import com.liferay.headless.asset.library.client.dto.v1_0.UserAccount;
import com.liferay.headless.asset.library.client.http.HttpInvoker;
import com.liferay.headless.asset.library.client.pagination.Page;
import com.liferay.headless.asset.library.client.pagination.Pagination;
import com.liferay.headless.asset.library.client.resource.v1_0.UserAccountResource;
import com.liferay.headless.asset.library.client.serdes.v1_0.UserAccountSerDes;
import com.liferay.headless.batch.engine.client.dto.v1_0.ImportTask;
import com.liferay.headless.batch.engine.client.http.HttpInvoker.HttpResponse;
import com.liferay.headless.batch.engine.client.resource.v1_0.ImportTaskResource;
import com.liferay.petra.function.UnsafeTriConsumer;
import com.liferay.petra.function.transform.TransformUtil;
import com.liferay.petra.reflect.ReflectionUtil;
import com.liferay.petra.string.StringBundler;
import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONObject;
import com.liferay.portal.kernel.json.JSONUtil;
import com.liferay.portal.kernel.log.LogFactoryUtil;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.ArrayUtil;
import com.liferay.portal.kernel.util.FastDateFormatFactoryUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
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
 * @author Roberto Díaz
 * @generated
 */
@Generated("")
public abstract class BaseUserAccountResourceTestCase {

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

		irrelevantDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		irrelevantDepotEntryGroup = irrelevantDepotEntry.getGroup();
		testDepotEntry = DepotEntryLocalServiceUtil.addDepotEntry(
			Collections.singletonMap(
				LocaleUtil.getDefault(), RandomTestUtil.randomString()),
			null, DepotConstants.TYPE_ASSET_LIBRARY,
			new ServiceContext() {
				{
					setCompanyId(testCompany.getCompanyId());
					setUserId(TestPropsValues.getUserId());
				}
			});
		testDepotEntryGroup = testDepotEntry.getGroup();

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

		userAccount.setExternalReferenceCode(regex);
		userAccount.setImage(regex);
		userAccount.setName(regex);

		String json = UserAccountSerDes.toJSON(userAccount);

		Assert.assertFalse(json.contains(regex));

		userAccount = UserAccountSerDes.toDTO(json);

		Assert.assertEquals(regex, userAccount.getExternalReferenceCode());
		Assert.assertEquals(regex, userAccount.getImage());
		Assert.assertEquals(regex, userAccount.getName());
	}

	@Test
	public void testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.
				deleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					userAccount.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCodeHttpResponse(
					testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					"-"));
	}

	protected UserAccount
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testDeleteAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testDeleteAssetLibraryUserAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		UserAccount userAccount =
			testDeleteAssetLibraryUserAccount_addUserAccount();

		assertHttpResponseStatusCode(
			204,
			userAccountResource.deleteAssetLibraryUserAccountHttpResponse(
				testDeleteAssetLibraryUserAccount_getAssetLibraryId(),
				userAccount.getId()));

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getAssetLibraryUserAccountHttpResponse(
				testDeleteAssetLibraryUserAccount_getAssetLibraryId(),
				userAccount.getId()));
		assertHttpResponseStatusCode(
			404,
			userAccountResource.getAssetLibraryUserAccountHttpResponse(
				testDeleteAssetLibraryUserAccount_getAssetLibraryId(), 0L));
	}

	protected UserAccount testDeleteAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testDeleteAssetLibraryUserAccount_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode()
		throws Exception {

		UserAccount postUserAccount =
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_addUserAccount();

		UserAccount getUserAccount =
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode(
					testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					postUserAccount.getExternalReferenceCode());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getIrrelevantExternalReferenceCode();

		Page<UserAccount> page =
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			UserAccount irrelevantUserAccount =
				testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantUserAccount());

			page =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						irrelevantExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		UserAccount userAccount1 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		page =
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, Pagination.of(1, 10),
					null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExpectedActions(
				externalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();

		Page<UserAccount> userAccountsPage =
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount2 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		UserAccount userAccount3 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
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
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSortString()
		throws Exception {

		testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSort(
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
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPageWithSort(
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
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, userAccount1);

		userAccount2 =
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				externalReferenceCode, userAccount2);

		Page<UserAccount> page =
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeUserAccountsPage(
					externalReferenceCode, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
						externalReferenceCode, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.
					getAssetLibraryByExternalReferenceCodeUserAccountsPage(
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
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_addUserAccount(
				String externalReferenceCode, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetAssetLibraryByExternalReferenceCodeUserAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetAssetLibraryUserAccount() throws Exception {
		UserAccount postUserAccount =
			testGetAssetLibraryUserAccount_addUserAccount();

		UserAccount getUserAccount =
			userAccountResource.getAssetLibraryUserAccount(
				testGetAssetLibraryUserAccount_getAssetLibraryId(),
				postUserAccount.getId());

		assertEquals(postUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testGetAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryUserAccount_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testGetAssetLibraryUserAccountsPage() throws Exception {
		Long assetLibraryId =
			testGetAssetLibraryUserAccountsPage_getAssetLibraryId();
		Long irrelevantAssetLibraryId =
			testGetAssetLibraryUserAccountsPage_getIrrelevantAssetLibraryId();

		Page<UserAccount> page =
			userAccountResource.getAssetLibraryUserAccountsPage(
				assetLibraryId, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantAssetLibraryId != null) {
			UserAccount irrelevantUserAccount =
				testGetAssetLibraryUserAccountsPage_addUserAccount(
					irrelevantAssetLibraryId, randomIrrelevantUserAccount());

			page = userAccountResource.getAssetLibraryUserAccountsPage(
				irrelevantAssetLibraryId, null, null,
				Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantUserAccount, (List<UserAccount>)page.getItems());
			assertValid(
				page,
				testGetAssetLibraryUserAccountsPage_getExpectedActions(
					irrelevantAssetLibraryId));
		}

		UserAccount userAccount1 =
			testGetAssetLibraryUserAccountsPage_addUserAccount(
				assetLibraryId, randomUserAccount());

		UserAccount userAccount2 =
			testGetAssetLibraryUserAccountsPage_addUserAccount(
				assetLibraryId, randomUserAccount());

		page = userAccountResource.getAssetLibraryUserAccountsPage(
			assetLibraryId, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(userAccount1, (List<UserAccount>)page.getItems());
		assertContains(userAccount2, (List<UserAccount>)page.getItems());
		assertValid(
			page,
			testGetAssetLibraryUserAccountsPage_getExpectedActions(
				assetLibraryId));
	}

	protected Map<String, Map<String, String>>
			testGetAssetLibraryUserAccountsPage_getExpectedActions(
				Long assetLibraryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetAssetLibraryUserAccountsPageWithPagination()
		throws Exception {

		Long assetLibraryId =
			testGetAssetLibraryUserAccountsPage_getAssetLibraryId();

		Page<UserAccount> userAccountsPage =
			userAccountResource.getAssetLibraryUserAccountsPage(
				assetLibraryId, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			userAccountsPage.getTotalCount());

		UserAccount userAccount1 =
			testGetAssetLibraryUserAccountsPage_addUserAccount(
				assetLibraryId, randomUserAccount());

		UserAccount userAccount2 =
			testGetAssetLibraryUserAccountsPage_addUserAccount(
				assetLibraryId, randomUserAccount());

		UserAccount userAccount3 =
			testGetAssetLibraryUserAccountsPage_addUserAccount(
				assetLibraryId, randomUserAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<UserAccount> page1 =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(userAccount1, (List<UserAccount>)page1.getItems());

			Page<UserAccount> page2 =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount2, (List<UserAccount>)page2.getItems());

			Page<UserAccount> page3 =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
		else {
			Page<UserAccount> page1 =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(1, totalCount + 2), null);

			List<UserAccount> userAccounts1 =
				(List<UserAccount>)page1.getItems();

			Assert.assertEquals(
				userAccounts1.toString(), totalCount + 2, userAccounts1.size());

			Page<UserAccount> page2 =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<UserAccount> userAccounts2 =
				(List<UserAccount>)page2.getItems();

			Assert.assertEquals(
				userAccounts2.toString(), 1, userAccounts2.size());

			Page<UserAccount> page3 =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(1, (int)totalCount + 3), null);

			assertContains(userAccount1, (List<UserAccount>)page3.getItems());
			assertContains(userAccount2, (List<UserAccount>)page3.getItems());
			assertContains(userAccount3, (List<UserAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetAssetLibraryUserAccountsPageWithSortDateTime()
		throws Exception {

		testGetAssetLibraryUserAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetAssetLibraryUserAccountsPageWithSortDouble()
		throws Exception {

		testGetAssetLibraryUserAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetAssetLibraryUserAccountsPageWithSortInteger()
		throws Exception {

		testGetAssetLibraryUserAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, userAccount1, userAccount2) -> {
				BeanTestUtil.setProperty(
					userAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					userAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetAssetLibraryUserAccountsPageWithSortString()
		throws Exception {

		testGetAssetLibraryUserAccountsPageWithSort(
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

	protected void testGetAssetLibraryUserAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, UserAccount, UserAccount, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long assetLibraryId =
			testGetAssetLibraryUserAccountsPage_getAssetLibraryId();

		UserAccount userAccount1 = randomUserAccount();
		UserAccount userAccount2 = randomUserAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, userAccount1, userAccount2);
		}

		userAccount1 = testGetAssetLibraryUserAccountsPage_addUserAccount(
			assetLibraryId, userAccount1);

		userAccount2 = testGetAssetLibraryUserAccountsPage_addUserAccount(
			assetLibraryId, userAccount2);

		Page<UserAccount> page =
			userAccountResource.getAssetLibraryUserAccountsPage(
				assetLibraryId, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<UserAccount> ascPage =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(userAccount1, (List<UserAccount>)ascPage.getItems());
			assertContains(userAccount2, (List<UserAccount>)ascPage.getItems());

			Page<UserAccount> descPage =
				userAccountResource.getAssetLibraryUserAccountsPage(
					assetLibraryId, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				userAccount2, (List<UserAccount>)descPage.getItems());
			assertContains(
				userAccount1, (List<UserAccount>)descPage.getItems());
		}
	}

	protected UserAccount testGetAssetLibraryUserAccountsPage_addUserAccount(
			Long assetLibraryId, UserAccount userAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetAssetLibraryUserAccountsPage_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	protected Long
			testGetAssetLibraryUserAccountsPage_getIrrelevantAssetLibraryId()
		throws Exception {

		return irrelevantDepotEntry.getDepotEntryId();
	}

	@Test
	public void testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode()
		throws Exception {

		UserAccount postUserAccount =
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_addUserAccount();

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount putUserAccount =
			userAccountResource.
				putAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode(
					testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					postUserAccount.getExternalReferenceCode());

		assertEquals(randomUserAccount, putUserAccount);
		assertValid(putUserAccount);

		UserAccount getUserAccount =
			userAccountResource.
				getAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode(
					testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode(),
					putUserAccount.getExternalReferenceCode());

		assertEquals(randomUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testPutAssetLibraryByExternalReferenceCodeAssetLibraryExternalReferenceCodeUserAccountByExternalReferenceCodeUserAccountExternalReferenceCode_getAssetLibraryExternalReferenceCode()
		throws Exception {

		return testDepotEntryGroup.getExternalReferenceCode();
	}

	@Test
	public void testPutAssetLibraryUserAccount() throws Exception {
		UserAccount postUserAccount =
			testPutAssetLibraryUserAccount_addUserAccount();

		UserAccount randomUserAccount = randomUserAccount();

		UserAccount putUserAccount =
			userAccountResource.putAssetLibraryUserAccount(
				testPutAssetLibraryUserAccount_getAssetLibraryId(),
				postUserAccount.getId());

		assertEquals(randomUserAccount, putUserAccount);
		assertValid(putUserAccount);

		UserAccount getUserAccount =
			userAccountResource.getAssetLibraryUserAccount(
				testPutAssetLibraryUserAccount_getAssetLibraryId(),
				putUserAccount.getId());

		assertEquals(randomUserAccount, getUserAccount);
		assertValid(getUserAccount);
	}

	protected UserAccount testPutAssetLibraryUserAccount_addUserAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testPutAssetLibraryUserAccount_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		UserAccount userAccount1 =
			testBatchEngineDeleteImportTask_addAssetLibraryUserAccount();

		testBatchEngineDeleteImportTask_deleteUserAccount(
			200, userAccount1.getExternalReferenceCode(),
			"assetLibraryExternalReferenceCode",
			testDepotEntryGroup.getExternalReferenceCode());

		assertHttpResponseStatusCode(
			404,
			userAccountResource.getAssetLibraryUserAccountHttpResponse(
				testBatchEngineDeleteImportTask_getAssetLibraryId(),
				userAccount1.getId()));
	}

	protected UserAccount
			testBatchEngineDeleteImportTask_addAssetLibraryUserAccount()
		throws Exception {

		return testDeleteAssetLibraryUserAccount_addUserAccount();
	}

	protected void testBatchEngineDeleteImportTask_deleteUserAccount(
			int expectedStatusCode, String externalReferenceCode,
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
				"com.liferay.headless.asset.library.dto.v1_0.UserAccount", null,
				null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		if (expectedStatusCode == 200) {
			waitForFinish(
				"COMPLETED",
				JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
		}
	}

	protected Long testBatchEngineDeleteImportTask_getAssetLibraryId()
		throws Exception {

		return testDepotEntry.getDepotEntryId();
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

		if (userAccount.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (userAccount.getExternalReferenceCode() == null) {
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

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (userAccount.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("roles", additionalAssertFieldName)) {
				if (userAccount.getRoles() == null) {
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
					com.liferay.headless.asset.library.dto.v1_0.UserAccount.
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

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						userAccount1.getExternalReferenceCode(),
						userAccount2.getExternalReferenceCode())) {

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

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getName(), userAccount2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("roles", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						userAccount1.getRoles(), userAccount2.getRoles())) {

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

		if (entityFieldName.equals("roles")) {
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
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				image = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
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
	protected DepotEntry irrelevantDepotEntry;
	protected com.liferay.portal.kernel.model.Group irrelevantDepotEntryGroup;
	protected DepotEntry testDepotEntry;
	protected com.liferay.portal.kernel.model.Group testDepotEntryGroup;
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
	private com.liferay.headless.asset.library.resource.v1_0.UserAccountResource
		_userAccountResource;

}