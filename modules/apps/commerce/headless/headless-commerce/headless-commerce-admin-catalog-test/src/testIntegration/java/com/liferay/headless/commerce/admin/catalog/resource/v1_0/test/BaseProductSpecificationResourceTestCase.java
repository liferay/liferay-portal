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
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductSpecification;
import com.liferay.headless.commerce.admin.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Page;
import com.liferay.headless.commerce.admin.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.ProductSpecificationResource;
import com.liferay.headless.commerce.admin.catalog.client.serdes.v1_0.ProductSpecificationSerDes;
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
public abstract class BaseProductSpecificationResourceTestCase {

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

		_productSpecificationResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productSpecificationResource = ProductSpecificationResource.builder(
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

		ProductSpecification productSpecification1 =
			randomProductSpecification();

		String json = objectMapper.writeValueAsString(productSpecification1);

		ProductSpecification productSpecification2 =
			ProductSpecificationSerDes.toDTO(json);

		Assert.assertTrue(equals(productSpecification1, productSpecification2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductSpecification productSpecification =
			randomProductSpecification();

		String json1 = objectMapper.writeValueAsString(productSpecification);
		String json2 = ProductSpecificationSerDes.toJSON(productSpecification);

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

		ProductSpecification productSpecification =
			randomProductSpecification();

		productSpecification.setExternalReferenceCode(regex);
		productSpecification.setKey(regex);
		productSpecification.setOptionCategoryExternalReferenceCode(regex);
		productSpecification.setSpecificationExternalReferenceCode(regex);
		productSpecification.setSpecificationKey(regex);

		String json = ProductSpecificationSerDes.toJSON(productSpecification);

		Assert.assertFalse(json.contains(regex));

		productSpecification = ProductSpecificationSerDes.toDTO(json);

		Assert.assertEquals(
			regex, productSpecification.getExternalReferenceCode());
		Assert.assertEquals(regex, productSpecification.getKey());
		Assert.assertEquals(
			regex,
			productSpecification.getOptionCategoryExternalReferenceCode());
		Assert.assertEquals(
			regex,
			productSpecification.getSpecificationExternalReferenceCode());
		Assert.assertEquals(regex, productSpecification.getSpecificationKey());
	}

	@Test
	public void testDeleteProductSpecification() throws Exception {
		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductSpecification productSpecification =
			testDeleteProductSpecification_addProductSpecification();

		assertHttpResponseStatusCode(
			204,
			productSpecificationResource.deleteProductSpecificationHttpResponse(
				productSpecification.getId()));

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification.getId()));
		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				0L));
	}

	protected ProductSpecification
			testDeleteProductSpecification_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductSpecification() throws Exception {

		// No namespace

		ProductSpecification productSpecification1 =
			testGraphQLDeleteProductSpecification_addProductSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductSpecification",
						new HashMap<String, Object>() {
							{
								put("id", productSpecification1.getId());
							}
						})),
				"JSONObject/data", "Object/deleteProductSpecification"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productSpecification",
					new HashMap<String, Object>() {
						{
							put("id", productSpecification1.getId());
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductSpecification productSpecification2 =
			testGraphQLDeleteProductSpecification_addProductSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductSpecification",
							new HashMap<String, Object>() {
								{
									put("id", productSpecification2.getId());
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductSpecification"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productSpecification",
						new HashMap<String, Object>() {
							{
								put("id", productSpecification2.getId());
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductSpecification
			testGraphQLDeleteProductSpecification_addProductSpecification()
		throws Exception {

		return testGraphQLProductSpecification_addProductSpecification();
	}

	@Test
	public void testDeleteProductSpecificationBatch() throws Exception {
		ProductSpecification productSpecification1 =
			testDeleteProductSpecificationBatch_addProductSpecification();

		testDeleteProductSpecificationBatch_deleteProductSpecification(
			202, productSpecification1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification1.getId()));

		productSpecification1 =
			testDeleteProductSpecificationBatch_addProductSpecification();

		testDeleteProductSpecificationBatch_deleteProductSpecification(
			202, null, productSpecification1.getId());

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification1.getId()));

		productSpecification1 =
			testDeleteProductSpecificationBatch_addProductSpecification();
		ProductSpecification productSpecification2 =
			testDeleteProductSpecificationBatch_addProductSpecification();

		testDeleteProductSpecificationBatch_deleteProductSpecification(
			202, productSpecification2.getExternalReferenceCode(),
			productSpecification1.getId());

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification1.getId()));
		assertHttpResponseStatusCode(
			200,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification2.getId()));

		testDeleteProductSpecificationBatch_deleteProductSpecification(
			202, productSpecification2.getExternalReferenceCode(),
			productSpecification1.getId());

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification2.getId()));
	}

	protected ProductSpecification
			testDeleteProductSpecificationBatch_addProductSpecification()
		throws Exception {

		return testDeleteProductSpecification_addProductSpecification();
	}

	protected void
			testDeleteProductSpecificationBatch_deleteProductSpecification(
				int expectedStatusCode, String externalReferenceCode, Long id)
		throws Exception {

		HttpInvoker.HttpResponse httpResponse =
			productSpecificationResource.
				deleteProductSpecificationBatchHttpResponse(
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
	public void testDeleteProductSpecificationByExternalReferenceCode()
		throws Exception {

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductSpecification productSpecification =
			testDeleteProductSpecificationByExternalReferenceCode_addProductSpecification();

		assertHttpResponseStatusCode(
			204,
			productSpecificationResource.
				deleteProductSpecificationByExternalReferenceCodeHttpResponse(
					productSpecification.getExternalReferenceCode()));

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.
				getProductSpecificationByExternalReferenceCodeHttpResponse(
					productSpecification.getExternalReferenceCode()));
		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.
				getProductSpecificationByExternalReferenceCodeHttpResponse(
					"-"));
	}

	protected ProductSpecification
			testDeleteProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLDeleteProductSpecificationByExternalReferenceCode()
		throws Exception {

		// No namespace

		ProductSpecification productSpecification1 =
			testGraphQLDeleteProductSpecificationByExternalReferenceCode_addProductSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"deleteProductSpecificationByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productSpecification1.
											getExternalReferenceCode() + "\"");
							}
						})),
				"JSONObject/data",
				"Object/deleteProductSpecificationByExternalReferenceCode"));

		JSONArray errorsJSONArray1 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"productSpecificationByExternalReferenceCode",
					new HashMap<String, Object>() {
						{
							put(
								"externalReferenceCode",
								"\"" +
									productSpecification1.
										getExternalReferenceCode() + "\"");
						}
					},
					getGraphQLFields())),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray1.length() > 0);

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		ProductSpecification productSpecification2 =
			testGraphQLDeleteProductSpecificationByExternalReferenceCode_addProductSpecification();

		Assert.assertTrue(
			JSONUtil.getValueAsBoolean(
				invokeGraphQLMutation(
					new GraphQLField(
						"headlessCommerceAdminCatalog_v1_0",
						new GraphQLField(
							"deleteProductSpecificationByExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"externalReferenceCode",
										"\"" +
											productSpecification2.
												getExternalReferenceCode() +
													"\"");
								}
							}))),
				"JSONObject/data",
				"JSONObject/headlessCommerceAdminCatalog_v1_0",
				"Object/deleteProductSpecificationByExternalReferenceCode"));

		JSONArray errorsJSONArray2 = JSONUtil.getValueAsJSONArray(
			invokeGraphQLQuery(
				new GraphQLField(
					"headlessCommerceAdminCatalog_v1_0",
					new GraphQLField(
						"productSpecificationByExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"externalReferenceCode",
									"\"" +
										productSpecification2.
											getExternalReferenceCode() + "\"");
							}
						},
						getGraphQLFields()))),
			"JSONArray/errors");

		Assert.assertTrue(errorsJSONArray2.length() > 0);
	}

	protected ProductSpecification
			testGraphQLDeleteProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		return testGraphQLProductSpecification_addProductSpecification();
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductSpecificationsPage()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getExternalReferenceCode();
		String irrelevantExternalReferenceCode =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getIrrelevantExternalReferenceCode();

		Page<ProductSpecification> page =
			productSpecificationResource.
				getProductByExternalReferenceCodeProductSpecificationsPage(
					externalReferenceCode, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantExternalReferenceCode != null) {
			ProductSpecification irrelevantProductSpecification =
				testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
					irrelevantExternalReferenceCode,
					randomIrrelevantProductSpecification());

			page =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						irrelevantExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductSpecification,
				(List<ProductSpecification>)page.getItems());
			assertValid(
				page,
				testGetProductByExternalReferenceCodeProductSpecificationsPage_getExpectedActions(
					irrelevantExternalReferenceCode));
		}

		ProductSpecification productSpecification1 =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				externalReferenceCode, randomProductSpecification());

		ProductSpecification productSpecification2 =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				externalReferenceCode, randomProductSpecification());

		page =
			productSpecificationResource.
				getProductByExternalReferenceCodeProductSpecificationsPage(
					externalReferenceCode, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productSpecification1, (List<ProductSpecification>)page.getItems());
		assertContains(
			productSpecification2, (List<ProductSpecification>)page.getItems());
		assertValid(
			page,
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getExpectedActions(
				externalReferenceCode));

		productSpecificationResource.deleteProductSpecification(
			productSpecification1.getId());

		productSpecificationResource.deleteProductSpecification(
			productSpecification2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getExpectedActions(
				String externalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductByExternalReferenceCodeProductSpecificationsPageWithPagination()
		throws Exception {

		String externalReferenceCode =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getExternalReferenceCode();

		Page<ProductSpecification> productSpecificationsPage =
			productSpecificationResource.
				getProductByExternalReferenceCodeProductSpecificationsPage(
					externalReferenceCode, null);

		int totalCount = GetterUtil.getInteger(
			productSpecificationsPage.getTotalCount());

		ProductSpecification productSpecification1 =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				externalReferenceCode, randomProductSpecification());

		ProductSpecification productSpecification2 =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				externalReferenceCode, randomProductSpecification());

		ProductSpecification productSpecification3 =
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				externalReferenceCode, randomProductSpecification());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductSpecification> page1 =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productSpecification1,
				(List<ProductSpecification>)page1.getItems());

			Page<ProductSpecification> page2 =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productSpecification2,
				(List<ProductSpecification>)page2.getItems());

			Page<ProductSpecification> page3 =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productSpecification3,
				(List<ProductSpecification>)page3.getItems());
		}
		else {
			Page<ProductSpecification> page1 =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ProductSpecification> productSpecifications1 =
				(List<ProductSpecification>)page1.getItems();

			Assert.assertEquals(
				productSpecifications1.toString(), totalCount + 2,
				productSpecifications1.size());

			Page<ProductSpecification> page2 =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductSpecification> productSpecifications2 =
				(List<ProductSpecification>)page2.getItems();

			Assert.assertEquals(
				productSpecifications2.toString(), 1,
				productSpecifications2.size());

			Page<ProductSpecification> page3 =
				productSpecificationResource.
					getProductByExternalReferenceCodeProductSpecificationsPage(
						externalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productSpecification1,
				(List<ProductSpecification>)page3.getItems());
			assertContains(
				productSpecification2,
				(List<ProductSpecification>)page3.getItems());
			assertContains(
				productSpecification3,
				(List<ProductSpecification>)page3.getItems());
		}
	}

	protected ProductSpecification
			testGetProductByExternalReferenceCodeProductSpecificationsPage_addProductSpecification(
				String externalReferenceCode,
				ProductSpecification productSpecification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetProductByExternalReferenceCodeProductSpecificationsPage_getIrrelevantExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductIdProductSpecificationsPage() throws Exception {
		Long id = testGetProductIdProductSpecificationsPage_getId();
		Long irrelevantId =
			testGetProductIdProductSpecificationsPage_getIrrelevantId();

		Page<ProductSpecification> page =
			productSpecificationResource.getProductIdProductSpecificationsPage(
				id, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if (irrelevantId != null) {
			ProductSpecification irrelevantProductSpecification =
				testGetProductIdProductSpecificationsPage_addProductSpecification(
					irrelevantId, randomIrrelevantProductSpecification());

			page =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						irrelevantId, Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductSpecification,
				(List<ProductSpecification>)page.getItems());
			assertValid(
				page,
				testGetProductIdProductSpecificationsPage_getExpectedActions(
					irrelevantId));
		}

		ProductSpecification productSpecification1 =
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				id, randomProductSpecification());

		ProductSpecification productSpecification2 =
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				id, randomProductSpecification());

		page =
			productSpecificationResource.getProductIdProductSpecificationsPage(
				id, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productSpecification1, (List<ProductSpecification>)page.getItems());
		assertContains(
			productSpecification2, (List<ProductSpecification>)page.getItems());
		assertValid(
			page,
			testGetProductIdProductSpecificationsPage_getExpectedActions(id));

		productSpecificationResource.deleteProductSpecification(
			productSpecification1.getId());

		productSpecificationResource.deleteProductSpecification(
			productSpecification2.getId());
	}

	protected Map<String, Map<String, String>>
			testGetProductIdProductSpecificationsPage_getExpectedActions(
				Long id)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetProductIdProductSpecificationsPageWithPagination()
		throws Exception {

		Long id = testGetProductIdProductSpecificationsPage_getId();

		Page<ProductSpecification> productSpecificationsPage =
			productSpecificationResource.getProductIdProductSpecificationsPage(
				id, null);

		int totalCount = GetterUtil.getInteger(
			productSpecificationsPage.getTotalCount());

		ProductSpecification productSpecification1 =
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				id, randomProductSpecification());

		ProductSpecification productSpecification2 =
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				id, randomProductSpecification());

		ProductSpecification productSpecification3 =
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				id, randomProductSpecification());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductSpecification> page1 =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productSpecification1,
				(List<ProductSpecification>)page1.getItems());

			Page<ProductSpecification> page2 =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productSpecification2,
				(List<ProductSpecification>)page2.getItems());

			Page<ProductSpecification> page3 =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productSpecification3,
				(List<ProductSpecification>)page3.getItems());
		}
		else {
			Page<ProductSpecification> page1 =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id, Pagination.of(1, totalCount + 2));

			List<ProductSpecification> productSpecifications1 =
				(List<ProductSpecification>)page1.getItems();

			Assert.assertEquals(
				productSpecifications1.toString(), totalCount + 2,
				productSpecifications1.size());

			Page<ProductSpecification> page2 =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductSpecification> productSpecifications2 =
				(List<ProductSpecification>)page2.getItems();

			Assert.assertEquals(
				productSpecifications2.toString(), 1,
				productSpecifications2.size());

			Page<ProductSpecification> page3 =
				productSpecificationResource.
					getProductIdProductSpecificationsPage(
						id, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productSpecification1,
				(List<ProductSpecification>)page3.getItems());
			assertContains(
				productSpecification2,
				(List<ProductSpecification>)page3.getItems());
			assertContains(
				productSpecification3,
				(List<ProductSpecification>)page3.getItems());
		}
	}

	protected ProductSpecification
			testGetProductIdProductSpecificationsPage_addProductSpecification(
				Long id, ProductSpecification productSpecification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdProductSpecificationsPage_getId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetProductIdProductSpecificationsPage_getIrrelevantId()
		throws Exception {

		return null;
	}

	@Test
	public void testGetProductSpecification() throws Exception {
		ProductSpecification postProductSpecification =
			testGetProductSpecification_addProductSpecification();

		ProductSpecification getProductSpecification =
			productSpecificationResource.getProductSpecification(
				postProductSpecification.getId());

		assertEquals(postProductSpecification, getProductSpecification);
		assertValid(getProductSpecification);
	}

	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		ProductSpecification postProductSpecification =
			testGetProductSpecification_addProductSpecification();

		ProductSpecification getProductSpecification =
			productSpecificationResource.getProductSpecification(
				postProductSpecification.getId());

		VulcanCRUDItemDelegate vulcanCRUDItemDelegate =
			_vulcanCRUDItemDelegateBuilderRegistry.builder(
				testCompany,
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSpecification"
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

		Object item = vulcanCRUDItemDelegate.getItem(
			postProductSpecification.getId());

		assertEquals(
			getProductSpecification,
			ProductSpecificationSerDes.toDTO(item.toString()));
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

	protected ProductSpecification
			testGetProductSpecification_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductSpecification() throws Exception {
		ProductSpecification productSpecification =
			testGraphQLGetProductSpecification_addProductSpecification();

		// No namespace

		Assert.assertTrue(
			equals(
				productSpecification,
				ProductSpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productSpecification",
								new HashMap<String, Object>() {
									{
										put("id", productSpecification.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/productSpecification"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productSpecification,
				ProductSpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productSpecification",
									new HashMap<String, Object>() {
										{
											put(
												"id",
												productSpecification.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productSpecification"))));
	}

	@Test
	public void testGraphQLGetProductSpecificationNotFound() throws Exception {
		Long irrelevantId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productSpecification",
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
							"productSpecification",
							new HashMap<String, Object>() {
								{
									put("id", irrelevantId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected ProductSpecification
			testGraphQLGetProductSpecification_addProductSpecification()
		throws Exception {

		return testGraphQLProductSpecification_addProductSpecification();
	}

	@Test
	public void testGetProductSpecificationByExternalReferenceCode()
		throws Exception {

		ProductSpecification postProductSpecification =
			testGetProductSpecificationByExternalReferenceCode_addProductSpecification();

		ProductSpecification getProductSpecification =
			productSpecificationResource.
				getProductSpecificationByExternalReferenceCode(
					postProductSpecification.getExternalReferenceCode());

		assertEquals(postProductSpecification, getProductSpecification);
		assertValid(getProductSpecification);
	}

	protected ProductSpecification
			testGetProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetProductSpecificationByExternalReferenceCode()
		throws Exception {

		ProductSpecification productSpecification =
			testGraphQLGetProductSpecificationByExternalReferenceCode_addProductSpecification();

		// No namespace

		Assert.assertTrue(
			equals(
				productSpecification,
				ProductSpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"productSpecificationByExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"externalReferenceCode",
											"\"" +
												productSpecification.
													getExternalReferenceCode() +
														"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/productSpecificationByExternalReferenceCode"))));

		// Using the namespace headlessCommerceAdminCatalog_v1_0

		Assert.assertTrue(
			equals(
				productSpecification,
				ProductSpecificationSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceAdminCatalog_v1_0",
								new GraphQLField(
									"productSpecificationByExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"externalReferenceCode",
												"\"" +
													productSpecification.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceAdminCatalog_v1_0",
						"Object/productSpecificationByExternalReferenceCode"))));
	}

	@Test
	public void testGraphQLGetProductSpecificationByExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"productSpecificationByExternalReferenceCode",
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
							"productSpecificationByExternalReferenceCode",
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

	protected ProductSpecification
			testGraphQLGetProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		return testGraphQLProductSpecification_addProductSpecification();
	}

	@Test
	public void testPatchProductSpecification() throws Exception {
		ProductSpecification postProductSpecification =
			testPatchProductSpecification_addProductSpecification();

		ProductSpecification randomPatchProductSpecification =
			randomPatchProductSpecification();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductSpecification patchProductSpecification =
			productSpecificationResource.patchProductSpecification(
				postProductSpecification.getId(),
				randomPatchProductSpecification);

		ProductSpecification expectedPatchProductSpecification =
			postProductSpecification.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductSpecification, expectedPatchProductSpecification);

		ProductSpecification getProductSpecification =
			productSpecificationResource.getProductSpecification(
				patchProductSpecification.getId());

		assertEquals(
			expectedPatchProductSpecification, getProductSpecification);
		assertValid(getProductSpecification);
	}

	protected ProductSpecification
			testPatchProductSpecification_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPatchProductSpecificationByExternalReferenceCode()
		throws Exception {

		ProductSpecification postProductSpecification =
			testPatchProductSpecificationByExternalReferenceCode_addProductSpecification();

		ProductSpecification randomPatchProductSpecification =
			randomPatchProductSpecification();

		@SuppressWarnings("PMD.UnusedLocalVariable")
		ProductSpecification patchProductSpecification =
			productSpecificationResource.
				patchProductSpecificationByExternalReferenceCode(
					postProductSpecification.getExternalReferenceCode(),
					randomPatchProductSpecification);

		ProductSpecification expectedPatchProductSpecification =
			postProductSpecification.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductSpecification, expectedPatchProductSpecification);

		ProductSpecification getProductSpecification =
			productSpecificationResource.
				getProductSpecificationByExternalReferenceCode(
					patchProductSpecification.getExternalReferenceCode());

		assertEquals(
			expectedPatchProductSpecification, getProductSpecification);
		assertValid(getProductSpecification);
	}

	protected ProductSpecification
			testPatchProductSpecificationByExternalReferenceCode_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductByExternalReferenceCodeProductSpecification()
		throws Exception {

		ProductSpecification randomProductSpecification =
			randomProductSpecification();

		ProductSpecification postProductSpecification =
			testPostProductByExternalReferenceCodeProductSpecification_addProductSpecification(
				randomProductSpecification);

		assertEquals(randomProductSpecification, postProductSpecification);
		assertValid(postProductSpecification);
	}

	protected ProductSpecification
			testPostProductByExternalReferenceCodeProductSpecification_addProductSpecification(
				ProductSpecification productSpecification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostProductIdProductSpecification() throws Exception {
		ProductSpecification randomProductSpecification =
			randomProductSpecification();

		ProductSpecification postProductSpecification =
			testPostProductIdProductSpecification_addProductSpecification(
				randomProductSpecification);

		assertEquals(randomProductSpecification, postProductSpecification);
		assertValid(postProductSpecification);
	}

	protected ProductSpecification
			testPostProductIdProductSpecification_addProductSpecification(
				ProductSpecification productSpecification)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		ProductSpecification productSpecification1 =
			testBatchEngineDeleteImportTask_addProductSpecification();

		testBatchEngineDeleteImportTask_deleteProductSpecification(
			200, productSpecification1.getExternalReferenceCode(), null);

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification1.getId()));

		productSpecification1 =
			testBatchEngineDeleteImportTask_addProductSpecification();

		testBatchEngineDeleteImportTask_deleteProductSpecification(
			200, null, productSpecification1.getId());

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification1.getId()));

		productSpecification1 =
			testBatchEngineDeleteImportTask_addProductSpecification();
		ProductSpecification productSpecification2 =
			testBatchEngineDeleteImportTask_addProductSpecification();

		testBatchEngineDeleteImportTask_deleteProductSpecification(
			200, productSpecification2.getExternalReferenceCode(),
			productSpecification1.getId());

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification1.getId()));
		assertHttpResponseStatusCode(
			200,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification2.getId()));

		testBatchEngineDeleteImportTask_deleteProductSpecification(
			200, productSpecification2.getExternalReferenceCode(),
			productSpecification1.getId());

		assertHttpResponseStatusCode(
			404,
			productSpecificationResource.getProductSpecificationHttpResponse(
				productSpecification2.getId()));
	}

	protected ProductSpecification
			testBatchEngineDeleteImportTask_addProductSpecification()
		throws Exception {

		return testDeleteProductSpecification_addProductSpecification();
	}

	protected void testBatchEngineDeleteImportTask_deleteProductSpecification(
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
				"com.liferay.headless.commerce.admin.catalog.dto.v1_0.ProductSpecification",
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

	protected ProductSpecification
			testGraphQLProductSpecification_addProductSpecification()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected void assertContains(
		ProductSpecification productSpecification,
		List<ProductSpecification> productSpecifications) {

		boolean contains = false;

		for (ProductSpecification item : productSpecifications) {
			if (equals(productSpecification, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productSpecifications + " does not contain " + productSpecification,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductSpecification productSpecification1,
		ProductSpecification productSpecification2) {

		Assert.assertTrue(
			productSpecification1 + " does not equal " + productSpecification2,
			equals(productSpecification1, productSpecification2));
	}

	protected void assertEquals(
		List<ProductSpecification> productSpecifications1,
		List<ProductSpecification> productSpecifications2) {

		Assert.assertEquals(
			productSpecifications1.size(), productSpecifications2.size());

		for (int i = 0; i < productSpecifications1.size(); i++) {
			ProductSpecification productSpecification1 =
				productSpecifications1.get(i);
			ProductSpecification productSpecification2 =
				productSpecifications2.get(i);

			assertEquals(productSpecification1, productSpecification2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductSpecification> productSpecifications1,
		List<ProductSpecification> productSpecifications2) {

		Assert.assertEquals(
			productSpecifications1.size(), productSpecifications2.size());

		for (ProductSpecification productSpecification1 :
				productSpecifications1) {

			boolean contains = false;

			for (ProductSpecification productSpecification2 :
					productSpecifications2) {

				if (equals(productSpecification1, productSpecification2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productSpecifications2 + " does not contain " +
					productSpecification1,
				contains);
		}
	}

	protected void assertValid(ProductSpecification productSpecification)
		throws Exception {

		boolean valid = true;

		if (productSpecification.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (productSpecification.getExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (productSpecification.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (productSpecification.getLabel() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"optionCategoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productSpecification.
						getOptionCategoryExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("optionCategoryId", additionalAssertFieldName)) {
				if (productSpecification.getOptionCategoryId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productSpecification.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (productSpecification.getProductId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"specificationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (productSpecification.
						getSpecificationExternalReferenceCode() == null) {

					valid = false;
				}

				continue;
			}

			if (Objects.equals("specificationId", additionalAssertFieldName)) {
				if (productSpecification.getSpecificationId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("specificationKey", additionalAssertFieldName)) {
				if (productSpecification.getSpecificationKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"specificationPriority", additionalAssertFieldName)) {

				if (productSpecification.getSpecificationPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("value", additionalAssertFieldName)) {
				if (productSpecification.getValue() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("visible", additionalAssertFieldName)) {
				if (productSpecification.getVisible() == null) {
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

	protected void assertValid(Page<ProductSpecification> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductSpecification> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductSpecification> productSpecifications =
			page.getItems();

		int size = productSpecifications.size();

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
					com.liferay.headless.commerce.admin.catalog.dto.v1_0.
						ProductSpecification.class)) {

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
		ProductSpecification productSpecification1,
		ProductSpecification productSpecification2) {

		if (productSpecification1 == productSpecification2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals(
					"externalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productSpecification1.getExternalReferenceCode(),
						productSpecification2.getExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getId(),
						productSpecification2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getKey(),
						productSpecification2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("label", additionalAssertFieldName)) {
				if (!equals(
						(Map)productSpecification1.getLabel(),
						(Map)productSpecification2.getLabel())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"optionCategoryExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productSpecification1.
							getOptionCategoryExternalReferenceCode(),
						productSpecification2.
							getOptionCategoryExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("optionCategoryId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getOptionCategoryId(),
						productSpecification2.getOptionCategoryId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getPriority(),
						productSpecification2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getProductId(),
						productSpecification2.getProductId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"specificationExternalReferenceCode",
					additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productSpecification1.
							getSpecificationExternalReferenceCode(),
						productSpecification2.
							getSpecificationExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("specificationId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getSpecificationId(),
						productSpecification2.getSpecificationId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("specificationKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getSpecificationKey(),
						productSpecification2.getSpecificationKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"specificationPriority", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productSpecification1.getSpecificationPriority(),
						productSpecification2.getSpecificationPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("value", additionalAssertFieldName)) {
				if (!equals(
						(Map)productSpecification1.getValue(),
						(Map)productSpecification2.getValue())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("visible", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productSpecification1.getVisible(),
						productSpecification2.getVisible())) {

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

		if (!(_productSpecificationResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productSpecificationResource;

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
		ProductSpecification productSpecification) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("externalReferenceCode")) {
			Object object = productSpecification.getExternalReferenceCode();

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

		if (entityFieldName.equals("key")) {
			Object object = productSpecification.getKey();

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

		if (entityFieldName.equals("label")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("optionCategoryExternalReferenceCode")) {
			Object object =
				productSpecification.getOptionCategoryExternalReferenceCode();

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

		if (entityFieldName.equals("optionCategoryId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(productSpecification.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("productId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("specificationExternalReferenceCode")) {
			Object object =
				productSpecification.getSpecificationExternalReferenceCode();

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

		if (entityFieldName.equals("specificationId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("specificationKey")) {
			Object object = productSpecification.getSpecificationKey();

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

		if (entityFieldName.equals("specificationPriority")) {
			sb.append(
				String.valueOf(
					productSpecification.getSpecificationPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("value")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("visible")) {
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

	protected ProductSpecification randomProductSpecification()
		throws Exception {

		return new ProductSpecification() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				optionCategoryExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				optionCategoryId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
				productId = RandomTestUtil.randomLong();
				specificationExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				specificationId = RandomTestUtil.randomLong();
				specificationKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				specificationPriority = RandomTestUtil.randomDouble();
				visible = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ProductSpecification randomIrrelevantProductSpecification()
		throws Exception {

		ProductSpecification randomIrrelevantProductSpecification =
			randomProductSpecification();

		return randomIrrelevantProductSpecification;
	}

	protected ProductSpecification randomPatchProductSpecification()
		throws Exception {

		return randomProductSpecification();
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

	protected ProductSpecificationResource productSpecificationResource;
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
		LogFactoryUtil.getLog(BaseProductSpecificationResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.admin.catalog.resource.v1_0.
		ProductSpecificationResource _productSpecificationResource;

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