/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPOptionValueExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPOptionValueException;
import com.liferay.commerce.product.model.CPOptionValue;
import com.liferay.commerce.product.service.persistence.CPOptionValuePersistence;
import com.liferay.commerce.product.service.persistence.CPOptionValueUtil;
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
public class CPOptionValuePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPOptionValueUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPOptionValue> iterator = _cpOptionValues.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionValue cpOptionValue = _persistence.create(pk);

		Assert.assertNotNull(cpOptionValue);

		Assert.assertEquals(cpOptionValue.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPOptionValue newCPOptionValue = addCPOptionValue();

		_persistence.remove(newCPOptionValue);

		CPOptionValue existingCPOptionValue = _persistence.fetchByPrimaryKey(
			newCPOptionValue.getPrimaryKey());

		Assert.assertNull(existingCPOptionValue);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPOptionValue();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionValue newCPOptionValue = _persistence.create(pk);

		newCPOptionValue.setMvccVersion(RandomTestUtil.nextLong());

		newCPOptionValue.setCtCollectionId(RandomTestUtil.nextLong());

		newCPOptionValue.setUuid(RandomTestUtil.randomString());

		newCPOptionValue.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPOptionValue.setCompanyId(RandomTestUtil.nextLong());

		newCPOptionValue.setUserId(RandomTestUtil.nextLong());

		newCPOptionValue.setUserName(RandomTestUtil.randomString());

		newCPOptionValue.setCreateDate(RandomTestUtil.nextDate());

		newCPOptionValue.setModifiedDate(RandomTestUtil.nextDate());

		newCPOptionValue.setCPOptionId(RandomTestUtil.nextLong());

		newCPOptionValue.setName(RandomTestUtil.randomString());

		newCPOptionValue.setPriority(RandomTestUtil.nextDouble());

		newCPOptionValue.setKey(RandomTestUtil.randomString());

		newCPOptionValue.setLastPublishDate(RandomTestUtil.nextDate());

		_cpOptionValues.add(_persistence.update(newCPOptionValue));

		CPOptionValue existingCPOptionValue = _persistence.findByPrimaryKey(
			newCPOptionValue.getPrimaryKey());

		Assert.assertEquals(
			existingCPOptionValue.getMvccVersion(),
			newCPOptionValue.getMvccVersion());
		Assert.assertEquals(
			existingCPOptionValue.getCtCollectionId(),
			newCPOptionValue.getCtCollectionId());
		Assert.assertEquals(
			existingCPOptionValue.getUuid(), newCPOptionValue.getUuid());
		Assert.assertEquals(
			existingCPOptionValue.getExternalReferenceCode(),
			newCPOptionValue.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPOptionValue.getCPOptionValueId(),
			newCPOptionValue.getCPOptionValueId());
		Assert.assertEquals(
			existingCPOptionValue.getCompanyId(),
			newCPOptionValue.getCompanyId());
		Assert.assertEquals(
			existingCPOptionValue.getUserId(), newCPOptionValue.getUserId());
		Assert.assertEquals(
			existingCPOptionValue.getUserName(),
			newCPOptionValue.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOptionValue.getCreateDate()),
			Time.getShortTimestamp(newCPOptionValue.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOptionValue.getModifiedDate()),
			Time.getShortTimestamp(newCPOptionValue.getModifiedDate()));
		Assert.assertEquals(
			existingCPOptionValue.getCPOptionId(),
			newCPOptionValue.getCPOptionId());
		Assert.assertEquals(
			existingCPOptionValue.getName(), newCPOptionValue.getName());
		AssertUtils.assertEquals(
			existingCPOptionValue.getPriority(),
			newCPOptionValue.getPriority());
		Assert.assertEquals(
			existingCPOptionValue.getKey(), newCPOptionValue.getKey());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPOptionValue.getLastPublishDate()),
			Time.getShortTimestamp(newCPOptionValue.getLastPublishDate()));
	}

	@Test(expected = DuplicateCPOptionValueExternalReferenceCodeException.class)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPOptionValue cpOptionValue = addCPOptionValue();

		CPOptionValue newCPOptionValue = addCPOptionValue();

		newCPOptionValue.setCompanyId(cpOptionValue.getCompanyId());

		newCPOptionValue = _persistence.update(newCPOptionValue);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPOptionValue);

		newCPOptionValue.setExternalReferenceCode(
			cpOptionValue.getExternalReferenceCode());

		_persistence.update(newCPOptionValue);
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
	public void testCountByCPOptionId() throws Exception {
		_persistence.countByCPOptionId(RandomTestUtil.nextLong());

		_persistence.countByCPOptionId(0L);
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
		CPOptionValue newCPOptionValue = addCPOptionValue();

		CPOptionValue existingCPOptionValue = _persistence.findByPrimaryKey(
			newCPOptionValue.getPrimaryKey());

		Assert.assertEquals(existingCPOptionValue, newCPOptionValue);
	}

	@Test(expected = NoSuchCPOptionValueException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPOptionValue> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPOptionValue", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true, "CPOptionValueId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "CPOptionId", true,
			"name", true, "priority", true, "key", true, "lastPublishDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPOptionValue newCPOptionValue = addCPOptionValue();

		CPOptionValue existingCPOptionValue = _persistence.fetchByPrimaryKey(
			newCPOptionValue.getPrimaryKey());

		Assert.assertEquals(existingCPOptionValue, newCPOptionValue);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionValue missingCPOptionValue = _persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPOptionValue);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPOptionValue newCPOptionValue1 = addCPOptionValue();
		CPOptionValue newCPOptionValue2 = addCPOptionValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOptionValue1.getPrimaryKey());
		primaryKeys.add(newCPOptionValue2.getPrimaryKey());

		Map<Serializable, CPOptionValue> cpOptionValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpOptionValues.size());
		Assert.assertEquals(
			newCPOptionValue1,
			cpOptionValues.get(newCPOptionValue1.getPrimaryKey()));
		Assert.assertEquals(
			newCPOptionValue2,
			cpOptionValues.get(newCPOptionValue2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPOptionValue> cpOptionValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpOptionValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPOptionValue newCPOptionValue = addCPOptionValue();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOptionValue.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPOptionValue> cpOptionValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpOptionValues.size());
		Assert.assertEquals(
			newCPOptionValue,
			cpOptionValues.get(newCPOptionValue.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPOptionValue> cpOptionValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpOptionValues.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPOptionValue newCPOptionValue = addCPOptionValue();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPOptionValue.getPrimaryKey());

		Map<Serializable, CPOptionValue> cpOptionValues =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpOptionValues.size());
		Assert.assertEquals(
			newCPOptionValue,
			cpOptionValues.get(newCPOptionValue.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPOptionValue newCPOptionValue = addCPOptionValue();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCPOptionValue.getPrimaryKey()));
	}

	private void _assertOriginalValues(CPOptionValue cpOptionValue) {
		Assert.assertEquals(
			Long.valueOf(cpOptionValue.getCPOptionId()),
			ReflectionTestUtil.<Long>invoke(
				cpOptionValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPOptionId"));
		Assert.assertEquals(
			cpOptionValue.getKey(),
			ReflectionTestUtil.invoke(
				cpOptionValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));

		Assert.assertEquals(
			cpOptionValue.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpOptionValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpOptionValue.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpOptionValue, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPOptionValue addCPOptionValue() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPOptionValue cpOptionValue = _persistence.create(pk);

		cpOptionValue.setMvccVersion(RandomTestUtil.nextLong());

		cpOptionValue.setCtCollectionId(RandomTestUtil.nextLong());

		cpOptionValue.setUuid(RandomTestUtil.randomString());

		cpOptionValue.setExternalReferenceCode(RandomTestUtil.randomString());

		cpOptionValue.setCompanyId(RandomTestUtil.nextLong());

		cpOptionValue.setUserId(RandomTestUtil.nextLong());

		cpOptionValue.setUserName(RandomTestUtil.randomString());

		cpOptionValue.setCreateDate(RandomTestUtil.nextDate());

		cpOptionValue.setModifiedDate(RandomTestUtil.nextDate());

		cpOptionValue.setCPOptionId(RandomTestUtil.nextLong());

		cpOptionValue.setName(RandomTestUtil.randomString());

		cpOptionValue.setPriority(RandomTestUtil.nextDouble());

		cpOptionValue.setKey(RandomTestUtil.randomString());

		cpOptionValue.setLastPublishDate(RandomTestUtil.nextDate());

		_cpOptionValues.add(_persistence.update(cpOptionValue));

		return cpOptionValue;
	}

	private List<CPOptionValue> _cpOptionValues =
		new ArrayList<CPOptionValue>();
	private CPOptionValuePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}