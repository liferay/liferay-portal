/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.test;

import com.liferay.account.exception.NoSuchGroupRelException;
import com.liferay.account.model.AccountGroupRel;
import com.liferay.account.service.AccountGroupRelLocalServiceUtil;
import com.liferay.account.service.persistence.AccountGroupRelPersistence;
import com.liferay.account.service.persistence.AccountGroupRelUtil;
import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class AccountGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.account.service"));

	@Before
	public void setUp() {
		_persistence = AccountGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AccountGroupRel> iterator = _accountGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroupRel accountGroupRel = _persistence.create(pk);

		Assert.assertNotNull(accountGroupRel);

		Assert.assertEquals(accountGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		_persistence.remove(newAccountGroupRel);

		AccountGroupRel existingAccountGroupRel =
			_persistence.fetchByPrimaryKey(newAccountGroupRel.getPrimaryKey());

		Assert.assertNull(existingAccountGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAccountGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		newAccountGroupRel.setCompanyId(RandomTestUtil.nextLong());

		newAccountGroupRel.setUserId(RandomTestUtil.nextLong());

		newAccountGroupRel.setUserName(RandomTestUtil.randomString());

		newAccountGroupRel.setCreateDate(RandomTestUtil.nextDate());

		newAccountGroupRel.setModifiedDate(RandomTestUtil.nextDate());

		newAccountGroupRel.setAccountGroupId(RandomTestUtil.nextLong());

		newAccountGroupRel.setClassNameId(RandomTestUtil.nextLong());

		newAccountGroupRel.setClassPK(RandomTestUtil.nextLong());

		newAccountGroupRel = _persistence.update(newAccountGroupRel);

		_accountGroupRels.add(newAccountGroupRel);

		AccountGroupRel existingAccountGroupRel = _persistence.findByPrimaryKey(
			newAccountGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountGroupRel.getMvccVersion(),
			newAccountGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingAccountGroupRel.getAccountGroupRelId(),
			newAccountGroupRel.getAccountGroupRelId());
		Assert.assertEquals(
			existingAccountGroupRel.getCompanyId(),
			newAccountGroupRel.getCompanyId());
		Assert.assertEquals(
			existingAccountGroupRel.getUserId(),
			newAccountGroupRel.getUserId());
		Assert.assertEquals(
			existingAccountGroupRel.getUserName(),
			newAccountGroupRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingAccountGroupRel.getCreateDate()),
			Time.getShortTimestamp(newAccountGroupRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingAccountGroupRel.getModifiedDate()),
			Time.getShortTimestamp(newAccountGroupRel.getModifiedDate()));
		Assert.assertEquals(
			existingAccountGroupRel.getAccountGroupId(),
			newAccountGroupRel.getAccountGroupId());
		Assert.assertEquals(
			existingAccountGroupRel.getClassNameId(),
			newAccountGroupRel.getClassNameId());
		Assert.assertEquals(
			existingAccountGroupRel.getClassPK(),
			newAccountGroupRel.getClassPK());
	}

	@Test
	public void testCountByAccountGroupId() throws Exception {
		_persistence.countByAccountGroupId(RandomTestUtil.nextLong());

		_persistence.countByAccountGroupId(0L);
	}

	@Test
	public void testCountByA_C() throws Exception {
		_persistence.countByA_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_C(0L, 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByA_C_C() throws Exception {
		_persistence.countByA_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByA_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		AccountGroupRel existingAccountGroupRel = _persistence.findByPrimaryKey(
			newAccountGroupRel.getPrimaryKey());

		Assert.assertEquals(existingAccountGroupRel, newAccountGroupRel);
	}

	@Test(expected = NoSuchGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AccountGroupRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AccountGroupRel", "mvccVersion", true, "accountGroupRelId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "accountGroupId", true, "classNameId",
			true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		AccountGroupRel existingAccountGroupRel =
			_persistence.fetchByPrimaryKey(newAccountGroupRel.getPrimaryKey());

		Assert.assertEquals(existingAccountGroupRel, newAccountGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroupRel missingAccountGroupRel = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingAccountGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AccountGroupRel newAccountGroupRel1 = addAccountGroupRel();
		AccountGroupRel newAccountGroupRel2 = addAccountGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountGroupRel1.getPrimaryKey());
		primaryKeys.add(newAccountGroupRel2.getPrimaryKey());

		Map<Serializable, AccountGroupRel> accountGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, accountGroupRels.size());
		Assert.assertEquals(
			newAccountGroupRel1,
			accountGroupRels.get(newAccountGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAccountGroupRel2,
			accountGroupRels.get(newAccountGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AccountGroupRel> accountGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AccountGroupRel> accountGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountGroupRels.size());
		Assert.assertEquals(
			newAccountGroupRel,
			accountGroupRels.get(newAccountGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AccountGroupRel> accountGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(accountGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountGroupRel.getPrimaryKey());

		Map<Serializable, AccountGroupRel> accountGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, accountGroupRels.size());
		Assert.assertEquals(
			newAccountGroupRel,
			accountGroupRels.get(newAccountGroupRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AccountGroupRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<AccountGroupRel>() {

				@Override
				public void performAction(AccountGroupRel accountGroupRel) {
					Assert.assertNotNull(accountGroupRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"accountGroupRelId",
				newAccountGroupRel.getAccountGroupRelId()));

		List<AccountGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		AccountGroupRel existingAccountGroupRel = result.get(0);

		Assert.assertEquals(existingAccountGroupRel, newAccountGroupRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"accountGroupRelId", RandomTestUtil.nextLong()));

		List<AccountGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("accountGroupRelId"));

		Object newAccountGroupRelId = newAccountGroupRel.getAccountGroupRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"accountGroupRelId", new Object[] {newAccountGroupRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAccountGroupRelId = result.get(0);

		Assert.assertEquals(existingAccountGroupRelId, newAccountGroupRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("accountGroupRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"accountGroupRelId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newAccountGroupRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		AccountGroupRel newAccountGroupRel = addAccountGroupRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"accountGroupRelId",
				newAccountGroupRel.getAccountGroupRelId()));

		List<AccountGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(AccountGroupRel accountGroupRel) {
		Assert.assertEquals(
			Long.valueOf(accountGroupRel.getAccountGroupId()),
			ReflectionTestUtil.<Long>invoke(
				accountGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "accountGroupId"));
		Assert.assertEquals(
			Long.valueOf(accountGroupRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				accountGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(accountGroupRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				accountGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected AccountGroupRel addAccountGroupRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountGroupRel accountGroupRel = _persistence.create(pk);

		accountGroupRel.setCompanyId(RandomTestUtil.nextLong());

		accountGroupRel.setUserId(RandomTestUtil.nextLong());

		accountGroupRel.setUserName(RandomTestUtil.randomString());

		accountGroupRel.setCreateDate(RandomTestUtil.nextDate());

		accountGroupRel.setModifiedDate(RandomTestUtil.nextDate());

		accountGroupRel.setAccountGroupId(RandomTestUtil.nextLong());

		accountGroupRel.setClassNameId(RandomTestUtil.nextLong());

		accountGroupRel.setClassPK(RandomTestUtil.nextLong());

		_accountGroupRels.add(_persistence.update(accountGroupRel));

		return accountGroupRel;
	}

	private List<AccountGroupRel> _accountGroupRels =
		new ArrayList<AccountGroupRel>();
	private AccountGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:716366221