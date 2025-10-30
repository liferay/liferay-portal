/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchPreferencesException;
import com.liferay.portal.kernel.model.PortalPreferences;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesPersistence;
import com.liferay.portal.kernel.service.persistence.PortalPreferencesUtil;
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
public class PortalPreferencesPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = PortalPreferencesUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PortalPreferences> iterator = _portalPreferenceses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferences portalPreferences = _persistence.create(pk);

		Assert.assertNotNull(portalPreferences);

		Assert.assertEquals(portalPreferences.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PortalPreferences newPortalPreferences = addPortalPreferences();

		_persistence.remove(newPortalPreferences);

		PortalPreferences existingPortalPreferences =
			_persistence.fetchByPrimaryKey(
				newPortalPreferences.getPrimaryKey());

		Assert.assertNull(existingPortalPreferences);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPortalPreferences();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferences newPortalPreferences = _persistence.create(pk);

		newPortalPreferences.setMvccVersion(RandomTestUtil.nextLong());

		newPortalPreferences.setCompanyId(RandomTestUtil.nextLong());

		newPortalPreferences.setOwnerId(RandomTestUtil.nextLong());

		newPortalPreferences.setOwnerType(RandomTestUtil.nextInt());

		_portalPreferenceses.add(_persistence.update(newPortalPreferences));

		PortalPreferences existingPortalPreferences =
			_persistence.findByPrimaryKey(newPortalPreferences.getPrimaryKey());

		Assert.assertEquals(
			existingPortalPreferences.getMvccVersion(),
			newPortalPreferences.getMvccVersion());
		Assert.assertEquals(
			existingPortalPreferences.getPortalPreferencesId(),
			newPortalPreferences.getPortalPreferencesId());
		Assert.assertEquals(
			existingPortalPreferences.getCompanyId(),
			newPortalPreferences.getCompanyId());
		Assert.assertEquals(
			existingPortalPreferences.getOwnerId(),
			newPortalPreferences.getOwnerId());
		Assert.assertEquals(
			existingPortalPreferences.getOwnerType(),
			newPortalPreferences.getOwnerType());
	}

	@Test
	public void testCountByOwnerType() throws Exception {
		_persistence.countByOwnerType(RandomTestUtil.nextInt());

		_persistence.countByOwnerType(0);
	}

	@Test
	public void testCountByO_O() throws Exception {
		_persistence.countByO_O(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByO_O(0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PortalPreferences newPortalPreferences = addPortalPreferences();

		PortalPreferences existingPortalPreferences =
			_persistence.findByPrimaryKey(newPortalPreferences.getPrimaryKey());

		Assert.assertEquals(existingPortalPreferences, newPortalPreferences);
	}

	@Test(expected = NoSuchPreferencesException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PortalPreferences> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PortalPreferences", "mvccVersion", true, "portalPreferencesId",
			true, "companyId", true, "ownerId", true, "ownerType", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PortalPreferences newPortalPreferences = addPortalPreferences();

		PortalPreferences existingPortalPreferences =
			_persistence.fetchByPrimaryKey(
				newPortalPreferences.getPrimaryKey());

		Assert.assertEquals(existingPortalPreferences, newPortalPreferences);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferences missingPortalPreferences =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPortalPreferences);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PortalPreferences newPortalPreferences1 = addPortalPreferences();
		PortalPreferences newPortalPreferences2 = addPortalPreferences();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortalPreferences1.getPrimaryKey());
		primaryKeys.add(newPortalPreferences2.getPrimaryKey());

		Map<Serializable, PortalPreferences> portalPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, portalPreferenceses.size());
		Assert.assertEquals(
			newPortalPreferences1,
			portalPreferenceses.get(newPortalPreferences1.getPrimaryKey()));
		Assert.assertEquals(
			newPortalPreferences2,
			portalPreferenceses.get(newPortalPreferences2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PortalPreferences> portalPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portalPreferenceses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PortalPreferences newPortalPreferences = addPortalPreferences();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortalPreferences.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PortalPreferences> portalPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portalPreferenceses.size());
		Assert.assertEquals(
			newPortalPreferences,
			portalPreferenceses.get(newPortalPreferences.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PortalPreferences> portalPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portalPreferenceses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PortalPreferences newPortalPreferences = addPortalPreferences();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortalPreferences.getPrimaryKey());

		Map<Serializable, PortalPreferences> portalPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portalPreferenceses.size());
		Assert.assertEquals(
			newPortalPreferences,
			portalPreferenceses.get(newPortalPreferences.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PortalPreferences newPortalPreferences = addPortalPreferences();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newPortalPreferences.getPrimaryKey()));
	}

	private void _assertOriginalValues(PortalPreferences portalPreferences) {
		Assert.assertEquals(
			Long.valueOf(portalPreferences.getOwnerId()),
			ReflectionTestUtil.<Long>invoke(
				portalPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ownerId"));
		Assert.assertEquals(
			Integer.valueOf(portalPreferences.getOwnerType()),
			ReflectionTestUtil.<Integer>invoke(
				portalPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ownerType"));
	}

	protected PortalPreferences addPortalPreferences() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortalPreferences portalPreferences = _persistence.create(pk);

		portalPreferences.setMvccVersion(RandomTestUtil.nextLong());

		portalPreferences.setCompanyId(RandomTestUtil.nextLong());

		portalPreferences.setOwnerId(RandomTestUtil.nextLong());

		portalPreferences.setOwnerType(RandomTestUtil.nextInt());

		_portalPreferenceses.add(_persistence.update(portalPreferences));

		return portalPreferences;
	}

	private List<PortalPreferences> _portalPreferenceses =
		new ArrayList<PortalPreferences>();
	private PortalPreferencesPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}