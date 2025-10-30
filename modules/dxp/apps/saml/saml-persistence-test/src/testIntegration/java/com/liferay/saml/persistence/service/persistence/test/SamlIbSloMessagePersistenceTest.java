/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.saml.persistence.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.saml.persistence.exception.NoSuchIbSloMessageException;
import com.liferay.saml.persistence.model.SamlIbSloMessage;
import com.liferay.saml.persistence.service.persistence.SamlIbSloMessagePersistence;
import com.liferay.saml.persistence.service.persistence.SamlIbSloMessageUtil;

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
public class SamlIbSloMessagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.saml.persistence.service"));

	@Before
	public void setUp() {
		_persistence = SamlIbSloMessageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SamlIbSloMessage> iterator = _samlIbSloMessages.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIbSloMessage samlIbSloMessage = _persistence.create(pk);

		Assert.assertNotNull(samlIbSloMessage);

		Assert.assertEquals(samlIbSloMessage.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SamlIbSloMessage newSamlIbSloMessage = addSamlIbSloMessage();

		_persistence.remove(newSamlIbSloMessage);

		SamlIbSloMessage existingSamlIbSloMessage =
			_persistence.fetchByPrimaryKey(newSamlIbSloMessage.getPrimaryKey());

		Assert.assertNull(existingSamlIbSloMessage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSamlIbSloMessage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIbSloMessage newSamlIbSloMessage = _persistence.create(pk);

		newSamlIbSloMessage.setCompanyId(RandomTestUtil.nextLong());

		newSamlIbSloMessage.setCreateDate(RandomTestUtil.nextDate());

		newSamlIbSloMessage.setSamlIdpEntityId(RandomTestUtil.randomString());

		newSamlIbSloMessage.setLogoutRequestXml(RandomTestUtil.randomString());

		newSamlIbSloMessage.setSamlIdpSessionIndex(
			RandomTestUtil.randomString());

		_samlIbSloMessages.add(_persistence.update(newSamlIbSloMessage));

		SamlIbSloMessage existingSamlIbSloMessage =
			_persistence.findByPrimaryKey(newSamlIbSloMessage.getPrimaryKey());

		Assert.assertEquals(
			existingSamlIbSloMessage.getSamlIbSloMessageId(),
			newSamlIbSloMessage.getSamlIbSloMessageId());
		Assert.assertEquals(
			existingSamlIbSloMessage.getCompanyId(),
			newSamlIbSloMessage.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSamlIbSloMessage.getCreateDate()),
			Time.getShortTimestamp(newSamlIbSloMessage.getCreateDate()));
		Assert.assertEquals(
			existingSamlIbSloMessage.getSamlIdpEntityId(),
			newSamlIbSloMessage.getSamlIdpEntityId());
		Assert.assertEquals(
			existingSamlIbSloMessage.getLogoutRequestXml(),
			newSamlIbSloMessage.getLogoutRequestXml());
		Assert.assertEquals(
			existingSamlIbSloMessage.getSamlIdpSessionIndex(),
			newSamlIbSloMessage.getSamlIdpSessionIndex());
	}

	@Test
	public void testCountBySamlIdpSessionIndex() throws Exception {
		_persistence.countBySamlIdpSessionIndex("");

		_persistence.countBySamlIdpSessionIndex("null");

		_persistence.countBySamlIdpSessionIndex((String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SamlIbSloMessage newSamlIbSloMessage = addSamlIbSloMessage();

		SamlIbSloMessage existingSamlIbSloMessage =
			_persistence.findByPrimaryKey(newSamlIbSloMessage.getPrimaryKey());

		Assert.assertEquals(existingSamlIbSloMessage, newSamlIbSloMessage);
	}

	@Test(expected = NoSuchIbSloMessageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SamlIbSloMessage> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SamlIbSloMessage", "samlIbSloMessageId", true, "companyId", true,
			"createDate", true, "samlIdpEntityId", true, "samlIdpSessionIndex",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SamlIbSloMessage newSamlIbSloMessage = addSamlIbSloMessage();

		SamlIbSloMessage existingSamlIbSloMessage =
			_persistence.fetchByPrimaryKey(newSamlIbSloMessage.getPrimaryKey());

		Assert.assertEquals(existingSamlIbSloMessage, newSamlIbSloMessage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIbSloMessage missingSamlIbSloMessage =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSamlIbSloMessage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SamlIbSloMessage newSamlIbSloMessage1 = addSamlIbSloMessage();
		SamlIbSloMessage newSamlIbSloMessage2 = addSamlIbSloMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIbSloMessage1.getPrimaryKey());
		primaryKeys.add(newSamlIbSloMessage2.getPrimaryKey());

		Map<Serializable, SamlIbSloMessage> samlIbSloMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, samlIbSloMessages.size());
		Assert.assertEquals(
			newSamlIbSloMessage1,
			samlIbSloMessages.get(newSamlIbSloMessage1.getPrimaryKey()));
		Assert.assertEquals(
			newSamlIbSloMessage2,
			samlIbSloMessages.get(newSamlIbSloMessage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SamlIbSloMessage> samlIbSloMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIbSloMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SamlIbSloMessage newSamlIbSloMessage = addSamlIbSloMessage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIbSloMessage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SamlIbSloMessage> samlIbSloMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIbSloMessages.size());
		Assert.assertEquals(
			newSamlIbSloMessage,
			samlIbSloMessages.get(newSamlIbSloMessage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SamlIbSloMessage> samlIbSloMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(samlIbSloMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SamlIbSloMessage newSamlIbSloMessage = addSamlIbSloMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSamlIbSloMessage.getPrimaryKey());

		Map<Serializable, SamlIbSloMessage> samlIbSloMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, samlIbSloMessages.size());
		Assert.assertEquals(
			newSamlIbSloMessage,
			samlIbSloMessages.get(newSamlIbSloMessage.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SamlIbSloMessage newSamlIbSloMessage = addSamlIbSloMessage();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSamlIbSloMessage.getPrimaryKey()));
	}

	private void _assertOriginalValues(SamlIbSloMessage samlIbSloMessage) {
		Assert.assertEquals(
			samlIbSloMessage.getSamlIdpSessionIndex(),
			ReflectionTestUtil.invoke(
				samlIbSloMessage, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "samlIdpSessionIndex"));
	}

	protected SamlIbSloMessage addSamlIbSloMessage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SamlIbSloMessage samlIbSloMessage = _persistence.create(pk);

		samlIbSloMessage.setCompanyId(RandomTestUtil.nextLong());

		samlIbSloMessage.setCreateDate(RandomTestUtil.nextDate());

		samlIbSloMessage.setSamlIdpEntityId(RandomTestUtil.randomString());

		samlIbSloMessage.setLogoutRequestXml(RandomTestUtil.randomString());

		samlIbSloMessage.setSamlIdpSessionIndex(RandomTestUtil.randomString());

		_samlIbSloMessages.add(_persistence.update(samlIbSloMessage));

		return samlIbSloMessage;
	}

	private List<SamlIbSloMessage> _samlIbSloMessages =
		new ArrayList<SamlIbSloMessage>();
	private SamlIbSloMessagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}