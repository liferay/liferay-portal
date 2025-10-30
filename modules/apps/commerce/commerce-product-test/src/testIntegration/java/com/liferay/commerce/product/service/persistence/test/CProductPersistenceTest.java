/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCProductExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCProductException;
import com.liferay.commerce.product.model.CProduct;
import com.liferay.commerce.product.service.persistence.CProductPersistence;
import com.liferay.commerce.product.service.persistence.CProductUtil;
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
public class CProductPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CProductUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CProduct> iterator = _cProducts.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CProduct cProduct = _persistence.create(pk);

		Assert.assertNotNull(cProduct);

		Assert.assertEquals(cProduct.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CProduct newCProduct = addCProduct();

		_persistence.remove(newCProduct);

		CProduct existingCProduct = _persistence.fetchByPrimaryKey(
			newCProduct.getPrimaryKey());

		Assert.assertNull(existingCProduct);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCProduct();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CProduct newCProduct = _persistence.create(pk);

		newCProduct.setMvccVersion(RandomTestUtil.nextLong());

		newCProduct.setCtCollectionId(RandomTestUtil.nextLong());

		newCProduct.setUuid(RandomTestUtil.randomString());

		newCProduct.setExternalReferenceCode(RandomTestUtil.randomString());

		newCProduct.setGroupId(RandomTestUtil.nextLong());

		newCProduct.setCompanyId(RandomTestUtil.nextLong());

		newCProduct.setUserId(RandomTestUtil.nextLong());

		newCProduct.setUserName(RandomTestUtil.randomString());

		newCProduct.setCreateDate(RandomTestUtil.nextDate());

		newCProduct.setModifiedDate(RandomTestUtil.nextDate());

		newCProduct.setPublishedCPDefinitionId(RandomTestUtil.nextLong());

		newCProduct.setLatestVersion(RandomTestUtil.nextInt());

		_cProducts.add(_persistence.update(newCProduct));

		CProduct existingCProduct = _persistence.findByPrimaryKey(
			newCProduct.getPrimaryKey());

		Assert.assertEquals(
			existingCProduct.getMvccVersion(), newCProduct.getMvccVersion());
		Assert.assertEquals(
			existingCProduct.getCtCollectionId(),
			newCProduct.getCtCollectionId());
		Assert.assertEquals(existingCProduct.getUuid(), newCProduct.getUuid());
		Assert.assertEquals(
			existingCProduct.getExternalReferenceCode(),
			newCProduct.getExternalReferenceCode());
		Assert.assertEquals(
			existingCProduct.getCProductId(), newCProduct.getCProductId());
		Assert.assertEquals(
			existingCProduct.getGroupId(), newCProduct.getGroupId());
		Assert.assertEquals(
			existingCProduct.getCompanyId(), newCProduct.getCompanyId());
		Assert.assertEquals(
			existingCProduct.getUserId(), newCProduct.getUserId());
		Assert.assertEquals(
			existingCProduct.getUserName(), newCProduct.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCProduct.getCreateDate()),
			Time.getShortTimestamp(newCProduct.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCProduct.getModifiedDate()),
			Time.getShortTimestamp(newCProduct.getModifiedDate()));
		Assert.assertEquals(
			existingCProduct.getPublishedCPDefinitionId(),
			newCProduct.getPublishedCPDefinitionId());
		Assert.assertEquals(
			existingCProduct.getLatestVersion(),
			newCProduct.getLatestVersion());
	}

	@Test(expected = DuplicateCProductExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CProduct cProduct = addCProduct();

		CProduct newCProduct = addCProduct();

		newCProduct.setCompanyId(cProduct.getCompanyId());

		newCProduct = _persistence.update(newCProduct);

		Session session = _persistence.getCurrentSession();

		session.evict(newCProduct);

		newCProduct.setExternalReferenceCode(
			cProduct.getExternalReferenceCode());

		_persistence.update(newCProduct);
	}

	@Test
	public void testCountByUuid() throws Exception {
		_persistence.countByUuid("");

		_persistence.countByUuid("null");

		_persistence.countByUuid((String)null);
	}

	@Test
	public void testCountByUUID_G() throws Exception {
		_persistence.countByUUID_G("", RandomTestUtil.nextLong());

		_persistence.countByUUID_G("null", 0L);

		_persistence.countByUUID_G((String)null, 0L);
	}

	@Test
	public void testCountByUuid_C() throws Exception {
		_persistence.countByUuid_C("", RandomTestUtil.nextLong());

		_persistence.countByUuid_C("null", 0L);

		_persistence.countByUuid_C((String)null, 0L);
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CProduct newCProduct = addCProduct();

		CProduct existingCProduct = _persistence.findByPrimaryKey(
			newCProduct.getPrimaryKey());

		Assert.assertEquals(existingCProduct, newCProduct);
	}

	@Test(expected = NoSuchCProductException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CProduct> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CProduct", "mvccVersion", true, "ctCollectionId", true, "uuid",
			true, "externalReferenceCode", true, "CProductId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "publishedCPDefinitionId",
			true, "latestVersion", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CProduct newCProduct = addCProduct();

		CProduct existingCProduct = _persistence.fetchByPrimaryKey(
			newCProduct.getPrimaryKey());

		Assert.assertEquals(existingCProduct, newCProduct);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CProduct missingCProduct = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCProduct);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CProduct newCProduct1 = addCProduct();
		CProduct newCProduct2 = addCProduct();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCProduct1.getPrimaryKey());
		primaryKeys.add(newCProduct2.getPrimaryKey());

		Map<Serializable, CProduct> cProducts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(2, cProducts.size());
		Assert.assertEquals(
			newCProduct1, cProducts.get(newCProduct1.getPrimaryKey()));
		Assert.assertEquals(
			newCProduct2, cProducts.get(newCProduct2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CProduct> cProducts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(cProducts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CProduct newCProduct = addCProduct();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCProduct.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CProduct> cProducts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, cProducts.size());
		Assert.assertEquals(
			newCProduct, cProducts.get(newCProduct.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CProduct> cProducts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertTrue(cProducts.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CProduct newCProduct = addCProduct();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCProduct.getPrimaryKey());

		Map<Serializable, CProduct> cProducts = _persistence.fetchByPrimaryKeys(
			primaryKeys);

		Assert.assertEquals(1, cProducts.size());
		Assert.assertEquals(
			newCProduct, cProducts.get(newCProduct.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CProduct newCProduct = addCProduct();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCProduct.getPrimaryKey()));
	}

	private void _assertOriginalValues(CProduct cProduct) {
		Assert.assertEquals(
			cProduct.getUuid(),
			ReflectionTestUtil.invoke(
				cProduct, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cProduct.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cProduct, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			cProduct.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cProduct, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cProduct.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cProduct, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CProduct addCProduct() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CProduct cProduct = _persistence.create(pk);

		cProduct.setMvccVersion(RandomTestUtil.nextLong());

		cProduct.setCtCollectionId(RandomTestUtil.nextLong());

		cProduct.setUuid(RandomTestUtil.randomString());

		cProduct.setExternalReferenceCode(RandomTestUtil.randomString());

		cProduct.setGroupId(RandomTestUtil.nextLong());

		cProduct.setCompanyId(RandomTestUtil.nextLong());

		cProduct.setUserId(RandomTestUtil.nextLong());

		cProduct.setUserName(RandomTestUtil.randomString());

		cProduct.setCreateDate(RandomTestUtil.nextDate());

		cProduct.setModifiedDate(RandomTestUtil.nextDate());

		cProduct.setPublishedCPDefinitionId(RandomTestUtil.nextLong());

		cProduct.setLatestVersion(RandomTestUtil.nextInt());

		_cProducts.add(_persistence.update(cProduct));

		return cProduct;
	}

	private List<CProduct> _cProducts = new ArrayList<CProduct>();
	private CProductPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}