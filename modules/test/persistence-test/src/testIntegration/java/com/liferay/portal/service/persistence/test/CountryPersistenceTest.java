/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.ActionableDynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQuery;
import com.liferay.portal.kernel.dao.orm.DynamicQueryFactoryUtil;
import com.liferay.portal.kernel.dao.orm.ProjectionFactoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.RestrictionsFactoryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.exception.DuplicateCountryExternalReferenceCodeException;
import com.liferay.portal.kernel.exception.NoSuchCountryException;
import com.liferay.portal.kernel.model.Country;
import com.liferay.portal.kernel.service.CountryLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.CountryPersistence;
import com.liferay.portal.kernel.service.persistence.CountryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class CountryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = CountryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Country> iterator = _countries.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Country country = _persistence.create(pk);

		Assert.assertNotNull(country);

		Assert.assertEquals(country.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Country newCountry = addCountry();

		_persistence.remove(newCountry);

		Country existingCountry = _persistence.fetchByPrimaryKey(
			newCountry.getPrimaryKey());

		Assert.assertNull(existingCountry);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCountry();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		Country newCountry = addCountry();

		newCountry.setCtCollectionId(RandomTestUtil.nextLong());

		newCountry.setUuid(RandomTestUtil.randomString());

		newCountry.setExternalReferenceCode(RandomTestUtil.randomString());

		newCountry.setDefaultLanguageId(RandomTestUtil.randomString());

		newCountry.setCompanyId(RandomTestUtil.nextLong());

		newCountry.setUserId(RandomTestUtil.nextLong());

		newCountry.setUserName(RandomTestUtil.randomString());

		newCountry.setCreateDate(RandomTestUtil.nextDate());

		newCountry.setModifiedDate(RandomTestUtil.nextDate());

		newCountry.setA2(RandomTestUtil.randomString());

		newCountry.setA3(RandomTestUtil.randomString());

		newCountry.setActive(RandomTestUtil.randomBoolean());

		newCountry.setBillingAllowed(RandomTestUtil.randomBoolean());

		newCountry.setGroupFilterEnabled(RandomTestUtil.randomBoolean());

		newCountry.setIdd(RandomTestUtil.randomString());

		newCountry.setName(RandomTestUtil.randomString());

		newCountry.setNumber(RandomTestUtil.randomString());

		newCountry.setPosition(RandomTestUtil.nextDouble());

		newCountry.setShippingAllowed(RandomTestUtil.randomBoolean());

		newCountry.setSubjectToVAT(RandomTestUtil.randomBoolean());

		newCountry.setZipRequired(RandomTestUtil.randomBoolean());

		newCountry.setLastPublishDate(RandomTestUtil.nextDate());

		newCountry.setStatus(RandomTestUtil.nextInt());

		newCountry = _persistence.update(newCountry);

		_countries.add(newCountry);

		Country existingCountry = _persistence.findByPrimaryKey(
			newCountry.getPrimaryKey());

		Assert.assertEquals(
			existingCountry.getMvccVersion(), newCountry.getMvccVersion());
		Assert.assertEquals(
			existingCountry.getCtCollectionId(),
			newCountry.getCtCollectionId());
		Assert.assertEquals(existingCountry.getUuid(), newCountry.getUuid());
		Assert.assertEquals(
			existingCountry.getExternalReferenceCode(),
			newCountry.getExternalReferenceCode());
		Assert.assertEquals(
			existingCountry.getDefaultLanguageId(),
			newCountry.getDefaultLanguageId());
		Assert.assertEquals(
			existingCountry.getCountryId(), newCountry.getCountryId());
		Assert.assertEquals(
			existingCountry.getCompanyId(), newCountry.getCompanyId());
		Assert.assertEquals(
			existingCountry.getUserId(), newCountry.getUserId());
		Assert.assertEquals(
			existingCountry.getUserName(), newCountry.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCountry.getCreateDate()),
			Time.getShortTimestamp(newCountry.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCountry.getModifiedDate()),
			Time.getShortTimestamp(newCountry.getModifiedDate()));
		Assert.assertEquals(existingCountry.getA2(), newCountry.getA2());
		Assert.assertEquals(existingCountry.getA3(), newCountry.getA3());
		Assert.assertEquals(existingCountry.isActive(), newCountry.isActive());
		Assert.assertEquals(
			existingCountry.isBillingAllowed(), newCountry.isBillingAllowed());
		Assert.assertEquals(
			existingCountry.isGroupFilterEnabled(),
			newCountry.isGroupFilterEnabled());
		Assert.assertEquals(existingCountry.getIdd(), newCountry.getIdd());
		Assert.assertEquals(existingCountry.getName(), newCountry.getName());
		Assert.assertEquals(
			existingCountry.getNumber(), newCountry.getNumber());
		AssertUtils.assertEquals(
			existingCountry.getPosition(), newCountry.getPosition());
		Assert.assertEquals(
			existingCountry.isShippingAllowed(),
			newCountry.isShippingAllowed());
		Assert.assertEquals(
			existingCountry.isSubjectToVAT(), newCountry.isSubjectToVAT());
		Assert.assertEquals(
			existingCountry.isZipRequired(), newCountry.isZipRequired());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCountry.getLastPublishDate()),
			Time.getShortTimestamp(newCountry.getLastPublishDate()));
		Assert.assertEquals(
			existingCountry.getStatus(), newCountry.getStatus());
	}

	@Test(expected = DuplicateCountryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		Country country = addCountry();

		Country newCountry = addCountry();

		newCountry.setCompanyId(country.getCompanyId());

		newCountry = _persistence.update(newCountry);

		Session session = _persistence.getCurrentSession();

		session.evict(newCountry);

		newCountry.setExternalReferenceCode(country.getExternalReferenceCode());

		_persistence.update(newCountry);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByActive() throws Exception {
		_persistence.countByActive(RandomTestUtil.randomBoolean());

		_persistence.countByActive(RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A2() throws Exception {
		_persistence.countByC_A2(RandomTestUtil.nextLong(), "");

		_persistence.countByC_A2(0L, "null");

		_persistence.countByC_A2(0L, (String)null);
	}

	@Test
	public void testCountByC_A3() throws Exception {
		_persistence.countByC_A3(RandomTestUtil.nextLong(), "");

		_persistence.countByC_A3(0L, "null");

		_persistence.countByC_A3(0L, (String)null);
	}

	@Test
	public void testCountByC_Active() throws Exception {
		_persistence.countByC_Active(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_Active(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_Name() throws Exception {
		_persistence.countByC_Name(RandomTestUtil.nextLong(), "");

		_persistence.countByC_Name(0L, "null");

		_persistence.countByC_Name(0L, (String)null);
	}

	@Test
	public void testCountByC_Number() throws Exception {
		_persistence.countByC_Number(RandomTestUtil.nextLong(), "");

		_persistence.countByC_Number(0L, "null");

		_persistence.countByC_Number(0L, (String)null);
	}

	@Test
	public void testCountByC_A_B() throws Exception {
		_persistence.countByC_A_B(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_A_B(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A_S() throws Exception {
		_persistence.countByC_A_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_A_S(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A_B_G() throws Exception {
		_persistence.countByC_A_B_G(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A_B_G(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A_G_S() throws Exception {
		_persistence.countByC_A_G_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A_G_S(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A_B_G_S() throws Exception {
		_persistence.countByC_A_B_G_S(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_A_B_G_S(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Country newCountry = addCountry();

		Country existingCountry = _persistence.findByPrimaryKey(
			newCountry.getPrimaryKey());

		Assert.assertEquals(existingCountry, newCountry);
	}

	@Test(expected = NoSuchCountryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Country> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Country", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "defaultLanguageId", true,
			"countryId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true, "a2", true, "a3",
			true, "active", true, "billingAllowed", true, "groupFilterEnabled",
			true, "idd", true, "name", true, "number", true, "position", true,
			"shippingAllowed", true, "subjectToVAT", true, "zipRequired", true,
			"lastPublishDate", true, "status", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Country newCountry = addCountry();

		Country existingCountry = _persistence.fetchByPrimaryKey(
			newCountry.getPrimaryKey());

		Assert.assertEquals(existingCountry, newCountry);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Country missingCountry = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCountry);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Country newCountry1 = addCountry();
		Country newCountry2 = addCountry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCountry1.getPrimaryKey());
		primaryKeys.add(newCountry2.getPrimaryKey());

		Map<Serializable, Country> countries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, countries.size());
		Assert.assertEquals(
			newCountry1, countries.get(newCountry1.getPrimaryKey()));
		Assert.assertEquals(
			newCountry2, countries.get(newCountry2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Country> countries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(countries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Country newCountry = addCountry();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCountry.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Country> countries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, countries.size());
		Assert.assertEquals(
			newCountry, countries.get(newCountry.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Country> countries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(countries.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Country newCountry = addCountry();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCountry.getPrimaryKey());

		Map<Serializable, Country> countries = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, countries.size());
		Assert.assertEquals(
			newCountry, countries.get(newCountry.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CountryLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Country>() {

				@Override
				public void performAction(Country country) {
					Assert.assertNotNull(country);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Country newCountry = addCountry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Country.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("countryId", newCountry.getCountryId()));

		List<Country> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Country existingCountry = result.get(0);

		Assert.assertEquals(existingCountry, newCountry);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Country.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("countryId", RandomTestUtil.nextLong()));

		List<Country> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Country newCountry = addCountry();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Country.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("countryId"));

		Object newCountryId = newCountry.getCountryId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"countryId", new Object[] {newCountryId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCountryId = result.get(0);

		Assert.assertEquals(existingCountryId, newCountryId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Country.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("countryId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"countryId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Country newCountry = addCountry();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCountry.getPrimaryKey()));
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

		Country newCountry = addCountry();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Country.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("countryId", newCountry.getCountryId()));

		List<Country> result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(Country country) {
		Assert.assertEquals(
			Long.valueOf(country.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			country.getA2(),
			ReflectionTestUtil.invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "a2"));

		Assert.assertEquals(
			Long.valueOf(country.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			country.getA3(),
			ReflectionTestUtil.invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "a3"));

		Assert.assertEquals(
			Long.valueOf(country.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			country.getName(),
			ReflectionTestUtil.invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "name"));

		Assert.assertEquals(
			Long.valueOf(country.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			country.getNumber(),
			ReflectionTestUtil.invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "number_"));

		Assert.assertEquals(
			country.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(country.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				country, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected Country addCountry() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Country country = _persistence.create(pk);

		country.setCtCollectionId(RandomTestUtil.nextLong());

		country.setUuid(RandomTestUtil.randomString());

		country.setExternalReferenceCode(RandomTestUtil.randomString());

		country.setDefaultLanguageId(RandomTestUtil.randomString());

		country.setCompanyId(RandomTestUtil.nextLong());

		country.setUserId(RandomTestUtil.nextLong());

		country.setUserName(RandomTestUtil.randomString());

		country.setCreateDate(RandomTestUtil.nextDate());

		country.setModifiedDate(RandomTestUtil.nextDate());

		country.setA2(RandomTestUtil.randomString());

		country.setA3(RandomTestUtil.randomString());

		country.setActive(RandomTestUtil.randomBoolean());

		country.setBillingAllowed(RandomTestUtil.randomBoolean());

		country.setGroupFilterEnabled(RandomTestUtil.randomBoolean());

		country.setIdd(RandomTestUtil.randomString());

		country.setName(RandomTestUtil.randomString());

		country.setNumber(RandomTestUtil.randomString());

		country.setPosition(RandomTestUtil.nextDouble());

		country.setShippingAllowed(RandomTestUtil.randomBoolean());

		country.setSubjectToVAT(RandomTestUtil.randomBoolean());

		country.setZipRequired(RandomTestUtil.randomBoolean());

		country.setLastPublishDate(RandomTestUtil.nextDate());

		country.setStatus(RandomTestUtil.nextInt());

		_countries.add(_persistence.update(country));

		return country;
	}

	private List<Country> _countries = new ArrayList<Country>();
	private CountryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:311387174