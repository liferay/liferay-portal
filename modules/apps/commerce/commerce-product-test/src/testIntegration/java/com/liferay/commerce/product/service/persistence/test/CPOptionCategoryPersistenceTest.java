/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPOptionCategoryExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPOptionCategoryException;
import com.liferay.commerce.product.model.CPOptionCategory;
import com.liferay.commerce.product.service.persistence.CPOptionCategoryPersistence;
import com.liferay.commerce.product.service.persistence.CPOptionCategoryUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.dao.orm.Session;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CPOptionCategoryPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPOptionCategoryUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPOptionCategory> iterator = _cpOptionCategories.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionCategory cpOptionCategory = _persistence.create(pk);

		Assert.assertNotNull(cpOptionCategory);

		Assert.assertEquals(cpOptionCategory.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		_persistence.remove(newCPOptionCategory);

		CPOptionCategory existingCPOptionCategory =
			_persistence.fetchByPrimaryKey(newCPOptionCategory.getPrimaryKey());

		Assert.assertNull(existingCPOptionCategory);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPOptionCategory();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionCategory newCPOptionCategory = _persistence.create(pk);

		newCPOptionCategory.setMvccVersion(RandomTestUtil.nextLong());

		newCPOptionCategory.setCtCollectionId(RandomTestUtil.nextLong());

		newCPOptionCategory.setUuid(RandomTestUtil.randomString());

		newCPOptionCategory.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPOptionCategory.setCompanyId(RandomTestUtil.nextLong());

		newCPOptionCategory.setUserId(RandomTestUtil.nextLong());

		newCPOptionCategory.setUserName(RandomTestUtil.randomString());

		newCPOptionCategory.setCreateDate(RandomTestUtil.nextDate());

		newCPOptionCategory.setModifiedDate(RandomTestUtil.nextDate());

		newCPOptionCategory.setTitle(RandomTestUtil.randomString());

		newCPOptionCategory.setDescription(RandomTestUtil.randomString());

		newCPOptionCategory.setPriority(RandomTestUtil.nextDouble());

		newCPOptionCategory.setKey(RandomTestUtil.randomString());

		newCPOptionCategory.setLastPublishDate(RandomTestUtil.nextDate());

		_cpOptionCategories.add(_persistence.update(newCPOptionCategory));

		CPOptionCategory existingCPOptionCategory =
			_persistence.findByPrimaryKey(newCPOptionCategory.getPrimaryKey());

		Assert.assertEquals(
			existingCPOptionCategory.getMvccVersion(),
			newCPOptionCategory.getMvccVersion());
		Assert.assertEquals(
			existingCPOptionCategory.getCtCollectionId(),
			newCPOptionCategory.getCtCollectionId());
		Assert.assertEquals(
			existingCPOptionCategory.getUuid(), newCPOptionCategory.getUuid());
		Assert.assertEquals(
			existingCPOptionCategory.getExternalReferenceCode(),
			newCPOptionCategory.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPOptionCategory.getCPOptionCategoryId(),
			newCPOptionCategory.getCPOptionCategoryId());
		Assert.assertEquals(
			existingCPOptionCategory.getCompanyId(),
			newCPOptionCategory.getCompanyId());
		Assert.assertEquals(
			existingCPOptionCategory.getUserId(),
			newCPOptionCategory.getUserId());
		Assert.assertEquals(
			existingCPOptionCategory.getUserName(),
			newCPOptionCategory.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOptionCategory.getCreateDate()),
			Time.getShortTimestamp(newCPOptionCategory.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOptionCategory.getModifiedDate()),
			Time.getShortTimestamp(newCPOptionCategory.getModifiedDate()));
		Assert.assertEquals(
			existingCPOptionCategory.getTitle(),
			newCPOptionCategory.getTitle());
		Assert.assertEquals(
			existingCPOptionCategory.getDescription(),
			newCPOptionCategory.getDescription());
		AssertUtils.assertEquals(
			existingCPOptionCategory.getPriority(),
			newCPOptionCategory.getPriority());
		Assert.assertEquals(
			existingCPOptionCategory.getKey(), newCPOptionCategory.getKey());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPOptionCategory.getLastPublishDate()),
			Time.getShortTimestamp(newCPOptionCategory.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateCPOptionCategoryExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPOptionCategory cpOptionCategory = addCPOptionCategory();

		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		newCPOptionCategory.setCompanyId(cpOptionCategory.getCompanyId());

		newCPOptionCategory = _persistence.update(newCPOptionCategory);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPOptionCategory);

		newCPOptionCategory.setExternalReferenceCode(
			cpOptionCategory.getExternalReferenceCode());

		_persistence.update(newCPOptionCategory);
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
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		CPOptionCategory existingCPOptionCategory =
			_persistence.findByPrimaryKey(newCPOptionCategory.getPrimaryKey());

		Assert.assertEquals(existingCPOptionCategory, newCPOptionCategory);
	}

	@Test(expected = NoSuchCPOptionCategoryException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPOptionCategory> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPOptionCategory", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "CPOptionCategoryId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "title", true,
			"description", true, "priority", true, "key", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		CPOptionCategory existingCPOptionCategory =
			_persistence.fetchByPrimaryKey(newCPOptionCategory.getPrimaryKey());

		Assert.assertEquals(existingCPOptionCategory, newCPOptionCategory);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionCategory missingCPOptionCategory =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPOptionCategory);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPOptionCategory newCPOptionCategory1 = addCPOptionCategory();
		CPOptionCategory newCPOptionCategory2 = addCPOptionCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOptionCategory1.getPrimaryKey());
		primaryKeys.add(newCPOptionCategory2.getPrimaryKey());

		Map<Serializable, CPOptionCategory> cpOptionCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpOptionCategories.size());
		Assert.assertEquals(
			newCPOptionCategory1,
			cpOptionCategories.get(newCPOptionCategory1.getPrimaryKey()));
		Assert.assertEquals(
			newCPOptionCategory2,
			cpOptionCategories.get(newCPOptionCategory2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPOptionCategory> cpOptionCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpOptionCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOptionCategory.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPOptionCategory> cpOptionCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpOptionCategories.size());
		Assert.assertEquals(
			newCPOptionCategory,
			cpOptionCategories.get(newCPOptionCategory.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPOptionCategory> cpOptionCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpOptionCategories.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOptionCategory.getPrimaryKey());

		Map<Serializable, CPOptionCategory> cpOptionCategories =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpOptionCategories.size());
		Assert.assertEquals(
			newCPOptionCategory,
			cpOptionCategories.get(newCPOptionCategory.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPOptionCategory newCPOptionCategory = addCPOptionCategory();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPOptionCategory.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPOptionCategory cpOptionCategory) {
		Assert.assertEquals(
			Long.valueOf(cpOptionCategory.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpOptionCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			cpOptionCategory.getKey(),
			ReflectionTestUtil.invoke(
				cpOptionCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));

		Assert.assertEquals(
			cpOptionCategory.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpOptionCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpOptionCategory.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpOptionCategory, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPOptionCategory addCPOptionCategory() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionCategory cpOptionCategory = _persistence.create(pk);

		cpOptionCategory.setMvccVersion(RandomTestUtil.nextLong());

		cpOptionCategory.setCtCollectionId(RandomTestUtil.nextLong());

		cpOptionCategory.setUuid(RandomTestUtil.randomString());

		cpOptionCategory.setExternalReferenceCode(
			RandomTestUtil.randomString());

		cpOptionCategory.setCompanyId(RandomTestUtil.nextLong());

		cpOptionCategory.setUserId(RandomTestUtil.nextLong());

		cpOptionCategory.setUserName(RandomTestUtil.randomString());

		cpOptionCategory.setCreateDate(RandomTestUtil.nextDate());

		cpOptionCategory.setModifiedDate(RandomTestUtil.nextDate());

		cpOptionCategory.setTitle(RandomTestUtil.randomString());

		cpOptionCategory.setDescription(RandomTestUtil.randomString());

		cpOptionCategory.setPriority(RandomTestUtil.nextDouble());

		cpOptionCategory.setKey(RandomTestUtil.randomString());

		cpOptionCategory.setLastPublishDate(RandomTestUtil.nextDate());

		_cpOptionCategories.add(_persistence.update(cpOptionCategory));

		return cpOptionCategory;
	}

	private List<CPOptionCategory> _cpOptionCategories =
		new ArrayList<CPOptionCategory>();
	private CPOptionCategoryPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}