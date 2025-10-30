/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.shipping.engine.fixed.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.shipping.engine.fixed.exception.NoSuchShippingFixedOptionException;
import com.liferay.commerce.shipping.engine.fixed.model.CommerceShippingFixedOption;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionPersistence;
import com.liferay.commerce.shipping.engine.fixed.service.persistence.CommerceShippingFixedOptionUtil;
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
public class CommerceShippingFixedOptionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.shipping.engine.fixed.service"));

	@Before
	public void setUp() {
		_persistence = CommerceShippingFixedOptionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceShippingFixedOption> iterator =
			_commerceShippingFixedOptions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOption commerceShippingFixedOption =
			_persistence.create(pk);

		Assert.assertNotNull(commerceShippingFixedOption);

		Assert.assertEquals(commerceShippingFixedOption.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceShippingFixedOption newCommerceShippingFixedOption =
			addCommerceShippingFixedOption();

		_persistence.remove(newCommerceShippingFixedOption);

		CommerceShippingFixedOption existingCommerceShippingFixedOption =
			_persistence.fetchByPrimaryKey(
				newCommerceShippingFixedOption.getPrimaryKey());

		Assert.assertNull(existingCommerceShippingFixedOption);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceShippingFixedOption();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOption newCommerceShippingFixedOption =
			_persistence.create(pk);

		newCommerceShippingFixedOption.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOption.setGroupId(RandomTestUtil.nextLong());

		newCommerceShippingFixedOption.setCompanyId(RandomTestUtil.nextLong());

		newCommerceShippingFixedOption.setUserId(RandomTestUtil.nextLong());

		newCommerceShippingFixedOption.setUserName(
			RandomTestUtil.randomString());

		newCommerceShippingFixedOption.setCreateDate(RandomTestUtil.nextDate());

		newCommerceShippingFixedOption.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceShippingFixedOption.setCommerceShippingMethodId(
			RandomTestUtil.nextLong());

		newCommerceShippingFixedOption.setAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceShippingFixedOption.setDescription(
			RandomTestUtil.randomString());

		newCommerceShippingFixedOption.setKey(RandomTestUtil.randomString());

		newCommerceShippingFixedOption.setName(RandomTestUtil.randomString());

		newCommerceShippingFixedOption.setPriority(RandomTestUtil.nextDouble());

		_commerceShippingFixedOptions.add(
			_persistence.update(newCommerceShippingFixedOption));

		CommerceShippingFixedOption existingCommerceShippingFixedOption =
			_persistence.findByPrimaryKey(
				newCommerceShippingFixedOption.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOption.getMvccVersion(),
			newCommerceShippingFixedOption.getMvccVersion());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.
				getCommerceShippingFixedOptionId(),
			newCommerceShippingFixedOption.getCommerceShippingFixedOptionId());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getGroupId(),
			newCommerceShippingFixedOption.getGroupId());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getCompanyId(),
			newCommerceShippingFixedOption.getCompanyId());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getUserId(),
			newCommerceShippingFixedOption.getUserId());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getUserName(),
			newCommerceShippingFixedOption.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingFixedOption.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceShippingFixedOption.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceShippingFixedOption.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceShippingFixedOption.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getCommerceShippingMethodId(),
			newCommerceShippingFixedOption.getCommerceShippingMethodId());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getAmount(),
			newCommerceShippingFixedOption.getAmount());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getDescription(),
			newCommerceShippingFixedOption.getDescription());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getKey(),
			newCommerceShippingFixedOption.getKey());
		Assert.assertEquals(
			existingCommerceShippingFixedOption.getName(),
			newCommerceShippingFixedOption.getName());
		AssertUtils.assertEquals(
			existingCommerceShippingFixedOption.getPriority(),
			newCommerceShippingFixedOption.getPriority());
	}

	@Test
	public void testCountByCommerceShippingMethodId() throws Exception {
		_persistence.countByCommerceShippingMethodId(RandomTestUtil.nextLong());

		_persistence.countByCommerceShippingMethodId(0L);
	}

	@Test
	public void testCountByC_K() throws Exception {
		_persistence.countByC_K(RandomTestUtil.nextLong(), "");

		_persistence.countByC_K(0L, "null");

		_persistence.countByC_K(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOption newCommerceShippingFixedOption =
			addCommerceShippingFixedOption();

		CommerceShippingFixedOption existingCommerceShippingFixedOption =
			_persistence.findByPrimaryKey(
				newCommerceShippingFixedOption.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOption,
			newCommerceShippingFixedOption);
	}

	@Test(expected = NoSuchShippingFixedOptionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceShippingFixedOption>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceShippingFixedOption", "mvccVersion", true,
			"commerceShippingFixedOptionId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "commerceShippingMethodId", true, "amount",
			true, "description", true, "key", true, "name", true, "priority",
			true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceShippingFixedOption newCommerceShippingFixedOption =
			addCommerceShippingFixedOption();

		CommerceShippingFixedOption existingCommerceShippingFixedOption =
			_persistence.fetchByPrimaryKey(
				newCommerceShippingFixedOption.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceShippingFixedOption,
			newCommerceShippingFixedOption);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOption missingCommerceShippingFixedOption =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceShippingFixedOption);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceShippingFixedOption newCommerceShippingFixedOption1 =
			addCommerceShippingFixedOption();
		CommerceShippingFixedOption newCommerceShippingFixedOption2 =
			addCommerceShippingFixedOption();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingFixedOption1.getPrimaryKey());
		primaryKeys.add(newCommerceShippingFixedOption2.getPrimaryKey());

		Map<Serializable, CommerceShippingFixedOption>
			commerceShippingFixedOptions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceShippingFixedOptions.size());
		Assert.assertEquals(
			newCommerceShippingFixedOption1,
			commerceShippingFixedOptions.get(
				newCommerceShippingFixedOption1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceShippingFixedOption2,
			commerceShippingFixedOptions.get(
				newCommerceShippingFixedOption2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceShippingFixedOption>
			commerceShippingFixedOptions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceShippingFixedOptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceShippingFixedOption newCommerceShippingFixedOption =
			addCommerceShippingFixedOption();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingFixedOption.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceShippingFixedOption>
			commerceShippingFixedOptions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceShippingFixedOptions.size());
		Assert.assertEquals(
			newCommerceShippingFixedOption,
			commerceShippingFixedOptions.get(
				newCommerceShippingFixedOption.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceShippingFixedOption>
			commerceShippingFixedOptions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceShippingFixedOptions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceShippingFixedOption newCommerceShippingFixedOption =
			addCommerceShippingFixedOption();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceShippingFixedOption.getPrimaryKey());

		Map<Serializable, CommerceShippingFixedOption>
			commerceShippingFixedOptions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceShippingFixedOptions.size());
		Assert.assertEquals(
			newCommerceShippingFixedOption,
			commerceShippingFixedOptions.get(
				newCommerceShippingFixedOption.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceShippingFixedOption newCommerceShippingFixedOption =
			addCommerceShippingFixedOption();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceShippingFixedOption.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceShippingFixedOption commerceShippingFixedOption) {

		Assert.assertEquals(
			Long.valueOf(commerceShippingFixedOption.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceShippingFixedOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			commerceShippingFixedOption.getKey(),
			ReflectionTestUtil.invoke(
				commerceShippingFixedOption, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "key_"));
	}

	protected CommerceShippingFixedOption addCommerceShippingFixedOption()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceShippingFixedOption commerceShippingFixedOption =
			_persistence.create(pk);

		commerceShippingFixedOption.setMvccVersion(RandomTestUtil.nextLong());

		commerceShippingFixedOption.setGroupId(RandomTestUtil.nextLong());

		commerceShippingFixedOption.setCompanyId(RandomTestUtil.nextLong());

		commerceShippingFixedOption.setUserId(RandomTestUtil.nextLong());

		commerceShippingFixedOption.setUserName(RandomTestUtil.randomString());

		commerceShippingFixedOption.setCreateDate(RandomTestUtil.nextDate());

		commerceShippingFixedOption.setModifiedDate(RandomTestUtil.nextDate());

		commerceShippingFixedOption.setCommerceShippingMethodId(
			RandomTestUtil.nextLong());

		commerceShippingFixedOption.setAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commerceShippingFixedOption.setDescription(
			RandomTestUtil.randomString());

		commerceShippingFixedOption.setKey(RandomTestUtil.randomString());

		commerceShippingFixedOption.setName(RandomTestUtil.randomString());

		commerceShippingFixedOption.setPriority(RandomTestUtil.nextDouble());

		_commerceShippingFixedOptions.add(
			_persistence.update(commerceShippingFixedOption));

		return commerceShippingFixedOption;
	}

	private List<CommerceShippingFixedOption> _commerceShippingFixedOptions =
		new ArrayList<CommerceShippingFixedOption>();
	private CommerceShippingFixedOptionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}