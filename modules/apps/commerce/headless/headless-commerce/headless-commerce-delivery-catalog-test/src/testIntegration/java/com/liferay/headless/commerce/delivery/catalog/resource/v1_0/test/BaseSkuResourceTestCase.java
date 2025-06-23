/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.delivery.catalog.resource.v1_0.test;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.SkuSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseSkuResourceTestCase {

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
		sku.setIncomingQuantityLabel(regex);
		sku.setManufacturerPartNumber(regex);
		sku.setReplacementSkuExternalReferenceCode(regex);
		sku.setSku(regex);

		String json = SkuSerDes.toJSON(sku);

		Assert.assertFalse(json.contains(regex));

		sku = SkuSerDes.toDTO(json);

		Assert.assertEquals(regex, sku.getExternalReferenceCode());
		Assert.assertEquals(regex, sku.getGtin());
		Assert.assertEquals(regex, sku.getIncomingQuantityLabel());
		Assert.assertEquals(regex, sku.getManufacturerPartNumber());
		Assert.assertEquals(
			regex, sku.getReplacementSkuExternalReferenceCode());
		Assert.assertEquals(regex, sku.getSku());
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode()
		throws Exception {

		Sku postSku =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_addSku();

		Sku getSku =
			skuResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode(
					testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getChannelExternalReferenceCode(),
					testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getProductExternalReferenceCode(),
					postSku.getExternalReferenceCode(), null, null);

		assertEquals(postSku, getSku);
		assertValid(getSku);
	}

	protected Sku
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_addSku()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getProductExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode()
		throws Exception {

		Sku sku =
			testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_addSku();

		// No namespace

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode",
								new HashMap<String, Object>() {
									{
										put(
											"channelExternalReferenceCode",
											"\"" +
												testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getChannelExternalReferenceCode() +
													"\"");

										put(
											"productExternalReferenceCode",
											"\"" +
												testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getProductExternalReferenceCode() +
													"\"");
										put(
											"skuExternalReferenceCode",
											"\"" +
												sku.getExternalReferenceCode() +
													"\"");
									}
								},
								getGraphQLFields())),
						"JSONObject/data",
						"Object/channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode"))));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCatalog_v1_0",
								new GraphQLField(
									"channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode",
									new HashMap<String, Object>() {
										{
											put(
												"channelExternalReferenceCode",
												"\"" +
													testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getChannelExternalReferenceCode() +
														"\"");

											put(
												"productExternalReferenceCode",
												"\"" +
													testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getProductExternalReferenceCode() +
														"\"");
											put(
												"skuExternalReferenceCode",
												"\"" +
													sku.
														getExternalReferenceCode() +
															"\"");
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCatalog_v1_0",
						"Object/channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode"))));
	}

	protected String
			testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_getProductExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCodeNotFound()
		throws Exception {

		String irrelevantChannelExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantProductExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";
		String irrelevantSkuExternalReferenceCode =
			"\"" + RandomTestUtil.randomString() + "\"";

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode",
						new HashMap<String, Object>() {
							{
								put(
									"channelExternalReferenceCode",
									irrelevantChannelExternalReferenceCode);
								put(
									"productExternalReferenceCode",
									irrelevantProductExternalReferenceCode);
								put(
									"skuExternalReferenceCode",
									irrelevantSkuExternalReferenceCode);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCatalog_v1_0",
						new GraphQLField(
							"channelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode",
							new HashMap<String, Object>() {
								{
									put(
										"channelExternalReferenceCode",
										irrelevantChannelExternalReferenceCode);
									put(
										"productExternalReferenceCode",
										irrelevantProductExternalReferenceCode);
									put(
										"skuExternalReferenceCode",
										irrelevantSkuExternalReferenceCode);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Sku
			testGraphQLGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuByExternalReferenceCodeSkuExternalReferenceCode_addSku()
		throws Exception {

		return testGraphQLSku_addSku();
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getChannelExternalReferenceCode();
		String irrelevantChannelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getIrrelevantChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getProductExternalReferenceCode();
		String irrelevantProductExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getIrrelevantProductExternalReferenceCode();

		Page<Sku> page =
			skuResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, RandomTestUtil.randomString(), Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelExternalReferenceCode != null) &&
			(irrelevantProductExternalReferenceCode != null)) {

			Sku irrelevantSku =
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode,
					randomIrrelevantSku());

			page =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						irrelevantChannelExternalReferenceCode,
						irrelevantProductExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSku, (List<Sku>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getExpectedActions(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode));
		}

		Sku sku1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomSku());

		Sku sku2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomSku());

		page =
			skuResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sku1, (List<Sku>)page.getItems());
		assertContains(sku2, (List<Sku>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getExpectedActions(
				channelExternalReferenceCode, productExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getExpectedActions(
				String channelExternalReferenceCode,
				String productExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPageWithPagination()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getProductExternalReferenceCode();

		Page<Sku> skusPage =
			skuResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null, null, null);

		int totalCount = GetterUtil.getInteger(skusPage.getTotalCount());

		Sku sku1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomSku());

		Sku sku2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomSku());

		Sku sku3 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Sku> page1 =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sku1, (List<Sku>)page1.getItems());

			Page<Sku> page2 =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(sku2, (List<Sku>)page2.getItems());

			Page<Sku> page3 =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(sku3, (List<Sku>)page3.getItems());
		}
		else {
			Page<Sku> page1 =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(1, totalCount + 2));

			List<Sku> skus1 = (List<Sku>)page1.getItems();

			Assert.assertEquals(skus1.toString(), totalCount + 2, skus1.size());

			Page<Sku> page2 =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Sku> skus2 = (List<Sku>)page2.getItems();

			Assert.assertEquals(skus2.toString(), 1, skus2.size());

			Page<Sku> page3 =
				skuResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage(
						channelExternalReferenceCode,
						productExternalReferenceCode, null, null,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(sku1, (List<Sku>)page3.getItems());
			assertContains(sku2, (List<Sku>)page3.getItems());
			assertContains(sku3, (List<Sku>)page3.getItems());
		}
	}

	protected Sku
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_addSku(
				String channelExternalReferenceCode,
				String productExternalReferenceCode, Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getIrrelevantChannelExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getProductExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkusPage_getIrrelevantProductExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelProductSku() throws Exception {
		Sku postSku = testGetChannelProductSku_addSku();

		Sku getSku = skuResource.getChannelProductSku(
			testGetChannelProductSku_getChannelId(),
			testGetChannelProductSku_getProductId(postSku), postSku.getId(),
			null, null);

		assertEquals(postSku, getSku);
		assertValid(getSku);
	}

	protected Sku testGetChannelProductSku_addSku() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductSku_getChannelId() throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductSku_getProductId(Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelProductSku() throws Exception {
		Sku sku = testGraphQLGetChannelProductSku_addSku();

		// No namespace

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"channelProductSku",
								new HashMap<String, Object>() {
									{
										put(
											"channelId",
											testGraphQLGetChannelProductSku_getChannelId());

										put(
											"productId",
											testGraphQLGetChannelProductSku_getProductId(
												sku));
										put("skuId", sku.getId());
									}
								},
								getGraphQLFields())),
						"JSONObject/data", "Object/channelProductSku"))));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertTrue(
			equals(
				sku,
				SkuSerDes.toDTO(
					JSONUtil.getValueAsString(
						invokeGraphQLQuery(
							new GraphQLField(
								"headlessCommerceDeliveryCatalog_v1_0",
								new GraphQLField(
									"channelProductSku",
									new HashMap<String, Object>() {
										{
											put(
												"channelId",
												testGraphQLGetChannelProductSku_getChannelId());

											put(
												"productId",
												testGraphQLGetChannelProductSku_getProductId(
													sku));
											put("skuId", sku.getId());
										}
									},
									getGraphQLFields()))),
						"JSONObject/data",
						"JSONObject/headlessCommerceDeliveryCatalog_v1_0",
						"Object/channelProductSku"))));
	}

	protected Long testGraphQLGetChannelProductSku_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGraphQLGetChannelProductSku_getProductId(Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testGraphQLGetChannelProductSkuNotFound() throws Exception {
		Long irrelevantChannelId = RandomTestUtil.randomLong();
		Long irrelevantProductId = RandomTestUtil.randomLong();
		Long irrelevantSkuId = RandomTestUtil.randomLong();

		// No namespace

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"channelProductSku",
						new HashMap<String, Object>() {
							{
								put("channelId", irrelevantChannelId);
								put("productId", irrelevantProductId);
								put("skuId", irrelevantSkuId);
							}
						},
						getGraphQLFields())),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));

		// Using the namespace headlessCommerceDeliveryCatalog_v1_0

		Assert.assertEquals(
			"Not Found",
			JSONUtil.getValueAsString(
				invokeGraphQLQuery(
					new GraphQLField(
						"headlessCommerceDeliveryCatalog_v1_0",
						new GraphQLField(
							"channelProductSku",
							new HashMap<String, Object>() {
								{
									put("channelId", irrelevantChannelId);
									put("productId", irrelevantProductId);
									put("skuId", irrelevantSkuId);
								}
							},
							getGraphQLFields()))),
				"JSONArray/errors", "Object/0", "JSONObject/extensions",
				"Object/code"));
	}

	protected Sku testGraphQLGetChannelProductSku_addSku() throws Exception {
		return testGraphQLSku_addSku();
	}

	@Test
	public void testGetChannelProductSkusPage() throws Exception {
		Long channelId = testGetChannelProductSkusPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelProductSkusPage_getIrrelevantChannelId();
		Long productId = testGetChannelProductSkusPage_getProductId();
		Long irrelevantProductId =
			testGetChannelProductSkusPage_getIrrelevantProductId();

		Page<Sku> page = skuResource.getChannelProductSkusPage(
			channelId, productId, null, RandomTestUtil.randomString(),
			Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelId != null) && (irrelevantProductId != null)) {
			Sku irrelevantSku = testGetChannelProductSkusPage_addSku(
				irrelevantChannelId, irrelevantProductId,
				randomIrrelevantSku());

			page = skuResource.getChannelProductSkusPage(
				irrelevantChannelId, irrelevantProductId, null, null,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(irrelevantSku, (List<Sku>)page.getItems());
			assertValid(
				page,
				testGetChannelProductSkusPage_getExpectedActions(
					irrelevantChannelId, irrelevantProductId));
		}

		Sku sku1 = testGetChannelProductSkusPage_addSku(
			channelId, productId, randomSku());

		Sku sku2 = testGetChannelProductSkusPage_addSku(
			channelId, productId, randomSku());

		page = skuResource.getChannelProductSkusPage(
			channelId, productId, null, null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(sku1, (List<Sku>)page.getItems());
		assertContains(sku2, (List<Sku>)page.getItems());
		assertValid(
			page,
			testGetChannelProductSkusPage_getExpectedActions(
				channelId, productId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelProductSkusPage_getExpectedActions(
				Long channelId, Long productId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelProductSkusPageWithPagination() throws Exception {
		Long channelId = testGetChannelProductSkusPage_getChannelId();
		Long productId = testGetChannelProductSkusPage_getProductId();

		Page<Sku> skusPage = skuResource.getChannelProductSkusPage(
			channelId, productId, null, null, null);

		int totalCount = GetterUtil.getInteger(skusPage.getTotalCount());

		Sku sku1 = testGetChannelProductSkusPage_addSku(
			channelId, productId, randomSku());

		Sku sku2 = testGetChannelProductSkusPage_addSku(
			channelId, productId, randomSku());

		Sku sku3 = testGetChannelProductSkusPage_addSku(
			channelId, productId, randomSku());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<Sku> page1 = skuResource.getChannelProductSkusPage(
				channelId, productId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
					pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(sku1, (List<Sku>)page1.getItems());

			Page<Sku> page2 = skuResource.getChannelProductSkusPage(
				channelId, productId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(sku2, (List<Sku>)page2.getItems());

			Page<Sku> page3 = skuResource.getChannelProductSkusPage(
				channelId, productId, null, null,
				Pagination.of(
					(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
					pageSizeLimit));

			assertContains(sku3, (List<Sku>)page3.getItems());
		}
		else {
			Page<Sku> page1 = skuResource.getChannelProductSkusPage(
				channelId, productId, null, null,
				Pagination.of(1, totalCount + 2));

			List<Sku> skus1 = (List<Sku>)page1.getItems();

			Assert.assertEquals(skus1.toString(), totalCount + 2, skus1.size());

			Page<Sku> page2 = skuResource.getChannelProductSkusPage(
				channelId, productId, null, null,
				Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<Sku> skus2 = (List<Sku>)page2.getItems();

			Assert.assertEquals(skus2.toString(), 1, skus2.size());

			Page<Sku> page3 = skuResource.getChannelProductSkusPage(
				channelId, productId, null, null,
				Pagination.of(1, (int)totalCount + 3));

			assertContains(sku1, (List<Sku>)page3.getItems());
			assertContains(sku2, (List<Sku>)page3.getItems());
			assertContains(sku3, (List<Sku>)page3.getItems());
		}
	}

	protected Sku testGetChannelProductSkusPage_addSku(
			Long channelId, Long productId, Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductSkusPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductSkusPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected Long testGetChannelProductSkusPage_getProductId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductSkusPage_getIrrelevantProductId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSku()
		throws Exception {

		Sku randomSku = randomSku();

		Sku postSku =
			testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSku_addSku(
				randomSku);

		assertEquals(randomSku, postSku);
		assertValid(postSku);
	}

	protected Sku
			testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSku_addSku(
				Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOption()
		throws Exception {

		Sku randomSku = randomSku();

		Sku postSku =
			testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOption_addSku(
				randomSku);

		assertEquals(randomSku, postSku);
		assertValid(postSku);
	}

	protected Sku
			testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeSkuBySkuOption_addSku(
				Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelProductSku() throws Exception {
		Sku randomSku = randomSku();

		Sku postSku = testPostChannelProductSku_addSku(randomSku);

		assertEquals(randomSku, postSku);
		assertValid(postSku);
	}

	protected Sku testPostChannelProductSku_addSku(Sku sku) throws Exception {
		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testPostChannelProductSkuBySkuOption() throws Exception {
		Sku randomSku = randomSku();

		Sku postSku = testPostChannelProductSkuBySkuOption_addSku(randomSku);

		assertEquals(randomSku, postSku);
		assertValid(postSku);
	}

	protected Sku testPostChannelProductSkuBySkuOption_addSku(Sku sku)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

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

			if (Objects.equals("DDMOptions", additionalAssertFieldName)) {
				if (sku.getDDMOptions() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"allowedOrderQuantities", additionalAssertFieldName)) {

				if (sku.getAllowedOrderQuantities() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("availability", additionalAssertFieldName)) {
				if (sku.getAvailability() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("backOrderAllowed", additionalAssertFieldName)) {
				if (sku.getBackOrderAllowed() == null) {
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

			if (Objects.equals(
					"displayDiscountLevels", additionalAssertFieldName)) {

				if (sku.getDisplayDiscountLevels() == null) {
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

			if (Objects.equals(
					"incomingQuantityLabel", additionalAssertFieldName)) {

				if (sku.getIncomingQuantityLabel() == null) {
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

			if (Objects.equals("maxOrderQuantity", additionalAssertFieldName)) {
				if (sku.getMaxOrderQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("minOrderQuantity", additionalAssertFieldName)) {
				if (sku.getMinOrderQuantity() == null) {
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

			if (Objects.equals(
					"productConfiguration", additionalAssertFieldName)) {

				if (sku.getProductConfiguration() == null) {
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

			if (Objects.equals("replacementSku", additionalAssertFieldName)) {
				if (sku.getReplacementSku() == null) {
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
					"skuUnitOfMeasures", additionalAssertFieldName)) {

				if (sku.getSkuUnitOfMeasures() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("tierPrices", additionalAssertFieldName)) {
				if (sku.getTierPrices() == null) {
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

		for (java.lang.reflect.Field field :
				getDeclaredFields(
					com.liferay.headless.commerce.delivery.catalog.dto.v1_0.Sku.
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

			if (Objects.equals("DDMOptions", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getDDMOptions(), sku2.getDDMOptions())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"allowedOrderQuantities", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getAllowedOrderQuantities(),
						sku2.getAllowedOrderQuantities())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("availability", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getAvailability(), sku2.getAvailability())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("backOrderAllowed", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getBackOrderAllowed(),
						sku2.getBackOrderAllowed())) {

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

			if (Objects.equals(
					"displayDiscountLevels", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getDisplayDiscountLevels(),
						sku2.getDisplayDiscountLevels())) {

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

			if (Objects.equals(
					"incomingQuantityLabel", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getIncomingQuantityLabel(),
						sku2.getIncomingQuantityLabel())) {

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

			if (Objects.equals("maxOrderQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getMaxOrderQuantity(),
						sku2.getMaxOrderQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("minOrderQuantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getMinOrderQuantity(),
						sku2.getMinOrderQuantity())) {

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

			if (Objects.equals(
					"productConfiguration", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getProductConfiguration(),
						sku2.getProductConfiguration())) {

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

			if (Objects.equals("replacementSku", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getReplacementSku(), sku2.getReplacementSku())) {

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
					"skuUnitOfMeasures", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						sku1.getSkuUnitOfMeasures(),
						sku2.getSkuUnitOfMeasures())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("tierPrices", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						sku1.getTierPrices(), sku2.getTierPrices())) {

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

		if (entityFieldName.equals("DDMOptions")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("allowedOrderQuantities")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("availability")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("backOrderAllowed")) {
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

		if (entityFieldName.equals("displayDiscountLevels")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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

		if (entityFieldName.equals("incomingQuantityLabel")) {
			Object object = sku.getIncomingQuantityLabel();

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

		if (entityFieldName.equals("maxOrderQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("minOrderQuantity")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("neverExpire")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productConfiguration")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("productId")) {
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

		if (entityFieldName.equals("replacementSku")) {
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

		if (entityFieldName.equals("skuUnitOfMeasures")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("tierPrices")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
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
				backOrderAllowed = RandomTestUtil.randomBoolean();
				depth = RandomTestUtil.randomDouble();
				discontinued = RandomTestUtil.randomBoolean();
				discontinuedDate = RandomTestUtil.nextDate();
				displayDate = RandomTestUtil.nextDate();
				displayDiscountLevels = RandomTestUtil.randomBoolean();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				gtin = StringUtil.toLowerCase(RandomTestUtil.randomString());
				height = RandomTestUtil.randomDouble();
				id = RandomTestUtil.randomLong();
				incomingQuantityLabel = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
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

	protected SkuResource skuResource;
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
		com.liferay.headless.commerce.delivery.catalog.resource.v1_0.SkuResource
			_skuResource;

}