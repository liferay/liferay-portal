/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.workflow.kaleo.exception.NoSuchActionException;
import com.liferay.portal.workflow.kaleo.model.KaleoAction;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoActionPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoActionUtil;

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
public class KaleoActionPersistenceTest {

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
		_persistence = KaleoActionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoAction> iterator = _kaleoActions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoAction kaleoAction = _persistence.create(pk);

		Assert.assertNotNull(kaleoAction);

		Assert.assertEquals(kaleoAction.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoAction newKaleoAction = addKaleoAction();

		_persistence.remove(newKaleoAction);

		KaleoAction existingKaleoAction = _persistence.fetchByPrimaryKey(
			newKaleoAction.getPrimaryKey());

		Assert.assertNull(existingKaleoAction);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoAction();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoAction newKaleoAction = _persistence.create(pk);

		newKaleoAction.setMvccVersion(RandomTestUtil.nextLong());

		newKaleoAction.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoAction.setGroupId(RandomTestUtil.nextLong());

		newKaleoAction.setCompanyId(RandomTestUtil.nextLong());

		newKaleoAction.setUserId(RandomTestUtil.nextLong());

		newKaleoAction.setUserName(RandomTestUtil.randomString());

		newKaleoAction.setCreateDate(RandomTestUtil.nextDate());

		newKaleoAction.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoAction.setKaleoClassName(RandomTestUtil.randomString());

		newKaleoAction.setKaleoClassPK(RandomTestUtil.nextLong());

		newKaleoAction.setKaleoDefinitionId(RandomTestUtil.nextLong());

		newKaleoAction.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		newKaleoAction.setKaleoNodeName(RandomTestUtil.randomString());

		newKaleoAction.setName(RandomTestUtil.randomString());

		newKaleoAction.setDescription(RandomTestUtil.randomString());

		newKaleoAction.setExecutionType(RandomTestUtil.randomString());

		newKaleoAction.setScript(RandomTestUtil.randomString());

		newKaleoAction.setScriptLanguage(RandomTestUtil.randomString());

		newKaleoAction.setScriptRequiredContexts(RandomTestUtil.randomString());

		newKaleoAction.setPriority(RandomTestUtil.nextInt());

		newKaleoAction.setType(RandomTestUtil.randomString());

		newKaleoAction.setStatus(RandomTestUtil.nextInt());

		_kaleoActions.add(_persistence.update(newKaleoAction));

		KaleoAction existingKaleoAction = _persistence.findByPrimaryKey(
			newKaleoAction.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoAction.getMvccVersion(),
			newKaleoAction.getMvccVersion());
		Assert.assertEquals(
			existingKaleoAction.getCtCollectionId(),
			newKaleoAction.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoAction.getKaleoActionId(),
			newKaleoAction.getKaleoActionId());
		Assert.assertEquals(
			existingKaleoAction.getGroupId(), newKaleoAction.getGroupId());
		Assert.assertEquals(
			existingKaleoAction.getCompanyId(), newKaleoAction.getCompanyId());
		Assert.assertEquals(
			existingKaleoAction.getUserId(), newKaleoAction.getUserId());
		Assert.assertEquals(
			existingKaleoAction.getUserName(), newKaleoAction.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoAction.getCreateDate()),
			Time.getShortTimestamp(newKaleoAction.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoAction.getModifiedDate()),
			Time.getShortTimestamp(newKaleoAction.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoAction.getKaleoClassName(),
			newKaleoAction.getKaleoClassName());
		Assert.assertEquals(
			existingKaleoAction.getKaleoClassPK(),
			newKaleoAction.getKaleoClassPK());
		Assert.assertEquals(
			existingKaleoAction.getKaleoDefinitionId(),
			newKaleoAction.getKaleoDefinitionId());
		Assert.assertEquals(
			existingKaleoAction.getKaleoDefinitionVersionId(),
			newKaleoAction.getKaleoDefinitionVersionId());
		Assert.assertEquals(
			existingKaleoAction.getKaleoNodeName(),
			newKaleoAction.getKaleoNodeName());
		Assert.assertEquals(
			existingKaleoAction.getName(), newKaleoAction.getName());
		Assert.assertEquals(
			existingKaleoAction.getDescription(),
			newKaleoAction.getDescription());
		Assert.assertEquals(
			existingKaleoAction.getExecutionType(),
			newKaleoAction.getExecutionType());
		Assert.assertEquals(
			existingKaleoAction.getScript(), newKaleoAction.getScript());
		Assert.assertEquals(
			existingKaleoAction.getScriptLanguage(),
			newKaleoAction.getScriptLanguage());
		Assert.assertEquals(
			existingKaleoAction.getScriptRequiredContexts(),
			newKaleoAction.getScriptRequiredContexts());
		Assert.assertEquals(
			existingKaleoAction.getPriority(), newKaleoAction.getPriority());
		Assert.assertEquals(
			existingKaleoAction.getType(), newKaleoAction.getType());
		Assert.assertEquals(
			existingKaleoAction.getStatus(), newKaleoAction.getStatus());
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
	public void testCountByKCN_KCPK() throws Exception {
		_persistence.countByKCN_KCPK("", RandomTestUtil.nextLong());

		_persistence.countByKCN_KCPK("null", 0L);

		_persistence.countByKCN_KCPK((String)null, 0L);
	}

	@Test
	public void testCountByC_KCN_KCPK() throws Exception {
		_persistence.countByC_KCN_KCPK(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong());

		_persistence.countByC_KCN_KCPK(0L, "null", 0L);

		_persistence.countByC_KCN_KCPK(0L, (String)null, 0L);
	}

	@Test
	public void testCountByKCN_KCPK_ET() throws Exception {
		_persistence.countByKCN_KCPK_ET("", RandomTestUtil.nextLong(), "");

		_persistence.countByKCN_KCPK_ET("null", 0L, "null");

		_persistence.countByKCN_KCPK_ET((String)null, 0L, (String)null);
	}

	@Test
	public void testCountByC_KCN_KCPK_ET() throws Exception {
		_persistence.countByC_KCN_KCPK_ET(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextLong(), "");

		_persistence.countByC_KCN_KCPK_ET(0L, "null", 0L, "null");

		_persistence.countByC_KCN_KCPK_ET(0L, (String)null, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoAction newKaleoAction = addKaleoAction();

		KaleoAction existingKaleoAction = _persistence.findByPrimaryKey(
			newKaleoAction.getPrimaryKey());

		Assert.assertEquals(existingKaleoAction, newKaleoAction);
	}

	@Test(expected = NoSuchActionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoAction> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoAction", "mvccVersion", true, "ctCollectionId", true,
			"kaleoActionId", true, "groupId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"kaleoClassName", true, "kaleoClassPK", true, "kaleoDefinitionId",
			true, "kaleoDefinitionVersionId", true, "kaleoNodeName", true,
			"name", true, "description", true, "executionType", true,
			"scriptLanguage", true, "scriptRequiredContexts", true, "priority",
			true, "type", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoAction newKaleoAction = addKaleoAction();

		KaleoAction existingKaleoAction = _persistence.fetchByPrimaryKey(
			newKaleoAction.getPrimaryKey());

		Assert.assertEquals(existingKaleoAction, newKaleoAction);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoAction missingKaleoAction = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoAction);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoAction newKaleoAction1 = addKaleoAction();
		KaleoAction newKaleoAction2 = addKaleoAction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoAction1.getPrimaryKey());
		primaryKeys.add(newKaleoAction2.getPrimaryKey());

		Map<Serializable, KaleoAction> kaleoActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoActions.size());
		Assert.assertEquals(
			newKaleoAction1, kaleoActions.get(newKaleoAction1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoAction2, kaleoActions.get(newKaleoAction2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoAction> kaleoActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoActions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoAction newKaleoAction = addKaleoAction();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoAction.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoAction> kaleoActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoActions.size());
		Assert.assertEquals(
			newKaleoAction, kaleoActions.get(newKaleoAction.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoAction> kaleoActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoActions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoAction newKaleoAction = addKaleoAction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoAction.getPrimaryKey());

		Map<Serializable, KaleoAction> kaleoActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoActions.size());
		Assert.assertEquals(
			newKaleoAction, kaleoActions.get(newKaleoAction.getPrimaryKey()));
	}

	protected KaleoAction addKaleoAction() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoAction kaleoAction = _persistence.create(pk);

		kaleoAction.setMvccVersion(RandomTestUtil.nextLong());

		kaleoAction.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoAction.setGroupId(RandomTestUtil.nextLong());

		kaleoAction.setCompanyId(RandomTestUtil.nextLong());

		kaleoAction.setUserId(RandomTestUtil.nextLong());

		kaleoAction.setUserName(RandomTestUtil.randomString());

		kaleoAction.setCreateDate(RandomTestUtil.nextDate());

		kaleoAction.setModifiedDate(RandomTestUtil.nextDate());

		kaleoAction.setKaleoClassName(RandomTestUtil.randomString());

		kaleoAction.setKaleoClassPK(RandomTestUtil.nextLong());

		kaleoAction.setKaleoDefinitionId(RandomTestUtil.nextLong());

		kaleoAction.setKaleoDefinitionVersionId(RandomTestUtil.nextLong());

		kaleoAction.setKaleoNodeName(RandomTestUtil.randomString());

		kaleoAction.setName(RandomTestUtil.randomString());

		kaleoAction.setDescription(RandomTestUtil.randomString());

		kaleoAction.setExecutionType(RandomTestUtil.randomString());

		kaleoAction.setScript(RandomTestUtil.randomString());

		kaleoAction.setScriptLanguage(RandomTestUtil.randomString());

		kaleoAction.setScriptRequiredContexts(RandomTestUtil.randomString());

		kaleoAction.setPriority(RandomTestUtil.nextInt());

		kaleoAction.setType(RandomTestUtil.randomString());

		kaleoAction.setStatus(RandomTestUtil.nextInt());

		_kaleoActions.add(_persistence.update(kaleoAction));

		return kaleoAction;
	}

	private List<KaleoAction> _kaleoActions = new ArrayList<KaleoAction>();
	private KaleoActionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}