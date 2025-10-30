/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchMessageException;
import com.liferay.change.tracking.model.CTMessage;
import com.liferay.change.tracking.service.persistence.CTMessagePersistence;
import com.liferay.change.tracking.service.persistence.CTMessageUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class CTMessagePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTMessageUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTMessage> iterator = _ctMessages.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTMessage ctMessage = _persistence.create(pk);

		Assert.assertNotNull(ctMessage);

		Assert.assertEquals(ctMessage.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTMessage newCTMessage = addCTMessage();

		_persistence.remove(newCTMessage);

		CTMessage existingCTMessage = _persistence.fetchByPrimaryKey(
			newCTMessage.getPrimaryKey());

		Assert.assertNull(existingCTMessage);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTMessage();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTMessage newCTMessage = _persistence.create(pk);

		newCTMessage.setMvccVersion(RandomTestUtil.nextLong());

		newCTMessage.setCompanyId(RandomTestUtil.nextLong());

		newCTMessage.setCtCollectionId(RandomTestUtil.nextLong());

		newCTMessage.setMessageContent(RandomTestUtil.randomString());

		_ctMessages.add(_persistence.update(newCTMessage));

		CTMessage existingCTMessage = _persistence.findByPrimaryKey(
			newCTMessage.getPrimaryKey());

		Assert.assertEquals(
			existingCTMessage.getMvccVersion(), newCTMessage.getMvccVersion());
		Assert.assertEquals(
			existingCTMessage.getCtMessageId(), newCTMessage.getCtMessageId());
		Assert.assertEquals(
			existingCTMessage.getCompanyId(), newCTMessage.getCompanyId());
		Assert.assertEquals(
			existingCTMessage.getCtCollectionId(),
			newCTMessage.getCtCollectionId());
		Assert.assertEquals(
			existingCTMessage.getMessageContent(),
			newCTMessage.getMessageContent());
	}

	@Test
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTMessage newCTMessage = addCTMessage();

		CTMessage existingCTMessage = _persistence.findByPrimaryKey(
			newCTMessage.getPrimaryKey());

		Assert.assertEquals(existingCTMessage, newCTMessage);
	}

	@Test(expected = NoSuchMessageException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTMessage> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTMessage", "mvccVersion", true, "ctMessageId", true, "companyId",
			true, "ctCollectionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTMessage newCTMessage = addCTMessage();

		CTMessage existingCTMessage = _persistence.fetchByPrimaryKey(
			newCTMessage.getPrimaryKey());

		Assert.assertEquals(existingCTMessage, newCTMessage);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTMessage missingCTMessage = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTMessage);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTMessage newCTMessage1 = addCTMessage();
		CTMessage newCTMessage2 = addCTMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTMessage1.getPrimaryKey());
		primaryKeys.add(newCTMessage2.getPrimaryKey());

		Map<Serializable, CTMessage> ctMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctMessages.size());
		Assert.assertEquals(
			newCTMessage1, ctMessages.get(newCTMessage1.getPrimaryKey()));
		Assert.assertEquals(
			newCTMessage2, ctMessages.get(newCTMessage2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTMessage> ctMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTMessage newCTMessage = addCTMessage();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTMessage.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTMessage> ctMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctMessages.size());
		Assert.assertEquals(
			newCTMessage, ctMessages.get(newCTMessage.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTMessage> ctMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctMessages.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTMessage newCTMessage = addCTMessage();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTMessage.getPrimaryKey());

		Map<Serializable, CTMessage> ctMessages =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctMessages.size());
		Assert.assertEquals(
			newCTMessage, ctMessages.get(newCTMessage.getPrimaryKey()));
	}

	protected CTMessage addCTMessage() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTMessage ctMessage = _persistence.create(pk);

		ctMessage.setMvccVersion(RandomTestUtil.nextLong());

		ctMessage.setCompanyId(RandomTestUtil.nextLong());

		ctMessage.setCtCollectionId(RandomTestUtil.nextLong());

		ctMessage.setMessageContent(RandomTestUtil.randomString());

		_ctMessages.add(_persistence.update(ctMessage));

		return ctMessage;
	}

	private List<CTMessage> _ctMessages = new ArrayList<CTMessage>();
	private CTMessagePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}