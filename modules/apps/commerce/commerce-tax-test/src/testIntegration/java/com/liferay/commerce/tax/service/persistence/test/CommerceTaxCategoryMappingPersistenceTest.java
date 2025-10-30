/**
 * SPDX-FileCopyrightText: (c) 2025 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.tax.exception.DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException;
import com.liferay.commerce.tax.exception.NoSuchTaxCategoryMappingException;
import com.liferay.commerce.tax.model.CommerceTaxCategoryMapping;
import com.liferay.commerce.tax.service.persistence.CommerceTaxCategoryMappingPersistence;
import com.liferay.commerce.tax.service.persistence.CommerceTaxCategoryMappingUtil;
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
public class CommerceTaxCategoryMappingPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.tax.service"));

	@Before
	public void setUp() {
		_persistence = CommerceTaxCategoryMappingUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTaxCategoryMapping> iterator =
			_commerceTaxCategoryMappings.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			_persistence.create(pk);

		Assert.assertNotNull(commerceTaxCategoryMapping);

		Assert.assertEquals(commerceTaxCategoryMapping.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		_persistence.remove(newCommerceTaxCategoryMapping);

		CommerceTaxCategoryMapping existingCommerceTaxCategoryMapping =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxCategoryMapping.getPrimaryKey());

		Assert.assertNull(existingCommerceTaxCategoryMapping);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTaxCategoryMapping();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			_persistence.create(pk);

		newCommerceTaxCategoryMapping.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceTaxCategoryMapping.setUuid(RandomTestUtil.randomString());

		newCommerceTaxCategoryMapping.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceTaxCategoryMapping.setGroupId(RandomTestUtil.nextLong());

		newCommerceTaxCategoryMapping.setCompanyId(RandomTestUtil.nextLong());

		newCommerceTaxCategoryMapping.setUserId(RandomTestUtil.nextLong());

		newCommerceTaxCategoryMapping.setUserName(
			RandomTestUtil.randomString());

		newCommerceTaxCategoryMapping.setCreateDate(RandomTestUtil.nextDate());

		newCommerceTaxCategoryMapping.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceTaxCategoryMapping.setCommerceTaxMethodId(
			RandomTestUtil.nextLong());

		newCommerceTaxCategoryMapping.setCPTaxCategoryId(
			RandomTestUtil.nextLong());

		_commerceTaxCategoryMappings.add(
			_persistence.update(newCommerceTaxCategoryMapping));

		CommerceTaxCategoryMapping existingCommerceTaxCategoryMapping =
			_persistence.findByPrimaryKey(
				newCommerceTaxCategoryMapping.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getMvccVersion(),
			newCommerceTaxCategoryMapping.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getUuid(),
			newCommerceTaxCategoryMapping.getUuid());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getExternalReferenceCode(),
			newCommerceTaxCategoryMapping.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.
				getCommerceTaxCategoryMappingId(),
			newCommerceTaxCategoryMapping.getCommerceTaxCategoryMappingId());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getGroupId(),
			newCommerceTaxCategoryMapping.getGroupId());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getCompanyId(),
			newCommerceTaxCategoryMapping.getCompanyId());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getUserId(),
			newCommerceTaxCategoryMapping.getUserId());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getUserName(),
			newCommerceTaxCategoryMapping.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTaxCategoryMapping.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceTaxCategoryMapping.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTaxCategoryMapping.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceTaxCategoryMapping.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getCommerceTaxMethodId(),
			newCommerceTaxCategoryMapping.getCommerceTaxMethodId());
		Assert.assertEquals(
			existingCommerceTaxCategoryMapping.getCPTaxCategoryId(),
			newCommerceTaxCategoryMapping.getCPTaxCategoryId());
	}

	@Test(
		expected = DuplicateCommerceTaxCategoryMappingExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		newCommerceTaxCategoryMapping.setCompanyId(
			commerceTaxCategoryMapping.getCompanyId());

		newCommerceTaxCategoryMapping = _persistence.update(
			newCommerceTaxCategoryMapping);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceTaxCategoryMapping);

		newCommerceTaxCategoryMapping.setExternalReferenceCode(
			commerceTaxCategoryMapping.getExternalReferenceCode());

		_persistence.update(newCommerceTaxCategoryMapping);
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
	public void testCountByCommerceTaxMethodId() throws Exception {
		_persistence.countByCommerceTaxMethodId(RandomTestUtil.nextLong());

		_persistence.countByCommerceTaxMethodId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		CommerceTaxCategoryMapping existingCommerceTaxCategoryMapping =
			_persistence.findByPrimaryKey(
				newCommerceTaxCategoryMapping.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxCategoryMapping, newCommerceTaxCategoryMapping);
	}

	@Test(expected = NoSuchTaxCategoryMappingException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTaxCategoryMapping>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceTaxCategoryMapping", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceTaxCategoryMappingId", true,
			"groupId", true, "companyId", true, "userId", true, "userName",
			true, "createDate", true, "modifiedDate", true,
			"commerceTaxMethodId", true, "CPTaxCategoryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		CommerceTaxCategoryMapping existingCommerceTaxCategoryMapping =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxCategoryMapping.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxCategoryMapping, newCommerceTaxCategoryMapping);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxCategoryMapping missingCommerceTaxCategoryMapping =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTaxCategoryMapping);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping1 =
			addCommerceTaxCategoryMapping();
		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping2 =
			addCommerceTaxCategoryMapping();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxCategoryMapping1.getPrimaryKey());
		primaryKeys.add(newCommerceTaxCategoryMapping2.getPrimaryKey());

		Map<Serializable, CommerceTaxCategoryMapping>
			commerceTaxCategoryMappings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceTaxCategoryMappings.size());
		Assert.assertEquals(
			newCommerceTaxCategoryMapping1,
			commerceTaxCategoryMappings.get(
				newCommerceTaxCategoryMapping1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTaxCategoryMapping2,
			commerceTaxCategoryMappings.get(
				newCommerceTaxCategoryMapping2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTaxCategoryMapping>
			commerceTaxCategoryMappings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceTaxCategoryMappings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxCategoryMapping.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTaxCategoryMapping>
			commerceTaxCategoryMappings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceTaxCategoryMappings.size());
		Assert.assertEquals(
			newCommerceTaxCategoryMapping,
			commerceTaxCategoryMappings.get(
				newCommerceTaxCategoryMapping.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTaxCategoryMapping>
			commerceTaxCategoryMappings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceTaxCategoryMappings.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxCategoryMapping.getPrimaryKey());

		Map<Serializable, CommerceTaxCategoryMapping>
			commerceTaxCategoryMappings = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceTaxCategoryMappings.size());
		Assert.assertEquals(
			newCommerceTaxCategoryMapping,
			commerceTaxCategoryMappings.get(
				newCommerceTaxCategoryMapping.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceTaxCategoryMapping newCommerceTaxCategoryMapping =
			addCommerceTaxCategoryMapping();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceTaxCategoryMapping.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceTaxCategoryMapping commerceTaxCategoryMapping) {

		Assert.assertEquals(
			commerceTaxCategoryMapping.getUuid(),
			ReflectionTestUtil.invoke(
				commerceTaxCategoryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(commerceTaxCategoryMapping.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxCategoryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(commerceTaxCategoryMapping.getCommerceTaxMethodId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxCategoryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceTaxMethodId"));
		Assert.assertEquals(
			Long.valueOf(commerceTaxCategoryMapping.getCPTaxCategoryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxCategoryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPTaxCategoryId"));

		Assert.assertEquals(
			commerceTaxCategoryMapping.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceTaxCategoryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceTaxCategoryMapping.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxCategoryMapping, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceTaxCategoryMapping addCommerceTaxCategoryMapping()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceTaxCategoryMapping commerceTaxCategoryMapping =
			_persistence.create(pk);

		commerceTaxCategoryMapping.setMvccVersion(RandomTestUtil.nextLong());

		commerceTaxCategoryMapping.setUuid(RandomTestUtil.randomString());

		commerceTaxCategoryMapping.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceTaxCategoryMapping.setGroupId(RandomTestUtil.nextLong());

		commerceTaxCategoryMapping.setCompanyId(RandomTestUtil.nextLong());

		commerceTaxCategoryMapping.setUserId(RandomTestUtil.nextLong());

		commerceTaxCategoryMapping.setUserName(RandomTestUtil.randomString());

		commerceTaxCategoryMapping.setCreateDate(RandomTestUtil.nextDate());

		commerceTaxCategoryMapping.setModifiedDate(RandomTestUtil.nextDate());

		commerceTaxCategoryMapping.setCommerceTaxMethodId(
			RandomTestUtil.nextLong());

		commerceTaxCategoryMapping.setCPTaxCategoryId(
			RandomTestUtil.nextLong());

		_commerceTaxCategoryMappings.add(
			_persistence.update(commerceTaxCategoryMapping));

		return commerceTaxCategoryMapping;
	}

	private List<CommerceTaxCategoryMapping> _commerceTaxCategoryMappings =
		new ArrayList<CommerceTaxCategoryMapping>();
	private CommerceTaxCategoryMappingPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}