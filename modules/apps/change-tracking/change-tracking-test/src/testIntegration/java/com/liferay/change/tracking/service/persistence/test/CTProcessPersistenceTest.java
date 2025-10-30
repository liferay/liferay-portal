/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.change.tracking.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.change.tracking.exception.NoSuchProcessException;
import com.liferay.change.tracking.model.CTProcess;
import com.liferay.change.tracking.service.persistence.CTProcessPersistence;
import com.liferay.change.tracking.service.persistence.CTProcessUtil;
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
public class CTProcessPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.change.tracking.service"));

	@Before
	public void setUp() {
		_persistence = CTProcessUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTProcess> iterator = _ctProcesses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTProcess ctProcess = _persistence.create(pk);

		Assert.assertNotNull(ctProcess);

		Assert.assertEquals(ctProcess.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTProcess newCTProcess = addCTProcess();

		_persistence.remove(newCTProcess);

		CTProcess existingCTProcess = _persistence.fetchByPrimaryKey(
			newCTProcess.getPrimaryKey());

		Assert.assertNull(existingCTProcess);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTProcess();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTProcess newCTProcess = _persistence.create(pk);

		newCTProcess.setMvccVersion(RandomTestUtil.nextLong());

		newCTProcess.setCompanyId(RandomTestUtil.nextLong());

		newCTProcess.setUserId(RandomTestUtil.nextLong());

		newCTProcess.setCreateDate(RandomTestUtil.nextDate());

		newCTProcess.setCtCollectionId(RandomTestUtil.nextLong());

		newCTProcess.setBackgroundTaskId(RandomTestUtil.nextLong());

		newCTProcess.setType(RandomTestUtil.nextInt());

		_ctProcesses.add(_persistence.update(newCTProcess));

		CTProcess existingCTProcess = _persistence.findByPrimaryKey(
			newCTProcess.getPrimaryKey());

		Assert.assertEquals(
			existingCTProcess.getMvccVersion(), newCTProcess.getMvccVersion());
		Assert.assertEquals(
			existingCTProcess.getCtProcessId(), newCTProcess.getCtProcessId());
		Assert.assertEquals(
			existingCTProcess.getCompanyId(), newCTProcess.getCompanyId());
		Assert.assertEquals(
			existingCTProcess.getUserId(), newCTProcess.getUserId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCTProcess.getCreateDate()),
			Time.getShortTimestamp(newCTProcess.getCreateDate()));
		Assert.assertEquals(
			existingCTProcess.getCtCollectionId(),
			newCTProcess.getCtCollectionId());
		Assert.assertEquals(
			existingCTProcess.getBackgroundTaskId(),
			newCTProcess.getBackgroundTaskId());
		Assert.assertEquals(
			existingCTProcess.getType(), newCTProcess.getType());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCtCollectionId() throws Exception {
		_persistence.countByCtCollectionId(RandomTestUtil.nextLong());

		_persistence.countByCtCollectionId(0L);
	}

	@Test
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_T(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTProcess newCTProcess = addCTProcess();

		CTProcess existingCTProcess = _persistence.findByPrimaryKey(
			newCTProcess.getPrimaryKey());

		Assert.assertEquals(existingCTProcess, newCTProcess);
	}

	@Test(expected = NoSuchProcessException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTProcess> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTProcess", "mvccVersion", true, "ctProcessId", true, "companyId",
			true, "userId", true, "createDate", true, "ctCollectionId", true,
			"backgroundTaskId", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTProcess newCTProcess = addCTProcess();

		CTProcess existingCTProcess = _persistence.fetchByPrimaryKey(
			newCTProcess.getPrimaryKey());

		Assert.assertEquals(existingCTProcess, newCTProcess);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTProcess missingCTProcess = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTProcess);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTProcess newCTProcess1 = addCTProcess();
		CTProcess newCTProcess2 = addCTProcess();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTProcess1.getPrimaryKey());
		primaryKeys.add(newCTProcess2.getPrimaryKey());

		Map<Serializable, CTProcess> ctProcesses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ctProcesses.size());
		Assert.assertEquals(
			newCTProcess1, ctProcesses.get(newCTProcess1.getPrimaryKey()));
		Assert.assertEquals(
			newCTProcess2, ctProcesses.get(newCTProcess2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTProcess> ctProcesses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctProcesses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTProcess newCTProcess = addCTProcess();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTProcess.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTProcess> ctProcesses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctProcesses.size());
		Assert.assertEquals(
			newCTProcess, ctProcesses.get(newCTProcess.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTProcess> ctProcesses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ctProcesses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTProcess newCTProcess = addCTProcess();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTProcess.getPrimaryKey());

		Map<Serializable, CTProcess> ctProcesses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ctProcesses.size());
		Assert.assertEquals(
			newCTProcess, ctProcesses.get(newCTProcess.getPrimaryKey()));
	}

	protected CTProcess addCTProcess() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTProcess ctProcess = _persistence.create(pk);

		ctProcess.setMvccVersion(RandomTestUtil.nextLong());

		ctProcess.setCompanyId(RandomTestUtil.nextLong());

		ctProcess.setUserId(RandomTestUtil.nextLong());

		ctProcess.setCreateDate(RandomTestUtil.nextDate());

		ctProcess.setCtCollectionId(RandomTestUtil.nextLong());

		ctProcess.setBackgroundTaskId(RandomTestUtil.nextLong());

		ctProcess.setType(RandomTestUtil.nextInt());

		_ctProcesses.add(_persistence.update(ctProcess));

		return ctProcess;
	}

	private List<CTProcess> _ctProcesses = new ArrayList<CTProcess>();
	private CTProcessPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}