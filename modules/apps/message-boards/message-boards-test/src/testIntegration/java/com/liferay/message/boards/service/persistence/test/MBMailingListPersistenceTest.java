/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.message.boards.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.message.boards.exception.NoSuchMailingListException;
import com.liferay.message.boards.model.MBMailingList;
import com.liferay.message.boards.service.persistence.MBMailingListPersistence;
import com.liferay.message.boards.service.persistence.MBMailingListUtil;
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
public class MBMailingListPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.message.boards.service"));

	@Before
	public void setUp() {
		_persistence = MBMailingListUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<MBMailingList> iterator = _mbMailingLists.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMailingList mbMailingList = _persistence.create(pk);

		Assert.assertNotNull(mbMailingList);

		Assert.assertEquals(mbMailingList.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		MBMailingList newMBMailingList = addMBMailingList();

		_persistence.remove(newMBMailingList);

		MBMailingList existingMBMailingList = _persistence.fetchByPrimaryKey(
			newMBMailingList.getPrimaryKey());

		Assert.assertNull(existingMBMailingList);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addMBMailingList();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMailingList newMBMailingList = _persistence.create(pk);

		newMBMailingList.setMvccVersion(RandomTestUtil.nextLong());

		newMBMailingList.setCtCollectionId(RandomTestUtil.nextLong());

		newMBMailingList.setUuid(RandomTestUtil.randomString());

		newMBMailingList.setGroupId(RandomTestUtil.nextLong());

		newMBMailingList.setCompanyId(RandomTestUtil.nextLong());

		newMBMailingList.setUserId(RandomTestUtil.nextLong());

		newMBMailingList.setUserName(RandomTestUtil.randomString());

		newMBMailingList.setCreateDate(RandomTestUtil.nextDate());

		newMBMailingList.setModifiedDate(RandomTestUtil.nextDate());

		newMBMailingList.setCategoryId(RandomTestUtil.nextLong());

		newMBMailingList.setEmailAddress(RandomTestUtil.randomString());

		newMBMailingList.setInProtocol(RandomTestUtil.randomString());

		newMBMailingList.setInServerName(RandomTestUtil.randomString());

		newMBMailingList.setInServerPort(RandomTestUtil.nextInt());

		newMBMailingList.setInUseSSL(RandomTestUtil.randomBoolean());

		newMBMailingList.setInUserName(RandomTestUtil.randomString());

		newMBMailingList.setInPassword(RandomTestUtil.randomString());

		newMBMailingList.setInReadInterval(RandomTestUtil.nextInt());

		newMBMailingList.setOutEmailAddress(RandomTestUtil.randomString());

		newMBMailingList.setOutCustom(RandomTestUtil.randomBoolean());

		newMBMailingList.setOutServerName(RandomTestUtil.randomString());

		newMBMailingList.setOutServerPort(RandomTestUtil.nextInt());

		newMBMailingList.setOutUseSSL(RandomTestUtil.randomBoolean());

		newMBMailingList.setOutUserName(RandomTestUtil.randomString());

		newMBMailingList.setOutPassword(RandomTestUtil.randomString());

		newMBMailingList.setAllowAnonymous(RandomTestUtil.randomBoolean());

		newMBMailingList.setActive(RandomTestUtil.randomBoolean());

		_mbMailingLists.add(_persistence.update(newMBMailingList));

		MBMailingList existingMBMailingList = _persistence.findByPrimaryKey(
			newMBMailingList.getPrimaryKey());

		Assert.assertEquals(
			existingMBMailingList.getMvccVersion(),
			newMBMailingList.getMvccVersion());
		Assert.assertEquals(
			existingMBMailingList.getCtCollectionId(),
			newMBMailingList.getCtCollectionId());
		Assert.assertEquals(
			existingMBMailingList.getUuid(), newMBMailingList.getUuid());
		Assert.assertEquals(
			existingMBMailingList.getMailingListId(),
			newMBMailingList.getMailingListId());
		Assert.assertEquals(
			existingMBMailingList.getGroupId(), newMBMailingList.getGroupId());
		Assert.assertEquals(
			existingMBMailingList.getCompanyId(),
			newMBMailingList.getCompanyId());
		Assert.assertEquals(
			existingMBMailingList.getUserId(), newMBMailingList.getUserId());
		Assert.assertEquals(
			existingMBMailingList.getUserName(),
			newMBMailingList.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBMailingList.getCreateDate()),
			Time.getShortTimestamp(newMBMailingList.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingMBMailingList.getModifiedDate()),
			Time.getShortTimestamp(newMBMailingList.getModifiedDate()));
		Assert.assertEquals(
			existingMBMailingList.getCategoryId(),
			newMBMailingList.getCategoryId());
		Assert.assertEquals(
			existingMBMailingList.getEmailAddress(),
			newMBMailingList.getEmailAddress());
		Assert.assertEquals(
			existingMBMailingList.getInProtocol(),
			newMBMailingList.getInProtocol());
		Assert.assertEquals(
			existingMBMailingList.getInServerName(),
			newMBMailingList.getInServerName());
		Assert.assertEquals(
			existingMBMailingList.getInServerPort(),
			newMBMailingList.getInServerPort());
		Assert.assertEquals(
			existingMBMailingList.isInUseSSL(), newMBMailingList.isInUseSSL());
		Assert.assertEquals(
			existingMBMailingList.getInUserName(),
			newMBMailingList.getInUserName());
		Assert.assertEquals(
			existingMBMailingList.getInPassword(),
			newMBMailingList.getInPassword());
		Assert.assertEquals(
			existingMBMailingList.getInReadInterval(),
			newMBMailingList.getInReadInterval());
		Assert.assertEquals(
			existingMBMailingList.getOutEmailAddress(),
			newMBMailingList.getOutEmailAddress());
		Assert.assertEquals(
			existingMBMailingList.isOutCustom(),
			newMBMailingList.isOutCustom());
		Assert.assertEquals(
			existingMBMailingList.getOutServerName(),
			newMBMailingList.getOutServerName());
		Assert.assertEquals(
			existingMBMailingList.getOutServerPort(),
			newMBMailingList.getOutServerPort());
		Assert.assertEquals(
			existingMBMailingList.isOutUseSSL(),
			newMBMailingList.isOutUseSSL());
		Assert.assertEquals(
			existingMBMailingList.getOutUserName(),
			newMBMailingList.getOutUserName());
		Assert.assertEquals(
			existingMBMailingList.getOutPassword(),
			newMBMailingList.getOutPassword());
		Assert.assertEquals(
			existingMBMailingList.isAllowAnonymous(),
			newMBMailingList.isAllowAnonymous());
		Assert.assertEquals(
			existingMBMailingList.isActive(), newMBMailingList.isActive());
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
	public void testCountByActive() throws Exception {
		_persistence.countByActive(RandomTestUtil.randomBoolean());

		_persistence.countByActive(RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		MBMailingList newMBMailingList = addMBMailingList();

		MBMailingList existingMBMailingList = _persistence.findByPrimaryKey(
			newMBMailingList.getPrimaryKey());

		Assert.assertEquals(existingMBMailingList, newMBMailingList);
	}

	@Test(expected = NoSuchMailingListException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<MBMailingList> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"MBMailingList", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "mailingListId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "categoryId", true, "emailAddress", true,
			"inProtocol", true, "inServerName", true, "inServerPort", true,
			"inUseSSL", true, "inUserName", true, "inPassword", true,
			"inReadInterval", true, "outEmailAddress", true, "outCustom", true,
			"outServerName", true, "outServerPort", true, "outUseSSL", true,
			"outUserName", true, "outPassword", true, "allowAnonymous", true,
			"active", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		MBMailingList newMBMailingList = addMBMailingList();

		MBMailingList existingMBMailingList = _persistence.fetchByPrimaryKey(
			newMBMailingList.getPrimaryKey());

		Assert.assertEquals(existingMBMailingList, newMBMailingList);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMailingList missingMBMailingList = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingMBMailingList);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		MBMailingList newMBMailingList1 = addMBMailingList();
		MBMailingList newMBMailingList2 = addMBMailingList();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBMailingList1.getPrimaryKey());
		primaryKeys.add(newMBMailingList2.getPrimaryKey());

		Map<Serializable, MBMailingList> mbMailingLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, mbMailingLists.size());
		Assert.assertEquals(
			newMBMailingList1,
			mbMailingLists.get(newMBMailingList1.getPrimaryKey()));
		Assert.assertEquals(
			newMBMailingList2,
			mbMailingLists.get(newMBMailingList2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, MBMailingList> mbMailingLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbMailingLists.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		MBMailingList newMBMailingList = addMBMailingList();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBMailingList.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, MBMailingList> mbMailingLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbMailingLists.size());
		Assert.assertEquals(
			newMBMailingList,
			mbMailingLists.get(newMBMailingList.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, MBMailingList> mbMailingLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(mbMailingLists.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		MBMailingList newMBMailingList = addMBMailingList();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newMBMailingList.getPrimaryKey());

		Map<Serializable, MBMailingList> mbMailingLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, mbMailingLists.size());
		Assert.assertEquals(
			newMBMailingList,
			mbMailingLists.get(newMBMailingList.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		MBMailingList newMBMailingList = addMBMailingList();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newMBMailingList.getPrimaryKey()));
	}

	private void _assertOriginalValues(MBMailingList mbMailingList) {
		Assert.assertEquals(
			mbMailingList.getUuid(),
			ReflectionTestUtil.invoke(
				mbMailingList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(mbMailingList.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbMailingList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(mbMailingList.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				mbMailingList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(mbMailingList.getCategoryId()),
			ReflectionTestUtil.<Long>invoke(
				mbMailingList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "categoryId"));
	}

	protected MBMailingList addMBMailingList() throws Exception {
		long pk = RandomTestUtil.nextLong();

		MBMailingList mbMailingList = _persistence.create(pk);

		mbMailingList.setMvccVersion(RandomTestUtil.nextLong());

		mbMailingList.setCtCollectionId(RandomTestUtil.nextLong());

		mbMailingList.setUuid(RandomTestUtil.randomString());

		mbMailingList.setGroupId(RandomTestUtil.nextLong());

		mbMailingList.setCompanyId(RandomTestUtil.nextLong());

		mbMailingList.setUserId(RandomTestUtil.nextLong());

		mbMailingList.setUserName(RandomTestUtil.randomString());

		mbMailingList.setCreateDate(RandomTestUtil.nextDate());

		mbMailingList.setModifiedDate(RandomTestUtil.nextDate());

		mbMailingList.setCategoryId(RandomTestUtil.nextLong());

		mbMailingList.setEmailAddress(RandomTestUtil.randomString());

		mbMailingList.setInProtocol(RandomTestUtil.randomString());

		mbMailingList.setInServerName(RandomTestUtil.randomString());

		mbMailingList.setInServerPort(RandomTestUtil.nextInt());

		mbMailingList.setInUseSSL(RandomTestUtil.randomBoolean());

		mbMailingList.setInUserName(RandomTestUtil.randomString());

		mbMailingList.setInPassword(RandomTestUtil.randomString());

		mbMailingList.setInReadInterval(RandomTestUtil.nextInt());

		mbMailingList.setOutEmailAddress(RandomTestUtil.randomString());

		mbMailingList.setOutCustom(RandomTestUtil.randomBoolean());

		mbMailingList.setOutServerName(RandomTestUtil.randomString());

		mbMailingList.setOutServerPort(RandomTestUtil.nextInt());

		mbMailingList.setOutUseSSL(RandomTestUtil.randomBoolean());

		mbMailingList.setOutUserName(RandomTestUtil.randomString());

		mbMailingList.setOutPassword(RandomTestUtil.randomString());

		mbMailingList.setAllowAnonymous(RandomTestUtil.randomBoolean());

		mbMailingList.setActive(RandomTestUtil.randomBoolean());

		_mbMailingLists.add(_persistence.update(mbMailingList));

		return mbMailingList;
	}

	private List<MBMailingList> _mbMailingLists =
		new ArrayList<MBMailingList>();
	private MBMailingListPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}