/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.pricing.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.pricing.exception.DuplicateCommercePricingClassExternalReferenceCodeException;
import com.liferay.commerce.pricing.exception.NoSuchPricingClassException;
import com.liferay.commerce.pricing.model.CommercePricingClass;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassPersistence;
import com.liferay.commerce.pricing.service.persistence.CommercePricingClassUtil;
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
public class CommercePricingClassPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.pricing.service"));

	@Before
	public void setUp() {
		_persistence = CommercePricingClassUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePricingClass> iterator =
			_commercePricingClasses.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePricingClass commercePricingClass = _persistence.create(pk);

		Assert.assertNotNull(commercePricingClass);

		Assert.assertEquals(commercePricingClass.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		_persistence.remove(newCommercePricingClass);

		CommercePricingClass existingCommercePricingClass =
			_persistence.fetchByPrimaryKey(
				newCommercePricingClass.getPrimaryKey());

		Assert.assertNull(existingCommercePricingClass);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePricingClass();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePricingClass newCommercePricingClass = _persistence.create(pk);

		newCommercePricingClass.setMvccVersion(RandomTestUtil.nextLong());

		newCommercePricingClass.setCtCollectionId(RandomTestUtil.nextLong());

		newCommercePricingClass.setUuid(RandomTestUtil.randomString());

		newCommercePricingClass.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommercePricingClass.setCompanyId(RandomTestUtil.nextLong());

		newCommercePricingClass.setUserId(RandomTestUtil.nextLong());

		newCommercePricingClass.setUserName(RandomTestUtil.randomString());

		newCommercePricingClass.setCreateDate(RandomTestUtil.nextDate());

		newCommercePricingClass.setModifiedDate(RandomTestUtil.nextDate());

		newCommercePricingClass.setTitle(RandomTestUtil.randomString());

		newCommercePricingClass.setDescription(RandomTestUtil.randomString());

		newCommercePricingClass.setLastPublishDate(RandomTestUtil.nextDate());

		_commercePricingClasses.add(
			_persistence.update(newCommercePricingClass));

		CommercePricingClass existingCommercePricingClass =
			_persistence.findByPrimaryKey(
				newCommercePricingClass.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePricingClass.getMvccVersion(),
			newCommercePricingClass.getMvccVersion());
		Assert.assertEquals(
			existingCommercePricingClass.getCtCollectionId(),
			newCommercePricingClass.getCtCollectionId());
		Assert.assertEquals(
			existingCommercePricingClass.getUuid(),
			newCommercePricingClass.getUuid());
		Assert.assertEquals(
			existingCommercePricingClass.getExternalReferenceCode(),
			newCommercePricingClass.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommercePricingClass.getCommercePricingClassId(),
			newCommercePricingClass.getCommercePricingClassId());
		Assert.assertEquals(
			existingCommercePricingClass.getCompanyId(),
			newCommercePricingClass.getCompanyId());
		Assert.assertEquals(
			existingCommercePricingClass.getUserId(),
			newCommercePricingClass.getUserId());
		Assert.assertEquals(
			existingCommercePricingClass.getUserName(),
			newCommercePricingClass.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePricingClass.getCreateDate()),
			Time.getShortTimestamp(newCommercePricingClass.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePricingClass.getModifiedDate()),
			Time.getShortTimestamp(newCommercePricingClass.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePricingClass.getTitle(),
			newCommercePricingClass.getTitle());
		Assert.assertEquals(
			existingCommercePricingClass.getDescription(),
			newCommercePricingClass.getDescription());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePricingClass.getLastPublishDate()),
			Time.getShortTimestamp(
				newCommercePricingClass.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateCommercePricingClassExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommercePricingClass commercePricingClass = addCommercePricingClass();

		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		newCommercePricingClass.setCompanyId(
			commercePricingClass.getCompanyId());

		newCommercePricingClass = _persistence.update(newCommercePricingClass);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommercePricingClass);

		newCommercePricingClass.setExternalReferenceCode(
			commercePricingClass.getExternalReferenceCode());

		_persistence.update(newCommercePricingClass);
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
		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		CommercePricingClass existingCommercePricingClass =
			_persistence.findByPrimaryKey(
				newCommercePricingClass.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePricingClass, newCommercePricingClass);
	}

	@Test(expected = NoSuchPricingClassException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePricingClass> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommercePricingClass", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true,
			"commercePricingClassId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "title",
			true, "description", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		CommercePricingClass existingCommercePricingClass =
			_persistence.fetchByPrimaryKey(
				newCommercePricingClass.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePricingClass, newCommercePricingClass);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePricingClass missingCommercePricingClass =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePricingClass);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePricingClass newCommercePricingClass1 =
			addCommercePricingClass();
		CommercePricingClass newCommercePricingClass2 =
			addCommercePricingClass();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePricingClass1.getPrimaryKey());
		primaryKeys.add(newCommercePricingClass2.getPrimaryKey());

		Map<Serializable, CommercePricingClass> commercePricingClasses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commercePricingClasses.size());
		Assert.assertEquals(
			newCommercePricingClass1,
			commercePricingClasses.get(
				newCommercePricingClass1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePricingClass2,
			commercePricingClasses.get(
				newCommercePricingClass2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePricingClass> commercePricingClasses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePricingClasses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePricingClass.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePricingClass> commercePricingClasses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePricingClasses.size());
		Assert.assertEquals(
			newCommercePricingClass,
			commercePricingClasses.get(
				newCommercePricingClass.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePricingClass> commercePricingClasses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePricingClasses.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePricingClass.getPrimaryKey());

		Map<Serializable, CommercePricingClass> commercePricingClasses =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePricingClasses.size());
		Assert.assertEquals(
			newCommercePricingClass,
			commercePricingClasses.get(
				newCommercePricingClass.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePricingClass newCommercePricingClass =
			addCommercePricingClass();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePricingClass.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePricingClass commercePricingClass) {

		Assert.assertEquals(
			commercePricingClass.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commercePricingClass, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commercePricingClass.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commercePricingClass, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommercePricingClass addCommercePricingClass() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePricingClass commercePricingClass = _persistence.create(pk);

		commercePricingClass.setMvccVersion(RandomTestUtil.nextLong());

		commercePricingClass.setCtCollectionId(RandomTestUtil.nextLong());

		commercePricingClass.setUuid(RandomTestUtil.randomString());

		commercePricingClass.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commercePricingClass.setCompanyId(RandomTestUtil.nextLong());

		commercePricingClass.setUserId(RandomTestUtil.nextLong());

		commercePricingClass.setUserName(RandomTestUtil.randomString());

		commercePricingClass.setCreateDate(RandomTestUtil.nextDate());

		commercePricingClass.setModifiedDate(RandomTestUtil.nextDate());

		commercePricingClass.setTitle(RandomTestUtil.randomString());

		commercePricingClass.setDescription(RandomTestUtil.randomString());

		commercePricingClass.setLastPublishDate(RandomTestUtil.nextDate());

		_commercePricingClasses.add(_persistence.update(commercePricingClass));

		return commercePricingClass;
	}

	private List<CommercePricingClass> _commercePricingClasses =
		new ArrayList<CommercePricingClass>();
	private CommercePricingClassPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}