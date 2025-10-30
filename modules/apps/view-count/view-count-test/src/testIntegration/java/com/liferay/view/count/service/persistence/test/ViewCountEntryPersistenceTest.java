/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.view.count.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.security.auth.CompanyThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.view.count.exception.NoSuchEntryException;
import com.liferay.view.count.model.ViewCountEntry;
import com.liferay.view.count.service.persistence.ViewCountEntryPK;
import com.liferay.view.count.service.persistence.ViewCountEntryPersistence;
import com.liferay.view.count.service.persistence.ViewCountEntryUtil;

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
public class ViewCountEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.view.count.service"));

	@Before
	public void setUp() {
		_persistence = ViewCountEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ViewCountEntry> iterator = _viewCountEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		ViewCountEntryPK pk = new ViewCountEntryPK(
			CompanyThreadLocal.getCompanyId(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		ViewCountEntry viewCountEntry = _persistence.create(pk);

		Assert.assertNotNull(viewCountEntry);

		Assert.assertEquals(viewCountEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ViewCountEntry newViewCountEntry = addViewCountEntry();

		_persistence.remove(newViewCountEntry);

		ViewCountEntry existingViewCountEntry = _persistence.fetchByPrimaryKey(
			newViewCountEntry.getPrimaryKey());

		Assert.assertNull(existingViewCountEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addViewCountEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ViewCountEntryPK pk = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		ViewCountEntry newViewCountEntry = _persistence.create(pk);

		newViewCountEntry.setViewCount(RandomTestUtil.nextLong());

		_viewCountEntries.add(_persistence.update(newViewCountEntry));

		ViewCountEntry existingViewCountEntry = _persistence.findByPrimaryKey(
			newViewCountEntry.getPrimaryKey());

		Assert.assertEquals(
			existingViewCountEntry.getCompanyId(),
			newViewCountEntry.getCompanyId());
		Assert.assertEquals(
			existingViewCountEntry.getClassNameId(),
			newViewCountEntry.getClassNameId());
		Assert.assertEquals(
			existingViewCountEntry.getClassPK(),
			newViewCountEntry.getClassPK());
		Assert.assertEquals(
			existingViewCountEntry.getViewCount(),
			newViewCountEntry.getViewCount());
	}

	@Test
	public void testCountByC_CN() throws Exception {
		_persistence.countByC_CN(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_CN(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ViewCountEntry newViewCountEntry = addViewCountEntry();

		ViewCountEntry existingViewCountEntry = _persistence.findByPrimaryKey(
			newViewCountEntry.getPrimaryKey());

		Assert.assertEquals(existingViewCountEntry, newViewCountEntry);
	}

	@Test(expected = NoSuchEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		ViewCountEntryPK pk = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ViewCountEntry newViewCountEntry = addViewCountEntry();

		ViewCountEntry existingViewCountEntry = _persistence.fetchByPrimaryKey(
			newViewCountEntry.getPrimaryKey());

		Assert.assertEquals(existingViewCountEntry, newViewCountEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		ViewCountEntryPK pk = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		ViewCountEntry missingViewCountEntry = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingViewCountEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ViewCountEntry newViewCountEntry1 = addViewCountEntry();
		ViewCountEntry newViewCountEntry2 = addViewCountEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newViewCountEntry1.getPrimaryKey());
		primaryKeys.add(newViewCountEntry2.getPrimaryKey());

		Map<Serializable, ViewCountEntry> viewCountEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, viewCountEntries.size());
		Assert.assertEquals(
			newViewCountEntry1,
			viewCountEntries.get(newViewCountEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newViewCountEntry2,
			viewCountEntries.get(newViewCountEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		ViewCountEntryPK pk1 = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		ViewCountEntryPK pk2 = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ViewCountEntry> viewCountEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(viewCountEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ViewCountEntry newViewCountEntry = addViewCountEntry();

		ViewCountEntryPK pk = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newViewCountEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ViewCountEntry> viewCountEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, viewCountEntries.size());
		Assert.assertEquals(
			newViewCountEntry,
			viewCountEntries.get(newViewCountEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ViewCountEntry> viewCountEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(viewCountEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ViewCountEntry newViewCountEntry = addViewCountEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newViewCountEntry.getPrimaryKey());

		Map<Serializable, ViewCountEntry> viewCountEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, viewCountEntries.size());
		Assert.assertEquals(
			newViewCountEntry,
			viewCountEntries.get(newViewCountEntry.getPrimaryKey()));
	}

	protected ViewCountEntry addViewCountEntry() throws Exception {
		ViewCountEntryPK pk = new ViewCountEntryPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		ViewCountEntry viewCountEntry = _persistence.create(pk);

		viewCountEntry.setViewCount(RandomTestUtil.nextLong());

		_viewCountEntries.add(_persistence.update(viewCountEntry));

		return viewCountEntry;
	}

	private List<ViewCountEntry> _viewCountEntries =
		new ArrayList<ViewCountEntry>();
	private ViewCountEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}