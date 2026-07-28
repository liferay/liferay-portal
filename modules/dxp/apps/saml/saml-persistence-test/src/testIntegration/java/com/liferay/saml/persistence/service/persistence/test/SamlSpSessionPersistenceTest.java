/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.test;

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
import com.liferay.saml.persistence.exception.NoSuchSpSessionException;
import com.liferay.saml.persistence.model.SamlSpSession;
import com.liferay.saml.persistence.service.SamlSpSessionLocalServiceUtil;
import com.liferay.saml.persistence.service.persistence.SamlSpSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpSessionUtil;

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
public class SamlSpSessionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlSpSessionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlSpSession> iterator = _samlSpSessions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpSession samlSpSession = _persistence.create(pk);

		Assert.assertNotNull(samlSpSession);

		Assert.assertEquals(samlSpSession.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		_persistence.remove(newSamlSpSession);

		SamlSpSession existingSamlSpSession = _persistence.fetchByPrimaryKey(
			newSamlSpSession.getPrimaryKey());

		Assert.assertNull(existingSamlSpSession);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlSpSession();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		newSamlSpSession.setCompanyId(RandomTestUtil.nextLong());

		newSamlSpSession.setUserId(RandomTestUtil.nextLong());

		newSamlSpSession.setUserName(RandomTestUtil.randomString());

		newSamlSpSession.setCreateDate(RandomTestUtil.nextDate());

		newSamlSpSession.setModifiedDate(RandomTestUtil.nextDate());

		newSamlSpSession.setSamlPeerBindingId(RandomTestUtil.nextLong());

		newSamlSpSession.setAssertionXml(RandomTestUtil.randomString());

		newSamlSpSession.setJSessionId(RandomTestUtil.randomString());

		newSamlSpSession.setSamlSpSessionKey(RandomTestUtil.randomString());

		newSamlSpSession.setSessionIndex(RandomTestUtil.randomString());

		newSamlSpSession.setTerminated(RandomTestUtil.randomBoolean());

		newSamlSpSession = _persistence.update(newSamlSpSession);

		_samlSpSessions.add(newSamlSpSession);

		SamlSpSession existingSamlSpSession = _persistence.findByPrimaryKey(
			newSamlSpSession.getPrimaryKey());

		Assert.assertEquals(
			existingSamlSpSession.getSamlSpSessionId(),
			newSamlSpSession.getSamlSpSessionId());
		Assert.assertEquals(
			existingSamlSpSession.getCompanyId(),
			newSamlSpSession.getCompanyId());
		Assert.assertEquals(
			existingSamlSpSession.getUserId(), newSamlSpSession.getUserId());
		Assert.assertEquals(
			existingSamlSpSession.getUserName(),
			newSamlSpSession.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlSpSession.getCreateDate()),
			Time.getShortTimestamp(newSamlSpSession.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlSpSession.getModifiedDate()),
			Time.getShortTimestamp(newSamlSpSession.getModifiedDate()));
		Assert.assertEquals(
			existingSamlSpSession.getSamlPeerBindingId(),
			newSamlSpSession.getSamlPeerBindingId());
		Assert.assertEquals(
			existingSamlSpSession.getAssertionXml(),
			newSamlSpSession.getAssertionXml());
		Assert.assertEquals(
			existingSamlSpSession.getJSessionId(),
			newSamlSpSession.getJSessionId());
		Assert.assertEquals(
			existingSamlSpSession.getSamlSpSessionKey(),
			newSamlSpSession.getSamlSpSessionKey());
		Assert.assertEquals(
			existingSamlSpSession.getSessionIndex(),
			newSamlSpSession.getSessionIndex());
		Assert.assertEquals(
			existingSamlSpSession.isTerminated(),
			newSamlSpSession.isTerminated());
	}

	@Test
	public void testCountBySamlPeerBindingId() throws Exception {
		_persistence.countBySamlPeerBindingId(RandomTestUtil.nextLong());

		_persistence.countBySamlPeerBindingId(0L);
	}

	@Test
	public void testCountByJSessionId() throws Exception {
		_persistence.countByJSessionId("");

		_persistence.countByJSessionId("null");

		_persistence.countByJSessionId((String)null);
	}

	@Test
	public void testCountBySamlSpSessionKey() throws Exception {
		_persistence.countBySamlSpSessionKey("");

		_persistence.countBySamlSpSessionKey("null");

		_persistence.countBySamlSpSessionKey((String)null);
	}

	@Test
	public void testCountByC_SI() throws Exception {
		_persistence.countByC_SI(RandomTestUtil.nextLong(), "");

		_persistence.countByC_SI(0L, "null");

		_persistence.countByC_SI(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		SamlSpSession existingSamlSpSession = _persistence.findByPrimaryKey(
			newSamlSpSession.getPrimaryKey());

		Assert.assertEquals(existingSamlSpSession, newSamlSpSession);
	}

	@Test(expected = NoSuchSpSessionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlSpSession> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlSpSession", "samlSpSessionId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "samlPeerBindingId", true, "jSessionId", true,
			"samlSpSessionKey", true, "sessionIndex", true, "terminated", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		SamlSpSession existingSamlSpSession = _persistence.fetchByPrimaryKey(
			newSamlSpSession.getPrimaryKey());

		Assert.assertEquals(existingSamlSpSession, newSamlSpSession);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpSession missingSamlSpSession = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlSpSession);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlSpSession newSamlSpSession1 = addSamlSpSession();
		SamlSpSession newSamlSpSession2 = addSamlSpSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpSession1.getPrimaryKey());
		primaryKeys.add(newSamlSpSession2.getPrimaryKey());

		Map<Serializable, SamlSpSession> samlSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlSpSessions.size());
		Assert.assertEquals(
			newSamlSpSession1,
			samlSpSessions.get(newSamlSpSession1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlSpSession2,
			samlSpSessions.get(newSamlSpSession2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlSpSession> samlSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlSpSession newSamlSpSession = addSamlSpSession();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpSession.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlSpSession> samlSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpSessions.size());
		Assert.assertEquals(
			newSamlSpSession,
			samlSpSessions.get(newSamlSpSession.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlSpSession> samlSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpSession.getPrimaryKey());

		Map<Serializable, SamlSpSession> samlSpSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpSessions.size());
		Assert.assertEquals(
			newSamlSpSession,
			samlSpSessions.get(newSamlSpSession.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SamlSpSessionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<SamlSpSession>() {

				@Override
				public void performAction(SamlSpSession samlSpSession) {
					Assert.assertNotNull(samlSpSession);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpSessionId", newSamlSpSession.getSamlSpSessionId()));

		List<SamlSpSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SamlSpSession existingSamlSpSession = result.get(0);

		Assert.assertEquals(existingSamlSpSession, newSamlSpSession);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpSessionId", RandomTestUtil.nextLong()));

		List<SamlSpSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpSession.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlSpSessionId"));

		Object newSamlSpSessionId = newSamlSpSession.getSamlSpSessionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlSpSessionId", new Object[] {newSamlSpSessionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSamlSpSessionId = result.get(0);

		Assert.assertEquals(existingSamlSpSessionId, newSamlSpSessionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpSession.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlSpSessionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlSpSessionId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlSpSession newSamlSpSession = addSamlSpSession();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSamlSpSession.getPrimaryKey()));
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

		SamlSpSession newSamlSpSession = addSamlSpSession();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpSessionId", newSamlSpSession.getSamlSpSessionId()));

		List<SamlSpSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(SamlSpSession samlSpSession) {
		Assert.assertEquals(
			samlSpSession.getJSessionId(),
			ReflectionTestUtil.invoke(
				samlSpSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "jSessionId"));

		Assert.assertEquals(
			samlSpSession.getSamlSpSessionKey(),
			ReflectionTestUtil.invoke(
				samlSpSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlSpSessionKey"));
	}

	protected SamlSpSession addSamlSpSession() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpSession samlSpSession = _persistence.create(pk);

		samlSpSession.setCompanyId(RandomTestUtil.nextLong());

		samlSpSession.setUserId(RandomTestUtil.nextLong());

		samlSpSession.setUserName(RandomTestUtil.randomString());

		samlSpSession.setCreateDate(RandomTestUtil.nextDate());

		samlSpSession.setModifiedDate(RandomTestUtil.nextDate());

		samlSpSession.setSamlPeerBindingId(RandomTestUtil.nextLong());

		samlSpSession.setAssertionXml(RandomTestUtil.randomString());

		samlSpSession.setJSessionId(RandomTestUtil.randomString());

		samlSpSession.setSamlSpSessionKey(RandomTestUtil.randomString());

		samlSpSession.setSessionIndex(RandomTestUtil.randomString());

		samlSpSession.setTerminated(RandomTestUtil.randomBoolean());

		_samlSpSessions.add(_persistence.update(samlSpSession));

		return samlSpSession;
	}

	private List<SamlSpSession> _samlSpSessions =
		new ArrayList<SamlSpSession>();
	private SamlSpSessionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1504653488