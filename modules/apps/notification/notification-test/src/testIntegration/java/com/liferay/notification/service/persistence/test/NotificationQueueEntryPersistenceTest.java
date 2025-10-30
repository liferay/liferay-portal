/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.exception.NoSuchNotificationQueueEntryException;
import com.liferay.notification.model.NotificationQueueEntry;
import com.liferay.notification.service.persistence.NotificationQueueEntryPersistence;
import com.liferay.notification.service.persistence.NotificationQueueEntryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class NotificationQueueEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.notification.service"));

	@Before
	public void setUp() {
		_persistence = NotificationQueueEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotificationQueueEntry> iterator =
			_notificationQueueEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntry notificationQueueEntry = _persistence.create(pk);

		Assert.assertNotNull(notificationQueueEntry);

		Assert.assertEquals(notificationQueueEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotificationQueueEntry newNotificationQueueEntry =
			addNotificationQueueEntry();

		_persistence.remove(newNotificationQueueEntry);

		NotificationQueueEntry existingNotificationQueueEntry =
			_persistence.fetchByPrimaryKey(
				newNotificationQueueEntry.getPrimaryKey());

		Assert.assertNull(existingNotificationQueueEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotificationQueueEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntry newNotificationQueueEntry = _persistence.create(
			pk);

		newNotificationQueueEntry.setMvccVersion(RandomTestUtil.nextLong());

		newNotificationQueueEntry.setCompanyId(RandomTestUtil.nextLong());

		newNotificationQueueEntry.setUserId(RandomTestUtil.nextLong());

		newNotificationQueueEntry.setUserName(RandomTestUtil.randomString());

		newNotificationQueueEntry.setCreateDate(RandomTestUtil.nextDate());

		newNotificationQueueEntry.setModifiedDate(RandomTestUtil.nextDate());

		newNotificationQueueEntry.setNotificationTemplateId(
			RandomTestUtil.nextLong());

		newNotificationQueueEntry.setBody(RandomTestUtil.randomString());

		newNotificationQueueEntry.setClassNameId(RandomTestUtil.nextLong());

		newNotificationQueueEntry.setClassPK(RandomTestUtil.nextLong());

		newNotificationQueueEntry.setPriority(RandomTestUtil.nextDouble());

		newNotificationQueueEntry.setSentDate(RandomTestUtil.nextDate());

		newNotificationQueueEntry.setSubject(RandomTestUtil.randomString());

		newNotificationQueueEntry.setType(RandomTestUtil.randomString());

		newNotificationQueueEntry.setStatus(RandomTestUtil.nextInt());

		_notificationQueueEntries.add(
			_persistence.update(newNotificationQueueEntry));

		NotificationQueueEntry existingNotificationQueueEntry =
			_persistence.findByPrimaryKey(
				newNotificationQueueEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationQueueEntry.getMvccVersion(),
			newNotificationQueueEntry.getMvccVersion());
		Assert.assertEquals(
			existingNotificationQueueEntry.getNotificationQueueEntryId(),
			newNotificationQueueEntry.getNotificationQueueEntryId());
		Assert.assertEquals(
			existingNotificationQueueEntry.getCompanyId(),
			newNotificationQueueEntry.getCompanyId());
		Assert.assertEquals(
			existingNotificationQueueEntry.getUserId(),
			newNotificationQueueEntry.getUserId());
		Assert.assertEquals(
			existingNotificationQueueEntry.getUserName(),
			newNotificationQueueEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationQueueEntry.getCreateDate()),
			Time.getShortTimestamp(newNotificationQueueEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationQueueEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newNotificationQueueEntry.getModifiedDate()));
		Assert.assertEquals(
			existingNotificationQueueEntry.getNotificationTemplateId(),
			newNotificationQueueEntry.getNotificationTemplateId());
		Assert.assertEquals(
			existingNotificationQueueEntry.getBody(),
			newNotificationQueueEntry.getBody());
		Assert.assertEquals(
			existingNotificationQueueEntry.getClassNameId(),
			newNotificationQueueEntry.getClassNameId());
		Assert.assertEquals(
			existingNotificationQueueEntry.getClassPK(),
			newNotificationQueueEntry.getClassPK());
		AssertUtils.assertEquals(
			existingNotificationQueueEntry.getPriority(),
			newNotificationQueueEntry.getPriority());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationQueueEntry.getSentDate()),
			Time.getShortTimestamp(newNotificationQueueEntry.getSentDate()));
		Assert.assertEquals(
			existingNotificationQueueEntry.getSubject(),
			newNotificationQueueEntry.getSubject());
		Assert.assertEquals(
			existingNotificationQueueEntry.getType(),
			newNotificationQueueEntry.getType());
		Assert.assertEquals(
			existingNotificationQueueEntry.getStatus(),
			newNotificationQueueEntry.getStatus());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByNotificationTemplateId() throws Exception {
		_persistence.countByNotificationTemplateId(RandomTestUtil.nextLong());

		_persistence.countByNotificationTemplateId(0L);
	}

	@Test
	public void testCountByLtSentDate() throws Exception {
		_persistence.countByLtSentDate(RandomTestUtil.nextDate());

		_persistence.countByLtSentDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByT_S() throws Exception {
		_persistence.countByT_S("", RandomTestUtil.nextInt());

		_persistence.countByT_S("null", 0);

		_persistence.countByT_S((String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NotificationQueueEntry newNotificationQueueEntry =
			addNotificationQueueEntry();

		NotificationQueueEntry existingNotificationQueueEntry =
			_persistence.findByPrimaryKey(
				newNotificationQueueEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationQueueEntry, newNotificationQueueEntry);
	}

	@Test(expected = NoSuchNotificationQueueEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotificationQueueEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"NotificationQueueEntry", "mvccVersion", true,
			"notificationQueueEntryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"notificationTemplateId", true, "classNameId", true, "classPK",
			true, "priority", true, "sentDate", true, "type", true, "status",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotificationQueueEntry newNotificationQueueEntry =
			addNotificationQueueEntry();

		NotificationQueueEntry existingNotificationQueueEntry =
			_persistence.fetchByPrimaryKey(
				newNotificationQueueEntry.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationQueueEntry, newNotificationQueueEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntry missingNotificationQueueEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNotificationQueueEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotificationQueueEntry newNotificationQueueEntry1 =
			addNotificationQueueEntry();
		NotificationQueueEntry newNotificationQueueEntry2 =
			addNotificationQueueEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationQueueEntry1.getPrimaryKey());
		primaryKeys.add(newNotificationQueueEntry2.getPrimaryKey());

		Map<Serializable, NotificationQueueEntry> notificationQueueEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, notificationQueueEntries.size());
		Assert.assertEquals(
			newNotificationQueueEntry1,
			notificationQueueEntries.get(
				newNotificationQueueEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newNotificationQueueEntry2,
			notificationQueueEntries.get(
				newNotificationQueueEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotificationQueueEntry> notificationQueueEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notificationQueueEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotificationQueueEntry newNotificationQueueEntry =
			addNotificationQueueEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationQueueEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotificationQueueEntry> notificationQueueEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notificationQueueEntries.size());
		Assert.assertEquals(
			newNotificationQueueEntry,
			notificationQueueEntries.get(
				newNotificationQueueEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotificationQueueEntry> notificationQueueEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notificationQueueEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotificationQueueEntry newNotificationQueueEntry =
			addNotificationQueueEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationQueueEntry.getPrimaryKey());

		Map<Serializable, NotificationQueueEntry> notificationQueueEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notificationQueueEntries.size());
		Assert.assertEquals(
			newNotificationQueueEntry,
			notificationQueueEntries.get(
				newNotificationQueueEntry.getPrimaryKey()));
	}

	protected NotificationQueueEntry addNotificationQueueEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntry notificationQueueEntry = _persistence.create(pk);

		notificationQueueEntry.setMvccVersion(RandomTestUtil.nextLong());

		notificationQueueEntry.setCompanyId(RandomTestUtil.nextLong());

		notificationQueueEntry.setUserId(RandomTestUtil.nextLong());

		notificationQueueEntry.setUserName(RandomTestUtil.randomString());

		notificationQueueEntry.setCreateDate(RandomTestUtil.nextDate());

		notificationQueueEntry.setModifiedDate(RandomTestUtil.nextDate());

		notificationQueueEntry.setNotificationTemplateId(
			RandomTestUtil.nextLong());

		notificationQueueEntry.setBody(RandomTestUtil.randomString());

		notificationQueueEntry.setClassNameId(RandomTestUtil.nextLong());

		notificationQueueEntry.setClassPK(RandomTestUtil.nextLong());

		notificationQueueEntry.setPriority(RandomTestUtil.nextDouble());

		notificationQueueEntry.setSentDate(RandomTestUtil.nextDate());

		notificationQueueEntry.setSubject(RandomTestUtil.randomString());

		notificationQueueEntry.setType(RandomTestUtil.randomString());

		notificationQueueEntry.setStatus(RandomTestUtil.nextInt());

		_notificationQueueEntries.add(
			_persistence.update(notificationQueueEntry));

		return notificationQueueEntry;
	}

	private List<NotificationQueueEntry> _notificationQueueEntries =
		new ArrayList<NotificationQueueEntry>();
	private NotificationQueueEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}