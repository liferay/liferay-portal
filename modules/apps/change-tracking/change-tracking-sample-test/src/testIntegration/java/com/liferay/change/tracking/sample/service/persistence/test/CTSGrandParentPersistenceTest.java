/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.sample.exception.NoSuchCTSGrandParentException;
import com.liferay.change.tracking.sample.model.CTSGrandParent;
import com.liferay.change.tracking.sample.service.CTSGrandParentLocalServiceUtil;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSGrandParentUtil;
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
public class CTSGrandParentPersistenceTest {

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
		_persistence = CTSGrandParentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTSGrandParent> iterator = _ctsGrandParents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSGrandParent ctsGrandParent = _persistence.create(pk);

		Assert.assertNotNull(ctsGrandParent);

		Assert.assertEquals(ctsGrandParent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		_persistence.remove(newCTSGrandParent);

		CTSGrandParent existingCTSGrandParent = _persistence.fetchByPrimaryKey(
			newCTSGrandParent.getPrimaryKey());

		Assert.assertNull(existingCTSGrandParent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTSGrandParent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSGrandParent newCTSGrandParent = _persistence.create(pk);

		newCTSGrandParent.setMvccVersion(RandomTestUtil.nextLong());

		newCTSGrandParent.setCompanyId(RandomTestUtil.nextLong());

		newCTSGrandParent.setParentCTSGrandParentId(RandomTestUtil.nextLong());

		newCTSGrandParent.setName(RandomTestUtil.randomString());

		_ctsGrandParents.add(_persistence.update(newCTSGrandParent));

		CTSGrandParent existingCTSGrandParent = _persistence.findByPrimaryKey(
			newCTSGrandParent.getPrimaryKey());

		Assert.assertEquals(
			existingCTSGrandParent.getMvccVersion(),
			newCTSGrandParent.getMvccVersion());
		Assert.assertEquals(
			existingCTSGrandParent.getCtsGrandParentId(),
			newCTSGrandParent.getCtsGrandParentId());
		Assert.assertEquals(
			existingCTSGrandParent.getCompanyId(),
			newCTSGrandParent.getCompanyId());
		Assert.assertEquals(
			existingCTSGrandParent.getParentCTSGrandParentId(),
			newCTSGrandParent.getParentCTSGrandParentId());
		Assert.assertEquals(
			existingCTSGrandParent.getName(), newCTSGrandParent.getName());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		CTSGrandParent existingCTSGrandParent = _persistence.findByPrimaryKey(
			newCTSGrandParent.getPrimaryKey());

		Assert.assertEquals(existingCTSGrandParent, newCTSGrandParent);
	}

	@Test(expected = NoSuchCTSGrandParentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTSGrandParent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTSGrandParent", "mvccVersion", true, "ctsGrandParentId", true,
			"companyId", true, "parentCTSGrandParentId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		CTSGrandParent existingCTSGrandParent = _persistence.fetchByPrimaryKey(
			newCTSGrandParent.getPrimaryKey());

		Assert.assertEquals(existingCTSGrandParent, newCTSGrandParent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSGrandParent missingCTSGrandParent = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCTSGrandParent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTSGrandParent newCTSGrandParent1 = addCTSGrandParent();
		CTSGrandParent newCTSGrandParent2 = addCTSGrandParent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSGrandParent1.getPrimaryKey());
		primaryKeys.add(newCTSGrandParent2.getPrimaryKey());

		Map<Serializable, CTSGrandParent> ctsGrandParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctsGrandParents.size());
		Assert.assertEquals(
			newCTSGrandParent1,
			ctsGrandParents.get(newCTSGrandParent1.getPrimaryKey()));
		Assert.assertEquals(
			newCTSGrandParent2,
			ctsGrandParents.get(newCTSGrandParent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTSGrandParent> ctsGrandParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctsGrandParents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSGrandParent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTSGrandParent> ctsGrandParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctsGrandParents.size());
		Assert.assertEquals(
			newCTSGrandParent,
			ctsGrandParents.get(newCTSGrandParent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTSGrandParent> ctsGrandParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctsGrandParents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSGrandParent.getPrimaryKey());

		Map<Serializable, CTSGrandParent> ctsGrandParents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctsGrandParents.size());
		Assert.assertEquals(
			newCTSGrandParent,
			ctsGrandParents.get(newCTSGrandParent.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CTSGrandParentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CTSGrandParent>() {

				@Override
				public void performAction(CTSGrandParent ctsGrandParent) {
					Assert.assertNotNull(ctsGrandParent);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSGrandParent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctsGrandParentId", newCTSGrandParent.getCtsGrandParentId()));

		List<CTSGrandParent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTSGrandParent existingCTSGrandParent = result.get(0);

		Assert.assertEquals(existingCTSGrandParent, newCTSGrandParent);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSGrandParent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctsGrandParentId", RandomTestUtil.nextLong()));

		List<CTSGrandParent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTSGrandParent newCTSGrandParent = addCTSGrandParent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSGrandParent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctsGrandParentId"));

		Object newCtsGrandParentId = newCTSGrandParent.getCtsGrandParentId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctsGrandParentId", new Object[] {newCtsGrandParentId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCtsGrandParentId = result.get(0);

		Assert.assertEquals(existingCtsGrandParentId, newCtsGrandParentId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSGrandParent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctsGrandParentId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctsGrandParentId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CTSGrandParent addCTSGrandParent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSGrandParent ctsGrandParent = _persistence.create(pk);

		ctsGrandParent.setMvccVersion(RandomTestUtil.nextLong());

		ctsGrandParent.setCompanyId(RandomTestUtil.nextLong());

		ctsGrandParent.setParentCTSGrandParentId(RandomTestUtil.nextLong());

		ctsGrandParent.setName(RandomTestUtil.randomString());

		_ctsGrandParents.add(_persistence.update(ctsGrandParent));

		return ctsGrandParent;
	}

	private List<CTSGrandParent> _ctsGrandParents =
		new ArrayList<CTSGrandParent>();
	private CTSGrandParentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}