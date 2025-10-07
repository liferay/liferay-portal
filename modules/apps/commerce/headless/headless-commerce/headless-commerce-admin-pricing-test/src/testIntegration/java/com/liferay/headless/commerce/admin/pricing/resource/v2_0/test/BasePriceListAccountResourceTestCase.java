/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListAccount;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceListAccountResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.PriceListAccountSerDes;
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
public abstract class BasePriceListAccountResourceTestCase {

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

		_priceListAccountResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		priceListAccountResource = PriceListAccountResource.builder(
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

		PriceListAccount priceListAccount1 = randomPriceListAccount();

		String json = objectMapper.writeValueAsString(priceListAccount1);

		PriceListAccount priceListAccount2 = PriceListAccountSerDes.toDTO(json);

		Assert.assertTrue(equals(priceListAccount1, priceListAccount2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		PriceListAccount priceListAccount = randomPriceListAccount();

		String json1 = objectMapper.writeValueAsString(priceListAccount);
		String json2 = PriceListAccountSerDes.toJSON(priceListAccount);

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

		PriceListAccount priceListAccount = randomPriceListAccount();

		priceListAccount.setAccountExternalReferenceCode(regex);
		priceListAccount.setPriceListExternalReferenceCode(regex);

		String json = PriceListAccountSerDes.toJSON(priceListAccount);

		Assert.assertFalse(json.contains(regex));

		priceListAccount = PriceListAccountSerDes.toDTO(json);

		Assert.assertEquals(
			regex, priceListAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			regex, priceListAccount.getPriceListExternalReferenceCode());
	}

	@Test
	public void testDeletePriceListAccount() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceListAccount priceListAccount =
			testDeletePriceListAccount_addPriceListAccount();

		assertHttpResponseStatusCode(
			204,
			priceListAccountResource.deletePriceListAccountHttpResponse(
				priceListAccount.getPriceListAccountId()));
	}

	protected PriceListAccount testDeletePriceListAccount_addPriceListAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeletePriceListAccount() throws Exception {

		// No namespace

		PriceListAccount priceListAccount1 =
			testGraphQLDeletePriceListAccount_addPriceListAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deletePriceListAccount",
						new HashMap<String, Object>() {
							{
								put(
									"priceListAccountId",
									priceListAccount1.getPriceListAccountId());
							}
						})),
				"JSONObject/data", "Object/deletePriceListAccount"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		PriceListAccount priceListAccount2 =
			testGraphQLDeletePriceListAccount_addPriceListAccount();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deletePriceListAccount",
							new HashMap<String, Object>() {
								{
									put(
										"priceListAccountId",
										priceListAccount2.
											getPriceListAccountId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deletePriceListAccount"));
	}

	protected PriceListAccount
			testGraphQLDeletePriceListAccount_addPriceListAccount()
		throws Exception {

		return testGraphQLPriceListAccount_addPriceListAccount();
	}

	@Test
	public void testDeletePriceListAccountBatch() throws Exception {
		PriceListAccount priceListAccount1 =
			testDeletePriceListAccountBatch_addPriceListAccount();

		testDeletePriceListAccountBatch_deletePriceListAccount(
			202, null, priceListAccount1.getPriceListAccountId());
	}

	protected PriceListAccount
			testDeletePriceListAccountBatch_addPriceListAccount()
		throws Exception {

		return testDeletePriceListAccount_addPriceListAccount();
	}

	protected void testDeletePriceListAccountBatch_deletePriceListAccount(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			priceListAccountResource.deletePriceListAccountBatchHttpResponse(
				null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceListAccountId", () -> id
					)));

		Assert.assertEquals(expectedStatusCode, httpResponse.getStatusCode());

		waitForFinish(
			"COMPLETED",
			JSONFactoryUtil.createJSONObject(httpResponse.getContent()));
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceListAccountsPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getIrrelevantExternalReferenceCode();

		Page<PriceListAccount> page =
			priceListAccountResource.
				getPriceListByExternalReferenceCodePriceListAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			PriceListAccount irrelevantPriceListAccount =
				testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
					irrelevantExternalReferenceCode,
					randomIrrelevantPriceListAccount());

			page =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceListAccount,
				(List<PriceListAccount>)page.getItems());
			assertValid(
				page,
				testGetPriceListByExternalReferenceCodePriceListAccountsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		PriceListAccount priceListAccount1 =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
				externalReferenceCode, randomPriceListAccount());

		PriceListAccount priceListAccount2 =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
				externalReferenceCode, randomPriceListAccount());

		page =
			priceListAccountResource.
				getPriceListByExternalReferenceCodePriceListAccountsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceListAccount1, (List<PriceListAccount>)page.getItems());
		assertContains(
			priceListAccount2, (List<PriceListAccount>)page.getItems());
		assertValid(
			page,
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getExpectedActions(
				externalReferenceCode));

		priceListAccountResource.deletePriceListAccount(
			priceListAccount1.getPriceListAccountId());

		priceListAccountResource.deletePriceListAccount(
			priceListAccount2.getPriceListAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListByExternalReferenceCodePriceListAccountsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getExternalReferenceCode();

		Page<PriceListAccount> priceListAccountsPage =
			priceListAccountResource.
				getPriceListByExternalReferenceCodePriceListAccountsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			priceListAccountsPage.getTotalCount());

		PriceListAccount priceListAccount1 =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
				externalReferenceCode, randomPriceListAccount());

		PriceListAccount priceListAccount2 =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
				externalReferenceCode, randomPriceListAccount());

		PriceListAccount priceListAccount3 =
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
				externalReferenceCode, randomPriceListAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceListAccount> page1 =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceListAccount1, (List<PriceListAccount>)page1.getItems());

			Page<PriceListAccount> page2 =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceListAccount2, (List<PriceListAccount>)page2.getItems());

			Page<PriceListAccount> page3 =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				priceListAccount3, (List<PriceListAccount>)page3.getItems());
		}
		else {
			Page<PriceListAccount> page1 =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<PriceListAccount> priceListAccounts1 =
				(List<PriceListAccount>)page1.getItems();

			Assert.assertEquals(
				priceListAccounts1.toString(), totalCount + 2,
				priceListAccounts1.size());

			Page<PriceListAccount> page2 =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceListAccount> priceListAccounts2 =
				(List<PriceListAccount>)page2.getItems();

			Assert.assertEquals(
				priceListAccounts2.toString(), 1, priceListAccounts2.size());

			Page<PriceListAccount> page3 =
				priceListAccountResource.
					getPriceListByExternalReferenceCodePriceListAccountsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				priceListAccount1, (List<PriceListAccount>)page3.getItems());
			assertContains(
				priceListAccount2, (List<PriceListAccount>)page3.getItems());
			assertContains(
				priceListAccount3, (List<PriceListAccount>)page3.getItems());
		}
	}

	protected PriceListAccount
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_addPriceListAccount(
				String externalReferenceCode, PriceListAccount priceListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceListByExternalReferenceCodePriceListAccountsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPage() throws Exception {
		Long id = testGetPriceListIdPriceListAccountsPage_getId();
		Long irrelevantId =
			testGetPriceListIdPriceListAccountsPage_getIrrelevantId();

		Page<PriceListAccount> page =
			priceListAccountResource.getPriceListIdPriceListAccountsPage(
				id, null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			PriceListAccount irrelevantPriceListAccount =
				testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
					irrelevantId, randomIrrelevantPriceListAccount());

			page = priceListAccountResource.getPriceListIdPriceListAccountsPage(
				irrelevantId, null, null, Pagination.of(1, (int)totalCount + 1),
				null);

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantPriceListAccount,
				(List<PriceListAccount>)page.getItems());
			assertValid(
				page,
				testGetPriceListIdPriceListAccountsPage_getExpectedActions(
					irrelevantId));
		}

		PriceListAccount priceListAccount1 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		PriceListAccount priceListAccount2 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		page = priceListAccountResource.getPriceListIdPriceListAccountsPage(
			id, null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			priceListAccount1, (List<PriceListAccount>)page.getItems());
		assertContains(
			priceListAccount2, (List<PriceListAccount>)page.getItems());
		assertValid(
			page,
			testGetPriceListIdPriceListAccountsPage_getExpectedActions(id));

		priceListAccountResource.deletePriceListAccount(
			priceListAccount1.getPriceListAccountId());

		priceListAccountResource.deletePriceListAccount(
			priceListAccount2.getPriceListAccountId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceListIdPriceListAccountsPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceListAccountsPage_getId();

		PriceListAccount priceListAccount1 = randomPriceListAccount();

		priceListAccount1 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, priceListAccount1);

		for (EntityField entityField : entityFields) {
			Page<PriceListAccount> page =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null,
					getFilterString(entityField, "between", priceListAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceListAccount1),
				(List<PriceListAccount>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithFilterDoubleEquals()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithFilter(
			"eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithFilterStringContains()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithFilterStringEquals()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithFilter(
			"eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithFilterStringStartsWith()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetPriceListIdPriceListAccountsPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceListAccountsPage_getId();

		PriceListAccount priceListAccount1 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		PriceListAccount priceListAccount2 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		for (EntityField entityField : entityFields) {
			Page<PriceListAccount> page =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null,
					getFilterString(entityField, operator, priceListAccount1),
					Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(priceListAccount1),
				(List<PriceListAccount>)page.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithPagination()
		throws Exception {

		Long id = testGetPriceListIdPriceListAccountsPage_getId();

		Page<PriceListAccount> priceListAccountsPage =
			priceListAccountResource.getPriceListIdPriceListAccountsPage(
				id, null, null, null, null);

		int totalCount = GetterUtil.getInteger(
			priceListAccountsPage.getTotalCount());

		PriceListAccount priceListAccount1 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		PriceListAccount priceListAccount2 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		PriceListAccount priceListAccount3 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, randomPriceListAccount());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<PriceListAccount> page1 =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				priceListAccount1, (List<PriceListAccount>)page1.getItems());

			Page<PriceListAccount> page2 =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				priceListAccount2, (List<PriceListAccount>)page2.getItems());

			Page<PriceListAccount> page3 =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit),
					null);

			assertContains(
				priceListAccount3, (List<PriceListAccount>)page3.getItems());
		}
		else {
			Page<PriceListAccount> page1 =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null, Pagination.of(1, totalCount + 2), null);

			List<PriceListAccount> priceListAccounts1 =
				(List<PriceListAccount>)page1.getItems();

			Assert.assertEquals(
				priceListAccounts1.toString(), totalCount + 2,
				priceListAccounts1.size());

			Page<PriceListAccount> page2 =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<PriceListAccount> priceListAccounts2 =
				(List<PriceListAccount>)page2.getItems();

			Assert.assertEquals(
				priceListAccounts2.toString(), 1, priceListAccounts2.size());

			Page<PriceListAccount> page3 =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null, Pagination.of(1, (int)totalCount + 3),
					null);

			assertContains(
				priceListAccount1, (List<PriceListAccount>)page3.getItems());
			assertContains(
				priceListAccount2, (List<PriceListAccount>)page3.getItems());
			assertContains(
				priceListAccount3, (List<PriceListAccount>)page3.getItems());
		}
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithSortDateTime()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, priceListAccount1, priceListAccount2) -> {
				BeanTestUtil.setProperty(
					priceListAccount1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithSortDouble()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, priceListAccount1, priceListAccount2) -> {
				BeanTestUtil.setProperty(
					priceListAccount1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(
					priceListAccount2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithSortInteger()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, priceListAccount1, priceListAccount2) -> {
				BeanTestUtil.setProperty(
					priceListAccount1, entityField.getName(), 0);
				BeanTestUtil.setProperty(
					priceListAccount2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetPriceListIdPriceListAccountsPageWithSortString()
		throws Exception {

		testGetPriceListIdPriceListAccountsPageWithSort(
			EntityField.Type.STRING,
			(entityField, priceListAccount1, priceListAccount2) -> {
				Class<?> clazz = priceListAccount1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						priceListAccount1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						priceListAccount2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						priceListAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						priceListAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						priceListAccount1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						priceListAccount2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetPriceListIdPriceListAccountsPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer
				<EntityField, PriceListAccount, PriceListAccount, Exception>
					unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Long id = testGetPriceListIdPriceListAccountsPage_getId();

		PriceListAccount priceListAccount1 = randomPriceListAccount();
		PriceListAccount priceListAccount2 = randomPriceListAccount();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(
				entityField, priceListAccount1, priceListAccount2);
		}

		priceListAccount1 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, priceListAccount1);

		priceListAccount2 =
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				id, priceListAccount2);

		Page<PriceListAccount> page =
			priceListAccountResource.getPriceListIdPriceListAccountsPage(
				id, null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<PriceListAccount> ascPage =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":asc");

			assertContains(
				priceListAccount1, (List<PriceListAccount>)ascPage.getItems());
			assertContains(
				priceListAccount2, (List<PriceListAccount>)ascPage.getItems());

			Page<PriceListAccount> descPage =
				priceListAccountResource.getPriceListIdPriceListAccountsPage(
					id, null, null,
					Pagination.of(1, (int)page.getTotalCount() + 1),
					entityField.getName() + ":desc");

			assertContains(
				priceListAccount2, (List<PriceListAccount>)descPage.getItems());
			assertContains(
				priceListAccount1, (List<PriceListAccount>)descPage.getItems());
		}
	}

	protected PriceListAccount
			testGetPriceListIdPriceListAccountsPage_addPriceListAccount(
				Long id, PriceListAccount priceListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceListAccountsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceListIdPriceListAccountsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostPriceListByExternalReferenceCodePriceListAccount()
		throws Exception {

		PriceListAccount randomPriceListAccount = randomPriceListAccount();

		PriceListAccount postPriceListAccount =
			testPostPriceListByExternalReferenceCodePriceListAccount_addPriceListAccount(
				randomPriceListAccount);

		assertEquals(randomPriceListAccount, postPriceListAccount);
		assertValid(postPriceListAccount);
	}

	protected PriceListAccount
			testPostPriceListByExternalReferenceCodePriceListAccount_addPriceListAccount(
				PriceListAccount priceListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceListIdPriceListAccount() throws Exception {
		PriceListAccount randomPriceListAccount = randomPriceListAccount();

		PriceListAccount postPriceListAccount =
			testPostPriceListIdPriceListAccount_addPriceListAccount(
				randomPriceListAccount);

		assertEquals(randomPriceListAccount, postPriceListAccount);
		assertValid(postPriceListAccount);
	}

	protected PriceListAccount
			testPostPriceListIdPriceListAccount_addPriceListAccount(
				PriceListAccount priceListAccount)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		PriceListAccount priceListAccount1 =
			testBatchEngineDeleteImportTask_addPriceListAccount();

		testBatchEngineDeleteImportTask_deletePriceListAccount(
			200, null, priceListAccount1.getPriceListAccountId());
	}

	protected PriceListAccount
			testBatchEngineDeleteImportTask_addPriceListAccount()
		throws Exception {

		return testDeletePriceListAccount_addPriceListAccount();
	}

	protected void testBatchEngineDeleteImportTask_deletePriceListAccount(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.PriceListAccount",
				null, null, null, null,
				JSONUtil.putAll(
					JSONUtil.put(
						"externalReferenceCode", () -> externalReferenceCode
					).put(
						"priceListAccountId", () -> id
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

	protected PriceListAccount testGraphQLPriceListAccount_addPriceListAccount()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		PriceListAccount priceListAccount,
		List<PriceListAccount> priceListAccounts) {

		boolean contains = false;

		for (PriceListAccount item : priceListAccounts) {
			if (equals(priceListAccount, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			priceListAccounts + " does not contain " + priceListAccount,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		PriceListAccount priceListAccount1,
		PriceListAccount priceListAccount2) {

		Assert.assertTrue(
			priceListAccount1 + " does not equal " + priceListAccount2,
			equals(priceListAccount1, priceListAccount2));
	}

	protected void assertEquals(
		List<PriceListAccount> priceListAccounts1,
		List<PriceListAccount> priceListAccounts2) {

		Assert.assertEquals(
			priceListAccounts1.size(), priceListAccounts2.size());

		for (int i = 0; i < priceListAccounts1.size(); i++) {
			PriceListAccount priceListAccount1 = priceListAccounts1.get(i);
			PriceListAccount priceListAccount2 = priceListAccounts2.get(i);

			assertEquals(priceListAccount1, priceListAccount2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<PriceListAccount> priceListAccounts1,
		List<PriceListAccount> priceListAccounts2) {

		Assert.assertEquals(
			priceListAccounts1.size(), priceListAccounts2.size());

		for (PriceListAccount priceListAccount1 : priceListAccounts1) {
			boolean contains = false;

			for (PriceListAccount priceListAccount2 : priceListAccounts2) {
				if (equals(priceListAccount1, priceListAccount2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				priceListAccounts2 + " does not contain " + priceListAccount1,
				contains);
		}
	}

	protected void assertValid(PriceListAccount priceListAccount)
		throws Exception {

		boolean valid = true;

		if (priceListAccount.getPriceListAccountId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (priceListAccount.getAccount() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceListAccount.getAccountExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (priceListAccount.getAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (priceListAccount.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("order", additionalAssertFieldName)) {
				if (priceListAccount.getOrder() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListAccountId", additionalAssertFieldName)) {

				if (priceListAccount.getPriceListAccountId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (priceListAccount.getPriceListExternalReferenceCode() ==
						null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (priceListAccount.getPriceListId() == null) {
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

	protected void assertValid(Page<PriceListAccount> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<PriceListAccount> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<PriceListAccount> priceListAccounts =
			page.getItems();

		int size = priceListAccounts.size();

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

		graphQLFields.add(new GraphQLField("priceListAccountId"));

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						PriceListAccount.class)) {

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
		PriceListAccount priceListAccount1,
		PriceListAccount priceListAccount2) {

		if (priceListAccount1 == priceListAccount2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("account", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListAccount1.getAccount(),
						priceListAccount2.getAccount())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"accountExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceListAccount1.getAccountExternalReferenceCode(),
						priceListAccount2.getAccountExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("accountId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListAccount1.getAccountId(),
						priceListAccount2.getAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)priceListAccount1.getActions(),
						(Map)priceListAccount2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("order", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListAccount1.getOrder(),
						priceListAccount2.getOrder())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListAccountId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceListAccount1.getPriceListAccountId(),
						priceListAccount2.getPriceListAccountId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceListExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						priceListAccount1.getPriceListExternalReferenceCode(),
						priceListAccount2.
							getPriceListExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceListId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						priceListAccount1.getPriceListId(),
						priceListAccount2.getPriceListId())) {

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

		if (!(_priceListAccountResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_priceListAccountResource;

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
		PriceListAccount priceListAccount) {

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
			Object object = priceListAccount.getAccountExternalReferenceCode();

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

		if (entityFieldName.equals("order")) {
			sb.append(String.valueOf(priceListAccount.getOrder()));

			return sb.toString();
		}

		if (entityFieldName.equals("priceListAccountId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceListExternalReferenceCode")) {
			Object object =
				priceListAccount.getPriceListExternalReferenceCode();

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

		if (entityFieldName.equals("priceListId")) {
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

	protected PriceListAccount randomPriceListAccount() throws Exception {
		return new PriceListAccount() {
			{
				accountExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				accountId = RandomTestUtil.randomLong();
				order = RandomTestUtil.randomInt();
				priceListAccountId = RandomTestUtil.randomLong();
				priceListExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceListId = RandomTestUtil.randomLong();
			}
		};
	}

	protected PriceListAccount randomIrrelevantPriceListAccount()
		throws Exception {

		PriceListAccount randomIrrelevantPriceListAccount =
			randomPriceListAccount();

		return randomIrrelevantPriceListAccount;
	}

	protected PriceListAccount randomPatchPriceListAccount() throws Exception {
		return randomPriceListAccount();
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

	protected PriceListAccountResource priceListAccountResource;
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
		LogFactoryUtil.getLog(BasePriceListAccountResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.pricing.resource.v2_0.
		PriceListAccountResource _priceListAccountResource;

}