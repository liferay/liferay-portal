/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchVirtualHostException;
import com.liferay.portal.kernel.model.VirtualHost;
import com.liferay.portal.kernel.service.persistence.VirtualHostPersistence;
import com.liferay.portal.kernel.service.persistence.VirtualHostUtil;
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
public class VirtualHostPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = VirtualHostUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<VirtualHost> iterator = _virtualHosts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VirtualHost virtualHost = _persistence.create(pk);

		Assert.assertNotNull(virtualHost);

		Assert.assertEquals(virtualHost.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		VirtualHost newVirtualHost = addVirtualHost();

		_persistence.remove(newVirtualHost);

		VirtualHost existingVirtualHost = _persistence.fetchByPrimaryKey(
			newVirtualHost.getPrimaryKey());

		Assert.assertNull(existingVirtualHost);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addVirtualHost();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VirtualHost newVirtualHost = _persistence.create(pk);

		newVirtualHost.setMvccVersion(RandomTestUtil.nextLong());

		newVirtualHost.setCtCollectionId(RandomTestUtil.nextLong());

		newVirtualHost.setCompanyId(RandomTestUtil.nextLong());

		newVirtualHost.setLayoutSetId(RandomTestUtil.nextLong());

		newVirtualHost.setHostname(RandomTestUtil.randomString());

		newVirtualHost.setDefaultVirtualHost(RandomTestUtil.randomBoolean());

		newVirtualHost.setLanguageId(RandomTestUtil.randomString());

		_virtualHosts.add(_persistence.update(newVirtualHost));

		VirtualHost existingVirtualHost = _persistence.findByPrimaryKey(
			newVirtualHost.getPrimaryKey());

		Assert.assertEquals(
			existingVirtualHost.getMvccVersion(),
			newVirtualHost.getMvccVersion());
		Assert.assertEquals(
			existingVirtualHost.getCtCollectionId(),
			newVirtualHost.getCtCollectionId());
		Assert.assertEquals(
			existingVirtualHost.getVirtualHostId(),
			newVirtualHost.getVirtualHostId());
		Assert.assertEquals(
			existingVirtualHost.getCompanyId(), newVirtualHost.getCompanyId());
		Assert.assertEquals(
			existingVirtualHost.getLayoutSetId(),
			newVirtualHost.getLayoutSetId());
		Assert.assertEquals(
			existingVirtualHost.getHostname(), newVirtualHost.getHostname());
		Assert.assertEquals(
			existingVirtualHost.isDefaultVirtualHost(),
			newVirtualHost.isDefaultVirtualHost());
		Assert.assertEquals(
			existingVirtualHost.getLanguageId(),
			newVirtualHost.getLanguageId());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByHostname() throws Exception {
		_persistence.countByHostname("");

		_persistence.countByHostname("null");

		_persistence.countByHostname((String)null);
	}

	@Test
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_L(0L, 0L);
	}

	@Test
	public void testCountByNotL_H() throws Exception {
		_persistence.countByNotL_H(RandomTestUtil.nextLong(), "");

		_persistence.countByNotL_H(0L, "null");

		_persistence.countByNotL_H(0L, (String)null);
	}

	@Test
	public void testCountByNotL_HArrayable() throws Exception {
		_persistence.countByNotL_H(
			RandomTestUtil.nextLong(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		VirtualHost newVirtualHost = addVirtualHost();

		VirtualHost existingVirtualHost = _persistence.findByPrimaryKey(
			newVirtualHost.getPrimaryKey());

		Assert.assertEquals(existingVirtualHost, newVirtualHost);
	}

	@Test(expected = NoSuchVirtualHostException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<VirtualHost> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"VirtualHost", "mvccVersion", true, "ctCollectionId", true,
			"virtualHostId", true, "companyId", true, "layoutSetId", true,
			"hostname", true, "defaultVirtualHost", true, "languageId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		VirtualHost newVirtualHost = addVirtualHost();

		VirtualHost existingVirtualHost = _persistence.fetchByPrimaryKey(
			newVirtualHost.getPrimaryKey());

		Assert.assertEquals(existingVirtualHost, newVirtualHost);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VirtualHost missingVirtualHost = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingVirtualHost);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		VirtualHost newVirtualHost1 = addVirtualHost();
		VirtualHost newVirtualHost2 = addVirtualHost();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newVirtualHost1.getPrimaryKey());
		primaryKeys.add(newVirtualHost2.getPrimaryKey());

		Map<Serializable, VirtualHost> virtualHosts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, virtualHosts.size());
		Assert.assertEquals(
			newVirtualHost1, virtualHosts.get(newVirtualHost1.getPrimaryKey()));
		Assert.assertEquals(
			newVirtualHost2, virtualHosts.get(newVirtualHost2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, VirtualHost> virtualHosts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(virtualHosts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		VirtualHost newVirtualHost = addVirtualHost();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newVirtualHost.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, VirtualHost> virtualHosts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, virtualHosts.size());
		Assert.assertEquals(
			newVirtualHost, virtualHosts.get(newVirtualHost.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, VirtualHost> virtualHosts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(virtualHosts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		VirtualHost newVirtualHost = addVirtualHost();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newVirtualHost.getPrimaryKey());

		Map<Serializable, VirtualHost> virtualHosts =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, virtualHosts.size());
		Assert.assertEquals(
			newVirtualHost, virtualHosts.get(newVirtualHost.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		VirtualHost newVirtualHost = addVirtualHost();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newVirtualHost.getPrimaryKey()));
	}

	private void _assertOriginalValues(VirtualHost virtualHost) {
		Assert.assertEquals(
			virtualHost.getHostname(),
			ReflectionTestUtil.invoke(
				virtualHost, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "hostname"));
	}

	protected VirtualHost addVirtualHost() throws Exception {
		long pk = RandomTestUtil.nextLong();

		VirtualHost virtualHost = _persistence.create(pk);

		virtualHost.setMvccVersion(RandomTestUtil.nextLong());

		virtualHost.setCtCollectionId(RandomTestUtil.nextLong());

		virtualHost.setCompanyId(RandomTestUtil.nextLong());

		virtualHost.setLayoutSetId(RandomTestUtil.nextLong());

		virtualHost.setHostname(RandomTestUtil.randomString());

		virtualHost.setDefaultVirtualHost(RandomTestUtil.randomBoolean());

		virtualHost.setLanguageId(RandomTestUtil.randomString());

		_virtualHosts.add(_persistence.update(virtualHost));

		return virtualHost;
	}

	private List<VirtualHost> _virtualHosts = new ArrayList<VirtualHost>();
	private VirtualHostPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}