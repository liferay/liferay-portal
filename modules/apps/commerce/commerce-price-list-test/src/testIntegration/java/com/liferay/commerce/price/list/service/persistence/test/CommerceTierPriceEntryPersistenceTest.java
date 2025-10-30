/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.price.list.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.price.list.exception.DuplicateCommerceTierPriceEntryExternalReferenceCodeException;
import com.liferay.commerce.price.list.exception.NoSuchTierPriceEntryException;
import com.liferay.commerce.price.list.model.CommerceTierPriceEntry;
import com.liferay.commerce.price.list.service.persistence.CommerceTierPriceEntryPersistence;
import com.liferay.commerce.price.list.service.persistence.CommerceTierPriceEntryUtil;
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
public class CommerceTierPriceEntryPersistenceTest {

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
		_persistence = CommerceTierPriceEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTierPriceEntry> iterator =
			_commerceTierPriceEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTierPriceEntry commerceTierPriceEntry = _persistence.create(pk);

		Assert.assertNotNull(commerceTierPriceEntry);

		Assert.assertEquals(commerceTierPriceEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		_persistence.remove(newCommerceTierPriceEntry);

		CommerceTierPriceEntry existingCommerceTierPriceEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceTierPriceEntry.getPrimaryKey());

		Assert.assertNull(existingCommerceTierPriceEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTierPriceEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTierPriceEntry newCommerceTierPriceEntry = _persistence.create(
			pk);

		newCommerceTierPriceEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceTierPriceEntry.setCtCollectionId(RandomTestUtil.nextLong());

		newCommerceTierPriceEntry.setUuid(RandomTestUtil.randomString());

		newCommerceTierPriceEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceTierPriceEntry.setCompanyId(RandomTestUtil.nextLong());

		newCommerceTierPriceEntry.setUserId(RandomTestUtil.nextLong());

		newCommerceTierPriceEntry.setUserName(RandomTestUtil.randomString());

		newCommerceTierPriceEntry.setCreateDate(RandomTestUtil.nextDate());

		newCommerceTierPriceEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceTierPriceEntry.setCommercePriceEntryId(
			RandomTestUtil.nextLong());

		newCommerceTierPriceEntry.setPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setPromoPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setDiscountDiscovery(
			RandomTestUtil.randomBoolean());

		newCommerceTierPriceEntry.setDiscountLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setDiscountLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setDiscountLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setDiscountLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setMinQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceTierPriceEntry.setDisplayDate(RandomTestUtil.nextDate());

		newCommerceTierPriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		newCommerceTierPriceEntry.setLastPublishDate(RandomTestUtil.nextDate());

		newCommerceTierPriceEntry.setStatus(RandomTestUtil.nextInt());

		newCommerceTierPriceEntry.setStatusByUserId(RandomTestUtil.nextLong());

		newCommerceTierPriceEntry.setStatusByUserName(
			RandomTestUtil.randomString());

		newCommerceTierPriceEntry.setStatusDate(RandomTestUtil.nextDate());

		_commerceTierPriceEntries.add(
			_persistence.update(newCommerceTierPriceEntry));

		CommerceTierPriceEntry existingCommerceTierPriceEntry =
			_persistence.findByPrimaryKey(
				newCommerceTierPriceEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTierPriceEntry.getMvccVersion(),
			newCommerceTierPriceEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getCtCollectionId(),
			newCommerceTierPriceEntry.getCtCollectionId());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getUuid(),
			newCommerceTierPriceEntry.getUuid());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getExternalReferenceCode(),
			newCommerceTierPriceEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getCommerceTierPriceEntryId(),
			newCommerceTierPriceEntry.getCommerceTierPriceEntryId());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getCompanyId(),
			newCommerceTierPriceEntry.getCompanyId());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getUserId(),
			newCommerceTierPriceEntry.getUserId());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getUserName(),
			newCommerceTierPriceEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTierPriceEntry.getCreateDate()),
			Time.getShortTimestamp(newCommerceTierPriceEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTierPriceEntry.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceTierPriceEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getCommercePriceEntryId(),
			newCommerceTierPriceEntry.getCommercePriceEntryId());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getPrice(),
			newCommerceTierPriceEntry.getPrice());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getPromoPrice(),
			newCommerceTierPriceEntry.getPromoPrice());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.isDiscountDiscovery(),
			newCommerceTierPriceEntry.isDiscountDiscovery());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getDiscountLevel1(),
			newCommerceTierPriceEntry.getDiscountLevel1());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getDiscountLevel2(),
			newCommerceTierPriceEntry.getDiscountLevel2());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getDiscountLevel3(),
			newCommerceTierPriceEntry.getDiscountLevel3());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getDiscountLevel4(),
			newCommerceTierPriceEntry.getDiscountLevel4());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getMinQuantity(),
			newCommerceTierPriceEntry.getMinQuantity());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTierPriceEntry.getDisplayDate()),
			Time.getShortTimestamp(newCommerceTierPriceEntry.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTierPriceEntry.getExpirationDate()),
			Time.getShortTimestamp(
				newCommerceTierPriceEntry.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTierPriceEntry.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommerceTierPriceEntry.getLastPublishDate()));
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getStatus(),
			newCommerceTierPriceEntry.getStatus());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getStatusByUserId(),
			newCommerceTierPriceEntry.getStatusByUserId());
		Assert.assertEquals(
			existingCommerceTierPriceEntry.getStatusByUserName(),
			newCommerceTierPriceEntry.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTierPriceEntry.getStatusDate()),
			Time.getShortTimestamp(newCommerceTierPriceEntry.getStatusDate()));
	}

	@Test(
		expected = DuplicateCommerceTierPriceEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceTierPriceEntry commerceTierPriceEntry =
			addCommerceTierPriceEntry();

		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		newCommerceTierPriceEntry.setCompanyId(
			commerceTierPriceEntry.getCompanyId());

		newCommerceTierPriceEntry = _persistence.update(
			newCommerceTierPriceEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceTierPriceEntry);

		newCommerceTierPriceEntry.setExternalReferenceCode(
			commerceTierPriceEntry.getExternalReferenceCode());

		_persistence.update(newCommerceTierPriceEntry);
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
	public void testCountByCommercePriceEntryId() throws Exception {
		_persistence.countByCommercePriceEntryId(RandomTestUtil.nextLong());

		_persistence.countByCommercePriceEntryId(0L);
	}

	@Test
	public void testCountByC_M() throws Exception {
		_persistence.countByC_M(RandomTestUtil.nextLong(), (BigDecimal)null);

		_persistence.countByC_M(0L, (BigDecimal)null);
	}

	@Test
	public void testCountByC_LteM() throws Exception {
		_persistence.countByC_LteM(RandomTestUtil.nextLong(), (BigDecimal)null);

		_persistence.countByC_LteM(0L, (BigDecimal)null);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
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
	public void testCountByC_LteM_S() throws Exception {
		_persistence.countByC_LteM_S(
			RandomTestUtil.nextLong(), (BigDecimal)null,
			RandomTestUtil.nextInt());

		_persistence.countByC_LteM_S(0L, (BigDecimal)null, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		CommerceTierPriceEntry existingCommerceTierPriceEntry =
			_persistence.findByPrimaryKey(
				newCommerceTierPriceEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTierPriceEntry, newCommerceTierPriceEntry);
	}

	@Test(expected = NoSuchTierPriceEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTierPriceEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceTierPriceEntry", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"commerceTierPriceEntryId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"commercePriceEntryId", true, "price", true, "promoPrice", true,
			"discountDiscovery", true, "discountLevel1", true, "discountLevel2",
			true, "discountLevel3", true, "discountLevel4", true, "minQuantity",
			true, "displayDate", true, "expirationDate", true,
			"lastPublishDate", true, "status", true, "statusByUserId", true,
			"statusByUserName", true, "statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		CommerceTierPriceEntry existingCommerceTierPriceEntry =
			_persistence.fetchByPrimaryKey(
				newCommerceTierPriceEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTierPriceEntry, newCommerceTierPriceEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTierPriceEntry missingCommerceTierPriceEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTierPriceEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTierPriceEntry newCommerceTierPriceEntry1 =
			addCommerceTierPriceEntry();
		CommerceTierPriceEntry newCommerceTierPriceEntry2 =
			addCommerceTierPriceEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTierPriceEntry1.getPrimaryKey());
		primaryKeys.add(newCommerceTierPriceEntry2.getPrimaryKey());

		Map<Serializable, CommerceTierPriceEntry> commerceTierPriceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceTierPriceEntries.size());
		Assert.assertEquals(
			newCommerceTierPriceEntry1,
			commerceTierPriceEntries.get(
				newCommerceTierPriceEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTierPriceEntry2,
			commerceTierPriceEntries.get(
				newCommerceTierPriceEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTierPriceEntry> commerceTierPriceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTierPriceEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTierPriceEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTierPriceEntry> commerceTierPriceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTierPriceEntries.size());
		Assert.assertEquals(
			newCommerceTierPriceEntry,
			commerceTierPriceEntries.get(
				newCommerceTierPriceEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTierPriceEntry> commerceTierPriceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTierPriceEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTierPriceEntry.getPrimaryKey());

		Map<Serializable, CommerceTierPriceEntry> commerceTierPriceEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTierPriceEntries.size());
		Assert.assertEquals(
			newCommerceTierPriceEntry,
			commerceTierPriceEntries.get(
				newCommerceTierPriceEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceTierPriceEntry newCommerceTierPriceEntry =
			addCommerceTierPriceEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceTierPriceEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceTierPriceEntry commerceTierPriceEntry) {

		Assert.assertEquals(
			Long.valueOf(commerceTierPriceEntry.getCommercePriceEntryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTierPriceEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commercePriceEntryId"));
		Assert.assertEquals(
			commerceTierPriceEntry.getMinQuantity(),
			ReflectionTestUtil.invoke(
				commerceTierPriceEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "minQuantity"));

		Assert.assertEquals(
			commerceTierPriceEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceTierPriceEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceTierPriceEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTierPriceEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceTierPriceEntry addCommerceTierPriceEntry()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceTierPriceEntry commerceTierPriceEntry = _persistence.create(pk);

		commerceTierPriceEntry.setMvccVersion(RandomTestUtil.nextLong());

		commerceTierPriceEntry.setCtCollectionId(RandomTestUtil.nextLong());

		commerceTierPriceEntry.setUuid(RandomTestUtil.randomString());

		commerceTierPriceEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceTierPriceEntry.setCompanyId(RandomTestUtil.nextLong());

		commerceTierPriceEntry.setUserId(RandomTestUtil.nextLong());

		commerceTierPriceEntry.setUserName(RandomTestUtil.randomString());

		commerceTierPriceEntry.setCreateDate(RandomTestUtil.nextDate());

		commerceTierPriceEntry.setModifiedDate(RandomTestUtil.nextDate());

		commerceTierPriceEntry.setCommercePriceEntryId(
			RandomTestUtil.nextLong());

		commerceTierPriceEntry.setPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setPromoPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setDiscountDiscovery(
			RandomTestUtil.randomBoolean());

		commerceTierPriceEntry.setDiscountLevel1(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setDiscountLevel2(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setDiscountLevel3(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setDiscountLevel4(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setMinQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceTierPriceEntry.setDisplayDate(RandomTestUtil.nextDate());

		commerceTierPriceEntry.setExpirationDate(RandomTestUtil.nextDate());

		commerceTierPriceEntry.setLastPublishDate(RandomTestUtil.nextDate());

		commerceTierPriceEntry.setStatus(RandomTestUtil.nextInt());

		commerceTierPriceEntry.setStatusByUserId(RandomTestUtil.nextLong());

		commerceTierPriceEntry.setStatusByUserName(
			RandomTestUtil.randomString());

		commerceTierPriceEntry.setStatusDate(RandomTestUtil.nextDate());

		_commerceTierPriceEntries.add(
			_persistence.update(commerceTierPriceEntry));

		return commerceTierPriceEntry;
	}

	private List<CommerceTierPriceEntry> _commerceTierPriceEntries =
		new ArrayList<CommerceTierPriceEntry>();
	private CommerceTierPriceEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}