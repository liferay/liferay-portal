/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchShippingOptionAccountEntryRelException;
import com.liferay.commerce.model.CommerceShippingOptionAccountEntryRel;
import com.liferay.commerce.service.CommerceShippingOptionAccountEntryRelLocalServiceUtil;
import com.liferay.commerce.service.persistence.CommerceShippingOptionAccountEntryRelPersistence;
import com.liferay.commerce.service.persistence.CommerceShippingOptionAccountEntryRelUtil;
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
public class CommerceShippingOptionAccountEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence =
			CommerceShippingOptionAccountEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceShippingOptionAccountEntryRel> iterator =
			_commerceShippingOptionAccountEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel = _persistence.create(pk);

		Assert.assertNotNull(commerceShippingOptionAccountEntryRel);

		Assert.assertEquals(
			commerceShippingOptionAccountEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		_persistence.remove(newCommerceShippingOptionAccountEntryRel);

		CommerceShippingOptionAccountEntryRel
			existingCommerceShippingOptionAccountEntryRel =
				_persistence.fetchByPrimaryKey(
					newCommerceShippingOptionAccountEntryRel.getPrimaryKey());

		Assert.assertNull(existingCommerceShippingOptionAccountEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceShippingOptionAccountEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		newCommerceShippingOptionAccountEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceShippingOptionAccountEntryRel.setUserId(
			RandomTestUtil.nextLong());

		newCommerceShippingOptionAccountEntryRel.setUserName(
			RandomTestUtil.randomString());

		newCommerceShippingOptionAccountEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceShippingOptionAccountEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceShippingOptionAccountEntryRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		newCommerceShippingOptionAccountEntryRel.setCommerceChannelId(
			RandomTestUtil.nextLong());

		newCommerceShippingOptionAccountEntryRel.setCommerceShippingMethodKey(
			RandomTestUtil.randomString());

		newCommerceShippingOptionAccountEntryRel.setCommerceShippingOptionKey(
			RandomTestUtil.randomString());

		newCommerceShippingOptionAccountEntryRel = _persistence.update(
			newCommerceShippingOptionAccountEntryRel);

		_commerceShippingOptionAccountEntryRels.add(
			newCommerceShippingOptionAccountEntryRel);

		CommerceShippingOptionAccountEntryRel
			existingCommerceShippingOptionAccountEntryRel =
				_persistence.findByPrimaryKey(
					newCommerceShippingOptionAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.getMvccVersion(),
			newCommerceShippingOptionAccountEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.
				getCommerceShippingOptionAccountEntryRelId(),
			newCommerceShippingOptionAccountEntryRel.
				getCommerceShippingOptionAccountEntryRelId());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.getCompanyId(),
			newCommerceShippingOptionAccountEntryRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.getUserId(),
			newCommerceShippingOptionAccountEntryRel.getUserId());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.getUserName(),
			newCommerceShippingOptionAccountEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingOptionAccountEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceShippingOptionAccountEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingOptionAccountEntryRel.
					getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceShippingOptionAccountEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.getAccountEntryId(),
			newCommerceShippingOptionAccountEntryRel.getAccountEntryId());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.
				getCommerceChannelId(),
			newCommerceShippingOptionAccountEntryRel.getCommerceChannelId());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.
				getCommerceShippingMethodKey(),
			newCommerceShippingOptionAccountEntryRel.
				getCommerceShippingMethodKey());
		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel.
				getCommerceShippingOptionKey(),
			newCommerceShippingOptionAccountEntryRel.
				getCommerceShippingOptionKey());
	}

	@Test
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountByCommerceChannelId() throws Exception {
		_persistence.countByCommerceChannelId(RandomTestUtil.nextLong());

		_persistence.countByCommerceChannelId(0L);
	}

	@Test
	public void testCountByCommerceShippingOptionKey() throws Exception {
		_persistence.countByCommerceShippingOptionKey("");

		_persistence.countByCommerceShippingOptionKey("null");

		_persistence.countByCommerceShippingOptionKey((String)null);
	}

	@Test
	public void testCountByA_C() throws Exception {
		_persistence.countByA_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByA_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		CommerceShippingOptionAccountEntryRel
			existingCommerceShippingOptionAccountEntryRel =
				_persistence.findByPrimaryKey(
					newCommerceShippingOptionAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel,
			newCommerceShippingOptionAccountEntryRel);
	}

	@Test(expected = NoSuchShippingOptionAccountEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceShippingOptionAccountEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CSOptionAccountEntryRel", "mvccVersion", true,
			"CommerceShippingOptionAccountEntryRelId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "accountEntryId", true, "commerceChannelId",
			true, "commerceShippingMethodKey", true,
			"commerceShippingOptionKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		CommerceShippingOptionAccountEntryRel
			existingCommerceShippingOptionAccountEntryRel =
				_persistence.fetchByPrimaryKey(
					newCommerceShippingOptionAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel,
			newCommerceShippingOptionAccountEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingOptionAccountEntryRel
			missingCommerceShippingOptionAccountEntryRel =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceShippingOptionAccountEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel1 =
				addCommerceShippingOptionAccountEntryRel();
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel2 =
				addCommerceShippingOptionAccountEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommerceShippingOptionAccountEntryRel1.getPrimaryKey());
		primaryKeys.add(
			newCommerceShippingOptionAccountEntryRel2.getPrimaryKey());

		Map<Serializable, CommerceShippingOptionAccountEntryRel>
			commerceShippingOptionAccountEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceShippingOptionAccountEntryRels.size());
		Assert.assertEquals(
			newCommerceShippingOptionAccountEntryRel1,
			commerceShippingOptionAccountEntryRels.get(
				newCommerceShippingOptionAccountEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceShippingOptionAccountEntryRel2,
			commerceShippingOptionAccountEntryRels.get(
				newCommerceShippingOptionAccountEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceShippingOptionAccountEntryRel>
			commerceShippingOptionAccountEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShippingOptionAccountEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommerceShippingOptionAccountEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceShippingOptionAccountEntryRel>
			commerceShippingOptionAccountEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShippingOptionAccountEntryRels.size());
		Assert.assertEquals(
			newCommerceShippingOptionAccountEntryRel,
			commerceShippingOptionAccountEntryRels.get(
				newCommerceShippingOptionAccountEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceShippingOptionAccountEntryRel>
			commerceShippingOptionAccountEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceShippingOptionAccountEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommerceShippingOptionAccountEntryRel.getPrimaryKey());

		Map<Serializable, CommerceShippingOptionAccountEntryRel>
			commerceShippingOptionAccountEntryRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceShippingOptionAccountEntryRels.size());
		Assert.assertEquals(
			newCommerceShippingOptionAccountEntryRel,
			commerceShippingOptionAccountEntryRels.get(
				newCommerceShippingOptionAccountEntryRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceShippingOptionAccountEntryRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceShippingOptionAccountEntryRel>() {

				@Override
				public void performAction(
					CommerceShippingOptionAccountEntryRel
						commerceShippingOptionAccountEntryRel) {

					Assert.assertNotNull(commerceShippingOptionAccountEntryRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingOptionAccountEntryRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CommerceShippingOptionAccountEntryRelId",
				newCommerceShippingOptionAccountEntryRel.
					getCommerceShippingOptionAccountEntryRelId()));

		List<CommerceShippingOptionAccountEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceShippingOptionAccountEntryRel
			existingCommerceShippingOptionAccountEntryRel = result.get(0);

		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRel,
			newCommerceShippingOptionAccountEntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingOptionAccountEntryRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CommerceShippingOptionAccountEntryRelId",
				RandomTestUtil.nextLong()));

		List<CommerceShippingOptionAccountEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingOptionAccountEntryRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"CommerceShippingOptionAccountEntryRelId"));

		Object newCommerceShippingOptionAccountEntryRelId =
			newCommerceShippingOptionAccountEntryRel.
				getCommerceShippingOptionAccountEntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CommerceShippingOptionAccountEntryRelId",
				new Object[] {newCommerceShippingOptionAccountEntryRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceShippingOptionAccountEntryRelId = result.get(0);

		Assert.assertEquals(
			existingCommerceShippingOptionAccountEntryRelId,
			newCommerceShippingOptionAccountEntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingOptionAccountEntryRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"CommerceShippingOptionAccountEntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CommerceShippingOptionAccountEntryRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceShippingOptionAccountEntryRel.getPrimaryKey()));
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

		CommerceShippingOptionAccountEntryRel
			newCommerceShippingOptionAccountEntryRel =
				addCommerceShippingOptionAccountEntryRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceShippingOptionAccountEntryRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CommerceShippingOptionAccountEntryRelId",
				newCommerceShippingOptionAccountEntryRel.
					getCommerceShippingOptionAccountEntryRelId()));

		List<CommerceShippingOptionAccountEntryRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel) {

		Assert.assertEquals(
			Long.valueOf(
				commerceShippingOptionAccountEntryRel.getAccountEntryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingOptionAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "accountEntryId"));
		Assert.assertEquals(
			Long.valueOf(
				commerceShippingOptionAccountEntryRel.getCommerceChannelId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingOptionAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceChannelId"));
	}

	protected CommerceShippingOptionAccountEntryRel
			addCommerceShippingOptionAccountEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceShippingOptionAccountEntryRel
			commerceShippingOptionAccountEntryRel = _persistence.create(pk);

		commerceShippingOptionAccountEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		commerceShippingOptionAccountEntryRel.setUserId(
			RandomTestUtil.nextLong());

		commerceShippingOptionAccountEntryRel.setUserName(
			RandomTestUtil.randomString());

		commerceShippingOptionAccountEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		commerceShippingOptionAccountEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceShippingOptionAccountEntryRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		commerceShippingOptionAccountEntryRel.setCommerceChannelId(
			RandomTestUtil.nextLong());

		commerceShippingOptionAccountEntryRel.setCommerceShippingMethodKey(
			RandomTestUtil.randomString());

		commerceShippingOptionAccountEntryRel.setCommerceShippingOptionKey(
			RandomTestUtil.randomString());

		_commerceShippingOptionAccountEntryRels.add(
			_persistence.update(commerceShippingOptionAccountEntryRel));

		return commerceShippingOptionAccountEntryRel;
	}

	private List<CommerceShippingOptionAccountEntryRel>
		_commerceShippingOptionAccountEntryRels =
			new ArrayList<CommerceShippingOptionAccountEntryRel>();
	private CommerceShippingOptionAccountEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-495047755