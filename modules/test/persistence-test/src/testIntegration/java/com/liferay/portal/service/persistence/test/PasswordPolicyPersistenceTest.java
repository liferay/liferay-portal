/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchPasswordPolicyException;
import com.liferay.portal.kernel.model.PasswordPolicy;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyPersistence;
import com.liferay.portal.kernel.service.persistence.PasswordPolicyUtil;
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
public class PasswordPolicyPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = PasswordPolicyUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<PasswordPolicy> iterator = _passwordPolicies.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PasswordPolicy passwordPolicy = _persistence.create(pk);

		Assert.assertNotNull(passwordPolicy);

		Assert.assertEquals(passwordPolicy.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		PasswordPolicy newPasswordPolicy = addPasswordPolicy();

		_persistence.remove(newPasswordPolicy);

		PasswordPolicy existingPasswordPolicy = _persistence.fetchByPrimaryKey(
			newPasswordPolicy.getPrimaryKey());

		Assert.assertNull(existingPasswordPolicy);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addPasswordPolicy();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PasswordPolicy newPasswordPolicy = _persistence.create(pk);

		newPasswordPolicy.setMvccVersion(RandomTestUtil.nextLong());

		newPasswordPolicy.setUuid(RandomTestUtil.randomString());

		newPasswordPolicy.setCompanyId(RandomTestUtil.nextLong());

		newPasswordPolicy.setUserId(RandomTestUtil.nextLong());

		newPasswordPolicy.setUserName(RandomTestUtil.randomString());

		newPasswordPolicy.setCreateDate(RandomTestUtil.nextDate());

		newPasswordPolicy.setModifiedDate(RandomTestUtil.nextDate());

		newPasswordPolicy.setDefaultPolicy(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setName(RandomTestUtil.randomString());

		newPasswordPolicy.setDescription(RandomTestUtil.randomString());

		newPasswordPolicy.setChangeable(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setChangeRequired(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setMinAge(RandomTestUtil.nextLong());

		newPasswordPolicy.setCheckSyntax(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setAllowDictionaryWords(
			RandomTestUtil.randomBoolean());

		newPasswordPolicy.setMinAlphanumeric(RandomTestUtil.nextInt());

		newPasswordPolicy.setMinLength(RandomTestUtil.nextInt());

		newPasswordPolicy.setMinLowerCase(RandomTestUtil.nextInt());

		newPasswordPolicy.setMinNumbers(RandomTestUtil.nextInt());

		newPasswordPolicy.setMinSymbols(RandomTestUtil.nextInt());

		newPasswordPolicy.setMinUpperCase(RandomTestUtil.nextInt());

		newPasswordPolicy.setRegex(RandomTestUtil.randomString());

		newPasswordPolicy.setHistory(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setHistoryCount(RandomTestUtil.nextInt());

		newPasswordPolicy.setExpireable(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setMaxAge(RandomTestUtil.nextLong());

		newPasswordPolicy.setWarningTime(RandomTestUtil.nextLong());

		newPasswordPolicy.setGraceLimit(RandomTestUtil.nextInt());

		newPasswordPolicy.setLockout(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setMaxFailure(RandomTestUtil.nextInt());

		newPasswordPolicy.setLockoutDuration(RandomTestUtil.nextLong());

		newPasswordPolicy.setRequireUnlock(RandomTestUtil.randomBoolean());

		newPasswordPolicy.setResetFailureCount(RandomTestUtil.nextLong());

		newPasswordPolicy.setResetTicketMaxAge(RandomTestUtil.nextLong());

		_passwordPolicies.add(_persistence.update(newPasswordPolicy));

		PasswordPolicy existingPasswordPolicy = _persistence.findByPrimaryKey(
			newPasswordPolicy.getPrimaryKey());

		Assert.assertEquals(
			existingPasswordPolicy.getMvccVersion(),
			newPasswordPolicy.getMvccVersion());
		Assert.assertEquals(
			existingPasswordPolicy.getUuid(), newPasswordPolicy.getUuid());
		Assert.assertEquals(
			existingPasswordPolicy.getPasswordPolicyId(),
			newPasswordPolicy.getPasswordPolicyId());
		Assert.assertEquals(
			existingPasswordPolicy.getCompanyId(),
			newPasswordPolicy.getCompanyId());
		Assert.assertEquals(
			existingPasswordPolicy.getUserId(), newPasswordPolicy.getUserId());
		Assert.assertEquals(
			existingPasswordPolicy.getUserName(),
			newPasswordPolicy.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingPasswordPolicy.getCreateDate()),
			Time.getShortTimestamp(newPasswordPolicy.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingPasswordPolicy.getModifiedDate()),
			Time.getShortTimestamp(newPasswordPolicy.getModifiedDate()));
		Assert.assertEquals(
			existingPasswordPolicy.isDefaultPolicy(),
			newPasswordPolicy.isDefaultPolicy());
		Assert.assertEquals(
			existingPasswordPolicy.getName(), newPasswordPolicy.getName());
		Assert.assertEquals(
			existingPasswordPolicy.getDescription(),
			newPasswordPolicy.getDescription());
		Assert.assertEquals(
			existingPasswordPolicy.isChangeable(),
			newPasswordPolicy.isChangeable());
		Assert.assertEquals(
			existingPasswordPolicy.isChangeRequired(),
			newPasswordPolicy.isChangeRequired());
		Assert.assertEquals(
			existingPasswordPolicy.getMinAge(), newPasswordPolicy.getMinAge());
		Assert.assertEquals(
			existingPasswordPolicy.isCheckSyntax(),
			newPasswordPolicy.isCheckSyntax());
		Assert.assertEquals(
			existingPasswordPolicy.isAllowDictionaryWords(),
			newPasswordPolicy.isAllowDictionaryWords());
		Assert.assertEquals(
			existingPasswordPolicy.getMinAlphanumeric(),
			newPasswordPolicy.getMinAlphanumeric());
		Assert.assertEquals(
			existingPasswordPolicy.getMinLength(),
			newPasswordPolicy.getMinLength());
		Assert.assertEquals(
			existingPasswordPolicy.getMinLowerCase(),
			newPasswordPolicy.getMinLowerCase());
		Assert.assertEquals(
			existingPasswordPolicy.getMinNumbers(),
			newPasswordPolicy.getMinNumbers());
		Assert.assertEquals(
			existingPasswordPolicy.getMinSymbols(),
			newPasswordPolicy.getMinSymbols());
		Assert.assertEquals(
			existingPasswordPolicy.getMinUpperCase(),
			newPasswordPolicy.getMinUpperCase());
		Assert.assertEquals(
			existingPasswordPolicy.getRegex(), newPasswordPolicy.getRegex());
		Assert.assertEquals(
			existingPasswordPolicy.isHistory(), newPasswordPolicy.isHistory());
		Assert.assertEquals(
			existingPasswordPolicy.getHistoryCount(),
			newPasswordPolicy.getHistoryCount());
		Assert.assertEquals(
			existingPasswordPolicy.isExpireable(),
			newPasswordPolicy.isExpireable());
		Assert.assertEquals(
			existingPasswordPolicy.getMaxAge(), newPasswordPolicy.getMaxAge());
		Assert.assertEquals(
			existingPasswordPolicy.getWarningTime(),
			newPasswordPolicy.getWarningTime());
		Assert.assertEquals(
			existingPasswordPolicy.getGraceLimit(),
			newPasswordPolicy.getGraceLimit());
		Assert.assertEquals(
			existingPasswordPolicy.isLockout(), newPasswordPolicy.isLockout());
		Assert.assertEquals(
			existingPasswordPolicy.getMaxFailure(),
			newPasswordPolicy.getMaxFailure());
		Assert.assertEquals(
			existingPasswordPolicy.getLockoutDuration(),
			newPasswordPolicy.getLockoutDuration());
		Assert.assertEquals(
			existingPasswordPolicy.isRequireUnlock(),
			newPasswordPolicy.isRequireUnlock());
		Assert.assertEquals(
			existingPasswordPolicy.getResetFailureCount(),
			newPasswordPolicy.getResetFailureCount());
		Assert.assertEquals(
			existingPasswordPolicy.getResetTicketMaxAge(),
			newPasswordPolicy.getResetTicketMaxAge());
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
	public void testCountByC_N() throws Exception {
		_persistence.countByC_N(RandomTestUtil.nextLong(), "");

		_persistence.countByC_N(0L, "null");

		_persistence.countByC_N(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		PasswordPolicy newPasswordPolicy = addPasswordPolicy();

		PasswordPolicy existingPasswordPolicy = _persistence.findByPrimaryKey(
			newPasswordPolicy.getPrimaryKey());

		Assert.assertEquals(existingPasswordPolicy, newPasswordPolicy);
	}

	@Test(expected = NoSuchPasswordPolicyException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<PasswordPolicy> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"PasswordPolicy", "mvccVersion", true, "uuid", true,
			"passwordPolicyId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"defaultPolicy", true, "name", true, "description", true,
			"changeable", true, "changeRequired", true, "minAge", true,
			"checkSyntax", true, "allowDictionaryWords", true,
			"minAlphanumeric", true, "minLength", true, "minLowerCase", true,
			"minNumbers", true, "minSymbols", true, "minUpperCase", true,
			"regex", true, "history", true, "historyCount", true, "expireable",
			true, "maxAge", true, "warningTime", true, "graceLimit", true,
			"lockout", true, "maxFailure", true, "lockoutDuration", true,
			"requireUnlock", true, "resetFailureCount", true,
			"resetTicketMaxAge", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		PasswordPolicy newPasswordPolicy = addPasswordPolicy();

		PasswordPolicy existingPasswordPolicy = _persistence.fetchByPrimaryKey(
			newPasswordPolicy.getPrimaryKey());

		Assert.assertEquals(existingPasswordPolicy, newPasswordPolicy);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PasswordPolicy missingPasswordPolicy = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingPasswordPolicy);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		PasswordPolicy newPasswordPolicy1 = addPasswordPolicy();
		PasswordPolicy newPasswordPolicy2 = addPasswordPolicy();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPasswordPolicy1.getPrimaryKey());
		primaryKeys.add(newPasswordPolicy2.getPrimaryKey());

		Map<Serializable, PasswordPolicy> passwordPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, passwordPolicies.size());
		Assert.assertEquals(
			newPasswordPolicy1,
			passwordPolicies.get(newPasswordPolicy1.getPrimaryKey()));
		Assert.assertEquals(
			newPasswordPolicy2,
			passwordPolicies.get(newPasswordPolicy2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, PasswordPolicy> passwordPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(passwordPolicies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		PasswordPolicy newPasswordPolicy = addPasswordPolicy();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPasswordPolicy.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, PasswordPolicy> passwordPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, passwordPolicies.size());
		Assert.assertEquals(
			newPasswordPolicy,
			passwordPolicies.get(newPasswordPolicy.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, PasswordPolicy> passwordPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(passwordPolicies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		PasswordPolicy newPasswordPolicy = addPasswordPolicy();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newPasswordPolicy.getPrimaryKey());

		Map<Serializable, PasswordPolicy> passwordPolicies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, passwordPolicies.size());
		Assert.assertEquals(
			newPasswordPolicy,
			passwordPolicies.get(newPasswordPolicy.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		PasswordPolicy newPasswordPolicy = addPasswordPolicy();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newPasswordPolicy.getPrimaryKey()));
	}

	private void _assertOriginalValues(PasswordPolicy passwordPolicy) {
		Assert.assertEquals(
			Long.valueOf(passwordPolicy.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				passwordPolicy, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			passwordPolicy.getName(),
			ReflectionTestUtil.invoke(
				passwordPolicy, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected PasswordPolicy addPasswordPolicy() throws Exception {
		long pk = RandomTestUtil.nextLong();

		PasswordPolicy passwordPolicy = _persistence.create(pk);

		passwordPolicy.setMvccVersion(RandomTestUtil.nextLong());

		passwordPolicy.setUuid(RandomTestUtil.randomString());

		passwordPolicy.setCompanyId(RandomTestUtil.nextLong());

		passwordPolicy.setUserId(RandomTestUtil.nextLong());

		passwordPolicy.setUserName(RandomTestUtil.randomString());

		passwordPolicy.setCreateDate(RandomTestUtil.nextDate());

		passwordPolicy.setModifiedDate(RandomTestUtil.nextDate());

		passwordPolicy.setDefaultPolicy(RandomTestUtil.randomBoolean());

		passwordPolicy.setName(RandomTestUtil.randomString());

		passwordPolicy.setDescription(RandomTestUtil.randomString());

		passwordPolicy.setChangeable(RandomTestUtil.randomBoolean());

		passwordPolicy.setChangeRequired(RandomTestUtil.randomBoolean());

		passwordPolicy.setMinAge(RandomTestUtil.nextLong());

		passwordPolicy.setCheckSyntax(RandomTestUtil.randomBoolean());

		passwordPolicy.setAllowDictionaryWords(RandomTestUtil.randomBoolean());

		passwordPolicy.setMinAlphanumeric(RandomTestUtil.nextInt());

		passwordPolicy.setMinLength(RandomTestUtil.nextInt());

		passwordPolicy.setMinLowerCase(RandomTestUtil.nextInt());

		passwordPolicy.setMinNumbers(RandomTestUtil.nextInt());

		passwordPolicy.setMinSymbols(RandomTestUtil.nextInt());

		passwordPolicy.setMinUpperCase(RandomTestUtil.nextInt());

		passwordPolicy.setRegex(RandomTestUtil.randomString());

		passwordPolicy.setHistory(RandomTestUtil.randomBoolean());

		passwordPolicy.setHistoryCount(RandomTestUtil.nextInt());

		passwordPolicy.setExpireable(RandomTestUtil.randomBoolean());

		passwordPolicy.setMaxAge(RandomTestUtil.nextLong());

		passwordPolicy.setWarningTime(RandomTestUtil.nextLong());

		passwordPolicy.setGraceLimit(RandomTestUtil.nextInt());

		passwordPolicy.setLockout(RandomTestUtil.randomBoolean());

		passwordPolicy.setMaxFailure(RandomTestUtil.nextInt());

		passwordPolicy.setLockoutDuration(RandomTestUtil.nextLong());

		passwordPolicy.setRequireUnlock(RandomTestUtil.randomBoolean());

		passwordPolicy.setResetFailureCount(RandomTestUtil.nextLong());

		passwordPolicy.setResetTicketMaxAge(RandomTestUtil.nextLong());

		_passwordPolicies.add(_persistence.update(passwordPolicy));

		return passwordPolicy;
	}

	private List<PasswordPolicy> _passwordPolicies =
		new ArrayList<PasswordPolicy>();
	private PasswordPolicyPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}