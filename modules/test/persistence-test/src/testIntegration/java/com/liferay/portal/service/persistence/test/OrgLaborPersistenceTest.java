/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchOrgLaborException;
import com.liferay.portal.kernel.model.OrgLabor;
import com.liferay.portal.kernel.service.persistence.OrgLaborPersistence;
import com.liferay.portal.kernel.service.persistence.OrgLaborUtil;
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
public class OrgLaborPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = OrgLaborUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<OrgLabor> iterator = _orgLabors.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OrgLabor orgLabor = _persistence.create(pk);

		Assert.assertNotNull(orgLabor);

		Assert.assertEquals(orgLabor.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		OrgLabor newOrgLabor = addOrgLabor();

		_persistence.remove(newOrgLabor);

		OrgLabor existingOrgLabor = _persistence.fetchByPrimaryKey(
			newOrgLabor.getPrimaryKey());

		Assert.assertNull(existingOrgLabor);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addOrgLabor();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OrgLabor newOrgLabor = _persistence.create(pk);

		newOrgLabor.setMvccVersion(RandomTestUtil.nextLong());

		newOrgLabor.setCompanyId(RandomTestUtil.nextLong());

		newOrgLabor.setOrganizationId(RandomTestUtil.nextLong());

		newOrgLabor.setListTypeId(RandomTestUtil.nextLong());

		newOrgLabor.setSunOpen(RandomTestUtil.nextInt());

		newOrgLabor.setSunClose(RandomTestUtil.nextInt());

		newOrgLabor.setMonOpen(RandomTestUtil.nextInt());

		newOrgLabor.setMonClose(RandomTestUtil.nextInt());

		newOrgLabor.setTueOpen(RandomTestUtil.nextInt());

		newOrgLabor.setTueClose(RandomTestUtil.nextInt());

		newOrgLabor.setWedOpen(RandomTestUtil.nextInt());

		newOrgLabor.setWedClose(RandomTestUtil.nextInt());

		newOrgLabor.setThuOpen(RandomTestUtil.nextInt());

		newOrgLabor.setThuClose(RandomTestUtil.nextInt());

		newOrgLabor.setFriOpen(RandomTestUtil.nextInt());

		newOrgLabor.setFriClose(RandomTestUtil.nextInt());

		newOrgLabor.setSatOpen(RandomTestUtil.nextInt());

		newOrgLabor.setSatClose(RandomTestUtil.nextInt());

		_orgLabors.add(_persistence.update(newOrgLabor));

		OrgLabor existingOrgLabor = _persistence.findByPrimaryKey(
			newOrgLabor.getPrimaryKey());

		Assert.assertEquals(
			existingOrgLabor.getMvccVersion(), newOrgLabor.getMvccVersion());
		Assert.assertEquals(
			existingOrgLabor.getOrgLaborId(), newOrgLabor.getOrgLaborId());
		Assert.assertEquals(
			existingOrgLabor.getCompanyId(), newOrgLabor.getCompanyId());
		Assert.assertEquals(
			existingOrgLabor.getOrganizationId(),
			newOrgLabor.getOrganizationId());
		Assert.assertEquals(
			existingOrgLabor.getListTypeId(), newOrgLabor.getListTypeId());
		Assert.assertEquals(
			existingOrgLabor.getSunOpen(), newOrgLabor.getSunOpen());
		Assert.assertEquals(
			existingOrgLabor.getSunClose(), newOrgLabor.getSunClose());
		Assert.assertEquals(
			existingOrgLabor.getMonOpen(), newOrgLabor.getMonOpen());
		Assert.assertEquals(
			existingOrgLabor.getMonClose(), newOrgLabor.getMonClose());
		Assert.assertEquals(
			existingOrgLabor.getTueOpen(), newOrgLabor.getTueOpen());
		Assert.assertEquals(
			existingOrgLabor.getTueClose(), newOrgLabor.getTueClose());
		Assert.assertEquals(
			existingOrgLabor.getWedOpen(), newOrgLabor.getWedOpen());
		Assert.assertEquals(
			existingOrgLabor.getWedClose(), newOrgLabor.getWedClose());
		Assert.assertEquals(
			existingOrgLabor.getThuOpen(), newOrgLabor.getThuOpen());
		Assert.assertEquals(
			existingOrgLabor.getThuClose(), newOrgLabor.getThuClose());
		Assert.assertEquals(
			existingOrgLabor.getFriOpen(), newOrgLabor.getFriOpen());
		Assert.assertEquals(
			existingOrgLabor.getFriClose(), newOrgLabor.getFriClose());
		Assert.assertEquals(
			existingOrgLabor.getSatOpen(), newOrgLabor.getSatOpen());
		Assert.assertEquals(
			existingOrgLabor.getSatClose(), newOrgLabor.getSatClose());
	}

	@Test
	public void testCountByOrganizationId() throws Exception {
		_persistence.countByOrganizationId(RandomTestUtil.nextLong());

		_persistence.countByOrganizationId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		OrgLabor newOrgLabor = addOrgLabor();

		OrgLabor existingOrgLabor = _persistence.findByPrimaryKey(
			newOrgLabor.getPrimaryKey());

		Assert.assertEquals(existingOrgLabor, newOrgLabor);
	}

	@Test(expected = NoSuchOrgLaborException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<OrgLabor> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"OrgLabor", "mvccVersion", true, "orgLaborId", true, "companyId",
			true, "organizationId", true, "listTypeId", true, "sunOpen", true,
			"sunClose", true, "monOpen", true, "monClose", true, "tueOpen",
			true, "tueClose", true, "wedOpen", true, "wedClose", true,
			"thuOpen", true, "thuClose", true, "friOpen", true, "friClose",
			true, "satOpen", true, "satClose", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		OrgLabor newOrgLabor = addOrgLabor();

		OrgLabor existingOrgLabor = _persistence.fetchByPrimaryKey(
			newOrgLabor.getPrimaryKey());

		Assert.assertEquals(existingOrgLabor, newOrgLabor);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OrgLabor missingOrgLabor = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingOrgLabor);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		OrgLabor newOrgLabor1 = addOrgLabor();
		OrgLabor newOrgLabor2 = addOrgLabor();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrgLabor1.getPrimaryKey());
		primaryKeys.add(newOrgLabor2.getPrimaryKey());

		Map<Serializable, OrgLabor> orgLabors = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, orgLabors.size());
		Assert.assertEquals(
			newOrgLabor1, orgLabors.get(newOrgLabor1.getPrimaryKey()));
		Assert.assertEquals(
			newOrgLabor2, orgLabors.get(newOrgLabor2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, OrgLabor> orgLabors = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(orgLabors.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		OrgLabor newOrgLabor = addOrgLabor();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrgLabor.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, OrgLabor> orgLabors = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, orgLabors.size());
		Assert.assertEquals(
			newOrgLabor, orgLabors.get(newOrgLabor.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, OrgLabor> orgLabors = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(orgLabors.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		OrgLabor newOrgLabor = addOrgLabor();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newOrgLabor.getPrimaryKey());

		Map<Serializable, OrgLabor> orgLabors = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, orgLabors.size());
		Assert.assertEquals(
			newOrgLabor, orgLabors.get(newOrgLabor.getPrimaryKey()));
	}

	protected OrgLabor addOrgLabor() throws Exception {
		long pk = RandomTestUtil.nextLong();

		OrgLabor orgLabor = _persistence.create(pk);

		orgLabor.setMvccVersion(RandomTestUtil.nextLong());

		orgLabor.setCompanyId(RandomTestUtil.nextLong());

		orgLabor.setOrganizationId(RandomTestUtil.nextLong());

		orgLabor.setListTypeId(RandomTestUtil.nextLong());

		orgLabor.setSunOpen(RandomTestUtil.nextInt());

		orgLabor.setSunClose(RandomTestUtil.nextInt());

		orgLabor.setMonOpen(RandomTestUtil.nextInt());

		orgLabor.setMonClose(RandomTestUtil.nextInt());

		orgLabor.setTueOpen(RandomTestUtil.nextInt());

		orgLabor.setTueClose(RandomTestUtil.nextInt());

		orgLabor.setWedOpen(RandomTestUtil.nextInt());

		orgLabor.setWedClose(RandomTestUtil.nextInt());

		orgLabor.setThuOpen(RandomTestUtil.nextInt());

		orgLabor.setThuClose(RandomTestUtil.nextInt());

		orgLabor.setFriOpen(RandomTestUtil.nextInt());

		orgLabor.setFriClose(RandomTestUtil.nextInt());

		orgLabor.setSatOpen(RandomTestUtil.nextInt());

		orgLabor.setSatClose(RandomTestUtil.nextInt());

		_orgLabors.add(_persistence.update(orgLabor));

		return orgLabor;
	}

	private List<OrgLabor> _orgLabors = new ArrayList<OrgLabor>();
	private OrgLaborPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}