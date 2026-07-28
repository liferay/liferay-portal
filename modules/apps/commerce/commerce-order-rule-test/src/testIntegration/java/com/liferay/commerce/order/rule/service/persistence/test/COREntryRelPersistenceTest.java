/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.order.rule.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.order.rule.exception.NoSuchCOREntryRelException;
import com.liferay.commerce.order.rule.model.COREntryRel;
import com.liferay.commerce.order.rule.service.COREntryRelLocalServiceUtil;
import com.liferay.commerce.order.rule.service.persistence.COREntryRelPersistence;
import com.liferay.commerce.order.rule.service.persistence.COREntryRelUtil;
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
public class COREntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.order.rule.service"));

	@Before
	public void setUp() {
		_persistence = COREntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<COREntryRel> iterator = _corEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntryRel corEntryRel = _persistence.create(pk);

		Assert.assertNotNull(corEntryRel);

		Assert.assertEquals(corEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		_persistence.remove(newCOREntryRel);

		COREntryRel existingCOREntryRel = _persistence.fetchByPrimaryKey(
			newCOREntryRel.getPrimaryKey());

		Assert.assertNull(existingCOREntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCOREntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		newCOREntryRel.setCompanyId(RandomTestUtil.nextLong());

		newCOREntryRel.setUserId(RandomTestUtil.nextLong());

		newCOREntryRel.setUserName(RandomTestUtil.randomString());

		newCOREntryRel.setCreateDate(RandomTestUtil.nextDate());

		newCOREntryRel.setModifiedDate(RandomTestUtil.nextDate());

		newCOREntryRel.setClassNameId(RandomTestUtil.nextLong());

		newCOREntryRel.setClassPK(RandomTestUtil.nextLong());

		newCOREntryRel.setCOREntryId(RandomTestUtil.nextLong());

		newCOREntryRel = _persistence.update(newCOREntryRel);

		_corEntryRels.add(newCOREntryRel);

		COREntryRel existingCOREntryRel = _persistence.findByPrimaryKey(
			newCOREntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCOREntryRel.getMvccVersion(),
			newCOREntryRel.getMvccVersion());
		Assert.assertEquals(
			existingCOREntryRel.getCOREntryRelId(),
			newCOREntryRel.getCOREntryRelId());
		Assert.assertEquals(
			existingCOREntryRel.getCompanyId(), newCOREntryRel.getCompanyId());
		Assert.assertEquals(
			existingCOREntryRel.getUserId(), newCOREntryRel.getUserId());
		Assert.assertEquals(
			existingCOREntryRel.getUserName(), newCOREntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntryRel.getCreateDate()),
			Time.getShortTimestamp(newCOREntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCOREntryRel.getModifiedDate()),
			Time.getShortTimestamp(newCOREntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingCOREntryRel.getClassNameId(),
			newCOREntryRel.getClassNameId());
		Assert.assertEquals(
			existingCOREntryRel.getClassPK(), newCOREntryRel.getClassPK());
		Assert.assertEquals(
			existingCOREntryRel.getCOREntryId(),
			newCOREntryRel.getCOREntryId());
	}

	@Test
	public void testCountByCOREntryId() throws Exception {
		_persistence.countByCOREntryId(RandomTestUtil.nextLong());

		_persistence.countByCOREntryId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		COREntryRel existingCOREntryRel = _persistence.findByPrimaryKey(
			newCOREntryRel.getPrimaryKey());

		Assert.assertEquals(existingCOREntryRel, newCOREntryRel);
	}

	@Test(expected = NoSuchCOREntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<COREntryRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"COREntryRel", "mvccVersion", true, "COREntryRelId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "classPK", true,
			"COREntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		COREntryRel existingCOREntryRel = _persistence.fetchByPrimaryKey(
			newCOREntryRel.getPrimaryKey());

		Assert.assertEquals(existingCOREntryRel, newCOREntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntryRel missingCOREntryRel = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCOREntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		COREntryRel newCOREntryRel1 = addCOREntryRel();
		COREntryRel newCOREntryRel2 = addCOREntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCOREntryRel1.getPrimaryKey());
		primaryKeys.add(newCOREntryRel2.getPrimaryKey());

		Map<Serializable, COREntryRel> corEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, corEntryRels.size());
		Assert.assertEquals(
			newCOREntryRel1, corEntryRels.get(newCOREntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCOREntryRel2, corEntryRels.get(newCOREntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, COREntryRel> corEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(corEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		COREntryRel newCOREntryRel = addCOREntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCOREntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, COREntryRel> corEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, corEntryRels.size());
		Assert.assertEquals(
			newCOREntryRel, corEntryRels.get(newCOREntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, COREntryRel> corEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(corEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCOREntryRel.getPrimaryKey());

		Map<Serializable, COREntryRel> corEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, corEntryRels.size());
		Assert.assertEquals(
			newCOREntryRel, corEntryRels.get(newCOREntryRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			COREntryRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<COREntryRel>() {

				@Override
				public void performAction(COREntryRel corEntryRel) {
					Assert.assertNotNull(corEntryRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			COREntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"COREntryRelId", newCOREntryRel.getCOREntryRelId()));

		List<COREntryRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		COREntryRel existingCOREntryRel = result.get(0);

		Assert.assertEquals(existingCOREntryRel, newCOREntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			COREntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"COREntryRelId", RandomTestUtil.nextLong()));

		List<COREntryRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			COREntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("COREntryRelId"));

		Object newCOREntryRelId = newCOREntryRel.getCOREntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"COREntryRelId", new Object[] {newCOREntryRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCOREntryRelId = result.get(0);

		Assert.assertEquals(existingCOREntryRelId, newCOREntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			COREntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("COREntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"COREntryRelId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		COREntryRel newCOREntryRel = addCOREntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCOREntryRel.getPrimaryKey()));
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

		COREntryRel newCOREntryRel = addCOREntryRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			COREntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"COREntryRelId", newCOREntryRel.getCOREntryRelId()));

		List<COREntryRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(COREntryRel corEntryRel) {
		Assert.assertEquals(
			Long.valueOf(corEntryRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				corEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(corEntryRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				corEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(corEntryRel.getCOREntryId()),
			ReflectionTestUtil.<Long>invoke(
				corEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "COREntryId"));
	}

	protected COREntryRel addCOREntryRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		COREntryRel corEntryRel = _persistence.create(pk);

		corEntryRel.setCompanyId(RandomTestUtil.nextLong());

		corEntryRel.setUserId(RandomTestUtil.nextLong());

		corEntryRel.setUserName(RandomTestUtil.randomString());

		corEntryRel.setCreateDate(RandomTestUtil.nextDate());

		corEntryRel.setModifiedDate(RandomTestUtil.nextDate());

		corEntryRel.setClassNameId(RandomTestUtil.nextLong());

		corEntryRel.setClassPK(RandomTestUtil.nextLong());

		corEntryRel.setCOREntryId(RandomTestUtil.nextLong());

		_corEntryRels.add(_persistence.update(corEntryRel));

		return corEntryRel;
	}

	private List<COREntryRel> _corEntryRels = new ArrayList<COREntryRel>();
	private COREntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1802783169