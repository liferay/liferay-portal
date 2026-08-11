/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchClobEntryException;
import com.liferay.portal.tools.service.builder.test.model.ClobEntry;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.ClobEntryUtil;

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
public class ClobEntryPersistenceTest {

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
		_persistence = ClobEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ClobEntry> iterator = _clobEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ClobEntry clobEntry = _persistence.create(pk);

		Assert.assertNotNull(clobEntry);

		Assert.assertEquals(clobEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		_persistence.remove(newClobEntry);

		ClobEntry existingClobEntry = _persistence.fetchByPrimaryKey(
			newClobEntry.getPrimaryKey());

		Assert.assertNull(existingClobEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addClobEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		newClobEntry.setContent(RandomTestUtil.randomString());

		newClobEntry = _persistence.update(newClobEntry);

		_clobEntries.add(newClobEntry);

		ClobEntry existingClobEntry = _persistence.findByPrimaryKey(
			newClobEntry.getPrimaryKey());

		Assert.assertEquals(
			existingClobEntry.getClobEntryId(), newClobEntry.getClobEntryId());
		Assert.assertEquals(
			existingClobEntry.getContent(), newClobEntry.getContent());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		ClobEntry existingClobEntry = _persistence.findByPrimaryKey(
			newClobEntry.getPrimaryKey());

		Assert.assertEquals(existingClobEntry, newClobEntry);
	}

	@Test(expected = NoSuchClobEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ClobEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ClobEntry", "clobEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		ClobEntry existingClobEntry = _persistence.fetchByPrimaryKey(
			newClobEntry.getPrimaryKey());

		Assert.assertEquals(existingClobEntry, newClobEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ClobEntry missingClobEntry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingClobEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ClobEntry newClobEntry1 = addClobEntry();
		ClobEntry newClobEntry2 = addClobEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClobEntry1.getPrimaryKey());
		primaryKeys.add(newClobEntry2.getPrimaryKey());

		Map<Serializable, ClobEntry> clobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, clobEntries.size());
		Assert.assertEquals(
			newClobEntry1, clobEntries.get(newClobEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newClobEntry2, clobEntries.get(newClobEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ClobEntry> clobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(clobEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ClobEntry newClobEntry = addClobEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClobEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ClobEntry> clobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, clobEntries.size());
		Assert.assertEquals(
			newClobEntry, clobEntries.get(newClobEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ClobEntry> clobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(clobEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newClobEntry.getPrimaryKey());

		Map<Serializable, ClobEntry> clobEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, clobEntries.size());
		Assert.assertEquals(
			newClobEntry, clobEntries.get(newClobEntry.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"clobEntryId", newClobEntry.getClobEntryId()));

		List<ClobEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ClobEntry existingClobEntry = result.get(0);

		Assert.assertEquals(existingClobEntry, newClobEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"clobEntryId", RandomTestUtil.nextLong()));

		List<ClobEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ClobEntry newClobEntry = addClobEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("clobEntryId"));

		Object newClobEntryId = newClobEntry.getClobEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"clobEntryId", new Object[] {newClobEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingClobEntryId = result.get(0);

		Assert.assertEquals(existingClobEntryId, newClobEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ClobEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("clobEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"clobEntryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ClobEntry addClobEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ClobEntry clobEntry = _persistence.create(pk);

		clobEntry.setContent(RandomTestUtil.randomString());

		_clobEntries.add(_persistence.update(clobEntry));

		return clobEntry;
	}

	private List<ClobEntry> _clobEntries = new ArrayList<ClobEntry>();
	private ClobEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2062299382