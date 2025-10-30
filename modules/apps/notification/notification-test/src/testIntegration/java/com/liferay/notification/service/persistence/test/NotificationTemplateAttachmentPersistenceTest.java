/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.exception.NoSuchNotificationTemplateAttachmentException;
import com.liferay.notification.model.NotificationTemplateAttachment;
import com.liferay.notification.service.persistence.NotificationTemplateAttachmentPersistence;
import com.liferay.notification.service.persistence.NotificationTemplateAttachmentUtil;
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
public class NotificationTemplateAttachmentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.notification.service"));

	@Before
	public void setUp() {
		_persistence = NotificationTemplateAttachmentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotificationTemplateAttachment> iterator =
			_notificationTemplateAttachments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplateAttachment notificationTemplateAttachment =
			_persistence.create(pk);

		Assert.assertNotNull(notificationTemplateAttachment);

		Assert.assertEquals(notificationTemplateAttachment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotificationTemplateAttachment newNotificationTemplateAttachment =
			addNotificationTemplateAttachment();

		_persistence.remove(newNotificationTemplateAttachment);

		NotificationTemplateAttachment existingNotificationTemplateAttachment =
			_persistence.fetchByPrimaryKey(
				newNotificationTemplateAttachment.getPrimaryKey());

		Assert.assertNull(existingNotificationTemplateAttachment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotificationTemplateAttachment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplateAttachment newNotificationTemplateAttachment =
			_persistence.create(pk);

		newNotificationTemplateAttachment.setMvccVersion(
			RandomTestUtil.nextLong());

		newNotificationTemplateAttachment.setCompanyId(
			RandomTestUtil.nextLong());

		newNotificationTemplateAttachment.setNotificationTemplateId(
			RandomTestUtil.nextLong());

		newNotificationTemplateAttachment.setObjectFieldId(
			RandomTestUtil.nextLong());

		_notificationTemplateAttachments.add(
			_persistence.update(newNotificationTemplateAttachment));

		NotificationTemplateAttachment existingNotificationTemplateAttachment =
			_persistence.findByPrimaryKey(
				newNotificationTemplateAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationTemplateAttachment.getMvccVersion(),
			newNotificationTemplateAttachment.getMvccVersion());
		Assert.assertEquals(
			existingNotificationTemplateAttachment.
				getNotificationTemplateAttachmentId(),
			newNotificationTemplateAttachment.
				getNotificationTemplateAttachmentId());
		Assert.assertEquals(
			existingNotificationTemplateAttachment.getCompanyId(),
			newNotificationTemplateAttachment.getCompanyId());
		Assert.assertEquals(
			existingNotificationTemplateAttachment.getNotificationTemplateId(),
			newNotificationTemplateAttachment.getNotificationTemplateId());
		Assert.assertEquals(
			existingNotificationTemplateAttachment.getObjectFieldId(),
			newNotificationTemplateAttachment.getObjectFieldId());
	}

	@Test
	public void testCountByNotificationTemplateId() throws Exception {
		_persistence.countByNotificationTemplateId(RandomTestUtil.nextLong());

		_persistence.countByNotificationTemplateId(0L);
	}

	@Test
	public void testCountByNTI_OFI() throws Exception {
		_persistence.countByNTI_OFI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByNTI_OFI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NotificationTemplateAttachment newNotificationTemplateAttachment =
			addNotificationTemplateAttachment();

		NotificationTemplateAttachment existingNotificationTemplateAttachment =
			_persistence.findByPrimaryKey(
				newNotificationTemplateAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationTemplateAttachment,
			newNotificationTemplateAttachment);
	}

	@Test(expected = NoSuchNotificationTemplateAttachmentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotificationTemplateAttachment>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"NTemplateAttachment", "mvccVersion", true,
			"notificationTemplateAttachmentId", true, "companyId", true,
			"notificationTemplateId", true, "objectFieldId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotificationTemplateAttachment newNotificationTemplateAttachment =
			addNotificationTemplateAttachment();

		NotificationTemplateAttachment existingNotificationTemplateAttachment =
			_persistence.fetchByPrimaryKey(
				newNotificationTemplateAttachment.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationTemplateAttachment,
			newNotificationTemplateAttachment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationTemplateAttachment missingNotificationTemplateAttachment =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNotificationTemplateAttachment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotificationTemplateAttachment newNotificationTemplateAttachment1 =
			addNotificationTemplateAttachment();
		NotificationTemplateAttachment newNotificationTemplateAttachment2 =
			addNotificationTemplateAttachment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationTemplateAttachment1.getPrimaryKey());
		primaryKeys.add(newNotificationTemplateAttachment2.getPrimaryKey());

		Map<Serializable, NotificationTemplateAttachment>
			notificationTemplateAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, notificationTemplateAttachments.size());
		Assert.assertEquals(
			newNotificationTemplateAttachment1,
			notificationTemplateAttachments.get(
				newNotificationTemplateAttachment1.getPrimaryKey()));
		Assert.assertEquals(
			newNotificationTemplateAttachment2,
			notificationTemplateAttachments.get(
				newNotificationTemplateAttachment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotificationTemplateAttachment>
			notificationTemplateAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(notificationTemplateAttachments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotificationTemplateAttachment newNotificationTemplateAttachment =
			addNotificationTemplateAttachment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationTemplateAttachment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotificationTemplateAttachment>
			notificationTemplateAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, notificationTemplateAttachments.size());
		Assert.assertEquals(
			newNotificationTemplateAttachment,
			notificationTemplateAttachments.get(
				newNotificationTemplateAttachment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotificationTemplateAttachment>
			notificationTemplateAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(notificationTemplateAttachments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotificationTemplateAttachment newNotificationTemplateAttachment =
			addNotificationTemplateAttachment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationTemplateAttachment.getPrimaryKey());

		Map<Serializable, NotificationTemplateAttachment>
			notificationTemplateAttachments = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, notificationTemplateAttachments.size());
		Assert.assertEquals(
			newNotificationTemplateAttachment,
			notificationTemplateAttachments.get(
				newNotificationTemplateAttachment.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		NotificationTemplateAttachment newNotificationTemplateAttachment =
			addNotificationTemplateAttachment();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newNotificationTemplateAttachment.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		NotificationTemplateAttachment notificationTemplateAttachment) {

		Assert.assertEquals(
			Long.valueOf(
				notificationTemplateAttachment.getNotificationTemplateId()),
			ReflectionTestUtil.<Long>invoke(
				notificationTemplateAttachment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "notificationTemplateId"));
		Assert.assertEquals(
			Long.valueOf(notificationTemplateAttachment.getObjectFieldId()),
			ReflectionTestUtil.<Long>invoke(
				notificationTemplateAttachment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectFieldId"));
	}

	protected NotificationTemplateAttachment addNotificationTemplateAttachment()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		NotificationTemplateAttachment notificationTemplateAttachment =
			_persistence.create(pk);

		notificationTemplateAttachment.setMvccVersion(
			RandomTestUtil.nextLong());

		notificationTemplateAttachment.setCompanyId(RandomTestUtil.nextLong());

		notificationTemplateAttachment.setNotificationTemplateId(
			RandomTestUtil.nextLong());

		notificationTemplateAttachment.setObjectFieldId(
			RandomTestUtil.nextLong());

		_notificationTemplateAttachments.add(
			_persistence.update(notificationTemplateAttachment));

		return notificationTemplateAttachment;
	}

	private List<NotificationTemplateAttachment>
		_notificationTemplateAttachments =
			new ArrayList<NotificationTemplateAttachment>();
	private NotificationTemplateAttachmentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}