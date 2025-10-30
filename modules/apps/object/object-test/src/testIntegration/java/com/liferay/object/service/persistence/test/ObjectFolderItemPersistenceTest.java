/**
 * SPDX-FileCopyrightText: (c) 2023 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.object.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.object.exception.NoSuchObjectFolderItemException;
import com.liferay.object.model.ObjectFolderItem;
import com.liferay.object.service.persistence.ObjectFolderItemPersistence;
import com.liferay.object.service.persistence.ObjectFolderItemUtil;
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
public class ObjectFolderItemPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.object.service"));

	@Before
	public void setUp() {
		_persistence = ObjectFolderItemUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ObjectFolderItem> iterator = _objectFolderItems.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolderItem objectFolderItem = _persistence.create(pk);

		Assert.assertNotNull(objectFolderItem);

		Assert.assertEquals(objectFolderItem.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ObjectFolderItem newObjectFolderItem = addObjectFolderItem();

		_persistence.remove(newObjectFolderItem);

		ObjectFolderItem existingObjectFolderItem =
			_persistence.fetchByPrimaryKey(newObjectFolderItem.getPrimaryKey());

		Assert.assertNull(existingObjectFolderItem);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addObjectFolderItem();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolderItem newObjectFolderItem = _persistence.create(pk);

		newObjectFolderItem.setMvccVersion(RandomTestUtil.nextLong());

		newObjectFolderItem.setUuid(RandomTestUtil.randomString());

		newObjectFolderItem.setCompanyId(RandomTestUtil.nextLong());

		newObjectFolderItem.setUserId(RandomTestUtil.nextLong());

		newObjectFolderItem.setUserName(RandomTestUtil.randomString());

		newObjectFolderItem.setCreateDate(RandomTestUtil.nextDate());

		newObjectFolderItem.setModifiedDate(RandomTestUtil.nextDate());

		newObjectFolderItem.setObjectDefinitionId(RandomTestUtil.nextLong());

		newObjectFolderItem.setObjectFolderId(RandomTestUtil.nextLong());

		newObjectFolderItem.setPositionX(RandomTestUtil.nextInt());

		newObjectFolderItem.setPositionY(RandomTestUtil.nextInt());

		_objectFolderItems.add(_persistence.update(newObjectFolderItem));

		ObjectFolderItem existingObjectFolderItem =
			_persistence.findByPrimaryKey(newObjectFolderItem.getPrimaryKey());

		Assert.assertEquals(
			existingObjectFolderItem.getMvccVersion(),
			newObjectFolderItem.getMvccVersion());
		Assert.assertEquals(
			existingObjectFolderItem.getUuid(), newObjectFolderItem.getUuid());
		Assert.assertEquals(
			existingObjectFolderItem.getObjectFolderItemId(),
			newObjectFolderItem.getObjectFolderItemId());
		Assert.assertEquals(
			existingObjectFolderItem.getCompanyId(),
			newObjectFolderItem.getCompanyId());
		Assert.assertEquals(
			existingObjectFolderItem.getUserId(),
			newObjectFolderItem.getUserId());
		Assert.assertEquals(
			existingObjectFolderItem.getUserName(),
			newObjectFolderItem.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectFolderItem.getCreateDate()),
			Time.getShortTimestamp(newObjectFolderItem.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingObjectFolderItem.getModifiedDate()),
			Time.getShortTimestamp(newObjectFolderItem.getModifiedDate()));
		Assert.assertEquals(
			existingObjectFolderItem.getObjectDefinitionId(),
			newObjectFolderItem.getObjectDefinitionId());
		Assert.assertEquals(
			existingObjectFolderItem.getObjectFolderId(),
			newObjectFolderItem.getObjectFolderId());
		Assert.assertEquals(
			existingObjectFolderItem.getPositionX(),
			newObjectFolderItem.getPositionX());
		Assert.assertEquals(
			existingObjectFolderItem.getPositionY(),
			newObjectFolderItem.getPositionY());
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
	public void testCountByObjectFolderId() throws Exception {
		_persistence.countByObjectFolderId(RandomTestUtil.nextLong());

		_persistence.countByObjectFolderId(0L);
	}

	@Test
	public void testCountByODI_OFI() throws Exception {
		_persistence.countByODI_OFI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByODI_OFI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ObjectFolderItem newObjectFolderItem = addObjectFolderItem();

		ObjectFolderItem existingObjectFolderItem =
			_persistence.findByPrimaryKey(newObjectFolderItem.getPrimaryKey());

		Assert.assertEquals(existingObjectFolderItem, newObjectFolderItem);
	}

	@Test(expected = NoSuchObjectFolderItemException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ObjectFolderItem> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ObjectFolderItem", "mvccVersion", true, "uuid", true,
			"objectFolderItemId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"objectDefinitionId", true, "objectFolderId", true, "positionX",
			true, "positionY", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ObjectFolderItem newObjectFolderItem = addObjectFolderItem();

		ObjectFolderItem existingObjectFolderItem =
			_persistence.fetchByPrimaryKey(newObjectFolderItem.getPrimaryKey());

		Assert.assertEquals(existingObjectFolderItem, newObjectFolderItem);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolderItem missingObjectFolderItem =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingObjectFolderItem);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ObjectFolderItem newObjectFolderItem1 = addObjectFolderItem();
		ObjectFolderItem newObjectFolderItem2 = addObjectFolderItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFolderItem1.getPrimaryKey());
		primaryKeys.add(newObjectFolderItem2.getPrimaryKey());

		Map<Serializable, ObjectFolderItem> objectFolderItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, objectFolderItems.size());
		Assert.assertEquals(
			newObjectFolderItem1,
			objectFolderItems.get(newObjectFolderItem1.getPrimaryKey()));
		Assert.assertEquals(
			newObjectFolderItem2,
			objectFolderItems.get(newObjectFolderItem2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ObjectFolderItem> objectFolderItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFolderItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ObjectFolderItem newObjectFolderItem = addObjectFolderItem();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFolderItem.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ObjectFolderItem> objectFolderItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFolderItems.size());
		Assert.assertEquals(
			newObjectFolderItem,
			objectFolderItems.get(newObjectFolderItem.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ObjectFolderItem> objectFolderItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(objectFolderItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ObjectFolderItem newObjectFolderItem = addObjectFolderItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newObjectFolderItem.getPrimaryKey());

		Map<Serializable, ObjectFolderItem> objectFolderItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, objectFolderItems.size());
		Assert.assertEquals(
			newObjectFolderItem,
			objectFolderItems.get(newObjectFolderItem.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ObjectFolderItem newObjectFolderItem = addObjectFolderItem();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newObjectFolderItem.getPrimaryKey()));
	}

	private void _assertOriginalValues(ObjectFolderItem objectFolderItem) {
		Assert.assertEquals(
			Long.valueOf(objectFolderItem.getObjectDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				objectFolderItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectDefinitionId"));
		Assert.assertEquals(
			Long.valueOf(objectFolderItem.getObjectFolderId()),
			ReflectionTestUtil.<Long>invoke(
				objectFolderItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "objectFolderId"));
	}

	protected ObjectFolderItem addObjectFolderItem() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ObjectFolderItem objectFolderItem = _persistence.create(pk);

		objectFolderItem.setMvccVersion(RandomTestUtil.nextLong());

		objectFolderItem.setUuid(RandomTestUtil.randomString());

		objectFolderItem.setCompanyId(RandomTestUtil.nextLong());

		objectFolderItem.setUserId(RandomTestUtil.nextLong());

		objectFolderItem.setUserName(RandomTestUtil.randomString());

		objectFolderItem.setCreateDate(RandomTestUtil.nextDate());

		objectFolderItem.setModifiedDate(RandomTestUtil.nextDate());

		objectFolderItem.setObjectDefinitionId(RandomTestUtil.nextLong());

		objectFolderItem.setObjectFolderId(RandomTestUtil.nextLong());

		objectFolderItem.setPositionX(RandomTestUtil.nextInt());

		objectFolderItem.setPositionY(RandomTestUtil.nextInt());

		_objectFolderItems.add(_persistence.update(objectFolderItem));

		return objectFolderItem;
	}

	private List<ObjectFolderItem> _objectFolderItems =
		new ArrayList<ObjectFolderItem>();
	private ObjectFolderItemPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}