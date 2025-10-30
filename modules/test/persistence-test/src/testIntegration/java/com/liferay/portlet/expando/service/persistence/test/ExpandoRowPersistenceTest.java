/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.exception.NoSuchRowException;
import com.liferay.expando.kernel.model.ExpandoRow;
import com.liferay.expando.kernel.service.persistence.ExpandoRowPersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoRowUtil;
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
public class ExpandoRowPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ExpandoRowUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ExpandoRow> iterator = _expandoRows.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoRow expandoRow = _persistence.create(pk);

		Assert.assertNotNull(expandoRow);

		Assert.assertEquals(expandoRow.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ExpandoRow newExpandoRow = addExpandoRow();

		_persistence.remove(newExpandoRow);

		ExpandoRow existingExpandoRow = _persistence.fetchByPrimaryKey(
			newExpandoRow.getPrimaryKey());

		Assert.assertNull(existingExpandoRow);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addExpandoRow();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoRow newExpandoRow = _persistence.create(pk);

		newExpandoRow.setMvccVersion(RandomTestUtil.nextLong());

		newExpandoRow.setCtCollectionId(RandomTestUtil.nextLong());

		newExpandoRow.setCompanyId(RandomTestUtil.nextLong());

		newExpandoRow.setModifiedDate(RandomTestUtil.nextDate());

		newExpandoRow.setTableId(RandomTestUtil.nextLong());

		newExpandoRow.setClassPK(RandomTestUtil.nextLong());

		_expandoRows.add(_persistence.update(newExpandoRow));

		ExpandoRow existingExpandoRow = _persistence.findByPrimaryKey(
			newExpandoRow.getPrimaryKey());

		Assert.assertEquals(
			existingExpandoRow.getMvccVersion(),
			newExpandoRow.getMvccVersion());
		Assert.assertEquals(
			existingExpandoRow.getCtCollectionId(),
			newExpandoRow.getCtCollectionId());
		Assert.assertEquals(
			existingExpandoRow.getRowId(), newExpandoRow.getRowId());
		Assert.assertEquals(
			existingExpandoRow.getCompanyId(), newExpandoRow.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingExpandoRow.getModifiedDate()),
			Time.getShortTimestamp(newExpandoRow.getModifiedDate()));
		Assert.assertEquals(
			existingExpandoRow.getTableId(), newExpandoRow.getTableId());
		Assert.assertEquals(
			existingExpandoRow.getClassPK(), newExpandoRow.getClassPK());
	}

	@Test
	public void testCountByTableId() throws Exception {
		_persistence.countByTableId(RandomTestUtil.nextLong());

		_persistence.countByTableId(0L);
	}

	@Test
	public void testCountByClassPK() throws Exception {
		_persistence.countByClassPK(RandomTestUtil.nextLong());

		_persistence.countByClassPK(0L);
	}

	@Test
	public void testCountByT_C() throws Exception {
		_persistence.countByT_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByT_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ExpandoRow newExpandoRow = addExpandoRow();

		ExpandoRow existingExpandoRow = _persistence.findByPrimaryKey(
			newExpandoRow.getPrimaryKey());

		Assert.assertEquals(existingExpandoRow, newExpandoRow);
	}

	@Test(expected = NoSuchRowException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ExpandoRow> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ExpandoRow", "mvccVersion", true, "ctCollectionId", true, "rowId",
			true, "companyId", true, "modifiedDate", true, "tableId", true,
			"classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ExpandoRow newExpandoRow = addExpandoRow();

		ExpandoRow existingExpandoRow = _persistence.fetchByPrimaryKey(
			newExpandoRow.getPrimaryKey());

		Assert.assertEquals(existingExpandoRow, newExpandoRow);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoRow missingExpandoRow = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingExpandoRow);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ExpandoRow newExpandoRow1 = addExpandoRow();
		ExpandoRow newExpandoRow2 = addExpandoRow();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoRow1.getPrimaryKey());
		primaryKeys.add(newExpandoRow2.getPrimaryKey());

		Map<Serializable, ExpandoRow> expandoRows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, expandoRows.size());
		Assert.assertEquals(
			newExpandoRow1, expandoRows.get(newExpandoRow1.getPrimaryKey()));
		Assert.assertEquals(
			newExpandoRow2, expandoRows.get(newExpandoRow2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ExpandoRow> expandoRows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(expandoRows.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ExpandoRow newExpandoRow = addExpandoRow();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoRow.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ExpandoRow> expandoRows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, expandoRows.size());
		Assert.assertEquals(
			newExpandoRow, expandoRows.get(newExpandoRow.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ExpandoRow> expandoRows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(expandoRows.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ExpandoRow newExpandoRow = addExpandoRow();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoRow.getPrimaryKey());

		Map<Serializable, ExpandoRow> expandoRows =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, expandoRows.size());
		Assert.assertEquals(
			newExpandoRow, expandoRows.get(newExpandoRow.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ExpandoRow newExpandoRow = addExpandoRow();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newExpandoRow.getPrimaryKey()));
	}

	private void _assertOriginalValues(ExpandoRow expandoRow) {
		Assert.assertEquals(
			Long.valueOf(expandoRow.getTableId()),
			ReflectionTestUtil.<Long>invoke(
				expandoRow, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "tableId"));
		Assert.assertEquals(
			Long.valueOf(expandoRow.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				expandoRow, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected ExpandoRow addExpandoRow() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoRow expandoRow = _persistence.create(pk);

		expandoRow.setMvccVersion(RandomTestUtil.nextLong());

		expandoRow.setCtCollectionId(RandomTestUtil.nextLong());

		expandoRow.setCompanyId(RandomTestUtil.nextLong());

		expandoRow.setModifiedDate(RandomTestUtil.nextDate());

		expandoRow.setTableId(RandomTestUtil.nextLong());

		expandoRow.setClassPK(RandomTestUtil.nextLong());

		_expandoRows.add(_persistence.update(expandoRow));

		return expandoRow;
	}

	private List<ExpandoRow> _expandoRows = new ArrayList<ExpandoRow>();
	private ExpandoRowPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}