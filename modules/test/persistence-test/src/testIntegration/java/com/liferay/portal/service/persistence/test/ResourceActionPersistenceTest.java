/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchResourceActionException;
import com.liferay.portal.kernel.model.ResourceAction;
import com.liferay.portal.kernel.service.persistence.ResourceActionPersistence;
import com.liferay.portal.kernel.service.persistence.ResourceActionUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class ResourceActionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ResourceActionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<ResourceAction> iterator = _resourceActions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourceAction resourceAction = _persistence.create(pk);

		Assert.assertNotNull(resourceAction);

		Assert.assertEquals(resourceAction.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		ResourceAction newResourceAction = addResourceAction();

		_persistence.remove(newResourceAction);

		ResourceAction existingResourceAction = _persistence.fetchByPrimaryKey(
			newResourceAction.getPrimaryKey());

		Assert.assertNull(existingResourceAction);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addResourceAction();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourceAction newResourceAction = _persistence.create(pk);

		newResourceAction.setMvccVersion(RandomTestUtil.nextLong());

		newResourceAction.setName(RandomTestUtil.randomString());

		newResourceAction.setActionId(RandomTestUtil.randomString());

		newResourceAction.setBitwiseValue(RandomTestUtil.nextLong());

		_resourceActions.add(_persistence.update(newResourceAction));

		ResourceAction existingResourceAction = _persistence.findByPrimaryKey(
			newResourceAction.getPrimaryKey());

		Assert.assertEquals(
			existingResourceAction.getMvccVersion(),
			newResourceAction.getMvccVersion());
		Assert.assertEquals(
			existingResourceAction.getResourceActionId(),
			newResourceAction.getResourceActionId());
		Assert.assertEquals(
			existingResourceAction.getName(), newResourceAction.getName());
		Assert.assertEquals(
			existingResourceAction.getActionId(),
			newResourceAction.getActionId());
		Assert.assertEquals(
			existingResourceAction.getBitwiseValue(),
			newResourceAction.getBitwiseValue());
	}

	@Test
	public void testCountByName() throws Exception {
		_persistence.countByName("");

		_persistence.countByName("null");

		_persistence.countByName((String)null);
	}

	@Test
	public void testCountByN_A() throws Exception {
		_persistence.countByN_A("", "");

		_persistence.countByN_A("null", "null");

		_persistence.countByN_A((String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		ResourceAction newResourceAction = addResourceAction();

		ResourceAction existingResourceAction = _persistence.findByPrimaryKey(
			newResourceAction.getPrimaryKey());

		Assert.assertEquals(existingResourceAction, newResourceAction);
	}

	@Test(expected = NoSuchResourceActionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<ResourceAction> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"ResourceAction", "mvccVersion", true, "resourceActionId", true,
			"name", true, "actionId", true, "bitwiseValue", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		ResourceAction newResourceAction = addResourceAction();

		ResourceAction existingResourceAction = _persistence.fetchByPrimaryKey(
			newResourceAction.getPrimaryKey());

		Assert.assertEquals(existingResourceAction, newResourceAction);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourceAction missingResourceAction = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingResourceAction);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		ResourceAction newResourceAction1 = addResourceAction();
		ResourceAction newResourceAction2 = addResourceAction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newResourceAction1.getPrimaryKey());
		primaryKeys.add(newResourceAction2.getPrimaryKey());

		Map<Serializable, ResourceAction> resourceActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, resourceActions.size());
		Assert.assertEquals(
			newResourceAction1,
			resourceActions.get(newResourceAction1.getPrimaryKey()));
		Assert.assertEquals(
			newResourceAction2,
			resourceActions.get(newResourceAction2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, ResourceAction> resourceActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(resourceActions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		ResourceAction newResourceAction = addResourceAction();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newResourceAction.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, ResourceAction> resourceActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, resourceActions.size());
		Assert.assertEquals(
			newResourceAction,
			resourceActions.get(newResourceAction.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, ResourceAction> resourceActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(resourceActions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		ResourceAction newResourceAction = addResourceAction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newResourceAction.getPrimaryKey());

		Map<Serializable, ResourceAction> resourceActions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, resourceActions.size());
		Assert.assertEquals(
			newResourceAction,
			resourceActions.get(newResourceAction.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		ResourceAction newResourceAction = addResourceAction();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newResourceAction.getPrimaryKey()));
	}

	private void _assertOriginalValues(ResourceAction resourceAction) {
		Assert.assertEquals(
			resourceAction.getName(),
			ReflectionTestUtil.invoke(
				resourceAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
		Assert.assertEquals(
			resourceAction.getActionId(),
			ReflectionTestUtil.invoke(
				resourceAction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "actionId"));
	}

	protected ResourceAction addResourceAction() throws Exception {
		long pk = RandomTestUtil.nextLong();

		ResourceAction resourceAction = _persistence.create(pk);

		resourceAction.setMvccVersion(RandomTestUtil.nextLong());

		resourceAction.setName(RandomTestUtil.randomString());

		resourceAction.setActionId(RandomTestUtil.randomString());

		resourceAction.setBitwiseValue(RandomTestUtil.nextLong());

		_resourceActions.add(_persistence.update(resourceAction));

		return resourceAction;
	}

	private List<ResourceAction> _resourceActions =
		new ArrayList<ResourceAction>();
	private ResourceActionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}