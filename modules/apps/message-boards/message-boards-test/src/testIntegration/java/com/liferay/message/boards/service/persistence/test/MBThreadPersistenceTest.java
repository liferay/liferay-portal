/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.NoSuchThreadException;
import com.liferay.message.boards.model.MBThread;
import com.liferay.message.boards.service.persistence.MBThreadPersistence;
import com.liferay.message.boards.service.persistence.MBThreadUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class MBThreadPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBThreadUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBThread> iterator = _mbThreads.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread mbThread = _persistence.create(pk);

		Assert.assertNotNull(mbThread);

		Assert.assertEquals(mbThread.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBThread newMBThread = addMBThread();

		_persistence.remove(newMBThread);

		MBThread existingMBThread = _persistence.fetchByPrimaryKey(
			newMBThread.getPrimaryKey());

		Assert.assertNull(existingMBThread);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBThread();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread newMBThread = _persistence.create(pk);

		newMBThread.setMvccVersion(RandomTestUtil.nextLong());

		newMBThread.setCtCollectionId(RandomTestUtil.nextLong());

		newMBThread.setUuid(RandomTestUtil.randomString());

		newMBThread.setGroupId(RandomTestUtil.nextLong());

		newMBThread.setCompanyId(RandomTestUtil.nextLong());

		newMBThread.setUserId(RandomTestUtil.nextLong());

		newMBThread.setUserName(RandomTestUtil.randomString());

		newMBThread.setCreateDate(RandomTestUtil.nextDate());

		newMBThread.setModifiedDate(RandomTestUtil.nextDate());

		newMBThread.setCategoryId(RandomTestUtil.nextLong());

		newMBThread.setRootMessageId(RandomTestUtil.nextLong());

		newMBThread.setRootMessageUserId(RandomTestUtil.nextLong());

		newMBThread.setTitle(RandomTestUtil.randomString());

		newMBThread.setLastPostByUserId(RandomTestUtil.nextLong());

		newMBThread.setLastPostDate(RandomTestUtil.nextDate());

		newMBThread.setPriority(RandomTestUtil.nextDouble());

		newMBThread.setQuestion(RandomTestUtil.randomBoolean());

		newMBThread.setLastPublishDate(RandomTestUtil.nextDate());

		newMBThread.setStatus(RandomTestUtil.nextInt());

		newMBThread.setStatusByUserId(RandomTestUtil.nextLong());

		newMBThread.setStatusByUserName(RandomTestUtil.randomString());

		newMBThread.setStatusDate(RandomTestUtil.nextDate());

		_mbThreads.add(_persistence.update(newMBThread));

		MBThread existingMBThread = _persistence.findByPrimaryKey(
			newMBThread.getPrimaryKey());

		Assert.assertEquals(
			existingMBThread.getMvccVersion(), newMBThread.getMvccVersion());
		Assert.assertEquals(
			existingMBThread.getCtCollectionId(),
			newMBThread.getCtCollectionId());
		Assert.assertEquals(existingMBThread.getUuid(), newMBThread.getUuid());
		Assert.assertEquals(
			existingMBThread.getThreadId(), newMBThread.getThreadId());
		Assert.assertEquals(
			existingMBThread.getGroupId(), newMBThread.getGroupId());
		Assert.assertEquals(
			existingMBThread.getCompanyId(), newMBThread.getCompanyId());
		Assert.assertEquals(
			existingMBThread.getUserId(), newMBThread.getUserId());
		Assert.assertEquals(
			existingMBThread.getUserName(), newMBThread.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThread.getCreateDate()),
			Time.getShortTimestamp(newMBThread.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThread.getModifiedDate()),
			Time.getShortTimestamp(newMBThread.getModifiedDate()));
		Assert.assertEquals(
			existingMBThread.getCategoryId(), newMBThread.getCategoryId());
		Assert.assertEquals(
			existingMBThread.getRootMessageId(),
			newMBThread.getRootMessageId());
		Assert.assertEquals(
			existingMBThread.getRootMessageUserId(),
			newMBThread.getRootMessageUserId());
		Assert.assertEquals(
			existingMBThread.getTitle(), newMBThread.getTitle());
		Assert.assertEquals(
			existingMBThread.getLastPostByUserId(),
			newMBThread.getLastPostByUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThread.getLastPostDate()),
			Time.getShortTimestamp(newMBThread.getLastPostDate()));
		AssertUtils.assertEquals(
			existingMBThread.getPriority(), newMBThread.getPriority());
		Assert.assertEquals(
			existingMBThread.isQuestion(), newMBThread.isQuestion());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThread.getLastPublishDate()),
			Time.getShortTimestamp(newMBThread.getLastPublishDate()));
		Assert.assertEquals(
			existingMBThread.getStatus(), newMBThread.getStatus());
		Assert.assertEquals(
			existingMBThread.getStatusByUserId(),
			newMBThread.getStatusByUserId());
		Assert.assertEquals(
			existingMBThread.getStatusByUserName(),
			newMBThread.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBThread.getStatusDate()),
			Time.getShortTimestamp(newMBThread.getStatusDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByRootMessageId() throws Exception {
		_persistence.countByRootMessageId(RandomTestUtil.nextLong());

		_persistence.countByRootMessageId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_CArrayable() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_NotC() throws Exception {
		_persistence.countByG_NotC(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_NotC(0L, 0L);
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDouble());

		_persistence.countByC_P(0L, 0D);
	}

	@Test
	public void testCountByL_P() throws Exception {
		_persistence.countByL_P(
			RandomTestUtil.nextDate(), RandomTestUtil.nextDouble());

		_persistence.countByL_P(RandomTestUtil.nextDate(), 0D);
	}

	@Test
	public void testCountByG_C_L() throws Exception {
		_persistence.countByG_C_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextDate());

		_persistence.countByG_C_L(0L, 0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByG_C_S() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_SArrayable() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_C_NotS() throws Exception {
		_persistence.countByG_C_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_NotSArrayable() throws Exception {
		_persistence.countByG_C_NotS(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_NotC_S() throws Exception {
		_persistence.countByG_NotC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_NotC_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_NotC_NotS() throws Exception {
		_persistence.countByG_NotC_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_NotC_NotS(0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBThread newMBThread = addMBThread();

		MBThread existingMBThread = _persistence.findByPrimaryKey(
			newMBThread.getPrimaryKey());

		Assert.assertEquals(existingMBThread, newMBThread);
	}

	@Test(expected = NoSuchThreadException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MBThread> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBThread", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "threadId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "categoryId", true, "rootMessageId", true,
			"rootMessageUserId", true, "title", true, "lastPostByUserId", true,
			"lastPostDate", true, "priority", true, "question", true,
			"lastPublishDate", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBThread newMBThread = addMBThread();

		MBThread existingMBThread = _persistence.fetchByPrimaryKey(
			newMBThread.getPrimaryKey());

		Assert.assertEquals(existingMBThread, newMBThread);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread missingMBThread = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBThread);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBThread newMBThread1 = addMBThread();
		MBThread newMBThread2 = addMBThread();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThread1.getPrimaryKey());
		primaryKeys.add(newMBThread2.getPrimaryKey());

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, mbThreads.size());
		Assert.assertEquals(
			newMBThread1, mbThreads.get(newMBThread1.getPrimaryKey()));
		Assert.assertEquals(
			newMBThread2, mbThreads.get(newMBThread2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(mbThreads.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBThread newMBThread = addMBThread();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThread.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, mbThreads.size());
		Assert.assertEquals(
			newMBThread, mbThreads.get(newMBThread.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(mbThreads.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBThread newMBThread = addMBThread();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBThread.getPrimaryKey());

		Map<Serializable, MBThread> mbThreads = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, mbThreads.size());
		Assert.assertEquals(
			newMBThread, mbThreads.get(newMBThread.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBThread newMBThread = addMBThread();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBThread.getPrimaryKey()));
	}

	private void _assertOriginalValues(MBThread mbThread) {
		Assert.assertEquals(
			mbThread.getUuid(),
			ReflectionTestUtil.invoke(
				mbThread, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbThread.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbThread, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(mbThread.getRootMessageId()),
			ReflectionTestUtil.<Long>invoke(
				mbThread, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "rootMessageId"));
	}

	protected MBThread addMBThread() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBThread mbThread = _persistence.create(pk);

		mbThread.setMvccVersion(RandomTestUtil.nextLong());

		mbThread.setCtCollectionId(RandomTestUtil.nextLong());

		mbThread.setUuid(RandomTestUtil.randomString());

		mbThread.setGroupId(RandomTestUtil.nextLong());

		mbThread.setCompanyId(RandomTestUtil.nextLong());

		mbThread.setUserId(RandomTestUtil.nextLong());

		mbThread.setUserName(RandomTestUtil.randomString());

		mbThread.setCreateDate(RandomTestUtil.nextDate());

		mbThread.setModifiedDate(RandomTestUtil.nextDate());

		mbThread.setCategoryId(RandomTestUtil.nextLong());

		mbThread.setRootMessageId(RandomTestUtil.nextLong());

		mbThread.setRootMessageUserId(RandomTestUtil.nextLong());

		mbThread.setTitle(RandomTestUtil.randomString());

		mbThread.setLastPostByUserId(RandomTestUtil.nextLong());

		mbThread.setLastPostDate(RandomTestUtil.nextDate());

		mbThread.setPriority(RandomTestUtil.nextDouble());

		mbThread.setQuestion(RandomTestUtil.randomBoolean());

		mbThread.setLastPublishDate(RandomTestUtil.nextDate());

		mbThread.setStatus(RandomTestUtil.nextInt());

		mbThread.setStatusByUserId(RandomTestUtil.nextLong());

		mbThread.setStatusByUserName(RandomTestUtil.randomString());

		mbThread.setStatusDate(RandomTestUtil.nextDate());

		_mbThreads.add(_persistence.update(mbThread));

		return mbThread;
	}

	private List<MBThread> _mbThreads = new ArrayList<MBThread>();
	private MBThreadPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}