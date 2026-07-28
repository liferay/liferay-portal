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
import com.liferay.portal.kernel.exception.NoSuchCompanyException;
import com.liferay.portal.kernel.model.Company;
import com.liferay.portal.kernel.service.CompanyLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.CompanyPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyUtil;
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
public class CompanyPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = CompanyUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<Company> iterator = _companies.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Company company = _persistence.create(pk);

		Assert.assertNotNull(company);

		Assert.assertEquals(company.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		Company newCompany = addCompany();

		_persistence.remove(newCompany);

		Company existingCompany = _persistence.fetchByPrimaryKey(
			newCompany.getPrimaryKey());

		Assert.assertNull(existingCompany);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCompany();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		Company newCompany = addCompany();

		newCompany.setUserId(RandomTestUtil.nextLong());

		newCompany.setUserName(RandomTestUtil.randomString());

		newCompany.setCreateDate(RandomTestUtil.nextDate());

		newCompany.setModifiedDate(RandomTestUtil.nextDate());

		newCompany.setWebId(RandomTestUtil.randomString());

		newCompany.setMx(RandomTestUtil.randomString());

		newCompany.setHomeURL(RandomTestUtil.randomString());

		newCompany.setLogoId(RandomTestUtil.nextLong());

		newCompany.setMaxUsers(RandomTestUtil.nextInt());

		newCompany.setActive(RandomTestUtil.randomBoolean());

		newCompany.setName(RandomTestUtil.randomString());

		newCompany.setLegalName(RandomTestUtil.randomString());

		newCompany.setLegalId(RandomTestUtil.randomString());

		newCompany.setLegalType(RandomTestUtil.randomString());

		newCompany.setSicCode(RandomTestUtil.randomString());

		newCompany.setTickerSymbol(RandomTestUtil.randomString());

		newCompany.setIndustry(RandomTestUtil.randomString());

		newCompany.setType(RandomTestUtil.randomString());

		newCompany.setSize(RandomTestUtil.randomString());

		newCompany.setIndexNameCurrent(RandomTestUtil.randomString());

		newCompany.setIndexNameNext(RandomTestUtil.randomString());

		newCompany = _persistence.update(newCompany);

		_companies.add(newCompany);

		Company existingCompany = _persistence.findByPrimaryKey(
			newCompany.getPrimaryKey());

		Assert.assertEquals(
			existingCompany.getMvccVersion(), newCompany.getMvccVersion());
		Assert.assertEquals(
			existingCompany.getCompanyId(), newCompany.getCompanyId());
		Assert.assertEquals(
			existingCompany.getUserId(), newCompany.getUserId());
		Assert.assertEquals(
			existingCompany.getUserName(), newCompany.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCompany.getCreateDate()),
			Time.getShortTimestamp(newCompany.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCompany.getModifiedDate()),
			Time.getShortTimestamp(newCompany.getModifiedDate()));
		Assert.assertEquals(existingCompany.getWebId(), newCompany.getWebId());
		Assert.assertEquals(existingCompany.getMx(), newCompany.getMx());
		Assert.assertEquals(
			existingCompany.getHomeURL(), newCompany.getHomeURL());
		Assert.assertEquals(
			existingCompany.getLogoId(), newCompany.getLogoId());
		Assert.assertEquals(
			existingCompany.getMaxUsers(), newCompany.getMaxUsers());
		Assert.assertEquals(existingCompany.isActive(), newCompany.isActive());
		Assert.assertEquals(existingCompany.getName(), newCompany.getName());
		Assert.assertEquals(
			existingCompany.getLegalName(), newCompany.getLegalName());
		Assert.assertEquals(
			existingCompany.getLegalId(), newCompany.getLegalId());
		Assert.assertEquals(
			existingCompany.getLegalType(), newCompany.getLegalType());
		Assert.assertEquals(
			existingCompany.getSicCode(), newCompany.getSicCode());
		Assert.assertEquals(
			existingCompany.getTickerSymbol(), newCompany.getTickerSymbol());
		Assert.assertEquals(
			existingCompany.getIndustry(), newCompany.getIndustry());
		Assert.assertEquals(existingCompany.getType(), newCompany.getType());
		Assert.assertEquals(existingCompany.getSize(), newCompany.getSize());
		Assert.assertEquals(
			existingCompany.getIndexNameCurrent(),
			newCompany.getIndexNameCurrent());
		Assert.assertEquals(
			existingCompany.getIndexNameNext(), newCompany.getIndexNameNext());
	}

	@Test
	public void testCountByWebId() throws Exception {
		_persistence.countByWebId("");

		_persistence.countByWebId("null");

		_persistence.countByWebId((String)null);
	}

	@Test
	public void testCountByLogoId() throws Exception {
		_persistence.countByLogoId(RandomTestUtil.nextLong());

		_persistence.countByLogoId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		Company newCompany = addCompany();

		Company existingCompany = _persistence.findByPrimaryKey(
			newCompany.getPrimaryKey());

		Assert.assertEquals(existingCompany, newCompany);
	}

	@Test(expected = NoSuchCompanyException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<Company> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"Company", "mvccVersion", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "webId",
			true, "mx", true, "homeURL", true, "logoId", true, "maxUsers", true,
			"active", true, "name", true, "legalName", true, "legalId", true,
			"legalType", true, "sicCode", true, "tickerSymbol", true,
			"industry", true, "type", true, "size", true, "indexNameCurrent",
			true, "indexNameNext", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		Company newCompany = addCompany();

		Company existingCompany = _persistence.fetchByPrimaryKey(
			newCompany.getPrimaryKey());

		Assert.assertEquals(existingCompany, newCompany);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Company missingCompany = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCompany);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		Company newCompany1 = addCompany();
		Company newCompany2 = addCompany();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompany1.getPrimaryKey());
		primaryKeys.add(newCompany2.getPrimaryKey());

		Map<Serializable, Company> companies = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, companies.size());
		Assert.assertEquals(
			newCompany1, companies.get(newCompany1.getPrimaryKey()));
		Assert.assertEquals(
			newCompany2, companies.get(newCompany2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, Company> companies = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(companies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		Company newCompany = addCompany();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompany.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, Company> companies = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, companies.size());
		Assert.assertEquals(
			newCompany, companies.get(newCompany.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, Company> companies = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(companies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		Company newCompany = addCompany();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompany.getPrimaryKey());

		Map<Serializable, Company> companies = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, companies.size());
		Assert.assertEquals(
			newCompany, companies.get(newCompany.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CompanyLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<Company>() {

				@Override
				public void performAction(Company company) {
					Assert.assertNotNull(company);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		Company newCompany = addCompany();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Company.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("companyId", newCompany.getCompanyId()));

		List<Company> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Company existingCompany = result.get(0);

		Assert.assertEquals(existingCompany, newCompany);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Company.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("companyId", RandomTestUtil.nextLong()));

		List<Company> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		Company newCompany = addCompany();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Company.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("companyId"));

		Object newCompanyId = newCompany.getCompanyId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"companyId", new Object[] {newCompanyId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCompanyId = result.get(0);

		Assert.assertEquals(existingCompanyId, newCompanyId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Company.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(ProjectionFactoryUtil.property("companyId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"companyId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		Company newCompany = addCompany();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCompany.getPrimaryKey()));
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

		Company newCompany = addCompany();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			Company.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq("companyId", newCompany.getCompanyId()));

		List<Company> result = _persistence.findWithDynamicQuery(dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(Company company) {
		Assert.assertEquals(
			company.getWebId(),
			ReflectionTestUtil.invoke(
				company, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "webId"));
	}

	protected Company addCompany() throws Exception {
		long pk = RandomTestUtil.nextLong();

		Company company = _persistence.create(pk);

		company.setUserId(RandomTestUtil.nextLong());

		company.setUserName(RandomTestUtil.randomString());

		company.setCreateDate(RandomTestUtil.nextDate());

		company.setModifiedDate(RandomTestUtil.nextDate());

		company.setWebId(RandomTestUtil.randomString());

		company.setMx(RandomTestUtil.randomString());

		company.setHomeURL(RandomTestUtil.randomString());

		company.setLogoId(RandomTestUtil.nextLong());

		company.setMaxUsers(RandomTestUtil.nextInt());

		company.setActive(RandomTestUtil.randomBoolean());

		company.setName(RandomTestUtil.randomString());

		company.setLegalName(RandomTestUtil.randomString());

		company.setLegalId(RandomTestUtil.randomString());

		company.setLegalType(RandomTestUtil.randomString());

		company.setSicCode(RandomTestUtil.randomString());

		company.setTickerSymbol(RandomTestUtil.randomString());

		company.setIndustry(RandomTestUtil.randomString());

		company.setType(RandomTestUtil.randomString());

		company.setSize(RandomTestUtil.randomString());

		company.setIndexNameCurrent(RandomTestUtil.randomString());

		company.setIndexNameNext(RandomTestUtil.randomString());

		_companies.add(_persistence.update(company));

		return company;
	}

	private List<Company> _companies = new ArrayList<Company>();
	private CompanyPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:-1203230343