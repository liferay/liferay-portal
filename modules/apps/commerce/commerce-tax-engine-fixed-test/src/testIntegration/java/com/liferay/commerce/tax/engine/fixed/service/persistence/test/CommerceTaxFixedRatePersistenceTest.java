/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateException;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRate;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRatePersistence;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRateUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CommerceTaxFixedRatePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.tax.engine.fixed.service"));

	@Before
	public void setUp() {
		_persistence = CommerceTaxFixedRateUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTaxFixedRate> iterator =
			_commerceTaxFixedRates.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRate commerceTaxFixedRate = _persistence.create(pk);

		Assert.assertNotNull(commerceTaxFixedRate);

		Assert.assertEquals(commerceTaxFixedRate.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTaxFixedRate newCommerceTaxFixedRate =
			addCommerceTaxFixedRate();

		_persistence.remove(newCommerceTaxFixedRate);

		CommerceTaxFixedRate existingCommerceTaxFixedRate =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxFixedRate.getPrimaryKey());

		Assert.assertNull(existingCommerceTaxFixedRate);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTaxFixedRate();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRate newCommerceTaxFixedRate = _persistence.create(pk);

		newCommerceTaxFixedRate.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceTaxFixedRate.setGroupId(RandomTestUtil.nextLong());

		newCommerceTaxFixedRate.setCompanyId(RandomTestUtil.nextLong());

		newCommerceTaxFixedRate.setUserId(RandomTestUtil.nextLong());

		newCommerceTaxFixedRate.setUserName(RandomTestUtil.randomString());

		newCommerceTaxFixedRate.setCreateDate(RandomTestUtil.nextDate());

		newCommerceTaxFixedRate.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceTaxFixedRate.setCPTaxCategoryId(RandomTestUtil.nextLong());

		newCommerceTaxFixedRate.setCommerceTaxMethodId(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRate.setRate(RandomTestUtil.nextDouble());

		_commerceTaxFixedRates.add(
			_persistence.update(newCommerceTaxFixedRate));

		CommerceTaxFixedRate existingCommerceTaxFixedRate =
			_persistence.findByPrimaryKey(
				newCommerceTaxFixedRate.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxFixedRate.getMvccVersion(),
			newCommerceTaxFixedRate.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getCommerceTaxFixedRateId(),
			newCommerceTaxFixedRate.getCommerceTaxFixedRateId());
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getGroupId(),
			newCommerceTaxFixedRate.getGroupId());
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getCompanyId(),
			newCommerceTaxFixedRate.getCompanyId());
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getUserId(),
			newCommerceTaxFixedRate.getUserId());
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getUserName(),
			newCommerceTaxFixedRate.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTaxFixedRate.getCreateDate()),
			Time.getShortTimestamp(newCommerceTaxFixedRate.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTaxFixedRate.getModifiedDate()),
			Time.getShortTimestamp(newCommerceTaxFixedRate.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getCPTaxCategoryId(),
			newCommerceTaxFixedRate.getCPTaxCategoryId());
		Assert.assertEquals(
			existingCommerceTaxFixedRate.getCommerceTaxMethodId(),
			newCommerceTaxFixedRate.getCommerceTaxMethodId());
		AssertUtils.assertEquals(
			existingCommerceTaxFixedRate.getRate(),
			newCommerceTaxFixedRate.getRate());
	}

	@Test
	public void testCountByCPTaxCategoryId() throws Exception {
		_persistence.countByCPTaxCategoryId(RandomTestUtil.nextLong());

		_persistence.countByCPTaxCategoryId(0L);
	}

	@Test
	public void testCountByCommerceTaxMethodId() throws Exception {
		_persistence.countByCommerceTaxMethodId(RandomTestUtil.nextLong());

		_persistence.countByCommerceTaxMethodId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceTaxFixedRate newCommerceTaxFixedRate =
			addCommerceTaxFixedRate();

		CommerceTaxFixedRate existingCommerceTaxFixedRate =
			_persistence.findByPrimaryKey(
				newCommerceTaxFixedRate.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxFixedRate, newCommerceTaxFixedRate);
	}

	@Test(expected = NoSuchTaxFixedRateException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTaxFixedRate> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceTaxFixedRate", "mvccVersion", true,
			"commerceTaxFixedRateId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "CPTaxCategoryId", true,
			"commerceTaxMethodId", true, "rate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTaxFixedRate newCommerceTaxFixedRate =
			addCommerceTaxFixedRate();

		CommerceTaxFixedRate existingCommerceTaxFixedRate =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxFixedRate.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxFixedRate, newCommerceTaxFixedRate);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRate missingCommerceTaxFixedRate =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTaxFixedRate);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTaxFixedRate newCommerceTaxFixedRate1 =
			addCommerceTaxFixedRate();
		CommerceTaxFixedRate newCommerceTaxFixedRate2 =
			addCommerceTaxFixedRate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxFixedRate1.getPrimaryKey());
		primaryKeys.add(newCommerceTaxFixedRate2.getPrimaryKey());

		Map<Serializable, CommerceTaxFixedRate> commerceTaxFixedRates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceTaxFixedRates.size());
		Assert.assertEquals(
			newCommerceTaxFixedRate1,
			commerceTaxFixedRates.get(
				newCommerceTaxFixedRate1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTaxFixedRate2,
			commerceTaxFixedRates.get(
				newCommerceTaxFixedRate2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTaxFixedRate> commerceTaxFixedRates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTaxFixedRates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTaxFixedRate newCommerceTaxFixedRate =
			addCommerceTaxFixedRate();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxFixedRate.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTaxFixedRate> commerceTaxFixedRates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTaxFixedRates.size());
		Assert.assertEquals(
			newCommerceTaxFixedRate,
			commerceTaxFixedRates.get(newCommerceTaxFixedRate.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTaxFixedRate> commerceTaxFixedRates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTaxFixedRates.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTaxFixedRate newCommerceTaxFixedRate =
			addCommerceTaxFixedRate();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxFixedRate.getPrimaryKey());

		Map<Serializable, CommerceTaxFixedRate> commerceTaxFixedRates =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTaxFixedRates.size());
		Assert.assertEquals(
			newCommerceTaxFixedRate,
			commerceTaxFixedRates.get(newCommerceTaxFixedRate.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceTaxFixedRate newCommerceTaxFixedRate =
			addCommerceTaxFixedRate();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceTaxFixedRate.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceTaxFixedRate commerceTaxFixedRate) {

		Assert.assertEquals(
			Long.valueOf(commerceTaxFixedRate.getCPTaxCategoryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxFixedRate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPTaxCategoryId"));
		Assert.assertEquals(
			Long.valueOf(commerceTaxFixedRate.getCommerceTaxMethodId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxFixedRate, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceTaxMethodId"));
	}

	protected CommerceTaxFixedRate addCommerceTaxFixedRate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRate commerceTaxFixedRate = _persistence.create(pk);

		commerceTaxFixedRate.setMvccVersion(RandomTestUtil.nextLong());

		commerceTaxFixedRate.setGroupId(RandomTestUtil.nextLong());

		commerceTaxFixedRate.setCompanyId(RandomTestUtil.nextLong());

		commerceTaxFixedRate.setUserId(RandomTestUtil.nextLong());

		commerceTaxFixedRate.setUserName(RandomTestUtil.randomString());

		commerceTaxFixedRate.setCreateDate(RandomTestUtil.nextDate());

		commerceTaxFixedRate.setModifiedDate(RandomTestUtil.nextDate());

		commerceTaxFixedRate.setCPTaxCategoryId(RandomTestUtil.nextLong());

		commerceTaxFixedRate.setCommerceTaxMethodId(RandomTestUtil.nextLong());

		commerceTaxFixedRate.setRate(RandomTestUtil.nextDouble());

		_commerceTaxFixedRates.add(_persistence.update(commerceTaxFixedRate));

		return commerceTaxFixedRate;
	}

	private List<CommerceTaxFixedRate> _commerceTaxFixedRates =
		new ArrayList<CommerceTaxFixedRate>();
	private CommerceTaxFixedRatePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}