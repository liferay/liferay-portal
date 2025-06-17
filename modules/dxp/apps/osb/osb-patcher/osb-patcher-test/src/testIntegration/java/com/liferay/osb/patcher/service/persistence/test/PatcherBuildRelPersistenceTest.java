/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherBuildRelException;
import com.liferay.osb.patcher.model.PatcherBuildRel;
import com.liferay.osb.patcher.service.PatcherBuildRelLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherBuildRelPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherBuildRelUtil;
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
public class PatcherBuildRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherBuildRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherBuildRel> iterator = _patcherBuildRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuildRel patcherBuildRel = _persistence.create(pk);

		Assert.assertNotNull(patcherBuildRel);

		Assert.assertEquals(patcherBuildRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		_persistence.remove(newPatcherBuildRel);

		PatcherBuildRel existingPatcherBuildRel =
			_persistence.fetchByPrimaryKey(newPatcherBuildRel.getPrimaryKey());

		Assert.assertNull(existingPatcherBuildRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherBuildRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuildRel newPatcherBuildRel = _persistence.create(pk);

		newPatcherBuildRel.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherBuildRel.setCompanyId(RandomTestUtil.nextLong());

		newPatcherBuildRel.setChildPatcherBuildId(RandomTestUtil.nextLong());

		newPatcherBuildRel.setParentPatcherBuildId(RandomTestUtil.nextLong());

		_patcherBuildRels.add(_persistence.update(newPatcherBuildRel));

		PatcherBuildRel existingPatcherBuildRel = _persistence.findByPrimaryKey(
			newPatcherBuildRel.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherBuildRel.getMvccVersion(),
			newPatcherBuildRel.getMvccVersion());
		Assert.assertEquals(
			existingPatcherBuildRel.getPatcherBuildRelId(),
			newPatcherBuildRel.getPatcherBuildRelId());
		Assert.assertEquals(
			existingPatcherBuildRel.getCompanyId(),
			newPatcherBuildRel.getCompanyId());
		Assert.assertEquals(
			existingPatcherBuildRel.getChildPatcherBuildId(),
			newPatcherBuildRel.getChildPatcherBuildId());
		Assert.assertEquals(
			existingPatcherBuildRel.getParentPatcherBuildId(),
			newPatcherBuildRel.getParentPatcherBuildId());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		PatcherBuildRel existingPatcherBuildRel = _persistence.findByPrimaryKey(
			newPatcherBuildRel.getPrimaryKey());

		Assert.assertEquals(existingPatcherBuildRel, newPatcherBuildRel);
	}

	@Test(expected = NoSuchPatcherBuildRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherBuildRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherBuildRel", "mvccVersion", true,
			"patcherBuildRelId", true, "companyId", true, "childPatcherBuildId",
			true, "parentPatcherBuildId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		PatcherBuildRel existingPatcherBuildRel =
			_persistence.fetchByPrimaryKey(newPatcherBuildRel.getPrimaryKey());

		Assert.assertEquals(existingPatcherBuildRel, newPatcherBuildRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuildRel missingPatcherBuildRel = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingPatcherBuildRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherBuildRel newPatcherBuildRel1 = addPatcherBuildRel();
		PatcherBuildRel newPatcherBuildRel2 = addPatcherBuildRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherBuildRel1.getPrimaryKey());
		primaryKeys.add(newPatcherBuildRel2.getPrimaryKey());

		Map<Serializable, PatcherBuildRel> patcherBuildRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherBuildRels.size());
		Assert.assertEquals(
			newPatcherBuildRel1,
			patcherBuildRels.get(newPatcherBuildRel1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherBuildRel2,
			patcherBuildRels.get(newPatcherBuildRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherBuildRel> patcherBuildRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherBuildRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherBuildRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherBuildRel> patcherBuildRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherBuildRels.size());
		Assert.assertEquals(
			newPatcherBuildRel,
			patcherBuildRels.get(newPatcherBuildRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherBuildRel> patcherBuildRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherBuildRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherBuildRel.getPrimaryKey());

		Map<Serializable, PatcherBuildRel> patcherBuildRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherBuildRels.size());
		Assert.assertEquals(
			newPatcherBuildRel,
			patcherBuildRels.get(newPatcherBuildRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherBuildRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<PatcherBuildRel>() {

				@Override
				public void performAction(PatcherBuildRel patcherBuildRel) {
					Assert.assertNotNull(patcherBuildRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuildRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherBuildRelId",
				newPatcherBuildRel.getPatcherBuildRelId()));

		List<PatcherBuildRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherBuildRel existingPatcherBuildRel = result.get(0);

		Assert.assertEquals(existingPatcherBuildRel, newPatcherBuildRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuildRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherBuildRelId", RandomTestUtil.nextLong()));

		List<PatcherBuildRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherBuildRel newPatcherBuildRel = addPatcherBuildRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuildRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherBuildRelId"));

		Object newPatcherBuildRelId = newPatcherBuildRel.getPatcherBuildRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherBuildRelId", new Object[] {newPatcherBuildRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherBuildRelId = result.get(0);

		Assert.assertEquals(existingPatcherBuildRelId, newPatcherBuildRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherBuildRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherBuildRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherBuildRelId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherBuildRel addPatcherBuildRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherBuildRel patcherBuildRel = _persistence.create(pk);

		patcherBuildRel.setMvccVersion(RandomTestUtil.nextLong());

		patcherBuildRel.setCompanyId(RandomTestUtil.nextLong());

		patcherBuildRel.setChildPatcherBuildId(RandomTestUtil.nextLong());

		patcherBuildRel.setParentPatcherBuildId(RandomTestUtil.nextLong());

		_patcherBuildRels.add(_persistence.update(patcherBuildRel));

		return patcherBuildRel;
	}

	private List<PatcherBuildRel> _patcherBuildRels =
		new ArrayList<PatcherBuildRel>();
	private PatcherBuildRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}