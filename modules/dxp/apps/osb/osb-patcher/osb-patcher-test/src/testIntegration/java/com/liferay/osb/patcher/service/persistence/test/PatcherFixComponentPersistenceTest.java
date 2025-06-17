/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.osb.patcher.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.osb.patcher.exception.NoSuchPatcherFixComponentException;
import com.liferay.osb.patcher.model.PatcherFixComponent;
import com.liferay.osb.patcher.service.PatcherFixComponentLocalServiceUtil;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentPersistence;
import com.liferay.osb.patcher.service.persistence.PatcherFixComponentUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
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
public class PatcherFixComponentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.osb.patcher.service"));

	@Before
	public void setUp() {
		_persistence = PatcherFixComponentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PatcherFixComponent> iterator =
			_patcherFixComponents.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixComponent patcherFixComponent = _persistence.create(pk);

		Assert.assertNotNull(patcherFixComponent);

		Assert.assertEquals(patcherFixComponent.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		_persistence.remove(newPatcherFixComponent);

		PatcherFixComponent existingPatcherFixComponent =
			_persistence.fetchByPrimaryKey(
				newPatcherFixComponent.getPrimaryKey());

		Assert.assertNull(existingPatcherFixComponent);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPatcherFixComponent();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixComponent newPatcherFixComponent = _persistence.create(pk);

		newPatcherFixComponent.setMvccVersion(RandomTestUtil.nextLong());

		newPatcherFixComponent.setCompanyId(RandomTestUtil.nextLong());

		newPatcherFixComponent.setUserId(RandomTestUtil.nextLong());

		newPatcherFixComponent.setUserName(RandomTestUtil.randomString());

		newPatcherFixComponent.setCreateDate(RandomTestUtil.nextDate());

		newPatcherFixComponent.setModifiedDate(RandomTestUtil.nextDate());

		newPatcherFixComponent.setName(RandomTestUtil.randomString());

		_patcherFixComponents.add(_persistence.update(newPatcherFixComponent));

		PatcherFixComponent existingPatcherFixComponent =
			_persistence.findByPrimaryKey(
				newPatcherFixComponent.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherFixComponent.getMvccVersion(),
			newPatcherFixComponent.getMvccVersion());
		Assert.assertEquals(
			existingPatcherFixComponent.getPatcherFixComponentId(),
			newPatcherFixComponent.getPatcherFixComponentId());
		Assert.assertEquals(
			existingPatcherFixComponent.getCompanyId(),
			newPatcherFixComponent.getCompanyId());
		Assert.assertEquals(
			existingPatcherFixComponent.getUserId(),
			newPatcherFixComponent.getUserId());
		Assert.assertEquals(
			existingPatcherFixComponent.getUserName(),
			newPatcherFixComponent.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPatcherFixComponent.getCreateDate()),
			Time.getShortTimestamp(newPatcherFixComponent.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingPatcherFixComponent.getModifiedDate()),
			Time.getShortTimestamp(newPatcherFixComponent.getModifiedDate()));
		Assert.assertEquals(
			existingPatcherFixComponent.getName(),
			newPatcherFixComponent.getName());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		PatcherFixComponent existingPatcherFixComponent =
			_persistence.findByPrimaryKey(
				newPatcherFixComponent.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherFixComponent, newPatcherFixComponent);
	}

	@Test(expected = NoSuchPatcherFixComponentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PatcherFixComponent> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OSBPatcher_PatcherFixComponent", "mvccVersion", true,
			"patcherFixComponentId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "name",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		PatcherFixComponent existingPatcherFixComponent =
			_persistence.fetchByPrimaryKey(
				newPatcherFixComponent.getPrimaryKey());

		Assert.assertEquals(
			existingPatcherFixComponent, newPatcherFixComponent);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixComponent missingPatcherFixComponent =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPatcherFixComponent);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PatcherFixComponent newPatcherFixComponent1 = addPatcherFixComponent();
		PatcherFixComponent newPatcherFixComponent2 = addPatcherFixComponent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixComponent1.getPrimaryKey());
		primaryKeys.add(newPatcherFixComponent2.getPrimaryKey());

		Map<Serializable, PatcherFixComponent> patcherFixComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, patcherFixComponents.size());
		Assert.assertEquals(
			newPatcherFixComponent1,
			patcherFixComponents.get(newPatcherFixComponent1.getPrimaryKey()));
		Assert.assertEquals(
			newPatcherFixComponent2,
			patcherFixComponents.get(newPatcherFixComponent2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PatcherFixComponent> patcherFixComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixComponents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixComponent.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PatcherFixComponent> patcherFixComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixComponents.size());
		Assert.assertEquals(
			newPatcherFixComponent,
			patcherFixComponents.get(newPatcherFixComponent.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PatcherFixComponent> patcherFixComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(patcherFixComponents.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPatcherFixComponent.getPrimaryKey());

		Map<Serializable, PatcherFixComponent> patcherFixComponents =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, patcherFixComponents.size());
		Assert.assertEquals(
			newPatcherFixComponent,
			patcherFixComponents.get(newPatcherFixComponent.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			PatcherFixComponentLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<PatcherFixComponent>() {

				@Override
				public void performAction(
					PatcherFixComponent patcherFixComponent) {

					Assert.assertNotNull(patcherFixComponent);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixComponentId",
				newPatcherFixComponent.getPatcherFixComponentId()));

		List<PatcherFixComponent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		PatcherFixComponent existingPatcherFixComponent = result.get(0);

		Assert.assertEquals(
			existingPatcherFixComponent, newPatcherFixComponent);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"patcherFixComponentId", RandomTestUtil.nextLong()));

		List<PatcherFixComponent> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		PatcherFixComponent newPatcherFixComponent = addPatcherFixComponent();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixComponentId"));

		Object newPatcherFixComponentId =
			newPatcherFixComponent.getPatcherFixComponentId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixComponentId",
				new Object[] {newPatcherFixComponentId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPatcherFixComponentId = result.get(0);

		Assert.assertEquals(
			existingPatcherFixComponentId, newPatcherFixComponentId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			PatcherFixComponent.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("patcherFixComponentId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"patcherFixComponentId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	protected PatcherFixComponent addPatcherFixComponent() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PatcherFixComponent patcherFixComponent = _persistence.create(pk);

		patcherFixComponent.setMvccVersion(RandomTestUtil.nextLong());

		patcherFixComponent.setCompanyId(RandomTestUtil.nextLong());

		patcherFixComponent.setUserId(RandomTestUtil.nextLong());

		patcherFixComponent.setUserName(RandomTestUtil.randomString());

		patcherFixComponent.setCreateDate(RandomTestUtil.nextDate());

		patcherFixComponent.setModifiedDate(RandomTestUtil.nextDate());

		patcherFixComponent.setName(RandomTestUtil.randomString());

		_patcherFixComponents.add(_persistence.update(patcherFixComponent));

		return patcherFixComponent;
	}

	private List<PatcherFixComponent> _patcherFixComponents =
		new ArrayList<PatcherFixComponent>();
	private PatcherFixComponentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}