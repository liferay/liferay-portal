/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureLayoutException;
import com.liferay.dynamic.data.mapping.model.DDMStructureLayout;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLayoutPersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureLayoutUtil;
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
public class DDMStructureLayoutPersistenceTest {

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
		_persistence = DDMStructureLayoutUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMStructureLayout> iterator = _ddmStructureLayouts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLayout ddmStructureLayout = _persistence.create(pk);

		Assert.assertNotNull(ddmStructureLayout);

		Assert.assertEquals(ddmStructureLayout.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMStructureLayout newDDMStructureLayout = addDDMStructureLayout();

		_persistence.remove(newDDMStructureLayout);

		DDMStructureLayout existingDDMStructureLayout =
			_persistence.fetchByPrimaryKey(
				newDDMStructureLayout.getPrimaryKey());

		Assert.assertNull(existingDDMStructureLayout);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMStructureLayout();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLayout newDDMStructureLayout = _persistence.create(pk);

		newDDMStructureLayout.setMvccVersion(RandomTestUtil.nextLong());

		newDDMStructureLayout.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMStructureLayout.setUuid(RandomTestUtil.randomString());

		newDDMStructureLayout.setGroupId(RandomTestUtil.nextLong());

		newDDMStructureLayout.setCompanyId(RandomTestUtil.nextLong());

		newDDMStructureLayout.setUserId(RandomTestUtil.nextLong());

		newDDMStructureLayout.setUserName(RandomTestUtil.randomString());

		newDDMStructureLayout.setCreateDate(RandomTestUtil.nextDate());

		newDDMStructureLayout.setModifiedDate(RandomTestUtil.nextDate());

		newDDMStructureLayout.setClassNameId(RandomTestUtil.nextLong());

		newDDMStructureLayout.setStructureLayoutKey(
			RandomTestUtil.randomString());

		newDDMStructureLayout.setStructureVersionId(RandomTestUtil.nextLong());

		newDDMStructureLayout.setName(RandomTestUtil.randomString());

		newDDMStructureLayout.setDescription(RandomTestUtil.randomString());

		newDDMStructureLayout.setDefinition(RandomTestUtil.randomString());

		_ddmStructureLayouts.add(_persistence.update(newDDMStructureLayout));

		DDMStructureLayout existingDDMStructureLayout =
			_persistence.findByPrimaryKey(
				newDDMStructureLayout.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructureLayout.getMvccVersion(),
			newDDMStructureLayout.getMvccVersion());
		Assert.assertEquals(
			existingDDMStructureLayout.getCtCollectionId(),
			newDDMStructureLayout.getCtCollectionId());
		Assert.assertEquals(
			existingDDMStructureLayout.getUuid(),
			newDDMStructureLayout.getUuid());
		Assert.assertEquals(
			existingDDMStructureLayout.getStructureLayoutId(),
			newDDMStructureLayout.getStructureLayoutId());
		Assert.assertEquals(
			existingDDMStructureLayout.getGroupId(),
			newDDMStructureLayout.getGroupId());
		Assert.assertEquals(
			existingDDMStructureLayout.getCompanyId(),
			newDDMStructureLayout.getCompanyId());
		Assert.assertEquals(
			existingDDMStructureLayout.getUserId(),
			newDDMStructureLayout.getUserId());
		Assert.assertEquals(
			existingDDMStructureLayout.getUserName(),
			newDDMStructureLayout.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMStructureLayout.getCreateDate()),
			Time.getShortTimestamp(newDDMStructureLayout.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDDMStructureLayout.getModifiedDate()),
			Time.getShortTimestamp(newDDMStructureLayout.getModifiedDate()));
		Assert.assertEquals(
			existingDDMStructureLayout.getClassNameId(),
			newDDMStructureLayout.getClassNameId());
		Assert.assertEquals(
			existingDDMStructureLayout.getStructureLayoutKey(),
			newDDMStructureLayout.getStructureLayoutKey());
		Assert.assertEquals(
			existingDDMStructureLayout.getStructureVersionId(),
			newDDMStructureLayout.getStructureVersionId());
		Assert.assertEquals(
			existingDDMStructureLayout.getName(),
			newDDMStructureLayout.getName());
		Assert.assertEquals(
			existingDDMStructureLayout.getDescription(),
			newDDMStructureLayout.getDescription());
		Assert.assertEquals(
			existingDDMStructureLayout.getDefinition(),
			newDDMStructureLayout.getDefinition());
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
	public void testCountByStructureLayoutKey() throws Exception {
		_persistence.countByStructureLayoutKey("");

		_persistence.countByStructureLayoutKey("null");

		_persistence.countByStructureLayoutKey((String)null);
	}

	@Test
	public void testCountByStructureVersionId() throws Exception {
		_persistence.countByStructureVersionId(RandomTestUtil.nextLong());

		_persistence.countByStructureVersionId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_C_S() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_S(0L, 0L, "null");

		_persistence.countByG_C_S(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_C_SV() throws Exception {
		_persistence.countByG_C_SV(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_C_SV(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMStructureLayout newDDMStructureLayout = addDDMStructureLayout();

		DDMStructureLayout existingDDMStructureLayout =
			_persistence.findByPrimaryKey(
				newDDMStructureLayout.getPrimaryKey());

		Assert.assertEquals(existingDDMStructureLayout, newDDMStructureLayout);
	}

	@Test(expected = NoSuchStructureLayoutException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMStructureLayout> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMStructureLayout", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "structureLayoutId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true,
			"structureLayoutKey", true, "structureVersionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMStructureLayout newDDMStructureLayout = addDDMStructureLayout();

		DDMStructureLayout existingDDMStructureLayout =
			_persistence.fetchByPrimaryKey(
				newDDMStructureLayout.getPrimaryKey());

		Assert.assertEquals(existingDDMStructureLayout, newDDMStructureLayout);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLayout missingDDMStructureLayout =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMStructureLayout);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMStructureLayout newDDMStructureLayout1 = addDDMStructureLayout();
		DDMStructureLayout newDDMStructureLayout2 = addDDMStructureLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLayout1.getPrimaryKey());
		primaryKeys.add(newDDMStructureLayout2.getPrimaryKey());

		Map<Serializable, DDMStructureLayout> ddmStructureLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmStructureLayouts.size());
		Assert.assertEquals(
			newDDMStructureLayout1,
			ddmStructureLayouts.get(newDDMStructureLayout1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMStructureLayout2,
			ddmStructureLayouts.get(newDDMStructureLayout2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMStructureLayout> ddmStructureLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureLayouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMStructureLayout newDDMStructureLayout = addDDMStructureLayout();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLayout.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMStructureLayout> ddmStructureLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureLayouts.size());
		Assert.assertEquals(
			newDDMStructureLayout,
			ddmStructureLayouts.get(newDDMStructureLayout.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMStructureLayout> ddmStructureLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructureLayouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMStructureLayout newDDMStructureLayout = addDDMStructureLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructureLayout.getPrimaryKey());

		Map<Serializable, DDMStructureLayout> ddmStructureLayouts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructureLayouts.size());
		Assert.assertEquals(
			newDDMStructureLayout,
			ddmStructureLayouts.get(newDDMStructureLayout.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMStructureLayout newDDMStructureLayout = addDDMStructureLayout();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDDMStructureLayout.getPrimaryKey()));
	}

	private void _assertOriginalValues(DDMStructureLayout ddmStructureLayout) {
		Assert.assertEquals(
			ddmStructureLayout.getUuid(),
			ReflectionTestUtil.invoke(
				ddmStructureLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddmStructureLayout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(ddmStructureLayout.getStructureVersionId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "structureVersionId"));

		Assert.assertEquals(
			Long.valueOf(ddmStructureLayout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(ddmStructureLayout.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructureLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			ddmStructureLayout.getStructureLayoutKey(),
			ReflectionTestUtil.invoke(
				ddmStructureLayout, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "structureLayoutKey"));
	}

	protected DDMStructureLayout addDDMStructureLayout() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructureLayout ddmStructureLayout = _persistence.create(pk);

		ddmStructureLayout.setMvccVersion(RandomTestUtil.nextLong());

		ddmStructureLayout.setCtCollectionId(RandomTestUtil.nextLong());

		ddmStructureLayout.setUuid(RandomTestUtil.randomString());

		ddmStructureLayout.setGroupId(RandomTestUtil.nextLong());

		ddmStructureLayout.setCompanyId(RandomTestUtil.nextLong());

		ddmStructureLayout.setUserId(RandomTestUtil.nextLong());

		ddmStructureLayout.setUserName(RandomTestUtil.randomString());

		ddmStructureLayout.setCreateDate(RandomTestUtil.nextDate());

		ddmStructureLayout.setModifiedDate(RandomTestUtil.nextDate());

		ddmStructureLayout.setClassNameId(RandomTestUtil.nextLong());

		ddmStructureLayout.setStructureLayoutKey(RandomTestUtil.randomString());

		ddmStructureLayout.setStructureVersionId(RandomTestUtil.nextLong());

		ddmStructureLayout.setName(RandomTestUtil.randomString());

		ddmStructureLayout.setDescription(RandomTestUtil.randomString());

		ddmStructureLayout.setDefinition(RandomTestUtil.randomString());

		_ddmStructureLayouts.add(_persistence.update(ddmStructureLayout));

		return ddmStructureLayout;
	}

	private List<DDMStructureLayout> _ddmStructureLayouts =
		new ArrayList<DDMStructureLayout>();
	private DDMStructureLayoutPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}