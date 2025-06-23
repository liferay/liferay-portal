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

import com.liferay.headless.commerce.delivery.catalog.client.dto.v1_0.ProductOption;
import com.liferay.headless.commerce.delivery.catalog.client.http.HttpInvoker;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Page;
import com.liferay.headless.commerce.delivery.catalog.client.pagination.Pagination;
import com.liferay.headless.commerce.delivery.catalog.client.resource.v1_0.ProductOptionResource;
import com.liferay.headless.commerce.delivery.catalog.client.serdes.v1_0.ProductOptionSerDes;
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
public abstract class BaseProductOptionResourceTestCase {

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

		_productOptionResource.setContextCompany(testCompany);

		_testCompanyAdminUser = UserTestUtil.getAdminUser(
			testCompany.getCompanyId());

		productOptionResource = ProductOptionResource.builder(
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

		ProductOption productOption1 = randomProductOption();

		String json = objectMapper.writeValueAsString(productOption1);

		ProductOption productOption2 = ProductOptionSerDes.toDTO(json);

		Assert.assertTrue(equals(productOption1, productOption2));
	}

	@Test
	public void testClientSerDesToJSON() throws Exception {
		ObjectMapper objectMapper = getClientSerDesObjectMapper();

		ProductOption productOption = randomProductOption();

		String json1 = objectMapper.writeValueAsString(productOption);
		String json2 = ProductOptionSerDes.toJSON(productOption);

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

		ProductOption productOption = randomProductOption();

		productOption.setDescription(regex);
		productOption.setFieldType(regex);
		productOption.setKey(regex);
		productOption.setName(regex);
		productOption.setOptionExternalReferenceCode(regex);

		String json = ProductOptionSerDes.toJSON(productOption);

		Assert.assertFalse(json.contains(regex));

		productOption = ProductOptionSerDes.toDTO(json);

		Assert.assertEquals(regex, productOption.getDescription());
		Assert.assertEquals(regex, productOption.getFieldType());
		Assert.assertEquals(regex, productOption.getKey());
		Assert.assertEquals(regex, productOption.getName());
		Assert.assertEquals(
			regex, productOption.getOptionExternalReferenceCode());
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getChannelExternalReferenceCode();
		String irrelevantChannelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getIrrelevantChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getProductExternalReferenceCode();
		String irrelevantProductExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getIrrelevantProductExternalReferenceCode();

		Page<ProductOption> page =
			productOptionResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelExternalReferenceCode != null) &&
			(irrelevantProductExternalReferenceCode != null)) {

			ProductOption irrelevantProductOption =
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode,
					randomIrrelevantProductOption());

			page =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						irrelevantChannelExternalReferenceCode,
						irrelevantProductExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOption, (List<ProductOption>)page.getItems());
			assertValid(
				page,
				testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getExpectedActions(
					irrelevantChannelExternalReferenceCode,
					irrelevantProductExternalReferenceCode));
		}

		ProductOption productOption1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomProductOption());

		ProductOption productOption2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomProductOption());

		page =
			productOptionResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(productOption1, (List<ProductOption>)page.getItems());
		assertContains(productOption2, (List<ProductOption>)page.getItems());
		assertValid(
			page,
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getExpectedActions(
				channelExternalReferenceCode, productExternalReferenceCode));
	}

	protected Map<String, Map<String, String>>
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getExpectedActions(
				String channelExternalReferenceCode,
				String productExternalReferenceCode)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPageWithPagination()
		throws Exception {

		String channelExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getChannelExternalReferenceCode();
		String productExternalReferenceCode =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getProductExternalReferenceCode();

		Page<ProductOption> productOptionsPage =
			productOptionResource.
				getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
					channelExternalReferenceCode, productExternalReferenceCode,
					null);

		int totalCount = GetterUtil.getInteger(
			productOptionsPage.getTotalCount());

		ProductOption productOption1 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomProductOption());

		ProductOption productOption2 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomProductOption());

		ProductOption productOption3 =
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
				channelExternalReferenceCode, productExternalReferenceCode,
				randomProductOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOption> page1 =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
							pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOption1, (List<ProductOption>)page1.getItems());

			Page<ProductOption> page2 =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productOption2, (List<ProductOption>)page2.getItems());

			Page<ProductOption> page3 =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(
							(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
							pageSizeLimit));

			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
		else {
			Page<ProductOption> page1 =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(1, totalCount + 2));

			List<ProductOption> productOptions1 =
				(List<ProductOption>)page1.getItems();

			Assert.assertEquals(
				productOptions1.toString(), totalCount + 2,
				productOptions1.size());

			Page<ProductOption> page2 =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOption> productOptions2 =
				(List<ProductOption>)page2.getItems();

			Assert.assertEquals(
				productOptions2.toString(), 1, productOptions2.size());

			Page<ProductOption> page3 =
				productOptionResource.
					getChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage(
						channelExternalReferenceCode,
						productExternalReferenceCode,
						Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productOption1, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption2, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
	}

	protected ProductOption
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_addProductOption(
				String channelExternalReferenceCode,
				String productExternalReferenceCode,
				ProductOption productOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getChannelExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getIrrelevantChannelExternalReferenceCode()
		throws Exception {

		return null;
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getProductExternalReferenceCode()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected String
			testGetChannelByExternalReferenceCodeChannelExternalReferenceCodeProductByExternalReferenceCodeProductExternalReferenceCodeProductOptionsPage_getIrrelevantProductExternalReferenceCode()
		throws Exception {

		return null;
	}

	@Test
	public void testGetChannelProductProductOptionsPage() throws Exception {
		Long channelId = testGetChannelProductProductOptionsPage_getChannelId();
		Long irrelevantChannelId =
			testGetChannelProductProductOptionsPage_getIrrelevantChannelId();
		Long productId = testGetChannelProductProductOptionsPage_getProductId();
		Long irrelevantProductId =
			testGetChannelProductProductOptionsPage_getIrrelevantProductId();

		Page<ProductOption> page =
			productOptionResource.getChannelProductProductOptionsPage(
				channelId, productId, Pagination.of(1, 10));

		long totalCount = page.getTotalCount();

		if ((irrelevantChannelId != null) && (irrelevantProductId != null)) {
			ProductOption irrelevantProductOption =
				testGetChannelProductProductOptionsPage_addProductOption(
					irrelevantChannelId, irrelevantProductId,
					randomIrrelevantProductOption());

			page = productOptionResource.getChannelProductProductOptionsPage(
				irrelevantChannelId, irrelevantProductId,
				Pagination.of(1, (int)totalCount + 1));

			Assert.assertEquals(totalCount + 1, page.getTotalCount());

			assertContains(
				irrelevantProductOption, (List<ProductOption>)page.getItems());
			assertValid(
				page,
				testGetChannelProductProductOptionsPage_getExpectedActions(
					irrelevantChannelId, irrelevantProductId));
		}

		ProductOption productOption1 =
			testGetChannelProductProductOptionsPage_addProductOption(
				channelId, productId, randomProductOption());

		ProductOption productOption2 =
			testGetChannelProductProductOptionsPage_addProductOption(
				channelId, productId, randomProductOption());

		page = productOptionResource.getChannelProductProductOptionsPage(
			channelId, productId, Pagination.of(1, 10));

		Assert.assertEquals(totalCount + 2, page.getTotalCount());

		assertContains(productOption1, (List<ProductOption>)page.getItems());
		assertContains(productOption2, (List<ProductOption>)page.getItems());
		assertValid(
			page,
			testGetChannelProductProductOptionsPage_getExpectedActions(
				channelId, productId));
	}

	protected Map<String, Map<String, String>>
			testGetChannelProductProductOptionsPage_getExpectedActions(
				Long channelId, Long productId)
		throws Exception {

		Map<String, Map<String, String>> expectedActions = new HashMap<>();

		return expectedActions;
	}

	@Test
	public void testGetChannelProductProductOptionsPageWithPagination()
		throws Exception {

		Long channelId = testGetChannelProductProductOptionsPage_getChannelId();
		Long productId = testGetChannelProductProductOptionsPage_getProductId();

		Page<ProductOption> productOptionsPage =
			productOptionResource.getChannelProductProductOptionsPage(
				channelId, productId, null);

		int totalCount = GetterUtil.getInteger(
			productOptionsPage.getTotalCount());

		ProductOption productOption1 =
			testGetChannelProductProductOptionsPage_addProductOption(
				channelId, productId, randomProductOption());

		ProductOption productOption2 =
			testGetChannelProductProductOptionsPage_addProductOption(
				channelId, productId, randomProductOption());

		ProductOption productOption3 =
			testGetChannelProductProductOptionsPage_addProductOption(
				channelId, productId, randomProductOption());

		// See com.liferay.portal.vulcan.internal.configuration.HeadlessAPICompanyConfiguration#pageSizeLimit

		int pageSizeLimit = 500;

		if (totalCount >= (pageSizeLimit - 2)) {
			Page<ProductOption> page1 =
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId,
					Pagination.of(
						(int)Math.ceil((totalCount + 1.0) / pageSizeLimit),
						pageSizeLimit));

			Assert.assertEquals(totalCount + 3, page1.getTotalCount());

			assertContains(
				productOption1, (List<ProductOption>)page1.getItems());

			Page<ProductOption> page2 =
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId,
					Pagination.of(
						(int)Math.ceil((totalCount + 2.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				productOption2, (List<ProductOption>)page2.getItems());

			Page<ProductOption> page3 =
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId,
					Pagination.of(
						(int)Math.ceil((totalCount + 3.0) / pageSizeLimit),
						pageSizeLimit));

			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
		else {
			Page<ProductOption> page1 =
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId, Pagination.of(1, totalCount + 2));

			List<ProductOption> productOptions1 =
				(List<ProductOption>)page1.getItems();

			Assert.assertEquals(
				productOptions1.toString(), totalCount + 2,
				productOptions1.size());

			Page<ProductOption> page2 =
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId, Pagination.of(2, totalCount + 2));

			Assert.assertEquals(totalCount + 3, page2.getTotalCount());

			List<ProductOption> productOptions2 =
				(List<ProductOption>)page2.getItems();

			Assert.assertEquals(
				productOptions2.toString(), 1, productOptions2.size());

			Page<ProductOption> page3 =
				productOptionResource.getChannelProductProductOptionsPage(
					channelId, productId,
					Pagination.of(1, (int)totalCount + 3));

			assertContains(
				productOption1, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption2, (List<ProductOption>)page3.getItems());
			assertContains(
				productOption3, (List<ProductOption>)page3.getItems());
		}
	}

	protected ProductOption
			testGetChannelProductProductOptionsPage_addProductOption(
				Long channelId, Long productId, ProductOption productOption)
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long testGetChannelProductProductOptionsPage_getChannelId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelProductProductOptionsPage_getIrrelevantChannelId()
		throws Exception {

		return null;
	}

	protected Long testGetChannelProductProductOptionsPage_getProductId()
		throws Exception {

		throw new UnsupportedOperationException(
			"This method needs to be implemented");
	}

	protected Long
			testGetChannelProductProductOptionsPage_getIrrelevantProductId()
		throws Exception {

		return null;
	}

	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		Assert.assertTrue(true);
	}

	protected void assertContains(
		ProductOption productOption, List<ProductOption> productOptions) {

		boolean contains = false;

		for (ProductOption item : productOptions) {
			if (equals(productOption, item)) {
				contains = true;

				break;
			}
		}

		Assert.assertTrue(
			productOptions + " does not contain " + productOption, contains);
	}

	protected void assertHttpResponseStatusCode(
		int expectedHttpResponseStatusCode,
		HttpInvoker.HttpResponse actualHttpResponse) {

		Assert.assertEquals(
			expectedHttpResponseStatusCode, actualHttpResponse.getStatusCode());
	}

	protected void assertEquals(
		ProductOption productOption1, ProductOption productOption2) {

		Assert.assertTrue(
			productOption1 + " does not equal " + productOption2,
			equals(productOption1, productOption2));
	}

	protected void assertEquals(
		List<ProductOption> productOptions1,
		List<ProductOption> productOptions2) {

		Assert.assertEquals(productOptions1.size(), productOptions2.size());

		for (int i = 0; i < productOptions1.size(); i++) {
			ProductOption productOption1 = productOptions1.get(i);
			ProductOption productOption2 = productOptions2.get(i);

			assertEquals(productOption1, productOption2);
		}
	}

	protected void assertEqualsIgnoringOrder(
		List<ProductOption> productOptions1,
		List<ProductOption> productOptions2) {

		Assert.assertEquals(productOptions1.size(), productOptions2.size());

		for (ProductOption productOption1 : productOptions1) {
			boolean contains = false;

			for (ProductOption productOption2 : productOptions2) {
				if (equals(productOption1, productOption2)) {
					contains = true;

					break;
				}
			}

			Assert.assertTrue(
				productOptions2 + " does not contain " + productOption1,
				contains);
		}
	}

	protected void assertValid(ProductOption productOption) throws Exception {
		boolean valid = true;

		if (productOption.getId() == null) {
			valid = false;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (productOption.getCatalogId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (productOption.getDescription() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("fieldType", additionalAssertFieldName)) {
				if (productOption.getFieldType() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (productOption.getKey() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (productOption.getName() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"optionExternalReferenceCode", additionalAssertFieldName)) {

				if (productOption.getOptionExternalReferenceCode() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("optionId", additionalAssertFieldName)) {
				if (productOption.getOptionId() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (productOption.getPriority() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals(
					"productOptionValues", additionalAssertFieldName)) {

				if (productOption.getProductOptionValues() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (productOption.getRequired() == null) {
					valid = false;
				}

				continue;
			}

			if (Objects.equals("skuContributor", additionalAssertFieldName)) {
				if (productOption.getSkuContributor() == null) {
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

	protected void assertValid(Page<ProductOption> page) {
		assertValid(page, Collections.emptyMap());
	}

	protected void assertValid(
		Page<ProductOption> page,
		Map<String, Map<String, String>> expectedActions) {

		boolean valid = false;

		java.util.Collection<ProductOption> productOptions = page.getItems();

		int size = productOptions.size();

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
						ProductOption.class)) {

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
		ProductOption productOption1, ProductOption productOption2) {

		if (productOption1 == productOption2) {
			return true;
		}

		for (String additionalAssertFieldName :
				getAdditionalAssertFieldNames()) {

			if (Objects.equals("catalogId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getCatalogId(),
						productOption2.getCatalogId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("description", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getDescription(),
						productOption2.getDescription())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("fieldType", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getFieldType(),
						productOption2.getFieldType())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("id", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getId(), productOption2.getId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("key", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getKey(), productOption2.getKey())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("name", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getName(), productOption2.getName())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"optionExternalReferenceCode", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOption1.getOptionExternalReferenceCode(),
						productOption2.getOptionExternalReferenceCode())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("optionId", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getOptionId(),
						productOption2.getOptionId())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("priority", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getPriority(),
						productOption2.getPriority())) {

					return false;
				}

				continue;
			}

			if (Objects.equals(
					"productOptionValues", additionalAssertFieldName)) {

				if (!Objects.deepEquals(
						productOption1.getProductOptionValues(),
						productOption2.getProductOptionValues())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("required", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getRequired(),
						productOption2.getRequired())) {

					return false;
				}

				continue;
			}

			if (Objects.equals("skuContributor", additionalAssertFieldName)) {
				if (!Objects.deepEquals(
						productOption1.getSkuContributor(),
						productOption2.getSkuContributor())) {

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

		if (!(_productOptionResource instanceof EntityModelResource)) {
			throw new UnsupportedOperationException(
				"Resource is not an instance of EntityModelResource");
		}

		EntityModelResource entityModelResource =
			(EntityModelResource)_productOptionResource;

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
		EntityField entityField, String operator, ProductOption productOption) {

		StringBundler sb = new StringBundler();

		String entityFieldName = entityField.getName();

		sb.append(entityFieldName);

		sb.append(" ");
		sb.append(operator);
		sb.append(" ");

		if (entityFieldName.equals("catalogId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("description")) {
			Object object = productOption.getDescription();

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

		if (entityFieldName.equals("fieldType")) {
			Object object = productOption.getFieldType();

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
			Object object = productOption.getKey();

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
			Object object = productOption.getName();

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

		if (entityFieldName.equals("optionExternalReferenceCode")) {
			Object object = productOption.getOptionExternalReferenceCode();

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

		if (entityFieldName.equals("optionId")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("priority")) {
			sb.append(String.valueOf(productOption.getPriority()));

			return sb.toString();
		}

		if (entityFieldName.equals("productOptionValues")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("required")) {
			throw new IllegalArgumentException(
				"Invalid entity field " + entityFieldName);
		}

		if (entityFieldName.equals("skuContributor")) {
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

	protected ProductOption randomProductOption() throws Exception {
		return new ProductOption() {
			{
				catalogId = RandomTestUtil.randomLong();
				description = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				fieldType = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				optionExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				optionId = RandomTestUtil.randomLong();
				priority = RandomTestUtil.randomDouble();
				required = RandomTestUtil.randomBoolean();
				skuContributor = RandomTestUtil.randomBoolean();
			}
		};
	}

	protected ProductOption randomIrrelevantProductOption() throws Exception {
		ProductOption randomIrrelevantProductOption = randomProductOption();

		return randomIrrelevantProductOption;
	}

	protected ProductOption randomPatchProductOption() throws Exception {
		return randomProductOption();
	}

	protected ProductOptionResource productOptionResource;
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
		LogFactoryUtil.getLog(BaseProductOptionResourceTestCase.class);

	private static Format _format;

	private com.liferay.portal.kernel.model.User _testCompanyAdminUser;

	@Inject
	private com.liferay.headless.commerce.delivery.catalog.resource.v1_0.
		ProductOptionResource _productOptionResource;

}