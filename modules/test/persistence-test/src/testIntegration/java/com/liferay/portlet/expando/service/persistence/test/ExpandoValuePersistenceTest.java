/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.expando.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.expando.kernel.exception.NoSuchValueException;
import com.liferay.expando.kernel.model.ExpandoValue;
import com.liferay.expando.kernel.service.persistence.ExpandoValuePersistence;
import com.liferay.expando.kernel.service.persistence.ExpandoValueUtil;
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
public class ExpandoValuePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ExpandoValueUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ExpandoValue> iterator = _expandoValues.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoValue expandoValue = _persistence.create(pk);

		Assert.assertNotNull(expandoValue);

		Assert.assertEquals(expandoValue.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ExpandoValue newExpandoValue = addExpandoValue();

		_persistence.remove(newExpandoValue);

		ExpandoValue existingExpandoValue = _persistence.fetchByPrimaryKey(
			newExpandoValue.getPrimaryKey());

		Assert.assertNull(existingExpandoValue);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addExpandoValue();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoValue newExpandoValue = _persistence.create(pk);

		newExpandoValue.setMvccVersion(RandomTestUtil.nextLong());

		newExpandoValue.setCtCollectionId(RandomTestUtil.nextLong());

		newExpandoValue.setCompanyId(RandomTestUtil.nextLong());

		newExpandoValue.setTableId(RandomTestUtil.nextLong());

		newExpandoValue.setColumnId(RandomTestUtil.nextLong());

		newExpandoValue.setRowId(RandomTestUtil.nextLong());

		newExpandoValue.setClassNameId(RandomTestUtil.nextLong());

		newExpandoValue.setClassPK(RandomTestUtil.nextLong());

		newExpandoValue.setData(RandomTestUtil.randomString());

		_expandoValues.add(_persistence.update(newExpandoValue));

		ExpandoValue existingExpandoValue = _persistence.findByPrimaryKey(
			newExpandoValue.getPrimaryKey());

		Assert.assertEquals(
			existingExpandoValue.getMvccVersion(),
			newExpandoValue.getMvccVersion());
		Assert.assertEquals(
			existingExpandoValue.getCtCollectionId(),
			newExpandoValue.getCtCollectionId());
		Assert.assertEquals(
			existingExpandoValue.getValueId(), newExpandoValue.getValueId());
		Assert.assertEquals(
			existingExpandoValue.getCompanyId(),
			newExpandoValue.getCompanyId());
		Assert.assertEquals(
			existingExpandoValue.getTableId(), newExpandoValue.getTableId());
		Assert.assertEquals(
			existingExpandoValue.getColumnId(), newExpandoValue.getColumnId());
		Assert.assertEquals(
			existingExpandoValue.getRowId(), newExpandoValue.getRowId());
		Assert.assertEquals(
			existingExpandoValue.getClassNameId(),
			newExpandoValue.getClassNameId());
		Assert.assertEquals(
			existingExpandoValue.getClassPK(), newExpandoValue.getClassPK());
		Assert.assertEquals(
			existingExpandoValue.getData(), newExpandoValue.getData());
	}

	@Test
	public void testCountByTableId() throws Exception {
		_persistence.countByTableId(RandomTestUtil.nextLong());

		_persistence.countByTableId(0L);
	}

	@Test
	public void testCountByColumnId() throws Exception {
		_persistence.countByColumnId(RandomTestUtil.nextLong());

		_persistence.countByColumnId(0L);
	}

	@Test
	public void testCountByRowId() throws Exception {
		_persistence.countByRowId(RandomTestUtil.nextLong());

		_persistence.countByRowId(0L);
	}

	@Test
	public void testCountByT_C() throws Exception {
		_persistence.countByT_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByT_C(0L, 0L);
	}

	@Test
	public void testCountByT_R() throws Exception {
		_persistence.countByT_R(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByT_R(0L, 0L);
	}

	@Test
	public void testCountByT_CPK() throws Exception {
		_persistence.countByT_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByT_CPK(0L, 0L);
	}

	@Test
	public void testCountByC_R() throws Exception {
		_persistence.countByC_R(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_R(0L, 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByT_C_C() throws Exception {
		_persistence.countByT_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByT_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByT_C_D() throws Exception {
		_persistence.countByT_C_D(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByT_C_D(0L, 0L, "null");

		_persistence.countByT_C_D(0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ExpandoValue newExpandoValue = addExpandoValue();

		ExpandoValue existingExpandoValue = _persistence.findByPrimaryKey(
			newExpandoValue.getPrimaryKey());

		Assert.assertEquals(existingExpandoValue, newExpandoValue);
	}

	@Test(expected = NoSuchValueException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ExpandoValue> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ExpandoValue", "mvccVersion", true, "ctCollectionId", true,
			"valueId", true, "companyId", true, "tableId", true, "columnId",
			true, "rowId", true, "classNameId", true, "classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ExpandoValue newExpandoValue = addExpandoValue();

		ExpandoValue existingExpandoValue = _persistence.fetchByPrimaryKey(
			newExpandoValue.getPrimaryKey());

		Assert.assertEquals(existingExpandoValue, newExpandoValue);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoValue missingExpandoValue = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingExpandoValue);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ExpandoValue newExpandoValue1 = addExpandoValue();
		ExpandoValue newExpandoValue2 = addExpandoValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoValue1.getPrimaryKey());
		primaryKeys.add(newExpandoValue2.getPrimaryKey());

		Map<Serializable, ExpandoValue> expandoValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, expandoValues.size());
		Assert.assertEquals(
			newExpandoValue1,
			expandoValues.get(newExpandoValue1.getPrimaryKey()));
		Assert.assertEquals(
			newExpandoValue2,
			expandoValues.get(newExpandoValue2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ExpandoValue> expandoValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(expandoValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ExpandoValue newExpandoValue = addExpandoValue();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoValue.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ExpandoValue> expandoValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, expandoValues.size());
		Assert.assertEquals(
			newExpandoValue,
			expandoValues.get(newExpandoValue.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ExpandoValue> expandoValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(expandoValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ExpandoValue newExpandoValue = addExpandoValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newExpandoValue.getPrimaryKey());

		Map<Serializable, ExpandoValue> expandoValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, expandoValues.size());
		Assert.assertEquals(
			newExpandoValue,
			expandoValues.get(newExpandoValue.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ExpandoValue newExpandoValue = addExpandoValue();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newExpandoValue.getPrimaryKey()));
	}

	private void _assertOriginalValues(ExpandoValue expandoValue) {
		Assert.assertEquals(
			Long.valueOf(expandoValue.getColumnId()),
			ReflectionTestUtil.<Long>invoke(
				expandoValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "columnId"));
		Assert.assertEquals(
			Long.valueOf(expandoValue.getRowId()),
			ReflectionTestUtil.<Long>invoke(
				expandoValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "rowId_"));

		Assert.assertEquals(
			Long.valueOf(expandoValue.getTableId()),
			ReflectionTestUtil.<Long>invoke(
				expandoValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "tableId"));
		Assert.assertEquals(
			Long.valueOf(expandoValue.getColumnId()),
			ReflectionTestUtil.<Long>invoke(
				expandoValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "columnId"));
		Assert.assertEquals(
			Long.valueOf(expandoValue.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				expandoValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected ExpandoValue addExpandoValue() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ExpandoValue expandoValue = _persistence.create(pk);

		expandoValue.setMvccVersion(RandomTestUtil.nextLong());

		expandoValue.setCtCollectionId(RandomTestUtil.nextLong());

		expandoValue.setCompanyId(RandomTestUtil.nextLong());

		expandoValue.setTableId(RandomTestUtil.nextLong());

		expandoValue.setColumnId(RandomTestUtil.nextLong());

		expandoValue.setRowId(RandomTestUtil.nextLong());

		expandoValue.setClassNameId(RandomTestUtil.nextLong());

		expandoValue.setClassPK(RandomTestUtil.nextLong());

		expandoValue.setData(RandomTestUtil.randomString());

		_expandoValues.add(_persistence.update(expandoValue));

		return expandoValue;
	}

	private List<ExpandoValue> _expandoValues = new ArrayList<ExpandoValue>();
	private ExpandoValuePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}