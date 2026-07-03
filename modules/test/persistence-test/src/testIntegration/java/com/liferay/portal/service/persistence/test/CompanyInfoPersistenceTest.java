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
import com.liferay.portal.kernel.exception.NoSuchCompanyInfoException;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.service.CompanyInfoLocalServiceUtil;
import com.liferay.portal.kernel.service.persistence.CompanyInfoPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyInfoUtil;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.IntegerWrapper;
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
public class CompanyInfoPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(Propagation.REQUIRED));

	@Before
	public void setUp() {
		_persistence = CompanyInfoUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CompanyInfo> iterator = _companyInfos.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CompanyInfo companyInfo = _persistence.create(pk);

		Assert.assertNotNull(companyInfo);

		Assert.assertEquals(companyInfo.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		_persistence.remove(newCompanyInfo);

		CompanyInfo existingCompanyInfo = _persistence.fetchByPrimaryKey(
			newCompanyInfo.getPrimaryKey());

		Assert.assertNull(existingCompanyInfo);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCompanyInfo();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		newCompanyInfo.setCompanyId(RandomTestUtil.nextLong());

		newCompanyInfo.setHomeURL(RandomTestUtil.randomString());

		newCompanyInfo.setIndexNameCurrent(RandomTestUtil.randomString());

		newCompanyInfo.setIndexNameNext(RandomTestUtil.randomString());

		newCompanyInfo.setIndustry(RandomTestUtil.randomString());

		newCompanyInfo.setKey(RandomTestUtil.randomString());

		newCompanyInfo.setLegalId(RandomTestUtil.randomString());

		newCompanyInfo.setLegalName(RandomTestUtil.randomString());

		newCompanyInfo.setLegalType(RandomTestUtil.randomString());

		newCompanyInfo.setLogoId(RandomTestUtil.nextLong());

		newCompanyInfo.setName(RandomTestUtil.randomString());

		newCompanyInfo.setSicCode(RandomTestUtil.randomString());

		newCompanyInfo.setSize(RandomTestUtil.randomString());

		newCompanyInfo.setTickerSymbol(RandomTestUtil.randomString());

		newCompanyInfo.setType(RandomTestUtil.randomString());

		newCompanyInfo = _persistence.update(newCompanyInfo);

		_companyInfos.add(newCompanyInfo);

		CompanyInfo existingCompanyInfo = _persistence.findByPrimaryKey(
			newCompanyInfo.getPrimaryKey());

		Assert.assertEquals(
			existingCompanyInfo.getMvccVersion(),
			newCompanyInfo.getMvccVersion());
		Assert.assertEquals(
			existingCompanyInfo.getCompanyInfoId(),
			newCompanyInfo.getCompanyInfoId());
		Assert.assertEquals(
			existingCompanyInfo.getCompanyId(), newCompanyInfo.getCompanyId());
		Assert.assertEquals(
			existingCompanyInfo.getHomeURL(), newCompanyInfo.getHomeURL());
		Assert.assertEquals(
			existingCompanyInfo.getIndexNameCurrent(),
			newCompanyInfo.getIndexNameCurrent());
		Assert.assertEquals(
			existingCompanyInfo.getIndexNameNext(),
			newCompanyInfo.getIndexNameNext());
		Assert.assertEquals(
			existingCompanyInfo.getIndustry(), newCompanyInfo.getIndustry());
		Assert.assertEquals(
			existingCompanyInfo.getKey(), newCompanyInfo.getKey());
		Assert.assertEquals(
			existingCompanyInfo.getLegalId(), newCompanyInfo.getLegalId());
		Assert.assertEquals(
			existingCompanyInfo.getLegalName(), newCompanyInfo.getLegalName());
		Assert.assertEquals(
			existingCompanyInfo.getLegalType(), newCompanyInfo.getLegalType());
		Assert.assertEquals(
			existingCompanyInfo.getLogoId(), newCompanyInfo.getLogoId());
		Assert.assertEquals(
			existingCompanyInfo.getName(), newCompanyInfo.getName());
		Assert.assertEquals(
			existingCompanyInfo.getSicCode(), newCompanyInfo.getSicCode());
		Assert.assertEquals(
			existingCompanyInfo.getSize(), newCompanyInfo.getSize());
		Assert.assertEquals(
			existingCompanyInfo.getTickerSymbol(),
			newCompanyInfo.getTickerSymbol());
		Assert.assertEquals(
			existingCompanyInfo.getType(), newCompanyInfo.getType());
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		CompanyInfo existingCompanyInfo = _persistence.findByPrimaryKey(
			newCompanyInfo.getPrimaryKey());

		Assert.assertEquals(existingCompanyInfo, newCompanyInfo);
	}

	@Test(expected = NoSuchCompanyInfoException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CompanyInfo> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CompanyInfo", "mvccVersion", true, "companyInfoId", true,
			"companyId", true, "homeURL", true, "indexNameCurrent", true,
			"indexNameNext", true, "industry", true, "legalId", true,
			"legalName", true, "legalType", true, "logoId", true, "name", true,
			"sicCode", true, "size", true, "tickerSymbol", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		CompanyInfo existingCompanyInfo = _persistence.fetchByPrimaryKey(
			newCompanyInfo.getPrimaryKey());

		Assert.assertEquals(existingCompanyInfo, newCompanyInfo);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CompanyInfo missingCompanyInfo = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCompanyInfo);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CompanyInfo newCompanyInfo1 = addCompanyInfo();
		CompanyInfo newCompanyInfo2 = addCompanyInfo();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompanyInfo1.getPrimaryKey());
		primaryKeys.add(newCompanyInfo2.getPrimaryKey());

		Map<Serializable, CompanyInfo> companyInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, companyInfos.size());
		Assert.assertEquals(
			newCompanyInfo1, companyInfos.get(newCompanyInfo1.getPrimaryKey()));
		Assert.assertEquals(
			newCompanyInfo2, companyInfos.get(newCompanyInfo2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CompanyInfo> companyInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(companyInfos.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CompanyInfo newCompanyInfo = addCompanyInfo();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompanyInfo.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CompanyInfo> companyInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, companyInfos.size());
		Assert.assertEquals(
			newCompanyInfo, companyInfos.get(newCompanyInfo.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CompanyInfo> companyInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(companyInfos.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCompanyInfo.getPrimaryKey());

		Map<Serializable, CompanyInfo> companyInfos =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, companyInfos.size());
		Assert.assertEquals(
			newCompanyInfo, companyInfos.get(newCompanyInfo.getPrimaryKey()));
	}

	@Test
	public void testActionableDynamicQuery() throws Exception {
		final IntegerWrapper count = new IntegerWrapper();

		ActionableDynamicQuery actionableDynamicQuery =
			CompanyInfoLocalServiceUtil.getActionableDynamicQuery();

		actionableDynamicQuery.setPerformActionMethod(
			new ActionableDynamicQuery.PerformActionMethod<CompanyInfo>() {

				@Override
				public void performAction(CompanyInfo companyInfo) {
					Assert.assertNotNull(companyInfo);

					count.increment();
				}

			});

		actionableDynamicQuery.performActions();

		Assert.assertEquals(count.getValue(), _persistence.countAll());
	}

	@Test
	public void testDynamicQueryByPrimaryKeyExisting() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompanyInfo.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyInfoId", newCompanyInfo.getCompanyInfoId()));

		List<CompanyInfo> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(1, result.size());

		CompanyInfo existingCompanyInfo = result.get(0);

		Assert.assertEquals(existingCompanyInfo, newCompanyInfo);
	}

	@Test
	public void testDynamicQueryByPrimaryKeyMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompanyInfo.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyInfoId", RandomTestUtil.nextLong()));

		List<CompanyInfo> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testDynamicQueryByProjectionExisting() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompanyInfo.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("companyInfoId"));

		Object newCompanyInfoId = newCompanyInfo.getCompanyInfoId();

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"companyInfoId", new Object[] {newCompanyInfoId}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(1, result.size());

		Object existingCompanyInfoId = result.get(0);

		Assert.assertEquals(existingCompanyInfoId, newCompanyInfoId);
	}

	@Test
	public void testDynamicQueryByProjectionMissing() throws Exception {
		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompanyInfo.class, _dynamicQueryClassLoader);

		dynamicQuery.setProjection(
			ProjectionFactoryUtil.property("companyInfoId"));

		dynamicQuery.add(
			RestrictionsFactoryUtil.in(
				"companyInfoId", new Object[] {RandomTestUtil.nextLong()}));

		List<Object> result = _persistence.findWithDynamicQuery(dynamicQuery);

		Assert.assertEquals(0, result.size());
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCompanyInfo.getPrimaryKey()));
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

		CompanyInfo newCompanyInfo = addCompanyInfo();

		if (clearSession) {
			Session session = _persistence.openSession();

			session.flush();

			session.clear();
		}

		DynamicQuery dynamicQuery = DynamicQueryFactoryUtil.forClass(
			CompanyInfo.class, _dynamicQueryClassLoader);

		dynamicQuery.add(
			RestrictionsFactoryUtil.eq(
				"companyInfoId", newCompanyInfo.getCompanyInfoId()));

		List<CompanyInfo> result = _persistence.findWithDynamicQuery(
			dynamicQuery);

		_assertOriginalValues(result.get(0));
	}

	private void _assertOriginalValues(CompanyInfo companyInfo) {
		Assert.assertEquals(
			Long.valueOf(companyInfo.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				companyInfo, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CompanyInfo addCompanyInfo() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CompanyInfo companyInfo = _persistence.create(pk);

		companyInfo.setCompanyId(RandomTestUtil.nextLong());

		companyInfo.setHomeURL(RandomTestUtil.randomString());

		companyInfo.setIndexNameCurrent(RandomTestUtil.randomString());

		companyInfo.setIndexNameNext(RandomTestUtil.randomString());

		companyInfo.setIndustry(RandomTestUtil.randomString());

		companyInfo.setKey(RandomTestUtil.randomString());

		companyInfo.setLegalId(RandomTestUtil.randomString());

		companyInfo.setLegalName(RandomTestUtil.randomString());

		companyInfo.setLegalType(RandomTestUtil.randomString());

		companyInfo.setLogoId(RandomTestUtil.nextLong());

		companyInfo.setName(RandomTestUtil.randomString());

		companyInfo.setSicCode(RandomTestUtil.randomString());

		companyInfo.setSize(RandomTestUtil.randomString());

		companyInfo.setTickerSymbol(RandomTestUtil.randomString());

		companyInfo.setType(RandomTestUtil.randomString());

		_companyInfos.add(_persistence.update(companyInfo));

		return companyInfo;
	}

	private List<CompanyInfo> _companyInfos = new ArrayList<CompanyInfo>();
	private CompanyInfoPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}
// LIFERAY-SERVICE-BUILDER-HASH:313530594