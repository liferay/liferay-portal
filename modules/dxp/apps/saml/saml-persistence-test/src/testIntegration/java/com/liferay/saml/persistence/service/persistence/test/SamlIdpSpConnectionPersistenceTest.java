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
import com.liferay.saml.persistence.exception.NoSuchIdpSpConnectionException;
import com.liferay.saml.persistence.model.SamlIdpSpConnection;
import com.liferay.saml.persistence.service.SamlIdpSpConnectionLocalServiceUtil;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpConnectionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlIdpSpConnectionUtil;

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
public class SamlIdpSpConnectionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlIdpSpConnectionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlIdpSpConnection> iterator =
			_samlIdpSpConnections.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpConnection samlIdpSpConnection = _persistence.create(pk);

		Assert.assertNotNull(samlIdpSpConnection);

		Assert.assertEquals(samlIdpSpConnection.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		_persistence.remove(newSamlIdpSpConnection);

		SamlIdpSpConnection existingSamlIdpSpConnection =
			_persistence.fetchByPrimaryKey(
				newSamlIdpSpConnection.getPrimaryKey());

		Assert.assertNull(existingSamlIdpSpConnection);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlIdpSpConnection();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpConnection newSamlIdpSpConnection = _persistence.create(pk);

		newSamlIdpSpConnection.setCompanyId(RandomTestUtil.nextLong());

		newSamlIdpSpConnection.setUserId(RandomTestUtil.nextLong());

		newSamlIdpSpConnection.setUserName(RandomTestUtil.randomString());

		newSamlIdpSpConnection.setCreateDate(RandomTestUtil.nextDate());

		newSamlIdpSpConnection.setModifiedDate(RandomTestUtil.nextDate());

		newSamlIdpSpConnection.setSamlSpEntityId(RandomTestUtil.randomString());

		newSamlIdpSpConnection.setAssertionLifetime(RandomTestUtil.nextInt());

		newSamlIdpSpConnection.setAttributeNames(RandomTestUtil.randomString());

		newSamlIdpSpConnection.setAttributesEnabled(
			RandomTestUtil.randomBoolean());

		newSamlIdpSpConnection.setAttributesNamespaceEnabled(
			RandomTestUtil.randomBoolean());

		newSamlIdpSpConnection.setEnabled(RandomTestUtil.randomBoolean());

		newSamlIdpSpConnection.setEncryptionForced(
			RandomTestUtil.randomBoolean());

		newSamlIdpSpConnection.setMetadataUpdatedDate(
			RandomTestUtil.nextDate());

		newSamlIdpSpConnection.setMetadataUrl(RandomTestUtil.randomString());

		newSamlIdpSpConnection.setMetadataXml(RandomTestUtil.randomString());

		newSamlIdpSpConnection.setName(RandomTestUtil.randomString());

		newSamlIdpSpConnection.setNameIdAttribute(
			RandomTestUtil.randomString());

		newSamlIdpSpConnection.setNameIdFormat(RandomTestUtil.randomString());

		_samlIdpSpConnections.add(_persistence.update(newSamlIdpSpConnection));

		SamlIdpSpConnection existingSamlIdpSpConnection =
			_persistence.findByPrimaryKey(
				newSamlIdpSpConnection.getPrimaryKey());

		Assert.assertEquals(
			existingSamlIdpSpConnection.getSamlIdpSpConnectionId(),
			newSamlIdpSpConnection.getSamlIdpSpConnectionId());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getCompanyId(),
			newSamlIdpSpConnection.getCompanyId());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getUserId(),
			newSamlIdpSpConnection.getUserId());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getUserName(),
			newSamlIdpSpConnection.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlIdpSpConnection.getCreateDate()),
			Time.getShortTimestamp(newSamlIdpSpConnection.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSamlIdpSpConnection.getModifiedDate()),
			Time.getShortTimestamp(newSamlIdpSpConnection.getModifiedDate()));
		Assert.assertEquals(
			existingSamlIdpSpConnection.getSamlSpEntityId(),
			newSamlIdpSpConnection.getSamlSpEntityId());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getAssertionLifetime(),
			newSamlIdpSpConnection.getAssertionLifetime());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getAttributeNames(),
			newSamlIdpSpConnection.getAttributeNames());
		Assert.assertEquals(
			existingSamlIdpSpConnection.isAttributesEnabled(),
			newSamlIdpSpConnection.isAttributesEnabled());
		Assert.assertEquals(
			existingSamlIdpSpConnection.isAttributesNamespaceEnabled(),
			newSamlIdpSpConnection.isAttributesNamespaceEnabled());
		Assert.assertEquals(
			existingSamlIdpSpConnection.isEnabled(),
			newSamlIdpSpConnection.isEnabled());
		Assert.assertEquals(
			existingSamlIdpSpConnection.isEncryptionForced(),
			newSamlIdpSpConnection.isEncryptionForced());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSamlIdpSpConnection.getMetadataUpdatedDate()),
			Time.getShortTimestamp(
				newSamlIdpSpConnection.getMetadataUpdatedDate()));
		Assert.assertEquals(
			existingSamlIdpSpConnection.getMetadataUrl(),
			newSamlIdpSpConnection.getMetadataUrl());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getMetadataXml(),
			newSamlIdpSpConnection.getMetadataXml());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getName(),
			newSamlIdpSpConnection.getName());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getNameIdAttribute(),
			newSamlIdpSpConnection.getNameIdAttribute());
		Assert.assertEquals(
			existingSamlIdpSpConnection.getNameIdFormat(),
			newSamlIdpSpConnection.getNameIdFormat());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_SSEI() throws Exception {
		_persistence.countByC_SSEI(RandomTestUtil.nextLong(), "");

		_persistence.countByC_SSEI(0L, "null");

		_persistence.countByC_SSEI(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		SamlIdpSpConnection existingSamlIdpSpConnection =
			_persistence.findByPrimaryKey(
				newSamlIdpSpConnection.getPrimaryKey());

		Assert.assertEquals(
			existingSamlIdpSpConnection, newSamlIdpSpConnection);
	}

	@Test(expected = NoSuchIdpSpConnectionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlIdpSpConnection> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlIdpSpConnection", "samlIdpSpConnectionId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "samlSpEntityId", true, "assertionLifetime",
			true, "attributeNames", true, "attributesEnabled", true,
			"attributesNamespaceEnabled", true, "enabled", true,
			"encryptionForced", true, "metadataUpdatedDate", true,
			"metadataUrl", true, "name", true, "nameIdAttribute", true,
			"nameIdFormat", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		SamlIdpSpConnection existingSamlIdpSpConnection =
			_persistence.fetchByPrimaryKey(
				newSamlIdpSpConnection.getPrimaryKey());

		Assert.assertEquals(
			existingSamlIdpSpConnection, newSamlIdpSpConnection);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpConnection missingSamlIdpSpConnection =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlIdpSpConnection);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlIdpSpConnection newSamlIdpSpConnection1 = addSamlIdpSpConnection();
		SamlIdpSpConnection newSamlIdpSpConnection2 = addSamlIdpSpConnection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSpConnection1.getPrimaryKey());
		primaryKeys.add(newSamlIdpSpConnection2.getPrimaryKey());

		Map<Serializable, SamlIdpSpConnection> samlIdpSpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlIdpSpConnections.size());
		Assert.assertEquals(
			newSamlIdpSpConnection1,
			samlIdpSpConnections.get(newSamlIdpSpConnection1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlIdpSpConnection2,
			samlIdpSpConnections.get(newSamlIdpSpConnection2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlIdpSpConnection> samlIdpSpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIdpSpConnections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSpConnection.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlIdpSpConnection> samlIdpSpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIdpSpConnections.size());
		Assert.assertEquals(
			newSamlIdpSpConnection,
			samlIdpSpConnections.get(newSamlIdpSpConnection.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlIdpSpConnection> samlIdpSpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIdpSpConnections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIdpSpConnection.getPrimaryKey());

		Map<Serializable, SamlIdpSpConnection> samlIdpSpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIdpSpConnections.size());
		Assert.assertEquals(
			newSamlIdpSpConnection,
			samlIdpSpConnections.get(newSamlIdpSpConnection.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SamlIdpSpConnectionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SamlIdpSpConnection>() {

				@Override
				public void performAction(
					SamlIdpSpConnection samlIdpSpConnection) {

					Assert.assertNotNull(samlIdpSpConnection);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlIdpSpConnectionId",
				newSamlIdpSpConnection.getSamlIdpSpConnectionId()));

		List<SamlIdpSpConnection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SamlIdpSpConnection existingSamlIdpSpConnection = result.get(0);

		Assert.assertEquals(
			existingSamlIdpSpConnection, newSamlIdpSpConnection);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlIdpSpConnectionId", RandomTestUtil.nextLong()));

		List<SamlIdpSpConnection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlIdpSpConnectionId"));

		Object newSamlIdpSpConnectionId =
			newSamlIdpSpConnection.getSamlIdpSpConnectionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlIdpSpConnectionId",
				new Object[] {newSamlIdpSpConnectionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSamlIdpSpConnectionId = result.get(0);

		Assert.assertEquals(
			existingSamlIdpSpConnectionId, newSamlIdpSpConnectionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlIdpSpConnectionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlIdpSpConnectionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSamlIdpSpConnection.getPrimaryKey()));
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

		SamlIdpSpConnection newSamlIdpSpConnection = addSamlIdpSpConnection();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlIdpSpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlIdpSpConnectionId",
				newSamlIdpSpConnection.getSamlIdpSpConnectionId()));

		List<SamlIdpSpConnection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		SamlIdpSpConnection samlIdpSpConnection) {

		Assert.assertEquals(
			Long.valueOf(samlIdpSpConnection.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				samlIdpSpConnection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			samlIdpSpConnection.getSamlSpEntityId(),
			ReflectionTestUtil.invoke(
				samlIdpSpConnection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlSpEntityId"));
	}

	protected SamlIdpSpConnection addSamlIdpSpConnection() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIdpSpConnection samlIdpSpConnection = _persistence.create(pk);

		samlIdpSpConnection.setCompanyId(RandomTestUtil.nextLong());

		samlIdpSpConnection.setUserId(RandomTestUtil.nextLong());

		samlIdpSpConnection.setUserName(RandomTestUtil.randomString());

		samlIdpSpConnection.setCreateDate(RandomTestUtil.nextDate());

		samlIdpSpConnection.setModifiedDate(RandomTestUtil.nextDate());

		samlIdpSpConnection.setSamlSpEntityId(RandomTestUtil.randomString());

		samlIdpSpConnection.setAssertionLifetime(RandomTestUtil.nextInt());

		samlIdpSpConnection.setAttributeNames(RandomTestUtil.randomString());

		samlIdpSpConnection.setAttributesEnabled(
			RandomTestUtil.randomBoolean());

		samlIdpSpConnection.setAttributesNamespaceEnabled(
			RandomTestUtil.randomBoolean());

		samlIdpSpConnection.setEnabled(RandomTestUtil.randomBoolean());

		samlIdpSpConnection.setEncryptionForced(RandomTestUtil.randomBoolean());

		samlIdpSpConnection.setMetadataUpdatedDate(RandomTestUtil.nextDate());

		samlIdpSpConnection.setMetadataUrl(RandomTestUtil.randomString());

		samlIdpSpConnection.setMetadataXml(RandomTestUtil.randomString());

		samlIdpSpConnection.setName(RandomTestUtil.randomString());

		samlIdpSpConnection.setNameIdAttribute(RandomTestUtil.randomString());

		samlIdpSpConnection.setNameIdFormat(RandomTestUtil.randomString());

		_samlIdpSpConnections.add(_persistence.update(samlIdpSpConnection));

		return samlIdpSpConnection;
	}

	private List<SamlIdpSpConnection> _samlIdpSpConnections =
		new ArrayList<SamlIdpSpConnection>();
	private SamlIdpSpConnectionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}