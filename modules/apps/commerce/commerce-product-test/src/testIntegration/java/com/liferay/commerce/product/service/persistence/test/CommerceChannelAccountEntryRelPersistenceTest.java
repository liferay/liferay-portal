/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchChannelAccountEntryRelException;
import com.liferay.commerce.product.model.CommerceChannelAccountEntryRel;
import com.liferay.commerce.product.service.persistence.CommerceChannelAccountEntryRelPersistence;
import com.liferay.commerce.product.service.persistence.CommerceChannelAccountEntryRelUtil;
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
public class CommerceChannelAccountEntryRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CommerceChannelAccountEntryRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceChannelAccountEntryRel> iterator =
			_commerceChannelAccountEntryRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_persistence.create(pk);

		Assert.assertNotNull(commerceChannelAccountEntryRel);

		Assert.assertEquals(commerceChannelAccountEntryRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			addCommerceChannelAccountEntryRel();

		_persistence.remove(newCommerceChannelAccountEntryRel);

		CommerceChannelAccountEntryRel existingCommerceChannelAccountEntryRel =
			_persistence.fetchByPrimaryKey(
				newCommerceChannelAccountEntryRel.getPrimaryKey());

		Assert.assertNull(existingCommerceChannelAccountEntryRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceChannelAccountEntryRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			_persistence.create(pk);

		newCommerceChannelAccountEntryRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setUserId(RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setUserName(
			RandomTestUtil.randomString());

		newCommerceChannelAccountEntryRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceChannelAccountEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceChannelAccountEntryRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setClassNameId(
			RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setClassPK(RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setCommerceChannelId(
			RandomTestUtil.nextLong());

		newCommerceChannelAccountEntryRel.setOverrideEligibility(
			RandomTestUtil.randomBoolean());

		newCommerceChannelAccountEntryRel.setPriority(
			RandomTestUtil.nextDouble());

		newCommerceChannelAccountEntryRel.setType(RandomTestUtil.nextInt());

		_commerceChannelAccountEntryRels.add(
			_persistence.update(newCommerceChannelAccountEntryRel));

		CommerceChannelAccountEntryRel existingCommerceChannelAccountEntryRel =
			_persistence.findByPrimaryKey(
				newCommerceChannelAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getMvccVersion(),
			newCommerceChannelAccountEntryRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getCtCollectionId(),
			newCommerceChannelAccountEntryRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.
				getCommerceChannelAccountEntryRelId(),
			newCommerceChannelAccountEntryRel.
				getCommerceChannelAccountEntryRelId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getCompanyId(),
			newCommerceChannelAccountEntryRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getUserId(),
			newCommerceChannelAccountEntryRel.getUserId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getUserName(),
			newCommerceChannelAccountEntryRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceChannelAccountEntryRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceChannelAccountEntryRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceChannelAccountEntryRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceChannelAccountEntryRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getAccountEntryId(),
			newCommerceChannelAccountEntryRel.getAccountEntryId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getClassNameId(),
			newCommerceChannelAccountEntryRel.getClassNameId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getClassPK(),
			newCommerceChannelAccountEntryRel.getClassPK());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getCommerceChannelId(),
			newCommerceChannelAccountEntryRel.getCommerceChannelId());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.isOverrideEligibility(),
			newCommerceChannelAccountEntryRel.isOverrideEligibility());
		AssertUtils.assertEquals(
			existingCommerceChannelAccountEntryRel.getPriority(),
			newCommerceChannelAccountEntryRel.getPriority());
		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel.getType(),
			newCommerceChannelAccountEntryRel.getType());
	}

	@Test
	public void testCountByAccountEntryId() throws Exception {
		_persistence.countByAccountEntryId(RandomTestUtil.nextLong());

		_persistence.countByAccountEntryId(0L);
	}

	@Test
	public void testCountByCommerceChannelId() throws Exception {
		_persistence.countByCommerceChannelId(RandomTestUtil.nextLong());

		_persistence.countByCommerceChannelId(0L);
	}

	@Test
	public void testCountByA_T() throws Exception {
		_persistence.countByA_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByA_T(0L, 0);
	}

	@Test
	public void testCountByC_C() throws Exception {
		_persistence.countByC_C(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong());

		_persistence.countByC_C(0L, 0L);
	}

	@Test
	public void testCountByC_T() throws Exception {
		_persistence.countByC_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_T(0L, 0);
	}

	@Test
	public void testCountByA_C_T() throws Exception {
		_persistence.countByA_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByA_C_T(0L, 0L, 0);
	}

	@Test
	public void testCountByC_C_C_T() throws Exception {
		_persistence.countByC_C_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextInt());

		_persistence.countByC_C_C_T(0L, 0L, 0L, 0);
	}

	@Test
	public void testCountByA_C_C_C_T() throws Exception {
		_persistence.countByA_C_C_C_T(
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextLong(), RandomTestUtil.nextLong(),
			RandomTestUtil.nextInt());

		_persistence.countByA_C_C_C_T(0L, 0L, 0L, 0L, 0);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			addCommerceChannelAccountEntryRel();

		CommerceChannelAccountEntryRel existingCommerceChannelAccountEntryRel =
			_persistence.findByPrimaryKey(
				newCommerceChannelAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel,
			newCommerceChannelAccountEntryRel);
	}

	@Test(expected = NoSuchChannelAccountEntryRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceChannelAccountEntryRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CChannelAccountEntryRel", "mvccVersion", true, "ctCollectionId",
			true, "commerceChannelAccountEntryRelId", true, "companyId", true,
			"userId", true, "userName", true, "createDate", true,
			"modifiedDate", true, "accountEntryId", true, "classNameId", true,
			"classPK", true, "commerceChannelId", true, "overrideEligibility",
			true, "priority", true, "type", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			addCommerceChannelAccountEntryRel();

		CommerceChannelAccountEntryRel existingCommerceChannelAccountEntryRel =
			_persistence.fetchByPrimaryKey(
				newCommerceChannelAccountEntryRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceChannelAccountEntryRel,
			newCommerceChannelAccountEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelAccountEntryRel missingCommerceChannelAccountEntryRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceChannelAccountEntryRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel1 =
			addCommerceChannelAccountEntryRel();
		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel2 =
			addCommerceChannelAccountEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannelAccountEntryRel1.getPrimaryKey());
		primaryKeys.add(newCommerceChannelAccountEntryRel2.getPrimaryKey());

		Map<Serializable, CommerceChannelAccountEntryRel>
			commerceChannelAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceChannelAccountEntryRels.size());
		Assert.assertEquals(
			newCommerceChannelAccountEntryRel1,
			commerceChannelAccountEntryRels.get(
				newCommerceChannelAccountEntryRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceChannelAccountEntryRel2,
			commerceChannelAccountEntryRels.get(
				newCommerceChannelAccountEntryRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceChannelAccountEntryRel>
			commerceChannelAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceChannelAccountEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			addCommerceChannelAccountEntryRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannelAccountEntryRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceChannelAccountEntryRel>
			commerceChannelAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceChannelAccountEntryRels.size());
		Assert.assertEquals(
			newCommerceChannelAccountEntryRel,
			commerceChannelAccountEntryRels.get(
				newCommerceChannelAccountEntryRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceChannelAccountEntryRel>
			commerceChannelAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceChannelAccountEntryRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			addCommerceChannelAccountEntryRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannelAccountEntryRel.getPrimaryKey());

		Map<Serializable, CommerceChannelAccountEntryRel>
			commerceChannelAccountEntryRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceChannelAccountEntryRels.size());
		Assert.assertEquals(
			newCommerceChannelAccountEntryRel,
			commerceChannelAccountEntryRels.get(
				newCommerceChannelAccountEntryRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceChannelAccountEntryRel newCommerceChannelAccountEntryRel =
			addCommerceChannelAccountEntryRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceChannelAccountEntryRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel) {

		Assert.assertEquals(
			Long.valueOf(commerceChannelAccountEntryRel.getAccountEntryId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "accountEntryId"));
		Assert.assertEquals(
			Long.valueOf(commerceChannelAccountEntryRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceChannelAccountEntryRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(commerceChannelAccountEntryRel.getCommerceChannelId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceChannelId"));
		Assert.assertEquals(
			Integer.valueOf(commerceChannelAccountEntryRel.getType()),
			ReflectionTestUtil.<Integer>invoke(
				commerceChannelAccountEntryRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "type_"));
	}

	protected CommerceChannelAccountEntryRel addCommerceChannelAccountEntryRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceChannelAccountEntryRel commerceChannelAccountEntryRel =
			_persistence.create(pk);

		commerceChannelAccountEntryRel.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setCtCollectionId(
			RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setCompanyId(RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setUserId(RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setUserName(
			RandomTestUtil.randomString());

		commerceChannelAccountEntryRel.setCreateDate(RandomTestUtil.nextDate());

		commerceChannelAccountEntryRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceChannelAccountEntryRel.setAccountEntryId(
			RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setClassNameId(
			RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setClassPK(RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setCommerceChannelId(
			RandomTestUtil.nextLong());

		commerceChannelAccountEntryRel.setOverrideEligibility(
			RandomTestUtil.randomBoolean());

		commerceChannelAccountEntryRel.setPriority(RandomTestUtil.nextDouble());

		commerceChannelAccountEntryRel.setType(RandomTestUtil.nextInt());

		_commerceChannelAccountEntryRels.add(
			_persistence.update(commerceChannelAccountEntryRel));

		return commerceChannelAccountEntryRel;
	}

	private List<CommerceChannelAccountEntryRel>
		_commerceChannelAccountEntryRels =
			new ArrayList<CommerceChannelAccountEntryRel>();
	private CommerceChannelAccountEntryRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}