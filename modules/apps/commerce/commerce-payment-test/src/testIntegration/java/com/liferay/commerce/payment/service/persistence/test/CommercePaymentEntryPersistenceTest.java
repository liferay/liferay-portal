/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.payment.exception.DuplicateCommercePaymentEntryExternalReferenceCodeException;
import com.liferay.commerce.payment.exception.NoSuchPaymentEntryException;
import com.liferay.commerce.payment.model.CommercePaymentEntry;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryUtil;
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
public class CommercePaymentEntryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.payment.service"));

	@Before
	public void setUp() {
		_persistence = CommercePaymentEntryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePaymentEntry> iterator =
			_commercePaymentEntries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntry commercePaymentEntry = _persistence.create(pk);

		Assert.assertNotNull(commercePaymentEntry);

		Assert.assertEquals(commercePaymentEntry.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		_persistence.remove(newCommercePaymentEntry);

		CommercePaymentEntry existingCommercePaymentEntry =
			_persistence.fetchByPrimaryKey(
				newCommercePaymentEntry.getPrimaryKey());

		Assert.assertNull(existingCommercePaymentEntry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePaymentEntry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntry newCommercePaymentEntry = _persistence.create(pk);

		newCommercePaymentEntry.setMvccVersion(RandomTestUtil.nextLong());

		newCommercePaymentEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommercePaymentEntry.setCompanyId(RandomTestUtil.nextLong());

		newCommercePaymentEntry.setUserId(RandomTestUtil.nextLong());

		newCommercePaymentEntry.setUserName(RandomTestUtil.randomString());

		newCommercePaymentEntry.setCreateDate(RandomTestUtil.nextDate());

		newCommercePaymentEntry.setModifiedDate(RandomTestUtil.nextDate());

		newCommercePaymentEntry.setClassNameId(RandomTestUtil.nextLong());

		newCommercePaymentEntry.setClassPK(RandomTestUtil.nextLong());

		newCommercePaymentEntry.setCommerceChannelId(RandomTestUtil.nextLong());

		newCommercePaymentEntry.setAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommercePaymentEntry.setCallbackURL(RandomTestUtil.randomString());

		newCommercePaymentEntry.setCancelURL(RandomTestUtil.randomString());

		newCommercePaymentEntry.setCurrencyCode(RandomTestUtil.randomString());

		newCommercePaymentEntry.setErrorMessages(RandomTestUtil.randomString());

		newCommercePaymentEntry.setLanguageId(RandomTestUtil.randomString());

		newCommercePaymentEntry.setNote(RandomTestUtil.randomString());

		newCommercePaymentEntry.setPayload(RandomTestUtil.randomString());

		newCommercePaymentEntry.setPaymentIntegrationKey(
			RandomTestUtil.randomString());

		newCommercePaymentEntry.setPaymentIntegrationType(
			RandomTestUtil.nextInt());

		newCommercePaymentEntry.setPaymentStatus(RandomTestUtil.nextInt());

		newCommercePaymentEntry.setReasonKey(RandomTestUtil.randomString());

		newCommercePaymentEntry.setReasonName(RandomTestUtil.randomString());

		newCommercePaymentEntry.setRedirectURL(RandomTestUtil.randomString());

		newCommercePaymentEntry.setTransactionCode(
			RandomTestUtil.randomString());

		newCommercePaymentEntry.setType(RandomTestUtil.nextInt());

		_commercePaymentEntries.add(
			_persistence.update(newCommercePaymentEntry));

		CommercePaymentEntry existingCommercePaymentEntry =
			_persistence.findByPrimaryKey(
				newCommercePaymentEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentEntry.getMvccVersion(),
			newCommercePaymentEntry.getMvccVersion());
		Assert.assertEquals(
			existingCommercePaymentEntry.getExternalReferenceCode(),
			newCommercePaymentEntry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommercePaymentEntry.getCommercePaymentEntryId(),
			newCommercePaymentEntry.getCommercePaymentEntryId());
		Assert.assertEquals(
			existingCommercePaymentEntry.getCompanyId(),
			newCommercePaymentEntry.getCompanyId());
		Assert.assertEquals(
			existingCommercePaymentEntry.getUserId(),
			newCommercePaymentEntry.getUserId());
		Assert.assertEquals(
			existingCommercePaymentEntry.getUserName(),
			newCommercePaymentEntry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentEntry.getCreateDate()),
			Time.getShortTimestamp(newCommercePaymentEntry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentEntry.getModifiedDate()),
			Time.getShortTimestamp(newCommercePaymentEntry.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePaymentEntry.getClassNameId(),
			newCommercePaymentEntry.getClassNameId());
		Assert.assertEquals(
			existingCommercePaymentEntry.getClassPK(),
			newCommercePaymentEntry.getClassPK());
		Assert.assertEquals(
			existingCommercePaymentEntry.getCommerceChannelId(),
			newCommercePaymentEntry.getCommerceChannelId());
		Assert.assertEquals(
			existingCommercePaymentEntry.getAmount(),
			newCommercePaymentEntry.getAmount());
		Assert.assertEquals(
			existingCommercePaymentEntry.getCallbackURL(),
			newCommercePaymentEntry.getCallbackURL());
		Assert.assertEquals(
			existingCommercePaymentEntry.getCancelURL(),
			newCommercePaymentEntry.getCancelURL());
		Assert.assertEquals(
			existingCommercePaymentEntry.getCurrencyCode(),
			newCommercePaymentEntry.getCurrencyCode());
		Assert.assertEquals(
			existingCommercePaymentEntry.getErrorMessages(),
			newCommercePaymentEntry.getErrorMessages());
		Assert.assertEquals(
			existingCommercePaymentEntry.getLanguageId(),
			newCommercePaymentEntry.getLanguageId());
		Assert.assertEquals(
			existingCommercePaymentEntry.getNote(),
			newCommercePaymentEntry.getNote());
		Assert.assertEquals(
			existingCommercePaymentEntry.getPayload(),
			newCommercePaymentEntry.getPayload());
		Assert.assertEquals(
			existingCommercePaymentEntry.getPaymentIntegrationKey(),
			newCommercePaymentEntry.getPaymentIntegrationKey());
		Assert.assertEquals(
			existingCommercePaymentEntry.getPaymentIntegrationType(),
			newCommercePaymentEntry.getPaymentIntegrationType());
		Assert.assertEquals(
			existingCommercePaymentEntry.getPaymentStatus(),
			newCommercePaymentEntry.getPaymentStatus());
		Assert.assertEquals(
			existingCommercePaymentEntry.getReasonKey(),
			newCommercePaymentEntry.getReasonKey());
		Assert.assertEquals(
			existingCommercePaymentEntry.getReasonName(),
			newCommercePaymentEntry.getReasonName());
		Assert.assertEquals(
			existingCommercePaymentEntry.getRedirectURL(),
			newCommercePaymentEntry.getRedirectURL());
		Assert.assertEquals(
			existingCommercePaymentEntry.getTransactionCode(),
			newCommercePaymentEntry.getTransactionCode());
		Assert.assertEquals(
			existingCommercePaymentEntry.getType(),
			newCommercePaymentEntry.getType());
	}

	@Test(
		expected = DuplicateCommercePaymentEntryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommercePaymentEntry commercePaymentEntry = addCommercePaymentEntry();

		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		newCommercePaymentEntry.setCompanyId(
			commercePaymentEntry.getCompanyId());

		newCommercePaymentEntry = _persistence.update(newCommercePaymentEntry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommercePaymentEntry);

		newCommercePaymentEntry.setExternalReferenceCode(
			commercePaymentEntry.getExternalReferenceCode());

		_persistence.update(newCommercePaymentEntry);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testCountByC_C_C_T() throws Exception {
		_persistence.countByC_C_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_C_C_T(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByC_C_C_P_T() throws Exception {
		_persistence.countByC_C_C_P_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByC_C_C_P_T(0L, 0L, 0L, 0, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		CommercePaymentEntry existingCommercePaymentEntry =
			_persistence.findByPrimaryKey(
				newCommercePaymentEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentEntry, newCommercePaymentEntry);
	}

	@Test(expected = NoSuchPaymentEntryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePaymentEntry> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommercePaymentEntry", "mvccVersion", true,
			"externalReferenceCode", true, "commercePaymentEntryId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "classPK", true,
			"commerceChannelId", true, "amount", true, "currencyCode", true,
			"languageId", true, "paymentIntegrationKey", true,
			"paymentIntegrationType", true, "paymentStatus", true, "reasonKey",
			true, "reasonName", true, "transactionCode", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		CommercePaymentEntry existingCommercePaymentEntry =
			_persistence.fetchByPrimaryKey(
				newCommercePaymentEntry.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentEntry, newCommercePaymentEntry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntry missingCommercePaymentEntry =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePaymentEntry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePaymentEntry newCommercePaymentEntry1 =
			addCommercePaymentEntry();
		CommercePaymentEntry newCommercePaymentEntry2 =
			addCommercePaymentEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentEntry1.getPrimaryKey());
		primaryKeys.add(newCommercePaymentEntry2.getPrimaryKey());

		Map<Serializable, CommercePaymentEntry> commercePaymentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commercePaymentEntries.size());
		Assert.assertEquals(
			newCommercePaymentEntry1,
			commercePaymentEntries.get(
				newCommercePaymentEntry1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePaymentEntry2,
			commercePaymentEntries.get(
				newCommercePaymentEntry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePaymentEntry> commercePaymentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePaymentEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentEntry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePaymentEntry> commercePaymentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePaymentEntries.size());
		Assert.assertEquals(
			newCommercePaymentEntry,
			commercePaymentEntries.get(
				newCommercePaymentEntry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePaymentEntry> commercePaymentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePaymentEntries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentEntry.getPrimaryKey());

		Map<Serializable, CommercePaymentEntry> commercePaymentEntries =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePaymentEntries.size());
		Assert.assertEquals(
			newCommercePaymentEntry,
			commercePaymentEntries.get(
				newCommercePaymentEntry.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePaymentEntry newCommercePaymentEntry =
			addCommercePaymentEntry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePaymentEntry.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePaymentEntry commercePaymentEntry) {

		Assert.assertEquals(
			commercePaymentEntry.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commercePaymentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commercePaymentEntry.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commercePaymentEntry, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommercePaymentEntry addCommercePaymentEntry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntry commercePaymentEntry = _persistence.create(pk);

		commercePaymentEntry.setMvccVersion(RandomTestUtil.nextLong());

		commercePaymentEntry.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commercePaymentEntry.setCompanyId(RandomTestUtil.nextLong());

		commercePaymentEntry.setUserId(RandomTestUtil.nextLong());

		commercePaymentEntry.setUserName(RandomTestUtil.randomString());

		commercePaymentEntry.setCreateDate(RandomTestUtil.nextDate());

		commercePaymentEntry.setModifiedDate(RandomTestUtil.nextDate());

		commercePaymentEntry.setClassNameId(RandomTestUtil.nextLong());

		commercePaymentEntry.setClassPK(RandomTestUtil.nextLong());

		commercePaymentEntry.setCommerceChannelId(RandomTestUtil.nextLong());

		commercePaymentEntry.setAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commercePaymentEntry.setCallbackURL(RandomTestUtil.randomString());

		commercePaymentEntry.setCancelURL(RandomTestUtil.randomString());

		commercePaymentEntry.setCurrencyCode(RandomTestUtil.randomString());

		commercePaymentEntry.setErrorMessages(RandomTestUtil.randomString());

		commercePaymentEntry.setLanguageId(RandomTestUtil.randomString());

		commercePaymentEntry.setNote(RandomTestUtil.randomString());

		commercePaymentEntry.setPayload(RandomTestUtil.randomString());

		commercePaymentEntry.setPaymentIntegrationKey(
			RandomTestUtil.randomString());

		commercePaymentEntry.setPaymentIntegrationType(
			RandomTestUtil.nextInt());

		commercePaymentEntry.setPaymentStatus(RandomTestUtil.nextInt());

		commercePaymentEntry.setReasonKey(RandomTestUtil.randomString());

		commercePaymentEntry.setReasonName(RandomTestUtil.randomString());

		commercePaymentEntry.setRedirectURL(RandomTestUtil.randomString());

		commercePaymentEntry.setTransactionCode(RandomTestUtil.randomString());

		commercePaymentEntry.setType(RandomTestUtil.nextInt());

		_commercePaymentEntries.add(_persistence.update(commercePaymentEntry));

		return commercePaymentEntry;
	}

	private List<CommercePaymentEntry> _commercePaymentEntries =
		new ArrayList<CommercePaymentEntry>();
	private CommercePaymentEntryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}