/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.exception.NoSuchDiscountAccountRelException;
import com.liferay.commerce.discount.model.CommerceDiscountAccountRel;
import com.liferay.commerce.discount.service.CommerceDiscountAccountRelLocalServiceUtil;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountAccountRelPersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountAccountRelUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class CommerceDiscountAccountRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.discount.service"));

	@Before
	public void setUp() {
		_persistence = CommerceDiscountAccountRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceDiscountAccountRel> iterator =
			_commerceDiscountAccountRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			_persistence.create(pk);

		Assert.assertNotNull(commerceDiscountAccountRel);

		Assert.assertEquals(commerceDiscountAccountRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		_persistence.remove(newCommerceDiscountAccountRel);

		CommerceDiscountAccountRel existingCommerceDiscountAccountRel =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountAccountRel.getPrimaryKey());

		Assert.assertNull(existingCommerceDiscountAccountRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceDiscountAccountRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		newCommerceDiscountAccountRel.setUuid(RandomTestUtil.randomString());

		newCommerceDiscountAccountRel.setCompanyId(RandomTestUtil.nextLong());

		newCommerceDiscountAccountRel.setUserId(RandomTestUtil.nextLong());

		newCommerceDiscountAccountRel.setUserName(
			RandomTestUtil.randomString());

		newCommerceDiscountAccountRel.setCreateDate(RandomTestUtil.nextDate());

		newCommerceDiscountAccountRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceDiscountAccountRel.setCommerceAccountId(
			RandomTestUtil.nextLong());

		newCommerceDiscountAccountRel.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		newCommerceDiscountAccountRel.setOrder(RandomTestUtil.nextInt());

		newCommerceDiscountAccountRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		newCommerceDiscountAccountRel = _persistence.update(
			newCommerceDiscountAccountRel);

		_commerceDiscountAccountRels.add(newCommerceDiscountAccountRel);

		CommerceDiscountAccountRel existingCommerceDiscountAccountRel =
			_persistence.findByPrimaryKey(
				newCommerceDiscountAccountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getMvccVersion(),
			newCommerceDiscountAccountRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getUuid(),
			newCommerceDiscountAccountRel.getUuid());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.
				getCommerceDiscountAccountRelId(),
			newCommerceDiscountAccountRel.getCommerceDiscountAccountRelId());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getCompanyId(),
			newCommerceDiscountAccountRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getUserId(),
			newCommerceDiscountAccountRel.getUserId());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getUserName(),
			newCommerceDiscountAccountRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountAccountRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceDiscountAccountRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountAccountRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceDiscountAccountRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getCommerceAccountId(),
			newCommerceDiscountAccountRel.getCommerceAccountId());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getCommerceDiscountId(),
			newCommerceDiscountAccountRel.getCommerceDiscountId());
		Assert.assertEquals(
			existingCommerceDiscountAccountRel.getOrder(),
			newCommerceDiscountAccountRel.getOrder());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountAccountRel.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommerceDiscountAccountRel.getLastPublishDate()));
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
	public void testCountByCommerceAccountId() throws Exception {
		_persistence.countByCommerceAccountId(RandomTestUtil.nextLong());

		_persistence.countByCommerceAccountId(0L);
	}

	@Test
	public void testCountByCommerceDiscountId() throws Exception {
		_persistence.countByCommerceDiscountId(RandomTestUtil.nextLong());

		_persistence.countByCommerceDiscountId(0L);
	}

	@Test
	public void testCountByCAI_CDI() throws Exception {
		_persistence.countByCAI_CDI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCAI_CDI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		CommerceDiscountAccountRel existingCommerceDiscountAccountRel =
			_persistence.findByPrimaryKey(
				newCommerceDiscountAccountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountAccountRel, newCommerceDiscountAccountRel);
	}

	@Test(expected = NoSuchDiscountAccountRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceDiscountAccountRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceDiscountAccountRel", "mvccVersion", true, "uuid", true,
			"commerceDiscountAccountRelId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"commerceAccountId", true, "commerceDiscountId", true, "order",
			true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		CommerceDiscountAccountRel existingCommerceDiscountAccountRel =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountAccountRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountAccountRel, newCommerceDiscountAccountRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountAccountRel missingCommerceDiscountAccountRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceDiscountAccountRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceDiscountAccountRel newCommerceDiscountAccountRel1 =
			addCommerceDiscountAccountRel();
		CommerceDiscountAccountRel newCommerceDiscountAccountRel2 =
			addCommerceDiscountAccountRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountAccountRel1.getPrimaryKey());
		primaryKeys.add(newCommerceDiscountAccountRel2.getPrimaryKey());

		Map<Serializable, CommerceDiscountAccountRel>
			commerceDiscountAccountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceDiscountAccountRels.size());
		Assert.assertEquals(
			newCommerceDiscountAccountRel1,
			commerceDiscountAccountRels.get(
				newCommerceDiscountAccountRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceDiscountAccountRel2,
			commerceDiscountAccountRels.get(
				newCommerceDiscountAccountRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceDiscountAccountRel>
			commerceDiscountAccountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceDiscountAccountRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountAccountRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceDiscountAccountRel>
			commerceDiscountAccountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceDiscountAccountRels.size());
		Assert.assertEquals(
			newCommerceDiscountAccountRel,
			commerceDiscountAccountRels.get(
				newCommerceDiscountAccountRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceDiscountAccountRel>
			commerceDiscountAccountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceDiscountAccountRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountAccountRel.getPrimaryKey());

		Map<Serializable, CommerceDiscountAccountRel>
			commerceDiscountAccountRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceDiscountAccountRels.size());
		Assert.assertEquals(
			newCommerceDiscountAccountRel,
			commerceDiscountAccountRels.get(
				newCommerceDiscountAccountRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceDiscountAccountRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceDiscountAccountRel>() {

				@Override
				public void performAction(
					CommerceDiscountAccountRel commerceDiscountAccountRel) {

					Assert.assertNotNull(commerceDiscountAccountRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountAccountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceDiscountAccountRelId",
				newCommerceDiscountAccountRel.
					getCommerceDiscountAccountRelId()));

		List<CommerceDiscountAccountRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceDiscountAccountRel existingCommerceDiscountAccountRel =
			result.get(0);

		Assert.assertEquals(
			existingCommerceDiscountAccountRel, newCommerceDiscountAccountRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountAccountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceDiscountAccountRelId", RandomTestUtil.nextLong()));

		List<CommerceDiscountAccountRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountAccountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceDiscountAccountRelId"));

		Object newCommerceDiscountAccountRelId =
			newCommerceDiscountAccountRel.getCommerceDiscountAccountRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceDiscountAccountRelId",
				new Object[] {newCommerceDiscountAccountRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceDiscountAccountRelId = result.get(0);

		Assert.assertEquals(
			existingCommerceDiscountAccountRelId,
			newCommerceDiscountAccountRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountAccountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceDiscountAccountRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceDiscountAccountRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceDiscountAccountRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		CommerceDiscountAccountRel newCommerceDiscountAccountRel =
			addCommerceDiscountAccountRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceDiscountAccountRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceDiscountAccountRelId",
				newCommerceDiscountAccountRel.
					getCommerceDiscountAccountRelId()));

		List<CommerceDiscountAccountRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommerceDiscountAccountRel commerceDiscountAccountRel) {

		Assert.assertEquals(
			Long.valueOf(commerceDiscountAccountRel.getCommerceAccountId()),
			ReflectionTestUtil.<Long>invoke(
				commerceDiscountAccountRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceAccountId"));
		Assert.assertEquals(
			Long.valueOf(commerceDiscountAccountRel.getCommerceDiscountId()),
			ReflectionTestUtil.<Long>invoke(
				commerceDiscountAccountRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceDiscountId"));
	}

	protected CommerceDiscountAccountRel addCommerceDiscountAccountRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceDiscountAccountRel commerceDiscountAccountRel =
			_persistence.create(pk);

		commerceDiscountAccountRel.setUuid(RandomTestUtil.randomString());

		commerceDiscountAccountRel.setCompanyId(RandomTestUtil.nextLong());

		commerceDiscountAccountRel.setUserId(RandomTestUtil.nextLong());

		commerceDiscountAccountRel.setUserName(RandomTestUtil.randomString());

		commerceDiscountAccountRel.setCreateDate(RandomTestUtil.nextDate());

		commerceDiscountAccountRel.setModifiedDate(RandomTestUtil.nextDate());

		commerceDiscountAccountRel.setCommerceAccountId(
			RandomTestUtil.nextLong());

		commerceDiscountAccountRel.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		commerceDiscountAccountRel.setOrder(RandomTestUtil.nextInt());

		commerceDiscountAccountRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commerceDiscountAccountRels.add(
			_persistence.update(commerceDiscountAccountRel));

		return commerceDiscountAccountRel;
	}

	private List<CommerceDiscountAccountRel> _commerceDiscountAccountRels =
		new ArrayList<CommerceDiscountAccountRel>();
	private CommerceDiscountAccountRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-2098303322