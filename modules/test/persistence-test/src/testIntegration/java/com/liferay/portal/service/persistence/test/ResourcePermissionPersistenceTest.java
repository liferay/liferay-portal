/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchResourcePermissionException;
import com.liferay.portal.kernel.model.ResourcePermission;
import com.liferay.portal.kernel.service.ResourcePermissionLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionPersistence;
import com.liferay.portal.kernel.service.persistence.ResourcePermissionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class ResourcePermissionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ResourcePermissionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ResourcePermission> iterator = _resourcePermissions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourcePermission resourcePermission = _persistence.create(pk);

		Assert.assertNotNull(resourcePermission);

		Assert.assertEquals(resourcePermission.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		_persistence.remove(newResourcePermission);

		ResourcePermission existingResourcePermission =
			_persistence.fetchByPrimaryKey(
				newResourcePermission.getPrimaryKey());

		Assert.assertNull(existingResourcePermission);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addResourcePermission();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourcePermission newResourcePermission = _persistence.create(pk);

		newResourcePermission.setMvccVersion(RandomTestUtil.nextLong());

		newResourcePermission.setCtCollectionId(RandomTestUtil.nextLong());

		newResourcePermission.setCompanyId(RandomTestUtil.nextLong());

		newResourcePermission.setName(RandomTestUtil.randomString());

		newResourcePermission.setScope(RandomTestUtil.nextInt());

		newResourcePermission.setPrimKey(RandomTestUtil.randomString());

		newResourcePermission.setPrimKeyId(RandomTestUtil.nextLong());

		newResourcePermission.setRoleId(RandomTestUtil.nextLong());

		newResourcePermission.setOwnerId(RandomTestUtil.nextLong());

		newResourcePermission.setActionIds(RandomTestUtil.nextLong());

		newResourcePermission.setViewActionId(RandomTestUtil.randomBoolean());

		_resourcePermissions.add(_persistence.update(newResourcePermission));

		ResourcePermission existingResourcePermission =
			_persistence.findByPrimaryKey(
				newResourcePermission.getPrimaryKey());

		Assert.assertEquals(
			existingResourcePermission.getMvccVersion(),
			newResourcePermission.getMvccVersion());
		Assert.assertEquals(
			existingResourcePermission.getCtCollectionId(),
			newResourcePermission.getCtCollectionId());
		Assert.assertEquals(
			existingResourcePermission.getResourcePermissionId(),
			newResourcePermission.getResourcePermissionId());
		Assert.assertEquals(
			existingResourcePermission.getCompanyId(),
			newResourcePermission.getCompanyId());
		Assert.assertEquals(
			existingResourcePermission.getName(),
			newResourcePermission.getName());
		Assert.assertEquals(
			existingResourcePermission.getScope(),
			newResourcePermission.getScope());
		Assert.assertEquals(
			existingResourcePermission.getPrimKey(),
			newResourcePermission.getPrimKey());
		Assert.assertEquals(
			existingResourcePermission.getPrimKeyId(),
			newResourcePermission.getPrimKeyId());
		Assert.assertEquals(
			existingResourcePermission.getRoleId(),
			newResourcePermission.getRoleId());
		Assert.assertEquals(
			existingResourcePermission.getOwnerId(),
			newResourcePermission.getOwnerId());
		Assert.assertEquals(
			existingResourcePermission.getActionIds(),
			newResourcePermission.getActionIds());
		Assert.assertEquals(
			existingResourcePermission.isViewActionId(),
			newResourcePermission.isViewActionId());
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testCountByScope() throws Exception {
		_persistence.countByScope(RandomTestUtil.nextInt());

		_persistence.countByScope(0);
	}

	@Test
	public void testCountByScopeArrayable() throws Exception {
		_persistence.countByScope(new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByRoleId() throws Exception {
		_persistence.countByRoleId(RandomTestUtil.nextLong());

		_persistence.countByRoleId(0L);
	}

	@Test
	public void testCountByC_LikeP() throws Exception {
		_persistence.countByC_LikeP(RandomTestUtil.nextLong(), "");

		_persistence.countByC_LikeP(0L, "null");

		_persistence.countByC_LikeP(0L, (String)null);
	}

	@Test
	public void testCountByC_N_S() throws Exception {
		_persistence.countByC_N_S(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt());

		_persistence.countByC_N_S(0L, "null", 0);

		_persistence.countByC_N_S(0L, (String)null, 0);
	}

	@Test
	public void testCountByC_S_P() throws Exception {
		_persistence.countByC_S_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(), "");

		_persistence.countByC_S_P(0L, 0, "null");

		_persistence.countByC_S_P(0L, 0, (String)null);
	}

	@Test
	public void testCountByC_N_S_P() throws Exception {
		_persistence.countByC_N_S_P(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt(), "");

		_persistence.countByC_N_S_P(0L, "null", 0, "null");

		_persistence.countByC_N_S_P(0L, (String)null, 0, (String)null);
	}

	@Test
	public void testCountByC_N_S_PArrayable() throws Exception {
		_persistence.countByC_N_S_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			RandomTestUtil.nextInt(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testCountByC_N_S_R() throws Exception {
		_persistence.countByC_N_S_R(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong());

		_persistence.countByC_N_S_R(0L, "null", 0, 0L);

		_persistence.countByC_N_S_R(0L, (String)null, 0, 0L);
	}

	@Test
	public void testCountByC_N_S_P_R() throws Exception {
		_persistence.countByC_N_S_P_R(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt(), "",
			RandomTestUtil.nextLong());

		_persistence.countByC_N_S_P_R(0L, "null", 0, "null", 0L);

		_persistence.countByC_N_S_P_R(0L, (String)null, 0, (String)null, 0L);
	}

	@Test
	public void testCountByC_N_S_P_RArrayable() throws Exception {
		_persistence.countByC_N_S_P_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			RandomTestUtil.nextInt(), RandomTestUtil.randomString(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByC_N_S_R_V() throws Exception {
		_persistence.countByC_N_S_R_V(
			RandomTestUtil.nextLong(), "", RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_N_S_R_V(
			0L, "null", 0, 0L, RandomTestUtil.randomBoolean());

		_persistence.countByC_N_S_R_V(
			0L, (String)null, 0, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_N_S_R_VArrayable() throws Exception {
		_persistence.countByC_N_S_R_V(
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			RandomTestUtil.nextInt(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		ResourcePermission existingResourcePermission =
			_persistence.findByPrimaryKey(
				newResourcePermission.getPrimaryKey());

		Assert.assertEquals(existingResourcePermission, newResourcePermission);
	}

	@Test(expected = NoSuchResourcePermissionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ResourcePermission> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ResourcePermission", "mvccVersion", true, "ctCollectionId", true,
			"resourcePermissionId", true, "companyId", true, "name", true,
			"scope", true, "primKey", true, "primKeyId", true, "roleId", true,
			"ownerId", true, "actionIds", true, "viewActionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		ResourcePermission existingResourcePermission =
			_persistence.fetchByPrimaryKey(
				newResourcePermission.getPrimaryKey());

		Assert.assertEquals(existingResourcePermission, newResourcePermission);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourcePermission missingResourcePermission =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingResourcePermission);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ResourcePermission newResourcePermission1 = addResourcePermission();
		ResourcePermission newResourcePermission2 = addResourcePermission();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newResourcePermission1.getPrimaryKey());
		primaryKeys.add(newResourcePermission2.getPrimaryKey());

		Map<Serializable, ResourcePermission> resourcePermissions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, resourcePermissions.size());
		Assert.assertEquals(
			newResourcePermission1,
			resourcePermissions.get(newResourcePermission1.getPrimaryKey()));
		Assert.assertEquals(
			newResourcePermission2,
			resourcePermissions.get(newResourcePermission2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ResourcePermission> resourcePermissions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(resourcePermissions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ResourcePermission newResourcePermission = addResourcePermission();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newResourcePermission.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ResourcePermission> resourcePermissions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, resourcePermissions.size());
		Assert.assertEquals(
			newResourcePermission,
			resourcePermissions.get(newResourcePermission.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ResourcePermission> resourcePermissions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(resourcePermissions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newResourcePermission.getPrimaryKey());

		Map<Serializable, ResourcePermission> resourcePermissions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, resourcePermissions.size());
		Assert.assertEquals(
			newResourcePermission,
			resourcePermissions.get(newResourcePermission.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			ResourcePermissionLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<ResourcePermission>() {

				@Override
				public void performAction(
					ResourcePermission resourcePermission) {

					Assert.assertNotNull(resourcePermission);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ResourcePermission.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"resourcePermissionId",
				newResourcePermission.getResourcePermissionId()));

		List<ResourcePermission> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		ResourcePermission existingResourcePermission = result.get(0);

		Assert.assertEquals(existingResourcePermission, newResourcePermission);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ResourcePermission.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"resourcePermissionId", RandomTestUtil.nextLong()));

		List<ResourcePermission> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ResourcePermission.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("resourcePermissionId"));

		Object newResourcePermissionId =
			newResourcePermission.getResourcePermissionId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"resourcePermissionId",
				new Object[] {newResourcePermissionId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingResourcePermissionId = result.get(0);

		Assert.assertEquals(
			existingResourcePermissionId, newResourcePermissionId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ResourcePermission.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("resourcePermissionId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"resourcePermissionId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ResourcePermission newResourcePermission = addResourcePermission();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newResourcePermission.getPrimaryKey()));
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

		ResourcePermission newResourcePermission = addResourcePermission();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			ResourcePermission.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"resourcePermissionId",
				newResourcePermission.getResourcePermissionId()));

		List<ResourcePermission> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(ResourcePermission resourcePermission) {
		Assert.assertEquals(
			Long.valueOf(resourcePermission.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				resourcePermission, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			resourcePermission.getName(),
			ReflectionTestUtil.invoke(
				resourcePermission, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			Integer.valueOf(resourcePermission.getScope()),
			ReflectionTestUtil.<Integer>invoke(
				resourcePermission, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "scope"));
		Assert.assertEquals(
			resourcePermission.getPrimKey(),
			ReflectionTestUtil.invoke(
				resourcePermission, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "primKey"));
		Assert.assertEquals(
			Long.valueOf(resourcePermission.getRoleId()),
			ReflectionTestUtil.<Long>invoke(
				resourcePermission, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "roleId"));
	}

	protected ResourcePermission addResourcePermission() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourcePermission resourcePermission = _persistence.create(pk);

		resourcePermission.setMvccVersion(RandomTestUtil.nextLong());

		resourcePermission.setCtCollectionId(RandomTestUtil.nextLong());

		resourcePermission.setCompanyId(RandomTestUtil.nextLong());

		resourcePermission.setName(RandomTestUtil.randomString());

		resourcePermission.setScope(RandomTestUtil.nextInt());

		resourcePermission.setPrimKey(RandomTestUtil.randomString());

		resourcePermission.setPrimKeyId(RandomTestUtil.nextLong());

		resourcePermission.setRoleId(RandomTestUtil.nextLong());

		resourcePermission.setOwnerId(RandomTestUtil.nextLong());

		resourcePermission.setActionIds(RandomTestUtil.nextLong());

		resourcePermission.setViewActionId(RandomTestUtil.randomBoolean());

		_resourcePermissions.add(_persistence.update(resourcePermission));

		return resourcePermission;
	}

	private List<ResourcePermission> _resourcePermissions =
		new ArrayList<ResourcePermission>();
	private ResourcePermissionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}