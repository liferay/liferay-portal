/**
 * SPDX-FileCopyrightText: (c) 2026 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.segments.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
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
import com.liferay.segments.exception.NoSuchExperienceAudienceEntryRelException;
import com.liferay.segments.model.SegmentsExperienceAudienceEntryRel;
import com.liferay.segments.service.SegmentsExperienceAudienceEntryRelLocalServiceUtil;
import com.liferay.segments.service.persistence.SegmentsExperienceAudienceEntryRelPersistence;
import com.liferay.segments.service.persistence.SegmentsExperienceAudienceEntryRelUtil;

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
public class SegmentsExperienceAudienceEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.segments.service"));

	@Before
	public void setUp() {
		_persistence = SegmentsExperienceAudienceEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SegmentsExperienceAudienceEntryRel> iterator =
			_segmentsExperienceAudienceEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel =
			_persistence.create(pk);

		Assert.assertNotNull(segmentsExperienceAudienceEntryRel);

		Assert.assertEquals(
			segmentsExperienceAudienceEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		_persistence.remove(newSegmentsExperienceAudienceEntryRel);

		SegmentsExperienceAudienceEntryRel
			existingSegmentsExperienceAudienceEntryRel =
				_persistence.fetchByPrimaryKey(
					newSegmentsExperienceAudienceEntryRel.getPrimaryKey());

		Assert.assertNull(existingSegmentsExperienceAudienceEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSegmentsExperienceAudienceEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		newSegmentsExperienceAudienceEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newSegmentsExperienceAudienceEntryRel.setUuid(
			RandomTestUtil.randomString());

		newSegmentsExperienceAudienceEntryRel.setGroupId(
			RandomTestUtil.nextLong());

		newSegmentsExperienceAudienceEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		newSegmentsExperienceAudienceEntryRel.setUserId(
			RandomTestUtil.nextLong());

		newSegmentsExperienceAudienceEntryRel.setUserName(
			RandomTestUtil.randomString());

		newSegmentsExperienceAudienceEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		newSegmentsExperienceAudienceEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newSegmentsExperienceAudienceEntryRel.setAudienceEntryERC(
			RandomTestUtil.randomString());

		newSegmentsExperienceAudienceEntryRel.setPriority(
			RandomTestUtil.nextInt());

		newSegmentsExperienceAudienceEntryRel.setSegmentsExperienceERC(
			RandomTestUtil.randomString());

		newSegmentsExperienceAudienceEntryRel = _persistence.update(
			newSegmentsExperienceAudienceEntryRel);

		_segmentsExperienceAudienceEntryRels.add(
			newSegmentsExperienceAudienceEntryRel);

		SegmentsExperienceAudienceEntryRel
			existingSegmentsExperienceAudienceEntryRel =
				_persistence.findByPrimaryKey(
					newSegmentsExperienceAudienceEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getMvccVersion(),
			newSegmentsExperienceAudienceEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getCtCollectionId(),
			newSegmentsExperienceAudienceEntryRel.getCtCollectionId());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getUuid(),
			newSegmentsExperienceAudienceEntryRel.getUuid());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.
				getSegmentsExperienceAudienceEntryRelId(),
			newSegmentsExperienceAudienceEntryRel.
				getSegmentsExperienceAudienceEntryRelId());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getGroupId(),
			newSegmentsExperienceAudienceEntryRel.getGroupId());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getCompanyId(),
			newSegmentsExperienceAudienceEntryRel.getCompanyId());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getUserId(),
			newSegmentsExperienceAudienceEntryRel.getUserId());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getUserName(),
			newSegmentsExperienceAudienceEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSegmentsExperienceAudienceEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newSegmentsExperienceAudienceEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSegmentsExperienceAudienceEntryRel.getModifiedDate()),
			Time.getShortTimestamp(
				newSegmentsExperienceAudienceEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getAudienceEntryERC(),
			newSegmentsExperienceAudienceEntryRel.getAudienceEntryERC());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.getPriority(),
			newSegmentsExperienceAudienceEntryRel.getPriority());
		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel.
				getSegmentsExperienceERC(),
			newSegmentsExperienceAudienceEntryRel.getSegmentsExperienceERC());
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
	public void testCountByG_SEERC() throws Exception {
		_persistence.countByG_SEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByG_SEERC(0L, "null");

		_persistence.countByG_SEERC(0L, (String)null);
	}

	@Test
	public void testCountByC_AEERC() throws Exception {
		_persistence.countByC_AEERC(RandomTestUtil.nextLong(), "");

		_persistence.countByC_AEERC(0L, "null");

		_persistence.countByC_AEERC(0L, (String)null);
	}

	@Test
	public void testCountByG_AEERC_SEERC() throws Exception {
		_persistence.countByG_AEERC_SEERC(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_AEERC_SEERC(0L, "null", "null");

		_persistence.countByG_AEERC_SEERC(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		SegmentsExperienceAudienceEntryRel
			existingSegmentsExperienceAudienceEntryRel =
				_persistence.findByPrimaryKey(
					newSegmentsExperienceAudienceEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel,
			newSegmentsExperienceAudienceEntryRel);
	}

	@Test(expected = NoSuchExperienceAudienceEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SegmentsExperienceAudienceEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"SExperienceAudienceEntryRel", "mvccVersion", true,
			"ctCollectionId", true, "uuid", true,
			"segmentsExperienceAudienceEntryRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "audienceEntryERC", true, "priority",
			true, "segmentsExperienceERC", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		SegmentsExperienceAudienceEntryRel
			existingSegmentsExperienceAudienceEntryRel =
				_persistence.fetchByPrimaryKey(
					newSegmentsExperienceAudienceEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel,
			newSegmentsExperienceAudienceEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperienceAudienceEntryRel
			missingSegmentsExperienceAudienceEntryRel =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSegmentsExperienceAudienceEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel1 =
				addSegmentsExperienceAudienceEntryRel();
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel2 =
				addSegmentsExperienceAudienceEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperienceAudienceEntryRel1.getPrimaryKey());
		primaryKeys.add(newSegmentsExperienceAudienceEntryRel2.getPrimaryKey());

		Map<Serializable, SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, segmentsExperienceAudienceEntryRels.size());
		Assert.assertEquals(
			newSegmentsExperienceAudienceEntryRel1,
			segmentsExperienceAudienceEntryRels.get(
				newSegmentsExperienceAudienceEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newSegmentsExperienceAudienceEntryRel2,
			segmentsExperienceAudienceEntryRels.get(
				newSegmentsExperienceAudienceEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsExperienceAudienceEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperienceAudienceEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsExperienceAudienceEntryRels.size());
		Assert.assertEquals(
			newSegmentsExperienceAudienceEntryRel,
			segmentsExperienceAudienceEntryRels.get(
				newSegmentsExperienceAudienceEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsExperienceAudienceEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperienceAudienceEntryRel.getPrimaryKey());

		Map<Serializable, SegmentsExperienceAudienceEntryRel>
			segmentsExperienceAudienceEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsExperienceAudienceEntryRels.size());
		Assert.assertEquals(
			newSegmentsExperienceAudienceEntryRel,
			segmentsExperienceAudienceEntryRels.get(
				newSegmentsExperienceAudienceEntryRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SegmentsExperienceAudienceEntryRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SegmentsExperienceAudienceEntryRel>() {

				@Override
				public void performAction(
					SegmentsExperienceAudienceEntryRel
						segmentsExperienceAudienceEntryRel) {

					Assert.assertNotNull(segmentsExperienceAudienceEntryRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperienceAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"segmentsExperienceAudienceEntryRelId",
				newSegmentsExperienceAudienceEntryRel.
					getSegmentsExperienceAudienceEntryRelId()));

		List<SegmentsExperienceAudienceEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		SegmentsExperienceAudienceEntryRel
			existingSegmentsExperienceAudienceEntryRel = result.get(0);

		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRel,
			newSegmentsExperienceAudienceEntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperienceAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"segmentsExperienceAudienceEntryRelId",
				RandomTestUtil.nextLong()));

		List<SegmentsExperienceAudienceEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperienceAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"segmentsExperienceAudienceEntryRelId"));

		Object newSegmentsExperienceAudienceEntryRelId =
			newSegmentsExperienceAudienceEntryRel.
				getSegmentsExperienceAudienceEntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"segmentsExperienceAudienceEntryRelId",
				new Object[] {newSegmentsExperienceAudienceEntryRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSegmentsExperienceAudienceEntryRelId = result.get(0);

		Assert.assertEquals(
			existingSegmentsExperienceAudienceEntryRelId,
			newSegmentsExperienceAudienceEntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperienceAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"segmentsExperienceAudienceEntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"segmentsExperienceAudienceEntryRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSegmentsExperienceAudienceEntryRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		SegmentsExperienceAudienceEntryRel
			newSegmentsExperienceAudienceEntryRel =
				addSegmentsExperienceAudienceEntryRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperienceAudienceEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"segmentsExperienceAudienceEntryRelId",
				newSegmentsExperienceAudienceEntryRel.
					getSegmentsExperienceAudienceEntryRelId()));

		List<SegmentsExperienceAudienceEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel) {

		Assert.assertEquals(
			segmentsExperienceAudienceEntryRel.getUuid(),
			ReflectionTestUtil.invoke(
				segmentsExperienceAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(segmentsExperienceAudienceEntryRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperienceAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(segmentsExperienceAudienceEntryRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperienceAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			segmentsExperienceAudienceEntryRel.getAudienceEntryERC(),
			ReflectionTestUtil.invoke(
				segmentsExperienceAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "audienceEntryERC"));
		Assert.assertEquals(
			segmentsExperienceAudienceEntryRel.getSegmentsExperienceERC(),
			ReflectionTestUtil.invoke(
				segmentsExperienceAudienceEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsExperienceERC"));
	}

	protected SegmentsExperienceAudienceEntryRel
			addSegmentsExperienceAudienceEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		SegmentsExperienceAudienceEntryRel segmentsExperienceAudienceEntryRel =
			_persistence.create(pk);

		segmentsExperienceAudienceEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		segmentsExperienceAudienceEntryRel.setUuid(
			RandomTestUtil.randomString());

		segmentsExperienceAudienceEntryRel.setGroupId(
			RandomTestUtil.nextLong());

		segmentsExperienceAudienceEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		segmentsExperienceAudienceEntryRel.setUserId(RandomTestUtil.nextLong());

		segmentsExperienceAudienceEntryRel.setUserName(
			RandomTestUtil.randomString());

		segmentsExperienceAudienceEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		segmentsExperienceAudienceEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		segmentsExperienceAudienceEntryRel.setAudienceEntryERC(
			RandomTestUtil.randomString());

		segmentsExperienceAudienceEntryRel.setPriority(
			RandomTestUtil.nextInt());

		segmentsExperienceAudienceEntryRel.setSegmentsExperienceERC(
			RandomTestUtil.randomString());

		_segmentsExperienceAudienceEntryRels.add(
			_persistence.update(segmentsExperienceAudienceEntryRel));

		return segmentsExperienceAudienceEntryRel;
	}

	private List<SegmentsExperienceAudienceEntryRel>
		_segmentsExperienceAudienceEntryRels =
			new ArrayList<SegmentsExperienceAudienceEntryRel>();
	private SegmentsExperienceAudienceEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1105885472