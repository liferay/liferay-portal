/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionRelException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionRel;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionRelPersistence;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CommerceShippingFixedOptionRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.shipping.engine.fixed.service"));

	@Before
	public void setUp() {
		_persistence = CommerceShippingFixedOptionRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceShippingFixedOptionRel> iterator =
			_commerceShippingFixedOptionRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			_persistence.create(pk);

		Assert.assertNotNull(commerceShippingFixedOptionRel);

		Assert.assertEquals(commerceShippingFixedOptionRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel =
			addCommerceShippingFixedOptionRel();

		_persistence.remove(newCommerceShippingFixedOptionRel);

		CommerceShippingFixedOptionRel existingCommerceShippingFixedOptionRel =
			_persistence.fetchByPrimaryKey(
				newCommerceShippingFixedOptionRel.getPrimaryKey());

		Assert.assertNull(existingCommerceShippingFixedOptionRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceShippingFixedOptionRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel =
			_persistence.create(pk);

		newCommerceShippingFixedOptionRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setGroupId(RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setUserId(RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setUserName(
			RandomTestUtil.randomString());

		newCommerceShippingFixedOptionRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceShippingFixedOptionRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceShippingFixedOptionRel.setCommerceShippingMethodId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setCommerceShippingFixedOptionId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setCountryId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setRegionId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionRel.setZip(RandomTestUtil.randomString());

		newCommerceShippingFixedOptionRel.setWeightFrom(
			RandomTestUtil.nextDouble());

		newCommerceShippingFixedOptionRel.setWeightTo(
			RandomTestUtil.nextDouble());

		newCommerceShippingFixedOptionRel.setFixedPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceShippingFixedOptionRel.setRateUnitWeightPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceShippingFixedOptionRel.setRatePercentage(
			RandomTestUtil.nextDouble());

		_commerceShippingFixedOptionRels.add(
			_persistence.update(newCommerceShippingFixedOptionRel));

		CommerceShippingFixedOptionRel existingCommerceShippingFixedOptionRel =
			_persistence.findByPrimaryKey(
				newCommerceShippingFixedOptionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getMvccVersion(),
			newCommerceShippingFixedOptionRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.
				getCommerceShippingFixedOptionRelId(),
			newCommerceShippingFixedOptionRel.
				getCommerceShippingFixedOptionRelId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getGroupId(),
			newCommerceShippingFixedOptionRel.getGroupId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getCompanyId(),
			newCommerceShippingFixedOptionRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getUserId(),
			newCommerceShippingFixedOptionRel.getUserId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getUserName(),
			newCommerceShippingFixedOptionRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingFixedOptionRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceShippingFixedOptionRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingFixedOptionRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceShippingFixedOptionRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.
				getCommerceShippingMethodId(),
			newCommerceShippingFixedOptionRel.getCommerceShippingMethodId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.
				getCommerceShippingFixedOptionId(),
			newCommerceShippingFixedOptionRel.
				getCommerceShippingFixedOptionId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.
				getCommerceInventoryWarehouseId(),
			newCommerceShippingFixedOptionRel.
				getCommerceInventoryWarehouseId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getCountryId(),
			newCommerceShippingFixedOptionRel.getCountryId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getRegionId(),
			newCommerceShippingFixedOptionRel.getRegionId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getZip(),
			newCommerceShippingFixedOptionRel.getZip());
		AssertUtils.assertEquals(
			existingCommerceShippingFixedOptionRel.getWeightFrom(),
			newCommerceShippingFixedOptionRel.getWeightFrom());
		AssertUtils.assertEquals(
			existingCommerceShippingFixedOptionRel.getWeightTo(),
			newCommerceShippingFixedOptionRel.getWeightTo());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getFixedPrice(),
			newCommerceShippingFixedOptionRel.getFixedPrice());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel.getRateUnitWeightPrice(),
			newCommerceShippingFixedOptionRel.getRateUnitWeightPrice());
		AssertUtils.assertEquals(
			existingCommerceShippingFixedOptionRel.getRatePercentage(),
			newCommerceShippingFixedOptionRel.getRatePercentage());
	}

	@Test
	public void testCountByCommerceShippingMethodId() throws Exception {
		_persistence.countByCommerceShippingMethodId(RandomTestUtil.nextLong());

		_persistence.countByCommerceShippingMethodId(0L);
	}

	@Test
	public void testCountByCommerceShippingFixedOptionId() throws Exception {
		_persistence.countByCommerceShippingFixedOptionId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceShippingFixedOptionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel =
			addCommerceShippingFixedOptionRel();

		CommerceShippingFixedOptionRel existingCommerceShippingFixedOptionRel =
			_persistence.findByPrimaryKey(
				newCommerceShippingFixedOptionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel,
			newCommerceShippingFixedOptionRel);
	}

	@Test(expected = NoSuchShippingFixedOptionRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceShippingFixedOptionRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CShippingFixedOptionRel", "mvccVersion", true,
			"commerceShippingFixedOptionRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "commerceShippingMethodId", true,
			"commerceShippingFixedOptionId", true,
			"commerceInventoryWarehouseId", true, "countryId", true, "regionId",
			true, "zip", true, "weightFrom", true, "weightTo", true,
			"fixedPrice", true, "rateUnitWeightPrice", true, "ratePercentage",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel =
			addCommerceShippingFixedOptionRel();

		CommerceShippingFixedOptionRel existingCommerceShippingFixedOptionRel =
			_persistence.fetchByPrimaryKey(
				newCommerceShippingFixedOptionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOptionRel,
			newCommerceShippingFixedOptionRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionRel missingCommerceShippingFixedOptionRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceShippingFixedOptionRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel1 =
			addCommerceShippingFixedOptionRel();
		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel2 =
			addCommerceShippingFixedOptionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingFixedOptionRel1.getPrimaryKey());
		primaryKeys.add(newCommerceShippingFixedOptionRel2.getPrimaryKey());

		Map<Serializable, CommerceShippingFixedOptionRel>
			commerceShippingFixedOptionRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceShippingFixedOptionRels.size());
		Assert.assertEquals(
			newCommerceShippingFixedOptionRel1,
			commerceShippingFixedOptionRels.get(
				newCommerceShippingFixedOptionRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceShippingFixedOptionRel2,
			commerceShippingFixedOptionRels.get(
				newCommerceShippingFixedOptionRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceShippingFixedOptionRel>
			commerceShippingFixedOptionRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceShippingFixedOptionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel =
			addCommerceShippingFixedOptionRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingFixedOptionRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceShippingFixedOptionRel>
			commerceShippingFixedOptionRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceShippingFixedOptionRels.size());
		Assert.assertEquals(
			newCommerceShippingFixedOptionRel,
			commerceShippingFixedOptionRels.get(
				newCommerceShippingFixedOptionRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceShippingFixedOptionRel>
			commerceShippingFixedOptionRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceShippingFixedOptionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceShippingFixedOptionRel newCommerceShippingFixedOptionRel =
			addCommerceShippingFixedOptionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingFixedOptionRel.getPrimaryKey());

		Map<Serializable, CommerceShippingFixedOptionRel>
			commerceShippingFixedOptionRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceShippingFixedOptionRels.size());
		Assert.assertEquals(
			newCommerceShippingFixedOptionRel,
			commerceShippingFixedOptionRels.get(
				newCommerceShippingFixedOptionRel.getPrimaryKey()));
	}

	protected CommerceShippingFixedOptionRel addCommerceShippingFixedOptionRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionRel commerceShippingFixedOptionRel =
			_persistence.create(pk);

		commerceShippingFixedOptionRel.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setGroupId(RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setCompanyId(RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setUserId(RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setUserName(
			RandomTestUtil.randomString());

		commerceShippingFixedOptionRel.setCreateDate(RandomTestUtil.nextDate());

		commerceShippingFixedOptionRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceShippingFixedOptionRel.setCommerceShippingMethodId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setCommerceShippingFixedOptionId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setCommerceInventoryWarehouseId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setCountryId(RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setRegionId(RandomTestUtil.nextLong());

		commerceShippingFixedOptionRel.setZip(RandomTestUtil.randomString());

		commerceShippingFixedOptionRel.setWeightFrom(
			RandomTestUtil.nextDouble());

		commerceShippingFixedOptionRel.setWeightTo(RandomTestUtil.nextDouble());

		commerceShippingFixedOptionRel.setFixedPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceShippingFixedOptionRel.setRateUnitWeightPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceShippingFixedOptionRel.setRatePercentage(
			RandomTestUtil.nextDouble());

		_commerceShippingFixedOptionRels.add(
			_persistence.update(commerceShippingFixedOptionRel));

		return commerceShippingFixedOptionRel;
	}

	private List<CommerceShippingFixedOptionRel>
		_commerceShippingFixedOptionRels =
			new ArrayList<CommerceShippingFixedOptionRel>();
	private CommerceShippingFixedOptionRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}