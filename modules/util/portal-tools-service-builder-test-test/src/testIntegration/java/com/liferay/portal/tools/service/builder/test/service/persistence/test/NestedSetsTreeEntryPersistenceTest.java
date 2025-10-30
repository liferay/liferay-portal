/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchNestedSetsTreeEntryException;
import com.liferay.portal.tools.service.builder.test.model.NestedSetsTreeEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.NestedSetsTreeEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.NestedSetsTreeEntryUtil;

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
public class NestedSetsTreeEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = NestedSetsTreeEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NestedSetsTreeEntry> iterator =
			_nestedSetsTreeEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NestedSetsTreeEntry nestedSetsTreeEntry = _persistence.create(pk);

		Assert.assertNotNull(nestedSetsTreeEntry);

		Assert.assertEquals(nestedSetsTreeEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NestedSetsTreeEntry newNestedSetsTreeEntry = addNestedSetsTreeEntry();

		_persistence.remove(newNestedSetsTreeEntry);

		NestedSetsTreeEntry existingNestedSetsTreeEntry =
			_persistence.fetchByPrimaryKey(
				newNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertNull(existingNestedSetsTreeEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNestedSetsTreeEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NestedSetsTreeEntry newNestedSetsTreeEntry = _persistence.create(pk);

		newNestedSetsTreeEntry.setGroupId(RandomTestUtil.nextLong());

		newNestedSetsTreeEntry.setLeftNestedSetsTreeEntryId(
			RandomTestUtil.nextLong());

		newNestedSetsTreeEntry.setRightNestedSetsTreeEntryId(
			RandomTestUtil.nextLong());

		_nestedSetsTreeEntries.add(_persistence.update(newNestedSetsTreeEntry));

		NestedSetsTreeEntry existingNestedSetsTreeEntry =
			_persistence.findByPrimaryKey(
				newNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNestedSetsTreeEntry.getNestedSetsTreeEntryId(),
			newNestedSetsTreeEntry.getNestedSetsTreeEntryId());
		Assert.assertEquals(
			existingNestedSetsTreeEntry.getGroupId(),
			newNestedSetsTreeEntry.getGroupId());
		Assert.assertEquals(
			existingNestedSetsTreeEntry.getParentNestedSetsTreeEntryId(),
			newNestedSetsTreeEntry.getParentNestedSetsTreeEntryId());
		Assert.assertEquals(
			existingNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId(),
			newNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			existingNestedSetsTreeEntry.getRightNestedSetsTreeEntryId(),
			newNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NestedSetsTreeEntry newNestedSetsTreeEntry = addNestedSetsTreeEntry();

		NestedSetsTreeEntry existingNestedSetsTreeEntry =
			_persistence.findByPrimaryKey(
				newNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNestedSetsTreeEntry, newNestedSetsTreeEntry);
	}

	@Test(expected = NoSuchNestedSetsTreeEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NestedSetsTreeEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"NestedSetsTreeEntry", "nestedSetsTreeEntryId", true, "groupId",
			true, "parentNestedSetsTreeEntryId", true,
			"leftNestedSetsTreeEntryId", true, "rightNestedSetsTreeEntryId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NestedSetsTreeEntry newNestedSetsTreeEntry = addNestedSetsTreeEntry();

		NestedSetsTreeEntry existingNestedSetsTreeEntry =
			_persistence.fetchByPrimaryKey(
				newNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNestedSetsTreeEntry, newNestedSetsTreeEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NestedSetsTreeEntry missingNestedSetsTreeEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNestedSetsTreeEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NestedSetsTreeEntry newNestedSetsTreeEntry1 = addNestedSetsTreeEntry();
		NestedSetsTreeEntry newNestedSetsTreeEntry2 = addNestedSetsTreeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNestedSetsTreeEntry1.getPrimaryKey());
		primaryKeys.add(newNestedSetsTreeEntry2.getPrimaryKey());

		Map<Serializable, NestedSetsTreeEntry> nestedSetsTreeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, nestedSetsTreeEntries.size());
		Assert.assertEquals(
			newNestedSetsTreeEntry1,
			nestedSetsTreeEntries.get(newNestedSetsTreeEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newNestedSetsTreeEntry2,
			nestedSetsTreeEntries.get(newNestedSetsTreeEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NestedSetsTreeEntry> nestedSetsTreeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(nestedSetsTreeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NestedSetsTreeEntry newNestedSetsTreeEntry = addNestedSetsTreeEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNestedSetsTreeEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NestedSetsTreeEntry> nestedSetsTreeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, nestedSetsTreeEntries.size());
		Assert.assertEquals(
			newNestedSetsTreeEntry,
			nestedSetsTreeEntries.get(newNestedSetsTreeEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NestedSetsTreeEntry> nestedSetsTreeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(nestedSetsTreeEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NestedSetsTreeEntry newNestedSetsTreeEntry = addNestedSetsTreeEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNestedSetsTreeEntry.getPrimaryKey());

		Map<Serializable, NestedSetsTreeEntry> nestedSetsTreeEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, nestedSetsTreeEntries.size());
		Assert.assertEquals(
			newNestedSetsTreeEntry,
			nestedSetsTreeEntries.get(newNestedSetsTreeEntry.getPrimaryKey()));
	}

	protected NestedSetsTreeEntry addNestedSetsTreeEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NestedSetsTreeEntry nestedSetsTreeEntry = _persistence.create(pk);

		nestedSetsTreeEntry.setGroupId(RandomTestUtil.nextLong());

		nestedSetsTreeEntry.setLeftNestedSetsTreeEntryId(
			RandomTestUtil.nextLong());

		nestedSetsTreeEntry.setRightNestedSetsTreeEntryId(
			RandomTestUtil.nextLong());

		_nestedSetsTreeEntries.add(_persistence.update(nestedSetsTreeEntry));

		return nestedSetsTreeEntry;
	}

	@Test
	public void testMoveTree() throws Exception {
		long groupId = RandomTestUtil.nextLong();

		NestedSetsTreeEntry rootNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		long previousRootLeftNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId();
		long previousRootRightNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId();

		NestedSetsTreeEntry childNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			previousRootLeftNestedSetsTreeEntryId,
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			previousRootRightNestedSetsTreeEntryId + 2,
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			childNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			childNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
	}

	@Test
	public void testMoveTreeFromLeft() throws Exception {
		long groupId = RandomTestUtil.nextLong();

		NestedSetsTreeEntry parentNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		NestedSetsTreeEntry childNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, parentNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		parentNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			parentNestedSetsTreeEntry.getPrimaryKey());

		NestedSetsTreeEntry rootNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		long previousRootLeftNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId();
		long previousRootRightNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId();

		parentNestedSetsTreeEntry.setParentNestedSetsTreeEntryId(
			rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		_persistence.update(parentNestedSetsTreeEntry);

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());
		childNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			childNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			previousRootLeftNestedSetsTreeEntryId - 4,
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			previousRootRightNestedSetsTreeEntryId,
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			childNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			childNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
	}

	@Test
	public void testMoveTreeFromRight() throws Exception {
		long groupId = RandomTestUtil.nextLong();

		NestedSetsTreeEntry rootNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		long previousRootLeftNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId();
		long previousRootRightNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId();

		NestedSetsTreeEntry parentNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		NestedSetsTreeEntry childNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, parentNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		parentNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			parentNestedSetsTreeEntry.getPrimaryKey());

		parentNestedSetsTreeEntry.setParentNestedSetsTreeEntryId(
			rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		_persistence.update(parentNestedSetsTreeEntry);

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());
		childNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			childNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			previousRootLeftNestedSetsTreeEntryId,
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			previousRootRightNestedSetsTreeEntryId + 4,
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			childNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			childNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
	}

	@Test
	public void testMoveTreeIntoTreeFromLeft() throws Exception {
		long groupId = RandomTestUtil.nextLong();

		NestedSetsTreeEntry parentNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		NestedSetsTreeEntry parentChildNestedSetsTreeEntry =
			addNestedSetsTreeEntry(
				groupId, parentNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		parentNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			parentNestedSetsTreeEntry.getPrimaryKey());

		NestedSetsTreeEntry rootNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		NestedSetsTreeEntry leftRootChildNestedSetsTreeEntry =
			addNestedSetsTreeEntry(
				groupId, rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());

		NestedSetsTreeEntry rightRootChildNestedSetsTreeEntry =
			addNestedSetsTreeEntry(
				groupId, rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());

		long previousRootLeftNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId();
		long previousRootRightNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId();

		parentNestedSetsTreeEntry.setParentNestedSetsTreeEntryId(
			rightRootChildNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		_persistence.update(parentNestedSetsTreeEntry);

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());
		leftRootChildNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			leftRootChildNestedSetsTreeEntry.getPrimaryKey());
		rightRootChildNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rightRootChildNestedSetsTreeEntry.getPrimaryKey());
		parentChildNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			parentChildNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			previousRootLeftNestedSetsTreeEntryId - 4,
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			previousRootRightNestedSetsTreeEntryId,
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			leftRootChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 7,
			leftRootChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 3,
			rightRootChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			rightRootChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rightRootChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() +
				1,
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rightRootChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() -
				1,
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			parentChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			parentChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
	}

	@Test
	public void testMoveTreeIntoTreeFromRight() throws Exception {
		long groupId = RandomTestUtil.nextLong();

		NestedSetsTreeEntry rootNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		NestedSetsTreeEntry leftRootChildNestedSetsTreeEntry =
			addNestedSetsTreeEntry(
				groupId, rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());

		NestedSetsTreeEntry rightRootChildNestedSetsTreeEntry =
			addNestedSetsTreeEntry(
				groupId, rootNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());

		long previousRootLeftNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId();
		long previousRootRightNestedSetsTreeEntryId =
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId();

		NestedSetsTreeEntry parentNestedSetsTreeEntry = addNestedSetsTreeEntry(
			groupId, null);

		NestedSetsTreeEntry parentChildNestedSetsTreeEntry =
			addNestedSetsTreeEntry(
				groupId, parentNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		parentNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			parentNestedSetsTreeEntry.getPrimaryKey());

		parentNestedSetsTreeEntry.setParentNestedSetsTreeEntryId(
			leftRootChildNestedSetsTreeEntry.getNestedSetsTreeEntryId());

		_persistence.update(parentNestedSetsTreeEntry);

		rootNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rootNestedSetsTreeEntry.getPrimaryKey());
		leftRootChildNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			leftRootChildNestedSetsTreeEntry.getPrimaryKey());
		rightRootChildNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			rightRootChildNestedSetsTreeEntry.getPrimaryKey());
		parentChildNestedSetsTreeEntry = _persistence.fetchByPrimaryKey(
			parentChildNestedSetsTreeEntry.getPrimaryKey());

		Assert.assertEquals(
			previousRootLeftNestedSetsTreeEntryId,
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			previousRootRightNestedSetsTreeEntryId + 4,
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			leftRootChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 3,
			leftRootChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 7,
			rightRootChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			rootNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			rightRootChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			leftRootChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			leftRootChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() -
				1,
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId() + 1,
			parentChildNestedSetsTreeEntry.getLeftNestedSetsTreeEntryId());
		Assert.assertEquals(
			parentNestedSetsTreeEntry.getRightNestedSetsTreeEntryId() - 1,
			parentChildNestedSetsTreeEntry.getRightNestedSetsTreeEntryId());
	}

	protected NestedSetsTreeEntry addNestedSetsTreeEntry(
			long groupId, Long parentNestedSetsTreeEntryId)
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		NestedSetsTreeEntry nestedSetsTreeEntry = _persistence.create(pk);

		nestedSetsTreeEntry.setGroupId(groupId);

		nestedSetsTreeEntry.setLeftNestedSetsTreeEntryId(
			RandomTestUtil.nextLong());

		nestedSetsTreeEntry.setRightNestedSetsTreeEntryId(
			RandomTestUtil.nextLong());

		if (parentNestedSetsTreeEntryId != null) {
			nestedSetsTreeEntry.setParentNestedSetsTreeEntryId(
				parentNestedSetsTreeEntryId);
		}

		_nestedSetsTreeEntries.add(_persistence.update(nestedSetsTreeEntry));

		return nestedSetsTreeEntry;
	}

	private List<NestedSetsTreeEntry> _nestedSetsTreeEntries =
		new ArrayList<NestedSetsTreeEntry>();
	private NestedSetsTreeEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}