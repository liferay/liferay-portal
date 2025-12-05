/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientASLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientASLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientASLocalMetadataLocalServiceUtil;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientASLocalMetadataUtil;
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
public class OAuthClientASLocalMetadataPersistenceTest {

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
		_persistence = OAuthClientASLocalMetadataUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OAuthClientASLocalMetadata> iterator =
			_oAuthClientASLocalMetadatas.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_persistence.create(pk);

		Assert.assertNotNull(oAuthClientASLocalMetadata);

		Assert.assertEquals(oAuthClientASLocalMetadata.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		_persistence.remove(newOAuthClientASLocalMetadata);

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.fetchByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertNull(existingOAuthClientASLocalMetadata);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOAuthClientASLocalMetadata();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			_persistence.create(pk);

		newOAuthClientASLocalMetadata.setMvccVersion(RandomTestUtil.nextLong());

		newOAuthClientASLocalMetadata.setCompanyId(RandomTestUtil.nextLong());

		newOAuthClientASLocalMetadata.setUserId(RandomTestUtil.nextLong());

		newOAuthClientASLocalMetadata.setUserName(
			RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setCreateDate(RandomTestUtil.nextDate());

		newOAuthClientASLocalMetadata.setModifiedDate(
			RandomTestUtil.nextDate());

		newOAuthClientASLocalMetadata.setIssuer(RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setLocalWellKnownEnabled(
			RandomTestUtil.randomBoolean());

		newOAuthClientASLocalMetadata.setLocalWellKnownURIOAS(
			RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setLocalWellKnownURIOIC(
			RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setMetadataJSONOAS(
			RandomTestUtil.randomString());

		newOAuthClientASLocalMetadata.setMetadataJSONOIC(
			RandomTestUtil.randomString());

		_oAuthClientASLocalMetadatas.add(
			_persistence.update(newOAuthClientASLocalMetadata));

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.findByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getMvccVersion(),
			newOAuthClientASLocalMetadata.getMvccVersion());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.
				getOAuthClientASLocalMetadataId(),
			newOAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getCompanyId(),
			newOAuthClientASLocalMetadata.getCompanyId());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getUserId(),
			newOAuthClientASLocalMetadata.getUserId());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getUserName(),
			newOAuthClientASLocalMetadata.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOAuthClientASLocalMetadata.getCreateDate()),
			Time.getShortTimestamp(
				newOAuthClientASLocalMetadata.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOAuthClientASLocalMetadata.getModifiedDate()),
			Time.getShortTimestamp(
				newOAuthClientASLocalMetadata.getModifiedDate()));
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getIssuer(),
			newOAuthClientASLocalMetadata.getIssuer());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.isLocalWellKnownEnabled(),
			newOAuthClientASLocalMetadata.isLocalWellKnownEnabled());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getLocalWellKnownURIOAS(),
			newOAuthClientASLocalMetadata.getLocalWellKnownURIOAS());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getLocalWellKnownURIOIC(),
			newOAuthClientASLocalMetadata.getLocalWellKnownURIOIC());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getMetadataJSONOAS(),
			newOAuthClientASLocalMetadata.getMetadataJSONOAS());
		Assert.assertEquals(
			existingOAuthClientASLocalMetadata.getMetadataJSONOIC(),
			newOAuthClientASLocalMetadata.getMetadataJSONOIC());
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
	public void testCountByC_I() throws Exception {
		_persistence.countByC_I(RandomTestUtil.nextLong(), "");

		_persistence.countByC_I(0L, "null");

		_persistence.countByC_I(0L, (String)null);
	}

	@Test
	public void testCountByLocalWellKnownURIOAS() throws Exception {
		_persistence.countByLocalWellKnownURIOAS("");

		_persistence.countByLocalWellKnownURIOAS("null");

		_persistence.countByLocalWellKnownURIOAS((String)null);
	}

	@Test
	public void testCountByLocalWellKnownURIOIC() throws Exception {
		_persistence.countByLocalWellKnownURIOIC("");

		_persistence.countByLocalWellKnownURIOIC("null");

		_persistence.countByLocalWellKnownURIOIC((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.findByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata, newOAuthClientASLocalMetadata);
	}

	@Test(expected = NoSuchOAuthClientASLocalMetadataException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OAuthClientASLocalMetadata>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"OAuthClientASLocalMetadata", "mvccVersion", true,
			"oAuthClientASLocalMetadataId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"issuer", true, "localWellKnownEnabled", true,
			"localWellKnownURIOAS", true, "localWellKnownURIOIC", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			_persistence.fetchByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata, newOAuthClientASLocalMetadata);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata missingOAuthClientASLocalMetadata =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOAuthClientASLocalMetadata);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata1 =
			addOAuthClientASLocalMetadata();
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata2 =
			addOAuthClientASLocalMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientASLocalMetadata1.getPrimaryKey());
		primaryKeys.add(newOAuthClientASLocalMetadata2.getPrimaryKey());

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, oAuthClientASLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientASLocalMetadata1,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata1.getPrimaryKey()));
		Assert.assertEquals(
			newOAuthClientASLocalMetadata2,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(oAuthClientASLocalMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientASLocalMetadata.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, oAuthClientASLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientASLocalMetadata,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(oAuthClientASLocalMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientASLocalMetadata.getPrimaryKey());

		Map<Serializable, OAuthClientASLocalMetadata>
			oAuthClientASLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, oAuthClientASLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientASLocalMetadata,
			oAuthClientASLocalMetadatas.get(
				newOAuthClientASLocalMetadata.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			OAuthClientASLocalMetadataLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<OAuthClientASLocalMetadata>() {

				@Override
				public void performAction(
					OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

					Assert.assertNotNull(oAuthClientASLocalMetadata);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientASLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientASLocalMetadataId",
				newOAuthClientASLocalMetadata.
					getOAuthClientASLocalMetadataId()));

		List<OAuthClientASLocalMetadata> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		OAuthClientASLocalMetadata existingOAuthClientASLocalMetadata =
			result.get(0);

		Assert.assertEquals(
			existingOAuthClientASLocalMetadata, newOAuthClientASLocalMetadata);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientASLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientASLocalMetadataId", RandomTestUtil.nextLong()));

		List<OAuthClientASLocalMetadata> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientASLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("oAuthClientASLocalMetadataId"));

		Object newOAuthClientASLocalMetadataId =
			newOAuthClientASLocalMetadata.getOAuthClientASLocalMetadataId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"oAuthClientASLocalMetadataId",
				new Object[] {newOAuthClientASLocalMetadataId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOAuthClientASLocalMetadataId = result.get(0);

		Assert.assertEquals(
			existingOAuthClientASLocalMetadataId,
			newOAuthClientASLocalMetadataId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientASLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("oAuthClientASLocalMetadataId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"oAuthClientASLocalMetadataId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newOAuthClientASLocalMetadata.getPrimaryKey()));
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

		OAuthClientASLocalMetadata newOAuthClientASLocalMetadata =
			addOAuthClientASLocalMetadata();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientASLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientASLocalMetadataId",
				newOAuthClientASLocalMetadata.
					getOAuthClientASLocalMetadataId()));

		List<OAuthClientASLocalMetadata> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		OAuthClientASLocalMetadata oAuthClientASLocalMetadata) {

		Assert.assertEquals(
			Long.valueOf(oAuthClientASLocalMetadata.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				oAuthClientASLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			oAuthClientASLocalMetadata.getIssuer(),
			ReflectionTestUtil.invoke(
				oAuthClientASLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "issuer"));

		Assert.assertEquals(
			oAuthClientASLocalMetadata.getLocalWellKnownURIOAS(),
			ReflectionTestUtil.invoke(
				oAuthClientASLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "localWellKnownURIOAS"));

		Assert.assertEquals(
			oAuthClientASLocalMetadata.getLocalWellKnownURIOIC(),
			ReflectionTestUtil.invoke(
				oAuthClientASLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "localWellKnownURIOIC"));
	}

	protected OAuthClientASLocalMetadata addOAuthClientASLocalMetadata()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		OAuthClientASLocalMetadata oAuthClientASLocalMetadata =
			_persistence.create(pk);

		oAuthClientASLocalMetadata.setMvccVersion(RandomTestUtil.nextLong());

		oAuthClientASLocalMetadata.setCompanyId(RandomTestUtil.nextLong());

		oAuthClientASLocalMetadata.setUserId(RandomTestUtil.nextLong());

		oAuthClientASLocalMetadata.setUserName(RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setCreateDate(RandomTestUtil.nextDate());

		oAuthClientASLocalMetadata.setModifiedDate(RandomTestUtil.nextDate());

		oAuthClientASLocalMetadata.setIssuer(RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setLocalWellKnownEnabled(
			RandomTestUtil.randomBoolean());

		oAuthClientASLocalMetadata.setLocalWellKnownURIOAS(
			RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setLocalWellKnownURIOIC(
			RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setMetadataJSONOAS(
			RandomTestUtil.randomString());

		oAuthClientASLocalMetadata.setMetadataJSONOIC(
			RandomTestUtil.randomString());

		_oAuthClientASLocalMetadatas.add(
			_persistence.update(oAuthClientASLocalMetadata));

		return oAuthClientASLocalMetadata;
	}

	private List<OAuthClientASLocalMetadata> _oAuthClientASLocalMetadatas =
		new ArrayList<OAuthClientASLocalMetadata>();
	private OAuthClientASLocalMetadataPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}