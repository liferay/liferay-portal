/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchBrowserTrackerException;
import com.liferay.portal.kernel.model.BrowserTracker;
import com.liferay.portal.kernel.service.persistence.BrowserTrackerPersistence;
import com.liferay.portal.kernel.service.persistence.BrowserTrackerUtil;
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
public class BrowserTrackerPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = BrowserTrackerUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<BrowserTracker> iterator = _browserTrackers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BrowserTracker browserTracker = _persistence.create(pk);

		Assert.assertNotNull(browserTracker);

		Assert.assertEquals(browserTracker.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		BrowserTracker newBrowserTracker = addBrowserTracker();

		_persistence.remove(newBrowserTracker);

		BrowserTracker existingBrowserTracker = _persistence.fetchByPrimaryKey(
			newBrowserTracker.getPrimaryKey());

		Assert.assertNull(existingBrowserTracker);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addBrowserTracker();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BrowserTracker newBrowserTracker = _persistence.create(pk);

		newBrowserTracker.setMvccVersion(RandomTestUtil.nextLong());

		newBrowserTracker.setCompanyId(RandomTestUtil.nextLong());

		newBrowserTracker.setUserId(RandomTestUtil.nextLong());

		newBrowserTracker.setBrowserKey(RandomTestUtil.nextLong());

		_browserTrackers.add(_persistence.update(newBrowserTracker));

		BrowserTracker existingBrowserTracker = _persistence.findByPrimaryKey(
			newBrowserTracker.getPrimaryKey());

		Assert.assertEquals(
			existingBrowserTracker.getMvccVersion(),
			newBrowserTracker.getMvccVersion());
		Assert.assertEquals(
			existingBrowserTracker.getBrowserTrackerId(),
			newBrowserTracker.getBrowserTrackerId());
		Assert.assertEquals(
			existingBrowserTracker.getCompanyId(),
			newBrowserTracker.getCompanyId());
		Assert.assertEquals(
			existingBrowserTracker.getUserId(), newBrowserTracker.getUserId());
		Assert.assertEquals(
			existingBrowserTracker.getBrowserKey(),
			newBrowserTracker.getBrowserKey());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		BrowserTracker newBrowserTracker = addBrowserTracker();

		BrowserTracker existingBrowserTracker = _persistence.findByPrimaryKey(
			newBrowserTracker.getPrimaryKey());

		Assert.assertEquals(existingBrowserTracker, newBrowserTracker);
	}

	@Test(expected = NoSuchBrowserTrackerException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<BrowserTracker> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"BrowserTracker", "mvccVersion", true, "browserTrackerId", true,
			"companyId", true, "userId", true, "browserKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		BrowserTracker newBrowserTracker = addBrowserTracker();

		BrowserTracker existingBrowserTracker = _persistence.fetchByPrimaryKey(
			newBrowserTracker.getPrimaryKey());

		Assert.assertEquals(existingBrowserTracker, newBrowserTracker);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BrowserTracker missingBrowserTracker = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingBrowserTracker);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		BrowserTracker newBrowserTracker1 = addBrowserTracker();
		BrowserTracker newBrowserTracker2 = addBrowserTracker();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBrowserTracker1.getPrimaryKey());
		primaryKeys.add(newBrowserTracker2.getPrimaryKey());

		Map<Serializable, BrowserTracker> browserTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, browserTrackers.size());
		Assert.assertEquals(
			newBrowserTracker1,
			browserTrackers.get(newBrowserTracker1.getPrimaryKey()));
		Assert.assertEquals(
			newBrowserTracker2,
			browserTrackers.get(newBrowserTracker2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, BrowserTracker> browserTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(browserTrackers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		BrowserTracker newBrowserTracker = addBrowserTracker();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBrowserTracker.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, BrowserTracker> browserTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, browserTrackers.size());
		Assert.assertEquals(
			newBrowserTracker,
			browserTrackers.get(newBrowserTracker.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, BrowserTracker> browserTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(browserTrackers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		BrowserTracker newBrowserTracker = addBrowserTracker();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newBrowserTracker.getPrimaryKey());

		Map<Serializable, BrowserTracker> browserTrackers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, browserTrackers.size());
		Assert.assertEquals(
			newBrowserTracker,
			browserTrackers.get(newBrowserTracker.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		BrowserTracker newBrowserTracker = addBrowserTracker();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newBrowserTracker.getPrimaryKey()));
	}

	private void _assertOriginalValues(BrowserTracker browserTracker) {
		Assert.assertEquals(
			Long.valueOf(browserTracker.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				browserTracker, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
	}

	protected BrowserTracker addBrowserTracker() throws Exception {
		long pk = RandomTestUtil.nextLong();

		BrowserTracker browserTracker = _persistence.create(pk);

		browserTracker.setMvccVersion(RandomTestUtil.nextLong());

		browserTracker.setCompanyId(RandomTestUtil.nextLong());

		browserTracker.setUserId(RandomTestUtil.nextLong());

		browserTracker.setBrowserKey(RandomTestUtil.nextLong());

		_browserTrackers.add(_persistence.update(browserTracker));

		return browserTracker;
	}

	private List<BrowserTracker> _browserTrackers =
		new ArrayList<BrowserTracker>();
	private BrowserTrackerPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}