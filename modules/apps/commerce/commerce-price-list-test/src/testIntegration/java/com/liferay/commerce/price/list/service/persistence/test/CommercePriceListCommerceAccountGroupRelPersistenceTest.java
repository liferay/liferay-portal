/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.NoSuchPriceListCommerceAccountGroupRelException;
import com.liferay.commerce.price.list.model.CommercePriceListCommerceAccountGroupRel;
import com.liferay.commerce.price.list.service.CommercePriceListCommerceAccountGroupRelLocalServiceUtil;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListCommerceAccountGroupRelPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListCommerceAccountGroupRelUtil;
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
public class CommercePriceListCommerceAccountGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.price.list.service"));

	@Before
	public void setUp() {
		_persistence =
			CommercePriceListCommerceAccountGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePriceListCommerceAccountGroupRel> iterator =
			_commercePriceListCommerceAccountGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel = _persistence.create(pk);

		Assert.assertNotNull(commercePriceListCommerceAccountGroupRel);

		Assert.assertEquals(
			commercePriceListCommerceAccountGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		_persistence.remove(newCommercePriceListCommerceAccountGroupRel);

		CommercePriceListCommerceAccountGroupRel
			existingCommercePriceListCommerceAccountGroupRel =
				_persistence.fetchByPrimaryKey(
					newCommercePriceListCommerceAccountGroupRel.
						getPrimaryKey());

		Assert.assertNull(existingCommercePriceListCommerceAccountGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePriceListCommerceAccountGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		newCommercePriceListCommerceAccountGroupRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommercePriceListCommerceAccountGroupRel.setUuid(
			RandomTestUtil.randomString());

		newCommercePriceListCommerceAccountGroupRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommercePriceListCommerceAccountGroupRel.setUserId(
			RandomTestUtil.nextLong());

		newCommercePriceListCommerceAccountGroupRel.setUserName(
			RandomTestUtil.randomString());

		newCommercePriceListCommerceAccountGroupRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommercePriceListCommerceAccountGroupRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePriceListCommerceAccountGroupRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		newCommercePriceListCommerceAccountGroupRel.setCommerceAccountGroupId(
			RandomTestUtil.nextLong());

		newCommercePriceListCommerceAccountGroupRel.setOrder(
			RandomTestUtil.nextInt());

		newCommercePriceListCommerceAccountGroupRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		newCommercePriceListCommerceAccountGroupRel = _persistence.update(
			newCommercePriceListCommerceAccountGroupRel);

		_commercePriceListCommerceAccountGroupRels.add(
			newCommercePriceListCommerceAccountGroupRel);

		CommercePriceListCommerceAccountGroupRel
			existingCommercePriceListCommerceAccountGroupRel =
				_persistence.findByPrimaryKey(
					newCommercePriceListCommerceAccountGroupRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.getMvccVersion(),
			newCommercePriceListCommerceAccountGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.
				getCtCollectionId(),
			newCommercePriceListCommerceAccountGroupRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.getUuid(),
			newCommercePriceListCommerceAccountGroupRel.getUuid());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.
				getCommercePriceListCommerceAccountGroupRelId(),
			newCommercePriceListCommerceAccountGroupRel.
				getCommercePriceListCommerceAccountGroupRelId());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.getCompanyId(),
			newCommercePriceListCommerceAccountGroupRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.getUserId(),
			newCommercePriceListCommerceAccountGroupRel.getUserId());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.getUserName(),
			newCommercePriceListCommerceAccountGroupRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListCommerceAccountGroupRel.
					getCreateDate()),
			Time.getShortTimestamp(
				newCommercePriceListCommerceAccountGroupRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListCommerceAccountGroupRel.
					getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePriceListCommerceAccountGroupRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.
				getCommercePriceListId(),
			newCommercePriceListCommerceAccountGroupRel.
				getCommercePriceListId());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.
				getCommerceAccountGroupId(),
			newCommercePriceListCommerceAccountGroupRel.
				getCommerceAccountGroupId());
		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel.getOrder(),
			newCommercePriceListCommerceAccountGroupRel.getOrder());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceListCommerceAccountGroupRel.
					getLastPublishDate()),
			Time.getShortTimestamp(
				newCommercePriceListCommerceAccountGroupRel.
					getLastPublishDate()));
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
	public void testCountByCommercePriceListId() throws Exception {
		_persistence.countByCommercePriceListId(RandomTestUtil.nextLong());

		_persistence.countByCommercePriceListId(0L);
	}

	@Test
	public void testCountByCAGI_CPI() throws Exception {
		_persistence.countByCAGI_CPI(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCAGI_CPI(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		CommercePriceListCommerceAccountGroupRel
			existingCommercePriceListCommerceAccountGroupRel =
				_persistence.findByPrimaryKey(
					newCommercePriceListCommerceAccountGroupRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel,
			newCommercePriceListCommerceAccountGroupRel);
	}

	@Test(expected = NoSuchPriceListCommerceAccountGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePriceListCommerceAccountGroupRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPLCommerceGroupAccountRel", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "commercePriceListCommerceAccountGroupRelId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "commercePriceListId",
			true, "commerceAccountGroupId", true, "order", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		CommercePriceListCommerceAccountGroupRel
			existingCommercePriceListCommerceAccountGroupRel =
				_persistence.fetchByPrimaryKey(
					newCommercePriceListCommerceAccountGroupRel.
						getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel,
			newCommercePriceListCommerceAccountGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceListCommerceAccountGroupRel
			missingCommercePriceListCommerceAccountGroupRel =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePriceListCommerceAccountGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel1 =
				addCommercePriceListCommerceAccountGroupRel();
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel2 =
				addCommercePriceListCommerceAccountGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePriceListCommerceAccountGroupRel1.getPrimaryKey());
		primaryKeys.add(
			newCommercePriceListCommerceAccountGroupRel2.getPrimaryKey());

		Map<Serializable, CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			2, commercePriceListCommerceAccountGroupRels.size());
		Assert.assertEquals(
			newCommercePriceListCommerceAccountGroupRel1,
			commercePriceListCommerceAccountGroupRels.get(
				newCommercePriceListCommerceAccountGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePriceListCommerceAccountGroupRel2,
			commercePriceListCommerceAccountGroupRels.get(
				newCommercePriceListCommerceAccountGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePriceListCommerceAccountGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePriceListCommerceAccountGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1, commercePriceListCommerceAccountGroupRels.size());
		Assert.assertEquals(
			newCommercePriceListCommerceAccountGroupRel,
			commercePriceListCommerceAccountGroupRels.get(
				newCommercePriceListCommerceAccountGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePriceListCommerceAccountGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePriceListCommerceAccountGroupRel.getPrimaryKey());

		Map<Serializable, CommercePriceListCommerceAccountGroupRel>
			commercePriceListCommerceAccountGroupRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(
			1, commercePriceListCommerceAccountGroupRels.size());
		Assert.assertEquals(
			newCommercePriceListCommerceAccountGroupRel,
			commercePriceListCommerceAccountGroupRels.get(
				newCommercePriceListCommerceAccountGroupRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommercePriceListCommerceAccountGroupRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommercePriceListCommerceAccountGroupRel>() {

				@Override
				public void performAction(
					CommercePriceListCommerceAccountGroupRel
						commercePriceListCommerceAccountGroupRel) {

					Assert.assertNotNull(
						commercePriceListCommerceAccountGroupRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceListCommerceAccountGroupRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceListCommerceAccountGroupRelId",
				newCommercePriceListCommerceAccountGroupRel.
					getCommercePriceListCommerceAccountGroupRelId()));

		List<CommercePriceListCommerceAccountGroupRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommercePriceListCommerceAccountGroupRel
			existingCommercePriceListCommerceAccountGroupRel = result.get(0);

		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRel,
			newCommercePriceListCommerceAccountGroupRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceListCommerceAccountGroupRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceListCommerceAccountGroupRelId",
				RandomTestUtil.nextLong()));

		List<CommercePriceListCommerceAccountGroupRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceListCommerceAccountGroupRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"commercePriceListCommerceAccountGroupRelId"));

		Object newCommercePriceListCommerceAccountGroupRelId =
			newCommercePriceListCommerceAccountGroupRel.
				getCommercePriceListCommerceAccountGroupRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commercePriceListCommerceAccountGroupRelId",
				new Object[] {newCommercePriceListCommerceAccountGroupRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommercePriceListCommerceAccountGroupRelId = result.get(
			0);

		Assert.assertEquals(
			existingCommercePriceListCommerceAccountGroupRelId,
			newCommercePriceListCommerceAccountGroupRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceListCommerceAccountGroupRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"commercePriceListCommerceAccountGroupRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commercePriceListCommerceAccountGroupRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePriceListCommerceAccountGroupRel.getPrimaryKey()));
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

		CommercePriceListCommerceAccountGroupRel
			newCommercePriceListCommerceAccountGroupRel =
				addCommercePriceListCommerceAccountGroupRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceListCommerceAccountGroupRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceListCommerceAccountGroupRelId",
				newCommercePriceListCommerceAccountGroupRel.
					getCommercePriceListCommerceAccountGroupRelId()));

		List<CommercePriceListCommerceAccountGroupRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel) {

		Assert.assertEquals(
			Long.valueOf(
				commercePriceListCommerceAccountGroupRel.
					getCommercePriceListId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListCommerceAccountGroupRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"commercePriceListId"));
		Assert.assertEquals(
			Long.valueOf(
				commercePriceListCommerceAccountGroupRel.
					getCommerceAccountGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceListCommerceAccountGroupRel,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"commerceAccountGroupId"));
	}

	protected CommercePriceListCommerceAccountGroupRel
			addCommercePriceListCommerceAccountGroupRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePriceListCommerceAccountGroupRel
			commercePriceListCommerceAccountGroupRel = _persistence.create(pk);

		commercePriceListCommerceAccountGroupRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		commercePriceListCommerceAccountGroupRel.setUuid(
			RandomTestUtil.randomString());

		commercePriceListCommerceAccountGroupRel.setCompanyId(
			RandomTestUtil.nextLong());

		commercePriceListCommerceAccountGroupRel.setUserId(
			RandomTestUtil.nextLong());

		commercePriceListCommerceAccountGroupRel.setUserName(
			RandomTestUtil.randomString());

		commercePriceListCommerceAccountGroupRel.setCreateDate(
			RandomTestUtil.nextDate());

		commercePriceListCommerceAccountGroupRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commercePriceListCommerceAccountGroupRel.setCommercePriceListId(
			RandomTestUtil.nextLong());

		commercePriceListCommerceAccountGroupRel.setCommerceAccountGroupId(
			RandomTestUtil.nextLong());

		commercePriceListCommerceAccountGroupRel.setOrder(
			RandomTestUtil.nextInt());

		commercePriceListCommerceAccountGroupRel.setLastPublishDate(
			RandomTestUtil.nextDate());

		_commercePriceListCommerceAccountGroupRels.add(
			_persistence.update(commercePriceListCommerceAccountGroupRel));

		return commercePriceListCommerceAccountGroupRel;
	}

	private List<CommercePriceListCommerceAccountGroupRel>
		_commercePriceListCommerceAccountGroupRels =
			new ArrayList<CommercePriceListCommerceAccountGroupRel>();
	private CommercePriceListCommerceAccountGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1746219097