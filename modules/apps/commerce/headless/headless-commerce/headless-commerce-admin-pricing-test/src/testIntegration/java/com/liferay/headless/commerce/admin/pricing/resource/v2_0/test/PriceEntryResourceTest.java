/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.CommerceCurrencyLocalService;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.client.problem.Problem;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Stefano Motta
 */
@RunWith(Arquillian.class)
public class PriceEntryResourceTest extends BasePriceEntryResourceTestCase {

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_user = UserTestUtil.addUser(testCompany);

		_commerceCurrency = _commerceCurrencyLocalService.addCommerceCurrency(
			null, _user.getUserId(), RandomTestUtil.randomString(),
			Collections.singletonMap(
				LocaleUtil.getSiteDefault(), RandomTestUtil.randomString()),
			RandomTestUtil.randomString(), BigDecimal.ONE, new HashMap<>(), 2,
			2, "HALF_EVEN", false, 0, true);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_commercePriceList =
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), _user.getUserId(),
				testGroup.getGroupId(), 0, false, _commerceCurrency.getCode(),
				1, 12, 0, 1, 2022, 0, 0, 0, 0, 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomBoolean(), true,
				RandomTestUtil.randomDouble(),
				CommercePriceListConstants.TYPE_PRICE_LIST, _serviceContext);

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
	public void testDeletePriceEntry() throws Exception {
		super.testDeletePriceEntry();
	}

	@Ignore
	@Override
	@Test
	public void testDeletePriceEntryBatch() throws Exception {
		super.testDeletePriceEntryBatch();
	}

	@Ignore
	@Override
	@Test
	public void testDeletePriceEntryByExternalReferenceCode() throws Exception {
		super.testDeletePriceEntryByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceEntry() throws Exception {
		super.testGetPriceEntry();
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceEntryByExternalReferenceCode() throws Exception {
		super.testGetPriceEntryByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeletePriceEntry() throws Exception {
		super.testGraphQLDeletePriceEntry();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLDeletePriceEntryByExternalReferenceCode()
		throws Exception {

		super.testGraphQLDeletePriceEntryByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceEntry() throws Exception {
		super.testGraphQLGetPriceEntry();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceEntryByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetPriceEntryByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceEntryByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetPriceEntryByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testPatchPriceEntry() throws Exception {
		super.testPatchPriceEntry();
	}

	@Ignore
	@Override
	@Test
	public void testPatchPriceEntryByExternalReferenceCode() throws Exception {
		super.testPatchPriceEntryByExternalReferenceCode();
	}

	@Override
	@Test
	public void testPostPriceListIdPriceEntry() throws Exception {
		super.testPostPriceListIdPriceEntry();

		_testPostPriceListIdPriceEntryWithExistingPriceEntryId();
		_testPostPriceListIdPriceEntryWithLazyReferencingDisabled();
		_testPostPriceListIdPriceEntryWithLazyReferencingEnabled();
		_testPostPriceListIdPriceEntryWithPriceOnApplicationOnBasePriceList();
		_testPostPriceListIdPriceEntryWithPriceOnApplicationOnPriceList();
	}

	@Ignore
	@Override
	@Test
	public void testVulcanCRUDItemDelegateGetItem() throws Exception {
		super.testVulcanCRUDItemDelegateGetItem();
	}

	@Override
	protected String[] getAdditionalAssertFieldNames() {
		return new String[] {
			"priceListId", "skuExternalReferenceCode", "skuId"
		};
	}

	@Override
	protected String[] getIgnoredEntityFieldNames() {
		return new String[] {
			"priceListExternalReferenceCode", "skuExternalReferenceCode"
		};
	}

	@Override
	protected PriceEntry randomPriceEntry() throws Exception {
		return new PriceEntry() {
			{
				active = RandomTestUtil.randomBoolean();
				bulkPricing = RandomTestUtil.randomBoolean();
				discountDiscovery = RandomTestUtil.randomBoolean();
				displayDate = RandomTestUtil.nextDate();
				expirationDate = RandomTestUtil.nextDate();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				hasTierPrice = RandomTestUtil.randomBoolean();
				neverExpire = true;
				price = RandomTestUtil.randomDouble();
				priceEntryId = RandomTestUtil.randomLong();
				priceListExternalReferenceCode =
					_commercePriceList.getExternalReferenceCode();
				priceListId = _commercePriceList.getCommercePriceListId();
				priceOnApplication = RandomTestUtil.randomBoolean();
				skuExternalReferenceCode =
					_cpInstance.getExternalReferenceCode();
				skuId = _cpInstance.getCPInstanceId();
			}
		};
	}

	@Override
	protected PriceEntry
			testGetPriceListByExternalReferenceCodePriceEntriesPage_addPriceEntry(
				String externalReferenceCode, PriceEntry priceEntry)
		throws Exception {

		return priceEntryResource.
			postPriceListByExternalReferenceCodePriceEntry(
				externalReferenceCode, priceEntry);
	}

	@Override
	protected String
			testGetPriceListByExternalReferenceCodePriceEntriesPage_getExternalReferenceCode()
		throws Exception {

		return _commercePriceList.getExternalReferenceCode();
	}

	@Override
	protected PriceEntry testGetPriceListIdPriceEntriesPage_addPriceEntry(
			Long id, PriceEntry priceEntry)
		throws Exception {

		return priceEntryResource.postPriceListIdPriceEntry(id, priceEntry);
	}

	@Override
	protected Long testGetPriceListIdPriceEntriesPage_getId() throws Exception {
		return _commercePriceList.getCommercePriceListId();
	}

	@Override
	protected PriceEntry
			testPostPriceListByExternalReferenceCodePriceEntry_addPriceEntry(
				PriceEntry priceEntry)
		throws Exception {

		return priceEntryResource.
			postPriceListByExternalReferenceCodePriceEntry(
				_commercePriceList.getExternalReferenceCode(), priceEntry);
	}

	@Override
	protected PriceEntry testPostPriceListIdPriceEntry_addPriceEntry(
			PriceEntry priceEntry)
		throws Exception {

		return priceEntryResource.postPriceListIdPriceEntry(
			_commercePriceList.getCommercePriceListId(), priceEntry);
	}

	@Override
	protected PriceEntry
			testPutPriceEntryByExternalReferenceCode_addPriceEntry()
		throws Exception {

		return priceEntryResource.postPriceListIdPriceEntry(
			_commercePriceList.getCommercePriceListId(), randomPriceEntry());
	}

	private CommercePriceList _addCommercePriceList() throws Exception {
		CommerceCatalog commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId(), _commerceCurrency.getCode());

		_commerceCatalogs.add(commerceCatalog);

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), _user.getUserId(),
				commerceCatalog.getGroupId(), 0, false,
				_commerceCurrency.getCode(), 1, 12, 0, 1, 2022, 0, 0, 0, 0, 0,
				RandomTestUtil.randomString(), RandomTestUtil.randomBoolean(),
				true, RandomTestUtil.randomDouble(),
				CommercePriceListConstants.TYPE_PRICE_LIST, _serviceContext);

		_commercePriceLists.add(commercePriceList);

		return commercePriceList;
	}

	private PriceEntry _randomPriceEntryWithEmptySku() {
		return new PriceEntry() {
			{
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				price = RandomTestUtil.randomDouble();
				productExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				productType = SimpleCPTypeConstants.NAME;
				skuExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
			}
		};
	}

	private void _testPostPriceListIdPriceEntryWithExistingPriceEntryId()
		throws Exception {

		PriceEntry randomPriceEntry = randomPriceEntry();

		PriceEntry postPriceEntry1 =
			priceEntryResource.postPriceListIdPriceEntry(
				_commercePriceList.getCommercePriceListId(),
				randomPriceEntry());

		randomPriceEntry.setPriceEntryId(postPriceEntry1.getPriceEntryId());

		CommercePriceList commercePriceList = _addCommercePriceList();

		PriceEntry postPriceEntry2 =
			priceEntryResource.postPriceListIdPriceEntry(
				commercePriceList.getCommercePriceListId(), randomPriceEntry);

		Assert.assertNotEquals(
			postPriceEntry1.getPriceEntryId(),
			postPriceEntry2.getPriceEntryId());

		PriceEntry priceEntry = priceEntryResource.getPriceEntry(
			postPriceEntry1.getPriceEntryId());

		Assert.assertEquals(postPriceEntry1.getPrice(), priceEntry.getPrice());
	}

	private void _testPostPriceListIdPriceEntryWithLazyReferencingDisabled()
		throws Exception {

		CommercePriceList commercePriceList = _addCommercePriceList();

		try {
			priceEntryResource.postPriceListIdPriceEntry(
				commercePriceList.getCommercePriceListId(),
				_randomPriceEntryWithEmptySku());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostPriceListIdPriceEntryWithLazyReferencingEnabled()
		throws Exception {

		PriceEntry postPriceEntry = null;

		PriceEntry priceEntry = _randomPriceEntryWithEmptySku();

		CommercePriceList commercePriceList = _addCommercePriceList();

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postPriceEntry = priceEntryResource.postPriceListIdPriceEntry(
				commercePriceList.getCommercePriceListId(), priceEntry);
		}

		String skuExternalReferenceCode =
			priceEntry.getSkuExternalReferenceCode();

		Assert.assertEquals(
			skuExternalReferenceCode,
			postPriceEntry.getSkuExternalReferenceCode());

		CPInstance cpInstance =
			_cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
				skuExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpInstance.getStatus());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			priceEntry.getProductExternalReferenceCode(),
			cpDefinition.getCProductExternalReferenceCode());
		Assert.assertEquals(
			commercePriceList.getGroupId(), cpDefinition.getGroupId());
	}

	private void _testPostPriceListIdPriceEntryWithPriceOnApplicationOnBasePriceList()
		throws Exception {

		CommercePriceList commercePriceList =
			_commercePriceListLocalService.addCommercePriceList(
				RandomTestUtil.randomString(), _user.getUserId(),
				testGroup.getGroupId(), 0, true, _commerceCurrency.getCode(), 1,
				12, 0, 1, 2022, 0, 0, 0, 0, 0, RandomTestUtil.randomString(),
				RandomTestUtil.randomBoolean(), true,
				RandomTestUtil.randomDouble(),
				CommercePriceListConstants.TYPE_PRICE_LIST, _serviceContext);

		PriceEntry randomPriceEntry = randomPriceEntry();

		randomPriceEntry.setPriceListExternalReferenceCode(
			commercePriceList.getExternalReferenceCode());
		randomPriceEntry.setPriceListId(
			commercePriceList.getCommercePriceListId());
		randomPriceEntry.setPriceOnApplication(true);

		PriceEntry postPriceEntry =
			priceEntryResource.postPriceListIdPriceEntry(
				commercePriceList.getCommercePriceListId(), randomPriceEntry);

		Assert.assertEquals(
			randomPriceEntry.getPriceOnApplication(),
			postPriceEntry.getPriceOnApplication());
	}

	private void _testPostPriceListIdPriceEntryWithPriceOnApplicationOnPriceList()
		throws Exception {

		PriceEntry randomPriceEntry = randomPriceEntry();

		PriceEntry postPriceEntry =
			priceEntryResource.postPriceListIdPriceEntry(
				_commercePriceList.getCommercePriceListId(), randomPriceEntry);

		Assert.assertEquals(
			postPriceEntry.getPriceOnApplication(),
			randomPriceEntry.getPriceOnApplication());
	}

	@DeleteAfterTestRun
	private List<CommerceCatalog> _commerceCatalogs = new ArrayList<>();

	@DeleteAfterTestRun
	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceCurrencyLocalService _commerceCurrencyLocalService;

	@DeleteAfterTestRun
	private CommercePriceList _commercePriceList;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@DeleteAfterTestRun
	private List<CommercePriceList> _commercePriceLists = new ArrayList<>();

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	private ServiceContext _serviceContext;

	@DeleteAfterTestRun
	private User _user;

}