/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructure;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructurePersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureUtil;
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
public class LayoutPageTemplateStructurePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.layout.page.template.service"));

	@Before
	public void setUp() {
		_persistence = LayoutPageTemplateStructureUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutPageTemplateStructure> iterator =
			_layoutPageTemplateStructures.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_persistence.create(pk);

		Assert.assertNotNull(layoutPageTemplateStructure);

		Assert.assertEquals(layoutPageTemplateStructure.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			addLayoutPageTemplateStructure();

		_persistence.remove(newLayoutPageTemplateStructure);

		LayoutPageTemplateStructure existingLayoutPageTemplateStructure =
			_persistence.fetchByPrimaryKey(
				newLayoutPageTemplateStructure.getPrimaryKey());

		Assert.assertNull(existingLayoutPageTemplateStructure);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutPageTemplateStructure();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			_persistence.create(pk);

		newLayoutPageTemplateStructure.setMvccVersion(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructure.setCtCollectionId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructure.setUuid(RandomTestUtil.randomString());

		newLayoutPageTemplateStructure.setGroupId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructure.setCompanyId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructure.setUserId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructure.setUserName(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructure.setCreateDate(RandomTestUtil.nextDate());

		newLayoutPageTemplateStructure.setModifiedDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateStructure.setPlid(RandomTestUtil.nextLong());

		_layoutPageTemplateStructures.add(
			_persistence.update(newLayoutPageTemplateStructure));

		LayoutPageTemplateStructure existingLayoutPageTemplateStructure =
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructure.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getMvccVersion(),
			newLayoutPageTemplateStructure.getMvccVersion());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getCtCollectionId(),
			newLayoutPageTemplateStructure.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getUuid(),
			newLayoutPageTemplateStructure.getUuid());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.
				getLayoutPageTemplateStructureId(),
			newLayoutPageTemplateStructure.getLayoutPageTemplateStructureId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getGroupId(),
			newLayoutPageTemplateStructure.getGroupId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getCompanyId(),
			newLayoutPageTemplateStructure.getCompanyId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getUserId(),
			newLayoutPageTemplateStructure.getUserId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getUserName(),
			newLayoutPageTemplateStructure.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructure.getCreateDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructure.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructure.getModifiedDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructure.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutPageTemplateStructure.getPlid(),
			newLayoutPageTemplateStructure.getPlid());
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
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			addLayoutPageTemplateStructure();

		LayoutPageTemplateStructure existingLayoutPageTemplateStructure =
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructure.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructure,
			newLayoutPageTemplateStructure);
	}

	@Test(expected = NoSuchPageTemplateStructureException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutPageTemplateStructure>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LayoutPageTemplateStructure", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"layoutPageTemplateStructureId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "plid", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			addLayoutPageTemplateStructure();

		LayoutPageTemplateStructure existingLayoutPageTemplateStructure =
			_persistence.fetchByPrimaryKey(
				newLayoutPageTemplateStructure.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructure,
			newLayoutPageTemplateStructure);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructure missingLayoutPageTemplateStructure =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutPageTemplateStructure);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructure newLayoutPageTemplateStructure1 =
			addLayoutPageTemplateStructure();
		LayoutPageTemplateStructure newLayoutPageTemplateStructure2 =
			addLayoutPageTemplateStructure();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateStructure1.getPrimaryKey());
		primaryKeys.add(newLayoutPageTemplateStructure2.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateStructure>
			layoutPageTemplateStructures = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, layoutPageTemplateStructures.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructure1,
			layoutPageTemplateStructures.get(
				newLayoutPageTemplateStructure1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutPageTemplateStructure2,
			layoutPageTemplateStructures.get(
				newLayoutPageTemplateStructure2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutPageTemplateStructure>
			layoutPageTemplateStructures = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutPageTemplateStructures.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			addLayoutPageTemplateStructure();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateStructure.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutPageTemplateStructure>
			layoutPageTemplateStructures = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutPageTemplateStructures.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructure,
			layoutPageTemplateStructures.get(
				newLayoutPageTemplateStructure.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutPageTemplateStructure>
			layoutPageTemplateStructures = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutPageTemplateStructures.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			addLayoutPageTemplateStructure();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateStructure.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateStructure>
			layoutPageTemplateStructures = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutPageTemplateStructures.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructure,
			layoutPageTemplateStructures.get(
				newLayoutPageTemplateStructure.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutPageTemplateStructure newLayoutPageTemplateStructure =
			addLayoutPageTemplateStructure();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructure.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		LayoutPageTemplateStructure layoutPageTemplateStructure) {

		Assert.assertEquals(
			layoutPageTemplateStructure.getUuid(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateStructure.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateStructure.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateStructure.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
	}

	protected LayoutPageTemplateStructure addLayoutPageTemplateStructure()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructure layoutPageTemplateStructure =
			_persistence.create(pk);

		layoutPageTemplateStructure.setMvccVersion(RandomTestUtil.nextLong());

		layoutPageTemplateStructure.setCtCollectionId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructure.setUuid(RandomTestUtil.randomString());

		layoutPageTemplateStructure.setGroupId(RandomTestUtil.nextLong());

		layoutPageTemplateStructure.setCompanyId(RandomTestUtil.nextLong());

		layoutPageTemplateStructure.setUserId(RandomTestUtil.nextLong());

		layoutPageTemplateStructure.setUserName(RandomTestUtil.randomString());

		layoutPageTemplateStructure.setCreateDate(RandomTestUtil.nextDate());

		layoutPageTemplateStructure.setModifiedDate(RandomTestUtil.nextDate());

		layoutPageTemplateStructure.setPlid(RandomTestUtil.nextLong());

		_layoutPageTemplateStructures.add(
			_persistence.update(layoutPageTemplateStructure));

		return layoutPageTemplateStructure;
	}

	private List<LayoutPageTemplateStructure> _layoutPageTemplateStructures =
		new ArrayList<LayoutPageTemplateStructure>();
	private LayoutPageTemplateStructurePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}