/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.segments.exception.NoSuchEntryRelException;
import com.liferay.segments.model.SegmentsEntryRel;
import com.liferay.segments.service.persistence.SegmentsEntryRelPersistence;
import com.liferay.segments.service.persistence.SegmentsEntryRelUtil;

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
public class SegmentsEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.segments.service"));

	@Before
	public void setUp() {
		_persistence = SegmentsEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SegmentsEntryRel> iterator = _segmentsEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsEntryRel segmentsEntryRel = _persistence.create(pk);

		Assert.assertNotNull(segmentsEntryRel);

		Assert.assertEquals(segmentsEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SegmentsEntryRel newSegmentsEntryRel = addSegmentsEntryRel();

		_persistence.remove(newSegmentsEntryRel);

		SegmentsEntryRel existingSegmentsEntryRel =
			_persistence.fetchByPrimaryKey(newSegmentsEntryRel.getPrimaryKey());

		Assert.assertNull(existingSegmentsEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSegmentsEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsEntryRel newSegmentsEntryRel = _persistence.create(pk);

		newSegmentsEntryRel.setMvccVersion(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setCtCollectionId(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setGroupId(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setCompanyId(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setUserId(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setUserName(RandomTestUtil.randomString());

		newSegmentsEntryRel.setCreateDate(RandomTestUtil.nextDate());

		newSegmentsEntryRel.setModifiedDate(RandomTestUtil.nextDate());

		newSegmentsEntryRel.setSegmentsEntryId(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setClassNameId(RandomTestUtil.nextLong());

		newSegmentsEntryRel.setClassPK(RandomTestUtil.nextLong());

		_segmentsEntryRels.add(_persistence.update(newSegmentsEntryRel));

		SegmentsEntryRel existingSegmentsEntryRel =
			_persistence.findByPrimaryKey(newSegmentsEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsEntryRel.getMvccVersion(),
			newSegmentsEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingSegmentsEntryRel.getCtCollectionId(),
			newSegmentsEntryRel.getCtCollectionId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getSegmentsEntryRelId(),
			newSegmentsEntryRel.getSegmentsEntryRelId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getGroupId(),
			newSegmentsEntryRel.getGroupId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getCompanyId(),
			newSegmentsEntryRel.getCompanyId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getUserId(),
			newSegmentsEntryRel.getUserId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getUserName(),
			newSegmentsEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSegmentsEntryRel.getCreateDate()),
			Time.getShortTimestamp(newSegmentsEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingSegmentsEntryRel.getModifiedDate()),
			Time.getShortTimestamp(newSegmentsEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingSegmentsEntryRel.getSegmentsEntryId(),
			newSegmentsEntryRel.getSegmentsEntryId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getClassNameId(),
			newSegmentsEntryRel.getClassNameId());
		Assert.assertEquals(
			existingSegmentsEntryRel.getClassPK(),
			newSegmentsEntryRel.getClassPK());
	}

	@Test
	public void testCountBySegmentsEntryId() throws Exception {
		_persistence.countBySegmentsEntryId(RandomTestUtil.nextLong());

		_persistence.countBySegmentsEntryId(0L);
	}

	@Test
	public void testCountByCN_CPK() throws Exception {
		_persistence.countByCN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCN_CPK(0L, 0L);
	}

	@Test
	public void testCountByG_CN_CPK() throws Exception {
		_persistence.countByG_CN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByG_CN_CPK(0L, 0L, 0L);
	}

	@Test
	public void testCountByS_CN_CPK() throws Exception {
		_persistence.countByS_CN_CPK(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByS_CN_CPK(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SegmentsEntryRel newSegmentsEntryRel = addSegmentsEntryRel();

		SegmentsEntryRel existingSegmentsEntryRel =
			_persistence.findByPrimaryKey(newSegmentsEntryRel.getPrimaryKey());

		Assert.assertEquals(existingSegmentsEntryRel, newSegmentsEntryRel);
	}

	@Test(expected = NoSuchEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SegmentsEntryRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SegmentsEntryRel", "mvccVersion", true, "ctCollectionId", true,
			"segmentsEntryRelId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "segmentsEntryId", true, "classNameId", true,
			"classPK", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SegmentsEntryRel newSegmentsEntryRel = addSegmentsEntryRel();

		SegmentsEntryRel existingSegmentsEntryRel =
			_persistence.fetchByPrimaryKey(newSegmentsEntryRel.getPrimaryKey());

		Assert.assertEquals(existingSegmentsEntryRel, newSegmentsEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsEntryRel missingSegmentsEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSegmentsEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SegmentsEntryRel newSegmentsEntryRel1 = addSegmentsEntryRel();
		SegmentsEntryRel newSegmentsEntryRel2 = addSegmentsEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsEntryRel1.getPrimaryKey());
		primaryKeys.add(newSegmentsEntryRel2.getPrimaryKey());

		Map<Serializable, SegmentsEntryRel> segmentsEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, segmentsEntryRels.size());
		Assert.assertEquals(
			newSegmentsEntryRel1,
			segmentsEntryRels.get(newSegmentsEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newSegmentsEntryRel2,
			segmentsEntryRels.get(newSegmentsEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SegmentsEntryRel> segmentsEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SegmentsEntryRel newSegmentsEntryRel = addSegmentsEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SegmentsEntryRel> segmentsEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsEntryRels.size());
		Assert.assertEquals(
			newSegmentsEntryRel,
			segmentsEntryRels.get(newSegmentsEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SegmentsEntryRel> segmentsEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SegmentsEntryRel newSegmentsEntryRel = addSegmentsEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsEntryRel.getPrimaryKey());

		Map<Serializable, SegmentsEntryRel> segmentsEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsEntryRels.size());
		Assert.assertEquals(
			newSegmentsEntryRel,
			segmentsEntryRels.get(newSegmentsEntryRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SegmentsEntryRel newSegmentsEntryRel = addSegmentsEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newSegmentsEntryRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(SegmentsEntryRel segmentsEntryRel) {
		Assert.assertEquals(
			Long.valueOf(segmentsEntryRel.getSegmentsEntryId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsEntryId"));
		Assert.assertEquals(
			Long.valueOf(segmentsEntryRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(segmentsEntryRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				segmentsEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected SegmentsEntryRel addSegmentsEntryRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsEntryRel segmentsEntryRel = _persistence.create(pk);

		segmentsEntryRel.setMvccVersion(RandomTestUtil.nextLong());

		segmentsEntryRel.setCtCollectionId(RandomTestUtil.nextLong());

		segmentsEntryRel.setGroupId(RandomTestUtil.nextLong());

		segmentsEntryRel.setCompanyId(RandomTestUtil.nextLong());

		segmentsEntryRel.setUserId(RandomTestUtil.nextLong());

		segmentsEntryRel.setUserName(RandomTestUtil.randomString());

		segmentsEntryRel.setCreateDate(RandomTestUtil.nextDate());

		segmentsEntryRel.setModifiedDate(RandomTestUtil.nextDate());

		segmentsEntryRel.setSegmentsEntryId(RandomTestUtil.nextLong());

		segmentsEntryRel.setClassNameId(RandomTestUtil.nextLong());

		segmentsEntryRel.setClassPK(RandomTestUtil.nextLong());

		_segmentsEntryRels.add(_persistence.update(segmentsEntryRel));

		return segmentsEntryRel;
	}

	private List<SegmentsEntryRel> _segmentsEntryRels =
		new ArrayList<SegmentsEntryRel>();
	private SegmentsEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}