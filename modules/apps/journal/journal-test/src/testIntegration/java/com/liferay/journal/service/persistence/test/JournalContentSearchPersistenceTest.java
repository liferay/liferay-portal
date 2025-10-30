/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.exception.NoSuchContentSearchException;
import com.liferay.journal.model.JournalContentSearch;
import com.liferay.journal.service.persistence.JournalContentSearchPersistence;
import com.liferay.journal.service.persistence.JournalContentSearchUtil;
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
public class JournalContentSearchPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.journal.service"));

	@Before
	public void setUp() {
		_persistence = JournalContentSearchUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JournalContentSearch> iterator =
			_journalContentSearchs.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalContentSearch journalContentSearch = _persistence.create(pk);

		Assert.assertNotNull(journalContentSearch);

		Assert.assertEquals(journalContentSearch.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JournalContentSearch newJournalContentSearch =
			addJournalContentSearch();

		_persistence.remove(newJournalContentSearch);

		JournalContentSearch existingJournalContentSearch =
			_persistence.fetchByPrimaryKey(
				newJournalContentSearch.getPrimaryKey());

		Assert.assertNull(existingJournalContentSearch);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJournalContentSearch();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalContentSearch newJournalContentSearch = _persistence.create(pk);

		newJournalContentSearch.setMvccVersion(RandomTestUtil.nextLong());

		newJournalContentSearch.setCtCollectionId(RandomTestUtil.nextLong());

		newJournalContentSearch.setGroupId(RandomTestUtil.nextLong());

		newJournalContentSearch.setCompanyId(RandomTestUtil.nextLong());

		newJournalContentSearch.setPrivateLayout(
			RandomTestUtil.randomBoolean());

		newJournalContentSearch.setLayoutId(RandomTestUtil.nextLong());

		newJournalContentSearch.setPortletId(RandomTestUtil.randomString());

		newJournalContentSearch.setArticleId(RandomTestUtil.randomString());

		_journalContentSearchs.add(
			_persistence.update(newJournalContentSearch));

		JournalContentSearch existingJournalContentSearch =
			_persistence.findByPrimaryKey(
				newJournalContentSearch.getPrimaryKey());

		Assert.assertEquals(
			existingJournalContentSearch.getMvccVersion(),
			newJournalContentSearch.getMvccVersion());
		Assert.assertEquals(
			existingJournalContentSearch.getCtCollectionId(),
			newJournalContentSearch.getCtCollectionId());
		Assert.assertEquals(
			existingJournalContentSearch.getContentSearchId(),
			newJournalContentSearch.getContentSearchId());
		Assert.assertEquals(
			existingJournalContentSearch.getGroupId(),
			newJournalContentSearch.getGroupId());
		Assert.assertEquals(
			existingJournalContentSearch.getCompanyId(),
			newJournalContentSearch.getCompanyId());
		Assert.assertEquals(
			existingJournalContentSearch.isPrivateLayout(),
			newJournalContentSearch.isPrivateLayout());
		Assert.assertEquals(
			existingJournalContentSearch.getLayoutId(),
			newJournalContentSearch.getLayoutId());
		Assert.assertEquals(
			existingJournalContentSearch.getPortletId(),
			newJournalContentSearch.getPortletId());
		Assert.assertEquals(
			existingJournalContentSearch.getArticleId(),
			newJournalContentSearch.getArticleId());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByPortletId() throws Exception {
		_persistence.countByPortletId("");

		_persistence.countByPortletId("null");

		_persistence.countByPortletId((String)null);
	}

	@Test
	public void testCountByArticleId() throws Exception {
		_persistence.countByArticleId("");

		_persistence.countByArticleId("null");

		_persistence.countByArticleId((String)null);
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(RandomTestUtil.nextLong(), "");

		_persistence.countByG_A(0L, "null");

		_persistence.countByG_A(0L, (String)null);
	}

	@Test
	public void testCountByG_P_L() throws Exception {
		_persistence.countByG_P_L(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong());

		_persistence.countByG_P_L(0L, RandomTestUtil.randomBoolean(), 0L);
	}

	@Test
	public void testCountByG_P_A() throws Exception {
		_persistence.countByG_P_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(), "");

		_persistence.countByG_P_A(0L, RandomTestUtil.randomBoolean(), "null");

		_persistence.countByG_P_A(
			0L, RandomTestUtil.randomBoolean(), (String)null);
	}

	@Test
	public void testCountByG_P_L_P() throws Exception {
		_persistence.countByG_P_L_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), "");

		_persistence.countByG_P_L_P(
			0L, RandomTestUtil.randomBoolean(), 0L, "null");

		_persistence.countByG_P_L_P(
			0L, RandomTestUtil.randomBoolean(), 0L, (String)null);
	}

	@Test
	public void testCountByG_P_L_P_A() throws Exception {
		_persistence.countByG_P_L_P_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.nextLong(), "", "");

		_persistence.countByG_P_L_P_A(
			0L, RandomTestUtil.randomBoolean(), 0L, "null", "null");

		_persistence.countByG_P_L_P_A(
			0L, RandomTestUtil.randomBoolean(), 0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JournalContentSearch newJournalContentSearch =
			addJournalContentSearch();

		JournalContentSearch existingJournalContentSearch =
			_persistence.findByPrimaryKey(
				newJournalContentSearch.getPrimaryKey());

		Assert.assertEquals(
			existingJournalContentSearch, newJournalContentSearch);
	}

	@Test(expected = NoSuchContentSearchException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<JournalContentSearch> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"JournalContentSearch", "mvccVersion", true, "ctCollectionId", true,
			"contentSearchId", true, "groupId", true, "companyId", true,
			"privateLayout", true, "layoutId", true, "portletId", true,
			"articleId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JournalContentSearch newJournalContentSearch =
			addJournalContentSearch();

		JournalContentSearch existingJournalContentSearch =
			_persistence.fetchByPrimaryKey(
				newJournalContentSearch.getPrimaryKey());

		Assert.assertEquals(
			existingJournalContentSearch, newJournalContentSearch);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalContentSearch missingJournalContentSearch =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingJournalContentSearch);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JournalContentSearch newJournalContentSearch1 =
			addJournalContentSearch();
		JournalContentSearch newJournalContentSearch2 =
			addJournalContentSearch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalContentSearch1.getPrimaryKey());
		primaryKeys.add(newJournalContentSearch2.getPrimaryKey());

		Map<Serializable, JournalContentSearch> journalContentSearchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, journalContentSearchs.size());
		Assert.assertEquals(
			newJournalContentSearch1,
			journalContentSearchs.get(
				newJournalContentSearch1.getPrimaryKey()));
		Assert.assertEquals(
			newJournalContentSearch2,
			journalContentSearchs.get(
				newJournalContentSearch2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JournalContentSearch> journalContentSearchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalContentSearchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JournalContentSearch newJournalContentSearch =
			addJournalContentSearch();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalContentSearch.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JournalContentSearch> journalContentSearchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalContentSearchs.size());
		Assert.assertEquals(
			newJournalContentSearch,
			journalContentSearchs.get(newJournalContentSearch.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JournalContentSearch> journalContentSearchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(journalContentSearchs.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JournalContentSearch newJournalContentSearch =
			addJournalContentSearch();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalContentSearch.getPrimaryKey());

		Map<Serializable, JournalContentSearch> journalContentSearchs =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, journalContentSearchs.size());
		Assert.assertEquals(
			newJournalContentSearch,
			journalContentSearchs.get(newJournalContentSearch.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JournalContentSearch newJournalContentSearch =
			addJournalContentSearch();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newJournalContentSearch.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		JournalContentSearch journalContentSearch) {

		Assert.assertEquals(
			Long.valueOf(journalContentSearch.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				journalContentSearch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Boolean.valueOf(journalContentSearch.getPrivateLayout()),
			ReflectionTestUtil.<Boolean>invoke(
				journalContentSearch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "privateLayout"));
		Assert.assertEquals(
			Long.valueOf(journalContentSearch.getLayoutId()),
			ReflectionTestUtil.<Long>invoke(
				journalContentSearch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "layoutId"));
		Assert.assertEquals(
			journalContentSearch.getPortletId(),
			ReflectionTestUtil.invoke(
				journalContentSearch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "portletId"));
		Assert.assertEquals(
			journalContentSearch.getArticleId(),
			ReflectionTestUtil.invoke(
				journalContentSearch, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "articleId"));
	}

	protected JournalContentSearch addJournalContentSearch() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalContentSearch journalContentSearch = _persistence.create(pk);

		journalContentSearch.setMvccVersion(RandomTestUtil.nextLong());

		journalContentSearch.setCtCollectionId(RandomTestUtil.nextLong());

		journalContentSearch.setGroupId(RandomTestUtil.nextLong());

		journalContentSearch.setCompanyId(RandomTestUtil.nextLong());

		journalContentSearch.setPrivateLayout(RandomTestUtil.randomBoolean());

		journalContentSearch.setLayoutId(RandomTestUtil.nextLong());

		journalContentSearch.setPortletId(RandomTestUtil.randomString());

		journalContentSearch.setArticleId(RandomTestUtil.randomString());

		_journalContentSearchs.add(_persistence.update(journalContentSearch));

		return journalContentSearch;
	}

	private List<JournalContentSearch> _journalContentSearchs =
		new ArrayList<JournalContentSearch>();
	private JournalContentSearchPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}