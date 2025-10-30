/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateUserExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchUserException;
import com.liferay.portal.kernel.model.User;
import com.liferay.portal.kernel.service.persistence.UserPersistence;
import com.liferay.portal.kernel.service.persistence.UserUtil;
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
public class UserPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<User> iterator = _users.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		User user = _persistence.create(pk);

		Assert.assertNotNull(user);

		Assert.assertEquals(user.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		User newUser = addUser();

		_persistence.remove(newUser);

		User existingUser = _persistence.fetchByPrimaryKey(
			newUser.getPrimaryKey());

		Assert.assertNull(existingUser);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUser();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		User newUser = _persistence.create(pk);

		newUser.setMvccVersion(RandomTestUtil.nextLong());

		newUser.setCtCollectionId(RandomTestUtil.nextLong());

		newUser.setUuid(RandomTestUtil.randomString());

		newUser.setExternalReferenceCode(RandomTestUtil.randomString());

		newUser.setCompanyId(RandomTestUtil.nextLong());

		newUser.setCreateDate(RandomTestUtil.nextDate());

		newUser.setModifiedDate(RandomTestUtil.nextDate());

		newUser.setContactId(RandomTestUtil.nextLong());

		newUser.setPassword(RandomTestUtil.randomString());

		newUser.setPasswordEncrypted(RandomTestUtil.randomBoolean());

		newUser.setPasswordReset(RandomTestUtil.randomBoolean());

		newUser.setPasswordModifiedDate(RandomTestUtil.nextDate());

		newUser.setDigest(RandomTestUtil.randomString());

		newUser.setReminderQueryQuestion(RandomTestUtil.randomString());

		newUser.setReminderQueryAnswer(RandomTestUtil.randomString());

		newUser.setGraceLoginCount(RandomTestUtil.nextInt());

		newUser.setScreenName(RandomTestUtil.randomString());

		newUser.setEmailAddress(RandomTestUtil.randomString());

		newUser.setFacebookId(RandomTestUtil.nextLong());

		newUser.setGoogleUserId(RandomTestUtil.randomString());

		newUser.setLdapServerId(RandomTestUtil.nextLong());

		newUser.setOpenId(RandomTestUtil.randomString());

		newUser.setPortraitId(RandomTestUtil.nextLong());

		newUser.setLanguageId(RandomTestUtil.randomString());

		newUser.setTimeZoneId(RandomTestUtil.randomString());

		newUser.setGreeting(RandomTestUtil.randomString());

		newUser.setComments(RandomTestUtil.randomString());

		newUser.setFirstName(RandomTestUtil.randomString());

		newUser.setMiddleName(RandomTestUtil.randomString());

		newUser.setLastName(RandomTestUtil.randomString());

		newUser.setJobTitle(RandomTestUtil.randomString());

		newUser.setLoginDate(RandomTestUtil.nextDate());

		newUser.setLoginIP(RandomTestUtil.randomString());

		newUser.setLastLoginDate(RandomTestUtil.nextDate());

		newUser.setLastLoginIP(RandomTestUtil.randomString());

		newUser.setLastFailedLoginDate(RandomTestUtil.nextDate());

		newUser.setFailedLoginAttempts(RandomTestUtil.nextInt());

		newUser.setLockout(RandomTestUtil.randomBoolean());

		newUser.setLockoutDate(RandomTestUtil.nextDate());

		newUser.setAgreedToTermsOfUse(RandomTestUtil.randomBoolean());

		newUser.setEmailAddressVerified(RandomTestUtil.randomBoolean());

		newUser.setType(RandomTestUtil.nextInt());

		newUser.setStatus(RandomTestUtil.nextInt());

		_users.add(_persistence.update(newUser));

		User existingUser = _persistence.findByPrimaryKey(
			newUser.getPrimaryKey());

		Assert.assertEquals(
			existingUser.getMvccVersion(), newUser.getMvccVersion());
		Assert.assertEquals(
			existingUser.getCtCollectionId(), newUser.getCtCollectionId());
		Assert.assertEquals(existingUser.getUuid(), newUser.getUuid());
		Assert.assertEquals(
			existingUser.getExternalReferenceCode(),
			newUser.getExternalReferenceCode());
		Assert.assertEquals(existingUser.getUserId(), newUser.getUserId());
		Assert.assertEquals(
			existingUser.getCompanyId(), newUser.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getCreateDate()),
			Time.getShortTimestamp(newUser.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getModifiedDate()),
			Time.getShortTimestamp(newUser.getModifiedDate()));
		Assert.assertEquals(
			existingUser.getContactId(), newUser.getContactId());
		Assert.assertEquals(existingUser.getPassword(), newUser.getPassword());
		Assert.assertEquals(
			existingUser.isPasswordEncrypted(), newUser.isPasswordEncrypted());
		Assert.assertEquals(
			existingUser.isPasswordReset(), newUser.isPasswordReset());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getPasswordModifiedDate()),
			Time.getShortTimestamp(newUser.getPasswordModifiedDate()));
		Assert.assertEquals(existingUser.getDigest(), newUser.getDigest());
		Assert.assertEquals(
			existingUser.getReminderQueryQuestion(),
			newUser.getReminderQueryQuestion());
		Assert.assertEquals(
			existingUser.getReminderQueryAnswer(),
			newUser.getReminderQueryAnswer());
		Assert.assertEquals(
			existingUser.getGraceLoginCount(), newUser.getGraceLoginCount());
		Assert.assertEquals(
			existingUser.getScreenName(), newUser.getScreenName());
		Assert.assertEquals(
			existingUser.getEmailAddress(), newUser.getEmailAddress());
		Assert.assertEquals(
			existingUser.getFacebookId(), newUser.getFacebookId());
		Assert.assertEquals(
			existingUser.getGoogleUserId(), newUser.getGoogleUserId());
		Assert.assertEquals(
			existingUser.getLdapServerId(), newUser.getLdapServerId());
		Assert.assertEquals(existingUser.getOpenId(), newUser.getOpenId());
		Assert.assertEquals(
			existingUser.getPortraitId(), newUser.getPortraitId());
		Assert.assertEquals(
			existingUser.getLanguageId(), newUser.getLanguageId());
		Assert.assertEquals(
			existingUser.getTimeZoneId(), newUser.getTimeZoneId());
		Assert.assertEquals(existingUser.getGreeting(), newUser.getGreeting());
		Assert.assertEquals(existingUser.getComments(), newUser.getComments());
		Assert.assertEquals(
			existingUser.getFirstName(), newUser.getFirstName());
		Assert.assertEquals(
			existingUser.getMiddleName(), newUser.getMiddleName());
		Assert.assertEquals(existingUser.getLastName(), newUser.getLastName());
		Assert.assertEquals(existingUser.getJobTitle(), newUser.getJobTitle());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getLoginDate()),
			Time.getShortTimestamp(newUser.getLoginDate()));
		Assert.assertEquals(existingUser.getLoginIP(), newUser.getLoginIP());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getLastLoginDate()),
			Time.getShortTimestamp(newUser.getLastLoginDate()));
		Assert.assertEquals(
			existingUser.getLastLoginIP(), newUser.getLastLoginIP());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getLastFailedLoginDate()),
			Time.getShortTimestamp(newUser.getLastFailedLoginDate()));
		Assert.assertEquals(
			existingUser.getFailedLoginAttempts(),
			newUser.getFailedLoginAttempts());
		Assert.assertEquals(existingUser.isLockout(), newUser.isLockout());
		Assert.assertEquals(
			Time.getShortTimestamp(existingUser.getLockoutDate()),
			Time.getShortTimestamp(newUser.getLockoutDate()));
		Assert.assertEquals(
			existingUser.isAgreedToTermsOfUse(),
			newUser.isAgreedToTermsOfUse());
		Assert.assertEquals(
			existingUser.isEmailAddressVerified(),
			newUser.isEmailAddressVerified());
		Assert.assertEquals(existingUser.getType(), newUser.getType());
		Assert.assertEquals(existingUser.getStatus(), newUser.getStatus());
	}

	@Test(expected = DuplicateUserExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		User user = addUser();

		User newUser = addUser();

		newUser.setCompanyId(user.getCompanyId());

		newUser = _persistence.update(newUser);

		Session session = _persistence.getCurrentSession();

		session.evict(newUser);

		newUser.setExternalReferenceCode(user.getExternalReferenceCode());

		_persistence.update(newUser);
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
	public void testCountByContactId() throws Exception {
		_persistence.countByContactId(RandomTestUtil.nextLong());

		_persistence.countByContactId(0L);
	}

	@Test
	public void testCountByEmailAddress() throws Exception {
		_persistence.countByEmailAddress("");

		_persistence.countByEmailAddress("null");

		_persistence.countByEmailAddress((String)null);
	}

	@Test
	public void testCountByPortraitId() throws Exception {
		_persistence.countByPortraitId(RandomTestUtil.nextLong());

		_persistence.countByPortraitId(0L);
	}

	@Test
	public void testCountByGtU_C() throws Exception {
		_persistence.countByGtU_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByGtU_C(0L, 0L);
	}

	@Test
	public void testCountByC_U() throws Exception {
		_persistence.countByC_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_U(0L, 0L);
	}

	@Test
	public void testCountByC_CD() throws Exception {
		_persistence.countByC_CD(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate());

		_persistence.countByC_CD(0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByC_MD() throws Exception {
		_persistence.countByC_MD(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate());

		_persistence.countByC_MD(0L, RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByC_SN() throws Exception {
		_persistence.countByC_SN(RandomTestUtil.nextLong(), "");

		_persistence.countByC_SN(0L, "null");

		_persistence.countByC_SN(0L, (String)null);
	}

	@Test
	public void testCountByC_EA() throws Exception {
		_persistence.countByC_EA(RandomTestUtil.nextLong(), "");

		_persistence.countByC_EA(0L, "null");

		_persistence.countByC_EA(0L, (String)null);
	}

	@Test
	public void testCountByC_FID() throws Exception {
		_persistence.countByC_FID(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_FID(0L, 0L);
	}

	@Test
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_T(0L, 0);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_S(0L, 0);
	}

	@Test
	public void testCountByC_CD_MD() throws Exception {
		_persistence.countByC_CD_MD(
			RandomTestUtil.nextLong(), RandomTestUtil.nextDate(),
			RandomTestUtil.nextDate());

		_persistence.countByC_CD_MD(
			0L, RandomTestUtil.nextDate(), RandomTestUtil.nextDate());
	}

	@Test
	public void testCountByC_T_S() throws Exception {
		_persistence.countByC_T_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt(),
			RandomTestUtil.nextInt());

		_persistence.countByC_T_S(0L, 0, 0);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		User newUser = addUser();

		User existingUser = _persistence.findByPrimaryKey(
			newUser.getPrimaryKey());

		Assert.assertEquals(existingUser, newUser);
	}

	@Test(expected = NoSuchUserException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<User> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"User_", "mvccVersion", true, "ctCollectionId", true, "uuid", true,
			"externalReferenceCode", true, "userId", true, "companyId", true,
			"createDate", true, "modifiedDate", true, "contactId", true,
			"password", true, "passwordEncrypted", true, "passwordReset", true,
			"passwordModifiedDate", true, "digest", true,
			"reminderQueryQuestion", true, "reminderQueryAnswer", true,
			"graceLoginCount", true, "screenName", true, "emailAddress", true,
			"facebookId", true, "googleUserId", true, "ldapServerId", true,
			"openId", true, "portraitId", true, "languageId", true,
			"timeZoneId", true, "greeting", true, "comments", true, "firstName",
			true, "middleName", true, "lastName", true, "jobTitle", true,
			"loginDate", true, "loginIP", true, "lastLoginDate", true,
			"lastLoginIP", true, "lastFailedLoginDate", true,
			"failedLoginAttempts", true, "lockout", true, "lockoutDate", true,
			"agreedToTermsOfUse", true, "emailAddressVerified", true, "type",
			true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		User newUser = addUser();

		User existingUser = _persistence.fetchByPrimaryKey(
			newUser.getPrimaryKey());

		Assert.assertEquals(existingUser, newUser);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		User missingUser = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUser);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		User newUser1 = addUser();
		User newUser2 = addUser();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUser1.getPrimaryKey());
		primaryKeys.add(newUser2.getPrimaryKey());

		Map<Serializable, User> users = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, users.size());
		Assert.assertEquals(newUser1, users.get(newUser1.getPrimaryKey()));
		Assert.assertEquals(newUser2, users.get(newUser2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, User> users = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(users.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		User newUser = addUser();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUser.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, User> users = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, users.size());
		Assert.assertEquals(newUser, users.get(newUser.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, User> users = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(users.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		User newUser = addUser();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUser.getPrimaryKey());

		Map<Serializable, User> users = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, users.size());
		Assert.assertEquals(newUser, users.get(newUser.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		User newUser = addUser();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newUser.getPrimaryKey()));
	}

	private void _assertOriginalValues(User user) {
		Assert.assertEquals(
			Long.valueOf(user.getContactId()),
			ReflectionTestUtil.<Long>invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"contactId"));

		Assert.assertEquals(
			Long.valueOf(user.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			Long.valueOf(user.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"userId"));

		Assert.assertEquals(
			Long.valueOf(user.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			user.getScreenName(),
			ReflectionTestUtil.invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"screenName"));

		Assert.assertEquals(
			Long.valueOf(user.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
		Assert.assertEquals(
			user.getEmailAddress(),
			ReflectionTestUtil.invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"emailAddress"));

		Assert.assertEquals(
			user.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(user.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				user, "getColumnOriginalValue", new Class<?>[] {String.class},
				"companyId"));
	}

	protected User addUser() throws Exception {
		long pk = RandomTestUtil.nextLong();

		User user = _persistence.create(pk);

		user.setMvccVersion(RandomTestUtil.nextLong());

		user.setCtCollectionId(RandomTestUtil.nextLong());

		user.setUuid(RandomTestUtil.randomString());

		user.setExternalReferenceCode(RandomTestUtil.randomString());

		user.setCompanyId(RandomTestUtil.nextLong());

		user.setCreateDate(RandomTestUtil.nextDate());

		user.setModifiedDate(RandomTestUtil.nextDate());

		user.setContactId(RandomTestUtil.nextLong());

		user.setPassword(RandomTestUtil.randomString());

		user.setPasswordEncrypted(RandomTestUtil.randomBoolean());

		user.setPasswordReset(RandomTestUtil.randomBoolean());

		user.setPasswordModifiedDate(RandomTestUtil.nextDate());

		user.setDigest(RandomTestUtil.randomString());

		user.setReminderQueryQuestion(RandomTestUtil.randomString());

		user.setReminderQueryAnswer(RandomTestUtil.randomString());

		user.setGraceLoginCount(RandomTestUtil.nextInt());

		user.setScreenName(RandomTestUtil.randomString());

		user.setEmailAddress(RandomTestUtil.randomString());

		user.setFacebookId(RandomTestUtil.nextLong());

		user.setGoogleUserId(RandomTestUtil.randomString());

		user.setLdapServerId(RandomTestUtil.nextLong());

		user.setOpenId(RandomTestUtil.randomString());

		user.setPortraitId(RandomTestUtil.nextLong());

		user.setLanguageId(RandomTestUtil.randomString());

		user.setTimeZoneId(RandomTestUtil.randomString());

		user.setGreeting(RandomTestUtil.randomString());

		user.setComments(RandomTestUtil.randomString());

		user.setFirstName(RandomTestUtil.randomString());

		user.setMiddleName(RandomTestUtil.randomString());

		user.setLastName(RandomTestUtil.randomString());

		user.setJobTitle(RandomTestUtil.randomString());

		user.setLoginDate(RandomTestUtil.nextDate());

		user.setLoginIP(RandomTestUtil.randomString());

		user.setLastLoginDate(RandomTestUtil.nextDate());

		user.setLastLoginIP(RandomTestUtil.randomString());

		user.setLastFailedLoginDate(RandomTestUtil.nextDate());

		user.setFailedLoginAttempts(RandomTestUtil.nextInt());

		user.setLockout(RandomTestUtil.randomBoolean());

		user.setLockoutDate(RandomTestUtil.nextDate());

		user.setAgreedToTermsOfUse(RandomTestUtil.randomBoolean());

		user.setEmailAddressVerified(RandomTestUtil.randomBoolean());

		user.setType(RandomTestUtil.nextInt());

		user.setStatus(RandomTestUtil.nextInt());

		_users.add(_persistence.update(user));

		return user;
	}

	private List<User> _users = new ArrayList<User>();
	private UserPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}