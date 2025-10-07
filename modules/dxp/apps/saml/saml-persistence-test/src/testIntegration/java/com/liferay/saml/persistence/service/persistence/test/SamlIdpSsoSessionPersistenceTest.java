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
import com.liferay.saml.persistence.exception.NoSuchIdpSsoSessionException;
import com.liferay.saml.persistence.model.SamlIdpSsoSession;
import com.liferay.saml.persistence.service.SamlIdpSsoSessionLocalServiceUtil;
import com.liferay.saml.persistence.service.persistence.SamlIdpSsoSessionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSsoSessionUtil;

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
public class SamlIdpSsoSessionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlIdpSsoSessionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlIdpSsoSession> iterator = _samlIdpSsoSessions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSsoSession samlIdpSsoSession = _persistence.create(pk);

		Assert.assertNotNull(samlIdpSsoSession);

		Assert.assertEquals(samlIdpSsoSession.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		_persistence.remove(newSamlIdpSsoSession);

		SamlIdpSsoSession existingSamlIdpSsoSession =
			_persistence.fetchByPrimaryKey(
				newSamlIdpSsoSession.getPrimaryKey());

		Assert.assertNull(existingSamlIdpSsoSession);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlIdpSsoSession();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSsoSession newSamlIdpSsoSession = _persistence.create(pk);

		newSamlIdpSsoSession.setCompanyId(RandomTestUtil.nextLong());

		newSamlIdpSsoSession.setUserId(RandomTestUtil.nextLong());

		newSamlIdpSsoSession.setUserName(RandomTestUtil.randomString());

		newSamlIdpSsoSession.setCreateDate(RandomTestUtil.nextDate());

		newSamlIdpSsoSession.setModifiedDate(RandomTestUtil.nextDate());

		newSamlIdpSsoSession.setSamlIdpSsoSessionKey(
			RandomTestUtil.randomString());

		_samlIdpSsoSessions.add(_persistence.update(newSamlIdpSsoSession));

		SamlIdpSsoSession existingSamlIdpSsoSession =
			_persistence.findByPrimaryKey(newSamlIdpSsoSession.getPrimaryKey());

		Assert.assertEquals(
			existingSamlIdpSsoSession.getSamlIdpSsoSessionId(),
			newSamlIdpSsoSession.getSamlIdpSsoSessionId());
		Assert.assertEquals(
			existingSamlIdpSsoSession.getCompanyId(),
			newSamlIdpSsoSession.getCompanyId());
		Assert.assertEquals(
			existingSamlIdpSsoSession.getUserId(),
			newSamlIdpSsoSession.getUserId());
		Assert.assertEquals(
			existingSamlIdpSsoSession.getUserName(),
			newSamlIdpSsoSession.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlIdpSsoSession.getCreateDate()),
			Time.getShortTimestamp(newSamlIdpSsoSession.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlIdpSsoSession.getModifiedDate()),
			Time.getShortTimestamp(newSamlIdpSsoSession.getModifiedDate()));
		Assert.assertEquals(
			existingSamlIdpSsoSession.getSamlIdpSsoSessionKey(),
			newSamlIdpSsoSession.getSamlIdpSsoSessionKey());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByLtCreateDate() throws Exception {
		_persistence.countByLtCreateDate(RandomTestUtil.nextDate());

		_persistence.countByLtCreateDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountBySamlIdpSsoSessionKey() throws Exception {
		_persistence.countBySamlIdpSsoSessionKey("");

		_persistence.countBySamlIdpSsoSessionKey("null");

		_persistence.countBySamlIdpSsoSessionKey((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		SamlIdpSsoSession existingSamlIdpSsoSession =
			_persistence.findByPrimaryKey(newSamlIdpSsoSession.getPrimaryKey());

		Assert.assertEquals(existingSamlIdpSsoSession, newSamlIdpSsoSession);
	}

	@Test(expected = NoSuchIdpSsoSessionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlIdpSsoSession> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlIdpSsoSession", "samlIdpSsoSessionId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "samlIdpSsoSessionKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		SamlIdpSsoSession existingSamlIdpSsoSession =
			_persistence.fetchByPrimaryKey(
				newSamlIdpSsoSession.getPrimaryKey());

		Assert.assertEquals(existingSamlIdpSsoSession, newSamlIdpSsoSession);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSsoSession missingSamlIdpSsoSession =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlIdpSsoSession);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlIdpSsoSession newSamlIdpSsoSession1 = addSamlIdpSsoSession();
		SamlIdpSsoSession newSamlIdpSsoSession2 = addSamlIdpSsoSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSsoSession1.getPrimaryKey());
		primaryKeys.add(newSamlIdpSsoSession2.getPrimaryKey());

		Map<Serializable, SamlIdpSsoSession> samlIdpSsoSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlIdpSsoSessions.size());
		Assert.assertEquals(
			newSamlIdpSsoSession1,
			samlIdpSsoSessions.get(newSamlIdpSsoSession1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlIdpSsoSession2,
			samlIdpSsoSessions.get(newSamlIdpSsoSession2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlIdpSsoSession> samlIdpSsoSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIdpSsoSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSsoSession.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlIdpSsoSession> samlIdpSsoSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIdpSsoSessions.size());
		Assert.assertEquals(
			newSamlIdpSsoSession,
			samlIdpSsoSessions.get(newSamlIdpSsoSession.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlIdpSsoSession> samlIdpSsoSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIdpSsoSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSsoSession.getPrimaryKey());

		Map<Serializable, SamlIdpSsoSession> samlIdpSsoSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIdpSsoSessions.size());
		Assert.assertEquals(
			newSamlIdpSsoSession,
			samlIdpSsoSessions.get(newSamlIdpSsoSession.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SamlIdpSsoSessionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SamlIdpSsoSession>() {

				@Override
				public void performAction(SamlIdpSsoSession samlIdpSsoSession) {
					Assert.assertNotNull(samlIdpSsoSession);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSsoSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlIdpSsoSessionId",
				newSamlIdpSsoSession.getSamlIdpSsoSessionId()));

		List<SamlIdpSsoSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SamlIdpSsoSession existingSamlIdpSsoSession = result.get(0);

		Assert.assertEquals(existingSamlIdpSsoSession, newSamlIdpSsoSession);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSsoSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlIdpSsoSessionId", RandomTestUtil.nextLong()));

		List<SamlIdpSsoSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSsoSession.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlIdpSsoSessionId"));

		Object newSamlIdpSsoSessionId =
			newSamlIdpSsoSession.getSamlIdpSsoSessionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlIdpSsoSessionId", new Object[] {newSamlIdpSsoSessionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSamlIdpSsoSessionId = result.get(0);

		Assert.assertEquals(
			existingSamlIdpSsoSessionId, newSamlIdpSsoSessionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSsoSession.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlIdpSsoSessionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlIdpSsoSessionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSamlIdpSsoSession.getPrimaryKey()));
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

		SamlIdpSsoSession newSamlIdpSsoSession = addSamlIdpSsoSession();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSsoSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlIdpSsoSessionId",
				newSamlIdpSsoSession.getSamlIdpSsoSessionId()));

		List<SamlIdpSsoSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(SamlIdpSsoSession samlIdpSsoSession) {
		Assert.assertEquals(
			Long.valueOf(samlIdpSsoSession.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				samlIdpSsoSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));

		Assert.assertEquals(
			samlIdpSsoSession.getSamlIdpSsoSessionKey(),
			ReflectionTestUtil.invoke(
				samlIdpSsoSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlIdpSsoSessionKey"));
	}

	protected SamlIdpSsoSession addSamlIdpSsoSession() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSsoSession samlIdpSsoSession = _persistence.create(pk);

		samlIdpSsoSession.setCompanyId(RandomTestUtil.nextLong());

		samlIdpSsoSession.setUserId(RandomTestUtil.nextLong());

		samlIdpSsoSession.setUserName(RandomTestUtil.randomString());

		samlIdpSsoSession.setCreateDate(RandomTestUtil.nextDate());

		samlIdpSsoSession.setModifiedDate(RandomTestUtil.nextDate());

		samlIdpSsoSession.setSamlIdpSsoSessionKey(
			RandomTestUtil.randomString());

		_samlIdpSsoSessions.add(_persistence.update(samlIdpSsoSession));

		return samlIdpSsoSession;
	}

	private List<SamlIdpSsoSession> _samlIdpSsoSessions =
		new ArrayList<SamlIdpSsoSession>();
	private SamlIdpSsoSessionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}