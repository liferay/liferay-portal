/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixRelException;
import com.liferay.osb.patcher.model.PatcherFixRel;
import com.liferay.osb.patcher.service.PatcherFixRelLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherFixRelPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixRelUtil;
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
public class PatcherFixRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherFixRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherFixRel> iterator = _patcherFixRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixRel patcherFixRel = _persistence.create(pk);

		Assert.assertNotNull(patcherFixRel);

		Assert.assertEquals(patcherFixRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		_persistence.remove(newPatcherFixRel);

		PatcherFixRel existingPatcherFixRel = _persistence.fetchByPrimaryKey(
			newPatcherFixRel.getPrimaryKey());

		Assert.assertNull(existingPatcherFixRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherFixRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixRel newPatcherFixRel = _persistence.create(pk);

		newPatcherFixRel.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherFixRel.setCompanyId(RandomTestUtil.nextLong());

		newPatcherFixRel.setChildPatcherFixId(RandomTestUtil.nextLong());

		newPatcherFixRel.setParentPatcherFixId(RandomTestUtil.nextLong());

		_patcherFixRels.add(_persistence.update(newPatcherFixRel));

		PatcherFixRel existingPatcherFixRel = _persistence.findByPrimaryKey(
			newPatcherFixRel.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherFixRel.getMvccVersion(),
			newPatcherFixRel.getMvccVersion());
		Assert.assertEquals(
			existingPatcherFixRel.getPatcherFixRelId(),
			newPatcherFixRel.getPatcherFixRelId());
		Assert.assertEquals(
			existingPatcherFixRel.getCompanyId(),
			newPatcherFixRel.getCompanyId());
		Assert.assertEquals(
			existingPatcherFixRel.getChildPatcherFixId(),
			newPatcherFixRel.getChildPatcherFixId());
		Assert.assertEquals(
			existingPatcherFixRel.getParentPatcherFixId(),
			newPatcherFixRel.getParentPatcherFixId());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		PatcherFixRel existingPatcherFixRel = _persistence.findByPrimaryKey(
			newPatcherFixRel.getPrimaryKey());

		Assert.assertEquals(existingPatcherFixRel, newPatcherFixRel);
	}

	@Test(expected = NoSuchPatcherFixRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherFixRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherFixRel", "mvccVersion", true, "patcherFixRelId",
			true, "companyId", true, "childPatcherFixId", true,
			"parentPatcherFixId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		PatcherFixRel existingPatcherFixRel = _persistence.fetchByPrimaryKey(
			newPatcherFixRel.getPrimaryKey());

		Assert.assertEquals(existingPatcherFixRel, newPatcherFixRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixRel missingPatcherFixRel = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherFixRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherFixRel newPatcherFixRel1 = addPatcherFixRel();
		PatcherFixRel newPatcherFixRel2 = addPatcherFixRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixRel1.getPrimaryKey());
		primaryKeys.add(newPatcherFixRel2.getPrimaryKey());

		Map<Serializable, PatcherFixRel> patcherFixRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherFixRels.size());
		Assert.assertEquals(
			newPatcherFixRel1,
			patcherFixRels.get(newPatcherFixRel1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherFixRel2,
			patcherFixRels.get(newPatcherFixRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherFixRel> patcherFixRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherFixRel> patcherFixRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixRels.size());
		Assert.assertEquals(
			newPatcherFixRel,
			patcherFixRels.get(newPatcherFixRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherFixRel> patcherFixRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixRel.getPrimaryKey());

		Map<Serializable, PatcherFixRel> patcherFixRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixRels.size());
		Assert.assertEquals(
			newPatcherFixRel,
			patcherFixRels.get(newPatcherFixRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherFixRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<PatcherFixRel>() {

				@Override
				public void performAction(PatcherFixRel patcherFixRel) {
					Assert.assertNotNull(patcherFixRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixRelId", newPatcherFixRel.getPatcherFixRelId()));

		List<PatcherFixRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherFixRel existingPatcherFixRel = result.get(0);

		Assert.assertEquals(existingPatcherFixRel, newPatcherFixRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixRelId", RandomTestUtil.nextLong()));

		List<PatcherFixRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherFixRel newPatcherFixRel = addPatcherFixRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixRelId"));

		Object newPatcherFixRelId = newPatcherFixRel.getPatcherFixRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixRelId", new Object[] {newPatcherFixRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherFixRelId = result.get(0);

		Assert.assertEquals(existingPatcherFixRelId, newPatcherFixRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixRelId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherFixRel addPatcherFixRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixRel patcherFixRel = _persistence.create(pk);

		patcherFixRel.setMvccVersion(RandomTestUtil.nextLong());

		patcherFixRel.setCompanyId(RandomTestUtil.nextLong());

		patcherFixRel.setChildPatcherFixId(RandomTestUtil.nextLong());

		patcherFixRel.setParentPatcherFixId(RandomTestUtil.nextLong());

		_patcherFixRels.add(_persistence.update(patcherFixRel));

		return patcherFixRel;
	}

	private List<PatcherFixRel> _patcherFixRels =
		new ArrayList<PatcherFixRel>();
	private PatcherFixRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}