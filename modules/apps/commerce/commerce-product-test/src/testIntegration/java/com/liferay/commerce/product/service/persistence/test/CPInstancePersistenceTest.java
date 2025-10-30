/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPInstanceExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPInstanceException;
import com.liferay.commerce.product.model.CPInstance;
import com.liferay.commerce.product.service.persistence.CPInstancePersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class CPInstancePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPInstanceUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPInstance> iterator = _cpInstances.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstance cpInstance = _persistence.create(pk);

		Assert.assertNotNull(cpInstance);

		Assert.assertEquals(cpInstance.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPInstance newCPInstance = addCPInstance();

		_persistence.remove(newCPInstance);

		CPInstance existingCPInstance = _persistence.fetchByPrimaryKey(
			newCPInstance.getPrimaryKey());

		Assert.assertNull(existingCPInstance);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPInstance();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstance newCPInstance = _persistence.create(pk);

		newCPInstance.setMvccVersion(RandomTestUtil.nextLong());

		newCPInstance.setCtCollectionId(RandomTestUtil.nextLong());

		newCPInstance.setUuid(RandomTestUtil.randomString());

		newCPInstance.setExternalReferenceCode(RandomTestUtil.randomString());

		newCPInstance.setGroupId(RandomTestUtil.nextLong());

		newCPInstance.setCompanyId(RandomTestUtil.nextLong());

		newCPInstance.setUserId(RandomTestUtil.nextLong());

		newCPInstance.setUserName(RandomTestUtil.randomString());

		newCPInstance.setCreateDate(RandomTestUtil.nextDate());

		newCPInstance.setModifiedDate(RandomTestUtil.nextDate());

		newCPInstance.setCPDefinitionId(RandomTestUtil.nextLong());

		newCPInstance.setCPInstanceUuid(RandomTestUtil.randomString());

		newCPInstance.setSku(RandomTestUtil.randomString());

		newCPInstance.setGtin(RandomTestUtil.randomString());

		newCPInstance.setManufacturerPartNumber(RandomTestUtil.randomString());

		newCPInstance.setPurchasable(RandomTestUtil.randomBoolean());

		newCPInstance.setWidth(RandomTestUtil.nextDouble());

		newCPInstance.setHeight(RandomTestUtil.nextDouble());

		newCPInstance.setDepth(RandomTestUtil.nextDouble());

		newCPInstance.setWeight(RandomTestUtil.nextDouble());

		newCPInstance.setPrice(new BigDecimal(RandomTestUtil.nextDouble()));

		newCPInstance.setPromoPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPInstance.setCost(new BigDecimal(RandomTestUtil.nextDouble()));

		newCPInstance.setPublished(RandomTestUtil.randomBoolean());

		newCPInstance.setDisplayDate(RandomTestUtil.nextDate());

		newCPInstance.setExpirationDate(RandomTestUtil.nextDate());

		newCPInstance.setLastPublishDate(RandomTestUtil.nextDate());

		newCPInstance.setOverrideSubscriptionInfo(
			RandomTestUtil.randomBoolean());

		newCPInstance.setSubscriptionEnabled(RandomTestUtil.randomBoolean());

		newCPInstance.setSubscriptionLength(RandomTestUtil.nextInt());

		newCPInstance.setSubscriptionType(RandomTestUtil.randomString());

		newCPInstance.setSubscriptionTypeSettings(
			RandomTestUtil.randomString());

		newCPInstance.setMaxSubscriptionCycles(RandomTestUtil.nextLong());

		newCPInstance.setDeliverySubscriptionEnabled(
			RandomTestUtil.randomBoolean());

		newCPInstance.setDeliverySubscriptionLength(RandomTestUtil.nextInt());

		newCPInstance.setDeliverySubscriptionType(
			RandomTestUtil.randomString());

		newCPInstance.setDeliverySubscriptionTypeSettings(
			RandomTestUtil.randomString());

		newCPInstance.setDeliveryMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		newCPInstance.setUnspsc(RandomTestUtil.randomString());

		newCPInstance.setDiscontinued(RandomTestUtil.randomBoolean());

		newCPInstance.setDiscontinuedDate(RandomTestUtil.nextDate());

		newCPInstance.setReplacementCPInstanceUuid(
			RandomTestUtil.randomString());

		newCPInstance.setReplacementCProductId(RandomTestUtil.nextLong());

		newCPInstance.setStatus(RandomTestUtil.nextInt());

		newCPInstance.setStatusByUserId(RandomTestUtil.nextLong());

		newCPInstance.setStatusByUserName(RandomTestUtil.randomString());

		newCPInstance.setStatusDate(RandomTestUtil.nextDate());

		_cpInstances.add(_persistence.update(newCPInstance));

		CPInstance existingCPInstance = _persistence.findByPrimaryKey(
			newCPInstance.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstance.getMvccVersion(),
			newCPInstance.getMvccVersion());
		Assert.assertEquals(
			existingCPInstance.getCtCollectionId(),
			newCPInstance.getCtCollectionId());
		Assert.assertEquals(
			existingCPInstance.getUuid(), newCPInstance.getUuid());
		Assert.assertEquals(
			existingCPInstance.getExternalReferenceCode(),
			newCPInstance.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPInstance.getCPInstanceId(),
			newCPInstance.getCPInstanceId());
		Assert.assertEquals(
			existingCPInstance.getGroupId(), newCPInstance.getGroupId());
		Assert.assertEquals(
			existingCPInstance.getCompanyId(), newCPInstance.getCompanyId());
		Assert.assertEquals(
			existingCPInstance.getUserId(), newCPInstance.getUserId());
		Assert.assertEquals(
			existingCPInstance.getUserName(), newCPInstance.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getCreateDate()),
			Time.getShortTimestamp(newCPInstance.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getModifiedDate()),
			Time.getShortTimestamp(newCPInstance.getModifiedDate()));
		Assert.assertEquals(
			existingCPInstance.getCPDefinitionId(),
			newCPInstance.getCPDefinitionId());
		Assert.assertEquals(
			existingCPInstance.getCPInstanceUuid(),
			newCPInstance.getCPInstanceUuid());
		Assert.assertEquals(
			existingCPInstance.getSku(), newCPInstance.getSku());
		Assert.assertEquals(
			existingCPInstance.getGtin(), newCPInstance.getGtin());
		Assert.assertEquals(
			existingCPInstance.getManufacturerPartNumber(),
			newCPInstance.getManufacturerPartNumber());
		Assert.assertEquals(
			existingCPInstance.isPurchasable(), newCPInstance.isPurchasable());
		AssertUtils.assertEquals(
			existingCPInstance.getWidth(), newCPInstance.getWidth());
		AssertUtils.assertEquals(
			existingCPInstance.getHeight(), newCPInstance.getHeight());
		AssertUtils.assertEquals(
			existingCPInstance.getDepth(), newCPInstance.getDepth());
		AssertUtils.assertEquals(
			existingCPInstance.getWeight(), newCPInstance.getWeight());
		Assert.assertEquals(
			existingCPInstance.getPrice(), newCPInstance.getPrice());
		Assert.assertEquals(
			existingCPInstance.getPromoPrice(), newCPInstance.getPromoPrice());
		Assert.assertEquals(
			existingCPInstance.getCost(), newCPInstance.getCost());
		Assert.assertEquals(
			existingCPInstance.isPublished(), newCPInstance.isPublished());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getDisplayDate()),
			Time.getShortTimestamp(newCPInstance.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getExpirationDate()),
			Time.getShortTimestamp(newCPInstance.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getLastPublishDate()),
			Time.getShortTimestamp(newCPInstance.getLastPublishDate()));
		Assert.assertEquals(
			existingCPInstance.isOverrideSubscriptionInfo(),
			newCPInstance.isOverrideSubscriptionInfo());
		Assert.assertEquals(
			existingCPInstance.isSubscriptionEnabled(),
			newCPInstance.isSubscriptionEnabled());
		Assert.assertEquals(
			existingCPInstance.getSubscriptionLength(),
			newCPInstance.getSubscriptionLength());
		Assert.assertEquals(
			existingCPInstance.getSubscriptionType(),
			newCPInstance.getSubscriptionType());
		Assert.assertEquals(
			existingCPInstance.getSubscriptionTypeSettings(),
			newCPInstance.getSubscriptionTypeSettings());
		Assert.assertEquals(
			existingCPInstance.getMaxSubscriptionCycles(),
			newCPInstance.getMaxSubscriptionCycles());
		Assert.assertEquals(
			existingCPInstance.isDeliverySubscriptionEnabled(),
			newCPInstance.isDeliverySubscriptionEnabled());
		Assert.assertEquals(
			existingCPInstance.getDeliverySubscriptionLength(),
			newCPInstance.getDeliverySubscriptionLength());
		Assert.assertEquals(
			existingCPInstance.getDeliverySubscriptionType(),
			newCPInstance.getDeliverySubscriptionType());
		Assert.assertEquals(
			existingCPInstance.getDeliverySubscriptionTypeSettings(),
			newCPInstance.getDeliverySubscriptionTypeSettings());
		Assert.assertEquals(
			existingCPInstance.getDeliveryMaxSubscriptionCycles(),
			newCPInstance.getDeliveryMaxSubscriptionCycles());
		Assert.assertEquals(
			existingCPInstance.getUnspsc(), newCPInstance.getUnspsc());
		Assert.assertEquals(
			existingCPInstance.isDiscontinued(),
			newCPInstance.isDiscontinued());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getDiscontinuedDate()),
			Time.getShortTimestamp(newCPInstance.getDiscontinuedDate()));
		Assert.assertEquals(
			existingCPInstance.getReplacementCPInstanceUuid(),
			newCPInstance.getReplacementCPInstanceUuid());
		Assert.assertEquals(
			existingCPInstance.getReplacementCProductId(),
			newCPInstance.getReplacementCProductId());
		Assert.assertEquals(
			existingCPInstance.getStatus(), newCPInstance.getStatus());
		Assert.assertEquals(
			existingCPInstance.getStatusByUserId(),
			newCPInstance.getStatusByUserId());
		Assert.assertEquals(
			existingCPInstance.getStatusByUserName(),
			newCPInstance.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPInstance.getStatusDate()),
			Time.getShortTimestamp(newCPInstance.getStatusDate()));
	}

	@Test(expected = DuplicateCPInstanceExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPInstance cpInstance = addCPInstance();

		CPInstance newCPInstance = addCPInstance();

		newCPInstance.setCompanyId(cpInstance.getCompanyId());

		newCPInstance = _persistence.update(newCPInstance);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPInstance);

		newCPInstance.setExternalReferenceCode(
			cpInstance.getExternalReferenceCode());

		_persistence.update(newCPInstance);
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
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testCountByCPInstanceUuid() throws Exception {
		_persistence.countByCPInstanceUuid("");

		_persistence.countByCPInstanceUuid("null");

		_persistence.countByCPInstanceUuid((String)null);
	}

	@Test
	public void testCountByG_ST() throws Exception {
		_persistence.countByG_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_ST(0L, 0);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(RandomTestUtil.nextLong(), "");

		_persistence.countByC_S(0L, "null");

		_persistence.countByC_S(0L, (String)null);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByCPDI_S() throws Exception {
		_persistence.countByCPDI_S(RandomTestUtil.nextLong(), "");

		_persistence.countByCPDI_S(0L, "null");

		_persistence.countByCPDI_S(0L, (String)null);
	}

	@Test
	public void testCountByC_ST() throws Exception {
		_persistence.countByC_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_ST(0L, 0);
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByC_LtD_S() throws Exception {
		_persistence.countByC_LtD_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextInt());

		_persistence.countByC_LtD_S(0L, RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByR_R_S() throws Exception {
		_persistence.countByR_R_S(
			"", RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByR_R_S("null", 0L, 0);

		_persistence.countByR_R_S((String)null, 0L, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPInstance newCPInstance = addCPInstance();

		CPInstance existingCPInstance = _persistence.findByPrimaryKey(
			newCPInstance.getPrimaryKey());

		Assert.assertEquals(existingCPInstance, newCPInstance);
	}

	@Test(expected = NoSuchCPInstanceException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPInstance> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPInstance", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "CPInstanceId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "CPDefinitionId",
			true, "CPInstanceUuid", true, "sku", true, "gtin", true,
			"manufacturerPartNumber", true, "purchasable", true, "width", true,
			"height", true, "depth", true, "weight", true, "price", true,
			"promoPrice", true, "cost", true, "published", true, "displayDate",
			true, "expirationDate", true, "lastPublishDate", true,
			"overrideSubscriptionInfo", true, "subscriptionEnabled", true,
			"subscriptionLength", true, "subscriptionType", true,
			"maxSubscriptionCycles", true, "deliverySubscriptionEnabled", true,
			"deliverySubscriptionLength", true, "deliverySubscriptionType",
			true, "deliverySubscriptionTypeSettings", true,
			"deliveryMaxSubscriptionCycles", true, "unspsc", true,
			"discontinued", true, "discontinuedDate", true,
			"replacementCPInstanceUuid", true, "replacementCProductId", true,
			"status", true, "statusByUserId", true, "statusByUserName", true,
			"statusDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPInstance newCPInstance = addCPInstance();

		CPInstance existingCPInstance = _persistence.fetchByPrimaryKey(
			newCPInstance.getPrimaryKey());

		Assert.assertEquals(existingCPInstance, newCPInstance);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstance missingCPInstance = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPInstance);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPInstance newCPInstance1 = addCPInstance();
		CPInstance newCPInstance2 = addCPInstance();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstance1.getPrimaryKey());
		primaryKeys.add(newCPInstance2.getPrimaryKey());

		Map<Serializable, CPInstance> cpInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpInstances.size());
		Assert.assertEquals(
			newCPInstance1, cpInstances.get(newCPInstance1.getPrimaryKey()));
		Assert.assertEquals(
			newCPInstance2, cpInstances.get(newCPInstance2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPInstance> cpInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpInstances.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPInstance newCPInstance = addCPInstance();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstance.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPInstance> cpInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpInstances.size());
		Assert.assertEquals(
			newCPInstance, cpInstances.get(newCPInstance.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPInstance> cpInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpInstances.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPInstance newCPInstance = addCPInstance();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstance.getPrimaryKey());

		Map<Serializable, CPInstance> cpInstances =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpInstances.size());
		Assert.assertEquals(
			newCPInstance, cpInstances.get(newCPInstance.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPInstance newCPInstance = addCPInstance();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPInstance.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPInstance cpInstance) {
		Assert.assertEquals(
			cpInstance.getUuid(),
			ReflectionTestUtil.invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpInstance.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpInstance.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			cpInstance.getCPInstanceUuid(),
			ReflectionTestUtil.invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPInstanceUuid"));

		Assert.assertEquals(
			Long.valueOf(cpInstance.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			cpInstance.getSku(),
			ReflectionTestUtil.invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "sku"));

		Assert.assertEquals(
			cpInstance.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpInstance.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstance, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPInstance addCPInstance() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstance cpInstance = _persistence.create(pk);

		cpInstance.setMvccVersion(RandomTestUtil.nextLong());

		cpInstance.setCtCollectionId(RandomTestUtil.nextLong());

		cpInstance.setUuid(RandomTestUtil.randomString());

		cpInstance.setExternalReferenceCode(RandomTestUtil.randomString());

		cpInstance.setGroupId(RandomTestUtil.nextLong());

		cpInstance.setCompanyId(RandomTestUtil.nextLong());

		cpInstance.setUserId(RandomTestUtil.nextLong());

		cpInstance.setUserName(RandomTestUtil.randomString());

		cpInstance.setCreateDate(RandomTestUtil.nextDate());

		cpInstance.setModifiedDate(RandomTestUtil.nextDate());

		cpInstance.setCPDefinitionId(RandomTestUtil.nextLong());

		cpInstance.setCPInstanceUuid(RandomTestUtil.randomString());

		cpInstance.setSku(RandomTestUtil.randomString());

		cpInstance.setGtin(RandomTestUtil.randomString());

		cpInstance.setManufacturerPartNumber(RandomTestUtil.randomString());

		cpInstance.setPurchasable(RandomTestUtil.randomBoolean());

		cpInstance.setWidth(RandomTestUtil.nextDouble());

		cpInstance.setHeight(RandomTestUtil.nextDouble());

		cpInstance.setDepth(RandomTestUtil.nextDouble());

		cpInstance.setWeight(RandomTestUtil.nextDouble());

		cpInstance.setPrice(new BigDecimal(RandomTestUtil.nextDouble()));

		cpInstance.setPromoPrice(new BigDecimal(RandomTestUtil.nextDouble()));

		cpInstance.setCost(new BigDecimal(RandomTestUtil.nextDouble()));

		cpInstance.setPublished(RandomTestUtil.randomBoolean());

		cpInstance.setDisplayDate(RandomTestUtil.nextDate());

		cpInstance.setExpirationDate(RandomTestUtil.nextDate());

		cpInstance.setLastPublishDate(RandomTestUtil.nextDate());

		cpInstance.setOverrideSubscriptionInfo(RandomTestUtil.randomBoolean());

		cpInstance.setSubscriptionEnabled(RandomTestUtil.randomBoolean());

		cpInstance.setSubscriptionLength(RandomTestUtil.nextInt());

		cpInstance.setSubscriptionType(RandomTestUtil.randomString());

		cpInstance.setSubscriptionTypeSettings(RandomTestUtil.randomString());

		cpInstance.setMaxSubscriptionCycles(RandomTestUtil.nextLong());

		cpInstance.setDeliverySubscriptionEnabled(
			RandomTestUtil.randomBoolean());

		cpInstance.setDeliverySubscriptionLength(RandomTestUtil.nextInt());

		cpInstance.setDeliverySubscriptionType(RandomTestUtil.randomString());

		cpInstance.setDeliverySubscriptionTypeSettings(
			RandomTestUtil.randomString());

		cpInstance.setDeliveryMaxSubscriptionCycles(RandomTestUtil.nextLong());

		cpInstance.setUnspsc(RandomTestUtil.randomString());

		cpInstance.setDiscontinued(RandomTestUtil.randomBoolean());

		cpInstance.setDiscontinuedDate(RandomTestUtil.nextDate());

		cpInstance.setReplacementCPInstanceUuid(RandomTestUtil.randomString());

		cpInstance.setReplacementCProductId(RandomTestUtil.nextLong());

		cpInstance.setStatus(RandomTestUtil.nextInt());

		cpInstance.setStatusByUserId(RandomTestUtil.nextLong());

		cpInstance.setStatusByUserName(RandomTestUtil.randomString());

		cpInstance.setStatusDate(RandomTestUtil.nextDate());

		_cpInstances.add(_persistence.update(cpInstance));

		return cpInstance;
	}

	private List<CPInstance> _cpInstances = new ArrayList<CPInstance>();
	private CPInstancePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}