/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchListTypeException;
import com.liferay.portal.kernel.model.ListType;
import com.liferay.portal.kernel.service.persistence.ListTypePersistence;
import com.liferay.portal.kernel.service.persistence.ListTypeUtil;
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
public class ListTypePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ListTypeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ListType> iterator = _listTypes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListType listType = _persistence.create(pk);

		Assert.assertNotNull(listType);

		Assert.assertEquals(listType.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ListType newListType = addListType();

		_persistence.remove(newListType);

		ListType existingListType = _persistence.fetchByPrimaryKey(
			newListType.getPrimaryKey());

		Assert.assertNull(existingListType);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addListType();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListType newListType = _persistence.create(pk);

		newListType.setMvccVersion(RandomTestUtil.nextLong());

		newListType.setUuid(RandomTestUtil.randomString());

		newListType.setCompanyId(RandomTestUtil.nextLong());

		newListType.setUserId(RandomTestUtil.nextLong());

		newListType.setUserName(RandomTestUtil.randomString());

		newListType.setCreateDate(RandomTestUtil.nextDate());

		newListType.setModifiedDate(RandomTestUtil.nextDate());

		newListType.setName(RandomTestUtil.randomString());

		newListType.setType(RandomTestUtil.randomString());

		_listTypes.add(_persistence.update(newListType));

		ListType existingListType = _persistence.findByPrimaryKey(
			newListType.getPrimaryKey());

		Assert.assertEquals(
			existingListType.getMvccVersion(), newListType.getMvccVersion());
		Assert.assertEquals(existingListType.getUuid(), newListType.getUuid());
		Assert.assertEquals(
			existingListType.getListTypeId(), newListType.getListTypeId());
		Assert.assertEquals(
			existingListType.getCompanyId(), newListType.getCompanyId());
		Assert.assertEquals(
			existingListType.getUserId(), newListType.getUserId());
		Assert.assertEquals(
			existingListType.getUserName(), newListType.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingListType.getCreateDate()),
			Time.getShortTimestamp(newListType.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingListType.getModifiedDate()),
			Time.getShortTimestamp(newListType.getModifiedDate()));
		Assert.assertEquals(existingListType.getName(), newListType.getName());
		Assert.assertEquals(existingListType.getType(), newListType.getType());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(RandomTestUtil.nextLong(), "");

		_persistence.countByC_T(0L, "null");

		_persistence.countByC_T(0L, (String)null);
	}

	@Test
	public void testCountByC_N_T() throws Exception {
		_persistence.countByC_N_T(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_N_T(0L, "null", "null");

		_persistence.countByC_N_T(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ListType newListType = addListType();

		ListType existingListType = _persistence.findByPrimaryKey(
			newListType.getPrimaryKey());

		Assert.assertEquals(existingListType, newListType);
	}

	@Test(expected = NoSuchListTypeException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ListType> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ListType", "mvccVersion", true, "uuid", true, "listTypeId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "name", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ListType newListType = addListType();

		ListType existingListType = _persistence.fetchByPrimaryKey(
			newListType.getPrimaryKey());

		Assert.assertEquals(existingListType, newListType);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListType missingListType = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingListType);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ListType newListType1 = addListType();
		ListType newListType2 = addListType();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newListType1.getPrimaryKey());
		primaryKeys.add(newListType2.getPrimaryKey());

		Map<Serializable, ListType> listTypes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, listTypes.size());
		Assert.assertEquals(
			newListType1, listTypes.get(newListType1.getPrimaryKey()));
		Assert.assertEquals(
			newListType2, listTypes.get(newListType2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ListType> listTypes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(listTypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ListType newListType = addListType();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newListType.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ListType> listTypes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, listTypes.size());
		Assert.assertEquals(
			newListType, listTypes.get(newListType.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ListType> listTypes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(listTypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ListType newListType = addListType();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newListType.getPrimaryKey());

		Map<Serializable, ListType> listTypes = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, listTypes.size());
		Assert.assertEquals(
			newListType, listTypes.get(newListType.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ListType newListType = addListType();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newListType.getPrimaryKey()));
	}

	private void _assertOriginalValues(ListType listType) {
		Assert.assertEquals(
			Long.valueOf(listType.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				listType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			listType.getName(),
			ReflectionTestUtil.invoke(
				listType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			listType.getType(),
			ReflectionTestUtil.invoke(
				listType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
	}

	protected ListType addListType() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ListType listType = _persistence.create(pk);

		listType.setMvccVersion(RandomTestUtil.nextLong());

		listType.setUuid(RandomTestUtil.randomString());

		listType.setCompanyId(RandomTestUtil.nextLong());

		listType.setUserId(RandomTestUtil.nextLong());

		listType.setUserName(RandomTestUtil.randomString());

		listType.setCreateDate(RandomTestUtil.nextDate());

		listType.setModifiedDate(RandomTestUtil.nextDate());

		listType.setName(RandomTestUtil.randomString());

		listType.setType(RandomTestUtil.randomString());

		_listTypes.add(_persistence.update(listType));

		return listType;
	}

	private List<ListType> _listTypes = new ArrayList<ListType>();
	private ListTypePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}