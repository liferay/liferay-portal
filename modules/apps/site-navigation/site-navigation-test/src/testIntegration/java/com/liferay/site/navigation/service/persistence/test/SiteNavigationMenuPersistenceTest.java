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
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.site.navigation.exception.DuplicateSiteNavigationMenuExternalReferenceCodeException;
import com.liferay.site.navigation.exception.NoSuchMenuException;
import com.liferay.site.navigation.model.SiteNavigationMenu;
import com.liferay.site.navigation.service.SiteNavigationMenuLocalServiceUtil;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuPersistence;
import com.liferay.site.navigation.service.persistence.SiteNavigationMenuUtil;

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
public class SiteNavigationMenuPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.site.navigation.service"));

	@Before
	public void setUp() {
		_persistence = SiteNavigationMenuUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<SiteNavigationMenu> iterator = _siteNavigationMenus.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenu siteNavigationMenu = _persistence.create(pk);

		Assert.assertNotNull(siteNavigationMenu);

		Assert.assertEquals(siteNavigationMenu.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		_persistence.remove(newSiteNavigationMenu);

		SiteNavigationMenu existingSiteNavigationMenu =
			_persistence.fetchByPrimaryKey(
				newSiteNavigationMenu.getPrimaryKey());

		Assert.assertNull(existingSiteNavigationMenu);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addSiteNavigationMenu();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		newSiteNavigationMenu.setCtCollectionId(RandomTestUtil.nextLong());

		newSiteNavigationMenu.setUuid(RandomTestUtil.randomString());

		newSiteNavigationMenu.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newSiteNavigationMenu.setGroupId(RandomTestUtil.nextLong());

		newSiteNavigationMenu.setCompanyId(RandomTestUtil.nextLong());

		newSiteNavigationMenu.setUserId(RandomTestUtil.nextLong());

		newSiteNavigationMenu.setUserName(RandomTestUtil.randomString());

		newSiteNavigationMenu.setCreateDate(RandomTestUtil.nextDate());

		newSiteNavigationMenu.setModifiedDate(RandomTestUtil.nextDate());

		newSiteNavigationMenu.setName(RandomTestUtil.randomString());

		newSiteNavigationMenu.setType(RandomTestUtil.nextInt());

		newSiteNavigationMenu.setAuto(RandomTestUtil.randomBoolean());

		newSiteNavigationMenu.setLastPublishDate(RandomTestUtil.nextDate());

		newSiteNavigationMenu = _persistence.update(newSiteNavigationMenu);

		_siteNavigationMenus.add(newSiteNavigationMenu);

		SiteNavigationMenu existingSiteNavigationMenu =
			_persistence.findByPrimaryKey(
				newSiteNavigationMenu.getPrimaryKey());

		Assert.assertEquals(
			existingSiteNavigationMenu.getMvccVersion(),
			newSiteNavigationMenu.getMvccVersion());
		Assert.assertEquals(
			existingSiteNavigationMenu.getCtCollectionId(),
			newSiteNavigationMenu.getCtCollectionId());
		Assert.assertEquals(
			existingSiteNavigationMenu.getUuid(),
			newSiteNavigationMenu.getUuid());
		Assert.assertEquals(
			existingSiteNavigationMenu.getExternalReferenceCode(),
			newSiteNavigationMenu.getExternalReferenceCode());
		Assert.assertEquals(
			existingSiteNavigationMenu.getSiteNavigationMenuId(),
			newSiteNavigationMenu.getSiteNavigationMenuId());
		Assert.assertEquals(
			existingSiteNavigationMenu.getGroupId(),
			newSiteNavigationMenu.getGroupId());
		Assert.assertEquals(
			existingSiteNavigationMenu.getCompanyId(),
			newSiteNavigationMenu.getCompanyId());
		Assert.assertEquals(
			existingSiteNavigationMenu.getUserId(),
			newSiteNavigationMenu.getUserId());
		Assert.assertEquals(
			existingSiteNavigationMenu.getUserName(),
			newSiteNavigationMenu.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingSiteNavigationMenu.getCreateDate()),
			Time.getShortTimestamp(newSiteNavigationMenu.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSiteNavigationMenu.getModifiedDate()),
			Time.getShortTimestamp(newSiteNavigationMenu.getModifiedDate()));
		Assert.assertEquals(
			existingSiteNavigationMenu.getName(),
			newSiteNavigationMenu.getName());
		Assert.assertEquals(
			existingSiteNavigationMenu.getType(),
			newSiteNavigationMenu.getType());
		Assert.assertEquals(
			existingSiteNavigationMenu.isAuto(),
			newSiteNavigationMenu.isAuto());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingSiteNavigationMenu.getLastPublishDate()),
			Time.getShortTimestamp(newSiteNavigationMenu.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateSiteNavigationMenuExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		SiteNavigationMenu siteNavigationMenu = addSiteNavigationMenu();

		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		newSiteNavigationMenu.setGroupId(siteNavigationMenu.getGroupId());

		newSiteNavigationMenu = _persistence.update(newSiteNavigationMenu);

		Session session = _persistence.getCurrentSession();

		session.evict(newSiteNavigationMenu);

		newSiteNavigationMenu.setExternalReferenceCode(
			siteNavigationMenu.getExternalReferenceCode());

		_persistence.update(newSiteNavigationMenu);
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
	public void testCountByGroupIdArrayable() throws Exception {
		_persistence.countByGroupId(new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByG_N() throws Exception {
		_persistence.countByG_N(RandomTestUtil.nextLong(), "");

		_persistence.countByG_N(0L, "null");

		_persistence.countByG_N(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeN() throws Exception {
		_persistence.countByG_LikeN(RandomTestUtil.nextLong(), "");

		_persistence.countByG_LikeN(0L, "null");

		_persistence.countByG_LikeN(0L, (String)null);
	}

	@Test
	public void testCountByG_LikeNArrayable() throws Exception {
		_persistence.countByG_LikeN(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomString());
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_T(0L, 0);
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		SiteNavigationMenu existingSiteNavigationMenu =
			_persistence.findByPrimaryKey(
				newSiteNavigationMenu.getPrimaryKey());

		Assert.assertEquals(existingSiteNavigationMenu, newSiteNavigationMenu);
	}

	@Test(expected = NoSuchMenuException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<SiteNavigationMenu> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"SiteNavigationMenu", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "siteNavigationMenuId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "name",
			true, "type", true, "auto", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		SiteNavigationMenu existingSiteNavigationMenu =
			_persistence.fetchByPrimaryKey(
				newSiteNavigationMenu.getPrimaryKey());

		Assert.assertEquals(existingSiteNavigationMenu, newSiteNavigationMenu);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenu missingSiteNavigationMenu =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingSiteNavigationMenu);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		SiteNavigationMenu newSiteNavigationMenu1 = addSiteNavigationMenu();
		SiteNavigationMenu newSiteNavigationMenu2 = addSiteNavigationMenu();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteNavigationMenu1.getPrimaryKey());
		primaryKeys.add(newSiteNavigationMenu2.getPrimaryKey());

		Map<Serializable, SiteNavigationMenu> siteNavigationMenus =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, siteNavigationMenus.size());
		Assert.assertEquals(
			newSiteNavigationMenu1,
			siteNavigationMenus.get(newSiteNavigationMenu1.getPrimaryKey()));
		Assert.assertEquals(
			newSiteNavigationMenu2,
			siteNavigationMenus.get(newSiteNavigationMenu2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, SiteNavigationMenu> siteNavigationMenus =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(siteNavigationMenus.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteNavigationMenu.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, SiteNavigationMenu> siteNavigationMenus =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, siteNavigationMenus.size());
		Assert.assertEquals(
			newSiteNavigationMenu,
			siteNavigationMenus.get(newSiteNavigationMenu.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, SiteNavigationMenu> siteNavigationMenus =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(siteNavigationMenus.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newSiteNavigationMenu.getPrimaryKey());

		Map<Serializable, SiteNavigationMenu> siteNavigationMenus =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, siteNavigationMenus.size());
		Assert.assertEquals(
			newSiteNavigationMenu,
			siteNavigationMenus.get(newSiteNavigationMenu.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			SiteNavigationMenuLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod
				<SiteNavigationMenu>() {

				@Override
				public void performAction(
					SiteNavigationMenu siteNavigationMenu) {

					Assert.assertNotNull(siteNavigationMenu);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenu.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteNavigationMenuId",
				newSiteNavigationMenu.getSiteNavigationMenuId()));

		List<SiteNavigationMenu> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		SiteNavigationMenu existingSiteNavigationMenu = result.get(0);

		Assert.assertEquals(existingSiteNavigationMenu, newSiteNavigationMenu);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenu.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteNavigationMenuId", RandomTestUtil.nextLong()));

		List<SiteNavigationMenu> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenu.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("siteNavigationMenuId"));

		Object newSiteNavigationMenuId =
			newSiteNavigationMenu.getSiteNavigationMenuId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"siteNavigationMenuId",
				new Object[] {newSiteNavigationMenuId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingSiteNavigationMenuId = result.get(0);

		Assert.assertEquals(
			existingSiteNavigationMenuId, newSiteNavigationMenuId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenu.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("siteNavigationMenuId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"siteNavigationMenuId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newSiteNavigationMenu.getPrimaryKey()));
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

		SiteNavigationMenu newSiteNavigationMenu = addSiteNavigationMenu();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			SiteNavigationMenu.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"siteNavigationMenuId",
				newSiteNavigationMenu.getSiteNavigationMenuId()));

		List<SiteNavigationMenu> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(SiteNavigationMenu siteNavigationMenu) {
		Assert.assertEquals(
			siteNavigationMenu.getUuid(),
			ReflectionTestUtil.invoke(
				siteNavigationMenu, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(siteNavigationMenu.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				siteNavigationMenu, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(siteNavigationMenu.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				siteNavigationMenu, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			siteNavigationMenu.getName(),
			ReflectionTestUtil.invoke(
				siteNavigationMenu, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			siteNavigationMenu.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				siteNavigationMenu, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(siteNavigationMenu.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				siteNavigationMenu, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
	}

	protected SiteNavigationMenu addSiteNavigationMenu() throws Exception {
		long pk = RandomTestUtil.nextLong();

		SiteNavigationMenu siteNavigationMenu = _persistence.create(pk);

		siteNavigationMenu.setCtCollectionId(RandomTestUtil.nextLong());

		siteNavigationMenu.setUuid(RandomTestUtil.randomString());

		siteNavigationMenu.setExternalReferenceCode(
			RandomTestUtil.randomString());

		siteNavigationMenu.setGroupId(RandomTestUtil.nextLong());

		siteNavigationMenu.setCompanyId(RandomTestUtil.nextLong());

		siteNavigationMenu.setUserId(RandomTestUtil.nextLong());

		siteNavigationMenu.setUserName(RandomTestUtil.randomString());

		siteNavigationMenu.setCreateDate(RandomTestUtil.nextDate());

		siteNavigationMenu.setModifiedDate(RandomTestUtil.nextDate());

		siteNavigationMenu.setName(RandomTestUtil.randomString());

		siteNavigationMenu.setType(RandomTestUtil.nextInt());

		siteNavigationMenu.setAuto(RandomTestUtil.randomBoolean());

		siteNavigationMenu.setLastPublishDate(RandomTestUtil.nextDate());

		_siteNavigationMenus.add(_persistence.update(siteNavigationMenu));

		return siteNavigationMenu;
	}

	private List<SiteNavigationMenu> _siteNavigationMenus =
		new ArrayList<SiteNavigationMenu>();
	private SiteNavigationMenuPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:676639039