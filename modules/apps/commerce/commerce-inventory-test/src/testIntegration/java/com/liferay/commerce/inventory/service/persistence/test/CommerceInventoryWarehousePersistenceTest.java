/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.inventory.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.inventory.exception.DuplicateCommerceInventoryWarehouseExternalReferenceCodeException;
import com.liferay.commerce.inventory.exception.NoSuchInventoryWarehouseException;
import com.liferay.commerce.inventory.model.CommerceInventoryWarehouse;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehousePersistence;
import com.liferay.commerce.inventory.service.persistence.CommerceInventoryWarehouseUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class CommerceInventoryWarehousePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.inventory.service"));

	@Before
	public void setUp() {
		_persistence = CommerceInventoryWarehouseUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceInventoryWarehouse> iterator =
			_commerceInventoryWarehouses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_persistence.create(pk);

		Assert.assertNotNull(commerceInventoryWarehouse);

		Assert.assertEquals(commerceInventoryWarehouse.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		_persistence.remove(newCommerceInventoryWarehouse);

		CommerceInventoryWarehouse existingCommerceInventoryWarehouse =
			_persistence.fetchByPrimaryKey(
				newCommerceInventoryWarehouse.getPrimaryKey());

		Assert.assertNull(existingCommerceInventoryWarehouse);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceInventoryWarehouse();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			_persistence.create(pk);

		newCommerceInventoryWarehouse.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceInventoryWarehouse.setUuid(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setCompanyId(RandomTestUtil.nextLong());

		newCommerceInventoryWarehouse.setUserId(RandomTestUtil.nextLong());

		newCommerceInventoryWarehouse.setUserName(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setCreateDate(RandomTestUtil.nextDate());

		newCommerceInventoryWarehouse.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceInventoryWarehouse.setName(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setDescription(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setActive(RandomTestUtil.randomBoolean());

		newCommerceInventoryWarehouse.setStreet1(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setStreet2(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setStreet3(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setCity(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setZip(RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setCommerceRegionCode(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setCountryTwoLettersISOCode(
			RandomTestUtil.randomString());

		newCommerceInventoryWarehouse.setLatitude(RandomTestUtil.nextDouble());

		newCommerceInventoryWarehouse.setLongitude(RandomTestUtil.nextDouble());

		newCommerceInventoryWarehouse.setType(RandomTestUtil.randomString());

		_commerceInventoryWarehouses.add(
			_persistence.update(newCommerceInventoryWarehouse));

		CommerceInventoryWarehouse existingCommerceInventoryWarehouse =
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouse.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getMvccVersion(),
			newCommerceInventoryWarehouse.getMvccVersion());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getUuid(),
			newCommerceInventoryWarehouse.getUuid());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getExternalReferenceCode(),
			newCommerceInventoryWarehouse.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.
				getCommerceInventoryWarehouseId(),
			newCommerceInventoryWarehouse.getCommerceInventoryWarehouseId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getCompanyId(),
			newCommerceInventoryWarehouse.getCompanyId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getUserId(),
			newCommerceInventoryWarehouse.getUserId());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getUserName(),
			newCommerceInventoryWarehouse.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryWarehouse.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceInventoryWarehouse.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceInventoryWarehouse.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceInventoryWarehouse.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getName(),
			newCommerceInventoryWarehouse.getName());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getDescription(),
			newCommerceInventoryWarehouse.getDescription());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.isActive(),
			newCommerceInventoryWarehouse.isActive());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getStreet1(),
			newCommerceInventoryWarehouse.getStreet1());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getStreet2(),
			newCommerceInventoryWarehouse.getStreet2());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getStreet3(),
			newCommerceInventoryWarehouse.getStreet3());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getCity(),
			newCommerceInventoryWarehouse.getCity());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getZip(),
			newCommerceInventoryWarehouse.getZip());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getCommerceRegionCode(),
			newCommerceInventoryWarehouse.getCommerceRegionCode());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getCountryTwoLettersISOCode(),
			newCommerceInventoryWarehouse.getCountryTwoLettersISOCode());
		AssertUtils.assertEquals(
			existingCommerceInventoryWarehouse.getLatitude(),
			newCommerceInventoryWarehouse.getLatitude());
		AssertUtils.assertEquals(
			existingCommerceInventoryWarehouse.getLongitude(),
			newCommerceInventoryWarehouse.getLongitude());
		Assert.assertEquals(
			existingCommerceInventoryWarehouse.getType(),
			newCommerceInventoryWarehouse.getType());
	}

	@Test(
		expected = DuplicateCommerceInventoryWarehouseExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceInventoryWarehouse commerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		newCommerceInventoryWarehouse.setCompanyId(
			commerceInventoryWarehouse.getCompanyId());

		newCommerceInventoryWarehouse = _persistence.update(
			newCommerceInventoryWarehouse);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceInventoryWarehouse);

		newCommerceInventoryWarehouse.setExternalReferenceCode(
			commerceInventoryWarehouse.getExternalReferenceCode());

		_persistence.update(newCommerceInventoryWarehouse);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByC_A_C() throws Exception {
		_persistence.countByC_A_C(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByC_A_C(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByC_A_C(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		CommerceInventoryWarehouse existingCommerceInventoryWarehouse =
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouse.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouse, newCommerceInventoryWarehouse);
	}

	@Test(expected = NoSuchInventoryWarehouseException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceInventoryWarehouse>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CIWarehouse", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceInventoryWarehouseId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "name", true, "description", true,
			"active", true, "street1", true, "street2", true, "street3", true,
			"city", true, "zip", true, "commerceRegionCode", true,
			"countryTwoLettersISOCode", true, "latitude", true, "longitude",
			true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		CommerceInventoryWarehouse existingCommerceInventoryWarehouse =
			_persistence.fetchByPrimaryKey(
				newCommerceInventoryWarehouse.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceInventoryWarehouse, newCommerceInventoryWarehouse);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouse missingCommerceInventoryWarehouse =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceInventoryWarehouse);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceInventoryWarehouse newCommerceInventoryWarehouse1 =
			addCommerceInventoryWarehouse();
		CommerceInventoryWarehouse newCommerceInventoryWarehouse2 =
			addCommerceInventoryWarehouse();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouse1.getPrimaryKey());
		primaryKeys.add(newCommerceInventoryWarehouse2.getPrimaryKey());

		Map<Serializable, CommerceInventoryWarehouse>
			commerceInventoryWarehouses = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceInventoryWarehouses.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouse1,
			commerceInventoryWarehouses.get(
				newCommerceInventoryWarehouse1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceInventoryWarehouse2,
			commerceInventoryWarehouses.get(
				newCommerceInventoryWarehouse2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceInventoryWarehouse>
			commerceInventoryWarehouses = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryWarehouses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouse.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceInventoryWarehouse>
			commerceInventoryWarehouses = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryWarehouses.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouse,
			commerceInventoryWarehouses.get(
				newCommerceInventoryWarehouse.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceInventoryWarehouse>
			commerceInventoryWarehouses = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceInventoryWarehouses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceInventoryWarehouse.getPrimaryKey());

		Map<Serializable, CommerceInventoryWarehouse>
			commerceInventoryWarehouses = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceInventoryWarehouses.size());
		Assert.assertEquals(
			newCommerceInventoryWarehouse,
			commerceInventoryWarehouses.get(
				newCommerceInventoryWarehouse.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceInventoryWarehouse newCommerceInventoryWarehouse =
			addCommerceInventoryWarehouse();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceInventoryWarehouse.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceInventoryWarehouse commerceInventoryWarehouse) {

		Assert.assertEquals(
			commerceInventoryWarehouse.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceInventoryWarehouse, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceInventoryWarehouse.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceInventoryWarehouse, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceInventoryWarehouse addCommerceInventoryWarehouse()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceInventoryWarehouse commerceInventoryWarehouse =
			_persistence.create(pk);

		commerceInventoryWarehouse.setMvccVersion(RandomTestUtil.nextLong());

		commerceInventoryWarehouse.setUuid(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceInventoryWarehouse.setCompanyId(RandomTestUtil.nextLong());

		commerceInventoryWarehouse.setUserId(RandomTestUtil.nextLong());

		commerceInventoryWarehouse.setUserName(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setCreateDate(RandomTestUtil.nextDate());

		commerceInventoryWarehouse.setModifiedDate(RandomTestUtil.nextDate());

		commerceInventoryWarehouse.setName(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setDescription(
			RandomTestUtil.randomString());

		commerceInventoryWarehouse.setActive(RandomTestUtil.randomBoolean());

		commerceInventoryWarehouse.setStreet1(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setStreet2(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setStreet3(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setCity(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setZip(RandomTestUtil.randomString());

		commerceInventoryWarehouse.setCommerceRegionCode(
			RandomTestUtil.randomString());

		commerceInventoryWarehouse.setCountryTwoLettersISOCode(
			RandomTestUtil.randomString());

		commerceInventoryWarehouse.setLatitude(RandomTestUtil.nextDouble());

		commerceInventoryWarehouse.setLongitude(RandomTestUtil.nextDouble());

		commerceInventoryWarehouse.setType(RandomTestUtil.randomString());

		_commerceInventoryWarehouses.add(
			_persistence.update(commerceInventoryWarehouse));

		return commerceInventoryWarehouse;
	}

	private List<CommerceInventoryWarehouse> _commerceInventoryWarehouses =
		new ArrayList<CommerceInventoryWarehouse>();
	private CommerceInventoryWarehousePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}