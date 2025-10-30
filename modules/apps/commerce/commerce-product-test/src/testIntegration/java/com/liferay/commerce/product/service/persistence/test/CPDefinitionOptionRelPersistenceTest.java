/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPDefinitionOptionRelException;
import com.liferay.commerce.product.model.CPDefinitionOptionRel;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelPersistence;
import com.liferay.commerce.product.service.persistence.CPDefinitionOptionRelUtil;
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
public class CPDefinitionOptionRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPDefinitionOptionRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPDefinitionOptionRel> iterator =
			_cpDefinitionOptionRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionRel cpDefinitionOptionRel = _persistence.create(pk);

		Assert.assertNotNull(cpDefinitionOptionRel);

		Assert.assertEquals(cpDefinitionOptionRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPDefinitionOptionRel newCPDefinitionOptionRel =
			addCPDefinitionOptionRel();

		_persistence.remove(newCPDefinitionOptionRel);

		CPDefinitionOptionRel existingCPDefinitionOptionRel =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionOptionRel.getPrimaryKey());

		Assert.assertNull(existingCPDefinitionOptionRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPDefinitionOptionRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionRel newCPDefinitionOptionRel = _persistence.create(
			pk);

		newCPDefinitionOptionRel.setMvccVersion(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setCtCollectionId(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setUuid(RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setGroupId(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setCompanyId(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setUserId(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setUserName(RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setCreateDate(RandomTestUtil.nextDate());

		newCPDefinitionOptionRel.setModifiedDate(RandomTestUtil.nextDate());

		newCPDefinitionOptionRel.setCPDefinitionId(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setCPOptionId(RandomTestUtil.nextLong());

		newCPDefinitionOptionRel.setName(RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setDescription(RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setCommerceOptionTypeKey(
			RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setInfoItemServiceKey(
			RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setPriority(RandomTestUtil.nextDouble());

		newCPDefinitionOptionRel.setDefinedExternally(
			RandomTestUtil.randomBoolean());

		newCPDefinitionOptionRel.setFacetable(RandomTestUtil.randomBoolean());

		newCPDefinitionOptionRel.setRequired(RandomTestUtil.randomBoolean());

		newCPDefinitionOptionRel.setSkuContributor(
			RandomTestUtil.randomBoolean());

		newCPDefinitionOptionRel.setKey(RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setPriceType(RandomTestUtil.randomString());

		newCPDefinitionOptionRel.setTypeSettings(RandomTestUtil.randomString());

		_cpDefinitionOptionRels.add(
			_persistence.update(newCPDefinitionOptionRel));

		CPDefinitionOptionRel existingCPDefinitionOptionRel =
			_persistence.findByPrimaryKey(
				newCPDefinitionOptionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionOptionRel.getMvccVersion(),
			newCPDefinitionOptionRel.getMvccVersion());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getCtCollectionId(),
			newCPDefinitionOptionRel.getCtCollectionId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getUuid(),
			newCPDefinitionOptionRel.getUuid());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getCPDefinitionOptionRelId(),
			newCPDefinitionOptionRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getGroupId(),
			newCPDefinitionOptionRel.getGroupId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getCompanyId(),
			newCPDefinitionOptionRel.getCompanyId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getUserId(),
			newCPDefinitionOptionRel.getUserId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getUserName(),
			newCPDefinitionOptionRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionOptionRel.getCreateDate()),
			Time.getShortTimestamp(newCPDefinitionOptionRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPDefinitionOptionRel.getModifiedDate()),
			Time.getShortTimestamp(newCPDefinitionOptionRel.getModifiedDate()));
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getCPDefinitionId(),
			newCPDefinitionOptionRel.getCPDefinitionId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getCPOptionId(),
			newCPDefinitionOptionRel.getCPOptionId());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getName(),
			newCPDefinitionOptionRel.getName());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getDescription(),
			newCPDefinitionOptionRel.getDescription());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getCommerceOptionTypeKey(),
			newCPDefinitionOptionRel.getCommerceOptionTypeKey());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getInfoItemServiceKey(),
			newCPDefinitionOptionRel.getInfoItemServiceKey());
		AssertUtils.assertEquals(
			existingCPDefinitionOptionRel.getPriority(),
			newCPDefinitionOptionRel.getPriority());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.isDefinedExternally(),
			newCPDefinitionOptionRel.isDefinedExternally());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.isFacetable(),
			newCPDefinitionOptionRel.isFacetable());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.isRequired(),
			newCPDefinitionOptionRel.isRequired());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.isSkuContributor(),
			newCPDefinitionOptionRel.isSkuContributor());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getKey(),
			newCPDefinitionOptionRel.getKey());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getPriceType(),
			newCPDefinitionOptionRel.getPriceType());
		Assert.assertEquals(
			existingCPDefinitionOptionRel.getTypeSettings(),
			newCPDefinitionOptionRel.getTypeSettings());
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
	public void testCountByCPDefinitionId() throws Exception {
		_persistence.countByCPDefinitionId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionId(0L);
	}

	@Test
	public void testCountByCPOptionId() throws Exception {
		_persistence.countByCPOptionId(RandomTestUtil.nextLong());

		_persistence.countByCPOptionId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByCPDI_R() throws Exception {
		_persistence.countByCPDI_R(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByCPDI_R(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_SC() throws Exception {
		_persistence.countByC_SC(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_SC(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPDefinitionOptionRel newCPDefinitionOptionRel =
			addCPDefinitionOptionRel();

		CPDefinitionOptionRel existingCPDefinitionOptionRel =
			_persistence.findByPrimaryKey(
				newCPDefinitionOptionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionOptionRel, newCPDefinitionOptionRel);
	}

	@Test(expected = NoSuchCPDefinitionOptionRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPDefinitionOptionRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPDefinitionOptionRel", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "CPDefinitionOptionRelId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "CPDefinitionId", true,
			"CPOptionId", true, "name", true, "description", true,
			"commerceOptionTypeKey", true, "infoItemServiceKey", true,
			"priority", true, "definedExternally", true, "facetable", true,
			"required", true, "skuContributor", true, "key", true, "priceType",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPDefinitionOptionRel newCPDefinitionOptionRel =
			addCPDefinitionOptionRel();

		CPDefinitionOptionRel existingCPDefinitionOptionRel =
			_persistence.fetchByPrimaryKey(
				newCPDefinitionOptionRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPDefinitionOptionRel, newCPDefinitionOptionRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionRel missingCPDefinitionOptionRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPDefinitionOptionRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPDefinitionOptionRel newCPDefinitionOptionRel1 =
			addCPDefinitionOptionRel();
		CPDefinitionOptionRel newCPDefinitionOptionRel2 =
			addCPDefinitionOptionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionOptionRel1.getPrimaryKey());
		primaryKeys.add(newCPDefinitionOptionRel2.getPrimaryKey());

		Map<Serializable, CPDefinitionOptionRel> cpDefinitionOptionRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpDefinitionOptionRels.size());
		Assert.assertEquals(
			newCPDefinitionOptionRel1,
			cpDefinitionOptionRels.get(
				newCPDefinitionOptionRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCPDefinitionOptionRel2,
			cpDefinitionOptionRels.get(
				newCPDefinitionOptionRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPDefinitionOptionRel> cpDefinitionOptionRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionOptionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPDefinitionOptionRel newCPDefinitionOptionRel =
			addCPDefinitionOptionRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionOptionRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPDefinitionOptionRel> cpDefinitionOptionRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionOptionRels.size());
		Assert.assertEquals(
			newCPDefinitionOptionRel,
			cpDefinitionOptionRels.get(
				newCPDefinitionOptionRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPDefinitionOptionRel> cpDefinitionOptionRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpDefinitionOptionRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPDefinitionOptionRel newCPDefinitionOptionRel =
			addCPDefinitionOptionRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPDefinitionOptionRel.getPrimaryKey());

		Map<Serializable, CPDefinitionOptionRel> cpDefinitionOptionRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpDefinitionOptionRels.size());
		Assert.assertEquals(
			newCPDefinitionOptionRel,
			cpDefinitionOptionRels.get(
				newCPDefinitionOptionRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPDefinitionOptionRel newCPDefinitionOptionRel =
			addCPDefinitionOptionRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPDefinitionOptionRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPDefinitionOptionRel cpDefinitionOptionRel) {

		Assert.assertEquals(
			cpDefinitionOptionRel.getUuid(),
			ReflectionTestUtil.invoke(
				cpDefinitionOptionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionOptionRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionOptionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpDefinitionOptionRel.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionOptionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			Long.valueOf(cpDefinitionOptionRel.getCPOptionId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionOptionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPOptionId"));

		Assert.assertEquals(
			Long.valueOf(cpDefinitionOptionRel.getCPDefinitionId()),
			ReflectionTestUtil.<Long>invoke(
				cpDefinitionOptionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionId"));
		Assert.assertEquals(
			cpDefinitionOptionRel.getKey(),
			ReflectionTestUtil.invoke(
				cpDefinitionOptionRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
	}

	protected CPDefinitionOptionRel addCPDefinitionOptionRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPDefinitionOptionRel cpDefinitionOptionRel = _persistence.create(pk);

		cpDefinitionOptionRel.setMvccVersion(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setCtCollectionId(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setUuid(RandomTestUtil.randomString());

		cpDefinitionOptionRel.setGroupId(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setCompanyId(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setUserId(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setUserName(RandomTestUtil.randomString());

		cpDefinitionOptionRel.setCreateDate(RandomTestUtil.nextDate());

		cpDefinitionOptionRel.setModifiedDate(RandomTestUtil.nextDate());

		cpDefinitionOptionRel.setCPDefinitionId(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setCPOptionId(RandomTestUtil.nextLong());

		cpDefinitionOptionRel.setName(RandomTestUtil.randomString());

		cpDefinitionOptionRel.setDescription(RandomTestUtil.randomString());

		cpDefinitionOptionRel.setCommerceOptionTypeKey(
			RandomTestUtil.randomString());

		cpDefinitionOptionRel.setInfoItemServiceKey(
			RandomTestUtil.randomString());

		cpDefinitionOptionRel.setPriority(RandomTestUtil.nextDouble());

		cpDefinitionOptionRel.setDefinedExternally(
			RandomTestUtil.randomBoolean());

		cpDefinitionOptionRel.setFacetable(RandomTestUtil.randomBoolean());

		cpDefinitionOptionRel.setRequired(RandomTestUtil.randomBoolean());

		cpDefinitionOptionRel.setSkuContributor(RandomTestUtil.randomBoolean());

		cpDefinitionOptionRel.setKey(RandomTestUtil.randomString());

		cpDefinitionOptionRel.setPriceType(RandomTestUtil.randomString());

		cpDefinitionOptionRel.setTypeSettings(RandomTestUtil.randomString());

		_cpDefinitionOptionRels.add(_persistence.update(cpDefinitionOptionRel));

		return cpDefinitionOptionRel;
	}

	private List<CPDefinitionOptionRel> _cpDefinitionOptionRels =
		new ArrayList<CPDefinitionOptionRel>();
	private CPDefinitionOptionRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}