/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.oauth.client.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.oauth.client.persistence.exception.DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException;
import com.liferay.oauth.client.persistence.exception.NoSuchOAuthClientPRLocalMetadataException;
import com.liferay.oauth.client.persistence.model.OAuthClientPRLocalMetadata;
import com.liferay.oauth.client.persistence.service.OAuthClientPRLocalMetadataLocalServiceUtil;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientPRLocalMetadataPersistence;
import com.liferay.oauth.client.persistence.service.persistence.OAuthClientPRLocalMetadataUtil;
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
public class OAuthClientPRLocalMetadataPersistenceTest {

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
		_persistence = OAuthClientPRLocalMetadataUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OAuthClientPRLocalMetadata> iterator =
			_oAuthClientPRLocalMetadatas.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			_persistence.create(pk);

		Assert.assertNotNull(oAuthClientPRLocalMetadata);

		Assert.assertEquals(oAuthClientPRLocalMetadata.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		_persistence.remove(newOAuthClientPRLocalMetadata);

		OAuthClientPRLocalMetadata existingOAuthClientPRLocalMetadata =
			_persistence.fetchByPrimaryKey(
				newOAuthClientPRLocalMetadata.getPrimaryKey());

		Assert.assertNull(existingOAuthClientPRLocalMetadata);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOAuthClientPRLocalMetadata();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			_persistence.create(pk);

		newOAuthClientPRLocalMetadata.setMvccVersion(RandomTestUtil.nextLong());

		newOAuthClientPRLocalMetadata.setUuid(RandomTestUtil.randomString());

		newOAuthClientPRLocalMetadata.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newOAuthClientPRLocalMetadata.setCompanyId(RandomTestUtil.nextLong());

		newOAuthClientPRLocalMetadata.setUserId(RandomTestUtil.nextLong());

		newOAuthClientPRLocalMetadata.setUserName(
			RandomTestUtil.randomString());

		newOAuthClientPRLocalMetadata.setCreateDate(RandomTestUtil.nextDate());

		newOAuthClientPRLocalMetadata.setModifiedDate(
			RandomTestUtil.nextDate());

		newOAuthClientPRLocalMetadata.setLocalWellKnownEnabled(
			RandomTestUtil.randomBoolean());

		newOAuthClientPRLocalMetadata.setLocalWellKnownURI(
			RandomTestUtil.randomString());

		newOAuthClientPRLocalMetadata.setMetadataJSON(
			RandomTestUtil.randomString());

		newOAuthClientPRLocalMetadata.setResource(
			RandomTestUtil.randomString());

		_oAuthClientPRLocalMetadatas.add(
			_persistence.update(newOAuthClientPRLocalMetadata));

		OAuthClientPRLocalMetadata existingOAuthClientPRLocalMetadata =
			_persistence.findByPrimaryKey(
				newOAuthClientPRLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getMvccVersion(),
			newOAuthClientPRLocalMetadata.getMvccVersion());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getUuid(),
			newOAuthClientPRLocalMetadata.getUuid());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getExternalReferenceCode(),
			newOAuthClientPRLocalMetadata.getExternalReferenceCode());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.
				getOAuthClientPRLocalMetadataId(),
			newOAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getCompanyId(),
			newOAuthClientPRLocalMetadata.getCompanyId());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getUserId(),
			newOAuthClientPRLocalMetadata.getUserId());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getUserName(),
			newOAuthClientPRLocalMetadata.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOAuthClientPRLocalMetadata.getCreateDate()),
			Time.getShortTimestamp(
				newOAuthClientPRLocalMetadata.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingOAuthClientPRLocalMetadata.getModifiedDate()),
			Time.getShortTimestamp(
				newOAuthClientPRLocalMetadata.getModifiedDate()));
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.isLocalWellKnownEnabled(),
			newOAuthClientPRLocalMetadata.isLocalWellKnownEnabled());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getLocalWellKnownURI(),
			newOAuthClientPRLocalMetadata.getLocalWellKnownURI());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getMetadataJSON(),
			newOAuthClientPRLocalMetadata.getMetadataJSON());
		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata.getResource(),
			newOAuthClientPRLocalMetadata.getResource());
	}

	@Test(
		expected = DuplicateOAuthClientPRLocalMetadataExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		newOAuthClientPRLocalMetadata.setCompanyId(
			oAuthClientPRLocalMetadata.getCompanyId());

		newOAuthClientPRLocalMetadata = _persistence.update(
			newOAuthClientPRLocalMetadata);

		Session session = _persistence.getCurrentSession();

		session.evict(newOAuthClientPRLocalMetadata);

		newOAuthClientPRLocalMetadata.setExternalReferenceCode(
			oAuthClientPRLocalMetadata.getExternalReferenceCode());

		_persistence.update(newOAuthClientPRLocalMetadata);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
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
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_L(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_LWKURI() throws Exception {
		_persistence.countByC_LWKURI(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LWKURI(0L, "null");

		_persistence.countByC_LWKURI(0L, (String)null);
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(RandomTestUtil.nextLong(), "");

		_persistence.countByC_R(0L, "null");

		_persistence.countByC_R(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata existingOAuthClientPRLocalMetadata =
			_persistence.findByPrimaryKey(
				newOAuthClientPRLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata, newOAuthClientPRLocalMetadata);
	}

	@Test(expected = NoSuchOAuthClientPRLocalMetadataException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OAuthClientPRLocalMetadata>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"OAuthClientPRLocalMetadata", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "oAuthClientPRLocalMetadataId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "localWellKnownEnabled", true,
			"localWellKnownURI", true, "resource", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		OAuthClientPRLocalMetadata existingOAuthClientPRLocalMetadata =
			_persistence.fetchByPrimaryKey(
				newOAuthClientPRLocalMetadata.getPrimaryKey());

		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata, newOAuthClientPRLocalMetadata);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OAuthClientPRLocalMetadata missingOAuthClientPRLocalMetadata =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOAuthClientPRLocalMetadata);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata1 =
			addOAuthClientPRLocalMetadata();
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata2 =
			addOAuthClientPRLocalMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientPRLocalMetadata1.getPrimaryKey());
		primaryKeys.add(newOAuthClientPRLocalMetadata2.getPrimaryKey());

		Map<Serializable, OAuthClientPRLocalMetadata>
			oAuthClientPRLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, oAuthClientPRLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientPRLocalMetadata1,
			oAuthClientPRLocalMetadatas.get(
				newOAuthClientPRLocalMetadata1.getPrimaryKey()));
		Assert.assertEquals(
			newOAuthClientPRLocalMetadata2,
			oAuthClientPRLocalMetadatas.get(
				newOAuthClientPRLocalMetadata2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OAuthClientPRLocalMetadata>
			oAuthClientPRLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(oAuthClientPRLocalMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientPRLocalMetadata.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OAuthClientPRLocalMetadata>
			oAuthClientPRLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, oAuthClientPRLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientPRLocalMetadata,
			oAuthClientPRLocalMetadatas.get(
				newOAuthClientPRLocalMetadata.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OAuthClientPRLocalMetadata>
			oAuthClientPRLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(oAuthClientPRLocalMetadatas.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOAuthClientPRLocalMetadata.getPrimaryKey());

		Map<Serializable, OAuthClientPRLocalMetadata>
			oAuthClientPRLocalMetadatas = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, oAuthClientPRLocalMetadatas.size());
		Assert.assertEquals(
			newOAuthClientPRLocalMetadata,
			oAuthClientPRLocalMetadatas.get(
				newOAuthClientPRLocalMetadata.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			OAuthClientPRLocalMetadataLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<OAuthClientPRLocalMetadata>() {

				@Override
				public void performAction(
					OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

					Assert.assertNotNull(oAuthClientPRLocalMetadata);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientPRLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientPRLocalMetadataId",
				newOAuthClientPRLocalMetadata.
					getOAuthClientPRLocalMetadataId()));

		List<OAuthClientPRLocalMetadata> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		OAuthClientPRLocalMetadata existingOAuthClientPRLocalMetadata =
			result.get(0);

		Assert.assertEquals(
			existingOAuthClientPRLocalMetadata, newOAuthClientPRLocalMetadata);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientPRLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientPRLocalMetadataId", RandomTestUtil.nextLong()));

		List<OAuthClientPRLocalMetadata> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientPRLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("oAuthClientPRLocalMetadataId"));

		Object newOAuthClientPRLocalMetadataId =
			newOAuthClientPRLocalMetadata.getOAuthClientPRLocalMetadataId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"oAuthClientPRLocalMetadataId",
				new Object[] {newOAuthClientPRLocalMetadataId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingOAuthClientPRLocalMetadataId = result.get(0);

		Assert.assertEquals(
			existingOAuthClientPRLocalMetadataId,
			newOAuthClientPRLocalMetadataId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientPRLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("oAuthClientPRLocalMetadataId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"oAuthClientPRLocalMetadataId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newOAuthClientPRLocalMetadata.getPrimaryKey()));
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

		OAuthClientPRLocalMetadata newOAuthClientPRLocalMetadata =
			addOAuthClientPRLocalMetadata();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			OAuthClientPRLocalMetadata.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"oAuthClientPRLocalMetadataId",
				newOAuthClientPRLocalMetadata.
					getOAuthClientPRLocalMetadataId()));

		List<OAuthClientPRLocalMetadata> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata) {

		Assert.assertEquals(
			Long.valueOf(oAuthClientPRLocalMetadata.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				oAuthClientPRLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			oAuthClientPRLocalMetadata.getLocalWellKnownURI(),
			ReflectionTestUtil.invoke(
				oAuthClientPRLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "localWellKnownURI"));

		Assert.assertEquals(
			Long.valueOf(oAuthClientPRLocalMetadata.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				oAuthClientPRLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			oAuthClientPRLocalMetadata.getResource(),
			ReflectionTestUtil.invoke(
				oAuthClientPRLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "resource"));

		Assert.assertEquals(
			oAuthClientPRLocalMetadata.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				oAuthClientPRLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(oAuthClientPRLocalMetadata.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				oAuthClientPRLocalMetadata, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected OAuthClientPRLocalMetadata addOAuthClientPRLocalMetadata()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		OAuthClientPRLocalMetadata oAuthClientPRLocalMetadata =
			_persistence.create(pk);

		oAuthClientPRLocalMetadata.setMvccVersion(RandomTestUtil.nextLong());

		oAuthClientPRLocalMetadata.setUuid(RandomTestUtil.randomString());

		oAuthClientPRLocalMetadata.setExternalReferenceCode(
			RandomTestUtil.randomString());

		oAuthClientPRLocalMetadata.setCompanyId(RandomTestUtil.nextLong());

		oAuthClientPRLocalMetadata.setUserId(RandomTestUtil.nextLong());

		oAuthClientPRLocalMetadata.setUserName(RandomTestUtil.randomString());

		oAuthClientPRLocalMetadata.setCreateDate(RandomTestUtil.nextDate());

		oAuthClientPRLocalMetadata.setModifiedDate(RandomTestUtil.nextDate());

		oAuthClientPRLocalMetadata.setLocalWellKnownEnabled(
			RandomTestUtil.randomBoolean());

		oAuthClientPRLocalMetadata.setLocalWellKnownURI(
			RandomTestUtil.randomString());

		oAuthClientPRLocalMetadata.setMetadataJSON(
			RandomTestUtil.randomString());

		oAuthClientPRLocalMetadata.setResource(RandomTestUtil.randomString());

		_oAuthClientPRLocalMetadatas.add(
			_persistence.update(oAuthClientPRLocalMetadata));

		return oAuthClientPRLocalMetadata;
	}

	private List<OAuthClientPRLocalMetadata> _oAuthClientPRLocalMetadatas =
		new ArrayList<OAuthClientPRLocalMetadata>();
	private OAuthClientPRLocalMetadataPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1177462802