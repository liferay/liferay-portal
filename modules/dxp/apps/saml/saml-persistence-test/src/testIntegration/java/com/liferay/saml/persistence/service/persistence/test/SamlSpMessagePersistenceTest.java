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
import com.liferay.saml.persistence.exception.NoSuchSpMessageException;
import com.liferay.saml.persistence.model.SamlSpMessage;
import com.liferay.saml.persistence.service.SamlSpMessageLocalServiceUtil;
import com.liferay.saml.persistence.service.persistence.SamlSpMessagePersistence;
import com.liferay.saml.persistence.service.persistence.SamlSpMessageUtil;

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
public class SamlSpMessagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlSpMessageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlSpMessage> iterator = _samlSpMessages.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpMessage samlSpMessage = _persistence.create(pk);

		Assert.assertNotNull(samlSpMessage);

		Assert.assertEquals(samlSpMessage.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		_persistence.remove(newSamlSpMessage);

		SamlSpMessage existingSamlSpMessage = _persistence.fetchByPrimaryKey(
			newSamlSpMessage.getPrimaryKey());

		Assert.assertNull(existingSamlSpMessage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlSpMessage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpMessage newSamlSpMessage = _persistence.create(pk);

		newSamlSpMessage.setCompanyId(RandomTestUtil.nextLong());

		newSamlSpMessage.setCreateDate(RandomTestUtil.nextDate());

		newSamlSpMessage.setSamlIdpEntityId(RandomTestUtil.randomString());

		newSamlSpMessage.setExpirationDate(RandomTestUtil.nextDate());

		newSamlSpMessage.setSamlIdpResponseKey(RandomTestUtil.randomString());

		_samlSpMessages.add(_persistence.update(newSamlSpMessage));

		SamlSpMessage existingSamlSpMessage = _persistence.findByPrimaryKey(
			newSamlSpMessage.getPrimaryKey());

		Assert.assertEquals(
			existingSamlSpMessage.getSamlSpMessageId(),
			newSamlSpMessage.getSamlSpMessageId());
		Assert.assertEquals(
			existingSamlSpMessage.getCompanyId(),
			newSamlSpMessage.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlSpMessage.getCreateDate()),
			Time.getShortTimestamp(newSamlSpMessage.getCreateDate()));
		Assert.assertEquals(
			existingSamlSpMessage.getSamlIdpEntityId(),
			newSamlSpMessage.getSamlIdpEntityId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlSpMessage.getExpirationDate()),
			Time.getShortTimestamp(newSamlSpMessage.getExpirationDate()));
		Assert.assertEquals(
			existingSamlSpMessage.getSamlIdpResponseKey(),
			newSamlSpMessage.getSamlIdpResponseKey());
	}

	@Test
	public void testCountByLtExpirationDate() throws Exception {
		_persistence.countByLtExpirationDate(RandomTestUtil.nextDate());

		_persistence.countByLtExpirationDate(RandomTestUtil.nextDate());
	}

	@Test
	public void testCountBySIEI_SIRK() throws Exception {
		_persistence.countBySIEI_SIRK("", "");

		_persistence.countBySIEI_SIRK("null", "null");

		_persistence.countBySIEI_SIRK((String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		SamlSpMessage existingSamlSpMessage = _persistence.findByPrimaryKey(
			newSamlSpMessage.getPrimaryKey());

		Assert.assertEquals(existingSamlSpMessage, newSamlSpMessage);
	}

	@Test(expected = NoSuchSpMessageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlSpMessage> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlSpMessage", "samlSpMessageId", true, "companyId", true,
			"createDate", true, "samlIdpEntityId", true, "expirationDate", true,
			"samlIdpResponseKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		SamlSpMessage existingSamlSpMessage = _persistence.fetchByPrimaryKey(
			newSamlSpMessage.getPrimaryKey());

		Assert.assertEquals(existingSamlSpMessage, newSamlSpMessage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpMessage missingSamlSpMessage = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlSpMessage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlSpMessage newSamlSpMessage1 = addSamlSpMessage();
		SamlSpMessage newSamlSpMessage2 = addSamlSpMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpMessage1.getPrimaryKey());
		primaryKeys.add(newSamlSpMessage2.getPrimaryKey());

		Map<Serializable, SamlSpMessage> samlSpMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlSpMessages.size());
		Assert.assertEquals(
			newSamlSpMessage1,
			samlSpMessages.get(newSamlSpMessage1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlSpMessage2,
			samlSpMessages.get(newSamlSpMessage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlSpMessage> samlSpMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpMessage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlSpMessage> samlSpMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpMessages.size());
		Assert.assertEquals(
			newSamlSpMessage,
			samlSpMessages.get(newSamlSpMessage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlSpMessage> samlSpMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlSpMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlSpMessage.getPrimaryKey());

		Map<Serializable, SamlSpMessage> samlSpMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlSpMessages.size());
		Assert.assertEquals(
			newSamlSpMessage,
			samlSpMessages.get(newSamlSpMessage.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SamlSpMessageLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<SamlSpMessage>() {

				@Override
				public void performAction(SamlSpMessage samlSpMessage) {
					Assert.assertNotNull(samlSpMessage);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpMessage.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpMessageId", newSamlSpMessage.getSamlSpMessageId()));

		List<SamlSpMessage> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SamlSpMessage existingSamlSpMessage = result.get(0);

		Assert.assertEquals(existingSamlSpMessage, newSamlSpMessage);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpMessage.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpMessageId", RandomTestUtil.nextLong()));

		List<SamlSpMessage> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpMessage.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlSpMessageId"));

		Object newSamlSpMessageId = newSamlSpMessage.getSamlSpMessageId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlSpMessageId", new Object[] {newSamlSpMessageId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSamlSpMessageId = result.get(0);

		Assert.assertEquals(existingSamlSpMessageId, newSamlSpMessageId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpMessage.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("samlSpMessageId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"samlSpMessageId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSamlSpMessage.getPrimaryKey()));
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

		SamlSpMessage newSamlSpMessage = addSamlSpMessage();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SamlSpMessage.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"samlSpMessageId", newSamlSpMessage.getSamlSpMessageId()));

		List<SamlSpMessage> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(SamlSpMessage samlSpMessage) {
		Assert.assertEquals(
			samlSpMessage.getSamlIdpEntityId(),
			ReflectionTestUtil.invoke(
				samlSpMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlIdpEntityId"));
		Assert.assertEquals(
			samlSpMessage.getSamlIdpResponseKey(),
			ReflectionTestUtil.invoke(
				samlSpMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlIdpResponseKey"));
	}

	protected SamlSpMessage addSamlSpMessage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlSpMessage samlSpMessage = _persistence.create(pk);

		samlSpMessage.setCompanyId(RandomTestUtil.nextLong());

		samlSpMessage.setCreateDate(RandomTestUtil.nextDate());

		samlSpMessage.setSamlIdpEntityId(RandomTestUtil.randomString());

		samlSpMessage.setExpirationDate(RandomTestUtil.nextDate());

		samlSpMessage.setSamlIdpResponseKey(RandomTestUtil.randomString());

		_samlSpMessages.add(_persistence.update(samlSpMessage));

		return samlSpMessage;
	}

	private List<SamlSpMessage> _samlSpMessages =
		new ArrayList<SamlSpMessage>();
	private SamlSpMessagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}