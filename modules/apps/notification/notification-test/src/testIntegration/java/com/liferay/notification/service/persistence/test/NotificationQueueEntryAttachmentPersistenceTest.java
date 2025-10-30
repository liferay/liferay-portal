/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.exception.NoSuchNotificationQueueEntryAttachmentException;
import com.liferay.notification.model.NotificationQueueEntryAttachment;
import com.liferay.notification.service.persistence.NotificationQueueEntryAttachmentPersistence;
import com.liferay.notification.service.persistence.NotificationQueueEntryAttachmentUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class NotificationQueueEntryAttachmentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.notification.service"));

	@Before
	public void setUp() {
		_persistence = NotificationQueueEntryAttachmentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotificationQueueEntryAttachment> iterator =
			_notificationQueueEntryAttachments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntryAttachment notificationQueueEntryAttachment =
			_persistence.create(pk);

		Assert.assertNotNull(notificationQueueEntryAttachment);

		Assert.assertEquals(
			notificationQueueEntryAttachment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment =
			addNotificationQueueEntryAttachment();

		_persistence.remove(newNotificationQueueEntryAttachment);

		NotificationQueueEntryAttachment
			existingNotificationQueueEntryAttachment =
				_persistence.fetchByPrimaryKey(
					newNotificationQueueEntryAttachment.getPrimaryKey());

		Assert.assertNull(existingNotificationQueueEntryAttachment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotificationQueueEntryAttachment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment =
			_persistence.create(pk);

		newNotificationQueueEntryAttachment.setMvccVersion(
			RandomTestUtil.nextLong());

		newNotificationQueueEntryAttachment.setCompanyId(
			RandomTestUtil.nextLong());

		newNotificationQueueEntryAttachment.setFileEntryId(
			RandomTestUtil.nextLong());

		newNotificationQueueEntryAttachment.setNotificationQueueEntryId(
			RandomTestUtil.nextLong());

		_notificationQueueEntryAttachments.add(
			_persistence.update(newNotificationQueueEntryAttachment));

		NotificationQueueEntryAttachment
			existingNotificationQueueEntryAttachment =
				_persistence.findByPrimaryKey(
					newNotificationQueueEntryAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationQueueEntryAttachment.getMvccVersion(),
			newNotificationQueueEntryAttachment.getMvccVersion());
		Assert.assertEquals(
			existingNotificationQueueEntryAttachment.
				getNotificationQueueEntryAttachmentId(),
			newNotificationQueueEntryAttachment.
				getNotificationQueueEntryAttachmentId());
		Assert.assertEquals(
			existingNotificationQueueEntryAttachment.getCompanyId(),
			newNotificationQueueEntryAttachment.getCompanyId());
		Assert.assertEquals(
			existingNotificationQueueEntryAttachment.getFileEntryId(),
			newNotificationQueueEntryAttachment.getFileEntryId());
		Assert.assertEquals(
			existingNotificationQueueEntryAttachment.
				getNotificationQueueEntryId(),
			newNotificationQueueEntryAttachment.getNotificationQueueEntryId());
	}

	@Test
	public void testCountByNotificationQueueEntryId() throws Exception {
		_persistence.countByNotificationQueueEntryId(RandomTestUtil.nextLong());

		_persistence.countByNotificationQueueEntryId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment =
			addNotificationQueueEntryAttachment();

		NotificationQueueEntryAttachment
			existingNotificationQueueEntryAttachment =
				_persistence.findByPrimaryKey(
					newNotificationQueueEntryAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationQueueEntryAttachment,
			newNotificationQueueEntryAttachment);
	}

	@Test(expected = NoSuchNotificationQueueEntryAttachmentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotificationQueueEntryAttachment>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"NQueueEntryAttachment", "mvccVersion", true,
			"notificationQueueEntryAttachmentId", true, "companyId", true,
			"fileEntryId", true, "notificationQueueEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment =
			addNotificationQueueEntryAttachment();

		NotificationQueueEntryAttachment
			existingNotificationQueueEntryAttachment =
				_persistence.fetchByPrimaryKey(
					newNotificationQueueEntryAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationQueueEntryAttachment,
			newNotificationQueueEntryAttachment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntryAttachment
			missingNotificationQueueEntryAttachment =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNotificationQueueEntryAttachment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment1 =
			addNotificationQueueEntryAttachment();
		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment2 =
			addNotificationQueueEntryAttachment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationQueueEntryAttachment1.getPrimaryKey());
		primaryKeys.add(newNotificationQueueEntryAttachment2.getPrimaryKey());

		Map<Serializable, NotificationQueueEntryAttachment>
			notificationQueueEntryAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, notificationQueueEntryAttachments.size());
		Assert.assertEquals(
			newNotificationQueueEntryAttachment1,
			notificationQueueEntryAttachments.get(
				newNotificationQueueEntryAttachment1.getPrimaryKey()));
		Assert.assertEquals(
			newNotificationQueueEntryAttachment2,
			notificationQueueEntryAttachments.get(
				newNotificationQueueEntryAttachment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotificationQueueEntryAttachment>
			notificationQueueEntryAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(notificationQueueEntryAttachments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment =
			addNotificationQueueEntryAttachment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationQueueEntryAttachment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotificationQueueEntryAttachment>
			notificationQueueEntryAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, notificationQueueEntryAttachments.size());
		Assert.assertEquals(
			newNotificationQueueEntryAttachment,
			notificationQueueEntryAttachments.get(
				newNotificationQueueEntryAttachment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotificationQueueEntryAttachment>
			notificationQueueEntryAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(notificationQueueEntryAttachments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotificationQueueEntryAttachment newNotificationQueueEntryAttachment =
			addNotificationQueueEntryAttachment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationQueueEntryAttachment.getPrimaryKey());

		Map<Serializable, NotificationQueueEntryAttachment>
			notificationQueueEntryAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, notificationQueueEntryAttachments.size());
		Assert.assertEquals(
			newNotificationQueueEntryAttachment,
			notificationQueueEntryAttachments.get(
				newNotificationQueueEntryAttachment.getPrimaryKey()));
	}

	protected NotificationQueueEntryAttachment
			addNotificationQueueEntryAttachment()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		NotificationQueueEntryAttachment notificationQueueEntryAttachment =
			_persistence.create(pk);

		notificationQueueEntryAttachment.setMvccVersion(
			RandomTestUtil.nextLong());

		notificationQueueEntryAttachment.setCompanyId(
			RandomTestUtil.nextLong());

		notificationQueueEntryAttachment.setFileEntryId(
			RandomTestUtil.nextLong());

		notificationQueueEntryAttachment.setNotificationQueueEntryId(
			RandomTestUtil.nextLong());

		_notificationQueueEntryAttachments.add(
			_persistence.update(notificationQueueEntryAttachment));

		return notificationQueueEntryAttachment;
	}

	private List<NotificationQueueEntryAttachment>
		_notificationQueueEntryAttachments =
			new ArrayList<NotificationQueueEntryAttachment>();
	private NotificationQueueEntryAttachmentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}