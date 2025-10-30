/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchShippingMethodException;
import com.liferay.commerce.model.CommerceShippingMethod;
import com.liferay.commerce.service.persistence.CommerceShippingMethodPersistence;
import com.liferay.commerce.service.persistence.CommerceShippingMethodUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CommerceShippingMethodPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceShippingMethodUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceShippingMethod> iterator =
			_commerceShippingMethods.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingMethod commerceShippingMethod = _persistence.create(pk);

		Assert.assertNotNull(commerceShippingMethod);

		Assert.assertEquals(commerceShippingMethod.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceShippingMethod newCommerceShippingMethod =
			addCommerceShippingMethod();

		_persistence.remove(newCommerceShippingMethod);

		CommerceShippingMethod existingCommerceShippingMethod =
			_persistence.fetchByPrimaryKey(
				newCommerceShippingMethod.getPrimaryKey());

		Assert.assertNull(existingCommerceShippingMethod);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceShippingMethod();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingMethod newCommerceShippingMethod = _persistence.create(
			pk);

		newCommerceShippingMethod.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceShippingMethod.setGroupId(RandomTestUtil.nextLong());

		newCommerceShippingMethod.setCompanyId(RandomTestUtil.nextLong());

		newCommerceShippingMethod.setUserId(RandomTestUtil.nextLong());

		newCommerceShippingMethod.setUserName(RandomTestUtil.randomString());

		newCommerceShippingMethod.setCreateDate(RandomTestUtil.nextDate());

		newCommerceShippingMethod.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceShippingMethod.setName(RandomTestUtil.randomString());

		newCommerceShippingMethod.setDescription(RandomTestUtil.randomString());

		newCommerceShippingMethod.setActive(RandomTestUtil.randomBoolean());

		newCommerceShippingMethod.setEngineKey(RandomTestUtil.randomString());

		newCommerceShippingMethod.setImageId(RandomTestUtil.nextLong());

		newCommerceShippingMethod.setPriority(RandomTestUtil.nextDouble());

		newCommerceShippingMethod.setTrackingURL(RandomTestUtil.randomString());

		newCommerceShippingMethod.setTypeSettings(
			RandomTestUtil.randomString());

		_commerceShippingMethods.add(
			_persistence.update(newCommerceShippingMethod));

		CommerceShippingMethod existingCommerceShippingMethod =
			_persistence.findByPrimaryKey(
				newCommerceShippingMethod.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingMethod.getMvccVersion(),
			newCommerceShippingMethod.getMvccVersion());
		Assert.assertEquals(
			existingCommerceShippingMethod.getCommerceShippingMethodId(),
			newCommerceShippingMethod.getCommerceShippingMethodId());
		Assert.assertEquals(
			existingCommerceShippingMethod.getGroupId(),
			newCommerceShippingMethod.getGroupId());
		Assert.assertEquals(
			existingCommerceShippingMethod.getCompanyId(),
			newCommerceShippingMethod.getCompanyId());
		Assert.assertEquals(
			existingCommerceShippingMethod.getUserId(),
			newCommerceShippingMethod.getUserId());
		Assert.assertEquals(
			existingCommerceShippingMethod.getUserName(),
			newCommerceShippingMethod.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingMethod.getCreateDate()),
			Time.getShortTimestamp(newCommerceShippingMethod.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingMethod.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceShippingMethod.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceShippingMethod.getName(),
			newCommerceShippingMethod.getName());
		Assert.assertEquals(
			existingCommerceShippingMethod.getDescription(),
			newCommerceShippingMethod.getDescription());
		Assert.assertEquals(
			existingCommerceShippingMethod.isActive(),
			newCommerceShippingMethod.isActive());
		Assert.assertEquals(
			existingCommerceShippingMethod.getEngineKey(),
			newCommerceShippingMethod.getEngineKey());
		Assert.assertEquals(
			existingCommerceShippingMethod.getImageId(),
			newCommerceShippingMethod.getImageId());
		AssertUtils.assertEquals(
			existingCommerceShippingMethod.getPriority(),
			newCommerceShippingMethod.getPriority());
		Assert.assertEquals(
			existingCommerceShippingMethod.getTrackingURL(),
			newCommerceShippingMethod.getTrackingURL());
		Assert.assertEquals(
			existingCommerceShippingMethod.getTypeSettings(),
			newCommerceShippingMethod.getTypeSettings());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_E() throws Exception {
		_persistence.countByG_E(RandomTestUtil.nextLong(), "");

		_persistence.countByG_E(0L, "null");

		_persistence.countByG_E(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceShippingMethod newCommerceShippingMethod =
			addCommerceShippingMethod();

		CommerceShippingMethod existingCommerceShippingMethod =
			_persistence.findByPrimaryKey(
				newCommerceShippingMethod.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingMethod, newCommerceShippingMethod);
	}

	@Test(expected = NoSuchShippingMethodException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceShippingMethod> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceShippingMethod", "mvccVersion", true,
			"commerceShippingMethodId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "name", true, "description", true, "active",
			true, "engineKey", true, "imageId", true, "priority", true,
			"trackingURL", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceShippingMethod newCommerceShippingMethod =
			addCommerceShippingMethod();

		CommerceShippingMethod existingCommerceShippingMethod =
			_persistence.fetchByPrimaryKey(
				newCommerceShippingMethod.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingMethod, newCommerceShippingMethod);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingMethod missingCommerceShippingMethod =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceShippingMethod);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceShippingMethod newCommerceShippingMethod1 =
			addCommerceShippingMethod();
		CommerceShippingMethod newCommerceShippingMethod2 =
			addCommerceShippingMethod();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingMethod1.getPrimaryKey());
		primaryKeys.add(newCommerceShippingMethod2.getPrimaryKey());

		Map<Serializable, CommerceShippingMethod> commerceShippingMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceShippingMethods.size());
		Assert.assertEquals(
			newCommerceShippingMethod1,
			commerceShippingMethods.get(
				newCommerceShippingMethod1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceShippingMethod2,
			commerceShippingMethods.get(
				newCommerceShippingMethod2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceShippingMethod> commerceShippingMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShippingMethods.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceShippingMethod newCommerceShippingMethod =
			addCommerceShippingMethod();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingMethod.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceShippingMethod> commerceShippingMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShippingMethods.size());
		Assert.assertEquals(
			newCommerceShippingMethod,
			commerceShippingMethods.get(
				newCommerceShippingMethod.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceShippingMethod> commerceShippingMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShippingMethods.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceShippingMethod newCommerceShippingMethod =
			addCommerceShippingMethod();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingMethod.getPrimaryKey());

		Map<Serializable, CommerceShippingMethod> commerceShippingMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShippingMethods.size());
		Assert.assertEquals(
			newCommerceShippingMethod,
			commerceShippingMethods.get(
				newCommerceShippingMethod.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceShippingMethod newCommerceShippingMethod =
			addCommerceShippingMethod();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceShippingMethod.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceShippingMethod commerceShippingMethod) {

		Assert.assertEquals(
			Long.valueOf(commerceShippingMethod.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingMethod, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			commerceShippingMethod.getEngineKey(),
			ReflectionTestUtil.invoke(
				commerceShippingMethod, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "engineKey"));
	}

	protected CommerceShippingMethod addCommerceShippingMethod()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceShippingMethod commerceShippingMethod = _persistence.create(pk);

		commerceShippingMethod.setMvccVersion(RandomTestUtil.nextLong());

		commerceShippingMethod.setGroupId(RandomTestUtil.nextLong());

		commerceShippingMethod.setCompanyId(RandomTestUtil.nextLong());

		commerceShippingMethod.setUserId(RandomTestUtil.nextLong());

		commerceShippingMethod.setUserName(RandomTestUtil.randomString());

		commerceShippingMethod.setCreateDate(RandomTestUtil.nextDate());

		commerceShippingMethod.setModifiedDate(RandomTestUtil.nextDate());

		commerceShippingMethod.setName(RandomTestUtil.randomString());

		commerceShippingMethod.setDescription(RandomTestUtil.randomString());

		commerceShippingMethod.setActive(RandomTestUtil.randomBoolean());

		commerceShippingMethod.setEngineKey(RandomTestUtil.randomString());

		commerceShippingMethod.setImageId(RandomTestUtil.nextLong());

		commerceShippingMethod.setPriority(RandomTestUtil.nextDouble());

		commerceShippingMethod.setTrackingURL(RandomTestUtil.randomString());

		commerceShippingMethod.setTypeSettings(RandomTestUtil.randomString());

		_commerceShippingMethods.add(
			_persistence.update(commerceShippingMethod));

		return commerceShippingMethod;
	}

	private List<CommerceShippingMethod> _commerceShippingMethods =
		new ArrayList<CommerceShippingMethod>();
	private CommerceShippingMethodPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}