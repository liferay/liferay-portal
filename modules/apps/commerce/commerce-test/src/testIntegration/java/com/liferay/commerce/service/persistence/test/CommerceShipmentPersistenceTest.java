/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.DuplicateCommerceShipmentExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchShipmentException;
import com.liferay.commerce.model.CommerceShipment;
import com.liferay.commerce.service.persistence.CommerceShipmentPersistence;
import com.liferay.commerce.service.persistence.CommerceShipmentUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
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
public class CommerceShipmentPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceShipmentUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceShipment> iterator = _commerceShipments.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShipment commerceShipment = _persistence.create(pk);

		Assert.assertNotNull(commerceShipment);

		Assert.assertEquals(commerceShipment.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceShipment newCommerceShipment = addCommerceShipment();

		_persistence.remove(newCommerceShipment);

		CommerceShipment existingCommerceShipment =
			_persistence.fetchByPrimaryKey(newCommerceShipment.getPrimaryKey());

		Assert.assertNull(existingCommerceShipment);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceShipment();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShipment newCommerceShipment = _persistence.create(pk);

		newCommerceShipment.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceShipment.setUuid(RandomTestUtil.randomString());

		newCommerceShipment.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceShipment.setGroupId(RandomTestUtil.nextLong());

		newCommerceShipment.setCompanyId(RandomTestUtil.nextLong());

		newCommerceShipment.setUserId(RandomTestUtil.nextLong());

		newCommerceShipment.setUserName(RandomTestUtil.randomString());

		newCommerceShipment.setCreateDate(RandomTestUtil.nextDate());

		newCommerceShipment.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceShipment.setCommerceAccountId(RandomTestUtil.nextLong());

		newCommerceShipment.setCommerceAddressId(RandomTestUtil.nextLong());

		newCommerceShipment.setCommerceShippingMethodId(
			RandomTestUtil.nextLong());

		newCommerceShipment.setCarrier(RandomTestUtil.randomString());

		newCommerceShipment.setExpectedDate(RandomTestUtil.nextDate());

		newCommerceShipment.setShippingDate(RandomTestUtil.nextDate());

		newCommerceShipment.setShippingOptionName(
			RandomTestUtil.randomString());

		newCommerceShipment.setTrackingNumber(RandomTestUtil.randomString());

		newCommerceShipment.setTrackingURL(RandomTestUtil.randomString());

		newCommerceShipment.setStatus(RandomTestUtil.nextInt());

		_commerceShipments.add(_persistence.update(newCommerceShipment));

		CommerceShipment existingCommerceShipment =
			_persistence.findByPrimaryKey(newCommerceShipment.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShipment.getMvccVersion(),
			newCommerceShipment.getMvccVersion());
		Assert.assertEquals(
			existingCommerceShipment.getUuid(), newCommerceShipment.getUuid());
		Assert.assertEquals(
			existingCommerceShipment.getExternalReferenceCode(),
			newCommerceShipment.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceShipment.getCommerceShipmentId(),
			newCommerceShipment.getCommerceShipmentId());
		Assert.assertEquals(
			existingCommerceShipment.getGroupId(),
			newCommerceShipment.getGroupId());
		Assert.assertEquals(
			existingCommerceShipment.getCompanyId(),
			newCommerceShipment.getCompanyId());
		Assert.assertEquals(
			existingCommerceShipment.getUserId(),
			newCommerceShipment.getUserId());
		Assert.assertEquals(
			existingCommerceShipment.getUserName(),
			newCommerceShipment.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceShipment.getCreateDate()),
			Time.getShortTimestamp(newCommerceShipment.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceShipment.getModifiedDate()),
			Time.getShortTimestamp(newCommerceShipment.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceShipment.getCommerceAccountId(),
			newCommerceShipment.getCommerceAccountId());
		Assert.assertEquals(
			existingCommerceShipment.getCommerceAddressId(),
			newCommerceShipment.getCommerceAddressId());
		Assert.assertEquals(
			existingCommerceShipment.getCommerceShippingMethodId(),
			newCommerceShipment.getCommerceShippingMethodId());
		Assert.assertEquals(
			existingCommerceShipment.getCarrier(),
			newCommerceShipment.getCarrier());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceShipment.getExpectedDate()),
			Time.getShortTimestamp(newCommerceShipment.getExpectedDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceShipment.getShippingDate()),
			Time.getShortTimestamp(newCommerceShipment.getShippingDate()));
		Assert.assertEquals(
			existingCommerceShipment.getShippingOptionName(),
			newCommerceShipment.getShippingOptionName());
		Assert.assertEquals(
			existingCommerceShipment.getTrackingNumber(),
			newCommerceShipment.getTrackingNumber());
		Assert.assertEquals(
			existingCommerceShipment.getTrackingURL(),
			newCommerceShipment.getTrackingURL());
		Assert.assertEquals(
			existingCommerceShipment.getStatus(),
			newCommerceShipment.getStatus());
	}

	@Test(
		expected = DuplicateCommerceShipmentExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceShipment commerceShipment = addCommerceShipment();

		CommerceShipment newCommerceShipment = addCommerceShipment();

		newCommerceShipment.setCompanyId(commerceShipment.getCompanyId());

		newCommerceShipment = _persistence.update(newCommerceShipment);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceShipment);

		newCommerceShipment.setExternalReferenceCode(
			commerceShipment.getExternalReferenceCode());

		_persistence.update(newCommerceShipment);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_CArrayable() throws Exception {
		_persistence.countByG_C(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong());
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByG_SArrayable() throws Exception {
		_persistence.countByG_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceShipment newCommerceShipment = addCommerceShipment();

		CommerceShipment existingCommerceShipment =
			_persistence.findByPrimaryKey(newCommerceShipment.getPrimaryKey());

		Assert.assertEquals(existingCommerceShipment, newCommerceShipment);
	}

	@Test(expected = NoSuchShipmentException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceShipment> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceShipment", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceShipmentId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "commerceAccountId",
			true, "commerceAddressId", true, "commerceShippingMethodId", true,
			"carrier", true, "expectedDate", true, "shippingDate", true,
			"trackingNumber", true, "trackingURL", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceShipment newCommerceShipment = addCommerceShipment();

		CommerceShipment existingCommerceShipment =
			_persistence.fetchByPrimaryKey(newCommerceShipment.getPrimaryKey());

		Assert.assertEquals(existingCommerceShipment, newCommerceShipment);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShipment missingCommerceShipment =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceShipment);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceShipment newCommerceShipment1 = addCommerceShipment();
		CommerceShipment newCommerceShipment2 = addCommerceShipment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShipment1.getPrimaryKey());
		primaryKeys.add(newCommerceShipment2.getPrimaryKey());

		Map<Serializable, CommerceShipment> commerceShipments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceShipments.size());
		Assert.assertEquals(
			newCommerceShipment1,
			commerceShipments.get(newCommerceShipment1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceShipment2,
			commerceShipments.get(newCommerceShipment2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceShipment> commerceShipments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShipments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceShipment newCommerceShipment = addCommerceShipment();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShipment.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceShipment> commerceShipments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShipments.size());
		Assert.assertEquals(
			newCommerceShipment,
			commerceShipments.get(newCommerceShipment.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceShipment> commerceShipments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShipments.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceShipment newCommerceShipment = addCommerceShipment();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShipment.getPrimaryKey());

		Map<Serializable, CommerceShipment> commerceShipments =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShipments.size());
		Assert.assertEquals(
			newCommerceShipment,
			commerceShipments.get(newCommerceShipment.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceShipment newCommerceShipment = addCommerceShipment();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCommerceShipment.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceShipment commerceShipment) {
		Assert.assertEquals(
			commerceShipment.getUuid(),
			ReflectionTestUtil.invoke(
				commerceShipment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceShipment.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShipment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			commerceShipment.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceShipment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceShipment.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShipment, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceShipment addCommerceShipment() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShipment commerceShipment = _persistence.create(pk);

		commerceShipment.setMvccVersion(RandomTestUtil.nextLong());

		commerceShipment.setUuid(RandomTestUtil.randomString());

		commerceShipment.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceShipment.setGroupId(RandomTestUtil.nextLong());

		commerceShipment.setCompanyId(RandomTestUtil.nextLong());

		commerceShipment.setUserId(RandomTestUtil.nextLong());

		commerceShipment.setUserName(RandomTestUtil.randomString());

		commerceShipment.setCreateDate(RandomTestUtil.nextDate());

		commerceShipment.setModifiedDate(RandomTestUtil.nextDate());

		commerceShipment.setCommerceAccountId(RandomTestUtil.nextLong());

		commerceShipment.setCommerceAddressId(RandomTestUtil.nextLong());

		commerceShipment.setCommerceShippingMethodId(RandomTestUtil.nextLong());

		commerceShipment.setCarrier(RandomTestUtil.randomString());

		commerceShipment.setExpectedDate(RandomTestUtil.nextDate());

		commerceShipment.setShippingDate(RandomTestUtil.nextDate());

		commerceShipment.setShippingOptionName(RandomTestUtil.randomString());

		commerceShipment.setTrackingNumber(RandomTestUtil.randomString());

		commerceShipment.setTrackingURL(RandomTestUtil.randomString());

		commerceShipment.setStatus(RandomTestUtil.nextInt());

		_commerceShipments.add(_persistence.update(commerceShipment));

		return commerceShipment;
	}

	private List<CommerceShipment> _commerceShipments =
		new ArrayList<CommerceShipment>();
	private CommerceShipmentPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}