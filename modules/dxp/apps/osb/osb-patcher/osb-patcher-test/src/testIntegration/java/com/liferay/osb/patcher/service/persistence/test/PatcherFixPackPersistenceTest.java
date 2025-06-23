/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixPackException;
import com.liferay.osb.patcher.model.PatcherFixPack;
import com.liferay.osb.patcher.service.PatcherFixPackLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixPackUtil;
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
public class PatcherFixPackPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherFixPackUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherFixPack> iterator = _patcherFixPacks.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixPack patcherFixPack = _persistence.create(pk);

		Assert.assertNotNull(patcherFixPack);

		Assert.assertEquals(patcherFixPack.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		_persistence.remove(newPatcherFixPack);

		PatcherFixPack existingPatcherFixPack = _persistence.fetchByPrimaryKey(
			newPatcherFixPack.getPrimaryKey());

		Assert.assertNull(existingPatcherFixPack);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherFixPack();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixPack newPatcherFixPack = _persistence.create(pk);

		newPatcherFixPack.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherFixPack.setCompanyId(RandomTestUtil.nextLong());

		newPatcherFixPack.setUserId(RandomTestUtil.nextLong());

		newPatcherFixPack.setUserName(RandomTestUtil.randomString());

		newPatcherFixPack.setCreateDate(RandomTestUtil.nextDate());

		newPatcherFixPack.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherFixPack.setPatcherBuildId(RandomTestUtil.nextLong());

		newPatcherFixPack.setPatcherFixComponentId(RandomTestUtil.nextLong());

		newPatcherFixPack.setPatcherProjectVersionId(RandomTestUtil.nextLong());

		newPatcherFixPack.setName(RandomTestUtil.randomString());

		newPatcherFixPack.setReleasedDate(RandomTestUtil.nextDate());

		newPatcherFixPack.setRequirements(RandomTestUtil.randomString());

		newPatcherFixPack.setVersion(RandomTestUtil.nextInt());

		newPatcherFixPack.setStatus(RandomTestUtil.nextInt());

		_patcherFixPacks.add(_persistence.update(newPatcherFixPack));

		PatcherFixPack existingPatcherFixPack = _persistence.findByPrimaryKey(
			newPatcherFixPack.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherFixPack.getMvccVersion(),
			newPatcherFixPack.getMvccVersion());
		Assert.assertEquals(
			existingPatcherFixPack.getPatcherFixPackId(),
			newPatcherFixPack.getPatcherFixPackId());
		Assert.assertEquals(
			existingPatcherFixPack.getCompanyId(),
			newPatcherFixPack.getCompanyId());
		Assert.assertEquals(
			existingPatcherFixPack.getUserId(), newPatcherFixPack.getUserId());
		Assert.assertEquals(
			existingPatcherFixPack.getUserName(),
			newPatcherFixPack.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFixPack.getCreateDate()),
			Time.getShortTimestamp(newPatcherFixPack.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFixPack.getModifiedDate()),
			Time.getShortTimestamp(newPatcherFixPack.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherFixPack.getPatcherBuildId(),
			newPatcherFixPack.getPatcherBuildId());
		Assert.assertEquals(
			existingPatcherFixPack.getPatcherFixComponentId(),
			newPatcherFixPack.getPatcherFixComponentId());
		Assert.assertEquals(
			existingPatcherFixPack.getPatcherProjectVersionId(),
			newPatcherFixPack.getPatcherProjectVersionId());
		Assert.assertEquals(
			existingPatcherFixPack.getName(), newPatcherFixPack.getName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFixPack.getReleasedDate()),
			Time.getShortTimestamp(newPatcherFixPack.getReleasedDate()));
		Assert.assertEquals(
			existingPatcherFixPack.getRequirements(),
			newPatcherFixPack.getRequirements());
		Assert.assertEquals(
			existingPatcherFixPack.getVersion(),
			newPatcherFixPack.getVersion());
		Assert.assertEquals(
			existingPatcherFixPack.getStatus(), newPatcherFixPack.getStatus());
	}

	@Test
	public void testCountByPatcherBuildId() throws Exception {
		_persistence.countByPatcherBuildId(RandomTestUtil.nextLong());

		_persistence.countByPatcherBuildId(0L);
	}

	@Test
	public void testCountByPatcherFixComponentId() throws Exception {
		_persistence.countByPatcherFixComponentId(RandomTestUtil.nextLong());

		_persistence.countByPatcherFixComponentId(0L);
	}

	@Test
	public void testCountByVersion() throws Exception {
		_persistence.countByVersion(RandomTestUtil.nextInt());

		_persistence.countByVersion(0);
	}

	@Test
	public void testCountByPFCI_PPVI() throws Exception {
		_persistence.countByPFCI_PPVI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByPFCI_PPVI(0L, 0L);
	}

	@Test
	public void testCountByPFCI_V() throws Exception {
		_persistence.countByPFCI_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByPFCI_V(0L, 0);
	}

	@Test
	public void testCountByPFCI_N() throws Exception {
		_persistence.countByPFCI_N(RandomTestUtil.nextLong(), "");

		_persistence.countByPFCI_N(0L, "null");

		_persistence.countByPFCI_N(0L, (String)null);
	}

	@Test
	public void testCountByPFCI_S() throws Exception {
		_persistence.countByPFCI_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByPFCI_S(0L, 0);
	}

	@Test
	public void testCountByPFCI_PPVI_GtV() throws Exception {
		_persistence.countByPFCI_PPVI_GtV(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByPFCI_PPVI_GtV(0L, 0L, 0);
	}

	@Test
	public void testCountByPFCI_PPVI_LtV() throws Exception {
		_persistence.countByPFCI_PPVI_LtV(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByPFCI_PPVI_LtV(0L, 0L, 0);
	}

	@Test
	public void testCountByPFCI_PPVI_N_V() throws Exception {
		_persistence.countByPFCI_PPVI_N_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByPFCI_PPVI_N_V(0L, 0L, "null", 0);

		_persistence.countByPFCI_PPVI_N_V(0L, 0L, (String)null, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		PatcherFixPack existingPatcherFixPack = _persistence.findByPrimaryKey(
			newPatcherFixPack.getPrimaryKey());

		Assert.assertEquals(existingPatcherFixPack, newPatcherFixPack);
	}

	@Test(expected = NoSuchPatcherFixPackException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherFixPack> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherFixPack", "mvccVersion", true,
			"patcherFixPackId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"patcherBuildId", true, "patcherFixComponentId", true,
			"patcherProjectVersionId", true, "name", true, "releasedDate", true,
			"requirements", true, "version", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		PatcherFixPack existingPatcherFixPack = _persistence.fetchByPrimaryKey(
			newPatcherFixPack.getPrimaryKey());

		Assert.assertEquals(existingPatcherFixPack, newPatcherFixPack);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixPack missingPatcherFixPack = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingPatcherFixPack);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherFixPack newPatcherFixPack1 = addPatcherFixPack();
		PatcherFixPack newPatcherFixPack2 = addPatcherFixPack();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixPack1.getPrimaryKey());
		primaryKeys.add(newPatcherFixPack2.getPrimaryKey());

		Map<Serializable, PatcherFixPack> patcherFixPacks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherFixPacks.size());
		Assert.assertEquals(
			newPatcherFixPack1,
			patcherFixPacks.get(newPatcherFixPack1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherFixPack2,
			patcherFixPacks.get(newPatcherFixPack2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherFixPack> patcherFixPacks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixPacks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixPack.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherFixPack> patcherFixPacks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixPacks.size());
		Assert.assertEquals(
			newPatcherFixPack,
			patcherFixPacks.get(newPatcherFixPack.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherFixPack> patcherFixPacks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixPacks.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixPack.getPrimaryKey());

		Map<Serializable, PatcherFixPack> patcherFixPacks =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixPacks.size());
		Assert.assertEquals(
			newPatcherFixPack,
			patcherFixPacks.get(newPatcherFixPack.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherFixPackLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<PatcherFixPack>() {

				@Override
				public void performAction(PatcherFixPack patcherFixPack) {
					Assert.assertNotNull(patcherFixPack);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixPack.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixPackId", newPatcherFixPack.getPatcherFixPackId()));

		List<PatcherFixPack> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherFixPack existingPatcherFixPack = result.get(0);

		Assert.assertEquals(existingPatcherFixPack, newPatcherFixPack);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixPack.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixPackId", RandomTestUtil.nextLong()));

		List<PatcherFixPack> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixPack.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixPackId"));

		Object newPatcherFixPackId = newPatcherFixPack.getPatcherFixPackId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixPackId", new Object[] {newPatcherFixPackId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherFixPackId = result.get(0);

		Assert.assertEquals(existingPatcherFixPackId, newPatcherFixPackId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixPack.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixPackId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixPackId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newPatcherFixPack.getPrimaryKey()));
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

		PatcherFixPack newPatcherFixPack = addPatcherFixPack();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixPack.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixPackId", newPatcherFixPack.getPatcherFixPackId()));

		List<PatcherFixPack> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(PatcherFixPack patcherFixPack) {
		Assert.assertEquals(
			Long.valueOf(patcherFixPack.getPatcherBuildId()),
			ReflectionTestUtil.<Long>invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "patcherBuildId"));

		Assert.assertEquals(
			Long.valueOf(patcherFixPack.getPatcherProjectVersionId()),
			ReflectionTestUtil.<Long>invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "patcherProjectVersionId"));
		Assert.assertEquals(
			patcherFixPack.getName(),
			ReflectionTestUtil.invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(patcherFixPack.getPatcherFixComponentId()),
			ReflectionTestUtil.<Long>invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "patcherFixComponentId"));
		Assert.assertEquals(
			Long.valueOf(patcherFixPack.getPatcherProjectVersionId()),
			ReflectionTestUtil.<Long>invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "patcherProjectVersionId"));
		Assert.assertEquals(
			patcherFixPack.getName(),
			ReflectionTestUtil.invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			Integer.valueOf(patcherFixPack.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				patcherFixPack, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected PatcherFixPack addPatcherFixPack() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixPack patcherFixPack = _persistence.create(pk);

		patcherFixPack.setMvccVersion(RandomTestUtil.nextLong());

		patcherFixPack.setCompanyId(RandomTestUtil.nextLong());

		patcherFixPack.setUserId(RandomTestUtil.nextLong());

		patcherFixPack.setUserName(RandomTestUtil.randomString());

		patcherFixPack.setCreateDate(RandomTestUtil.nextDate());

		patcherFixPack.setModifiedDate(RandomTestUtil.nextDate());

		patcherFixPack.setPatcherBuildId(RandomTestUtil.nextLong());

		patcherFixPack.setPatcherFixComponentId(RandomTestUtil.nextLong());

		patcherFixPack.setPatcherProjectVersionId(RandomTestUtil.nextLong());

		patcherFixPack.setName(RandomTestUtil.randomString());

		patcherFixPack.setReleasedDate(RandomTestUtil.nextDate());

		patcherFixPack.setRequirements(RandomTestUtil.randomString());

		patcherFixPack.setVersion(RandomTestUtil.nextInt());

		patcherFixPack.setStatus(RandomTestUtil.nextInt());

		_patcherFixPacks.add(_persistence.update(patcherFixPack));

		return patcherFixPack;
	}

	private List<PatcherFixPack> _patcherFixPacks =
		new ArrayList<PatcherFixPack>();
	private PatcherFixPackPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}