/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.NoSuchThreadFlagException;
import com.liferay.message.boards.model.MBThreadFlag;
import com.liferay.message.boards.service.persistence.MBThreadFlagPersistence;
import com.liferay.message.boards.service.persistence.MBThreadFlagUtil;
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
public class MBThreadFlagPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBThreadFlagUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBThreadFlag> iterator = _mbThreadFlags.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThreadFlag mbThreadFlag = _persistence.create(pk);

		Assert.assertNotNull(mbThreadFlag);

		Assert.assertEquals(mbThreadFlag.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBThreadFlag newMBThreadFlag = addMBThreadFlag();

		_persistence.remove(newMBThreadFlag);

		MBThreadFlag existingMBThreadFlag = _persistence.fetchByPrimaryKey(
			newMBThreadFlag.getPrimaryKey());

		Assert.assertNull(existingMBThreadFlag);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBThreadFlag();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThreadFlag newMBThreadFlag = _persistence.create(pk);

		newMBThreadFlag.setMvccVersion(RandomTestUtil.nextLong());

		newMBThreadFlag.setCtCollectionId(RandomTestUtil.nextLong());

		newMBThreadFlag.setUuid(RandomTestUtil.randomString());

		newMBThreadFlag.setGroupId(RandomTestUtil.nextLong());

		newMBThreadFlag.setCompanyId(RandomTestUtil.nextLong());

		newMBThreadFlag.setUserId(RandomTestUtil.nextLong());

		newMBThreadFlag.setUserName(RandomTestUtil.randomString());

		newMBThreadFlag.setCreateDate(RandomTestUtil.nextDate());

		newMBThreadFlag.setModifiedDate(RandomTestUtil.nextDate());

		newMBThreadFlag.setThreadId(RandomTestUtil.nextLong());

		newMBThreadFlag.setLastPublishDate(RandomTestUtil.nextDate());

		_mbThreadFlags.add(_persistence.update(newMBThreadFlag));

		MBThreadFlag existingMBThreadFlag = _persistence.findByPrimaryKey(
			newMBThreadFlag.getPrimaryKey());

		Assert.assertEquals(
			existingMBThreadFlag.getMvccVersion(),
			newMBThreadFlag.getMvccVersion());
		Assert.assertEquals(
			existingMBThreadFlag.getCtCollectionId(),
			newMBThreadFlag.getCtCollectionId());
		Assert.assertEquals(
			existingMBThreadFlag.getUuid(), newMBThreadFlag.getUuid());
		Assert.assertEquals(
			existingMBThreadFlag.getThreadFlagId(),
			newMBThreadFlag.getThreadFlagId());
		Assert.assertEquals(
			existingMBThreadFlag.getGroupId(), newMBThreadFlag.getGroupId());
		Assert.assertEquals(
			existingMBThreadFlag.getCompanyId(),
			newMBThreadFlag.getCompanyId());
		Assert.assertEquals(
			existingMBThreadFlag.getUserId(), newMBThreadFlag.getUserId());
		Assert.assertEquals(
			existingMBThreadFlag.getUserName(), newMBThreadFlag.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThreadFlag.getCreateDate()),
			Time.getShortTimestamp(newMBThreadFlag.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThreadFlag.getModifiedDate()),
			Time.getShortTimestamp(newMBThreadFlag.getModifiedDate()));
		Assert.assertEquals(
			existingMBThreadFlag.getThreadId(), newMBThreadFlag.getThreadId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThreadFlag.getLastPublishDate()),
			Time.getShortTimestamp(newMBThreadFlag.getLastPublishDate()));
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByThreadId() throws Exception {
		_persistence.countByThreadId(RandomTestUtil.nextLong());

		_persistence.countByThreadId(0L);
	}

	@Test
	public void testCountByU_T() throws Exception {
		_persistence.countByU_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_T(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBThreadFlag newMBThreadFlag = addMBThreadFlag();

		MBThreadFlag existingMBThreadFlag = _persistence.findByPrimaryKey(
			newMBThreadFlag.getPrimaryKey());

		Assert.assertEquals(existingMBThreadFlag, newMBThreadFlag);
	}

	@Test(expected = NoSuchThreadFlagException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MBThreadFlag> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBThreadFlag", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "threadFlagId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "threadId", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBThreadFlag newMBThreadFlag = addMBThreadFlag();

		MBThreadFlag existingMBThreadFlag = _persistence.fetchByPrimaryKey(
			newMBThreadFlag.getPrimaryKey());

		Assert.assertEquals(existingMBThreadFlag, newMBThreadFlag);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThreadFlag missingMBThreadFlag = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBThreadFlag);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBThreadFlag newMBThreadFlag1 = addMBThreadFlag();
		MBThreadFlag newMBThreadFlag2 = addMBThreadFlag();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThreadFlag1.getPrimaryKey());
		primaryKeys.add(newMBThreadFlag2.getPrimaryKey());

		Map<Serializable, MBThreadFlag> mbThreadFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, mbThreadFlags.size());
		Assert.assertEquals(
			newMBThreadFlag1,
			mbThreadFlags.get(newMBThreadFlag1.getPrimaryKey()));
		Assert.assertEquals(
			newMBThreadFlag2,
			mbThreadFlags.get(newMBThreadFlag2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBThreadFlag> mbThreadFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbThreadFlags.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBThreadFlag newMBThreadFlag = addMBThreadFlag();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThreadFlag.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBThreadFlag> mbThreadFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbThreadFlags.size());
		Assert.assertEquals(
			newMBThreadFlag,
			mbThreadFlags.get(newMBThreadFlag.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBThreadFlag> mbThreadFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbThreadFlags.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBThreadFlag newMBThreadFlag = addMBThreadFlag();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThreadFlag.getPrimaryKey());

		Map<Serializable, MBThreadFlag> mbThreadFlags =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbThreadFlags.size());
		Assert.assertEquals(
			newMBThreadFlag,
			mbThreadFlags.get(newMBThreadFlag.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBThreadFlag newMBThreadFlag = addMBThreadFlag();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBThreadFlag.getPrimaryKey()));
	}

	private void _assertOriginalValues(MBThreadFlag mbThreadFlag) {
		Assert.assertEquals(
			mbThreadFlag.getUuid(),
			ReflectionTestUtil.invoke(
				mbThreadFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbThreadFlag.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbThreadFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(mbThreadFlag.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				mbThreadFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(mbThreadFlag.getThreadId()),
			ReflectionTestUtil.<Long>invoke(
				mbThreadFlag, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "threadId"));
	}

	protected MBThreadFlag addMBThreadFlag() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThreadFlag mbThreadFlag = _persistence.create(pk);

		mbThreadFlag.setMvccVersion(RandomTestUtil.nextLong());

		mbThreadFlag.setCtCollectionId(RandomTestUtil.nextLong());

		mbThreadFlag.setUuid(RandomTestUtil.randomString());

		mbThreadFlag.setGroupId(RandomTestUtil.nextLong());

		mbThreadFlag.setCompanyId(RandomTestUtil.nextLong());

		mbThreadFlag.setUserId(RandomTestUtil.nextLong());

		mbThreadFlag.setUserName(RandomTestUtil.randomString());

		mbThreadFlag.setCreateDate(RandomTestUtil.nextDate());

		mbThreadFlag.setModifiedDate(RandomTestUtil.nextDate());

		mbThreadFlag.setThreadId(RandomTestUtil.nextLong());

		mbThreadFlag.setLastPublishDate(RandomTestUtil.nextDate());

		_mbThreadFlags.add(_persistence.update(mbThreadFlag));

		return mbThreadFlag;
	}

	private List<MBThreadFlag> _mbThreadFlags = new ArrayList<MBThreadFlag>();
	private MBThreadFlagPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}