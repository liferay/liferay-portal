/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.exception.NoSuchPriceModifierRelException;
import com.liferay.commerce.pricing.model.CommercePriceModifierRel;
import com.liferay.commerce.pricing.service.CommercePriceModifierRelLocalServiceUtil;
import com.liferay.commerce.pricing.service.persistence.CommercePriceModifierRelPersistence;
import com.liferay.commerce.pricing.service.persistence.CommercePriceModifierRelUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
public class CommercePriceModifierRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.pricing.service"));

	@Before
	public void setUp() {
		_persistence = CommercePriceModifierRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePriceModifierRel> iterator =
			_commercePriceModifierRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceModifierRel commercePriceModifierRel = _persistence.create(
			pk);

		Assert.assertNotNull(commercePriceModifierRel);

		Assert.assertEquals(commercePriceModifierRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		_persistence.remove(newCommercePriceModifierRel);

		CommercePriceModifierRel existingCommercePriceModifierRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceModifierRel.getPrimaryKey());

		Assert.assertNull(existingCommercePriceModifierRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePriceModifierRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		newCommercePriceModifierRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommercePriceModifierRel.setCompanyId(RandomTestUtil.nextLong());

		newCommercePriceModifierRel.setUserId(RandomTestUtil.nextLong());

		newCommercePriceModifierRel.setUserName(RandomTestUtil.randomString());

		newCommercePriceModifierRel.setCreateDate(RandomTestUtil.nextDate());

		newCommercePriceModifierRel.setModifiedDate(RandomTestUtil.nextDate());

		newCommercePriceModifierRel.setCommercePriceModifierId(
			RandomTestUtil.nextLong());

		newCommercePriceModifierRel.setClassNameId(RandomTestUtil.nextLong());

		newCommercePriceModifierRel.setClassPK(RandomTestUtil.nextLong());

		newCommercePriceModifierRel = _persistence.update(
			newCommercePriceModifierRel);

		_commercePriceModifierRels.add(newCommercePriceModifierRel);

		CommercePriceModifierRel existingCommercePriceModifierRel =
			_persistence.findByPrimaryKey(
				newCommercePriceModifierRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceModifierRel.getMvccVersion(),
			newCommercePriceModifierRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getCtCollectionId(),
			newCommercePriceModifierRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getCommercePriceModifierRelId(),
			newCommercePriceModifierRel.getCommercePriceModifierRelId());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getCompanyId(),
			newCommercePriceModifierRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getUserId(),
			newCommercePriceModifierRel.getUserId());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getUserName(),
			newCommercePriceModifierRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceModifierRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePriceModifierRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceModifierRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePriceModifierRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePriceModifierRel.getCommercePriceModifierId(),
			newCommercePriceModifierRel.getCommercePriceModifierId());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getClassNameId(),
			newCommercePriceModifierRel.getClassNameId());
		Assert.assertEquals(
			existingCommercePriceModifierRel.getClassPK(),
			newCommercePriceModifierRel.getClassPK());
	}

	@Test
	public void testCountByCommercePriceModifierId() throws Exception {
		_persistence.countByCommercePriceModifierId(RandomTestUtil.nextLong());

		_persistence.countByCommercePriceModifierId(0L);
	}

	@Test
	public void testCountByCPM_CN() throws Exception {
		_persistence.countByCPM_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCPM_CN(0L, 0L);
	}

	@Test
	public void testCountByCN_CPK() throws Exception {
		_persistence.countByCN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCN_CPK(0L, 0L);
	}

	@Test
	public void testCountByCPM_CN_CPK() throws Exception {
		_persistence.countByCPM_CN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByCPM_CN_CPK(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		CommercePriceModifierRel existingCommercePriceModifierRel =
			_persistence.findByPrimaryKey(
				newCommercePriceModifierRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceModifierRel, newCommercePriceModifierRel);
	}

	@Test(expected = NoSuchPriceModifierRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePriceModifierRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommercePriceModifierRel", "mvccVersion", true, "ctCollectionId",
			true, "commercePriceModifierRelId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "commercePriceModifierId", true,
			"classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		CommercePriceModifierRel existingCommercePriceModifierRel =
			_persistence.fetchByPrimaryKey(
				newCommercePriceModifierRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceModifierRel, newCommercePriceModifierRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceModifierRel missingCommercePriceModifierRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePriceModifierRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePriceModifierRel newCommercePriceModifierRel1 =
			addCommercePriceModifierRel();
		CommercePriceModifierRel newCommercePriceModifierRel2 =
			addCommercePriceModifierRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceModifierRel1.getPrimaryKey());
		primaryKeys.add(newCommercePriceModifierRel2.getPrimaryKey());

		Map<Serializable, CommercePriceModifierRel> commercePriceModifierRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commercePriceModifierRels.size());
		Assert.assertEquals(
			newCommercePriceModifierRel1,
			commercePriceModifierRels.get(
				newCommercePriceModifierRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePriceModifierRel2,
			commercePriceModifierRels.get(
				newCommercePriceModifierRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePriceModifierRel> commercePriceModifierRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePriceModifierRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceModifierRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePriceModifierRel> commercePriceModifierRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePriceModifierRels.size());
		Assert.assertEquals(
			newCommercePriceModifierRel,
			commercePriceModifierRels.get(
				newCommercePriceModifierRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePriceModifierRel> commercePriceModifierRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePriceModifierRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceModifierRel.getPrimaryKey());

		Map<Serializable, CommercePriceModifierRel> commercePriceModifierRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePriceModifierRels.size());
		Assert.assertEquals(
			newCommercePriceModifierRel,
			commercePriceModifierRels.get(
				newCommercePriceModifierRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommercePriceModifierRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommercePriceModifierRel>() {

				@Override
				public void performAction(
					CommercePriceModifierRel commercePriceModifierRel) {

					Assert.assertNotNull(commercePriceModifierRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceModifierRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceModifierRelId",
				newCommercePriceModifierRel.getCommercePriceModifierRelId()));

		List<CommercePriceModifierRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommercePriceModifierRel existingCommercePriceModifierRel = result.get(
			0);

		Assert.assertEquals(
			existingCommercePriceModifierRel, newCommercePriceModifierRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceModifierRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceModifierRelId", RandomTestUtil.nextLong()));

		List<CommercePriceModifierRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceModifierRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commercePriceModifierRelId"));

		Object newCommercePriceModifierRelId =
			newCommercePriceModifierRel.getCommercePriceModifierRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commercePriceModifierRelId",
				new Object[] {newCommercePriceModifierRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommercePriceModifierRelId = result.get(0);

		Assert.assertEquals(
			existingCommercePriceModifierRelId, newCommercePriceModifierRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceModifierRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commercePriceModifierRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commercePriceModifierRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePriceModifierRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		CommercePriceModifierRel newCommercePriceModifierRel =
			addCommercePriceModifierRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceModifierRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceModifierRelId",
				newCommercePriceModifierRel.getCommercePriceModifierRelId()));

		List<CommercePriceModifierRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommercePriceModifierRel commercePriceModifierRel) {

		Assert.assertEquals(
			Long.valueOf(commercePriceModifierRel.getCommercePriceModifierId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceModifierRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commercePriceModifierId"));
		Assert.assertEquals(
			Long.valueOf(commercePriceModifierRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceModifierRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commercePriceModifierRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceModifierRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected CommercePriceModifierRel addCommercePriceModifierRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePriceModifierRel commercePriceModifierRel = _persistence.create(
			pk);

		commercePriceModifierRel.setCtCollectionId(RandomTestUtil.nextLong());

		commercePriceModifierRel.setCompanyId(RandomTestUtil.nextLong());

		commercePriceModifierRel.setUserId(RandomTestUtil.nextLong());

		commercePriceModifierRel.setUserName(RandomTestUtil.randomString());

		commercePriceModifierRel.setCreateDate(RandomTestUtil.nextDate());

		commercePriceModifierRel.setModifiedDate(RandomTestUtil.nextDate());

		commercePriceModifierRel.setCommercePriceModifierId(
			RandomTestUtil.nextLong());

		commercePriceModifierRel.setClassNameId(RandomTestUtil.nextLong());

		commercePriceModifierRel.setClassPK(RandomTestUtil.nextLong());

		_commercePriceModifierRels.add(
			_persistence.update(commercePriceModifierRel));

		return commercePriceModifierRel;
	}

	private List<CommercePriceModifierRel> _commercePriceModifierRels =
		new ArrayList<CommercePriceModifierRel>();
	private CommercePriceModifierRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1944374381