/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.changeset.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.changeset.exception.NoSuchCollectionException;
import com.liferay.changeset.model.ChangesetCollection;
import com.liferay.changeset.service.persistence.ChangesetCollectionPersistence;
import com.liferay.changeset.service.persistence.ChangesetCollectionUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class ChangesetCollectionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.changeset.service"));

	@Before
	public void setUp() {
		_persistence = ChangesetCollectionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ChangesetCollection> iterator =
			_changesetCollections.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetCollection changesetCollection = _persistence.create(pk);

		Assert.assertNotNull(changesetCollection);

		Assert.assertEquals(changesetCollection.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ChangesetCollection newChangesetCollection = addChangesetCollection();

		_persistence.remove(newChangesetCollection);

		ChangesetCollection existingChangesetCollection =
			_persistence.fetchByPrimaryKey(
				newChangesetCollection.getPrimaryKey());

		Assert.assertNull(existingChangesetCollection);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addChangesetCollection();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetCollection newChangesetCollection = _persistence.create(pk);

		newChangesetCollection.setGroupId(RandomTestUtil.nextLong());

		newChangesetCollection.setCompanyId(RandomTestUtil.nextLong());

		newChangesetCollection.setUserId(RandomTestUtil.nextLong());

		newChangesetCollection.setUserName(RandomTestUtil.randomString());

		newChangesetCollection.setCreateDate(RandomTestUtil.nextDate());

		newChangesetCollection.setModifiedDate(RandomTestUtil.nextDate());

		newChangesetCollection.setName(RandomTestUtil.randomString());

		newChangesetCollection.setDescription(RandomTestUtil.randomString());

		_changesetCollections.add(_persistence.update(newChangesetCollection));

		ChangesetCollection existingChangesetCollection =
			_persistence.findByPrimaryKey(
				newChangesetCollection.getPrimaryKey());

		Assert.assertEquals(
			existingChangesetCollection.getChangesetCollectionId(),
			newChangesetCollection.getChangesetCollectionId());
		Assert.assertEquals(
			existingChangesetCollection.getGroupId(),
			newChangesetCollection.getGroupId());
		Assert.assertEquals(
			existingChangesetCollection.getCompanyId(),
			newChangesetCollection.getCompanyId());
		Assert.assertEquals(
			existingChangesetCollection.getUserId(),
			newChangesetCollection.getUserId());
		Assert.assertEquals(
			existingChangesetCollection.getUserName(),
			newChangesetCollection.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingChangesetCollection.getCreateDate()),
			Time.getShortTimestamp(newChangesetCollection.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingChangesetCollection.getModifiedDate()),
			Time.getShortTimestamp(newChangesetCollection.getModifiedDate()));
		Assert.assertEquals(
			existingChangesetCollection.getName(),
			newChangesetCollection.getName());
		Assert.assertEquals(
			existingChangesetCollection.getDescription(),
			newChangesetCollection.getDescription());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
	}

	@Test
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ChangesetCollection newChangesetCollection = addChangesetCollection();

		ChangesetCollection existingChangesetCollection =
			_persistence.findByPrimaryKey(
				newChangesetCollection.getPrimaryKey());

		Assert.assertEquals(
			existingChangesetCollection, newChangesetCollection);
	}

	@Test(expected = NoSuchCollectionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ChangesetCollection> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ChangesetCollection", "changesetCollectionId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "name", true,
			"description", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ChangesetCollection newChangesetCollection = addChangesetCollection();

		ChangesetCollection existingChangesetCollection =
			_persistence.fetchByPrimaryKey(
				newChangesetCollection.getPrimaryKey());

		Assert.assertEquals(
			existingChangesetCollection, newChangesetCollection);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetCollection missingChangesetCollection =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingChangesetCollection);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ChangesetCollection newChangesetCollection1 = addChangesetCollection();
		ChangesetCollection newChangesetCollection2 = addChangesetCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChangesetCollection1.getPrimaryKey());
		primaryKeys.add(newChangesetCollection2.getPrimaryKey());

		Map<Serializable, ChangesetCollection> changesetCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, changesetCollections.size());
		Assert.assertEquals(
			newChangesetCollection1,
			changesetCollections.get(newChangesetCollection1.getPrimaryKey()));
		Assert.assertEquals(
			newChangesetCollection2,
			changesetCollections.get(newChangesetCollection2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ChangesetCollection> changesetCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(changesetCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ChangesetCollection newChangesetCollection = addChangesetCollection();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChangesetCollection.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ChangesetCollection> changesetCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, changesetCollections.size());
		Assert.assertEquals(
			newChangesetCollection,
			changesetCollections.get(newChangesetCollection.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ChangesetCollection> changesetCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(changesetCollections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ChangesetCollection newChangesetCollection = addChangesetCollection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newChangesetCollection.getPrimaryKey());

		Map<Serializable, ChangesetCollection> changesetCollections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, changesetCollections.size());
		Assert.assertEquals(
			newChangesetCollection,
			changesetCollections.get(newChangesetCollection.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ChangesetCollection newChangesetCollection = addChangesetCollection();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newChangesetCollection.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		ChangesetCollection changesetCollection) {

		Assert.assertEquals(
			Long.valueOf(changesetCollection.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				changesetCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			changesetCollection.getName(),
			ReflectionTestUtil.invoke(
				changesetCollection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected ChangesetCollection addChangesetCollection() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ChangesetCollection changesetCollection = _persistence.create(pk);

		changesetCollection.setGroupId(RandomTestUtil.nextLong());

		changesetCollection.setCompanyId(RandomTestUtil.nextLong());

		changesetCollection.setUserId(RandomTestUtil.nextLong());

		changesetCollection.setUserName(RandomTestUtil.randomString());

		changesetCollection.setCreateDate(RandomTestUtil.nextDate());

		changesetCollection.setModifiedDate(RandomTestUtil.nextDate());

		changesetCollection.setName(RandomTestUtil.randomString());

		changesetCollection.setDescription(RandomTestUtil.randomString());

		_changesetCollections.add(_persistence.update(changesetCollection));

		return changesetCollection;
	}

	private List<ChangesetCollection> _changesetCollections =
		new ArrayList<ChangesetCollection>();
	private ChangesetCollectionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}