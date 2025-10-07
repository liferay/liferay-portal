/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.inventory.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.inventory.client.dto.v1_0.WarehouseAccount;
import com.liferay.headless.commerce.admin.inventory.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Page;
import com.liferay.headless.commerce.admin.inventory.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.inventory.client.resource.v1_0.WarehouseAccountResource;
import com.liferay.headless.commerce.admin.inventory.client.serdes.v1_0.WarehouseAccountSerDes;
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
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.odata.entity.EntityField;
import com.liferay.portal.odata.entity.EntityModel;
import com.liferay.portal.search.test.rule.SearchTestRule;
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
 * @author Alessio Antonio Rendina
 * @generated
 */
@Generated("")
public abstract class BaseWarehouseAccountResourceTestCase {

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

		_warehouseAccountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		warehouseAccountResource = WarehouseAccountResource.builder(
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

		WarehouseAccount warehouseAccount1 = randomWarehouseAccount();

		String json = objectMapper.writeValueAsString(warehouseAccount1);

		WarehouseAccount warehouseAccount2 = WarehouseAccountSerDes.toDTO(json);

		Assert.assertTrue(equals(warehouseAccount1, warehouseAccount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		WarehouseAccount warehouseAccount = randomWarehouseAccount();

		String json1 = objectMapper.writeValueAsString(warehouseAccount);
		String json2 = WarehouseAccountSerDes.toJSON(warehouseAccount);

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

		WarehouseAccount warehouseAccount = randomWarehouseAccount();

		warehouseAccount.setAccountExternalReferenceCode(regex);
		warehouseAccount.setWarehouseExternalReferenceCode(regex);

		String json = WarehouseAccountSerDes.toJSON(warehouseAccount);

		Assert.assertFalse(json.contains(regex));

		warehouseAccount = WarehouseAccountSerDes.toDTO(json);

		Assert.assertEquals(
			regex, warehouseAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			regex, warehouseAccount.getWarehouseExternalReferenceCode());
	}

	@Test
	public void testDeleteWarehouseAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		WarehouseAccount warehouseAccount =
			testDeleteWarehouseAccount_addWarehouseAccount();

		assertHttpResponseStatusCode(
			204,
			warehouseAccountResource.deleteWarehouseAccountHttpResponse(
				warehouseAccount.getWarehouseAccountId()));
	}

	protected WarehouseAccount testDeleteWarehouseAccount_addWarehouseAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteWarehouseAccount() throws Exception {

		// No namespace

		WarehouseAccount warehouseAccount1 =
			testGraphQLDeleteWarehouseAccount_addWarehouseAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteWarehouseAccount",
						new HashMap<String, Object>() {
							{
								put(
									"warehouseAccountId",
									warehouseAccount1.getWarehouseAccountId());
							}
						})),
				"JSONObject/data", "Object/deleteWarehouseAccount"));

		// Using the namespace headlessCommerceAdminInventory_v1_0

		WarehouseAccount warehouseAccount2 =
			testGraphQLDeleteWarehouseAccount_addWarehouseAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminInventory_v1_0",
						new GraphQLField(
							"deleteWarehouseAccount",
							new HashMap<String, Object>() {
								{
									put(
										"warehouseAccountId",
										warehouseAccount2.
											getWarehouseAccountId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminInventory_v1_0",
				"Object/deleteWarehouseAccount"));
	}

	protected WarehouseAccount
			testGraphQLDeleteWarehouseAccount_addWarehouseAccount()
		throws Exception {

		return testGraphQLWarehouseAccount_addWarehouseAccount();
	}

	@Test
	public void testDeleteWarehouseAccountBatch() throws Exception {
		WarehouseAccount warehouseAccount1 =
			testDeleteWarehouseAccountBatch_addWarehouseAccount();

		testDeleteWarehouseAccountBatch_deleteWarehouseAccount(
			202, null, warehouseAccount1.getWarehouseAccountId());
	}

	protected WarehouseAccount
			testDeleteWarehouseAccountBatch_addWarehouseAccount()
		throws Exception {

		return testDeleteWarehouseAccount_addWarehouseAccount();
	}

	protected void testDeleteWarehouseAccountBatch_deleteWarehouseAccount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			warehouseAccountResource.deleteWarehouseAccountBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"warehouseAccountId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getIrrelevantExternalReferenceCode();

		Page<WarehouseAccount> page =
			warehouseAccountResource.
				getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			WarehouseAccount irrelevantWarehouseAccount =
				testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantWarehouseAccount());

			page =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWarehouseAccount,
				(List<WarehouseAccount>)page.getItems());
			assertValid(
				page,
				testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		WarehouseAccount warehouseAccount1 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				externalReferenceCode, randomWarehouseAccount());

		WarehouseAccount warehouseAccount2 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				externalReferenceCode, randomWarehouseAccount());

		page =
			warehouseAccountResource.
				getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			warehouseAccount1, (List<WarehouseAccount>)page.getItems());
		assertContains(
			warehouseAccount2, (List<WarehouseAccount>)page.getItems());
		assertValid(
			page,
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExpectedActions(
				externalReferenceCode));

		warehouseAccountResource.deleteWarehouseAccount(
			warehouseAccount1.getWarehouseAccountId());

		warehouseAccountResource.deleteWarehouseAccount(
			warehouseAccount2.getWarehouseAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseByExternalReferenceCodeWarehouseAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExternalReferenceCode();

		Page<WarehouseAccount> warehouseAccountsPage =
			warehouseAccountResource.
				getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			warehouseAccountsPage.getTotalCount());

		WarehouseAccount warehouseAccount1 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				externalReferenceCode, randomWarehouseAccount());

		WarehouseAccount warehouseAccount2 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				externalReferenceCode, randomWarehouseAccount());

		WarehouseAccount warehouseAccount3 =
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				externalReferenceCode, randomWarehouseAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseAccount> page1 =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseAccount1, (List<WarehouseAccount>)page1.getItems());

			Page<WarehouseAccount> page2 =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				warehouseAccount2, (List<WarehouseAccount>)page2.getItems());

			Page<WarehouseAccount> page3 =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				warehouseAccount3, (List<WarehouseAccount>)page3.getItems());
		}
		else {
			Page<WarehouseAccount> page1 =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<WarehouseAccount> warehouseAccounts1 =
				(List<WarehouseAccount>)page1.getItems();

			Assert.assertEquals(
				warehouseAccounts1.toString(), totalCount + 2,
				warehouseAccounts1.size());

			Page<WarehouseAccount> page2 =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseAccount> warehouseAccounts2 =
				(List<WarehouseAccount>)page2.getItems();

			Assert.assertEquals(
				warehouseAccounts2.toString(), 1, warehouseAccounts2.size());

			Page<WarehouseAccount> page3 =
				warehouseAccountResource.
					getWarehouseByExternalReferenceCodeWarehouseAccountsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				warehouseAccount1, (List<WarehouseAccount>)page3.getItems());
			assertContains(
				warehouseAccount2, (List<WarehouseAccount>)page3.getItems());
			assertContains(
				warehouseAccount3, (List<WarehouseAccount>)page3.getItems());
		}
	}

	protected WarehouseAccount
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_addWarehouseAccount(
				String externalReferenceCode, WarehouseAccount warehouseAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetWarehouseByExternalReferenceCodeWarehouseAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPage() throws Exception {
		Long id = testGetWarehouseIdWarehouseAccountsPage_getId();
		Long irrelevantId =
			testGetWarehouseIdWarehouseAccountsPage_getIrrelevantId();

		Page<WarehouseAccount> page =
			warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			WarehouseAccount irrelevantWarehouseAccount =
				testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
					irrelevantId, randomIrrelevantWarehouseAccount());

			page = warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantWarehouseAccount,
				(List<WarehouseAccount>)page.getItems());
			assertValid(
				page,
				testGetWarehouseIdWarehouseAccountsPage_getExpectedActions(
					irrelevantId));
		}

		WarehouseAccount warehouseAccount1 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		WarehouseAccount warehouseAccount2 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		page = warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			warehouseAccount1, (List<WarehouseAccount>)page.getItems());
		assertContains(
			warehouseAccount2, (List<WarehouseAccount>)page.getItems());
		assertValid(
			page,
			testGetWarehouseIdWarehouseAccountsPage_getExpectedActions(id));

		warehouseAccountResource.deleteWarehouseAccount(
			warehouseAccount1.getWarehouseAccountId());

		warehouseAccountResource.deleteWarehouseAccount(
			warehouseAccount2.getWarehouseAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetWarehouseIdWarehouseAccountsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetWarehouseIdWarehouseAccountsPage_getId();

		WarehouseAccount warehouseAccount1 = randomWarehouseAccount();

		warehouseAccount1 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, warehouseAccount1);

		for (EntityField entityField : entityFields) {
			Page<WarehouseAccount> page =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null,
					getFilterString(entityField, "between", warehouseAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(warehouseAccount1),
				(List<WarehouseAccount>)page.getItems());
		}
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithFilterStringContains()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetWarehouseIdWarehouseAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetWarehouseIdWarehouseAccountsPage_getId();

		WarehouseAccount warehouseAccount1 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		WarehouseAccount warehouseAccount2 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		for (EntityField entityField : entityFields) {
			Page<WarehouseAccount> page =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null,
					getFilterString(entityField, operator, warehouseAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(warehouseAccount1),
				(List<WarehouseAccount>)page.getItems());
		}
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithPagination()
		throws Exception {

		Long id = testGetWarehouseIdWarehouseAccountsPage_getId();

		Page<WarehouseAccount> warehouseAccountsPage =
			warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			warehouseAccountsPage.getTotalCount());

		WarehouseAccount warehouseAccount1 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		WarehouseAccount warehouseAccount2 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		WarehouseAccount warehouseAccount3 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, randomWarehouseAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<WarehouseAccount> page1 =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				warehouseAccount1, (List<WarehouseAccount>)page1.getItems());

			Page<WarehouseAccount> page2 =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				warehouseAccount2, (List<WarehouseAccount>)page2.getItems());

			Page<WarehouseAccount> page3 =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				warehouseAccount3, (List<WarehouseAccount>)page3.getItems());
		}
		else {
			Page<WarehouseAccount> page1 =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<WarehouseAccount> warehouseAccounts1 =
				(List<WarehouseAccount>)page1.getItems();

			Assert.assertEquals(
				warehouseAccounts1.toString(), totalCount + 2,
				warehouseAccounts1.size());

			Page<WarehouseAccount> page2 =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<WarehouseAccount> warehouseAccounts2 =
				(List<WarehouseAccount>)page2.getItems();

			Assert.assertEquals(
				warehouseAccounts2.toString(), 1, warehouseAccounts2.size());

			Page<WarehouseAccount> page3 =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				warehouseAccount1, (List<WarehouseAccount>)page3.getItems());
			assertContains(
				warehouseAccount2, (List<WarehouseAccount>)page3.getItems());
			assertContains(
				warehouseAccount3, (List<WarehouseAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithSortDateTime()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, warehouseAccount1, warehouseAccount2) -> {
				BeanTestUtil.setProperty(
					warehouseAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithSortDouble()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, warehouseAccount1, warehouseAccount2) -> {
				BeanTestUtil.setProperty(
					warehouseAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					warehouseAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithSortInteger()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, warehouseAccount1, warehouseAccount2) -> {
				BeanTestUtil.setProperty(
					warehouseAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					warehouseAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetWarehouseIdWarehouseAccountsPageWithSortString()
		throws Exception {

		testGetWarehouseIdWarehouseAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, warehouseAccount1, warehouseAccount2) -> {
				Class<?> clazz = warehouseAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						warehouseAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						warehouseAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						warehouseAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						warehouseAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						warehouseAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						warehouseAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetWarehouseIdWarehouseAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, WarehouseAccount, WarehouseAccount, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetWarehouseIdWarehouseAccountsPage_getId();

		WarehouseAccount warehouseAccount1 = randomWarehouseAccount();
		WarehouseAccount warehouseAccount2 = randomWarehouseAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, warehouseAccount1, warehouseAccount2);
		}

		warehouseAccount1 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, warehouseAccount1);

		warehouseAccount2 =
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				id, warehouseAccount2);

		Page<WarehouseAccount> page =
			warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<WarehouseAccount> ascPage =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				warehouseAccount1, (List<WarehouseAccount>)ascPage.getItems());
			assertContains(
				warehouseAccount2, (List<WarehouseAccount>)ascPage.getItems());

			Page<WarehouseAccount> descPage =
				warehouseAccountResource.getWarehouseIdWarehouseAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				warehouseAccount2, (List<WarehouseAccount>)descPage.getItems());
			assertContains(
				warehouseAccount1, (List<WarehouseAccount>)descPage.getItems());
		}
	}

	protected WarehouseAccount
			testGetWarehouseIdWarehouseAccountsPage_addWarehouseAccount(
				Long id, WarehouseAccount warehouseAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWarehouseIdWarehouseAccountsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetWarehouseIdWarehouseAccountsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostWarehouseByExternalReferenceCodeWarehouseAccount()
		throws Exception {

		WarehouseAccount randomWarehouseAccount = randomWarehouseAccount();

		WarehouseAccount postWarehouseAccount =
			testPostWarehouseByExternalReferenceCodeWarehouseAccount_addWarehouseAccount(
				randomWarehouseAccount);

		assertEquals(randomWarehouseAccount, postWarehouseAccount);
		assertValid(postWarehouseAccount);
	}

	protected WarehouseAccount
			testPostWarehouseByExternalReferenceCodeWarehouseAccount_addWarehouseAccount(
				WarehouseAccount warehouseAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostWarehouseIdWarehouseAccount() throws Exception {
		WarehouseAccount randomWarehouseAccount = randomWarehouseAccount();

		WarehouseAccount postWarehouseAccount =
			testPostWarehouseIdWarehouseAccount_addWarehouseAccount(
				randomWarehouseAccount);

		assertEquals(randomWarehouseAccount, postWarehouseAccount);
		assertValid(postWarehouseAccount);
	}

	protected WarehouseAccount
			testPostWarehouseIdWarehouseAccount_addWarehouseAccount(
				WarehouseAccount warehouseAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		WarehouseAccount warehouseAccount1 =
			testBatchEngineDeleteImportTask_addWarehouseAccount();

		testBatchEngineDeleteImportTask_deleteWarehouseAccount(
			200, null, warehouseAccount1.getWarehouseAccountId());
	}

	protected WarehouseAccount
			testBatchEngineDeleteImportTask_addWarehouseAccount()
		throws Exception {

		return testDeleteWarehouseAccount_addWarehouseAccount();
	}

	protected void testBatchEngineDeleteImportTask_deleteWarehouseAccount(
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
				"com.liferay.headless.commerce.admin.inventory.dto.v1_0.WarehouseAccount",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"warehouseAccountId", () -> id
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

	protected WarehouseAccount testGraphQLWarehouseAccount_addWarehouseAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		WarehouseAccount warehouseAccount,
		List<WarehouseAccount> warehouseAccounts) {

		boolean contains = false;

		for (WarehouseAccount item : warehouseAccounts) {
			if (equals(warehouseAccount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			warehouseAccounts + " does not contain " + warehouseAccount,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		WarehouseAccount warehouseAccount1,
		WarehouseAccount warehouseAccount2) {

		Assert.assertTrue(
			warehouseAccount1 + " does not equal " + warehouseAccount2,
			equals(warehouseAccount1, warehouseAccount2));
	}

	protected void assertEquals(
		List<WarehouseAccount> warehouseAccounts1,
		List<WarehouseAccount> warehouseAccounts2) {

		Assert.assertEquals(
			warehouseAccounts1.size(), warehouseAccounts2.size());

		for (int i = 0; i < warehouseAccounts1.size(); i++) {
			WarehouseAccount warehouseAccount1 = warehouseAccounts1.get(i);
			WarehouseAccount warehouseAccount2 = warehouseAccounts2.get(i);

			assertEquals(warehouseAccount1, warehouseAccount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<WarehouseAccount> warehouseAccounts1,
		List<WarehouseAccount> warehouseAccounts2) {

		Assert.assertEquals(
			warehouseAccounts1.size(), warehouseAccounts2.size());

		for (WarehouseAccount warehouseAccount1 : warehouseAccounts1) {
			boolean contains = false;

			for (WarehouseAccount warehouseAccount2 : warehouseAccounts2) {
				if (equals(warehouseAccount1, warehouseAccount2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				warehouseAccounts2 + " does not contain " + warehouseAccount1,
				contains);
		}
	}

	protected void assertValid(WarehouseAccount warehouseAccount)
		throws Exception {

		boolean valid = true;

		if (warehouseAccount.getWarehouseAccountId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (warehouseAccount.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (warehouseAccount.getAccountExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (warehouseAccount.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (warehouseAccount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseAccountId", additionalAssertFieldName)) {

				if (warehouseAccount.getWarehouseAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (warehouseAccount.getWarehouseExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (warehouseAccount.getWarehouseId() == null) {
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

	protected void assertValid(Page<WarehouseAccount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<WarehouseAccount> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<WarehouseAccount> warehouseAccounts =
			page.getItems();

		int size = warehouseAccounts.size();

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

		graphQLFields.add(new GraphQLField("warehouseAccountId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.inventory.dto.v1_0.
						WarehouseAccount.class)) {

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
		WarehouseAccount warehouseAccount1,
		WarehouseAccount warehouseAccount2) {

		if (warehouseAccount1 == warehouseAccount2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseAccount1.getAccount(),
						warehouseAccount2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseAccount1.getAccountExternalReferenceCode(),
						warehouseAccount2.getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseAccount1.getAccountId(),
						warehouseAccount2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)warehouseAccount1.getActions(),
						(Map)warehouseAccount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseAccountId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseAccount1.getWarehouseAccountId(),
						warehouseAccount2.getWarehouseAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"warehouseExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						warehouseAccount1.getWarehouseExternalReferenceCode(),
						warehouseAccount2.
							getWarehouseExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("warehouseId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						warehouseAccount1.getWarehouseId(),
						warehouseAccount2.getWarehouseId())) {

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

		if (!(_warehouseAccountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_warehouseAccountResource;

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
		WarehouseAccount warehouseAccount) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("account")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("accountExternalReferenceCode")) {
			Object object = warehouseAccount.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("warehouseAccountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("warehouseExternalReferenceCode")) {
			Object object =
				warehouseAccount.getWarehouseExternalReferenceCode();

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

		if (entityFieldName.equals("warehouseId")) {
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

	protected WarehouseAccount randomWarehouseAccount() throws Exception {
		return new WarehouseAccount() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				warehouseAccountId = RandomTestUtil.randomLong();
				warehouseExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				warehouseId = RandomTestUtil.randomLong();
			}
		};
	}

	protected WarehouseAccount randomIrrelevantWarehouseAccount()
		throws Exception {

		WarehouseAccount randomIrrelevantWarehouseAccount =
			randomWarehouseAccount();

		return randomIrrelevantWarehouseAccount;
	}

	protected WarehouseAccount randomPatchWarehouseAccount() throws Exception {
		return randomWarehouseAccount();
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

	protected WarehouseAccountResource warehouseAccountResource;
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
		LogFactoryUtil.getLog(BaseWarehouseAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.inventory.resource.v1_0.
		WarehouseAccountResource _warehouseAccountResource;

}