/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherAccountException;
import com.liferay.osb.patcher.model.PatcherAccount;
import com.liferay.osb.patcher.service.PatcherAccountLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherAccountPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherAccountUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
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
public class PatcherAccountPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherAccountUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherAccount> iterator = _patcherAccounts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherAccount patcherAccount = _persistence.create(pk);

		Assert.assertNotNull(patcherAccount);

		Assert.assertEquals(patcherAccount.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherAccount newPatcherAccount = addPatcherAccount();

		_persistence.remove(newPatcherAccount);

		PatcherAccount existingPatcherAccount = _persistence.fetchByPrimaryKey(
			newPatcherAccount.getPrimaryKey());

		Assert.assertNull(existingPatcherAccount);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherAccount();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherAccount newPatcherAccount = _persistence.create(pk);

		newPatcherAccount.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherAccount.setCompanyId(RandomTestUtil.nextLong());

		newPatcherAccount.setUserId(RandomTestUtil.nextLong());

		newPatcherAccount.setUserName(RandomTestUtil.randomString());

		newPatcherAccount.setCreateDate(RandomTestUtil.nextDate());

		newPatcherAccount.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherAccount.setAccountEntryId(RandomTestUtil.nextLong());

		newPatcherAccount.setAccountEntryCode(RandomTestUtil.randomString());

		_patcherAccounts.add(_persistence.update(newPatcherAccount));

		PatcherAccount existingPatcherAccount = _persistence.findByPrimaryKey(
			newPatcherAccount.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherAccount.getMvccVersion(),
			newPatcherAccount.getMvccVersion());
		Assert.assertEquals(
			existingPatcherAccount.getPatcherAccountId(),
			newPatcherAccount.getPatcherAccountId());
		Assert.assertEquals(
			existingPatcherAccount.getCompanyId(),
			newPatcherAccount.getCompanyId());
		Assert.assertEquals(
			existingPatcherAccount.getUserId(), newPatcherAccount.getUserId());
		Assert.assertEquals(
			existingPatcherAccount.getUserName(),
			newPatcherAccount.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherAccount.getCreateDate()),
			Time.getShortTimestamp(newPatcherAccount.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherAccount.getModifiedDate()),
			Time.getShortTimestamp(newPatcherAccount.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherAccount.getAccountEntryId(),
			newPatcherAccount.getAccountEntryId());
		Assert.assertEquals(
			existingPatcherAccount.getAccountEntryCode(),
			newPatcherAccount.getAccountEntryCode());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherAccount newPatcherAccount = addPatcherAccount();

		PatcherAccount existingPatcherAccount = _persistence.findByPrimaryKey(
			newPatcherAccount.getPrimaryKey());

		Assert.assertEquals(existingPatcherAccount, newPatcherAccount);
	}

	@Test(expected = NoSuchPatcherAccountException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherAccount> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherAccount", "mvccVersion", true,
			"patcherAccountId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"accountEntryId", true, "accountEntryCode", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherAccount newPatcherAccount = addPatcherAccount();

		PatcherAccount existingPatcherAccount = _persistence.fetchByPrimaryKey(
			newPatcherAccount.getPrimaryKey());

		Assert.assertEquals(existingPatcherAccount, newPatcherAccount);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherAccount missingPatcherAccount = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingPatcherAccount);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherAccount newPatcherAccount1 = addPatcherAccount();
		PatcherAccount newPatcherAccount2 = addPatcherAccount();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherAccount1.getPrimaryKey());
		primaryKeys.add(newPatcherAccount2.getPrimaryKey());

		Map<Serializable, PatcherAccount> patcherAccounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherAccounts.size());
		Assert.assertEquals(
			newPatcherAccount1,
			patcherAccounts.get(newPatcherAccount1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherAccount2,
			patcherAccounts.get(newPatcherAccount2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherAccount> patcherAccounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherAccounts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherAccount newPatcherAccount = addPatcherAccount();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherAccount.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherAccount> patcherAccounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherAccounts.size());
		Assert.assertEquals(
			newPatcherAccount,
			patcherAccounts.get(newPatcherAccount.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherAccount> patcherAccounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherAccounts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherAccount newPatcherAccount = addPatcherAccount();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherAccount.getPrimaryKey());

		Map<Serializable, PatcherAccount> patcherAccounts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherAccounts.size());
		Assert.assertEquals(
			newPatcherAccount,
			patcherAccounts.get(newPatcherAccount.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherAccountLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<PatcherAccount>() {

				@Override
				public void performAction(PatcherAccount patcherAccount) {
					Assert.assertNotNull(patcherAccount);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherAccount newPatcherAccount = addPatcherAccount();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherAccount.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherAccountId", newPatcherAccount.getPatcherAccountId()));

		List<PatcherAccount> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherAccount existingPatcherAccount = result.get(0);

		Assert.assertEquals(existingPatcherAccount, newPatcherAccount);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherAccount.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherAccountId", RandomTestUtil.nextLong()));

		List<PatcherAccount> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherAccount newPatcherAccount = addPatcherAccount();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherAccount.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherAccountId"));

		Object newPatcherAccountId = newPatcherAccount.getPatcherAccountId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherAccountId", new Object[] {newPatcherAccountId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherAccountId = result.get(0);

		Assert.assertEquals(existingPatcherAccountId, newPatcherAccountId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherAccount.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherAccountId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherAccountId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherAccount addPatcherAccount() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherAccount patcherAccount = _persistence.create(pk);

		patcherAccount.setMvccVersion(RandomTestUtil.nextLong());

		patcherAccount.setCompanyId(RandomTestUtil.nextLong());

		patcherAccount.setUserId(RandomTestUtil.nextLong());

		patcherAccount.setUserName(RandomTestUtil.randomString());

		patcherAccount.setCreateDate(RandomTestUtil.nextDate());

		patcherAccount.setModifiedDate(RandomTestUtil.nextDate());

		patcherAccount.setAccountEntryId(RandomTestUtil.nextLong());

		patcherAccount.setAccountEntryCode(RandomTestUtil.randomString());

		_patcherAccounts.add(_persistence.update(patcherAccount));

		return patcherAccount;
	}

	private List<PatcherAccount> _patcherAccounts =
		new ArrayList<PatcherAccount>();
	private PatcherAccountPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}