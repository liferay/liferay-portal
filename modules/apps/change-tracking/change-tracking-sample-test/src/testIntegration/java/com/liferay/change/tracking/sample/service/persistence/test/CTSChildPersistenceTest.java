/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.sample.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.sample.exception.NoSuchCTSChildException;
import com.liferay.change.tracking.sample.model.CTSChild;
import com.liferay.change.tracking.sample.service.CTSChildLocalServiceUtil;
import com.liferay.change.tracking.sample.service.persistence.CTSChildPersistence;
import com.liferay.change.tracking.sample.service.persistence.CTSChildUtil;
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
public class CTSChildPersistenceTest {

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
		_persistence = CTSChildUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTSChild> iterator = _ctsChilds.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSChild ctsChild = _persistence.create(pk);

		Assert.assertNotNull(ctsChild);

		Assert.assertEquals(ctsChild.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTSChild newCTSChild = addCTSChild();

		_persistence.remove(newCTSChild);

		CTSChild existingCTSChild = _persistence.fetchByPrimaryKey(
			newCTSChild.getPrimaryKey());

		Assert.assertNull(existingCTSChild);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTSChild();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSChild newCTSChild = _persistence.create(pk);

		newCTSChild.setMvccVersion(RandomTestUtil.nextLong());

		newCTSChild.setCtCollectionId(RandomTestUtil.nextLong());

		newCTSChild.setCompanyId(RandomTestUtil.nextLong());

		newCTSChild.setCtsGrandParentId(RandomTestUtil.nextLong());

		newCTSChild.setParentCTSChildId(RandomTestUtil.nextLong());

		newCTSChild.setCtsParentName(RandomTestUtil.randomString());

		newCTSChild.setName(RandomTestUtil.randomString());

		_ctsChilds.add(_persistence.update(newCTSChild));

		CTSChild existingCTSChild = _persistence.findByPrimaryKey(
			newCTSChild.getPrimaryKey());

		Assert.assertEquals(
			existingCTSChild.getMvccVersion(), newCTSChild.getMvccVersion());
		Assert.assertEquals(
			existingCTSChild.getCtCollectionId(),
			newCTSChild.getCtCollectionId());
		Assert.assertEquals(
			existingCTSChild.getCtsChildId(), newCTSChild.getCtsChildId());
		Assert.assertEquals(
			existingCTSChild.getCompanyId(), newCTSChild.getCompanyId());
		Assert.assertEquals(
			existingCTSChild.getCtsGrandParentId(),
			newCTSChild.getCtsGrandParentId());
		Assert.assertEquals(
			existingCTSChild.getParentCTSChildId(),
			newCTSChild.getParentCTSChildId());
		Assert.assertEquals(
			existingCTSChild.getCtsParentName(),
			newCTSChild.getCtsParentName());
		Assert.assertEquals(existingCTSChild.getName(), newCTSChild.getName());
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
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_P(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTSChild newCTSChild = addCTSChild();

		CTSChild existingCTSChild = _persistence.findByPrimaryKey(
			newCTSChild.getPrimaryKey());

		Assert.assertEquals(existingCTSChild, newCTSChild);
	}

	@Test(expected = NoSuchCTSChildException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTSChild> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTSChild", "mvccVersion", true, "ctCollectionId", true,
			"ctsChildId", true, "companyId", true, "ctsGrandParentId", true,
			"parentCTSChildId", true, "ctsParentName", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTSChild newCTSChild = addCTSChild();

		CTSChild existingCTSChild = _persistence.fetchByPrimaryKey(
			newCTSChild.getPrimaryKey());

		Assert.assertEquals(existingCTSChild, newCTSChild);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSChild missingCTSChild = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTSChild);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTSChild newCTSChild1 = addCTSChild();
		CTSChild newCTSChild2 = addCTSChild();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSChild1.getPrimaryKey());
		primaryKeys.add(newCTSChild2.getPrimaryKey());

		Map<Serializable, CTSChild> ctsChilds = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, ctsChilds.size());
		Assert.assertEquals(
			newCTSChild1, ctsChilds.get(newCTSChild1.getPrimaryKey()));
		Assert.assertEquals(
			newCTSChild2, ctsChilds.get(newCTSChild2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTSChild> ctsChilds = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctsChilds.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTSChild newCTSChild = addCTSChild();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSChild.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTSChild> ctsChilds = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctsChilds.size());
		Assert.assertEquals(
			newCTSChild, ctsChilds.get(newCTSChild.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTSChild> ctsChilds = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctsChilds.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTSChild newCTSChild = addCTSChild();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTSChild.getPrimaryKey());

		Map<Serializable, CTSChild> ctsChilds = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctsChilds.size());
		Assert.assertEquals(
			newCTSChild, ctsChilds.get(newCTSChild.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CTSChildLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CTSChild>() {

				@Override
				public void performAction(CTSChild ctsChild) {
					Assert.assertNotNull(ctsChild);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTSChild newCTSChild = addCTSChild();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSChild.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctsChildId", newCTSChild.getCtsChildId()));

		List<CTSChild> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTSChild existingCTSChild = result.get(0);

		Assert.assertEquals(existingCTSChild, newCTSChild);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSChild.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"ctsChildId", RandomTestUtil.nextLong()));

		List<CTSChild> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTSChild newCTSChild = addCTSChild();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSChild.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctsChildId"));

		Object newCtsChildId = newCTSChild.getCtsChildId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctsChildId", new Object[] {newCtsChildId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCtsChildId = result.get(0);

		Assert.assertEquals(existingCtsChildId, newCtsChildId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTSChild.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("ctsChildId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"ctsChildId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected CTSChild addCTSChild() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTSChild ctsChild = _persistence.create(pk);

		ctsChild.setMvccVersion(RandomTestUtil.nextLong());

		ctsChild.setCtCollectionId(RandomTestUtil.nextLong());

		ctsChild.setCompanyId(RandomTestUtil.nextLong());

		ctsChild.setCtsGrandParentId(RandomTestUtil.nextLong());

		ctsChild.setParentCTSChildId(RandomTestUtil.nextLong());

		ctsChild.setCtsParentName(RandomTestUtil.randomString());

		ctsChild.setName(RandomTestUtil.randomString());

		_ctsChilds.add(_persistence.update(ctsChild));

		return ctsChild;
	}

	private List<CTSChild> _ctsChilds = new ArrayList<CTSChild>();
	private CTSChildPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}