/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

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
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLVEntryLocalizationException;
import com.liferay.portal.tools.service.builder.test.model.LVEntryLocalization;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LVEntryLocalizationUtil;

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
public class LVEntryLocalizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.portal.tools.service.builder.test.service"));

	@Before
	public void setUp() {
		_persistence = LVEntryLocalizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LVEntryLocalization> iterator =
			_lvEntryLocalizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalization lvEntryLocalization = _persistence.create(pk);

		Assert.assertNotNull(lvEntryLocalization);

		Assert.assertEquals(lvEntryLocalization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LVEntryLocalization newLVEntryLocalization = addLVEntryLocalization();

		_persistence.remove(newLVEntryLocalization);

		LVEntryLocalization existingLVEntryLocalization =
			_persistence.fetchByPrimaryKey(
				newLVEntryLocalization.getPrimaryKey());

		Assert.assertNull(existingLVEntryLocalization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLVEntryLocalization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalization newLVEntryLocalization = _persistence.create(pk);

		newLVEntryLocalization.setMvccVersion(RandomTestUtil.nextLong());

		newLVEntryLocalization.setHeadId(RandomTestUtil.nextLong());

		newLVEntryLocalization.setCompanyId(RandomTestUtil.nextLong());

		newLVEntryLocalization.setLvEntryId(RandomTestUtil.nextLong());

		newLVEntryLocalization.setLanguageId(RandomTestUtil.randomString());

		newLVEntryLocalization.setTitle(RandomTestUtil.randomString());

		newLVEntryLocalization.setContent(RandomTestUtil.randomString());

		_lvEntryLocalizations.add(_persistence.update(newLVEntryLocalization));

		LVEntryLocalization existingLVEntryLocalization =
			_persistence.findByPrimaryKey(
				newLVEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryLocalization.getMvccVersion(),
			newLVEntryLocalization.getMvccVersion());
		Assert.assertEquals(
			existingLVEntryLocalization.getHeadId(),
			newLVEntryLocalization.getHeadId());
		Assert.assertEquals(
			existingLVEntryLocalization.getLvEntryLocalizationId(),
			newLVEntryLocalization.getLvEntryLocalizationId());
		Assert.assertEquals(
			existingLVEntryLocalization.getCompanyId(),
			newLVEntryLocalization.getCompanyId());
		Assert.assertEquals(
			existingLVEntryLocalization.getLvEntryId(),
			newLVEntryLocalization.getLvEntryId());
		Assert.assertEquals(
			existingLVEntryLocalization.getLanguageId(),
			newLVEntryLocalization.getLanguageId());
		Assert.assertEquals(
			existingLVEntryLocalization.getTitle(),
			newLVEntryLocalization.getTitle());
		Assert.assertEquals(
			existingLVEntryLocalization.getContent(),
			newLVEntryLocalization.getContent());
	}

	@Test
	public void testCountByLvEntryId() throws Exception {
		_persistence.countByLvEntryId(RandomTestUtil.nextLong());

		_persistence.countByLvEntryId(0L);
	}

	@Test
	public void testCountByLvEntryId_LanguageId() throws Exception {
		_persistence.countByLvEntryId_LanguageId(RandomTestUtil.nextLong(), "");

		_persistence.countByLvEntryId_LanguageId(0L, "null");

		_persistence.countByLvEntryId_LanguageId(0L, (String)null);
	}

	@Test
	public void testCountByHeadId() throws Exception {
		_persistence.countByHeadId(RandomTestUtil.nextLong());

		_persistence.countByHeadId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LVEntryLocalization newLVEntryLocalization = addLVEntryLocalization();

		LVEntryLocalization existingLVEntryLocalization =
			_persistence.findByPrimaryKey(
				newLVEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryLocalization, newLVEntryLocalization);
	}

	@Test(expected = NoSuchLVEntryLocalizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LVEntryLocalization> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"LVEntryLocalization", "mvccVersion", true, "headId", true,
			"lvEntryLocalizationId", true, "companyId", true, "lvEntryId", true,
			"languageId", true, "title", true, "content", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LVEntryLocalization newLVEntryLocalization = addLVEntryLocalization();

		LVEntryLocalization existingLVEntryLocalization =
			_persistence.fetchByPrimaryKey(
				newLVEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingLVEntryLocalization, newLVEntryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalization missingLVEntryLocalization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLVEntryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LVEntryLocalization newLVEntryLocalization1 = addLVEntryLocalization();
		LVEntryLocalization newLVEntryLocalization2 = addLVEntryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryLocalization1.getPrimaryKey());
		primaryKeys.add(newLVEntryLocalization2.getPrimaryKey());

		Map<Serializable, LVEntryLocalization> lvEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, lvEntryLocalizations.size());
		Assert.assertEquals(
			newLVEntryLocalization1,
			lvEntryLocalizations.get(newLVEntryLocalization1.getPrimaryKey()));
		Assert.assertEquals(
			newLVEntryLocalization2,
			lvEntryLocalizations.get(newLVEntryLocalization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LVEntryLocalization> lvEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(lvEntryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LVEntryLocalization newLVEntryLocalization = addLVEntryLocalization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryLocalization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LVEntryLocalization> lvEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, lvEntryLocalizations.size());
		Assert.assertEquals(
			newLVEntryLocalization,
			lvEntryLocalizations.get(newLVEntryLocalization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LVEntryLocalization> lvEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(lvEntryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LVEntryLocalization newLVEntryLocalization = addLVEntryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLVEntryLocalization.getPrimaryKey());

		Map<Serializable, LVEntryLocalization> lvEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, lvEntryLocalizations.size());
		Assert.assertEquals(
			newLVEntryLocalization,
			lvEntryLocalizations.get(newLVEntryLocalization.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LVEntryLocalization newLVEntryLocalization = addLVEntryLocalization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLVEntryLocalization.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		LVEntryLocalization lvEntryLocalization) {

		Assert.assertEquals(
			Long.valueOf(lvEntryLocalization.getLvEntryId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "lvEntryId"));
		Assert.assertEquals(
			lvEntryLocalization.getLanguageId(),
			ReflectionTestUtil.invoke(
				lvEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));

		Assert.assertEquals(
			Long.valueOf(lvEntryLocalization.getHeadId()),
			ReflectionTestUtil.<Long>invoke(
				lvEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "headId"));
	}

	protected LVEntryLocalization addLVEntryLocalization() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LVEntryLocalization lvEntryLocalization = _persistence.create(pk);

		lvEntryLocalization.setMvccVersion(RandomTestUtil.nextLong());

		lvEntryLocalization.setHeadId(-pk);

		lvEntryLocalization.setCompanyId(RandomTestUtil.nextLong());

		lvEntryLocalization.setLvEntryId(RandomTestUtil.nextLong());

		lvEntryLocalization.setLanguageId(RandomTestUtil.randomString());

		lvEntryLocalization.setTitle(RandomTestUtil.randomString());

		lvEntryLocalization.setContent(RandomTestUtil.randomString());

		_lvEntryLocalizations.add(_persistence.update(lvEntryLocalization));

		return lvEntryLocalization;
	}

	private List<LVEntryLocalization> _lvEntryLocalizations =
		new ArrayList<LVEntryLocalization>();
	private LVEntryLocalizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}