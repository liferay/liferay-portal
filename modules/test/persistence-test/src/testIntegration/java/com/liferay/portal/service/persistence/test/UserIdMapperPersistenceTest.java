/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchUserIdMapperException;
import com.liferay.portal.kernel.model.UserIdMapper;
import com.liferay.portal.kernel.service.persistence.UserIdMapperPersistence;
import com.liferay.portal.kernel.service.persistence.UserIdMapperUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
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
public class UserIdMapperPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = UserIdMapperUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<UserIdMapper> iterator = _userIdMappers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserIdMapper userIdMapper = _persistence.create(pk);

		Assert.assertNotNull(userIdMapper);

		Assert.assertEquals(userIdMapper.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		UserIdMapper newUserIdMapper = addUserIdMapper();

		_persistence.remove(newUserIdMapper);

		UserIdMapper existingUserIdMapper = _persistence.fetchByPrimaryKey(
			newUserIdMapper.getPrimaryKey());

		Assert.assertNull(existingUserIdMapper);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addUserIdMapper();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserIdMapper newUserIdMapper = _persistence.create(pk);

		newUserIdMapper.setMvccVersion(RandomTestUtil.nextLong());

		newUserIdMapper.setCompanyId(RandomTestUtil.nextLong());

		newUserIdMapper.setUserId(RandomTestUtil.nextLong());

		newUserIdMapper.setType(RandomTestUtil.randomString());

		newUserIdMapper.setDescription(RandomTestUtil.randomString());

		newUserIdMapper.setExternalUserId(RandomTestUtil.randomString());

		_userIdMappers.add(_persistence.update(newUserIdMapper));

		UserIdMapper existingUserIdMapper = _persistence.findByPrimaryKey(
			newUserIdMapper.getPrimaryKey());

		Assert.assertEquals(
			existingUserIdMapper.getMvccVersion(),
			newUserIdMapper.getMvccVersion());
		Assert.assertEquals(
			existingUserIdMapper.getUserIdMapperId(),
			newUserIdMapper.getUserIdMapperId());
		Assert.assertEquals(
			existingUserIdMapper.getCompanyId(),
			newUserIdMapper.getCompanyId());
		Assert.assertEquals(
			existingUserIdMapper.getUserId(), newUserIdMapper.getUserId());
		Assert.assertEquals(
			existingUserIdMapper.getType(), newUserIdMapper.getType());
		Assert.assertEquals(
			existingUserIdMapper.getDescription(),
			newUserIdMapper.getDescription());
		Assert.assertEquals(
			existingUserIdMapper.getExternalUserId(),
			newUserIdMapper.getExternalUserId());
	}

	@Test
	public void testCountByUserId() throws Exception {
		_persistence.countByUserId(RandomTestUtil.nextLong());

		_persistence.countByUserId(0L);
	}

	@Test
	public void testCountByU_T() throws Exception {
		_persistence.countByU_T(RandomTestUtil.nextLong(), "");

		_persistence.countByU_T(0L, "null");

		_persistence.countByU_T(0L, (String)null);
	}

	@Test
	public void testCountByT_E() throws Exception {
		_persistence.countByT_E("", "");

		_persistence.countByT_E("null", "null");

		_persistence.countByT_E((String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		UserIdMapper newUserIdMapper = addUserIdMapper();

		UserIdMapper existingUserIdMapper = _persistence.findByPrimaryKey(
			newUserIdMapper.getPrimaryKey());

		Assert.assertEquals(existingUserIdMapper, newUserIdMapper);
	}

	@Test(expected = NoSuchUserIdMapperException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<UserIdMapper> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"UserIdMapper", "mvccVersion", true, "userIdMapperId", true,
			"companyId", true, "userId", true, "type", true, "description",
			true, "externalUserId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		UserIdMapper newUserIdMapper = addUserIdMapper();

		UserIdMapper existingUserIdMapper = _persistence.fetchByPrimaryKey(
			newUserIdMapper.getPrimaryKey());

		Assert.assertEquals(existingUserIdMapper, newUserIdMapper);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserIdMapper missingUserIdMapper = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingUserIdMapper);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		UserIdMapper newUserIdMapper1 = addUserIdMapper();
		UserIdMapper newUserIdMapper2 = addUserIdMapper();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserIdMapper1.getPrimaryKey());
		primaryKeys.add(newUserIdMapper2.getPrimaryKey());

		Map<Serializable, UserIdMapper> userIdMappers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, userIdMappers.size());
		Assert.assertEquals(
			newUserIdMapper1,
			userIdMappers.get(newUserIdMapper1.getPrimaryKey()));
		Assert.assertEquals(
			newUserIdMapper2,
			userIdMappers.get(newUserIdMapper2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, UserIdMapper> userIdMappers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userIdMappers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		UserIdMapper newUserIdMapper = addUserIdMapper();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserIdMapper.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, UserIdMapper> userIdMappers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userIdMappers.size());
		Assert.assertEquals(
			newUserIdMapper,
			userIdMappers.get(newUserIdMapper.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, UserIdMapper> userIdMappers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(userIdMappers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		UserIdMapper newUserIdMapper = addUserIdMapper();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newUserIdMapper.getPrimaryKey());

		Map<Serializable, UserIdMapper> userIdMappers =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, userIdMappers.size());
		Assert.assertEquals(
			newUserIdMapper,
			userIdMappers.get(newUserIdMapper.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		UserIdMapper newUserIdMapper = addUserIdMapper();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newUserIdMapper.getPrimaryKey()));
	}

	private void _assertOriginalValues(UserIdMapper userIdMapper) {
		Assert.assertEquals(
			Long.valueOf(userIdMapper.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				userIdMapper, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			userIdMapper.getType(),
			ReflectionTestUtil.invoke(
				userIdMapper, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));

		Assert.assertEquals(
			userIdMapper.getType(),
			ReflectionTestUtil.invoke(
				userIdMapper, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
		Assert.assertEquals(
			userIdMapper.getExternalUserId(),
			ReflectionTestUtil.invoke(
				userIdMapper, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalUserId"));
	}

	protected UserIdMapper addUserIdMapper() throws Exception {
		long pk = RandomTestUtil.nextLong();

		UserIdMapper userIdMapper = _persistence.create(pk);

		userIdMapper.setMvccVersion(RandomTestUtil.nextLong());

		userIdMapper.setCompanyId(RandomTestUtil.nextLong());

		userIdMapper.setUserId(RandomTestUtil.nextLong());

		userIdMapper.setType(RandomTestUtil.randomString());

		userIdMapper.setDescription(RandomTestUtil.randomString());

		userIdMapper.setExternalUserId(RandomTestUtil.randomString());

		_userIdMappers.add(_persistence.update(userIdMapper));

		return userIdMapper;
	}

	private List<UserIdMapper> _userIdMappers = new ArrayList<UserIdMapper>();
	private UserIdMapperPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}