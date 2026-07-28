/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.exception.NoSuchPricingClassCPDefinitionRelException;
import com.liferay.commerce.pricing.model.CommercePricingClassCPDefinitionRel;
import com.liferay.commerce.pricing.service.CommercePricingClassCPDefinitionRelLocalServiceUtil;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelPersistence;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassCPDefinitionRelUtil;
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
public class CommercePricingClassCPDefinitionRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.pricing.service"));

	@Before
	public void setUp() {
		_persistence = CommercePricingClassCPDefinitionRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePricingClassCPDefinitionRel> iterator =
			_commercePricingClassCPDefinitionRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel = _persistence.create(pk);

		Assert.assertNotNull(commercePricingClassCPDefinitionRel);

		Assert.assertEquals(
			commercePricingClassCPDefinitionRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		_persistence.remove(newCommercePricingClassCPDefinitionRel);

		CommercePricingClassCPDefinitionRel
			existingCommercePricingClassCPDefinitionRel =
				_persistence.fetchByPrimaryKey(
					newCommercePricingClassCPDefinitionRel.getPrimaryKey());

		Assert.assertNull(existingCommercePricingClassCPDefinitionRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePricingClassCPDefinitionRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		newCommercePricingClassCPDefinitionRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommercePricingClassCPDefinitionRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommercePricingClassCPDefinitionRel.setUserId(
			RandomTestUtil.nextLong());

		newCommercePricingClassCPDefinitionRel.setUserName(
			RandomTestUtil.randomString());

		newCommercePricingClassCPDefinitionRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommercePricingClassCPDefinitionRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePricingClassCPDefinitionRel.setCommercePricingClassId(
			RandomTestUtil.nextLong());

		newCommercePricingClassCPDefinitionRel.setCPDefinitionId(
			RandomTestUtil.nextLong());

		newCommercePricingClassCPDefinitionRel = _persistence.update(
			newCommercePricingClassCPDefinitionRel);

		_commercePricingClassCPDefinitionRels.add(
			newCommercePricingClassCPDefinitionRel);

		CommercePricingClassCPDefinitionRel
			existingCommercePricingClassCPDefinitionRel =
				_persistence.findByPrimaryKey(
					newCommercePricingClassCPDefinitionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.getMvccVersion(),
			newCommercePricingClassCPDefinitionRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.getCtCollectionId(),
			newCommercePricingClassCPDefinitionRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.
				getCommercePricingClassCPDefinitionRelId(),
			newCommercePricingClassCPDefinitionRel.
				getCommercePricingClassCPDefinitionRelId());
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.getCompanyId(),
			newCommercePricingClassCPDefinitionRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.getUserId(),
			newCommercePricingClassCPDefinitionRel.getUserId());
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.getUserName(),
			newCommercePricingClassCPDefinitionRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePricingClassCPDefinitionRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePricingClassCPDefinitionRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePricingClassCPDefinitionRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePricingClassCPDefinitionRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.
				getCommercePricingClassId(),
			newCommercePricingClassCPDefinitionRel.getCommercePricingClassId());
		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel.getCPDefinitionId(),
			newCommercePricingClassCPDefinitionRel.getCPDefinitionId());
	}

	@Test
	public void testCountByCommercePricingClassId() throws Exception {
		_persistence.countByCommercePricingClassId(RandomTestUtil.nextLong());

		_persistence.countByCommercePricingClassId(0L);
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		CommercePricingClassCPDefinitionRel
			existingCommercePricingClassCPDefinitionRel =
				_persistence.findByPrimaryKey(
					newCommercePricingClassCPDefinitionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel,
			newCommercePricingClassCPDefinitionRel);
	}

	@Test(expected = NoSuchPricingClassCPDefinitionRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePricingClassCPDefinitionRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPricingClassCPDefinitionRel", "mvccVersion", true,
			"ctCollectionId", true, "CommercePricingClassCPDefinitionRelId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "commercePricingClassId",
			true, "CPDefinitionId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		CommercePricingClassCPDefinitionRel
			existingCommercePricingClassCPDefinitionRel =
				_persistence.fetchByPrimaryKey(
					newCommercePricingClassCPDefinitionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel,
			newCommercePricingClassCPDefinitionRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePricingClassCPDefinitionRel
			missingCommercePricingClassCPDefinitionRel =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePricingClassCPDefinitionRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel1 =
				addCommercePricingClassCPDefinitionRel();
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel2 =
				addCommercePricingClassCPDefinitionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePricingClassCPDefinitionRel1.getPrimaryKey());
		primaryKeys.add(
			newCommercePricingClassCPDefinitionRel2.getPrimaryKey());

		Map<Serializable, CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commercePricingClassCPDefinitionRels.size());
		Assert.assertEquals(
			newCommercePricingClassCPDefinitionRel1,
			commercePricingClassCPDefinitionRels.get(
				newCommercePricingClassCPDefinitionRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePricingClassCPDefinitionRel2,
			commercePricingClassCPDefinitionRels.get(
				newCommercePricingClassCPDefinitionRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePricingClassCPDefinitionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePricingClassCPDefinitionRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePricingClassCPDefinitionRels.size());
		Assert.assertEquals(
			newCommercePricingClassCPDefinitionRel,
			commercePricingClassCPDefinitionRels.get(
				newCommercePricingClassCPDefinitionRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePricingClassCPDefinitionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePricingClassCPDefinitionRel.getPrimaryKey());

		Map<Serializable, CommercePricingClassCPDefinitionRel>
			commercePricingClassCPDefinitionRels =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePricingClassCPDefinitionRels.size());
		Assert.assertEquals(
			newCommercePricingClassCPDefinitionRel,
			commercePricingClassCPDefinitionRels.get(
				newCommercePricingClassCPDefinitionRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommercePricingClassCPDefinitionRelLocalServiceUtil.
				getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommercePricingClassCPDefinitionRel>() {

				@Override
				public void performAction(
					CommercePricingClassCPDefinitionRel
						commercePricingClassCPDefinitionRel) {

					Assert.assertNotNull(commercePricingClassCPDefinitionRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePricingClassCPDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CommercePricingClassCPDefinitionRelId",
				newCommercePricingClassCPDefinitionRel.
					getCommercePricingClassCPDefinitionRelId()));

		List<CommercePricingClassCPDefinitionRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommercePricingClassCPDefinitionRel
			existingCommercePricingClassCPDefinitionRel = result.get(0);

		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRel,
			newCommercePricingClassCPDefinitionRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePricingClassCPDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CommercePricingClassCPDefinitionRelId",
				RandomTestUtil.nextLong()));

		List<CommercePricingClassCPDefinitionRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePricingClassCPDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"CommercePricingClassCPDefinitionRelId"));

		Object newCommercePricingClassCPDefinitionRelId =
			newCommercePricingClassCPDefinitionRel.
				getCommercePricingClassCPDefinitionRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CommercePricingClassCPDefinitionRelId",
				new Object[] {newCommercePricingClassCPDefinitionRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommercePricingClassCPDefinitionRelId = result.get(0);

		Assert.assertEquals(
			existingCommercePricingClassCPDefinitionRelId,
			newCommercePricingClassCPDefinitionRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePricingClassCPDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property(
				"CommercePricingClassCPDefinitionRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CommercePricingClassCPDefinitionRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePricingClassCPDefinitionRel.getPrimaryKey()));
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

		CommercePricingClassCPDefinitionRel
			newCommercePricingClassCPDefinitionRel =
				addCommercePricingClassCPDefinitionRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePricingClassCPDefinitionRel.class,
			_dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CommercePricingClassCPDefinitionRelId",
				newCommercePricingClassCPDefinitionRel.
					getCommercePricingClassCPDefinitionRelId()));

		List<CommercePricingClassCPDefinitionRel> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel) {

		Assert.assertEquals(
			Long.valueOf(
				commercePricingClassCPDefinitionRel.
					getCommercePricingClassId()),
			ReflectionTestUtil.<Long>invoke(
				commercePricingClassCPDefinitionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commercePricingClassId"));
		Assert.assertEquals(
			Long.valueOf(
				commercePricingClassCPDefinitionRel.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				commercePricingClassCPDefinitionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
	}

	protected CommercePricingClassCPDefinitionRel
			addCommercePricingClassCPDefinitionRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePricingClassCPDefinitionRel
			commercePricingClassCPDefinitionRel = _persistence.create(pk);

		commercePricingClassCPDefinitionRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		commercePricingClassCPDefinitionRel.setCompanyId(
			RandomTestUtil.nextLong());

		commercePricingClassCPDefinitionRel.setUserId(
			RandomTestUtil.nextLong());

		commercePricingClassCPDefinitionRel.setUserName(
			RandomTestUtil.randomString());

		commercePricingClassCPDefinitionRel.setCreateDate(
			RandomTestUtil.nextDate());

		commercePricingClassCPDefinitionRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commercePricingClassCPDefinitionRel.setCommercePricingClassId(
			RandomTestUtil.nextLong());

		commercePricingClassCPDefinitionRel.setCPDefinitionId(
			RandomTestUtil.nextLong());

		_commercePricingClassCPDefinitionRels.add(
			_persistence.update(commercePricingClassCPDefinitionRel));

		return commercePricingClassCPDefinitionRel;
	}

	private List<CommercePricingClassCPDefinitionRel>
		_commercePricingClassCPDefinitionRels =
			new ArrayList<CommercePricingClassCPDefinitionRel>();
	private CommercePricingClassCPDefinitionRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:1356820981