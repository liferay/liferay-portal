/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.dynamic.data.mapping.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.dynamic.data.mapping.exception.NoSuchStructureException;
import com.liferay.dynamic.data.mapping.model.DDMStructure;
import com.liferay.dynamic.data.mapping.service.DDMStructureLocalServiceUtil;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructurePersistence;
import com.liferay.dynamic.data.mapping.service.persistence.DDMStructureUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class DDMStructurePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.dynamic.data.mapping.service"));

	@Before
	public void setUp() {
		_persistence = DDMStructureUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<DDMStructure> iterator = _ddmStructures.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructure ddmStructure = _persistence.create(pk);

		Assert.assertNotNull(ddmStructure);

		Assert.assertEquals(ddmStructure.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		_persistence.remove(newDDMStructure);

		DDMStructure existingDDMStructure = _persistence.fetchByPrimaryKey(
			newDDMStructure.getPrimaryKey());

		Assert.assertNull(existingDDMStructure);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addDDMStructure();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructure newDDMStructure = _persistence.create(pk);

		newDDMStructure.setMvccVersion(RandomTestUtil.nextLong());

		newDDMStructure.setCtCollectionId(RandomTestUtil.nextLong());

		newDDMStructure.setUuid(RandomTestUtil.randomString());

		newDDMStructure.setExternalReferenceCode(RandomTestUtil.randomString());

		newDDMStructure.setGroupId(RandomTestUtil.nextLong());

		newDDMStructure.setCompanyId(RandomTestUtil.nextLong());

		newDDMStructure.setUserId(RandomTestUtil.nextLong());

		newDDMStructure.setUserName(RandomTestUtil.randomString());

		newDDMStructure.setVersionUserId(RandomTestUtil.nextLong());

		newDDMStructure.setVersionUserName(RandomTestUtil.randomString());

		newDDMStructure.setCreateDate(RandomTestUtil.nextDate());

		newDDMStructure.setModifiedDate(RandomTestUtil.nextDate());

		newDDMStructure.setParentStructureId(RandomTestUtil.nextLong());

		newDDMStructure.setClassNameId(RandomTestUtil.nextLong());

		newDDMStructure.setStructureKey(RandomTestUtil.randomString());

		newDDMStructure.setVersion(RandomTestUtil.randomString());

		newDDMStructure.setName(RandomTestUtil.randomString());

		newDDMStructure.setDescription(RandomTestUtil.randomString());

		newDDMStructure.setDefinition(RandomTestUtil.randomString());

		newDDMStructure.setStorageType(RandomTestUtil.randomString());

		newDDMStructure.setType(RandomTestUtil.nextInt());

		newDDMStructure.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmStructures.add(_persistence.update(newDDMStructure));

		DDMStructure existingDDMStructure = _persistence.findByPrimaryKey(
			newDDMStructure.getPrimaryKey());

		Assert.assertEquals(
			existingDDMStructure.getMvccVersion(),
			newDDMStructure.getMvccVersion());
		Assert.assertEquals(
			existingDDMStructure.getCtCollectionId(),
			newDDMStructure.getCtCollectionId());
		Assert.assertEquals(
			existingDDMStructure.getUuid(), newDDMStructure.getUuid());
		Assert.assertEquals(
			existingDDMStructure.getExternalReferenceCode(),
			newDDMStructure.getExternalReferenceCode());
		Assert.assertEquals(
			existingDDMStructure.getStructureId(),
			newDDMStructure.getStructureId());
		Assert.assertEquals(
			existingDDMStructure.getGroupId(), newDDMStructure.getGroupId());
		Assert.assertEquals(
			existingDDMStructure.getCompanyId(),
			newDDMStructure.getCompanyId());
		Assert.assertEquals(
			existingDDMStructure.getUserId(), newDDMStructure.getUserId());
		Assert.assertEquals(
			existingDDMStructure.getUserName(), newDDMStructure.getUserName());
		Assert.assertEquals(
			existingDDMStructure.getVersionUserId(),
			newDDMStructure.getVersionUserId());
		Assert.assertEquals(
			existingDDMStructure.getVersionUserName(),
			newDDMStructure.getVersionUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMStructure.getCreateDate()),
			Time.getShortTimestamp(newDDMStructure.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMStructure.getModifiedDate()),
			Time.getShortTimestamp(newDDMStructure.getModifiedDate()));
		Assert.assertEquals(
			existingDDMStructure.getParentStructureId(),
			newDDMStructure.getParentStructureId());
		Assert.assertEquals(
			existingDDMStructure.getClassNameId(),
			newDDMStructure.getClassNameId());
		Assert.assertEquals(
			existingDDMStructure.getStructureKey(),
			newDDMStructure.getStructureKey());
		Assert.assertEquals(
			existingDDMStructure.getVersion(), newDDMStructure.getVersion());
		Assert.assertEquals(
			existingDDMStructure.getName(), newDDMStructure.getName());
		Assert.assertEquals(
			existingDDMStructure.getDescription(),
			newDDMStructure.getDescription());
		Assert.assertEquals(
			existingDDMStructure.getDefinition(),
			newDDMStructure.getDefinition());
		Assert.assertEquals(
			existingDDMStructure.getStorageType(),
			newDDMStructure.getStorageType());
		Assert.assertEquals(
			existingDDMStructure.getType(), newDDMStructure.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(existingDDMStructure.getLastPublishDate()),
			Time.getShortTimestamp(newDDMStructure.getLastPublishDate()));
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByParentStructureId() throws Exception {
		_persistence.countByParentStructureId(RandomTestUtil.nextLong());

		_persistence.countByParentStructureId(0L);
	}

	@Test
	public void testCountByStructureKey() throws Exception {
		_persistence.countByStructureKey("");

		_persistence.countByStructureKey("null");

		_persistence.countByStructureKey((String)null);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_P(0L, 0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_CArrayable() throws Exception {
		_persistence.countByG_C(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByERC_G_C() throws Exception {
		_persistence.countByERC_G_C(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByERC_G_C("null", 0L, 0L);

		_persistence.countByERC_G_C((String)null, 0L, 0L);
	}

	@Test
	public void testCountByG_C_S() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_C_S(0L, 0L, "null");

		_persistence.countByG_C_S(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_N_D() throws Exception {
		_persistence.countByG_N_D(RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_N_D(0L, "null", "null");

		_persistence.countByG_N_D(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_C_N_D() throws Exception {
		_persistence.countByG_C_N_D(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_C_N_D(0L, 0L, "null", "null");

		_persistence.countByG_C_N_D(0L, 0L, (String)null, (String)null);
	}

	@Test
	public void testCountByG_C_N_DArrayable() throws Exception {
		_persistence.countByG_C_N_D(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			RandomTestUtil.randomString());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		DDMStructure existingDDMStructure = _persistence.findByPrimaryKey(
			newDDMStructure.getPrimaryKey());

		Assert.assertEquals(existingDDMStructure, newDDMStructure);
	}

	@Test(expected = NoSuchStructureException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<DDMStructure> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"DDMStructure", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "structureId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"versionUserId", true, "versionUserName", true, "createDate", true,
			"modifiedDate", true, "parentStructureId", true, "classNameId",
			true, "structureKey", true, "version", true, "name", true,
			"storageType", true, "type", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		DDMStructure existingDDMStructure = _persistence.fetchByPrimaryKey(
			newDDMStructure.getPrimaryKey());

		Assert.assertEquals(existingDDMStructure, newDDMStructure);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructure missingDDMStructure = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingDDMStructure);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		DDMStructure newDDMStructure1 = addDDMStructure();
		DDMStructure newDDMStructure2 = addDDMStructure();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructure1.getPrimaryKey());
		primaryKeys.add(newDDMStructure2.getPrimaryKey());

		Map<Serializable, DDMStructure> ddmStructures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, ddmStructures.size());
		Assert.assertEquals(
			newDDMStructure1,
			ddmStructures.get(newDDMStructure1.getPrimaryKey()));
		Assert.assertEquals(
			newDDMStructure2,
			ddmStructures.get(newDDMStructure2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, DDMStructure> ddmStructures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructures.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		DDMStructure newDDMStructure = addDDMStructure();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructure.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, DDMStructure> ddmStructures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructures.size());
		Assert.assertEquals(
			newDDMStructure,
			ddmStructures.get(newDDMStructure.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, DDMStructure> ddmStructures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(ddmStructures.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newDDMStructure.getPrimaryKey());

		Map<Serializable, DDMStructure> ddmStructures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, ddmStructures.size());
		Assert.assertEquals(
			newDDMStructure,
			ddmStructures.get(newDDMStructure.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			DDMStructureLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<DDMStructure>() {

				@Override
				public void performAction(DDMStructure ddmStructure) {
					Assert.assertNotNull(ddmStructure);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructure.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"structureId", newDDMStructure.getStructureId()));

		List<DDMStructure> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		DDMStructure existingDDMStructure = result.get(0);

		Assert.assertEquals(existingDDMStructure, newDDMStructure);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructure.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"structureId", RandomTestUtil.nextLong()));

		List<DDMStructure> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructure.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("structureId"));

		Object newStructureId = newDDMStructure.getStructureId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"structureId", new Object[] {newStructureId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingStructureId = result.get(0);

		Assert.assertEquals(existingStructureId, newStructureId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructure.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("structureId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"structureId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		DDMStructure newDDMStructure = addDDMStructure();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newDDMStructure.getPrimaryKey()));
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

		DDMStructure newDDMStructure = addDDMStructure();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			DDMStructure.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"structureId", newDDMStructure.getStructureId()));

		List<DDMStructure> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(DDMStructure ddmStructure) {
		Assert.assertEquals(
			ddmStructure.getUuid(),
			ReflectionTestUtil.invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(ddmStructure.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			ddmStructure.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(ddmStructure.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(ddmStructure.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));

		Assert.assertEquals(
			Long.valueOf(ddmStructure.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(ddmStructure.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			ddmStructure.getStructureKey(),
			ReflectionTestUtil.invoke(
				ddmStructure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "structureKey"));
	}

	protected DDMStructure addDDMStructure() throws Exception {
		long pk = RandomTestUtil.nextLong();

		DDMStructure ddmStructure = _persistence.create(pk);

		ddmStructure.setMvccVersion(RandomTestUtil.nextLong());

		ddmStructure.setCtCollectionId(RandomTestUtil.nextLong());

		ddmStructure.setUuid(RandomTestUtil.randomString());

		ddmStructure.setExternalReferenceCode(RandomTestUtil.randomString());

		ddmStructure.setGroupId(RandomTestUtil.nextLong());

		ddmStructure.setCompanyId(RandomTestUtil.nextLong());

		ddmStructure.setUserId(RandomTestUtil.nextLong());

		ddmStructure.setUserName(RandomTestUtil.randomString());

		ddmStructure.setVersionUserId(RandomTestUtil.nextLong());

		ddmStructure.setVersionUserName(RandomTestUtil.randomString());

		ddmStructure.setCreateDate(RandomTestUtil.nextDate());

		ddmStructure.setModifiedDate(RandomTestUtil.nextDate());

		ddmStructure.setParentStructureId(RandomTestUtil.nextLong());

		ddmStructure.setClassNameId(RandomTestUtil.nextLong());

		ddmStructure.setStructureKey(RandomTestUtil.randomString());

		ddmStructure.setVersion(RandomTestUtil.randomString());

		ddmStructure.setName(RandomTestUtil.randomString());

		ddmStructure.setDescription(RandomTestUtil.randomString());

		ddmStructure.setDefinition(RandomTestUtil.randomString());

		ddmStructure.setStorageType(RandomTestUtil.randomString());

		ddmStructure.setType(RandomTestUtil.nextInt());

		ddmStructure.setLastPublishDate(RandomTestUtil.nextDate());

		_ddmStructures.add(_persistence.update(ddmStructure));

		return ddmStructure;
	}

	private List<DDMStructure> _ddmStructures = new ArrayList<DDMStructure>();
	private DDMStructurePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}