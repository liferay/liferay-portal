/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.trash.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.trash.exception.NoSuchVersionException;
import com.liferay.trash.model.TrashVersion;
import com.liferay.trash.service.persistence.TrashVersionPersistence;
import com.liferay.trash.service.persistence.TrashVersionUtil;

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
public class TrashVersionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.trash.service"));

	@Before
	public void setUp() {
		_persistence = TrashVersionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<TrashVersion> iterator = _trashVersions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashVersion trashVersion = _persistence.create(pk);

		Assert.assertNotNull(trashVersion);

		Assert.assertEquals(trashVersion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		TrashVersion newTrashVersion = addTrashVersion();

		_persistence.remove(newTrashVersion);

		TrashVersion existingTrashVersion = _persistence.fetchByPrimaryKey(
			newTrashVersion.getPrimaryKey());

		Assert.assertNull(existingTrashVersion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addTrashVersion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashVersion newTrashVersion = _persistence.create(pk);

		newTrashVersion.setMvccVersion(RandomTestUtil.nextLong());

		newTrashVersion.setCtCollectionId(RandomTestUtil.nextLong());

		newTrashVersion.setCompanyId(RandomTestUtil.nextLong());

		newTrashVersion.setEntryId(RandomTestUtil.nextLong());

		newTrashVersion.setClassNameId(RandomTestUtil.nextLong());

		newTrashVersion.setClassPK(RandomTestUtil.nextLong());

		newTrashVersion.setTypeSettings(RandomTestUtil.randomString());

		newTrashVersion.setStatus(RandomTestUtil.nextInt());

		_trashVersions.add(_persistence.update(newTrashVersion));

		TrashVersion existingTrashVersion = _persistence.findByPrimaryKey(
			newTrashVersion.getPrimaryKey());

		Assert.assertEquals(
			existingTrashVersion.getMvccVersion(),
			newTrashVersion.getMvccVersion());
		Assert.assertEquals(
			existingTrashVersion.getCtCollectionId(),
			newTrashVersion.getCtCollectionId());
		Assert.assertEquals(
			existingTrashVersion.getVersionId(),
			newTrashVersion.getVersionId());
		Assert.assertEquals(
			existingTrashVersion.getCompanyId(),
			newTrashVersion.getCompanyId());
		Assert.assertEquals(
			existingTrashVersion.getEntryId(), newTrashVersion.getEntryId());
		Assert.assertEquals(
			existingTrashVersion.getClassNameId(),
			newTrashVersion.getClassNameId());
		Assert.assertEquals(
			existingTrashVersion.getClassPK(), newTrashVersion.getClassPK());
		Assert.assertEquals(
			existingTrashVersion.getTypeSettings(),
			newTrashVersion.getTypeSettings());
		Assert.assertEquals(
			existingTrashVersion.getStatus(), newTrashVersion.getStatus());
	}

	@Test
	public void testCountByEntryId() throws Exception {
		_persistence.countByEntryId(RandomTestUtil.nextLong());

		_persistence.countByEntryId(0L);
	}

	@Test
	public void testCountByE_CN() throws Exception {
		_persistence.countByE_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByE_CN(0L, 0L);
	}

	@Test
	public void testCountByCN_CPK() throws Exception {
		_persistence.countByCN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCN_CPK(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		TrashVersion newTrashVersion = addTrashVersion();

		TrashVersion existingTrashVersion = _persistence.findByPrimaryKey(
			newTrashVersion.getPrimaryKey());

		Assert.assertEquals(existingTrashVersion, newTrashVersion);
	}

	@Test(expected = NoSuchVersionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<TrashVersion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"TrashVersion", "mvccVersion", true, "ctCollectionId", true,
			"versionId", true, "companyId", true, "entryId", true,
			"classNameId", true, "classPK", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		TrashVersion newTrashVersion = addTrashVersion();

		TrashVersion existingTrashVersion = _persistence.fetchByPrimaryKey(
			newTrashVersion.getPrimaryKey());

		Assert.assertEquals(existingTrashVersion, newTrashVersion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashVersion missingTrashVersion = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingTrashVersion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		TrashVersion newTrashVersion1 = addTrashVersion();
		TrashVersion newTrashVersion2 = addTrashVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTrashVersion1.getPrimaryKey());
		primaryKeys.add(newTrashVersion2.getPrimaryKey());

		Map<Serializable, TrashVersion> trashVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, trashVersions.size());
		Assert.assertEquals(
			newTrashVersion1,
			trashVersions.get(newTrashVersion1.getPrimaryKey()));
		Assert.assertEquals(
			newTrashVersion2,
			trashVersions.get(newTrashVersion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, TrashVersion> trashVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(trashVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		TrashVersion newTrashVersion = addTrashVersion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTrashVersion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, TrashVersion> trashVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, trashVersions.size());
		Assert.assertEquals(
			newTrashVersion,
			trashVersions.get(newTrashVersion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, TrashVersion> trashVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(trashVersions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		TrashVersion newTrashVersion = addTrashVersion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newTrashVersion.getPrimaryKey());

		Map<Serializable, TrashVersion> trashVersions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, trashVersions.size());
		Assert.assertEquals(
			newTrashVersion,
			trashVersions.get(newTrashVersion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		TrashVersion newTrashVersion = addTrashVersion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newTrashVersion.getPrimaryKey()));
	}

	private void _assertOriginalValues(TrashVersion trashVersion) {
		Assert.assertEquals(
			Long.valueOf(trashVersion.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				trashVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(trashVersion.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				trashVersion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected TrashVersion addTrashVersion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		TrashVersion trashVersion = _persistence.create(pk);

		trashVersion.setMvccVersion(RandomTestUtil.nextLong());

		trashVersion.setCtCollectionId(RandomTestUtil.nextLong());

		trashVersion.setCompanyId(RandomTestUtil.nextLong());

		trashVersion.setEntryId(RandomTestUtil.nextLong());

		trashVersion.setClassNameId(RandomTestUtil.nextLong());

		trashVersion.setClassPK(RandomTestUtil.nextLong());

		trashVersion.setTypeSettings(RandomTestUtil.randomString());

		trashVersion.setStatus(RandomTestUtil.nextInt());

		_trashVersions.add(_persistence.update(trashVersion));

		return trashVersion;
	}

	private List<TrashVersion> _trashVersions = new ArrayList<TrashVersion>();
	private TrashVersionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}