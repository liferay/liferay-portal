/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.NoSuchCountryLocalizationException;
import com.liferay.portal.kernel.model.CountryLocalization;
import com.liferay.portal.kernel.service.persistence.CountryLocalizationPersistence;
import com.liferay.portal.kernel.service.persistence.CountryLocalizationUtil;
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
public class CountryLocalizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = CountryLocalizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CountryLocalization> iterator =
			_countryLocalizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CountryLocalization countryLocalization = _persistence.create(pk);

		Assert.assertNotNull(countryLocalization);

		Assert.assertEquals(countryLocalization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		_persistence.remove(newCountryLocalization);

		CountryLocalization existingCountryLocalization =
			_persistence.fetchByPrimaryKey(
				newCountryLocalization.getPrimaryKey());

		Assert.assertNull(existingCountryLocalization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCountryLocalization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		newCountryLocalization.setCtCollectionId(RandomTestUtil.nextLong());

		newCountryLocalization.setCompanyId(RandomTestUtil.nextLong());

		newCountryLocalization.setCountryId(RandomTestUtil.nextLong());

		newCountryLocalization.setLanguageId(RandomTestUtil.randomString());

		newCountryLocalization.setTitle(RandomTestUtil.randomString());

		newCountryLocalization = _persistence.update(newCountryLocalization);

		_countryLocalizations.add(newCountryLocalization);

		CountryLocalization existingCountryLocalization =
			_persistence.findByPrimaryKey(
				newCountryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCountryLocalization.getMvccVersion(),
			newCountryLocalization.getMvccVersion());
		Assert.assertEquals(
			existingCountryLocalization.getCtCollectionId(),
			newCountryLocalization.getCtCollectionId());
		Assert.assertEquals(
			existingCountryLocalization.getCountryLocalizationId(),
			newCountryLocalization.getCountryLocalizationId());
		Assert.assertEquals(
			existingCountryLocalization.getCompanyId(),
			newCountryLocalization.getCompanyId());
		Assert.assertEquals(
			existingCountryLocalization.getCountryId(),
			newCountryLocalization.getCountryId());
		Assert.assertEquals(
			existingCountryLocalization.getLanguageId(),
			newCountryLocalization.getLanguageId());
		Assert.assertEquals(
			existingCountryLocalization.getTitle(),
			newCountryLocalization.getTitle());
	}

	@Test
	public void testCountByCountryId() throws Exception {
		_persistence.countByCountryId(RandomTestUtil.nextLong());

		_persistence.countByCountryId(0L);
	}

	@Test
	public void testCountByCountryId_LanguageId() throws Exception {
		_persistence.countByCountryId_LanguageId(RandomTestUtil.nextLong(), "");

		_persistence.countByCountryId_LanguageId(0L, "null");

		_persistence.countByCountryId_LanguageId(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		CountryLocalization existingCountryLocalization =
			_persistence.findByPrimaryKey(
				newCountryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCountryLocalization, newCountryLocalization);
	}

	@Test(expected = NoSuchCountryLocalizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CountryLocalization> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CountryLocalization", "mvccVersion", true, "ctCollectionId", true,
			"countryLocalizationId", true, "companyId", true, "countryId", true,
			"languageId", true, "title", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		CountryLocalization existingCountryLocalization =
			_persistence.fetchByPrimaryKey(
				newCountryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCountryLocalization, newCountryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CountryLocalization missingCountryLocalization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCountryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CountryLocalization newCountryLocalization1 = addCountryLocalization();
		CountryLocalization newCountryLocalization2 = addCountryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCountryLocalization1.getPrimaryKey());
		primaryKeys.add(newCountryLocalization2.getPrimaryKey());

		Map<Serializable, CountryLocalization> countryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, countryLocalizations.size());
		Assert.assertEquals(
			newCountryLocalization1,
			countryLocalizations.get(newCountryLocalization1.getPrimaryKey()));
		Assert.assertEquals(
			newCountryLocalization2,
			countryLocalizations.get(newCountryLocalization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CountryLocalization> countryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(countryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CountryLocalization newCountryLocalization = addCountryLocalization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCountryLocalization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CountryLocalization> countryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, countryLocalizations.size());
		Assert.assertEquals(
			newCountryLocalization,
			countryLocalizations.get(newCountryLocalization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CountryLocalization> countryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(countryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCountryLocalization.getPrimaryKey());

		Map<Serializable, CountryLocalization> countryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, countryLocalizations.size());
		Assert.assertEquals(
			newCountryLocalization,
			countryLocalizations.get(newCountryLocalization.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CountryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"countryLocalizationId",
				newCountryLocalization.getCountryLocalizationId()));

		List<CountryLocalization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CountryLocalization existingCountryLocalization = result.get(0);

		Assert.assertEquals(
			existingCountryLocalization, newCountryLocalization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CountryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"countryLocalizationId", RandomTestUtil.nextLong()));

		List<CountryLocalization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CountryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("countryLocalizationId"));

		Object newCountryLocalizationId =
			newCountryLocalization.getCountryLocalizationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"countryLocalizationId",
				new Object[] {newCountryLocalizationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCountryLocalizationId = result.get(0);

		Assert.assertEquals(
			existingCountryLocalizationId, newCountryLocalizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CountryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("countryLocalizationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"countryLocalizationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CountryLocalization newCountryLocalization = addCountryLocalization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCountryLocalization.getPrimaryKey()));
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

		CountryLocalization newCountryLocalization = addCountryLocalization();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CountryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"countryLocalizationId",
				newCountryLocalization.getCountryLocalizationId()));

		List<CountryLocalization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CountryLocalization countryLocalization) {

		Assert.assertEquals(
			Long.valueOf(countryLocalization.getCountryId()),
			ReflectionTestUtil.<Long>invoke(
				countryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "countryId"));
		Assert.assertEquals(
			countryLocalization.getLanguageId(),
			ReflectionTestUtil.invoke(
				countryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected CountryLocalization addCountryLocalization() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CountryLocalization countryLocalization = _persistence.create(pk);

		countryLocalization.setCtCollectionId(RandomTestUtil.nextLong());

		countryLocalization.setCompanyId(RandomTestUtil.nextLong());

		countryLocalization.setCountryId(RandomTestUtil.nextLong());

		countryLocalization.setLanguageId(RandomTestUtil.randomString());

		countryLocalization.setTitle(RandomTestUtil.randomString());

		_countryLocalizations.add(_persistence.update(countryLocalization));

		return countryLocalization;
	}

	private List<CountryLocalization> _countryLocalizations =
		new ArrayList<CountryLocalization>();
	private CountryLocalizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:828056412