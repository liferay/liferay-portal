/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.exception.NoSuchNotificationRecipientException;
import com.liferay.notification.model.NotificationRecipient;
import com.liferay.notification.service.persistence.NotificationRecipientPersistence;
import com.liferay.notification.service.persistence.NotificationRecipientUtil;
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
public class NotificationRecipientPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.notification.service"));

	@Before
	public void setUp() {
		_persistence = NotificationRecipientUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotificationRecipient> iterator =
			_notificationRecipients.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationRecipient notificationRecipient = _persistence.create(pk);

		Assert.assertNotNull(notificationRecipient);

		Assert.assertEquals(notificationRecipient.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotificationRecipient newNotificationRecipient =
			addNotificationRecipient();

		_persistence.remove(newNotificationRecipient);

		NotificationRecipient existingNotificationRecipient =
			_persistence.fetchByPrimaryKey(
				newNotificationRecipient.getPrimaryKey());

		Assert.assertNull(existingNotificationRecipient);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotificationRecipient();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationRecipient newNotificationRecipient = _persistence.create(
			pk);

		newNotificationRecipient.setMvccVersion(RandomTestUtil.nextLong());

		newNotificationRecipient.setUuid(RandomTestUtil.randomString());

		newNotificationRecipient.setCompanyId(RandomTestUtil.nextLong());

		newNotificationRecipient.setUserId(RandomTestUtil.nextLong());

		newNotificationRecipient.setUserName(RandomTestUtil.randomString());

		newNotificationRecipient.setCreateDate(RandomTestUtil.nextDate());

		newNotificationRecipient.setModifiedDate(RandomTestUtil.nextDate());

		newNotificationRecipient.setClassNameId(RandomTestUtil.nextLong());

		newNotificationRecipient.setClassPK(RandomTestUtil.nextLong());

		_notificationRecipients.add(
			_persistence.update(newNotificationRecipient));

		NotificationRecipient existingNotificationRecipient =
			_persistence.findByPrimaryKey(
				newNotificationRecipient.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationRecipient.getMvccVersion(),
			newNotificationRecipient.getMvccVersion());
		Assert.assertEquals(
			existingNotificationRecipient.getUuid(),
			newNotificationRecipient.getUuid());
		Assert.assertEquals(
			existingNotificationRecipient.getNotificationRecipientId(),
			newNotificationRecipient.getNotificationRecipientId());
		Assert.assertEquals(
			existingNotificationRecipient.getCompanyId(),
			newNotificationRecipient.getCompanyId());
		Assert.assertEquals(
			existingNotificationRecipient.getUserId(),
			newNotificationRecipient.getUserId());
		Assert.assertEquals(
			existingNotificationRecipient.getUserName(),
			newNotificationRecipient.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationRecipient.getCreateDate()),
			Time.getShortTimestamp(newNotificationRecipient.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationRecipient.getModifiedDate()),
			Time.getShortTimestamp(newNotificationRecipient.getModifiedDate()));
		Assert.assertEquals(
			existingNotificationRecipient.getClassNameId(),
			newNotificationRecipient.getClassNameId());
		Assert.assertEquals(
			existingNotificationRecipient.getClassPK(),
			newNotificationRecipient.getClassPK());
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
	public void testCountByClassPK() throws Exception {
		_persistence.countByClassPK(RandomTestUtil.nextLong());

		_persistence.countByClassPK(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NotificationRecipient newNotificationRecipient =
			addNotificationRecipient();

		NotificationRecipient existingNotificationRecipient =
			_persistence.findByPrimaryKey(
				newNotificationRecipient.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationRecipient, newNotificationRecipient);
	}

	@Test(expected = NoSuchNotificationRecipientException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotificationRecipient> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"NotificationRecipient", "mvccVersion", true, "uuid", true,
			"notificationRecipientId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotificationRecipient newNotificationRecipient =
			addNotificationRecipient();

		NotificationRecipient existingNotificationRecipient =
			_persistence.fetchByPrimaryKey(
				newNotificationRecipient.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationRecipient, newNotificationRecipient);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationRecipient missingNotificationRecipient =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNotificationRecipient);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotificationRecipient newNotificationRecipient1 =
			addNotificationRecipient();
		NotificationRecipient newNotificationRecipient2 =
			addNotificationRecipient();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationRecipient1.getPrimaryKey());
		primaryKeys.add(newNotificationRecipient2.getPrimaryKey());

		Map<Serializable, NotificationRecipient> notificationRecipients =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, notificationRecipients.size());
		Assert.assertEquals(
			newNotificationRecipient1,
			notificationRecipients.get(
				newNotificationRecipient1.getPrimaryKey()));
		Assert.assertEquals(
			newNotificationRecipient2,
			notificationRecipients.get(
				newNotificationRecipient2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotificationRecipient> notificationRecipients =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notificationRecipients.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotificationRecipient newNotificationRecipient =
			addNotificationRecipient();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationRecipient.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotificationRecipient> notificationRecipients =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notificationRecipients.size());
		Assert.assertEquals(
			newNotificationRecipient,
			notificationRecipients.get(
				newNotificationRecipient.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotificationRecipient> notificationRecipients =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(notificationRecipients.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotificationRecipient newNotificationRecipient =
			addNotificationRecipient();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationRecipient.getPrimaryKey());

		Map<Serializable, NotificationRecipient> notificationRecipients =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, notificationRecipients.size());
		Assert.assertEquals(
			newNotificationRecipient,
			notificationRecipients.get(
				newNotificationRecipient.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		NotificationRecipient newNotificationRecipient =
			addNotificationRecipient();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newNotificationRecipient.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		NotificationRecipient notificationRecipient) {

		Assert.assertEquals(
			Long.valueOf(notificationRecipient.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				notificationRecipient, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected NotificationRecipient addNotificationRecipient()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		NotificationRecipient notificationRecipient = _persistence.create(pk);

		notificationRecipient.setMvccVersion(RandomTestUtil.nextLong());

		notificationRecipient.setUuid(RandomTestUtil.randomString());

		notificationRecipient.setCompanyId(RandomTestUtil.nextLong());

		notificationRecipient.setUserId(RandomTestUtil.nextLong());

		notificationRecipient.setUserName(RandomTestUtil.randomString());

		notificationRecipient.setCreateDate(RandomTestUtil.nextDate());

		notificationRecipient.setModifiedDate(RandomTestUtil.nextDate());

		notificationRecipient.setClassNameId(RandomTestUtil.nextLong());

		notificationRecipient.setClassPK(RandomTestUtil.nextLong());

		_notificationRecipients.add(_persistence.update(notificationRecipient));

		return notificationRecipient;
	}

	private List<NotificationRecipient> _notificationRecipients =
		new ArrayList<NotificationRecipient>();
	private NotificationRecipientPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}