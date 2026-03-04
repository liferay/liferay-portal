/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.DuplicateCommercePriceListExternalReferenceCodeException;
import com.liferay.commerce.price.list.exception.NoSuchPriceListException;
import com.liferay.commerce.price.list.model.CommercePriceList;
import com.liferay.commerce.price.list.service.CommercePriceListLocalServiceUtil;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListPersistence;
import com.liferay.commerce.price.list.service.persistence.CommercePriceListUtil;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CommercePriceListPersistenceTest {

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
		_persistence = CommercePriceListUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePriceList> iterator = _commercePriceLists.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceList commercePriceList = _persistence.create(pk);

		Assert.assertNotNull(commercePriceList);

		Assert.assertEquals(commercePriceList.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePriceList newCommercePriceList = addCommercePriceList();

		_persistence.remove(newCommercePriceList);

		CommercePriceList existingCommercePriceList =
			_persistence.fetchByPrimaryKey(
				newCommercePriceList.getPrimaryKey());

		Assert.assertNull(existingCommercePriceList);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePriceList();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceList newCommercePriceList = _persistence.create(pk);

		newCommercePriceList.setMvccVersion(RandomTestUtil.nextLong());

		newCommercePriceList.setCtCollectionId(RandomTestUtil.nextLong());

		newCommercePriceList.setUuid(RandomTestUtil.randomString());

		newCommercePriceList.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommercePriceList.setGroupId(RandomTestUtil.nextLong());

		newCommercePriceList.setCompanyId(RandomTestUtil.nextLong());

		newCommercePriceList.setUserId(RandomTestUtil.nextLong());

		newCommercePriceList.setUserName(RandomTestUtil.randomString());

		newCommercePriceList.setCreateDate(RandomTestUtil.nextDate());

		newCommercePriceList.setModifiedDate(RandomTestUtil.nextDate());

		newCommercePriceList.setCatalogBasePriceList(
			RandomTestUtil.randomBoolean());

		newCommercePriceList.setCommerceCurrencyCode(
			RandomTestUtil.randomString());

		newCommercePriceList.setDisplayDate(RandomTestUtil.nextDate());

		newCommercePriceList.setExpirationDate(RandomTestUtil.nextDate());

		newCommercePriceList.setName(RandomTestUtil.randomString());

		newCommercePriceList.setNetPrice(RandomTestUtil.randomBoolean());

		newCommercePriceList.setParentCommercePriceListId(
			RandomTestUtil.nextLong());

		newCommercePriceList.setPriority(RandomTestUtil.nextDouble());

		newCommercePriceList.setType(RandomTestUtil.randomString());

		newCommercePriceList.setLastPublishDate(RandomTestUtil.nextDate());

		newCommercePriceList.setStatus(RandomTestUtil.nextInt());

		newCommercePriceList.setStatusByUserId(RandomTestUtil.nextLong());

		newCommercePriceList.setStatusByUserName(RandomTestUtil.randomString());

		newCommercePriceList.setStatusDate(RandomTestUtil.nextDate());

		_commercePriceLists.add(_persistence.update(newCommercePriceList));

		CommercePriceList existingCommercePriceList =
			_persistence.findByPrimaryKey(newCommercePriceList.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePriceList.getMvccVersion(),
			newCommercePriceList.getMvccVersion());
		Assert.assertEquals(
			existingCommercePriceList.getCtCollectionId(),
			newCommercePriceList.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePriceList.getUuid(),
			newCommercePriceList.getUuid());
		Assert.assertEquals(
			existingCommercePriceList.getExternalReferenceCode(),
			newCommercePriceList.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommercePriceList.getCommercePriceListId(),
			newCommercePriceList.getCommercePriceListId());
		Assert.assertEquals(
			existingCommercePriceList.getGroupId(),
			newCommercePriceList.getGroupId());
		Assert.assertEquals(
			existingCommercePriceList.getCompanyId(),
			newCommercePriceList.getCompanyId());
		Assert.assertEquals(
			existingCommercePriceList.getUserId(),
			newCommercePriceList.getUserId());
		Assert.assertEquals(
			existingCommercePriceList.getUserName(),
			newCommercePriceList.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommercePriceList.getCreateDate()),
			Time.getShortTimestamp(newCommercePriceList.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommercePriceList.getModifiedDate()),
			Time.getShortTimestamp(newCommercePriceList.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePriceList.isCatalogBasePriceList(),
			newCommercePriceList.isCatalogBasePriceList());
		Assert.assertEquals(
			existingCommercePriceList.getCommerceCurrencyCode(),
			newCommercePriceList.getCommerceCurrencyCode());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommercePriceList.getDisplayDate()),
			Time.getShortTimestamp(newCommercePriceList.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceList.getExpirationDate()),
			Time.getShortTimestamp(newCommercePriceList.getExpirationDate()));
		Assert.assertEquals(
			existingCommercePriceList.getName(),
			newCommercePriceList.getName());
		Assert.assertEquals(
			existingCommercePriceList.isNetPrice(),
			newCommercePriceList.isNetPrice());
		Assert.assertEquals(
			existingCommercePriceList.getParentCommercePriceListId(),
			newCommercePriceList.getParentCommercePriceListId());
		AssertUtils.assertEquals(
			existingCommercePriceList.getPriority(),
			newCommercePriceList.getPriority());
		Assert.assertEquals(
			existingCommercePriceList.getType(),
			newCommercePriceList.getType());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePriceList.getLastPublishDate()),
			Time.getShortTimestamp(newCommercePriceList.getLastPublishDate()));
		Assert.assertEquals(
			existingCommercePriceList.getStatus(),
			newCommercePriceList.getStatus());
		Assert.assertEquals(
			existingCommercePriceList.getStatusByUserId(),
			newCommercePriceList.getStatusByUserId());
		Assert.assertEquals(
			existingCommercePriceList.getStatusByUserName(),
			newCommercePriceList.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommercePriceList.getStatusDate()),
			Time.getShortTimestamp(newCommercePriceList.getStatusDate()));
	}

	@Test(
		expected = DuplicateCommercePriceListExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommercePriceList commercePriceList = addCommercePriceList();

		CommercePriceList newCommercePriceList = addCommercePriceList();

		newCommercePriceList.setCompanyId(commercePriceList.getCompanyId());

		newCommercePriceList = _persistence.update(newCommercePriceList);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommercePriceList);

		newCommercePriceList.setExternalReferenceCode(
			commercePriceList.getExternalReferenceCode());

		_persistence.update(newCommercePriceList);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByParentCommercePriceListId() throws Exception {
		_persistence.countByParentCommercePriceListId(
			RandomTestUtil.nextLong());

		_persistence.countByParentCommercePriceListId(0L);
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
	public void testCountByG_CBPL() throws Exception {
		_persistence.countByG_CBPL(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_CBPL(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByG_C_S() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_SArrayable() throws Exception {
		_persistence.countByG_C_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_C_NotS() throws Exception {
		_persistence.countByG_C_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_NotSArrayable() throws Exception {
		_persistence.countByG_C_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_CBPL_T() throws Exception {
		_persistence.countByG_CBPL_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_CBPL_T(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_CBPL_T(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_C_T_S() throws Exception {
		_persistence.countByG_C_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_C_T_S(0L, 0L, "null", 0);

		_persistence.countByG_C_T_S(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_C_T_SArrayable() throws Exception {
		_persistence.countByG_C_T_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
			RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_C_T_NotS() throws Exception {
		_persistence.countByG_C_T_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "",
			RandomTestUtil.nextInt());

		_persistence.countByG_C_T_NotS(0L, 0L, "null", 0);

		_persistence.countByG_C_T_NotS(0L, 0L, (String)null, 0);
	}

	@Test
	public void testCountByG_C_T_NotSArrayable() throws Exception {
		_persistence.countByG_C_T_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.randomString(),
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
		CommercePriceList newCommercePriceList = addCommercePriceList();

		CommercePriceList existingCommercePriceList =
			_persistence.findByPrimaryKey(newCommercePriceList.getPrimaryKey());

		Assert.assertEquals(existingCommercePriceList, newCommercePriceList);
	}

	@Test(expected = NoSuchPriceListException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePriceList> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommercePriceList", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "commercePriceListId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"catalogBasePriceList", true, "commerceCurrencyCode", true,
			"displayDate", true, "expirationDate", true, "name", true,
			"netPrice", true, "parentCommercePriceListId", true, "priority",
			true, "type", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePriceList newCommercePriceList = addCommercePriceList();

		CommercePriceList existingCommercePriceList =
			_persistence.fetchByPrimaryKey(
				newCommercePriceList.getPrimaryKey());

		Assert.assertEquals(existingCommercePriceList, newCommercePriceList);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceList missingCommercePriceList =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePriceList);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePriceList newCommercePriceList1 = addCommercePriceList();
		CommercePriceList newCommercePriceList2 = addCommercePriceList();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceList1.getPrimaryKey());
		primaryKeys.add(newCommercePriceList2.getPrimaryKey());

		Map<Serializable, CommercePriceList> commercePriceLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commercePriceLists.size());
		Assert.assertEquals(
			newCommercePriceList1,
			commercePriceLists.get(newCommercePriceList1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePriceList2,
			commercePriceLists.get(newCommercePriceList2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePriceList> commercePriceLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePriceLists.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePriceList newCommercePriceList = addCommercePriceList();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceList.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePriceList> commercePriceLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePriceLists.size());
		Assert.assertEquals(
			newCommercePriceList,
			commercePriceLists.get(newCommercePriceList.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePriceList> commercePriceLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePriceLists.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePriceList newCommercePriceList = addCommercePriceList();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePriceList.getPrimaryKey());

		Map<Serializable, CommercePriceList> commercePriceLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePriceLists.size());
		Assert.assertEquals(
			newCommercePriceList,
			commercePriceLists.get(newCommercePriceList.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CommercePriceListLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CommercePriceList>() {

				@Override
				public void performAction(CommercePriceList commercePriceList) {
					Assert.assertNotNull(commercePriceList);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CommercePriceList newCommercePriceList = addCommercePriceList();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceList.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceListId",
				newCommercePriceList.getCommercePriceListId()));

		List<CommercePriceList> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CommercePriceList existingCommercePriceList = result.get(0);

		Assert.assertEquals(existingCommercePriceList, newCommercePriceList);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceList.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceListId", RandomTestUtil.nextLong()));

		List<CommercePriceList> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CommercePriceList newCommercePriceList = addCommercePriceList();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceList.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commercePriceListId"));

		Object newCommercePriceListId =
			newCommercePriceList.getCommercePriceListId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commercePriceListId", new Object[] {newCommercePriceListId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCommercePriceListId = result.get(0);

		Assert.assertEquals(
			existingCommercePriceListId, newCommercePriceListId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceList.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("commercePriceListId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"commercePriceListId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePriceList newCommercePriceList = addCommercePriceList();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePriceList.getPrimaryKey()));
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

		CommercePriceList newCommercePriceList = addCommercePriceList();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CommercePriceList.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"commercePriceListId",
				newCommercePriceList.getCommercePriceListId()));

		List<CommercePriceList> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(CommercePriceList commercePriceList) {
		Assert.assertEquals(
			commercePriceList.getUuid(),
			ReflectionTestUtil.invoke(
				commercePriceList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commercePriceList.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			commercePriceList.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commercePriceList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commercePriceList.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commercePriceList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommercePriceList addCommercePriceList() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePriceList commercePriceList = _persistence.create(pk);

		commercePriceList.setMvccVersion(RandomTestUtil.nextLong());

		commercePriceList.setCtCollectionId(RandomTestUtil.nextLong());

		commercePriceList.setUuid(RandomTestUtil.randomString());

		commercePriceList.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commercePriceList.setGroupId(RandomTestUtil.nextLong());

		commercePriceList.setCompanyId(RandomTestUtil.nextLong());

		commercePriceList.setUserId(RandomTestUtil.nextLong());

		commercePriceList.setUserName(RandomTestUtil.randomString());

		commercePriceList.setCreateDate(RandomTestUtil.nextDate());

		commercePriceList.setModifiedDate(RandomTestUtil.nextDate());

		commercePriceList.setCatalogBasePriceList(
			RandomTestUtil.randomBoolean());

		commercePriceList.setCommerceCurrencyCode(
			RandomTestUtil.randomString());

		commercePriceList.setDisplayDate(RandomTestUtil.nextDate());

		commercePriceList.setExpirationDate(RandomTestUtil.nextDate());

		commercePriceList.setName(RandomTestUtil.randomString());

		commercePriceList.setNetPrice(RandomTestUtil.randomBoolean());

		commercePriceList.setParentCommercePriceListId(
			RandomTestUtil.nextLong());

		commercePriceList.setPriority(RandomTestUtil.nextDouble());

		commercePriceList.setType(RandomTestUtil.randomString());

		commercePriceList.setLastPublishDate(RandomTestUtil.nextDate());

		commercePriceList.setStatus(RandomTestUtil.nextInt());

		commercePriceList.setStatusByUserId(RandomTestUtil.nextLong());

		commercePriceList.setStatusByUserName(RandomTestUtil.randomString());

		commercePriceList.setStatusDate(RandomTestUtil.nextDate());

		_commercePriceLists.add(_persistence.update(commercePriceList));

		return commercePriceList;
	}

	private List<CommercePriceList> _commercePriceLists =
		new ArrayList<CommercePriceList>();
	private CommercePriceListPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}