/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionQualifierException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOptionQualifier;
import com.liferay.commerce.shipping.engine.fixed.service.CommerceShippingFixedOptionQualifierLocalServiceUtil;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionQualifierPersistence;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionQualifierUtil;
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
public class CommerceShippingFixedOptionQualifierPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.shipping.engine.fixed.service"));

	@Before
	public void setUp() {
		_persistence =
			CommerceShippingFixedOptionQualifierUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceShippingFixedOptionQualifier> iterator =
			_commerceShippingFixedOptionQualifiers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier = _persistence.create(pk);

		Assert.assertNotNull(commerceShippingFixedOptionQualifier);

		Assert.assertEquals(
			commerceShippingFixedOptionQualifier.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		_persistence.remove(newCommerceShippingFixedOptionQualifier);

		CommerceShippingFixedOptionQualifier
			existingCommerceShippingFixedOptionQualifier =
				_persistence.fetchByPrimaryKey(
					newCommerceShippingFixedOptionQualifier.getPrimaryKey());

		Assert.assertNull(existingCommerceShippingFixedOptionQualifier);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceShippingFixedOptionQualifier();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		newCommerceShippingFixedOptionQualifier.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionQualifier.setUserId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionQualifier.setUserName(
			RandomTestUtil.randomString());

		newCommerceShippingFixedOptionQualifier.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceShippingFixedOptionQualifier.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceShippingFixedOptionQualifier.setClassNameId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionQualifier.setClassPK(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionQualifier.
			setCommerceShippingFixedOptionId(RandomTestUtil.nextLong());

		newCommerceShippingFixedOptionQualifier = _persistence.update(
			newCommerceShippingFixedOptionQualifier);

		_commerceShippingFixedOptionQualifiers.add(
			newCommerceShippingFixedOptionQualifier);

		CommerceShippingFixedOptionQualifier
			existingCommerceShippingFixedOptionQualifier =
				_persistence.findByPrimaryKey(
					newCommerceShippingFixedOptionQualifier.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.getMvccVersion(),
			newCommerceShippingFixedOptionQualifier.getMvccVersion());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionQualifierId(),
			newCommerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionQualifierId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.getCompanyId(),
			newCommerceShippingFixedOptionQualifier.getCompanyId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.getUserId(),
			newCommerceShippingFixedOptionQualifier.getUserId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.getUserName(),
			newCommerceShippingFixedOptionQualifier.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingFixedOptionQualifier.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceShippingFixedOptionQualifier.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingFixedOptionQualifier.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceShippingFixedOptionQualifier.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.getClassNameId(),
			newCommerceShippingFixedOptionQualifier.getClassNameId());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.getClassPK(),
			newCommerceShippingFixedOptionQualifier.getClassPK());
		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionId(),
			newCommerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionId());
	}

	@Test
	public void testCountByCommerceShippingFixedOptionId() throws Exception {
		_persistence.countByCommerceShippingFixedOptionId(
			RandomTestUtil.nextLong());

		_persistence.countByCommerceShippingFixedOptionId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		CommerceShippingFixedOptionQualifier
			existingCommerceShippingFixedOptionQualifier =
				_persistence.findByPrimaryKey(
					newCommerceShippingFixedOptionQualifier.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier,
			newCommerceShippingFixedOptionQualifier);
	}

	@Test(expected = NoSuchShippingFixedOptionQualifierException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceShippingFixedOptionQualifier>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CSFixedOptionQualifier", "mvccVersion", true,
			"commerceShippingFixedOptionQualifierId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"commerceShippingFixedOptionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		CommerceShippingFixedOptionQualifier
			existingCommerceShippingFixedOptionQualifier =
				_persistence.fetchByPrimaryKey(
					newCommerceShippingFixedOptionQualifier.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier,
			newCommerceShippingFixedOptionQualifier);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionQualifier
			missingCommerceShippingFixedOptionQualifier =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceShippingFixedOptionQualifier);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier1 =
				addCommerceShippingFixedOptionQualifier();
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier2 =
				addCommerceShippingFixedOptionQualifier();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommerceShippingFixedOptionQualifier1.getPrimaryKey());
		primaryKeys.add(
			newCommerceShippingFixedOptionQualifier2.getPrimaryKey());

		Map<Serializable, CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceShippingFixedOptionQualifiers.size());
		Assert.assertEquals(
			newCommerceShippingFixedOptionQualifier1,
			commerceShippingFixedOptionQualifiers.get(
				newCommerceShippingFixedOptionQualifier1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceShippingFixedOptionQualifier2,
			commerceShippingFixedOptionQualifiers.get(
				newCommerceShippingFixedOptionQualifier2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShippingFixedOptionQualifiers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommerceShippingFixedOptionQualifier.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShippingFixedOptionQualifiers.size());
		Assert.assertEquals(
			newCommerceShippingFixedOptionQualifier,
			commerceShippingFixedOptionQualifiers.get(
				newCommerceShippingFixedOptionQualifier.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShippingFixedOptionQualifiers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommerceShippingFixedOptionQualifier.getPrimaryKey());

		Map<Serializable, CommerceShippingFixedOptionQualifier>
			commerceShippingFixedOptionQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShippingFixedOptionQualifiers.size());
		Assert.assertEquals(
			newCommerceShippingFixedOptionQualifier,
			commerceShippingFixedOptionQualifiers.get(
				newCommerceShippingFixedOptionQualifier.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceShippingFixedOptionQualifierLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceShippingFixedOptionQualifier>() {

				@Override
				public void performAction(
					CommerceShippingFixedOptionQualifier
						commerceShippingFixedOptionQualifier) {

					Assert.assertNotNull(commerceShippingFixedOptionQualifier);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingFixedOptionQualifier.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceShippingFixedOptionQualifierId",
				newCommerceShippingFixedOptionQualifier.
					getCommerceShippingFixedOptionQualifierId()));

		List<CommerceShippingFixedOptionQualifier> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceShippingFixedOptionQualifier
			existingCommerceShippingFixedOptionQualifier = result.get(0);

		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifier,
			newCommerceShippingFixedOptionQualifier);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingFixedOptionQualifier.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceShippingFixedOptionQualifierId",
				RandomTestUtil.nextLong()));

		List<CommerceShippingFixedOptionQualifier> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingFixedOptionQualifier.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"commerceShippingFixedOptionQualifierId"));

		Object newCommerceShippingFixedOptionQualifierId =
			newCommerceShippingFixedOptionQualifier.
				getCommerceShippingFixedOptionQualifierId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceShippingFixedOptionQualifierId",
				new Object[] {newCommerceShippingFixedOptionQualifierId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceShippingFixedOptionQualifierId = result.get(0);

		Assert.assertEquals(
			existingCommerceShippingFixedOptionQualifierId,
			newCommerceShippingFixedOptionQualifierId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingFixedOptionQualifier.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"commerceShippingFixedOptionQualifierId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceShippingFixedOptionQualifierId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceShippingFixedOptionQualifier.getPrimaryKey()));
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

		CommerceShippingFixedOptionQualifier
			newCommerceShippingFixedOptionQualifier =
				addCommerceShippingFixedOptionQualifier();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingFixedOptionQualifier.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceShippingFixedOptionQualifierId",
				newCommerceShippingFixedOptionQualifier.
					getCommerceShippingFixedOptionQualifierId()));

		List<CommerceShippingFixedOptionQualifier> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier) {

		Assert.assertEquals(
			Long.valueOf(commerceShippingFixedOptionQualifier.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingFixedOptionQualifier, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceShippingFixedOptionQualifier.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingFixedOptionQualifier, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(
				commerceShippingFixedOptionQualifier.
					getCommerceShippingFixedOptionId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingFixedOptionQualifier, "getColumnOriginalValue",
				new Class<?>[] {String.class},
				"commerceShippingFixedOptionId"));
	}

	protected CommerceShippingFixedOptionQualifier
			addCommerceShippingFixedOptionQualifier()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOptionQualifier
			commerceShippingFixedOptionQualifier = _persistence.create(pk);

		commerceShippingFixedOptionQualifier.setCompanyId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionQualifier.setUserId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionQualifier.setUserName(
			RandomTestUtil.randomString());

		commerceShippingFixedOptionQualifier.setCreateDate(
			RandomTestUtil.nextDate());

		commerceShippingFixedOptionQualifier.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceShippingFixedOptionQualifier.setClassNameId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionQualifier.setClassPK(
			RandomTestUtil.nextLong());

		commerceShippingFixedOptionQualifier.setCommerceShippingFixedOptionId(
			RandomTestUtil.nextLong());

		_commerceShippingFixedOptionQualifiers.add(
			_persistence.update(commerceShippingFixedOptionQualifier));

		return commerceShippingFixedOptionQualifier;
	}

	private List<CommerceShippingFixedOptionQualifier>
		_commerceShippingFixedOptionQualifiers =
			new ArrayList<CommerceShippingFixedOptionQualifier>();
	private CommerceShippingFixedOptionQualifierPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1242503575