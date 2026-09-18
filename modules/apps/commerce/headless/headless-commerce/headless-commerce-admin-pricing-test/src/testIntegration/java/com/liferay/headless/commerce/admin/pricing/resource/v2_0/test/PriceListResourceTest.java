/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.headless.commerce.admin.pricing.resource.v2_0.test;

import com.liferay.account.model.AccountEntry;
import com.liferay.account.service.AccountEntryLocalService;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.test.util.CommerceCurrencyTestUtil;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.model.CommercePriceListChannelRel;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListChannelRelLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListOrderTypeRelLocalService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalService;
import com.liferay.commerce.pricing.constants.CommercePriceModifierConstants;
import com.liferay.commerce.pricing.model.CommercePriceModifier;
import com.liferay.commerce.pricing.service.CommercePriceModifierLocalService;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CommerceCatalog;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.CPInstanceLocalService;
import com.liferay.commerce.product.service.CommerceCatalogLocalService;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.product.type.simple.constants.SimpleCPTypeConstants;
import com.liferay.commerce.service.CommerceOrderTypeLocalService;
import com.liferay.commerce.test.util.CommerceTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.exportimport.test.util.LazyReferencingTestUtil;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.Creator;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceEntry;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceList;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListAccount;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListChannel;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceListOrderType;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.PriceModifier;
import com.liferay.headless.commerce.admin.pricing.client.dto.v2_0.TierPrice;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Page;
import com.liferay.headless.commerce.admin.pricing.client.pagination.Pagination;
import com.liferay.headless.commerce.admin.pricing.client.problem.Problem;
import com.liferay.headless.commerce.admin.pricing.client.resource.v2_0.PriceListResource;
import com.liferay.headless.commerce.core.util.DateConfig;
import com.liferay.petra.lang.SafeCloseable;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.model.SystemEvent;
import com.liferay.portal.kernel.model.SystemEventConstants;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.ClassNameLocalService;
import com.liferay.portal.kernel.service.ServiceContext;
import com.liferay.portal.kernel.service.SystemEventLocalService;
import com.liferay.portal.kernel.service.UserLocalService;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.ServiceContextTestUtil;
import com.liferay.portal.kernel.test.util.UserTestUtil;
import com.liferay.portal.kernel.util.LocaleUtil;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 */
@RunWith(Arquillian.class)
public class PriceListResourceTest extends BasePriceListResourceTestCase {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	@Override
	public void setUp() throws Exception {
		super.setUp();

		_commerceCurrency = CommerceCurrencyTestUtil.addCommerceCurrency(
			testGroup.getCompanyId());

		_user = UserTestUtil.addUser(testCompany);

		_serviceContext = ServiceContextTestUtil.getServiceContext(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId());

		_accountEntry = _accountEntryLocalService.addAccountEntry(
			StringPool.BLANK, _user.getUserId(), 0,
			RandomTestUtil.randomString(), RandomTestUtil.randomString(), null,
			RandomTestUtil.randomString() + "@liferay.com", null, null,
			"business", 1, _serviceContext);

		_commerceCatalog = CommerceTestUtil.addCommerceCatalog(
			testCompany.getCompanyId(), testGroup.getGroupId(),
			_user.getUserId(), _commerceCurrency.getCode());
	}

	@Override
	@Test
	public void testDeletePriceListByExternalReferenceCode() throws Exception {
		super.testDeletePriceListByExternalReferenceCode();

		_testDeletePriceListByExternalReferenceCodeWithSystemEvent();
	}

	@Override
	@Test
	public void testGetPriceListsPage() throws Exception {
		super.testGetPriceListsPage();

		_testGetPriceListsPageWithChannelFilter();
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceListsPageWithFilterDateTimeEquals()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceListsPageWithFilterStringContains()
		throws Exception {
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceListsPageWithFilterStringEquals() throws Exception {
		super.testGetPriceListsPageWithFilterStringEquals();
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceListsPageWithFilterStringStartsWith()
		throws Exception {

		super.testGetPriceListsPageWithFilterStringStartsWith();
	}

	@Ignore
	@Override
	@Test
	public void testGetPriceListsPageWithSortString() throws Exception {
		super.testGetPriceListsPageWithSortString();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceList() throws Exception {
		super.testGraphQLGetPriceList();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceListByExternalReferenceCode()
		throws Exception {

		super.testGraphQLGetPriceListByExternalReferenceCode();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceListByExternalReferenceCodeNotFound()
		throws Exception {

		super.testGraphQLGetPriceListByExternalReferenceCodeNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceListNotFound() throws Exception {
		super.testGraphQLGetPriceListNotFound();
	}

	@Ignore
	@Override
	@Test
	public void testGraphQLGetPriceListsPage() throws Exception {
		super.testGraphQLGetPriceListsPage();
	}

	@Override
	@Test
	public void testPatchPriceList() throws Exception {
		super.testPatchPriceList();

		_testPatchPriceListWithSameAccount();
	}

	@Override
	@Test
	public void testPostPriceList() throws Exception {
		super.testPostPriceList();

		_testPostPriceListWithCreator();
		_testPostPriceListWithExistingIds();
		_testPostPriceListWithLazyReferencingDisabled();
		_testPostPriceListWithLazyReferencingEnabled();
		_testPostPriceListWithSamePriceListAccount();
		_testPostPriceListWithSamePriceListChannel();
	}

	@Override
	protected PriceList randomPriceList() throws Exception {
		long time = System.currentTimeMillis();

		return new PriceList() {
			{
				active = Boolean.TRUE;
				author = StringUtil.toLowerCase(RandomTestUtil.randomString());
				catalogBasePriceList = Boolean.FALSE;
				catalogExternalReferenceCode =
					_commerceCatalog.getExternalReferenceCode();
				catalogId = _commerceCatalog.getCommerceCatalogId();
				catalogName = _commerceCatalog.getName();
				createDate = RandomTestUtil.nextDate();
				currencyCode = _commerceCurrency.getCode();
				currencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				currencyId = _commerceCurrency.getCommerceCurrencyId();
				displayDate = new Date(time - Time.HOUR);
				expirationDate = new Date(time + Time.DAY);
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				id = RandomTestUtil.randomLong();
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				netPrice = RandomTestUtil.randomBoolean();
				neverExpire = RandomTestUtil.randomBoolean();
				parentPriceListId = 0L;
				priority = RandomTestUtil.randomDouble();
				type = Type.create(CommercePriceListConstants.TYPE_PRICE_LIST);
			}
		};
	}

	@Override
	protected PriceList testDeletePriceList_addPriceList() throws Exception {
		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList
			testDeletePriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList testGetPriceList_addPriceList() throws Exception {
		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList testGetPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList testGetPriceListsPage_addPriceList(PriceList priceList)
		throws Exception {

		return priceListResource.postPriceList(priceList);
	}

	@Override
	protected PriceList testGraphQLPriceList_addPriceList() throws Exception {
		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList testPatchPriceList_addPriceList() throws Exception {
		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList testPatchPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		return priceListResource.postPriceList(randomPriceList());
	}

	@Override
	protected PriceList testPostPriceList_addPriceList(PriceList priceList)
		throws Exception {

		return priceListResource.postPriceList(priceList);
	}

	@Override
	protected PriceList testPutPriceListByExternalReferenceCode_addPriceList()
		throws Exception {

		return priceListResource.postPriceList(randomPriceList());
	}

	private PriceListAccount _createPriceListAccount(PriceList priceList)
		throws Exception {

		return new PriceListAccount() {
			{
				accountExternalReferenceCode =
					_accountEntry.getExternalReferenceCode();
				accountId = _accountEntry.getAccountEntryId();
				order = RandomTestUtil.randomInt();
				priceListExternalReferenceCode =
					priceList.getExternalReferenceCode();
				priceListId = priceList.getId();
			}
		};
	}

	private PriceList _randomPriceListWithEmptyCatalog() {
		return new PriceList() {
			{
				catalogCurrencyCode = _commerceCurrency.getCode();
				catalogCurrencyExternalReferenceCode =
					_commerceCurrency.getExternalReferenceCode();
				catalogExternalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				currencyCode = _commerceCurrency.getCode();
				externalReferenceCode = StringUtil.toLowerCase(
					RandomTestUtil.randomString());
				name = StringUtil.toLowerCase(RandomTestUtil.randomString());
				priceEntries = new PriceEntry[] {
					new PriceEntry() {
						{
							externalReferenceCode = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
							price = RandomTestUtil.randomDouble();
							productExternalReferenceCode =
								StringUtil.toLowerCase(
									RandomTestUtil.randomString());
							productType = SimpleCPTypeConstants.NAME;
							skuExternalReferenceCode = StringUtil.toLowerCase(
								RandomTestUtil.randomString());
						}
					}
				};
				priority = RandomTestUtil.randomDouble();
				type = Type.create(CommercePriceListConstants.TYPE_PRICE_LIST);
			}
		};
	}

	private PriceList _randomPriceListWithPriceEntry(CPInstance cpInstance)
		throws Exception {

		PriceList priceList = randomPriceList();

		priceList.setPriceEntries(
			new PriceEntry[] {
				new PriceEntry() {
					{
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						price = RandomTestUtil.randomDouble();
						skuExternalReferenceCode =
							cpInstance.getExternalReferenceCode();
						tierPrices = new TierPrice[] {
							new TierPrice() {
								{
									externalReferenceCode =
										StringUtil.toLowerCase(
											RandomTestUtil.randomString());
									minimumQuantity = BigDecimal.TEN;
									price = RandomTestUtil.randomDouble();
								}
							}
						};
					}
				}
			});
		priceList.setPriceModifiers(
			new PriceModifier[] {
				new PriceModifier() {
					{
						active = true;
						externalReferenceCode = StringUtil.toLowerCase(
							RandomTestUtil.randomString());
						modifierAmount = BigDecimal.ONE;
						modifierType =
							CommercePriceModifierConstants.
								MODIFIER_TYPE_PERCENTAGE;
						target = CommercePriceModifierConstants.TARGET_CATALOG;
						title = RandomTestUtil.randomString();
					}
				}
			});

		return priceList;
	}

	private void _testDeletePriceListByExternalReferenceCodeWithSystemEvent()
		throws Exception {

		PriceList priceList =
			testDeletePriceListByExternalReferenceCode_addPriceList();

		priceListResource.deletePriceListByExternalReferenceCode(
			priceList.getExternalReferenceCode());

		List<SystemEvent> systemEvents =
			_systemEventLocalService.getSystemEvents(
				0,
				_classNameLocalService.getClassNameId(CommercePriceList.class),
				priceList.getId(), SystemEventConstants.TYPE_DELETE);

		Assert.assertEquals(systemEvents.toString(), 1, systemEvents.size());

		SystemEvent systemEvent = systemEvents.get(0);

		Assert.assertEquals(
			priceList.getExternalReferenceCode(),
			systemEvent.getClassExternalReferenceCode());
	}

	private void _testGetPriceListsPageWithChannelFilter() throws Exception {
		CommerceChannel commerceChannel1 = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());
		CommerceChannel commerceChannel2 = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceChannels.add(commerceChannel1);
		_commerceChannels.add(commerceChannel2);

		CommercePriceList commercePriceList1 =
			CommercePriceListTestUtil.addChannelPriceList(
				testGroup.getGroupId(), commerceChannel1.getCommerceChannelId(),
				CommercePriceListConstants.TYPE_PRICE_LIST);
		CommercePriceList commercePriceList2 =
			CommercePriceListTestUtil.addChannelPriceList(
				testGroup.getGroupId(), commerceChannel2.getCommerceChannelId(),
				CommercePriceListConstants.TYPE_PRICE_LIST);

		_commercePriceLists.add(commercePriceList1);
		_commercePriceLists.add(commercePriceList2);

		Page<PriceList> page = priceListResource.getPriceListsPage(
			null,
			String.format(
				"(channelId/any(x:(x eq %s)))",
				commerceChannel1.getCommerceChannelId()),
			Pagination.of(1, 10), null);

		assertEquals(
			Collections.singletonList(
				priceListResource.getPriceList(
					commercePriceList1.getCommercePriceListId())),
			(List<PriceList>)page.getItems());
	}

	private void _testPatchPriceListWithSameAccount() throws Exception {
		User omniadminUser = UserTestUtil.addOmniadminUser();
		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		PriceListResource priceListResource = PriceListResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "priceListAccounts"
		).build();

		PriceList priceList = randomPriceList();

		PriceListAccount priceListAccount = _createPriceListAccount(priceList);

		priceListAccount.setAccountId(0L);
		priceListAccount.setPriceListId(0L);

		priceList.setPriceListAccounts(
			new PriceListAccount[] {priceListAccount});

		PriceList expectedPriceList = priceListResource.getPriceList(
			testPostPriceList_addPriceList(
				priceList
			).getId());

		PriceListAccount[] expectedPriceListAccounts =
			expectedPriceList.getPriceListAccounts();

		PriceListAccount expectedPriceListAccount =
			expectedPriceListAccounts[0];

		Assert.assertEquals(
			priceListAccount.toString(),
			expectedPriceListAccount.getAccountExternalReferenceCode(),
			priceListAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			priceListAccount.toString(), expectedPriceListAccount.getOrder(),
			priceListAccount.getOrder());
		Assert.assertEquals(
			priceListAccount.toString(),
			expectedPriceListAccount.getPriceListExternalReferenceCode(),
			priceListAccount.getPriceListExternalReferenceCode());

		priceListAccount.setOrder(RandomTestUtil.randomInt());

		priceList.setPriceListAccounts(
			new PriceListAccount[] {priceListAccount});

		expectedPriceList = priceListResource.patchPriceList(
			expectedPriceList.getId(), priceList);

		expectedPriceListAccounts = expectedPriceList.getPriceListAccounts();

		expectedPriceListAccount = expectedPriceListAccounts[0];

		Assert.assertEquals(
			priceListAccount.toString(),
			expectedPriceListAccount.getAccountExternalReferenceCode(),
			priceListAccount.getAccountExternalReferenceCode());
		Assert.assertEquals(
			priceListAccount.toString(), expectedPriceListAccount.getOrder(),
			priceListAccount.getOrder());
		Assert.assertEquals(
			priceListAccount.toString(),
			expectedPriceListAccount.getPriceListExternalReferenceCode(),
			priceListAccount.getPriceListExternalReferenceCode());
	}

	private void _testPostPriceListWithCreator() throws Exception {
		String password = RandomTestUtil.randomString();
		User user = UserTestUtil.addOmniadminUser();

		_userLocalService.updatePassword(
			user.getUserId(), password, password, false, true);

		PriceListResource priceListResource = PriceListResource.builder(
		).authentication(
			user.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "creator"
		).build();

		PriceList postPriceList = priceListResource.postPriceList(
			randomPriceList());

		Creator creator = postPriceList.getCreator();

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			creator.getExternalReferenceCode());
	}

	private void _testPostPriceListWithExistingIds() throws Exception {
		CPInstance cpInstance = CPTestUtil.addCPInstanceWithRandomSku(
			testGroup.getGroupId(), BigDecimal.TEN);

		_cpInstances.add(cpInstance);

		PriceList priceList1 = _randomPriceListWithPriceEntry(cpInstance);

		priceListResource.postPriceList(priceList1);

		PriceList priceList2 = _randomPriceListWithPriceEntry(cpInstance);

		PriceEntry[] priceEntries2 = priceList2.getPriceEntries();

		PriceEntry priceEntry2 = priceEntries2[0];

		PriceEntry[] priceEntries1 = priceList1.getPriceEntries();

		PriceEntry priceEntry1 = priceEntries1[0];

		CommercePriceEntry commercePriceEntry =
			_commercePriceEntryLocalService.
				fetchCommercePriceEntryByExternalReferenceCode(
					priceEntry1.getExternalReferenceCode(),
					testCompany.getCompanyId());

		priceEntry2.setPriceEntryId(
			commercePriceEntry.getCommercePriceEntryId());

		TierPrice[] tierPrices2 = priceEntry2.getTierPrices();

		TierPrice tierPrice2 = tierPrices2[0];

		TierPrice[] tierPrices1 = priceEntry1.getTierPrices();

		TierPrice tierPrice1 = tierPrices1[0];

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.
				fetchCommerceTierPriceEntryByExternalReferenceCode(
					tierPrice1.getExternalReferenceCode(),
					testCompany.getCompanyId());

		tierPrice2.setId(commerceTierPriceEntry.getCommerceTierPriceEntryId());

		PriceModifier[] priceModifiers2 = priceList2.getPriceModifiers();

		PriceModifier priceModifier2 = priceModifiers2[0];

		PriceModifier[] priceModifiers1 = priceList1.getPriceModifiers();

		PriceModifier priceModifier1 = priceModifiers1[0];

		CommercePriceModifier commercePriceModifier =
			_commercePriceModifierLocalService.
				fetchCommercePriceModifierByExternalReferenceCode(
					priceModifier1.getExternalReferenceCode(),
					testCompany.getCompanyId());

		priceModifier2.setId(
			commercePriceModifier.getCommercePriceModifierId());

		priceListResource.postPriceList(priceList2);

		BigDecimal commercePriceEntryPrice = commercePriceEntry.getPrice();

		commercePriceEntry =
			_commercePriceEntryLocalService.getCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId());

		Assert.assertEquals(
			commercePriceEntryPrice, commercePriceEntry.getPrice());

		BigDecimal commerceTierPriceEntryPrice =
			commerceTierPriceEntry.getPrice();

		commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.getCommerceTierPriceEntry(
				commerceTierPriceEntry.getCommerceTierPriceEntryId());

		Assert.assertEquals(
			commerceTierPriceEntryPrice, commerceTierPriceEntry.getPrice());

		commercePriceModifier =
			_commercePriceModifierLocalService.getCommercePriceModifier(
				commercePriceModifier.getCommercePriceModifierId());

		Assert.assertEquals(
			priceModifier1.getTitle(), commercePriceModifier.getTitle());
	}

	private void _testPostPriceListWithLazyReferencingDisabled()
		throws Exception {

		try {
			priceListResource.postPriceList(_randomPriceListWithEmptyCatalog());

			Assert.fail();
		}
		catch (Problem.ProblemException problemException) {
			Problem problem = problemException.getProblem();

			Assert.assertEquals("NOT_FOUND", problem.getStatus());
		}
	}

	private void _testPostPriceListWithLazyReferencingEnabled()
		throws Exception {

		PriceList postPriceList = null;

		PriceList priceList = _randomPriceListWithEmptyCatalog();

		priceList.setCatalogId(_commerceCatalog.getCommerceCatalogId());

		PriceListOrderType priceListOrderType = new PriceListOrderType();

		DateConfig displayDateConfig = DateConfig.toDisplayDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());
		DateConfig expirationDateConfig = DateConfig.toExpirationDateConfig(
			RandomTestUtil.nextDate(), _user.getTimeZone());

		CommerceOrderType commerceOrderType =
			_commerceOrderTypeLocalService.addCommerceOrderType(
				RandomTestUtil.randomString(), _user.getUserId(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomLocaleStringMap(),
				RandomTestUtil.randomBoolean(), displayDateConfig.getMonth(),
				displayDateConfig.getDay(), displayDateConfig.getYear(),
				displayDateConfig.getHour(), displayDateConfig.getMinute(), 0,
				expirationDateConfig.getMonth(), expirationDateConfig.getDay(),
				expirationDateConfig.getYear(), expirationDateConfig.getHour(),
				expirationDateConfig.getMinute(), true, _serviceContext);

		_commerceOrderTypes.add(commerceOrderType);

		priceListOrderType.setOrderTypeExternalReferenceCode(
			StringUtil.toLowerCase(RandomTestUtil.randomString()));
		priceListOrderType.setOrderTypeId(
			commerceOrderType.getCommerceOrderTypeId());

		priceList.setPriceListOrderTypes(
			new PriceListOrderType[] {priceListOrderType});

		try (SafeCloseable safeCloseable =
				LazyReferencingTestUtil.setLazyReferencingWithSafeCloseable(
					true)) {

			postPriceList = priceListResource.postPriceList(priceList);
		}

		String catalogExternalReferenceCode =
			priceList.getCatalogExternalReferenceCode();

		Assert.assertEquals(
			catalogExternalReferenceCode,
			postPriceList.getCatalogExternalReferenceCode());

		CommerceCatalog commerceCatalog =
			_commerceCatalogLocalService.
				fetchCommerceCatalogByExternalReferenceCode(
					catalogExternalReferenceCode, testCompany.getCompanyId());

		Assert.assertEquals(
			_commerceCurrency.getCode(),
			commerceCatalog.getCommerceCurrencyCode());
		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, commerceCatalog.getStatus());

		PriceEntry[] priceEntries = priceList.getPriceEntries();

		PriceEntry priceEntry = priceEntries[0];

		CPInstance cpInstance =
			_cpInstanceLocalService.fetchCPInstanceByExternalReferenceCode(
				priceEntry.getSkuExternalReferenceCode(),
				testCompany.getCompanyId());

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, cpInstance.getStatus());

		CPDefinition cpDefinition = cpInstance.getCPDefinition();

		Assert.assertEquals(
			priceEntry.getProductExternalReferenceCode(),
			cpDefinition.getCProductExternalReferenceCode());
		Assert.assertEquals(
			commerceCatalog.getGroupId(), cpDefinition.getGroupId());

		CommerceOrderType emptyCommerceOrderType =
			_commerceOrderTypeLocalService.
				fetchCommerceOrderTypeByExternalReferenceCode(
					priceListOrderType.getOrderTypeExternalReferenceCode(),
					testCompany.getCompanyId());

		_commerceOrderTypes.add(emptyCommerceOrderType);

		Assert.assertEquals(
			WorkflowConstants.STATUS_EMPTY, emptyCommerceOrderType.getStatus());
		Assert.assertNotNull(
			_commercePriceListOrderTypeRelLocalService.
				fetchCommercePriceListOrderTypeRel(
					postPriceList.getId(),
					emptyCommerceOrderType.getCommerceOrderTypeId()));
	}

	private void _testPostPriceListWithSamePriceListAccount() throws Exception {
		User omniadminUser = UserTestUtil.addOmniadminUser();
		String password = RandomTestUtil.randomString();

		_userLocalService.updatePassword(
			omniadminUser.getUserId(), password, password, false, true);

		PriceListResource priceListResource = PriceListResource.builder(
		).authentication(
			omniadminUser.getEmailAddress(), password
		).locale(
			LocaleUtil.getDefault()
		).parameters(
			"nestedFields", "priceListAccounts"
		).build();

		PriceList priceList = randomPriceList();

		priceList.setPriceListAccounts(
			new PriceListAccount[] {_createPriceListAccount(priceList)});

		PriceList expectedPriceList = priceListResource.getPriceList(
			testPostPriceList_addPriceList(
				priceList
			).getId());
		PriceList actualPriceList = priceListResource.getPriceList(
			testPostPriceList_addPriceList(
				priceList
			).getId());

		assertEquals(expectedPriceList, actualPriceList);

		PriceListAccount[] expectedPriceListAccounts =
			expectedPriceList.getPriceListAccounts();

		Assert.assertEquals(
			Arrays.toString(expectedPriceListAccounts), 1,
			expectedPriceListAccounts.length);

		PriceListAccount[] actualPriceListAccounts =
			actualPriceList.getPriceListAccounts();

		Assert.assertEquals(
			Arrays.toString(actualPriceListAccounts), 1,
			actualPriceListAccounts.length);

		PriceListAccount expectedPriceListAccount =
			expectedPriceListAccounts[0];
		PriceListAccount actualPriceListAccount = expectedPriceListAccounts[0];

		Assert.assertEquals(
			actualPriceListAccount.toString(),
			expectedPriceListAccount.getAccountId(),
			actualPriceListAccount.getAccountId());
		Assert.assertEquals(
			actualPriceListAccount.toString(),
			expectedPriceListAccount.getPriceListId(),
			actualPriceListAccount.getPriceListId());
	}

	private void _testPostPriceListWithSamePriceListChannel() throws Exception {
		PriceList priceList = randomPriceList();

		PriceListChannel priceListChannel = new PriceListChannel();

		CommerceChannel commerceChannel = CommerceTestUtil.addCommerceChannel(
			testGroup.getGroupId(), _commerceCurrency.getCode());

		_commerceChannels.add(commerceChannel);

		priceListChannel.setChannelExternalReferenceCode(
			commerceChannel.getExternalReferenceCode());

		priceListChannel.setOrder(1);

		priceList.setPriceListChannels(
			new PriceListChannel[] {priceListChannel});

		priceListResource.postPriceList(priceList);

		priceListChannel.setOrder(2);

		PriceList putPriceList =
			priceListResource.putPriceListByExternalReferenceCode(
				priceList.getExternalReferenceCode(), priceList);

		List<CommercePriceListChannelRel> commercePriceListChannelRels =
			_commercePriceListChannelRelLocalService.
				getCommercePriceListChannelRels(putPriceList.getId());

		Assert.assertEquals(
			commercePriceListChannelRels.toString(), 1,
			commercePriceListChannelRels.size());

		CommercePriceListChannelRel commercePriceListChannelRel =
			commercePriceListChannelRels.get(0);

		Assert.assertEquals(
			commerceChannel.getCommerceChannelId(),
			commercePriceListChannelRel.getCommerceChannelId());
		Assert.assertEquals(2, commercePriceListChannelRel.getOrder());
	}

	private AccountEntry _accountEntry;

	@Inject
	private AccountEntryLocalService _accountEntryLocalService;

	@Inject
	private ClassNameLocalService _classNameLocalService;

	private CommerceCatalog _commerceCatalog;

	@Inject
	private CommerceCatalogLocalService _commerceCatalogLocalService;

	@DeleteAfterTestRun
	private List<CommerceChannel> _commerceChannels = new ArrayList<>();

	private CommerceCurrency _commerceCurrency;

	@Inject
	private CommerceOrderTypeLocalService _commerceOrderTypeLocalService;

	@DeleteAfterTestRun
	private List<CommerceOrderType> _commerceOrderTypes = new ArrayList<>();

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListChannelRelLocalService
		_commercePriceListChannelRelLocalService;

	@Inject
	private CommercePriceListOrderTypeRelLocalService
		_commercePriceListOrderTypeRelLocalService;

	@DeleteAfterTestRun
	private List<CommercePriceList> _commercePriceLists = new ArrayList<>();

	@Inject
	private CommercePriceModifierLocalService
		_commercePriceModifierLocalService;

	@Inject
	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

	@Inject
	private CPInstanceLocalService _cpInstanceLocalService;

	@DeleteAfterTestRun
	private List<CPInstance> _cpInstances = new ArrayList<>();

	private ServiceContext _serviceContext;

	@Inject
	private SystemEventLocalService _systemEventLocalService;

	private User _user;

	@Inject
	private UserLocalService _userLocalService;

}