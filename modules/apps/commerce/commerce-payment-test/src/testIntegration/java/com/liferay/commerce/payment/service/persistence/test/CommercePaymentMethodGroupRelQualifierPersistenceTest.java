/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.payment.exception.NoSuchPaymentMethodGroupRelQualifierException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRelQualifier;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelQualifierPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelQualifierUtil;
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
public class CommercePaymentMethodGroupRelQualifierPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.payment.service"));

	@Before
	public void setUp() {
		_persistence =
			CommercePaymentMethodGroupRelQualifierUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePaymentMethodGroupRelQualifier> iterator =
			_commercePaymentMethodGroupRelQualifiers.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier = _persistence.create(pk);

		Assert.assertNotNull(commercePaymentMethodGroupRelQualifier);

		Assert.assertEquals(
			commercePaymentMethodGroupRelQualifier.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier =
				addCommercePaymentMethodGroupRelQualifier();

		_persistence.remove(newCommercePaymentMethodGroupRelQualifier);

		CommercePaymentMethodGroupRelQualifier
			existingCommercePaymentMethodGroupRelQualifier =
				_persistence.fetchByPrimaryKey(
					newCommercePaymentMethodGroupRelQualifier.getPrimaryKey());

		Assert.assertNull(existingCommercePaymentMethodGroupRelQualifier);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePaymentMethodGroupRelQualifier();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier = _persistence.create(pk);

		newCommercePaymentMethodGroupRelQualifier.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRelQualifier.setCompanyId(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRelQualifier.setUserId(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRelQualifier.setUserName(
			RandomTestUtil.randomString());

		newCommercePaymentMethodGroupRelQualifier.setCreateDate(
			RandomTestUtil.nextDate());

		newCommercePaymentMethodGroupRelQualifier.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePaymentMethodGroupRelQualifier.setClassNameId(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRelQualifier.setClassPK(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRelQualifier.
			setCommercePaymentMethodGroupRelId(RandomTestUtil.nextLong());

		_commercePaymentMethodGroupRelQualifiers.add(
			_persistence.update(newCommercePaymentMethodGroupRelQualifier));

		CommercePaymentMethodGroupRelQualifier
			existingCommercePaymentMethodGroupRelQualifier =
				_persistence.findByPrimaryKey(
					newCommercePaymentMethodGroupRelQualifier.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.getMvccVersion(),
			newCommercePaymentMethodGroupRelQualifier.getMvccVersion());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.
				getCommercePaymentMethodGroupRelQualifierId(),
			newCommercePaymentMethodGroupRelQualifier.
				getCommercePaymentMethodGroupRelQualifierId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.getCompanyId(),
			newCommercePaymentMethodGroupRelQualifier.getCompanyId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.getUserId(),
			newCommercePaymentMethodGroupRelQualifier.getUserId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.getUserName(),
			newCommercePaymentMethodGroupRelQualifier.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentMethodGroupRelQualifier.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePaymentMethodGroupRelQualifier.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentMethodGroupRelQualifier.
					getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePaymentMethodGroupRelQualifier.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.getClassNameId(),
			newCommercePaymentMethodGroupRelQualifier.getClassNameId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.getClassPK(),
			newCommercePaymentMethodGroupRelQualifier.getClassPK());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier.
				getCommercePaymentMethodGroupRelId(),
			newCommercePaymentMethodGroupRelQualifier.
				getCommercePaymentMethodGroupRelId());
	}

	@Test
	public void testCountByCommercePaymentMethodGroupRelId() throws Exception {
		_persistence.countByCommercePaymentMethodGroupRelId(
			RandomTestUtil.nextLong());

		_persistence.countByCommercePaymentMethodGroupRelId(0L);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_C_C() throws Exception {
		_persistence.countByC_C_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong());

		_persistence.countByC_C_C(0L, 0L, 0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier =
				addCommercePaymentMethodGroupRelQualifier();

		CommercePaymentMethodGroupRelQualifier
			existingCommercePaymentMethodGroupRelQualifier =
				_persistence.findByPrimaryKey(
					newCommercePaymentMethodGroupRelQualifier.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier,
			newCommercePaymentMethodGroupRelQualifier);
	}

	@Test(expected = NoSuchPaymentMethodGroupRelQualifierException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePaymentMethodGroupRelQualifier>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CPMethodGroupRelQualifier", "mvccVersion", true,
			"commercePaymentMethodGroupRelQualifierId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"commercePaymentMethodGroupRelId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier =
				addCommercePaymentMethodGroupRelQualifier();

		CommercePaymentMethodGroupRelQualifier
			existingCommercePaymentMethodGroupRelQualifier =
				_persistence.fetchByPrimaryKey(
					newCommercePaymentMethodGroupRelQualifier.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentMethodGroupRelQualifier,
			newCommercePaymentMethodGroupRelQualifier);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRelQualifier
			missingCommercePaymentMethodGroupRelQualifier =
				_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePaymentMethodGroupRelQualifier);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier1 =
				addCommercePaymentMethodGroupRelQualifier();
		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier2 =
				addCommercePaymentMethodGroupRelQualifier();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePaymentMethodGroupRelQualifier1.getPrimaryKey());
		primaryKeys.add(
			newCommercePaymentMethodGroupRelQualifier2.getPrimaryKey());

		Map<Serializable, CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commercePaymentMethodGroupRelQualifiers.size());
		Assert.assertEquals(
			newCommercePaymentMethodGroupRelQualifier1,
			commercePaymentMethodGroupRelQualifiers.get(
				newCommercePaymentMethodGroupRelQualifier1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePaymentMethodGroupRelQualifier2,
			commercePaymentMethodGroupRelQualifiers.get(
				newCommercePaymentMethodGroupRelQualifier2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePaymentMethodGroupRelQualifiers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier =
				addCommercePaymentMethodGroupRelQualifier();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePaymentMethodGroupRelQualifier.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePaymentMethodGroupRelQualifiers.size());
		Assert.assertEquals(
			newCommercePaymentMethodGroupRelQualifier,
			commercePaymentMethodGroupRelQualifiers.get(
				newCommercePaymentMethodGroupRelQualifier.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commercePaymentMethodGroupRelQualifiers.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier =
				addCommercePaymentMethodGroupRelQualifier();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(
			newCommercePaymentMethodGroupRelQualifier.getPrimaryKey());

		Map<Serializable, CommercePaymentMethodGroupRelQualifier>
			commercePaymentMethodGroupRelQualifiers =
				_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commercePaymentMethodGroupRelQualifiers.size());
		Assert.assertEquals(
			newCommercePaymentMethodGroupRelQualifier,
			commercePaymentMethodGroupRelQualifiers.get(
				newCommercePaymentMethodGroupRelQualifier.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePaymentMethodGroupRelQualifier
			newCommercePaymentMethodGroupRelQualifier =
				addCommercePaymentMethodGroupRelQualifier();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePaymentMethodGroupRelQualifier.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier) {

		Assert.assertEquals(
			Long.valueOf(
				commercePaymentMethodGroupRelQualifier.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commercePaymentMethodGroupRelQualifier,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"classNameId"));
		Assert.assertEquals(
			Long.valueOf(commercePaymentMethodGroupRelQualifier.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commercePaymentMethodGroupRelQualifier,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"classPK"));
		Assert.assertEquals(
			Long.valueOf(
				commercePaymentMethodGroupRelQualifier.
					getCommercePaymentMethodGroupRelId()),
			ReflectionTestUtil.<Long>invoke(
				commercePaymentMethodGroupRelQualifier,
				"getColumnOriginalValue", new Class<?>[] {String.class},
				"CPaymentMethodGroupRelId"));
	}

	protected CommercePaymentMethodGroupRelQualifier
			addCommercePaymentMethodGroupRelQualifier()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRelQualifier
			commercePaymentMethodGroupRelQualifier = _persistence.create(pk);

		commercePaymentMethodGroupRelQualifier.setMvccVersion(
			RandomTestUtil.nextLong());

		commercePaymentMethodGroupRelQualifier.setCompanyId(
			RandomTestUtil.nextLong());

		commercePaymentMethodGroupRelQualifier.setUserId(
			RandomTestUtil.nextLong());

		commercePaymentMethodGroupRelQualifier.setUserName(
			RandomTestUtil.randomString());

		commercePaymentMethodGroupRelQualifier.setCreateDate(
			RandomTestUtil.nextDate());

		commercePaymentMethodGroupRelQualifier.setModifiedDate(
			RandomTestUtil.nextDate());

		commercePaymentMethodGroupRelQualifier.setClassNameId(
			RandomTestUtil.nextLong());

		commercePaymentMethodGroupRelQualifier.setClassPK(
			RandomTestUtil.nextLong());

		commercePaymentMethodGroupRelQualifier.
			setCommercePaymentMethodGroupRelId(RandomTestUtil.nextLong());

		_commercePaymentMethodGroupRelQualifiers.add(
			_persistence.update(commercePaymentMethodGroupRelQualifier));

		return commercePaymentMethodGroupRelQualifier;
	}

	private List<CommercePaymentMethodGroupRelQualifier>
		_commercePaymentMethodGroupRelQualifiers =
			new ArrayList<CommercePaymentMethodGroupRelQualifier>();
	private CommercePaymentMethodGroupRelQualifierPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}