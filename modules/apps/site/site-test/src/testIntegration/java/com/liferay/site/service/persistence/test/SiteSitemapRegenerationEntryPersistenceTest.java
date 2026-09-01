/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.site.exception.NoSuchSitemapRegenerationEntryException;
import com.liferay.site.model.SiteSitemapRegenerationEntry;
import com.liferay.site.service.SiteSitemapRegenerationEntryLocalServiceUtil;
import com.liferay.site.service.persistence.SiteSitemapRegenerationEntryPersistence;
import com.liferay.site.service.persistence.SiteSitemapRegenerationEntryUtil;

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
public class SiteSitemapRegenerationEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.site.service"));

	@Before
	public void setUp() {
		_persistence = SiteSitemapRegenerationEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SiteSitemapRegenerationEntry> iterator =
			_siteSitemapRegenerationEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry =
			_persistence.create(pk);

		Assert.assertNotNull(siteSitemapRegenerationEntry);

		Assert.assertEquals(siteSitemapRegenerationEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		_persistence.remove(newSiteSitemapRegenerationEntry);

		SiteSitemapRegenerationEntry existingSiteSitemapRegenerationEntry =
			_persistence.fetchByPrimaryKey(
				newSiteSitemapRegenerationEntry.getPrimaryKey());

		Assert.assertNull(existingSiteSitemapRegenerationEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSiteSitemapRegenerationEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		newSiteSitemapRegenerationEntry.setGroupId(RandomTestUtil.nextLong());

		newSiteSitemapRegenerationEntry.setCompanyId(RandomTestUtil.nextLong());

		newSiteSitemapRegenerationEntry.setAssetTypeKey(
			RandomTestUtil.randomString());

		newSiteSitemapRegenerationEntry = _persistence.update(
			newSiteSitemapRegenerationEntry);

		_siteSitemapRegenerationEntries.add(newSiteSitemapRegenerationEntry);

		SiteSitemapRegenerationEntry existingSiteSitemapRegenerationEntry =
			_persistence.findByPrimaryKey(
				newSiteSitemapRegenerationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry.getMvccVersion(),
			newSiteSitemapRegenerationEntry.getMvccVersion());
		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry.
				getSiteSitemapRegenerationEntryId(),
			newSiteSitemapRegenerationEntry.
				getSiteSitemapRegenerationEntryId());
		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry.getGroupId(),
			newSiteSitemapRegenerationEntry.getGroupId());
		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry.getCompanyId(),
			newSiteSitemapRegenerationEntry.getCompanyId());
		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry.getAssetTypeKey(),
			newSiteSitemapRegenerationEntry.getAssetTypeKey());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_C_A() throws Exception {
		_persistence.countByG_C_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_A(0L, 0L, "null");

		_persistence.countByG_C_A(0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		SiteSitemapRegenerationEntry existingSiteSitemapRegenerationEntry =
			_persistence.findByPrimaryKey(
				newSiteSitemapRegenerationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry,
			newSiteSitemapRegenerationEntry);
	}

	@Test(expected = NoSuchSitemapRegenerationEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SiteSitemapRegenerationEntry>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"SiteSitemapRegenerationEntry", "mvccVersion", true,
			"siteSitemapRegenerationEntryId", true, "groupId", true,
			"companyId", true, "assetTypeKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		SiteSitemapRegenerationEntry existingSiteSitemapRegenerationEntry =
			_persistence.fetchByPrimaryKey(
				newSiteSitemapRegenerationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry,
			newSiteSitemapRegenerationEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteSitemapRegenerationEntry missingSiteSitemapRegenerationEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSiteSitemapRegenerationEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry1 =
			addSiteSitemapRegenerationEntry();
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry2 =
			addSiteSitemapRegenerationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteSitemapRegenerationEntry1.getPrimaryKey());
		primaryKeys.add(newSiteSitemapRegenerationEntry2.getPrimaryKey());

		Map<Serializable, SiteSitemapRegenerationEntry>
			siteSitemapRegenerationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, siteSitemapRegenerationEntries.size());
		Assert.assertEquals(
			newSiteSitemapRegenerationEntry1,
			siteSitemapRegenerationEntries.get(
				newSiteSitemapRegenerationEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newSiteSitemapRegenerationEntry2,
			siteSitemapRegenerationEntries.get(
				newSiteSitemapRegenerationEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SiteSitemapRegenerationEntry>
			siteSitemapRegenerationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteSitemapRegenerationEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SiteSitemapRegenerationEntry>
			siteSitemapRegenerationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, siteSitemapRegenerationEntries.size());
		Assert.assertEquals(
			newSiteSitemapRegenerationEntry,
			siteSitemapRegenerationEntries.get(
				newSiteSitemapRegenerationEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SiteSitemapRegenerationEntry>
			siteSitemapRegenerationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(siteSitemapRegenerationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteSitemapRegenerationEntry.getPrimaryKey());

		Map<Serializable, SiteSitemapRegenerationEntry>
			siteSitemapRegenerationEntries = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, siteSitemapRegenerationEntries.size());
		Assert.assertEquals(
			newSiteSitemapRegenerationEntry,
			siteSitemapRegenerationEntries.get(
				newSiteSitemapRegenerationEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SiteSitemapRegenerationEntryLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SiteSitemapRegenerationEntry>() {

				@Override
				public void performAction(
					SiteSitemapRegenerationEntry siteSitemapRegenerationEntry) {

					Assert.assertNotNull(siteSitemapRegenerationEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteSitemapRegenerationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteSitemapRegenerationEntryId",
				newSiteSitemapRegenerationEntry.
					getSiteSitemapRegenerationEntryId()));

		List<SiteSitemapRegenerationEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		SiteSitemapRegenerationEntry existingSiteSitemapRegenerationEntry =
			result.get(0);

		Assert.assertEquals(
			existingSiteSitemapRegenerationEntry,
			newSiteSitemapRegenerationEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteSitemapRegenerationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteSitemapRegenerationEntryId", RandomTestUtil.nextLong()));

		List<SiteSitemapRegenerationEntry> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SiteSitemapRegenerationEntry newSiteSitemapRegenerationEntry =
			addSiteSitemapRegenerationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteSitemapRegenerationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("siteSitemapRegenerationEntryId"));

		Object newSiteSitemapRegenerationEntryId =
			newSiteSitemapRegenerationEntry.getSiteSitemapRegenerationEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"siteSitemapRegenerationEntryId",
				new Object[] {newSiteSitemapRegenerationEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSiteSitemapRegenerationEntryId = result.get(0);

		Assert.assertEquals(
			existingSiteSitemapRegenerationEntryId,
			newSiteSitemapRegenerationEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteSitemapRegenerationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("siteSitemapRegenerationEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"siteSitemapRegenerationEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected SiteSitemapRegenerationEntry addSiteSitemapRegenerationEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		SiteSitemapRegenerationEntry siteSitemapRegenerationEntry =
			_persistence.create(pk);

		siteSitemapRegenerationEntry.setGroupId(RandomTestUtil.nextLong());

		siteSitemapRegenerationEntry.setCompanyId(RandomTestUtil.nextLong());

		siteSitemapRegenerationEntry.setAssetTypeKey(
			RandomTestUtil.randomString());

		_siteSitemapRegenerationEntries.add(
			_persistence.update(siteSitemapRegenerationEntry));

		return siteSitemapRegenerationEntry;
	}

	private List<SiteSitemapRegenerationEntry> _siteSitemapRegenerationEntries =
		new ArrayList<SiteSitemapRegenerationEntry>();
	private SiteSitemapRegenerationEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1907743928