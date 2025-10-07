/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateLayoutExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchLayoutException;
import com.liferay.portal.kernel.model.Layout;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.LayoutLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.LayoutPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutUtil;
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
public class LayoutPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = LayoutUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Layout> iterator = _layouts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Layout layout = _persistence.create(pk);

		Assert.assertNotNull(layout);

		Assert.assertEquals(layout.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Layout newLayout = addLayout();

		_persistence.remove(newLayout);

		Layout existingLayout = _persistence.fetchByPrimaryKey(
			newLayout.getPrimaryKey());

		Assert.assertNull(existingLayout);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayout();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Layout newLayout = _persistence.create(pk);

		newLayout.setMvccVersion(RandomTestUtil.nextLong());

		newLayout.setCtCollectionId(RandomTestUtil.nextLong());

		newLayout.setUuid(RandomTestUtil.randomString());

		newLayout.setExternalReferenceCode(RandomTestUtil.randomString());

		newLayout.setGroupId(RandomTestUtil.nextLong());

		newLayout.setCompanyId(RandomTestUtil.nextLong());

		newLayout.setUserId(RandomTestUtil.nextLong());

		newLayout.setUserName(RandomTestUtil.randomString());

		newLayout.setCreateDate(RandomTestUtil.nextDate());

		newLayout.setModifiedDate(RandomTestUtil.nextDate());

		newLayout.setParentPlid(RandomTestUtil.nextLong());

		newLayout.setPrivateLayout(RandomTestUtil.randomBoolean());

		newLayout.setLayoutId(RandomTestUtil.nextLong());

		newLayout.setParentLayoutId(RandomTestUtil.nextLong());

		newLayout.setClassNameId(RandomTestUtil.nextLong());

		newLayout.setClassPK(RandomTestUtil.nextLong());

		newLayout.setName(RandomTestUtil.randomString());

		newLayout.setTitle(RandomTestUtil.randomString());

		newLayout.setDescription(RandomTestUtil.randomString());

		newLayout.setKeywords(RandomTestUtil.randomString());

		newLayout.setRobots(RandomTestUtil.randomString());

		newLayout.setType(RandomTestUtil.randomString());

		newLayout.setTypeSettings(RandomTestUtil.randomString());

		newLayout.setHidden(RandomTestUtil.randomBoolean());

		newLayout.setSystem(RandomTestUtil.randomBoolean());

		newLayout.setFriendlyURL(RandomTestUtil.randomString());

		newLayout.setIconImageId(RandomTestUtil.nextLong());

		newLayout.setThemeId(RandomTestUtil.randomString());

		newLayout.setColorSchemeId(RandomTestUtil.randomString());

		newLayout.setStyleBookEntryId(RandomTestUtil.nextLong());

		newLayout.setCss(RandomTestUtil.randomString());

		newLayout.setPriority(RandomTestUtil.nextInt());

		newLayout.setFaviconFileEntryId(RandomTestUtil.nextLong());

		newLayout.setMasterLayoutPlid(RandomTestUtil.nextLong());

		newLayout.setLayoutPrototypeUuid(RandomTestUtil.randomString());

		newLayout.setLayoutPrototypeLinkEnabled(RandomTestUtil.randomBoolean());

		newLayout.setLayoutSetPrototypeLayoutERC(RandomTestUtil.randomString());

		newLayout.setPublishDate(RandomTestUtil.nextDate());

		newLayout.setLastPublishDate(RandomTestUtil.nextDate());

		newLayout.setStatus(RandomTestUtil.nextInt());

		newLayout.setStatusByUserId(RandomTestUtil.nextLong());

		newLayout.setStatusByUserName(RandomTestUtil.randomString());

		newLayout.setStatusDate(RandomTestUtil.nextDate());

		_layouts.add(_persistence.update(newLayout));

		Layout existingLayout = _persistence.findByPrimaryKey(
			newLayout.getPrimaryKey());

		Assert.assertEquals(
			existingLayout.getMvccVersion(), newLayout.getMvccVersion());
		Assert.assertEquals(
			existingLayout.getCtCollectionId(), newLayout.getCtCollectionId());
		Assert.assertEquals(existingLayout.getUuid(), newLayout.getUuid());
		Assert.assertEquals(
			existingLayout.getExternalReferenceCode(),
			newLayout.getExternalReferenceCode());
		Assert.assertEquals(existingLayout.getPlid(), newLayout.getPlid());
		Assert.assertEquals(
			existingLayout.getGroupId(), newLayout.getGroupId());
		Assert.assertEquals(
			existingLayout.getCompanyId(), newLayout.getCompanyId());
		Assert.assertEquals(existingLayout.getUserId(), newLayout.getUserId());
		Assert.assertEquals(
			existingLayout.getUserName(), newLayout.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayout.getCreateDate()),
			Time.getShortTimestamp(newLayout.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayout.getModifiedDate()),
			Time.getShortTimestamp(newLayout.getModifiedDate()));
		Assert.assertEquals(
			existingLayout.getParentPlid(), newLayout.getParentPlid());
		Assert.assertEquals(
			existingLayout.isPrivateLayout(), newLayout.isPrivateLayout());
		Assert.assertEquals(
			existingLayout.getLayoutId(), newLayout.getLayoutId());
		Assert.assertEquals(
			existingLayout.getParentLayoutId(), newLayout.getParentLayoutId());
		Assert.assertEquals(
			existingLayout.getClassNameId(), newLayout.getClassNameId());
		Assert.assertEquals(
			existingLayout.getClassPK(), newLayout.getClassPK());
		Assert.assertEquals(existingLayout.getName(), newLayout.getName());
		Assert.assertEquals(existingLayout.getTitle(), newLayout.getTitle());
		Assert.assertEquals(
			existingLayout.getDescription(), newLayout.getDescription());
		Assert.assertEquals(
			existingLayout.getKeywords(), newLayout.getKeywords());
		Assert.assertEquals(existingLayout.getRobots(), newLayout.getRobots());
		Assert.assertEquals(existingLayout.getType(), newLayout.getType());
		Assert.assertEquals(
			existingLayout.getTypeSettings(), newLayout.getTypeSettings());
		Assert.assertEquals(existingLayout.isHidden(), newLayout.isHidden());
		Assert.assertEquals(existingLayout.isSystem(), newLayout.isSystem());
		Assert.assertEquals(
			existingLayout.getFriendlyURL(), newLayout.getFriendlyURL());
		Assert.assertEquals(
			existingLayout.getIconImageId(), newLayout.getIconImageId());
		Assert.assertEquals(
			existingLayout.getThemeId(), newLayout.getThemeId());
		Assert.assertEquals(
			existingLayout.getColorSchemeId(), newLayout.getColorSchemeId());
		Assert.assertEquals(
			existingLayout.getStyleBookEntryId(),
			newLayout.getStyleBookEntryId());
		Assert.assertEquals(existingLayout.getCss(), newLayout.getCss());
		Assert.assertEquals(
			existingLayout.getPriority(), newLayout.getPriority());
		Assert.assertEquals(
			existingLayout.getFaviconFileEntryId(),
			newLayout.getFaviconFileEntryId());
		Assert.assertEquals(
			existingLayout.getMasterLayoutPlid(),
			newLayout.getMasterLayoutPlid());
		Assert.assertEquals(
			existingLayout.getLayoutPrototypeUuid(),
			newLayout.getLayoutPrototypeUuid());
		Assert.assertEquals(
			existingLayout.isLayoutPrototypeLinkEnabled(),
			newLayout.isLayoutPrototypeLinkEnabled());
		Assert.assertEquals(
			existingLayout.getLayoutSetPrototypeLayoutERC(),
			newLayout.getLayoutSetPrototypeLayoutERC());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayout.getPublishDate()),
			Time.getShortTimestamp(newLayout.getPublishDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayout.getLastPublishDate()),
			Time.getShortTimestamp(newLayout.getLastPublishDate()));
		Assert.assertEquals(existingLayout.getStatus(), newLayout.getStatus());
		Assert.assertEquals(
			existingLayout.getStatusByUserId(), newLayout.getStatusByUserId());
		Assert.assertEquals(
			existingLayout.getStatusByUserName(),
			newLayout.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayout.getStatusDate()),
			Time.getShortTimestamp(newLayout.getStatusDate()));
	}

	@Test(expected = DuplicateLayoutExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		Layout layout = addLayout();

		Layout newLayout = addLayout();

		newLayout.setGroupId(layout.getGroupId());

		newLayout = _persistence.update(newLayout);

		Session session = _persistence.getCurrentSession();

		session.evict(newLayout);

		newLayout.setExternalReferenceCode(layout.getExternalReferenceCode());

		_persistence.update(newLayout);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G_P() throws Exception {
		_persistence.countByUUID_G_P(
			"", RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_P(
			"null", 0L, RandomTestUtil.randomBoolean());

		_persistence.countByUUID_G_P(
			(String)null, 0L, RandomTestUtil.randomBoolean());
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
	public void testCountByParentPlid() throws Exception {
		_persistence.countByParentPlid(RandomTestUtil.nextLong());

		_persistence.countByParentPlid(0L);
	}

	@Test
	public void testCountByIconImageId() throws Exception {
		_persistence.countByIconImageId(RandomTestUtil.nextLong());

		_persistence.countByIconImageId(0L);
	}

	@Test
	public void testCountByLayoutPrototypeUuid() throws Exception {
		_persistence.countByLayoutPrototypeUuid("");

		_persistence.countByLayoutPrototypeUuid("null");

		_persistence.countByLayoutPrototypeUuid((String)null);
	}

	@Test
	public void testCountByLayoutSetPrototypeLayoutERC() throws Exception {
		_persistence.countByLayoutSetPrototypeLayoutERC("");

		_persistence.countByLayoutSetPrototypeLayoutERC("null");

		_persistence.countByLayoutSetPrototypeLayoutERC((String)null);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_T() throws Exception {
		_persistence.countByG_T(RandomTestUtil.nextLong(), "");

		_persistence.countByG_T(0L, "null");

		_persistence.countByG_T(0L, (String)null);
	}

	@Test
	public void testCountByG_MLP() throws Exception {
		_persistence.countByG_MLP(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_MLP(0L, 0L);
	}

	@Test
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(RandomTestUtil.nextLong(), "");

		_persistence.countByC_L(0L, "null");

		_persistence.countByC_L(0L, (String)null);
	}

	@Test
	public void testCountByP_I() throws Exception {
		_persistence.countByP_I(
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextLong());

		_persistence.countByP_I(RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_CArrayable() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_P_L() throws Exception {
		_persistence.countByG_P_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByG_P_L(0L, RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByG_P_P() throws Exception {
		_persistence.countByG_P_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByG_P_P(0L, RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByG_P_PArrayable() throws Exception {
		_persistence.countByG_P_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			new long[] {RandomTestUtil.nextLong(), 0L});
	}

	@Test
	public void testCountByG_P_T() throws Exception {
		_persistence.countByG_P_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_P_T(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_P_T(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_P_TArrayable() throws Exception {
		_persistence.countByG_P_T(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			new String[] {
				RandomTestUtil.randomString(), "", "null", null, null
			});
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
	public void testCountByG_P_LSPLE() throws Exception {
		_persistence.countByG_P_LSPLE(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_P_LSPLE(
			0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_P_LSPLE(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_P_ST() throws Exception {
		_persistence.countByG_P_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextInt());

		_persistence.countByG_P_ST(0L, RandomTestUtil.randomBoolean(), 0);
	}

	@Test
	public void testCountByG_P_STArrayable() throws Exception {
		_persistence.countByG_P_ST(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			new int[] {RandomTestUtil.nextInt(), 0});
	}

	@Test
	public void testCountByG_P_P_H() throws Exception {
		_persistence.countByG_P_P_H(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_P_P_H(
			0L, RandomTestUtil.randomBoolean(), 0L,
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_P_HArrayable() throws Exception {
		_persistence.countByG_P_P_H(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_P_S() throws Exception {
		_persistence.countByG_P_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_P_P_S(
			0L, RandomTestUtil.randomBoolean(), 0L,
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_P_SArrayable() throws Exception {
		_persistence.countByG_P_P_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_P_LteP() throws Exception {
		_persistence.countByG_P_P_LteP(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByG_P_P_LteP(
			0L, RandomTestUtil.randomBoolean(), 0L, 0);
	}

	@Test
	public void testCountByERC_G() throws Exception {
		_persistence.countByERC_G("", RandomTestUtil.nextLong());

		_persistence.countByERC_G("null", 0L);

		_persistence.countByERC_G((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Layout newLayout = addLayout();

		Layout existingLayout = _persistence.findByPrimaryKey(
			newLayout.getPrimaryKey());

		Assert.assertEquals(existingLayout, newLayout);
	}

	@Test(expected = NoSuchLayoutException.class)
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

	protected OrderByComparator<Layout> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Layout", "mvccVersion", true, "ctCollectionId", true, "uuid", true,
			"externalReferenceCode", true, "plid", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "parentPlid", true, "privateLayout",
			true, "layoutId", true, "parentLayoutId", true, "classNameId", true,
			"classPK", true, "name", true, "keywords", true, "robots", true,
			"type", true, "hidden", true, "system", true, "friendlyURL", true,
			"iconImageId", true, "themeId", true, "colorSchemeId", true,
			"styleBookEntryId", true, "priority", true, "faviconFileEntryId",
			true, "masterLayoutPlid", true, "layoutPrototypeUuid", true,
			"layoutPrototypeLinkEnabled", true, "layoutSetPrototypeLayoutERC",
			true, "publishDate", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Layout newLayout = addLayout();

		Layout existingLayout = _persistence.fetchByPrimaryKey(
			newLayout.getPrimaryKey());

		Assert.assertEquals(existingLayout, newLayout);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Layout missingLayout = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayout);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Layout newLayout1 = addLayout();
		Layout newLayout2 = addLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayout1.getPrimaryKey());
		primaryKeys.add(newLayout2.getPrimaryKey());

		Map<Serializable, Layout> layouts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, layouts.size());
		Assert.assertEquals(
			newLayout1, layouts.get(newLayout1.getPrimaryKey()));
		Assert.assertEquals(
			newLayout2, layouts.get(newLayout2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Layout> layouts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(layouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Layout newLayout = addLayout();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayout.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Layout> layouts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, layouts.size());
		Assert.assertEquals(newLayout, layouts.get(newLayout.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Layout> layouts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(layouts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Layout newLayout = addLayout();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayout.getPrimaryKey());

		Map<Serializable, Layout> layouts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, layouts.size());
		Assert.assertEquals(newLayout, layouts.get(newLayout.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			LayoutLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Layout>() {

				@Override
				public void performAction(Layout layout) {
					Assert.assertNotNull(layout);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Layout newLayout = addLayout();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Layout.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("plid", newLayout.getPlid()));

		List<Layout> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Layout existingLayout = result.get(0);

		Assert.assertEquals(existingLayout, newLayout);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Layout.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("plid", RandomTestUtil.nextLong()));

		List<Layout> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Layout newLayout = addLayout();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Layout.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("plid"));

		Object newPlid = newLayout.getPlid();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in("plid", new Object[] {newPlid}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingPlid = result.get(0);

		Assert.assertEquals(existingPlid, newPlid);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Layout.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("plid"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"plid", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Layout newLayout = addLayout();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLayout.getPrimaryKey()));
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

		Layout newLayout = addLayout();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Layout.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("plid", newLayout.getPlid()));

		List<Layout> result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(Layout layout) {
		Assert.assertEquals(
			layout.getUuid(),
			ReflectionTestUtil.invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"uuid_"));
		Assert.assertEquals(
			Long.valueOf(layout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layout.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"privateLayout"));

		Assert.assertEquals(
			Long.valueOf(layout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layout.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"privateLayout"));
		Assert.assertEquals(
			Long.valueOf(layout.getLayoutId()),
			ReflectionTestUtil.<Long>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"layoutId"));

		Assert.assertEquals(
			Long.valueOf(layout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layout.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"privateLayout"));
		Assert.assertEquals(
			layout.getFriendlyURL(),
			ReflectionTestUtil.invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"friendlyURL"));

		Assert.assertEquals(
			layout.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(layout.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layout, "getColumnOriginalValue", new Class<?>[] {String.class},
				"groupId"));
	}

	protected Layout addLayout() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Layout layout = _persistence.create(pk);

		layout.setMvccVersion(RandomTestUtil.nextLong());

		layout.setCtCollectionId(RandomTestUtil.nextLong());

		layout.setUuid(RandomTestUtil.randomString());

		layout.setExternalReferenceCode(RandomTestUtil.randomString());

		layout.setGroupId(RandomTestUtil.nextLong());

		layout.setCompanyId(RandomTestUtil.nextLong());

		layout.setUserId(RandomTestUtil.nextLong());

		layout.setUserName(RandomTestUtil.randomString());

		layout.setCreateDate(RandomTestUtil.nextDate());

		layout.setModifiedDate(RandomTestUtil.nextDate());

		layout.setParentPlid(RandomTestUtil.nextLong());

		layout.setPrivateLayout(RandomTestUtil.randomBoolean());

		layout.setLayoutId(RandomTestUtil.nextLong());

		layout.setParentLayoutId(RandomTestUtil.nextLong());

		layout.setClassNameId(RandomTestUtil.nextLong());

		layout.setClassPK(RandomTestUtil.nextLong());

		layout.setName(RandomTestUtil.randomString());

		layout.setTitle(RandomTestUtil.randomString());

		layout.setDescription(RandomTestUtil.randomString());

		layout.setKeywords(RandomTestUtil.randomString());

		layout.setRobots(RandomTestUtil.randomString());

		layout.setType(RandomTestUtil.randomString());

		layout.setTypeSettings(RandomTestUtil.randomString());

		layout.setHidden(RandomTestUtil.randomBoolean());

		layout.setSystem(RandomTestUtil.randomBoolean());

		layout.setFriendlyURL(RandomTestUtil.randomString());

		layout.setIconImageId(RandomTestUtil.nextLong());

		layout.setThemeId(RandomTestUtil.randomString());

		layout.setColorSchemeId(RandomTestUtil.randomString());

		layout.setStyleBookEntryId(RandomTestUtil.nextLong());

		layout.setCss(RandomTestUtil.randomString());

		layout.setPriority(RandomTestUtil.nextInt());

		layout.setFaviconFileEntryId(RandomTestUtil.nextLong());

		layout.setMasterLayoutPlid(RandomTestUtil.nextLong());

		layout.setLayoutPrototypeUuid(RandomTestUtil.randomString());

		layout.setLayoutPrototypeLinkEnabled(RandomTestUtil.randomBoolean());

		layout.setLayoutSetPrototypeLayoutERC(RandomTestUtil.randomString());

		layout.setPublishDate(RandomTestUtil.nextDate());

		layout.setLastPublishDate(RandomTestUtil.nextDate());

		layout.setStatus(RandomTestUtil.nextInt());

		layout.setStatusByUserId(RandomTestUtil.nextLong());

		layout.setStatusByUserName(RandomTestUtil.randomString());

		layout.setStatusDate(RandomTestUtil.nextDate());

		_layouts.add(_persistence.update(layout));

		return layout;
	}

	private List<Layout> _layouts = new ArrayList<Layout>();
	private LayoutPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}