/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionValueRelException;
import com.liferay.commerce.product.model.CPDefinitionOptionValueRel;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionValueRelUtil;
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
public class CPDefinitionOptionValueRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionOptionValueRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinitionOptionValueRel> iterator =
			_cpDefinitionOptionValueRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_persistence.create(pk);

		Assert.assertNotNull(cpDefinitionOptionValueRel);

		Assert.assertEquals(cpDefinitionOptionValueRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			addCPDefinitionOptionValueRel();

		_persistence.remove(newCPDefinitionOptionValueRel);

		CPDefinitionOptionValueRel existingCPDefinitionOptionValueRel =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionOptionValueRel.getPrimaryKey());

		Assert.assertNull(existingCPDefinitionOptionValueRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinitionOptionValueRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			_persistence.create(pk);

		newCPDefinitionOptionValueRel.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setUuid(RandomTestUtil.randomString());

		newCPDefinitionOptionValueRel.setGroupId(RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setUserId(RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setUserName(
			RandomTestUtil.randomString());

		newCPDefinitionOptionValueRel.setCreateDate(RandomTestUtil.nextDate());

		newCPDefinitionOptionValueRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCPDefinitionOptionValueRel.setCPDefinitionOptionRelId(
			RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setCPInstanceUuid(
			RandomTestUtil.randomString());

		newCPDefinitionOptionValueRel.setCProductId(RandomTestUtil.nextLong());

		newCPDefinitionOptionValueRel.setKey(RandomTestUtil.randomString());

		newCPDefinitionOptionValueRel.setName(RandomTestUtil.randomString());

		newCPDefinitionOptionValueRel.setPreselected(
			RandomTestUtil.randomBoolean());

		newCPDefinitionOptionValueRel.setPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPDefinitionOptionValueRel.setPriority(RandomTestUtil.nextDouble());

		newCPDefinitionOptionValueRel.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCPDefinitionOptionValueRel.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_cpDefinitionOptionValueRels.add(
			_persistence.update(newCPDefinitionOptionValueRel));

		CPDefinitionOptionValueRel existingCPDefinitionOptionValueRel =
			_persistence.findByPrimaryKey(
				newCPDefinitionOptionValueRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getMvccVersion(),
			newCPDefinitionOptionValueRel.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getCtCollectionId(),
			newCPDefinitionOptionValueRel.getCtCollectionId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getUuid(),
			newCPDefinitionOptionValueRel.getUuid());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.
				getCPDefinitionOptionValueRelId(),
			newCPDefinitionOptionValueRel.getCPDefinitionOptionValueRelId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getGroupId(),
			newCPDefinitionOptionValueRel.getGroupId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getCompanyId(),
			newCPDefinitionOptionValueRel.getCompanyId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getUserId(),
			newCPDefinitionOptionValueRel.getUserId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getUserName(),
			newCPDefinitionOptionValueRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionOptionValueRel.getCreateDate()),
			Time.getShortTimestamp(
				newCPDefinitionOptionValueRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionOptionValueRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCPDefinitionOptionValueRel.getModifiedDate()));
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getCPDefinitionOptionRelId(),
			newCPDefinitionOptionValueRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getCPInstanceUuid(),
			newCPDefinitionOptionValueRel.getCPInstanceUuid());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getCProductId(),
			newCPDefinitionOptionValueRel.getCProductId());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getKey(),
			newCPDefinitionOptionValueRel.getKey());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getName(),
			newCPDefinitionOptionValueRel.getName());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.isPreselected(),
			newCPDefinitionOptionValueRel.isPreselected());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getPrice(),
			newCPDefinitionOptionValueRel.getPrice());
		AssertUtils.assertEquals(
			existingCPDefinitionOptionValueRel.getPriority(),
			newCPDefinitionOptionValueRel.getPriority());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getQuantity(),
			newCPDefinitionOptionValueRel.getQuantity());
		Assert.assertEquals(
			existingCPDefinitionOptionValueRel.getUnitOfMeasureKey(),
			newCPDefinitionOptionValueRel.getUnitOfMeasureKey());
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByCPDefinitionOptionRelId() throws Exception {
		_persistence.countByCPDefinitionOptionRelId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionOptionRelId(0L);
	}

	@Test
	public void testCountByCPInstanceUuid() throws Exception {
		_persistence.countByCPInstanceUuid("");

		_persistence.countByCPInstanceUuid("null");

		_persistence.countByCPInstanceUuid((String)null);
	}

	@Test
	public void testCountByKey() throws Exception {
		_persistence.countByKey("");

		_persistence.countByKey("null");

		_persistence.countByKey((String)null);
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testCountByCDORI_P() throws Exception {
		_persistence.countByCDORI_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByCDORI_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			addCPDefinitionOptionValueRel();

		CPDefinitionOptionValueRel existingCPDefinitionOptionValueRel =
			_persistence.findByPrimaryKey(
				newCPDefinitionOptionValueRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionOptionValueRel, newCPDefinitionOptionValueRel);
	}

	@Test(expected = NoSuchCPDefinitionOptionValueRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDefinitionOptionValueRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPDefinitionOptionValueRel", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "CPDefinitionOptionValueRelId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "CPDefinitionOptionRelId",
			true, "CPInstanceUuid", true, "CProductId", true, "key", true,
			"name", true, "preselected", true, "price", true, "priority", true,
			"quantity", true, "unitOfMeasureKey", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			addCPDefinitionOptionValueRel();

		CPDefinitionOptionValueRel existingCPDefinitionOptionValueRel =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionOptionValueRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionOptionValueRel, newCPDefinitionOptionValueRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionValueRel missingCPDefinitionOptionValueRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinitionOptionValueRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel1 =
			addCPDefinitionOptionValueRel();
		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel2 =
			addCPDefinitionOptionValueRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionOptionValueRel1.getPrimaryKey());
		primaryKeys.add(newCPDefinitionOptionValueRel2.getPrimaryKey());

		Map<Serializable, CPDefinitionOptionValueRel>
			cpDefinitionOptionValueRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, cpDefinitionOptionValueRels.size());
		Assert.assertEquals(
			newCPDefinitionOptionValueRel1,
			cpDefinitionOptionValueRels.get(
				newCPDefinitionOptionValueRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinitionOptionValueRel2,
			cpDefinitionOptionValueRels.get(
				newCPDefinitionOptionValueRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinitionOptionValueRel>
			cpDefinitionOptionValueRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpDefinitionOptionValueRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			addCPDefinitionOptionValueRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionOptionValueRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinitionOptionValueRel>
			cpDefinitionOptionValueRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpDefinitionOptionValueRels.size());
		Assert.assertEquals(
			newCPDefinitionOptionValueRel,
			cpDefinitionOptionValueRels.get(
				newCPDefinitionOptionValueRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinitionOptionValueRel>
			cpDefinitionOptionValueRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(cpDefinitionOptionValueRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			addCPDefinitionOptionValueRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionOptionValueRel.getPrimaryKey());

		Map<Serializable, CPDefinitionOptionValueRel>
			cpDefinitionOptionValueRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, cpDefinitionOptionValueRels.size());
		Assert.assertEquals(
			newCPDefinitionOptionValueRel,
			cpDefinitionOptionValueRels.get(
				newCPDefinitionOptionValueRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinitionOptionValueRel newCPDefinitionOptionValueRel =
			addCPDefinitionOptionValueRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDefinitionOptionValueRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDefinitionOptionValueRel cpDefinitionOptionValueRel) {

		Assert.assertEquals(
			cpDefinitionOptionValueRel.getUuid(),
			ReflectionTestUtil.invoke(
				cpDefinitionOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionOptionValueRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(
				cpDefinitionOptionValueRel.getCPDefinitionOptionRelId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionOptionRelId"));
		Assert.assertEquals(
			cpDefinitionOptionValueRel.getKey(),
			ReflectionTestUtil.invoke(
				cpDefinitionOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
	}

	protected CPDefinitionOptionValueRel addCPDefinitionOptionValueRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionValueRel cpDefinitionOptionValueRel =
			_persistence.create(pk);

		cpDefinitionOptionValueRel.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setCtCollectionId(RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setUuid(RandomTestUtil.randomString());

		cpDefinitionOptionValueRel.setGroupId(RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setCompanyId(RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setUserId(RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setUserName(RandomTestUtil.randomString());

		cpDefinitionOptionValueRel.setCreateDate(RandomTestUtil.nextDate());

		cpDefinitionOptionValueRel.setModifiedDate(RandomTestUtil.nextDate());

		cpDefinitionOptionValueRel.setCPDefinitionOptionRelId(
			RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setCPInstanceUuid(
			RandomTestUtil.randomString());

		cpDefinitionOptionValueRel.setCProductId(RandomTestUtil.nextLong());

		cpDefinitionOptionValueRel.setKey(RandomTestUtil.randomString());

		cpDefinitionOptionValueRel.setName(RandomTestUtil.randomString());

		cpDefinitionOptionValueRel.setPreselected(
			RandomTestUtil.randomBoolean());

		cpDefinitionOptionValueRel.setPrice(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpDefinitionOptionValueRel.setPriority(RandomTestUtil.nextDouble());

		cpDefinitionOptionValueRel.setQuantity(
			new BigDecimal(RandomTestUtil.nextDouble()));

		cpDefinitionOptionValueRel.setUnitOfMeasureKey(
			RandomTestUtil.randomString());

		_cpDefinitionOptionValueRels.add(
			_persistence.update(cpDefinitionOptionValueRel));

		return cpDefinitionOptionValueRel;
	}

	private List<CPDefinitionOptionValueRel> _cpDefinitionOptionValueRels =
		new ArrayList<CPDefinitionOptionValueRel>();
	private CPDefinitionOptionValueRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}