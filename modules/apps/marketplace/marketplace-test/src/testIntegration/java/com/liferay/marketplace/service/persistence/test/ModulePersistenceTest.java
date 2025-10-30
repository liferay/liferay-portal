/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.marketplace.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.marketplace.exception.NoSuchModuleException;
import com.liferay.marketplace.model.Module;
import com.liferay.marketplace.service.persistence.ModulePersistence;
import com.liferay.marketplace.service.persistence.ModuleUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class ModulePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.marketplace.service"));

	@Before
	public void setUp() {
		_persistence = ModuleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Module> iterator = _modules.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Module module = _persistence.create(pk);

		Assert.assertNotNull(module);

		Assert.assertEquals(module.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Module newModule = addModule();

		_persistence.remove(newModule);

		Module existingModule = _persistence.fetchByPrimaryKey(
			newModule.getPrimaryKey());

		Assert.assertNull(existingModule);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addModule();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Module newModule = _persistence.create(pk);

		newModule.setUuid(RandomTestUtil.randomString());

		newModule.setCompanyId(RandomTestUtil.nextLong());

		newModule.setAppId(RandomTestUtil.nextLong());

		newModule.setBundleSymbolicName(RandomTestUtil.randomString());

		newModule.setBundleVersion(RandomTestUtil.randomString());

		newModule.setContextName(RandomTestUtil.randomString());

		_modules.add(_persistence.update(newModule));

		Module existingModule = _persistence.findByPrimaryKey(
			newModule.getPrimaryKey());

		Assert.assertEquals(existingModule.getUuid(), newModule.getUuid());
		Assert.assertEquals(
			existingModule.getModuleId(), newModule.getModuleId());
		Assert.assertEquals(
			existingModule.getCompanyId(), newModule.getCompanyId());
		Assert.assertEquals(existingModule.getAppId(), newModule.getAppId());
		Assert.assertEquals(
			existingModule.getBundleSymbolicName(),
			newModule.getBundleSymbolicName());
		Assert.assertEquals(
			existingModule.getBundleVersion(), newModule.getBundleVersion());
		Assert.assertEquals(
			existingModule.getContextName(), newModule.getContextName());
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByAppId() throws Exception {
		_persistence.countByAppId(RandomTestUtil.nextLong());

		_persistence.countByAppId(0L);
	}

	@Test
	public void testCountByBundleSymbolicName() throws Exception {
		_persistence.countByBundleSymbolicName("");

		_persistence.countByBundleSymbolicName("null");

		_persistence.countByBundleSymbolicName((String)null);
	}

	@Test
	public void testCountByContextName() throws Exception {
		_persistence.countByContextName("");

		_persistence.countByContextName("null");

		_persistence.countByContextName((String)null);
	}

	@Test
	public void testCountByA_CN() throws Exception {
		_persistence.countByA_CN(RandomTestUtil.nextLong(), "");

		_persistence.countByA_CN(0L, "null");

		_persistence.countByA_CN(0L, (String)null);
	}

	@Test
	public void testCountByA_BSN_BV() throws Exception {
		_persistence.countByA_BSN_BV(RandomTestUtil.nextLong(), "", "");

		_persistence.countByA_BSN_BV(0L, "null", "null");

		_persistence.countByA_BSN_BV(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Module newModule = addModule();

		Module existingModule = _persistence.findByPrimaryKey(
			newModule.getPrimaryKey());

		Assert.assertEquals(existingModule, newModule);
	}

	@Test(expected = NoSuchModuleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Module> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Marketplace_Module", "uuid", true, "moduleId", true, "companyId",
			true, "appId", true, "bundleSymbolicName", true, "bundleVersion",
			true, "contextName", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Module newModule = addModule();

		Module existingModule = _persistence.fetchByPrimaryKey(
			newModule.getPrimaryKey());

		Assert.assertEquals(existingModule, newModule);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Module missingModule = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingModule);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Module newModule1 = addModule();
		Module newModule2 = addModule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newModule1.getPrimaryKey());
		primaryKeys.add(newModule2.getPrimaryKey());

		Map<Serializable, Module> modules = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, modules.size());
		Assert.assertEquals(
			newModule1, modules.get(newModule1.getPrimaryKey()));
		Assert.assertEquals(
			newModule2, modules.get(newModule2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Module> modules = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(modules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Module newModule = addModule();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newModule.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Module> modules = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, modules.size());
		Assert.assertEquals(newModule, modules.get(newModule.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Module> modules = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(modules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Module newModule = addModule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newModule.getPrimaryKey());

		Map<Serializable, Module> modules = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, modules.size());
		Assert.assertEquals(newModule, modules.get(newModule.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Module newModule = addModule();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newModule.getPrimaryKey()));
	}

	private void _assertOriginalValues(Module module) {
		Assert.assertEquals(
			Long.valueOf(module.getAppId()),
			ReflectionTestUtil.<Long>invoke(
				module, "getColumnOriginalValue", new Class<?>[] {String.class},
				"appId"));
		Assert.assertEquals(
			module.getContextName(),
			ReflectionTestUtil.invoke(
				module, "getColumnOriginalValue", new Class<?>[] {String.class},
				"contextName"));

		Assert.assertEquals(
			Long.valueOf(module.getAppId()),
			ReflectionTestUtil.<Long>invoke(
				module, "getColumnOriginalValue", new Class<?>[] {String.class},
				"appId"));
		Assert.assertEquals(
			module.getBundleSymbolicName(),
			ReflectionTestUtil.invoke(
				module, "getColumnOriginalValue", new Class<?>[] {String.class},
				"bundleSymbolicName"));
		Assert.assertEquals(
			module.getBundleVersion(),
			ReflectionTestUtil.invoke(
				module, "getColumnOriginalValue", new Class<?>[] {String.class},
				"bundleVersion"));
	}

	protected Module addModule() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Module module = _persistence.create(pk);

		module.setUuid(RandomTestUtil.randomString());

		module.setCompanyId(RandomTestUtil.nextLong());

		module.setAppId(RandomTestUtil.nextLong());

		module.setBundleSymbolicName(RandomTestUtil.randomString());

		module.setBundleVersion(RandomTestUtil.randomString());

		module.setContextName(RandomTestUtil.randomString());

		_modules.add(_persistence.update(module));

		return module;
	}

	private List<Module> _modules = new ArrayList<Module>();
	private ModulePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}