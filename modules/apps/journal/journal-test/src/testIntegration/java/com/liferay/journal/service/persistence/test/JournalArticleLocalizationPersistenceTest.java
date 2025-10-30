/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.journal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.journal.exception.NoSuchArticleLocalizationException;
import com.liferay.journal.model.JournalArticleLocalization;
import com.liferay.journal.service.persistence.JournalArticleLocalizationPersistence;
import com.liferay.journal.service.persistence.JournalArticleLocalizationUtil;
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
public class JournalArticleLocalizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.journal.service"));

	@Before
	public void setUp() {
		_persistence = JournalArticleLocalizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<JournalArticleLocalization> iterator =
			_journalArticleLocalizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticleLocalization journalArticleLocalization =
			_persistence.create(pk);

		Assert.assertNotNull(journalArticleLocalization);

		Assert.assertEquals(journalArticleLocalization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		JournalArticleLocalization newJournalArticleLocalization =
			addJournalArticleLocalization();

		_persistence.remove(newJournalArticleLocalization);

		JournalArticleLocalization existingJournalArticleLocalization =
			_persistence.fetchByPrimaryKey(
				newJournalArticleLocalization.getPrimaryKey());

		Assert.assertNull(existingJournalArticleLocalization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addJournalArticleLocalization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticleLocalization newJournalArticleLocalization =
			_persistence.create(pk);

		newJournalArticleLocalization.setMvccVersion(RandomTestUtil.nextLong());

		newJournalArticleLocalization.setCtCollectionId(
			RandomTestUtil.nextLong());

		newJournalArticleLocalization.setCompanyId(RandomTestUtil.nextLong());

		newJournalArticleLocalization.setArticlePK(RandomTestUtil.nextLong());

		newJournalArticleLocalization.setTitle(RandomTestUtil.randomString());

		newJournalArticleLocalization.setDescription(
			RandomTestUtil.randomString());

		newJournalArticleLocalization.setLanguageId(
			RandomTestUtil.randomString());

		_journalArticleLocalizations.add(
			_persistence.update(newJournalArticleLocalization));

		JournalArticleLocalization existingJournalArticleLocalization =
			_persistence.findByPrimaryKey(
				newJournalArticleLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticleLocalization.getMvccVersion(),
			newJournalArticleLocalization.getMvccVersion());
		Assert.assertEquals(
			existingJournalArticleLocalization.getCtCollectionId(),
			newJournalArticleLocalization.getCtCollectionId());
		Assert.assertEquals(
			existingJournalArticleLocalization.getArticleLocalizationId(),
			newJournalArticleLocalization.getArticleLocalizationId());
		Assert.assertEquals(
			existingJournalArticleLocalization.getCompanyId(),
			newJournalArticleLocalization.getCompanyId());
		Assert.assertEquals(
			existingJournalArticleLocalization.getArticlePK(),
			newJournalArticleLocalization.getArticlePK());
		Assert.assertEquals(
			existingJournalArticleLocalization.getTitle(),
			newJournalArticleLocalization.getTitle());
		Assert.assertEquals(
			existingJournalArticleLocalization.getDescription(),
			newJournalArticleLocalization.getDescription());
		Assert.assertEquals(
			existingJournalArticleLocalization.getLanguageId(),
			newJournalArticleLocalization.getLanguageId());
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_A(0L, 0L);
	}

	@Test
	public void testCountByC_A_L() throws Exception {
		_persistence.countByC_A_L(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(), "");

		_persistence.countByC_A_L(0L, 0L, "null");

		_persistence.countByC_A_L(0L, 0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		JournalArticleLocalization newJournalArticleLocalization =
			addJournalArticleLocalization();

		JournalArticleLocalization existingJournalArticleLocalization =
			_persistence.findByPrimaryKey(
				newJournalArticleLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticleLocalization, newJournalArticleLocalization);
	}

	@Test(expected = NoSuchArticleLocalizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<JournalArticleLocalization>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"JournalArticleLocalization", "mvccVersion", true, "ctCollectionId",
			true, "articleLocalizationId", true, "companyId", true, "articlePK",
			true, "title", true, "description", true, "languageId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		JournalArticleLocalization newJournalArticleLocalization =
			addJournalArticleLocalization();

		JournalArticleLocalization existingJournalArticleLocalization =
			_persistence.fetchByPrimaryKey(
				newJournalArticleLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingJournalArticleLocalization, newJournalArticleLocalization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		JournalArticleLocalization missingJournalArticleLocalization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingJournalArticleLocalization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		JournalArticleLocalization newJournalArticleLocalization1 =
			addJournalArticleLocalization();
		JournalArticleLocalization newJournalArticleLocalization2 =
			addJournalArticleLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticleLocalization1.getPrimaryKey());
		primaryKeys.add(newJournalArticleLocalization2.getPrimaryKey());

		Map<Serializable, JournalArticleLocalization>
			journalArticleLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, journalArticleLocalizations.size());
		Assert.assertEquals(
			newJournalArticleLocalization1,
			journalArticleLocalizations.get(
				newJournalArticleLocalization1.getPrimaryKey()));
		Assert.assertEquals(
			newJournalArticleLocalization2,
			journalArticleLocalizations.get(
				newJournalArticleLocalization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, JournalArticleLocalization>
			journalArticleLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(journalArticleLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		JournalArticleLocalization newJournalArticleLocalization =
			addJournalArticleLocalization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticleLocalization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, JournalArticleLocalization>
			journalArticleLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, journalArticleLocalizations.size());
		Assert.assertEquals(
			newJournalArticleLocalization,
			journalArticleLocalizations.get(
				newJournalArticleLocalization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, JournalArticleLocalization>
			journalArticleLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(journalArticleLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		JournalArticleLocalization newJournalArticleLocalization =
			addJournalArticleLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newJournalArticleLocalization.getPrimaryKey());

		Map<Serializable, JournalArticleLocalization>
			journalArticleLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, journalArticleLocalizations.size());
		Assert.assertEquals(
			newJournalArticleLocalization,
			journalArticleLocalizations.get(
				newJournalArticleLocalization.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		JournalArticleLocalization newJournalArticleLocalization =
			addJournalArticleLocalization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newJournalArticleLocalization.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		JournalArticleLocalization journalArticleLocalization) {

		Assert.assertEquals(
			Long.valueOf(journalArticleLocalization.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				journalArticleLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			Long.valueOf(journalArticleLocalization.getArticlePK()),
			ReflectionTestUtil.<Long>invoke(
				journalArticleLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "articlePK"));
		Assert.assertEquals(
			journalArticleLocalization.getLanguageId(),
			ReflectionTestUtil.invoke(
				journalArticleLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected JournalArticleLocalization addJournalArticleLocalization()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		JournalArticleLocalization journalArticleLocalization =
			_persistence.create(pk);

		journalArticleLocalization.setMvccVersion(RandomTestUtil.nextLong());

		journalArticleLocalization.setCtCollectionId(RandomTestUtil.nextLong());

		journalArticleLocalization.setCompanyId(RandomTestUtil.nextLong());

		journalArticleLocalization.setArticlePK(RandomTestUtil.nextLong());

		journalArticleLocalization.setTitle(RandomTestUtil.randomString());

		journalArticleLocalization.setDescription(
			RandomTestUtil.randomString());

		journalArticleLocalization.setLanguageId(RandomTestUtil.randomString());

		_journalArticleLocalizations.add(
			_persistence.update(journalArticleLocalization));

		return journalArticleLocalization;
	}

	private List<JournalArticleLocalization> _journalArticleLocalizations =
		new ArrayList<JournalArticleLocalization>();
	private JournalArticleLocalizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}