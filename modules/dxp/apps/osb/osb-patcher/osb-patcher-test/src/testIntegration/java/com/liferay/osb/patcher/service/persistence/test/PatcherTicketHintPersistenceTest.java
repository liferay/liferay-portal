/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherTicketHintException;
import com.liferay.osb.patcher.model.PatcherTicketHint;
import com.liferay.osb.patcher.service.PatcherTicketHintLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherTicketHintPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherTicketHintUtil;
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
public class PatcherTicketHintPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherTicketHintUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherTicketHint> iterator = _patcherTicketHints.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherTicketHint patcherTicketHint = _persistence.create(pk);

		Assert.assertNotNull(patcherTicketHint);

		Assert.assertEquals(patcherTicketHint.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		_persistence.remove(newPatcherTicketHint);

		PatcherTicketHint existingPatcherTicketHint =
			_persistence.fetchByPrimaryKey(
				newPatcherTicketHint.getPrimaryKey());

		Assert.assertNull(existingPatcherTicketHint);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherTicketHint();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherTicketHint newPatcherTicketHint = _persistence.create(pk);

		newPatcherTicketHint.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherTicketHint.setCompanyId(RandomTestUtil.nextLong());

		newPatcherTicketHint.setUserId(RandomTestUtil.nextLong());

		newPatcherTicketHint.setUserName(RandomTestUtil.randomString());

		newPatcherTicketHint.setCreateDate(RandomTestUtil.nextDate());

		newPatcherTicketHint.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherTicketHint.setPatcherProductVersionId(
			RandomTestUtil.nextLong());

		newPatcherTicketHint.setScript(RandomTestUtil.randomString());

		_patcherTicketHints.add(_persistence.update(newPatcherTicketHint));

		PatcherTicketHint existingPatcherTicketHint =
			_persistence.findByPrimaryKey(newPatcherTicketHint.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherTicketHint.getMvccVersion(),
			newPatcherTicketHint.getMvccVersion());
		Assert.assertEquals(
			existingPatcherTicketHint.getPatcherTicketHintId(),
			newPatcherTicketHint.getPatcherTicketHintId());
		Assert.assertEquals(
			existingPatcherTicketHint.getCompanyId(),
			newPatcherTicketHint.getCompanyId());
		Assert.assertEquals(
			existingPatcherTicketHint.getUserId(),
			newPatcherTicketHint.getUserId());
		Assert.assertEquals(
			existingPatcherTicketHint.getUserName(),
			newPatcherTicketHint.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherTicketHint.getCreateDate()),
			Time.getShortTimestamp(newPatcherTicketHint.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherTicketHint.getModifiedDate()),
			Time.getShortTimestamp(newPatcherTicketHint.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherTicketHint.getPatcherProductVersionId(),
			newPatcherTicketHint.getPatcherProductVersionId());
		Assert.assertEquals(
			existingPatcherTicketHint.getScript(),
			newPatcherTicketHint.getScript());
	}

	@Test
	public void testCountByPatcherProductVersionId() throws Exception {
		_persistence.countByPatcherProductVersionId(RandomTestUtil.nextLong());

		_persistence.countByPatcherProductVersionId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		PatcherTicketHint existingPatcherTicketHint =
			_persistence.findByPrimaryKey(newPatcherTicketHint.getPrimaryKey());

		Assert.assertEquals(existingPatcherTicketHint, newPatcherTicketHint);
	}

	@Test(expected = NoSuchPatcherTicketHintException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherTicketHint> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherTicketHint", "mvccVersion", true,
			"patcherTicketHintId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"patcherProductVersionId", true, "script", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		PatcherTicketHint existingPatcherTicketHint =
			_persistence.fetchByPrimaryKey(
				newPatcherTicketHint.getPrimaryKey());

		Assert.assertEquals(existingPatcherTicketHint, newPatcherTicketHint);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherTicketHint missingPatcherTicketHint =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherTicketHint);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherTicketHint newPatcherTicketHint1 = addPatcherTicketHint();
		PatcherTicketHint newPatcherTicketHint2 = addPatcherTicketHint();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherTicketHint1.getPrimaryKey());
		primaryKeys.add(newPatcherTicketHint2.getPrimaryKey());

		Map<Serializable, PatcherTicketHint> patcherTicketHints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherTicketHints.size());
		Assert.assertEquals(
			newPatcherTicketHint1,
			patcherTicketHints.get(newPatcherTicketHint1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherTicketHint2,
			patcherTicketHints.get(newPatcherTicketHint2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherTicketHint> patcherTicketHints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherTicketHints.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherTicketHint.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherTicketHint> patcherTicketHints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherTicketHints.size());
		Assert.assertEquals(
			newPatcherTicketHint,
			patcherTicketHints.get(newPatcherTicketHint.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherTicketHint> patcherTicketHints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherTicketHints.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherTicketHint.getPrimaryKey());

		Map<Serializable, PatcherTicketHint> patcherTicketHints =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherTicketHints.size());
		Assert.assertEquals(
			newPatcherTicketHint,
			patcherTicketHints.get(newPatcherTicketHint.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherTicketHintLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<PatcherTicketHint>() {

				@Override
				public void performAction(PatcherTicketHint patcherTicketHint) {
					Assert.assertNotNull(patcherTicketHint);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherTicketHint.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherTicketHintId",
				newPatcherTicketHint.getPatcherTicketHintId()));

		List<PatcherTicketHint> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherTicketHint existingPatcherTicketHint = result.get(0);

		Assert.assertEquals(existingPatcherTicketHint, newPatcherTicketHint);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherTicketHint.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherTicketHintId", RandomTestUtil.nextLong()));

		List<PatcherTicketHint> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherTicketHint.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherTicketHintId"));

		Object newPatcherTicketHintId =
			newPatcherTicketHint.getPatcherTicketHintId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherTicketHintId", new Object[] {newPatcherTicketHintId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherTicketHintId = result.get(0);

		Assert.assertEquals(
			existingPatcherTicketHintId, newPatcherTicketHintId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherTicketHint.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherTicketHintId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherTicketHintId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newPatcherTicketHint.getPrimaryKey()));
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

		PatcherTicketHint newPatcherTicketHint = addPatcherTicketHint();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherTicketHint.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherTicketHintId",
				newPatcherTicketHint.getPatcherTicketHintId()));

		List<PatcherTicketHint> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(PatcherTicketHint patcherTicketHint) {
		Assert.assertEquals(
			Long.valueOf(patcherTicketHint.getPatcherProductVersionId()),
			ReflectionTestUtil.<Long>invoke(
				patcherTicketHint, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "patcherProductVersionId"));
	}

	protected PatcherTicketHint addPatcherTicketHint() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherTicketHint patcherTicketHint = _persistence.create(pk);

		patcherTicketHint.setMvccVersion(RandomTestUtil.nextLong());

		patcherTicketHint.setCompanyId(RandomTestUtil.nextLong());

		patcherTicketHint.setUserId(RandomTestUtil.nextLong());

		patcherTicketHint.setUserName(RandomTestUtil.randomString());

		patcherTicketHint.setCreateDate(RandomTestUtil.nextDate());

		patcherTicketHint.setModifiedDate(RandomTestUtil.nextDate());

		patcherTicketHint.setPatcherProductVersionId(RandomTestUtil.nextLong());

		patcherTicketHint.setScript(RandomTestUtil.randomString());

		_patcherTicketHints.add(_persistence.update(patcherTicketHint));

		return patcherTicketHint;
	}

	private List<PatcherTicketHint> _patcherTicketHints =
		new ArrayList<PatcherTicketHint>();
	private PatcherTicketHintPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}