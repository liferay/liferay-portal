/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
import com.liferay.segments.exception.NoSuchExperimentRelException;
import com.liferay.segments.model.SegmentsExperimentRel;
import com.liferay.segments.service.persistence.SegmentsExperimentRelPersistence;
import com.liferay.segments.service.persistence.SegmentsExperimentRelUtil;

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
public class SegmentsExperimentRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.segments.service"));

	@Before
	public void setUp() {
		_persistence = SegmentsExperimentRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SegmentsExperimentRel> iterator =
			_segmentsExperimentRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperimentRel segmentsExperimentRel = _persistence.create(pk);

		Assert.assertNotNull(segmentsExperimentRel);

		Assert.assertEquals(segmentsExperimentRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SegmentsExperimentRel newSegmentsExperimentRel =
			addSegmentsExperimentRel();

		_persistence.remove(newSegmentsExperimentRel);

		SegmentsExperimentRel existingSegmentsExperimentRel =
			_persistence.fetchByPrimaryKey(
				newSegmentsExperimentRel.getPrimaryKey());

		Assert.assertNull(existingSegmentsExperimentRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSegmentsExperimentRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperimentRel newSegmentsExperimentRel = _persistence.create(
			pk);

		newSegmentsExperimentRel.setMvccVersion(RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setCtCollectionId(RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setGroupId(RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setCompanyId(RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setUserId(RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setUserName(RandomTestUtil.randomString());

		newSegmentsExperimentRel.setCreateDate(RandomTestUtil.nextDate());

		newSegmentsExperimentRel.setModifiedDate(RandomTestUtil.nextDate());

		newSegmentsExperimentRel.setSegmentsExperimentId(
			RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setSegmentsExperienceId(
			RandomTestUtil.nextLong());

		newSegmentsExperimentRel.setSplit(RandomTestUtil.nextDouble());

		_segmentsExperimentRels.add(
			_persistence.update(newSegmentsExperimentRel));

		SegmentsExperimentRel existingSegmentsExperimentRel =
			_persistence.findByPrimaryKey(
				newSegmentsExperimentRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperimentRel.getMvccVersion(),
			newSegmentsExperimentRel.getMvccVersion());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getCtCollectionId(),
			newSegmentsExperimentRel.getCtCollectionId());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getSegmentsExperimentRelId(),
			newSegmentsExperimentRel.getSegmentsExperimentRelId());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getGroupId(),
			newSegmentsExperimentRel.getGroupId());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getCompanyId(),
			newSegmentsExperimentRel.getCompanyId());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getUserId(),
			newSegmentsExperimentRel.getUserId());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getUserName(),
			newSegmentsExperimentRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSegmentsExperimentRel.getCreateDate()),
			Time.getShortTimestamp(newSegmentsExperimentRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSegmentsExperimentRel.getModifiedDate()),
			Time.getShortTimestamp(newSegmentsExperimentRel.getModifiedDate()));
		Assert.assertEquals(
			existingSegmentsExperimentRel.getSegmentsExperimentId(),
			newSegmentsExperimentRel.getSegmentsExperimentId());
		Assert.assertEquals(
			existingSegmentsExperimentRel.getSegmentsExperienceId(),
			newSegmentsExperimentRel.getSegmentsExperienceId());
		AssertUtils.assertEquals(
			existingSegmentsExperimentRel.getSplit(),
			newSegmentsExperimentRel.getSplit());
	}

	@Test
	public void testCountBySegmentsExperimentId() throws Exception {
		_persistence.countBySegmentsExperimentId(RandomTestUtil.nextLong());

		_persistence.countBySegmentsExperimentId(0L);
	}

	@Test
	public void testCountBySegmentsExperienceId() throws Exception {
		_persistence.countBySegmentsExperienceId(RandomTestUtil.nextLong());

		_persistence.countBySegmentsExperienceId(0L);
	}

	@Test
	public void testCountByS_S() throws Exception {
		_persistence.countByS_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByS_S(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SegmentsExperimentRel newSegmentsExperimentRel =
			addSegmentsExperimentRel();

		SegmentsExperimentRel existingSegmentsExperimentRel =
			_persistence.findByPrimaryKey(
				newSegmentsExperimentRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperimentRel, newSegmentsExperimentRel);
	}

	@Test(expected = NoSuchExperimentRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SegmentsExperimentRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SegmentsExperimentRel", "mvccVersion", true, "ctCollectionId",
			true, "segmentsExperimentRelId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "segmentsExperimentId", true,
			"segmentsExperienceId", true, "split", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SegmentsExperimentRel newSegmentsExperimentRel =
			addSegmentsExperimentRel();

		SegmentsExperimentRel existingSegmentsExperimentRel =
			_persistence.fetchByPrimaryKey(
				newSegmentsExperimentRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperimentRel, newSegmentsExperimentRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperimentRel missingSegmentsExperimentRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSegmentsExperimentRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SegmentsExperimentRel newSegmentsExperimentRel1 =
			addSegmentsExperimentRel();
		SegmentsExperimentRel newSegmentsExperimentRel2 =
			addSegmentsExperimentRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperimentRel1.getPrimaryKey());
		primaryKeys.add(newSegmentsExperimentRel2.getPrimaryKey());

		Map<Serializable, SegmentsExperimentRel> segmentsExperimentRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, segmentsExperimentRels.size());
		Assert.assertEquals(
			newSegmentsExperimentRel1,
			segmentsExperimentRels.get(
				newSegmentsExperimentRel1.getPrimaryKey()));
		Assert.assertEquals(
			newSegmentsExperimentRel2,
			segmentsExperimentRels.get(
				newSegmentsExperimentRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SegmentsExperimentRel> segmentsExperimentRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsExperimentRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SegmentsExperimentRel newSegmentsExperimentRel =
			addSegmentsExperimentRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperimentRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SegmentsExperimentRel> segmentsExperimentRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsExperimentRels.size());
		Assert.assertEquals(
			newSegmentsExperimentRel,
			segmentsExperimentRels.get(
				newSegmentsExperimentRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SegmentsExperimentRel> segmentsExperimentRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsExperimentRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SegmentsExperimentRel newSegmentsExperimentRel =
			addSegmentsExperimentRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperimentRel.getPrimaryKey());

		Map<Serializable, SegmentsExperimentRel> segmentsExperimentRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsExperimentRels.size());
		Assert.assertEquals(
			newSegmentsExperimentRel,
			segmentsExperimentRels.get(
				newSegmentsExperimentRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SegmentsExperimentRel newSegmentsExperimentRel =
			addSegmentsExperimentRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSegmentsExperimentRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		SegmentsExperimentRel segmentsExperimentRel) {

		Assert.assertEquals(
			Long.valueOf(segmentsExperimentRel.getSegmentsExperimentId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperimentRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsExperimentId"));
		Assert.assertEquals(
			Long.valueOf(segmentsExperimentRel.getSegmentsExperienceId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperimentRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsExperienceId"));
	}

	protected SegmentsExperimentRel addSegmentsExperimentRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		SegmentsExperimentRel segmentsExperimentRel = _persistence.create(pk);

		segmentsExperimentRel.setMvccVersion(RandomTestUtil.nextLong());

		segmentsExperimentRel.setCtCollectionId(RandomTestUtil.nextLong());

		segmentsExperimentRel.setGroupId(RandomTestUtil.nextLong());

		segmentsExperimentRel.setCompanyId(RandomTestUtil.nextLong());

		segmentsExperimentRel.setUserId(RandomTestUtil.nextLong());

		segmentsExperimentRel.setUserName(RandomTestUtil.randomString());

		segmentsExperimentRel.setCreateDate(RandomTestUtil.nextDate());

		segmentsExperimentRel.setModifiedDate(RandomTestUtil.nextDate());

		segmentsExperimentRel.setSegmentsExperimentId(
			RandomTestUtil.nextLong());

		segmentsExperimentRel.setSegmentsExperienceId(
			RandomTestUtil.nextLong());

		segmentsExperimentRel.setSplit(RandomTestUtil.nextDouble());

		_segmentsExperimentRels.add(_persistence.update(segmentsExperimentRel));

		return segmentsExperimentRel;
	}

	private List<SegmentsExperimentRel> _segmentsExperimentRels =
		new ArrayList<SegmentsExperimentRel>();
	private SegmentsExperimentRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}