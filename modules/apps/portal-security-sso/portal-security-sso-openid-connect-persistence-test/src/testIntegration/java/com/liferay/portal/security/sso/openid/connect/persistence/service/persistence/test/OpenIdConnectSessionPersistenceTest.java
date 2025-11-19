/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.test;

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
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchSessionException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectSession;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectSessionLocalServiceUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectSessionPersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectSessionUtil;
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
public class OpenIdConnectSessionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.security.sso.openid.connect.persistence.service"));

	@Before
	public void setUp() {
		_persistence = OpenIdConnectSessionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OpenIdConnectSession> iterator =
			_openIdConnectSessions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectSession openIdConnectSession = _persistence.create(pk);

		Assert.assertNotNull(openIdConnectSession);

		Assert.assertEquals(openIdConnectSession.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		_persistence.remove(newOpenIdConnectSession);

		OpenIdConnectSession existingOpenIdConnectSession =
			_persistence.fetchByPrimaryKey(
				newOpenIdConnectSession.getPrimaryKey());

		Assert.assertNull(existingOpenIdConnectSession);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOpenIdConnectSession();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectSession newOpenIdConnectSession = _persistence.create(pk);

		newOpenIdConnectSession.setMvccVersion(RandomTestUtil.nextLong());

		newOpenIdConnectSession.setCompanyId(RandomTestUtil.nextLong());

		newOpenIdConnectSession.setUserId(RandomTestUtil.nextLong());

		newOpenIdConnectSession.setModifiedDate(RandomTestUtil.nextDate());

		newOpenIdConnectSession.setAccessToken(RandomTestUtil.randomString());

		newOpenIdConnectSession.setAccessTokenExpirationDate(
			RandomTestUtil.nextDate());

		newOpenIdConnectSession.setAuthServerWellKnownURI(
			RandomTestUtil.randomString());

		newOpenIdConnectSession.setClientId(RandomTestUtil.randomString());

		newOpenIdConnectSession.setIdToken(RandomTestUtil.randomString());

		newOpenIdConnectSession.setRefreshToken(RandomTestUtil.randomString());

		newOpenIdConnectSession.setSid(RandomTestUtil.randomString());

		_openIdConnectSessions.add(
			_persistence.update(newOpenIdConnectSession));

		OpenIdConnectSession existingOpenIdConnectSession =
			_persistence.findByPrimaryKey(
				newOpenIdConnectSession.getPrimaryKey());

		Assert.assertEquals(
			existingOpenIdConnectSession.getMvccVersion(),
			newOpenIdConnectSession.getMvccVersion());
		Assert.assertEquals(
			existingOpenIdConnectSession.getOpenIdConnectSessionId(),
			newOpenIdConnectSession.getOpenIdConnectSessionId());
		Assert.assertEquals(
			existingOpenIdConnectSession.getCompanyId(),
			newOpenIdConnectSession.getCompanyId());
		Assert.assertEquals(
			existingOpenIdConnectSession.getUserId(),
			newOpenIdConnectSession.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOpenIdConnectSession.getModifiedDate()),
			Time.getShortTimestamp(newOpenIdConnectSession.getModifiedDate()));
		Assert.assertEquals(
			existingOpenIdConnectSession.getAccessToken(),
			newOpenIdConnectSession.getAccessToken());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOpenIdConnectSession.getAccessTokenExpirationDate()),
			Time.getShortTimestamp(
				newOpenIdConnectSession.getAccessTokenExpirationDate()));
		Assert.assertEquals(
			existingOpenIdConnectSession.getAuthServerWellKnownURI(),
			newOpenIdConnectSession.getAuthServerWellKnownURI());
		Assert.assertEquals(
			existingOpenIdConnectSession.getClientId(),
			newOpenIdConnectSession.getClientId());
		Assert.assertEquals(
			existingOpenIdConnectSession.getIdToken(),
			newOpenIdConnectSession.getIdToken());
		Assert.assertEquals(
			existingOpenIdConnectSession.getRefreshToken(),
			newOpenIdConnectSession.getRefreshToken());
		Assert.assertEquals(
			existingOpenIdConnectSession.getSid(),
			newOpenIdConnectSession.getSid());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByLtAccessTokenExpirationDate() throws Exception {
		_persistence.countByLtAccessTokenExpirationDate(
			RandomTestUtil.nextDate());

		_persistence.countByLtAccessTokenExpirationDate(
			RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByA_S() throws Exception {
		_persistence.countByA_S("", "");

		_persistence.countByA_S("null", "null");

		_persistence.countByA_S((String)null, (String)null);
	}

	@Test
	public void testCountByC_A_C() throws Exception {
		_persistence.countByC_A_C(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_A_C(0L, "null", "null");

		_persistence.countByC_A_C(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByU_A_C() throws Exception {
		_persistence.countByU_A_C(RandomTestUtil.nextLong(), "", "");

		_persistence.countByU_A_C(0L, "null", "null");

		_persistence.countByU_A_C(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		OpenIdConnectSession existingOpenIdConnectSession =
			_persistence.findByPrimaryKey(
				newOpenIdConnectSession.getPrimaryKey());

		Assert.assertEquals(
			existingOpenIdConnectSession, newOpenIdConnectSession);
	}

	@Test(expected = NoSuchSessionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OpenIdConnectSession> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OpenIdConnectSession", "mvccVersion", true,
			"openIdConnectSessionId", true, "companyId", true, "userId", true,
			"modifiedDate", true, "accessTokenExpirationDate", true,
			"authServerWellKnownURI", true, "clientId", true, "refreshToken",
			true, "sid", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		OpenIdConnectSession existingOpenIdConnectSession =
			_persistence.fetchByPrimaryKey(
				newOpenIdConnectSession.getPrimaryKey());

		Assert.assertEquals(
			existingOpenIdConnectSession, newOpenIdConnectSession);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectSession missingOpenIdConnectSession =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOpenIdConnectSession);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OpenIdConnectSession newOpenIdConnectSession1 =
			addOpenIdConnectSession();
		OpenIdConnectSession newOpenIdConnectSession2 =
			addOpenIdConnectSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOpenIdConnectSession1.getPrimaryKey());
		primaryKeys.add(newOpenIdConnectSession2.getPrimaryKey());

		Map<Serializable, OpenIdConnectSession> openIdConnectSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, openIdConnectSessions.size());
		Assert.assertEquals(
			newOpenIdConnectSession1,
			openIdConnectSessions.get(
				newOpenIdConnectSession1.getPrimaryKey()));
		Assert.assertEquals(
			newOpenIdConnectSession2,
			openIdConnectSessions.get(
				newOpenIdConnectSession2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OpenIdConnectSession> openIdConnectSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(openIdConnectSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOpenIdConnectSession.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OpenIdConnectSession> openIdConnectSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, openIdConnectSessions.size());
		Assert.assertEquals(
			newOpenIdConnectSession,
			openIdConnectSessions.get(newOpenIdConnectSession.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OpenIdConnectSession> openIdConnectSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(openIdConnectSessions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOpenIdConnectSession.getPrimaryKey());

		Map<Serializable, OpenIdConnectSession> openIdConnectSessions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, openIdConnectSessions.size());
		Assert.assertEquals(
			newOpenIdConnectSession,
			openIdConnectSessions.get(newOpenIdConnectSession.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			OpenIdConnectSessionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<OpenIdConnectSession>() {

				@Override
				public void performAction(
					OpenIdConnectSession openIdConnectSession) {

					Assert.assertNotNull(openIdConnectSession);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"openIdConnectSessionId",
				newOpenIdConnectSession.getOpenIdConnectSessionId()));

		List<OpenIdConnectSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		OpenIdConnectSession existingOpenIdConnectSession = result.get(0);

		Assert.assertEquals(
			existingOpenIdConnectSession, newOpenIdConnectSession);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"openIdConnectSessionId", RandomTestUtil.nextLong()));

		List<OpenIdConnectSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectSession.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("openIdConnectSessionId"));

		Object newOpenIdConnectSessionId =
			newOpenIdConnectSession.getOpenIdConnectSessionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"openIdConnectSessionId",
				new Object[] {newOpenIdConnectSessionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOpenIdConnectSessionId = result.get(0);

		Assert.assertEquals(
			existingOpenIdConnectSessionId, newOpenIdConnectSessionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectSession.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("openIdConnectSessionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"openIdConnectSessionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newOpenIdConnectSession.getPrimaryKey()));
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

		OpenIdConnectSession newOpenIdConnectSession =
			addOpenIdConnectSession();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectSession.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"openIdConnectSessionId",
				newOpenIdConnectSession.getOpenIdConnectSessionId()));

		List<OpenIdConnectSession> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		OpenIdConnectSession openIdConnectSession) {

		Assert.assertEquals(
			openIdConnectSession.getAuthServerWellKnownURI(),
			ReflectionTestUtil.invoke(
				openIdConnectSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "authServerWellKnownURI"));
		Assert.assertEquals(
			openIdConnectSession.getSid(),
			ReflectionTestUtil.invoke(
				openIdConnectSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sid"));

		Assert.assertEquals(
			Long.valueOf(openIdConnectSession.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				openIdConnectSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			openIdConnectSession.getAuthServerWellKnownURI(),
			ReflectionTestUtil.invoke(
				openIdConnectSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "authServerWellKnownURI"));
		Assert.assertEquals(
			openIdConnectSession.getClientId(),
			ReflectionTestUtil.invoke(
				openIdConnectSession, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "clientId"));
	}

	protected OpenIdConnectSession addOpenIdConnectSession() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectSession openIdConnectSession = _persistence.create(pk);

		openIdConnectSession.setMvccVersion(RandomTestUtil.nextLong());

		openIdConnectSession.setCompanyId(RandomTestUtil.nextLong());

		openIdConnectSession.setUserId(RandomTestUtil.nextLong());

		openIdConnectSession.setModifiedDate(RandomTestUtil.nextDate());

		openIdConnectSession.setAccessToken(RandomTestUtil.randomString());

		openIdConnectSession.setAccessTokenExpirationDate(
			RandomTestUtil.nextDate());

		openIdConnectSession.setAuthServerWellKnownURI(
			RandomTestUtil.randomString());

		openIdConnectSession.setClientId(RandomTestUtil.randomString());

		openIdConnectSession.setIdToken(RandomTestUtil.randomString());

		openIdConnectSession.setRefreshToken(RandomTestUtil.randomString());

		openIdConnectSession.setSid(RandomTestUtil.randomString());

		_openIdConnectSessions.add(_persistence.update(openIdConnectSession));

		return openIdConnectSession;
	}

	private List<OpenIdConnectSession> _openIdConnectSessions =
		new ArrayList<OpenIdConnectSession>();
	private OpenIdConnectSessionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}