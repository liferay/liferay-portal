/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.internal.model.listener.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.constants.CommercePriceListConstants;
import com.liferay.commerce.price.list.model.CommercePriceEntry;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceEntryLocalServiceUtil;
import com.liferay.commerce.price.list.service.persistence.CommercePriceEntryUtil;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.CPInstanceUnitOfMeasureLocalServiceUtil;
import com.liferay.commerce.product.test.util.CPTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceEntryTestUtil;
import com.liferay.commerce.test.util.price.list.CommercePriceListTestUtil;
import com.liferay.petra.string.StringPool;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.model.Group;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.rule.DeleteAfterTestRun;
import com.liferay.portal.kernel.test.util.GroupTestUtil;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TransactionTestUtil;
import com.liferay.portal.kernel.util.BigDecimalUtil;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PermissionCheckerMethodTestRule;

import java.math.BigDecimal;

import java.util.List;

import org.frutilla.FrutillaRule;

import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Crescenzo Rega
 */
@RunWith(Arquillian.class)
public class CPInstanceUnitOfMeasureModelListenerTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(),
			PermissionCheckerMethodTestRule.INSTANCE);

	@Before
	public void setUp() throws Exception {
		_group = GroupTestUtil.addGroup();

		_commercePriceList = CommercePriceListTestUtil.addCommercePriceList(
			_group.getGroupId(), true,
			CommercePriceListConstants.TYPE_PRICE_LIST,
			RandomTestUtil.nextDouble());
		_cpInstance = CPTestUtil.addCPInstanceWithRandomSkuFromCatalog(
			_group.getGroupId());
	}

	@Test
	public void testAddCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Add multiple CPInstance Unit Of Measure"
		).given(
			"A Price List"
		).and(
			"A (SKU) CpInstance in a random CpDefinition"
		).and(
			"The SKU of the new entry"
		).and(
			"The unit of measure key of the entry"
		).when(
			"The CPInstance Unit Of Measure is added"
		).then(
			"The price entries related to the sk are added"
		);

		CommercePriceEntryTestUtil.addOrUpdateCommercePriceEntry(
			null, 0, _cpInstance.getCPInstanceId(),
			_commercePriceList.getCommercePriceListId(), null,
			RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
			StringPool.BLANK);

		Assert.assertEquals(
			1,
			_getCommercePriceEntriesCount(
				_commercePriceList.getCommercePriceListId()));

		CPTestUtil.addCPInstanceUnitOfMeasure(
			_group.getGroupId(), _cpInstance.getCPInstanceId(),
			RandomTestUtil.randomString(), BigDecimal.ONE,
			_cpInstance.getSku());

		Assert.assertEquals(
			1,
			_getCommercePriceEntriesCount(
				_commercePriceList.getCommercePriceListId()));

		BigDecimal quantity = BigDecimal.TEN;
		String unitOfMeasureKey = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			_group.getGroupId(), _cpInstance.getCPInstanceId(),
			unitOfMeasureKey, quantity, _cpInstance.getSku());

		Assert.assertEquals(
			2,
			_getCommercePriceEntriesCount(
				_commercePriceList.getCommercePriceListId()));

		List<CommercePriceEntry> commercePriceEntries =
			CommercePriceEntryLocalServiceUtil.getCommercePriceEntries(
				_cpInstance.getCPInstanceUuid(), quantity, unitOfMeasureKey);

		Assert.assertFalse(commercePriceEntries.isEmpty());
	}

	@Test
	public void testAddFirstCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Add the first CPInstance Unit Of Measure"
		).given(
			"A Price List"
		).and(
			"A (SKU) CpInstance in a random CpDefinition"
		).and(
			"The SKU of the new entry"
		).and(
			"The unit of measure key of the entry"
		).when(
			"The CPInstance Unit Of Measure is added"
		).then(
			"Update key and quantity of the entry"
		);

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addOrUpdateCommercePriceEntry(
				null, 0, _cpInstance.getCPInstanceId(),
				_commercePriceList.getCommercePriceListId(), null,
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
				StringPool.BLANK);

		Assert.assertEquals(
			1,
			_getCommercePriceEntriesCount(
				_commercePriceList.getCommercePriceListId()));

		BigDecimal quantity = BigDecimal.TEN;
		String unitOfMeasureKey = RandomTestUtil.randomString();

		CPTestUtil.addCPInstanceUnitOfMeasure(
			_group.getGroupId(), _cpInstance.getCPInstanceId(),
			unitOfMeasureKey, quantity, _cpInstance.getSku());

		Assert.assertEquals(
			1,
			_getCommercePriceEntriesCount(
				_commercePriceList.getCommercePriceListId()));

		commercePriceEntry =
			CommercePriceEntryLocalServiceUtil.fetchCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId());

		Assert.assertTrue(
			BigDecimalUtil.eq(quantity, commercePriceEntry.getQuantity()));
		Assert.assertEquals(
			unitOfMeasureKey, commercePriceEntry.getUnitOfMeasureKey());
	}

	@Test
	public void testDeleteCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Delete a CPInstance Unit Of Measure"
		).given(
			"A Price List"
		).and(
			"A (SKU) CpInstance in a random CpDefinition"
		).and(
			"The SKU of the new entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The unit of measure key of the entry"
		).when(
			"The CPInstance Unit Of Measure is delete"
		).then(
			"it is not the last the remove"
		).and(
			"Also the corresponding price entry"
		);

		CommercePriceEntryTestUtil.addOrUpdateCommercePriceEntry(
			null, 0, _cpInstance.getCPInstanceId(),
			_commercePriceList.getCommercePriceListId(), null,
			RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
			StringPool.BLANK);

		CPTestUtil.addCPInstanceUnitOfMeasure(
			_group.getGroupId(), _cpInstance.getCPInstanceId(),
			RandomTestUtil.randomString(),
			BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10)),
			_cpInstance.getSku());

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				_group.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(),
				BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10)),
				_cpInstance.getSku());

		List<CommercePriceEntry> commercePriceEntries =
			_getCommercePriceEntries(
				_commercePriceList.getCommercePriceListId(), QueryUtil.ALL_POS,
				QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commercePriceEntries.toString(), 2, commercePriceEntries.size());

		CPInstanceUnitOfMeasureLocalServiceUtil.deleteCPInstanceUnitOfMeasure(
			cpInstanceUnitOfMeasure);

		commercePriceEntries = _getCommercePriceEntries(
			_commercePriceList.getCommercePriceListId(), QueryUtil.ALL_POS,
			QueryUtil.ALL_POS, null);

		Assert.assertEquals(
			commercePriceEntries.toString(), 1, commercePriceEntries.size());
	}

	@Test
	public void testDeleteLastCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Delete a CPInstance Unit Of Measure"
		).given(
			"A Price List"
		).and(
			"A (SKU) CpInstance in a random CpDefinition"
		).and(
			"The SKU of the new entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The unit of measure key of the entry"
		).when(
			"The CPInstance Unit Of Measure is delete"
		).then(
			"It is the last"
		).and(
			"Set key = null and quantity = 1 on commercePriceEntry"
		);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				_group.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(),
				BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10)),
				_cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addOrUpdateCommercePriceEntry(
				null, 0, _cpInstance.getCPInstanceId(),
				_commercePriceList.getCommercePriceListId(), null,
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
				cpInstanceUnitOfMeasure.getKey());

		CPInstanceUnitOfMeasureLocalServiceUtil.deleteCPInstanceUnitOfMeasure(
			cpInstanceUnitOfMeasure);

		commercePriceEntry =
			CommercePriceEntryLocalServiceUtil.fetchCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId());

		Assert.assertNull(commercePriceEntry.getQuantity());
		Assert.assertEquals(
			StringPool.BLANK, commercePriceEntry.getUnitOfMeasureKey());
	}

	@Test
	public void testUpdateCPInstanceUnitOfMeasure() throws Exception {
		frutillaRule.scenario(
			"Update a CPInstance Unit Of Measure"
		).given(
			"A Price List"
		).and(
			"A (SKU) CpInstance in a random CpDefinition"
		).and(
			"The SKU of the new entry"
		).and(
			"The price of the entry"
		).and(
			"The promo price of the entry"
		).and(
			"The unit of measure key of the entry"
		).when(
			"The CPInstance Unit Of Measure is updated"
		).then(
			"Update key and quantity"
		);

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure =
			CPTestUtil.addCPInstanceUnitOfMeasure(
				_group.getGroupId(), _cpInstance.getCPInstanceId(),
				RandomTestUtil.randomString(),
				BigDecimal.valueOf(RandomTestUtil.randomInt(1, 10)),
				_cpInstance.getSku());

		CommercePriceEntry commercePriceEntry =
			CommercePriceEntryTestUtil.addOrUpdateCommercePriceEntry(
				null, 0, _cpInstance.getCPInstanceId(),
				_commercePriceList.getCommercePriceListId(), null,
				RandomTestUtil.randomDouble(), RandomTestUtil.randomDouble(),
				cpInstanceUnitOfMeasure.getKey());

		BigDecimal quantity = BigDecimal.TEN;

		cpInstanceUnitOfMeasure.setIncrementalOrderQuantity(quantity);

		String unitOfMeasureKey = "new";

		cpInstanceUnitOfMeasure.setKey(unitOfMeasureKey);

		CPInstanceUnitOfMeasureLocalServiceUtil.updateCPInstanceUnitOfMeasure(
			cpInstanceUnitOfMeasure);

		commercePriceEntry =
			CommercePriceEntryLocalServiceUtil.fetchCommercePriceEntry(
				commercePriceEntry.getCommercePriceEntryId());

		Assert.assertTrue(
			BigDecimalUtil.eq(quantity, commercePriceEntry.getQuantity()));
		Assert.assertEquals(
			unitOfMeasureKey, commercePriceEntry.getUnitOfMeasureKey());
	}

	@Rule
	public FrutillaRule frutillaRule = new FrutillaRule();

	private List<CommercePriceEntry> _getCommercePriceEntries(
			long commercePriceListId, int start, int end,
			OrderByComparator<CommercePriceEntry> orderByComparator)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommercePriceEntryUtil.getPersistence(),
			commercePriceEntryPersistence ->
				commercePriceEntryPersistence.findByCommercePriceListId(
					commercePriceListId, start, end, orderByComparator));
	}

	private int _getCommercePriceEntriesCount(long commercePriceListId)
		throws Exception {

		return TransactionTestUtil.invoke(
			CommercePriceEntryUtil.getPersistence(),
			commercePriceEntryPersistence ->
				commercePriceEntryPersistence.countByCommercePriceListId(
					commercePriceListId));
	}

	@DeleteAfterTestRun
	private CommercePriceList _commercePriceList;

	@DeleteAfterTestRun
	private CPInstance _cpInstance;

	@DeleteAfterTestRun
	private Group _group;

}