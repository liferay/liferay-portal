/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchRecentLayoutBranchException;
import com.liferay.portal.kernel.model.RecentLayoutBranch;
import com.liferay.portal.kernel.service.persistence.RecentLayoutBranchPersistence;
import com.liferay.portal.kernel.service.persistence.RecentLayoutBranchUtil;
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
public class RecentLayoutBranchPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RecentLayoutBranchUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RecentLayoutBranch> iterator = _recentLayoutBranchs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutBranch recentLayoutBranch = _persistence.create(pk);

		Assert.assertNotNull(recentLayoutBranch);

		Assert.assertEquals(recentLayoutBranch.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RecentLayoutBranch newRecentLayoutBranch = addRecentLayoutBranch();

		_persistence.remove(newRecentLayoutBranch);

		RecentLayoutBranch existingRecentLayoutBranch =
			_persistence.fetchByPrimaryKey(
				newRecentLayoutBranch.getPrimaryKey());

		Assert.assertNull(existingRecentLayoutBranch);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRecentLayoutBranch();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutBranch newRecentLayoutBranch = _persistence.create(pk);

		newRecentLayoutBranch.setMvccVersion(RandomTestUtil.nextLong());

		newRecentLayoutBranch.setGroupId(RandomTestUtil.nextLong());

		newRecentLayoutBranch.setCompanyId(RandomTestUtil.nextLong());

		newRecentLayoutBranch.setUserId(RandomTestUtil.nextLong());

		newRecentLayoutBranch.setLayoutBranchId(RandomTestUtil.nextLong());

		newRecentLayoutBranch.setLayoutSetBranchId(RandomTestUtil.nextLong());

		newRecentLayoutBranch.setPlid(RandomTestUtil.nextLong());

		_recentLayoutBranchs.add(_persistence.update(newRecentLayoutBranch));

		RecentLayoutBranch existingRecentLayoutBranch =
			_persistence.findByPrimaryKey(
				newRecentLayoutBranch.getPrimaryKey());

		Assert.assertEquals(
			existingRecentLayoutBranch.getMvccVersion(),
			newRecentLayoutBranch.getMvccVersion());
		Assert.assertEquals(
			existingRecentLayoutBranch.getRecentLayoutBranchId(),
			newRecentLayoutBranch.getRecentLayoutBranchId());
		Assert.assertEquals(
			existingRecentLayoutBranch.getGroupId(),
			newRecentLayoutBranch.getGroupId());
		Assert.assertEquals(
			existingRecentLayoutBranch.getCompanyId(),
			newRecentLayoutBranch.getCompanyId());
		Assert.assertEquals(
			existingRecentLayoutBranch.getUserId(),
			newRecentLayoutBranch.getUserId());
		Assert.assertEquals(
			existingRecentLayoutBranch.getLayoutBranchId(),
			newRecentLayoutBranch.getLayoutBranchId());
		Assert.assertEquals(
			existingRecentLayoutBranch.getLayoutSetBranchId(),
			newRecentLayoutBranch.getLayoutSetBranchId());
		Assert.assertEquals(
			existingRecentLayoutBranch.getPlid(),
			newRecentLayoutBranch.getPlid());
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
	public void testCountByLayoutBranchId() throws Exception {
		_persistence.countByLayoutBranchId(RandomTestUtil.nextLong());

		_persistence.countByLayoutBranchId(0L);
	}

	@Test
	public void testCountByU_L_P() throws Exception {
		_persistence.countByU_L_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByU_L_P(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RecentLayoutBranch newRecentLayoutBranch = addRecentLayoutBranch();

		RecentLayoutBranch existingRecentLayoutBranch =
			_persistence.findByPrimaryKey(
				newRecentLayoutBranch.getPrimaryKey());

		Assert.assertEquals(existingRecentLayoutBranch, newRecentLayoutBranch);
	}

	@Test(expected = NoSuchRecentLayoutBranchException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RecentLayoutBranch> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RecentLayoutBranch", "mvccVersion", true, "recentLayoutBranchId",
			true, "groupId", true, "companyId", true, "userId", true,
			"layoutBranchId", true, "layoutSetBranchId", true, "plid", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RecentLayoutBranch newRecentLayoutBranch = addRecentLayoutBranch();

		RecentLayoutBranch existingRecentLayoutBranch =
			_persistence.fetchByPrimaryKey(
				newRecentLayoutBranch.getPrimaryKey());

		Assert.assertEquals(existingRecentLayoutBranch, newRecentLayoutBranch);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutBranch missingRecentLayoutBranch =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRecentLayoutBranch);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RecentLayoutBranch newRecentLayoutBranch1 = addRecentLayoutBranch();
		RecentLayoutBranch newRecentLayoutBranch2 = addRecentLayoutBranch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRecentLayoutBranch1.getPrimaryKey());
		primaryKeys.add(newRecentLayoutBranch2.getPrimaryKey());

		Map<Serializable, RecentLayoutBranch> recentLayoutBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, recentLayoutBranchs.size());
		Assert.assertEquals(
			newRecentLayoutBranch1,
			recentLayoutBranchs.get(newRecentLayoutBranch1.getPrimaryKey()));
		Assert.assertEquals(
			newRecentLayoutBranch2,
			recentLayoutBranchs.get(newRecentLayoutBranch2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RecentLayoutBranch> recentLayoutBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(recentLayoutBranchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RecentLayoutBranch newRecentLayoutBranch = addRecentLayoutBranch();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRecentLayoutBranch.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RecentLayoutBranch> recentLayoutBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, recentLayoutBranchs.size());
		Assert.assertEquals(
			newRecentLayoutBranch,
			recentLayoutBranchs.get(newRecentLayoutBranch.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RecentLayoutBranch> recentLayoutBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(recentLayoutBranchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RecentLayoutBranch newRecentLayoutBranch = addRecentLayoutBranch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRecentLayoutBranch.getPrimaryKey());

		Map<Serializable, RecentLayoutBranch> recentLayoutBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, recentLayoutBranchs.size());
		Assert.assertEquals(
			newRecentLayoutBranch,
			recentLayoutBranchs.get(newRecentLayoutBranch.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RecentLayoutBranch newRecentLayoutBranch = addRecentLayoutBranch();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newRecentLayoutBranch.getPrimaryKey()));
	}

	private void _assertOriginalValues(RecentLayoutBranch recentLayoutBranch) {
		Assert.assertEquals(
			Long.valueOf(recentLayoutBranch.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				recentLayoutBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(recentLayoutBranch.getLayoutSetBranchId()),
			ReflectionTestUtil.<Long>invoke(
				recentLayoutBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "layoutSetBranchId"));
		Assert.assertEquals(
			Long.valueOf(recentLayoutBranch.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				recentLayoutBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
	}

	protected RecentLayoutBranch addRecentLayoutBranch() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RecentLayoutBranch recentLayoutBranch = _persistence.create(pk);

		recentLayoutBranch.setMvccVersion(RandomTestUtil.nextLong());

		recentLayoutBranch.setGroupId(RandomTestUtil.nextLong());

		recentLayoutBranch.setCompanyId(RandomTestUtil.nextLong());

		recentLayoutBranch.setUserId(RandomTestUtil.nextLong());

		recentLayoutBranch.setLayoutBranchId(RandomTestUtil.nextLong());

		recentLayoutBranch.setLayoutSetBranchId(RandomTestUtil.nextLong());

		recentLayoutBranch.setPlid(RandomTestUtil.nextLong());

		_recentLayoutBranchs.add(_persistence.update(recentLayoutBranch));

		return recentLayoutBranch;
	}

	private List<RecentLayoutBranch> _recentLayoutBranchs =
		new ArrayList<RecentLayoutBranch>();
	private RecentLayoutBranchPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}