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
import com.liferay.social.kernel.exception.NoSuchActivityAchievementException;
import com.liferay.social.kernel.model.SocialActivityAchievement;
import com.liferay.social.kernel.service.persistence.SocialActivityAchievementPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivityAchievementUtil;

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
public class SocialActivityAchievementPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = SocialActivityAchievementUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SocialActivityAchievement> iterator =
			_socialActivityAchievements.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityAchievement socialActivityAchievement =
			_persistence.create(pk);

		Assert.assertNotNull(socialActivityAchievement);

		Assert.assertEquals(socialActivityAchievement.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SocialActivityAchievement newSocialActivityAchievement =
			addSocialActivityAchievement();

		_persistence.remove(newSocialActivityAchievement);

		SocialActivityAchievement existingSocialActivityAchievement =
			_persistence.fetchByPrimaryKey(
				newSocialActivityAchievement.getPrimaryKey());

		Assert.assertNull(existingSocialActivityAchievement);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSocialActivityAchievement();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityAchievement newSocialActivityAchievement =
			_persistence.create(pk);

		newSocialActivityAchievement.setMvccVersion(RandomTestUtil.nextLong());

		newSocialActivityAchievement.setCtCollectionId(
			RandomTestUtil.nextLong());

		newSocialActivityAchievement.setGroupId(RandomTestUtil.nextLong());

		newSocialActivityAchievement.setCompanyId(RandomTestUtil.nextLong());

		newSocialActivityAchievement.setUserId(RandomTestUtil.nextLong());

		newSocialActivityAchievement.setCreateDate(RandomTestUtil.nextLong());

		newSocialActivityAchievement.setName(RandomTestUtil.randomString());

		newSocialActivityAchievement.setFirstInGroup(
			RandomTestUtil.randomBoolean());

		_socialActivityAchievements.add(
			_persistence.update(newSocialActivityAchievement));

		SocialActivityAchievement existingSocialActivityAchievement =
			_persistence.findByPrimaryKey(
				newSocialActivityAchievement.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivityAchievement.getMvccVersion(),
			newSocialActivityAchievement.getMvccVersion());
		Assert.assertEquals(
			existingSocialActivityAchievement.getCtCollectionId(),
			newSocialActivityAchievement.getCtCollectionId());
		Assert.assertEquals(
			existingSocialActivityAchievement.getActivityAchievementId(),
			newSocialActivityAchievement.getActivityAchievementId());
		Assert.assertEquals(
			existingSocialActivityAchievement.getGroupId(),
			newSocialActivityAchievement.getGroupId());
		Assert.assertEquals(
			existingSocialActivityAchievement.getCompanyId(),
			newSocialActivityAchievement.getCompanyId());
		Assert.assertEquals(
			existingSocialActivityAchievement.getUserId(),
			newSocialActivityAchievement.getUserId());
		Assert.assertEquals(
			existingSocialActivityAchievement.getCreateDate(),
			newSocialActivityAchievement.getCreateDate());
		Assert.assertEquals(
			existingSocialActivityAchievement.getName(),
			newSocialActivityAchievement.getName());
		Assert.assertEquals(
			existingSocialActivityAchievement.isFirstInGroup(),
			newSocialActivityAchievement.isFirstInGroup());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_U() throws Exception {
		_persistence.countByG_U(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_U(0L, 0L);
	}

	@Test
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByG_F() throws Exception {
		_persistence.countByG_F(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_F(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_U_N() throws Exception {
		_persistence.countByG_U_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByG_U_N(0L, 0L, "null");

		_persistence.countByG_U_N(0L, 0L, (String)null);
	}

	@Test
	public void testCountByG_U_F() throws Exception {
		_persistence.countByG_U_F(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_U_F(0L, 0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SocialActivityAchievement newSocialActivityAchievement =
			addSocialActivityAchievement();

		SocialActivityAchievement existingSocialActivityAchievement =
			_persistence.findByPrimaryKey(
				newSocialActivityAchievement.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivityAchievement, newSocialActivityAchievement);
	}

	@Test(expected = NoSuchActivityAchievementException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SocialActivityAchievement>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"SocialActivityAchievement", "mvccVersion", true, "ctCollectionId",
			true, "activityAchievementId", true, "groupId", true, "companyId",
			true, "userId", true, "createDate", true, "name", true,
			"firstInGroup", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SocialActivityAchievement newSocialActivityAchievement =
			addSocialActivityAchievement();

		SocialActivityAchievement existingSocialActivityAchievement =
			_persistence.fetchByPrimaryKey(
				newSocialActivityAchievement.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivityAchievement, newSocialActivityAchievement);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivityAchievement missingSocialActivityAchievement =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSocialActivityAchievement);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SocialActivityAchievement newSocialActivityAchievement1 =
			addSocialActivityAchievement();
		SocialActivityAchievement newSocialActivityAchievement2 =
			addSocialActivityAchievement();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivityAchievement1.getPrimaryKey());
		primaryKeys.add(newSocialActivityAchievement2.getPrimaryKey());

		Map<Serializable, SocialActivityAchievement>
			socialActivityAchievements = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, socialActivityAchievements.size());
		Assert.assertEquals(
			newSocialActivityAchievement1,
			socialActivityAchievements.get(
				newSocialActivityAchievement1.getPrimaryKey()));
		Assert.assertEquals(
			newSocialActivityAchievement2,
			socialActivityAchievements.get(
				newSocialActivityAchievement2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SocialActivityAchievement>
			socialActivityAchievements = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(socialActivityAchievements.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SocialActivityAchievement newSocialActivityAchievement =
			addSocialActivityAchievement();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivityAchievement.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SocialActivityAchievement>
			socialActivityAchievements = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, socialActivityAchievements.size());
		Assert.assertEquals(
			newSocialActivityAchievement,
			socialActivityAchievements.get(
				newSocialActivityAchievement.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SocialActivityAchievement>
			socialActivityAchievements = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(socialActivityAchievements.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SocialActivityAchievement newSocialActivityAchievement =
			addSocialActivityAchievement();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivityAchievement.getPrimaryKey());

		Map<Serializable, SocialActivityAchievement>
			socialActivityAchievements = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, socialActivityAchievements.size());
		Assert.assertEquals(
			newSocialActivityAchievement,
			socialActivityAchievements.get(
				newSocialActivityAchievement.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SocialActivityAchievement newSocialActivityAchievement =
			addSocialActivityAchievement();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSocialActivityAchievement.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		SocialActivityAchievement socialActivityAchievement) {

		Assert.assertEquals(
			Long.valueOf(socialActivityAchievement.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivityAchievement, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(socialActivityAchievement.getUserId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivityAchievement, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "userId"));
		Assert.assertEquals(
			socialActivityAchievement.getName(),
			ReflectionTestUtil.invoke(
				socialActivityAchievement, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected SocialActivityAchievement addSocialActivityAchievement()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		SocialActivityAchievement socialActivityAchievement =
			_persistence.create(pk);

		socialActivityAchievement.setMvccVersion(RandomTestUtil.nextLong());

		socialActivityAchievement.setCtCollectionId(RandomTestUtil.nextLong());

		socialActivityAchievement.setGroupId(RandomTestUtil.nextLong());

		socialActivityAchievement.setCompanyId(RandomTestUtil.nextLong());

		socialActivityAchievement.setUserId(RandomTestUtil.nextLong());

		socialActivityAchievement.setCreateDate(RandomTestUtil.nextLong());

		socialActivityAchievement.setName(RandomTestUtil.randomString());

		socialActivityAchievement.setFirstInGroup(
			RandomTestUtil.randomBoolean());

		_socialActivityAchievements.add(
			_persistence.update(socialActivityAchievement));

		return socialActivityAchievement;
	}

	private List<SocialActivityAchievement> _socialActivityAchievements =
		new ArrayList<SocialActivityAchievement>();
	private SocialActivityAchievementPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}