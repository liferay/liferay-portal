/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.layout.seo.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.layout.seo.exception.NoSuchEntryCustomMetaTagException;
import com.liferay.layout.seo.model.LayoutSEOEntryCustomMetaTag;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryCustomMetaTagPersistence;
import com.liferay.layout.seo.service.persistence.LayoutSEOEntryCustomMetaTagUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class LayoutSEOEntryCustomMetaTagPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.layout.seo.service"));

	@Before
	public void setUp() {
		_persistence = LayoutSEOEntryCustomMetaTagUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LayoutSEOEntryCustomMetaTag> iterator =
			_layoutSEOEntryCustomMetaTags.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag =
			_persistence.create(pk);

		Assert.assertNotNull(layoutSEOEntryCustomMetaTag);

		Assert.assertEquals(layoutSEOEntryCustomMetaTag.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag =
			addLayoutSEOEntryCustomMetaTag();

		_persistence.remove(newLayoutSEOEntryCustomMetaTag);

		LayoutSEOEntryCustomMetaTag existingLayoutSEOEntryCustomMetaTag =
			_persistence.fetchByPrimaryKey(
				newLayoutSEOEntryCustomMetaTag.getPrimaryKey());

		Assert.assertNull(existingLayoutSEOEntryCustomMetaTag);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLayoutSEOEntryCustomMetaTag();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag =
			_persistence.create(pk);

		newLayoutSEOEntryCustomMetaTag.setMvccVersion(
			RandomTestUtil.nextLong());

		newLayoutSEOEntryCustomMetaTag.setCtCollectionId(
			RandomTestUtil.nextLong());

		newLayoutSEOEntryCustomMetaTag.setGroupId(RandomTestUtil.nextLong());

		newLayoutSEOEntryCustomMetaTag.setCompanyId(RandomTestUtil.nextLong());

		newLayoutSEOEntryCustomMetaTag.setLayoutSEOEntryId(
			RandomTestUtil.nextLong());

		newLayoutSEOEntryCustomMetaTag.setContent(
			RandomTestUtil.randomString());

		newLayoutSEOEntryCustomMetaTag.setProperty(
			RandomTestUtil.randomString());

		_layoutSEOEntryCustomMetaTags.add(
			_persistence.update(newLayoutSEOEntryCustomMetaTag));

		LayoutSEOEntryCustomMetaTag existingLayoutSEOEntryCustomMetaTag =
			_persistence.findByPrimaryKey(
				newLayoutSEOEntryCustomMetaTag.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getMvccVersion(),
			newLayoutSEOEntryCustomMetaTag.getMvccVersion());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getCtCollectionId(),
			newLayoutSEOEntryCustomMetaTag.getCtCollectionId());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.
				getLayoutSEOEntryCustomMetaTagId(),
			newLayoutSEOEntryCustomMetaTag.getLayoutSEOEntryCustomMetaTagId());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getGroupId(),
			newLayoutSEOEntryCustomMetaTag.getGroupId());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getCompanyId(),
			newLayoutSEOEntryCustomMetaTag.getCompanyId());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getLayoutSEOEntryId(),
			newLayoutSEOEntryCustomMetaTag.getLayoutSEOEntryId());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getContent(),
			newLayoutSEOEntryCustomMetaTag.getContent());
		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag.getProperty(),
			newLayoutSEOEntryCustomMetaTag.getProperty());
	}

	@Test
	public void testCountByG_L() throws Exception {
		_persistence.countByG_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_L(0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag =
			addLayoutSEOEntryCustomMetaTag();

		LayoutSEOEntryCustomMetaTag existingLayoutSEOEntryCustomMetaTag =
			_persistence.findByPrimaryKey(
				newLayoutSEOEntryCustomMetaTag.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag,
			newLayoutSEOEntryCustomMetaTag);
	}

	@Test(expected = NoSuchEntryCustomMetaTagException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LayoutSEOEntryCustomMetaTag>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LayoutSEOEntryCustomMetaTag", "mvccVersion", true,
			"ctCollectionId", true, "layoutSEOEntryCustomMetaTagId", true,
			"groupId", true, "companyId", true, "layoutSEOEntryId", true,
			"content", true, "property", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag =
			addLayoutSEOEntryCustomMetaTag();

		LayoutSEOEntryCustomMetaTag existingLayoutSEOEntryCustomMetaTag =
			_persistence.fetchByPrimaryKey(
				newLayoutSEOEntryCustomMetaTag.getPrimaryKey());

		Assert.assertEquals(
			existingLayoutSEOEntryCustomMetaTag,
			newLayoutSEOEntryCustomMetaTag);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntryCustomMetaTag missingLayoutSEOEntryCustomMetaTag =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLayoutSEOEntryCustomMetaTag);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag1 =
			addLayoutSEOEntryCustomMetaTag();
		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag2 =
			addLayoutSEOEntryCustomMetaTag();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSEOEntryCustomMetaTag1.getPrimaryKey());
		primaryKeys.add(newLayoutSEOEntryCustomMetaTag2.getPrimaryKey());

		Map<Serializable, LayoutSEOEntryCustomMetaTag>
			layoutSEOEntryCustomMetaTags = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, layoutSEOEntryCustomMetaTags.size());
		Assert.assertEquals(
			newLayoutSEOEntryCustomMetaTag1,
			layoutSEOEntryCustomMetaTags.get(
				newLayoutSEOEntryCustomMetaTag1.getPrimaryKey()));
		Assert.assertEquals(
			newLayoutSEOEntryCustomMetaTag2,
			layoutSEOEntryCustomMetaTags.get(
				newLayoutSEOEntryCustomMetaTag2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LayoutSEOEntryCustomMetaTag>
			layoutSEOEntryCustomMetaTags = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutSEOEntryCustomMetaTags.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag =
			addLayoutSEOEntryCustomMetaTag();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSEOEntryCustomMetaTag.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LayoutSEOEntryCustomMetaTag>
			layoutSEOEntryCustomMetaTags = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutSEOEntryCustomMetaTags.size());
		Assert.assertEquals(
			newLayoutSEOEntryCustomMetaTag,
			layoutSEOEntryCustomMetaTags.get(
				newLayoutSEOEntryCustomMetaTag.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LayoutSEOEntryCustomMetaTag>
			layoutSEOEntryCustomMetaTags = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(layoutSEOEntryCustomMetaTags.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LayoutSEOEntryCustomMetaTag newLayoutSEOEntryCustomMetaTag =
			addLayoutSEOEntryCustomMetaTag();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLayoutSEOEntryCustomMetaTag.getPrimaryKey());

		Map<Serializable, LayoutSEOEntryCustomMetaTag>
			layoutSEOEntryCustomMetaTags = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, layoutSEOEntryCustomMetaTags.size());
		Assert.assertEquals(
			newLayoutSEOEntryCustomMetaTag,
			layoutSEOEntryCustomMetaTags.get(
				newLayoutSEOEntryCustomMetaTag.getPrimaryKey()));
	}

	protected LayoutSEOEntryCustomMetaTag addLayoutSEOEntryCustomMetaTag()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LayoutSEOEntryCustomMetaTag layoutSEOEntryCustomMetaTag =
			_persistence.create(pk);

		layoutSEOEntryCustomMetaTag.setMvccVersion(RandomTestUtil.nextLong());

		layoutSEOEntryCustomMetaTag.setCtCollectionId(
			RandomTestUtil.nextLong());

		layoutSEOEntryCustomMetaTag.setGroupId(RandomTestUtil.nextLong());

		layoutSEOEntryCustomMetaTag.setCompanyId(RandomTestUtil.nextLong());

		layoutSEOEntryCustomMetaTag.setLayoutSEOEntryId(
			RandomTestUtil.nextLong());

		layoutSEOEntryCustomMetaTag.setContent(RandomTestUtil.randomString());

		layoutSEOEntryCustomMetaTag.setProperty(RandomTestUtil.randomString());

		_layoutSEOEntryCustomMetaTags.add(
			_persistence.update(layoutSEOEntryCustomMetaTag));

		return layoutSEOEntryCustomMetaTag;
	}

	private List<LayoutSEOEntryCustomMetaTag> _layoutSEOEntryCustomMetaTags =
		new ArrayList<LayoutSEOEntryCustomMetaTag>();
	private LayoutSEOEntryCustomMetaTagPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}