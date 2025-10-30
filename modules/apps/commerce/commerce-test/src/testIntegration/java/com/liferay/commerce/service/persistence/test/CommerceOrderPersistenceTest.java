/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.DuplicateCommerceOrderExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderException;
import com.liferay.commerce.model.CommerceOrder;
import com.liferay.commerce.service.persistence.CommerceOrderPersistence;
import com.liferay.commerce.service.persistence.CommerceOrderUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;

import java.io.Serializable;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @generated
 */
@RunWith(Arquillian.class)
public class CommerceOrderPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceOrderUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceOrder> iterator = _commerceOrders.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrder commerceOrder = _persistence.create(pk);

		Assert.assertNotNull(commerceOrder);

		Assert.assertEquals(commerceOrder.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceOrder newCommerceOrder = addCommerceOrder();

		_persistence.remove(newCommerceOrder);

		CommerceOrder existingCommerceOrder = _persistence.fetchByPrimaryKey(
			newCommerceOrder.getPrimaryKey());

		Assert.assertNull(existingCommerceOrder);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceOrder();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrder newCommerceOrder = _persistence.create(pk);

		newCommerceOrder.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceOrder.setUuid(RandomTestUtil.randomString());

		newCommerceOrder.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceOrder.setGroupId(RandomTestUtil.nextLong());

		newCommerceOrder.setCompanyId(RandomTestUtil.nextLong());

		newCommerceOrder.setUserId(RandomTestUtil.nextLong());

		newCommerceOrder.setUserName(RandomTestUtil.randomString());

		newCommerceOrder.setCreateDate(RandomTestUtil.nextDate());

		newCommerceOrder.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceOrder.setBillingAddressId(RandomTestUtil.nextLong());

		newCommerceOrder.setCommerceAccountId(RandomTestUtil.nextLong());

		newCommerceOrder.setCommerceCurrencyCode(RandomTestUtil.randomString());

		newCommerceOrder.setCommerceOrderTypeId(RandomTestUtil.nextLong());

		newCommerceOrder.setCommerceShippingMethodId(RandomTestUtil.nextLong());

		newCommerceOrder.setDeliveryCommerceTermEntryId(
			RandomTestUtil.nextLong());

		newCommerceOrder.setPaymentCommerceTermEntryId(
			RandomTestUtil.nextLong());

		newCommerceOrder.setShippingAddressId(RandomTestUtil.nextLong());

		newCommerceOrder.setAdvanceStatus(RandomTestUtil.randomString());

		newCommerceOrder.setCommercePaymentMethodKey(
			RandomTestUtil.randomString());

		newCommerceOrder.setCouponCode(RandomTestUtil.randomString());

		newCommerceOrder.setDeliveryCommerceTermEntryDescription(
			RandomTestUtil.randomString());

		newCommerceOrder.setDeliveryCommerceTermEntryName(
			RandomTestUtil.randomString());

		newCommerceOrder.setLastPriceUpdateDate(RandomTestUtil.nextDate());

		newCommerceOrder.setManuallyAdjusted(RandomTestUtil.randomBoolean());

		newCommerceOrder.setName(RandomTestUtil.randomString());

		newCommerceOrder.setOrderDate(RandomTestUtil.nextDate());

		newCommerceOrder.setOrderStatus(RandomTestUtil.nextInt());

		newCommerceOrder.setPaymentCommerceTermEntryDescription(
			RandomTestUtil.randomString());

		newCommerceOrder.setPaymentCommerceTermEntryName(
			RandomTestUtil.randomString());

		newCommerceOrder.setPaymentStatus(RandomTestUtil.nextInt());

		newCommerceOrder.setPrintedNote(RandomTestUtil.randomString());

		newCommerceOrder.setPurchaseOrderNumber(RandomTestUtil.randomString());

		newCommerceOrder.setRequestedDeliveryDate(RandomTestUtil.nextDate());

		newCommerceOrder.setShippable(RandomTestUtil.randomBoolean());

		newCommerceOrder.setShippingAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel1WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel2WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel3WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountPercentageLevel4WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingDiscountWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setShippingOptionName(RandomTestUtil.randomString());

		newCommerceOrder.setShippingWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotal(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel1WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel2WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel3WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountPercentageLevel4WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalDiscountWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setSubtotalWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotal(new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel1WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel2WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel3WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountPercentageLevel4WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalDiscountWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTotalWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceOrder.setTransactionId(RandomTestUtil.randomString());

		newCommerceOrder.setStatus(RandomTestUtil.nextInt());

		newCommerceOrder.setStatusByUserId(RandomTestUtil.nextLong());

		newCommerceOrder.setStatusByUserName(RandomTestUtil.randomString());

		newCommerceOrder.setStatusDate(RandomTestUtil.nextDate());

		_commerceOrders.add(_persistence.update(newCommerceOrder));

		CommerceOrder existingCommerceOrder = _persistence.findByPrimaryKey(
			newCommerceOrder.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrder.getMvccVersion(),
			newCommerceOrder.getMvccVersion());
		Assert.assertEquals(
			existingCommerceOrder.getUuid(), newCommerceOrder.getUuid());
		Assert.assertEquals(
			existingCommerceOrder.getExternalReferenceCode(),
			newCommerceOrder.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceOrder.getCommerceOrderId(),
			newCommerceOrder.getCommerceOrderId());
		Assert.assertEquals(
			existingCommerceOrder.getGroupId(), newCommerceOrder.getGroupId());
		Assert.assertEquals(
			existingCommerceOrder.getCompanyId(),
			newCommerceOrder.getCompanyId());
		Assert.assertEquals(
			existingCommerceOrder.getUserId(), newCommerceOrder.getUserId());
		Assert.assertEquals(
			existingCommerceOrder.getUserName(),
			newCommerceOrder.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrder.getCreateDate()),
			Time.getShortTimestamp(newCommerceOrder.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrder.getModifiedDate()),
			Time.getShortTimestamp(newCommerceOrder.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceOrder.getBillingAddressId(),
			newCommerceOrder.getBillingAddressId());
		Assert.assertEquals(
			existingCommerceOrder.getCommerceAccountId(),
			newCommerceOrder.getCommerceAccountId());
		Assert.assertEquals(
			existingCommerceOrder.getCommerceCurrencyCode(),
			newCommerceOrder.getCommerceCurrencyCode());
		Assert.assertEquals(
			existingCommerceOrder.getCommerceOrderTypeId(),
			newCommerceOrder.getCommerceOrderTypeId());
		Assert.assertEquals(
			existingCommerceOrder.getCommerceShippingMethodId(),
			newCommerceOrder.getCommerceShippingMethodId());
		Assert.assertEquals(
			existingCommerceOrder.getDeliveryCommerceTermEntryId(),
			newCommerceOrder.getDeliveryCommerceTermEntryId());
		Assert.assertEquals(
			existingCommerceOrder.getPaymentCommerceTermEntryId(),
			newCommerceOrder.getPaymentCommerceTermEntryId());
		Assert.assertEquals(
			existingCommerceOrder.getShippingAddressId(),
			newCommerceOrder.getShippingAddressId());
		Assert.assertEquals(
			existingCommerceOrder.getAdvanceStatus(),
			newCommerceOrder.getAdvanceStatus());
		Assert.assertEquals(
			existingCommerceOrder.getCommercePaymentMethodKey(),
			newCommerceOrder.getCommercePaymentMethodKey());
		Assert.assertEquals(
			existingCommerceOrder.getCouponCode(),
			newCommerceOrder.getCouponCode());
		Assert.assertEquals(
			existingCommerceOrder.getDeliveryCommerceTermEntryDescription(),
			newCommerceOrder.getDeliveryCommerceTermEntryDescription());
		Assert.assertEquals(
			existingCommerceOrder.getDeliveryCommerceTermEntryName(),
			newCommerceOrder.getDeliveryCommerceTermEntryName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrder.getLastPriceUpdateDate()),
			Time.getShortTimestamp(newCommerceOrder.getLastPriceUpdateDate()));
		Assert.assertEquals(
			existingCommerceOrder.isManuallyAdjusted(),
			newCommerceOrder.isManuallyAdjusted());
		Assert.assertEquals(
			existingCommerceOrder.getName(), newCommerceOrder.getName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrder.getOrderDate()),
			Time.getShortTimestamp(newCommerceOrder.getOrderDate()));
		Assert.assertEquals(
			existingCommerceOrder.getOrderStatus(),
			newCommerceOrder.getOrderStatus());
		Assert.assertEquals(
			existingCommerceOrder.getPaymentCommerceTermEntryDescription(),
			newCommerceOrder.getPaymentCommerceTermEntryDescription());
		Assert.assertEquals(
			existingCommerceOrder.getPaymentCommerceTermEntryName(),
			newCommerceOrder.getPaymentCommerceTermEntryName());
		Assert.assertEquals(
			existingCommerceOrder.getPaymentStatus(),
			newCommerceOrder.getPaymentStatus());
		Assert.assertEquals(
			existingCommerceOrder.getPrintedNote(),
			newCommerceOrder.getPrintedNote());
		Assert.assertEquals(
			existingCommerceOrder.getPurchaseOrderNumber(),
			newCommerceOrder.getPurchaseOrderNumber());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrder.getRequestedDeliveryDate()),
			Time.getShortTimestamp(
				newCommerceOrder.getRequestedDeliveryDate()));
		Assert.assertEquals(
			existingCommerceOrder.isShippable(),
			newCommerceOrder.isShippable());
		Assert.assertEquals(
			existingCommerceOrder.getShippingAmount(),
			newCommerceOrder.getShippingAmount());
		Assert.assertEquals(
			existingCommerceOrder.getShippingDiscountAmount(),
			newCommerceOrder.getShippingDiscountAmount());
		Assert.assertEquals(
			existingCommerceOrder.getShippingDiscountPercentageLevel1(),
			newCommerceOrder.getShippingDiscountPercentageLevel1());
		Assert.assertEquals(
			existingCommerceOrder.getShippingDiscountPercentageLevel2(),
			newCommerceOrder.getShippingDiscountPercentageLevel2());
		Assert.assertEquals(
			existingCommerceOrder.getShippingDiscountPercentageLevel3(),
			newCommerceOrder.getShippingDiscountPercentageLevel3());
		Assert.assertEquals(
			existingCommerceOrder.getShippingDiscountPercentageLevel4(),
			newCommerceOrder.getShippingDiscountPercentageLevel4());
		Assert.assertEquals(
			existingCommerceOrder.
				getShippingDiscountPercentageLevel1WithTaxAmount(),
			newCommerceOrder.
				getShippingDiscountPercentageLevel1WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getShippingDiscountPercentageLevel2WithTaxAmount(),
			newCommerceOrder.
				getShippingDiscountPercentageLevel2WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getShippingDiscountPercentageLevel3WithTaxAmount(),
			newCommerceOrder.
				getShippingDiscountPercentageLevel3WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getShippingDiscountPercentageLevel4WithTaxAmount(),
			newCommerceOrder.
				getShippingDiscountPercentageLevel4WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getShippingDiscountWithTaxAmount(),
			newCommerceOrder.getShippingDiscountWithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getShippingOptionName(),
			newCommerceOrder.getShippingOptionName());
		Assert.assertEquals(
			existingCommerceOrder.getShippingWithTaxAmount(),
			newCommerceOrder.getShippingWithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotal(),
			newCommerceOrder.getSubtotal());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalDiscountAmount(),
			newCommerceOrder.getSubtotalDiscountAmount());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalDiscountPercentageLevel1(),
			newCommerceOrder.getSubtotalDiscountPercentageLevel1());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalDiscountPercentageLevel2(),
			newCommerceOrder.getSubtotalDiscountPercentageLevel2());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalDiscountPercentageLevel3(),
			newCommerceOrder.getSubtotalDiscountPercentageLevel3());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalDiscountPercentageLevel4(),
			newCommerceOrder.getSubtotalDiscountPercentageLevel4());
		Assert.assertEquals(
			existingCommerceOrder.
				getSubtotalDiscountPercentageLevel1WithTaxAmount(),
			newCommerceOrder.
				getSubtotalDiscountPercentageLevel1WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getSubtotalDiscountPercentageLevel2WithTaxAmount(),
			newCommerceOrder.
				getSubtotalDiscountPercentageLevel2WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getSubtotalDiscountPercentageLevel3WithTaxAmount(),
			newCommerceOrder.
				getSubtotalDiscountPercentageLevel3WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getSubtotalDiscountPercentageLevel4WithTaxAmount(),
			newCommerceOrder.
				getSubtotalDiscountPercentageLevel4WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalDiscountWithTaxAmount(),
			newCommerceOrder.getSubtotalDiscountWithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getSubtotalWithTaxAmount(),
			newCommerceOrder.getSubtotalWithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getTaxAmount(),
			newCommerceOrder.getTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getTotal(), newCommerceOrder.getTotal());
		Assert.assertEquals(
			existingCommerceOrder.getTotalDiscountAmount(),
			newCommerceOrder.getTotalDiscountAmount());
		Assert.assertEquals(
			existingCommerceOrder.getTotalDiscountPercentageLevel1(),
			newCommerceOrder.getTotalDiscountPercentageLevel1());
		Assert.assertEquals(
			existingCommerceOrder.getTotalDiscountPercentageLevel2(),
			newCommerceOrder.getTotalDiscountPercentageLevel2());
		Assert.assertEquals(
			existingCommerceOrder.getTotalDiscountPercentageLevel3(),
			newCommerceOrder.getTotalDiscountPercentageLevel3());
		Assert.assertEquals(
			existingCommerceOrder.getTotalDiscountPercentageLevel4(),
			newCommerceOrder.getTotalDiscountPercentageLevel4());
		Assert.assertEquals(
			existingCommerceOrder.
				getTotalDiscountPercentageLevel1WithTaxAmount(),
			newCommerceOrder.getTotalDiscountPercentageLevel1WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getTotalDiscountPercentageLevel2WithTaxAmount(),
			newCommerceOrder.getTotalDiscountPercentageLevel2WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getTotalDiscountPercentageLevel3WithTaxAmount(),
			newCommerceOrder.getTotalDiscountPercentageLevel3WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.
				getTotalDiscountPercentageLevel4WithTaxAmount(),
			newCommerceOrder.getTotalDiscountPercentageLevel4WithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getTotalDiscountWithTaxAmount(),
			newCommerceOrder.getTotalDiscountWithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getTotalWithTaxAmount(),
			newCommerceOrder.getTotalWithTaxAmount());
		Assert.assertEquals(
			existingCommerceOrder.getTransactionId(),
			newCommerceOrder.getTransactionId());
		Assert.assertEquals(
			existingCommerceOrder.getStatus(), newCommerceOrder.getStatus());
		Assert.assertEquals(
			existingCommerceOrder.getStatusByUserId(),
			newCommerceOrder.getStatusByUserId());
		Assert.assertEquals(
			existingCommerceOrder.getStatusByUserName(),
			newCommerceOrder.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrder.getStatusDate()),
			Time.getShortTimestamp(newCommerceOrder.getStatusDate()));
	}

	@Test(expected = DuplicateCommerceOrderExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceOrder commerceOrder = addCommerceOrder();

		CommerceOrder newCommerceOrder = addCommerceOrder();

		newCommerceOrder.setCompanyId(commerceOrder.getCompanyId());

		newCommerceOrder = _persistence.update(newCommerceOrder);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceOrder);

		newCommerceOrder.setExternalReferenceCode(
			commerceOrder.getExternalReferenceCode());

		_persistence.update(newCommerceOrder);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByBillingAddressId() throws Exception {
		_persistence.countByBillingAddressId(RandomTestUtil.nextLong());

		_persistence.countByBillingAddressId(0L);
	}

	@Test
	public void testCountByCommerceAccountId() throws Exception {
		_persistence.countByCommerceAccountId(RandomTestUtil.nextLong());

		_persistence.countByCommerceAccountId(0L);
	}

	@Test
	public void testCountByShippingAddressId() throws Exception {
		_persistence.countByShippingAddressId(RandomTestUtil.nextLong());

		_persistence.countByShippingAddressId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_CP() throws Exception {
		_persistence.countByG_CP(RandomTestUtil.nextLong(), "");

		_persistence.countByG_CP(0L, "null");

		_persistence.countByG_CP(0L, (String)null);
	}

	@Test
	public void testCountByG_U_O() throws Exception {
		_persistence.countByG_U_O(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_U_O(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_O() throws Exception {
		_persistence.countByG_C_O(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_O(0L, 0L, 0);
	}

	@Test
	public void testCountByU_LtC_O() throws Exception {
		_persistence.countByU_LtC_O(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextInt());

		_persistence.countByU_LtC_O(0L, RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByC_LtC_O() throws Exception {
		_persistence.countByC_LtC_O(
			RandomTestUtil.nextDate(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByC_LtC_O(RandomTestUtil.nextDate(), 0L, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceOrder newCommerceOrder = addCommerceOrder();

		CommerceOrder existingCommerceOrder = _persistence.findByPrimaryKey(
			newCommerceOrder.getPrimaryKey());

		Assert.assertEquals(existingCommerceOrder, newCommerceOrder);
	}

	@Test(expected = NoSuchOrderException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceOrder> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceOrder", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceOrderId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "billingAddressId", true,
			"commerceAccountId", true, "commerceCurrencyCode", true,
			"commerceOrderTypeId", true, "commerceShippingMethodId", true,
			"deliveryCommerceTermEntryId", true, "paymentCommerceTermEntryId",
			true, "shippingAddressId", true, "advanceStatus", true,
			"commercePaymentMethodKey", true, "couponCode", true,
			"deliveryCommerceTermEntryName", true, "lastPriceUpdateDate", true,
			"manuallyAdjusted", true, "name", true, "orderDate", true,
			"orderStatus", true, "paymentCommerceTermEntryName", true,
			"paymentStatus", true, "printedNote", true, "purchaseOrderNumber",
			true, "requestedDeliveryDate", true, "shippable", true,
			"shippingAmount", true, "shippingDiscountAmount", true,
			"shippingDiscountPercentageLevel1", true,
			"shippingDiscountPercentageLevel2", true,
			"shippingDiscountPercentageLevel3", true,
			"shippingDiscountPercentageLevel4", true,
			"shippingDiscountPercentageLevel1WithTaxAmount", true,
			"shippingDiscountPercentageLevel2WithTaxAmount", true,
			"shippingDiscountPercentageLevel3WithTaxAmount", true,
			"shippingDiscountPercentageLevel4WithTaxAmount", true,
			"shippingDiscountWithTaxAmount", true, "shippingOptionName", true,
			"shippingWithTaxAmount", true, "subtotal", true,
			"subtotalDiscountAmount", true, "subtotalDiscountPercentageLevel1",
			true, "subtotalDiscountPercentageLevel2", true,
			"subtotalDiscountPercentageLevel3", true,
			"subtotalDiscountPercentageLevel4", true,
			"subtotalDiscountPercentageLevel1WithTaxAmount", true,
			"subtotalDiscountPercentageLevel2WithTaxAmount", true,
			"subtotalDiscountPercentageLevel3WithTaxAmount", true,
			"subtotalDiscountPercentageLevel4WithTaxAmount", true,
			"subtotalDiscountWithTaxAmount", true, "subtotalWithTaxAmount",
			true, "taxAmount", true, "total", true, "totalDiscountAmount", true,
			"totalDiscountPercentageLevel1", true,
			"totalDiscountPercentageLevel2", true,
			"totalDiscountPercentageLevel3", true,
			"totalDiscountPercentageLevel4", true,
			"totalDiscountPercentageLevel1WithTaxAmount", true,
			"totalDiscountPercentageLevel2WithTaxAmount", true,
			"totalDiscountPercentageLevel3WithTaxAmount", true,
			"totalDiscountPercentageLevel4WithTaxAmount", true,
			"totalDiscountWithTaxAmount", true, "totalWithTaxAmount", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceOrder newCommerceOrder = addCommerceOrder();

		CommerceOrder existingCommerceOrder = _persistence.fetchByPrimaryKey(
			newCommerceOrder.getPrimaryKey());

		Assert.assertEquals(existingCommerceOrder, newCommerceOrder);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrder missingCommerceOrder = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceOrder);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceOrder newCommerceOrder1 = addCommerceOrder();
		CommerceOrder newCommerceOrder2 = addCommerceOrder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrder1.getPrimaryKey());
		primaryKeys.add(newCommerceOrder2.getPrimaryKey());

		Map<Serializable, CommerceOrder> commerceOrders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceOrders.size());
		Assert.assertEquals(
			newCommerceOrder1,
			commerceOrders.get(newCommerceOrder1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceOrder2,
			commerceOrders.get(newCommerceOrder2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceOrder> commerceOrders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceOrder newCommerceOrder = addCommerceOrder();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrder.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceOrder> commerceOrders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrders.size());
		Assert.assertEquals(
			newCommerceOrder,
			commerceOrders.get(newCommerceOrder.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceOrder> commerceOrders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrders.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceOrder newCommerceOrder = addCommerceOrder();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrder.getPrimaryKey());

		Map<Serializable, CommerceOrder> commerceOrders =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrders.size());
		Assert.assertEquals(
			newCommerceOrder,
			commerceOrders.get(newCommerceOrder.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceOrder newCommerceOrder = addCommerceOrder();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCommerceOrder.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceOrder commerceOrder) {
		Assert.assertEquals(
			commerceOrder.getUuid(),
			ReflectionTestUtil.invoke(
				commerceOrder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceOrder.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			commerceOrder.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceOrder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceOrder.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrder, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceOrder addCommerceOrder() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrder commerceOrder = _persistence.create(pk);

		commerceOrder.setMvccVersion(RandomTestUtil.nextLong());

		commerceOrder.setUuid(RandomTestUtil.randomString());

		commerceOrder.setExternalReferenceCode(RandomTestUtil.randomString());

		commerceOrder.setGroupId(RandomTestUtil.nextLong());

		commerceOrder.setCompanyId(RandomTestUtil.nextLong());

		commerceOrder.setUserId(RandomTestUtil.nextLong());

		commerceOrder.setUserName(RandomTestUtil.randomString());

		commerceOrder.setCreateDate(RandomTestUtil.nextDate());

		commerceOrder.setModifiedDate(RandomTestUtil.nextDate());

		commerceOrder.setBillingAddressId(RandomTestUtil.nextLong());

		commerceOrder.setCommerceAccountId(RandomTestUtil.nextLong());

		commerceOrder.setCommerceCurrencyCode(RandomTestUtil.randomString());

		commerceOrder.setCommerceOrderTypeId(RandomTestUtil.nextLong());

		commerceOrder.setCommerceShippingMethodId(RandomTestUtil.nextLong());

		commerceOrder.setDeliveryCommerceTermEntryId(RandomTestUtil.nextLong());

		commerceOrder.setPaymentCommerceTermEntryId(RandomTestUtil.nextLong());

		commerceOrder.setShippingAddressId(RandomTestUtil.nextLong());

		commerceOrder.setAdvanceStatus(RandomTestUtil.randomString());

		commerceOrder.setCommercePaymentMethodKey(
			RandomTestUtil.randomString());

		commerceOrder.setCouponCode(RandomTestUtil.randomString());

		commerceOrder.setDeliveryCommerceTermEntryDescription(
			RandomTestUtil.randomString());

		commerceOrder.setDeliveryCommerceTermEntryName(
			RandomTestUtil.randomString());

		commerceOrder.setLastPriceUpdateDate(RandomTestUtil.nextDate());

		commerceOrder.setManuallyAdjusted(RandomTestUtil.randomBoolean());

		commerceOrder.setName(RandomTestUtil.randomString());

		commerceOrder.setOrderDate(RandomTestUtil.nextDate());

		commerceOrder.setOrderStatus(RandomTestUtil.nextInt());

		commerceOrder.setPaymentCommerceTermEntryDescription(
			RandomTestUtil.randomString());

		commerceOrder.setPaymentCommerceTermEntryName(
			RandomTestUtil.randomString());

		commerceOrder.setPaymentStatus(RandomTestUtil.nextInt());

		commerceOrder.setPrintedNote(RandomTestUtil.randomString());

		commerceOrder.setPurchaseOrderNumber(RandomTestUtil.randomString());

		commerceOrder.setRequestedDeliveryDate(RandomTestUtil.nextDate());

		commerceOrder.setShippable(RandomTestUtil.randomBoolean());

		commerceOrder.setShippingAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel1WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel2WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel3WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountPercentageLevel4WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingDiscountWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setShippingOptionName(RandomTestUtil.randomString());

		commerceOrder.setShippingWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotal(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel1WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel2WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel3WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountPercentageLevel4WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalDiscountWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setSubtotalWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTaxAmount(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotal(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel1WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel2WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel3WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountPercentageLevel4WithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalDiscountWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTotalWithTaxAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceOrder.setTransactionId(RandomTestUtil.randomString());

		commerceOrder.setStatus(RandomTestUtil.nextInt());

		commerceOrder.setStatusByUserId(RandomTestUtil.nextLong());

		commerceOrder.setStatusByUserName(RandomTestUtil.randomString());

		commerceOrder.setStatusDate(RandomTestUtil.nextDate());

		_commerceOrders.add(_persistence.update(commerceOrder));

		return commerceOrder;
	}

	private List<CommerceOrder> _commerceOrders =
		new ArrayList<CommerceOrder>();
	private CommerceOrderPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}