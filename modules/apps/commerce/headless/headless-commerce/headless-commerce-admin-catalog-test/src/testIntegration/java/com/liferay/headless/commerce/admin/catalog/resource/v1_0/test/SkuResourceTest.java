/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.catalog.resource.v1_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.product.constants.CPConstants;
import com.liferay.commerce.product.helper.CPInstanceHelper;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPDefinitionLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionRelLocalService;
import com.liferay.commerce.product.service.CPDefinitionOptionValueRelLocalService;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalService;
import com.liferay.commerce.product.service.CPOptionLocalService;
import com.liferay.commerce.product.service.CPOptionValueLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.Sku;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuOption;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuUnitOfMeasure;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuVirtualSettings;
import com.liferay.headless.commerce.admin.catalog.client.dto.v1_0.SkuVirtualSettingsFileEntry;
import com.liferay.headless.commerce.admin.catalog.client.resource.v1_0.SkuResource;
import com.liferay.headless.commerce.core.util.LanguageUtils;
import com.liferay.journal.constants.JournalFolderConstants;
import com.liferay.journal.model.JournalArticle;
import com.liferay.journal.service.JournalArticleLocalService;
import com.liferay.journal.test.util.JournalTestUtil;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.GroupLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.PropsValues;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;
import java.math.RoundingMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class SkuResourceTest extends BaseSkuResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_cpDefinition = CPTestUtil.addCPDefinition(
			testGroup.getGroupId(), "virtual", true, false);

		_user = UserTestUtil.addUser(testCompany);

		ServiceContext serviceContext =
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId());

		_cpOption = _cpOptionLocalService.addCPOption(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			CPConstants.PRODUCT_OPTION_SELECT_KEY, false, false, false,
			RandomTestUtil.randomString(), serviceContext);

		_cpOptionValue = _cpOptionValueLocalService.addCPOptionValue(
			_cpOption.getCPOptionId(), RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.nextDouble(), RandomTestUtil.randomString(),
			serviceContext);

		_cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.addCPDefinitionOptionRel(
				_cpDefinition.getCPDefinitionId(), _cpOption.getCPOptionId(),
				serviceContext);

		_cpDefinitionOptionValueRels =
			_cpDefinitionOptionRel.getCPDefinitionOptionValueRels();

		_cProduct = _cpDefinition.getCProduct();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		super.tearDown();

		List<CPInstance> cpInstances = _cpInstanceLocalService.getCPInstances(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		for (CPInstance cpInstance : cpInstances) {
			_cpInstanceLocalService.deleteCPInstance(cpInstance);
		}
	}

	@Ignore
	@Override
	@Test
	public void testGetUnitOfMeasureSkusPage() throws Exception {
		super.testGetUnitOfMeasureSkusPage();
	}

	@Ignore
	@Override
	@Test
	public void testGetUnitOfMeasureSkusPageWithPagination() throws Exception {
		super.testGetUnitOfMeasureSkusPageWithPagination();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteSku() throws Exception {
		super.testGraphQLDeleteSku();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeleteSkuByExternalReferenceCode() throws Exception {
		super.testGraphQLDeleteSkuByExternalReferenceCode();
	}

	@Override
	@Test
	public void testPatchSku() throws Exception {
		super.testPatchSku();

		_testPatchSkuExternalReferenceCode();
		_testPatchSkuWithReplacementSku();
		_testPatchSkuWithPricing();
		_testPatchSkuWithShipping();
		_testPatchSkuWithUnitOfMeasure();
	}

	@Override
	@Test
	public void testPostProductIdSku() throws Exception {
		super.testPostProductIdSku();

		_testPostProductIdSkuWithLazyReferencedOptionExternalReferenceCode();
		_testPostProductIdSkuWithLazyReferencedParentOptionExternalReferenceCode();
		_testPostProductIdSkuWithNonexistentOptionExternalReferenceCode();
		_testPostProductIdSkuWithOptionExternalReferenceCode();
		_testPostProductIdSkuWithOptionId();
		_testPostProductIdSkuWithOptionIdKey();
		_testPostProductIdSkuWithOptionKey();
		_testPostProductIdSkuWithSkuVirtualSettings();
		_testPostProductIdSkuWithSkuVirtualSettingsWhenLazyReferencingEnabled();
		_testPostProductIdSkuWithTermsOfUseJournalArticleExternalReferenceCode();
		_testPostProductIdSkuWithTermsOfUseJournalArticleGroupExternalReferenceCode();
	}

	@Override
	@Test
	public void testPutSkuByExternalReferenceCode() throws Exception {
		super.testPutSkuByExternalReferenceCode();

		_testPutSkuByExternalReferenceCodeWithLazyReferences();
		_testPutSkuByExternalReferenceCodeWithUnitOfMeasures();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"depth", "discontinued", "gtin", "height", "manufacturerPartNumber",
			"published", "purchasable", "sku", "unspsc", "weight", "width"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {"catalogId"};
	}

	@Override
	protected Sku randomSku() throws Exception {
		return new Sku() {
			{
				depth = RandomTestUtil.randomDouble();
				discontinued = false;
				discontinuedDate = RandomTestUtil.nextDate();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				gtin = StringUtil.toLowerCase(RandomTestUtil.randomString());
				height = RandomTestUtil.randomDouble();
				inventoryLevel = RandomTestUtil.randomInt();
				manufacturerPartNumber = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				neverExpire = true;
				published = true;
				purchasable = true;
				sku = StringUtil.toLowerCase(RandomTestUtil.randomString());
				unspsc = StringUtil.toLowerCase(RandomTestUtil.randomString());
				weight = RandomTestUtil.randomDouble();
				width = RandomTestUtil.randomDouble();
			}
		};
	}

	@Override
	protected Sku testDeleteSku_addSku() throws Exception {
		return skuResource.postProductIdSku(
			_cProduct.getCProductId(), randomSku());
	}

	@Override
	protected Sku testDeleteSkuByExternalReferenceCode_addSku()
		throws Exception {

		return skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), randomSku());
	}

	@Override
	protected Sku testGetProductByExternalReferenceCodeSkusPage_addSku(
			String externalReferenceCode, Sku sku)
		throws Exception {

		return skuResource.postProductByExternalReferenceCodeSku(
			externalReferenceCode, sku);
	}

	@Override
	protected String
			testGetProductByExternalReferenceCodeSkusPage_getExternalReferenceCode()
		throws Exception {

		return _cProduct.getExternalReferenceCode();
	}

	@Override
	protected Sku testGetProductIdSkusPage_addSku(Long id, Sku sku)
		throws Exception {

		return skuResource.postProductIdSku(id, sku);
	}

	@Override
	protected Long testGetProductIdSkusPage_getId() throws Exception {
		return _cProduct.getCProductId();
	}

	@Override
	protected Sku testGetSku_addSku() throws Exception {
		return skuResource.postProductIdSku(
			_cProduct.getCProductId(), randomSku());
	}

	@Override
	protected Sku testGetSkuByExternalReferenceCode_addSku() throws Exception {
		return skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), randomSku());
	}

	@Override
	protected Sku testGetSkusPage_addSku(Sku sku) throws Exception {
		return skuResource.postProductIdSku(_cProduct.getCProductId(), sku);
	}

	@Override
	protected Sku testGetUnitOfMeasureSkusPage_addSku(Sku sku)
		throws Exception {

		return skuResource.postProductIdSku(_cProduct.getCProductId(), sku);
	}

	@Override
	protected Sku testGraphQLSku_addSku() throws Exception {
		return skuResource.postProductIdSku(
			_cProduct.getCProductId(), randomSku());
	}

	@Override
	protected Sku testPatchSku_addSku() throws Exception {
		return skuResource.postProductIdSku(
			_cProduct.getCProductId(), randomSku());
	}

	@Override
	protected Sku testPatchSkuByExternalReferenceCode_addSku()
		throws Exception {

		return skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), randomSku());
	}

	@Override
	protected Sku testPostProductByExternalReferenceCodeSku_addSku(Sku sku)
		throws Exception {

		return skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), sku);
	}

	@Override
	protected Sku testPostProductIdSku_addSku(Sku sku) throws Exception {
		return skuResource.postProductIdSku(_cProduct.getCProductId(), sku);
	}

	@Override
	protected Sku testPutSkuByExternalReferenceCode_addSku() throws Exception {
		return skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), randomSku());
	}

	@Override
	protected Sku testPutSkuByExternalReferenceCode_createSku()
		throws Exception {

		return skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), randomSku());
	}

	private void _assertCPInstanceOptionValueRel(
		CPDefinitionOptionRel cpDefinitionOptionRel,
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel, Sku sku) {

		List<CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_cpInstanceHelper.getCPInstanceCPInstanceOptionValueRels(
				sku.getId());

		Assert.assertEquals(
			cpInstanceOptionValueRels.toString(), 1,
			cpInstanceOptionValueRels.size());

		CPInstanceOptionValueRel cpInstanceOptionValueRel =
			cpInstanceOptionValueRels.get(0);

		Assert.assertEquals(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpInstanceOptionValueRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
			cpInstanceOptionValueRel.getCPDefinitionOptionValueRelId());
	}

	private void _assertSkuUnitOfMeasures(Sku expectedSku, Sku sku) {
		SkuUnitOfMeasure[] expectedSkuUnitOfMeasures =
			expectedSku.getSkuUnitOfMeasures();
		SkuUnitOfMeasure[] skuUnitOfMeasures = sku.getSkuUnitOfMeasures();

		Assert.assertEquals(
			Arrays.toString(skuUnitOfMeasures),
			expectedSkuUnitOfMeasures.length, skuUnitOfMeasures.length);

		Map<String, SkuUnitOfMeasure> skuUnitOfMeasuresMap = new HashMap<>();

		for (SkuUnitOfMeasure skuUnitOfMeasure : skuUnitOfMeasures) {
			skuUnitOfMeasuresMap.put(
				skuUnitOfMeasure.getKey(), skuUnitOfMeasure);
		}

		for (SkuUnitOfMeasure expectedSkuUnitOfMeasure :
				expectedSkuUnitOfMeasures) {

			SkuUnitOfMeasure skuUnitOfMeasure = skuUnitOfMeasuresMap.get(
				expectedSkuUnitOfMeasure.getKey());

			Assert.assertEquals(
				expectedSkuUnitOfMeasure.getPrimary(),
				skuUnitOfMeasure.getPrimary());

			BigDecimal basePrice = expectedSkuUnitOfMeasure.getBasePrice();

			Assert.assertEquals(
				basePrice.setScale(2, RoundingMode.HALF_UP),
				skuUnitOfMeasure.getBasePrice());
		}
	}

	private CommercePriceEntry _getCommercePriceEntry(
		CPInstance cpInstance, String priceListType, String uomKey) {

		return _commercePriceEntryLocalService.
			getInstanceBaseCommercePriceEntry(
				cpInstance.getCPInstanceUuid(), priceListType, uomKey);
	}

	private SkuVirtualSettings _postSkuWithTermsOfUse(
			String groupExternalReferenceCode, JournalArticle journalArticle)
		throws Exception {

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		SkuResource skuResource = SkuResource.builder(
		).authentication(
			adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "skuVirtualSettings"
		).build();

		Sku randomSku = randomSku();

		randomSku.setSkuVirtualSettings(
			new SkuVirtualSettings() {
				{
					activationStatus = 0;
					duration = RandomTestUtil.nextLong();
					maxUsages = RandomTestUtil.nextInt();
					override = true;
					termsOfUseJournalArticleExternalReferenceCode =
						journalArticle.getExternalReferenceCode();
					termsOfUseJournalArticleGroupExternalReferenceCode =
						groupExternalReferenceCode;
					termsOfUseRequired = true;
					url = "https://liferay.com";
				}
			});

		Sku postSku = skuResource.postProductIdSku(
			_cpDefinition.getCProductId(), randomSku);

		return postSku.getSkuVirtualSettings();
	}

	private SkuUnitOfMeasure _randomSkuUnitOfMeasure(boolean primaryValue) {
		return new SkuUnitOfMeasure() {
			{
				active = true;
				basePrice = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				incrementalOrderQuantity = BigDecimal.valueOf(
					RandomTestUtil.randomInt(1, 10));
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				name = LanguageUtils.getLanguageIdMap(
					RandomTestUtil.randomLocaleStringMap());
				precision = 0;
				primary = primaryValue;
				priority = RandomTestUtil.randomDouble();
				promoPrice = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				rate = BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10));
			}
		};
	}

	private Sku _randomSkuWithParentOptionExternalReferenceCode(
			String optionKey, String optionKeyExternalReferenceCode,
			String optionValueKeyExternalReferenceCode,
			String parentOptionKeyExternalReferenceCode)
		throws Exception {

		Sku sku = randomSku();

		sku.setSkuOptions(
			() -> new SkuOption[] {
				new SkuOption() {
					{
						key = optionKey;
						optionExternalReferenceCode =
							optionKeyExternalReferenceCode;
						optionValueExternalReferenceCode =
							optionValueKeyExternalReferenceCode;
						parentOptionExternalReferenceCode =
							parentOptionKeyExternalReferenceCode;
						parentOptionFieldType =
							CPConstants.PRODUCT_OPTION_SELECT_KEY;
						parentOptionSkuContributor = true;
					}
				}
			});

		return sku;
	}

	private Sku _randomSkuWithSkuOptions(
			String optionKey, String optionKeyExternalReferenceCode,
			Long optionKeyId, String optionValueKeyExternalReferenceCode,
			Long optionValueKeyId, String optionValue)
		throws Exception {

		Sku sku = randomSku();

		sku.setSkuOptions(
			() -> new SkuOption[] {
				new SkuOption() {
					{
						key = optionKey;
						optionExternalReferenceCode =
							optionKeyExternalReferenceCode;
						optionId = optionKeyId;
						optionValueExternalReferenceCode =
							optionValueKeyExternalReferenceCode;
						optionValueId = optionValueKeyId;
						value = optionValue;
					}
				}
			});

		return sku;
	}

	private Sku _randomSkuWithUnitOfMeasures() throws Exception {
		Sku sku = randomSku();

		sku.setProductExternalReferenceCode(
			_cProduct::getExternalReferenceCode);
		sku.setSkuUnitOfMeasures(
			() -> new SkuUnitOfMeasure[] {
				_randomSkuUnitOfMeasure(true), _randomSkuUnitOfMeasure(false)
			});

		return sku;
	}

	private void _testPatchSkuExternalReferenceCode() throws Exception {
		Sku sku = testPatchSku_addSku();

		Sku randomSku = new Sku() {
			{
				externalReferenceCode = RandomTestUtil.randomString();
			}
		};

		Sku patchSku = skuResource.patchSku(sku.getId(), randomSku);

		Assert.assertEquals(
			patchSku.getExternalReferenceCode(),
			randomSku.getExternalReferenceCode());
		assertValid(patchSku);
	}

	private void _testPatchSkuWithPricing() throws Exception {
		Sku sku = testPatchSku_addSku();

		Sku randomSku = new Sku() {
			{
				cost = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				price = BigDecimal.valueOf(RandomTestUtil.randomDouble());
				promoPrice = BigDecimal.valueOf(RandomTestUtil.randomDouble());
			}
		};

		Sku patchSku = skuResource.patchSku(sku.getId(), randomSku);

		Assert.assertEquals(patchSku.getCost(), randomSku.getCost());
		Assert.assertEquals(patchSku.getPrice(), randomSku.getPrice());
		Assert.assertEquals(
			patchSku.getPromoPrice(), randomSku.getPromoPrice());

		CommercePriceEntry commercePriceEntry = _getCommercePriceEntry(
			_cpInstanceLocalService.fetchCPInstance(sku.getId()),
			CommercePriceListConstants.TYPE_PRICE_LIST, null);

		Assert.assertEquals(BigDecimal.ZERO, commercePriceEntry.getPrice());

		assertValid(patchSku);
	}

	private void _testPatchSkuWithReplacementSku() throws Exception {
		Sku sku1 = testPatchSku_addSku();
		Sku sku2 = testPatchSku_addSku();

		Sku patchSku1 = skuResource.patchSku(
			sku1.getId(),
			new Sku() {
				{
					discontinued = true;
					discontinuedDate = RandomTestUtil.nextDate();
					replacementSkuExternalReferenceCode =
						sku2.getExternalReferenceCode();
					replacementSkuId = sku2.getId();
				}
			});

		Assert.assertTrue(patchSku1.getDiscontinued());
		Assert.assertEquals(
			sku2.getExternalReferenceCode(),
			patchSku1.getReplacementSkuExternalReferenceCode());
		Assert.assertEquals(sku2.getId(), patchSku1.getReplacementSkuId());

		assertValid(patchSku1);

		Sku patchSku2 = skuResource.patchSku(
			patchSku1.getId(),
			new Sku() {
				{
					discontinued = false;
				}
			});

		Assert.assertFalse(patchSku2.getDiscontinued());
		Assert.assertNull(patchSku2.getReplacementSkuExternalReferenceCode());
		Assert.assertNull(patchSku2.getReplacementSkuId());

		assertValid(patchSku2);
	}

	private void _testPatchSkuWithShipping() throws Exception {
		Sku sku = testPatchSku_addSku();

		Sku randomSku = new Sku() {
			{
				depth = RandomTestUtil.randomDouble();
				height = RandomTestUtil.randomDouble();
				weight = RandomTestUtil.randomDouble();
				width = RandomTestUtil.randomDouble();
			}
		};

		Sku patchSku = skuResource.patchSku(sku.getId(), randomSku);

		Assert.assertEquals(patchSku.getDepth(), randomSku.getDepth());
		Assert.assertEquals(patchSku.getHeight(), randomSku.getHeight());
		Assert.assertEquals(patchSku.getWeight(), randomSku.getWeight());
		Assert.assertEquals(patchSku.getWidth(), randomSku.getWidth());

		assertValid(patchSku);
	}

	private void _testPatchSkuWithUnitOfMeasure() throws Exception {
		Sku sku = testPatchSku_addSku();

		SkuUnitOfMeasure randomSkuUnitOfMeasure = _randomSkuUnitOfMeasure(true);

		SkuUnitOfMeasure[] randomSkuUnitOfMeasureArray = {
			randomSkuUnitOfMeasure
		};

		sku.setSkuUnitOfMeasures(randomSkuUnitOfMeasureArray);

		Sku patchSku = skuResource.patchSku(sku.getId(), sku);

		SkuUnitOfMeasure[] skuUnitOfMeasures = patchSku.getSkuUnitOfMeasures();

		Assert.assertTrue(
			(skuUnitOfMeasures != null) && (skuUnitOfMeasures.length == 1));

		BigDecimal randomSkuUnitOfMeasureBasePrice =
			randomSkuUnitOfMeasure.getBasePrice();
		SkuUnitOfMeasure skuUnitOfMeasure = skuUnitOfMeasures[0];

		Assert.assertEquals(
			skuUnitOfMeasure.getBasePrice(),
			randomSkuUnitOfMeasureBasePrice.setScale(2, RoundingMode.HALF_UP));
		Assert.assertEquals(
			skuUnitOfMeasure.getKey(), randomSkuUnitOfMeasure.getKey());
		Assert.assertEquals(
			skuUnitOfMeasure.getPriority(),
			randomSkuUnitOfMeasure.getPriority());

		BigDecimal randomSkuUnitOfMeasurePromoPrice =
			randomSkuUnitOfMeasure.getPromoPrice();

		Assert.assertEquals(
			skuUnitOfMeasure.getPromoPrice(),
			randomSkuUnitOfMeasurePromoPrice.setScale(2, RoundingMode.HALF_UP));

		assertValid(patchSku);
	}

	private void _testPostProductIdSkuWithLazyReferencedOptionExternalReferenceCode()
		throws Exception {

		CPOption cpOption = _cpOptionLocalService.addCPOption(
			RandomTestUtil.randomString(), _user.getUserId(),
			RandomTestUtil.randomLocaleStringMap(),
			RandomTestUtil.randomLocaleStringMap(),
			CPConstants.PRODUCT_OPTION_SELECT_KEY, false, false, false,
			RandomTestUtil.randomString(),
			ServiceContextTestUtil.getServiceContext(
				testCompany.getCompanyId(), testGroup.getGroupId(),
				_user.getUserId()));

		String externalReferenceCode = RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			skuResource.postProductIdSku(
				_cpDefinition.getCProductId(),
				_randomSkuWithSkuOptions(
					cpOption.getKey(), externalReferenceCode, null, null, null,
					null));
		}

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelByExternalReferenceCode(
					externalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpDefinitionOptionRel.getStatus());
		Assert.assertEquals(
			_cpDefinition.getCPDefinitionId(),
			cpDefinitionOptionRel.getCPDefinitionId());
		Assert.assertEquals(
			cpOption.getCPOptionId(), cpDefinitionOptionRel.getCPOptionId());
	}

	private void _testPostProductIdSkuWithLazyReferencedParentOptionExternalReferenceCode()
		throws Exception {

		Sku postSku = null;

		String optionExternalReferenceCode = RandomTestUtil.randomString();
		String optionValueExternalReferenceCode = RandomTestUtil.randomString();
		String parentOptionExternalReferenceCode =
			RandomTestUtil.randomString();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postSku = skuResource.postProductIdSku(
				_cpDefinition.getCProductId(),
				_randomSkuWithParentOptionExternalReferenceCode(
					RandomTestUtil.randomString(), optionExternalReferenceCode,
					optionValueExternalReferenceCode,
					parentOptionExternalReferenceCode));
		}

		CPOption cpOption =
			_cpOptionLocalService.getCPOptionByExternalReferenceCode(
				parentOptionExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			CPConstants.PRODUCT_OPTION_SELECT_KEY,
			cpOption.getCommerceOptionTypeKey());
		Assert.assertEquals(
			StringUtil.toLowerCase(parentOptionExternalReferenceCode),
			cpOption.getKey());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpOption.getStatus());
		Assert.assertTrue(cpOption.isSkuContributor());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelByExternalReferenceCode(
					optionExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			_cpDefinition.getCPDefinitionId(),
			cpDefinitionOptionRel.getCPDefinitionId());
		Assert.assertEquals(
			cpOption.getCPOptionId(), cpDefinitionOptionRel.getCPOptionId());
		Assert.assertEquals(
			CPConstants.PRODUCT_OPTION_SELECT_KEY,
			cpDefinitionOptionRel.getCommerceOptionTypeKey());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpDefinitionOptionRel.getStatus());
		Assert.assertTrue(cpDefinitionOptionRel.isSkuContributor());

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRelByExternalReferenceCode(
					optionValueExternalReferenceCode,
					testCompany.getCompanyId());

		Assert.assertEquals(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());

		_assertCPInstanceOptionValueRel(
			cpDefinitionOptionRel, cpDefinitionOptionValueRel, postSku);
	}

	private void _testPostProductIdSkuWithNonexistentOptionExternalReferenceCode()
		throws Exception {

		assertHttpResponseStatusCode(
			404,
			skuResource.postProductIdSkuHttpResponse(
				_cpDefinition.getCProductId(),
				_randomSkuWithSkuOptions(
					null, RandomTestUtil.randomString(), null, null, null,
					null)));
	}

	private void _testPostProductIdSkuWithOptionExternalReferenceCode()
		throws Exception {

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRels.get(0);

		Sku postSku = skuResource.postProductIdSku(
			_cpDefinition.getCProductId(),
			_randomSkuWithSkuOptions(
				null, _cpDefinitionOptionRel.getExternalReferenceCode(), null,
				cpDefinitionOptionValueRel.getExternalReferenceCode(), null,
				null));

		SkuOption[] skuOptions = postSku.getSkuOptions();

		Assert.assertTrue((skuOptions != null) && (skuOptions.length == 1));

		SkuOption skuOption = skuOptions[0];

		Assert.assertEquals(skuOption.getKey(), _cpOption.getKey());
		Assert.assertEquals(
			skuOption.getOptionExternalReferenceCode(),
			_cpDefinitionOptionRel.getExternalReferenceCode());
		Assert.assertEquals(
			(long)skuOption.getOptionId(),
			_cpDefinitionOptionRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			skuOption.getOptionValueExternalReferenceCode(),
			cpDefinitionOptionValueRel.getExternalReferenceCode());
		Assert.assertEquals(
			(long)skuOption.getOptionValueId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());
		Assert.assertEquals(skuOption.getValue(), _cpOptionValue.getKey());

		_assertCPInstanceOptionValueRel(
			_cpDefinitionOptionRel, cpDefinitionOptionValueRel, postSku);
	}

	private void _testPostProductIdSkuWithOptionId() throws Exception {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRels.get(0);

		Sku postSku = skuResource.postProductIdSku(
			_cpDefinition.getCProductId(),
			_randomSkuWithSkuOptions(
				null, null, _cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
				null,
				cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId(),
				null));

		SkuOption[] skuOptions = postSku.getSkuOptions();

		Assert.assertTrue((skuOptions != null) && (skuOptions.length == 1));

		SkuOption skuOption = skuOptions[0];

		Assert.assertEquals(skuOption.getKey(), _cpOption.getKey());
		Assert.assertEquals(
			(long)skuOption.getOptionId(),
			_cpDefinitionOptionRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			(long)skuOption.getOptionValueId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());
		Assert.assertEquals(skuOption.getValue(), _cpOptionValue.getKey());
	}

	private void _testPostProductIdSkuWithOptionIdKey() throws Exception {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRels.get(0);

		Sku postSku = skuResource.postProductIdSku(
			_cpDefinition.getCProductId(),
			_randomSkuWithSkuOptions(
				String.valueOf(
					_cpDefinitionOptionRel.getCPDefinitionOptionRelId()),
				null, null, null, null,
				String.valueOf(
					cpDefinitionOptionValueRel.
						getCPDefinitionOptionValueRelId())));

		SkuOption[] skuOptions = postSku.getSkuOptions();

		Assert.assertTrue((skuOptions != null) && (skuOptions.length == 1));

		SkuOption skuOption = skuOptions[0];

		Assert.assertEquals(skuOption.getKey(), _cpOption.getKey());
		Assert.assertEquals(
			(long)skuOption.getOptionId(),
			_cpDefinitionOptionRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			(long)skuOption.getOptionValueId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());
		Assert.assertEquals(skuOption.getValue(), _cpOptionValue.getKey());
	}

	private void _testPostProductIdSkuWithOptionKey() throws Exception {
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRels.get(0);

		Sku postSku = skuResource.postProductIdSku(
			_cpDefinition.getCProductId(),
			_randomSkuWithSkuOptions(
				_cpOption.getKey(), null, null, null, null,
				_cpOptionValue.getKey()));

		SkuOption[] skuOptions = postSku.getSkuOptions();

		Assert.assertTrue((skuOptions != null) && (skuOptions.length == 1));

		SkuOption skuOption = skuOptions[0];

		Assert.assertEquals(skuOption.getKey(), _cpOption.getKey());
		Assert.assertEquals(
			(long)skuOption.getOptionId(),
			_cpDefinitionOptionRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			(long)skuOption.getOptionValueId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());
		Assert.assertEquals(skuOption.getValue(), _cpOptionValue.getKey());
	}

	private void _testPostProductIdSkuWithSkuVirtualSettings()
		throws Exception {

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		SkuResource skuResource = SkuResource.builder(
		).authentication(
			adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "skuVirtualSettings"
		).build();

		SkuVirtualSettings randomSkuVirtualSettings = new SkuVirtualSettings() {
			{
				activationStatus = 0;
				duration = RandomTestUtil.nextLong();
				maxUsages = RandomTestUtil.nextInt();
				override = true;
				sampleURL = "https://liferay.com";
				termsOfUseRequired = false;
				url = "https://liferay.com";
				useSample = true;
			}
		};

		Sku randomSku = randomSku();

		randomSku.setSkuVirtualSettings(randomSkuVirtualSettings);

		Sku postSku = skuResource.postProductIdSku(
			_cpDefinition.getCProductId(), randomSku);

		SkuVirtualSettings postSkuVirtualSettings =
			postSku.getSkuVirtualSettings();

		Assert.assertNotNull(postSkuVirtualSettings);
		Assert.assertEquals(
			postSkuVirtualSettings.getActivationStatus(),
			randomSkuVirtualSettings.getActivationStatus());
		Assert.assertEquals(
			postSkuVirtualSettings.getDuration(),
			randomSkuVirtualSettings.getDuration());
		Assert.assertEquals(
			postSkuVirtualSettings.getMaxUsages(),
			randomSkuVirtualSettings.getMaxUsages());
		Assert.assertEquals(
			postSkuVirtualSettings.getOverride(),
			randomSkuVirtualSettings.getOverride());
		Assert.assertEquals(
			postSkuVirtualSettings.getSampleURL(),
			randomSkuVirtualSettings.getSampleURL());
		Assert.assertEquals(
			postSkuVirtualSettings.getTermsOfUseRequired(),
			randomSkuVirtualSettings.getTermsOfUseRequired());
		Assert.assertEquals(
			postSkuVirtualSettings.getUrl(), randomSkuVirtualSettings.getUrl());
		Assert.assertEquals(
			postSkuVirtualSettings.getUseSample(),
			randomSkuVirtualSettings.getUseSample());
	}

	private void _testPostProductIdSkuWithSkuVirtualSettingsWhenLazyReferencingEnabled()
		throws Exception {

		Sku postSku = null;

		Sku randomSku = randomSku();

		randomSku.setSkuVirtualSettings(
			new SkuVirtualSettings() {
				{
					override = true;
					skuVirtualSettingsFileEntries =
						new SkuVirtualSettingsFileEntry[] {
							new SkuVirtualSettingsFileEntry() {
								{
									url = "https://liferay.com";
									version = RandomTestUtil.randomString();
								}
							}
						};
					url = "https://liferay.com";
				}
			});

		User adminUser = UserTestUtil.getAdminUser(testCompany.getCompanyId());

		SkuResource skuResource = SkuResource.builder(
		).authentication(
			adminUser.getEmailAddress(), PropsValues.DEFAULT_ADMIN_PASSWORD
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "skuVirtualSettings"
		).build();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postSku = skuResource.postProductIdSku(
				_cpDefinition.getCProductId(), randomSku);
		}

		SkuVirtualSettings skuVirtualSettings = postSku.getSkuVirtualSettings();

		SkuVirtualSettingsFileEntry[] skuVirtualSettingsFileEntries =
			skuVirtualSettings.getSkuVirtualSettingsFileEntries();

		Assert.assertEquals(
			Arrays.toString(skuVirtualSettingsFileEntries), 1,
			skuVirtualSettingsFileEntries.length);
	}

	private void _testPostProductIdSkuWithTermsOfUseJournalArticleExternalReferenceCode()
		throws Exception {

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_cpDefinition.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		SkuVirtualSettings postSkuVirtualSettings = _postSkuWithTermsOfUse(
			null, journalArticle);

		Assert.assertEquals(
			(Long)journalArticle.getResourcePrimKey(),
			postSkuVirtualSettings.getTermsOfUseJournalArticleId());
		Assert.assertEquals(
			journalArticle.getExternalReferenceCode(),
			postSkuVirtualSettings.
				getTermsOfUseJournalArticleExternalReferenceCode());
	}

	private void _testPostProductIdSkuWithTermsOfUseJournalArticleGroupExternalReferenceCode()
		throws Exception {

		_group = GroupTestUtil.addGroup();

		_group.setExternalReferenceCode(RandomTestUtil.randomString());

		_group = _groupLocalService.updateGroup(_group);

		JournalArticle journalArticle = JournalTestUtil.addArticle(
			_group.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		JournalArticle catalogJournalArticle = JournalTestUtil.addArticle(
			_cpDefinition.getGroupId(),
			JournalFolderConstants.DEFAULT_PARENT_FOLDER_ID);

		catalogJournalArticle.setExternalReferenceCode(
			journalArticle.getExternalReferenceCode());

		catalogJournalArticle =
			_journalArticleLocalService.updateJournalArticle(
				catalogJournalArticle);

		SkuVirtualSettings postSkuVirtualSettings = _postSkuWithTermsOfUse(
			_group.getExternalReferenceCode(), journalArticle);

		Assert.assertEquals(
			_group.getExternalReferenceCode(),
			postSkuVirtualSettings.
				getTermsOfUseJournalArticleGroupExternalReferenceCode());
		Assert.assertNotEquals(
			(Long)catalogJournalArticle.getResourcePrimKey(),
			postSkuVirtualSettings.getTermsOfUseJournalArticleId());
		Assert.assertEquals(
			(Long)journalArticle.getResourcePrimKey(),
			postSkuVirtualSettings.getTermsOfUseJournalArticleId());
	}

	private void _testPutSkuByExternalReferenceCodeWithLazyReferences()
		throws Exception {

		String optionKeyExternalReferenceCode = RandomTestUtil.randomString();
		String optionValueKeyExternalReferenceCode =
			RandomTestUtil.randomString();
		String parentOptionKeyExternalReferenceCode =
			RandomTestUtil.randomString();

		SkuOption skuOption = new SkuOption() {
			{
				key = StringUtil.toLowerCase(RandomTestUtil.randomString());
				optionExternalReferenceCode = optionKeyExternalReferenceCode;
				optionFieldType = CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY;
				optionSkuContributor = true;
				optionValueExternalReferenceCode =
					optionValueKeyExternalReferenceCode;
				parentOptionExternalReferenceCode =
					parentOptionKeyExternalReferenceCode;
				parentOptionFieldType = CPConstants.PRODUCT_OPTION_SELECT_KEY;
				parentOptionSkuContributor = true;
				value = _CP_DEFINITION_OPTION_VALUE_REL_KEY;
			}
		};

		Sku sku = randomSku();

		String catalogCurrencyCode = RandomTestUtil.randomString();
		String catalogCurrencyExternalReferenceCode =
			RandomTestUtil.randomString();
		String catalogExternalReferenceCode = RandomTestUtil.randomString();
		String externalReferenceCode = RandomTestUtil.randomString();
		String productExternalReferenceCode = RandomTestUtil.randomString();

		sku.setCatalogCurrencyCode(catalogCurrencyCode);
		sku.setCatalogCurrencyExternalReferenceCode(
			catalogCurrencyExternalReferenceCode);
		sku.setCatalogExternalReferenceCode(catalogExternalReferenceCode);
		sku.setExternalReferenceCode(externalReferenceCode);
		sku.setProductExternalReferenceCode(productExternalReferenceCode);
		sku.setProductType(_PRODUCT_TYPE_NAME);
		sku.setSkuOptions(new SkuOption[] {skuOption});

		Sku putSku = null;

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			putSku = skuResource.putSkuByExternalReferenceCode(
				externalReferenceCode, sku);
		}

		_commerceCurrency =
			_commerceCurrencyLocalService.
				getCommerceCurrencyByExternalReferenceCode(
					catalogCurrencyExternalReferenceCode,
					testCompany.getCompanyId());

		Assert.assertEquals(catalogCurrencyCode, _commerceCurrency.getCode());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, _commerceCurrency.getStatus());

		_commerceCatalog =
			_commerceCatalogLocalService.
				getCommerceCatalogByExternalReferenceCode(
					catalogExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			catalogCurrencyCode, _commerceCatalog.getCommerceCurrencyCode());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, _commerceCatalog.getStatus());

		_lazyReferencedCPDefinition =
			_cpDefinitionLocalService.
				getCPDefinitionByCProductExternalReferenceCode(
					productExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			_commerceCatalog.getGroupId(),
			_lazyReferencedCPDefinition.getGroupId());
		Assert.assertEquals(
			_PRODUCT_TYPE_NAME,
			_lazyReferencedCPDefinition.getProductTypeName());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY,
			_lazyReferencedCPDefinition.getStatus());

		CPInstance cpInstance =
			_cpInstanceLocalService.getCPInstanceByExternalReferenceCode(
				externalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			_lazyReferencedCPDefinition.getCPDefinitionId(),
			cpInstance.getCPDefinitionId());
		Assert.assertEquals(putSku.getId(), (Long)cpInstance.getCPInstanceId());

		_lazyReferencedCPOption =
			_cpOptionLocalService.getCPOptionByExternalReferenceCode(
				parentOptionKeyExternalReferenceCode,
				testCompany.getCompanyId());

		Assert.assertEquals(
			CPConstants.PRODUCT_OPTION_SELECT_KEY,
			_lazyReferencedCPOption.getCommerceOptionTypeKey());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY,
			_lazyReferencedCPOption.getStatus());

		CPDefinitionOptionRel cpDefinitionOptionRel =
			_cpDefinitionOptionRelLocalService.
				getCPDefinitionOptionRelByExternalReferenceCode(
					optionKeyExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			_lazyReferencedCPDefinition.getCPDefinitionId(),
			cpDefinitionOptionRel.getCPDefinitionId());
		Assert.assertEquals(
			CPConstants.PRODUCT_OPTION_SELECT_DATE_KEY,
			cpDefinitionOptionRel.getCommerceOptionTypeKey());

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_cpDefinitionOptionValueRelLocalService.
				getCPDefinitionOptionValueRelByExternalReferenceCode(
					optionValueKeyExternalReferenceCode,
					testCompany.getCompanyId());

		Assert.assertEquals(
			cpDefinitionOptionRel.getCPDefinitionOptionRelId(),
			cpDefinitionOptionValueRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			_CP_DEFINITION_OPTION_VALUE_REL_KEY,
			cpDefinitionOptionValueRel.getKey());
	}

	private void _testPutSkuByExternalReferenceCodeWithUnitOfMeasures()
		throws Exception {

		Sku sku = _randomSkuWithUnitOfMeasures();

		Sku postSku = skuResource.postProductByExternalReferenceCodeSku(
			_cProduct.getExternalReferenceCode(), sku);
		Sku putSku1 = skuResource.putSkuByExternalReferenceCode(
			sku.getExternalReferenceCode(), sku);

		Assert.assertEquals(postSku.getId(), putSku1.getId());

		_assertSkuUnitOfMeasures(sku, putSku1);

		Sku putSku2 = skuResource.putSkuByExternalReferenceCode(
			sku.getExternalReferenceCode(), sku);

		Assert.assertEquals(putSku1.getId(), putSku2.getId());

		_assertSkuUnitOfMeasures(sku, putSku2);
	}

	private static final String _CP_DEFINITION_OPTION_VALUE_REL_KEY =
		"03-18-2024-16-45-1-hours-europe-paris";

	private static final String _PRODUCT_TYPE_NAME = "simple";

	@DeleteAfterTestRun
	private CProduct _cProduct;

	@DeleteAfterTestRun
	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@DeleteAfterTestRun
	private CPDefinition _cpDefinition;

	@Inject
	private CPDefinitionLocalService _cpDefinitionLocalService;

	@DeleteAfterTestRun
	private CPDefinitionOptionRel _cpDefinitionOptionRel;

	@Inject
	private CPDefinitionOptionRelLocalService
		_cpDefinitionOptionRelLocalService;

	@Inject
	private CPDefinitionOptionValueRelLocalService
		_cpDefinitionOptionValueRelLocalService;

	@DeleteAfterTestRun
	private List<CPDefinitionOptionValueRel> _cpDefinitionOptionValueRels =
		new ArrayList<>();

	@Inject
	private CPInstanceHelper _cpInstanceHelper;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@Inject
	private CPInstanceUnitOfMeasureLocalService
		_cpInstanceUnitOfMeasureLocalService;

	@DeleteAfterTestRun
	private CPOption _cpOption;

	@Inject
	private CPOptionLocalService _cpOptionLocalService;

	@DeleteAfterTestRun
	private CPOptionValue _cpOptionValue;

	@Inject
	private CPOptionValueLocalService _cpOptionValueLocalService;

	@DeleteAfterTestRun
	private Group _group;

	@Inject
	private GroupLocalService _groupLocalService;

	@Inject
	private JournalArticleLocalService _journalArticleLocalService;

	@DeleteAfterTestRun
	private CPDefinition _lazyReferencedCPDefinition;

	@DeleteAfterTestRun
	private CPOption _lazyReferencedCPOption;

	@DeleteAfterTestRun
	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}