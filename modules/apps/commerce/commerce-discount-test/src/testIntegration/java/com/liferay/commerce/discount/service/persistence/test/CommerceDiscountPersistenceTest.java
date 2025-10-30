/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.exception.DuplicateCommerceDiscountExternalReferenceCodeException;
import com.liferay.commerce.discount.exception.NoSuchDiscountException;
import com.liferay.commerce.discount.model.CommerceDiscount;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
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
public class CommerceDiscountPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.discount.service"));

	@Before
	public void setUp() {
		_persistence = CommerceDiscountUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceDiscount> iterator = _commerceDiscounts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscount commerceDiscount = _persistence.create(pk);

		Assert.assertNotNull(commerceDiscount);

		Assert.assertEquals(commerceDiscount.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		_persistence.remove(newCommerceDiscount);

		CommerceDiscount existingCommerceDiscount =
			_persistence.fetchByPrimaryKey(newCommerceDiscount.getPrimaryKey());

		Assert.assertNull(existingCommerceDiscount);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceDiscount();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscount newCommerceDiscount = _persistence.create(pk);

		newCommerceDiscount.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceDiscount.setUuid(RandomTestUtil.randomString());

		newCommerceDiscount.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceDiscount.setCompanyId(RandomTestUtil.nextLong());

		newCommerceDiscount.setUserId(RandomTestUtil.nextLong());

		newCommerceDiscount.setUserName(RandomTestUtil.randomString());

		newCommerceDiscount.setCreateDate(RandomTestUtil.nextDate());

		newCommerceDiscount.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceDiscount.setTitle(RandomTestUtil.randomString());

		newCommerceDiscount.setTarget(RandomTestUtil.randomString());

		newCommerceDiscount.setUseCouponCode(RandomTestUtil.randomBoolean());

		newCommerceDiscount.setCouponCode(RandomTestUtil.randomString());

		newCommerceDiscount.setUsePercentage(RandomTestUtil.randomBoolean());

		newCommerceDiscount.setMaximumDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceDiscount.setLevel(RandomTestUtil.randomString());

		newCommerceDiscount.setLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceDiscount.setLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceDiscount.setLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceDiscount.setLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceDiscount.setLimitationType(RandomTestUtil.randomString());

		newCommerceDiscount.setLimitationTimes(RandomTestUtil.nextInt());

		newCommerceDiscount.setLimitationTimesPerAccount(
			RandomTestUtil.nextInt());

		newCommerceDiscount.setNumberOfUse(RandomTestUtil.nextInt());

		newCommerceDiscount.setRulesConjunction(RandomTestUtil.randomBoolean());

		newCommerceDiscount.setActive(RandomTestUtil.randomBoolean());

		newCommerceDiscount.setDisplayDate(RandomTestUtil.nextDate());

		newCommerceDiscount.setExpirationDate(RandomTestUtil.nextDate());

		newCommerceDiscount.setLastPublishDate(RandomTestUtil.nextDate());

		newCommerceDiscount.setStatus(RandomTestUtil.nextInt());

		newCommerceDiscount.setStatusByUserId(RandomTestUtil.nextLong());

		newCommerceDiscount.setStatusByUserName(RandomTestUtil.randomString());

		newCommerceDiscount.setStatusDate(RandomTestUtil.nextDate());

		_commerceDiscounts.add(_persistence.update(newCommerceDiscount));

		CommerceDiscount existingCommerceDiscount =
			_persistence.findByPrimaryKey(newCommerceDiscount.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscount.getMvccVersion(),
			newCommerceDiscount.getMvccVersion());
		Assert.assertEquals(
			existingCommerceDiscount.getUuid(), newCommerceDiscount.getUuid());
		Assert.assertEquals(
			existingCommerceDiscount.getExternalReferenceCode(),
			newCommerceDiscount.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceDiscount.getCommerceDiscountId(),
			newCommerceDiscount.getCommerceDiscountId());
		Assert.assertEquals(
			existingCommerceDiscount.getCompanyId(),
			newCommerceDiscount.getCompanyId());
		Assert.assertEquals(
			existingCommerceDiscount.getUserId(),
			newCommerceDiscount.getUserId());
		Assert.assertEquals(
			existingCommerceDiscount.getUserName(),
			newCommerceDiscount.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceDiscount.getCreateDate()),
			Time.getShortTimestamp(newCommerceDiscount.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceDiscount.getModifiedDate()),
			Time.getShortTimestamp(newCommerceDiscount.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceDiscount.getTitle(),
			newCommerceDiscount.getTitle());
		Assert.assertEquals(
			existingCommerceDiscount.getTarget(),
			newCommerceDiscount.getTarget());
		Assert.assertEquals(
			existingCommerceDiscount.isUseCouponCode(),
			newCommerceDiscount.isUseCouponCode());
		Assert.assertEquals(
			existingCommerceDiscount.getCouponCode(),
			newCommerceDiscount.getCouponCode());
		Assert.assertEquals(
			existingCommerceDiscount.isUsePercentage(),
			newCommerceDiscount.isUsePercentage());
		Assert.assertEquals(
			existingCommerceDiscount.getMaximumDiscountAmount(),
			newCommerceDiscount.getMaximumDiscountAmount());
		Assert.assertEquals(
			existingCommerceDiscount.getLevel(),
			newCommerceDiscount.getLevel());
		Assert.assertEquals(
			existingCommerceDiscount.getLevel1(),
			newCommerceDiscount.getLevel1());
		Assert.assertEquals(
			existingCommerceDiscount.getLevel2(),
			newCommerceDiscount.getLevel2());
		Assert.assertEquals(
			existingCommerceDiscount.getLevel3(),
			newCommerceDiscount.getLevel3());
		Assert.assertEquals(
			existingCommerceDiscount.getLevel4(),
			newCommerceDiscount.getLevel4());
		Assert.assertEquals(
			existingCommerceDiscount.getLimitationType(),
			newCommerceDiscount.getLimitationType());
		Assert.assertEquals(
			existingCommerceDiscount.getLimitationTimes(),
			newCommerceDiscount.getLimitationTimes());
		Assert.assertEquals(
			existingCommerceDiscount.getLimitationTimesPerAccount(),
			newCommerceDiscount.getLimitationTimesPerAccount());
		Assert.assertEquals(
			existingCommerceDiscount.getNumberOfUse(),
			newCommerceDiscount.getNumberOfUse());
		Assert.assertEquals(
			existingCommerceDiscount.isRulesConjunction(),
			newCommerceDiscount.isRulesConjunction());
		Assert.assertEquals(
			existingCommerceDiscount.isActive(),
			newCommerceDiscount.isActive());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceDiscount.getDisplayDate()),
			Time.getShortTimestamp(newCommerceDiscount.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscount.getExpirationDate()),
			Time.getShortTimestamp(newCommerceDiscount.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscount.getLastPublishDate()),
			Time.getShortTimestamp(newCommerceDiscount.getLastPublishDate()));
		Assert.assertEquals(
			existingCommerceDiscount.getStatus(),
			newCommerceDiscount.getStatus());
		Assert.assertEquals(
			existingCommerceDiscount.getStatusByUserId(),
			newCommerceDiscount.getStatusByUserId());
		Assert.assertEquals(
			existingCommerceDiscount.getStatusByUserName(),
			newCommerceDiscount.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceDiscount.getStatusDate()),
			Time.getShortTimestamp(newCommerceDiscount.getStatusDate()));
	}

	@Test(
		expected = DuplicateCommerceDiscountExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceDiscount commerceDiscount = addCommerceDiscount();

		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		newCommerceDiscount.setCompanyId(commerceDiscount.getCompanyId());

		newCommerceDiscount = _persistence.update(newCommerceDiscount);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceDiscount);

		newCommerceDiscount.setExternalReferenceCode(
			commerceDiscount.getExternalReferenceCode());

		_persistence.update(newCommerceDiscount);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByLtE_S() throws Exception {
		_persistence.countByLtE_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtE_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByC_C_A() throws Exception {
		_persistence.countByC_C_A(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean());

		_persistence.countByC_C_A(0L, "null", RandomTestUtil.randomBoolean());

		_persistence.countByC_C_A(
			0L, (String)null, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_L_A_S() throws Exception {
		_persistence.countByC_L_A_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByC_L_A_S(
			0L, "null", RandomTestUtil.randomBoolean(), 0);

		_persistence.countByC_L_A_S(
			0L, (String)null, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		CommerceDiscount existingCommerceDiscount =
			_persistence.findByPrimaryKey(newCommerceDiscount.getPrimaryKey());

		Assert.assertEquals(existingCommerceDiscount, newCommerceDiscount);
	}

	@Test(expected = NoSuchDiscountException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceDiscount> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceDiscount", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceDiscountId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "title", true, "target", true,
			"useCouponCode", true, "couponCode", true, "usePercentage", true,
			"maximumDiscountAmount", true, "level", true, "level1", true,
			"level2", true, "level3", true, "level4", true, "limitationType",
			true, "limitationTimes", true, "limitationTimesPerAccount", true,
			"numberOfUse", true, "rulesConjunction", true, "active", true,
			"displayDate", true, "expirationDate", true, "lastPublishDate",
			true, "status", true, "statusByUserId", true, "statusByUserName",
			true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		CommerceDiscount existingCommerceDiscount =
			_persistence.fetchByPrimaryKey(newCommerceDiscount.getPrimaryKey());

		Assert.assertEquals(existingCommerceDiscount, newCommerceDiscount);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscount missingCommerceDiscount =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceDiscount);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceDiscount newCommerceDiscount1 = addCommerceDiscount();
		CommerceDiscount newCommerceDiscount2 = addCommerceDiscount();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscount1.getPrimaryKey());
		primaryKeys.add(newCommerceDiscount2.getPrimaryKey());

		Map<Serializable, CommerceDiscount> commerceDiscounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceDiscounts.size());
		Assert.assertEquals(
			newCommerceDiscount1,
			commerceDiscounts.get(newCommerceDiscount1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceDiscount2,
			commerceDiscounts.get(newCommerceDiscount2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceDiscount> commerceDiscounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceDiscounts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscount.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceDiscount> commerceDiscounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceDiscounts.size());
		Assert.assertEquals(
			newCommerceDiscount,
			commerceDiscounts.get(newCommerceDiscount.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceDiscount> commerceDiscounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceDiscounts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscount.getPrimaryKey());

		Map<Serializable, CommerceDiscount> commerceDiscounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceDiscounts.size());
		Assert.assertEquals(
			newCommerceDiscount,
			commerceDiscounts.get(newCommerceDiscount.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceDiscount newCommerceDiscount = addCommerceDiscount();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCommerceDiscount.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceDiscount commerceDiscount) {
		Assert.assertEquals(
			Long.valueOf(commerceDiscount.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceDiscount, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			commerceDiscount.getCouponCode(),
			ReflectionTestUtil.invoke(
				commerceDiscount, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "couponCode"));
		Assert.assertEquals(
			Boolean.valueOf(commerceDiscount.getActive()),
			ReflectionTestUtil.<Boolean>invoke(
				commerceDiscount, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "active_"));

		Assert.assertEquals(
			commerceDiscount.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceDiscount, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceDiscount.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceDiscount, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceDiscount addCommerceDiscount() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscount commerceDiscount = _persistence.create(pk);

		commerceDiscount.setMvccVersion(RandomTestUtil.nextLong());

		commerceDiscount.setUuid(RandomTestUtil.randomString());

		commerceDiscount.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceDiscount.setCompanyId(RandomTestUtil.nextLong());

		commerceDiscount.setUserId(RandomTestUtil.nextLong());

		commerceDiscount.setUserName(RandomTestUtil.randomString());

		commerceDiscount.setCreateDate(RandomTestUtil.nextDate());

		commerceDiscount.setModifiedDate(RandomTestUtil.nextDate());

		commerceDiscount.setTitle(RandomTestUtil.randomString());

		commerceDiscount.setTarget(RandomTestUtil.randomString());

		commerceDiscount.setUseCouponCode(RandomTestUtil.randomBoolean());

		commerceDiscount.setCouponCode(RandomTestUtil.randomString());

		commerceDiscount.setUsePercentage(RandomTestUtil.randomBoolean());

		commerceDiscount.setMaximumDiscountAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceDiscount.setLevel(RandomTestUtil.randomString());

		commerceDiscount.setLevel1(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceDiscount.setLevel2(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceDiscount.setLevel3(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceDiscount.setLevel4(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceDiscount.setLimitationType(RandomTestUtil.randomString());

		commerceDiscount.setLimitationTimes(RandomTestUtil.nextInt());

		commerceDiscount.setLimitationTimesPerAccount(RandomTestUtil.nextInt());

		commerceDiscount.setNumberOfUse(RandomTestUtil.nextInt());

		commerceDiscount.setRulesConjunction(RandomTestUtil.randomBoolean());

		commerceDiscount.setActive(RandomTestUtil.randomBoolean());

		commerceDiscount.setDisplayDate(RandomTestUtil.nextDate());

		commerceDiscount.setExpirationDate(RandomTestUtil.nextDate());

		commerceDiscount.setLastPublishDate(RandomTestUtil.nextDate());

		commerceDiscount.setStatus(RandomTestUtil.nextInt());

		commerceDiscount.setStatusByUserId(RandomTestUtil.nextLong());

		commerceDiscount.setStatusByUserName(RandomTestUtil.randomString());

		commerceDiscount.setStatusDate(RandomTestUtil.nextDate());

		_commerceDiscounts.add(_persistence.update(commerceDiscount));

		return commerceDiscount;
	}

	private List<CommerceDiscount> _commerceDiscounts =
		new ArrayList<CommerceDiscount>();
	private CommerceDiscountPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}