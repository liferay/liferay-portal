/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.asset.category.property.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.asset.category.property.exception.DuplicateAssetCategoryPropertyExternalReferenceCodeException;
import com.liferay.asset.category.property.exception.NoSuchCategoryPropertyException;
import com.liferay.asset.category.property.model.AssetCategoryProperty;
import com.liferay.asset.category.property.service.persistence.AssetCategoryPropertyPersistence;
import com.liferay.asset.category.property.service.persistence.AssetCategoryPropertyUtil;
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
public class AssetCategoryPropertyPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.asset.category.property.service"));

	@Before
	public void setUp() {
		_persistence = AssetCategoryPropertyUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<AssetCategoryProperty> iterator =
			_assetCategoryProperties.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategoryProperty assetCategoryProperty = _persistence.create(pk);

		Assert.assertNotNull(assetCategoryProperty);

		Assert.assertEquals(assetCategoryProperty.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		_persistence.remove(newAssetCategoryProperty);

		AssetCategoryProperty existingAssetCategoryProperty =
			_persistence.fetchByPrimaryKey(
				newAssetCategoryProperty.getPrimaryKey());

		Assert.assertNull(existingAssetCategoryProperty);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addAssetCategoryProperty();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategoryProperty newAssetCategoryProperty = _persistence.create(
			pk);

		newAssetCategoryProperty.setMvccVersion(RandomTestUtil.nextLong());

		newAssetCategoryProperty.setCtCollectionId(RandomTestUtil.nextLong());

		newAssetCategoryProperty.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newAssetCategoryProperty.setCompanyId(RandomTestUtil.nextLong());

		newAssetCategoryProperty.setUserId(RandomTestUtil.nextLong());

		newAssetCategoryProperty.setUserName(RandomTestUtil.randomString());

		newAssetCategoryProperty.setCreateDate(RandomTestUtil.nextDate());

		newAssetCategoryProperty.setModifiedDate(RandomTestUtil.nextDate());

		newAssetCategoryProperty.setCategoryId(RandomTestUtil.nextLong());

		newAssetCategoryProperty.setKey(RandomTestUtil.randomString());

		newAssetCategoryProperty.setValue(RandomTestUtil.randomString());

		_assetCategoryProperties.add(
			_persistence.update(newAssetCategoryProperty));

		AssetCategoryProperty existingAssetCategoryProperty =
			_persistence.findByPrimaryKey(
				newAssetCategoryProperty.getPrimaryKey());

		Assert.assertEquals(
			existingAssetCategoryProperty.getMvccVersion(),
			newAssetCategoryProperty.getMvccVersion());
		Assert.assertEquals(
			existingAssetCategoryProperty.getCtCollectionId(),
			newAssetCategoryProperty.getCtCollectionId());
		Assert.assertEquals(
			existingAssetCategoryProperty.getExternalReferenceCode(),
			newAssetCategoryProperty.getExternalReferenceCode());
		Assert.assertEquals(
			existingAssetCategoryProperty.getCategoryPropertyId(),
			newAssetCategoryProperty.getCategoryPropertyId());
		Assert.assertEquals(
			existingAssetCategoryProperty.getCompanyId(),
			newAssetCategoryProperty.getCompanyId());
		Assert.assertEquals(
			existingAssetCategoryProperty.getUserId(),
			newAssetCategoryProperty.getUserId());
		Assert.assertEquals(
			existingAssetCategoryProperty.getUserName(),
			newAssetCategoryProperty.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetCategoryProperty.getCreateDate()),
			Time.getShortTimestamp(newAssetCategoryProperty.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingAssetCategoryProperty.getModifiedDate()),
			Time.getShortTimestamp(newAssetCategoryProperty.getModifiedDate()));
		Assert.assertEquals(
			existingAssetCategoryProperty.getCategoryId(),
			newAssetCategoryProperty.getCategoryId());
		Assert.assertEquals(
			existingAssetCategoryProperty.getKey(),
			newAssetCategoryProperty.getKey());
		Assert.assertEquals(
			existingAssetCategoryProperty.getValue(),
			newAssetCategoryProperty.getValue());
	}

	@Test(
		expected = DuplicateAssetCategoryPropertyExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		AssetCategoryProperty assetCategoryProperty =
			addAssetCategoryProperty();

		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		newAssetCategoryProperty.setCompanyId(
			assetCategoryProperty.getCompanyId());

		newAssetCategoryProperty = _persistence.update(
			newAssetCategoryProperty);

		Session session = _persistence.getCurrentSession();

		session.evict(newAssetCategoryProperty);

		newAssetCategoryProperty.setExternalReferenceCode(
			assetCategoryProperty.getExternalReferenceCode());

		_persistence.update(newAssetCategoryProperty);
	}

	@Test
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCategoryId() throws Exception {
		_persistence.countByCategoryId(RandomTestUtil.nextLong());

		_persistence.countByCategoryId(0L);
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testCountByCA_K() throws Exception {
		_persistence.countByCA_K(RandomTestUtil.nextLong(), "");

		_persistence.countByCA_K(0L, "null");

		_persistence.countByCA_K(0L, (String)null);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		AssetCategoryProperty existingAssetCategoryProperty =
			_persistence.findByPrimaryKey(
				newAssetCategoryProperty.getPrimaryKey());

		Assert.assertEquals(
			existingAssetCategoryProperty, newAssetCategoryProperty);
	}

	@Test(expected = NoSuchCategoryPropertyException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<AssetCategoryProperty> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"AssetCategoryProperty", "mvccVersion", true, "ctCollectionId",
			true, "externalReferenceCode", true, "categoryPropertyId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "categoryId", true, "key", true,
			"value", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		AssetCategoryProperty existingAssetCategoryProperty =
			_persistence.fetchByPrimaryKey(
				newAssetCategoryProperty.getPrimaryKey());

		Assert.assertEquals(
			existingAssetCategoryProperty, newAssetCategoryProperty);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		AssetCategoryProperty missingAssetCategoryProperty =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingAssetCategoryProperty);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		AssetCategoryProperty newAssetCategoryProperty1 =
			addAssetCategoryProperty();
		AssetCategoryProperty newAssetCategoryProperty2 =
			addAssetCategoryProperty();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetCategoryProperty1.getPrimaryKey());
		primaryKeys.add(newAssetCategoryProperty2.getPrimaryKey());

		Map<Serializable, AssetCategoryProperty> assetCategoryProperties =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, assetCategoryProperties.size());
		Assert.assertEquals(
			newAssetCategoryProperty1,
			assetCategoryProperties.get(
				newAssetCategoryProperty1.getPrimaryKey()));
		Assert.assertEquals(
			newAssetCategoryProperty2,
			assetCategoryProperties.get(
				newAssetCategoryProperty2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, AssetCategoryProperty> assetCategoryProperties =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetCategoryProperties.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetCategoryProperty.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, AssetCategoryProperty> assetCategoryProperties =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetCategoryProperties.size());
		Assert.assertEquals(
			newAssetCategoryProperty,
			assetCategoryProperties.get(
				newAssetCategoryProperty.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, AssetCategoryProperty> assetCategoryProperties =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(assetCategoryProperties.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newAssetCategoryProperty.getPrimaryKey());

		Map<Serializable, AssetCategoryProperty> assetCategoryProperties =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, assetCategoryProperties.size());
		Assert.assertEquals(
			newAssetCategoryProperty,
			assetCategoryProperties.get(
				newAssetCategoryProperty.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		AssetCategoryProperty newAssetCategoryProperty =
			addAssetCategoryProperty();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newAssetCategoryProperty.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		AssetCategoryProperty assetCategoryProperty) {

		Assert.assertEquals(
			Long.valueOf(assetCategoryProperty.getCategoryId()),
			ReflectionTestUtil.<Long>invoke(
				assetCategoryProperty, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "categoryId"));
		Assert.assertEquals(
			assetCategoryProperty.getKey(),
			ReflectionTestUtil.invoke(
				assetCategoryProperty, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));

		Assert.assertEquals(
			assetCategoryProperty.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				assetCategoryProperty, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(assetCategoryProperty.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				assetCategoryProperty, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected AssetCategoryProperty addAssetCategoryProperty()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		AssetCategoryProperty assetCategoryProperty = _persistence.create(pk);

		assetCategoryProperty.setMvccVersion(RandomTestUtil.nextLong());

		assetCategoryProperty.setCtCollectionId(RandomTestUtil.nextLong());

		assetCategoryProperty.setExternalReferenceCode(
			RandomTestUtil.randomString());

		assetCategoryProperty.setCompanyId(RandomTestUtil.nextLong());

		assetCategoryProperty.setUserId(RandomTestUtil.nextLong());

		assetCategoryProperty.setUserName(RandomTestUtil.randomString());

		assetCategoryProperty.setCreateDate(RandomTestUtil.nextDate());

		assetCategoryProperty.setModifiedDate(RandomTestUtil.nextDate());

		assetCategoryProperty.setCategoryId(RandomTestUtil.nextLong());

		assetCategoryProperty.setKey(RandomTestUtil.randomString());

		assetCategoryProperty.setValue(RandomTestUtil.randomString());

		_assetCategoryProperties.add(
			_persistence.update(assetCategoryProperty));

		return assetCategoryProperty;
	}

	private List<AssetCategoryProperty> _assetCategoryProperties =
		new ArrayList<AssetCategoryProperty>();
	private AssetCategoryPropertyPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}