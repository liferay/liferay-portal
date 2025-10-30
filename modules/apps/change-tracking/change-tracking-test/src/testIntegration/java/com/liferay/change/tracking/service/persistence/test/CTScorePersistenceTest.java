/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchScoreException;
import com.liferay.change.tracking.model.CTScore;
import com.liferay.change.tracking.service.persistence.CTScorePersistence;
import com.liferay.change.tracking.service.persistence.CTScoreUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class CTScorePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTScoreUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTScore> iterator = _ctScores.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTScore ctScore = _persistence.create(pk);

		Assert.assertNotNull(ctScore);

		Assert.assertEquals(ctScore.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTScore newCTScore = addCTScore();

		_persistence.remove(newCTScore);

		CTScore existingCTScore = _persistence.fetchByPrimaryKey(
			newCTScore.getPrimaryKey());

		Assert.assertNull(existingCTScore);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTScore();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTScore newCTScore = _persistence.create(pk);

		newCTScore.setMvccVersion(RandomTestUtil.nextLong());

		newCTScore.setCompanyId(RandomTestUtil.nextLong());

		newCTScore.setCtCollectionId(RandomTestUtil.nextLong());

		newCTScore.setScore(RandomTestUtil.nextInt());

		_ctScores.add(_persistence.update(newCTScore));

		CTScore existingCTScore = _persistence.findByPrimaryKey(
			newCTScore.getPrimaryKey());

		Assert.assertEquals(
			existingCTScore.getMvccVersion(), newCTScore.getMvccVersion());
		Assert.assertEquals(
			existingCTScore.getCtScoreId(), newCTScore.getCtScoreId());
		Assert.assertEquals(
			existingCTScore.getCompanyId(), newCTScore.getCompanyId());
		Assert.assertEquals(
			existingCTScore.getCtCollectionId(),
			newCTScore.getCtCollectionId());
		Assert.assertEquals(existingCTScore.getScore(), newCTScore.getScore());
	}

	@Test
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTScore newCTScore = addCTScore();

		CTScore existingCTScore = _persistence.findByPrimaryKey(
			newCTScore.getPrimaryKey());

		Assert.assertEquals(existingCTScore, newCTScore);
	}

	@Test(expected = NoSuchScoreException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTScore> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTScore", "mvccVersion", true, "ctScoreId", true, "companyId",
			true, "ctCollectionId", true, "score", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTScore newCTScore = addCTScore();

		CTScore existingCTScore = _persistence.fetchByPrimaryKey(
			newCTScore.getPrimaryKey());

		Assert.assertEquals(existingCTScore, newCTScore);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTScore missingCTScore = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTScore);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTScore newCTScore1 = addCTScore();
		CTScore newCTScore2 = addCTScore();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTScore1.getPrimaryKey());
		primaryKeys.add(newCTScore2.getPrimaryKey());

		Map<Serializable, CTScore> ctScores = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, ctScores.size());
		Assert.assertEquals(
			newCTScore1, ctScores.get(newCTScore1.getPrimaryKey()));
		Assert.assertEquals(
			newCTScore2, ctScores.get(newCTScore2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTScore> ctScores = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctScores.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTScore newCTScore = addCTScore();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTScore.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTScore> ctScores = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctScores.size());
		Assert.assertEquals(
			newCTScore, ctScores.get(newCTScore.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTScore> ctScores = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(ctScores.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTScore newCTScore = addCTScore();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTScore.getPrimaryKey());

		Map<Serializable, CTScore> ctScores = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, ctScores.size());
		Assert.assertEquals(
			newCTScore, ctScores.get(newCTScore.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CTScore newCTScore = addCTScore();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCTScore.getPrimaryKey()));
	}

	private void _assertOriginalValues(CTScore ctScore) {
		Assert.assertEquals(
			Long.valueOf(ctScore.getCtCollectionId()),
			ReflectionTestUtil.<Long>invoke(
				ctScore, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ctCollectionId"));
	}

	protected CTScore addCTScore() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTScore ctScore = _persistence.create(pk);

		ctScore.setMvccVersion(RandomTestUtil.nextLong());

		ctScore.setCompanyId(RandomTestUtil.nextLong());

		ctScore.setCtCollectionId(RandomTestUtil.nextLong());

		ctScore.setScore(RandomTestUtil.nextInt());

		_ctScores.add(_persistence.update(ctScore));

		return ctScore;
	}

	private List<CTScore> _ctScores = new ArrayList<CTScore>();
	private CTScorePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}