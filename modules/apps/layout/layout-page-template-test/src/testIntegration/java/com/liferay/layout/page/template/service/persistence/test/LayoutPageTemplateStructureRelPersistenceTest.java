/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.page.template.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.page.template.exception.NoSuchPageTemplateStructureRelException;
import com.liferay.layout.page.template.model.LayoutPageTemplateStructureRel;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelPersistence;
import com.liferay.layout.page.template.service.persistence.LayoutPageTemplateStructureRelUtil;
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
public class LayoutPageTemplateStructureRelPersistenceTest {

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
		_persistence = LayoutPageTemplateStructureRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutPageTemplateStructureRel> iterator =
			_layoutPageTemplateStructureRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_persistence.create(pk);

		Assert.assertNotNull(layoutPageTemplateStructureRel);

		Assert.assertEquals(layoutPageTemplateStructureRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			addLayoutPageTemplateStructureRel();

		_persistence.remove(newLayoutPageTemplateStructureRel);

		LayoutPageTemplateStructureRel existingLayoutPageTemplateStructureRel =
			_persistence.fetchByPrimaryKey(
				newLayoutPageTemplateStructureRel.getPrimaryKey());

		Assert.assertNull(existingLayoutPageTemplateStructureRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutPageTemplateStructureRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			_persistence.create(pk);

		newLayoutPageTemplateStructureRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setUuid(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRel.setGroupId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setCompanyId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setUserId(RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setUserName(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRel.setCreateDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRel.setLayoutPageTemplateStructureId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setSegmentsExperienceId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setData(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		newLayoutPageTemplateStructureRel.setStatus(RandomTestUtil.nextInt());

		newLayoutPageTemplateStructureRel.setStatusByUserId(
			RandomTestUtil.nextLong());

		newLayoutPageTemplateStructureRel.setStatusByUserName(
			RandomTestUtil.randomString());

		newLayoutPageTemplateStructureRel.setStatusDate(
			RandomTestUtil.nextDate());

		_layoutPageTemplateStructureRels.add(
			_persistence.update(newLayoutPageTemplateStructureRel));

		LayoutPageTemplateStructureRel existingLayoutPageTemplateStructureRel =
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructureRel.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getMvccVersion(),
			newLayoutPageTemplateStructureRel.getMvccVersion());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getCtCollectionId(),
			newLayoutPageTemplateStructureRel.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getUuid(),
			newLayoutPageTemplateStructureRel.getUuid());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.
				getLayoutPageTemplateStructureRelId(),
			newLayoutPageTemplateStructureRel.
				getLayoutPageTemplateStructureRelId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getGroupId(),
			newLayoutPageTemplateStructureRel.getGroupId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getCompanyId(),
			newLayoutPageTemplateStructureRel.getCompanyId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getUserId(),
			newLayoutPageTemplateStructureRel.getUserId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getUserName(),
			newLayoutPageTemplateStructureRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRel.getCreateDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRel.getModifiedDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRel.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.
				getLayoutPageTemplateStructureId(),
			newLayoutPageTemplateStructureRel.
				getLayoutPageTemplateStructureId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getSegmentsExperienceId(),
			newLayoutPageTemplateStructureRel.getSegmentsExperienceId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getData(),
			newLayoutPageTemplateStructureRel.getData());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRel.getLastPublishDate()));
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getStatus(),
			newLayoutPageTemplateStructureRel.getStatus());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getStatusByUserId(),
			newLayoutPageTemplateStructureRel.getStatusByUserId());
		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel.getStatusByUserName(),
			newLayoutPageTemplateStructureRel.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutPageTemplateStructureRel.getStatusDate()),
			Time.getShortTimestamp(
				newLayoutPageTemplateStructureRel.getStatusDate()));
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
	public void testCountByLayoutPageTemplateStructureId() throws Exception {
		_persistence.countByLayoutPageTemplateStructureId(
			RandomTestUtil.nextLong());

		_persistence.countByLayoutPageTemplateStructureId(0L);
	}

	@Test
	public void testCountBySegmentsExperienceId() throws Exception {
		_persistence.countBySegmentsExperienceId(RandomTestUtil.nextLong());

		_persistence.countBySegmentsExperienceId(0L);
	}

	@Test
	public void testCountByL_S() throws Exception {
		_persistence.countByL_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByL_S(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			addLayoutPageTemplateStructureRel();

		LayoutPageTemplateStructureRel existingLayoutPageTemplateStructureRel =
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructureRel.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel,
			newLayoutPageTemplateStructureRel);
	}

	@Test(expected = NoSuchPageTemplateStructureRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutPageTemplateStructureRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LayoutPageTemplateStructureRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"layoutPageTemplateStructureRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "layoutPageTemplateStructureId", true,
			"segmentsExperienceId", true, "lastPublishDate", true, "status",
			true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			addLayoutPageTemplateStructureRel();

		LayoutPageTemplateStructureRel existingLayoutPageTemplateStructureRel =
			_persistence.fetchByPrimaryKey(
				newLayoutPageTemplateStructureRel.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutPageTemplateStructureRel,
			newLayoutPageTemplateStructureRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRel missingLayoutPageTemplateStructureRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutPageTemplateStructureRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel1 =
			addLayoutPageTemplateStructureRel();
		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel2 =
			addLayoutPageTemplateStructureRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateStructureRel1.getPrimaryKey());
		primaryKeys.add(newLayoutPageTemplateStructureRel2.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateStructureRel>
			layoutPageTemplateStructureRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, layoutPageTemplateStructureRels.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRel1,
			layoutPageTemplateStructureRels.get(
				newLayoutPageTemplateStructureRel1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutPageTemplateStructureRel2,
			layoutPageTemplateStructureRels.get(
				newLayoutPageTemplateStructureRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutPageTemplateStructureRel>
			layoutPageTemplateStructureRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutPageTemplateStructureRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			addLayoutPageTemplateStructureRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateStructureRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutPageTemplateStructureRel>
			layoutPageTemplateStructureRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutPageTemplateStructureRels.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRel,
			layoutPageTemplateStructureRels.get(
				newLayoutPageTemplateStructureRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutPageTemplateStructureRel>
			layoutPageTemplateStructureRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutPageTemplateStructureRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			addLayoutPageTemplateStructureRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutPageTemplateStructureRel.getPrimaryKey());

		Map<Serializable, LayoutPageTemplateStructureRel>
			layoutPageTemplateStructureRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutPageTemplateStructureRels.size());
		Assert.assertEquals(
			newLayoutPageTemplateStructureRel,
			layoutPageTemplateStructureRels.get(
				newLayoutPageTemplateStructureRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutPageTemplateStructureRel newLayoutPageTemplateStructureRel =
			addLayoutPageTemplateStructureRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutPageTemplateStructureRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel) {

		Assert.assertEquals(
			layoutPageTemplateStructureRel.getUuid(),
			ReflectionTestUtil.invoke(
				layoutPageTemplateStructureRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(layoutPageTemplateStructureRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateStructureRel.
					getLayoutPageTemplateStructureId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRel, "getColumnOriginalValue",
				new Class<?>[] {String.class},
				"layoutPageTemplateStructureId"));
		Assert.assertEquals(
			Long.valueOf(
				layoutPageTemplateStructureRel.getSegmentsExperienceId()),
			ReflectionTestUtil.<Long>invoke(
				layoutPageTemplateStructureRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsExperienceId"));
	}

	protected LayoutPageTemplateStructureRel addLayoutPageTemplateStructureRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutPageTemplateStructureRel layoutPageTemplateStructureRel =
			_persistence.create(pk);

		layoutPageTemplateStructureRel.setMvccVersion(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setUuid(RandomTestUtil.randomString());

		layoutPageTemplateStructureRel.setGroupId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setCompanyId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setUserId(RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setUserName(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRel.setCreateDate(RandomTestUtil.nextDate());

		layoutPageTemplateStructureRel.setModifiedDate(
			RandomTestUtil.nextDate());

		layoutPageTemplateStructureRel.setLayoutPageTemplateStructureId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setSegmentsExperienceId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setData(RandomTestUtil.randomString());

		layoutPageTemplateStructureRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		layoutPageTemplateStructureRel.setStatus(RandomTestUtil.nextInt());

		layoutPageTemplateStructureRel.setStatusByUserId(
			RandomTestUtil.nextLong());

		layoutPageTemplateStructureRel.setStatusByUserName(
			RandomTestUtil.randomString());

		layoutPageTemplateStructureRel.setStatusDate(RandomTestUtil.nextDate());

		_layoutPageTemplateStructureRels.add(
			_persistence.update(layoutPageTemplateStructureRel));

		return layoutPageTemplateStructureRel;
	}

	private List<LayoutPageTemplateStructureRel>
		_layoutPageTemplateStructureRels =
			new ArrayList<LayoutPageTemplateStructureRel>();
	private LayoutPageTemplateStructureRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}