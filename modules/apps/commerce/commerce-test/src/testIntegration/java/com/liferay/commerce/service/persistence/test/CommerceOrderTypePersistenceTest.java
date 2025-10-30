/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.DuplicateCommerceOrderTypeExternalReferenceCodeException;
import com.liferay.commerce.exception.NoSuchOrderTypeException;
import com.liferay.commerce.model.CommerceOrderType;
import com.liferay.commerce.service.persistence.CommerceOrderTypePersistence;
import com.liferay.commerce.service.persistence.CommerceOrderTypeUtil;
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
public class CommerceOrderTypePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceOrderTypeUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceOrderType> iterator = _commerceOrderTypes.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderType commerceOrderType = _persistence.create(pk);

		Assert.assertNotNull(commerceOrderType);

		Assert.assertEquals(commerceOrderType.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		_persistence.remove(newCommerceOrderType);

		CommerceOrderType existingCommerceOrderType =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderType.getPrimaryKey());

		Assert.assertNull(existingCommerceOrderType);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceOrderType();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderType newCommerceOrderType = _persistence.create(pk);

		newCommerceOrderType.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceOrderType.setUuid(RandomTestUtil.randomString());

		newCommerceOrderType.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceOrderType.setCompanyId(RandomTestUtil.nextLong());

		newCommerceOrderType.setUserId(RandomTestUtil.nextLong());

		newCommerceOrderType.setUserName(RandomTestUtil.randomString());

		newCommerceOrderType.setCreateDate(RandomTestUtil.nextDate());

		newCommerceOrderType.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceOrderType.setName(RandomTestUtil.randomString());

		newCommerceOrderType.setDescription(RandomTestUtil.randomString());

		newCommerceOrderType.setActive(RandomTestUtil.randomBoolean());

		newCommerceOrderType.setDisplayDate(RandomTestUtil.nextDate());

		newCommerceOrderType.setDisplayOrder(RandomTestUtil.nextInt());

		newCommerceOrderType.setExpirationDate(RandomTestUtil.nextDate());

		newCommerceOrderType.setLastPublishDate(RandomTestUtil.nextDate());

		newCommerceOrderType.setStatus(RandomTestUtil.nextInt());

		newCommerceOrderType.setStatusByUserId(RandomTestUtil.nextLong());

		newCommerceOrderType.setStatusByUserName(RandomTestUtil.randomString());

		newCommerceOrderType.setStatusDate(RandomTestUtil.nextDate());

		_commerceOrderTypes.add(_persistence.update(newCommerceOrderType));

		CommerceOrderType existingCommerceOrderType =
			_persistence.findByPrimaryKey(newCommerceOrderType.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceOrderType.getMvccVersion(),
			newCommerceOrderType.getMvccVersion());
		Assert.assertEquals(
			existingCommerceOrderType.getUuid(),
			newCommerceOrderType.getUuid());
		Assert.assertEquals(
			existingCommerceOrderType.getExternalReferenceCode(),
			newCommerceOrderType.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceOrderType.getCommerceOrderTypeId(),
			newCommerceOrderType.getCommerceOrderTypeId());
		Assert.assertEquals(
			existingCommerceOrderType.getCompanyId(),
			newCommerceOrderType.getCompanyId());
		Assert.assertEquals(
			existingCommerceOrderType.getUserId(),
			newCommerceOrderType.getUserId());
		Assert.assertEquals(
			existingCommerceOrderType.getUserName(),
			newCommerceOrderType.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrderType.getCreateDate()),
			Time.getShortTimestamp(newCommerceOrderType.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrderType.getModifiedDate()),
			Time.getShortTimestamp(newCommerceOrderType.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceOrderType.getName(),
			newCommerceOrderType.getName());
		Assert.assertEquals(
			existingCommerceOrderType.getDescription(),
			newCommerceOrderType.getDescription());
		Assert.assertEquals(
			existingCommerceOrderType.isActive(),
			newCommerceOrderType.isActive());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrderType.getDisplayDate()),
			Time.getShortTimestamp(newCommerceOrderType.getDisplayDate()));
		Assert.assertEquals(
			existingCommerceOrderType.getDisplayOrder(),
			newCommerceOrderType.getDisplayOrder());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrderType.getExpirationDate()),
			Time.getShortTimestamp(newCommerceOrderType.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceOrderType.getLastPublishDate()),
			Time.getShortTimestamp(newCommerceOrderType.getLastPublishDate()));
		Assert.assertEquals(
			existingCommerceOrderType.getStatus(),
			newCommerceOrderType.getStatus());
		Assert.assertEquals(
			existingCommerceOrderType.getStatusByUserId(),
			newCommerceOrderType.getStatusByUserId());
		Assert.assertEquals(
			existingCommerceOrderType.getStatusByUserName(),
			newCommerceOrderType.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceOrderType.getStatusDate()),
			Time.getShortTimestamp(newCommerceOrderType.getStatusDate()));
	}

	@Test(
		expected = DuplicateCommerceOrderTypeExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceOrderType commerceOrderType = addCommerceOrderType();

		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		newCommerceOrderType.setCompanyId(commerceOrderType.getCompanyId());

		newCommerceOrderType = _persistence.update(newCommerceOrderType);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceOrderType);

		newCommerceOrderType.setExternalReferenceCode(
			commerceOrderType.getExternalReferenceCode());

		_persistence.update(newCommerceOrderType);
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
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByLtE_S() throws Exception {
		_persistence.countByLtE_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtE_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		CommerceOrderType existingCommerceOrderType =
			_persistence.findByPrimaryKey(newCommerceOrderType.getPrimaryKey());

		Assert.assertEquals(existingCommerceOrderType, newCommerceOrderType);
	}

	@Test(expected = NoSuchOrderTypeException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceOrderType> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceOrderType", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceOrderTypeId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "name", true, "description", true,
			"active", true, "displayDate", true, "displayOrder", true,
			"expirationDate", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		CommerceOrderType existingCommerceOrderType =
			_persistence.fetchByPrimaryKey(
				newCommerceOrderType.getPrimaryKey());

		Assert.assertEquals(existingCommerceOrderType, newCommerceOrderType);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderType missingCommerceOrderType =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceOrderType);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceOrderType newCommerceOrderType1 = addCommerceOrderType();
		CommerceOrderType newCommerceOrderType2 = addCommerceOrderType();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderType1.getPrimaryKey());
		primaryKeys.add(newCommerceOrderType2.getPrimaryKey());

		Map<Serializable, CommerceOrderType> commerceOrderTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceOrderTypes.size());
		Assert.assertEquals(
			newCommerceOrderType1,
			commerceOrderTypes.get(newCommerceOrderType1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceOrderType2,
			commerceOrderTypes.get(newCommerceOrderType2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceOrderType> commerceOrderTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderTypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderType.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceOrderType> commerceOrderTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderTypes.size());
		Assert.assertEquals(
			newCommerceOrderType,
			commerceOrderTypes.get(newCommerceOrderType.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceOrderType> commerceOrderTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceOrderTypes.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceOrderType.getPrimaryKey());

		Map<Serializable, CommerceOrderType> commerceOrderTypes =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceOrderTypes.size());
		Assert.assertEquals(
			newCommerceOrderType,
			commerceOrderTypes.get(newCommerceOrderType.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceOrderType newCommerceOrderType = addCommerceOrderType();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceOrderType.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceOrderType commerceOrderType) {
		Assert.assertEquals(
			commerceOrderType.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceOrderType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceOrderType.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceOrderType, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceOrderType addCommerceOrderType() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceOrderType commerceOrderType = _persistence.create(pk);

		commerceOrderType.setMvccVersion(RandomTestUtil.nextLong());

		commerceOrderType.setUuid(RandomTestUtil.randomString());

		commerceOrderType.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceOrderType.setCompanyId(RandomTestUtil.nextLong());

		commerceOrderType.setUserId(RandomTestUtil.nextLong());

		commerceOrderType.setUserName(RandomTestUtil.randomString());

		commerceOrderType.setCreateDate(RandomTestUtil.nextDate());

		commerceOrderType.setModifiedDate(RandomTestUtil.nextDate());

		commerceOrderType.setName(RandomTestUtil.randomString());

		commerceOrderType.setDescription(RandomTestUtil.randomString());

		commerceOrderType.setActive(RandomTestUtil.randomBoolean());

		commerceOrderType.setDisplayDate(RandomTestUtil.nextDate());

		commerceOrderType.setDisplayOrder(RandomTestUtil.nextInt());

		commerceOrderType.setExpirationDate(RandomTestUtil.nextDate());

		commerceOrderType.setLastPublishDate(RandomTestUtil.nextDate());

		commerceOrderType.setStatus(RandomTestUtil.nextInt());

		commerceOrderType.setStatusByUserId(RandomTestUtil.nextLong());

		commerceOrderType.setStatusByUserName(RandomTestUtil.randomString());

		commerceOrderType.setStatusDate(RandomTestUtil.nextDate());

		_commerceOrderTypes.add(_persistence.update(commerceOrderType));

		return commerceOrderType;
	}

	private List<CommerceOrderType> _commerceOrderTypes =
		new ArrayList<CommerceOrderType>();
	private CommerceOrderTypePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}