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
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.TierPriceResource;
import com.liferay.headless.commerce.admin.pricing.client.serdes.v2_0.TierPriceSerDes;
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
import com.liferay.portal.kernel.util.Time;
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
 * @author Zoltán Takács
 * @generated
 */
@Generated("")
public abstract class BaseTierPriceResourceTestCase {

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

		_tierPriceResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		tierPriceResource = TierPriceResource.builder(
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

		TierPrice tierPrice1 = randomTierPrice();

		String json = objectMapper.writeValueAsString(tierPrice1);

		TierPrice tierPrice2 = TierPriceSerDes.toDTO(json);

		Assert.assertTrue(equals(tierPrice1, tierPrice2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		TierPrice tierPrice = randomTierPrice();

		String json1 = objectMapper.writeValueAsString(tierPrice);
		String json2 = TierPriceSerDes.toJSON(tierPrice);

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

		TierPrice tierPrice = randomTierPrice();

		tierPrice.setExternalReferenceCode(regex);
		tierPrice.setPriceEntryExternalReferenceCode(regex);
		tierPrice.setPriceFormatted(regex);
		tierPrice.setUnitOfMeasureKey(regex);

		String json = TierPriceSerDes.toJSON(tierPrice);

		Assert.assertFalse(json.contains(regex));

		tierPrice = TierPriceSerDes.toDTO(json);

		Assert.assertEquals(regex, tierPrice.getExternalReferenceCode());
		Assert.assertEquals(
			regex, tierPrice.getPriceEntryExternalReferenceCode());
		Assert.assertEquals(regex, tierPrice.getPriceFormatted());
		Assert.assertEquals(regex, tierPrice.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteTierPrice() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TierPrice tierPrice = testDeleteTierPrice_addTierPrice();

		assertHttpResponseStatusCode(
			204,
			tierPriceResource.deleteTierPriceHttpResponse(tierPrice.getId()));

		assertHttpResponseStatusCode(
			404, tierPriceResource.getTierPriceHttpResponse(tierPrice.getId()));
		assertHttpResponseStatusCode(
			404, tierPriceResource.getTierPriceHttpResponse(0L));
	}

	protected TierPrice testDeleteTierPrice_addTierPrice() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteTierPrice() throws Exception {

		// No namespace

		TierPrice tierPrice1 = testGraphQLDeleteTierPrice_addTierPrice();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteTierPrice",
						new HashMap<String, Object>() {
							{
								put("id", tierPrice1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteTierPrice"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"tierPrice",
					new HashMap<String, Object>() {
						{
							put("id", tierPrice1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		TierPrice tierPrice2 = testGraphQLDeleteTierPrice_addTierPrice();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteTierPrice",
							new HashMap<String, Object>() {
								{
									put("id", tierPrice2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteTierPrice"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"tierPrice",
						new HashMap<String, Object>() {
							{
								put("id", tierPrice2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected TierPrice testGraphQLDeleteTierPrice_addTierPrice()
		throws Exception {

		return testGraphQLTierPrice_addTierPrice();
	}

	@Test
	public void testDeleteTierPriceBatch() throws Exception {
		TierPrice tierPrice1 = testDeleteTierPriceBatch_addTierPrice();

		testDeleteTierPriceBatch_deleteTierPrice(
			202, tierPrice1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice1.getId()));

		tierPrice1 = testDeleteTierPriceBatch_addTierPrice();

		testDeleteTierPriceBatch_deleteTierPrice(202, null, tierPrice1.getId());

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice1.getId()));

		tierPrice1 = testDeleteTierPriceBatch_addTierPrice();
		TierPrice tierPrice2 = testDeleteTierPriceBatch_addTierPrice();

		testDeleteTierPriceBatch_deleteTierPrice(
			202, tierPrice2.getExternalReferenceCode(), tierPrice1.getId());

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice1.getId()));
		assertHttpResponseStatusCode(
			200,
			tierPriceResource.getTierPriceHttpResponse(tierPrice2.getId()));

		testDeleteTierPriceBatch_deleteTierPrice(
			202, tierPrice2.getExternalReferenceCode(), tierPrice1.getId());

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice2.getId()));
	}

	protected TierPrice testDeleteTierPriceBatch_addTierPrice()
		throws Exception {

		return testDeleteTierPrice_addTierPrice();
	}

	protected void testDeleteTierPriceBatch_deleteTierPrice(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			tierPriceResource.deleteTierPriceBatchHttpResponse(
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
	public void testDeleteTierPriceByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		TierPrice tierPrice =
			testDeleteTierPriceByExternalReferenceCode_addTierPrice();

		assertHttpResponseStatusCode(
			204,
			tierPriceResource.
				deleteTierPriceByExternalReferenceCodeHttpResponse(
					tierPrice.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceByExternalReferenceCodeHttpResponse(
				tierPrice.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceByExternalReferenceCodeHttpResponse(
				"-"));
	}

	protected TierPrice
			testDeleteTierPriceByExternalReferenceCode_addTierPrice()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteTierPriceByExternalReferenceCode()
		throws Exception {

		// No namespace

		TierPrice tierPrice1 =
			testGraphQLDeleteTierPriceByExternalReferenceCode_addTierPrice();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteTierPriceByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										tierPrice1.getExternalReferenceCode() +
											"\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteTierPriceByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"tierPriceByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + tierPrice1.getExternalReferenceCode() +
									"\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminPricing_v2_0

		TierPrice tierPrice2 =
			testGraphQLDeleteTierPriceByExternalReferenceCode_addTierPrice();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"deleteTierPriceByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											tierPrice2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminPricing_v2_0",
				"Object/deleteTierPriceByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminPricing_v2_0",
					new GraphQLField(
						"tierPriceByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										tierPrice2.getExternalReferenceCode() +
											"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected TierPrice
			testGraphQLDeleteTierPriceByExternalReferenceCode_addTierPrice()
		throws Exception {

		return testGraphQLTierPrice_addTierPrice();
	}

	@Test
	public void testGetPriceEntryByExternalReferenceCodeTierPricesPage()
		throws Exception {

		String externalReferenceCode =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getIrrelevantExternalReferenceCode();

		Page<TierPrice> page =
			tierPriceResource.
				getPriceEntryByExternalReferenceCodeTierPricesPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			TierPrice irrelevantTierPrice =
				testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
					irrelevantExternalReferenceCode,
					randomIrrelevantTierPrice());

			page =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantTierPrice, (List<TierPrice>)page.getItems());
			assertValid(
				page,
				testGetPriceEntryByExternalReferenceCodeTierPricesPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		TierPrice tierPrice1 =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
				externalReferenceCode, randomTierPrice());

		TierPrice tierPrice2 =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
				externalReferenceCode, randomTierPrice());

		page =
			tierPriceResource.
				getPriceEntryByExternalReferenceCodeTierPricesPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(tierPrice1, (List<TierPrice>)page.getItems());
		assertContains(tierPrice2, (List<TierPrice>)page.getItems());
		assertValid(
			page,
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getExpectedActions(
				externalReferenceCode));

		tierPriceResource.deleteTierPrice(tierPrice1.getId());

		tierPriceResource.deleteTierPrice(tierPrice2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetPriceEntryByExternalReferenceCodeTierPricesPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getExternalReferenceCode();

		Page<TierPrice> tierPricesPage =
			tierPriceResource.
				getPriceEntryByExternalReferenceCodeTierPricesPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(tierPricesPage.getTotalCount());

		TierPrice tierPrice1 =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
				externalReferenceCode, randomTierPrice());

		TierPrice tierPrice2 =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
				externalReferenceCode, randomTierPrice());

		TierPrice tierPrice3 =
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
				externalReferenceCode, randomTierPrice());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TierPrice> page1 =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(tierPrice1, (List<TierPrice>)page1.getItems());

			Page<TierPrice> page2 =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(tierPrice2, (List<TierPrice>)page2.getItems());

			Page<TierPrice> page3 =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(tierPrice3, (List<TierPrice>)page3.getItems());
		}
		else {
			Page<TierPrice> page1 =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<TierPrice> tierPrices1 = (List<TierPrice>)page1.getItems();

			Assert.assertEquals(
				tierPrices1.toString(), totalCount + 2, tierPrices1.size());

			Page<TierPrice> page2 =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TierPrice> tierPrices2 = (List<TierPrice>)page2.getItems();

			Assert.assertEquals(tierPrices2.toString(), 1, tierPrices2.size());

			Page<TierPrice> page3 =
				tierPriceResource.
					getPriceEntryByExternalReferenceCodeTierPricesPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(tierPrice1, (List<TierPrice>)page3.getItems());
			assertContains(tierPrice2, (List<TierPrice>)page3.getItems());
			assertContains(tierPrice3, (List<TierPrice>)page3.getItems());
		}
	}

	protected TierPrice
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_addTierPrice(
				String externalReferenceCode, TierPrice tierPrice)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetPriceEntryByExternalReferenceCodeTierPricesPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetPriceEntryIdTierPricesPage() throws Exception {
		Long priceEntryId = testGetPriceEntryIdTierPricesPage_getPriceEntryId();
		Long irrelevantPriceEntryId =
			testGetPriceEntryIdTierPricesPage_getIrrelevantPriceEntryId();

		Page<TierPrice> page = tierPriceResource.getPriceEntryIdTierPricesPage(
			priceEntryId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantPriceEntryId != null) {
			TierPrice irrelevantTierPrice =
				testGetPriceEntryIdTierPricesPage_addTierPrice(
					irrelevantPriceEntryId, randomIrrelevantTierPrice());

			page = tierPriceResource.getPriceEntryIdTierPricesPage(
				irrelevantPriceEntryId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantTierPrice, (List<TierPrice>)page.getItems());
			assertValid(
				page,
				testGetPriceEntryIdTierPricesPage_getExpectedActions(
					irrelevantPriceEntryId));
		}

		TierPrice tierPrice1 = testGetPriceEntryIdTierPricesPage_addTierPrice(
			priceEntryId, randomTierPrice());

		TierPrice tierPrice2 = testGetPriceEntryIdTierPricesPage_addTierPrice(
			priceEntryId, randomTierPrice());

		page = tierPriceResource.getPriceEntryIdTierPricesPage(
			priceEntryId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(tierPrice1, (List<TierPrice>)page.getItems());
		assertContains(tierPrice2, (List<TierPrice>)page.getItems());
		assertValid(
			page,
			testGetPriceEntryIdTierPricesPage_getExpectedActions(priceEntryId));

		tierPriceResource.deleteTierPrice(tierPrice1.getId());

		tierPriceResource.deleteTierPrice(tierPrice2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetPriceEntryIdTierPricesPage_getExpectedActions(
				Long priceEntryId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		Map createBatchAction = new HashMap<>();
		createBatchAction.put("method", "POST");
		createBatchAction.put(
			"href",
			"http://localhost:8080/o/headless-commerce-admin-pricing/v2.0/price-entries/{priceEntryId}/tier-prices/batch".
				replace("{priceEntryId}", String.valueOf(priceEntryId)));

		expectedActions.put("createBatch", createBatchAction);

		return expectedActions;
	}

	@Test
	public void testGetPriceEntryIdTierPricesPageWithPagination()
		throws Exception {

		Long priceEntryId = testGetPriceEntryIdTierPricesPage_getPriceEntryId();

		Page<TierPrice> tierPricesPage =
			tierPriceResource.getPriceEntryIdTierPricesPage(priceEntryId, null);

		int totalCount = GetterUtil.getInteger(tierPricesPage.getTotalCount());

		TierPrice tierPrice1 = testGetPriceEntryIdTierPricesPage_addTierPrice(
			priceEntryId, randomTierPrice());

		TierPrice tierPrice2 = testGetPriceEntryIdTierPricesPage_addTierPrice(
			priceEntryId, randomTierPrice());

		TierPrice tierPrice3 = testGetPriceEntryIdTierPricesPage_addTierPrice(
			priceEntryId, randomTierPrice());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<TierPrice> page1 =
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(tierPrice1, (List<TierPrice>)page1.getItems());

			Page<TierPrice> page2 =
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(tierPrice2, (List<TierPrice>)page2.getItems());

			Page<TierPrice> page3 =
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(tierPrice3, (List<TierPrice>)page3.getItems());
		}
		else {
			Page<TierPrice> page1 =
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId, Pagination.of(1, totalCount + 2));

			List<TierPrice> tierPrices1 = (List<TierPrice>)page1.getItems();

			Assert.assertEquals(
				tierPrices1.toString(), totalCount + 2, tierPrices1.size());

			Page<TierPrice> page2 =
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<TierPrice> tierPrices2 = (List<TierPrice>)page2.getItems();

			Assert.assertEquals(tierPrices2.toString(), 1, tierPrices2.size());

			Page<TierPrice> page3 =
				tierPriceResource.getPriceEntryIdTierPricesPage(
					priceEntryId, Pagination.of(1, (int)totalCount + 3));

			assertContains(tierPrice1, (List<TierPrice>)page3.getItems());
			assertContains(tierPrice2, (List<TierPrice>)page3.getItems());
			assertContains(tierPrice3, (List<TierPrice>)page3.getItems());
		}
	}

	protected TierPrice testGetPriceEntryIdTierPricesPage_addTierPrice(
			Long priceEntryId, TierPrice tierPrice)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceEntryIdTierPricesPage_getPriceEntryId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetPriceEntryIdTierPricesPage_getIrrelevantPriceEntryId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetTierPrice() throws Exception {
		TierPrice postTierPrice = testGetTierPrice_addTierPrice();

		TierPrice getTierPrice = tierPriceResource.getTierPrice(
			postTierPrice.getId());

		assertEquals(postTierPrice, getTierPrice);
		assertValid(getTierPrice);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		TierPrice postTierPrice = testGetTierPrice_addTierPrice();

		TierPrice getTierPrice = tierPriceResource.getTierPrice(
			postTierPrice.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice"
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

		Object item = vulcanCRUDItemDelegate.getItem(postTierPrice.getId());

		assertEquals(getTierPrice, TierPriceSerDes.toDTO(item.toString()));
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

	protected TierPrice testGetTierPrice_addTierPrice() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTierPrice() throws Exception {
		TierPrice tierPrice = testGraphQLGetTierPrice_addTierPrice();

		// No namespace

		Assert.assertTrue(
			equals(
				tierPrice,
				TierPriceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"tierPrice",
								new HashMap<String, Object>() {
									{
										put("id", tierPrice.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/tierPrice"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				tierPrice,
				TierPriceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"tierPrice",
									new HashMap<String, Object>() {
										{
											put("id", tierPrice.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/tierPrice"))));
	}

	@Test
	public void testGraphQLGetTierPriceNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"tierPrice",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"tierPrice",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected TierPrice testGraphQLGetTierPrice_addTierPrice()
		throws Exception {

		return testGraphQLTierPrice_addTierPrice();
	}

	@Test
	public void testGetTierPriceByExternalReferenceCode() throws Exception {
		TierPrice postTierPrice =
			testGetTierPriceByExternalReferenceCode_addTierPrice();

		TierPrice getTierPrice =
			tierPriceResource.getTierPriceByExternalReferenceCode(
				postTierPrice.getExternalReferenceCode());

		assertEquals(postTierPrice, getTierPrice);
		assertValid(getTierPrice);
	}

	protected TierPrice testGetTierPriceByExternalReferenceCode_addTierPrice()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetTierPriceByExternalReferenceCode()
		throws Exception {

		TierPrice tierPrice =
			testGraphQLGetTierPriceByExternalReferenceCode_addTierPrice();

		// No namespace

		Assert.assertTrue(
			equals(
				tierPrice,
				TierPriceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"tierPriceByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												tierPrice.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/tierPriceByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertTrue(
			equals(
				tierPrice,
				TierPriceSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminPricing_v2_0",
								new GraphQLField(
									"tierPriceByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													tierPrice.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminPricing_v2_0",
						"Object/tierPriceByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetTierPriceByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"tierPriceByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminPricing_v2_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminPricing_v2_0",
						new GraphQLField(
							"tierPriceByExternalReferenceCode",
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

	protected TierPrice
			testGraphQLGetTierPriceByExternalReferenceCode_addTierPrice()
		throws Exception {

		return testGraphQLTierPrice_addTierPrice();
	}

	@Test
	public void testPatchTierPrice() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPatchTierPriceByExternalReferenceCode() throws Exception {
		Assert.assertTrue(false);
	}

	@Test
	public void testPostPriceEntryByExternalReferenceCodeTierPrice()
		throws Exception {

		TierPrice randomTierPrice = randomTierPrice();

		TierPrice postTierPrice =
			testPostPriceEntryByExternalReferenceCodeTierPrice_addTierPrice(
				randomTierPrice);

		assertEquals(randomTierPrice, postTierPrice);
		assertValid(postTierPrice);
	}

	protected TierPrice
			testPostPriceEntryByExternalReferenceCodeTierPrice_addTierPrice(
				TierPrice tierPrice)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostPriceEntryIdTierPrice() throws Exception {
		TierPrice randomTierPrice = randomTierPrice();

		TierPrice postTierPrice = testPostPriceEntryIdTierPrice_addTierPrice(
			randomTierPrice);

		assertEquals(randomTierPrice, postTierPrice);
		assertValid(postTierPrice);
	}

	protected TierPrice testPostPriceEntryIdTierPrice_addTierPrice(
			TierPrice tierPrice)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		TierPrice tierPrice1 = testBatchEngineDeleteImportTask_addTierPrice();

		testBatchEngineDeleteImportTask_deleteTierPrice(
			200, tierPrice1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice1.getId()));

		tierPrice1 = testBatchEngineDeleteImportTask_addTierPrice();

		testBatchEngineDeleteImportTask_deleteTierPrice(
			200, null, tierPrice1.getId());

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice1.getId()));

		tierPrice1 = testBatchEngineDeleteImportTask_addTierPrice();
		TierPrice tierPrice2 = testBatchEngineDeleteImportTask_addTierPrice();

		testBatchEngineDeleteImportTask_deleteTierPrice(
			200, tierPrice2.getExternalReferenceCode(), tierPrice1.getId());

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice1.getId()));
		assertHttpResponseStatusCode(
			200,
			tierPriceResource.getTierPriceHttpResponse(tierPrice2.getId()));

		testBatchEngineDeleteImportTask_deleteTierPrice(
			200, tierPrice2.getExternalReferenceCode(), tierPrice1.getId());

		assertHttpResponseStatusCode(
			404,
			tierPriceResource.getTierPriceHttpResponse(tierPrice2.getId()));
	}

	protected TierPrice testBatchEngineDeleteImportTask_addTierPrice()
		throws Exception {

		return testDeleteTierPrice_addTierPrice();
	}

	protected void testBatchEngineDeleteImportTask_deleteTierPrice(
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
				"com.liferay.headless.commerce.admin.pricing.dto.v2_0.TierPrice",
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

	protected TierPrice testGraphQLTierPrice_addTierPrice() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		TierPrice tierPrice, List<TierPrice> tierPrices) {

		boolean contains = false;

		for (TierPrice item : tierPrices) {
			if (equals(tierPrice, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			tierPrices + " does not contain " + tierPrice, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(TierPrice tierPrice1, TierPrice tierPrice2) {
		Assert.assertTrue(
			tierPrice1 + " does not equal " + tierPrice2,
			equals(tierPrice1, tierPrice2));
	}

	protected void assertEquals(
		List<TierPrice> tierPrices1, List<TierPrice> tierPrices2) {

		Assert.assertEquals(tierPrices1.size(), tierPrices2.size());

		for (int i = 0; i < tierPrices1.size(); i++) {
			TierPrice tierPrice1 = tierPrices1.get(i);
			TierPrice tierPrice2 = tierPrices2.get(i);

			assertEquals(tierPrice1, tierPrice2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<TierPrice> tierPrices1, List<TierPrice> tierPrices2) {

		Assert.assertEquals(tierPrices1.size(), tierPrices2.size());

		for (TierPrice tierPrice1 : tierPrices1) {
			boolean contains = false;

			for (TierPrice tierPrice2 : tierPrices2) {
				if (equals(tierPrice1, tierPrice2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				tierPrices2 + " does not contain " + tierPrice1, contains);
		}
	}

	protected void assertValid(TierPrice tierPrice) throws Exception {
		boolean valid = true;

		if (tierPrice.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (tierPrice.getActions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (tierPrice.getActive() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (tierPrice.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"discountDiscovery", additionalAssertFieldName)) {

				if (tierPrice.getDiscountDiscovery() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel1", additionalAssertFieldName)) {
				if (tierPrice.getDiscountLevel1() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel2", additionalAssertFieldName)) {
				if (tierPrice.getDiscountLevel2() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel3", additionalAssertFieldName)) {
				if (tierPrice.getDiscountLevel3() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discountLevel4", additionalAssertFieldName)) {
				if (tierPrice.getDiscountLevel4() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (tierPrice.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (tierPrice.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (tierPrice.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("minimumQuantity", additionalAssertFieldName)) {
				if (tierPrice.getMinimumQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (tierPrice.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (tierPrice.getPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"priceEntryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (tierPrice.getPriceEntryExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceEntryId", additionalAssertFieldName)) {
				if (tierPrice.getPriceEntryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceFormatted", additionalAssertFieldName)) {
				if (tierPrice.getPriceFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (tierPrice.getUnitOfMeasureKey() == null) {
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

	protected void assertValid(Page<TierPrice> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<TierPrice> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<TierPrice> tierPrices = page.getItems();

		int size = tierPrices.size();

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
					com.liferay.headless.commerce.admin.pricing.dto.v2_0.
						TierPrice.class)) {

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

	protected boolean equals(TierPrice tierPrice1, TierPrice tierPrice2) {
		if (tierPrice1 == tierPrice2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("actions", additionalAssertFieldName)) {
				if (!equals(
						(Map)tierPrice1.getActions(),
						(Map)tierPrice2.getActions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("active", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getActive(), tierPrice2.getActive())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!equals(
						(Map)tierPrice1.getCustomFields(),
						(Map)tierPrice2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"discountDiscovery", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						tierPrice1.getDiscountDiscovery(),
						tierPrice2.getDiscountDiscovery())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel1", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getDiscountLevel1(),
						tierPrice2.getDiscountLevel1())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel2", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getDiscountLevel2(),
						tierPrice2.getDiscountLevel2())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel3", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getDiscountLevel3(),
						tierPrice2.getDiscountLevel3())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discountLevel4", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getDiscountLevel4(),
						tierPrice2.getDiscountLevel4())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getDisplayDate(),
						tierPrice2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getExpirationDate(),
						tierPrice2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						tierPrice1.getExternalReferenceCode(),
						tierPrice2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getId(), tierPrice2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("minimumQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getMinimumQuantity(),
						tierPrice2.getMinimumQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getNeverExpire(),
						tierPrice2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getPrice(), tierPrice2.getPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"priceEntryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						tierPrice1.getPriceEntryExternalReferenceCode(),
						tierPrice2.getPriceEntryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceEntryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getPriceEntryId(),
						tierPrice2.getPriceEntryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceFormatted", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getPriceFormatted(),
						tierPrice2.getPriceFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						tierPrice1.getUnitOfMeasureKey(),
						tierPrice2.getUnitOfMeasureKey())) {

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

		if (!(_tierPriceResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_tierPriceResource;

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
		EntityField entityField, String operator, TierPrice tierPrice) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("actions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("active")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountDiscovery")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel1")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel2")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel3")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discountLevel4")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = tierPrice.getDisplayDate();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(tierPrice.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = tierPrice.getExpirationDate();

				sb = new StringBundler();

				sb.append("(");
				sb.append(entityFieldName);
				sb.append(" gt ");
				sb.append(_format.format(date.getTime() - (2 * Time.SECOND)));
				sb.append(" and ");
				sb.append(entityFieldName);
				sb.append(" lt ");
				sb.append(_format.format(date.getTime() + (2 * Time.SECOND)));
				sb.append(")");
			}
			else {
				sb.append(entityFieldName);

				sb.append(" ");
				sb.append(operator);
				sb.append(" ");

				sb.append(_format.format(tierPrice.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = tierPrice.getExternalReferenceCode();

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

		if (entityFieldName.equals("minimumQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			sb.append(String.valueOf(tierPrice.getPrice()));

			return sb.toString();
		}

		if (entityFieldName.equals("priceEntryExternalReferenceCode")) {
			Object object = tierPrice.getPriceEntryExternalReferenceCode();

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

		if (entityFieldName.equals("priceEntryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priceFormatted")) {
			Object object = tierPrice.getPriceFormatted();

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

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = tierPrice.getUnitOfMeasureKey();

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

	protected TierPrice randomTierPrice() throws Exception {
		return new TierPrice() {
			{
				active = RandomTestUtil.randomBoolean();
				discountDiscovery = RandomTestUtil.randomBoolean();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				neverExpire = RandomTestUtil.randomBoolean();
				price = RandomTestUtil.randomDouble();
				priceEntryExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priceEntryId = RandomTestUtil.randomLong();
				priceFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	protected TierPrice randomIrrelevantTierPrice() throws Exception {
		TierPrice randomIrrelevantTierPrice = randomTierPrice();

		return randomIrrelevantTierPrice;
	}

	protected TierPrice randomPatchTierPrice() throws Exception {
		return randomTierPrice();
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

	protected TierPriceResource tierPriceResource;
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
		LogFactoryUtil.getLog(BaseTierPriceResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.pricing.resource.v2_0.
			TierPriceResource _tierPriceResource;

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