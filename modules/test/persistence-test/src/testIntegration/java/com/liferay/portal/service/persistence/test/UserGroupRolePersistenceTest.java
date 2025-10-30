/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserGroupRoleException;
import com.liferay.portal.kernel.model.UserGroupRole;
import com.liferay.portal.kernel.service.persistence.UserGroupRolePersistence;
import com.liferay.portal.kernel.service.persistence.UserGroupRoleUtil;
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
public class UserGroupRolePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserGroupRoleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UserGroupRole> iterator = _userGroupRoles.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroupRole userGroupRole = _persistence.create(pk);

		Assert.assertNotNull(userGroupRole);

		Assert.assertEquals(userGroupRole.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UserGroupRole newUserGroupRole = addUserGroupRole();

		_persistence.remove(newUserGroupRole);

		UserGroupRole existingUserGroupRole = _persistence.fetchByPrimaryKey(
			newUserGroupRole.getPrimaryKey());

		Assert.assertNull(existingUserGroupRole);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUserGroupRole();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroupRole newUserGroupRole = _persistence.create(pk);

		newUserGroupRole.setMvccVersion(RandomTestUtil.nextLong());

		newUserGroupRole.setCtCollectionId(RandomTestUtil.nextLong());

		newUserGroupRole.setCompanyId(RandomTestUtil.nextLong());

		newUserGroupRole.setUserId(RandomTestUtil.nextLong());

		newUserGroupRole.setGroupId(RandomTestUtil.nextLong());

		newUserGroupRole.setRoleId(RandomTestUtil.nextLong());

		_userGroupRoles.add(_persistence.update(newUserGroupRole));

		UserGroupRole existingUserGroupRole = _persistence.findByPrimaryKey(
			newUserGroupRole.getPrimaryKey());

		Assert.assertEquals(
			existingUserGroupRole.getMvccVersion(),
			newUserGroupRole.getMvccVersion());
		Assert.assertEquals(
			existingUserGroupRole.getCtCollectionId(),
			newUserGroupRole.getCtCollectionId());
		Assert.assertEquals(
			existingUserGroupRole.getUserGroupRoleId(),
			newUserGroupRole.getUserGroupRoleId());
		Assert.assertEquals(
			existingUserGroupRole.getCompanyId(),
			newUserGroupRole.getCompanyId());
		Assert.assertEquals(
			existingUserGroupRole.getUserId(), newUserGroupRole.getUserId());
		Assert.assertEquals(
			existingUserGroupRole.getGroupId(), newUserGroupRole.getGroupId());
		Assert.assertEquals(
			existingUserGroupRole.getRoleId(), newUserGroupRole.getRoleId());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByRoleId() throws Exception {
		_persistence.countByRoleId(RandomTestUtil.nextLong());

		_persistence.countByRoleId(0L);
	}

	@Test
	public void testCountByU_G() throws Exception {
		_persistence.countByU_G(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByU_G(0L, 0L);
	}

	@Test
	public void testCountByG_R() throws Exception {
		_persistence.countByG_R(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_R(0L, 0L);
	}

	@Test
	public void testCountByU_G_R() throws Exception {
		_persistence.countByU_G_R(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByU_G_R(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UserGroupRole newUserGroupRole = addUserGroupRole();

		UserGroupRole existingUserGroupRole = _persistence.findByPrimaryKey(
			newUserGroupRole.getPrimaryKey());

		Assert.assertEquals(existingUserGroupRole, newUserGroupRole);
	}

	@Test(expected = NoSuchUserGroupRoleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UserGroupRole> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"UserGroupRole", "mvccVersion", true, "ctCollectionId", true,
			"userGroupRoleId", true, "companyId", true, "userId", true,
			"groupId", true, "roleId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UserGroupRole newUserGroupRole = addUserGroupRole();

		UserGroupRole existingUserGroupRole = _persistence.fetchByPrimaryKey(
			newUserGroupRole.getPrimaryKey());

		Assert.assertEquals(existingUserGroupRole, newUserGroupRole);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroupRole missingUserGroupRole = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUserGroupRole);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UserGroupRole newUserGroupRole1 = addUserGroupRole();
		UserGroupRole newUserGroupRole2 = addUserGroupRole();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserGroupRole1.getPrimaryKey());
		primaryKeys.add(newUserGroupRole2.getPrimaryKey());

		Map<Serializable, UserGroupRole> userGroupRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, userGroupRoles.size());
		Assert.assertEquals(
			newUserGroupRole1,
			userGroupRoles.get(newUserGroupRole1.getPrimaryKey()));
		Assert.assertEquals(
			newUserGroupRole2,
			userGroupRoles.get(newUserGroupRole2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UserGroupRole> userGroupRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userGroupRoles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UserGroupRole newUserGroupRole = addUserGroupRole();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserGroupRole.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UserGroupRole> userGroupRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userGroupRoles.size());
		Assert.assertEquals(
			newUserGroupRole,
			userGroupRoles.get(newUserGroupRole.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UserGroupRole> userGroupRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userGroupRoles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UserGroupRole newUserGroupRole = addUserGroupRole();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserGroupRole.getPrimaryKey());

		Map<Serializable, UserGroupRole> userGroupRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userGroupRoles.size());
		Assert.assertEquals(
			newUserGroupRole,
			userGroupRoles.get(newUserGroupRole.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UserGroupRole newUserGroupRole = addUserGroupRole();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newUserGroupRole.getPrimaryKey()));
	}

	private void _assertOriginalValues(UserGroupRole userGroupRole) {
		Assert.assertEquals(
			Long.valueOf(userGroupRole.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				userGroupRole, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(userGroupRole.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				userGroupRole, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(userGroupRole.getRoleId()),
			ReflectionTestUtil.<Long>invoke(
				userGroupRole, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "roleId"));
	}

	protected UserGroupRole addUserGroupRole() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserGroupRole userGroupRole = _persistence.create(pk);

		userGroupRole.setMvccVersion(RandomTestUtil.nextLong());

		userGroupRole.setCtCollectionId(RandomTestUtil.nextLong());

		userGroupRole.setCompanyId(RandomTestUtil.nextLong());

		userGroupRole.setUserId(RandomTestUtil.nextLong());

		userGroupRole.setGroupId(RandomTestUtil.nextLong());

		userGroupRole.setRoleId(RandomTestUtil.nextLong());

		_userGroupRoles.add(_persistence.update(userGroupRole));

		return userGroupRole;
	}

	private List<UserGroupRole> _userGroupRoles =
		new ArrayList<UserGroupRole>();
	private UserGroupRolePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}