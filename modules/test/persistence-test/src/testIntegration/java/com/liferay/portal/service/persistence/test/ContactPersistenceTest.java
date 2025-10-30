/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchContactException;
import com.liferay.portal.kernel.model.Contact;
import com.liferay.portal.kernel.service.persistence.ContactPersistence;
import com.liferay.portal.kernel.service.persistence.ContactUtil;
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
public class ContactPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = ContactUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Contact> iterator = _contacts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Contact contact = _persistence.create(pk);

		Assert.assertNotNull(contact);

		Assert.assertEquals(contact.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Contact newContact = addContact();

		_persistence.remove(newContact);

		Contact existingContact = _persistence.fetchByPrimaryKey(
			newContact.getPrimaryKey());

		Assert.assertNull(existingContact);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addContact();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Contact newContact = _persistence.create(pk);

		newContact.setMvccVersion(RandomTestUtil.nextLong());

		newContact.setCompanyId(RandomTestUtil.nextLong());

		newContact.setUserId(RandomTestUtil.nextLong());

		newContact.setUserName(RandomTestUtil.randomString());

		newContact.setCreateDate(RandomTestUtil.nextDate());

		newContact.setModifiedDate(RandomTestUtil.nextDate());

		newContact.setClassNameId(RandomTestUtil.nextLong());

		newContact.setClassPK(RandomTestUtil.nextLong());

		newContact.setParentContactId(RandomTestUtil.nextLong());

		newContact.setEmailAddress(RandomTestUtil.randomString());

		newContact.setFirstName(RandomTestUtil.randomString());

		newContact.setMiddleName(RandomTestUtil.randomString());

		newContact.setLastName(RandomTestUtil.randomString());

		newContact.setPrefixListTypeId(RandomTestUtil.nextLong());

		newContact.setSuffixListTypeId(RandomTestUtil.nextLong());

		newContact.setMale(RandomTestUtil.randomBoolean());

		newContact.setBirthday(RandomTestUtil.nextDate());

		newContact.setSmsSn(RandomTestUtil.randomString());

		newContact.setFacebookSn(RandomTestUtil.randomString());

		newContact.setJabberSn(RandomTestUtil.randomString());

		newContact.setSkypeSn(RandomTestUtil.randomString());

		newContact.setTwitterSn(RandomTestUtil.randomString());

		newContact.setEmployeeStatusId(RandomTestUtil.randomString());

		newContact.setEmployeeNumber(RandomTestUtil.randomString());

		newContact.setJobTitle(RandomTestUtil.randomString());

		newContact.setJobClass(RandomTestUtil.randomString());

		newContact.setHoursOfOperation(RandomTestUtil.randomString());

		_contacts.add(_persistence.update(newContact));

		Contact existingContact = _persistence.findByPrimaryKey(
			newContact.getPrimaryKey());

		Assert.assertEquals(
			existingContact.getMvccVersion(), newContact.getMvccVersion());
		Assert.assertEquals(
			existingContact.getContactId(), newContact.getContactId());
		Assert.assertEquals(
			existingContact.getCompanyId(), newContact.getCompanyId());
		Assert.assertEquals(
			existingContact.getUserId(), newContact.getUserId());
		Assert.assertEquals(
			existingContact.getUserName(), newContact.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingContact.getCreateDate()),
			Time.getShortTimestamp(newContact.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingContact.getModifiedDate()),
			Time.getShortTimestamp(newContact.getModifiedDate()));
		Assert.assertEquals(
			existingContact.getClassNameId(), newContact.getClassNameId());
		Assert.assertEquals(
			existingContact.getClassPK(), newContact.getClassPK());
		Assert.assertEquals(
			existingContact.getParentContactId(),
			newContact.getParentContactId());
		Assert.assertEquals(
			existingContact.getEmailAddress(), newContact.getEmailAddress());
		Assert.assertEquals(
			existingContact.getFirstName(), newContact.getFirstName());
		Assert.assertEquals(
			existingContact.getMiddleName(), newContact.getMiddleName());
		Assert.assertEquals(
			existingContact.getLastName(), newContact.getLastName());
		Assert.assertEquals(
			existingContact.getPrefixListTypeId(),
			newContact.getPrefixListTypeId());
		Assert.assertEquals(
			existingContact.getSuffixListTypeId(),
			newContact.getSuffixListTypeId());
		Assert.assertEquals(existingContact.isMale(), newContact.isMale());
		Assert.assertEquals(
			Time.getShortTimestamp(existingContact.getBirthday()),
			Time.getShortTimestamp(newContact.getBirthday()));
		Assert.assertEquals(existingContact.getSmsSn(), newContact.getSmsSn());
		Assert.assertEquals(
			existingContact.getFacebookSn(), newContact.getFacebookSn());
		Assert.assertEquals(
			existingContact.getJabberSn(), newContact.getJabberSn());
		Assert.assertEquals(
			existingContact.getSkypeSn(), newContact.getSkypeSn());
		Assert.assertEquals(
			existingContact.getTwitterSn(), newContact.getTwitterSn());
		Assert.assertEquals(
			existingContact.getEmployeeStatusId(),
			newContact.getEmployeeStatusId());
		Assert.assertEquals(
			existingContact.getEmployeeNumber(),
			newContact.getEmployeeNumber());
		Assert.assertEquals(
			existingContact.getJobTitle(), newContact.getJobTitle());
		Assert.assertEquals(
			existingContact.getJobClass(), newContact.getJobClass());
		Assert.assertEquals(
			existingContact.getHoursOfOperation(),
			newContact.getHoursOfOperation());
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
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Contact newContact = addContact();

		Contact existingContact = _persistence.findByPrimaryKey(
			newContact.getPrimaryKey());

		Assert.assertEquals(existingContact, newContact);
	}

	@Test(expected = NoSuchContactException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Contact> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Contact_", "mvccVersion", true, "contactId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"parentContactId", true, "emailAddress", true, "firstName", true,
			"middleName", true, "lastName", true, "prefixListTypeId", true,
			"suffixListTypeId", true, "male", true, "birthday", true, "smsSn",
			true, "facebookSn", true, "jabberSn", true, "skypeSn", true,
			"twitterSn", true, "employeeStatusId", true, "employeeNumber", true,
			"jobTitle", true, "jobClass", true, "hoursOfOperation", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Contact newContact = addContact();

		Contact existingContact = _persistence.fetchByPrimaryKey(
			newContact.getPrimaryKey());

		Assert.assertEquals(existingContact, newContact);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Contact missingContact = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingContact);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Contact newContact1 = addContact();
		Contact newContact2 = addContact();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newContact1.getPrimaryKey());
		primaryKeys.add(newContact2.getPrimaryKey());

		Map<Serializable, Contact> contacts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, contacts.size());
		Assert.assertEquals(
			newContact1, contacts.get(newContact1.getPrimaryKey()));
		Assert.assertEquals(
			newContact2, contacts.get(newContact2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Contact> contacts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(contacts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Contact newContact = addContact();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newContact.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Contact> contacts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, contacts.size());
		Assert.assertEquals(
			newContact, contacts.get(newContact.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Contact> contacts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(contacts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Contact newContact = addContact();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newContact.getPrimaryKey());

		Map<Serializable, Contact> contacts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, contacts.size());
		Assert.assertEquals(
			newContact, contacts.get(newContact.getPrimaryKey()));
	}

	protected Contact addContact() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Contact contact = _persistence.create(pk);

		contact.setMvccVersion(RandomTestUtil.nextLong());

		contact.setCompanyId(RandomTestUtil.nextLong());

		contact.setUserId(RandomTestUtil.nextLong());

		contact.setUserName(RandomTestUtil.randomString());

		contact.setCreateDate(RandomTestUtil.nextDate());

		contact.setModifiedDate(RandomTestUtil.nextDate());

		contact.setClassNameId(RandomTestUtil.nextLong());

		contact.setClassPK(RandomTestUtil.nextLong());

		contact.setParentContactId(RandomTestUtil.nextLong());

		contact.setEmailAddress(RandomTestUtil.randomString());

		contact.setFirstName(RandomTestUtil.randomString());

		contact.setMiddleName(RandomTestUtil.randomString());

		contact.setLastName(RandomTestUtil.randomString());

		contact.setPrefixListTypeId(RandomTestUtil.nextLong());

		contact.setSuffixListTypeId(RandomTestUtil.nextLong());

		contact.setMale(RandomTestUtil.randomBoolean());

		contact.setBirthday(RandomTestUtil.nextDate());

		contact.setSmsSn(RandomTestUtil.randomString());

		contact.setFacebookSn(RandomTestUtil.randomString());

		contact.setJabberSn(RandomTestUtil.randomString());

		contact.setSkypeSn(RandomTestUtil.randomString());

		contact.setTwitterSn(RandomTestUtil.randomString());

		contact.setEmployeeStatusId(RandomTestUtil.randomString());

		contact.setEmployeeNumber(RandomTestUtil.randomString());

		contact.setJobTitle(RandomTestUtil.randomString());

		contact.setJobClass(RandomTestUtil.randomString());

		contact.setHoursOfOperation(RandomTestUtil.randomString());

		_contacts.add(_persistence.update(contact));

		return contact;
	}

	private List<Contact> _contacts = new ArrayList<Contact>();
	private ContactPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}