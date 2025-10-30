/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchPortletException;
import com.liferay.portal.kernel.model.Portlet;
import com.liferay.portal.kernel.service.persistence.PortletPersistence;
import com.liferay.portal.kernel.service.persistence.PortletUtil;
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
public class PortletPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = PortletUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Portlet> iterator = _portlets.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Portlet portlet = _persistence.create(pk);

		Assert.assertNotNull(portlet);

		Assert.assertEquals(portlet.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Portlet newPortlet = addPortlet();

		_persistence.remove(newPortlet);

		Portlet existingPortlet = _persistence.fetchByPrimaryKey(
			newPortlet.getPrimaryKey());

		Assert.assertNull(existingPortlet);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPortlet();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Portlet newPortlet = _persistence.create(pk);

		newPortlet.setMvccVersion(RandomTestUtil.nextLong());

		newPortlet.setCompanyId(RandomTestUtil.nextLong());

		newPortlet.setPortletId(RandomTestUtil.randomString());

		newPortlet.setRoles(RandomTestUtil.randomString());

		newPortlet.setActive(RandomTestUtil.randomBoolean());

		_portlets.add(_persistence.update(newPortlet));

		Portlet existingPortlet = _persistence.findByPrimaryKey(
			newPortlet.getPrimaryKey());

		Assert.assertEquals(
			existingPortlet.getMvccVersion(), newPortlet.getMvccVersion());
		Assert.assertEquals(existingPortlet.getId(), newPortlet.getId());
		Assert.assertEquals(
			existingPortlet.getCompanyId(), newPortlet.getCompanyId());
		Assert.assertEquals(
			existingPortlet.getPortletId(), newPortlet.getPortletId());
		Assert.assertEquals(existingPortlet.getRoles(), newPortlet.getRoles());
		Assert.assertEquals(existingPortlet.isActive(), newPortlet.isActive());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(RandomTestUtil.nextLong(), "");

		_persistence.countByC_P(0L, "null");

		_persistence.countByC_P(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Portlet newPortlet = addPortlet();

		Portlet existingPortlet = _persistence.findByPrimaryKey(
			newPortlet.getPrimaryKey());

		Assert.assertEquals(existingPortlet, newPortlet);
	}

	@Test(expected = NoSuchPortletException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Portlet> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Portlet", "mvccVersion", true, "id", true, "companyId", true,
			"portletId", true, "roles", true, "active", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Portlet newPortlet = addPortlet();

		Portlet existingPortlet = _persistence.fetchByPrimaryKey(
			newPortlet.getPrimaryKey());

		Assert.assertEquals(existingPortlet, newPortlet);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Portlet missingPortlet = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPortlet);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Portlet newPortlet1 = addPortlet();
		Portlet newPortlet2 = addPortlet();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortlet1.getPrimaryKey());
		primaryKeys.add(newPortlet2.getPrimaryKey());

		Map<Serializable, Portlet> portlets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, portlets.size());
		Assert.assertEquals(
			newPortlet1, portlets.get(newPortlet1.getPrimaryKey()));
		Assert.assertEquals(
			newPortlet2, portlets.get(newPortlet2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Portlet> portlets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(portlets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Portlet newPortlet = addPortlet();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortlet.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Portlet> portlets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, portlets.size());
		Assert.assertEquals(
			newPortlet, portlets.get(newPortlet.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Portlet> portlets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(portlets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Portlet newPortlet = addPortlet();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortlet.getPrimaryKey());

		Map<Serializable, Portlet> portlets = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, portlets.size());
		Assert.assertEquals(
			newPortlet, portlets.get(newPortlet.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Portlet newPortlet = addPortlet();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newPortlet.getPrimaryKey()));
	}

	private void _assertOriginalValues(Portlet portlet) {
		Assert.assertEquals(
			Long.valueOf(portlet.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				portlet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			portlet.getPortletId(),
			ReflectionTestUtil.invoke(
				portlet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletId"));
	}

	protected Portlet addPortlet() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Portlet portlet = _persistence.create(pk);

		portlet.setMvccVersion(RandomTestUtil.nextLong());

		portlet.setCompanyId(RandomTestUtil.nextLong());

		portlet.setPortletId(RandomTestUtil.randomString());

		portlet.setRoles(RandomTestUtil.randomString());

		portlet.setActive(RandomTestUtil.randomBoolean());

		_portlets.add(_persistence.update(portlet));

		return portlet;
	}

	private List<Portlet> _portlets = new ArrayList<Portlet>();
	private PortletPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}