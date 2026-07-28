/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationListRelException;
import com.liferay.commerce.product.model.CPConfigurationListRel;
import com.liferay.commerce.product.service.CPConfigurationListRelLocalServiceUtil;
import com.liferay.commerce.product.service.persistence.CPConfigurationListRelPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationListRelUtil;
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
public class CPConfigurationListRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPConfigurationListRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPConfigurationListRel> iterator =
			_cpConfigurationListRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationListRel cpConfigurationListRel = _persistence.create(pk);

		Assert.assertNotNull(cpConfigurationListRel);

		Assert.assertEquals(cpConfigurationListRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		_persistence.remove(newCPConfigurationListRel);

		CPConfigurationListRel existingCPConfigurationListRel =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationListRel.getPrimaryKey());

		Assert.assertNull(existingCPConfigurationListRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPConfigurationListRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		newCPConfigurationListRel.setCtCollectionId(RandomTestUtil.nextLong());

		newCPConfigurationListRel.setCompanyId(RandomTestUtil.nextLong());

		newCPConfigurationListRel.setUserId(RandomTestUtil.nextLong());

		newCPConfigurationListRel.setUserName(RandomTestUtil.randomString());

		newCPConfigurationListRel.setCreateDate(RandomTestUtil.nextDate());

		newCPConfigurationListRel.setModifiedDate(RandomTestUtil.nextDate());

		newCPConfigurationListRel.setClassNameId(RandomTestUtil.nextLong());

		newCPConfigurationListRel.setClassPK(RandomTestUtil.nextLong());

		newCPConfigurationListRel.setCPConfigurationListId(
			RandomTestUtil.nextLong());

		newCPConfigurationListRel = _persistence.update(
			newCPConfigurationListRel);

		_cpConfigurationListRels.add(newCPConfigurationListRel);

		CPConfigurationListRel existingCPConfigurationListRel =
			_persistence.findByPrimaryKey(
				newCPConfigurationListRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationListRel.getMvccVersion(),
			newCPConfigurationListRel.getMvccVersion());
		Assert.assertEquals(
			existingCPConfigurationListRel.getCtCollectionId(),
			newCPConfigurationListRel.getCtCollectionId());
		Assert.assertEquals(
			existingCPConfigurationListRel.getCPConfigurationListRelId(),
			newCPConfigurationListRel.getCPConfigurationListRelId());
		Assert.assertEquals(
			existingCPConfigurationListRel.getCompanyId(),
			newCPConfigurationListRel.getCompanyId());
		Assert.assertEquals(
			existingCPConfigurationListRel.getUserId(),
			newCPConfigurationListRel.getUserId());
		Assert.assertEquals(
			existingCPConfigurationListRel.getUserName(),
			newCPConfigurationListRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationListRel.getCreateDate()),
			Time.getShortTimestamp(newCPConfigurationListRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationListRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCPConfigurationListRel.getModifiedDate()));
		Assert.assertEquals(
			existingCPConfigurationListRel.getClassNameId(),
			newCPConfigurationListRel.getClassNameId());
		Assert.assertEquals(
			existingCPConfigurationListRel.getClassPK(),
			newCPConfigurationListRel.getClassPK());
		Assert.assertEquals(
			existingCPConfigurationListRel.getCPConfigurationListId(),
			newCPConfigurationListRel.getCPConfigurationListId());
	}

	@Test
	public void testCountByCPConfigurationListId() throws Exception {
		_persistence.countByCPConfigurationListId(RandomTestUtil.nextLong());

		_persistence.countByCPConfigurationListId(0L);
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
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		CPConfigurationListRel existingCPConfigurationListRel =
			_persistence.findByPrimaryKey(
				newCPConfigurationListRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationListRel, newCPConfigurationListRel);
	}

	@Test(expected = NoSuchCPConfigurationListRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPConfigurationListRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPConfigurationListRel", "mvccVersion", true, "ctCollectionId",
			true, "CPConfigurationListRelId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "CPConfigurationListId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		CPConfigurationListRel existingCPConfigurationListRel =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationListRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationListRel, newCPConfigurationListRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationListRel missingCPConfigurationListRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPConfigurationListRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPConfigurationListRel newCPConfigurationListRel1 =
			addCPConfigurationListRel();
		CPConfigurationListRel newCPConfigurationListRel2 =
			addCPConfigurationListRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationListRel1.getPrimaryKey());
		primaryKeys.add(newCPConfigurationListRel2.getPrimaryKey());

		Map<Serializable, CPConfigurationListRel> cpConfigurationListRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpConfigurationListRels.size());
		Assert.assertEquals(
			newCPConfigurationListRel1,
			cpConfigurationListRels.get(
				newCPConfigurationListRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCPConfigurationListRel2,
			cpConfigurationListRels.get(
				newCPConfigurationListRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPConfigurationListRel> cpConfigurationListRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpConfigurationListRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationListRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPConfigurationListRel> cpConfigurationListRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpConfigurationListRels.size());
		Assert.assertEquals(
			newCPConfigurationListRel,
			cpConfigurationListRels.get(
				newCPConfigurationListRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPConfigurationListRel> cpConfigurationListRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpConfigurationListRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationListRel.getPrimaryKey());

		Map<Serializable, CPConfigurationListRel> cpConfigurationListRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpConfigurationListRels.size());
		Assert.assertEquals(
			newCPConfigurationListRel,
			cpConfigurationListRels.get(
				newCPConfigurationListRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CPConfigurationListRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CPConfigurationListRel>() {

				@Override
				public void performAction(
					CPConfigurationListRel cpConfigurationListRel) {

					Assert.assertNotNull(cpConfigurationListRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationListRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPConfigurationListRelId",
				newCPConfigurationListRel.getCPConfigurationListRelId()));

		List<CPConfigurationListRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CPConfigurationListRel existingCPConfigurationListRel = result.get(0);

		Assert.assertEquals(
			existingCPConfigurationListRel, newCPConfigurationListRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationListRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPConfigurationListRelId", RandomTestUtil.nextLong()));

		List<CPConfigurationListRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationListRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CPConfigurationListRelId"));

		Object newCPConfigurationListRelId =
			newCPConfigurationListRel.getCPConfigurationListRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CPConfigurationListRelId",
				new Object[] {newCPConfigurationListRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCPConfigurationListRelId = result.get(0);

		Assert.assertEquals(
			existingCPConfigurationListRelId, newCPConfigurationListRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationListRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CPConfigurationListRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CPConfigurationListRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPConfigurationListRel.getPrimaryKey()));
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

		CPConfigurationListRel newCPConfigurationListRel =
			addCPConfigurationListRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationListRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPConfigurationListRelId",
				newCPConfigurationListRel.getCPConfigurationListRelId()));

		List<CPConfigurationListRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CPConfigurationListRel cpConfigurationListRel) {

		Assert.assertEquals(
			Long.valueOf(cpConfigurationListRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationListRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationListRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationListRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationListRel.getCPConfigurationListId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationListRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPConfigurationListId"));
	}

	protected CPConfigurationListRel addCPConfigurationListRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPConfigurationListRel cpConfigurationListRel = _persistence.create(pk);

		cpConfigurationListRel.setCtCollectionId(RandomTestUtil.nextLong());

		cpConfigurationListRel.setCompanyId(RandomTestUtil.nextLong());

		cpConfigurationListRel.setUserId(RandomTestUtil.nextLong());

		cpConfigurationListRel.setUserName(RandomTestUtil.randomString());

		cpConfigurationListRel.setCreateDate(RandomTestUtil.nextDate());

		cpConfigurationListRel.setModifiedDate(RandomTestUtil.nextDate());

		cpConfigurationListRel.setClassNameId(RandomTestUtil.nextLong());

		cpConfigurationListRel.setClassPK(RandomTestUtil.nextLong());

		cpConfigurationListRel.setCPConfigurationListId(
			RandomTestUtil.nextLong());

		_cpConfigurationListRels.add(
			_persistence.update(cpConfigurationListRel));

		return cpConfigurationListRel;
	}

	private List<CPConfigurationListRel> _cpConfigurationListRels =
		new ArrayList<CPConfigurationListRel>();
	private CPConfigurationListRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:80569526