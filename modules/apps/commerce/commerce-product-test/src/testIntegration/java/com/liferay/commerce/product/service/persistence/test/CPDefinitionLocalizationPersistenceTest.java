/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionLocalizationException;
import com.liferay.commerce.product.model.CPDefinitionLocalization;
import com.liferay.commerce.product.service.persistence.CPDefinitionLocalizationPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionLocalizationUtil;
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
public class CPDefinitionLocalizationPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionLocalizationUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinitionLocalization> iterator =
			_cpDefinitionLocalizations.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionLocalization cpDefinitionLocalization = _persistence.create(
			pk);

		Assert.assertNotNull(cpDefinitionLocalization);

		Assert.assertEquals(cpDefinitionLocalization.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		_persistence.remove(newCPDefinitionLocalization);

		CPDefinitionLocalization existingCPDefinitionLocalization =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionLocalization.getPrimaryKey());

		Assert.assertNull(existingCPDefinitionLocalization);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinitionLocalization();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionLocalization newCPDefinitionLocalization =
			_persistence.create(pk);

		newCPDefinitionLocalization.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinitionLocalization.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCPDefinitionLocalization.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinitionLocalization.setCPDefinitionId(
			RandomTestUtil.nextLong());

		newCPDefinitionLocalization.setLanguageId(
			RandomTestUtil.randomString());

		newCPDefinitionLocalization.setCProductId(RandomTestUtil.nextLong());

		newCPDefinitionLocalization.setName(RandomTestUtil.randomString());

		newCPDefinitionLocalization.setShortDescription(
			RandomTestUtil.randomString());

		newCPDefinitionLocalization.setDescription(
			RandomTestUtil.randomString());

		newCPDefinitionLocalization.setMetaTitle(RandomTestUtil.randomString());

		newCPDefinitionLocalization.setMetaDescription(
			RandomTestUtil.randomString());

		newCPDefinitionLocalization.setMetaKeywords(
			RandomTestUtil.randomString());

		_cpDefinitionLocalizations.add(
			_persistence.update(newCPDefinitionLocalization));

		CPDefinitionLocalization existingCPDefinitionLocalization =
			_persistence.findByPrimaryKey(
				newCPDefinitionLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionLocalization.getMvccVersion(),
			newCPDefinitionLocalization.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getCtCollectionId(),
			newCPDefinitionLocalization.getCtCollectionId());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getCpDefinitionLocalizationId(),
			newCPDefinitionLocalization.getCpDefinitionLocalizationId());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getCompanyId(),
			newCPDefinitionLocalization.getCompanyId());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getCPDefinitionId(),
			newCPDefinitionLocalization.getCPDefinitionId());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getLanguageId(),
			newCPDefinitionLocalization.getLanguageId());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getCProductId(),
			newCPDefinitionLocalization.getCProductId());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getName(),
			newCPDefinitionLocalization.getName());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getShortDescription(),
			newCPDefinitionLocalization.getShortDescription());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getDescription(),
			newCPDefinitionLocalization.getDescription());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getMetaTitle(),
			newCPDefinitionLocalization.getMetaTitle());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getMetaDescription(),
			newCPDefinitionLocalization.getMetaDescription());
		Assert.assertEquals(
			existingCPDefinitionLocalization.getMetaKeywords(),
			newCPDefinitionLocalization.getMetaKeywords());
	}

	@Test
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testCountByCPDefinitionId_LanguageId() throws Exception {
		_persistence.countByCPDefinitionId_LanguageId(
			RandomTestUtil.nextLong(), "");

		_persistence.countByCPDefinitionId_LanguageId(0L, "null");

		_persistence.countByCPDefinitionId_LanguageId(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		CPDefinitionLocalization existingCPDefinitionLocalization =
			_persistence.findByPrimaryKey(
				newCPDefinitionLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionLocalization, newCPDefinitionLocalization);
	}

	@Test(expected = NoSuchCPDefinitionLocalizationException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDefinitionLocalization>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPDefinitionLocalization", "mvccVersion", true, "ctCollectionId",
			true, "cpDefinitionLocalizationId", true, "companyId", true,
			"CPDefinitionId", true, "languageId", true, "CProductId", true,
			"name", true, "shortDescription", true, "metaTitle", true,
			"metaDescription", true, "metaKeywords", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		CPDefinitionLocalization existingCPDefinitionLocalization =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionLocalization.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionLocalization, newCPDefinitionLocalization);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionLocalization missingCPDefinitionLocalization =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinitionLocalization);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinitionLocalization newCPDefinitionLocalization1 =
			addCPDefinitionLocalization();
		CPDefinitionLocalization newCPDefinitionLocalization2 =
			addCPDefinitionLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionLocalization1.getPrimaryKey());
		primaryKeys.add(newCPDefinitionLocalization2.getPrimaryKey());

		Map<Serializable, CPDefinitionLocalization> cpDefinitionLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpDefinitionLocalizations.size());
		Assert.assertEquals(
			newCPDefinitionLocalization1,
			cpDefinitionLocalizations.get(
				newCPDefinitionLocalization1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinitionLocalization2,
			cpDefinitionLocalizations.get(
				newCPDefinitionLocalization2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinitionLocalization> cpDefinitionLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionLocalization.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinitionLocalization> cpDefinitionLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionLocalizations.size());
		Assert.assertEquals(
			newCPDefinitionLocalization,
			cpDefinitionLocalizations.get(
				newCPDefinitionLocalization.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinitionLocalization> cpDefinitionLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionLocalizations.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionLocalization.getPrimaryKey());

		Map<Serializable, CPDefinitionLocalization> cpDefinitionLocalizations =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionLocalizations.size());
		Assert.assertEquals(
			newCPDefinitionLocalization,
			cpDefinitionLocalizations.get(
				newCPDefinitionLocalization.getPrimaryKey()));
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPDefinitionLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cpDefinitionLocalizationId",
				newCPDefinitionLocalization.getCpDefinitionLocalizationId()));

		List<CPDefinitionLocalization> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		CPDefinitionLocalization existingCPDefinitionLocalization = result.get(
			0);

		Assert.assertEquals(
			existingCPDefinitionLocalization, newCPDefinitionLocalization);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPDefinitionLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cpDefinitionLocalizationId", RandomTestUtil.nextLong()));

		List<CPDefinitionLocalization> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPDefinitionLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cpDefinitionLocalizationId"));

		Object newCpDefinitionLocalizationId =
			newCPDefinitionLocalization.getCpDefinitionLocalizationId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cpDefinitionLocalizationId",
				new Object[] {newCpDefinitionLocalizationId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCpDefinitionLocalizationId = result.get(0);

		Assert.assertEquals(
			existingCpDefinitionLocalizationId, newCpDefinitionLocalizationId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPDefinitionLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("cpDefinitionLocalizationId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"cpDefinitionLocalizationId",
				new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDefinitionLocalization.getPrimaryKey()));
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

		CPDefinitionLocalization newCPDefinitionLocalization =
			addCPDefinitionLocalization();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CPDefinitionLocalization.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"cpDefinitionLocalizationId",
				newCPDefinitionLocalization.getCpDefinitionLocalizationId()));

		List<CPDefinitionLocalization> result =
			_persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(
		CPDefinitionLocalization cpDefinitionLocalization) {

		Assert.assertEquals(
			Long.valueOf(cpDefinitionLocalization.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			cpDefinitionLocalization.getLanguageId(),
			ReflectionTestUtil.invoke(
				cpDefinitionLocalization, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "languageId"));
	}

	protected CPDefinitionLocalization addCPDefinitionLocalization()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDefinitionLocalization cpDefinitionLocalization = _persistence.create(
			pk);

		cpDefinitionLocalization.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinitionLocalization.setCtCollectionId(RandomTestUtil.nextLong());

		cpDefinitionLocalization.setCompanyId(RandomTestUtil.nextLong());

		cpDefinitionLocalization.setCPDefinitionId(RandomTestUtil.nextLong());

		cpDefinitionLocalization.setLanguageId(RandomTestUtil.randomString());

		cpDefinitionLocalization.setCProductId(RandomTestUtil.nextLong());

		cpDefinitionLocalization.setName(RandomTestUtil.randomString());

		cpDefinitionLocalization.setShortDescription(
			RandomTestUtil.randomString());

		cpDefinitionLocalization.setDescription(RandomTestUtil.randomString());

		cpDefinitionLocalization.setMetaTitle(RandomTestUtil.randomString());

		cpDefinitionLocalization.setMetaDescription(
			RandomTestUtil.randomString());

		cpDefinitionLocalization.setMetaKeywords(RandomTestUtil.randomString());

		_cpDefinitionLocalizations.add(
			_persistence.update(cpDefinitionLocalization));

		return cpDefinitionLocalization;
	}

	private List<CPDefinitionLocalization> _cpDefinitionLocalizations =
		new ArrayList<CPDefinitionLocalization>();
	private CPDefinitionLocalizationPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}