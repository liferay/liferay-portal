/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionException;
import com.liferay.commerce.product.model.CPDefinition;
import com.liferay.commerce.product.service.persistence.CPDefinitionPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CPDefinitionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinition> iterator = _cpDefinitions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinition cpDefinition = _persistence.create(pk);

		Assert.assertNotNull(cpDefinition);

		Assert.assertEquals(cpDefinition.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinition newCPDefinition = addCPDefinition();

		_persistence.remove(newCPDefinition);

		CPDefinition existingCPDefinition = _persistence.fetchByPrimaryKey(
			newCPDefinition.getPrimaryKey());

		Assert.assertNull(existingCPDefinition);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinition();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinition newCPDefinition = _persistence.create(pk);

		newCPDefinition.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinition.setCtCollectionId(RandomTestUtil.nextLong());

		newCPDefinition.setUuid(RandomTestUtil.randomString());

		newCPDefinition.setDefaultLanguageId(RandomTestUtil.randomString());

		newCPDefinition.setGroupId(RandomTestUtil.nextLong());

		newCPDefinition.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinition.setUserId(RandomTestUtil.nextLong());

		newCPDefinition.setUserName(RandomTestUtil.randomString());

		newCPDefinition.setCreateDate(RandomTestUtil.nextDate());

		newCPDefinition.setModifiedDate(RandomTestUtil.nextDate());

		newCPDefinition.setCProductId(RandomTestUtil.nextLong());

		newCPDefinition.setCPTaxCategoryId(RandomTestUtil.nextLong());

		newCPDefinition.setProductTypeName(RandomTestUtil.randomString());

		newCPDefinition.setAvailableIndividually(
			RandomTestUtil.randomBoolean());

		newCPDefinition.setIgnoreSKUCombinations(
			RandomTestUtil.randomBoolean());

		newCPDefinition.setShippable(RandomTestUtil.randomBoolean());

		newCPDefinition.setFreeShipping(RandomTestUtil.randomBoolean());

		newCPDefinition.setShipSeparately(RandomTestUtil.randomBoolean());

		newCPDefinition.setShippingExtraPrice(RandomTestUtil.nextDouble());

		newCPDefinition.setWidth(RandomTestUtil.nextDouble());

		newCPDefinition.setHeight(RandomTestUtil.nextDouble());

		newCPDefinition.setDepth(RandomTestUtil.nextDouble());

		newCPDefinition.setWeight(RandomTestUtil.nextDouble());

		newCPDefinition.setTaxExempt(RandomTestUtil.randomBoolean());

		newCPDefinition.setTelcoOrElectronics(RandomTestUtil.randomBoolean());

		newCPDefinition.setDDMStructureKey(RandomTestUtil.randomString());

		newCPDefinition.setPublished(RandomTestUtil.randomBoolean());

		newCPDefinition.setDisplayDate(RandomTestUtil.nextDate());

		newCPDefinition.setExpirationDate(RandomTestUtil.nextDate());

		newCPDefinition.setLastPublishDate(RandomTestUtil.nextDate());

		newCPDefinition.setSubscriptionEnabled(RandomTestUtil.randomBoolean());

		newCPDefinition.setSubscriptionLength(RandomTestUtil.nextInt());

		newCPDefinition.setSubscriptionType(RandomTestUtil.randomString());

		newCPDefinition.setSubscriptionTypeSettings(
			RandomTestUtil.randomString());

		newCPDefinition.setMaxSubscriptionCycles(RandomTestUtil.nextLong());

		newCPDefinition.setDeliverySubscriptionEnabled(
			RandomTestUtil.randomBoolean());

		newCPDefinition.setDeliverySubscriptionLength(RandomTestUtil.nextInt());

		newCPDefinition.setDeliverySubscriptionType(
			RandomTestUtil.randomString());

		newCPDefinition.setDeliverySubscriptionTypeSettings(
			RandomTestUtil.randomString());

		newCPDefinition.setDeliveryMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		newCPDefinition.setAccountGroupFilterEnabled(
			RandomTestUtil.randomBoolean());

		newCPDefinition.setChannelFilterEnabled(RandomTestUtil.randomBoolean());

		newCPDefinition.setVersion(RandomTestUtil.nextInt());

		newCPDefinition.setStatus(RandomTestUtil.nextInt());

		newCPDefinition.setStatusByUserId(RandomTestUtil.nextLong());

		newCPDefinition.setStatusByUserName(RandomTestUtil.randomString());

		newCPDefinition.setStatusDate(RandomTestUtil.nextDate());

		_cpDefinitions.add(_persistence.update(newCPDefinition));

		CPDefinition existingCPDefinition = _persistence.findByPrimaryKey(
			newCPDefinition.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinition.getMvccVersion(),
			newCPDefinition.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinition.getCtCollectionId(),
			newCPDefinition.getCtCollectionId());
		Assert.assertEquals(
			existingCPDefinition.getUuid(), newCPDefinition.getUuid());
		Assert.assertEquals(
			existingCPDefinition.getDefaultLanguageId(),
			newCPDefinition.getDefaultLanguageId());
		Assert.assertEquals(
			existingCPDefinition.getCPDefinitionId(),
			newCPDefinition.getCPDefinitionId());
		Assert.assertEquals(
			existingCPDefinition.getGroupId(), newCPDefinition.getGroupId());
		Assert.assertEquals(
			existingCPDefinition.getCompanyId(),
			newCPDefinition.getCompanyId());
		Assert.assertEquals(
			existingCPDefinition.getUserId(), newCPDefinition.getUserId());
		Assert.assertEquals(
			existingCPDefinition.getUserName(), newCPDefinition.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDefinition.getCreateDate()),
			Time.getShortTimestamp(newCPDefinition.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDefinition.getModifiedDate()),
			Time.getShortTimestamp(newCPDefinition.getModifiedDate()));
		Assert.assertEquals(
			existingCPDefinition.getCProductId(),
			newCPDefinition.getCProductId());
		Assert.assertEquals(
			existingCPDefinition.getCPTaxCategoryId(),
			newCPDefinition.getCPTaxCategoryId());
		Assert.assertEquals(
			existingCPDefinition.getProductTypeName(),
			newCPDefinition.getProductTypeName());
		Assert.assertEquals(
			existingCPDefinition.isAvailableIndividually(),
			newCPDefinition.isAvailableIndividually());
		Assert.assertEquals(
			existingCPDefinition.isIgnoreSKUCombinations(),
			newCPDefinition.isIgnoreSKUCombinations());
		Assert.assertEquals(
			existingCPDefinition.isShippable(), newCPDefinition.isShippable());
		Assert.assertEquals(
			existingCPDefinition.isFreeShipping(),
			newCPDefinition.isFreeShipping());
		Assert.assertEquals(
			existingCPDefinition.isShipSeparately(),
			newCPDefinition.isShipSeparately());
		AssertUtils.assertEquals(
			existingCPDefinition.getShippingExtraPrice(),
			newCPDefinition.getShippingExtraPrice());
		AssertUtils.assertEquals(
			existingCPDefinition.getWidth(), newCPDefinition.getWidth());
		AssertUtils.assertEquals(
			existingCPDefinition.getHeight(), newCPDefinition.getHeight());
		AssertUtils.assertEquals(
			existingCPDefinition.getDepth(), newCPDefinition.getDepth());
		AssertUtils.assertEquals(
			existingCPDefinition.getWeight(), newCPDefinition.getWeight());
		Assert.assertEquals(
			existingCPDefinition.isTaxExempt(), newCPDefinition.isTaxExempt());
		Assert.assertEquals(
			existingCPDefinition.isTelcoOrElectronics(),
			newCPDefinition.isTelcoOrElectronics());
		Assert.assertEquals(
			existingCPDefinition.getDDMStructureKey(),
			newCPDefinition.getDDMStructureKey());
		Assert.assertEquals(
			existingCPDefinition.isPublished(), newCPDefinition.isPublished());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDefinition.getDisplayDate()),
			Time.getShortTimestamp(newCPDefinition.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDefinition.getExpirationDate()),
			Time.getShortTimestamp(newCPDefinition.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDefinition.getLastPublishDate()),
			Time.getShortTimestamp(newCPDefinition.getLastPublishDate()));
		Assert.assertEquals(
			existingCPDefinition.isSubscriptionEnabled(),
			newCPDefinition.isSubscriptionEnabled());
		Assert.assertEquals(
			existingCPDefinition.getSubscriptionLength(),
			newCPDefinition.getSubscriptionLength());
		Assert.assertEquals(
			existingCPDefinition.getSubscriptionType(),
			newCPDefinition.getSubscriptionType());
		Assert.assertEquals(
			existingCPDefinition.getSubscriptionTypeSettings(),
			newCPDefinition.getSubscriptionTypeSettings());
		Assert.assertEquals(
			existingCPDefinition.getMaxSubscriptionCycles(),
			newCPDefinition.getMaxSubscriptionCycles());
		Assert.assertEquals(
			existingCPDefinition.isDeliverySubscriptionEnabled(),
			newCPDefinition.isDeliverySubscriptionEnabled());
		Assert.assertEquals(
			existingCPDefinition.getDeliverySubscriptionLength(),
			newCPDefinition.getDeliverySubscriptionLength());
		Assert.assertEquals(
			existingCPDefinition.getDeliverySubscriptionType(),
			newCPDefinition.getDeliverySubscriptionType());
		Assert.assertEquals(
			existingCPDefinition.getDeliverySubscriptionTypeSettings(),
			newCPDefinition.getDeliverySubscriptionTypeSettings());
		Assert.assertEquals(
			existingCPDefinition.getDeliveryMaxSubscriptionCycles(),
			newCPDefinition.getDeliveryMaxSubscriptionCycles());
		Assert.assertEquals(
			existingCPDefinition.isAccountGroupFilterEnabled(),
			newCPDefinition.isAccountGroupFilterEnabled());
		Assert.assertEquals(
			existingCPDefinition.isChannelFilterEnabled(),
			newCPDefinition.isChannelFilterEnabled());
		Assert.assertEquals(
			existingCPDefinition.getVersion(), newCPDefinition.getVersion());
		Assert.assertEquals(
			existingCPDefinition.getStatus(), newCPDefinition.getStatus());
		Assert.assertEquals(
			existingCPDefinition.getStatusByUserId(),
			newCPDefinition.getStatusByUserId());
		Assert.assertEquals(
			existingCPDefinition.getStatusByUserName(),
			newCPDefinition.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPDefinition.getStatusDate()),
			Time.getShortTimestamp(newCPDefinition.getStatusDate()));
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
	public void testCountByCProductId() throws Exception {
		_persistence.countByCProductId(RandomTestUtil.nextLong());

		_persistence.countByCProductId(0L);
	}

	@Test
	public void testCountByCPTaxCategoryId() throws Exception {
		_persistence.countByCPTaxCategoryId(RandomTestUtil.nextLong());

		_persistence.countByCPTaxCategoryId(0L);
	}

	@Test
	public void testCountByG_SE() throws Exception {
		_persistence.countByG_SE(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_SE(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_S() throws Exception {
		_persistence.countByG_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_S(0L, 0);
	}

	@Test
	public void testCountByC_V() throws Exception {
		_persistence.countByC_V(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_V(0L, 0);
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
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinition newCPDefinition = addCPDefinition();

		CPDefinition existingCPDefinition = _persistence.findByPrimaryKey(
			newCPDefinition.getPrimaryKey());

		Assert.assertEquals(existingCPDefinition, newCPDefinition);
	}

	@Test(expected = NoSuchCPDefinitionException.class)
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

	protected OrderByComparator<CPDefinition> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPDefinition", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "defaultLanguageId", true, "CPDefinitionId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "CProductId", true,
			"CPTaxCategoryId", true, "productTypeName", true,
			"availableIndividually", true, "ignoreSKUCombinations", true,
			"shippable", true, "freeShipping", true, "shipSeparately", true,
			"shippingExtraPrice", true, "width", true, "height", true, "depth",
			true, "weight", true, "taxExempt", true, "telcoOrElectronics", true,
			"DDMStructureKey", true, "published", true, "displayDate", true,
			"expirationDate", true, "lastPublishDate", true,
			"subscriptionEnabled", true, "subscriptionLength", true,
			"subscriptionType", true, "maxSubscriptionCycles", true,
			"deliverySubscriptionEnabled", true, "deliverySubscriptionLength",
			true, "deliverySubscriptionType", true,
			"deliverySubscriptionTypeSettings", true,
			"deliveryMaxSubscriptionCycles", true, "accountGroupFilterEnabled",
			true, "channelFilterEnabled", true, "version", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinition newCPDefinition = addCPDefinition();

		CPDefinition existingCPDefinition = _persistence.fetchByPrimaryKey(
			newCPDefinition.getPrimaryKey());

		Assert.assertEquals(existingCPDefinition, newCPDefinition);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinition missingCPDefinition = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinition);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinition newCPDefinition1 = addCPDefinition();
		CPDefinition newCPDefinition2 = addCPDefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinition1.getPrimaryKey());
		primaryKeys.add(newCPDefinition2.getPrimaryKey());

		Map<Serializable, CPDefinition> cpDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpDefinitions.size());
		Assert.assertEquals(
			newCPDefinition1,
			cpDefinitions.get(newCPDefinition1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinition2,
			cpDefinitions.get(newCPDefinition2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinition> cpDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinition newCPDefinition = addCPDefinition();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinition.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinition> cpDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitions.size());
		Assert.assertEquals(
			newCPDefinition,
			cpDefinitions.get(newCPDefinition.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinition> cpDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinition newCPDefinition = addCPDefinition();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinition.getPrimaryKey());

		Map<Serializable, CPDefinition> cpDefinitions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitions.size());
		Assert.assertEquals(
			newCPDefinition,
			cpDefinitions.get(newCPDefinition.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinition newCPDefinition = addCPDefinition();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPDefinition.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPDefinition cpDefinition) {
		Assert.assertEquals(
			cpDefinition.getUuid(),
			ReflectionTestUtil.invoke(
				cpDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDefinition.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpDefinition.getCProductId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CProductId"));
		Assert.assertEquals(
			Integer.valueOf(cpDefinition.getVersion()),
			ReflectionTestUtil.<Integer>invoke(
				cpDefinition, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "version"));
	}

	protected CPDefinition addCPDefinition() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinition cpDefinition = _persistence.create(pk);

		cpDefinition.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinition.setCtCollectionId(RandomTestUtil.nextLong());

		cpDefinition.setUuid(RandomTestUtil.randomString());

		cpDefinition.setDefaultLanguageId(RandomTestUtil.randomString());

		cpDefinition.setGroupId(RandomTestUtil.nextLong());

		cpDefinition.setCompanyId(RandomTestUtil.nextLong());

		cpDefinition.setUserId(RandomTestUtil.nextLong());

		cpDefinition.setUserName(RandomTestUtil.randomString());

		cpDefinition.setCreateDate(RandomTestUtil.nextDate());

		cpDefinition.setModifiedDate(RandomTestUtil.nextDate());

		cpDefinition.setCProductId(RandomTestUtil.nextLong());

		cpDefinition.setCPTaxCategoryId(RandomTestUtil.nextLong());

		cpDefinition.setProductTypeName(RandomTestUtil.randomString());

		cpDefinition.setAvailableIndividually(RandomTestUtil.randomBoolean());

		cpDefinition.setIgnoreSKUCombinations(RandomTestUtil.randomBoolean());

		cpDefinition.setShippable(RandomTestUtil.randomBoolean());

		cpDefinition.setFreeShipping(RandomTestUtil.randomBoolean());

		cpDefinition.setShipSeparately(RandomTestUtil.randomBoolean());

		cpDefinition.setShippingExtraPrice(RandomTestUtil.nextDouble());

		cpDefinition.setWidth(RandomTestUtil.nextDouble());

		cpDefinition.setHeight(RandomTestUtil.nextDouble());

		cpDefinition.setDepth(RandomTestUtil.nextDouble());

		cpDefinition.setWeight(RandomTestUtil.nextDouble());

		cpDefinition.setTaxExempt(RandomTestUtil.randomBoolean());

		cpDefinition.setTelcoOrElectronics(RandomTestUtil.randomBoolean());

		cpDefinition.setDDMStructureKey(RandomTestUtil.randomString());

		cpDefinition.setPublished(RandomTestUtil.randomBoolean());

		cpDefinition.setDisplayDate(RandomTestUtil.nextDate());

		cpDefinition.setExpirationDate(RandomTestUtil.nextDate());

		cpDefinition.setLastPublishDate(RandomTestUtil.nextDate());

		cpDefinition.setSubscriptionEnabled(RandomTestUtil.randomBoolean());

		cpDefinition.setSubscriptionLength(RandomTestUtil.nextInt());

		cpDefinition.setSubscriptionType(RandomTestUtil.randomString());

		cpDefinition.setSubscriptionTypeSettings(RandomTestUtil.randomString());

		cpDefinition.setMaxSubscriptionCycles(RandomTestUtil.nextLong());

		cpDefinition.setDeliverySubscriptionEnabled(
			RandomTestUtil.randomBoolean());

		cpDefinition.setDeliverySubscriptionLength(RandomTestUtil.nextInt());

		cpDefinition.setDeliverySubscriptionType(RandomTestUtil.randomString());

		cpDefinition.setDeliverySubscriptionTypeSettings(
			RandomTestUtil.randomString());

		cpDefinition.setDeliveryMaxSubscriptionCycles(
			RandomTestUtil.nextLong());

		cpDefinition.setAccountGroupFilterEnabled(
			RandomTestUtil.randomBoolean());

		cpDefinition.setChannelFilterEnabled(RandomTestUtil.randomBoolean());

		cpDefinition.setVersion(RandomTestUtil.nextInt());

		cpDefinition.setStatus(RandomTestUtil.nextInt());

		cpDefinition.setStatusByUserId(RandomTestUtil.nextLong());

		cpDefinition.setStatusByUserName(RandomTestUtil.randomString());

		cpDefinition.setStatusDate(RandomTestUtil.nextDate());

		_cpDefinitions.add(_persistence.update(cpDefinition));

		return cpDefinition;
	}

	private List<CPDefinition> _cpDefinitions = new ArrayList<CPDefinition>();
	private CPDefinitionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}