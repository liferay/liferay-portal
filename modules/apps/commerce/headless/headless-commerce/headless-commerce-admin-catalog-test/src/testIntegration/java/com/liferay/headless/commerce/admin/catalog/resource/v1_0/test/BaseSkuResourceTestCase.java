/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.SkuSerDes;
import com.liferay.oauth2.provider.scope.ScopeChecker;
import com.liferay.petra.function.UnsafeTriConsumer;
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
import com.liferay.portal.search.test.rule.SearchTestRule;
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
public abstract class BaseSkuResourceTestCase {

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

		_skuResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		skuResource = SkuResource.builder(
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

		Sku sku1 = randomSku();

		String json = objectMapper.writeValueAsString(sku1);

		Sku sku2 = SkuSerDes.toDTO(json);

		Assert.assertTrue(equals(sku1, sku2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		Sku sku = randomSku();

		String json1 = objectMapper.writeValueAsString(sku);
		String json2 = SkuSerDes.toJSON(sku);

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

		Sku sku = randomSku();

		sku.setExternalReferenceCode(regex);
		sku.setGtin(regex);
		sku.setManufacturerPartNumber(regex);
		sku.setReplacementSkuExternalReferenceCode(regex);
		sku.setSku(regex);
		sku.setUnitOfMeasureKey(regex);
		sku.setUnitOfMeasureSkuId(regex);
		sku.setUnspsc(regex);

		String json = SkuSerDes.toJSON(sku);

		Assert.assertFalse(json.contains(regex));

		sku = SkuSerDes.toDTO(json);

		Assert.assertEquals(regex, sku.getExternalReferenceCode());
		Assert.assertEquals(regex, sku.getGtin());
		Assert.assertEquals(regex, sku.getManufacturerPartNumber());
		Assert.assertEquals(
			regex, sku.getReplacementSkuExternalReferenceCode());
		Assert.assertEquals(regex, sku.getSku());
		Assert.assertEquals(regex, sku.getUnitOfMeasureKey());
		Assert.assertEquals(regex, sku.getUnitOfMeasureSkuId());
		Assert.assertEquals(regex, sku.getUnspsc());
	}

	@Test
	public void testDeleteSku() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Sku sku = testDeleteSku_addSku();

		assertHttpResponseStatusCode(
			204, skuResource.deleteSkuHttpResponse(sku.getId()));

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku.getId()));
		assertHttpResponseStatusCode(404, skuResource.getSkuHttpResponse(0L));
	}

	protected Sku testDeleteSku_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSku() throws Exception {

		// No namespace

		Sku sku1 = testGraphQLDeleteSku_addSku();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSku",
						new HashMap<String, Object>() {
							{
								put("id", sku1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteSku"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"sku",
					new HashMap<String, Object>() {
						{
							put("id", sku1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Sku sku2 = testGraphQLDeleteSku_addSku();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteSku",
							new HashMap<String, Object>() {
								{
									put("id", sku2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteSku"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"sku",
						new HashMap<String, Object>() {
							{
								put("id", sku2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Sku testGraphQLDeleteSku_addSku() throws Exception {
		return testGraphQLSku_addSku();
	}

	@Test
	public void testDeleteSkuBatch() throws Exception {
		Sku sku1 = testDeleteSkuBatch_addSku();

		testDeleteSkuBatch_deleteSku(
			202, sku1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku1.getId()));

		sku1 = testDeleteSkuBatch_addSku();

		testDeleteSkuBatch_deleteSku(202, null, sku1.getId());

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku1.getId()));

		sku1 = testDeleteSkuBatch_addSku();
		Sku sku2 = testDeleteSkuBatch_addSku();

		testDeleteSkuBatch_deleteSku(
			202, sku2.getExternalReferenceCode(), sku1.getId());

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku1.getId()));
		assertHttpResponseStatusCode(
			200, skuResource.getSkuHttpResponse(sku2.getId()));

		testDeleteSkuBatch_deleteSku(
			202, sku2.getExternalReferenceCode(), sku1.getId());

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku2.getId()));
	}

	protected Sku testDeleteSkuBatch_addSku() throws Exception {
		return testDeleteSku_addSku();
	}

	protected void testDeleteSkuBatch_deleteSku(
			int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			skuResource.deleteSkuBatchHttpResponse(
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
	public void testDeleteSkuByExternalReferenceCode() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		Sku sku = testDeleteSkuByExternalReferenceCode_addSku();

		assertHttpResponseStatusCode(
			204,
			skuResource.deleteSkuByExternalReferenceCodeHttpResponse(
				sku.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			skuResource.getSkuByExternalReferenceCodeHttpResponse(
				sku.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404, skuResource.getSkuByExternalReferenceCodeHttpResponse("-"));
	}

	protected Sku testDeleteSkuByExternalReferenceCode_addSku()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteSkuByExternalReferenceCode() throws Exception {

		// No namespace

		Sku sku1 = testGraphQLDeleteSkuByExternalReferenceCode_addSku();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteSkuByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + sku1.getExternalReferenceCode() +
										"\"");
							}
						})),
				"JSONObject/data", "Object/deleteSkuByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"skuByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" + sku1.getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Sku sku2 = testGraphQLDeleteSkuByExternalReferenceCode_addSku();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteSkuByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" + sku2.getExternalReferenceCode() +
											"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteSkuByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"skuByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" + sku2.getExternalReferenceCode() +
										"\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected Sku testGraphQLDeleteSkuByExternalReferenceCode_addSku()
		throws Exception {

		return testGraphQLSku_addSku();
	}

	@Test
	public void testGetProductByExternalReferenceCodeSkusPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeSkusPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductByExternalReferenceCodeSkusPage_getIrrelevantExternalReferenceCode();

		Page<Sku> page = skuResource.getProductByExternalReferenceCodeSkusPage(
			externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			Sku irrelevantSku =
				testGetProductByExternalReferenceCodeSkusPage_addSku(
					irrelevantExternalReferenceCode, randomIrrelevantSku());

			page = skuResource.getProductByExternalReferenceCodeSkusPage(
				irrelevantExternalReferenceCode,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSku, (List<Sku>)page.getItems());
			assertValid(
				page,
				testGetProductByExternalReferenceCodeSkusPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		Sku sku1 = testGetProductByExternalReferenceCodeSkusPage_addSku(
			externalReferenceCode, randomSku());

		Sku sku2 = testGetProductByExternalReferenceCodeSkusPage_addSku(
			externalReferenceCode, randomSku());

		page = skuResource.getProductByExternalReferenceCodeSkusPage(
			externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sku1, (List<Sku>)page.getItems());
		assertContains(sku2, (List<Sku>)page.getItems());
		assertValid(
			page,
			testGetProductByExternalReferenceCodeSkusPage_getExpectedActions(
				externalReferenceCode));

		skuResource.deleteSku(sku1.getId());

		skuResource.deleteSku(sku2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductByExternalReferenceCodeSkusPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductByExternalReferenceCodeSkusPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeSkusPage_getExternalReferenceCode();

		Page<Sku> skusPage =
			skuResource.getProductByExternalReferenceCodeSkusPage(
				externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(skusPage.getTotalCount());

		Sku sku1 = testGetProductByExternalReferenceCodeSkusPage_addSku(
			externalReferenceCode, randomSku());

		Sku sku2 = testGetProductByExternalReferenceCodeSkusPage_addSku(
			externalReferenceCode, randomSku());

		Sku sku3 = testGetProductByExternalReferenceCodeSkusPage_addSku(
			externalReferenceCode, randomSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Sku> page1 =
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sku1, (List<Sku>)page1.getItems());

			Page<Sku> page2 =
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(sku2, (List<Sku>)page2.getItems());

			Page<Sku> page3 =
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(sku3, (List<Sku>)page3.getItems());
		}
		else {
			Page<Sku> page1 =
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode, Pagination.of(1, totalCount + 2));

			List<Sku> skus1 = (List<Sku>)page1.getItems();

			Assert.assertEquals(skus1.toString(), totalCount + 2, skus1.size());

			Page<Sku> page2 =
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Sku> skus2 = (List<Sku>)page2.getItems();

			Assert.assertEquals(skus2.toString(), 1, skus2.size());

			Page<Sku> page3 =
				skuResource.getProductByExternalReferenceCodeSkusPage(
					externalReferenceCode,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(sku1, (List<Sku>)page3.getItems());
			assertContains(sku2, (List<Sku>)page3.getItems());
			assertContains(sku3, (List<Sku>)page3.getItems());
		}
	}

	protected Sku testGetProductByExternalReferenceCodeSkusPage_addSku(
			String externalReferenceCode, Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeSkusPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeSkusPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdSkusPage() throws Exception {
		Long id = testGetProductIdSkusPage_getId();
		Long irrelevantId = testGetProductIdSkusPage_getIrrelevantId();

		Page<Sku> page = skuResource.getProductIdSkusPage(
			id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			Sku irrelevantSku = testGetProductIdSkusPage_addSku(
				irrelevantId, randomIrrelevantSku());

			page = skuResource.getProductIdSkusPage(
				irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSku, (List<Sku>)page.getItems());
			assertValid(
				page,
				testGetProductIdSkusPage_getExpectedActions(irrelevantId));
		}

		Sku sku1 = testGetProductIdSkusPage_addSku(id, randomSku());

		Sku sku2 = testGetProductIdSkusPage_addSku(id, randomSku());

		page = skuResource.getProductIdSkusPage(id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sku1, (List<Sku>)page.getItems());
		assertContains(sku2, (List<Sku>)page.getItems());
		assertValid(page, testGetProductIdSkusPage_getExpectedActions(id));

		skuResource.deleteSku(sku1.getId());

		skuResource.deleteSku(sku2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductIdSkusPage_getExpectedActions(Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductIdSkusPageWithPagination() throws Exception {
		Long id = testGetProductIdSkusPage_getId();

		Page<Sku> skusPage = skuResource.getProductIdSkusPage(id, null);

		int totalCount = GetterUtil.getInteger(skusPage.getTotalCount());

		Sku sku1 = testGetProductIdSkusPage_addSku(id, randomSku());

		Sku sku2 = testGetProductIdSkusPage_addSku(id, randomSku());

		Sku sku3 = testGetProductIdSkusPage_addSku(id, randomSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Sku> page1 = skuResource.getProductIdSkusPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sku1, (List<Sku>)page1.getItems());

			Page<Sku> page2 = skuResource.getProductIdSkusPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(sku2, (List<Sku>)page2.getItems());

			Page<Sku> page3 = skuResource.getProductIdSkusPage(
				id,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(sku3, (List<Sku>)page3.getItems());
		}
		else {
			Page<Sku> page1 = skuResource.getProductIdSkusPage(
				id, Pagination.of(1, totalCount + 2));

			List<Sku> skus1 = (List<Sku>)page1.getItems();

			Assert.assertEquals(skus1.toString(), totalCount + 2, skus1.size());

			Page<Sku> page2 = skuResource.getProductIdSkusPage(
				id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Sku> skus2 = (List<Sku>)page2.getItems();

			Assert.assertEquals(skus2.toString(), 1, skus2.size());

			Page<Sku> page3 = skuResource.getProductIdSkusPage(
				id, Pagination.of(1, (int)totalCount + 3));

			assertContains(sku1, (List<Sku>)page3.getItems());
			assertContains(sku2, (List<Sku>)page3.getItems());
			assertContains(sku3, (List<Sku>)page3.getItems());
		}
	}

	protected Sku testGetProductIdSkusPage_addSku(Long id, Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdSkusPage_getId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdSkusPage_getIrrelevantId() throws Exception {
		return null;
	}

	@Test
	public void testGetSku() throws Exception {
		Sku postSku = testGetSku_addSku();

		Sku getSku = skuResource.getSku(postSku.getId());

		assertEquals(postSku, getSku);
		assertValid(getSku);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		Sku postSku = testGetSku_addSku();

		Sku getSku = skuResource.getSku(postSku.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku"
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

		Object item = vulcanCRUDItemDelegate.getItem(postSku.getId());

		assertEquals(getSku, SkuSerDes.toDTO(item.toString()));
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

	protected Sku testGetSku_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSku() throws Exception {
		Sku sku = testGraphQLGetSku_addSku();

		// No namespace

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"sku",
								new HashMap<String, Object>() {
									{
										put("id", sku.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/sku"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"sku",
									new HashMap<String, Object>() {
										{
											put("id", sku.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/sku"))));
	}

	@Test
	public void testGraphQLGetSkuNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"sku",
						new HashMap<String, Object>() {
							{
								put("id", irrelevantId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"sku",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Sku testGraphQLGetSku_addSku() throws Exception {
		return testGraphQLSku_addSku();
	}

	@Test
	public void testGetSkuByExternalReferenceCode() throws Exception {
		Sku postSku = testGetSkuByExternalReferenceCode_addSku();

		Sku getSku = skuResource.getSkuByExternalReferenceCode(
			postSku.getExternalReferenceCode());

		assertEquals(postSku, getSku);
		assertValid(getSku);
	}

	protected Sku testGetSkuByExternalReferenceCode_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetSkuByExternalReferenceCode() throws Exception {
		Sku sku = testGraphQLGetSkuByExternalReferenceCode_addSku();

		// No namespace

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"skuByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												sku.getExternalReferenceCode() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/skuByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"skuByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													sku.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/skuByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetSkuByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"skuByExternalReferenceCode",
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

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"skuByExternalReferenceCode",
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

	protected Sku testGraphQLGetSkuByExternalReferenceCode_addSku()
		throws Exception {

		return testGraphQLSku_addSku();
	}

	@Test
	public void testGetSkusPage() throws Exception {
		Page<Sku> page = skuResource.getSkusPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Sku sku1 = testGetSkusPage_addSku(randomSku());

		Sku sku2 = testGetSkusPage_addSku(randomSku());

		page = skuResource.getSkusPage(null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sku1, (List<Sku>)page.getItems());
		assertContains(sku2, (List<Sku>)page.getItems());
		assertValid(page, testGetSkusPage_getExpectedActions());

		skuResource.deleteSku(sku1.getId());

		skuResource.deleteSku(sku2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetSkusPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetSkusPageWithFilterDateTimeEquals() throws Exception {
		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Sku sku1 = randomSku();

		sku1 = testGetSkusPage_addSku(sku1);

		for (EntityField entityField : entityFields) {
			Page<Sku> page = skuResource.getSkusPage(
				null, getFilterString(entityField, "between", sku1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sku1), (List<Sku>)page.getItems());
		}
	}

	@Test
	public void testGetSkusPageWithFilterDoubleEquals() throws Exception {
		testGetSkusPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetSkusPageWithFilterStringContains() throws Exception {
		testGetSkusPageWithFilter("contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetSkusPageWithFilterStringEquals() throws Exception {
		testGetSkusPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetSkusPageWithFilterStringStartsWith() throws Exception {
		testGetSkusPageWithFilter("startswith", EntityField.Type.STRING);
	}

	protected void testGetSkusPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Sku sku1 = testGetSkusPage_addSku(randomSku());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Sku sku2 = testGetSkusPage_addSku(randomSku());

		for (EntityField entityField : entityFields) {
			Page<Sku> page = skuResource.getSkusPage(
				null, getFilterString(entityField, operator, sku1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sku1), (List<Sku>)page.getItems());
		}
	}

	@Test
	public void testGetSkusPageWithPagination() throws Exception {
		Page<Sku> skusPage = skuResource.getSkusPage(null, null, null, null);

		int totalCount = GetterUtil.getInteger(skusPage.getTotalCount());

		Sku sku1 = testGetSkusPage_addSku(randomSku());

		Sku sku2 = testGetSkusPage_addSku(randomSku());

		Sku sku3 = testGetSkusPage_addSku(randomSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Sku> page1 = skuResource.getSkusPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sku1, (List<Sku>)page1.getItems());

			Page<Sku> page2 = skuResource.getSkusPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(sku2, (List<Sku>)page2.getItems());

			Page<Sku> page3 = skuResource.getSkusPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(sku3, (List<Sku>)page3.getItems());
		}
		else {
			Page<Sku> page1 = skuResource.getSkusPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Sku> skus1 = (List<Sku>)page1.getItems();

			Assert.assertEquals(skus1.toString(), totalCount + 2, skus1.size());

			Page<Sku> page2 = skuResource.getSkusPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Sku> skus2 = (List<Sku>)page2.getItems();

			Assert.assertEquals(skus2.toString(), 1, skus2.size());

			Page<Sku> page3 = skuResource.getSkusPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(sku1, (List<Sku>)page3.getItems());
			assertContains(sku2, (List<Sku>)page3.getItems());
			assertContains(sku3, (List<Sku>)page3.getItems());
		}
	}

	@Test
	public void testGetSkusPageWithSortDateTime() throws Exception {
		testGetSkusPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sku1, sku2) -> {
				BeanTestUtil.setProperty(
					sku1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetSkusPageWithSortDouble() throws Exception {
		testGetSkusPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sku1, sku2) -> {
				BeanTestUtil.setProperty(sku1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(sku2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetSkusPageWithSortInteger() throws Exception {
		testGetSkusPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sku1, sku2) -> {
				BeanTestUtil.setProperty(sku1, entityField.getName(), 0);
				BeanTestUtil.setProperty(sku2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetSkusPageWithSortString() throws Exception {
		testGetSkusPageWithSort(
			EntityField.Type.STRING,
			(entityField, sku1, sku2) -> {
				Class<?> clazz = sku1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sku1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sku2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sku1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sku2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sku1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sku2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetSkusPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Sku, Sku, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Sku sku1 = randomSku();
		Sku sku2 = randomSku();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sku1, sku2);
		}

		sku1 = testGetSkusPage_addSku(sku1);

		sku2 = testGetSkusPage_addSku(sku2);

		Page<Sku> page = skuResource.getSkusPage(null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Sku> ascPage = skuResource.getSkusPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(sku1, (List<Sku>)ascPage.getItems());
			assertContains(sku2, (List<Sku>)ascPage.getItems());

			Page<Sku> descPage = skuResource.getSkusPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(sku2, (List<Sku>)descPage.getItems());
			assertContains(sku1, (List<Sku>)descPage.getItems());
		}
	}

	protected Sku testGetSkusPage_addSku(Sku sku) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGetUnitOfMeasureSkusPage() throws Exception {
		Page<Sku> page = skuResource.getUnitOfMeasureSkusPage(
			null, null, Pagination.of(1, 10), null);

		long totalCount = page.getTotalCount();

		Sku sku1 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		Sku sku2 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		page = skuResource.getUnitOfMeasureSkusPage(
			null, null, Pagination.of(1, 10), null);

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sku1, (List<Sku>)page.getItems());
		assertContains(sku2, (List<Sku>)page.getItems());
		assertValid(page, testGetUnitOfMeasureSkusPage_getExpectedActions());

		skuResource.deleteSku(sku1.getId());

		skuResource.deleteSku(sku2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetUnitOfMeasureSkusPage_getExpectedActions()
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithFilterDateTimeEquals()
		throws Exception {

		List<EntityField> entityFields = getEntityFields(
			EntityField.Type.DATE_TIME);

		if (entityFields.isEmpty()) {
			return;
		}

		Sku sku1 = randomSku();

		sku1 = testGetUnitOfMeasureSkusPage_addSku(sku1);

		for (EntityField entityField : entityFields) {
			Page<Sku> page = skuResource.getUnitOfMeasureSkusPage(
				null, getFilterString(entityField, "between", sku1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sku1), (List<Sku>)page.getItems());
		}
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithFilterDoubleEquals()
		throws Exception {

		testGetUnitOfMeasureSkusPageWithFilter("eq", EntityField.Type.DOUBLE);
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithFilterStringContains()
		throws Exception {

		testGetUnitOfMeasureSkusPageWithFilter(
			"contains", EntityField.Type.STRING);
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithFilterStringEquals()
		throws Exception {

		testGetUnitOfMeasureSkusPageWithFilter("eq", EntityField.Type.STRING);
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithFilterStringStartsWith()
		throws Exception {

		testGetUnitOfMeasureSkusPageWithFilter(
			"startswith", EntityField.Type.STRING);
	}

	protected void testGetUnitOfMeasureSkusPageWithFilter(
			String operator, EntityField.Type type)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Sku sku1 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Sku sku2 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		for (EntityField entityField : entityFields) {
			Page<Sku> page = skuResource.getUnitOfMeasureSkusPage(
				null, getFilterString(entityField, operator, sku1),
				Pagination.of(1, 2), null);

			assertEquals(
				Collections.singletonList(sku1), (List<Sku>)page.getItems());
		}
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithPagination() throws Exception {
		Page<Sku> skusPage = skuResource.getUnitOfMeasureSkusPage(
			null, null, null, null);

		int totalCount = GetterUtil.getInteger(skusPage.getTotalCount());

		Sku sku1 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		Sku sku2 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		Sku sku3 = testGetUnitOfMeasureSkusPage_addSku(randomSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Sku> page1 = skuResource.getUnitOfMeasureSkusPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sku1, (List<Sku>)page1.getItems());

			Page<Sku> page2 = skuResource.getUnitOfMeasureSkusPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(sku2, (List<Sku>)page2.getItems());

			Page<Sku> page3 = skuResource.getUnitOfMeasureSkusPage(
				null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit),
				null);

			assertContains(sku3, (List<Sku>)page3.getItems());
		}
		else {
			Page<Sku> page1 = skuResource.getUnitOfMeasureSkusPage(
				null, null, Pagination.of(1, totalCount + 2), null);

			List<Sku> skus1 = (List<Sku>)page1.getItems();

			Assert.assertEquals(skus1.toString(), totalCount + 2, skus1.size());

			Page<Sku> page2 = skuResource.getUnitOfMeasureSkusPage(
				null, null, Pagination.of(2, totalCount + 2), null);

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Sku> skus2 = (List<Sku>)page2.getItems();

			Assert.assertEquals(skus2.toString(), 1, skus2.size());

			Page<Sku> page3 = skuResource.getUnitOfMeasureSkusPage(
				null, null, Pagination.of(1, (int)totalCount + 3), null);

			assertContains(sku1, (List<Sku>)page3.getItems());
			assertContains(sku2, (List<Sku>)page3.getItems());
			assertContains(sku3, (List<Sku>)page3.getItems());
		}
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithSortDateTime()
		throws Exception {

		testGetUnitOfMeasureSkusPageWithSort(
			EntityField.Type.DATE_TIME,
			(entityField, sku1, sku2) -> {
				BeanTestUtil.setProperty(
					sku1, entityField.getName(),
					new Date(System.currentTimeMillis() - (2 * Time.MINUTE)));
			});
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithSortDouble() throws Exception {
		testGetUnitOfMeasureSkusPageWithSort(
			EntityField.Type.DOUBLE,
			(entityField, sku1, sku2) -> {
				BeanTestUtil.setProperty(sku1, entityField.getName(), 0.1);
				BeanTestUtil.setProperty(sku2, entityField.getName(), 0.5);
			});
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithSortInteger() throws Exception {
		testGetUnitOfMeasureSkusPageWithSort(
			EntityField.Type.INTEGER,
			(entityField, sku1, sku2) -> {
				BeanTestUtil.setProperty(sku1, entityField.getName(), 0);
				BeanTestUtil.setProperty(sku2, entityField.getName(), 1);
			});
	}

	@Test
	public void testGetUnitOfMeasureSkusPageWithSortString() throws Exception {
		testGetUnitOfMeasureSkusPageWithSort(
			EntityField.Type.STRING,
			(entityField, sku1, sku2) -> {
				Class<?> clazz = sku1.getClass();

				String entityFieldName = entityField.getName();

				Method method = clazz.getMethod(
					"get" + StringUtil.upperCaseFirstLetter(entityFieldName));

				Class<?> returnType = method.getReturnType();

				if (returnType.isAssignableFrom(Map.class)) {
					BeanTestUtil.setProperty(
						sku1, entityFieldName,
						Collections.singletonMap("Aaa", "Aaa"));
					BeanTestUtil.setProperty(
						sku2, entityFieldName,
						Collections.singletonMap("Bbb", "Bbb"));
				}
				else if (entityFieldName.contains("email")) {
					BeanTestUtil.setProperty(
						sku1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
					BeanTestUtil.setProperty(
						sku2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()) +
									"@liferay.com");
				}
				else {
					BeanTestUtil.setProperty(
						sku1, entityFieldName,
						"aaa" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
					BeanTestUtil.setProperty(
						sku2, entityFieldName,
						"bbb" +
							StringUtil.toLowerCase(
								RandomTestUtil.randomString()));
				}
			});
	}

	protected void testGetUnitOfMeasureSkusPageWithSort(
			EntityField.Type type,
			UnsafeTriConsumer<EntityField, Sku, Sku, Exception>
				unsafeTriConsumer)
		throws Exception {

		List<EntityField> entityFields = getEntityFields(type);

		if (entityFields.isEmpty()) {
			return;
		}

		Sku sku1 = randomSku();
		Sku sku2 = randomSku();

		for (EntityField entityField : entityFields) {
			unsafeTriConsumer.accept(entityField, sku1, sku2);
		}

		sku1 = testGetUnitOfMeasureSkusPage_addSku(sku1);

		sku2 = testGetUnitOfMeasureSkusPage_addSku(sku2);

		Page<Sku> page = skuResource.getUnitOfMeasureSkusPage(
			null, null, null, null);

		for (EntityField entityField : entityFields) {
			Page<Sku> ascPage = skuResource.getUnitOfMeasureSkusPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":asc");

			assertContains(sku1, (List<Sku>)ascPage.getItems());
			assertContains(sku2, (List<Sku>)ascPage.getItems());

			Page<Sku> descPage = skuResource.getUnitOfMeasureSkusPage(
				null, null, Pagination.of(1, (int)page.getTotalCount() + 1),
				entityField.getName() + ":desc");

			assertContains(sku2, (List<Sku>)descPage.getItems());
			assertContains(sku1, (List<Sku>)descPage.getItems());
		}
	}

	protected Sku testGetUnitOfMeasureSkusPage_addSku(Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchSku() throws Exception {
		Sku postSku = testPatchSku_addSku();

		Sku randomPatchSku = randomPatchSku();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Sku patchSku = skuResource.patchSku(postSku.getId(), randomPatchSku);

		Sku expectedPatchSku = postSku.clone();

		BeanTestUtil.copyProperties(randomPatchSku, expectedPatchSku);

		Sku getSku = skuResource.getSku(patchSku.getId());

		assertEquals(expectedPatchSku, getSku);
		assertValid(getSku);
	}

	protected Sku testPatchSku_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchSkuByExternalReferenceCode() throws Exception {
		Sku postSku = testPatchSkuByExternalReferenceCode_addSku();

		Sku randomPatchSku = randomPatchSku();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		Sku patchSku = skuResource.patchSkuByExternalReferenceCode(
			postSku.getExternalReferenceCode(), randomPatchSku);

		Sku expectedPatchSku = postSku.clone();

		BeanTestUtil.copyProperties(randomPatchSku, expectedPatchSku);

		Sku getSku = skuResource.getSkuByExternalReferenceCode(
			patchSku.getExternalReferenceCode());

		assertEquals(expectedPatchSku, getSku);
		assertValid(getSku);
	}

	protected Sku testPatchSkuByExternalReferenceCode_addSku()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeSku() throws Exception {
		Sku randomSku = randomSku();

		Sku postSku = testPostProductByExternalReferenceCodeSku_addSku(
			randomSku);

		assertEquals(randomSku, postSku);
		assertValid(postSku);
	}

	protected Sku testPostProductByExternalReferenceCodeSku_addSku(Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdSku() throws Exception {
		Sku randomSku = randomSku();

		Sku postSku = testPostProductIdSku_addSku(randomSku);

		assertEquals(randomSku, postSku);
		assertValid(postSku);
	}

	protected Sku testPostProductIdSku_addSku(Sku sku) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPutSkuByExternalReferenceCode() throws Exception {
		Sku postSku = testPutSkuByExternalReferenceCode_addSku();

		Sku randomSku = randomSku();

		Sku putSku = skuResource.putSkuByExternalReferenceCode(
			postSku.getExternalReferenceCode(), randomSku);

		assertEquals(randomSku, putSku);
		assertValid(putSku);

		Sku getSku = skuResource.getSkuByExternalReferenceCode(
			putSku.getExternalReferenceCode());

		assertEquals(randomSku, getSku);
		assertValid(getSku);

		Sku newSku = testPutSkuByExternalReferenceCode_createSku();

		putSku = skuResource.putSkuByExternalReferenceCode(
			newSku.getExternalReferenceCode(), newSku);

		assertEquals(newSku, putSku);
		assertValid(putSku);

		getSku = skuResource.getSkuByExternalReferenceCode(
			putSku.getExternalReferenceCode());

		assertEquals(newSku, getSku);

		Assert.assertEquals(
			newSku.getExternalReferenceCode(),
			putSku.getExternalReferenceCode());
	}

	protected Sku testPutSkuByExternalReferenceCode_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Sku testPutSkuByExternalReferenceCode_createSku()
		throws Exception {

		return randomSku();
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Sku sku1 = testBatchEngineDeleteImportTask_addSku();

		testBatchEngineDeleteImportTask_deleteSku(
			200, sku1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku1.getId()));

		sku1 = testBatchEngineDeleteImportTask_addSku();

		testBatchEngineDeleteImportTask_deleteSku(200, null, sku1.getId());

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku1.getId()));

		sku1 = testBatchEngineDeleteImportTask_addSku();
		Sku sku2 = testBatchEngineDeleteImportTask_addSku();

		testBatchEngineDeleteImportTask_deleteSku(
			200, sku2.getExternalReferenceCode(), sku1.getId());

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku1.getId()));
		assertHttpResponseStatusCode(
			200, skuResource.getSkuHttpResponse(sku2.getId()));

		testBatchEngineDeleteImportTask_deleteSku(
			200, sku2.getExternalReferenceCode(), sku1.getId());

		assertHttpResponseStatusCode(
			404, skuResource.getSkuHttpResponse(sku2.getId()));
	}

	protected Sku testBatchEngineDeleteImportTask_addSku() throws Exception {
		return testDeleteSku_addSku();
	}

	protected void testBatchEngineDeleteImportTask_deleteSku(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku",
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

	@Rule
	public SearchTestRule searchTestRule = new SearchTestRule();

	protected Sku testGraphQLSku_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(Sku sku, List<Sku> skus) {
		boolean contains = false;

		for (Sku item : skus) {
			if (equals(sku, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(skus + " does not contain " + sku, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(Sku sku1, Sku sku2) {
		Assert.assertTrue(sku1 + " does not equal " + sku2, equals(sku1, sku2));
	}

	protected void assertEquals(List<Sku> skus1, List<Sku> skus2) {
		Assert.assertEquals(skus1.size(), skus2.size());

		for (int i = 0; i < skus1.size(); i++) {
			Sku sku1 = skus1.get(i);
			Sku sku2 = skus2.get(i);

			assertEquals(sku1, sku2);
		}
	}

	protected void assertEqualsIgnoringOrder(List<Sku> skus1, List<Sku> skus2) {
		Assert.assertEquals(skus1.size(), skus2.size());

		for (Sku sku1 : skus1) {
			boolean contains = false;

			for (Sku sku2 : skus2) {
				if (equals(sku1, sku2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(skus2 + " does not contain " + sku1, contains);
		}
	}

	protected void assertValid(Sku sku) throws Exception {
		boolean valid = true;

		if (sku.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("cost", additionalAssertFieldName)) {
				if (sku.getCost() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (sku.getCustomFields() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("depth", additionalAssertFieldName)) {
				if (sku.getDepth() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discontinued", additionalAssertFieldName)) {
				if (sku.getDiscontinued() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("discontinuedDate", additionalAssertFieldName)) {
				if (sku.getDiscontinuedDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (sku.getDisplayDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (sku.getExpirationDate() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (sku.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("gtin", additionalAssertFieldName)) {
				if (sku.getGtin() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("height", additionalAssertFieldName)) {
				if (sku.getHeight() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("inventoryLevel", additionalAssertFieldName)) {
				if (sku.getInventoryLevel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"manufacturerPartNumber", additionalAssertFieldName)) {

				if (sku.getManufacturerPartNumber() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (sku.getNeverExpire() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (sku.getPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (sku.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productName", additionalAssertFieldName)) {
				if (sku.getProductName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("promoPrice", additionalAssertFieldName)) {
				if (sku.getPromoPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("published", additionalAssertFieldName)) {
				if (sku.getPublished() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("purchasable", additionalAssertFieldName)) {
				if (sku.getPurchasable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"replacementSkuExternalReferenceCode",
					additionalAssertFieldName)) {

				if (sku.getReplacementSkuExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("replacementSkuId", additionalAssertFieldName)) {
				if (sku.getReplacementSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (sku.getSku() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuOptions", additionalAssertFieldName)) {
				if (sku.getSkuOptions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuSubscriptionConfiguration",
					additionalAssertFieldName)) {

				if (sku.getSkuSubscriptionConfiguration() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuUnitOfMeasures", additionalAssertFieldName)) {

				if (sku.getSkuUnitOfMeasures() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"skuVirtualSettings", additionalAssertFieldName)) {

				if (sku.getSkuVirtualSettings() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (sku.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"unitOfMeasureName", additionalAssertFieldName)) {

				if (sku.getUnitOfMeasureName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"unitOfMeasureSkuId", additionalAssertFieldName)) {

				if (sku.getUnitOfMeasureSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unspsc", additionalAssertFieldName)) {
				if (sku.getUnspsc() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("weight", additionalAssertFieldName)) {
				if (sku.getWeight() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("width", additionalAssertFieldName)) {
				if (sku.getWidth() == null) {
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

	protected void assertValid(Page<Sku> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<Sku> page, Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<Sku> skus = page.getItems();

		int size = skus.size();

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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.Sku.
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

	protected boolean equals(Sku sku1, Sku sku2) {
		if (sku1 == sku2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("cost", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getCost(), sku2.getCost())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("customFields", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getCustomFields(), sku2.getCustomFields())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("depth", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getDepth(), sku2.getDepth())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("discontinued", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getDiscontinued(), sku2.getDiscontinued())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("discontinuedDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getDiscontinuedDate(),
						sku2.getDiscontinuedDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("displayDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getDisplayDate(), sku2.getDisplayDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("expirationDate", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getExpirationDate(), sku2.getExpirationDate())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getExternalReferenceCode(),
						sku2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("gtin", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getGtin(), sku2.getGtin())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("height", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getHeight(), sku2.getHeight())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getId(), sku2.getId())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("inventoryLevel", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getInventoryLevel(), sku2.getInventoryLevel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"manufacturerPartNumber", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getManufacturerPartNumber(),
						sku2.getManufacturerPartNumber())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("neverExpire", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getNeverExpire(), sku2.getNeverExpire())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getPrice(), sku2.getPrice())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getProductId(), sku2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productName", additionalAssertFieldName)) {
				if (!equals(
						(Map)sku1.getProductName(),
						(Map)sku2.getProductName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("promoPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getPromoPrice(), sku2.getPromoPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("published", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getPublished(), sku2.getPublished())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("purchasable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getPurchasable(), sku2.getPurchasable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"replacementSkuExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getReplacementSkuExternalReferenceCode(),
						sku2.getReplacementSkuExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("replacementSkuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getReplacementSkuId(),
						sku2.getReplacementSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("sku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getSku(), sku2.getSku())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("skuOptions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getSkuOptions(), sku2.getSkuOptions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuSubscriptionConfiguration",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getSkuSubscriptionConfiguration(),
						sku2.getSkuSubscriptionConfiguration())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuUnitOfMeasures", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getSkuUnitOfMeasures(),
						sku2.getSkuUnitOfMeasures())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"skuVirtualSettings", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getSkuVirtualSettings(),
						sku2.getSkuVirtualSettings())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getUnitOfMeasureKey(),
						sku2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"unitOfMeasureName", additionalAssertFieldName)) {

				if (!equals(
						(Map)sku1.getUnitOfMeasureName(),
						(Map)sku2.getUnitOfMeasureName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"unitOfMeasureSkuId", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getUnitOfMeasureSkuId(),
						sku2.getUnitOfMeasureSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unspsc", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getUnspsc(), sku2.getUnspsc())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("weight", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getWeight(), sku2.getWeight())) {
					return false;
				}

				continue;
			}

			if (Objects.equals("width", additionalAssertFieldName)) {
				if (!Objects.deepEquals(sku1.getWidth(), sku2.getWidth())) {
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

		if (!(_skuResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_skuResource;

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
		EntityField entityField, String operator, Sku sku) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("cost")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("customFields")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("depth")) {
			sb.append(String.valueOf(sku.getDepth()));

			return sb.toString();
		}

		if (entityFieldName.equals("discontinued")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("discontinuedDate")) {
			if (operator.equals("between")) {
				Date date = sku.getDiscontinuedDate();

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

				sb.append(_format.format(sku.getDiscontinuedDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("displayDate")) {
			if (operator.equals("between")) {
				Date date = sku.getDisplayDate();

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

				sb.append(_format.format(sku.getDisplayDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("expirationDate")) {
			if (operator.equals("between")) {
				Date date = sku.getExpirationDate();

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

				sb.append(_format.format(sku.getExpirationDate()));
			}

			return sb.toString();
		}

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = sku.getExternalReferenceCode();

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

		if (entityFieldName.equals("gtin")) {
			Object object = sku.getGtin();

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

		if (entityFieldName.equals("height")) {
			sb.append(String.valueOf(sku.getHeight()));

			return sb.toString();
		}

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("inventoryLevel")) {
			sb.append(String.valueOf(sku.getInventoryLevel()));

			return sb.toString();
		}

		if (entityFieldName.equals("manufacturerPartNumber")) {
			Object object = sku.getManufacturerPartNumber();

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

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productName")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("promoPrice")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("published")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("purchasable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("replacementSkuExternalReferenceCode")) {
			Object object = sku.getReplacementSkuExternalReferenceCode();

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

		if (entityFieldName.equals("replacementSkuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("sku")) {
			Object object = sku.getSku();

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

		if (entityFieldName.equals("skuOptions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuSubscriptionConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuUnitOfMeasures")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuVirtualSettings")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitOfMeasureKey")) {
			Object object = sku.getUnitOfMeasureKey();

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

		if (entityFieldName.equals("unitOfMeasureName")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("unitOfMeasureSkuId")) {
			Object object = sku.getUnitOfMeasureSkuId();

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

		if (entityFieldName.equals("unspsc")) {
			Object object = sku.getUnspsc();

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

		if (entityFieldName.equals("weight")) {
			sb.append(String.valueOf(sku.getWeight()));

			return sb.toString();
		}

		if (entityFieldName.equals("width")) {
			sb.append(String.valueOf(sku.getWidth()));

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

	protected Sku randomSku() throws Exception {
		return new Sku() {
			{
				depth = RandomTestUtil.randomDouble();
				discontinued = RandomTestUtil.randomBoolean();
				discontinuedDate = RandomTestUtil.nextDate();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				gtin = StringUtil.toLowerCase(RandomTestUtil.randomString());
				height = RandomTestUtil.randomDouble();
				id = RandomTestUtil.randomLong();
				inventoryLevel = RandomTestUtil.randomInt();
				manufacturerPartNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				neverExpire = RandomTestUtil.randomBoolean();
				productId = RandomTestUtil.randomLong();
				published = RandomTestUtil.randomBoolean();
				purchasable = RandomTestUtil.randomBoolean();
				replacementSkuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				replacementSkuId = RandomTestUtil.randomLong();
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasureSkuId = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unspsc = StringUtil.toLowerCase(RandomTestUtil.randomString());
				weight = RandomTestUtil.randomDouble();
				width = RandomTestUtil.randomDouble();
			}
		};
	}

	protected Sku randomIrrelevantSku() throws Exception {
		Sku randomIrrelevantSku = randomSku();

		return randomIrrelevantSku;
	}

	protected Sku randomPatchSku() throws Exception {
		return randomSku();
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

	protected SkuResource skuResource;
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
		LogFactoryUtil.getLog(BaseSkuResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private
		com.liferay.headless.commerce.admin.catalog.resource.v1_0.SkuResource
			_skuResource;

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