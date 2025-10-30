/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.exception.NoSuchAddressRestrictionException;
import com.liferay.commerce.model.CommerceAddressRestriction;
import com.liferay.commerce.service.persistence.CommerceAddressRestrictionPersistence;
import com.liferay.commerce.service.persistence.CommerceAddressRestrictionUtil;
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
public class CommerceAddressRestrictionPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.service"));

	@Before
	public void setUp() {
		_persistence = CommerceAddressRestrictionUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceAddressRestriction> iterator =
			_commerceAddressRestrictions.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceAddressRestriction commerceAddressRestriction =
			_persistence.create(pk);

		Assert.assertNotNull(commerceAddressRestriction);

		Assert.assertEquals(commerceAddressRestriction.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceAddressRestriction newCommerceAddressRestriction =
			addCommerceAddressRestriction();

		_persistence.remove(newCommerceAddressRestriction);

		CommerceAddressRestriction existingCommerceAddressRestriction =
			_persistence.fetchByPrimaryKey(
				newCommerceAddressRestriction.getPrimaryKey());

		Assert.assertNull(existingCommerceAddressRestriction);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceAddressRestriction();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceAddressRestriction newCommerceAddressRestriction =
			_persistence.create(pk);

		newCommerceAddressRestriction.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceAddressRestriction.setGroupId(RandomTestUtil.nextLong());

		newCommerceAddressRestriction.setCompanyId(RandomTestUtil.nextLong());

		newCommerceAddressRestriction.setUserId(RandomTestUtil.nextLong());

		newCommerceAddressRestriction.setUserName(
			RandomTestUtil.randomString());

		newCommerceAddressRestriction.setCreateDate(RandomTestUtil.nextDate());

		newCommerceAddressRestriction.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceAddressRestriction.setClassNameId(RandomTestUtil.nextLong());

		newCommerceAddressRestriction.setClassPK(RandomTestUtil.nextLong());

		newCommerceAddressRestriction.setCountryId(RandomTestUtil.nextLong());

		_commerceAddressRestrictions.add(
			_persistence.update(newCommerceAddressRestriction));

		CommerceAddressRestriction existingCommerceAddressRestriction =
			_persistence.findByPrimaryKey(
				newCommerceAddressRestriction.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceAddressRestriction.getMvccVersion(),
			newCommerceAddressRestriction.getMvccVersion());
		Assert.assertEquals(
			existingCommerceAddressRestriction.
				getCommerceAddressRestrictionId(),
			newCommerceAddressRestriction.getCommerceAddressRestrictionId());
		Assert.assertEquals(
			existingCommerceAddressRestriction.getGroupId(),
			newCommerceAddressRestriction.getGroupId());
		Assert.assertEquals(
			existingCommerceAddressRestriction.getCompanyId(),
			newCommerceAddressRestriction.getCompanyId());
		Assert.assertEquals(
			existingCommerceAddressRestriction.getUserId(),
			newCommerceAddressRestriction.getUserId());
		Assert.assertEquals(
			existingCommerceAddressRestriction.getUserName(),
			newCommerceAddressRestriction.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceAddressRestriction.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceAddressRestriction.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceAddressRestriction.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceAddressRestriction.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceAddressRestriction.getClassNameId(),
			newCommerceAddressRestriction.getClassNameId());
		Assert.assertEquals(
			existingCommerceAddressRestriction.getClassPK(),
			newCommerceAddressRestriction.getClassPK());
		Assert.assertEquals(
			existingCommerceAddressRestriction.getCountryId(),
			newCommerceAddressRestriction.getCountryId());
	}

	@Test
	public void testCountByCountryId() throws Exception {
		_persistence.countByCountryId(RandomTestUtil.nextLong());

		_persistence.countByCountryId(0L);
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
		CommerceAddressRestriction newCommerceAddressRestriction =
			addCommerceAddressRestriction();

		CommerceAddressRestriction existingCommerceAddressRestriction =
			_persistence.findByPrimaryKey(
				newCommerceAddressRestriction.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceAddressRestriction, newCommerceAddressRestriction);
	}

	@Test(expected = NoSuchAddressRestrictionException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceAddressRestriction>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceAddressRestriction", "mvccVersion", true,
			"commerceAddressRestrictionId", true, "groupId", true, "companyId",
			true, "userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "classNameId", true, "classPK", true,
			"countryId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceAddressRestriction newCommerceAddressRestriction =
			addCommerceAddressRestriction();

		CommerceAddressRestriction existingCommerceAddressRestriction =
			_persistence.fetchByPrimaryKey(
				newCommerceAddressRestriction.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceAddressRestriction, newCommerceAddressRestriction);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceAddressRestriction missingCommerceAddressRestriction =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceAddressRestriction);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceAddressRestriction newCommerceAddressRestriction1 =
			addCommerceAddressRestriction();
		CommerceAddressRestriction newCommerceAddressRestriction2 =
			addCommerceAddressRestriction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceAddressRestriction1.getPrimaryKey());
		primaryKeys.add(newCommerceAddressRestriction2.getPrimaryKey());

		Map<Serializable, CommerceAddressRestriction>
			commerceAddressRestrictions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceAddressRestrictions.size());
		Assert.assertEquals(
			newCommerceAddressRestriction1,
			commerceAddressRestrictions.get(
				newCommerceAddressRestriction1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceAddressRestriction2,
			commerceAddressRestrictions.get(
				newCommerceAddressRestriction2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceAddressRestriction>
			commerceAddressRestrictions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceAddressRestrictions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceAddressRestriction newCommerceAddressRestriction =
			addCommerceAddressRestriction();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceAddressRestriction.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceAddressRestriction>
			commerceAddressRestrictions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceAddressRestrictions.size());
		Assert.assertEquals(
			newCommerceAddressRestriction,
			commerceAddressRestrictions.get(
				newCommerceAddressRestriction.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceAddressRestriction>
			commerceAddressRestrictions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceAddressRestrictions.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceAddressRestriction newCommerceAddressRestriction =
			addCommerceAddressRestriction();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceAddressRestriction.getPrimaryKey());

		Map<Serializable, CommerceAddressRestriction>
			commerceAddressRestrictions = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceAddressRestrictions.size());
		Assert.assertEquals(
			newCommerceAddressRestriction,
			commerceAddressRestrictions.get(
				newCommerceAddressRestriction.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceAddressRestriction newCommerceAddressRestriction =
			addCommerceAddressRestriction();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceAddressRestriction.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceAddressRestriction commerceAddressRestriction) {

		Assert.assertEquals(
			Long.valueOf(commerceAddressRestriction.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceAddressRestriction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceAddressRestriction.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceAddressRestriction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(commerceAddressRestriction.getCountryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceAddressRestriction, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "countryId"));
	}

	protected CommerceAddressRestriction addCommerceAddressRestriction()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceAddressRestriction commerceAddressRestriction =
			_persistence.create(pk);

		commerceAddressRestriction.setMvccVersion(RandomTestUtil.nextLong());

		commerceAddressRestriction.setGroupId(RandomTestUtil.nextLong());

		commerceAddressRestriction.setCompanyId(RandomTestUtil.nextLong());

		commerceAddressRestriction.setUserId(RandomTestUtil.nextLong());

		commerceAddressRestriction.setUserName(RandomTestUtil.randomString());

		commerceAddressRestriction.setCreateDate(RandomTestUtil.nextDate());

		commerceAddressRestriction.setModifiedDate(RandomTestUtil.nextDate());

		commerceAddressRestriction.setClassNameId(RandomTestUtil.nextLong());

		commerceAddressRestriction.setClassPK(RandomTestUtil.nextLong());

		commerceAddressRestriction.setCountryId(RandomTestUtil.nextLong());

		_commerceAddressRestrictions.add(
			_persistence.update(commerceAddressRestriction));

		return commerceAddressRestriction;
	}

	private List<CommerceAddressRestriction> _commerceAddressRestrictions =
		new ArrayList<CommerceAddressRestriction>();
	private CommerceAddressRestrictionPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}