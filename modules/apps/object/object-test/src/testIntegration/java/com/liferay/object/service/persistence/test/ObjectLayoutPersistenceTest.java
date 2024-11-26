/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectLayoutException;
import com.liferay.object.model.ObjectLayout;
import com.liferay.object.service.ObjectLayoutLocalServiceUtil;
import com.liferay.object.service.persistence.ObjectLayoutPersistence;
import com.liferay.object.service.persistence.ObjectLayoutUtil;
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
public class ObjectLayoutPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectLayoutUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectLayout> iterator = _objectLayouts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayout objectLayout = _persistence.create(pk);

		Assert.assertNotNull(objectLayout);

		Assert.assertEquals(objectLayout.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectLayout newObjectLayout = addObjectLayout();

		_persistence.remove(newObjectLayout);

		ObjectLayout existingObjectLayout = _persistence.fetchByPrimaryKey(
			newObjectLayout.getPrimaryKey());

		Assert.assertNull(existingObjectLayout);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectLayout();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayout newObjectLayout = _persistence.create(pk);

		newObjectLayout.setMvccVersion(RandomTestUtil.nextLong());

		newObjectLayout.setUuid(RandomTestUtil.randomString());

		newObjectLayout.setCompanyId(RandomTestUtil.nextLong());

		newObjectLayout.setUserId(RandomTestUtil.nextLong());

		newObjectLayout.setUserName(RandomTestUtil.randomString());

		newObjectLayout.setCreateDate(RandomTestUtil.nextDate());

		newObjectLayout.setModifiedDate(RandomTestUtil.nextDate());

		newObjectLayout.setObjectDefinitionId(RandomTestUtil.nextLong());

		newObjectLayout.setDefaultObjectLayout(RandomTestUtil.randomBoolean());

		newObjectLayout.setName(RandomTestUtil.randomString());

		_objectLayouts.add(_persistence.update(newObjectLayout));

		ObjectLayout existingObjectLayout = _persistence.findByPrimaryKey(
			newObjectLayout.getPrimaryKey());

		Assert.assertEquals(
			existingObjectLayout.getMvccVersion(),
			newObjectLayout.getMvccVersion());
		Assert.assertEquals(
			existingObjectLayout.getUuid(), newObjectLayout.getUuid());
		Assert.assertEquals(
			existingObjectLayout.getObjectLayoutId(),
			newObjectLayout.getObjectLayoutId());
		Assert.assertEquals(
			existingObjectLayout.getCompanyId(),
			newObjectLayout.getCompanyId());
		Assert.assertEquals(
			existingObjectLayout.getUserId(), newObjectLayout.getUserId());
		Assert.assertEquals(
			existingObjectLayout.getUserName(), newObjectLayout.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectLayout.getCreateDate()),
			Time.getShortTimestamp(newObjectLayout.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectLayout.getModifiedDate()),
			Time.getShortTimestamp(newObjectLayout.getModifiedDate()));
		Assert.assertEquals(
			existingObjectLayout.getObjectDefinitionId(),
			newObjectLayout.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectLayout.isDefaultObjectLayout(),
			newObjectLayout.isDefaultObjectLayout());
		Assert.assertEquals(
			existingObjectLayout.getName(), newObjectLayout.getName());
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
	public void testCountByObjectDefinitionId() throws Exception {
		_persistence.countByObjectDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByObjectDefinitionId(0L);
	}

	@Test
	public void testCountByC_DOL() throws Exception {
		_persistence.countByC_DOL(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_DOL(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByODI_DOL() throws Exception {
		_persistence.countByODI_DOL(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByODI_DOL(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectLayout newObjectLayout = addObjectLayout();

		ObjectLayout existingObjectLayout = _persistence.findByPrimaryKey(
			newObjectLayout.getPrimaryKey());

		Assert.assertEquals(existingObjectLayout, newObjectLayout);
	}

	@Test(expected = NoSuchObjectLayoutException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectLayout> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectLayout", "mvccVersion", true, "uuid", true, "objectLayoutId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "objectDefinitionId",
			true, "defaultObjectLayout", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectLayout newObjectLayout = addObjectLayout();

		ObjectLayout existingObjectLayout = _persistence.fetchByPrimaryKey(
			newObjectLayout.getPrimaryKey());

		Assert.assertEquals(existingObjectLayout, newObjectLayout);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayout missingObjectLayout = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectLayout);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectLayout newObjectLayout1 = addObjectLayout();
		ObjectLayout newObjectLayout2 = addObjectLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayout1.getPrimaryKey());
		primaryKeys.add(newObjectLayout2.getPrimaryKey());

		Map<Serializable, ObjectLayout> objectLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectLayouts.size());
		Assert.assertEquals(
			newObjectLayout1,
			objectLayouts.get(newObjectLayout1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectLayout2,
			objectLayouts.get(newObjectLayout2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectLayout> objectLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectLayouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectLayout newObjectLayout = addObjectLayout();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayout.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectLayout> objectLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectLayouts.size());
		Assert.assertEquals(
			newObjectLayout,
			objectLayouts.get(newObjectLayout.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectLayout> objectLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectLayouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectLayout newObjectLayout = addObjectLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayout.getPrimaryKey());

		Map<Serializable, ObjectLayout> objectLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectLayouts.size());
		Assert.assertEquals(
			newObjectLayout,
			objectLayouts.get(newObjectLayout.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ObjectLayoutLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<ObjectLayout>() {

				@Override
				public void performAction(ObjectLayout objectLayout) {
					Assert.assertNotNull(objectLayout);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ObjectLayout newObjectLayout = addObjectLayout();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectLayout.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectLayoutId", newObjectLayout.getObjectLayoutId()));

		List<ObjectLayout> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ObjectLayout existingObjectLayout = result.get(0);

		Assert.assertEquals(existingObjectLayout, newObjectLayout);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectLayout.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"objectLayoutId", RandomTestUtil.nextLong()));

		List<ObjectLayout> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ObjectLayout newObjectLayout = addObjectLayout();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectLayout.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectLayoutId"));

		Object newObjectLayoutId = newObjectLayout.getObjectLayoutId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectLayoutId", new Object[] {newObjectLayoutId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingObjectLayoutId = result.get(0);

		Assert.assertEquals(existingObjectLayoutId, newObjectLayoutId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ObjectLayout.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("objectLayoutId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"objectLayoutId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected ObjectLayout addObjectLayout() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayout objectLayout = _persistence.create(pk);

		objectLayout.setMvccVersion(RandomTestUtil.nextLong());

		objectLayout.setUuid(RandomTestUtil.randomString());

		objectLayout.setCompanyId(RandomTestUtil.nextLong());

		objectLayout.setUserId(RandomTestUtil.nextLong());

		objectLayout.setUserName(RandomTestUtil.randomString());

		objectLayout.setCreateDate(RandomTestUtil.nextDate());

		objectLayout.setModifiedDate(RandomTestUtil.nextDate());

		objectLayout.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectLayout.setDefaultObjectLayout(RandomTestUtil.randomBoolean());

		objectLayout.setName(RandomTestUtil.randomString());

		_objectLayouts.add(_persistence.update(objectLayout));

		return objectLayout;
	}

	private List<ObjectLayout> _objectLayouts = new ArrayList<ObjectLayout>();
	private ObjectLayoutPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}