/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetPrototypeException;
import com.liferay.portal.kernel.model.LayoutSetPrototype;
import com.liferay.portal.kernel.service.LayoutSetPrototypeLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.LayoutSetPrototypePersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetPrototypeUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class LayoutSetPrototypePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = LayoutSetPrototypeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutSetPrototype> iterator = _layoutSetPrototypes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetPrototype layoutSetPrototype = _persistence.create(pk);

		Assert.assertNotNull(layoutSetPrototype);

		Assert.assertEquals(layoutSetPrototype.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		_persistence.remove(newLayoutSetPrototype);

		LayoutSetPrototype existingLayoutSetPrototype =
			_persistence.fetchByPrimaryKey(
				newLayoutSetPrototype.getPrimaryKey());

		Assert.assertNull(existingLayoutSetPrototype);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutSetPrototype();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetPrototype newLayoutSetPrototype = _persistence.create(pk);

		newLayoutSetPrototype.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutSetPrototype.setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutSetPrototype.setUuid(RandomTestUtil.randomString());

		newLayoutSetPrototype.setCompanyId(RandomTestUtil.nextLong());

		newLayoutSetPrototype.setUserId(RandomTestUtil.nextLong());

		newLayoutSetPrototype.setUserName(RandomTestUtil.randomString());

		newLayoutSetPrototype.setCreateDate(RandomTestUtil.nextDate());

		newLayoutSetPrototype.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutSetPrototype.setName(RandomTestUtil.randomString());

		newLayoutSetPrototype.setDescription(RandomTestUtil.randomString());

		newLayoutSetPrototype.setSettings(RandomTestUtil.randomString());

		newLayoutSetPrototype.setActive(RandomTestUtil.randomBoolean());

		_layoutSetPrototypes.add(_persistence.update(newLayoutSetPrototype));

		LayoutSetPrototype existingLayoutSetPrototype =
			_persistence.findByPrimaryKey(
				newLayoutSetPrototype.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSetPrototype.getMvccVersion(),
			newLayoutSetPrototype.getMvccVersion());
		Assert.assertEquals(
			existingLayoutSetPrototype.getCtCollectionId(),
			newLayoutSetPrototype.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutSetPrototype.getUuid(),
			newLayoutSetPrototype.getUuid());
		Assert.assertEquals(
			existingLayoutSetPrototype.getLayoutSetPrototypeId(),
			newLayoutSetPrototype.getLayoutSetPrototypeId());
		Assert.assertEquals(
			existingLayoutSetPrototype.getCompanyId(),
			newLayoutSetPrototype.getCompanyId());
		Assert.assertEquals(
			existingLayoutSetPrototype.getUserId(),
			newLayoutSetPrototype.getUserId());
		Assert.assertEquals(
			existingLayoutSetPrototype.getUserName(),
			newLayoutSetPrototype.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSetPrototype.getCreateDate()),
			Time.getShortTimestamp(newLayoutSetPrototype.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutSetPrototype.getModifiedDate()),
			Time.getShortTimestamp(newLayoutSetPrototype.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutSetPrototype.getName(),
			newLayoutSetPrototype.getName());
		Assert.assertEquals(
			existingLayoutSetPrototype.getDescription(),
			newLayoutSetPrototype.getDescription());
		Assert.assertEquals(
			existingLayoutSetPrototype.getSettings(),
			newLayoutSetPrototype.getSettings());
		Assert.assertEquals(
			existingLayoutSetPrototype.isActive(),
			newLayoutSetPrototype.isActive());
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
		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		LayoutSetPrototype existingLayoutSetPrototype =
			_persistence.findByPrimaryKey(
				newLayoutSetPrototype.getPrimaryKey());

		Assert.assertEquals(existingLayoutSetPrototype, newLayoutSetPrototype);
	}

	@Test(expected = NoSuchLayoutSetPrototypeException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutSetPrototype> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutSetPrototype", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "layoutSetPrototypeId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "settings", true, "active", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		LayoutSetPrototype existingLayoutSetPrototype =
			_persistence.fetchByPrimaryKey(
				newLayoutSetPrototype.getPrimaryKey());

		Assert.assertEquals(existingLayoutSetPrototype, newLayoutSetPrototype);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetPrototype missingLayoutSetPrototype =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutSetPrototype);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutSetPrototype newLayoutSetPrototype1 = addLayoutSetPrototype();
		LayoutSetPrototype newLayoutSetPrototype2 = addLayoutSetPrototype();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSetPrototype1.getPrimaryKey());
		primaryKeys.add(newLayoutSetPrototype2.getPrimaryKey());

		Map<Serializable, LayoutSetPrototype> layoutSetPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutSetPrototypes.size());
		Assert.assertEquals(
			newLayoutSetPrototype1,
			layoutSetPrototypes.get(newLayoutSetPrototype1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutSetPrototype2,
			layoutSetPrototypes.get(newLayoutSetPrototype2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutSetPrototype> layoutSetPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSetPrototypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSetPrototype.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutSetPrototype> layoutSetPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSetPrototypes.size());
		Assert.assertEquals(
			newLayoutSetPrototype,
			layoutSetPrototypes.get(newLayoutSetPrototype.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutSetPrototype> layoutSetPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSetPrototypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSetPrototype.getPrimaryKey());

		Map<Serializable, LayoutSetPrototype> layoutSetPrototypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSetPrototypes.size());
		Assert.assertEquals(
			newLayoutSetPrototype,
			layoutSetPrototypes.get(newLayoutSetPrototype.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			LayoutSetPrototypeLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<LayoutSetPrototype>() {

				@Override
				public void performAction(
					LayoutSetPrototype layoutSetPrototype) {

					Assert.assertNotNull(layoutSetPrototype);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutSetPrototype.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutSetPrototypeId",
				newLayoutSetPrototype.getLayoutSetPrototypeId()));

		List<LayoutSetPrototype> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		LayoutSetPrototype existingLayoutSetPrototype = result.get(0);

		Assert.assertEquals(existingLayoutSetPrototype, newLayoutSetPrototype);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutSetPrototype.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"layoutSetPrototypeId", RandomTestUtil.nextLong()));

		List<LayoutSetPrototype> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		LayoutSetPrototype newLayoutSetPrototype = addLayoutSetPrototype();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutSetPrototype.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("layoutSetPrototypeId"));

		Object newLayoutSetPrototypeId =
			newLayoutSetPrototype.getLayoutSetPrototypeId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"layoutSetPrototypeId",
				new Object[] {newLayoutSetPrototypeId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingLayoutSetPrototypeId = result.get(0);

		Assert.assertEquals(
			existingLayoutSetPrototypeId, newLayoutSetPrototypeId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LayoutSetPrototype.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("layoutSetPrototypeId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"layoutSetPrototypeId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected LayoutSetPrototype addLayoutSetPrototype() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetPrototype layoutSetPrototype = _persistence.create(pk);

		layoutSetPrototype.setMvccVersion(RandomTestUtil.nextLong());

		layoutSetPrototype.setCtCollectionId(RandomTestUtil.nextLong());

		layoutSetPrototype.setUuid(RandomTestUtil.randomString());

		layoutSetPrototype.setCompanyId(RandomTestUtil.nextLong());

		layoutSetPrototype.setUserId(RandomTestUtil.nextLong());

		layoutSetPrototype.setUserName(RandomTestUtil.randomString());

		layoutSetPrototype.setCreateDate(RandomTestUtil.nextDate());

		layoutSetPrototype.setModifiedDate(RandomTestUtil.nextDate());

		layoutSetPrototype.setName(RandomTestUtil.randomString());

		layoutSetPrototype.setDescription(RandomTestUtil.randomString());

		layoutSetPrototype.setSettings(RandomTestUtil.randomString());

		layoutSetPrototype.setActive(RandomTestUtil.randomBoolean());

		_layoutSetPrototypes.add(_persistence.update(layoutSetPrototype));

		return layoutSetPrototype;
	}

	private List<LayoutSetPrototype> _layoutSetPrototypes =
		new ArrayList<LayoutSetPrototype>();
	private LayoutSetPrototypePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}