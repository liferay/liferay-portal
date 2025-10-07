/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductConfigurationListAccount;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductConfigurationListAccountResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductConfigurationListAccountSerDes;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseProductConfigurationListAccountResourceTestCase {

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

		_productConfigurationListAccountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productConfigurationListAccountResource =
			ProductConfigurationListAccountResource.builder(
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

		ProductConfigurationListAccount productConfigurationListAccount1 =
			randomProductConfigurationListAccount();

		String json = objectMapper.writeValueAsString(
			productConfigurationListAccount1);

		ProductConfigurationListAccount productConfigurationListAccount2 =
			ProductConfigurationListAccountSerDes.toDTO(json);

		Assert.assertTrue(
			equals(
				productConfigurationListAccount1,
				productConfigurationListAccount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductConfigurationListAccount productConfigurationListAccount =
			randomProductConfigurationListAccount();

		String json1 = objectMapper.writeValueAsString(
			productConfigurationListAccount);
		String json2 = ProductConfigurationListAccountSerDes.toJSON(
			productConfigurationListAccount);

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

		ProductConfigurationListAccount productConfigurationListAccount =
			randomProductConfigurationListAccount();

		productConfigurationListAccount.setAccountExternalReferenceCode(regex);
		productConfigurationListAccount.
			setProductConfigurationListExternalReferenceCode(regex);

		String json = ProductConfigurationListAccountSerDes.toJSON(
			productConfigurationListAccount);

		Assert.assertFalse(json.contains(regex));

		productConfigurationListAccount =
			ProductConfigurationListAccountSerDes.toDTO(json);

		Assert.assertEquals(
			regex,
			productConfigurationListAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			regex,
			productConfigurationListAccount.
				getProductConfigurationListExternalReferenceCode());
	}

	@Test
	public void testDeleteProductConfigurationListAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationListAccount productConfigurationListAccount =
			testDeleteProductConfigurationListAccount_addProductConfigurationListAccount();

		assertHttpResponseStatusCode(
			204,
			productConfigurationListAccountResource.
				deleteProductConfigurationListAccountHttpResponse(
					productConfigurationListAccount.
						getProductConfigurationListAccountId()));
	}

	protected ProductConfigurationListAccount
			testDeleteProductConfigurationListAccount_addProductConfigurationListAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductConfigurationListAccount()
		throws Exception {

		// No namespace

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testGraphQLDeleteProductConfigurationListAccount_addProductConfigurationListAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductConfigurationListAccount",
						new HashMap<String, Object>() {
							{
								put(
									"productConfigurationListAccountId",
									productConfigurationListAccount1.
										getProductConfigurationListAccountId());
							}
						})),
				"JSONObject/data",
				"Object/deleteProductConfigurationListAccount"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductConfigurationListAccount productConfigurationListAccount2 =
			testGraphQLDeleteProductConfigurationListAccount_addProductConfigurationListAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductConfigurationListAccount",
							new HashMap<String, Object>() {
								{
									put(
										"productConfigurationListAccountId",
										productConfigurationListAccount2.
											getProductConfigurationListAccountId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductConfigurationListAccount"));
	}

	protected ProductConfigurationListAccount
			testGraphQLDeleteProductConfigurationListAccount_addProductConfigurationListAccount()
		throws Exception {

		return testGraphQLProductConfigurationListAccount_addProductConfigurationListAccount();
	}

	@Test
	public void testDeleteProductConfigurationListAccountBatch()
		throws Exception {

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testDeleteProductConfigurationListAccountBatch_addProductConfigurationListAccount();

		testDeleteProductConfigurationListAccountBatch_deleteProductConfigurationListAccount(
			202, null,
			productConfigurationListAccount1.
				getProductConfigurationListAccountId());
	}

	protected ProductConfigurationListAccount
			testDeleteProductConfigurationListAccountBatch_addProductConfigurationListAccount()
		throws Exception {

		return testDeleteProductConfigurationListAccount_addProductConfigurationListAccount();
	}

	protected void
			testDeleteProductConfigurationListAccountBatch_deleteProductConfigurationListAccount(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productConfigurationListAccountResource.
				deleteProductConfigurationListAccountBatchHttpResponse(
					null,
					JSONUtil.putAll(
						JSONUtil.put(
							"externalReferenceCode", () -> externalReferenceCode
						).put(
							"productConfigurationListAccountId", () -> id
						)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getIrrelevantExternalReferenceCode();

		Page<ProductConfigurationListAccount> page =
			productConfigurationListAccountResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductConfigurationListAccount
				irrelevantProductConfigurationListAccount =
					testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
						irrelevantExternalReferenceCode,
						randomIrrelevantProductConfigurationListAccount());

			page =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfigurationListAccount,
				(List<ProductConfigurationListAccount>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				externalReferenceCode, randomProductConfigurationListAccount());

		ProductConfigurationListAccount productConfigurationListAccount2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				externalReferenceCode, randomProductConfigurationListAccount());

		page =
			productConfigurationListAccountResource.
				getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationListAccount1,
			(List<ProductConfigurationListAccount>)page.getItems());
		assertContains(
			productConfigurationListAccount2,
			(List<ProductConfigurationListAccount>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExpectedActions(
				externalReferenceCode));

		productConfigurationListAccountResource.
			deleteProductConfigurationListAccount(
				productConfigurationListAccount1.
					getProductConfigurationListAccountId());

		productConfigurationListAccountResource.
			deleteProductConfigurationListAccount(
				productConfigurationListAccount2.
					getProductConfigurationListAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExternalReferenceCode();

		Page<ProductConfigurationListAccount>
			productConfigurationListAccountsPage =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListAccountsPage.getTotalCount());

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				externalReferenceCode, randomProductConfigurationListAccount());

		ProductConfigurationListAccount productConfigurationListAccount2 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				externalReferenceCode, randomProductConfigurationListAccount());

		ProductConfigurationListAccount productConfigurationListAccount3 =
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				externalReferenceCode, randomProductConfigurationListAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationListAccount> page1 =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationListAccount1,
				(List<ProductConfigurationListAccount>)page1.getItems());

			Page<ProductConfigurationListAccount> page2 =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productConfigurationListAccount2,
				(List<ProductConfigurationListAccount>)page2.getItems());

			Page<ProductConfigurationListAccount> page3 =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productConfigurationListAccount3,
				(List<ProductConfigurationListAccount>)page3.getItems());
		}
		else {
			Page<ProductConfigurationListAccount> page1 =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ProductConfigurationListAccount>
				productConfigurationListAccounts1 =
					(List<ProductConfigurationListAccount>)page1.getItems();

			Assert.assertEquals(
				productConfigurationListAccounts1.toString(), totalCount + 2,
				productConfigurationListAccounts1.size());

			Page<ProductConfigurationListAccount> page2 =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationListAccount>
				productConfigurationListAccounts2 =
					(List<ProductConfigurationListAccount>)page2.getItems();

			Assert.assertEquals(
				productConfigurationListAccounts2.toString(), 1,
				productConfigurationListAccounts2.size());

			Page<ProductConfigurationListAccount> page3 =
				productConfigurationListAccountResource.
					getProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productConfigurationListAccount1,
				(List<ProductConfigurationListAccount>)page3.getItems());
			assertContains(
				productConfigurationListAccount2,
				(List<ProductConfigurationListAccount>)page3.getItems());
			assertContains(
				productConfigurationListAccount3,
				(List<ProductConfigurationListAccount>)page3.getItems());
		}
	}

	protected ProductConfigurationListAccount
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				String externalReferenceCode,
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductConfigurationListByExternalReferenceCodeProductConfigurationListAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPage()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId();
		Long irrelevantId =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getIrrelevantId();

		Page<ProductConfigurationListAccount> page =
			productConfigurationListAccountResource.
				getProductConfigurationListIdProductConfigurationListAccountsPage(
					id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductConfigurationListAccount
				irrelevantProductConfigurationListAccount =
					testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
						irrelevantId,
						randomIrrelevantProductConfigurationListAccount());

			page =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						irrelevantId, null, null,
						Pagination.of(1, (int)totalCount + 1), null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductConfigurationListAccount,
				(List<ProductConfigurationListAccount>)page.getItems());
			assertValid(
				page,
				testGetProductConfigurationListIdProductConfigurationListAccountsPage_getExpectedActions(
					irrelevantId));
		}

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		ProductConfigurationListAccount productConfigurationListAccount2 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		page =
			productConfigurationListAccountResource.
				getProductConfigurationListIdProductConfigurationListAccountsPage(
					id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productConfigurationListAccount1,
			(List<ProductConfigurationListAccount>)page.getItems());
		assertContains(
			productConfigurationListAccount2,
			(List<ProductConfigurationListAccount>)page.getItems());
		assertValid(
			page,
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getExpectedActions(
				id));

		productConfigurationListAccountResource.
			deleteProductConfigurationListAccount(
				productConfigurationListAccount1.
					getProductConfigurationListAccountId());

		productConfigurationListAccountResource.
			deleteProductConfigurationListAccount(
				productConfigurationListAccount2.
					getProductConfigurationListAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId();

		ProductConfigurationListAccount productConfigurationListAccount1 =
			randomProductConfigurationListAccount();

		productConfigurationListAccount1 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, productConfigurationListAccount1);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListAccount> page =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null,
						getFilterString(
							entityField, "between",
							productConfigurationListAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfigurationListAccount1),
				(List<ProductConfigurationListAccount>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilterStringContains()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationListAccountsPageWithFilter(
				String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId();

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductConfigurationListAccount productConfigurationListAccount2 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListAccount> page =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null,
						getFilterString(
							entityField, operator,
							productConfigurationListAccount1),
						Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(productConfigurationListAccount1),
				(List<ProductConfigurationListAccount>)page.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithPagination()
		throws Exception {

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId();

		Page<ProductConfigurationListAccount>
			productConfigurationListAccountsPage =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			productConfigurationListAccountsPage.getTotalCount());

		ProductConfigurationListAccount productConfigurationListAccount1 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		ProductConfigurationListAccount productConfigurationListAccount2 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		ProductConfigurationListAccount productConfigurationListAccount3 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, randomProductConfigurationListAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductConfigurationListAccount> page1 =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productConfigurationListAccount1,
				(List<ProductConfigurationListAccount>)page1.getItems());

			Page<ProductConfigurationListAccount> page2 =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationListAccount2,
				(List<ProductConfigurationListAccount>)page2.getItems());

			Page<ProductConfigurationListAccount> page3 =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit),
						null);

			assertContains(
				productConfigurationListAccount3,
				(List<ProductConfigurationListAccount>)page3.getItems());
		}
		else {
			Page<ProductConfigurationListAccount> page1 =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null, Pagination.of(1, totalCount + 2), null);

			List<ProductConfigurationListAccount>
				productConfigurationListAccounts1 =
					(List<ProductConfigurationListAccount>)page1.getItems();

			Assert.assertEquals(
				productConfigurationListAccounts1.toString(), totalCount + 2,
				productConfigurationListAccounts1.size());

			Page<ProductConfigurationListAccount> page2 =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductConfigurationListAccount>
				productConfigurationListAccounts2 =
					(List<ProductConfigurationListAccount>)page2.getItems();

			Assert.assertEquals(
				productConfigurationListAccounts2.toString(), 1,
				productConfigurationListAccounts2.size());

			Page<ProductConfigurationListAccount> page3 =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null, Pagination.of(1, (int)totalCount + 3),
						null);

			assertContains(
				productConfigurationListAccount1,
				(List<ProductConfigurationListAccount>)page3.getItems());
			assertContains(
				productConfigurationListAccount2,
				(List<ProductConfigurationListAccount>)page3.getItems());
			assertContains(
				productConfigurationListAccount3,
				(List<ProductConfigurationListAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSortDateTime()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, productConfigurationListAccount1,
			 productConfigurationListAccount2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSortDouble()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, productConfigurationListAccount1,
			 productConfigurationListAccount2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListAccount1, entityField.getName(),
					0.1);
				BeanTestUtil.setProperty(
					productConfigurationListAccount2, entityField.getName(),
					0.5);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSortInteger()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, productConfigurationListAccount1,
			 productConfigurationListAccount2) -> {

				BeanTestUtil.setProperty(
					productConfigurationListAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					productConfigurationListAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSortString()
		throws Exception {

		testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, productConfigurationListAccount1,
			 productConfigurationListAccount2) -> {

				Class<?> clazz = productConfigurationListAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						productConfigurationListAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						productConfigurationListAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						productConfigurationListAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						productConfigurationListAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						productConfigurationListAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						productConfigurationListAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void
			testGetProductConfigurationListIdProductConfigurationListAccountsPageWithSort(
				EntityField.Type type,
				UnsafeTriConsumer
					<EntityField, ProductConfigurationListAccount,
					 ProductConfigurationListAccount, Exception>
						unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId();

		ProductConfigurationListAccount productConfigurationListAccount1 =
			randomProductConfigurationListAccount();
		ProductConfigurationListAccount productConfigurationListAccount2 =
			randomProductConfigurationListAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, productConfigurationListAccount1,
				productConfigurationListAccount2);
		}

		productConfigurationListAccount1 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, productConfigurationListAccount1);

		productConfigurationListAccount2 =
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				id, productConfigurationListAccount2);

		Page<ProductConfigurationListAccount> page =
			productConfigurationListAccountResource.
				getProductConfigurationListIdProductConfigurationListAccountsPage(
					id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<ProductConfigurationListAccount> ascPage =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":asc");

			assertContains(
				productConfigurationListAccount1,
				(List<ProductConfigurationListAccount>)ascPage.getItems());
			assertContains(
				productConfigurationListAccount2,
				(List<ProductConfigurationListAccount>)ascPage.getItems());

			Page<ProductConfigurationListAccount> descPage =
				productConfigurationListAccountResource.
					getProductConfigurationListIdProductConfigurationListAccountsPage(
						id, null, null,
						Pagination.of(1, (int)page.getTotalCount() + 1),
						entityField.getName() + ":desc");

			assertContains(
				productConfigurationListAccount2,
				(List<ProductConfigurationListAccount>)descPage.getItems());
			assertContains(
				productConfigurationListAccount1,
				(List<ProductConfigurationListAccount>)descPage.getItems());
		}
	}

	protected ProductConfigurationListAccount
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_addProductConfigurationListAccount(
				Long id,
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetProductConfigurationListIdProductConfigurationListAccountsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount()
		throws Exception {

		ProductConfigurationListAccount randomProductConfigurationListAccount =
			randomProductConfigurationListAccount();

		ProductConfigurationListAccount postProductConfigurationListAccount =
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount_addProductConfigurationListAccount(
				randomProductConfigurationListAccount);

		assertEquals(
			randomProductConfigurationListAccount,
			postProductConfigurationListAccount);
		assertValid(postProductConfigurationListAccount);
	}

	protected ProductConfigurationListAccount
			testPostProductConfigurationListByExternalReferenceCodeProductConfigurationListAccount_addProductConfigurationListAccount(
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductConfigurationListIdProductConfigurationListAccount()
		throws Exception {

		ProductConfigurationListAccount randomProductConfigurationListAccount =
			randomProductConfigurationListAccount();

		ProductConfigurationListAccount postProductConfigurationListAccount =
			testPostProductConfigurationListIdProductConfigurationListAccount_addProductConfigurationListAccount(
				randomProductConfigurationListAccount);

		assertEquals(
			randomProductConfigurationListAccount,
			postProductConfigurationListAccount);
		assertValid(postProductConfigurationListAccount);
	}

	protected ProductConfigurationListAccount
			testPostProductConfigurationListIdProductConfigurationListAccount_addProductConfigurationListAccount(
				ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductConfigurationListAccount productConfigurationListAccount1 =
			testBatchEngineDeleteImportTask_addProductConfigurationListAccount();

		testBatchEngineDeleteImportTask_deleteProductConfigurationListAccount(
			200, null,
			productConfigurationListAccount1.
				getProductConfigurationListAccountId());
	}

	protected ProductConfigurationListAccount
			testBatchEngineDeleteImportTask_addProductConfigurationListAccount()
		throws Exception {

		return testDeleteProductConfigurationListAccount_addProductConfigurationListAccount();
	}

	protected void
			testBatchEngineDeleteImportTask_deleteProductConfigurationListAccount(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductConfigurationListAccount",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"productConfigurationListAccountId", () -> id
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

	protected ProductConfigurationListAccount
			testGraphQLProductConfigurationListAccount_addProductConfigurationListAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductConfigurationListAccount productConfigurationListAccount,
		List<ProductConfigurationListAccount>
			productConfigurationListAccounts) {

		boolean contains = false;

		for (ProductConfigurationListAccount item :
				productConfigurationListAccounts) {

			if (equals(productConfigurationListAccount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productConfigurationListAccounts + " does not contain " +
				productConfigurationListAccount,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductConfigurationListAccount productConfigurationListAccount1,
		ProductConfigurationListAccount productConfigurationListAccount2) {

		Assert.assertTrue(
			productConfigurationListAccount1 + " does not equal " +
				productConfigurationListAccount2,
			equals(
				productConfigurationListAccount1,
				productConfigurationListAccount2));
	}

	protected void assertEquals(
		List<ProductConfigurationListAccount> productConfigurationListAccounts1,
		List<ProductConfigurationListAccount>
			productConfigurationListAccounts2) {

		Assert.assertEquals(
			productConfigurationListAccounts1.size(),
			productConfigurationListAccounts2.size());

		for (int i = 0; i < productConfigurationListAccounts1.size(); i++) {
			ProductConfigurationListAccount productConfigurationListAccount1 =
				productConfigurationListAccounts1.get(i);
			ProductConfigurationListAccount productConfigurationListAccount2 =
				productConfigurationListAccounts2.get(i);

			assertEquals(
				productConfigurationListAccount1,
				productConfigurationListAccount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductConfigurationListAccount> productConfigurationListAccounts1,
		List<ProductConfigurationListAccount>
			productConfigurationListAccounts2) {

		Assert.assertEquals(
			productConfigurationListAccounts1.size(),
			productConfigurationListAccounts2.size());

		for (ProductConfigurationListAccount productConfigurationListAccount1 :
				productConfigurationListAccounts1) {

			boolean contains = false;

			for (ProductConfigurationListAccount
					productConfigurationListAccount2 :
						productConfigurationListAccounts2) {

				if (equals(
						productConfigurationListAccount1,
						productConfigurationListAccount2)) {

					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productConfigurationListAccounts2 + " does not contain " +
					productConfigurationListAccount1,
				contains);
		}
	}

	protected void assertValid(
			ProductConfigurationListAccount productConfigurationListAccount)
		throws Exception {

		boolean valid = true;

		if (productConfigurationListAccount.
				getProductConfigurationListAccountId() == null) {

			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (productConfigurationListAccount.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationListAccount.
						getAccountExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (productConfigurationListAccount.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (productConfigurationListAccount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListAccountId",
					additionalAssertFieldName)) {

				if (productConfigurationListAccount.
						getProductConfigurationListAccountId() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productConfigurationListAccount.
						getProductConfigurationListExternalReferenceCode() ==
							null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListId", additionalAssertFieldName)) {

				if (productConfigurationListAccount.
						getProductConfigurationListId() == null) {

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

	protected void assertValid(Page<ProductConfigurationListAccount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductConfigurationListAccount> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductConfigurationListAccount>
			productConfigurationListAccounts = page.getItems();

		int size = productConfigurationListAccounts.size();

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

		graphQLFields.add(
			new GraphQLField("productConfigurationListAccountId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						ProductConfigurationListAccount.class)) {

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
		ProductConfigurationListAccount productConfigurationListAccount1,
		ProductConfigurationListAccount productConfigurationListAccount2) {

		if (productConfigurationListAccount1 ==
				productConfigurationListAccount2) {

			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListAccount1.getAccount(),
						productConfigurationListAccount2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccount1.
							getAccountExternalReferenceCode(),
						productConfigurationListAccount2.
							getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productConfigurationListAccount1.getAccountId(),
						productConfigurationListAccount2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)productConfigurationListAccount1.getActions(),
						(Map)productConfigurationListAccount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListAccountId",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccount1.
							getProductConfigurationListAccountId(),
						productConfigurationListAccount2.
							getProductConfigurationListAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccount1.
							getProductConfigurationListExternalReferenceCode(),
						productConfigurationListAccount2.
							getProductConfigurationListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productConfigurationListId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productConfigurationListAccount1.
							getProductConfigurationListId(),
						productConfigurationListAccount2.
							getProductConfigurationListId())) {

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

		if (!(_productConfigurationListAccountResource instanceof
				EntityModelResource)) {

			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productConfigurationListAccountResource;

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
		ProductConfigurationListAccount productConfigurationListAccount) {

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
			Object object =
				productConfigurationListAccount.
					getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("productConfigurationListAccountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals(
				"productConfigurationListExternalReferenceCode")) {

			Object object =
				productConfigurationListAccount.
					getProductConfigurationListExternalReferenceCode();

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

		if (entityFieldName.equals("productConfigurationListId")) {
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

	protected ProductConfigurationListAccount
			randomProductConfigurationListAccount()
		throws Exception {

		return new ProductConfigurationListAccount() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				productConfigurationListAccountId = RandomTestUtil.randomLong();
				productConfigurationListExternalReferenceCode =
					StringUtil.toLowerCase(RandomTestUtil.randomString());
				productConfigurationListId = RandomTestUtil.randomLong();
			}
		};
	}

	protected ProductConfigurationListAccount
			randomIrrelevantProductConfigurationListAccount()
		throws Exception {

		ProductConfigurationListAccount
			randomIrrelevantProductConfigurationListAccount =
				randomProductConfigurationListAccount();

		return randomIrrelevantProductConfigurationListAccount;
	}

	protected ProductConfigurationListAccount
			randomPatchProductConfigurationListAccount()
		throws Exception {

		return randomProductConfigurationListAccount();
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

	protected ProductConfigurationListAccountResource
		productConfigurationListAccountResource;
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
			BaseProductConfigurationListAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductConfigurationListAccountResource
			_productConfigurationListAccountResource;

}