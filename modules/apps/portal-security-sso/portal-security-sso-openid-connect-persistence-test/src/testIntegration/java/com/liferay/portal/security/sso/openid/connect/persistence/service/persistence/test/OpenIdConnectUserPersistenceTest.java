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
import com.liferay.portal.security.sso.openid.connect.persistence.exception.NoSuchUserException;
import com.liferay.portal.security.sso.openid.connect.persistence.model.OpenIdConnectUser;
import com.liferay.portal.security.sso.openid.connect.persistence.service.OpenIdConnectUserLocalServiceUtil;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectUserPersistence;
import com.liferay.portal.security.sso.openid.connect.persistence.service.persistence.OpenIdConnectUserUtil;
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
public class OpenIdConnectUserPersistenceTest {

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
		_persistence = OpenIdConnectUserUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OpenIdConnectUser> iterator = _openIdConnectUsers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectUser openIdConnectUser = _persistence.create(pk);

		Assert.assertNotNull(openIdConnectUser);

		Assert.assertEquals(openIdConnectUser.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		_persistence.remove(newOpenIdConnectUser);

		OpenIdConnectUser existingOpenIdConnectUser =
			_persistence.fetchByPrimaryKey(
				newOpenIdConnectUser.getPrimaryKey());

		Assert.assertNull(existingOpenIdConnectUser);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOpenIdConnectUser();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectUser newOpenIdConnectUser = _persistence.create(pk);

		newOpenIdConnectUser.setMvccVersion(RandomTestUtil.nextLong());

		newOpenIdConnectUser.setCompanyId(RandomTestUtil.nextLong());

		newOpenIdConnectUser.setUserId(RandomTestUtil.nextLong());

		newOpenIdConnectUser.setCreateDate(RandomTestUtil.nextDate());

		newOpenIdConnectUser.setIssuer(RandomTestUtil.randomString());

		newOpenIdConnectUser.setSubject(RandomTestUtil.randomString());

		_openIdConnectUsers.add(_persistence.update(newOpenIdConnectUser));

		OpenIdConnectUser existingOpenIdConnectUser =
			_persistence.findByPrimaryKey(newOpenIdConnectUser.getPrimaryKey());

		Assert.assertEquals(
			existingOpenIdConnectUser.getMvccVersion(),
			newOpenIdConnectUser.getMvccVersion());
		Assert.assertEquals(
			existingOpenIdConnectUser.getOpenIdConnectUserId(),
			newOpenIdConnectUser.getOpenIdConnectUserId());
		Assert.assertEquals(
			existingOpenIdConnectUser.getCompanyId(),
			newOpenIdConnectUser.getCompanyId());
		Assert.assertEquals(
			existingOpenIdConnectUser.getUserId(),
			newOpenIdConnectUser.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingOpenIdConnectUser.getCreateDate()),
			Time.getShortTimestamp(newOpenIdConnectUser.getCreateDate()));
		Assert.assertEquals(
			existingOpenIdConnectUser.getIssuer(),
			newOpenIdConnectUser.getIssuer());
		Assert.assertEquals(
			existingOpenIdConnectUser.getSubject(),
			newOpenIdConnectUser.getSubject());
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByC_I_S() throws Exception {
		_persistence.countByC_I_S(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_I_S(0L, "null", "null");

		_persistence.countByC_I_S(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		OpenIdConnectUser existingOpenIdConnectUser =
			_persistence.findByPrimaryKey(newOpenIdConnectUser.getPrimaryKey());

		Assert.assertEquals(existingOpenIdConnectUser, newOpenIdConnectUser);
	}

	@Test(expected = NoSuchUserException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OpenIdConnectUser> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OpenIdConnectUser", "mvccVersion", true, "openIdConnectUserId",
			true, "companyId", true, "userId", true, "createDate", true,
			"issuer", true, "subject", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		OpenIdConnectUser existingOpenIdConnectUser =
			_persistence.fetchByPrimaryKey(
				newOpenIdConnectUser.getPrimaryKey());

		Assert.assertEquals(existingOpenIdConnectUser, newOpenIdConnectUser);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectUser missingOpenIdConnectUser =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOpenIdConnectUser);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OpenIdConnectUser newOpenIdConnectUser1 = addOpenIdConnectUser();
		OpenIdConnectUser newOpenIdConnectUser2 = addOpenIdConnectUser();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOpenIdConnectUser1.getPrimaryKey());
		primaryKeys.add(newOpenIdConnectUser2.getPrimaryKey());

		Map<Serializable, OpenIdConnectUser> openIdConnectUsers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, openIdConnectUsers.size());
		Assert.assertEquals(
			newOpenIdConnectUser1,
			openIdConnectUsers.get(newOpenIdConnectUser1.getPrimaryKey()));
		Assert.assertEquals(
			newOpenIdConnectUser2,
			openIdConnectUsers.get(newOpenIdConnectUser2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OpenIdConnectUser> openIdConnectUsers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(openIdConnectUsers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOpenIdConnectUser.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OpenIdConnectUser> openIdConnectUsers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, openIdConnectUsers.size());
		Assert.assertEquals(
			newOpenIdConnectUser,
			openIdConnectUsers.get(newOpenIdConnectUser.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OpenIdConnectUser> openIdConnectUsers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(openIdConnectUsers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOpenIdConnectUser.getPrimaryKey());

		Map<Serializable, OpenIdConnectUser> openIdConnectUsers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, openIdConnectUsers.size());
		Assert.assertEquals(
			newOpenIdConnectUser,
			openIdConnectUsers.get(newOpenIdConnectUser.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			OpenIdConnectUserLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<OpenIdConnectUser>() {

				@Override
				public void performAction(OpenIdConnectUser openIdConnectUser) {
					Assert.assertNotNull(openIdConnectUser);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectUser.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"openIdConnectUserId",
				newOpenIdConnectUser.getOpenIdConnectUserId()));

		List<OpenIdConnectUser> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		OpenIdConnectUser existingOpenIdConnectUser = result.get(0);

		Assert.assertEquals(existingOpenIdConnectUser, newOpenIdConnectUser);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectUser.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"openIdConnectUserId", RandomTestUtil.nextLong()));

		List<OpenIdConnectUser> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectUser.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("openIdConnectUserId"));

		Object newOpenIdConnectUserId =
			newOpenIdConnectUser.getOpenIdConnectUserId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"openIdConnectUserId", new Object[] {newOpenIdConnectUserId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOpenIdConnectUserId = result.get(0);

		Assert.assertEquals(
			existingOpenIdConnectUserId, newOpenIdConnectUserId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectUser.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("openIdConnectUserId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"openIdConnectUserId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newOpenIdConnectUser.getPrimaryKey()));
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

		OpenIdConnectUser newOpenIdConnectUser = addOpenIdConnectUser();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OpenIdConnectUser.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"openIdConnectUserId",
				newOpenIdConnectUser.getOpenIdConnectUserId()));

		List<OpenIdConnectUser> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(OpenIdConnectUser openIdConnectUser) {
		Assert.assertEquals(
			Long.valueOf(openIdConnectUser.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				openIdConnectUser, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			openIdConnectUser.getIssuer(),
			ReflectionTestUtil.invoke(
				openIdConnectUser, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "issuer"));
		Assert.assertEquals(
			openIdConnectUser.getSubject(),
			ReflectionTestUtil.invoke(
				openIdConnectUser, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "subject"));
	}

	protected OpenIdConnectUser addOpenIdConnectUser() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OpenIdConnectUser openIdConnectUser = _persistence.create(pk);

		openIdConnectUser.setMvccVersion(RandomTestUtil.nextLong());

		openIdConnectUser.setCompanyId(RandomTestUtil.nextLong());

		openIdConnectUser.setUserId(RandomTestUtil.nextLong());

		openIdConnectUser.setCreateDate(RandomTestUtil.nextDate());

		openIdConnectUser.setIssuer(RandomTestUtil.randomString());

		openIdConnectUser.setSubject(RandomTestUtil.randomString());

		_openIdConnectUsers.add(_persistence.update(openIdConnectUser));

		return openIdConnectUser;
	}

	private List<OpenIdConnectUser> _openIdConnectUsers =
		new ArrayList<OpenIdConnectUser>();
	private OpenIdConnectUserPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}