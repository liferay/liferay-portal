/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPOptionExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPOptionException;
import com.liferay.commerce.product.model.CPOption;
import com.liferay.commerce.product.service.persistence.CPOptionPersistence;
import com.liferay.commerce.product.service.persistence.CPOptionUtil;
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
public class CPOptionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPOptionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPOption> iterator = _cpOptions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOption cpOption = _persistence.create(pk);

		Assert.assertNotNull(cpOption);

		Assert.assertEquals(cpOption.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPOption newCPOption = addCPOption();

		_persistence.remove(newCPOption);

		CPOption existingCPOption = _persistence.fetchByPrimaryKey(
			newCPOption.getPrimaryKey());

		Assert.assertNull(existingCPOption);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPOption();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOption newCPOption = _persistence.create(pk);

		newCPOption.setMvccVersion(RandomTestUtil.nextLong());

		newCPOption.setCtCollectionId(RandomTestUtil.nextLong());

		newCPOption.setUuid(RandomTestUtil.randomString());

		newCPOption.setExternalReferenceCode(RandomTestUtil.randomString());

		newCPOption.setCompanyId(RandomTestUtil.nextLong());

		newCPOption.setUserId(RandomTestUtil.nextLong());

		newCPOption.setUserName(RandomTestUtil.randomString());

		newCPOption.setCreateDate(RandomTestUtil.nextDate());

		newCPOption.setModifiedDate(RandomTestUtil.nextDate());

		newCPOption.setName(RandomTestUtil.randomString());

		newCPOption.setDescription(RandomTestUtil.randomString());

		newCPOption.setCommerceOptionTypeKey(RandomTestUtil.randomString());

		newCPOption.setFacetable(RandomTestUtil.randomBoolean());

		newCPOption.setRequired(RandomTestUtil.randomBoolean());

		newCPOption.setSkuContributor(RandomTestUtil.randomBoolean());

		newCPOption.setKey(RandomTestUtil.randomString());

		newCPOption.setLastPublishDate(RandomTestUtil.nextDate());

		_cpOptions.add(_persistence.update(newCPOption));

		CPOption existingCPOption = _persistence.findByPrimaryKey(
			newCPOption.getPrimaryKey());

		Assert.assertEquals(
			existingCPOption.getMvccVersion(), newCPOption.getMvccVersion());
		Assert.assertEquals(
			existingCPOption.getCtCollectionId(),
			newCPOption.getCtCollectionId());
		Assert.assertEquals(existingCPOption.getUuid(), newCPOption.getUuid());
		Assert.assertEquals(
			existingCPOption.getExternalReferenceCode(),
			newCPOption.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPOption.getCPOptionId(), newCPOption.getCPOptionId());
		Assert.assertEquals(
			existingCPOption.getCompanyId(), newCPOption.getCompanyId());
		Assert.assertEquals(
			existingCPOption.getUserId(), newCPOption.getUserId());
		Assert.assertEquals(
			existingCPOption.getUserName(), newCPOption.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOption.getCreateDate()),
			Time.getShortTimestamp(newCPOption.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOption.getModifiedDate()),
			Time.getShortTimestamp(newCPOption.getModifiedDate()));
		Assert.assertEquals(existingCPOption.getName(), newCPOption.getName());
		Assert.assertEquals(
			existingCPOption.getDescription(), newCPOption.getDescription());
		Assert.assertEquals(
			existingCPOption.getCommerceOptionTypeKey(),
			newCPOption.getCommerceOptionTypeKey());
		Assert.assertEquals(
			existingCPOption.isFacetable(), newCPOption.isFacetable());
		Assert.assertEquals(
			existingCPOption.isRequired(), newCPOption.isRequired());
		Assert.assertEquals(
			existingCPOption.isSkuContributor(),
			newCPOption.isSkuContributor());
		Assert.assertEquals(existingCPOption.getKey(), newCPOption.getKey());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOption.getLastPublishDate()),
			Time.getShortTimestamp(newCPOption.getLastPublishDate()));
	}

	@Test(expected = DuplicateCPOptionExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPOption cpOption = addCPOption();

		CPOption newCPOption = addCPOption();

		newCPOption.setCompanyId(cpOption.getCompanyId());

		newCPOption = _persistence.update(newCPOption);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPOption);

		newCPOption.setExternalReferenceCode(
			cpOption.getExternalReferenceCode());

		_persistence.update(newCPOption);
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
		CPOption newCPOption = addCPOption();

		CPOption existingCPOption = _persistence.findByPrimaryKey(
			newCPOption.getPrimaryKey());

		Assert.assertEquals(existingCPOption, newCPOption);
	}

	@Test(expected = NoSuchCPOptionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPOption> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPOption", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "CPOptionId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "name", true, "description", true,
			"commerceOptionTypeKey", true, "facetable", true, "required", true,
			"skuContributor", true, "key", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPOption newCPOption = addCPOption();

		CPOption existingCPOption = _persistence.fetchByPrimaryKey(
			newCPOption.getPrimaryKey());

		Assert.assertEquals(existingCPOption, newCPOption);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOption missingCPOption = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPOption);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPOption newCPOption1 = addCPOption();
		CPOption newCPOption2 = addCPOption();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOption1.getPrimaryKey());
		primaryKeys.add(newCPOption2.getPrimaryKey());

		Map<Serializable, CPOption> cpOptions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, cpOptions.size());
		Assert.assertEquals(
			newCPOption1, cpOptions.get(newCPOption1.getPrimaryKey()));
		Assert.assertEquals(
			newCPOption2, cpOptions.get(newCPOption2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPOption> cpOptions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(cpOptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPOption newCPOption = addCPOption();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOption.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPOption> cpOptions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, cpOptions.size());
		Assert.assertEquals(
			newCPOption, cpOptions.get(newCPOption.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPOption> cpOptions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(cpOptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPOption newCPOption = addCPOption();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOption.getPrimaryKey());

		Map<Serializable, CPOption> cpOptions = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, cpOptions.size());
		Assert.assertEquals(
			newCPOption, cpOptions.get(newCPOption.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPOption newCPOption = addCPOption();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPOption.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPOption cpOption) {
		Assert.assertEquals(
			Long.valueOf(cpOption.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			cpOption.getKey(),
			ReflectionTestUtil.invoke(
				cpOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));

		Assert.assertEquals(
			cpOption.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpOption.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPOption addCPOption() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOption cpOption = _persistence.create(pk);

		cpOption.setMvccVersion(RandomTestUtil.nextLong());

		cpOption.setCtCollectionId(RandomTestUtil.nextLong());

		cpOption.setUuid(RandomTestUtil.randomString());

		cpOption.setExternalReferenceCode(RandomTestUtil.randomString());

		cpOption.setCompanyId(RandomTestUtil.nextLong());

		cpOption.setUserId(RandomTestUtil.nextLong());

		cpOption.setUserName(RandomTestUtil.randomString());

		cpOption.setCreateDate(RandomTestUtil.nextDate());

		cpOption.setModifiedDate(RandomTestUtil.nextDate());

		cpOption.setName(RandomTestUtil.randomString());

		cpOption.setDescription(RandomTestUtil.randomString());

		cpOption.setCommerceOptionTypeKey(RandomTestUtil.randomString());

		cpOption.setFacetable(RandomTestUtil.randomBoolean());

		cpOption.setRequired(RandomTestUtil.randomBoolean());

		cpOption.setSkuContributor(RandomTestUtil.randomBoolean());

		cpOption.setKey(RandomTestUtil.randomString());

		cpOption.setLastPublishDate(RandomTestUtil.nextDate());

		_cpOptions.add(_persistence.update(cpOption));

		return cpOption;
	}

	private List<CPOption> _cpOptions = new ArrayList<CPOption>();
	private CPOptionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}