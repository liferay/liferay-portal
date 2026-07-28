/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.ratings.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
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
import com.liferay.ratings.kernel.exception.NoSuchStatsException;
import com.liferay.ratings.kernel.model.RatingsStats;
import com.liferay.ratings.kernel.service.RatingsStatsLocalServiceUtil;
import com.liferay.ratings.kernel.service.persistence.RatingsStatsPersistence;
import com.liferay.ratings.kernel.service.persistence.RatingsStatsUtil;

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
public class RatingsStatsPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = RatingsStatsUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<RatingsStats> iterator = _ratingsStatses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsStats ratingsStats = _persistence.create(pk);

		Assert.assertNotNull(ratingsStats);

		Assert.assertEquals(ratingsStats.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		_persistence.remove(newRatingsStats);

		RatingsStats existingRatingsStats = _persistence.fetchByPrimaryKey(
			newRatingsStats.getPrimaryKey());

		Assert.assertNull(existingRatingsStats);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addRatingsStats();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		newRatingsStats.setCtCollectionId(RandomTestUtil.nextLong());

		newRatingsStats.setCompanyId(RandomTestUtil.nextLong());

		newRatingsStats.setCreateDate(RandomTestUtil.nextDate());

		newRatingsStats.setModifiedDate(RandomTestUtil.nextDate());

		newRatingsStats.setClassNameId(RandomTestUtil.nextLong());

		newRatingsStats.setClassPK(RandomTestUtil.nextLong());

		newRatingsStats.setTotalEntries(RandomTestUtil.nextInt());

		newRatingsStats.setTotalScore(RandomTestUtil.nextDouble());

		newRatingsStats.setAverageScore(RandomTestUtil.nextDouble());

		newRatingsStats = _persistence.update(newRatingsStats);

		_ratingsStatses.add(newRatingsStats);

		RatingsStats existingRatingsStats = _persistence.findByPrimaryKey(
			newRatingsStats.getPrimaryKey());

		Assert.assertEquals(
			existingRatingsStats.getMvccVersion(),
			newRatingsStats.getMvccVersion());
		Assert.assertEquals(
			existingRatingsStats.getCtCollectionId(),
			newRatingsStats.getCtCollectionId());
		Assert.assertEquals(
			existingRatingsStats.getStatsId(), newRatingsStats.getStatsId());
		Assert.assertEquals(
			existingRatingsStats.getCompanyId(),
			newRatingsStats.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingRatingsStats.getCreateDate()),
			Time.getShortTimestamp(newRatingsStats.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingRatingsStats.getModifiedDate()),
			Time.getShortTimestamp(newRatingsStats.getModifiedDate()));
		Assert.assertEquals(
			existingRatingsStats.getClassNameId(),
			newRatingsStats.getClassNameId());
		Assert.assertEquals(
			existingRatingsStats.getClassPK(), newRatingsStats.getClassPK());
		Assert.assertEquals(
			existingRatingsStats.getTotalEntries(),
			newRatingsStats.getTotalEntries());
		AssertUtils.assertEquals(
			existingRatingsStats.getTotalScore(),
			newRatingsStats.getTotalScore());
		AssertUtils.assertEquals(
			existingRatingsStats.getAverageScore(),
			newRatingsStats.getAverageScore());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_CArrayable() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		RatingsStats existingRatingsStats = _persistence.findByPrimaryKey(
			newRatingsStats.getPrimaryKey());

		Assert.assertEquals(existingRatingsStats, newRatingsStats);
	}

	@Test(expected = NoSuchStatsException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<RatingsStats> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"RatingsStats", "mvccVersion", true, "ctCollectionId", true,
			"statsId", true, "companyId", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"totalEntries", true, "totalScore", true, "averageScore", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		RatingsStats existingRatingsStats = _persistence.fetchByPrimaryKey(
			newRatingsStats.getPrimaryKey());

		Assert.assertEquals(existingRatingsStats, newRatingsStats);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsStats missingRatingsStats = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingRatingsStats);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		RatingsStats newRatingsStats1 = addRatingsStats();
		RatingsStats newRatingsStats2 = addRatingsStats();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRatingsStats1.getPrimaryKey());
		primaryKeys.add(newRatingsStats2.getPrimaryKey());

		Map<Serializable, RatingsStats> ratingsStatses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ratingsStatses.size());
		Assert.assertEquals(
			newRatingsStats1,
			ratingsStatses.get(newRatingsStats1.getPrimaryKey()));
		Assert.assertEquals(
			newRatingsStats2,
			ratingsStatses.get(newRatingsStats2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, RatingsStats> ratingsStatses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ratingsStatses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		RatingsStats newRatingsStats = addRatingsStats();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRatingsStats.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, RatingsStats> ratingsStatses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ratingsStatses.size());
		Assert.assertEquals(
			newRatingsStats,
			ratingsStatses.get(newRatingsStats.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, RatingsStats> ratingsStatses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ratingsStatses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newRatingsStats.getPrimaryKey());

		Map<Serializable, RatingsStats> ratingsStatses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ratingsStatses.size());
		Assert.assertEquals(
			newRatingsStats,
			ratingsStatses.get(newRatingsStats.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			RatingsStatsLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<RatingsStats>() {

				@Override
				public void performAction(RatingsStats ratingsStats) {
					Assert.assertNotNull(ratingsStats);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			RatingsStats.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"statsId", newRatingsStats.getStatsId()));

		List<RatingsStats> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		RatingsStats existingRatingsStats = result.get(0);

		Assert.assertEquals(existingRatingsStats, newRatingsStats);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			RatingsStats.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("statsId", RandomTestUtil.nextLong()));

		List<RatingsStats> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			RatingsStats.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("statsId"));

		Object newStatsId = newRatingsStats.getStatsId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("statsId", new Object[] {newStatsId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingStatsId = result.get(0);

		Assert.assertEquals(existingStatsId, newStatsId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			RatingsStats.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("statsId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"statsId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		RatingsStats newRatingsStats = addRatingsStats();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newRatingsStats.getPrimaryKey()));
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

		RatingsStats newRatingsStats = addRatingsStats();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			RatingsStats.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"statsId", newRatingsStats.getStatsId()));

		List<RatingsStats> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(RatingsStats ratingsStats) {
		Assert.assertEquals(
			Long.valueOf(ratingsStats.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ratingsStats, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(ratingsStats.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				ratingsStats, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
	}

	protected RatingsStats addRatingsStats() throws Exception {
		long pk = RandomTestUtil.nextLong();

		RatingsStats ratingsStats = _persistence.create(pk);

		ratingsStats.setCtCollectionId(RandomTestUtil.nextLong());

		ratingsStats.setCompanyId(RandomTestUtil.nextLong());

		ratingsStats.setCreateDate(RandomTestUtil.nextDate());

		ratingsStats.setModifiedDate(RandomTestUtil.nextDate());

		ratingsStats.setClassNameId(RandomTestUtil.nextLong());

		ratingsStats.setClassPK(RandomTestUtil.nextLong());

		ratingsStats.setTotalEntries(RandomTestUtil.nextInt());

		ratingsStats.setTotalScore(RandomTestUtil.nextDouble());

		ratingsStats.setAverageScore(RandomTestUtil.nextDouble());

		_ratingsStatses.add(_persistence.update(ratingsStats));

		return ratingsStats;
	}

	private List<RatingsStats> _ratingsStatses = new ArrayList<RatingsStats>();
	private RatingsStatsPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1319246282