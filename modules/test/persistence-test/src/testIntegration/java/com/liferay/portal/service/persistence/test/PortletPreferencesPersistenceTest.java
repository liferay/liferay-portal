/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferencesException;
import com.liferay.portal.kernel.model.PortletPreferences;
import com.liferay.portal.kernel.service.persistence.PortletPreferencesPersistence;
import com.liferay.portal.kernel.service.persistence.PortletPreferencesUtil;
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
public class PortletPreferencesPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = PortletPreferencesUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PortletPreferences> iterator =
			_portletPreferenceses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferences portletPreferences = _persistence.create(pk);

		Assert.assertNotNull(portletPreferences);

		Assert.assertEquals(portletPreferences.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PortletPreferences newPortletPreferences = addPortletPreferences();

		_persistence.remove(newPortletPreferences);

		PortletPreferences existingPortletPreferences =
			_persistence.fetchByPrimaryKey(
				newPortletPreferences.getPrimaryKey());

		Assert.assertNull(existingPortletPreferences);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPortletPreferences();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferences newPortletPreferences = _persistence.create(pk);

		newPortletPreferences.setMvccVersion(RandomTestUtil.nextLong());

		newPortletPreferences.setCtCollectionId(RandomTestUtil.nextLong());

		newPortletPreferences.setCompanyId(RandomTestUtil.nextLong());

		newPortletPreferences.setOwnerId(RandomTestUtil.nextLong());

		newPortletPreferences.setOwnerType(RandomTestUtil.nextInt());

		newPortletPreferences.setPlid(RandomTestUtil.nextLong());

		newPortletPreferences.setPortletId(RandomTestUtil.randomString());

		_portletPreferenceses.add(_persistence.update(newPortletPreferences));

		PortletPreferences existingPortletPreferences =
			_persistence.findByPrimaryKey(
				newPortletPreferences.getPrimaryKey());

		Assert.assertEquals(
			existingPortletPreferences.getMvccVersion(),
			newPortletPreferences.getMvccVersion());
		Assert.assertEquals(
			existingPortletPreferences.getCtCollectionId(),
			newPortletPreferences.getCtCollectionId());
		Assert.assertEquals(
			existingPortletPreferences.getPortletPreferencesId(),
			newPortletPreferences.getPortletPreferencesId());
		Assert.assertEquals(
			existingPortletPreferences.getCompanyId(),
			newPortletPreferences.getCompanyId());
		Assert.assertEquals(
			existingPortletPreferences.getOwnerId(),
			newPortletPreferences.getOwnerId());
		Assert.assertEquals(
			existingPortletPreferences.getOwnerType(),
			newPortletPreferences.getOwnerType());
		Assert.assertEquals(
			existingPortletPreferences.getPlid(),
			newPortletPreferences.getPlid());
		Assert.assertEquals(
			existingPortletPreferences.getPortletId(),
			newPortletPreferences.getPortletId());
	}

	@Test
	public void testCountByOwnerId() throws Exception {
		_persistence.countByOwnerId(RandomTestUtil.nextLong());

		_persistence.countByOwnerId(0L);
	}

	@Test
	public void testCountByPlid() throws Exception {
		_persistence.countByPlid(RandomTestUtil.nextLong());

		_persistence.countByPlid(0L);
	}

	@Test
	public void testCountByPortletId() throws Exception {
		_persistence.countByPortletId("");

		_persistence.countByPortletId("null");

		_persistence.countByPortletId((String)null);
	}

	@Test
	public void testCountByO_P() throws Exception {
		_persistence.countByO_P(RandomTestUtil.nextInt(), "");

		_persistence.countByO_P(0, "null");

		_persistence.countByO_P(0, (String)null);
	}

	@Test
	public void testCountByP_P() throws Exception {
		_persistence.countByP_P(RandomTestUtil.nextLong(), "");

		_persistence.countByP_P(0L, "null");

		_persistence.countByP_P(0L, (String)null);
	}

	@Test
	public void testCountByO_O_P() throws Exception {
		_persistence.countByO_O_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong());

		_persistence.countByO_O_P(0L, 0, 0L);
	}

	@Test
	public void testCountByO_O_PI() throws Exception {
		_persistence.countByO_O_PI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(), "");

		_persistence.countByO_O_PI(0L, 0, "null");

		_persistence.countByO_O_PI(0L, 0, (String)null);
	}

	@Test
	public void testCountByO_P_P() throws Exception {
		_persistence.countByO_P_P(
			RandomTestUtil.nextInt(), RandomTestUtil.nextLong(), "");

		_persistence.countByO_P_P(0, 0L, "null");

		_persistence.countByO_P_P(0, 0L, (String)null);
	}

	@Test
	public void testCountByC_O_O_LikeP() throws Exception {
		_persistence.countByC_O_O_LikeP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), "");

		_persistence.countByC_O_O_LikeP(0L, 0L, 0, "null");

		_persistence.countByC_O_O_LikeP(0L, 0L, 0, (String)null);
	}

	@Test
	public void testCountByO_O_P_P() throws Exception {
		_persistence.countByO_O_P_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByO_O_P_P(0L, 0, 0L, "null");

		_persistence.countByO_O_P_P(0L, 0, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PortletPreferences newPortletPreferences = addPortletPreferences();

		PortletPreferences existingPortletPreferences =
			_persistence.findByPrimaryKey(
				newPortletPreferences.getPrimaryKey());

		Assert.assertEquals(existingPortletPreferences, newPortletPreferences);
	}

	@Test(expected = NoSuchPortletPreferencesException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PortletPreferences> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PortletPreferences", "mvccVersion", true, "ctCollectionId", true,
			"portletPreferencesId", true, "companyId", true, "ownerId", true,
			"ownerType", true, "plid", true, "portletId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PortletPreferences newPortletPreferences = addPortletPreferences();

		PortletPreferences existingPortletPreferences =
			_persistence.fetchByPrimaryKey(
				newPortletPreferences.getPrimaryKey());

		Assert.assertEquals(existingPortletPreferences, newPortletPreferences);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferences missingPortletPreferences =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPortletPreferences);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PortletPreferences newPortletPreferences1 = addPortletPreferences();
		PortletPreferences newPortletPreferences2 = addPortletPreferences();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortletPreferences1.getPrimaryKey());
		primaryKeys.add(newPortletPreferences2.getPrimaryKey());

		Map<Serializable, PortletPreferences> portletPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, portletPreferenceses.size());
		Assert.assertEquals(
			newPortletPreferences1,
			portletPreferenceses.get(newPortletPreferences1.getPrimaryKey()));
		Assert.assertEquals(
			newPortletPreferences2,
			portletPreferenceses.get(newPortletPreferences2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PortletPreferences> portletPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portletPreferenceses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PortletPreferences newPortletPreferences = addPortletPreferences();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortletPreferences.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PortletPreferences> portletPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portletPreferenceses.size());
		Assert.assertEquals(
			newPortletPreferences,
			portletPreferenceses.get(newPortletPreferences.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PortletPreferences> portletPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portletPreferenceses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PortletPreferences newPortletPreferences = addPortletPreferences();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortletPreferences.getPrimaryKey());

		Map<Serializable, PortletPreferences> portletPreferenceses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portletPreferenceses.size());
		Assert.assertEquals(
			newPortletPreferences,
			portletPreferenceses.get(newPortletPreferences.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PortletPreferences newPortletPreferences = addPortletPreferences();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newPortletPreferences.getPrimaryKey()));
	}

	private void _assertOriginalValues(PortletPreferences portletPreferences) {
		Assert.assertEquals(
			Long.valueOf(portletPreferences.getOwnerId()),
			ReflectionTestUtil.<Long>invoke(
				portletPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ownerId"));
		Assert.assertEquals(
			Integer.valueOf(portletPreferences.getOwnerType()),
			ReflectionTestUtil.<Integer>invoke(
				portletPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "ownerType"));
		Assert.assertEquals(
			Long.valueOf(portletPreferences.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				portletPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
		Assert.assertEquals(
			portletPreferences.getPortletId(),
			ReflectionTestUtil.invoke(
				portletPreferences, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletId"));
	}

	protected PortletPreferences addPortletPreferences() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferences portletPreferences = _persistence.create(pk);

		portletPreferences.setMvccVersion(RandomTestUtil.nextLong());

		portletPreferences.setCtCollectionId(RandomTestUtil.nextLong());

		portletPreferences.setCompanyId(RandomTestUtil.nextLong());

		portletPreferences.setOwnerId(RandomTestUtil.nextLong());

		portletPreferences.setOwnerType(RandomTestUtil.nextInt());

		portletPreferences.setPlid(RandomTestUtil.nextLong());

		portletPreferences.setPortletId(RandomTestUtil.randomString());

		_portletPreferenceses.add(_persistence.update(portletPreferences));

		return portletPreferences;
	}

	private List<PortletPreferences> _portletPreferenceses =
		new ArrayList<PortletPreferences>();
	private PortletPreferencesPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}