/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.sample.exception.NoSuchCTSParentException;
import com.liferay.change.tracking.sample.model.CTSParent;
import com.liferay.change.tracking.sample.service.CTSParentLocalServiceUtil;
import com.liferay.change.tracking.sample.service.persistence.CTSParentPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSParentUtil;
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
public class CTSParentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.change.tracking.sample.service"));

	@Before
	public void setUp() {
		_persistence = CTSParentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTSParent> iterator = _ctsParents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSParent ctsParent = _persistence.create(pk);

		Assert.assertNotNull(ctsParent);

		Assert.assertEquals(ctsParent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTSParent newCTSParent = addCTSParent();

		_persistence.remove(newCTSParent);

		CTSParent existingCTSParent = _persistence.fetchByPrimaryKey(
			newCTSParent.getPrimaryKey());

		Assert.assertNull(existingCTSParent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTSParent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSParent newCTSParent = _persistence.create(pk);

		newCTSParent.setMvccVersion(RandomTestUtil.nextLong());

		newCTSParent.setCtCollectionId(RandomTestUtil.nextLong());

		newCTSParent.setCompanyId(RandomTestUtil.nextLong());

		newCTSParent.setCtsGrandParentId(RandomTestUtil.nextLong());

		newCTSParent.setName(RandomTestUtil.randomString());

		_ctsParents.add(_persistence.update(newCTSParent));

		CTSParent existingCTSParent = _persistence.findByPrimaryKey(
			newCTSParent.getPrimaryKey());

		Assert.assertEquals(
			existingCTSParent.getMvccVersion(), newCTSParent.getMvccVersion());
		Assert.assertEquals(
			existingCTSParent.getCtCollectionId(),
			newCTSParent.getCtCollectionId());
		Assert.assertEquals(
			existingCTSParent.getCtsParentId(), newCTSParent.getCtsParentId());
		Assert.assertEquals(
			existingCTSParent.getCompanyId(), newCTSParent.getCompanyId());
		Assert.assertEquals(
			existingCTSParent.getCtsGrandParentId(),
			newCTSParent.getCtsGrandParentId());
		Assert.assertEquals(
			existingCTSParent.getName(), newCTSParent.getName());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTSParent newCTSParent = addCTSParent();

		CTSParent existingCTSParent = _persistence.findByPrimaryKey(
			newCTSParent.getPrimaryKey());

		Assert.assertEquals(existingCTSParent, newCTSParent);
	}

	@Test(expected = NoSuchCTSParentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTSParent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTSParent", "mvccVersion", true, "ctCollectionId", true,
			"ctsParentId", true, "companyId", true, "ctsGrandParentId", true,
			"name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTSParent newCTSParent = addCTSParent();

		CTSParent existingCTSParent = _persistence.fetchByPrimaryKey(
			newCTSParent.getPrimaryKey());

		Assert.assertEquals(existingCTSParent, newCTSParent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSParent missingCTSParent = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTSParent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTSParent newCTSParent1 = addCTSParent();
		CTSParent newCTSParent2 = addCTSParent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSParent1.getPrimaryKey());
		primaryKeys.add(newCTSParent2.getPrimaryKey());

		Map<Serializable, CTSParent> ctsParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctsParents.size());
		Assert.assertEquals(
			newCTSParent1, ctsParents.get(newCTSParent1.getPrimaryKey()));
		Assert.assertEquals(
			newCTSParent2, ctsParents.get(newCTSParent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTSParent> ctsParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctsParents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTSParent newCTSParent = addCTSParent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSParent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTSParent> ctsParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctsParents.size());
		Assert.assertEquals(
			newCTSParent, ctsParents.get(newCTSParent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTSParent> ctsParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctsParents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTSParent newCTSParent = addCTSParent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSParent.getPrimaryKey());

		Map<Serializable, CTSParent> ctsParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctsParents.size());
		Assert.assertEquals(
			newCTSParent, ctsParents.get(newCTSParent.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CTSParentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CTSParent>() {

				@Override
				public void performAction(CTSParent ctsParent) {
					Assert.assertNotNull(ctsParent);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTSParent newCTSParent = addCTSParent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSParent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctsParentId", newCTSParent.getCtsParentId()));

		List<CTSParent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTSParent existingCTSParent = result.get(0);

		Assert.assertEquals(existingCTSParent, newCTSParent);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSParent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctsParentId", RandomTestUtil.nextLong()));

		List<CTSParent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTSParent newCTSParent = addCTSParent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSParent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctsParentId"));

		Object newCtsParentId = newCTSParent.getCtsParentId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctsParentId", new Object[] {newCtsParentId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCtsParentId = result.get(0);

		Assert.assertEquals(existingCtsParentId, newCtsParentId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSParent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctsParentId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctsParentId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CTSParent addCTSParent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSParent ctsParent = _persistence.create(pk);

		ctsParent.setMvccVersion(RandomTestUtil.nextLong());

		ctsParent.setCtCollectionId(RandomTestUtil.nextLong());

		ctsParent.setCompanyId(RandomTestUtil.nextLong());

		ctsParent.setCtsGrandParentId(RandomTestUtil.nextLong());

		ctsParent.setName(RandomTestUtil.randomString());

		_ctsParents.add(_persistence.update(ctsParent));

		return ctsParent;
	}

	private List<CTSParent> _ctsParents = new ArrayList<CTSParent>();
	private CTSParentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}