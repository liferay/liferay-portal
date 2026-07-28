/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.tools.service.builder.test.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
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
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.test.rule.LiferayIntegrationTestRule;
import com.liferay.portal.test.rule.PersistenceTestRule;
import com.liferay.portal.test.rule.TransactionalTestRule;
import com.liferay.portal.tools.service.builder.test.exception.NoSuchLocalizedEntryLocalizationException;
import com.liferay.portal.tools.service.builder.test.model.LocalizedEntryLocalization;
import com.liferay.portal.tools.service.builder.test.service.persistence.LocalizedEntryLocalizationPersistence;
import com.liferay.portal.tools.service.builder.test.service.persistence.LocalizedEntryLocalizationUtil;

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
public class LocalizedEntryLocalizationPersistenceTest {

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
		_persistence = LocalizedEntryLocalizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<LocalizedEntryLocalization> iterator =
			_localizedEntryLocalizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LocalizedEntryLocalization localizedEntryLocalization =
			_persistence.create(pk);

		Assert.assertNotNull(localizedEntryLocalization);

		Assert.assertEquals(localizedEntryLocalization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		_persistence.remove(newLocalizedEntryLocalization);

		LocalizedEntryLocalization existingLocalizedEntryLocalization =
			_persistence.fetchByPrimaryKey(
				newLocalizedEntryLocalization.getPrimaryKey());

		Assert.assertNull(existingLocalizedEntryLocalization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addLocalizedEntryLocalization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		newLocalizedEntryLocalization.setLocalizedEntryId(
			RandomTestUtil.nextLong());

		newLocalizedEntryLocalization.setLanguageId(
			RandomTestUtil.randomString());

		newLocalizedEntryLocalization.setTitle(RandomTestUtil.randomString());

		newLocalizedEntryLocalization.setContent(RandomTestUtil.randomString());

		newLocalizedEntryLocalization = _persistence.update(
			newLocalizedEntryLocalization);

		_localizedEntryLocalizations.add(newLocalizedEntryLocalization);

		LocalizedEntryLocalization existingLocalizedEntryLocalization =
			_persistence.findByPrimaryKey(
				newLocalizedEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingLocalizedEntryLocalization.getMvccVersion(),
			newLocalizedEntryLocalization.getMvccVersion());
		Assert.assertEquals(
			existingLocalizedEntryLocalization.
				getLocalizedEntryLocalizationId(),
			newLocalizedEntryLocalization.getLocalizedEntryLocalizationId());
		Assert.assertEquals(
			existingLocalizedEntryLocalization.getLocalizedEntryId(),
			newLocalizedEntryLocalization.getLocalizedEntryId());
		Assert.assertEquals(
			existingLocalizedEntryLocalization.getLanguageId(),
			newLocalizedEntryLocalization.getLanguageId());
		Assert.assertEquals(
			existingLocalizedEntryLocalization.getTitle(),
			newLocalizedEntryLocalization.getTitle());
		Assert.assertEquals(
			existingLocalizedEntryLocalization.getContent(),
			newLocalizedEntryLocalization.getContent());
	}

	@Test
	public void testCountByLocalizedEntryId() throws Exception {
		_persistence.countByLocalizedEntryId(RandomTestUtil.nextLong());

		_persistence.countByLocalizedEntryId(0L);
	}

	@Test
	public void testCountByLocalizedEntryId_LanguageId() throws Exception {
		_persistence.countByLocalizedEntryId_LanguageId(
			RandomTestUtil.nextLong(), "");

		_persistence.countByLocalizedEntryId_LanguageId(0L, "null");

		_persistence.countByLocalizedEntryId_LanguageId(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		LocalizedEntryLocalization existingLocalizedEntryLocalization =
			_persistence.findByPrimaryKey(
				newLocalizedEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingLocalizedEntryLocalization, newLocalizedEntryLocalization);
	}

	@Test(expected = NoSuchLocalizedEntryLocalizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<LocalizedEntryLocalization>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"LocalizedEntryLocalization", "mvccVersion", true,
			"localizedEntryLocalizationId", true, "localizedEntryId", true,
			"languageId", true, "title", true, "content", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		LocalizedEntryLocalization existingLocalizedEntryLocalization =
			_persistence.fetchByPrimaryKey(
				newLocalizedEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingLocalizedEntryLocalization, newLocalizedEntryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		LocalizedEntryLocalization missingLocalizedEntryLocalization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingLocalizedEntryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		LocalizedEntryLocalization newLocalizedEntryLocalization1 =
			addLocalizedEntryLocalization();
		LocalizedEntryLocalization newLocalizedEntryLocalization2 =
			addLocalizedEntryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLocalizedEntryLocalization1.getPrimaryKey());
		primaryKeys.add(newLocalizedEntryLocalization2.getPrimaryKey());

		Map<Serializable, LocalizedEntryLocalization>
			localizedEntryLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, localizedEntryLocalizations.size());
		Assert.assertEquals(
			newLocalizedEntryLocalization1,
			localizedEntryLocalizations.get(
				newLocalizedEntryLocalization1.getPrimaryKey()));
		Assert.assertEquals(
			newLocalizedEntryLocalization2,
			localizedEntryLocalizations.get(
				newLocalizedEntryLocalization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, LocalizedEntryLocalization>
			localizedEntryLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(localizedEntryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLocalizedEntryLocalization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, LocalizedEntryLocalization>
			localizedEntryLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, localizedEntryLocalizations.size());
		Assert.assertEquals(
			newLocalizedEntryLocalization,
			localizedEntryLocalizations.get(
				newLocalizedEntryLocalization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, LocalizedEntryLocalization>
			localizedEntryLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(localizedEntryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newLocalizedEntryLocalization.getPrimaryKey());

		Map<Serializable, LocalizedEntryLocalization>
			localizedEntryLocalizations = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, localizedEntryLocalizations.size());
		Assert.assertEquals(
			newLocalizedEntryLocalization,
			localizedEntryLocalizations.get(
				newLocalizedEntryLocalization.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LocalizedEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"localizedEntryLocalizationId",
				newLocalizedEntryLocalization.
					getLocalizedEntryLocalizationId()));

		List<LocalizedEntryLocalization> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		LocalizedEntryLocalization existingLocalizedEntryLocalization =
			result.get(0);

		Assert.assertEquals(
			existingLocalizedEntryLocalization, newLocalizedEntryLocalization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LocalizedEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"localizedEntryLocalizationId", RandomTestUtil.nextLong()));

		List<LocalizedEntryLocalization> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LocalizedEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("localizedEntryLocalizationId"));

		Object newLocalizedEntryLocalizationId =
			newLocalizedEntryLocalization.getLocalizedEntryLocalizationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"localizedEntryLocalizationId",
				new Object[] {newLocalizedEntryLocalizationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingLocalizedEntryLocalizationId = result.get(0);

		Assert.assertEquals(
			existingLocalizedEntryLocalizationId,
			newLocalizedEntryLocalizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LocalizedEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("localizedEntryLocalizationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"localizedEntryLocalizationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newLocalizedEntryLocalization.getPrimaryKey()));
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

		LocalizedEntryLocalization newLocalizedEntryLocalization =
			addLocalizedEntryLocalization();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			LocalizedEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"localizedEntryLocalizationId",
				newLocalizedEntryLocalization.
					getLocalizedEntryLocalizationId()));

		List<LocalizedEntryLocalization> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		LocalizedEntryLocalization localizedEntryLocalization) {

		Assert.assertEquals(
			Long.valueOf(localizedEntryLocalization.getLocalizedEntryId()),
			ReflectionTestUtil.<Long>invoke(
				localizedEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "localizedEntryId"));
		Assert.assertEquals(
			localizedEntryLocalization.getLanguageId(),
			ReflectionTestUtil.invoke(
				localizedEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected LocalizedEntryLocalization addLocalizedEntryLocalization()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		LocalizedEntryLocalization localizedEntryLocalization =
			_persistence.create(pk);

		localizedEntryLocalization.setLocalizedEntryId(
			RandomTestUtil.nextLong());

		localizedEntryLocalization.setLanguageId(RandomTestUtil.randomString());

		localizedEntryLocalization.setTitle(RandomTestUtil.randomString());

		localizedEntryLocalization.setContent(RandomTestUtil.randomString());

		_localizedEntryLocalizations.add(
			_persistence.update(localizedEntryLocalization));

		return localizedEntryLocalization;
	}

	private List<LocalizedEntryLocalization> _localizedEntryLocalizations =
		new ArrayList<LocalizedEntryLocalization>();
	private LocalizedEntryLocalizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:280351175