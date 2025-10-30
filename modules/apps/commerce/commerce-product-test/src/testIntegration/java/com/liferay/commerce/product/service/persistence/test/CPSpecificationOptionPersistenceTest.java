/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPSpecificationOptionExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPSpecificationOptionException;
import com.liferay.commerce.product.model.CPSpecificationOption;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionPersistence;
import com.liferay.commerce.product.service.persistence.CPSpecificationOptionUtil;
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
public class CPSpecificationOptionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPSpecificationOptionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPSpecificationOption> iterator =
			_cpSpecificationOptions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPSpecificationOption cpSpecificationOption = _persistence.create(pk);

		Assert.assertNotNull(cpSpecificationOption);

		Assert.assertEquals(cpSpecificationOption.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		_persistence.remove(newCPSpecificationOption);

		CPSpecificationOption existingCPSpecificationOption =
			_persistence.fetchByPrimaryKey(
				newCPSpecificationOption.getPrimaryKey());

		Assert.assertNull(existingCPSpecificationOption);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPSpecificationOption();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPSpecificationOption newCPSpecificationOption = _persistence.create(
			pk);

		newCPSpecificationOption.setMvccVersion(RandomTestUtil.nextLong());

		newCPSpecificationOption.setCtCollectionId(RandomTestUtil.nextLong());

		newCPSpecificationOption.setUuid(RandomTestUtil.randomString());

		newCPSpecificationOption.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPSpecificationOption.setCompanyId(RandomTestUtil.nextLong());

		newCPSpecificationOption.setUserId(RandomTestUtil.nextLong());

		newCPSpecificationOption.setUserName(RandomTestUtil.randomString());

		newCPSpecificationOption.setCreateDate(RandomTestUtil.nextDate());

		newCPSpecificationOption.setModifiedDate(RandomTestUtil.nextDate());

		newCPSpecificationOption.setCPOptionCategoryId(
			RandomTestUtil.nextLong());

		newCPSpecificationOption.setTitle(RandomTestUtil.randomString());

		newCPSpecificationOption.setDescription(RandomTestUtil.randomString());

		newCPSpecificationOption.setFacetable(RandomTestUtil.randomBoolean());

		newCPSpecificationOption.setKey(RandomTestUtil.randomString());

		newCPSpecificationOption.setPriority(RandomTestUtil.nextDouble());

		newCPSpecificationOption.setVisible(RandomTestUtil.randomBoolean());

		newCPSpecificationOption.setLastPublishDate(RandomTestUtil.nextDate());

		_cpSpecificationOptions.add(
			_persistence.update(newCPSpecificationOption));

		CPSpecificationOption existingCPSpecificationOption =
			_persistence.findByPrimaryKey(
				newCPSpecificationOption.getPrimaryKey());

		Assert.assertEquals(
			existingCPSpecificationOption.getMvccVersion(),
			newCPSpecificationOption.getMvccVersion());
		Assert.assertEquals(
			existingCPSpecificationOption.getCtCollectionId(),
			newCPSpecificationOption.getCtCollectionId());
		Assert.assertEquals(
			existingCPSpecificationOption.getUuid(),
			newCPSpecificationOption.getUuid());
		Assert.assertEquals(
			existingCPSpecificationOption.getExternalReferenceCode(),
			newCPSpecificationOption.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPSpecificationOption.getCPSpecificationOptionId(),
			newCPSpecificationOption.getCPSpecificationOptionId());
		Assert.assertEquals(
			existingCPSpecificationOption.getCompanyId(),
			newCPSpecificationOption.getCompanyId());
		Assert.assertEquals(
			existingCPSpecificationOption.getUserId(),
			newCPSpecificationOption.getUserId());
		Assert.assertEquals(
			existingCPSpecificationOption.getUserName(),
			newCPSpecificationOption.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPSpecificationOption.getCreateDate()),
			Time.getShortTimestamp(newCPSpecificationOption.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPSpecificationOption.getModifiedDate()),
			Time.getShortTimestamp(newCPSpecificationOption.getModifiedDate()));
		Assert.assertEquals(
			existingCPSpecificationOption.getCPOptionCategoryId(),
			newCPSpecificationOption.getCPOptionCategoryId());
		Assert.assertEquals(
			existingCPSpecificationOption.getTitle(),
			newCPSpecificationOption.getTitle());
		Assert.assertEquals(
			existingCPSpecificationOption.getDescription(),
			newCPSpecificationOption.getDescription());
		Assert.assertEquals(
			existingCPSpecificationOption.isFacetable(),
			newCPSpecificationOption.isFacetable());
		Assert.assertEquals(
			existingCPSpecificationOption.getKey(),
			newCPSpecificationOption.getKey());
		AssertUtils.assertEquals(
			existingCPSpecificationOption.getPriority(),
			newCPSpecificationOption.getPriority());
		Assert.assertEquals(
			existingCPSpecificationOption.isVisible(),
			newCPSpecificationOption.isVisible());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPSpecificationOption.getLastPublishDate()),
			Time.getShortTimestamp(
				newCPSpecificationOption.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateCPSpecificationOptionExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPSpecificationOption cpSpecificationOption =
			addCPSpecificationOption();

		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		newCPSpecificationOption.setCompanyId(
			cpSpecificationOption.getCompanyId());

		newCPSpecificationOption = _persistence.update(
			newCPSpecificationOption);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPSpecificationOption);

		newCPSpecificationOption.setExternalReferenceCode(
			cpSpecificationOption.getExternalReferenceCode());

		_persistence.update(newCPSpecificationOption);
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
	public void testCountByCPOptionCategoryId() throws Exception {
		_persistence.countByCPOptionCategoryId(RandomTestUtil.nextLong());

		_persistence.countByCPOptionCategoryId(0L);
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
		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		CPSpecificationOption existingCPSpecificationOption =
			_persistence.findByPrimaryKey(
				newCPSpecificationOption.getPrimaryKey());

		Assert.assertEquals(
			existingCPSpecificationOption, newCPSpecificationOption);
	}

	@Test(expected = NoSuchCPSpecificationOptionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPSpecificationOption> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPSpecificationOption", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "externalReferenceCode", true,
			"CPSpecificationOptionId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"CPOptionCategoryId", true, "title", true, "description", true,
			"facetable", true, "key", true, "priority", true, "visible", true,
			"lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		CPSpecificationOption existingCPSpecificationOption =
			_persistence.fetchByPrimaryKey(
				newCPSpecificationOption.getPrimaryKey());

		Assert.assertEquals(
			existingCPSpecificationOption, newCPSpecificationOption);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPSpecificationOption missingCPSpecificationOption =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPSpecificationOption);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPSpecificationOption newCPSpecificationOption1 =
			addCPSpecificationOption();
		CPSpecificationOption newCPSpecificationOption2 =
			addCPSpecificationOption();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPSpecificationOption1.getPrimaryKey());
		primaryKeys.add(newCPSpecificationOption2.getPrimaryKey());

		Map<Serializable, CPSpecificationOption> cpSpecificationOptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpSpecificationOptions.size());
		Assert.assertEquals(
			newCPSpecificationOption1,
			cpSpecificationOptions.get(
				newCPSpecificationOption1.getPrimaryKey()));
		Assert.assertEquals(
			newCPSpecificationOption2,
			cpSpecificationOptions.get(
				newCPSpecificationOption2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPSpecificationOption> cpSpecificationOptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpSpecificationOptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPSpecificationOption.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPSpecificationOption> cpSpecificationOptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpSpecificationOptions.size());
		Assert.assertEquals(
			newCPSpecificationOption,
			cpSpecificationOptions.get(
				newCPSpecificationOption.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPSpecificationOption> cpSpecificationOptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpSpecificationOptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPSpecificationOption.getPrimaryKey());

		Map<Serializable, CPSpecificationOption> cpSpecificationOptions =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpSpecificationOptions.size());
		Assert.assertEquals(
			newCPSpecificationOption,
			cpSpecificationOptions.get(
				newCPSpecificationOption.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPSpecificationOption newCPSpecificationOption =
			addCPSpecificationOption();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPSpecificationOption.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPSpecificationOption cpSpecificationOption) {

		Assert.assertEquals(
			Long.valueOf(cpSpecificationOption.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpSpecificationOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			cpSpecificationOption.getKey(),
			ReflectionTestUtil.invoke(
				cpSpecificationOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));

		Assert.assertEquals(
			cpSpecificationOption.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpSpecificationOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpSpecificationOption.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpSpecificationOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPSpecificationOption addCPSpecificationOption()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPSpecificationOption cpSpecificationOption = _persistence.create(pk);

		cpSpecificationOption.setMvccVersion(RandomTestUtil.nextLong());

		cpSpecificationOption.setCtCollectionId(RandomTestUtil.nextLong());

		cpSpecificationOption.setUuid(RandomTestUtil.randomString());

		cpSpecificationOption.setExternalReferenceCode(
			RandomTestUtil.randomString());

		cpSpecificationOption.setCompanyId(RandomTestUtil.nextLong());

		cpSpecificationOption.setUserId(RandomTestUtil.nextLong());

		cpSpecificationOption.setUserName(RandomTestUtil.randomString());

		cpSpecificationOption.setCreateDate(RandomTestUtil.nextDate());

		cpSpecificationOption.setModifiedDate(RandomTestUtil.nextDate());

		cpSpecificationOption.setCPOptionCategoryId(RandomTestUtil.nextLong());

		cpSpecificationOption.setTitle(RandomTestUtil.randomString());

		cpSpecificationOption.setDescription(RandomTestUtil.randomString());

		cpSpecificationOption.setFacetable(RandomTestUtil.randomBoolean());

		cpSpecificationOption.setKey(RandomTestUtil.randomString());

		cpSpecificationOption.setPriority(RandomTestUtil.nextDouble());

		cpSpecificationOption.setVisible(RandomTestUtil.randomBoolean());

		cpSpecificationOption.setLastPublishDate(RandomTestUtil.nextDate());

		_cpSpecificationOptions.add(_persistence.update(cpSpecificationOption));

		return cpSpecificationOption;
	}

	private List<CPSpecificationOption> _cpSpecificationOptions =
		new ArrayList<CPSpecificationOption>();
	private CPSpecificationOptionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}