/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.portal.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.exception.NoSuchCompanyInfoException;
import com.liferay.portal.kernel.model.CompanyInfo;
import com.liferay.portal.kernel.service.persistence.CompanyInfoPersistence;
import com.liferay.portal.kernel.service.persistence.CompanyInfoUtil;
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
		long pk = RandomTestUtil.nextLong();

		CompanyInfo newCompanyInfo = _persistence.create(pk);

		newCompanyInfo.setMvccVersion(RandomTestUtil.nextLong());

		newCompanyInfo.setCompanyId(RandomTestUtil.nextLong());

		newCompanyInfo.setKey(RandomTestUtil.randomString());

		_companyInfos.add(_persistence.update(newCompanyInfo));

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
			existingCompanyInfo.getKey(), newCompanyInfo.getKey());
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
			"companyId", true);
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
	public void testResetOriginalValues() throws Exception {
		CompanyInfo newCompanyInfo = addCompanyInfo();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCompanyInfo.getPrimaryKey()));
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

		companyInfo.setMvccVersion(RandomTestUtil.nextLong());

		companyInfo.setCompanyId(RandomTestUtil.nextLong());

		companyInfo.setKey(RandomTestUtil.randomString());

		_companyInfos.add(_persistence.update(companyInfo));

		return companyInfo;
	}

	private List<CompanyInfo> _companyInfos = new ArrayList<CompanyInfo>();
	private CompanyInfoPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}