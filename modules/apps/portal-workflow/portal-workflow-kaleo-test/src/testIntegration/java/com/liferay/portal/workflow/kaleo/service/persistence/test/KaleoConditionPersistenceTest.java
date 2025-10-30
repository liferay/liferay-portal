/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

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
import com.liferay.portal.workflow.kaleo.exception.NoSuchConditionException;
import com.liferay.portal.workflow.kaleo.model.KaleoCondition;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoConditionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoConditionUtil;

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
public class KaleoConditionPersistenceTest {

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
		_persistence = KaleoConditionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoCondition> iterator = _kaleoConditions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoCondition kaleoCondition = _persistence.create(pk);

		Assert.assertNotNull(kaleoCondition);

		Assert.assertEquals(kaleoCondition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoCondition newKaleoCondition = addKaleoCondition();

		_persistence.remove(newKaleoCondition);

		KaleoCondition existingKaleoCondition = _persistence.fetchByPrimaryKey(
			newKaleoCondition.getPrimaryKey());

		Assert.assertNull(existingKaleoCondition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoCondition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoCondition newKaleoCondition = _persistence.create(pk);

		newKaleoCondition.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoCondition.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoCondition.setGroupId(RandomTestUtil.nextLong());

		newKaleoCondition.setCompanyId(RandomTestUtil.nextLong());

		newKaleoCondition.setUserId(RandomTestUtil.nextLong());

		newKaleoCondition.setUserName(RandomTestUtil.randomString());

		newKaleoCondition.setCreateDate(RandomTestUtil.nextDate());

		newKaleoCondition.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoCondition.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoCondition.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoCondition.setKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoCondition.setScript(RandomTestUtil.randomString());

		newKaleoCondition.setScriptLanguage(RandomTestUtil.randomString());

		newKaleoCondition.setScriptRequiredContexts(
			RandomTestUtil.randomString());

		_kaleoConditions.add(_persistence.update(newKaleoCondition));

		KaleoCondition existingKaleoCondition = _persistence.findByPrimaryKey(
			newKaleoCondition.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoCondition.getMvccVersion(),
			newKaleoCondition.getMvccVersion());
		Assert.assertEquals(
			existingKaleoCondition.getCtCollectionId(),
			newKaleoCondition.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoCondition.getKaleoConditionId(),
			newKaleoCondition.getKaleoConditionId());
		Assert.assertEquals(
			existingKaleoCondition.getGroupId(),
			newKaleoCondition.getGroupId());
		Assert.assertEquals(
			existingKaleoCondition.getCompanyId(),
			newKaleoCondition.getCompanyId());
		Assert.assertEquals(
			existingKaleoCondition.getUserId(), newKaleoCondition.getUserId());
		Assert.assertEquals(
			existingKaleoCondition.getUserName(),
			newKaleoCondition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoCondition.getCreateDate()),
			Time.getShortTimestamp(newKaleoCondition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoCondition.getModifiedDate()),
			Time.getShortTimestamp(newKaleoCondition.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoCondition.getKaleoDefinitionId(),
			newKaleoCondition.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoCondition.getKaleoDefinitionVersionId(),
			newKaleoCondition.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoCondition.getKaleoNodeId(),
			newKaleoCondition.getKaleoNodeId());
		Assert.assertEquals(
			existingKaleoCondition.getScript(), newKaleoCondition.getScript());
		Assert.assertEquals(
			existingKaleoCondition.getScriptLanguage(),
			newKaleoCondition.getScriptLanguage());
		Assert.assertEquals(
			existingKaleoCondition.getScriptRequiredContexts(),
			newKaleoCondition.getScriptRequiredContexts());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByKaleoDefinitionVersionId() throws Exception {
		_persistence.countByKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		_persistence.countByKaleoDefinitionVersionId(0L);
	}

	@Test
	public void testCountByKaleoNodeId() throws Exception {
		_persistence.countByKaleoNodeId(RandomTestUtil.nextLong());

		_persistence.countByKaleoNodeId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoCondition newKaleoCondition = addKaleoCondition();

		KaleoCondition existingKaleoCondition = _persistence.findByPrimaryKey(
			newKaleoCondition.getPrimaryKey());

		Assert.assertEquals(existingKaleoCondition, newKaleoCondition);
	}

	@Test(expected = NoSuchConditionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoCondition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoCondition", "mvccVersion", true, "ctCollectionId", true,
			"kaleoConditionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "kaleoDefinitionId", true,
			"kaleoDefinitionVersionId", true, "kaleoNodeId", true,
			"scriptLanguage", true, "scriptRequiredContexts", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoCondition newKaleoCondition = addKaleoCondition();

		KaleoCondition existingKaleoCondition = _persistence.fetchByPrimaryKey(
			newKaleoCondition.getPrimaryKey());

		Assert.assertEquals(existingKaleoCondition, newKaleoCondition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoCondition missingKaleoCondition = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingKaleoCondition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoCondition newKaleoCondition1 = addKaleoCondition();
		KaleoCondition newKaleoCondition2 = addKaleoCondition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoCondition1.getPrimaryKey());
		primaryKeys.add(newKaleoCondition2.getPrimaryKey());

		Map<Serializable, KaleoCondition> kaleoConditions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoConditions.size());
		Assert.assertEquals(
			newKaleoCondition1,
			kaleoConditions.get(newKaleoCondition1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoCondition2,
			kaleoConditions.get(newKaleoCondition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoCondition> kaleoConditions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoConditions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoCondition newKaleoCondition = addKaleoCondition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoCondition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoCondition> kaleoConditions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoConditions.size());
		Assert.assertEquals(
			newKaleoCondition,
			kaleoConditions.get(newKaleoCondition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoCondition> kaleoConditions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoConditions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoCondition newKaleoCondition = addKaleoCondition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoCondition.getPrimaryKey());

		Map<Serializable, KaleoCondition> kaleoConditions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoConditions.size());
		Assert.assertEquals(
			newKaleoCondition,
			kaleoConditions.get(newKaleoCondition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoCondition newKaleoCondition = addKaleoCondition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKaleoCondition.getPrimaryKey()));
	}

	private void _assertOriginalValues(KaleoCondition kaleoCondition) {
		Assert.assertEquals(
			Long.valueOf(kaleoCondition.getKaleoNodeId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoCondition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoNodeId"));
	}

	protected KaleoCondition addKaleoCondition() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoCondition kaleoCondition = _persistence.create(pk);

		kaleoCondition.setMvccVersion(RandomTestUtil.nextLong());

		kaleoCondition.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoCondition.setGroupId(RandomTestUtil.nextLong());

		kaleoCondition.setCompanyId(RandomTestUtil.nextLong());

		kaleoCondition.setUserId(RandomTestUtil.nextLong());

		kaleoCondition.setUserName(RandomTestUtil.randomString());

		kaleoCondition.setCreateDate(RandomTestUtil.nextDate());

		kaleoCondition.setModifiedDate(RandomTestUtil.nextDate());

		kaleoCondition.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoCondition.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		kaleoCondition.setKaleoNodeId(RandomTestUtil.nextLong());

		kaleoCondition.setScript(RandomTestUtil.randomString());

		kaleoCondition.setScriptLanguage(RandomTestUtil.randomString());

		kaleoCondition.setScriptRequiredContexts(RandomTestUtil.randomString());

		_kaleoConditions.add(_persistence.update(kaleoCondition));

		return kaleoCondition;
	}

	private List<KaleoCondition> _kaleoConditions =
		new ArrayList<KaleoCondition>();
	private KaleoConditionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}