/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetBranchException;
import com.liferay.portal.kernel.model.LayoutSetBranch;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetBranchUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class LayoutSetBranchPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = LayoutSetBranchUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutSetBranch> iterator = _layoutSetBranchs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetBranch layoutSetBranch = _persistence.create(pk);

		Assert.assertNotNull(layoutSetBranch);

		Assert.assertEquals(layoutSetBranch.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutSetBranch newLayoutSetBranch = addLayoutSetBranch();

		_persistence.remove(newLayoutSetBranch);

		LayoutSetBranch existingLayoutSetBranch =
			_persistence.fetchByPrimaryKey(newLayoutSetBranch.getPrimaryKey());

		Assert.assertNull(existingLayoutSetBranch);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutSetBranch();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetBranch newLayoutSetBranch = _persistence.create(pk);

		newLayoutSetBranch.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutSetBranch.setGroupId(RandomTestUtil.nextLong());

		newLayoutSetBranch.setCompanyId(RandomTestUtil.nextLong());

		newLayoutSetBranch.setUserId(RandomTestUtil.nextLong());

		newLayoutSetBranch.setUserName(RandomTestUtil.randomString());

		newLayoutSetBranch.setCreateDate(RandomTestUtil.nextDate());

		newLayoutSetBranch.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutSetBranch.setPrivateLayout(RandomTestUtil.randomBoolean());

		newLayoutSetBranch.setName(RandomTestUtil.randomString());

		newLayoutSetBranch.setDescription(RandomTestUtil.randomString());

		newLayoutSetBranch.setMaster(RandomTestUtil.randomBoolean());

		newLayoutSetBranch.setLogoId(RandomTestUtil.nextLong());

		newLayoutSetBranch.setThemeId(RandomTestUtil.randomString());

		newLayoutSetBranch.setColorSchemeId(RandomTestUtil.randomString());

		newLayoutSetBranch.setCss(RandomTestUtil.randomString());

		newLayoutSetBranch.setSettings(RandomTestUtil.randomString());

		newLayoutSetBranch.setLayoutSetPrototypeUuid(
			RandomTestUtil.randomString());

		newLayoutSetBranch.setLayoutSetPrototypeLinkEnabled(
			RandomTestUtil.randomBoolean());

		_layoutSetBranchs.add(_persistence.update(newLayoutSetBranch));

		LayoutSetBranch existingLayoutSetBranch = _persistence.findByPrimaryKey(
			newLayoutSetBranch.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSetBranch.getMvccVersion(),
			newLayoutSetBranch.getMvccVersion());
		Assert.assertEquals(
			existingLayoutSetBranch.getLayoutSetBranchId(),
			newLayoutSetBranch.getLayoutSetBranchId());
		Assert.assertEquals(
			existingLayoutSetBranch.getGroupId(),
			newLayoutSetBranch.getGroupId());
		Assert.assertEquals(
			existingLayoutSetBranch.getCompanyId(),
			newLayoutSetBranch.getCompanyId());
		Assert.assertEquals(
			existingLayoutSetBranch.getUserId(),
			newLayoutSetBranch.getUserId());
		Assert.assertEquals(
			existingLayoutSetBranch.getUserName(),
			newLayoutSetBranch.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSetBranch.getCreateDate()),
			Time.getShortTimestamp(newLayoutSetBranch.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSetBranch.getModifiedDate()),
			Time.getShortTimestamp(newLayoutSetBranch.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutSetBranch.isPrivateLayout(),
			newLayoutSetBranch.isPrivateLayout());
		Assert.assertEquals(
			existingLayoutSetBranch.getName(), newLayoutSetBranch.getName());
		Assert.assertEquals(
			existingLayoutSetBranch.getDescription(),
			newLayoutSetBranch.getDescription());
		Assert.assertEquals(
			existingLayoutSetBranch.isMaster(), newLayoutSetBranch.isMaster());
		Assert.assertEquals(
			existingLayoutSetBranch.getLogoId(),
			newLayoutSetBranch.getLogoId());
		Assert.assertEquals(
			existingLayoutSetBranch.getThemeId(),
			newLayoutSetBranch.getThemeId());
		Assert.assertEquals(
			existingLayoutSetBranch.getColorSchemeId(),
			newLayoutSetBranch.getColorSchemeId());
		Assert.assertEquals(
			existingLayoutSetBranch.getCss(), newLayoutSetBranch.getCss());
		Assert.assertEquals(
			existingLayoutSetBranch.getSettings(),
			newLayoutSetBranch.getSettings());
		Assert.assertEquals(
			existingLayoutSetBranch.getLayoutSetPrototypeUuid(),
			newLayoutSetBranch.getLayoutSetPrototypeUuid());
		Assert.assertEquals(
			existingLayoutSetBranch.isLayoutSetPrototypeLinkEnabled(),
			newLayoutSetBranch.isLayoutSetPrototypeLinkEnabled());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P_N() throws Exception {
		_persistence.countByG_P_N(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_P_N(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_P_N(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_P_M() throws Exception {
		_persistence.countByG_P_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByG_P_M(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutSetBranch newLayoutSetBranch = addLayoutSetBranch();

		LayoutSetBranch existingLayoutSetBranch = _persistence.findByPrimaryKey(
			newLayoutSetBranch.getPrimaryKey());

		Assert.assertEquals(existingLayoutSetBranch, newLayoutSetBranch);
	}

	@Test(expected = NoSuchLayoutSetBranchException.class)
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

	protected OrderByComparator<LayoutSetBranch> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutSetBranch", "mvccVersion", true, "layoutSetBranchId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "privateLayout",
			true, "name", true, "description", true, "master", true, "logoId",
			true, "themeId", true, "colorSchemeId", true,
			"layoutSetPrototypeUuid", true, "layoutSetPrototypeLinkEnabled",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutSetBranch newLayoutSetBranch = addLayoutSetBranch();

		LayoutSetBranch existingLayoutSetBranch =
			_persistence.fetchByPrimaryKey(newLayoutSetBranch.getPrimaryKey());

		Assert.assertEquals(existingLayoutSetBranch, newLayoutSetBranch);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetBranch missingLayoutSetBranch = _persistence.fetchByPrimaryKey(
			pk);

		Assert.assertNull(missingLayoutSetBranch);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutSetBranch newLayoutSetBranch1 = addLayoutSetBranch();
		LayoutSetBranch newLayoutSetBranch2 = addLayoutSetBranch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSetBranch1.getPrimaryKey());
		primaryKeys.add(newLayoutSetBranch2.getPrimaryKey());

		Map<Serializable, LayoutSetBranch> layoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutSetBranchs.size());
		Assert.assertEquals(
			newLayoutSetBranch1,
			layoutSetBranchs.get(newLayoutSetBranch1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutSetBranch2,
			layoutSetBranchs.get(newLayoutSetBranch2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutSetBranch> layoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSetBranchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutSetBranch newLayoutSetBranch = addLayoutSetBranch();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSetBranch.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutSetBranch> layoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSetBranchs.size());
		Assert.assertEquals(
			newLayoutSetBranch,
			layoutSetBranchs.get(newLayoutSetBranch.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutSetBranch> layoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSetBranchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutSetBranch newLayoutSetBranch = addLayoutSetBranch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSetBranch.getPrimaryKey());

		Map<Serializable, LayoutSetBranch> layoutSetBranchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSetBranchs.size());
		Assert.assertEquals(
			newLayoutSetBranch,
			layoutSetBranchs.get(newLayoutSetBranch.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutSetBranch newLayoutSetBranch = addLayoutSetBranch();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLayoutSetBranch.getPrimaryKey()));
	}

	private void _assertOriginalValues(LayoutSetBranch layoutSetBranch) {
		Assert.assertEquals(
			Long.valueOf(layoutSetBranch.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutSetBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layoutSetBranch.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layoutSetBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "privateLayout"));
		Assert.assertEquals(
			layoutSetBranch.getName(),
			ReflectionTestUtil.invoke(
				layoutSetBranch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));
	}

	protected LayoutSetBranch addLayoutSetBranch() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSetBranch layoutSetBranch = _persistence.create(pk);

		layoutSetBranch.setMvccVersion(RandomTestUtil.nextLong());

		layoutSetBranch.setGroupId(RandomTestUtil.nextLong());

		layoutSetBranch.setCompanyId(RandomTestUtil.nextLong());

		layoutSetBranch.setUserId(RandomTestUtil.nextLong());

		layoutSetBranch.setUserName(RandomTestUtil.randomString());

		layoutSetBranch.setCreateDate(RandomTestUtil.nextDate());

		layoutSetBranch.setModifiedDate(RandomTestUtil.nextDate());

		layoutSetBranch.setPrivateLayout(RandomTestUtil.randomBoolean());

		layoutSetBranch.setName(RandomTestUtil.randomString());

		layoutSetBranch.setDescription(RandomTestUtil.randomString());

		layoutSetBranch.setMaster(RandomTestUtil.randomBoolean());

		layoutSetBranch.setLogoId(RandomTestUtil.nextLong());

		layoutSetBranch.setThemeId(RandomTestUtil.randomString());

		layoutSetBranch.setColorSchemeId(RandomTestUtil.randomString());

		layoutSetBranch.setCss(RandomTestUtil.randomString());

		layoutSetBranch.setSettings(RandomTestUtil.randomString());

		layoutSetBranch.setLayoutSetPrototypeUuid(
			RandomTestUtil.randomString());

		layoutSetBranch.setLayoutSetPrototypeLinkEnabled(
			RandomTestUtil.randomBoolean());

		_layoutSetBranchs.add(_persistence.update(layoutSetBranch));

		return layoutSetBranch;
	}

	private List<LayoutSetBranch> _layoutSetBranchs =
		new ArrayList<LayoutSetBranch>();
	private LayoutSetBranchPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}