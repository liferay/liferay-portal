/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.exception.NoSuchDiscountRelException;
import com.liferay.commerce.discount.model.CommerceDiscountRel;
import com.liferay.commerce.discount.service.CommerceDiscountRelLocalServiceUtil;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRelPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRelUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class CommerceDiscountRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.discount.service"));

	@Before
	public void setUp() {
		_persistence = CommerceDiscountRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceDiscountRel> iterator =
			_commerceDiscountRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRel commerceDiscountRel = _persistence.create(pk);

		Assert.assertNotNull(commerceDiscountRel);

		Assert.assertEquals(commerceDiscountRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		_persistence.remove(newCommerceDiscountRel);

		CommerceDiscountRel existingCommerceDiscountRel =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountRel.getPrimaryKey());

		Assert.assertNull(existingCommerceDiscountRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceDiscountRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRel newCommerceDiscountRel = _persistence.create(pk);

		newCommerceDiscountRel.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceDiscountRel.setCompanyId(RandomTestUtil.nextLong());

		newCommerceDiscountRel.setUserId(RandomTestUtil.nextLong());

		newCommerceDiscountRel.setUserName(RandomTestUtil.randomString());

		newCommerceDiscountRel.setCreateDate(RandomTestUtil.nextDate());

		newCommerceDiscountRel.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceDiscountRel.setCommerceDiscountId(RandomTestUtil.nextLong());

		newCommerceDiscountRel.setClassNameId(RandomTestUtil.nextLong());

		newCommerceDiscountRel.setClassPK(RandomTestUtil.nextLong());

		newCommerceDiscountRel.setTypeSettings(RandomTestUtil.randomString());

		_commerceDiscountRels.add(_persistence.update(newCommerceDiscountRel));

		CommerceDiscountRel existingCommerceDiscountRel =
			_persistence.findByPrimaryKey(
				newCommerceDiscountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountRel.getMvccVersion(),
			newCommerceDiscountRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceDiscountRel.getCommerceDiscountRelId(),
			newCommerceDiscountRel.getCommerceDiscountRelId());
		Assert.assertEquals(
			existingCommerceDiscountRel.getCompanyId(),
			newCommerceDiscountRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceDiscountRel.getUserId(),
			newCommerceDiscountRel.getUserId());
		Assert.assertEquals(
			existingCommerceDiscountRel.getUserName(),
			newCommerceDiscountRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceDiscountRel.getCreateDate()),
			Time.getShortTimestamp(newCommerceDiscountRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountRel.getModifiedDate()),
			Time.getShortTimestamp(newCommerceDiscountRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceDiscountRel.getCommerceDiscountId(),
			newCommerceDiscountRel.getCommerceDiscountId());
		Assert.assertEquals(
			existingCommerceDiscountRel.getClassNameId(),
			newCommerceDiscountRel.getClassNameId());
		Assert.assertEquals(
			existingCommerceDiscountRel.getClassPK(),
			newCommerceDiscountRel.getClassPK());
		Assert.assertEquals(
			existingCommerceDiscountRel.getTypeSettings(),
			newCommerceDiscountRel.getTypeSettings());
	}

	@Test
	public void testCountByCommerceDiscountId() throws Exception {
		_persistence.countByCommerceDiscountId(RandomTestUtil.nextLong());

		_persistence.countByCommerceDiscountId(0L);
	}

	@Test
	public void testCountByCD_CN() throws Exception {
		_persistence.countByCD_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCD_CN(0L, 0L);
	}

	@Test
	public void testCountByCN_CPK() throws Exception {
		_persistence.countByCN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCN_CPK(0L, 0L);
	}

	@Test
	public void testCountByCD_CN_CPK() throws Exception {
		_persistence.countByCD_CN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByCD_CN_CPK(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		CommerceDiscountRel existingCommerceDiscountRel =
			_persistence.findByPrimaryKey(
				newCommerceDiscountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountRel, newCommerceDiscountRel);
	}

	@Test(expected = NoSuchDiscountRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceDiscountRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceDiscountRel", "mvccVersion", true, "commerceDiscountRelId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "commerceDiscountId",
			true, "classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		CommerceDiscountRel existingCommerceDiscountRel =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountRel, newCommerceDiscountRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRel missingCommerceDiscountRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceDiscountRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceDiscountRel newCommerceDiscountRel1 = addCommerceDiscountRel();
		CommerceDiscountRel newCommerceDiscountRel2 = addCommerceDiscountRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountRel1.getPrimaryKey());
		primaryKeys.add(newCommerceDiscountRel2.getPrimaryKey());

		Map<Serializable, CommerceDiscountRel> commerceDiscountRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceDiscountRels.size());
		Assert.assertEquals(
			newCommerceDiscountRel1,
			commerceDiscountRels.get(newCommerceDiscountRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceDiscountRel2,
			commerceDiscountRels.get(newCommerceDiscountRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceDiscountRel> commerceDiscountRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceDiscountRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceDiscountRel> commerceDiscountRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceDiscountRels.size());
		Assert.assertEquals(
			newCommerceDiscountRel,
			commerceDiscountRels.get(newCommerceDiscountRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceDiscountRel> commerceDiscountRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceDiscountRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountRel.getPrimaryKey());

		Map<Serializable, CommerceDiscountRel> commerceDiscountRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceDiscountRels.size());
		Assert.assertEquals(
			newCommerceDiscountRel,
			commerceDiscountRels.get(newCommerceDiscountRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceDiscountRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceDiscountRel>() {

				@Override
				public void performAction(
					CommerceDiscountRel commerceDiscountRel) {

					Assert.assertNotNull(commerceDiscountRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceDiscountRelId",
				newCommerceDiscountRel.getCommerceDiscountRelId()));

		List<CommerceDiscountRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceDiscountRel existingCommerceDiscountRel = result.get(0);

		Assert.assertEquals(
			existingCommerceDiscountRel, newCommerceDiscountRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceDiscountRelId", RandomTestUtil.nextLong()));

		List<CommerceDiscountRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceDiscountRel newCommerceDiscountRel = addCommerceDiscountRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceDiscountRelId"));

		Object newCommerceDiscountRelId =
			newCommerceDiscountRel.getCommerceDiscountRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceDiscountRelId",
				new Object[] {newCommerceDiscountRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceDiscountRelId = result.get(0);

		Assert.assertEquals(
			existingCommerceDiscountRelId, newCommerceDiscountRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceDiscountRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceDiscountRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CommerceDiscountRel addCommerceDiscountRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRel commerceDiscountRel = _persistence.create(pk);

		commerceDiscountRel.setMvccVersion(RandomTestUtil.nextLong());

		commerceDiscountRel.setCompanyId(RandomTestUtil.nextLong());

		commerceDiscountRel.setUserId(RandomTestUtil.nextLong());

		commerceDiscountRel.setUserName(RandomTestUtil.randomString());

		commerceDiscountRel.setCreateDate(RandomTestUtil.nextDate());

		commerceDiscountRel.setModifiedDate(RandomTestUtil.nextDate());

		commerceDiscountRel.setCommerceDiscountId(RandomTestUtil.nextLong());

		commerceDiscountRel.setClassNameId(RandomTestUtil.nextLong());

		commerceDiscountRel.setClassPK(RandomTestUtil.nextLong());

		commerceDiscountRel.setTypeSettings(RandomTestUtil.randomString());

		_commerceDiscountRels.add(_persistence.update(commerceDiscountRel));

		return commerceDiscountRel;
	}

	private List<CommerceDiscountRel> _commerceDiscountRels =
		new ArrayList<CommerceDiscountRel>();
	private CommerceDiscountRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}