/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchContentException;
import com.liferay.dynamic.data.mapping.model.DDMContent;
import com.liferay.dynamic.data.mapping.service.persistence.DDMContentPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMContentUtil;
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
public class DDMContentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMContentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMContent> iterator = _ddmContents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMContent ddmContent = _persistence.create(pk);

		Assert.assertNotNull(ddmContent);

		Assert.assertEquals(ddmContent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMContent newDDMContent = addDDMContent();

		_persistence.remove(newDDMContent);

		DDMContent existingDDMContent = _persistence.fetchByPrimaryKey(
			newDDMContent.getPrimaryKey());

		Assert.assertNull(existingDDMContent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMContent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMContent newDDMContent = _persistence.create(pk);

		newDDMContent.setMvccVersion(RandomTestUtil.nextLong());

		newDDMContent.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMContent.setUuid(RandomTestUtil.randomString());

		newDDMContent.setGroupId(RandomTestUtil.nextLong());

		newDDMContent.setCompanyId(RandomTestUtil.nextLong());

		newDDMContent.setUserId(RandomTestUtil.nextLong());

		newDDMContent.setUserName(RandomTestUtil.randomString());

		newDDMContent.setCreateDate(RandomTestUtil.nextDate());

		newDDMContent.setModifiedDate(RandomTestUtil.nextDate());

		newDDMContent.setName(RandomTestUtil.randomString());

		newDDMContent.setDescription(RandomTestUtil.randomString());

		newDDMContent.setData(RandomTestUtil.randomString());

		_ddmContents.add(_persistence.update(newDDMContent));

		DDMContent existingDDMContent = _persistence.findByPrimaryKey(
			newDDMContent.getPrimaryKey());

		Assert.assertEquals(
			existingDDMContent.getMvccVersion(),
			newDDMContent.getMvccVersion());
		Assert.assertEquals(
			existingDDMContent.getCtCollectionId(),
			newDDMContent.getCtCollectionId());
		Assert.assertEquals(
			existingDDMContent.getUuid(), newDDMContent.getUuid());
		Assert.assertEquals(
			existingDDMContent.getContentId(), newDDMContent.getContentId());
		Assert.assertEquals(
			existingDDMContent.getGroupId(), newDDMContent.getGroupId());
		Assert.assertEquals(
			existingDDMContent.getCompanyId(), newDDMContent.getCompanyId());
		Assert.assertEquals(
			existingDDMContent.getUserId(), newDDMContent.getUserId());
		Assert.assertEquals(
			existingDDMContent.getUserName(), newDDMContent.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMContent.getCreateDate()),
			Time.getShortTimestamp(newDDMContent.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMContent.getModifiedDate()),
			Time.getShortTimestamp(newDDMContent.getModifiedDate()));
		Assert.assertEquals(
			existingDDMContent.getName(), newDDMContent.getName());
		Assert.assertEquals(
			existingDDMContent.getDescription(),
			newDDMContent.getDescription());
		Assert.assertEquals(
			existingDDMContent.getData(), newDDMContent.getData());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMContent newDDMContent = addDDMContent();

		DDMContent existingDDMContent = _persistence.findByPrimaryKey(
			newDDMContent.getPrimaryKey());

		Assert.assertEquals(existingDDMContent, newDDMContent);
	}

	@Test(expected = NoSuchContentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMContent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMContent", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "contentId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "name", true, "description", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMContent newDDMContent = addDDMContent();

		DDMContent existingDDMContent = _persistence.fetchByPrimaryKey(
			newDDMContent.getPrimaryKey());

		Assert.assertEquals(existingDDMContent, newDDMContent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMContent missingDDMContent = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMContent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMContent newDDMContent1 = addDDMContent();
		DDMContent newDDMContent2 = addDDMContent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMContent1.getPrimaryKey());
		primaryKeys.add(newDDMContent2.getPrimaryKey());

		Map<Serializable, DDMContent> ddmContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmContents.size());
		Assert.assertEquals(
			newDDMContent1, ddmContents.get(newDDMContent1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMContent2, ddmContents.get(newDDMContent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMContent> ddmContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmContents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMContent newDDMContent = addDDMContent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMContent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMContent> ddmContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmContents.size());
		Assert.assertEquals(
			newDDMContent, ddmContents.get(newDDMContent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMContent> ddmContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmContents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMContent newDDMContent = addDDMContent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMContent.getPrimaryKey());

		Map<Serializable, DDMContent> ddmContents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmContents.size());
		Assert.assertEquals(
			newDDMContent, ddmContents.get(newDDMContent.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMContent newDDMContent = addDDMContent();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMContent.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDMContent ddmContent) {
		Assert.assertEquals(
			ddmContent.getUuid(),
			ReflectionTestUtil.invoke(
				ddmContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddmContent.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmContent, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected DDMContent addDDMContent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMContent ddmContent = _persistence.create(pk);

		ddmContent.setMvccVersion(RandomTestUtil.nextLong());

		ddmContent.setCtCollectionId(RandomTestUtil.nextLong());

		ddmContent.setUuid(RandomTestUtil.randomString());

		ddmContent.setGroupId(RandomTestUtil.nextLong());

		ddmContent.setCompanyId(RandomTestUtil.nextLong());

		ddmContent.setUserId(RandomTestUtil.nextLong());

		ddmContent.setUserName(RandomTestUtil.randomString());

		ddmContent.setCreateDate(RandomTestUtil.nextDate());

		ddmContent.setModifiedDate(RandomTestUtil.nextDate());

		ddmContent.setName(RandomTestUtil.randomString());

		ddmContent.setDescription(RandomTestUtil.randomString());

		ddmContent.setData(RandomTestUtil.randomString());

		_ddmContents.add(_persistence.update(ddmContent));

		return ddmContent;
	}

	private List<DDMContent> _ddmContents = new ArrayList<DDMContent>();
	private DDMContentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}