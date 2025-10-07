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
import com.liferay.saml.persistence.exception.NoSuchSpIdpConnectionException;
import com.liferay.saml.persistence.model.SamlSpIdpConnection;
import com.liferay.saml.persistence.service.SamlSpIdpConnectionLocalServiceUtil;
import com.liferay.saml.persistence.service.persistence.SamlSpIdpConnectionPersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpIdpConnectionUtil;

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
public class SamlSpIdpConnectionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlSpIdpConnectionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlSpIdpConnection> iterator =
			_samlSpIdpConnections.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpIdpConnection samlSpIdpConnection = _persistence.create(pk);

		Assert.assertNotNull(samlSpIdpConnection);

		Assert.assertEquals(samlSpIdpConnection.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		_persistence.remove(newSamlSpIdpConnection);

		SamlSpIdpConnection existingSamlSpIdpConnection =
			_persistence.fetchByPrimaryKey(
				newSamlSpIdpConnection.getPrimaryKey());

		Assert.assertNull(existingSamlSpIdpConnection);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlSpIdpConnection();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpIdpConnection newSamlSpIdpConnection = _persistence.create(pk);

		newSamlSpIdpConnection.setCompanyId(RandomTestUtil.nextLong());

		newSamlSpIdpConnection.setUserId(RandomTestUtil.nextLong());

		newSamlSpIdpConnection.setUserName(RandomTestUtil.randomString());

		newSamlSpIdpConnection.setCreateDate(RandomTestUtil.nextDate());

		newSamlSpIdpConnection.setModifiedDate(RandomTestUtil.nextDate());

		newSamlSpIdpConnection.setSamlIdpEntityId(
			RandomTestUtil.randomString());

		newSamlSpIdpConnection.setAssertionSignatureRequired(
			RandomTestUtil.randomBoolean());

		newSamlSpIdpConnection.setClockSkew(RandomTestUtil.nextLong());

		newSamlSpIdpConnection.setEnabled(RandomTestUtil.randomBoolean());

		newSamlSpIdpConnection.setForceAuthn(RandomTestUtil.randomBoolean());

		newSamlSpIdpConnection.setLdapImportEnabled(
			RandomTestUtil.randomBoolean());

		newSamlSpIdpConnection.setMetadataUpdatedDate(
			RandomTestUtil.nextDate());

		newSamlSpIdpConnection.setMetadataUrl(RandomTestUtil.randomString());

		newSamlSpIdpConnection.setMetadataXml(RandomTestUtil.randomString());

		newSamlSpIdpConnection.setName(RandomTestUtil.randomString());

		newSamlSpIdpConnection.setNameIdFormat(RandomTestUtil.randomString());

		newSamlSpIdpConnection.setSignAuthnRequest(
			RandomTestUtil.randomBoolean());

		newSamlSpIdpConnection.setUnknownUsersAreStrangers(
			RandomTestUtil.randomBoolean());

		newSamlSpIdpConnection.setUserAttributeMappings(
			RandomTestUtil.randomString());

		newSamlSpIdpConnection.setUserIdentifierExpression(
			RandomTestUtil.randomString());

		_samlSpIdpConnections.add(_persistence.update(newSamlSpIdpConnection));

		SamlSpIdpConnection existingSamlSpIdpConnection =
			_persistence.findByPrimaryKey(
				newSamlSpIdpConnection.getPrimaryKey());

		Assert.assertEquals(
			existingSamlSpIdpConnection.getSamlSpIdpConnectionId(),
			newSamlSpIdpConnection.getSamlSpIdpConnectionId());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getCompanyId(),
			newSamlSpIdpConnection.getCompanyId());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getUserId(),
			newSamlSpIdpConnection.getUserId());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getUserName(),
			newSamlSpIdpConnection.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlSpIdpConnection.getCreateDate()),
			Time.getShortTimestamp(newSamlSpIdpConnection.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSamlSpIdpConnection.getModifiedDate()),
			Time.getShortTimestamp(newSamlSpIdpConnection.getModifiedDate()));
		Assert.assertEquals(
			existingSamlSpIdpConnection.getSamlIdpEntityId(),
			newSamlSpIdpConnection.getSamlIdpEntityId());
		Assert.assertEquals(
			existingSamlSpIdpConnection.isAssertionSignatureRequired(),
			newSamlSpIdpConnection.isAssertionSignatureRequired());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getClockSkew(),
			newSamlSpIdpConnection.getClockSkew());
		Assert.assertEquals(
			existingSamlSpIdpConnection.isEnabled(),
			newSamlSpIdpConnection.isEnabled());
		Assert.assertEquals(
			existingSamlSpIdpConnection.isForceAuthn(),
			newSamlSpIdpConnection.isForceAuthn());
		Assert.assertEquals(
			existingSamlSpIdpConnection.isLdapImportEnabled(),
			newSamlSpIdpConnection.isLdapImportEnabled());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSamlSpIdpConnection.getMetadataUpdatedDate()),
			Time.getShortTimestamp(
				newSamlSpIdpConnection.getMetadataUpdatedDate()));
		Assert.assertEquals(
			existingSamlSpIdpConnection.getMetadataUrl(),
			newSamlSpIdpConnection.getMetadataUrl());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getMetadataXml(),
			newSamlSpIdpConnection.getMetadataXml());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getName(),
			newSamlSpIdpConnection.getName());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getNameIdFormat(),
			newSamlSpIdpConnection.getNameIdFormat());
		Assert.assertEquals(
			existingSamlSpIdpConnection.isSignAuthnRequest(),
			newSamlSpIdpConnection.isSignAuthnRequest());
		Assert.assertEquals(
			existingSamlSpIdpConnection.isUnknownUsersAreStrangers(),
			newSamlSpIdpConnection.isUnknownUsersAreStrangers());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getUserAttributeMappings(),
			newSamlSpIdpConnection.getUserAttributeMappings());
		Assert.assertEquals(
			existingSamlSpIdpConnection.getUserIdentifierExpression(),
			newSamlSpIdpConnection.getUserIdentifierExpression());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_SIEI() throws Exception {
		_persistence.countByC_SIEI(RandomTestUtil.nextLong(), "");

		_persistence.countByC_SIEI(0L, "null");

		_persistence.countByC_SIEI(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		SamlSpIdpConnection existingSamlSpIdpConnection =
			_persistence.findByPrimaryKey(
				newSamlSpIdpConnection.getPrimaryKey());

		Assert.assertEquals(
			existingSamlSpIdpConnection, newSamlSpIdpConnection);
	}

	@Test(expected = NoSuchSpIdpConnectionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlSpIdpConnection> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlSpIdpConnection", "samlSpIdpConnectionId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "samlIdpEntityId", true,
			"assertionSignatureRequired", true, "clockSkew", true, "enabled",
			true, "forceAuthn", true, "ldapImportEnabled", true,
			"metadataUpdatedDate", true, "metadataUrl", true, "name", true,
			"nameIdFormat", true, "signAuthnRequest", true,
			"unknownUsersAreStrangers", true, "userAttributeMappings", true,
			"userIdentifierExpression", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		SamlSpIdpConnection existingSamlSpIdpConnection =
			_persistence.fetchByPrimaryKey(
				newSamlSpIdpConnection.getPrimaryKey());

		Assert.assertEquals(
			existingSamlSpIdpConnection, newSamlSpIdpConnection);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpIdpConnection missingSamlSpIdpConnection =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlSpIdpConnection);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlSpIdpConnection newSamlSpIdpConnection1 = addSamlSpIdpConnection();
		SamlSpIdpConnection newSamlSpIdpConnection2 = addSamlSpIdpConnection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpIdpConnection1.getPrimaryKey());
		primaryKeys.add(newSamlSpIdpConnection2.getPrimaryKey());

		Map<Serializable, SamlSpIdpConnection> samlSpIdpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlSpIdpConnections.size());
		Assert.assertEquals(
			newSamlSpIdpConnection1,
			samlSpIdpConnections.get(newSamlSpIdpConnection1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlSpIdpConnection2,
			samlSpIdpConnections.get(newSamlSpIdpConnection2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlSpIdpConnection> samlSpIdpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpIdpConnections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpIdpConnection.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlSpIdpConnection> samlSpIdpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpIdpConnections.size());
		Assert.assertEquals(
			newSamlSpIdpConnection,
			samlSpIdpConnections.get(newSamlSpIdpConnection.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlSpIdpConnection> samlSpIdpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpIdpConnections.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpIdpConnection.getPrimaryKey());

		Map<Serializable, SamlSpIdpConnection> samlSpIdpConnections =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpIdpConnections.size());
		Assert.assertEquals(
			newSamlSpIdpConnection,
			samlSpIdpConnections.get(newSamlSpIdpConnection.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SamlSpIdpConnectionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SamlSpIdpConnection>() {

				@Override
				public void performAction(
					SamlSpIdpConnection samlSpIdpConnection) {

					Assert.assertNotNull(samlSpIdpConnection);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpIdpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpIdpConnectionId",
				newSamlSpIdpConnection.getSamlSpIdpConnectionId()));

		List<SamlSpIdpConnection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SamlSpIdpConnection existingSamlSpIdpConnection = result.get(0);

		Assert.assertEquals(
			existingSamlSpIdpConnection, newSamlSpIdpConnection);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpIdpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpIdpConnectionId", RandomTestUtil.nextLong()));

		List<SamlSpIdpConnection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpIdpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlSpIdpConnectionId"));

		Object newSamlSpIdpConnectionId =
			newSamlSpIdpConnection.getSamlSpIdpConnectionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlSpIdpConnectionId",
				new Object[] {newSamlSpIdpConnectionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSamlSpIdpConnectionId = result.get(0);

		Assert.assertEquals(
			existingSamlSpIdpConnectionId, newSamlSpIdpConnectionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpIdpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlSpIdpConnectionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlSpIdpConnectionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSamlSpIdpConnection.getPrimaryKey()));
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

		SamlSpIdpConnection newSamlSpIdpConnection = addSamlSpIdpConnection();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpIdpConnection.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpIdpConnectionId",
				newSamlSpIdpConnection.getSamlSpIdpConnectionId()));

		List<SamlSpIdpConnection> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		SamlSpIdpConnection samlSpIdpConnection) {

		Assert.assertEquals(
			Long.valueOf(samlSpIdpConnection.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				samlSpIdpConnection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			samlSpIdpConnection.getSamlIdpEntityId(),
			ReflectionTestUtil.invoke(
				samlSpIdpConnection, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlIdpEntityId"));
	}

	protected SamlSpIdpConnection addSamlSpIdpConnection() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpIdpConnection samlSpIdpConnection = _persistence.create(pk);

		samlSpIdpConnection.setCompanyId(RandomTestUtil.nextLong());

		samlSpIdpConnection.setUserId(RandomTestUtil.nextLong());

		samlSpIdpConnection.setUserName(RandomTestUtil.randomString());

		samlSpIdpConnection.setCreateDate(RandomTestUtil.nextDate());

		samlSpIdpConnection.setModifiedDate(RandomTestUtil.nextDate());

		samlSpIdpConnection.setSamlIdpEntityId(RandomTestUtil.randomString());

		samlSpIdpConnection.setAssertionSignatureRequired(
			RandomTestUtil.randomBoolean());

		samlSpIdpConnection.setClockSkew(RandomTestUtil.nextLong());

		samlSpIdpConnection.setEnabled(RandomTestUtil.randomBoolean());

		samlSpIdpConnection.setForceAuthn(RandomTestUtil.randomBoolean());

		samlSpIdpConnection.setLdapImportEnabled(
			RandomTestUtil.randomBoolean());

		samlSpIdpConnection.setMetadataUpdatedDate(RandomTestUtil.nextDate());

		samlSpIdpConnection.setMetadataUrl(RandomTestUtil.randomString());

		samlSpIdpConnection.setMetadataXml(RandomTestUtil.randomString());

		samlSpIdpConnection.setName(RandomTestUtil.randomString());

		samlSpIdpConnection.setNameIdFormat(RandomTestUtil.randomString());

		samlSpIdpConnection.setSignAuthnRequest(RandomTestUtil.randomBoolean());

		samlSpIdpConnection.setUnknownUsersAreStrangers(
			RandomTestUtil.randomBoolean());

		samlSpIdpConnection.setUserAttributeMappings(
			RandomTestUtil.randomString());

		samlSpIdpConnection.setUserIdentifierExpression(
			RandomTestUtil.randomString());

		_samlSpIdpConnections.add(_persistence.update(samlSpIdpConnection));

		return samlSpIdpConnection;
	}

	private List<SamlSpIdpConnection> _samlSpIdpConnections =
		new ArrayList<SamlSpIdpConnection>();
	private SamlSpIdpConnectionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}