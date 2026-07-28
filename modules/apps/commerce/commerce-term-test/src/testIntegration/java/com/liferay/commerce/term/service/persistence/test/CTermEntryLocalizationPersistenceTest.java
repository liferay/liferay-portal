/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.term.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.term.exception.NoSuchCTermEntryLocalizationException;
import com.liferay.commerce.term.model.CTermEntryLocalization;
import com.liferay.commerce.term.service.persistence.CTermEntryLocalizationPersistence;
import com.liferay.commerce.term.service.persistence.CTermEntryLocalizationUtil;
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
public class CTermEntryLocalizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.term.service"));

	@Before
	public void setUp() {
		_persistence = CTermEntryLocalizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CTermEntryLocalization> iterator =
			_cTermEntryLocalizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTermEntryLocalization cTermEntryLocalization = _persistence.create(pk);

		Assert.assertNotNull(cTermEntryLocalization);

		Assert.assertEquals(cTermEntryLocalization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		_persistence.remove(newCTermEntryLocalization);

		CTermEntryLocalization existingCTermEntryLocalization =
			_persistence.fetchByPrimaryKey(
				newCTermEntryLocalization.getPrimaryKey());

		Assert.assertNull(existingCTermEntryLocalization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCTermEntryLocalization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		newCTermEntryLocalization.setCompanyId(RandomTestUtil.nextLong());

		newCTermEntryLocalization.setCommerceTermEntryId(
			RandomTestUtil.nextLong());

		newCTermEntryLocalization.setLanguageId(RandomTestUtil.randomString());

		newCTermEntryLocalization.setDescription(RandomTestUtil.randomString());

		newCTermEntryLocalization.setLabel(RandomTestUtil.randomString());

		newCTermEntryLocalization = _persistence.update(
			newCTermEntryLocalization);

		_cTermEntryLocalizations.add(newCTermEntryLocalization);

		CTermEntryLocalization existingCTermEntryLocalization =
			_persistence.findByPrimaryKey(
				newCTermEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCTermEntryLocalization.getMvccVersion(),
			newCTermEntryLocalization.getMvccVersion());
		Assert.assertEquals(
			existingCTermEntryLocalization.getCTermEntryLocalizationId(),
			newCTermEntryLocalization.getCTermEntryLocalizationId());
		Assert.assertEquals(
			existingCTermEntryLocalization.getCompanyId(),
			newCTermEntryLocalization.getCompanyId());
		Assert.assertEquals(
			existingCTermEntryLocalization.getCommerceTermEntryId(),
			newCTermEntryLocalization.getCommerceTermEntryId());
		Assert.assertEquals(
			existingCTermEntryLocalization.getLanguageId(),
			newCTermEntryLocalization.getLanguageId());
		Assert.assertEquals(
			existingCTermEntryLocalization.getDescription(),
			newCTermEntryLocalization.getDescription());
		Assert.assertEquals(
			existingCTermEntryLocalization.getLabel(),
			newCTermEntryLocalization.getLabel());
	}

	@Test
	public void testCountByCommerceTermEntryId() throws Exception {
		_persistence.countByCommerceTermEntryId(RandomTestUtil.nextLong());

		_persistence.countByCommerceTermEntryId(0L);
	}

	@Test
	public void testCountByCommerceTermEntryId_LanguageId() throws Exception {
		_persistence.countByCommerceTermEntryId_LanguageId(
			RandomTestUtil.nextLong(), "");

		_persistence.countByCommerceTermEntryId_LanguageId(0L, "null");

		_persistence.countByCommerceTermEntryId_LanguageId(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		CTermEntryLocalization existingCTermEntryLocalization =
			_persistence.findByPrimaryKey(
				newCTermEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCTermEntryLocalization, newCTermEntryLocalization);
	}

	@Test(expected = NoSuchCTermEntryLocalizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CTermEntryLocalization> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CTermEntryLocalization", "mvccVersion", true,
			"cTermEntryLocalizationId", true, "companyId", true,
			"commerceTermEntryId", true, "languageId", true, "label", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		CTermEntryLocalization existingCTermEntryLocalization =
			_persistence.fetchByPrimaryKey(
				newCTermEntryLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCTermEntryLocalization, newCTermEntryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CTermEntryLocalization missingCTermEntryLocalization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCTermEntryLocalization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CTermEntryLocalization newCTermEntryLocalization1 =
			addCTermEntryLocalization();
		CTermEntryLocalization newCTermEntryLocalization2 =
			addCTermEntryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTermEntryLocalization1.getPrimaryKey());
		primaryKeys.add(newCTermEntryLocalization2.getPrimaryKey());

		Map<Serializable, CTermEntryLocalization> cTermEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cTermEntryLocalizations.size());
		Assert.assertEquals(
			newCTermEntryLocalization1,
			cTermEntryLocalizations.get(
				newCTermEntryLocalization1.getPrimaryKey()));
		Assert.assertEquals(
			newCTermEntryLocalization2,
			cTermEntryLocalizations.get(
				newCTermEntryLocalization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CTermEntryLocalization> cTermEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cTermEntryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTermEntryLocalization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CTermEntryLocalization> cTermEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cTermEntryLocalizations.size());
		Assert.assertEquals(
			newCTermEntryLocalization,
			cTermEntryLocalizations.get(
				newCTermEntryLocalization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CTermEntryLocalization> cTermEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cTermEntryLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCTermEntryLocalization.getPrimaryKey());

		Map<Serializable, CTermEntryLocalization> cTermEntryLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cTermEntryLocalizations.size());
		Assert.assertEquals(
			newCTermEntryLocalization,
			cTermEntryLocalizations.get(
				newCTermEntryLocalization.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTermEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cTermEntryLocalizationId",
				newCTermEntryLocalization.getCTermEntryLocalizationId()));

		List<CTermEntryLocalization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CTermEntryLocalization existingCTermEntryLocalization = result.get(0);

		Assert.assertEquals(
			existingCTermEntryLocalization, newCTermEntryLocalization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTermEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cTermEntryLocalizationId", RandomTestUtil.nextLong()));

		List<CTermEntryLocalization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTermEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cTermEntryLocalizationId"));

		Object newCTermEntryLocalizationId =
			newCTermEntryLocalization.getCTermEntryLocalizationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cTermEntryLocalizationId",
				new Object[] {newCTermEntryLocalizationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCTermEntryLocalizationId = result.get(0);

		Assert.assertEquals(
			existingCTermEntryLocalizationId, newCTermEntryLocalizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTermEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cTermEntryLocalizationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cTermEntryLocalizationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCTermEntryLocalization.getPrimaryKey()));
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

		CTermEntryLocalization newCTermEntryLocalization =
			addCTermEntryLocalization();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CTermEntryLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cTermEntryLocalizationId",
				newCTermEntryLocalization.getCTermEntryLocalizationId()));

		List<CTermEntryLocalization> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CTermEntryLocalization cTermEntryLocalization) {

		Assert.assertEquals(
			Long.valueOf(cTermEntryLocalization.getCommerceTermEntryId()),
			ReflectionTestUtil.<Long>invoke(
				cTermEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceTermEntryId"));
		Assert.assertEquals(
			cTermEntryLocalization.getLanguageId(),
			ReflectionTestUtil.invoke(
				cTermEntryLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected CTermEntryLocalization addCTermEntryLocalization()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CTermEntryLocalization cTermEntryLocalization = _persistence.create(pk);

		cTermEntryLocalization.setCompanyId(RandomTestUtil.nextLong());

		cTermEntryLocalization.setCommerceTermEntryId(
			RandomTestUtil.nextLong());

		cTermEntryLocalization.setLanguageId(RandomTestUtil.randomString());

		cTermEntryLocalization.setDescription(RandomTestUtil.randomString());

		cTermEntryLocalization.setLabel(RandomTestUtil.randomString());

		_cTermEntryLocalizations.add(
			_persistence.update(cTermEntryLocalization));

		return cTermEntryLocalization;
	}

	private List<CTermEntryLocalization> _cTermEntryLocalizations =
		new ArrayList<CTermEntryLocalization>();
	private CTermEntryLocalizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:821465778