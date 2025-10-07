/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.site.navigation.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.site.navigation.exception.DuplicateSiteNavigationMenuItemExternalReferenceCodeException;
import com.liferay.site.navigation.exception.NoSuchMenuItemException;
import com.liferay.site.navigation.model.SiteNavigationMenuItem;
import com.liferay.site.navigation.service.SiteNavigationMenuItemLocalServiceUtil;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuItemPersistence;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuItemUtil;

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
public class SiteNavigationMenuItemPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.site.navigation.service"));

	@Before
	public void setUp() {
		_persistence = SiteNavigationMenuItemUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SiteNavigationMenuItem> iterator =
			_siteNavigationMenuItems.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenuItem siteNavigationMenuItem = _persistence.create(pk);

		Assert.assertNotNull(siteNavigationMenuItem);

		Assert.assertEquals(siteNavigationMenuItem.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		_persistence.remove(newSiteNavigationMenuItem);

		SiteNavigationMenuItem existingSiteNavigationMenuItem =
			_persistence.fetchByPrimaryKey(
				newSiteNavigationMenuItem.getPrimaryKey());

		Assert.assertNull(existingSiteNavigationMenuItem);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSiteNavigationMenuItem();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenuItem newSiteNavigationMenuItem = _persistence.create(
			pk);

		newSiteNavigationMenuItem.setMvccVersion(RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setCtCollectionId(RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setUuid(RandomTestUtil.randomString());

		newSiteNavigationMenuItem.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newSiteNavigationMenuItem.setGroupId(RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setCompanyId(RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setUserId(RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setUserName(RandomTestUtil.randomString());

		newSiteNavigationMenuItem.setCreateDate(RandomTestUtil.nextDate());

		newSiteNavigationMenuItem.setModifiedDate(RandomTestUtil.nextDate());

		newSiteNavigationMenuItem.setSiteNavigationMenuId(
			RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setParentSiteNavigationMenuItemId(
			RandomTestUtil.nextLong());

		newSiteNavigationMenuItem.setName(RandomTestUtil.randomString());

		newSiteNavigationMenuItem.setType(RandomTestUtil.randomString());

		newSiteNavigationMenuItem.setTypeSettings(
			RandomTestUtil.randomString());

		newSiteNavigationMenuItem.setOrder(RandomTestUtil.nextInt());

		newSiteNavigationMenuItem.setLastPublishDate(RandomTestUtil.nextDate());

		_siteNavigationMenuItems.add(
			_persistence.update(newSiteNavigationMenuItem));

		SiteNavigationMenuItem existingSiteNavigationMenuItem =
			_persistence.findByPrimaryKey(
				newSiteNavigationMenuItem.getPrimaryKey());

		Assert.assertEquals(
			existingSiteNavigationMenuItem.getMvccVersion(),
			newSiteNavigationMenuItem.getMvccVersion());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getCtCollectionId(),
			newSiteNavigationMenuItem.getCtCollectionId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getUuid(),
			newSiteNavigationMenuItem.getUuid());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getExternalReferenceCode(),
			newSiteNavigationMenuItem.getExternalReferenceCode());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getSiteNavigationMenuItemId(),
			newSiteNavigationMenuItem.getSiteNavigationMenuItemId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getGroupId(),
			newSiteNavigationMenuItem.getGroupId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getCompanyId(),
			newSiteNavigationMenuItem.getCompanyId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getUserId(),
			newSiteNavigationMenuItem.getUserId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getUserName(),
			newSiteNavigationMenuItem.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSiteNavigationMenuItem.getCreateDate()),
			Time.getShortTimestamp(newSiteNavigationMenuItem.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSiteNavigationMenuItem.getModifiedDate()),
			Time.getShortTimestamp(
				newSiteNavigationMenuItem.getModifiedDate()));
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getSiteNavigationMenuId(),
			newSiteNavigationMenuItem.getSiteNavigationMenuId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getParentSiteNavigationMenuItemId(),
			newSiteNavigationMenuItem.getParentSiteNavigationMenuItemId());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getName(),
			newSiteNavigationMenuItem.getName());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getType(),
			newSiteNavigationMenuItem.getType());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getTypeSettings(),
			newSiteNavigationMenuItem.getTypeSettings());
		Assert.assertEquals(
			existingSiteNavigationMenuItem.getOrder(),
			newSiteNavigationMenuItem.getOrder());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSiteNavigationMenuItem.getLastPublishDate()),
			Time.getShortTimestamp(
				newSiteNavigationMenuItem.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateSiteNavigationMenuItemExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		SiteNavigationMenuItem siteNavigationMenuItem =
			addSiteNavigationMenuItem();

		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		newSiteNavigationMenuItem.setGroupId(
			siteNavigationMenuItem.getGroupId());

		newSiteNavigationMenuItem = _persistence.update(
			newSiteNavigationMenuItem);

		Session session = _persistence.getCurrentSession();

		session.evict(newSiteNavigationMenuItem);

		newSiteNavigationMenuItem.setExternalReferenceCode(
			siteNavigationMenuItem.getExternalReferenceCode());

		_persistence.update(newSiteNavigationMenuItem);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountBySiteNavigationMenuId() throws Exception {
		_persistence.countBySiteNavigationMenuId(RandomTestUtil.nextLong());

		_persistence.countBySiteNavigationMenuId(0L);
	}

	@Test
	public void testCountByParentSiteNavigationMenuItemId() throws Exception {
		_persistence.countByParentSiteNavigationMenuItemId(
			RandomTestUtil.nextLong());

		_persistence.countByParentSiteNavigationMenuItemId(0L);
	}

	@Test
	public void testCountByType() throws Exception {
		_persistence.countByType("");

		_persistence.countByType("null");

		_persistence.countByType((String)null);
	}

	@Test
	public void testCountByS_P() throws Exception {
		_persistence.countByS_P(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByS_P(0L, 0L);
	}

	@Test
	public void testCountByS_LikeN() throws Exception {
		_persistence.countByS_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByS_LikeN(0L, "null");

		_persistence.countByS_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		SiteNavigationMenuItem existingSiteNavigationMenuItem =
			_persistence.findByPrimaryKey(
				newSiteNavigationMenuItem.getPrimaryKey());

		Assert.assertEquals(
			existingSiteNavigationMenuItem, newSiteNavigationMenuItem);
	}

	@Test(expected = NoSuchMenuItemException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SiteNavigationMenuItem> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SiteNavigationMenuItem", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"siteNavigationMenuItemId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "siteNavigationMenuId", true,
			"parentSiteNavigationMenuItemId", true, "name", true, "type", true,
			"order", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		SiteNavigationMenuItem existingSiteNavigationMenuItem =
			_persistence.fetchByPrimaryKey(
				newSiteNavigationMenuItem.getPrimaryKey());

		Assert.assertEquals(
			existingSiteNavigationMenuItem, newSiteNavigationMenuItem);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenuItem missingSiteNavigationMenuItem =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSiteNavigationMenuItem);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SiteNavigationMenuItem newSiteNavigationMenuItem1 =
			addSiteNavigationMenuItem();
		SiteNavigationMenuItem newSiteNavigationMenuItem2 =
			addSiteNavigationMenuItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteNavigationMenuItem1.getPrimaryKey());
		primaryKeys.add(newSiteNavigationMenuItem2.getPrimaryKey());

		Map<Serializable, SiteNavigationMenuItem> siteNavigationMenuItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, siteNavigationMenuItems.size());
		Assert.assertEquals(
			newSiteNavigationMenuItem1,
			siteNavigationMenuItems.get(
				newSiteNavigationMenuItem1.getPrimaryKey()));
		Assert.assertEquals(
			newSiteNavigationMenuItem2,
			siteNavigationMenuItems.get(
				newSiteNavigationMenuItem2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SiteNavigationMenuItem> siteNavigationMenuItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(siteNavigationMenuItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteNavigationMenuItem.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SiteNavigationMenuItem> siteNavigationMenuItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, siteNavigationMenuItems.size());
		Assert.assertEquals(
			newSiteNavigationMenuItem,
			siteNavigationMenuItems.get(
				newSiteNavigationMenuItem.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SiteNavigationMenuItem> siteNavigationMenuItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(siteNavigationMenuItems.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteNavigationMenuItem.getPrimaryKey());

		Map<Serializable, SiteNavigationMenuItem> siteNavigationMenuItems =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, siteNavigationMenuItems.size());
		Assert.assertEquals(
			newSiteNavigationMenuItem,
			siteNavigationMenuItems.get(
				newSiteNavigationMenuItem.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SiteNavigationMenuItemLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SiteNavigationMenuItem>() {

				@Override
				public void performAction(
					SiteNavigationMenuItem siteNavigationMenuItem) {

					Assert.assertNotNull(siteNavigationMenuItem);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenuItem.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteNavigationMenuItemId",
				newSiteNavigationMenuItem.getSiteNavigationMenuItemId()));

		List<SiteNavigationMenuItem> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SiteNavigationMenuItem existingSiteNavigationMenuItem = result.get(0);

		Assert.assertEquals(
			existingSiteNavigationMenuItem, newSiteNavigationMenuItem);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenuItem.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteNavigationMenuItemId", RandomTestUtil.nextLong()));

		List<SiteNavigationMenuItem> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenuItem.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("siteNavigationMenuItemId"));

		Object newSiteNavigationMenuItemId =
			newSiteNavigationMenuItem.getSiteNavigationMenuItemId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"siteNavigationMenuItemId",
				new Object[] {newSiteNavigationMenuItemId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSiteNavigationMenuItemId = result.get(0);

		Assert.assertEquals(
			existingSiteNavigationMenuItemId, newSiteNavigationMenuItemId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenuItem.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("siteNavigationMenuItemId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"siteNavigationMenuItemId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSiteNavigationMenuItem.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromDatabase()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(true);
	}

	@Test
	public void testResetOriginalValuesWithDynamicQueryLoadFromSession()
		throws Exception {

		_testResetOriginalValuesWithDynamicQuery(false);
	}

	private void _testResetOriginalValuesWithDynamicQuery(boolean clearSession)
		throws Exception {

		SiteNavigationMenuItem newSiteNavigationMenuItem =
			addSiteNavigationMenuItem();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenuItem.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteNavigationMenuItemId",
				newSiteNavigationMenuItem.getSiteNavigationMenuItemId()));

		List<SiteNavigationMenuItem> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		SiteNavigationMenuItem siteNavigationMenuItem) {

		Assert.assertEquals(
			siteNavigationMenuItem.getUuid(),
			ReflectionTestUtil.invoke(
				siteNavigationMenuItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(siteNavigationMenuItem.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				siteNavigationMenuItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			siteNavigationMenuItem.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				siteNavigationMenuItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(siteNavigationMenuItem.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				siteNavigationMenuItem, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected SiteNavigationMenuItem addSiteNavigationMenuItem()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenuItem siteNavigationMenuItem = _persistence.create(pk);

		siteNavigationMenuItem.setMvccVersion(RandomTestUtil.nextLong());

		siteNavigationMenuItem.setCtCollectionId(RandomTestUtil.nextLong());

		siteNavigationMenuItem.setUuid(RandomTestUtil.randomString());

		siteNavigationMenuItem.setExternalReferenceCode(
			RandomTestUtil.randomString());

		siteNavigationMenuItem.setGroupId(RandomTestUtil.nextLong());

		siteNavigationMenuItem.setCompanyId(RandomTestUtil.nextLong());

		siteNavigationMenuItem.setUserId(RandomTestUtil.nextLong());

		siteNavigationMenuItem.setUserName(RandomTestUtil.randomString());

		siteNavigationMenuItem.setCreateDate(RandomTestUtil.nextDate());

		siteNavigationMenuItem.setModifiedDate(RandomTestUtil.nextDate());

		siteNavigationMenuItem.setSiteNavigationMenuId(
			RandomTestUtil.nextLong());

		siteNavigationMenuItem.setParentSiteNavigationMenuItemId(
			RandomTestUtil.nextLong());

		siteNavigationMenuItem.setName(RandomTestUtil.randomString());

		siteNavigationMenuItem.setType(RandomTestUtil.randomString());

		siteNavigationMenuItem.setTypeSettings(RandomTestUtil.randomString());

		siteNavigationMenuItem.setOrder(RandomTestUtil.nextInt());

		siteNavigationMenuItem.setLastPublishDate(RandomTestUtil.nextDate());

		_siteNavigationMenuItems.add(
			_persistence.update(siteNavigationMenuItem));

		return siteNavigationMenuItem;
	}

	private List<SiteNavigationMenuItem> _siteNavigationMenuItems =
		new ArrayList<SiteNavigationMenuItem>();
	private SiteNavigationMenuItemPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}