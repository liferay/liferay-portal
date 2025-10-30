/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchPortletPreferenceValueException;
import com.liferay.portal.kernel.model.PortletPreferenceValue;
import com.liferay.portal.kernel.service.persistence.PortletPreferenceValuePersistence;
import com.liferay.portal.kernel.service.persistence.PortletPreferenceValueUtil;
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
public class PortletPreferenceValuePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = PortletPreferenceValueUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PortletPreferenceValue> iterator =
			_portletPreferenceValues.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferenceValue portletPreferenceValue = _persistence.create(pk);

		Assert.assertNotNull(portletPreferenceValue);

		Assert.assertEquals(portletPreferenceValue.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PortletPreferenceValue newPortletPreferenceValue =
			addPortletPreferenceValue();

		_persistence.remove(newPortletPreferenceValue);

		PortletPreferenceValue existingPortletPreferenceValue =
			_persistence.fetchByPrimaryKey(
				newPortletPreferenceValue.getPrimaryKey());

		Assert.assertNull(existingPortletPreferenceValue);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPortletPreferenceValue();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferenceValue newPortletPreferenceValue = _persistence.create(
			pk);

		newPortletPreferenceValue.setMvccVersion(RandomTestUtil.nextLong());

		newPortletPreferenceValue.setCtCollectionId(RandomTestUtil.nextLong());

		newPortletPreferenceValue.setCompanyId(RandomTestUtil.nextLong());

		newPortletPreferenceValue.setPortletPreferencesId(
			RandomTestUtil.nextLong());

		newPortletPreferenceValue.setIndex(RandomTestUtil.nextInt());

		newPortletPreferenceValue.setLargeValue(RandomTestUtil.randomString());

		newPortletPreferenceValue.setName(RandomTestUtil.randomString());

		newPortletPreferenceValue.setReadOnly(RandomTestUtil.randomBoolean());

		newPortletPreferenceValue.setSmallValue(RandomTestUtil.randomString());

		_portletPreferenceValues.add(
			_persistence.update(newPortletPreferenceValue));

		PortletPreferenceValue existingPortletPreferenceValue =
			_persistence.findByPrimaryKey(
				newPortletPreferenceValue.getPrimaryKey());

		Assert.assertEquals(
			existingPortletPreferenceValue.getMvccVersion(),
			newPortletPreferenceValue.getMvccVersion());
		Assert.assertEquals(
			existingPortletPreferenceValue.getCtCollectionId(),
			newPortletPreferenceValue.getCtCollectionId());
		Assert.assertEquals(
			existingPortletPreferenceValue.getPortletPreferenceValueId(),
			newPortletPreferenceValue.getPortletPreferenceValueId());
		Assert.assertEquals(
			existingPortletPreferenceValue.getCompanyId(),
			newPortletPreferenceValue.getCompanyId());
		Assert.assertEquals(
			existingPortletPreferenceValue.getPortletPreferencesId(),
			newPortletPreferenceValue.getPortletPreferencesId());
		Assert.assertEquals(
			existingPortletPreferenceValue.getIndex(),
			newPortletPreferenceValue.getIndex());
		Assert.assertEquals(
			existingPortletPreferenceValue.getLargeValue(),
			newPortletPreferenceValue.getLargeValue());
		Assert.assertEquals(
			existingPortletPreferenceValue.getName(),
			newPortletPreferenceValue.getName());
		Assert.assertEquals(
			existingPortletPreferenceValue.isReadOnly(),
			newPortletPreferenceValue.isReadOnly());
		Assert.assertEquals(
			existingPortletPreferenceValue.getSmallValue(),
			newPortletPreferenceValue.getSmallValue());
	}

	@Test
	public void testCountByPortletPreferencesId() throws Exception {
		_persistence.countByPortletPreferencesId(RandomTestUtil.nextLong());

		_persistence.countByPortletPreferencesId(0L);
	}

	@Test
	public void testCountByP_N() throws Exception {
		_persistence.countByP_N(RandomTestUtil.nextLong(), "");

		_persistence.countByP_N(0L, "null");

		_persistence.countByP_N(0L, (String)null);
	}

	@Test
	public void testCountByC_N_SV() throws Exception {
		_persistence.countByC_N_SV(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_N_SV(0L, "null", "null");

		_persistence.countByC_N_SV(0L, (String)null, (String)null);
	}

	@Test
	public void testCountByP_I_N() throws Exception {
		_persistence.countByP_I_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(), "");

		_persistence.countByP_I_N(0L, 0, "null");

		_persistence.countByP_I_N(0L, 0, (String)null);
	}

	@Test
	public void testCountByP_N_SV() throws Exception {
		_persistence.countByP_N_SV(RandomTestUtil.nextLong(), "", "");

		_persistence.countByP_N_SV(0L, "null", "null");

		_persistence.countByP_N_SV(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PortletPreferenceValue newPortletPreferenceValue =
			addPortletPreferenceValue();

		PortletPreferenceValue existingPortletPreferenceValue =
			_persistence.findByPrimaryKey(
				newPortletPreferenceValue.getPrimaryKey());

		Assert.assertEquals(
			existingPortletPreferenceValue, newPortletPreferenceValue);
	}

	@Test(expected = NoSuchPortletPreferenceValueException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PortletPreferenceValue> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PortletPreferenceValue", "mvccVersion", true, "ctCollectionId",
			true, "portletPreferenceValueId", true, "companyId", true,
			"portletPreferencesId", true, "index", true, "name", true,
			"readOnly", true, "smallValue", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PortletPreferenceValue newPortletPreferenceValue =
			addPortletPreferenceValue();

		PortletPreferenceValue existingPortletPreferenceValue =
			_persistence.fetchByPrimaryKey(
				newPortletPreferenceValue.getPrimaryKey());

		Assert.assertEquals(
			existingPortletPreferenceValue, newPortletPreferenceValue);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PortletPreferenceValue missingPortletPreferenceValue =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingPortletPreferenceValue);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PortletPreferenceValue newPortletPreferenceValue1 =
			addPortletPreferenceValue();
		PortletPreferenceValue newPortletPreferenceValue2 =
			addPortletPreferenceValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortletPreferenceValue1.getPrimaryKey());
		primaryKeys.add(newPortletPreferenceValue2.getPrimaryKey());

		Map<Serializable, PortletPreferenceValue> portletPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, portletPreferenceValues.size());
		Assert.assertEquals(
			newPortletPreferenceValue1,
			portletPreferenceValues.get(
				newPortletPreferenceValue1.getPrimaryKey()));
		Assert.assertEquals(
			newPortletPreferenceValue2,
			portletPreferenceValues.get(
				newPortletPreferenceValue2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PortletPreferenceValue> portletPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portletPreferenceValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PortletPreferenceValue newPortletPreferenceValue =
			addPortletPreferenceValue();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortletPreferenceValue.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PortletPreferenceValue> portletPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portletPreferenceValues.size());
		Assert.assertEquals(
			newPortletPreferenceValue,
			portletPreferenceValues.get(
				newPortletPreferenceValue.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PortletPreferenceValue> portletPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(portletPreferenceValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PortletPreferenceValue newPortletPreferenceValue =
			addPortletPreferenceValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPortletPreferenceValue.getPrimaryKey());

		Map<Serializable, PortletPreferenceValue> portletPreferenceValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, portletPreferenceValues.size());
		Assert.assertEquals(
			newPortletPreferenceValue,
			portletPreferenceValues.get(
				newPortletPreferenceValue.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PortletPreferenceValue newPortletPreferenceValue =
			addPortletPreferenceValue();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newPortletPreferenceValue.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		PortletPreferenceValue portletPreferenceValue) {

		Assert.assertEquals(
			Long.valueOf(portletPreferenceValue.getPortletPreferencesId()),
			ReflectionTestUtil.<Long>invoke(
				portletPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletPreferencesId"));
		Assert.assertEquals(
			Integer.valueOf(portletPreferenceValue.getIndex()),
			ReflectionTestUtil.<Integer>invoke(
				portletPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "index_"));
		Assert.assertEquals(
			portletPreferenceValue.getName(),
			ReflectionTestUtil.invoke(
				portletPreferenceValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected PortletPreferenceValue addPortletPreferenceValue()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		PortletPreferenceValue portletPreferenceValue = _persistence.create(pk);

		portletPreferenceValue.setMvccVersion(RandomTestUtil.nextLong());

		portletPreferenceValue.setCtCollectionId(RandomTestUtil.nextLong());

		portletPreferenceValue.setCompanyId(RandomTestUtil.nextLong());

		portletPreferenceValue.setPortletPreferencesId(
			RandomTestUtil.nextLong());

		portletPreferenceValue.setIndex(RandomTestUtil.nextInt());

		portletPreferenceValue.setLargeValue(RandomTestUtil.randomString());

		portletPreferenceValue.setName(RandomTestUtil.randomString());

		portletPreferenceValue.setReadOnly(RandomTestUtil.randomBoolean());

		portletPreferenceValue.setSmallValue(RandomTestUtil.randomString());

		_portletPreferenceValues.add(
			_persistence.update(portletPreferenceValue));

		return portletPreferenceValue;
	}

	private List<PortletPreferenceValue> _portletPreferenceValues =
		new ArrayList<PortletPreferenceValue>();
	private PortletPreferenceValuePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}