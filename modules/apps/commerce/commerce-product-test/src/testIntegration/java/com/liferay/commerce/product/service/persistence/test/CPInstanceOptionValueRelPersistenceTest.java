/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchCPInstanceOptionValueRelException;
import com.liferay.commerce.product.model.CPInstanceOptionValueRel;
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelPersistence;
import com.liferay.commerce.product.service.persistence.CPInstanceOptionValueRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CPInstanceOptionValueRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPInstanceOptionValueRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPInstanceOptionValueRel> iterator =
			_cpInstanceOptionValueRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstanceOptionValueRel cpInstanceOptionValueRel = _persistence.create(
			pk);

		Assert.assertNotNull(cpInstanceOptionValueRel);

		Assert.assertEquals(cpInstanceOptionValueRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			addCPInstanceOptionValueRel();

		_persistence.remove(newCPInstanceOptionValueRel);

		CPInstanceOptionValueRel existingCPInstanceOptionValueRel =
			_persistence.fetchByPrimaryKey(
				newCPInstanceOptionValueRel.getPrimaryKey());

		Assert.assertNull(existingCPInstanceOptionValueRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPInstanceOptionValueRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			_persistence.create(pk);

		newCPInstanceOptionValueRel.setMvccVersion(RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setUuid(RandomTestUtil.randomString());

		newCPInstanceOptionValueRel.setGroupId(RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setCompanyId(RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setUserId(RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setUserName(RandomTestUtil.randomString());

		newCPInstanceOptionValueRel.setCreateDate(RandomTestUtil.nextDate());

		newCPInstanceOptionValueRel.setModifiedDate(RandomTestUtil.nextDate());

		newCPInstanceOptionValueRel.setCPDefinitionOptionRelId(
			RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setCPDefinitionOptionValueRelId(
			RandomTestUtil.nextLong());

		newCPInstanceOptionValueRel.setCPInstanceId(RandomTestUtil.nextLong());

		_cpInstanceOptionValueRels.add(
			_persistence.update(newCPInstanceOptionValueRel));

		CPInstanceOptionValueRel existingCPInstanceOptionValueRel =
			_persistence.findByPrimaryKey(
				newCPInstanceOptionValueRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getMvccVersion(),
			newCPInstanceOptionValueRel.getMvccVersion());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getCtCollectionId(),
			newCPInstanceOptionValueRel.getCtCollectionId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getUuid(),
			newCPInstanceOptionValueRel.getUuid());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getCPInstanceOptionValueRelId(),
			newCPInstanceOptionValueRel.getCPInstanceOptionValueRelId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getGroupId(),
			newCPInstanceOptionValueRel.getGroupId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getCompanyId(),
			newCPInstanceOptionValueRel.getCompanyId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getUserId(),
			newCPInstanceOptionValueRel.getUserId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getUserName(),
			newCPInstanceOptionValueRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPInstanceOptionValueRel.getCreateDate()),
			Time.getShortTimestamp(
				newCPInstanceOptionValueRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPInstanceOptionValueRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCPInstanceOptionValueRel.getModifiedDate()));
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getCPDefinitionOptionRelId(),
			newCPInstanceOptionValueRel.getCPDefinitionOptionRelId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getCPDefinitionOptionValueRelId(),
			newCPInstanceOptionValueRel.getCPDefinitionOptionValueRelId());
		Assert.assertEquals(
			existingCPInstanceOptionValueRel.getCPInstanceId(),
			newCPInstanceOptionValueRel.getCPInstanceId());
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
	public void testCountByCPDefinitionOptionRelId() throws Exception {
		_persistence.countByCPDefinitionOptionRelId(RandomTestUtil.nextLong());

		_persistence.countByCPDefinitionOptionRelId(0L);
	}

	@Test
	public void testCountByCPInstanceId() throws Exception {
		_persistence.countByCPInstanceId(RandomTestUtil.nextLong());

		_persistence.countByCPInstanceId(0L);
	}

	@Test
	public void testCountByCDORI_CII() throws Exception {
		_persistence.countByCDORI_CII(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCDORI_CII(0L, 0L);
	}

	@Test
	public void testCountByCDOVRI_CII() throws Exception {
		_persistence.countByCDOVRI_CII(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByCDOVRI_CII(0L, 0L);
	}

	@Test
	public void testCountByCDORI_CDOVRI_CII() throws Exception {
		_persistence.countByCDORI_CDOVRI_CII(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByCDORI_CDOVRI_CII(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			addCPInstanceOptionValueRel();

		CPInstanceOptionValueRel existingCPInstanceOptionValueRel =
			_persistence.findByPrimaryKey(
				newCPInstanceOptionValueRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstanceOptionValueRel, newCPInstanceOptionValueRel);
	}

	@Test(expected = NoSuchCPInstanceOptionValueRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPInstanceOptionValueRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPInstanceOptionValueRel", "mvccVersion", true, "ctCollectionId",
			true, "uuid", true, "CPInstanceOptionValueRelId", true, "groupId",
			true, "companyId", true, "userId", true, "userName", true,
			"createDate", true, "modifiedDate", true, "CPDefinitionOptionRelId",
			true, "CPDefinitionOptionValueRelId", true, "CPInstanceId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			addCPInstanceOptionValueRel();

		CPInstanceOptionValueRel existingCPInstanceOptionValueRel =
			_persistence.fetchByPrimaryKey(
				newCPInstanceOptionValueRel.getPrimaryKey());

		Assert.assertEquals(
			existingCPInstanceOptionValueRel, newCPInstanceOptionValueRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPInstanceOptionValueRel missingCPInstanceOptionValueRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPInstanceOptionValueRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPInstanceOptionValueRel newCPInstanceOptionValueRel1 =
			addCPInstanceOptionValueRel();
		CPInstanceOptionValueRel newCPInstanceOptionValueRel2 =
			addCPInstanceOptionValueRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstanceOptionValueRel1.getPrimaryKey());
		primaryKeys.add(newCPInstanceOptionValueRel2.getPrimaryKey());

		Map<Serializable, CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpInstanceOptionValueRels.size());
		Assert.assertEquals(
			newCPInstanceOptionValueRel1,
			cpInstanceOptionValueRels.get(
				newCPInstanceOptionValueRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCPInstanceOptionValueRel2,
			cpInstanceOptionValueRels.get(
				newCPInstanceOptionValueRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpInstanceOptionValueRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			addCPInstanceOptionValueRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstanceOptionValueRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpInstanceOptionValueRels.size());
		Assert.assertEquals(
			newCPInstanceOptionValueRel,
			cpInstanceOptionValueRels.get(
				newCPInstanceOptionValueRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpInstanceOptionValueRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			addCPInstanceOptionValueRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPInstanceOptionValueRel.getPrimaryKey());

		Map<Serializable, CPInstanceOptionValueRel> cpInstanceOptionValueRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpInstanceOptionValueRels.size());
		Assert.assertEquals(
			newCPInstanceOptionValueRel,
			cpInstanceOptionValueRels.get(
				newCPInstanceOptionValueRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPInstanceOptionValueRel newCPInstanceOptionValueRel =
			addCPInstanceOptionValueRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPInstanceOptionValueRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPInstanceOptionValueRel cpInstanceOptionValueRel) {

		Assert.assertEquals(
			cpInstanceOptionValueRel.getUuid(),
			ReflectionTestUtil.invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpInstanceOptionValueRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(
				cpInstanceOptionValueRel.getCPDefinitionOptionValueRelId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionOptionValueRelId"));
		Assert.assertEquals(
			Long.valueOf(cpInstanceOptionValueRel.getCPInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPInstanceId"));

		Assert.assertEquals(
			Long.valueOf(cpInstanceOptionValueRel.getCPDefinitionOptionRelId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionOptionRelId"));
		Assert.assertEquals(
			Long.valueOf(
				cpInstanceOptionValueRel.getCPDefinitionOptionValueRelId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPDefinitionOptionValueRelId"));
		Assert.assertEquals(
			Long.valueOf(cpInstanceOptionValueRel.getCPInstanceId()),
			ReflectionTestUtil.<Long>invoke(
				cpInstanceOptionValueRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "CPInstanceId"));
	}

	protected CPInstanceOptionValueRel addCPInstanceOptionValueRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CPInstanceOptionValueRel cpInstanceOptionValueRel = _persistence.create(
			pk);

		cpInstanceOptionValueRel.setMvccVersion(RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setCtCollectionId(RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setUuid(RandomTestUtil.randomString());

		cpInstanceOptionValueRel.setGroupId(RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setCompanyId(RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setUserId(RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setUserName(RandomTestUtil.randomString());

		cpInstanceOptionValueRel.setCreateDate(RandomTestUtil.nextDate());

		cpInstanceOptionValueRel.setModifiedDate(RandomTestUtil.nextDate());

		cpInstanceOptionValueRel.setCPDefinitionOptionRelId(
			RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setCPDefinitionOptionValueRelId(
			RandomTestUtil.nextLong());

		cpInstanceOptionValueRel.setCPInstanceId(RandomTestUtil.nextLong());

		_cpInstanceOptionValueRels.add(
			_persistence.update(cpInstanceOptionValueRel));

		return cpInstanceOptionValueRel;
	}

	private List<CPInstanceOptionValueRel> _cpInstanceOptionValueRels =
		new ArrayList<CPInstanceOptionValueRel>();
	private CPInstanceOptionValueRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}