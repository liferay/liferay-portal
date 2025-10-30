/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCommerceChannelExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchChannelException;
import com.liferay.commerce.product.model.CommerceChannel;
import com.liferay.commerce.product.service.persistence.CommerceChannelPersistence;
import com.liferay.commerce.product.service.persistence.CommerceChannelUtil;
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
public class CommerceChannelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CommerceChannelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceChannel> iterator = _commerceChannels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannel commerceChannel = _persistence.create(pk);

		Assert.assertNotNull(commerceChannel);

		Assert.assertEquals(commerceChannel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceChannel newCommerceChannel = addCommerceChannel();

		_persistence.remove(newCommerceChannel);

		CommerceChannel existingCommerceChannel =
			_persistence.fetchByPrimaryKey(newCommerceChannel.getPrimaryKey());

		Assert.assertNull(existingCommerceChannel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceChannel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannel newCommerceChannel = _persistence.create(pk);

		newCommerceChannel.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceChannel.setCtCollectionId(RandomTestUtil.nextLong());

		newCommerceChannel.setUuid(RandomTestUtil.randomString());

		newCommerceChannel.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceChannel.setCompanyId(RandomTestUtil.nextLong());

		newCommerceChannel.setUserId(RandomTestUtil.nextLong());

		newCommerceChannel.setUserName(RandomTestUtil.randomString());

		newCommerceChannel.setCreateDate(RandomTestUtil.nextDate());

		newCommerceChannel.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceChannel.setAccountEntryId(RandomTestUtil.nextLong());

		newCommerceChannel.setSiteGroupId(RandomTestUtil.nextLong());

		newCommerceChannel.setName(RandomTestUtil.randomString());

		newCommerceChannel.setType(RandomTestUtil.randomString());

		newCommerceChannel.setTypeSettings(RandomTestUtil.randomString());

		newCommerceChannel.setCommerceCurrencyCode(
			RandomTestUtil.randomString());

		newCommerceChannel.setPriceDisplayType(RandomTestUtil.randomString());

		newCommerceChannel.setDiscountsTargetNetPrice(
			RandomTestUtil.randomBoolean());

		_commerceChannels.add(_persistence.update(newCommerceChannel));

		CommerceChannel existingCommerceChannel = _persistence.findByPrimaryKey(
			newCommerceChannel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceChannel.getMvccVersion(),
			newCommerceChannel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceChannel.getCtCollectionId(),
			newCommerceChannel.getCtCollectionId());
		Assert.assertEquals(
			existingCommerceChannel.getUuid(), newCommerceChannel.getUuid());
		Assert.assertEquals(
			existingCommerceChannel.getExternalReferenceCode(),
			newCommerceChannel.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceChannel.getCommerceChannelId(),
			newCommerceChannel.getCommerceChannelId());
		Assert.assertEquals(
			existingCommerceChannel.getCompanyId(),
			newCommerceChannel.getCompanyId());
		Assert.assertEquals(
			existingCommerceChannel.getUserId(),
			newCommerceChannel.getUserId());
		Assert.assertEquals(
			existingCommerceChannel.getUserName(),
			newCommerceChannel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceChannel.getCreateDate()),
			Time.getShortTimestamp(newCommerceChannel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceChannel.getModifiedDate()),
			Time.getShortTimestamp(newCommerceChannel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceChannel.getAccountEntryId(),
			newCommerceChannel.getAccountEntryId());
		Assert.assertEquals(
			existingCommerceChannel.getSiteGroupId(),
			newCommerceChannel.getSiteGroupId());
		Assert.assertEquals(
			existingCommerceChannel.getName(), newCommerceChannel.getName());
		Assert.assertEquals(
			existingCommerceChannel.getType(), newCommerceChannel.getType());
		Assert.assertEquals(
			existingCommerceChannel.getTypeSettings(),
			newCommerceChannel.getTypeSettings());
		Assert.assertEquals(
			existingCommerceChannel.getCommerceCurrencyCode(),
			newCommerceChannel.getCommerceCurrencyCode());
		Assert.assertEquals(
			existingCommerceChannel.getPriceDisplayType(),
			newCommerceChannel.getPriceDisplayType());
		Assert.assertEquals(
			existingCommerceChannel.isDiscountsTargetNetPrice(),
			newCommerceChannel.isDiscountsTargetNetPrice());
	}

	@Test(
		expected = DuplicateCommerceChannelExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceChannel commerceChannel = addCommerceChannel();

		CommerceChannel newCommerceChannel = addCommerceChannel();

		newCommerceChannel.setCompanyId(commerceChannel.getCompanyId());

		newCommerceChannel = _persistence.update(newCommerceChannel);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceChannel);

		newCommerceChannel.setExternalReferenceCode(
			commerceChannel.getExternalReferenceCode());

		_persistence.update(newCommerceChannel);
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
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountBySiteGroupId() throws Exception {
		_persistence.countBySiteGroupId(RandomTestUtil.nextLong());

		_persistence.countBySiteGroupId(0L);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceChannel newCommerceChannel = addCommerceChannel();

		CommerceChannel existingCommerceChannel = _persistence.findByPrimaryKey(
			newCommerceChannel.getPrimaryKey());

		Assert.assertEquals(existingCommerceChannel, newCommerceChannel);
	}

	@Test(expected = NoSuchChannelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceChannel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceChannel", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "commerceChannelId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "accountEntryId", true,
			"siteGroupId", true, "name", true, "type", true, "typeSettings",
			true, "commerceCurrencyCode", true, "priceDisplayType", true,
			"discountsTargetNetPrice", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceChannel newCommerceChannel = addCommerceChannel();

		CommerceChannel existingCommerceChannel =
			_persistence.fetchByPrimaryKey(newCommerceChannel.getPrimaryKey());

		Assert.assertEquals(existingCommerceChannel, newCommerceChannel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannel missingCommerceChannel = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingCommerceChannel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceChannel newCommerceChannel1 = addCommerceChannel();
		CommerceChannel newCommerceChannel2 = addCommerceChannel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannel1.getPrimaryKey());
		primaryKeys.add(newCommerceChannel2.getPrimaryKey());

		Map<Serializable, CommerceChannel> commerceChannels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceChannels.size());
		Assert.assertEquals(
			newCommerceChannel1,
			commerceChannels.get(newCommerceChannel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceChannel2,
			commerceChannels.get(newCommerceChannel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceChannel> commerceChannels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceChannels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceChannel newCommerceChannel = addCommerceChannel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceChannel> commerceChannels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceChannels.size());
		Assert.assertEquals(
			newCommerceChannel,
			commerceChannels.get(newCommerceChannel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceChannel> commerceChannels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceChannels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceChannel newCommerceChannel = addCommerceChannel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannel.getPrimaryKey());

		Map<Serializable, CommerceChannel> commerceChannels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceChannels.size());
		Assert.assertEquals(
			newCommerceChannel,
			commerceChannels.get(newCommerceChannel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceChannel newCommerceChannel = addCommerceChannel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCommerceChannel.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceChannel commerceChannel) {
		Assert.assertEquals(
			Long.valueOf(commerceChannel.getSiteGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "siteGroupId"));

		Assert.assertEquals(
			commerceChannel.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceChannel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceChannel.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceChannel addCommerceChannel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannel commerceChannel = _persistence.create(pk);

		commerceChannel.setMvccVersion(RandomTestUtil.nextLong());

		commerceChannel.setCtCollectionId(RandomTestUtil.nextLong());

		commerceChannel.setUuid(RandomTestUtil.randomString());

		commerceChannel.setExternalReferenceCode(RandomTestUtil.randomString());

		commerceChannel.setCompanyId(RandomTestUtil.nextLong());

		commerceChannel.setUserId(RandomTestUtil.nextLong());

		commerceChannel.setUserName(RandomTestUtil.randomString());

		commerceChannel.setCreateDate(RandomTestUtil.nextDate());

		commerceChannel.setModifiedDate(RandomTestUtil.nextDate());

		commerceChannel.setAccountEntryId(RandomTestUtil.nextLong());

		commerceChannel.setSiteGroupId(RandomTestUtil.nextLong());

		commerceChannel.setName(RandomTestUtil.randomString());

		commerceChannel.setType(RandomTestUtil.randomString());

		commerceChannel.setTypeSettings(RandomTestUtil.randomString());

		commerceChannel.setCommerceCurrencyCode(RandomTestUtil.randomString());

		commerceChannel.setPriceDisplayType(RandomTestUtil.randomString());

		commerceChannel.setDiscountsTargetNetPrice(
			RandomTestUtil.randomBoolean());

		_commerceChannels.add(_persistence.update(commerceChannel));

		return commerceChannel;
	}

	private List<CommerceChannel> _commerceChannels =
		new ArrayList<CommerceChannel>();
	private CommerceChannelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}