/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.test;

import com.liferay.account.exception.DuplicateAccountGroupExternalReferenceCodeException;
import com.liferay.account.exception.NoSuchGroupException;
import com.liferay.account.model.AccountGroup;
import com.liferay.account.service.persistence.AccountGroupPersistence;
import com.liferay.account.service.persistence.AccountGroupUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class AccountGroupPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.account.service"));

	@Before
	public void setUp() {
		_persistence = AccountGroupUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AccountGroup> iterator = _accountGroups.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroup accountGroup = _persistence.create(pk);

		Assert.assertNotNull(accountGroup);

		Assert.assertEquals(accountGroup.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AccountGroup newAccountGroup = addAccountGroup();

		_persistence.remove(newAccountGroup);

		AccountGroup existingAccountGroup = _persistence.fetchByPrimaryKey(
			newAccountGroup.getPrimaryKey());

		Assert.assertNull(existingAccountGroup);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAccountGroup();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroup newAccountGroup = _persistence.create(pk);

		newAccountGroup.setMvccVersion(RandomTestUtil.nextLong());

		newAccountGroup.setUuid(RandomTestUtil.randomString());

		newAccountGroup.setExternalReferenceCode(RandomTestUtil.randomString());

		newAccountGroup.setCompanyId(RandomTestUtil.nextLong());

		newAccountGroup.setUserId(RandomTestUtil.nextLong());

		newAccountGroup.setUserName(RandomTestUtil.randomString());

		newAccountGroup.setCreateDate(RandomTestUtil.nextDate());

		newAccountGroup.setModifiedDate(RandomTestUtil.nextDate());

		newAccountGroup.setDefaultAccountGroup(RandomTestUtil.randomBoolean());

		newAccountGroup.setDescription(RandomTestUtil.randomString());

		newAccountGroup.setName(RandomTestUtil.randomString());

		newAccountGroup.setType(RandomTestUtil.randomString());

		newAccountGroup.setStatus(RandomTestUtil.nextInt());

		_accountGroups.add(_persistence.update(newAccountGroup));

		AccountGroup existingAccountGroup = _persistence.findByPrimaryKey(
			newAccountGroup.getPrimaryKey());

		Assert.assertEquals(
			existingAccountGroup.getMvccVersion(),
			newAccountGroup.getMvccVersion());
		Assert.assertEquals(
			existingAccountGroup.getUuid(), newAccountGroup.getUuid());
		Assert.assertEquals(
			existingAccountGroup.getExternalReferenceCode(),
			newAccountGroup.getExternalReferenceCode());
		Assert.assertEquals(
			existingAccountGroup.getAccountGroupId(),
			newAccountGroup.getAccountGroupId());
		Assert.assertEquals(
			existingAccountGroup.getCompanyId(),
			newAccountGroup.getCompanyId());
		Assert.assertEquals(
			existingAccountGroup.getUserId(), newAccountGroup.getUserId());
		Assert.assertEquals(
			existingAccountGroup.getUserName(), newAccountGroup.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAccountGroup.getCreateDate()),
			Time.getShortTimestamp(newAccountGroup.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingAccountGroup.getModifiedDate()),
			Time.getShortTimestamp(newAccountGroup.getModifiedDate()));
		Assert.assertEquals(
			existingAccountGroup.isDefaultAccountGroup(),
			newAccountGroup.isDefaultAccountGroup());
		Assert.assertEquals(
			existingAccountGroup.getDescription(),
			newAccountGroup.getDescription());
		Assert.assertEquals(
			existingAccountGroup.getName(), newAccountGroup.getName());
		Assert.assertEquals(
			existingAccountGroup.getType(), newAccountGroup.getType());
		Assert.assertEquals(
			existingAccountGroup.getStatus(), newAccountGroup.getStatus());
	}

	@Test(expected = DuplicateAccountGroupExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AccountGroup accountGroup = addAccountGroup();

		AccountGroup newAccountGroup = addAccountGroup();

		newAccountGroup.setCompanyId(accountGroup.getCompanyId());

		newAccountGroup = _persistence.update(newAccountGroup);

		Session session = _persistence.getCurrentSession();

		session.evict(newAccountGroup);

		newAccountGroup.setExternalReferenceCode(
			accountGroup.getExternalReferenceCode());

		_persistence.update(newAccountGroup);
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
	public void testCountByAccountGroupId() throws Exception {
		_persistence.countByAccountGroupId(RandomTestUtil.nextLong());

		_persistence.countByAccountGroupId(0L);
	}

	@Test
	public void testCountByAccountGroupIdArrayable() throws Exception {
		_persistence.countByAccountGroupId(
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_D() throws Exception {
		_persistence.countByC_D(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_D(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_LikeN() throws Exception {
		_persistence.countByC_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeN(0L, "null");

		_persistence.countByC_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(RandomTestUtil.nextLong(), "");

		_persistence.countByC_T(0L, "null");

		_persistence.countByC_T(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AccountGroup newAccountGroup = addAccountGroup();

		AccountGroup existingAccountGroup = _persistence.findByPrimaryKey(
			newAccountGroup.getPrimaryKey());

		Assert.assertEquals(existingAccountGroup, newAccountGroup);
	}

	@Test(expected = NoSuchGroupException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AccountGroup> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AccountGroup", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "accountGroupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "defaultAccountGroup", true, "description",
			true, "name", true, "type", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AccountGroup newAccountGroup = addAccountGroup();

		AccountGroup existingAccountGroup = _persistence.fetchByPrimaryKey(
			newAccountGroup.getPrimaryKey());

		Assert.assertEquals(existingAccountGroup, newAccountGroup);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroup missingAccountGroup = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAccountGroup);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AccountGroup newAccountGroup1 = addAccountGroup();
		AccountGroup newAccountGroup2 = addAccountGroup();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountGroup1.getPrimaryKey());
		primaryKeys.add(newAccountGroup2.getPrimaryKey());

		Map<Serializable, AccountGroup> accountGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, accountGroups.size());
		Assert.assertEquals(
			newAccountGroup1,
			accountGroups.get(newAccountGroup1.getPrimaryKey()));
		Assert.assertEquals(
			newAccountGroup2,
			accountGroups.get(newAccountGroup2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AccountGroup> accountGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountGroups.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AccountGroup newAccountGroup = addAccountGroup();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountGroup.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AccountGroup> accountGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountGroups.size());
		Assert.assertEquals(
			newAccountGroup,
			accountGroups.get(newAccountGroup.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AccountGroup> accountGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountGroups.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AccountGroup newAccountGroup = addAccountGroup();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountGroup.getPrimaryKey());

		Map<Serializable, AccountGroup> accountGroups =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountGroups.size());
		Assert.assertEquals(
			newAccountGroup,
			accountGroups.get(newAccountGroup.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AccountGroup newAccountGroup = addAccountGroup();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAccountGroup.getPrimaryKey()));
	}

	private void _assertOriginalValues(AccountGroup accountGroup) {
		Assert.assertEquals(
			accountGroup.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				accountGroup, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(accountGroup.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				accountGroup, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected AccountGroup addAccountGroup() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroup accountGroup = _persistence.create(pk);

		accountGroup.setMvccVersion(RandomTestUtil.nextLong());

		accountGroup.setUuid(RandomTestUtil.randomString());

		accountGroup.setExternalReferenceCode(RandomTestUtil.randomString());

		accountGroup.setCompanyId(RandomTestUtil.nextLong());

		accountGroup.setUserId(RandomTestUtil.nextLong());

		accountGroup.setUserName(RandomTestUtil.randomString());

		accountGroup.setCreateDate(RandomTestUtil.nextDate());

		accountGroup.setModifiedDate(RandomTestUtil.nextDate());

		accountGroup.setDefaultAccountGroup(RandomTestUtil.randomBoolean());

		accountGroup.setDescription(RandomTestUtil.randomString());

		accountGroup.setName(RandomTestUtil.randomString());

		accountGroup.setType(RandomTestUtil.randomString());

		accountGroup.setStatus(RandomTestUtil.nextInt());

		_accountGroups.add(_persistence.update(accountGroup));

		return accountGroup;
	}

	private List<AccountGroup> _accountGroups = new ArrayList<AccountGroup>();
	private AccountGroupPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}