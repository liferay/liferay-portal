/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPInstanceUnitOfMeasureException;
import com.liferay.commerce.product.model.CPInstanceUnitOfMeasure;
import com.liferay.commerce.product.service.persistence.CPInstanceUnitOfMeasurePersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceUnitOfMeasureUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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

import java.math.BigDecimal;

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
public class CPInstanceUnitOfMeasurePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPInstanceUnitOfMeasureUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPInstanceUnitOfMeasure> iterator =
			_cpInstanceUnitOfMeasures.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure = _persistence.create(
			pk);

		Assert.assertNotNull(cpInstanceUnitOfMeasure);

		Assert.assertEquals(cpInstanceUnitOfMeasure.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			addCPInstanceUnitOfMeasure();

		_persistence.remove(newCPInstanceUnitOfMeasure);

		CPInstanceUnitOfMeasure existingCPInstanceUnitOfMeasure =
			_persistence.fetchByPrimaryKey(
				newCPInstanceUnitOfMeasure.getPrimaryKey());

		Assert.assertNull(existingCPInstanceUnitOfMeasure);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPInstanceUnitOfMeasure();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			_persistence.create(pk);

		newCPInstanceUnitOfMeasure.setMvccVersion(RandomTestUtil.nextLong());

		newCPInstanceUnitOfMeasure.setCtCollectionId(RandomTestUtil.nextLong());

		newCPInstanceUnitOfMeasure.setUuid(RandomTestUtil.randomString());

		newCPInstanceUnitOfMeasure.setCompanyId(RandomTestUtil.nextLong());

		newCPInstanceUnitOfMeasure.setUserId(RandomTestUtil.nextLong());

		newCPInstanceUnitOfMeasure.setUserName(RandomTestUtil.randomString());

		newCPInstanceUnitOfMeasure.setCreateDate(RandomTestUtil.nextDate());

		newCPInstanceUnitOfMeasure.setModifiedDate(RandomTestUtil.nextDate());

		newCPInstanceUnitOfMeasure.setCPInstanceId(RandomTestUtil.nextLong());

		newCPInstanceUnitOfMeasure.setActive(RandomTestUtil.randomBoolean());

		newCPInstanceUnitOfMeasure.setIncrementalOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPInstanceUnitOfMeasure.setKey(RandomTestUtil.randomString());

		newCPInstanceUnitOfMeasure.setName(RandomTestUtil.randomString());

		newCPInstanceUnitOfMeasure.setPrecision(RandomTestUtil.nextInt());

		newCPInstanceUnitOfMeasure.setPricingQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPInstanceUnitOfMeasure.setPrimary(RandomTestUtil.randomBoolean());

		newCPInstanceUnitOfMeasure.setPriority(RandomTestUtil.nextDouble());

		newCPInstanceUnitOfMeasure.setRate(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPInstanceUnitOfMeasure.setSku(RandomTestUtil.randomString());

		_cpInstanceUnitOfMeasures.add(
			_persistence.update(newCPInstanceUnitOfMeasure));

		CPInstanceUnitOfMeasure existingCPInstanceUnitOfMeasure =
			_persistence.findByPrimaryKey(
				newCPInstanceUnitOfMeasure.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getMvccVersion(),
			newCPInstanceUnitOfMeasure.getMvccVersion());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getCtCollectionId(),
			newCPInstanceUnitOfMeasure.getCtCollectionId());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getUuid(),
			newCPInstanceUnitOfMeasure.getUuid());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId(),
			newCPInstanceUnitOfMeasure.getCPInstanceUnitOfMeasureId());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getCompanyId(),
			newCPInstanceUnitOfMeasure.getCompanyId());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getUserId(),
			newCPInstanceUnitOfMeasure.getUserId());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getUserName(),
			newCPInstanceUnitOfMeasure.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPInstanceUnitOfMeasure.getCreateDate()),
			Time.getShortTimestamp(newCPInstanceUnitOfMeasure.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPInstanceUnitOfMeasure.getModifiedDate()),
			Time.getShortTimestamp(
				newCPInstanceUnitOfMeasure.getModifiedDate()));
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getCPInstanceId(),
			newCPInstanceUnitOfMeasure.getCPInstanceId());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.isActive(),
			newCPInstanceUnitOfMeasure.isActive());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getIncrementalOrderQuantity(),
			newCPInstanceUnitOfMeasure.getIncrementalOrderQuantity());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getKey(),
			newCPInstanceUnitOfMeasure.getKey());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getName(),
			newCPInstanceUnitOfMeasure.getName());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getPrecision(),
			newCPInstanceUnitOfMeasure.getPrecision());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getPricingQuantity(),
			newCPInstanceUnitOfMeasure.getPricingQuantity());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.isPrimary(),
			newCPInstanceUnitOfMeasure.isPrimary());
		AssertUtils.assertEquals(
			existingCPInstanceUnitOfMeasure.getPriority(),
			newCPInstanceUnitOfMeasure.getPriority());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getRate(),
			newCPInstanceUnitOfMeasure.getRate());
		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure.getSku(),
			newCPInstanceUnitOfMeasure.getSku());
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
	public void testCountByCPInstanceId() throws Exception {
		_persistence.countByCPInstanceId(RandomTestUtil.nextLong());

		_persistence.countByCPInstanceId(0L);
	}

	@Test
	public void testCountByC_S() throws Exception {
		_persistence.countByC_S(RandomTestUtil.nextLong(), "");

		_persistence.countByC_S(0L, "null");

		_persistence.countByC_S(0L, (String)null);
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_K_S() throws Exception {
		_persistence.countByC_K_S(RandomTestUtil.nextLong(), "", "");

		_persistence.countByC_K_S(0L, "null", "null");

		_persistence.countByC_K_S(0L, (String)null, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			addCPInstanceUnitOfMeasure();

		CPInstanceUnitOfMeasure existingCPInstanceUnitOfMeasure =
			_persistence.findByPrimaryKey(
				newCPInstanceUnitOfMeasure.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure, newCPInstanceUnitOfMeasure);
	}

	@Test(expected = NoSuchCPInstanceUnitOfMeasureException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPInstanceUnitOfMeasure>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPInstanceUOM", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "CPInstanceUnitOfMeasureId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "CPInstanceId", true, "active", true,
			"incrementalOrderQuantity", true, "key", true, "name", true,
			"precision", true, "pricingQuantity", true, "primary", true,
			"priority", true, "rate", true, "sku", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			addCPInstanceUnitOfMeasure();

		CPInstanceUnitOfMeasure existingCPInstanceUnitOfMeasure =
			_persistence.fetchByPrimaryKey(
				newCPInstanceUnitOfMeasure.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstanceUnitOfMeasure, newCPInstanceUnitOfMeasure);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstanceUnitOfMeasure missingCPInstanceUnitOfMeasure =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPInstanceUnitOfMeasure);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure1 =
			addCPInstanceUnitOfMeasure();
		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure2 =
			addCPInstanceUnitOfMeasure();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstanceUnitOfMeasure1.getPrimaryKey());
		primaryKeys.add(newCPInstanceUnitOfMeasure2.getPrimaryKey());

		Map<Serializable, CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpInstanceUnitOfMeasures.size());
		Assert.assertEquals(
			newCPInstanceUnitOfMeasure1,
			cpInstanceUnitOfMeasures.get(
				newCPInstanceUnitOfMeasure1.getPrimaryKey()));
		Assert.assertEquals(
			newCPInstanceUnitOfMeasure2,
			cpInstanceUnitOfMeasures.get(
				newCPInstanceUnitOfMeasure2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpInstanceUnitOfMeasures.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			addCPInstanceUnitOfMeasure();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstanceUnitOfMeasure.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpInstanceUnitOfMeasures.size());
		Assert.assertEquals(
			newCPInstanceUnitOfMeasure,
			cpInstanceUnitOfMeasures.get(
				newCPInstanceUnitOfMeasure.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpInstanceUnitOfMeasures.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			addCPInstanceUnitOfMeasure();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstanceUnitOfMeasure.getPrimaryKey());

		Map<Serializable, CPInstanceUnitOfMeasure> cpInstanceUnitOfMeasures =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpInstanceUnitOfMeasures.size());
		Assert.assertEquals(
			newCPInstanceUnitOfMeasure,
			cpInstanceUnitOfMeasures.get(
				newCPInstanceUnitOfMeasure.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPInstanceUnitOfMeasure newCPInstanceUnitOfMeasure =
			addCPInstanceUnitOfMeasure();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPInstanceUnitOfMeasure.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure) {

		Assert.assertEquals(
			Long.valueOf(cpInstanceUnitOfMeasure.getCPInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceUnitOfMeasure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPInstanceId"));
		Assert.assertEquals(
			cpInstanceUnitOfMeasure.getKey(),
			ReflectionTestUtil.invoke(
				cpInstanceUnitOfMeasure, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
	}

	protected CPInstanceUnitOfMeasure addCPInstanceUnitOfMeasure()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPInstanceUnitOfMeasure cpInstanceUnitOfMeasure = _persistence.create(
			pk);

		cpInstanceUnitOfMeasure.setMvccVersion(RandomTestUtil.nextLong());

		cpInstanceUnitOfMeasure.setCtCollectionId(RandomTestUtil.nextLong());

		cpInstanceUnitOfMeasure.setUuid(RandomTestUtil.randomString());

		cpInstanceUnitOfMeasure.setCompanyId(RandomTestUtil.nextLong());

		cpInstanceUnitOfMeasure.setUserId(RandomTestUtil.nextLong());

		cpInstanceUnitOfMeasure.setUserName(RandomTestUtil.randomString());

		cpInstanceUnitOfMeasure.setCreateDate(RandomTestUtil.nextDate());

		cpInstanceUnitOfMeasure.setModifiedDate(RandomTestUtil.nextDate());

		cpInstanceUnitOfMeasure.setCPInstanceId(RandomTestUtil.nextLong());

		cpInstanceUnitOfMeasure.setActive(RandomTestUtil.randomBoolean());

		cpInstanceUnitOfMeasure.setIncrementalOrderQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpInstanceUnitOfMeasure.setKey(RandomTestUtil.randomString());

		cpInstanceUnitOfMeasure.setName(RandomTestUtil.randomString());

		cpInstanceUnitOfMeasure.setPrecision(RandomTestUtil.nextInt());

		cpInstanceUnitOfMeasure.setPricingQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpInstanceUnitOfMeasure.setPrimary(RandomTestUtil.randomBoolean());

		cpInstanceUnitOfMeasure.setPriority(RandomTestUtil.nextDouble());

		cpInstanceUnitOfMeasure.setRate(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpInstanceUnitOfMeasure.setSku(RandomTestUtil.randomString());

		_cpInstanceUnitOfMeasures.add(
			_persistence.update(cpInstanceUnitOfMeasure));

		return cpInstanceUnitOfMeasure;
	}

	private List<CPInstanceUnitOfMeasure> _cpInstanceUnitOfMeasures =
		new ArrayList<CPInstanceUnitOfMeasure>();
	private CPInstanceUnitOfMeasurePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}