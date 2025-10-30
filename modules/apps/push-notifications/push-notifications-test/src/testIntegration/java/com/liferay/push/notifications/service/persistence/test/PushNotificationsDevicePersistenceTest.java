/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.push.notifications.service.persistence.test;

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
import com.liferay.push.notifications.exception.NoSuchDeviceException;
import com.liferay.push.notifications.model.PushNotificationsDevice;
import com.liferay.push.notifications.service.persistence.PushNotificationsDevicePersistence;
import com.liferay.push.notifications.service.persistence.PushNotificationsDeviceUtil;

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
public class PushNotificationsDevicePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.push.notifications.service"));

	@Before
	public void setUp() {
		_persistence = PushNotificationsDeviceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PushNotificationsDevice> iterator =
			_pushNotificationsDevices.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PushNotificationsDevice pushNotificationsDevice = _persistence.create(
			pk);

		Assert.assertNotNull(pushNotificationsDevice);

		Assert.assertEquals(pushNotificationsDevice.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PushNotificationsDevice newPushNotificationsDevice =
			addPushNotificationsDevice();

		_persistence.remove(newPushNotificationsDevice);

		PushNotificationsDevice existingPushNotificationsDevice =
			_persistence.fetchByPrimaryKey(
				newPushNotificationsDevice.getPrimaryKey());

		Assert.assertNull(existingPushNotificationsDevice);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPushNotificationsDevice();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PushNotificationsDevice newPushNotificationsDevice =
			_persistence.create(pk);

		newPushNotificationsDevice.setCompanyId(RandomTestUtil.nextLong());

		newPushNotificationsDevice.setUserId(RandomTestUtil.nextLong());

		newPushNotificationsDevice.setCreateDate(RandomTestUtil.nextDate());

		newPushNotificationsDevice.setPlatform(RandomTestUtil.randomString());

		newPushNotificationsDevice.setToken(RandomTestUtil.randomString());

		_pushNotificationsDevices.add(
			_persistence.update(newPushNotificationsDevice));

		PushNotificationsDevice existingPushNotificationsDevice =
			_persistence.findByPrimaryKey(
				newPushNotificationsDevice.getPrimaryKey());

		Assert.assertEquals(
			existingPushNotificationsDevice.getPushNotificationsDeviceId(),
			newPushNotificationsDevice.getPushNotificationsDeviceId());
		Assert.assertEquals(
			existingPushNotificationsDevice.getCompanyId(),
			newPushNotificationsDevice.getCompanyId());
		Assert.assertEquals(
			existingPushNotificationsDevice.getUserId(),
			newPushNotificationsDevice.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingPushNotificationsDevice.getCreateDate()),
			Time.getShortTimestamp(newPushNotificationsDevice.getCreateDate()));
		Assert.assertEquals(
			existingPushNotificationsDevice.getPlatform(),
			newPushNotificationsDevice.getPlatform());
		Assert.assertEquals(
			existingPushNotificationsDevice.getToken(),
			newPushNotificationsDevice.getToken());
	}

	@Test
	public void testCountByToken() throws Exception {
		_persistence.countByToken("");

		_persistence.countByToken("null");

		_persistence.countByToken((String)null);
	}

	@Test
	public void testCountByU_P() throws Exception {
		_persistence.countByU_P(RandomTestUtil.nextLong(), "");

		_persistence.countByU_P(0L, "null");

		_persistence.countByU_P(0L, (String)null);
	}

	@Test
	public void testCountByU_PArrayable() throws Exception {
		_persistence.countByU_P(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PushNotificationsDevice newPushNotificationsDevice =
			addPushNotificationsDevice();

		PushNotificationsDevice existingPushNotificationsDevice =
			_persistence.findByPrimaryKey(
				newPushNotificationsDevice.getPrimaryKey());

		Assert.assertEquals(
			existingPushNotificationsDevice, newPushNotificationsDevice);
	}

	@Test(expected = NoSuchDeviceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PushNotificationsDevice>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"PushNotificationsDevice", "pushNotificationsDeviceId", true,
			"companyId", true, "userId", true, "createDate", true, "platform",
			true, "token", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PushNotificationsDevice newPushNotificationsDevice =
			addPushNotificationsDevice();

		PushNotificationsDevice existingPushNotificationsDevice =
			_persistence.fetchByPrimaryKey(
				newPushNotificationsDevice.getPrimaryKey());

		Assert.assertEquals(
			existingPushNotificationsDevice, newPushNotificationsDevice);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PushNotificationsDevice missingPushNotificationsDevice =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPushNotificationsDevice);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PushNotificationsDevice newPushNotificationsDevice1 =
			addPushNotificationsDevice();
		PushNotificationsDevice newPushNotificationsDevice2 =
			addPushNotificationsDevice();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPushNotificationsDevice1.getPrimaryKey());
		primaryKeys.add(newPushNotificationsDevice2.getPrimaryKey());

		Map<Serializable, PushNotificationsDevice> pushNotificationsDevices =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, pushNotificationsDevices.size());
		Assert.assertEquals(
			newPushNotificationsDevice1,
			pushNotificationsDevices.get(
				newPushNotificationsDevice1.getPrimaryKey()));
		Assert.assertEquals(
			newPushNotificationsDevice2,
			pushNotificationsDevices.get(
				newPushNotificationsDevice2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PushNotificationsDevice> pushNotificationsDevices =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(pushNotificationsDevices.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PushNotificationsDevice newPushNotificationsDevice =
			addPushNotificationsDevice();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPushNotificationsDevice.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PushNotificationsDevice> pushNotificationsDevices =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, pushNotificationsDevices.size());
		Assert.assertEquals(
			newPushNotificationsDevice,
			pushNotificationsDevices.get(
				newPushNotificationsDevice.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PushNotificationsDevice> pushNotificationsDevices =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(pushNotificationsDevices.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PushNotificationsDevice newPushNotificationsDevice =
			addPushNotificationsDevice();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPushNotificationsDevice.getPrimaryKey());

		Map<Serializable, PushNotificationsDevice> pushNotificationsDevices =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, pushNotificationsDevices.size());
		Assert.assertEquals(
			newPushNotificationsDevice,
			pushNotificationsDevices.get(
				newPushNotificationsDevice.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PushNotificationsDevice newPushNotificationsDevice =
			addPushNotificationsDevice();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newPushNotificationsDevice.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		PushNotificationsDevice pushNotificationsDevice) {

		Assert.assertEquals(
			pushNotificationsDevice.getToken(),
			ReflectionTestUtil.invoke(
				pushNotificationsDevice, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "token"));
	}

	protected PushNotificationsDevice addPushNotificationsDevice()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		PushNotificationsDevice pushNotificationsDevice = _persistence.create(
			pk);

		pushNotificationsDevice.setCompanyId(RandomTestUtil.nextLong());

		pushNotificationsDevice.setUserId(RandomTestUtil.nextLong());

		pushNotificationsDevice.setCreateDate(RandomTestUtil.nextDate());

		pushNotificationsDevice.setPlatform(RandomTestUtil.randomString());

		pushNotificationsDevice.setToken(RandomTestUtil.randomString());

		_pushNotificationsDevices.add(
			_persistence.update(pushNotificationsDevice));

		return pushNotificationsDevice;
	}

	private List<PushNotificationsDevice> _pushNotificationsDevices =
		new ArrayList<PushNotificationsDevice>();
	private PushNotificationsDevicePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}