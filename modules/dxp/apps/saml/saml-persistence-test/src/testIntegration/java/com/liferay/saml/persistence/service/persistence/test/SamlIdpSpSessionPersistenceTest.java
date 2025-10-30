/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.saml.persistence.exception.NoSuchIdpSpSessionException;
import com.liferay.saml.persistence.model.SamlIdpSpSession;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpSessionUtil;

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
public class SamlIdpSpSessionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlIdpSpSessionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlIdpSpSession> iterator = _samlIdpSpSessions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpSession samlIdpSpSession = _persistence.create(pk);

		Assert.assertNotNull(samlIdpSpSession);

		Assert.assertEquals(samlIdpSpSession.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlIdpSpSession newSamlIdpSpSession = addSamlIdpSpSession();

		_persistence.remove(newSamlIdpSpSession);

		SamlIdpSpSession existingSamlIdpSpSession =
			_persistence.fetchByPrimaryKey(newSamlIdpSpSession.getPrimaryKey());

		Assert.assertNull(existingSamlIdpSpSession);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlIdpSpSession();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpSession newSamlIdpSpSession = _persistence.create(pk);

		newSamlIdpSpSession.setCompanyId(RandomTestUtil.nextLong());

		newSamlIdpSpSession.setUserId(RandomTestUtil.nextLong());

		newSamlIdpSpSession.setUserName(RandomTestUtil.randomString());

		newSamlIdpSpSession.setCreateDate(RandomTestUtil.nextDate());

		newSamlIdpSpSession.setModifiedDate(RandomTestUtil.nextDate());

		newSamlIdpSpSession.setSamlIdpSsoSessionId(RandomTestUtil.nextLong());

		newSamlIdpSpSession.setSamlPeerBindingId(RandomTestUtil.nextLong());

		_samlIdpSpSessions.add(_persistence.update(newSamlIdpSpSession));

		SamlIdpSpSession existingSamlIdpSpSession =
			_persistence.findByPrimaryKey(newSamlIdpSpSession.getPrimaryKey());

		Assert.assertEquals(
			existingSamlIdpSpSession.getSamlIdpSpSessionId(),
			newSamlIdpSpSession.getSamlIdpSpSessionId());
		Assert.assertEquals(
			existingSamlIdpSpSession.getCompanyId(),
			newSamlIdpSpSession.getCompanyId());
		Assert.assertEquals(
			existingSamlIdpSpSession.getUserId(),
			newSamlIdpSpSession.getUserId());
		Assert.assertEquals(
			existingSamlIdpSpSession.getUserName(),
			newSamlIdpSpSession.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlIdpSpSession.getCreateDate()),
			Time.getShortTimestamp(newSamlIdpSpSession.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlIdpSpSession.getModifiedDate()),
			Time.getShortTimestamp(newSamlIdpSpSession.getModifiedDate()));
		Assert.assertEquals(
			existingSamlIdpSpSession.getSamlIdpSsoSessionId(),
			newSamlIdpSpSession.getSamlIdpSsoSessionId());
		Assert.assertEquals(
			existingSamlIdpSpSession.getSamlPeerBindingId(),
			newSamlIdpSpSession.getSamlPeerBindingId());
	}

	@Test
	public void testCountByLtCreateDate() throws Exception {
		_persistence.countByLtCreateDate(RandomTestUtil.nextDate());

		_persistence.countByLtCreateDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountBySamlIdpSsoSessionId() throws Exception {
		_persistence.countBySamlIdpSsoSessionId(RandomTestUtil.nextLong());

		_persistence.countBySamlIdpSsoSessionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlIdpSpSession newSamlIdpSpSession = addSamlIdpSpSession();

		SamlIdpSpSession existingSamlIdpSpSession =
			_persistence.findByPrimaryKey(newSamlIdpSpSession.getPrimaryKey());

		Assert.assertEquals(existingSamlIdpSpSession, newSamlIdpSpSession);
	}

	@Test(expected = NoSuchIdpSpSessionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlIdpSpSession> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlIdpSpSession", "samlIdpSpSessionId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "samlIdpSsoSessionId", true,
			"samlPeerBindingId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlIdpSpSession newSamlIdpSpSession = addSamlIdpSpSession();

		SamlIdpSpSession existingSamlIdpSpSession =
			_persistence.fetchByPrimaryKey(newSamlIdpSpSession.getPrimaryKey());

		Assert.assertEquals(existingSamlIdpSpSession, newSamlIdpSpSession);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpSession missingSamlIdpSpSession =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlIdpSpSession);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlIdpSpSession newSamlIdpSpSession1 = addSamlIdpSpSession();
		SamlIdpSpSession newSamlIdpSpSession2 = addSamlIdpSpSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSpSession1.getPrimaryKey());
		primaryKeys.add(newSamlIdpSpSession2.getPrimaryKey());

		Map<Serializable, SamlIdpSpSession> samlIdpSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlIdpSpSessions.size());
		Assert.assertEquals(
			newSamlIdpSpSession1,
			samlIdpSpSessions.get(newSamlIdpSpSession1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlIdpSpSession2,
			samlIdpSpSessions.get(newSamlIdpSpSession2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlIdpSpSession> samlIdpSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIdpSpSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlIdpSpSession newSamlIdpSpSession = addSamlIdpSpSession();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSpSession.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlIdpSpSession> samlIdpSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIdpSpSessions.size());
		Assert.assertEquals(
			newSamlIdpSpSession,
			samlIdpSpSessions.get(newSamlIdpSpSession.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlIdpSpSession> samlIdpSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIdpSpSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlIdpSpSession newSamlIdpSpSession = addSamlIdpSpSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSpSession.getPrimaryKey());

		Map<Serializable, SamlIdpSpSession> samlIdpSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIdpSpSessions.size());
		Assert.assertEquals(
			newSamlIdpSpSession,
			samlIdpSpSessions.get(newSamlIdpSpSession.getPrimaryKey()));
	}

	protected SamlIdpSpSession addSamlIdpSpSession() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpSession samlIdpSpSession = _persistence.create(pk);

		samlIdpSpSession.setCompanyId(RandomTestUtil.nextLong());

		samlIdpSpSession.setUserId(RandomTestUtil.nextLong());

		samlIdpSpSession.setUserName(RandomTestUtil.randomString());

		samlIdpSpSession.setCreateDate(RandomTestUtil.nextDate());

		samlIdpSpSession.setModifiedDate(RandomTestUtil.nextDate());

		samlIdpSpSession.setSamlIdpSsoSessionId(RandomTestUtil.nextLong());

		samlIdpSpSession.setSamlPeerBindingId(RandomTestUtil.nextLong());

		_samlIdpSpSessions.add(_persistence.update(samlIdpSpSession));

		return samlIdpSpSession;
	}

	private List<SamlIdpSpSession> _samlIdpSpSessions =
		new ArrayList<SamlIdpSpSession>();
	private SamlIdpSpSessionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}