/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchFilterPrimaryEntryException;
import com.liferay.portal.tools.service.builder.test.model.FilterPrimaryEntry;
import com.liferay.portal.tools.service.builder.test.service.FilterPrimaryEntryLocalServiceUtil;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterPrimaryEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.FilterPrimaryEntryUtil;

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
public class FilterPrimaryEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = FilterPrimaryEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<FilterPrimaryEntry> iterator =
			_filterPrimaryEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterPrimaryEntry filterPrimaryEntry = _persistence.create(pk);

		Assert.assertNotNull(filterPrimaryEntry);

		Assert.assertEquals(filterPrimaryEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		_persistence.remove(newFilterPrimaryEntry);

		FilterPrimaryEntry existingFilterPrimaryEntry =
			_persistence.fetchByPrimaryKey(
				newFilterPrimaryEntry.getPrimaryKey());

		Assert.assertNull(existingFilterPrimaryEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addFilterPrimaryEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		newFilterPrimaryEntry.setGroupId(RandomTestUtil.nextLong());

		newFilterPrimaryEntry.setCompanyId(RandomTestUtil.nextLong());

		newFilterPrimaryEntry.setUserId(RandomTestUtil.nextLong());

		newFilterPrimaryEntry.setResourcePrimKey(RandomTestUtil.nextLong());

		newFilterPrimaryEntry = _persistence.update(newFilterPrimaryEntry);

		_filterPrimaryEntries.add(newFilterPrimaryEntry);

		FilterPrimaryEntry existingFilterPrimaryEntry =
			_persistence.findByPrimaryKey(
				newFilterPrimaryEntry.getPrimaryKey());

		Assert.assertEquals(
			existingFilterPrimaryEntry.getFilterPrimaryEntryId(),
			newFilterPrimaryEntry.getFilterPrimaryEntryId());
		Assert.assertEquals(
			existingFilterPrimaryEntry.getGroupId(),
			newFilterPrimaryEntry.getGroupId());
		Assert.assertEquals(
			existingFilterPrimaryEntry.getCompanyId(),
			newFilterPrimaryEntry.getCompanyId());
		Assert.assertEquals(
			existingFilterPrimaryEntry.getUserId(),
			newFilterPrimaryEntry.getUserId());
		Assert.assertEquals(
			existingFilterPrimaryEntry.getResourcePrimKey(),
			newFilterPrimaryEntry.getResourcePrimKey());
	}

	@Test
	public void testCountByResourcePrimKey() throws Exception {
		_persistence.countByResourcePrimKey(RandomTestUtil.nextLong());

		_persistence.countByResourcePrimKey(0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		FilterPrimaryEntry existingFilterPrimaryEntry =
			_persistence.findByPrimaryKey(
				newFilterPrimaryEntry.getPrimaryKey());

		Assert.assertEquals(existingFilterPrimaryEntry, newFilterPrimaryEntry);
	}

	@Test(expected = NoSuchFilterPrimaryEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<FilterPrimaryEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"FilterPrimaryEntry", "filterPrimaryEntryId", true, "groupId", true,
			"companyId", true, "userId", true, "resourcePrimKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		FilterPrimaryEntry existingFilterPrimaryEntry =
			_persistence.fetchByPrimaryKey(
				newFilterPrimaryEntry.getPrimaryKey());

		Assert.assertEquals(existingFilterPrimaryEntry, newFilterPrimaryEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterPrimaryEntry missingFilterPrimaryEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingFilterPrimaryEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		FilterPrimaryEntry newFilterPrimaryEntry1 = addFilterPrimaryEntry();
		FilterPrimaryEntry newFilterPrimaryEntry2 = addFilterPrimaryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFilterPrimaryEntry1.getPrimaryKey());
		primaryKeys.add(newFilterPrimaryEntry2.getPrimaryKey());

		Map<Serializable, FilterPrimaryEntry> filterPrimaryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, filterPrimaryEntries.size());
		Assert.assertEquals(
			newFilterPrimaryEntry1,
			filterPrimaryEntries.get(newFilterPrimaryEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newFilterPrimaryEntry2,
			filterPrimaryEntries.get(newFilterPrimaryEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, FilterPrimaryEntry> filterPrimaryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(filterPrimaryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFilterPrimaryEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, FilterPrimaryEntry> filterPrimaryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, filterPrimaryEntries.size());
		Assert.assertEquals(
			newFilterPrimaryEntry,
			filterPrimaryEntries.get(newFilterPrimaryEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, FilterPrimaryEntry> filterPrimaryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(filterPrimaryEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newFilterPrimaryEntry.getPrimaryKey());

		Map<Serializable, FilterPrimaryEntry> filterPrimaryEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, filterPrimaryEntries.size());
		Assert.assertEquals(
			newFilterPrimaryEntry,
			filterPrimaryEntries.get(newFilterPrimaryEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			FilterPrimaryEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<FilterPrimaryEntry>() {

				@Override
				public void performAction(
					FilterPrimaryEntry filterPrimaryEntry) {

					Assert.assertNotNull(filterPrimaryEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterPrimaryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"filterPrimaryEntryId",
				newFilterPrimaryEntry.getFilterPrimaryEntryId()));

		List<FilterPrimaryEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		FilterPrimaryEntry existingFilterPrimaryEntry = result.get(0);

		Assert.assertEquals(existingFilterPrimaryEntry, newFilterPrimaryEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterPrimaryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"filterPrimaryEntryId", RandomTestUtil.nextLong()));

		List<FilterPrimaryEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		FilterPrimaryEntry newFilterPrimaryEntry = addFilterPrimaryEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterPrimaryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("filterPrimaryEntryId"));

		Object newFilterPrimaryEntryId =
			newFilterPrimaryEntry.getFilterPrimaryEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"filterPrimaryEntryId",
				new Object[] {newFilterPrimaryEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingFilterPrimaryEntryId = result.get(0);

		Assert.assertEquals(
			existingFilterPrimaryEntryId, newFilterPrimaryEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			FilterPrimaryEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("filterPrimaryEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"filterPrimaryEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected FilterPrimaryEntry addFilterPrimaryEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		FilterPrimaryEntry filterPrimaryEntry = _persistence.create(pk);

		filterPrimaryEntry.setGroupId(RandomTestUtil.nextLong());

		filterPrimaryEntry.setCompanyId(RandomTestUtil.nextLong());

		filterPrimaryEntry.setUserId(RandomTestUtil.nextLong());

		filterPrimaryEntry.setResourcePrimKey(RandomTestUtil.nextLong());

		_filterPrimaryEntries.add(_persistence.update(filterPrimaryEntry));

		return filterPrimaryEntry;
	}

	private List<FilterPrimaryEntry> _filterPrimaryEntries =
		new ArrayList<FilterPrimaryEntry>();
	private FilterPrimaryEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1935280507