/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.notification.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.notification.exception.NoSuchNotificationRecipientSettingException;
import com.liferay.notification.model.NotificationRecipientSetting;
import com.liferay.notification.service.persistence.NotificationRecipientSettingPersistence;
import com.liferay.notification.service.persistence.NotificationRecipientSettingUtil;
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
public class NotificationRecipientSettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.notification.service"));

	@Before
	public void setUp() {
		_persistence = NotificationRecipientSettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<NotificationRecipientSetting> iterator =
			_notificationRecipientSettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationRecipientSetting notificationRecipientSetting =
			_persistence.create(pk);

		Assert.assertNotNull(notificationRecipientSetting);

		Assert.assertEquals(notificationRecipientSetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		NotificationRecipientSetting newNotificationRecipientSetting =
			addNotificationRecipientSetting();

		_persistence.remove(newNotificationRecipientSetting);

		NotificationRecipientSetting existingNotificationRecipientSetting =
			_persistence.fetchByPrimaryKey(
				newNotificationRecipientSetting.getPrimaryKey());

		Assert.assertNull(existingNotificationRecipientSetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addNotificationRecipientSetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationRecipientSetting newNotificationRecipientSetting =
			_persistence.create(pk);

		newNotificationRecipientSetting.setMvccVersion(
			RandomTestUtil.nextLong());

		newNotificationRecipientSetting.setUuid(RandomTestUtil.randomString());

		newNotificationRecipientSetting.setCompanyId(RandomTestUtil.nextLong());

		newNotificationRecipientSetting.setUserId(RandomTestUtil.nextLong());

		newNotificationRecipientSetting.setUserName(
			RandomTestUtil.randomString());

		newNotificationRecipientSetting.setCreateDate(
			RandomTestUtil.nextDate());

		newNotificationRecipientSetting.setModifiedDate(
			RandomTestUtil.nextDate());

		newNotificationRecipientSetting.setNotificationRecipientId(
			RandomTestUtil.nextLong());

		newNotificationRecipientSetting.setName(RandomTestUtil.randomString());

		newNotificationRecipientSetting.setValue(RandomTestUtil.randomString());

		_notificationRecipientSettings.add(
			_persistence.update(newNotificationRecipientSetting));

		NotificationRecipientSetting existingNotificationRecipientSetting =
			_persistence.findByPrimaryKey(
				newNotificationRecipientSetting.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationRecipientSetting.getMvccVersion(),
			newNotificationRecipientSetting.getMvccVersion());
		Assert.assertEquals(
			existingNotificationRecipientSetting.getUuid(),
			newNotificationRecipientSetting.getUuid());
		Assert.assertEquals(
			existingNotificationRecipientSetting.
				getNotificationRecipientSettingId(),
			newNotificationRecipientSetting.
				getNotificationRecipientSettingId());
		Assert.assertEquals(
			existingNotificationRecipientSetting.getCompanyId(),
			newNotificationRecipientSetting.getCompanyId());
		Assert.assertEquals(
			existingNotificationRecipientSetting.getUserId(),
			newNotificationRecipientSetting.getUserId());
		Assert.assertEquals(
			existingNotificationRecipientSetting.getUserName(),
			newNotificationRecipientSetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationRecipientSetting.getCreateDate()),
			Time.getShortTimestamp(
				newNotificationRecipientSetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingNotificationRecipientSetting.getModifiedDate()),
			Time.getShortTimestamp(
				newNotificationRecipientSetting.getModifiedDate()));
		Assert.assertEquals(
			existingNotificationRecipientSetting.getNotificationRecipientId(),
			newNotificationRecipientSetting.getNotificationRecipientId());
		Assert.assertEquals(
			existingNotificationRecipientSetting.getName(),
			newNotificationRecipientSetting.getName());
		Assert.assertEquals(
			existingNotificationRecipientSetting.getValue(),
			newNotificationRecipientSetting.getValue());
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
	public void testCountByNotificationRecipientId() throws Exception {
		_persistence.countByNotificationRecipientId(RandomTestUtil.nextLong());

		_persistence.countByNotificationRecipientId(0L);
	}

	@Test
	public void testCountByNRI_N() throws Exception {
		_persistence.countByNRI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByNRI_N(0L, "null");

		_persistence.countByNRI_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		NotificationRecipientSetting newNotificationRecipientSetting =
			addNotificationRecipientSetting();

		NotificationRecipientSetting existingNotificationRecipientSetting =
			_persistence.findByPrimaryKey(
				newNotificationRecipientSetting.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationRecipientSetting,
			newNotificationRecipientSetting);
	}

	@Test(expected = NoSuchNotificationRecipientSettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<NotificationRecipientSetting>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"NotificationRecipientSetting", "mvccVersion", true, "uuid", true,
			"notificationRecipientSettingId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"notificationRecipientId", true, "name", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		NotificationRecipientSetting newNotificationRecipientSetting =
			addNotificationRecipientSetting();

		NotificationRecipientSetting existingNotificationRecipientSetting =
			_persistence.fetchByPrimaryKey(
				newNotificationRecipientSetting.getPrimaryKey());

		Assert.assertEquals(
			existingNotificationRecipientSetting,
			newNotificationRecipientSetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		NotificationRecipientSetting missingNotificationRecipientSetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingNotificationRecipientSetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		NotificationRecipientSetting newNotificationRecipientSetting1 =
			addNotificationRecipientSetting();
		NotificationRecipientSetting newNotificationRecipientSetting2 =
			addNotificationRecipientSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationRecipientSetting1.getPrimaryKey());
		primaryKeys.add(newNotificationRecipientSetting2.getPrimaryKey());

		Map<Serializable, NotificationRecipientSetting>
			notificationRecipientSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, notificationRecipientSettings.size());
		Assert.assertEquals(
			newNotificationRecipientSetting1,
			notificationRecipientSettings.get(
				newNotificationRecipientSetting1.getPrimaryKey()));
		Assert.assertEquals(
			newNotificationRecipientSetting2,
			notificationRecipientSettings.get(
				newNotificationRecipientSetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, NotificationRecipientSetting>
			notificationRecipientSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(notificationRecipientSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		NotificationRecipientSetting newNotificationRecipientSetting =
			addNotificationRecipientSetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationRecipientSetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, NotificationRecipientSetting>
			notificationRecipientSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, notificationRecipientSettings.size());
		Assert.assertEquals(
			newNotificationRecipientSetting,
			notificationRecipientSettings.get(
				newNotificationRecipientSetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, NotificationRecipientSetting>
			notificationRecipientSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(notificationRecipientSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		NotificationRecipientSetting newNotificationRecipientSetting =
			addNotificationRecipientSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newNotificationRecipientSetting.getPrimaryKey());

		Map<Serializable, NotificationRecipientSetting>
			notificationRecipientSettings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, notificationRecipientSettings.size());
		Assert.assertEquals(
			newNotificationRecipientSetting,
			notificationRecipientSettings.get(
				newNotificationRecipientSetting.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		NotificationRecipientSetting newNotificationRecipientSetting =
			addNotificationRecipientSetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newNotificationRecipientSetting.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		NotificationRecipientSetting notificationRecipientSetting) {

		Assert.assertEquals(
			Long.valueOf(
				notificationRecipientSetting.getNotificationRecipientId()),
			ReflectionTestUtil.<Long>invoke(
				notificationRecipientSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "notificationRecipientId"));
		Assert.assertEquals(
			notificationRecipientSetting.getName(),
			ReflectionTestUtil.invoke(
				notificationRecipientSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected NotificationRecipientSetting addNotificationRecipientSetting()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		NotificationRecipientSetting notificationRecipientSetting =
			_persistence.create(pk);

		notificationRecipientSetting.setMvccVersion(RandomTestUtil.nextLong());

		notificationRecipientSetting.setUuid(RandomTestUtil.randomString());

		notificationRecipientSetting.setCompanyId(RandomTestUtil.nextLong());

		notificationRecipientSetting.setUserId(RandomTestUtil.nextLong());

		notificationRecipientSetting.setUserName(RandomTestUtil.randomString());

		notificationRecipientSetting.setCreateDate(RandomTestUtil.nextDate());

		notificationRecipientSetting.setModifiedDate(RandomTestUtil.nextDate());

		notificationRecipientSetting.setNotificationRecipientId(
			RandomTestUtil.nextLong());

		notificationRecipientSetting.setName(RandomTestUtil.randomString());

		notificationRecipientSetting.setValue(RandomTestUtil.randomString());

		_notificationRecipientSettings.add(
			_persistence.update(notificationRecipientSetting));

		return notificationRecipientSetting;
	}

	private List<NotificationRecipientSetting> _notificationRecipientSettings =
		new ArrayList<NotificationRecipientSetting>();
	private NotificationRecipientSettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}