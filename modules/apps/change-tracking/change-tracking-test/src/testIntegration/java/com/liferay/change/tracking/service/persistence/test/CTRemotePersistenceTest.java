/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchRemoteException;
import com.liferay.change.tracking.model.CTRemote;
import com.liferay.change.tracking.service.persistence.CTRemotePersistence;
import com.liferay.change.tracking.service.persistence.CTRemoteUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CTRemotePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTRemoteUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTRemote> iterator = _ctRemotes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTRemote ctRemote = _persistence.create(pk);

		Assert.assertNotNull(ctRemote);

		Assert.assertEquals(ctRemote.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTRemote newCTRemote = addCTRemote();

		_persistence.remove(newCTRemote);

		CTRemote existingCTRemote = _persistence.fetchByPrimaryKey(
			newCTRemote.getPrimaryKey());

		Assert.assertNull(existingCTRemote);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTRemote();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTRemote newCTRemote = _persistence.create(pk);

		newCTRemote.setMvccVersion(RandomTestUtil.nextLong());

		newCTRemote.setCompanyId(RandomTestUtil.nextLong());

		newCTRemote.setUserId(RandomTestUtil.nextLong());

		newCTRemote.setCreateDate(RandomTestUtil.nextDate());

		newCTRemote.setModifiedDate(RandomTestUtil.nextDate());

		newCTRemote.setName(RandomTestUtil.randomString());

		newCTRemote.setDescription(RandomTestUtil.randomString());

		newCTRemote.setUrl(RandomTestUtil.randomString());

		newCTRemote.setClientId(RandomTestUtil.randomString());

		newCTRemote.setClientSecret(RandomTestUtil.randomString());

		_ctRemotes.add(_persistence.update(newCTRemote));

		CTRemote existingCTRemote = _persistence.findByPrimaryKey(
			newCTRemote.getPrimaryKey());

		Assert.assertEquals(
			existingCTRemote.getMvccVersion(), newCTRemote.getMvccVersion());
		Assert.assertEquals(
			existingCTRemote.getCtRemoteId(), newCTRemote.getCtRemoteId());
		Assert.assertEquals(
			existingCTRemote.getCompanyId(), newCTRemote.getCompanyId());
		Assert.assertEquals(
			existingCTRemote.getUserId(), newCTRemote.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTRemote.getCreateDate()),
			Time.getShortTimestamp(newCTRemote.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTRemote.getModifiedDate()),
			Time.getShortTimestamp(newCTRemote.getModifiedDate()));
		Assert.assertEquals(existingCTRemote.getName(), newCTRemote.getName());
		Assert.assertEquals(
			existingCTRemote.getDescription(), newCTRemote.getDescription());
		Assert.assertEquals(existingCTRemote.getUrl(), newCTRemote.getUrl());
		Assert.assertEquals(
			existingCTRemote.getClientId(), newCTRemote.getClientId());
		Assert.assertEquals(
			existingCTRemote.getClientSecret(), newCTRemote.getClientSecret());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTRemote newCTRemote = addCTRemote();

		CTRemote existingCTRemote = _persistence.findByPrimaryKey(
			newCTRemote.getPrimaryKey());

		Assert.assertEquals(existingCTRemote, newCTRemote);
	}

	@Test(expected = NoSuchRemoteException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTRemote> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTRemote", "mvccVersion", true, "ctRemoteId", true, "companyId",
			true, "userId", true, "createDate", true, "modifiedDate", true,
			"name", true, "description", true, "url", true, "clientId", true,
			"clientSecret", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTRemote newCTRemote = addCTRemote();

		CTRemote existingCTRemote = _persistence.fetchByPrimaryKey(
			newCTRemote.getPrimaryKey());

		Assert.assertEquals(existingCTRemote, newCTRemote);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTRemote missingCTRemote = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTRemote);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTRemote newCTRemote1 = addCTRemote();
		CTRemote newCTRemote2 = addCTRemote();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTRemote1.getPrimaryKey());
		primaryKeys.add(newCTRemote2.getPrimaryKey());

		Map<Serializable, CTRemote> ctRemotes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, ctRemotes.size());
		Assert.assertEquals(
			newCTRemote1, ctRemotes.get(newCTRemote1.getPrimaryKey()));
		Assert.assertEquals(
			newCTRemote2, ctRemotes.get(newCTRemote2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTRemote> ctRemotes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctRemotes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTRemote newCTRemote = addCTRemote();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTRemote.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTRemote> ctRemotes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctRemotes.size());
		Assert.assertEquals(
			newCTRemote, ctRemotes.get(newCTRemote.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTRemote> ctRemotes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctRemotes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTRemote newCTRemote = addCTRemote();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTRemote.getPrimaryKey());

		Map<Serializable, CTRemote> ctRemotes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctRemotes.size());
		Assert.assertEquals(
			newCTRemote, ctRemotes.get(newCTRemote.getPrimaryKey()));
	}

	protected CTRemote addCTRemote() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTRemote ctRemote = _persistence.create(pk);

		ctRemote.setMvccVersion(RandomTestUtil.nextLong());

		ctRemote.setCompanyId(RandomTestUtil.nextLong());

		ctRemote.setUserId(RandomTestUtil.nextLong());

		ctRemote.setCreateDate(RandomTestUtil.nextDate());

		ctRemote.setModifiedDate(RandomTestUtil.nextDate());

		ctRemote.setName(RandomTestUtil.randomString());

		ctRemote.setDescription(RandomTestUtil.randomString());

		ctRemote.setUrl(RandomTestUtil.randomString());

		ctRemote.setClientId(RandomTestUtil.randomString());

		ctRemote.setClientSecret(RandomTestUtil.randomString());

		_ctRemotes.add(_persistence.update(ctRemote));

		return ctRemote;
	}

	private List<CTRemote> _ctRemotes = new ArrayList<CTRemote>();
	private CTRemotePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}