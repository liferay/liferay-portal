/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.payment.exception.NoSuchPaymentEntryAuditException;
import com.liferay.commerce.payment.model.CommercePaymentEntryAudit;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryAuditPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentEntryAuditUtil;
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
public class CommercePaymentEntryAuditPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.payment.service"));

	@Before
	public void setUp() {
		_persistence = CommercePaymentEntryAuditUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePaymentEntryAudit> iterator =
			_commercePaymentEntryAudits.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			_persistence.create(pk);

		Assert.assertNotNull(commercePaymentEntryAudit);

		Assert.assertEquals(commercePaymentEntryAudit.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePaymentEntryAudit newCommercePaymentEntryAudit =
			addCommercePaymentEntryAudit();

		_persistence.remove(newCommercePaymentEntryAudit);

		CommercePaymentEntryAudit existingCommercePaymentEntryAudit =
			_persistence.fetchByPrimaryKey(
				newCommercePaymentEntryAudit.getPrimaryKey());

		Assert.assertNull(existingCommercePaymentEntryAudit);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePaymentEntryAudit();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntryAudit newCommercePaymentEntryAudit =
			_persistence.create(pk);

		newCommercePaymentEntryAudit.setMvccVersion(RandomTestUtil.nextLong());

		newCommercePaymentEntryAudit.setCompanyId(RandomTestUtil.nextLong());

		newCommercePaymentEntryAudit.setUserId(RandomTestUtil.nextLong());

		newCommercePaymentEntryAudit.setUserName(RandomTestUtil.randomString());

		newCommercePaymentEntryAudit.setCreateDate(RandomTestUtil.nextDate());

		newCommercePaymentEntryAudit.setModifiedDate(RandomTestUtil.nextDate());

		newCommercePaymentEntryAudit.setCommercePaymentEntryId(
			RandomTestUtil.nextLong());

		newCommercePaymentEntryAudit.setAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		newCommercePaymentEntryAudit.setCurrencyCode(
			RandomTestUtil.randomString());

		newCommercePaymentEntryAudit.setLogType(RandomTestUtil.randomString());

		newCommercePaymentEntryAudit.setLogTypeSettings(
			RandomTestUtil.randomString());

		_commercePaymentEntryAudits.add(
			_persistence.update(newCommercePaymentEntryAudit));

		CommercePaymentEntryAudit existingCommercePaymentEntryAudit =
			_persistence.findByPrimaryKey(
				newCommercePaymentEntryAudit.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getMvccVersion(),
			newCommercePaymentEntryAudit.getMvccVersion());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getCommercePaymentEntryAuditId(),
			newCommercePaymentEntryAudit.getCommercePaymentEntryAuditId());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getCompanyId(),
			newCommercePaymentEntryAudit.getCompanyId());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getUserId(),
			newCommercePaymentEntryAudit.getUserId());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getUserName(),
			newCommercePaymentEntryAudit.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentEntryAudit.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePaymentEntryAudit.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentEntryAudit.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePaymentEntryAudit.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getCommercePaymentEntryId(),
			newCommercePaymentEntryAudit.getCommercePaymentEntryId());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getAmount(),
			newCommercePaymentEntryAudit.getAmount());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getCurrencyCode(),
			newCommercePaymentEntryAudit.getCurrencyCode());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getLogType(),
			newCommercePaymentEntryAudit.getLogType());
		Assert.assertEquals(
			existingCommercePaymentEntryAudit.getLogTypeSettings(),
			newCommercePaymentEntryAudit.getLogTypeSettings());
	}

	@Test
	public void testCountByCommercePaymentEntryId() throws Exception {
		_persistence.countByCommercePaymentEntryId(RandomTestUtil.nextLong());

		_persistence.countByCommercePaymentEntryId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePaymentEntryAudit newCommercePaymentEntryAudit =
			addCommercePaymentEntryAudit();

		CommercePaymentEntryAudit existingCommercePaymentEntryAudit =
			_persistence.findByPrimaryKey(
				newCommercePaymentEntryAudit.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentEntryAudit, newCommercePaymentEntryAudit);
	}

	@Test(expected = NoSuchPaymentEntryAuditException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePaymentEntryAudit>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommercePaymentEntryAudit", "mvccVersion", true,
			"commercePaymentEntryAuditId", true, "companyId", true, "userId",
			true, "userName", true, "createDate", true, "modifiedDate", true,
			"commercePaymentEntryId", true, "amount", true, "currencyCode",
			true, "logType", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePaymentEntryAudit newCommercePaymentEntryAudit =
			addCommercePaymentEntryAudit();

		CommercePaymentEntryAudit existingCommercePaymentEntryAudit =
			_persistence.fetchByPrimaryKey(
				newCommercePaymentEntryAudit.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentEntryAudit, newCommercePaymentEntryAudit);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntryAudit missingCommercePaymentEntryAudit =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePaymentEntryAudit);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePaymentEntryAudit newCommercePaymentEntryAudit1 =
			addCommercePaymentEntryAudit();
		CommercePaymentEntryAudit newCommercePaymentEntryAudit2 =
			addCommercePaymentEntryAudit();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentEntryAudit1.getPrimaryKey());
		primaryKeys.add(newCommercePaymentEntryAudit2.getPrimaryKey());

		Map<Serializable, CommercePaymentEntryAudit>
			commercePaymentEntryAudits = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commercePaymentEntryAudits.size());
		Assert.assertEquals(
			newCommercePaymentEntryAudit1,
			commercePaymentEntryAudits.get(
				newCommercePaymentEntryAudit1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePaymentEntryAudit2,
			commercePaymentEntryAudits.get(
				newCommercePaymentEntryAudit2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePaymentEntryAudit>
			commercePaymentEntryAudits = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePaymentEntryAudits.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePaymentEntryAudit newCommercePaymentEntryAudit =
			addCommercePaymentEntryAudit();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentEntryAudit.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePaymentEntryAudit>
			commercePaymentEntryAudits = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePaymentEntryAudits.size());
		Assert.assertEquals(
			newCommercePaymentEntryAudit,
			commercePaymentEntryAudits.get(
				newCommercePaymentEntryAudit.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePaymentEntryAudit>
			commercePaymentEntryAudits = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePaymentEntryAudits.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePaymentEntryAudit newCommercePaymentEntryAudit =
			addCommercePaymentEntryAudit();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentEntryAudit.getPrimaryKey());

		Map<Serializable, CommercePaymentEntryAudit>
			commercePaymentEntryAudits = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePaymentEntryAudits.size());
		Assert.assertEquals(
			newCommercePaymentEntryAudit,
			commercePaymentEntryAudits.get(
				newCommercePaymentEntryAudit.getPrimaryKey()));
	}

	protected CommercePaymentEntryAudit addCommercePaymentEntryAudit()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePaymentEntryAudit commercePaymentEntryAudit =
			_persistence.create(pk);

		commercePaymentEntryAudit.setMvccVersion(RandomTestUtil.nextLong());

		commercePaymentEntryAudit.setCompanyId(RandomTestUtil.nextLong());

		commercePaymentEntryAudit.setUserId(RandomTestUtil.nextLong());

		commercePaymentEntryAudit.setUserName(RandomTestUtil.randomString());

		commercePaymentEntryAudit.setCreateDate(RandomTestUtil.nextDate());

		commercePaymentEntryAudit.setModifiedDate(RandomTestUtil.nextDate());

		commercePaymentEntryAudit.setCommercePaymentEntryId(
			RandomTestUtil.nextLong());

		commercePaymentEntryAudit.setAmount(
			new BigDecimal(RandomTestUtil.nextDouble()));

		commercePaymentEntryAudit.setCurrencyCode(
			RandomTestUtil.randomString());

		commercePaymentEntryAudit.setLogType(RandomTestUtil.randomString());

		commercePaymentEntryAudit.setLogTypeSettings(
			RandomTestUtil.randomString());

		_commercePaymentEntryAudits.add(
			_persistence.update(commercePaymentEntryAudit));

		return commercePaymentEntryAudit;
	}

	private List<CommercePaymentEntryAudit> _commercePaymentEntryAudits =
		new ArrayList<CommercePaymentEntryAudit>();
	private CommercePaymentEntryAuditPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}