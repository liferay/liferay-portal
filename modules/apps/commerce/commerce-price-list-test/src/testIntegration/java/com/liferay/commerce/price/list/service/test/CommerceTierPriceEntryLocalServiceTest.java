/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.CommerceTierPriceEntryMinQuantityException;
import com.liferay.commerce.price.list.exception.DuplicateCommerceTierPriceEntryException;
import com.liferay.commerce.price.list.exception.NoSuchPriceEntryException;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalService;
import com.liferay.commerce.price.list.service.CommercePriceListLocalService;
import com.liferay.commerce.price.list.service.CommerceTierPriceEntryLocalService;
import com.liferay.commerce.test.util.price.list.CommercePriceEntryTestUtil;
import com.liferay.commerce.test.util.price.list.CommerceTierPriceEntryTestUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.CompanyTestUtil;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.test.rule.Inject;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.hamcrest.CoreMatchers;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Zoltán Takács
 * @author Luca Pellizzon
 */
@RunWith(Arquillian.class)
public class CommerceTierPriceEntryLocalServiceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@BeforeClass
	public static void setUpClass() throws Exception {
		_company = CompanyTestUtil.addCompany();
	}

	@Before
	public void setUp() throws Exception {
		User guestUser = _company.getGuestUser();

		_group = GroupTestUtil.addGroup(
			_company.getCompanyId(), guestUser.getUserId(), 0);
	}

	@After
	public void tearDown() throws Exception {
		_commercePriceListLocalService.deleteCommercePriceLists(
			_company.getCompanyId());
	}

	@Test
	public void testAddCommerceTierPriceEntry1() throws Exception {
		frutillaRule.scenario(
			"Adding a Tier Price Entry to a Price Entry"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).when(
			"The price, promo price"
		).and(
			"The minimum quantity are checked against the input data"
		).then(
			"The result should be a new Tier Price Entry on the Price Entry"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		double minQuantity = RandomTestUtil.randomInt(1, 100);
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntry commerceTierPriceEntry =
			CommerceTierPriceEntryTestUtil.addCommerceTierPriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), minQuantity,
				price, promoPrice, null);

		_assertTierPriceEntryAttributes(
			commercePriceEntry, minQuantity, price, promoPrice,
			commerceTierPriceEntry);
	}

	@Test
	public void testAddCommerceTierPriceEntry2() throws Exception {
		frutillaRule.scenario(
			"Adding a Tier Price Entry to a Price Entry"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The external reference code"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).then(
			"The result should be a new Tier Price Entry on the Price Entry"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		String externalReferenceCode = RandomTestUtil.randomString();
		double minQuantity = RandomTestUtil.randomInt(1, 100);
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntry commerceTierPriceEntry =
			CommerceTierPriceEntryTestUtil.addCommerceTierPriceEntry(
				commercePriceEntry.getCommercePriceEntryId(), minQuantity,
				price, promoPrice, externalReferenceCode);

		_assertTierPriceEntryAttributes(
			commercePriceEntry, minQuantity, price, promoPrice,
			commerceTierPriceEntry);
		Assert.assertEquals(
			externalReferenceCode,
			commerceTierPriceEntry.getExternalReferenceCode());
	}

	@Test
	public void testAddOrUpdateCommerceTierPriceEntry1() throws Exception {
		frutillaRule.scenario(
			"Adding a Tier Price Entry to a Price Entry"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The external reference code"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).and(
			"commerceTierPriceEntryId, priceEntryExternalReferenceCode"
		).and(
			"entryExternalReferenceCode are not used"
		).then(
			"The result should be a new Tier Price Entry on the Price Entry"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		double minQuantity = RandomTestUtil.randomInt(1, 100);
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntry commerceTierPriceEntry =
			CommerceTierPriceEntryTestUtil.addOrUpdateCommerceTierPriceEntry(
				_company.getCompanyId(), 0L,
				commercePriceEntry.getCommercePriceEntryId(), minQuantity,
				price, promoPrice, null, null);

		_assertTierPriceEntryAttributes(
			commercePriceEntry, minQuantity, price, promoPrice,
			commerceTierPriceEntry);
	}

	@Test
	public void testAddOrUpdateCommerceTierPriceEntry2() throws Exception {
		frutillaRule.scenario(
			"Updating a Tier Price Entry on a Price Entry"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The external reference code"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).and(
			"commerceTierPriceEntryId"
		).and(
			"priceEntryExternalReferenceCode are not used"
		).and(
			"entryExternalReferenceCode is used"
		).then(
			"The result should be the updated Tier Price Entry"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		String externalReferenceCode = RandomTestUtil.randomString();
		double minQuantity = RandomTestUtil.randomInt(1, 100);
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntryTestUtil.addOrUpdateCommerceTierPriceEntry(
			_company.getCompanyId(), 0L,
			commercePriceEntry.getCommercePriceEntryId(), minQuantity, price,
			promoPrice, externalReferenceCode, null);

		CommerceTierPriceEntry commerceTierPriceEntry =
			_commerceTierPriceEntryLocalService.
				fetchCommerceTierPriceEntryByExternalReferenceCode(
					externalReferenceCode, _group.getCompanyId());

		_assertTierPriceEntryAttributes(
			commercePriceEntry, minQuantity, price, promoPrice,
			commerceTierPriceEntry);
	}

	@Test(expected = DuplicateCommerceTierPriceEntryException.class)
	public void testAddOrUpdateCommerceTierPriceEntry3() throws Exception {
		frutillaRule.scenario(
			"Adding a new Tier Price Entry on a Price Entry where the same " +
				"amount of minQuantity is already used in another entry"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The external reference code"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).and(
			"commerceTierPriceEntryId, priceEntryExternalReferenceCode"
		).and(
			"entryExternalReferenceCode are not used"
		).then(
			"The result should be a DuplicateCommerceTierPriceEntryException"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		double minQuantity = RandomTestUtil.randomInt(1, 100);
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntryTestUtil.addCommerceTierPriceEntry(
			commercePriceEntry.getCommercePriceEntryId(), minQuantity, price,
			promoPrice, null);

		CommerceTierPriceEntryTestUtil.addOrUpdateCommerceTierPriceEntry(
			_company.getCompanyId(), 0L,
			commercePriceEntry.getCommercePriceEntryId(), minQuantity, price,
			promoPrice, null, null);
	}

	@Test(expected = NoSuchPriceEntryException.class)
	public void testAddOrUpdateCommerceTierPriceEntry4() throws Exception {
		frutillaRule.scenario(
			"Adding a new Tier Price Entry where the referred Price Entry " +
				"does not exist"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The external reference code"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).and(
			"commerceTierPriceEntryId, priceEntryExternalReferenceCode"
		).and(
			"entryExternalReferenceCode are not used"
		).then(
			"The result should be a NoSuchPriceEntryException"
		);

		long commercePriceEntryId = RandomTestUtil.randomLong();

		double minQuantity = RandomTestUtil.randomDouble();
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntryTestUtil.addOrUpdateCommerceTierPriceEntry(
			_company.getCompanyId(), 0L, commercePriceEntryId, minQuantity,
			price, promoPrice, null, null);
	}

	@Test
	public void testAddOrUpdateCommerceTierPriceEntry5() throws Exception {
		frutillaRule.scenario(
			"Adding a new Tier Price Entry where the referred Price Entry is " +
				"given by its external reference code"
		).given(
			"A Price Entry's external reference code"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).and(
			"commerceTierPriceEntryId"
		).and(
			"entryExternalReferenceCode are not used"
		).and(
			"priceEntryExternalReferenceCode is used"
		).then(
			"The result should be a new Tier Price Entry on the Price Entry"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		String priceEntryExternalReferenceCode = RandomTestUtil.randomString();

		_commercePriceEntryLocalService.updateExternalReferenceCode(
			commercePriceEntry, priceEntryExternalReferenceCode);

		double minQuantity = BigDecimal.valueOf(
			RandomTestUtil.randomInt(1, 100), 0
		).doubleValue();
		double price = RandomTestUtil.randomDouble();
		double promoPrice = RandomTestUtil.randomDouble();

		CommerceTierPriceEntryTestUtil.addOrUpdateCommerceTierPriceEntry(
			_company.getCompanyId(), 0L, 0L, minQuantity, price, promoPrice,
			null, priceEntryExternalReferenceCode);

		CommercePriceEntry actualCommercePriceEntry =
			_commercePriceEntryLocalService.
				fetchCommercePriceEntryByExternalReferenceCode(
					priceEntryExternalReferenceCode, _group.getCompanyId());

		Assert.assertTrue(actualCommercePriceEntry.isHasTierPrice());

		Assert.assertThat(
			_commerceTierPriceEntryLocalService.
				getCommerceTierPriceEntriesCount(
					actualCommercePriceEntry.getCommercePriceEntryId()),
			CoreMatchers.equalTo(1));

		List<CommerceTierPriceEntry> commerceTierPriceEntries =
			_commerceTierPriceEntryLocalService.getCommerceTierPriceEntries(
				actualCommercePriceEntry.getCommercePriceEntryId(),
				QueryUtil.ALL_POS, QueryUtil.ALL_POS);

		_assertTierPriceEntryAttributes(
			actualCommercePriceEntry, minQuantity, price, promoPrice,
			commerceTierPriceEntries.get(0));
	}

	@Test(expected = CommerceTierPriceEntryMinQuantityException.class)
	public void testAddOrUpdateCommerceTierPriceEntry6() throws Exception {
		frutillaRule.scenario(
			"Adding a new Tier Price Entry where the referred Price Entry " +
				"does not exist"
		).given(
			"A Price Entry"
		).and(
			"The minimum quantity of the entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The external reference code"
		).when(
			"The price, promo price, external reference code"
		).and(
			"The minimum quantity are checked against the input data"
		).and(
			"commerceTierPriceEntryId, priceEntryExternalReferenceCode"
		).and(
			"entryExternalReferenceCode are not used"
		).then(
			"The result should be a CommerceTierPriceEntryMinQuantityException"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addCommercePriceEntry(
				_group.getGroupId());

		CommerceTierPriceEntryTestUtil.addOrUpdateCommerceTierPriceEntry(
			_company.getCompanyId(), 0L,
			commercePriceEntry.getCommercePriceEntryId(), -1,
			RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(), null,
			null);
	}

	@Rule
	public final FrutillaRule frutillaRule = new FrutillaRule();

	private void _assertTierPriceEntryAttributes(
			CommercePriceEntry commercePriceEntry, double minQuantity,
			double price, double promoPrice,
			CommerceTierPriceEntry commerceTierPriceEntry)
		throws Exception {

		CommercePriceEntry actualCommercePriceEntry =
			commerceTierPriceEntry.getCommercePriceEntry();

		Assert.assertThat(
			commercePriceEntry.getCommercePriceEntryId(),
			CoreMatchers.equalTo(
				actualCommercePriceEntry.getCommercePriceEntryId()));

		BigDecimal actualMinQuantity = commerceTierPriceEntry.getMinQuantity();

		Assert.assertEquals(
			minQuantity, actualMinQuantity.doubleValue(), 0.0001);

		BigDecimal actualPrice = commerceTierPriceEntry.getPrice();

		Assert.assertEquals(price, actualPrice.doubleValue(), 0.0001);

		BigDecimal actualPromoPrice = commerceTierPriceEntry.getPromoPrice();

		Assert.assertEquals(promoPrice, actualPromoPrice.doubleValue(), 0.0001);
	}

	private static Company _company;

	@Inject
	private CommercePriceEntryLocalService _commercePriceEntryLocalService;

	@Inject
	private CommercePriceListLocalService _commercePriceListLocalService;

	@Inject
	private CommerceTierPriceEntryLocalService
		_commerceTierPriceEntryLocalService;

	private Group _group;

}