/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portlet.social.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.social.kernel.exception.NoSuchActivityLimitException;
import com.liferay.social.kernel.model.SocialActivityLimit;
import com.liferay.social.kernel.service.persistence.SocialActivityLimitPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityLimitUtil;

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
public class SocialActivityLimitPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = SocialActivityLimitUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SocialActivityLimit> iterator =
			_socialActivityLimits.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityLimit socialActivityLimit = _persistence.create(pk);

		Assert.assertNotNull(socialActivityLimit);

		Assert.assertEquals(socialActivityLimit.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SocialActivityLimit newSocialActivityLimit = addSocialActivityLimit();

		_persistence.remove(newSocialActivityLimit);

		SocialActivityLimit existingSocialActivityLimit =
			_persistence.fetchByPrimaryKey(
				newSocialActivityLimit.getPrimaryKey());

		Assert.assertNull(existingSocialActivityLimit);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSocialActivityLimit();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityLimit newSocialActivityLimit = _persistence.create(pk);

		newSocialActivityLimit.setMvccVersion(RandomTestUtil.nextLong());

		newSocialActivityLimit.setCtCollectionId(RandomTestUtil.nextLong());

		newSocialActivityLimit.setGroupId(RandomTestUtil.nextLong());

		newSocialActivityLimit.setCompanyId(RandomTestUtil.nextLong());

		newSocialActivityLimit.setUserId(RandomTestUtil.nextLong());

		newSocialActivityLimit.setClassNameId(RandomTestUtil.nextLong());

		newSocialActivityLimit.setClassPK(RandomTestUtil.nextLong());

		newSocialActivityLimit.setActivityType(RandomTestUtil.nextInt());

		newSocialActivityLimit.setActivityCounterName(
			RandomTestUtil.randomString());

		newSocialActivityLimit.setValue(RandomTestUtil.randomString());

		_socialActivityLimits.add(_persistence.update(newSocialActivityLimit));

		SocialActivityLimit existingSocialActivityLimit =
			_persistence.findByPrimaryKey(
				newSocialActivityLimit.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivityLimit.getMvccVersion(),
			newSocialActivityLimit.getMvccVersion());
		Assert.assertEquals(
			existingSocialActivityLimit.getCtCollectionId(),
			newSocialActivityLimit.getCtCollectionId());
		Assert.assertEquals(
			existingSocialActivityLimit.getActivityLimitId(),
			newSocialActivityLimit.getActivityLimitId());
		Assert.assertEquals(
			existingSocialActivityLimit.getGroupId(),
			newSocialActivityLimit.getGroupId());
		Assert.assertEquals(
			existingSocialActivityLimit.getCompanyId(),
			newSocialActivityLimit.getCompanyId());
		Assert.assertEquals(
			existingSocialActivityLimit.getUserId(),
			newSocialActivityLimit.getUserId());
		Assert.assertEquals(
			existingSocialActivityLimit.getClassNameId(),
			newSocialActivityLimit.getClassNameId());
		Assert.assertEquals(
			existingSocialActivityLimit.getClassPK(),
			newSocialActivityLimit.getClassPK());
		Assert.assertEquals(
			existingSocialActivityLimit.getActivityType(),
			newSocialActivityLimit.getActivityType());
		Assert.assertEquals(
			existingSocialActivityLimit.getActivityCounterName(),
			newSocialActivityLimit.getActivityCounterName());
		Assert.assertEquals(
			existingSocialActivityLimit.getValue(),
			newSocialActivityLimit.getValue());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
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
	public void testCountByG_U_C_C_A_A() throws Exception {
		_persistence.countByG_U_C_C_A_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), "");

		_persistence.countByG_U_C_C_A_A(0L, 0L, 0L, 0L, 0, "null");

		_persistence.countByG_U_C_C_A_A(0L, 0L, 0L, 0L, 0, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SocialActivityLimit newSocialActivityLimit = addSocialActivityLimit();

		SocialActivityLimit existingSocialActivityLimit =
			_persistence.findByPrimaryKey(
				newSocialActivityLimit.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivityLimit, newSocialActivityLimit);
	}

	@Test(expected = NoSuchActivityLimitException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SocialActivityLimit> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SocialActivityLimit", "mvccVersion", true, "ctCollectionId", true,
			"activityLimitId", true, "groupId", true, "companyId", true,
			"userId", true, "classNameId", true, "classPK", true,
			"activityType", true, "activityCounterName", true, "value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SocialActivityLimit newSocialActivityLimit = addSocialActivityLimit();

		SocialActivityLimit existingSocialActivityLimit =
			_persistence.fetchByPrimaryKey(
				newSocialActivityLimit.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivityLimit, newSocialActivityLimit);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityLimit missingSocialActivityLimit =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSocialActivityLimit);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SocialActivityLimit newSocialActivityLimit1 = addSocialActivityLimit();
		SocialActivityLimit newSocialActivityLimit2 = addSocialActivityLimit();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivityLimit1.getPrimaryKey());
		primaryKeys.add(newSocialActivityLimit2.getPrimaryKey());

		Map<Serializable, SocialActivityLimit> socialActivityLimits =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, socialActivityLimits.size());
		Assert.assertEquals(
			newSocialActivityLimit1,
			socialActivityLimits.get(newSocialActivityLimit1.getPrimaryKey()));
		Assert.assertEquals(
			newSocialActivityLimit2,
			socialActivityLimits.get(newSocialActivityLimit2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SocialActivityLimit> socialActivityLimits =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(socialActivityLimits.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SocialActivityLimit newSocialActivityLimit = addSocialActivityLimit();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivityLimit.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SocialActivityLimit> socialActivityLimits =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, socialActivityLimits.size());
		Assert.assertEquals(
			newSocialActivityLimit,
			socialActivityLimits.get(newSocialActivityLimit.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SocialActivityLimit> socialActivityLimits =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(socialActivityLimits.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SocialActivityLimit newSocialActivityLimit = addSocialActivityLimit();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivityLimit.getPrimaryKey());

		Map<Serializable, SocialActivityLimit> socialActivityLimits =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, socialActivityLimits.size());
		Assert.assertEquals(
			newSocialActivityLimit,
			socialActivityLimits.get(newSocialActivityLimit.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SocialActivityLimit newSocialActivityLimit = addSocialActivityLimit();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSocialActivityLimit.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		SocialActivityLimit socialActivityLimit) {

		Assert.assertEquals(
			Long.valueOf(socialActivityLimit.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivityLimit, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(socialActivityLimit.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivityLimit, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			Long.valueOf(socialActivityLimit.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivityLimit, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(socialActivityLimit.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				socialActivityLimit, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Integer.valueOf(socialActivityLimit.getActivityType()),
			ReflectionTestUtil.<Integer>invoke(
				socialActivityLimit, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "activityType"));
		Assert.assertEquals(
			socialActivityLimit.getActivityCounterName(),
			ReflectionTestUtil.invoke(
				socialActivityLimit, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "activityCounterName"));
	}

	protected SocialActivityLimit addSocialActivityLimit() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityLimit socialActivityLimit = _persistence.create(pk);

		socialActivityLimit.setMvccVersion(RandomTestUtil.nextLong());

		socialActivityLimit.setCtCollectionId(RandomTestUtil.nextLong());

		socialActivityLimit.setGroupId(RandomTestUtil.nextLong());

		socialActivityLimit.setCompanyId(RandomTestUtil.nextLong());

		socialActivityLimit.setUserId(RandomTestUtil.nextLong());

		socialActivityLimit.setClassNameId(RandomTestUtil.nextLong());

		socialActivityLimit.setClassPK(RandomTestUtil.nextLong());

		socialActivityLimit.setActivityType(RandomTestUtil.nextInt());

		socialActivityLimit.setActivityCounterName(
			RandomTestUtil.randomString());

		socialActivityLimit.setValue(RandomTestUtil.randomString());

		_socialActivityLimits.add(_persistence.update(socialActivityLimit));

		return socialActivityLimit;
	}

	private List<SocialActivityLimit> _socialActivityLimits =
		new ArrayList<SocialActivityLimit>();
	private SocialActivityLimitPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}