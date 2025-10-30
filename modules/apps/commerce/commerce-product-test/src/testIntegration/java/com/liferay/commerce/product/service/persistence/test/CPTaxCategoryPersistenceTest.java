/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPTaxCategoryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPTaxCategoryException;
import com.liferay.commerce.product.model.CPTaxCategory;
import com.liferay.commerce.product.service.persistence.CPTaxCategoryPersistence;
import com.liferay.commerce.product.service.persistence.CPTaxCategoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.transaction.Propagation;
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
public class CPTaxCategoryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPTaxCategoryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPTaxCategory> iterator = _cpTaxCategories.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPTaxCategory cpTaxCategory = _persistence.create(pk);

		Assert.assertNotNull(cpTaxCategory);

		Assert.assertEquals(cpTaxCategory.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		_persistence.remove(newCPTaxCategory);

		CPTaxCategory existingCPTaxCategory = _persistence.fetchByPrimaryKey(
			newCPTaxCategory.getPrimaryKey());

		Assert.assertNull(existingCPTaxCategory);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPTaxCategory();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPTaxCategory newCPTaxCategory = _persistence.create(pk);

		newCPTaxCategory.setMvccVersion(RandomTestUtil.nextLong());

		newCPTaxCategory.setCtCollectionId(RandomTestUtil.nextLong());

		newCPTaxCategory.setUuid(RandomTestUtil.randomString());

		newCPTaxCategory.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPTaxCategory.setCompanyId(RandomTestUtil.nextLong());

		newCPTaxCategory.setUserId(RandomTestUtil.nextLong());

		newCPTaxCategory.setUserName(RandomTestUtil.randomString());

		newCPTaxCategory.setCreateDate(RandomTestUtil.nextDate());

		newCPTaxCategory.setModifiedDate(RandomTestUtil.nextDate());

		newCPTaxCategory.setName(RandomTestUtil.randomString());

		newCPTaxCategory.setDescription(RandomTestUtil.randomString());

		_cpTaxCategories.add(_persistence.update(newCPTaxCategory));

		CPTaxCategory existingCPTaxCategory = _persistence.findByPrimaryKey(
			newCPTaxCategory.getPrimaryKey());

		Assert.assertEquals(
			existingCPTaxCategory.getMvccVersion(),
			newCPTaxCategory.getMvccVersion());
		Assert.assertEquals(
			existingCPTaxCategory.getCtCollectionId(),
			newCPTaxCategory.getCtCollectionId());
		Assert.assertEquals(
			existingCPTaxCategory.getUuid(), newCPTaxCategory.getUuid());
		Assert.assertEquals(
			existingCPTaxCategory.getExternalReferenceCode(),
			newCPTaxCategory.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPTaxCategory.getCPTaxCategoryId(),
			newCPTaxCategory.getCPTaxCategoryId());
		Assert.assertEquals(
			existingCPTaxCategory.getCompanyId(),
			newCPTaxCategory.getCompanyId());
		Assert.assertEquals(
			existingCPTaxCategory.getUserId(), newCPTaxCategory.getUserId());
		Assert.assertEquals(
			existingCPTaxCategory.getUserName(),
			newCPTaxCategory.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPTaxCategory.getCreateDate()),
			Time.getShortTimestamp(newCPTaxCategory.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPTaxCategory.getModifiedDate()),
			Time.getShortTimestamp(newCPTaxCategory.getModifiedDate()));
		Assert.assertEquals(
			existingCPTaxCategory.getName(), newCPTaxCategory.getName());
		Assert.assertEquals(
			existingCPTaxCategory.getDescription(),
			newCPTaxCategory.getDescription());
	}

	@Test(expected = DuplicateCPTaxCategoryExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPTaxCategory cpTaxCategory = addCPTaxCategory();

		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		newCPTaxCategory.setCompanyId(cpTaxCategory.getCompanyId());

		newCPTaxCategory = _persistence.update(newCPTaxCategory);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPTaxCategory);

		newCPTaxCategory.setExternalReferenceCode(
			cpTaxCategory.getExternalReferenceCode());

		_persistence.update(newCPTaxCategory);
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
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		CPTaxCategory existingCPTaxCategory = _persistence.findByPrimaryKey(
			newCPTaxCategory.getPrimaryKey());

		Assert.assertEquals(existingCPTaxCategory, newCPTaxCategory);
	}

	@Test(expected = NoSuchCPTaxCategoryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPTaxCategory> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPTaxCategory", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "CPTaxCategoryId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "name", true,
			"description", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		CPTaxCategory existingCPTaxCategory = _persistence.fetchByPrimaryKey(
			newCPTaxCategory.getPrimaryKey());

		Assert.assertEquals(existingCPTaxCategory, newCPTaxCategory);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPTaxCategory missingCPTaxCategory = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPTaxCategory);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPTaxCategory newCPTaxCategory1 = addCPTaxCategory();
		CPTaxCategory newCPTaxCategory2 = addCPTaxCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPTaxCategory1.getPrimaryKey());
		primaryKeys.add(newCPTaxCategory2.getPrimaryKey());

		Map<Serializable, CPTaxCategory> cpTaxCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpTaxCategories.size());
		Assert.assertEquals(
			newCPTaxCategory1,
			cpTaxCategories.get(newCPTaxCategory1.getPrimaryKey()));
		Assert.assertEquals(
			newCPTaxCategory2,
			cpTaxCategories.get(newCPTaxCategory2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPTaxCategory> cpTaxCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpTaxCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPTaxCategory.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPTaxCategory> cpTaxCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpTaxCategories.size());
		Assert.assertEquals(
			newCPTaxCategory,
			cpTaxCategories.get(newCPTaxCategory.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPTaxCategory> cpTaxCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpTaxCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPTaxCategory.getPrimaryKey());

		Map<Serializable, CPTaxCategory> cpTaxCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpTaxCategories.size());
		Assert.assertEquals(
			newCPTaxCategory,
			cpTaxCategories.get(newCPTaxCategory.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPTaxCategory newCPTaxCategory = addCPTaxCategory();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPTaxCategory.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPTaxCategory cpTaxCategory) {
		Assert.assertEquals(
			cpTaxCategory.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpTaxCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpTaxCategory.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpTaxCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPTaxCategory addCPTaxCategory() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPTaxCategory cpTaxCategory = _persistence.create(pk);

		cpTaxCategory.setMvccVersion(RandomTestUtil.nextLong());

		cpTaxCategory.setCtCollectionId(RandomTestUtil.nextLong());

		cpTaxCategory.setUuid(RandomTestUtil.randomString());

		cpTaxCategory.setExternalReferenceCode(RandomTestUtil.randomString());

		cpTaxCategory.setCompanyId(RandomTestUtil.nextLong());

		cpTaxCategory.setUserId(RandomTestUtil.nextLong());

		cpTaxCategory.setUserName(RandomTestUtil.randomString());

		cpTaxCategory.setCreateDate(RandomTestUtil.nextDate());

		cpTaxCategory.setModifiedDate(RandomTestUtil.nextDate());

		cpTaxCategory.setName(RandomTestUtil.randomString());

		cpTaxCategory.setDescription(RandomTestUtil.randomString());

		_cpTaxCategories.add(_persistence.update(cpTaxCategory));

		return cpTaxCategory;
	}

	private List<CPTaxCategory> _cpTaxCategories =
		new ArrayList<CPTaxCategory>();
	private CPTaxCategoryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}