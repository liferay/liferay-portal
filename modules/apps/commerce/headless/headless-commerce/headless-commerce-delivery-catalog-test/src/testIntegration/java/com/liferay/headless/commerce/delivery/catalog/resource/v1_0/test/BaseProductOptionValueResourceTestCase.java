/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
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

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.ProductOptionValueResource;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.ProductOptionValueSerDes;
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
 * @author Andrea Sbarra
 * @generated
 */
@Generated("")
public abstract class BaseProductOptionValueResourceTestCase {

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

		_productOptionValueResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productOptionValueResource = ProductOptionValueResource.builder(
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

		ProductOptionValue productOptionValue1 = randomProductOptionValue();

		String json = objectMapper.writeValueAsString(productOptionValue1);

		ProductOptionValue productOptionValue2 = ProductOptionValueSerDes.toDTO(
			json);

		Assert.assertTrue(equals(productOptionValue1, productOptionValue2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductOptionValue productOptionValue = randomProductOptionValue();

		String json1 = objectMapper.writeValueAsString(productOptionValue);
		String json2 = ProductOptionValueSerDes.toJSON(productOptionValue);

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

		ProductOptionValue productOptionValue = randomProductOptionValue();

		productOptionValue.setInfoMessage(regex);
		productOptionValue.setKey(regex);
		productOptionValue.setName(regex);
		productOptionValue.setPrice(regex);
		productOptionValue.setPriceType(regex);
		productOptionValue.setQuantity(regex);
		productOptionValue.setRelativePriceFormatted(regex);
		productOptionValue.setTotalPrice(regex);
		productOptionValue.setUnitOfMeasureKey(regex);

		String json = ProductOptionValueSerDes.toJSON(productOptionValue);

		Assert.assertFalse(json.contains(regex));

		productOptionValue = ProductOptionValueSerDes.toDTO(json);

		Assert.assertEquals(regex, productOptionValue.getInfoMessage());
		Assert.assertEquals(regex, productOptionValue.getKey());
		Assert.assertEquals(regex, productOptionValue.getName());
		Assert.assertEquals(regex, productOptionValue.getPrice());
		Assert.assertEquals(regex, productOptionValue.getPriceType());
		Assert.assertEquals(regex, productOptionValue.getQuantity());
		Assert.assertEquals(
			regex, productOptionValue.getRelativePriceFormatted());
		Assert.assertEquals(regex, productOptionValue.getTotalPrice());
		Assert.assertEquals(regex, productOptionValue.getUnitOfMeasureKey());
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getChannelExternalReferenceCode();
		String irrelevantChannelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getIrrelevantChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getProductExternalReferenceCode();
		String irrelevantProductExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getIrrelevantProductExternalReferenceCode();
		String productOptionExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getProductOptionExternalReferenceCode();
		String irrelevantProductOptionExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getIrrelevantProductOptionExternalReferenceCode();

		Page<ProductOptionValue> page =
			productOptionValueResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					productOptionExternalReferenceCode, null,
					RandomTestUtil.randomString(), null, null,
					Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelExternalReferenceCode != null) &&
			(irrelevantProductExternalReferenceCode != null) &&
			(irrelevantProductOptionExternalReferenceCode != null)) {

			ProductOptionValue irrelevantProductOptionValue =
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode,
					irrelevantProductOptionExternalReferenceCode,
					randomIrrelevantProductOptionValue());

			page =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						irrelevantChannelExternalReferenceCode,
						irrelevantProductExternalReferenceCode,
						irrelevantProductOptionExternalReferenceCode, null,
						null, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOptionValue,
				(List<ProductOptionValue>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getExpectedActions(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode,
					irrelevantProductOptionExternalReferenceCode));
		}

		ProductOptionValue productOptionValue1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
				channelExternalReferenceCode, productExternalReferenceCode,
				productOptionExternalReferenceCode, randomProductOptionValue());

		ProductOptionValue productOptionValue2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
				channelExternalReferenceCode, productExternalReferenceCode,
				productOptionExternalReferenceCode, randomProductOptionValue());

		page =
			productOptionValueResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					productOptionExternalReferenceCode, null, null, null, null,
					Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productOptionValue1, (List<ProductOptionValue>)page.getItems());
		assertContains(
			productOptionValue2, (List<ProductOptionValue>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getExpectedActions(
				channelExternalReferenceCode, productExternalReferenceCode,
				productOptionExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getExpectedActions(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				String productOptionExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPageWithPagination()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getProductExternalReferenceCode();
		String productOptionExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getProductOptionExternalReferenceCode();

		Page<ProductOptionValue> productOptionValuesPage =
			productOptionValueResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					productOptionExternalReferenceCode, null, null, null, null,
					null);

		int totalCount = GetterUtil.getInteger(
			productOptionValuesPage.getTotalCount());

		ProductOptionValue productOptionValue1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
				channelExternalReferenceCode, productExternalReferenceCode,
				productOptionExternalReferenceCode, randomProductOptionValue());

		ProductOptionValue productOptionValue2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
				channelExternalReferenceCode, productExternalReferenceCode,
				productOptionExternalReferenceCode, randomProductOptionValue());

		ProductOptionValue productOptionValue3 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
				channelExternalReferenceCode, productExternalReferenceCode,
				productOptionExternalReferenceCode, randomProductOptionValue());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOptionValue> page1 =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)page1.getItems());

			Page<ProductOptionValue> page2 =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)page2.getItems());

			Page<ProductOptionValue> page3 =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productOptionValue3,
				(List<ProductOptionValue>)page3.getItems());
		}
		else {
			Page<ProductOptionValue> page1 =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, null, null, null,
						null, Pagination.of(1, totalCount + 2));

			List<ProductOptionValue> productOptionValues1 =
				(List<ProductOptionValue>)page1.getItems();

			Assert.assertEquals(
				productOptionValues1.toString(), totalCount + 2,
				productOptionValues1.size());

			Page<ProductOptionValue> page2 =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, null, null, null,
						null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOptionValue> productOptionValues2 =
				(List<ProductOptionValue>)page2.getItems();

			Assert.assertEquals(
				productOptionValues2.toString(), 1,
				productOptionValues2.size());

			Page<ProductOptionValue> page3 =
				productOptionValueResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						productOptionExternalReferenceCode, null, null, null,
						null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)page3.getItems());
			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)page3.getItems());
			assertContains(
				productOptionValue3,
				(List<ProductOptionValue>)page3.getItems());
		}
	}

	protected ProductOptionValue
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_addProductOptionValue(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				String productOptionExternalReferenceCode,
				ProductOptionValue productOptionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getIrrelevantChannelExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getProductExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getIrrelevantProductExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getProductOptionExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage_getIrrelevantProductOptionExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelProductProductOptionProductOptionValuesPage()
		throws Exception {

		Long channelId =
			testGetChannelProductProductOptionProductOptionValuesPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelProductProductOptionProductOptionValuesPage_getIrrelevantChannelId();
		Long productId =
			testGetChannelProductProductOptionProductOptionValuesPage_getProductId();
		Long irrelevantProductId =
			testGetChannelProductProductOptionProductOptionValuesPage_getIrrelevantProductId();
		Long productOptionId =
			testGetChannelProductProductOptionProductOptionValuesPage_getProductOptionId();
		Long irrelevantProductOptionId =
			testGetChannelProductProductOptionProductOptionValuesPage_getIrrelevantProductOptionId();

		Page<ProductOptionValue> page =
			productOptionValueResource.
				getChannelProductProductOptionProductOptionValuesPage(
					channelId, productId, productOptionId, null,
					RandomTestUtil.randomString(), null, null,
					Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelId != null) && (irrelevantProductId != null) &&
			(irrelevantProductOptionId != null)) {

			ProductOptionValue irrelevantProductOptionValue =
				testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
					irrelevantChannelId, irrelevantProductId,
					irrelevantProductOptionId,
					randomIrrelevantProductOptionValue());

			page =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						irrelevantChannelId, irrelevantProductId,
						irrelevantProductOptionId, null, null, null, null,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOptionValue,
				(List<ProductOptionValue>)page.getItems());
			assertValid(
				page,
				testGetChannelProductProductOptionProductOptionValuesPage_getExpectedActions(
					irrelevantChannelId, irrelevantProductId,
					irrelevantProductOptionId));
		}

		ProductOptionValue productOptionValue1 =
			testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
				channelId, productId, productOptionId,
				randomProductOptionValue());

		ProductOptionValue productOptionValue2 =
			testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
				channelId, productId, productOptionId,
				randomProductOptionValue());

		page =
			productOptionValueResource.
				getChannelProductProductOptionProductOptionValuesPage(
					channelId, productId, productOptionId, null, null, null,
					null, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(
			productOptionValue1, (List<ProductOptionValue>)page.getItems());
		assertContains(
			productOptionValue2, (List<ProductOptionValue>)page.getItems());
		assertValid(
			page,
			testGetChannelProductProductOptionProductOptionValuesPage_getExpectedActions(
				channelId, productId, productOptionId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelProductProductOptionProductOptionValuesPage_getExpectedActions(
				Long channelId, Long productId, Long productOptionId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelProductProductOptionProductOptionValuesPageWithPagination()
		throws Exception {

		Long channelId =
			testGetChannelProductProductOptionProductOptionValuesPage_getChannelId();
		Long productId =
			testGetChannelProductProductOptionProductOptionValuesPage_getProductId();
		Long productOptionId =
			testGetChannelProductProductOptionProductOptionValuesPage_getProductOptionId();

		Page<ProductOptionValue> productOptionValuesPage =
			productOptionValueResource.
				getChannelProductProductOptionProductOptionValuesPage(
					channelId, productId, productOptionId, null, null, null,
					null, null);

		int totalCount = GetterUtil.getInteger(
			productOptionValuesPage.getTotalCount());

		ProductOptionValue productOptionValue1 =
			testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
				channelId, productId, productOptionId,
				randomProductOptionValue());

		ProductOptionValue productOptionValue2 =
			testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
				channelId, productId, productOptionId,
				randomProductOptionValue());

		ProductOptionValue productOptionValue3 =
			testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
				channelId, productId, productOptionId,
				randomProductOptionValue());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOptionValue> page1 =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)page1.getItems());

			Page<ProductOptionValue> page2 =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)page2.getItems());

			Page<ProductOptionValue> page3 =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, null, null, null,
						null,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productOptionValue3,
				(List<ProductOptionValue>)page3.getItems());
		}
		else {
			Page<ProductOptionValue> page1 =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, null, null, null,
						null, Pagination.of(1, totalCount + 2));

			List<ProductOptionValue> productOptionValues1 =
				(List<ProductOptionValue>)page1.getItems();

			Assert.assertEquals(
				productOptionValues1.toString(), totalCount + 2,
				productOptionValues1.size());

			Page<ProductOptionValue> page2 =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, null, null, null,
						null, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOptionValue> productOptionValues2 =
				(List<ProductOptionValue>)page2.getItems();

			Assert.assertEquals(
				productOptionValues2.toString(), 1,
				productOptionValues2.size());

			Page<ProductOptionValue> page3 =
				productOptionValueResource.
					getChannelProductProductOptionProductOptionValuesPage(
						channelId, productId, productOptionId, null, null, null,
						null, Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productOptionValue1,
				(List<ProductOptionValue>)page3.getItems());
			assertContains(
				productOptionValue2,
				(List<ProductOptionValue>)page3.getItems());
			assertContains(
				productOptionValue3,
				(List<ProductOptionValue>)page3.getItems());
		}
	}

	protected ProductOptionValue
			testGetChannelProductProductOptionProductOptionValuesPage_addProductOptionValue(
				Long channelId, Long productId, Long productOptionId,
				ProductOptionValue productOptionValue)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelProductProductOptionProductOptionValuesPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelProductProductOptionProductOptionValuesPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected Long
			testGetChannelProductProductOptionProductOptionValuesPage_getProductId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelProductProductOptionProductOptionValuesPage_getIrrelevantProductId()
		throws Exception {

		return null;
	}

	protected Long
			testGetChannelProductProductOptionProductOptionValuesPage_getProductOptionId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelProductProductOptionProductOptionValuesPage_getIrrelevantProductOptionId()
		throws Exception {

		return null;
	}

	@Test
	public void testPostChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionByExternalReferenceCodeProductOptionExternalReferenceCodeProductOptionValuesPage()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testPostChannelProductProductOptionProductOptionValuesPage()
		throws Exception {

		Assert.assertTrue(false);
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		ProductOptionValue productOptionValue,
		List<ProductOptionValue> productOptionValues) {

		boolean contains = false;

		for (ProductOptionValue item : productOptionValues) {
			if (equals(productOptionValue, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productOptionValues + " does not contain " + productOptionValue,
			contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductOptionValue productOptionValue1,
		ProductOptionValue productOptionValue2) {

		Assert.assertTrue(
			productOptionValue1 + " does not equal " + productOptionValue2,
			equals(productOptionValue1, productOptionValue2));
	}

	protected void assertEquals(
		List<ProductOptionValue> productOptionValues1,
		List<ProductOptionValue> productOptionValues2) {

		Assert.assertEquals(
			productOptionValues1.size(), productOptionValues2.size());

		for (int i = 0; i < productOptionValues1.size(); i++) {
			ProductOptionValue productOptionValue1 = productOptionValues1.get(
				i);
			ProductOptionValue productOptionValue2 = productOptionValues2.get(
				i);

			assertEquals(productOptionValue1, productOptionValue2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductOptionValue> productOptionValues1,
		List<ProductOptionValue> productOptionValues2) {

		Assert.assertEquals(
			productOptionValues1.size(), productOptionValues2.size());

		for (ProductOptionValue productOptionValue1 : productOptionValues1) {
			boolean contains = false;

			for (ProductOptionValue productOptionValue2 :
					productOptionValues2) {

				if (equals(productOptionValue1, productOptionValue2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productOptionValues2 + " does not contain " +
					productOptionValue1,
				contains);
		}
	}

	protected void assertValid(ProductOptionValue productOptionValue)
		throws Exception {

		boolean valid = true;

		if (productOptionValue.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("infoMessage", additionalAssertFieldName)) {
				if (productOptionValue.getInfoMessage() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (productOptionValue.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (productOptionValue.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("preselected", additionalAssertFieldName)) {
				if (productOptionValue.getPreselected() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (productOptionValue.getPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priceType", additionalAssertFieldName)) {
				if (productOptionValue.getPriceType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productOptionValue.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("productOptionId", additionalAssertFieldName)) {
				if (productOptionValue.getProductOptionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (productOptionValue.getQuantity() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"relativePriceFormatted", additionalAssertFieldName)) {

				if (productOptionValue.getRelativePriceFormatted() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("selectable", additionalAssertFieldName)) {
				if (productOptionValue.getSelectable() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (productOptionValue.getSkuId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("totalPrice", additionalAssertFieldName)) {
				if (productOptionValue.getTotalPrice() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (productOptionValue.getUnitOfMeasureKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("visible", additionalAssertFieldName)) {
				if (productOptionValue.getVisible() == null) {
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

	protected void assertValid(Page<ProductOptionValue> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductOptionValue> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductOptionValue> productOptionValues =
			page.getItems();

		int size = productOptionValues.size();

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
					com.liferay.headless.commerce.delivery.catalog.dto.v1_0.
						ProductOptionValue.class)) {

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
		ProductOptionValue productOptionValue1,
		ProductOptionValue productOptionValue2) {

		if (productOptionValue1 == productOptionValue2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getId(),
						productOptionValue2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("infoMessage", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getInfoMessage(),
						productOptionValue2.getInfoMessage())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getKey(),
						productOptionValue2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getName(),
						productOptionValue2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("preselected", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getPreselected(),
						productOptionValue2.getPreselected())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("price", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getPrice(),
						productOptionValue2.getPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priceType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getPriceType(),
						productOptionValue2.getPriceType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getPriority(),
						productOptionValue2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("productOptionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getProductOptionId(),
						productOptionValue2.getProductOptionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("quantity", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getQuantity(),
						productOptionValue2.getQuantity())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"relativePriceFormatted", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOptionValue1.getRelativePriceFormatted(),
						productOptionValue2.getRelativePriceFormatted())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("selectable", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getSelectable(),
						productOptionValue2.getSelectable())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getSkuId(),
						productOptionValue2.getSkuId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("totalPrice", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getTotalPrice(),
						productOptionValue2.getTotalPrice())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("unitOfMeasureKey", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getUnitOfMeasureKey(),
						productOptionValue2.getUnitOfMeasureKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("visible", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOptionValue1.getVisible(),
						productOptionValue2.getVisible())) {

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

		if (!(_productOptionValueResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productOptionValueResource;

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
		ProductOptionValue productOptionValue) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("id")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("infoMessage")) {
			Object object = productOptionValue.getInfoMessage();

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

		if (entityFieldName.equals("key")) {
			Object object = productOptionValue.getKey();

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
			Object object = productOptionValue.getName();

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

		if (entityFieldName.equals("preselected")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("price")) {
			Object object = productOptionValue.getPrice();

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

		if (entityFieldName.equals("priceType")) {
			Object object = productOptionValue.getPriceType();

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

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(productOptionValue.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("productOptionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("quantity")) {
			Object object = productOptionValue.getQuantity();

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

		if (entityFieldName.equals("relativePriceFormatted")) {
			Object object = productOptionValue.getRelativePriceFormatted();

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

		if (entityFieldName.equals("selectable")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("totalPrice")) {
			Object object = productOptionValue.getTotalPrice();

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
			Object object = productOptionValue.getUnitOfMeasureKey();

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

	protected ProductOptionValue randomProductOptionValue() throws Exception {
		return new ProductOptionValue() {
			{
				id = RandomTestUtil.randomLong();
				infoMessage = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				preselected = RandomTestUtil.randomBoolean();
				price = StringUtil.toLowerCase(RandomTestUtil.randomString());
				priceType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				priority = RandomTestUtil.randomDouble();
				productOptionId = RandomTestUtil.randomLong();
				quantity = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				relativePriceFormatted = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				selectable = RandomTestUtil.randomBoolean();
				skuId = RandomTestUtil.randomLong();
				totalPrice = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				visible = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ProductOptionValue randomIrrelevantProductOptionValue()
		throws Exception {

		ProductOptionValue randomIrrelevantProductOptionValue =
			randomProductOptionValue();

		return randomIrrelevantProductOptionValue;
	}

	protected ProductOptionValue randomPatchProductOptionValue()
		throws Exception {

		return randomProductOptionValue();
	}

	protected ProductOptionValueResource productOptionValueResource;
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
		LogFactoryUtil.getLog(BaseProductOptionValueResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.catalog.resource.v1_0.
		ProductOptionValueResource _productOptionValueResource;

}