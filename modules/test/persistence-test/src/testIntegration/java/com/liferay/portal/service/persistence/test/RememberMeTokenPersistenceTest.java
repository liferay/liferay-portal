/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchRememberMeTokenException;
import com.liferay.portal.kernel.model.RememberMeToken;
import com.liferay.portal.kernel.service.persistence.RememberMeTokenPersistence;
import com.liferay.portal.kernel.service.persistence.RememberMeTokenUtil;
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
public class RememberMeTokenPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RememberMeTokenUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RememberMeToken> iterator = _rememberMeTokens.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RememberMeToken rememberMeToken = _persistence.create(pk);

		Assert.assertNotNull(rememberMeToken);

		Assert.assertEquals(rememberMeToken.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RememberMeToken newRememberMeToken = addRememberMeToken();

		_persistence.remove(newRememberMeToken);

		RememberMeToken existingRememberMeToken =
			_persistence.fetchByPrimaryKey(newRememberMeToken.getPrimaryKey());

		Assert.assertNull(existingRememberMeToken);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRememberMeToken();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RememberMeToken newRememberMeToken = _persistence.create(pk);

		newRememberMeToken.setMvccVersion(RandomTestUtil.nextLong());

		newRememberMeToken.setCompanyId(RandomTestUtil.nextLong());

		newRememberMeToken.setUserId(RandomTestUtil.nextLong());

		newRememberMeToken.setCreateDate(RandomTestUtil.nextDate());

		newRememberMeToken.setExpirationDate(RandomTestUtil.nextDate());

		newRememberMeToken.setValue(RandomTestUtil.randomString());

		_rememberMeTokens.add(_persistence.update(newRememberMeToken));

		RememberMeToken existingRememberMeToken = _persistence.findByPrimaryKey(
			newRememberMeToken.getPrimaryKey());

		Assert.assertEquals(
			existingRememberMeToken.getMvccVersion(),
			newRememberMeToken.getMvccVersion());
		Assert.assertEquals(
			existingRememberMeToken.getRememberMeTokenId(),
			newRememberMeToken.getRememberMeTokenId());
		Assert.assertEquals(
			existingRememberMeToken.getCompanyId(),
			newRememberMeToken.getCompanyId());
		Assert.assertEquals(
			existingRememberMeToken.getUserId(),
			newRememberMeToken.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRememberMeToken.getCreateDate()),
			Time.getShortTimestamp(newRememberMeToken.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingRememberMeToken.getExpirationDate()),
			Time.getShortTimestamp(newRememberMeToken.getExpirationDate()));
		Assert.assertEquals(
			existingRememberMeToken.getValue(), newRememberMeToken.getValue());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByLteExpirationDate() throws Exception {
		_persistence.countByLteExpirationDate(RandomTestUtil.nextDate());

		_persistence.countByLteExpirationDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RememberMeToken newRememberMeToken = addRememberMeToken();

		RememberMeToken existingRememberMeToken = _persistence.findByPrimaryKey(
			newRememberMeToken.getPrimaryKey());

		Assert.assertEquals(existingRememberMeToken, newRememberMeToken);
	}

	@Test(expected = NoSuchRememberMeTokenException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RememberMeToken> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RememberMeToken", "mvccVersion", true, "rememberMeTokenId", true,
			"companyId", true, "userId", true, "createDate", true,
			"expirationDate", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RememberMeToken newRememberMeToken = addRememberMeToken();

		RememberMeToken existingRememberMeToken =
			_persistence.fetchByPrimaryKey(newRememberMeToken.getPrimaryKey());

		Assert.assertEquals(existingRememberMeToken, newRememberMeToken);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RememberMeToken missingRememberMeToken = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingRememberMeToken);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RememberMeToken newRememberMeToken1 = addRememberMeToken();
		RememberMeToken newRememberMeToken2 = addRememberMeToken();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRememberMeToken1.getPrimaryKey());
		primaryKeys.add(newRememberMeToken2.getPrimaryKey());

		Map<Serializable, RememberMeToken> rememberMeTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, rememberMeTokens.size());
		Assert.assertEquals(
			newRememberMeToken1,
			rememberMeTokens.get(newRememberMeToken1.getPrimaryKey()));
		Assert.assertEquals(
			newRememberMeToken2,
			rememberMeTokens.get(newRememberMeToken2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RememberMeToken> rememberMeTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(rememberMeTokens.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RememberMeToken newRememberMeToken = addRememberMeToken();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRememberMeToken.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RememberMeToken> rememberMeTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, rememberMeTokens.size());
		Assert.assertEquals(
			newRememberMeToken,
			rememberMeTokens.get(newRememberMeToken.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RememberMeToken> rememberMeTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(rememberMeTokens.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RememberMeToken newRememberMeToken = addRememberMeToken();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRememberMeToken.getPrimaryKey());

		Map<Serializable, RememberMeToken> rememberMeTokens =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, rememberMeTokens.size());
		Assert.assertEquals(
			newRememberMeToken,
			rememberMeTokens.get(newRememberMeToken.getPrimaryKey()));
	}

	protected RememberMeToken addRememberMeToken() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RememberMeToken rememberMeToken = _persistence.create(pk);

		rememberMeToken.setMvccVersion(RandomTestUtil.nextLong());

		rememberMeToken.setCompanyId(RandomTestUtil.nextLong());

		rememberMeToken.setUserId(RandomTestUtil.nextLong());

		rememberMeToken.setCreateDate(RandomTestUtil.nextDate());

		rememberMeToken.setExpirationDate(RandomTestUtil.nextDate());

		rememberMeToken.setValue(RandomTestUtil.randomString());

		_rememberMeTokens.add(_persistence.update(rememberMeToken));

		return rememberMeToken;
	}

	private List<RememberMeToken> _rememberMeTokens =
		new ArrayList<RememberMeToken>();
	private RememberMeTokenPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}