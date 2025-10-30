/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectLayoutTabException;
import com.liferay.object.model.ObjectLayoutTab;
import com.liferay.object.service.persistence.ObjectLayoutTabPersistence;
import com.liferay.object.service.persistence.ObjectLayoutTabUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class ObjectLayoutTabPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectLayoutTabUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectLayoutTab> iterator = _objectLayoutTabs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutTab objectLayoutTab = _persistence.create(pk);

		Assert.assertNotNull(objectLayoutTab);

		Assert.assertEquals(objectLayoutTab.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectLayoutTab newObjectLayoutTab = addObjectLayoutTab();

		_persistence.remove(newObjectLayoutTab);

		ObjectLayoutTab existingObjectLayoutTab =
			_persistence.fetchByPrimaryKey(newObjectLayoutTab.getPrimaryKey());

		Assert.assertNull(existingObjectLayoutTab);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectLayoutTab();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutTab newObjectLayoutTab = _persistence.create(pk);

		newObjectLayoutTab.setMvccVersion(RandomTestUtil.nextLong());

		newObjectLayoutTab.setUuid(RandomTestUtil.randomString());

		newObjectLayoutTab.setCompanyId(RandomTestUtil.nextLong());

		newObjectLayoutTab.setUserId(RandomTestUtil.nextLong());

		newObjectLayoutTab.setUserName(RandomTestUtil.randomString());

		newObjectLayoutTab.setCreateDate(RandomTestUtil.nextDate());

		newObjectLayoutTab.setModifiedDate(RandomTestUtil.nextDate());

		newObjectLayoutTab.setObjectLayoutId(RandomTestUtil.nextLong());

		newObjectLayoutTab.setObjectRelationshipId(RandomTestUtil.nextLong());

		newObjectLayoutTab.setName(RandomTestUtil.randomString());

		newObjectLayoutTab.setPriority(RandomTestUtil.nextInt());

		_objectLayoutTabs.add(_persistence.update(newObjectLayoutTab));

		ObjectLayoutTab existingObjectLayoutTab = _persistence.findByPrimaryKey(
			newObjectLayoutTab.getPrimaryKey());

		Assert.assertEquals(
			existingObjectLayoutTab.getMvccVersion(),
			newObjectLayoutTab.getMvccVersion());
		Assert.assertEquals(
			existingObjectLayoutTab.getUuid(), newObjectLayoutTab.getUuid());
		Assert.assertEquals(
			existingObjectLayoutTab.getObjectLayoutTabId(),
			newObjectLayoutTab.getObjectLayoutTabId());
		Assert.assertEquals(
			existingObjectLayoutTab.getCompanyId(),
			newObjectLayoutTab.getCompanyId());
		Assert.assertEquals(
			existingObjectLayoutTab.getUserId(),
			newObjectLayoutTab.getUserId());
		Assert.assertEquals(
			existingObjectLayoutTab.getUserName(),
			newObjectLayoutTab.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectLayoutTab.getCreateDate()),
			Time.getShortTimestamp(newObjectLayoutTab.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectLayoutTab.getModifiedDate()),
			Time.getShortTimestamp(newObjectLayoutTab.getModifiedDate()));
		Assert.assertEquals(
			existingObjectLayoutTab.getObjectLayoutId(),
			newObjectLayoutTab.getObjectLayoutId());
		Assert.assertEquals(
			existingObjectLayoutTab.getObjectRelationshipId(),
			newObjectLayoutTab.getObjectRelationshipId());
		Assert.assertEquals(
			existingObjectLayoutTab.getName(), newObjectLayoutTab.getName());
		Assert.assertEquals(
			existingObjectLayoutTab.getPriority(),
			newObjectLayoutTab.getPriority());
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
	public void testCountByObjectLayoutId() throws Exception {
		_persistence.countByObjectLayoutId(RandomTestUtil.nextLong());

		_persistence.countByObjectLayoutId(0L);
	}

	@Test
	public void testCountByObjectRelationshipId() throws Exception {
		_persistence.countByObjectRelationshipId(RandomTestUtil.nextLong());

		_persistence.countByObjectRelationshipId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectLayoutTab newObjectLayoutTab = addObjectLayoutTab();

		ObjectLayoutTab existingObjectLayoutTab = _persistence.findByPrimaryKey(
			newObjectLayoutTab.getPrimaryKey());

		Assert.assertEquals(existingObjectLayoutTab, newObjectLayoutTab);
	}

	@Test(expected = NoSuchObjectLayoutTabException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectLayoutTab> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectLayoutTab", "mvccVersion", true, "uuid", true,
			"objectLayoutTabId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectLayoutId", true, "objectRelationshipId", true, "name", true,
			"priority", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectLayoutTab newObjectLayoutTab = addObjectLayoutTab();

		ObjectLayoutTab existingObjectLayoutTab =
			_persistence.fetchByPrimaryKey(newObjectLayoutTab.getPrimaryKey());

		Assert.assertEquals(existingObjectLayoutTab, newObjectLayoutTab);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutTab missingObjectLayoutTab = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingObjectLayoutTab);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectLayoutTab newObjectLayoutTab1 = addObjectLayoutTab();
		ObjectLayoutTab newObjectLayoutTab2 = addObjectLayoutTab();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayoutTab1.getPrimaryKey());
		primaryKeys.add(newObjectLayoutTab2.getPrimaryKey());

		Map<Serializable, ObjectLayoutTab> objectLayoutTabs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectLayoutTabs.size());
		Assert.assertEquals(
			newObjectLayoutTab1,
			objectLayoutTabs.get(newObjectLayoutTab1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectLayoutTab2,
			objectLayoutTabs.get(newObjectLayoutTab2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectLayoutTab> objectLayoutTabs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectLayoutTabs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectLayoutTab newObjectLayoutTab = addObjectLayoutTab();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayoutTab.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectLayoutTab> objectLayoutTabs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectLayoutTabs.size());
		Assert.assertEquals(
			newObjectLayoutTab,
			objectLayoutTabs.get(newObjectLayoutTab.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectLayoutTab> objectLayoutTabs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectLayoutTabs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectLayoutTab newObjectLayoutTab = addObjectLayoutTab();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectLayoutTab.getPrimaryKey());

		Map<Serializable, ObjectLayoutTab> objectLayoutTabs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectLayoutTabs.size());
		Assert.assertEquals(
			newObjectLayoutTab,
			objectLayoutTabs.get(newObjectLayoutTab.getPrimaryKey()));
	}

	protected ObjectLayoutTab addObjectLayoutTab() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectLayoutTab objectLayoutTab = _persistence.create(pk);

		objectLayoutTab.setMvccVersion(RandomTestUtil.nextLong());

		objectLayoutTab.setUuid(RandomTestUtil.randomString());

		objectLayoutTab.setCompanyId(RandomTestUtil.nextLong());

		objectLayoutTab.setUserId(RandomTestUtil.nextLong());

		objectLayoutTab.setUserName(RandomTestUtil.randomString());

		objectLayoutTab.setCreateDate(RandomTestUtil.nextDate());

		objectLayoutTab.setModifiedDate(RandomTestUtil.nextDate());

		objectLayoutTab.setObjectLayoutId(RandomTestUtil.nextLong());

		objectLayoutTab.setObjectRelationshipId(RandomTestUtil.nextLong());

		objectLayoutTab.setName(RandomTestUtil.randomString());

		objectLayoutTab.setPriority(RandomTestUtil.nextInt());

		_objectLayoutTabs.add(_persistence.update(objectLayoutTab));

		return objectLayoutTab;
	}

	private List<ObjectLayoutTab> _objectLayoutTabs =
		new ArrayList<ObjectLayoutTab>();
	private ObjectLayoutTabPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}