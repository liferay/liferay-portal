/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.account.service.persistence.test;

import com.liferay.account.exception.NoSuchEntryOrganizationRelException;
import com.liferay.account.model.AccountEntryOrganizationRel;
import com.liferay.account.service.AccountEntryOrganizationRelLocalServiceUtil;
import com.liferay.account.service.persistence.AccountEntryOrganizationRelPersistence;
import com.liferay.account.service.persistence.AccountEntryOrganizationRelUtil;
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
public class AccountEntryOrganizationRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.account.service"));

	@Before
	public void setUp() {
		_persistence = AccountEntryOrganizationRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AccountEntryOrganizationRel> iterator =
			_accountEntryOrganizationRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_persistence.create(pk);

		Assert.assertNotNull(accountEntryOrganizationRel);

		Assert.assertEquals(accountEntryOrganizationRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		_persistence.remove(newAccountEntryOrganizationRel);

		AccountEntryOrganizationRel existingAccountEntryOrganizationRel =
			_persistence.fetchByPrimaryKey(
				newAccountEntryOrganizationRel.getPrimaryKey());

		Assert.assertNull(existingAccountEntryOrganizationRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAccountEntryOrganizationRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		newAccountEntryOrganizationRel.setCompanyId(RandomTestUtil.nextLong());

		newAccountEntryOrganizationRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		newAccountEntryOrganizationRel.setOrganizationId(
			RandomTestUtil.nextLong());

		newAccountEntryOrganizationRel = _persistence.update(
			newAccountEntryOrganizationRel);

		_accountEntryOrganizationRels.add(newAccountEntryOrganizationRel);

		AccountEntryOrganizationRel existingAccountEntryOrganizationRel =
			_persistence.findByPrimaryKey(
				newAccountEntryOrganizationRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountEntryOrganizationRel.getMvccVersion(),
			newAccountEntryOrganizationRel.getMvccVersion());
		Assert.assertEquals(
			existingAccountEntryOrganizationRel.
				getAccountEntryOrganizationRelId(),
			newAccountEntryOrganizationRel.getAccountEntryOrganizationRelId());
		Assert.assertEquals(
			existingAccountEntryOrganizationRel.getCompanyId(),
			newAccountEntryOrganizationRel.getCompanyId());
		Assert.assertEquals(
			existingAccountEntryOrganizationRel.getAccountEntryId(),
			newAccountEntryOrganizationRel.getAccountEntryId());
		Assert.assertEquals(
			existingAccountEntryOrganizationRel.getOrganizationId(),
			newAccountEntryOrganizationRel.getOrganizationId());
	}

	@Test
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountByOrganizationId() throws Exception {
		_persistence.countByOrganizationId(RandomTestUtil.nextLong());

		_persistence.countByOrganizationId(0L);
	}

	@Test
	public void testCountByA_O() throws Exception {
		_persistence.countByA_O(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_O(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		AccountEntryOrganizationRel existingAccountEntryOrganizationRel =
			_persistence.findByPrimaryKey(
				newAccountEntryOrganizationRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountEntryOrganizationRel,
			newAccountEntryOrganizationRel);
	}

	@Test(expected = NoSuchEntryOrganizationRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AccountEntryOrganizationRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"AccountEntryOrganizationRel", "mvccVersion", true,
			"accountEntryOrganizationRelId", true, "companyId", true,
			"accountEntryId", true, "organizationId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		AccountEntryOrganizationRel existingAccountEntryOrganizationRel =
			_persistence.fetchByPrimaryKey(
				newAccountEntryOrganizationRel.getPrimaryKey());

		Assert.assertEquals(
			existingAccountEntryOrganizationRel,
			newAccountEntryOrganizationRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AccountEntryOrganizationRel missingAccountEntryOrganizationRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAccountEntryOrganizationRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AccountEntryOrganizationRel newAccountEntryOrganizationRel1 =
			addAccountEntryOrganizationRel();
		AccountEntryOrganizationRel newAccountEntryOrganizationRel2 =
			addAccountEntryOrganizationRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountEntryOrganizationRel1.getPrimaryKey());
		primaryKeys.add(newAccountEntryOrganizationRel2.getPrimaryKey());

		Map<Serializable, AccountEntryOrganizationRel>
			accountEntryOrganizationRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, accountEntryOrganizationRels.size());
		Assert.assertEquals(
			newAccountEntryOrganizationRel1,
			accountEntryOrganizationRels.get(
				newAccountEntryOrganizationRel1.getPrimaryKey()));
		Assert.assertEquals(
			newAccountEntryOrganizationRel2,
			accountEntryOrganizationRels.get(
				newAccountEntryOrganizationRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AccountEntryOrganizationRel>
			accountEntryOrganizationRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(accountEntryOrganizationRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountEntryOrganizationRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AccountEntryOrganizationRel>
			accountEntryOrganizationRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, accountEntryOrganizationRels.size());
		Assert.assertEquals(
			newAccountEntryOrganizationRel,
			accountEntryOrganizationRels.get(
				newAccountEntryOrganizationRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AccountEntryOrganizationRel>
			accountEntryOrganizationRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(accountEntryOrganizationRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAccountEntryOrganizationRel.getPrimaryKey());

		Map<Serializable, AccountEntryOrganizationRel>
			accountEntryOrganizationRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, accountEntryOrganizationRels.size());
		Assert.assertEquals(
			newAccountEntryOrganizationRel,
			accountEntryOrganizationRels.get(
				newAccountEntryOrganizationRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			AccountEntryOrganizationRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<AccountEntryOrganizationRel>() {

				@Override
				public void performAction(
					AccountEntryOrganizationRel accountEntryOrganizationRel) {

					Assert.assertNotNull(accountEntryOrganizationRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountEntryOrganizationRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"accountEntryOrganizationRelId",
				newAccountEntryOrganizationRel.
					getAccountEntryOrganizationRelId()));

		List<AccountEntryOrganizationRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		AccountEntryOrganizationRel existingAccountEntryOrganizationRel =
			result.get(0);

		Assert.assertEquals(
			existingAccountEntryOrganizationRel,
			newAccountEntryOrganizationRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountEntryOrganizationRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"accountEntryOrganizationRelId", RandomTestUtil.nextLong()));

		List<AccountEntryOrganizationRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountEntryOrganizationRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("accountEntryOrganizationRelId"));

		Object newAccountEntryOrganizationRelId =
			newAccountEntryOrganizationRel.getAccountEntryOrganizationRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"accountEntryOrganizationRelId",
				new Object[] {newAccountEntryOrganizationRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingAccountEntryOrganizationRelId = result.get(0);

		Assert.assertEquals(
			existingAccountEntryOrganizationRelId,
			newAccountEntryOrganizationRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountEntryOrganizationRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("accountEntryOrganizationRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"accountEntryOrganizationRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAccountEntryOrganizationRel.getPrimaryKey()));
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

		AccountEntryOrganizationRel newAccountEntryOrganizationRel =
			addAccountEntryOrganizationRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			AccountEntryOrganizationRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"accountEntryOrganizationRelId",
				newAccountEntryOrganizationRel.
					getAccountEntryOrganizationRelId()));

		List<AccountEntryOrganizationRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		AccountEntryOrganizationRel accountEntryOrganizationRel) {

		Assert.assertEquals(
			Long.valueOf(accountEntryOrganizationRel.getAccountEntryId()),
			ReflectionTestUtil.<Long>invoke(
				accountEntryOrganizationRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "accountEntryId"));
		Assert.assertEquals(
			Long.valueOf(accountEntryOrganizationRel.getOrganizationId()),
			ReflectionTestUtil.<Long>invoke(
				accountEntryOrganizationRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "organizationId"));
	}

	protected AccountEntryOrganizationRel addAccountEntryOrganizationRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AccountEntryOrganizationRel accountEntryOrganizationRel =
			_persistence.create(pk);

		accountEntryOrganizationRel.setCompanyId(RandomTestUtil.nextLong());

		accountEntryOrganizationRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		accountEntryOrganizationRel.setOrganizationId(
			RandomTestUtil.nextLong());

		_accountEntryOrganizationRels.add(
			_persistence.update(accountEntryOrganizationRel));

		return accountEntryOrganizationRel;
	}

	private List<AccountEntryOrganizationRel> _accountEntryOrganizationRels =
		new ArrayList<AccountEntryOrganizationRel>();
	private AccountEntryOrganizationRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-368067776