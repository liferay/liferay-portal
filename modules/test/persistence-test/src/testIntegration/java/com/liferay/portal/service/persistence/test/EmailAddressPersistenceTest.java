/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateEmailAddressExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchEmailAddressException;
import com.liferay.portal.kernel.model.EmailAddress;
import com.liferay.portal.kernel.service.persistence.EmailAddressPersistence;
import com.liferay.portal.kernel.service.persistence.EmailAddressUtil;
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
public class EmailAddressPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = EmailAddressUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<EmailAddress> iterator = _emailAddresses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EmailAddress emailAddress = _persistence.create(pk);

		Assert.assertNotNull(emailAddress);

		Assert.assertEquals(emailAddress.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		EmailAddress newEmailAddress = addEmailAddress();

		_persistence.remove(newEmailAddress);

		EmailAddress existingEmailAddress = _persistence.fetchByPrimaryKey(
			newEmailAddress.getPrimaryKey());

		Assert.assertNull(existingEmailAddress);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addEmailAddress();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EmailAddress newEmailAddress = _persistence.create(pk);

		newEmailAddress.setMvccVersion(RandomTestUtil.nextLong());

		newEmailAddress.setCtCollectionId(RandomTestUtil.nextLong());

		newEmailAddress.setUuid(RandomTestUtil.randomString());

		newEmailAddress.setExternalReferenceCode(RandomTestUtil.randomString());

		newEmailAddress.setCompanyId(RandomTestUtil.nextLong());

		newEmailAddress.setUserId(RandomTestUtil.nextLong());

		newEmailAddress.setUserName(RandomTestUtil.randomString());

		newEmailAddress.setCreateDate(RandomTestUtil.nextDate());

		newEmailAddress.setModifiedDate(RandomTestUtil.nextDate());

		newEmailAddress.setClassNameId(RandomTestUtil.nextLong());

		newEmailAddress.setClassPK(RandomTestUtil.nextLong());

		newEmailAddress.setAddress(RandomTestUtil.randomString());

		newEmailAddress.setListTypeId(RandomTestUtil.nextLong());

		newEmailAddress.setPrimary(RandomTestUtil.randomBoolean());

		_emailAddresses.add(_persistence.update(newEmailAddress));

		EmailAddress existingEmailAddress = _persistence.findByPrimaryKey(
			newEmailAddress.getPrimaryKey());

		Assert.assertEquals(
			existingEmailAddress.getMvccVersion(),
			newEmailAddress.getMvccVersion());
		Assert.assertEquals(
			existingEmailAddress.getCtCollectionId(),
			newEmailAddress.getCtCollectionId());
		Assert.assertEquals(
			existingEmailAddress.getUuid(), newEmailAddress.getUuid());
		Assert.assertEquals(
			existingEmailAddress.getExternalReferenceCode(),
			newEmailAddress.getExternalReferenceCode());
		Assert.assertEquals(
			existingEmailAddress.getEmailAddressId(),
			newEmailAddress.getEmailAddressId());
		Assert.assertEquals(
			existingEmailAddress.getCompanyId(),
			newEmailAddress.getCompanyId());
		Assert.assertEquals(
			existingEmailAddress.getUserId(), newEmailAddress.getUserId());
		Assert.assertEquals(
			existingEmailAddress.getUserName(), newEmailAddress.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingEmailAddress.getCreateDate()),
			Time.getShortTimestamp(newEmailAddress.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingEmailAddress.getModifiedDate()),
			Time.getShortTimestamp(newEmailAddress.getModifiedDate()));
		Assert.assertEquals(
			existingEmailAddress.getClassNameId(),
			newEmailAddress.getClassNameId());
		Assert.assertEquals(
			existingEmailAddress.getClassPK(), newEmailAddress.getClassPK());
		Assert.assertEquals(
			existingEmailAddress.getAddress(), newEmailAddress.getAddress());
		Assert.assertEquals(
			existingEmailAddress.getListTypeId(),
			newEmailAddress.getListTypeId());
		Assert.assertEquals(
			existingEmailAddress.isPrimary(), newEmailAddress.isPrimary());
	}

	@Test(expected = DuplicateEmailAddressExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		EmailAddress emailAddress = addEmailAddress();

		EmailAddress newEmailAddress = addEmailAddress();

		newEmailAddress.setCompanyId(emailAddress.getCompanyId());

		newEmailAddress = _persistence.update(newEmailAddress);

		Session session = _persistence.getCurrentSession();

		session.evict(newEmailAddress);

		newEmailAddress.setExternalReferenceCode(
			emailAddress.getExternalReferenceCode());

		_persistence.update(newEmailAddress);
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
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
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
	public void testCountByC_C_C_P() throws Exception {
		_persistence.countByC_C_C_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_C_C_P(0L, 0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		EmailAddress newEmailAddress = addEmailAddress();

		EmailAddress existingEmailAddress = _persistence.findByPrimaryKey(
			newEmailAddress.getPrimaryKey());

		Assert.assertEquals(existingEmailAddress, newEmailAddress);
	}

	@Test(expected = NoSuchEmailAddressException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<EmailAddress> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"EmailAddress", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "emailAddressId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "classNameId", true, "classPK", true,
			"address", true, "listTypeId", true, "primary", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		EmailAddress newEmailAddress = addEmailAddress();

		EmailAddress existingEmailAddress = _persistence.fetchByPrimaryKey(
			newEmailAddress.getPrimaryKey());

		Assert.assertEquals(existingEmailAddress, newEmailAddress);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EmailAddress missingEmailAddress = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingEmailAddress);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		EmailAddress newEmailAddress1 = addEmailAddress();
		EmailAddress newEmailAddress2 = addEmailAddress();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEmailAddress1.getPrimaryKey());
		primaryKeys.add(newEmailAddress2.getPrimaryKey());

		Map<Serializable, EmailAddress> emailAddresses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, emailAddresses.size());
		Assert.assertEquals(
			newEmailAddress1,
			emailAddresses.get(newEmailAddress1.getPrimaryKey()));
		Assert.assertEquals(
			newEmailAddress2,
			emailAddresses.get(newEmailAddress2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, EmailAddress> emailAddresses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(emailAddresses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		EmailAddress newEmailAddress = addEmailAddress();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEmailAddress.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, EmailAddress> emailAddresses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, emailAddresses.size());
		Assert.assertEquals(
			newEmailAddress,
			emailAddresses.get(newEmailAddress.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, EmailAddress> emailAddresses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(emailAddresses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		EmailAddress newEmailAddress = addEmailAddress();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newEmailAddress.getPrimaryKey());

		Map<Serializable, EmailAddress> emailAddresses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, emailAddresses.size());
		Assert.assertEquals(
			newEmailAddress,
			emailAddresses.get(newEmailAddress.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		EmailAddress newEmailAddress = addEmailAddress();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newEmailAddress.getPrimaryKey()));
	}

	private void _assertOriginalValues(EmailAddress emailAddress) {
		Assert.assertEquals(
			emailAddress.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				emailAddress, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(emailAddress.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				emailAddress, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected EmailAddress addEmailAddress() throws Exception {
		long pk = RandomTestUtil.nextLong();

		EmailAddress emailAddress = _persistence.create(pk);

		emailAddress.setMvccVersion(RandomTestUtil.nextLong());

		emailAddress.setCtCollectionId(RandomTestUtil.nextLong());

		emailAddress.setUuid(RandomTestUtil.randomString());

		emailAddress.setExternalReferenceCode(RandomTestUtil.randomString());

		emailAddress.setCompanyId(RandomTestUtil.nextLong());

		emailAddress.setUserId(RandomTestUtil.nextLong());

		emailAddress.setUserName(RandomTestUtil.randomString());

		emailAddress.setCreateDate(RandomTestUtil.nextDate());

		emailAddress.setModifiedDate(RandomTestUtil.nextDate());

		emailAddress.setClassNameId(RandomTestUtil.nextLong());

		emailAddress.setClassPK(RandomTestUtil.nextLong());

		emailAddress.setAddress(RandomTestUtil.randomString());

		emailAddress.setListTypeId(RandomTestUtil.nextLong());

		emailAddress.setPrimary(RandomTestUtil.randomBoolean());

		_emailAddresses.add(_persistence.update(emailAddress));

		return emailAddress;
	}

	private List<EmailAddress> _emailAddresses = new ArrayList<EmailAddress>();
	private EmailAddressPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}