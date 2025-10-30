/**
 * SPDX-FileCopyrightText: (c) 2024 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.DuplicateCPConfigurationListExternalReferenceCodeException;
import com.liferay.commerce.product.exception.NoSuchCPConfigurationListException;
import com.liferay.commerce.product.model.CPConfigurationList;
import com.liferay.commerce.product.service.persistence.CPConfigurationListPersistence;
import com.liferay.commerce.product.service.persistence.CPConfigurationListUtil;
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
public class CPConfigurationListPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CPConfigurationListUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CPConfigurationList> iterator =
			_cpConfigurationLists.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationList cpConfigurationList = _persistence.create(pk);

		Assert.assertNotNull(cpConfigurationList);

		Assert.assertEquals(cpConfigurationList.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		_persistence.remove(newCPConfigurationList);

		CPConfigurationList existingCPConfigurationList =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationList.getPrimaryKey());

		Assert.assertNull(existingCPConfigurationList);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCPConfigurationList();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationList newCPConfigurationList = _persistence.create(pk);

		newCPConfigurationList.setMvccVersion(RandomTestUtil.nextLong());

		newCPConfigurationList.setCtCollectionId(RandomTestUtil.nextLong());

		newCPConfigurationList.setUuid(RandomTestUtil.randomString());

		newCPConfigurationList.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCPConfigurationList.setGroupId(RandomTestUtil.nextLong());

		newCPConfigurationList.setCompanyId(RandomTestUtil.nextLong());

		newCPConfigurationList.setUserId(RandomTestUtil.nextLong());

		newCPConfigurationList.setUserName(RandomTestUtil.randomString());

		newCPConfigurationList.setCreateDate(RandomTestUtil.nextDate());

		newCPConfigurationList.setModifiedDate(RandomTestUtil.nextDate());

		newCPConfigurationList.setParentCPConfigurationListId(
			RandomTestUtil.nextLong());

		newCPConfigurationList.setMaster(RandomTestUtil.randomBoolean());

		newCPConfigurationList.setName(RandomTestUtil.randomString());

		newCPConfigurationList.setPriority(RandomTestUtil.nextDouble());

		newCPConfigurationList.setDisplayDate(RandomTestUtil.nextDate());

		newCPConfigurationList.setExpirationDate(RandomTestUtil.nextDate());

		newCPConfigurationList.setLastPublishDate(RandomTestUtil.nextDate());

		newCPConfigurationList.setStatus(RandomTestUtil.nextInt());

		newCPConfigurationList.setStatusByUserId(RandomTestUtil.nextLong());

		newCPConfigurationList.setStatusByUserName(
			RandomTestUtil.randomString());

		newCPConfigurationList.setStatusDate(RandomTestUtil.nextDate());

		_cpConfigurationLists.add(_persistence.update(newCPConfigurationList));

		CPConfigurationList existingCPConfigurationList =
			_persistence.findByPrimaryKey(
				newCPConfigurationList.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationList.getMvccVersion(),
			newCPConfigurationList.getMvccVersion());
		Assert.assertEquals(
			existingCPConfigurationList.getCtCollectionId(),
			newCPConfigurationList.getCtCollectionId());
		Assert.assertEquals(
			existingCPConfigurationList.getUuid(),
			newCPConfigurationList.getUuid());
		Assert.assertEquals(
			existingCPConfigurationList.getExternalReferenceCode(),
			newCPConfigurationList.getExternalReferenceCode());
		Assert.assertEquals(
			existingCPConfigurationList.getCPConfigurationListId(),
			newCPConfigurationList.getCPConfigurationListId());
		Assert.assertEquals(
			existingCPConfigurationList.getGroupId(),
			newCPConfigurationList.getGroupId());
		Assert.assertEquals(
			existingCPConfigurationList.getCompanyId(),
			newCPConfigurationList.getCompanyId());
		Assert.assertEquals(
			existingCPConfigurationList.getUserId(),
			newCPConfigurationList.getUserId());
		Assert.assertEquals(
			existingCPConfigurationList.getUserName(),
			newCPConfigurationList.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPConfigurationList.getCreateDate()),
			Time.getShortTimestamp(newCPConfigurationList.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationList.getModifiedDate()),
			Time.getShortTimestamp(newCPConfigurationList.getModifiedDate()));
		Assert.assertEquals(
			existingCPConfigurationList.getParentCPConfigurationListId(),
			newCPConfigurationList.getParentCPConfigurationListId());
		Assert.assertEquals(
			existingCPConfigurationList.isMaster(),
			newCPConfigurationList.isMaster());
		Assert.assertEquals(
			existingCPConfigurationList.getName(),
			newCPConfigurationList.getName());
		AssertUtils.assertEquals(
			existingCPConfigurationList.getPriority(),
			newCPConfigurationList.getPriority());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationList.getDisplayDate()),
			Time.getShortTimestamp(newCPConfigurationList.getDisplayDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationList.getExpirationDate()),
			Time.getShortTimestamp(newCPConfigurationList.getExpirationDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCPConfigurationList.getLastPublishDate()),
			Time.getShortTimestamp(
				newCPConfigurationList.getLastPublishDate()));
		Assert.assertEquals(
			existingCPConfigurationList.getStatus(),
			newCPConfigurationList.getStatus());
		Assert.assertEquals(
			existingCPConfigurationList.getStatusByUserId(),
			newCPConfigurationList.getStatusByUserId());
		Assert.assertEquals(
			existingCPConfigurationList.getStatusByUserName(),
			newCPConfigurationList.getStatusByUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCPConfigurationList.getStatusDate()),
			Time.getShortTimestamp(newCPConfigurationList.getStatusDate()));
	}

	@Test(
		expected = DuplicateCPConfigurationListExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CPConfigurationList cpConfigurationList = addCPConfigurationList();

		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		newCPConfigurationList.setCompanyId(cpConfigurationList.getCompanyId());

		newCPConfigurationList = _persistence.update(newCPConfigurationList);

		Session session = _persistence.getCurrentSession();

		session.evict(newCPConfigurationList);

		newCPConfigurationList.setExternalReferenceCode(
			cpConfigurationList.getExternalReferenceCode());

		_persistence.update(newCPConfigurationList);
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
	public void testCountByCompanyId() throws Exception {
		_persistence.countByCompanyId(RandomTestUtil.nextLong());

		_persistence.countByCompanyId(0L);
	}

	@Test
	public void testCountByParentCPConfigurationListId() throws Exception {
		_persistence.countByParentCPConfigurationListId(
			RandomTestUtil.nextLong());

		_persistence.countByParentCPConfigurationListId(0L);
	}

	@Test
	public void testCountByG_C() throws Exception {
		_persistence.countByG_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByG_C(0L, 0L);
	}

	@Test
	public void testCountByG_CArrayable() throws Exception {
		_persistence.countByG_C(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong());
	}

	@Test
	public void testCountByG_M() throws Exception {
		_persistence.countByG_M(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_M(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByLtD_S() throws Exception {
		_persistence.countByLtD_S(
			RandomTestUtil.nextDate(), RandomTestUtil.nextInt());

		_persistence.countByLtD_S(RandomTestUtil.nextDate(), 0);
	}

	@Test
	public void testCountByG_C_S() throws Exception {
		_persistence.countByG_C_S(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_S(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_SArrayable() throws Exception {
		_persistence.countByG_C_S(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByG_C_NotS() throws Exception {
		_persistence.countByG_C_NotS(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByG_C_NotS(0L, 0L, 0);
	}

	@Test
	public void testCountByG_C_NotSArrayable() throws Exception {
		_persistence.countByG_C_NotS(
			new long[] {RandomTestUtil.nextLong(), 0L},
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		CPConfigurationList existingCPConfigurationList =
			_persistence.findByPrimaryKey(
				newCPConfigurationList.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationList, newCPConfigurationList);
	}

	@Test(expected = NoSuchCPConfigurationListException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CPConfigurationList> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CPConfigurationList", "mvccVersion", true, "ctCollectionId", true,
			"uuid", true, "externalReferenceCode", true,
			"CPConfigurationListId", true, "groupId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "parentCPConfigurationListId", true, "master",
			true, "name", true, "priority", true, "displayDate", true,
			"expirationDate", true, "lastPublishDate", true, "status", true,
			"statusByUserId", true, "statusByUserName", true, "statusDate",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		CPConfigurationList existingCPConfigurationList =
			_persistence.fetchByPrimaryKey(
				newCPConfigurationList.getPrimaryKey());

		Assert.assertEquals(
			existingCPConfigurationList, newCPConfigurationList);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationList missingCPConfigurationList =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCPConfigurationList);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CPConfigurationList newCPConfigurationList1 = addCPConfigurationList();
		CPConfigurationList newCPConfigurationList2 = addCPConfigurationList();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationList1.getPrimaryKey());
		primaryKeys.add(newCPConfigurationList2.getPrimaryKey());

		Map<Serializable, CPConfigurationList> cpConfigurationLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, cpConfigurationLists.size());
		Assert.assertEquals(
			newCPConfigurationList1,
			cpConfigurationLists.get(newCPConfigurationList1.getPrimaryKey()));
		Assert.assertEquals(
			newCPConfigurationList2,
			cpConfigurationLists.get(newCPConfigurationList2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CPConfigurationList> cpConfigurationLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpConfigurationLists.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationList.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CPConfigurationList> cpConfigurationLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpConfigurationLists.size());
		Assert.assertEquals(
			newCPConfigurationList,
			cpConfigurationLists.get(newCPConfigurationList.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CPConfigurationList> cpConfigurationLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(cpConfigurationLists.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCPConfigurationList.getPrimaryKey());

		Map<Serializable, CPConfigurationList> cpConfigurationLists =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, cpConfigurationLists.size());
		Assert.assertEquals(
			newCPConfigurationList,
			cpConfigurationLists.get(newCPConfigurationList.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CPConfigurationList newCPConfigurationList = addCPConfigurationList();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCPConfigurationList.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CPConfigurationList cpConfigurationList) {

		Assert.assertEquals(
			cpConfigurationList.getUuid(),
			ReflectionTestUtil.invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "uuid_"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationList.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));

		Assert.assertEquals(
			Long.valueOf(cpConfigurationList.getParentCPConfigurationListId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "parentCPConfigurationListId"));

		Assert.assertEquals(
			Long.valueOf(cpConfigurationList.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			Boolean.valueOf(cpConfigurationList.getMaster()),
			ReflectionTestUtil.<Boolean>invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "master"));

		Assert.assertEquals(
			cpConfigurationList.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(cpConfigurationList.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				cpConfigurationList, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CPConfigurationList addCPConfigurationList() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CPConfigurationList cpConfigurationList = _persistence.create(pk);

		cpConfigurationList.setMvccVersion(RandomTestUtil.nextLong());

		cpConfigurationList.setCtCollectionId(RandomTestUtil.nextLong());

		cpConfigurationList.setUuid(RandomTestUtil.randomString());

		cpConfigurationList.setExternalReferenceCode(
			RandomTestUtil.randomString());

		cpConfigurationList.setGroupId(RandomTestUtil.nextLong());

		cpConfigurationList.setCompanyId(RandomTestUtil.nextLong());

		cpConfigurationList.setUserId(RandomTestUtil.nextLong());

		cpConfigurationList.setUserName(RandomTestUtil.randomString());

		cpConfigurationList.setCreateDate(RandomTestUtil.nextDate());

		cpConfigurationList.setModifiedDate(RandomTestUtil.nextDate());

		cpConfigurationList.setParentCPConfigurationListId(
			RandomTestUtil.nextLong());

		cpConfigurationList.setMaster(RandomTestUtil.randomBoolean());

		cpConfigurationList.setName(RandomTestUtil.randomString());

		cpConfigurationList.setPriority(RandomTestUtil.nextDouble());

		cpConfigurationList.setDisplayDate(RandomTestUtil.nextDate());

		cpConfigurationList.setExpirationDate(RandomTestUtil.nextDate());

		cpConfigurationList.setLastPublishDate(RandomTestUtil.nextDate());

		cpConfigurationList.setStatus(RandomTestUtil.nextInt());

		cpConfigurationList.setStatusByUserId(RandomTestUtil.nextLong());

		cpConfigurationList.setStatusByUserName(RandomTestUtil.randomString());

		cpConfigurationList.setStatusDate(RandomTestUtil.nextDate());

		_cpConfigurationLists.add(_persistence.update(cpConfigurationList));

		return cpConfigurationList;
	}

	private List<CPConfigurationList> _cpConfigurationLists =
		new ArrayList<CPConfigurationList>();
	private CPConfigurationListPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}