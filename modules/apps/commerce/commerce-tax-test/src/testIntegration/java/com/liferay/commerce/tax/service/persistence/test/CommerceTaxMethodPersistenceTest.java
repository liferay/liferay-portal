/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.tax.exception.NoSuchTaxMethodException;
import com.liferay.commerce.tax.model.CommerceTaxMethod;
import com.liferay.commerce.tax.service.persistence.CommerceTaxMethodPersistence;
import com.liferay.commerce.tax.service.persistence.CommerceTaxMethodUtil;
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
public class CommerceTaxMethodPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.tax.service"));

	@Before
	public void setUp() {
		_persistence = CommerceTaxMethodUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTaxMethod> iterator = _commerceTaxMethods.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxMethod commerceTaxMethod = _persistence.create(pk);

		Assert.assertNotNull(commerceTaxMethod);

		Assert.assertEquals(commerceTaxMethod.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTaxMethod newCommerceTaxMethod = addCommerceTaxMethod();

		_persistence.remove(newCommerceTaxMethod);

		CommerceTaxMethod existingCommerceTaxMethod =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxMethod.getPrimaryKey());

		Assert.assertNull(existingCommerceTaxMethod);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTaxMethod();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxMethod newCommerceTaxMethod = _persistence.create(pk);

		newCommerceTaxMethod.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceTaxMethod.setGroupId(RandomTestUtil.nextLong());

		newCommerceTaxMethod.setCompanyId(RandomTestUtil.nextLong());

		newCommerceTaxMethod.setUserId(RandomTestUtil.nextLong());

		newCommerceTaxMethod.setUserName(RandomTestUtil.randomString());

		newCommerceTaxMethod.setCreateDate(RandomTestUtil.nextDate());

		newCommerceTaxMethod.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceTaxMethod.setName(RandomTestUtil.randomString());

		newCommerceTaxMethod.setDescription(RandomTestUtil.randomString());

		newCommerceTaxMethod.setEngineKey(RandomTestUtil.randomString());

		newCommerceTaxMethod.setPercentage(RandomTestUtil.randomBoolean());

		newCommerceTaxMethod.setActive(RandomTestUtil.randomBoolean());

		newCommerceTaxMethod.setTypeSettings(RandomTestUtil.randomString());

		_commerceTaxMethods.add(_persistence.update(newCommerceTaxMethod));

		CommerceTaxMethod existingCommerceTaxMethod =
			_persistence.findByPrimaryKey(newCommerceTaxMethod.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxMethod.getMvccVersion(),
			newCommerceTaxMethod.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTaxMethod.getCommerceTaxMethodId(),
			newCommerceTaxMethod.getCommerceTaxMethodId());
		Assert.assertEquals(
			existingCommerceTaxMethod.getGroupId(),
			newCommerceTaxMethod.getGroupId());
		Assert.assertEquals(
			existingCommerceTaxMethod.getCompanyId(),
			newCommerceTaxMethod.getCompanyId());
		Assert.assertEquals(
			existingCommerceTaxMethod.getUserId(),
			newCommerceTaxMethod.getUserId());
		Assert.assertEquals(
			existingCommerceTaxMethod.getUserName(),
			newCommerceTaxMethod.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceTaxMethod.getCreateDate()),
			Time.getShortTimestamp(newCommerceTaxMethod.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceTaxMethod.getModifiedDate()),
			Time.getShortTimestamp(newCommerceTaxMethod.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTaxMethod.getName(),
			newCommerceTaxMethod.getName());
		Assert.assertEquals(
			existingCommerceTaxMethod.getDescription(),
			newCommerceTaxMethod.getDescription());
		Assert.assertEquals(
			existingCommerceTaxMethod.getEngineKey(),
			newCommerceTaxMethod.getEngineKey());
		Assert.assertEquals(
			existingCommerceTaxMethod.isPercentage(),
			newCommerceTaxMethod.isPercentage());
		Assert.assertEquals(
			existingCommerceTaxMethod.isActive(),
			newCommerceTaxMethod.isActive());
		Assert.assertEquals(
			existingCommerceTaxMethod.getTypeSettings(),
			newCommerceTaxMethod.getTypeSettings());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_E() throws Exception {
		_persistence.countByG_E(RandomTestUtil.nextLong(), "");

		_persistence.countByG_E(0L, "null");

		_persistence.countByG_E(0L, (String)null);
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceTaxMethod newCommerceTaxMethod = addCommerceTaxMethod();

		CommerceTaxMethod existingCommerceTaxMethod =
			_persistence.findByPrimaryKey(newCommerceTaxMethod.getPrimaryKey());

		Assert.assertEquals(existingCommerceTaxMethod, newCommerceTaxMethod);
	}

	@Test(expected = NoSuchTaxMethodException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTaxMethod> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceTaxMethod", "mvccVersion", true, "commerceTaxMethodId",
			true, "groupId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "name",
			true, "description", true, "engineKey", true, "percentage", true,
			"active", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTaxMethod newCommerceTaxMethod = addCommerceTaxMethod();

		CommerceTaxMethod existingCommerceTaxMethod =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxMethod.getPrimaryKey());

		Assert.assertEquals(existingCommerceTaxMethod, newCommerceTaxMethod);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxMethod missingCommerceTaxMethod =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTaxMethod);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTaxMethod newCommerceTaxMethod1 = addCommerceTaxMethod();
		CommerceTaxMethod newCommerceTaxMethod2 = addCommerceTaxMethod();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxMethod1.getPrimaryKey());
		primaryKeys.add(newCommerceTaxMethod2.getPrimaryKey());

		Map<Serializable, CommerceTaxMethod> commerceTaxMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceTaxMethods.size());
		Assert.assertEquals(
			newCommerceTaxMethod1,
			commerceTaxMethods.get(newCommerceTaxMethod1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTaxMethod2,
			commerceTaxMethods.get(newCommerceTaxMethod2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTaxMethod> commerceTaxMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTaxMethods.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTaxMethod newCommerceTaxMethod = addCommerceTaxMethod();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxMethod.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTaxMethod> commerceTaxMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTaxMethods.size());
		Assert.assertEquals(
			newCommerceTaxMethod,
			commerceTaxMethods.get(newCommerceTaxMethod.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTaxMethod> commerceTaxMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceTaxMethods.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTaxMethod newCommerceTaxMethod = addCommerceTaxMethod();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxMethod.getPrimaryKey());

		Map<Serializable, CommerceTaxMethod> commerceTaxMethods =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceTaxMethods.size());
		Assert.assertEquals(
			newCommerceTaxMethod,
			commerceTaxMethods.get(newCommerceTaxMethod.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceTaxMethod newCommerceTaxMethod = addCommerceTaxMethod();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceTaxMethod.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceTaxMethod commerceTaxMethod) {
		Assert.assertEquals(
			Long.valueOf(commerceTaxMethod.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commerceTaxMethod, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			commerceTaxMethod.getEngineKey(),
			ReflectionTestUtil.invoke(
				commerceTaxMethod, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "engineKey"));
	}

	protected CommerceTaxMethod addCommerceTaxMethod() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxMethod commerceTaxMethod = _persistence.create(pk);

		commerceTaxMethod.setMvccVersion(RandomTestUtil.nextLong());

		commerceTaxMethod.setGroupId(RandomTestUtil.nextLong());

		commerceTaxMethod.setCompanyId(RandomTestUtil.nextLong());

		commerceTaxMethod.setUserId(RandomTestUtil.nextLong());

		commerceTaxMethod.setUserName(RandomTestUtil.randomString());

		commerceTaxMethod.setCreateDate(RandomTestUtil.nextDate());

		commerceTaxMethod.setModifiedDate(RandomTestUtil.nextDate());

		commerceTaxMethod.setName(RandomTestUtil.randomString());

		commerceTaxMethod.setDescription(RandomTestUtil.randomString());

		commerceTaxMethod.setEngineKey(RandomTestUtil.randomString());

		commerceTaxMethod.setPercentage(RandomTestUtil.randomBoolean());

		commerceTaxMethod.setActive(RandomTestUtil.randomBoolean());

		commerceTaxMethod.setTypeSettings(RandomTestUtil.randomString());

		_commerceTaxMethods.add(_persistence.update(commerceTaxMethod));

		return commerceTaxMethod;
	}

	private List<CommerceTaxMethod> _commerceTaxMethods =
		new ArrayList<CommerceTaxMethod>();
	private CommerceTaxMethodPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}