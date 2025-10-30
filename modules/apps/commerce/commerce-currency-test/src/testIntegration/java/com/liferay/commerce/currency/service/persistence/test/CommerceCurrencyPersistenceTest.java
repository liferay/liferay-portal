/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.currency.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.currency.exception.DuplicateCommerceCurrencyExternalReferenceCodeException;
import com.liferay.commerce.currency.exception.NoSuchCurrencyException;
import com.liferay.commerce.currency.model.CommerceCurrency;
import com.liferay.commerce.currency.service.persistence.CommerceCurrencyPersistence;
import com.liferay.commerce.currency.service.persistence.CommerceCurrencyUtil;
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
public class CommerceCurrencyPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.currency.service"));

	@Before
	public void setUp() {
		_persistence = CommerceCurrencyUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceCurrency> iterator = _commerceCurrencies.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCurrency commerceCurrency = _persistence.create(pk);

		Assert.assertNotNull(commerceCurrency);

		Assert.assertEquals(commerceCurrency.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		_persistence.remove(newCommerceCurrency);

		CommerceCurrency existingCommerceCurrency =
			_persistence.fetchByPrimaryKey(newCommerceCurrency.getPrimaryKey());

		Assert.assertNull(existingCommerceCurrency);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceCurrency();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCurrency newCommerceCurrency = _persistence.create(pk);

		newCommerceCurrency.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceCurrency.setUuid(RandomTestUtil.randomString());

		newCommerceCurrency.setExternalReferenceCode(
			RandomTestUtil.randomString());

		newCommerceCurrency.setCompanyId(RandomTestUtil.nextLong());

		newCommerceCurrency.setUserId(RandomTestUtil.nextLong());

		newCommerceCurrency.setUserName(RandomTestUtil.randomString());

		newCommerceCurrency.setCreateDate(RandomTestUtil.nextDate());

		newCommerceCurrency.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceCurrency.setCode(RandomTestUtil.randomString());

		newCommerceCurrency.setName(RandomTestUtil.randomString());

		newCommerceCurrency.setSymbol(RandomTestUtil.randomString());

		newCommerceCurrency.setRate(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommerceCurrency.setFormatPattern(RandomTestUtil.randomString());

		newCommerceCurrency.setMaxFractionDigits(RandomTestUtil.nextInt());

		newCommerceCurrency.setMinFractionDigits(RandomTestUtil.nextInt());

		newCommerceCurrency.setRoundingMode(RandomTestUtil.randomString());

		newCommerceCurrency.setPrimary(RandomTestUtil.randomBoolean());

		newCommerceCurrency.setPriority(RandomTestUtil.nextDouble());

		newCommerceCurrency.setActive(RandomTestUtil.randomBoolean());

		newCommerceCurrency.setLastPublishDate(RandomTestUtil.nextDate());

		_commerceCurrencies.add(_persistence.update(newCommerceCurrency));

		CommerceCurrency existingCommerceCurrency =
			_persistence.findByPrimaryKey(newCommerceCurrency.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceCurrency.getMvccVersion(),
			newCommerceCurrency.getMvccVersion());
		Assert.assertEquals(
			existingCommerceCurrency.getUuid(), newCommerceCurrency.getUuid());
		Assert.assertEquals(
			existingCommerceCurrency.getExternalReferenceCode(),
			newCommerceCurrency.getExternalReferenceCode());
		Assert.assertEquals(
			existingCommerceCurrency.getCommerceCurrencyId(),
			newCommerceCurrency.getCommerceCurrencyId());
		Assert.assertEquals(
			existingCommerceCurrency.getCompanyId(),
			newCommerceCurrency.getCompanyId());
		Assert.assertEquals(
			existingCommerceCurrency.getUserId(),
			newCommerceCurrency.getUserId());
		Assert.assertEquals(
			existingCommerceCurrency.getUserName(),
			newCommerceCurrency.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceCurrency.getCreateDate()),
			Time.getShortTimestamp(newCommerceCurrency.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceCurrency.getModifiedDate()),
			Time.getShortTimestamp(newCommerceCurrency.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceCurrency.getCode(), newCommerceCurrency.getCode());
		Assert.assertEquals(
			existingCommerceCurrency.getName(), newCommerceCurrency.getName());
		Assert.assertEquals(
			existingCommerceCurrency.getSymbol(),
			newCommerceCurrency.getSymbol());
		Assert.assertEquals(
			existingCommerceCurrency.getRate(), newCommerceCurrency.getRate());
		Assert.assertEquals(
			existingCommerceCurrency.getFormatPattern(),
			newCommerceCurrency.getFormatPattern());
		Assert.assertEquals(
			existingCommerceCurrency.getMaxFractionDigits(),
			newCommerceCurrency.getMaxFractionDigits());
		Assert.assertEquals(
			existingCommerceCurrency.getMinFractionDigits(),
			newCommerceCurrency.getMinFractionDigits());
		Assert.assertEquals(
			existingCommerceCurrency.getRoundingMode(),
			newCommerceCurrency.getRoundingMode());
		Assert.assertEquals(
			existingCommerceCurrency.isPrimary(),
			newCommerceCurrency.isPrimary());
		AssertUtils.assertEquals(
			existingCommerceCurrency.getPriority(),
			newCommerceCurrency.getPriority());
		Assert.assertEquals(
			existingCommerceCurrency.isActive(),
			newCommerceCurrency.isActive());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceCurrency.getLastPublishDate()),
			Time.getShortTimestamp(newCommerceCurrency.getLastPublishDate()));
	}

	@Test(
		expected = DuplicateCommerceCurrencyExternalReferenceCodeException.class
	)
	public void testUpdateWithExistingExternalReferenceCode() throws Exception {
		CommerceCurrency commerceCurrency = addCommerceCurrency();

		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		newCommerceCurrency.setCompanyId(commerceCurrency.getCompanyId());

		newCommerceCurrency = _persistence.update(newCommerceCurrency);

		Session session = _persistence.getCurrentSession();

		session.evict(newCommerceCurrency);

		newCommerceCurrency.setExternalReferenceCode(
			commerceCurrency.getExternalReferenceCode());

		_persistence.update(newCommerceCurrency);
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
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(RandomTestUtil.nextLong(), "");

		_persistence.countByC_C(0L, "null");

		_persistence.countByC_C(0L, (String)null);
	}

	@Test
	public void testCountByC_P() throws Exception {
		_persistence.countByC_P(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_P(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_A() throws Exception {
		_persistence.countByC_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByC_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByC_P_A() throws Exception {
		_persistence.countByC_P_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean(),
			RandomTestUtil.randomBoolean());

		_persistence.countByC_P_A(
			0L, RandomTestUtil.randomBoolean(), RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByERC_C() throws Exception {
		_persistence.countByERC_C("", RandomTestUtil.nextLong());

		_persistence.countByERC_C("null", 0L);

		_persistence.countByERC_C((String)null, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		CommerceCurrency existingCommerceCurrency =
			_persistence.findByPrimaryKey(newCommerceCurrency.getPrimaryKey());

		Assert.assertEquals(existingCommerceCurrency, newCommerceCurrency);
	}

	@Test(expected = NoSuchCurrencyException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceCurrency> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceCurrency", "mvccVersion", true, "uuid", true,
			"externalReferenceCode", true, "commerceCurrencyId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "code", true, "name", true, "symbol",
			true, "rate", true, "formatPattern", true, "maxFractionDigits",
			true, "minFractionDigits", true, "roundingMode", true, "primary",
			true, "priority", true, "active", true, "lastPublishDate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		CommerceCurrency existingCommerceCurrency =
			_persistence.fetchByPrimaryKey(newCommerceCurrency.getPrimaryKey());

		Assert.assertEquals(existingCommerceCurrency, newCommerceCurrency);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCurrency missingCommerceCurrency =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceCurrency);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceCurrency newCommerceCurrency1 = addCommerceCurrency();
		CommerceCurrency newCommerceCurrency2 = addCommerceCurrency();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceCurrency1.getPrimaryKey());
		primaryKeys.add(newCommerceCurrency2.getPrimaryKey());

		Map<Serializable, CommerceCurrency> commerceCurrencies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceCurrencies.size());
		Assert.assertEquals(
			newCommerceCurrency1,
			commerceCurrencies.get(newCommerceCurrency1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceCurrency2,
			commerceCurrencies.get(newCommerceCurrency2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceCurrency> commerceCurrencies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceCurrencies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceCurrency.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceCurrency> commerceCurrencies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceCurrencies.size());
		Assert.assertEquals(
			newCommerceCurrency,
			commerceCurrencies.get(newCommerceCurrency.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceCurrency> commerceCurrencies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceCurrencies.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceCurrency.getPrimaryKey());

		Map<Serializable, CommerceCurrency> commerceCurrencies =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceCurrencies.size());
		Assert.assertEquals(
			newCommerceCurrency,
			commerceCurrencies.get(newCommerceCurrency.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceCurrency newCommerceCurrency = addCommerceCurrency();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(newCommerceCurrency.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceCurrency commerceCurrency) {
		Assert.assertEquals(
			Long.valueOf(commerceCurrency.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceCurrency, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
		Assert.assertEquals(
			commerceCurrency.getCode(),
			ReflectionTestUtil.invoke(
				commerceCurrency, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "code_"));

		Assert.assertEquals(
			commerceCurrency.getExternalReferenceCode(),
			ReflectionTestUtil.invoke(
				commerceCurrency, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "externalReferenceCode"));
		Assert.assertEquals(
			Long.valueOf(commerceCurrency.getCompanyId()),
			ReflectionTestUtil.<Long>invoke(
				commerceCurrency, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "companyId"));
	}

	protected CommerceCurrency addCommerceCurrency() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceCurrency commerceCurrency = _persistence.create(pk);

		commerceCurrency.setMvccVersion(RandomTestUtil.nextLong());

		commerceCurrency.setUuid(RandomTestUtil.randomString());

		commerceCurrency.setExternalReferenceCode(
			RandomTestUtil.randomString());

		commerceCurrency.setCompanyId(RandomTestUtil.nextLong());

		commerceCurrency.setUserId(RandomTestUtil.nextLong());

		commerceCurrency.setUserName(RandomTestUtil.randomString());

		commerceCurrency.setCreateDate(RandomTestUtil.nextDate());

		commerceCurrency.setModifiedDate(RandomTestUtil.nextDate());

		commerceCurrency.setCode(RandomTestUtil.randomString());

		commerceCurrency.setName(RandomTestUtil.randomString());

		commerceCurrency.setSymbol(RandomTestUtil.randomString());

		commerceCurrency.setRate(new BigDecimal(RandomTestUtil.nextDouble()));

		commerceCurrency.setFormatPattern(RandomTestUtil.randomString());

		commerceCurrency.setMaxFractionDigits(RandomTestUtil.nextInt());

		commerceCurrency.setMinFractionDigits(RandomTestUtil.nextInt());

		commerceCurrency.setRoundingMode(RandomTestUtil.randomString());

		commerceCurrency.setPrimary(RandomTestUtil.randomBoolean());

		commerceCurrency.setPriority(RandomTestUtil.nextDouble());

		commerceCurrency.setActive(RandomTestUtil.randomBoolean());

		commerceCurrency.setLastPublishDate(RandomTestUtil.nextDate());

		_commerceCurrencies.add(_persistence.update(commerceCurrency));

		return commerceCurrency;
	}

	private List<CommerceCurrency> _commerceCurrencies =
		new ArrayList<CommerceCurrency>();
	private CommerceCurrencyPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}