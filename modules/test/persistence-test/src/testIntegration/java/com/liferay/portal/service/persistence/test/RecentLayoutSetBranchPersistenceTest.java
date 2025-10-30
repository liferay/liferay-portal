/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchRecentLayoutSetBranchException;
import com.liferay.portal.kernel.model.RecentLayoutSetBranch;
import com.liferay.portal.kernel.service.persistence.RecentLayoutSetBranchPersistence;
import com.liferay.portal.kernel.service.persistence.RecentLayoutSetBranchUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class RecentLayoutSetBranchPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RecentLayoutSetBranchUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RecentLayoutSetBranch> iterator =
			_recentLayoutSetBranchs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutSetBranch recentLayoutSetBranch = _persistence.create(pk);

		Assert.assertNotNull(recentLayoutSetBranch);

		Assert.assertEquals(recentLayoutSetBranch.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RecentLayoutSetBranch newRecentLayoutSetBranch =
			addRecentLayoutSetBranch();

		_persistence.remove(newRecentLayoutSetBranch);

		RecentLayoutSetBranch existingRecentLayoutSetBranch =
			_persistence.fetchByPrimaryKey(
				newRecentLayoutSetBranch.getPrimaryKey());

		Assert.assertNull(existingRecentLayoutSetBranch);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRecentLayoutSetBranch();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutSetBranch newRecentLayoutSetBranch = _persistence.create(
			pk);

		newRecentLayoutSetBranch.setMvccVersion(RandomTestUtil.nextLong());

		newRecentLayoutSetBranch.setGroupId(RandomTestUtil.nextLong());

		newRecentLayoutSetBranch.setCompanyId(RandomTestUtil.nextLong());

		newRecentLayoutSetBranch.setUserId(RandomTestUtil.nextLong());

		newRecentLayoutSetBranch.setLayoutSetBranchId(
			RandomTestUtil.nextLong());

		newRecentLayoutSetBranch.setLayoutSetId(RandomTestUtil.nextLong());

		_recentLayoutSetBranchs.add(
			_persistence.update(newRecentLayoutSetBranch));

		RecentLayoutSetBranch existingRecentLayoutSetBranch =
			_persistence.findByPrimaryKey(
				newRecentLayoutSetBranch.getPrimaryKey());

		Assert.assertEquals(
			existingRecentLayoutSetBranch.getMvccVersion(),
			newRecentLayoutSetBranch.getMvccVersion());
		Assert.assertEquals(
			existingRecentLayoutSetBranch.getRecentLayoutSetBranchId(),
			newRecentLayoutSetBranch.getRecentLayoutSetBranchId());
		Assert.assertEquals(
			existingRecentLayoutSetBranch.getGroupId(),
			newRecentLayoutSetBranch.getGroupId());
		Assert.assertEquals(
			existingRecentLayoutSetBranch.getCompanyId(),
			newRecentLayoutSetBranch.getCompanyId());
		Assert.assertEquals(
			existingRecentLayoutSetBranch.getUserId(),
			newRecentLayoutSetBranch.getUserId());
		Assert.assertEquals(
			existingRecentLayoutSetBranch.getLayoutSetBranchId(),
			newRecentLayoutSetBranch.getLayoutSetBranchId());
		Assert.assertEquals(
			existingRecentLayoutSetBranch.getLayoutSetId(),
			newRecentLayoutSetBranch.getLayoutSetId());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByLayoutSetBranchId() throws Exception {
		_persistence.countByLayoutSetBranchId(RandomTestUtil.nextLong());

		_persistence.countByLayoutSetBranchId(0L);
	}

	@Test
	public void testCountByU_L() throws Exception {
		_persistence.countByU_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_L(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RecentLayoutSetBranch newRecentLayoutSetBranch =
			addRecentLayoutSetBranch();

		RecentLayoutSetBranch existingRecentLayoutSetBranch =
			_persistence.findByPrimaryKey(
				newRecentLayoutSetBranch.getPrimaryKey());

		Assert.assertEquals(
			existingRecentLayoutSetBranch, newRecentLayoutSetBranch);
	}

	@Test(expected = NoSuchRecentLayoutSetBranchException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RecentLayoutSetBranch> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RecentLayoutSetBranch", "mvccVersion", true,
			"recentLayoutSetBranchId", true, "groupId", true, "companyId", true,
			"userId", true, "layoutSetBranchId", true, "layoutSetId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RecentLayoutSetBranch newRecentLayoutSetBranch =
			addRecentLayoutSetBranch();

		RecentLayoutSetBranch existingRecentLayoutSetBranch =
			_persistence.fetchByPrimaryKey(
				newRecentLayoutSetBranch.getPrimaryKey());

		Assert.assertEquals(
			existingRecentLayoutSetBranch, newRecentLayoutSetBranch);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutSetBranch missingRecentLayoutSetBranch =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRecentLayoutSetBranch);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RecentLayoutSetBranch newRecentLayoutSetBranch1 =
			addRecentLayoutSetBranch();
		RecentLayoutSetBranch newRecentLayoutSetBranch2 =
			addRecentLayoutSetBranch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRecentLayoutSetBranch1.getPrimaryKey());
		primaryKeys.add(newRecentLayoutSetBranch2.getPrimaryKey());

		Map<Serializable, RecentLayoutSetBranch> recentLayoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, recentLayoutSetBranchs.size());
		Assert.assertEquals(
			newRecentLayoutSetBranch1,
			recentLayoutSetBranchs.get(
				newRecentLayoutSetBranch1.getPrimaryKey()));
		Assert.assertEquals(
			newRecentLayoutSetBranch2,
			recentLayoutSetBranchs.get(
				newRecentLayoutSetBranch2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RecentLayoutSetBranch> recentLayoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(recentLayoutSetBranchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RecentLayoutSetBranch newRecentLayoutSetBranch =
			addRecentLayoutSetBranch();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRecentLayoutSetBranch.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RecentLayoutSetBranch> recentLayoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, recentLayoutSetBranchs.size());
		Assert.assertEquals(
			newRecentLayoutSetBranch,
			recentLayoutSetBranchs.get(
				newRecentLayoutSetBranch.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RecentLayoutSetBranch> recentLayoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(recentLayoutSetBranchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RecentLayoutSetBranch newRecentLayoutSetBranch =
			addRecentLayoutSetBranch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRecentLayoutSetBranch.getPrimaryKey());

		Map<Serializable, RecentLayoutSetBranch> recentLayoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, recentLayoutSetBranchs.size());
		Assert.assertEquals(
			newRecentLayoutSetBranch,
			recentLayoutSetBranchs.get(
				newRecentLayoutSetBranch.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RecentLayoutSetBranch newRecentLayoutSetBranch =
			addRecentLayoutSetBranch();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newRecentLayoutSetBranch.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		RecentLayoutSetBranch recentLayoutSetBranch) {

		Assert.assertEquals(
			Long.valueOf(recentLayoutSetBranch.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				recentLayoutSetBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(recentLayoutSetBranch.getLayoutSetId()),
			ReflectionTestUtil.<Long>invoke(
				recentLayoutSetBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "layoutSetId"));
	}

	protected RecentLayoutSetBranch addRecentLayoutSetBranch()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		RecentLayoutSetBranch recentLayoutSetBranch = _persistence.create(pk);

		recentLayoutSetBranch.setMvccVersion(RandomTestUtil.nextLong());

		recentLayoutSetBranch.setGroupId(RandomTestUtil.nextLong());

		recentLayoutSetBranch.setCompanyId(RandomTestUtil.nextLong());

		recentLayoutSetBranch.setUserId(RandomTestUtil.nextLong());

		recentLayoutSetBranch.setLayoutSetBranchId(RandomTestUtil.nextLong());

		recentLayoutSetBranch.setLayoutSetId(RandomTestUtil.nextLong());

		_recentLayoutSetBranchs.add(_persistence.update(recentLayoutSetBranch));

		return recentLayoutSetBranch;
	}

	private List<RecentLayoutSetBranch> _recentLayoutSetBranchs =
		new ArrayList<RecentLayoutSetBranch>();
	private RecentLayoutSetBranchPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}