/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.ProductOptionValue;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class ProductOptionValueResourceTest
	extends BaseProductOptionValueResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testCompany.getCompanyId());

		_user = UserTestUtil.addUser(testCompany);

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testCompany.getCompanyId(), testCompany.getGroupId(),
			_user.getUserId(), _commerceCurrency.getCode());

		_cpDefinition = CPTestUtil.addCPDefinitionFromCatalog(
			_commerceCatalog.getGroupId(), SimpleCPTypeConstants.NAME, false,
			false);

		_cpOption = CPTestUtil.addCPOption(testGroup.getGroupId(), false);

		_cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
				_cpDefinition.getCPDefinitionId(), _cpOption.getCPOptionId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				CPConstants.PRODUCT_OPTION_DATE_KEY,
				RandomTestUtil.randomDouble(), false, false, false, false,
				"static",
				ServiceContextTestUtil.getServiceContext(
					testCompany.getCompanyId(), testGroup.getGroupId(),
					_user.getUserId()));

		_cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);
	}

	@Ignore
	@Override
	@Test
	public void testBatchEngineDeleteImportTask() throws Exception {
		super.testBatchEngineDeleteImportTask();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProductOptionValue() throws Exception {
		super.testDeleteProductOptionValue();
	}

	@Ignore
	@Override
	@Test
	public void testDeleteProductOptionValueBatch() throws Exception {
		super.testDeleteProductOptionValueBatch();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductOptionIdProductOptionValuesPage()
		throws Exception {

		super.testGetProductOptionIdProductOptionValuesPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductOptionIdProductOptionValuesPageWithPagination()
		throws Exception {

		super.testGetProductOptionIdProductOptionValuesPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGetProductOptionValue() throws Exception {
		super.testGetProductOptionValue();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteProductOptionValue() throws Exception {
		super.testGraphQLDeleteProductOptionValue();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductOptionValue() throws Exception {
		super.testGraphQLGetProductOptionValue();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetProductOptionValueNotFound() throws Exception {
		super.testGraphQLGetProductOptionValueNotFound();
	}

	@Override
	@Test
	public void testPatchProductOptionValue() throws Exception {
		super.testPatchProductOptionValue();

		_testPatchProductOptionValueWithSkuExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostProductOptionIdProductOptionValue() throws Exception {
		super.testPostProductOptionIdProductOptionValue();

		_testPostProductOptionIdProductOptionValueWithSkuExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
	}

	@Override
	protected ProductOptionValue randomPatchProductOptionValue()
		throws Exception {

		return new ProductOptionValue() {
			{
				id = RandomTestUtil.randomLong();
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				preselected = RandomTestUtil.randomBoolean();
				priority = RandomTestUtil.randomDouble();
				quantity = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				skuExternalReferenceCode =
					_cpInstance.getExternalReferenceCode();
				skuId = _cpInstance.getCPInstanceId();
				unitOfMeasureKey = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	@Override
	protected ProductOptionValue randomProductOptionValue() throws Exception {
		return new ProductOptionValue() {
			{
				deltaPrice = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				quantity = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				skuExternalReferenceCode =
					_cpInstance.getExternalReferenceCode();
				skuId = _cpInstance.getCPInstanceId();
			}
		};
	}

	@Ignore
	@Override
	@Test
	protected ProductOptionValue
			testDeleteProductOptionValue_addProductOptionValue()
		throws Exception {

		return super.testDeleteProductOptionValue_addProductOptionValue();
	}

	@Override
	protected ProductOptionValue
			testPatchProductOptionValue_addProductOptionValue()
		throws Exception {

		return productOptionValueResource.postProductOptionIdProductOptionValue(
			_cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			randomProductOptionValue());
	}

	@Override
	protected ProductOptionValue
			testPostProductOptionIdProductOptionValue_addProductOptionValue(
				ProductOptionValue productOptionValue)
		throws Exception {

		return productOptionValueResource.postProductOptionIdProductOptionValue(
			_cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			productOptionValue);
	}

	private void _testPatchProductOptionValueWithSkuExternalReferenceCode()
		throws Exception {

		ProductOptionValue postProductOptionValue =
			testPatchProductOptionValue_addProductOptionValue();

		ProductOptionValue randomPatchProductOptionValue =
			randomProductOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		long cpInstanceId = cpInstance.getCPInstanceId();

		randomPatchProductOptionValue.setSkuExternalReferenceCode(
			cpInstance.getExternalReferenceCode());

		randomPatchProductOptionValue.setSkuId(0L);

		ProductOptionValue patchProductOptionValue =
			productOptionValueResource.patchProductOptionValue(
				postProductOptionValue.getId(), randomPatchProductOptionValue);

		randomPatchProductOptionValue.setSkuId(cpInstanceId);

		ProductOptionValue expectedPatchProductOptionValue =
			postProductOptionValue.clone();

		BeanTestUtil.copyProperties(
			randomPatchProductOptionValue, expectedPatchProductOptionValue);

		ProductOptionValue getProductOptionValue =
			productOptionValueResource.getProductOptionValue(
				patchProductOptionValue.getId());

		assertEquals(expectedPatchProductOptionValue, getProductOptionValue);
		assertValid(getProductOptionValue);
		Assert.assertEquals(
			getProductOptionValue.getSkuExternalReferenceCode(),
			randomPatchProductOptionValue.getSkuExternalReferenceCode());
		Assert.assertEquals(
			cpInstanceId, GetterUtil.getLong(getProductOptionValue.getSkuId()));
	}

	private void _testPostProductOptionIdProductOptionValueWithSkuExternalReferenceCode()
		throws Exception {

		ProductOptionValue randomProductOptionValue =
			randomProductOptionValue();

		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		long cpInstanceId = cpInstance.getCPInstanceId();

		randomProductOptionValue.setSkuExternalReferenceCode(
			cpInstance.getExternalReferenceCode());

		randomProductOptionValue.setSkuId(0L);

		ProductOptionValue postProductOptionValue =
			testPostProductOptionIdProductOptionValue_addProductOptionValue(
				randomProductOptionValue);

		randomProductOptionValue.setSkuId(cpInstanceId);

		assertEquals(randomProductOptionValue, postProductOptionValue);
		assertValid(postProductOptionValue);
		Assert.assertEquals(
			randomProductOptionValue.getSkuExternalReferenceCode(),
			postProductOptionValue.getSkuExternalReferenceCode());
		Assert.assertEquals(
			cpInstanceId,
			GetterUtil.getLong(postProductOptionValue.getSkuId()));
	}

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@DeleteAfterTestRun
	private CPDefinitionOptionRel _cpDefinitionOptionRel;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private CPOption _cpOption;

	@DeleteAfterTestRun
	private User _user;

}