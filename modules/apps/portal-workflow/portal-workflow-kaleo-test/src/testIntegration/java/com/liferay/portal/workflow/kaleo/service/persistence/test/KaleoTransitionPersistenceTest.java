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
import com.liferay.portal.workflow.kaleo.exception.NoSuchTransitionException;
import com.liferay.portal.workflow.kaleo.model.KaleoTransition;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTransitionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoTransitionUtil;

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
public class KaleoTransitionPersistenceTest {

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
		_persistence = KaleoTransitionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoTransition> iterator = _kaleoTransitions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTransition kaleoTransition = _persistence.create(pk);

		Assert.assertNotNull(kaleoTransition);

		Assert.assertEquals(kaleoTransition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoTransition newKaleoTransition = addKaleoTransition();

		_persistence.remove(newKaleoTransition);

		KaleoTransition existingKaleoTransition =
			_persistence.fetchByPrimaryKey(newKaleoTransition.getPrimaryKey());

		Assert.assertNull(existingKaleoTransition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoTransition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTransition newKaleoTransition = _persistence.create(pk);

		newKaleoTransition.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoTransition.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoTransition.setGroupId(RandomTestUtil.nextLong());

		newKaleoTransition.setCompanyId(RandomTestUtil.nextLong());

		newKaleoTransition.setUserId(RandomTestUtil.nextLong());

		newKaleoTransition.setUserName(RandomTestUtil.randomString());

		newKaleoTransition.setCreateDate(RandomTestUtil.nextDate());

		newKaleoTransition.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoTransition.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoTransition.setKaleoDefinitionVersionId(
			RandomTestUtil.nextLong());

		newKaleoTransition.setKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoTransition.setName(RandomTestUtil.randomString());

		newKaleoTransition.setLabel(RandomTestUtil.randomString());

		newKaleoTransition.setDescription(RandomTestUtil.randomString());

		newKaleoTransition.setSourceKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoTransition.setSourceKaleoNodeName(
			RandomTestUtil.randomString());

		newKaleoTransition.setTargetKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoTransition.setTargetKaleoNodeName(
			RandomTestUtil.randomString());

		newKaleoTransition.setDefaultTransition(RandomTestUtil.randomBoolean());

		_kaleoTransitions.add(_persistence.update(newKaleoTransition));

		KaleoTransition existingKaleoTransition = _persistence.findByPrimaryKey(
			newKaleoTransition.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoTransition.getMvccVersion(),
			newKaleoTransition.getMvccVersion());
		Assert.assertEquals(
			existingKaleoTransition.getCtCollectionId(),
			newKaleoTransition.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoTransition.getKaleoTransitionId(),
			newKaleoTransition.getKaleoTransitionId());
		Assert.assertEquals(
			existingKaleoTransition.getGroupId(),
			newKaleoTransition.getGroupId());
		Assert.assertEquals(
			existingKaleoTransition.getCompanyId(),
			newKaleoTransition.getCompanyId());
		Assert.assertEquals(
			existingKaleoTransition.getUserId(),
			newKaleoTransition.getUserId());
		Assert.assertEquals(
			existingKaleoTransition.getUserName(),
			newKaleoTransition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoTransition.getCreateDate()),
			Time.getShortTimestamp(newKaleoTransition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoTransition.getModifiedDate()),
			Time.getShortTimestamp(newKaleoTransition.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoTransition.getKaleoDefinitionId(),
			newKaleoTransition.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoTransition.getKaleoDefinitionVersionId(),
			newKaleoTransition.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoTransition.getKaleoNodeId(),
			newKaleoTransition.getKaleoNodeId());
		Assert.assertEquals(
			existingKaleoTransition.getName(), newKaleoTransition.getName());
		Assert.assertEquals(
			existingKaleoTransition.getLabel(), newKaleoTransition.getLabel());
		Assert.assertEquals(
			existingKaleoTransition.getDescription(),
			newKaleoTransition.getDescription());
		Assert.assertEquals(
			existingKaleoTransition.getSourceKaleoNodeId(),
			newKaleoTransition.getSourceKaleoNodeId());
		Assert.assertEquals(
			existingKaleoTransition.getSourceKaleoNodeName(),
			newKaleoTransition.getSourceKaleoNodeName());
		Assert.assertEquals(
			existingKaleoTransition.getTargetKaleoNodeId(),
			newKaleoTransition.getTargetKaleoNodeId());
		Assert.assertEquals(
			existingKaleoTransition.getTargetKaleoNodeName(),
			newKaleoTransition.getTargetKaleoNodeName());
		Assert.assertEquals(
			existingKaleoTransition.isDefaultTransition(),
			newKaleoTransition.isDefaultTransition());
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
	public void testCountByKNI_N() throws Exception {
		_persistence.countByKNI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByKNI_N(0L, "null");

		_persistence.countByKNI_N(0L, (String)null);
	}

	@Test
	public void testCountByKNI_DT() throws Exception {
		_persistence.countByKNI_DT(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByKNI_DT(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoTransition newKaleoTransition = addKaleoTransition();

		KaleoTransition existingKaleoTransition = _persistence.findByPrimaryKey(
			newKaleoTransition.getPrimaryKey());

		Assert.assertEquals(existingKaleoTransition, newKaleoTransition);
	}

	@Test(expected = NoSuchTransitionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoTransition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoTransition", "mvccVersion", true, "ctCollectionId", true,
			"kaleoTransitionId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "kaleoDefinitionId", true,
			"kaleoDefinitionVersionId", true, "kaleoNodeId", true, "name", true,
			"label", true, "description", true, "sourceKaleoNodeId", true,
			"sourceKaleoNodeName", true, "targetKaleoNodeId", true,
			"targetKaleoNodeName", true, "defaultTransition", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoTransition newKaleoTransition = addKaleoTransition();

		KaleoTransition existingKaleoTransition =
			_persistence.fetchByPrimaryKey(newKaleoTransition.getPrimaryKey());

		Assert.assertEquals(existingKaleoTransition, newKaleoTransition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTransition missingKaleoTransition = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingKaleoTransition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoTransition newKaleoTransition1 = addKaleoTransition();
		KaleoTransition newKaleoTransition2 = addKaleoTransition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTransition1.getPrimaryKey());
		primaryKeys.add(newKaleoTransition2.getPrimaryKey());

		Map<Serializable, KaleoTransition> kaleoTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoTransitions.size());
		Assert.assertEquals(
			newKaleoTransition1,
			kaleoTransitions.get(newKaleoTransition1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoTransition2,
			kaleoTransitions.get(newKaleoTransition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoTransition> kaleoTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTransitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoTransition newKaleoTransition = addKaleoTransition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTransition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoTransition> kaleoTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTransitions.size());
		Assert.assertEquals(
			newKaleoTransition,
			kaleoTransitions.get(newKaleoTransition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoTransition> kaleoTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoTransitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoTransition newKaleoTransition = addKaleoTransition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoTransition.getPrimaryKey());

		Map<Serializable, KaleoTransition> kaleoTransitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoTransitions.size());
		Assert.assertEquals(
			newKaleoTransition,
			kaleoTransitions.get(newKaleoTransition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoTransition newKaleoTransition = addKaleoTransition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKaleoTransition.getPrimaryKey()));
	}

	private void _assertOriginalValues(KaleoTransition kaleoTransition) {
		Assert.assertEquals(
			Long.valueOf(kaleoTransition.getKaleoNodeId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoTransition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoNodeId"));
		Assert.assertEquals(
			kaleoTransition.getName(),
			ReflectionTestUtil.invoke(
				kaleoTransition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(kaleoTransition.getKaleoNodeId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoTransition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoNodeId"));
		Assert.assertEquals(
			Boolean.valueOf(kaleoTransition.getDefaultTransition()),
			ReflectionTestUtil.<Boolean>invoke(
				kaleoTransition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "defaultTransition"));
	}

	protected KaleoTransition addKaleoTransition() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoTransition kaleoTransition = _persistence.create(pk);

		kaleoTransition.setMvccVersion(RandomTestUtil.nextLong());

		kaleoTransition.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoTransition.setGroupId(RandomTestUtil.nextLong());

		kaleoTransition.setCompanyId(RandomTestUtil.nextLong());

		kaleoTransition.setUserId(RandomTestUtil.nextLong());

		kaleoTransition.setUserName(RandomTestUtil.randomString());

		kaleoTransition.setCreateDate(RandomTestUtil.nextDate());

		kaleoTransition.setModifiedDate(RandomTestUtil.nextDate());

		kaleoTransition.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoTransition.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		kaleoTransition.setKaleoNodeId(RandomTestUtil.nextLong());

		kaleoTransition.setName(RandomTestUtil.randomString());

		kaleoTransition.setLabel(RandomTestUtil.randomString());

		kaleoTransition.setDescription(RandomTestUtil.randomString());

		kaleoTransition.setSourceKaleoNodeId(RandomTestUtil.nextLong());

		kaleoTransition.setSourceKaleoNodeName(RandomTestUtil.randomString());

		kaleoTransition.setTargetKaleoNodeId(RandomTestUtil.nextLong());

		kaleoTransition.setTargetKaleoNodeName(RandomTestUtil.randomString());

		kaleoTransition.setDefaultTransition(RandomTestUtil.randomBoolean());

		_kaleoTransitions.add(_persistence.update(kaleoTransition));

		return kaleoTransition;
	}

	private List<KaleoTransition> _kaleoTransitions =
		new ArrayList<KaleoTransition>();
	private KaleoTransitionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}