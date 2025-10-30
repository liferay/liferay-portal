/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.exception.NoSuchColumnException;
import com.liferay.expando.kernel.model.ExpandoColumn;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoColumnUtil;
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
public class ExpandoColumnPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ExpandoColumnUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ExpandoColumn> iterator = _expandoColumns.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoColumn expandoColumn = _persistence.create(pk);

		Assert.assertNotNull(expandoColumn);

		Assert.assertEquals(expandoColumn.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ExpandoColumn newExpandoColumn = addExpandoColumn();

		_persistence.remove(newExpandoColumn);

		ExpandoColumn existingExpandoColumn = _persistence.fetchByPrimaryKey(
			newExpandoColumn.getPrimaryKey());

		Assert.assertNull(existingExpandoColumn);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addExpandoColumn();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoColumn newExpandoColumn = _persistence.create(pk);

		newExpandoColumn.setMvccVersion(RandomTestUtil.nextLong());

		newExpandoColumn.setCtCollectionId(RandomTestUtil.nextLong());

		newExpandoColumn.setCompanyId(RandomTestUtil.nextLong());

		newExpandoColumn.setModifiedDate(RandomTestUtil.nextDate());

		newExpandoColumn.setTableId(RandomTestUtil.nextLong());

		newExpandoColumn.setName(RandomTestUtil.randomString());

		newExpandoColumn.setType(RandomTestUtil.nextInt());

		newExpandoColumn.setDefaultData(RandomTestUtil.randomString());

		newExpandoColumn.setTypeSettings(RandomTestUtil.randomString());

		_expandoColumns.add(_persistence.update(newExpandoColumn));

		ExpandoColumn existingExpandoColumn = _persistence.findByPrimaryKey(
			newExpandoColumn.getPrimaryKey());

		Assert.assertEquals(
			existingExpandoColumn.getMvccVersion(),
			newExpandoColumn.getMvccVersion());
		Assert.assertEquals(
			existingExpandoColumn.getCtCollectionId(),
			newExpandoColumn.getCtCollectionId());
		Assert.assertEquals(
			existingExpandoColumn.getColumnId(),
			newExpandoColumn.getColumnId());
		Assert.assertEquals(
			existingExpandoColumn.getCompanyId(),
			newExpandoColumn.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingExpandoColumn.getModifiedDate()),
			Time.getShortTimestamp(newExpandoColumn.getModifiedDate()));
		Assert.assertEquals(
			existingExpandoColumn.getTableId(), newExpandoColumn.getTableId());
		Assert.assertEquals(
			existingExpandoColumn.getName(), newExpandoColumn.getName());
		Assert.assertEquals(
			existingExpandoColumn.getType(), newExpandoColumn.getType());
		Assert.assertEquals(
			existingExpandoColumn.getDefaultData(),
			newExpandoColumn.getDefaultData());
		Assert.assertEquals(
			existingExpandoColumn.getTypeSettings(),
			newExpandoColumn.getTypeSettings());
	}

	@Test
	public void testCountByTableId() throws Exception {
		_persistence.countByTableId(RandomTestUtil.nextLong());

		_persistence.countByTableId(0L);
	}

	@Test
	public void testCountByT_N() throws Exception {
		_persistence.countByT_N(RandomTestUtil.nextLong(), "");

		_persistence.countByT_N(0L, "null");

		_persistence.countByT_N(0L, (String)null);
	}

	@Test
	public void testCountByT_NArrayable() throws Exception {
		_persistence.countByT_N(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ExpandoColumn newExpandoColumn = addExpandoColumn();

		ExpandoColumn existingExpandoColumn = _persistence.findByPrimaryKey(
			newExpandoColumn.getPrimaryKey());

		Assert.assertEquals(existingExpandoColumn, newExpandoColumn);
	}

	@Test(expected = NoSuchColumnException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ExpandoColumn> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ExpandoColumn", "mvccVersion", true, "ctCollectionId", true,
			"columnId", true, "companyId", true, "modifiedDate", true,
			"tableId", true, "name", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ExpandoColumn newExpandoColumn = addExpandoColumn();

		ExpandoColumn existingExpandoColumn = _persistence.fetchByPrimaryKey(
			newExpandoColumn.getPrimaryKey());

		Assert.assertEquals(existingExpandoColumn, newExpandoColumn);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoColumn missingExpandoColumn = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingExpandoColumn);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ExpandoColumn newExpandoColumn1 = addExpandoColumn();
		ExpandoColumn newExpandoColumn2 = addExpandoColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoColumn1.getPrimaryKey());
		primaryKeys.add(newExpandoColumn2.getPrimaryKey());

		Map<Serializable, ExpandoColumn> expandoColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, expandoColumns.size());
		Assert.assertEquals(
			newExpandoColumn1,
			expandoColumns.get(newExpandoColumn1.getPrimaryKey()));
		Assert.assertEquals(
			newExpandoColumn2,
			expandoColumns.get(newExpandoColumn2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ExpandoColumn> expandoColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(expandoColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ExpandoColumn newExpandoColumn = addExpandoColumn();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoColumn.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ExpandoColumn> expandoColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, expandoColumns.size());
		Assert.assertEquals(
			newExpandoColumn,
			expandoColumns.get(newExpandoColumn.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ExpandoColumn> expandoColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(expandoColumns.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ExpandoColumn newExpandoColumn = addExpandoColumn();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoColumn.getPrimaryKey());

		Map<Serializable, ExpandoColumn> expandoColumns =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, expandoColumns.size());
		Assert.assertEquals(
			newExpandoColumn,
			expandoColumns.get(newExpandoColumn.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ExpandoColumn newExpandoColumn = addExpandoColumn();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newExpandoColumn.getPrimaryKey()));
	}

	private void _assertOriginalValues(ExpandoColumn expandoColumn) {
		Assert.assertEquals(
			Long.valueOf(expandoColumn.getTableId()),
			ReflectionTestUtil.<Long>invoke(
				expandoColumn, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "tableId"));
		Assert.assertEquals(
			expandoColumn.getName(),
			ReflectionTestUtil.invoke(
				expandoColumn, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected ExpandoColumn addExpandoColumn() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoColumn expandoColumn = _persistence.create(pk);

		expandoColumn.setMvccVersion(RandomTestUtil.nextLong());

		expandoColumn.setCtCollectionId(RandomTestUtil.nextLong());

		expandoColumn.setCompanyId(RandomTestUtil.nextLong());

		expandoColumn.setModifiedDate(RandomTestUtil.nextDate());

		expandoColumn.setTableId(RandomTestUtil.nextLong());

		expandoColumn.setName(RandomTestUtil.randomString());

		expandoColumn.setType(RandomTestUtil.nextInt());

		expandoColumn.setDefaultData(RandomTestUtil.randomString());

		expandoColumn.setTypeSettings(RandomTestUtil.randomString());

		_expandoColumns.add(_persistence.update(expandoColumn));

		return expandoColumn;
	}

	private List<ExpandoColumn> _expandoColumns =
		new ArrayList<ExpandoColumn>();
	private ExpandoColumnPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}