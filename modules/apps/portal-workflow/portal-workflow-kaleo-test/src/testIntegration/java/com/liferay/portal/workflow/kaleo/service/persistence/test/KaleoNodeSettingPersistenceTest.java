/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.workflow.kaleo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.workflow.kaleo.exception.NoSuchNodeSettingException;
import com.liferay.portal.workflow.kaleo.model.KaleoNodeSetting;
import com.liferay.portal.workflow.kaleo.service.KaleoNodeSettingLocalServiceUtil;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodeSettingPersistence;
import com.liferay.portal.workflow.kaleo.service.persistence.KaleoNodeSettingUtil;

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
public class KaleoNodeSettingPersistenceTest {

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
		_persistence = KaleoNodeSettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<KaleoNodeSetting> iterator = _kaleoNodeSettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNodeSetting kaleoNodeSetting = _persistence.create(pk);

		Assert.assertNotNull(kaleoNodeSetting);

		Assert.assertEquals(kaleoNodeSetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		_persistence.remove(newKaleoNodeSetting);

		KaleoNodeSetting existingKaleoNodeSetting =
			_persistence.fetchByPrimaryKey(newKaleoNodeSetting.getPrimaryKey());

		Assert.assertNull(existingKaleoNodeSetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addKaleoNodeSetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		newKaleoNodeSetting.setCtCollectionId(RandomTestUtil.nextLong());

		newKaleoNodeSetting.setCompanyId(RandomTestUtil.nextLong());

		newKaleoNodeSetting.setUserId(RandomTestUtil.nextLong());

		newKaleoNodeSetting.setUserName(RandomTestUtil.randomString());

		newKaleoNodeSetting.setCreateDate(RandomTestUtil.nextDate());

		newKaleoNodeSetting.setModifiedDate(RandomTestUtil.nextDate());

		newKaleoNodeSetting.setKaleoNodeId(RandomTestUtil.nextLong());

		newKaleoNodeSetting.setName(RandomTestUtil.randomString());

		newKaleoNodeSetting.setValue(RandomTestUtil.randomString());

		newKaleoNodeSetting = _persistence.update(newKaleoNodeSetting);

		_kaleoNodeSettings.add(newKaleoNodeSetting);

		KaleoNodeSetting existingKaleoNodeSetting =
			_persistence.findByPrimaryKey(newKaleoNodeSetting.getPrimaryKey());

		Assert.assertEquals(
			existingKaleoNodeSetting.getMvccVersion(),
			newKaleoNodeSetting.getMvccVersion());
		Assert.assertEquals(
			existingKaleoNodeSetting.getCtCollectionId(),
			newKaleoNodeSetting.getCtCollectionId());
		Assert.assertEquals(
			existingKaleoNodeSetting.getKaleoNodeSettingId(),
			newKaleoNodeSetting.getKaleoNodeSettingId());
		Assert.assertEquals(
			existingKaleoNodeSetting.getCompanyId(),
			newKaleoNodeSetting.getCompanyId());
		Assert.assertEquals(
			existingKaleoNodeSetting.getUserId(),
			newKaleoNodeSetting.getUserId());
		Assert.assertEquals(
			existingKaleoNodeSetting.getUserName(),
			newKaleoNodeSetting.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoNodeSetting.getCreateDate()),
			Time.getShortTimestamp(newKaleoNodeSetting.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingKaleoNodeSetting.getModifiedDate()),
			Time.getShortTimestamp(newKaleoNodeSetting.getModifiedDate()));
		Assert.assertEquals(
			existingKaleoNodeSetting.getKaleoNodeId(),
			newKaleoNodeSetting.getKaleoNodeId());
		Assert.assertEquals(
			existingKaleoNodeSetting.getName(), newKaleoNodeSetting.getName());
		Assert.assertEquals(
			existingKaleoNodeSetting.getValue(),
			newKaleoNodeSetting.getValue());
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		KaleoNodeSetting existingKaleoNodeSetting =
			_persistence.findByPrimaryKey(newKaleoNodeSetting.getPrimaryKey());

		Assert.assertEquals(existingKaleoNodeSetting, newKaleoNodeSetting);
	}

	@Test(expected = NoSuchNodeSettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<KaleoNodeSetting> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"KaleoNodeSetting", "mvccVersion", true, "ctCollectionId", true,
			"kaleoNodeSettingId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"kaleoNodeId", true, "name", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		KaleoNodeSetting existingKaleoNodeSetting =
			_persistence.fetchByPrimaryKey(newKaleoNodeSetting.getPrimaryKey());

		Assert.assertEquals(existingKaleoNodeSetting, newKaleoNodeSetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNodeSetting missingKaleoNodeSetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingKaleoNodeSetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		KaleoNodeSetting newKaleoNodeSetting1 = addKaleoNodeSetting();
		KaleoNodeSetting newKaleoNodeSetting2 = addKaleoNodeSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNodeSetting1.getPrimaryKey());
		primaryKeys.add(newKaleoNodeSetting2.getPrimaryKey());

		Map<Serializable, KaleoNodeSetting> kaleoNodeSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, kaleoNodeSettings.size());
		Assert.assertEquals(
			newKaleoNodeSetting1,
			kaleoNodeSettings.get(newKaleoNodeSetting1.getPrimaryKey()));
		Assert.assertEquals(
			newKaleoNodeSetting2,
			kaleoNodeSettings.get(newKaleoNodeSetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, KaleoNodeSetting> kaleoNodeSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoNodeSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNodeSetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, KaleoNodeSetting> kaleoNodeSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoNodeSettings.size());
		Assert.assertEquals(
			newKaleoNodeSetting,
			kaleoNodeSettings.get(newKaleoNodeSetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, KaleoNodeSetting> kaleoNodeSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(kaleoNodeSettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newKaleoNodeSetting.getPrimaryKey());

		Map<Serializable, KaleoNodeSetting> kaleoNodeSettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, kaleoNodeSettings.size());
		Assert.assertEquals(
			newKaleoNodeSetting,
			kaleoNodeSettings.get(newKaleoNodeSetting.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			KaleoNodeSettingLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<KaleoNodeSetting>() {

				@Override
				public void performAction(KaleoNodeSetting kaleoNodeSetting) {
					Assert.assertNotNull(kaleoNodeSetting);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNodeSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoNodeSettingId",
				newKaleoNodeSetting.getKaleoNodeSettingId()));

		List<KaleoNodeSetting> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		KaleoNodeSetting existingKaleoNodeSetting = result.get(0);

		Assert.assertEquals(existingKaleoNodeSetting, newKaleoNodeSetting);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNodeSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoNodeSettingId", RandomTestUtil.nextLong()));

		List<KaleoNodeSetting> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNodeSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kaleoNodeSettingId"));

		Object newKaleoNodeSettingId =
			newKaleoNodeSetting.getKaleoNodeSettingId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kaleoNodeSettingId", new Object[] {newKaleoNodeSettingId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingKaleoNodeSettingId = result.get(0);

		Assert.assertEquals(existingKaleoNodeSettingId, newKaleoNodeSettingId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNodeSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("kaleoNodeSettingId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"kaleoNodeSettingId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newKaleoNodeSetting.getPrimaryKey()));
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

		KaleoNodeSetting newKaleoNodeSetting = addKaleoNodeSetting();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			KaleoNodeSetting.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"kaleoNodeSettingId",
				newKaleoNodeSetting.getKaleoNodeSettingId()));

		List<KaleoNodeSetting> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(KaleoNodeSetting kaleoNodeSetting) {
		Assert.assertEquals(
			Long.valueOf(kaleoNodeSetting.getKaleoNodeId()),
			ReflectionTestUtil.<Long>invoke(
				kaleoNodeSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "kaleoNodeId"));
		Assert.assertEquals(
			kaleoNodeSetting.getName(),
			ReflectionTestUtil.invoke(
				kaleoNodeSetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected KaleoNodeSetting addKaleoNodeSetting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		KaleoNodeSetting kaleoNodeSetting = _persistence.create(pk);

		kaleoNodeSetting.setCtCollectionId(RandomTestUtil.nextLong());

		kaleoNodeSetting.setCompanyId(RandomTestUtil.nextLong());

		kaleoNodeSetting.setUserId(RandomTestUtil.nextLong());

		kaleoNodeSetting.setUserName(RandomTestUtil.randomString());

		kaleoNodeSetting.setCreateDate(RandomTestUtil.nextDate());

		kaleoNodeSetting.setModifiedDate(RandomTestUtil.nextDate());

		kaleoNodeSetting.setKaleoNodeId(RandomTestUtil.nextLong());

		kaleoNodeSetting.setName(RandomTestUtil.randomString());

		kaleoNodeSetting.setValue(RandomTestUtil.randomString());

		_kaleoNodeSettings.add(_persistence.update(kaleoNodeSetting));

		return kaleoNodeSetting;
	}

	private List<KaleoNodeSetting> _kaleoNodeSettings =
		new ArrayList<KaleoNodeSetting>();
	private KaleoNodeSettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1688448557