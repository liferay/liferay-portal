/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutPrototypeException;
import com.liferay.portal.kernel.model.LayoutPrototype;
import com.liferay.portal.kernel.service.persistence.LayoutPrototypePersistence;
import com.liferay.portal.kernel.service.persistence.LayoutPrototypeUtil;
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
public class LayoutPrototypePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = LayoutPrototypeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutPrototype> iterator = _layoutPrototypes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPrototype layoutPrototype = _persistence.create(pk);

		Assert.assertNotNull(layoutPrototype);

		Assert.assertEquals(layoutPrototype.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutPrototype newLayoutPrototype = addLayoutPrototype();

		_persistence.remove(newLayoutPrototype);

		LayoutPrototype existingLayoutPrototype =
			_persistence.fetchByPrimaryKey(newLayoutPrototype.getPrimaryKey());

		Assert.assertNull(existingLayoutPrototype);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutPrototype();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPrototype newLayoutPrototype = _persistence.create(pk);

		newLayoutPrototype.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutPrototype.setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutPrototype.setUuid(RandomTestUtil.randomString());

		newLayoutPrototype.setCompanyId(RandomTestUtil.nextLong());

		newLayoutPrototype.setUserId(RandomTestUtil.nextLong());

		newLayoutPrototype.setUserName(RandomTestUtil.randomString());

		newLayoutPrototype.setCreateDate(RandomTestUtil.nextDate());

		newLayoutPrototype.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutPrototype.setName(RandomTestUtil.randomString());

		newLayoutPrototype.setDescription(RandomTestUtil.randomString());

		newLayoutPrototype.setSettings(RandomTestUtil.randomString());

		newLayoutPrototype.setActive(RandomTestUtil.randomBoolean());

		_layoutPrototypes.add(_persistence.update(newLayoutPrototype));

		LayoutPrototype existingLayoutPrototype = _persistence.findByPrimaryKey(
			newLayoutPrototype.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPrototype.getMvccVersion(),
			newLayoutPrototype.getMvccVersion());
		Assert.assertEquals(
			existingLayoutPrototype.getCtCollectionId(),
			newLayoutPrototype.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutPrototype.getUuid(), newLayoutPrototype.getUuid());
		Assert.assertEquals(
			existingLayoutPrototype.getLayoutPrototypeId(),
			newLayoutPrototype.getLayoutPrototypeId());
		Assert.assertEquals(
			existingLayoutPrototype.getCompanyId(),
			newLayoutPrototype.getCompanyId());
		Assert.assertEquals(
			existingLayoutPrototype.getUserId(),
			newLayoutPrototype.getUserId());
		Assert.assertEquals(
			existingLayoutPrototype.getUserName(),
			newLayoutPrototype.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutPrototype.getCreateDate()),
			Time.getShortTimestamp(newLayoutPrototype.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutPrototype.getModifiedDate()),
			Time.getShortTimestamp(newLayoutPrototype.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutPrototype.getName(), newLayoutPrototype.getName());
		Assert.assertEquals(
			existingLayoutPrototype.getDescription(),
			newLayoutPrototype.getDescription());
		Assert.assertEquals(
			existingLayoutPrototype.getSettings(),
			newLayoutPrototype.getSettings());
		Assert.assertEquals(
			existingLayoutPrototype.isActive(), newLayoutPrototype.isActive());
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
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutPrototype newLayoutPrototype = addLayoutPrototype();

		LayoutPrototype existingLayoutPrototype = _persistence.findByPrimaryKey(
			newLayoutPrototype.getPrimaryKey());

		Assert.assertEquals(existingLayoutPrototype, newLayoutPrototype);
	}

	@Test(expected = NoSuchLayoutPrototypeException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutPrototype> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutPrototype", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "layoutPrototypeId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "settings", true, "active", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutPrototype newLayoutPrototype = addLayoutPrototype();

		LayoutPrototype existingLayoutPrototype =
			_persistence.fetchByPrimaryKey(newLayoutPrototype.getPrimaryKey());

		Assert.assertEquals(existingLayoutPrototype, newLayoutPrototype);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPrototype missingLayoutPrototype = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingLayoutPrototype);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutPrototype newLayoutPrototype1 = addLayoutPrototype();
		LayoutPrototype newLayoutPrototype2 = addLayoutPrototype();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPrototype1.getPrimaryKey());
		primaryKeys.add(newLayoutPrototype2.getPrimaryKey());

		Map<Serializable, LayoutPrototype> layoutPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutPrototypes.size());
		Assert.assertEquals(
			newLayoutPrototype1,
			layoutPrototypes.get(newLayoutPrototype1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutPrototype2,
			layoutPrototypes.get(newLayoutPrototype2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutPrototype> layoutPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutPrototypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutPrototype newLayoutPrototype = addLayoutPrototype();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPrototype.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutPrototype> layoutPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutPrototypes.size());
		Assert.assertEquals(
			newLayoutPrototype,
			layoutPrototypes.get(newLayoutPrototype.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutPrototype> layoutPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutPrototypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutPrototype newLayoutPrototype = addLayoutPrototype();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPrototype.getPrimaryKey());

		Map<Serializable, LayoutPrototype> layoutPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutPrototypes.size());
		Assert.assertEquals(
			newLayoutPrototype,
			layoutPrototypes.get(newLayoutPrototype.getPrimaryKey()));
	}

	protected LayoutPrototype addLayoutPrototype() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPrototype layoutPrototype = _persistence.create(pk);

		layoutPrototype.setMvccVersion(RandomTestUtil.nextLong());

		layoutPrototype.setCtCollectionId(RandomTestUtil.nextLong());

		layoutPrototype.setUuid(RandomTestUtil.randomString());

		layoutPrototype.setCompanyId(RandomTestUtil.nextLong());

		layoutPrototype.setUserId(RandomTestUtil.nextLong());

		layoutPrototype.setUserName(RandomTestUtil.randomString());

		layoutPrototype.setCreateDate(RandomTestUtil.nextDate());

		layoutPrototype.setModifiedDate(RandomTestUtil.nextDate());

		layoutPrototype.setName(RandomTestUtil.randomString());

		layoutPrototype.setDescription(RandomTestUtil.randomString());

		layoutPrototype.setSettings(RandomTestUtil.randomString());

		layoutPrototype.setActive(RandomTestUtil.randomBoolean());

		_layoutPrototypes.add(_persistence.update(layoutPrototype));

		return layoutPrototype;
	}

	private List<LayoutPrototype> _layoutPrototypes =
		new ArrayList<LayoutPrototype>();
	private LayoutPrototypePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}