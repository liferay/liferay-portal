/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutFriendlyURLException;
import com.liferay.portal.kernel.model.LayoutFriendlyURL;
import com.liferay.portal.kernel.service.persistence.LayoutFriendlyURLPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutFriendlyURLUtil;
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
public class LayoutFriendlyURLPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = LayoutFriendlyURLUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutFriendlyURL> iterator = _layoutFriendlyURLs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutFriendlyURL layoutFriendlyURL = _persistence.create(pk);

		Assert.assertNotNull(layoutFriendlyURL);

		Assert.assertEquals(layoutFriendlyURL.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutFriendlyURL newLayoutFriendlyURL = addLayoutFriendlyURL();

		_persistence.remove(newLayoutFriendlyURL);

		LayoutFriendlyURL existingLayoutFriendlyURL =
			_persistence.fetchByPrimaryKey(
				newLayoutFriendlyURL.getPrimaryKey());

		Assert.assertNull(existingLayoutFriendlyURL);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutFriendlyURL();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutFriendlyURL newLayoutFriendlyURL = _persistence.create(pk);

		newLayoutFriendlyURL.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutFriendlyURL.setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutFriendlyURL.setUuid(RandomTestUtil.randomString());

		newLayoutFriendlyURL.setGroupId(RandomTestUtil.nextLong());

		newLayoutFriendlyURL.setCompanyId(RandomTestUtil.nextLong());

		newLayoutFriendlyURL.setUserId(RandomTestUtil.nextLong());

		newLayoutFriendlyURL.setUserName(RandomTestUtil.randomString());

		newLayoutFriendlyURL.setCreateDate(RandomTestUtil.nextDate());

		newLayoutFriendlyURL.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutFriendlyURL.setPlid(RandomTestUtil.nextLong());

		newLayoutFriendlyURL.setPrivateLayout(RandomTestUtil.randomBoolean());

		newLayoutFriendlyURL.setFriendlyURL(RandomTestUtil.randomString());

		newLayoutFriendlyURL.setLanguageId(RandomTestUtil.randomString());

		newLayoutFriendlyURL.setLastPublishDate(RandomTestUtil.nextDate());

		_layoutFriendlyURLs.add(_persistence.update(newLayoutFriendlyURL));

		LayoutFriendlyURL existingLayoutFriendlyURL =
			_persistence.findByPrimaryKey(newLayoutFriendlyURL.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutFriendlyURL.getMvccVersion(),
			newLayoutFriendlyURL.getMvccVersion());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getCtCollectionId(),
			newLayoutFriendlyURL.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getUuid(),
			newLayoutFriendlyURL.getUuid());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getLayoutFriendlyURLId(),
			newLayoutFriendlyURL.getLayoutFriendlyURLId());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getGroupId(),
			newLayoutFriendlyURL.getGroupId());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getCompanyId(),
			newLayoutFriendlyURL.getCompanyId());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getUserId(),
			newLayoutFriendlyURL.getUserId());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getUserName(),
			newLayoutFriendlyURL.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutFriendlyURL.getCreateDate()),
			Time.getShortTimestamp(newLayoutFriendlyURL.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutFriendlyURL.getModifiedDate()),
			Time.getShortTimestamp(newLayoutFriendlyURL.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutFriendlyURL.getPlid(),
			newLayoutFriendlyURL.getPlid());
		Assert.assertEquals(
			existingLayoutFriendlyURL.isPrivateLayout(),
			newLayoutFriendlyURL.isPrivateLayout());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getFriendlyURL(),
			newLayoutFriendlyURL.getFriendlyURL());
		Assert.assertEquals(
			existingLayoutFriendlyURL.getLanguageId(),
			newLayoutFriendlyURL.getLanguageId());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingLayoutFriendlyURL.getLastPublishDate()),
			Time.getShortTimestamp(newLayoutFriendlyURL.getLastPublishDate()));
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByPlid() throws Exception {
		_persistence.countByPlid(RandomTestUtil.nextLong());

		_persistence.countByPlid(0L);
	}

	@Test
	public void testCountByC_F() throws Exception {
		_persistence.countByC_F(RandomTestUtil.nextLong(), "");

		_persistence.countByC_F(0L, "null");

		_persistence.countByC_F(0L, (String)null);
	}

	@Test
	public void testCountByP_F() throws Exception {
		_persistence.countByP_F(RandomTestUtil.nextLong(), "");

		_persistence.countByP_F(0L, "null");

		_persistence.countByP_F(0L, (String)null);
	}

	@Test
	public void testCountByP_L() throws Exception {
		_persistence.countByP_L(RandomTestUtil.nextLong(), "");

		_persistence.countByP_L(0L, "null");

		_persistence.countByP_L(0L, (String)null);
	}

	@Test
	public void testCountByP_LArrayable() throws Exception {
		_persistence.countByP_L(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString());
	}

	@Test
	public void testCountByG_P_F() throws Exception {
		_persistence.countByG_P_F(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_P_F(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_P_F(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_P_F_L() throws Exception {
		_persistence.countByG_P_F_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "", "");

		_persistence.countByG_P_F_L(
			0L, RandomTestUtil.randomBoolean(), "null", "null");

		_persistence.countByG_P_F_L(
			0L, RandomTestUtil.randomBoolean(), (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutFriendlyURL newLayoutFriendlyURL = addLayoutFriendlyURL();

		LayoutFriendlyURL existingLayoutFriendlyURL =
			_persistence.findByPrimaryKey(newLayoutFriendlyURL.getPrimaryKey());

		Assert.assertEquals(existingLayoutFriendlyURL, newLayoutFriendlyURL);
	}

	@Test(expected = NoSuchLayoutFriendlyURLException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutFriendlyURL> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutFriendlyURL", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "layoutFriendlyURLId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "plid", true, "privateLayout", true,
			"friendlyURL", true, "languageId", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutFriendlyURL newLayoutFriendlyURL = addLayoutFriendlyURL();

		LayoutFriendlyURL existingLayoutFriendlyURL =
			_persistence.fetchByPrimaryKey(
				newLayoutFriendlyURL.getPrimaryKey());

		Assert.assertEquals(existingLayoutFriendlyURL, newLayoutFriendlyURL);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutFriendlyURL missingLayoutFriendlyURL =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutFriendlyURL);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutFriendlyURL newLayoutFriendlyURL1 = addLayoutFriendlyURL();
		LayoutFriendlyURL newLayoutFriendlyURL2 = addLayoutFriendlyURL();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutFriendlyURL1.getPrimaryKey());
		primaryKeys.add(newLayoutFriendlyURL2.getPrimaryKey());

		Map<Serializable, LayoutFriendlyURL> layoutFriendlyURLs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutFriendlyURLs.size());
		Assert.assertEquals(
			newLayoutFriendlyURL1,
			layoutFriendlyURLs.get(newLayoutFriendlyURL1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutFriendlyURL2,
			layoutFriendlyURLs.get(newLayoutFriendlyURL2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutFriendlyURL> layoutFriendlyURLs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutFriendlyURLs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutFriendlyURL newLayoutFriendlyURL = addLayoutFriendlyURL();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutFriendlyURL.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutFriendlyURL> layoutFriendlyURLs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutFriendlyURLs.size());
		Assert.assertEquals(
			newLayoutFriendlyURL,
			layoutFriendlyURLs.get(newLayoutFriendlyURL.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutFriendlyURL> layoutFriendlyURLs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutFriendlyURLs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutFriendlyURL newLayoutFriendlyURL = addLayoutFriendlyURL();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutFriendlyURL.getPrimaryKey());

		Map<Serializable, LayoutFriendlyURL> layoutFriendlyURLs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutFriendlyURLs.size());
		Assert.assertEquals(
			newLayoutFriendlyURL,
			layoutFriendlyURLs.get(newLayoutFriendlyURL.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutFriendlyURL newLayoutFriendlyURL = addLayoutFriendlyURL();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLayoutFriendlyURL.getPrimaryKey()));
	}

	private void _assertOriginalValues(LayoutFriendlyURL layoutFriendlyURL) {
		Assert.assertEquals(
			layoutFriendlyURL.getUuid(),
			ReflectionTestUtil.invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(layoutFriendlyURL.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(layoutFriendlyURL.getPlid()),
			ReflectionTestUtil.<Long>invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "plid"));
		Assert.assertEquals(
			layoutFriendlyURL.getLanguageId(),
			ReflectionTestUtil.invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));

		Assert.assertEquals(
			Long.valueOf(layoutFriendlyURL.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layoutFriendlyURL.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "privateLayout"));
		Assert.assertEquals(
			layoutFriendlyURL.getFriendlyURL(),
			ReflectionTestUtil.invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "friendlyURL"));
		Assert.assertEquals(
			layoutFriendlyURL.getLanguageId(),
			ReflectionTestUtil.invoke(
				layoutFriendlyURL, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected LayoutFriendlyURL addLayoutFriendlyURL() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutFriendlyURL layoutFriendlyURL = _persistence.create(pk);

		layoutFriendlyURL.setMvccVersion(RandomTestUtil.nextLong());

		layoutFriendlyURL.setCtCollectionId(RandomTestUtil.nextLong());

		layoutFriendlyURL.setUuid(RandomTestUtil.randomString());

		layoutFriendlyURL.setGroupId(RandomTestUtil.nextLong());

		layoutFriendlyURL.setCompanyId(RandomTestUtil.nextLong());

		layoutFriendlyURL.setUserId(RandomTestUtil.nextLong());

		layoutFriendlyURL.setUserName(RandomTestUtil.randomString());

		layoutFriendlyURL.setCreateDate(RandomTestUtil.nextDate());

		layoutFriendlyURL.setModifiedDate(RandomTestUtil.nextDate());

		layoutFriendlyURL.setPlid(RandomTestUtil.nextLong());

		layoutFriendlyURL.setPrivateLayout(RandomTestUtil.randomBoolean());

		layoutFriendlyURL.setFriendlyURL(RandomTestUtil.randomString());

		layoutFriendlyURL.setLanguageId(RandomTestUtil.randomString());

		layoutFriendlyURL.setLastPublishDate(RandomTestUtil.nextDate());

		_layoutFriendlyURLs.add(_persistence.update(layoutFriendlyURL));

		return layoutFriendlyURL;
	}

	private List<LayoutFriendlyURL> _layoutFriendlyURLs =
		new ArrayList<LayoutFriendlyURL>();
	private LayoutFriendlyURLPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}