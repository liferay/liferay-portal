/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.persistence.test;

import com.liferay.announcements.kernel.exception.NoSuchFlagException;
import com.liferay.announcements.kernel.model.AnnouncementsFlag;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsFlagPersistence;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsFlagUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
public class AnnouncementsFlagPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AnnouncementsFlagUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AnnouncementsFlag> iterator = _announcementsFlags.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsFlag announcementsFlag = _persistence.create(pk);

		Assert.assertNotNull(announcementsFlag);

		Assert.assertEquals(announcementsFlag.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AnnouncementsFlag newAnnouncementsFlag = addAnnouncementsFlag();

		_persistence.remove(newAnnouncementsFlag);

		AnnouncementsFlag existingAnnouncementsFlag =
			_persistence.fetchByPrimaryKey(
				newAnnouncementsFlag.getPrimaryKey());

		Assert.assertNull(existingAnnouncementsFlag);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAnnouncementsFlag();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsFlag newAnnouncementsFlag = _persistence.create(pk);

		newAnnouncementsFlag.setMvccVersion(RandomTestUtil.nextLong());

		newAnnouncementsFlag.setCtCollectionId(RandomTestUtil.nextLong());

		newAnnouncementsFlag.setCompanyId(RandomTestUtil.nextLong());

		newAnnouncementsFlag.setUserId(RandomTestUtil.nextLong());

		newAnnouncementsFlag.setCreateDate(RandomTestUtil.nextDate());

		newAnnouncementsFlag.setEntryId(RandomTestUtil.nextLong());

		newAnnouncementsFlag.setValue(RandomTestUtil.nextInt());

		_announcementsFlags.add(_persistence.update(newAnnouncementsFlag));

		AnnouncementsFlag existingAnnouncementsFlag =
			_persistence.findByPrimaryKey(newAnnouncementsFlag.getPrimaryKey());

		Assert.assertEquals(
			existingAnnouncementsFlag.getMvccVersion(),
			newAnnouncementsFlag.getMvccVersion());
		Assert.assertEquals(
			existingAnnouncementsFlag.getCtCollectionId(),
			newAnnouncementsFlag.getCtCollectionId());
		Assert.assertEquals(
			existingAnnouncementsFlag.getFlagId(),
			newAnnouncementsFlag.getFlagId());
		Assert.assertEquals(
			existingAnnouncementsFlag.getCompanyId(),
			newAnnouncementsFlag.getCompanyId());
		Assert.assertEquals(
			existingAnnouncementsFlag.getUserId(),
			newAnnouncementsFlag.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAnnouncementsFlag.getCreateDate()),
			Time.getShortTimestamp(newAnnouncementsFlag.getCreateDate()));
		Assert.assertEquals(
			existingAnnouncementsFlag.getEntryId(),
			newAnnouncementsFlag.getEntryId());
		Assert.assertEquals(
			existingAnnouncementsFlag.getValue(),
			newAnnouncementsFlag.getValue());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByEntryId() throws Exception {
		_persistence.countByEntryId(RandomTestUtil.nextLong());

		_persistence.countByEntryId(0L);
	}

	@Test
	public void testCountByU_E_V() throws Exception {
		_persistence.countByU_E_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByU_E_V(0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AnnouncementsFlag newAnnouncementsFlag = addAnnouncementsFlag();

		AnnouncementsFlag existingAnnouncementsFlag =
			_persistence.findByPrimaryKey(newAnnouncementsFlag.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsFlag, newAnnouncementsFlag);
	}

	@Test(expected = NoSuchFlagException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AnnouncementsFlag> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AnnouncementsFlag", "mvccVersion", true, "ctCollectionId", true,
			"flagId", true, "companyId", true, "userId", true, "createDate",
			true, "entryId", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AnnouncementsFlag newAnnouncementsFlag = addAnnouncementsFlag();

		AnnouncementsFlag existingAnnouncementsFlag =
			_persistence.fetchByPrimaryKey(
				newAnnouncementsFlag.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsFlag, newAnnouncementsFlag);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsFlag missingAnnouncementsFlag =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAnnouncementsFlag);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AnnouncementsFlag newAnnouncementsFlag1 = addAnnouncementsFlag();
		AnnouncementsFlag newAnnouncementsFlag2 = addAnnouncementsFlag();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsFlag1.getPrimaryKey());
		primaryKeys.add(newAnnouncementsFlag2.getPrimaryKey());

		Map<Serializable, AnnouncementsFlag> announcementsFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, announcementsFlags.size());
		Assert.assertEquals(
			newAnnouncementsFlag1,
			announcementsFlags.get(newAnnouncementsFlag1.getPrimaryKey()));
		Assert.assertEquals(
			newAnnouncementsFlag2,
			announcementsFlags.get(newAnnouncementsFlag2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AnnouncementsFlag> announcementsFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(announcementsFlags.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AnnouncementsFlag newAnnouncementsFlag = addAnnouncementsFlag();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsFlag.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AnnouncementsFlag> announcementsFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, announcementsFlags.size());
		Assert.assertEquals(
			newAnnouncementsFlag,
			announcementsFlags.get(newAnnouncementsFlag.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AnnouncementsFlag> announcementsFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(announcementsFlags.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AnnouncementsFlag newAnnouncementsFlag = addAnnouncementsFlag();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsFlag.getPrimaryKey());

		Map<Serializable, AnnouncementsFlag> announcementsFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, announcementsFlags.size());
		Assert.assertEquals(
			newAnnouncementsFlag,
			announcementsFlags.get(newAnnouncementsFlag.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AnnouncementsFlag newAnnouncementsFlag = addAnnouncementsFlag();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAnnouncementsFlag.getPrimaryKey()));
	}

	private void _assertOriginalValues(AnnouncementsFlag announcementsFlag) {
		Assert.assertEquals(
			Long.valueOf(announcementsFlag.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				announcementsFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(announcementsFlag.getEntryId()),
			ReflectionTestUtil.<Long>invoke(
				announcementsFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "entryId"));
		Assert.assertEquals(
			Integer.valueOf(announcementsFlag.getValue()),
			ReflectionTestUtil.<Integer>invoke(
				announcementsFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "value"));
	}

	protected AnnouncementsFlag addAnnouncementsFlag() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsFlag announcementsFlag = _persistence.create(pk);

		announcementsFlag.setMvccVersion(RandomTestUtil.nextLong());

		announcementsFlag.setCtCollectionId(RandomTestUtil.nextLong());

		announcementsFlag.setCompanyId(RandomTestUtil.nextLong());

		announcementsFlag.setUserId(RandomTestUtil.nextLong());

		announcementsFlag.setCreateDate(RandomTestUtil.nextDate());

		announcementsFlag.setEntryId(RandomTestUtil.nextLong());

		announcementsFlag.setValue(RandomTestUtil.nextInt());

		_announcementsFlags.add(_persistence.update(announcementsFlag));

		return announcementsFlag;
	}

	private List<AnnouncementsFlag> _announcementsFlags =
		new ArrayList<AnnouncementsFlag>();
	private AnnouncementsFlagPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}