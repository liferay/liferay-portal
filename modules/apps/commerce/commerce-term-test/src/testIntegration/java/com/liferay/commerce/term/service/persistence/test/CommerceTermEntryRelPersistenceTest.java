/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.term.exception.NoSuchTermEntryRelException;
import com.liferay.commerce.term.model.CommerceTermEntryRel;
import com.liferay.commerce.term.service.CommerceTermEntryRelLocalServiceUtil;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryRelPersistence;
import com.liferay.commerce.term.service.persistence.CommerceTermEntryRelUtil;
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
public class CommerceTermEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.term.service"));

	@Before
	public void setUp() {
		_persistence = CommerceTermEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTermEntryRel> iterator =
			_commerceTermEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntryRel commerceTermEntryRel = _persistence.create(pk);

		Assert.assertNotNull(commerceTermEntryRel);

		Assert.assertEquals(commerceTermEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		_persistence.remove(newCommerceTermEntryRel);

		CommerceTermEntryRel existingCommerceTermEntryRel =
			_persistence.fetchByPrimaryKey(
				newCommerceTermEntryRel.getPrimaryKey());

		Assert.assertNull(existingCommerceTermEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTermEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		newCommerceTermEntryRel.setCompanyId(RandomTestUtil.nextLong());

		newCommerceTermEntryRel.setUserId(RandomTestUtil.nextLong());

		newCommerceTermEntryRel.setUserName(RandomTestUtil.randomString());

		newCommerceTermEntryRel.setCreateDate(RandomTestUtil.nextDate());

		newCommerceTermEntryRel.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceTermEntryRel.setClassNameId(RandomTestUtil.nextLong());

		newCommerceTermEntryRel.setClassPK(RandomTestUtil.nextLong());

		newCommerceTermEntryRel.setCommerceTermEntryId(
			RandomTestUtil.nextLong());

		newCommerceTermEntryRel = _persistence.update(newCommerceTermEntryRel);

		_commerceTermEntryRels.add(newCommerceTermEntryRel);

		CommerceTermEntryRel existingCommerceTermEntryRel =
			_persistence.findByPrimaryKey(
				newCommerceTermEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTermEntryRel.getMvccVersion(),
			newCommerceTermEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTermEntryRel.getCommerceTermEntryRelId(),
			newCommerceTermEntryRel.getCommerceTermEntryRelId());
		Assert.assertEquals(
			existingCommerceTermEntryRel.getCompanyId(),
			newCommerceTermEntryRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceTermEntryRel.getUserId(),
			newCommerceTermEntryRel.getUserId());
		Assert.assertEquals(
			existingCommerceTermEntryRel.getUserName(),
			newCommerceTermEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTermEntryRel.getCreateDate()),
			Time.getShortTimestamp(newCommerceTermEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTermEntryRel.getModifiedDate()),
			Time.getShortTimestamp(newCommerceTermEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTermEntryRel.getClassNameId(),
			newCommerceTermEntryRel.getClassNameId());
		Assert.assertEquals(
			existingCommerceTermEntryRel.getClassPK(),
			newCommerceTermEntryRel.getClassPK());
		Assert.assertEquals(
			existingCommerceTermEntryRel.getCommerceTermEntryId(),
			newCommerceTermEntryRel.getCommerceTermEntryId());
	}

	@Test
	public void testCountByCommerceTermEntryId() throws Exception {
		_persistence.countByCommerceTermEntryId(RandomTestUtil.nextLong());

		_persistence.countByCommerceTermEntryId(0L);
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
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		CommerceTermEntryRel existingCommerceTermEntryRel =
			_persistence.findByPrimaryKey(
				newCommerceTermEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTermEntryRel, newCommerceTermEntryRel);
	}

	@Test(expected = NoSuchTermEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTermEntryRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceTermEntryRel", "mvccVersion", true,
			"commerceTermEntryRelId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "commerceTermEntryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		CommerceTermEntryRel existingCommerceTermEntryRel =
			_persistence.fetchByPrimaryKey(
				newCommerceTermEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTermEntryRel, newCommerceTermEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntryRel missingCommerceTermEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTermEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTermEntryRel newCommerceTermEntryRel1 =
			addCommerceTermEntryRel();
		CommerceTermEntryRel newCommerceTermEntryRel2 =
			addCommerceTermEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTermEntryRel1.getPrimaryKey());
		primaryKeys.add(newCommerceTermEntryRel2.getPrimaryKey());

		Map<Serializable, CommerceTermEntryRel> commerceTermEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceTermEntryRels.size());
		Assert.assertEquals(
			newCommerceTermEntryRel1,
			commerceTermEntryRels.get(
				newCommerceTermEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTermEntryRel2,
			commerceTermEntryRels.get(
				newCommerceTermEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTermEntryRel> commerceTermEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTermEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTermEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTermEntryRel> commerceTermEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTermEntryRels.size());
		Assert.assertEquals(
			newCommerceTermEntryRel,
			commerceTermEntryRels.get(newCommerceTermEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTermEntryRel> commerceTermEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTermEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTermEntryRel.getPrimaryKey());

		Map<Serializable, CommerceTermEntryRel> commerceTermEntryRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTermEntryRels.size());
		Assert.assertEquals(
			newCommerceTermEntryRel,
			commerceTermEntryRels.get(newCommerceTermEntryRel.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommerceTermEntryRelLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommerceTermEntryRel>() {

				@Override
				public void performAction(
					CommerceTermEntryRel commerceTermEntryRel) {

					Assert.assertNotNull(commerceTermEntryRel);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceTermEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceTermEntryRelId",
				newCommerceTermEntryRel.getCommerceTermEntryRelId()));

		List<CommerceTermEntryRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommerceTermEntryRel existingCommerceTermEntryRel = result.get(0);

		Assert.assertEquals(
			existingCommerceTermEntryRel, newCommerceTermEntryRel);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceTermEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceTermEntryRelId", RandomTestUtil.nextLong()));

		List<CommerceTermEntryRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceTermEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceTermEntryRelId"));

		Object newCommerceTermEntryRelId =
			newCommerceTermEntryRel.getCommerceTermEntryRelId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceTermEntryRelId",
				new Object[] {newCommerceTermEntryRelId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommerceTermEntryRelId = result.get(0);

		Assert.assertEquals(
			existingCommerceTermEntryRelId, newCommerceTermEntryRelId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceTermEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commerceTermEntryRelId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commerceTermEntryRelId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceTermEntryRel.getPrimaryKey()));
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

		CommerceTermEntryRel newCommerceTermEntryRel =
			addCommerceTermEntryRel();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommerceTermEntryRel.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commerceTermEntryRelId",
				newCommerceTermEntryRel.getCommerceTermEntryRelId()));

		List<CommerceTermEntryRel> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CommerceTermEntryRel commerceTermEntryRel) {

		Assert.assertEquals(
			Long.valueOf(commerceTermEntryRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTermEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceTermEntryRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceTermEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(commerceTermEntryRel.getCommerceTermEntryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTermEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceTermEntryId"));
	}

	protected CommerceTermEntryRel addCommerceTermEntryRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTermEntryRel commerceTermEntryRel = _persistence.create(pk);

		commerceTermEntryRel.setCompanyId(RandomTestUtil.nextLong());

		commerceTermEntryRel.setUserId(RandomTestUtil.nextLong());

		commerceTermEntryRel.setUserName(RandomTestUtil.randomString());

		commerceTermEntryRel.setCreateDate(RandomTestUtil.nextDate());

		commerceTermEntryRel.setModifiedDate(RandomTestUtil.nextDate());

		commerceTermEntryRel.setClassNameId(RandomTestUtil.nextLong());

		commerceTermEntryRel.setClassPK(RandomTestUtil.nextLong());

		commerceTermEntryRel.setCommerceTermEntryId(RandomTestUtil.nextLong());

		_commerceTermEntryRels.add(_persistence.update(commerceTermEntryRel));

		return commerceTermEntryRel;
	}

	private List<CommerceTermEntryRel> _commerceTermEntryRels =
		new ArrayList<CommerceTermEntryRel>();
	private CommerceTermEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:2036157880