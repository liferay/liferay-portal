/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchLayoutSetException;
import com.liferay.portal.kernel.model.LayoutSet;
import com.liferay.portal.kernel.service.persistence.LayoutSetPersistence;
import com.liferay.portal.kernel.service.persistence.LayoutSetUtil;
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
public class LayoutSetPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = LayoutSetUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutSet> iterator = _layoutSets.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSet layoutSet = _persistence.create(pk);

		Assert.assertNotNull(layoutSet);

		Assert.assertEquals(layoutSet.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutSet newLayoutSet = addLayoutSet();

		_persistence.remove(newLayoutSet);

		LayoutSet existingLayoutSet = _persistence.fetchByPrimaryKey(
			newLayoutSet.getPrimaryKey());

		Assert.assertNull(existingLayoutSet);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutSet();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSet newLayoutSet = _persistence.create(pk);

		newLayoutSet.setMvccVersion(RandomTestUtil.nextLong());

		newLayoutSet.setCtCollectionId(RandomTestUtil.nextLong());

		newLayoutSet.setGroupId(RandomTestUtil.nextLong());

		newLayoutSet.setCompanyId(RandomTestUtil.nextLong());

		newLayoutSet.setCreateDate(RandomTestUtil.nextDate());

		newLayoutSet.setModifiedDate(RandomTestUtil.nextDate());

		newLayoutSet.setPrivateLayout(RandomTestUtil.randomBoolean());

		newLayoutSet.setLogoId(RandomTestUtil.nextLong());

		newLayoutSet.setThemeId(RandomTestUtil.randomString());

		newLayoutSet.setColorSchemeId(RandomTestUtil.randomString());

		newLayoutSet.setFaviconFileEntryId(RandomTestUtil.nextLong());

		newLayoutSet.setCss(RandomTestUtil.randomString());

		newLayoutSet.setSettings(RandomTestUtil.randomString());

		newLayoutSet.setLayoutSetPrototypeUuid(RandomTestUtil.randomString());

		newLayoutSet.setLayoutSetPrototypeLinkEnabled(
			RandomTestUtil.randomBoolean());

		_layoutSets.add(_persistence.update(newLayoutSet));

		LayoutSet existingLayoutSet = _persistence.findByPrimaryKey(
			newLayoutSet.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSet.getMvccVersion(), newLayoutSet.getMvccVersion());
		Assert.assertEquals(
			existingLayoutSet.getCtCollectionId(),
			newLayoutSet.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutSet.getLayoutSetId(), newLayoutSet.getLayoutSetId());
		Assert.assertEquals(
			existingLayoutSet.getGroupId(), newLayoutSet.getGroupId());
		Assert.assertEquals(
			existingLayoutSet.getCompanyId(), newLayoutSet.getCompanyId());
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSet.getCreateDate()),
			Time.getShortTimestamp(newLayoutSet.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingLayoutSet.getModifiedDate()),
			Time.getShortTimestamp(newLayoutSet.getModifiedDate()));
		Assert.assertEquals(
			existingLayoutSet.isPrivateLayout(),
			newLayoutSet.isPrivateLayout());
		Assert.assertEquals(
			existingLayoutSet.getLogoId(), newLayoutSet.getLogoId());
		Assert.assertEquals(
			existingLayoutSet.getThemeId(), newLayoutSet.getThemeId());
		Assert.assertEquals(
			existingLayoutSet.getColorSchemeId(),
			newLayoutSet.getColorSchemeId());
		Assert.assertEquals(
			existingLayoutSet.getFaviconFileEntryId(),
			newLayoutSet.getFaviconFileEntryId());
		Assert.assertEquals(existingLayoutSet.getCss(), newLayoutSet.getCss());
		Assert.assertEquals(
			existingLayoutSet.getSettings(), newLayoutSet.getSettings());
		Assert.assertEquals(
			existingLayoutSet.getLayoutSetPrototypeUuid(),
			newLayoutSet.getLayoutSetPrototypeUuid());
		Assert.assertEquals(
			existingLayoutSet.isLayoutSetPrototypeLinkEnabled(),
			newLayoutSet.isLayoutSetPrototypeLinkEnabled());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByLayoutSetPrototypeUuid() throws Exception {
		_persistence.countByLayoutSetPrototypeUuid("");

		_persistence.countByLayoutSetPrototypeUuid("null");

		_persistence.countByLayoutSetPrototypeUuid((String)null);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_L() throws Exception {
		_persistence.countByC_L(RandomTestUtil.nextLong(), "");

		_persistence.countByC_L(0L, "null");

		_persistence.countByC_L(0L, (String)null);
	}

	@Test
	public void testCountByP_L() throws Exception {
		_persistence.countByP_L(
			RandomTestUtil.randomBoolean(), RandomTestUtil.nextLong());

		_persistence.countByP_L(RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutSet newLayoutSet = addLayoutSet();

		LayoutSet existingLayoutSet = _persistence.findByPrimaryKey(
			newLayoutSet.getPrimaryKey());

		Assert.assertEquals(existingLayoutSet, newLayoutSet);
	}

	@Test(expected = NoSuchLayoutSetException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutSet> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LayoutSet", "mvccVersion", true, "ctCollectionId", true,
			"layoutSetId", true, "groupId", true, "companyId", true,
			"createDate", true, "modifiedDate", true, "privateLayout", true,
			"logoId", true, "themeId", true, "colorSchemeId", true,
			"faviconFileEntryId", true, "layoutSetPrototypeUuid", true,
			"layoutSetPrototypeLinkEnabled", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutSet newLayoutSet = addLayoutSet();

		LayoutSet existingLayoutSet = _persistence.fetchByPrimaryKey(
			newLayoutSet.getPrimaryKey());

		Assert.assertEquals(existingLayoutSet, newLayoutSet);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSet missingLayoutSet = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutSet);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutSet newLayoutSet1 = addLayoutSet();
		LayoutSet newLayoutSet2 = addLayoutSet();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSet1.getPrimaryKey());
		primaryKeys.add(newLayoutSet2.getPrimaryKey());

		Map<Serializable, LayoutSet> layoutSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, layoutSets.size());
		Assert.assertEquals(
			newLayoutSet1, layoutSets.get(newLayoutSet1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutSet2, layoutSets.get(newLayoutSet2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutSet> layoutSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutSet newLayoutSet = addLayoutSet();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSet.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutSet> layoutSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSets.size());
		Assert.assertEquals(
			newLayoutSet, layoutSets.get(newLayoutSet.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutSet> layoutSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(layoutSets.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutSet newLayoutSet = addLayoutSet();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSet.getPrimaryKey());

		Map<Serializable, LayoutSet> layoutSets =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, layoutSets.size());
		Assert.assertEquals(
			newLayoutSet, layoutSets.get(newLayoutSet.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LayoutSet newLayoutSet = addLayoutSet();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newLayoutSet.getPrimaryKey()));
	}

	private void _assertOriginalValues(LayoutSet layoutSet) {
		Assert.assertEquals(
			Long.valueOf(layoutSet.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				layoutSet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Boolean.valueOf(layoutSet.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				layoutSet, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "privateLayout"));
	}

	protected LayoutSet addLayoutSet() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSet layoutSet = _persistence.create(pk);

		layoutSet.setMvccVersion(RandomTestUtil.nextLong());

		layoutSet.setCtCollectionId(RandomTestUtil.nextLong());

		layoutSet.setGroupId(RandomTestUtil.nextLong());

		layoutSet.setCompanyId(RandomTestUtil.nextLong());

		layoutSet.setCreateDate(RandomTestUtil.nextDate());

		layoutSet.setModifiedDate(RandomTestUtil.nextDate());

		layoutSet.setPrivateLayout(RandomTestUtil.randomBoolean());

		layoutSet.setLogoId(RandomTestUtil.nextLong());

		layoutSet.setThemeId(RandomTestUtil.randomString());

		layoutSet.setColorSchemeId(RandomTestUtil.randomString());

		layoutSet.setFaviconFileEntryId(RandomTestUtil.nextLong());

		layoutSet.setCss(RandomTestUtil.randomString());

		layoutSet.setSettings(RandomTestUtil.randomString());

		layoutSet.setLayoutSetPrototypeUuid(RandomTestUtil.randomString());

		layoutSet.setLayoutSetPrototypeLinkEnabled(
			RandomTestUtil.randomBoolean());

		_layoutSets.add(_persistence.update(layoutSet));

		return layoutSet;
	}

	private List<LayoutSet> _layoutSets = new ArrayList<LayoutSet>();
	private LayoutSetPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}