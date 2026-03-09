/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
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
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.segments.exception.DuplicateSegmentsExperienceExternalReferenceCodeException;
import com.liferay.segments.exception.NoSuchExperienceException;
import com.liferay.segments.model.SegmentsExperience;
import com.liferay.segments.service.SegmentsExperienceLocalServiceUtil;
import com.liferay.segments.service.persistence.SegmentsExperiencePersistence;
import com.liferay.segments.service.persistence.SegmentsExperienceUtil;

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
public class SegmentsExperiencePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.segments.service"));

	@Before
	public void setUp() {
		_persistence = SegmentsExperienceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SegmentsExperience> iterator = _segmentsExperiences.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperience segmentsExperience = _persistence.create(pk);

		Assert.assertNotNull(segmentsExperience);

		Assert.assertEquals(segmentsExperience.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		_persistence.remove(newSegmentsExperience);

		SegmentsExperience existingSegmentsExperience =
			_persistence.fetchByPrimaryKey(
				newSegmentsExperience.getPrimaryKey());

		Assert.assertNull(existingSegmentsExperience);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSegmentsExperience();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperience newSegmentsExperience = _persistence.create(pk);

		newSegmentsExperience.setMvccVersion(RandomTestUtil.nextLong());

		newSegmentsExperience.setCtCollectionId(RandomTestUtil.nextLong());

		newSegmentsExperience.setUuid(RandomTestUtil.randomString());

		newSegmentsExperience.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newSegmentsExperience.setGroupId(RandomTestUtil.nextLong());

		newSegmentsExperience.setCompanyId(RandomTestUtil.nextLong());

		newSegmentsExperience.setUserId(RandomTestUtil.nextLong());

		newSegmentsExperience.setUserName(RandomTestUtil.randomString());

		newSegmentsExperience.setCreateDate(RandomTestUtil.nextDate());

		newSegmentsExperience.setModifiedDate(RandomTestUtil.nextDate());

		newSegmentsExperience.setSegmentsEntryERC(
			RandomTestUtil.randomString());

		newSegmentsExperience.setSegmentsEntryScopeERC(
			RandomTestUtil.randomString());

		newSegmentsExperience.setSegmentsExperienceKey(
			RandomTestUtil.randomString());

		newSegmentsExperience.setPlid(RandomTestUtil.nextLong());

		newSegmentsExperience.setName(RandomTestUtil.randomString());

		newSegmentsExperience.setPriority(RandomTestUtil.nextInt());

		newSegmentsExperience.setActive(RandomTestUtil.randomBoolean());

		newSegmentsExperience.setTypeSettings(RandomTestUtil.randomString());

		newSegmentsExperience.setLastPublishDate(RandomTestUtil.nextDate());

		_segmentsExperiences.add(_persistence.update(newSegmentsExperience));

		SegmentsExperience existingSegmentsExperience =
			_persistence.findByPrimaryKey(
				newSegmentsExperience.getPrimaryKey());

		Assert.assertEquals(
			existingSegmentsExperience.getMvccVersion(),
			newSegmentsExperience.getMvccVersion());
		Assert.assertEquals(
			existingSegmentsExperience.getCtCollectionId(),
			newSegmentsExperience.getCtCollectionId());
		Assert.assertEquals(
			existingSegmentsExperience.getUuid(),
			newSegmentsExperience.getUuid());
		Assert.assertEquals(
			existingSegmentsExperience.getExternalReferenceCode(),
			newSegmentsExperience.getExternalReferenceCode());
		Assert.assertEquals(
			existingSegmentsExperience.getSegmentsExperienceId(),
			newSegmentsExperience.getSegmentsExperienceId());
		Assert.assertEquals(
			existingSegmentsExperience.getGroupId(),
			newSegmentsExperience.getGroupId());
		Assert.assertEquals(
			existingSegmentsExperience.getCompanyId(),
			newSegmentsExperience.getCompanyId());
		Assert.assertEquals(
			existingSegmentsExperience.getUserId(),
			newSegmentsExperience.getUserId());
		Assert.assertEquals(
			existingSegmentsExperience.getUserName(),
			newSegmentsExperience.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSegmentsExperience.getCreateDate()),
			Time.getShortTimestamp(newSegmentsExperience.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSegmentsExperience.getModifiedDate()),
			Time.getShortTimestamp(newSegmentsExperience.getModifiedDate()));
		Assert.assertEquals(
			existingSegmentsExperience.getSegmentsEntryERC(),
			newSegmentsExperience.getSegmentsEntryERC());
		Assert.assertEquals(
			existingSegmentsExperience.getSegmentsEntryScopeERC(),
			newSegmentsExperience.getSegmentsEntryScopeERC());
		Assert.assertEquals(
			existingSegmentsExperience.getSegmentsExperienceKey(),
			newSegmentsExperience.getSegmentsExperienceKey());
		Assert.assertEquals(
			existingSegmentsExperience.getPlid(),
			newSegmentsExperience.getPlid());
		Assert.assertEquals(
			existingSegmentsExperience.getName(),
			newSegmentsExperience.getName());
		Assert.assertEquals(
			existingSegmentsExperience.getPriority(),
			newSegmentsExperience.getPriority());
		Assert.assertEquals(
			existingSegmentsExperience.isActive(),
			newSegmentsExperience.isActive());
		Assert.assertEquals(
			existingSegmentsExperience.getTypeSettings(),
			newSegmentsExperience.getTypeSettings());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSegmentsExperience.getLastPublishDate()),
			Time.getShortTimestamp(newSegmentsExperience.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateSegmentsExperienceExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		SegmentsExperience segmentsExperience = addSegmentsExperience();

		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		newSegmentsExperience.setGroupId(segmentsExperience.getGroupId());

		newSegmentsExperience = _persistence.update(newSegmentsExperience);

		Session session = _persistence.getCurrentSession();

		session.evict(newSegmentsExperience);

		newSegmentsExperience.setExternalReferenceCode(
			segmentsExperience.getExternalReferenceCode());

		_persistence.update(newSegmentsExperience);
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
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_AArrayable() throws Exception {
		_persistence.countByG_A(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountBySEERC_SESERC() throws Exception {
		_persistence.countBySEERC_SESERC("", "");

		_persistence.countBySEERC_SESERC("null", "null");

		_persistence.countBySEERC_SESERC((String)null, (String)null);
	}

	@Test
	public void testCountByG_SEERC_SESERC() throws Exception {
		_persistence.countByG_SEERC_SESERC(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_SEERC_SESERC(0L, "null", "null");

		_persistence.countByG_SEERC_SESERC(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_SEK_P() throws Exception {
		_persistence.countByG_SEK_P(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong());

		_persistence.countByG_SEK_P(0L, "null", 0L);

		_persistence.countByG_SEK_P(0L, (String)null, 0L);
	}

	@Test
	public void testCountByG_P_P() throws Exception {
		_persistence.countByG_P_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_P(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_GtP() throws Exception {
		_persistence.countByG_P_GtP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_GtP(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_LtP() throws Exception {
		_persistence.countByG_P_LtP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_LtP(0L, 0L, 0);
	}

	@Test
	public void testCountByG_P_A() throws Exception {
		_persistence.countByG_P_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_P_A(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_SEERC_SESERC_P() throws Exception {
		_persistence.countByG_SEERC_SESERC_P(
			RandomTestUtil.nextLong(), "", "", RandomTestUtil.nextLong());

		_persistence.countByG_SEERC_SESERC_P(0L, "null", "null", 0L);

		_persistence.countByG_SEERC_SESERC_P(
			0L, (String)null, (String)null, 0L);
	}

	@Test
	public void testCountByG_SEERC_SESERC_P_A() throws Exception {
		_persistence.countByG_SEERC_SESERC_P_A(
			RandomTestUtil.nextLong(), "", "", RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_SEERC_SESERC_P_A(
			0L, "null", "null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByG_SEERC_SESERC_P_A(
			0L, (String)null, (String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_SEERC_SESERC_P_AArrayable() throws Exception {
		_persistence.countByG_SEERC_SESERC_P_A(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			},
			RandomTestUtil.randomString(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		SegmentsExperience existingSegmentsExperience =
			_persistence.findByPrimaryKey(
				newSegmentsExperience.getPrimaryKey());

		Assert.assertEquals(existingSegmentsExperience, newSegmentsExperience);
	}

	@Test(expected = NoSuchExperienceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SegmentsExperience> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SegmentsExperience", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "segmentsExperienceId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"segmentsEntryERC", true, "segmentsEntryScopeERC", true,
			"segmentsExperienceKey", true, "plid", true, "name", true,
			"priority", true, "active", true, "typeSettings", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		SegmentsExperience existingSegmentsExperience =
			_persistence.fetchByPrimaryKey(
				newSegmentsExperience.getPrimaryKey());

		Assert.assertEquals(existingSegmentsExperience, newSegmentsExperience);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperience missingSegmentsExperience =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSegmentsExperience);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SegmentsExperience newSegmentsExperience1 = addSegmentsExperience();
		SegmentsExperience newSegmentsExperience2 = addSegmentsExperience();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperience1.getPrimaryKey());
		primaryKeys.add(newSegmentsExperience2.getPrimaryKey());

		Map<Serializable, SegmentsExperience> segmentsExperiences =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, segmentsExperiences.size());
		Assert.assertEquals(
			newSegmentsExperience1,
			segmentsExperiences.get(newSegmentsExperience1.getPrimaryKey()));
		Assert.assertEquals(
			newSegmentsExperience2,
			segmentsExperiences.get(newSegmentsExperience2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SegmentsExperience> segmentsExperiences =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsExperiences.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperience.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SegmentsExperience> segmentsExperiences =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsExperiences.size());
		Assert.assertEquals(
			newSegmentsExperience,
			segmentsExperiences.get(newSegmentsExperience.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SegmentsExperience> segmentsExperiences =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(segmentsExperiences.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSegmentsExperience.getPrimaryKey());

		Map<Serializable, SegmentsExperience> segmentsExperiences =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, segmentsExperiences.size());
		Assert.assertEquals(
			newSegmentsExperience,
			segmentsExperiences.get(newSegmentsExperience.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SegmentsExperienceLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SegmentsExperience>() {

				@Override
				public void performAction(
					SegmentsExperience segmentsExperience) {

					Assert.assertNotNull(segmentsExperience);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperience.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"segmentsExperienceId",
				newSegmentsExperience.getSegmentsExperienceId()));

		List<SegmentsExperience> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SegmentsExperience existingSegmentsExperience = result.get(0);

		Assert.assertEquals(existingSegmentsExperience, newSegmentsExperience);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperience.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"segmentsExperienceId", RandomTestUtil.nextLong()));

		List<SegmentsExperience> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperience.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("segmentsExperienceId"));

		Object newSegmentsExperienceId =
			newSegmentsExperience.getSegmentsExperienceId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"segmentsExperienceId",
				new Object[] {newSegmentsExperienceId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSegmentsExperienceId = result.get(0);

		Assert.assertEquals(
			existingSegmentsExperienceId, newSegmentsExperienceId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperience.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("segmentsExperienceId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"segmentsExperienceId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSegmentsExperience.getPrimaryKey()));
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

		SegmentsExperience newSegmentsExperience = addSegmentsExperience();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SegmentsExperience.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"segmentsExperienceId",
				newSegmentsExperience.getSegmentsExperienceId()));

		List<SegmentsExperience> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(SegmentsExperience segmentsExperience) {
		Assert.assertEquals(
			segmentsExperience.getUuid(),
			ReflectionTestUtil.invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(segmentsExperience.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(segmentsExperience.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			segmentsExperience.getSegmentsExperienceKey(),
			ReflectionTestUtil.invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "segmentsExperienceKey"));
		Assert.assertEquals(
			Long.valueOf(segmentsExperience.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));

		Assert.assertEquals(
			Long.valueOf(segmentsExperience.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(segmentsExperience.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
		Assert.assertEquals(
			Integer.valueOf(segmentsExperience.getPriority()),
			ReflectionTestUtil.<Integer>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "priority"));

		Assert.assertEquals(
			segmentsExperience.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(segmentsExperience.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				segmentsExperience, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected SegmentsExperience addSegmentsExperience() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SegmentsExperience segmentsExperience = _persistence.create(pk);

		segmentsExperience.setMvccVersion(RandomTestUtil.nextLong());

		segmentsExperience.setCtCollectionId(RandomTestUtil.nextLong());

		segmentsExperience.setUuid(RandomTestUtil.randomString());

		segmentsExperience.setExternalReferenceCode(
			RandomTestUtil.randomString());

		segmentsExperience.setGroupId(RandomTestUtil.nextLong());

		segmentsExperience.setCompanyId(RandomTestUtil.nextLong());

		segmentsExperience.setUserId(RandomTestUtil.nextLong());

		segmentsExperience.setUserName(RandomTestUtil.randomString());

		segmentsExperience.setCreateDate(RandomTestUtil.nextDate());

		segmentsExperience.setModifiedDate(RandomTestUtil.nextDate());

		segmentsExperience.setSegmentsEntryERC(RandomTestUtil.randomString());

		segmentsExperience.setSegmentsEntryScopeERC(
			RandomTestUtil.randomString());

		segmentsExperience.setSegmentsExperienceKey(
			RandomTestUtil.randomString());

		segmentsExperience.setPlid(RandomTestUtil.nextLong());

		segmentsExperience.setName(RandomTestUtil.randomString());

		segmentsExperience.setPriority(RandomTestUtil.nextInt());

		segmentsExperience.setActive(RandomTestUtil.randomBoolean());

		segmentsExperience.setTypeSettings(RandomTestUtil.randomString());

		segmentsExperience.setLastPublishDate(RandomTestUtil.nextDate());

		_segmentsExperiences.add(_persistence.update(segmentsExperience));

		return segmentsExperience;
	}

	private List<SegmentsExperience> _segmentsExperiences =
		new ArrayList<SegmentsExperience>();
	private SegmentsExperiencePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}