/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.NoSuchDiscussionException;
import com.liferay.message.boards.model.MBDiscussion;
import com.liferay.message.boards.service.persistence.MBDiscussionPersistence;
import com.liferay.message.boards.service.persistence.MBDiscussionUtil;
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
public class MBDiscussionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBDiscussionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBDiscussion> iterator = _mbDiscussions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBDiscussion mbDiscussion = _persistence.create(pk);

		Assert.assertNotNull(mbDiscussion);

		Assert.assertEquals(mbDiscussion.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBDiscussion newMBDiscussion = addMBDiscussion();

		_persistence.remove(newMBDiscussion);

		MBDiscussion existingMBDiscussion = _persistence.fetchByPrimaryKey(
			newMBDiscussion.getPrimaryKey());

		Assert.assertNull(existingMBDiscussion);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBDiscussion();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBDiscussion newMBDiscussion = _persistence.create(pk);

		newMBDiscussion.setMvccVersion(RandomTestUtil.nextLong());

		newMBDiscussion.setCtCollectionId(RandomTestUtil.nextLong());

		newMBDiscussion.setUuid(RandomTestUtil.randomString());

		newMBDiscussion.setGroupId(RandomTestUtil.nextLong());

		newMBDiscussion.setCompanyId(RandomTestUtil.nextLong());

		newMBDiscussion.setUserId(RandomTestUtil.nextLong());

		newMBDiscussion.setUserName(RandomTestUtil.randomString());

		newMBDiscussion.setCreateDate(RandomTestUtil.nextDate());

		newMBDiscussion.setModifiedDate(RandomTestUtil.nextDate());

		newMBDiscussion.setClassNameId(RandomTestUtil.nextLong());

		newMBDiscussion.setClassPK(RandomTestUtil.nextLong());

		newMBDiscussion.setThreadId(RandomTestUtil.nextLong());

		newMBDiscussion.setLastPublishDate(RandomTestUtil.nextDate());

		_mbDiscussions.add(_persistence.update(newMBDiscussion));

		MBDiscussion existingMBDiscussion = _persistence.findByPrimaryKey(
			newMBDiscussion.getPrimaryKey());

		Assert.assertEquals(
			existingMBDiscussion.getMvccVersion(),
			newMBDiscussion.getMvccVersion());
		Assert.assertEquals(
			existingMBDiscussion.getCtCollectionId(),
			newMBDiscussion.getCtCollectionId());
		Assert.assertEquals(
			existingMBDiscussion.getUuid(), newMBDiscussion.getUuid());
		Assert.assertEquals(
			existingMBDiscussion.getDiscussionId(),
			newMBDiscussion.getDiscussionId());
		Assert.assertEquals(
			existingMBDiscussion.getGroupId(), newMBDiscussion.getGroupId());
		Assert.assertEquals(
			existingMBDiscussion.getCompanyId(),
			newMBDiscussion.getCompanyId());
		Assert.assertEquals(
			existingMBDiscussion.getUserId(), newMBDiscussion.getUserId());
		Assert.assertEquals(
			existingMBDiscussion.getUserName(), newMBDiscussion.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBDiscussion.getCreateDate()),
			Time.getShortTimestamp(newMBDiscussion.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBDiscussion.getModifiedDate()),
			Time.getShortTimestamp(newMBDiscussion.getModifiedDate()));
		Assert.assertEquals(
			existingMBDiscussion.getClassNameId(),
			newMBDiscussion.getClassNameId());
		Assert.assertEquals(
			existingMBDiscussion.getClassPK(), newMBDiscussion.getClassPK());
		Assert.assertEquals(
			existingMBDiscussion.getThreadId(), newMBDiscussion.getThreadId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBDiscussion.getLastPublishDate()),
			Time.getShortTimestamp(newMBDiscussion.getLastPublishDate()));
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
	public void testCountByThreadId() throws Exception {
		_persistence.countByThreadId(RandomTestUtil.nextLong());

		_persistence.countByThreadId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBDiscussion newMBDiscussion = addMBDiscussion();

		MBDiscussion existingMBDiscussion = _persistence.findByPrimaryKey(
			newMBDiscussion.getPrimaryKey());

		Assert.assertEquals(existingMBDiscussion, newMBDiscussion);
	}

	@Test(expected = NoSuchDiscussionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MBDiscussion> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBDiscussion", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "discussionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"threadId", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBDiscussion newMBDiscussion = addMBDiscussion();

		MBDiscussion existingMBDiscussion = _persistence.fetchByPrimaryKey(
			newMBDiscussion.getPrimaryKey());

		Assert.assertEquals(existingMBDiscussion, newMBDiscussion);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBDiscussion missingMBDiscussion = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBDiscussion);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBDiscussion newMBDiscussion1 = addMBDiscussion();
		MBDiscussion newMBDiscussion2 = addMBDiscussion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBDiscussion1.getPrimaryKey());
		primaryKeys.add(newMBDiscussion2.getPrimaryKey());

		Map<Serializable, MBDiscussion> mbDiscussions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, mbDiscussions.size());
		Assert.assertEquals(
			newMBDiscussion1,
			mbDiscussions.get(newMBDiscussion1.getPrimaryKey()));
		Assert.assertEquals(
			newMBDiscussion2,
			mbDiscussions.get(newMBDiscussion2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBDiscussion> mbDiscussions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbDiscussions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBDiscussion newMBDiscussion = addMBDiscussion();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBDiscussion.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBDiscussion> mbDiscussions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbDiscussions.size());
		Assert.assertEquals(
			newMBDiscussion,
			mbDiscussions.get(newMBDiscussion.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBDiscussion> mbDiscussions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbDiscussions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBDiscussion newMBDiscussion = addMBDiscussion();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBDiscussion.getPrimaryKey());

		Map<Serializable, MBDiscussion> mbDiscussions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbDiscussions.size());
		Assert.assertEquals(
			newMBDiscussion,
			mbDiscussions.get(newMBDiscussion.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBDiscussion newMBDiscussion = addMBDiscussion();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBDiscussion.getPrimaryKey()));
	}

	private void _assertOriginalValues(MBDiscussion mbDiscussion) {
		Assert.assertEquals(
			mbDiscussion.getUuid(),
			ReflectionTestUtil.invoke(
				mbDiscussion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbDiscussion.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbDiscussion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(mbDiscussion.getThreadId()),
			ReflectionTestUtil.<Long>invoke(
				mbDiscussion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "threadId"));

		Assert.assertEquals(
			Long.valueOf(mbDiscussion.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				mbDiscussion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(mbDiscussion.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				mbDiscussion, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected MBDiscussion addMBDiscussion() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBDiscussion mbDiscussion = _persistence.create(pk);

		mbDiscussion.setMvccVersion(RandomTestUtil.nextLong());

		mbDiscussion.setCtCollectionId(RandomTestUtil.nextLong());

		mbDiscussion.setUuid(RandomTestUtil.randomString());

		mbDiscussion.setGroupId(RandomTestUtil.nextLong());

		mbDiscussion.setCompanyId(RandomTestUtil.nextLong());

		mbDiscussion.setUserId(RandomTestUtil.nextLong());

		mbDiscussion.setUserName(RandomTestUtil.randomString());

		mbDiscussion.setCreateDate(RandomTestUtil.nextDate());

		mbDiscussion.setModifiedDate(RandomTestUtil.nextDate());

		mbDiscussion.setClassNameId(RandomTestUtil.nextLong());

		mbDiscussion.setClassPK(RandomTestUtil.nextLong());

		mbDiscussion.setThreadId(RandomTestUtil.nextLong());

		mbDiscussion.setLastPublishDate(RandomTestUtil.nextDate());

		_mbDiscussions.add(_persistence.update(mbDiscussion));

		return mbDiscussion;
	}

	private List<MBDiscussion> _mbDiscussions = new ArrayList<MBDiscussion>();
	private MBDiscussionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}