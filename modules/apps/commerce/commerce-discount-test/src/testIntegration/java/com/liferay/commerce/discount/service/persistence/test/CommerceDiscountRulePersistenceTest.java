/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.discount.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.discount.exception.NoSuchDiscountRuleException;
import com.liferay.commerce.discount.model.CommerceDiscountRule;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRulePersistence;
import com.liferay.commerce.discount.service.persistence.CommerceDiscountRuleUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
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
public class CommerceDiscountRulePersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.discount.service"));

	@Before
	public void setUp() {
		_persistence = CommerceDiscountRuleUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceDiscountRule> iterator =
			_commerceDiscountRules.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRule commerceDiscountRule = _persistence.create(pk);

		Assert.assertNotNull(commerceDiscountRule);

		Assert.assertEquals(commerceDiscountRule.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceDiscountRule newCommerceDiscountRule =
			addCommerceDiscountRule();

		_persistence.remove(newCommerceDiscountRule);

		CommerceDiscountRule existingCommerceDiscountRule =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountRule.getPrimaryKey());

		Assert.assertNull(existingCommerceDiscountRule);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceDiscountRule();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRule newCommerceDiscountRule = _persistence.create(pk);

		newCommerceDiscountRule.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceDiscountRule.setCompanyId(RandomTestUtil.nextLong());

		newCommerceDiscountRule.setUserId(RandomTestUtil.nextLong());

		newCommerceDiscountRule.setUserName(RandomTestUtil.randomString());

		newCommerceDiscountRule.setCreateDate(RandomTestUtil.nextDate());

		newCommerceDiscountRule.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceDiscountRule.setName(RandomTestUtil.randomString());

		newCommerceDiscountRule.setCommerceDiscountId(
			RandomTestUtil.nextLong());

		newCommerceDiscountRule.setType(RandomTestUtil.randomString());

		newCommerceDiscountRule.setTypeSettings(RandomTestUtil.randomString());

		_commerceDiscountRules.add(
			_persistence.update(newCommerceDiscountRule));

		CommerceDiscountRule existingCommerceDiscountRule =
			_persistence.findByPrimaryKey(
				newCommerceDiscountRule.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountRule.getMvccVersion(),
			newCommerceDiscountRule.getMvccVersion());
		Assert.assertEquals(
			existingCommerceDiscountRule.getCommerceDiscountRuleId(),
			newCommerceDiscountRule.getCommerceDiscountRuleId());
		Assert.assertEquals(
			existingCommerceDiscountRule.getCompanyId(),
			newCommerceDiscountRule.getCompanyId());
		Assert.assertEquals(
			existingCommerceDiscountRule.getUserId(),
			newCommerceDiscountRule.getUserId());
		Assert.assertEquals(
			existingCommerceDiscountRule.getUserName(),
			newCommerceDiscountRule.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountRule.getCreateDate()),
			Time.getShortTimestamp(newCommerceDiscountRule.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceDiscountRule.getModifiedDate()),
			Time.getShortTimestamp(newCommerceDiscountRule.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceDiscountRule.getName(),
			newCommerceDiscountRule.getName());
		Assert.assertEquals(
			existingCommerceDiscountRule.getCommerceDiscountId(),
			newCommerceDiscountRule.getCommerceDiscountId());
		Assert.assertEquals(
			existingCommerceDiscountRule.getType(),
			newCommerceDiscountRule.getType());
		Assert.assertEquals(
			existingCommerceDiscountRule.getTypeSettings(),
			newCommerceDiscountRule.getTypeSettings());
	}

	@Test
	public void testCountByCommerceDiscountId() throws Exception {
		_persistence.countByCommerceDiscountId(RandomTestUtil.nextLong());

		_persistence.countByCommerceDiscountId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceDiscountRule newCommerceDiscountRule =
			addCommerceDiscountRule();

		CommerceDiscountRule existingCommerceDiscountRule =
			_persistence.findByPrimaryKey(
				newCommerceDiscountRule.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountRule, newCommerceDiscountRule);
	}

	@Test(expected = NoSuchDiscountRuleException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceDiscountRule> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceDiscountRule", "mvccVersion", true,
			"commerceDiscountRuleId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true, "name",
			true, "commerceDiscountId", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceDiscountRule newCommerceDiscountRule =
			addCommerceDiscountRule();

		CommerceDiscountRule existingCommerceDiscountRule =
			_persistence.fetchByPrimaryKey(
				newCommerceDiscountRule.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceDiscountRule, newCommerceDiscountRule);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRule missingCommerceDiscountRule =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceDiscountRule);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceDiscountRule newCommerceDiscountRule1 =
			addCommerceDiscountRule();
		CommerceDiscountRule newCommerceDiscountRule2 =
			addCommerceDiscountRule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountRule1.getPrimaryKey());
		primaryKeys.add(newCommerceDiscountRule2.getPrimaryKey());

		Map<Serializable, CommerceDiscountRule> commerceDiscountRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceDiscountRules.size());
		Assert.assertEquals(
			newCommerceDiscountRule1,
			commerceDiscountRules.get(
				newCommerceDiscountRule1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceDiscountRule2,
			commerceDiscountRules.get(
				newCommerceDiscountRule2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceDiscountRule> commerceDiscountRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceDiscountRules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceDiscountRule newCommerceDiscountRule =
			addCommerceDiscountRule();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountRule.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceDiscountRule> commerceDiscountRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceDiscountRules.size());
		Assert.assertEquals(
			newCommerceDiscountRule,
			commerceDiscountRules.get(newCommerceDiscountRule.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceDiscountRule> commerceDiscountRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceDiscountRules.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceDiscountRule newCommerceDiscountRule =
			addCommerceDiscountRule();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceDiscountRule.getPrimaryKey());

		Map<Serializable, CommerceDiscountRule> commerceDiscountRules =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceDiscountRules.size());
		Assert.assertEquals(
			newCommerceDiscountRule,
			commerceDiscountRules.get(newCommerceDiscountRule.getPrimaryKey()));
	}

	protected CommerceDiscountRule addCommerceDiscountRule() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceDiscountRule commerceDiscountRule = _persistence.create(pk);

		commerceDiscountRule.setMvccVersion(RandomTestUtil.nextLong());

		commerceDiscountRule.setCompanyId(RandomTestUtil.nextLong());

		commerceDiscountRule.setUserId(RandomTestUtil.nextLong());

		commerceDiscountRule.setUserName(RandomTestUtil.randomString());

		commerceDiscountRule.setCreateDate(RandomTestUtil.nextDate());

		commerceDiscountRule.setModifiedDate(RandomTestUtil.nextDate());

		commerceDiscountRule.setName(RandomTestUtil.randomString());

		commerceDiscountRule.setCommerceDiscountId(RandomTestUtil.nextLong());

		commerceDiscountRule.setType(RandomTestUtil.randomString());

		commerceDiscountRule.setTypeSettings(RandomTestUtil.randomString());

		_commerceDiscountRules.add(_persistence.update(commerceDiscountRule));

		return commerceDiscountRule;
	}

	private List<CommerceDiscountRule> _commerceDiscountRules =
		new ArrayList<CommerceDiscountRule>();
	private CommerceDiscountRulePersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}