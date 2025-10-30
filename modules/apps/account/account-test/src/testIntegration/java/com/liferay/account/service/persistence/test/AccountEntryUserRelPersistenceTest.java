/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.test;

import com.liferay.account.exception.NoSuchEntryUserRelException;
import com.liferay.account.model.AccountEntryUserRel;
import com.liferay.account.service.persistence.AccountEntryUserRelPersistence;
import com.liferay.account.service.persistence.AccountEntryUserRelUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
public class AccountEntryUserRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.account.service"));

	@Before
	public void setUp() {
		_persistence = AccountEntryUserRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AccountEntryUserRel> iterator =
			_accountEntryUserRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountEntryUserRel accountEntryUserRel = _persistence.create(pk);

		Assert.assertNotNull(accountEntryUserRel);

		Assert.assertEquals(accountEntryUserRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AccountEntryUserRel newAccountEntryUserRel = addAccountEntryUserRel();

		_persistence.remove(newAccountEntryUserRel);

		AccountEntryUserRel existingAccountEntryUserRel =
			_persistence.fetchByPrimaryKey(
				newAccountEntryUserRel.getPrimaryKey());

		Assert.assertNull(existingAccountEntryUserRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAccountEntryUserRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountEntryUserRel newAccountEntryUserRel = _persistence.create(pk);

		newAccountEntryUserRel.setMvccVersion(RandomTestUtil.nextLong());

		newAccountEntryUserRel.setCompanyId(RandomTestUtil.nextLong());

		newAccountEntryUserRel.setAccountEntryId(RandomTestUtil.nextLong());

		newAccountEntryUserRel.setAccountUserId(RandomTestUtil.nextLong());

		_accountEntryUserRels.add(_persistence.update(newAccountEntryUserRel));

		AccountEntryUserRel existingAccountEntryUserRel =
			_persistence.findByPrimaryKey(
				newAccountEntryUserRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountEntryUserRel.getMvccVersion(),
			newAccountEntryUserRel.getMvccVersion());
		Assert.assertEquals(
			existingAccountEntryUserRel.getAccountEntryUserRelId(),
			newAccountEntryUserRel.getAccountEntryUserRelId());
		Assert.assertEquals(
			existingAccountEntryUserRel.getCompanyId(),
			newAccountEntryUserRel.getCompanyId());
		Assert.assertEquals(
			existingAccountEntryUserRel.getAccountEntryId(),
			newAccountEntryUserRel.getAccountEntryId());
		Assert.assertEquals(
			existingAccountEntryUserRel.getAccountUserId(),
			newAccountEntryUserRel.getAccountUserId());
	}

	@Test
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountByAccountUserId() throws Exception {
		_persistence.countByAccountUserId(RandomTestUtil.nextLong());

		_persistence.countByAccountUserId(0L);
	}

	@Test
	public void testCountByAEI_AUI() throws Exception {
		_persistence.countByAEI_AUI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByAEI_AUI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AccountEntryUserRel newAccountEntryUserRel = addAccountEntryUserRel();

		AccountEntryUserRel existingAccountEntryUserRel =
			_persistence.findByPrimaryKey(
				newAccountEntryUserRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountEntryUserRel, newAccountEntryUserRel);
	}

	@Test(expected = NoSuchEntryUserRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AccountEntryUserRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AccountEntryUserRel", "mvccVersion", true, "accountEntryUserRelId",
			true, "companyId", true, "accountEntryId", true, "accountUserId",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AccountEntryUserRel newAccountEntryUserRel = addAccountEntryUserRel();

		AccountEntryUserRel existingAccountEntryUserRel =
			_persistence.fetchByPrimaryKey(
				newAccountEntryUserRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountEntryUserRel, newAccountEntryUserRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountEntryUserRel missingAccountEntryUserRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAccountEntryUserRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AccountEntryUserRel newAccountEntryUserRel1 = addAccountEntryUserRel();
		AccountEntryUserRel newAccountEntryUserRel2 = addAccountEntryUserRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountEntryUserRel1.getPrimaryKey());
		primaryKeys.add(newAccountEntryUserRel2.getPrimaryKey());

		Map<Serializable, AccountEntryUserRel> accountEntryUserRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, accountEntryUserRels.size());
		Assert.assertEquals(
			newAccountEntryUserRel1,
			accountEntryUserRels.get(newAccountEntryUserRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAccountEntryUserRel2,
			accountEntryUserRels.get(newAccountEntryUserRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AccountEntryUserRel> accountEntryUserRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountEntryUserRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AccountEntryUserRel newAccountEntryUserRel = addAccountEntryUserRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountEntryUserRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AccountEntryUserRel> accountEntryUserRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountEntryUserRels.size());
		Assert.assertEquals(
			newAccountEntryUserRel,
			accountEntryUserRels.get(newAccountEntryUserRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AccountEntryUserRel> accountEntryUserRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountEntryUserRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AccountEntryUserRel newAccountEntryUserRel = addAccountEntryUserRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountEntryUserRel.getPrimaryKey());

		Map<Serializable, AccountEntryUserRel> accountEntryUserRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountEntryUserRels.size());
		Assert.assertEquals(
			newAccountEntryUserRel,
			accountEntryUserRels.get(newAccountEntryUserRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AccountEntryUserRel newAccountEntryUserRel = addAccountEntryUserRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAccountEntryUserRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AccountEntryUserRel accountEntryUserRel) {

		Assert.assertEquals(
			Long.valueOf(accountEntryUserRel.getAccountEntryId()),
			ReflectionTestUtil.<Long>invoke(
				accountEntryUserRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "accountEntryId"));
		Assert.assertEquals(
			Long.valueOf(accountEntryUserRel.getAccountUserId()),
			ReflectionTestUtil.<Long>invoke(
				accountEntryUserRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "accountUserId"));
	}

	protected AccountEntryUserRel addAccountEntryUserRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountEntryUserRel accountEntryUserRel = _persistence.create(pk);

		accountEntryUserRel.setMvccVersion(RandomTestUtil.nextLong());

		accountEntryUserRel.setCompanyId(RandomTestUtil.nextLong());

		accountEntryUserRel.setAccountEntryId(RandomTestUtil.nextLong());

		accountEntryUserRel.setAccountUserId(RandomTestUtil.nextLong());

		_accountEntryUserRels.add(_persistence.update(accountEntryUserRel));

		return accountEntryUserRel;
	}

	private List<AccountEntryUserRel> _accountEntryUserRels =
		new ArrayList<AccountEntryUserRel>();
	private AccountEntryUserRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}