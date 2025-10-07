/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.depot.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.depot.exception.NoSuchEntryGroupRelException;
import com.liferay.depot.model.DepotEntryGroupRel;
import com.liferay.depot.service.DepotEntryGroupRelLocalServiceUtil;
import com.liferay.depot.service.persistence.DepotEntryGroupRelPersistence;
import com.liferay.depot.service.persistence.DepotEntryGroupRelUtil;
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
public class DepotEntryGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.depot.service"));

	@Before
	public void setUp() {
		_persistence = DepotEntryGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DepotEntryGroupRel> iterator = _depotEntryGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryGroupRel depotEntryGroupRel = _persistence.create(pk);

		Assert.assertNotNull(depotEntryGroupRel);

		Assert.assertEquals(depotEntryGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		_persistence.remove(newDepotEntryGroupRel);

		DepotEntryGroupRel existingDepotEntryGroupRel =
			_persistence.fetchByPrimaryKey(
				newDepotEntryGroupRel.getPrimaryKey());

		Assert.assertNull(existingDepotEntryGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDepotEntryGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryGroupRel newDepotEntryGroupRel = _persistence.create(pk);

		newDepotEntryGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setCtCollectionId(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setUuid(RandomTestUtil.randomString());

		newDepotEntryGroupRel.setGroupId(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setCompanyId(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setUserId(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setUserName(RandomTestUtil.randomString());

		newDepotEntryGroupRel.setCreateDate(RandomTestUtil.nextDate());

		newDepotEntryGroupRel.setModifiedDate(RandomTestUtil.nextDate());

		newDepotEntryGroupRel.setDdmStructuresAvailable(
			RandomTestUtil.randomBoolean());

		newDepotEntryGroupRel.setDepotEntryId(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setSearchable(RandomTestUtil.randomBoolean());

		newDepotEntryGroupRel.setToGroupId(RandomTestUtil.nextLong());

		newDepotEntryGroupRel.setType(RandomTestUtil.nextInt());

		newDepotEntryGroupRel.setLastPublishDate(RandomTestUtil.nextDate());

		_depotEntryGroupRels.add(_persistence.update(newDepotEntryGroupRel));

		DepotEntryGroupRel existingDepotEntryGroupRel =
			_persistence.findByPrimaryKey(
				newDepotEntryGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingDepotEntryGroupRel.getMvccVersion(),
			newDepotEntryGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getCtCollectionId(),
			newDepotEntryGroupRel.getCtCollectionId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getUuid(),
			newDepotEntryGroupRel.getUuid());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getDepotEntryGroupRelId(),
			newDepotEntryGroupRel.getDepotEntryGroupRelId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getGroupId(),
			newDepotEntryGroupRel.getGroupId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getCompanyId(),
			newDepotEntryGroupRel.getCompanyId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getUserId(),
			newDepotEntryGroupRel.getUserId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getUserName(),
			newDepotEntryGroupRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDepotEntryGroupRel.getCreateDate()),
			Time.getShortTimestamp(newDepotEntryGroupRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDepotEntryGroupRel.getModifiedDate()),
			Time.getShortTimestamp(newDepotEntryGroupRel.getModifiedDate()));
		Assert.assertEquals(
			existingDepotEntryGroupRel.isDdmStructuresAvailable(),
			newDepotEntryGroupRel.isDdmStructuresAvailable());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getDepotEntryId(),
			newDepotEntryGroupRel.getDepotEntryId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.isSearchable(),
			newDepotEntryGroupRel.isSearchable());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getToGroupId(),
			newDepotEntryGroupRel.getToGroupId());
		Assert.assertEquals(
			existingDepotEntryGroupRel.getType(),
			newDepotEntryGroupRel.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingDepotEntryGroupRel.getLastPublishDate()),
			Time.getShortTimestamp(newDepotEntryGroupRel.getLastPublishDate()));
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByDepotEntryId() throws Exception {
		_persistence.countByDepotEntryId(RandomTestUtil.nextLong());

		_persistence.countByDepotEntryId(0L);
	}

	@Test
	public void testCountByToGroupId() throws Exception {
		_persistence.countByToGroupId(RandomTestUtil.nextLong());

		_persistence.countByToGroupId(0L);
	}

	@Test
	public void testCountByDDMSA_TGI() throws Exception {
		_persistence.countByDDMSA_TGI(
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextLong());

		_persistence.countByDDMSA_TGI(RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByD_TGI() throws Exception {
		_persistence.countByD_TGI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByD_TGI(0L, 0L);
	}

	@Test
	public void testCountByS_TGI() throws Exception {
		_persistence.countByS_TGI(
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextLong());

		_persistence.countByS_TGI(RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByTGI_T() throws Exception {
		_persistence.countByTGI_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByTGI_T(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		DepotEntryGroupRel existingDepotEntryGroupRel =
			_persistence.findByPrimaryKey(
				newDepotEntryGroupRel.getPrimaryKey());

		Assert.assertEquals(existingDepotEntryGroupRel, newDepotEntryGroupRel);
	}

	@Test(expected = NoSuchEntryGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DepotEntryGroupRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DepotEntryGroupRel", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "depotEntryGroupRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "ddmStructuresAvailable", true,
			"depotEntryId", true, "searchable", true, "toGroupId", true, "type",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		DepotEntryGroupRel existingDepotEntryGroupRel =
			_persistence.fetchByPrimaryKey(
				newDepotEntryGroupRel.getPrimaryKey());

		Assert.assertEquals(existingDepotEntryGroupRel, newDepotEntryGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryGroupRel missingDepotEntryGroupRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDepotEntryGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DepotEntryGroupRel newDepotEntryGroupRel1 = addDepotEntryGroupRel();
		DepotEntryGroupRel newDepotEntryGroupRel2 = addDepotEntryGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotEntryGroupRel1.getPrimaryKey());
		primaryKeys.add(newDepotEntryGroupRel2.getPrimaryKey());

		Map<Serializable, DepotEntryGroupRel> depotEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, depotEntryGroupRels.size());
		Assert.assertEquals(
			newDepotEntryGroupRel1,
			depotEntryGroupRels.get(newDepotEntryGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newDepotEntryGroupRel2,
			depotEntryGroupRels.get(newDepotEntryGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DepotEntryGroupRel> depotEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(depotEntryGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotEntryGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DepotEntryGroupRel> depotEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, depotEntryGroupRels.size());
		Assert.assertEquals(
			newDepotEntryGroupRel,
			depotEntryGroupRels.get(newDepotEntryGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DepotEntryGroupRel> depotEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(depotEntryGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDepotEntryGroupRel.getPrimaryKey());

		Map<Serializable, DepotEntryGroupRel> depotEntryGroupRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, depotEntryGroupRels.size());
		Assert.assertEquals(
			newDepotEntryGroupRel,
			depotEntryGroupRels.get(newDepotEntryGroupRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DepotEntryGroupRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<DepotEntryGroupRel>() {

				@Override
				public void performAction(
					DepotEntryGroupRel depotEntryGroupRel) {

					Assert.assertNotNull(depotEntryGroupRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"depotEntryGroupRelId",
				newDepotEntryGroupRel.getDepotEntryGroupRelId()));

		List<DepotEntryGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DepotEntryGroupRel existingDepotEntryGroupRel = result.get(0);

		Assert.assertEquals(existingDepotEntryGroupRel, newDepotEntryGroupRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"depotEntryGroupRelId", RandomTestUtil.nextLong()));

		List<DepotEntryGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("depotEntryGroupRelId"));

		Object newDepotEntryGroupRelId =
			newDepotEntryGroupRel.getDepotEntryGroupRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"depotEntryGroupRelId",
				new Object[] {newDepotEntryGroupRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingDepotEntryGroupRelId = result.get(0);

		Assert.assertEquals(
			existingDepotEntryGroupRelId, newDepotEntryGroupRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("depotEntryGroupRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"depotEntryGroupRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newDepotEntryGroupRel.getPrimaryKey()));
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

		DepotEntryGroupRel newDepotEntryGroupRel = addDepotEntryGroupRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DepotEntryGroupRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"depotEntryGroupRelId",
				newDepotEntryGroupRel.getDepotEntryGroupRelId()));

		List<DepotEntryGroupRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(DepotEntryGroupRel depotEntryGroupRel) {
		Assert.assertEquals(
			depotEntryGroupRel.getUuid(),
			ReflectionTestUtil.invoke(
				depotEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(depotEntryGroupRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				depotEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(depotEntryGroupRel.getDepotEntryId()),
			ReflectionTestUtil.<Long>invoke(
				depotEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "depotEntryId"));
		Assert.assertEquals(
			Long.valueOf(depotEntryGroupRel.getToGroupId()),
			ReflectionTestUtil.<Long>invoke(
				depotEntryGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "toGroupId"));
	}

	protected DepotEntryGroupRel addDepotEntryGroupRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DepotEntryGroupRel depotEntryGroupRel = _persistence.create(pk);

		depotEntryGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		depotEntryGroupRel.setCtCollectionId(RandomTestUtil.nextLong());

		depotEntryGroupRel.setUuid(RandomTestUtil.randomString());

		depotEntryGroupRel.setGroupId(RandomTestUtil.nextLong());

		depotEntryGroupRel.setCompanyId(RandomTestUtil.nextLong());

		depotEntryGroupRel.setUserId(RandomTestUtil.nextLong());

		depotEntryGroupRel.setUserName(RandomTestUtil.randomString());

		depotEntryGroupRel.setCreateDate(RandomTestUtil.nextDate());

		depotEntryGroupRel.setModifiedDate(RandomTestUtil.nextDate());

		depotEntryGroupRel.setDdmStructuresAvailable(
			RandomTestUtil.randomBoolean());

		depotEntryGroupRel.setDepotEntryId(RandomTestUtil.nextLong());

		depotEntryGroupRel.setSearchable(RandomTestUtil.randomBoolean());

		depotEntryGroupRel.setToGroupId(RandomTestUtil.nextLong());

		depotEntryGroupRel.setType(RandomTestUtil.nextInt());

		depotEntryGroupRel.setLastPublishDate(RandomTestUtil.nextDate());

		_depotEntryGroupRels.add(_persistence.update(depotEntryGroupRel));

		return depotEntryGroupRel;
	}

	private List<DepotEntryGroupRel> _depotEntryGroupRels =
		new ArrayList<DepotEntryGroupRel>();
	private DepotEntryGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}