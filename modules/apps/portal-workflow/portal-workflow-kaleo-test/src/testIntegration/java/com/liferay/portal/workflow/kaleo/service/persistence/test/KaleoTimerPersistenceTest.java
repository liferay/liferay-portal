/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.workflow.kaleo.exception.NoSuchTimerException;
import com.liferay.portal.workflow.kaleo.model.KaleoTimer;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTimerPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTimerUtil;

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
public class KaleoTimerPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.workflow.kaleo.service"));

	@Before
	public void setUp() {
		_persistence = KaleoTimerUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoTimer> iterator = _kaleoTimers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimer kaleoTimer = _persistence.create(pk);

		Assert.assertNotNull(kaleoTimer);

		Assert.assertEquals(kaleoTimer.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoTimer newKaleoTimer = addKaleoTimer();

		_persistence.remove(newKaleoTimer);

		KaleoTimer existingKaleoTimer = _persistence.fetchByPrimaryKey(
			newKaleoTimer.getPrimaryKey());

		Assert.assertNull(existingKaleoTimer);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoTimer();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimer newKaleoTimer = _persistence.create(pk);

		newKaleoTimer.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoTimer.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoTimer.setGroupId(RandomTestUtil.nextLong());

		newKaleoTimer.setCompanyId(RandomTestUtil.nextLong());

		newKaleoTimer.setUserId(RandomTestUtil.nextLong());

		newKaleoTimer.setUserName(RandomTestUtil.randomString());

		newKaleoTimer.setCreateDate(RandomTestUtil.nextDate());

		newKaleoTimer.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoTimer.setKaleoClassName(RandomTestUtil.randomString());

		newKaleoTimer.setKaleoClassPK(RandomTestUtil.nextLong());

		newKaleoTimer.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoTimer.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		newKaleoTimer.setName(RandomTestUtil.randomString());

		newKaleoTimer.setBlocking(RandomTestUtil.randomBoolean());

		newKaleoTimer.setDescription(RandomTestUtil.randomString());

		newKaleoTimer.setDuration(RandomTestUtil.nextDouble());

		newKaleoTimer.setScale(RandomTestUtil.randomString());

		newKaleoTimer.setRecurrenceDuration(RandomTestUtil.nextDouble());

		newKaleoTimer.setRecurrenceScale(RandomTestUtil.randomString());

		_kaleoTimers.add(_persistence.update(newKaleoTimer));

		KaleoTimer existingKaleoTimer = _persistence.findByPrimaryKey(
			newKaleoTimer.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTimer.getMvccVersion(),
			newKaleoTimer.getMvccVersion());
		Assert.assertEquals(
			existingKaleoTimer.getCtCollectionId(),
			newKaleoTimer.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoTimer.getKaleoTimerId(),
			newKaleoTimer.getKaleoTimerId());
		Assert.assertEquals(
			existingKaleoTimer.getGroupId(), newKaleoTimer.getGroupId());
		Assert.assertEquals(
			existingKaleoTimer.getCompanyId(), newKaleoTimer.getCompanyId());
		Assert.assertEquals(
			existingKaleoTimer.getUserId(), newKaleoTimer.getUserId());
		Assert.assertEquals(
			existingKaleoTimer.getUserName(), newKaleoTimer.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoTimer.getCreateDate()),
			Time.getShortTimestamp(newKaleoTimer.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoTimer.getModifiedDate()),
			Time.getShortTimestamp(newKaleoTimer.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoTimer.getKaleoClassName(),
			newKaleoTimer.getKaleoClassName());
		Assert.assertEquals(
			existingKaleoTimer.getKaleoClassPK(),
			newKaleoTimer.getKaleoClassPK());
		Assert.assertEquals(
			existingKaleoTimer.getKaleoDefinitionId(),
			newKaleoTimer.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoTimer.getKaleoDefinitionVersionId(),
			newKaleoTimer.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoTimer.getName(), newKaleoTimer.getName());
		Assert.assertEquals(
			existingKaleoTimer.isBlocking(), newKaleoTimer.isBlocking());
		Assert.assertEquals(
			existingKaleoTimer.getDescription(),
			newKaleoTimer.getDescription());
		AssertUtils.assertEquals(
			existingKaleoTimer.getDuration(), newKaleoTimer.getDuration());
		Assert.assertEquals(
			existingKaleoTimer.getScale(), newKaleoTimer.getScale());
		AssertUtils.assertEquals(
			existingKaleoTimer.getRecurrenceDuration(),
			newKaleoTimer.getRecurrenceDuration());
		Assert.assertEquals(
			existingKaleoTimer.getRecurrenceScale(),
			newKaleoTimer.getRecurrenceScale());
	}

	@Test
	public void testCountByKCN_KCPK() throws Exception {
		_persistence.countByKCN_KCPK("", RandomTestUtil.nextLong());

		_persistence.countByKCN_KCPK("null", 0L);

		_persistence.countByKCN_KCPK((String)null, 0L);
	}

	@Test
	public void testCountByKCN_KCPK_Blocking() throws Exception {
		_persistence.countByKCN_KCPK_Blocking(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByKCN_KCPK_Blocking(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByKCN_KCPK_Blocking(
			(String)null, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoTimer newKaleoTimer = addKaleoTimer();

		KaleoTimer existingKaleoTimer = _persistence.findByPrimaryKey(
			newKaleoTimer.getPrimaryKey());

		Assert.assertEquals(existingKaleoTimer, newKaleoTimer);
	}

	@Test(expected = NoSuchTimerException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoTimer> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoTimer", "mvccVersion", true, "ctCollectionId", true,
			"kaleoTimerId", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"kaleoClassName", true, "kaleoClassPK", true, "kaleoDefinitionId",
			true, "kaleoDefinitionVersionId", true, "name", true, "blocking",
			true, "description", true, "duration", true, "scale", true,
			"recurrenceDuration", true, "recurrenceScale", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoTimer newKaleoTimer = addKaleoTimer();

		KaleoTimer existingKaleoTimer = _persistence.fetchByPrimaryKey(
			newKaleoTimer.getPrimaryKey());

		Assert.assertEquals(existingKaleoTimer, newKaleoTimer);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimer missingKaleoTimer = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoTimer);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoTimer newKaleoTimer1 = addKaleoTimer();
		KaleoTimer newKaleoTimer2 = addKaleoTimer();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTimer1.getPrimaryKey());
		primaryKeys.add(newKaleoTimer2.getPrimaryKey());

		Map<Serializable, KaleoTimer> kaleoTimers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoTimers.size());
		Assert.assertEquals(
			newKaleoTimer1, kaleoTimers.get(newKaleoTimer1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoTimer2, kaleoTimers.get(newKaleoTimer2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoTimer> kaleoTimers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTimers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoTimer newKaleoTimer = addKaleoTimer();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTimer.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoTimer> kaleoTimers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTimers.size());
		Assert.assertEquals(
			newKaleoTimer, kaleoTimers.get(newKaleoTimer.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoTimer> kaleoTimers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTimers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoTimer newKaleoTimer = addKaleoTimer();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTimer.getPrimaryKey());

		Map<Serializable, KaleoTimer> kaleoTimers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTimers.size());
		Assert.assertEquals(
			newKaleoTimer, kaleoTimers.get(newKaleoTimer.getPrimaryKey()));
	}

	protected KaleoTimer addKaleoTimer() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTimer kaleoTimer = _persistence.create(pk);

		kaleoTimer.setMvccVersion(RandomTestUtil.nextLong());

		kaleoTimer.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoTimer.setGroupId(RandomTestUtil.nextLong());

		kaleoTimer.setCompanyId(RandomTestUtil.nextLong());

		kaleoTimer.setUserId(RandomTestUtil.nextLong());

		kaleoTimer.setUserName(RandomTestUtil.randomString());

		kaleoTimer.setCreateDate(RandomTestUtil.nextDate());

		kaleoTimer.setModifiedDate(RandomTestUtil.nextDate());

		kaleoTimer.setKaleoClassName(RandomTestUtil.randomString());

		kaleoTimer.setKaleoClassPK(RandomTestUtil.nextLong());

		kaleoTimer.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoTimer.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		kaleoTimer.setName(RandomTestUtil.randomString());

		kaleoTimer.setBlocking(RandomTestUtil.randomBoolean());

		kaleoTimer.setDescription(RandomTestUtil.randomString());

		kaleoTimer.setDuration(RandomTestUtil.nextDouble());

		kaleoTimer.setScale(RandomTestUtil.randomString());

		kaleoTimer.setRecurrenceDuration(RandomTestUtil.nextDouble());

		kaleoTimer.setRecurrenceScale(RandomTestUtil.randomString());

		_kaleoTimers.add(_persistence.update(kaleoTimer));

		return kaleoTimer;
	}

	private List<KaleoTimer> _kaleoTimers = new ArrayList<KaleoTimer>();
	private KaleoTimerPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}