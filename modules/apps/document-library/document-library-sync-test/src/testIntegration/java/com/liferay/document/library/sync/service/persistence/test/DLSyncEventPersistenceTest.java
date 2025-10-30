/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.document.library.sync.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.document.library.sync.exception.NoSuchEventException;
import com.liferay.document.library.sync.model.DLSyncEvent;
import com.liferay.document.library.sync.service.persistence.DLSyncEventPersistence;
import com.liferay.document.library.sync.service.persistence.DLSyncEventUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
public class DLSyncEventPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.document.library.sync.service"));

	@Before
	public void setUp() {
		_persistence = DLSyncEventUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DLSyncEvent> iterator = _dlSyncEvents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLSyncEvent dlSyncEvent = _persistence.create(pk);

		Assert.assertNotNull(dlSyncEvent);

		Assert.assertEquals(dlSyncEvent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DLSyncEvent newDLSyncEvent = addDLSyncEvent();

		_persistence.remove(newDLSyncEvent);

		DLSyncEvent existingDLSyncEvent = _persistence.fetchByPrimaryKey(
			newDLSyncEvent.getPrimaryKey());

		Assert.assertNull(existingDLSyncEvent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDLSyncEvent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLSyncEvent newDLSyncEvent = _persistence.create(pk);

		newDLSyncEvent.setCompanyId(RandomTestUtil.nextLong());

		newDLSyncEvent.setModifiedTime(RandomTestUtil.nextLong());

		newDLSyncEvent.setEvent(RandomTestUtil.randomString());

		newDLSyncEvent.setType(RandomTestUtil.randomString());

		newDLSyncEvent.setTypePK(RandomTestUtil.nextLong());

		_dlSyncEvents.add(_persistence.update(newDLSyncEvent));

		DLSyncEvent existingDLSyncEvent = _persistence.findByPrimaryKey(
			newDLSyncEvent.getPrimaryKey());

		Assert.assertEquals(
			existingDLSyncEvent.getSyncEventId(),
			newDLSyncEvent.getSyncEventId());
		Assert.assertEquals(
			existingDLSyncEvent.getCompanyId(), newDLSyncEvent.getCompanyId());
		Assert.assertEquals(
			existingDLSyncEvent.getModifiedTime(),
			newDLSyncEvent.getModifiedTime());
		Assert.assertEquals(
			existingDLSyncEvent.getEvent(), newDLSyncEvent.getEvent());
		Assert.assertEquals(
			existingDLSyncEvent.getType(), newDLSyncEvent.getType());
		Assert.assertEquals(
			existingDLSyncEvent.getTypePK(), newDLSyncEvent.getTypePK());
	}

	@Test
	public void testCountByGtModifiedTime() throws Exception {
		_persistence.countByGtModifiedTime(RandomTestUtil.nextLong());

		_persistence.countByGtModifiedTime(0L);
	}

	@Test
	public void testCountByTypePK() throws Exception {
		_persistence.countByTypePK(RandomTestUtil.nextLong());

		_persistence.countByTypePK(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DLSyncEvent newDLSyncEvent = addDLSyncEvent();

		DLSyncEvent existingDLSyncEvent = _persistence.findByPrimaryKey(
			newDLSyncEvent.getPrimaryKey());

		Assert.assertEquals(existingDLSyncEvent, newDLSyncEvent);
	}

	@Test(expected = NoSuchEventException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DLSyncEvent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DLSyncEvent", "syncEventId", true, "companyId", true,
			"modifiedTime", true, "event", true, "type", true, "typePK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DLSyncEvent newDLSyncEvent = addDLSyncEvent();

		DLSyncEvent existingDLSyncEvent = _persistence.fetchByPrimaryKey(
			newDLSyncEvent.getPrimaryKey());

		Assert.assertEquals(existingDLSyncEvent, newDLSyncEvent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLSyncEvent missingDLSyncEvent = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDLSyncEvent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DLSyncEvent newDLSyncEvent1 = addDLSyncEvent();
		DLSyncEvent newDLSyncEvent2 = addDLSyncEvent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLSyncEvent1.getPrimaryKey());
		primaryKeys.add(newDLSyncEvent2.getPrimaryKey());

		Map<Serializable, DLSyncEvent> dlSyncEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, dlSyncEvents.size());
		Assert.assertEquals(
			newDLSyncEvent1, dlSyncEvents.get(newDLSyncEvent1.getPrimaryKey()));
		Assert.assertEquals(
			newDLSyncEvent2, dlSyncEvents.get(newDLSyncEvent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DLSyncEvent> dlSyncEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlSyncEvents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DLSyncEvent newDLSyncEvent = addDLSyncEvent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLSyncEvent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DLSyncEvent> dlSyncEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlSyncEvents.size());
		Assert.assertEquals(
			newDLSyncEvent, dlSyncEvents.get(newDLSyncEvent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DLSyncEvent> dlSyncEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(dlSyncEvents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DLSyncEvent newDLSyncEvent = addDLSyncEvent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDLSyncEvent.getPrimaryKey());

		Map<Serializable, DLSyncEvent> dlSyncEvents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, dlSyncEvents.size());
		Assert.assertEquals(
			newDLSyncEvent, dlSyncEvents.get(newDLSyncEvent.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DLSyncEvent newDLSyncEvent = addDLSyncEvent();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDLSyncEvent.getPrimaryKey()));
	}

	private void _assertOriginalValues(DLSyncEvent dlSyncEvent) {
		Assert.assertEquals(
			Long.valueOf(dlSyncEvent.getTypePK()),
			ReflectionTestUtil.<Long>invoke(
				dlSyncEvent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "typePK"));
	}

	protected DLSyncEvent addDLSyncEvent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DLSyncEvent dlSyncEvent = _persistence.create(pk);

		dlSyncEvent.setCompanyId(RandomTestUtil.nextLong());

		dlSyncEvent.setModifiedTime(RandomTestUtil.nextLong());

		dlSyncEvent.setEvent(RandomTestUtil.randomString());

		dlSyncEvent.setType(RandomTestUtil.randomString());

		dlSyncEvent.setTypePK(RandomTestUtil.nextLong());

		_dlSyncEvents.add(_persistence.update(dlSyncEvent));

		return dlSyncEvent;
	}

	private List<DLSyncEvent> _dlSyncEvents = new ArrayList<DLSyncEvent>();
	private DLSyncEventPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}