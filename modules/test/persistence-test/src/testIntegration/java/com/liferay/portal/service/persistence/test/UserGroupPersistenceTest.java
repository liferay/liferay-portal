/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateUserGroupExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchUserGroupException;
import com.liferay.portal.kernel.model.UserGroup;
import com.liferay.portal.kernel.service.persistence.UserGroupPersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupUtil;
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
public class UserGroupPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserGroupUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UserGroup> iterator = _userGroups.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroup userGroup = _persistence.create(pk);

		Assert.assertNotNull(userGroup);

		Assert.assertEquals(userGroup.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UserGroup newUserGroup = addUserGroup();

		_persistence.remove(newUserGroup);

		UserGroup existingUserGroup = _persistence.fetchByPrimaryKey(
			newUserGroup.getPrimaryKey());

		Assert.assertNull(existingUserGroup);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUserGroup();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroup newUserGroup = _persistence.create(pk);

		newUserGroup.setMvccVersion(RandomTestUtil.nextLong());

		newUserGroup.setCtCollectionId(RandomTestUtil.nextLong());

		newUserGroup.setUuid(RandomTestUtil.randomString());

		newUserGroup.setExternalReferenceCode(RandomTestUtil.randomString());

		newUserGroup.setCompanyId(RandomTestUtil.nextLong());

		newUserGroup.setUserId(RandomTestUtil.nextLong());

		newUserGroup.setUserName(RandomTestUtil.randomString());

		newUserGroup.setCreateDate(RandomTestUtil.nextDate());

		newUserGroup.setModifiedDate(RandomTestUtil.nextDate());

		newUserGroup.setParentUserGroupId(RandomTestUtil.nextLong());

		newUserGroup.setName(RandomTestUtil.randomString());

		newUserGroup.setDescription(RandomTestUtil.randomString());

		newUserGroup.setAddedByLDAPImport(RandomTestUtil.randomBoolean());

		_userGroups.add(_persistence.update(newUserGroup));

		UserGroup existingUserGroup = _persistence.findByPrimaryKey(
			newUserGroup.getPrimaryKey());

		Assert.assertEquals(
			existingUserGroup.getMvccVersion(), newUserGroup.getMvccVersion());
		Assert.assertEquals(
			existingUserGroup.getCtCollectionId(),
			newUserGroup.getCtCollectionId());
		Assert.assertEquals(
			existingUserGroup.getUuid(), newUserGroup.getUuid());
		Assert.assertEquals(
			existingUserGroup.getExternalReferenceCode(),
			newUserGroup.getExternalReferenceCode());
		Assert.assertEquals(
			existingUserGroup.getUserGroupId(), newUserGroup.getUserGroupId());
		Assert.assertEquals(
			existingUserGroup.getCompanyId(), newUserGroup.getCompanyId());
		Assert.assertEquals(
			existingUserGroup.getUserId(), newUserGroup.getUserId());
		Assert.assertEquals(
			existingUserGroup.getUserName(), newUserGroup.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUserGroup.getCreateDate()),
			Time.getShortTimestamp(newUserGroup.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingUserGroup.getModifiedDate()),
			Time.getShortTimestamp(newUserGroup.getModifiedDate()));
		Assert.assertEquals(
			existingUserGroup.getParentUserGroupId(),
			newUserGroup.getParentUserGroupId());
		Assert.assertEquals(
			existingUserGroup.getName(), newUserGroup.getName());
		Assert.assertEquals(
			existingUserGroup.getDescription(), newUserGroup.getDescription());
		Assert.assertEquals(
			existingUserGroup.isAddedByLDAPImport(),
			newUserGroup.isAddedByLDAPImport());
	}

	@Test(expected = DuplicateUserGroupExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		UserGroup userGroup = addUserGroup();

		UserGroup newUserGroup = addUserGroup();

		newUserGroup.setCompanyId(userGroup.getCompanyId());

		newUserGroup = _persistence.update(newUserGroup);

		Session session = _persistence.getCurrentSession();

		session.evict(newUserGroup);

		newUserGroup.setExternalReferenceCode(
			userGroup.getExternalReferenceCode());

		_persistence.update(newUserGroup);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_P(0L, 0L);
	}

	@Test
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testCountByC_LikeN() throws Exception {
		_persistence.countByC_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeN(0L, "null");

		_persistence.countByC_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByGtU_C_P() throws Exception {
		_persistence.countByGtU_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByGtU_C_P(0L, 0L, 0L);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UserGroup newUserGroup = addUserGroup();

		UserGroup existingUserGroup = _persistence.findByPrimaryKey(
			newUserGroup.getPrimaryKey());

		Assert.assertEquals(existingUserGroup, newUserGroup);
	}

	@Test(expected = NoSuchUserGroupException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UserGroup> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"UserGroup", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "userGroupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "parentUserGroupId", true, "name", true,
			"description", true, "addedByLDAPImport", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UserGroup newUserGroup = addUserGroup();

		UserGroup existingUserGroup = _persistence.fetchByPrimaryKey(
			newUserGroup.getPrimaryKey());

		Assert.assertEquals(existingUserGroup, newUserGroup);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroup missingUserGroup = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUserGroup);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UserGroup newUserGroup1 = addUserGroup();
		UserGroup newUserGroup2 = addUserGroup();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserGroup1.getPrimaryKey());
		primaryKeys.add(newUserGroup2.getPrimaryKey());

		Map<Serializable, UserGroup> userGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, userGroups.size());
		Assert.assertEquals(
			newUserGroup1, userGroups.get(newUserGroup1.getPrimaryKey()));
		Assert.assertEquals(
			newUserGroup2, userGroups.get(newUserGroup2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UserGroup> userGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userGroups.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UserGroup newUserGroup = addUserGroup();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserGroup.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UserGroup> userGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userGroups.size());
		Assert.assertEquals(
			newUserGroup, userGroups.get(newUserGroup.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UserGroup> userGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userGroups.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UserGroup newUserGroup = addUserGroup();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserGroup.getPrimaryKey());

		Map<Serializable, UserGroup> userGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userGroups.size());
		Assert.assertEquals(
			newUserGroup, userGroups.get(newUserGroup.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UserGroup newUserGroup = addUserGroup();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newUserGroup.getPrimaryKey()));
	}

	private void _assertOriginalValues(UserGroup userGroup) {
		Assert.assertEquals(
			Long.valueOf(userGroup.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				userGroup, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			userGroup.getName(),
			ReflectionTestUtil.invoke(
				userGroup, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			userGroup.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				userGroup, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(userGroup.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				userGroup, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected UserGroup addUserGroup() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroup userGroup = _persistence.create(pk);

		userGroup.setMvccVersion(RandomTestUtil.nextLong());

		userGroup.setCtCollectionId(RandomTestUtil.nextLong());

		userGroup.setUuid(RandomTestUtil.randomString());

		userGroup.setExternalReferenceCode(RandomTestUtil.randomString());

		userGroup.setCompanyId(RandomTestUtil.nextLong());

		userGroup.setUserId(RandomTestUtil.nextLong());

		userGroup.setUserName(RandomTestUtil.randomString());

		userGroup.setCreateDate(RandomTestUtil.nextDate());

		userGroup.setModifiedDate(RandomTestUtil.nextDate());

		userGroup.setParentUserGroupId(RandomTestUtil.nextLong());

		userGroup.setName(RandomTestUtil.randomString());

		userGroup.setDescription(RandomTestUtil.randomString());

		userGroup.setAddedByLDAPImport(RandomTestUtil.randomBoolean());

		_userGroups.add(_persistence.update(userGroup));

		return userGroup;
	}

	private List<UserGroup> _userGroups = new ArrayList<UserGroup>();
	private UserGroupPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}