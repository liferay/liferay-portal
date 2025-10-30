/**
 * SPDX-FileCopyrightText: (c) 2000 Liferay, Inc. https://liferay.com
 * SPDX-License-Identifier: LGPL-2.1-or-later OR LicenseRef-Liferay-DXP-EULA-2.0.0-2023-06
 */

package com.liferay.commerce.tax.engine.fixed.service.persistence.test;

import com.liferay.arquillian.extension.junit.bridge.junit.Arquillian;
import com.liferay.commerce.tax.engine.fixed.exception.NoSuchTaxFixedRateAddressRelException;
import com.liferay.commerce.tax.engine.fixed.model.CommerceTaxFixedRateAddressRel;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRateAddressRelPersistence;
import com.liferay.commerce.tax.engine.fixed.service.persistence.CommerceTaxFixedRateAddressRelUtil;
import com.liferay.portal.kernel.dao.orm.QueryUtil;
import com.liferay.portal.kernel.test.AssertUtils;
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
public class CommerceTaxFixedRateAddressRelPersistenceTest {

	@ClassRule
	@Rule
	public static final AggregateTestRule aggregateTestRule =
		new AggregateTestRule(
			new LiferayIntegrationTestRule(), PersistenceTestRule.INSTANCE,
			new TransactionalTestRule(
				Propagation.REQUIRED,
				"com.liferay.commerce.tax.engine.fixed.service"));

	@Before
	public void setUp() {
		_persistence = CommerceTaxFixedRateAddressRelUtil.getPersistence();

		Class<?> clazz = _persistence.getClass();

		_dynamicQueryClassLoader = clazz.getClassLoader();
	}

	@After
	public void tearDown() throws Exception {
		Iterator<CommerceTaxFixedRateAddressRel> iterator =
			_commerceTaxFixedRateAddressRels.iterator();

		while (iterator.hasNext()) {
			_persistence.remove(iterator.next());

			iterator.remove();
		}
	}

	@Test
	public void testCreate() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_persistence.create(pk);

		Assert.assertNotNull(commerceTaxFixedRateAddressRel);

		Assert.assertEquals(commerceTaxFixedRateAddressRel.getPrimaryKey(), pk);
	}

	@Test
	public void testRemove() throws Exception {
		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel =
			addCommerceTaxFixedRateAddressRel();

		_persistence.remove(newCommerceTaxFixedRateAddressRel);

		CommerceTaxFixedRateAddressRel existingCommerceTaxFixedRateAddressRel =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxFixedRateAddressRel.getPrimaryKey());

		Assert.assertNull(existingCommerceTaxFixedRateAddressRel);
	}

	@Test
	public void testUpdateNew() throws Exception {
		addCommerceTaxFixedRateAddressRel();
	}

	@Test
	public void testUpdateExisting() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel =
			_persistence.create(pk);

		newCommerceTaxFixedRateAddressRel.setMvccVersion(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setGroupId(RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setCompanyId(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setUserId(RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setUserName(
			RandomTestUtil.randomString());

		newCommerceTaxFixedRateAddressRel.setCreateDate(
			RandomTestUtil.nextDate());

		newCommerceTaxFixedRateAddressRel.setModifiedDate(
			RandomTestUtil.nextDate());

		newCommerceTaxFixedRateAddressRel.setCommerceTaxMethodId(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setCPTaxCategoryId(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setCountryId(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setRegionId(
			RandomTestUtil.nextLong());

		newCommerceTaxFixedRateAddressRel.setZip(RandomTestUtil.randomString());

		newCommerceTaxFixedRateAddressRel.setRate(RandomTestUtil.nextDouble());

		_commerceTaxFixedRateAddressRels.add(
			_persistence.update(newCommerceTaxFixedRateAddressRel));

		CommerceTaxFixedRateAddressRel existingCommerceTaxFixedRateAddressRel =
			_persistence.findByPrimaryKey(
				newCommerceTaxFixedRateAddressRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getMvccVersion(),
			newCommerceTaxFixedRateAddressRel.getMvccVersion());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.
				getCommerceTaxFixedRateAddressRelId(),
			newCommerceTaxFixedRateAddressRel.
				getCommerceTaxFixedRateAddressRelId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getGroupId(),
			newCommerceTaxFixedRateAddressRel.getGroupId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getCompanyId(),
			newCommerceTaxFixedRateAddressRel.getCompanyId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getUserId(),
			newCommerceTaxFixedRateAddressRel.getUserId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getUserName(),
			newCommerceTaxFixedRateAddressRel.getUserName());
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTaxFixedRateAddressRel.getCreateDate()),
			Time.getShortTimestamp(
				newCommerceTaxFixedRateAddressRel.getCreateDate()));
		Assert.assertEquals(
			Time.getShortTimestamp(
				existingCommerceTaxFixedRateAddressRel.getModifiedDate()),
			Time.getShortTimestamp(
				newCommerceTaxFixedRateAddressRel.getModifiedDate()));
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getCommerceTaxMethodId(),
			newCommerceTaxFixedRateAddressRel.getCommerceTaxMethodId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getCPTaxCategoryId(),
			newCommerceTaxFixedRateAddressRel.getCPTaxCategoryId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getCountryId(),
			newCommerceTaxFixedRateAddressRel.getCountryId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getRegionId(),
			newCommerceTaxFixedRateAddressRel.getRegionId());
		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getZip(),
			newCommerceTaxFixedRateAddressRel.getZip());
		AssertUtils.assertEquals(
			existingCommerceTaxFixedRateAddressRel.getRate(),
			newCommerceTaxFixedRateAddressRel.getRate());
	}

	@Test
	public void testCountByCommerceTaxMethodId() throws Exception {
		_persistence.countByCommerceTaxMethodId(RandomTestUtil.nextLong());

		_persistence.countByCommerceTaxMethodId(0L);
	}

	@Test
	public void testCountByCPTaxCategoryId() throws Exception {
		_persistence.countByCPTaxCategoryId(RandomTestUtil.nextLong());

		_persistence.countByCPTaxCategoryId(0L);
	}

	@Test
	public void testCountByCountryId() throws Exception {
		_persistence.countByCountryId(RandomTestUtil.nextLong());

		_persistence.countByCountryId(0L);
	}

	@Test
	public void testFindByPrimaryKeyExisting() throws Exception {
		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel =
			addCommerceTaxFixedRateAddressRel();

		CommerceTaxFixedRateAddressRel existingCommerceTaxFixedRateAddressRel =
			_persistence.findByPrimaryKey(
				newCommerceTaxFixedRateAddressRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel,
			newCommerceTaxFixedRateAddressRel);
	}

	@Test(expected = NoSuchTaxFixedRateAddressRelException.class)
	public void testFindByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		_persistence.findByPrimaryKey(pk);
	}

	@Test
	public void testFindAll() throws Exception {
		_persistence.findAll(
			QueryUtil.ALL_POS, QueryUtil.ALL_POS, getOrderByComparator());
	}

	protected OrderByComparator<CommerceTaxFixedRateAddressRel>
		getOrderByComparator() {

		return OrderByComparatorFactoryUtil.create(
			"CommerceTaxFixedRateAddressRel", "mvccVersion", true,
			"commerceTaxFixedRateAddressRelId", true, "groupId", true,
			"companyId", true, "userId", true, "userName", true, "createDate",
			true, "modifiedDate", true, "commerceTaxMethodId", true,
			"CPTaxCategoryId", true, "countryId", true, "regionId", true, "zip",
			true, "rate", true);
	}

	@Test
	public void testFetchByPrimaryKeyExisting() throws Exception {
		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel =
			addCommerceTaxFixedRateAddressRel();

		CommerceTaxFixedRateAddressRel existingCommerceTaxFixedRateAddressRel =
			_persistence.fetchByPrimaryKey(
				newCommerceTaxFixedRateAddressRel.getPrimaryKey());

		Assert.assertEquals(
			existingCommerceTaxFixedRateAddressRel,
			newCommerceTaxFixedRateAddressRel);
	}

	@Test
	public void testFetchByPrimaryKeyMissing() throws Exception {
		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRateAddressRel missingCommerceTaxFixedRateAddressRel =
			_persistence.fetchByPrimaryKey(pk);

		Assert.assertNull(missingCommerceTaxFixedRateAddressRel);
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereAllPrimaryKeysExist()
		throws Exception {

		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel1 =
			addCommerceTaxFixedRateAddressRel();
		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel2 =
			addCommerceTaxFixedRateAddressRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxFixedRateAddressRel1.getPrimaryKey());
		primaryKeys.add(newCommerceTaxFixedRateAddressRel2.getPrimaryKey());

		Map<Serializable, CommerceTaxFixedRateAddressRel>
			commerceTaxFixedRateAddressRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(2, commerceTaxFixedRateAddressRels.size());
		Assert.assertEquals(
			newCommerceTaxFixedRateAddressRel1,
			commerceTaxFixedRateAddressRels.get(
				newCommerceTaxFixedRateAddressRel1.getPrimaryKey()));
		Assert.assertEquals(
			newCommerceTaxFixedRateAddressRel2,
			commerceTaxFixedRateAddressRels.get(
				newCommerceTaxFixedRateAddressRel2.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereNoPrimaryKeysExist()
		throws Exception {

		long pk1 = RandomTestUtil.nextLong();

		long pk2 = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(pk1);
		primaryKeys.add(pk2);

		Map<Serializable, CommerceTaxFixedRateAddressRel>
			commerceTaxFixedRateAddressRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceTaxFixedRateAddressRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithMultiplePrimaryKeysWhereSomePrimaryKeysExist()
		throws Exception {

		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel =
			addCommerceTaxFixedRateAddressRel();

		long pk = RandomTestUtil.nextLong();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxFixedRateAddressRel.getPrimaryKey());
		primaryKeys.add(pk);

		Map<Serializable, CommerceTaxFixedRateAddressRel>
			commerceTaxFixedRateAddressRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceTaxFixedRateAddressRels.size());
		Assert.assertEquals(
			newCommerceTaxFixedRateAddressRel,
			commerceTaxFixedRateAddressRels.get(
				newCommerceTaxFixedRateAddressRel.getPrimaryKey()));
	}

	@Test
	public void testFetchByPrimaryKeysWithNoPrimaryKeys() throws Exception {
		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		Map<Serializable, CommerceTaxFixedRateAddressRel>
			commerceTaxFixedRateAddressRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertTrue(commerceTaxFixedRateAddressRels.isEmpty());
	}

	@Test
	public void testFetchByPrimaryKeysWithOnePrimaryKey() throws Exception {
		CommerceTaxFixedRateAddressRel newCommerceTaxFixedRateAddressRel =
			addCommerceTaxFixedRateAddressRel();

		Set<Serializable> primaryKeys = new HashSet<Serializable>();

		primaryKeys.add(newCommerceTaxFixedRateAddressRel.getPrimaryKey());

		Map<Serializable, CommerceTaxFixedRateAddressRel>
			commerceTaxFixedRateAddressRels = _persistence.fetchByPrimaryKeys(
				primaryKeys);

		Assert.assertEquals(1, commerceTaxFixedRateAddressRels.size());
		Assert.assertEquals(
			newCommerceTaxFixedRateAddressRel,
			commerceTaxFixedRateAddressRels.get(
				newCommerceTaxFixedRateAddressRel.getPrimaryKey()));
	}

	protected CommerceTaxFixedRateAddressRel addCommerceTaxFixedRateAddressRel()
		throws Exception {

		long pk = RandomTestUtil.nextLong();

		CommerceTaxFixedRateAddressRel commerceTaxFixedRateAddressRel =
			_persistence.create(pk);

		commerceTaxFixedRateAddressRel.setMvccVersion(
			RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setGroupId(RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setCompanyId(RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setUserId(RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setUserName(
			RandomTestUtil.randomString());

		commerceTaxFixedRateAddressRel.setCreateDate(RandomTestUtil.nextDate());

		commerceTaxFixedRateAddressRel.setModifiedDate(
			RandomTestUtil.nextDate());

		commerceTaxFixedRateAddressRel.setCommerceTaxMethodId(
			RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setCPTaxCategoryId(
			RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setCountryId(RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setRegionId(RandomTestUtil.nextLong());

		commerceTaxFixedRateAddressRel.setZip(RandomTestUtil.randomString());

		commerceTaxFixedRateAddressRel.setRate(RandomTestUtil.nextDouble());

		_commerceTaxFixedRateAddressRels.add(
			_persistence.update(commerceTaxFixedRateAddressRel));

		return commerceTaxFixedRateAddressRel;
	}

	private List<CommerceTaxFixedRateAddressRel>
		_commerceTaxFixedRateAddressRels =
			new ArrayList<CommerceTaxFixedRateAddressRel>();
	private CommerceTaxFixedRateAddressRelPersistence _persistence;
	private ClassLoader _dynamicQueryClassLoader;

}