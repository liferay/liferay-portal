/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.product.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.product.exception.NoSuchChannelRelException;
import com.liferay.commerce.product.model.CommerceChannelRel;
import com.liferay.commerce.product.service.persistence.CommerceChannelRelPersistence;
import com.liferay.commerce.product.service.persistence.CommerceChannelRelUtil;
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
public class CommerceChannelRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.product.service"));

	@Before
	public void setUp() {
		_persistence = CommerceChannelRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceChannelRel> iterator = _commerceChannelRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelRel commerceChannelRel = _persistence.create(pk);

		Assert.assertNotNull(commerceChannelRel);

		Assert.assertEquals(commerceChannelRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceChannelRel newCommerceChannelRel = addCommerceChannelRel();

		_persistence.remove(newCommerceChannelRel);

		CommerceChannelRel existingCommerceChannelRel =
			_persistence.fetchByPrimaryKey(
				newCommerceChannelRel.getPrimaryKey());

		Assert.assertNull(existingCommerceChannelRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceChannelRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelRel newCommerceChannelRel = _persistence.create(pk);

		newCommerceChannelRel.setMvccVersion(RandomTestUtil.nextLong());

		newCommerceChannelRel.setCtCollectionId(RandomTestUtil.nextLong());

		newCommerceChannelRel.setCompanyId(RandomTestUtil.nextLong());

		newCommerceChannelRel.setUserId(RandomTestUtil.nextLong());

		newCommerceChannelRel.setUserName(RandomTestUtil.randomString());

		newCommerceChannelRel.setCreateDate(RandomTestUtil.nextDate());

		newCommerceChannelRel.setModifiedDate(RandomTestUtil.nextDate());

		newCommerceChannelRel.setClassNameId(RandomTestUtil.nextLong());

		newCommerceChannelRel.setClassPK(RandomTestUtil.nextLong());

		newCommerceChannelRel.setCommerceChannelId(RandomTestUtil.nextLong());

		_commerceChannelRels.add(_persistence.update(newCommerceChannelRel));

		CommerceChannelRel existingCommerceChannelRel =
			_persistence.findByPrimaryKey(
				newCommerceChannelRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceChannelRel.getMvccVersion(),
			newCommerceChannelRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceChannelRel.getCtCollectionId(),
			newCommerceChannelRel.getCtCollectionId());
		Assert.assertEquals(
			existingCommerceChannelRel.getCommerceChannelRelId(),
			newCommerceChannelRel.getCommerceChannelRelId());
		Assert.assertEquals(
			existingCommerceChannelRel.getCompanyId(),
			newCommerceChannelRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceChannelRel.getUserId(),
			newCommerceChannelRel.getUserId());
		Assert.assertEquals(
			existingCommerceChannelRel.getUserName(),
			newCommerceChannelRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(existingCommerceChannelRel.getCreateDate()),
			Time.getShortTimestamp(newCommerceChannelRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceChannelRel.getModifiedDate()),
			Time.getShortTimestamp(newCommerceChannelRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceChannelRel.getClassNameId(),
			newCommerceChannelRel.getClassNameId());
		Assert.assertEquals(
			existingCommerceChannelRel.getClassPK(),
			newCommerceChannelRel.getClassPK());
		Assert.assertEquals(
			existingCommerceChannelRel.getCommerceChannelId(),
			newCommerceChannelRel.getCommerceChannelId());
	}

	@Test
	public void testCountByCommerceChannelId() throws Exception {
		_persistence.countByCommerceChannelId(RandomTestUtil.nextLong());

		_persistence.countByCommerceChannelId(0L);
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
		CommerceChannelRel newCommerceChannelRel = addCommerceChannelRel();

		CommerceChannelRel existingCommerceChannelRel =
			_persistence.findByPrimaryKey(
				newCommerceChannelRel.getPrimaryKey());

		Assert.assertEquals(existingCommerceChannelRel, newCommerceChannelRel);
	}

	@Test(expected = NoSuchChannelRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceChannelRel> getOrderByComparator() {
		return OrderByComparatorFactoryUtil.create(
			"CommerceChannelRel", "mvccVersion", true, "ctCollectionId", true,
			"commerceChannelRelId", true, "companyId", true, "userId", true,
			"userName", true, "createDate", true, "modifiedDate", true,
			"classNameId", true, "classPK", true, "commerceChannelId", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceChannelRel newCommerceChannelRel = addCommerceChannelRel();

		CommerceChannelRel existingCommerceChannelRel =
			_persistence.fetchByPrimaryKey(
				newCommerceChannelRel.getPrimaryKey());

		Assert.assertEquals(existingCommerceChannelRel, newCommerceChannelRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelRel missingCommerceChannelRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceChannelRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceChannelRel newCommerceChannelRel1 = addCommerceChannelRel();
		CommerceChannelRel newCommerceChannelRel2 = addCommerceChannelRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannelRel1.getPrimaryKey());
		primaryKeys.add(newCommerceChannelRel2.getPrimaryKey());

		Map<Serializable, CommerceChannelRel> commerceChannelRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(2, commerceChannelRels.size());
		Assert.assertEquals(
			newCommerceChannelRel1,
			commerceChannelRels.get(newCommerceChannelRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceChannelRel2,
			commerceChannelRels.get(newCommerceChannelRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceChannelRel> commerceChannelRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceChannelRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceChannelRel newCommerceChannelRel = addCommerceChannelRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannelRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceChannelRel> commerceChannelRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceChannelRels.size());
		Assert.assertEquals(
			newCommerceChannelRel,
			commerceChannelRels.get(newCommerceChannelRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceChannelRel> commerceChannelRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertTrue(commerceChannelRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceChannelRel newCommerceChannelRel = addCommerceChannelRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceChannelRel.getPrimaryKey());

		Map<Serializable, CommerceChannelRel> commerceChannelRels =
			_persistence.fetchByPrimaryKeys(primaryKeys);

		Assert.assertEquals(1, commerceChannelRels.size());
		Assert.assertEquals(
			newCommerceChannelRel,
			commerceChannelRels.get(newCommerceChannelRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommerceChannelRel newCommerceChannelRel = addCommerceChannelRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommerceChannelRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(CommerceChannelRel commerceChannelRel) {
		Assert.assertEquals(
			Long.valueOf(commerceChannelRel.getClassNameId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classNameId"));
		Assert.assertEquals(
			Long.valueOf(commerceChannelRel.getClassPK()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "classPK"));
		Assert.assertEquals(
			Long.valueOf(commerceChannelRel.getCommerceChannelId()),
			ReflectionTestUtil.<Long>invoke(
				commerceChannelRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "commerceChannelId"));
	}

	protected CommerceChannelRel addCommerceChannelRel() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceChannelRel commerceChannelRel = _persistence.create(pk);

		commerceChannelRel.setMvccVersion(RandomTestUtil.nextLong());

		commerceChannelRel.setCtCollectionId(RandomTestUtil.nextLong());

		commerceChannelRel.setCompanyId(RandomTestUtil.nextLong());

		commerceChannelRel.setUserId(RandomTestUtil.nextLong());

		commerceChannelRel.setUserName(RandomTestUtil.randomString());

		commerceChannelRel.setCreateDate(RandomTestUtil.nextDate());

		commerceChannelRel.setModifiedDate(RandomTestUtil.nextDate());

		commerceChannelRel.setClassNameId(RandomTestUtil.nextLong());

		commerceChannelRel.setClassPK(RandomTestUtil.nextLong());

		commerceChannelRel.setCommerceChannelId(RandomTestUtil.nextLong());

		_commerceChannelRels.add(_persistence.update(commerceChannelRel));

		return commerceChannelRel;
	}

	private List<CommerceChannelRel> _commerceChannelRels =
		new ArrayList<CommerceChannelRel>();
	private CommerceChannelRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}