/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientEntryException;
import com.liferay.oauth.client.persistence.model.OAuthClientEntry;
import com.liferay.oauth.client.persistence.service.OAuthClientEntryLocalServiceUtil;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientEntryPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientEntryUtil;
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
public class OAuthClientEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.oauth.client.persistence.service"));

	@Before
	public void setUp() {
		_persistence = OAuthClientEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OAuthClientEntry> iterator = _oAuthClientEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientEntry oAuthClientEntry = _persistence.create(pk);

		Assert.assertNotNull(oAuthClientEntry);

		Assert.assertEquals(oAuthClientEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		_persistence.remove(newOAuthClientEntry);

		OAuthClientEntry existingOAuthClientEntry =
			_persistence.fetchByPrimaryKey(newOAuthClientEntry.getPrimaryKey());

		Assert.assertNull(existingOAuthClientEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOAuthClientEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientEntry newOAuthClientEntry = _persistence.create(pk);

		newOAuthClientEntry.setMvccVersion(RandomTestUtil.nextLong());

		newOAuthClientEntry.setCompanyId(RandomTestUtil.nextLong());

		newOAuthClientEntry.setUserId(RandomTestUtil.nextLong());

		newOAuthClientEntry.setUserName(RandomTestUtil.randomString());

		newOAuthClientEntry.setCreateDate(RandomTestUtil.nextDate());

		newOAuthClientEntry.setModifiedDate(RandomTestUtil.nextDate());

		newOAuthClientEntry.setAuthRequestParametersJSON(
			RandomTestUtil.randomString());

		newOAuthClientEntry.setAuthServerWellKnownURI(
			RandomTestUtil.randomString());

		newOAuthClientEntry.setClientId(RandomTestUtil.randomString());

		newOAuthClientEntry.setCustomClaimsJSON(RandomTestUtil.randomString());

		newOAuthClientEntry.setInfoJSON(RandomTestUtil.randomString());

		newOAuthClientEntry.setMetadataCacheTime(RandomTestUtil.nextLong());

		newOAuthClientEntry.setOIDCUserInfoMapperJSON(
			RandomTestUtil.randomString());

		newOAuthClientEntry.setTokenRequestParametersJSON(
			RandomTestUtil.randomString());

		_oAuthClientEntries.add(_persistence.update(newOAuthClientEntry));

		OAuthClientEntry existingOAuthClientEntry =
			_persistence.findByPrimaryKey(newOAuthClientEntry.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientEntry.getMvccVersion(),
			newOAuthClientEntry.getMvccVersion());
		Assert.assertEquals(
			existingOAuthClientEntry.getOAuthClientEntryId(),
			newOAuthClientEntry.getOAuthClientEntryId());
		Assert.assertEquals(
			existingOAuthClientEntry.getCompanyId(),
			newOAuthClientEntry.getCompanyId());
		Assert.assertEquals(
			existingOAuthClientEntry.getUserId(),
			newOAuthClientEntry.getUserId());
		Assert.assertEquals(
			existingOAuthClientEntry.getUserName(),
			newOAuthClientEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingOAuthClientEntry.getCreateDate()),
			Time.getShortTimestamp(newOAuthClientEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingOAuthClientEntry.getModifiedDate()),
			Time.getShortTimestamp(newOAuthClientEntry.getModifiedDate()));
		Assert.assertEquals(
			existingOAuthClientEntry.getAuthRequestParametersJSON(),
			newOAuthClientEntry.getAuthRequestParametersJSON());
		Assert.assertEquals(
			existingOAuthClientEntry.getAuthServerWellKnownURI(),
			newOAuthClientEntry.getAuthServerWellKnownURI());
		Assert.assertEquals(
			existingOAuthClientEntry.getClientId(),
			newOAuthClientEntry.getClientId());
		Assert.assertEquals(
			existingOAuthClientEntry.getCustomClaimsJSON(),
			newOAuthClientEntry.getCustomClaimsJSON());
		Assert.assertEquals(
			existingOAuthClientEntry.getInfoJSON(),
			newOAuthClientEntry.getInfoJSON());
		Assert.assertEquals(
			existingOAuthClientEntry.getMetadataCacheTime(),
			newOAuthClientEntry.getMetadataCacheTime());
		Assert.assertEquals(
			existingOAuthClientEntry.getOIDCUserInfoMapperJSON(),
			newOAuthClientEntry.getOIDCUserInfoMapperJSON());
		Assert.assertEquals(
			existingOAuthClientEntry.getTokenRequestParametersJSON(),
			newOAuthClientEntry.getTokenRequestParametersJSON());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(RandomTestUtil.nextLong(), "");

		_persistence.countByC_A(0L, "null");

		_persistence.countByC_A(0L, (String)null);
	}

	@Test
	public void testCountByC_A_C() throws Exception {
		_persistence.countByC_A_C(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_A_C(0L, "null", "null");

		_persistence.countByC_A_C(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		OAuthClientEntry existingOAuthClientEntry =
			_persistence.findByPrimaryKey(newOAuthClientEntry.getPrimaryKey());

		Assert.assertEquals(existingOAuthClientEntry, newOAuthClientEntry);
	}

	@Test(expected = NoSuchOAuthClientEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OAuthClientEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OAuthClientEntry", "mvccVersion", true, "oAuthClientEntryId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "authRequestParametersJSON", true,
			"authServerWellKnownURI", true, "clientId", true,
			"metadataCacheTime", true, "oidcUserInfoMapperJSON", true,
			"tokenRequestParametersJSON", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		OAuthClientEntry existingOAuthClientEntry =
			_persistence.fetchByPrimaryKey(newOAuthClientEntry.getPrimaryKey());

		Assert.assertEquals(existingOAuthClientEntry, newOAuthClientEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientEntry missingOAuthClientEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOAuthClientEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OAuthClientEntry newOAuthClientEntry1 = addOAuthClientEntry();
		OAuthClientEntry newOAuthClientEntry2 = addOAuthClientEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientEntry1.getPrimaryKey());
		primaryKeys.add(newOAuthClientEntry2.getPrimaryKey());

		Map<Serializable, OAuthClientEntry> oAuthClientEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, oAuthClientEntries.size());
		Assert.assertEquals(
			newOAuthClientEntry1,
			oAuthClientEntries.get(newOAuthClientEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newOAuthClientEntry2,
			oAuthClientEntries.get(newOAuthClientEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OAuthClientEntry> oAuthClientEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(oAuthClientEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OAuthClientEntry> oAuthClientEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, oAuthClientEntries.size());
		Assert.assertEquals(
			newOAuthClientEntry,
			oAuthClientEntries.get(newOAuthClientEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OAuthClientEntry> oAuthClientEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(oAuthClientEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientEntry.getPrimaryKey());

		Map<Serializable, OAuthClientEntry> oAuthClientEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, oAuthClientEntries.size());
		Assert.assertEquals(
			newOAuthClientEntry,
			oAuthClientEntries.get(newOAuthClientEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			OAuthClientEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<OAuthClientEntry>() {

				@Override
				public void performAction(OAuthClientEntry oAuthClientEntry) {
					Assert.assertNotNull(oAuthClientEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientEntryId",
				newOAuthClientEntry.getOAuthClientEntryId()));

		List<OAuthClientEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		OAuthClientEntry existingOAuthClientEntry = result.get(0);

		Assert.assertEquals(existingOAuthClientEntry, newOAuthClientEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientEntryId", RandomTestUtil.nextLong()));

		List<OAuthClientEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("oAuthClientEntryId"));

		Object newOAuthClientEntryId =
			newOAuthClientEntry.getOAuthClientEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"oAuthClientEntryId", new Object[] {newOAuthClientEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOAuthClientEntryId = result.get(0);

		Assert.assertEquals(existingOAuthClientEntryId, newOAuthClientEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("oAuthClientEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"oAuthClientEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newOAuthClientEntry.getPrimaryKey()));
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

		OAuthClientEntry newOAuthClientEntry = addOAuthClientEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientEntryId",
				newOAuthClientEntry.getOAuthClientEntryId()));

		List<OAuthClientEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(OAuthClientEntry oAuthClientEntry) {
		Assert.assertEquals(
			Long.valueOf(oAuthClientEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				oAuthClientEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			oAuthClientEntry.getAuthServerWellKnownURI(),
			ReflectionTestUtil.invoke(
				oAuthClientEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "authServerWellKnownURI"));
		Assert.assertEquals(
			oAuthClientEntry.getClientId(),
			ReflectionTestUtil.invoke(
				oAuthClientEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "clientId"));
	}

	protected OAuthClientEntry addOAuthClientEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientEntry oAuthClientEntry = _persistence.create(pk);

		oAuthClientEntry.setMvccVersion(RandomTestUtil.nextLong());

		oAuthClientEntry.setCompanyId(RandomTestUtil.nextLong());

		oAuthClientEntry.setUserId(RandomTestUtil.nextLong());

		oAuthClientEntry.setUserName(RandomTestUtil.randomString());

		oAuthClientEntry.setCreateDate(RandomTestUtil.nextDate());

		oAuthClientEntry.setModifiedDate(RandomTestUtil.nextDate());

		oAuthClientEntry.setAuthRequestParametersJSON(
			RandomTestUtil.randomString());

		oAuthClientEntry.setAuthServerWellKnownURI(
			RandomTestUtil.randomString());

		oAuthClientEntry.setClientId(RandomTestUtil.randomString());

		oAuthClientEntry.setCustomClaimsJSON(RandomTestUtil.randomString());

		oAuthClientEntry.setInfoJSON(RandomTestUtil.randomString());

		oAuthClientEntry.setMetadataCacheTime(RandomTestUtil.nextLong());

		oAuthClientEntry.setOIDCUserInfoMapperJSON(
			RandomTestUtil.randomString());

		oAuthClientEntry.setTokenRequestParametersJSON(
			RandomTestUtil.randomString());

		_oAuthClientEntries.add(_persistence.update(oAuthClientEntry));

		return oAuthClientEntry;
	}

	private List<OAuthClientEntry> _oAuthClientEntries =
		new ArrayList<OAuthClientEntry>();
	private OAuthClientEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}