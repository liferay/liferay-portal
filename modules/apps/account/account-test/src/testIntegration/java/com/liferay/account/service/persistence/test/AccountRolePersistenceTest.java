/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.test;

import com.liferay.account.exception.DuplicateAccountRoleExternalReferenceCodeException;
import com.liferay.account.exception.NoSuchRoleException;
import com.liferay.account.model.AccountRole;
import com.liferay.account.service.persistence.AccountRolePersistence;
import com.liferay.account.service.persistence.AccountRoleUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class AccountRolePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.account.service"));

	@Before
	public void setUp() {
		_persistence = AccountRoleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AccountRole> iterator = _accountRoles.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountRole accountRole = _persistence.create(pk);

		Assert.assertNotNull(accountRole);

		Assert.assertEquals(accountRole.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AccountRole newAccountRole = addAccountRole();

		_persistence.remove(newAccountRole);

		AccountRole existingAccountRole = _persistence.fetchByPrimaryKey(
			newAccountRole.getPrimaryKey());

		Assert.assertNull(existingAccountRole);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAccountRole();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountRole newAccountRole = _persistence.create(pk);

		newAccountRole.setMvccVersion(RandomTestUtil.nextLong());

		newAccountRole.setExternalReferenceCode(RandomTestUtil.randomString());

		newAccountRole.setCompanyId(RandomTestUtil.nextLong());

		newAccountRole.setAccountEntryId(RandomTestUtil.nextLong());

		newAccountRole.setRoleId(RandomTestUtil.nextLong());

		_accountRoles.add(_persistence.update(newAccountRole));

		AccountRole existingAccountRole = _persistence.findByPrimaryKey(
			newAccountRole.getPrimaryKey());

		Assert.assertEquals(
			existingAccountRole.getMvccVersion(),
			newAccountRole.getMvccVersion());
		Assert.assertEquals(
			existingAccountRole.getExternalReferenceCode(),
			newAccountRole.getExternalReferenceCode());
		Assert.assertEquals(
			existingAccountRole.getAccountRoleId(),
			newAccountRole.getAccountRoleId());
		Assert.assertEquals(
			existingAccountRole.getCompanyId(), newAccountRole.getCompanyId());
		Assert.assertEquals(
			existingAccountRole.getAccountEntryId(),
			newAccountRole.getAccountEntryId());
		Assert.assertEquals(
			existingAccountRole.getRoleId(), newAccountRole.getRoleId());
	}

	@Test(expected = DuplicateAccountRoleExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AccountRole accountRole = addAccountRole();

		AccountRole newAccountRole = addAccountRole();

		newAccountRole.setCompanyId(accountRole.getCompanyId());

		newAccountRole = _persistence.update(newAccountRole);

		Session session = _persistence.getCurrentSession();

		session.evict(newAccountRole);

		newAccountRole.setExternalReferenceCode(
			accountRole.getExternalReferenceCode());

		_persistence.update(newAccountRole);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountByAccountEntryIdArrayable() throws Exception {
		_persistence.countByAccountEntryId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByRoleId() throws Exception {
		_persistence.countByRoleId(RandomTestUtil.nextLong());

		_persistence.countByRoleId(0L);
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_A(0L, 0L);
	}

	@Test
	public void testCountByC_AArrayable() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AccountRole newAccountRole = addAccountRole();

		AccountRole existingAccountRole = _persistence.findByPrimaryKey(
			newAccountRole.getPrimaryKey());

		Assert.assertEquals(existingAccountRole, newAccountRole);
	}

	@Test(expected = NoSuchRoleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AccountRole> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AccountRole", "mvccVersion", true, "externalReferenceCode", true,
			"accountRoleId", true, "companyId", true, "accountEntryId", true,
			"roleId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AccountRole newAccountRole = addAccountRole();

		AccountRole existingAccountRole = _persistence.fetchByPrimaryKey(
			newAccountRole.getPrimaryKey());

		Assert.assertEquals(existingAccountRole, newAccountRole);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountRole missingAccountRole = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAccountRole);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AccountRole newAccountRole1 = addAccountRole();
		AccountRole newAccountRole2 = addAccountRole();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountRole1.getPrimaryKey());
		primaryKeys.add(newAccountRole2.getPrimaryKey());

		Map<Serializable, AccountRole> accountRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, accountRoles.size());
		Assert.assertEquals(
			newAccountRole1, accountRoles.get(newAccountRole1.getPrimaryKey()));
		Assert.assertEquals(
			newAccountRole2, accountRoles.get(newAccountRole2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AccountRole> accountRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountRoles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AccountRole newAccountRole = addAccountRole();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountRole.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AccountRole> accountRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountRoles.size());
		Assert.assertEquals(
			newAccountRole, accountRoles.get(newAccountRole.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AccountRole> accountRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountRoles.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AccountRole newAccountRole = addAccountRole();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountRole.getPrimaryKey());

		Map<Serializable, AccountRole> accountRoles =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountRoles.size());
		Assert.assertEquals(
			newAccountRole, accountRoles.get(newAccountRole.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AccountRole newAccountRole = addAccountRole();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAccountRole.getPrimaryKey()));
	}

	private void _assertOriginalValues(AccountRole accountRole) {
		Assert.assertEquals(
			Long.valueOf(accountRole.getRoleId()),
			ReflectionTestUtil.<Long>invoke(
				accountRole, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "roleId"));

		Assert.assertEquals(
			accountRole.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				accountRole, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(accountRole.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				accountRole, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected AccountRole addAccountRole() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountRole accountRole = _persistence.create(pk);

		accountRole.setMvccVersion(RandomTestUtil.nextLong());

		accountRole.setExternalReferenceCode(RandomTestUtil.randomString());

		accountRole.setCompanyId(RandomTestUtil.nextLong());

		accountRole.setAccountEntryId(RandomTestUtil.nextLong());

		accountRole.setRoleId(RandomTestUtil.nextLong());

		_accountRoles.add(_persistence.update(accountRole));

		return accountRole;
	}

	private List<AccountRole> _accountRoles = new ArrayList<AccountRole>();
	private AccountRolePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}