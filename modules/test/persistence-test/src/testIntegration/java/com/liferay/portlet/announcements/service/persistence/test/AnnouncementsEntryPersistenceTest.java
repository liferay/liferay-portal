/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.announcements.service.persistence.test;

import com.liferay.announcements.kernel.exception.NoSuchEntryException;
import com.liferay.announcements.kernel.model.AnnouncementsEntry;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsEntryPersistence;
import com.liferay.announcements.kernel.service.persistence.AnnouncementsEntryUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class AnnouncementsEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = AnnouncementsEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AnnouncementsEntry> iterator =
			_announcementsEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsEntry announcementsEntry = _persistence.create(pk);

		Assert.assertNotNull(announcementsEntry);

		Assert.assertEquals(announcementsEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AnnouncementsEntry newAnnouncementsEntry = addAnnouncementsEntry();

		_persistence.remove(newAnnouncementsEntry);

		AnnouncementsEntry existingAnnouncementsEntry =
			_persistence.fetchByPrimaryKey(
				newAnnouncementsEntry.getPrimaryKey());

		Assert.assertNull(existingAnnouncementsEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAnnouncementsEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsEntry newAnnouncementsEntry = _persistence.create(pk);

		newAnnouncementsEntry.setMvccVersion(RandomTestUtil.nextLong());

		newAnnouncementsEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newAnnouncementsEntry.setUuid(RandomTestUtil.randomString());

		newAnnouncementsEntry.setCompanyId(RandomTestUtil.nextLong());

		newAnnouncementsEntry.setUserId(RandomTestUtil.nextLong());

		newAnnouncementsEntry.setUserName(RandomTestUtil.randomString());

		newAnnouncementsEntry.setCreateDate(RandomTestUtil.nextDate());

		newAnnouncementsEntry.setModifiedDate(RandomTestUtil.nextDate());

		newAnnouncementsEntry.setClassNameId(RandomTestUtil.nextLong());

		newAnnouncementsEntry.setClassPK(RandomTestUtil.nextLong());

		newAnnouncementsEntry.setTitle(RandomTestUtil.randomString());

		newAnnouncementsEntry.setContent(RandomTestUtil.randomString());

		newAnnouncementsEntry.setUrl(RandomTestUtil.randomString());

		newAnnouncementsEntry.setType(RandomTestUtil.randomString());

		newAnnouncementsEntry.setDisplayDate(RandomTestUtil.nextDate());

		newAnnouncementsEntry.setExpirationDate(RandomTestUtil.nextDate());

		newAnnouncementsEntry.setPriority(RandomTestUtil.nextInt());

		newAnnouncementsEntry.setAlert(RandomTestUtil.randomBoolean());

		_announcementsEntries.add(_persistence.update(newAnnouncementsEntry));

		AnnouncementsEntry existingAnnouncementsEntry =
			_persistence.findByPrimaryKey(
				newAnnouncementsEntry.getPrimaryKey());

		Assert.assertEquals(
			existingAnnouncementsEntry.getMvccVersion(),
			newAnnouncementsEntry.getMvccVersion());
		Assert.assertEquals(
			existingAnnouncementsEntry.getCtCollectionId(),
			newAnnouncementsEntry.getCtCollectionId());
		Assert.assertEquals(
			existingAnnouncementsEntry.getUuid(),
			newAnnouncementsEntry.getUuid());
		Assert.assertEquals(
			existingAnnouncementsEntry.getEntryId(),
			newAnnouncementsEntry.getEntryId());
		Assert.assertEquals(
			existingAnnouncementsEntry.getCompanyId(),
			newAnnouncementsEntry.getCompanyId());
		Assert.assertEquals(
			existingAnnouncementsEntry.getUserId(),
			newAnnouncementsEntry.getUserId());
		Assert.assertEquals(
			existingAnnouncementsEntry.getUserName(),
			newAnnouncementsEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAnnouncementsEntry.getCreateDate()),
			Time.getShortTimestamp(newAnnouncementsEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAnnouncementsEntry.getModifiedDate()),
			Time.getShortTimestamp(newAnnouncementsEntry.getModifiedDate()));
		Assert.assertEquals(
			existingAnnouncementsEntry.getClassNameId(),
			newAnnouncementsEntry.getClassNameId());
		Assert.assertEquals(
			existingAnnouncementsEntry.getClassPK(),
			newAnnouncementsEntry.getClassPK());
		Assert.assertEquals(
			existingAnnouncementsEntry.getTitle(),
			newAnnouncementsEntry.getTitle());
		Assert.assertEquals(
			existingAnnouncementsEntry.getContent(),
			newAnnouncementsEntry.getContent());
		Assert.assertEquals(
			existingAnnouncementsEntry.getUrl(),
			newAnnouncementsEntry.getUrl());
		Assert.assertEquals(
			existingAnnouncementsEntry.getType(),
			newAnnouncementsEntry.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAnnouncementsEntry.getDisplayDate()),
			Time.getShortTimestamp(newAnnouncementsEntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAnnouncementsEntry.getExpirationDate()),
			Time.getShortTimestamp(newAnnouncementsEntry.getExpirationDate()));
		Assert.assertEquals(
			existingAnnouncementsEntry.getPriority(),
			newAnnouncementsEntry.getPriority());
		Assert.assertEquals(
			existingAnnouncementsEntry.isAlert(),
			newAnnouncementsEntry.isAlert());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_A() throws Exception {
		_persistence.countByC_C_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_C_A(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_C_C_A() throws Exception {
		_persistence.countByC_C_C_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_C_C_A(0L, 0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AnnouncementsEntry newAnnouncementsEntry = addAnnouncementsEntry();

		AnnouncementsEntry existingAnnouncementsEntry =
			_persistence.findByPrimaryKey(
				newAnnouncementsEntry.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsEntry, newAnnouncementsEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AnnouncementsEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AnnouncementsEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "entryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "title", true, "url", true,
			"type", true, "displayDate", true, "expirationDate", true,
			"priority", true, "alert", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AnnouncementsEntry newAnnouncementsEntry = addAnnouncementsEntry();

		AnnouncementsEntry existingAnnouncementsEntry =
			_persistence.fetchByPrimaryKey(
				newAnnouncementsEntry.getPrimaryKey());

		Assert.assertEquals(existingAnnouncementsEntry, newAnnouncementsEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsEntry missingAnnouncementsEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAnnouncementsEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AnnouncementsEntry newAnnouncementsEntry1 = addAnnouncementsEntry();
		AnnouncementsEntry newAnnouncementsEntry2 = addAnnouncementsEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsEntry1.getPrimaryKey());
		primaryKeys.add(newAnnouncementsEntry2.getPrimaryKey());

		Map<Serializable, AnnouncementsEntry> announcementsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, announcementsEntries.size());
		Assert.assertEquals(
			newAnnouncementsEntry1,
			announcementsEntries.get(newAnnouncementsEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newAnnouncementsEntry2,
			announcementsEntries.get(newAnnouncementsEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AnnouncementsEntry> announcementsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(announcementsEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AnnouncementsEntry newAnnouncementsEntry = addAnnouncementsEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AnnouncementsEntry> announcementsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, announcementsEntries.size());
		Assert.assertEquals(
			newAnnouncementsEntry,
			announcementsEntries.get(newAnnouncementsEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AnnouncementsEntry> announcementsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(announcementsEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AnnouncementsEntry newAnnouncementsEntry = addAnnouncementsEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAnnouncementsEntry.getPrimaryKey());

		Map<Serializable, AnnouncementsEntry> announcementsEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, announcementsEntries.size());
		Assert.assertEquals(
			newAnnouncementsEntry,
			announcementsEntries.get(newAnnouncementsEntry.getPrimaryKey()));
	}

	protected AnnouncementsEntry addAnnouncementsEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AnnouncementsEntry announcementsEntry = _persistence.create(pk);

		announcementsEntry.setMvccVersion(RandomTestUtil.nextLong());

		announcementsEntry.setCtCollectionId(RandomTestUtil.nextLong());

		announcementsEntry.setUuid(RandomTestUtil.randomString());

		announcementsEntry.setCompanyId(RandomTestUtil.nextLong());

		announcementsEntry.setUserId(RandomTestUtil.nextLong());

		announcementsEntry.setUserName(RandomTestUtil.randomString());

		announcementsEntry.setCreateDate(RandomTestUtil.nextDate());

		announcementsEntry.setModifiedDate(RandomTestUtil.nextDate());

		announcementsEntry.setClassNameId(RandomTestUtil.nextLong());

		announcementsEntry.setClassPK(RandomTestUtil.nextLong());

		announcementsEntry.setTitle(RandomTestUtil.randomString());

		announcementsEntry.setContent(RandomTestUtil.randomString());

		announcementsEntry.setUrl(RandomTestUtil.randomString());

		announcementsEntry.setType(RandomTestUtil.randomString());

		announcementsEntry.setDisplayDate(RandomTestUtil.nextDate());

		announcementsEntry.setExpirationDate(RandomTestUtil.nextDate());

		announcementsEntry.setPriority(RandomTestUtil.nextInt());

		announcementsEntry.setAlert(RandomTestUtil.randomBoolean());

		_announcementsEntries.add(_persistence.update(announcementsEntry));

		return announcementsEntry;
	}

	private List<AnnouncementsEntry> _announcementsEntries =
		new ArrayList<AnnouncementsEntry>();
	private AnnouncementsEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}