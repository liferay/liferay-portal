/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.payment.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.payment.exception.NoSuchPaymentMethodGroupRelException;
import com.liferay.commerce.payment.model.CommercePaymentMethodGroupRel;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelPersistence;
import com.liferay.commerce.payment.service.persistence.CommercePaymentMethodGroupRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.security.permission.InlineSQLHelperUtil;
import com.liferay.portal.kernel.security.permission.PermissionThreadLocal;
import com.liferay.portal.kernel.test.AssertUtils;
import com.liferay.portal.kernel.test.ReflectionTestUtil;
import com.liferay.portal.kernel.test.rule.AggregateTestRule;
import com.liferay.portal.kernel.test.util.RandomTestUtil;
import com.liferay.portal.kernel.test.util.TestPropsValues;
import com.liferay.portal.kernel.transaction.Propagation;
import com.liferay.portal.kernel.util.OrderByComparator;
import com.liferay.portal.kernel.util.OrderByComparatorFactoryUtil;
import com.liferay.portal.kernel.util.Time;
import com.liferay.portal.security.permission.SimplePermissionChecker;
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
public class CommercePaymentMethodGroupRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED, "com.liferay.commerce.payment.service"));

	@Before
	public void setUp() {
		_persistence = CommercePaymentMethodGroupRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommercePaymentMethodGroupRel> iterator =
			_commercePaymentMethodGroupRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_persistence.create(pk);

		Assert.assertNotNull(commercePaymentMethodGroupRel);

		Assert.assertEquals(commercePaymentMethodGroupRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel();

		_persistence.remove(newCommercePaymentMethodGroupRel);

		CommercePaymentMethodGroupRel existingCommercePaymentMethodGroupRel =
			_persistence.fetchByPrimaryKey(
				newCommercePaymentMethodGroupRel.getPrimaryKey());

		Assert.assertNull(existingCommercePaymentMethodGroupRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommercePaymentMethodGroupRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			_persistence.create(pk);

		newCommercePaymentMethodGroupRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRel.setGroupId(RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRel.setUserId(RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRel.setUserName(
			RandomTestUtil.randomString());

		newCommercePaymentMethodGroupRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommercePaymentMethodGroupRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommercePaymentMethodGroupRel.setName(RandomTestUtil.randomString());

		newCommercePaymentMethodGroupRel.setDescription(
			RandomTestUtil.randomString());

		newCommercePaymentMethodGroupRel.setActive(
			RandomTestUtil.randomBoolean());

		newCommercePaymentMethodGroupRel.setImageId(RandomTestUtil.nextLong());

		newCommercePaymentMethodGroupRel.setPaymentIntegrationKey(
			RandomTestUtil.randomString());

		newCommercePaymentMethodGroupRel.setPriority(
			RandomTestUtil.nextDouble());

		newCommercePaymentMethodGroupRel.setTypeSettings(
			RandomTestUtil.randomString());

		_commercePaymentMethodGroupRels.add(
			_persistence.update(newCommercePaymentMethodGroupRel));

		CommercePaymentMethodGroupRel existingCommercePaymentMethodGroupRel =
			_persistence.findByPrimaryKey(
				newCommercePaymentMethodGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getMvccVersion(),
			newCommercePaymentMethodGroupRel.getMvccVersion());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.
				getCommercePaymentMethodGroupRelId(),
			newCommercePaymentMethodGroupRel.
				getCommercePaymentMethodGroupRelId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getGroupId(),
			newCommercePaymentMethodGroupRel.getGroupId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getCompanyId(),
			newCommercePaymentMethodGroupRel.getCompanyId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getUserId(),
			newCommercePaymentMethodGroupRel.getUserId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getUserName(),
			newCommercePaymentMethodGroupRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentMethodGroupRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommercePaymentMethodGroupRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommercePaymentMethodGroupRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommercePaymentMethodGroupRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getName(),
			newCommercePaymentMethodGroupRel.getName());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getDescription(),
			newCommercePaymentMethodGroupRel.getDescription());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.isActive(),
			newCommercePaymentMethodGroupRel.isActive());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getImageId(),
			newCommercePaymentMethodGroupRel.getImageId());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getPaymentIntegrationKey(),
			newCommercePaymentMethodGroupRel.getPaymentIntegrationKey());
		AssertUtils.assertEquals(
			existingCommercePaymentMethodGroupRel.getPriority(),
			newCommercePaymentMethodGroupRel.getPriority());
		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel.getTypeSettings(),
			newCommercePaymentMethodGroupRel.getTypeSettings());
	}

	@Test
	public void testCountByGroupId() throws Exception {
		_persistence.countByGroupId(RandomTestUtil.nextLong());

		_persistence.countByGroupId(0L);
	}

	@Test
	public void testCountByG_A() throws Exception {
		_persistence.countByG_A(
			RandomTestUtil.nextLong(), RandomTestUtil.randomBoolean());

		_persistence.countByG_A(0L, RandomTestUtil.randomBoolean());
	}

	@Test
	public void testCountByG_P() throws Exception {
		_persistence.countByG_P(RandomTestUtil.nextLong(), "");

		_persistence.countByG_P(0L, "null");

		_persistence.countByG_P(0L, (String)null);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel();

		CommercePaymentMethodGroupRel existingCommercePaymentMethodGroupRel =
			_persistence.findByPrimaryKey(
				newCommercePaymentMethodGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel,
			newCommercePaymentMethodGroupRel);
	}

	@Test(expected = NoSuchPaymentMethodGroupRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	@Test
	public void testFilterFindByGroupId() throws Exception {
		PermissionThreadLocal.setPermissionChecker(
			new SimplePermissionChecker() {
				{
					init(TestPropsValues.getUser());
				}

				@Override
				public boolean isCompanyAdmin(long companyId) {
					return false;
				}

			});

		Assert.assertTrue(InlineSQLHelperUtil.isEnabled(0));

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, null);

		_persistence.filterFindByGroupId(
			0, QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommercePaymentMethodGroupRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommercePaymentMethodGroupRel", "mvccVersion", true,
			"commercePaymentMethodGroupRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "name", true, "description", true,
			"active", true, "imageId", true, "paymentIntegrationKey", true,
			"priority", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel();

		CommercePaymentMethodGroupRel existingCommercePaymentMethodGroupRel =
			_persistence.fetchByPrimaryKey(
				newCommercePaymentMethodGroupRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommercePaymentMethodGroupRel,
			newCommercePaymentMethodGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRel missingCommercePaymentMethodGroupRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommercePaymentMethodGroupRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel1 =
			addCommercePaymentMethodGroupRel();
		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel2 =
			addCommercePaymentMethodGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentMethodGroupRel1.getPrimaryKey());
		primaryKeys.add(newCommercePaymentMethodGroupRel2.getPrimaryKey());

		Map<Serializable, CommercePaymentMethodGroupRel>
			commercePaymentMethodGroupRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commercePaymentMethodGroupRels.size());
		Assert.assertEquals(
			newCommercePaymentMethodGroupRel1,
			commercePaymentMethodGroupRels.get(
				newCommercePaymentMethodGroupRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommercePaymentMethodGroupRel2,
			commercePaymentMethodGroupRels.get(
				newCommercePaymentMethodGroupRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommercePaymentMethodGroupRel>
			commercePaymentMethodGroupRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePaymentMethodGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentMethodGroupRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommercePaymentMethodGroupRel>
			commercePaymentMethodGroupRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePaymentMethodGroupRels.size());
		Assert.assertEquals(
			newCommercePaymentMethodGroupRel,
			commercePaymentMethodGroupRels.get(
				newCommercePaymentMethodGroupRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommercePaymentMethodGroupRel>
			commercePaymentMethodGroupRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commercePaymentMethodGroupRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommercePaymentMethodGroupRel.getPrimaryKey());

		Map<Serializable, CommercePaymentMethodGroupRel>
			commercePaymentMethodGroupRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commercePaymentMethodGroupRels.size());
		Assert.assertEquals(
			newCommercePaymentMethodGroupRel,
			commercePaymentMethodGroupRels.get(
				newCommercePaymentMethodGroupRel.getPrimaryKey()));
	}

	@Test
	public void testResetOriginalValues() throws Exception {
		CommercePaymentMethodGroupRel newCommercePaymentMethodGroupRel =
			addCommercePaymentMethodGroupRel();

		_persistence.clearCache();

		_assertOriginalValues(
			_persistence.findByPrimaryKey(
				newCommercePaymentMethodGroupRel.getPrimaryKey()));
	}

	private void _assertOriginalValues(
		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel) {

		Assert.assertEquals(
			Long.valueOf(commercePaymentMethodGroupRel.getGroupId()),
			ReflectionTestUtil.<Long>invoke(
				commercePaymentMethodGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "groupId"));
		Assert.assertEquals(
			commercePaymentMethodGroupRel.getPaymentIntegrationKey(),
			ReflectionTestUtil.invoke(
				commercePaymentMethodGroupRel, "getColumnOriginalValue",
				new Class<?>[] {String.class}, "paymentIntegrationKey"));
	}

	protected CommercePaymentMethodGroupRel addCommercePaymentMethodGroupRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommercePaymentMethodGroupRel commercePaymentMethodGroupRel =
			_persistence.create(pk);

		commercePaymentMethodGroupRel.setMvccVersion(RandomTestUtil.nextLong());

		commercePaymentMethodGroupRel.setGroupId(RandomTestUtil.nextLong());

		commercePaymentMethodGroupRel.setCompanyId(RandomTestUtil.nextLong());

		commercePaymentMethodGroupRel.setUserId(RandomTestUtil.nextLong());

		commercePaymentMethodGroupRel.setUserName(
			RandomTestUtil.randomString());

		commercePaymentMethodGroupRel.setCreateDate(RandomTestUtil.nextDate());

		commercePaymentMethodGroupRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commercePaymentMethodGroupRel.setName(RandomTestUtil.randomString());

		commercePaymentMethodGroupRel.setDescription(
			RandomTestUtil.randomString());

		commercePaymentMethodGroupRel.setActive(RandomTestUtil.randomBoolean());

		commercePaymentMethodGroupRel.setImageId(RandomTestUtil.nextLong());

		commercePaymentMethodGroupRel.setPaymentIntegrationKey(
			RandomTestUtil.randomString());

		commercePaymentMethodGroupRel.setPriority(RandomTestUtil.nextDouble());

		commercePaymentMethodGroupRel.setTypeSettings(
			RandomTestUtil.randomString());

		_commercePaymentMethodGroupRels.add(
			_persistence.update(commercePaymentMethodGroupRel));

		return commercePaymentMethodGroupRel;
	}

	private List<CommercePaymentMethodGroupRel>
		_commercePaymentMethodGroupRels =
			new ArrayList<CommercePaymentMethodGroupRel>();
	private CommercePaymentMethodGroupRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}