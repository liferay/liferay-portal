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
import com.liferay.social.kernel.exception.NoSuchActivitySettingException;
import com.liferay.social.kernel.model.SocialActivitySetting;
import com.liferay.social.kernel.service.persistence.SocialActivitySettingPersistence;
import com.liferay.social.kernel.service.persistence.SocialActivitySettingUtil;

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
public class SocialActivitySettingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = SocialActivitySettingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SocialActivitySetting> iterator =
			_socialActivitySettings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivitySetting socialActivitySetting = _persistence.create(pk);

		Assert.assertNotNull(socialActivitySetting);

		Assert.assertEquals(socialActivitySetting.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SocialActivitySetting newSocialActivitySetting =
			addSocialActivitySetting();

		_persistence.remove(newSocialActivitySetting);

		SocialActivitySetting existingSocialActivitySetting =
			_persistence.fetchByPrimaryKey(
				newSocialActivitySetting.getPrimaryKey());

		Assert.assertNull(existingSocialActivitySetting);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSocialActivitySetting();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivitySetting newSocialActivitySetting = _persistence.create(
			pk);

		newSocialActivitySetting.setMvccVersion(RandomTestUtil.nextLong());

		newSocialActivitySetting.setCtCollectionId(RandomTestUtil.nextLong());

		newSocialActivitySetting.setGroupId(RandomTestUtil.nextLong());

		newSocialActivitySetting.setCompanyId(RandomTestUtil.nextLong());

		newSocialActivitySetting.setClassNameId(RandomTestUtil.nextLong());

		newSocialActivitySetting.setActivityType(RandomTestUtil.nextInt());

		newSocialActivitySetting.setName(RandomTestUtil.randomString());

		newSocialActivitySetting.setValue(RandomTestUtil.randomString());

		_socialActivitySettings.add(
			_persistence.update(newSocialActivitySetting));

		SocialActivitySetting existingSocialActivitySetting =
			_persistence.findByPrimaryKey(
				newSocialActivitySetting.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivitySetting.getMvccVersion(),
			newSocialActivitySetting.getMvccVersion());
		Assert.assertEquals(
			existingSocialActivitySetting.getCtCollectionId(),
			newSocialActivitySetting.getCtCollectionId());
		Assert.assertEquals(
			existingSocialActivitySetting.getActivitySettingId(),
			newSocialActivitySetting.getActivitySettingId());
		Assert.assertEquals(
			existingSocialActivitySetting.getGroupId(),
			newSocialActivitySetting.getGroupId());
		Assert.assertEquals(
			existingSocialActivitySetting.getCompanyId(),
			newSocialActivitySetting.getCompanyId());
		Assert.assertEquals(
			existingSocialActivitySetting.getClassNameId(),
			newSocialActivitySetting.getClassNameId());
		Assert.assertEquals(
			existingSocialActivitySetting.getActivityType(),
			newSocialActivitySetting.getActivityType());
		Assert.assertEquals(
			existingSocialActivitySetting.getName(),
			newSocialActivitySetting.getName());
		Assert.assertEquals(
			existingSocialActivitySetting.getValue(),
			newSocialActivitySetting.getValue());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_A(0L, 0);
	}

	@Test
	public void testCountByG_C_A() throws Exception {
		_persistence.countByG_C_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_A(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_A_N() throws Exception {
		_persistence.countByG_C_A_N(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt(), "");

		_persistence.countByG_C_A_N(0L, 0L, 0, "null");

		_persistence.countByG_C_A_N(0L, 0L, 0, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SocialActivitySetting newSocialActivitySetting =
			addSocialActivitySetting();

		SocialActivitySetting existingSocialActivitySetting =
			_persistence.findByPrimaryKey(
				newSocialActivitySetting.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivitySetting, newSocialActivitySetting);
	}

	@Test(expected = NoSuchActivitySettingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SocialActivitySetting> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SocialActivitySetting", "mvccVersion", true, "ctCollectionId",
			true, "activitySettingId", true, "groupId", true, "companyId", true,
			"classNameId", true, "activityType", true, "name", true, "value",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SocialActivitySetting newSocialActivitySetting =
			addSocialActivitySetting();

		SocialActivitySetting existingSocialActivitySetting =
			_persistence.fetchByPrimaryKey(
				newSocialActivitySetting.getPrimaryKey());

		Assert.assertEquals(
			existingSocialActivitySetting, newSocialActivitySetting);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SocialActivitySetting missingSocialActivitySetting =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSocialActivitySetting);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SocialActivitySetting newSocialActivitySetting1 =
			addSocialActivitySetting();
		SocialActivitySetting newSocialActivitySetting2 =
			addSocialActivitySetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivitySetting1.getPrimaryKey());
		primaryKeys.add(newSocialActivitySetting2.getPrimaryKey());

		Map<Serializable, SocialActivitySetting> socialActivitySettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, socialActivitySettings.size());
		Assert.assertEquals(
			newSocialActivitySetting1,
			socialActivitySettings.get(
				newSocialActivitySetting1.getPrimaryKey()));
		Assert.assertEquals(
			newSocialActivitySetting2,
			socialActivitySettings.get(
				newSocialActivitySetting2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SocialActivitySetting> socialActivitySettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(socialActivitySettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SocialActivitySetting newSocialActivitySetting =
			addSocialActivitySetting();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivitySetting.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SocialActivitySetting> socialActivitySettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, socialActivitySettings.size());
		Assert.assertEquals(
			newSocialActivitySetting,
			socialActivitySettings.get(
				newSocialActivitySetting.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SocialActivitySetting> socialActivitySettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(socialActivitySettings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SocialActivitySetting newSocialActivitySetting =
			addSocialActivitySetting();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSocialActivitySetting.getPrimaryKey());

		Map<Serializable, SocialActivitySetting> socialActivitySettings =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, socialActivitySettings.size());
		Assert.assertEquals(
			newSocialActivitySetting,
			socialActivitySettings.get(
				newSocialActivitySetting.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SocialActivitySetting newSocialActivitySetting =
			addSocialActivitySetting();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSocialActivitySetting.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		SocialActivitySetting socialActivitySetting) {

		Assert.assertEquals(
			Long.valueOf(socialActivitySetting.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivitySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Long.valueOf(socialActivitySetting.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				socialActivitySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Integer.valueOf(socialActivitySetting.getActivityType()),
			ReflectionTestUtil.<Integer>invoke(
				socialActivitySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "activityType"));
		Assert.assertEquals(
			socialActivitySetting.getName(),
			ReflectionTestUtil.invoke(
				socialActivitySetting, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected SocialActivitySetting addSocialActivitySetting()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		SocialActivitySetting socialActivitySetting = _persistence.create(pk);

		socialActivitySetting.setMvccVersion(RandomTestUtil.nextLong());

		socialActivitySetting.setCtCollectionId(RandomTestUtil.nextLong());

		socialActivitySetting.setGroupId(RandomTestUtil.nextLong());

		socialActivitySetting.setCompanyId(RandomTestUtil.nextLong());

		socialActivitySetting.setClassNameId(RandomTestUtil.nextLong());

		socialActivitySetting.setActivityType(RandomTestUtil.nextInt());

		socialActivitySetting.setName(RandomTestUtil.randomString());

		socialActivitySetting.setValue(RandomTestUtil.randomString());

		_socialActivitySettings.add(_persistence.update(socialActivitySetting));

		return socialActivitySetting;
	}

	private List<SocialActivitySetting> _socialActivitySettings =
		new ArrayList<SocialActivitySetting>();
	private SocialActivitySettingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}