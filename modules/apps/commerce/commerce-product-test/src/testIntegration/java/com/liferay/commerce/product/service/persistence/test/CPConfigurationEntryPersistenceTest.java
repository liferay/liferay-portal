/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPConfigurationEntryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationEntryException;
import com.liferay.commerce.product.model.CPConfigurationEntry;
import com.liferay.commerce.product.service.CPConfigurationEntryLocalServiceUtil;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationEntryUtil;
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

import java.math.BigDecimal;

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
public class CPConfigurationEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPConfigurationEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPConfigurationEntry> iterator =
			_cpConfigurationEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntry cpConfigurationEntry = _persistence.create(pk);

		Assert.assertNotNull(cpConfigurationEntry);

		Assert.assertEquals(cpConfigurationEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		_persistence.remove(newCPConfigurationEntry);

		CPConfigurationEntry existingCPConfigurationEntry =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationEntry.getPrimaryKey());

		Assert.assertNull(existingCPConfigurationEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPConfigurationEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntry newCPConfigurationEntry = _persistence.create(pk);

		newCPConfigurationEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setUuid(RandomTestUtil.randomString());

		newCPConfigurationEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPConfigurationEntry.setGroupId(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setCompanyId(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setUserId(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setUserName(RandomTestUtil.randomString());

		newCPConfigurationEntry.setCreateDate(RandomTestUtil.nextDate());

		newCPConfigurationEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCPConfigurationEntry.setClassNameId(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setClassPK(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setCPConfigurationListId(
			RandomTestUtil.nextLong());

		newCPConfigurationEntry.setCPTaxCategoryId(RandomTestUtil.nextLong());

		newCPConfigurationEntry.setAllowedOrderQuantities(
			RandomTestUtil.randomString());

		newCPConfigurationEntry.setBackOrders(RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setCommerceAvailabilityEstimateId(
			RandomTestUtil.nextLong());

		newCPConfigurationEntry.setCPDefinitionInventoryEngine(
			RandomTestUtil.randomString());

		newCPConfigurationEntry.setDepth(RandomTestUtil.nextDouble());

		newCPConfigurationEntry.setDisplayAvailability(
			RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setDisplayStockQuantity(
			RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setFreeShipping(RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setHeight(RandomTestUtil.nextDouble());

		newCPConfigurationEntry.setLowStockActivity(
			RandomTestUtil.randomString());

		newCPConfigurationEntry.setMaxOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPConfigurationEntry.setMinOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPConfigurationEntry.setMinStockQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPConfigurationEntry.setMultipleOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPConfigurationEntry.setPurchasable(RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setShippable(RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setShippingExtraPrice(
			RandomTestUtil.nextDouble());

		newCPConfigurationEntry.setShipSeparately(
			RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setTaxExempt(RandomTestUtil.randomBoolean());

		newCPConfigurationEntry.setWeight(RandomTestUtil.nextDouble());

		newCPConfigurationEntry.setWidth(RandomTestUtil.nextDouble());

		_cpConfigurationEntries.add(
			_persistence.update(newCPConfigurationEntry));

		CPConfigurationEntry existingCPConfigurationEntry =
			_persistence.findByPrimaryKey(
				newCPConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationEntry.getMvccVersion(),
			newCPConfigurationEntry.getMvccVersion());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCtCollectionId(),
			newCPConfigurationEntry.getCtCollectionId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getUuid(),
			newCPConfigurationEntry.getUuid());
		Assert.assertEquals(
			existingCPConfigurationEntry.getExternalReferenceCode(),
			newCPConfigurationEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCPConfigurationEntryId(),
			newCPConfigurationEntry.getCPConfigurationEntryId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getGroupId(),
			newCPConfigurationEntry.getGroupId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCompanyId(),
			newCPConfigurationEntry.getCompanyId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getUserId(),
			newCPConfigurationEntry.getUserId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getUserName(),
			newCPConfigurationEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationEntry.getCreateDate()),
			Time.getShortTimestamp(newCPConfigurationEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationEntry.getModifiedDate()),
			Time.getShortTimestamp(newCPConfigurationEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCPConfigurationEntry.getClassNameId(),
			newCPConfigurationEntry.getClassNameId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getClassPK(),
			newCPConfigurationEntry.getClassPK());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCPConfigurationListId(),
			newCPConfigurationEntry.getCPConfigurationListId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCPTaxCategoryId(),
			newCPConfigurationEntry.getCPTaxCategoryId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getAllowedOrderQuantities(),
			newCPConfigurationEntry.getAllowedOrderQuantities());
		Assert.assertEquals(
			existingCPConfigurationEntry.isBackOrders(),
			newCPConfigurationEntry.isBackOrders());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCommerceAvailabilityEstimateId(),
			newCPConfigurationEntry.getCommerceAvailabilityEstimateId());
		Assert.assertEquals(
			existingCPConfigurationEntry.getCPDefinitionInventoryEngine(),
			newCPConfigurationEntry.getCPDefinitionInventoryEngine());
		AssertUtils.assertEquals(
			existingCPConfigurationEntry.getDepth(),
			newCPConfigurationEntry.getDepth());
		Assert.assertEquals(
			existingCPConfigurationEntry.isDisplayAvailability(),
			newCPConfigurationEntry.isDisplayAvailability());
		Assert.assertEquals(
			existingCPConfigurationEntry.isDisplayStockQuantity(),
			newCPConfigurationEntry.isDisplayStockQuantity());
		Assert.assertEquals(
			existingCPConfigurationEntry.isFreeShipping(),
			newCPConfigurationEntry.isFreeShipping());
		AssertUtils.assertEquals(
			existingCPConfigurationEntry.getHeight(),
			newCPConfigurationEntry.getHeight());
		Assert.assertEquals(
			existingCPConfigurationEntry.getLowStockActivity(),
			newCPConfigurationEntry.getLowStockActivity());
		Assert.assertEquals(
			existingCPConfigurationEntry.getMaxOrderQuantity(),
			newCPConfigurationEntry.getMaxOrderQuantity());
		Assert.assertEquals(
			existingCPConfigurationEntry.getMinOrderQuantity(),
			newCPConfigurationEntry.getMinOrderQuantity());
		Assert.assertEquals(
			existingCPConfigurationEntry.getMinStockQuantity(),
			newCPConfigurationEntry.getMinStockQuantity());
		Assert.assertEquals(
			existingCPConfigurationEntry.getMultipleOrderQuantity(),
			newCPConfigurationEntry.getMultipleOrderQuantity());
		Assert.assertEquals(
			existingCPConfigurationEntry.isPurchasable(),
			newCPConfigurationEntry.isPurchasable());
		Assert.assertEquals(
			existingCPConfigurationEntry.isShippable(),
			newCPConfigurationEntry.isShippable());
		AssertUtils.assertEquals(
			existingCPConfigurationEntry.getShippingExtraPrice(),
			newCPConfigurationEntry.getShippingExtraPrice());
		Assert.assertEquals(
			existingCPConfigurationEntry.isShipSeparately(),
			newCPConfigurationEntry.isShipSeparately());
		Assert.assertEquals(
			existingCPConfigurationEntry.isTaxExempt(),
			newCPConfigurationEntry.isTaxExempt());
		AssertUtils.assertEquals(
			existingCPConfigurationEntry.getWeight(),
			newCPConfigurationEntry.getWeight());
		AssertUtils.assertEquals(
			existingCPConfigurationEntry.getWidth(),
			newCPConfigurationEntry.getWidth());
	}

	@Test(
		expected = DuplicateCPConfigurationEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPConfigurationEntry cpConfigurationEntry = addCPConfigurationEntry();

		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		newCPConfigurationEntry.setCompanyId(
			cpConfigurationEntry.getCompanyId());

		newCPConfigurationEntry = _persistence.update(newCPConfigurationEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPConfigurationEntry);

		newCPConfigurationEntry.setExternalReferenceCode(
			cpConfigurationEntry.getExternalReferenceCode());

		_persistence.update(newCPConfigurationEntry);
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
	public void testCountByCPConfigurationListId() throws Exception {
		_persistence.countByCPConfigurationListId(RandomTestUtil.nextLong());

		_persistence.countByCPConfigurationListId(0L);
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
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		CPConfigurationEntry existingCPConfigurationEntry =
			_persistence.findByPrimaryKey(
				newCPConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationEntry, newCPConfigurationEntry);
	}

	@Test(expected = NoSuchCPConfigurationEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPConfigurationEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPConfigurationEntry", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true,
			"CPConfigurationEntryId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"CPConfigurationListId", true, "CPTaxCategoryId", true,
			"allowedOrderQuantities", true, "backOrders", true,
			"commerceAvailabilityEstimateId", true,
			"CPDefinitionInventoryEngine", true, "depth", true,
			"displayAvailability", true, "displayStockQuantity", true,
			"freeShipping", true, "height", true, "lowStockActivity", true,
			"maxOrderQuantity", true, "minOrderQuantity", true,
			"minStockQuantity", true, "multipleOrderQuantity", true,
			"purchasable", true, "shippable", true, "shippingExtraPrice", true,
			"shipSeparately", true, "taxExempt", true, "weight", true, "width",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		CPConfigurationEntry existingCPConfigurationEntry =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationEntry, newCPConfigurationEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntry missingCPConfigurationEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPConfigurationEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPConfigurationEntry newCPConfigurationEntry1 =
			addCPConfigurationEntry();
		CPConfigurationEntry newCPConfigurationEntry2 =
			addCPConfigurationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationEntry1.getPrimaryKey());
		primaryKeys.add(newCPConfigurationEntry2.getPrimaryKey());

		Map<Serializable, CPConfigurationEntry> cpConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpConfigurationEntries.size());
		Assert.assertEquals(
			newCPConfigurationEntry1,
			cpConfigurationEntries.get(
				newCPConfigurationEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCPConfigurationEntry2,
			cpConfigurationEntries.get(
				newCPConfigurationEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPConfigurationEntry> cpConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpConfigurationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPConfigurationEntry> cpConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpConfigurationEntries.size());
		Assert.assertEquals(
			newCPConfigurationEntry,
			cpConfigurationEntries.get(
				newCPConfigurationEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPConfigurationEntry> cpConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpConfigurationEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationEntry.getPrimaryKey());

		Map<Serializable, CPConfigurationEntry> cpConfigurationEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpConfigurationEntries.size());
		Assert.assertEquals(
			newCPConfigurationEntry,
			cpConfigurationEntries.get(
				newCPConfigurationEntry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CPConfigurationEntryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<CPConfigurationEntry>() {

				@Override
				public void performAction(
					CPConfigurationEntry cpConfigurationEntry) {

					Assert.assertNotNull(cpConfigurationEntry);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPConfigurationEntryId",
				newCPConfigurationEntry.getCPConfigurationEntryId()));

		List<CPConfigurationEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CPConfigurationEntry existingCPConfigurationEntry = result.get(0);

		Assert.assertEquals(
			existingCPConfigurationEntry, newCPConfigurationEntry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPConfigurationEntryId", RandomTestUtil.nextLong()));

		List<CPConfigurationEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CPConfigurationEntryId"));

		Object newCPConfigurationEntryId =
			newCPConfigurationEntry.getCPConfigurationEntryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CPConfigurationEntryId",
				new Object[] {newCPConfigurationEntryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCPConfigurationEntryId = result.get(0);

		Assert.assertEquals(
			existingCPConfigurationEntryId, newCPConfigurationEntryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("CPConfigurationEntryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"CPConfigurationEntryId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPConfigurationEntry.getPrimaryKey()));
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

		CPConfigurationEntry newCPConfigurationEntry =
			addCPConfigurationEntry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPConfigurationEntry.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"CPConfigurationEntryId",
				newCPConfigurationEntry.getCPConfigurationEntryId()));

		List<CPConfigurationEntry> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CPConfigurationEntry cpConfigurationEntry) {

		Assert.assertEquals(
			cpConfigurationEntry.getUuid(),
			ReflectionTestUtil.invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationEntry.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpConfigurationEntry.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationEntry.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationEntry.getCPConfigurationListId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPConfigurationListId"));

		Assert.assertEquals(
			cpConfigurationEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPConfigurationEntry addCPConfigurationEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationEntry cpConfigurationEntry = _persistence.create(pk);

		cpConfigurationEntry.setMvccVersion(RandomTestUtil.nextLong());

		cpConfigurationEntry.setCtCollectionId(RandomTestUtil.nextLong());

		cpConfigurationEntry.setUuid(RandomTestUtil.randomString());

		cpConfigurationEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		cpConfigurationEntry.setGroupId(RandomTestUtil.nextLong());

		cpConfigurationEntry.setCompanyId(RandomTestUtil.nextLong());

		cpConfigurationEntry.setUserId(RandomTestUtil.nextLong());

		cpConfigurationEntry.setUserName(RandomTestUtil.randomString());

		cpConfigurationEntry.setCreateDate(RandomTestUtil.nextDate());

		cpConfigurationEntry.setModifiedDate(RandomTestUtil.nextDate());

		cpConfigurationEntry.setClassNameId(RandomTestUtil.nextLong());

		cpConfigurationEntry.setClassPK(RandomTestUtil.nextLong());

		cpConfigurationEntry.setCPConfigurationListId(
			RandomTestUtil.nextLong());

		cpConfigurationEntry.setCPTaxCategoryId(RandomTestUtil.nextLong());

		cpConfigurationEntry.setAllowedOrderQuantities(
			RandomTestUtil.randomString());

		cpConfigurationEntry.setBackOrders(RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setCommerceAvailabilityEstimateId(
			RandomTestUtil.nextLong());

		cpConfigurationEntry.setCPDefinitionInventoryEngine(
			RandomTestUtil.randomString());

		cpConfigurationEntry.setDepth(RandomTestUtil.nextDouble());

		cpConfigurationEntry.setDisplayAvailability(
			RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setDisplayStockQuantity(
			RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setFreeShipping(RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setHeight(RandomTestUtil.nextDouble());

		cpConfigurationEntry.setLowStockActivity(RandomTestUtil.randomString());

		cpConfigurationEntry.setMaxOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpConfigurationEntry.setMinOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpConfigurationEntry.setMinStockQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpConfigurationEntry.setMultipleOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpConfigurationEntry.setPurchasable(RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setShippable(RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setShippingExtraPrice(RandomTestUtil.nextDouble());

		cpConfigurationEntry.setShipSeparately(RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setTaxExempt(RandomTestUtil.randomBoolean());

		cpConfigurationEntry.setWeight(RandomTestUtil.nextDouble());

		cpConfigurationEntry.setWidth(RandomTestUtil.nextDouble());

		_cpConfigurationEntries.add(_persistence.update(cpConfigurationEntry));

		return cpConfigurationEntry;
	}

	private List<CPConfigurationEntry> _cpConfigurationEntries =
		new ArrayList<CPConfigurationEntry>();
	private CPConfigurationEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}